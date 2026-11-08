"""Gera os vetores de conformidade RNG/JSON/hash para o Kotlin (MI-03) em world/src/test/resources/conformidade/.

Uso: python3 -I Mythralis/motor/exportar_vetores.py   (determinístico: rodar de novo gera o mesmo arquivo)
Formato (uma linha por vetor, campos separados por '|'; valores por ','):
  RNG|semente|op|argumentos|valores        op: random, bits, randrange, randint, choice (sobre 0..n-1)
  DIA|semente|motor|dia|semente_inteira|randrange(1000) x 5
  JSON|entrada (JSON ascii)|canon|sha256(canon)
  CADEIA|hash_inicial|delta (JSON ascii)|hash_final
Segundo arquivo, vetores-estado.txt (MI-04):
  CAL|reino|dia_mundo|ano,dia_do_ano,estacao,lua|ano_base
  ESTADO|sem_dinamica|hash|n_entidades            (Estado.do_molde(world, None))
  ESTADO|com_dinamica|hash|n_entidades            (com a Dinamica; o Kotlin só confere a partir do MI-05)
  DELTA|delta sem importancia/notificacao (JSON ascii)|importancia|hash_do_estado_apos|n_entidades|n_ecos_ativos
    (sequência aplicada em ordem sobre o estado sem dinâmica; a importância é calculada antes de aplicar)
Terceiro arquivo, vetores-motores-pessoas.txt (MI-05): simulador só com demografia, sucessao, relogios e eventos
(`motores` = esses quatro), 10 anos por semente, um ponto de conferência por ano:
  HDR|semente|motores|hash_molde|hash_dinamica
  ANO|semente|ano_simulado|dia_fim|deltas|notificacoes|hash_cronica|hash_estado|n_entidades
Quarto arquivo, vetores-motores-mundo.txt (MI-06): simulador com os 12 motores (`motores.criar`), 100 anos por semente,
um ponto de conferência a cada 10 anos:
  HDR|semente|motores|hash_molde|hash_dinamica
  ANO|semente|ano_simulado|dia_fim|deltas|notificacoes|hash_cronica|hash_estado|n_entidades|regime|n_eras
Quinto arquivo, vetores-simulador.txt (MI-07): simulador completo (blocos, invariantes, saúde), 500 anos por semente (1, 7, 42):
  HDR|semente|motores|hash_molde|hash_dinamica
  BLOCO|semente|<linha de saude.txt do bloco>          (10 anos por bloco; inclui resultado, alertas, falhas e hash da crônica)
  PONTO|semente|ano_simulado|dia_fim|deltas|notificacoes|hash_cronica|hash_estado|n_entidades   (a cada 50 anos)
  INV|semente|sha256(invariantes.txt)|resultado final
Sexto arquivo, vetores-lote.txt (MI-08; `--lote`): 20 sementes consecutivas (1 a 20) x 500 anos, passo 10, 12 motores
(igual a `simulador.py --lote 20 --anos 500 --passo 10`); métricas da seção 14 do plano derivadas dos deltas (`lote.estatisticas`):
  HDR|motores|hash_molde|hash_dinamica
  LOTE|semente|resultado|deltas|notificacoes|hash_cronica|hash_estado|n_entidades|fim_era_1|primeiro_incidente|primeira_guerra|
       reinos_caidos|misterios_resolvidos|relogios_estourados|relogios_1o_seculo|pactos_formados|pactos_encerrados
"""
import os, sys, json, random
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import cronica as CR, carregador as C, calendario as CAL, estado as ES, importancia as IM, dinamica as DIN

RAIZ = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..'))
SAIDA = os.path.join(RAIZ, 'world', 'src', 'test', 'resources', 'conformidade', 'vetores-rng-json.txt')
SAIDA_MOTORES = os.path.join(RAIZ, 'world', 'src', 'test', 'resources', 'conformidade', 'vetores-motores-pessoas.txt')
SAIDA_MUNDO = os.path.join(RAIZ, 'world', 'src', 'test', 'resources', 'conformidade', 'vetores-motores-mundo.txt')
SAIDA_SIM = os.path.join(RAIZ, 'world', 'src', 'test', 'resources', 'conformidade', 'vetores-simulador.txt')
SAIDA_LOTE = os.path.join(RAIZ, 'world', 'src', 'test', 'resources', 'conformidade', 'vetores-lote.txt')
SAIDA_ESTADO = os.path.join(RAIZ, 'world', 'src', 'test', 'resources', 'conformidade', 'vetores-estado.txt')
SEMENTES = [0, 1, 42, 2**31, 2**32 - 1, 2**32, 123456789012345, 2**63 + 12345, 2**64 - 1, 0xdeadbeefcafef00d]


def rng_linhas():
    L = []
    for s in SEMENTES:
        r = random.Random(s)
        L.append('RNG|%d|random||%s' % (s, ','.join(repr(r.random()) for _ in range(12))))
        r = random.Random(s)
        for k in (1, 5, 31, 32, 33, 48, 62):
            L.append('RNG|%d|bits|%d|%s' % (s, k, ','.join(str(r.getrandbits(k)) for _ in range(6))))
        r = random.Random(s)
        for n in (1, 2, 3, 7, 10, 365, 1000, 1001, 2**20, 10**9):
            L.append('RNG|%d|randrange|%d|%s' % (s, n, ','.join(str(r.randrange(n)) for _ in range(10))))
        r = random.Random(s)
        for a, b in ((-6, 6), (1, 3), (-3, 3), (5, 5)):
            L.append('RNG|%d|randint|%d,%d|%s' % (s, a, b, ','.join(str(r.randint(a, b)) for _ in range(10))))
        r = random.Random(s)
        for n in (1, 4, 11):
            L.append('RNG|%d|choice|%d|%s' % (s, n, ','.join(str(r.choice(list(range(n)))) for _ in range(10))))
    return L


def dia_linhas():
    L = []
    for sem, mot, dia in (('1', 'demografia', 0), ('7', 'economia', 365), ('42', 'guerra', 36500), ('2026', 'ecos', 12345)):
        n = int(CR.sha('%s|%s|%d' % (sem, mot, dia))[:16], 16)
        r = random.Random(n)
        L.append('DIA|%s|%s|%d|%d|%s' % (sem, mot, dia, n, ','.join(str(r.randrange(1000)) for _ in range(5))))
    return L


CASOS = [
    None, True, False, 0, -1, 123456789012, 2**62, '', 'a', 'ação — “aspas” ♥', 'linha\nquebra\ttab\r"asp\\as"\x00\x08\x0c\x1f\x7f',
    '\U0001F600 emoji', [], {}, [1, [2, [3]], {'b': 1, 'a': None}], {'b': 1, 'a': 2, 'A': 3, 'é': 4, 'z': 5, '\uffff': 6, '\U00010000': 7},
    {'x': {'y': [True, None, 'k'], 'a': {}}, '10': 1, '9': 2, '': 3},
    {'id': 'd1', 'dia_mundo': 0, 'era': 1, 'tipo': 'nasc', 'entidade': 'npc.1', 'campo': 'estado', 'estado_anterior': None,
     'estado_novo': {'nome': 'Éowyn', 'idade': 20}, 'causa': '—', 'agentes': ['a', 'b'], 'quem_sabe': [], 'certeza': 'confirmado',
     'origem': 'demografia', 'importancia': 2, 'notificacao': False},
]


def json_linhas():
    L = []
    for c in CASOS:
        e = json.dumps(c, ensure_ascii=True)
        k = CR.canon(c)
        L.append('JSON|%s|%s|%s' % (e, k, CR.sha(k)))
    h = CR.sha(CR.canon({'cabecalho': {'semente': 1, 'molde': 'x'}}))
    L.append('CADEIA|%s|%s' % ('', h))
    for c in CASOS[-3:]:
        if isinstance(c, dict):
            h2 = CR.sha(h + CR.canon(c))
            L.append('CADEIA|%s|%s|%s' % (h, json.dumps(c, ensure_ascii=True), h2))
            h = h2
    return L


def _delta(e, dia, tipo, entidade, campo=None, novo=None, causa='teste', agentes=(), certeza='confirmado'):
    d = dict(id='d%06d' % (len(e._seq) + 1), dia_mundo=dia, era=e.mundo['era'], tipo=tipo, entidade=entidade, campo=campo,
             estado_anterior=e.estado_anterior_de(tipo, entidade, campo), estado_novo=novo, causa=causa, agentes=list(agentes),
             quem_sabe=[], certeza=certeza, origem='SIMULADO')
    e._seq.append(1)
    return d


def estado_linhas():
    w = C.carregar()
    L = []
    for rid in sorted(w.reinos):
        cal = CAL.construir(w)[rid]
        for dia in (-366, -365, -1, 0, 1, 29, 30, 90, 91, 92, 181, 182, 183, 272, 273, 274, 363, 364, 365, 366, 729, 730, 36500, 182500):
            dt = cal.data(dia)
            L.append('CAL|%s|%d|%d,%d,%s,%d|%d' % (rid, dia, dt['ano'], dt['dia_do_ano'], dt['estacao'], dt['lua'], CAL.ano_base(dia)))
    s0 = ES.Estado.do_molde(w, None)
    L.append('ESTADO|sem_dinamica|%s|%d' % (s0.hash(), len(s0.ent)))
    s1 = ES.Estado.do_molde(w, DIN.Dinamica(C.RAIZ))
    L.append('ESTADO|com_dinamica|%s|%d' % (s1.hash(), len(s1.ent)))
    e = ES.Estado.do_molde(w, None)
    e._seq = []
    principal = next(k for k in sorted(e.ent) if e.ent[k].get('principal') and e.ent[k].get('vivo'))
    outro = next(k for k in sorted(e.ent) if k.startswith('npc.') and not e.ent[k].get('principal') and e.ent[k].get('vivo'))
    governante = next(k for k in sorted(e.ent) if k.startswith('npc.') and 'governante' in e.ent[k].get('papeis', '').split(';')
                      and e.ent[k].get('vivo'))
    cidade = next(k for k in sorted(e.ent) if k.startswith('city.'))
    par = next(k for k in sorted(e.ent) if k.startswith('par.'))
    relogio = next(k for k in sorted(e.ent) if k.startswith('clock.'))
    evento = next(k for k in sorted(e.ent) if k.startswith('event.'))
    faccao = next(k for k in sorted(e.ent) if k.startswith('faction.'))
    seg = e.ent[relogio]['segmentos']
    passos = [
        ('era', 'mundo', None, dict(era=ES.ERA_INICIAL[0], nome=ES.ERA_INICIAL[1], regime=ES.ERA_INICIAL[2]), 0),
        ('campo', outro, 'saude', 'doente', 10),
        ('campo', principal, 'saude', 'doente', 20),
        ('campo', outro, 'vivo', False, 30),
        ('campo', principal, 'vivo', False, 40),
        ('campo', governante, 'vivo', False, 50),
        ('criacao', 'npc.sim.0001', None, dict(vivo=True, reino=e.ent[governante]['reino'], cidade=e.ent[governante]['cidade'],
                                               papeis='governante', principal=False, nome='Novo'), 60),
        ('criacao', 'npc.sim.0002', None, dict(vivo=True, reino=e.ent[governante]['reino'], papeis='cidadao', cargo='ferreiro'), 61),
        ('campo', relogio, 'progresso', seg - 1, 70),
        ('campo', relogio, 'progresso', 1, 71),
        ('campo', relogio, 'estourado', True, 72),
        ('campo', par, 'relacao', 'rivalidade', 80),
        ('campo', par, 'tensao', 77, 81),
        ('campo', evento, 'estado', 'em_cadeia', 90),
        ('campo', evento, 'estado', 'ocorrido', 91),
        ('campo', cidade, 'governante', 'npc.sim.0001', 100),
        ('campo', faccao, 'lideranca', 'npc.sim.0001', 101),
        ('guerra', cidade, None, None, 110),
        ('tratado', faccao, None, None, 111),
        ('boato', cidade, None, None, 112),
        ('laco_aliado', faccao, None, None, 113),
        ('pista_de_misterio', cidade, None, None, 114),
        ('tipo_desconhecido', cidade, None, None, 115),
        ('incidente_fronteira', 'kingdom.aeltharyn', None, None, 116),
    ]
    for tipo, ent, campo, novo, dia in passos:
        d = _delta(e, dia, tipo, ent, campo, novo)
        imp = IM.calcular(d, e)
        d['importancia'] = imp
        e.aplicar(d)
        d2 = {k: v for k, v in d.items() if k != 'importancia'}
        L.append('DELTA|%s|%d|%s|%d|%d' % (json.dumps(d2, ensure_ascii=True), imp, e.hash(), len(e.ent), len(e.ecos_ativos)))
    eco = next(k for k in sorted(e.ent) if k.startswith('eco.'))
    atual = e.ent[eco]['difusao']
    novo = dict(atual, ativo=False)
    d = _delta(e, 120, 'campo', eco, 'difusao', novo)
    d['importancia'] = IM.calcular(d, e)
    e.aplicar(d)
    d2 = {k: v for k, v in d.items() if k != 'importancia'}
    L.append('DELTA|%s|%d|%s|%d|%d' % (json.dumps(d2, ensure_ascii=True), d['importancia'], e.hash(), len(e.ent), len(e.ecos_ativos)))
    return L


def motores_linhas():
    import tempfile, shutil, simulador as SIM
    from motores import Demografia, Sucessao, Relogios, Eventos
    L = []
    for semente in (1, 7, 42):
        for ano in range(1, 11):
            tmp = tempfile.mkdtemp()
            try:
                din = DIN.Dinamica(C.RAIZ)
                mot = [Demografia(din), Sucessao(din), Relogios(din), Eventos(din)]
                sim = SIM.Simulador(os.path.join(tmp, 'r'), semente=semente, passo_anos=ano, motores=mot)
                sim.iniciar()
                if ano == 1:
                    L.append('HDR|%d|%s|%s|%s' % (semente, ','.join(sorted(m.nome for m in mot)), sim.hash_molde0, sim.hash_din0))
                sim.rodar(ano)
                L.append('ANO|%d|%d|%d|%d|%d|%s|%s|%d' % (semente, ano, sim.estado.proximo_dia, len(sim.crono.deltas), sim.n_notif,
                                                          sim.crono.hash, sim.estado.hash(), len(sim.estado.ent)))
            finally:
                shutil.rmtree(tmp, ignore_errors=True)
    return L


def mundo_linhas():
    import tempfile, shutil, simulador as SIM, motores as MOT
    L = []
    for semente in (1, 7, 42):
        tmp = tempfile.mkdtemp()
        try:
            din = DIN.Dinamica(C.RAIZ)
            mot = MOT.criar(C.RAIZ)
            pontos = []

            class Sim(SIM.Simulador):
                def _fechar_bloco(self, dia_fim):
                    r = super()._fechar_bloco(dia_fim)
                    pontos.append('ANO|%d|%d|%d|%d|%d|%s|%s|%d|%s|%d' % (
                        semente, len(pontos) * 10 + 10, self.estado.proximo_dia, len(self.crono.deltas), self.n_notif,
                        self.crono.hash, self.estado.hash(), len(self.estado.ent), self.estado.mundo['regime'], len(self.estado.eras)))
                    return r
            sim = Sim(os.path.join(tmp, 'r'), semente=semente, passo_anos=10, motores=mot)
            sim.iniciar()
            L.append('HDR|%d|%s|%s|%s' % (semente, ','.join(sorted(m.nome for m in mot)), sim.hash_molde0, sim.hash_din0))
            sim.rodar(100)
            L += pontos
        finally:
            shutil.rmtree(tmp, ignore_errors=True)
    return L


def simulador_linhas(anos=500):
    import tempfile, shutil, simulador as SIM, motores as MOT
    L = []
    for semente in (1, 7, 42):
        tmp = tempfile.mkdtemp()
        try:
            mot = MOT.criar(C.RAIZ)
            pontos = []

            class Sim(SIM.Simulador):
                def _fechar_bloco(self, dia_fim):
                    r = super()._fechar_bloco(dia_fim)
                    pontos.append((self.estado.proximo_dia, len(self.crono.deltas), self.n_notif, self.crono.hash,
                                       self.estado.hash(), len(self.estado.ent)))
                    return r
            sim = Sim(os.path.join(tmp, 'r'), semente=semente, passo_anos=10, motores=mot)
            sim.iniciar()
            L.append('HDR|%d|%s|%s|%s' % (semente, ','.join(sorted(m.nome for m in mot)), sim.hash_molde0, sim.hash_din0))
            res = sim.rodar(anos)
            with open(os.path.join(tmp, 'r', 'saude.txt'), encoding='utf-8') as f:
                L += ['BLOCO|%d|%s' % (semente, x.rstrip('\n')) for x in f if not x.startswith(('#', 'bloco|'))]
            for i, p in enumerate(pontos, 1):
                if i % 5 == 0:
                    L.append('PONTO|%d|%d|%d|%d|%d|%s|%s|%d' % (semente, i * 10, *p))
            with open(os.path.join(tmp, 'r', 'invariantes.txt'), encoding='utf-8') as f:
                L.append('INV|%d|%s|%s' % (semente, CR.sha(f.read()), res['resultado']))
        finally:
            shutil.rmtree(tmp, ignore_errors=True)
    return L


def lote_linhas(n=20, anos=500):
    import tempfile, shutil, simulador as SIM, motores as MOT, lote as LOT
    world = C.carregar()
    L = []
    cache = os.environ.get('LOTE_CACHE')  # opcional: pasta onde cada semente pronta fica guardada (retomada após interrupção)
    for semente in range(1, n + 1):
        arq = os.path.join(cache, 's%d.txt' % semente) if cache else None
        if arq and os.path.exists(arq):
            with open(arq, encoding='utf-8') as f:
                L += f.read().splitlines()
            continue
        tmp = tempfile.mkdtemp()
        ini = len(L)
        try:
            mot = MOT.criar(C.RAIZ)
            sim = SIM.Simulador(os.path.join(tmp, 'r'), semente=semente, passo_anos=10, motores=mot)
            sim.iniciar()
            if semente == 1:
                L.append('HDR|%s|%s|%s' % (','.join(sorted(m.nome for m in mot)), sim.hash_molde0, sim.hash_din0))
            res = sim.rodar(anos)
            e = LOT.estatisticas(world, sim.crono.deltas, sim.crono.cabecalho)
            L.append('LOTE|%d|%s|%d|%d|%s|%s|%d|%s|%s|%s|%s|%d|%d|%d|%d|%d' % (
                semente, res['resultado'], len(sim.crono.deltas), sim.n_notif, sim.crono.hash, sim.estado.hash(), len(sim.estado.ent),
                e['fim_era_1'], e['primeiro_incidente'], e['primeira_guerra'], ','.join(e['reinos_caidos']) or '—',
                e['misterios_resolvidos'], len(e['estourados']), e['relogios_1o_seculo'], e['pactos_formados'], e['pactos_encerrados']))
            if arq:
                os.makedirs(cache, exist_ok=True)
                with open(arq, 'w', encoding='utf-8', newline='\n') as f:
                    f.write('\n'.join(L[ini:]) + '\n')
            print('semente %d ok' % semente, flush=True)
        finally:
            shutil.rmtree(tmp, ignore_errors=True)
    return L


def main():
    if '--lote' in sys.argv:
        L6 = lote_linhas()
        with open(SAIDA_LOTE, 'w', encoding='utf-8', newline='\n') as f:
            f.write('\n'.join(L6) + '\n')
        print('%d vetores em %s' % (len(L6), os.path.relpath(SAIDA_LOTE, RAIZ)))
        return
    if '--simulador' in sys.argv:
        L5 = simulador_linhas()
        with open(SAIDA_SIM, 'w', encoding='utf-8', newline='\n') as f:
            f.write('\n'.join(L5) + '\n')
        print('%d vetores em %s' % (len(L5), os.path.relpath(SAIDA_SIM, RAIZ)))
        return
    L4 = mundo_linhas()
    with open(SAIDA_MUNDO, 'w', encoding='utf-8', newline='\n') as f:
        f.write('\n'.join(L4) + '\n')
    print('%d vetores em %s' % (len(L4), os.path.relpath(SAIDA_MUNDO, RAIZ)))
    L3 = motores_linhas()
    with open(SAIDA_MOTORES, 'w', encoding='utf-8', newline='\n') as f:
        f.write('\n'.join(L3) + '\n')
    print('%d vetores em %s' % (len(L3), os.path.relpath(SAIDA_MOTORES, RAIZ)))
    L2 = estado_linhas()
    with open(SAIDA_ESTADO, 'w', encoding='utf-8', newline='\n') as f:
        f.write('\n'.join(L2) + '\n')
    print('%d vetores em %s' % (len(L2), os.path.relpath(SAIDA_ESTADO, RAIZ)))
    L = rng_linhas() + dia_linhas() + json_linhas()
    os.makedirs(os.path.dirname(SAIDA), exist_ok=True)
    with open(SAIDA, 'w', encoding='utf-8', newline='\n') as f:
        f.write('\n'.join(L) + '\n')
    print('%d vetores em %s' % (len(L), os.path.relpath(SAIDA, RAIZ)))


if __name__ == '__main__':
    main()
