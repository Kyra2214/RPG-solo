# 12 — Eventos-tipo e cadeias causais (R2-5.2)

**Estado:** rascunho de R2-5.2 para aprovação do autor · **Data:** 2026-11-04
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, cidade, facção, relógio ou fato novo**. Dados em `12-eventos.csv` (10 colunas, no molde do reino 1).
**Fonte:** docs 01, 03, 04, 05, 06 e 07; `12-relogios.csv`, `12-recursos.csv`. Os 7 relógios (CL-01 a CL-07) e os conflitos C-01 a C-10 dão os gatilhos; as dependências DR-01 a DR-11 e as leis L-01 a L-10 dão os elos.

## Regras

- Cada evento é uma **possibilidade causal**, não um fato nem uma obrigação. Só acontece com o **gatilho observável** de `12-relogios.csv` (ou, nos eventos sem relógio, o gatilho da coluna). Pistas e escolhas vêm antes das consequências graves.
- **Encadeamento: ASSUMINDO.** Cada elo repete algo já documentado (quem paga, ao completar, tensão, lei), mas a ordem entre eles é leitura editorial.
- Nenhum evento envolve Aeltharyn, o Mistério D ou a mina selada, nem toca rota ou tempo de viagem (toda duração segue `desconhecida`, D-41). A origem do combustível e do alimento continua desconhecida.
- 13 eventos: 8 com relógio (CL-01 a CL-07; CL-02 e CL-06 têm dois cada) e 5 sem relógio (C-06, C-07, C-09 e a tensão S-14, que não viram relógio nesta partida, D-37).

## Eventos

### `event.fogo-reduzido` — Fogo principal de Brasalume em turnos reduzidos
- **Gatilho:** nova disputa pelo turno do fogo principal ou nova queda de fornecimento observada
- **Relógio:** `clock.combustivel`
- **Cadeia:** turno do fogo principal reduzido em Brasalume > famílias pequenas de forja perdem turnos > metal enviado a Barcaalta e Rodaviva diminui > carga de metal para o cais atrasa > o Conselho das Famílias de Fogo e a câmara de arbitragem precisam decidir (a origem do combustível continua desconhecida).
- **Cidades e assentamentos:** `city.brasalume`, `city.barcaalta`, `city.rodaviva`, `settlement.cinzeiro`, `settlement.veio-fundo`
- **Recursos:** combustivel, metal, carga-e-cais
- **Pessoas e facções:** `npc.gundrik-muralha-lenta`, `npc.karna-brasa-mansa`, `npc.brundar-veio-alto`, `faction.casa-das-fornalhas`, `faction.irmandade-do-veio`, `faction.companhia-da-descida`
- **Rotas:** `route.barcaalta-brasalume`
- **Fonte:** doc 06 CL-01, C-01; doc 05 §1 (L-03), §2.2 (DR-01, DR-05, DR-06); encadeamento: ASSUMINDO

### `event.comporta-cede` — Comporta velha cede em Veiaclara
- **Gatilho:** prova nova de registro alterado ou degelo fora de data observado, com comporta velha em uso
- **Relógio:** `clock.comportas`
- **Cadeia:** comporta velha cede > água sobe ou falta em cidades a jusante (só dentro das Coroas) > as cidades a jusante pedem socorro sem ter pago a manutenção > a Casa das Comportas cobra verba > o Conselho da Nascente precisa decidir quem paga.
- **Cidades e assentamentos:** `city.veiaclara`, `settlement.comporta-velha`, `settlement.lago-de-cima`
- **Recursos:** agua
- **Pessoas e facções:** `npc.pelma-gota-medida`, `npc.pimm-veio-pequeno`, `faction.casa-das-comportas`
- **Rotas:** `route.veiaclara-jusante`
- **Fonte:** doc 06 CL-02, C-03; doc 05 §2.2 (DR-02); doc 04 §7; encadeamento: ASSUMINDO

### `event.registro-provado` — Registro de comporta alterado é provado
- **Gatilho:** prova nova de que o registro de uma abertura foi alterado (MP-01)
- **Relógio:** `clock.comportas`
- **Cadeia:** prova de registro alterado > falta grave pela Lei da Comporta (L-02) > o Conselho da Nascente julga > as cidades a jusante deixam de confiar nos números da Casa das Comportas > pedem auditoria antes de pagar.
- **Cidades e assentamentos:** `city.veiaclara`, `settlement.comporta-velha`
- **Recursos:** agua
- **Pessoas e facções:** `npc.pelma-gota-medida`, `npc.pimm-veio-pequeno`, `faction.casa-das-comportas`
- **Rotas:** `route.veiaclara-jusante`
- **Fonte:** doc 06 CL-02; doc 05 §1 (L-02); doc 01 N-07, MP-01; encadeamento: ASSUMINDO

### `event.cais-fechado` — Cais de Barcaalta fecha por conferência
- **Gatilho:** barca sem registro de carga ou novo conflito entre carga e taxas
- **Relógio:** `clock.cais`
- **Cadeia:** barca sem registro > a Lei do Cais (L-09) manda conferir carga e taxa > o cais fecha até a Mesa do Cais resolver > mercadorias ficam retidas > famílias de cais perdem renda > a Companhia da Descida pressiona a Mesa (a Descida não é descrita rio abaixo).
- **Cidades e assentamentos:** `city.barcaalta`, `settlement.cordoaria`, `settlement.primeira-rampa`
- **Recursos:** carga-e-cais, metal, pecas
- **Pessoas e facções:** `npc.torgrim-corda-dupla`, `npc.seis-eixos`, `faction.companhia-da-descida`, `faction.casa-dos-despertos`
- **Rotas:** `route.barcaalta-argente`, `route.barcaalta-brasalume`, `route.barcaalta-rodaviva`
- **Fonte:** doc 06 CL-03, C-04; doc 05 §1 (L-09), §3; encadeamento: ASSUMINDO

### `event.registro-duplo` — Portão-Cinza exige registro duplo
- **Gatilho:** nova página arrancada do Livro de Passagens ou novo caso de delito de custódia (MP-02)
- **Relógio:** `clock.passagens`
- **Cadeia:** página arrancada > a Mesa do Portão decide registro duplo para quem sobe e quem desce > o albergue perde hóspedes que fogem do registro > caravanas pagam tarifas ainda em disputa > Pedra-Ata recebe delegações atrasadas.
- **Cidades e assentamentos:** `city.portao-cinza`, `city.pedra-ata`, `settlement.albergue-de-cima`, `settlement.galeria-alta`, `settlement.patio-das-delegacoes`
- **Recursos:** passagem-e-hospedagem
- **Pessoas e facções:** `npc.thaurak-escama-fria`, `npc.varkhan-escama-vigia`, `npc.skit-fenda-quente`, `faction.vigias-do-limiar`, `faction.rede-dos-passos-altos`
- **Rotas:** `route.barcaalta-portao-cinza`, `route.concilio-pedra-ata`
- **Fonte:** doc 06 CL-04, C-05; doc 05 §1 (L-04), §2.3; doc 03 S-09; encadeamento: ASSUMINDO

### `event.assento-vazio` — Concílio sem presidente de guarda
- **Gatilho:** o fim do ciclo de Velka é cumprido na cena sem acordo de sucessão anunciado
- **Relógio:** `clock.assento`
- **Cadeia:** ciclo termina sem sucessor combinado > o Concílio fica sem presidente de guarda por um ciclo > matérias reservadas param > cada cidade pede a cópia da ata que lhe convém > o Arquivo das Coroas recebe pedidos de cópias diferentes (MP-04).
- **Cidades e assentamentos:** `city.pedra-ata`, `settlement.arquivo-velho`, `settlement.patio-das-delegacoes`
- **Recursos:** arquivo-e-selos
- **Pessoas e facções:** `npc.velka-selo-firme`, `npc.maro-tinta-velha`, `npc.haldra-tinta-funda`, `npc.sharra-selo-de-voto`, `faction.arquivistas-das-coroas`
- **Rotas:** `route.concilio-pedra-ata`
- **Fonte:** doc 06 CL-05, C-10; doc 04 §2; doc 05 §2.2 (DR-07); encadeamento: ASSUMINDO

### `event.peca-sumida` — Peça rara some e as oficinas se acusam
- **Gatilho:** nova peça rara sumida
- **Relógio:** `clock.pecas`
- **Cadeia:** peça rara some > cada oficina culpa outra > a vila de Roda-Menor é acusada > oficinas menores param por falta de peça > a Assembleia das Oficinas vota um foro provisório ou uma oficina fecha.
- **Cidades e assentamentos:** `city.rodaviva`, `city.barcaalta`, `settlement.roda-menor`, `settlement.oficina-sem-dono`
- **Recursos:** pecas
- **Pessoas e facções:** `npc.fenwick-mola-solta`, `npc.primeira-peca`, `npc.quinze-pinos`, `faction.oficinas-reunidas`, `faction.casa-dos-despertos`
- **Rotas:** `route.barcaalta-rodaviva`
- **Fonte:** doc 06 CL-06, C-08; doc 03 S-15; doc 05 §2.2 (DR-05, DR-06); encadeamento: ASSUMINDO

### `event.pedido-desperto` — Construtos pedem reconhecimento formal
- **Gatilho:** novo pedido formal de reconhecimento feito por construtos
- **Relógio:** `clock.pecas`
- **Cadeia:** pedido formal dos Despertos > a Assembleia das Oficinas discute sem que os Despertos sem dono votem > oficinas reclamam os construtos como propriedade > a cidade precisa definir um foro > o registro de carga de Barcaalta, onde constam construtos de carga, entra na disputa (a origem dos Despertos fica desconhecida).
- **Cidades e assentamentos:** `city.rodaviva`, `city.barcaalta`, `settlement.oficina-sem-dono`, `settlement.primeira-rampa`
- **Recursos:** pecas, carga-e-cais
- **Pessoas e facções:** `npc.quinze-pinos`, `npc.seis-eixos`, `npc.primeira-peca`, `npc.fenwick-mola-solta`, `faction.casa-dos-despertos`, `faction.liga-dos-degraus`, `faction.oficinas-reunidas`
- **Rotas:** `route.barcaalta-rodaviva`
- **Fonte:** doc 06 CL-06, C-08; doc 04 §9, §10, §11; doc 03 S-16; encadeamento: ASSUMINDO

### `event.nevasca-fecha-passos` — Nevasca fecha os passos
- **Gatilho:** nova nevasca observada
- **Relógio:** `clock.passos`
- **Cadeia:** nevasca fecha os passos > mensageiros recusam voar > avisos entre cidades atrasam > Altavento fica sem reserva para os que pousam > o reino fica dividido em blocos por um ciclo (a estação sozinha não é gatilho).
- **Cidades e assentamentos:** `city.altavento`, `city.portao-cinza`, `settlement.ninho-seco`, `settlement.rastro-branco`
- **Recursos:** mensagens-e-passos, alimento
- **Pessoas e facções:** `npc.iskra-pena-longa`, `npc.vireo-vento-rapido`, `npc.zhae-garra-de-nevoa`, `npc.miraq-salto-de-nevoa`, `faction.rede-dos-passos-altos`, `faction.vigias-do-limiar`
- **Rotas:** `route.altavento-passos`, `route.altavento-pedra-ata`
- **Fonte:** doc 06 CL-07, C-02; doc 05 §2.2 (DR-04, DR-09); doc 03 S-03; encadeamento: ASSUMINDO

### `event.mapa-rival` — Mapas rivais e um viajante perdido
- **Gatilho:** galerias novas que aparecem e mapas rivais em circulação (MP-06); sem relógio
- **Relógio:** —
- **Cadeia:** galeria nova aparece > mapas rivais circulam > cada família de guias acusa o mercado de vender o mapa errado > um viajante entra sem guia e se perde > a Rotunda dos Mapas arbitra pela Lei do Mapa (L-05).
- **Cidades e assentamentos:** `city.sinuosa`, `settlement.fenda-mercado`, `settlement.boca-larga`
- **Recursos:** mapas-e-guias
- **Pessoas e facções:** `npc.nael-curva-antiga`, `npc.ilmeth-curva-segura`, `npc.pip-vapor-curto`, `faction.casa-dos-guias`, `faction.mercadores-de-fendas`
- **Rotas:** —
- **Fonte:** doc 06 C-06; doc 05 §1 (L-05); doc 03 S-11, S-12; doc 01 MP-06; encadeamento: ASSUMINDO

### `event.degrau-na-escala` — Pedido de passagens na escala dos menores
- **Gatilho:** pedido formal de acesso na escala dos menores em obra da cidade (L-07); sem relógio
- **Relógio:** —
- **Cadeia:** menores pedem acesso mínimo > os grandes veem ofensa à casa antiga > o Salão dos Juramentos de Sangue e a Liga dos Degraus se opõem > obra e honra entram no custo de qualquer saída.
- **Cidades e assentamentos:** `city.cumieira`, `settlement.degrau-pequeno`, `settlement.pasto-de-cume`
- **Recursos:** pedra-e-rebanhos
- **Pessoas e facções:** `npc.brunhild-degrau-fundo`, `npc.gorrak-pedra-grande`, `npc.maur-camada-velha`, `faction.liga-dos-degraus`, `faction.conselho-de-sangue`
- **Rotas:** —
- **Fonte:** doc 06 C-07; doc 05 §1 (L-07); doc 03 S-13; encadeamento: ASSUMINDO

### `event.caminho-nos-pastos` — Caminho aberto pelos pastos de Cumieira
- **Gatilho:** a cidade decide abrir um caminho pelos pastos do clã
- **Relógio:** —
- **Cadeia:** a cidade abre caminho pelos pastos > o clã vê quebra de juramento > o Salão dos Juramentos de Sangue contesta > lã e carne chegam atrasadas à cidade > a Liga dos Degraus é pedida como mediadora (ASSUMINDO).
- **Cidades e assentamentos:** `city.cumieira`, `settlement.pasto-de-cume`, `settlement.degrau-pequeno`
- **Recursos:** pedra-e-rebanhos, alimento
- **Pessoas e facções:** `npc.gorrak-pedra-grande`, `npc.brunhild-degrau-fundo`, `npc.maur-camada-velha`, `faction.conselho-de-sangue`, `faction.liga-dos-degraus`
- **Rotas:** —
- **Fonte:** doc 03 S-14 (tensão); doc 01 C-07; encadeamento: ASSUMINDO

### `event.faceta-rachada` — Cota apertada e faceta rachada em Geodália
- **Gatilho:** tremor racha uma faceta ou lente e as cotas de extração apertam (C-09); sem relógio
- **Relógio:** —
- **Cadeia:** faceta ou lente rachada > cotas de extração apertam pela Lei da Extração Medida (L-06) > lapidadores de Corte-Fino perdem encomendas > a manutenção das lentes encarece > a luz e os sinais para as outras cidades ficam em risco (DR-03).
- **Cidades e assentamentos:** `city.geodalia`, `settlement.poco-alto`, `settlement.corte-fino`
- **Recursos:** cristal-e-luz
- **Pessoas e facções:** `npc.aurea-faceta-longa`, `npc.iridel-luz-partida`, `npc.tuck-lente-torta`, `faction.coro-das-facetas`
- **Rotas:** —
- **Fonte:** doc 06 C-09; doc 05 §1 (L-06), §2.2 (DR-03); doc 03 S-17, S-18; doc 01 N-08; encadeamento: ASSUMINDO

## Conferências

| Verificação | Resultado |
|---|---|
| Eventos | 13 (mínimo 10, máximo 15) |
| ID de cidade, assentamento, pessoa ou facção fora do registro mestre | 0 |
| Recurso fora de `12-recursos.csv` | 0 |
| Relógio fora de `12-relogios.csv` | 0 |
| Rota fora do doc 07 | 0 |
| Campo vazio | 0 |
| Evento que cita Aeltharyn, Mistério D ou tempo de viagem | 0 |
