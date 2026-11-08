"""Camada de dinâmica (F4): lê dinamica/*.txt. Só leitura; nunca volta para o molde. Arquivo ausente = vazio (sem erro)."""
import os, re, hashlib
import carregador as C, cronica as CR


def _tab(raiz, nome):
    f = os.path.join(raiz, 'dinamica', nome)
    if not os.path.exists(f):
        return []
    return C.ler_tabela(f)[1]


class Dinamica:
    def __init__(self, raiz=C.RAIZ):
        self.vazia = not os.path.exists(os.path.join(raiz, 'dinamica', 'classes_longevidade.txt'))
        self.classes = {r['classe']: dict(mat=int(r['maturidade']), mad=int(r['madura_ini']), ido=int(r['idosa_ini']),
                                          max=int(r['vida_max']), base=int(r['mortalidade_base_pm']),
                                          mido=int(r['mortalidade_idosa_pm'])) for r in _tab(raiz, 'classes_longevidade.txt')}
        self.longev = {r['id']: r['classe'] for r in _tab(raiz, 'longevidade.txt')}
        self.faixas = {r['faixa']: (int(r['de_pm']), int(r['ate_pm'])) for r in _tab(raiz, 'faixas_etarias.txt')}
        self.lim = {r['chave']: int(r['valor']) for r in _tab(raiz, 'limiares.txt')}
        self.econ = {r['chave']: int(r['valor']) for r in _tab(raiz, 'economia.txt')}
        self.ecos = {r['chave']: int(r['valor']) for r in _tab(raiz, 'ecos.txt')}
        self.mist = {r['chave']: int(r['valor']) for r in _tab(raiz, 'misterios.txt')}
        self.front = {r['chave']: int(r['valor']) for r in _tab(raiz, 'fronteira.txt')}
        self.dipl = {r['chave']: int(r['valor']) for r in _tab(raiz, 'diplomacia.txt')}
        self.guerra = {r['chave']: int(r['valor']) for r in _tab(raiz, 'guerra.txt')}
        self.inv = {r['chave']: int(r['valor']) for r in _tab(raiz, 'invariantes.txt')}
        self.relogios = {}
        for r in _tab(raiz, 'relogios_dinamica.txt'):
            self.relogios[r['relogio']] = dict(taxa=int(r['taxa_base_pm']), pressoes=C.toks(r['pressoes']),
                                               travas=C.toks(r['travas']), eventos=C.toks(r['evento_ao_completar']),
                                               inacao=int(r['inacao_pm']))

    def classe_de(self, povo, subraca):
        return self.longev.get(subraca) or self.longev.get(povo) or 'humana'

    def idade_inicial(self, npc_id, idade_txt, classe):
        """Idade em anos de um NPC do molde: número do texto, ou derivada da faixa (determinística pelo id)."""
        k = self.classes.get(classe)
        if not k or k['max'] == 0:
            return None
        m = re.match(r'\s*(?:cerca de\s+)?(\d+)\s*$', idade_txt)
        if m:
            return int(m.group(1))
        faixa = re.match(r'\s*([a-zA-Zçã?]+)', idade_txt)
        faixa = faixa.group(1).lower() if faixa else '?'
        de, ate = self.faixas.get(faixa, self.faixas.get('?', (250, 650)))
        lo, hi = max(k['mat'], k['max'] * de // 1000), max(k['mat'] + 1, k['max'] * ate // 1000)
        h = int(CR.sha('idade|' + npc_id)[:8], 16)
        return lo + h % max(1, hi - lo)

    def nasc_dia(self, npc_id, idade_txt, classe):
        a = self.idade_inicial(npc_id, idade_txt, classe)
        if a is None:
            return None
        return -(a * 365 + int(CR.sha('dia|' + npc_id)[:8], 16) % 365)

    def morte_pm(self, classe, idade):
        """Chance de morte natural no ano, por mil."""
        k = self.classes.get(classe)
        if not k or k['max'] == 0:
            return 0
        if idade < k['ido']:
            return k['base']
        frac = min(1000, (idade - k['ido']) * 1000 // max(1, k['max'] - k['ido']))
        return min(1000, k['mido'] + (1000 - k['mido']) * frac * frac // 1000000)

    def e_idoso(self, classe, idade):
        k = self.classes.get(classe)
        return bool(k and k['max'] and idade >= k['ido'])
