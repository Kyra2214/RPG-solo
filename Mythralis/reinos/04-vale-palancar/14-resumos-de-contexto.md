# 14 — Resumos de contexto (R4-6.1)

**Estado:** rascunho de R4-6.1 para aprovação do autor (D-129) · **Data:** 2026-11-07

**Etiqueta:** tudo é **[PROPOSTO]**; resumo é material de consulta para a IA, não texto de lore: não altera fato nem estado. **Fonte:** doc 01 partes 1 e 2, doc 03, doc 04 §8 e §9, doc 06, doc 07, doc 10; `12-registro-mestre.csv`, `12-assentamentos.csv`, `12-facoes.csv`, `12-relogios.csv`, `12-competencias.csv`, `12-fichas.csv`, `12-relacoes.csv`, `12-demografia.csv`. Dados em `14-resumos-de-contexto.csv`. Molde: `reinos/03-veu-dos-malditos/14-resumos-de-contexto.md`.

## Regras

- **55 entidades, 1 resumo cada, com ID:** 8 cidades, 14 facções, 8 relógios e 25 pessoas (12 principais, 7 sem ficha, 6 heróis lendários). O reino não tem assentamentos. Cada resumo tem **no máximo 500 caracteres** (maior: 479) e uma linha de **vizinhança** (no máximo 300; maior: 150); a coluna `caracteres` registra o tamanho.
- **Vizinhança:** cidade → ligações do doc 07, todas sem duração; facção → aliados e rivais (doc 04 §9); relógio → localidade e entidades; pessoa → lugar e até 3 relações de certeza C (rivalidade, aliança, vínculo de cargo, dívida, nessa ordem).
- **Só o que o texto escreve:** nenhum campo foi completado por dedução. Classe e profissão (ASSUMINDO) não entram. Nenhuma duração de rota nem número de população aparece; população só como faixa de porte relativo.
- **Sem segredos nem conhecimento restrito:** o resumo não usa as linhas de suspeita e desconhecido de `12-conhecimento.csv` nem a coluna `nunca_diz` de `12-voz.csv`; relações com certeza ASSUMINDO (dívidas e medos) foram excluídas; o medo das fichas não entra. A coluna `camada_max` é **definitiva** desde o R4-6.2 (`14-camadas-de-informacao.md`): `local` para tudo, exceto os 8 relógios, `secreto` (uso de Mestre e motor); a separação por fato está em `14-camadas-de-informacao.csv`.
- **Termos de fronteira:** nenhum resumo cita Aeltharyn, as Coroas, o Véu ou mistérios de outros reinos (a palavra "Descida" só aparece no nome registrado de Praia-Pardo, "O Pouso da Descida"). Os limites de rota ("o Vale não vai além do pé das elevações", "nada além do Marco do Ermo", "só do lado do Vale") só repetem o doc 07.
- Capacidade e limite das pessoas vêm de `12-fichas.csv` e `12-competencias.csv`; sem ficha, só o papel escrito.

## Cidades

- **`city.tabuleiro-velho` — Tabuleiro-Velho — O Celeiro do Planalto.** Fortaleza-celeiro de planalto (regime fixo). Liderança: Moura Mel-Guardado. Obrigação: Guardar a reserva e os marcos dos pastos pactuados; abrigar clãs em ano ruim. Interesse: Manter a reserva e o muro. Tensão: Clãs de fora pedem reserva e os guardiões dizem que é de quem a juntou; ninguém concorda sobre o direito quando a migração falha. Povos: Ursino; Porcino; Cervino. Porte: média (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Terreiro-Maior (Tabuleiro é lugar-mãe); Vazante (Tabuleiro é lugar-mãe); Gramal (Tabuleiro é lugar-mãe).
- **`city.rasovau` — Rasovau — O Vau das Trocas.** Vau e mercado de curral no curso do Argente (regime fixo). Liderança: desconhecida. Obrigação: Manter o vau transitável e o mercado de curral. Interesse: Cobrar passagem e acertar trocas. Tensão: Dois grupos de clãs cobram pela mesma passagem e cada um diz que o Trato é seu; o Trato está em disputa (doc 04 §8). Povos: Goblin; Vulpino; Leporino. Porte: média (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Praia-Pardo (fluxo do rio); Encosta-Aberta (só apoio; lugar-mãe Rasovau; sem trilha documentada).
- **`city.praia-pardo` — Praia-Pardo — O Pouso da Descida.** Pouso fluvial onde o Argente entra nas planícies (regime fixo). Liderança: Vrissk Colônia-Quieta. Obrigação: Receber, guardar e acertar a carga de quem chega pelo rio. Interesse: Manter o pouso e o acerto de carga. Tensão: Clãs discordam sobre quem pode cobrar o desembarque; carga chega com papel que o Vale não conhece. Povos: Goblin; Insetoide. Porte: pequena (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Rasovau (fluxo do rio); rio acima (pelo Argente) (só do lado do Vale).
- **`city.alvasal` — Alvasal — O Pouso do Ermo.** Pouso de caravana na borda norte do Ermo de Sal (regime fixo). Liderança: Aruun Poço-Compartilhado. Obrigação: Guardar a cisterna e quem se prepara para o Ermo. Interesse: Manter a água e os acertos de caravana. Tensão: A cisterna não serve a todas as caravanas ao mesmo tempo; discordância sobre a preferência. Povos: Goblin; Goblin de Legião; Lupino. Porte: pequena (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Ermo de Sal (Rota do Sal; nada além do Marco do Ermo).
- **`city.terreiro-maior` — Terreiro-Maior — O Ponto da Assembleia.** Terreiro sazonal de reunião dos clãs (regime sazonal). Liderança: Yorna Palavra-Firme. Obrigação: Reunir os clãs, mediar pactos e acertar contas; o Capitão comparece sem presidir. Interesse: Decidir o rumo do reino na paz. Tensão: Mandato do Capitão de Marcha perto do fim; Assembleia dividida entre prova e aclamação. Povos: Orcano; Centauro; Lupino; Cervino. Porte: grande (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Tabuleiro-Velho (Tabuleiro é lugar-mãe); Vazante (vínculo político).
- **`city.vazante` — Vazante — O Acampamento da Marcha.** Acampamento móvel de migração e defesa (regime sazonal). Liderança: Kharos Casco-de-Marcha. Obrigação: Conduzir a migração e a defesa; responder à Assembleia. Interesse: Achar pasto e manter a marcha inteira. Tensão: Dois campos de pasto possíveis; qualquer escolha deixa um clã sem pasto. Povos: Orcano; Felídeo Errante; Centauro; Goblin de Legião; Lupino. Porte: grande (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Tabuleiro-Velho (Tabuleiro é lugar-mãe); Terreiro-Maior (vínculo político).
- **`city.encosta-aberta` — Encosta-Aberta — O Pouso das Elevações.** Pouso sazonal de vigia no pé das elevações ocidentais (regime sazonal). Liderança: desconhecida. Obrigação: Vigiar o pé das elevações e guiar quem sobe. Interesse: Manter a vigia e a pastagem de espera. Tensão: Dois clãs disputam quem guia o rebanho e quem fica com o que sobra no pouso; o Trato está em disputa (doc 04 §8). Povos: Felídeo Errante; Centauro; Cervino. Porte: pequena (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Rasovau (só apoio; lugar-mãe Rasovau; sem trilha documentada); elevações ocidentais (o Vale não vai além do pé das elevações).
- **`city.gramal` — Gramal — O Território dos Rebanhos.** Território sazonal de pasto e caça dos clãs (regime sazonal). Liderança: Tibbe Salto-Curto. Obrigação: Dividir o pasto e marcar os rebanhos. Interesse: Pasto e caça para a estação. Tensão: Clãs de gado e de caça dizem que o mesmo campo é deles. Povos: Orcano; Leporino; Ursino; Porcino; Insetoide. Porte: média (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Tabuleiro-Velho (Tabuleiro é lugar-mãe).

## Facções

- **`faction.guardas-da-marcha` — Guardas da Marcha.** Guardas da Marcha, facção. Presença: Vazante. Líder: Kharos Casco-de-Marcha. Objetivo: manter a marcha inteira e defender o Vale. Métodos: batedores, anel de carroças, turnos. Tensão: Dois campos de pasto possíveis; qualquer escolha deixa um clã sem pasto (doc 03, vazante).  
  *Vizinhança:* Aliados: Vigias da Cisterna, Guias da Encosta. Rivais: nenhum documentado.
- **`faction.clas-da-prova` — Clãs da Prova.** Clãs da Prova, facção. Presença: Terreiro-Maior. Líder: Gruzk Estandarte-Baixo. Objetivo: Capitão escolhido por prova de marcha. Métodos: voto na Assembleia, ordem de legião. Tensão: Mandato do Capitão de Marcha perto do fim; Assembleia dividida entre prova e aclamação (doc 03, terreiro-maior).  
  *Vizinhança:* Aliados: Vigias da Cisterna, Guardiões da Reserva. Rivais: Clãs da Aclamação.
- **`faction.clas-da-aclamacao` — Clãs da Aclamação.** Clãs da Aclamação, facção. Presença: Terreiro-Maior. Líder: Cadrek Cerca-Aberta. Objetivo: Capitão escolhido por aclamação. Métodos: voto na Assembleia, hospitalidade. Tensão: Mandato do Capitão de Marcha perto do fim; Assembleia dividida entre prova e aclamação (doc 03, terreiro-maior).  
  *Vizinhança:* Aliados: Mesa da Partilha, Clãs da Caça. Rivais: Clãs da Prova.
- **`faction.guardioes-da-reserva` — Guardiões da Reserva.** Guardiões da Reserva, facção. Presença: Tabuleiro-Velho. Líder: Moura Mel-Guardado. Objetivo: reserva de quem a juntou. Métodos: muro, entrega em ordem. Tensão: Clãs de fora pedem reserva e os guardiões dizem que é de quem a juntou; ninguém concorda sobre o direito quando a migração falha (doc 03, tabuleiro-velho).  
  *Vizinhança:* Aliados: Clãs da Prova, Clãs do Gado, Vigias das Trilhas. Rivais: Mesa da Partilha.
- **`faction.mesa-da-partilha` — Mesa da Partilha.** Mesa da Partilha, facção. Presença: Tabuleiro-Velho. Líder: Pocha Ninhada-Farta. Objetivo: que a reserva socorra quem fica sem. Métodos: doação, pedido na Assembleia. Tensão: Clãs de fora pedem reserva e os guardiões dizem que é de quem a juntou; ninguém concorda sobre o direito quando a migração falha (doc 03, tabuleiro-velho).  
  *Vizinhança:* Aliados: Clãs da Aclamação, Clãs da Caça. Rivais: Guardiões da Reserva.
- **`faction.pedras-de-cima` — Pedras de Cima.** Pedras de Cima, facção. Presença: Rasovau. Líder: Zinu Troca-Justa. Objetivo: o Trato do Vau para o grupo de cima. Métodos: cobrança, cuidado das pedras. Tensão: Dois grupos de clãs cobram pela mesma passagem e cada um diz que o Trato é seu (doc 03, rasovau).  
  *Vizinhança:* Aliados: Receptores do Pouso. Rivais: Pedras de Baixo.
- **`faction.pedras-de-baixo` — Pedras de Baixo.** Pedras de Baixo, facção. Presença: Rasovau. Líder: Lumbo Pedra-de-Baixo. Objetivo: o Trato do Vau para o grupo de baixo. Métodos: cobrança, travessia de rebanhos. Tensão: Dois grupos de clãs cobram pela mesma passagem e cada um diz que o Trato é seu (doc 03, rasovau).  
  *Vizinhança:* Aliados: Balança do Pouso. Rivais: Pedras de Cima.
- **`faction.receptores-do-pouso` — Receptores do Pouso.** Receptores do Pouso, facção. Presença: Praia-Pardo. Líder: Vrissk Colônia-Quieta. Objetivo: desembarque cobrado por quem recebe. Métodos: rampa, guarda de carga. Tensão: Clãs discordam sobre quem pode cobrar o desembarque; carga chega com papel que o Vale não conhece (doc 03, praia-pardo).  
  *Vizinhança:* Aliados: Pedras de Cima. Rivais: Balança do Pouso.
- **`faction.balanca-do-pouso` — Balança do Pouso.** Balança do Pouso, facção. Presença: Praia-Pardo. Líder: Grebb Balança-Justa. Objetivo: desembarque cobrado em parte por todos os clãs. Métodos: pesagem, partilha. Tensão: Clãs discordam sobre quem pode cobrar o desembarque; carga chega com papel que o Vale não conhece (doc 03, praia-pardo).  
  *Vizinhança:* Aliados: Pedras de Baixo. Rivais: Receptores do Pouso.
- **`faction.vigias-da-cisterna` — Vigias da Cisterna.** Vigias da Cisterna, facção. Presença: Alvasal. Líder: Aruun Poço-Compartilhado. Objetivo: guardar a água e atender as caravanas. Métodos: cerca de sal, turnos. Tensão: A cisterna não serve a todas as caravanas ao mesmo tempo; discordância sobre a preferência (doc 03, alvasal).  
  *Vizinhança:* Aliados: Guardas da Marcha, Clãs da Prova. Rivais: nenhum documentado.
- **`faction.guias-da-encosta` — Guias da Encosta.** Guias da Encosta, facção. Presença: Encosta-Aberta. Líder: Tessun Salto-Calado. Objetivo: guiar rebanhos até o pé das elevações. Métodos: guia, conhecimento das trilhas. Tensão: Dois clãs disputam quem guia o rebanho e quem fica com o que sobra no pouso (doc 03, encosta-aberta).  
  *Vizinhança:* Aliados: Guardas da Marcha. Rivais: Vigias das Trilhas.
- **`faction.vigias-das-trilhas` — Vigias das Trilhas.** Vigias das Trilhas, facção. Presença: Encosta-Aberta. Líder: Sendro Clareira-Vigia. Objetivo: vigiar as trilhas e ficar com o que sobra no pouso. Métodos: vigia de pedra, cerca de espera. Tensão: Dois clãs disputam quem guia o rebanho e quem fica com o que sobra no pouso (doc 03, encosta-aberta).  
  *Vizinhança:* Aliados: Guardiões da Reserva. Rivais: Guias da Encosta.
- **`faction.clas-do-gado` — Clãs do Gado.** Clãs do Gado, facção. Presença: Gramal. Líder: Tibbe Salto-Curto. Objetivo: o campo para o gado. Métodos: marca, cerca de parição. Tensão: Clãs de gado e de caça dizem que o mesmo campo é deles (doc 03, gramal).  
  *Vizinhança:* Aliados: Guardiões da Reserva. Rivais: Clãs da Caça.
- **`faction.clas-da-caca` — Clãs da Caça.** Clãs da Caça, facção. Presença: Gramal. Líder: Ghorla Fartura-Dividida. Objetivo: o campo para a caça. Métodos: batida, divisão da caça. Tensão: Clãs de gado e de caça dizem que o mesmo campo é deles (doc 03, gramal).  
  *Vizinhança:* Aliados: Clãs da Aclamação, Mesa da Partilha. Rivais: Clãs do Gado.

## Relógios

- **`clock.reserva-em-ano-ruim` — CP-01 A reserva sem regra.** CP-01 A reserva sem regra (Tabuleiro-Velho); progresso 2/6. Move: clã de fora pedindo abrigo e reserva no celeiro; guardiões recusando a entrega; pasto fraco observado. Trava: entrega em ordem pelo Trato do Celeiro; doação de Pocha a quem o celeiro não atende; acordo na Assembleia sobre a ordem de entrega. Decisor no segmento 5: Assembleia de Clãs (Terreiro-Maior), com o Trato do Celeiro ouvido. Inação: só avança com novo pedido de reserva ou nova recusa observados.  
  *Vizinhança:* Lugar: Tabuleiro-Velho. Entidades: Moura Mel-Guardado; Pocha Ninhada-Farta; Guardiões da Reserva; Mesa da Partilha.
- **`clock.dupla-cobranca` — CP-02 Dois grupos, um vau.** CP-02 Dois grupos, um vau (Rasovau); progresso 3/6. Move: quem atravessa pagando duas vezes ou nenhuma; cobrança de um grupo contestada pelo outro em público. Trava: recados de Quira entre os dois grupos; acerto na Tenda dos Recados; apelo do Trato do Vau à Assembleia. Decisor no segmento 5: Assembleia de Clãs (Terreiro-Maior), por apelo do Trato do Vau (doc 04 §6); um dos dois grupos pode dizer a quem se paga antes. Inação: só avança com nova travessia contestada observada.  
  *Vizinhança:* Lugar: Rasovau. Entidades: Zinu Troca-Justa; Quira Recado-Certo; Pedras de Cima; Pedras de Baixo.
- **`clock.desembarque` — CP-03 O desembarque cobrado de dois jeitos.** CP-03 O desembarque cobrado de dois jeitos (Praia-Pardo); progresso 2/6. Move: carga que chega com papel que o Vale não conhece; Receptores e Balança cobrando critérios diferentes na mesma rampa. Trava: guarda de carga no Barracão até o acerto; Vrissk aceitando carga sem conferir de onde vem; pacto de passagem do desembarque. Decisor no segmento 5: Trato do Desembarque (Praia-Pardo), guardião do pouso: Vrissk. Inação: só avança com nova carga contestada observada.  
  *Vizinhança:* Lugar: Praia-Pardo. Entidades: Vrissk Colônia-Quieta; Receptores do Pouso; Balança do Pouso.
- **`clock.cisterna` — CP-04 A cisterna única.** CP-04 A cisterna única (Alvasal); progresso 2/6. Move: duas caravanas na cisterna ao mesmo tempo; recusa de água observada; queixa de caravana ou de clã sobre preferência. Trava: turnos de vigia de Gruzk; atendimento em ordem pelo Trato do Poço; pacto de preferência na cisterna (nunca posto, doc 04 §10, item 4). Decisor no segmento 5: Trato do Poço (Alvasal), chefe: Aruun. Inação: só avança com nova espera ou recusa observada.  
  *Vizinhança:* Lugar: Alvasal. Entidades: Gruzk Estandarte-Baixo; Vigias da Cisterna.
- **`clock.sucessao-do-capitao` — CP-05 O mandato que acaba.** CP-05 O mandato que acaba (Terreiro-Maior); progresso 3/6. Move: nova divergência pública entre Clãs da Prova e Clãs da Aclamação; Kharos adiando o anúncio; clã recusando a rodada. Trava: ordem da reunião por Yorna; hospedagem neutra de Cassan na Ramada; rodada em que todos são ouvidos (L-01). Decisor no segmento 5: Assembleia de Clãs (Terreiro-Maior), ordem da reunião por Yorna. Inação: só avança com nova divergência pública ou novo adiamento observados.  
  *Vizinhança:* Lugar: Terreiro-Maior. Entidades: Yorna Palavra-Firme; Cassan Galhada-Velha; Kharos Casco-de-Marcha; Clãs da Prova; Clãs da Aclamação.
- **`clock.campo-da-marcha` — CP-06 Dois campos, um rumo.** CP-06 Dois campos, um rumo (Vazante); progresso 2/6. Move: batedores trazendo notícias diferentes dos dois campos; clã sem pasto na estação reclamando ao Capitão. Trava: conselho de Rukhan; consulta aos Tratos dos campos; reserva do Tabuleiro-Velho como retaguarda (lugar-mãe). Decisor no segmento 5: Capitão de Marcha (Vazante): Kharos, diante dos clãs. Inação: só avança com nova queixa de clã ou novo relato de batedor observados.  
  *Vizinhança:* Lugar: Vazante. Entidades: Kharos Casco-de-Marcha; Rukhan Retorno-Certo; Guardas da Marcha.
- **`clock.guia-das-trilhas` — CP-07 Quem guia o rebanho.** CP-07 Quem guia o rebanho (Encosta-Aberta); progresso 2/6. Move: rebanho esperando guia no pouso; disputa pública sobre o que sobra no pouso; Vigias das Trilhas recusando a espera. Trava: Tessun guiando sem tomar partido; pastagem de espera no Cerca de Espera; acordo do Trato da Encosta. Decisor no segmento 5: Trato da Encosta (Encosta-Aberta), chefe `desconhecido` (doc 04 §8). Inação: só avança com novo rebanho em espera ou nova recusa observados.  
  *Vizinhança:* Lugar: Encosta-Aberta. Entidades: Tessun Salto-Calado; Guias da Encosta; Vigias das Trilhas.
- **`clock.capim-disputado` — CP-08 Gado e caça no mesmo campo.** CP-08 Gado e caça no mesmo campo (Gramal); progresso 2/6. Move: marcas e mourões contestados; acusação pública de estrago do pasto; estação de pasto fraco observada. Trava: divisão do campo por Tibbe; batida combinada fora da parição; acerto entre Clãs do Gado e da Caça na Tenda de Marcas. Decisor no segmento 5: Trato dos Rebanhos (Gramal), chefe: Tibbe. Inação: só avança com nova contestação de marca ou acusação observadas.  
  *Vizinhança:* Lugar: Gramal. Entidades: Tibbe Salto-Curto; Ghorla Fartura-Dividida; Clãs do Gado; Clãs da Caça.

## Pessoas

- **`npc.kharos-casco-de-marcha` — Kharos Casco-de-Marcha.** Kharos Casco-de-Marcha, Centauro. Capitão de Marcha; guardião da Vazante. Limite: chefia a migração e a defesa; não preside a Assembleia nem decide pactos. Pressão: O mandato está perto do fim; a Assembleia se divide entre prova e aclamação; qualquer campo escolhido deixa um clã sem pasto.  
  *Vizinhança:* Lugar: Vazante. Relações: aliança: Vigias da Cisterna; aliança: Guias da Encosta; vínculo de cargo: Rukhan Retorno-Certo.
- **`npc.yorna-palavra-firme` — Yorna Palavra-Firme.** Yorna Palavra-Firme, Orcano. Guardiã da Pedra da Palavra; cuida da ordem da Assembleia (Trato do Terreiro); guardiã do Terreiro-Maior. Limite: dá a palavra e ordena a reunião; não vota, não escolhe o Capitão e não comanda marcha. Pressão: Os dois lados da sucessão a cobram; um terreiro cheio é frágil se o Capitão estiver longe.  
  *Vizinhança:* Lugar: Terreiro-Maior. Relações: vínculo de cargo: Cassan Galhada-Velha; vínculo de cargo: Kharos Casco-de-Marcha; dívida: Terreiro-Maior.
- **`npc.gruzk-estandarte-baixo` — Gruzk Estandarte-Baixo.** Gruzk Estandarte-Baixo, Goblin de Legião. Comandante das vigias de Alvasal (turnos de legião); guardião de Alvasal. Limite: comanda só as vigias do pouso; responde ao Capitão de Marcha na defesa e não decide a ordem das caravanas. Pressão: A cisterna não serve a todas ao mesmo tempo; as caravanas e os clãs discordam sobre a preferência.  
  *Vizinhança:* Lugar: Alvasal. Relações: rivalidade: Clãs da Aclamação; aliança: Vigias da Cisterna; aliança: Guardiões da Reserva.
- **`npc.moura-mel-guardado` — Moura Mel-Guardado.** Moura Mel-Guardado, Ursino. Guardiã do Celeiro; chefe do Trato do Celeiro. Limite: abre e fecha o celeiro; não decide o direito à reserva, só a ordem de entrega. Pressão: Clãs de fora pedem reserva e os guardiões dizem que é de quem a juntou; ela cala a favor de uns e de outros.  
  *Vizinhança:* Lugar: Tabuleiro-Velho. Relações: rivalidade: Mesa da Partilha; aliança: Clãs da Prova; aliança: Clãs do Gado.
- **`npc.pocha-ninhada-farta` — Pocha Ninhada-Farta.** Pocha Ninhada-Farta, Porcino. Benfeitora do Tabuleiro-Velho: abre a reserva dela a quem o celeiro não atende. Limite: dá do que é dela; não abre a reserva comum. Pressão: Os clãs guardiões a acusam de enfraquecer o celeiro; os de fora esperam mais dela do que ela pode dar.  
  *Vizinhança:* Lugar: Tabuleiro-Velho. Relações: rivalidade: Guardiões da Reserva; aliança: Clãs da Aclamação; aliança: Clãs da Caça.
- **`npc.tessun-salto-calado` — Tessun Salto-Calado.** Tessun Salto-Calado, Felídeo Errante. Guia das trilhas até o pé das elevações; herói de Encosta-Aberta (levou um rebanho inteiro até o pouso numa estação ruim, sem perder um animal). Limite: guia até o pé das elevações; o que há do outro lado é rumor e não é dele (contrato §5, item 9). Pressão: Dois clãs disputam quem guia; os que ficam acham que quem sobe volta com o rumo trocado.  
  *Vizinhança:* Lugar: Encosta-Aberta. Relações: rivalidade: Vigias das Trilhas; aliança: Guardas da Marcha; vínculo de cargo: Sendro Clareira-Vigia.
- **`npc.rukhan-retorno-certo` — Rukhan Retorno-Certo.** Rukhan Retorno-Certo, Lupino. Batedor-chefe da Marcha; herói da Vazante (reuniu uma marcha dispersa antes do inverno). Limite: bate o campo e aconselha; não decide o campo nem comanda defesa. Pressão: Os clãs querem que ele diga qual dos dois campos é o certo; ele sabe que qualquer um custa a alguém.  
  *Vizinhança:* Lugar: Vazante. Relações: vínculo de cargo: Bolgra Forja-de-Campo.
- **`npc.zinu-troca-justa` — Zinu Troca-Justa.** Zinu Troca-Justa, Goblin. Guardião do Vau; chefe de um dos grupos de clãs que cobram a passagem. Limite: cuida das pedras e cobra a passagem do lado dele; não fecha o vau. Pressão: Quem atravessa paga duas vezes ou nenhuma; o outro grupo o acusa de cobrar por pedras que não são suas.  
  *Vizinhança:* Lugar: Rasovau. Relações: rivalidade: Pedras de Baixo; aliança: Receptores do Pouso; vínculo de cargo: Quira Recado-Certo.
- **`npc.quira-recado-certo` — Quira Recado-Certo.** Quira Recado-Certo, Vulpino. Benfeitora de Rasovau: leva recados entre os dois grupos que cobram e entre clãs que não se falam. Limite: leva recado; não decide, não cobra e não negocia em nome de ninguém. Pressão: Os dois grupos a veem como espiã do outro.  
  *Vizinhança:* Lugar: Rasovau. Relações: vínculo de cargo: Lumbo Pedra-de-Baixo.
- **`npc.vrissk-colonia-quieta` — Vrissk Colônia-Quieta.** Vrissk Colônia-Quieta, Insetoide. Guardião do Pouso; chefe do Trato do Desembarque. Limite: recebe, guarda e acerta; não pergunta de onde vem a carga nem decide por que desce (contrato §5, item 5). Pressão: Os clãs discordam sobre quem cobra; a carga chega com papel que o Vale não conhece.  
  *Vizinhança:* Lugar: Praia-Pardo. Relações: rivalidade: Balança do Pouso; aliança: Pedras de Cima; vínculo de cargo: Grebb Balança-Justa.
- **`npc.tibbe-salto-curto` — Tibbe Salto-Curto.** Tibbe Salto-Curto, Leporino. Guardião do Território; chefe do Trato dos Rebanhos. Limite: marca os rebanhos e divide o campo; não escolhe o campo da Marcha. Pressão: Cada lado acusa o outro de estragar o pasto; a estação não espera.  
  *Vizinhança:* Lugar: Gramal. Relações: rivalidade: Clãs da Caça; aliança: Guardiões da Reserva; vínculo de cargo: Ghorla Fartura-Dividida.
- **`npc.cassan-galhada-velha` — Cassan Galhada-Velha.** Cassan Galhada-Velha, Cervino. Benfeitor do Terreiro-Maior: hospeda clãs de fora na Ramada dos Visitantes. Limite: hospeda e escuta; não vota e não toma lado em público. Pressão: Os dois lados da sucessão querem a hospedagem dele como apoio.  
  *Vizinhança:* Lugar: Terreiro-Maior. Relações: nenhuma documentada.
- **`npc.bruga-colheita-salva` — Bruga Colheita-Salva.** Bruga Colheita-Salva, Porcino, falecido(a). Herói lendário: figura da versão dos Porcinos da lenda de Tabuleiro-Velho; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Tabuleiro-Velho. Relações: nenhuma documentada.
- **`npc.jessik-trato-primeiro` — Jessik Trato-Primeiro.** Jessik Trato-Primeiro, Vulpino, falecido(a). Herói lendário: figura da versão dos Vulpinos da lenda de Rasovau; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Rasovau. Relações: nenhuma documentada.
- **`npc.nokk-peso-alheio` — Nokk Peso-Alheio.** Nokk Peso-Alheio, Goblin, falecido(a). Herói lendário: figura da versão dos Goblins da lenda de Praia-Pardo; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Praia-Pardo. Relações: nenhuma documentada.
- **`npc.grebb-balanca-justa` — Grebb Balança-Justa.** Grebb Balança-Justa, Goblin. Benfeitor de Praia-Pardo (tríade); líder de Balança do Pouso (objetivo: desembarque cobrado em parte por todos os clãs). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Praia-Pardo. Relações: rivalidade: Receptores do Pouso; aliança: Pedras de Baixo.
- **`npc.urrek-bando-parado` — Urrek Bando-Parado.** Urrek Bando-Parado, Lupino, falecido(a). Herói lendário: figura da versão dos Lupinos da lenda de Alvasal; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Alvasal. Relações: nenhuma documentada.
- **`npc.aruun-poco-compartilhado` — Aruun Poço-Compartilhado.** Aruun Poço-Compartilhado, Lupino. Benfeitor de Alvasal (tríade); chefe do Trato do Poço; líder de Vigias da Cisterna (objetivo: guardar a água e atender as caravanas). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Alvasal. Relações: aliança: Guardas da Marcha; aliança: Clãs da Prova; dívida: Alvasal.
- **`npc.hukk-pedra-de-divisa` — Hukk Pedra-de-Divisa.** Hukk Pedra-de-Divisa, Orcano, falecido(a). Herói lendário: figura da versão dos Orcanos da lenda de Terreiro-Maior; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Terreiro-Maior. Relações: nenhuma documentada.
- **`npc.bolgra-forja-de-campo` — Bolgra Forja-de-Campo.** Bolgra Forja-de-Campo, Orcano. Benfeitor da Vazante (tríade). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Vazante. Relações: nenhuma documentada.
- **`npc.sendro-clareira-vigia` — Sendro Clareira-Vigia.** Sendro Clareira-Vigia, Cervino. Guardião de Encosta-Aberta (tríade); líder de Vigias das Trilhas (objetivo: vigiar as trilhas e ficar com o que sobra no pouso). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Encosta-Aberta. Relações: rivalidade: Guias da Encosta; aliança: Guardiões da Reserva; vínculo de cargo: Cadrek Cerca-Aberta.
- **`npc.cadrek-cerca-aberta` — Cadrek Cerca-Aberta.** Cadrek Cerca-Aberta, Centauro. Benfeitor de Encosta-Aberta (tríade); líder de Clãs da Aclamação (objetivo: Capitão escolhido por aclamação). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Encosta-Aberta. Relações: rivalidade: Clãs da Prova; aliança: Mesa da Partilha; aliança: Clãs da Caça.
- **`npc.zzirr-favo-dividido` — Zzirr Favo-Dividido.** Zzirr Favo-Dividido, Insetoide, falecido(a). Herói lendário: figura da versão dos Insetoides da lenda de Gramal; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Gramal. Relações: nenhuma documentada.
- **`npc.ghorla-fartura-dividida` — Ghorla Fartura-Dividida.** Ghorla Fartura-Dividida, Porcino. Benfeitora de Gramal (tríade); líder de Clãs da Caça (objetivo: o campo para a caça). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Gramal. Relações: rivalidade: Clãs do Gado; aliança: Clãs da Aclamação; aliança: Mesa da Partilha.
- **`npc.lumbo-pedra-de-baixo` — Lumbo Pedra-de-Baixo.** Lumbo Pedra-de-Baixo, Leporino. Chefe de um dos grupos de clãs que cobram a passagem do vau; líder de Pedras de Baixo (objetivo: o Trato do Vau para o grupo de baixo). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito (doc 04 §8 e §9).  
  *Vizinhança:* Lugar: Rasovau. Relações: rivalidade: Pedras de Cima; aliança: Balança do Pouso.

## Conferências

| Verificação | Resultado |
|---|---|
| Entidades com resumo e ID | 55 de 55 (47 do registro mestre mais 8 relógios) |
| Entidades do registro mestre sem resumo | 0 |
| Resumo acima de 500 caracteres | 0 (maior: 479) |
| Vizinhança acima de 300 caracteres | 0 (maior: 150) |
| Resumo que usa suspeita, desconhecido de `12-conhecimento.csv` ou `nunca_diz` | 0 |
| Duração de rota, número de população ou termo de fronteira | 0 (só o nome registrado "O Pouso da Descida") |
| Campo vazio | 0 |

## Decisão pendente

**D-129:** aprovar os 55 resumos e a vizinhança. Opções: **A.** aprovar; **B.** trocar o texto de uma entidade (diga qual). **Recomendação: A.**
