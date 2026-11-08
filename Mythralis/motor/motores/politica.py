"""Política de facções (F4). Anual. Tensão (0..100) por par de facções do mesmo reino, vinda do molde (aliados/rivais).
Sobe com a rivalidade e a pressão dos relógios, volta à base (homeostase); passou do limiar -> ruptura, disputa aberta,
trégua ou tratado, sempre com custo. Só dentro dos reinos (Era I); par aliado nunca vira rival sem passar por `rompida` (I-11)."""
class Politica:
    nome = 'politica'; frequencia = 'anual'

    def __init__(self, din):
        self.din = din

    def _base(self, rel):
        L = self.din.lim
        return {'aliança': L.get('tensao_base_alianca', 20), 'rivalidade': L.get('tensao_base_rivalidade', 50),
                'tratado': L.get('tensao_base_tratado', 25), 'rompida': L.get('tensao_base_rivalidade', 50),
                'disputa_aberta': L.get('limiar_disputa_aberta', 85), 'ambigua': L.get('tensao_inicial_ambigua', 40)}[rel]

    def executar(self, ctx):
        if self.din.vazia:
            return []
        L, e, out = self.din.lim, ctx.estado, []
        quase = {}
        for k, c in e.ent.items():
            if k.startswith('clock.') and not c['estourado'] and c['progresso'] >= c['segmentos'] - 2:
                quase[k] = c
        for k in sorted(k for k in e.ent if k.startswith('par.')):
            p = e.ent[k]
            a, b, rel, t = p['a'], p['b'], p['relacao'], p['tensao']
            rep = [e.ent[a]['lideranca'], e.ent[b]['lideranca']]
            ids = [k]
            base = self._base(rel)
            dt = ctx.rng.randint(-L.get('deriva_anual', 6), L.get('deriva_anual', 6))
            dt += (base - t) * L.get('decaimento_anual', 3) // 20 if base != t else 0
            dr = self.din.relogios
            for ck in quase:
                rg = dr.get(ck)
                if rg and a in rg['pressoes'] and b in rg['pressoes']:
                    dt += L.get('pressao_relogio', 3); ids.append(ck)
            novo_t = max(0, min(L.get('tensao_maxima', 100), t + dt))
            q = [p['reino']]
            if novo_t != t:
                out.append(dict(tipo='campo', entidade=k, campo='tensao', estado_novo=novo_t,
                                causa=dict(ids=ids[:3], regra='tensao-anual'), quem_sabe=q))
            nova_rel, tipo_reg, custo = None, None, 0
            if rel == 'aliança' and novo_t >= L.get('limiar_romper_alianca', 80):
                nova_rel, tipo_reg, custo = 'rompida', 'ruptura', 1
            elif rel == 'rivalidade' and novo_t >= L.get('limiar_disputa_aberta', 85):
                nova_rel, tipo_reg, custo = 'disputa_aberta', 'disputa_aberta', 1
            elif rel in ('rivalidade', 'ambigua') and novo_t <= L.get('limiar_tratado', 15):
                nova_rel, tipo_reg = 'tratado', 'tratado'
            elif rel == 'disputa_aberta' and novo_t <= L.get('limiar_tregua', 55):
                nova_rel, tipo_reg = 'rivalidade', 'tregua'
            elif rel == 'tratado' and novo_t >= L.get('limiar_tratado_quebra', 70):
                nova_rel, tipo_reg, custo = 'rivalidade', 'ruptura', 1
            elif rel == 'rompida':
                nova_rel, tipo_reg = 'rivalidade', 'tensao_pos_ruptura'
            if nova_rel:
                cz = dict(ids=[k, a, b], regra='limiar-de-' + tipo_reg.replace('_', '-'))
                out.append(dict(tipo=tipo_reg, entidade=k, causa=cz, quem_sabe=q, agentes=[x for x in rep if e.vivo(x)],
                                estado_novo=dict(de=rel, para=nova_rel, tensao=novo_t)))
                out.append(dict(tipo='campo', entidade=k, campo='relacao', estado_novo=nova_rel, causa=cz, quem_sabe=q))
                if custo:   # custo da ruptura (homeostase): a tensão cai depois do estouro
                    t2 = max(0, novo_t - L.get('custo_ruptura', 25))
                    out.append(dict(tipo='campo', entidade=k, campo='tensao', estado_novo=t2, causa=cz, quem_sabe=q))
        return out
