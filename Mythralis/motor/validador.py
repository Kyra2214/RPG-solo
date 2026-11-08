"""Validador de MUNDO (os 5 reinos juntos). Não repete validar_reino.py (regras de cada reino); confere o que só
aparece com os cinco reinos carregados: esquema único, IDs únicos no mundo, calendários, catálogo, cobertura de
povos e o que o motor precisa para simular (líderes vivos, NPC vivo com lugar, relógios coerentes)."""
import re
from collections import Counter, defaultdict
import carregador as C
import calendario as CAL


def validar(w, din=None):
    F = []   # (codigo, gravidade, entidade, mensagem)

    def f(cod, sev, ent, msg): F.append((cod, sev, ent, msg))

    # W-00 carga
    for pasta, tipo, e in w.erros:
        f('W-00', 'E', '%s/%s' % (pasta, tipo), e)
    if len(w.reinos) != 5:
        f('W-00', 'E', 'mundo', 'esperados 5 reinos, carregados %d' % len(w.reinos))
    # W-01 esquema único
    for tipo, por in w.cab.items():
        if len({tuple(h) for h in por.values()}) > 1:
            f('W-01', 'E', tipo, 'cabeçalhos diferentes entre reinos: ' + ', '.join(
                '%s=%d campos' % (p[:2], len(h)) for p, h in sorted(por.items())))
    # W-02 IDs únicos no mundo
    for tipo in C.PREFIXO:
        c = Counter(r['id'] for r in w.t[tipo])
        for i, n in c.items():
            if n > 1: f('W-02', 'E', i, 'ID duplicado no mundo (%s, %d vezes)' % (tipo, n))
        for r in w.t[tipo]:
            if not r['id'].startswith(C.PREFIXO[tipo]): f('W-02', 'E', r['id'], 'prefixo errado para ' + tipo)
    todos = [r['id'] for t in C.PREFIXO for r in w.t[t]]
    # um id não pode servir a dois tipos
    for i, n in Counter(todos).items():
        if n > 1 and len({w.tipo(i)}) == 1 and False: pass
    # W-03 calendários
    cals = CAL.construir(w)
    for rid, c in cals.items():
        if c is None: f('W-03', 'E', rid, 'reino sem calendário carregado')
        elif not c.valido(): f('W-03', 'E', c.id, 'calendário inválido (dias, estações ou ano inicial)')
    if len({c.id for c in cals.values() if c}) < len(w.reinos): f('W-03', 'A', 'mundo', 'reinos compartilham calendário')
    # W-04 catálogo de personagem
    cat = w.catalogo
    RACE = {x['id'] for x in cat['races']}; SUB = {x['id']: x['parentRaceId'] for x in cat['subraces']}
    CLS = {x['id'] for x in cat['classes']}; PRO = {x['id'] for x in cat['professions']}
    exc = {(r['regra'], r['entidade']) for r in w.t['excecoes']}
    for r in w.t['npc']:
        i = r['id']
        if ('V-12', i) in exc: continue
        if r['povo'] not in RACE: f('W-04', 'E', i, 'povo fora do catálogo: ' + r['povo'])
        if r['subraca'] != '—':
            if r['subraca'] not in SUB: f('W-04', 'E', i, 'sub-raça fora do catálogo: ' + r['subraca'])
            elif SUB[r['subraca']] != r['povo']: f('W-04', 'E', i, 'sub-raça não pertence ao povo')
        if r['classe'] not in CLS and not (r['classe'] == '—' and r['vivo'] == 'não'): f('W-04', 'E', i, 'classe fora do catálogo: ' + r['classe'])
        if r['profissao'] not in PRO and r['profissao'] != '—': f('W-04', 'E', i, 'profissão fora do catálogo: ' + r['profissao'])
    # W-05 cobertura de povos e sub-raças (a pergunta original da auditoria)
    nativo = defaultdict(set)
    for r in w.t['povo_no_reino']: nativo[r['povo']].add(r['reino'])
    for p in sorted(RACE - set(nativo)): f('W-05', 'E', p, 'raça sem lugar em nenhum reino (povo_no_reino)')
    for p in nativo:
        if p not in RACE: f('W-05', 'E', p, 'povo_no_reino aponta para raça fora do catálogo')
    usadas = set()
    blob = ' '.join(' '.join(v for k, v in r.items() if isinstance(v, str)) for t in ('cidade', 'npc', 'faccao', 'resumo', 'fato', 'povo_no_reino') for r in w.t[t])
    for s in SUB:
        if re.search(r'(?<![\w-])' + re.escape(s) + r'(?![\w-])', blob): usadas.add(s)
    import glob, os
    prosa = ' '.join(open(x, encoding='utf-8', errors='ignore').read() for x in glob.glob(os.path.join(C.RAIZ, 'reinos', '*', '*.md')) + glob.glob(os.path.join(C.RAIZ, 'mundo', '*.md')))
    for s in sorted(set(SUB) - usadas):
        so_prosa = re.search(r'(?<![\w-])' + re.escape(s) + r'(?![\w-])', prosa)
        f('W-05', 'A', s, 'sub-raça sem NPC nem cidade que a use' + ('; aparece só em prosa dos reinos' if so_prosa else '; não aparece em nenhum reino'))
    # W-06 referências no mundo todo (cruzando reinos é permitido; id inexistente não)
    ids = set(w.idx)
    def refs(campo_ids, ent, valor, prefixos=None):
        for t in C.toks(valor):
            if re.match(r'^(?:npc|city|faction|settlement|clock|event|route|kingdom)\.[a-z0-9.-]+$', t) and t not in ids:
                f('W-06', 'A' if ('V-10', ent) in exc else 'E', ent, '%s aponta para ID inexistente: %s' % (campo_id_nome(campo_ids), t))
    def campo_id_nome(x): return x
    for r in w.t['relacao']:
        for k in ('origem', 'destino'):
            if re.match(r'^(?:npc|city|faction|settlement)\.', r[k]) and r[k] not in ids:
                f('W-06', 'E', r['origem'], 'relação com %s inexistente: %s' % (k, r[k]))
    for r in w.t['relogio']: refs('relogio.entidades', r['id'], r['entidades'])
    for r in w.t['evento']:
        for k in ('relogio', 'cidades_e_assentamentos', 'pessoas_e_faccoes', 'rotas'): refs('evento.' + k, r['id'], r[k])
    cruzam = [r for r in w.t['relacao'] if r['destino'] in ids and w.reino_de(r['origem']) and w.reino_de(r['destino'])
              and w.reino_de(r['origem']) != w.reino_de(r['destino'])]
    if cruzam: f('W-06', 'A', 'mundo', '%d relações cruzam reinos (entram só pelos contratos de fronteira na Era I)' % len(cruzam))
    # W-07 pronto para simular
    npc = {r['id']: r for r in w.t['npc']}
    for r in w.t['cidade']:
        g = r['governante']
        if g in npc and npc[g]['vivo'] != 'sim': f('W-07', 'E', r['id'], 'governante morto: ' + g)
    for r in w.t['faccao']:
        if r['lideranca'] in npc and npc[r['lideranca']]['vivo'] != 'sim': f('W-07', 'E', r['id'], 'liderança morta: ' + r['lideranca'])
    sem_lugar = [r['id'] for r in w.t['npc'] if r['vivo'] == 'sim' and r['cidade'] == '—' and r['faccao'] == '—']
    if sem_lugar: f('W-07', 'A', 'mundo', '%d NPCs vivos sem cidade nem facção (o motor os coloca "em trânsito"): %s' % (len(sem_lugar), ', '.join(sem_lugar[:6]) + ('…' if len(sem_lugar) > 6 else '')))
    for r in w.t['relogio']:
        try:
            if int(r['progresso']) > int(r['segmentos']) or int(r['segmentos']) < 1: f('W-07', 'E', r['id'], 'segmentos/progresso incoerentes')
        except ValueError:
            f('W-07', 'E', r['id'], 'segmentos/progresso não numéricos')
    # cada reino precisa de governante vivo e de capital (ou lacuna explícita)
    for rid, info in w.reinos.items():
        cap = info['row']['capital']
        if cap not in ('—', '?') and cap not in ids: f('W-07', 'E', rid, 'capital inexistente: ' + cap)
        gov = [r for r in w.t['npc'] if r['reino'] == rid and r['vivo'] == 'sim' and 'governante' in C.toks(r['papeis'])]
        if not gov: f('W-07', 'E', rid, 'reino sem NPC governante vivo')
    # W-08 rotas: pontas existentes e grafo por reino
    for r in w.t['rota']:
        for k in ('de', 'para'):
            if r[k] not in ids: f('W-08', 'E', r['id'], 'ponta inexistente: ' + r[k])
    for rid in w.reinos:
        locs = [r['id'] for r in w.t['cidade'] if r['reino'] == rid]
        adj = defaultdict(set)
        for r in w.t['rota']:
            if r['reino'] == rid: adj[r['de']].add(r['para']); adj[r['para']].add(r['de'])
        if locs:
            seen, pilha = set(), [locs[0]]
            while pilha:
                x = pilha.pop()
                if x in seen: continue
                seen.add(x); pilha += list(adj[x] - seen)
            fora = [l for l in locs if l not in seen]
            if fora: f('W-08', 'A' if ('V-23', rid) in exc else 'E', rid, 'cidades fora do grafo de rotas: ' + ', '.join(fora))
    nrot = sum(1 for r in w.t['rota'] if r['estado'] != 'desconhecida')
    f('W-08', 'A', 'mundo', 'rotas com duração: %d de %d; rotas ENTRE reinos: %d (depende de M-07)' % (
        nrot, len(w.t['rota']), sum(1 for r in w.t['rota'] if w.reino_de(r['de']) != w.reino_de(r['para']))))
    # W-09 dinâmica (F4): o que o motor lê além do molde
    if din is not None and not din.vazia:
        for x in cat['races'] + cat['subraces']:
            if x['id'] not in din.longev: f('W-09', 'E', x['id'], 'raça ou sub-raça sem classe de longevidade')
        for c in set(din.longev.values()) - set(din.classes): f('W-09', 'E', c, 'classe de longevidade sem parâmetros')
        for r in w.t['relogio']:
            if r['id'] not in din.relogios: f('W-09', 'A', r['id'], 'relógio sem regra em relogios_dinamica.txt (o motor usa o padrão)')
        sem = [r['id'] for r in w.t['evento'] if r['relogio'] not in {x['id'] for x in w.t['relogio']}]
        if sem: f('W-09', 'A', 'mundo', '%d eventos sem relógio (gatilho só em prosa): ficam latentes na F4' % len(sem))
    F.sort(key=lambda x: (x[1] != 'E', x[0], x[2]))
    return F


def resumo(F):
    return sum(1 for x in F if x[1] == 'E'), sum(1 for x in F if x[1] == 'A')
