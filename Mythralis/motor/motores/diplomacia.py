"""Diplomacia (F7.2). Anual; só fora do regime selado. Cada par `front.<a>+<b>` tem `relacao` (neutra, tratado, ruptura).
Um incidente `pendente` (F7.1) espera `dipl_janela_anos`: a cada ano pode virar tratado ou ruptura; sem resposta esfria.
Tratado: dura `dipl_tratado_anos`, tira tensão e desacelera a subida; pode ser quebrado (`quebra_de_contrato` e ruptura).
Ruptura: soma tensão, acelera a subida e dura `dipl_ruptura_anos`. Agentes de um tratado: o governante vivo de uma cidade de
cada reino (cruzar a fronteira é permitido no regime poroso; fica registrado). Uma transição por par por ano; tudo com causa."""


class Diplomacia:
    nome = 'diplomacia'; frequencia = 'anual'

    def __init__(self, din):
        self.din = din

    @staticmethod
    def _agente(e, reino, excluir=None):
        excluir = set(excluir or ())
        for k in sorted(k for k in e.ent if k.startswith('city.') and e.ent[k].get('reino') == reino):
            for g in str(e.ent[k].get('governante', '')).replace(',', ';').split(';'):
                if g.strip() not in excluir and g.strip() in e.ent and e.vivo(g.strip()):
                    return g.strip()
        return None

    def executar(self, ctx):
        D, e, out = self.din.dipl, ctx.estado, []
        if self.din.vazia or not D or e.mundo['regime'] == 'selado':
            return []
        dia = ctx.dia_mundo
        tmax = self.din.front.get('front_tensao_max', 400)
        for k in sorted(k for k in e.ent if k.startswith('front.')):
            f = e.ent[k]; qs = list(f['reinos'])
            cz = dict(ids=[k], regra='diplomacia-de-fronteira')

            def campos(rel, tensao, pend=False):
                r = [dict(tipo='campo', entidade=k, campo='relacao', estado_novo=rel, quem_sabe=qs, causa=cz),
                     dict(tipo='campo', entidade=k, campo='relacao_dia', estado_novo=dia, quem_sabe=qs, causa=cz)]
                if tensao != f['tensao']:
                    r.append(dict(tipo='campo', entidade=k, campo='tensao', estado_novo=tensao, quem_sabe=qs, causa=cz))
                if pend:
                    r.append(dict(tipo='campo', entidade=k, campo='pendente', estado_novo=False, quem_sabe=qs, causa=cz))
                return r
            rel, desde = f['relacao'], f['relacao_dia']
            if rel == 'guerra':
                continue   # a guerra é do motor de guerra (F7.3)
            if rel == 'tratado' and dia >= desde + D.get('dipl_tratado_anos', 30) * 365:
                out += campos('neutra', f['tensao']); continue
            if rel == 'ruptura' and dia >= desde + D.get('dipl_ruptura_anos', 12) * 365:
                out += campos('neutra', f['tensao']); continue
            if rel == 'tratado' and ctx.rng.randrange(1000) < D.get('dipl_quebra_pm', 25):
                out.append(dict(tipo='quebra_de_contrato', entidade=k, quem_sabe=qs, causa=dict(cz, regra='tratado-quebrado'),
                                estado_novo=dict(de='tratado')))
                out += campos('ruptura', min(tmax, f['tensao'] + D.get('dipl_ruptura_soma', 60))); continue
            if not f['pendente']:
                continue
            if dia - f['ultimo_dia'] > D.get('dipl_janela_anos', 5) * 365:
                out.append(dict(tipo='campo', entidade=k, campo='pendente', estado_novo=False, quem_sabe=qs,
                                causa=dict(cz, regra='incidente-sem-resposta')))
                continue
            r = ctx.rng.randrange(1000)
            if r < D.get('dipl_tratado_pm', 350):
                ags = [a for a in (self._agente(e, q) for q in qs) if a]
                out.append(dict(tipo='tratado', entidade=k, quem_sabe=qs, agentes=ags, causa=dict(cz, regra='tratado-de-fronteira'),
                                estado_novo=dict(duracao_anos=D.get('dipl_tratado_anos', 30))))
                out += campos('tratado', max(0, f['tensao'] - D.get('dipl_tratado_reduz', 120)), True)
            elif r < D.get('dipl_tratado_pm', 350) + D.get('dipl_ruptura_pm', 200):
                out.append(dict(tipo='ruptura', entidade=k, quem_sabe=qs, causa=dict(cz, regra='ruptura-de-fronteira'),
                                estado_novo=dict(tensao=f['tensao'])))
                out += campos('ruptura', min(tmax, f['tensao'] + D.get('dipl_ruptura_soma', 60)), True)
        return out
