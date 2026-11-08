# 15 — Cenas de teste (R3-6.3)

**Estado:** rascunho de R3-6.3 para aprovação do autor (D-94) · **Data:** 2026-11-06

**Etiqueta:** tudo é **[PROPOSTO]**; as cenas são testes de contexto, não texto de lore. **Fonte:** `14-resumos-de-contexto` (R3-6.1), `14-camadas-de-informacao` (R3-6.2), `13-sementes-de-aventura`, `12-voz.csv`, doc 04 §3, doc 07 e doc 01 parte 5 (calendário). Dados em `15-cenas-de-teste.csv`. Molde: `reinos/02-.../15-cenas-de-teste.md`.

## Regras

- Cada cena tem a lista **entra** (texto exato que vai ao contexto da IA, no máximo ~2.400 caracteres) e a lista **não entra** (o que fica fora e quando entraria).
- Só entram fatos de camada **público** ou **local**; fato restrito ou secreto só entra depois de cumprido o caminho de descoberta (R3-6.2). Relógios nunca entram.
- O texto de "entra" é montado dos resumos, fatos, sementes, vozes e linhas de rota já escritos; nada foi acrescentado. A data é a abertura do jogo: Névoa Fechada, Lua 1, dia 1 do ano 389 da Contagem das Provações (doc 01 parte 5).
- **Adaptação ao reino sem assentamentos (D-66):** o "vilarejo de charco" do roadmap é **Lamaluz**, cidade de charco sobre estacas (ASSUMINDO); a "cidade de neblina" é Morrovéu; a rota é o Ermo de Sal (o Argente não é testado aqui).
- Nenhuma cena traz NPC com `vivo = não`, rota com duração, ou afirmação sobre Aeltharyn. Na cena da orla norte, a única fala sobre o além é o rumor da "floresta de névoa", marcado como rumor e não fato; a menção a Aeltharyn nos resumos é só o veto do reino.
- A contagem de caracteres inclui os rótulos `[id]` e as quebras entre linhas.

## `cena.charco-lamaluz` — Cidade de charco: Lamaluz

Chegada a uma cidade sobre estacas e passarelas e conversa com o Piloto-Mor, sem carregar o reino.

**Entra (1723 caracteres):**

- [data] Ano 389 da Contagem das Provações, Névoa Fechada (inverno), Lua 1, dia 1.
- [city.lamaluz] Cidade de charco sobre estacas e passarelas. Governo: Pactuários dos Canais; à frente, Nuvik Canal-Torto. Vive de juncos, peixe de lama e pilotagem. Tensão: Famílias antigas e lagartos de charco querem partir (Fugas do Véu); quem guia os que partem é acusado de quebrar o pacto. Povos: Escamado; Ratoide; Moldável; Escamado Miúdo e Pequenino. Porte: média (relativo, sem número).
- [city.lamaluz] Vizinhança: Ligações (sem duração): Morrovéu (assento no Conselho); Fozbreu (Pilotos dos Canais; Fugas do Véu partem de Lamaluz).
- [npc.tazk-pedra-ao-sol] Tazk Pedra-ao-Sol, Escamado. Piloto-Mor de Lamaluz; herói vivo (tríade); líder dos Pilotos dos Canais (F-04). Navegador; conhece os canais mutáveis; restrição: guia, não faz nem desfaz pacto e não manda nos Pactuários dos Canais. Agenda: Manter os canais navegáveis e decidir se guia ou não as famílias que partem.
- [voz npc.tazk-pedra-ao-sol] tom: paciente e prático; fala de água e de rota; modelo: "Os canais mudam. Quem os conhece, mantém a passagem aberta." | "Guio quem pede. O pacto é quem julga, não eu." | "Pergunte ao fundo do canal antes de perguntar a mim."
- [fact.tazk-conhece-canais|local] Conhece os canais mutáveis de Lamaluz
- [fact.canais-e-partida|publico] os canais mudam de lugar com a cheia; famílias querem partir e o pacto manda que fiquem
- [seed.lamaluz.p] A lamparina sem dono: Tazk Pedra-ao-Sol ouve de novo o rumor de lamparinas que ninguém acendeu e pede que alguém veja se alguma está acesa no Cais das Lamparinas, sem afirmar que haja.
- [regra] A pena de quem guia os que partem e o alcance de L-09 fora de Lamaluz são desconhecidos; ficam com o Mestre.

**Não entra:**

- `seed.lamaluz.m` e `.g`: só se a cena os disparar
- `fact.tazk-guiou-quem-partia` (secreto) e `clock.partida` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.julgamento-de-quem-guia` e `event.cheia-fora-de-epoca`: só se o gatilho aparecer na cena
- resumo de `npc.nuvik-canal-torto` e das facções `faction.pilotos-dos-canais` e `faction.guardioes-das-lamparinas`: só se aparecerem
- as outras sete cidades, as outras dez facções e os outros 7 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.chegada-morroveu` — Chegada a uma cidade de neblina: Morrovéu

Chegada ao morro entre charcos e neblina e primeiro contato com a guarda da Corte.

**Entra (1791 caracteres):**

- [data] Ano 389 da Contagem das Provações, Névoa Fechada (inverno), Lua 1, dia 1.
- [city.morroveu] Cidade-fortaleza num morro entre charcos. Governo: a Corte, com o Conselho dos Pactuários (um assento por lugar); à frente, Morvan Voz-Baixa. Vive de tributo em pactos, escribas, armas e hospedagem de desafiantes. Tensão: Pactuários divididos entre defender o rei, que trouxe paz, e lembrar que a regra é o rei ser vencido; lista de desafiantes parada há cerca de cem anos. Povos: Nascido do Crepúsculo; Umbral; Ursino Sombrio; Humano (forasteiro). Porte: grande (relativo, sem número).
- [city.morroveu] Vizinhança: Ligações (sem duração): assento de cada um dos sete outros lugares no Conselho; Selovale (Memória das Provações); Fozbreu (Defensores da Paz). Tensão, não rota: Candeia-Morta.
- [npc.orsk-cicatriz-de-bando] Orsk Cicatriz-de-Bando, Ursino Sombrio. General da Guarda de Morrovéu; líder da Guarda do Rei (F-01). Comandante de escolta profissional; comanda a guarda de Morrovéu; restrição: não comanda as vigias dos pactos dos outros lugares. Agenda: Manter a guarda unida e disciplinada enquanto a pergunta sobre o rei não é feita em voz alta.
- [voz npc.orsk-cicatriz-de-bando] tom: disciplinado e silencioso; frases curtas de comando; modelo: "A guarda serve ao rei. Nesta cidade, isso basta." | "Meu posto acaba nos muros de Morrovéu." | "Quem entra, entra por registro."
- [fact.orsk-comanda-guarda|publico] Comanda a guarda que serve ao rei
- [fact.lista-nao-anda|publico] a lista de desafiantes não anda há cerca de cem anos
- [rumor] dizem que o rei não dorme (rumor, não é fato).
- [seed.morroveu.p] A inscrição que não chegou: Cerrane Herança-Firme pede que alguém confira no Arquivo dos Reis que Passaram se há inscrição de desafiante que nunca chegou à lista, sem afirmar que exista.

**Não entra:**

- `npc.morvan-voz-baixa` e `npc.cerrane-heranca-firme` (resumos): só se a cena os trouxer
- `fact.orsk-guarda-sem-rei` e `fact.morvan-custo-estabilizacao` (secretos) e `clock.desafiantes` (secreto): só depois de cumprido o caminho de descoberta
- `seed.morroveu.m` e `.g`, e `event.conselho-vota-em-publico`: só se disparados
- hipóteses e "a verdade não pode ser" de MV-01 e MV-02 (`13-misterios-limites.md`): só ao aprofundar
- as outras sete cidades, as outras dez facções e os outros 7 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.audiencia-conselho` — Audiência de pacto: Conselho dos Pactuários

Pedido público de um lugar ao Conselho, presidido por Cerrane, que não vota.

**Entra (1901 caracteres):**

- [data] Ano 389 da Contagem das Provações, Névoa Fechada (inverno), Lua 1, dia 1.
- [city.morroveu] Cidade-fortaleza num morro entre charcos. Governo: a Corte, com o Conselho dos Pactuários (um assento por lugar); à frente, Morvan Voz-Baixa. Vive de tributo em pactos, escribas, armas e hospedagem de desafiantes. Tensão: Pactuários divididos entre defender o rei, que trouxe paz, e lembrar que a regra é o rei ser vencido; lista de desafiantes parada há cerca de cem anos. Povos: Nascido do Crepúsculo; Umbral; Ursino Sombrio; Humano (forasteiro). Porte: grande (relativo, sem número).
- [city.morroveu] Vizinhança: Ligações (sem duração): assento de cada um dos sete outros lugares no Conselho; Selovale (Memória das Provações); Fozbreu (Defensores da Paz). Tensão, não rota: Candeia-Morta.
- [npc.cerrane-heranca-firme] Cerrane Herança-Firme, Nascido do Crepúsculo. Presidente do Conselho dos Pactuários (preside e não vota); benfeitora de Morrovéu (tríade). Mediadora; convoca e preside; restrição: não vota por lugar nenhum, não desempata e não desafia o rei (doc 04 §2.2). Agenda: Manter um assento por lugar e impedir que o Conselho se parta entre defensores e críticos do rei.
- [voz npc.cerrane-heranca-firme] tom: conciliadora e cautelosa; escolhe cada palavra; modelo: "Eu convoco e presido. Voto, não." | "Cada lugar tem o seu assento; nenhum vale mais que outro." | "Que isso fique registrado como o Conselho decidir."
- [fact.cerrane-preside-conselho|publico] Preside o Conselho dos Pactuários
- [fact.lista-nao-anda|publico] a lista de desafiantes não anda há cerca de cem anos
- [regra] Conselho dos Pactuários: vota questões comuns, chama o rei a responder e registra desafiantes; não depõe o rei, pois só a Provação depõe (doc 04 §3).
- [seed.morroveu.m] Dois lados do Conselho: Pactuários tomam partido em público e Cerrane quer um relato dos dois lados antes de convocar a sessão, sem que ela vote.

**Não entra:**

- `fact.cerrane-clausula-nao-lida` (secreto) e `clock.desafiantes` (secreto): só depois de cumprido o caminho de descoberta
- `event.conselho-vota-em-publico`: só se o gatilho aparecer na cena
- `seed.morroveu.g`: só se disparada
- resumo de `npc.morvan-voz-baixa` e `npc.orsk-cicatriz-de-bando`: só se aparecerem
- hipóteses e vetos de MV-01 (`13-misterios-limites.md`): só ao aprofundar; nenhum resultado de voto é decidido aqui
- as outras sete cidades, as outras dez facções e os outros 7 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.arquivo-selovale` — Investigação em arquivo: Selovale

Busca no Arquivo dos Pactos Velhos, com a presidente do Conselho dos Selos, sem decidir qual versão do texto vale.

**Entra (1643 caracteres):**

- [data] Ano 389 da Contagem das Provações, Névoa Fechada (inverno), Lua 1, dia 1.
- [city.selovale] Vale de pactos antigos, com cidade-arquivo. Governo: Conselho dos Selos; à frente, Yeshe Forma-Emprestada. Vive de arquivo, juramentos, cópia de selos e parecer. Tensão: Um pacto antigo vence e ninguém sabe o que o fim dele concede; discordância sobre qual versão do texto vale. Povos: Nascido do Crepúsculo; Moldável; Ursino Sombrio; Draconato, Feérico e Humano. Porte: pequena (relativo, sem número).
- [city.selovale] Vizinhança: Ligações (sem duração): Morrovéu (assento; Memória das Provações); fica ao norte da capital.
- [npc.yeshe-forma-emprestada] Yeshe Forma-Emprestada, Moldável. Presidente do Conselho dos Selos (assento de Selovale); guardiã do Arquivo dos Pactos Velhos; líder da Memória das Provações (F-03). Cronista e arquivista; interpreta e guarda; restrição: não rompe selo nem decide por Morrovéu. Agenda: Descobrir o que o pacto antigo que vence concede antes que alguém invoque a cláusula.
- [voz npc.yeshe-forma-emprestada] tom: erudita e adaptável; cita o texto; modelo: "O texto diz o que diz, e a versão importa." | "Guardo o arquivo. Selo, eu não rompo." | "Não decido por Morrovéu; leio e registro."
- [fact.yeshe-preside-selos|local] Preside o Conselho dos Selos; guarda o Arquivo dos Pactos Velhos
- [fact.pacto-antigo-vence|local] um pacto antigo vence e ninguém sabe o que o fim concede
- [seed.selovale.m] A cópia mais antiga: Yeshe pede que alguém ache a cópia mais antiga de um texto contestado entre os Pactuários.
- [regra] O Conselho dos Selos interpreta cláusulas antigas e dá parecer; não executa o parecer (doc 04 §3).

**Não entra:**

- `fact.yeshe-clausula-lida` (restrito): só com autorização do Arquivo ou favor de Yeshe
- `clock.pacto-que-vence` (secreto, uso de Mestre e motor) e `event.sessao-publica-de-parecer`: só se o gatilho aparecer
- `seed.selovale.p` e `.g`: só se disparadas
- hipóteses e "a verdade não pode ser" de MV-06 (`13-misterios-limites.md`): só ao aprofundar
- resumo de `faction.memoria-das-provacoes`: só se aparecer
- as outras sete cidades, as outras dez facções e os outros 7 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.rota-ermo-de-sal` — Viagem por rota: Ermo de Sal

Escolta de uma caravana saindo de Beira-Sal pela Rota do Sal, com acerto oral no Pátio, só do lado do Véu.

**Entra (1614 caracteres):**

- [data] Ano 389 da Contagem das Provações, Névoa Fechada (inverno), Lua 1, dia 1.
- [city.beira-sal] Posto de caravanas na borda do Ermo de Sal. Governo: Pactuários dos Poços; à frente, Teshi Nó-de-Poço. Vive de sal, água, guias e acertos. Tensão: Guias cobram por pactos orais sem prova; caravanas do outro lado contestam a conta. Povos: Ratoide; Metamorfo Lupino; Aracnídeo; Goblin, Vulpino e Humano. Porte: pequena (relativo, sem número).
- [city.beira-sal] Vizinhança: Ligações (sem duração): Morrovéu (assento no Conselho); Ermo de Sal pela Rota do Sal.
- [npc.vurga-sede-vencida] Vurga Sede-Vencida, Metamorfo Lupino. Guia-Mestra dos Guias de Sal; herói vivo (tríade); líder dos Guias de Sal (F-06). Guia de deserto; conduz caravanas pelo Ermo de Sal; restrição: guia e testemunha acertos, não faz pacto pelo posto nem fala pelas Marcas das Feras. Agenda: Manter a Rota do Sal viva e os acertos dos guias honestos.
- [voz npc.vurga-sede-vencida] tom: resistente e direta; sem enfeite; modelo: "A Rota do Sal vive de acerto honesto." | "Guio a caravana. Quem paga, paga o combinado." | "Testemunho o que vi. O resto, não."
- [fact.vurga-conduz-caravanas|local] Conduz caravanas pelo Ermo
- [fact.pactos-orais|local] guias cobram por pactos orais que ninguém prova
- [seed.beira-sal.m] Um acerto sem prova: Vurga pede escolta para uma caravana e alguém que testemunhe um acerto oral no Pátio dos Acertos.
- [rota] `route.beira-sal-ermo`: Beira-Sal e o Ermo de Sal pela Rota do Sal, com guias e acertos; duração desconhecida; contato aberto com as Marcas das Feras, nada afirmado sobre elas; não é rota até outro reino (doc 07).

**Não entra:**

- `fact.vurga-guia-sem-sede` (restrito): só com favor ou testemunho dos guias
- `fact.teshi-agua-escondida` (secreto) e `clock.acertos-orais` (secreto): só depois de cumprido o caminho de descoberta
- `seed.beira-sal.p` e `.g`, e `event.acertos-orais-suspensos`: só se disparados
- nada sobre as Marcas das Feras, o Pacto Humano ou o outro lado do Ermo
- resumo de `npc.teshi-no-de-poco`: só se aparecer
- as outras sete cidades, as outras dez facções e os outros 7 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.orla-norte-rumor` — Travessia da orla norte: Borda-Mansa (só rumor)

Rondas na orla de Borda-Mansa com o Vigia-Mor, em que a névoa só mostra o que quer; qualquer fala sobre o além é só rumor.

**Entra (1631 caracteres):**

- [data] Ano 389 da Contagem das Provações, Névoa Fechada (inverno), Lua 1, dia 1.
- [city.borda-mansa] Posto de orla com vigia de neblina. Governo: Pactuários da Vigia; à frente, Krell Eco-da-Vigia. Vive de vigia, sinais e couro de charco. Tensão: Gente de longe chega sem guia e sem registro; o posto não sabe quem é quem. Povos: Corvídeo; Umbral; Aracnídeo; Micélioide, Felídeo Errante e Corvário. Porte: pequena (relativo, sem número).
- [city.borda-mansa] Vizinhança: Ligações (sem duração): Morrovéu (assento no Conselho); orla norte do reino; nada se liga a Aeltharyn.
- [npc.krell-eco-da-vigia] Krell Eco-da-Vigia, Corvídeo. Vigia-Mor de Borda-Mansa, Pactuário da Vigia (assento); guardião do Pacto da Orla; líder dos Vigias da Orla (F-08). Sentinela e observador do clima; comanda a Torre das Fogueiras; restrição: vigia e abriga; só diz rumor sobre Aeltharyn e não liga o posto a Brumafunda (C, contrato de fronteira). Agenda: Abrigar quem chega e saber quem é quem sem fechar o posto.
- [voz npc.krell-eco-da-vigia] tom: atento e econômico em palavras; modelo: "A torre vigia e abriga. É o que o posto faz." | "Quem chega, eu registro. Quem não registra, eu observo." | "Do que há além da neblina, só ouvi rumor."
- [fact.krell-comanda-torre|local] Comanda a Torre das Fogueiras
- [fact.chegadas-sem-guia|local] gente de longe aparece sem guia e sem registro
- [rumor] a orla fala de uma "floresta de névoa" mais ao norte, sem nome e sem história; só rumor, sem causa e sem lugar (não é fato).
- [seed.borda-mansa.p] Uma noite de chegadas: Krell Eco-da-Vigia pede que alguém vigie uma noite de chegadas na Cerca Mansa e registre quem vem.

**Não entra:**

- `fact.krell-ajuda-de-dentro` (secreto) e `clock.orla-sem-guia` (secreto): só depois de cumprido o caminho de descoberta
- `event.audiencia-sobre-registro` e `event.neblina-cobre-a-orla`: só se o gatilho aparecer
- `seed.borda-mansa.m` e `.g`: só se disparadas
- hipóteses e "a verdade não pode ser" de MV-07 (`13-misterios-limites.md`): só ao aprofundar; nenhuma resposta sobre o rumor
- nenhum dado de Aeltharyn: o contrato de fronteira só permite o rumor acima
- as outras sete cidades, as outras dez facções e os outros 7 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## Conferências

| Verificação | Resultado |
|---|---|
| Cenas | 6 (a a f do roadmap) |
| Maior "entra" | 1901 caracteres (limite ~2.400) |
| Fato restrito ou secreto (marcador) em "entra" | 0 |
| Relógio em "entra" | 0 |
| Frase `nunca_diz` copiada em "entra" | 0 |
| Cena com lista "não entra" | 6 de 6 |
| NPC falecido, rota com duração ou fato de Aeltharyn em "entra" | 0 |

## Decisão pendente

**D-94:** aprovar as seis cenas e a adaptação do vilarejo a Lamaluz. Opções: **A.** aprovar; **B.** trocar uma cena ou o lugar de teste (diga qual). **Recomendação: A.**
