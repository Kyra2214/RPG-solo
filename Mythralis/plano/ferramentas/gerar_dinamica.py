"""Gera dinamica/*.txt (F4). Parâmetros PROPOSTOS: números de simulação que o cânone não tem (PLANO_MOTOR §9).
Uso: python3 plano/ferramentas/gerar_dinamica.py   (idempotente; o motor só lê os arquivos)"""
import os, re, sys
RAIZ = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..'))
sys.path.insert(0, os.path.join(RAIZ, 'motor'))
import carregador as C

SAIDA = os.path.join(RAIZ, 'dinamica')
PROP = 'PROPOSTO'

# classe -> (maturidade, madura_ini, idosa_ini, vida_max, mortalidade_base_pm, mortalidade_idosa_pm); anos; pm = por mil ao ano
CLASSES = {
    'efemera': (2, 6, 12, 20, 20, 150), 'curta': (8, 20, 40, 60, 4, 60), 'humana': (16, 40, 65, 100, 2, 30),
    'longa': (40, 120, 250, 400, 1, 20), 'milenar': (100, 300, 700, 1100, 1, 15), 'sem_idade': (0, 0, 0, 0, 0, 0),
}
RACAS = {
    'curta': 'goblin kobold homem-rato homem-coelho homem-raposa homem-corvo kenku insetoide'.split(),
    'longa': 'anao gnomo tortle feerico vampirico gigante sombrio onirico elemental-fogo fungal'.split(),
    'milenar': 'elfo ancestral-estelar elemental-terra elemental-agua elemental-ar plantae cristalino'.split(),
    'sem_idade': ['construto'],
}
FAIXAS = [('jovem', 160, 300), ('adulta', 250, 450), ('madura', 400, 650), ('idosa', 650, 900), ('?', 250, 650)]
LIMIARES = [
    ('doenca_idosa_pm', 40, 'por mil ao ano, de quem passou da faixa idosa', 'demografia'),
    ('recuperacao_pm', 350, 'por mil ao ano, de quem está doente', 'demografia'),
    ('fator_mortalidade_doente', 4, 'multiplica a morte natural de quem está doente', 'demografia'),
    ('prazo_vacancia_anos', 3, 'anos de vacância antes de nascer um NPC simulado para o posto', 'sucessao'),
    ('tensao_maxima', 100, 'teto da tensão por par de facções', 'politica'),
    ('tensao_base_alianca', 20, 'tensão para a qual um par aliado volta com o tempo', 'politica'),
    ('tensao_base_rivalidade', 50, 'idem, rivais', 'politica'),
    ('tensao_base_tratado', 25, 'idem, em tratado', 'politica'),
    ('tensao_inicial_ambigua', 40, 'par declarado aliado e rival ao mesmo tempo no molde (aviso do gerador)', 'politica'),
    ('limiar_romper_alianca', 68, 'aliança com tensão a partir daqui rompe (registro ruptura)', 'politica'),
    ('limiar_disputa_aberta', 78, 'rivalidade com tensão a partir daqui vira disputa aberta', 'politica'),
    ('limiar_tratado', 24, 'rivalidade com tensão até aqui vira tratado', 'politica'),
    ('limiar_tratado_quebra', 58, 'tratado com tensão a partir daqui se rompe e volta a rivalidade', 'politica'),
    ('limiar_tregua', 50, 'disputa aberta com tensão até aqui vira trégua (volta a rivalidade)', 'politica'),
    ('deriva_anual', 9, 'amplitude do sorteio anual da tensão (±)', 'politica'),
    ('decaimento_anual', 1, 'volta à base por ano (homeostase)', 'politica'),
    ('pressao_relogio', 3, 'tensão extra por ano se um relógio dos dois lados está a 2 segmentos de estourar', 'politica'),
    ('custo_ruptura', 25, 'tensão que se perde ao romper ou entrar em disputa (custo)', 'politica'),
    ('relogio_pressao_pm', 4, 'por mil por lua, por pressão ativa', 'relogios'),
    ('relogio_trava_pm', 4, 'por mil por lua, por trava ativa (reduz o avanço e aumenta o recuo)', 'relogios'),
    ('relogio_recuo_base_pm', 12, 'por mil por lua de recuar 1 segmento (homeostase)', 'relogios'),
    ('relogio_max_pm', 300, 'teto do avanço por lua', 'relogios'),
    ('atraso_cadeia_luas', 2, 'luas entre dois passos de uma cadeia de evento', 'eventos'),
]

ECONOMIA = [
    ('econ_producao_padrao', 2, 'produção ordinal (0 a 5) quando o molde traz "?" (ASSUMINDO)', 'economia'),
    ('econ_demanda_padrao_consumidor', 2, 'demanda ordinal quando o molde traz "?" e o papel é consumidor (ASSUMINDO)', 'economia'),
    ('econ_demanda_padrao_produtor', 0, 'demanda ordinal quando o molde traz "?" e o papel é produtor/transportador (ASSUMINDO)', 'economia'),
    ('econ_alcance_passos', 3, 'passos máximos de rota aberta para um produtor abastecer outro lugar', 'economia'),
    ('econ_import_max', 2, 'teto de oferta ordinal que um lugar recebe por rotas', 'economia'),
    ('econ_escassez_max', 3, 'teto do nível de escassez (0 a 3)', 'economia'),
    ('econ_quebra_safra_pm', 80, 'por mil por estação de um produtor perder 1 de produção', 'economia'),
    ('econ_recuperacao_pm', 500, 'por mil por estação de um produtor recuperar 1 de produção', 'economia'),
    ('econ_revolta_estacoes', 4, 'estações seguidas em escassez máxima que viram revolta (homeostase: zera depois)', 'economia'),
    ('rota_bloqueio_pm', 40, 'por mil por estação (fora do inverno) de uma rota aberta bloquear', 'economia'),
    ('rota_bloqueio_inverno_pm', 150, 'idem, no inverno', 'economia'),
    ('rota_bloqueio_incerta_pm', 30, 'extra por mil se a rota é com_ressalva ou desconhecida', 'economia'),
    ('rota_reabertura_pm', 450, 'por mil por estação de uma rota bloqueada reabrir', 'economia'),
]

ECOS = [
    ('eco_atraso_dias', 12, 'dias entre dois passos de difusão de um Eco', 'ecos'),
    ('eco_max_passos', 8, 'passos máximos de difusão de um Eco', 'ecos'),
    ('eco_prob_publico_pm', 800, 'por mil por passo, por vizinho: fato de camada pública passa adiante', 'ecos'),
    ('eco_prob_local_pm', 450, 'idem, camada local', 'ecos'),
    ('eco_prob_restrito_pm', 120, 'idem, camada restrita', 'ecos'),
    ('eco_prob_secreto_pm', 20, 'idem, camada secreta', 'ecos'),
    ('eco_passos_por_degrau', 2, 'passos para a certeza descer um degrau (confirmado, alegado, rumor)', 'ecos'),
]

MISTERIOS = [
    ('mist_pista_pm', 70, 'por mil por ano de um mistério aberto produzir uma pista (sem agente, só pista, nunca resposta)', 'misterios'),
    ('mist_max_pistas', 6, 'teto de pistas por mistério (homeostase: o mundo não esgota o mistério sozinho)', 'misterios'),
]

FRONTEIRA = [
    ('fronteira_limite_anos', 120, 'anos sem gatilho de fronteira após os quais a Era I (selado) termina por segurança (D-M4)', 'fronteira'),
    ('front_tensao_base', 1, 'pontos de tensão que cada par vizinho do anel soma por ano', 'fronteira'),
    ('front_tensao_por_relogio', 1, 'pontos de tensão por ano a mais para cada relógio estourado nos dois reinos do par', 'fronteira'),
    ('front_tensao_jitter', 3, 'sorteio de 0 a este valor somado à tensão de cada ano (semente por motor e data)', 'fronteira'),
    ('front_tensao_limiar', 300, 'tensão em que o par produz um incidente de fronteira (o primeiro encerra o regime selado, D-M4)', 'fronteira'),
    ('front_tensao_pos_incidente', 150, 'tensão do par depois de um incidente (homeostase: a tensão não zera)', 'fronteira'),
    ('front_tensao_max', 400, 'teto da tensão de um par', 'fronteira'),
]

DIPLOMACIA = [
    ('dipl_janela_anos', 5, 'anos em que um incidente de fronteira espera resposta (tratado ou ruptura) antes de esfriar sem resposta', 'diplomacia'),
    ('dipl_tratado_pm', 350, 'por mil por ano de um incidente pendente virar tratado entre os dois reinos', 'diplomacia'),
    ('dipl_ruptura_pm', 200, 'por mil por ano de um incidente pendente virar ruptura (somado ao de tratado; o resto é espera)', 'diplomacia'),
    ('dipl_tratado_reduz', 120, 'pontos de tensão que um tratado tira do par', 'diplomacia'),
    ('dipl_tratado_anos', 30, 'duração de um tratado, em anos', 'diplomacia'),
    ('dipl_tratado_fator_pm', 500, 'fator por mil do crescimento anual da tensão com tratado vigente', 'diplomacia'),
    ('dipl_quebra_pm', 25, 'por mil por ano de um tratado vigente ser quebrado (quebra de contrato e ruptura)', 'diplomacia'),
    ('dipl_ruptura_soma', 60, 'pontos de tensão que uma ruptura soma ao par', 'diplomacia'),
    ('dipl_ruptura_anos', 12, 'duração da ruptura, em anos, até o par voltar a neutra', 'diplomacia'),
    ('dipl_ruptura_fator_pm', 1500, 'fator por mil do crescimento anual da tensão durante a ruptura', 'diplomacia'),
]

GUERRA = [
    ('guerra_tensao_min', 240, 'tensão mínima de um par em ruptura para poder entrar em guerra', 'guerra'),
    ('guerra_pm', 80, 'por mil por ano de um par em ruptura, com a tensão mínima e as duas coroas firmes, entrar em guerra', 'guerra'),
    ('guerra_anos', 6, 'duração de uma guerra, em anos, até a paz (tratado de paz)', 'guerra'),
    ('guerra_pos_tensao', 150, 'tensão do par depois da paz ou da queda de uma coroa', 'guerra'),
    ('guerra_baixa_pm', 150, 'por mil por ano, em cada reino em guerra, de morrer um NPC principal ou general (homeostase: só com vivos suficientes)', 'guerra'),
    ('guerra_baixa_min_vivos', 4, 'mínimo de principais e generais vivos de um reino para que a guerra cause baixa', 'guerra'),
    ('queda_min_anos', 3, 'anos de guerra antes de uma coroa poder cair', 'guerra'),
    ('queda_pm', 20, 'por mil por ano de guerra, em cada reino, de a coroa cair (marco da era; a guerra termina)', 'guerra'),
]

INVARIANTES = [
    ('inv_min_importantes_decada', 3, 'I-17: mínimo de deltas de importância 3+ por reino a cada 10 anos (proporcional ao bloco)', 'invariantes'),
    ('inv_max_importantes_ano', 60, 'I-18: máximo de deltas de importância 3+ por reino em um ano', 'invariantes'),
]


def w_tab(nome, cab, linhas, nota):
    p = os.path.join(SAIDA, nome)
    t = '# esquema=1\n# ' + nota + '\n' + '|'.join(cab) + '\n' + ''.join('|'.join(str(x).replace('|', '/') for x in l) + '\n' for l in linhas)
    with open(p, 'w', encoding='utf-8', newline='\n') as f:
        f.write(t)


def main():
    w = C.carregar()
    os.makedirs(SAIDA, exist_ok=True)
    w_tab('classes_longevidade.txt', ['classe', 'maturidade', 'madura_ini', 'idosa_ini', 'vida_max', 'mortalidade_base_pm',
                                      'mortalidade_idosa_pm', 'fonte', 'etiqueta'],
          [(k,) + v + ('PLANO_MOTOR §9.2 (D-M6)', PROP) for k, v in CLASSES.items()],
          'anos; pm = por mil ao ano; sem_idade não morre de velhice. Revisão do autor pendente (D-M6).')
    cl = {r: k for k, v in RACAS.items() for r in v}
    cat = w.catalogo
    linhas = []
    for r in cat['races']:
        linhas.append(('raca', r['id'], cl.get(r['id'], 'humana'), 'raça' + ('' if r['id'] in cl else ' (padrão humana)'), PROP))
    pai = {r['id']: cl.get(r['id'], 'humana') for r in cat['races']}
    for s in cat['subraces']:
        linhas.append(('subraca', s['id'], pai[s['parentRaceId']], 'herda da raça ' + s['parentRaceId'], PROP))
    w_tab('longevidade.txt', ['tipo', 'id', 'classe', 'fonte', 'etiqueta'], linhas, 'uma classe por raça e por sub-raça (50 + 50).')
    w_tab('faixas_etarias.txt', ['faixa', 'de_pm', 'ate_pm', 'fonte', 'etiqueta'],
          [(a, b, c, 'fração da vida_max da classe, em por mil; "?" cobre idade desconhecida', PROP) for a, b, c in FAIXAS],
          'tradução de jovem/adulta/madura/idosa em intervalo, por fração da vida_max da classe de longevidade.')
    w_tab('limiares.txt', ['chave', 'valor', 'descricao', 'motor', 'fonte', 'etiqueta'],
          [(a, b, c, d, 'PLANO_MOTOR §9.2', PROP) for a, b, c, d in LIMIARES], 'limiares e taxas dos motores da F4.')
    w_tab('economia.txt', ['chave', 'valor', 'descricao', 'motor', 'fonte', 'etiqueta'],
          [(a, b, c, d, 'PLANO_MOTOR §9.2', PROP) for a, b, c, d in ECONOMIA], 'escalas ordinais 0 a 5 e taxas do motor de economia e rotas (F5.1).')
    w_tab('ecos.txt', ['chave', 'valor', 'descricao', 'motor', 'fonte', 'etiqueta'],
          [(a, b, c, d, 'PLANO_MOTOR §7 e §9.2', PROP) for a, b, c, d in ECOS], 'taxas do motor de Ecos (F5.2).')
    w_tab('misterios.txt', ['chave', 'valor', 'descricao', 'motor', 'fonte', 'etiqueta'],
          [(a, b, c, d, 'PLANO_MOTOR §7 e §9.2', PROP) for a, b, c, d in MISTERIOS], 'taxas do motor de mistérios (F5.3).')
    w_tab('fronteira.txt', ['chave', 'valor', 'descricao', 'motor', 'fonte', 'etiqueta'],
          [(a, b, c, d, 'PLANO_MOTOR §8 (D-M4)', PROP) for a, b, c, d in FRONTEIRA], 'limite de segurança do regime selado (F5.4) e tensão entre reinos vizinhos do anel (F7.1).')
    w_tab('diplomacia.txt', ['chave', 'valor', 'descricao', 'motor', 'fonte', 'etiqueta'],
          [(a, b, c, d, 'PLANO_MOTOR §7 e §8', PROP) for a, b, c, d in DIPLOMACIA], 'tratados, rupturas e quebras de contrato entre reinos vizinhos (F7.2).')
    w_tab('guerra.txt', ['chave', 'valor', 'descricao', 'motor', 'fonte', 'etiqueta'],
          [(a, b, c, d, 'PLANO_MOTOR §7 e §8', PROP) for a, b, c, d in GUERRA], 'guerras, baixas, paz e queda de coroa entre reinos vizinhos (F7.3).')
    w_tab('invariantes.txt', ['chave', 'valor', 'descricao', 'motor', 'fonte', 'etiqueta'],
          [(a, b, c, d, 'PLANO_MOTOR §12 e §14', PROP) for a, b, c, d in INVARIANTES], 'faixas dos invariantes de alerta I-17 e I-18 (F6.2).')
    # relógios: regra executável gerada de `entidades`; revisada à mão depois (revisado=não até lá)
    ev = {}
    for e in w.t['evento']:
        ev.setdefault(e['relogio'], []).append(e['id'])
    linhas = []
    for r in w.t['relogio']:
        ids = [t for t in C.toks(r['entidades']) if t in w.idx]
        npcs = {n['id']: n for n in w.t['npc']}
        pres = [i for i in ids if i.startswith(('faction.', 'city.', 'settlement.'))]
        trav = [i for i in ids if i in npcs and set(C.toks(npcs[i]['papeis'])) & {'heroi', 'guardiao', 'benfeitor', 'governante'}]
        ina = 0 if r['inacao'].strip().lower().startswith('só avança') else 6
        linhas.append((r['id'], 8, ';'.join(pres) or '—', ';'.join(trav) or '—', ';'.join(ev.get(r['id'], [])) or '—', ina,
                       'gerado de relogio.entidades; papéis do catálogo de NPC', PROP, 'não'))
    w_tab('relogios_dinamica.txt', ['relogio', 'taxa_base_pm', 'pressoes', 'travas', 'evento_ao_completar', 'inacao_pm',
                                    'fonte', 'etiqueta', 'revisado'], linhas,
          'taxa_base_pm: por mil por lua de avançar 1 segmento; pressões e travas são ids; revisado=não até o autor conferir.')
    print('dinamica/: 5 arquivos;', len(linhas), 'relógios')


if __name__ == '__main__':
    main()
