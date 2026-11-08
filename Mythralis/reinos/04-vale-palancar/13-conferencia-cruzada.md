# Vale Palancar — relatório de conferência cruzada (R4-2.4)

**Estado:** R4-2.4, D-115 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; não cria fato novo. Confere os docs 01, 03, 04, 05, 06, 10, o registro mestre, `12-facoes.csv`, `12-assentamentos.csv` e `ALINHAMENTO_PALANCAR.md`. Verificações por script, no molde de `reinos/03-.../13-conferencia-cruzada.md`.

## 1. Aliado e rival ao mesmo tempo

| Verificação | Resultado |
|---|---|
| facção que é aliada e rival da mesma outra | 0 |
| aliança assimétrica (A aliada de B, B não de A) | 0 |
| rivalidade assimétrica | 0 |
| aliada de uma rival direta (A–B aliadas, B–C rivais, A–C aliadas) | 0 |
| liderança de facção fora dos lugares de presença da facção | 2 (Clãs da Prova e Clãs da Aclamação; **intencional proposto, CI-09**) |
| pessoa com povo ausente da lista de povos presentes do lugar | 0 |
| facções presentes por lugar: doc 01 × `12-facoes.csv` | 0 diferenças |
| pessoa que lidera mais de uma facção | 0 |
| lugar com mais de um líder em `12-assentamentos.csv` | 0 |

2 `rivais` seguem `desconhecido`, com motivo no doc 04 (CI-08).

## 2. Idades e cargos duplicados

### 2.1 Idades
Faixas etárias das 12 fichas: 5 madura, 4 adulta, 3 idosa. Nenhuma idade numérica; 0 conflito. As 13 pessoas sem ficha têm faixa `desconhecida` ou só lenda.

### 2.2 Cargos duplicados

Pessoa que **chefia um Trato ou comanda e também lidera uma facção**:

| Pessoa | Cargos | Situação |
|---|---|---|
| Kharos Casco-de-Marcha (N-01) | Capitão de Marcha; F-01 Guardas da Marcha | **intencional proposto (CI-07)** |
| Gruzk Estandarte-Baixo (N-03) | comandante das vigias de Alvasal; F-02 Clãs da Prova | **intencional proposto (CI-07)** |
| Moura Mel-Guardado (N-04) | chefe do Trato do Celeiro; F-04 Guardiões da Reserva | **intencional proposto (CI-07)**; declarado no R4-2.4 |
| Zinu Troca-Justa (N-08) | chefe de um dos grupos do vau; F-06 Pedras de Cima | **intencional proposto (CI-07)** |
| Vrissk Colônia-Quieta (N-10) | chefe do Trato do Desembarque; F-08 Receptores do Pouso | **intencional proposto (CI-07)** |
| Aruun Poço-Compartilhado | chefe do Trato do Poço; F-10 Vigias da Cisterna | **intencional proposto (CI-07)**; declarado no R4-2.4 |
| Tibbe Salto-Curto (N-11) | chefe do Trato dos Rebanhos; F-13 Clãs do Gado | **intencional proposto (CI-07)** |

Sem acúmulo: Yorna (chefia o Trato do Terreiro, sem facção), Pocha, Cassan, Rukhan, Quira, Tessun, Sendro, Cadrek, Grebb, Ghorla e Lumbo lideram no máximo uma facção, sem chefiar Trato. Os heróis lendários não têm cargo.

## 3. Quem sabe o quê

| Mistério | Quem guarda uma parte | Suspeita ou ignora | Quem não pode saber |
|---|---|---|---|
| MPL-01 marco sem clã | Moura (N-04) | **pode recusar** conferir o marco; ninguém confirma de quem é | ninguém decide o direito à reserva |
| MPL-02 conta que não fecha | Moura (N-04); Pocha (N-05) | Moura **suspeita-se** de guardar reserva fora da conta; **ignora-se** de onde vem a reserva de Pocha | ninguém liga a reserva a lugar fora do Vale |
| MPL-03 pedra que mudou | Zinu (N-08); Lumbo; Quira (N-09) | Quira **suspeita-se** de guardar recados não entregues; **ignora-se** a quem pertencem as pedras | ninguém decide a posse das pedras |
| MPL-04 degrau a mais | Vrissk (N-10) | **suspeita-se** de ter recusado carga por motivo que não diz; **ignora** o que mais chega | ninguém sabe a causa da Descida |
| MPL-05 cisterna salgada | Gruzk (N-03); Aruun | Gruzk **suspeita-se** de ter negado água a quem tinha direito; os dois **podem divergir** sobre quanta água há | ninguém sabe nada do Ermo ou de Beira-Sal |
| MPL-06 pedra que esfria | Yorna (N-02); Cassan (N-12) | Yorna **suspeita-se** de saber um costume esquecido; **ignora** o que a Pedra registra | ninguém usa a Pedra para decidir a sucessão |
| MPL-07 mapa e terceiro campo | Kharos (N-01); Rukhan (N-07) | Kharos **suspeita-se** de ter escolhido o sucessor; Rukhan **suspeita-se** de ter visto um terceiro campo; **ignora-se** o que viu na última vez que se afastou | ninguém sabe rota para outro reino |
| MPL-08 campo nunca mais escolhido | Yorna (N-02); Cassan (N-12) | Cassan **suspeita-se** de saber qual clã apoia qual lado; **ignora** por que a Ramada nunca fica vazia | ninguém liga o campo a lore de outro reino |
| MPL-09 rumo trocado | Tessun (N-06); Sendro | Tessun **suspeita-se** de ter ouvido do outro lado mais do que conta; **ignora** por que ficou do lado de cá | ninguém sabe o que há do outro lado além do rumor |
| MPL-10 capim em círculo | Tibbe (N-11); Ghorla | Tibbe **suspeita-se** de ter favorecido o gado; **ignora** qual lado tem razão | ninguém decide quem tem razão sobre o capim |

0 caso em que o **confirmado** de uma ficha contradiz o **desconhecido** de outra; 0 NPC que sabe uma resposta de mistério.

## 4. Contradições intencionais e pontos abertos

| ID | Contradição ou lacuna | Onde está | Por que fica |
|---|---|---|---|
| CI-01 | duas versões da lenda de cada um dos 8 lugares | doc 01 §2 e parte 3 §3 | cada lugar conta a sua |
| CI-02 | qual poder é mais velho, a Assembleia ou a Marcha | doc 01 parte 3 §1 e §4 | motor da sucessão; `desconhecido` |
| CI-03 | o mandato de Kharos acaba e o limite de estações não é dito; como foi escolhido é `desconhecido` | doc 04 §5; doc 01 parte 2 | gancho central; D-63 |
| CI-04 | Trato do Vau e Trato da Encosta sem chefe aceito (`lideranca_id` `desconhecido`) | doc 04 §8; `12-assentamentos.csv` | o Trato é o que está em disputa |
| CI-05 | crime grave, exílio e pena de morte não fixados | doc 04 §6; doc 05 §1 | aberta de propósito (D-109, opção C) |
| CI-06 | gnomos e construtos só visitantes em Praia-Pardo × "—" na matriz de M-05 | doc 01 §2.3; doc 05 §3 | **resolvido** (D-103); não é contradição |
| CI-07 | 7 acúmulos de cargo (§2.2) | docs 01, 04 | intencional proposto |
| CI-08 | 2 rivais `desconhecido` (Guardas da Marcha, Vigias da Cisterna) | doc 04 §9 | cada um com motivo registrado |
| CI-09 | Clãs da Prova e Clãs da Aclamação têm líder em Alvasal e Encosta-Aberta e presença no Terreiro-Maior | doc 04 §9 e §9.2 | blocos de clãs que agem na Assembleia, não no lugar do líder |
| CI-10 | origem e sentido de "Palancar" `desconhecidos`, com duas versões | doc 01 parte 3 §2 | nome de reino, não de lugar |
| CI-11 | origem do metal da forja da Vazante; o que o Vale oferece à Descida e à Rota do Sal | doc 05 §2 | **ponto aberto** (D-109, opção B), não contradição |
| CI-12 | Lessa Cervo-Manso (Aeltharyn) sem ID no registro de lá | contrato P-4 | **ponto aberto**, herdado, não é do reino 4 |

### 4.1 Inconsistências acidentais encontradas e tratadas
- Moura (chefe do Trato do Celeiro e líder dos Guardiões da Reserva) e Aruun (chefe do Trato do Poço e líder dos Vigias da Cisterna) acumulavam cargo sem constar na lista de acúmulos declarados do doc 04 §9: **declarados** no doc 04 (de 5 para 7).
- R4-2.2: doc 04 e D-108 diziam 3 rivais `desconhecido`; são 2 (já corrigido).
- Nenhuma outra: 0 ID duplicado, 0 citado sem registro, 0 número, tempo ou rota novo.

## 5. Conferências da Fase 2

| Item | Resultado |
|---|---|
| IDs únicos do registro | 47 (8 lugares, 14 facções, 25 pessoas), 0 duplicado |
| IDs citados nos docs sem registro | 0 |
| campos vazios em `12-facoes.csv` e `12-assentamentos.csv` | 0 |
| IDs de raça, sub-raça, classe e profissão fora do catálogo | 0 |
| colisão de ID com os reinos 1, 2 e 3 | 0 |
| arquivos de Aeltharyn, das Coroas e do Véu alterados nesta fase | 0 |

## 6. Decisão pendente

**D-115:** aprovar o relatório, os 7 acúmulos de cargo como intencionais (CI-07) e as contradições e pontos abertos CI-01 a CI-12. Opções: **A.** aprovar; **B.** desfazer algum acúmulo ou fechar uma lacuna (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**
