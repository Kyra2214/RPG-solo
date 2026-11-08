"""Fronteira (F5.4, F7.1). Anual. Regime `selado` (Era I): os contratos de fronteira valem como regra do motor e nada cruza o reino;
as proibições são conferidas por delta em I-10 (invariantes.py).
F7.1: cada vizinhança do anel (M-04) é um par `front.<a>+<b>` com tensão em pontos: sobe por ano com a base e com os relógios
estourados dos dois reinos; ao atingir o limiar vira um `incidente_fronteira` (registro, com causa) e a tensão cai para o valor
pós-incidente. O primeiro incidente é o gatilho do fim da Era I: o regime passa a `poroso` (era.002). Segurança (D-M4): sem
incidente, a era termina em `fronteira_limite_anos`. Um incidente fica `pendente` à espera de resposta do motor de diplomacia (F7.2); guerras e o regime aberto são da F7.3."""


class Fronteira:
    nome = 'fronteira'; frequencia = 'anual'

    def __init__(self, din):
        self.din = din

    def executar(self, ctx):
        F, D, e, out = self.din.front, self.din.dipl, ctx.estado, []
        if self.din.vazia or not F:
            return []
        primeiro = None
        for k in sorted(k for k in e.ent if k.startswith('front.')):
            f = e.ent[k]
            if f.get('relacao') == 'guerra':
                continue   # em guerra a tensão fica congelada e não há incidente novo (F7.3)
            ids = [c for r in f['reinos'] for c in sorted(x for x in e.ent if x.startswith('clock.')
                                                           and e.ent[x]['reino'] == r and e.ent[x]['estourado'])]
            g = F.get('front_tensao_base', 1) + F.get('front_tensao_por_relogio', 1) * len(ids) \
                + ctx.rng.randrange(F.get('front_tensao_jitter', 3) + 1)
            fator = {'tratado': D.get('dipl_tratado_fator_pm', 1000), 'ruptura': D.get('dipl_ruptura_fator_pm', 1000)}.get(f.get('relacao'), 1000)
            t = f['tensao'] + g * fator // 1000
            t = min(t, F.get('front_tensao_max', 400))
            cz = dict(ids=[k] + ids[:3], regra='tensao-de-fronteira')
            if t >= F.get('front_tensao_limiar', 300):
                out.append(dict(tipo='incidente_fronteira', entidade=k, quem_sabe=list(f['reinos']), causa=cz,
                                estado_novo=dict(tensao=t, relogios_estourados=len(ids))))
                out.append(dict(tipo='campo', entidade=k, campo='tensao', estado_novo=F.get('front_tensao_pos_incidente', 150),
                                quem_sabe=list(f['reinos']), causa=cz))
                out.append(dict(tipo='campo', entidade=k, campo='incidentes', estado_novo=f['incidentes'] + 1,
                                quem_sabe=list(f['reinos']), causa=cz))
                out.append(dict(tipo='campo', entidade=k, campo='ultimo_dia', estado_novo=ctx.dia_mundo,
                                quem_sabe=list(f['reinos']), causa=cz))
                if not f.get('pendente'):
                    out.append(dict(tipo='campo', entidade=k, campo='pendente', estado_novo=True,
                                    quem_sabe=list(f['reinos']), causa=cz))
                primeiro = primeiro or k
            else:
                out.append(dict(tipo='campo', entidade=k, campo='tensao', estado_novo=t, quem_sabe=list(f['reinos']), causa=cz))
        lim = F.get('fronteira_limite_anos')
        if e.mundo['regime'] != 'selado':
            return out
        if primeiro:
            causa = dict(ids=[primeiro], regra='primeiro-incidente-de-fronteira')
        elif lim and ctx.dia_mundo >= lim * 365:
            causa = dict(ids=['mundo.mythralis'], regra='limite-de-anos-sem-gatilho')
        else:
            return out
        out.append(dict(tipo='era', entidade='mundo', quem_sabe=['mundo.mythralis'],
                        estado_novo=dict(era='era.002', nome='Fronteiras Porosas', regime='poroso'), causa=causa))
        return out
