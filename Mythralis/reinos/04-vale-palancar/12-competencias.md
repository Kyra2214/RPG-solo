# 12 — Competências pelo catálogo (R4-4.2)

**Estado:** R4-4.2, D-121 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, nome, lugar, facção ou fato novo**. Dados em `12-competencias.csv` (mesmo formato dos reinos 2 e 3: `id, nome, raca_id, subraca_id, classe_id, profissao_id, capacidades, limites, evidencia`).

## 1. Regras

- **IDs** de raça, sub-raça, classe e profissão vêm de `referencia/racas-e-povos/ALINHAMENTO_PALANCAR.md` (D-114) e existem em `referencia/dados-brutos/personagem-catalogo.json`; classe e profissão são leitura editorial (ASSUMINDO). Sub-raça `—` onde o catálogo não atribui uma ao povo (13 pessoas); profissão `—` onde não há ofício escrito (15 pessoas).
- **Capacidades e limites** só repetem o que os docs já dizem: os 12 principais, a ficha (`12-fichas.md`); os 7 sem ficha, o papel do doc 01 parte 2 §3 e do doc 04 §8 e §9; os 6 heróis lendários, a lenda (doc 01 parte 2 §3.1). Nada de nível, bônus numérico, magia ou habilidade nova.
- Heróis lendários estão falecidos: limite padrão = "não age no início; o que de fato aconteceu é desconhecido".
- Pessoas sem ficha: modo e meios do ofício e limite do cargo ficam `desconhecidos`; não se inventa ofício.
- O catálogo não vincula habilidades mecânicas a classe ou profissão (`competencies` vazio nas 50 classes e nas 104 profissões); por isso não há ID de habilidade a citar.
- Os Tratos, a Assembleia e o Capitão têm os limites já escritos (doc 04 §1 a §3); nada aqui os amplia nem os reduz.

## 2. Conferências

| Verificação | Resultado |
|---|---|
| Pessoas no registro mestre × linhas do CSV | 25 de 25 |
| Principais / sem ficha / heróis lendários | 12 / 7 / 6 |
| ID de raça, sub-raça, classe ou profissão fora do catálogo | 0 |
| Sub-raça com raça-mãe errada | 0 |
| Raça diferente de `povo_id` do registro | 0 |
| Campo vazio | 0 |
| Capacidade sem limite | 0 |
| Classe e profissão iguais às de `12-fichas.csv` e `ALINHAMENTO_PALANCAR.md` | 12 de 12 (as 25 conferem com o alinhamento) |
| Habilidade mecânica ou número inventado | 0 |

## 3. Pontos abertos

1. Kharos tem classe `comandante`, mas o catálogo não tem a de capitão (D-114); não muda.
2. Profissão `—` em 15 pessoas (sem ofício escrito); não se inventa.
3. Bolgra Forja-de-Campo consta como `ferreiro` (leitura editorial do doc 01 §2.6, "forja de campo"); a origem do metal segue `desconhecida` (doc 05 §2).
4. As competências dos 7 sem ficha só serão aprofundadas se o autor pedir ficha completa a eles.

## 4. Decisão pendente

**D-121:** aprovar as competências e limites das 25 pessoas. Opções: **A.** aprovar; **B.** trocar classe, profissão ou limite de alguém (diga quem). **Recomendação: A.**
