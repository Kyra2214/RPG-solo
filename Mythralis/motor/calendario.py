"""Calendário do mundo: a ponte entre reinos é o dia do ano (1 a 365); o ano local é só rótulo (M-02).

dia_mundo = 0 é o Silêncio, Lua 1, dia 1 do ano-base (742 das Seivas, Aeltharyn).
"""
import re

ANO_BASE = 742          # ano de Aeltharyn do dia_mundo 0 (M-02 §1, D-14)
DIAS_ANO = 365


def dia_do_ano(dia_mundo):
    return dia_mundo % DIAS_ANO + 1


def ano_base(dia_mundo):
    return ANO_BASE + dia_mundo // DIAS_ANO


def estacao_idx(doy):
    """0..3: M-02 §3 (inverno, primavera, verão, outono). Cada estação = 3 luas de 30 dias + 1 dia de transição;
    os dias 364 e 365 fecham o ano e ficam no outono (virada para o inverno)."""
    if doy <= 91: return 0
    if doy <= 182: return 1
    if doy <= 273: return 2
    return 3


def lua(doy):
    """Lua 1..12 ou 0 para dia de transição (Dias Sem Raiz e equivalentes)."""
    if doy in (91, 182, 273, 364, 365): return 0
    bloco = (doy - 1) // 91
    return bloco * 3 + ((doy - 1) % 91) // 30 + 1


def parse_ano_local(ano_inicial):
    m = re.match(r'\s*(\d+)', ano_inicial)
    return int(m.group(1)) if m else None


class CalendarioReino:
    def __init__(self, row):
        self.id = row['id']
        self.meses = int(row['meses']); self.dias_mes = int(row['dias_por_mes']); self.transicao = int(row['dias_de_transicao'])
        self.estacoes = [x.strip() for x in row['estacoes'].split(';')]
        self.ano_inicial = parse_ano_local(row['ano_inicial'])
        self.estacao_inicial = row['estacao_inicial']

    def valido(self):
        return self.meses * self.dias_mes + self.transicao == DIAS_ANO and len(self.estacoes) == 4 \
            and self.estacao_inicial == self.estacoes[0] and self.ano_inicial is not None

    def data(self, dia_mundo):
        doy = dia_do_ano(dia_mundo)
        ano = self.ano_inicial + dia_mundo // DIAS_ANO
        return dict(ano=ano, dia_do_ano=doy, estacao=self.estacoes[estacao_idx(doy)], lua=lua(doy))


def construir(world):
    cals = {c['id']: CalendarioReino(c) for c in world.t['calendario']}
    por_reino = {}
    for rid, info in world.reinos.items():
        por_reino[rid] = cals.get(info['row']['calendario'])
    return por_reino
