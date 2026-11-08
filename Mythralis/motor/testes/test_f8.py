"""Testes da F8: níveis de relevância (D-M14) e povos, alianças entre raças e sub-raças (D-M15).
Rodar na raiz de Mythralis: python3 -m unittest discover -s motor/testes -v"""
import os, sys, shutil, tempfile, unittest, json
AQUI = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(AQUI, '..'))
import carregador as C, dinamica as D, estado as ES, invariantes as INV, motores as M, simulador as S, niveis as NV

MUNDO = C.carregar()
DIN = D.Dinamica()


def delta(**campos):
    d = dict(id='d900001', dia_mundo=10, era='era.001', tipo='registro', entidade='mundo.mythralis', campo=None,
             estado_anterior=None, estado_novo=None, agentes=[], quem_sabe=[], importancia=3,
             causa=dict(ids=['mundo.mythralis'], regra='teste-f8'), certeza='confirmado', origem='SIMULADO')
    d.update(campos)
    return d


class TestPovosDoMolde(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.e = ES.Estado.do_molde(MUNDO, DIN)
        cls.lacos = {k: v for k, v in cls.e.ent.items() if k.startswith('laco.')}
        cls.pactos = {k: v for k, v in cls.e.ent.items() if k.startswith('pacto.')}

    def test_toda_cidade_do_mundo_tem_lacos(self):
        cidades = {c['id'] for c in MUNDO.t['cidade']}
        self.assertEqual(len(cidades), 46)
        self.assertEqual({v['cidade'] for v in self.lacos.values()}, cidades)

    def test_lacos_nascem_neutros_e_sem_mesma_linhagem(self):
        sub = {s['id']: s['parentRaceId'] for s in MUNDO.catalogo['subraces']}
        for k, v in self.lacos.items():
            self.assertEqual(v['relacao'], 'neutra'); self.assertEqual(v['tensao'], DIN.povos['povos_tensao_base'])
            self.assertNotEqual(sub.get(v['a'], v['a']), sub.get(v['b'], v['b']), k)
            self.assertEqual(v['reino'], MUNDO.reino_de(v['cidade']))

    def test_existem_pares_de_raca_e_de_subraca(self):
        racas = {r['id'] for r in MUNDO.catalogo['races']}
        self.assertTrue(any(v['a'] in racas and v['b'] in racas for v in self.lacos.values()))
        self.assertTrue(any(v['a'] not in racas or v['b'] not in racas for v in self.lacos.values()))

    def test_pacto_so_onde_o_par_convive_em_cidades_suficientes(self):
        self.assertTrue(self.pactos)
        for k, p in self.pactos.items():
            self.assertGreaterEqual(len(p['cidades']), DIN.povos['povos_pacto_min_cidades']); self.assertEqual(p['estado'], 'nenhum')
            for c in p['cidades']:
                self.assertIn('laco.%s@%s+%s' % (c, p['a'], p['b']), self.e.ent)

    def test_molde_nao_muda(self):
        self.assertEqual(MUNDO.hash_molde, C.carregar().hash_molde)


class TestInvariantesPovos(unittest.TestCase):
    def setUp(self):
        self.e = ES.Estado.do_molde(MUNDO, DIN)
        self.e.mundo = {'era': 'era.001', 'regime': 'selado'}
        self.laco = next(k for k, v in sorted(self.e.ent.items()) if k.startswith('laco.'))
        self.l = self.e.ent[self.laco]; self.cid = self.l['cidade']
        self.gov = next(g for g in self.e.ent[self.cid]['governante'].replace(',', ';').split(';') if g in self.e.ent and self.e.vivo(g))

    def chk(self, d):
        return [x[0] for x in INV.checar_delta(d, self.e, 0, set(), MUNDO, MUNDO.catalogo, DIN)]

    def test_alianca_sem_governante_viola_I26(self):
        d = delta(tipo='campo', entidade=self.laco, campo='relacao', estado_anterior='neutra', estado_novo='alianca', importancia=1,
                  causa=dict(ids=[self.laco], regra='t'))
        self.assertIn('I-26', self.chk(d))
        self.assertNotIn('I-26', self.chk(dict(d, agentes=[self.gov])))

    def test_transicao_invalida_viola_I26(self):
        d = delta(tipo='campo', entidade=self.laco, campo='relacao', estado_anterior='alianca', estado_novo='rivalidade', importancia=1,
                  causa=dict(ids=[self.laco], regra='t'))
        self.assertIn('I-26', self.chk(d))

    def test_pacto_sem_liacos_viola_I27(self):
        pk = next(k for k in sorted(self.e.ent) if k.startswith('pacto.'))
        d = delta(tipo='alianca_de_povos', entidade=pk, causa=dict(ids=[pk], regra='t'))
        self.assertIn('I-27', self.chk(d))
        p = self.e.ent[pk]
        for c in p['cidades']:
            self.e.ent['laco.%s@%s+%s' % (c, p['a'], p['b'])]['relacao'] = 'alianca'
        self.e.mundo['regime'] = 'poroso'
        self.assertNotIn('I-27', self.chk(d))
        self.assertIn('I-27', self.chk(delta(tipo='fim_de_alianca_de_povos', entidade=pk, causa=dict(ids=[pk], regra='t'))))


class TestNiveis(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.N = NV.Niveis(MUNDO)
        cls.capital = MUNDO.t['reino'][0]['capital']
        cls.heroi = next(n['id'] for n in MUNDO.t['npc'] if 'heroi' in n['papeis'].split(';') and n['cidade'] not in cls.N.capitais)

    def test_marcos_sao_nivel_1(self):
        for t in ('era', 'guerra', 'queda_de_coroa', 'alianca_de_povos', 'fim_de_alianca_de_povos'):
            self.assertEqual(self.N.nivel(delta(tipo=t, entidade='mundo' if t == 'era' else 'pacto.x+y')), 1)
        self.assertEqual(self.N.nivel(delta(tipo='tratado', entidade='front.a+b')), 1)

    def test_heroi_nivel_2_e_bloqueio_nivel_3(self):
        self.assertEqual(self.N.nivel(delta(tipo='campo', entidade=self.heroi, campo='vivo', estado_novo=False)), 2)
        self.assertEqual(self.N.nivel(delta(tipo='bloqueio_de_rota', entidade='route.x')), 3)

    def test_laco_com_mediador_nivel_2_sem_mediador_nivel_3(self):
        k = 'laco.%s@a+b' % self.capital
        self.assertEqual(self.N.nivel(delta(tipo='laco_aliado', entidade=k, agentes=[self.heroi])), 2)
        self.assertEqual(self.N.nivel(delta(tipo='laco_aliado', entidade=k, agentes=[])), 3)
        self.assertEqual(self.N.cidade(delta(entidade=k)), self.capital)

    def test_governante_da_capital_nivel_1_outras_cidades_nivel_2(self):
        outra = next(c['id'] for c in MUNDO.t['cidade'] if c['id'] not in self.N.capitais)
        mk = lambda c: delta(tipo='campo', entidade=c, campo='governante', estado_anterior='x', estado_novo='y')
        self.assertEqual(self.N.nivel(mk(self.capital)), 1); self.assertEqual(self.N.nivel(mk(outra)), 2)

    def test_citados_promove_causa_de_nivel_1(self):
        n3 = delta(id='d1', tipo='campo', entidade='clock.x', campo='estourado', importancia=2)
        n1 = delta(id='d2', tipo='guerra', entidade='front.a+b', causa=dict(ids=['clock.x'], regra='t'))
        self.assertIn('clock.x', NV.Niveis.citados([n3, n1], self.N.nivel))


class TestRodadaPovos(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.tmp = tempfile.mkdtemp(prefix='myth8-')
        cls.dirs = []
        for nome, passo in (('a', 10), ('b', 10), ('c', 1)):
            d = os.path.join(cls.tmp, nome)
            sim = S.Simulador(d, 7, passo, M.criar(), world=MUNDO); sim.iniciar(); r = sim.rodar(60)
            cls.dirs.append((d, r, sim))

    @classmethod
    def tearDownClass(cls):
        shutil.rmtree(cls.tmp, True)

    def test_rodada_ok_e_invariantes_novos_passam(self):
        d, r, _ = self.dirs[0]
        self.assertEqual(r['resultado'], 'OK')
        txt = open(os.path.join(d, 'invariantes.txt'), encoding='utf-8').read()
        for i in ('I-26', 'I-27', 'I-28'):
            self.assertIn('%s|E|ok|' % i, txt)

    def test_povos_produz_aliancas_laco_a_laco(self):
        _, _, sim = self.dirs[0]
        tipos = {d['tipo'] for d in sim.crono.deltas}
        self.assertIn('laco_aliado', tipos)
        self.assertTrue(all(d['importancia'] <= 2 for d in sim.crono.deltas if d['tipo'].startswith('laco_')))

    def test_determinismo_e_passo_neutro(self):
        a, b, c = (x[0] for x in self.dirs)
        self.assertEqual(INV.comparar_rodadas(a, b), [])
        self.assertEqual(INV.comparar_rodadas(a, c), [])

    def test_relatorios_por_nivel_e_cidade(self):
        d = self.dirs[0][0]
        txt = open(os.path.join(d, 'niveis.md'), encoding='utf-8').read()  # noqa
        self.assertIn('Nível 1', txt); self.assertIn('Nível 2', txt); self.assertIn('Nível 3', txt)
        linhas = [l for l in txt.split('## Total por cidade')[1].splitlines() if l.startswith('| ') and not l.startswith(('| cidade', '|---'))]
        self.assertEqual(len(linhas), 46)
        self.assertTrue(os.path.exists(os.path.join(d, 'metricas_cidades.csv')))


if __name__ == '__main__':
    unittest.main()
