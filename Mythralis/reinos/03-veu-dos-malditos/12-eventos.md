# 12 — Eventos-tipo e cadeias causais (R3-5.2)

**Estado:** rascunho de R3-5.2 para aprovação do autor (D-88) · **Data:** 2026-11-06
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, lugar, facção, relógio, rota ou fato novo**. Dados em `12-eventos.csv` (10 colunas, molde do reino 2; `cidades` no lugar de `cidades_e_assentamentos`, pois o reino não tem assentamentos, D-66).
**Fonte:** docs 01, 04, 05, 06, 07 e 08; `12-relogios.csv`, `12-fichas.csv`. Os 8 relógios dão os gatilhos; as dependências DR-01 a DR-09 e as leis L-01 a L-09 dão os elos. Coluna `recursos`: termos qualitativos do doc 08 §3 (o reino 3 não tem ID de recurso, D-81).

## Regras

- Cada evento é uma **possibilidade causal**, não um fato nem uma obrigação. Só acontece com o **gatilho observável** da coluna (os dos relógios vêm de `12-relogios.csv`). Pistas e escolhas vêm antes das consequências graves.
- **Encadeamento: ASSUMINDO.** Cada elo repete algo já documentado (quem paga, ao completar, lei, dependência); a ordem entre eles é leitura editorial.
- Nenhum evento envolve Aeltharyn, as Coroas, as Marcas das Feras ou a Neblina Longa, nem dá duração a rota (toda duração segue `desconhecida`, D-79). O resultado de L-01, L-07 e L-09, o que a Descida traz e o que o fim do pacto antigo concede continuam `desconhecidos`.
- 12 eventos: 8 que completam um relógio (CV-01 a CV-08), 2 que só fornecem gatilho a relógio (`cheia-fora-de-epoca`, `neblina-cobre-a-orla`) e 2 sem relógio (`tributo-que-nao-basta`, `turno-sem-cobertura`).

## Eventos

### `event.conselho-vota-em-publico` — Conselho dos Pactuários obrigado a votar em público
- **Gatilho:** nova inscrição travada ou nova tomada pública de partido observada
- **Relógio:** `clock.desafiantes`
- **Cadeia:** inscrição que não chega ao registro ou Pactuário que toma partido em público > o Conselho se divide entre quem defende o rei que trouxe a paz e quem lembra que o rei deve ser vencido > Cerrane convoca a sessão (preside e não vota) > o Conselho vota em público se chama o rei a responder (doc 04 §3) > o resultado não é decidido aqui e a Provação segue só pela regra (L-01).
- **Lugares:** `city.morroveu`
- **Recursos (termos do doc 08):** tributo em pactos; escribas e registro
- **Pessoas e facções:** `npc.morvan-voz-baixa`, `npc.orsk-cicatriz-de-bando`, `npc.cerrane-heranca-firme`, `faction.guarda-do-rei`, `faction.defensores-da-paz`, `faction.memoria-das-provacoes`
- **Rotas:** `route.conselho-morroveu`
- **Fonte:** doc 06 CV-01, CF-01; doc 04 §2 a §5 e §10 item 1; doc 05 §1 (L-01); encadeamento: ASSUMINDO

### `event.julgamento-de-quem-guia` — Julgamento formal de quem guia os que partem
- **Gatilho:** nova acusação contra quem guia ou nova família que parte observada
- **Relógio:** `clock.partida`
- **Cadeia:** família pede guia e o pacto manda ficar (L-09) > quem guia é acusado de quebrar o pacto > Tazk e os pilotos arcam com a acusação; as famílias de pacto antigo ficam sem saída segura > os Pactuários dos Canais abrem julgamento formal, com a pena `desconhecida` e o efeito dentro de Lamaluz > os Pilotos dos Canais também estão em Fozbreu (DR-05) e a acusação vira pressão sobre eles lá, sem julgamento fora de Lamaluz.
- **Lugares:** `city.lamaluz`, `city.fozbreu`
- **Recursos (termos do doc 08):** pilotagem
- **Pessoas e facções:** `npc.tazk-pedra-ao-sol`, `npc.nuvik-canal-torto`, `faction.pilotos-dos-canais`, `faction.guardioes-das-lamparinas`
- **Rotas:** `route.lamaluz-fozbreu`
- **Fonte:** doc 06 CV-02, CF-02; doc 04 §6 e §9.2; doc 05 §1 (L-09); doc 08 DR-05; encadeamento: ASSUMINDO

### `event.fardos-suspensos` — Casa da Barra suspende carga sem papel
- **Gatilho:** novo fardo sem papel ou novo visitante sem registro observado
- **Relógio:** `clock.fardos`
- **Cadeia:** carga sem papel que o pacto reconheça chega com a Descida > o Armazém dos Fardos Sem Dono lota > a Casa da Barra suspende a recepção de carga sem papel até haver regra provisória > carga e visitantes ficam parados em Fozbreu, onde o rio termina (DR-03) > os Pactuários da Foz decidem a regra provisória; o que a Descida traz continua `desconhecido`.
- **Lugares:** `city.fozbreu`
- **Recursos (termos do doc 08):** Descida do Argente; pilotagem da barra
- **Pessoas e facções:** `npc.rusk-rede-da-barra`, `faction.defensores-da-paz`, `faction.pilotos-dos-canais`
- **Rotas:** `route.fozbreu-argente`
- **Fonte:** doc 06 CV-03, CF-03; doc 05 §1 (L-04) e §3; doc 08 DR-03; encadeamento: ASSUMINDO

### `event.acertos-orais-suspensos` — Pátio dos Acertos suspende acertos orais sem prova
- **Gatilho:** nova cobrança contestada ou novo sinal de poço observado
- **Relógio:** `clock.acertos-orais`
- **Cadeia:** guia cobra por pacto oral sem prova ou caravana contesta a conta > o atrito entre guias e caravanas chega à Casa dos Poços > o Pátio dos Acertos passa a exigir testemunha de ambos os lados e suspende acertos orais sem prova > a Rota do Sal, de que Beira-Sal depende (DR-04), passa a exigir testemunha em cada travessia > o valor de um pacto oral já feito segue `desconhecido` (L-05).
- **Lugares:** `city.beira-sal`
- **Recursos (termos do doc 08):** sal; água; guias; acertos
- **Pessoas e facções:** `npc.vurga-sede-vencida`, `npc.teshi-no-de-poco`, `faction.guias-de-sal`, `faction.casa-dos-pocos`
- **Rotas:** `route.beira-sal-ermo`
- **Fonte:** doc 06 CV-04, CF-04; doc 05 §1 (L-05); doc 08 DR-04; encadeamento: ASSUMINDO

### `event.sessao-publica-de-parecer` — Conselho dos Selos abre sessão pública de parecer
- **Gatilho:** nova invocação de cláusula ou nova divergência observada entre versões
- **Relógio:** `clock.pacto-que-vence`
- **Cadeia:** alguém invoca uma cláusula esquecida (L-08) > os Pactuários discordam em público sobre qual versão do texto vale > o Conselho dos Selos abre sessão pública de parecer, sem poder de execução (doc 04 §3) > Morrovéu, que depende dos pactos antigos de Selovale (DR-06), acompanha pela Memória das Provações > o parecer não está decidido aqui e o que o fim do pacto concede segue `desconhecido`.
- **Lugares:** `city.selovale`, `city.morroveu`
- **Recursos (termos do doc 08):** arquivo de pactos; parecer
- **Pessoas e facções:** `npc.yeshe-forma-emprestada`, `faction.memoria-das-provacoes`
- **Rotas:** `route.morroveu-selovale`
- **Fonte:** doc 06 CV-05, CF-05; doc 04 §3 e §10 item 5; doc 05 §1 (L-08); doc 08 DR-06; encadeamento: ASSUMINDO

### `event.audiencia-sobre-registro` — Audiência sobre registrar quem chega à orla
- **Gatilho:** nova chegada sem guia ou nova passagem sem registro observada
- **Relógio:** `clock.orla-sem-guia`
- **Cadeia:** chegadas sem guia e sem registro se repetem, ou alguém passa com ajuda de dentro (observado) > os Vigias da Orla e quem chega pagam o custo do abrigo sem lista > os Pactuários da Vigia debatem em audiência se registrar quem chega fere o abrigo (L-03) > o resultado não é decidido aqui.
- **Lugares:** `city.borda-mansa`
- **Recursos (termos do doc 08):** vigia; guarda de quem chega
- **Pessoas e facções:** `npc.krell-eco-da-vigia`, `faction.vigias-da-orla`
- **Rotas:** nenhuma
- **Fonte:** doc 06 CV-06, CF-06; doc 05 §1 (L-03); encadeamento: ASSUMINDO

### `event.julgamento-de-hospede` — Julgamento formal de um caso de hóspede
- **Gatilho:** novo pedido formal de parente ou nova recusa observada de hóspede
- **Relógio:** `clock.hospedes`
- **Cadeia:** parente cobra a volta de um hóspede e o hóspede não quer ouvir > a Casa dos Fios, os parentes e o hóspede pagam em confiança > a Casa abre julgamento formal de um caso (L-06), sem decidir se há quebra > a origem dos hóspedes segue `desconhecida` (DR-07) e o resultado fica a registrar.
- **Lugares:** `city.fio-longo`
- **Recursos (termos do doc 08):** hospedagem de repouso
- **Pessoas e facções:** `npc.ondra-fio-de-sono`, `faction.casa-dos-fios`
- **Rotas:** nenhuma
- **Fonte:** doc 06 CV-07, CF-07; doc 05 §1 (L-06); doc 08 DR-07; encadeamento: ASSUMINDO

### `event.divisao-dita-em-voz-alta` — A divisão das Despedidas é dita em voz alta
- **Gatilho:** novo pedido da Corte observado ou nova divergência pública
- **Relógio:** `clock.despedidas`
- **Cadeia:** pedido da Corte que o pacto pode proibir ou divergência pública entre Pactuários > a Irmandade das Despedidas, os Turnos da Vigília e a cidade, frágil de dia, pagam o silêncio quebrado > a divisão é dita em voz alta na Casa das Despedidas > os Pactuários-mor precisam responder o que L-07 pede de um rei necromante; a resposta não é decidida aqui e a lei segue aberta.
- **Lugares:** `city.candeia-morta`, `city.morroveu`
- **Recursos (termos do doc 08):** cuidado dos mortos; guarda de portões
- **Pessoas e facções:** `npc.dhun-contorno-quieto`, `npc.ilkar-turno-aberto`, `npc.morvan-voz-baixa`, `faction.irmandade-das-despedidas`, `faction.turnos-da-vigilia`
- **Rotas:** nenhuma
- **Fonte:** doc 06 CV-08, CF-08; doc 04 §9; doc 05 §1 (L-07); encadeamento: ASSUMINDO

### `event.tributo-que-nao-basta` — Tributo em pactos que não basta
- **Gatilho:** Conselho constata que um lugar entregou menos do que o pacto produz (observado em cena)
- **Relógio:** nenhum
- **Cadeia:** um lugar entrega menos do que o seu pacto produz > Morrovéu, que depende do tributo dos outros lugares (DR-01), sente a falta na Corte e na guarda > o Pactuário-mor do lugar é chamado ao Conselho > o Conselho não pode votar contra o texto do pacto (L-02) > o que acontece se um lugar falha segue `desconhecido` (doc 08 §5).
- **Lugares:** `city.morroveu`
- **Recursos (termos do doc 08):** tributo em pactos
- **Pessoas e facções:** `npc.morvan-voz-baixa`, `npc.cerrane-heranca-firme`, `npc.orsk-cicatriz-de-bando`
- **Rotas:** `route.conselho-morroveu`
- **Fonte:** doc 05 §1 (L-02) e §2; doc 08 DR-01, DR-02 e §5; doc 04 §7; encadeamento: ASSUMINDO

### `event.cheia-fora-de-epoca` — Cheia ou seca fora de época em Lamaluz
- **Gatilho:** cheia ou seca fora de época observada nos canais de Lamaluz
- **Relógio:** `clock.partida`
- **Cadeia:** cheia ou seca fora de época > os canais mudam de lugar e as passarelas recolhem > a pilotagem vira condição para qualquer travessia (DR-05) > mais famílias pedem guia e a acusação contra quem guia ganha causa observável (gatilho de `clock.partida`) > nenhuma pena é decidida (L-09).
- **Lugares:** `city.lamaluz`
- **Recursos (termos do doc 08):** juncos; peixe de lama; pilotagem
- **Pessoas e facções:** `npc.tazk-pedra-ao-sol`, `npc.nuvik-canal-torto`, `faction.pilotos-dos-canais`
- **Rotas:** nenhuma
- **Fonte:** doc 06 CV-02; doc 01 parte 1 §2.2; doc 05 §2; doc 08 §3 e DR-05; encadeamento: ASSUMINDO

### `event.neblina-cobre-a-orla` — Névoa cobre a orla de Borda-Mansa
- **Gatilho:** névoa cobrindo a orla observada em cena
- **Relógio:** `clock.orla-sem-guia`
- **Cadeia:** a névoa cobre a orla > o alcance da vigia cai, pois só vê o que a névoa deixa (DR-09) > Krell e a Torre das Fogueiras perdem o controle de quem chega > mais chegadas sem guia passam sem registro (gatilho de `clock.orla-sem-guia`) > a Lei do Abrigo segue valendo (L-03); o que há além da orla não é afirmado.
- **Lugares:** `city.borda-mansa`
- **Recursos (termos do doc 08):** vigia; fogueiras de sinal
- **Pessoas e facções:** `npc.krell-eco-da-vigia`, `faction.vigias-da-orla`
- **Rotas:** nenhuma
- **Fonte:** doc 06 CV-06; doc 01 parte 1 §2.6; doc 08 DR-09; encadeamento: ASSUMINDO

### `event.turno-sem-cobertura` — Turno de vigia fica sem cobertura em Candeia-Morta
- **Gatilho:** turno de vigia sem cobertura observado
- **Relógio:** nenhum
- **Cadeia:** um turno de vigia fica sem cobertura > a cidade, frágil de dia, fica sem guarda de portões e de luz fria (DR-08) > o cuidado dos mortos e das despedidas fica exposto > Ilkar e os Turnos da Vigília respondem e a Irmandade das Despedidas é chamada a reforçar > a Lei dos Mortos segue como está (L-07); se a necromancia a viola segue aberto.
- **Lugares:** `city.candeia-morta`
- **Recursos (termos do doc 08):** ofícios de noite; guarda de portões; cuidado dos mortos
- **Pessoas e facções:** `npc.ilkar-turno-aberto`, `npc.dhun-contorno-quieto`, `faction.turnos-da-vigilia`, `faction.irmandade-das-despedidas`
- **Rotas:** nenhuma
- **Fonte:** doc 08 §3 e DR-08; doc 01 parte 1 §2.8; doc 05 §1 (L-07); encadeamento: ASSUMINDO

## Conferências

| Verificação | Resultado |
|---|---|
| Eventos | 12 (meta 10 a 15) |
| Ligados a relógio / sem relógio | 10 / 2 |
| ID de lugar, pessoa ou facção fora do registro mestre | 0 |
| ID de relógio fora de `12-relogios.csv` | 0 |
| ID de rota fora do doc 07 | 0 |
| Relógios com ao menos um evento | 8 de 8 |
| Campo vazio | 0 |
| Fronteira (Aeltharyn, Coroas, Marcas das Feras, Neblina Longa) | 0 |

## Decisão pendente

**D-88:** aprovar os 12 eventos e as cadeias. Opções: **A.** aprovar; **B.** trocar, tirar ou somar um evento (diga qual). **Recomendação: A.**
