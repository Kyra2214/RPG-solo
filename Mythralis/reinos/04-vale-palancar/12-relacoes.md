# 12 — Relações e conhecimento (R4-4.3)

**Estado:** R4-4.3, D-122 (aguardando o autor) · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, nome, lugar, facção, relação ou fato novo**: reúne o que os docs 01, 03, 04 e `12-fichas.md` já dizem. Dados em `12-relacoes.csv` (`origem_id, destino_id, destino_texto, tipo, descricao, fonte, certeza`) e `12-conhecimento.csv` (`npc_id, estado, fato, fonte`), no formato dos reinos 2 e 3.

## 1. Regras

- **Toda relação tem fonte** (coluna `fonte`); `certeza` = C (documentado) ou ASSUMINDO (leitura editorial).
- **Eixos da matriz:** aliança e rivalidade (doc 04 §9, na liderança de cada facção), dívida (a obrigação do Trato de cada lugar, doc 03, e o apoio ao lugar-mãe dos quatro sazonais com lugar-mãe), tensão (doc 03), vínculo de cargo (mesmo lugar, tríade, Assembleia e Capitão; docs 01, 03 e 04) e medo (fichas, ASSUMINDO).
- **Origem da dívida e da tensão de um lugar:** o chefe do Trato (doc 04 §8). Em Rasovau e Encosta-Aberta o chefe é `desconhecido` (o Trato está em disputa); a origem passa a ser o guardião do lugar na tríade (Zinu, Sendro), marcada ASSUMINDO, sem declarar chefe.
- **Afeto: 0 relação.** Nenhum doc documenta afeto entre pessoas; nada foi inventado. Fica para o autor decidir (D-122).
- Relação pessoal sem fonte continua `desconhecida`, como nas fichas. Rivais `desconhecido` de Guardas da Marcha e Vigias da Cisterna (CI-08) não geram linha.
- **Conhecimento:** confirmado, suspeita e desconhecido. Os 12 principais vêm das fichas (C, S, D); os 7 sem ficha têm o confirmado de docs 01 e 04, o desconhecido do que não está escrito e, em 4 deles, a suspeita que os mistérios já registram em "provas possíveis" (Lumbo, Aruun, Sendro, Ghorla); os 6 heróis lendários têm a lenda como confirmada e o que de fato aconteceu como desconhecido (doc 01 parte 2 §3.1).
- Nenhuma linha de conhecimento dá a resposta de um mistério (consistente com `13-conferencia-cruzada.md` §3). Nenhuma relação toca Aeltharyn, as Coroas ou o Véu (contrato de fronteira).

## 2. Conferências

| Verificação | Resultado |
|---|---|
| Relações no CSV | **81** (22 alianças, 12 rivalidades, 12 dívidas, 8 tensões, 15 vínculos de cargo, 12 medos, 0 afeto) |
| Relação sem origem (`fonte` vazia) | 0 |
| `origem_id` e `destino_id` fora do registro mestre | 0 |
| Aliança e rivalidade entre o mesmo par | 0 (herdado de `13-conferencia-cruzada.md`) |
| Linhas de conhecimento | **66** (25 confirmado, 16 suspeita, 25 desconhecido) |
| Pessoas do registro com ao menos uma linha de conhecimento | 25 de 25 |
| Principais iguais às fichas (C, S, D) | 12 de 12 |
| Segredo de suspeita ou desconhecido em resumo público | 0 (camadas ficam para R4-6.2) |
| Mestiço, raça "vilã" ou "leal por natureza" | 0 |

## 3. Pontos abertos

1. Afeto e dívida entre pessoas: nenhum doc documenta; só dívida de lugar com o Trato e com o lugar-mãe (D-122).
2. Chefe do Trato do Vau e do Trato da Encosta `desconhecido` (CI-04): não gera relação de chefia.
3. Relações com Aeltharyn, as Coroas e o Véu: 0 (contrato de fronteira); só rumor em Encosta-Aberta.

## 4. Decisão pendente

**D-122:** aprovar a matriz de relações e o conhecimento. Opções: **A.** aprovar; **B.** autorizar relações de afeto ou dívida entre pessoas, marcadas ASSUMINDO (diga quais pares). **Recomendação: A.**
