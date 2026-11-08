"""Relógios executáveis (F4). Lunar. Avanço por pressões, recuo por travas e homeostase; estouro quando completo.
Regra por relógio em dinamica/relogios_dinamica.txt (padrão com aviso do validador se faltar)."""


class Relogios:
    nome = 'relogios'; frequencia = 'lunar'

    def __init__(self, din):
        self.din = din

    def _ativa(self, ctx, i):
        e = ctx.estado
        if i.startswith('npc.'):
            return e.vivo(i)
        if i.startswith('faction.'):   # facção pressiona quando algum par dela está em tensão alta
            return any(p['tensao'] >= self.din.lim.get('limiar_disputa_aberta', 85) - 20 for k, p in e.ent.items()
                       if k.startswith('par.') and i in (p['a'], p['b']))
        return True

    def executar(self, ctx):
        if self.din.vazia:
            return []
        out, e, L = [], ctx.estado, self.din.lim
        for i in sorted(k for k in e.ent if k.startswith('clock.')):
            c = e.ent[i]
            if c['estourado']:
                continue
            reg = self.din.relogios.get(i) or dict(taxa=20, pressoes=[], travas=[], eventos=[], inacao=12)
            pres = sum(1 for x in reg['pressoes'] if self._ativa(ctx, x))
            trav = sum(1 for x in reg['travas'] if self._ativa(ctx, x))
            avanco = max(0, min(L.get('relogio_max_pm', 300), reg['taxa'] + reg['inacao'] + L.get('relogio_pressao_pm', 4) * pres
                                - L.get('relogio_trava_pm', 4) * trav))
            recuo = L.get('relogio_recuo_base_pm', 20) + L.get('relogio_trava_pm', 4) * trav
            causa = dict(ids=[i] + [x for x in reg['pressoes'] + reg['travas'] if x in e.ent][:3], regra='relogio-lunar')
            q = [e.ent[i]['reino']]
            if c['progresso'] >= c['segmentos']:
                out.append(dict(tipo='campo', entidade=i, campo='estourado', estado_novo=True, causa=dict(ids=[i], regra='relogio-completo'),
                                quem_sabe=q))
                continue
            r = ctx.rng.randrange(1000)
            if r < avanco:
                out.append(dict(tipo='campo', entidade=i, campo='progresso', estado_novo=c['progresso'] + 1,
                                causa=dict(causa, regra='relogio-avanco'), quem_sabe=q))
            elif c['progresso'] > 0 and r < avanco + recuo:
                out.append(dict(tipo='campo', entidade=i, campo='progresso', estado_novo=c['progresso'] - 1,
                                causa=dict(causa, regra='relogio-recuo'), quem_sabe=q))
        return out
