"""Nomes de NPCs simulados, pelo padrão de M-10 §1 (nome curto de 1 a 3 sílabas + epíteto com hífen).
ASSUMINDO: não há lista de nomes em M-10 (só regras); as sílabas abaixo são o molde mínimo do gerador e os
epítetos são recombinados dos já usados no molde. Nunca repete nome completo nem primeiro nome do molde (M-10 §2.1)."""
SILABAS = ['ba', 'ce', 'da', 'ei', 'fi', 'ga', 'ha', 'il', 'ju', 'ka', 'le', 'ma', 'ne', 'or', 'pa', 're', 'sa', 'ta', 'ul', 've', 'zo']


def usados(estado):
    comp = {e['nome'] for e in estado.ent.values() if isinstance(e, dict) and e.get('nome')}
    return comp, {n.split()[0] for n in comp}


def epitetos(estado):
    out = set()
    for e in estado.ent.values():
        n = e.get('nome') if isinstance(e, dict) else None
        if n and ' ' in n:
            out.add(n.split(' ', 1)[1])
    return sorted(out)


def gerar(rng, estado, extras=()):
    comp, prim = usados(estado)
    comp = comp | set(extras); prim = prim | {x.split()[0] for x in extras}
    ep = epitetos(estado)
    for _ in range(200):
        pnome = ''.join(rng.choice(SILABAS) for _ in range(rng.randint(1, 3))).capitalize()
        a, b = rng.choice(ep), rng.choice(ep)
        # epíteto composto de duas metades já usadas no molde
        ep_novo = a.split('-')[0] + '-' + b.split('-')[-1] if '-' in a and '-' in b else a
        nome = pnome + ' ' + ep_novo
        if pnome not in prim and nome not in comp:
            return nome
    return 'Anônimo %05d' % (len(comp) + 1)
