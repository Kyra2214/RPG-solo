# Azeroth — relatório de conferência cruzada (R5-2.4)

**Estado:** R5-2.4, D-152 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; não cria fato novo. Confere os docs 01, 03, 04, 05, 06, 10, o registro mestre, `12-facoes.csv`, `12-assentamentos.csv` e `ALINHAMENTO_AZEROTH.md`. Verificações por script, no molde de `reinos/04-vale-palancar/13-conferencia-cruzada.md`.

## 1. Aliado e rival ao mesmo tempo

| Verificação | Resultado |
|---|---|
| facção que é aliada e rival da mesma outra | 0 |
| aliança assimétrica (A aliada de B, B não de A) | 0 |
| rivalidade assimétrica | 0 |
| aliada de uma rival direta (A–B aliadas, B–C rivais, A–C aliadas) | 0 |
| liderança de facção fora dos lugares de presença da facção | 0 |
| pessoa com povo ausente da lista de povos nativos do lugar (doc 10 §4) | 0 |
| facções presentes por lugar: doc 04 §10.2 × `12-facoes.csv` | 0 diferenças (10 lugares) |
| pessoa que lidera mais de uma facção | 0 |
| lugar com líder fora do próprio lugar em `12-assentamentos.csv` | 0 |

1 `rivais` segue `desconhecido` (F-07, Pilotos da Enseada), com motivo no doc 04 (CI-08). Alvamar tem dois chefes (Pelmo e Dorgan) por desenho (doc 04 §9).

## 2. Idades e cargos duplicados

### 2.1 Idades
Faixas etárias das 12 fichas: 6 madura, 3 jovem, 2 adulta, 1 idosa. Nenhuma idade numérica; 0 conflito. As 26 pessoas sem ficha têm faixa `desconhecida` ou só lenda.

### 2.2 Cargos duplicados

**Acúmulos já declarados no doc 04 §10** (cargo de ficha e liderança de facção):

| Pessoa | Cargos | Situação |
|---|---|---|
| Solenne Vigília-Longa (N-07) | comandante e guardiã de Torrefronte; F-02 Guarnição do Limite | **intencional proposto (CI-07)** |
| Dorgan Escudo-Lento (N-08) | comandante e guardião de Alvamar; F-05 Guarda da Corrente | **intencional proposto (CI-07)** |
| Nerella Canto-da-Barra (N-09) | herói vivo; F-07 Pilotos da Enseada | **intencional proposto (CI-07)** |
| Auriven Porta-Acesa (N-12) | benfeitor de Meiovau; F-14 Albergue dos Chegados | **intencional proposto (CI-07)** |

**Achado e declarado no R5-2.4:** nove pessoas da tríade (guardião ou benfeitor do lugar) também lideram a facção local, e o doc 04 §10 não as listava como acúmulo: Helion (F-01), Ebbo (F-03), Perrin (F-06), Orrume (F-08), Corali (F-09), Casimar (F-10), Ixra (F-11), Eloh (F-12) e Hollis (F-13). **Declarados** no doc 04 (de 4 para 13 acúmulos); **intencional proposto (CI-07)**.

Sem acúmulo: Pelmo (F-04) e Ruvane (F-15) lideram só a facção; Odile, Corlan, Brevo, Tomlin, Rilla, Astrel, Tolla e Garvane não lideram facção; os 8 heróis lendários e os benfeitores restantes não têm cargo de facção.

## 3. Quem sabe o quê

Fonte: doc 01 parte 4 (MA-01 a MA-10), campos "Parte de" e "Provas possíveis".

| Mistério | Quem guarda uma parte | Suspeita ou ignora |
|---|---|---|
| MA-01 Campoalvo | Odile (N-01) | **pode calar** o tamanho dos celeiros; Tomlin (N-04) **pode divergir** |
| MA-02 Vaudouro | Garvane (N-10) | **pode recusar** dizer por que deixou passar a barcaça |
| MA-03 Torrefronte | Solenne (N-07) | **pode recusar** mostrar as páginas |
| MA-04 Alvamar | Corlan (N-02); Rilla (N-05); Dorgan (N-08) | Rilla e Dorgan **podem calar** o que viram e por que afrouxaram a corrente |
| MA-05 Enseada-Vigia | Nerella (N-09) | **pode recusar** ensinar a rota |
| MA-06 Concharia | Orrume e Corali | **podem divergir** sobre o casco e o coral |
| MA-07 Torreclara | Brevo (N-03) | **pode calar** quem guarda o código-mestre |
| MA-08 Estrelado | Astrel (N-06); Ixra; Eloh | Astrel **pode calar** o que leu; Ixra e Eloh **podem divergir** sobre o mapa |
| MA-09 Pouso-das-Brisas | Hollis | **pode calar** quem ensina o fio das pipas |
| MA-10 Meiovau | Tolla (N-11); Auriven (N-12) | **podem calar** a chave e a origem da doação |

0 mistério com resposta escolhida (doc 01 parte 4 §2: "Resposta única: nenhuma"); 0 NPC que sabe uma resposta.

## 4. Contradições intencionais e pontos abertos

| ID | Contradição ou lacuna | Onde está | Por que fica |
|---|---|---|---|
| CI-01 | duas versões da lenda de cada um dos 10 lugares | doc 01 parte 3 §3 | cada lugar conta a sua |
| CI-02 | quem tem a precedência entre as três coroas | docs 01, 03 e 04 §1 | motor do reino; a Mesa não decide a precedência |
| CI-03 | quem tem a chave copiada na Mesa de Meiovau (`desconhecida`) | doc 04 §3.2; MA-10 | gancho central |
| CI-04 | Concharia e Estrelado sem chefe (`lideranca_id` `desconhecido`) | doc 04 §9; `12-assentamentos.csv` | a voz do órgão é o que está em disputa |
| CI-05 | crime grave, exílio e pena de morte não fixados; lacunas L-01 a L-10 | doc 04 §6; doc 05 §1 | aberto de propósito |
| CI-06 | três costumes de sucessão, um por coroa; como cada governante foi reconhecido e se algum herdeiro já foi revogado é `desconhecido` | doc 04 §2.2 | desenho do reino |
| CI-07 | 13 acúmulos de cargo (§2.2) | docs 01 e 04 | intencional proposto |
| CI-08 | 1 rival `desconhecido` (F-07) | doc 04 §10 | motivo registrado |
| CI-09 | origem e sentido de "Azeroth" `desconhecidos`, com duas versões | doc 01 parte 3 §2 | nome de reino, não de lugar |
| CI-10 | lacunas de papel por povo (3 sem herói, 4 sem guardião, 2 sem benfeitor) | doc 10 §6 | não se inventa pessoa |
| CI-11 | 17 forasteiros sem lugar fixo; origem e caminho `desconhecidos` | doc 10 §5 | sem rota (contrato A2) |
| CI-12 | pontos abertos do contrato P-2 (qual pântano) e P-3 (fronteira com o Véu na foz) | `CONTRATO_DE_FRONTEIRA_PACTO_HUMANO.md` | herdados; nenhum texto afirma foz do Argente |

### 4.1 Inconsistências acidentais encontradas e tratadas
- Nove acúmulos tríade + facção fora da lista declarada do doc 04 §10: **declarados** (de 4 para 13).
- Nenhuma outra: 0 ID duplicado, 0 citado sem registro, 0 número, tempo ou rota novo.

## 5. Conferências da Fase 2

| Item | Resultado |
|---|---|
| IDs únicos do registro | 63 (10 lugares, 15 facções, 38 pessoas), 0 duplicado |
| IDs citados nos docs sem registro | 0 |
| campos vazios em `12-facoes.csv` e `12-assentamentos.csv` | 0 |
| IDs de raça, sub-raça, classe e profissão fora do catálogo | 0 |
| colisão de ID com os reinos 1 a 4 | 0 |
| arquivos dos reinos 1 a 4 alterados nesta fase | 0 |

## 6. Decisão pendente

**D-152:** aprovar o relatório, os 13 acúmulos de cargo como intencionais (CI-07) e as contradições e pontos abertos CI-01 a CI-12. Opções: **A.** aprovar; **B.** desfazer algum acúmulo ou fechar uma lacuna (diga qual). **Recomendação: A.**
