# 12 — Voz e ganchos de diálogo (R4-4.4)

**Estado:** R4-4.4, D-123 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não cria fato, NPC ou relação novos**. Dados em `12-voz.csv` (`npc_id, tom, frases_modelo, nunca_diz, base`), no molde dos reinos 1 a 3.

## 1. Regras

- 12 principais (N-01 a N-12): `tom` a partir da personalidade da ficha (R4-4.1); 2 a 3 `frases_modelo` (separadas por `|`) como **instrução de tom, não como fala canônica** (ASSUMINDO); `nunca_diz` reúne o que a ficha guarda como suspeita ou desconhecido e o que ultrapassa o limite do cargo.
- Frases só repetem o que já é documentado (cargo, limite, pacto, agenda); nenhuma revela suspeita ou desconhecido das fichas.
- Yorna dá a palavra e não vota; Kharos não preside nem faz pacto; Quira leva recado e não decide; Tessun só tem rumor sobre o outro lado e não liga o pouso a Aeltharyn (contrato de fronteira).
- Nenhuma fala atribui natureza a uma raça (W-01); goblins de legião e Goblins não são "o inimigo".

## 2. Conferências

| Verificação | Resultado |
|---|---|
| Principais com voz | 12 de 12 |
| Frases por principal | 2 a 3 |
| Campo vazio | 0 |
| Frase que revela suspeita ou desconhecido | 0 (conferido por script e lido uma a uma) |
| ID fora do registro mestre | 0 |
| Número de tempo ou distância | 0 |

## 3. Decisão pendente

**D-123:** aprovar tom, frases e o que nunca dizem. Opções: **A.** aprovar; **B.** trocar tom ou frase de alguém (diga quem). **Recomendação: A.**

---

# Fechamento da Fase 4 (F4): testes completos

Suíte local: validador dos reinos 2 e 3, compilação dos scripts, integridade dos CSV e conferências do reino 4.

| Verificação | Resultado |
|---|---|
| Validador do reino 2 | 0 erro, 40 avisos, 7 exceções; relatório idêntico ao salvo |
| Validador do reino 3 | 0 erro, 22 avisos, 6 exceções; relatório idêntico ao salvo |
| Scripts Python (`plano/ferramentas`) | 3 de 3 compilam |
| CSV do reino 4 | 9 íntegros (colunas uniformes em todas as linhas): registro 47, facções 14, lugares 8, demografia 8, fichas 12, competências 25, relações 81, conhecimento 66, voz 12 |
| IDs do registro | 47 únicos; 0 colisão com os registros dos reinos 1 a 3; 0 ID citado nos CSV sem registro |
| Voz = fichas | 12 de 12 |
| Competências no catálogo | 0 ID fora do `personagem-catalogo.json` |
| Aliança e rivalidade no mesmo par; assimetria; relação sem fonte | 0; 0; 0 |
| Referências de arquivo nos `.md` do reino 4 | 0 inválida |
| Regressão | reinos 1, 2 e 3, `mundo/`, `referencia/` e `referencia-tecnica/` byte a byte iguais à base da sessão; nenhum arquivo de ferramenta alterado |
| Contrato de fronteira | nenhuma pessoa, relação ou fala envolve Aeltharyn, as Coroas ou o Véu além do contrato |
| Dependência de rede | nenhuma verificação ignorada |
