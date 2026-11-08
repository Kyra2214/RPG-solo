# 12 — Relógios: gatilhos, trava, custo e inação (R2-5.1)

**Estado:** rascunho de R2-5.1 para aprovação do autor · **Data:** 2026-11-04
**Etiqueta:** **[PROPOSTO]**; **não cria relógio, NPC, cidade ou fato novo**: reúne os 7 relógios CL-01 a CL-07 de `06-modelo-operacional-e-aventuras.md` (D-37) com gatilhos observáveis, custo por segmento e efeito da inação. Dados em `12-relogios.csv` (12 colunas, no molde do reino 1 mais `gatilhos_observaveis` e `decisao_no_segmento_5`).
**ASSUMINDO:** "o que trava", "quem paga" e "efeito da inação" seguem como leitura editorial do doc 06; os **gatilhos** são só o que o "efeito da inação" já exige, reescrito como lista; a **decisão no segmento 5** é o governo local ou o Concílio já documentados no doc 04 §9 e nas fichas.

## 1. Regras comuns

- Nenhum relógio avança por calendário. A estação só cria condição (R2-3.4).
- Todo avanço exige **causa observável** (um gatilho da tabela), **nexo** com o relógio e **localidade** afetada. Máximo de **1 segmento por causa**; uma cena não avança todos os relógios.
- **Inação:** o mundo não pune o jogador automaticamente. Ignorar uma oportunidade só avança um relógio se o gatilho já existe na cena. Sem gatilho, nada muda.
- Um avanço muda a **escala** da situação, não escolhe a consequência. Nenhum relógio envolve Aeltharyn, o Mistério D ou a mina selada.

## 2. Custo por segmento (vale para os 7)

| Segmento | Escala | Custo para quem age | O que o mundo mostra |
|---|---|---|---|
| 1–2 | sinais locais | baixo: tempo e atenção | pistas, rumores, relatos isolados |
| 3–4 | pressão sobre uma cadeia | médio: favor, patrulha, material | a cadeia de dependência (doc 05 §2.2) fica visível; NPCs mudam de agenda |
| 5 | decisão pública exigida | alto: custo político ou material real em qualquer escolha | o órgão da coluna "decisão pública no segmento 5" precisa decidir; nenhuma saída é grátis |
| 6 | efeito ao completar | consequência irreversível dentro do escopo escrito | muda estado, não resolve a trama |

## 3. Os sete relógios

### `clock.combustivel` — CL-01 Combustível de Brasalume (3/6)
| Campo | Texto |
|---|---|
| O que move | fornalhas gastando mais do que a cidade consegue repor; disputa pelo turno do fogo principal (L-03) |
| Gatilhos observáveis de avanço | nova disputa pelo turno do fogo principal; nova queda de fornecimento observada |
| O que trava (ASSUMINDO) | turno acordado entre as famílias; combustível registrado pelo Concílio; redução voluntária do consumo |
| Quem paga (ASSUMINDO) | famílias de forja pequenas; Barcaalta (peças e carga dependem do metal); a câmara de arbitragem |
| Decisão pública no segmento 5 | Conselho das Famílias de Fogo (Brasalume) |
| Ao completar (segmento 6) | o fogo principal passa a funcionar por turnos reduzidos; **origem do combustível continua desconhecida** |
| Efeito da inação (ASSUMINDO) | só avança se houver nova disputa pelo turno ou nova queda de fornecimento observada |
| Entidades | `city.brasalume`, `npc.gundrik-muralha-lenta`, `npc.karna-brasa-mansa`, `faction.casa-das-fornalhas` |

### `clock.comportas` — CL-02 Comportas e degelo de Veiaclara (2/6)
| Campo | Texto |
|---|---|
| O que move | degelo fora de data; cidades a jusante pedindo água sem pagar manutenção; registro alterado (MP-01) |
| Gatilhos observáveis de avanço | prova nova de registro alterado; degelo fora de data observado |
| O que trava (ASSUMINDO) | registro auditado; manutenção acordada; prova de quem alterou o registro |
| Quem paga (ASSUMINDO) | cidades a jusante; Veiaclara; a Casa das Comportas |
| Decisão pública no segmento 5 | Conselho da Nascente (Veiaclara) |
| Ao completar (segmento 6) | uma comporta cede e a água sobe ou falta em cidades a jusante (**só dentro das Coroas**) |
| Efeito da inação (ASSUMINDO) | só avança com prova nova de registro alterado ou com degelo fora de data observado |
| Entidades | `city.veiaclara`, `npc.pelma-gota-medida`, `npc.pimm-veio-pequeno`, `faction.casa-das-comportas` |

### `clock.cais` — CL-03 Cais saturado e registros em conflito (3/6)
| Campo | Texto |
|---|---|
| O que move | mais barcas do que o cais comporta; carga e taxa não batem; regras sobre construtos de carga |
| Gatilhos observáveis de avanço | nova barca sem registro; novo conflito de taxas |
| O que trava (ASSUMINDO) | conferência dos dois registros (L-09); limite de barcas por jornada; regra provisória de construtos |
| Quem paga (ASSUMINDO) | famílias de cais; Mesa do Cais; Barcaalta e as cidades que dependem da Descida |
| Decisão pública no segmento 5 | Mesa do Cais (Barcaalta) |
| Ao completar (segmento 6) | o cais fecha por conferência até a Mesa resolver; mercadorias ficam retidas (**a Descida não é descrita rio abaixo**) |
| Efeito da inação (ASSUMINDO) | só avança com nova barca sem registro ou novo conflito de taxas |
| Entidades | `city.barcaalta`, `npc.torgrim-corda-dupla`, `npc.seis-eixos`, `faction.companhia-da-descida` |

### `clock.passagens` — CL-04 Livro de Passagens e viajantes sem registro (2/6)
| Campo | Texto |
|---|---|
| O que move | mais viajantes sem registro; páginas arrancadas (MP-02); tarifas em disputa |
| Gatilhos observáveis de avanço | nova página arrancada do Livro; novo caso de delito de custódia |
| O que trava (ASSUMINDO) | cópia do Livro nas casas de ofício; tarifa comum provisória; vigias reforçados |
| Quem paga (ASSUMINDO) | Mesa do Portão; viajantes e caravanas; Pedra-Ata (hospedagem) |
| Decisão pública no segmento 5 | Mesa do Portão (Portão-Cinza) |
| Ao completar (segmento 6) | o Portão passa a exigir registro duplo a quem sobe e a quem desce, por decisão da Mesa |
| Efeito da inação (ASSUMINDO) | só avança com nova página arrancada ou novo caso de delito de custódia observado |
| Entidades | `city.portao-cinza`, `npc.thaurak-escama-fria`, `faction.vigias-do-limiar` |

### `clock.assento` — CL-05 Fim do ciclo do assento (2/6)
| Campo | Texto |
|---|---|
| O que move | o ciclo de Velka Selo-Firme termina sem sucessor combinado; cada cidade quer o assento por primeiro; cópias da ata em disputa (MP-04) |
| Gatilhos observáveis de avanço | fim do ciclo cumprido na cena sem acordo anunciado |
| O que trava (ASSUMINDO) | acordo de ordem entre pelo menos 7 cidades; arbitragem das cópias (L-08); proposta de sucessão aceita |
| Quem paga (ASSUMINDO) | Pedra-Ata; cidades pequenas; Arquivo das Coroas |
| Decisão pública no segmento 5 | Concílio das Coroas (Pedra-Ata) |
| Ao completar (segmento 6) | o Concílio fica sem presidente de guarda por um ciclo e as matérias reservadas param |
| Efeito da inação (ASSUMINDO) | avança só quando o fim do ciclo é cumprido na cena sem acordo anunciado |
| Entidades | `city.pedra-ata`, `npc.velka-selo-firme`, `npc.maro-tinta-velha`, `faction.arquivistas-das-coroas` |

### `clock.pecas` — CL-06 Peças raras e foro dos Despertos (2/6)
| Campo | Texto |
|---|---|
| O que move | peças raras somem e cada oficina culpa outra; construtos antigos pedem direitos que a cidade não definiu (MP-03) |
| Gatilhos observáveis de avanço | nova peça sumida; novo pedido formal de construto |
| O que trava (ASSUMINDO) | registro de peças na Casa das Peças; foro provisório acordado pelas oficinas; prova de onde as peças foram |
| Quem paga (ASSUMINDO) | oficinas menores; Barcaalta; construtos sem dono |
| Decisão pública no segmento 5 | Assembleia das Oficinas (Rodaviva) |
| Ao completar (segmento 6) | uma oficina fecha por falta de peça ou a Assembleia vota um foro provisório; **não decide a origem dos Despertos** |
| Efeito da inação (ASSUMINDO) | só avança com nova peça sumida ou novo pedido formal de construto |
| Entidades | `city.rodaviva`, `npc.fenwick-mola-solta`, `npc.primeira-peca`, `npc.quinze-pinos`, `faction.oficinas-reunidas` |

### `clock.passos` — CL-07 Nevasca e passos fechados (1/6)
| Campo | Texto |
|---|---|
| O que move | nevasca que fecha os passos; mensageiros que recusam voar quando o vento muda |
| Gatilhos observáveis de avanço | nova nevasca observada; a estação sozinha não é gatilho |
| O que trava (ASSUMINDO) | mensageiros avisados com antecedência; depósitos de emergência; passos reabertos |
| Quem paga (ASSUMINDO) | Altavento; cidades que dependem da Rede dos Passos Altos; Portão-Cinza |
| Decisão pública no segmento 5 | Assembleia dos Picos (Altavento) |
| Ao completar (segmento 6) | o reino fica dividido em blocos por um ciclo; avisos entre cidades atrasam |
| Efeito da inação (ASSUMINDO) | só avança com nova nevasca observada; estação sozinha não avança |
| Entidades | `city.altavento`, `npc.iskra-pena-longa`, `npc.vireo-vento-rapido`, `faction.rede-dos-passos-altos` |

**Conferência:** 7 relógios, progresso inicial 1/6 a 3/6, todos com "o que move" e "o que trava", gatilho, decisão no segmento 5 e efeito da inação; todas as entidades existem no registro mestre; 0 relógio liga as Coroas a Aeltharyn.
