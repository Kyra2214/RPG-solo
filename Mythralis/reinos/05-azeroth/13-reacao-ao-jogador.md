# Azeroth — Reação do reino ao jogador (doc 13, R5-5.4)

**Estado:** R5-5.4, D-164 (aceita pela continuação) · **Data:** 2026-11-07

**Etiqueta:** tudo é **[PROPOSTO]**. **Fonte:** docs 01 (parte 1), 04 (§2 a §10), 05 (§1 a §4), 06 e 07; `12-registro-mestre.csv`, `12-relacoes.csv`, `12-relogios.csv`; `plano/INTEGRACAO_FUTURA_RPG_SOLO.md` (MP-06, MP-07). Dados em `13-reputacao.csv` (25 linhas) e `13-canais.csv` (11 linhas). Molde: `reinos/04-vale-palancar/13-reacao-ao-jogador.md`.

## 1. Regras e limites

- Descreve **a camada de mundo**: o que cada lugar e facção valoriza ou pune, quem reage e por que canal a notícia chega. **Números de fama, alcance, preço e sorteio de rumor pertencem ao motor** (MP-07); aqui só há linguagem qualitativa.
- **Reputação não substitui prova**: rumor justifica investigação, não condenação. O mesmo ato pode dar fama diferente em cada lugar e facção.
- **Cada coroa governa o que é seu e não manda nas outras** (D-141): ser procurado numa coroa não torna o jogador procurado nas outras, e a Mesa de Meiovau não executa nada. A fama de um lugar chega a outro só pelos canais da seção 4.
- A notícia viaja só pelos canais da seção 4. **Toda duração de rota é `desconhecida` (doc 07):** a notícia **não tem prazo definido**. Linha de torres derrubada, cerração longa, comporta fechada ou noite sem luz cortam o canal.
- A IA narra a distorção do rumor, nunca a transforma em fato. Nenhuma reação exige a presença do jogador e nenhuma é punição automática.
- **Nada aqui toca Aeltharyn, o Véu, as Coroas ou o Vale**, nem diz como a fama chegaria a eles. Nenhuma reação depende da raça, do povo ou da origem do jogador (W-01): **ninguém pergunta o motivo a quem chega** e nenhum chegado é ameaça por origem (doc 04 §7). O entendimento entre coroas é **acordo** (doc 04 §5).
- Mapeamento de ato para reação é leitura editorial (ASSUMINDO), derivada só de objetivos, leis e justiça já escritos; nenhuma facção, ofício, NPC, lugar ou rota novo. A precedência entre as coroas segue sem árbitro: nenhuma reação a decide, e **tomar partido em público** custa disposição com quem está do outro lado.

## 2. Fama e reputação por lugar e facção (R5-5.4a)

Disposição é **por entidade**, não global. Ver `13-reputacao.csv` (prefixo `rep.`). As três coroas não têm ID próprio (são instituições); a disposição com cada uma se lê pela capital (`city.campoalvo`, `city.alvamar`, `city.torreclara`) e pelo conselho (F-01, F-04, F-10).

| Entidade | Ganha disposição | Perde disposição | Quem reage | Canais |
|---|---|---|---|---|
| `city.campoalvo` | medir o grão no Salão das Medidas e aceitar a medida; guardar o excedente nos Celeiros Comuns; ajudar no aterro e nos canais de drenagem; pedir abertura de celeiro com causa observável | mexer na medida ou na Casa das Pesagens; esconder ou desviar excedente; tomar partido em público pela precedência de outra coroa; contestar o preço do pão fora do dia de pesagem | `npc.helion-vigilia-do-celeiro`, `npc.dodda-prato-cheio`, `npc.odile-medida-cheia`, `npc.tomlin-horta-larga`, `faction.conselho-dos-celeiros` | Salão das Medidas; Celeiros Comuns; Casa das Pesagens |
| `city.vaudouro` | passar pelas comportas pagando o que a medida manda; avisar as duas coroas antes de mexer na passagem; usar o Pátio das Barcaças e a Feira das Pontes sem escolher coroa | fechar comporta sem aviso às duas coroas; passar sem pagar; escolher por conta própria qual coroa taxa; alimentar a dúvida entre as coroas em proveito próprio | `npc.ebbo-praia-rasa`, `npc.larkin-barcaca-cheia`, `npc.garvane-remo-firme`, `faction.casa-dos-barqueiros` | Pátio das Barcaças; Comportas Velhas; Feira das Pontes |
| `city.torrefronte` | registrar quem chega ou parte no Livro dos Chegados; respeitar a Torre da Vigia, o Muro de Terra e o Pátio das Guardas; deixar a leitura do registro a quem o cuida | passar o limite sem registro; escrever ou arrancar página do Livro; tomar o lado da guarnição ou da vigia em público; responder por quem chega no lugar da guarnição | `npc.solenne-vigilia-longa`, `npc.sabeth-patio-aberto`, `faction.guarnicao-do-limite`, `faction.posto-dos-pipeiros` | Livro dos Chegados; Pátio das Guardas |
| `city.alvamar` | entrar pelo molhe e pagar a taxa no cais; respeitar a Corrente da Boca e os turnos; receber quem chega na Doca dos Povos do Mar; levar a disputa da taxa ao órgão, não à rua | descarregar fora do molhe; mexer na corrente; esconder carga da pesagem; decidir por conta própria se a taxa serve ao porto ou à Coroa | `npc.corlan-cais-primeiro`, `npc.rilla-espuma-fina`, `npc.dorgan-escudo-lento`, `npc.pelmo-balanca-de-porto`, `npc.marnu-espuma-dada`, `faction.alfandega-das-mares`, `faction.guarda-da-corrente` | Casa do Cais; Molhe Grande; Doca dos Povos do Mar |
| `city.enseada-vigia` | manter a luz acesa; avisar a barra a quem chega; usar a Casa dos Pilotos; acolher quem chega no Cais dos Recém-Chegados | apagar ou desviar farol; deixar quem chega sem aviso de barra; tomar os faróis para uma coroa contra a outra em público | `npc.perrin-farol-aceso`, `npc.nerella-canto-da-barra`, `npc.ressan-concha-aberta`, `faction.irmandade-dos-faroleiros`, `faction.pilotos-da-enseada` | Farol do Sul; Casa dos Pilotos; Pedra da Barra |
| `city.concharia` | encomendar casco e conserto dentro da Lei do Santuário; poupar o Jardim de Coral e o Santuário da Enseada; entrar pelo Canal Estreito com quem o conhece | ferir o coral ou o santuário; forçar canal novo; tomar o lado dos construtores ou dos guardas em público | `npc.orrume-abrigo-de-casco`, `npc.corali-fundo-de-coral`, `faction.construtores-de-concha`, `faction.guardas-do-santuario` | Estaleiro Grande; Santuário da Enseada; Canal Estreito |
| `city.torreclara` | pagar o aviso e a hora; guardar o código da torre; levar notícia pelo Pátio dos Códigos | mudar código fora da Casa dos Sinais; calar torre; copiar código; exigir sinal próprio sem passar pela Casa | `npc.casimar-codigo-limpo`, `npc.clarel-lume-de-torre`, `npc.brevo-rumo-alto`, `npc.astrel-noite-clara`, `faction.casa-dos-sinais` | Torre-Mestra; Pátio dos Códigos; Mesa dos Mapas |
| `city.estrelado` | levar doação e pedido ao Arquivo dos Astros; pedir calendário de rota; ouvir a leitura do céu sem tomar lado entre estelares e luminares | falsear previsão ou calendário; pedir previsão que sirva a uma coroa só; mexer no Arquivo dos Astros; tomar partido na leitura do céu | `npc.eloh-cupula-acesa`, `npc.ixra-noite-aberta`, `faction.leitores-do-ceu`, `faction.vigilia-da-cupula` | Arquivo dos Astros; Pátio das Rotas; Cúpula Grande |
| `city.pouso-das-brisas` | usar o Pouso Largo; consertar pipa e torre na Oficina de Pipas; avisar o Posto quando a linha cai | derrubar ou queimar torre; calar a linha para uma coroa; usar a Rampa dos Sinais sem aviso; pedir sinal próprio fora do Posto | `npc.hollis-torre-do-meio`, `npc.poppel-mesa-de-viajante`, `faction.posto-dos-pipeiros` | Torre do Meio; Pouso Largo; Oficina de Pipas |
| `city.meiovau` | deixar a arma e a guarda de coroa fora do pátio; ouvir cada cadeira na rodada; respeitar as três chaves; registrar o chegado e respeitar o Albergue; levar à Mesa um ponto concreto | entrar armado ou com guarda de coroa no pátio; levar, expulsar ou entregar chegado sozinho; copiar ou retirar chave; decidir sem as três; perguntar o motivo a quem chega | `npc.tolla-mesa-posta`, `npc.auriven-porta-acesa`, `npc.ruvane-chave-contada`, `faction.albergue-dos-chegados`, `faction.mesa-estreita` | Casa das Três Chaves; Pátio das Audiências; Albergue dos Chegados |
| `faction.conselho-dos-celeiros` | guardar o excedente e manter o celeiro comum; medir e pesar sem desvio | esvaziar celeiro; mexer na medida; exigir preço fora da pesagem | `npc.helion-vigilia-do-celeiro` | Salão das Medidas; Celeiros Comuns |
| `faction.guarnicao-do-limite` | registrar no Livro dos Chegados; manter o limite vigiado; ler o registro de um só modo | passar sem registro; mexer em página do Livro; contestar o registro em público | `npc.solenne-vigilia-longa` | Livro dos Chegados; Pátio das Guardas |
| `faction.casa-dos-barqueiros` | manter doca, canal e comporta; avisar antes de fechar; pagar o aluguel de canal | fechar comporta sem aviso; passar sem pagar o aluguel; tomar o lado de uma coroa contra os barqueiros | `npc.ebbo-praia-rasa` | Pátio das Barcaças; Comportas Velhas |
| `faction.alfandega-das-mares` | declarar a carga na pesagem; levar a taxa ao porto e não só à Coroa; respeitar o registro | esconder carga; descarregar fora do molhe; pagar a uma coroa por fora da pesagem | `npc.pelmo-balanca-de-porto` | Casa do Cais; pesagem |
| `faction.guarda-da-corrente` | respeitar molhe, corrente e turnos; abrir a baía a quem precisa | mexer na corrente; forçar a boca; entrar sem passar pelo molhe | `npc.dorgan-escudo-lento` | Molhe Grande; Corrente da Boca |
| `faction.irmandade-dos-faroleiros` | manter a luz; dar a voz a quem vive de barco; avisar a barra | apagar farol; mandar nos faroleiros em nome de uma coroa | `npc.perrin-farol-aceso` | Farol do Sul; aviso de barra |
| `faction.pilotos-da-enseada` | respeitar o ensino da barra; piloto por ofício; levar o aviso inteiro | passar a barra sem aviso; forçar piloto a servir uma só coroa | `npc.nerella-canto-da-barra` | Casa dos Pilotos; Pedra da Barra |
| `faction.construtores-de-concha` | encomendar casco, vela e canal; dar prazo ao estaleiro | atrasar casco por favor político; combater o canal maior em público sem ouvir o estaleiro | `npc.orrume-abrigo-de-casco` | Estaleiro Grande; Canal Estreito |
| `faction.guardas-do-santuario` | respeitar jardim e santuário; apoiar a recusa de canal que fere o coral | ferir o coral; forçar canal; usar o santuário como pressão sobre os construtores | `npc.corali-fundo-de-coral` | Santuário da Enseada; Jardim de Coral |
| `faction.casa-dos-sinais` | guardar o código; manter a rede inteira; pagar pela hora certa | mudar código fora da Casa; calar torre; copiar código | `npc.casimar-codigo-limpo` | Torre-Mestra; Pátio dos Códigos |
| `faction.leitores-do-ceu` | respeitar mapa, órbita e previsão dos estelares; levar doação | falsear previsão; tratar a leitura da Vigília como única | `npc.ixra-noite-aberta` | Mesa dos Mapas; Pátio das Rotas |
| `faction.vigilia-da-cupula` | respeitar a cúpula, o arquivo e o voto da vigília; levar doação | falsear calendário de rota; tratar a leitura dos Leitores como única | `npc.eloh-cupula-acesa` | Cúpula Grande; Arquivo dos Astros |
| `faction.posto-dos-pipeiros` | manter torres e pousos; consertar pipa e torre; avisar a linha caída | derrubar torre; calar a linha; pedir sinal só para uma coroa | `npc.hollis-torre-do-meio` | Torre do Meio; Oficina de Pipas |
| `faction.albergue-dos-chegados` | acolher quem chega sem perguntar o motivo; doar com transparência | entregar chegado a uma coroa; exigir prova de origem | `npc.auriven-porta-acesa` | Albergue dos Chegados; Cais dos Recém-Chegados |
| `faction.mesa-estreita` | registrar e dar prazo antes de qualquer sustento; levar pedido à Mesa | dar sustento sem registro; esconder chegado do registro | `npc.ruvane-chave-contada` | Casa das Três Chaves; contagem |

## 3. Procurado, favor e reparação (R5-5.4b)

### 3.1 Estados de "procurado" (ASSUMINDO, ordinais)

| Estado | Condição | Limite escrito |
|---|---|---|
| Rumor | alguém diz que o jogador fez algo | justifica investigação, não condenação |
| Investigação | o lugar ou a facção aponta o ato e a regra | testemunho, registro e inferência são provas diferentes (leitura editorial) |
| Procurado numa coroa | o órgão do lugar aponta quebra da lei (L-01 a L-08) | quem julga é o governante da coroa, com o conselho; o apelo vai ao governante (doc 04 §6); não vale nas outras coroas |
| Disputa entre coroas | duas coroas reclamam o caso e as partes levam a Mesa | acordo ou nada, sem execução forçada (doc 04 §6); a Mesa só abre com as três chaves |
| Resolvido | decisão registrada | sanção qualitativa (§3.4); crime grave contra pessoa, exílio e pena de morte são `desconhecidos` (doc 04 §6) |

### 3.2 Ato, regra e autoridade

| Ato | Regra | Autoridade inicial | Desfecho possível |
|---|---|---|---|
| mexer na medida ou desviar o excedente | Lei das Medidas (L-01) | Conselho dos Celeiros | reparação ou perda de ofício; quem ajusta a medida em ano ruim segue `desconhecido` |
| fechar comporta sem aviso às duas coroas ou passar sem pagar | Lei da Passagem (L-02) | Casa dos Barqueiros; apelo ao governante do Trigal | pagar o que a medida manda; a quem pagar com duas coroas segue `desconhecido` |
| passar o limite sem registro ou mexer no Livro | Lei do Livro (L-03) | Guarnição do Limite, sob a sua coroa | reparação ou perda do posto; quem lê o registro segue `desconhecido` |
| descarregar fora do molhe ou mexer na corrente | Lei da Baía (L-04) | Guarda da Corrente e Alfândega das Marés | reparação ou exclusão do cais; se a taxa serve ao porto ou à Coroa segue `desconhecido` |
| apagar farol ou deixar a barra sem aviso | Lei da Luz (L-05) | Irmandade dos Faroleiros; infração na barra: Guarda da Corrente e Alfândega | reparação ou exclusão do cais; de quem são os faróis segue `desconhecido` |
| ferir o jardim ou o santuário, ou forçar canal novo | Lei do Santuário (L-06) | Mestres de Concha | reparação (ASSUMINDO; o doc 04 não fixa sanção própria); se o canal maior fere o santuário segue `desconhecido` |
| mudar, copiar ou calar código de torre | Lei do Código (L-07) | Casa dos Sinais, sob a sua coroa | reparação ou perda do posto; quem é avisado quando o código muda segue `desconhecido` |
| falsear previsão ou calendário de rota | Lei do Céu (L-08) | Mesa dos Astros, sob a Coroa do Vento | reparação (ASSUMINDO; sem sanção própria no doc 04); quem interpreta o céu segue `desconhecido` |
| entrar armado ou com guarda de coroa no pátio, copiar chave ou decidir sem as três | Lei das Três Chaves (L-09) | Mesa de Meiovau | quem rompe acordo perde a cadeira até reparar (doc 04 §5); a chave copiada segue `desconhecida` |
| levar, expulsar ou entregar chegado sozinho | Costume da Porta (L-10) | Albergue dos Chegados; Mesa de Meiovau | só com as três chaves; prazo, sustento e trabalho do chegado seguem em disputa |

### 3.3 Favor

- **Pessoal:** NPC com vínculo (`12-relacoes.csv`) pode pedir ou conceder; só sabe o que consta em `12-conhecimento.csv`. Afeto: 0 relação documentada (D-159); nenhum favor de afeto é inventado.
- **Institucional:** facção cobra ou concede conforme `13-reputacao.csv`; não é troca automática. Facções rivais (doc 04 §10) não concedem favor uma à outra por causa do jogador.
- **Dívida de apoio:** o lugar-mãe e o lugar apoiado devem apoio por vínculo político (doc 03 §1; doc 07), sem preço nem quantia aqui.
- **Hospitalidade:** o Albergue dos Chegados recebe quem chega a Meiovau pelo Costume da Porta, sem perguntar o motivo; hospedagem não prova apoio a nenhuma coroa.

### 3.4 Reparação

Escrita no doc 04 §6, em termos qualitativos: **reparação ou perda de ofício** (disputa dentro de uma coroa), **pagar o que a medida manda** (taxa de passagem), **reparação ou exclusão do cais** (porto, molhe e barra), **reparação ou perda do posto** (torre, código e registro do limite) e **acordo ou nada** (disputa entre coroas). Exílio e pena de morte **não estão fixados**. Reparar não apaga o rumor: a reputação muda por canal e por lugar, no ritmo do motor.

## 4. Intermediários (R5-5.4c)

Quem leva a notícia, o pedido ou a decisão. Ver `13-canais.csv` (prefixo `canal.azeroth-`).

| Canal | Função | O que o cânone diz | Limite | Rota |
|---|---|---|---|---|
| `canal.azeroth-mesa` — Mesa de Meiovau e as três chaves | levar o ponto concreto entre coroas e arbitrar o que elas não acertam em casa | a Mesa só abre com as três chaves; fala-se uma cadeira por vez, sem arma e sem guarda de coroa no pátio; decide por maioria; arbitra ponto concreto | não decide a precedência, não impõe e não executa; vale pela chave de cada coroa; se uma chave foi copiada segue `desconhecido` | `route.meiovau-campoalvo`, `route.meiovau-alvamar`, `route.meiovau-torreclara` |
| `canal.azeroth-celeiros` — Salão das Medidas e Celeiros Comuns | levar pedido de medida, de pesagem e de abertura de celeiro | o governante despacha no Salão das Medidas; o Conselho dos Celeiros guarda o excedente e pode recusar medida, não governar | só vale dentro da Coroa do Trigal; quem ajusta a medida em ano ruim segue `desconhecido` | `route.campoalvo-vaudouro`, `route.meiovau-campoalvo` |
| `canal.azeroth-barcacas` — Casa dos Barqueiros e comportas | levar notícia e pedido entre o interior e o mar pelo rio | a Casa cuida de docas, canais e comportas; as comportas não se fecham sem aviso às duas coroas | não decide quem taxa a passagem; a Casa lucra com a dúvida, então a notícia pode vir torcida; sem duração | `route.campoalvo-vaudouro`, `route.vaudouro-costa-sul` |
| `canal.azeroth-livro` — Livro dos Chegados e guarnição | registrar quem chega ou parte do limite e levar o registro à coroa | a guarnição registra no Livro; guarnição e vigia leem o registro de modos diferentes | prova o que foi registrado, não quem é o chegado; as páginas que ninguém explica seguem `desconhecidas`; nenhuma rota além do limite | `route.campoalvo-torrefronte` |
| `canal.azeroth-cais` — Casa do Cais, Alfândega e molhe | levar o aviso de carga, de taxa e de entrada ao cais | a Alfândega pesa e registra a carga; a Guarda da Corrente guarda a boca; a taxa é cobrada no cais | a taxa não prova de onde vem a carga; navio que descarrega fora do molhe é rumor, não ligação (MA-04) | `route.alvamar-enseada-vigia`, `route.meiovau-alvamar`, `route.alvamar-costas` |
| `canal.azeroth-farois` — Faróis, Casa dos Pilotos e Cais dos Recém-Chegados | levar o aviso de barra e a notícia de mar a quem chega | os faroleiros mantêm a luz; os pilotos guiam a barra; o Cais dos Recém-Chegados recebe quem chega | só o lado de cá da rota das Costas; uma noite sem luz corta o canal; de quem são os faróis segue `desconhecido` | `route.enseada-vigia-costas`, `route.alvamar-enseada-vigia` |
| `canal.azeroth-santuario` — Santuário da Enseada e Canal Estreito | levar a recusa ou o pedido de obra entre o estaleiro e o jardim | os Mestres de Concha abrigam construtores e guardas; o canal é a entrada entre os recifes | não decide se o canal maior fere o santuário; a voz do órgão está em disputa | `route.alvamar-concharia` |
| `canal.azeroth-torres` — Casa dos Sinais, torres e pipas | levar aviso e hora pela linha de torres e pelos pousos | a Casa guarda os códigos e vende aviso e hora; o Posto dos Pipeiros mantém torres e pousos entre o interior e a costa | torre calada, cerração longa ou linha derrubada cortam o canal; quem é avisado quando o código muda segue `desconhecido`; sem duração | `route.torreclara-pouso-das-brisas`, `route.pouso-das-brisas-litoral` |
| `canal.azeroth-astros` — Mesa dos Astros e Arquivo dos Astros | levar previsão e calendário de rota às coroas | a Mesa dos Astros abriga Leitores do Céu e Vigília da Cúpula; o calendário de rota e a previsão saem dela | não decide quem interpreta o céu; cada coroa pede a previsão que lhe serve; depende de a Coroa do Vento pagar | `route.torreclara-estrelado` |
| `canal.azeroth-albergue` — Albergue dos Chegados e Mesa Estreita | levar o registro e o pedido de acolhida de quem chega a Meiovau | o Costume da Porta rege a acolhida: registro, guarda do Albergue e nenhuma coroa sozinha; Albergue pede acolhida larga, Mesa Estreita pede registro e prazo | só a chegada em Meiovau; prazo, sustento e trabalho do chegado seguem em disputa; nada sobre como chegam | `route.meiovau-fugas` |
| `canal.azeroth-rumor` — Rumor local | espalhar uma versão de um ato dentro do lugar | cada lugar tem rumores não confirmados (doc 01, parte 1) | rumor não vira fato e justifica investigação, não condenação; a IA narra a distorção sem transformá-la em fato | nenhuma escrita |

**Lacunas explícitas:** não há caçador de recompensa, cartaz ou mensageiro de ofício escrito em Azeroth; nenhum foi criado. Os pipeiros mantêm torres e pousos, mas o doc 04 não lhes dá ofício de mensageiro. Pares de lugares sem ligação descrita no doc 07 (por exemplo Campoalvo ↔ Alvamar) **não têm canal escrito**: a notícia só chega por quem passa por Meiovau ou pelas rotas documentadas (ASSUMINDO).

## 5. Ligação com o plano do jogo

- **MP-07 (fama e rumores):** a disposição é por entidade, qualitativa, e vem de `13-reputacao.csv`; os canais de `13-canais.csv` e as rotas do doc 07 dão os caminhos; números, alcance e duração são do motor, não daqui.
- **MP-06 (caçadores):** Azeroth não escreve caçador de recompensa; se o motor oferecer um, só pode agir pelos canais acima e sem criar facção ou ofício.
- Gatilhos de relógio (`12-relogios.csv`) e eventos (`12-eventos.csv`) podem mudar a disposição só por causa observável; nenhuma reação é automática.

## 6. Conferências

| Verificação | Resultado |
|---|---|
| Linhas de reputação | 25 (10 lugares e 15 facções) |
| Canais | 11 |
| ID fora do registro mestre ou NPC falecido | 0 |
| Rota fora do doc 07 | 0 |
| Campo vazio | 0 |
| Fronteira (Aeltharyn, Véu, Coroas, Vale) | 0 |
| Número de fama, preço, prazo ou distância | 0 |
| Termo proibido pelo doc 04 ou reação que depende da raça do jogador | 0 |
| NPC, lugar, facção, ofício ou rota novo | 0 |

## 7. Decisão pendente

**D-164:** aprovar a reação, os estados de procurado e os 11 canais. Opções: **A.** aprovar; **B.** trocar ou tirar uma reação ou canal (diga qual). **Recomendação: A.**
