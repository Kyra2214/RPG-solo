"""Sucessão (F4): posto com titular morto -> herdeiro -> vice (vínculo de cargo) -> candidato da facção/cidade ->
vacância com prazo e interino -> NPC simulado quando o prazo acaba. Anual (roda depois da demografia)."""
import nomes as NM
import carregador as C


def _afin(heir, morto):
    """Palavras próprias (maiúsculas, fora 'Coroa') do cargo do herdeiro que também estão no cargo do morto."""
    pal = {w.strip('.,;()`') for w in heir.split() if w[:1].isupper() and len(w) > 3 and w != 'Coroa'}
    return sum(1 for w in pal if w in morto)


class Sucessao:
    nome = 'sucessao'; frequencia = 'anual'

    def __init__(self, din):
        self.din = din
        self._rel = None
        self._novos = {}

    def _vinculos(self, mundo):
        if self._rel is None:
            self._rel = [(r['origem'], r['destino']) for r in mundo.t['relacao'] if r['tipo'] == 'vínculo de cargo'
                         and r['destino'].startswith('npc.')]
        return self._rel

    def executar(self, ctx):
        if self.din.vazia:
            return []
        e, out = ctx.estado, []
        self._novos = {}
        titulares = set()
        for k, p in e.ent.items():
            if k.startswith('posto.'):
                titulares.update(x for x in p['titular'].split(';') if x.startswith('npc.'))
        for k in sorted(k for k in e.ent if k.startswith('posto.')):
            p = e.ent[k]
            tit = [x for x in p['titular'].split(';') if x.startswith('npc.')]
            vivos = [x for x in tit if e.vivo(x)]
            if tit and len(vivos) == len(tit):
                continue
            if tit and vivos:                     # parte da diarquia morreu: o cargo continua com quem vive
                out += self._mudar_titular(ctx, k, p, ';'.join(vivos), tit, 'diarquia-reduzida', [])
                continue
            morto = tit[-1] if tit else (p['anterior'] if p['anterior'] != '—' else None)
            ref = dict(e.ent.get(morto, {}), _id=morto) if morto else {}
            novo, regra = self._escolher(ctx, p, morto, ref, titulares)
            if novo is None and p['vago_desde'] is not None and ctx.dia_mundo >= p['prazo']:
                novo, regra = self._criar(ctx, p, ref, out), 'nomeacao-de-simulado'
            if novo is not None:
                out += self._posse(ctx, k, p, novo, tit, ref, regra)
                titulares.add(novo)
            elif p['vago_desde'] is not None:
                if not e.vivo(p['interino']):      # o interino morreu: troca-se o responsável
                    out.append(dict(tipo='campo', entidade=k, campo='interino', estado_novo=self._interino(ctx, p, titulares),
                                    causa=dict(ids=[k], regra='interino-substituido'), quem_sabe=[p['reino']]))
            else:
                inter = self._interino(ctx, p, titulares)
                prazo = ctx.dia_mundo + 365 * self.din.lim.get('prazo_vacancia_anos', 3)
                for campo, val in (('anterior', morto or '—'), ('titular', '—'), ('vago_desde', ctx.dia_mundo),
                                   ('prazo', prazo), ('interino', inter)):
                    out.append(dict(tipo='campo', entidade=k, campo=campo, estado_novo=val,
                                    causa=dict(ids=[morto or k], regra='vacancia'), quem_sabe=[p['reino']]))
        return out

    # ---- escolha do sucessor
    def _livres(self, ctx, reino, titulares):
        e = ctx.estado
        return [i for i in sorted(e.ent) if i.startswith('npc.') and e.vivo(i) and e.ent[i]['reino'] == reino
                and i not in titulares]

    def _ordem(self, ctx, ids):
        e = ctx.estado
        return sorted(ids, key=lambda i: (not e.ent[i].get('principal'), i))

    def _escolher(self, ctx, p, morto, ref, titulares):
        e = ctx.estado
        livres = self._livres(ctx, p['reino'], titulares)
        if not livres:
            return None, None
        if 'governante' in p['papel'] or 'governante' in str(ref.get('papeis', '')):
            hs = [i for i in livres if 'herdeiro' in e.ent[i].get('papeis', '').split(';')]
            if hs:
                melhor = max(_afin(e.ent[i]['cargo'], ref.get('cargo', '')) for i in hs)
                if melhor > 0 or len(hs) == 1:
                    h = [i for i in hs if _afin(e.ent[i]['cargo'], ref.get('cargo', '')) == melhor]
                    return self._ordem(ctx, h)[0], 'sucessao-herdeiro'
        if morto:
            vs = set(o for o, d in self._vinculos(ctx.mundo) if d == morto) & set(livres)
            vs = vs or set(d for o, d in self._vinculos(ctx.mundo) if o == morto) & set(livres)
            if vs:
                return self._ordem(ctx, vs)[0], 'sucessao-vice'
        cand = self._membros(ctx, p, ref, livres)
        if cand:
            return self._ordem(ctx, cand)[0], 'sucessao-candidato'
        return None, None

    def _membros(self, ctx, p, ref, livres):
        e = ctx.estado
        if p['tipo'] == 'faccao':
            return [i for i in livres if e.ent[i]['faccao'] == p['alvo']]
        if p['tipo'] == 'cidade':
            return [i for i in livres if e.ent[i]['cidade'] == p['alvo']]
        c, f = ref.get('cidade', '—'), ref.get('faccao', '—')
        return [i for i in livres if (c != '—' and e.ent[i]['cidade'] == c) or (f != '—' and e.ent[i]['faccao'] == f)]

    def _interino(self, ctx, p, titulares):
        """Responsável interino: membro livre, senão qualquer livre do reino, senão qualquer vivo do reino
        (mesmo titular de outro posto), senão qualquer vivo do mundo; '—' só se o mundo ficou sem ninguém."""
        e = ctx.estado
        livres = self._livres(ctx, p['reino'], titulares)
        c = self._membros(ctx, p, {}, livres) or livres
        if c:
            return self._ordem(ctx, c)[0]
        vivos = [i for i in sorted(e.ent) if i.startswith('npc.') and e.vivo(i) and e.ent[i]['reino'] == p['reino']]
        vivos = vivos or [i for i in sorted(e.ent) if i.startswith('npc.') and e.vivo(i)]
        return self._ordem(ctx, vivos)[0] if vivos else '—'

    # ---- deltas
    def _mudar_titular(self, ctx, k, p, novo, antigos, regra, ids):
        out = [dict(tipo='campo', entidade=k, campo='titular', estado_novo=novo,
                    causa=dict(ids=[antigos[-1]], regra=regra), quem_sabe=[p['reino']])]
        if p['tipo'] == 'cidade':
            out.append(dict(tipo='campo', entidade=p['alvo'], campo='governante', estado_novo=novo,
                            causa=dict(ids=[k], regra=regra), quem_sabe=[p['reino']]))
        elif p['tipo'] == 'faccao':
            out.append(dict(tipo='campo', entidade=p['alvo'], campo='lideranca', estado_novo=novo,
                            causa=dict(ids=[k], regra=regra), quem_sabe=[p['reino']]))
        return out

    def _posse(self, ctx, k, p, novo, antigos, ref, regra):
        out = []
        if p['vago_desde'] is not None:
            for campo, val in (('vago_desde', None), ('prazo', None), ('interino', '—'), ('anterior', '—')):
                out.append(dict(tipo='campo', entidade=k, campo=campo, estado_novo=val,
                                causa=dict(ids=[k], regra=regra), quem_sabe=[p['reino']]))
        out += self._mudar_titular(ctx, k, p, novo, antigos or [ref.get('_id', k)], regra, [])
        for d in out:
            d['agentes'] = [novo] if d['campo'] in ('titular', 'governante', 'lideranca') else []
        n = ctx.estado.ent.get(novo) or self._novos.get(novo)
        base = set(n['papeis'].split(';')) - {'—', '?', ''}
        add = set(ref.get('papeis', '').split(';')) & {'governante', 'general'}
        papeis = ';'.join(sorted(base | add)) or '—'
        cargo = ref.get('cargo') or n['cargo']
        for campo, val in (('cargo', cargo), ('papeis', papeis)):
            if n.get(campo) != val and not (novo in self._novos):
                out.append(dict(tipo='campo', entidade=novo, campo=campo, estado_novo=val,
                                causa=dict(ids=[k], regra=regra), quem_sabe=[p['reino']]))
        if novo in self._novos:        # NPC simulado já nasce com o cargo (a criação é anterior; não há campo a mudar)
            self._novos[novo].update(cargo=cargo, papeis=papeis)
        return out

    def _criar(self, ctx, p, ref, out):
        """NPC simulado (D-M5): só quando o prazo da vacância acaba e ninguém serve. Nome por M-10 (nomes.py)."""
        e, rng, w = ctx.estado, ctx.rng, ctx.mundo
        povos = sorted({r['povo'] for r in w.t['povo_no_reino'] if r['reino'] == p['reino']}
                       & {x['id'] for x in w.catalogo['races']})
        povo = rng.choice(povos) if povos else 'humano'
        subs = sorted(s['id'] for s in w.catalogo['subraces'] if s['parentRaceId'] == povo)
        sub = rng.choice(subs) if subs else '—'
        cidade = p['alvo'] if p['tipo'] == 'cidade' else ref.get('cidade', '—')
        if cidade == '—' and p['tipo'] == 'faccao':
            fr = [r for r in w.t['faccao'] if r['id'] == p['alvo']]
            cs = C.toks(fr[0]['presenca_cidades']) if fr else []
            cidade = cs[0] if cs else '—'
        classe = self.din.classe_de(povo, sub)
        k = self.din.classes.get(classe) or self.din.classes['humana']
        idade = k['mat'] + rng.randrange(max(1, k['mad'] - k['mat'])) + k['mat']
        nasc = ctx.dia_mundo - 365 * idade if k['max'] else None
        nid = 'npc.sim-%05d' % (e.n_criados + len(self._novos) + 1)
        cargo = ref.get('cargo') or '—'
        papeis = ';'.join(sorted(set(ref.get('papeis', '').split(';')) & {'governante', 'general'})) or '—'
        novo = dict(
                            vivo=True, reino=p['reino'], cidade=cidade, faccao=p['alvo'] if p['tipo'] == 'faccao' else '—',
                            cargo=cargo, papeis=papeis, principal=False, povo=povo, subraca=sub,
                            nome=NM.gerar(rng, e, [v['nome'] for v in self._novos.values()]),
                            saude='sao', classe=classe, nasc_dia=nasc, origem_npc='nomeacao')
        self._novos[nid] = novo
        out.append(dict(tipo='criacao', entidade=nid, causa=dict(ids=[p['alvo']], regra='nomeacao-sem-candidato'),
                        quem_sabe=[p['reino']], agentes=[], estado_novo=dict(novo)))
        return nid
