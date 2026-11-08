"""Simulador de Mythralis (F3: estado, crônica, replay, hash, importância, notificações, passo de 10 anos;
F4: dinâmica e motores de demografia, sucessão, relógios, eventos e política;
F5.1: economia e rotas; F5.2: Ecos; F5.3: mistérios; F5.4: fronteira; F6.3: modo lote em lote.py; F6.4: retomada +anos e CLI).

  python3 motor/simulador.py --anos 200 --passo 10 --semente 7
  python3 motor/simulador.py --retomar saidas/rodada-s7 --anos +100      # mais 100 anos (com --retomar, N e +N são iguais)
  python3 motor/simulador.py --retomar saidas/rodada-s7 --ate-ano 400    # até o ano 400 do mundo
  python3 motor/simulador.py --lote 20 --anos 500 --passo 10

O laço interno é sempre por DIA do mundo; o bloco (--passo, em anos) é só o ponto de parada e de conferência
(check de saúde -> saude.txt). Por isso o passo não muda a crônica (I-22).
"""
import os, sys, json, glob, random, argparse
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import carregador as C, calendario as CAL, cronica as CR, estado as ES, importancia as IM, invariantes as INV, dinamica as DIN, relatorio as REL

VERSAO = 'F8.2'
FREQ = ('diaria', 'lunar', 'sazonal', 'anual')


class ErroRetomada(Exception):
    pass


def hash_molde(raiz):
    pastas = sorted(glob.glob(os.path.join(raiz, 'reinos', '0*-*', 'dados')))
    return C.hash_molde(pastas + [os.path.join(raiz, 'mundo'), os.path.join(raiz, 'referencia', 'dados-brutos')])


def hash_dinamica(raiz):
    h = ''
    for f in sorted(glob.glob(os.path.join(raiz, 'dinamica', '*.txt'))):
        with open(f, encoding='utf-8') as g:
            h += os.path.basename(f) + CR.sha(g.read())
    return CR.sha(h)


def replay(world, deltas, din=None):
    """estado = molde + dinâmica + replay(crônica)."""
    s = ES.Estado.do_molde(world, din or DIN.Dinamica(C.RAIZ))
    for d in deltas:
        s.aplicar(d)
    return s


def frequencias(dia):
    f = ['diaria']
    if dia % 30 == 0: f.append('lunar')
    doy = CAL.dia_do_ano(dia)
    if doy in (1, 92, 183, 274): f.append('sazonal')
    if doy == 1: f.append('anual')
    return f


class Ctx:
    def __init__(self, dia, estado, rng, mundo):
        self.dia_mundo = dia; self.dia_do_ano = CAL.dia_do_ano(dia); self.ano = CAL.ano_base(dia)
        self.estado = estado; self.rng = rng; self.mundo = mundo; self.era = estado.mundo['era']


class Simulador:
    def __init__(self, saida, semente=1, passo_anos=None, motores=None, raiz=C.RAIZ, world=None):
        self.saida = saida; self.semente = semente; self._passo_auto = passo_anos is None; self.passo = max(1, int(passo_anos or 10)); self.raiz = raiz
        self.world = world or C.carregar(raiz)
        self.cals = CAL.construir(self.world)
        self.din = DIN.Dinamica(raiz)
        self.motores = sorted(motores or [], key=lambda m: m.nome)
        self.por_freq = {f: [m for m in self.motores if m.frequencia == f] for f in FREQ}
        self.crono = None; self.estado = None
        self.ids_delta = set(); self.notif_ids = set(); self.n_notif = 0
        self.viol_bloco = []; self.viol_total = []; self.alert_bloco = []; self.alert_total = []
        self.bloco_ini = 0; self.n_delta_ini = 0; self.n_notif_ini = 0
        self.resultado = 'OK'; self.blocos = 0
        self._reinos = REL.Reinos(self.world, []); self._enraizados = set()

    # ---- caminhos
    def p(self, nome): return os.path.join(self.saida, nome)

    # ---- início e retomada
    def iniciar(self):
        os.makedirs(self.saida, exist_ok=True)
        if os.path.exists(self.p('cronica.jsonl')):
            raise ErroRetomada('a rodada já existe; use retomar: ' + self.saida)
        cab = dict(versao_motor=VERSAO, esquema_cronica=1, mundo='mundo.mythralis', semente=self.semente,
                   hash_molde=hash_molde(self.raiz), hash_dinamica=hash_dinamica(self.raiz), ano_base=CAL.ANO_BASE)
        self.hash_molde0, self.hash_din0 = cab['hash_molde'], cab['hash_dinamica']
        self.crono = CR.Cronica.criar(self.p('cronica.jsonl'), cab)
        self.notif = open(self.p('notificacoes.jsonl'), 'w', encoding='utf-8', newline='\n')
        self.saude = open(self.p('saude.txt'), 'w', encoding='utf-8', newline='\n')
        self.saude.write('# esquema=1\nbloco|ano_ini|ano_fim|dia_ini|dia_fim|parcial|resultado|deltas|notificacoes|alertas|falhas|hash_cronica\n')
        self.estado = ES.Estado.do_molde(self.world, self.din)

    def retomar(self):
        with open(self.p('progresso.json'), encoding='utf-8') as f:
            pr = json.load(f)
        if pr.get('resultado') == 'FALHA':
            raise ErroRetomada('a rodada terminou em FALHA; corrija e rode de novo (o snapshot do bloco anterior está em snapshots/)')
        if self._passo_auto and pr.get('passo'):
            self.passo = int(pr['passo'])
        self.viol_total = [tuple(x) for x in pr.get('viol', [])]; self.alert_total = [tuple(x) for x in pr.get('alert', [])]
        self.crono = CR.Cronica.abrir(self.p('cronica.jsonl'), anexar=True)
        if self.crono.hash != pr['hash_cronica']:
            raise ErroRetomada('a crônica foi além do último bloco conferido (rodada interrompida)')
        cab = self.crono.cabecalho
        self.semente = cab['semente']; self.hash_molde0, self.hash_din0 = cab['hash_molde'], cab['hash_dinamica']
        if hash_molde(self.raiz) != self.hash_molde0 or hash_dinamica(self.raiz) != self.hash_din0:
            raise ErroRetomada('o molde ou a dinâmica mudaram desde o início da rodada (I-01)')
        self.estado = replay(self.world, self.crono.deltas, self.din)
        self.estado.proximo_dia = pr['proximo_dia']
        self._reinos = REL.Reinos(self.world, self.crono.deltas)
        self._reinos.eco.update({k: e['reino'] or '—' for k, e in self.estado.ent.items() if k.startswith('eco.')})
        self.ids_delta = {d['id'] for d in self.crono.deltas}
        with open(self.p('notificacoes.jsonl'), encoding='utf-8') as f:
            for l in f:
                self.notif_ids.add(json.loads(l)['id'])
        self.n_notif = len(self.notif_ids)
        self.notif = open(self.p('notificacoes.jsonl'), 'a', encoding='utf-8', newline='\n')
        self.saude = open(self.p('saude.txt'), 'a', encoding='utf-8', newline='\n')

    # ---- emissão de deltas
    def _emitir(self, dia, dr):
        e = self.estado
        d = dict(id='d%06d' % (len(self.crono.deltas) + 1), dia_mundo=dia, era=e.mundo['era'], tipo=dr.get('tipo'),
                 entidade=dr.get('entidade'), campo=dr.get('campo'),
                 estado_anterior=e.estado_anterior_de(dr.get('tipo'), dr.get('entidade'), dr.get('campo')),
                 estado_novo=dr.get('estado_novo'), causa=dr.get('causa'), agentes=list(dr.get('agentes') or []),
                 quem_sabe=list(dr.get('quem_sabe') or []), certeza=dr.get('certeza', 'confirmado'),
                 origem=dr.get('origem', 'SIMULADO'))
        ultimo = self.crono.deltas[-1]['dia_mundo'] if self.crono.deltas else 0
        viol = INV.checar_delta(d, e, ultimo, self.ids_delta, self.world, self.world.catalogo, self.din)
        d['importancia'] = IM.calcular(d, e) if d['tipo'] and d['entidade'] else 0
        try:
            e.aplicar(d)
        except ES.ErroEstado as x:
            self.viol_bloco.append(('F-00', d['id'], str(x)))
            return
        d['notificacao'] = self._notificar(d, viol) if d['importancia'] >= IM.LIMIAR_NOTIFICACAO else None
        self.crono.anexar(d); self.ids_delta.add(d['id'])
        for inv, msg in viol:
            self.viol_bloco.append((inv, d['id'], msg))

    def _resumo(self, d):
        if d['tipo'] == 'campo':
            return '%s.%s: %s -> %s' % (d['entidade'], d['campo'], d['estado_anterior'], d['estado_novo'])
        if d['tipo'] == 'criacao':
            return '%s criada' % d['entidade']
        if d['tipo'] == 'era':
            return 'nova era %s (%s)' % (d['estado_novo']['nome'], d['estado_novo']['regime'])
        return '%s: %s' % (d['tipo'], d['entidade'])

    def _notificar(self, d, viol):
        """Check de notificação: grava a notificação e roda o mini-check dos invariantes sobre o delta."""
        self.n_notif += 1
        nid = 'n%06d' % self.n_notif
        reino = self.estado.ent.get(d['entidade'], {}).get('reino') or self.world.reino_de(d['entidade'])
        cal = self.cals.get(reino)
        rec = dict(id=nid, delta=d['id'], dia_mundo=d['dia_mundo'], ano_base=CAL.ano_base(d['dia_mundo']),
                   dia_do_ano=CAL.dia_do_ano(d['dia_mundo']), reino=reino, data_local=cal.data(d['dia_mundo']) if cal else None,
                   entidade=d['entidade'], tipo=d['tipo'], importancia=d['importancia'], resumo=self._resumo(d),
                   causa=d['causa'], quem_sabe=d['quem_sabe'], certeza=d['certeza'],
                   status='ALERTA' if viol else 'OK', violacoes=['%s: %s' % v for v in viol])
        self.notif.write(CR.canon(rec) + '\n')
        self.notif_ids.add(nid)
        return nid

    # ---- laço de tempo
    def _dia(self, dia):
        for f in frequencias(dia):
            for m in self.por_freq[f]:
                rng = random.Random(int(CR.sha('%s|%s|%d' % (self.semente, m.nome, dia))[:16], 16))
                for dr in m.executar(Ctx(dia, self.estado, rng, self.world)) or []:
                    self._emitir(dia, dr)

    def rodar(self, anos):
        ini = self.estado.proximo_dia
        fim = ini + int(round(anos * CAL.DIAS_ANO))
        bloco_dias = self.passo * CAL.DIAS_ANO
        self._abrir_bloco(ini)
        if not self.crono.deltas:   # rodada nova: registra o começo do mundo
            self._emitir(ini, dict(tipo='era', entidade='mundo', estado_novo=dict(
                era=ES.ERA_INICIAL[0], nome=ES.ERA_INICIAL[1], regime=ES.ERA_INICIAL[2]),
                causa=dict(ids=['mundo.mythralis'], regra='inicio-da-rodada'), certeza='confirmado'))
        for dia in range(ini, fim):
            self._dia(dia)
            if (dia + 1) % bloco_dias == 0 or dia + 1 == fim:
                self.estado.proximo_dia = dia + 1
                self.resultado = self._fechar_bloco(dia)
                if self.resultado == 'FALHA':
                    break
                if dia + 1 < fim:
                    self._abrir_bloco(dia + 1)
        self._encerrar()
        return dict(resultado=self.resultado, dia=self.estado.proximo_dia, deltas=len(self.crono.deltas),
                    notificacoes=self.n_notif, blocos=self.blocos, hash_cronica=self.crono.hash)

    def _abrir_bloco(self, dia):
        self.bloco_ini = dia; self.n_delta_ini = len(self.crono.deltas); self.n_notif_ini = self.n_notif
        self.viol_bloco = []; self.alert_bloco = []

    def _fechar_bloco(self, dia_fim):
        """Check de saúde do bloco. Devolve OK, ALERTA ou FALHA."""
        v = self.viol_bloco
        for d in self.crono.deltas[self.n_delta_ini:]:
            if d['importancia'] >= IM.LIMIAR_NOTIFICACAO and d['notificacao'] not in self.notif_ids:
                v.append(('I-21', d['id'], 'delta de importância %d sem notificação' % d['importancia']))
        if hash_molde(self.raiz) != self.hash_molde0:
            v.append(('I-01', 'molde', 'o hash do molde mudou durante a rodada'))
        if hash_dinamica(self.raiz) != self.hash_din0:
            v.append(('I-01', 'dinamica', 'o hash da dinâmica mudou durante a rodada'))
        al = []
        for inv, ent, msg in INV.checar_estado(self.estado, dia_fim, {m.nome for m in self.motores}):
            (v if INV.IMPLEMENTADOS[inv][0] == 'E' else al).append((inv, ent, msg))
        parcial0 = (self.bloco_ini % (self.passo * CAL.DIAS_ANO) != 0) or ((dia_fim + 1) % (self.passo * CAL.DIAS_ANO) != 0)
        bd = self.crono.deltas[self.n_delta_ini:]
        self._reinos.m.update({d['entidade']: d['estado_novo']['reino'] for d in bd
                               if d['tipo'] == 'criacao' and isinstance(d['estado_novo'], dict) and d['estado_novo'].get('reino')})
        for d in bd:
            if str(d['entidade']).startswith('eco.') and d['entidade'] in self.estado.ent:
                self._reinos.eco[d['entidade']] = self.estado.ent[d['entidade']]['reino'] or '—'
        for inv, ent, msg in INV.checar_bloco(bd, self.estado, dia_fim, parcial0, self.cals, self.din.inv, self._reinos,
                                              self.world, self._enraizados):
            (v if INV.IMPLEMENTADOS[inv][0] == 'E' else al).append((inv, ent, msg))
        self.alert_bloco = al
        res = 'FALHA' if v else ('ALERTA' if al else 'OK')
        bloco_dias = self.passo * CAL.DIAS_ANO
        parcial = 'sim' if (self.bloco_ini % bloco_dias or (dia_fim + 1) % bloco_dias) else 'não'
        self.blocos += 1
        self.saude.write('|'.join(str(x) for x in (
            self.bloco_ini // bloco_dias + 1, CAL.ano_base(self.bloco_ini), CAL.ano_base(dia_fim), self.bloco_ini, dia_fim,
            parcial, res, len(self.crono.deltas) - self.n_delta_ini, self.n_notif - self.n_notif_ini, len(al), len(v),
            self.crono.hash[:16])) + '\n')
        self.saude.flush()
        self.viol_total += v; self.alert_total += al
        self._progresso()
        if res != 'FALHA':
            os.makedirs(self.p('snapshots'), exist_ok=True)
            snap = dict(dia=self.estado.proximo_dia, hash_cronica=self.crono.hash, deltas=len(self.crono.deltas),
                        hash_estado=self.estado.hash(), estado=self.estado.como_dict())
            nome = 'snapshots/bloco-%05d-dia%d.json' % (self.bloco_ini // bloco_dias + 1, dia_fim)
            with open(self.p(nome), 'w', encoding='utf-8') as f:
                json.dump(snap, f, ensure_ascii=False, sort_keys=True)
        return res

    def _progresso(self):
        self.crono.descarregar(); self.notif.flush()
        with open(self.p('notificacoes.jsonl'), encoding='utf-8') as f:
            hn = CR.sha(f.read())
        with open(self.p('progresso.json'), 'w', encoding='utf-8') as f:
            json.dump(dict(proximo_dia=self.estado.proximo_dia, hash_cronica=self.crono.hash, deltas=len(self.crono.deltas),
                           notificacoes=self.n_notif, hash_notificacoes=hn, hash_estado=self.estado.hash(),
                           resultado=self.resultado, passo=self.passo,
                           viol=[list(x) for x in self.viol_total], alert=[list(x) for x in self.alert_total]), f, sort_keys=True)

    def _encerrar(self):
        self._progresso()
        linhas = ['codigo|gravidade|situacao|detalhe']
        por = {}
        for inv, ent, msg in self.viol_total + self.alert_total:
            por.setdefault(inv, []).append('%s: %s' % (ent, msg))
        for cod, (g, nome) in sorted(INV.IMPLEMENTADOS.items()):
            if cod in ('I-02', 'I-19', 'I-22'):
                linhas.append('%s|%s|fora-da-rodada|%s; comparado entre rodadas em motor/testes/test_f3.py' % (cod, g, nome))
            elif cod in por:
                linhas.append('%s|%s|%s|%s (%d); primeiro: %s' % (cod, g, 'FALHOU' if g == 'E' else 'ALERTA', nome, len(por[cod]), por[cod][0]))
            else:
                linhas.append('%s|%s|ok|%s' % (cod, g, nome))
        for cod, quando in sorted(INV.PENDENTES.items()):
            linhas.append('%s|?|pendente|%s' % (cod, quando))
        for f_ in sorted(k for k in por if k.startswith('F-')):
            linhas.append('%s|E|FALHOU|formato/aplicação (%d); primeiro: %s' % (f_, len(por[f_]), por[f_][0]))
        with open(self.p('invariantes.txt'), 'w', encoding='utf-8', newline='\n') as f:
            f.write('\n'.join(linhas) + '\n')
        REL.gerar(self.saida, self.world, self.crono.deltas, self.crono.cabecalho)
        self.crono.fechar(); self.notif.close(); self.saude.close()


def main(argv=None):
    ap = argparse.ArgumentParser(description='Simulador de Mythralis')
    ap.add_argument('--anos', help='anos a simular; com --retomar, a mais (N ou +N)')
    ap.add_argument('--ate-ano', type=int, help='simula até este ano do mundo (alternativa a --anos)')
    ap.add_argument('--passo', type=int, help='bloco de conferência, em anos (padrão 10; na retomada, o da rodada)')
    ap.add_argument('--semente', type=int, default=1)
    ap.add_argument('--saida', help='nome da rodada em saidas/ (padrão rodada-s<semente>)')
    ap.add_argument('--retomar', help='pasta de uma rodada para continuar')
    ap.add_argument('--lote', type=int, help='número de sementes consecutivas (a partir de --semente) sobre o mesmo molde')
    a = ap.parse_args(argv)
    if (a.anos is None) == (a.ate_ano is None):
        ap.error('informe --anos ou --ate-ano (um dos dois)')
    if a.ate_ano is not None and a.lote:
        ap.error('--ate-ano não vale com --lote')
    try:
        a.anos = None if a.anos is None else float(str(a.anos).lstrip('+'))
    except ValueError:
        ap.error('--anos inválido: %s' % a.anos)
    if a.anos is not None and a.anos <= 0:
        ap.error('--anos deve ser positivo')
    if a.lote:
        import lote
        L = lote.rodar_lote(a.lote, a.anos, a.passo or 10, a.semente, os.path.join(C.RAIZ, 'saidas', a.saida or 'lote-%da-%ds' % (a.anos, a.lote)))
        print('lote: %d sementes, %d com FALHA' % (len(L), sum(1 for x in L if x['resultado'] == 'FALHA')))
        return 1 if any(x['resultado'] == 'FALHA' for x in L) else 0
    import motores
    MOTORES = motores.criar()
    try:
        if a.retomar:
            sim = Simulador(a.retomar, passo_anos=a.passo, motores=MOTORES); sim.retomar()
        else:
            sim = Simulador(os.path.join(C.RAIZ, 'saidas', a.saida or 'rodada-s%d' % a.semente), a.semente, a.passo, MOTORES)
            sim.iniciar()
    except (ErroRetomada, FileNotFoundError) as x:
        print('ERRO: %s' % x); return 2
    if a.ate_ano is not None:
        a.anos = a.ate_ano - CAL.ano_base(sim.estado.proximo_dia)
        if a.anos <= 0:
            print('ERRO: a rodada já está no ano %d' % CAL.ano_base(sim.estado.proximo_dia)); return 2
    r = sim.rodar(a.anos)
    print('%s: %g anos, %d deltas, %d notificações, %d blocos, dia %d, hash %s' % (
        r['resultado'], a.anos, r['deltas'], r['notificacoes'], r['blocos'], r['dia'], r['hash_cronica'][:16]))
    return 1 if r['resultado'] == 'FALHA' else 0


if __name__ == '__main__':
    sys.exit(main())
