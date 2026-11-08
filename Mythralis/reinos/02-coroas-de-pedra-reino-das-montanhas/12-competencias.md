# 12 — Competências pelo catálogo (R2-4.2)

**Estado:** rascunho de R2-4.2 para aprovação do autor · **Data:** 2026-11-04
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, nome, cidade, facção ou fato novo**. Dados em `12-competencias.csv` (mesmo formato do reino 1: `id, nome, raca_id, subraca_id, classe_id, profissao_id, capacidades, limites, evidencia`).

## 1. Regras

- **IDs** de raça, sub-raça, classe e profissão vêm de `referencia/racas-e-povos/ALINHAMENTO_COROAS.md` (D-39) e existem em `referencia/dados-brutos/personagem-catalogo.json`; classe e profissão são leitura editorial (ASSUMINDO).
- **Capacidades e limites** só repetem o que os docs já dizem: os 12 principais, a ficha (`12-fichas.md`); as 20 lideranças de assentamento, a obrigação e a tensão do doc 03 e o governo local do doc 04; os 13 heróis lendários, a lenda (doc 01 parte 2 e doc 10 §6.1). Nada de nível, bônus numérico, magia ou habilidade nova.
- Heróis lendários estão falecidos: limite padrão = "não age no início; o que de fato aconteceu é desconhecido".
- O catálogo não vincula habilidades mecânicas a classe ou profissão (`competencies` vazio); por isso não há ID de habilidade a citar.

## 2. Conferências

| Verificação | Resultado |
|---|---|
| Pessoas no registro mestre × linhas do CSV | 45 de 45 |
| Principais / lideranças / heróis lendários | 12 / 20 / 13 |
| ID de raça, sub-raça, classe ou profissão fora do catálogo | 0 |
| Sub-raça com raça-mãe errada | 0 |
| Raça diferente de `povo_id` do registro | 0 |
| Campo vazio | 0 |
| Capacidade sem limite | 0 |
