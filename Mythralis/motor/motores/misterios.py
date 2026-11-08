"""Mistérios (F5.3). Anual. O mundo anda em torno de um mistério, nunca o resolve: sem agente investigador só há pistas.
Cada pista aponta uma `hipoteses_abertas` do molde (nada inventado), com certeza `suspeita`, até o teto `mist_max_pistas`.
Resolver exige agente e a evidência listada no molde (I-08, em invariantes.py); este motor nunca emite resolução."""


class Misterios:
    nome = 'misterios'; frequencia = 'anual'

    def __init__(self, din):
        self.din = din

    def executar(self, ctx):
        M, e, out = self.din.mist, ctx.estado, []
        if self.din.vazia or not M:
            return []
        for k in sorted(k for k in e.ent if k.startswith('mist.')):
            m = e.ent[k]
            if m['estado'] != 'aberto' or m['pistas'] >= M.get('mist_max_pistas', 6):
                continue
            if ctx.rng.randrange(1000) >= M.get('mist_pista_pm', 70):
                continue
            row = ctx.mundo.get(k) or {}
            hip = [h.strip() for h in row.get('hipoteses_abertas', '').split(';') if h.strip()]
            hi = ctx.rng.randrange(len(hip)) if hip else None
            n = m['pistas'] + 1
            cz = dict(ids=[k], regra='pista-sem-agente')
            out.append(dict(tipo='pista_de_misterio', entidade=k, certeza='suspeita', quem_sabe=[m['reino']], causa=cz,
                            estado_novo=dict(pista=n, em_torno_de=hip[hi] if hip else '?')))
            out.append(dict(tipo='campo', entidade=k, campo='pistas', estado_novo=n, quem_sabe=[m['reino']], causa=cz))
        return out
