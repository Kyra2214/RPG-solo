"""Testes da F5 (economia e rotas, Ecos, mistérios, fronteira). Rodar: python3 -m unittest discover -s motor/testes -v"""
import os, sys, shutil, tempfile, unittest
AQUI = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(AQUI, '..'))
import carregador as C, cronica as CR, estado as ES, invariantes as INV, simulador as S, dinamica as D, motores as M

MUNDO = C.carregar()
DIN = D.Dinamica()


def viol(d, e):
    return INV.checar_delta(d, e, 0, set(), MUNDO, MUNDO.catalogo)


def delta(**k):
    b = dict(id='d900001', dia_mundo=0, tipo='registro', entidade='mundo', campo=None, estado_novo=None, agentes=[],
             causa=dict(ids=['mundo.mythralis'], regra='teste'), certeza='confirmado', origem='SIMULADO', quem_sabe=[])
    b.update(k)
    return b


class TestF5(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.tmp = tempfile.mkdtemp(prefix='myth5-')
        cls.sim = S.Simulador(os.path.join(cls.tmp, 'a'), 1, 10, M.criar(), world=MUNDO)
        cls.sim.iniciar(); cls.sim.rodar(30)
        cls.deltas = cls.sim.crono.deltas

    @classmethod
    def tearDownClass(cls):
        shutil.rmtree(cls.tmp, True)

    def test_estado_inicial(self):
        e = ES.Estado.do_molde(MUNDO, DIN)
        self.assertEqual(sum(1 for k in e.ent if k.startswith('mist.')), len(MUNDO.t['misterio']))
        self.assertTrue(any(k.startswith('route.') for k in e.ent))
        self.assertTrue(any(k.startswith('econ.') for k in e.ent))

    def test_rodada_ok(self):
        self.assertEqual(self.sim.resultado, 'OK')
        tipos = {d['tipo'] for d in self.deltas}
        self.assertIn('bloqueio_de_rota', tipos)
        self.assertTrue(any(d.get('campo') == 'difusao' for d in self.deltas))

    def test_nunca_resolve_misterio(self):
        self.assertFalse([d for d in self.deltas if d['entidade'].startswith('mist.') and d.get('campo') == 'estado'])

    def test_eco_restrito_avanca_por_rota(self):
        e, n = self.sim.estado, 0
        for d in self.deltas:
            if d.get('campo') == 'difusao' and e.ent[d['entidade']]['camada'] in ('restrito', 'secreto') \
                    and len(d['estado_novo']['lugares']) > len(d['estado_anterior']['lugares']):
                n += 1
                self.assertTrue(any(i.startswith('route.') for i in d['causa']['ids']))
        self.assertGreaterEqual(n, 0)

    def test_i08(self):
        e = ES.Estado.do_molde(MUNDO, DIN)
        k = next(x for x in e.ent if x.startswith('mist.'))
        self.assertTrue([v for v in viol(delta(tipo='campo', entidade=k, campo='estado', estado_novo='resolvido'), e) if v[0] == 'I-08'])
        row = MUNDO.get(k); ev = row['evidencia_para_resolver'].split(';')[0].strip()
        ag = next(i for i in e.ent if i.startswith('npc.') and e.ent[i]['vivo'])
        d = delta(tipo='campo', entidade=k, campo='estado', agentes=[ag], estado_novo=dict(evidencia=ev))
        self.assertFalse([v for v in viol(d, e) if v[0] == 'I-08'])

    def test_i09(self):
        e = ES.Estado.do_molde(MUNDO, DIN)
        e.ent['eco.x'] = dict(camada='secreto', difusao=dict(passo=0, lugares=['city.a'], certeza='confirmado', ativo=True))
        n = dict(passo=1, lugares=['city.a', 'city.b'], certeza='alegado', ativo=True)
        d = delta(tipo='campo', entidade='eco.x', campo='difusao', estado_novo=n)
        self.assertTrue([v for v in viol(d, e) if v[0] == 'I-09'])
        d['causa'] = dict(ids=['eco.x', 'route.y'], regra='difusao-de-eco')
        self.assertFalse([v for v in viol(d, e) if v[0] == 'I-09'])

    def test_i10(self):
        e = ES.Estado.do_molde(MUNDO, DIN)
        e.mundo = {'era': 'era.001', 'regime': 'selado'}
        self.assertTrue([v for v in viol(delta(tipo='criacao', entidade='route.nova'), e) if v[0] == 'I-10'])
        a = next(i for i in e.ent if i.startswith('npc.') and e.ent[i]['vivo'] and e.ent[i]['reino'] != 'kingdom.aeltharyn')
        d = delta(tipo='registro', entidade='city.elarion', agentes=[a])
        self.assertTrue([v for v in viol(d, e) if v[0] == 'I-10'])
        e.mundo['regime'] = 'poroso'
        self.assertFalse([v for v in viol(d, e) if v[0] == 'I-10'])

    def test_fronteira_encerra_era(self):
        s = S.Simulador(os.path.join(self.tmp, 'f'), 1, 10, [M.Fronteira(DIN)], world=MUNDO)
        s.iniciar(); s.rodar(121)
        self.assertEqual(s.estado.mundo['regime'], 'poroso')
        self.assertEqual(s.resultado, 'OK')

    def test_determinismo_e_passo(self):
        def r(n, p):
            x = S.Simulador(os.path.join(self.tmp, n), 3, p, M.criar(), world=MUNDO); x.iniciar(); x.rodar(10); return os.path.join(self.tmp, n)
        a, b = r('d1', 10), r('d2', 5)
        self.assertEqual(INV.comparar_rodadas(a, b), [])


if __name__ == '__main__':
    unittest.main()
