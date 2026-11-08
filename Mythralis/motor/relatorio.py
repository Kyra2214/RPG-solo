"""Relatório de uma rodada (F6.1): `relatorio.md` (por era e por década) e `metricas.csv` (série anual por reino).
Tudo é derivado dos deltas da crônica (nunca escrito à mão) e é determinístico. Não entra nos hashes da rodada.
  python3 motor/relatorio.py saidas/<rodada>      # regenera os dois arquivos de uma rodada pronta
"""
import os, sys, json
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import carregador as C, calendario as CAL, cronica as CR

COLUNAS = ('deltas', 'importantes', 'mortes', 'sucessoes', 'relogios_estourados', 'eventos', 'bloqueios', 'pistas',
           'rupturas', 'tratados', 'revoltas', 'npcs_simulados')
NOMES = dict(deltas='deltas', importantes='importantes (3+)', mortes='mortes', sucessoes='sucessões', relogios_estourados='relógios estourados',
             eventos='eventos', bloqueios='bloqueios de rota', pistas='pistas de mistério', rupturas='rupturas e disputas',
             tratados='tratados', revoltas='revoltas', npcs_simulados='NPCs simulados')


class Reinos:
    """Reino de qualquer id de delta: molde, entidade criada pela simulação, econ, par, posto e Eco."""
    def __init__(self, world, deltas):
        self.w = world; self.m = {}; self.eco = {}
        for d in deltas:
            if d['tipo'] == 'criacao' and isinstance(d['estado_novo'], dict) and d['estado_novo'].get('reino'):
                self.m[d['entidade']] = d['estado_novo']['reino']

    def de(self, i, n=0):
        if i in self.m:
            return self.m[i]
        if i.startswith('crown.'):
            return 'kingdom.' + i[6:]
        if i.startswith('front.'):
            return 'kingdom.' + i[6:].split('+')[0]
        if i.startswith('eco.'):
            return self.eco.get(i, '—')
        if i.startswith(('econ.', 'posto.', 'par.')):
            base = i.split('@')[-1] if i.startswith('econ.') else i.split('.', 1)[1].split('+')[0]
            r = self.de(base, n + 1) if n < 3 else None
        else:
            r = self.w.reino_de(i)
        self.m[i] = r or '—'
        return self.m[i]


def classe(d):
    t, c, n = d['tipo'], d.get('campo'), d.get('estado_novo')
    if t == 'campo':
        if c == 'vivo' and n is False: return 'mortes'
        if c in ('governante', 'lideranca', 'titular'): return 'sucessoes'
        if c == 'estourado' and n is True: return 'relogios_estourados'
    return {'evento_ocorrido': 'eventos', 'bloqueio_de_rota': 'bloqueios', 'pista_de_misterio': 'pistas', 'ruptura': 'rupturas',
            'disputa_aberta': 'rupturas', 'incidente_fronteira': 'rupturas', 'quebra_de_contrato': 'rupturas', 'guerra': 'rupturas', 'queda_de_coroa': 'rupturas', 'tratado': 'tratados', 'revolta': 'revoltas'}.get(t) or \
        ('npcs_simulados' if t == 'criacao' and str(d['entidade']).startswith('npc.') else None)


def texto(d):
    t, n = d['tipo'], d.get('estado_novo')
    if t == 'era': return 'nova era: %s (%s)' % (n['nome'], n['regime'])
    if t == 'campo' and d['campo'] == 'vivo': return 'morte de %s' % d['entidade']
    if t == 'campo': return '%s.%s: %s -> %s' % (d['entidade'], d['campo'], d['estado_anterior'], n)
    if t == 'criacao': return '%s criada' % d['entidade']
    return '%s: %s' % (t, d['entidade'])


def tabelas(world, deltas):
    R = Reinos(world, deltas)
    por_id = {d['id']: d for d in deltas}
    anual = {}
    for d in deltas:
        if str(d['entidade']).startswith('eco.') and d['entidade'] not in R.eco:
            o = por_id.get(d['entidade'][4:]); R.eco[d['entidade']] = R.de(o['entidade']) if o else '—'
        k = (CAL.ano_base(d['dia_mundo']), R.de(d['entidade']) if d['entidade'] != 'mundo' else '—')
        linha = anual.setdefault(k, dict.fromkeys(COLUNAS, 0))
        linha['deltas'] += 1
        if d.get('importancia', 0) >= 3: linha['importantes'] += 1
        c = classe(d)
        if c: linha[c] += 1
    return anual


def gerar(saida, world, deltas, cab):
    anual = tabelas(world, deltas)
    ano0 = cab.get('ano_base', CAL.ANO_BASE)
    ult = max((d['dia_mundo'] for d in deltas), default=0)
    anoN = CAL.ano_base(ult)
    with open(os.path.join(saida, 'metricas.csv'), 'w', encoding='utf-8', newline='\n') as f:
        f.write('ano|reino|' + '|'.join(COLUNAS) + '\n')
        for (a, r) in sorted(anual):
            f.write('%d|%s|%s\n' % (a, r, '|'.join(str(anual[(a, r)][c]) for c in COLUNAS)))
    eras = [(d['estado_novo'], d['dia_mundo']) for d in deltas if d['tipo'] == 'era']
    L = ['# Relatório da rodada', '',
         'Semente %s, motor %s, anos %d a %d (%d deltas). Gerado dos deltas da crônica; molde `%s`.' % (
             cab.get('semente'), cab.get('versao_motor'), ano0, anoN, len(deltas), str(cab.get('hash_molde'))[:16]), '']
    for i, (era, ini) in enumerate(eras):
        fim = eras[i + 1][1] if i + 1 < len(eras) else ult + 1
        a0, a1 = CAL.ano_base(ini), CAL.ano_base(max(ini, fim - 1))
        L += ['## %s — %s (regime %s), anos %d a %d' % (era['era'], era['nome'], era['regime'], a0, a1), '']
        dec0 = a0 - (a0 - ano0) % 10
        for dec in range(dec0, a1 + 1, 10):
            ds = [d for d in deltas if ini <= d['dia_mundo'] < fim and dec <= CAL.ano_base(d['dia_mundo']) < dec + 10]
            if not ds: continue
            tot = dict.fromkeys(COLUNAS, 0)
            for d in ds:
                tot['deltas'] += 1
                if d.get('importancia', 0) >= 3: tot['importantes'] += 1
                c = classe(d)
                if c: tot[c] += 1
            L.append('### Anos %d a %d' % (dec, dec + 9))
            L.append('; '.join('%s %d' % (NOMES[c], tot[c]) for c in COLUNAS if tot[c]) + '.')
            marcos = [d for d in ds if d.get('importancia', 0) >= 4]
            for d in marcos[:15]:
                L.append('- ano %d: %s' % (CAL.ano_base(d['dia_mundo']), texto(d)))
            if len(marcos) > 15: L.append('- (mais %d marcos na crônica)' % (len(marcos) - 15))
            L.append('')
    L += ['## Total por reino', '', '| reino | ' + ' | '.join(NOMES[c] for c in COLUNAS) + ' |', '|---|' + '---|' * len(COLUNAS)]
    reinos = sorted({r for _, r in anual})
    for r in reinos:
        L.append('| %s | ' % r + ' | '.join(str(sum(v[c] for (a, rr), v in anual.items() if rr == r)) for c in COLUNAS) + ' |')
    with open(os.path.join(saida, 'relatorio.md'), 'w', encoding='utf-8', newline='\n') as f:
        f.write('\n'.join(L) + '\n')


def main(argv=None):
    pasta = (argv or sys.argv[1:])[0]
    cr = CR.Cronica.abrir(os.path.join(pasta, 'cronica.jsonl'))
    gerar(pasta, C.carregar(), cr.deltas, cr.cabecalho)
    cr.fechar()


if __name__ == '__main__':
    main()
