# Azeroth — Fichas completas dos doze principais (doc 12, R5-4.1)

**Estado:** R5-4.1, D-157 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, nome, lugar, facção ou fato novo**: reúne e completa as fichas N-01 a N-12 (doc 01 parte 2, D-144) no campo a campo de `MODELO_DE_REINO.md` §4, com raça, sub-raça, classe e profissão de `ALINHAMENTO_AZEROTH.md` (D-151), facções e aliados de `12-facoes.csv` e doc 04, relógios de `06`. Dados em `12-fichas.csv` (16 colunas).
**Estados:** C confirmado · S suspeita · D desconhecido · ASSUMINDO = leitura editorial sem fonte direta (traços, medos e condições de evolução). Nada se infere além dos docs 01, 04 e 06. Escala qualitativa (D-63); nenhum mestiço (D-64); nenhum povo é bom, mau ou leal por natureza (W-01).
**Campos feitos depois da ficha:** competências detalhadas (R5-4.2), matriz de relações (R5-4.3), voz (R5-4.4) e camadas de informação (R5-6.2, coluna `camadas`). Onde a ficha precisa deles, traz só o que já está documentado e `desconhecida`.

## 1. Conferências

| Verificação | Resultado |
|---|---|
| Principais com ficha completa | **12 de 12** (16 campos, 0 vazio) |
| Agenda e pressão distintas | **12 agendas e 12 pressões diferentes** |
| Governantes e herdeiros | 3 governantes (Odile, Corlan, Brevo) e 3 herdeiros (Tomlin, Rilla, Astrel), sem facção declarada |
| Heróis vivos / generais | 2 (Nerella, Garvane) / 2 (Solenne, Dorgan) |
| Povo, sub-raça, classe e profissão | conferidos em `ALINHAMENTO_AZEROTH.md` (sub-raça `—` onde o catálogo não atribui; profissão `—` onde não há ofício escrito) |
| Origem | desconhecida em todos (nenhuma documentada) |
| Estado | vivo em todos |
| Acúmulos de cargo (CI-07) | **4 de 12 marcados** como "acúmulo intencional proposto" (Solenne, Dorgan, Nerella, Auriven), os 4 do doc 04 §10 |
| Sem acúmulo | Odile, Corlan, Brevo, Tomlin, Rilla, Astrel, Garvane e Tolla |
| Lugares com principal | 7 de 10 (Campoalvo 2, Alvamar 3, Torreclara 2, Meiovau 2; Vaudouro, Torrefronte e Enseada-Vigia 1); Concharia, Estrelado e Pouso-das-Brisas têm só tríade fora dos 12 (D-22 não exige) |
| Relógio ligado a cada principal | 12 de 12 (CA-01 a CA-08 e CA-10 do doc 06; progresso 2/6 e 3/6 como em `06`) |

## 2. Fichas por lugar

### Campoalvo — `city.campoalvo`

#### `npc.odile-medida-cheia` — Odile Medida-Cheia (N-01)

| Campo | Ficha |
|---|---|
| Idade | idosa (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | Humano (humano); sub-raça: humano-das-cidades |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | governante da Coroa do Trigal; despacha no Salão das Medidas |
| Cidade e facção | city.campoalvo; facção: nenhuma declarada (governante da Coroa do Trigal) |
| Personalidade, valores, medos, limites | zelosa e orgulhosa; valores: o pão do reino e a medida certa; medo: que o excedente apodreça e a precedência passe ao Cais. Traços e medos: ASSUMINDO (leitura da agenda, da pressão e do desconhecido do doc 01) |
| Capacidades, influência, restrições | governante da Coroa do Trigal; restrição: Governa o Trigal e fixa as medidas; não manda no Cais, no Vento nem em Meiovau; catálogo: classe estrategista, profissão contador (R5-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | herdeiro: Tomlin Horta-Larga (C, doc 04 §2.2); mesma cidade de Tomlin Horta-Larga (C); demais relações: desconhecidas (R5-4.3) |
| Biografia e marcos | C: Governa a Coroa do Trigal e reside em Campoalvo; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Governa a Coroa do Trigal e reside em Campoalvo. S: Guarda em segredo o tamanho real dos celeiros. D: Como foi reconhecida e o que faria se a precedência fosse perdida. |
| Estado atual | vivo |
| Agenda atual | Manter o preço do pão e a precedência do Trigal sem fechar os celeiros a quem tem fome. |
| Pressão | O Cais diz que o excedente apodrece sem o mar; o Vento diz que ela só sabe da colheita depois do aviso; o herdeiro já fala em ceder. |
| Condição de evolução | ASSUMINDO: se `clock.medida-em-ano-ruim` (2/6) avança, escolhe entre dizer em voz alta quem ajusta a medida em ano ruim e abrir parte dos celeiros ou manter calado o tamanho real dos celeiros; nada se resolve por calendário |
| Camadas de informação | `fact.odile-governa-a-coroa-do`, `fact.odile-guarda-em-segredo-o-tamanho` (`14-camadas-de-informacao.csv`) |

#### `npc.tomlin-horta-larga` — Tomlin Horta-Larga (N-04)

| Campo | Ficha |
|---|---|
| Idade | jovem (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | Pequenino (pequenino); sub-raça: pequenino-viajante |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | herdeiro reconhecido da Coroa do Trigal |
| Cidade e facção | city.campoalvo; facção: nenhuma declarada (herdeiro reconhecido da Coroa do Trigal) |
| Personalidade, valores, medos, limites | cauteloso e conciliador; valores: o respeito do Cais e de Meiovau; medo: herdar uma precedência já perdida. Traços e medos: ASSUMINDO (leitura da agenda, da pressão e do desconhecido do doc 01) |
| Capacidades, influência, restrições | herdeiro reconhecido da Coroa do Trigal; restrição: Aprende e representa o Trigal onde a governante manda; não governa nem assina medidas enquanto o cargo estiver ocupado; catálogo: classe diplomata, profissão — (R5-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | governante: Odile Medida-Cheia (C, doc 04 §2.2); mesma cidade de Odile Medida-Cheia (C); demais relações: desconhecidas (R5-4.3) |
| Biografia e marcos | C: É o herdeiro reconhecido do Trigal; a regra de sucessão fica para o R5-1.4; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: É o herdeiro reconhecido do Trigal; a regra de sucessão fica para o R5-1.4. S: Já conversou com o Cais sem avisar a governante. D: Por que foi reconhecido e o que faria com o preço do pão. |
| Estado atual | vivo |
| Agenda atual | Chegar ao cargo sem que o Trigal perca Meiovau nem o respeito do Cais. |
| Pressão | Os celeiros esperam que ele mantenha a precedência; Odile o acha mole demais; as outras coroas o testam em Meiovau. |
| Condição de evolução | ASSUMINDO: se `clock.medida-em-ano-ruim` (2/6) avança, escolhe entre levar à governante a ideia de ceder um ponto do preço ou intermediar um acordo no Salão das Medidas sem avisá-la; nada se resolve por calendário |
| Camadas de informação | `fact.tomlin-e-o-herdeiro-reconhecido`, `fact.tomlin-ja-conversou-com-o-cais` (`14-camadas-de-informacao.csv`) |

### Vaudouro — `city.vaudouro`

#### `npc.garvane-remo-firme` — Garvane Remo-Firme (N-10)

| Campo | Ficha |
|---|---|
| Idade | adulta (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | Humano (humano); sub-raça: humano-das-cidades |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | barqueiro-chefe das barcaças de Vaudouro; herói de Vaudouro (manteve as comportas e as barcaças numa cheia, sem perder carga nem barqueiro) |
| Cidade e facção | city.vaudouro; facção: nenhuma declarada |
| Personalidade, valores, medos, limites | direto e avesso a ser símbolo; valores: a passagem livre entre os dois rios; medo: ser feito símbolo da Casa dos Barqueiros e ver a comporta fechada. Traços e medos: ASSUMINDO (leitura da agenda, da pressão e do desconhecido do doc 01) |
| Capacidades, influência, restrições | barqueiro-chefe das barcaças de Vaudouro; restrição: Conduz barcaças e fala pelos barqueiros da cheia; não manda nas comportas nem na Casa dos Barqueiros; catálogo: classe marinheiro, profissão marinheiro-de-carga (R5-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | demais relações: desconhecidas (R5-4.3) |
| Biografia e marcos | C: Salvou carga e barqueiros na cheia; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Salvou carga e barqueiros na cheia. S: Já deixou passar uma barcaça sem taxa. D: Por que ninguém lhe pediu que chefiasse a Casa. |
| Estado atual | vivo |
| Agenda atual | Que a passagem entre os dois rios não seja fechada por uma disputa de taxa. |
| Pressão | O Trigal e o Cais pedem que ele escolha a taxa a que obedece; a Casa dos Barqueiros o quer como símbolo e ele não quer ser. |
| Condição de evolução | ASSUMINDO: se `clock.passagem-dos-rios` (3/6) avança, escolhe entre dizer por que deixou passar a barcaça sem taxa ou escolher a taxa a que obedece, em silêncio; nada se resolve por calendário |
| Camadas de informação | `fact.garvane-salvou-carga-e-barqueiros`, `fact.garvane-ja-deixou-passar-uma-barcaca` (`14-camadas-de-informacao.csv`) |

### Torrefronte — `city.torrefronte`

#### `npc.solenne-vigilia-longa` — Solenne Vigília-Longa (N-07)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | Luminar (aasimar); sub-raça: luminar-do-alvorecer |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | comandante da guarnição do Trigal; guardiã de Torrefronte; acúmulo intencional proposto (CI-07) |
| Cidade e facção | city.torrefronte; facção: faction.guarnicao-do-limite (Guarnição do Limite, líder, F-02) |
| Personalidade, valores, medos, limites | rigorosa e solitária; valores: o limite vigiado e o registro lido de um só modo; medo: abrir o limite a quem não devia, em nome do Livro. Traços e medos: ASSUMINDO (leitura da agenda, da pressão e do desconhecido do doc 01) |
| Capacidades, influência, restrições | comandante da guarnição do Trigal; restrição: Comanda só a guarnição do Trigal; não decide quem passa nem responde pelo registro sem o Vento e não conhece o que há do outro lado (contrato §6, item 11); catálogo: classe comandante, profissão — (R5-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | facção Guarnição do Limite: aliada de Conselho dos Celeiros, Vigília da Cúpula, Mesa Estreita; rival de Posto dos Pipeiros (C, doc 04 §10); demais relações: desconhecidas (R5-4.3) |
| Biografia e marcos | C: Comanda a guarnição de Torrefronte; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Comanda a guarnição de Torrefronte. S: Já abriu o limite a quem não devia, em nome do Livro. D: Que ordem original sustenta a vigia e quem escreveu as páginas do Livro. |
| Estado atual | vivo |
| Agenda atual | Manter o limite vigiado e o Livro dos Chegados lido de um só modo sem dobrar a vigia do Vento. |
| Pressão | O Vento reivindica a torre; o registro tem páginas que ninguém explica; o suprimento vem de dentro e atrasa em ano de cheia. |
| Condição de evolução | ASSUMINDO: se `clock.livro-lido-de-dois-modos` (2/6) avança, escolhe entre mostrar as páginas do Livro diante de uma testemunha da vigia ou manter as páginas fechadas e a leitura da guarnição; nada se resolve por calendário |
| Camadas de informação | `fact.solenne-comanda-a-guarnicao-de`, `fact.solenne-ja-abriu-o-limite-a` (`14-camadas-de-informacao.csv`) |

### Alvamar — `city.alvamar`

#### `npc.corlan-cais-primeiro` — Corlan Cais-Primeiro (N-02)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | Abissal Costeiro (tritao); sub-raça: abissal-das-profundezas |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | governante da Coroa do Cais; despacha na Casa do Cais |
| Cidade e facção | city.alvamar; facção: nenhuma declarada (governante da Coroa do Cais) |
| Personalidade, valores, medos, limites | calculista e vaidoso do porto; valores: o porto como porta do reino; medo: sufocar o porto com a taxa e perder o apoio da Alfândega. Traços e medos: ASSUMINDO (leitura da agenda, da pressão e do desconhecido do doc 01) |
| Capacidades, influência, restrições | governante da Coroa do Cais; restrição: Governa o Cais e comanda o porto; não taxa o Trigal em Vaudouro sem acordo e não manda no Vento; catálogo: classe diplomata, profissão — (R5-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | herdeira: Rilla Espuma-Fina (C, doc 04 §2.2); mesma cidade de Rilla Espuma-Fina, Dorgan Escudo-Lento (C); demais relações: desconhecidas (R5-4.3) |
| Biografia e marcos | C: Governa a Coroa do Cais e reside em Alvamar; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Governa a Coroa do Cais e reside em Alvamar. S: Já deixou certos navios descarregarem fora do molhe em troca de apoio. D: Como chegou ao cargo e quanto a Alfândega realmente arrecada. |
| Estado atual | vivo |
| Agenda atual | Garantir que o porto continue a ser a porta do reino e que a taxa sirva à Coroa sem sufocar o porto. |
| Pressão | A Alfândega das Marés cobra contas; o Trigal e o Vento a acusam de tratar o mar como propriedade; o estaleiro pede mais um canal. |
| Condição de evolução | ASSUMINDO: se `clock.taxa-do-molhe` (3/6) avança, escolhe entre declarar a regra de descarga fora do molhe ou continuar deixando certos navios descarregarem em troca de apoio; nada se resolve por calendário |
| Camadas de informação | `fact.corlan-governa-a-coroa-do`, `fact.corlan-ja-deixou-certos-navios-descarregarem` (`14-camadas-de-informacao.csv`) |

#### `npc.rilla-espuma-fina` — Rilla Espuma-Fina (N-05)

| Campo | Ficha |
|---|---|
| Idade | jovem (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | Nascido da Maré (elemental-agua); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | herdeira reconhecida da Coroa do Cais |
| Cidade e facção | city.alvamar; facção: nenhuma declarada (herdeira reconhecida da Coroa do Cais) |
| Personalidade, valores, medos, limites | atenta e discreta; valores: o porto aberto; medo: o porto fechado por uma disputa de taxa. Traços e medos: ASSUMINDO (leitura da agenda, da pressão e do desconhecido do doc 01) |
| Capacidades, influência, restrições | herdeira reconhecida da Coroa do Cais; restrição: Representa o Cais em Meiovau e no porto onde Corlan manda; não cobra taxa nem decide o canal; catálogo: classe diplomata, profissão — (R5-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | governante: Corlan Cais-Primeiro (C, doc 04 §2.2); mesma cidade de Corlan Cais-Primeiro, Dorgan Escudo-Lento (C); demais relações: desconhecidas (R5-4.3) |
| Biografia e marcos | C: É a herdeira reconhecida do Cais; a regra de sucessão fica para o R5-1.4; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: É a herdeira reconhecida do Cais; a regra de sucessão fica para o R5-1.4. S: Sabe de um navio que descarrega fora do molhe. D: Por que foi reconhecida e de que lado ficaria no caso do canal. |
| Estado atual | vivo |
| Agenda atual | Que o Cais mantenha a precedência sem que o porto seja fechado por uma disputa de taxa. |
| Pressão | A Alfândega a cortejou; Corlan a mantém longe das contas; o estaleiro e o santuário querem o apoio dela. |
| Condição de evolução | ASSUMINDO: se `clock.taxa-do-molhe` (3/6) avança, escolhe entre contar o que sabe do navio que descarrega fora do molhe ou calar-se para não perder a confiança da Alfândega e de Corlan; nada se resolve por calendário |
| Camadas de informação | `fact.rilla-e-a-herdeira-reconhecida`, `fact.rilla-sabe-de-um-navio-que` (`14-camadas-de-informacao.csv`) |

#### `npc.dorgan-escudo-lento` — Dorgan Escudo-Lento (N-08)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | Tartarino (tortle); sub-raça: tartarino-das-ilhas |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | comandante do molhe e da Corrente da Boca; guardião de Alvamar; acúmulo intencional proposto (CI-07) |
| Cidade e facção | city.alvamar; facção: faction.guarda-da-corrente (Guarda da Corrente, líder, F-05) |
| Personalidade, valores, medos, limites | lento, firme e pouco falante; valores: a baía fechada a quem não deve entrar e aberta a quem precisa; medo: a corrente falhar outra vez sem ninguém dizer por quê. Traços e medos: ASSUMINDO (leitura da agenda, da pressão e do desconhecido do doc 01) |
| Capacidades, influência, restrições | comandante do molhe e da Corrente da Boca; restrição: Guarda o molhe e a corrente; não taxa, não decide quem governa o porto e não comanda fora da baía; catálogo: classe comandante, profissão — (R5-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | facção Guarda da Corrente: aliada de Irmandade dos Faroleiros; rival de Alfândega das Marés (C, doc 04 §10); mesma cidade de Corlan Cais-Primeiro, Rilla Espuma-Fina (C); demais relações: desconhecidas (R5-4.3) |
| Biografia e marcos | C: Comanda o molhe e a corrente; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Comanda o molhe e a corrente. S: Já deixou a corrente frouxa por um motivo que não explicou. D: Quando e por que a corrente falhou, e quem a consertou. |
| Estado atual | vivo |
| Agenda atual | Manter a baía fechada a quem não deve entrar e aberta a quem precisa, sem que a corrente falhe. |
| Pressão | A corrente já falhou uma vez e ninguém quis dizer quando; os navios que descarregam fora do molhe lhe fogem do controle; a Coroa quer taxa e o porto quer passagem. |
| Condição de evolução | ASSUMINDO: se `clock.taxa-do-molhe` (3/6) avança, escolhe entre dizer quando e por que a corrente falhou ou manter calado o motivo de a corrente estar frouxa; nada se resolve por calendário |
| Camadas de informação | `fact.dorgan-comanda-o-molhe-e`, `fact.dorgan-ja-deixou-a-corrente-frouxa` (`14-camadas-de-informacao.csv`) |

### Enseada-Vigia — `city.enseada-vigia`

#### `npc.nerella-canto-da-barra` — Nerella Canto-da-Barra (N-09)

| Campo | Ficha |
|---|---|
| Idade | adulta (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | Sereiano (sereiano); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | piloto da Barra; herói de Enseada-Vigia (guiou uma frota inteira à barra numa noite sem luz, sem perder um casco); acúmulo intencional proposto (CI-07) |
| Cidade e facção | city.enseada-vigia; facção: faction.pilotos-da-enseada (Pilotos da Enseada, líder, F-07) |
| Personalidade, valores, medos, limites | generosa e teimosa; valores: a barra que não é de coroa nenhuma; medo: o pilotar virar ofício de uma só coroa. Traços e medos: ASSUMINDO (leitura da agenda, da pressão e do desconhecido do doc 01) |
| Capacidades, influência, restrições | piloto da Barra; restrição: Guia e ensina a barra; não decide quem recebe o farol e não representa os faroleiros; catálogo: classe marinheiro, profissão navegador (R5-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | facção Pilotos da Enseada: aliada de Irmandade dos Faroleiros, Guardas do Santuário; rival de desconhecido (sem adversário dado em facção, doc 04 §10) (C, doc 04 §10); demais relações: desconhecidas (R5-4.3) |
| Biografia e marcos | C: Pilota a barra e guiou a frota; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Pilota a barra e guiou a frota. S: Sabe uma rota de volta que não ensinou a ninguém. D: Como aprendeu a barra e por que continua a pilotar de graça. |
| Estado atual | vivo |
| Agenda atual | Que o pilotar a barra não vire ofício de uma só coroa. |
| Pressão | O Cais quer os pilotos a seu mando, o Vento quer os sinais da costa; os recém-chegados pedem a ela o caminho que só os antigos sabem. |
| Condição de evolução | ASSUMINDO: se `clock.luz-entre-coroas` (2/6) avança, escolhe entre ensinar a rota de volta aos pilotos da Enseada ou guardar a rota só para si; nada se resolve por calendário |
| Camadas de informação | `fact.nerella-pilota-a-barra-e`, `fact.nerella-sabe-uma-rota-de-volta` (`14-camadas-de-informacao.csv`) |

### Torreclara — `city.torreclara`

#### `npc.brevo-rumo-alto` — Brevo Rumo-Alto (N-03)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | Nascido do Vento (elemental-ar); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | governante da Coroa do Vento; despacha na Torre-Mestra |
| Cidade e facção | city.torreclara; facção: nenhuma declarada (governante da Coroa do Vento) |
| Personalidade, valores, medos, limites | sereno e reservado; valores: o aviso a tempo; medo: a Coroa cega numa cerração longa e ser chamado de dono da arma. Traços e medos: ASSUMINDO (leitura da agenda, da pressão e do desconhecido do doc 01) |
| Capacidades, influência, restrições | governante da Coroa do Vento; restrição: Governa os sinais e as torres do Vento; não comanda guarnição nem molhe e não decide o preço do grão ou da taxa; catálogo: classe estrategista, profissão — (R5-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | herdeiro: Astrel Noite-Clara (C, doc 04 §2.2); mesma cidade de Astrel Noite-Clara (C); demais relações: desconhecidas (R5-4.3) |
| Biografia e marcos | C: Governa a Coroa do Vento e reside em Torreclara; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Governa a Coroa do Vento e reside em Torreclara. S: Já mudou um código de torre sem avisar as outras coroas. D: Quem guarda o código-mestre além dele e o que o Vento faria se perdesse a Torre-Mestra. |
| Estado atual | vivo |
| Agenda atual | Manter a rede de torres inteira e o aviso a tempo, sem que ninguém a chame de arma. |
| Pressão | As outras coroas temem depender de quem pode calar uma torre; o Cais e o Trigal disputam torres e faróis; uma cerração longa deixaria a Coroa cega. |
| Condição de evolução | ASSUMINDO: se `clock.codigo-sem-aviso` (3/6) avança, escolhe entre avisar as outras coroas a cada mudança de código ou manter em sigilo quem guarda o código-mestre; nada se resolve por calendário |
| Camadas de informação | `fact.brevo-governa-a-coroa-do`, `fact.brevo-ja-mudou-um-codigo-de` (`14-camadas-de-informacao.csv`) |

#### `npc.astrel-noite-clara` — Astrel Noite-Clara (N-06)

| Campo | Ficha |
|---|---|
| Idade | jovem (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | Estelar (ancestral-estelar); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | herdeiro reconhecido da Coroa do Vento; liga a Torre-Mestra à Mesa dos Astros |
| Cidade e facção | city.torreclara; facção: nenhuma declarada (herdeiro reconhecido da Coroa do Vento) |
| Personalidade, valores, medos, limites | curioso e franco; valores: o céu ao alcance de todos; medo: a rede partida e a Coroa dependente de um só código. Traços e medos: ASSUMINDO (leitura da agenda, da pressão e do desconhecido do doc 01) |
| Capacidades, influência, restrições | herdeiro reconhecido da Coroa do Vento; restrição: Aprende os códigos e a leitura do céu; não decide a previsão e não altera código de torre; catálogo: classe diplomata, profissão astronomo (R5-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | governante: Brevo Rumo-Alto (C, doc 04 §2.2); mesma cidade de Brevo Rumo-Alto (C); demais relações: desconhecidas (R5-4.3) |
| Biografia e marcos | C: É o herdeiro reconhecido do Vento; a regra de sucessão fica para o R5-1.4; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: É o herdeiro reconhecido do Vento; a regra de sucessão fica para o R5-1.4. S: Leu no céu um sinal que não contou a Brevo. D: Por que foi reconhecido e o que faria se a rede fosse partida. |
| Estado atual | vivo |
| Agenda atual | Manter o aviso a tempo e o céu ao alcance de todos, para que a Coroa não dependa de um só código. |
| Pressão | Estelares e luminares o querem do lado deles em Estrelado; Brevo o vê como ligação demais ao observatório. |
| Condição de evolução | ASSUMINDO: se `clock.leitura-do-ceu` (2/6) avança, escolhe entre contar a Brevo o sinal que leu no céu ou calar o sinal e levar só a leitura de Estrelado à Mesa dos Astros; nada se resolve por calendário |
| Camadas de informação | `fact.astrel-e-o-herdeiro-reconhecido`, `fact.astrel-leu-no-ceu-um-sinal` (`14-camadas-de-informacao.csv`) |

### Meiovau — `city.meiovau`

#### `npc.tolla-mesa-posta` — Tolla Mesa-Posta (N-11)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | Pequenino (pequenino); sub-raça: pequenino-viajante |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | guardiã das Três Chaves; preside a Mesa de Meiovau |
| Cidade e facção | city.meiovau; facção: nenhuma (preside a Mesa de Meiovau, instituição sem facção-mãe, doc 04 §3.1) |
| Personalidade, valores, medos, limites | paciente e exata; valores: a Mesa útil às três coroas e a cidade fora da mão de uma só; medo: perder Meiovau para uma coroa e a chave copiada vir à tona. Traços e medos: ASSUMINDO (leitura da agenda, da pressão e do desconhecido do doc 01) |
| Capacidades, influência, restrições | guardiã das Três Chaves; restrição: Guarda as chaves e preside a Mesa; não abre nada sem as três e não vota pelas coroas; catálogo: classe guardiao-do-codigo, profissão mediador (R5-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | mesma cidade de Auriven Porta-Acesa (C); demais relações: desconhecidas (R5-4.3) |
| Biografia e marcos | C: Preside a Mesa de Meiovau; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Preside a Mesa de Meiovau. S: Sabe qual chave pode ter sido copiada. D: Quem a escolheu para a Mesa e o que a Mesa decidiu sem as três. |
| Estado atual | vivo |
| Agenda atual | Manter a Mesa útil às três coroas e a cidade fora da mão de uma só. |
| Pressão | As coroas pedem que ela decida o tratamento de quem chega do Véu e quem tem precedência; uma chave pode ter sido copiada. |
| Condição de evolução | ASSUMINDO: se `clock.acolhida-de-meiovau` (3/6) avança, escolhe entre dizer qual chave pode ter sido copiada ou calar a chave e conduzir a Mesa só pelo que está nas três mãos; nada se resolve por calendário |
| Camadas de informação | `fact.tolla-preside-a-mesa-de`, `fact.tolla-sabe-qual-chave-pode-ter` (`14-camadas-de-informacao.csv`) |

#### `npc.auriven-porta-acesa` — Auriven Porta-Acesa (N-12)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | Luminar (aasimar); sub-raça: luminar-do-alvorecer |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | benfeitor de Meiovau; mantém o Albergue dos Chegados, que recebe quem chega a pé e de barca; acúmulo intencional proposto (CI-07) |
| Cidade e facção | city.meiovau; facção: faction.albergue-dos-chegados (Albergue dos Chegados, líder, F-14) |
| Personalidade, valores, medos, limites | acolhedor e teimoso; valores: que ninguém durma ao relento; medo: o albergue pedir mais do que o doador dá. Traços e medos: ASSUMINDO (leitura da agenda, da pressão e do desconhecido do doc 01) |
| Capacidades, influência, restrições | benfeitor de Meiovau; restrição: Hospeda; não decide o tratamento de ninguém, não pergunta o motivo da chegada e não fala pela Mesa; catálogo: classe guardiao, profissão estalajadeiro (R5-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | facção Albergue dos Chegados: aliada de Casa dos Barqueiros, Irmandade dos Faroleiros; rival de Mesa Estreita (C, doc 04 §10); mesma cidade de Tolla Mesa-Posta (C); demais relações: desconhecidas (R5-4.3) |
| Biografia e marcos | C: Mantém o Albergue dos Chegados; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Mantém o Albergue dos Chegados. S: Sustenta o albergue com doação que não diz de onde vem. D: Por que o albergue nunca fecha. |
| Estado atual | vivo |
| Agenda atual | Que ninguém durma ao relento enquanto a Mesa discute como acolher. |
| Pressão | As coroas o veem como voto a favor de quem chega; o albergue pede mais do que o doador dá. |
| Condição de evolução | ASSUMINDO: se `clock.acolhida-de-meiovau` (3/6) avança, escolhe entre dizer de onde vem a doação que sustenta o albergue ou manter a origem da doação calada; nada se resolve por calendário |
| Camadas de informação | `fact.auriven-mantem-o-albergue-dos`, `fact.auriven-sustenta-o-albergue-com-doacao` (`14-camadas-de-informacao.csv`) |

## 3. Decisão pendente

**D-157:** as 12 fichas completas (md e csv). **ASSUMINDO:** traços, medos e condições de evolução como leitura editorial; origem `desconhecida`; coluna `camadas` criada no R5-6.2. Opções: **A.** aprovar (recomendada); **B.** trocar traço, medo ou condição de uma ficha (diga qual). **Recomendação: A.**
