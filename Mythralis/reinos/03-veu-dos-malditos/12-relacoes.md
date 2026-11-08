# 12 — Relações e conhecimento (R3-4.3)

**Estado:** rascunho de R3-4.3 para aprovação do autor (D-85) · **Data:** 2026-11-06
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, nome, lugar, facção, relação ou fato novo**: reúne o que os docs 01, 03, 04, 06 e `12-fichas.md` já dizem. Dados em `12-relacoes.csv` (`origem_id, destino_id, destino_texto, tipo, descricao, fonte, certeza`) e `12-conhecimento.csv` (`npc_id, estado, fato, fonte`), no formato do reino 2.

## 1. Regras

- **Toda relação tem fonte** (coluna `fonte`); `certeza` = C (documentado) ou ASSUMINDO (leitura editorial).
- **Eixos da matriz:** aliança e rivalidade (doc 04 §9 e §10, na liderança de cada facção), dívida (a obrigação do pacto de cada lugar, doc 03; no reino 3 não há cidade-mãe, D-66), tensão (doc 03), vínculo de cargo (mesmo lugar, Conselho, tríade; docs 01, 04 e 06) e medo (fichas, ASSUMINDO).
- **Afeto: 0 relação.** Nenhum doc documenta afeto entre pessoas; nada foi inventado. Fica para o autor decidir (D-85).
- Relação pessoal sem fonte continua `desconhecida`, como nas fichas. Rivais `desconhecido` das facções (CI-11) não geram linha.
- **Conhecimento:** confirmado, suspeita e desconhecido. Os 12 principais vêm das fichas (C, S, D); Nuvik e os 6 benfeitores têm só o confirmado do doc 01 e o desconhecido do que não está escrito; os 11 heróis lendários têm a lenda como confirmada e o que de fato aconteceu como desconhecido (doc 01 parte 2 §3.1 e doc 10 §6.1).
- Nenhuma linha de conhecimento dá a resposta de um mistério (consistente com `13-conferencia-cruzada.md` §3).

## 2. Conferências

| Verificação | Resultado |
|---|---|
| Relações no CSV | **74** (14 alianças, 10 rivalidades, 8 dívidas, 8 tensões, 22 vínculos de cargo, 12 medos, 0 afeto) |
| Relação sem origem (`fonte` vazia) | 0 |
| `origem_id` e `destino_id` fora do registro mestre | 0 |
| Aliança e rivalidade entre o mesmo par | 0 (herdado de `13-conferencia-cruzada.md`) |
| Linhas de conhecimento | **72** (30 confirmado, 12 suspeita, 30 desconhecido) |
| Pessoas do registro com ao menos uma linha de conhecimento | 30 de 30 |
| Segredo de suspeita ou desconhecido em resumo público | 0 (camadas ficam para R3-6.2) |

## 3. Pontos abertos

1. Afeto e dívida entre pessoas: nenhum doc documenta; só dívida de lugar com o pacto (D-85).
2. Morvan sem assento declarado (CI-10): não gera relação de voto.
3. Relações com Aeltharyn e as Coroas: 0 (contrato de fronteira); só rumor em Borda-Mansa.

## 4. Decisão pendente

**D-85:** aprovar a matriz de relações e o conhecimento. Opções: **A.** aprovar; **B.** autorizar relações de afeto ou dívida entre pessoas, marcadas ASSUMINDO (diga quais pares). **Recomendação: A.**
