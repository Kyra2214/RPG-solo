"""Motores de Mythralis. F4: demografia, sucessão, relógios, eventos e política (só dentro dos reinos, Era I).
F5.1: economia e rotas (sazonal). F7.2: diplomacia (anual). F7.3: guerra (anual). F5.2: Ecos (diária). F5.3: mistérios (anual). F5.4: fronteira (anual).

Interface de um motor:
    class Meu:
        nome = 'demografia'                  # único; define a ordem de execução (alfabética) e a semente do rng
        frequencia = 'anual'                 # 'diaria' | 'lunar' | 'sazonal' | 'anual'
        def executar(self, ctx):             # ctx.dia_mundo, .dia_do_ano, .ano, .estado (só leitura), .rng, .mundo, .era
            return [ {tipo, entidade, campo, estado_novo, causa{ids,regra}, agentes, quem_sabe, certeza}, ... ]
O simulador preenche id, dia_mundo, era, estado_anterior, origem, importancia e notificacao.
Atenção: os deltas de uma chamada só são aplicados ao estado DEPOIS que `executar` devolve a lista.
"""
import os, sys
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..'))
import carregador as C
import dinamica as D
from .demografia import Demografia
from .sucessao import Sucessao
from .relogios import Relogios
from .eventos import Eventos
from .politica import Politica
from .economia import Economia
from .ecos import Ecos
from .misterios import Misterios
from .diplomacia import Diplomacia
from .fronteira import Fronteira
from .guerra import Guerra


def criar(raiz=C.RAIZ):
    din = D.Dinamica(raiz)
    return [Demografia(din), Sucessao(din), Relogios(din), Eventos(din), Politica(din), Economia(din), Ecos(din), Misterios(din), Diplomacia(din), Fronteira(din), Guerra(din)]


MOTORES = criar()
