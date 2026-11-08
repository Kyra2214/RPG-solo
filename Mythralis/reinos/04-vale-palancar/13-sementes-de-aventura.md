# 13 — Sementes de aventura (R4-5.3)

**Estado:** R4-5.3, D-126 aceita pela continuação · **Data:** 2026-11-07

**Etiqueta:** tudo é **[PROPOSTO]**; semente é situação jogável, não fato canônico. **Fonte:** docs 01 (parte 1), 04, 05 e 06; `12-relogios.csv`, `12-eventos.csv`, `12-registro-mestre.csv`. Dados em `13-sementes-de-aventura.csv` (11 colunas, molde dos reinos 2 e 3).

## Regras

- 8 lugares × 3 sementes = **24**: uma **pequena (P)**, uma **média (M)** e uma **grande (G)**. ID: `seed.<lugar>.<p|m|g>`. O reino não tem assentamentos (D-102). As médias são as oito oportunidades OP-01 a OP-08 do doc 06; as pequenas nascem dos rumores do doc 01; as grandes, dos problemas atuais.
- Semente é **situação jogável**, não fato canônico. Não altera estado, relógio, lei, pacto ou relação até ser resolvida por ação, regra ou evento validado.
- **Sem solução predefinida:** *abordagens possíveis* são caminhos, não respostas; nenhuma semente escolhe resposta de mistério (MPL-01 a MPL-10) nem decide L-01, L-03, L-04 a L-09, a regra de escolha do Capitão, o direito à reserva, a posse das pedras, quem cobra o desembarque, a preferência na cisterna, quem tem razão sobre o capim, o que a Descida traz ou o que há do outro lado das elevações.
- **Cada semente tem "se ninguém age":** resultado proporcional, sem punição automática; relógio só avança por causa observável (`12-relogios.md` §1).
- **Só entidades existentes:** agentes são pessoas vivas e facções do registro mestre; nenhum NPC novo; nenhum falecido como agente. Clãs e grupos sem nome são cenário, não personagem.
- **Ameaça** (1 a 4, ASSUMINDO, mesma escala dos reinos 2 e 3) mede o **risco para o personagem**: 1 incômodo ou risco baixo; 2 moderado; 3 sério ou envolve autoridade; 4 alto, pode mudar uma instituição ou um relógio. Faixas: P 1, M 2–3, G 3–4; nenhuma semente tem nível 5.
- **Liga** só com relógio ou evento-tipo já escritos; "ligação não provada" nas pequenas, que partem de rumor.
- Nenhum tempo, distância, rota, número, população ou preço novo. Nada sobre Aeltharyn, as Coroas, o Véu ou o Pacto Humano; onde o rumor fala do outro lado das elevações, fica só rumor.

## Resumo

| Faixa | Sementes | Ameaça 1 | 2 | 3 | 4 |
|---|---|---|---|---|---|
| Pequena | 8 | 8 | 0 | 0 | 0 |
| Média | 8 | 0 | 7 | 1 | 0 |
| Grande | 8 | 0 | 0 | 7 | 1 |

Total: **24** sementes; 16 com ligação a relógio ou evento (as 16 médias e grandes); as 8 pequenas, sem ligação escrita.

## Lugares

### `city.tabuleiro-velho` — Tabuleiro-Velho

- **Pequena — A câmara mais fria** (`seed.tabuleiro-velho.p`, ameaça 1): Pocha Ninhada-Farta ouviu dizer que o celeiro tem uma câmara mais fria que o resto e pede que alguém olhe, sem afirmar que haja segredo.  
  *Agentes:* `npc.pocha-ninhada-farta`. *Abordagens possíveis:* olhar o Celeiro Comprido; ouvir os guardiões; perguntar à Mesa da Partilha. *Se ninguém age:* pode ser só a pedra do celeiro; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.1 (rumores); MPL-02.
- **Média — Um marco que ninguém reconhece** (`seed.tabuleiro-velho.m`, ameaça 2): Moura Mel-Guardado pede que alguém confira no Campo dos Marcos um marco que nenhum clã reconhece, sem decidir de quem é.  
  *Agentes:* `npc.moura-mel-guardado`. *Abordagens possíveis:* ler o Campo dos Marcos; ouvir os guardiões e a Mesa da Partilha; perguntar a cada clã de pasto pactuado. *Se ninguém age:* o marco segue sem dono; a conta de quem tem direito à reserva não muda. *Liga:* `clock.reserva-em-ano-ruim`, `event.assembleia-diz-o-que-o-celeiro-deve`. *Fonte:* doc 06 OP-01, CFP-01; MPL-01.
- **Grande — Quem tem direito à reserva** (`seed.tabuleiro-velho.g`, ameaça 3): Em ano de pasto fraco, clãs de fora pedem abrigo e os guardiões dizem que a reserva é de quem a juntou; Moura e Pocha precisam de quem leve os dois lados à Assembleia sem decidir o direito.  
  *Agentes:* `npc.moura-mel-guardado`, `npc.pocha-ninhada-farta`, `faction.guardioes-da-reserva`, `faction.mesa-da-partilha`. *Abordagens possíveis:* ouvir os clãs de fora e os guardiões; levar o pedido ao Terreiro-Maior; acordar uma ordem de entrega provisória. *Se ninguém age:* o pedido segue sem resposta e a Mesa distribui o que pode; o relógio só avança com novo pedido ou nova recusa. *Liga:* `clock.reserva-em-ano-ruim`, `event.assembleia-diz-o-que-o-celeiro-deve`, `event.pasto-fraco-na-estacao`. *Fonte:* doc 01 §2.1 (problemas); doc 04 §10; doc 05 §1 (L-07).

### `city.rasovau` — Rasovau

- **Pequena — O recado adiantado** (`seed.rasovau.p`, ameaça 1): Quira Recado-Certo ouviu que um recado chegou a quem ainda não tinha chegado e pede que alguém anote quando isso acontecer, sem dizer que é verdade.  
  *Agentes:* `npc.quira-recado-certo`. *Abordagens possíveis:* anotar chegadas e recados na Tenda dos Recados; comparar as anotações; perguntar no Cais de Cordas. *Se ninguém age:* pode ser coincidência; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.2 (rumores); MPL-03.
- **Média — Um recado entre os dois grupos** (`seed.rasovau.m`, ameaça 2): Quira pede que alguém leve um recado entre os dois grupos que cobram sem ser tomado por espião.  
  *Agentes:* `npc.quira-recado-certo`, `npc.zinu-troca-justa`, `npc.lumbo-pedra-de-baixo`. *Abordagens possíveis:* levar o recado inteiro; ouvir cada grupo antes; deixar o recado na Tenda e não responder por ele. *Se ninguém age:* o recado fica na Tenda; nenhum dos grupos muda de posição. *Liga:* `clock.dupla-cobranca`, `event.quem-recebe-a-passagem`. *Fonte:* doc 06 OP-02, CFP-02; MPL-03.
- **Grande — Duas cobranças, um vau** (`seed.rasovau.g`, ameaça 3): Quem atravessa paga duas vezes ou nenhuma e os dois grupos contestam a cobrança em público; o Trato do Vau não tem chefe aceito e precisa de quem ajude a dizer a quem se paga sem decidir a quem pertencem as pedras.  
  *Agentes:* `npc.zinu-troca-justa`, `npc.lumbo-pedra-de-baixo`, `npc.quira-recado-certo`, `faction.pedras-de-cima`, `faction.pedras-de-baixo`. *Abordagens possíveis:* mediar um acerto na Tenda dos Recados; levar o apelo à Assembleia; garantir a travessia dos rebanhos parados. *Se ninguém age:* a travessia segue contestada; o relógio só avança com nova travessia contestada. *Liga:* `clock.dupla-cobranca`, `event.quem-recebe-a-passagem`. *Fonte:* doc 01 §2.2 (problemas); doc 04 §6, §8 e §10; doc 05 §1 (L-04).

### `city.praia-pardo` — Praia-Pardo

- **Pequena — Quem desce sem ter subido** (`seed.praia-pardo.p`, ameaça 1): Vrissk Colônia-Quieta pede que alguém anote quem desce na rampa e o que diz sobre a chegada, sem tomar o rumor por fato.  
  *Agentes:* `npc.vrissk-colonia-quieta`. *Abordagens possíveis:* anotar chegadas na rampa; comparar as anotações; ouvir os clãs vizinhos. *Se ninguém age:* o rumor fica rumor; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.3 (rumores); MPL-04.
- **Média — Uma carga sem origem dita** (`seed.praia-pardo.m`, ameaça 2): Vrissk pede que alguém acompanhe o acerto de uma carga com papel que o Vale não conhece, sem perguntar de onde vem.  
  *Agentes:* `npc.vrissk-colonia-quieta`, `npc.grebb-balanca-justa`. *Abordagens possíveis:* acompanhar o acerto no Barracão; ouvir Receptores e Balança; registrar só o que o pouso vê. *Se ninguém age:* a carga fica guardada no Barracão; nada se resolve. *Liga:* `clock.desembarque`, `event.regra-da-rampa`. *Fonte:* doc 06 OP-03, CFP-03; MPL-04.
- **Grande — Quem cobra a rampa** (`seed.praia-pardo.g`, ameaça 3): Receptores e Balança cobram critérios diferentes na mesma rampa e a carga espera; o Trato do Desembarque precisa de quem ajude a dizer quem cobra sem decidir o que a Descida traz.  
  *Agentes:* `npc.vrissk-colonia-quieta`, `npc.grebb-balanca-justa`, `faction.receptores-do-pouso`, `faction.balanca-do-pouso`. *Abordagens possíveis:* mediar entre os dois critérios; propor regra provisória de guarda; ouvir os clãs vizinhos que protegem o pouso. *Se ninguém age:* a carga segue no Barracão; o relógio só avança com nova carga contestada. *Liga:* `clock.desembarque`, `event.regra-da-rampa`. *Fonte:* doc 01 §2.3 (problemas); doc 04 §7 e §10; doc 05 §1 (L-05) e §3.

### `city.alvasal` — Alvasal

- **Pequena — A água mais salgada** (`seed.alvasal.p`, ameaça 1): Aruun Poço-Compartilhado pede que alguém prove a água da Cisterna Branca antes e depois de uma caravana, sem afirmar que a caravana a mudou.  
  *Agentes:* `npc.aruun-poco-compartilhado`. *Abordagens possíveis:* provar a água em dois momentos; ouvir os turnos; perguntar às caravanas. *Se ninguém age:* pode ser variação natural; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.4 (rumores); MPL-05.
- **Média — Duas caravanas, uma cisterna** (`seed.alvasal.m`, ameaça 2): Gruzk Estandarte-Baixo pede que alguém escolte duas caravanas até a cisterna sem que briguem por água.  
  *Agentes:* `npc.gruzk-estandarte-baixo`, `npc.aruun-poco-compartilhado`. *Abordagens possíveis:* combinar a ordem com as duas; apoiar os turnos de vigia; ouvir a queixa de cada lado. *Se ninguém age:* a fila se forma e a espera pesa; ninguém decide a preferência. *Liga:* `clock.cisterna`, `event.caravanas-juntas-na-cisterna`. *Fonte:* doc 06 OP-04, CFP-04; MPL-05.
- **Grande — Quem tem preferência na água** (`seed.alvasal.g`, ameaça 3): A cisterna não serve a todas as caravanas ao mesmo tempo e a preferência nunca foi posta em pacto; o Trato do Poço precisa de quem ajude a dizer quem tem preferência sem decidir a regra.  
  *Agentes:* `npc.gruzk-estandarte-baixo`, `npc.aruun-poco-compartilhado`, `faction.vigias-da-cisterna`. *Abordagens possíveis:* ouvir caravanas e clãs do pouso; propor atendimento em ordem; levar o caso à Assembleia. *Se ninguém age:* a espera segue; o relógio só avança com nova espera ou recusa observada. *Liga:* `clock.cisterna`, `event.preferencia-na-cisterna`, `event.caravanas-juntas-na-cisterna`. *Fonte:* doc 01 §2.4 (problemas); doc 04 §8 e §10 item 4; doc 05 §1 (L-06).

### `city.terreiro-maior` — Terreiro-Maior

- **Pequena — A Pedra que esfria** (`seed.terreiro-maior.p`, ameaça 1): Yorna Palavra-Firme pede que alguém anote quando a Pedra da Palavra esfria numa reunião, sem dizer que isso prova mentira.  
  *Agentes:* `npc.yorna-palavra-firme`. *Abordagens possíveis:* anotar o que se diz quando a Pedra esfria; comparar as anotações; ouvir Cassan na Ramada. *Se ninguém age:* pode ser coincidência; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.5 (rumores); MPL-06.
- **Média — O clã que não quer entrar na rodada** (`seed.terreiro-maior.m`, ameaça 3): Yorna pede que alguém ouça um clã que não quer entrar na rodada e leve a palavra dele ao Fogo do Meio.  
  *Agentes:* `npc.yorna-palavra-firme`, `npc.cassan-galhada-velha`. *Abordagens possíveis:* ouvir o clã na Ramada; levar a palavra sem mudá-la; pedir a Cassan hospedagem neutra. *Se ninguém age:* o clã fica de fora e a Assembleia perde uma voz. *Liga:* `clock.sucessao-do-capitao`, `event.declaracao-no-fogo-do-meio`. *Fonte:* doc 06 OP-05, CFP-05; MPL-06, MPL-08.
- **Grande — Prova ou aclamação** (`seed.terreiro-maior.g`, ameaça 4): O mandato do Capitão perto do fim divide a Assembleia entre prova e aclamação; Yorna e Cassan precisam de quem ajude a manter a rodada inteira sem escolher o método nem o sucessor.  
  *Agentes:* `npc.yorna-palavra-firme`, `npc.cassan-galhada-velha`, `npc.kharos-casco-de-marcha`, `faction.clas-da-prova`, `faction.clas-da-aclamacao`. *Abordagens possíveis:* manter todos ouvidos no Fogo do Meio; hospedar sem tomar lado; levar a Kharos o rumor dos clãs. *Se ninguém age:* as posições endurecem; o relógio só avança com nova divergência pública ou novo adiamento. *Liga:* `clock.sucessao-do-capitao`, `event.declaracao-no-fogo-do-meio`, `event.terreiro-cheio`. *Fonte:* doc 01 §2.5 (problemas); doc 04 §5 e §10 item 1; doc 05 §1 (L-01, L-03).

### `city.vazante` — Vazante

- **Pequena — Os pousos da Marcha** (`seed.vazante.p`, ameaça 1): Rukhan Retorno-Certo pede que alguém anote onde a Marcha pousa, só para conferir o rumor de que nunca dorme duas vezes no mesmo campo.  
  *Agentes:* `npc.rukhan-retorno-certo`. *Abordagens possíveis:* anotar cada pouso; comparar com os relatos dos batedores; ouvir o Trato dos campos. *Se ninguém age:* pode ser só costume; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.6 (rumores); MPL-07.
- **Média — Bater um campo** (`seed.vazante.m`, ameaça 2): Rukhan pede que alguém bata um campo com ele e volte com o que viu, sem decidir por ele.  
  *Agentes:* `npc.rukhan-retorno-certo`. *Abordagens possíveis:* bater o campo com ele; anotar pasto e caça; entregar o relato inteiro ao Capitão. *Se ninguém age:* o campo fica sem relato e a escolha segue em aberto. *Liga:* `clock.campo-da-marcha`, `event.escolha-do-campo`. *Fonte:* doc 06 OP-06, CFP-06; MPL-07.
- **Grande — Dois campos, um rumo** (`seed.vazante.g`, ameaça 3): A marcha precisa escolher entre dois campos e cada escolha deixa um clã sem pasto; Kharos e Rukhan precisam de quem ajude a ouvir os clãs sem escolher o campo no lugar do Capitão.  
  *Agentes:* `npc.kharos-casco-de-marcha`, `npc.rukhan-retorno-certo`, `faction.guardas-da-marcha`. *Abordagens possíveis:* ouvir o clã sem pasto; consultar os Tratos dos campos; levar a reserva do Tabuleiro-Velho como retaguarda. *Se ninguém age:* a partida adia; o relógio só avança com nova queixa ou novo relato de batedor. *Liga:* `clock.campo-da-marcha`, `event.escolha-do-campo`, `event.pasto-fraco-na-estacao`. *Fonte:* doc 01 §2.6 (problemas); doc 04 §3 e §9; doc 05 §1 (L-03).

### `city.encosta-aberta` — Encosta-Aberta

- **Pequena — O rumo trocado** (`seed.encosta-aberta.p`, ameaça 1): Tessun Salto-Calado pede que alguém acompanhe um guia de volta ao pouso e anote o que ele diz do que ouviu lá em cima, sem tomar o rumor por fato.  
  *Agentes:* `npc.tessun-salto-calado`. *Abordagens possíveis:* acompanhar o guia; anotar só o que ele disser; comparar com o que outros guias contam. *Se ninguém age:* o rumor fica rumor; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.7 (rumores); MPL-09.
- **Média — Um rebanho até o pé das elevações** (`seed.encosta-aberta.m`, ameaça 2): Tessun pede que alguém guie um rebanho até o pé das elevações sem tomar o lado de nenhum dos clãs.  
  *Agentes:* `npc.tessun-salto-calado`, `npc.sendro-clareira-vigia`. *Abordagens possíveis:* guiar sem tomar partido; usar a pastagem de espera; parar onde a Lei da Trilha para. *Se ninguém age:* o rebanho fica esperando no pouso. *Liga:* `clock.guia-das-trilhas`, `event.guia-combinado`. *Fonte:* doc 06 OP-07, CFP-07; MPL-09.
- **Grande — Quem guia e quem fica com o que sobra** (`seed.encosta-aberta.g`, ameaça 3): Dois grupos disputam quem guia o rebanho e quem fica com o que sobra no pouso; o Trato da Encosta não tem chefe aceito e precisa de quem ajude a combinar sem decidir o que há do outro lado.  
  *Agentes:* `npc.tessun-salto-calado`, `npc.sendro-clareira-vigia`, `faction.guias-da-encosta`, `faction.vigias-das-trilhas`. *Abordagens possíveis:* combinar guia e pastagem partilhada; ouvir os dois grupos; manter as trilhas abertas. *Se ninguém age:* os rebanhos esperam; o relógio só avança com novo rebanho em espera ou nova recusa. *Liga:* `clock.guia-das-trilhas`, `event.guia-combinado`. *Fonte:* doc 01 §2.7 (problemas); doc 04 §8 e §10 item 5; doc 05 §1 (L-09).

### `city.gramal` — Gramal

- **Pequena — O capim em círculo** (`seed.gramal.p`, ameaça 1): Tibbe Salto-Curto pede que alguém olhe um campo onde o capim cresce em círculo, sem dizer o que isso significa.  
  *Agentes:* `npc.tibbe-salto-curto`. *Abordagens possíveis:* olhar o campo; ouvir gado e caça; anotar o que cada lado diz. *Se ninguém age:* pode ser só o capim; nada muda. *Liga:* ligação não provada. *Fonte:* doc 01 §2.8 (rumores); MPL-10.
- **Média — A marca e o mourão** (`seed.gramal.m`, ameaça 2): Tibbe pede que alguém testemunhe a marca de um rebanho e um mourão contestado, ouvindo os dois lados.  
  *Agentes:* `npc.tibbe-salto-curto`, `npc.ghorla-fartura-dividida`. *Abordagens possíveis:* ver a marca na Tenda de Marcas; ouvir os dois lados; registrar sem dizer quem tem razão. *Se ninguém age:* a marca segue contestada. *Liga:* `clock.capim-disputado`, `event.divisao-do-campo`. *Fonte:* doc 06 OP-08, CFP-08; MPL-10.
- **Grande — Gado e caça no mesmo campo** (`seed.gramal.g`, ameaça 3): Clãs de gado e de caça dizem que o mesmo campo é deles; Tibbe e Ghorla precisam de quem ajude a dividir o campo por estação sem dizer quem tem razão sobre o capim.  
  *Agentes:* `npc.tibbe-salto-curto`, `npc.ghorla-fartura-dividida`, `faction.clas-do-gado`, `faction.clas-da-caca`. *Abordagens possíveis:* combinar a divisão por estação; fixar a batida fora da parição; ouvir os dois lados na Tenda de Marcas. *Se ninguém age:* a disputa esquenta com o pasto fraco; o relógio só avança com nova contestação ou acusação. *Liga:* `clock.capim-disputado`, `event.divisao-do-campo`, `event.pasto-fraco-na-estacao`. *Fonte:* doc 01 §2.8 (problemas); doc 04 §9 e §10 item 6; doc 05 §1 (L-08).

## Conferências

| Verificação | Resultado |
|---|---|
| Sementes | 24 (8 P, 8 M, 8 G) |
| Lugares com as três faixas | 8 de 8 |
| Agente fora do registro mestre ou falecido | 0 |
| Relógio ou evento fora de `12-relogios.csv` e `12-eventos.csv` | 0 |
| Campo vazio | 0 |
| Semente que resolve mistério, lei ou lacuna | 0 |
| Fronteira (Aeltharyn, Coroas, Véu, Pacto Humano) | 0 |
| Número, preço ou duração | 0 |

## Decisão pendente

**D-126:** aprovar as 24 sementes. Opções: **A.** aprovar; **B.** trocar, tirar ou somar uma semente (diga qual). **Recomendação: A.**
