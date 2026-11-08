# 12 — Registro mestre de entidades do reino 2 (R2-2.1, versão 1)

**Estado:** rascunho de R2-2.1 para aprovação do autor · **Data:** 2026-11-03
**Etiqueta:** **[PROPOSTO]**; **não é cânone novo**: só organiza o que já está nos docs 01, 03, 04, 05, 06 e 10 (D-31 a D-37). Onde um dado não foi encontrado, a célula traz `—`; nada foi inventado.
**Uso:** fonte única de IDs do reino 2. Antes de criar NPC, assentamento ou facção, procure aqui. **Um ID nunca é reutilizado.**
**Arquivo irmão:** `12-registro-mestre.csv` (formato de D-21: `tipo,id,nome,situacao,id_catalogo,cidade,papel,povo,vivo,extra,ficha_doc`; **coluna a mais** `povo_id` com o ID da raça-base do catálogo). **ASSUMINDO:** `id_catalogo` fica `—` porque o catálogo técnico não tem figuras nomeadas, só raças, sub-raças, classes, profissões e híbridos; o alinhamento por NPC está em `referencia/racas-e-povos/ALINHAMENTO_COROAS.md` (R2-2.3).
**Arquivos irmãos (R2-2.2):** `12-facoes.csv` (14 facções: id, nome, liderança, objetivo, recursos, métodos, tensão, aliados, rivais, presença em cidades e assentamentos) e `12-assentamentos.csv` (20 assentamentos: id, cidade-mãe, liderança, obrigação, interesse local, tensão), no mesmo molde do reino 1; `12-demografia.csv` (R2-3.2: 10 cidades e 20 assentamentos com faixa de porte relativo, sem número); `12-fichas.csv` (R2-4.1: 12 fichas completas dos principais); `12-competencias.csv` (R2-4.2: capacidades e limites das 45 pessoas, com IDs do catálogo); `12-relacoes.csv` e `12-conhecimento.csv` (R2-4.3: relações com fonte e confirmado, suspeita e desconhecido); `12-voz.csv` (R2-4.4: tom, frases-modelo e o que os 12 principais nunca dizem); `12-relogios.csv` (R2-5.1: os 7 relógios com gatilhos, trava e inação); `12-eventos.csv` (R2-5.2: 13 eventos-tipo com cadeia causal); `12-recursos.csv` (R2-3.3: 84 linhas de recurso por localidade, níveis ordinais 0 a 5 provisórios, reserva `?`). Aliados e rivais vêm como IDs de facção; onde o doc 04 diz que não há, a célula traz `desconhecido`; unidades locais aparecem entre parênteses na presença em cidades.
**Fases seguintes:** alinhamento ao catálogo (R2-2.3) e conferência cruzada (R2-2.4).

## 1. Resumo

| Tipo | Quantidade |
|---|---:|
| Cidades | 10 |
| Facções | 14 |
| Assentamentos | 20 |
| Pessoas (NPCs) distintas | 45 |

Pessoas por situação:

| Situação | Qtd |
|---|---:|
| proposto, ficha completa (D-33) | 12 |
| proposto, herói lendário (D-33) | 13 |
| proposto, só liderança (sem ficha) | 20 |

Pessoas por povo (ID do catálogo):

| Povo | ID | Qtd |
|---|---|---:|
| Anão | `anao` | 9 |
| Gnomo (das rochas) | `gnomo` | 6 |
| Draconato | `draconato` | 5 |
| Cristalino | `cristalino` | 4 |
| Construto Desperto | `construto` | 4 |
| Nascido da Brasa | `elemental-fogo` | 3 |
| Labiríntico | `minotauro` | 3 |
| Felino das Nuvens | `felino-das-nuvens` | 3 |
| Escamado Miúdo | `kobold` | 3 |
| Giganteano | `gigante` | 2 |
| Altaneiro | `goliath` | 2 |
| Nascido da Rocha | `elemental-terra` | 1 |

**Conferência:** 0 ID repetido; 0 ID citado nos docs sem definição; 0 cidade-mãe inexistente; 0 liderança inexistente; os 12 `povo_id` existem no catálogo (`races`).

## 2. Cidades (10)

| ID | Nome | Tipo e governo local | Ref. |
|---|---|---|---|
| `city.brasalume` | Brasalume — A Forja dos Dentes | forja vulcânica e cidade-fornalha \| governo: Conselho das Famílias de Fogo, presidido por Gundrik Muralha-Lenta (Guarda-Mor das Fornalhas); assento rotativo no Concílio das Coroas (doc 04 §9). | C-01 |
| `city.altavento` | Altavento — A Cidade dos Picos | cidade de altitude, ninhos e passos de vento \| governo: Assembleia dos Picos, com Vireo Vento-Rápido como Voz dos Picos; assento rotativo no Concílio das Coroas (doc 04 §9). | C-02 |
| `city.veiaclara` | Veiaclara — A Cidade da Nascente | cidade de nascente e gelo derretido \| governo: Conselho da Nascente, convocado por Pelma Gota-Medida (Medidora-Mor); a água é decidida pelo conselho, não por ela (doc 04 §9). | C-03 |
| `city.barcaalta` | Barcaalta — O Porto da Cabeceira | porto fluvial de altitude na cabeceira do Argente \| governo: Mesa do Cais, presidida por Torgrim Corda-Dupla (Mestre do Cais); acúmulo intencional com a Companhia da Descida (doc 04 §9). | C-04 |
| `city.portao-cinza` | Portão-Cinza — O Limiar do Esporão | cidade-fronteira e posto de descida \| governo: Mesa do Portão, presidida por Thaurak Escama-Fria (Portão-Mor); acúmulo intencional com os Vigias do Limiar (doc 04 §9). | C-05 |
| `city.sinuosa` | Sinuosa — A Cidade dos Mil Corredores | cidade-labirinto subterrânea \| governo: Rotunda dos Mapas, com presidência rotativa entre famílias de guias; neste ciclo, Ilmeth Curva-Segura (doc 04 §9). | C-06 |
| `city.cumieira` | Cumieira — A Casa dos Grandes | cidade monumental de grande escala \| governo: Salão dos Juramentos de Sangue, com Gorrak Pedra-Grande como Primeiro do Salão; acúmulo intencional com o Conselho de Sangue (doc 04 §9). | C-07 |
| `city.rodaviva` | Rodaviva — A Cidade que Despertou | cidade de oficinas de construtos \| governo: Assembleia das Oficinas, presidida neste ciclo por Fenwick Mola-Solta; Construtos sem dono não votam (doc 04 §9). | C-08 |
| `city.geodalia` | Geodália — A Cidade das Facetas | cidade de cristal e luz \| governo: Câmara das Facetas, com Iridel Luz-Partida como Primeira da Câmara; acúmulo intencional com o Coro das Facetas (doc 04 §9). | C-09 |
| `city.pedra-ata` | Pedra-Ata — A Cidade do Concílio | cidade-sede do arquivo e do Concílio das Coroas \| governo: Mesa de Pedra-Ata, presidida por Velka Selo-Firme (Guardiã do Selo), que também é presidente de guarda do Concílio (doc 04 §2.2 e §9). | C-10 |

## 3. Facções (14)

| ID | Nome | Liderança e cidades | Ref. |
|---|---|---|---|
| `faction.casa-das-fornalhas` | Casa das Fornalhas | liderança: npc.karna-brasa-mansa \| cidades: `city.brasalume`. **Assentamentos:** `settlement.cinzeiro` (cobra a cinza das fornalhas). | F-01 |
| `faction.irmandade-do-veio` | Irmandade do Veio | liderança: npc.brundar-veio-alto \| cidades: `city.brasalume`. **Assentamentos:** `settlement.veio-fundo`. | F-02 |
| `faction.rede-dos-passos-altos` | Rede dos Passos Altos | liderança: npc.zhae-garra-de-nevoa \| cidades: `city.altavento`. **Assentamentos:** `settlement.ninho-seco`, `settlement.rastro-branco`. | F-03 |
| `faction.casa-das-comportas` | Casa das Comportas | liderança: npc.pimm-veio-pequeno \| cidades: `city.veiaclara`. **Assentamentos:** `settlement.comporta-velha`. | F-04 |
| `faction.companhia-da-descida` | Companhia da Descida | liderança: npc.torgrim-corda-dupla \| cidades: `city.barcaalta`, `city.portao-cinza` (Albergue-de-Cima). **Assentamentos:** `settlement.cordoaria`, `settlement.albergue-de-cima`. | F-05 |
| `faction.vigias-do-limiar` | Vigias do Limiar | liderança: npc.thaurak-escama-fria \| cidades: `city.portao-cinza`. **Assentamentos:** `settlement.galeria-alta` (Varkhan Escama-Vigia, `npc.varkhan-escama-vigia`). | F-06 |
| `faction.casa-dos-guias` | Casa dos Guias | liderança: npc.nael-curva-antiga \| cidades: `city.sinuosa`. **Assentamentos:** `settlement.boca-larga` (Ilmeth Curva-Segura, `npc.ilmeth-curva-segura`). | F-07 |
| `faction.mercadores-de-fendas` | Mercadores de Fendas | liderança: npc.pip-vapor-curto \| cidades: `city.sinuosa`. **Assentamentos:** `settlement.fenda-mercado`. | F-08 |
| `faction.conselho-de-sangue` | Conselho de Sangue | liderança: npc.gorrak-pedra-grande \| cidades: `city.cumieira`. **Assentamentos:** `settlement.pasto-de-cume`. | F-09 |
| `faction.liga-dos-degraus` | Liga dos Degraus | liderança: npc.brunhild-degrau-fundo \| cidades: `city.cumieira`. **Assentamentos:** `settlement.degrau-pequeno`. | F-10 |
| `faction.casa-dos-despertos` | Casa dos Despertos | liderança: npc.quinze-pinos \| cidades: `city.rodaviva`, `city.barcaalta`. **Assentamentos:** `settlement.oficina-sem-dono`, `settlement.primeira-rampa` (Seis-Eixos, `npc.seis-eixos`). | F-11 |
| `faction.oficinas-reunidas` | Oficinas Reunidas | liderança: npc.fenwick-mola-solta \| cidades: `city.rodaviva`. **Assentamentos:** `settlement.roda-menor`. | F-12 |
| `faction.coro-das-facetas` | Coro das Facetas | liderança: npc.iridel-luz-partida \| cidades: `city.geodalia`. **Assentamentos:** `settlement.poco-alto`, `settlement.corte-fino`. | F-13 |
| `faction.arquivistas-das-coroas` | Arquivistas das Coroas | liderança: npc.maro-tinta-velha \| cidades: `city.pedra-ata`. **Assentamentos:** `settlement.arquivo-velho` (Haldra Tinta-Funda, `npc.haldra-tinta-funda`). | F-14 |

## 4. Assentamentos (20)

| ID | Nome | Cidade-mãe | Tipo | Liderança | Ref. |
|---|---|---|---|---|---|
| `settlement.cinzeiro` | Cinzeiro — Vila das Cinzas | `city.brasalume` | coleta de cinza, escória e vidro vulcânico | `npc.zeka-faisca-mansa` | S-01 |
| `settlement.veio-fundo` | Veio-Fundo — A Mina de Superfície | `city.brasalume` | mina aberta de minério | `npc.brundar-veio-alto` | S-02 |
| `settlement.ninho-seco` | Ninho-Seco — O Posto dos Mensageiros | `city.altavento` | posto sazonal de pouso e repasse de mensagens | `npc.iskra-pena-longa` | S-03 |
| `settlement.rastro-branco` | Rastro-Branco — A Comunidade da Neve | `city.altavento` | caçadores e guias de neve | `npc.miraq-salto-de-nevoa` | S-04 |
| `settlement.comporta-velha` | Comporta-Velha — O Distrito das Comportas | `city.veiaclara` | guarda e manutenção das comportas | `npc.pimm-veio-pequeno` | S-05 |
| `settlement.lago-de-cima` | Lago-de-Cima — A Vila do Gelo | `city.veiaclara` | coleta de gelo e cristal bruto do lago alto | `npc.aurel-faceta-calma` | S-06 |
| `settlement.cordoaria` | Cordoaria — O Bairro das Cordas | `city.barcaalta` | oficinas de corda, correntes e velas | `npc.torgrim-corda-dupla` | S-07 |
| `settlement.primeira-rampa` | Primeira-Rampa — O Núcleo do Registro | `city.barcaalta` | registro da Descida e guarda da rampa mais antiga | `npc.seis-eixos` | S-08 |
| `settlement.albergue-de-cima` | Albergue-de-Cima — A Hospedagem Sazonal | `city.portao-cinza` | hospedagem de caravanas em meses de passagem | `npc.skit-fenda-quente` | S-09 |
| `settlement.galeria-alta` | Galeria-Alta — O Núcleo dos Vigias | `city.portao-cinza` | alojamento e escala dos vigias | `npc.varkhan-escama-vigia` | S-10 |
| `settlement.fenda-mercado` | Fenda-Mercado — O Mercado de Fendas | `city.sinuosa` | mercado de rotas e mapas | `npc.pip-vapor-curto` | S-11 |
| `settlement.boca-larga` | Boca-Larga — O Acesso Externo | `city.sinuosa` | posto da entrada mais larga da cidade | `npc.ilmeth-curva-segura` | S-12 |
| `settlement.degrau-pequeno` | Degrau-Pequeno — O Bairro dos Menores | `city.cumieira` | bairro de moradia em escala menor, dentro da cidade grande | `npc.brunhild-degrau-fundo` | S-13 |
| `settlement.pasto-de-cume` | Pasto-de-Cume — O Pasto de Altitude | `city.cumieira` | rebanhos de altitude e lã grossa | `npc.gorrak-pedra-grande` | S-14 |
| `settlement.roda-menor` | Roda-Menor — O Distrito das Rodas | `city.rodaviva` | rodas d'água, engrenagens de carga e relojoaria | `npc.fenwick-mola-solta` | S-15 |
| `settlement.oficina-sem-dono` | Oficina-Sem-Dono — A Casa dos Despertos | `city.rodaviva` | núcleo onde moram construtos sem dono registrado | `npc.quinze-pinos` | S-16 |
| `settlement.poco-alto` | Poço-Alto — O Distrito do Poço de Luz | `city.geodalia` | guarda do poço vertical e das lentes | `npc.iridel-luz-partida` | S-17 |
| `settlement.corte-fino` | Corte-Fino — A Comunidade dos Lapidadores | `city.geodalia` | lapidação e oficinas de cristal | `npc.tuck-lente-torta` | S-18 |
| `settlement.patio-das-delegacoes` | Pátio-das-Delegações — O Bairro da Hospedagem | `city.pedra-ata` | hospedagem e serviço das delegações | `npc.sharra-selo-de-voto` | S-19 |
| `settlement.arquivo-velho` | Arquivo-Velho — O Núcleo dos Escribas | `city.pedra-ata` | escribas e cópia de atas | `npc.haldra-tinta-funda` | S-20 |

## 5. Pessoas (45)

| ID | Nome | Povo (`povo_id`) | Cidade | Vivo | Papel | Situação |
|---|---|---|---|---|---|---|
| `npc.aurea-faceta-longa` | Aurea Faceta-Longa | Cristalino (`cristalino`) | `city.geodalia` | sim | Benfeitora da Câmara das Lentes. | proposto, ficha completa (D-33) |
| `npc.aurel-faceta-calma` | Aurel Faceta-Calma | Cristalino (`cristalino`) | `city.veiaclara` | sim | Liderança de Lago-de-Cima | proposto, só liderança (sem ficha) |
| `npc.brakka-luz-no-portao` | Brakka Luz-no-Portão | Draconato (`draconato`) | `city.portao-cinza` | não | Herói lendário | proposto, herói lendário (D-33) |
| `npc.brundar-veio-alto` | Brundar Veio-Alto | Anão (`anao`) | `city.brasalume` | sim | Liderança de Veio-Fundo | proposto, só liderança (sem ficha) |
| `npc.brunhild-degrau-fundo` | Brunhild Degrau-Fundo | Anão (`anao`) | `city.cumieira` | sim | Liderança de Degrau-Pequeno | proposto, só liderança (sem ficha) |
| `npc.ciri-faceta-primeira` | Ciri Faceta-Primeira | Cristalino (`cristalino`) | `city.geodalia` | não | Herói lendário | proposto, herói lendário (D-33) |
| `npc.dois-fusos` | Dois-Fusos | Construto Desperto (`construto`) | `city.rodaviva` | não | Herói lendário | proposto, herói lendário (D-33) |
| `npc.dorn-primeira-descida` | Dorn Primeira-Descida | Anão (`anao`) | `city.barcaalta` | não | Herói lendário | proposto, herói lendário (D-33) |
| `npc.embra-cinza-viva` | Embra Cinza-Viva | Nascido da Brasa (`elemental-fogo`) | `city.brasalume` | não | Herói lendário | proposto, herói lendário (D-33) |
| `npc.fenwick-mola-solta` | Fenwick Mola-Solta | Gnomo (das rochas) (`gnomo`) | `city.rodaviva` | sim | Liderança de Roda-Menor | proposto, só liderança (sem ficha) |
| `npc.gorrak-pedra-grande` | Gorrak Pedra-Grande | Giganteano (`gigante`) | `city.cumieira` | sim | Liderança de Pasto-de-Cume | proposto, só liderança (sem ficha) |
| `npc.gundrik-muralha-lenta` | Gundrik Muralha-Lenta | Anão (`anao`) | `city.brasalume` | sim | General da guarda das fornalhas. | proposto, ficha completa (D-33) |
| `npc.haldra-tinta-funda` | Haldra Tinta-Funda | Anão (`anao`) | `city.pedra-ata` | sim | Liderança de Arquivo-Velho | proposto, só liderança (sem ficha) |
| `npc.hrodgar-primeiro-fogo` | Hrodgar Primeiro-Fogo | Anão (`anao`) | `city.brasalume` | não | Herói lendário | proposto, herói lendário (D-33) |
| `npc.ilmeth-curva-segura` | Ilmeth Curva-Segura | Labiríntico (`minotauro`) | `city.sinuosa` | sim | Liderança de Boca-Larga | proposto, só liderança (sem ficha) |
| `npc.iridel-luz-partida` | Iridel Luz-Partida | Cristalino (`cristalino`) | `city.geodalia` | sim | Liderança de Poço-Alto | proposto, só liderança (sem ficha) |
| `npc.iskra-pena-longa` | Iskra Pena-Longa | Altaneiro (`goliath`) | `city.altavento` | sim | Liderança de Ninho-Seco | proposto, só liderança (sem ficha) |
| `npc.karna-brasa-mansa` | Karna Brasa-Mansa | Nascido da Brasa (`elemental-fogo`) | `city.brasalume` | sim | Mestra da Fornalha-Mãe. | proposto, ficha completa (D-33) |
| `npc.maro-tinta-velha` | Maro Tinta-Velha | Draconato (`draconato`) | `city.pedra-ata` | sim | Arquivista-chefe das Coroas. | proposto, ficha completa (D-33) |
| `npc.maur-camada-velha` | Maur Camada-Velha | Nascido da Rocha (`elemental-terra`) | `city.cumieira` | sim | Herói do Pórtico Grande. | proposto, ficha completa (D-33) |
| `npc.miraq-salto-de-nevoa` | Miraq Salto-de-Névoa | Felino das Nuvens (`felino-das-nuvens`) | `city.altavento` | sim | Liderança de Rastro-Branco | proposto, só liderança (sem ficha) |
| `npc.nael-curva-antiga` | Nael Curva-Antiga | Labiríntico (`minotauro`) | `city.sinuosa` | sim | Mestre da Casa dos Guias. | proposto, ficha completa (D-33) |
| `npc.naru-sombra-de-neve` | Naru Sombra-de-Neve | Felino das Nuvens (`felino-das-nuvens`) | `city.altavento` | não | Herói lendário | proposto, herói lendário (D-33) |
| `npc.orla-fio-de-mapa` | Orla Fio-de-Mapa | Labiríntico (`minotauro`) | `city.sinuosa` | não | Herói lendário | proposto, herói lendário (D-33) |
| `npc.pelma-gota-medida` | Pelma Gota-Medida | Gnomo (das rochas) (`gnomo`) | `city.veiaclara` | sim | Guardiã das comportas. | proposto, ficha completa (D-33) |
| `npc.pimm-veio-pequeno` | Pimm Veio-Pequeno | Gnomo (das rochas) (`gnomo`) | `city.veiaclara` | sim | Liderança de Comporta-Velha | proposto, só liderança (sem ficha) |
| `npc.pip-vapor-curto` | Pip Vapor-Curto | Escamado Miúdo (`kobold`) | `city.sinuosa` | sim | Liderança de Fenda-Mercado | proposto, só liderança (sem ficha) |
| `npc.primeira-peca` | Primeira-Peça | Construto Desperto (`construto`) | `city.rodaviva` | sim | Memória viva da Casa dos Despertos. | proposto, ficha completa (D-33) |
| `npc.quinze-pinos` | Quinze-Pinos | Construto Desperto (`construto`) | `city.rodaviva` | sim | Liderança de Oficina-Sem-Dono | proposto, só liderança (sem ficha) |
| `npc.seis-eixos` | Seis-Eixos | Construto Desperto (`construto`) | `city.barcaalta` | sim | Liderança de Primeira-Rampa | proposto, só liderança (sem ficha) |
| `npc.sevro-selo-primeiro` | Sevro Selo-Primeiro | Anão (`anao`) | `city.pedra-ata` | não | Herói lendário | proposto, herói lendário (D-33) |
| `npc.sharra-selo-de-voto` | Sharra Selo-de-Voto | Draconato (`draconato`) | `city.pedra-ata` | sim | Liderança de Pátio-das-Delegações | proposto, só liderança (sem ficha) |
| `npc.skit-fenda-quente` | Skit Fenda-Quente | Escamado Miúdo (`kobold`) | `city.portao-cinza` | sim | Liderança de Albergue-de-Cima | proposto, só liderança (sem ficha) |
| `npc.thaurak-escama-fria` | Thaurak Escama-Fria | Draconato (`draconato`) | `city.portao-cinza` | sim | General do Limiar do Esporão. | proposto, ficha completa (D-33) |
| `npc.throm-juramento-alto` | Throm Juramento-Alto | Giganteano (`gigante`) | `city.cumieira` | não | Herói lendário | proposto, herói lendário (D-33) |
| `npc.tik-fenda-funda` | Tik Fenda-Funda | Escamado Miúdo (`kobold`) | `city.sinuosa` | não | Herói lendário | proposto, herói lendário (D-33) |
| `npc.tobiah-mola-mestra` | Tobiah Mola-Mestra | Gnomo (das rochas) (`gnomo`) | `city.rodaviva` | não | Herói lendário | proposto, herói lendário (D-33) |
| `npc.torgrim-corda-dupla` | Torgrim Corda-Dupla | Anão (`anao`) | `city.barcaalta` | sim | Liderança de Cordoaria | proposto, só liderança (sem ficha) |
| `npc.tuck-lente-torta` | Tuck Lente-Torta | Gnomo (das rochas) (`gnomo`) | `city.geodalia` | sim | Liderança de Corte-Fino | proposto, só liderança (sem ficha) |
| `npc.varkhan-escama-vigia` | Varkhan Escama-Vigia | Draconato (`draconato`) | `city.portao-cinza` | sim | Liderança de Galeria-Alta | proposto, só liderança (sem ficha) |
| `npc.velka-selo-firme` | Velka Selo-Firme | Anão (`anao`) | `city.pedra-ata` | sim | Presidente do Concílio das Coroas neste ciclo. | proposto, ficha completa (D-33) |
| `npc.vireo-vento-rapido` | Vireo Vento-Rápido | Altaneiro (`goliath`) | `city.altavento` | sim | Herói da Escadaria Cega. | proposto, ficha completa (D-33) |
| `npc.ysmira-primeiro-degelo` | Ysmira Primeiro-Degelo | Gnomo (das rochas) (`gnomo`) | `city.veiaclara` | não | Herói lendário | proposto, herói lendário (D-33) |
| `npc.zeka-faisca-mansa` | Zeka Faísca-Mansa | Nascido da Brasa (`elemental-fogo`) | `city.brasalume` | sim | Liderança de Cinzeiro | proposto, só liderança (sem ficha) |
| `npc.zhae-garra-de-nevoa` | Zhae Garra-de-Névoa | Felino das Nuvens (`felino-das-nuvens`) | `city.altavento` | sim | Guardiã dos passos altos. | proposto, ficha completa (D-33) |

## 6. Observações

- **Pessoas só liderança** (sem ficha completa): 20 NPCs citados como liderança de assentamento, facção ou governo; ficha completa em R2-4.1, se o autor quiser; nenhum vira principal sem aprovação (D-22 limita principais a 8 a 12; os 12 principais já têm ficha).
- **Nomes parecidos:** `npc.aurea-faceta-longa` (ficha completa, Geodália) e `npc.aurel-faceta-calma` (liderança, Veiaclara) são pessoas diferentes; o R2-2.4 confere se a semelhança de nome atrapalha.
- **Nenhuma entidade de Aeltharyn** está neste registro; os 135 IDs do reino 1 não mudam.
- **Relógios** (`clock.*`) e mistérios (`MP-nn`) ficam no doc 06 e no doc 01 parte 4; entram como dados quando o autor aprovar o formato (R2-2.2).

## 7. Decisão pendente

**D-38:** aprovar o registro como fonte única de IDs do reino 2, incluindo a coluna extra `povo_id` (opção A) ou removê-la e deixá-la só para o R2-2.3 (opção B). **Recomendação: A.**
