"""Testes integrados da F7 (fronteira, diplomacia, guerra e tramas já existentes).
Rodar na raiz de Mythralis: python3 -m unittest discover -s motor/testes -v"""
import os, sys, shutil, tempfile, unittest
from types import SimpleNamespace
AQUI = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(AQUI, '..'))
import carregador as C, dinamica as D, estado as ES, invariantes as INV, motores as M, simulador as S
from motores.guerra import Guerra

MUNDO = C.carregar()
DIN = D.Dinamica()


def delta(**campos):
    d = dict(id='d900001', dia_mundo=10, era='era.002', tipo='registro', entidade='mundo.mythralis',
             campo=None, estado_anterior=None, estado_novo=None, agentes=[], quem_sabe=[],
             causa=dict(ids=['mundo.mythralis'], regra='teste-f7'), certeza='confirmado', origem='SIMULADO')
    d.update(campos)
    return d


class TestInvariantesF7(unittest.TestCase):
    def setUp(self):
        self.estado = ES.Estado.do_molde(MUNDO, DIN)
        self.estado.mundo = {'era': 'era.002', 'regime': 'poroso'}
        self.front = next(k for k in sorted(self.estado.ent) if k.startswith('front.'))
        self.par = self.estado.ent[self.front]
        self.par['tensao'] = max(300, DIN.guerra.get('guerra_tensao_min', 0))
        self.ids = set()

    def viola(self, d):
        return INV.checar_delta(d, self.estado, 0, self.ids, MUNDO, MUNDO.catalogo, DIN)

    def transicao(self, de, para, regra='teste-relacao'):
        self.par['relacao'] = de
        return delta(tipo='campo', entidade=self.front, campo='relacao', estado_anterior=de,
                     estado_novo=para, causa=dict(ids=[self.front], regra=regra))

    def test_i23_transicoes_de_fronteira_validas(self):
        for de, para in (('neutra', 'neutra'), ('neutra', 'tratado'), ('neutra', 'ruptura'),
                         ('tratado', 'tratado'), ('tratado', 'neutra'), ('tratado', 'ruptura'),
                         ('ruptura', 'ruptura'), ('ruptura', 'neutra'), ('ruptura', 'tratado'),
                         ('ruptura', 'guerra'), ('guerra', 'guerra'), ('guerra', 'neutra'), ('guerra', 'tratado')):
            with self.subTest(de=de, para=para):
                d = self.transicao(de, para, 'guerra-de-fronteira' if para == 'guerra' else 'teste-relacao')
                self.assertNotIn('I-23', {v[0] for v in self.viola(d)})

    def test_i23_bloqueia_neutra_direto_para_guerra(self):
        d = self.transicao('neutra', 'guerra', 'guerra-de-fronteira')
        self.assertIn('I-23', {v[0] for v in self.viola(d)})

    def test_i24_guerra_exige_ruptura_coroas_firmes_e_tensao(self):
        d = delta(tipo='guerra', entidade=self.front, causa=dict(ids=[self.front], regra='guerra-de-fronteira'))
        self.assertIn('I-24', {v[0] for v in self.viola(d)})
        self.par['relacao'] = 'ruptura'
        self.par['tensao'] = 0
        self.assertIn('I-24', {v[0] for v in self.viola(d)})
        self.par['tensao'] = DIN.guerra.get('guerra_tensao_min', 0)
        crowns = ['crown.' + r[8:] for r in self.par['reinos']]
        self.estado.ent[crowns[0]]['estado'] = 'caida'
        self.assertIn('I-24', {v[0] for v in self.viola(d)})

    def test_i24_queda_exige_guerra_e_coroa_nao_restaura(self):
        kingdom = self.par['reinos'][0]
        crown = 'crown.' + kingdom[8:]
        causa = dict(ids=[self.front, crown], regra='queda-de-coroa-em-guerra')
        d = delta(tipo='queda_de_coroa', entidade=crown, causa=causa)
        self.assertIn('I-24', {v[0] for v in self.viola(d)})
        self.par['relacao'] = 'guerra'
        self.assertNotIn('I-24', {v[0] for v in self.viola(d)})
        self.estado.ent[crown]['estado'] = 'caida'
        restauracao = delta(tipo='campo', entidade=crown, campo='estado', estado_anterior='caida',
                            estado_novo='firme', causa=causa)
        self.assertIn('I-24', {v[0] for v in self.viola(restauracao)})

    def test_i25_era_inicial_e_gatilhos(self):
        estado = ES.Estado.do_molde(MUNDO, DIN)
        d0 = delta(tipo='era', entidade='mundo', estado_anterior={'era': None, 'regime': None},
                   estado_novo=dict(era='era.001', nome='Fronteiras Seladas', regime='selado'),
                   causa=dict(ids=['mundo.mythralis'], regra='inicio-da-rodada'))
        self.assertNotIn('I-25', {v[0] for v in INV.checar_delta(d0, estado, 0, set(), MUNDO, MUNDO.catalogo, DIN)})
        estado.mundo = {'era': 'era.001', 'regime': 'selado'}
        estado.eras['era.001'] = dict(nome='Fronteiras Seladas', regime='selado', dia_inicio=0)
        bad = delta(tipo='era', entidade='mundo', estado_anterior={'era': 'era.001', 'regime': 'selado'},
                    estado_novo=dict(era='era.002', nome='Sem gatilho', regime='poroso'),
                    causa=dict(ids=['mundo.mythralis'], regra='data-inventada'))
        self.assertIn('I-25', {v[0] for v in INV.checar_delta(bad, estado, 0, set(), MUNDO, MUNDO.catalogo, DIN)})


class TestGuerraMesmoCiclo(unittest.TestCase):
    def test_npc_morto_na_baixa_nao_assina_paz_no_mesmo_ciclo(self):
        estado = ES.Estado.do_molde(MUNDO, DIN)
        estado.mundo = {'era': 'era.003', 'regime': 'aberto'}
        par_id = next(k for k in sorted(estado.ent) if k.startswith('front.'))
        par = estado.ent[par_id]
        reino = par['reinos'][0]
        candidatos = sorted(i for i, v in estado.ent.items() if i.startswith('npc.') and v.get('reino') == reino
                            and v.get('vivo') is True and (v.get('principal') or 'general' in str(v.get('papeis', '')).split(';')))
        self.assertTrue(candidatos)
        vitima = candidatos[0]
        cidade = sorted(i for i, v in estado.ent.items() if i.startswith('city.') and v.get('reino') == reino)[0]
        estado.ent[cidade]['governante'] = vitima
        par['relacao'] = 'guerra'
        par['relacao_dia'] = 0
        par['tensao'] = 300
        for q in par['reinos']:
            estado.ent['crown.' + q[8:]]['estado'] = 'firme'

        class RNG:
            def __init__(self):
                self.valores = iter((0, 999, 999, 999))
            def randrange(self, n):
                return next(self.valores) if n == 1000 else 0

        din = SimpleNamespace(vazia=False,
                              guerra={'guerra_baixa_min_vivos': 1, 'guerra_baixa_pm': 1000, 'queda_pm': 0,
                                      'queda_min_anos': 3, 'guerra_anos': 6, 'guerra_pos_tensao': 150},
                              dipl={'dipl_tratado_anos': 30})
        ctx = SimpleNamespace(estado=estado, dia_mundo=6 * 365, rng=RNG())
        deltas = Guerra(din).executar(ctx)
        baixa = next(d for d in deltas if d.get('tipo') == 'campo' and d.get('entidade') == vitima and d.get('campo') == 'vivo')
        paz = next(d for d in deltas if d.get('tipo') == 'tratado' and d.get('causa', {}).get('regra') == 'paz-apos-guerra')
        self.assertIs(baixa['estado_novo'], False)
        self.assertNotIn(vitima, paz['agentes'])


class TestTramasF7(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.tmp = tempfile.mkdtemp(prefix='myth-f7-')
        cls.saida = os.path.join(cls.tmp, 'f7-150a')
        cls.sim = S.Simulador(cls.saida, 1, 10, M.criar(), world=MUNDO)
        cls.sim.iniciar()
        cls.resultado = cls.sim.rodar(150)

    @classmethod
    def tearDownClass(cls):
        shutil.rmtree(cls.tmp, True)

    def test_rodada_f7_sem_falhas_e_invariantes_novos_ok(self):
        self.assertNotEqual(self.resultado['resultado'], 'FALHA')
        with open(os.path.join(self.saida, 'invariantes.txt'), encoding='utf-8') as f:
            inv = f.read()
        for cod in ('I-23', 'I-24', 'I-25'):
            self.assertIn(cod + '|E|ok', inv)

    def test_guerra_abre_regime_e_cadeias_de_eventos_mantem_causa(self):
        ds = self.sim.crono.deltas
        incidentes = [d for d in ds if d['tipo'] == 'incidente_fronteira']
        porosas = [d for d in ds if d['tipo'] == 'era' and d['estado_novo'].get('regime') == 'poroso']
        quebras = [d for d in ds if d['tipo'] == 'quebra_de_contrato']
        self.assertTrue(any(d['tipo'] == 'guerra' for d in ds))
        self.assertTrue(any(d['tipo'] == 'era' and d['estado_novo'].get('regime') == 'aberto' for d in ds))
        self.assertTrue(incidentes)
        self.assertTrue(porosas)
        self.assertEqual(porosas[0]['causa']['regra'], 'primeiro-incidente-de-fronteira')
        self.assertTrue(quebras)
        self.assertTrue(all(d['causa']['ids'] and d['origem'] == 'SIMULADO' for d in quebras))
        passos = [d for d in ds if d['tipo'] == 'passo_de_cadeia']
        self.assertTrue(passos, 'as tramas F7 reutilizam cadeias de eventos do molde')
        self.assertTrue(all(d['causa']['ids'] and d['origem'] == 'SIMULADO' for d in passos))
        self.assertTrue(any(d['tipo'] == 'fim_de_cadeia' for d in ds))
        consequencias = [d for d in ds if d['tipo'] == 'consequencia_sem_agente']
        self.assertTrue(consequencias)
        self.assertTrue(all(d['causa']['regra'] == 'se-ninguem-age' and d['certeza'] == 'alegado'
                            for d in consequencias))


if __name__ == '__main__':
    unittest.main()
