# Azeroth — Competências pelo catálogo (doc 12, R5-4.2)

**Estado:** R5-4.2, D-158 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, nome, lugar, facção ou fato novo**. Dados em `12-competencias.csv` (mesmo formato dos reinos 2, 3 e 4: `id, nome, raca_id, subraca_id, classe_id, profissao_id, capacidades, limites, evidencia`).

## 1. Regras

- **IDs** de raça, sub-raça, classe e profissão vêm de `referencia/racas-e-povos/ALINHAMENTO_AZEROTH.md` (D-151) e existem em `referencia/dados-brutos/personagem-catalogo.json`; classe e profissão são leitura editorial (ASSUMINDO). Sub-raça `—` onde o catálogo não atribui uma ao povo (11 pessoas); profissão `—` onde não há ofício escrito (25 pessoas); classe `—` nos 8 heróis lendários (sem feito escrito).
- **Capacidades e limites** só repetem o que os docs já dizem: os 12 principais, a ficha (`12-fichas.md`); os 18 sem ficha, o papel do doc 01 parte 2 §3 e do doc 04 §9 e §10; os 8 heróis lendários, a lenda (doc 01 parte 2 §3.1). Nada de nível, bônus numérico, magia ou habilidade nova.
- Heróis lendários estão falecidos: limite padrão = "não age no início; o que de fato aconteceu é desconhecido".
- Pessoas sem ficha: modo e meios do ofício e limite do cargo ficam `desconhecidos`; não se inventa ofício.
- O catálogo não vincula habilidades mecânicas a classe ou profissão; por isso não há ID de habilidade a citar.
- Coroas, Mesa de Meiovau e órgãos locais mantêm as competências e os limites do doc 04 §4; nada aqui os amplia nem os reduz.

## 2. Conferências

| Verificação | Resultado |
|---|---|
| Pessoas no registro mestre × linhas do CSV | 38 de 38 |
| Principais / sem ficha / heróis lendários | 12 / 18 / 8 |
| ID de raça, sub-raça, classe ou profissão fora do catálogo | 0 |
| Sub-raça com raça-mãe errada | 0 |
| Raça diferente de `povo_id` do registro | 0 |
| Campo vazio | 0 |
| Capacidade sem limite | 0 |
| Classe e profissão iguais às de `12-fichas.csv` e `ALINHAMENTO_AZEROTH.md` | 38 de 38 |
| Habilidade mecânica ou número inventado | 0 |

## 3. Pontos abertos

1. Classe e profissão são aproximações do papel escrito (D-151); não mudam.
2. Profissão `—` em 25 pessoas (sem ofício escrito); não se inventa.
3. Os 18 sem ficha só terão competências aprofundadas se o autor pedir ficha completa a eles.
4. Concharia e Estrelado não têm chefe de órgão (`desconhecido`, doc 04 §9); nada aqui o define.

## 4. Decisão pendente

**D-158:** aprovar as competências e limites das 38 pessoas. Opções: **A.** aprovar; **B.** trocar classe, profissão ou limite de alguém (diga quem). **Recomendação: A.**
