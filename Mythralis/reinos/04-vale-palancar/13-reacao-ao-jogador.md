# 13 — Reação do reino ao jogador (R4-5.4)

**Estado:** R4-5.4, D-127 (aguardando o autor) · **Data:** 2026-11-07

**Etiqueta:** tudo é **[PROPOSTO]**. **Fonte:** doc 01 (parte 1), 04 (§2 a §9), 05 (§1 a §3), 06 e 07; `12-registro-mestre.csv`, `12-relacoes.csv`, `12-relogios.csv`; `plano/INTEGRACAO_FUTURA_RPG_SOLO.md` (MP-06, MP-07). Dados em `13-reputacao.csv` (22 linhas) e `13-canais.csv` (11 linhas). Molde: `reinos/03-.../13-reacao-ao-jogador.md`.

## 1. Regras e limites

- Descreve **a camada de mundo**: o que cada lugar e facção valoriza ou pune, quem reage e por que canal a notícia chega. **Números de fama, alcance, preço e sorteio de rumor pertencem ao motor** (MP-07); aqui só há linguagem qualitativa.
- **Reputação não substitui prova**: rumor justifica investigação, não condenação. O mesmo ato pode dar fama diferente em cada lugar e facção.
- A notícia viaja só pelos canais da seção 4. **Toda duração de rota é `desconhecida` (doc 07):** a notícia **não tem prazo definido**. Lugares sazonais mudam de campo, então o canal que passa por eles também muda.
- A IA narra a distorção do rumor, nunca a transforma em fato. Nenhuma reação exige a presença do jogador e nenhuma é punição automática.
- **Nada aqui toca Aeltharyn, as Coroas, o Véu ou o Pacto Humano**, nem diz como a fama chegaria a eles. O reino não tem trono nem jurisdição fora do Vale (D-102); quem julga é a Assembleia, o Capitão ou o Trato do lugar (doc 04 §6).
- Mapeamento de ato para reação é leitura editorial (ASSUMINDO), derivada só de objetivos, leis e justiça já escritos; nenhuma facção, ofício ou NPC novo. Clã é forma de organização, não traço de raça (W-01): nenhuma reação depende da raça do jogador.

## 2. Fama e reputação por lugar e facção (R4-5.4a)

Disposição é **por entidade**, não global. Ver `13-reputacao.csv` (prefixo `rep.`).

| Entidade | Ganha disposição | Perde disposição | Quem reage | Canais |
|---|---|---|---|---|
| `city.tabuleiro-velho` | entregar a reserva em ordem; respeitar os marcos e o Campo dos Marcos; renovar o pacto quando o campo muda; pedir abrigo sem esvaziar o celeiro | mexer em marco; esvaziar o celeiro; exigir reserva fora da ordem do Trato; deixar a reserva sem guarda quando os clãs partem | `npc.moura-mel-guardado`, `npc.pocha-ninhada-farta`, `faction.guardioes-da-reserva`, `faction.mesa-da-partilha` | Campo dos Marcos; Celeiro Comprido; Mesa da Partilha |
| `city.rasovau` | pagar o que o pacto manda; manter o vau transitável; levar o recado inteiro; ouvir os dois grupos | fechar o vau; passar sem pagar a nenhum dos grupos; tomar partido em público entre os dois; ler recado alheio | `npc.zinu-troca-justa`, `npc.quira-recado-certo`, `npc.lumbo-pedra-de-baixo`, `faction.pedras-de-cima`, `faction.pedras-de-baixo` | Tenda dos Recados; Cais de Cordas; Curral de Trocas |
| `city.praia-pardo` | declarar a carga no Barracão; acertar no pouso e não na margem; respeitar a rampa e a Fogueira de Chegada; esperar o acerto | descarregar na margem; esconder carga; escolher por conta própria quem cobra o desembarque; perguntar a Vrissk de onde vem o que ele aceita | `npc.vrissk-colonia-quieta`, `npc.grebb-balanca-justa`, `faction.receptores-do-pouso`, `faction.balanca-do-pouso` | Mastro dos Avisos; Barracão dos Recém-Chegados; Fogueira de Chegada |
| `city.alvasal` | esperar a vez na cisterna; respeitar a cerca de sal e os turnos de vigia; pagar o que o pacto de passagem manda | furar a fila; sujar ou esvaziar a cisterna; negociar preferência por fora do Trato do Poço | `npc.gruzk-estandarte-baixo`, `npc.aruun-poco-compartilhado`, `faction.vigias-da-cisterna` | Pátio das Cargas; Cisterna Branca; Cerca de Sal |
| `city.terreiro-maior` | deixar a arma fora do Fogo do Meio; ouvir todos antes de fechar; respeitar a Pedra da Palavra; hospedar na Ramada sem tomar lado | chegar armado ao Fogo do Meio; calar um clã; tomar lado em público contra a rodada; usar a hospedagem para pressionar | `npc.yorna-palavra-firme`, `npc.cassan-galhada-velha`, `faction.clas-da-prova`, `faction.clas-da-aclamacao` | Pedra da Palavra; Fogo do Meio; Ramada dos Visitantes |
| `city.vazante` | acompanhar a marcha inteira; entregar o relato do campo inteiro; obedecer a ordem do campo; ajudar o Cercado dos Feridos | partir um clã; sair da marcha sem avisar; esconder um campo; fazer pacto ou cobrar passagem em nome do Capitão | `npc.kharos-casco-de-marcha`, `npc.rukhan-retorno-certo`, `faction.guardas-da-marcha` | Tablado dos Avisos; Tenda do Capitão; Anel de Carroças |
| `city.encosta-aberta` | guiar até o pé das elevações e parar ali; usar a pastagem de espera; deixar a trilha aberta | levar rebanho além do pé das elevações; tomar o lado de um dos grupos; recusar a espera a rebanho parado | `npc.tessun-salto-calado`, `npc.sendro-clareira-vigia`, `faction.guias-da-encosta`, `faction.vigias-das-trilhas` | Pedra de Vigia; Pouso dos Cascos; Cerca de Espera |
| `city.gramal` | respeitar a marca do rebanho; dividir o campo por pacto de pasto; combinar a batida fora da parição | apagar marca; mover mourão; estragar o pasto; caçar na Cerca de Parição | `npc.tibbe-salto-curto`, `npc.ghorla-fartura-dividida`, `faction.clas-do-gado`, `faction.clas-da-caca` | Tenda de Marcas; Mourão Velho; Aguada Larga |
| `faction.guardas-da-marcha` | manter a marcha inteira; servir de batedor e de anel de carroças; respeitar os turnos | partir um clã; abandonar a marcha | `npc.kharos-casco-de-marcha` | Anel de Carroças; turnos |
| `faction.clas-da-prova` | defender Capitão escolhido por prova de marcha; votar na Assembleia; respeitar a ordem de legião | esconder a prova; recusar a rodada | `npc.gruzk-estandarte-baixo` | voto na Assembleia; ordem de legião |
| `faction.clas-da-aclamacao` | defender Capitão escolhido por aclamação; votar na Assembleia; hospedar com cuidado | calar a aclamação; recusar a rodada | `npc.cadrek-cerca-aberta` | voto na Assembleia; hospitalidade |
| `faction.guardioes-da-reserva` | guardar a reserva de quem a juntou; entregar em ordem; cuidar do muro | esvaziar o celeiro; exigir reserva fora da ordem | `npc.moura-mel-guardado` | Celeiro Comprido; Muro de Pedra Seca |
| `faction.mesa-da-partilha` | socorrer quem fica sem; pedir à Assembleia; doar com transparência | enfraquecer o celeiro sem aviso; esconder doação | `npc.pocha-ninhada-farta` | Mesa da Partilha; pedido na Assembleia |
| `faction.pedras-de-cima` | cuidar das pedras do vau; cobrar o que o pacto manda; manter a travessia | fechar o vau; cobrar por pedras que não cuida | `npc.zinu-troca-justa` | Pedras do Vau; Tenda dos Recados |
| `faction.pedras-de-baixo` | cobrar o que o pacto manda; travessia de rebanhos; manter o vau | fechar o vau; mover pedra sem avisar | `npc.lumbo-pedra-de-baixo` | Pedras do Vau; Curral de Trocas |
| `faction.receptores-do-pouso` | receber a carga na rampa; guardar a carga; acertar no pouso | descarregar na margem; deixar carga sumir | `npc.vrissk-colonia-quieta` | Rampa de Pedra; Barracão dos Recém-Chegados |
| `faction.balanca-do-pouso` | pesar e partilhar o desembarque; acertar entre os clãs | esconder peso; cobrar sem partilha | `npc.grebb-balanca-justa` | pesagem; Mastro dos Avisos |
| `faction.vigias-da-cisterna` | guardar a água; atender as caravanas em ordem; manter a cerca de sal | deixar a cisterna sem turno; negar água sem pacto | `npc.aruun-poco-compartilhado` | Cerca de Sal; Cisterna Branca |
| `faction.guias-da-encosta` | guiar rebanhos até o pé das elevações; manter as trilhas abertas | guiar além do pé das elevações; fechar trilha | `npc.tessun-salto-calado` | Pedra de Vigia; guia |
| `faction.vigias-das-trilhas` | vigiar as trilhas; cuidar da espera no pouso | recusar a espera; prender guia sem motivo | `npc.sendro-clareira-vigia` | Pouso dos Cascos; Cerca de Espera |
| `faction.clas-do-gado` | marcar e proteger o rebanho; cercar a parição; dividir o campo | apagar marca; fechar campo à caça sem pacto | `npc.tibbe-salto-curto` | Tenda de Marcas; Cerca de Parição |
| `faction.clas-da-caca` | combinar a batida; dividir a caça; respeitar a parição | caçar na parição; negar a divisão | `npc.ghorla-fartura-dividida` | Mourão Velho; batida |

## 3. Procurado, favor e reparação (R4-5.4b)

### 3.1 Estados de "procurado" (ASSUMINDO, ordinais)

| Estado | Condição | Limite escrito |
|---|---|---|
| Rumor | alguém diz que o jogador fez algo | justifica investigação, não condenação |
| Investigação | o lugar ou a facção aponta o ato e a regra | testemunho, registro e inferência são provas diferentes (leitura editorial) |
| Procurado num lugar | o Trato do lugar aponta quebra do pacto | quem aplica o pacto no lugar é o Trato; o apelo vai à Assembleia (doc 04 §3 e §6) |
| Infração na marcha | o Capitão aponta infração durante a marcha | o Capitão julga infração durante a marcha (doc 04 §6); a sanção é qualitativa |
| Disputa entre clãs | dois clãs ou dois lugares reivindicam o caso | a Assembleia julga disputa entre clãs, com a Pedra da Palavra (doc 04 §6) |
| Resolvido | decisão registrada | a sanção é qualitativa (§3.4); crime grave, exílio e pena de morte são `desconhecidos` (doc 04 §6) |

### 3.2 Ato, regra e autoridade

| Ato | Regra | Autoridade inicial | Desfecho possível |
|---|---|---|---|
| chegar armado ao Fogo do Meio ou calar um clã | Lei do Fogo do Meio (L-01) | Trato do Terreiro | o que se faz com quem chega armado e quem decide que todos foram ouvidos seguem `desconhecidos` |
| quebrar ou não renovar um pacto | Lei do Pacto Renovado (L-02) | Assembleia; Trato do lugar | perda de voz no Trato até reparar; quem renova se um signatário diverge segue `desconhecido` |
| partir um clã ou cobrar passagem em nome do Capitão | Lei da Marcha Inteira (L-03) | Assembleia | o mandato pode custar; quando uma ordem "parte" um clã segue `desconhecido` |
| fechar o vau ou passar sem pagar | Lei do Vau (L-04) | Trato do Vau; apelo à Assembleia | pagar o que o pacto manda; a quem pagar com dois grupos segue `desconhecido` |
| descarregar na margem ou esconder carga | Lei do Desembarque (L-05) | Trato do Desembarque | guarda e acerto no pouso; que papel o Vale reconhece segue `desconhecido` |
| furar a fila ou sujar a cisterna | Lei da Cisterna (L-06) | Trato do Poço | atendimento em ordem; a preferência entre caravanas segue `desconhecida` |
| esvaziar o celeiro ou exigir reserva fora da ordem | Lei da Reserva (L-07) | Trato do Celeiro | ordem de entrega; quem tem direito à reserva segue `desconhecido` |
| apagar marca ou estragar pasto | Lei da Marca (L-08) | Trato dos Rebanhos | reparação em gado ou reserva (doc 04 §6); quem tem razão sobre o capim segue `desconhecido` |
| levar rebanho além do pé das elevações | Lei da Trilha (L-09) | Trato da Encosta | o Vale não vai além; o que há do outro lado não é matéria de lei do Vale |
| tratar o visitante de outro reino como representante | Lei do Hóspede (L-10) | clã anfitrião; Trato do lugar | o visitante é julgado como o clã que o hospeda; o visitante sem anfitrião segue `desconhecido` |

### 3.3 Favor

- **Pessoal:** NPC com vínculo (`12-relacoes.csv`) pode pedir ou conceder; só sabe o que consta em `12-conhecimento.csv`. Afeto: 0 relação documentada (D-122); nenhum favor de afeto é inventado.
- **Institucional:** facção cobra ou concede conforme `13-reputacao.csv`; não é troca automática.
- **Dívida de pacto:** o Trato deve ao pacto e à Assembleia (doc 03 §1); os sazonais devem apoio ao lugar-mãe (ASSUMINDO, DR-01 a DR-04). Não há preço nem quantia aqui.
- **Hospitalidade:** a Ramada recebe clãs de fora; o hóspede é julgado como o clã que o hospeda (L-10).

### 3.4 Reparação

Escrita no doc 04 §6, em termos qualitativos: **reparação em gado ou reserva** (disputa entre clãs), **reparação ou exclusão do campo** (infração na marcha), **pagar o que o pacto manda** (passagem) e **perda de voz no Trato até reparar** (pacto quebrado). Exílio e pena de morte **não estão fixados**. Reparar não apaga o rumor: a reputação muda por canal e por lugar, no ritmo do motor.

## 4. Intermediários (R4-5.4c)

Quem leva a notícia, o pedido ou a decisão. Ver `13-canais.csv` (prefixo `canal.vale-`).

| Canal | Função | O que o cânone diz | Limite | Rota |
|---|---|---|---|---|
| `canal.vale-assembleia` — Assembleia de Clãs e Pedra da Palavra | levar o pedido ou a decisão entre os Tratos e o Capitão | decide na paz no Terreiro-Maior, faz e desfaz pactos, julga disputa entre clãs, escolhe e remove o Capitão; Yorna dá a palavra e não vota | não conduz marcha nem defesa; o resultado de uma rodada não está decidido aqui; tudo vale só entre clãs do Vale | `route.terreiro-vazante` |
| `canal.vale-marcha` — Marcha e batedores | levar notícia entre os campos e a Vazante | os batedores de Rukhan batem os campos para a Marcha; o Capitão ordena a marcha; o Tablado dos Avisos leva a palavra ao anel | alcance e duração `desconhecidos`; a marcha muda de campo com o pasto, então o canal também muda | `route.tabuleiro-vazante`, `route.terreiro-vazante` |
| `canal.vale-campo-dos-marcos` — Campo dos Marcos e pactos | registrar e renovar os pactos de passagem e pasto | os marcos dos pastos pactuados ficam no Campo dos Marcos, no Tabuleiro-Velho; o pacto é renovado quando o campo muda | registra o pacto, não prova quem o cumpre; marco sem clã é mistério aberto (MPL-01); sem duração | `route.tabuleiro-terreiro-maior`, `route.tabuleiro-gramal` |
| `canal.vale-recados` — Tenda dos Recados | levar recado entre os dois grupos do vau e entre clãs que não se falam | a Tenda guarda recados; Quira leva recado e não decide, não cobra e não negocia por ninguém | o recado não é prova; o que Quira guarda na Tenda é `desconhecido` | `route.praia-pardo-rasovau` |
| `canal.vale-desembarque` — Mastro dos Avisos e Barracão | registrar o que chega pelo rio e avisar o pouso | o pouso recebe, guarda e acerta a carga; o Mastro dos Avisos anuncia; o Barracão guarda | prova o que foi visto na rampa, não de onde vem; a Descida não ganha causa, número nem fim rio acima | `route.praia-pardo-rasovau` |
| `canal.vale-cisterna` — Pátio das Cargas e Cerca de Sal | registrar quem espera a água e levar notícia entre caravanas | o Trato do Poço atende a caravana sob a cerca de sal e os turnos de vigia | só do lado do Vale; nada sobre o Ermo de Sal além do Marco do Ermo; sem duração | nenhuma escrita |
| `canal.vale-trilhas` — Pedra de Vigia e guias da Encosta | levar notícia entre o pouso e os rebanhos | a Pedra de Vigia vê as elevações; os guias levam o rebanho até o pé delas e não além | o que há do outro lado é rumor e não é do Vale; sem duração | `route.rasovau-encosta-aberta` |
| `canal.vale-tenda-de-marcas` — Tenda de Marcas e Mourão Velho | registrar a marca dos rebanhos e levar o acerto entre gado e caça | a Tenda de Marcas guarda a marca; o campo se divide por pacto de pasto | a marca prova a posse do rebanho, não do campo; quem tem razão sobre o capim é `desconhecido` | `route.tabuleiro-gramal` |
| `canal.vale-ramada` — Ramada dos Visitantes | levar notícia por clãs hospedados | Cassan hospeda clãs de fora na Ramada sem tomar lado; todo clã é ouvido antes de ir embora | a hospedagem não prova apoio; por que a Ramada nunca fica vazia é `desconhecido` | nenhuma escrita |
| `canal.vale-reserva` — Celeiro e Mesa da Partilha | levar o pedido de reserva e a resposta | o Trato do Celeiro cuida da ordem de entrega; Pocha abre a reserva própria a quem o celeiro não atende | não decide quem tem direito à reserva (L-07); de onde vem a reserva de Pocha é `desconhecido` | `route.tabuleiro-terreiro-maior` |
| `canal.vale-rumor` — Rumor local | espalhar uma versão de um ato dentro do lugar | cada lugar tem rumores não confirmados (doc 01 parte 1 §2) | rumor não vira fato e justifica investigação, não condenação; a IA narra a distorção sem transformá-la em fato | nenhuma escrita |

**Lacunas explícitas:** não há caçador de recompensa, cartaz ou mensageiro de ofício escrito no Vale (Quira leva recado, não é mensageira de ofício do reino); nenhum foi criado. Alvasal não tem rota escrita para outro lugar do Vale no doc 07; a notícia só chega pelas caravanas e por quem chega ou parte (ASSUMINDO).

## 5. Ligação com o plano do jogo

- **MP-07 (fama e rumores):** a disposição é por entidade, qualitativa, e vem de `13-reputacao.csv`; os canais de `13-canais.csv` e as rotas do doc 07 dão os caminhos; números, alcance e duração são do motor, não daqui.
- **MP-06 (caçadores):** o Vale não escreve caçador de recompensa; se o motor oferecer um, só pode agir pelos canais acima e sem criar facção ou ofício.
- Gatilhos de relógio (`12-relogios.csv`) e eventos (`12-eventos.csv`) podem mudar a disposição só por causa observável; nenhuma reação é automática.

## 6. Conferências

| Verificação | Resultado |
|---|---|
| Linhas de reputação | 22 (8 lugares e 14 facções) |
| Canais | 11 |
| ID fora do registro mestre ou NPC falecido | 0 |
| Rota fora do doc 07 | 0 |
| Campo vazio | 0 |
| Fronteira (Aeltharyn, Coroas, Véu, Pacto Humano) | 0 |
| Número de fama, preço, prazo ou distância | 0 |
| Reação que depende da raça do jogador | 0 |

## 7. Decisão pendente

**D-127:** aprovar a reação, os estados de procurado e os 11 canais. Opções: **A.** aprovar; **B.** trocar ou tirar uma reação ou canal (diga qual). **Recomendação: A.**
