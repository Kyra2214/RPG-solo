# 12 — Eventos-tipo e cadeias causais (M4.2)

**Fonte:** doc 01 §25.3 (4 cadeias prontas), §26 (efeito dos 7 relógios), §5.1, §19.1 e §28; doc 03, 04, 07; `12-recursos.csv`. Dados em `12-eventos.csv`.

**Regras:** cada evento é uma **possibilidade causal**, não um fato nem uma obrigação (doc 01 §25.3). Só acontece com gatilho observável (doc 06 §7). Pistas e escolhas vêm antes de consequências graves. Os eventos `dique-rompido`, `corte-ilegal`, `navio-quarentena`, `previsao-falha`, `corredor-fechado`, `pacto-suspenso`, `mina-rompe` e `rede-resina` vêm do texto; os outros cinco combinam dependências já escritas e marcam o encadeamento como ASSUMINDO. Nenhum fato novo de mundo foi criado (ex.: quais duas cidades suspendem o pacto fica *a definir*).

## Eventos

### `event.dique-rompido` — Dique rompido em Nymvar
- **Gatilho:** dique ou comporta cede (reparo adiado e cheia)
- **Relógio:** `clock.dikes`
- **Cadeia:** inundação local em Nymvar > perda de sementes em Arven > preço dos alimentos sobe em Elarion e Silvaran > pedido de importação acelerada > pressão para cortar madeira em Vael-Tir.
- **Cidades e assentamentos:** `city.nymvar`, `city.arven`, `city.elarion`, `city.silvaran`, `city.vael-tir`, `settlement.junco-alto`
- **Recursos:** graos, madeira
- **Pessoas e facções:** `npc.elsin-varzea-clara`, `npc.beren-tres-canais`, `npc.edris-do-sul`, `npc.maereth-vaelorian`
- **Rotas:** —
- **Fonte:** doc 01 §25.3 (cadeia 1)

### `event.corte-ilegal` — Corte ilegal em Vael-Tir
- **Gatilho:** corte próximo a raízes ou acima da cota
- **Relógio:** `clock.heart-trees`
- **Cadeia:** erosão e lama no afluente > água turva em Nymvar > alertas miceliais em Orvass > suspeita contra curandeiros e madeireiros > disputa no Conselho das Sete Copas.
- **Cidades e assentamentos:** `city.vael-tir`, `city.nymvar`, `city.orvass`, `city.elarion`, `settlement.copa-funda`
- **Recursos:** madeira, agua
- **Pessoas e facções:** `npc.saelith-arco-de-chuva`, `npc.voz-muitas-raizes`, `npc.vaunor-eir`, `faction.vigias-primeiro-verde`, `faction.circulo-micelial`
- **Rotas:** —
- **Fonte:** doc 01 §25.3 (cadeia 2)

### `event.navio-quarentena` — Quarentena do navio sem tripulação em Thalanor
- **Gatilho:** prova ignorada ou carga sem registro; porto fecha descarga
- **Relógio:** `clock.ghost-ship`
- **Cadeia:** quarentena reduz descarga em Thalanor > mercadorias atrasam > artesãos de Torvelume ficam sem insumos > Liga das Rotas pressiona pela reabertura > famílias de náufragos exigem investigação.
- **Cidades e assentamentos:** `city.thalanor`, `city.torvelume`, `settlement.ponta-da-concha`, `settlement.farol-de-ambar`
- **Recursos:** minerio, madeira
- **Pessoas e facções:** `npc.syranna-das-mares`, `npc.yara-sal-de-mare`, `npc.terenna-luz-de-lastro`, `faction.liga-das-rotas`
- **Rotas:** `route.thalanor-elarion`, `route.thalanor-silvaran`, `route.nymvar-thalanor`
- **Fonte:** doc 01 §25.3 (cadeia 3)

### `event.previsao-falha` — Falha nas previsões de Caladwen
- **Gatilho:** estudiosos erram janela de plantio por mudança de clima de causa desconhecida
- **Relógio:** —
- **Cadeia:** plantio atrasado em Arven > menos excedente para outras cidades > Silvaran reduz festivais e refeições públicas > a população culpa estudiosos ou o calendário.
- **Cidades e assentamentos:** `city.caladwen`, `city.arven`, `city.silvaran`
- **Recursos:** graos
- **Pessoas e facções:** `npc.selvaris-noctel`, `npc.orelia-ceu-de-cinza`, `npc.tessa-folhinha`, `faction.liga-celeiros`, `faction.circulo-estacoes`
- **Rotas:** —
- **Fonte:** doc 01 §25.3 (cadeia 4)

### `event.rota-celeiros-fechada` — Rota dos Celeiros fechada
- **Gatilho:** lama, cheia ou bloqueio fecha Nymvar–Arven (rota de 2 a 4 dias por caravana)
- **Relógio:** —
- **Cadeia:** sementes de Arven não chegam a Nymvar > Nymvar perde produção de grãos > Arven fica sem os canais e a água de Nymvar > Elarion, que depende dos dois, sente o abastecimento (ASSUMINDO: encadeamento a partir das dependências de grãos e água).
- **Cidades e assentamentos:** `city.nymvar`, `city.arven`, `city.elarion`
- **Recursos:** graos, agua
- **Pessoas e facções:** `npc.elsin-varzea-clara`, `npc.tessa-folhinha`, `faction.liga-celeiros`, `faction.liga-das-rotas`
- **Rotas:** `route.nymvar-arven`
- **Fonte:** doc 01 §25 e §25.1; doc 07; 12-recursos.csv

### `event.rei-adoece` — Rei adoece
- **Gatilho:** condição de saúde do rei (event-tipo; causa não definida)
- **Relógio:** —
- **Cadeia:** sucessão e regência entram em pauta > blocos do conselho se rearranjam > cidades testam a autoridade da Coroa (ASSUMINDO: leitura das regras de sucessão do doc 04 §6).
- **Cidades e assentamentos:** `city.elarion`
- **Recursos:** —
- **Pessoas e facções:** `npc.aerendyl-vaelorian`, `npc.maereth-vaelorian`, `npc.saelith-arco-de-chuva`, `npc.lethariel-nascente-clara`
- **Rotas:** —
- **Fonte:** doc 04 §6; doc 01 §4.1, §4.3 e §28

### `event.corredor-fechado` — Corredor centauro fechado
- **Gatilho:** cerca nova, colheita fora de época ou patrulha sem consulta
- **Relógio:** `clock.centaur-corridors`
- **Cadeia:** rota sazonal fecha > caravanas desviam > Liga das Rotas pressiona pela estrada > centauros bloqueiam trechos > Vael-Tir e Passo do Veado ficam no meio.
- **Cidades e assentamentos:** `city.vael-tir`, `settlement.passo-do-veado`
- **Recursos:** —
- **Pessoas e facções:** `npc.oranna-passo-largo`, `npc.calar-passo-curvo`, `npc.eryndra-folha-verde`, `faction.liga-das-rotas`
- **Rotas:** —
- **Fonte:** doc 01 §13 (Conflito G) e §26; doc 03 §5.2

### `event.pacto-suspenso` — Duas cidades suspendem uma obrigação do pacto
- **Gatilho:** tributo desigual, rumor sem resposta ou socorro injusto
- **Relógio:** `clock.city-distrust`
- **Cadeia:** duas cidades suspendem uma obrigação do Pacto das Dez Cidades > o conselho debate > a Coroa teme precedente fiscal > cidades vizinhas reavaliam suas obrigações (quais cidades: a definir; ASSUMINDO candidata: Nymvar).
- **Cidades e assentamentos:** `city.nymvar`, `city.elarion`
- **Recursos:** —
- **Pessoas e facções:** `npc.edris-do-sul`, `npc.maereth-vaelorian`, `npc.aerendyl-vaelorian`
- **Rotas:** —
- **Fonte:** doc 01 §26 e §28; doc 04 caso A

### `event.mina-rompe` — Acesso da mina selada se rompe
- **Gatilho:** escavação clandestina, tremores ou sabotagem
- **Relógio:** `clock.sealed-mine`
- **Cadeia:** acesso se rompe em Porta Selada > perigo real para trabalhadores > Torvelume interdita e investiga > ferragens atrasam para as cidades dependentes > o que existe abaixo continua desconhecido.
- **Cidades e assentamentos:** `city.torvelume`, `settlement.porta-selada`, `settlement.ferro-folha`, `city.elarion`, `city.nymvar`, `city.vael-tir`, `city.arven`, `city.orvass`
- **Recursos:** minerio
- **Pessoas e facções:** `npc.dauren-galho-de-ferro`, `npc.rovan-cinza-de-ferro`, `npc.meda-martelo-calmo`, `faction.guarda-espinha`
- **Rotas:** —
- **Fonte:** doc 01 §26; 12-recursos.csv (minério)

### `event.rede-resina` — Rede organizada de resina
- **Gatilho:** licenças falsas, patrulhas desviadas e demanda elevada
- **Relógio:** `clock.resin-smuggling`
- **Cadeia:** surge uma rede de extração organizada > Vigias e Copa-Funda reforçam patrulha > suspeita recai sobre Companhia do Âmbar e Liga das Rotas, sem prova > cotas de madeira e resina ficam em disputa.
- **Cidades e assentamentos:** `city.vael-tir`, `city.thalanor`, `settlement.copa-funda`
- **Recursos:** resina, madeira
- **Pessoas e facções:** `npc.maelor-venn`, `npc.tavian-lume-fino`, `npc.arieth-tres-mudas`, `npc.saelith-arco-de-chuva`, `faction.companhia-ambar`, `faction.liga-das-rotas`, `faction.vigias-primeiro-verde`
- **Rotas:** —
- **Fonte:** doc 01 §26 e §13 (Conflito H)

### `event.ponte-cede` — Ponte antiga cede em Elarion
- **Gatilho:** rachaduras ignoradas (relatórios de manutenção adiados por disputa orçamentária)
- **Relógio:** —
- **Cadeia:** uma ponte antiga cede > evacuação pelas rotas de serviço > Avariel Thorne admite o erro ou o oculta > Parel Três-Parafusos e os moradores treinados assumem reparos > cresce a desconfiança de quem vive nas copas inferiores.
- **Cidades e assentamentos:** `city.elarion`, `settlement.sete-degraus`
- **Recursos:** —
- **Pessoas e facções:** `npc.avariel-thorne`, `npc.parel-tres-parafusos`, `npc.maereth-cartografa-das-copas`, `npc.avarra-sete-pontes`
- **Rotas:** —
- **Fonte:** doc 01 §5.1 e §19.1; fichas de Elarion

### `event.arquivo-sinistro` — Incêndio ou sabotagem no Arquivo das Seivas
- **Gatilho:** fogo ou sabotagem (vulnerabilidades de Elarion, doc 01 §5.1)
- **Relógio:** —
- **Cadeia:** registros em risco > Nimeth prioriza a retirada por valor histórico > documentos comuns se perdem > Arquivistas e Saelith revisam a custódia > o sumiço de documentos da Guerra das Cinzas Verdes fica ainda mais difícil de rastrear.
- **Cidades e assentamentos:** `city.elarion`
- **Recursos:** —
- **Pessoas e facções:** `npc.nimeth-vela-chuva`, `npc.lethariel-nascente-clara`, `npc.nara-tres-fios`, `npc.saelith-arco-de-chuva`, `faction.arquivistas-seiva`
- **Rotas:** —
- **Fonte:** doc 01 §5.1 e §19.1; fichas de Elarion

### `event.mudas-raras` — Coroa exige as mudas raras de Raiz-de-Prata
- **Gatilho:** a Coroa insiste na entrega para restaurar áreas nobres
- **Relógio:** —
- **Cadeia:** Raiz-de-Prata entrega as mudas raras ou recusa > sementes locais saem ou ficam > a próxima estação de plantio fica ameaçada ou a disputa com a Coroa cresce.
- **Cidades e assentamentos:** `settlement.raiz-de-prata`, `city.elarion`
- **Recursos:** —
- **Pessoas e facções:** `npc.nivara-folha-de-orvalho`, `npc.lethariel-nascente-clara`
- **Rotas:** —
- **Fonte:** doc 03 §2.1; doc 01 §29

