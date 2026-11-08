# 12 — Registro mestre de entidades do reino 5 (R5-2.1, versão 1)

**Estado:** R5-2.1, D-149 aceita pela continuação (07/11/2026) · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; **não é cânone novo**: só organiza o que já está nos docs 01, 04 e 10 (D-142 a D-148). Onde um dado não existe, a célula traz `—` ou fica vazia; nada foi inventado. As **26 pessoas sem ficha** (16 da tríade, 8 heróis lendários e os 2 líderes novos do doc 04) **ganharam ID agora**, pelo nome já escrito nos docs 01 e 04, sem criar pessoa nova.
**Uso:** fonte única de IDs do reino 5. Antes de criar NPC, lugar ou facção, procure aqui. **Um ID nunca é reutilizado.** Relógios (`clock.*`) estão no doc 06 e entram em `12-relogios` no R5-5.1; mistérios, conflitos e oportunidades usam rótulos MA, CFA e OA até o R5-5.5.
**Arquivo irmão:** `12-registro-mestre.csv` (formato de D-21: `tipo,id,nome,situacao,id_catalogo,cidade,papel,povo,vivo,extra,ficha_doc,povo_id`). **ASSUMINDO:** `id_catalogo` fica vazio (o catálogo não tem figuras nomeadas); `cidade` guarda o ID do lugar principal; `povo_id` é a raça-base do catálogo; sub-raça, classe e profissão por NPC: `referencia/racas-e-povos/ALINHAMENTO_AZEROTH.md` (R5-2.3); a residência dos governantes e dos herdeiros segue o doc 01 (parte 1 §1 e parte 2); líderes novos ficam no lugar da facção (Pelmo em Alvamar, Ruvane em Meiovau); tipo `facção` com cedilha, como nos reinos 2 a 4. As três coroas, os dois rios e as instituições (doc 04 §10.1) **não recebem ID de registro** nesta versão (coroa e rio não são `city.*`, `npc.*` nem `faction.*`).
**Arquivos irmãos (R5-2.2):** `12-facoes.csv` (15 facções) e `12-assentamentos.csv` (os 10 lugares), §6. **Vêm depois:** catálogo (R5-2.3, feito: `referencia/racas-e-povos/ALINHAMENTO_AZEROTH.md`), conferência cruzada (R5-2.4).

## 1. Resumo

| Tipo | Quantidade |
|---|---:|
| Lugares principais (`city.*`) | 10 (todos fixos) |
| Facções | 15 |
| Assentamentos | 0 (sem vilas menores, doc 03) |
| Pessoas (NPCs) distintas | 38 |

| Situação das pessoas | Qtd |
|---|---:|
| proposto, ficha completa (D-144) | 12 |
| proposto, herói lendário (D-144) | 8 |
| proposto, só tríade (sem ficha) | 16 |
| proposto, só líder de facção (sem ficha) (D-145) | 2 |

30 vivos e 8 falecidos (lendas). Pessoas por povo (ID do catálogo):

| Povo | ID | Qtd |
|---|---|---:|
| Humano | `humano` | 11 |
| Luminar | `aasimar` | 5 |
| Pequenino | `pequenino` | 5 |
| Abissal Costeiro | `tritao` | 3 |
| Estelar | `ancestral-estelar` | 3 |
| Nascido da Maré | `elemental-agua` | 3 |
| Nascido do Vento | `elemental-ar` | 3 |
| Tartarino | `tortle` | 3 |
| Sereiano | `sereiano` | 2 |

9 de 9 nativos cobertos; nenhum forasteiro e nenhum mestiço entre as pessoas (D-64).

## 2. Lugares

| ID | Nome | Tipo e regime | Fonte |
|---|---|---|---|
| `city.campoalvo` | Campoalvo — A Capital do Trigal | capital de coroa, cidade de planície | regime: fixo | AZ-01 (doc 01 parte 1) |
| `city.vaudouro` | Vaudouro — O Mercado dos Rios | cidade fluvial de mercado | regime: fixo | AZ-02 (doc 01 parte 1) |
| `city.torrefronte` | Torrefronte — A Guarda do Limite | fortaleza de limite | regime: fixo | AZ-03 (doc 01 parte 1) |
| `city.alvamar` | Alvamar — A Capital do Cais | capital de coroa, porto sobre o Mar Austral | regime: fixo | AZ-04 (doc 01 parte 1) |
| `city.enseada-vigia` | Enseada-Vigia — O Porto das Costas | porto de rota marítima | regime: fixo | AZ-05 (doc 01 parte 1) |
| `city.concharia` | Concharia — O Estaleiro dos Recifes | estaleiro e santuário costeiro | regime: fixo | AZ-06 (doc 01 parte 1) |
| `city.torreclara` | Torreclara — A Capital do Vento | capital de coroa, cidade de torres de sinais | regime: fixo | AZ-07 (doc 01 parte 1) |
| `city.estrelado` | Estrelado — O Observatório | observatório e arquivo celeste | regime: fixo | AZ-08 (doc 01 parte 1) |
| `city.pouso-das-brisas` | Pouso-das-Brisas — A Estação dos Sinais | estação de torres e pouso de rota | regime: fixo | AZ-09 (doc 01 parte 1) |
| `city.meiovau` | Meiovau — A Cidade das Três Chaves | cidade livre, ponto de encontro das coroas | regime: fixo | AZ-10 (doc 01 parte 1) |

## 3. Facções

| ID | Nome | Líder e objetivo | Fonte |
|---|---|---|---|
| `faction.conselho-dos-celeiros` | Conselho dos Celeiros | líder: Helion Vigília-do-Celeiro | objetivo: manter os celeiros comuns e o preço do pão | F-01 (doc 04 §10) |
| `faction.guarnicao-do-limite` | Guarnição do Limite | líder: Solenne (N-07) | objetivo: limite vigiado e registro lido de um só modo | F-02 (doc 04 §10) |
| `faction.casa-dos-barqueiros` | Casa dos Barqueiros | líder: Ebbo Praia-Rasa | objetivo: docas, canais e comportas na mão dos barqueiros | F-03 (doc 04 §10) |
| `faction.alfandega-das-mares` | Alfândega das Marés | líder: Pelmo Balança-de-Porto | objetivo: taxa do porto a serviço do porto, não só da Coroa | F-04 (doc 04 §10) |
| `faction.guarda-da-corrente` | Guarda da Corrente | líder: Dorgan (N-08) | objetivo: baía fechada a quem não deve entrar e aberta a quem precisa | F-05 (doc 04 §10) |
| `faction.irmandade-dos-faroleiros` | Irmandade dos Faroleiros | líder: Perrin Farol-Aceso | objetivo: luzes da costa e voz de quem vive de barco | F-06 (doc 04 §10) |
| `faction.pilotos-da-enseada` | Pilotos da Enseada | líder: Nerella (N-09) | objetivo: a barra pilotada por ofício, não por coroa | F-07 (doc 04 §10) |
| `faction.construtores-de-concha` | Construtores de Concha | líder: Orrume Abrigo-de-Casco | objetivo: estaleiro com canal maior para navios maiores | F-08 (doc 04 §10) |
| `faction.guardas-do-santuario` | Guardas do Santuário | líder: Corali Fundo-de-Coral | objetivo: jardim e santuário em paz | F-09 (doc 04 §10) |
| `faction.casa-dos-sinais` | Casa dos Sinais | líder: Casimar Código-Limpo | objetivo: rede de torres inteira e códigos guardados | F-10 (doc 04 §10) |
| `faction.leitores-do-ceu` | Leitores do Céu | líder: Ixra Noite-Aberta | objetivo: leitura do céu pelos estelares | F-11 (doc 04 §10) |
| `faction.vigilia-da-cupula` | Vigília da Cúpula | líder: Eloh Cúpula-Acesa | objetivo: leitura do céu pelo voto da vigília | F-12 (doc 04 §10) |
| `faction.posto-dos-pipeiros` | Posto dos Pipeiros | líder: Hollis Torre-do-Meio | objetivo: linha de torres e pousos entre o interior e a costa | F-13 (doc 04 §10) |
| `faction.albergue-dos-chegados` | Albergue dos Chegados | líder: Auriven (N-12) | objetivo: acolhida larga a quem chega | F-14 (doc 04 §10) |
| `faction.mesa-estreita` | Mesa Estreita | líder: Ruvane Chave-Contada | objetivo: registro e prazo antes de qualquer sustento | F-15 (doc 04 §10) |

## 4. Pessoas

| ID | Nome | Povo (`povo_id`) | Lugar | Papel | Situação | Vivo |
|---|---|---|---|---|---|---|
| `npc.odile-medida-cheia` | Odile Medida-Cheia | Humano (`humano`) | `city.campoalvo` | governante da Coroa do Trigal; despacha no Salão das Medidas | proposto, ficha completa (D-144) | sim |
| `npc.corlan-cais-primeiro` | Corlan Cais-Primeiro | Abissal Costeiro (`tritao`) | `city.alvamar` | governante da Coroa do Cais; despacha na Casa do Cais | proposto, ficha completa (D-144) | sim |
| `npc.brevo-rumo-alto` | Brevo Rumo-Alto | Nascido do Vento (`elemental-ar`) | `city.torreclara` | governante da Coroa do Vento; despacha na Torre-Mestra | proposto, ficha completa (D-144) | sim |
| `npc.tomlin-horta-larga` | Tomlin Horta-Larga | Pequenino (`pequenino`) | `city.campoalvo` | herdeiro reconhecido da Coroa do Trigal | proposto, ficha completa (D-144) | sim |
| `npc.rilla-espuma-fina` | Rilla Espuma-Fina | Nascido da Maré (`elemental-agua`) | `city.alvamar` | herdeira reconhecida da Coroa do Cais | proposto, ficha completa (D-144) | sim |
| `npc.astrel-noite-clara` | Astrel Noite-Clara | Estelar (`ancestral-estelar`) | `city.torreclara` | herdeiro reconhecido da Coroa do Vento; liga a Torre-Mestra à Mesa dos Astros | proposto, ficha completa (D-144) | sim |
| `npc.solenne-vigilia-longa` | Solenne Vigília-Longa | Luminar (`aasimar`) | `city.torrefronte` | comandante da guarnição do Trigal; guardiã de Torrefronte | proposto, ficha completa (D-144) | sim |
| `npc.dorgan-escudo-lento` | Dorgan Escudo-Lento | Tartarino (`tortle`) | `city.alvamar` | comandante do molhe e da Corrente da Boca; guardião de Alvamar | proposto, ficha completa (D-144) | sim |
| `npc.nerella-canto-da-barra` | Nerella Canto-da-Barra | Sereiano (`sereiano`) | `city.enseada-vigia` | piloto da Barra; herói de Enseada-Vigia (guiou uma frota inteira à barra numa noite sem luz, sem perder um casco) | proposto, ficha completa (D-144) | sim |
| `npc.garvane-remo-firme` | Garvane Remo-Firme | Humano (`humano`) | `city.vaudouro` | barqueiro-chefe das barcaças de Vaudouro; herói de Vaudouro (manteve as comportas e as barcaças numa cheia, sem perder carga nem barqueiro) | proposto, ficha completa (D-144) | sim |
| `npc.tolla-mesa-posta` | Tolla Mesa-Posta | Pequenino (`pequenino`) | `city.meiovau` | guardiã das Três Chaves; preside a Mesa de Meiovau | proposto, ficha completa (D-144) | sim |
| `npc.auriven-porta-acesa` | Auriven Porta-Acesa | Luminar (`aasimar`) | `city.meiovau` | benfeitor de Meiovau; mantém o Albergue dos Chegados, que recebe quem chega a pé e de barca | proposto, ficha completa (D-144) | sim |
| `npc.wendra-aterro-primeiro` | Wendra Aterro-Primeiro | Humano (`humano`) | `city.campoalvo` | Herói lendário | proposto, herói lendário (D-144) | não |
| `npc.helion-vigilia-do-celeiro` | Helion Vigília-do-Celeiro | Luminar (`aasimar`) | `city.campoalvo` | guardião de Campoalvo | proposto, só tríade (sem ficha) | sim |
| `npc.dodda-prato-cheio` | Dodda Prato-Cheio | Pequenino (`pequenino`) | `city.campoalvo` | benfeitor de Campoalvo | proposto, só tríade (sem ficha) | sim |
| `npc.ebbo-praia-rasa` | Ebbo Praia-Rasa | Nascido da Maré (`elemental-agua`) | `city.vaudouro` | guardião de Vaudouro | proposto, só tríade (sem ficha) | sim |
| `npc.larkin-barcaca-cheia` | Larkin Barcaça-Cheia | Pequenino (`pequenino`) | `city.vaudouro` | benfeitor de Vaudouro | proposto, só tríade (sem ficha) | sim |
| `npc.halvard-limite-firme` | Halvard Limite-Firme | Humano (`humano`) | `city.torrefronte` | Herói lendário | proposto, herói lendário (D-144) | não |
| `npc.sabeth-patio-aberto` | Sabeth Pátio-Aberto | Humano (`humano`) | `city.torrefronte` | benfeitor de Torrefronte | proposto, só tríade (sem ficha) | sim |
| `npc.sundar-licenca-de-cais` | Sundar Licença-de-Cais | Abissal Costeiro (`tritao`) | `city.alvamar` | Herói lendário | proposto, herói lendário (D-144) | não |
| `npc.marnu-espuma-dada` | Marnu Espuma-Dada | Nascido da Maré (`elemental-agua`) | `city.alvamar` | benfeitor de Alvamar | proposto, só tríade (sem ficha) | sim |
| `npc.perrin-farol-aceso` | Perrin Farol-Aceso | Humano (`humano`) | `city.enseada-vigia` | guardião de Enseada-Vigia | proposto, só tríade (sem ficha) | sim |
| `npc.ressan-concha-aberta` | Ressan Concha-Aberta | Abissal Costeiro (`tritao`) | `city.enseada-vigia` | benfeitor de Enseada-Vigia | proposto, só tríade (sem ficha) | sim |
| `npc.mossa-casco-primeiro` | Mossa Casco-Primeiro | Tartarino (`tortle`) | `city.concharia` | Herói lendário | proposto, herói lendário (D-144) | não |
| `npc.orrume-abrigo-de-casco` | Orrume Abrigo-de-Casco | Tartarino (`tortle`) | `city.concharia` | guardião de Concharia | proposto, só tríade (sem ficha) | sim |
| `npc.corali-fundo-de-coral` | Corali Fundo-de-Coral | Sereiano (`sereiano`) | `city.concharia` | benfeitor de Concharia | proposto, só tríade (sem ficha) | sim |
| `npc.raffe-sopro-primeiro` | Raffe Sopro-Primeiro | Nascido do Vento (`elemental-ar`) | `city.torreclara` | Herói lendário | proposto, herói lendário (D-144) | não |
| `npc.casimar-codigo-limpo` | Casimar Código-Limpo | Humano (`humano`) | `city.torreclara` | guardião de Torreclara | proposto, só tríade (sem ficha) | sim |
| `npc.clarel-lume-de-torre` | Clarel Lume-de-Torre | Luminar (`aasimar`) | `city.torreclara` | benfeitor de Torreclara | proposto, só tríade (sem ficha) | sim |
| `npc.orbane-rota-perdida` | Orbane Rota-Perdida | Estelar (`ancestral-estelar`) | `city.estrelado` | Herói lendário | proposto, herói lendário (D-144) | não |
| `npc.eloh-cupula-acesa` | Eloh Cúpula-Acesa | Luminar (`aasimar`) | `city.estrelado` | guardião de Estrelado | proposto, só tríade (sem ficha) | sim |
| `npc.ixra-noite-aberta` | Ixra Noite-Aberta | Estelar (`ancestral-estelar`) | `city.estrelado` | benfeitor de Estrelado | proposto, só tríade (sem ficha) | sim |
| `npc.zefir-pipa-caida` | Zefir Pipa-Caída | Nascido do Vento (`elemental-ar`) | `city.pouso-das-brisas` | Herói lendário | proposto, herói lendário (D-144) | não |
| `npc.hollis-torre-do-meio` | Hollis Torre-do-Meio | Humano (`humano`) | `city.pouso-das-brisas` | guardião de Pouso-das-Brisas | proposto, só tríade (sem ficha) | sim |
| `npc.poppel-mesa-de-viajante` | Poppel Mesa-de-Viajante | Pequenino (`pequenino`) | `city.pouso-das-brisas` | benfeitor de Pouso-das-Brisas | proposto, só tríade (sem ficha) | sim |
| `npc.ostrel-chave-primeira` | Ostrel Chave-Primeira | Humano (`humano`) | `city.meiovau` | Herói lendário | proposto, herói lendário (D-144) | não |
| `npc.pelmo-balanca-de-porto` | Pelmo Balança-de-Porto | Humano (`humano`) | `city.alvamar` | líder da Alfândega das Marés (F-04) | proposto, só líder de facção (sem ficha) (D-145) | sim |
| `npc.ruvane-chave-contada` | Ruvane Chave-Contada | Humano (`humano`) | `city.meiovau` | líder da Mesa Estreita (F-15) | proposto, só líder de facção (sem ficha) (D-145) | sim |

## 5. Conferências

| Item | Resultado |
|---|---|
| IDs duplicados no registro | 0 (63 IDs únicos) |
| Colisão com os registros mestres dos reinos 1, 2, 3 e 4 (IDs exatos e mesmo nome sem prefixo) | 0 |
| `povo_id` fora do catálogo (`personagem-catalogo.json`) | 0 |
| `cidade` inexistente | 0 |
| ID citado nos docs 01, 03, 04, 06 e 10 e ausente do registro | 0 (conferido por script; `npc.syranna-das-mares` aparece só como veto no doc 10 e pertence a Aeltharyn) |
| Pessoas por povo × doc 01 §4 | 9 de 9 nativos; Humano 11, Pequenino 5, Luminar 5, Tartarino 3, Abissal Costeiro 3, Nascido da Maré 3, Nascido do Vento 3, Estelar 3, Sereiano 2 |
| Líder de cada uma das 15 facções existe como pessoa | 15 de 15 |
| Heróis lendários marcados como falecidos | 8 de 8 |
| Mestiços, forasteiros, número, rota ou tempo | 0 |

## 6. Facções e lugares como dados (R5-2.2)

- `12-facoes.csv`: 15 linhas, 11 colunas, 0 campo vazio. Liderança, objetivo, métodos, aliados e rivais vêm do doc 04 §10; presença por lugar, do doc 04 §10.2 (presenças reclamadas ficam anotadas); `tensao`, do doc 03 (o problema do lugar principal da facção). **`recursos`** é `desconhecido` nas 15 (o doc 04 não lista recursos de facção; não se inventa).
- Aliados e rivais em IDs de facção: 0 assimetria, 0 aliado que seja rival, 0 ID inexistente no registro. 1 `rivais` `desconhecido`, com o motivo do doc 04: `faction.pilotos-da-enseada`.
- `12-assentamentos.csv`: 10 linhas (os 10 lugares), 6 colunas, 0 campo vazio. `cidade_mae` é `—` nas três capitais e em Meiovau e a capital da coroa nos outros seis (doc 03). `lideranca_id` é o chefe do doc 04 §9 (Alvamar: Pelmo e Dorgan); em Concharia e Estrelado é `desconhecido`, porque a voz do órgão é o que está em disputa.

## 7. Decisão pendente

**D-150** (facções e lugares como dados) **e D-149:** o registro mestre (63 entidades) e os IDs novos das 26 pessoas sem ficha. **ASSUMINDO:** a regra de ID (`npc.` mais o nome sem acento), a residência dos líderes novos e a ausência de ID para coroas, rios e instituições.
