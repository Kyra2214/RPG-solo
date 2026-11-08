"""Níveis de relevância (F8.1, D-M14). Leitura DERIVADA dos deltas: nada é gravado na crônica nem entra nos hashes.
  1 = alianças e rupturas entre nações, raças e sub-raças (pactos, fronteira, guerra, coroa, era) e reis (governante e herdeiro, e o posto
      de governante da capital)
  2 = heróis, guardiões, benfeitores, generais, NPCs principais, líderes de facção, governantes de cidades que não são capital, pares de
      facções e laços de povos com mediador
  3 = o resto; só entra na história se for CAUSA citada de um delta de nível 1 ou 2 (`citados`); senão vira contagem (virada de era)
Cada delta também é atribuído a uma cidade (quando tem) e a um ou mais reinos."""
import re
import relatorio as REL

TIPOS_N1 = ('era', 'guerra', 'queda_de_coroa', 'fato_confirmado_alterado', 'alianca_de_povos', 'fim_de_alianca_de_povos')
TIPOS_LACO = ('laco_aliado', 'laco_rompido', 'laco_rival')
PAPEIS_N1 = {'governante', 'herdeiro'}
PAPEIS_N2 = {'heroi', 'guardiao', 'benfeitor', 'general', 'principal'}
SUC_REGRAS = {'sucessao-herdeiro', 'sucessao-vice', 'sucessao-candidato', 'diarquia-reduzida', 'nomeacao-sem-candidato',
              'nomeacao-de-simulado', 'vacancia', 'interino-substituido'}
GOVERNANTE_DE_CIDADE_N1 = False  # F9.2 (D-179): True sobe o governante de toda cidade ao nível 1 (testado: estoura a meta do nível 1); False = regra da F8 (só capitais)
NOMES_NIVEL = {1: 'nível 1', 2: 'nível 2', 3: 'nível 3'}


class Niveis:
    def __init__(self, world, deltas=(), estado=None, reinos=None):
        self.w = world; self.estado = estado
        self.R = reinos or REL.Reinos(world, deltas)
        self.capitais = {r['capital'] for r in world.t['reino']}
        self.papeis = {n['id']: set(n['papeis'].split(';')) | ({'principal'} if n['principal'] == 'sim' else set()) for n in world.t['npc']}
        self.npc_cidade = {n['id']: n['cidade'] for n in world.t['npc']}
        for d in deltas:
            self.ver(d)

    def ver(self, d):
        """Aprende NPCs simulados (criacao) à medida que a crônica avança."""
        if d['tipo'] == 'criacao' and str(d['entidade']).startswith('npc.') and isinstance(d.get('estado_novo'), dict):
            n = d['estado_novo']
            self.papeis[d['entidade']] = set(str(n.get('papeis', '')).split(';')) | ({'principal'} if n.get('principal') else set())
            self.npc_cidade[d['entidade']] = n.get('cidade', '—')

    def _npc(self, e):
        """(papéis, cidade) de um NPC: do molde, das criações vistas ou do estado (NPC simulado de um bloco anterior)."""
        if e in self.papeis:
            return self.papeis[e], self.npc_cidade.get(e)
        x = (self.estado.ent.get(e) if self.estado else None) or {}
        return set(str(x.get('papeis', '')).split(';')) | ({'principal'} if x.get('principal') else set()), x.get('cidade')

    def cidade(self, d):
        e = str(d['entidade'])
        if e.startswith('laco.'): return e[5:].split('@')[0]
        if e.startswith('city.'): return e
        if e.startswith('posto.city.'): return e[6:]
        if e.startswith('npc.'):
            c = self._npc(e)[1]
            return c if c and c.startswith('city.') else None
        if e.startswith('pacto.') and isinstance(d.get('estado_novo'), dict) and d['estado_novo'].get('cidades'):
            return d['estado_novo']['cidades'][0]
        if e.startswith('econ.') and '@city.' in e: return e.split('@')[1]
        return None

    def reinos(self, d):
        if str(d['entidade']) in self.w.reinos:
            return [d['entidade']]
        if str(d['entidade']).startswith('pacto.'):
            return [q for q in (d.get('quem_sabe') or []) if q in self.w.reinos] or ['—']
        c = self.cidade(d)
        r = self.w.reino_de(c) if c else None
        r = r or self.R.de(d['entidade'])
        return [r] if r and r != '—' else (['—'] if not str(d['entidade']).startswith(('front.', 'crown.')) else
                                         [q for q in (d.get('quem_sabe') or []) if q in self.w.reinos] or ['—'])

    def _mediador(self, d):
        return any(self._npc(a)[0] & PAPEIS_N2 for a in d.get('agentes') or [])

    def nivel(self, d):
        t, e, c = d['tipo'], str(d['entidade']), d.get('campo')
        if t in TIPOS_N1 or e.startswith(('front.', 'crown.', 'pacto.')): return 1
        if t in TIPOS_LACO or e.startswith('laco.'):
            return 2 if self._mediador(d) else 3
        if e.startswith('npc.'):
            p, cn = self._npc(e)
            if p & PAPEIS_N1 and cn in self.capitais: return 1   # rei: governante ou herdeiro na capital
            if GOVERNANTE_DE_CIDADE_N1 and 'governante' in p and cn: return 1   # F9.2: governante de qualquer cidade
            return 2 if p & (PAPEIS_N1 | PAPEIS_N2) else 3
        cid = self.cidade(d)
        if e.startswith(('city.', 'posto.city.')) and c in ('governante', 'titular', 'vago_desde', 'interino'):
            return 1 if (cid in self.capitais or GOVERNANTE_DE_CIDADE_N1) else 2
        if e.startswith(('faction.', 'posto.faction.')) and c in ('lideranca', 'titular', 'vago_desde', 'interino'): return 2
        if e.startswith('par.') and t in ('tratado', 'ruptura', 'disputa_aberta', 'campo'): return 2
        return 3

    @staticmethod
    def evento(d):
        """Chave de contagem (F9.1): todos os deltas de uma sucessão (posse, vacância, interino, cargo, papéis, criação do simulado) no mesmo dia
        e posto contam como UM evento; os demais deltas contam um cada. Só leitura; a crônica e os hashes não mudam."""
        c = d.get('causa') or {}
        if c.get('regra') not in SUC_REGRAS: return d['id']
        e = str(d['entidade'])
        if e.startswith('posto.'): posto = e
        elif e.startswith(('city.', 'faction.')): posto = 'posto.' + e
        elif d['tipo'] == 'criacao' and c.get('ids'): posto = 'posto.' + str(c['ids'][0])
        elif c.get('ids') and str(c['ids'][0]).startswith('posto.'): posto = str(c['ids'][0])
        else: return d['id']
        return 'suc:%s:%s' % (d['dia_mundo'], posto)

    @staticmethod
    def citados(deltas, nivel_de):
        """Entidades citadas na causa de deltas de nível 1 ou 2: o nível 3 que 'é necessário para a história'."""
        s = set()
        for d in deltas:
            if d.get('importancia', 0) >= 2 and nivel_de(d) <= 2:
                s.update(d['causa']['ids'])
        return s


def gerar_niveis(saida, world, deltas, cab):
    import os, collections
    import calendario as CAL
    N = Niveis(world, deltas)
    ano0 = cab.get('ano_base', CAL.ANO_BASE)
    niv = {d['id']: N.nivel(d) for d in deltas}
    cit = N.citados(deltas, lambda d: niv[d['id']])
    dec = lambda d: (CAL.ano_base(d['dia_mundo']) - ano0) // 10
    n1 = [d for d in deltas if niv[d['id']] == 1 and d.get('importancia', 0) >= 3 and d['entidade'] != 'mundo']
    n2 = [d for d in deltas if niv[d['id']] == 2 and d.get('importancia', 0) >= 2]
    n3 = [d for d in deltas if niv[d['id']] == 3 and d.get('importancia', 0) >= 2]
    eras = [(d['estado_novo'], d['dia_mundo']) for d in deltas if d['tipo'] == 'era']
    ult = max((d['dia_mundo'] for d in deltas), default=0)

    def era_de(d):
        k = 0
        for i, (_, ini) in enumerate(eras):
            if d['dia_mundo'] >= ini: k = i
        return k
    nome_cid = {c['id']: c['nome'].split(' — ')[0] for c in world.t['cidade']}
    nome_rei = {k: v['row']['nome'] for k, v in world.reinos.items()}
    L = ['# Relatório por níveis de relevância', '',
         'Semente %s, anos %d a %d. Leitura derivada dos deltas (D-M14); não entra nos hashes.' % (cab.get('semente'), ano0, CAL.ano_base(ult)), '',
         '**Nível 1:** alianças e rupturas entre nações, raças e sub-raças, e reis (cada evento, abaixo). **Nível 2:** heróis, NPCs secundários, '
         'facções e laços com mediador (um resumo por década e reino). **Nível 3:** o resto (só entra se for causa citada; senão, contagem por era).', '']
    for i, (era, ini) in enumerate(eras):
        fim = eras[i + 1][1] if i + 1 < len(eras) else ult + 1
        L += ['## %s — %s (regime %s)' % (era['era'], era['nome'], era['regime']), '', '### Nível 1 — marcos', '']
        ds = [d for d in n1 if ini <= d['dia_mundo'] < fim]
        for d in ds:
            rs = N.reinos(d); c = N.cidade(d)
            L.append('- ano %d [%s%s]: %s' % (CAL.ano_base(d['dia_mundo']) - ano0, ', '.join(nome_rei.get(r, r) for r in rs),
                                                (' / ' + nome_cid.get(c, c)) if c else '', REL.texto(d)))
        if not ds: L.append('- (nenhum)')
        L += ['', '### Nível 2 — resumo por década', '']
        porrei = collections.defaultdict(list)
        for d in n2:
            if ini <= d['dia_mundo'] < fim:
                for r in N.reinos(d)[:1]: porrei[(dec(d), r)].append(d)
        for (k, r) in sorted(porrei):
            ds2 = list({N.evento(d): d for d in reversed(porrei[(k, r)])}.values())
            cs = collections.Counter(nome_cid.get(N.cidade(d), '—') for d in ds2 if N.cidade(d))
            ts = collections.Counter('laços de povos com mediador' if d['tipo'] in TIPOS_LACO else
                                     ('mortes' if d['tipo'] == 'campo' and d.get('campo') == 'vivo' else
                                      ('sucessões' if N.evento(d).startswith('suc:') else
                                       ('criações' if d['tipo'] == 'criacao' else 'outros'))) for d in ds2)
            L.append('- anos %d a %d, %s: %d eventos (%s); cidades: %s.' % (k * 10, k * 10 + 9, nome_rei.get(r, r), len(ds2),
                     ', '.join('%s %d' % (a, b) for a, b in ts.most_common()), ', '.join('%s %d' % (a, b) for a, b in cs.most_common(3)) or '—'))
        L += ['', '### Nível 3 — resumo da virada de era', '']
        d3 = [d for d in n3 if ini <= d['dia_mundo'] < fim]
        tc = collections.Counter(d['tipo'] if d['tipo'] != 'campo' else 'campo:' + str(d.get('campo')) for d in d3)
        L.append('%d eventos de nível 3 (importância 2+): %s.' % (len(d3), ', '.join('%s %d' % (a, b) for a, b in tc.most_common(8)) or 'nenhum'))
        cd = [d for d in d3 if d['entidade'] in cit]
        L.append('Citados como causa de nível 1 ou 2: %d.' % len(cd))
        for d in cd[:20]:
            L.append('- ano %d: %s' % (CAL.ano_base(d['dia_mundo']) - ano0, REL.texto(d)))
        L.append('')
    L += ['## Total por cidade', '', '| cidade | reino | nível 1 | nível 2 | nível 3 |', '|---|---|---|---|---|']
    por_c = collections.defaultdict(lambda: [0, 0, 0]); linhas = []
    for lista, ix in ((n1, 0), (n2, 1), (n3, 2)):
        for d in lista:
            c = N.cidade(d)
            if c:
                por_c[c][ix] += 1
                linhas.append((dec(d), c, ix))
    for c in sorted(r['id'] for r in world.t['cidade']):
        L.append('| %s | %s | %d | %d | %d |' % (nome_cid[c], nome_rei.get(world.reino_de(c), '—'), *por_c[c]))
    with open(os.path.join(saida, 'niveis.md'), 'w', encoding='utf-8', newline='\n') as f:
        f.write('\n'.join(L) + '\n')
    cont = collections.Counter(linhas)
    with open(os.path.join(saida, 'metricas_cidades.csv'), 'w', encoding='utf-8', newline='\n') as f:
        f.write('decada|cidade|nivel|eventos\n')
        for (k, c, ix) in sorted(cont):
            f.write('%d|%s|%d|%d\n' % (k, c, ix + 1, cont[(k, c, ix)]))
