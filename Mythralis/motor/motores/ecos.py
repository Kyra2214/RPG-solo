"""Ecos de Mythralis (F5.2). Diária. Todo delta de importância 3+ nasce como `eco.<delta>` (em Estado.aplicar) no lugar de origem.
A cada `eco_atraso_dias` o Eco passa a lugares vizinhos por rota ABERTA do mesmo reino, com chance por camada
(publico > local > restrito > secreto). A certeza desce a cada `eco_passos_por_degrau` passos (confirmado, alegado, rumor).
Um delta `campo difusao` por passo: quem_sabe = lugares que souberam agora; a rota usada fica na causa (caminho registrado)."""

ORDEM = ['confirmado', 'alegado', 'rumor']


class Ecos:
    nome = 'ecos'; frequencia = 'diaria'

    def __init__(self, din):
        self.din = din

    def executar(self, ctx):
        e, E = ctx.estado, self.din.ecos
        if self.din.vazia or not E or not e.ecos_ativos:
            return []
        atraso = E.get('eco_atraso_dias', 12)
        devidos = [k for k in sorted(e.ecos_ativos) if ctx.dia_mundo == e.ent[k]['inicio'] + (e.ent[k]['difusao']['passo'] + 1) * atraso]
        if not devidos:
            return []
        adj = {}
        for k in sorted(k for k in e.ent if k.startswith('route.')):
            r = e.ent[k]
            if r['estado'] == 'aberta':
                adj.setdefault(r['de'], []).append((r['para'], k)); adj.setdefault(r['para'], []).append((r['de'], k))
        out = []
        for k in devidos:
            c = e.ent[k]; df = c['difusao']
            p = df['passo'] + 1
            pm = E.get('eco_prob_%s_pm' % c['camada'], 450)
            sabem, novos, via = set(df['lugares']), [], None
            for l in sorted(sabem):
                for viz, rota in sorted(adj.get(l, [])):
                    if viz not in sabem and viz not in novos and ctx.rng.randrange(1000) < pm:
                        novos.append(viz); via = via or rota
            base = ORDEM.index(df['certeza']) if df['certeza'] in ORDEM else len(ORDEM) - 1
            cert = ORDEM[min(len(ORDEM) - 1, base + p // max(1, E.get('eco_passos_por_degrau', 2)))]
            todos = sorted(sabem | set(novos))
            fronteira = any(v not in todos for l in todos for v, _ in adj.get(l, []))
            ativo = p < E.get('eco_max_passos', 8) and fronteira
            out.append(dict(tipo='campo', entidade=k, campo='difusao', estado_novo=dict(passo=p, lugares=todos, certeza=cert, ativo=ativo),
                            quem_sabe=novos or [c['reino']], certeza=cert,
                            causa=dict(ids=[k] + ([via] if via else []) + [c['origem_delta']], regra='difusao-de-eco')))
        return out
