# Coroas de Pedra — matriz de povos nativos, cidades e forasteiros (doc 10)

**Estado:** rascunho de R2-1.6 para aprovação do autor · **Data:** 2026-11-03
**Etiqueta:** tudo é **[PROPOSTO P-01 a P-08]**; só vira cânone com a aprovação (D-36). Cânone de apoio: M-05 (D-24: 12 nativos e 17 forasteiros das Montanhas), doc 01 (cidades e NPCs, D-31 e D-33), doc 03, doc 04, `referencia/dados-brutos/personagem-catalogo.json` (IDs), `plano/CONTRATO_DE_FRONTEIRA_AELTHARYN_COROAS.md`.
**Regras seguidas:** povo escrito em **ID do catálogo** desde a primeira linha; nenhuma quantidade de gente (R2-3.2); nenhuma rota ou tempo (R2-3.1); nenhum povo cidadão, vilão ou leal por natureza (W-01, regra 4); sub-raça só do catálogo; mestiço pela regra D-64 (não alocado); **forasteiro de Aeltharyn não cria rota nem vínculo** (contrato §3.3 e §3.7); único NPC novo: 5 heróis lendários falecidos (§6), fora do limite de principais (D-22).
**Campos adiados:** conferência final de IDs (R2-2.3); competências e classes (R2-4.2).

## 1. Os doze povos nativos **[PROPOSTO P-01]**

Nativo = povo de referência do reino, não cidadania nem lealdade (M-05 §1). Cidades vêm das fichas do doc 01 §2.

| Nº | Povo | ID do catálogo | Cidades onde está | Identidade no reino (uma linha) |
|---:|---|---|---|---|
| 1 | Anão | `anao` | Brasalume, Altavento, Veiaclara, Barcaalta, Portão-Cinza, Cumieira, Rodaviva, Pedra-Ata (8) | mineiros, forjadores, cordoeiros, escribas e guardas; o povo mais espalhado, sem comando comum |
| 2 | Gnomo (das rochas) | `gnomo` (linhagem regional, ver §2.1) | Veiaclara, Barcaalta, Sinuosa, Rodaviva, Geodália (5) | medidores, relojoeiros, guias de rocha e lapidadores |
| 3 | Draconato | `draconato` | Brasalume, Portão-Cinza, Pedra-Ata (3) | vigias, arquivistas e juramento de clã |
| 4 | Altaneiro | `goliath` | Altavento (1) | mensageiros de altitude e guias de passo |
| 5 | Labiríntico | `minotauro` | Sinuosa (1) | guias de galeria e donos do traçado |
| 6 | Escamado Miúdo | `kobold` | Portão-Cinza, Sinuosa, Geodália (3) | hospedeiros, mercadores e gente de galeria estreita |
| 7 | Nascido da Rocha | `elemental-terra` | Cumieira (1) | pedra viva; os poucos que vivem entre grandes e pequenos |
| 8 | Nascido da Brasa | `elemental-fogo` | Brasalume (1) | gente do fogo e da cinza |
| 9 | Cristalino | `cristalino` | Veiaclara, Geodália (2) | gente de luz, som e água ouvida |
| 10 | Construto Desperto | `construto` | Barcaalta, Rodaviva (2) | trabalhadores e memória de oficina; origem aberta (doc 01 C-08) |
| 11 | Giganteano | `gigante` | Cumieira (1) | clã de juramento; escala grande |
| 12 | Felino das Nuvens | `felino-das-nuvens` | Altavento, Pedra-Ata (2) | caçadores e guias de passo alto |

**Conferência:** 30 vagas de povo em 10 cidades = 3 por cidade; **toda cidade tem ≥ 2 povos** (V-36); nenhum nativo fica sem cidade.

### 1.1 O que nenhum povo é

Nenhum dos 12 é, por natureza, cidadão leal, vilão, subordinado ou governante. Cada povo tem membros em cidades diferentes com opiniões diferentes; os povos de uma só cidade (Altaneiro, Labiríntico, Nascido da Rocha, Nascido da Brasa, Giganteano) têm **famílias fora dela**, sem cidade-mãe própria (ASSUMINDO, para não criar cidade nova; onde vivem é **desconhecido** até R2-3.1).

## 2. Sub-raças do catálogo **[PROPOSTO P-02]**

Só IDs que existem no JSON. Onde cada uma aparece é ASSUMINDO pelo nome e pelo tom da cidade; a troca é livre e não afeta outros docs.

| Povo | Sub-raça (ID) | Onde aparece |
|---|---|---|
| Anão | `anao-das-forjas` | Brasalume |
| Anão | `anao-das-montanhas` | Cumieira, Pedra-Ata, Altavento |
| Anão | `anao-dos-abismos` | Rodaviva, Portão-Cinza |
| Gnomo | `gnomo-das-engrenagens` | Rodaviva, Barcaalta |
| Gnomo | `gnomo-dos-vales` | **nenhuma** (é a linhagem de Aeltharyn; ver §2.1) |
| Draconato | `draconato-cinzento` | Portão-Cinza, Brasalume |
| Draconato | `draconato-de-cobre` | Pedra-Ata |
| Altaneiro | `altaneiro-das-neves`; `altaneiro-dos-penhascos` | Altavento (as duas) |
| Labiríntico | `labirintico-dos-arquivos`; `labirintico-das-fortalezas` | Sinuosa (as duas) |
| Escamado Miúdo | `escamado-das-minas` | Sinuosa, Portão-Cinza |
| Escamado Miúdo | `escamado-das-ruinas` | Geodália |

Sem sub-raça no catálogo (não se inventa): Nascido da Rocha, Nascido da Brasa, Cristalino, Construto Desperto, Giganteano, Felino das Nuvens.

### 2.1 O "Gnomo (das rochas)"

O doc 01 e o M-05 chamam a linhagem das Coroas de **Gnomo (das rochas)**. O catálogo só tem `gnomo-das-engrenagens` e `gnomo-dos-vales`; **`gnomo-das-rochas` não existe**. Tratamento (ASSUMINDO, como Aeltharyn trata "centauro-das-florestas"): é **linhagem regional editorial** do ID `gnomo`, sem ID próprio de sub-raça; `gnomo-dos-vales` fica reservado a Aeltharyn. A conferência dessa escolha com o catálogo é do R2-2.3 (decisão D-36, ponto 2).

## 3. Mestiços **[D-64, substitui P-03]**

Mestiço **não é alocado**: não há tabela de mestiços por cidade nem contagem deles no reino. Pode nascer na cidade de qualquer um dos pais (por exemplo, filho de anão e de gnomo em Brasalume ou em Rodaviva), em qualquer combinação de raças ou sub-raças do catálogo; o jogador escolhe. Regra completa e os 20 exemplos nomeados: `referencia/racas-e-povos/hibridos.md`.

Para as Coroas valem só estes limites: mestiço não é povo, facção ou casta; não prova assentamento antigo e **não cria rota** (a origem do lado forasteiro, §5, continua desconhecida); não toca o foro indefinido dos Despertos (doc 05, lacuna) por ser mestiço; nenhum NPC novo nasce disso.

## 4. Cidades × povos **[PROPOSTO P-04]**

Povos presentes (doc 01), com sub-raças de §2. Mestiços não entram na tabela (§3).

| Cidade | Nativos | Sub-raças | Forasteiros típicos (§5) |
|---|---|---|---|
| Brasalume | Anão; Nascido da Brasa; Draconato | anao-das-forjas; draconato-cinzento | Orcano; Ursino; Humano |
| Altavento | Altaneiro; Felino das Nuvens; Anão | altaneiro (2); anao-das-montanhas | Nascido do Vento; Estelar; Elfo |
| Veiaclara | Gnomo; Cristalino; Anão | — | Elfo; Luminar |
| Barcaalta | Anão; Gnomo; Construto Desperto | gnomo-das-engrenagens | Goblin; Pequenino; Humano; Ratoide |
| Portão-Cinza | Anão; Draconato; Escamado Miúdo | anao-dos-abismos; draconato-cinzento; escamado-das-minas | Humano; Elfo; Hobgoblin; Lupino |
| Sinuosa | Labiríntico; Escamado Miúdo; Gnomo | labirintico (2); escamado-das-minas | Umbral; Moldável; Goblin |
| Cumieira | Giganteano; Nascido da Rocha; Anão | anao-das-montanhas | Orcano; Porcino; Ursino |
| Rodaviva | Construto Desperto; Gnomo; Anão | gnomo-das-engrenagens; anao-dos-abismos | Pequenino; Goblin; Humano |
| Geodália | Cristalino; Gnomo; Escamado Miúdo | escamado-das-ruinas | Luminar; Tiefling; Elfo |
| Pedra-Ata | Anão; Draconato; Felino das Nuvens | anao-das-montanhas; draconato-de-cobre | Humano; Pequenino; Tiefling; Elfo |

## 5. Forasteiros **[PROPOSTO P-05]**

Os 17 de M-05 §4 (coluna Montanhas = F). **Forasteiro** = origem fora das Coroas, como residente, viajante, mercador, refugiado, estudioso ou diáspora; **motivo** é possibilidade, não destino do povo (um povo aparece em vários motivos). **Origem e caminho: desconhecidos**; nenhuma linha cria rota, tratado ou comunidade grande. Portas de entrada reconhecidas: Portão-Cinza (Livro de Passagens) e Barcaalta (Registro da Descida), conforme os docs 01 e 05.

| ID | Povo | Onde aparece | Motivos possíveis |
|---|---|---|---|
| `humano` | Humano | Portão-Cinza, Pedra-Ata, Rodaviva, Brasalume, Barcaalta | comércio, ofício, família mista |
| `elfo` | Elfo | Portão-Cinza, Pedra-Ata, Geodália, Altavento, Veiaclara | estudo, viagem, peregrinação; **individual**, sem mandato de Aeltharyn (contrato §3.2) |
| `orcano` | Orcano | Brasalume, Cumieira | trabalho pesado, família mista, mercenário avulso |
| `pequenino` | Pequenino | Barcaalta, Rodaviva, Pedra-Ata | hospedagem, cozinha de cais, comércio |
| `tiefling` | Nascido do Crepúsculo | Pedra-Ata, Geodália | arquivo, estudo, exílio |
| `aasimar` | Luminar | Veiaclara, Geodália | peregrinação à nascente, estudo da luz |
| `goblin` | Goblin | Barcaalta, Sinuosa, Rodaviva | oficina, cais, mercado |
| `hobgoblin` | Goblin de Legião | Portão-Cinza | guarda de caravana, serviço avulso |
| `bugbear` | Ursino Sombrio | Brasalume, Cumieira | trabalho, refúgio |
| `homem-rato` | Ratoide | Barcaalta | cais, comércio, galerias de manutenção |
| `homem-urso` | Ursino | Cumieira, Brasalume | pastoreio, trabalho, caça de altitude |
| `homem-lobo` | Lupino | Portão-Cinza | guia de caravana, vigia sazonal |
| `homem-javali` | Porcino | Cumieira | pastoreio, comércio |
| `elemental-ar` | Nascido do Vento | Altavento | mensageiro, viajante de passos |
| `sombrio` | Umbral | Sinuosa | guia de galeria escura, refúgio |
| `metamorfo` | Moldável | Sinuosa | mercado de fendas; sua presença é **motivo de desconfiança social**, não prova de crime |
| `ancestral-estelar` | Estelar | Altavento | observação de céu alto |

**Regras:** forasteiro só aparece no jogo se tiver **motivo individual**; um grupo de forasteiros de um mesmo povo é **comunidade** e exige registro (doc 04 §4; D-36). O Livro de Passagens anota **quem sobe e quem desce**, não de onde veio por rota (doc 04 §3). Lupino, Hobgoblin, Ursino Sombrio e Moldável não são tratados como ameaça por natureza.

## 6. Papéis por povo e lacunas **[PROPOSTO P-06]**

Papel = herói, guardião ou benfeitor/liderança já aprovado (doc 01 e doc 03). Lacuna de herói preenchida por **5 heróis lendários falecidos novos** (§6.1); lacunas de guardião e benfeitor **ficam abertas** (D-33 só exige tríade por cidade).

| Povo | Herói | Guardião | Benfeitor ou liderança |
|---|---|---|---|
| Anão | Hrodgar Primeiro-Fogo; Dorn Primeira-Descida; Sevro Selo-Primeiro | Gundrik Muralha-Lenta; Torgrim Corda-Dupla | Brunhild Degrau-Fundo; Haldra Tinta-Funda; Brundar Veio-Alto; Velka Selo-Firme (governante) |
| Gnomo (das rochas) | Ysmira Primeiro-Degelo; Tobiah Mola-Mestra | Pelma Gota-Medida | Fenwick Mola-Solta; Pimm Veio-Pequeno (liderança da Casa das Comportas) |
| Draconato | Brakka Luz-no-Portão | Thaurak Escama-Fria | Sharra Selo-de-Voto; Varkhan Escama-Vigia; Maro Tinta-Velha (arquivista) |
| Altaneiro | Vireo Vento-Rápido | lacuna aberta | Iskra Pena-Longa |
| Labiríntico | Orla Fio-de-Mapa | Nael Curva-Antiga | Ilmeth Curva-Segura |
| Escamado Miúdo | **Tik Fenda-Funda** (novo) | lacuna aberta | Skit Fenda-Quente; Pip Vapor-Curto |
| Nascido da Rocha | Maur Camada-Velha | lacuna aberta | lacuna aberta |
| Nascido da Brasa | **Embra Cinza-Viva** (novo) | lacuna aberta | Karna Brasa-Mansa; Zeka Faísca-Mansa |
| Cristalino | Ciri Faceta-Primeira | Iridel Luz-Partida | Aurea Faceta-Longa; Aurel Faceta-Calma |
| Construto Desperto | **Dois-Fusos** (novo) | Primeira-Peça | Seis-Eixos; Quinze-Pinos |
| Giganteano | **Throm Juramento-Alto** (novo) | Gorrak Pedra-Grande | lacuna aberta |
| Felino das Nuvens | **Naru Sombra-de-Neve** (novo) | Zhae Garra-de-Névoa | Miraq Salto-de-Névoa |

**Correção à parte 2 do doc 01:** Fenwick Mola-Solta (`npc.fenwick-mola-solta`, Gnomo das rochas) faltava na tabela "cobertura por povo" (§4 da parte 2). Entra aqui, sem mudar cargo, cidade ou ficha.

### 6.1 Os cinco heróis lendários novos

Todos **falecidos**, sem ficha completa (`categoria: herói lendário`), com **confirmado** (a lenda é contada) e **desconhecido** (o que de fato ocorreu), no mesmo molde de doc 01 §3.1. Nenhum é principal; nenhum resolve mistério de Aeltharyn.

- **Tik Fenda-Funda** (`npc.tik-fenda-funda`, Escamado Miúdo, Sinuosa): lenda de quem entrou na fenda de uma galeria nova e voltou com o mapa dela. **Desconhecido:** se o mapa existiu.
- **Embra Cinza-Viva** (`npc.embra-cinza-viva`, Nascido da Brasa, Brasalume): lenda de quem carregou brasa de uma fornalha apagada a outra, por Cinzeiro. **Desconhecido:** qual fornalha apagou.
- **Dois-Fusos** (`npc.dois-fusos`, Construto Desperto, Rodaviva): lenda do primeiro construto que se recusou a ser desligado por um dono. **Desconhecido:** se o dono existiu.
- **Throm Juramento-Alto** (`npc.throm-juramento-alto`, Giganteano, Cumieira): lenda de quem levantou o Pórtico para que os menores passassem sem curvar-se. **Desconhecido:** se a versão dos grandes ou a dos menores é a que conta o Pórtico.
- **Naru Sombra-de-Neve** (`npc.naru-sombra-de-neve`, Felino das Nuvens, Altavento): lenda de quem seguiu um chamado do passo e não voltou. **Desconhecido:** que chamado e por que o passo não registra a volta.

Nomes conferidos contra os reservados do reino 1 (M-10): 0 colisão.

## 7. Representação e o que este documento não decide **[PROPOSTO P-07]**

- **Regras:** nenhum povo é só vilão, só herói ou só funcionário (doc 01, parte 2 §4); com os papéis acima, os 12 povos têm ao menos 1 herói (aprovado ou lendário); 11 têm também guardião, benfeitor ou liderança nomeados; o **Nascido da Rocha** só tem o herói (Maur Camada-Velha), lacuna deixada aberta.
- **Cultura, religião e língua** de cada povo: não decididas (M-05 §7); convenções de nome por povo em M-10.
- **Quantidade de gente, proporção entre povos, nascimento:** R2-3.2.
- **Relação entre povos nas cidades** (afeto, dívida, rivalidade): R2-4.3.
- **Povos de Aeltharyn** (Elfo, Orcano, Pequenino, Gnomo dos vales, Goblin, Anão, Ratoide, Humano): só como forasteiros individuais; nenhuma cidade de Aeltharyn alterada (contrato §3.9).

## 8. Conferências **[PROPOSTO P-08]**

| Item | Resultado |
|---|---|
| 12 nativos | 12, todos com ID do catálogo (gnomo com ressalva, §2.1) |
| 17 forasteiros | 17 do M-05, nenhum a mais, nenhum a menos |
| Cidades com ≥ 2 povos | 10 de 10 (3 cada) |
| Sub-raças | 13 do catálogo (12 em uso) |
| Mestiços | regra D-64: nenhum alocado, sem lista fechada |
| NPC novo | 5 lendários falecidos, IDs `npc.*` únicos, sem colisão |
| Rota, tempo, população | 0 |
| Termos de fronteira | Elfo, Orcano, Goblin, Pequenino, Humano, Anão, Ratoide: só como indivíduos; nenhuma menção a Torvelume, mina selada ou Mistério D |

## 9. Decisão pendente

**D-36:** aprovar (1) a matriz de povos, sub-raças e forasteiros (mestiços seguem a D-64, sem alocação); (2) o tratamento do Gnomo (das rochas) como linhagem regional editorial do ID `gnomo` ou, como alternativa, usar `gnomo-das-engrenagens` como sub-raça das Coroas; (3) os 5 heróis lendários novos. Sem aprovação, R2-2.3 não parte desta matriz.
