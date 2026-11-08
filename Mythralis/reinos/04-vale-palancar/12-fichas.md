# 12 — Fichas completas dos doze principais (R4-4.1)

**Estado:** R4-4.1, D-120 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, nome, lugar, facção ou fato novo**: reúne e completa as fichas N-01 a N-12 (doc 01 parte 2, D-107) no campo a campo de `MODELO_DE_REINO.md` §4, com raça, sub-raça, classe e profissão de `ALINHAMENTO_PALANCAR.md` (D-114), facções e aliados de `12-facoes.csv` e doc 04, relógios de `06`. Dados em `12-fichas.csv` (as mesmas 17 colunas dos reinos 2 e 3).
**Estados:** C confirmado · S suspeita · D desconhecido · ASSUMINDO = leitura editorial sem fonte direta (traços, medos e condições de evolução). Nada se infere além dos docs 01, 04 e 06. Escala qualitativa (D-63); nenhum mestiço (D-64).
**Campos feitos depois da ficha:** competências detalhadas (R4-4.2), matriz de relações (R4-4.3), voz (R4-4.4), camadas de informação (feitas no R4-6.2: coluna `camadas`). Onde a ficha precisa deles, traz só o que já está documentado e `desconhecida`.

## 1. Conferências

| Verificação | Resultado |
|---|---|
| Principais com ficha completa | **12 de 12** (17 campos, 0 vazio) |
| Agenda e pressão distintas | **12 agendas e 12 pressões diferentes** |
| Heróis vivos / generais | 2 (Tessun, Rukhan) / 2 (Kharos, Gruzk) |
| Povo, sub-raça, classe e profissão | conferidos em `ALINHAMENTO_PALANCAR.md` (sub-raça `—` onde o catálogo não atribui; profissão `—` onde não há ofício escrito) |
| Origem | desconhecida em todos (nenhuma documentada) |
| Estado | vivo em todos |
| Acúmulos de cargo (CI-07) | **6 de 7 marcados como "acúmulo intencional proposto"** (Kharos, Gruzk, Moura, Zinu, Vrissk, Tibbe); o sétimo, Aruun, não tem ficha completa (fora do mínimo de D-22) |
| Sem acúmulo | Yorna, Pocha, Cassan, Rukhan, Quira, Tessun |
| Lugares com principal | 8 de 8 (Tabuleiro-Velho, Rasovau, Terreiro-Maior e Vazante 2; os demais 1) |
| Relógio ligado a cada principal | 12 de 12 (8 relógios de `06`; progresso 2/6 e 3/6 como em `06`) |

## 2. Fichas por lugar

### Tabuleiro-Velho — `city.tabuleiro-velho`

#### `npc.moura-mel-guardado` — Moura Mel-Guardado (N-04)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | homem-urso (Ursino); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | guardiã do Celeiro; chefe do Trato do Celeiro; acúmulo intencional proposto (CI-07) |
| Cidade e facção | city.tabuleiro-velho; facção: faction.guardioes-da-reserva (Guardiões da Reserva, líder, F-04) |
| Personalidade, valores, medos, limites | guardada e calada; valores: o celeiro de pé; medo: fechar a porta a quem precisa ou abri-la a quem esvazia. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | guardiã do Celeiro; chefe do Trato do Celeiro; restrição: abre e fecha o celeiro; não decide o direito à reserva, só a ordem de entrega; catálogo: classe guardiao, profissão — (R4-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | facção Guardiões da Reserva: aliada de Clãs da Prova, Clãs do Gado, Vigias das Trilhas; rival de Mesa da Partilha (C, doc 04 §9); mesma cidade de Pocha Ninhada-Farta (C); demais relações: desconhecidas (R4-4.3) |
| Biografia e marcos | C: Chefia o Trato do Celeiro; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Chefia o Trato do Celeiro. S: Guarda uma parte da reserva fora da conta. D: Quem tem direito à reserva quando a migração falha. |
| Estado atual | vivo |
| Agenda atual | Manter a reserva e o muro sem fechar o celeiro a quem chega em ano ruim. |
| Pressão | Clãs de fora pedem reserva e os guardiões dizem que é de quem a juntou; ela cala a favor de uns e de outros. |
| Condição de evolução | ASSUMINDO: se `clock.reserva-em-ano-ruim` (2/6) avança, escolhe entre abrir a parte da reserva que fica fora da conta ou manter o celeiro como está; nada se resolve por calendário |
| Camadas de informação | `fact.kharos-capitao-vazante`, `fact.kharos-decidiu-sucessor` (`14-camadas-de-informacao.csv`) |

#### `npc.pocha-ninhada-farta` — Pocha Ninhada-Farta (N-05)

| Campo | Ficha |
|---|---|
| Idade | idosa (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | homem-javali (Porcino); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | benfeitora do Tabuleiro-Velho: abre a reserva dela a quem o celeiro não atende |
| Cidade e facção | city.tabuleiro-velho; facção: faction.mesa-da-partilha (Mesa da Partilha, líder, F-05) |
| Personalidade, valores, medos, limites | generosa e teimosa; valores: nenhum clã com fome; medo: ser a causa de o celeiro enfraquecer. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | benfeitora do Tabuleiro-Velho: abre a reserva dela a quem o celeiro não atende; restrição: dá do que é dela; não abre a reserva comum; catálogo: classe sobrevivente, profissão — (R4-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | facção Mesa da Partilha: aliada de Clãs da Aclamação, Clãs da Caça; rival de Guardiões da Reserva (C, doc 04 §9); mesma cidade de Moura Mel-Guardado (C); demais relações: desconhecidas (R4-4.3) |
| Biografia e marcos | C: Distribui reserva própria a clãs que o celeiro não atende; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Distribui reserva própria a clãs que o celeiro não atende. S: Sustenta, sem dizer, um clã que o Trato recusou. D: De onde vem a reserva dela. |
| Estado atual | vivo |
| Agenda atual | Que nenhum clã passe fome por causa de uma disputa sobre o direito à reserva. |
| Pressão | Os clãs guardiões a acusam de enfraquecer o celeiro; os de fora esperam mais dela do que ela pode dar. |
| Condição de evolução | ASSUMINDO: se `clock.reserva-em-ano-ruim` (2/6) avança, escolhe entre dizer de onde vem a reserva dela ou seguir distribuindo sem explicar; nada se resolve por calendário |
| Camadas de informação | `fact.yorna-cuida-ordem-reuniao`, `fact.yorna-costume-esquecido` (`14-camadas-de-informacao.csv`) |

### Rasovau — `city.rasovau`

#### `npc.zinu-troca-justa` — Zinu Troca-Justa (N-08)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | goblin (Goblin); sub-raça: goblin-das-sucatas |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | guardião do Vau; chefe de um dos grupos de clãs que cobram a passagem; acúmulo intencional proposto (CI-07) |
| Cidade e facção | city.rasovau; facção: faction.pedras-de-cima (Pedras de Cima, líder, F-06) |
| Personalidade, valores, medos, limites | negociador e orgulhoso; valores: o Trato como direito; medo: o vau virar guerra entre os dois grupos. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | guardião do Vau; chefe de um dos grupos de clãs que cobram a passagem; restrição: cuida das pedras e cobra a passagem do lado dele; não fecha o vau; catálogo: classe guardiao, profissão comerciante (R4-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | facção Pedras de Cima: aliada de Receptores do Pouso; rival de Pedras de Baixo (C, doc 04 §9); mesma cidade de Quira Recado-Certo (C); demais relações: desconhecidas (R4-4.3) |
| Biografia e marcos | C: Chefia um dos grupos que cobram; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Chefia um dos grupos que cobram. S: Já deixou passar quem não pagou para ganhar um favor. D: A quem pertencem de fato as pedras do vau. |
| Estado atual | vivo |
| Agenda atual | Que o Trato do Vau seja dele sem guerra com o outro grupo. |
| Pressão | Quem atravessa paga duas vezes ou nenhuma; o outro grupo o acusa de cobrar por pedras que não são suas. |
| Condição de evolução | ASSUMINDO: se `clock.dupla-cobranca` (3/6) avança, escolhe entre abrir mão de parte da cobrança ou manter a passagem do lado dele; nada se resolve por calendário |
| Camadas de informação | `fact.gruzk-comanda-turnos-cisterna`, `fact.gruzk-negou-agua` (`14-camadas-de-informacao.csv`) |

#### `npc.quira-recado-certo` — Quira Recado-Certo (N-09)

| Campo | Ficha |
|---|---|
| Idade | adulta (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | homem-raposa (Vulpino); sub-raça: homem-raposa-dos-vales |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | benfeitora de Rasovau: leva recados entre os dois grupos que cobram e entre clãs que não se falam |
| Cidade e facção | city.rasovau; facção: nenhuma declarada |
| Personalidade, valores, medos, limites | discreta e ágil; valores: o recado chegar inteiro; medo: ser tomada por espiã e perder a confiança dos dois lados. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | benfeitora de Rasovau: leva recados entre os dois grupos que cobram e entre clãs que não se falam; restrição: leva recado; não decide, não cobra e não negocia em nome de ninguém; catálogo: classe diplomata, profissão mensageiro (R4-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | sem facção declarada; mesma cidade de Zinu Troca-Justa (C); demais relações: desconhecidas (R4-4.3) |
| Biografia e marcos | C: Leva recados entre os grupos; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Leva recados entre os grupos. S: Guarda os recados que preferiu não entregar. D: O que guarda na Tenda dos Recados. |
| Estado atual | vivo |
| Agenda atual | Que o vau tenha um só Trato antes de alguém sair ferido. |
| Pressão | Os dois grupos a veem como espiã do outro. |
| Condição de evolução | ASSUMINDO: se `clock.dupla-cobranca` (3/6) avança, escolhe entre entregar os recados que guardou ou seguir levando só os que escolheu; nada se resolve por calendário |
| Camadas de informação | `fact.moura-chefia-trato-celeiro`, `fact.moura-reserva-fora-da-conta` (`14-camadas-de-informacao.csv`) |

### Praia-Pardo — `city.praia-pardo`

#### `npc.vrissk-colonia-quieta` — Vrissk Colônia-Quieta (N-10)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | insetoide (Insetoide); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | guardião do Pouso; chefe do Trato do Desembarque; acúmulo intencional proposto (CI-07) |
| Cidade e facção | city.praia-pardo; facção: faction.receptores-do-pouso (Receptores do Pouso, líder, F-08) |
| Personalidade, valores, medos, limites | quieto e minucioso; valores: o pouso em ordem; medo: o desembarque virar disputa e a carga sumir. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | guardião do Pouso; chefe do Trato do Desembarque; restrição: recebe, guarda e acerta; não pergunta de onde vem a carga nem decide por que desce (contrato §5, item 5); catálogo: classe guardiao, profissão avaliador-de-materiais (R4-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | facção Receptores do Pouso: aliada de Pedras de Cima; rival de Balança do Pouso (C, doc 04 §9); demais relações: desconhecidas (R4-4.3) |
| Biografia e marcos | C: Chefia o Trato do Desembarque; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Chefia o Trato do Desembarque. S: Já recusou carga por um motivo que não explicou. D: Quem escolheu o pouso e o que mais chega que ninguém registra. |
| Estado atual | vivo |
| Agenda atual | Receber o que desce sem que o desembarque vire disputa entre clãs. |
| Pressão | Os clãs discordam sobre quem cobra; a carga chega com papel que o Vale não conhece. |
| Condição de evolução | ASSUMINDO: se `clock.desembarque` (2/6) avança, escolhe entre explicar a carga que recusou ou manter o silêncio; nada se resolve por calendário |
| Camadas de informação | `fact.pocha-distribui-reserva-propria`, `fact.pocha-sustenta-cla-recusado` (`14-camadas-de-informacao.csv`) |

### Alvasal — `city.alvasal`

#### `npc.gruzk-estandarte-baixo` — Gruzk Estandarte-Baixo (N-03)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | hobgoblin (Goblin de Legião); sub-raça: legionario-dos-estandartes |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | comandante das vigias de Alvasal (turnos de legião); guardião de Alvasal; acúmulo intencional proposto (CI-07) |
| Cidade e facção | city.alvasal; facção: faction.clas-da-prova (Clãs da Prova, líder, F-02) |
| Personalidade, valores, medos, limites | disciplinado e seco; valores: a ordem do turno; medo: a cisterna virar briga entre caravanas e clãs. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | comandante das vigias de Alvasal (turnos de legião); guardião de Alvasal; restrição: comanda só as vigias do pouso; responde ao Capitão de Marcha na defesa e não decide a ordem das caravanas; catálogo: classe comandante, profissão — (R4-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | facção Clãs da Prova: aliada de Vigias da Cisterna, Guardiões da Reserva; rival de Clãs da Aclamação (C, doc 04 §9); demais relações: desconhecidas (R4-4.3) |
| Biografia e marcos | C: Comanda os turnos de vigia da cisterna; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Comanda os turnos de vigia da cisterna. S: Já negou água a quem tinha direito para evitar um mal maior. D: Que ordem original sustenta os turnos de legião. |
| Estado atual | vivo |
| Agenda atual | Manter a cisterna guardada e as caravanas atendidas sem que ninguém leve o pouso à briga. |
| Pressão | A cisterna não serve a todas ao mesmo tempo; as caravanas e os clãs discordam sobre a preferência. |
| Condição de evolução | ASSUMINDO: se `clock.cisterna` (2/6) avança, escolhe entre declarar a ordem de preferência dos turnos na cisterna ou seguir atendendo caso a caso; nada se resolve por calendário |
| Camadas de informação | `fact.tessun-guia-rebanhos-ao-pouso`, `fact.tessun-ouviu-do-outro-lado` (`14-camadas-de-informacao.csv`) |

### Terreiro-Maior — `city.terreiro-maior`

#### `npc.yorna-palavra-firme` — Yorna Palavra-Firme (N-02)

| Campo | Ficha |
|---|---|
| Idade | idosa (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | orcano (Orcano); sub-raça: orcano-das-estepes |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | guardiã da Pedra da Palavra; cuida da ordem da Assembleia (Trato do Terreiro); guardiã do Terreiro-Maior |
| Cidade e facção | city.terreiro-maior; facção: nenhuma declarada |
| Personalidade, valores, medos, limites | severa e paciente; valores: a palavra dada e a Assembleia inteira; medo: o Fogo do Meio virar briga com a sucessão em aberto. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | guardiã da Pedra da Palavra; cuida da ordem da Assembleia (Trato do Terreiro); guardiã do Terreiro-Maior; restrição: dá a palavra e ordena a reunião; não vota, não escolhe o Capitão e não comanda marcha; catálogo: classe guardiao-de-reliquias, profissão mestre-de-cerimonias (R4-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | sem facção declarada; mesma cidade de Cassan Galhada-Velha (C); demais relações: desconhecidas (R4-4.3) |
| Biografia e marcos | C: Cuida da ordem da reunião; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Cuida da ordem da reunião. S: Sabe de um costume da Pedra da Palavra que a Assembleia esqueceu. D: O que a Pedra da Palavra registra e quem a escolheu para o cargo. |
| Estado atual | vivo |
| Agenda atual | Manter a Assembleia inteira e sem arma no Fogo do Meio até a escolha do próximo Capitão. |
| Pressão | Os dois lados da sucessão a cobram; um terreiro cheio é frágil se o Capitão estiver longe. |
| Condição de evolução | ASSUMINDO: se `clock.sucessao-do-capitao` (3/6) avança, escolhe entre dizer o costume da Pedra da Palavra que a Assembleia esqueceu ou manter a ordem sem dizê-lo; nada se resolve por calendário |
| Camadas de informação | `fact.rukhan-bate-campos`, `fact.rukhan-terceiro-campo` (`14-camadas-de-informacao.csv`) |

#### `npc.cassan-galhada-velha` — Cassan Galhada-Velha (N-12)

| Campo | Ficha |
|---|---|
| Idade | idosa (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | homem-cervo (Cervino); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | benfeitor do Terreiro-Maior: hospeda clãs de fora na Ramada dos Visitantes |
| Cidade e facção | city.terreiro-maior; facção: nenhuma declarada |
| Personalidade, valores, medos, limites | hospitaleiro e prudente; valores: todo clã ouvido; medo: ser usado como apoio de um dos lados. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | benfeitor do Terreiro-Maior: hospeda clãs de fora na Ramada dos Visitantes; restrição: hospeda e escuta; não vota e não toma lado em público; catálogo: classe diplomata, profissão estalajadeiro (R4-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | sem facção declarada; mesma cidade de Yorna Palavra-Firme (C); demais relações: desconhecidas (R4-4.3) |
| Biografia e marcos | C: Hospeda clãs de fora no Terreiro; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Hospeda clãs de fora no Terreiro. S: Sabe qual clã apoia qual lado. D: Por que a Ramada nunca fica vazia. |
| Estado atual | vivo |
| Agenda atual | Que nenhum clã saia da Assembleia sem ter sido ouvido. |
| Pressão | Os dois lados da sucessão querem a hospedagem dele como apoio. |
| Condição de evolução | ASSUMINDO: se `clock.sucessao-do-capitao` (3/6) avança, escolhe entre dizer em público qual clã apoia qual lado ou continuar escutando sem tomar lado; nada se resolve por calendário |
| Camadas de informação | `fact.zinu-chefia-grupo-cobra`, `fact.zinu-deixou-passar-sem-pagar` (`14-camadas-de-informacao.csv`) |

### Vazante — `city.vazante`

#### `npc.kharos-casco-de-marcha` — Kharos Casco-de-Marcha (N-01)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | centauro (Centauro); sub-raça: centauro-das-pradarias |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Capitão de Marcha; guardião da Vazante; acúmulo intencional proposto (CI-07) |
| Cidade e facção | city.vazante; facção: faction.guardas-da-marcha (Guardas da Marcha, líder, F-01) |
| Personalidade, valores, medos, limites | firme e prudente; valores: a marcha inteira; medo: partir os clãs ao escolher o campo e entregar o mandato incompleto (S). Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | Capitão de Marcha; guardião da Vazante; restrição: chefia a migração e a defesa; não preside a Assembleia nem decide pactos; catálogo: classe comandante, profissão — (R4-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | facção Guardas da Marcha: aliada de Vigias da Cisterna, Guias da Encosta; rivais: desconhecido (não toma lado na sucessão) (C, doc 04 §9); mesma cidade de Rukhan Retorno-Certo (C); demais relações: desconhecidas (R4-4.3) |
| Biografia e marcos | C: É o Capitão de Marcha e reside na Vazante; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: É o Capitão de Marcha e reside na Vazante. S: Já decidiu, sozinho, quem deseja que o suceda. D: Como foi escolhido e o que faria se a Assembleia o contrariasse. |
| Estado atual | vivo |
| Agenda atual | Escolher o campo de pasto da próxima marcha sem partir os clãs e entregar o mandato inteiro. |
| Pressão | O mandato está perto do fim; a Assembleia se divide entre prova e aclamação; qualquer campo escolhido deixa um clã sem pasto. |
| Condição de evolução | ASSUMINDO: se `clock.sucessao-do-capitao` (3/6) avança, escolhe entre apontar em público quem deseja que o suceda ou entregar a escolha à Assembleia sem dizer o que deseja; nada se resolve por calendário |
| Camadas de informação | `fact.quira-leva-recados`, `fact.quira-recados-nao-entregues` (`14-camadas-de-informacao.csv`) |

#### `npc.rukhan-retorno-certo` — Rukhan Retorno-Certo (N-07)

| Campo | Ficha |
|---|---|
| Idade | adulta (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | homem-lobo (Lupino); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | batedor-chefe da Marcha; herói da Vazante (reuniu uma marcha dispersa antes do inverno) |
| Cidade e facção | city.vazante; facção: nenhuma declarada |
| Personalidade, valores, medos, limites | atento e reservado; valores: nenhum clã de fora do pasto; medo: errar o campo e custar um clã. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | batedor-chefe da Marcha; herói da Vazante (reuniu uma marcha dispersa antes do inverno); restrição: bate o campo e aconselha; não decide o campo nem comanda defesa; catálogo: classe batedor, profissão — (R4-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | sem facção declarada; mesma cidade de Kharos Casco-de-Marcha (C); demais relações: desconhecidas (R4-4.3) |
| Biografia e marcos | C: Bate os campos para a Marcha; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Bate os campos para a Marcha. S: Já viu um terceiro campo e não o disse. D: O que viu da última vez que se afastou da Marcha. |
| Estado atual | vivo |
| Agenda atual | Achar o pasto que não deixe nenhum clã de fora. |
| Pressão | Os clãs querem que ele diga qual dos dois campos é o certo; ele sabe que qualquer um custa a alguém. |
| Condição de evolução | ASSUMINDO: se `clock.campo-da-marcha` (2/6) avança, escolhe entre dizer o terceiro campo que viu ou aconselhar só entre os dois já conhecidos; nada se resolve por calendário |
| Camadas de informação | `fact.vrissk-chefia-trato-desembarque`, `fact.vrissk-recusou-carga` (`14-camadas-de-informacao.csv`) |

### Encosta-Aberta — `city.encosta-aberta`

#### `npc.tessun-salto-calado` — Tessun Salto-Calado (N-06)

| Campo | Ficha |
|---|---|
| Idade | adulta (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | tabaxi (Felídeo Errante); sub-raça: felideo-das-dunas |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | guia das trilhas até o pé das elevações; herói de Encosta-Aberta (levou um rebanho inteiro até o pouso numa estação ruim, sem perder um animal) |
| Cidade e facção | city.encosta-aberta; facção: faction.guias-da-encosta (Guias da Encosta, líder, F-11) |
| Personalidade, valores, medos, limites | calado e leve; valores: as trilhas abertas; medo: voltar de lá e não ser acreditado. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | guia das trilhas até o pé das elevações; herói de Encosta-Aberta (levou um rebanho inteiro até o pouso numa estação ruim, sem perder um animal); restrição: guia até o pé das elevações; o que há do outro lado é rumor e não é dele (contrato §5, item 9); catálogo: classe batedor, profissão guia-de-montanha (R4-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | facção Guias da Encosta: aliada de Guardas da Marcha; rival de Vigias das Trilhas (C, doc 04 §9); demais relações: desconhecidas (R4-4.3) |
| Biografia e marcos | C: Guia rebanhos até o pouso; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Guia rebanhos até o pouso. S: Já ouviu do outro lado mais do que conta. D: Por que escolheu ficar do lado de cá. |
| Estado atual | vivo |
| Agenda atual | Que a disputa pelo guia do rebanho não feche as trilhas. |
| Pressão | Dois clãs disputam quem guia; os que ficam acham que quem sobe volta com o rumo trocado. |
| Condição de evolução | ASSUMINDO: se `clock.guia-das-trilhas` (2/6) avança, escolhe entre dizer o que ouviu do outro lado das elevações ou guiar calado; nada se resolve por calendário |
| Camadas de informação | `fact.tibbe-chefia-trato-rebanhos`, `fact.tibbe-favoreceu-gado` (`14-camadas-de-informacao.csv`) |

### Gramal — `city.gramal`

#### `npc.tibbe-salto-curto` — Tibbe Salto-Curto (N-11)

| Campo | Ficha |
|---|---|
| Idade | adulta (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | homem-coelho (Leporino); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | guardião do Território; chefe do Trato dos Rebanhos; acúmulo intencional proposto (CI-07) |
| Cidade e facção | city.gramal; facção: faction.clas-do-gado (Clãs do Gado, líder, F-13) |
| Personalidade, valores, medos, limites | ligeiro e conciliador; valores: o campo dividido em paz; medo: a estação acabar antes do acerto. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | guardião do Território; chefe do Trato dos Rebanhos; restrição: marca os rebanhos e divide o campo; não escolhe o campo da Marcha; catálogo: classe domador, profissão criador-de-animais (R4-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | facção Clãs do Gado: aliada de Guardiões da Reserva; rival de Clãs da Caça (C, doc 04 §9); demais relações: desconhecidas (R4-4.3) |
| Biografia e marcos | C: Chefia o Trato dos Rebanhos; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Chefia o Trato dos Rebanhos. S: Já favoreceu os clãs de gado no último acerto. D: Qual lado tem razão sobre o capim. |
| Estado atual | vivo |
| Agenda atual | Dividir o campo entre gado e caça antes que a estação acabe. |
| Pressão | Cada lado acusa o outro de estragar o pasto; a estação não espera. |
| Condição de evolução | ASSUMINDO: se `clock.capim-disputado` (2/6) avança, escolhe entre repartir o campo sem favorecer o gado ou manter o último acerto; nada se resolve por calendário |
| Camadas de informação | `fact.cassan-hospeda-visitantes`, `fact.cassan-sabe-quem-apoia-quem` (`14-camadas-de-informacao.csv`) |

## 3. O que a ficha não decide

- Nenhum fato sobre a reserva do Tabuleiro-Velho, o marco sem clã, as pedras do vau, a carga do desembarque, a cisterna, a Pedra da Palavra, o terceiro campo ou o que há do outro lado das elevações; permanecem em S e D.
- Nenhum resultado de relógio, de sucessão ou de disputa; nenhuma regra de L-07 (direito à reserva) nem das demais lacunas das leis.
- Aruun, Cadrek, Lumbo, Grebb, Sendro, Ghorla, Jessik, Bruga, Nokk, Urrek, Hukk, Bolgra, Zzirr e os heróis lendários continuam só com nome e povo (fora do mínimo de D-22).
- Nada altera Aeltharyn, as Coroas ou o Véu; nenhum NPC sabe nada deles além de rumor.

## 4. Decisão pendente

**D-120:** aprovar as 12 fichas e a marca de acúmulo intencional (CI-07). Opções: **A.** aprovar; **B.** trocar traço, medo ou condição de evolução de alguma ficha (diga qual). **Recomendação: A.**
