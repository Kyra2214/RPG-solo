"""Eventos (F4). Lunar. Estouro de relógio -> evento ocorre -> a `cadeia` do molde vira passos em ordem, com atraso.
Sem jogador: ao fim da cadeia entram as consequências `se_ninguem_age` das sementes ligadas ao evento.
Eventos sem relógio (gatilho só em prosa) ficam latentes: não há gatilho executável na F4."""
import carregador as C


class Eventos:
    nome = 'eventos'; frequencia = 'lunar'

    def __init__(self, din):
        self.din = din
        self._idx = None

    def _prep(self, w):
        if self._idx is None:
            ev = {r['id']: r for r in w.t['evento']}
            sem = {}
            for s in w.t['semente']:
                for x in C.toks(s['relogio_evento']):
                    if x.startswith('event.'):
                        sem.setdefault(x, []).append(s)
            self._idx = (ev, sem)
        return self._idx

    def executar(self, ctx):
        if self.din.vazia:
            return []
        ev, sem = self._prep(ctx.mundo)
        e, out = ctx.estado, []
        atraso = 30 * self.din.lim.get('atraso_cadeia_luas', 2)
        for i in sorted(k for k in e.ent if k.startswith('event.')):
            s, r = e.ent[i], ev[i]
            q = [s['reino']] + [x for x in C.toks(r['cidades_e_assentamentos']) if x in e.ent][:3]
            if s['estado'] == 'latente':
                rel = r['relogio']
                if rel in e.ent and e.ent[rel].get('estourado') is True:
                    cz = dict(ids=[rel, i], regra='estouro-de-relogio')
                    out.append(dict(tipo='evento_ocorrido', entidade=i, causa=cz, quem_sabe=q,
                                    estado_novo=dict(relogio=rel, nome=r['nome'])))
                    out.append(dict(tipo='campo', entidade=i, campo='estado', estado_novo='em_cadeia', causa=cz, quem_sabe=q))
                    out.append(dict(tipo='campo', entidade=i, campo='inicio_dia', estado_novo=ctx.dia_mundo, causa=cz, quem_sabe=q))
            elif s['estado'] == 'em_cadeia' and ctx.dia_mundo >= s['inicio_dia'] + atraso * s['passo']:
                passos = [x.strip() for x in r['cadeia'].split(' > ') if x.strip()]
                n = s['passo']
                ags = [x for x in C.toks(r['pessoas_e_faccoes']) if x.startswith('npc.') and e.vivo(x)]
                cz = dict(ids=[i], regra='cadeia-do-evento')
                out.append(dict(tipo='passo_de_cadeia', entidade=i, causa=cz, quem_sabe=q, agentes=ags,
                                estado_novo=dict(passo=n + 1, de=len(passos), texto=passos[min(n, len(passos) - 1)])))
                out.append(dict(tipo='campo', entidade=i, campo='passo', estado_novo=n + 1, causa=cz, quem_sabe=q))
                if n + 1 >= s['total']:
                    out.append(dict(tipo='campo', entidade=i, campo='estado', estado_novo='concluido', causa=cz, quem_sabe=q))
                    out.append(dict(tipo='fim_de_cadeia', entidade=i, causa=cz, quem_sabe=q, estado_novo=dict(nome=r['nome'])))
                    for sd in sem.get(i, []):
                        out.append(dict(tipo='consequencia_sem_agente', entidade=sd['id'], quem_sabe=q,
                                        causa=dict(ids=[i, sd['id']], regra='se-ninguem-age'), certeza='alegado',
                                        estado_novo=dict(texto=sd['se_ninguem_age'])))
        return out
