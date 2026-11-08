# 12 — Competências pelo catálogo (R3-4.2)

**Estado:** rascunho de R3-4.2 para aprovação do autor (D-84) · **Data:** 2026-11-06
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, nome, lugar, facção ou fato novo**. Dados em `12-competencias.csv` (mesmo formato do reino 2: `id, nome, raca_id, subraca_id, classe_id, profissao_id, capacidades, limites, evidencia`).

## 1. Regras

- **IDs** de raça, sub-raça, classe e profissão vêm de `referencia/racas-e-povos/ALINHAMENTO_VEU.md` (D-77) e existem em `referencia/dados-brutos/personagem-catalogo.json`; classe e profissão são leitura editorial (ASSUMINDO). Morvan segue `invocador` (necromante não existe no catálogo, D-66).
- **Capacidades e limites** só repetem o que os docs já dizem: os 12 principais, a ficha (`12-fichas.md`); os 7 da tríade sem ficha, o papel do doc 01 §3 (e, para Nuvik, o doc 04); os 11 heróis lendários, a lenda (doc 01 parte 2 §3.1 e doc 10 §6.1). Nada de nível, bônus numérico, magia ou habilidade nova.
- Heróis lendários estão falecidos: limite padrão = "não age no início; o que de fato aconteceu é desconhecido".
- Benfeitores sem ficha: modo e meios do benefício ficam `desconhecidos`; não se inventa ofício.
- O catálogo não vincula habilidades mecânicas a classe ou profissão (`competencies` vazio); por isso não há ID de habilidade a citar.

## 2. Conferências

| Verificação | Resultado |
|---|---|
| Pessoas no registro mestre × linhas do CSV | 30 de 30 |
| Principais / tríade sem ficha / heróis lendários | 12 / 7 / 11 |
| ID de raça, sub-raça, classe ou profissão fora do catálogo | 0 |
| Sub-raça com raça-mãe errada | 0 |
| Raça diferente de `povo_id` do registro | 0 |
| Campo vazio | 0 |
| Capacidade sem limite | 0 |
| Classe e profissão iguais às de `12-fichas.csv` e `ALINHAMENTO_VEU.md` | 12 de 12 |

## 3. Pontos abertos

1. Necromancia de Morvan: ofício com custo e limites (doc 01 parte 4 §3); o catálogo não tem a classe e não muda.
2. Profissão `—` em 11 pessoas (sem ofício escrito); não se inventa.
3. Corvídeo (`kenku`) × Corvário (`homem-corvo`) segue aberto (CI-12).

## 4. Decisão pendente

**D-84:** aprovar as competências e limites das 30 pessoas. Opções: **A.** aprovar; **B.** trocar classe, profissão ou limite de alguém (diga quem). **Recomendação: A.**
