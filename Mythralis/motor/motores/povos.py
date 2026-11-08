"""Povos (F8.2). Anual. Laços entre raças e sub-raças em todas as cidades (`laco.<cidade>@<a>+<b>`) e pactos entre povos
(`pacto.<a>+<b>`). Cada laço é avaliado a cada `povos_passo_anos` anos (escalonado pelo índice do laço, para a crônica não explodir):
a tensão volta à base, sorteia, sobe com vacância do posto de governante, escassez 3 na cidade e guerra do reino, e desce com mediadores
(herói, guardião ou benfeitor de um dos povos que mora na cidade) e com o pacto do par. O pacto acaba quando os laços aliados
caem abaixo do mínimo que o formou. `neutra` com tensão baixa vira `alianca`
(assinada pelo governante vivo da cidade); `alianca` com tensão alta rompe; `neutra` com tensão muito alta vira `rivalidade`.
Pacto: laços aliados do par em `povos_pacto_min_cidades` cidades. No regime selado só conta um reino de cada vez (I-10); fora dele o
pacto pode cruzar reinos. O motor só lê os outros motores; não mexe em fronteira, guerra nem política. Tudo com causa."""
MEDIADORES = ('heroi', 'guardiao', 'benfeitor')


class Povos:
    nome = 'povos'; frequencia = 'anual'

    def __init__(self, din):
        self.din = din

    @staticmethod
    def _governante(e, cid):
        for g in str(e.ent[cid].get('governante', '')).replace(',', ';').split(';'):
            g = g.strip()
            if g in e.ent and e.vivo(g):
                return g
        return None

    @staticmethod
    def _eh(n, povo):
        return n.get('povo') == povo or n.get('subraca') == povo

    def executar(self, ctx):
        P, e, out = self.din.povos, ctx.estado, []
        if self.din.vazia or not P:
            return []
        dia, selado = ctx.dia_mundo, e.mundo['regime'] == 'selado'
        ano = dia // 365; passo = max(1, P.get('povos_passo_anos', 5))
        em_guerra = set()
        for k, f in e.ent.items():
            if k.startswith('front.') and f['relacao'] == 'guerra':
                em_guerra.update(f['reinos'])
        escassez = {x['localidade'] for k, x in e.ent.items() if k.startswith('econ.') and x['escassez'] >= 3}
        mediadores = {}
        for k in sorted(k for k in e.ent if k.startswith('npc.')):
            n = e.ent[k]
            if n.get('vivo') is True and set(str(n.get('papeis', '')).split(';')) & set(MEDIADORES):
                mediadores.setdefault(n.get('cidade'), []).append(k)
        pactos = {k: v for k, v in e.ent.items() if k.startswith('pacto.')}
        lacos = sorted(k for k in e.ent if k.startswith('laco.'))
        for i, k in enumerate(lacos):
            if (ano + i) % passo:
                continue
            l = e.ent[k]; cid = l['cidade']; reino = l['reino']
            pk = 'pacto.%s+%s' % (l['a'], l['b'])
            meds = [m for m in mediadores.get(cid, []) if self._eh(e.ent[m], l['a']) or self._eh(e.ent[m], l['b'])]
            base = P['povos_tensao_base']; t = l['tensao']
            t += max(-P['povos_decaimento'], min(P['povos_decaimento'], base - t))
            t += ctx.rng.randint(-P['povos_deriva'], P['povos_deriva'])
            posto = e.ent.get('posto.' + cid)
            if posto and posto.get('vago_desde') is not None: t += P['povos_pressao_vacancia']
            if cid in escassez: t += P['povos_pressao_escassez']
            if reino in em_guerra: t += P['povos_pressao_guerra']
            t -= P['povos_alivio_mediador'] * min(P['povos_alivio_mediador_max'], len(meds))
            if pk in pactos and pactos[pk]['estado'] == 'alianca': t -= P['povos_alivio_pacto']
            t = max(0, min(100, t))
            rel, novo, pos, reg, ags = l['relacao'], l['relacao'], t, None, []
            qs = [reino, cid]; cz = dict(ids=[k, cid], regra='laco-de-povos')
            if rel == 'neutra' and t <= P['povos_lim_aliar']:
                gov = self._governante(e, cid)
                if gov:
                    novo, pos, reg, ags = 'alianca', P['povos_pos_alianca'], 'laco_aliado', [gov] + meds[:2]
            elif rel == 'neutra' and t >= P['povos_lim_rival']:
                novo, reg = 'rivalidade', 'laco_rival'
            elif rel == 'alianca' and t >= P['povos_lim_romper']:
                novo, pos, reg = 'neutra', P['povos_pos_ruptura'], 'laco_rompido'
            elif rel == 'rivalidade' and t <= P['povos_lim_rival_fim']:
                novo = 'neutra'
            if reg:
                out.append(dict(tipo=reg, entidade=k, agentes=ags, quem_sabe=qs, causa=cz,
                                estado_novo=dict(cidade=cid, a=l['a'], b=l['b'], de=rel, para=novo)))
            if novo != rel:
                out.append(dict(tipo='campo', entidade=k, campo='relacao', estado_novo=novo, agentes=ags, quem_sabe=qs, causa=cz))
                out.append(dict(tipo='campo', entidade=k, campo='desde', estado_novo=dia, quem_sabe=qs, causa=cz))
            if pos != l['tensao']:
                out.append(dict(tipo='campo', entidade=k, campo='tensao', estado_novo=pos, quem_sabe=qs, causa=cz))

        # pactos: contas com o estado atual + mudanças deste ciclo
        mudou = {}
        for d in out:
            if d['tipo'] == 'campo' and d.get('campo') == 'relacao':
                mudou[d['entidade']] = d['estado_novo']
        for pk in sorted(pactos):
            pc = pactos[pk]
            por_reino = {}
            for cid in pc['cidades']:
                lk = 'laco.%s@%s+%s' % (cid, pc['a'], pc['b'])
                rel = mudou.get(lk, e.ent[lk]['relacao'])
                if rel == 'alianca':
                    por_reino.setdefault(e.ent[cid]['reino'], []).append(cid)
            n_total = sum(len(v) for v in por_reino.values())
            n = max((len(v) for v in por_reino.values()), default=0) if selado else n_total
            qs = sorted(por_reino) or list(pc['reinos'])
            cz = dict(ids=[pk] + sorted(c for v in por_reino.values() for c in v)[:6], regra='pacto-entre-povos')
            if pc['estado'] == 'nenhum' and n >= P.get('povos_pacto_min_cidades', 2):
                alvo = sorted(por_reino, key=lambda r: (-len(por_reino[r]), r))[:1] if selado else sorted(por_reino)
                cids = sorted(c for r in alvo for c in por_reino[r])
                ags = []
                for c in cids:
                    g = self._governante(e, c)
                    if g and g not in ags: ags.append(g)
                    ags += [m for m in mediadores.get(c, []) if (self._eh(e.ent[m], pc['a']) or self._eh(e.ent[m], pc['b'])) and m not in ags][:1]
                ags = ags[:6]
                out.append(dict(tipo='alianca_de_povos', entidade=pk, agentes=ags, quem_sabe=alvo, causa=cz,
                                estado_novo=dict(a=pc['a'], b=pc['b'], cidades=cids)))
                out.append(dict(tipo='campo', entidade=pk, campo='estado', estado_novo='alianca', agentes=ags, quem_sabe=alvo, causa=cz))
                out.append(dict(tipo='campo', entidade=pk, campo='desde', estado_novo=dia, quem_sabe=alvo, causa=cz))
            elif pc['estado'] == 'alianca' and n < P.get('povos_pacto_min_cidades', 2):
                out.append(dict(tipo='fim_de_alianca_de_povos', entidade=pk, quem_sabe=list(pc['reinos']),
                                causa=dict(ids=[pk], regra='pacto-sem-lacos-suficientes'), estado_novo=dict(a=pc['a'], b=pc['b'])))
                out.append(dict(tipo='campo', entidade=pk, campo='estado', estado_novo='nenhum', quem_sabe=list(pc['reinos']),
                                causa=dict(ids=[pk], regra='pacto-sem-lacos-suficientes')))
                out.append(dict(tipo='campo', entidade=pk, campo='desde', estado_novo=dia, quem_sabe=list(pc['reinos']),
                                causa=dict(ids=[pk], regra='pacto-sem-lacos-suficientes')))
        return out
