# Azeroth — demografia (doc 12)

**Estado:** R5-3.2, D-154 aceita pela continuação · **Data:** 2026-11-07 · **Etiqueta:** **[PROPOSTO DM-01 a DM-05; faixas ASSUMINDO]**
**Fonte:** as faixas já estão no doc 01 (campo População de cada lugar, aprovado em D-142, ASSUMINDO D-63); este doc as reúne, dá a evidência e fixa as regras de uso. Nenhum doc do reino 5 traz população; a faixa é **porte relativo**, nunca número. Dados em `12-demografia.csv` (molde dos reinos 2 a 4: `valor_perfil` = "sem número", mais `evidencia` e `confianca`).

## 1. Regras

1. **Faixa, não número (DM-01).** *Pequena*, *média* ou *grande* **em relação aos outros nove lugares**. Nenhuma faixa equivale a um intervalo de habitantes.
2. **Não somar (DM-02).** Nunca somar faixas entre lugares; nunca apresentar "total do reino", "população média" ou "percentual de povos". **Nenhuma faixa é censo.**
3. **Todos os lugares são fixos (DM-03).** Não há lugar sazonal; a faixa não muda de campo nem de estação. Uma cheia, uma praga de campo, um bloqueio do porto ou uma cerração longa (doc 01, defesas e vulnerabilidades) pesam sobre o lugar sem virar número.
4. **População flutuante não quantificada (DM-04):** visitantes avulsos (Elfo, Anão, Gnomo, Goblin, Ratoide, Escamado), viajantes e mercadores, quem chega pelas Costas e os chegados do Véu em Meiovau **não entram** em nenhuma faixa. Mestiços não são alocados (D-64).
5. **Proporção entre povos: desconhecida (DM-05).** O doc 01 e o doc 10 dizem quais povos estão em cada lugar, não quantos. Nada de "maioria" de um povo sem decisão; nenhum povo é maioria, cidadão ou leal por natureza (W-01). As três coroas e a Mesa de Meiovau não têm população própria.
6. **O reino não tem assentamentos além dos dez lugares** (D-141, V-31): não há vila nem posto para somar.

## 2. Faixas

| Lugar | Regime | Faixa | Evidência |
|---|---|---|---|
| Campoalvo | fixo | grande | capital de coroa de planície; celeiros comuns que guardam o excedente de várias vilas; o preço do pão do reino se discute aqui |
| Vaudouro | fixo | média | cidade fluvial de mercado na junção de dois rios, sem muralha, só comportas e vigias de canal |
| Torrefronte | fixo | pequena | fortaleza de limite de torre alta e muro de terra, que depende de suprimento de dentro do reino |
| Alvamar | fixo | grande | capital de coroa e porto com molhe e corrente; recebe a rota das Costas na Doca dos Povos do Mar |
| Enseada-Vigia | fixo | média | porto de rota marítima, de faróis e barra difícil, que recebe quem chega pelas Costas |
| Concharia | fixo | pequena | estaleiro e santuário numa enseada de recifes, com entrada por um canal estreito |
| Torreclara | fixo | média | capital de coroa e cidade de torres de sinais, com pouca força própria além dos sinais |
| Estrelado | fixo | pequena | observatório e arquivo celeste, longe de rio e de cidade, sem defesa própria |
| Pouso-das-Brisas | fixo | pequena | estação de torres de madeira e vigias e pouso de rota entre o interior e a costa |
| Meiovau | fixo | média | cidade livre, ponto de encontro das três coroas, sem muralha; recebe visitantes e chegados do Véu |

Confiança `baixa` nas dez (faixa ASSUMINDO no doc 01). Nenhum lugar tem número, intervalo ou ordem de grandeza.

## 3. Conferências

- 10 faixas (2 grandes, 4 médias, 4 pequenas) conferidas por script contra o campo População do doc 01: 0 diferença; 10 IDs existem no registro mestre.
- 0 número, intervalo, total, média ou percentual; 0 censo; 0 proporção entre povos.
- Flutuantes, visitantes e chegados fora de qualquer faixa.
- Aeltharyn, Véu, Coroas, Vale, Mistério C e Neblina Longa intocados.

## 4. Decisão pendente

**D-154:** as 10 faixas e as regras DM-01 a DM-05. **ASSUMINDO:** faixa como porte relativo dentro do reino; `confianca` baixa em todas. Opções: **A.** aprovar; **B.** trocar a faixa de algum lugar (diga qual). **Recomendação: A.**
