# 12 — Eventos-tipo e cadeias causais (R4-5.2)

**Estado:** R4-5.2, D-125 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, lugar, facção, relógio, rota ou fato novo**. Dados em `12-eventos.csv` (10 colunas, molde dos reinos 2 e 3; `cidades` no lugar de `cidades_e_assentamentos`, pois o reino não tem assentamentos, D-102).
**Fonte:** docs 01, 04, 05, 06, 07, 08 e 09; `12-relogios.csv`, `12-fichas.csv`. Os 8 relógios dão os gatilhos; as dependências DR-01 a DR-12 e as leis L-01 a L-10 dão os elos. Coluna `recursos`: termos qualitativos do doc 09 (o reino 4 não tem ID de recurso, D-118).

## Regras

- Cada evento é uma **possibilidade causal**, não um fato nem uma obrigação. Só acontece com o **gatilho observável** da coluna (os dos relógios vêm de `12-relogios.csv`). Pistas e escolhas vêm antes das consequências graves.
- **Encadeamento: ASSUMINDO.** Cada elo repete algo já documentado (quem paga, ao completar, lei, dependência); a ordem entre eles é leitura editorial.
- Só entram **rotas entre dois lugares do Vale** que existem no doc 07; toda duração segue `desconhecida` (D-116). As ligações com o rio acima, o Ermo de Sal e as elevações não entram em nenhum evento.
- Nenhum evento envolve Aeltharyn, as Coroas, o Véu ou o Pacto Humano, nem a Neblina Longa, o Mistério D, o Mistério E ou o Conflito G. O resultado de L-01, L-03, L-04 a L-09, o que a Descida traz e o que há do outro lado das elevações continuam `desconhecidos`.
- 12 eventos: 8 que completam um relógio (CP-01 a CP-08), 2 que só fornecem gatilho a relógio (`pasto-fraco-na-estacao`, `caravanas-juntas-na-cisterna`) e 2 sem relógio (`terreiro-cheio`, `celeiro-guardado-por-poucos`).

## Eventos

### `event.assembleia-diz-o-que-o-celeiro-deve` — A Assembleia é chamada a dizer o que o celeiro deve a quem chega
- **Gatilho:** novo pedido de reserva observado; nova recusa de entrega observada
- **Relógio:** `clock.reserva-em-ano-ruim`
- **Cadeia:** clã de fora pede abrigo e reserva em ano de pasto fraco > os guardiões dizem que a reserva é de quem a juntou (L-07) > Pocha abre a reserva dela a quem o celeiro não atende, sem abrir a reserva comum > Moura mantém a ordem de entrega e a Mesa da Partilha leva o pedido ao Terreiro-Maior (DR-02) > a Assembleia é chamada a dizer o que o celeiro deve a quem chega; o direito à reserva não é decidido aqui.
- **Lugares:** `city.tabuleiro-velho`, `city.terreiro-maior`
- **Recursos (termos do doc 09):** reserva; grão e carne seca; couro e lã
- **Pessoas e facções:** `npc.moura-mel-guardado`, `npc.pocha-ninhada-farta`, `npc.yorna-palavra-firme`, `faction.guardioes-da-reserva`, `faction.mesa-da-partilha`
- **Rotas:** `route.tabuleiro-terreiro-maior`
- **Fonte:** doc 06 CP-01, CFP-01; doc 04 §3 e §10; doc 05 §1 (L-07); doc 08 DR-02, DR-09; encadeamento: ASSUMINDO

### `event.quem-recebe-a-passagem` — Um dos grupos ou a Assembleia diz a quem se paga a passagem
- **Gatilho:** nova travessia contestada observada
- **Relógio:** `clock.dupla-cobranca`
- **Cadeia:** quem atravessa paga duas vezes ou nenhuma > um grupo contesta em público a cobrança do outro (L-04) > os rebanhos param na margem e Quira leva recados entre os dois > o Trato do Vau apela à Assembleia (doc 04 §6) > um dos grupos ou a Assembleia diz em voz alta a quem se paga; a quem pertencem as pedras não é decidido aqui.
- **Lugares:** `city.rasovau`, `city.terreiro-maior`
- **Recursos (termos do doc 09):** troca; acerto; vau transitável
- **Pessoas e facções:** `npc.zinu-troca-justa`, `npc.quira-recado-certo`, `npc.lumbo-pedra-de-baixo`, `faction.pedras-de-cima`, `faction.pedras-de-baixo`
- **Rotas:** nenhuma
- **Fonte:** doc 06 CP-02, CFP-02; doc 04 §6, §7 e §9; doc 05 §1 (L-04); doc 08 DR-07; encadeamento: ASSUMINDO

### `event.regra-da-rampa` — O Trato do Desembarque diz quem cobra e que papel se reconhece
- **Gatilho:** nova carga contestada observada
- **Relógio:** `clock.desembarque`
- **Cadeia:** carga chega com papel que o Vale não conhece (L-05) > Receptores e Balança cobram critérios diferentes na mesma rampa > a carga fica guardada no Barracão até o acerto e Vrissk aceita carga sem conferir de onde vem > clãs vizinhos que protegem o pouso (DR-05) arcam com a espera > o Trato do Desembarque diz quem cobra e o que se reconhece como papel; a Descida continua sem causa, sem número e sem fim rio acima.
- **Lugares:** `city.praia-pardo`, `city.rasovau`
- **Recursos (termos do doc 09):** recepção; acerto; guarda de carga
- **Pessoas e facções:** `npc.vrissk-colonia-quieta`, `npc.grebb-balanca-justa`, `faction.receptores-do-pouso`, `faction.balanca-do-pouso`
- **Rotas:** `route.praia-pardo-rasovau`
- **Fonte:** doc 06 CP-03, CFP-03; doc 04 §7 e §9; doc 05 §1 (L-05) e §3; doc 08 DR-05, DR-06; encadeamento: ASSUMINDO

### `event.preferencia-na-cisterna` — O Trato do Poço diz quem tem preferência na cisterna
- **Gatilho:** nova espera de caravana observada; nova recusa de água observada
- **Relógio:** `clock.cisterna`
- **Cadeia:** duas caravanas chegam à cisterna ao mesmo tempo > uma delas é recusada ou espera além do que o pacto manda (L-06) > Gruzk mantém os turnos e Aruun atende em ordem > caravanas e clãs do pouso arcam com a espera (DR-08) > o Trato do Poço diz quem tem preferência; a regra não é decidida aqui e nada se afirma sobre o Ermo de Sal.
- **Lugares:** `city.alvasal`
- **Recursos (termos do doc 09):** sal; água; acerto de caravana
- **Pessoas e facções:** `npc.gruzk-estandarte-baixo`, `npc.aruun-poco-compartilhado`, `faction.vigias-da-cisterna`
- **Rotas:** nenhuma
- **Fonte:** doc 06 CP-04, CFP-04; doc 04 §8 e §10 item 4; doc 05 §1 (L-06) e §4; doc 08 DR-08; encadeamento: ASSUMINDO

### `event.declaracao-no-fogo-do-meio` — A Assembleia declara se vale prova ou aclamação
- **Gatilho:** nova divergência pública entre prova e aclamação observada; novo adiamento do anúncio observado
- **Relógio:** `clock.sucessao-do-capitao`
- **Cadeia:** Clãs da Prova e Clãs da Aclamação divergem em público ou Kharos adia o anúncio > Yorna ordena a reunião e Cassan hospeda sem tomar lado na Ramada (L-01) > a Marcha fica sem rumo se o mandato acabar sem sucessor (L-03) > a Assembleia declara, no Fogo do Meio, se vale prova ou aclamação; a escolha não é decidida aqui e o interregno segue como saída possível.
- **Lugares:** `city.terreiro-maior`, `city.vazante`
- **Recursos (termos do doc 09):** hospedagem; mediação; marcha
- **Pessoas e facções:** `npc.yorna-palavra-firme`, `npc.cassan-galhada-velha`, `npc.kharos-casco-de-marcha`, `faction.clas-da-prova`, `faction.clas-da-aclamacao`
- **Rotas:** `route.terreiro-vazante`
- **Fonte:** doc 06 CP-05, CFP-05; doc 04 §5 e §10 item 1; doc 05 §1 (L-01, L-03); doc 08 DR-02, DR-12; encadeamento: ASSUMINDO

### `event.escolha-do-campo` — O Capitão escolhe o campo diante dos clãs
- **Gatilho:** nova queixa de clã sem pasto observada; novo relato de batedor observado
- **Relógio:** `clock.campo-da-marcha`
- **Cadeia:** batedores trazem notícias diferentes dos dois campos ou um clã sem pasto reclama ao Capitão > Rukhan aconselha e os Tratos dos campos são consultados > a reserva do Tabuleiro-Velho serve de retaguarda (DR-01) > o Capitão escolhe o campo diante dos clãs; qualquer escolha custa a algum clã e a ordem que parte um clã segue como lacuna (L-03).
- **Lugares:** `city.vazante`, `city.tabuleiro-velho`
- **Recursos (termos do doc 09):** marcha; caça; pasto; reserva
- **Pessoas e facções:** `npc.kharos-casco-de-marcha`, `npc.rukhan-retorno-certo`, `faction.guardas-da-marcha`
- **Rotas:** `route.tabuleiro-vazante`
- **Fonte:** doc 06 CP-06, CFP-06; doc 04 §3 e §9; doc 05 §1 (L-03); doc 08 DR-01, DR-10; encadeamento: ASSUMINDO

### `event.guia-combinado` — O Trato da Encosta diz quem guia e quem fica com o que sobra
- **Gatilho:** novo rebanho em espera observado; nova recusa de espera observada
- **Relógio:** `clock.guia-das-trilhas`
- **Cadeia:** rebanho espera guia no pouso ou os Vigias das Trilhas recusam a espera > Tessun guia sem tomar partido e a pastagem de espera segura o rebanho (L-09) > rebanhos parados e Guias da Encosta arcam com a espera > o Rasovau, lugar-mãe (DR-04), é o apoio político do pouso, sem entrega descrita > o Trato da Encosta diz quem guia e quem fica com o que sobra; não decide o que há do outro lado.
- **Lugares:** `city.encosta-aberta`, `city.rasovau`
- **Recursos (termos do doc 09):** pouso; pastagem de espera; guia
- **Pessoas e facções:** `npc.tessun-salto-calado`, `npc.sendro-clareira-vigia`, `faction.guias-da-encosta`, `faction.vigias-das-trilhas`
- **Rotas:** `route.rasovau-encosta-aberta`
- **Fonte:** doc 06 CP-07, CFP-07; doc 04 §9 e §10 item 5; doc 05 §1 (L-09) e §5; doc 08 DR-04; encadeamento: ASSUMINDO

### `event.divisao-do-campo` — O Trato dos Rebanhos divide o campo entre gado e caça
- **Gatilho:** nova contestação de marca observada; nova acusação de estrago observada
- **Relógio:** `clock.capim-disputado`
- **Cadeia:** marca ou mourão contestado, ou acusação pública de estrago do pasto > Tibbe divide o campo e a batida é combinada fora da parição (L-08) > Ghorla sustenta a caça sem acordo e os dois lados perdem parte do campo > os marcos do Tabuleiro-Velho dão a base do acerto (DR-03) > o Trato dos Rebanhos divide o campo diante dos dois lados; quem tem razão sobre o capim não é decidido aqui.
- **Lugares:** `city.gramal`, `city.tabuleiro-velho`
- **Recursos (termos do doc 09):** gado; couro; caça; marca dos rebanhos
- **Pessoas e facções:** `npc.tibbe-salto-curto`, `npc.ghorla-fartura-dividida`, `faction.clas-do-gado`, `faction.clas-da-caca`
- **Rotas:** `route.tabuleiro-gramal`
- **Fonte:** doc 06 CP-08, CFP-08; doc 04 §9 e §10 item 6; doc 05 §1 (L-08); doc 08 DR-03, DR-11; encadeamento: ASSUMINDO

### `event.pasto-fraco-na-estacao` — Estação de pasto fraco nos campos
- **Gatilho:** pasto fraco observado na estação
- **Relógio:** `clock.reserva-em-ano-ruim`, `clock.campo-da-marcha`, `clock.capim-disputado`
- **Cadeia:** o pasto rende pouco na estação (condição, não data) > clãs de fora pedem reserva no Tabuleiro-Velho (gatilho de CP-01) > a Vazante pesa dois campos (gatilho de CP-06) > em Gramal a disputa entre gado e caça esquenta depressa (gatilho de CP-08) > cada relógio só avança se o seu próprio gatilho já existe na cena; nenhum avança por esta condição.
- **Lugares:** `city.tabuleiro-velho`, `city.vazante`, `city.gramal`
- **Recursos (termos do doc 09):** pasto; reserva
- **Pessoas e facções:** `npc.moura-mel-guardado`, `npc.kharos-casco-de-marcha`, `npc.tibbe-salto-curto`
- **Rotas:** `route.tabuleiro-vazante`, `route.tabuleiro-gramal`
- **Fonte:** doc 06 CP-01, CP-06, CP-08; doc 01 parte 1; doc 08 DR-09, DR-10, DR-11; encadeamento: ASSUMINDO

### `event.caravanas-juntas-na-cisterna` — Duas caravanas na cisterna ao mesmo tempo
- **Gatilho:** duas caravanas na cisterna ao mesmo tempo
- **Relógio:** `clock.cisterna`
- **Cadeia:** duas caravanas chegam ao Pátio das Cargas > a cisterna não serve às duas ao mesmo tempo (DR-08) > a cerca de sal e os turnos de vigia seguram a fila > o gatilho de CP-04 existe na cena; recusa ou queixa só aparecem se acontecerem.
- **Lugares:** `city.alvasal`
- **Recursos (termos do doc 09):** água; carga; acerto de caravana
- **Pessoas e facções:** `npc.gruzk-estandarte-baixo`, `npc.aruun-poco-compartilhado`, `faction.vigias-da-cisterna`
- **Rotas:** nenhuma
- **Fonte:** doc 06 CP-04; doc 01 parte 1 §2.4; doc 05 §4; doc 08 DR-08; encadeamento: ASSUMINDO

### `event.terreiro-cheio` — Terreiro cheio e frágil na reunião dos clãs
- **Gatilho:** terreiro cheio de clãs hospedados observado
- **Relógio:** nenhum
- **Cadeia:** muitos clãs se reúnem no Terreiro-Maior > hospedagem e forragem da Ramada apertam (DR-12) > Cassan hospeda sem tomar lado e Yorna mantém a ordem (L-01) > o terreiro cheio é frágil e sem defesa própria; a reserva do Tabuleiro-Velho sustenta a reunião (DR-02) > nada se decide; a situação só pesa sobre a Assembleia.
- **Lugares:** `city.terreiro-maior`, `city.tabuleiro-velho`
- **Recursos (termos do doc 09):** hospedagem; forragem; mediação
- **Pessoas e facções:** `npc.cassan-galhada-velha`, `npc.yorna-palavra-firme`
- **Rotas:** `route.tabuleiro-terreiro-maior`
- **Fonte:** doc 09 (Terreiro-Maior); doc 05 §2; doc 08 DR-02, DR-12; encadeamento: ASSUMINDO

### `event.celeiro-guardado-por-poucos` — Celeiro guardado por poucos quando os clãs partem
- **Gatilho:** clãs em marcha e celeiro com poucos guardas observados
- **Relógio:** nenhum
- **Cadeia:** os clãs partem em marcha > o Tabuleiro-Velho fica guardado por poucos (doc 09) > Moura mantém a reserva e o muro (L-07) > os clãs que entregam a reserva (DR-09) deixam de estar presentes > a vulnerabilidade pesa sobre o celeiro; não prova colapso e nenhum ataque é escrito.
- **Lugares:** `city.tabuleiro-velho`
- **Recursos (termos do doc 09):** reserva; muro; guarda dos marcos
- **Pessoas e facções:** `npc.moura-mel-guardado`, `faction.guardioes-da-reserva`
- **Rotas:** nenhuma
- **Fonte:** doc 09 (Tabuleiro-Velho); doc 01 parte 1 §2.1; doc 05 §1 (L-07); doc 08 DR-09; encadeamento: ASSUMINDO

## Conferências

| Verificação | Resultado |
|---|---|
| Eventos | 12 (meta 10 a 15) |
| Ligados a relógio / sem relógio | 10 / 2 |
| ID de lugar, pessoa ou facção fora do registro mestre | 0 |
| ID de relógio fora de `12-relogios.csv` | 0 |
| ID de rota fora do doc 07; rota com ponta fora do Vale | 0; 0 |
| Relógios com ao menos um evento | 8 de 8 |
| Campo vazio | 0 |
| Fronteira (Aeltharyn, Coroas, Véu, Pacto Humano, Neblina Longa) | 0 |
| Número, preço ou duração | 0 |

## Decisão pendente

**D-125:** aprovar os 12 eventos e as cadeias. Opções: **A.** aprovar; **B.** trocar, tirar ou somar um evento (diga qual). **Recomendação: A.**
