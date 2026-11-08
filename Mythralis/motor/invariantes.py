"""Invariantes de continuidade. F3: I-01, I-03, I-05, I-06, I-07, I-13, I-14 e I-21 rodam dentro da rodada;
I-02, I-19 e I-22 comparam rodadas (ver `comparar_rodadas` e motor/testes/test_f3.py).
F4: I-04 e I-15 conferem o estado ao fim de cada bloco (`checar_estado`); I-11 confere cada delta de par de facções.
I-08 e I-09 (F5.3) e I-10 (F5.4) conferem cada delta; I-12, I-16 a I-18 e I-20 (F6.2) conferem cada bloco (`checar_bloco`)."""
import re
from cronica import CERTEZAS

IMPLEMENTADOS = {
    'I-01': ('E', 'molde intacto (hash dos dados/, mundo/ e catálogo; hash da dinâmica)'),
    'I-02': ('E', 'determinismo (mesma semente, mesma crônica)'),
    'I-03': ('E', 'morto não age'),
    'I-04': ('E', 'cargo ocupado ou vacância válida (prazo e interino)'),
    'I-05': ('E', 'referências válidas'),
    'I-06': ('E', 'toda mudança tem causa'),
    'I-07': ('E', 'origem marcada (nada novo é ORIGINAL)'),
    'I-08': ('E', 'mistério sem evidência não se resolve (exige agente, evidência listada e verdade não vedada)'),
    'I-09': ('E', 'camada respeitada (Eco restrito ou secreto só avança por rota registrada)'),
    'I-10': ('E', 'contrato de fronteira na Era I (selado): sem rota nova, sem Mistério D resolvido, sem agente de outro reino, sem troca de dono de Torvelume)'),
    'I-12': ('E', 'calendário coerente (a mesma data do mundo dá o mesmo dia do ano em todos os reinos)'),
    'I-16': ('A', 'mundo não morre (todo reino mantém NPCs vivos)'),
    'I-17': ('A', 'mundo não congela (mínimo de eventos importantes por década e reino)'),
    'I-18': ('A', 'sem explosão (teto de eventos importantes por ano e reino)'),
    'I-20': ('E', 'legibilidade (toda causa remonta ao molde ou a um delta anterior)'),
    'I-11': ('E', 'aliado e rival ao mesmo tempo é proibido (par só muda por transição permitida)'),
    'I-13': ('E', 'ordem causal'),
    'I-14': ('E', 'linhagem de NPC simulado'),
    'I-15': ('A', 'NPC simulado vivo sem lugar'),
    'I-19': ('E', 'retomada coerente'),
    'I-21': ('E', 'notificação completa (importância 3+)'),
    'I-22': ('E', 'passo neutro'),
    'I-23': ('E', 'transições permitidas da relação entre reinos vizinhos'),
    'I-24': ('E', 'guerra válida e queda de coroa irreversível durante guerra'),
    'I-25': ('E', 'mudança de era/regime com gatilho e causa correspondentes'),
}
PENDENTES = {}   # F6.2: nenhum invariante pendente
TRANSICOES_PAR = {   # relação do par de facções -> destinos permitidos (I-11)
    'aliança': {'rompida'}, 'rompida': {'rivalidade'}, 'rivalidade': {'disputa_aberta', 'tratado'},
    'disputa_aberta': {'rivalidade'}, 'tratado': {'rivalidade'}, 'ambigua': {'rivalidade', 'tratado'},
}
PADRAO_DELTA = re.compile(r'^d\d{6}$')
ORIGENS_NPC = ('nascimento', 'chegada', 'nomeacao')
MECANICOS = ('campo', 'criacao', 'era')


def checar_delta(d, estado, ultimo_dia, ids_cronica, world, catalogo, dinamica=None):
    """Devolve lista de (invariante, mensagem). `estado` é o estado ANTES de aplicar o delta."""
    v = []
    for k in ('tipo', 'entidade'):
        if not isinstance(d.get(k), str) or not d[k]:
            v.append(('F-00', 'delta sem %s' % k))
    if d.get('certeza') not in CERTEZAS:
        v.append(('F-00', 'certeza fora do vocabulário: %r' % d.get('certeza')))
    if d.get('tipo') == 'campo' and not d.get('campo'):
        v.append(('F-00', 'delta de campo sem campo'))
    # I-03 morto não age
    for a in d.get('agentes') or []:
        if estado.morto(a):
            v.append(('I-03', 'agente morto: ' + a))
    # I-06 causa
    c = d.get('causa')
    if not (isinstance(c, dict) and c.get('ids') and c.get('regra')):
        v.append(('I-06', 'delta sem causa (ids e regra)'))
        c = {'ids': []}
    # I-07 origem
    if d.get('origem') != 'SIMULADO':
        v.append(('I-07', 'origem diferente de SIMULADO: %r' % d.get('origem')))
    # I-13 ordem causal
    if d.get('dia_mundo', 0) < ultimo_dia:
        v.append(('I-13', 'dia_mundo %d anterior ao último registrado %d' % (d['dia_mundo'], ultimo_dia)))
    for i in c['ids']:
        if PADRAO_DELTA.match(i) and i not in ids_cronica:
            v.append(('I-13', 'causa aponta para delta que não veio antes: ' + i))
    # I-05 referências
    def existe(i):
        return i in estado.ent or i in world.idx or i in ('mundo', 'mundo.mythralis') or i in ids_cronica
    if d.get('tipo') != 'criacao' and d.get('entidade') and not existe(d['entidade']):
        v.append(('I-05', 'entidade inexistente: ' + d['entidade']))
    for i in c['ids'] + list(d.get('agentes') or []):
        if not existe(i):
            v.append(('I-05', 'referência inexistente: ' + i))
    # I-08 mistério
    ns = d.get('estado_novo')
    resolve = (d.get('tipo') == 'tentativa_de_resolver_misterio' and isinstance(ns, dict) and ns.get('resolvido')) or \
        (d.get('tipo') == 'campo' and d.get('campo') == 'estado' and str(d.get('entidade')).startswith('mist.') and ns != 'aberto')
    if resolve:
        row = world.get(d['entidade']) or {}
        ev = ns.get('evidencia', '') if isinstance(ns, dict) else ''
        vd = ns.get('verdade', '') if isinstance(ns, dict) else ''
        if not d.get('agentes'):
            v.append(('I-08', 'mistério resolvido sem agente investigador: ' + d['entidade']))
        if not ev or ev.lower() not in row.get('evidencia_para_resolver', '').lower():
            v.append(('I-08', 'mistério resolvido sem a evidência listada no molde: ' + d['entidade']))
        if vd and vd.lower() in row.get('a_verdade_nao_pode_ser', '').lower():
            v.append(('I-08', 'verdade vedada pelo molde: ' + d['entidade']))
    # I-09 Eco restrito ou secreto só avança por rota registrada
    if d.get('tipo') == 'campo' and d.get('campo') == 'difusao':
        eco = estado.ent.get(d['entidade']) or {}
        if eco.get('camada') in ('restrito', 'secreto') and ns and len(ns['lugares']) > len(eco['difusao']['lugares']) \
                and not any(str(i).startswith('route.') for i in c['ids']):
            v.append(('I-09', 'Eco ' + eco['camada'] + ' avançou sem rota registrada: ' + d['entidade']))
    # I-10 contrato de fronteira (só no regime selado)
    if estado.mundo.get('regime') == 'selado':
        ent = estado.ent.get(d.get('entidade')) or {}
        rk = ent.get('reino')
        if d.get('tipo') == 'criacao' and str(d.get('entidade')).startswith('route.'):
            v.append(('I-10', 'rota nova na Era I: ' + d['entidade']))
        if d.get('entidade') == 'mist.d-mina-selada' and resolve:
            v.append(('I-10', 'Mistério D resolvido na Era I'))
        for a in d.get('agentes') or []:
            ra = (estado.ent.get(a) or {}).get('reino')
            if rk and ra and ra != rk:
                v.append(('I-10', 'agente de outro reino (%s) em %s' % (a, d['entidade'])))
        if d.get('tipo') == 'campo' and d.get('entidade') in ('city.torvelume', 'posto.city.torvelume') and \
                d.get('campo') in ('governante', 'titular'):
            for x in str(ns).split(';'):
                rx = (estado.ent.get(x) or {}).get('reino')
                if rx and rk and rx != rk:
                    v.append(('I-10', 'Torvelume trocou de dono: ' + x))
    # I-11 par de facções
    if d.get('tipo') == 'campo' and d.get('campo') == 'relacao' and str(d.get('entidade')).startswith('par.'):
        if d.get('estado_novo') not in TRANSICOES_PAR.get(d.get('estado_anterior'), ()):
            v.append(('I-11', 'transição de par não permitida: %s -> %s' % (d.get('estado_anterior'), d.get('estado_novo'))))
    # I-14 linhagem
    if d.get('tipo') == 'criacao' and d['entidade'].startswith('npc.'):
        n = d.get('estado_novo') or {}
        racas = {x['id'] for x in catalogo['races']}
        sub = {x['id']: x['parentRaceId'] for x in catalogo['subraces']}
        if n.get('origem_npc') not in ORIGENS_NPC:
            v.append(('I-14', 'NPC simulado sem origem (nascimento, chegada ou nomeação)'))
        if n.get('povo') not in racas:
            v.append(('I-14', 'povo fora do catálogo: %r' % n.get('povo')))
        s = n.get('subraca', '—')
        if s != '—' and sub.get(s) != n.get('povo'):
            v.append(('I-14', 'sub-raça inválida para o povo: %r' % s))
    # I-23: relações de fronteira seguem apenas transições produzidas pelos motores F7.
    eid = d.get('entidade', '')
    if d.get('tipo') == 'campo' and d.get('campo') == 'relacao' and eid.startswith('front.'):
        permitidas = {
            'neutra': {'neutra', 'tratado', 'ruptura'},
            'tratado': {'tratado', 'neutra', 'ruptura'},
            'ruptura': {'ruptura', 'neutra', 'tratado', 'guerra'},
            'guerra': {'guerra', 'neutra', 'tratado'},
        }
        if d.get('estado_novo') not in permitidas.get(d.get('estado_anterior'), set()):
            v.append(('I-23', 'transição de fronteira não permitida: %s -> %s' % (
                d.get('estado_anterior'), d.get('estado_novo'))))

    def guerra_valida(par_id):
        par = estado.ent.get(par_id) or {}
        reinos_par = par.get('reinos') or []
        coroas = ['crown.' + r[8:] for r in reinos_par if str(r).startswith('kingdom.')]
        minimo = dinamica.guerra.get('guerra_tensao_min', 0) if dinamica else 0
        return (len(coroas) == 2 and par.get('relacao') == 'ruptura'
                and par.get('tensao', -1) >= minimo
                and estado.mundo.get('regime') != 'selado'
                and all(c in estado.ent and estado.ent[c].get('estado') == 'firme' for c in coroas))

    # I-24: uma guerra começa só a partir de ruptura elegível; coroa só cai numa guerra em curso.
    e_guerra = eid.startswith('front.') and guerra_valida(eid)
    if d.get('tipo') == 'guerra' or (d.get('tipo') == 'campo' and d.get('campo') == 'relacao'
                                      and d.get('estado_novo') == 'guerra' and eid.startswith('front.')):
        if not e_guerra or c.get('regra') != 'guerra-de-fronteira':
            v.append(('I-24', 'guerra sem ruptura elegível, coroas firmes ou causa de fronteira'))
    queda = d.get('tipo') == 'queda_de_coroa' or (d.get('tipo') == 'campo' and eid.startswith('crown.')
                                                   and d.get('campo') == 'estado' and d.get('estado_novo') == 'caida')
    if queda:
        coroa = estado.ent.get(eid) or {}
        reino = coroa.get('reino')
        pares_em_guerra = [k for k, p in estado.ent.items() if k.startswith('front.')
                           and p.get('relacao') == 'guerra' and reino in p.get('reinos', [])]
        if (not eid.startswith('crown.') or coroa.get('estado') != 'firme' or not pares_em_guerra
                or c.get('regra') != 'queda-de-coroa-em-guerra'
                or not any(k in (c.get('ids') or []) for k in pares_em_guerra)):
            v.append(('I-24', 'queda de coroa sem guerra ativa e causa correspondente'))
    if d.get('tipo') == 'campo' and eid.startswith('crown.') and d.get('campo') == 'estado' \
            and estado.ent.get(eid, {}).get('estado') == 'caida' and d.get('estado_novo') != 'caida':
        v.append(('I-24', 'coroa caída não pode ser restaurada pela simulação'))

    # I-25: toda era posterior à inicial corresponde a gatilho F7 já refletido no estado.
    if d.get('tipo') == 'era':
        novo = d.get('estado_novo') or {}
        causa = d.get('causa') or {}
        regra = causa.get('regra')
        antigo = estado.mundo.get('regime')
        proxima = 'era.%03d' % (len(estado.eras) + 1)
        ok = novo.get('era') == proxima
        if antigo is None:
            ok = ok and novo.get('regime') == 'selado' and regra == 'inicio-da-rodada'
        elif antigo == 'selado' and novo.get('regime') == 'poroso':
            ok = ok and regra in ('primeiro-incidente-de-fronteira', 'limite-de-anos-sem-gatilho')
            if regra == 'primeiro-incidente-de-fronteira':
                ok = ok and any(str(x).startswith('front.') for x in (causa.get('ids') or []))
        elif antigo == 'poroso' and novo.get('regime') == 'aberto':
            ok = ok and regra == 'primeira-guerra' and any(
                str(x).startswith('front.') and (estado.ent.get(x) or {}).get('relacao') == 'guerra'
                for x in (causa.get('ids') or []))
        elif antigo == 'aberto' and novo.get('regime') == 'aberto':
            ok = ok and regra == 'queda-de-coroa-em-guerra' and any(
                str(x).startswith('crown.') and (estado.ent.get(x) or {}).get('estado') == 'caida'
                for x in (causa.get('ids') or []))
        else:
            ok = False
        if not ok:
            v.append(('I-25', 'mudança de era/regime sem gatilho F7 válido: %r -> %r (%s)' % (
                antigo, novo.get('regime'), regra)))
    return v


def checar_estado(estado, dia, motores=('sucessao',)):
    """Conferência do estado ao fim de um bloco. Devolve lista de (invariante, entidade, mensagem).
    I-04 só vale quando o motor de sucessão está na rodada (sem ele ninguém repõe o cargo)."""
    v = []
    for k in sorted(estado.ent):
        e = estado.ent[k]
        if k.startswith('posto.') and 'sucessao' in motores:
            tit = [x for x in e['titular'].split(';') if x.startswith('npc.')]
            if tit:
                for x in tit:
                    if not estado.vivo(x):
                        v.append(('I-04', k, 'titular morto sem vacância registrada: ' + x))
            elif e['vago_desde'] is None or e['prazo'] is None or not estado.vivo(e['interino']):
                v.append(('I-04', k, 'vacância sem prazo ou sem interino vivo'))
            elif dia > e['prazo'] + 366:
                v.append(('I-04', k, 'vacância além do prazo (%d dias)' % (dia - e['prazo'])))
        elif k.startswith('npc.') and e.get('origem_npc') and e.get('vivo') is True and e.get('cidade') in ('—', '?', None):
            v.append(('I-15', k, 'NPC simulado vivo sem cidade'))
    return v


def comparar_rodadas(dir_a, dir_b):
    """I-02 / I-19 / I-22: compara duas rodadas pelo hash da crônica, das notificações e do estado final."""
    import json, os
    def lido(d):
        with open(os.path.join(d, 'progresso.json'), encoding='utf-8') as f:
            p = json.load(f)
        return p['hash_cronica'], p['hash_notificacoes'], p['hash_estado'], p['proximo_dia']
    a, b = lido(dir_a), lido(dir_b)
    nomes = ('crônica', 'notificações', 'estado', 'dia final')
    return [n for n, x, y in zip(nomes, a, b) if x != y]   # vazio = rodadas idênticas


PADRAO_ID = PADRAO_DELTA


def checar_bloco(deltas, estado, dia_fim, parcial, cals, inv, reinos, world, enraizados):
    """F6.2: I-12, I-16, I-17, I-18 e I-20 ao fim de um bloco. `reinos` = relatorio.Reinos; `enraizados` = ids de delta já
    com causa ligada ao molde (conjunto mantido pelo simulador; id de delta fora do bloco e fora do conjunto vale como conferido)."""
    v = []
    # I-12
    doys = {r: c.data(dia_fim)['dia_do_ano'] for r, c in cals.items() if c and c.valido()}
    if len(set(doys.values())) > 1:
        v.append(('I-12', 'mundo', 'dia do ano diferente entre reinos no dia %d: %s' % (dia_fim, doys)))
    # I-16
    for r in sorted(world.reinos):
        if not any(e.get('vivo') is True and e.get('reino') == r for k, e in estado.ent.items() if k.startswith('npc.')):
            v.append(('I-16', r, 'reino sem NPC vivo'))
    # I-20
    ids = {d['id']: d for d in deltas}
    for d in deltas:
        pend, ok = [d['id']], False
        vis = set()
        while pend and not ok:
            i = pend.pop()
            if i in vis:
                continue
            vis.add(i)
            x = ids.get(i)
            if x is None:
                ok = True; continue
            if i in enraizados:
                ok = True; continue
            for c in x['causa']['ids']:
                if c == i:
                    continue
                if not PADRAO_ID.match(c):
                    ok = True; break
                pend.append(c)
        if ok:
            enraizados.add(d['id'])
        else:
            v.append(('I-20', d['id'], 'causa não remonta ao molde nem a um delta anterior'))
    # I-17 e I-18 (alertas)
    por_ano = {}
    for d in deltas:
        if d.get('importancia', 0) >= 3 and d['entidade'] != 'mundo':
            r = reinos.de(d['entidade'])
            if r in world.reinos:
                k = (r, int(d['dia_mundo'] // 365))
                por_ano[k] = por_ano.get(k, 0) + 1
    teto = inv.get('inv_max_importantes_ano')
    if teto:
        for (r, a), n in sorted(por_ano.items()):
            if n > teto:
                v.append(('I-18', r, '%d eventos importantes no ano %d (teto %d)' % (n, a, teto)))
    minimo = inv.get('inv_min_importantes_decada')
    if minimo and not parcial and deltas:
        anos = max(1, (deltas[-1]['dia_mundo'] - deltas[0]['dia_mundo']) // 365 + 1)
        for r in sorted(world.reinos):
            n = sum(c for (rr, a), c in por_ano.items() if rr == r)
            if n * 10 < minimo * anos:
                v.append(('I-17', r, 'só %d eventos importantes em %d anos (mínimo %d por década)' % (n, anos, minimo)))
    return v
