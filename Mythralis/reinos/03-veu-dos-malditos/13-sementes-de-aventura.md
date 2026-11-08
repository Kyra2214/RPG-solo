# 13 — Sementes de aventura (R3-5.3)

**Estado:** rascunho de R3-5.3 para aprovação do autor (D-89) · **Data:** 2026-11-06

**Etiqueta:** tudo é **[PROPOSTO]**; semente é situação jogável, não fato canônico. **Fonte:** docs 01 (parte 1), 04, 05, 06 e 08; `12-relogios.csv`, `12-eventos.csv`, `12-registro-mestre.csv`. Dados em `13-sementes-de-aventura.csv` (11 colunas, molde do reino 2).

## Regras

- 8 lugares × 3 sementes = **24**: uma **pequena (P)**, uma **média (M)** e uma **grande (G)**. ID: `seed.<lugar>.<p|m|g>`. O reino não tem assentamentos (D-66).
- Semente é **situação jogável**, não fato canônico. Não altera estado, relógio, lei, pacto ou relação até ser resolvida por ação, regra ou evento validado.
- **Sem solução predefinida:** *abordagens possíveis* são caminhos, não respostas; nenhuma semente escolhe resposta de mistério (MV-01 a MV-09) nem decide L-01, L-07, L-09, o que a Descida traz ou o que o fim do pacto antigo concede.
- **Cada semente tem "se ninguém age":** resultado proporcional, sem punição automática; relógio só avança por causa observável (`12-relogios.md` §1).
- **Só entidades existentes:** agentes são pessoas vivas e facções do registro mestre; nenhum NPC novo; nenhum falecido como agente. Pedidos de "família" ou "hóspede" sem nome são cenário, não personagem.
- **Ameaça** (1 a 4, ASSUMINDO, mesma escala do reino 2) mede o **risco para o personagem**: 1 incômodo ou risco baixo; 2 moderado; 3 sério ou envolve autoridade; 4 alto, pode mudar uma instituição ou um relógio. Faixas: P 1, M 2–3, G 3–4; nenhuma semente tem nível 5.
- **Liga** só com relógio ou evento-tipo já escritos; "ligação não provada" onde as fontes não ligam (as pequenas de rumor).
- Lacuna explícita: nenhum tempo, distância, rota, número, população ou preço novo. Nada sobre Aeltharyn, as Coroas, as Marcas das Feras, Brumafunda ou a Neblina Longa.

## Resumo

| Faixa | Sementes | Ameaça 1 | 2 | 3 | 4 |
|---|---|---|---|---|---|
| Pequena | 8 | 8 | 0 | 0 | 0 |
| Média | 8 | 0 | 7 | 1 | 0 |
| Grande | 8 | 0 | 0 | 6 | 2 |

Total: **24** sementes; 17 com ligação a relógio ou evento (as 8 pequenas, sem ligação escrita).

## Lugares

### `city.morroveu` — Morrovéu

- **Pequena — A inscrição que não chegou** (`seed.morroveu.p`, ameaça 1): Cerrane Herança-Firme pede que alguém confira no Arquivo dos Reis que Passaram se há inscrição de desafiante que nunca chegou à lista, sem afirmar que exista.  
  *Agentes:* `npc.cerrane-heranca-firme`. *Abordagens possíveis:* ler o registro do Arquivo; ouvir escribas; perguntar à Guarda quem anota a entrada no Campo das Provações. *Se ninguém age:* a lista segue como está; nada indica que haja inscrição perdida. *Liga:* `clock.desafiantes`. *Fonte:* doc 01 §2.1 (problemas); doc 06 OV-01.
- **Média — Dois lados do Conselho** (`seed.morroveu.m`, ameaça 3): Pactuários tomam partido em público e Cerrane quer um relato dos dois lados antes de convocar a sessão, sem que ela vote.  
  *Agentes:* `npc.cerrane-heranca-firme`, `npc.orsk-cicatriz-de-bando`, `faction.defensores-da-paz`, `faction.memoria-das-provacoes`. *Abordagens possíveis:* ouvir quem defende o rei e quem lembra a regra; comparar as duas versões da lenda da coroa; registrar cada fala como o Conselho aprovar. *Se ninguém age:* as posições endurecem; nenhuma sessão é convocada por conta própria. *Liga:* `clock.desafiantes`. *Fonte:* doc 01 §2.1 (problemas, lenda); doc 04 §2; doc 06 CV-01, CF-01.
- **Grande — O rei chamado a responder** (`seed.morroveu.g`, ameaça 4): O Conselho se prepara para votar em público se chama o rei a responder; Cerrane precisa de quem confira que a sessão respeita a Lei da Provação sem decidir o resultado, e a Guarda do Rei observa.  
  *Agentes:* `npc.cerrane-heranca-firme`, `npc.orsk-cicatriz-de-bando`, `npc.morvan-voz-baixa`, `faction.guarda-do-rei`, `faction.defensores-da-paz`. *Abordagens possíveis:* conferir o texto da Lei da Provação (L-01) com os escribas; mediar entre Pactuários e Guarda; levar o impasse ao Conselho dos Selos (doc 04 §2.2). *Se ninguém age:* a sessão ocorre sem mediador; o resultado não é decidido aqui e a Provação segue só pela regra. *Liga:* `event.conselho-vota-em-publico`. *Fonte:* doc 04 §3 e §5; doc 05 §1 (L-01); doc 06 CV-01.

### `city.lamaluz` — Lamaluz

- **Pequena — A lamparina sem dono** (`seed.lamaluz.p`, ameaça 1): Tazk Pedra-ao-Sol ouve de novo o rumor de lamparinas que ninguém acendeu e pede que alguém veja se alguma está acesa no Cais das Lamparinas, sem afirmar que haja.  
  *Agentes:* `npc.tazk-pedra-ao-sol`. *Abordagens possíveis:* rondar o Cais à noite; perguntar a quem mantém as lamparinas; comparar os relatos dos pilotos. *Se ninguém age:* pode ser só conversa de cais; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.2 (rumores).
- **Média — Uma família que pede guia** (`seed.lamaluz.m`, ameaça 2): Uma família de pacto antigo pede guia e Tazk quer quem a conduza ou testemunhe o caso sem cair em acusação.  
  *Agentes:* `npc.tazk-pedra-ao-sol`, `npc.nuvik-canal-torto`, `faction.pilotos-dos-canais`, `faction.guardioes-das-lamparinas`. *Abordagens possíveis:* guiar por um canal conhecido; testemunhar o pedido perante os Pactuários dos Canais; pedir um acordo sobre o alcance de L-09. *Se ninguém age:* a família decide sozinha ou fica; ninguém é acusado sem causa observada. *Liga:* `clock.partida`. *Fonte:* doc 01 §2.2 (problemas); doc 06 OV-02, CV-02; doc 05 §1 (L-09).
- **Grande — Quem guia, julgado** (`seed.lamaluz.g`, ameaça 3): Os Pactuários dos Canais se preparam para abrir julgamento formal de quem guia os que partem; Tazk precisa de quem ouça o caso antes do acusado e confira o que o pacto manda.  
  *Agentes:* `npc.tazk-pedra-ao-sol`, `npc.nuvik-canal-torto`, `faction.pilotos-dos-canais`, `faction.guardioes-das-lamparinas`. *Abordagens possíveis:* reunir testemunhas de cada lado; ler o texto do pacto com os Pactuários; propor ouvir o caso antes de acusar. *Se ninguém age:* o julgamento abre sem testemunha de defesa; a pena segue `desconhecida` e o efeito fica em Lamaluz. *Liga:* `event.julgamento-de-quem-guia`. *Fonte:* doc 04 §6; doc 05 §1 (L-09); doc 06 CV-02, CF-02.

### `city.fozbreu` — Fozbreu

- **Pequena — A maré que deixa coisas** (`seed.fozbreu.p`, ameaça 1): Rusk Rede-da-Barra ouve de novo que a maré de certo dia deixa na lama coisas que o rio não carregou e pede que alguém confira o que a maré deixou, sem afirmar que exista.  
  *Agentes:* `npc.rusk-rede-da-barra`. *Abordagens possíveis:* andar pelos bancos de lama na maré baixa; perguntar à Torre das Marés; comparar com o registro da Casa da Barra. *Se ninguém age:* pode ser só história de porto; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.3 (rumores).
- **Média — O fardo sem dono** (`seed.fozbreu.m`, ameaça 2): Rusk pede que alguém confira um fardo do armazém contra os registros da Casa da Barra.  
  *Agentes:* `npc.rusk-rede-da-barra`, `faction.defensores-da-paz`. *Abordagens possíveis:* conferir o registro do fardo; ouvir quem recebe cargas; propor registro provisório de cada fardo. *Se ninguém age:* o fardo segue sem dono; o armazém continua cheio. *Liga:* `clock.fardos`. *Fonte:* doc 01 §2.3 (problemas, rumores); doc 06 OV-03, CV-03.
- **Grande — Carga parada na barra** (`seed.fozbreu.g`, ameaça 3): A Casa da Barra se prepara para suspender a carga sem papel até haver regra provisória; Rusk precisa de quem redija a regra com os Pactuários da Foz sem decidir o que a Descida traz.  
  *Agentes:* `npc.rusk-rede-da-barra`, `faction.defensores-da-paz`, `faction.pilotos-dos-canais`. *Abordagens possíveis:* propor regra provisória de papel; ouvir os pilotos da barra; separar carga de visitantes sem registro. *Se ninguém age:* a suspensão começa sem regra; o que a Descida traz continua `desconhecido`. *Liga:* `event.fardos-suspensos`. *Fonte:* doc 05 §1 (L-04) e §3; doc 06 CV-03, CF-03.

### `city.beira-sal` — Beira-Sal

- **Pequena — O guia que voltou sem sede** (`seed.beira-sal.p`, ameaça 1): Vurga Sede-Vencida ouve de novo o rumor do guia que sumiu no Ermo e voltou sem sede e pede que alguém escute as versões entre os guias, sem afirmar que seja verdade.  
  *Agentes:* `npc.vurga-sede-vencida`. *Abordagens possíveis:* ouvir os guias na Casa dos Guias de Sal; comparar as versões; perguntar na Feira da Beira. *Se ninguém age:* pode ser só história de guia; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.4 (rumores).
- **Média — Um acerto sem prova** (`seed.beira-sal.m`, ameaça 2): Vurga pede escolta para uma caravana e alguém que testemunhe um acerto oral no Pátio dos Acertos.  
  *Agentes:* `npc.vurga-sede-vencida`, `npc.teshi-no-de-poco`, `faction.guias-de-sal`, `faction.casa-dos-pocos`. *Abordagens possíveis:* escoltar a caravana; testemunhar o acerto; propor registro no Pátio. *Se ninguém age:* o acerto fica só na palavra; a conta segue contestada. *Liga:* `clock.acertos-orais`. *Fonte:* doc 01 §2.4 (problemas); doc 06 OV-04, CV-04; doc 05 §1 (L-05).
- **Grande — Pátio sem acertos orais** (`seed.beira-sal.g`, ameaça 3): O Pátio dos Acertos se prepara para exigir testemunha de ambos os lados e suspender acertos orais sem prova; Vurga e Teshi precisam de quem faça a transição sem decidir o valor dos pactos já feitos.  
  *Agentes:* `npc.vurga-sede-vencida`, `npc.teshi-no-de-poco`, `faction.guias-de-sal`, `faction.casa-dos-pocos`. *Abordagens possíveis:* listar acertos em curso; recrutar testemunhas neutras; vistoriar os poços sinalizados. *Se ninguém age:* a suspensão vale sem aviso aos guias; o valor de um pacto oral já feito segue `desconhecido`. *Liga:* `event.acertos-orais-suspensos`. *Fonte:* doc 05 §1 (L-05); doc 06 CV-04, CF-04.

### `city.selovale` — Selovale

- **Pequena — A pedra sem nome** (`seed.selovale.p`, ameaça 1): Yeshe Forma-Emprestada ouve de novo o rumor de uma pedra de selo sem nome e pede que alguém confira o Círculo das Pedras de Selo contra o registro, sem afirmar que exista.  
  *Agentes:* `npc.yeshe-forma-emprestada`. *Abordagens possíveis:* contar as pedras com um escriba; comparar com o Arquivo dos Pactos Velhos; perguntar na Casa das Cláusulas. *Se ninguém age:* as contas batem ou não; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.5 (rumores).
- **Média — A cópia mais antiga** (`seed.selovale.m`, ameaça 2): Yeshe pede que alguém ache a cópia mais antiga de um texto contestado entre os Pactuários.  
  *Agentes:* `npc.yeshe-forma-emprestada`, `npc.garrun-toca-funda`, `faction.memoria-das-provacoes`. *Abordagens possíveis:* procurar no Arquivo dos Pactos Velhos; comparar cópias na Casa das Cláusulas; registrar a cópia segundo a Lei das Cláusulas. *Se ninguém age:* a divergência segue sem cópia de referência. *Liga:* `clock.pacto-que-vence`. *Fonte:* doc 01 §2.5 (problemas); doc 06 OV-05, CV-05; doc 05 §1 (L-08).
- **Grande — Sessão pública de parecer** (`seed.selovale.g`, ameaça 3): O Conselho dos Selos se prepara para abrir sessão pública de parecer sobre o pacto que vence; Yeshe precisa de quem confira o texto sem prejulgar o que o fim concede.  
  *Agentes:* `npc.yeshe-forma-emprestada`, `faction.memoria-das-provacoes`. *Abordagens possíveis:* conferir as versões do texto com os Pactuários; ler a cláusula como a Lei das Cláusulas permite; levar o parecer a Morrovéu só como parecer. *Se ninguém age:* a sessão ocorre sem conferência; o parecer não é decidido aqui e não é executado por ele. *Liga:* `event.sessao-publica-de-parecer`. *Fonte:* doc 04 §3; doc 05 §1 (L-08); doc 06 CV-05.

### `city.borda-mansa` — Borda-Mansa

- **Pequena — Uma noite de chegadas** (`seed.borda-mansa.p`, ameaça 1): Krell Eco-da-Vigia pede que alguém vigie uma noite de chegadas na Cerca Mansa e registre quem vem.  
  *Agentes:* `npc.krell-eco-da-vigia`, `npc.szia-fio-aberto`. *Abordagens possíveis:* vigiar com as fogueiras de sinal; anotar no Pátio dos Sem Guia; ouvir os vigias de turno. *Se ninguém age:* ninguém é registrado; a noite passa como as outras. *Liga:* ligação não provada. *Fonte:* doc 01 §2.6 (problemas); doc 06 OV-06.
- **Média — A névoa fecha a orla** (`seed.borda-mansa.m`, ameaça 2): A névoa cobre a orla e Krell quer quem ajude a vigiar o que a Torre das Fogueiras já não vê.  
  *Agentes:* `npc.krell-eco-da-vigia`, `faction.vigias-da-orla`. *Abordagens possíveis:* reforçar as fogueiras de sinal; combinar rondas com os vigias; perguntar a quem chegou sem guia. *Se ninguém age:* a vigia vê só o que a névoa deixa; mais gente pode chegar sem registro. *Liga:* `event.neblina-cobre-a-orla`. *Fonte:* doc 01 §2.6 (defesas); doc 08 DR-09; doc 06 CV-06.
- **Grande — Registrar fere o abrigo?** (`seed.borda-mansa.g`, ameaça 3): Os Pactuários da Vigia se preparam para debater em audiência se registrar quem chega fere o abrigo; Krell precisa de quem apresente a situação dos dois lados sem decidir.  
  *Agentes:* `npc.krell-eco-da-vigia`, `npc.szia-fio-aberto`, `faction.vigias-da-orla`. *Abordagens possíveis:* reunir relatos de quem chegou; mostrar o custo do abrigo sem lista; apresentar L-03 aos Pactuários. *Se ninguém age:* a audiência ocorre sem relato de quem chega; o resultado não é decidido aqui. *Liga:* `event.audiencia-sobre-registro`. *Fonte:* doc 05 §1 (L-03); doc 06 CV-06, CF-06.

### `city.fio-longo` — Fio-Longo

- **Pequena — O fio que guarda o sonho** (`seed.fio-longo.p`, ameaça 1): Ondra Fio-de-Sono ouve de novo o rumor de que o fio de um tecido guarda o sonho de quem o tece e pede que alguém veja de onde o boato vem entre os hóspedes, sem afirmar que seja verdade.  
  *Agentes:* `npc.ondra-fio-de-sono`, `npc.rhek-relato-manso`. *Abordagens possíveis:* conversar com tecelões no Tear Grande; ouvir hóspedes no Salão das Camas Longas; comparar as versões. *Se ninguém age:* pode ser só conversa de hóspede; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.7 (rumores).
- **Média — Um hóspede e seus parentes** (`seed.fio-longo.m`, ameaça 2): Ondra pede que alguém ouça um hóspede e seus parentes sem tomar partido.  
  *Agentes:* `npc.ondra-fio-de-sono`, `faction.casa-dos-fios`. *Abordagens possíveis:* ouvir o hóspede a sós; ouvir os parentes; propor audiência com os dois lados. *Se ninguém age:* o pedido dos parentes fica sem resposta; a Casa mantém que é escolha do hóspede. *Liga:* `clock.hospedes`. *Fonte:* doc 01 §2.7 (problemas); doc 06 OV-07, CV-07; doc 05 §1 (L-06).
- **Grande — Caso de hóspede em julgamento** (`seed.fio-longo.g`, ameaça 3): A Casa dos Fios se prepara para abrir julgamento formal de um caso de hóspede; Ondra precisa de quem junte o que cada lado diz sem decidir se há quebra da Lei do Repouso.  
  *Agentes:* `npc.ondra-fio-de-sono`, `faction.casa-dos-fios`. *Abordagens possíveis:* reunir os depoimentos; conferir a Lei do Repouso (L-06); pedir mediação antes do julgamento. *Se ninguém age:* o julgamento abre com um lado só; o resultado fica a registrar. *Liga:* `event.julgamento-de-hospede`. *Fonte:* doc 05 §1 (L-06); doc 06 CV-07, CF-07.

### `city.candeia-morta` — Candeia-Morta

- **Pequena — A candeia que acende de longe** (`seed.candeia-morta.p`, ameaça 1): Dhun Contorno-Quieto ouve de novo que certas candeias só acendem quando alguém morre longe e pede que alguém anote quando uma acende na Praça das Candeias, sem afirmar que seja verdade.  
  *Agentes:* `npc.dhun-contorno-quieto`. *Abordagens possíveis:* anotar com os vigias de turno; comparar as anotações; perguntar na Casa das Despedidas. *Se ninguém age:* pode ser coincidência; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.8 (rumores).
- **Média — Uma vigília e o Muro** (`seed.candeia-morta.m`, ameaça 2): Dhun pede que alguém acompanhe uma vigília e confira uma anotação do Muro dos Nomes.  
  *Agentes:* `npc.dhun-contorno-quieto`, `npc.ilkar-turno-aberto`, `faction.irmandade-das-despedidas`, `faction.turnos-da-vigilia`. *Abordagens possíveis:* acompanhar a vigília; conferir a anotação com os Pactuários; registrar sem tirar conclusão. *Se ninguém age:* a anotação fica sem conferência; nada muda. *Liga:* `clock.despedidas`. *Fonte:* doc 01 §2.8 (rumores); doc 06 OV-08, CV-08.
- **Grande — A divisão em voz alta** (`seed.candeia-morta.g`, ameaça 4): A divisão entre os Pactuários das Despedidas está para ser dita em voz alta na Casa das Despedidas; Dhun e Ilkar precisam de quem ajude a conduzir a conversa sem decidir o que L-07 pede de um rei necromante.  
  *Agentes:* `npc.dhun-contorno-quieto`, `npc.ilkar-turno-aberto`, `faction.irmandade-das-despedidas`, `faction.turnos-da-vigilia`. *Abordagens possíveis:* mediar a reunião dos Pactuários; garantir que os turnos não fechem; ouvir cada lado antes da sessão. *Se ninguém age:* a divisão é dita sem mediação; a resposta não é decidida aqui e a lei segue aberta. *Liga:* `event.divisao-dita-em-voz-alta`. *Fonte:* doc 05 §1 (L-07); doc 06 CV-08, CF-08.

## Conferências

| Verificação | Resultado |
|---|---|
| Sementes | 24 (8 P, 8 M, 8 G) |
| Lugares com as três faixas | 8 de 8 |
| Agente fora do registro mestre ou falecido | 0 |
| Relógio ou evento fora de `12-relogios.csv` e `12-eventos.csv` | 0 |
| Campo vazio | 0 |
| Semente que resolve mistério, lei ou lacuna | 0 |
| Fronteira (Aeltharyn, Coroas, Marcas das Feras, Brumafunda, Neblina Longa) | 0 |

## Decisão pendente

**D-89:** aprovar as 24 sementes. Opções: **A.** aprovar; **B.** trocar, tirar ou somar uma semente (diga qual). **Recomendação: A.**
