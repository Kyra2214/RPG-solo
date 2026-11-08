"""Modo lote (F6.3): várias sementes sobre o mesmo molde, com estatística entre rodadas.
  python3 motor/simulador.py --lote 20 --anos 500 --passo 10 [--semente 1] [--saida nome]
  python3 motor/lote.py saidas/<lote>      # regenera lote.md e lote.csv de um lote pronto
Cada rodada fica em saidas/<lote>/rodada-s<semente>/ (com relatório e métricas próprios). O lote grava `lote.csv`
(uma linha por semente) e `lote.md` (estatística). Tudo é derivado dos deltas; não entra nos hashes das rodadas.
"""
import os, sys, json, statistics
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import carregador as C, calendario as CAL, cronica as CR, relatorio as REL

INCIDENTES = ('incidente_fronteira',)
COLS = ('semente', 'resultado', 'deltas', 'fim_era_1', 'primeiro_incidente', 'primeira_guerra', 'reinos_caidos',
        'misterios_resolvidos', 'relogios_estourados', 'relogios_1o_seculo', 'min_importantes_decada', 'max_importantes_decada')


def _ano(d, ano0):
    """Ano desde o início da rodada (as metas do plano, seção 14, são relativas)."""
    return CAL.ano_base(d['dia_mundo']) - ano0 if d else ''


def estatisticas(world, deltas, cab):
    R = REL.Reinos(world, deltas); por_id = {d['id']: d for d in deltas}
    ano0 = cab.get('ano_base', CAL.ANO_BASE); ult = max((d['dia_mundo'] for d in deltas), default=0)
    anoN = CAL.ano_base(ult)
    era2 = next((d for d in deltas if d['tipo'] == 'era' and d['id'] != deltas[0]['id']), None)
    inc = next((d for d in deltas if d['tipo'] in INCIDENTES), None)
    gue = next((d for d in deltas if d['tipo'] == 'guerra'), None)
    caidos = sorted({R.de(d['entidade']) for d in deltas if d['tipo'] == 'queda_de_coroa'} - {'—'})
    resolvidos = sum(1 for d in deltas if d['tipo'] == 'tentativa_de_resolver_misterio'
                     and isinstance(d['estado_novo'], dict) and d['estado_novo'].get('resolvido'))
    est = {}
    for d in deltas:
        if d['tipo'] == 'campo' and d.get('campo') == 'estourado' and d['estado_novo'] is True and str(d['entidade']).startswith('clock.'):
            est.setdefault(d['entidade'], CAL.ano_base(d['dia_mundo']))
    por_dec = {}
    for d in deltas:
        if d.get('importancia', 0) >= 3 and d['entidade'] != 'mundo':
            r = R.de(d['entidade'])
            if r != '—':
                k = (r, (CAL.ano_base(d['dia_mundo']) - ano0) // 10); por_dec[k] = por_dec.get(k, 0) + 1
    reinos = sorted({c['reino'] for c in world.t['cidade']})
    decs = [k for k in range((anoN - ano0 + 1) // 10)]
    vals = [por_dec.get((r, k), 0) for r in reinos for k in decs]
    return dict(fim_era_1=_ano(era2, ano0), primeiro_incidente=_ano(inc, ano0), primeira_guerra=_ano(gue, ano0), reinos_caidos=caidos,
                misterios_resolvidos=resolvidos, estourados=est,
                relogios_1o_seculo=sum(1 for a in est.values() if a < ano0 + 100),
                min_dec=min(vals) if vals else '', max_dec=max(vals) if vals else '', deltas=len(deltas))


def _resumo(v):
    v = [x for x in v if x != '']
    return 'nenhuma' if not v else 'em %d: min %s, mediana %s, máx %s' % (len(v), min(v), statistics.median(v), max(v))


def gerar(pasta, world, linhas, total_relogios):
    with open(os.path.join(pasta, 'lote.csv'), 'w', encoding='utf-8', newline='\n') as f:
        f.write('|'.join(COLS) + '\n')
        for L in linhas:
            f.write('|'.join(str(x) for x in (L['semente'], L['resultado'], L['deltas'], L['fim_era_1'], L['primeiro_incidente'],
                    L['primeira_guerra'], ','.join(L['reinos_caidos']) or '—', L['misterios_resolvidos'], len(L['estourados']),
                    L['relogios_1o_seculo'], L['min_dec'], L['max_dec'])) + '\n')
    n = len(linhas); ok = [L for L in linhas if L['resultado'] != 'FALHA']
    M = ['# Relatório do lote', '', '%d sementes (%s); %d sem FALHA. Derivado dos deltas de cada rodada.' % (
        n, ', '.join(str(L['semente']) for L in linhas), len(ok)), '',
        '| métrica | resultado (n = sementes em que ocorreu) |', '|---|---|',
        '| fim da Era I (anos desde o início) | %s |' % _resumo([L['fim_era_1'] for L in ok]),
        '| primeiro incidente de fronteira (anos desde o início) | %s |' % _resumo([L['primeiro_incidente'] for L in ok]),
        '| primeira guerra entre reinos (anos desde o início) | %s |' % _resumo([L['primeira_guerra'] for L in ok]),
        '| mistérios resolvidos | total %d |' % sum(L['misterios_resolvidos'] for L in ok),
        '| relógios estourados no 1º século | %s de %d |' % (_resumo([L['relogios_1o_seculo'] for L in ok]), total_relogios),
        '| menos deltas 3+ de um reino em uma década (mundo morto) | %s |' % _resumo([L['min_dec'] for L in ok]),
        '| mais deltas 3+ de um reino em uma década (caos) | %s |' % _resumo([L['max_dec'] for L in ok]), '',
        '## Reinos caídos por semente', '']
    cont = {}
    for L in ok:
        for r in L['reinos_caidos']: cont[r] = cont.get(r, 0) + 1
    reinos = sorted({c['reino'] for c in world.t['cidade']})
    M += ['| reino | sementes em que caiu |', '|---|---|'] + ['| %s | %d de %d |' % (r, cont.get(r, 0), len(ok)) for r in reinos]
    M += ['', 'Desequilíbrio: reino que cai em todas as sementes (com 2 ou mais sementes): %s.' % (
        ', '.join(r for r in reinos if len(ok) > 1 and cont.get(r, 0) == len(ok)) or 'nenhum'), '',
        '## Relógios por frequência de estouro', '']
    ids = sorted(r['id'] for r in world.t['relogio'])
    conta = {i: sum(1 for L in ok if i in L['estourados']) for i in ids}
    nunca = [i for i in ids if conta[i] == 0]; sempre = [i for i in ids if ok and conta[i] == len(ok)]
    M += ['- nunca estouram em nenhuma semente (%d): %s' % (len(nunca), ', '.join(nunca) or '—'),
          '- estouram em todas as sementes (%d): %s' % (len(sempre), ', '.join(sempre) or '—'), '',
          '## Por semente', '', '| ' + ' | '.join(COLS) + ' |', '|' + '---|' * len(COLS)]
    for L in linhas:
        M.append('| ' + ' | '.join(str(x) for x in (L['semente'], L['resultado'], L['deltas'], L['fim_era_1'] or '—',
                 L['primeiro_incidente'] or '—', L['primeira_guerra'] or '—', ','.join(L['reinos_caidos']) or '—',
                 L['misterios_resolvidos'], len(L['estourados']), L['relogios_1o_seculo'], L['min_dec'], L['max_dec'])) + ' |')
    with open(os.path.join(pasta, 'lote.md'), 'w', encoding='utf-8', newline='\n') as f:
        f.write('\n'.join(M) + '\n')


def rodar_lote(n, anos, passo, semente_ini, pasta, raiz=C.RAIZ):
    import simulador as S, motores
    world = C.carregar(raiz); os.makedirs(pasta, exist_ok=True); linhas = []
    for s in range(semente_ini, semente_ini + int(n)):
        sim = S.Simulador(os.path.join(pasta, 'rodada-s%d' % s), s, passo, motores.criar(raiz), raiz, world)
        sim.iniciar(); r = sim.rodar(anos)
        cr = CR.Cronica.abrir(os.path.join(sim.saida, 'cronica.jsonl'))
        L = estatisticas(world, cr.deltas, cr.cabecalho); cr.fechar()
        L.update(semente=s, resultado=r['resultado']); linhas.append(L)
    gerar(pasta, world, linhas, len(world.t['relogio']))
    return linhas


def regenerar(pasta):
    world = C.carregar(); linhas = []
    for sub in sorted(os.listdir(pasta), key=lambda x: int(x.split('-s')[1]) if x.startswith('rodada-s') else 0):
        if not sub.startswith('rodada-s'): continue
        cr = CR.Cronica.abrir(os.path.join(pasta, sub, 'cronica.jsonl'))
        with open(os.path.join(pasta, sub, 'progresso.json'), encoding='utf-8') as f:
            res = json.load(f)['resultado']
        L = estatisticas(world, cr.deltas, cr.cabecalho); cr.fechar()
        L.update(semente=cr.cabecalho['semente'], resultado=res); linhas.append(L)
    gerar(pasta, world, linhas, len(world.t['relogio']))


if __name__ == '__main__':
    regenerar(sys.argv[1])
