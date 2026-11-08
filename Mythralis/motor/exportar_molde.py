"""Exporta o molde de Mythralis e a dinâmica para os recursos do app (MI-01 do plano de integração).

Uso: python3 -I motor/exportar_molde.py [destino]              exporta e verifica
     python3 -I motor/exportar_molde.py [destino] --verificar  só verifica o que já foi exportado

Destino padrão: rules/src/main/resources/mundos/mythralis (D-I4). Além do molde e da dinâmica, grava derivados/ (hoje só
subracas-em-prosa.txt, que o validador do MI-02 usa). A árvore exportada espelha os caminhos de
Mythralis/ (reinos/*/dados, mundo, referencia/dados-brutos e dinamica), porque o hash do molde inclui o caminho
relativo de cada arquivo. O manifesto.txt lista os arquivos na ordem exata do hash, com tamanho e sha256 de cada um.
Somente leitura do molde; stdlib; sem data nem outro dado que mude entre execuções (rodar de novo gera o mesmo).
"""
import os, re, sys, glob, shutil, hashlib

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import carregador as C

RAIZ = C.RAIZ
REPO = os.path.abspath(os.path.join(RAIZ, '..'))
DESTINO_PADRAO = os.path.join(REPO, 'rules', 'src', 'main', 'resources', 'mundos', 'mythralis')
EXT_MOLDE = ('.txt', '.md', '.csv', '.json')
SUBPASTAS = ('reinos', 'mundo', 'referencia', 'dinamica', 'derivados')
MANIFESTO = 'manifesto.txt'
DERIVADO_SUBRACAS = 'derivados/subracas-em-prosa.txt'
CABECALHO = 'tipo|caminho|bytes|sha256'


def _rel(raiz, f):
    return os.path.relpath(f, raiz).replace(os.sep, '/')


def arquivos_molde(raiz):
    """Mesma seleção e mesma ordem de carregador.hash_molde: reinos/0*-*/dados, mundo, referencia/dados-brutos."""
    pastas = sorted(glob.glob(os.path.join(raiz, 'reinos', '0*-*', 'dados')))
    pastas += [os.path.join(raiz, 'mundo'), os.path.join(raiz, 'referencia', 'dados-brutos')]
    out = []
    for p in pastas:
        for f in sorted(glob.glob(os.path.join(p, '**', '*'), recursive=True)):
            if os.path.isfile(f) and f.endswith(EXT_MOLDE):
                out.append(f)
    return out


def arquivos_dinamica(raiz):
    return sorted(glob.glob(os.path.join(raiz, 'dinamica', '*.txt')))


def arquivos_derivados(raiz):
    return sorted(glob.glob(os.path.join(raiz, 'derivados', '*.txt')))


def subracas_em_prosa():
    """Sub-raças do catálogo citadas na prosa (reinos/*/*.md e mundo/*.md), que não vai para o app. Mesma busca de
    validador.py (W-05); o validador em Kotlin só precisa saber QUAIS aparecem, para escrever a mesma mensagem."""
    sub = sorted(x['id'] for x in C.carregar().catalogo['subraces'])
    arqs = glob.glob(os.path.join(RAIZ, 'reinos', '*', '*.md')) + glob.glob(os.path.join(RAIZ, 'mundo', '*.md'))
    prosa = ' '.join(open(x, encoding='utf-8', errors='ignore').read() for x in arqs)
    return [s for s in sub if re.search(r'(?<![\w-])' + re.escape(s) + r'(?![\w-])', prosa)]


def texto_derivado_subracas():
    return '\n'.join(['# esquema=1',
                      '# sub-raças do catálogo citadas na prosa de reinos/*/*.md e mundo/*.md (derivado por exportar_molde.py)',
                      'subraca'] + subracas_em_prosa()) + '\n'


def hash_arquivos(raiz, arquivos):
    h = hashlib.sha256()
    for f in arquivos:
        h.update(_rel(raiz, f).encode())
        with open(f, 'rb') as g:
            h.update(g.read())
    return h.hexdigest()


def montar_manifesto(raiz):
    molde, din, der = arquivos_molde(raiz), arquivos_dinamica(raiz), arquivos_derivados(raiz)
    L = ['# esquema=1',
         '# gerado por Mythralis/motor/exportar_molde.py; não editar à mão',
         '# molde_sha256=' + hash_arquivos(raiz, molde),
         '# dinamica_sha256=' + hash_arquivos(raiz, din),
         '# arquivos_molde=%d' % len(molde),
         '# arquivos_dinamica=%d' % len(din),
         '# arquivos_derivados=%d' % len(der),
         CABECALHO]
    for tipo, lista in (('molde', molde), ('dinamica', din), ('derivado', der)):
        for f in lista:
            rel = _rel(raiz, f)
            assert '|' not in rel and '\n' not in rel, 'caminho com caractere reservado: ' + rel
            with open(f, 'rb') as g:
                b = g.read()
            L.append('|'.join([tipo, rel, str(len(b)), hashlib.sha256(b).hexdigest()]))
    return '\n'.join(L) + '\n'


def verificar(destino):
    """Devolve (erros, hash_molde). O hash do destino tem de ser o do carregador e o do validacao-mundo.txt."""
    erros = []
    mf = os.path.join(destino, MANIFESTO)
    if not os.path.isfile(mf):
        return ['sem %s em %s' % (MANIFESTO, destino)], ''
    with open(mf, encoding='utf-8', newline='') as g:
        gravado = g.read()
    esperado = montar_manifesto(destino)
    if gravado != esperado:
        erros.append('manifesto não confere com os arquivos exportados (arquivo alterado, faltando ou sobrando)')
    nomes = {l.split('|')[1] for l in gravado.split('\n') if l.startswith(('molde|', 'dinamica|', 'derivado|'))}
    for s in SUBPASTAS:
        for f in glob.glob(os.path.join(destino, s, '**', '*'), recursive=True):
            if os.path.isfile(f) and _rel(destino, f) not in nomes:
                erros.append('arquivo fora do manifesto: ' + _rel(destino, f))
    gerado = os.path.join(destino, DERIVADO_SUBRACAS)
    if not os.path.isfile(gerado):
        erros.append('falta ' + DERIVADO_SUBRACAS)
    else:
        with open(gerado, encoding='utf-8', newline='') as g:
            if g.read() != texto_derivado_subracas():
                erros.append(DERIVADO_SUBRACAS + ' difere do que a prosa de origem gera; exporte de novo')
    h = hash_arquivos(destino, arquivos_molde(destino))
    ref = C.carregar().hash_molde
    if h != ref:
        erros.append('hash do destino (%s) difere do molde de origem (%s)' % (h[:16], ref[:16]))
    val = os.path.join(RAIZ, 'saidas', 'validacao-mundo.txt')
    if os.path.isfile(val):
        with open(val, encoding='utf-8') as g:
            m = re.search(r'molde sha256=([0-9a-f]+)', g.read())
        if not m:
            erros.append('validacao-mundo.txt sem a linha de resumo com o hash do molde')
        elif not h.startswith(m.group(1)):
            erros.append('hash do destino (%s) difere do validacao-mundo.txt (%s); rode validar_mundo.py' % (h[:16], m.group(1)))
    else:
        print('aviso: saidas/validacao-mundo.txt ausente; hash não conferido com ele')
    return erros, h


def exportar(destino):
    destino = os.path.abspath(destino)
    if RAIZ == destino or RAIZ.startswith(destino + os.sep):
        sys.exit('destino inválido (é a própria origem ou uma pasta acima dela): ' + destino)
    molde, din = arquivos_molde(RAIZ), arquivos_dinamica(RAIZ)
    if hash_arquivos(RAIZ, molde) != C.carregar().hash_molde:
        sys.exit('o hash calculado aqui difere do carregador; não exportado')
    for s in SUBPASTAS:
        shutil.rmtree(os.path.join(destino, s), ignore_errors=True)
    for f in molde + din:
        d = os.path.join(destino, os.path.relpath(f, RAIZ))
        os.makedirs(os.path.dirname(d), exist_ok=True)
        shutil.copyfile(f, d)  # bytes idênticos: o hash depende deles
    os.makedirs(os.path.join(destino, 'derivados'), exist_ok=True)
    with open(os.path.join(destino, DERIVADO_SUBRACAS), 'w', encoding='utf-8', newline='\n') as g:
        g.write(texto_derivado_subracas())
    with open(os.path.join(destino, MANIFESTO), 'w', encoding='utf-8', newline='\n') as g:
        g.write(montar_manifesto(destino))
    return len(molde), len(din)


if __name__ == '__main__':
    args = [a for a in sys.argv[1:] if not a.startswith('--')]
    so_verificar = '--verificar' in sys.argv[1:]
    dest = os.path.abspath(args[0]) if args else DESTINO_PADRAO
    if not so_verificar:
        n_m, n_d = exportar(dest)
        print('exportado: %d arquivos do molde + %d da dinâmica em %s' % (n_m, n_d, dest))
    erros, h = verificar(dest)
    for e in erros:
        print('ERRO: ' + e)
    if not erros:
        print('OK: molde sha256=%s (confere com o carregador e com saidas/validacao-mundo.txt)' % h[:16])
    sys.exit(1 if erros else 0)
