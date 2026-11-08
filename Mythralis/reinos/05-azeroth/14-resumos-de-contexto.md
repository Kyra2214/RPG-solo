# Azeroth — Resumos de contexto (doc 14, R5-6.1)

**Estado:** R5-6.1, D-166 aceita pela continuação · **Data:** 2026-11-07

**Etiqueta:** tudo é **[PROPOSTO]**; resumo é material de consulta para a IA, não texto de lore: não altera fato nem estado. **Fonte:** doc 01 partes 1 e 2, docs 03, 04, 06 e 07; `12-registro-mestre.csv`, `12-assentamentos.csv`, `12-facoes.csv`, `12-relogios.csv`, `12-competencias.csv`, `12-fichas.csv`, `12-relacoes.csv`, `12-demografia.csv`. Dados em `14-resumos-de-contexto.csv`. Molde: `reinos/04-vale-palancar/14-resumos-de-contexto.md`.

## Regras

- **73 entidades, 1 resumo cada, com ID:** 10 cidades, 15 facções, 10 relógios e 38 pessoas (12 principais, 18 sem ficha, 8 heróis lendários). Cada resumo tem **no máximo 500 caracteres** (maior: 476) e uma linha de **vizinhança** (no máximo 300; maior: 181); a coluna `caracteres` registra o tamanho.
- **Vizinhança:** cidade → ligações do doc 07, todas sem duração; facção → aliados e rivais (doc 04 §10); relógio → localidade e entidades; pessoa → lugar e até 3 relações de certeza C (rivalidade, aliança, vínculo de cargo, dívida, nessa ordem).
- **Só o que o texto escreve:** nenhum campo foi completado por dedução. Classe e profissão (ASSUMINDO) não entram. Nenhuma duração de rota nem número de população aparece; população só como faixa de porte relativo.
- **Sem segredos nem conhecimento restrito:** o resumo não usa as linhas de suspeita e desconhecido de `12-conhecimento.csv` nem a coluna `nunca_diz` de `12-voz.csv`; relações com certeza ASSUMINDO (medos e algumas dívidas e tensões) foram excluídas. A coluna `camada_max` é **definitiva** desde o R5-6.2 (`14-camadas-de-informacao.md`): `local` para tudo, exceto os 10 relógios, `secreto` (uso de Mestre e motor); a separação por fato está em `14-camadas-de-informacao.csv`.
- **Termos de fronteira:** nenhum resumo cita Aeltharyn, as Coroas, o Vale nem mistérios de outros reinos; "quem chega do Véu" aparece só no texto de tensão de Meiovau e das duas facções de acolhida, como já escrito no doc 04 (só o lado de cá).
- Capacidade e limite das pessoas vêm de `12-fichas.csv` e `12-competencias.csv`; sem ficha, só o papel escrito.

## Cidades

- **`city.campoalvo` — Campoalvo — A Capital do Trigal.** Campoalvo — A Capital do Trigal. Capital de coroa, cidade de planície (regime fixo). Liderança: Helion Vigília-do-Celeiro. Obrigação: Guardar o excedente, medir o grão e manter o aterro e os canais. Interesse: Manter a precedência do Trigal e o preço do grão. Tensão: O Trigal exige precedência; quem compra o grão questiona o preço. Povos: Humano; Pequenino; Luminar. Porte: grande (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Vaudouro (documentada sem duração); Torrefronte (sugerida por dependência); Meiovau (documentada sem duração).
- **`city.vaudouro` — Vaudouro — O Mercado dos Rios.** Vaudouro — O Mercado dos Rios. Cidade fluvial de mercado (regime fixo). Liderança: Ebbo Praia-Rasa. Obrigação: Manter docas, canais e comportas; tributar a passagem para o Trigal. Interesse: Lucrar com a passagem entre os dois rios. Tensão: Trigal e Cais discutem quem taxa o que passa; os barqueiros dizem que a cidade é do rio. Povos: Humano; Pequenino; Nascido da Maré. Porte: média (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Campoalvo (documentada sem duração); costa sul (pelo Corre-Largo) (documentada sem duração).
- **`city.torrefronte` — Torrefronte — A Guarda do Limite.** Torrefronte — A Guarda do Limite. Fortaleza de limite (regime fixo). Liderança: Solenne Vigília-Longa. Obrigação: Vigiar o limite e registrar quem chega e quem parte. Interesse: Manter o limite e o registro sob a própria coroa. Tensão: Guarnição e vigia discordam sobre quem responde por quem chega. Povos: Humano (das fronteiras); Luminar. Porte: pequena (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Campoalvo (sugerida por dependência).
- **`city.alvamar` — Alvamar — A Capital do Cais.** Alvamar — A Capital do Cais. Capital de coroa, porto sobre o Mar Austral (regime fixo). Liderança: Pelmo Balança-de-Porto e Dorgan Escudo-Lento. Obrigação: Receber a carga, taxar o mar e manter molhe e corrente. Interesse: Manter a precedência do Cais e a entrada pelo mar. Tensão: O Cais exige precedência; a Alfândega discute se a taxa serve ao porto ou à Coroa. Povos: Humano; Abissal Costeiro; Sereiano; Nascido da Maré; Tartarino. Porte: grande (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Enseada-Vigia (sugerida por dependência); Concharia (sugerida por dependência); Meiovau (documentada sem duração); rota das Costas (documentada sem duração).
- **`city.enseada-vigia` — Enseada-Vigia — O Porto das Costas.** Enseada-Vigia — O Porto das Costas. Porto de rota marítima (regime fixo). Liderança: Perrin Farol-Aceso. Obrigação: Manter as luzes e receber quem chega pela rota das Costas. Interesse: Manter a barra aberta e a voz dos faroleiros. Tensão: O Cais quer os faroleiros a seu mando e o Vento quer os sinais da costa. Povos: Humano; Sereiano; Tartarino; Abissal Costeiro. Porte: média (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Alvamar (sugerida por dependência); rota das Costas (documentada sem duração).
- **`city.concharia` — Concharia — O Estaleiro dos Recifes.** Concharia — O Estaleiro dos Recifes. Estaleiro e santuário costeiro (regime fixo). Liderança: desconhecida. Obrigação: Construir e consertar casco; guardar o santuário e o jardim. Interesse: Manter o estaleiro e o santuário. Tensão: O Cais quer abrir outro canal; o santuário diz que o jardim morre. Povos: Tartarino; Sereiano; Humano; Nascido da Maré. Porte: pequena (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Alvamar (sugerida por dependência).
- **`city.torreclara` — Torreclara — A Capital do Vento.** Torreclara — A Capital do Vento. Capital de coroa, cidade de torres de sinais (regime fixo). Liderança: Casimar Código-Limpo. Obrigação: Manter a rede de torres e os códigos; vender aviso e hora certa. Interesse: Manter a precedência do Vento e o alcance dos sinais. Tensão: As outras coroas temem depender de quem pode calar uma torre. Povos: Nascido do Vento; Estelar; Humano; Luminar. Porte: média (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Pouso-das-Brisas (documentada sem duração); Estrelado (sugerida por dependência); Meiovau (documentada sem duração).
- **`city.estrelado` — Estrelado — O Observatório.** Estrelado — O Observatório. Observatório e arquivo celeste (regime fixo). Liderança: desconhecida. Obrigação: Guardar o arquivo celeste, o calendário de rota e o ensino. Interesse: Manter a autoridade sobre o que o céu diz. Tensão: Estelares e luminares discordam sobre quem interpreta o céu; cada coroa pede a previsão que lhe serve. Povos: Estelar; Luminar; Humano; Nascido do Vento. Porte: pequena (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Torreclara (sugerida por dependência).
- **`city.pouso-das-brisas` — Pouso-das-Brisas — A Estação dos Sinais.** Pouso-das-Brisas — A Estação dos Sinais. Estação de torres e pouso de rota (regime fixo). Liderança: Hollis Torre-do-Meio. Obrigação: Manter as torres e os pousos entre o interior e a costa. Interesse: Manter a linha de sinais e o pouso. Tensão: As outras coroas desconfiam de que a linha serve só ao Vento. Povos: Nascido do Vento; Humano; Pequenino (viajante). Porte: pequena (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Torreclara (documentada sem duração); litoral (linha de torres) (documentada sem duração).
- **`city.meiovau` — Meiovau — A Cidade das Três Chaves.** Meiovau — A Cidade das Três Chaves. Cidade livre, ponto de encontro das coroas (regime fixo). Liderança: Tolla Mesa-Posta. Obrigação: Guardar as três chaves, arbitrar e acolher quem chega. Interesse: Manter a mesa neutra e útil às três. Tensão: Como acolher quem chega do Véu; precedência entre coroas. Povos: Humano; Pequenino; Luminar; Nascido da Maré; Nascido do Vento. Porte: média (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Campoalvo (documentada sem duração); Alvamar (documentada sem duração); Torreclara (documentada sem duração); chegados do Véu (documentada sem duração).

## Facções

- **`faction.conselho-dos-celeiros` — Conselho dos Celeiros.** Conselho dos Celeiros. Facção de Campoalvo. Líder: Helion Vigília-do-Celeiro. Objetivo: manter os celeiros comuns e o preço do pão. Métodos: medida, guarda do excedente. Tensão: O Trigal exige precedência; quem compra o grão questiona o preço (doc 03).  
  *Vizinhança:* Aliados: Casa dos Barqueiros; Guarnição do Limite; Mesa Estreita. Rivais: Alfândega das Marés.
- **`faction.guarnicao-do-limite` — Guarnição do Limite.** Guarnição do Limite. Facção de Torrefronte. Líder: Solenne Vigília-Longa. Objetivo: limite vigiado e registro lido de um só modo. Métodos: guarda, Livro dos Chegados. Tensão: Guarnição e vigia discordam sobre quem responde por quem chega (doc 03).  
  *Vizinhança:* Aliados: Conselho dos Celeiros; Vigília da Cúpula; Mesa Estreita. Rivais: Posto dos Pipeiros.
- **`faction.casa-dos-barqueiros` — Casa dos Barqueiros.** Casa dos Barqueiros. Facção de Vaudouro. Líder: Ebbo Praia-Rasa. Objetivo: docas, canais e comportas na mão dos barqueiros. Métodos: comporta, aluguel de canal. Tensão: Trigal e Cais discutem quem taxa o que passa; os barqueiros dizem que a cidade é do rio (doc 03).  
  *Vizinhança:* Aliados: Conselho dos Celeiros; Albergue dos Chegados. Rivais: Alfândega das Marés.
- **`faction.alfandega-das-mares` — Alfândega das Marés.** Alfândega das Marés. Facção de Alvamar. Líder: Pelmo Balança-de-Porto. Objetivo: taxa do porto a serviço do porto, não só da Coroa. Métodos: pesagem, registro de carga. Tensão: O Cais exige precedência; a Alfândega discute se a taxa serve ao porto ou à Coroa (doc 03).  
  *Vizinhança:* Aliados: Construtores de Concha. Rivais: Conselho dos Celeiros; Casa dos Barqueiros; Guarda da Corrente.
- **`faction.guarda-da-corrente` — Guarda da Corrente.** Guarda da Corrente. Facção de Alvamar. Líder: Dorgan Escudo-Lento. Objetivo: baía fechada a quem não deve entrar e aberta a quem precisa. Métodos: molhe, corrente, turnos. Tensão: O Cais exige precedência; a Alfândega discute se a taxa serve ao porto ou à Coroa (doc 03).  
  *Vizinhança:* Aliados: Irmandade dos Faroleiros. Rivais: Alfândega das Marés.
- **`faction.irmandade-dos-faroleiros` — Irmandade dos Faroleiros.** Irmandade dos Faroleiros. Facção de Enseada-Vigia. Líder: Perrin Farol-Aceso. Objetivo: luzes da costa e voz de quem vive de barco. Métodos: farol, aviso de barra. Tensão: O Cais quer os faroleiros a seu mando e o Vento quer os sinais da costa (doc 03).  
  *Vizinhança:* Aliados: Guarda da Corrente; Pilotos da Enseada; Albergue dos Chegados. Rivais: Casa dos Sinais.
- **`faction.pilotos-da-enseada` — Pilotos da Enseada.** Pilotos da Enseada. Facção de Enseada-Vigia. Líder: Nerella Canto-da-Barra. Objetivo: a barra pilotada por ofício, não por coroa. Métodos: piloto, ensino da barra. Tensão: O Cais quer os faroleiros a seu mando e o Vento quer os sinais da costa (doc 03).  
  *Vizinhança:* Aliados: Irmandade dos Faroleiros; Guardas do Santuário. Rivais: desconhecido.
- **`faction.construtores-de-concha` — Construtores de Concha.** Construtores de Concha. Facção de Concharia. Líder: Orrume Abrigo-de-Casco. Objetivo: estaleiro com canal maior para navios maiores. Métodos: casco, vela, canal. Tensão: O Cais quer abrir outro canal; o santuário diz que o jardim morre (doc 03).  
  *Vizinhança:* Aliados: Alfândega das Marés. Rivais: Guardas do Santuário.
- **`faction.guardas-do-santuario` — Guardas do Santuário.** Guardas do Santuário. Facção de Concharia. Líder: Corali Fundo-de-Coral. Objetivo: jardim e santuário em paz. Métodos: guarda, recusa de canal. Tensão: O Cais quer abrir outro canal; o santuário diz que o jardim morre (doc 03).  
  *Vizinhança:* Aliados: Pilotos da Enseada. Rivais: Construtores de Concha.
- **`faction.casa-dos-sinais` — Casa dos Sinais.** Casa dos Sinais. Facção de Torreclara, Enseada-Vigia. Líder: Casimar Código-Limpo. Objetivo: rede de torres inteira e códigos guardados. Métodos: código, aviso, hora certa. Tensão: As outras coroas temem depender de quem pode calar uma torre (doc 03).  
  *Vizinhança:* Aliados: Posto dos Pipeiros; Leitores do Céu. Rivais: Irmandade dos Faroleiros.
- **`faction.leitores-do-ceu` — Leitores do Céu.** Leitores do Céu. Facção de Estrelado. Líder: Ixra Noite-Aberta. Objetivo: leitura do céu pelos estelares. Métodos: mapa, órbita, previsão. Tensão: Estelares e luminares discordam sobre quem interpreta o céu; cada coroa pede a previsão que lhe serve (doc 03).  
  *Vizinhança:* Aliados: Casa dos Sinais. Rivais: Vigília da Cúpula.
- **`faction.vigilia-da-cupula` — Vigília da Cúpula.** Vigília da Cúpula. Facção de Estrelado. Líder: Eloh Cúpula-Acesa. Objetivo: leitura do céu pelo voto da vigília. Métodos: cúpula, arquivo, calendário de rota. Tensão: Estelares e luminares discordam sobre quem interpreta o céu; cada coroa pede a previsão que lhe serve (doc 03).  
  *Vizinhança:* Aliados: Guarnição do Limite. Rivais: Leitores do Céu.
- **`faction.posto-dos-pipeiros` — Posto dos Pipeiros.** Posto dos Pipeiros. Facção de Pouso-das-Brisas, Torrefronte. Líder: Hollis Torre-do-Meio. Objetivo: linha de torres e pousos entre o interior e a costa. Métodos: torre, pipa, pouso. Tensão: As outras coroas desconfiam de que a linha serve só ao Vento (doc 03).  
  *Vizinhança:* Aliados: Casa dos Sinais. Rivais: Guarnição do Limite.
- **`faction.albergue-dos-chegados` — Albergue dos Chegados.** Albergue dos Chegados. Facção de Meiovau. Líder: Auriven Porta-Acesa. Objetivo: acolhida larga a quem chega. Métodos: hospedagem, doação. Tensão: Como acolher quem chega do Véu; precedência entre coroas (doc 03).  
  *Vizinhança:* Aliados: Casa dos Barqueiros; Irmandade dos Faroleiros. Rivais: Mesa Estreita.
- **`faction.mesa-estreita` — Mesa Estreita.** Mesa Estreita. Facção de Meiovau. Líder: Ruvane Chave-Contada. Objetivo: registro e prazo antes de qualquer sustento. Métodos: contagem, pedido na Mesa. Tensão: Como acolher quem chega do Véu; precedência entre coroas (doc 03).  
  *Vizinhança:* Aliados: Conselho dos Celeiros; Guarnição do Limite. Rivais: Albergue dos Chegados.

## Relógios

- **`clock.medida-em-ano-ruim` — CA-01 A medida do pão.** CA-01 A medida do pão (Campoalvo); progresso 2/6. Move: colheita fraca observada; pedido de abertura de celeiro recusado; preço do pão contestado em dia de pesagem. Trava: sessão do Conselho dos Celeiros que reconhece a medida; acordo no Salão das Medidas; Tomlin intermediando. Decisor no segmento 5: governante do Trigal, com o Conselho dos Celeiros ouvido. Inação: só avança com novo preço contestado ou novo pedido de abertura observados.  
  *Vizinhança:* Lugar: Campoalvo. Entidades: Odile Medida-Cheia; Tomlin Horta-Larga; Conselho dos Celeiros.
- **`clock.passagem-dos-rios` — CA-02 Duas taxas, uma barcaça.** CA-02 Duas taxas, uma barcaça (Vaudouro); progresso 3/6. Move: barcaça cobrada por duas coroas; Casa dos Barqueiros lucrando com a dúvida; comporta fechada sem aviso a uma das coroas. Trava: aviso às duas coroas antes de fechar comporta (L-02); apelo ao governante do Trigal; Garvane liberando barcaça sem taxa. Decisor no segmento 5: Mesa de Meiovau, a pedido das coroas (doc 04 §3). Inação: só avança com nova cobrança dupla ou nova comporta fechada sem aviso observadas.  
  *Vizinhança:* Lugar: Vaudouro. Entidades: Garvane Remo-Firme; Casa dos Barqueiros; Alfândega das Marés; Conselho dos Celeiros.
- **`clock.livro-lido-de-dois-modos` — CA-03 O registro lido de dois modos.** CA-03 O registro lido de dois modos (Torrefronte); progresso 2/6. Move: chegado registrado com leitura contestada entre guarnição e vigia; Solenne abrindo o limite em nome do Livro. Trava: revisão das páginas por Solenne com testemunha; acordo entre a guarnição e o Posto dos Pipeiros. Decisor no segmento 5: Mesa de Meiovau, a pedido das duas coroas (doc 04 §3). Inação: só avança com novo registro contestado observado.  
  *Vizinhança:* Lugar: Torrefronte. Entidades: Solenne Vigília-Longa; Guarnição do Limite; Posto dos Pipeiros.
- **`clock.taxa-do-molhe` — CA-04 A taxa do molhe.** CA-04 A taxa do molhe (Alvamar); progresso 3/6. Move: nova descarga fora do molhe observada; Alfândega contestando a taxa em público; corrente frouxa observada. Trava: Guarda da Corrente registrando cada entrada; acordo entre a Alfândega e o governante do Cais; Rilla calando ou falando do navio. Decisor no segmento 5: governante do Cais, com a Alfândega das Marés ouvida. Inação: só avança com nova descarga fora do molhe ou nova contestação da taxa observadas.  
  *Vizinhança:* Lugar: Alvamar. Entidades: Corlan Cais-Primeiro; Dorgan Escudo-Lento; Rilla Espuma-Fina; Alfândega das Marés; Guarda da Corrente.
- **`clock.luz-entre-coroas` — CA-05 A luz entre duas coroas.** CA-05 A luz entre duas coroas (Enseada-Vigia); progresso 2/6. Move: ordem do Cais aos faroleiros contestada; pedido do Vento por sinais da costa; pilotos sem luz em noite de barra. Trava: Irmandade dos Faroleiros mantendo a luz acesa; Pilotos da Enseada guiando sem tomar partido; Nerella. Decisor no segmento 5: Mesa de Meiovau, a pedido do Cais e do Vento. Inação: só avança com nova ordem contestada ou novo pedido de sinal observados.  
  *Vizinhança:* Lugar: Enseada-Vigia. Entidades: Nerella Canto-da-Barra; Irmandade dos Faroleiros; Pilotos da Enseada; Casa dos Sinais.
- **`clock.canal-e-jardim` — CA-06 O canal e o jardim.** CA-06 O canal e o jardim (Concharia); progresso 2/6. Move: pedido do Cais para abrir o canal; recusa dos Guardas do Santuário; coral novo observado no traçado. Trava: acordo entre Construtores de Concha e Guardas do Santuário; obra adiada; Alfândega pedindo prazo. Decisor no segmento 5: governante do Cais, com os dois lados ouvidos. Inação: só avança com novo pedido de obra ou nova recusa observados.  
  *Vizinhança:* Lugar: Concharia. Entidades: Construtores de Concha; Guardas do Santuário; Alfândega das Marés.
- **`clock.codigo-sem-aviso` — CA-07 O código sem aviso.** CA-07 O código sem aviso (Torreclara); progresso 3/6. Move: nova mudança de código sem aviso observada; Trigal e Cais exigindo os códigos; torre calada em hora de aviso. Trava: Casimar registrando cada mudança; aviso pela Casa dos Sinais; Brevo dividindo o código-mestre. Decisor no segmento 5: governante do Vento, com a Casa dos Sinais ouvida. Inação: só avança com nova mudança sem aviso ou nova exigência de código observadas.  
  *Vizinhança:* Lugar: Torreclara. Entidades: Brevo Rumo-Alto; Astrel Noite-Clara; Casa dos Sinais; Posto dos Pipeiros.
- **`clock.leitura-do-ceu` — CA-08 Duas leituras do mesmo céu.** CA-08 Duas leituras do mesmo céu (Estrelado); progresso 2/6. Move: duas previsões diferentes entregues a coroas diferentes; Estelares e Luminares discordando em público. Trava: calendário de rota mantido; sessão conjunta no Pátio das Rotas; Eloh e Ixra citando as duas. Decisor no segmento 5: Mesa de Meiovau, a pedido das coroas. Inação: só avança com nova previsão divergente observada.  
  *Vizinhança:* Lugar: Estrelado. Entidades: Astrel Noite-Clara; Leitores do Céu; Vigília da Cúpula.
- **`clock.sinais-proprios` — CA-09 O sinal que cada coroa quer.** CA-09 O sinal que cada coroa quer (Pouso-das-Brisas); progresso 2/6. Move: pedido de Trigal ou Cais por sinal próprio; recusa do Posto dos Pipeiros; linha de torres calada para uma coroa. Trava: Casa dos Sinais atendendo o pedido; Hollis ensinando o fio das pipas; acordo sobre horário de aviso. Decisor no segmento 5: governante do Vento, com a Mesa de Meiovau se uma coroa apelar. Inação: só avança com novo pedido de sinal próprio ou nova recusa observados.  
  *Vizinhança:* Lugar: Pouso-das-Brisas. Entidades: Brevo Rumo-Alto; Posto dos Pipeiros; Casa dos Sinais.
- **`clock.acolhida-de-meiovau` — CA-10 Prazo e sustento do chegado.** CA-10 Prazo e sustento do chegado (Meiovau); progresso 3/6. Move: chegado novo sem registro; Mesa Estreita pedindo prazo antes do sustento; Albergue pedindo acolhida larga. Trava: Costume da Porta (L-10); Tolla ordenando a audiência; doação do Albergue. Decisor no segmento 5: Mesa de Meiovau, com as três chaves (L-09). Inação: só avança com novo chegado contestado ou novo pedido de prazo observados.  
  *Vizinhança:* Lugar: Meiovau. Entidades: Tolla Mesa-Posta; Auriven Porta-Acesa; Albergue dos Chegados; Mesa Estreita.

## Pessoas

- **`npc.odile-medida-cheia` — Odile Medida-Cheia.** Odile Medida-Cheia, Humano. Governante da Coroa do Trigal; despacha no Salão das Medidas. Limite: Governa o Trigal e fixa as medidas; não manda no Cais, no Vento nem em Meiovau. Pressão: O Cais diz que o excedente apodrece sem o mar; o Vento diz que ela só sabe da colheita depois do aviso; o herdeiro já fala em ceder.  
  *Vizinhança:* Lugar: Campoalvo. Relações: vínculo de cargo: Tomlin Horta-Larga.
- **`npc.corlan-cais-primeiro` — Corlan Cais-Primeiro.** Corlan Cais-Primeiro, Abissal Costeiro. Governante da Coroa do Cais; despacha na Casa do Cais. Limite: Governa o Cais e comanda o porto; não taxa o Trigal em Vaudouro sem acordo e não manda no Vento. Pressão: A Alfândega das Marés cobra contas; o Trigal e o Vento a acusam de tratar o mar como propriedade; o estaleiro pede mais um canal.  
  *Vizinhança:* Lugar: Alvamar. Relações: vínculo de cargo: Rilla Espuma-Fina.
- **`npc.brevo-rumo-alto` — Brevo Rumo-Alto.** Brevo Rumo-Alto, Nascido do Vento. Governante da Coroa do Vento; despacha na Torre-Mestra. Limite: Governa os sinais e as torres do Vento; não comanda guarnição nem molhe e não decide o preço do grão ou da taxa. Pressão: As outras coroas temem depender de quem pode calar uma torre; o Cais e o Trigal disputam torres e faróis; uma cerração longa deixaria a Coroa cega.  
  *Vizinhança:* Lugar: Torreclara. Relações: vínculo de cargo: Astrel Noite-Clara.
- **`npc.tomlin-horta-larga` — Tomlin Horta-Larga.** Tomlin Horta-Larga, Pequenino. Herdeiro reconhecido da Coroa do Trigal. Limite: Aprende e representa o Trigal onde a governante manda; não governa nem assina medidas enquanto o cargo estiver ocupado. Pressão: Os celeiros esperam que ele mantenha a precedência; Odile o acha mole demais; as outras coroas o testam em Meiovau.  
  *Vizinhança:* Lugar: Campoalvo. Relações: nenhuma documentada.
- **`npc.rilla-espuma-fina` — Rilla Espuma-Fina.** Rilla Espuma-Fina, Nascido da Maré. Herdeira reconhecida da Coroa do Cais. Limite: Representa o Cais em Meiovau e no porto onde Corlan manda; não cobra taxa nem decide o canal. Pressão: A Alfândega a cortejou; Corlan a mantém longe das contas; o estaleiro e o santuário querem o apoio dela.  
  *Vizinhança:* Lugar: Alvamar. Relações: nenhuma documentada.
- **`npc.astrel-noite-clara` — Astrel Noite-Clara.** Astrel Noite-Clara, Estelar. Herdeiro reconhecido da Coroa do Vento; liga a Torre-Mestra à Mesa dos Astros. Limite: Aprende os códigos e a leitura do céu; não decide a previsão e não altera código de torre. Pressão: Estelares e luminares o querem do lado deles em Estrelado; Brevo o vê como ligação demais ao observatório.  
  *Vizinhança:* Lugar: Torreclara. Relações: nenhuma documentada.
- **`npc.solenne-vigilia-longa` — Solenne Vigília-Longa.** Solenne Vigília-Longa, Luminar. Comandante da guarnição do Trigal; guardiã de Torrefronte. Líder de Guarnição do Limite (objetivo: limite vigiado e registro lido de um só modo). Limite: Comanda só a guarnição do Trigal; não decide quem passa nem responde pelo registro sem o Vento. Pressão: O Vento reivindica a torre; o registro tem páginas que ninguém explica; o suprimento vem de dentro e atrasa em ano de cheia.  
  *Vizinhança:* Lugar: Torrefronte. Relações: rivalidade: Posto dos Pipeiros; aliança: Conselho dos Celeiros; aliança: Vigília da Cúpula.
- **`npc.dorgan-escudo-lento` — Dorgan Escudo-Lento.** Dorgan Escudo-Lento, Tartarino. Comandante do molhe e da Corrente da Boca; guardião de Alvamar. Líder de Guarda da Corrente (objetivo: baía fechada a quem não deve entrar e aberta a quem precisa). Limite: Guarda o molhe e a corrente; não taxa, não decide quem governa o porto e não comanda fora da baía. Pressão: A corrente já falhou uma vez e ninguém quis dizer quando; os navios que descarregam fora do molhe lhe fogem do controle; a Coroa quer taxa e o porto quer passagem.  
  *Vizinhança:* Lugar: Alvamar. Relações: rivalidade: Alfândega das Marés; aliança: Irmandade dos Faroleiros; vínculo de cargo: Corlan Cais-Primeiro.
- **`npc.nerella-canto-da-barra` — Nerella Canto-da-Barra.** Nerella Canto-da-Barra, Sereiano. Piloto da Barra; herói de Enseada-Vigia. Líder de Pilotos da Enseada (objetivo: a barra pilotada por ofício, não por coroa). Limite: Guia e ensina a barra; não decide quem recebe o farol e não representa os faroleiros. Pressão: O Cais quer os pilotos a seu mando, o Vento quer os sinais da costa; os recém-chegados pedem a ela o caminho que só os antigos sabem.  
  *Vizinhança:* Lugar: Enseada-Vigia. Relações: aliança: Irmandade dos Faroleiros; aliança: Guardas do Santuário.
- **`npc.garvane-remo-firme` — Garvane Remo-Firme.** Garvane Remo-Firme, Humano. Barqueiro-chefe das barcaças de Vaudouro; herói de Vaudouro. Limite: Conduz barcaças e fala pelos barqueiros da cheia; não manda nas comportas nem na Casa dos Barqueiros. Pressão: O Trigal e o Cais pedem que ele escolha a taxa a que obedece; a Casa dos Barqueiros o quer como símbolo e ele não quer ser.  
  *Vizinhança:* Lugar: Vaudouro. Relações: nenhuma documentada.
- **`npc.tolla-mesa-posta` — Tolla Mesa-Posta.** Tolla Mesa-Posta, Pequenino. Guardiã das Três Chaves; preside a Mesa de Meiovau. Limite: Guarda as chaves e preside a Mesa; não abre nada sem as três e não vota pelas coroas. Pressão: As coroas pedem que ela decida o tratamento de quem chega do Véu e quem tem precedência; uma chave pode ter sido copiada.  
  *Vizinhança:* Lugar: Meiovau. Relações: vínculo de cargo: Auriven Porta-Acesa.
- **`npc.auriven-porta-acesa` — Auriven Porta-Acesa.** Auriven Porta-Acesa, Luminar. Benfeitor de Meiovau; mantém o Albergue dos Chegados, que recebe quem chega a pé e de barca. Líder de Albergue dos Chegados (objetivo: acolhida larga a quem chega). Limite: Hospeda; não decide o tratamento de ninguém, não pergunta o motivo da chegada e não fala pela Mesa. Pressão: As coroas o veem como voto a favor de quem chega; o albergue pede mais do que o doador dá.  
  *Vizinhança:* Lugar: Meiovau. Relações: rivalidade: Mesa Estreita; aliança: Casa dos Barqueiros; aliança: Irmandade dos Faroleiros.
- **`npc.wendra-aterro-primeiro` — Wendra Aterro-Primeiro.** Wendra Aterro-Primeiro, Humano, falecido(a). Herói lendário: figura da lenda de Campoalvo; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Campoalvo. Relações: nenhuma documentada.
- **`npc.helion-vigilia-do-celeiro` — Helion Vigília-do-Celeiro.** Helion Vigília-do-Celeiro, Luminar. Guardião de Campoalvo. Líder de Conselho dos Celeiros (objetivo: manter os celeiros comuns e o preço do pão). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Campoalvo. Relações: rivalidade: Alfândega das Marés; aliança: Casa dos Barqueiros; aliança: Guarnição do Limite.
- **`npc.dodda-prato-cheio` — Dodda Prato-Cheio.** Dodda Prato-Cheio, Pequenino. Benfeitor de Campoalvo. Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Campoalvo. Relações: nenhuma documentada.
- **`npc.ebbo-praia-rasa` — Ebbo Praia-Rasa.** Ebbo Praia-Rasa, Nascido da Maré. Guardião de Vaudouro. Líder de Casa dos Barqueiros (objetivo: docas, canais e comportas na mão dos barqueiros). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Vaudouro. Relações: rivalidade: Alfândega das Marés; aliança: Conselho dos Celeiros; aliança: Albergue dos Chegados.
- **`npc.larkin-barcaca-cheia` — Larkin Barcaça-Cheia.** Larkin Barcaça-Cheia, Pequenino. Benfeitor de Vaudouro. Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Vaudouro. Relações: nenhuma documentada.
- **`npc.halvard-limite-firme` — Halvard Limite-Firme.** Halvard Limite-Firme, Humano, falecido(a). Herói lendário: figura da lenda de Torrefronte; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Torrefronte. Relações: nenhuma documentada.
- **`npc.sabeth-patio-aberto` — Sabeth Pátio-Aberto.** Sabeth Pátio-Aberto, Humano. Benfeitor de Torrefronte. Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Torrefronte. Relações: nenhuma documentada.
- **`npc.sundar-licenca-de-cais` — Sundar Licença-de-Cais.** Sundar Licença-de-Cais, Abissal Costeiro, falecido(a). Herói lendário: figura da lenda de Alvamar; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Alvamar. Relações: nenhuma documentada.
- **`npc.marnu-espuma-dada` — Marnu Espuma-Dada.** Marnu Espuma-Dada, Nascido da Maré. Benfeitor de Alvamar. Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Alvamar. Relações: nenhuma documentada.
- **`npc.perrin-farol-aceso` — Perrin Farol-Aceso.** Perrin Farol-Aceso, Humano. Guardião de Enseada-Vigia. Líder de Irmandade dos Faroleiros (objetivo: luzes da costa e voz de quem vive de barco). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Enseada-Vigia. Relações: rivalidade: Casa dos Sinais; aliança: Guarda da Corrente; aliança: Pilotos da Enseada.
- **`npc.ressan-concha-aberta` — Ressan Concha-Aberta.** Ressan Concha-Aberta, Abissal Costeiro. Benfeitor de Enseada-Vigia. Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Enseada-Vigia. Relações: nenhuma documentada.
- **`npc.mossa-casco-primeiro` — Mossa Casco-Primeiro.** Mossa Casco-Primeiro, Tartarino, falecido(a). Herói lendário: figura da lenda de Concharia; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Concharia. Relações: nenhuma documentada.
- **`npc.orrume-abrigo-de-casco` — Orrume Abrigo-de-Casco.** Orrume Abrigo-de-Casco, Tartarino. Guardião de Concharia. Líder de Construtores de Concha (objetivo: estaleiro com canal maior para navios maiores). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Concharia. Relações: rivalidade: Guardas do Santuário; aliança: Alfândega das Marés; vínculo de cargo: Corali Fundo-de-Coral.
- **`npc.corali-fundo-de-coral` — Corali Fundo-de-Coral.** Corali Fundo-de-Coral, Sereiano. Benfeitor de Concharia. Líder de Guardas do Santuário (objetivo: jardim e santuário em paz). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Concharia. Relações: rivalidade: Construtores de Concha; aliança: Pilotos da Enseada.
- **`npc.raffe-sopro-primeiro` — Raffe Sopro-Primeiro.** Raffe Sopro-Primeiro, Nascido do Vento, falecido(a). Herói lendário: figura da lenda de Torreclara; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Torreclara. Relações: nenhuma documentada.
- **`npc.casimar-codigo-limpo` — Casimar Código-Limpo.** Casimar Código-Limpo, Humano. Guardião de Torreclara. Líder de Casa dos Sinais (objetivo: rede de torres inteira e códigos guardados). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Torreclara. Relações: rivalidade: Irmandade dos Faroleiros; aliança: Posto dos Pipeiros; aliança: Leitores do Céu.
- **`npc.clarel-lume-de-torre` — Clarel Lume-de-Torre.** Clarel Lume-de-Torre, Luminar. Benfeitor de Torreclara. Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Torreclara. Relações: nenhuma documentada.
- **`npc.orbane-rota-perdida` — Orbane Rota-Perdida.** Orbane Rota-Perdida, Estelar, falecido(a). Herói lendário: figura da lenda de Estrelado; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Estrelado. Relações: nenhuma documentada.
- **`npc.eloh-cupula-acesa` — Eloh Cúpula-Acesa.** Eloh Cúpula-Acesa, Luminar. Guardião de Estrelado. Líder de Vigília da Cúpula (objetivo: leitura do céu pelo voto da vigília). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Estrelado. Relações: rivalidade: Leitores do Céu; aliança: Guarnição do Limite; vínculo de cargo: Ixra Noite-Aberta.
- **`npc.ixra-noite-aberta` — Ixra Noite-Aberta.** Ixra Noite-Aberta, Estelar. Benfeitor de Estrelado. Líder de Leitores do Céu (objetivo: leitura do céu pelos estelares). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Estrelado. Relações: rivalidade: Vigília da Cúpula; aliança: Casa dos Sinais.
- **`npc.zefir-pipa-caida` — Zefir Pipa-Caída.** Zefir Pipa-Caída, Nascido do Vento, falecido(a). Herói lendário: figura da lenda de Pouso-das-Brisas; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Pouso-das-Brisas. Relações: nenhuma documentada.
- **`npc.hollis-torre-do-meio` — Hollis Torre-do-Meio.** Hollis Torre-do-Meio, Humano. Guardião de Pouso-das-Brisas. Líder de Posto dos Pipeiros (objetivo: linha de torres e pousos entre o interior e a costa). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Pouso-das-Brisas. Relações: rivalidade: Guarnição do Limite; aliança: Casa dos Sinais; vínculo de cargo: Brevo Rumo-Alto.
- **`npc.poppel-mesa-de-viajante` — Poppel Mesa-de-Viajante.** Poppel Mesa-de-Viajante, Pequenino. Benfeitor de Pouso-das-Brisas. Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Pouso-das-Brisas. Relações: nenhuma documentada.
- **`npc.ostrel-chave-primeira` — Ostrel Chave-Primeira.** Ostrel Chave-Primeira, Humano, falecido(a). Herói lendário: figura da lenda de Meiovau; lenda não é fato. Limite: não age no início; o que de fato aconteceu é desconhecido.  
  *Vizinhança:* Lugar: Meiovau. Relações: nenhuma documentada.
- **`npc.pelmo-balanca-de-porto` — Pelmo Balança-de-Porto.** Pelmo Balança-de-Porto, Humano. Líder de Alfândega das Marés (objetivo: taxa do porto a serviço do porto, não só da Coroa). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Alvamar. Relações: rivalidade: Conselho dos Celeiros; rivalidade: Casa dos Barqueiros; rivalidade: Guarda da Corrente.
- **`npc.ruvane-chave-contada` — Ruvane Chave-Contada.** Ruvane Chave-Contada, Humano. Líder de Mesa Estreita (objetivo: registro e prazo antes de qualquer sustento). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.  
  *Vizinhança:* Lugar: Meiovau. Relações: rivalidade: Albergue dos Chegados; aliança: Conselho dos Celeiros; aliança: Guarnição do Limite.

## Conferências

| Verificação | Resultado |
|---|---|
| Entidades com resumo e ID | 73 de 73 (63 do registro mestre mais 10 relógios) |
| Entidades do registro mestre sem resumo | 0 |
| Resumo acima de 500 caracteres | 0 (maior: 476) |
| Vizinhança acima de 300 caracteres | 0 (maior: 181) |
| Resumo que usa suspeita, desconhecido de `12-conhecimento.csv` ou `nunca_diz` | 0 |
| Duração de rota, número de população ou termo de fronteira | 0 |
| Campo vazio | 0 |

## Decisão pendente

**D-166:** aprovar os 73 resumos e a vizinhança. Opções: **A.** aprovar; **B.** trocar o texto de uma entidade (diga qual). **Recomendação: A.**
