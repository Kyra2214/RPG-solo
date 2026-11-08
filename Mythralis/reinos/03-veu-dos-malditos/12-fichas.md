# 12 — Fichas completas dos doze principais (R3-4.1)

**Estado:** rascunho de R3-4.1 para aprovação do autor (D-83) · **Data:** 2026-11-06
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, nome, lugar, facção ou fato novo**: reúne e completa as fichas N-01 a N-12 (doc 01 parte 2, D-70) no campo a campo de `MODELO_DE_REINO.md` §4, com raça, sub-raça, classe e profissão de `ALINHAMENTO_VEU.md` (D-77), facções e aliados de `12-facoes.csv` e doc 04, relógios de `06`. Dados em `12-fichas.csv` (mesmos 17 colunas do reino 2).
**Estados:** C confirmado · S suspeita · D desconhecido · ASSUMINDO = leitura editorial sem fonte direta (traços, medos e condições de evolução). Nada se infere além dos docs 01, 04 e 06.
**Campos feitos depois da ficha:** competências detalhadas (R3-4.2), matriz de relações (R3-4.3), voz (R3-4.4), camadas de informação (feitas no R3-6.2: coluna `camadas`). Onde a ficha precisa deles, traz só o que já está documentado e `desconhecida`.

## 1. Conferências

| Verificação | Resultado |
|---|---|
| Principais com ficha completa | **12 de 12** (17 campos, 0 vazio) |
| Agenda e pressão distintas | **12 agendas e 12 pressões diferentes** |
| Heróis vivos / generais | 2 (Tazk, Vurga) / 2 (Orsk, Ilkar) |
| Povo, sub-raça, classe e profissão | conferidos em `ALINHAMENTO_VEU.md` (sub-raça `—` onde o catálogo não atribui) |
| Origem | desconhecida em todos (nenhuma documentada) |
| Estado | vivo em todos |
| Acúmulos de cargo (CI-09) | **6 de 7 marcados como "acúmulo intencional proposto"** (Rusk, Teshi, Yeshe, Krell, Ondra, Dhun); o sétimo, Nuvik, não tem ficha completa (fora do mínimo de D-22) |
| Sem acúmulo | Morvan (sem assento, CI-10), Orsk, Cerrane, Ilkar, Tazk, Vurga |
| Lugares com principal | 8 de 8 (Morrovéu 3; Beira-Sal e Candeia-Morta 2; os demais 1) |
| Relógio ligado a cada principal | 12 de 12 (8 relógios de `06`; progresso 1/6 a 3/6 como em `06`) |

## 2. Fichas por lugar

### Morrovéu — `city.morroveu`

#### `npc.morvan-voz-baixa` — Morvan Voz-Baixa (N-01)

| Campo | Ficha |
|---|---|
| Idade | desconhecida |
| Povo (ID) e sub-raça | humano (Humano); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Rei Vencedor do Véu dos Malditos; reina em Morrovéu há cerca de cem anos (contagem local); sem assento declarado no Conselho (CI-10) |
| Cidade e facção | city.morroveu; facção: nenhuma |
| Personalidade, valores, medos, limites | reservado e contido (voz baixa no nome); valores: pactos de pé e terras estáveis; medo: ser vencido na Provação das Armas e o que a estabilização custa (S). Traços e medos: ASSUMINDO |
| Capacidades, influência, restrições | necromante: ofício dele, não de povo, com custo e limites (doc 01 parte 4 §3); vence a coroa; restrição: não rompe nem reescreve os pactos e não decide contra o texto de um pacto (doc 04 §2); catálogo: classe invocador, profissão escrivao-judicial (R3-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | rei de uma Corte que tem Conselho dos Pactuários e Guarda do Rei; sem facção (C); relação pessoal com Orsk, Cerrane e com os lugares: desconhecida (R3-4.3) |
| Biografia e marcos | C: reina há cerca de cem anos; estabilizou as terras; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: É necromante; reina em Morrovéu há cerca de cem anos; estabilizou as terras. S: A estabilidade tem um custo que ele paga e ninguém nomeia. D: Origem, idade, método da estabilização e por que ninguém o venceu. |
| Estado atual | vivo |
| Agenda atual | Manter as terras estáveis e os pactos de pé sem ser vencido na Provação das Armas. |
| Pressão | Os Pactuários se dividem entre defender o rei que trouxe paz e lembrar que o rei deve ser vencido; a lista de desafiantes não anda. |
| Condição de evolução | ASSUMINDO: se `clock.desafiantes` (2/6) avança, escolhe entre responder em público ao Conselho ou manter o silêncio; o motivo de ninguém o ter vencido segue `desconhecido` |
| Camadas de informação | `fact.morvan-necromante-reina`, `fact.morvan-custo-estabilizacao` (`14-camadas-de-informacao.csv`) |

#### `npc.orsk-cicatriz-de-bando` — Orsk Cicatriz-de-Bando (N-02)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | bugbear (Ursino Sombrio); sub-raça: ursino-da-sombra |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | General da Guarda de Morrovéu; líder da Guarda do Rei (F-01) |
| Cidade e facção | city.morroveu; facção: guarda-do-rei (Guarda do Rei, líder) |
| Personalidade, valores, medos, limites | disciplinado e silencioso; valores: guarda unida; medo: ser forçado a escolher entre rei e cidade (S). Traços e medos: ASSUMINDO |
| Capacidades, influência, restrições | comandante de escolta profissional; comanda a guarda de Morrovéu; restrição: não comanda as vigias dos pactos dos outros lugares (C); catálogo: classe comandante, profissão escolta-profissional (R3-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | mesma cidade de Morvan e de Cerrane (C); facção Guarda do Rei: aliada de Defensores da Paz, Turnos da Vigília; rival de Memória das Provações (C, doc 04 §9 e §10); demais relações: desconhecidas (R3-4.3) |
| Biografia e marcos | C: comanda a guarda que serve ao rei; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Comanda a guarda que serve ao rei. S: Já pensou, sozinho, a quem a guarda seguiria se o rei caísse. D: O que a guarda faria se o rei fosse vencido. |
| Estado atual | vivo |
| Agenda atual | Manter a guarda unida e disciplinada enquanto a pergunta sobre o rei não é feita em voz alta. |
| Pressão | A guarda serve ao rei, não à cidade; os Pactuários testam a lealdade dela. |
| Condição de evolução | ASSUMINDO: se `clock.desafiantes` (2/6) avança, escolhe entre manter a guarda fechada ao rei ou deixar a pergunta chegar ao quartel |
| Camadas de informação | `fact.orsk-comanda-guarda`, `fact.orsk-guarda-sem-rei` (`14-camadas-de-informacao.csv`) |

#### `npc.cerrane-heranca-firme` — Cerrane Herança-Firme (N-03)

| Campo | Ficha |
|---|---|
| Idade | idosa (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | tiefling (Nascido do Crepúsculo); sub-raça: crepuscular-de-chifres |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Presidente do Conselho dos Pactuários (preside e não vota); benfeitora de Morrovéu (tríade) |
| Cidade e facção | city.morroveu; facção: nenhuma |
| Personalidade, valores, medos, limites | conciliadora e cautelosa; valores: Conselho de pé; medo: que o Conselho se parta ou que a cláusula que cala seja lida (S). Traços e medos: ASSUMINDO |
| Capacidades, influência, restrições | mediadora; convoca e preside; restrição: não vota por lugar nenhum, não desempata e não desafia o rei (doc 04 §2.2); catálogo: classe diplomata, profissão mediador (R3-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | preside os oito assentos; mesma cidade de Morvan e de Orsk; o Arquivo dos Reis que Passaram lembra a regra que ela cala (C); relação pessoal com Morvan: desconhecida (R3-4.3) |
| Biografia e marcos | C: preside o Conselho; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Preside o Conselho dos Pactuários. S: Sabe de uma cláusula da coroa que o Conselho não leu. D: Quem presidirá o Conselho se o rei cair. |
| Estado atual | vivo |
| Agenda atual | Manter um assento por lugar e impedir que o Conselho se parta entre defensores e críticos do rei. |
| Pressão | Os dois lados a cobram; o Arquivo dos Reis que Passaram lembra a regra que ela cala. |
| Condição de evolução | ASSUMINDO: se `clock.desafiantes` (2/6) avança, escolhe entre convocar a votação pública ou adiar a sessão; nada se resolve por calendário |
| Camadas de informação | `fact.cerrane-preside-conselho`, `fact.cerrane-clausula-nao-lida` (`14-camadas-de-informacao.csv`) |

### Lamaluz — `city.lamaluz`

#### `npc.tazk-pedra-ao-sol` — Tazk Pedra-ao-Sol (N-05)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | lizardino (Escamado dos Mangues); sub-raça: escamado-dos-mangues |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Piloto-Mor de Lamaluz; herói vivo (tríade); líder dos Pilotos dos Canais (F-04) |
| Cidade e facção | city.lamaluz; facção: pilotos-dos-canais (Pilotos dos Canais, líder) |
| Personalidade, valores, medos, limites | paciente e prático; valores: canais navegáveis; medo: guiar quem parte e ser acusado de quebrar o pacto (S). Traços e medos: ASSUMINDO |
| Capacidades, influência, restrições | navegador; conhece os canais mutáveis (C); restrição: guia, não faz nem desfaz pacto e não manda nos Pactuários dos Canais (C); catálogo: classe explorador, profissão navegador (R3-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | os que partem pedem guia e o pacto os acusa (C, doc 04 §10); facção Pilotos dos Canais: aliada de Guias de Sal; rival de Guardiões das Lamparinas (C, doc 04 §9 e §10); demais relações: desconhecidas (R3-4.3) |
| Biografia e marcos | C: conhece os canais mutáveis de Lamaluz; a lenda de herói é ASSUMINDO (barcas tiradas de uma cheia fora de época); demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Conhece os canais mutáveis de Lamaluz. S: Já guiou alguém que partia. D: Se o pacto pode ser cumprido sem prender ninguém. |
| Estado atual | vivo |
| Agenda atual | Manter os canais navegáveis e decidir se guia ou não as famílias que partem. |
| Pressão | Os que partem pedem guia; o pacto manda que fiquem e acusa quem guia. |
| Condição de evolução | ASSUMINDO: se `clock.partida` (3/6) completa, é julgado ou testemunha em julgamento formal dentro de Lamaluz; a pena segue `desconhecida` (L-09) |
| Camadas de informação | `fact.tazk-conhece-canais`, `fact.tazk-guiou-quem-partia` (`14-camadas-de-informacao.csv`) |

### Fozbreu — `city.fozbreu`

#### `npc.rusk-rede-da-barra` — Rusk Rede-da-Barra (N-06)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | homem-rato (Ratoide dos Mercados); sub-raça: ratoide-dos-mercados |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Mestre da Barra, Pactuário da Foz (assento de Fozbreu); líder dos Defensores da Paz (F-02); acúmulo intencional proposto (CI-09, assento e facção) |
| Cidade e facção | city.fozbreu; facção: defensores-da-paz (Defensores da Paz, líder) |
| Personalidade, valores, medos, limites | cuidadoso e atento a papel; valores: barra pilotável; medo: que o fardo sem dono chegue a ele (S). Traços e medos: ASSUMINDO |
| Capacidades, influência, restrições | guardião do código e navegador; guarda a barra e o Pacto da Barra; restrição: sem frota e sem poder fora do porto (C); catálogo: classe guardiao-do-codigo, profissão navegador (R3-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | assento de Fozbreu no Conselho; relação pessoal com os visitantes e com o dono do fardo: desconhecida (R3-4.3); facção Defensores da Paz: aliada de Guarda do Rei, Casa dos Poços, Guardiões das Lamparinas; rival de Memória das Provações, Irmandade das Despedidas (C, doc 04 §9 e §10) |
| Biografia e marcos | C: guarda o Pacto da Barra; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Guarda o Pacto da Barra. S: Sabe quem é o dono do fardo que ninguém reclamou. D: O que o armazém realmente guarda. |
| Estado atual | vivo |
| Agenda atual | Manter a barra pilotável e o porto de pé sem frota de guerra. |
| Pressão | Chegam fardos sem papel que o pacto reconheça e visitantes de longe que não são moradores. |
| Condição de evolução | ASSUMINDO: se `clock.fardos` (2/6) avança, escolhe entre registrar o fardo, calar ou entregar o dono; a Descida não é afetada |
| Camadas de informação | `fact.rusk-guarda-barra`, `fact.rusk-dono-do-fardo` (`14-camadas-de-informacao.csv`) |

### Beira-Sal — `city.beira-sal`

#### `npc.vurga-sede-vencida` — Vurga Sede-Vencida (N-07)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | licantropo (Metamorfo Lupino); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Guia-Mestra dos Guias de Sal; herói vivo (tríade); líder dos Guias de Sal (F-06) |
| Cidade e facção | city.beira-sal; facção: guias-de-sal (Guias de Sal, líder) |
| Personalidade, valores, medos, limites | resistente e direta; valores: acertos honestos; medo: que um guia sumido volte a falar (S). Traços e medos: ASSUMINDO |
| Capacidades, influência, restrições | guia de deserto; conduz caravanas pelo Ermo de Sal; restrição: guia e testemunha acertos, não faz pacto pelo posto nem fala pelas Marcas das Feras (C); catálogo: classe explorador, profissão guia-de-deserto (R3-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | mesma cidade de Teshi (C); os guias cobram por pactos orais sem prova (C); facção Guias de Sal: aliada de Pilotos dos Canais; rival de Casa dos Poços (C, doc 04 §9 e §10); demais relações: desconhecidas (R3-4.3) |
| Biografia e marcos | C: conduz caravanas pelo Ermo; a lenda de herói é ASSUMINDO (travessias do Ermo de Sal); demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Conduz caravanas pelo Ermo. S: Sabe qual guia sumiu e voltou sem sede. D: Por que um poço passou a dar água salgada. |
| Estado atual | vivo |
| Agenda atual | Manter a Rota do Sal viva e os acertos dos guias honestos. |
| Pressão | Os guias cobram por pactos orais sem prova e as caravanas de fora contestam a conta. |
| Condição de evolução | ASSUMINDO: se `clock.acertos-orais` (2/6) avança, escolhe entre testemunhar um acerto sem prova ou recusar; a Rota do Sal segue só do lado do Véu |
| Camadas de informação | `fact.vurga-conduz-caravanas`, `fact.vurga-guia-sem-sede` (`14-camadas-de-informacao.csv`) |

#### `npc.teshi-no-de-poco` — Teshi Nó-de-Poço (N-08)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | aracnideo (Aracnídeo); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Pactuária dos Poços (assento de Beira-Sal); guardiã do Pacto da Caravana; líder da Casa dos Poços (F-07); acúmulo intencional proposto (CI-09, assento e facção) |
| Cidade e facção | city.beira-sal; facção: casa-dos-pocos (Casa dos Poços, líder) |
| Personalidade, valores, medos, limites | minuciosa e fechada; valores: Pátio neutro; medo: que os poços não deem conta (S). Traços e medos: ASSUMINDO |
| Capacidades, influência, restrições | guardiã do código e avaliadora de materiais; guarda o Poço Primeiro e as sentinelas de sal (C); restrição: não manda nos guias nem nas caravanas (C); catálogo: classe guardiao-do-codigo, profissão avaliador-de-materiais (R3-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | mesma cidade de Vurga (C); assento de Beira-Sal; qualquer falha nos poços aumenta a cobrança dos guias (C); facção Casa dos Poços: aliada de Defensores da Paz; rival de Guias de Sal (C, doc 04 §9 e §10); demais relações: desconhecidas (R3-4.3) |
| Biografia e marcos | C: guarda o Poço Primeiro e as sentinelas de sal; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Guarda o Poço Primeiro e as sentinelas de sal. S: Escondeu quanto água o posto realmente tem. D: Até quando os poços dão conta. |
| Estado atual | vivo |
| Agenda atual | Manter os poços sinalizados e o Pátio dos Acertos neutro. |
| Pressão | Depende de poucos poços; qualquer falha aumenta a cobrança dos guias. |
| Condição de evolução | ASSUMINDO: se `clock.acertos-orais` (2/6) avança, escolhe entre revelar a água que há ou manter a reserva escondida |
| Camadas de informação | `fact.teshi-guarda-pocos`, `fact.teshi-agua-escondida` (`14-camadas-de-informacao.csv`) |

### Selovale — `city.selovale`

#### `npc.yeshe-forma-emprestada` — Yeshe Forma-Emprestada (N-09)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | metamorfo (Moldável); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Presidente do Conselho dos Selos (assento de Selovale); guardiã do Arquivo dos Pactos Velhos; líder da Memória das Provações (F-03); acúmulo intencional proposto (CI-09, assento e facção) |
| Cidade e facção | city.selovale; facção: memoria-das-provacoes (Memória das Provações, líder) |
| Personalidade, valores, medos, limites | erudita e adaptável; valores: o texto certo; medo: invocarem a cláusula antes dela (S). Traços e medos: ASSUMINDO |
| Capacidades, influência, restrições | cronista e arquivista; interpreta e guarda; restrição: não rompe selo nem decide por Morrovéu (C); catálogo: classe cronista, profissão arquivista (R3-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | assento de Selovale; o Conselho dos Selos julga o conflito entre rei e Conselho (doc 04 §5); os Pactuários discordam de qual versão do texto vale (C); facção Memória das Provações: aliada de Irmandade das Despedidas; rival de Guarda do Rei, Defensores da Paz (C, doc 04 §9 e §10); demais relações: desconhecidas (R3-4.3) |
| Biografia e marcos | C: preside o Conselho dos Selos; guarda o Arquivo dos Pactos Velhos; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Preside o Conselho dos Selos; guarda o Arquivo dos Pactos Velhos. S: Já leu uma cláusula que o Conselho desconhece. D: O que o fim do pacto concede, a quem ou contra quem. |
| Estado atual | vivo |
| Agenda atual | Descobrir o que o pacto antigo que vence concede antes que alguém invoque a cláusula. |
| Pressão | Os Pactuários discordam de qual versão do texto vale. |
| Condição de evolução | ASSUMINDO: se `clock.pacto-que-vence` (2/6) avança, escolhe entre publicar a cláusula ou guardá-la; o que o fim concede segue `desconhecido` |
| Camadas de informação | `fact.yeshe-preside-selos`, `fact.yeshe-clausula-lida` (`14-camadas-de-informacao.csv`) |

### Borda-Mansa — `city.borda-mansa`

#### `npc.krell-eco-da-vigia` — Krell Eco-da-Vigia (N-10)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | kenku (Corvídeo das Ruínas); sub-raça: corvideo-das-ruinas |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Vigia-Mor de Borda-Mansa, Pactuário da Vigia (assento); guardião do Pacto da Orla; líder dos Vigias da Orla (F-08); acúmulo intencional proposto (CI-09, assento e facção) |
| Cidade e facção | city.borda-mansa; facção: vigias-da-orla (Vigias da Orla, líder) |
| Personalidade, valores, medos, limites | atento e econômico em palavras; valores: abrigo e controle; medo: que alguém passe com ajuda de dentro (S). Traços e medos: ASSUMINDO |
| Capacidades, influência, restrições | sentinela e observador do clima; comanda a Torre das Fogueiras (C); restrição: vigia e abriga; só diz rumor sobre Aeltharyn e não liga o posto a Brumafunda (C, contrato de fronteira); catálogo: classe sentinela, profissão observador-do-clima (R3-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | assento de Borda-Mansa; aliado da Casa dos Fios por facção (C); rival `desconhecido` (CI-11); facção Vigias da Orla: aliada de Casa dos Fios; rival: `desconhecido` (CI-11) (C, doc 04 §9 e §10); demais relações: desconhecidas (R3-4.3) |
| Biografia e marcos | C: comanda a Torre das Fogueiras; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Comanda a Torre das Fogueiras. S: Alguém sem registro já passou pela orla com ajuda de dentro. D: O que há na faixa de neblina além da orla (só rumor). |
| Estado atual | vivo |
| Agenda atual | Abrigar quem chega e saber quem é quem sem fechar o posto. |
| Pressão | Chega gente de longe sem guia e sem registro, e a névoa só mostra o que quer. |
| Condição de evolução | ASSUMINDO: se `clock.orla-sem-guia` (1/6) avança, escolhe entre registrar quem chega ou deixar passar sem registro; nada sobre Aeltharyn além de rumor |
| Camadas de informação | `fact.krell-comanda-torre`, `fact.krell-ajuda-de-dentro` (`14-camadas-de-informacao.csv`) |

### Fio-Longo — `city.fio-longo`

#### `npc.ondra-fio-de-sono` — Ondra Fio-de-Sono (N-11)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | onirico (Onírico); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Mestra da Casa dos Fios (assento de Fio-Longo); guardiã do Pacto do Repouso; líder da Casa dos Fios (F-09); acúmulo intencional proposto (CI-09, assento e facção) |
| Cidade e facção | city.fio-longo; facção: casa-dos-fios (Casa dos Fios, líder) |
| Personalidade, valores, medos, limites | acolhedora e evasiva; valores: ninguém atacado dormindo; medo: que a Casa seja acusada de reter hóspedes (S). Traços e medos: ASSUMINDO |
| Capacidades, influência, restrições | tecelã de sonhos e estalajadeira; hospeda e guarda o pacto; restrição: não obriga ninguém a ficar nem a partir (C); catálogo: classe tecelao-de-sonhos, profissão estalajadeiro (R3-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | assento de Fio-Longo; aliada dos Vigias da Orla por facção (C); rival `desconhecido` (CI-11); parentes cobram a volta de hóspedes (C); facção Casa dos Fios: aliada de Vigias da Orla; rival: `desconhecido` (CI-11) (C, doc 04 §9 e §10); demais relações: desconhecidas (R3-4.3) |
| Biografia e marcos | C: governa a Casa dos Fios; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Governa a Casa dos Fios. S: Sabe de quem não quer partir e por quê. D: O que o quarto que ninguém abre guarda. |
| Estado atual | vivo |
| Agenda atual | Manter a Casa um lugar de repouso e o pacto de que ninguém é atacado dormindo. |
| Pressão | Parentes cobram a volta de hóspedes que não querem ir embora. |
| Condição de evolução | ASSUMINDO: se `clock.hospedes` (1/6) avança, escolhe entre abrir o quarto fechado ou manter a Casa discreta; o que há nele segue `desconhecido` |
| Camadas de informação | `fact.ondra-governa-casa`, `fact.ondra-hospedes-que-ficam` (`14-camadas-de-informacao.csv`) |

### Candeia-Morta — `city.candeia-morta`

#### `npc.ilkar-turno-aberto` — Ilkar Turno-Aberto (N-04)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | vampirico (Noctívago); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | General das Vigílias de Candeia-Morta; líder dos Turnos da Vigília (F-11) |
| Cidade e facção | city.candeia-morta; facção: turnos-da-vigilia (Turnos da Vigília, líder) |
| Personalidade, valores, medos, limites | vigilante e metódico; valores: turnos sem fechar; medo: que a Corte peça o que o Pacto da Vigília proíbe (S). Traços e medos: ASSUMINDO |
| Capacidades, influência, restrições | comandante de escolta profissional; comanda os turnos da cidade; restrição: não decide o que o Pacto da Vigília exige (C); catálogo: classe comandante, profissão escolta-profissional (R3-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | mesma cidade de Dhun (C); a divisão dos Pactuários chega ao quartel (C); facção Turnos da Vigília: aliada de Guarda do Rei; rival: `desconhecido` (CI-11) (C, doc 04 §9 e §10); demais relações: desconhecidas (R3-4.3) |
| Biografia e marcos | C: comanda as vigias da cidade; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Comanda as vigias da cidade. S: A Corte já pediu algo que o Pacto da Vigília proibiria. D: Se o Pacto da Vigília o obriga a se opor ao rei. |
| Estado atual | vivo |
| Agenda atual | Manter os turnos de vigia sem fechar e proteger a cidade de dia, quando ela é frágil. |
| Pressão | Os Pactuários das Despedidas divergem sobre o que o pacto pede de um rei necromante, e a divisão chega ao quartel. |
| Condição de evolução | ASSUMINDO: se `clock.despedidas` (2/6) avança, escolhe entre manter os turnos neutros ou recusar um pedido da Corte; nada se resolve por calendário |
| Camadas de informação | `fact.ilkar-comanda-vigias`, `fact.ilkar-pedido-da-corte` (`14-camadas-de-informacao.csv`) |

#### `npc.dhun-contorno-quieto` — Dhun Contorno-Quieto (N-12)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número; ASSUMINDO) |
| Povo (ID) e sub-raça | sombrio (Umbral); sub-raça: — |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Pactuário-mor das Despedidas (assento de Candeia-Morta); benfeitor da cidade (tríade); líder da Irmandade das Despedidas (F-10); acúmulo intencional proposto (CI-09, assento e facção) |
| Cidade e facção | city.candeia-morta; facção: irmandade-das-despedidas (Irmandade das Despedidas, líder) |
| Personalidade, valores, medos, limites | sereno e discreto; valores: mortos em descanso; medo: o nome que mudou de lugar no Muro (S). Traços e medos: ASSUMINDO |
| Capacidades, influência, restrições | sacerdote e mestre de cerimônias; conduz as despedidas e guarda o Muro dos Nomes (C); restrição: guarda o pacto e o Muro, não confronta o rei sozinho (C); catálogo: classe sacerdote, profissão mestre-de-cerimonias (R3-4.2 detalha); influência e recursos além do cargo: desconhecidos |
| Relações | assento de Candeia-Morta; mesma cidade de Ilkar (C); os Pactuários divergem, sem dizer em voz alta, sobre o rei necromante (C); facção Irmandade das Despedidas: aliada de Memória das Provações; rival de Defensores da Paz (C, doc 04 §9 e §10); demais relações: desconhecidas (R3-4.3) |
| Biografia e marcos | C: conduz as despedidas e guarda o Muro dos Nomes; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Conduz as despedidas e guarda o Muro dos Nomes. S: Já viu um nome mudar de lugar no Muro. D: Se o pacto pede algo do rei. |
| Estado atual | vivo |
| Agenda atual | Manter o pacto de que os mortos descansam e a Casa das Despedidas aberta. |
| Pressão | Os Pactuários divergem, sem dizer em voz alta, sobre o que o pacto pede de um rei necromante. |
| Condição de evolução | ASSUMINDO: se `clock.despedidas` (2/6) avança, escolhe entre dizer em voz alta o que o pacto pede do rei ou manter o silêncio; nada se resolve por calendário |
| Camadas de informação | `fact.dhun-guarda-muro`, `fact.dhun-nome-mudou` (`14-camadas-de-informacao.csv`) |

## 3. O que a ficha não decide

- Nenhum fato sobre a necromancia de Morvan, a cláusula de Selovale, o fardo de Fozbreu, o poço de Beira-Sal, os hóspedes de Fio-Longo ou o Muro dos Nomes; permanecem em S e D.
- Nenhum resultado de relógio, Provação ou julgamento (L-09 segue `desconhecida`).
- Nuvik Canal-Torto e os demais da tríade sem ficha completa continuam só com nome e povo (fora do mínimo de D-22).

## 4. Decisão pendente

**D-83:** aprovar as 12 fichas e a marca de acúmulo intencional (CI-09). Opções: **A.** aprovar; **B.** trocar traço, medo ou condição de evolução de alguma ficha (diga qual). **Recomendação: A.**
