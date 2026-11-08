"""Testes da F4. Rodar: python3 -m unittest discover -s motor/testes -v   (na raiz de Mythralis)
Aceite da F4: rodar 100 anos só dentro dos reinos (Era I) sem erro, com os cinco motores reais."""
import os, sys, json, shutil, tempfile, unittest
AQUI = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(AQUI, '..'))
import carregador as C, cronica as CR, estado as ES, invariantes as INV, simulador as S, dinamica as D, motores as M

MUNDO = C.carregar()
DIN = D.Dinamica()


def deltas(d):
    return CR.Cronica.abrir(os.path.join(d, 'cronica.jsonl')).deltas


def ler(p):
    with open(p, encoding='utf-8') as f:
        return f.read()


class Base(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.mkdtemp(prefix='myth4-')
        self.addCleanup(shutil.rmtree, self.tmp, True)

    def rodar(self, nome, anos, passo=10, semente=1, extra=(), motores=None):
        d = os.path.join(self.tmp, nome)
        mot = (M.criar() if motores is None else motores) + list(extra)
        sim = S.Simulador(d, semente, passo, mot, world=MUNDO)
        sim.iniciar()
        r = sim.rodar(anos)
        return d, r, sim


class TestDinamica(unittest.TestCase):
    def test_cobertura(self):
        self.assertFalse(DIN.vazia)
        self.assertTrue(all(x['id'] in DIN.longev for x in MUNDO.catalogo['races'] + MUNDO.catalogo['subraces']))
        self.assertEqual(set(DIN.longev.values()) - set(DIN.classes), set())
        self.assertTrue(all(r['id'] in DIN.relogios for r in MUNDO.t['relogio']))

    def test_idade_e_morte(self):
        self.assertEqual(DIN.idade_inicial('npc.x', '63', 'humana'), 63)
        self.assertEqual(DIN.idade_inicial('npc.x', 'cerca de 240', 'longa'), 240)
        a = DIN.idade_inicial('npc.x', 'idosa (faixa, sem número; ASSUMINDO)', 'humana')
        self.assertTrue(65 <= a <= 90)
        self.assertEqual(a, DIN.idade_inicial('npc.x', 'idosa', 'humana'))      # determinístico
        self.assertIsNone(DIN.idade_inicial('npc.x', '?', 'sem_idade'))
        self.assertEqual(DIN.morte_pm('sem_idade', 500), 0)
        self.assertLess(DIN.morte_pm('humana', 30), DIN.morte_pm('humana', 80))
        self.assertEqual(DIN.morte_pm('humana', 200), 1000)

    def test_estado_inicial(self):
        e = ES.Estado.do_molde(MUNDO, DIN)
        self.assertEqual(sum(1 for i in e.ent if i.startswith('posto.')), 110)
        self.assertEqual(sum(1 for i in e.ent if i.startswith('par.')), 64)
        self.assertEqual(sum(1 for i in e.ent if i.startswith('event.')), 62)
        self.assertEqual(ES.Estado.do_molde(MUNDO, DIN).hash(), e.hash())


class TestCemAnos(Base):
    @classmethod
    def setUpClass(cls):
        cls.tmp0 = tempfile.mkdtemp(prefix='myth4c-')
        sim = S.Simulador(os.path.join(cls.tmp0, 'c'), 1, 10, M.criar(), world=MUNDO)
        sim.iniciar(); cls.r = sim.rodar(100); cls.sim = sim; cls.d = os.path.join(cls.tmp0, 'c')
        cls.D = deltas(cls.d)

    @classmethod
    def tearDownClass(cls):
        shutil.rmtree(cls.tmp0, True)

    def test_aceite_100_anos_sem_erro(self):
        self.assertEqual(self.r['resultado'], 'OK'); self.assertEqual(self.r['blocos'], 10)
        txt = ler(os.path.join(self.d, 'invariantes.txt'))
        self.assertNotIn('FALHOU', txt)
        for cod in ('I-01', 'I-03', 'I-04', 'I-05', 'I-06', 'I-07', 'I-11', 'I-13', 'I-14', 'I-21'):
            self.assertIn(cod + '|E|ok', txt)
        self.assertIn('I-15|A|ok', txt)

    def test_cada_motor_agiu(self):
        t = {}
        for x in self.D:
            t[x['tipo']] = t.get(x['tipo'], 0) + 1
        for k in ('criacao', 'evento_ocorrido', 'passo_de_cadeia', 'fim_de_cadeia', 'tratado', 'disputa_aberta'):
            self.assertGreater(t.get(k, 0), 0, k)
        campos = {x['campo'] for x in self.D if x['tipo'] == 'campo'}
        for k in ('vivo', 'saude', 'titular', 'progresso', 'estourado', 'tensao', 'relacao', 'governante', 'lideranca'):
            self.assertIn(k, campos, k)

    def test_toda_mudanca_tem_causa_e_simulado(self):
        self.assertTrue(all(x['origem'] == 'SIMULADO' and x['causa']['ids'] and x['causa']['regra'] for x in self.D))

    def test_replay_reconstroi_o_estado(self):
        self.assertEqual(S.replay(MUNDO, self.D, DIN).hash(), self.sim.estado.hash())

    def test_npcs_simulados_validos(self):
        racas = {x['id'] for x in MUNDO.catalogo['races']}
        novos = [x for x in self.D if x['tipo'] == 'criacao']
        self.assertTrue(novos)
        nomes = [x['estado_novo']['nome'] for x in novos]
        base = {r['nome'] for r in MUNDO.t['npc']}; prim = {n.split()[0] for n in base}
        self.assertEqual(len(nomes), len(set(nomes)))
        self.assertFalse(set(nomes) & base); self.assertFalse({n.split()[0] for n in nomes} & prim)
        self.assertTrue(all(x['estado_novo']['povo'] in racas and x['estado_novo']['origem_npc'] == 'nomeacao' for x in novos))

    def test_misterio_nao_se_resolve(self):
        self.assertFalse([x for x in self.D if x['tipo'] == 'tentativa_de_resolver_misterio'])

    def test_morto_nao_ocupa_posto(self):
        e = self.sim.estado
        for k, p in e.ent.items():
            if k.startswith('posto.') and p['titular'] != '—':
                self.assertTrue(all(e.vivo(x) for x in p['titular'].split(';')), k)


class TestDeterminismoF4(Base):
    def test_I02_I19_I22_com_motores_reais(self):
        a, _, _ = self.rodar('a', 40); b, _, _ = self.rodar('b', 40)
        self.assertEqual(INV.comparar_rodadas(a, b), [])
        c, _, _ = self.rodar('c', 40, semente=2)
        self.assertNotEqual(INV.comparar_rodadas(a, c), [])
        p1, _, _ = self.rodar('p1', 30, passo=1); p10, _, _ = self.rodar('p10', 30, passo=10)
        self.assertEqual(INV.comparar_rodadas(p1, p10), [])
        meio, _, _ = self.rodar('meio', 20)
        sim = S.Simulador(meio, passo_anos=10, motores=M.criar(), world=MUNDO); sim.retomar(); sim.rodar(20)
        tudo, _, _ = self.rodar('tudo', 40)
        self.assertEqual(INV.comparar_rodadas(tudo, meio), [])
        self.assertEqual(ler(os.path.join(tudo, 'cronica.jsonl')), ler(os.path.join(meio, 'cronica.jsonl')))


class Mata:
    """Teste: mata um NPC no primeiro dia (roda antes dos motores: nome em ordem alfabética)."""
    nome = 'aa-mata'; frequencia = 'anual'

    def __init__(self, alvo):
        self.alvo = alvo

    def executar(self, ctx):
        if ctx.estado.vivo(self.alvo):
            return [dict(tipo='campo', entidade=self.alvo, campo='vivo', estado_novo=False,
                         causa=dict(ids=[self.alvo], regra='teste-morte'))]


class MataTarde(Mata):
    nome = 'zz-mata-tarde'    # depois da sucessão, no último ano: deixa um posto com titular morto (viola I-04)

    def executar(self, ctx):
        if ctx.dia_mundo >= 2 * 365:
            return Mata.executar(self, ctx)


class RompeDireto:
    nome = 'zz-par'; frequencia = 'anual'

    def executar(self, ctx):
        k = next(i for i in sorted(ctx.estado.ent) if i.startswith('par.') and ctx.estado.ent[i]['relacao'] == 'aliança')
        if ctx.estado.ent[k]['relacao'] == 'aliança':
            return [dict(tipo='campo', entidade=k, campo='relacao', estado_novo='rivalidade',
                         causa=dict(ids=[k], regra='defeito'))]


class TestSucessao(Base):
    def test_herdeiro_assume_a_coroa(self):
        d, r, sim = self.rodar('h', 1, extra=[Mata('npc.odile-medida-cheia')], motores=[M.Demografia(DIN), M.Sucessao(DIN)])
        self.assertEqual(r['resultado'], 'OK')
        p = sim.estado.ent['posto.npc.odile-medida-cheia']
        self.assertEqual(p['titular'], 'npc.tomlin-horta-larga')
        self.assertIn('governante', sim.estado.ent['npc.tomlin-horta-larga']['papeis'])
        regras = [x['causa']['regra'] for x in sim.crono.deltas if x['entidade'] == 'posto.npc.odile-medida-cheia']
        self.assertIn('sucessao-herdeiro', regras)

    def test_vacancia_e_nomeacao_de_simulado(self):
        alvo = 'npc.odile-medida-cheia'
        e0 = ES.Estado.do_molde(MUNDO, DIN)
        # mata todos os vivos do reino de Azeroth que poderiam herdar: força vacância longa
        reino = e0.ent[alvo]['reino']
        todos = [i for i, v in e0.ent.items() if i.startswith('npc.') and v['reino'] == reino and v['vivo']]

        class MataTodos(Mata):
            nome = 'aa-mata-todos'
            def executar(self, ctx):
                return [dict(tipo='campo', entidade=i, campo='vivo', estado_novo=False, causa=dict(ids=[i], regra='teste-morte'))
                        for i in todos if ctx.estado.vivo(i)]
        d, r, sim = self.rodar('v', 5, extra=[MataTodos(alvo)], motores=[M.Sucessao(DIN)])
        self.assertNotEqual(r['resultado'], 'FALHA')      # F6.2: a morte em massa do reino gera alerta (I-16 a I-18), nunca falha
        self.assertTrue({x[0] for x in sim.alert_total} <= {'I-16', 'I-17', 'I-18'})
        ds = sim.crono.deltas
        self.assertTrue(any(x['causa']['regra'] == 'vacancia' for x in ds))
        self.assertTrue(any(x['tipo'] == 'criacao' for x in ds))          # prazo de 3 anos passou: nasceu NPC simulado


class TestFalhasF4(Base):
    def test_I04_titular_morto_sem_sucessao_falha(self):
        _, r, sim = self.rodar('i4', 3, extra=[MataTarde('npc.odile-medida-cheia')], motores=[M.Sucessao(DIN)])
        self.assertEqual(r['resultado'], 'FALHA')
        self.assertIn('I-04|E|FALHOU', ler(os.path.join(sim.saida, 'invariantes.txt')))

    def test_I11_par_aliado_nao_vira_rival_direto(self):
        _, r, sim = self.rodar('i11', 2, extra=[RompeDireto()], motores=[])
        self.assertEqual(r['resultado'], 'FALHA')
        self.assertIn('I-11|E|FALHOU', ler(os.path.join(sim.saida, 'invariantes.txt')))

    def test_dinamica_vazia_motores_calados(self):
        raiz = os.path.join(self.tmp, 'raiz')
        os.makedirs(os.path.join(raiz, 'dinamica'))
        vazia = D.Dinamica(raiz)
        self.assertTrue(vazia.vazia)
        mot = [M.Demografia(vazia), M.Sucessao(vazia), M.Relogios(vazia), M.Eventos(vazia), M.Politica(vazia)]
        _, r, _ = self.rodar('vz', 20, motores=mot)
        self.assertEqual(r['deltas'], 1)


if __name__ == '__main__':
    unittest.main()
