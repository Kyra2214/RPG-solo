"""Carregador único de Mythralis: lê os 5 reinos (esquema=1) num só World. Somente leitura do molde."""
import os, re, glob, json, hashlib

RAIZ = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), '..'))

TIPOS = ['reino', 'calendario', 'cidade', 'assentamento', 'npc', 'faccao', 'rota', 'relogio', 'evento', 'semente',
         'misterio', 'fato', 'reputacao', 'canal', 'cena', 'resumo', 'recurso', 'relacao', 'povo_no_reino',
         'excecoes', 'ids_aposentados']
PREFIXO = {'reino': 'kingdom.', 'calendario': 'calendario.', 'cidade': 'city.', 'assentamento': 'settlement.',
           'npc': 'npc.', 'faccao': 'faction.', 'rota': 'route.', 'relogio': 'clock.', 'evento': 'event.',
           'semente': 'seed.', 'misterio': 'mist.', 'fato': 'fact.', 'reputacao': 'rep.', 'canal': 'canal.',
           'cena': 'cena.'}


def _split(l):
    out, cur, i = [], '', 0
    while i < len(l):
        if l[i] == '\\' and i + 1 < len(l):
            cur += l[i:i + 2]; i += 2; continue
        if l[i] == '|':
            out.append(cur); cur = ''
        else:
            cur += l[i]
        i += 1
    out.append(cur)
    return [c.replace('\\|', '|').replace('\\\\', '\\') for c in out]


def ler_tabela(caminho):
    """Devolve (cabecalho, linhas[dict], erros[str])."""
    erros = []
    with open(caminho, encoding='utf-8') as f:
        L = f.read().split('\n')
    if not L or L[0].strip() != '# esquema=1':
        erros.append('falta "# esquema=1" na 1ª linha')
    L = [l for l in L if l and not l.startswith('#')]
    if not L:
        return [], [], erros + ['arquivo vazio']
    h = _split(L[0]); rows = []
    for n, l in enumerate(L[1:], 2):
        c = _split(l)
        if len(c) != len(h):
            erros.append('linha %d com %d campos (esperado %d)' % (n, len(c), len(h))); continue
        rows.append(dict(zip(h, c)))
    return h, rows, erros


def toks(s):
    """Campo-lista: itens separados por ';', sem os marcadores '?' e '—'."""
    return [x.strip() for x in s.split(';') if x.strip() and x.strip() not in ('?', '—', '--')]


class World:
    def __init__(self):
        self.reinos = {}        # id do reino -> dict(pasta, calendario, ...)
        self.t = {k: [] for k in TIPOS}   # tabelas do mundo inteiro, linhas com '_reino'
        self.cab = {}           # tipo -> {pasta: cabeçalho}
        self.erros = []         # erros de carga (formato)
        self.idx = {}           # id -> (tipo, linha)
        self.catalogo = None
        self.hash_molde = ''

    # ---- consultas
    def get(self, i):
        return self.idx.get(i, (None, None))[1]

    def tipo(self, i):
        return self.idx.get(i, (None, None))[0]

    def reino_de(self, i):
        r = self.get(i)
        return r.get('reino') or r.get('_reino') if r else None


def hash_molde(pastas):
    h = hashlib.sha256()
    for p in pastas:
        for f in sorted(glob.glob(os.path.join(p, '**', '*'), recursive=True)):
            if os.path.isfile(f) and f.endswith(('.txt', '.md', '.csv', '.json')):
                h.update(os.path.relpath(f, RAIZ).encode())
                with open(f, 'rb') as g:
                    h.update(g.read())
    return h.hexdigest()


def carregar(raiz=RAIZ):
    w = World()
    pastas = sorted(glob.glob(os.path.join(raiz, 'reinos', '0*-*', 'dados')))
    for pasta in pastas:
        nome = os.path.basename(os.path.dirname(pasta))
        for tipo in TIPOS:
            f = os.path.join(pasta, tipo + '.txt')
            if not os.path.exists(f):
                w.erros.append((nome, tipo, 'arquivo ausente')); continue
            h, rows, er = ler_tabela(f)
            w.cab.setdefault(tipo, {})[nome] = h
            for e in er:
                w.erros.append((nome, tipo, e))
            for r in rows:
                r['_reino_pasta'] = nome
                w.t[tipo].append(r)
    for r in w.t['reino']:
        w.reinos[r['id']] = dict(row=r, pasta=r['_reino_pasta'])
    for r in w.t['cidade'] + w.t['assentamento'] + w.t['npc'] + w.t['faccao'] + w.t['rota'] + w.t['relogio'] \
            + w.t['evento'] + w.t['semente'] + w.t['misterio'] + w.t['fato'] + w.t['reputacao'] + w.t['canal'] \
            + w.t['cena'] + w.t['calendario'] + w.t['reino']:
        w.idx.setdefault(r['id'], (None, r))
    for tipo in PREFIXO:
        for r in w.t[tipo]:
            w.idx[r['id']] = (tipo, r)
    cat = os.path.join(raiz, 'referencia', 'dados-brutos', 'personagem-catalogo.json')
    with open(cat, encoding='utf-8') as g:
        w.catalogo = json.load(g)
    w.hash_molde = hash_molde(list(pastas) + [os.path.join(raiz, 'mundo'), os.path.dirname(cat)])
    return w
