# CATALOGO_ESTILOS_RPG.md

Fonte: documento "Catálogo de Estilos, Sistemas e Criação de Personagens" (seções 3 a 7). Os IDs abaixo são os mesmos de `rules/src/main/resources/catalog/estilos.txt` (carregados e testados).

**Observações de taxonomia:**
- Duplicatas exatas removidas: "Exploração de masmorras" (ficou só em Estruturas) e "Reinos de cultivo" (ficou só em Progressão).
- "Cinematográfico" existe em Tom e em Filosofia de regras: são dimensões diferentes, IDs distintos.
- Wuxia, Xianxia e Xuanhuan são entradas separadas, não sinônimos.
- Sobreposições conceituais (ex.: "Intriga política" como gênero e "Intriga política e social" como estrutura) foram mantidas, pois pertencem a dimensões diferentes.
- Dificuldade, tom, complexidade de regras e limites de conteúdo são configurações independentes.

O catálogo aceita entradas novas e misturas: basta acrescentar uma linha no arquivo, sem mudar código.


## Gêneros e ambientações (85)


**fantasia** (13)

- `setting.fantasia-medieval` Fantasia medieval
- `setting.fantasia-epica` Fantasia épica
- `setting.alta-fantasia` Alta fantasia
- `setting.fantasia-sombria` Fantasia sombria
- `setting.fantasia-heroica` Fantasia heroica
- `setting.fantasia-de-baixa-magia` Fantasia de baixa magia
- `setting.fantasia-urbana` Fantasia urbana
- `setting.fantasia-mitologica` Fantasia mitológica
- `setting.fantasia-historica` Fantasia histórica
- `setting.espada-e-feiticaria` Espada e feitiçaria
- `setting.reinos-em-guerra` Reinos em guerra
- `setting.contos-de-fadas` Contos de fadas
- `setting.fantasia-surreal` Fantasia surreal


**cultivo** (14)

- `setting.wuxia` Wuxia
- `setting.xianxia` Xianxia
- `setting.xuanhuan` Xuanhuan
- `setting.cultivo-espiritual` Cultivo espiritual
- `setting.seitas-e-clas` Seitas e clãs
- `setting.artes-marciais` Artes marciais
- `setting.torneios-e-duelos` Torneios e duelos
- `setting.alquimia-e-refinamento` Alquimia e refinamento
- `setting.pilulas-e-tesouros` Pílulas e tesouros
- `setting.tecnicas-secretas` Técnicas secretas
- `setting.tribulacoes-celestiais` Tribulações celestiais
- `setting.ascensao-a-imortalidade` Ascensão à imortalidade
- `setting.reencarnacao-e-transmigracao` Reencarnação e transmigração
- `setting.demonios-imortais-e-divindades` Demônios, imortais e divindades


**historico** (12)

- `setting.japao-feudal` Japão feudal
- `setting.china-historica` China histórica
- `setting.europa-medieval` Europa medieval
- `setting.antiguidade-classica` Antiguidade clássica
- `setting.egito-antigo` Egito antigo
- `setting.era-dos-vikings` Era dos vikings
- `setting.renascimento` Renascimento
- `setting.pirataria` Pirataria
- `setting.faroeste` Faroeste
- `setting.guerras-historicas` Guerras históricas
- `setting.intrigas-de-corte` Intrigas de corte
- `setting.aventuras-historicas-sem-elementos-sobrenaturais` Aventuras históricas sem elementos sobrenaturais


**scifi** (17)

- `setting.ficcao-cientifica-classica` Ficção científica clássica
- `setting.space-opera` Space opera
- `setting.exploracao-espacial` Exploração espacial
- `setting.cyberpunk` Cyberpunk
- `setting.solarpunk` Solarpunk
- `setting.steampunk` Steampunk
- `setting.dieselpunk` Dieselpunk
- `setting.biopunk` Biopunk
- `setting.pos-humanismo` Pós-humanismo
- `setting.inteligencia-artificial` Inteligência artificial
- `setting.robotica` Robótica
- `setting.mechas` Mechas
- `setting.colonizacao-espacial` Colonização espacial
- `setting.viagens-no-tempo` Viagens no tempo
- `setting.primeiro-contato-alienigena` Primeiro contato alienígena
- `setting.distopia-tecnologica` Distopia tecnológica
- `setting.guerra-espacial` Guerra espacial


**terror** (13)

- `setting.horror-gotico` Horror gótico
- `setting.horror-cosmico` Horror cósmico
- `setting.terror-psicologico` Terror psicológico
- `setting.horror-corporal` Horror corporal
- `setting.sobrevivencia-em-horror` Sobrevivência em horror
- `setting.investigacao-sobrenatural` Investigação sobrenatural
- `setting.ocultismo` Ocultismo
- `setting.vampiros` Vampiros
- `setting.lobisomens` Lobisomens
- `setting.fantasmas` Fantasmas
- `setting.demonios` Demônios
- `setting.folclore-e-lendas` Folclore e lendas
- `setting.apocalipse-zumbi` Apocalipse zumbi


**outros** (16)

- `setting.pos-apocaliptico` Pós-apocalíptico
- `setting.sobrevivencia` Sobrevivência
- `setting.super-herois` Super-heróis
- `setting.espionagem` Espionagem
- `setting.investigacao-criminal` Investigação criminal
- `setting.misterio-policial` Mistério policial
- `setting.drama-contemporaneo` Drama contemporâneo
- `setting.romance-e-relacionamentos` Romance e relacionamentos
- `setting.intriga-politica` Intriga política
- `setting.guerra-e-estrategia-militar` Guerra e estratégia militar
- `setting.esportes-e-competicao` Esportes e competição
- `setting.comedia-e-parodia` Comédia e paródia
- `setting.aventura-pulp` Aventura pulp
- `setting.exploracao-maritima` Exploração marítima
- `setting.gestao-de-negocios` Gestão de negócios
- `setting.simulacao-de-profissoes` Simulação de profissões


## Estruturas e estilos de campanha (24)


**estrutura** (24)

- `structure.sandbox` Sandbox
- `structure.campanha-guiada-por-historia` Campanha guiada por história
- `structure.campanha-episodica` Campanha episódica
- `structure.campanha-dividida-em-capitulos` Campanha dividida em capítulos
- `structure.missoes-independentes` Missões independentes
- `structure.exploracao-de-mundo-aberto` Exploração de mundo aberto
- `structure.exploracao-de-masmorras` Exploração de masmorras
- `structure.hexcrawl-e-exploracao-por-regioes` Hexcrawl e exploração por regiões
- `structure.investigacao-e-misterio` Investigação e mistério
- `structure.intriga-politica-e-social` Intriga política e social
- `structure.combate-tatico` Combate tático
- `structure.estrategia-militar` Estratégia militar
- `structure.sobrevivencia-e-gestao-de-recursos` Sobrevivência e gestão de recursos
- `structure.viagens-e-jornadas` Viagens e jornadas
- `structure.construcao-de-bases-reinos-ou-organizacoes` Construção de bases, reinos ou organizações
- `structure.gestao-economica` Gestão econômica
- `structure.progressao-de-poder` Progressão de poder
- `structure.treinamento-e-desenvolvimento-de-habilidades` Treinamento e desenvolvimento de habilidades
- `structure.torneios-e-competicoes` Torneios e competições
- `structure.drama-de-personagens` Drama de personagens
- `structure.amizades-rivalidades-e-romances` Amizades, rivalidades e romances
- `structure.fuga-perseguicao-e-infiltracao` Fuga, perseguição e infiltração
- `structure.horror-e-vulnerabilidade` Horror e vulnerabilidade
- `structure.historias-emergentes` Histórias emergentes


## Filosofias de regras e resolução (14)


**filosofia-de-design** (6)

- `philosophy.rules-light` Rules-light
- `philosophy.rules-heavy` Rules-heavy
- `philosophy.narrativo` Narrativo
- `philosophy.tatico` Tático
- `philosophy.simulacionista` Simulacionista
- `philosophy.cinematografico` Cinematográfico


**metodo-de-resolucao** (6)

- `philosophy.testes-de-pericia` Testes de perícia
- `philosophy.atributos-e-modificadores` Atributos e modificadores
- `philosophy.pools-de-dados` Pools de dados
- `philosophy.dados-percentuais` Dados percentuais
- `philosophy.cartas-ou-dados-especiais` Cartas ou dados especiais
- `philosophy.aleatoriedade-reduzida-ou-inexistente` Aleatoriedade reduzida ou inexistente


**representacao-de-cena** (2)

- `philosophy.teatro-da-mente` Teatro da mente
- `philosophy.combate-com-mapas-e-posicionamento` Combate com mapas e posicionamento


## Tom narrativo (13)


**tom** (13)

- `tone.heroico` Heroico
- `tone.epico` Épico
- `tone.sombrio` Sombrio
- `tone.realista` Realista
- `tone.humoristico` Humorístico
- `tone.tragico` Trágico
- `tone.misterioso` Misterioso
- `tone.aventureiro` Aventureiro
- `tone.brutal` Brutal
- `tone.esperancoso` Esperançoso
- `tone.cinematografico` Cinematográfico
- `tone.politico` Político
- `tone.intimista` Intimista


## Dificuldade (5)


**dificuldade** (5)

- `difficulty.facil` Fácil
- `difficulty.normal` Normal
- `difficulty.dificil-hard` Difícil (Hard)
- `difficulty.mortal` Mortal
- `difficulty.personalizada` Personalizada


## Modelos de progressão (12)


**progressao** (12)

- `progression.niveis` Níveis
- `progression.experiencia` Experiência
- `progression.compra-de-atributos-ou-habilidades` Compra de atributos ou habilidades
- `progression.marcos-narrativos` Marcos narrativos
- `progression.treinamento` Treinamento
- `progression.reputacao-e-influencia` Reputação e influência
- `progression.equipamentos-e-recursos` Equipamentos e recursos
- `progression.conquistas` Conquistas
- `progression.reinos-de-cultivo` Reinos de cultivo
- `progression.transformacao-sobrenatural` Transformação sobrenatural
- `progression.evolucao-de-organizacoes-e-bases` Evolução de organizações e bases
- `progression.progressao-horizontal` Progressão horizontal


**Total: 153 entradas, sem IDs duplicados (verificado por teste).**
