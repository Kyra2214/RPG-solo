# 12 — Registro mestre de entidades (M1.1, versão 6)

**Estado:** versão 6 (M2.2: seção 11, faixas de população); versão 5 (M1.5: seção 10, unidades locais); versão 4 (M1.3: seções 8 e 9 com facções e assentamentos como dados); versão 3, 25/10/2026, depois do M1.4. Aplica as decisões D-01 a D-07: os 7 conflitos de ID foram resolvidos (vale o ID dos documentos, já corrigido no catálogo técnico), os 32 IDs que só existiam no catálogo agora estão escritos nos documentos, e a Vessa Fio-Dourado passou a Silvaran. **Não é canon novo**: só organiza o que já existe. Onde um dado não foi encontrado, a célula traz `—`; nada foi inventado.
**Uso:** é a fonte única de IDs. Antes de criar um NPC, assentamento ou facção, procure aqui. Um ID nunca é reutilizado.
**Arquivos irmãos:** `12-registro-mestre.csv` (mesmos dados; a coluna `ficha_doc` traz a linha de povo e idade como está no documento), `12-facoes.csv` e `12-assentamentos.csv` (seções 8 e 9, M1.3) e `12-demografia.csv` (seção 11, M2.2).

## 1. Resumo

| Tipo | Quantidade |
|---|---:|
| Cidades | 10 |
| Facções | 10 |
| Assentamentos | 20 |
| Pessoas (NPCs) distintas | 95 |

Pessoas por situação de ID:

| Situação | Qtd | O que fazer |
|---|---:|---|
| **nos dois** (mesmo ID nos documentos e no catálogo) | 51 | conferido por script em 25/10/2026 |
| **só nos docs** (sem entrada no catálogo) | 44 | criar entrada no catálogo na integração (inclui governo e líderes dos assentamentos) |

Conferência: 51 figuras no catálogo, todas com o mesmo ID nos documentos; 95 IDs `npc.*` nos documentos; nenhum ID repetido para pessoas diferentes.

## 2. Cidades (10)

| ID | Nome | Tipo | População (texto do doc) | Governo local |
|---|---|---|---|---|
| `city.elarion` | Elarion — Cidade das Sete Copas | capital política, arquivo e centro cerimonial | cerca de 82.000 | Intendente Avariel Thorne, nomeado pelo rei e fiscalizado pelo conselho urbano |
| `city.nymvar` | Nymvar — Cidade das Três Vozes | cidade ribeirinha, celeiro e centro de cura | cerca de 31.000 | Matriarca eleita dos canais, Elsin Várzea-Clara, com mandato renovável após prestação de contas |
| `city.thalanor` | Thalanor — Porto da Folha Azul | porto marítimo e alfândega | cerca de 44.000 | Almirante-civil Syranna das Marés, escolhida por conselho de capitães e confirmada pela Coroa |
| `city.vael-tir` | Vael-Tir — A Cidade sob as Copas | assentamento arbóreo, patrulha e manejo florestal | cerca de 19.000 | Guardiã de Trilhas Eryndra Folha-Verde, escolhida por patrulheiros e comunidades locais |
| `city.orvass` | Orvass — Cidade das Águas Escuras | cidade de pântano, pesca, ervas e travessias | cerca de 16.500 | Conselho das Estacas, composto por representantes de bairros e comunidades de água |
| `city.caladwen` | Caladwen — Cidade dos Arquivos Celestes | centro acadêmico e observatório | cerca de 23.000 | Reitora dos Ciclos, Selvaris Noctel, escolhida por três escolas e confirmada pelo conselho local |
| `city.brumafunda` | Brumafunda — A Cidade do Bosque Velado | cidade de fronteira ecológica, guias e coleta | cerca de 12.000 | Vigia-Mor Neralis Vento-Manso, responsável por rotas e quarentenas |
| `city.arven` | Arven — As Clareiras Douradas | centro agrícola e de criação sustentável | cerca de 27.000 | Prefeita dos Celeiros, Tessa Folhinha, escolhida por associações de produtores |
| `city.silvaran` | Silvaran — A Cidade dos Festivais | centro cultural, santuários sazonais e hospitalidade | cerca de 21.000 | Anfitriã das Estações, Lyris Melodia-Clara, cargo ritual que depende de consenso entre bairros |
| `city.torvelume` | Torvelume — A Fortaleza da Espinha de Vidro | fortaleza florestal, posto de fronteira e cidade de ofícios | cerca de 18.000 | Comandante-Civil Dauren Galho-de-Ferro, escolhido pela guarnição e ratificado pelos habitantes |

## 3. Facções (10)

As facções ainda **não existem no catálogo técnico**. A liderança aparece por nome nos documentos, sem ID de NPC.

| ID | Nome | Liderança (texto do doc) | Objetivo |
|---|---|---|---|
| `faction.vigias-primeiro-verde` | Vigias do Primeiro Verde | Saelith Arco-de-Chuva, general da guarda florestal. | proteger ecossistemas, patrulhar áreas antigas e impedir extração destrutiva. |
| `faction.liga-das-rotas` | Liga das Rotas | Tavian Lume-Fino, com representantes locais independentes. | manter estradas, rios e portos abertos e previsíveis. |
| `faction.arquivistas-seiva` | Arquivistas da Seiva | Lethariel Nascente-Clara. | preservar fontes históricas, registros de tratados e testemunhos. |
| `faction.circulo-estacoes` | Círculo das Estações | Lyris Melodia-Clara. | manter ritos sazonais, pactos de hospitalidade e transmissão cultural. |
| `faction.irmandade-aguas` | Irmandade das Águas | Elsin Várzea-Clara, em coordenação com barqueiros locais. | proteger rios, canais, diques e comunidades ribeirinhas. |
| `faction.companhia-ambar` | Companhia do Âmbar | Maelor Venn, comerciante carismático que evita cargos públicos. | explorar e comercializar âmbar, resina certificada, fibras e produtos de luxo. |
| `faction.circulo-micelial` | Círculo Micelial | Voz-de-Muitas-Raízes, identidade coletiva usada por uma assembleia; nem todos os micélioides a reconhecem. | proteger comunidades micélioides e estudar redes de fungos sem destruir seus habitats. |
| `faction.rede-guias` | Rede dos Guias | conselho rotativo de guias; a figura mais respeitada é Neralis Vento-Manso. | oferecer travessias seguras em regiões difíceis. |
| `faction.liga-celeiros` | Liga dos Celeiros | Tessa Folhinha. | proteger estoques, sementes e abastecimento em crises. |
| `faction.guarda-espinha` | Guarda da Espinha | Dauren Galho-de-Ferro. | proteger a passagem noroeste e impedir que ruínas perigosas sejam abertas sem preparação. |

## 4. Assentamentos (20)

Os assentamentos ainda **não existem no catálogo técnico**. A cidade indicada é a cidade-mãe política (vínculo, não posição espacial; ver doc 03).

| ID | Nome | Cidade-mãe | Tipo | População estimada | Liderança | ID da liderança |
|---|---|---|---|---|---|---|
| `settlement.raiz-de-prata` | Raiz-de-Prata | Elarion | vila de viveiros e sementes | 1.800 residentes | Nivara Folha-de-Orvalho | `npc.nivara-folha-de-orvalho` |
| `settlement.sete-degraus` | Sete Degraus | Elarion | povoado de ponte, pedágio e hospedagem | 2.400 residentes | Othalen Vira-Pedra | `npc.othalen-vira-pedra` |
| `settlement.junco-alto` | Junco Alto | Nymvar | vila de diques e lavouras ribeirinhas | 3.100 residentes | Beren Três-Canais | `npc.beren-tres-canais` |
| `settlement.tres-barcas` | Três Barcas | Nymvar | entreposto fluvial | 2.200 residentes, com população sazonal adicional ainda não contabilizada | Sava Mão-de-Corrente | `npc.sava-mao-de-corrente` |
| `settlement.ponta-da-concha` | Ponta da Concha | Thalanor | vila pesqueira | 2.700 residentes | Yara Sal-de-Maré | `npc.yara-sal-de-mare` |
| `settlement.farol-de-ambar` | Farol de Âmbar | Thalanor | farol e aldeia costeira | 900 residentes | Terenna Luz-de-Lastro | `npc.terenna-luz-de-lastro` |
| `settlement.copa-funda` | Copa-Funda | Vael-Tir | comunidade de manejo florestal | 1.600 residentes | Arieth Três-Mudas | `npc.arieth-tres-mudas` |
| `settlement.passo-do-veado` | Passo do Veado | Vael-Tir | posto de trilha e mercado sazonal | 1.100 residentes fixos, sem contar visitantes e manadas em passagem | Calar Passo-Curvo | `npc.calar-passo-curvo` |
| `settlement.lodo-claro` | Lodo-Claro | Orvass | aldeia de ervas, filtros e cuidados comunitários | 1.300 residentes | Nima Barro-Manso | `npc.nima-barro-manso` |
| `settlement.ilhas-junco-negro` | Ilhas do Junco Negro | Orvass | conjunto de ilhotas e comunidades aquáticas | 2.000 residentes distribuídos por três comunidades | Vela Três-Margens | `npc.vela-tres-margens` |
| `settlement.vigilia-do-lago` | Vigília do Lago | Caladwen | aldeia de observadores meteorológicos | 1.000 residentes | Orelia Céu-de-Cinza | `npc.orelia-ceu-de-cinza` |
| `settlement.papel-velho` | Papel-Velho | Caladwen | povoado de copistas, encadernadores e restauradores | 1.700 residentes | Marel Tinta-Firme | `npc.marel-tinta-firme` |
| `settlement.eco-baixo` | Eco Baixo | Brumafunda | aldeia de guias | 1.200 residentes | Mira Ouve-Chuva | `npc.mira-ouve-chuva` |
| `settlement.torre-nevoa-mansa` | Torre da Névoa Mansa | Brumafunda | posto de observação com pequena aldeia | 650 residentes | Iven Marca-de-Som | `npc.iven-marca-de-som` |
| `settlement.mel-do-poente` | Mel do Poente | Arven | vila de apiários e pomares | 2.600 residentes | Sena Pólen-Dourado | `npc.sena-polen-dourado` |
| `settlement.campo-tres-familias` | Campo das Três Famílias | Arven | aldeia agrícola | 3.400 residentes | Doria Três-Sulcos | `npc.doria-tres-sulcos` |
| `settlement.mascara-nova` | Máscara Nova | Silvaran | vila de artistas e artesãos | 1.500 residentes | Vessa Fio-Dourado | `npc.vessa-fio-dourado` |
| `settlement.fonte-do-regresso` | Fonte do Regresso | Silvaran | povoado de hospitalidade e abrigo | 1.900 residentes | Dalen Porta-Aberta | `npc.dalen-porta-aberta` |
| `settlement.ferro-folha` | Ferro-Folha | Torvelume | vila de oficinas e reparos | 2.800 residentes | Meda Martelo-Calmo | `npc.meda-martelo-calmo` |
| `settlement.porta-selada` | Porta Selada | Torvelume | assentamento próximo às galerias interditadas | 1.400 residentes | Rovan Cinza-de-Ferro | `npc.rovan-cinza-de-ferro` |

## 5. Pessoas (NPCs)

**Cidade** vem do catálogo, da cidade-mãe do assentamento (líderes) ou `—` quando não deu para determinar sem adivinhar. **Papel** é HERO, GUARDIAN ou BENEFACTOR (tríade), só para quem está no catálogo.

### 5.1 Nos documentos e no catálogo (51)

| ID | Nome | Cidade | Papel | Povo | Vivo | Ficha no documento |
|---|---|---|---|---|---|---|
| `npc.anelra-copa-aberta` | Anelra Copa-Aberta | Arven | HERO | plantae | não |  |
| `npc.bento-sete-sementes` | Bento Sete-Sementes | Arven | BENEFACTOR | pequenino-ribeirinho | sim |  |
| `npc.calar-raiz-curva` | Calar Raiz-Curva | Arven | GUARDIAN | centauro-das-florestas | sim |  |
| `npc.ruma-coracao-solo` | Ruma Coração-de-Solo | Arven | GUARDIAN | plantae | sim |  |
| `npc.sava-broto-novo` | Sava Broto-Novo | Arven | BENEFACTOR | plantae | sim |  |
| `npc.toma-mel-de-ambar` | Toma Mel-de-Âmbar | Arven | HERO | pequenino-ribeirinho | não | Pequenino, falecido há 140 anos. |
| `npc.kheva-ouvido-nevoa` | Kheva Ouvido-de-Névoa | Brumafunda | HERO | homem-corvo-das-torres | sim |  |
| `npc.milo-passo-bruma` | Milo Passo-de-Bruma | Brumafunda | BENEFACTOR | felideo-das-selvas | sim |  |
| `npc.nessa-agua-lenta` | Nessa Água-Lenta | Brumafunda | GUARDIAN | elfo-do-crepusculo | sim |  |
| `npc.nima-rede-micelio` | Nima Rede-de-Micélio | Brumafunda | BENEFACTOR | fungal | sim |  |
| `npc.sela-dos-retornos` | Sela dos Retornos | Brumafunda | HERO | felideo-das-selvas | não | Felídea das Selvas, desaparecida há 70 anos. |
| `npc.selene-mascara-clara` | Selenê Máscara-Clara | Brumafunda | GUARDIAN | feerico | sim |  |
| `npc.sivra-pata-musgo` | Sivra Pata-de-Musgo | Brumafunda | GUARDIAN | felideo-das-selvas | sim |  |
| `npc.teren-asa-corda` | Teren Asa-de-Corda | Brumafunda | GUARDIAN | homem-corvo-das-torres | sim |  |
| `npc.asha-nove-pergaminhos` | Asha Nove-Pergaminhos | Caladwen | BENEFACTOR | elfo-das-estrelas | sim |  |
| `npc.elyon-prisma-claro` | Elyon Prisma-Claro | Caladwen | GUARDIAN | elfo-das-estrelas | sim |  |
| `npc.ithren-quatro-estacoes` | Mestre Ithren das Quatro Estações | Caladwen | HERO | elfo-das-estrelas | não | Elfo das Estrelas, falecido há 260 anos. |
| `npc.avarra-sete-pontes` | Avarra das Sete Pontes | Elarion | HERO | elfo | não | Elfa dos Bosques, falecida na Era da Coroa de Prata. |
| `npc.nilo-recado-certo` | Nilo Recado-Certo | Elarion | BENEFACTOR | homem-corvo-das-torres | sim |  |
| `npc.nimeth-vela-chuva` | Nimeth Vela-de-Chuva | Elarion | GUARDIAN | elfo-das-estrelas | sim |  |
| `npc.parel-tres-parafusos` | Parel Três-Parafusos | Elarion | BENEFACTOR | gnomo-dos-vales | sim | benfeitor vivo dos bairros suspensos |
| `npc.saelith-arco-de-chuva` | Saelith Arco-de-Chuva | Elarion | GUARDIAN | elfo | sim | Elfa dos Bosques, cerca de 240 anos. |
| `npc.dena-tres-correntes` | Dena Três-Correntes | Nymvar | HERO | pequenino-ribeirinho | não | Pequenina ribeirinha, falecida há cerca de 300 anos. |
| `npc.iven-folhas-brancas` | Iven das Folhas Brancas | Nymvar | BENEFACTOR | elfo-dos-bosques | sim |  |
| `npc.mara-barro-claro` | Mara Barro-Claro | Nymvar | GUARDIAN | pequenino-ribeirinho | sim |  |
| `npc.korga-ponta-junco` | Korga Ponta-de-Junco | Orvass | BENEFACTOR | orcano-das-matas | sim |  |
| `npc.nima-raiz-de-lodo` | Nima Raiz-de-Lodo | Orvass | HERO | orcano-das-matas | não | Curandeira orcana das matas, falecida há 90 anos. |
| `npc.oru-rede-chuva` | Oru Rede-de-Chuva | Orvass | HERO | fungal | não |  |
| `npc.syris-canal-velado` | Syris do Canal Velado | Orvass | HERO | elfo-do-crepusculo | não |  |
| `npc.veyra-pavio-silencioso` | Veyra Pavio-Silencioso | Orvass | BENEFACTOR | elfo-do-crepusculo | sim |  |
| `npc.voz-muitas-raizes` | Voz-de-Muitas-Raízes | Orvass | GUARDIAN | fungal | sim | Identidade coletiva micélioide. |
| `npc.alena-quatro-mascaras` | Alena das Quatro Máscaras | Silvaran | HERO | silvano-dos-festivais | não |  |
| `npc.dara-guarda-da-rota` | Dara Guarda-da-Rota | Silvaran | GUARDIAN | satiro | sim |  |
| `npc.dara-passo-ritmico` | Dara Passo-Rítmico | Silvaran | BENEFACTOR | satiro | sim |  |
| `npc.iria-dos-ecos` | Iria dos Ecos | Silvaran | HERO | feerico | sim | guardiã viva da Torre sem Sino |
| `npc.luma-folha-prata` | Luma Folha-de-Prata | Silvaran | BENEFACTOR | feerico | sim |  |
| `npc.miri-corda-de-lua` | Miri Corda-de-Lua | Silvaran | HERO | satiro | não | Silvana Festiva, falecida há 110 anos. |
| `npc.vessa-fio-dourado` | Vessa Fio-Dourado | Silvaran | BENEFACTOR | silvano-dos-festivais | sim | liderança de assentamento |
| `npc.voro-cancao-pedra` | Voro Canção-de-Pedra | Silvaran | GUARDIAN | silvano-dos-festivais | sim |  |
| `npc.yssara-folha-de-sal` | Capitã Yssara Folha-de-Sal | Thalanor | HERO | abissal-das-enseadas | não | Elfa costeira, falecida há 120 anos. |
| `npc.mira-sal-prata` | Mira Sal-de-Prata | Thalanor | BENEFACTOR | abissal-das-enseadas | sim |  |
| `npc.naren-mare-reversa` | Náren da Maré Reversa | Thalanor | GUARDIAN | abissal-das-enseadas | sim |  |
| `npc.talia-cordame-verde` | Tália Cordame-Verde | Thalanor | BENEFACTOR | elfo | sim |  |
| `npc.hadrik-sete-bigornas` | Hadrik das Sete Bigornas | Torvelume | HERO | gnomo-dos-vales | não | Anão residente, falecido há 180 anos. |
| `npc.meda-martelo-calmo` | Meda Martelo-Calmo | Torvelume | GUARDIAN | gnomo-dos-vales | sim | liderança de assentamento |
| `npc.vessa-engrenagem-lua` | Vessa Engrenagem-de-Lua | Torvelume | BENEFACTOR | gnomo-dos-vales | sim |  |
| `npc.eshara-chuva-folha` | Eshara Chuva-na-Folha | Vael-Tir | GUARDIAN | elfo-dos-bosques | sim |  |
| `npc.mirek-junco-ferro` | Mirek Junco-Ferro | Vael-Tir | GUARDIAN | orcano-das-matas | sim |  |
| `npc.oranna-passo-largo` | Oranna Passo-Largo | Vael-Tir | HERO | centauro-das-florestas | sim | Centaura das Florestas, viva, idade aproximada de 96 anos. |
| `npc.oren-galho-quieto` | Oren Galho-Quieto | Vael-Tir | HERO | elfo-dos-bosques | não | Elfo dos Bosques, falecido há 200 anos. |
| `npc.tavro-passo-raiz` | Tavro Passo-de-Raiz | Vael-Tir | BENEFACTOR | centauro-das-florestas | sim |  |

### 5.2 Só nos documentos (44)

| ID | Nome | Cidade | Papel | Povo | Vivo | Ficha no documento |
|---|---|---|---|---|---|---|
| `npc.doria-tres-sulcos` | Doria Três-Sulcos | Arven | — | — | — | liderança de assentamento |
| `npc.sena-polen-dourado` | Sena Pólen-Dourado | Arven | — | — | — | liderança de assentamento |
| `npc.iven-marca-de-som` | Iven Marca-de-Som | Brumafunda | — | — | — | liderança de assentamento |
| `npc.mira-ouve-chuva` | Mira Ouve-Chuva | Brumafunda | — | — | — | liderança de assentamento |
| `npc.marel-tinta-firme` | Marel Tinta-Firme | Caladwen | — | — | — | liderança de assentamento |
| `npc.orelia-ceu-de-cinza` | Orelia Céu-de-Cinza | Caladwen | — | — | — | liderança de assentamento |
| `npc.tirven-eixo-claro` | Tirven Eixo-Claro | Caladwen, Torvelume | — | — | — | artífice histórico, morto há cerca de 210 anos |
| `npc.maereth-cartografa-das-copas` | Maereth, a Cartógrafa das Copas | Elarion | — | — | — | heroína histórica, morta há 430 anos |
| `npc.nivara-folha-de-orvalho` | Nivara Folha-de-Orvalho | Elarion | — | — | — | liderança de assentamento |
| `npc.othalen-vira-pedra` | Othalen Vira-Pedra | Elarion | — | — | — | liderança de assentamento |
| `npc.beren-tres-canais` | Beren Três-Canais | Nymvar | — | — | — | Orcano das Matas, 61 anos. |
| `npc.sava-mao-de-corrente` | Sava Mão-de-Corrente | Nymvar | — | — | — | liderança de assentamento |
| `npc.nima-barro-manso` | Nima Barro-Manso | Orvass | — | — | — | liderança de assentamento |
| `npc.vela-tres-margens` | Vela Três-Margens | Orvass | — | — | — | liderança de assentamento |
| `npc.dalen-porta-aberta` | Dalen Porta-Aberta | Silvaran | — | — | — | liderança de assentamento |
| `npc.terenna-luz-de-lastro` | Terenna Luz-de-Lastro | Thalanor | — | — | — | liderança de assentamento |
| `npc.yara-sal-de-mare` | Yara Sal-de-Maré | Thalanor | — | — | — | liderança de assentamento |
| `npc.rovan-cinza-de-ferro` | Rovan Cinza-de-Ferro | Torvelume | — | — | — | liderança de assentamento |
| `npc.arieth-tres-mudas` | Arieth Três-Mudas | Vael-Tir | — | — | — | liderança de assentamento |
| `npc.calar-passo-curvo` | Calar Passo-Curvo | Vael-Tir | — | — | — | liderança de assentamento |
| `npc.avariel-thorne` | Avariel Thorne | — | — | — | — | Elfo, cerca de 176 anos. |
| `npc.dauren-galho-de-ferro` | Dauren Galho-de-Ferro | — | — | — | — | Anão residente, 118 anos. |
| `npc.edris-do-sul` | Edris do Sul | — | — | — | — | Humano de família antiga de Nymvar, 48 anos. |
| `npc.elarwen-segunda` | Elarwen II — a Rainha das Nascentes | — | — | — | — | Elfa, falecida há séculos. |
| `npc.elsin-varzea-clara` | Elsin Várzea-Clara | — | — | — | — | Pequenina ribeirinha, 64 anos. |
| `npc.eryndra-folha-verde` | Eryndra Folha-Verde | — | — | — | — | Elfa dos Bosques, cerca de 132 anos. |
| `npc.fen-orvaluz` | Fen Orvaluz | — | — | — | — | Felídeo das Selvas, cerca de 46 anos. |
| `npc.ilyra-tres-rios` | Ilyra dos Três Rios — a Curandeira da Vigília | — | — | — | — | Elfa, falecida há cerca de 160 anos. |
| `npc.ivara-polen-de-lume` | Ivara Pólen-de-Lume | — | — | — | — | Feérica, idade desconhecida. |
| `npc.lethariel-nascente-clara` | Lethariel Nascente-Clara | — | — | — | — | Elfa das Estrelas, cerca de 310 anos. |
| `npc.lyris-melodia-clara` | Lyris Melodia-Clara | — | — | — | — | Silvano Festivo, idade aproximada de 72 anos. |
| `npc.maelor-venn` | Maelor Venn | — | — | — | — | Elfo, cerca de 154 anos. |
| `npc.odran-sete-bigornas` | Mestre Odran Sete-Bigornas — o Artífice sem Coroa | — | — | — | — | Anão residente em Torvelume, falecido. |
| `npc.mirel-caule-vivo` | Mirel Caule-Vivo | — | — | — | — | Verdejante, idade incerta; sua comunidade considera a pergunta mal formulada. |
| `npc.nara-tres-fios` | Nara Três-Fios | — | — | — | — | Corvária, cerca de 39 anos. |
| `npc.neralis-vento-manso` | Neralis Vento-Manso | — | — | — | — | Elfo do Crepúsculo, cerca de 190 anos. |
| `npc.maereth-vaelorian` | Princesa Maereth Vaelorian | — | — | — | — | herdeira reconhecida e Guardiã do Pacto das Nascentes |
| `npc.aerendyl-vaelorian` | Rei Aerendyl Vaelorian | — | — | — | — | Rei de Aeltharyn, guardião formal dos Sete Juramentos |
| `npc.selvaris-noctel` | Selvaris Noctel | — | — | — | — | Elfo das Estrelas, cerca de 221 anos. |
| `npc.syranna-das-mares` | Syranna das Marés | — | — | — | — | Elfa costeira, cerca de 205 anos. |
| `npc.tavian-lume-fino` | Tavian Lume-Fino | — | — | — | — | Gnomo dos Vales, cerca de 84 anos. |
| `npc.tessa-folhinha` | Tessa Folhinha | — | — | — | — | Pequenina ribeirinha, 57 anos. |
| `npc.tharion-folha-de-ferro` | Tharion Folha-de-Ferro — o Capitão das Rotas Abertas | — | — | — | — | Elfo dos Bosques, falecido. |
| `npc.vaunor-eir` | Vaunor Eir | — | — | — | — | representante das casas de manejo florestal no Conselho das Sete Copas |

## 7. Fichas mínimas das 44 pessoas só dos documentos (M1.2)

Cada linha repete o que o texto diz; `—` significa que o texto não diz (nada foi completado por dedução). Povo e idade dos 17 líderes de assentamento (menos Beren) não estão escritos nos documentos: entram no M3.1/M3.2. Fichas completas (agenda, medo, conhecimento) para rei e princesa estão em `01-reino-das-florestas.md`, seções 4.1 e 4.2; para os líderes de assentamento, no doc 03.

| ID | Nome | Cidade | Cargo ou papel (texto do doc) | Povo e idade (texto do doc) |
|---|---|---|---|---|
| `npc.aerendyl-vaelorian` | Rei Aerendyl Vaelorian | — | Rei de Aeltharyn, guardião formal dos Sete Juramentos | Elfo dos Bosques, 487 anos |
| `npc.maereth-vaelorian` | Princesa Maereth Vaelorian | — | herdeira reconhecida e Guardiã do Pacto das Nascentes | Elfa das Estrelas, 173 anos |
| `npc.doria-tres-sulcos` | Doria Três-Sulcos | Arven | liderança de assentamento: juíza de terras escolhida por famílias residentes e confirmada em assembleia anual | — |
| `npc.sena-polen-dourado` | Sena Pólen-Dourado | Arven | liderança de assentamento: eleita pelo conselho de apicultores e produtores | — |
| `npc.tessa-folhinha` | Tessa Folhinha | Arven | Prefeita dos Celeiros de Arven | Pequenina ribeirinha, 57 anos |
| `npc.iven-marca-de-som` | Iven Marca-de-Som | Brumafunda | liderança de assentamento: vigia eleito pelos residentes e confirmado por acordo com os Vigias do Bosque | — |
| `npc.mira-ouve-chuva` | Mira Ouve-Chuva | Brumafunda | liderança de assentamento: eleita pela assembleia de guias e moradores que mantêm as trilhas | — |
| `npc.neralis-vento-manso` | Neralis Vento-Manso | Brumafunda | Vigia-Mor de Brumafunda e figura respeitada entre guias | Elfo do Crepúsculo, cerca de 190 anos |
| `npc.marel-tinta-firme` | Marel Tinta-Firme | Caladwen | liderança de assentamento: eleito pelo conselho de oficinas | — |
| `npc.orelia-ceu-de-cinza` | Orelia Céu-de-Cinza | Caladwen | liderança de assentamento: curadora do céu escolhida por observadores locais, com prestação de contas pública a cada estação | — |
| `npc.selvaris-noctel` | Selvaris Noctel | Caladwen | Reitora de Caladwen | Elfo das Estrelas, cerca de 221 anos |
| `npc.avariel-thorne` | Avariel Thorne | Elarion | Intendente de Elarion | Elfo, cerca de 176 anos |
| `npc.maereth-cartografa-das-copas` | Maereth, a Cartógrafa das Copas | Elarion | heroína histórica; desenhou rotas de evacuação nos níveis inferiores da capital | Elfa, morta há 430 anos (pessoa distinta da princesa) |
| `npc.nivara-folha-de-orvalho` | Nivara Folha-de-Orvalho | Elarion | liderança de assentamento: eleita por famílias viveiristas por mandatos de seis anos; a confirmação distrital é formal, mas a recusa real pode provocar disputa | — |
| `npc.othalen-vira-pedra` | Othalen Vira-Pedra | Elarion | liderança de assentamento: magistrado eleito por moradores e comerciantes a cada quatro anos | — |
| `npc.beren-tres-canais` | Beren Três-Canais | Nymvar | liderança de assentamento: construtor de diques escolhido pelo conselho de famílias e equipes de manutenção | Orcano das Matas, 61 anos |
| `npc.elsin-varzea-clara` | Elsin Várzea-Clara | Nymvar | Matriarca dos canais de Nymvar | Pequenina ribeirinha, 64 anos |
| `npc.sava-mao-de-corrente` | Sava Mão-de-Corrente | Nymvar | liderança de assentamento: mestra das barcas escolhida pelas guildas e sujeita a revisão pública anual | — |
| `npc.nima-barro-manso` | Nima Barro-Manso | Orvass | liderança de assentamento: escolhida por curandeiros e famílias por mandato revogável | — |
| `npc.vela-tres-margens` | Vela Três-Margens | Orvass | liderança de assentamento: porta-voz escolhido pelas comunidades, com mandato revogável em assembleia | — |
| `npc.dalen-porta-aberta` | Dalen Porta-Aberta | Silvaran | liderança de assentamento: anfitrião comunal escolhido por bairros e substituível por votação | — |
| `npc.lyris-melodia-clara` | Lyris Melodia-Clara | Silvaran | Anfitriã das Estações de Silvaran | Silvano Festivo, idade aproximada de 72 anos |
| `npc.syranna-das-mares` | Syranna das Marés | Thalanor | Almirante-civil de Thalanor | Elfa costeira, cerca de 205 anos |
| `npc.terenna-luz-de-lastro` | Terenna Luz-de-Lastro | Thalanor | liderança de assentamento: guardiã escolhida pelos moradores e confirmada pela autoridade portuária | — |
| `npc.yara-sal-de-mare` | Yara Sal-de-Maré | Thalanor | liderança de assentamento: porta-voz eleita por famílias de pesca e capitães de barcos pequenos | — |
| `npc.dauren-galho-de-ferro` | Dauren Galho-de-Ferro | Torvelume | Comandante-civil de Torvelume | Anão residente, 118 anos |
| `npc.rovan-cinza-de-ferro` | Rovan Cinza-de-Ferro | Torvelume | liderança de assentamento: administrador civil escolhido pelos moradores, sujeito a confirmação comunitária e inspeção da fortaleza | — |
| `npc.arieth-tres-mudas` | Arieth Três-Mudas | Vael-Tir | liderança de assentamento: escolhida pelo Círculo de Guardiões por voto das famílias residentes e dos trabalhadores de manejo | — |
| `npc.calar-passo-curvo` | Calar Passo-Curvo | Vael-Tir | liderança de assentamento: guardião rotativo escolhido por comunidades que utilizam a rota | — |
| `npc.eryndra-folha-verde` | Eryndra Folha-Verde | Vael-Tir | Guardiã de Trilhas em Vael-Tir | Elfa dos Bosques, cerca de 132 anos |
| `npc.edris-do-sul` | Edris do Sul | — | Representante provisório das cidades de várzea | Humano de família antiga de Nymvar, 48 anos |
| `npc.elarwen-segunda` | Elarwen II — a Rainha das Nascentes | — | Negociou o Pacto das Nascentes após a Guerra das Cinzas Verdes | Elfa, falecida há séculos |
| `npc.fen-orvaluz` | Fen Orvaluz | — | Guia e caçador de resgate que opera entre Vael-Tir e Brumafunda | Felídeo das Selvas, cerca de 46 anos |
| `npc.ilyra-tres-rios` | Ilyra dos Três Rios — a Curandeira da Vigília | — | Coordenou a quarentena de sementes e distribuiu remédios durante a Vigília Longa | Elfa, falecida há cerca de 160 anos |
| `npc.ivara-polen-de-lume` | Ivara Pólen-de-Lume | — | Mediadora informal entre Silvaran e comunidades feéricas | Feérica, idade desconhecida |
| `npc.lethariel-nascente-clara` | Lethariel Nascente-Clara | — | Arquivista-mor | Elfa das Estrelas, cerca de 310 anos |
| `npc.maelor-venn` | Maelor Venn | — | Comerciante da Companhia do Âmbar | Elfo, cerca de 154 anos |
| `npc.odran-sete-bigornas` | Mestre Odran Sete-Bigornas — o Artífice sem Coroa | — | Reparou ferramentas, pontes e portões durante uma geração | Anão residente em Torvelume, falecido |
| `npc.mirel-caule-vivo` | Mirel Caule-Vivo | — | Representante de comunidades vegetais | Verdejante, idade incerta; sua comunidade considera a pergunta mal formulada |
| `npc.nara-tres-fios` | Nara Três-Fios | — | Mensageira e restauradora de mapas em Elarion | Corvária, cerca de 39 anos |
| `npc.tavian-lume-fino` | Tavian Lume-Fino | — | Negociador da Liga das Rotas | Gnomo dos Vales, cerca de 84 anos |
| `npc.tharion-folha-de-ferro` | Tharion Folha-de-Ferro — o Capitão das Rotas Abertas | — | Manteve rotas de evacuação durante a Vigília Longa e recusou abandonar aldeias isoladas | Elfo dos Bosques, falecido |
| `npc.tirven-eixo-claro` | Tirven Eixo-Claro | — | artífice histórico (comportas modulares), lembrado em Caladwen e Arven | Gnomo dos Vales, morto há cerca de 210 anos |
| `npc.vaunor-eir` | Vaunor Eir | — | representante das casas de manejo florestal no Conselho das Sete Copas; quer cotas rígidas de extração | — |

Sucessão e vacância: ver doc 04, seção 6 (6.1 para autoridades locais). Conselho das Sete Copas: doc 01, seção 4.3, e doc 04, seção 3.

## 8. Pendências

1. **44 pessoas só nos documentos** (seção 5.2): precisam de entrada no catálogo na integração. a seção 7 traz a ficha mínima de todas (M1.2 feito; inclui Vaunor Eir, D-08); povo e idade ainda faltam para 17 líderes de assentamento.
2. **Facções**: liderança só por nome; falta ligar cada facção a um `npc.*` (M1.3).
3. **Sem entrada de catálogo**: 10 facções e 20 assentamentos (M1.3).
4. **Cidade em branco** (`—`): governo das cidades e lideranças de facção. M3.1 cobriu só Elarion (`12-fichas-elarion.md`); rei e princesa aguardam D-19; Edran Sem-Selo sem ID (D-18); demais cidades seguem em branco.
5. **Arven sem benfeitora** depois de D-07 (Vessa passou a Silvaran): decidir quem ocupa a vaga na tríade (M3.1).

## 8. Facções como dados (M1.3a)

Fonte: doc 01, seção 7. Campo sem texto no doc fica como *não informado no doc 01* (lacuna explícita; nada inventado). **Presença em cidades** = cidades cuja lista de facções do doc 01 traz o nome exato da facção; **presença em assentamentos** = assentamentos cuja linha *Relações* (doc 03) cita a facção. Outras organizações citadas nas cidades (ex.: Vigias da Costa, Guarda das Copas) não são das 10 facções e não entram.

| ID | Liderança (ID) | Recursos | Métodos | Tensão ou conflito | Aliados | Rivais | Presença: cidades | Presença: assentamentos |
|---|---|---|---|---|---|---|---|---|
| `faction.vigias-primeiro-verde` | `npc.saelith-arco-de-chuva` | patrulheiros, mapas de trilhas, postos de observação e aliados locais. | patrulha, apreensão, negociação e, em casos graves, fechamento de trilhas. | alguns querem mais autonomia; outros obedecem estritamente à Coroa. | comunidades de Vael-Tir, centauros e parte dos Verdejantes. | extratores ilegais e políticos que tratam toda restrição como obstáculo econômico. | `city.vael-tir`; unidades locais em `city.brumafunda`, `city.orvass`, `city.thalanor` | `settlement.copa-funda` |
| `faction.liga-das-rotas` | `npc.tavian-lume-fino` | barqueiros, caravanas, crédito comercial, armazéns e mapas. | contratos, pressão econômica, arbitragem e informação. | financia infraestrutura útil, mas alguns membros toleram contrabando lucrativo. | cidades mercantis e parte dos gnomos. | inspetores corruptos, monopólios de portagem e grupos que fecham rotas sem consulta. | `city.arven`, `city.elarion`, `city.nymvar` | `settlement.tres-barcas` |
| `faction.arquivistas-seiva` | `npc.lethariel-nascente-clara` | arquivos, escribas, copistas, linguistas e acesso limitado a câmaras antigas. | pesquisa, catalogação, autenticação e publicação. | a instituição protege documentos, mas a corte pode restringir o acesso a registros politicamente perigosos. | não informado no doc 01 | não informado no doc 01 | `city.caladwen`, `city.elarion` | nenhum citado |
| `faction.circulo-estacoes` | `npc.lyris-melodia-clara` | redes de anfitriões, artistas, mensageiros e mediadores. | festas, juramentos, mediação e proteção de refugiados. | alguns membros defendem abertura cultural; outros temem que o comércio transforme ritos em espetáculo. | não informado no doc 01 | não informado no doc 01 | `city.silvaran` | nenhum citado |
| `faction.irmandade-aguas` | `npc.elsin-varzea-clara` | embarcações, engenheiros de canal, mapas de cheias e reservas. | manutenção, alertas, negociação e bloqueios temporários em emergências. | disputa recursos com a guarda real e com proprietários que desviam água. | não informado no doc 01 | não informado no doc 01 | `city.orvass`, `city.nymvar`; unidade local em `city.thalanor` | `settlement.junco-alto`, `settlement.lodo-claro` |
| `faction.companhia-ambar` | `npc.maelor-venn` | capital, armazéns, navios e contratos. | patrocínio, compra de concessões e pressão sobre tarifas. | uma ala da companhia compra resina ilegal; a liderança afirma não controlar todos os agentes. A culpa precisa ser investigada, não presumida. | não informado no doc 01 | não informado no doc 01 | `city.thalanor`, `city.torvelume` | `settlement.raiz-de-prata`, `settlement.ponta-da-concha`, `settlement.copa-funda` |
| `faction.circulo-micelial` | `npc.voz-muitas-raizes` | conhecimento de esporos, rotas úmidas, remédios e decomposição. | troca de conhecimento, quarentena e negociação comunitária. | a linguagem e os ritmos de decisão do círculo são frequentemente interpretados como evasão por autoridades da capital. | não informado no doc 01 | não informado no doc 01 | `city.orvass` | nenhum citado |
| `faction.rede-guias` | `npc.neralis-vento-manso` | mapas, sinais de trilha, abrigo e conhecimento local. | contratos, treinamento, resgate e cobrança por rotas perigosas. | alguns guias escondem rotas para manter o monopólio; a maioria depende de reputação e segurança. | não informado no doc 01 | não informado no doc 01 | `city.brumafunda`, `city.vael-tir` | `settlement.eco-baixo` |
| `faction.liga-celeiros` | `npc.tessa-folhinha` | celeiros distribuídos, agricultores, sementes e transporte. | reserva coletiva, empréstimos e distribuição emergencial. | proprietários ricos tentam controlar os estoques; a liga não é imune à corrupção. | não informado no doc 01 | não informado no doc 01 | `city.arven` | `settlement.raiz-de-prata`, `settlement.mel-do-poente`, `settlement.fonte-do-regresso` |
| `faction.guarda-espinha` | `npc.dauren-galho-de-ferro` | fortificação, patrulhas, ferramentas e depósitos. | controle de passagem, escolta e interdição. | recebe ordens da Coroa, mas responde também aos moradores que mantêm a fortaleza. | não informado no doc 01 | não informado no doc 01 | `city.torvelume` | nenhum citado |

Lacunas explícitas: aliados e rivais só estão escritos para as facções que os trazem no doc 01; as demais ficam *não informado* até decisão do autor (M1.5 ou fase 2/3).

## 9. Assentamentos como dados (M1.3b)

Fonte: doc 03. Cada vínculo aponta para ID existente neste registro (cidade-mãe e liderança).

| ID | Cidade-mãe | Liderança (ID) | Obrigação | Interesse local | Tensão |
|---|---|---|---|---|---|
| `settlement.raiz-de-prata` | `city.elarion` | `npc.nivara-folha-de-orvalho` | manter mudas de espécies comuns e uma reserva emergencial de árvores nativas. | proteger sementes adaptadas ao solo da vila, inclusive variedades que a corte considera pouco produtivas. | a Coroa exige mudas de espécies raras para restaurar áreas nobres; Nivara afirma que retirar as sementes locais agora comprometerá a próxima estação. |
| `settlement.sete-degraus` | `city.elarion` | `npc.othalen-vira-pedra` | manter a ponte transitável e garantir passagem a mensageiros oficiais, equipes de socorro e evacuações. | receber pagamento pelo desgaste da ponte e impedir que a isenção de nobres recaia sobre os demais viajantes. | os registros de pedágio não explicam por que três casas nobres atravessam sem pagar. Othalen suspeita de privilégio legal, mas ainda não viu o decreto original. |
| `settlement.junco-alto` | `city.nymvar` | `npc.beren-tres-canais` | manter comportas e fornecer equipes durante cheias extraordinárias. | priorizar reparos preventivos em vez de grandes obras cerimoniais na capital. | a vila alega que o trecho superior recebe manutenção primeiro, desviando o risco de inundação para bairros abaixo. Beren foi acusado de desviar materiais, mas ainda precisa apresentar todos os registros. |
| `settlement.tres-barcas` | `city.nymvar` | `npc.sava-mao-de-corrente` | manter embarcações de emergência e publicar tarifas de travessia. | ajustar os preços à corrente, à carga e ao risco sem perder o direito de negociar livremente. | a cidade exige tarifa máxima durante crises; barqueiros afirmam que a regra os obriga a operar com prejuízo quando a corrente fica perigosa. |
| `settlement.ponta-da-concha` | `city.thalanor` | `npc.yara-sal-de-mare` | manter vigias costeiros e participar do resgate de embarcações quando convocada. | conservar áreas de pesca tradicionais e impedir que licenças favoreçam grandes frotas. | novas restrições de pesca foram impostas depois de relatos de contaminação, mas nenhuma compensação foi paga às famílias afetadas. |
| `settlement.farol-de-ambar` | `city.thalanor` | `npc.terenna-luz-de-lastro` | manter os sinais marítimos e registrar embarcações desconhecidas. | preservar a autonomia dos faroleiros para emitir alertas mesmo quando isso prejudica o comércio. | a guarda portuária quer usar o farol para observação política e censura de sinais; os moradores temem que atrasos de autorização provoquem naufrágios. |
| `settlement.copa-funda` | `city.vael-tir` | `npc.arieth-tres-mudas` | cumprir cotas de extração e patrulhar os limites ancestrais. | obter cotas ajustadas à praga que reduziu as árvores aptas ao corte, em vez de compensar a perda com corte em áreas novas. | a cota real foi calculada com dados anteriores à praga. Se a vila cumprir a ordem, pode perder a capacidade de regeneração; se recusar, arrisca sanções. |
| `settlement.passo-do-veado` | `city.vael-tir` | `npc.calar-passo-curvo` | manter a passagem e suspender a caça durante a migração acordada. | permitir comércio sem transformar o corredor em estrada permanente. | a Liga das Rotas quer alargar a trilha; comunidades centauras afirmam que o projeto reduzirá a largura segura de passagem. |
| `settlement.lodo-claro` | `city.orvass` | `npc.nima-barro-manso` | compartilhar amostras de água e fornecer apoio em surtos, preservando as reservas locais. | regular a coleta de plantas raras conforme a capacidade de regeneração. | licenças de Orvass permitem coleta comercial acima do que a aldeia considera sustentável; a cidade acusa a aldeia de esconder recursos públicos. |
| `settlement.ilhas-junco-negro` | `city.orvass` | `npc.vela-tres-margens` | manter passagens aquáticas e abrigos de cheia. | conservar o direito de usar mapas sazonais e canais que não constam nos mapas oficiais. | guardas detêm barqueiros por usar passagens fora dos registros da cidade; os moradores alegam que os canais mudam naturalmente e que os mapas oficiais estão desatualizados. |
| `settlement.vigilia-do-lago` | `city.caladwen` | `npc.orelia-ceu-de-cinza` | manter registros climáticos e compartilhar alertas de risco com as cidades conectadas. | publicar dados úteis à população sem entregar observações sensíveis para uso militar exclusivo. | o Colégio dos Ciclos quer classificar certos padrões como informação estratégica; agricultores afirmam que atrasar alertas custa colheitas e vidas. |
| `settlement.papel-velho` | `city.caladwen` | `npc.marel-tinta-firme` | conservar cópias públicas de documentos frágeis e informar danos irreversíveis. | preservar cópias independentes e permitir que fontes divergentes sejam comparadas. | casas nobres compram cópias privadas e pressionam oficinas a não reproduzir versões que contradizem a história oficial. |
| `settlement.eco-baixo` | `city.brumafunda` | `npc.mira-ouve-chuva` | atualizar marcos de trilha e participar de buscas por desaparecidos. | proteger caminhos residenciais e rotas de resgate sem publicá-los todos para comerciantes e caçadores. | mercadores acusam os guias de esconder rotas para criar monopólio; os moradores temem que divulgar cada passagem leve caçadores a áreas frágeis. |
| `settlement.torre-nevoa-mansa` | `city.brumafunda` | `npc.iven-marca-de-som` | emitir alertas e fechar trilhas quando houver risco verificável. | manter a autoridade de fechar a passagem com base em sinais locais, sem esperar autorização distante. | estudiosos de Caladwen querem manter a torre aberta para observar um fenômeno; os vigias dizem que os experimentos aumentam o risco para viajantes. |
| `settlement.mel-do-poente` | `city.arven` | `npc.sena-polen-dourado` | conservar corredores de polinização e contribuir com sementes durante emergências. | proteger flores silvestres e diversidade de abelhas, mesmo quando reduzem a área disponível para produção intensiva. | grandes produtores querem ampliar os campos e remover faixas floridas; apicultores alertam que a colheita futura cairá se a polinização piorar. |
| `settlement.campo-tres-familias` | `city.arven` | `npc.doria-tres-sulcos` | entregar a contribuição de grãos pactuada segundo a colheita efetiva. | rever a base de tributo calculada a partir de uma safra excepcional que não se repetiu. | a administração regional cobra a quantia antiga, alegando que o pacto não foi formalmente alterado; famílias endividadas ameaçam abandonar terras. |
| `settlement.mascara-nova` | `city.silvaran` | `npc.vessa-fio-dourado` | manter uma casa de acolhimento durante os festivais e oferecer apresentações comunitárias em ocasiões pactuadas. | preservar o direito de artistas recusarem apresentações sem perder acesso a oficinas e moradia. | Silvaran interpreta a obrigação cultural como serviço gratuito obrigatório; artistas alegam que a exigência se tornou exploração de trabalho. |
| `settlement.fonte-do-regresso` | `city.silvaran` | `npc.dalen-porta-aberta` | oferecer abrigo temporário a viajantes em perigo e refugiados durante emergências reconhecidas. | manter o acolhimento como dever comunitário sem transferir todo o custo às famílias anfitriãs. | moradores discordam sobre acolher refugiados de uma disputa política; alguns temem represálias, outros consideram a recusa uma violação do pacto de hospitalidade. |
| `settlement.ferro-folha` | `city.torvelume` | `npc.meda-martelo-calmo` | reparar ferramentas e estruturas comuns conforme prioridades pactuadas. | dar prioridade a pontes, diques e ferramentas civis antes de encomendas militares extraordinárias. | a guarnição exige armamentos e peças de fortificação enquanto estruturas civis aguardam reparo. |
| `settlement.porta-selada` | `city.torvelume` | `npc.rovan-cinza-de-ferro` | vigiar os acessos às galerias, relatar tremores e impedir entradas não autorizadas. | garantir salários e investigação transparente antes de qualquer reabertura. | trabalhadores querem retomar a mineração; famílias temem que o selamento esconda negligência, perigo real ou prova de irregularidade antiga. |

Conferência (25/10/2026): 10 facções e 20 assentamentos sem campo vazio; 10 IDs de liderança de facção e 20 de assentamento existem na seção 5; 20 cidades-mãe existem na seção 2.

## 10. Unidades locais e sinônimos (M1.5, D-09 e D-10)

Organizações citadas nas listas de facções das cidades (doc 01, seção 5) sem ID próprio. Decisão D-09: são **unidades locais** das 10 facções. Onde o texto não indica a facção-mãe, fica **a definir** (lacuna explícita; nada deduzido). Não recebem ID `faction.*`.

| Unidade local | Cidade | Facção-mãe |
|---|---|---|
| Vigias do Bosque | `city.brumafunda` | `faction.vigias-primeiro-verde` |
| Vigias do Brejo | `city.orvass` | `faction.vigias-primeiro-verde` |
| Vigias da Costa | `city.thalanor` | `faction.vigias-primeiro-verde` |
| Irmandade dos Barqueiros | `city.nymvar` (D-10), `city.thalanor` | `faction.irmandade-aguas` |
| Guarda das Copas | `city.elarion` | a definir |
| Círculo das Trilhas | `city.vael-tir` | a definir |
| Círculo dos Curandeiros | `city.nymvar` | a definir |
| Colégio dos Ciclos | `city.caladwen` | a definir |
| Oficina de Precisão | `city.caladwen` | a definir |
| Círculo dos Ecos | `city.brumafunda` | a definir |
| Irmandade dos Apicultores | `city.arven` | a definir |
| Artífices do Som | `city.silvaran` | a definir |
| Liga dos Artífices | `city.torvelume` | a definir |

A Casa da Hospitalidade (Silvaran) segue como instituição, não facção (doc 01, seção 5.9).

## 11. Faixas de população (M2.2)

Fonte e regras: doc 05, seções 1.1 a 1.2.0. A população "(texto do doc)" das seções 2 e 4 é o valor do perfil (escala narrativa); o valor de uso é a faixa. Todas as categorias demográficas estão **indefinidas**; nada se soma.

| ID | Faixa de uso |
|---|---|
| `city.elarion` | mais de 50.000 |
| `city.nymvar` | 30.000–49.999 |
| `city.thalanor` | 30.000–49.999 |
| `city.vael-tir` | 15.000–29.999 |
| `city.orvass` | 15.000–29.999 |
| `city.caladwen` | 15.000–29.999 |
| `city.brumafunda` | 10.000–14.999 |
| `city.arven` | 15.000–29.999 |
| `city.silvaran` | 15.000–29.999 |
| `city.torvelume` | 15.000–29.999 |
| `settlement.raiz-de-prata` | 1.000–1.999 |
| `settlement.sete-degraus` | 2.000–2.999 |
| `settlement.junco-alto` | 3.000–3.999 |
| `settlement.tres-barcas` | 2.000–2.999 |
| `settlement.ponta-da-concha` | 2.000–2.999 |
| `settlement.farol-de-ambar` | 500–999 |
| `settlement.copa-funda` | 1.000–1.999 |
| `settlement.passo-do-veado` | 1.000–1.999 fixos; visitantes e manadas não quantificados |
| `settlement.lodo-claro` | 1.000–1.999 |
| `settlement.ilhas-junco-negro` | 2.000–2.999 |
| `settlement.vigilia-do-lago` | 1.000–1.999 |
| `settlement.papel-velho` | 1.000–1.999 |
| `settlement.eco-baixo` | 1.000–1.999 |
| `settlement.torre-nevoa-mansa` | 500–999 |
| `settlement.mel-do-poente` | 2.000–2.999 |
| `settlement.campo-tres-familias` | 3.000–3.999 |
| `settlement.mascara-nova` | 1.000–1.999 |
| `settlement.fonte-do-regresso` | 1.000–1.999 |
| `settlement.ferro-folha` | 2.000–2.999 |
| `settlement.porta-selada` | 1.000–1.999 |

## 12. Competências pelo catálogo (M3.2, versão 7)

Arquivo: `12-competencias.csv` (95 pessoas: raça, sub-raça, classe, profissão, capacidades, limites, evidência). Todos os IDs de raça, sub-raça, classe e profissão existem em `referencia/dados-brutos/personagem-catalogo.json` (conferido por script em 25/10/2026).

- **Raça e sub-raça:** do campo povo do registro (51) ou do texto dos docs (26). 18 pessoas ficam `—`: 17 lideranças de assentamento e Vaunor Eir (povo não escrito).
- **Classe (32) e profissão (42):** *proposta* (ASSUMINDO), só onde o cargo ou a obra descrita no texto sustenta; `—` onde não há base. Nada foi deduzido para fechar buraco; o autor pode trocar qualquer uma.
- **Capacidades e limites:** das 51 figuras do catálogo, o texto do `.kt` (não canônico); das demais, o texto do doc 01; `—` onde o doc não diz.
- **Divergências de povo (D-20):** Yssara Folha-de-Sal (registro: `abissal-das-enseadas`; doc 01: "Elfa costeira") e Hadrik das Sete Bigornas (registro: `gnomo-dos-vales`; doc 01: "Anão residente"). O CSV segue o registro.

## 13. Relações e conhecimento (M3.3, versão 8)

- `12-relacoes.csv`: 76 relações (origem, destino por ID ou texto, tipo, descrição, fonte). Tipos: cooperação 17, dependência 14, rivalidade 8, discordância 8, desconfiança 7, negociação 5, vínculo 5, tensão 4, afeto 3, respeito 2, dívida 2, ligação comercial 1. 29 têm destino sem ID (grupo ou instituição só em texto: ex. "Coroa", "guarda real"); não ganharam ID.
- `12-conhecimento.csv`: 59 linhas para 33 pessoas, com estado *confirmado*, *suspeita* ou *desconhecido* e a fonte. Cobre as 20 lideranças de assentamento, a Coroa e os NPCs vivos do doc 01 §9 com conhecimento escrito; as demais pessoas ficam sem linha (nada escrito no texto).
- Regra: toda linha tem fonte (doc 03 §, doc 01 § ou `.kt`). Origem e destino por ID existem neste registro (conferido por script). Os tipos *afeto*, *dívida*, *rivalidade* e *medo* seguem o roadmap; *medo* não aparece porque nenhum texto escreve medo entre duas pessoas. Rivalidade entre pessoas só quando o texto diz.
- ASSUMINDO: "foi amiga de Maereth" (Saelith, doc 01 §9) refere-se à Princesa Maereth Vaelorian, pela menção a "reforma".

## 14. Voz e ganchos de diálogo (M3.4, versão 9)

`12-voz.csv`: 25 NPCs principais (os 23 do doc 01 §4 e §9 com personalidade escrita, mais Nimeth e Parel). Cada um tem *tom* (1 linha), 2 frases-modelo (separadas por `|`) e *nunca diz*. Uso: instrução de tom, não texto de cena.

- **Base:** personalidade, objetivos e limites já escritos (coluna `base`). As frases são **propostas de tom** (ASSUMINDO), não falas canônicas; o autor pode trocá-las.
- *Nunca diz* vem de limites e recusas escritos no texto; nenhum segredo novo foi criado e nenhum segredo da seção 13 aparece como fala.
- Fora desta lista: heróis históricos (mortos), lideranças de assentamento e demais pessoas sem personalidade escrita.
