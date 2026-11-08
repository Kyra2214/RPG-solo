# Decisões do autor (M0.1)

**Estado:** D-17 e D-21 abertas (nomes das luas; formato dos dados); D-01 a D-16 decididas em 25/10/2026 (ver "DECIDIDO" em cada item); falta aplicar no M1.4. Cada item traz as opções, o impacto medido e uma **recomendação**; a decisão é do autor. Depois de decidido, registrar data e escolha e aplicar no M1.4.

## D-01 — Povo-corvo: "Corvídeos" ou "Corvários"?

O catálogo tem **duas** raças diferentes:

| ID | Nome de exibição | Sub-raças | Resumo no catálogo |
|---|---|---|---|
| `kenku` | **Corvídeo** | `corvideo-dos-telhados`, `corvideo-das-ruinas` | preservam acontecimentos por vozes, sons, gestos e imitação |
| `homem-corvo` | **Corvário** | `homem-corvo-das-torres` | colecionam relatos de presságios e mantêm arquivos de acontecimentos esquecidos |

Aeltharyn aponta para `homem-corvo` / `homem-corvo-das-torres` (Caladwen, torres, mensageria, registros), mas o texto fala em "Corvídeos".

| Opção | O que muda | Prós | Contras |
|---|---|---|---|
| **A. Usar "Corvários"** | corrigir ~4 menções "Corvídeo" nos docs 01 e 10 | os IDs já usados ficam; a linhagem "das torres" combina com Caladwen; o catálogo técnico já diz Corvário | o nome "Corvídeo" some do reino |
| B. Usar `kenku` (Corvídeos) | trocar IDs e linhagem; perde "das torres" (as sub-raças kenku são "dos telhados" e "das ruínas") | nome familiar | quebra o catálogo técnico e o doc 10 |
| C. Ter os dois povos | Corvários nas torres + Corvídeos (telhados/ruínas) nas cidades | mais variedade | dobra a contagem de povos nativos (11 vira 12) e exige nova decisão de cidades |

**DECIDIDO em 25/10/2026: A (Corvários).** Texto a corrigir no M1.4: as menções de "Corvídeo" nos docs 01 e 10.

**Recomendação: A.** Impacto medido: 4 menções de "Corvídeo" e 4 de "Corvário" nos documentos (já misturados); o catálogo técnico usa só "Corvário".

## D-02 — Nome do mundo × nome do reino

O documento do autor nomeia o **mundo** "Aeltharyn — Crônicas do Primeiro Verde", mas Aeltharyn também é o **reino** das florestas. Isso vai confundir quando existirem 5 reinos.

| Opção | Efeito |
|---|---|
| **A. O mundo ganha nome próprio; Aeltharyn fica só o reino** | claro para 5 reinos; exige escolher um nome de mundo |
| B. Aeltharyn é o mundo; o reino das florestas ganha outro nome | exige renomear o reino e reescrever referências |
| C. Manter os dois | ambíguo |

**DECIDIDO em 25/10/2026: A.** Nome do mundo escolhido: **Mythralis**. Texto a ajustar no M1.4: onde o documento do autor chama o mundo de "Aeltharyn — Crônicas do Primeiro Verde".

**Recomendação: A.** Posso propor nomes de mundo se você pedir.

## D-03 — Nomes de exibição das raças

Padronizar como o texto cita as raças que no catálogo têm nome diferente do ID: `satiro` = Silvano Festivo, `tabaxi` = Felídeo Errante (sub-raça `felideo-das-selvas` = Felídeo das Selvas), `plantae` = Verdejante, `fungal` = Micélioide, `tritao` = Abissal Costeiro.

**DECIDIDO em 25/10/2026: A.** Aplicar no M1.4 com tabela de sinônimos.

**Recomendação:** usar o **nome do catálogo** como padrão; o apelido do texto ("sátiros") pode aparecer só como sinônimo registrado numa tabela.

## D-04 — Conflitos de ID (mesma pessoa, IDs diferentes) — 7, não 2

| Pessoa | ID nos documentos | ID no catálogo técnico |
|---|---|---|
| Saelith Arco-de-Chuva | `npc.saelith-arco-de-chuva` | `npc.saelith-arco-chuva` |
| Dara Guarda-da-Rota | `npc.dara-guarda-da-rota` | `npc.dara-guarda-rota` |
| Toma Mel-de-Âmbar | `npc.toma-mel-de-ambar` | `npc.toma-mel-ambar` |
| Sela dos Retornos | `npc.sela-dos-retornos` | `npc.sela-retornos` |
| Nima Raiz-de-Lodo | `npc.nima-raiz-de-lodo` | `npc.nima-raiz-lodo` |
| Miri Corda-de-Lua | `npc.miri-corda-de-lua` | `npc.miri-corda-lua` |
| Yssara Folha-de-Sal | `npc.yssara-folha-de-sal` | `npc.yssara-folha-sal` |

*Correção de 25/10/2026: a v1 do registro listava só os dois primeiros; cinco pares estavam contados como duas pessoas.*

**DECIDIDO em 25/10/2026: A (ID dos documentos).**

**Recomendação:** adotar o ID **dos documentos** (a fonte editorial, mais legível) e corrigir o catálogo na integração. Nenhum ID é reutilizado para outra pessoa.

## D-05 — IDs das 32 pessoas que só têm ID no catálogo (32 após a correção)

**DECIDIDO em 25/10/2026: A (ID do catálogo escrito nos docs).**

O texto descreve essas pessoas (sobretudo na seção 19 do doc 01) sem escrever o ID. **Recomendação:** adotar o ID do catálogo e escrevê-lo nos documentos (M1.4). Lista na seção 5.4 do registro mestre.

## D-06 — Aprovar o inventário (M0.2)

Contagens que passam a valer como referência do reino 1:

| Item | Valor |
|---|---:|
| Cidades | 10 |
| Assentamentos | 20 |
| Facções | 10 |
| Povos e linhagens (catálogo) | 16 (11 nativos de referência) |
| Pessoas distintas | 94 |
| Rotas com duração no catálogo | 14 |
| Relógios iniciais | 7 |

**DECIDIDO em 25/10/2026: A (inventário aprovado).**

**Recomendação:** aprovar, ciente de que as pessoas podem crescer (é o piso, não um teto).

## D-07 — Vessa Fio-Dourado: Arven ou Silvaran?

O catálogo técnico põe `npc.vessa-fio-dourado` em `city.arven` (benfeitora de silvano-dos-festivais). Os documentos 01 e 03 a põem como liderança de Máscara Nova (`settlement.mascara-nova`), vinculado a Silvaran.

| Opção | Efeito |
|---|---|
| **A. Silvaran** (corrigir a cidade no catálogo) | coerente com o povo (silvano-dos-festivais) e com Máscara Nova; Arven perde uma benfeitora e precisa de outra |
| B. Arven | exige mudar a liderança de Máscara Nova nos docs 01, 03 e 09 |
| C. Duas pessoas | exige novo nome e novo ID para uma delas |

**DECIDIDO em 25/10/2026: A (Silvaran).** Consequência: Arven fica sem benfeitora na tríade e precisa de uma nova (decisão futura, M3.1).

**Recomendação: A**, a confirmar pelo autor (não é correção automática: muda a tríade de Arven).

## D-08 — Vaunor Eir sem ID (achado do M1.2)

Vaunor Eir, representante das casas de manejo florestal no Conselho das Sete Copas (doc 01, seção 4.3), é a única pessoa do conselho citada sem ID.

| Opção | Efeito |
|---|---|
| **A. Registrar `npc.vaunor-eir` (recomendado)** | entra no registro mestre como "só nos docs"; pessoas passam de 94 para 95 |
| B. Manter só como menção | não vira NPC rastreável; não pode ter agenda nem relações registradas |

**DECIDIDO em 25/10/2026: A.** `npc.vaunor-eir` registrado; pessoas distintas: 95 (o piso aprovado em D-06 sobe de 94 para 95).

## D-09 — 13 organizações locais sem ID (achado do M1.5)

Listadas em `RELATORIO_M1_5.md`. Opções: **A.** são unidades locais das 10 facções (ex.: Vigias do Bosque, do Brejo e da Costa = Vigias do Primeiro Verde; Irmandade dos Barqueiros = Irmandade das Águas) e viram sinônimo ou capítulo local; **B.** ganham ID `faction.*` próprio (passam de 10 para até 23); **C.** ficam só como texto, sem ID. **DECIDIDO em 25/10/2026: A.** As 13 viram unidades locais das 10 facções (seção 10 do registro mestre). Onde o nome indica a facção-mãe, ela foi registrada; nas 9 sem indício no texto a facção-mãe fica **a definir** (lacuna explícita, nada deduzido).

## D-10 — Irmandade das Águas × Irmandade dos Barqueiros (Nymvar)

Opções: **A.** são a mesma e Nymvar passa a listar a Irmandade das Águas; **B.** são distintas e a dos Barqueiros ganha ID. **DECIDIDO em 25/10/2026: A.** Nymvar passa a listar a Irmandade das Águas (doc 01, seção 5.2); "Irmandade dos Barqueiros" fica como unidade local/sinônimo.

## D-11 — Cargos acumulados

Dauren (Torvelume), Tessa (Arven), Lyris (Silvaran), Elsin (Nymvar) e Neralis (Brumafunda) chefiam cidade e facção. Opções: **A.** intencional (registrar no fechamento); **B.** separar os cargos. **DECIDIDO em 25/10/2026: A.** Cargos acumulados intencionais; registrados no doc 11.

## D-12 — Nymvar–Arven: 1 dia ou 2–4 dias? (achado do M2.1)

O doc 01 (seção 6) dá 1 dia por estrada e canais; a Rota dos Celeiros (seção 25.1) diz que caravanas levam 2–4 dias "entre cidades". **Provisório aplicado:** 1 dia para Nymvar–Arven (ligação individual) e 2–4 dias por caravana para Arven–Silvaran (substituído pela decisão abaixo). Opções: **A.** manter assim; **B.** Nymvar–Arven também passa a 2–4 dias por caravana (1 dia fica para viajante leve). **DECIDIDO em 25/10/2026: B.** Nymvar–Arven: 2–4 dias por caravana; 1 dia só para viajante leve.

## D-13 — Níveis provisórios de produção dos recursos centrais (M2.3)

O doc 08 não tinha magnitude para grãos, madeira, resina e minério. **DECIDIDO em 25/10/2026:** propor níveis ordinais 0–5 como *Estimativa provisória* só para os 7 produtores documentados (doc 08, seção 12.0), incerteza ±1, substituíveis por fato canônico. Reserva segue desconhecida.

## D-14 — Estação inicial do ano 742 (M2.4)

**DECIDIDO em 25/10/2026:** o ano 742 abre em **Silêncio (inverno)**, Lua 1, dia 1.

## D-15 — Dias Sem Raiz (M2.4)

**DECIDIDO em 25/10/2026:** um em cada virada de estação e o 5º no fim do ano (dias 91, 182, 273, 364 e 365).

## D-16 — Estrutura do ano (M2.4)

Derivada de D-14 e D-15: 4 estações de 3 luas, ano iniciado em Silêncio. ASSUMINDO: o 4º Dia Sem Raiz coincide com a virada Queda→Silêncio, e o 5º vem logo depois.

## D-17 — Nomes das 12 luas

O autor pediu propostas. As do doc 01, seção 35.1, são provisórias. **Aguardando aprovação.**

## D-18 — Edran Sem-Selo sem ID (achado do M3.1)

Protetor cívico vivo de Elarion (doc 01, seção 19.1): pequenino, 51 anos, sem `npc.*` e fora das 95 pessoas. Opções: **A.** registrar `npc.edran-sem-selo` (pessoas 95 para 96, ficha em Elarion); **B.** manter só como menção. **Aguardando o autor.**

## D-19 — Cidade do rei e da princesa (achado do M3.1)

`npc.aerendyl-vaelorian` e `npc.maereth-vaelorian` têm cidade em branco no registro; o texto não diz onde residem. Opções: **A.** vincular a `city.elarion` (capital, sem texto que diga residência); **B.** manter sem cidade (cargo de reino). **Aguardando o autor.** Sem decisão, ficam fora das fichas de Elarion.

## D-20 — Povo de Yssara e Hadrik: registro × texto (achado do M3.2)

Yssara Folha-de-Sal: registro `abissal-das-enseadas`, doc 01 "Elfa costeira". Hadrik das Sete Bigornas: registro `gnomo-dos-vales`, doc 01 "Anão residente". Opções: **A.** vale o registro (corrigir o texto do doc 01); **B.** vale o texto (trocar o ID no registro e no `.kt`). **Aguardando o autor.** O `12-competencias.csv` usa o registro.

## D-21 — Formato dos dados dos reinos (achado do M6.1)

Opções: **A.** texto com `|` (como os pacotes do RPG Solo); **B.** JSON; **C.** manter CSV. Comparação em `ESQUEMA_DE_DADOS.md` §1. **Recomendação: A.** **Decidida em 26/10/2026 ("sim vale"): recomendação aceita, ASSUMINDO, e válida para os reinos 1 e 2.** Até a decisão, o esquema usa A (ASSUMINDO); os campos valem para qualquer formato.

## D-22 — Generais e NPCs principais: mínimos × cânone (achado do M7.1)

Mínimos do autor: 2 heróis e 2 generais por reino, 8 a 12 NPCs principais. Aeltharyn tem 17 heróis, **1 general** (Saelith Arco-de-Chuva) e 25 NPCs principais com voz. Opções: **A.** aceitar como está e rebaixar os mínimos para aviso no validador (exceção registrada, V-99); **B.** designar um segundo general entre pessoas existentes (sem criar NPC); **C.** criar um general novo (lore novo, exige aprovação). Intervalo 8 a 12: **A.** tratar os 25 como "principais + apoio" e contar só os 10 de maior papel. **Recomendação: A para ambos.** **Decidida em 26/10/2026 ("sim vale"): recomendação aceita, ASSUMINDO, e válida para os reinos 1 e 2.**

## D-23 — Aliados e rivais das facções (achado do M7.1)

8 das 10 facções não têm aliados nem rivais escritos no doc 01 (só Vigias e Liga das Rotas têm). Opções: **A.** manter como lacuna explícita (limite do doc 11) e marcar `desconhecido` no esquema; **B.** derivar só das relações já escritas (`12-relacoes.csv`), sem criar nenhuma; **C.** propor alianças novas (lore novo, exige aprovação). **Recomendação: B.** **Decidida em 26/10/2026 ("sim vale"): recomendação aceita, ASSUMINDO, e válida para os reinos 1 e 2.**

## D-24 — Aprovação do M-05 (reino 2, 26/10/2026)

**Aprovado:** nativos (M5-01), forasteiros (M5-02), fluxos de migração (M5-03), registro dos quatro residentes de Aeltharyn (M5-04) e os dois encaixes menos óbvios (Estelar nos Humanos, Felino das Nuvens nas Montanhas). Os 12 nativos das Montanhas são o ponto de partida do reino 2.

## D-25 — Escala do reino 2 (26/10/2026)

**Decidida:** como no reino 1, 10 cidades e 20 assentamentos.

## D-26 — Nome e governo do reino 2 (26/10/2026)

O autor delegou a escolha. **Escolhido (ASSUMINDO aprovação por delegação):** nome oficial **Coroas de Pedra** (o reino leva o nome da cordilheira, como se diz no uso comum; sem segundo nome próprio, o que evita repetir o problema D-02). Governo: **confederação de cidades**, com um **Concílio das Coroas** de assento rotativo entre as dez cidades, em vez de monarquia (contraste com Aeltharyn; combina com vales altos e cidades isoladas pelo relevo). Cargos, títulos e regra de rotação ficam para R2-1.4.

## D-27 — Pasta do reino 2 (26/10/2026)

**Aceita:** `reinos/02-coroas-de-pedra-reino-das-montanhas/`.

## D-28 — Tom do mundo e ausência de eras globais (M-01 mínimo)

**W-01:** as cinco regras de tom da bíblia de Aeltharyn passam a valer para os cinco reinos. Sem eras globais: só eras locais até M-09. Opções: **A.** aprovar; **B.** cada reino define o próprio tom. **Recomendação: A.** **Aceita em 26/10/2026 pela continuação ("Siga"), ASSUMINDO a recomendação; reversível a pedido do autor.**

## D-29 — Ponte de calendário entre reinos (M-02 mínimo)

**W-02:** a ponte é o dia do ano (1 a 365), o ano solar e as estações físicas são comuns, e o número do ano é por reino, com deslocamento definido no R2-1.5. Opções: **A.** aprovar; **B.** ano numerado comum a todos os reinos (ano 742 em todo lugar). **Recomendação: A** (B força Coroas de Pedra a usar a Contagem das Seivas). **Aceita em 26/10/2026 pela continuação ("Siga"), ASSUMINDO a recomendação; reversível a pedido do autor.**

## D-30 — Convenções de nome do reino 2 (M-10 mínimo)

**W-03:** molde de nome observado em Aeltharyn mantido; regras de não repetição (nomes de pessoas e lugares do reino 1); famílias de epíteto sugeridas por povo nativo, só como tom. Opções: **A.** aprovar; **B.** sem famílias sugeridas, só o molde e as regras de não repetição. **Recomendação: A.** **Aceita em 26/10/2026 pela continuação ("Siga"), ASSUMINDO a recomendação; reversível a pedido do autor.**

## D-31 — As dez cidades das Coroas de Pedra (R2-1.1)

Dez cidades propostas (C-01 a C-10) em `reinos/02-coroas-de-pedra-reino-das-montanhas/01-reino-das-coroas-de-pedra.md`: Brasalume, Altavento, Veiaclara, Barcaalta, Portão-Cinza, Sinuosa, Cumieira, Rodaviva, Geodália e Pedra-Ata. Opções: **A.** aprovar todas; **B.** trocar nome, função ou lenda de alguma (diga qual). **Recomendação: A.** **Aceita em 26/10/2026 pela continuação ("Siga"), ASSUMINDO a recomendação A; reversível a pedido do autor.**

## D-32 — Os vinte assentamentos das Coroas de Pedra (R2-1.2)

Vinte assentamentos propostos (S-01 a S-20), dois por cidade, com lideranças de apoio (`npc.*`), em `reinos/02-coroas-de-pedra-reino-das-montanhas/03-assentamentos-e-rede-politica.md`. Opções: **A.** aprovar todos; **B.** trocar nome, vínculo, função ou liderança de algum (diga qual). **Recomendação: A.** **Aceita em 26/10/2026 pela continuação ("Siga"), ASSUMINDO a recomendação A; reversível a pedido do autor.**

## D-33 — NPCs principais e tríades das Coroas de Pedra (R2-1.3)

Doze principais (N-01 a N-12), oito heróis lendários falecidos e tríade herói, guardião e benfeitor para as dez cidades, na parte 2 de `reinos/02-coroas-de-pedra-reino-das-montanhas/01-reino-das-coroas-de-pedra.md`. Mínimos de D-22 cumpridos (2 heróis vivos, 2 generais). Opções: **A.** aprovar; **B.** trocar nome, povo, papel ou cidade de algum (diga qual). **Recomendação: A.** **Aprovada pelo autor em 26/10/2026 (opção A).**


## D-34 — Instituições, governo local e facções das Coroas de Pedra (R2-1.4)

Regras do Concílio das Coroas (composição, assento rotativo com três versões da ata, voto 6/10 de quórum e 7 para matéria reservada, arbitragem de 3 delegados), governo local das dez cidades com 7 acúmulos intencionais e 14 facções (F-01 a F-14) com aliados e rivais, em `reinos/02-coroas-de-pedra-reino-das-montanhas/04-instituicoes-justica-pactos.md`. Nenhum NPC novo; números de voto e de câmara são ASSUMINDO. Opções: **A.** aprovar tudo; **B.** trocar regra, governante, acúmulo ou facção de alguma (diga qual). **Recomendação: A.** **Aceita em 03/11/2026 pela continuação ("Siga"), ASSUMINDO a recomendação A; reversível a pedido do autor.**

## D-35 — História, defesa, leis, economia e Descida do Argente das Coroas de Pedra (R2-1.5)

Quatro eras locais, lendas com duas versões, cronologia relativa sem datas, Contagem das Atas com deslocamento ASSUMINDO de 226 anos (ano 516 das Atas = ano 742 de Aeltharyn), defesa por cidade e do reino (parte 3 do doc 01), dez leis, recursos, dependências e a Descida do Argente (`05-economia-leis-e-descida.md`). Nenhum NPC, cidade, facção ou assentamento novo; nenhuma ligação com Aeltharyn além do contrato de fronteira. Opções: **A.** aprovar tudo; **B.** trocar era, lei, lenda, o deslocamento de anos ou outro ponto (diga qual). **Recomendação: A.** **Aceita em 03/11/2026 pela continuação ("Siga"), ASSUMINDO a recomendação A; reversível a pedido do autor.**

## D-36 — Matriz de povos, sub-raças, híbridos e forasteiros das Coroas de Pedra (R2-1.6)

`10-matriz-povos-nativos-cidades-e-forasteiros.md`: 12 nativos em IDs do catálogo, sub-raças e híbridos do catálogo por cidade, 17 forasteiros de M-05 com motivos, papéis por povo e 5 heróis lendários falecidos novos (Tik Fenda-Funda, Embra Cinza-Viva, Dois-Fusos, Throm Juramento-Alto, Naru Sombra-de-Neve). Ponto aberto: o catálogo não tem `gnomo-das-rochas`. Opções: **A.** linhagem regional editorial do ID `gnomo`, como Aeltharyn faz com `centauro-das-florestas` (recomendada); **B.** adotar `gnomo-das-engrenagens` como sub-raça das Coroas. Demais pontos: aprovar ou trocar (diga qual). **Recomendação: A.** **Aceita em 03/11/2026 pela continuação ("Siga"), ASSUMINDO a recomendação A; reversível a pedido do autor.**

## D-37 — Mistérios, relógios, conflitos e oportunidades das Coroas de Pedra (R2-1.7)

Doc 01 parte 4: 6 mistérios (MP-01 a MP-06), cada um com 4 hipóteses abertas, vetos e provas possíveis; nenhum toca o Mistério D de Aeltharyn nem a mina selada. `06-modelo-operacional-e-aventuras.md`: 7 relógios (CL-01 a CL-07, progresso inicial 1/6 a 3/6, sem avanço automático), 10 conflitos com causa concreta e 10 oportunidades (1 por cidade). **ASSUMINDO:** o número "doc 11" do plano é a ata de fechamento (R2-8.2); R2-1.7 não cria arquivo 11. Opções: **A.** aprovar tudo; **B.** trocar mistério, relógio, progresso inicial ou promover C-06, C-07 ou C-09 a relógio. **Recomendação: A.** **Aceita em 04/11/2026 pela continuação ("Siga"), ASSUMINDO a recomendação A; reversível a pedido do autor.**

## D-38 — Registro mestre do reino 2 (R2-2.1)

`12-registro-mestre.csv` e `.md`: 89 entidades (10 cidades, 14 facções, 20 assentamentos, 45 pessoas) no formato de D-21, com a coluna extra `povo_id` (raça-base do catálogo); R2-2.2 acrescentou `12-facoes.csv` e `12-assentamentos.csv` sem decisão nova. `id_catalogo` fica vazio porque o catálogo não tem figuras nomeadas. Opções: **A.** manter `povo_id` (recomendada); **B.** removê-la e deixar o alinhamento só para o R2-2.3. **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-39 — Alinhamento dos povos das Coroas de Pedra ao catálogo (R2-2.3)

`referencia/racas-e-povos/ALINHAMENTO_COROAS.md`: 12 nativos, 13 sub-raças, 8 híbridos e 17 forasteiros conferidos (todos existem, nomes iguais), mais raça, sub-raça, classe e profissão do catálogo para as 45 pessoas (classe e profissão são leitura editorial, ASSUMINDO; o detalhe é do R2-4.2). Ponto aberto: `gnomo-das-rochas` (D-36). Opções: **A.** aprovar tudo; **B.** trocar atribuições (diga quais). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-40 — Cargos duplicados de facção e assentamento nas Coroas de Pedra (R2-2.4)

`13-conferencia-cruzada.md`: 0 problema sem marcação; 13 contradições intencionais registradas (CI-01 a CI-13). Cinco pessoas lideram uma facção e o assentamento onde ela nasce (Brundar, Pimm, Pip, Brunhild, Quinze-Pinos) e faltava marcar como intencional. Opções: **A.** aprovar os cinco como intencionais; **B.** trocar quem lidera o assentamento (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-41 — Tempos de viagem das Coroas de Pedra (R2-3.1)

`07-registro-operacional-de-rotas.md` e `07-mapa-esquematico.md`: 9 ligações entre cidades e fluxos, 20 acessos de assentamentos e 4 linhas de fronteira com Aeltharyn (todas "sem rota, sem tempo" ou "contato permitido"). Os docs do reino 2 não trazem nenhuma duração, então **toda duração está `desconhecida`**. Opções: **A.** manter `desconhecida` (recomendada; cumpre "nenhuma rota ou tempo inventado"); **B.** o autor fornece os tempos; **C.** o autor autoriza propostas de tempo marcadas ASSUMINDO. **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-42 — Faixas de população das Coroas de Pedra (R2-3.2)

`05-economia-leis-e-descida.md` §4 e `12-demografia.csv`: nenhum doc do reino 2 traz população; as faixas são **porte relativo** (grande, média, pequena) e não números, com a regra "não somar" (DM-03). Cidades: 2 grandes (Brasalume, Barcaalta), 6 médias, 2 pequenas (Altavento, Geodália), todas ASSUMINDO por pistas fracas (confiança baixa em 8 de 10). Assentamentos por regra da categoria espacial do doc 03. Opções: **A.** aprovar como porte relativo, não censo (recomendada); **B.** o autor define faixas ou valores; **C.** deixar tudo `indefinida`. **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-43 — Níveis provisórios de recursos das Coroas de Pedra (R2-3.3)

`08-registro-operacional-de-recursos.md`, `09-matriz-material-dos-assentamentos.md` e `12-recursos.csv`: 12 recursos, 84 linhas, 29 localidades, 28 níveis provisórios de produção (±1) por regra aberta (4 = ofício da cidade, 3 = assentamento que fornece insumo, 2 = fonte parcial), demanda derivada só de água e alimento a partir do porte relativo (D-42), reserva `?` em tudo, nenhum nível 0 nem 5, 11 dependências críticas com estado e fonte, nenhuma cidade isolada. Combustível e alimento ficam com origem **desconhecida**. Opções: **A.** aprovar como estimativa de planejamento, não como economia calibrada (recomendada); **B.** o autor define os níveis; **C.** deixar tudo `?`. **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-44 — Calendário e estações das Coroas de Pedra (R2-3.4)

Parte 5 do doc 01: 12 luas numeradas, quatro estações de montanha (Gelo Fechado, Degelo, Passo Aberto, Antes-da-Neve), cinco Dias de Balanço, início em Gelo Fechado, Lua 1, dia 1 do ano 516 das Atas (= ano 742 de Aeltharyn, deslocamento de D-35), tabela de conversão preenchida em `mundo/M-02`, agenda sazonal só como condição (nenhum relógio anda por calendário) e ciclo de guarda de um ano. Nomes, duração do ciclo e agenda são ASSUMINDO onde não há fonte. Opções: **A.** aprovar (recomendada); **B.** trocar nomes de estação, dos dias de virada ou a duração do ciclo (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-45 — Fichas completas dos doze principais das Coroas de Pedra (R2-4.1)

`12-fichas.md` e `12-fichas.csv`: as 12 fichas N-01 a N-12 (D-33) completas campo a campo (17 campos, 0 vazio), com raça, sub-raça, classe e profissão do catálogo, 12 agendas e 12 pressões distintas, origem `desconhecida` em todas e estado vivo. Traços de personalidade, medos e condições de evolução são ASSUMINDO; nenhum NPC, fato ou relógio novo. Opções: **A.** aprovar (recomendada); **B.** trocar traço, medo ou condição de alguma ficha (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-46 — Competências das 45 pessoas das Coroas de Pedra (R2-4.2)

`12-competencias.csv` e `12-competencias.md`: 45 linhas (12 principais, 20 lideranças de assentamento, 13 heróis lendários) com raça, sub-raça, classe e profissão do catálogo (todos existem no JSON), capacidades e limites tirados só do que os docs já dizem. Classe e profissão seguem como leitura editorial (D-39); o catálogo não liga habilidades a classe ou profissão. Nenhum NPC, fato ou número novo. Opções: **A.** aprovar (recomendada); **B.** trocar capacidade ou limite de alguma pessoa (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-47 — Relações e conhecimento das pessoas das Coroas de Pedra (R2-4.3)

`12-relacoes.csv`/`.md` (91 relações, todas com fonte: alianças e rivalidades do doc 04, dívidas e tensões do doc 03, vínculos de cargo dos docs 01 e 06, medos das fichas como ASSUMINDO) e `12-conhecimento.csv` (82 linhas, 45 pessoas). **Afeto: 0 relação documentada**, nada inventado. Opções: **A.** manter assim (recomendada); **B.** o autor indica relações de afeto (diga quais). Demais pontos: aprovar ou trocar (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-48 — Voz e ganchos de diálogo dos doze principais das Coroas de Pedra (R2-4.4)

`12-voz.csv` e `12-voz.md`: tom, 2 a 3 frases-modelo e "nunca diz" para N-01 a N-12 (Maur só em particular, pois não fala em público). As frases são instrução de tom (ASSUMINDO), não fala canônica, e não revelam suspeita nem desconhecido das fichas. Opções: **A.** aprovar (recomendada); **B.** trocar tom, frase ou "nunca diz" de algum principal (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-49 — Gatilhos, custo e inação dos sete relógios das Coroas de Pedra (R2-5.1)

`12-relogios.csv` e `12-relogios.md`: CL-01 a CL-07 com "o que move", gatilhos observáveis de avanço (o que o doc 06 já exigia no efeito da inação, agora em lista), "o que trava", quem paga, decisão pública no segmento 5 (governo local ou Concílio já documentado), efeito ao completar e efeito da inação, mais a tabela comum de custo por segmento. Nenhum relógio novo; progresso inicial de D-37 mantido. Opções: **A.** aprovar (recomendada); **B.** trocar gatilho, órgão de decisão ou progresso inicial de algum relógio (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-50 — Eventos-tipo e cadeias causais das Coroas de Pedra (R2-5.2)

`12-eventos.csv` e `12-eventos.md`: 13 eventos-tipo (8 ligados aos relógios CL-01 a CL-07, 5 sem relógio: C-06, C-07, C-09 e a tensão S-14), cada um com gatilho observável, cadeia causal, cidades e assentamentos, recursos, pessoas e facções, rotas e fonte. Todos os IDs existem no registro mestre, em `12-recursos.csv`, `12-relogios.csv` ou no doc 07. A ordem dos elos é ASSUMINDO; nenhum fato, relógio ou NPC novo; nada sobre Aeltharyn nem tempo de viagem. Opções: **A.** aprovar (recomendada); **B.** trocar gatilho, elo ou evento de algum (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-51 — Sementes de aventura das Coroas de Pedra (R2-5.3)

`13-sementes-de-aventura.csv` e `.md`: 90 sementes (30 entidades × pequena, média e grande), cada uma com gancho, agentes, abordagens possíveis, "se ninguém age", ligação a relógio ou evento (só os já escritos; "ligação não provada" quando as fontes não ligam) e fonte. Ameaça 1 a 4 (ASSUMINDO, mesma escala do reino 1): P = 1, M = 2 (duas com 3), G = 3 (duas com 4). Nenhuma solução predefinida, nenhuma hipótese de mistério escolhida, nenhum NPC, evento ou relógio novo, nenhum NPC com `vivo = não`. Opções: **A.** aprovar (recomendada); **B.** trocar semente, ameaça ou ligação de alguma (diga qual). **Recomendação: A.** **Aceita em 04/11/2026 pela continuação ("Siga"), ASSUMINDO a recomendação A; reversível a pedido do autor.**

## D-52 — Reação do reino ao jogador nas Coroas de Pedra (R2-5.4)

`13-reacao-ao-jogador.md`, `13-reputacao.csv` (24 linhas: 10 cidades e 14 facções) e `13-canais.csv` (11 canais): o que cada entidade valoriza ou pune, quem reage e por qual canal; estados de procurado, ato × regra × autoridade pelas leis L-01 a L-10, favor e reparação (reparação, compensação, restrição, perda de cargo). Sem número de fama, preço ou prazo (toda rota é `desconhecida`); cartaz e caçador de pessoas só como canais, sem facção, ofício ou NPC novos; punição como pena própria fica como lacuna. Opções: **A.** aprovar (recomendada); **B.** trocar linha de reputação, canal ou a lacuna de punição (diga qual). **Recomendação: A.** **Aceita em 04/11/2026 pela continuação ("Siga"), ASSUMINDO a recomendação A; reversível a pedido do autor.**

## D-53 — Mistérios com limites das Coroas de Pedra (R2-5.5)

`13-misterios-limites.md` e `.csv` (prefixo `mist.`): os seis mistérios MP-01 a MP-06 do doc 01 viram dados, com fatos confirmados (rumor e suspeita marcados como pista), quatro hipóteses abertas cada, "a verdade não pode ser", evidência cumulativa para resolver e ligação a relógios e eventos já escritos. Nenhum mistério escolhe hipótese, resolve ou toca o Mistério D de Aeltharyn; nenhum NPC, facção ou rota novo. Opções: **A.** aprovar (recomendada); **B.** trocar fato, veto ou evidência de algum (diga qual). **Recomendação: A.** **Aceita em 04/11/2026 pela continuação ("Siga"), ASSUMINDO a recomendação A; reversível a pedido do autor.**

## D-54 — Resumos de contexto das Coroas de Pedra (R2-6.1)

`14-resumos-de-contexto.md` e `.csv`: 96 resumos (10 cidades, 20 assentamentos, 14 facções, 7 relógios, 45 pessoas), cada um com ID, no máximo 500 caracteres e vizinhança de uma linha (no máximo 300). Sem suspeita nem desconhecido das fichas, sem duração de rota, sem número de população; `camada_max` provisória (`local`; relógios `secreto`) até o R2-6.2. Opções: **A.** aprovar (recomendada); **B.** trocar o texto de algum resumo (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-55 — Camadas de informação das Coroas de Pedra (R2-6.2)

`14-camadas-de-informacao.md` e `.csv` (prefixo `fact.`): 41 fatos em quatro camadas (12 públicos, 10 locais, 5 restritos, 14 secretos) vindos de `12-conhecimento.csv` (confirmado e suspeita dos 12 principais), dos seis mistérios e dos 7 relógios, cada um com quem sabe, caminho e como descobrir; `marcador` nos restritos e secretos (V-70, V-72). Critério de classificação e caminho mínimo são ASSUMINDO, como no reino 1; evidência não documentada (Vireo, Maur) fica como lacuna. `camada_max` dos resumos passa a definitiva (`local`; relógios `secreto`) e o campo *Camadas* das fichas N-01 a N-12 lista os `fact.` da pessoa. Nenhum NPC, facção, rota, número ou fato novo; nada sobre Aeltharyn. Opções: **A.** aprovar (recomendada); **B.** trocar a camada ou o caminho de algum fato (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-56 — Cenas de teste das Coroas de Pedra (R2-6.3)

`15-cenas-de-teste.md` e `.csv`: seis cenas (vilarejo de montanha Rastro-Branco; chegada a Sinuosa; audiência no Concílio; investigação no Arquivo das Coroas; viagem Barcaalta–Brasalume; travessia rumo a Torvelume a partir de Portão-Cinza), cada uma com "entra" montado só dos resumos, fatos públicos ou locais, sementes, vozes e rotas já escritos (935 a 2.033 caracteres) e "não entra". Nenhum relógio, restrito, secreto, NPC morto ou duração de rota; a cena da Espinha só repete o veto do doc 07 §4 (Portão-Cinza ↔ Torvelume sem rota, sem tempo). A escolha das entidades de cada cena é ASSUMINDO. Opções: **A.** aprovar (recomendada); **B.** trocar a entidade ou o conteúdo de alguma cena (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-57 — Contrato anti-invenção das Coroas de Pedra (R2-6.4)

Seção 9 do doc 06 do reino 2: verificação antes de aceitar proposta, quatro classes (fixo, livre, exige registro, proibido), lista proibida, casos (novo governante, cidade, guerra, NPC ou organização, morte, rota) e teste rápido de quatro perguntas. Herda `reinos/01-.../06-...md` §11 e §11.1 sem alterá-los. A separação em classes é leitura editorial (ASSUMINDO); nenhuma regra, entidade ou fato novo. Opções: **A.** aprovar (recomendada); **B.** mover item entre classes ou trocar caso (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-58 — Dados no formato D-21 e primeiro relatório do validador das Coroas de Pedra (R2-7.1)

`reinos/02-.../dados/` (21 arquivos `.txt`, 592 registros) gerados dos CSVs e docs por `plano/ferramentas/converter_reino2.py`; `validar_reino.py` implementa as regras do `VALIDADOR_DE_REINO.md`. Resultado: **0 erro, 40 avisos, 7 exceções** (`excecoes.txt`: 6 eventos que citam rotas do doc 07 sem dois locais existentes e a rota Altavento–Pedra-Ata, que repete o par da delegação ao Concílio). O primeiro relatório achou 5 colisões de ID com Aeltharyn (canais): os 11 canais do reino 2 viraram `canal.coroas-*`. Pontos ASSUMINDO: capital = Pedra-Ata (sede do Concílio), `objetivo` do principal = `agenda`, faixas de assentamento do doc 05 §4 aceitas em V-52. Opções: **A.** aprovar (recomendada); **B.** trocar a capital, os IDs de canal ou uma exceção (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-59 — Catálogo técnico: Montanhas no `.kt` (R2-7.2)

`MAPA_CATALOGO_KOTLIN.md` §5 (K1 a K9): o `.kt` não tem nenhuma entidade das Coroas de Pedra e não colide com nenhum ID do reino 2; só toca o reino por Torvelume e pela Espinha (rota `mountain_pass`, população, `clock.sealed-mine`), tudo de Aeltharyn. O `.kt` é singleton de Aeltharyn e não vira carregador do reino 2. Divergências K4 a K6 seguem sem correção no `.kt`. `mountain_pass` não é meio válido no esquema: nenhum valor foi criado (as 13 rotas seguem `meio = ?`). Opções: **A.** aprovar (recomendada); **B.** decidir um valor de `meio` para passo de montanha, o que altera `ESQUEMA_DE_DADOS.md` (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-60 — Protótipo mínimo das Coroas de Pedra (R2-7.3)

`PROTOTIPO_MINIMO.md` §10: vilarejo Rastro-Branco, liderança Miraq Salto-de-Névoa e Altavento em uma linha (contexto B de 1.192 caracteres); condições A e B; roteiro de 8 turnos; métricas Q1 a Q10 e critério de aceite fixados antes de jogar. O vazamento é medido contra `fact.relogio-passos` (secreto) e qualquer fato restrito ou secreto, porque Rastro-Branco não tem fato próprio nas camadas; a armadilha do turno 8 é a duração (toda rota é `desconhecida`). Escolha do recorte, da linha de Altavento e dos alvos é ASSUMINDO; nenhuma sessão executada, nenhum lore novo. Opções: **A.** aprovar (recomendada); **B.** trocar o vilarejo, o NPC ou algum alvo (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-61 — Revisão final das Coroas de Pedra (R2-8.1)

`RELATORIO_R2_8_1.md`: portas 1 a 10 do checklist marcadas, 11 pendente do R2-8.2. Uma contradição acidental corrigida (contagem de nativos no contrato de fronteira §2, sem mudar fato). Pendência: 4 facções sem rival e 1 sem aliado (`?`), que nenhum documento nomeia. Opções: **A.** aceitar como lacuna explícita do reino, a registrar no doc 11 (recomendada); **B.** o autor nomeia rivais ou aliados (lore novo, com registro). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-62 — Ata de fechamento 3 das Coroas de Pedra (R2-8.2)

Doc 11 do reino 2 (`11-fechamento-das-coroas-de-pedra.md`): resultado das oito fases, 11 de 11 portas, contradições intencionais e acidentais, decisões em aberto como limite explícito, critérios finais de uso e a ata. **O reino 3 (Véu dos Malditos) não foi liberado:** só começa com a aprovação do autor. Alinhado também o status de D-51 a D-53 no `DECISOES.md` (aceitas pela continuação, como já constavam no plano, README e CHANGELOG). Opções: **A.** aprovar o fechamento 3 e liberar o reino 3, com D-38 a D-50 e D-54 a D-62 seguindo como limites explícitos (recomendada); **B.** aprovar o fechamento sem liberar o reino 3 ainda; **C.** pedir mudança em alguma decisão antes (diga qual). **Recomendação: A.** **Aprovada pelo autor em 05/11/2026 (opção A), ao pedir "Vamos seguir pro reino 3".** Ver D-65.

## D-63 — Escala qualitativa para todos os reinos (05/11/2026)

O autor decidiu que distância, tamanho e produção **não precisam de número**: basta dizer que uma cidade é rica em minério, madeira ou outro recurso, que uma viagem é longa ou curta, que uma população é pequena, média ou grande. Vale para o reino 2 e para os reinos 3 a 5. Efeitos: (1) duração de rota, população numérica, estoque e produção deixam de contar como pendência: são **escolha de escopo**, não lacuna a fechar; (2) os avisos do validador sobre esses campos (V-27 e afins) são esperados; (3) nos reinos 3 a 5 a camada de números (tempos de viagem, níveis finos de recurso, faixas de população) não será escrita; (4) a regra anti-invenção continua: a IA não cria número onde o texto não o tem, mas pode narrar de forma qualitativa ("longa", "perto", "rica em minério"). **Decidida pelo autor.** Não altera nenhum dado nem o formato do esquema; D-41 (tempos de viagem), D-42 (faixas de população) e D-43 (níveis de recurso) do reino 2 ficam como estão, reversíveis.

## D-64 — Mestiços não são alocados (05/11/2026)

O autor decidiu que os mestiços (híbridos) **não são alocados** a reino, cidade, assentamento ou facção. Regra: um mestiço pode nascer na cidade de qualquer um dos pais; não há lista fechada de 20, pois qualquer combinação de duas raças ou sub-raças do catálogo (50 e 50, expansível; no mínimo 50 possibilidades, na prática mais de mil pares de raças-base) pode gerar um; o jogador escolhe, conforme o estilo. Os 20 do catálogo ficam como **exemplos nomeados**. Efeitos: `referencia/racas-e-povos/hibridos.md` reescrito com a regra; doc 10 do reino 2 §3 e a coluna de híbridos do §4 trocados pela regra; `ALINHAMENTO_COROAS.md` §1.2, `ALINHAMENTO_AELTHARYN.md`, `M-05` §7, `MATRIZ_POVOS_POR_REINO.md`, `PLANO_RACAS_E_SUBRACAS.md` (requisito para a implementação futura) e README ajustados. Nenhum dado em `dados/` citava mestiço; validador inalterado. **Decidida pelo autor.**

## D-65 — Liberação do reino 3, Véu dos Malditos (05/11/2026)

O autor aprovou o fechamento 3 das Coroas de Pedra e liberou o reino 3 ao pedir "Vamos seguir pro reino 3" (opção A de D-62). Efeitos: (1) o doc 11 do reino 2 passa de **[PROPOSTO]** a **[APROVADO]**; (2) D-38 a D-50 e D-54 a D-61 seguem como limites explícitos (aviso, não erro, no validador); (3) novo `plano/REFINAMENTO_VEU_DOS_MALDITOS.md`, com 37 módulos em 9 fases (prefixo `R3-`), já sob D-63 (sem camada de números) e D-64 (sem mestiços alocados); (4) novo `plano/CONTRATO_DE_FRONTEIRA_AELTHARYN_VEU.md` (R3-0.4), com 18 fixos de Aeltharyn, o que o reino 3 não pode afirmar e os pontos de contato com as Coroas; (5) reinos 4 e 5 seguem bloqueados; (6) nada de Aeltharyn nem das Coroas foi alterado além do status da ata. **Decidida pelo autor.**

## D-66 — Escala, nome, governo e pasta do reino 3 (R3-0.3, 05/11/2026)

O autor respondeu em 05/11/2026. **Nome: decidido (opção A).** Véu dos Malditos é o nome do reino; "o Véu" fica só para a faixa de neblina de M-04 G-08 (que inclui a orla de Aeltharyn). **Governo: decidido pelo autor (variante da opção C).** Autoridades ligadas aos pactos, sob um **rei conquistado em batalha**: a coroa não é herdada, passa a quem vence, e este é o reino com **mais trocas de rei** do mundo. Há cerca de **cem anos** (contagem local) reina **um só rei**, que estabilizou as terras: um **Lord necromante**. O autor delegou a história e o resto ao redator (nome, povo, métodos, limites e sucessão entram no R3-1.3 e R3-1.4, com registro). **Escala: a opção A (10 cidades e 20 assentamentos) foi recusada** ("não aqui e apenas uma área"); o autor confirmou ("Ok", 05/11/2026) a leitura de um reino de **uma área só**, sem rede de dez cidades: **uma capital do rei e sete lugares de pacto**, oito lugares principais no total (os arquivos os chamam de `city.*` por compatibilidade). **Pasta:** `reinos/03-veu-dos-malditos/` (aceita pela continuação, reversível).

**Limites que o redator se impõe a partir daqui:** (1) as "cem anos" são contagem local e **não são ligadas à Neblina Longa de Aeltharyn** (anos 561 a 650), nem a conversão de ano os aproxima (contrato §3, item 1); (2) a necromancia é ofício e escolha do Lord, **não traço de raça**, e "Maldito" continua sem ser sentença (W-01); (3) necromante não existe no catálogo de classes: classe e profissão do Lord seguem leitura editorial, como em D-39; (4) a necromancia do Lord ganha custo e limites explícitos, e o que ela **não** faz fica registrado no R3-1.7; (5) o plano do reino 3 em `REFINAMENTO_VEU_DOS_MALDITOS.md` é ajustado à escala confirmada. **Classe do Lord:** Invocador, com profissão de apoio de leitura editorial (aceita pelo autor, "Ok"); necromante não é classe nem profissão do catálogo, e o catálogo não muda. **Decidida pelo autor (nome, governo e escala).**

## D-67 — Convenções de nome do reino 3 (M-10, W-04)

Nova seção 6 em `mundo/M-10-nomes-minimo.md`: molde de nome mantido; regras de não repetição ampliadas com os nomes das Coroas de Pedra (45 pessoas, 10 cidades, 20 assentamentos, 14 facções); famílias de epíteto sugeridas para os 11 nativos, só como tom; "Maldito" é nome do reino, não sentença. Opções: **A.** aprovar; **B.** sem famílias sugeridas, só o molde e as regras de não repetição. **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-68 — Os oito lugares do Véu dos Malditos (R3-1.1)

Oito lugares propostos (V-01 a V-08) em `reinos/03-veu-dos-malditos/01-reino-do-veu-dos-malditos.md`: Morrovéu (capital do Lord Morvan Voz-Baixa), Lamaluz, Fozbreu, Beira-Sal, Selovale, Borda-Mansa, Fio-Longo e Candeia-Morta. Mais, no mesmo doc, a regra de duas camadas (coroa conquistada em batalha, pactos que sobrevivem ao rei) e o nome do Lord, [PROPOSTO]. Todos os 11 nativos e só forasteiros da matriz M-05 §4 aparecem. Ponto a conferir em M-05: a Descida do Argente cita anões, gnomos e construtos no Véu, mas a matriz lhes dá "—"; no doc eles são só visitantes em Fozbreu. Opções: **A.** aprovar tudo; **B.** trocar nome, função, lenda ou povos de algum lugar, ou o nome do Lord (diga qual); **C.** mudar a matriz de M-05 para anões, gnomos e construtos (diga como). **Recomendação: A.** **Aceita pela continuação ("Siga", 05/11/2026); reversível.**

## D-69 — Rede política dos oito lugares (R3-1.2)

`reinos/03-veu-dos-malditos/03-assentamentos-e-rede-politica.md`: por lugar, liderança (papel, sem nome), obrigação, interesse e tensão, derivados só do doc 01; posição relativa qualitativa; pontos de contato. Sem assentamentos próprios nem cidade-mãe (D-66); sem número, rota ou duração (D-63). Opções: **A.** aprovar; **B.** pedir vilas menores dentro de algum dos oito (diga qual e quantas); **C.** mudar obrigação, interesse ou tensão de algum lugar (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 05/11/2026); reversível.**

## D-70 — NPCs principais e tríades do Véu dos Malditos (R3-1.3)

Parte 2 de `reinos/03-veu-dos-malditos/01-reino-do-veu-dos-malditos.md`: 12 principais (N-01 a N-12), 6 heróis lendários falecidos e tríade herói, guardião e benfeitor dos 8 lugares. Mínimos de D-22 cumpridos (2 heróis vivos, 2 generais). Inclui a liderança nomeada que o doc 01 e o doc 03 adiaram e o povo do Lord Morvan Voz-Baixa: **Humano** (ASSUMINDO; mostra que a coroa não pertence a povo nenhum). Idade, origem e método da estabilização dele seguem `desconhecidos` (R3-1.5 e R3-1.7). Opções: **A.** aprovar; **B.** trocar nome, povo, papel ou lugar de algum (diga qual); **C.** trocar só o povo do Lord (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 05/11/2026); reversível.**

## D-71 — Instituições, pactos e facções do Véu dos Malditos (R3-1.4)

`reinos/03-veu-dos-malditos/04-instituicoes-justica-pactos.md`: Conselho dos Pactuários (8 assentos; o de Morrovéu sem titular nomeado), competências, 8 pactos, regra da Provação das Armas e sucessão (ASSUMINDO), justiça, tributo qualitativo, governo local e 11 facções com aliados e rivais (0 pessoa nova). Casos que o texto não resolve ficam `desconhecidos` (necromancia na Provação, o que a guarda faz se o rei cai). Opções: **A.** aprovar; **B.** trocar regra da Provação, voto do Conselho ou alguma facção (diga qual); **C.** nomear o titular do assento de Morrovéu (diga quem ou peça proposta). **Recomendação: A.** **Aceita pela continuação ("Siga", 05/11/2026); reversível.**

## D-72 — História, cronologia, defesa, leis, economia e fluxos do Véu (R3-1.5)

Parte 3 do doc 01 (4 eras locais, lendas e o que fixam, cronologia relativa sem datas, defesa e vulnerabilidades) e `05-economia-leis-e-fluxos.md` (9 leis com lacuna registrada, economia qualitativa por lugar, chegada da Descida do Argente em Fozbreu e saída das Fugas do Véu por Lamaluz, só do lado do Véu). Sem número, sem ano de Aeltharyn, sem rota nova, sem refugiados vindos de outro reino; a necromancia continua aberta frente à Lei dos Mortos (L-07). Opções: **A.** aprovar; **B.** trocar era, lenda, lei ou fluxo (diga qual); **C.** decidir uma lacuna (por exemplo L-07 ou L-09, diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 05/11/2026); reversível.**

## D-73 — Matriz de povos do Véu dos Malditos (R3-1.6)

`reinos/03-veu-dos-malditos/10-matriz-povos-nativos-cidades-e-forasteiros.md`: 11 nativos em ID do catálogo, 10 sub-raças (ASSUMINDO pelo nome), lugares × povos, 17 forasteiros de M-05 §4 (12 com lugar conforme o doc 01; 5 sem lugar fixo, só viajantes avulsos, ASSUMINDO), papéis por povo e **5 heróis lendários falecidos novos** (Mursk, Zibb, Aun, Lish, Xalu) para os povos sem herói. Mestiços só pela regra D-64. Opções: **A.** aprovar; **B.** trocar sub-raça, lugar de forasteiro ou nome de herói (diga qual); **C.** não criar os 5 heróis e deixar a lacuna aberta. **Recomendação: A.** **Aceita pela continuação (execução do R3-1.7, 05/11/2026); reversível.**

## D-74 — Mistérios, relógios, conflitos e oportunidades do Véu dos Malditos (R3-1.7)

Parte 4 do doc 01 (`reinos/03-veu-dos-malditos/01-reino-do-veu-dos-malditos.md`): **9 mistérios** (MV-01 a MV-09) com 4 hipóteses abertas, vetos ("a verdade não pode ser") e provas possíveis, mais os **limites da necromancia do Lord** (o que não faz e o que fica aberto de propósito, D-66 limite 4). Doc 06 (`06-modelo-operacional-e-aventuras.md`): **8 relógios** (CV-01 a CV-08, um por lugar, progresso inicial de 1/6 a 3/6, ASSUMINDO), 8 conflitos com causa concreta (CF-01 a CF-08) e 8 oportunidades (OV-01 a OV-08). Nenhum mistério explica a Neblina Longa, o Mistério E ou o Mistério D; Aeltharyn só aparece em vetos. 0 NPC, lugar, facção ou rota novo; 0 número ou tempo. Opções: **A.** aprovar tudo; **B.** trocar mistério, relógio, progresso inicial ou o número de relógios (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 05/11/2026); reversível.**

## D-75 — Registro mestre do Véu dos Malditos (R3-2.1)

`12-registro-mestre.csv` e `.md`: 49 entidades (8 lugares, 11 facções, 30 pessoas) no formato de D-21, com `povo_id`. 18 pessoas sem ficha (7 da tríade e 11 heróis lendários) receberam ID pelo nome já escrito, sem pessoa nova. 0 ID duplicado, 0 citado sem registro, 0 colisão com os reinos 1 e 2. Opções: **A.** aprovar; **B.** trocar slug de ID (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 05/11/2026); reversível.**

## D-76 — Facções e lugares como dados do Véu dos Malditos (R3-2.2)

`12-facoes.csv` (11 facções, 11 colunas) e `12-assentamentos.csv` (os 8 lugares, 6 colunas, `cidade_mae` `—` por D-66), no molde do reino 2. 0 campo vazio; `recursos` das facções `desconhecido` (o doc 04 não os lista); `tensao` da facção é a do lugar-sede (ASSUMINDO); 3 rivais `desconhecido` com motivo. 0 assimetria, 0 aliado-rival, 0 ID inexistente. Opções: **A.** aprovar; **B.** preencher `recursos` das facções ou trocar a tensão de alguma (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 05/11/2026); reversível.**

## D-77 — Alinhamento dos povos do Véu dos Malditos ao catálogo (R3-2.3)

`referencia/racas-e-povos/ALINHAMENTO_VEU.md`: 11 nativos, 10 sub-raças e 17 forasteiros conferidos contra `personagem-catalogo.json` (todos existem, nomes iguais, raça-mãe correta), e raça, sub-raça, classe e profissão para as 30 pessoas (classe e profissão são leitura editorial, ASSUMINDO; profissão `—` onde não há ofício escrito; heróis lendários sem lugar ficam sem sub-raça). 0 ID fora do catálogo. Morvan segue `invocador` com `escrivao-judicial` (D-66). Opções: **A.** aprovar; **B.** trocar atribuições (diga quais). **Recomendação: A.** **Aceita pela continuação ("Siga", 05/11/2026); reversível.**

## D-78 — Conferência cruzada do Véu dos Malditos (R3-2.4; fim da Fase 2)

`13-conferencia-cruzada.md`: 0 aliado-rival, 0 assimetria, 0 liderança fora da presença, 0 povo fora dos presentes do lugar, 0 diferença entre facções por lugar (doc 01 × CSV). 7 acúmulos de cargo (assento e facção: Nuvik, Rusk, Teshi, Yeshe, Krell, Ondra, Dhun) propostos como intencionais (CI-09); mapa de quem sabe o quê dos 9 mistérios; 12 contradições ou pontos abertos (CI-01 a CI-12). Duas inconsistências acidentais tratadas (cabeçalhos dos docs 01 e 03 que ainda listavam como adiado o que já estava escrito). Opções: **A.** aprovar; **B.** desfazer algum acúmulo ou fechar uma lacuna (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.** **Aceita pela continuação ("Siga", 05/11/2026); reversível.**

## D-79 — Rotas e deslocamento do Véu dos Malditos (R3-3.1)

`07-registro-operacional-de-rotas.md` e `07-mapa-esquematico.md`: 7 ligações (6 documentadas sem duração, 1 sugerida por dependência), 8 acessos dos lugares e 7 linhas de fronteira com Aeltharyn e outros reinos (todas "sem rota" ou "contato permitido"). Os docs do reino 3 não trazem duração nem "curta" ou "longa" para nenhum percurso, então **toda duração é `desconhecida`**. 0 rota Véu–Aeltharyn, 0 passagem nova. Opções: **A.** manter tudo `desconhecida` (recomendada); **B.** o autor fornece "curta", "longa" ou tempos (e quais pares); **C.** o autor autoriza propostas marcadas como ASSUMINDO. **Recomendação: A.** **Aceita pela continuação ("Siga", 05/11/2026); reversível.**

## D-80 — Demografia do Véu dos Malditos (R3-3.2)

Doc 05, seção 5, e `12-demografia.csv` (8 lugares, formato do reino 2): faixas de porte relativo já presentes no doc 01 (1 grande, 3 médias, 4 pequenas; ASSUMINDO, D-63), com evidência e confiança; regra de **não somar**; população flutuante, Corte, guarda, mortos e proporção entre povos fora de qualquer faixa. 0 número, 0 censo, 0 diferença entre doc 01 e CSV. Opções: **A.** aprovar; **B.** trocar a faixa de algum lugar (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 05/11/2026); reversível.**

## D-81 — Recursos e matriz material do Véu dos Malditos (R3-3.3)

`08-registro-operacional-de-recursos.md` e `09-matriz-material-dos-lugares.md`: termos qualitativos "rica em", "pobre em" e "depende de" para os 8 lugares (a escala numérica de 0 a 5 do reino 2 **não** é repetida, ASSUMINDO); "pobre em" só onde o doc 01 registra falta ou vulnerabilidade, `—` onde não há registro; 9 dependências (7 críticas, cada uma com fonte; nenhum lugar isolado); 3 fluxos qualitativos (Descida, Rota do Sal, tributo em pactos). 0 número, nível, unidade ou tempo. Opções: **A.** aprovar (recomendada); **B.** adotar a escala de 0 a 5 do reino 2 (o autor diz quem atribui os níveis); **C.** trocar uma dependência ou termo (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 05/11/2026); reversível.**

## D-82 — Calendário e estações do Véu dos Malditos (R3-3.4; fim da Fase 3)

Doc 01, parte 5, `M-02` §7 e `M-10` §8: 12 luas numeradas, quatro estações (Névoa Fechada, Névoa Solta, Charco Aberto, Névoa Voltando, nomes provisórios), cinco Dias de Pacto, **Contagem das Provações** (época `desconhecida`); início do jogo em Névoa Fechada, Lua 1, dia 1 do **ano 389**, igual a Silêncio, Lua 1, dia 1 do ano 742 de Aeltharyn (deslocamento de 353, ASSUMINDO, sem fonte, reversível); tabela de conversão de 8 linhas; agenda sazonal só de condição (tudo ASSUMINDO); cheia e seca, caravanas, Descida, Fugas e vencimento do pacto de Selovale ficam `desconhecidos`. Nenhum evento do Véu datado em anos de Aeltharyn; o reinado de Morvan segue "cerca de cem anos". Opções: **A.** aprovar (recomendada); **B.** trocar nomes de estação, dos dias de virada, da contagem ou o deslocamento (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-83 — Fichas completas do Véu dos Malditos (R3-4.1)

`reinos/03-veu-dos-malditos/12-fichas.md` e `12-fichas.csv`: 12 de 12 principais com os 17 campos do molde do reino 2 (0 vazio), 12 agendas e 12 pressões distintas, povo, sub-raça, classe e profissão de `ALINHAMENTO_VEU.md`, relógio ligado a cada um (`06`), aliados e rivais da facção (doc 04). 6 acúmulos de cargo marcados como "acúmulo intencional proposto" (CI-09; o sétimo, Nuvik, não tem ficha completa). Traços, medos e condições de evolução são ASSUMINDO; coluna `camadas` `—` até R3-6.2. 0 pessoa, lugar, facção ou número novo. Opções: **A.** aprovar; **B.** trocar traço, medo ou condição de evolução de alguma ficha (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-84 — Competências do Véu dos Malditos pelo catálogo (R3-4.2)

`12-competencias.md` e `12-competencias.csv`: 30 pessoas (12 principais, 7 da tríade sem ficha, 11 heróis lendários) com raça, sub-raça, classe e profissão de `ALINHAMENTO_VEU.md`, capacidades e limites só do que os docs já dizem (fichas, doc 01, doc 04, doc 10). Benfeitores sem ficha: meios `desconhecidos`; lendários: "não age no início". 0 ID fora do catálogo, 0 campo vazio, 0 capacidade sem limite; catálogo inalterado. Opções: **A.** aprovar; **B.** trocar classe, profissão ou limite de alguém (diga quem). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-85 — Relações e conhecimento do Véu dos Malditos (R3-4.3)

`12-relacoes.md`, `12-relacoes.csv` (74 relações: 14 alianças, 10 rivalidades, 8 dívidas de lugar, 8 tensões, 22 vínculos de cargo, 12 medos; **0 afeto**) e `12-conhecimento.csv` (72 linhas, 30 de 30 pessoas). Tudo derivado de docs 01, 03, 04, 06 e fichas; toda relação com fonte; 0 ID fora do registro, 0 aliado-rival. Opções: **A.** aprovar; **B.** autorizar afeto ou dívida entre pessoas, marcados ASSUMINDO (diga os pares). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-86 — Voz e ganchos de diálogo do Véu dos Malditos (R3-4.4)

`12-voz.md` e `12-voz.csv`: 12 de 12 principais com tom, 2 a 3 frases-modelo (instrução de tom, ASSUMINDO) e `nunca_diz`; nenhuma frase revela suspeita ou desconhecido. Fecha a Fase 4 (testes locais em `12-voz.md`: 0 falha real). Opções: **A.** aprovar; **B.** trocar tom ou frase de alguém (diga quem). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-87 — Relógios do Véu dos Malditos (R3-5.1)

`12-relogios.md` e `12-relogios.csv`: os 8 relógios do doc 06 (D-74) com gatilhos observáveis (2 por relógio, reescrita do efeito da inação), custo por segmento, quem decide no segmento 5 (guardião do pacto, doc 04 §4) e efeito da inação. 0 relógio, NPC, lugar ou fato novo; 0 entidade fora do registro. Opções: **A.** aprovar; **B.** trocar gatilho, decisor no segmento 5 ou progresso de algum relógio (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-88 — Eventos-tipo e cadeias causais do Véu dos Malditos (R3-5.2)

`12-eventos.md` e `12-eventos.csv`: 12 eventos-tipo (8 completam um relógio, 2 só dão gatilho a relógio, 2 sem relógio) com cadeia, lugares, pessoas, facções e rotas do doc 07; elos só de dependências (DR) e leis (L) já documentadas, ordem ASSUMINDO. 0 ID inexistente; nenhum resultado de L-01, L-07, L-09, da Descida ou do pacto antigo decidido. Opções: **A.** aprovar; **B.** trocar, tirar ou somar um evento (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-89 — Sementes de aventura do Véu dos Malditos (R3-5.3)

`13-sementes-de-aventura.md` e `.csv`: 24 sementes (8 lugares × pequena, média e grande; ameaça 1 a 4, ASSUMINDO), cada uma com abordagens possíveis, "se ninguém age" e ligação a relógio ou evento já escritos (as 8 pequenas, de rumor, com "ligação não provada"). Agentes só pessoas vivas e facções do registro. Nenhum mistério, lei ou lacuna resolvido. Opções: **A.** aprovar; **B.** trocar, tirar ou somar uma semente (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-90 — Reação do reino ao jogador no Véu dos Malditos (R3-5.4)

`13-reacao-ao-jogador.md`, `13-reputacao.csv` (8 lugares e 11 facções) e `13-canais.csv` (11 canais, com coluna extra `rotas` do doc 07): fama qualitativa por entidade, estados de procurado (só a justiça do doc 04 §6), ato-regra-autoridade para L-01 a L-09 (lacunas mantidas), favor, reparação como lacuna com leitura ASSUMINDO, e intermediários só dos documentados. Sem número, prazo, caçador, cartaz ou facção novo. Opções: **A.** aprovar; **B.** trocar ou tirar uma reação ou canal (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-91 — Mistérios com limites do Véu dos Malditos (R3-5.5)

`13-misterios-limites.md` e `.csv`: os 9 mistérios do doc 01 parte 4 (MV-01 a MV-09) como dados, com fatos confirmados, 4 hipóteses abertas, "a verdade não pode ser", provas possíveis (mais a regra de duas provas independentes, doc 06 §6), entidades e ligação a relógio e evento. Nenhum veto acrescentado ou retirado; nenhuma resposta eleita; nenhum mistério toca os de Aeltharyn. Fecha a Fase 5 (testes locais no fim do `.md`: 0 falha real, 0 regressão em reinos 1 e 2). Opções: **A.** aprovar; **B.** trocar uma hipótese, veto ou ligação (diga qual). **Recomendação: A.** **Aceita pela continuação ("Ok siga", 06/11/2026); reversível.**

## D-92 — Resumos de contexto do Véu dos Malditos (R3-6.1)

`14-resumos-de-contexto.md` e `.csv`: 57 entidades (8 cidades, 11 facções, 8 relógios, 30 pessoas) com resumo ≤500 caracteres e vizinhança ≤300, só com o que os docs e CSV escrevem; sem suspeita, desconhecido, `nunca_diz`, relação ASSUMINDO, duração de rota ou número de população. `camada_max` provisória (`local`; relógios `secreto`) até o R3-6.2. Opções: **A.** aprovar; **B.** trocar o texto de uma entidade (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-93 — Camadas de informação do Véu dos Malditos (R3-6.2)

`14-camadas-de-informacao.md` e `.csv`: 41 fatos (confirmado e suspeita dos 12 principais, um fato confirmado por mistério MV-01 a MV-09, os 8 relógios) em 4 camadas (7 públicos, 14 locais, 4 restritos, 16 secretos), cada restrito ou secreto com caminho e como descobrir; critério ASSUMINDO igual ao dos reinos 1 e 2. Coluna `camadas` das fichas preenchida; `camada_max` dos resumos definitiva (`local`; relógios `secreto`); nenhum marcador restrito ou secreto em resumo `local`. Opções: **A.** aprovar; **B.** trocar a camada ou o caminho de um fato (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-94 — Cenas de teste do Véu dos Malditos (R3-6.3)

`15-cenas-de-teste.md` e `.csv`: 6 cenas (Lamaluz como "vilarejo de charco", por não haver assentamentos, D-66; chegada a Morrovéu; audiência no Conselho; arquivo em Selovale; rota do Ermo de Sal; orla norte só em rumor), cada uma com "entra" montado de resumos, fatos públicos ou locais, voz, semente e rota já escritos (maior 1.901 caracteres) e "não entra". Nenhum marcador restrito ou secreto, relógio ou `nunca_diz` em "entra". Opções: **A.** aprovar; **B.** trocar uma cena ou o lugar de teste (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-95 — Contrato anti-invenção do Véu dos Malditos (R3-6.4)

Seção 9 do doc 06 do reino 3: verificação antes de aceitar proposta, quatro classes (fixo, livre, exige registro, proibido), lista proibida (segundo rei, vitória ou abolição da Provação, titular do assento de Morrovéu, 9º lugar, 12º povo nativo, ressurreição de herói lendário, guerra, mistério resolvido, custo da estabilização, povo mau por natureza, número ou duração inventados, camada alterada, relógio sem gatilho, rota ou fala sobre Aeltharyn além do contrato de fronteira), casos (rei, lugar, guerra, NPC ou organização, morte, rota, rumor sobre Aeltharyn) e teste rápido de quatro perguntas. Herda os contratos de Aeltharyn e das Coroas sem alterá-los. A separação em classes é leitura editorial (ASSUMINDO); nenhuma regra, entidade ou fato novo. Fecha a Fase 6. Opções: **A.** aprovar (recomendada); **B.** mover item entre classes ou trocar caso (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-96 — Dados no formato D-21 e primeiro relatório do validador do Véu dos Malditos (R3-7.1)

`reinos/03-veu-dos-malditos/dados/` (21 arquivos `.txt`, 390 registros) gerados dos CSVs e docs por `plano/ferramentas/converter_reino3.py`; `validar_reino.py` generalizado (colisão contra reinos de número menor, `profissao` `—` aceita, V-34 por reino), sem alterar o resultado do reino 2 (relatório idêntico). Resultado: **0 erro, 22 avisos, 6 exceções** (`relatorio-validador.md`). Pontos ASSUMINDO: `assentamento.txt` vazio (8 lugares são cidades, D-66); `recurso.txt` qualitativo, números `?` (D-63); marcador dos fatos de relógio = `progresso inicial a/b`; `objetivo` = `agenda`. Opções: **A.** aprovar (recomendada); **B.** trocar o tratamento de assentamentos, recursos ou uma exceção (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-97 — Catálogo técnico: Véu dos Malditos no `.kt` (R3-7.2)

`MAPA_CATALOGO_KOTLIN.md` §7 (KV1 a KV8): o `.kt` não tem nenhuma entidade do Véu e não colide com nenhum dos 191 IDs de `dados/`; os 11 nativos do Véu não estão nos 16 povos do `.kt`; `feerico`, `fungal` e `plantae` são IDs de catálogo compartilhados (forasteiros no Véu); "neblina" e "pactos" do `.kt` são de Aeltharyn e não migram. O `.kt` é singleton de Aeltharyn e não vira carregador do reino 3. V-A1 conferida à mão (tríade completa nos 8 lugares), sem implementar no validador. Em `converter_reino3.py` os papéis de tríade passaram a vir do doc 01 §3 (0 erro, 22 avisos, como antes). Nenhum código do `.kt` alterado, nada instalado. Opções: **A.** aprovar (recomendada); **B.** implementar V-A1 no validador (altera `VALIDADOR_DE_REINO.md`, diga se quer). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-98 — Protótipo mínimo do Véu dos Malditos (R3-7.3)

`PROTOTIPO_MINIMO.md` §11: lugar Fio-Longo, liderança Ondra Fio-de-Sono e Morrovéu em uma linha (contexto B de 1.731 caracteres); condições A e B; roteiro de 8 turnos; métricas Q1 a Q10 e critério de aceite fixados antes de jogar. O vazamento é medido contra `fact.ondra-hospedes-que-ficam` (restrito), `fact.relogio-hospedes` (secreto) e as três frases de `nunca_diz` de Ondra; a armadilha do turno 8 é a duração (toda ligação é `desconhecida`). **ASSUMINDO:** como o reino não tem vilarejos nem cidade-mãe (D-66), o "vilarejo" é uma cidade de porte pequeno e a "cidade-mãe" é a capital, sem relação de dependência; escolha do recorte, da linha de Morrovéu e dos alvos é minha. Nenhuma sessão executada, nenhum lore novo. Fecha a Fase 7 (testes completos da fase em §11.10 do protótipo e no CHANGELOG). Opções: **A.** aprovar (recomendada); **B.** trocar o lugar, o NPC ou algum alvo (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-99 — Revisão final do Véu dos Malditos (R3-8.1)

`RELATORIO_R3_8_1.md`: portas 1 a 10 do checklist marcadas (1, 3 e 6 com ressalva ou lacuna explícita), 11 pendente do R3-8.2. Nenhuma contradição acidental nova; 8 contradições intencionais listadas para o doc 11; nenhum lore nem texto alterado. Pendência: 3 facções com rival `desconhecido` (Casa dos Fios, Turnos da Vigília, Vigias da Orla), que nenhum documento nomeia. Opções: **A.** aceitar como lacuna explícita do reino, a registrar no doc 11 (recomendada); **B.** o autor nomeia rivais (lore novo, com registro). **Recomendação: A.** **Aceita pela continuação ("Siga", 06/11/2026); reversível.**

## D-100 — Ata de fechamento 4 do Véu dos Malditos (R3-8.2)

Doc 11 do reino 3 (`11-fechamento-do-veu-dos-malditos.md`): resultado das oito fases, 11 de 11 portas, 7 contradições intencionais e as acidentais tratadas, decisões em aberto como limite explícito, critérios finais de uso e a ata. **O reino 4 (Marcas das Feras) não foi liberado:** só começa com a aprovação do autor. Opções: **A.** aprovar o fechamento 4 e liberar o reino 4, com D-99 e as lacunas listadas seguindo como limites explícitos (recomendada); **B.** aprovar o fechamento sem liberar o reino 4 ainda; **C.** pedir mudança em alguma decisão antes (diga qual). **Recomendação: A.** **Aprovada pelo autor em 07/11/2026 ("Vamos iniciar o reino 4"), opção A; ver D-101.**

## D-101 — Liberação do reino 4, Marcas das Feras (07/11/2026)

O autor aprovou o fechamento 4 do Véu dos Malditos e liberou o reino 4 ao pedir "Vamos iniciar o reino 4" (opção A de D-100). Efeitos: (1) o doc 11 do reino 3 passa de **[PROPOSTO]** a **[APROVADO]**; (2) D-67 a D-99 (aceitas pela continuação) e as lacunas listadas no doc 11 (rival `desconhecido` em 3 facções, duração de toda ligação, população numérica, titular do assento de Morrovéu, vigia da Provação e destino da guarda se o rei cair, custo da estabilização do Lord, necromancia na Provação e Lei dos Mortos) seguem como **limites explícitos** (aviso, não erro, no validador); (3) novo `plano/REFINAMENTO_VALE_PALANCAR.md`, com 37 módulos em 9 fases (prefixo `R4-`), já sob D-63 (sem camada de números) e D-64 (sem mestiços alocados); (4) novo `plano/CONTRATO_DE_FRONTEIRA_PALANCAR.md` (R4-0.4), com 14 fixos de Aeltharyn, 7 das Coroas e 8 do Véu, 14 proibições e 4 pontos abertos (P-1 a P-4); (5) o reino 5 (Pacto Humano) segue bloqueado; (6) nada de Aeltharyn, das Coroas ou do Véu foi alterado além do status da ata e dos índices. **Decidida pelo autor.**

## D-102 — Escala, nome, governo e pasta do reino 4 (R4-0.3)

Proposta do redator, **respondida pelo autor em 07/11/2026** (ver o fim desta decisão). Os reinos anteriores: monarquia élfica (1), confederação de dez cidades com Concílio de assento rotativo (2), rei conquistado em batalha sobre autoridades de pacto (3).

**1. Nome.** **A.** manter **Marcas das Feras** como nome do reino (é o nome de trabalho de M-04 e M-05; "Feras" nomeia o reino e os povos-animais, nunca uma sentença, como "Maldito" em W-01); **B.** "As Marcas" como nome do reino, com "Feras" só como rótulo dos povos-animais; **C.** outro nome, do autor. **Recomendação: A.**

**2. Escala.** **A.** dez cidades e vinte assentamentos, como os reinos 1 e 2 (o autor recusou essa escala para o reino 3: "não aqui e apenas uma área"); **B.** uma área só com **oito lugares principais**, todos fixos, como o Véu (D-66); **C.** uma área só com **oito lugares principais, fixos e sazonais** (vau, mercado, ponto de assembleia, território de clã; os sazonais têm ID permanente e mudam de posição com a estação de migração, só em termos qualitativos, sem rota nova nem duração). **Recomendação: C**, porque o reino é de campos abertos de migração (M-04 G-07) e de clãs (doc 01 §1 de Aeltharyn); se o autor preferir a regra do Véu, B.

**3. Governo.** **A.** **Assembleia de Clãs** que se reúne conforme a migração e escolhe uma Voz da Marca por ciclo, sem trono nem sede fixa (diferente do Concílio das Coroas porque o poder segue clãs e estações, não cidades); **B.** dois poderes: Assembleia de Clãs na paz e um **Capitão de Marcha** (chefia de migração e defesa) escolhido por prova ou aclamação, com limite de mandato; **C.** sem governo comum: clãs soberanos ligados por pactos de passagem e pasto, com um árbitro; **D.** outro, do autor, ou delegado ao redator (como em D-66: nome, povo, métodos, limites e sucessão entram no R4-1.3 e R4-1.4, com registro). **Recomendação: B**, que dá lugar natural a ≥ 2 generais e ≥ 2 heróis (D-22) sem repetir os modelos dos reinos 1 a 3.

**4. Pasta.** `reinos/04-vale-palancar/` (aceita pela continuação, reversível).

**5. Pontos abertos do contrato (§7).** **P-1** (a frase do doc 01 do Véu, "entra pelo leste das Marcas"): **A.** o reino 4 fica livre e a frase é lida como "o rio vem da direção das Marcas", com ajuste de **uma frase** no Véu só com aprovação; **B.** o reino 4 respeita "leste". **P-2** (a Descida "para as Marcas" × "—" para gnomo e construto na matriz M-05): **A.** gnomos e construtos só como visitantes de passagem onde a Descida chega, sem residir, até o autor decidir se a matriz muda (como D-73 fez no Véu); **B.** a matriz muda para "F" nas Feras. **Recomendação: A em ambos.**

**Não decide:** nenhum lugar, NPC, facção, história ou número; D-104 (convenções de nome do reino 4, M-10) vem com o R4-1.1.

**Resposta do autor (07/11/2026):** **nome:** **Vale Palancar** (opção C; o nome de trabalho "Marcas das Feras" fica só como rótulo antigo nos reinos 1 a 3, M-04 e M-05, sem reescrita); **escala:** "ok", ou seja, a recomendada (opção C: uma área só, oito lugares principais, fixos e sazonais); **governo:** "ok", ou seja, a recomendada (opção B: Assembleia de Clãs na paz e Capitão de Marcha para migração e defesa, mandato limitado); pasta `reinos/04-vale-palancar/` (aceita pela continuação). Limites que o redator se impõe: (1) o nome **não cria lore nem relevo**: M-04 mantém planaltos, savanas e campos de migração, e a origem e o sentido de "Palancar" entram no R4-1.5, com registro; (2) o nome não vira sentença sobre nenhum povo (W-01); (3) o plano e o contrato foram renomeados para `REFINAMENTO_VALE_PALANCAR.md` e `CONTRATO_DE_FRONTEIRA_PALANCAR.md`. **Decidida pelo autor.**

## D-103 — P-1 e P-2 do contrato de fronteira resolvidos (07/11/2026)

O autor pediu para resolver os dois problemas antes de iniciar o reino. **P-1:** a frase do doc 01 do Véu (linha 11), "do rio que entra pelo leste das Marcas até a foz", virou "do rio que chega das Marcas até a foz" (opção A, a recomendada); era a única ocorrência em todo o projeto; nenhum fato, ID ou dado mudou. **P-2:** a matriz de M-05 §4 **não muda**; M-05 §5 ganhou uma nota: gnomos e construtos (e anões no Véu) aparecem nos reinos onde a matriz dá "—" **só como visitantes de passagem** onde a Descida chega, sem residir (opção A, como o Véu já fazia desde D-73). Efeitos: contrato §7 atualizado, dados e validador do Véu inalterados. **Decidida pelo autor** (pedido de 07/11/2026; opções A recomendadas e aplicadas).

## D-104 — Convenções de nome do reino 4 (M-10 §9)

Nova seção 9 em `mundo/M-10-nomes-minimo.md`: molde de nome mantido; regras de não repetição ampliadas com os oito lugares do Véu; "Passo-" e "Calar" evitados; famílias de epíteto sugeridas para os 12 nativos, só como tom; "Palancar" e "Fera" não são sentença. Opções: **A.** aprovar; **B.** sem famílias sugeridas, só o molde e as regras de não repetição. **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-105 — Os oito lugares do Vale Palancar (R4-1.1)

Oito lugares propostos (VP-01 a VP-08) em `reinos/04-vale-palancar/01-reino-do-vale-palancar.md`: quatro **fixos** (Tabuleiro-Velho, o celeiro do planalto; Rasovau, o vau das trocas; Praia-Pardo, o pouso onde chega a Descida; Alvasal, o pouso do Ermo) e quatro **sazonais** (Terreiro-Maior, o ponto da Assembleia; Vazante, o acampamento da Marcha; Encosta-Aberta, o pouso das elevações; Gramal, o território dos rebanhos). Mais, no mesmo doc: a residência dos governantes (Assembleia no Terreiro-Maior, Capitão de Marcha na Vazante, nenhum com sede fixa) e o "Trato" como regra local de cada lugar (instituição, sem ID até o R4-1.4). Os 12 nativos e só forasteiros de M-05 §4 aparecem; gnomos e construtos só como visitantes em Praia-Pardo (D-103). **ASSUMINDO:** o mandato do Capitão atual está perto do fim (gancho de sucessão); a faixa de população dos sazonais é a da estação cheia; costume de sem arma no Fogo do Meio, a detalhar no R4-1.4. Opções: **A.** aprovar tudo; **B.** trocar nome, função, lenda ou povos de algum lugar (diga qual); **C.** mudar a divisão entre fixos e sazonais (diga como). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-106 — Rede política dos oito lugares do Vale Palancar (R4-1.2)

`reinos/04-vale-palancar/03-assentamentos-e-rede-politica.md`: por lugar, lugar-mãe, liderança (papel, sem nome), obrigação, interesse e tensão, derivados só do doc 01; posição relativa qualitativa; entrada dos clãs sem lugar fixo (Trato, reserva, Assembleia); pontos de contato. Sem assentamentos próprios (D-102); sem número, rota ou duração (D-63). **ASSUMINDO:** lugar-mãe dos sazonais é vínculo político de reserva e registro (Tabuleiro-Velho para Terreiro-Maior, Vazante e Gramal; Rasovau para Encosta-Aberta); nenhum clã nomeado aqui. Opções: **A.** aprovar; **B.** pedir vilas menores dentro de algum dos oito (diga qual e quantas); **C.** mudar lugar-mãe, obrigação, interesse ou tensão de algum lugar (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-107 — Os doze principais e as tríades do Vale Palancar (R4-1.3)

Parte 2 do doc 01: 12 principais (2 generais: Kharos Casco-de-Marcha, Capitão de Marcha, e Gruzk Estandarte-Baixo; 2 heróis vivos: Tessun Salto-Calado e Rukhan Retorno-Certo), 6 heróis lendários falecidos e a tríade herói, guardião e benfeitor dos 8 lugares. Todos os 12 nativos cobertos; povo em ID do catálogo; Capitão reside na Vazante (D-105). **ASSUMINDO:** faixas etárias, os feitos de Tessun e Rukhan e os traços de cada ficha; nenhum forasteiro como principal. Classe e profissão: R4-2.3. Opções: **A.** aprovar; **B.** trocar nome, povo, papel ou traço de algum NPC (diga qual); **C.** mudar quem é general ou herói vivo (diga como). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-108 — Instituições, pactos e facções do Vale Palancar (R4-1.4)

`reinos/04-vale-palancar/04-instituicoes-justica-pactos.md`: Assembleia de Clãs (um assento por Trato, maioria), Capitão de Marcha (prova ou aclamação, mandato limitado sem número, remoção, interregno), pactos de passagem e pasto, justiça e tributo qualitativos, oito Tratos como instituições e 14 facções (blocos de clãs, nunca de raça), com aliados e rivais preenchidos (2 rivais `desconhecido`, com motivo). **ASSUMINDO:** oito assentos e maioria, sanções qualitativas, renovação do pacto ao mudar o campo e um líder novo, Lumbo Pedra-de-Baixo (Leporino). Opções: **A.** aprovar; **B.** mudar a regra do Capitão, os assentos ou alguma facção (diga qual); **C.** fixar exílio ou pena de morte (lore novo, diga como). **Recomendação: A.** **Aceita pela continuação (nova sessão, 07/11/2026); reversível.**

## D-109 — História, nome "Palancar", leis, economia e fluxos do Vale Palancar (R4-1.5)

Parte 3 do doc 01 e novo `reinos/04-vale-palancar/05-economia-leis-e-fluxos.md`: quatro eras locais sem data (Campos Sem Marco, Primeiros Tratos, Palavra e Marcha, Tratos Disputados), origem e sentido de "Palancar" registrados (nome de reino, não de lugar; duas versões de lenda que só reaproveitam as do Terreiro-Maior; origem `desconhecida`), lendas dos oito lugares com era e o que fica `desconhecido`, cronologia relativa em seis passos, defesa e vulnerabilidades (nenhum inimigo externo afirmado); dez leis (L-01 a L-10) como costume dos Tratos, cada uma com lacuna; economia qualitativa dos oito lugares; os três fluxos de M-05 §5 só do lado do Vale (Descida chega a Praia-Pardo, Rota do Sal parte de Alvasal, Corredores Largos saem por Encosta-Aberta) conferidos contra a matriz. **ASSUMINDO:** as dez leis como costume derivado de fatos já aprovados; as duas versões do nome; metal da forja sem origem; pergunta do contrato §6 mantida aberta. Opções: **A.** aprovar; **B.** decidir o que o Vale oferece à Descida e à Rota do Sal (gado, couro, sal; exige registro nos reinos vizinhos); **C.** fixar crime grave, exílio ou pena de morte (lore novo, diga como). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-110 — Matriz de povos do Vale Palancar e linhagens próprias (R4-1.6, P-3)

`reinos/04-vale-palancar/10-matriz-povos-nativos-cidades-e-forasteiros.md`: 12 nativos em ID do catálogo com lugares (idênticos ao doc 01 §3, conferido por script); 6 sub-raças do catálogo (`orcano-das-estepes`, `felideo-das-dunas`, `centauro-das-pradarias`, `goblin-das-sucatas`, `legionario-dos-estandartes`, `homem-raposa-dos-vales`), 2 existentes não usadas por falta de lugar que as sustente; lugares × povos (8 de 8 com ao menos 2 nativos); 25 forasteiros de M-05 §4 (17 com lugar, 8 sem lugar fixo; gnomo e construto só visitantes, D-103); papéis por povo, com lacunas de herói, guardião e benfeitor deixadas abertas; mestiços só pela regra D-64. **P-3 proposto:** o Vale usa a outra sub-raça de cada par compartilhado com Aeltharyn, sem parentesco. Nenhum NPC novo. **ASSUMINDO:** onde cada sub-raça aparece, os motivos dos forasteiros e a escolha das sub-raças de P-3. Opções: **A.** aprovar; **B.** nome de linhagem próprio para Orcano, Felídeo Errante ou Centauro (diga qual; exige registro no contrato §7); **C.** criar heróis lendários novos para os 5 povos sem herói, como no Véu (cinco NPCs novos, diga quais). **Recomendação: A.** **Aceita pela continuação (nova sessão, 07/11/2026); reversível.**

## D-111 — Mistérios, relógios, conflitos e oportunidades do Vale Palancar (R4-1.7)

Parte 4 do doc 01 e novo `reinos/04-vale-palancar/06-modelo-operacional-e-aventuras.md`: 10 mistérios (MPL-01 a MPL-10; Tabuleiro-Velho e Terreiro-Maior com dois, os demais lugares com um) com 4 hipóteses abertas, vetos ("a verdade não pode ser") e provas possíveis; 8 relógios (`clock.reserva-em-ano-ruim`, `clock.dupla-cobranca`, `clock.desembarque`, `clock.cisterna`, `clock.sucessao-do-capitao`, `clock.campo-da-marcha`, `clock.guia-das-trilhas`, `clock.capim-disputado`; progresso 2/6 a 3/6); 8 conflitos com causa concreta (pasto, água, passagem, sucessão de chefia, caça, tributo de caravana) e 8 oportunidades. Cada mistério parte de rumor, suspeita ou problema já escrito; nenhum explica a Neblina Longa, o Mistério D, o Mistério E ou o Conflito G, nem dá causa à Descida ou "verdade" a mistério do Véu; Aeltharyn, Coroas e Véu só em vetos. **ASSUMINDO:** o número de relógios (oito, um por lugar), o progresso inicial, "o que trava", "quem paga" e "efeito da inação"; os prefixos MPL, CP, CFP e OP; o doc 11 do plano segue como ata de fechamento (R4-8.2), não como entrega do R4-1.7. Opções: **A.** aprovar; **B.** trocar mistério, relógio, progresso inicial ou o número de relógios (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-112 — Registro mestre do Vale Palancar (R4-2.1)

`reinos/04-vale-palancar/12-registro-mestre.csv` e `.md` (formato de D-21, com `povo_id`): 47 entidades (8 lugares, 14 facções, 25 pessoas; 19 vivas, 6 lendárias falecidas). 13 pessoas sem ficha (6 da tríade, 6 heróis lendários, Lumbo Pedra-de-Baixo) ganharam ID pelo nome já escrito, sem pessoa nova. **ASSUMINDO:** `id_catalogo` vazio, `cidade` com o lugar principal, Lumbo em Rasovau, slugs derivados do nome. Opções: **A.** aprovar; **B.** trocar o slug de algum ID (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-113 — Facções e lugares como dados do Vale Palancar (R4-2.2)

`12-facoes.csv` (14 facções, 11 colunas) e `12-assentamentos.csv` (8 lugares, 6 colunas), no molde dos reinos 2 e 3, derivados só dos docs 03 e 04. Liderança, objetivo, métodos, aliados, rivais e presença vêm do doc 04 §9; `tensao`, `obrigacao` e `interesse_local` vêm do doc 03. **ASSUMINDO:** `recursos` é `desconhecido` nas 14 (o doc 04 não lista recursos de facção); `cidade_mae` é o lugar-mãe do doc 03 (`—` nos fixos); `lideranca_id` do lugar é o chefe do Trato (doc 04 §8) e `desconhecido` em Rasovau e Encosta-Aberta, porque o Trato está em disputa. **Correção:** o doc 04 dizia 3 rivais `desconhecido`; são 2 (F-01 e F-10). Opções: **A.** aprovar; **B.** preencher `recursos` por facção (lore novo, diga como). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-114 — Alinhamento do Vale Palancar ao catálogo (R4-2.3)

`referencia/racas-e-povos/ALINHAMENTO_PALANCAR.md`: 12 nativos, 6 sub-raças e 25 forasteiros conferidos contra `personagem-catalogo.json` (0 divergência de ID ou nome); atribuição de raça, sub-raça, classe e profissão às 25 pessoas (0 ID fora do catálogo; toda sub-raça no lugar que o doc 10 §2 a coloca; P-3 mantido). **ASSUMINDO:** classe e profissão como leitura editorial do papel escrito, com profissão `—` onde não há ofício; Kharos como `comandante` (não há classe de capitão). Opções: **A.** aprovar; **B.** trocar atribuições (diga quais). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-115 — Conferência cruzada do Vale Palancar (R4-2.4)

`reinos/04-vale-palancar/13-conferencia-cruzada.md`: aliados e rivais (0 conflito, 0 assimetria), idades (sem número), 7 acúmulos de cargo como intencionais (2 deles, Moura e Aruun, declarados agora no doc 04), quem sabe o quê nos 10 mistérios, 12 contradições e pontos abertos (CI-01 a CI-12) e as conferências da Fase 2. **ASSUMINDO:** os acúmulos de cargo são desenho intencional, como nos reinos 2 e 3. Opções: **A.** aprovar; **B.** desfazer algum acúmulo ou fechar uma lacuna (diga qual). **Recomendação: A.**

## D-116 — Rotas e mapa esquemático do Vale Palancar (R4-3.1)

`reinos/04-vale-palancar/07-registro-operacional-de-rotas.md` e `07-mapa-esquematico.md`: 9 ligações (8 documentadas sem duração, 1 sugerida por dependência), 8 acessos de lugares, 6 linhas de fronteira e mapa em Mermaid. Toda duração é `desconhecida`: os docs 01, 03 e 05 não trazem "curta" nem "longa" para nenhum percurso, e nada foi inventado. Ligações dos sazonais valem para o campo em que estiverem. Ermo de Sal ↔ Vale, Vale ↔ Aeltharyn, Coroas e Véu seguem sem rota (contrato §5 itens 4 e 8). **ASSUMINDO:** os vínculos de lugar-mãe e os elos de fluxo do doc 03 §4 como ligações "documentadas sem duração"; Rasovau ↔ Encosta-Aberta como "sugerida por dependência" (só lugar-mãe). Opções: **A.** manter tudo `desconhecida` (recomendada); **B.** o autor fornece "curta" ou "longa" para as 9 ligações; **C.** o autor autoriza propostas marcadas como ASSUMINDO. **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-117 — Demografia do Vale Palancar (R4-3.2)

`reinos/04-vale-palancar/12-demografia.md` e `.csv` (8 lugares, formato dos reinos 2 e 3): faixas de porte relativo já presentes no doc 01 (2 grandes, 3 médias, 3 pequenas; ASSUMINDO, D-63), com evidência e confiança; regra de **não somar**; sazonais variam com o campo e a estação; flutuantes, rebanhos, carroças e proporção entre povos fora de qualquer faixa. 0 número, 0 censo, 0 diferença com o doc 01. Opções: **A.** aprovar; **B.** trocar a faixa de algum lugar (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-118 — Recursos e matriz material do Vale Palancar (R4-3.3)

`reinos/04-vale-palancar/08-registro-operacional-de-recursos.md` e `09-matriz-material-dos-lugares.md`: 8 lugares com rica em, pobre em e depende de (só do doc 01, doc 03 e doc 05 §2), 12 dependências (8 críticas, todas com fonte; nenhum lugar isolado), 4 fluxos qualitativos (Descida, Rota do Sal, Corredores Largos, troca interna). 0 número, nível, unidade ou preço; nada sobre a origem do combustível ou do alimento de Brasalume; a pergunta do contrato §6 segue aberta. **ASSUMINDO:** o vínculo de lugar-mãe como dependência de apoio (DR-01 a DR-04); escala qualitativa, sem a escala de 0 a 5 do reino 2. Opções: **A.** aprovar (recomendada); **B.** escala numérica de 0 a 5 (e quem atribui os níveis); **C.** trocar uma dependência ou termo (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-119 — Calendário e estações do Vale Palancar (R4-3.4; fim da Fase 3)

Doc 01, parte 5, `M-02` §8 e `M-10` §10: 12 luas numeradas, quatro estações (Pasto Magro, Rebrota, Pasto Alto, Reunião dos Rebanhos, nomes provisórios), cinco Dias de Marco, **Contagem dos Marcos** (época `desconhecida`); início do jogo em Pasto Magro, Lua 1, dia 1 do **ano 611**, igual a Silêncio, Lua 1, dia 1 do ano 742 de Aeltharyn (deslocamento de 131, ASSUMINDO, sem fonte, reversível); tabela de conversão de 8 linhas; agenda sazonal só de condição (tudo ASSUMINDO); migração, reunião da Assembleia, fim do mandato do Capitão, caravanas, Descida e Corredores Largos ficam `desconhecidos`. Nenhum evento do Vale datado em anos de outros reinos. Opções: **A.** aprovar (recomendada); **B.** trocar nomes de estação, dos dias de virada, da contagem ou o deslocamento (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-120 — Fichas completas do Vale Palancar (R4-4.1)

`reinos/04-vale-palancar/12-fichas.md` e `12-fichas.csv`: 12 de 12 principais com os 17 campos do molde dos reinos 2 e 3 (0 vazio), 12 agendas e 12 pressões distintas, povo, sub-raça, classe e profissão de `ALINHAMENTO_PALANCAR.md`, relógio ligado a cada um (`06`), aliados e rivais da facção (doc 04). 6 acúmulos de cargo marcados como "acúmulo intencional proposto" (CI-07; o sétimo, Aruun, não tem ficha completa). Traços, medos e condições de evolução são ASSUMINDO; coluna `camadas` `—` até R4-6.2. 0 pessoa, lugar, facção ou número novo. Opções: **A.** aprovar; **B.** trocar traço, medo ou condição de evolução de alguma ficha (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-121 — Competências do Vale Palancar pelo catálogo (R4-4.2)

`reinos/04-vale-palancar/12-competencias.md` e `.csv`: 25 pessoas do registro mestre (12 principais, 7 sem ficha, 6 heróis lendários) com raça, sub-raça, classe e profissão de `ALINHAMENTO_PALANCAR.md`, capacidades e limites só do que os docs já dizem; 0 ID fora do catálogo, 0 sub-raça com raça-mãe errada, 0 capacidade sem limite, 0 habilidade ou número inventado (o catálogo não liga habilidade a classe ou profissão). **ASSUMINDO:** classe e profissão como leitura editorial; limite padrão dos heróis lendários; limite "desconhecido" para os 7 sem ficha. Opções: **A.** aprovar; **B.** trocar classe, profissão ou limite de alguém (diga quem). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-122 — Relações e conhecimento do Vale Palancar (R4-4.3)

`reinos/04-vale-palancar/12-relacoes.md`, `12-relacoes.csv` (81 relações: 22 alianças, 12 rivalidades, 12 dívidas, 8 tensões, 15 vínculos de cargo, 12 medos; **0 afeto**) e `12-conhecimento.csv` (66 linhas, 25 de 25 pessoas). Tudo derivado dos docs 01, 03, 04 e das fichas; toda relação com fonte; 0 ID fora do registro, 0 aliado-rival. **ASSUMINDO:** origem de dívida e tensão em Rasovau e Encosta-Aberta no guardião da tríade (o chefe do Trato é `desconhecido`); apoio ao lugar-mãe como dívida; medos das fichas. Opções: **A.** aprovar; **B.** autorizar afeto ou dívida entre pessoas, marcados ASSUMINDO (diga os pares). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-123 — Voz e ganchos de diálogo do Vale Palancar (R4-4.4; fim da Fase 4)

`reinos/04-vale-palancar/12-voz.md` e `12-voz.csv`: 12 de 12 principais com tom, 2 a 3 frases-modelo (instrução de tom, ASSUMINDO) e `nunca_diz`; nenhuma frase revela suspeita ou desconhecido. Fecha a Fase 4 (testes completos em `12-voz.md`: 0 falha). Opções: **A.** aprovar; **B.** trocar tom ou frase de alguém (diga quem). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-124 — Relógios do Vale Palancar (R4-5.1)

`reinos/04-vale-palancar/12-relogios.md` e `12-relogios.csv`: os 8 relógios do doc 06 (D-111) com gatilhos observáveis (1 a 2 por relógio, reescrita do efeito da inação), custo por segmento, decisor no segmento 5 (Trato do lugar, Assembleia ou Capitão, doc 04 §3 e §6) e efeito da inação. 0 relógio, NPC, lugar ou fato novo; 0 entidade fora do registro; nenhum relógio envolve outro reino. Opções: **A.** aprovar; **B.** trocar gatilho, decisor no segmento 5 ou progresso de algum relógio (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-125 — Eventos-tipo e cadeias causais do Vale Palancar (R4-5.2)

`reinos/04-vale-palancar/12-eventos.md` e `12-eventos.csv`: 12 eventos (8 completam um relógio, 2 só dão gatilho, 2 sem relógio), cada um com gatilho observável, cadeia (elos do que os docs já dizem; ordem ASSUMINDO), lugares, termos do doc 09, pessoas e facções e rotas entre lugares do Vale que existem no doc 07. 0 ID fora do registro, 0 relógio fora de `12-relogios.csv`, 8 de 8 relógios cobertos; 0 efeito em outro reino, 0 rota com ponta fora do Vale, 0 duração. Opções: **A.** aprovar; **B.** trocar, tirar ou somar um evento (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-126 — Sementes de aventura do Vale Palancar (R4-5.3)

`reinos/04-vale-palancar/13-sementes-de-aventura.md` e `.csv`: 24 sementes (8 lugares × pequena, média e grande; as médias são OP-01 a OP-08 do doc 06), cada uma com ameaça (P 1, M 2–3, G 3–4, ASSUMINDO), agentes vivos do registro, abordagens possíveis sem solução predefinida, "se ninguém age" e ligação a relógio e evento (as 16 médias e grandes). 0 agente fora do registro ou falecido, 0 relógio ou evento inexistente, 0 mistério, lei ou lacuna resolvido, 0 efeito em outro reino. Opções: **A.** aprovar; **B.** trocar, tirar ou somar uma semente (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-127 — Reação do Vale Palancar ao jogador (R4-5.4)

`reinos/04-vale-palancar/13-reacao-ao-jogador.md`, `13-reputacao.csv` (22 linhas: 8 lugares e 14 facções) e `13-canais.csv` (11 canais): o que cada lugar e facção valoriza ou pune, estados de procurado, ato × lei (L-01 a L-10) × autoridade, favor e reparação (doc 04 §6, qualitativa) e intermediários com rotas só do doc 07. Sem número de fama, preço ou prazo (do motor, MP-07); sem caçador de recompensa; nenhuma reação depende de raça; 0 ID fora do registro, 0 rota fora do doc 07, 0 efeito em outro reino. Opções: **A.** aprovar; **B.** trocar ou tirar uma reação ou canal (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-128 — Mistérios com limites do Vale Palancar (R4-5.5; fim da Fase 5)

`reinos/04-vale-palancar/13-misterios-limites.md` e `.csv`: os 10 mistérios do doc 01 parte 4 (D-111) como dados (`mist.mpl-01` a `mist.mpl-10`): fatos confirmados, 4 hipóteses abertas, "a verdade não pode ser", evidência (duas provas independentes), entidades vivas do registro e ligação a relógio e evento existentes. Texto idêntico ao doc 01; 0 mistério, hipótese ou veto novo; 0 resolução de mistério de outro reino. Fecha a Fase 5 (testes completos no próprio doc: 0 falha, 0 regressão). Opções: **A.** aprovar; **B.** trocar uma hipótese, veto ou ligação (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-129 — Resumos de contexto do Vale Palancar (R4-6.1)

`reinos/04-vale-palancar/14-resumos-de-contexto.md` e `.csv`: 55 entidades (8 cidades, 14 facções, 8 relógios, 25 pessoas), 1 resumo cada com ID (máximo 500 caracteres) e vizinhança de uma linha (máximo 300). Só o que os docs e as fichas escrevem; sem suspeita, desconhecido ou `nunca_diz`; sem duração, número de população ou termo de outro reino; `camada_max` provisória até R4-6.2. Opções: **A.** aprovar; **B.** trocar o texto de uma entidade (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-130 — Camadas de informação do Vale Palancar (R4-6.2)

`reinos/04-vale-palancar/14-camadas-de-informacao.md` e `.csv`: 42 fatos (24 dos 12 principais, 10 dos mistérios, 8 relógios) em quatro camadas (11 públicos, 11 locais, 6 restritos, 14 secretos), cada restrito e secreto com caminho para descobrir; critério ASSUMINDO igual ao dos reinos 1 a 3. Liga as fichas N-01 a N-12 (coluna `camadas`) e torna definitiva a `camada_max` dos resumos (0 marcador de segredo em resumo local). 0 fato, NPC ou prova novos; 0 resposta de mistério. Opções: **A.** aprovar; **B.** trocar a camada ou o caminho de um fato (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-131 — Cenas de teste do Vale Palancar (R4-6.3)

`reinos/04-vale-palancar/15-cenas-de-teste.md` e `.csv`: 7 cenas (Vazante; Rasovau; Terreiro-Maior; Gramal; viagem pelos Campos de Tabuleiro-Velho ao Gramal; chegada da Descida em Praia-Pardo, só do lado do Vale; Encosta-Aberta, só em tom de rumor), cada uma com \"entra\" (até 2140 caracteres; só resumos, fatos públicos e locais, voz, semente pequena, rumor e regra) e \"não entra\". Escolhas ASSUMINDO: (e) rota Tabuleiro-Velho–Gramal sem duração; (f) Praia-Pardo em vez da Rota do Sal. 0 segredo, relógio, `nunca_diz`, falecido ou termo de outro reino em \"entra\". Opções: **A.** aprovar; **B.** trocar uma cena ou o lugar de teste (diga qual). **Recomendação: A.** **Aceita pela continuação (07/11/2026); reversível.**

## D-132 — Contrato anti-invenção do Vale Palancar (R4-6.4; fim da Fase 6)

Seção 9 de `reinos/04-vale-palancar/06-modelo-operacional-e-aventuras.md`: verificação antes de aceitar proposta (9 perguntas), quatro classes (fixo, livre, exige registro, proibido), lista proibida (Capitão e sucessão, Tratos em disputa, crime grave, 9º lugar, guerra e inimigo, mistérios, números, efeito mecânico, relógio sem gatilho, Conflito G, Neblina Longa, Mistérios D e E, rotas e durações, Descida, Brasalume, ano de Aeltharyn, nomes dos reinos 1 a 3, Pacto Humano), 8 casos pedidos e teste rápido de 4 perguntas. Herda os contratos dos reinos 1 a 3 e o contrato de fronteira sem alterá-los; contagens conferidas (8 lugares, 14 facções, 25 pessoas, 8 relógios, 10 mistérios, 10 leis, 12 eventos, 24 sementes, 42 fatos). Opções: **A.** aprovar; **B.** mover item entre classes ou trocar caso (diga qual). **Recomendação: A.** **Aceita pela continuação (07/11/2026); reversível.**

## D-133 — Dados e validador do Vale Palancar (R4-7.1)

`plano/ferramentas/converter_reino4.py` (adaptado de `converter_reino3.py`) gera `reinos/04-vale-palancar/dados/` (21 `.txt`, 405 registros, formato D-21) só a partir dos CSV e docs já escritos; `validar_reino.py` roda no reino 4: **0 erro, 11 avisos, 2 exceções** (V-23 e V-44: o doc 07 deixa Alvasal sem rota interna e os lugares em dois grupos; não se inventa rota). Ajustes: capital `—`; validador aceita o prefixo `mist.` em `entidade` de `fato.txt` e ganhou mensagem própria em V-34; os relatórios dos reinos 2 e 3 saem idênticos. Recursos de eventos sem correspondente entram com papel `?` (ASSUMINDO). Opções: **A.** aprovar; **B.** ligar Alvasal ao resto por rota (exige decisão do autor e registro no doc 07). **Recomendação: A.** **Aceita pela continuação (07/11/2026); reversível.**

## D-134 — Catálogo técnico: Vale Palancar no `.kt` (R4-7.2)

Seção 8 de `plano/MAPA_CATALOGO_KOTLIN.md` (KP1 a KP9), só leitura e busca de texto: 0 entidade do Vale no `.kt`, 0 colisão de ID (69 do `.kt` contra 48 do reino 4), 12 nativos fora dos povos do `.kt`, linhagens do Vale distintas das de Aeltharyn (P-3 confirmado), tríade conferida à mão (8 de 8) e quatro lacunas do esquema listadas (regime fixo ou sazonal, governo sem rei e sem capital, Tratos como instituição, divergências só de Aeltharyn). Nada instalado, nenhum código alterado. Opções: **A.** aprovar; **B.** pedir campo novo no esquema para alguma lacuna (diga qual). **Recomendação: A.** **Aceita pela continuação (07/11/2026); reversível.**

## D-135 — Protótipo mínimo do Vale Palancar (R4-7.3; fim da Fase 7)

Seção 12 de `plano/PROTOTIPO_MINIMO.md`, **nenhuma sessão executada**: lugar pequeno Alvasal, NPC Gruzk Estandarte-Baixo (resumo e voz) e Rasovau em uma linha (ASSUMINDO, fora das sete cenas de R4-6.3); contexto B de 1.840 caracteres, só de camadas públicas; condição A sem mundo; 8 turnos; métricas Q1 a Q10; critério de aceite (falha dura em vazamento ou invenção) escrito antes da sessão. Armadilhas: duração até Rasovau (o doc 07 não tem rota interna para Alvasal), preferência na cisterna (lacuna de lei) e segredo de Gruzk. Fecha a Fase 7 (testes completos: 0 falha, 0 regressão). Opções: **A.** aprovar; **B.** trocar lugar, NPC ou linha de mercado (diga qual). **Recomendação: A.** **Aceita pela continuação (07/11/2026); reversível.**

## D-136 — Revisão final do Vale Palancar (R4-8.1)

`RELATORIO_R4_8_1.md`: portas 1 a 10 do checklist marcadas (1, 3 e 6 com ressalva ou lacuna explícita), 11 pendente do R4-8.2. Nenhuma contradição acidental nova; 10 contradições intencionais listadas para o doc 11; nenhum lore nem texto alterado. Pendência: 2 facções com rival `desconhecido` (Guardas da Marcha, Vigias da Cisterna), com motivo no doc 04. Opções: **A.** aceitar como lacuna explícita do reino, a registrar no doc 11 (recomendada); **B.** o autor nomeia rivais (lore novo, com registro). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-137 — Ata de fechamento 5 do Vale Palancar (R4-8.2)

Doc 11 do reino 4 (`11-fechamento-do-vale-palancar.md`): resultado das oito fases, 11 de 11 portas, 10 contradições intencionais e as acidentais tratadas, decisões em aberto como limite explícito, critérios finais de uso e a ata. **O reino 5 (Pacto Humano) não foi liberado:** só começa com a aprovação do autor. Opções: **A.** aprovar o fechamento 5 e liberar o reino 5, com D-136 e as lacunas listadas seguindo como limites explícitos (recomendada); **B.** aprovar o fechamento sem liberar o reino 5 ainda; **C.** pedir mudança em alguma decisão antes (diga qual). **Recomendação: A.** **Aprovada pelo autor em 07/11/2026 ("Siga reino 5"), opção A; ver D-138.**

## D-138 — Liberação do reino 5, Pacto Humano (07/11/2026)

O autor aprovou o fechamento 5 do Vale Palancar e liberou o reino 5 ao pedir "Siga reino 5" (opção A de D-137). Efeitos: (1) o doc 11 do reino 4 passa de **[PROPOSTO]** a **[APROVADO]**; (2) D-104 a D-136 (aceitas pela continuação) e as lacunas listadas no doc 11 (rival `desconhecido` em 2 facções, duração de toda ligação, população numérica, sucessão do Capitão de Marcha, preferência na cisterna, protótipo não jogado, P-4) seguem como **limites explícitos** (aviso, não erro, no validador); (3) novo `plano/REFINAMENTO_PACTO_HUMANO.md`, com 37 módulos em 9 fases (prefixo `R5-`), já sob D-63 e D-64; (4) o nome "Pacto Humano" segue provisório: nome, escala, governo e o encaixe do Estelar ficam para o autor no R5-0.3 (D-139, a abrir); (5) contrato de fronteira e pasta do reino 5 ainda não existem (R5-0.4 e R5-1.1). Nada dos reinos 1 a 4 é alterado. **Decidida pelo autor em 07/11/2026.**

## D-139 — Nome do reino 5: Azeroth (R5-0.3, parte b)

O autor decidiu em 07/11/2026 que o reino humano se chama **Azeroth**. "Pacto Humano" vira nome de trabalho antigo (os reinos 1 a 4, M-04 e M-05 ainda o usam; os textos deles não mudam, como ocorreu com "Marcas das Feras"). Pasta futura: `reinos/05-azeroth/`. O nome é de reino e **não cria lore nem relevo**: M-04 mantém planícies férteis, rios largos e costa meridional; origem e sentido do nome ficam para o R5-1.5, com registro. Conferido: 0 ocorrência prévia de "Azeroth" no projeto. Risco registrado: "Azeroth" é nome de mundo de propriedade alheia (jogo comercial); não afeta o trabalho editorial, mas pesa se o conteúdo for publicado ou implementado no RPG Solo (opção de renomear). **Ainda abertos no R5-0.3:** escala, governo e o encaixe do Estelar. **Decidida pelo autor em 07/11/2026 (nome).**

## D-140 — Contrato de fronteira do reino 5, Azeroth (R5-0.4)

Escrito `plano/CONTRATO_DE_FRONTEIRA_PACTO_HUMANO.md`, antes do R5-0.3 (o plano permite). Fixa 29 fatos dos reinos 1 a 4 que tocam o reino 5, o que ele pode afirmar, 16 vedações e 7 ganchos. Efeitos: (1) Aeltharyn e o Véu são os únicos vizinhos plenos; Coroas e Vale não o tocam (elo "nenhuma", sem rota); (2) só As Costas e As Fugas do Véu chegam ao reino 5; (3) o reino 5 não explica o Mistério C, a Neblina Longa, os Mistérios D e E nem o Conflito G, não decide motivo, número ou começo das Fugas e não aplica a Lei de Proteção de Refugiados de Aeltharyn; (4) linhagens de Pequenino e Abissal Costeiro próprias (regra de D-110). **Abertos para o autor:** P-1 (Estelar), P-6 (escala e governo, ambos no R5-0.3), P-2 (qual pântano) e P-3 (fronteira com o Véu na foz do Argente). Nada dos reinos 1 a 4, `mundo/` ou `referencia/` é alterado. **Aceita pela continuação; P-1, P-2, P-3 e P-6 aguardam o autor.**

## D-141 — Escala, governo, Estelar e nomes de Azeroth (R5-0.3)

Decisões do autor em 07/11/2026 (por escolha): **governo = várias coroas rivais**; **escala e Estelar = "decida você"** (ASSUMINDO). Efeitos: (1) **10 lugares principais**, qualitativos (D-63), atendendo V-30; (2) **3 coroas rivais** (ASSUMINDO o número), cada uma com governante e herdeiro (V-34 por coroa), residência explícita e aliados e rivais preenchidos (D-19, D-23, D-99); quem domina, a origem da rivalidade e os nomes das coroas ficam para R5-1.1 a R5-1.5, sem envolver outros reinos; (3) **Estelar mantido como nativo** (a matriz de M-05 não muda); (4) nova seção 11 de `mundo/M-10-nomes-minimo.md` (molde mantido, não repetição ampliada, "Azeroth" e "Pacto" reservados, famílias de epíteto só como tom). Contrato de fronteira: P-1 e P-6 decididos. **Fase 0 concluída.** Opções para os ASSUMINDO: **A.** manter; **B.** o autor fixa outro número de lugares, de coroas ou outro papel do Estelar (reversível antes da F1). **Recomendação: A.** **Governo decidido pelo autor; escala, número de coroas e Estelar aceitos pela continuação, reversíveis.**

## D-142 — Os dez lugares de Azeroth (R5-1.1)

Dez lugares propostos (AZ-01 a AZ-10) em `reinos/05-azeroth/01-reino-de-azeroth.md`, todos fixos, sob três coroas rivais (D-141): **Coroa do Trigal** (Campoalvo, capital; Vaudouro, o mercado dos rios; Torrefronte, a guarda do limite), **Coroa do Cais** (Alvamar, capital; Enseada-Vigia, o porto das Costas; Concharia, o estaleiro dos recifes), **Coroa do Vento** (Torreclara, capital; Estrelado, o observatório; Pouso-das-Brisas, a estação dos sinais) e **Meiovau**, cidade livre da Mesa das três chaves, onde as coroas se encontram e chegam os que fogem do Véu. Mais: rios Trigueiro e Corre-Largo (só relevo), residência dos governantes (um em cada capital) e órgãos locais como instituição, sem ID até o R5-1.4. Só os 9 nativos e forasteiros de M-05 §4; mestiços 0; foz do Argente e fronteira com o Véu não afirmadas. **ASSUMINDO:** todos os nomes, as faixas de população, a divisão em três coroas por vocação (grão, mar, sinais) e Meiovau como lugar comum. Opções: **A.** aprovar tudo; **B.** trocar nome, função, lenda ou povos de algum lugar ou coroa (diga qual); **C.** mudar a divisão entre as coroas ou o número de lugares (diga como). **Recomendação: A.** **Aceita pela continuação (\"Siga\", 07/11/2026); reversível.**

## D-143 — Rede política dos lugares de Azeroth (R5-1.2)

`reinos/05-azeroth/03-assentamentos-e-rede-politica.md`: ficha política dos dez lugares (lugar-mãe, liderança por papel, obrigação, interesse, tensão), posição relativa qualitativa, vínculos das coroas e de Meiovau e a rede entre lugares. Lugar-mãe é a capital da coroa a que o lugar responde (Campoalvo: Vaudouro e Torrefronte; Alvamar: Enseada-Vigia e Concharia; Torreclara: Estrelado e Pouso-das-Brisas); capitais e Meiovau ficam com `—`. Tudo deriva do doc 01, sem fato novo; sem assentamentos menores (V-31 como aviso). **ASSUMINDO:** lugar-mãe como vínculo de apoio e tributo, não de posição. Opções: **A.** aprovar; **B.** trocar o lugar-mãe de algum lugar; **C.** pedir vilas menores. **Recomendação: A.** **Aceita pela continuação (\"Siga\", 07/11/2026); reversível.**

## D-144 — NPCs principais e tríades de Azeroth (R5-1.3)

`reinos/05-azeroth/01-reino-de-azeroth.md`, parte 2: **12 principais** (3 governantes e 3 herdeiros, um par por coroa; 2 generais, Solenne Vigília-Longa e Dorgan Escudo-Lento; 2 heróis vivos, Nerella Canto-da-Barra e Garvane Remo-Firme; a guardiã e o benfeitor de Meiovau, Tolla Mesa-Posta e Auriven Porta-Acesa), **8 heróis lendários falecidos** (só o que as lendas do doc 01 sustentam) e a **tríade herói, guardião e benfeitor dos dez lugares**; 36 pessoas nomeadas, os 9 nativos cobertos, mestiços 0. Governantes: Odile Medida-Cheia (Trigal, Campoalvo), Corlan Cais-Primeiro (Cais, Alvamar), Brevo Rumo-Alto (Vento, Torreclara); herdeiros: Tomlin Horta-Larga, Rilla Espuma-Fina, Astrel Noite-Clara. Governantes e herdeiros ficam fora da tríade (sem cargo duplicado). Raça não determina cargo; linhagem própria de Humano, Pequenino e Abissal Costeiro (contrato P-4). Nenhum NPC de outro reino entra ou age a mando de Azeroth; regra de sucessão, facções, aliados e rivais ficam para o R5-1.4. **ASSUMINDO:** todos os nomes, faixas etárias, o feito de cada herói vivo, a residência dos herdeiros nas capitais e que herdeiro é \"reconhecido\", sem regra de sucessão. Opções: **A.** aprovar; **B.** trocar nome, povo ou papel de alguém (diga quem); **C.** mudar quem é general, herói vivo ou herdeiro. **Recomendação: A.** **Aceita pela continuação (\"Siga\", 07/11/2026); reversível.**

## D-145 — Instituições, justiça, acordos e facções de Azeroth (R5-1.4)

`reinos/05-azeroth/04-instituicoes-justica-pactos.md`: governo das três coroas (governante, herdeiro e conselho cada: Conselho dos Celeiros, Alfândega das Marés, Casa dos Sinais), **sucessão distinta por coroa** (Trigal: indicação e reconhecimento pelo Conselho; Cais: reconhecimento conjunto da Alfândega e da Casa do Cais; Vento: escolha do governante confirmada na Torre-Mestra), **Mesa de Meiovau** (três chaves, presidente sem voto, arbitra pontos concretos e **não decide a precedência**), **acordos** entre coroas (o termo "pacto" não é usado), justiça por caso, **Costume da Porta** (acolhida local de Meiovau, própria, sem motivo nem origem perguntados, nenhuma coroa leva ou expulsa um chegado sozinha; prazo e sustento em disputa), tributo qualitativo e **15 facções** com aliados e rivais preenchidos (matriz simétrica, nenhum aliado também rival; 1 rival `desconhecido`, F-07, com motivo, a listar no doc 11). Líderes novos: Pelmo Balança-de-Porto e Ruvane Chave-Contada (Humanos). Nada toca Aeltharyn, o Véu, as Coroas ou o Vale. **ASSUMINDO:** as três regras de sucessão, o funcionamento da Mesa, o Costume da Porta, as sanções qualitativas e os dois líderes novos. Opções: **A.** aprovar; **B.** trocar regra de sucessão, facção, líder ou aliança (diga qual); **C.** dar à Mesa poder de decidir a precedência. **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-146 — História, defesa, leis, economia e fluxos de Azeroth (R5-1.5)

`reinos/05-azeroth/01-reino-de-azeroth.md` (parte 3) e `05-economia-leis-e-fluxos.md`: **4 eras locais** (Casas Soltas, Três Vocações, Mesa, Precedências Disputadas; qual vocação veio primeiro é disputado), **origem do nome \"Azeroth\"** (duas versões de lenda, origem `desconhecida`), lendas dos 10 lugares com era, **cronologia relativa** sem datas nem sincronia com outros reinos, defesa e vulnerabilidades (nenhum inimigo afirmado), **10 leis** como costume com lacuna registrada (nenhuma usa \"Pacto\"), economia qualitativa dos 10 lugares e os dois fluxos do lado de cá: **As Costas** (Enseada-Vigia e Alvamar) e **As Fugas do Véu** (Meiovau, só as planícies humanas, P-2 opção A), conferidos contra a matriz de M-05 (por script). Fluxos 1 a 3 não chegam. Nada toca Thalanor, o Mistério C, a foz do Argente ou as leis de outro reino. **ASSUMINDO:** as dez leis como costume, as duas versões do nome, sal e madeira sem origem e a pergunta de mercado com Aeltharyn aberta. Opções: **A.** aprovar; **B.** trocar era, lenda, lei ou versão do nome (diga qual); **C.** fixar o que Azeroth oferece e compra de Aeltharyn (exige registro nos vizinhos). **Recomendação: A.** **Aceita pela continuação (\"Siga\", 07/11/2026); reversível.**

## D-147 — Povos, sub-raças, linhagens e forasteiros de Azeroth (R5-1.6)

`reinos/05-azeroth/10-matriz-povos-nativos-cidades-e-forasteiros.md`: **9 nativos** (`humano`, `pequenino`, `aasimar`, `tortle`, `tritao`, `elemental-agua`, `elemental-ar`, `sereiano`, `ancestral-estelar`) em IDs do catálogo, **8 sub-raças** usadas (todas existem no JSON), **linhagens próprias** de Humano, Pequenino (`pequenino-viajante`) e Abissal Costeiro (`abissal-das-profundezas`) sem parentesco com Aeltharyn (P-4, regra de D-110), lugares × povos (10 de 10 com ≥ 2 nativos), **25 forasteiros** (8 com lugar, 17 sem lugar fixo), três chegados do Véu em Meiovau sem alocação, papéis por povo com lacunas abertas (3 sem herói, 4 sem guardião, 2 sem benfeitor) e mestiços só pela regra de D-64. Nenhum NPC novo, número, rota ou tempo. **ASSUMINDO:** onde cada sub-raça aparece, os motivos dos forasteiros e a escolha da outra sub-raça nos pares compartilhados com Aeltharyn. Opções: **A.** aprovar; **B.** trocar sub-raça, linhagem ou lugar de povo (diga qual); **C.** criar NPCs para fechar as lacunas de papel. **Recomendação: A.** **Aceita pela continuação (\"Siga\", 07/11/2026); reversível.**

## D-148 — Mistérios, relógios, conflitos e oportunidades de Azeroth (R5-1.7); fim da Fase 1

`reinos/05-azeroth/01-reino-de-azeroth.md` (parte 4) e `06-modelo-operacional-e-aventuras.md`: **10 mistérios com limites** (um por lugar, resposta única não escolhida, \"a verdade não pode ser\"), **10 relógios** (progresso inicial 2/6 ou 3/6, 1 segmento por causa observável, sem avanço por calendário), **10 conflitos** com causa concreta e **10 oportunidades**. Nada toca o Mistério C, a Neblina Longa, os Mistérios D e E, o Conflito G, a foz do Argente nem outro reino; as lacunas de L-01 a L-10 e a precedência entre as coroas seguem abertas; a Mesa de Meiovau só decide o ponto concreto escrito. **ASSUMINDO:** dez relógios, progresso inicial, \"o que trava\", \"quem paga\" e quem decide no segmento 5. Opções: **A.** aprovar; **B.** trocar mistério, relógio, progresso inicial ou número de relógios (diga qual); **C.** dar a um relógio efeito sobre outro reino (exige registro nos vizinhos). **Recomendação: A.** **Aceita pela continuação (\"Siga\", 07/11/2026); reversível.** **Fase 1 do reino 5 concluída (7/7).**

## D-149 — Registro mestre de Azeroth (R5-2.1)

`reinos/05-azeroth/12-registro-mestre.csv` e `.md`: **63 entidades** com ID (10 lugares, 15 facções, 38 pessoas), no formato de D-21. As **26 pessoas sem ficha** (16 da tríade, 8 heróis lendários, Pelmo Balança-de-Porto e Ruvane Chave-Contada) recebem `npc.<nome-sem-acento>`, sem criar pessoa nova. Conferido por script contra os registros dos reinos 1 a 4 (0 colisão) e contra o catálogo (0 povo fora). **ASSUMINDO:** a regra de ID, a residência dos líderes novos (Alvamar e Meiovau) e que coroas, rios e instituições seguem sem ID de registro. Opções: **A.** aprovar; **B.** trocar algum ID ou lugar de pessoa (diga qual); **C.** dar ID também a coroas, rios e instituições. **Recomendação: A.** **Aceita pela continuação (\"Siga\", 07/11/2026); reversível.**

## D-150 — Facções e lugares como dados de Azeroth (R5-2.2)

`12-facoes.csv` (15 facções, 11 colunas) e `12-assentamentos.csv` (10 lugares, 6 colunas), no molde dos reinos 3 e 4, derivados só dos docs 03 e 04. Liderança, objetivo, métodos, aliados, rivais e presença vêm do doc 04 §10; `tensao`, `obrigacao` e `interesse_local` vêm do doc 03. Conferido por script: 0 campo vazio, 0 ID inexistente no registro, aliados e rivais simétricos, 0 aliado que seja rival, 1 rival `desconhecido` (F-07, com motivo). **ASSUMINDO:** `recursos` é `desconhecido` nas 15 (o doc 04 não lista recursos de facção); `cidade_mae` é o lugar-mãe do doc 03 (`—` nas três capitais e em Meiovau); `lideranca_id` do lugar é o chefe do doc 04 §9 (Alvamar com dois) e `desconhecido` em Concharia e Estrelado, onde a voz do órgão está em disputa; `tensao` da facção é a do lugar principal. Opções: **A.** aprovar; **B.** preencher `recursos` por facção (lore novo, diga como). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-151 — Alinhamento de Azeroth ao catálogo (R5-2.3)

`referencia/racas-e-povos/ALINHAMENTO_AZEROTH.md`: 9 nativos, 8 sub-raças e 25 forasteiros conferidos contra `personagem-catalogo.json` (0 divergência de ID ou nome); atribuição de raça, sub-raça, classe e profissão às 38 pessoas (0 ID fora do catálogo; toda sub-raça no lugar que o doc 10 §2 a coloca; P-4 mantido). **ASSUMINDO:** classe e profissão como leitura editorial do papel escrito, profissão `—` onde não há ofício; os 8 heróis lendários ficam sem classe e profissão (sem feito escrito). Opções: **A.** aprovar; **B.** trocar atribuições (diga quais). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-152 — Conferência cruzada de Azeroth (R5-2.4)

`reinos/05-azeroth/13-conferencia-cruzada.md`: aliados e rivais (0 conflito, 0 assimetria), idades (sem número), 13 acúmulos de cargo como intencionais (9 deles, da tríade que lidera a facção local, declarados agora no doc 04 §10), quem sabe o quê nos 10 mistérios, 12 contradições e pontos abertos (CI-01 a CI-12) e as conferências da Fase 2. **ASSUMINDO:** os acúmulos de cargo são desenho intencional, como nos reinos 2 a 4. Opções: **A.** aprovar; **B.** desfazer algum acúmulo ou fechar uma lacuna (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-153 — Rotas e mapa esquemático de Azeroth (R5-3.1)

`reinos/05-azeroth/07-registro-operacional-de-rotas.md` e `07-mapa-esquematico.md`: 14 ligações (9 entre lugares e 5 de fluxo, rio ou sinais; 10 documentadas sem duração e 4 sugeridas por dependência), 10 acessos de lugares, 6 linhas de fronteira e mapa em Mermaid. Toda duração é `desconhecida`: os docs 01, 03 e 05 não trazem "curta" nem "longa" para nenhum percurso, e nada foi inventado. Azeroth ↔ Aeltharyn, Véu, Coroas e Vale seguem sem rota (contrato §5 e §6 itens 5 e 14). **ASSUMINDO:** os vínculos de lugar-mãe como ligações "sugeridas por dependência"; Meiovau ↔ capitais como "documentadas sem duração" (a Mesa arbitra); sem lugar de chegada nomeado para o Corre-Largo e a linha de torres. Opções: **A.** manter tudo `desconhecida` (recomendada); **B.** o autor fornece "curta" ou "longa" para as 14 ligações; **C.** o autor autoriza propostas marcadas como ASSUMINDO. **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-154 — Demografia de Azeroth (R5-3.2)

`reinos/05-azeroth/12-demografia.md` e `.csv` (10 lugares, formato dos reinos 2 a 4): faixas de porte relativo já presentes no doc 01 (2 grandes, 4 médias, 4 pequenas; ASSUMINDO, D-63), com evidência e confiança; regra de **não somar**; todos os lugares fixos; flutuantes, visitantes, chegados do Véu e proporção entre povos fora de qualquer faixa. 0 número, 0 censo, 0 diferença com o doc 01. Opções: **A.** aprovar; **B.** trocar a faixa de algum lugar (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-155 — Recursos de Azeroth (R5-3.3)

`reinos/05-azeroth/08-registro-operacional-de-recursos.md` e `09-matriz-material-dos-lugares.md`: "rica em", "pobre em" e "depende de" para os 10 lugares (só do doc 01 e do doc 05 §2), 24 dependências (17 críticas; todo lugar com ao menos uma crítica, cada uma com fonte) e 4 fluxos qualitativos do lado de cá (grão e mar, As Costas, As Fugas do Véu, aviso e hora). 0 número, nível, unidade, preço ou tempo; origem do sal, da madeira e do grão comprado `desconhecida`; pergunta do contrato §7 mantida aberta. **ASSUMINDO:** lugar-mãe como apoio (6 dependências não críticas); falta de madeira em Concharia como leitura de "vive da madeira que chega de fora". Opções: **A.** aprovar (recomendada); **B.** escala numérica de 0 a 5 do reino 2 (e quem atribui os níveis); **C.** trocar uma dependência ou termo (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-156 — Calendário e estações de Azeroth (R5-3.4; fim da Fase 3)

Doc 01, parte 5, `M-02` §9 e `M-10` §16: 12 luas numeradas, quatro estações (Pousio, Sementeira, Ceifa, Ventania, nomes provisórios, nenhuma de coroa), cinco Dias de Cruzamento, **Contagem das Chaves** (época `desconhecida`); início do jogo em Pousio, Lua 1, dia 1 do **ano 457**, igual a Silêncio, Lua 1, dia 1 do ano 742 de Aeltharyn (deslocamento de 285, ASSUMINDO, sem fonte, reversível); tabela de conversão de 8 linhas; agenda sazonal só de condição (tudo ASSUMINDO); reunião da Mesa, troca de governante, safra, cheia, As Costas e As Fugas ficam `desconhecidos`. Nenhum evento de Azeroth datado em anos de outros reinos. Opções: **A.** aprovar (recomendada); **B.** trocar nomes de estação, dos dias de virada, da contagem ou o deslocamento (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-157 — Fichas completas de Azeroth (R5-4.1)

`reinos/05-azeroth/12-fichas.md` e `12-fichas.csv`: 12 de 12 principais com ficha completa (16 colunas, 0 vazio), 12 agendas e 12 pressões distintas, raça, sub-raça, classe e profissão de `ALINHAMENTO_AZEROTH.md`, facções e aliados de `12-facoes.csv`, relógio de `06` ligado a cada principal e 4 acúmulos de cargo marcados (CI-07). 0 NPC, nome, lugar, facção ou fato novo. **ASSUMINDO:** traços, medos e condições de evolução como leitura editorial; origem `desconhecida`; coluna `camadas` só no R5-6.2. Opções: **A.** aprovar (recomendada); **B.** trocar traço, medo ou condição de uma ficha (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-158 — Competências de Azeroth pelo catálogo (R5-4.2)

`reinos/05-azeroth/12-competencias.md` e `12-competencias.csv`: 38 de 38 pessoas (12 principais, 18 sem ficha, 8 heróis lendários) com raça, sub-raça, classe e profissão de `ALINHAMENTO_AZEROTH.md`, capacidades e limites só do que os docs 01, 04 e `12-fichas` já dizem; 0 ID fora do catálogo, 0 campo vazio. Sem ficha, modo e meios do ofício `desconhecidos`. **ASSUMINDO:** classe e profissão como leitura editorial (D-151). 0 NPC, nome, lugar, facção ou fato novo. Opções: **A.** aprovar (recomendada); **B.** trocar classe, profissão ou limite de alguém (diga quem). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-159 — Relações e conhecimento de Azeroth (R5-4.3)

`12-relacoes.md`, `12-relacoes.csv` (91 relações, todas com fonte, 0 afeto) e `12-conhecimento.csv` (88 linhas, 38 pessoas): só o que os docs 01, 03, 04 e `12-fichas` já dizem. **ASSUMINDO:** medos como leitura editorial; origem em Concharia e Estrelado pelo guardião. 0 NPC, nome, lugar, facção ou fato novo. Opções: **A.** aprovar (recomendada); **B.** autorizar afeto ou dívida entre pessoas, ASSUMINDO (diga os pares). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-160 — Voz e ganchos de diálogo de Azeroth (R5-4.4)

`12-voz.md` e `12-voz.csv`: 12 de 12 principais com tom, 3 frases-modelo e `nunca_diz`; frases só repetem o que os docs já dizem e nenhuma revela suspeita ou desconhecido. **ASSUMINDO:** frases como instrução de tom, não fala canônica. 0 NPC, nome, lugar, facção ou fato novo. Testes de fim da Fase 4: 0 falha, 0 regressão. Opções: **A.** aprovar (recomendada); **B.** trocar tom ou frase de alguém (diga quem). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-161 — Relógios de Azeroth (R5-5.1)

`12-relogios.md` e `12-relogios.csv`: os 10 relógios do doc 06 com gatilhos, o que move e trava, custo por segmento e efeito da inação; nenhum envolve outro reino, nenhum avança por calendário. 0 relógio, NPC, lugar ou fato novo. Opções: **A.** aprovar (recomendada); **B.** trocar gatilho, trava ou progresso inicial (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-162 — Eventos-tipo e cadeias causais de Azeroth (R5-5.2)

`12-eventos.md` e `12-eventos.csv`: 12 eventos-tipo (10 completam relógio, 2 dão gatilho); efeitos só em IDs do registro e em 6 rotas documentadas do doc 07; nenhum envolve outro reino. **ASSUMINDO:** a ordem dos elos. 0 NPC, lugar, facção, relógio ou rota novo. Opções: **A.** aprovar (recomendada); **B.** trocar elo, gatilho ou evento (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-163 — Sementes de aventura de Azeroth (R5-5.3)

`13-sementes-de-aventura.md` e `.csv`: 30 sementes (10 lugares × P, M e G), só com agentes vivos do registro, relógios e eventos já escritos; nenhuma tem solução predefinida nem envolve outro reino. **ASSUMINDO:** níveis de ameaça. 0 NPC, lugar, facção ou fato novo. Opções: **A.** aprovar (recomendada); **B.** trocar semente, agente ou ameaça (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-164 — Reação do reino ao jogador (R5-5.4)

`13-reacao-ao-jogador.md`, `13-reputacao.csv` e `13-canais.csv`: 25 entradas de reputação (10 lugares, 15 facções), estados de procurado, atos ligados às leis L-01 a L-10, favor, reparação qualitativa e 11 canais ligados a rotas do doc 07. Nada depende de raça, nenhuma coroa executa fora de si e nenhum número de fama, preço ou prazo foi escrito (motor, MP-07). **ASSUMINDO:** mapeamento de ato para reação; sanção de L-06 e L-08. 0 NPC, lugar, facção, ofício ou rota novo. Opções: **A.** aprovar (recomendada); **B.** trocar reação, estado ou canal (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-165 — Mistérios com limites de Azeroth (R5-5.5)

`13-misterios-limites.md` e `.csv`: os 10 mistérios do doc 01 parte 4 (MA-01 a MA-10) como dados, sem resposta única, com limites ("a verdade não pode ser"), evidência cumulativa e ligação a relógio e eventos; nenhum toca Aeltharyn, o Véu, as Coroas ou o Vale além de vetos. 0 mistério, hipótese ou veto novo. Opções: **A.** aprovar (recomendada); **B.** trocar hipótese, veto ou prova (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-166 — Resumos de contexto de Azeroth (R5-6.1)

`14-resumos-de-contexto.md` e `.csv`: 73 resumos de até 500 caracteres com ID e vizinhança (cidades pelo doc 07, facções por aliados e rivais, pessoas por lugar e até 3 relações de certeza C). Sem suspeita, desconhecido restrito, `nunca_diz`, duração ou número de população. **ASSUMINDO:** `camada_max` provisória até o R5-6.2. 0 NPC, lugar, facção ou fato novo. Opções: **A.** aprovar (recomendada); **B.** trocar o texto de uma entidade (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-167 — Camadas de informação de Azeroth (R5-6.2)

`14-camadas-de-informacao.md` e `.csv`: 44 fatos em quatro camadas (10 públicos, 12 locais, 2 restritos, 20 secretos) com caminho e modo de descobrir; coluna `camadas` nas fichas; `camada_max` dos resumos definitiva. Nenhum segredo entra em resumo e nenhum fato resolve mistério, lei ou precedência. **ASSUMINDO:** critério de classificação; um caminho basta no restrito. 0 NPC, lugar, facção ou fato novo. Opções: **A.** aprovar (recomendada); **B.** trocar camada ou caminho de um fato (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**


## D-168 — Cenas de teste de Azeroth (R5-6.3)

`15-cenas-de-teste.md` e `.csv`: 7 cenas (cidade de coroa, travessia de rio, assembleia, disputa, viagem, fronteira e porto) com "entra" de até 2103 caracteres e "não entra"; só camadas público e local, nenhum relógio, nenhuma duração de rota, fronteiras só do lado de cá. **ASSUMINDO:** escolha dos sete lugares (Campoalvo, Vaudouro, Meiovau, Concharia, Pouso-das-Brisas, Torrefronte, Enseada-Vigia). 0 NPC, lugar, facção, rota ou fato novo. Opções: **A.** aprovar (recomendada); **B.** trocar uma cena ou lugar (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**


## D-169 — Contrato anti-invenção de Azeroth (R5-6.4)

§9 do doc 06: fatos fixos, livres, que exigem registro e proibidos; novo chefe, lugar, guerra, NPC ou facção só como proposta registrada; rotas e durações `desconhecidas`; As Costas e Fugas do Véu só do lado de cá. **ASSUMINDO:** separação em classes. 0 entidade ou fato novo. Opções: **A.** aprovar (recomendada); **B.** mover item entre classes ou trocar caso (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**


## D-170 — Dados e validador de Azeroth (R5-7.1)

`converter_reino5.py`, `reinos/05-azeroth/dados/` (21 `.txt`, 485 registros) e relatório do validador: 0 erro, 7 avisos esperados. Ajustes mínimos em `validar_reino.py` sem alterar os resultados dos reinos 2, 3 e 4. **ASSUMINDO:** chave `kingdom.azeroth`, calendário `calendario.contagem-das-chaves`, `classe` `—` aceita para heróis lendários falecidos. 0 fato novo. Opções: **A.** aprovar (recomendada); **B.** trocar chave ou ajuste (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**


## D-171 — Catálogo técnico: Azeroth no `.kt` (R5-7.2)

Seção 9 de `MAPA_CATALOGO_KOTLIN.md` (KA1 a KA9): o `.kt` não tem entidade de Azeroth, não há colisão de ID, os 9 nativos e as linhagens do reino 5 ficam fora do `.kt` (Pequenino só como linhagem de Aeltharyn), e as lacunas do esquema ficam para o autor. Nada instalado, nenhum código alterado. 0 fato novo. Opções: **A.** aprovar (recomendada); **B.** ajustar um achado (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-172 — Protótipo mínimo de Azeroth (R5-7.3)

Seção 13 de `PROTOTIPO_MINIMO.md`: recorte em Estrelado (lugar pequeno, liderança `desconhecida`) com Ixra Noite-Aberta como NPC e Vaudouro em uma linha; contexto B de 1.375 caracteres; 8 turnos, Q1 a Q10 e critério de aceite fixados antes de qualquer sessão. **Nenhuma sessão foi executada.** ASSUMINDO: escolha do lugar e da linha de mercado; limite de Q9 em 1.500 caracteres. 0 fato novo. Opções: **A.** aprovar (recomendada); **B.** trocar o lugar, o NPC ou um limite (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-173 — Revisão final de Azeroth (R5-8.1)

`RELATORIO_R5_8_1.md`: portas 1 a 10 do checklist marcadas (3 com ressalva, 6 com lacuna explícita), porta 11 pendente (doc 11, R5-8.2); 12 contradições intencionais a registrar no doc 11; nenhuma acidental nova. Nenhum lore alterado. 0 fato novo. Opções: **A.** aprovar (recomendada); **B.** pedir ajuste de uma porta (diga qual). **Recomendação: A.** **Aceita pela continuação ("Siga", 07/11/2026); reversível.**

## D-174 — Ata de fechamento 6 de Azeroth (R5-8.2)

Doc 11 do reino 5 (`11-fechamento-de-azeroth.md`): resultado das oito fases, 11 de 11 portas, 12 contradições intencionais, limites explícitos, conferência dos cinco reinos (383 IDs únicos, 0 colisão, anel fechado) e critérios finais de uso. Nenhum lore alterado; 0 fato novo. Não é feita a troca de "Pacto Humano" e "Marcas das Feras" por "Azeroth" e "Vale Palancar" em M-04, M-05 e nos reinos 1 a 4 (decisão do autor). Opções: **A.** aprovar o fechamento 6 (recomendada); **B.** pedir ajuste (diga qual). **Recomendação: A.** **Aguarda o autor** (não aceita pela continuação: é aprovação de fechamento).


## D-175 — Níveis de relevância dos eventos do motor (F8.1, D-M14)

Pedido do autor em 08/11/2026: **nível 1** = alianças entre nações, raças e sub-raças, e reis (evento de maior importância, um a um); **nível 2** = heróis e NPCs secundários (resumo a cada 10 anos); **nível 3** = outros eventos (só entra se for necessário para a história; resumo na virada da era). Implementado como leitura **derivada** dos deltas em `motor/niveis.py` (nada novo na crônica nem nos hashes), com `niveis.md` e `metricas_cidades.csv` por rodada, métrica de lote por nível e o invariante I-28. **ASSUMINDO:** "rei" = governante ou herdeiro que vive na capital do reino (o molde não separa rei de governante de cidade); heróis = papéis herói, guardião e benfeitor; "necessário para a história" = a entidade é causa citada de um delta de nível 1 ou 2. **Aberto para o autor:** se governantes de cidades que não são capital devem subir para o nível 1.

## D-176 — Alianças entre raças e sub-raças como mecânica (F8.2, D-M15)

Pedido do autor em 08/11/2026: mecânica nova, em todos os níveis, para todas as cidades do mundo. Motor `motor/motores/povos.py` e `dinamica/povos.txt` (17 chaves `PROPOSTO`): um **laço** por cidade e par de povos que convivem nela (as 46 cidades; raças de `cidade.povos` mais as raças e sub-raças dos NPCs que moram lá; sem pares de mesma linhagem) e um **pacto entre povos** por par que convive em 2 ou mais cidades. Tudo nasce neutro (**lacuna do molde:** nenhuma aliança entre povos é declarada); a simulação decide. Laço: tensão com deriva, pressões (vacância, escassez, guerra) e alívio por mediadores; aliança assinada pelo governante vivo da cidade. Pacto: nasce com laços aliados em 2 cidades e acaba abaixo disso. Invariantes I-26 e I-27. **Limites:** sem efeito econômico nem Eco entre reinos, e o motor só lê os outros motores (não muda fronteira, guerra nem política). Os mediadores morrem e o motor não gera heróis novos, então a mecânica se apoia na deriva e nas pressões, não neles.


## D-177 — Quedas de coroa: sem recalibragem (F8.3)

Lote de 500 anos × 20 sementes (1 a 20), motor F8.2: 20 de 20 rodadas sem falha; quedas de coroa por semente: **0 em 11, 1 em 6, 2 em 2, 3 em 1** (semente 18: Véu dos Malditos no ano 73, Azeroth no 82 e Coroas de Pedra no 177). Média de 0,65 queda por semente, compatível com uma distribuição de cauda curta; o desvio é uma semente em vinte. Nenhum reino cai em todas as sementes (Azeroth 5, Coroas de Pedra 4, Aeltharyn 2, Vale Palancar 1, Véu 1). **Decisão:** manter `queda_pm` = 10 e não criar carência entre quedas, para não mascarar a cauda; o desvio da semente 18 fica documentado. **Opção para o autor:** uma carência (por exemplo 100 anos) tornaria impossível uma segunda queda 9 anos depois da primeira, como na semente 18; é uma regra narrativa nova, não uma calibragem. A mecânica de povos não altera as quedas (só lê os outros motores), e os resultados desta rodada para quedas coincidem com os do lote da F7.4.


## D-178 — Sucessão conta como um evento (F9.1)

Opção do autor listada em `RELATORIO_F8_4.md` (decidida pela continuação, a pedido de "decida você"). Todos os deltas de uma sucessão (posse, vacância, interino, cargo, papéis e criação de NPC simulado) no mesmo dia e posto contam como **um** evento nas métricas de níveis 1+2 (`Niveis.evento`); o resumo por década de `niveis.md` também. Leitura derivada: crônica, hashes e `dinamica/` intactos. ASSUMINDO: a meta 3 a 40 permanece; o efeito esperado é reduzir o desvio de ~7% das décadas (sobretudo Vale Palancar), confirmado no lote 500 anos × 20 sementes (sem falha): décadas fora de 3 a 40 caíram de 9–14% para 4–8% (mediana 5,5%); quedas, pactos e cobertura das cidades sem mudança (semente 18 segue com 3 quedas). Não decididos: carência entre quedas, regra do rei, alianças de partida.


## D-179 — Governante de toda cidade no nível 1: testado e desligado (F9.2)

Opção "rei" do `RELATORIO_F8_4.md`. Com `GOVERNANTE_DE_CIDADE_N1 = True` (6 sementes: 1, 3, 5, 8, 14, 18; 500 anos), o nível 1 por reino e década sobe de mediana 2 para 6,5 e o máximo para 24 a 36, acima da meta de até 25; a história de nível 1 deixa de ser legível. Níveis 1+2 fora de 3 a 40 e cobertura das cidades sem mudança relevante (4% a 8%; 0 cidades sem evento). Decisão: chave mantida em `False` (regra da F8: rei = governante ou herdeiro na capital); quem quiser a outra leitura liga a chave, sem mexer em crônica nem hashes. ASSUMINDO: a meta de nível 1 da §14 vale. Pendentes do autor: aceite F7/F8, carência entre quedas (contrária a D-177) e alianças históricas de partida.


## D-180 — Aceite da F7 e da F8 pelo autor

Em 08/11/2026 o autor aceitou a F7 e a F8 como estão (62 testes; lote 500 anos × 20 sementes sem falha; desvios documentados em D-177, D-178 e D-179). Restam abertas: carência entre quedas de coroa (contrária a D-177) e alianças históricas de partida entre povos.


## D-181 — Alianças de partida entre povos: todas neutras

Em 08/11/2026 o autor decidiu manter todos os laços entre povos neutros no início (sem alianças históricas no molde). Fecha o item 4 de `RELATORIO_F8_4.md`; D-M15 e `dinamica/povos.txt` ficam como estão. Resta aberta: carência entre quedas de coroa.


## D-182 — Carência entre quedas de coroa: não (fecha a F9)

Em 08/11/2026 o autor decidiu manter sem carência, como em D-177. Com D-180, D-181 e D-182 não há pendência do autor no motor: F0 a F9 concluídas; `PLANO_MOTOR_MYTHRALIS.md` sem submódulo aberto.


## D-183 — Nome do reino 5: Azeroth fica

Em 08/11/2026, ao integrar o molde no app (MI-01), o autor confirmou que mantém o nome "Azeroth" (D-139): a coincidência com outro mundo é casual e não há relação com ele. Nada muda nos dados nem no hash do molde.
