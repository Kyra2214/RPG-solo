"""Testes da F3. Rodar: python3 -m unittest discover -s motor/testes -v   (na raiz de Mythralis)
Cobre I-01, I-02, I-19, I-21, I-22, I-03, I-07 e o replay/hash da crônica, com um motor-sonda (só de teste)."""
import os, sys, json, shutil, tempfile, unittest
AQUI = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(AQUI, '..'))
import carregador as C, cronica as CR, estado as ES, invariantes as INV, simulador as S

MUNDO = C.carregar()


class Sonda:
    """Motor de teste: mortes, relógios, incidentes, nascimentos e boatos, tudo por rng determinístico."""
    nome = 'sonda'; frequencia = 'anual'

    def executar(self, ctx):
        out, e = [], ctx.estado
        vivos = sorted(i for i, a in e.ent.items() if i.startswith('npc.') and a.get('vivo') is True)
        if vivos and ctx.rng.random() < 0.3:
            n = ctx.rng.choice(vivos)
            out.append(dict(tipo='campo', entidade=n, campo='vivo', estado_novo=False,
                            causa=dict(ids=[n], regra='sonda-morte'), certeza='confirmado'))
        rel = sorted(i for i, a in e.ent.items() if i.startswith('clock.') and not a['estourado'])
        if rel:
            r = ctx.rng.choice(rel); a = e.ent[r]
            if a['progresso'] < a['segmentos']:
                out.append(dict(tipo='campo', entidade=r, campo='progresso', estado_novo=a['progresso'] + 1,
                                causa=dict(ids=[r], regra='sonda-relogio')))
            else:
                out.append(dict(tipo='campo', entidade=r, campo='estourado', estado_novo=True,
                                causa=dict(ids=[r], regra='sonda-estouro')))
        if ctx.rng.random() < 0.25:
            k = ctx.rng.choice(sorted(MUNDO.reinos))
            out.append(dict(tipo='incidente_fronteira', entidade=k, causa=dict(ids=[k], regra='sonda-incidente'),
                            certeza='alegado'))
        if ctx.rng.random() < 0.4:
            k = ctx.rng.choice(sorted(MUNDO.reinos))
            out.append(dict(tipo='criacao', entidade='npc.sim-%05d' % (e.n_criados + 1),
                            estado_novo=dict(vivo=True, reino=k, cidade='—', faccao='—', cargo='—', papeis='—',
                                             principal=False, povo='humano', subraca='—', origem_npc='nascimento'),
                            causa=dict(ids=[k], regra='sonda-nascimento')))
        return out


class SondaDiaria:
    nome = 'sonda-diaria'; frequencia = 'diaria'

    def executar(self, ctx):
        if ctx.rng.random() < 1 / 400:
            return [dict(tipo='boato', entidade='mundo', causa=dict(ids=['mundo.mythralis'], regra='sonda-boato'),
                         certeza='rumor')]


class SondaMortoAge:
    """Defeito proposital: faz um NPC morto agir (viola I-03)."""
    nome = 'zz-morto-age'; frequencia = 'anual'

    def executar(self, ctx):
        mortos = sorted(i for i, a in ctx.estado.ent.items() if a.get('vivo') is False)
        if mortos and ctx.dia_mundo > 12 * 365:   # só no 2º bloco, para sobrar um snapshot do 1º
            return [dict(tipo='boato', entidade='mundo', agentes=[mortos[0]],
                         causa=dict(ids=['mundo.mythralis'], regra='defeito'))]


class SondaOriginal:
    nome = 'zz-original'; frequencia = 'anual'

    def executar(self, ctx):
        if ctx.dia_mundo > 0:
            return [dict(tipo='boato', entidade='mundo', origem='ORIGINAL',
                         causa=dict(ids=['mundo.mythralis'], regra='defeito'))]


def motores(): return [Sonda(), SondaDiaria()]


def ler(p):
    with open(p, encoding='utf-8') as f:
        return f.read()


class Base(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.mkdtemp(prefix='myth-')
        self.addCleanup(shutil.rmtree, self.tmp, True)

    def rodar(self, nome, anos, passo=10, semente=1, mot=None, sim_cls=S.Simulador, retomar=False):
        d = os.path.join(self.tmp, nome)
        sim = sim_cls(d, semente, passo, motores() if mot is None else mot, world=MUNDO)
        sim.retomar() if retomar else sim.iniciar()
        r = sim.rodar(anos)
        return d, r, sim


class TestMotorVazio(Base):
    def test_vazio(self):
        d, r, sim = self.rodar('v', 30, mot=[])
        self.assertEqual(r['resultado'], 'OK'); self.assertEqual(r['deltas'], 1); self.assertEqual(r['notificacoes'], 1)
        self.assertEqual(r['blocos'], 3)
        self.assertEqual(sim.estado.mundo, {'era': 'era.001', 'regime': 'selado'})

    def test_molde_no_estado(self):
        e = ES.Estado.do_molde(MUNDO)
        self.assertEqual(sum(1 for i in e.ent if i.startswith('npc.')), 233)
        self.assertEqual(sum(1 for i in e.ent if i.startswith('clock.')), 40)


class TestInvariantesEntreRodadas(Base):
    def test_I02_determinismo(self):
        a, _, _ = self.rodar('a', 40); b, _, _ = self.rodar('b', 40)
        self.assertEqual(INV.comparar_rodadas(a, b), [])
        c, _, _ = self.rodar('c', 40, semente=2)
        self.assertNotEqual(INV.comparar_rodadas(a, c), [])

    def test_I19_retomada(self):
        tudo, _, _ = self.rodar('tudo', 50)
        d, _, _ = self.rodar('meio', 25)
        sim = S.Simulador(d, passo_anos=10, motores=motores(), world=MUNDO); sim.retomar(); sim.rodar(25)
        self.assertEqual(INV.comparar_rodadas(tudo, d), [])
        cr = lambda x: ler(os.path.join(x, 'cronica.jsonl'))
        self.assertEqual(cr(tudo), cr(d))

    def test_I22_passo_neutro(self):
        r1, _, _ = self.rodar('p1', 30, passo=1); r10, _, _ = self.rodar('p10', 30, passo=10)
        r50, _, _ = self.rodar('p50', 30, passo=50)
        self.assertEqual(INV.comparar_rodadas(r1, r10), []); self.assertEqual(INV.comparar_rodadas(r10, r50), [])

    def test_replay_reconstroi_o_estado(self):
        d, _, sim = self.rodar('r', 40)
        cr = CR.Cronica.abrir(os.path.join(d, 'cronica.jsonl'))
        self.assertEqual(S.replay(MUNDO, cr.deltas).hash(), sim.estado.hash())
        self.assertEqual(cr.hash, sim.crono.hash)

    def test_cadeia_de_hash_detecta_adulteracao(self):
        d, _, _ = self.rodar('adu', 30)
        p = os.path.join(d, 'cronica.jsonl'); L = ler(p).split('\n')
        L[3] = L[3].replace('sonda', 'xonda', 1)
        with open(p, 'w', encoding='utf-8') as f:
            f.write('\n'.join(L))
        with self.assertRaises(CR.ErroCronica): CR.Cronica.abrir(p)


class TestNotificacoes(Base):
    def test_notificacao_para_importancia_3(self):
        d, _, sim = self.rodar('n', 40)
        notifs = [json.loads(l) for l in ler(os.path.join(d, 'notificacoes.jsonl')).splitlines()]
        altos = [x for x in sim.crono.deltas if x['importancia'] >= 3]
        self.assertTrue(len(altos) > 5)
        self.assertEqual(sorted(n['delta'] for n in notifs), sorted(x['id'] for x in altos))
        self.assertTrue(all(x['notificacao'] is None for x in sim.crono.deltas if x['importancia'] < 3))
        self.assertTrue(all(n['status'] == 'OK' for n in notifs))

    def test_I21_notificacao_faltando_falha(self):
        class SemNotif(S.Simulador):
            def _notificar(self, d, viol): return None
        d, r, _ = self.rodar('sn', 20, sim_cls=SemNotif)
        self.assertEqual(r['resultado'], 'FALHA')
        self.assertIn('I-21', ler(os.path.join(d, 'invariantes.txt')))


class TestFalhasParam(Base):
    def test_I03_morto_age_para_a_rodada(self):
        d, r, sim = self.rodar('m', 60, mot=motores() + [SondaMortoAge()])
        self.assertEqual(r['resultado'], 'FALHA')
        self.assertLess(r['dia'], 60 * 365)                      # parou no bloco com a falha
        txt = ler(os.path.join(d, 'saude.txt'))
        self.assertIn('|FALHA|', txt)
        self.assertTrue(os.listdir(os.path.join(d, 'snapshots')))  # snapshot do bloco anterior fica para depurar
        self.assertIn('I-03|E|FALHOU', ler(os.path.join(d, 'invariantes.txt')))

    def test_I07_origem_original_falha(self):
        _, r, _ = self.rodar('o', 20, mot=[SondaOriginal()])
        self.assertEqual(r['resultado'], 'FALHA')


class TestI01Molde(Base):
    def test_mexer_no_molde_falha(self):
        raiz = os.path.join(self.tmp, 'raiz')
        for sub in ('mundo', os.path.join('referencia', 'dados-brutos')):
            shutil.copytree(os.path.join(C.RAIZ, sub), os.path.join(raiz, sub))
        for pasta in os.listdir(os.path.join(C.RAIZ, 'reinos')):
            shutil.copytree(os.path.join(C.RAIZ, 'reinos', pasta, 'dados'), os.path.join(raiz, 'reinos', pasta, 'dados'))
        alvo = os.path.join(raiz, 'reinos', '05-azeroth', 'dados', 'fato.txt')

        class Vandalo:
            nome = 'vandalo'; frequencia = 'anual'
            def executar(self, ctx):
                if ctx.dia_mundo > 0:
                    with open(alvo, 'a', encoding='utf-8') as f:
                        f.write('# alterado\n')
        sim = S.Simulador(os.path.join(self.tmp, 'sai'), 1, 10, [Vandalo()], raiz=raiz, world=MUNDO)
        sim.iniciar(); r = sim.rodar(15)
        self.assertEqual(r['resultado'], 'FALHA')
        self.assertIn('I-01|E|FALHOU', ler(os.path.join(self.tmp, 'sai', 'invariantes.txt')))


if __name__ == '__main__':
    unittest.main()
