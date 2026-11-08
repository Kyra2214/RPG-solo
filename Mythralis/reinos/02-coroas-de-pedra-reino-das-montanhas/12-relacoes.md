# 12 — Relações e conhecimento (R2-4.3)

**Estado:** rascunho de R2-4.3 para aprovação do autor · **Data:** 2026-11-04
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, nome, cidade, facção, relação ou fato novo**: reúne o que os docs 01, 03, 04, 06 e `12-fichas.md` já dizem. Dados em `12-relacoes.csv` (`origem_id, destino_id, destino_texto, tipo, descricao, fonte, certeza`) e `12-conhecimento.csv` (`npc_id, estado, fato, fonte`).

## 1. Regras

- **Toda relação tem fonte** (coluna `fonte`); `certeza` = C (documentado) ou ASSUMINDO (leitura editorial das fichas).
- **Eixos da matriz:** aliança e rivalidade (docs 04 §10), dívida (a obrigação documentada de cada liderança de assentamento com a cidade-mãe), tensão (doc 03), vínculo de cargo (mesma cidade ou instituição, docs 01 e 06) e medo (fichas, ASSUMINDO).
- **Afeto: 0 relação.** Nenhum doc documenta afeto entre pessoas; nada foi inventado. Fica para o autor decidir (D-47).
- Relação pessoal sem fonte continua `desconhecida`, como nas fichas.
- **Conhecimento:** confirmado, suspeita e desconhecido. Os 12 principais vêm das fichas (C, S, D); as 20 lideranças só têm o confirmado do doc 03 (sem suspeita ou desconhecido documentado); os 13 heróis lendários têm a lenda como confirmada e o que não se sabe como desconhecido (doc 01 parte 2 e doc 10 §6.1).

## 2. Conferências

| Verificação | Resultado |
|---|---|
| Relações no CSV | 91 (16 alianças, 12 rivalidades, 20 dívidas, 21 tensões, 10 vínculos de cargo, 12 medos) |
| Relação sem origem (`fonte` vazia) | 0 |
| `origem_id` e `destino_id` fora do registro mestre | 0 |
| Aliança e rivalidade entre o mesmo par de facções | 0 (herdado de `13-conferencia-cruzada.md`) |
| Linhas de conhecimento | 82 (45 confirmado, 12 suspeita, 25 desconhecido) |
| Pessoas do registro com ao menos uma linha de conhecimento | 45 de 45 |
| Segredo de suspeita ou desconhecido em resumo público | 0 (camadas ficam para R2-6.2) |
