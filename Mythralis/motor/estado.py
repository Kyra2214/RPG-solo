"""Estado mutável de Mythralis (F3). Nunca é a verdade: é o que sobra de aplicar a crônica ao molde.

Tipos mecânicos de delta:
  campo    -> muda um campo de uma entidade (estado_anterior precisa bater com o atual)
  criacao  -> cria entidade nova (sempre SIMULADO); entidade ainda não pode existir
  era      -> troca de era/regime (entidade 'mundo')
Qualquer outro tipo (incidente_fronteira, tratado, guerra, boato...) é registro: entra na crônica, não muda o estado.
"""
import json, re, itertools
from cronica import canon, sha
from carregador import toks

MECANICOS = ('campo', 'criacao', 'era')
ERA_INICIAL = ('era.001', 'Fronteiras Seladas', 'selado')
# anel de M-04 (G-02), sentido horário; vizinhos = pares consecutivos (ASSUMINDO: Pacto Humano = Azeroth, Marcas das Feras = Vale Palancar)
ANEL = ('kingdom.coroas-de-pedra', 'kingdom.aeltharyn', 'kingdom.azeroth', 'kingdom.veu-dos-malditos', 'kingdom.vale-palancar')


class ErroEstado(Exception):
    pass


def _bool(v):
    return str(v).strip().lower() == 'sim'


class Estado:
    def __init__(self):
        self.proximo_dia = 0
        self.ent = {}             # id -> {campo: valor}
        self.eras = {}            # id -> {nome, regime, dia_inicio}
        self.mundo = {'era': None, 'regime': None}
        self.ecos_ativos = set()  # derivado da crônica (aplicar); não é serializado
        self.n_criados = 0        # contador de entidades criadas pela simulação (ids estáveis)

    # ---- construção a partir do molde (somente leitura)
    @classmethod
    def do_molde(cls, world, din=None):
        """Estado inicial = molde (+ dinâmica, só para a idade dos NPCs vivos). Postos, pares de facções e eventos
        nascem aqui, derivados do molde; nada disso é editado no molde."""
        s = cls()
        L = (lambda k, d: din.lim.get(k, d)) if din else (lambda k, d: d)
        for r in world.t['npc']:
            e = dict(vivo=_bool(r['vivo']), reino=r['reino'], cidade=r['cidade'], faccao=r['faccao'],
                     cargo=r['cargo'], papeis=r['papeis'], principal=_bool(r['principal']),
                     povo=r['povo'], subraca=r['subraca'], nome=r['nome'], saude='sao')
            if din and not din.vazia:
                e['classe'] = din.classe_de(r['povo'], r['subraca'])
                e['nasc_dia'] = din.nasc_dia(r['id'], r['idade'], e['classe']) if e['vivo'] else None
            s.ent[r['id']] = e
        for r in world.t['relogio']:
            s.ent[r['id']] = dict(reino=r['reino'], segmentos=int(r['segmentos']), progresso=int(r['progresso']),
                                  estourado=False)
        for r in world.t['faccao']:
            s.ent[r['id']] = dict(reino=r['reino'], lideranca=r['lideranca'])
        for r in world.t['cidade']:
            s.ent[r['id']] = dict(reino=r['reino'], governante=r['governante'])
        s._postos(world)
        s._pares(world, L)
        if din and not din.vazia and din.econ:
            s._economia(world, din.econ)
        if din and not din.vazia and din.mist:
            for r in world.t['misterio']:
                loc = next((x for x in toks(r['entidades']) if x.startswith(('city.', 'settlement.'))), None)
                s.ent[r['id']] = dict(reino=r['reino'], estado='aberto', pistas=0, **(dict(localidade=loc) if loc else {}))
        for r in world.t['evento']:
            s.ent[r['id']] = dict(reino=r['reino'], estado='latente', passo=0, inicio_dia=None,
                                  total=max(1, len([x for x in r['cadeia'].split(' > ') if x.strip()])))
        if din and not din.vazia and din.front.get('front_tensao_limiar'):
            s._fronteiras(world)
        if din and not din.vazia and din.povos.get('povos_tensao_base') is not None:
            s._povos(world, din.povos)
        return s

    @staticmethod
    def povos_por_cidade(world):
        """Povos (raças e sub-raças) que convivem em cada cidade, derivados do molde: `cidade.povos` e os NPCs que moram nela."""
        racas = {r['id'] for r in world.catalogo['races']}
        sub = {x['id']: x['parentRaceId'] for x in world.catalogo['subraces']}
        tok = {c['id']: {x for x in toks(c['povos']) if x in racas} for c in world.t['cidade']}
        for n in world.t['npc']:
            if n['cidade'] in tok:
                if n['povo'] in racas: tok[n['cidade']].add(n['povo'])
                if n['subraca'] in sub: tok[n['cidade']].add(n['subraca'])
        return tok, sub

    def _povos(self, world, P):
        """F8.2: `laco.<cidade>@<a>+<b>` (um por cidade e par de povos que convivem; neutro, ASSUMINDO: o molde não declara alianças
        entre povos) e `pacto.<a>+<b>` (par que convive em pelo menos `povos_pacto_min_cidades` cidades). Pares de mesma linhagem ficam de fora."""
        tok, sub = self.povos_por_cidade(world)
        pares = {}
        for cid in sorted(tok):
            reino = world.reino_de(cid)
            for a, b in itertools.combinations(sorted(tok[cid]), 2):
                if sub.get(a, a) == sub.get(b, b):
                    continue
                self.ent['laco.%s@%s+%s' % (cid, a, b)] = dict(reino=reino, cidade=cid, a=a, b=b, relacao='neutra',
                                                              tensao=P['povos_tensao_base'], desde=None)
                pares.setdefault((a, b), []).append(cid)
        for (a, b), cs in sorted(pares.items()):
            if len(cs) >= P.get('povos_pacto_min_cidades', 2):
                reinos = sorted({world.reino_de(c) for c in cs})
                self.ent['pacto.%s+%s' % (a, b)] = dict(reino=reinos[0] if len(reinos) == 1 else None, reinos=reinos, cidade=cs[0],
                                                       a=a, b=b, cidades=cs, estado='nenhum', desde=None)

    def _fronteiras(self, world):
        """Um par `front.<a>+<b>` por vizinhança do anel (M-04): tensão em pontos, incidentes, relação (neutra, tratado, ruptura, guerra) e dia do último; e `crown.<reino>` (firme ou caída, F7.3). Derivado do molde."""
        for i, a in enumerate(ANEL):
            b = ANEL[(i + 1) % len(ANEL)]
            if a in world.reinos and b in world.reinos:
                self.ent['crown.' + a[8:]] = dict(reino=a, estado='firme', caida_dia=None)
                self.ent['front.%s+%s' % (a[8:], b[8:])] = dict(reino=a, reinos=[a, b], tensao=0, incidentes=0, ultimo_dia=None,
                                                                       relacao='neutra', relacao_dia=None, pendente=False)

    def _economia(self, world, E):
        """Rotas (`route.*`, estado operacional aberta/bloqueada) e linhas de economia (`econ.<recurso>@<lugar>`, escalas 0 a 5).
        Só cidades e assentamentos; '?' vira o padrão da dinâmica (ASSUMINDO). O molde não é editado."""
        def ordinal(txt, padrao):
            m = re.match(r'\s*(\d)', txt)
            return min(5, int(m.group(1))) if m else padrao
        for r in world.t['rota']:
            self.ent[r['id']] = dict(reino=r['reino'], de=r['de'], para=r['para'], evidencia=r['estado'], estado='aberta')
        for r in world.t['recurso']:
            loc = r['localidade']
            if not loc.startswith(('city.', 'settlement.')):
                continue
            k = 'econ.%s@%s' % (r['recurso'], loc)
            reino = world.reino_de(loc)
            if k in self.ent or not reino:
                continue
            prod = r['papel'] == 'produtor'
            p0 = ordinal(r['producao'], E.get('econ_producao_padrao', 2)) if prod else 0
            d0 = ordinal(r['demanda'], E.get('econ_demanda_padrao_produtor', 0) if prod else E.get('econ_demanda_padrao_consumidor', 2))
            self.ent[k] = dict(reino=reino, recurso=r['recurso'], localidade=loc, papel=r['papel'], producao=p0,
                               producao_atual=p0, demanda=d0, escassez=0, estacoes_criticas=0)

    def _postos(self, world):
        """Um posto por governante de cidade, líder de facção e NPC governante/general que não seja nenhum dos dois.
        Só entram titulares que são NPC vivo no molde (lacuna explícita não vira posto)."""
        def titulares(txt):
            t = [x for x in txt.split(';') if x.strip()]
            return t if t and all(x in self.ent and self.ent[x].get('vivo') for x in t) else None
        usados = set()
        for tipo, tab, campo, papel in (('cidade', 'cidade', 'governante', 'governante'),
                                        ('faccao', 'faccao', 'lideranca', 'lideranca')):
            for r in world.t[tab]:
                t = titulares(r[campo])
                if t:
                    self.ent['posto.' + r['id']] = dict(reino=r['reino'], tipo=tipo, alvo=r['id'], papel=papel,
                                                        titular=';'.join(t), vago_desde=None, prazo=None, interino='—', anterior='—')
                    usados.update(t)
        for r in world.t['npc']:
            p = set(x for x in r['papeis'].split(';'))
            if r['id'] not in usados and self.ent[r['id']]['vivo'] and p & {'governante', 'general'}:
                self.ent['posto.' + r['id']] = dict(reino=r['reino'], tipo='npc', alvo=r['id'],
                                                    papel='governante' if 'governante' in p else 'general',
                                                    titular=r['id'], vago_desde=None, prazo=None, interino='—', anterior='—')

    def _pares(self, world, L):
        al, ri = set(), set()
        for f in world.t['faccao']:
            for x in toks(f['aliados']):
                if x in self.ent and x != f['id']: al.add(tuple(sorted((f['id'], x))))
            for x in toks(f['rivais']):
                if x in self.ent and x != f['id']: ri.add(tuple(sorted((f['id'], x))))
        for a, b in sorted(al | ri):
            rel = 'ambigua' if (a, b) in al and (a, b) in ri else ('aliança' if (a, b) in al else 'rivalidade')
            t0 = {'aliança': L('tensao_base_alianca', 20), 'rivalidade': L('tensao_base_rivalidade', 50),
                  'ambigua': L('tensao_inicial_ambigua', 40)}[rel]
            self.ent['par.%s+%s' % (a, b)] = dict(reino=self.ent[a]['reino'], a=a, b=b, relacao=rel, tensao=t0)

    # ---- consultas
    def existe(self, i):
        return i in self.ent

    def vivo(self, i):
        e = self.ent.get(i)
        return e is not None and e.get('vivo', True) is True

    def morto(self, i):
        e = self.ent.get(i)
        return e is not None and e.get('vivo', True) is False

    # ---- aplicação de deltas
    def aplicar(self, d):
        t = d['tipo']
        if t == 'campo':
            e = self.ent.get(d['entidade'])
            if e is None:
                raise ErroEstado('entidade inexistente: ' + d['entidade'])
            atual = e.get(d['campo'])
            if atual != d['estado_anterior']:
                raise ErroEstado('estado_anterior não bate em %s.%s: esperado %r, atual %r' % (
                    d['entidade'], d['campo'], d['estado_anterior'], atual))
            e[d['campo']] = d['estado_novo']
        elif t == 'criacao':
            if d['entidade'] in self.ent:
                raise ErroEstado('entidade já existe: ' + d['entidade'])
            self.ent[d['entidade']] = json.loads(json.dumps(d['estado_novo']))
            self.n_criados += 1
        elif t == 'era':
            n = d['estado_novo']
            if {'era': self.mundo['era'], 'regime': self.mundo['regime']} != d['estado_anterior']:
                raise ErroEstado('estado_anterior da era não bate')
            self.eras[n['era']] = dict(nome=n['nome'], regime=n['regime'], dia_inicio=d['dia_mundo'])
            self.mundo = {'era': n['era'], 'regime': n['regime']}
        # outros tipos: só registro
        if d['tipo'] == 'campo' and str(d['entidade']).startswith('eco.') and d['campo'] == 'difusao':
            (self.ecos_ativos.add if d['estado_novo']['ativo'] else self.ecos_ativos.discard)(d['entidade'])
        elif d.get('importancia', 0) >= 3 and d['tipo'] != 'era' and not str(d['entidade']).startswith('eco.'):
            self._nasce_eco(d)

    PUBLICOS = ('alianca_de_povos', 'fim_de_alianca_de_povos', 'revolta', 'bloqueio_de_rota', 'guerra', 'queda_de_coroa', 'tratado', 'ruptura', 'disputa_aberta',
                'evento_ocorrido', 'fim_de_cadeia', 'incidente_fronteira', 'quebra_de_contrato')

    def _lugar(self, i, n=0):
        e = self.ent.get(i)
        if i.startswith(('city.', 'settlement.')):
            return i
        if e is None or n > 3:
            return None
        for c in ('localidade', 'de', 'cidade'):
            if str(e.get(c, '')).startswith(('city.', 'settlement.')):
                return e[c]
        if e.get('alvo'):
            return self._lugar(e['alvo'], n + 1)
        reino = e.get('reino')   # sem lugar próprio: a primeira cidade do reino (ASSUMINDO)
        return next((k for k in sorted(self.ent) if k.startswith('city.') and self.ent[k].get('reino') == reino), None)

    def _nasce_eco(self, d):
        lugar = self._lugar(d['entidade'])
        if lugar is None:
            return
        camada = 'restrito' if d['tipo'] in ('pista_de_misterio', 'tentativa_de_resolver_misterio') else \
            ('publico' if d['importancia'] >= 4 or d['tipo'] in self.PUBLICOS else 'local')
        k = 'eco.' + d['id']
        self.ent[k] = dict(reino=self.ent.get(d['entidade'], {}).get('reino') or self.ent.get(lugar, {}).get('reino'),
                           origem_delta=d['id'], tipo=d['tipo'], entidade=d['entidade'], camada=camada, inicio=d['dia_mundo'],
                           difusao=dict(passo=0, lugares=[lugar], certeza=d['certeza'], ativo=True))
        self.ecos_ativos.add(k)

    def estado_anterior_de(self, tipo, entidade, campo):
        if tipo == 'campo':
            e = self.ent.get(entidade)
            return None if e is None else e.get(campo)
        if tipo == 'era':
            return {'era': self.mundo['era'], 'regime': self.mundo['regime']}
        return None

    # ---- serialização
    def como_dict(self):
        return dict(proximo_dia=self.proximo_dia, mundo=self.mundo, eras=self.eras, n_criados=self.n_criados,
                    ent=self.ent)

    def hash(self):
        d = self.como_dict(); d.pop('proximo_dia')   # o dia avança mesmo sem delta; o estado é o que mudou
        return sha(canon(d))
