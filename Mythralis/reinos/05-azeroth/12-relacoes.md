# Azeroth — Relações e conhecimento (doc 12, R5-4.3)

**Estado:** R5-4.3, D-159 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, nome, lugar, facção, relação ou fato novo**: reúne o que os docs 01, 03, 04 e `12-fichas.md` já dizem. Dados em `12-relacoes.csv` (`origem_id, destino_id, destino_texto, tipo, descricao, fonte, certeza`) e `12-conhecimento.csv` (`npc_id, estado, fato, fonte`), no formato dos reinos 2, 3 e 4.

## 1. Regras

- **Toda relação tem fonte** (coluna `fonte`); `certeza` = C (documentado) ou ASSUMINDO (leitura editorial).
- **Eixos da matriz:** aliança e rivalidade (doc 04 §10, a partir do líder de cada facção), dívida (o apoio e tributo do lugar ao lugar-mãe, doc 03 §1), tensão (doc 03 §1), vínculo de cargo (governante e herdeiro, doc 04 §2.2; órgão local e governante da coroa, doc 04 §9; guardião e benfeitor da tríade, doc 01 parte 2 §3) e medo (fichas, ASSUMINDO).
- **Origem da dívida e da tensão de um lugar:** o chefe do órgão local (doc 04 §9). Em Concharia e Estrelado o chefe é `desconhecido` (a voz do órgão está em disputa); a origem passa a ser o guardião do lugar na tríade (Orrume, Eloh), marcada ASSUMINDO, sem declarar chefe.
- **Afeto: 0 relação.** Nenhum doc documenta afeto entre pessoas; nada foi inventado. Fica para o autor decidir (D-159).
- Relação pessoal sem fonte continua `desconhecida`. O rival `desconhecido` de Pilotos da Enseada (F-07, motivo no doc 04) não gera linha.
- **Conhecimento:** confirmado, suspeita e desconhecido. Os 12 principais vêm das fichas (C, S, D); os 18 sem ficha têm o confirmado do papel (docs 01 e 04) e o desconhecido do que não está escrito, sem suspeita (nenhum doc a registra); os 8 heróis lendários têm a lenda como confirmada e o que de fato aconteceu como desconhecido (doc 01 parte 2 §3.1).
- Nenhuma linha dá a resposta de um mistério. Nenhuma relação toca Aeltharyn, as Coroas, o Véu ou o Vale Palancar (contrato de fronteira).

## 2. Conferências

| Verificação | Resultado |
|---|---|
| Relações no CSV | **91** (26 alianças, 16 rivalidades, 6 dívidas, 10 tensões, 21 vínculos de cargo, 12 medos, 0 afeto) |
| Relação sem origem (`fonte` vazia) | 0 |
| `origem_id` e `destino_id` fora do registro mestre | 0 |
| Aliança e rivalidade entre o mesmo par | 0 |
| Linhas de conhecimento | **88** (38 confirmado, 12 suspeita, 38 desconhecido) |
| Pessoas do registro com ao menos uma linha de conhecimento | 38 de 38 |
| Principais iguais às fichas (C, S, D) | 12 de 12 |
| Campo vazio | 0 |
| Mestiço, raça "vilã" ou "leal por natureza" | 0 |

## 3. Pontos abertos

1. Afeto e dívida entre pessoas: nenhum doc documenta; só dívida de lugar com o lugar-mãe (D-159).
2. Chefe dos Mestres de Concha e da Mesa dos Astros `desconhecido`: não gera relação de chefia.
3. Os 18 sem ficha não têm suspeita; só se o autor pedir ficha completa a eles.

## 4. Decisão pendente

**D-159:** aprovar a matriz de relações e o conhecimento. Opções: **A.** aprovar; **B.** autorizar relações de afeto ou dívida entre pessoas, marcadas ASSUMINDO (diga quais pares). **Recomendação: A.**
