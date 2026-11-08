# Mapa do catálogo Kotlin para o esquema (M6.3)

**Estado:** especificação de conversão. O `.kt` não compila aqui (`referencia-tecnica/LEIA-ME.md`) e **não é canônico**: onde diverge dos documentos, valem os documentos e os CSVs. Depois da conversão ninguém precisa do `.kt` para entender os dados.
**Fonte:** `referencia-tecnica/AeltharynWorld.kt` (51 figuras, 10 cidades, 16 povos, 14 rotas, 7 relógios) e `AeltharynWorldTest.kt`; destino em `ESQUEMA_DE_DADOS.md`; regras em `VALIDADOR_DE_REINO.md`.

## 1. Campo a campo

### `WorldCity` → `cidade.txt`
| Kotlin | Esquema | Conversão |
|---|---|---|
| `id` | `id` | igual |
| `name` | `nome` | igual |
| `population: Int` | `populacao_faixa` | **trocar** número por faixa de uso (`12-demografia.csv`, M2.2); o número não migra (nunca somar) |
| `role` | `funcao` | texto igual |
| `supplies` | `recurso_chave` | lista; cada item vira também linha em `recurso.txt` com `papel` e fonte (`12-recursos.csv`) |
| `dependsOn` (lista de cidades) | `recurso.txt` (dependência) e `rota.txt` | **não migra como cidade**: dependência vira recurso consumido; só entra se o doc 08 ou o `12-recursos.csv` a escreve |
| `activeProblem` | `problemas` | **camada**: texto do `.kt` era `secret` do `Place`; só migra o que o doc 01 escreve como problema público; o resto vira `fato.txt` com camada |
| `legend` | `lenda` | igual |
| `peopleIds` | `povos` | lista de IDs do catálogo de personagem |
| — | `reino`, `tipo`, `localizacao`, `governo`, `governante`, `economia`, `defesas`, `locais`, `faccoes`, `rumores`, `acesso`, `fonte` | **não existem no `.kt`**; vêm do doc 01 §5 e dos fichários (`12-registro-mestre`, `12-fichas-elarion`) |

### `WorldPeople` → `povo_no_reino.txt` (arquivo de apoio novo, lacuna do esquema)
| Kotlin | Esquema | Conversão |
|---|---|---|
| `id` | `povo` → catálogo | ID do catálogo de personagem |
| `name` | — | descartar (o nome vem do catálogo; sinônimos em `ALINHAMENTO_AELTHARYN.md`) |
| `relation` | `relacao` | `originário`, `linhagem`, `aliado`, `vizinho`, `migrante`, conforme o doc 10 |
| `cityIds` | `cidades` | lista; coerente com `cidade.povos` (V-36) |
| `notes` | `nota` | texto igual |

### `WorldFigure` → `npc.txt`
| Kotlin | Esquema | Conversão |
|---|---|---|
| `id` | `id` | os 7 IDs divergentes já foram trocados (D-04/D-05) |
| `name` | `nome` | igual |
| `cityId` | `cidade` | igual (exceto `npc.vessa-fio-dourado`, D-07 → `city.silvaran`) |
| `peopleId` | `povo` | igual (D-20 aberta para Yssara e Hadrik) |
| `role` `HERO`/`GUARDIAN`/`BENEFACTOR` | `papeis` | `heroi`, `guardiao`, `benfeitor` |
| `aliveAtStart` | `vivo` | `true` → `sim`, `false` → `não` |
| `contribution` | `capacidades` | texto igual |
| `limitation` | `limites` | texto igual |
| `currentAgenda` | `agenda` | **só se o valor difere de `contribution`**; senão `?` até a ficha (M3.1 marcou como ASSUMINDO) |
| `pressure` | `pressao` | idem em relação a `limitation` |
| — | `reino`, `idade`, `subraca`, `classe`, `profissao`, `faccao`, `cargo`, `principal`, `personalidade`, `objetivo`, `tom`, `resumo`, `fonte` | de `12-registro-mestre`, `12-competencias.csv`, `12-voz.csv`, `14-resumos-de-contexto.csv` |

As 44 pessoas que só existem nos documentos (governo, conselho, líderes) não estão no `.kt`; entram direto do registro mestre. Papéis `governante`, `herdeiro`, `general` e `conselheiro` também vêm dos documentos.

### `WorldRoute` → `rota.txt`
| Kotlin | Esquema | Conversão |
|---|---|---|
| `fromCityId`, `toCityId` | `de`, `para` | igual |
| `days: Int` | `duracao_min`, `duracao_max`, `estado` | **trocar**: valor único vira intervalo do doc 07; onde o `.kt` e o doc 07 divergem (doc 07 §3.2) vale o doc 07 |
| `mode` | `meio` | `river` → `rio`, `forest` → `trilha`, demais pelo doc 07; rota com mais de um meio vira `misto` ou linhas separadas |
| `goods` | `recurso.txt` (transporte) | só se o doc 08 escreve; senão descartar |
| `seasonalRisk` | `condicao` | texto igual; sem modificador numérico |
| — | `id`, `reino`, `fonte` | `route.<de>-<para>` do doc 07 |

As rotas só do doc 07 (acessos locais de assentamento, corredores com duração `?`) entram como `desconhecida`; nenhuma duração é inventada.

### `WorldClock` → `relogio.txt`
| Kotlin | Esquema | Conversão |
|---|---|---|
| `id` | `id` | igual |
| `name` | `nome` | igual |
| `initialProgress`, `segments` | `progresso`, `segmentos` | igual |
| `advancesWhen` | `move` | `12-relogios.csv` é a versão mais completa |
| `consequence` | `completa` | idem |
| — | `trava`, `paga`, `inacao`, `entidades`, `reino`, `fonte` | de `12-relogios.csv` |

## 2. O que descartar

| Item | Motivo |
|---|---|
| `install(session)` | cria 10 lugares, rotas, 7 relógios e 37 NPCs de uma vez; contradiz o doc 02 (carregar só o que a cena pede). Substituído por **molde estático + instanciação sob demanda + deltas** (`ESQUEMA_DE_DADOS.md` §5) |
| `Place.secret = activeProblem` | mistura camada pública e segredo num campo; substituído por `fato.txt` com camada e caminho |
| `session.state.places.isNotEmpty()` (recusa campanha com mapa) | impede combinar com mundo herdado ou outro molde |
| `NpcDossier` gerado por modelo de texto ("é herói de…") | personalidade e objetivo vêm da ficha, não de frase-molde |
| `CampaignSession`, `Place`, `PlaceMap`, `Entity`, `Command` | dependências do motor, fora do conteúdo editorial |
| `population: Int` | substituído por faixa de uso |
| `dependsOn` entre cidades como lista solta | vira dependência de recurso, com fonte |
| `WorldPeople.name` | o catálogo de personagem é a fonte do nome |
| `kotlinx.serialization` e as `data class` | formato do código, não do dado |

## 3. Funções e testes para regras

| Kotlin | Destino |
|---|---|
| `validate()`: IDs únicos | V-03 |
| `validate()`: 10 cidades, 16 povos | V-30; a contagem de povos deixa de ser regra do motor |
| `validate()`: capital existe | V-10 (`reino.capital`) |
| `validate()`: povo e cidade coerentes entre si | V-10, V-12, V-36 |
| `validate()`: cada cidade com HERO, GUARDIAN e BENEFACTOR; cada povo idem | **regra de autoria de Aeltharyn**, não genérica; fica como regra opcional `V-A1` do reino (tríade, `MODELO_DE_REINO.md` §4) |
| `validate()`: rota com cidade inexistente, a si mesma, duração ≤ 0 | V-20, V-22 |
| `validate()`: relógio com progresso fora da faixa | V-60 |
| `validate()`: cidade sem rota a partir da capital | V-23 |
| `sceneContext()` | cenas (`15-cenas-de-teste`) e resumos com `camada_max`; o texto-molde some |
| Teste `everyLivingFigureHasAnActionableAgendaAndDistinctPressure` | V-35 |
| Demais testes | cobertos por V-23, V-30, V-60; `install` não tem equivalente |

**Regra opcional V-A1** (nova, só para reinos que mantêm a tríade): cada cidade tem ≥ 1 NPC `heroi`, `guardiao` e `benfeitor`; **A** se faltar (Arven está sem benfeitora desde D-07).

## 4. Ordem de conversão sugerida (para o M6.4 e a implementação)

1. `reino`, `cidade`, `assentamento`, `faccao` (do registro mestre e dos fichários).
2. `npc` (registro mestre + competências + voz), cruzando com o `.kt` só para `agenda` e `pressao` distintas.
3. `rota` (doc 07) e `recurso` (`12-recursos.csv`).
4. `relogio`, `evento`, `semente`, `misterio`, `fato`, `resumo`, `cena`.
5. Rodar o validador; o `.kt` deixa de ser consultado.

## 5. Montanhas no `.kt` (R2-7.2, 2026-11-05)

**Verificação** de `AeltharynWorld.kt` e `AeltharynWorldTest.kt` (nenhum código alterado, nada instalado, nada executado). O mapa das seções 1 a 4 vale para as Coroas de Pedra sem mudança de campo.

| # | Achado | Tratamento |
|---|---|---|
| K1 | **0 entidade das Coroas** no `.kt`: 0 cidade, povo, figura, rota ou relógio do reino 2; o único `kingdom` é `kingdom.aeltharyn`. Os 592 registros de `reinos/02-.../dados/` vêm só dos documentos | conversão do reino 2 nunca consulta o `.kt` (o passo 5 da seção 4 já vale desde o início) |
| K2 | **0 colisão de ID**: 69 IDs `kingdom.`, `city.`, `npc.`, `clock.` e `route.` do `.kt` contra os IDs do reino 2 | nada a renomear; os 11 canais já foram renomeados em R2-7.1 (D-58) |
| K3 | O `.kt` é **singleton de Aeltharyn**: `object AeltharynWorld`, `CAPITAL_CITY_ID = city.elarion`, `validate()` exige 10 cidades e 16 povos, `install()` cria tudo de uma vez | não serve de carregador do reino 2; vale a regra do `INTEGRACAO_FUTURA_RPG_SOLO.md` §2 (dados em arquivo, carregador genérico no motor). A contagem de povos não é regra do reino 2 |
| K4 | `mode = "mountain_pass"` (rota Caladwen–Torvelume) **não existe** no enum `meio` (`estrada`, `rio`, `mar`, `trilha`, `misto`) | não migra como valor; vale o doc 07 do reino 1 (estrada com ressalva). As 13 rotas do reino 2 têm `meio = ?`; nenhum valor de montanha é criado. **Lacuna do esquema** para futura rota de passo (decisão do autor) |
| K5 | `days = 4` em Caladwen–Torvelume; doc 07 do reino 1 diz cerca de 3 dias (corredor 4 a 5 dias com o trecho a Vael-Tir) | divergência já registrada (doc 07 §3.2); vale o doc 07; sem efeito no reino 2 |
| K6 | `population = 17000` de Torvelume; documentos dizem cerca de 18.000 | número não migra (§1); vale o doc; sem efeito no reino 2 |
| K7 | `clock.sealed-mine` (Mina selada de Torvelume, 1/6) é o único tema de montanha com estado no `.kt` | pertence a Aeltharyn e ao Mistério D; o reino 2 não afirma resposta (contrato de fronteira §3, item 1) e nenhum relógio das Coroas o usa |
| K8 | `gnomo-dos-vales` consta nos povos do `.kt` e em `povo_no_reino.txt` do reino 2; nenhum ID de anão ou outro nativo das Montanhas consta no `.kt` | ID de catálogo compartilhado, sem conflito; anões em Torvelume são residentes individuais (contrato F10), sem entrada no `.kt` |
| K9 | `V-A1` (tríade herói, guardião e benfeitor) e os 7 papéis do `.kt` | não se aplicam ao reino 2 (R2-7.1: V-A1 fora) |

**Divergências sem correção no `.kt`:** K4, K5 e K6; o `.kt` não será atualizado (seção 6). **Nada instalado, nenhum código alterado, `.kt` não executado.**

## 7. Véu dos Malditos no `.kt` (R3-7.2, 2026-11-06)

**Verificação** de `AeltharynWorld.kt` e `AeltharynWorldTest.kt` (nenhum código alterado, nada instalado, nada executado). O mapa das seções 1 a 4 vale para o Véu dos Malditos sem mudança de campo.

| # | Achado | Tratamento |
|---|---|---|
| KV1 | **0 entidade do Véu** no `.kt`: 0 cidade, povo, figura, rota ou relógio do reino 3; o único `kingdom` é `kingdom.aeltharyn`. Os 390 registros de `reinos/03-.../dados/` vêm só dos documentos e CSVs | o `.kt` não é fonte do reino 3 |
| KV2 | **0 colisão de ID:** os 70 IDs `kingdom.`, `city.`, `npc.` e `clock.` do `.kt` contra os 191 IDs de `dados/` | nada a renomear; o validador (V-03) também compara com os reinos de número menor |
| KV3 | O `.kt` é **singleton de Aeltharyn** (`object AeltharynWorld`, capital `city.elarion`, `validate()` exige 10 cidades e 16 povos, `install()` cria tudo de uma vez) | não vira carregador do reino 3, que tem 8 lugares (D-66) |
| KV4 | Os **11 povos nativos** do Véu (`tiefling`, `kenku`, `bugbear`, `lizardino`, `homem-rato`, `sombrio`, `onirico`, `vampirico`, `licantropo`, `metamorfo`, `aracnideo`) **não constam** nos 16 povos do `.kt`. Três IDs de forasteiro do Véu (`feerico`, `fungal`, `plantae`) constam como povos de Aeltharyn | ID de catálogo compartilhado, sem conflito; a relação com cada reino fica em `povo_no_reino.txt` de cada um (como K8) |
| KV5 | **"Neblina" e "pactos" no `.kt`:** 10 linhas com "neblina" e 8 com "pacto" ou "pactuadas", todas de Aeltharyn (Brumafunda, guias, abrigo, círculo de pactos instável, pactos de hospitalidade). São homônimos de elementos do Véu, não da mesma coisa | nada migra; o contrato de fronteira §3 proíbe explicar a Neblina Longa e ligar os pactos do Véu aos de Aeltharyn |
| KV6 | `mode = "mountain_pass"` (Caladwen–Torvelume) e os `days` das rotas do `.kt` | irrelevantes ao Véu: as 10 rotas de `rota.txt` seguem `meio = ?` e `desconhecida` (D-63); nenhum valor criado |
| KV7 | **V-A1 (tríade):** o Véu mantém a tríade (doc 01 parte 2 §3). Conferência em `dados/npc.txt` (`papeis`): **8 de 8 lugares** com `heroi`, `guardiao` e `benfeitor`; 6 dos 8 heróis são lendários falecidos | a regra V-A1 não está implementada em `validar_reino.py`; conferida à mão nesta etapa, sem alterar o validador |
| KV8 | `K4`, `K5` e `K6` (divergências de rota e população de Torvelume) | só de Aeltharyn; sem efeito no reino 3 |

**Nada instalado, nenhum código alterado, `.kt` não executado.** O `.kt` não será atualizado (seção 6).

## 8. Vale Palancar no `.kt` (R4-7.2, 2026-11-07)

**Verificação** de `AeltharynWorld.kt` e `AeltharynWorldTest.kt` (nenhum código alterado, nada instalado, nada executado; só leitura e busca de texto). O mapa das seções 1 a 4 vale para o Vale Palancar sem mudança de campo.

| # | Achado | Tratamento |
|---|---|---|
| KP1 | **0 entidade do Vale** no `.kt`: 0 cidade, povo, figura, rota ou relógio do reino 4; o único `kingdom` é `kingdom.aeltharyn`. Os 405 registros de `reinos/04-vale-palancar/dados/` vêm só dos documentos e CSVs. Busca de "Argente", "Ermo", "Descida", "Rota do Sal", "Assembleia", "Capitão" e "marcha": 0 ocorrência | o `.kt` não é fonte do reino 4 |
| KP2 | **0 colisão de ID:** os 69 IDs `kingdom.`, `city.`, `npc.`, `clock.` e `route.` do `.kt` contra os 48 do reino 4 (1 reino, 8 cidades, 25 pessoas, 8 relógios, 6 rotas) | nada a renomear; o validador (V-03) também compara com os reinos de número menor |
| KP3 | O `.kt` é **singleton de Aeltharyn** (`object AeltharynWorld`, capital `city.elarion`, `validate()` exige 10 cidades e 16 povos, `install()` cria tudo de uma vez) | não vira carregador do reino 4, que tem 8 lugares, sem capital (D-102) |
| KP4 | Os **12 nativos** do Vale (`orcano`, `tabaxi`, `centauro`, `goblin`, `hobgoblin`, `homem-raposa`, `homem-coelho`, `homem-urso`, `homem-lobo`, `homem-javali`, `homem-cervo`, `insetoide`) **não constam** nos povos do `.kt`. Quatro IDs de forasteiro do Vale (`elfo`, `feerico`, `plantae`, `satiro`) constam como povos de Aeltharyn | ID de catálogo compartilhado, sem conflito; a relação com cada reino fica em `povo_no_reino.txt` de cada um (como K8 e KV4) |
| KP5 | **Linhagens (P-3):** o `.kt` usa `centauro-das-florestas`, `orcano-das-matas` e `felideo-das-selvas`; o Vale usa `centauro-das-pradarias`, `orcano-das-estepes` e `felideo-das-dunas`. Nenhum ID de sub-raça do Vale está no `.kt` | confirma a separação do contrato §7 (P-3, D-110): sem parentesco, sem ID repetido |
| KP6 | **Centauros, migração e sazonal no `.kt`:** 7 linhas de `centauro-das-florestas` e as de "corredores de migração", "acordo sazonal" e "rota sazonal" são do Conflito G e da Oranna, de Aeltharyn | nada migra; o contrato de fronteira §5 item 1 proíbe decidir o Conflito G e usar os centauros de Aeltharyn como do Vale |
| KP7 | `mode = "mountain_pass"` (Caladwen–Torvelume) e os `days` das rotas do `.kt` | irrelevantes ao Vale: as 6 rotas de `rota.txt` seguem `meio = ?` e `desconhecida` (D-63); nenhum valor criado. As 3 ligações externas (rio acima, Ermo de Sal, elevações ocidentais) são exceções de `excecoes.txt`, sem rota |
| KP8 | **V-A1 (tríade):** conferência em `dados/npc.txt` (`papeis`): **8 de 8 lugares** com `heroi`, `guardiao` e `benfeitor`; 6 dos 8 `heroi` de lugar são lendários falecidos (os outros dois, Tessun e Rukhan, são vivos) | a regra V-A1 não está implementada em `validar_reino.py`; conferida à mão, sem alterar o validador |
| KP9 | **Lacunas do esquema para o reino 4** (sem criar campo): (a) **regime** fixo ou sazonal e posição móvel de lugar não têm campo em `cidade.txt` (ficam nos docs 01 e 07 e em `extra` do registro mestre); (b) **governo sem rei e sem capital** (Assembleia e Capitão): `reino.txt` guarda `capital = —` e o texto no `tema`; (c) **Tratos** e a Assembleia são instituições sem entidade própria; (d) `K4`, `K5` e `K6` (rota e população de Torvelume) são só de Aeltharyn | decisão do autor para o motor; nada é criado agora |

**Nada instalado, nenhum código alterado, `.kt` não executado.** O `.kt` não será atualizado (seção 6).

## 9. Azeroth no `.kt` (R5-7.2, 2026-11-07)

**Verificação** de `AeltharynWorld.kt` e `AeltharynWorldTest.kt` (nenhum código alterado, nada instalado, nada executado; só leitura e busca de texto). O mapa das seções 1 a 4 vale para Azeroth sem mudança de campo.

| # | Achado | Tratamento |
|---|---|---|
| KA1 | **0 entidade de Azeroth** no `.kt`: 0 cidade, povo, figura, rota ou relógio do reino 5; o único `kingdom` é `kingdom.aeltharyn`. Os 485 registros de `reinos/05-azeroth/dados/` vêm só dos documentos e CSVs. Busca de "Trigal", "Cais", "Vento", "Meiovau", "Mesa", "Chaves", "Estelar", "Mar Austral" e "Azeroth": 0 ocorrência (as únicas "ocorrências" textuais são palavras como "locais") | o `.kt` não é fonte do reino 5 |
| KA2 | **0 colisão de ID:** os 69 IDs `kingdom.`, `city.`, `npc.`, `clock.` e `route.` do `.kt` contra os 68 do reino 5 (1 reino, 10 cidades, 38 pessoas, 10 relógios, 9 rotas) | nada a renomear; o validador (V-03) também compara com os reinos de número menor |
| KA3 | O `.kt` é **singleton de Aeltharyn** (`object AeltharynWorld`, capital `city.elarion`, `validate()` exige 10 cidades e 16 povos, `install()` cria tudo de uma vez) | não vira carregador do reino 5, que tem 10 lugares, três capitais de coroa e uma cidade livre, sem capital única (doc 01 parte 1) |
| KA4 | Os **9 nativos** de Azeroth (`humano`, `pequenino`, `aasimar`, `tortle`, `tritao`, `elemental-agua`, `elemental-ar`, `sereiano`, `ancestral-estelar`) **não constam** como povos do `.kt`, exceto o Pequenino, que existe lá só como linhagem `pequenino-ribeirinho`; o forasteiro `elfo` consta como povo de Aeltharyn | ID de catálogo compartilhado, sem conflito; a relação com cada reino fica em `povo_no_reino.txt` de cada um (como K8, KV4 e KP4) |
| KA5 | **Linhagens (P-4):** os 8 IDs de sub-raça de Azeroth (`humano-das-cidades`, `humano-das-fronteiras`, `luminar-do-alvorecer`, `luminar-do-zenite`, `pequenino-viajante`, `tartarino-das-ilhas`, `tartarino-dos-recifes`, `abissal-das-profundezas`) **não estão no `.kt`**; o `.kt` usa `pequenino-ribeirinho` e `abissal-das-enseadas` | confirma a separação do contrato §6 item 9 (P-4, regra de D-110): sem parentesco, sem ID repetido |
| KA6 | **Torres, sinais e comportas no `.kt`:** `homem-corvo-das-torres`, rede de sinais, torres de aviso e comportas de Aeltharyn são de Aeltharyn | nada migra; as torres, os sinais e as comportas de Azeroth (Casa dos Sinais, Posto dos Pipeiros, Casa dos Barqueiros) vêm só dos docs 01, 04 e 07 |
| KA7 | `mode = "mountain_pass"` e os `days` das rotas do `.kt` | irrelevantes a Azeroth: as 9 rotas de `rota.txt` seguem `meio = ?` e `desconhecida` (D-63, D-153); nenhum valor criado. As 5 linhas de fluxo, rio ou sinais (Corre-Largo, linha de torres, As Costas ×2, As Fugas do Véu) não viram rota |
| KA8 | **V-A1 (tríade):** conferência em `dados/npc.txt` (`papeis`): **10 de 10 lugares** com `heroi`, `guardiao` e `benfeitor`; 8 dos 10 `heroi` de lugar são lendários falecidos (os outros dois, Garvane e Nerella, são vivos) | a regra V-A1 não está implementada em `validar_reino.py`; conferida à mão, sem alterar o validador |
| KA9 | **Lacunas do esquema para o reino 5** (sem criar campo): (a) **três coroas e a Mesa** (governo por coroa, herdeiro por coroa, três chaves) e as 15 facções-instituições não têm entidade de "coroa" nem de "órgão"; `reino.txt` guarda `capital = —` e o texto no `tema`; (b) **governo duplo de lugar** (Alvamar tem dois governantes de lugar): `governante` com dois IDs separados por `;`; (c) `classe` `—` dos 8 heróis lendários falecidos (aceito só para falecido no validador); (d) `K4`, `K5` e `K6` (rota e população de Torvelume) são só de Aeltharyn | decisão do autor para o motor; nada é criado agora |

**Nada instalado, nenhum código alterado, `.kt` não executado.** O `.kt` não será atualizado (seção 6).

## 6. Limites

- A conversão de Aeltharyn não foi executada. Para os reinos 2, 3, 4 e 5, os `.txt` do esquema existem (R2-7.1, R3-7.1, R4-7.1 e R5-7.1, `converter_reino2.py`, `converter_reino3.py`, `converter_reino4.py` e `converter_reino5.py`), gerados dos documentos e CSVs, não do `.kt`.
- Divergências entre `.kt` e documentos continuam registradas no doc 07 §3.2 e em `ALINHAMENTO_AELTHARYN.md`; o `.kt` não será atualizado.
