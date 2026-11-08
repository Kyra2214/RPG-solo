"""Crônica de Mythralis: registro append-only de deltas, com cadeia de hash (F3).

Formato (jsonl, uma linha por registro):
  linha 1:  {"cabecalho": {...}}
  demais:   {"delta": {...}, "h": "<sha256 encadeado>"}
O hash de cada linha encadeia o da anterior; adulterar ou reordenar uma linha quebra a cadeia.
A crônica é a fonte da verdade: estado = molde + dinâmica + replay(crônica).
"""
import json, hashlib, os

CERTEZAS = ('confirmado', 'alegado', 'suspeita', 'rumor', 'inferência', 'desconhecido')
CAMPOS_DELTA = ('id', 'dia_mundo', 'era', 'tipo', 'entidade', 'campo', 'estado_anterior', 'estado_novo', 'causa',
                'agentes', 'quem_sabe', 'certeza', 'origem', 'importancia', 'notificacao')


def canon(o):
    return json.dumps(o, ensure_ascii=False, sort_keys=True, separators=(',', ':'))


def sha(s):
    return hashlib.sha256(s.encode('utf-8')).hexdigest()


class ErroCronica(Exception):
    pass


class Cronica:
    def __init__(self, caminho):
        self.caminho = caminho
        self.cabecalho = None
        self.deltas = []
        self.hash = ''
        self._f = None

    @classmethod
    def criar(cls, caminho, cabecalho):
        if os.path.exists(caminho):
            raise ErroCronica('crônica já existe: ' + caminho)
        os.makedirs(os.path.dirname(os.path.abspath(caminho)), exist_ok=True)
        c = cls(caminho)
        c.cabecalho = dict(cabecalho)
        c.hash = sha(canon({'cabecalho': c.cabecalho}))
        c._f = open(caminho, 'w', encoding='utf-8', newline='\n')
        c._f.write(canon({'cabecalho': c.cabecalho}) + '\n')
        c._f.flush()
        return c

    @classmethod
    def abrir(cls, caminho, anexar=False):
        """Lê e confere a cadeia de hash. Com anexar=True deixa o arquivo aberto para continuar."""
        c = cls(caminho)
        with open(caminho, encoding='utf-8') as f:
            linhas = f.read().split('\n')
        if linhas and linhas[-1] == '':
            linhas.pop()
        if not linhas:
            raise ErroCronica('crônica vazia')
        cab = json.loads(linhas[0])
        if 'cabecalho' not in cab:
            raise ErroCronica('falta o cabeçalho na 1ª linha')
        c.cabecalho = cab['cabecalho']
        h = sha(canon({'cabecalho': c.cabecalho}))
        for n, l in enumerate(linhas[1:], 2):
            r = json.loads(l)
            h = sha(h + canon(r['delta']))
            if r.get('h') != h:
                raise ErroCronica('cadeia de hash quebrada na linha %d' % n)
            c.deltas.append(r['delta'])
        c.hash = h
        if anexar:
            c._f = open(caminho, 'a', encoding='utf-8', newline='\n')
        return c

    def anexar(self, delta):
        h = sha(self.hash + canon(delta))
        self._f.write(canon({'delta': delta, 'h': h}) + '\n')
        self.hash = h
        self.deltas.append(delta)

    def descarregar(self):
        if self._f:
            self._f.flush()

    def fechar(self):
        if self._f:
            self._f.flush(); self._f.close(); self._f = None
