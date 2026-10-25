package dev.rpgsolo.engine

import kotlinx.serialization.Serializable

/**
 * Catálogo canônico inicial de Aeltharyn. Este arquivo fornece dados tipados e validados ao motor;
 * não cria automaticamente NPCs na campanha nem revela segredos ao jogador.
 */
@Serializable
data class WorldCity(
    val id: String,
    val name: String,
    val population: Int,
    val role: String,
    val supplies: List<String>,
    val dependsOn: List<String>,
    val activeProblem: String,
    val legend: String,
    val peopleIds: List<String>,
)

@Serializable
data class WorldPeople(
    val id: String,
    val name: String,
    val relation: String,
    val cityIds: List<String>,
    val notes: String,
)

@Serializable
data class WorldFigure(
    val id: String,
    val name: String,
    val cityId: String,
    val peopleId: String,
    val role: String,
    val aliveAtStart: Boolean,
    val contribution: String,
    val limitation: String,
)

@Serializable
data class WorldRoute(
    val fromCityId: String,
    val toCityId: String,
    val days: Int,
    val mode: String,
    val goods: List<String>,
    val seasonalRisk: String,
)

@Serializable
data class WorldClock(
    val id: String,
    val name: String,
    val initialProgress: Int,
    val segments: Int,
    val advancesWhen: String,
    val consequence: String,
)

object AeltharynWorld {
    const val ID = "kingdom.aeltharyn"
    const val NAME = "Aeltharyn — Reino das Florestas"
    const val YEAR = 742
    const val CAPITAL_CITY_ID = "city.elarion"

    val people =
        listOf(
            WorldPeople(
                "elfo",
                "Elfo",
                "originário e majoritário",
                listOf("city.elarion", "city.vael-tir", "city.caladwen", "city.thalanor"),
                "Povo dominante; a identidade élfica não implica lealdade automática ao rei.",
            ),
            WorldPeople(
                "elfo-dos-bosques",
                "Elfo dos Bosques",
                "linhagem central",
                listOf("city.vael-tir", "city.elarion", "city.brumafunda", "city.nymvar"),
                "Manejo da mata, patrulhas e pactos de preservação.",
            ),
            WorldPeople(
                "elfo-das-estrelas",
                "Elfo das Estrelas",
                "linhagem antiga",
                listOf("city.caladwen", "city.elarion"),
                "Observação celeste, calendário e arquivos.",
            ),
            WorldPeople(
                "elfo-do-crepusculo",
                "Elfo do Crepúsculo",
                "linhagem de fronteira",
                listOf("city.orvass", "city.brumafunda", "city.elarion"),
                "Rotas noturnas e investigação; não é inerentemente maligno.",
            ),
            WorldPeople(
                "feerico",
                "Feérico",
                "povo antigo vizinho",
                listOf("city.silvaran", "city.brumafunda"),
                "Pactos de hospitalidade e comunidades com leis próprias.",
            ),
            WorldPeople(
                "plantae",
                "Verdejante",
                "povo ecológico local",
                listOf("city.arven", "city.vael-tir"),
                "Solo vivo, mudas e diálogo com ecossistemas.",
            ),
            WorldPeople(
                "fungal",
                "Micélioide",
                "povo de zonas úmidas",
                listOf("city.orvass", "city.brumafunda"),
                "Redes fúngicas, decomposição e sinais de contaminação.",
            ),
            WorldPeople(
                "satiro",
                "Sátiro / Silvano Festivo",
                "povo vizinho",
                listOf("city.silvaran", "city.arven"),
                "Festivais, mediação e memória oral.",
            ),
            WorldPeople(
                "silvano-dos-festivais",
                "Silvano dos Festivais",
                "linhagem cultural local",
                listOf("city.silvaran", "city.arven"),
                "Pactos sazonais, artes e hospitalidade.",
            ),
            WorldPeople(
                "centauro-das-florestas",
                "Centauro das Florestas",
                "aliado regional autônomo",
                listOf("city.vael-tir", "city.arven"),
                "Corredores de migração e pastagens protegidas.",
            ),
            WorldPeople(
                "felideo-das-selvas",
                "Felídeo das Selvas",
                "povo de mata densa",
                listOf("city.brumafunda", "city.vael-tir"),
                "Guias, rastreio e cuidado de fauna.",
            ),
            WorldPeople(
                "orcano-das-matas",
                "Orcano das Matas",
                "comunidades pactuadas",
                listOf("city.orvass", "city.vael-tir"),
                "Medicina local, defesa de aldeias e passarelas.",
            ),
            WorldPeople(
                "pequenino-ribeirinho",
                "Pequenino Ribeirinho",
                "povo das várzeas",
                listOf("city.nymvar", "city.arven"),
                "Agricultura, barcaças, sementes e diques.",
            ),
            WorldPeople(
                "gnomo-dos-vales",
                "Gnomo dos Vales",
                "povo integrado por ofícios",
                listOf("city.caladwen", "city.nymvar", "city.elarion", "city.torvelume"),
                "Instrumentos, irrigação e medição.",
            ),
            WorldPeople(
                "homem-corvo-das-torres",
                "Corvário das Torres",
                "comunidades de mensageiros",
                listOf("city.brumafunda", "city.elarion"),
                "Mensagens e orientação; não são espiões por natureza.",
            ),
            WorldPeople(
                "abissal-das-enseadas",
                "Abissal das Enseadas",
                "aliado costeiro",
                listOf("city.thalanor"),
                "Navegação costeira, resgate e leitura de marés.",
            ),
        )

    val cities =
        listOf(
            WorldCity(
                "city.elarion",
                "Elarion — Cidade das Sete Copas",
                82000,
                "capital política, arquivo e centro cerimonial",
                listOf("administração", "ensino", "artesanato", "documentos"),
                listOf("city.nymvar", "city.arven", "city.torvelume"),
                "Documentos sobre a Guerra das Cinzas Verdes desapareceram; pontes antigas exigem reparo.",
                "As sete árvores se inclinaram quando os primeiros juramentos foram pronunciados.",
                listOf(
                    "elfo",
                    "elfo-dos-bosques",
                    "elfo-das-estrelas",
                    "elfo-do-crepusculo",
                    "gnomo-dos-vales",
                    "homem-corvo-das-torres",
                ),
            ),
            WorldCity(
                "city.nymvar",
                "Nymvar — Cidade das Três Vozes",
                31000,
                "celeiro, canais e centro de cura",
                listOf("grãos", "frutas", "ervas", "peixe de rio", "transporte fluvial"),
                listOf("city.arven", "city.torvelume", "city.elarion"),
                "A dívida de manutenção dos diques aumenta antes da estação de cheias.",
                "A barqueira sem nome ainda guia barcos perdidos quando três correntes se encontram.",
                listOf("pequenino-ribeirinho", "gnomo-dos-vales", "elfo-dos-bosques"),
            ),
            WorldCity(
                "city.thalanor",
                "Thalanor — Porto da Folha Azul",
                27000,
                "porto, resgate e comércio costeiro",
                listOf("pesca", "sal", "cartas náuticas", "comércio marítimo"),
                listOf("city.nymvar", "city.arven", "city.caladwen"),
                "Um navio chegou sem tripulação e o protocolo de quarentena divide capitães e famílias.",
                "Uma luz azul aparece na enseada antes das tempestades mais perigosas.",
                listOf("abissal-das-enseadas", "elfo"),
            ),
            WorldCity(
                "city.vael-tir",
                "Vael-Tir — A Cidade sob as Copas",
                19000,
                "manejo florestal e defesa de corredores",
                listOf("madeira autorizada", "guias", "resina licenciada", "patrulha"),
                listOf("city.brumafunda", "city.torvelume", "city.arven"),
                "Licenças de corte são falsificadas perto de uma área de reprodução.",
                "Uma trilha muda de lugar para quem entra com intenção de incendiar a mata.",
                listOf(
                    "elfo",
                    "elfo-dos-bosques",
                    "centauro-das-florestas",
                    "felideo-das-selvas",
                    "orcano-das-matas",
                    "plantae",
                ),
            ),
            WorldCity(
                "city.orvass",
                "Orvass — Cidade das Águas Escuras",
                14000,
                "pântano, medicina e comunidades sobre passarelas",
                listOf("plantas medicinais", "fungos", "pesca", "conhecimento de águas"),
                listOf("city.nymvar", "city.brumafunda", "city.torvelume"),
                "Sinais de contaminação aparecem numa rede de canais, mas a origem não foi provada.",
                "Lanternas baixas mostram o caminho a quem respeita os canais e confundem quem os trata como atalho.",
                listOf("elfo-do-crepusculo", "fungal", "orcano-das-matas"),
            ),
            WorldCity(
                "city.caladwen",
                "Caladwen — Cidade dos Arquivos Celestes",
                22000,
                "observatório, calendário e ensino",
                listOf("calendários", "mapas", "observações climáticas", "formação"),
                listOf("city.thalanor", "city.arven", "city.torvelume"),
                "Duas previsões recentes falharam e o conselho exige transparência sobre a incerteza.",
                "A Criança do Lago aparece em relatos incompatíveis, nenhum deles confirmado.",
                listOf("elfo", "elfo-das-estrelas", "gnomo-dos-vales"),
            ),
            WorldCity(
                "city.brumafunda",
                "Brumafunda — A Cidade do Bosque Velado",
                12000,
                "guias, resgate e orientação na neblina",
                listOf("guias", "mapas locais", "sinais de trilha", "resgate"),
                listOf("city.vael-tir", "city.orvass", "city.caladwen", "city.arven"),
                "Sela dos Retornos desapareceu e novos sinais não combinam com os mapas conhecidos.",
                "A neblina repete vozes verdadeiras, mas nunca na ordem em que foram ditas.",
                listOf(
                    "elfo-dos-bosques",
                    "elfo-do-crepusculo",
                    "feerico",
                    "fungal",
                    "felideo-das-selvas",
                    "homem-corvo-das-torres",
                ),
            ),
            WorldCity(
                "city.arven",
                "Arven — As Clareiras Douradas",
                36000,
                "agricultura, pomares, apiários e sementes",
                listOf("grãos", "frutas", "mel", "fibras", "sementes"),
                listOf("city.nymvar", "city.torvelume", "city.silvaran"),
                "Uma praga de flores ameaça polinizadores e a próxima reserva de sementes.",
                "Uma colmeia antiga nunca perde sua rainha, mas ninguém sabe quantos enxames a substituíram.",
                listOf(
                    "plantae",
                    "satiro",
                    "silvano-dos-festivais",
                    "centauro-das-florestas",
                    "pequenino-ribeirinho",
                ),
            ),
            WorldCity(
                "city.silvaran",
                "Silvaran — A Cidade dos Festivais",
                24000,
                "artes, hospitalidade e mediação",
                listOf("festivais", "artesanato", "hospedagem", "mediação cultural"),
                listOf("city.arven", "city.thalanor", "city.brumafunda"),
                "Uma casa poderosa tenta comprar o direito exclusivo de patrocinar o festival principal.",
                "A peça de Miri muda o final quando duas famílias rivais assistem juntas.",
                listOf("feerico", "satiro", "silvano-dos-festivais"),
            ),
            WorldCity(
                "city.torvelume",
                "Torvelume — A Fortaleza da Espinha de Vidro",
                17000,
                "ofícios, reparos e defesa de passagem",
                listOf("ferragens", "ferramentas", "reparos", "portões"),
                listOf("city.arven", "city.nymvar", "city.vael-tir", "city.caladwen"),
                "A mina selada apresenta tremores e há pressão para reabrir o acesso.",
                "As sete bigornas soam sozinhas antes de um portão falhar — ou assim afirmam os ferreiros.",
                listOf("elfo", "gnomo-dos-vales"),
            ),
        )

    val figures =
        listOf(
            WorldFigure(
                "npc.avarra-sete-pontes",
                "Avarra das Sete Pontes",
                "city.elarion",
                "elfo",
                "HERO",
                false,
                "Unificou pontes entre os assentamentos das copas.",
                "Sua obra deslocou famílias; a versão oficial omite parte desse custo.",
            ),
            WorldFigure(
                "npc.nimeth-vela-chuva",
                "Nimeth Vela-de-Chuva",
                "city.elarion",
                "elfo-das-estrelas",
                "GUARDIAN",
                true,
                "Preserva e duplica arquivos com cuidado para não expor informantes.",
                "Não consegue salvar e catalogar todos os documentos sob pressão.",
            ),
            WorldFigure(
                "npc.parel-tres-parafusos",
                "Parel Três-Parafusos",
                "city.elarion",
                "gnomo-dos-vales",
                "BENEFACTOR",
                true,
                "Ensina moradores a inspecionar cabos e pontes.",
                "A oficina tem recursos para atender apenas parte da cidade.",
            ),
            WorldFigure(
                "npc.dena-tres-correntes",
                "Dena Três-Correntes",
                "city.nymvar",
                "pequenino-ribeirinho",
                "HERO",
                false,
                "Redesenhou comportas e reduziu danos de cheias.",
                "Algumas obras deslocaram famílias e seus desenhos são incompletos.",
            ),
            WorldFigure(
                "npc.mara-barro-claro",
                "Mara Barro-Claro",
                "city.nymvar",
                "pequenino-ribeirinho",
                "GUARDIAN",
                true,
                "Inspeciona diques e suspende travessias inseguras.",
                "Seu rigor atrasa comércio quando a manutenção é urgente.",
            ),
            WorldFigure(
                "npc.iven-folhas-brancas",
                "Iven das Folhas Brancas",
                "city.nymvar",
                "elfo-dos-bosques",
                "BENEFACTOR",
                true,
                "Coordena transporte de pacientes e registra tratamentos que falharam.",
                "Não dispõe de remédios raros para todos os casos.",
            ),
            WorldFigure(
                "npc.yssara-folha-sal",
                "Yssara Folha-de-Sal",
                "city.thalanor",
                "abissal-das-enseadas",
                "HERO",
                false,
                "Padronizou cartas de resgate e protocolos de tempestade.",
                "Autorizou um abandono controverso durante uma quarentena.",
            ),
            WorldFigure(
                "npc.naren-mare-reversa",
                "Náren da Maré Reversa",
                "city.thalanor",
                "abissal-das-enseadas",
                "GUARDIAN",
                true,
                "Monitora correntes rasas e coordena alertas de maré.",
                "Recusa resgates com chance mínima de retorno e é contestado por famílias.",
            ),
            WorldFigure(
                "npc.talia-cordame-verde",
                "Tália Cordame-Verde",
                "city.thalanor",
                "elfo",
                "BENEFACTOR",
                true,
                "Mantém cooperativa de segurança e apoio a trabalhadores portuários.",
                "O fundo não cobre todos os acidentes.",
            ),
            WorldFigure(
                "npc.oren-galho-quieto",
                "Oren Galho-Quieto",
                "city.vael-tir",
                "elfo-dos-bosques",
                "HERO",
                false,
                "Abriu uma rota de resgate e depois ajudou a fechá-la para proteger a mata.",
                "Seu mapa tem versões distintas e não serve para toda estação.",
            ),
            WorldFigure(
                "npc.eshara-chuva-folha",
                "Eshara Chuva-na-Folha",
                "city.vael-tir",
                "elfo-dos-bosques",
                "GUARDIAN",
                true,
                "Negocia e protege corredores silvestres.",
                "Não comanda todos os patrulheiros e pode ser desobedecida.",
            ),
            WorldFigure(
                "npc.tavro-passo-raiz",
                "Tavro Passo-de-Raiz",
                "city.vael-tir",
                "centauro-das-florestas",
                "BENEFACTOR",
                true,
                "Mantém abrigos e pontos de água sem exigir juramento político.",
                "A rede depende de contribuições voluntárias.",
            ),
            WorldFigure(
                "npc.nima-raiz-lodo",
                "Nima Raiz-de-Lodo",
                "city.orvass",
                "orcano-das-matas",
                "HERO",
                false,
                "Registrou tratamentos úteis e efeitos adversos perigosos.",
                "Uma mistura antiga ajudou adultos e causou reação grave em crianças.",
            ),
            WorldFigure(
                "npc.voz-muitas-raizes",
                "Voz-de-Muitas-Raízes",
                "city.orvass",
                "fungal",
                "GUARDIAN",
                true,
                "Preserva redes miceliais e comunica sinais de contaminação.",
                "A comunicação é lenta e exige tradução para decisões urgentes.",
            ),
            WorldFigure(
                "npc.korga-ponta-junco",
                "Korga Ponta-de-Junco",
                "city.orvass",
                "orcano-das-matas",
                "BENEFACTOR",
                true,
                "Organizou passarelas elevadas e depósitos de remédios.",
                "Madeira tratada e manutenção são escassas.",
            ),
            WorldFigure(
                "npc.ithren-quatro-estacoes",
                "Ithren das Quatro Estações",
                "city.caladwen",
                "elfo-das-estrelas",
                "HERO",
                false,
                "Corrigiu tabelas de plantio ao demonstrar erro acumulado no calendário.",
                "A reforma desagradou sacerdotes e não prevê todas as mudanças climáticas.",
            ),
            WorldFigure(
                "npc.elyon-prisma-claro",
                "Elyon Prisma-Claro",
                "city.caladwen",
                "elfo-das-estrelas",
                "GUARDIAN",
                true,
                "Protege observações e publica margem de incerteza.",
                "Suas previsões dependem de instrumentos e dados incompletos.",
            ),
            WorldFigure(
                "npc.asha-nove-pergaminhos",
                "Asha Nove-Pergaminhos",
                "city.caladwen",
                "elfo-das-estrelas",
                "BENEFACTOR",
                true,
                "Torna pesquisas verificáveis acessíveis sem expor originais frágeis.",
                "Cópias não substituem todas as fontes perdidas.",
            ),
            WorldFigure(
                "npc.sela-retornos",
                "Sela dos Retornos",
                "city.brumafunda",
                "felideo-das-selvas",
                "HERO",
                false,
                "Criou cordões táteis e marcas sonoras para resgate na neblina.",
                "Desapareceu; sua morte não foi comprovada.",
            ),
            WorldFigure(
                "npc.sivra-pata-musgo",
                "Sivra Pata-de-Musgo",
                "city.brumafunda",
                "felideo-das-selvas",
                "GUARDIAN",
                true,
                "Protege ninhos e registra caça ilegal.",
                "Recusa revelar localizações de espécies vulneráveis.",
            ),
            WorldFigure(
                "npc.milo-passo-bruma",
                "Milo Passo-de-Bruma",
                "city.brumafunda",
                "felideo-das-selvas",
                "BENEFACTOR",
                true,
                "Mantém abrigo para viajantes que retornam de expedições incompletas.",
                "O abrigo depende de doações e pode fechar durante a neblina extrema.",
            ),
            WorldFigure(
                "npc.toma-mel-ambar",
                "Toma Mel-de-Âmbar",
                "city.arven",
                "pequenino-ribeirinho",
                "HERO",
                false,
                "Protegeu sementes e mel durante uma praga de flores.",
                "Sua recusa em vender todo o estoque dividiu a cidade.",
            ),
            WorldFigure(
                "npc.ruma-coracao-solo",
                "Ruma Coração-de-Solo",
                "city.arven",
                "plantae",
                "GUARDIAN",
                true,
                "Identifica esgotamento do solo e coordena recuperação de clareiras.",
                "Não consegue reverter uma safra perdida de imediato.",
            ),
            WorldFigure(
                "npc.bento-sete-sementes",
                "Bento Sete-Sementes",
                "city.arven",
                "pequenino-ribeirinho",
                "BENEFACTOR",
                true,
                "Mantém banco de sementes diversificadas e troca variedades testadas.",
                "A reserva é vulnerável a fungos e precisa de financiamento.",
            ),
            WorldFigure(
                "npc.miri-corda-lua",
                "Miri Corda-de-Lua",
                "city.silvaran",
                "satiro",
                "HERO",
                false,
                "Criou uma peça pública que permitiu negociação entre casas rivais.",
                "O acordo foi temporário e não resolveu a disputa por terras.",
            ),
            WorldFigure(
                "npc.voro-cancao-pedra",
                "Voro Canção-de-Pedra",
                "city.silvaran",
                "silvano-dos-festivais",
                "GUARDIAN",
                true,
                "Protege espaços de ensaio e artistas contra coerção.",
                "Depende de locais cedidos por instituições com interesses próprios.",
            ),
            WorldFigure(
                "npc.dara-passo-ritmico",
                "Dara Passo-Rítmico",
                "city.silvaran",
                "satiro",
                "BENEFACTOR",
                true,
                "Organiza apresentações gratuitas em bairros afastados.",
                "A agenda é limitada por recursos e transporte.",
            ),
            WorldFigure(
                "npc.hadrik-sete-bigornas",
                "Hadrik das Sete Bigornas",
                "city.torvelume",
                "gnomo-dos-vales",
                "HERO",
                false,
                "Padronizou peças de reparo e recusou desmontar uma ponte para bloquear refugiados.",
                "Foi preso por desobediência e sua oficina perdeu contratos.",
            ),
            WorldFigure(
                "npc.meda-martelo-calmo",
                "Meda Martelo-Calmo",
                "city.torvelume",
                "gnomo-dos-vales",
                "GUARDIAN",
                true,
                "Fiscaliza padrões de segurança em oficinas e portões.",
                "Pode interditar uma estrutura, mas não consegue financiá-la.",
            ),
            WorldFigure(
                "npc.vessa-engrenagem-lua",
                "Vessa Engrenagem-de-Lua",
                "city.torvelume",
                "gnomo-dos-vales",
                "BENEFACTOR",
                true,
                "Oferece ferramentas e bolsas a aprendizes sem recursos.",
                "As bolsas cobrem apenas uma parte dos candidatos.",
            ),
        )

    val routes =
        listOf(
            WorldRoute(
                "city.elarion",
                "city.nymvar",
                2,
                "river",
                listOf("grãos", "documentos", "ervas"),
                "Cheias e corrente contrária podem dobrar o tempo.",
            ),
            WorldRoute(
                "city.nymvar",
                "city.thalanor",
                4,
                "river",
                listOf("grãos", "peixe", "carga portuária"),
                "Tempestades e quarentena interrompem a descarga.",
            ),
            WorldRoute(
                "city.elarion",
                "city.vael-tir",
                2,
                "forest",
                listOf("mensagens", "ferramentas", "licenças"),
                "Trilhas fecham durante reprodução e incêndios.",
            ),
            WorldRoute(
                "city.vael-tir",
                "city.brumafunda",
                2,
                "forest",
                listOf("guias", "madeira autorizada", "mapas"),
                "Neblina e sinais sonoros enganosos exigem guia.",
            ),
            WorldRoute(
                "city.nymvar",
                "city.arven",
                2,
                "road",
                listOf("sementes", "frutas", "grãos"),
                "Lama de primavera pode dobrar o tempo.",
            ),
            WorldRoute(
                "city.arven",
                "city.silvaran",
                3,
                "road",
                listOf("alimentos", "mel", "artesanato"),
                "Caravanas precisam evitar corredores de migração.",
            ),
            WorldRoute(
                "city.caladwen",
                "city.torvelume",
                4,
                "mountain_pass",
                listOf("instrumentos", "ferragens", "mapas"),
                "Geada e deslizamentos podem fechar a passagem.",
            ),
            WorldRoute(
                "city.torvelume",
                "city.vael-tir",
                3,
                "road",
                listOf("ferramentas", "peças de reparo"),
                "A estrada exige manutenção e licença de carga.",
            ),
            WorldRoute(
                "city.orvass",
                "city.nymvar",
                3,
                "marsh",
                listOf("remédios", "fungos", "peixe"),
                "O tempo varia com o nível da água e os canais.",
            ),
            WorldRoute(
                "city.orvass",
                "city.brumafunda",
                5,
                "marsh_forest",
                listOf("remédios", "mensagens", "mapas locais"),
                "Canais mudam e mapas antigos não garantem passagem.",
            ),
            WorldRoute(
                "city.caladwen",
                "city.thalanor",
                4,
                "road",
                listOf("previsões", "cartas náuticas", "amostras"),
                "As observações de maré e as condições costeiras alteram o prazo.",
            ),
            WorldRoute(
                "city.brumafunda",
                "city.arven",
                4,
                "forest",
                listOf("guias", "sementes", "mantimentos"),
                "A rota fica vulnerável a neblina e animais em reprodução.",
            ),
            WorldRoute(
                "city.arven",
                "city.torvelume",
                4,
                "road",
                listOf("alimentos", "ferragens"),
                "Caravanas lentas e pontes em reparo atrasam carga.",
            ),
            WorldRoute(
                "city.elarion",
                "city.torvelume",
                5,
                "road",
                listOf("ordens", "ferragens", "documentos"),
                "Deslizamentos e inspeções de carga aumentam o percurso.",
            ),
        )

    val clocks =
        listOf(
            WorldClock(
                "clock.heart-trees",
                "Doença das árvores-coração",
                2,
                6,
                "Semanas sem amostras, cortes próximos a raízes ou relatórios ocultos.",
                "Uma árvore perde uma função ecológica importante; a causa ainda precisa ser investigada.",
            ),
            WorldClock(
                "clock.dikes",
                "Dívida dos diques",
                3,
                6,
                "Cheias, reparos adiados e disputa por verbas.",
                "Um bairro de Nymvar sofre inundação grave.",
            ),
            WorldClock(
                "clock.resin-smuggling",
                "Contrabando de resina",
                2,
                6,
                "Licenças falsas, patrulhas desviadas e procura elevada.",
                "Surge uma rede organizada de extração ilegal.",
            ),
            WorldClock(
                "clock.city-distrust",
                "Desconfiança entre cidades",
                1,
                6,
                "Tributos desiguais, rumores e socorro distribuído injustamente.",
                "Duas cidades suspendem temporariamente uma obrigação do pacto.",
            ),
            WorldClock(
                "clock.ghost-ship",
                "Navio sem tripulação",
                1,
                6,
                "Provas ignoradas, carga movimentada sem registro ou testemunhas intimidadas.",
                "Uma prova física se perde ou ocorre um segundo incidente.",
            ),
            WorldClock(
                "clock.sealed-mine",
                "Mina selada de Torvelume",
                1,
                6,
                "Escavação clandestina, tremores ou sabotagem.",
                "Um acesso se rompe e cria perigo real; o que existe abaixo continua desconhecido.",
            ),
            WorldClock(
                "clock.centaur-corridors",
                "Corredores centauros",
                2,
                6,
                "Cercas novas, colheita fora de época ou patrulhas sem consulta.",
                "Uma rota sazonal fecha e caravanas precisam desviar.",
            ),
        )

    /** Instala o mapa e os relógios iniciais numa campanha recém-criada, em um único lote atômico. */
    fun install(session: CampaignSession): ExecResult {
        val problems = validate()
        if (problems.isNotEmpty()) {
            return ExecResult.Rejected(
                "catálogo Aeltharyn inválido: ${problems.joinToString("; ")}",
                0,
            )
        }
        if (session.state.places.isNotEmpty() || session.state.currentPlaceId != null) {
            return ExecResult.Rejected("Aeltharyn só pode ser instalado numa campanha sem mapa prévio", 0)
        }
        val clockCollisions = clocks.map { it.id }.filter { it in session.state.clocks }
        if (clockCollisions.isNotEmpty()) {
            return ExecResult.Rejected("relógios já existentes: ${clockCollisions.joinToString()}", 0)
        }
        val commands = mutableListOf<Command>()
        cities.forEach { city ->
            commands.add(
                AddPlace(
                    Place(
                    id = city.id,
                    name = city.name,
                    description =
                        "${city.role}. Recursos: ${city.supplies.joinToString(", ")}. Lenda local: ${city.legend}",
                        secret = city.activeProblem,
                    ),
                ),
            )
        }
        routes.forEach { route ->
            commands += ConnectPlaces(route.fromCityId, route.toCityId)
        }
        commands += DiscoverPlace(CAPITAL_CITY_ID)
        clocks.forEach { clock ->
            commands.add(
                CreateClock(
                    clock.id,
                    clock.name,
                    clock.segments,
                    consequence = clock.consequence,
                ),
            )
            if (clock.initialProgress > 0) {
                commands += AdvanceClock(clock.id, clock.initialProgress)
            }
        }
        return session.executeAll(commands)
    }

    /** Retorna todos os problemas estruturais encontrados; lista vazia significa catálogo internamente consistente. */
    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        fun unique(
            values: List<String>,
            label: String,
        ) {
            values.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.forEach {
                errors += "$label duplicado: $it"
            }
        }
        unique(cities.map { it.id }, "city id")
        unique(people.map { it.id }, "people id")
        unique(figures.map { it.id }, "figure id")
        unique(clocks.map { it.id }, "clock id")
        val cityIds = cities.mapTo(mutableSetOf()) { it.id }
        val peopleIds = people.mapTo(mutableSetOf()) { it.id }
        val figureRoles = setOf("HERO", "GUARDIAN", "BENEFACTOR")
        if (cities.size != 10) errors += "Aeltharyn deve conter 10 cidades; encontrado: ${cities.size}"
        if (people.size != 16) {
            errors += "Aeltharyn deve conter 16 povos/linhagens selecionados; encontrado: ${people.size}"
        }
        if (cities.none { it.id == CAPITAL_CITY_ID }) errors += "capital inexistente: $CAPITAL_CITY_ID"
        people.forEach { p ->
            if (p.cityIds.isEmpty()) errors += "povo sem cidade: ${p.id}"
            p.cityIds.filterNot { it in cityIds }.forEach { errors += "cidade inexistente $it em ${p.id}" }
        }
        cities.forEach { c ->
            if (c.population <= 0) errors += "população inválida: ${c.id}"
            if (c.supplies.isEmpty() || c.dependsOn.isEmpty()) errors += "rede econômica incompleta: ${c.id}"
            c.dependsOn.filterNot { it in cityIds }.forEach {
                errors += "dependência de cidade inexistente: ${c.id} -> $it"
            }
            c.peopleIds.filterNot { it in peopleIds }.forEach { errors += "povo não catalogado em ${c.id}: $it" }
            c.peopleIds.forEach { peopleId ->
                if (c.id !in people.firstOrNull { it.id == peopleId }?.cityIds.orEmpty()) {
                    errors += "presença de $peopleId em ${c.id} não consta na distribuição do povo"
                }
            }
            val roles = figures.filter { it.cityId == c.id }.map { it.role }.toSet()
            figureRoles.filterNot { it in roles }.forEach { errors += "falta função $it em ${c.id}" }
        }
        figures.forEach { f ->
            if (f.cityId !in cityIds) errors += "cidade inexistente em figura: ${f.id}"
            if (f.peopleId !in peopleIds) errors += "povo inexistente em figura: ${f.id}"
            if (f.peopleId in peopleIds && f.cityId in cityIds) {
                if (f.peopleId !in cities.first { it.id == f.cityId }.peopleIds) {
                    errors += "povo da figura ${f.id} não está listado na cidade"
                }
                if (f.cityId !in people.first { it.id == f.peopleId }.cityIds) {
                    errors += "cidade da figura ${f.id} não consta na distribuição do povo"
                }
            }
            if (f.role !in figureRoles) errors += "função desconhecida em figura: ${f.id}"
        }
        routes.forEach { route ->
            if (route.fromCityId !in cityIds || route.toCityId !in cityIds) {
                errors += "rota aponta para cidade inexistente: ${route.fromCityId} -> ${route.toCityId}"
            }
            if (route.fromCityId == route.toCityId) errors += "rota liga cidade a si mesma: ${route.fromCityId}"
            if (route.days <= 0) errors += "duração inválida de rota: ${route.fromCityId} -> ${route.toCityId}"
        }
        clocks.forEach { clock ->
            if (clock.segments <= 0 || clock.initialProgress !in 0..clock.segments) {
                errors += "progresso inválido no relógio: ${clock.id}"
            }
        }
        if (cities.isNotEmpty()) {
            val adjacency = cityIds.associateWith { mutableSetOf<String>() }
            routes.forEach { route ->
                if (route.fromCityId in cityIds && route.toCityId in cityIds) {
                    adjacency.getValue(route.fromCityId) += route.toCityId
                    adjacency.getValue(route.toCityId) += route.fromCityId
                }
            }
            val visited = mutableSetOf<String>()
            val queue = ArrayDeque(listOf(CAPITAL_CITY_ID))
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                if (visited.add(current)) adjacency[current].orEmpty().forEach { if (it !in visited) queue.addLast(it) }
            }
            (cityIds - visited).forEach { errors += "cidade sem rota a partir da capital: $it" }
        }
        return errors
    }
}
