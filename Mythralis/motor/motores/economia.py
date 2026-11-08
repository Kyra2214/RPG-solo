"""Economia e rotas (F5.1). Sazonal. Só dentro dos reinos (Era I).
Rotas: aberta <-> bloqueada por estação (mais no inverno; rota incerta bloqueia mais). Produtores: quebra e recuperação de safra.
Escassez (0 a 3) = déficit atual MENOS o déficit estrutural (mesma conta com todas as rotas abertas e produção cheia):
o déficit que o molde já traz (sem produtor alcançável) não é crise; só o que piora por rota fechada ou safra ruim conta.
Oferta = produção local + recebida por rotas, até `econ_alcance_passos` passos.
Escassez máxima por `econ_revolta_estacoes` estações seguidas vira revolta e zera o contador (homeostase)."""
import calendario as CAL


class Economia:
    nome = 'economia'; frequencia = 'sazonal'

    def __init__(self, din):
        self.din = din

    def executar(self, ctx):
        E, e, out = self.din.econ, ctx.estado, []
        if self.din.vazia or not E:
            return []
        inverno = CAL.estacao_idx(ctx.dia_do_ano) == 0
        rotas = {}
        for k in sorted(k for k in e.ent if k.startswith('route.')):
            r = e.ent[k]
            atual, novo, q = r['estado'], r['estado'], [r['reino']]
            x = ctx.rng.randrange(1000)
            if atual == 'aberta':
                pm = E.get('rota_bloqueio_inverno_pm', 150) if inverno else E.get('rota_bloqueio_pm', 40)
                if r['evidencia'] != 'documentada':
                    pm += E.get('rota_bloqueio_incerta_pm', 30)
                if x < pm:
                    novo = 'bloqueada'
                    out.append(dict(tipo='bloqueio_de_rota', entidade=k, estado_novo=dict(de=atual, para=novo), quem_sabe=q,
                                    causa=dict(ids=[k, r['de'], r['para']], regra='estacao-bloqueio-rota')))
            elif x < E.get('rota_reabertura_pm', 450):
                novo = 'aberta'
                out.append(dict(tipo='rota_reaberta', entidade=k, estado_novo=dict(de=atual, para=novo), quem_sabe=q,
                                causa=dict(ids=[k, r['de'], r['para']], regra='estacao-reabertura-rota')))
            if novo != atual:
                out.append(dict(tipo='campo', entidade=k, campo='estado', estado_novo=novo, quem_sabe=q,
                                causa=dict(ids=[k, r['de'], r['para']], regra='estacao-rota')))
            rotas[k] = (r, novo)
        adj, adj0 = {}, {}
        for r, est in rotas.values():
            adj0.setdefault(r['de'], set()).add(r['para']); adj0.setdefault(r['para'], set()).add(r['de'])
            if est == 'aberta':
                adj.setdefault(r['de'], set()).add(r['para']); adj.setdefault(r['para'], set()).add(r['de'])
        econ = sorted(k for k in e.ent if k.startswith('econ.'))
        prod = {}
        for k in econ:
            c = e.ent[k]
            if c['papel'] != 'produtor':
                continue
            q, pa = [c['reino']], c['producao_atual']
            x = ctx.rng.randrange(1000)
            if pa == c['producao'] and pa > 0 and x < E.get('econ_quebra_safra_pm', 80):
                pa -= 1; regra = 'quebra-de-safra'
            elif pa < c['producao'] and x < E.get('econ_recuperacao_pm', 500):
                pa += 1; regra = 'recuperacao-de-safra'
            else:
                regra = None
            if regra:
                out.append(dict(tipo='campo', entidade=k, campo='producao_atual', estado_novo=pa, quem_sabe=q,
                                causa=dict(ids=[k, c['localidade']], regra=regra)))
            prod.setdefault((c['reino'], c['recurso']), []).append((c['localidade'], pa))
        alcance, cache = E.get('econ_alcance_passos', 3), {}

        def alcancados(loc, g):
            if (loc, id(g)) not in cache:
                vis, fila = {loc}, [loc]
                for _ in range(alcance):
                    fila = [y for x in fila for y in sorted(g.get(x, ())) if y not in vis]
                    vis.update(fila)
                cache[(loc, id(g))] = vis
            return cache[(loc, id(g))]
        for k in econ:
            c = e.ent[k]
            if c['demanda'] <= 0:
                continue
            loc, ps = c['localidade'], prod.get((c['reino'], c['recurso']), [])
            propria = sum(p for l, p in ps if l == loc)
            viz = alcancados(loc, adj)
            recebida = min(E.get('econ_import_max', 2), sum(p for l, p in ps if l != loc and l in viz))
            pcheia = sum(e.ent['econ.%s@%s' % (c['recurso'], l)]['producao'] for l in {c['localidade']} if 'econ.%s@%s' % (c['recurso'], l) in e.ent and e.ent['econ.%s@%s' % (c['recurso'], l)]['papel'] == 'produtor')
            vz0 = alcancados(loc, adj0)
            rcheia = min(E.get('econ_import_max', 2), sum(e.ent[x]['producao'] for x in econ if e.ent[x]['recurso'] == c['recurso'] and e.ent[x]['reino'] == c['reino']
                                                           and e.ent[x]['papel'] == 'produtor' and e.ent[x]['localidade'] != loc and e.ent[x]['localidade'] in vz0))
            esc = max(0, min(E.get('econ_escassez_max', 3), (pcheia + rcheia) - (propria + recebida)))
            q, cr = [c['reino']], c['estacoes_criticas']
            ids = [k, loc]
            if esc != c['escassez']:
                out.append(dict(tipo='campo', entidade=k, campo='escassez', estado_novo=esc, quem_sabe=q,
                                causa=dict(ids=ids, regra='balanco-sazonal')))
            ncr = cr + 1 if esc >= E.get('econ_escassez_max', 3) else 0
            if ncr >= E.get('econ_revolta_estacoes', 4):
                gov = e.ent.get(loc, {}).get('governante', '')
                out.append(dict(tipo='revolta', entidade=loc, quem_sabe=q, estado_novo=dict(recurso=c['recurso'], escassez=esc),
                                agentes=[x for x in gov.split(';') if x and e.vivo(x)],
                                causa=dict(ids=ids, regra='escassez-prolongada')))
                ncr = 0
            if ncr != cr:
                out.append(dict(tipo='campo', entidade=k, campo='estacoes_criticas', estado_novo=ncr, quem_sabe=q,
                                causa=dict(ids=ids, regra='escassez-prolongada')))
        return out
