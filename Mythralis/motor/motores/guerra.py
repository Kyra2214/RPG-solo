"""Guerra (F7.3). Anual; só fora do regime selado. Um par `front.*` em `ruptura` com tensão alta e as duas coroas `firmes` pode entrar em
guerra (`guerra`, relação `guerra`). A PRIMEIRA guerra muda o regime para `aberto` (era nova, causa `primeira-guerra`). Durante a guerra:
baixas (morte de NPC principal ou general, só com vivos suficientes) e chance de queda de coroa (`queda_de_coroa`, `crown.<reino>` passa a
`caida`; a guerra termina, nova era). Sem queda, a guerra acaba em `guerra_anos` com um tratado de paz. Tudo com causa; a tensão do par
fica congelada em guerra (fronteira e diplomacia não mexem nele)."""
from .diplomacia import Diplomacia


class Guerra:
    nome = 'guerra'; frequencia = 'anual'

    def __init__(self, din):
        self.din = din

    def executar(self, ctx):
        G, D, e, out = self.din.guerra, self.din.dipl, ctx.estado, []
        if self.din.vazia or not G or e.mundo['regime'] == 'selado':
            return []
        dia, regime, n_era = ctx.dia_mundo, e.mundo['regime'], len(e.eras)
        mortos_agendados = set()

        def era(nome, regime_novo, causa):
            nonlocal n_era
            n_era += 1
            return dict(tipo='era', entidade='mundo', quem_sabe=['mundo.mythralis'], causa=causa,
                        estado_novo=dict(era='era.%03d' % n_era, nome=nome, regime=regime_novo))

        for k in sorted(k for k in e.ent if k.startswith('front.')):
            f = e.ent[k]; qs = list(f['reinos']); cz = dict(ids=[k], regra='guerra-de-fronteira')
            coroas = ['crown.' + q[8:] for q in qs]

            def campos(rel, tensao, pend=False):
                r = [dict(tipo='campo', entidade=k, campo='relacao', estado_novo=rel, quem_sabe=qs, causa=cz),
                     dict(tipo='campo', entidade=k, campo='relacao_dia', estado_novo=dia, quem_sabe=qs, causa=cz)]
                if tensao != f['tensao']:
                    r.append(dict(tipo='campo', entidade=k, campo='tensao', estado_novo=tensao, quem_sabe=qs, causa=cz))
                if pend:
                    r.append(dict(tipo='campo', entidade=k, campo='pendente', estado_novo=False, quem_sabe=qs, causa=cz))
                return r
            if f['relacao'] == 'guerra':
                for q in qs:
                    cands = sorted(i for i, v in e.ent.items() if i.startswith('npc.') and i not in mortos_agendados
                                   and v.get('reino') == q and v.get('vivo') is True
                                   and (v.get('principal') or 'general' in str(v.get('papeis', '')).split(';')))
                    if len(cands) >= G.get('guerra_baixa_min_vivos', 4) and ctx.rng.randrange(1000) < G.get('guerra_baixa_pm', 150):
                        vitima = cands[ctx.rng.randrange(len(cands))]
                        mortos_agendados.add(vitima)
                        out.append(dict(tipo='campo', entidade=vitima, campo='vivo', estado_novo=False,
                                        quem_sabe=[q], causa=dict(cz, regra='morte-em-guerra')))
                caiu = None
                if dia >= f['relacao_dia'] + G.get('queda_min_anos', 3) * 365:
                    for q, c in zip(qs, coroas):
                        if c in e.ent and e.ent[c]['estado'] == 'firme' and ctx.rng.randrange(1000) < G.get('queda_pm', 20):
                            caiu = (q, c); break
                if caiu:
                    q, c = caiu; cc = dict(ids=[k, c], regra='queda-de-coroa-em-guerra')
                    out.append(dict(tipo='queda_de_coroa', entidade=c, quem_sabe=qs, causa=cc, estado_novo=dict(guerra=k)))
                    out.append(dict(tipo='campo', entidade=c, campo='estado', estado_novo='caida', quem_sabe=qs, causa=cc))
                    out.append(dict(tipo='campo', entidade=c, campo='caida_dia', estado_novo=dia, quem_sabe=qs, causa=cc))
                    out += campos('neutra', G.get('guerra_pos_tensao', 150))
                    out.append(era('Queda da Coroa de %s' % ctx.mundo.reinos[q]['row']['nome'], regime, cc))
                elif dia >= f['relacao_dia'] + G.get('guerra_anos', 6) * 365:
                    ags = [a for a in (Diplomacia._agente(e, q, mortos_agendados) for q in qs) if a]
                    out.append(dict(tipo='tratado', entidade=k, quem_sabe=qs, agentes=ags, causa=dict(cz, regra='paz-apos-guerra'),
                                    estado_novo=dict(paz=True, duracao_anos=D.get('dipl_tratado_anos', 30))))
                    out += campos('tratado', G.get('guerra_pos_tensao', 150))
                continue
            if f['relacao'] == 'ruptura' and f['tensao'] >= G.get('guerra_tensao_min', 330) \
                    and all(c in e.ent and e.ent[c]['estado'] == 'firme' for c in coroas) \
                    and ctx.rng.randrange(1000) < G.get('guerra_pm', 80):
                out.append(dict(tipo='guerra', entidade=k, quem_sabe=qs, causa=cz, estado_novo=dict(tensao=f['tensao'])))
                out += campos('guerra', f['tensao'], f['pendente'])
                if regime != 'aberto':
                    out.append(era('Fronteiras Abertas', 'aberto', dict(ids=[k], regra='primeira-guerra')))
                    regime = 'aberto'
        return out
