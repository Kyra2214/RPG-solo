# 13 — Reação do reino ao jogador (R2-5.4)

**Estado:** rascunho de R2-5.4 para aprovação do autor (D-52) · **Data:** 2026-11-04

**Etiqueta:** tudo é **[PROPOSTO]**. **Fonte:** doc 01 (parte 1), doc 03, doc 04 §3, §5, §7, §10, doc 05 §1 e §2.2, doc 06 §1; `12-registro-mestre.csv`, `12-relacoes.csv`; `plano/INTEGRACAO_FUTURA_RPG_SOLO.md` (MP-06, MP-07). Dados em `13-reputacao.csv` e `13-canais.csv`. Molde: `reinos/01-.../13-reacao-ao-jogador.md`.

## 1. Regras e limites

- Este documento descreve **a camada de mundo**: o que cada cidade e facção valoriza ou pune, quem reage e por que canal a notícia chega. **Números de fama, alcance, preço e sorteio de rumor pertencem ao motor** (MP-07); aqui só há linguagem qualitativa.
- **Reputação não substitui prova** (doc 04 §5): rumor justifica investigação, não condenação. O mesmo ato pode dar fama diferente em cada cidade e facção.
- A notícia viaja só pelos canais da seção 4. **Toda duração de rota é `desconhecida` (doc 07):** a notícia **não tem prazo definido**; nenhum tempo foi inventado.
- A IA narra a distorção do rumor, nunca a transforma em fato. Nenhuma reação exige a presença do jogador e nenhuma é punição automática (doc 06 §1).
- Nenhuma instituição das Coroas tem jurisdição fora do reino (doc 04 §3); **nada aqui toca Aeltharyn** nem diz como a fama chegaria lá.
- Mapeamento de ato para reação é leitura editorial (ASSUMINDO), derivada só de objetivos, tensões e leis já escritos; nenhuma facção, ofício ou NPC novo.

## 2. Fama e reputação por cidade e facção (R2-5.4a)

Disposição é **por entidade**, não global. Ver `13-reputacao.csv` (prefixo `rep.`).

| Entidade | Ganha disposição | Perde disposição | Quem reage | Canais |
|---|---|---|---|---|
| `city.brasalume` | respeitar o turno do fogo e os juramentos de fogo; poupar combustível quando falta; registrar o que entra | furar o turno; esconder estoque; favorecer uma família contra o registro | `npc.gundrik-muralha-lenta`, `npc.karna-brasa-mansa`, `faction.casa-das-fornalhas`, `faction.irmandade-do-veio` | Casa dos Juramentos de Fogo; Galeria dos Cadinhos; Conselho das Famílias de Fogo |
| `city.altavento` | honrar avisos de avalanche e de nevasca; levar cartas e aviso por passo difícil; ajudar quem pousa sem reserva | forçar passo fechado; desprezar os mensageiros; pagar repasse sempre atrasado | `npc.vireo-vento-rapido`, `npc.iskra-pena-longa`, `npc.zhae-garra-de-nevoa` | Plataforma dos Mensageiros; Salão dos Ventos; Voo dos Mensageiros |
| `city.veiaclara` | registro honesto das comportas; pagar ou ajudar na manutenção; respeitar o aviso antes de cortar água | alterar o registro (falta grave); usar a água como arma; tratar a nascente só como mercadoria | `npc.pelma-gota-medida`, `npc.pimm-veio-pequeno`, `faction.casa-das-comportas` | Casa das Comportas; Galeria dos Medidores; Conselho da Nascente |
| `city.barcaalta` | carga com registro e taxa pagos; conferir os dois registros; tratar construtos de carga como parte do registro quando a Mesa o decidir | barca sem registro de carga; fraude de corda ou de taxa; esconder diferença entre carga e taxa | `npc.torgrim-corda-dupla`, `npc.seis-eixos`, `faction.companhia-da-descida` | Registro da Descida; Cais Alto; Mesa do Cais |
| `city.portao-cinza` | registro completo de quem sobe e desce; hospedar sem perguntar a origem; ajudar os vigias nas escalas | arrancar ou omitir página do Livro (delito de custódia); passar sem registro; usar a hospedagem para encobrir delito | `npc.thaurak-escama-fria`, `npc.varkhan-escama-vigia`, `npc.skit-fenda-quente`, `faction.vigias-do-limiar` | Livro de Passagens; Galeria dos Vigias; Albergue das Duas Direções; Mesa do Portão |
| `city.sinuosa` | seguir guia; mapa honesto; ajudar em buscas de viajante perdido | vender mapa que se sabe errado (fraude); entrar sem guia e culpar o guia; esconder corredor novo | `npc.nael-curva-antiga`, `npc.ilmeth-curva-segura`, `npc.pip-vapor-curto`, `faction.casa-dos-guias`, `faction.mercadores-de-fendas` | Rotunda dos Mapas; Casa dos Guias; Mercado de Fendas |
| `city.cumieira` | honrar juramento de clã; hospitalidade ritual; consultar o clã antes de obra em pasto; acesso mínimo na escala dos menores | quebrar juramento; abrir caminho pelos pastos sem consulta; tratar o pedido dos menores só como ofensa ou só como capricho | `npc.gorrak-pedra-grande`, `npc.brunhild-degrau-fundo`, `npc.maur-camada-velha`, `faction.conselho-de-sangue`, `faction.liga-dos-degraus` | Salão dos Juramentos de Sangue; Escada dos Pequenos; Mercado de Cume |
| `city.rodaviva` | peças registradas na Casa das Peças; ouvir os Despertos sem presumir a origem deles; consertar sem reivindicar posse | acusar oficina sem prova; ocultar peça rara; tratar Despertos só como propriedade ou só como pessoas antes de a cidade decidir | `npc.fenwick-mola-solta`, `npc.quinze-pinos`, `npc.primeira-peca`, `faction.oficinas-reunidas`, `faction.casa-dos-despertos` | Casa das Peças; Galeria dos Despertos; Assembleia das Oficinas |
| `city.geodalia` | respeitar a cota por faceta; cuidar das lentes e do poço de luz; ouvir os lapidadores | extrair acima da cota; sombrear o poço; ignorar o eco da caverna | `npc.iridel-luz-partida`, `npc.aurea-faceta-longa`, `npc.tuck-lente-torta`, `faction.coro-das-facetas` | Câmara das Lentes; Galeria dos Ecos; Poço Alto; sinais de luz |
| `city.pedra-ata` | preservar fonte e cadeia de custódia; hospedar delegações sem favorecer; respeitar a Pedra dos Pactos | forjar, subtrair ou esconder cópia; favorecer uma cidade no assento; ofender delegação | `npc.velka-selo-firme`, `npc.maro-tinta-velha`, `npc.sharra-selo-de-voto`, `faction.arquivistas-das-coroas` | Arquivo das Coroas; Pátio das Delegações; Salão do Concílio |
| `faction.casa-das-fornalhas` | respeitar turno e juramento de fogo; poupar combustível | furar o turno; esconder estoque; favorecer uma família | `npc.karna-brasa-mansa` | Casa dos Juramentos de Fogo; Galeria dos Cadinhos |
| `faction.irmandade-do-veio` | cota que pague o risco de desabamento; galeria nova bem avaliada | ignorar risco de desabamento; impor cota sem ouvir os mineiros | `npc.brundar-veio-alto` | mineiros de Veio-Fundo |
| `faction.rede-dos-passos-altos` | levar cartas e viajantes; honrar o fechamento preventivo; ajudar mensageiros em nevasca | forçar passo fechado; pagar repasse atrasado; desprezar o aviso de avalanche | `npc.zhae-garra-de-nevoa`, `npc.iskra-pena-longa`, `npc.miraq-salto-de-nevoa` | Voo dos Mensageiros; Guardiões dos Passos |
| `faction.casa-das-comportas` | registro público honesto; ajudar a pagar a manutenção | alterar o registro; usar comporta como arma; calar sobre comporta velha | `npc.pimm-veio-pequeno` | registro de aberturas; Galeria dos Medidores |
| `faction.companhia-da-descida` | carga registrada; contrato claro; preço estável de corda; hospedagem justa | barca sem registro; fugir da taxa; quebrar a preferência de cais | `npc.torgrim-corda-dupla`, `npc.skit-fenda-quente` | Cais Alto; Hospedeiros do Limiar |
| `faction.vigias-do-limiar` | registro completo; apoio à vigília e às escalas; aviso a Altavento | arrancar página; passar sem registro; recusar inspeção sem motivo | `npc.thaurak-escama-fria`, `npc.varkhan-escama-vigia` | Livro de Passagens; Galeria dos Vigias |
| `faction.casa-dos-guias` | seguir guia; mapa honesto; ajuda em resgate | vender mapa que se sabe errado; abandonar viajante; esconder corredor | `npc.nael-curva-antiga`, `npc.ilmeth-curva-segura` | Rotunda dos Mapas; Boca-Larga |
| `faction.mercadores-de-fendas` | mercado aberto, neutro e pago; pagar pela cópia de mapa | copiar mapa sem pagar; quebrar a neutralidade do mercado | `npc.pip-vapor-curto` | barracas do Mercado de Fendas; copiador pago |
| `faction.conselho-de-sangue` | honrar o primeiro juramento; hospitalidade ritual; consultar o clã antes de obra em pasto | quebrar juramento; abrir caminho sem consulta; ofender a casa antiga | `npc.gorrak-pedra-grande` | Salão dos Juramentos de Sangue; conselho de sangue |
| `faction.liga-dos-degraus` | pedido claro de degraus e portas na escala dos menores; mediação com os grandes | ignorar a escala dos menores; obra que fira sem consulta | `npc.brunhild-degrau-fundo` | Degrau-Pequeno; Escada dos Pequenos |
| `faction.casa-dos-despertos` | tratar os Despertos como interlocutores; respeitar a recusa de tarefa; ouvir os mais antigos | reclamar como propriedade; forçar tarefa; decidir a origem deles | `npc.quinze-pinos`, `npc.seis-eixos` | Oficina-Sem-Dono; memória de Primeira-Peça (sem comando) |
| `faction.oficinas-reunidas` | peças registradas na Casa das Peças; honestidade entre oficinas | acusar sem prova; ocultar peça; barrar foro provisório por interesse | `npc.fenwick-mola-solta` | Casa das Peças; Assembleia das Oficinas |
| `faction.coro-das-facetas` | respeitar a cota por faceta; cuidar de lentes e poço; voto sonoro honesto | extrair acima da cota; sombrear o poço | `npc.iridel-luz-partida`, `npc.tuck-lente-torta` | Câmara das Lentes; Lapidadores de Corte-Fino |
| `faction.arquivistas-das-coroas` | preservar a fonte e a cadeia de custódia; incluir todas as cópias divergentes | forjar, subtrair ou esconder cópia; tratar cópia como prova do conteúdo | `npc.maro-tinta-velha`, `npc.haldra-tinta-funda` | Arquivo das Coroas; Arquivo-Velho |

## 3. Procurado, favor e reparação (R2-5.4b)

### 3.1 Estados de "procurado" (ASSUMINDO, ordinais)

| Estado | Condição | Limite escrito |
|---|---|---|
| Rumor | alguém diz que o jogador fez algo | justifica investigação, não condenação (doc 04 §5) |
| Investigação | acusação que indica o ato, a regra e a prova | testemunho, registro e inferência são provas diferentes; confissão sob ameaça não confirma (doc 04 §5) |
| Procurado numa jurisdição | a autoridade local do incidente emite pedido | o Concílio não assume o caso local; vale fora só por arbitragem ou pacto (doc 04 §3, §5) |
| Disputa de jurisdição | duas autoridades reivindicam o caso | vai à câmara de arbitragem, com três delegados de cidades não envolvidas (doc 04 §5); capturar não decide quem julga |
| Resolvido | decisão com reparação, compensação, restrição ou perda de cargo | a decisão distingue as quatro (doc 04 §5); não se presume pena máxima |

O acusado pode dar a sua versão e contestar a prova; quem tem interesse declara o conflito; cada povo tem meios razoáveis de se manifestar (voto sonoro, eco, juramento, registro). São princípios disputados e aplicados de modo desigual (doc 04 §5).

### 3.2 Ato, regra e autoridade

| Ato | Regra | Autoridade inicial | Desfecho possível |
|---|---|---|---|
| cortar água, passagem, luz ou fogo de que outra cidade depende, sem aviso | Lei do Aviso (L-01) | governo local; câmara de arbitragem | restrição de uso do bem e compensação à cidade afetada |
| alterar registro de comporta | Lei da Comporta (L-02) | Conselho da Nascente | perda de cargo ou restrição |
| furar o turno do fogo principal | Lei do Fogo Partilhado (L-03) | Conselho das Famílias de Fogo | decisão do Conselho; o turno da cidade vem antes em falta de combustível |
| arrancar ou omitir página do Livro ou do Registro | Lei de Passagem (L-04), delito de custódia | Mesa do Portão ou Mesa do Cais | restrição ou perda de cargo |
| vender mapa que se sabe errado | Lei do Mapa (L-05) | Casa dos Guias; Rotunda dos Mapas | reparação e restrição ao ofício |
| extrair cristal acima da cota | Lei da Extração Medida (L-06) | Câmara das Facetas | reparação e restrição |
| recusar acesso na escala dos menores | Lei da Escala (L-07) | governo local; Salão dos Juramentos de Sangue (juramento não substitui a lei da cidade) | disputa em aberto em Cumieira; acesso mínimo é a saída escrita |
| subtrair ou forjar cópia de ata | Lei da Cópia (L-08) | Arquivo das Coroas; câmara de arbitragem | tribunal ou arbitragem; a posse não prova o conteúdo |
| barca sem registro de carga | Lei do Cais (L-09) | Mesa do Cais | a barca não desce até a conferência |
| entregar hóspede sem pedido formal ou usar a hospedagem para encobrir delito | Costume da Hospedagem (L-10) | Mesa de Pedra-Ata; Mesa do Portão | não é asilo; cada cidade lê o limite a seu modo |
| direitos de Construtos Despertos | **sem foro definido** (doc 04 §5; lacuna intencional) | nenhuma por ora | o texto não inventa foro |

### 3.3 Favor

- **Pessoal:** NPC com vínculo (`12-relacoes.csv`) pode pedir ou conceder; só sabe o que consta em `12-conhecimento.csv`. Afeto: 0 relação documentada (D-47); nenhum favor de afeto é inventado.
- **Institucional:** facção cobra ou concede conforme `13-reputacao.csv`; não é troca automática.
- **Dívida de ofício:** a contribuição de ofício (doc 04 §6) é a moeda de favor entre cidades; não há preço nem número aqui.
- **Hospitalidade e juramento:** o Costume da Hospedagem (L-10) obriga a receber sem perguntar a quem serve; juramento de clã e juramento de fogo valem entre os seus (ASSUMINDO leitura: não obrigam quem não jurou).

### 3.4 Reparação

Formas escritas no doc 04 §5: **reparação** (refazer o dano), **compensação** (pagar a quem perdeu), **restrição** (de uso, de acesso ou do ofício) e **perda de cargo** (só se o jogador tiver cargo). Punição como pena própria não está escrita nas Coroas: fica como lacuna, sem inventar. Reparar não apaga o rumor: a reputação muda por canal e por cidade, no ritmo do motor.

## 4. Intermediários (R2-5.4c)

Quem leva a notícia, o pedido ou a decisão. Ver `13-canais.csv` (prefixo `canal.`).

| Canal | Função | O que o cânone diz | Limite |
|---|---|---|---|
| `canal.coroas-cartaz` — Cartaz | anúncio público de um ato, pedido ou procura | não escrito no cânone das Coroas; ASSUMINDO uso como canal, sem criar facção nem ofício | vale só como rumor até haver acusação com ato, regra e prova (doc 04 §5) |
| `canal.coroas-cacador` — Caçador de pessoas | rastrear alguém procurado por uma autoridade | não há caçador de recompensa escrito nas Coroas; lacuna explícita, nenhuma facção criada; só há vigias que inspecionam e guias que conhecem as rotas | capturar não define quem julga (doc 04 §5); nenhum caçador sem decisão do autor |
| `canal.coroas-emissario` — Emissário do Concílio | levar convocação, arbitragem ou pedido de uma cidade a outra | o Concílio arbitra disputas entre cidades e interpreta o pacto; a câmara de arbitragem tem delegados de cidades não envolvidas; a decisão pode exigir consentimento e execução local | o Concílio não assume o caso local (doc 04 §3, §5); nenhuma jurisdição fora do reino |
| `canal.coroas-mensageiro` — Mensageiro de altitude | levar notícia e cartas entre cidades | o Voo dos Mensageiros de Ninho-Seco repassa cartas; os mensageiros recusam voar quando o vento muda; as cidades pagam o repasse atrasado | sem prazo definido: toda duração de rota é desconhecida (doc 07); nevasca ou vento atrasa |
| `canal.coroas-mercado` — Mercado de Fendas | propagar notícia e vender mapa e informação de rota | o Mercado de Fendas é aberto, neutro e pago; os mercadores copiam e vendem mapa | pode vender versão enviesada (ASSUMINDO); a Lei do Mapa pune mapa que se sabe errado |
| `canal.coroas-registro` — Livro de Passagens e Registro da Descida | registrar quem sobe e desce e a carga que passa | o Livro registra quem sobe e desce pelo Portão-Cinza e o Registro, quem passa pelo cais; os dois têm lacunas ou não batem com as taxas | prova o que foi anotado, não o que foi omitido; não cria rota nem tempo de viagem (doc 04 §3, §5) |
| `canal.coroas-arquivo` — Arquivo das Coroas e cadeia de custódia | registrar fatos, atas e cópias | o Arquivo preserva a fonte, registra a cadeia de custódia e inclui todas as cópias divergentes; ata fechada só abre por matéria reservada | ter a cópia não torna o conteúdo verdadeiro (L-08); pode restringir acesso |
| `canal.coroas-hospedagem` — Pátio das Delegações e albergues | acolher delegação, viajante e perseguido e registrar hóspedes | o Costume da Hospedagem manda receber sem perguntar a quem serve; não é asilo; o albergue resiste a registrar todos os hóspedes | a cidade não entrega nem protege contra pedido formal; cada cidade lê o limite a seu modo (L-10) |
| `canal.coroas-guias` — Casa dos Guias | levar notícia e recusa de guia dentro das galerias | o guia responde pelo que guiou; guia obrigatório na entrada de Sinuosa; a Casa pode recusar guia | a notícia circula só dentro e perto da cidade; nenhum mapa é fonte final (L-05) |
| `canal.coroas-sinais-de-luz` — Sinais de luz de Geodália | avisar outras cidades por luz e eco | Geodália emite sinais de luz e defende-se por sinais e eco; a luz depende de lentes e poço | pode atrasar ou falhar com lente rachada ou obra no poço; sem prazo definido |
| `canal.coroas-salao-de-juramentos` — Salão dos Juramentos de Sangue | julgar juramento de clã e levar a decisão aos clãs | o Salão julga juramento de clã em Cumieira; as decisões chegam ao clã em seu tempo | juramento de clã não substitui a lei da cidade (doc 04 §5) |

**Lacunas explícitas:** cartaz e caçador de pessoas não estão escritos nas Coroas; entram só como canais, sem facção, ofício ou NPC novos. Nenhum prazo de notícia: toda rota é `desconhecida`.

## 5. Ligação com o jogo

- **MP-07:** `13-reputacao.csv` dá a disposição qualitativa por cidade e facção; `13-canais.csv` dá os canais da notícia.
- **MP-06 (caçadores):** o mundo só autoriza caçadores onde o texto os escreve; nas Coroas não escreve (ver `canal.coroas-cacador`); qualquer um depende de decisão do autor.
- **Sementes (R2-5.3):** as reações valem para as 90 sementes; nenhuma exige NPC inexistente.

## 6. Conferências

- 24 linhas de reputação (10 cidades e 14 facções) e 11 canais; 0 campo vazio, exceto `entidades` de `canal.coroas-cartaz` (lacuna explícita); todo ID de cidade, facção e NPC existe no registro mestre; 0 NPC com `vivo = não` como reator.
- 0 número de fama, preço ou prazo; 0 NPC, facção ou ofício novo; 0 termo de fronteira com Aeltharyn.

## 7. Decisão pendente

**D-52** (ver `plano/DECISOES.md`): aprovar a reputação por entidade, os estados de procurado, as formas de reparação e os 11 canais. Opções: **A.** aprovar tudo (recomendada); **B.** trocar linha de reputação, canal ou a lacuna de punição (diga qual).
