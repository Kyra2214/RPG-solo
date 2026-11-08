"""Consultas obrigatórias (F6.2; PLANO_MOTOR §10): sobre a crônica de uma rodada, sem alterar nada.
  python3 motor/consultas.py <rodada> linha <entidade>        linha do tempo de uma entidade
  python3 motor/consultas.py <rodada> cadeia <delta>          cadeia causal de um delta (ou evento)
  python3 motor/consultas.py <rodada> titular <posto|alvo> <ano>   quem ocupa um cargo no fim do ano
  python3 motor/consultas.py <rodada> relogio <clock.id> <ano>     estado de um relógio no fim do ano
O ano é o ano do mundo (742 em diante)."""
import os, sys, re
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import carregador as C, calendario as CAL, cronica as CR, estado as ES, dinamica as DIN
from invariantes import PADRAO_DELTA


def fim_do_ano(ano):
    return (ano - CAL.ANO_BASE + 1) * CAL.DIAS_ANO - 1


def linha_do_tempo(deltas, entidade):
    """Deltas em que a entidade é o alvo, agente ou parte da causa, em ordem."""
    return [d for d in deltas if d['entidade'] == entidade or entidade in (d.get('agentes') or []) or entidade in d['causa']['ids']]


def cadeia_causal(deltas, delta_id):
    """Do delta até as raízes: devolve (deltas ancestrais em ordem causal, ids de molde ou estado que o enraízam)."""
    por = {d['id']: d for d in deltas}
    if delta_id not in por:
        raise KeyError(delta_id)
    ordem, raizes, vis, pend = [], [], set(), [delta_id]
    while pend:
        i = pend.pop(0)
        if i in vis:
            continue
        vis.add(i)
        if i in por:
            ordem.append(por[i])
            pend.extend(c for c in por[i]['causa']['ids'] if c != i)
        else:
            raizes.append(i)
    ordem.sort(key=lambda d: d['id'])
    return ordem, sorted(raizes)


def estado_em(world, deltas, ano, din=None):
    """Replay até o fim do ano: estado = molde + dinâmica + deltas daquela data para trás."""
    s = ES.Estado.do_molde(world, din or DIN.Dinamica(C.RAIZ))
    lim = fim_do_ano(ano)
    for d in deltas:
        if d['dia_mundo'] > lim:
            break
        s.aplicar(d)
    return s


def titular_em(world, deltas, alvo, ano, din=None):
    """Titular (e vacância) de um posto no fim do ano. `alvo` = id do posto ou da cidade, facção ou NPC que o tem."""
    s = estado_em(world, deltas, ano, din)
    k = alvo if alvo.startswith('posto.') else 'posto.' + alvo
    p = s.ent.get(k)
    return None if p is None else dict(posto=k, titular=p['titular'], vago_desde=p['vago_desde'], interino=p['interino'])


def relogio_em(world, deltas, relogio, ano, din=None):
    s = estado_em(world, deltas, ano, din)
    c = s.ent.get(relogio)
    return None if c is None else dict(relogio=relogio, progresso=c['progresso'], segmentos=c['segmentos'], estourado=c['estourado'])


def _fmt(d):
    return 'ano %d | %s | %s %s %s | causa %s' % (CAL.ano_base(d['dia_mundo']), d['id'], d['tipo'], d['entidade'],
                                                  d['campo'] or '', ','.join(d['causa']['ids']) + ' (' + d['causa']['regra'] + ')')


def main(argv=None):
    a = argv or sys.argv[1:]
    cr = CR.Cronica.abrir(os.path.join(a[0], 'cronica.jsonl'))
    w = C.carregar()
    if a[1] == 'linha':
        print('\n'.join(_fmt(d) for d in linha_do_tempo(cr.deltas, a[2])))
    elif a[1] == 'cadeia':
        o, r = cadeia_causal(cr.deltas, a[2])
        print('\n'.join(_fmt(d) for d in o)); print('raízes: ' + ', '.join(r))
    elif a[1] == 'titular':
        print(titular_em(w, cr.deltas, a[2], int(a[3])))
    elif a[1] == 'relogio':
        print(relogio_em(w, cr.deltas, a[2], int(a[3])))
    cr.fechar()


if __name__ == '__main__':
    main()
