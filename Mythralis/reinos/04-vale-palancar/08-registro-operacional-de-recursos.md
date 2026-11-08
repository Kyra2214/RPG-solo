# Vale Palancar — registro operacional de recursos (doc 08)

**Estado:** R4-3.3, D-118 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; só organiza a evidência dos docs 01, 03, 04, 05 e 07 (D-104 a D-117) e de M-04 e M-05. Nada vira cânone sem aprovação. Matriz dos oito lugares: `09-matriz-material-dos-lugares.md`.
**Regras seguidas:** escala **qualitativa** (D-63): "rica em", "pobre em", "depende de"; **nenhum número, nível, estoque, preço, unidade ou tempo**; desconhecido nunca vira ausência; nenhuma entidade, rota ou fluxo novos; nada afirma nem nega fornecimento a Aeltharyn, às Coroas ou ao Véu além do que M-05 §5 já diz; **nada sobre a origem do combustível ou do alimento de Brasalume** (contrato C5); nada sobre o Mistério D, o Mistério E, a Neblina Longa ou o Conflito G.
**ASSUMINDO:** os reinos 2 e 3 não pedem escala numérica aqui; o reino 4 **não a usa**. Se o autor quiser a escala de 0 a 5 do reino 2, é a decisão **B** de D-118.

## 1. Objetivo e limites

O registro diz o que cada lugar tem em abundância, onde é frágil e de quem depende, **sem fabricar precisão**. Uma reserva guardada não prova celeiro cheio; um poço não prova água sobrando; um Trato não prova entrega suficiente. Os termos servem para escolher cenas e consequências, não para simular a economia.

## 2. Estados de evidência e leitura dos termos

- **Documentado:** afirmado por doc do reino 4 (cite o doc). **ASSUMINDO:** inferido do texto sem afirmação direta (por exemplo o vínculo de lugar-mãe como apoio). **Desconhecido:** sem base; **não é zero**.
- **Rica em:** o doc 05 §2 ("rico em ou vive de") ou o campo Economia do doc 01 coloca isso como ofício ou ganho do lugar. Não diz quantidade.
- **Pobre em:** **só** onde o doc 01 registra falta ou vulnerabilidade (campos Defesas e vulnerabilidades e Problemas atuais). Onde não há registro, a célula traz `—`: ausência de registro **não** é riqueza nem pobreza.
- **Depende de:** o doc 05 §2 (coluna Depende de) e o doc 03 §1 (lugar-mãe e obrigação).
- **Sazonais:** o que um lugar sazonal "tem" vale para o campo e a estação em que estiver (D-117, DM-03).

## 3. Por lugar

| Lugar | Rica em | Pobre em | Depende de | Estado | Fonte |
|---|---|---|---|---|---|
| `city.tabuleiro-velho` | grão e carne seca guardados; couro e lã; guarda dos marcos dos pastos | guarda quando os clãs partem em marcha; água (uma só cisterna alta) | de os clãs entregarem o que juntam; do ano de pasto; da cisterna alta | Documentado | doc 01 §2.1; doc 05 §2 |
| `city.rasovau` | troca de gado, couro e sal entre clãs; recados e acertos de passagem | defesa (sem muro nem guarnição) | do vau seguir transitável; de quem atravessa respeitar o Trato | Documentado | doc 01 §2.2; doc 05 §2 |
| `city.praia-pardo` | recepção, guarda e acerto da carga que chega pelo rio; pouso | defesa própria (pouca) | do que chega pelo rio, que o pouso não controla; dos clãs vizinhos para a proteção | Documentado | doc 01 §2.3; doc 05 §2 |
| `city.alvasal` | sal; água; carga; acerto de quem se prepara para o Ermo | água para todas as caravanas ao mesmo tempo (uma só cisterna) | da cisterna | Documentado | doc 01 §2.4; doc 05 §2 |
| `city.terreiro-maior` | hospedagem de clãs; fogo e forragem; mediação de pactos; contas entre clãs | defesa própria (nenhuma); fica frágil se o Capitão estiver longe | de a Assembleia escolher campo ao alcance dos clãs; dos hospedeiros da Ramada; da reserva do Tabuleiro-Velho (lugar-mãe) | Documentado (lugar-mãe como apoio: ASSUMINDO) | doc 01 §2.5; doc 03 §1; doc 05 §2 |
| `city.vazante` | a própria marcha: carroças, rebanhos, forja de campo, caça | pasto para os dois campos possíveis (cada escolha deixa um clã sem pasto) | de pasto e caça; da reserva do Tabuleiro-Velho (lugar-mãe) | Documentado (lugar-mãe como apoio: ASSUMINDO) | doc 01 §2.6; doc 03 §1; doc 05 §2 |
| `city.encosta-aberta` | pouso; pastagem de espera; guia de quem sobe | visão do que está do outro lado das elevações | dos clãs vigias; de Rasovau (lugar-mãe) | Documentado (lugar-mãe como apoio: ASSUMINDO) | doc 01 §2.7; doc 03 §1; doc 05 §2 |
| `city.gramal` | gado; couro; caça; marca dos rebanhos e divisão do pasto | defesa fixa (nenhuma); pasto em ano fraco | de pasto bom na estação; de a disputa entre gado e caça não virar guerra; da reserva do Tabuleiro-Velho (lugar-mãe) | Documentado (lugar-mãe como apoio: ASSUMINDO) | doc 01 §2.8; doc 03 §1; doc 05 §2 |

## 4. Dependências entre lugares

| ID | Quem depende | De quem ou do quê | O quê | Estado | Crítica? | Fonte |
|---|---|---|---|---|---|---|
| DR-01 | `city.vazante` | `city.tabuleiro-velho` | reserva que sustenta a marcha | Documentada (ASSUMINDO, lugar-mãe) | **sim** | doc 03 §1 e §4; doc 05 §2 |
| DR-02 | `city.terreiro-maior` | `city.tabuleiro-velho` | reserva e marcos que sustentam as reuniões | Documentada (ASSUMINDO, lugar-mãe) | **sim** (política) | doc 03 §1 e §4 |
| DR-03 | `city.gramal` | `city.tabuleiro-velho` | marcos dos pastos que sustentam a divisão do pasto | Documentada (ASSUMINDO, lugar-mãe) | **sim** | doc 03 §1 e §4 |
| DR-04 | `city.encosta-aberta` | `city.rasovau` | apoio do lugar-mãe à vigia e ao pouso; o que Rasovau entrega é `desconhecido` | Documentada (ASSUMINDO, lugar-mãe; valor desconhecido) | **sim** (política) | doc 03 §1 |
| DR-05 | `city.praia-pardo` | clãs vizinhos | proteção, por pouca defesa própria | Documentada | **sim** | doc 01 §2.3; doc 05 §2 |
| DR-06 | `city.praia-pardo` | o rio (Descida do Argente) | chegada de carga e visitantes de passagem | Documentada | não (pressão, não sustento) | doc 05 §3; doc 01 §2.3 |
| DR-07 | `city.rasovau` | quem atravessa | respeito ao Trato e o vau seguir transitável | Documentada | **sim** | doc 05 §2; doc 01 §2.2 |
| DR-08 | `city.alvasal` | a cisterna e as caravanas | água, carga e acerto; a cisterna não serve a todas ao mesmo tempo | Documentada | **sim** | doc 01 §2.4; doc 05 §4 |
| DR-09 | `city.tabuleiro-velho` | os clãs que entregam a reserva | o que juntam, o ano de pasto e a cisterna alta | Documentada | **sim** | doc 05 §2; doc 01 §2.1 |
| DR-10 | `city.vazante` | pasto e caça | sustento da marcha | Documentada | não (condição) | doc 05 §2 |
| DR-11 | `city.gramal` | pasto bom e paz entre gado e caça | sustento do território | Documentada | não (condição) | doc 05 §2; doc 01 §2.8 |
| DR-12 | `city.terreiro-maior` | a Assembleia escolher campo e os hospedeiros da Ramada | hospedagem e forragem | Documentada | não (condição) | doc 05 §2; doc 01 §2.5 |

**Nenhum lugar funciona isolado:** cada um dos oito tem pelo menos uma dependência crítica documentada (Tabuleiro-Velho: DR-09; Rasovau: DR-07; Praia-Pardo: DR-05; Alvasal: DR-08; Terreiro-Maior: DR-02; Vazante: DR-01; Encosta-Aberta: DR-04; Gramal: DR-03). **Cada dependência crítica tem fonte** na última coluna. Quatro delas (DR-01 a DR-04) dependem do vínculo de lugar-mãe, ASSUMINDO (doc 03).

## 5. O que a Descida, a Rota do Sal e os Corredores Largos trazem (só qualitativo)

| Fluxo | Quem passa | O que traz ou leva | O que não se sabe | Fonte |
|---|---|---|---|---|
| Descida do Argente (chega a Praia-Pardo) | anões e kobolds; gnomos e construtos só como visitantes de passagem | carga com papel que as Coroas reconhecem; o pouso não confere o conteúdo; o que chega passa a ser trocado em Rasovau | o que fica e o que segue; o que o Vale manda de volta; quando começou aqui; o que a Descida faz rio acima; **origem do combustível ou do alimento de Brasalume** | doc 05 §3; M-05 §5; contrato C3 e C5 |
| Rota do Sal (parte de Alvasal) | mercadores, goblins e ratoides, em caravana e com guias | água, carga, acerto e pouso antes da partida; sal de Alvasal | o que as caravanas levam e trazem; o que acontece além do Marco do Ermo; nada afirmado sobre o Véu | doc 05 §4; M-05 §5; M-04 G-10 |
| Corredores Largos (saem por Encosta-Aberta) | centauros, felídeos, cervinos e lupinos, como visitantes individuais | guia, pastagem de espera e pouso; **sem comércio com Aeltharyn descrito** | o que acontece do outro lado; qualquer troca com Aeltharyn | doc 05 §5; doc 01 §2.7; M-05 §5 |
| Troca entre clãs e reserva (interno) | clãs, Tratos | gado, couro, sal, grão, carne seca e lã por troca e por pactos de passagem e de pasto, sem quantia | se a reserva basta; o que acontece se um lugar falha; de onde vem o metal da forja da Vazante (`desconhecido`) | doc 04 §4; doc 05 §2 |

Nenhum fluxo liga o Vale a Aeltharyn além do pouso da Encosta; a Descida não vai além de Praia-Pardo e Rasovau deste lado. **O que o Vale oferece à Descida e à Rota do Sal segue em aberto** (contrato §6; D-109, opção B).

## 6. Regras de uso

- Não converter "rica em" em quantidade, nem "depende de" em prazo.
- Dependência crítica falha **por causa observável** (relógios do doc 06), nunca por calendário.
- `—` em "Pobre em" significa **sem registro**, não "sem falta".
- Nenhum desses registros decide as lacunas das leis (doc 05 §1) nem o Conflito G.

## 7. Conferências

- 8 lugares com rica em, pobre em, depende de e fonte; 12 dependências (8 críticas); 4 fluxos qualitativos.
- 0 número, nível, unidade, preço ou tempo; 0 entidade, rota ou fluxo novos; 0 origem de combustível ou alimento de Brasalume; 0 vínculo com Aeltharyn, Coroas ou Véu além de M-05 §5.
- Termos de fronteira: só em "Fluxos" e nas restrições, como "sem comércio descrito" e "nada afirmado".

## 8. Decisão pendente

**D-118:** os termos qualitativos, as 12 dependências (8 críticas) e os 4 fluxos dos docs 08 e 09. Opções: **A.** aprovar (recomendada); **B.** o autor prefere a escala numérica de 0 a 5 do reino 2 (e quem atribui os níveis); **C.** trocar uma dependência ou termo (diga qual). **Recomendação: A.**
