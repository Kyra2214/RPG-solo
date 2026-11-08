# 15 — Cenas de teste (R4-6.3)

**Estado:** rascunho de R4-6.3 (D-131 aceita pela continuação) · **Data:** 2026-11-07

**Etiqueta:** tudo é **[PROPOSTO]**; as cenas são testes de contexto, não texto de lore. **Fonte:** `14-resumos-de-contexto` (R4-6.1), `14-camadas-de-informacao` (R4-6.2), `13-sementes-de-aventura`, `12-voz.csv`, doc 04 §3, doc 07 e doc 01 parte 5 (calendário). Dados em `15-cenas-de-teste.csv`. Molde: `reinos/03-veu-dos-malditos/15-cenas-de-teste.md`.

## Regras

- Cada cena tem a lista **entra** (texto exato que vai ao contexto da IA, no máximo ~2.400 caracteres) e a lista **não entra** (o que fica fora e quando entraria).
- Só entram fatos de camada **público** ou **local**; fato restrito ou secreto só entra depois de cumprido o caminho de descoberta (R4-6.2). Relógios nunca entram.
- O texto de "entra" é montado dos resumos, fatos, sementes, vozes e linhas de rota já escritos; nada foi acrescentado. A data é a abertura do jogo: Pasto Magro, Lua 1, dia 1 do ano 611 da Contagem dos Marcos (doc 01 parte 5; a época da contagem é `desconhecida`).
- **Escolhas do roadmap (ASSUMINDO):** (a) Vazante; (b) Rasovau; (c) Terreiro-Maior; (d) Gramal; (e) viagem pelos Campos de Tabuleiro-Velho ao Gramal, sem duração nem distância (D-63); (f) a **chegada da Descida em Praia-Pardo**, só do lado do Vale (a opção da Rota do Sal em Alvasal fica para a bateria seguinte); (g) Encosta-Aberta, só em tom de rumor.
- Nenhuma cena traz NPC com `vivo = não`, rota com duração, ou afirmação sobre outro reino. Na cena (g) a única fala sobre o além é o rumor da "terra de árvores muito altas", marcado como rumor e não fato, sem nome, sem rota e sem ligação a nada do Vale (contrato §5, item 9).
- A contagem de caracteres inclui os rótulos `[id]` e as quebras entre linhas.

## `cena.marcha-vazante` — Acampamento de clã em migração: Vazante

Chegada ao acampamento móvel da Marcha e conversa com o Capitão sobre o campo.

**Entra (1883 caracteres):**

- Ano 611 da Contagem dos Marcos, Pasto Magro (inverno), Lua 1, dia 1.
- [city.vazante] Vazante — O Acampamento da Marcha. Acampamento móvel de migração e defesa (regime sazonal). Liderança: Kharos Casco-de-Marcha. Obrigação: Conduzir a migração e a defesa; responder à Assembleia. Interesse: Achar pasto e manter a marcha inteira. Tensão: Dois campos de pasto possíveis; qualquer escolha deixa um clã sem pasto. Povos: Orcano; Felídeo Errante; Centauro; Goblin de Legião; Lupino. Porte: grande (relativo, sem número).
- [city.vazante] Vizinhança: Ligações (sem duração): Tabuleiro-Velho (Tabuleiro é lugar-mãe); Terreiro-Maior (vínculo político).
- [npc.kharos-casco-de-marcha] Kharos Casco-de-Marcha, Centauro. Capitão de Marcha; guardião da Vazante. Limite: chefia a migração e a defesa; não preside a Assembleia nem decide pactos. Pressão: O mandato está perto do fim; a Assembleia se divide entre prova e aclamação; qualquer campo escolhido deixa um clã sem pasto.
- [voz npc.kharos-casco-de-marcha] tom: firme e prudente; decide devagar e fala em termos de marcha; modelo: "A marcha vai inteira ou não vai." | "Escolho o campo; os pactos não são meus para mexer." | "A Assembleia me deu o mandato; a Assembleia dirá o que vem depois."
- [fact.kharos-capitao-vazante|publico] É o Capitão de Marcha e reside na Vazante
- [fact.rukhan-bate-campos|local] Bate os campos para a Marcha
- [fact.mpl-07-dois-campos|local] a marcha escolhe entre dois campos e cada escolha deixa um clã sem pasto
- [seed.vazante.p] Os pousos da Marcha: Rukhan Retorno-Certo pede que alguém anote onde a Marcha pousa, só para conferir o rumor de que nunca dorme duas vezes no mesmo campo.
- [rumor] dizem que a Marcha nunca dorme duas vezes no mesmo campo; que o Capitão carrega um mapa que ninguém mais leu (rumor, não é fato).
- [regra] qual dos dois campos é o certo não está decidido; fica com o Mestre e com a Assembleia.

**Não entra:**

- `seed.vazante.m` e `.g`: só se a cena os disparar
- `fact.kharos-decidiu-sucessor`, `fact.rukhan-terceiro-campo` (restrito ou secreto) e `clock.campo-da-marcha` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.escolha-do-campo` e `event.pasto-fraco-na-estacao`: só se o gatilho aparecer na cena
- resumo de `npc.rukhan-retorno-certo`, `npc.bolgra-forja-de-campo`, `faction.guardas-da-marcha`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MPL-07 (`13-misterios-limites.md`): só ao aprofundar
- as outras sete cidades, as outras 13 facções e os outros 7 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.chegada-rasovau` — Chegada a um lugar de vau e mercado: Rasovau

Chegada ao vau e ao mercado de curral e primeiro contato com quem cobra a passagem.

**Entra (1723 caracteres):**

- Ano 611 da Contagem dos Marcos, Pasto Magro (inverno), Lua 1, dia 1.
- [city.rasovau] Rasovau — O Vau das Trocas. Vau e mercado de curral no curso do Argente (regime fixo). Liderança: desconhecida. Obrigação: Manter o vau transitável e o mercado de curral. Interesse: Cobrar passagem e acertar trocas. Tensão: Dois grupos de clãs cobram pela mesma passagem e cada um diz que o Trato é seu; o Trato está em disputa (doc 04 §8). Povos: Goblin; Vulpino; Leporino. Porte: média (relativo, sem número).
- [city.rasovau] Vizinhança: Ligações (sem duração): Praia-Pardo (fluxo do rio); Encosta-Aberta (só apoio; lugar-mãe Rasovau; sem trilha documentada).
- [npc.zinu-troca-justa] Zinu Troca-Justa, Goblin. Guardião do Vau; chefe de um dos grupos de clãs que cobram a passagem. Limite: cuida das pedras e cobra a passagem do lado dele; não fecha o vau. Pressão: Quem atravessa paga duas vezes ou nenhuma; o outro grupo o acusa de cobrar por pedras que não são suas.
- [voz npc.zinu-troca-justa] tom: negociador e orgulhoso; trata cada coisa como contrato; modelo: "O Trato do Vau é direito, não favor." | "Cuido das pedras e cobro do meu lado; não fecho o vau." | "Quem paga a mim não paga duas vezes."
- [fact.zinu-chefia-grupo-cobra|publico] Chefia um dos grupos que cobram
- [fact.mpl-03-dois-grupos-um-vau|publico] dois grupos cobram pela mesma passagem; o vau não se fecha
- [seed.rasovau.p] O recado adiantado: Quira Recado-Certo ouviu que um recado chegou a quem ainda não tinha chegado e pede que alguém anote quando isso acontecer, sem dizer que é verdade.
- [rumor] contam que uma das pedras do vau mudou de lugar de um ciclo para o outro (rumor, não é fato).
- [regra] a quem pertencem as pedras (L-04) é desconhecido; fica com o Mestre.

**Não entra:**

- `seed.rasovau.m` e `.g`: só se a cena os disparar
- `fact.zinu-deixou-passar-sem-pagar`, `fact.quira-recados-nao-entregues` (restrito ou secreto) e `clock.dupla-cobranca` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.quem-recebe-a-passagem`: só se o gatilho aparecer na cena
- resumo de `npc.quira-recado-certo`, `npc.lumbo-pedra-de-baixo`, `faction.pedras-de-cima`, `faction.pedras-de-baixo`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MPL-03 (`13-misterios-limites.md`): só ao aprofundar
- as outras sete cidades, as outras 13 facções e os outros 7 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.assembleia-terreiro` — Assembleia de clãs: Terreiro-Maior

Reunião aberta dos clãs no Fogo do Meio, com a palavra dada pela guardiã da Pedra.

**Entra (1969 caracteres):**

- Ano 611 da Contagem dos Marcos, Pasto Magro (inverno), Lua 1, dia 1.
- [city.terreiro-maior] Terreiro-Maior — O Ponto da Assembleia. Terreiro sazonal de reunião dos clãs (regime sazonal). Liderança: Yorna Palavra-Firme. Obrigação: Reunir os clãs, mediar pactos e acertar contas; o Capitão comparece sem presidir. Interesse: Decidir o rumo do reino na paz. Tensão: Mandato do Capitão de Marcha perto do fim; Assembleia dividida entre prova e aclamação. Povos: Orcano; Centauro; Lupino; Cervino. Porte: grande (relativo, sem número).
- [city.terreiro-maior] Vizinhança: Ligações (sem duração): Tabuleiro-Velho (Tabuleiro é lugar-mãe); Vazante (vínculo político).
- [npc.yorna-palavra-firme] Yorna Palavra-Firme, Orcano. Guardiã da Pedra da Palavra; cuida da ordem da Assembleia (Trato do Terreiro); guardiã do Terreiro-Maior. Limite: dá a palavra e ordena a reunião; não vota, não escolhe o Capitão e não comanda marcha. Pressão: Os dois lados da sucessão a cobram; um terreiro cheio é frágil se o Capitão estiver longe.
- [voz npc.yorna-palavra-firme] tom: severa e paciente; palavra medida, de quem ordena a reunião; modelo: "Fala quem tem a Pedra; os outros ouvem." | "Sem arma no Fogo do Meio." | "Eu dou a palavra. Voto, não."
- [fact.yorna-cuida-ordem-reuniao|publico] Cuida da ordem da reunião
- [fact.cassan-hospeda-visitantes|publico] Hospeda clãs de fora no Terreiro
- [fact.mpl-06-pedra-da-palavra|publico] a Pedra da Palavra dá a palavra na rodada; a reunião só se fecha quando todos foram ouvidos
- [fact.mpl-08-campo-a-cada-ciclo|publico] a Assembleia escolhe o campo do terreiro a cada ciclo, sempre ao alcance dos clãs em marcha
- [seed.terreiro-maior.p] A Pedra que esfria: Yorna Palavra-Firme pede que alguém anote quando a Pedra da Palavra esfria numa reunião, sem dizer que isso prova mentira.
- [rumor] contam que a Pedra da Palavra esfria quando alguém mente (rumor, não é fato).
- [regra] a sucessão do Capitão (prova ou aclamação) está aberta; ninguém a decide na cena.

**Não entra:**

- `seed.terreiro-maior.m` e `.g`: só se a cena os disparar
- `fact.yorna-costume-esquecido`, `fact.cassan-sabe-quem-apoia-quem` (restrito ou secreto) e `clock.sucessao-do-capitao` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.declaracao-no-fogo-do-meio`: só se o gatilho aparecer na cena
- resumo de `npc.cassan-galhada-velha`, `npc.kharos-casco-de-marcha`, `faction.clas-da-prova`, `faction.clas-da-aclamacao`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MPL-06 e MPL-08 (`13-misterios-limites.md`): só ao aprofundar
- as outras sete cidades, as outras 13 facções e os outros 7 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.disputa-gramal` — Disputa de pasto: Gramal

Cena de acerto entre clãs de gado e de caça sobre o mesmo campo, com o chefe do Trato dos Rebanhos.

**Entra (1640 caracteres):**

- Ano 611 da Contagem dos Marcos, Pasto Magro (inverno), Lua 1, dia 1.
- [city.gramal] Gramal — O Território dos Rebanhos. Território sazonal de pasto e caça dos clãs (regime sazonal). Liderança: Tibbe Salto-Curto. Obrigação: Dividir o pasto e marcar os rebanhos. Interesse: Pasto e caça para a estação. Tensão: Clãs de gado e de caça dizem que o mesmo campo é deles. Povos: Orcano; Leporino; Ursino; Porcino; Insetoide. Porte: média (relativo, sem número).
- [city.gramal] Vizinhança: Ligações (sem duração): Tabuleiro-Velho (Tabuleiro é lugar-mãe).
- [npc.tibbe-salto-curto] Tibbe Salto-Curto, Leporino. Guardião do Território; chefe do Trato dos Rebanhos. Limite: marca os rebanhos e divide o campo; não escolhe o campo da Marcha. Pressão: Cada lado acusa o outro de estragar o pasto; a estação não espera.
- [voz npc.tibbe-salto-curto] tom: ligeiro e conciliador; fala depressa e sorri entre as frases; modelo: "O campo se divide em paz, ou não se divide." | "Gado e caça: cada um com a sua marca." | "Precisamos do acerto antes que a estação acabe."
- [fact.tibbe-chefia-trato-rebanhos|local] Chefia o Trato dos Rebanhos
- [fact.mpl-10-gado-e-caca-mesmo-campo|local] clãs de gado e clãs de caça dizem que o mesmo campo é deles e cada lado acusa o outro de estragar o pasto
- [seed.gramal.p] O capim em círculo: Tibbe Salto-Curto pede que alguém olhe um campo onde o capim cresce em círculo, sem dizer o que isso significa.
- [rumor] contam que há um campo onde o capim cresce em círculo; que um mourão foi fincado de um lado e achado do outro (rumor, não é fato).
- [regra] quem tem razão sobre o capim (L-08) está aberto; o Mestre não decide por decreto.

**Não entra:**

- `seed.gramal.m` e `.g`: só se a cena os disparar
- `fact.tibbe-favoreceu-gado` (restrito ou secreto) e `clock.capim-disputado` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.divisao-do-campo` e `event.pasto-fraco-na-estacao`: só se o gatilho aparecer na cena
- resumo de `npc.ghorla-fartura-dividida`, `faction.clas-do-gado`, `faction.clas-da-caca`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MPL-10 (`13-misterios-limites.md`): só ao aprofundar
- as outras sete cidades, as outras 13 facções e os outros 7 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.viagem-campos` — Viagem pelos Campos: de Tabuleiro-Velho ao Gramal

Viagem narrada sem distância nem tempo, partindo do celeiro rumo ao território dos rebanhos.

**Entra (1825 caracteres):**

- Ano 611 da Contagem dos Marcos, Pasto Magro (inverno), Lua 1, dia 1.
- [city.tabuleiro-velho] Tabuleiro-Velho — O Celeiro do Planalto. Fortaleza-celeiro de planalto (regime fixo). Liderança: Moura Mel-Guardado. Obrigação: Guardar a reserva e os marcos dos pastos pactuados; abrigar clãs em ano ruim. Interesse: Manter a reserva e o muro. Tensão: Clãs de fora pedem reserva e os guardiões dizem que é de quem a juntou; ninguém concorda sobre o direito quando a migração falha. Povos: Ursino; Porcino; Cervino. Porte: média (relativo, sem número).
- [city.tabuleiro-velho] Vizinhança: Ligações (sem duração): Terreiro-Maior (Tabuleiro é lugar-mãe); Vazante (Tabuleiro é lugar-mãe); Gramal (Tabuleiro é lugar-mãe).
- [city.gramal] Gramal — O Território dos Rebanhos. Território sazonal de pasto e caça dos clãs (regime sazonal). Liderança: Tibbe Salto-Curto. Obrigação: Dividir o pasto e marcar os rebanhos. Interesse: Pasto e caça para a estação. Tensão: Clãs de gado e de caça dizem que o mesmo campo é deles. Povos: Orcano; Leporino; Ursino; Porcino; Insetoide. Porte: média (relativo, sem número).
- [rota route.tabuleiro-gramal] Tabuleiro-Velho ↔ Gramal: vínculo político; o Gramal muda de campo com a estação
- [fact.mpl-01-marco-pastos-pactuados|local] o Campo dos Marcos guarda os marcos dos pastos pactuados; os marcos são renovados quando o campo muda
- [fact.mpl-02-celeiro-guarda-reserva|publico] o celeiro guarda grão e carne seca para os anos ruins
- [seed.tabuleiro-velho.p] A câmara mais fria: Pocha Ninhada-Farta ouviu dizer que o celeiro tem uma câmara mais fria que o resto e pede que alguém olhe, sem afirmar que haja segredo.
- [rumor] dizem que há um marco no campo que ninguém sabe a que clã pertence (rumor, não é fato).
- [regra] a rota não tem duração nem distância (D-63): a viagem é narrada por cena, não por contagem.

**Não entra:**

- `seed.tabuleiro-velho.m` e `.g`: só se a cena os disparar
- `fact.moura-reserva-fora-da-conta`, `fact.pocha-sustenta-cla-recusado` (restrito ou secreto) e `clock.reserva-em-ano-ruim` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.assembleia-diz-o-que-o-celeiro-deve` e `event.pasto-fraco-na-estacao`: só se o gatilho aparecer na cena
- resumo de `npc.moura-mel-guardado`, `npc.pocha-ninhada-farta`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MPL-01 e MPL-02 (`13-misterios-limites.md`): só ao aprofundar
- as outras sete cidades, as outras 13 facções e os outros 7 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.descida-praia-pardo` — Chegada da Descida, só do lado das Marcas: Praia-Pardo

Visitantes chegam ao pouso pelo Argente; o pouso recebe, guarda e acerta a carga.

**Entra (1784 caracteres):**

- Ano 611 da Contagem dos Marcos, Pasto Magro (inverno), Lua 1, dia 1.
- [city.praia-pardo] Praia-Pardo — O Pouso da Descida. Pouso fluvial onde o Argente entra nas planícies (regime fixo). Liderança: Vrissk Colônia-Quieta. Obrigação: Receber, guardar e acertar a carga de quem chega pelo rio. Interesse: Manter o pouso e o acerto de carga. Tensão: Clãs discordam sobre quem pode cobrar o desembarque; carga chega com papel que o Vale não conhece. Povos: Goblin; Insetoide. Porte: pequena (relativo, sem número).
- [city.praia-pardo] Vizinhança: Ligações (sem duração): Rasovau (fluxo do rio); rio acima (pelo Argente) (só do lado do Vale).
- [npc.vrissk-colonia-quieta] Vrissk Colônia-Quieta, Insetoide. Guardião do Pouso; chefe do Trato do Desembarque. Limite: recebe, guarda e acerta; não pergunta de onde vem a carga nem decide por que desce (contrato §5, item 5). Pressão: Os clãs discordam sobre quem cobra; a carga chega com papel que o Vale não conhece.
- [voz npc.vrissk-colonia-quieta] tom: quieto e minucioso; fala devagar, em conta e registro; modelo: "O pouso está em ordem. Anote o que traz." | "Receber e acertar a carga é o meu Trato." | "Quem cobra o desembarque o pacto diz; eu aplico."
- [fact.vrissk-chefia-trato-desembarque|local] Chefia o Trato do Desembarque
- [fact.mpl-04-carga-com-papel|local] o pouso recebe carga com papel que o Vale não reconhece
- [seed.praia-pardo.p] Quem desce sem ter subido: Vrissk Colônia-Quieta pede que alguém anote quem desce na rampa e o que diz sobre a chegada, sem tomar o rumor por fato.
- [rumor] dizem que alguns visitantes de passagem descem sem nunca terem subido; que a rampa tem um degrau a mais a cada estação (rumor, não é fato).
- [regra] só o lado do Vale: causa, origem, número e fim da Descida são desconhecidos e ficam fora da cena.

**Não entra:**

- `seed.praia-pardo.m` e `.g`: só se a cena os disparar
- `fact.vrissk-recusou-carga` (restrito ou secreto) e `clock.desembarque` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.regra-da-rampa`: só se o gatilho aparecer na cena
- resumo de `npc.grebb-balanca-justa`, `faction.receptores-do-pouso`, `faction.balanca-do-pouso`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MPL-04 (`13-misterios-limites.md`): só ao aprofundar
- as outras sete cidades, as outras 13 facções e os outros 7 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.travessia-elevacoes` — Travessia das elevações ocidentais, só em tom de rumor: Encosta-Aberta

Subida guiada até o pé das elevações e conversa com o guia sobre o que se diz do outro lado.

**Entra (2140 caracteres):**

- Ano 611 da Contagem dos Marcos, Pasto Magro (inverno), Lua 1, dia 1.
- [city.encosta-aberta] Encosta-Aberta — O Pouso das Elevações. Pouso sazonal de vigia no pé das elevações ocidentais (regime sazonal). Liderança: desconhecida. Obrigação: Vigiar o pé das elevações e guiar quem sobe. Interesse: Manter a vigia e a pastagem de espera. Tensão: Dois clãs disputam quem guia o rebanho e quem fica com o que sobra no pouso; o Trato está em disputa (doc 04 §8). Povos: Felídeo Errante; Centauro; Cervino. Porte: pequena (relativo, sem número).
- [city.encosta-aberta] Vizinhança: Ligações (sem duração): Rasovau (só apoio; lugar-mãe Rasovau; sem trilha documentada); elevações ocidentais (o Vale não vai além do pé das elevações).
- [npc.tessun-salto-calado] Tessun Salto-Calado, Felídeo Errante. Guia das trilhas até o pé das elevações; herói de Encosta-Aberta (levou um rebanho inteiro até o pouso numa estação ruim, sem perder um animal). Limite: guia até o pé das elevações; o que há do outro lado é rumor e não é dele (contrato §5, item 9). Pressão: Dois clãs disputam quem guia; os que ficam acham que quem sobe volta com o rumo trocado.
- [voz npc.tessun-salto-calado] tom: calado e leve; fala baixo, como quem ainda ouve a trilha; modelo: "A trilha fica aberta enquanto o guia passar." | "Levo o rebanho até o pouso. Dali, cada um com o seu." | "Do outro lado? Só o que se ouve."
- [fact.tessun-guia-rebanhos-ao-pouso|local] Guia rebanhos até o pouso
- [fact.mpl-09-guia-ate-o-pe|publico] o guia leva o rebanho até o pé das elevações e não além
- [rota route.encosta-aberta-elevacoes] Encosta-Aberta ↔ elevações ocidentais: o Vale não vai além do pé das elevações (L-09)
- [seed.encosta-aberta.p] O rumo trocado: Tessun Salto-Calado pede que alguém acompanhe um guia de volta ao pouso e anote o que ele diz do que ouviu lá em cima, sem tomar o rumor por fato.
- [rumor] a encosta fala de uma terra de árvores muito altas do outro lado; há quem suba e volte com o rumo trocado (rumor, não é fato; sem nome, sem rota e sem ligação a nada do Vale).
- [regra] o guia vai até o pé das elevações e não além (L-09); o que há do outro lado é rumor e não é do Vale.

**Não entra:**

- `seed.encosta-aberta.m` e `.g`: só se a cena os disparar
- `fact.tessun-ouviu-do-outro-lado` (restrito ou secreto) e `clock.guia-das-trilhas` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.guia-combinado`: só se o gatilho aparecer na cena
- resumo de `npc.sendro-clareira-vigia`, `npc.cadrek-cerca-aberta`, `faction.guias-da-encosta`, `faction.vigias-das-trilhas`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MPL-09 (`13-misterios-limites.md`): só ao aprofundar
- nenhuma resposta sobre o rumor; nenhum dado de outro reino: o contrato de fronteira só permite o rumor acima
- as outras sete cidades, as outras 13 facções e os outros 7 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## Conferências

| Verificação | Resultado |
|---|---|
| Cenas | 7 (a a g do roadmap) |
| Maior "entra" | 2140 caracteres (limite ~2.400) |
| Fato restrito ou secreto (marcador) em "entra" | 0 |
| Relógio em "entra" | 0 |
| Frase `nunca_diz` copiada em "entra" | 0 |
| Cena com lista "não entra" | 7 de 7 |
| NPC falecido, rota com duração ou termo de outro reino em "entra" | 0 |

## Decisão pendente

**D-131 (aceita pela continuação):** aprovar as sete cenas e as escolhas (e) e (f). Opções: **A.** aprovar; **B.** trocar uma cena ou o lugar de teste (diga qual). **Recomendação: A.**
