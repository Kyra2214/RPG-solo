"""Demografia de pessoas (F4): idade por classe de longevidade, doença e morte natural. Anual."""


class Demografia:
    nome = 'demografia'; frequencia = 'anual'

    def __init__(self, din):
        self.din = din

    def executar(self, ctx):
        if self.din.vazia:
            return []
        out, e = [], ctx.estado
        fd = self.din.lim.get('fator_mortalidade_doente', 4)
        for i in sorted(k for k in e.ent if k.startswith('npc.')):
            n = e.ent[i]
            if n.get('vivo') is not True or n.get('nasc_dia') is None:
                continue
            idade = (ctx.dia_mundo - n['nasc_dia']) // 365
            p = self.din.morte_pm(n['classe'], idade)
            doente = n.get('saude') == 'doente'
            if doente:
                p = min(1000, p * fd)
            if ctx.rng.randrange(1000) < p:
                out.append(dict(tipo='campo', entidade=i, campo='vivo', estado_novo=False,
                                causa=dict(ids=[i], regra='morte-natural' + ('-doente' if doente else '')),
                                quem_sabe=[n['reino']]))
                continue
            if doente:
                if ctx.rng.randrange(1000) < self.din.lim.get('recuperacao_pm', 350):
                    out.append(dict(tipo='campo', entidade=i, campo='saude', estado_novo='sao',
                                    causa=dict(ids=[i], regra='recuperacao'), quem_sabe=[n['reino']]))
            elif self.din.e_idoso(n['classe'], idade) and ctx.rng.randrange(1000) < self.din.lim.get('doenca_idosa_pm', 40):
                out.append(dict(tipo='campo', entidade=i, campo='saude', estado_novo='doente',
                                causa=dict(ids=[i], regra='doenca-da-idade'), quem_sabe=[n['reino']]))
        return out
