# Azeroth — Voz e ganchos de diálogo (doc 12, R5-4.4)

**Estado:** R5-4.4, D-160 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não cria fato, NPC ou relação novos**. Dados em `12-voz.csv` (`npc_id, tom, frases_modelo, nunca_diz, base`), no molde dos reinos 1 a 4.

## 1. Regras

- 12 principais (N-01 a N-12): `tom` a partir da personalidade da ficha (R5-4.1); 3 `frases_modelo` por principal (separadas por `|`) como **instrução de tom, não como fala canônica** (ASSUMINDO); `nunca_diz` reúne a suspeita e o desconhecido da ficha e o que ultrapassa o limite do cargo.
- Frases só repetem o que já é documentado (cargo, limite, agenda); nenhuma revela suspeita ou desconhecido das fichas.
- Odile não manda no Cais nem no Vento; Tomlin não assina medida; Tolla não vota pelas coroas e não decide a precedência; Auriven não pergunta o motivo da chegada; Dorgan não taxa; Brevo não comanda guarnição nem molhe.
- Nenhuma fala atribui natureza a uma raça (W-01); nenhuma cita Aeltharyn, as Coroas de Pedra, o Véu ou o Vale Palancar (contratos de fronteira).

## 2. Conferências

| Verificação | Resultado |
|---|---|
| Principais com voz | 12 de 12 |
| Frases por principal | 3 |
| Campo vazio | 0 |
| Frase que revela suspeita ou desconhecido | 0 (lidas uma a uma) |
| ID fora do registro mestre | 0 |
| Número de tempo ou distância | 0 |

## 3. Decisão pendente

**D-160:** aprovar tom, frases e o que nunca dizem. Opções: **A.** aprovar; **B.** trocar tom ou frase de alguém (diga quem). **Recomendação: A.**

---

# Fechamento da Fase 4 (F4): testes completos

Suíte local, sem rede.

| Verificação | Resultado |
|---|---|
| Scripts Python (`plano/ferramentas`) | 4 de 4 compilam |
| Validador do reino 2 | 0 erro, 40 avisos, 7 exceções (igual ao salvo) |
| Validador do reino 3 | 0 erro, 22 avisos, 6 exceções (igual ao salvo) |
| Validador do reino 4 | 0 erro, 11 avisos, 2 exceções (igual ao salvo) |
| CSV de Azeroth | 9 íntegros (colunas uniformes): registro 63, facções 15, lugares 10, demografia 10, fichas 12, competências 38, relações 91, conhecimento 88, voz 12 |
| IDs do registro | 63 únicos; 0 colisão com os reinos 1 a 4; 0 ID citado nos CSV sem registro |
| Referências inválidas nos docs | 0 (`npc.syranna-das-mares` é Aeltharyn, citado só como veto no doc 10) |
| Regressão | reinos 1 a 4, `referencia/`, `referencia-tecnica/`, `mundo/` e `plano/ferramentas/` idênticos à base; só mudaram os docs de plano, README, CHANGELOG e `reinos/05-azeroth/` |
| Falhas / regressões | 0 / 0 |
