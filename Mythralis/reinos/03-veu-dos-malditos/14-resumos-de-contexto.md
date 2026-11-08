# 14 — Resumos de contexto (R3-6.1)

**Estado:** rascunho de R3-6.1 para aprovação do autor (D-92) · **Data:** 2026-11-06

**Etiqueta:** tudo é **[PROPOSTO]**; resumo é material de consulta para a IA, não texto de lore: não altera fato nem estado. **Fonte:** doc 01 §2 e §3, doc 04 §8 e §9, doc 05 §2, doc 06, doc 07; `12-registro-mestre.csv`, `12-assentamentos.csv`, `12-facoes.csv`, `12-relogios.csv`, `12-competencias.csv`, `12-fichas.csv`, `12-relacoes.csv`, `12-demografia.csv`. Dados em `14-resumos-de-contexto.csv`. Molde: `reinos/02-.../14-resumos-de-contexto.md`.

## Regras

- **57 entidades, 1 resumo cada, com ID:** 8 cidades, 11 facções, 8 relógios e 30 pessoas (12 principais, 7 da tríade sem ficha, 11 heróis lendários). O reino não tem assentamentos (D-66). Cada resumo tem **no máximo 500 caracteres** (maior: 486) e uma linha de **vizinhança** (no máximo 300; maior: 175); a coluna `caracteres` registra o tamanho.
- **Vizinhança:** cidade → ligações do doc 07, todas sem duração; facção → aliados e rivais (doc 04 §9); relógio → localidade e entidades; pessoa → lugar e até 3 relações de certeza C (rivalidade, aliança, vínculo de cargo, dívida, nessa ordem).
- **Só o que o texto escreve:** nenhum campo foi completado por dedução. Classe e profissão propostas não entram. Nenhuma duração de rota nem número de população aparece; população só como faixa de porte relativo (doc 05 §4).
- **Sem segredos nem conhecimento restrito:** o resumo não usa as linhas de suspeita e desconhecido de `12-conhecimento.csv` nem a coluna `nunca_diz` de `12-voz.csv`; relações com certeza ASSUMINDO foram excluídas. A coluna `camada_max` é **definitiva** desde o R3-6.2 (`14-camadas-de-informacao.md`): `local` para tudo, exceto os 8 relógios, `secreto` (uso de Mestre e motor); a separação por fato está em `14-camadas-de-informacao.csv`.
- **Termos de fronteira:** nenhum resumo afirma algo sobre Aeltharyn; as menções são só vetos: "nada se liga a Aeltharyn" (Borda-Mansa) e o limite de Krell (só rumor sobre Aeltharyn; sem ligação com Brumafunda). Nada sobre as Coroas ou as Marcas das Feras.
- Capacidade e limite das pessoas vêm de `12-fichas.csv` e `12-competencias.csv` (só do que os docs 01, 04 e 10 dizem).

## Cidades

- **`city.morroveu` — Morrovéu — A Corte do Rei Vencedor.** Cidade-fortaleza num morro entre charcos. Governo: a Corte, com o Conselho dos Pactuários (um assento por lugar); à frente, Morvan Voz-Baixa. Vive de tributo em pactos, escribas, armas e hospedagem de desafiantes. Tensão: Pactuários divididos entre defender o rei, que trouxe paz, e lembrar que a regra é o rei ser vencido; lista de desafiantes parada há cerca de cem anos. Povos: Nascido do Crepúsculo; Umbral; Ursino Sombrio; Humano (forasteiro). Porte: grande (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): assento de cada um dos sete outros lugares no Conselho; Selovale (Memória das Provações); Fozbreu (Defensores da Paz). Tensão, não rota: Candeia-Morta.
- **`city.lamaluz` — Lamaluz — A Cidade das Lamparinas.** Cidade de charco sobre estacas e passarelas. Governo: Pactuários dos Canais; à frente, Nuvik Canal-Torto. Vive de juncos, peixe de lama e pilotagem. Tensão: Famílias antigas e lagartos de charco querem partir (Fugas do Véu); quem guia os que partem é acusado de quebrar o pacto. Povos: Escamado; Ratoide; Moldável; Escamado Miúdo e Pequenino. Porte: média (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Morrovéu (assento no Conselho); Fozbreu (Pilotos dos Canais; Fugas do Véu partem de Lamaluz).
- **`city.fozbreu` — Fozbreu — O Porto da Foz.** Porto na foz do Argente, sobre o Mar Austral. Governo: Pactuários da Foz; à frente, Rusk Rede-da-Barra. Vive de breu, sal, pesca de barra e cabotagem. Tensão: Carga sem papel que o pacto reconheça; fardos e visitantes de longe que não são moradores. Povos: Escamado; Ratoide; Corvídeo; Tartarino, Goblin e Humano. Porte: média (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Morrovéu (assento; Defensores da Paz); Lamaluz (Pilotos dos Canais); rio acima pelo Argente; cabotagem no Mar Austral.
- **`city.beira-sal` — Beira-Sal — O Posto das Caravanas.** Posto de caravanas na borda do Ermo de Sal. Governo: Pactuários dos Poços; à frente, Teshi Nó-de-Poço. Vive de sal, água, guias e acertos. Tensão: Guias cobram por pactos orais sem prova; caravanas do outro lado contestam a conta. Povos: Ratoide; Metamorfo Lupino; Aracnídeo; Goblin, Vulpino e Humano. Porte: pequena (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Morrovéu (assento no Conselho); Ermo de Sal pela Rota do Sal.
- **`city.selovale` — Selovale — O Vale dos Pactos Velhos.** Vale de pactos antigos, com cidade-arquivo. Governo: Conselho dos Selos; à frente, Yeshe Forma-Emprestada. Vive de arquivo, juramentos, cópia de selos e parecer. Tensão: Um pacto antigo vence e ninguém sabe o que o fim dele concede; discordância sobre qual versão do texto vale. Povos: Nascido do Crepúsculo; Moldável; Ursino Sombrio; Draconato, Feérico e Humano. Porte: pequena (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Morrovéu (assento; Memória das Provações); fica ao norte da capital.
- **`city.borda-mansa` — Borda-Mansa — O Posto da Orla Norte.** Posto de orla com vigia de neblina. Governo: Pactuários da Vigia; à frente, Krell Eco-da-Vigia. Vive de vigia, sinais e couro de charco. Tensão: Gente de longe chega sem guia e sem registro; o posto não sabe quem é quem. Povos: Corvídeo; Umbral; Aracnídeo; Micélioide, Felídeo Errante e Corvário. Porte: pequena (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Morrovéu (assento no Conselho); orla norte do reino; nada se liga a Aeltharyn.
- **`city.fio-longo` — Fio-Longo — A Casa dos Sonhos.** Vila-hospedaria de repouso e sonhos. Governo: Casa dos Fios; à frente, Ondra Fio-de-Sono. Vive de hospedagem de repouso, tecelagem, sonhos e remédios de sono. Tensão: Hóspedes que não querem ir embora; a Casa diz que é escolha, os parentes dizem que não. Povos: Onírico; Moldável; Corvídeo; Feérico, Pequenino e Humano. Porte: pequena (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Morrovéu (assento no Conselho); afastada das trilhas de caravana, sudoeste da capital.
- **`city.candeia-morta` — Candeia-Morta — A Cidade das Vigílias.** Cidade noturna de vigílias e despedidas. Governo: Pactuários das Despedidas; à frente, Dhun Contorno-Quieto. Vive de ofícios de noite, luz fria, guarda de portões e cuidado dos mortos. Tensão: Pactuários divididos sobre o que o pacto pede de um rei necromante, sem dizer em voz alta. Povos: Noctívago; Umbral; Nascido do Crepúsculo; Humano e Nascido da Brasa. Porte: média (relativo, sem número).  
  *Vizinhança:* Ligações (sem duração): Morrovéu (assento no Conselho); ao sul da capital; tensão com a Corte, não rota.

## Facções

- **`faction.guarda-do-rei` — Guarda do Rei.** Facção liderada por Orsk Cicatriz-de-Bando. Objetivo: servir ao rei e manter Morrovéu. Métodos: disciplina, turnos, vigia da Corte. Presente em Morrovéu. Tensão: Pactuários divididos entre defender o rei, que trouxe paz, e lembrar que a regra é o rei ser vencido.  
  *Vizinhança:* Aliados: Defensores da Paz, Turnos da Vigília. Rivais: Memória das Provações.
- **`faction.defensores-da-paz` — Defensores da Paz.** Facção liderada por Rusk Rede-da-Barra. Objetivo: manter o rei que trouxe paz. Métodos: voto no Conselho, pressão sobre os lugares. Presente em Morrovéu, Fozbreu. Tensão: Carga sem papel que o pacto reconheça; fardos e visitantes de longe que não são moradores (doc 03, fozbreu).  
  *Vizinhança:* Aliados: Guarda do Rei, Casa dos Poços, Guardiões das Lamparinas. Rivais: Memória das Provações, Irmandade das Despedidas.
- **`faction.memoria-das-provacoes` — Memória das Provações.** Facção liderada por Yeshe Forma-Emprestada. Objetivo: lembrar que o rei deve ser vencido. Métodos: arquivo, parecer, inscrição de desafiantes. Presente em Morrovéu, Selovale. Tensão: Um pacto antigo vence e ninguém sabe o que o fim dele concede; discordância sobre qual versão do texto vale (doc 03, selovale).  
  *Vizinhança:* Aliados: Irmandade das Despedidas. Rivais: Guarda do Rei, Defensores da Paz.
- **`faction.pilotos-dos-canais` — Pilotos dos Canais.** Facção liderada por Tazk Pedra-ao-Sol. Objetivo: manter os canais navegáveis e guiar quem pede. Métodos: pilotagem, lamparinas, passarelas. Presente em Lamaluz, Fozbreu. Tensão: Famílias antigas e lagartos de charco querem partir (Fugas do Véu).  
  *Vizinhança:* Aliados: Guias de Sal. Rivais: Guardiões das Lamparinas.
- **`faction.guardioes-das-lamparinas` — Guardiões das Lamparinas.** Facção liderada por Nuvik Canal-Torto. Objetivo: manter as famílias de pacto em Lamaluz. Métodos: vigia do pacto, acusação. Presente em Lamaluz. Tensão: Famílias antigas e lagartos de charco querem partir (Fugas do Véu).  
  *Vizinhança:* Aliados: Defensores da Paz. Rivais: Pilotos dos Canais.
- **`faction.guias-de-sal` — Guias de Sal.** Facção liderada por Vurga Sede-Vencida. Objetivo: manter a Rota do Sal e os acertos honestos. Métodos: guia, testemunho, cobrança. Presente em Beira-Sal. Tensão: Guias cobram por pactos orais sem prova; caravanas do outro lado contestam a conta (doc 03, beira-sal).  
  *Vizinhança:* Aliados: Pilotos dos Canais. Rivais: Casa dos Poços.
- **`faction.casa-dos-pocos` — Casa dos Poços.** Facção liderada por Teshi Nó-de-Poço. Objetivo: guardar os poços e o Pátio dos Acertos neutro. Métodos: sinalização, sentinelas de sal. Presente em Beira-Sal. Tensão: Guias cobram por pactos orais sem prova; caravanas do outro lado contestam a conta (doc 03, beira-sal).  
  *Vizinhança:* Aliados: Defensores da Paz. Rivais: Guias de Sal.
- **`faction.vigias-da-orla` — Vigias da Orla.** Facção liderada por Krell Eco-da-Vigia. Objetivo: abrigar quem chega sem perder o controle. Métodos: fogueiras de sinal, patrulha. Presente em Borda-Mansa. Tensão: Gente de longe chega sem guia e sem registro; o posto não sabe quem é quem (doc 03, borda-mansa).  
  *Vizinhança:* Aliados: Casa dos Fios. Rivais: desconhecido.
- **`faction.casa-dos-fios` — Casa dos Fios.** Facção liderada por Ondra Fio-de-Sono. Objetivo: manter o repouso e o pacto de ninguém atacado dormindo. Métodos: hospedagem, tecelagem, remédios de sono. Presente em Fio-Longo. Tensão: Hóspedes que não querem ir embora; a Casa diz que é escolha, os parentes dizem que não (doc 03, fio-longo).  
  *Vizinhança:* Aliados: Vigias da Orla. Rivais: desconhecido.
- **`faction.irmandade-das-despedidas` — Irmandade das Despedidas.** Facção liderada por Dhun Contorno-Quieto. Objetivo: manter o pacto de que os mortos descansam. Métodos: despedidas, Muro dos Nomes. Presente em Candeia-Morta. Tensão: Pactuários divididos sobre o que o pacto pede de um rei necromante, sem dizer em voz alta (doc 03, candeia-morta).  
  *Vizinhança:* Aliados: Memória das Provações. Rivais: Defensores da Paz.
- **`faction.turnos-da-vigilia` — Turnos da Vigília.** Facção liderada por Ilkar Turno-Aberto. Objetivo: manter os turnos sem fechar e proteger a cidade de dia. Métodos: vigias, portões. Presente em Candeia-Morta. Tensão: Pactuários divididos sobre o que o pacto pede de um rei necromante, sem dizer em voz alta (doc 03, candeia-morta).  
  *Vizinhança:* Aliados: Guarda do Rei. Rivais: desconhecido.

## Relógios

- **`clock.desafiantes` — A lista parada e o Conselho dividido.** Relógio, 2/6 no início; uso de Mestre e motor. Avança só por causa observável. Move: nova inscrição que não chega ao registro; Pactuários tomando partido em público; recusa de registrar. Trava: toda inscrição lançada no Arquivo dos Reis que Passaram. Ao completar: o Conselho é obrigado a votar em público se chama o rei a responder (doc 04 §3).  
  *Vizinhança:* Localidade: Morrovéu. Entidades: Morvan Voz-Baixa, Orsk Cicatriz-de-Bando, Cerrane Herança-Firme, Guarda do Rei, Defensores da Paz, Memória das Provações.
- **`clock.partida` — Partida e acusação em Lamaluz.** Relógio, 3/6 no início; uso de Mestre e motor. Avança só por causa observável. Move: nova família que pede guia; acusação formal contra quem guia; cheia ou seca fora de época. Trava: acordo entre Pactuários dos Canais e Pilotos sobre o alcance de L-09. Ao completar: os Pactuários dos Canais abrem **julgamento formal** de quem guia.  
  *Vizinhança:* Localidade: Lamaluz. Entidades: Tazk Pedra-ao-Sol, Pilotos dos Canais, Guardiões das Lamparinas.
- **`clock.fardos` — Fardos sem papel em Fozbreu.** Relógio, 2/6 no início; uso de Mestre e motor. Avança só por causa observável. Move: carga que chega sem papel que o pacto reconheça; visitantes de passagem sem registro; armazém sem espaço. Trava: regra provisória de papel na Casa da Barra; registro de cada fardo. Ao completar: a Casa da Barra suspende a recepção de carga sem papel até haver regra provisória.  
  *Vizinhança:* Localidade: Fozbreu. Entidades: Rusk Rede-da-Barra, Defensores da Paz, Pilotos dos Canais.
- **`clock.acertos-orais` — Acertos orais em Beira-Sal.** Relógio, 2/6 no início; uso de Mestre e motor. Avança só por causa observável. Move: guia que cobra por pacto oral sem prova; caravana que contesta a conta. Trava: acerto feito no Pátio dos Acertos com testemunha; vistoria dos poços; Pátio neutro. Ao completar: o Pátio dos Acertos passa a exigir testemunha de ambos os lados e **suspende acertos orais sem prova**.  
  *Vizinhança:* Localidade: Beira-Sal. Entidades: Vurga Sede-Vencida, Teshi Nó-de-Poço, Guias de Sal, Casa dos Poços.
- **`clock.pacto-que-vence` — O pacto antigo que vence em Selovale.** Relógio, 2/6 no início; uso de Mestre e motor. Avança só por causa observável. Move: alguém invoca uma cláusula esquecida; nova leitura que muda o texto; Pactuários discordando em público. Trava: cópia conferida no Arquivo dos Pactos Velhos; sessão do Conselho dos Selos. Ao completar: o Conselho dos Selos é obrigado a abrir **sessão pública de parecer** sobre o texto.  
  *Vizinhança:* Localidade: Selovale. Entidades: Yeshe Forma-Emprestada, Memória das Provações.
- **`clock.orla-sem-guia` — Chegadas sem guia em Borda-Mansa.** Relógio, 1/6 no início; uso de Mestre e motor. Avança só por causa observável. Move: mais chegadas sem guia e sem registro; alguém que passa com ajuda de dentro, **observado**. Trava: lista no Pátio dos Sem Guia; reforço da Torre das Fogueiras. Ao completar: os Pactuários da Vigia debatem em audiência se **registrar quem chega fere o abrigo** (L-03).  
  *Vizinhança:* Localidade: Borda-Mansa. Entidades: Krell Eco-da-Vigia, Vigias da Orla.
- **`clock.hospedes` — Hóspedes que não partem em Fio-Longo.** Relógio, 1/6 no início; uso de Mestre e motor. Avança só por causa observável. Move: parente que cobra a volta de um hóspede; hóspede que não quer ouvir o parente. Trava: a Casa ouve o hóspede com o parente presente. Ao completar: a Casa dos Fios abre **julgamento formal** de um caso de hóspede (Lei do Repouso, L-06), **sem decidir se há quebra**.  
  *Vizinhança:* Localidade: Fio-Longo. Entidades: Ondra Fio-de-Sono, Casa dos Fios.
- **`clock.despedidas` — A divisão das Despedidas em Candeia-Morta.** Relógio, 2/6 no início; uso de Mestre e motor. Avança só por causa observável. Move: Pactuários que divergem sobre o que o pacto pede de um rei necromante. Trava: reunião dos Pactuários das Despedidas com mediação de Dhun; Casa das Despedidas aberta. Ao completar: a divisão é dita em voz alta na Casa das Despedidas e os Pactuários-mor precisam responder o que **L-07 pede de um rei.  
  *Vizinhança:* Localidade: Candeia-Morta. Entidades: Dhun Contorno-Quieto, Ilkar Turno-Aberto, Irmandade das Despedidas, Turnos da Vigília.

## Pessoas

- **`npc.morvan-voz-baixa` — Morvan Voz-Baixa.** Morvan Voz-Baixa, Humano. Rei Vencedor do Véu dos Malditos; reina em Morrovéu há cerca de cem anos (contagem local); sem assento declarado no Conselho. Necromante: ofício dele, não de povo, com custo e limites (doc 01 parte 4 §3); vence a coroa; restrição: não rompe nem reescreve os pactos e não decide contra o texto de um pacto (doc 04 §2). Agenda: Manter as terras estáveis e os pactos de pé sem ser vencido na Provação das Armas.  
  *Vizinhança:* Lugar: Morrovéu. Relações: vínculo de cargo com Orsk Cicatriz-de-Bando; vínculo de cargo com Cerrane Herança-Firme.
- **`npc.orsk-cicatriz-de-bando` — Orsk Cicatriz-de-Bando.** Orsk Cicatriz-de-Bando, Ursino Sombrio. General da Guarda de Morrovéu; líder da Guarda do Rei (F-01). Comandante de escolta profissional; comanda a guarda de Morrovéu; restrição: não comanda as vigias dos pactos dos outros lugares. Agenda: Manter a guarda unida e disciplinada enquanto a pergunta sobre o rei não é feita em voz alta.  
  *Vizinhança:* Lugar: Morrovéu. Relações: rivalidade da facção com Memória das Provações; aliança da facção com Defensores da Paz; aliança da facção com Turnos da Vigília.
- **`npc.cerrane-heranca-firme` — Cerrane Herança-Firme.** Cerrane Herança-Firme, Nascido do Crepúsculo. Presidente do Conselho dos Pactuários (preside e não vota); benfeitora de Morrovéu (tríade). Mediadora; convoca e preside; restrição: não vota por lugar nenhum, não desempata e não desafia o rei (doc 04 §2.2). Agenda: Manter um assento por lugar e impedir que o Conselho se parta entre defensores e críticos do rei.  
  *Vizinhança:* Lugar: Morrovéu. Relações: vínculo de cargo com Nuvik Canal-Torto; vínculo de cargo com Rusk Rede-da-Barra; vínculo de cargo com Teshi Nó-de-Poço.
- **`npc.ilkar-turno-aberto` — Ilkar Turno-Aberto.** Ilkar Turno-Aberto, Noctívago. General das Vigílias de Candeia-Morta; líder dos Turnos da Vigília (F-11). Comandante de escolta profissional; comanda os turnos da cidade; restrição: não decide o que o Pacto da Vigília exige. Agenda: Manter os turnos de vigia sem fechar e proteger a cidade de dia, quando ela é frágil.  
  *Vizinhança:* Lugar: Candeia-Morta. Relações: aliança da facção com Guarda do Rei; vínculo de cargo com Dhun Contorno-Quieto.
- **`npc.tazk-pedra-ao-sol` — Tazk Pedra-ao-Sol.** Tazk Pedra-ao-Sol, Escamado. Piloto-Mor de Lamaluz; herói vivo (tríade); líder dos Pilotos dos Canais (F-04). Navegador; conhece os canais mutáveis; restrição: guia, não faz nem desfaz pacto e não manda nos Pactuários dos Canais. Agenda: Manter os canais navegáveis e decidir se guia ou não as famílias que partem.  
  *Vizinhança:* Lugar: Lamaluz. Relações: rivalidade da facção com Guardiões das Lamparinas; aliança da facção com Guias de Sal; vínculo de cargo com Nuvik Canal-Torto.
- **`npc.rusk-rede-da-barra` — Rusk Rede-da-Barra.** Rusk Rede-da-Barra, Ratoide. Mestre da Barra, Pactuário da Foz (assento de Fozbreu); líder dos Defensores da Paz (F-02). Guardião do código e navegador; guarda a barra e o Pacto da Barra; restrição: sem frota e sem poder fora do porto. Agenda: Manter a barra pilotável e o porto de pé sem frota de guerra.  
  *Vizinhança:* Lugar: Fozbreu. Relações: rivalidade da facção com Memória das Provações; rivalidade da facção com Irmandade das Despedidas; aliança da facção com Guarda do Rei.
- **`npc.vurga-sede-vencida` — Vurga Sede-Vencida.** Vurga Sede-Vencida, Metamorfo Lupino. Guia-Mestra dos Guias de Sal; herói vivo (tríade); líder dos Guias de Sal (F-06). Guia de deserto; conduz caravanas pelo Ermo de Sal; restrição: guia e testemunha acertos, não faz pacto pelo posto nem fala pelas Marcas das Feras. Agenda: Manter a Rota do Sal viva e os acertos dos guias honestos.  
  *Vizinhança:* Lugar: Beira-Sal. Relações: rivalidade da facção com Casa dos Poços; aliança da facção com Pilotos dos Canais; vínculo de cargo com Teshi Nó-de-Poço.
- **`npc.teshi-no-de-poco` — Teshi Nó-de-Poço.** Teshi Nó-de-Poço, Aracnídeo. Pactuária dos Poços (assento de Beira-Sal); guardiã do Pacto da Caravana; líder da Casa dos Poços (F-07). Guardiã do código e avaliadora de materiais; guarda o Poço Primeiro e as sentinelas de sal; restrição: não manda nos guias nem nas caravanas. Agenda: Manter os poços sinalizados e o Pátio dos Acertos neutro.  
  *Vizinhança:* Lugar: Beira-Sal. Relações: rivalidade da facção com Guias de Sal; aliança da facção com Defensores da Paz; vínculo de cargo com Brisk Poço-Cheio.
- **`npc.yeshe-forma-emprestada` — Yeshe Forma-Emprestada.** Yeshe Forma-Emprestada, Moldável. Presidente do Conselho dos Selos (assento de Selovale); guardiã do Arquivo dos Pactos Velhos; líder da Memória das Provações (F-03). Cronista e arquivista; interpreta e guarda; restrição: não rompe selo nem decide por Morrovéu. Agenda: Descobrir o que o pacto antigo que vence concede antes que alguém invoque a cláusula.  
  *Vizinhança:* Lugar: Selovale. Relações: rivalidade da facção com Guarda do Rei; rivalidade da facção com Defensores da Paz; aliança da facção com Irmandade das Despedidas.
- **`npc.krell-eco-da-vigia` — Krell Eco-da-Vigia.** Krell Eco-da-Vigia, Corvídeo. Vigia-Mor de Borda-Mansa, Pactuário da Vigia (assento); guardião do Pacto da Orla; líder dos Vigias da Orla (F-08). Sentinela e observador do clima; comanda a Torre das Fogueiras; restrição: vigia e abriga; só diz rumor sobre Aeltharyn e não liga o posto a Brumafunda (C, contrato de fronteira). Agenda: Abrigar quem chega e saber quem é quem sem fechar o posto.  
  *Vizinhança:* Lugar: Borda-Mansa. Relações: aliança da facção com Casa dos Fios; vínculo de cargo com Szia Fio-Aberto; dívida com Borda-Mansa.
- **`npc.ondra-fio-de-sono` — Ondra Fio-de-Sono.** Ondra Fio-de-Sono, Onírico. Mestra da Casa dos Fios (assento de Fio-Longo); guardiã do Pacto do Repouso; líder da Casa dos Fios (F-09). Tecelã de sonhos e estalajadeira; hospeda e guarda o pacto; restrição: não obriga ninguém a ficar nem a partir. Agenda: Manter a Casa um lugar de repouso e o pacto de que ninguém é atacado dormindo.  
  *Vizinhança:* Lugar: Fio-Longo. Relações: aliança da facção com Vigias da Orla; vínculo de cargo com Rhek Relato-Manso; dívida com Fio-Longo.
- **`npc.dhun-contorno-quieto` — Dhun Contorno-Quieto.** Dhun Contorno-Quieto, Umbral. Pactuário-mor das Despedidas (assento de Candeia-Morta); benfeitor da cidade (tríade); líder da Irmandade das Despedidas (F-10). Sacerdote e mestre de cerimônias; conduz as despedidas e guarda o Muro dos Nomes; restrição: guarda o pacto e o Muro, não confronta o rei sozinho. Agenda: Manter o pacto de que os mortos descansam e a Casa das Despedidas aberta.  
  *Vizinhança:* Lugar: Candeia-Morta. Relações: rivalidade da facção com Defensores da Paz; aliança da facção com Memória das Provações; dívida com Candeia-Morta.
- **`npc.ardh-primeiro-perdedor` — Ardh Primeiro-Perdedor.** Ardh Primeiro-Perdedor, Nascido do Crepúsculo, falecido(a). Figura da versão dos Pactuários da lenda de Morrovéu (o primeiro rei que perdeu). Limite: não age no início; o que de fato aconteceu é desconhecido (lenda não é fato).  
  *Vizinhança:* Lugar: Morrovéu. Relações: nenhuma documentada.
- **`npc.nuvik-canal-torto` — Nuvik Canal-Torto.** Nuvik Canal-Torto, Ratoide. Guardião da tríade de Lamaluz; Pactuário dos Canais (assento); líder dos Guardiões das Lamparinas (vigia do pacto, acusação). Limite: Guarda o pacto de Lamaluz; poder além do assento e da facção: desconhecido. Sem ficha completa (fora do mínimo de 12 principais, D-22).  
  *Vizinhança:* Lugar: Lamaluz. Relações: rivalidade da facção com Pilotos dos Canais; aliança da facção com Defensores da Paz; vínculo de cargo com Lorra Empréstimo-Justo.
- **`npc.lorra-emprestimo-justo` — Lorra Empréstimo-Justo.** Lorra Empréstimo-Justo, Moldável. Benfeitor(a) da tríade de Lamaluz; modo e meios do benefício: desconhecidos. Limite: Nada além do papel de benfeitor(a) está documentado.  
  *Vizinhança:* Lugar: Lamaluz. Relações: nenhuma documentada.
- **`npc.kerrak-eco-do-mar` — Kerrak Eco-do-Mar.** Kerrak Eco-do-Mar, Corvídeo, falecido(a). Figura da versão dos Corvídeos da lenda de Fozbreu (o rio repete o que o mar disse). Limite: não age no início; o que de fato aconteceu é desconhecido (lenda não é fato).  
  *Vizinhança:* Lugar: Fozbreu. Relações: nenhuma documentada.
- **`npc.ssavi-lodo-firme` — Ssavi Lodo-Firme.** Ssavi Lodo-Firme, Escamado. Benfeitor(a) da tríade de Fozbreu; modo e meios do benefício: desconhecidos. Limite: Nada além do papel de benfeitor(a) está documentado.  
  *Vizinhança:* Lugar: Fozbreu. Relações: nenhuma documentada.
- **`npc.brisk-poco-cheio` — Brisk Poço-Cheio.** Brisk Poço-Cheio, Ratoide. Benfeitor da tríade de Beira-Sal; modo e meios do benefício: desconhecidos. Limite: Nada além do papel de benfeitor está documentado.  
  *Vizinhança:* Lugar: Beira-Sal. Relações: nenhuma documentada.
- **`npc.ethra-vale-guardado` — Ethra Vale-Guardado.** Ethra Vale-Guardado, Nascido do Crepúsculo, falecido(a). Figura da versão dos Crepusculares da lenda de Selovale (selos postos para proteger). Limite: não age no início; o que de fato aconteceu é desconhecido (lenda não é fato).  
  *Vizinhança:* Lugar: Selovale. Relações: nenhuma documentada.
- **`npc.garrun-toca-funda` — Garrun Toca-Funda.** Garrun Toca-Funda, Ursino Sombrio. Benfeitor da tríade de Selovale; modo e meios do benefício: desconhecidos. Limite: Nada além do papel de benfeitor está documentado.  
  *Vizinhança:* Lugar: Selovale. Relações: nenhuma documentada.
- **`npc.piok-fogueira-primeira` — Piok Fogueira-Primeira.** Piok Fogueira-Primeira, Corvídeo, falecido(a). Quem teria acendido a primeira fogueira de sinal de Borda-Mansa. Limite: não age no início; o que de fato aconteceu é desconhecido (lenda não é fato).  
  *Vizinhança:* Lugar: Borda-Mansa. Relações: nenhuma documentada.
- **`npc.szia-fio-aberto` — Szia Fio-Aberto.** Szia Fio-Aberto, Aracnídeo. Benfeitor(a) da tríade de Borda-Mansa; modo e meios do benefício: desconhecidos. Limite: Nada além do papel de benfeitor(a) está documentado.  
  *Vizinhança:* Lugar: Borda-Mansa. Relações: nenhuma documentada.
- **`npc.noor-despertar-primeiro` — Noor Despertar-Primeiro.** Noor Despertar-Primeiro, Onírico, falecido(a). Figura da versão dos Oníricos da lenda de Fio-Longo (a casa nasceu de um sonho). Limite: não age no início; o que de fato aconteceu é desconhecido (lenda não é fato).  
  *Vizinhança:* Lugar: Fio-Longo. Relações: nenhuma documentada.
- **`npc.rhek-relato-manso` — Rhek Relato-Manso.** Rhek Relato-Manso, Corvídeo. Benfeitor da tríade de Fio-Longo; modo e meios do benefício: desconhecidos. Limite: Nada além do papel de benfeitor está documentado.  
  *Vizinhança:* Lugar: Fio-Longo. Relações: nenhuma documentada.
- **`npc.sorren-vigilia-primeira` — Sorren Vigília-Primeira.** Sorren Vigília-Primeira, Noctívago, falecido(a). Figura da versão dos Noctívagos da lenda de Candeia-Morta (vigília que prometeu não ser a última). Limite: não age no início; o que de fato aconteceu é desconhecido (lenda não é fato).  
  *Vizinhança:* Lugar: Candeia-Morta. Relações: nenhuma documentada.
- **`npc.mursk-juramento-de-bando` — Mursk Juramento-de-Bando.** Mursk Juramento-de-Bando, Ursino Sombrio, falecido(a). Herói lendário sem lugar fixo; lugar, era e feitos: desconhecidos. Limite: não age no início; o que de fato aconteceu é desconhecido (lenda não é fato).  
  *Vizinhança:* Lugar: sem lugar fixo (lenda do povo). Relações: nenhuma documentada.
- **`npc.zibb-rede-primeira` — Zibb Rede-Primeira.** Zibb Rede-Primeira, Ratoide, falecido(a). Herói lendário sem lugar fixo; lugar, era e feitos: desconhecidos. Limite: não age no início; o que de fato aconteceu é desconhecido (lenda não é fato).  
  *Vizinhança:* Lugar: sem lugar fixo (lenda do povo). Relações: nenhuma documentada.
- **`npc.aun-silencio-primeiro` — Aun Silêncio-Primeiro.** Aun Silêncio-Primeiro, Umbral, falecido(a). Herói lendário sem lugar fixo; lugar, era e feitos: desconhecidos. Limite: não age no início; o que de fato aconteceu é desconhecido (lenda não é fato).  
  *Vizinhança:* Lugar: sem lugar fixo (lenda do povo). Relações: nenhuma documentada.
- **`npc.lish-rosto-primeiro` — Lish Rosto-Primeiro.** Lish Rosto-Primeiro, Moldável, falecido(a). Herói lendário sem lugar fixo; lugar, era e feitos: desconhecidos. Limite: não age no início; o que de fato aconteceu é desconhecido (lenda não é fato).  
  *Vizinhança:* Lugar: sem lugar fixo (lenda do povo). Relações: nenhuma documentada.
- **`npc.xalu-no-primeiro` — Xalu Nó-Primeiro.** Xalu Nó-Primeiro, Aracnídeo, falecido(a). Herói lendário sem lugar fixo; lugar, era e feitos: desconhecidos. Limite: não age no início; o que de fato aconteceu é desconhecido (lenda não é fato).  
  *Vizinhança:* Lugar: sem lugar fixo (lenda do povo). Relações: nenhuma documentada.

## Conferências

| Verificação | Resultado |
|---|---|
| Entidades com resumo e ID | 57 de 57 (49 do registro mestre mais 8 relógios) |
| Entidades do registro mestre sem resumo | 0 |
| Resumo acima de 500 caracteres | 0 (maior: 486) |
| Vizinhança acima de 300 caracteres | 0 (maior: 175) |
| Resumo que usa suspeita, desconhecido ou `nunca_diz` | 0 |
| Duração de rota, número de população ou termo de fronteira fora de veto | 0 |
| Campo vazio | 0 |

## Decisão pendente

**D-92:** aprovar os 57 resumos e a vizinhança. Opções: **A.** aprovar; **B.** trocar o texto de uma entidade (diga qual). **Recomendação: A.**
