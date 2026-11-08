"""Importância dos deltas (0 a 4) por REGRA, nunca por sorteio. 3 ou mais dispara o check de notificação.

0 trivial | 1 menor | 2 notável (fica só na crônica) | 3 importante (notifica) | 4 marcante (notifica)
F3 traz as regras-base abaixo; F4 e F5 acrescentam as de cada motor.
"""
LIMIAR_NOTIFICACAO = 3

# tipos de registro (não mudam o estado) e sua importância
TIPO_REGISTRO = {
    'incidente_fronteira': 3, 'quebra_de_contrato': 3, 'tratado': 3, 'ruptura': 3, 'cisma': 3, 'fusao': 3,
    'extincao': 3, 'bloqueio_de_rota': 3, 'revolta': 3, 'pista_de_misterio': 3, 'tentativa_de_resolver_misterio': 3,
    'alianca_de_povos': 3, 'fim_de_alianca_de_povos': 3, 'laco_aliado': 2, 'laco_rompido': 2, 'laco_rival': 2,
    'guerra': 4, 'queda_de_coroa': 4, 'fato_confirmado_alterado': 4,
    'disputa_aberta': 3, 'evento_ocorrido': 3, 'fim_de_cadeia': 3, 'passo_de_cadeia': 2,
    'consequencia_sem_agente': 2, 'rota_reaberta': 2, 'tregua': 2, 'tensao_pos_ruptura': 1, 'boato': 1,
}
CAMPOS_DE_CARGO = ('governante', 'lideranca', 'titular')
CARGOS_DE_PODER = ('governante', 'general', 'herdeiro')


def calcular(d, estado):
    """d: delta (sem importancia ainda); estado: Estado ANTES de aplicar o delta."""
    t = d['tipo']
    if t == 'era':
        return 4
    if t == 'criacao':
        n = d['estado_novo'] or {}
        poder = any(p in CARGOS_DE_PODER for p in str(n.get('papeis', '')).split(';')) or n.get('cargo') in CAMPOS_DE_CARGO
        return 3 if poder else 1
    if t == 'campo':
        c, ent, novo = d['campo'], estado.ent.get(d['entidade'], {}), d['estado_novo']
        if c == 'vivo' and novo is False:
            if 'governante' in str(ent.get('papeis', '')).split(';'):
                return 4
            return 3 if ent.get('principal') else 2
        if c in CAMPOS_DE_CARGO:
            return 3
        if c == 'vago_desde' and novo is not None:
            return 3
        if c == 'relacao' and str(d['entidade']).startswith('par.'):
            return 3
        if c == 'saude' and novo == 'doente':
            return 3 if ent.get('principal') else 2
        if c == 'estado' and str(d['entidade']).startswith('event.'):
            return 3 if novo == 'em_cadeia' else 2
        if c == 'escassez' and str(d['entidade']).startswith('econ.'):
            return 2 if novo >= 3 else 1
        if c == 'difusao':
            return 0
        if c == 'tensao' and str(d['entidade']).startswith('par.'):
            return 1
        if c == 'estourado' and novo is True:
            return 3
        if c == 'progresso':
            return 3 if novo == ent.get('segmentos', 0) - 1 else 1
        if c == 'estado' and str(d['entidade']).startswith('fact.') and d['estado_anterior'] == 'confirmado':
            return 4
        return 1
    return TIPO_REGISTRO.get(t, 2)
