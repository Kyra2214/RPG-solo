# Véu dos Malditos — registro operacional de recursos (doc 08)

**Estado:** rascunho de R3-3.3 para aprovação do autor (D-81) · **Data:** 2026-11-05
**Etiqueta:** **[PROPOSTO]**; só organiza a evidência dos docs 01, 03, 04, 05 e 07 (D-68 a D-80) e de M-04 e M-05. Nada vira cânone sem aprovação. Matriz dos oito lugares: `09-matriz-material-dos-lugares.md`.
**Regras seguidas:** escala **qualitativa** (D-63): "rica em", "pobre em", "depende de"; **nenhum número, nível, estoque, preço, unidade ou tempo**; desconhecido nunca vira ausência; nenhuma entidade, rota ou fluxo novos; nada afirma nem nega fornecimento a Aeltharyn ou às Coroas além do que M-05 §5 já diz; nada sobre o Mistério D, o Mistério E ou a Neblina Longa.
**ASSUMINDO:** o reino 2 usou uma escala de 0 a 5; aqui **não se repete** (o roadmap do reino 3 pede termos qualitativos e o doc 05 §2 já os usa). Se o autor quiser a escala numérica, é a decisão **B** de D-81.

## 1. Objetivo e limites

O registro diz o que cada lugar tem em abundância, onde é frágil e de quem depende, **sem fabricar precisão**. Uma oficina forte não prova peça em estoque; um posto não prova água sobrando; um tributo não prova entrega suficiente. Os termos servem para escolher cenas e consequências, não para simular a economia.

## 2. Estados de evidência e leitura dos termos

- **Documentado:** afirmado por doc do reino 3 (cite o doc). **ASSUMINDO:** inferido do texto sem afirmação direta (por exemplo o tributo em pactos, I-07). **Desconhecido:** sem base; **não é zero**.
- **Rica em:** o doc 05 §2 ("rico em ou vive de") ou o campo Economia do doc 01 coloca isso como ofício ou ganho do lugar. Não diz quantidade.
- **Pobre em:** **só** onde o doc 01 registra falta ou vulnerabilidade (campos Defesas e vulnerabilidades e Problemas atuais). Onde não há registro, a célula traz `—`: ausência de registro **não** é riqueza nem pobreza.
- **Depende de:** o doc 05 §2 (coluna Depende de) e o doc 04 §7 dizem de quem ou do quê o lugar depende.

## 3. Por lugar

| Lugar | Rica em | Pobre em | Depende de | Estado | Fonte |
|---|---|---|---|---|---|
| `city.morroveu` | tributo em pactos; escribas e registro; armas e armaduras para a Provação; hospedagem de desafiantes | — | dos pactos dos outros lugares | Documentado (tributo: ASSUMINDO, I-07) | doc 01 V-01; doc 05 §2; doc 04 §7 |
| `city.lamaluz` | juncos; peixe de lama; pilotagem; lamparinas | — | de canais que mudam com a cheia; da seca e da cheia na época certa | Documentado | doc 01 V-02; doc 05 §2 |
| `city.fozbreu` | breu; sal; pesca de barra; cabotagem | frota de guerra própria (não tem) | de quem sabe entrar pela barra | Documentado | doc 01 V-03; doc 05 §2 |
| `city.beira-sal` | sal; água; guias; acertos | poços (depende de poucos) | de poucos poços | Documentado | doc 01 V-04; doc 05 §2 |
| `city.selovale` | arquivo de pactos; juramentos; cópia de selos; parecer | — | de quem guarda os selos | Documentado | doc 01 V-05; doc 05 §2 |
| `city.borda-mansa` | vigia; fogueiras de sinal; couro de charco; guarda de quem chega | alcance da vigia (só vê o que a névoa deixa) | da névoa deixar ver | Documentado | doc 01 V-06; doc 05 §2 |
| `city.fio-longo` | hospedagem de repouso; tecelagem; interpretação de sonhos; remédios de sono | defesa de quem dorme (não pode se defender; protegido só por pacto) | dos hóspedes e do pacto do repouso | Documentado | doc 01 V-07; doc 05 §2 |
| `city.candeia-morta` | ofícios de noite; luz fria; guarda de portões; cuidado dos mortos e das despedidas | defesa de dia (cidade frágil) | dos turnos de vigia | Documentado | doc 01 V-08; doc 05 §2 |

## 4. Dependências entre lugares

| ID | Quem depende | De quem ou do quê | O quê | Estado | Crítica? | Fonte |
|---|---|---|---|---|---|---|
| DR-01 | `city.morroveu` | os sete outros lugares | tributo em pactos: cada lugar entrega o que o pacto produz | Documentada (ASSUMINDO, I-07) | **sim** | doc 05 §2 (Morrovéu depende dos pactos dos outros); doc 04 §7 |
| DR-02 | os sete outros lugares | `city.morroveu` e o Conselho dos Pactuários | assento por lugar; os pactos sobrevivem a cada rei | Documentada | **sim** (política) | doc 03 §3; doc 04 §2.1 |
| DR-03 | `city.fozbreu` | o rio (Descida do Argente) | chegada de carga e visitantes de passagem | Documentada | não (pressão, não sustento) | doc 05 §3; doc 01 V-03 |
| DR-04 | `city.beira-sal` | a Rota do Sal (Ermo de Sal) | caravanas, travessias e acertos; é lugar de passagem | Documentada | **sim** | doc 01 V-04; doc 03 §3; M-05 §5 |
| DR-05 | `city.lamaluz` e `city.fozbreu` | pilotos dos canais e da barra | pilotagem em águas incertas | Documentada | **sim** (em cada um) | doc 04 §9.2 (Pilotos dos Canais nas duas) |
| DR-06 | `city.morroveu` | `city.selovale` | pactos antigos que fundamentam a regra da coroa; o que a coroa deve ao arquivo é `desconhecido` | Documentada (valor desconhecido) | **sim** (política) | doc 03 §3 |
| DR-07 | `city.fio-longo` | hóspedes de fora do lugar | quem repousa na Casa; origem dos hóspedes `desconhecida` | Documentada (origem desconhecida) | **sim** | doc 05 §2; doc 01 V-07 |
| DR-08 | `city.candeia-morta` | os turnos de vigia | luz, guarda de portões e cuidado dos mortos dependem de turnos que nunca fecham | Documentada | **sim** | doc 01 V-08; doc 05 §2 |
| DR-09 | `city.borda-mansa` | a névoa | o alcance da vigia depende da névoa deixar ver | Documentada | não (condição) | doc 01 V-06 |

**Nenhum lugar funciona isolado:** cada um dos oito tem pelo menos uma dependência crítica documentada (Morrovéu: DR-01, DR-02 e DR-06; Lamaluz e Fozbreu: DR-02 e DR-05; Beira-Sal: DR-02 e DR-04; Selovale: DR-02 e DR-06; Fio-Longo: DR-02 e DR-07; Candeia-Morta: DR-02 e DR-08; Borda-Mansa: DR-02, além da condição DR-09). **Cada dependência crítica tem fonte** na última coluna.

## 5. O que a Descida e a Rota do Sal trazem (só qualitativo)

| Fluxo | Quem passa | O que traz ou leva | O que não se sabe | Fonte |
|---|---|---|---|---|
| Descida do Argente (chega a Fozbreu) | anões, kobolds, gnomos e construtos, só como visitantes de passagem | o que as Coroas dizem que sai de lá (metal e ferramentas); o porto não confere o conteúdo | o que fica ou segue; o que o Véu manda de volta; quando começou aqui; o que a Descida faz rio acima | doc 05 §3; M-05 §5 |
| Rota do Sal (Ermo de Sal, com Beira-Sal como posto) | mercadores, goblins e ratoides, em caravana e com guias | sal e a travessia guiada | o que passa nos dois sentidos; nada é afirmado sobre as Marcas das Feras | doc 01 V-04; M-05 §5; M-04 G-10 |
| Tributo em pactos (interno) | Pactuários e a Corte | o que cada pacto produz, sem quantia | se a entrega basta; o que acontece se um lugar falha | doc 04 §7 (ASSUMINDO) |

Nenhum fluxo liga o Véu a Aeltharyn; a Descida não chega a Aeltharyn (M-04 G-06).

## 6. Regras de uso

- Não converter "rica em" em quantidade, nem "depende de" em prazo.
- Dependência crítica falha **por causa observável** (relógios do doc 06), nunca por calendário.
- `—` em "Pobre em" significa **sem registro**, não "sem falta".
- Nenhum desses registros decide as lacunas das leis (L-04, L-05 e demais).

## 7. Conferências

- 8 lugares com rica em, pobre em, depende de e fonte; 9 dependências (7 críticas); 3 fluxos qualitativos.
- 0 número, nível, unidade, preço ou tempo; 0 entidade, rota ou fluxo novos; 0 vínculo com Aeltharyn ou com as Coroas além de M-05 §5.
- Termos de fronteira: só "Aeltharyn" em "nenhum fluxo liga" e a menção a G-06 (o Argente não toca Aeltharyn).

## 8. Decisão pendente

**D-81:** os termos qualitativos, as 9 dependências (7 críticas) e os 3 fluxos dos docs 08 e 09. Opções: **A.** aprovar (recomendada); **B.** o autor prefere a escala numérica de 0 a 5 do reino 2 (e quem atribui os níveis); **C.** trocar uma dependência ou termo (diga qual). **Recomendação: A.**
