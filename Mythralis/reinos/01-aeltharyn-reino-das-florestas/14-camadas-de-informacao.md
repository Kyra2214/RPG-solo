# 14 — Camadas de informação (M5.2)

**Fonte:** doc 02 §4–§5 (cinco camadas do mundo persistente; segredos e arquivos); doc 04 §10 (registros público, local, restrito, arquivo histórico, privado); doc 06 §10.3 e §11 (contexto que fica fora; segredo sem caminho de descoberta); doc 03 (campo *Conhecimento e segredo*); doc 01 §4, §9, §13; `12-conhecimento.csv`, `13-misterios-limites.csv`, `12-relogios.csv`. Dados em `14-camadas-de-informacao.csv` (prefixo `fact.`).

## Regras

- **Quatro camadas por fato:** público, local, restrito, secreto. A camada descreve **quem pode saber sem esforço especial**; não diz se o fato é verdadeiro (doc 04 §10: classificação não determina a verdade).
- **66 fatos** em 4 camadas: 6 públicos, 34 locais, 14 restritos, 12 secretos. Fatos *desconhecido* (`12-conhecimento.csv`) não entram: não há o que proteger. Boatos não confirmados também não.
- **Critério de classificação** (leitura editorial, ASSUMINDO, derivada do doc 04 §10 e do doc 03): **secreto** = o texto diz que o titular esconde, omite, não informa ou teme revelar, ou é o estado real do mundo que ninguém detém (relógios); **restrito** = o titular guarda o fato em registro, amostra, caderno, diário ou lista que só cede por autorização, favor ou ordem legal; **local** = conhecido pela comunidade ou registrado em registro local, sem esconderijo escrito; **público** = visível a qualquer um na região.
- **Segredo nunca sai por narração espontânea** (doc 02 §8, doc 06 §11): um fato restrito ou secreto só entra na cena depois que o personagem cumpre um dos caminhos da coluna *caminho*. O mundo e o Mestre sabem; o personagem só sabe o que descobriu (doc 02 §4).
- **Ficha:** cada ficha de pessoa de Elarion ganhou o campo *Camadas* com os IDs `fact.*` da pessoa (`12-fichas-elarion.md` e `.csv`). Para as demais entidades, o campo vive só no CSV desta camada, pela coluna `entidade`.
- Nada de lore foi criado: o fato é o que o texto escreve; o *caminho* vem da evidência que o texto cita (amostras, cadernos, registro, saída escrita no doc 03) ou do caminho-padrão da camada.

## Camadas e acesso

| Camada | Quem sabe sem esforço | Caminho mínimo para o personagem descobrir | Pode aparecer em resumo? |
|---|---|---|---|
| **Público** | qualquer pessoa na região | ouvir, estar no local, perguntar | sim |
| **Local** | a comunidade, a cidade-mãe e quem mantém o registro local | presença na comunidade, perguntar ao titular, ler o registro local, testemunho | sim (camada máxima dos resumos) |
| **Restrito** | só o titular e quem ele autorizar | autorização de arquivo, favor ou confiança do titular, ordem legal, exame da evidência guardada (ASSUMINDO: um caminho basta) | **não** |
| **Secreto** | só o titular (ou ninguém, no caso dos relógios) | evidência independente (inspeção, comparação de fontes ou testemunho) **mais** confiança ou confronto; o titular não confessa só por ser perguntado (ASSUMINDO) | **não** |

## Caminhos (coluna `caminho`)

| Código | Significa |
|---|---|
| `ouvir` | rumor, conversa, aviso público |
| `presenca` | estar no local, assembleia ou comunidade |
| `perguntar` | perguntar ao titular ou a quem convive com o fato |
| `registro-local` | ler registro local (atas, manutenção, contas, pedágio, livros de farol) |
| `arquivo-autorizado` | arquivo restrito ou privado, com autorização (doc 04 §10; doc 01 §18) |
| `ordem-legal` | decisão formal que obriga a entrega |
| `favor` | confiança, dívida ou troca com o titular |
| `inspecao` | examinar amostra, terreno, estoque ou evidência física |
| `comparacao` | cruzar duas fontes que discordam |
| `testemunho` | ouvir quem viu ou viveu o fato |

## Resumos e camadas

- `14-resumos-de-contexto.csv` ganhou a coluna `camada_max`: **local** para cidades, assentamentos, facções e pessoas (nenhum resumo contém fato restrito ou secreto); **secreto** para os 7 relógios (descrevem causas ocultas; uso só de Mestre e motor, nunca em contexto público).
- Conferência feita: nenhum fato restrito ou secreto aparece, por frase-marcador, em resumo de camada máxima `local`. Resumo é consulta, não texto de lore.

## Subida de camada

- Descobrir um fato muda o **conhecimento do personagem**, não a camada do fato (doc 02 §4). Divulgar um fato restrito ou secreto por ato do jogador ou de um NPC é evento do mundo e precisa de registro; a camada só muda por esse registro (ASSUMINDO).
- Revelar um fato ao Mestre para conduzir a cena não o revela ao jogador (doc 02 §5).

## Fatos por camada


### Público (6)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.nimeth-documentos-sumidos` | documentos da Guerra das Cinzas Verdes desapareceram; quem os levou e onde há cópias é desconhecido | confirmado | `npc.nimeth-vela-chuva` | ouvir; perguntar | qualquer pessoa em Elarion comenta; perguntar a Nimeth | doc 01 §5.1 |
| `fact.mist-a-sintomas` | duas árvores-coração mostram perda de brilho nas folhas internas e raízes que evitam certas áreas; os sintomas lembram a Vigília Longa; não há prova de causa comum | confirmado | — | ouvir; presenca | ir ao Jardim das Árvores-Coração ou ouvir os Vigias | doc 01 §13 A |
| `fact.mist-c-navio` | o navio chegou a Thalanor em boas condições, sem tripulação e sem sinais claros de combate | confirmado | — | ouvir; presenca | ir ao porto de Thalanor | doc 01 §13 C |
| `fact.mist-d-mina` | a mina de Torvelume está interditada; abrir sem avaliação pode causar desabamento e expor trabalhadores | confirmado | `npc.dauren-galho-de-ferro` | ouvir; presenca | ir a Torvelume | doc 01 §13 D |
| `fact.mist-h-maelor` | a riqueza de Maelor não prova culpa e ele nega saber de extração ilegal por agentes contratados | confirmado | `npc.maelor-venn` | ouvir; perguntar | perguntar a Maelor | doc 01 §13 H; doc 01 §9 |
| `fact.mist-i-galeria` | uma galeria tem um espaço vazio entre duas soberanas documentadas; a corte diz que o retrato foi perdido e a tradição popular diz que ela pediu para ser apagada | confirmado | — | ouvir; presenca | visitar a galeria | doc 01 §13 I |

### Local (34)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.raiz-germinacao-anormal` | alguns lotes de sementes tiveram germinação anormal; a causa é desconhecida | confirmado | `npc.nivara-folha-de-orvalho` | presenca; perguntar | visitar os viveiros; Nivara responde a quem pergunta | doc 03 §2.1 |
| `fact.sete-degraus-volume` | Othalen conhece o volume de passagem pela ponte | confirmado | `npc.othalen-vira-pedra` | registro-local; perguntar | ler os registros do povoado ou perguntar a Othalen | doc 03 §2.2 |
| `fact.sete-degraus-isencao` | três casas nobres atravessam sem pagar e os registros não explicam; Othalen suspeita de privilégio legal e não viu o decreto original | suspeita | `npc.othalen-vira-pedra` | registro-local; perguntar | ler os registros ou perguntar a Othalen; o decreto original segue desconhecido | doc 03 §2.2 |
| `fact.sete-degraus-tarifa-local` | a cópia local de tarifas de Sete Degraus difere da versão exposta na capital | confirmado | `npc.othalen-vira-pedra` | registro-local; comparacao | comparar a cópia local com a versão exposta em Elarion | doc 03 §2.2 |
| `fact.junco-defeitos-dique` | o dique tem defeitos estruturais; quem autorizou o lote de material inferior é desconhecido | confirmado | `npc.beren-tres-canais`; `npc.elsin-varzea-clara` | presenca; perguntar | ver o dique ou perguntar a Beren | doc 03 §3.1; doc 01 §9 |
| `fact.nymvar-material-inferior` | um trecho do dique foi reparado com material inferior; quem autorizou a compra é desconhecido | confirmado | `npc.elsin-varzea-clara`; `npc.beren-tres-canais` | presenca; perguntar | perguntar em Nymvar ou Junco Alto | doc 01 §9 |
| `fact.tres-barcas-passagens` | Sava sabe quais embarcações passaram; a escrituração de cargas não é completa | confirmado | `npc.sava-mao-de-corrente` | registro-local; perguntar | ler o registro das travessias ou perguntar a Sava | doc 03 §3.2 |
| `fact.ponta-luzes-navio` | pescadores viram luzes incomuns perto da rota do navio sem tripulação e discordam sobre data e distância; Yara preserva os relatos | confirmado | `npc.yara-sal-de-mare` | presenca; testemunho; perguntar | ouvir os pescadores ou Yara | doc 03 §4.1 |
| `fact.farol-intervalo-luz` | numa noite houve um intervalo de luz no farol, não explicado; o livro de manutenção dessa noite está incompleto | confirmado | `npc.terenna-luz-de-lastro` | registro-local; perguntar | ler o livro do farol ou perguntar a Terenna | doc 03 §4.2 |
| `fact.copa-marcas-corte` | Arieth conhece marcas de corte e árvores doentes; não prova quem paga pelos cortes noturnos | confirmado | `npc.arieth-tres-mudas` | presenca; inspecao; perguntar | percorrer a mata ou perguntar a Arieth | doc 03 §5.1 |
| `fact.passo-horarios` | Calar conhece os horários habituais de passagem, não o comportamento de todas as manadas | confirmado | `npc.calar-passo-curvo` | presenca; perguntar | perguntar a Calar | doc 03 §5.2 |
| `fact.passo-marco-movido` | um marco foi movido após uma enchente e ainda não foi recolocado oficialmente | confirmado | `npc.calar-passo-curvo` | presenca; perguntar | ir ao passo ou perguntar a Calar | doc 03 §5.2 |
| `fact.lodo-sinais-contaminacao` | Nima reconhece alguns sinais de contaminação, mas não identifica o veneno | confirmado | `npc.nima-barro-manso` | presenca; perguntar | perguntar a Nima | doc 03 §6.1 |
| `fact.junco-canais` | Vela conhece canais praticáveis em certas estações, não rotas seguras o ano inteiro | confirmado | `npc.vela-tres-margens` | presenca; perguntar | perguntar a Vela ou a barqueiros | doc 03 §6.2 |
| `fact.vigilia-medicao-embaracosa` | Orelia recusou apagar uma medição considerada embaraçosa | confirmado | `npc.orelia-ceu-de-cinza` | perguntar; registro-local | perguntar a Orelia ou a quem lhe pediu | doc 03 §7.1 |
| `fact.eco-marcas-alteradas` | Mira sabe quais marcas de trilha foram alteradas recentemente; quem as alterou é desconhecido | confirmado | `npc.mira-ouve-chuva` | presenca; inspecao; perguntar | ir à trilha ou perguntar a Mira | doc 03 §8.1 |
| `fact.sena-colmeias` | colmeias abandonaram uma área; a causa (pesticida, clima ou doença) não foi provada | confirmado | `npc.sena-polen-dourado` | presenca; perguntar | perguntar a Sena ou aos apicultores | doc 03 §9.1 |
| `fact.vessa-cancao` | uma canção foi apropriada por uma família nobre; a versão mais antiga não foi encontrada | confirmado | `npc.vessa-fio-dourado` | presenca; perguntar | perguntar a Vessa | doc 03 §10.1 |
| `fact.meda-pecas` | as peças disponíveis não bastam para todas as encomendas; a causa (má previsão ou desvio de materiais) é desconhecida | confirmado | `npc.meda-martelo-calmo` | presenca; perguntar | perguntar a Meda | doc 03 §11.1 |
| `fact.rovan-tremores` | os tremores não ocorrem em intervalos regulares | confirmado | `npc.rovan-cinza-de-ferro` | presenca; perguntar | perguntar a Rovan ou aos moradores | doc 03 §11.2 |
| `fact.rovan-pedidos-inspecao` | Rovan conserva cópias de pedidos de inspeção sem resposta | confirmado | `npc.rovan-cinza-de-ferro` | registro-local; perguntar | perguntar a Rovan | doc 03 §11.2 |
| `fact.saelith-corte-ancestrais` | há corte de árvores ancestrais; quem corta é desconhecido | confirmado | `npc.saelith-arco-de-chuva`; `npc.eryndra-folha-verde` | presenca; inspecao; perguntar | percorrer a mata ou perguntar aos Vigias | doc 01 §9 |
| `fact.lethariel-oitava-cidade` | Lethariel suspeita, por uma crônica, de uma oitava cidade fundadora e a procura | suspeita | `npc.lethariel-nascente-clara` | perguntar; registro-local | perguntar a Lethariel no Arquivo | doc 01 §9 |
| `fact.selvaris-manuscrito` | Selvaris suspeita que um manuscrito prove a oitava cidade fundadora | suspeita | `npc.selvaris-noctel` | perguntar; registro-local | perguntar a Selvaris | doc 01 §9 |
| `fact.eryndra-marcas` | Eryndra conhece as marcas de corte na mata | confirmado | `npc.eryndra-folha-verde` | presenca; perguntar | perguntar a Eryndra | doc 01 §9 |
| `fact.avariel-pontes` | Avariel tem relatórios de pontes danificadas | confirmado | `npc.avariel-thorne` | registro-local; perguntar | perguntar ao Intendente ou ao pessoal de manutenção | doc 01 §9 |
| `fact.nara-mapas-alterados` | um conjunto de mapas do Arquivo das Seivas foi alterado; quem alterou é desconhecido | confirmado | `npc.nara-tres-fios` | perguntar; arquivo-autorizado; comparacao | perguntar a Nara; comparar cópias exige autorização no Arquivo | doc 01 §9 |
| `fact.mist-b-manuscrito` | o manuscrito da oitava cidade é fragmentário, o nome da comunidade está apagado, ela não assinou os juramentos e uma cópia incompleta circula entre estudiosos | confirmado | `npc.lethariel-nascente-clara`; `npc.selvaris-noctel` | perguntar; registro-local | perguntar a estudiosos em Elarion ou Caladwen | doc 01 §13 B |
| `fact.mist-b-registros-divergem` | os registros não concordam sobre todos os nomes dos sete assentamentos | confirmado | `npc.lethariel-nascente-clara` | registro-local; comparacao | comparar registros de cidades diferentes | doc 01 §13 B |
| `fact.mist-c-carga` | a carga do navio foi registrada de forma incompleta | confirmado | — | registro-local; perguntar | consultar o registro da alfândega de Thalanor | doc 01 §13 C |
| `fact.mist-d-vibracoes` | vibrações e luzes foram relatadas perto da mina | alegado | — | presenca; testemunho | ouvir moradores de Torvelume e do Ferro-Folha | doc 01 §13 D |
| `fact.mist-e-marcas` | guias relatam marcas de trilha onde não foram colocadas | alegado | — | presenca; testemunho | ouvir guias de Brumafunda | doc 01 §13 E |
| `fact.mist-h-resina` | a Companhia do Âmbar compra material de origem duvidosa | confirmado | — | registro-local; comparacao | comparar cargas e pagamentos (evidência listada no mistério H) | doc 01 §13 H |
| `fact.mist-j-raposa` | a raposa de espelho seria vista em noites de névoa perto de quem esqueceu o próprio nome ou abandonou uma promessa | alegado | — | testemunho; perguntar | perguntar a guias de Brumafunda | doc 01 §13 J |

### Restrito (14)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.junco-amostras-reparo` | Beren guardou amostras do último reparo do dique | confirmado | `npc.beren-tres-canais` | favor; inspecao | Beren as entrega a quem apoia uma inspeção independente (saída escrita no doc 03) | doc 03 §3.1 |
| `fact.nymvar-provas-elsin` | Elsin tem provas de que o trecho do dique foi reparado com material inferior | confirmado | `npc.elsin-varzea-clara` | favor; arquivo-autorizado | Elsin as mostra a quem tem motivo e confiança; não as publica sozinha | doc 01 §9 |
| `fact.lodo-amostras-contradizem` | Nima guarda amostras que contradizem um relatório oficial; a origem não está confirmada | confirmado | `npc.nima-barro-manso` | favor; inspecao; ordem-legal | Nima as cede a exame independente (saída escrita no doc 03); só a ordem legal as tira dela | doc 03 §6.1 |
| `fact.junco-passagem-contrabando` | Vela sabe de uma passagem usada para contrabando; os responsáveis não estão identificados | confirmado | `npc.vela-tres-margens` | favor; testemunho | Vela só indica a passagem a quem ajuda na investigação | doc 03 §6.2 |
| `fact.vigilia-padrao-luz` | os registros do lago mostram um padrão incomum de luz; a origem sobrenatural não é provada | confirmado | `npc.orelia-ceu-de-cinza` | arquivo-autorizado; favor | Orelia mostra os registros a quem os usa sem entregá-los a uso militar exclusivo | doc 03 §7.1 |
| `fact.marel-copias-tratado` | Marel achou diferenças entre duas cópias de um tratado e não sabe qual é anterior | confirmado | `npc.marel-tinta-firme` | arquivo-autorizado; comparacao; favor | perícia documental (saída escrita no doc 03) ou confiança de Marel | doc 03 §7.2 |
| `fact.marel-registro-encomendas` | Marel guarda o registro de quem encomendou cada cópia | confirmado | `npc.marel-tinta-firme` | favor; ordem-legal | Marel pesa anonimato dos clientes contra interesse público; cede por confiança ou ordem legal | doc 03 §7.2 |
| `fact.eco-trilha-evacuacao` | Mira mantém uma trilha de evacuação fora dos mapas comerciais | confirmado | `npc.mira-ouve-chuva` | favor | acesso controlado, só com a confiança de Mira (saída escrita no doc 03) | doc 03 §8.1 |
| `fact.eco-sombra-torre` | Iven registrou uma sombra que não coincide com a posição aparente da torre; o diário tem intervalos sem observação por neblina; ele não afirma causa sobrenatural | confirmado | `npc.iven-marca-de-som` | favor; inspecao | Iven mostra o diário a quem propõe observação supervisionada (saída escrita no doc 03) | doc 03 §8.2 |
| `fact.sena-amostras-polen` | Sena guardou amostras de pólen de lotes diferentes | confirmado | `npc.sena-polen-dourado` | favor; inspecao | Sena as cede a quem pode analisá-las | doc 03 §9.1 |
| `fact.doria-cadernos` | Doria possui cadernos de produção que demonstram a queda de produção | confirmado | `npc.doria-tres-sulcos` | favor; arquivo-autorizado | Doria hesita em publicá-los; cede por confiança ou em auditoria de safra (saída escrita no doc 03) | doc 03 §9.2 |
| `fact.dalen-lista-hospedes` | Dalen sabe quem recebeu abrigo e recusou entregar a lista sem ordem legal clara | confirmado | `npc.dalen-porta-aberta` | ordem-legal; favor | só com ordem legal clara ou a confiança de Dalen (proteção de testemunha) | doc 03 §10.2 |
| `fact.rei-extracao-ilegal` | o rei sabe das árvores doentes e recebeu relatórios de extração ilegal; não conhece os responsáveis nem a causa completa | confirmado | `npc.aerendyl-vaelorian` | arquivo-autorizado; favor | audiência ou autorização para os arquivos da Coroa (doc 01 §18: arquivos restritos exigem autorização) | doc 01 §4.1 |
| `fact.neralis-mapa-desaparecido` | Neralis guarda um mapa recebido de uma pessoa desaparecida e não sabe se é confiável | confirmado | `npc.neralis-vento-manso` | favor; perguntar | Neralis o mostra a quem ajuda a verificá-lo no terreno | doc 01 §9; doc 01 §13 E |

### Secreto (12)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.raiz-estoque-escondido` | Nivara esconde parte do estoque de sementes para que não seja levado antes da votação da nova cota | confirmado | `npc.nivara-folha-de-orvalho` | inspecao; comparacao; favor | contar o estoque contra o declarado, ou ganhar a confiança de Nivara (auditoria transparente é saída escrita no doc 03) | doc 03 §2.1 |
| `fact.tres-barcas-travessia-noturna` | Sava autorizou uma travessia noturna sem registro para evacuar famílias, violando a regra formal por motivo humanitário | confirmado | `npc.sava-mao-de-corrente` | testemunho; comparacao; favor | famílias evacuadas e barqueiros podem testemunhar; Sava admite por confiança ou diante de testemunho | doc 03 §3.2 |
| `fact.copa-area-omitida` | Arieth omitiu uma área de doença para evitar uma interdição total | confirmado | `npc.arieth-tres-mudas` | inspecao; comparacao; favor | percorrer a mata e comparar com o relatório entregue, ou ganhar a confiança de Arieth (revisão de cotas é saída escrita no doc 03) | doc 03 §5.1 |
| `fact.doria-declaracoes` | algumas famílias declararam menos do que colheram; Doria teme que publicar os cadernos revele isso | suspeita | `npc.doria-tres-sulcos` | comparacao; favor | cruzar os cadernos com as declarações de safra, ou ganhar a confiança de Doria | doc 03 §9.2 |
| `fact.maereth-encontros-sul` | a princesa mantém encontros discretos com representantes das comunidades do sul, sem informar todos os conselheiros | confirmado | `npc.maereth-vaelorian` | testemunho; comparacao; favor | os representantes do sul podem confirmar; Maereth admite por confiança ou diante de testemunho | doc 01 §4.2 |
| `fact.relogio-heart-trees` | estado real e causas de “Doença das árvores-coração”: progresso inicial 2/6; o que move: semanas sem coleta de amostras; corte próximo a raízes; relatórios ocultos | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 02 §4 |
| `fact.relogio-dikes` | estado real e causas de “Dívida dos diques”: progresso inicial 3/6; o que move: cheias, adiamento de reparos e disputa por verbas | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 02 §4 |
| `fact.relogio-resin-smuggling` | estado real e causas de “Contrabando de resina”: progresso inicial 2/6; o que move: licenças falsas; patrulhas desviadas; demanda elevada | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 02 §4 |
| `fact.relogio-city-distrust` | estado real e causas de “Desconfiança entre cidades”: progresso inicial 1/6; o que move: tributos desiguais; rumores; socorro distribuído de forma injusta | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 02 §4 |
| `fact.relogio-ghost-ship` | estado real e causas de “Navio sem tripulação”: progresso inicial 1/6; o que move: provas ignoradas; carga movimentada sem registro; testemunhas intimidadas | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 02 §4 |
| `fact.relogio-sealed-mine` | estado real e causas de “Mina selada de Torvelume”: progresso inicial 1/6; o que move: escavação clandestina; tremores; sabotagem | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 02 §4 |
| `fact.relogio-centaur-corridors` | estado real e causas de “Corredores centauros”: progresso inicial 2/6; o que move: novas cercas; colheita fora de época; patrulhas sem consulta | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 02 §4 |

## Limites

- A camada é leitura editorial; se o autor discordar de uma classificação, muda-se a linha, não o lore.
- Fatos de pessoas, cidades e assentamentos fora de `12-conhecimento.csv`, de `13-misterios-limites.csv` e de `12-relogios.csv` ainda não têm camada; entram quando uma cena (M5.3) os usar.
- O motor decide como o conhecimento do personagem é guardado; aqui só ficam fato, camada e caminho.
