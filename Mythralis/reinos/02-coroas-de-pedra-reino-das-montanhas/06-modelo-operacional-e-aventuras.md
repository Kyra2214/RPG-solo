# Coroas de Pedra — modelo operacional, relógios e oportunidades (doc 06)

**Estado:** rascunho de R2-1.7 para aprovação do autor · **Data:** 2026-11-03
**Etiqueta:** tudo é **[PROPOSTO CL-01 a CL-07, O-01 a O-10]**; só vira cânone com a aprovação (D-37). Cânone de apoio: doc 01 (partes 1 a 4), doc 03, doc 04, doc 05; molde: `reinos/01-.../06-...` e `12-relogios.md` do reino 1.
**Regras seguidas:** relógio nunca avança por calendário; avanço exige causa observável, nexo e localidade; nenhum relógio resolve ou toca o Mistério D de Aeltharyn; nenhum liga as Coroas a evento de Aeltharyn; nenhum número de população, estoque ou preço. **O doc 11** do plano é a ata de fechamento (R2-8.2); R2-1.7 não cria arquivo com esse número.
**ASSUMINDO:** "o que trava", "quem paga" e "efeito da inação" são leitura editorial derivada só do que as fichas já dizem; o **progresso inicial** de cada relógio é escolha de partida e o autor pode trocá-lo.

## 1. Regras comuns

- Nenhum relógio avança por calendário. A estação só cria condição (agenda sazonal: R2-3.4).
- Todo avanço exige **causa observável** (algo que aconteceu na cena), **nexo** com o relógio e **localidade** afetada. Máximo de **1 segmento por causa**; uma cena não avança todos os relógios.
- **Inação:** o mundo não pune o jogador automaticamente. Ignorar uma oportunidade só avança um relógio se o gatilho observável já existe na cena.
- Um avanço muda a **escala** da situação, não escolhe a consequência. Mistérios (doc 01 parte 4) não têm relógio; evidência se acumula na narração.

## 2. Custo por segmento (vale para os 7)

| Segmento | Escala | Custo para quem age | O que o mundo mostra |
|---|---|---|---|
| 1–2 | sinais locais | baixo: tempo e atenção | pistas, rumores, relatos isolados |
| 3–4 | pressão sobre uma cadeia | médio: favor, patrulha, material | a cadeia de dependência (doc 05 §2.2) fica visível; NPCs mudam de agenda |
| 5 | decisão pública exigida | alto: custo político ou material real em qualquer escolha | o Concílio, a câmara de arbitragem ou o governo local precisa decidir; nenhuma saída é grátis |
| 6 | efeito ao completar | consequência irreversível dentro do escopo escrito | muda estado, não resolve a trama |

## 3. Os sete relógios

### `clock.combustivel` — CL-01 Combustível de Brasalume (3/6)
| Campo | Texto |
|---|---|
| O que move | fornalhas gastando mais do que a cidade consegue repor; disputa pelo turno do fogo principal (L-03) |
| O que trava (ASSUMINDO) | turno acordado entre as famílias; combustível registrado pelo Concílio; redução voluntária do consumo |
| Quem paga (ASSUMINDO) | famílias de forja pequenas; Barcaalta (peças e carga dependem do metal); a câmara de arbitragem |
| Ao completar | o fogo principal passa a funcionar por turnos reduzidos; **origem do combustível continua desconhecida** |
| Efeito da inação (ASSUMINDO) | só avança se houver nova disputa pelo turno ou nova queda de fornecimento observada |
| Entidades | `city.brasalume`, `npc.gundrik-muralha-lenta`, `npc.karna-brasa-mansa`, `faction.casa-das-fornalhas` |
| Fonte | doc 01 C-01, doc 05 §1 (L-03), §2.1 |

### `clock.comportas` — CL-02 Comportas e degelo de Veiaclara (2/6)
| Campo | Texto |
|---|---|
| O que move | degelo fora de data; cidades a jusante pedindo água sem pagar manutenção; registro alterado (MP-01) |
| O que trava (ASSUMINDO) | registro auditado; manutenção acordada; prova de quem alterou o registro |
| Quem paga (ASSUMINDO) | cidades a jusante; Veiaclara; a Casa das Comportas |
| Ao completar | uma comporta cede e a água sobe ou falta em cidades a jusante (**só dentro das Coroas**) |
| Efeito da inação (ASSUMINDO) | só avança com prova nova de registro alterado ou com degelo fora de data observado |
| Entidades | `city.veiaclara`, `npc.pelma-gota-medida`, `npc.pimm-veio-pequeno`, `faction.casa-das-comportas` |
| Fonte | doc 01 C-03, N-07; doc 05 §1 (L-02) |

### `clock.cais` — CL-03 Cais saturado e registros em conflito (3/6)
| Campo | Texto |
|---|---|
| O que move | mais barcas do que o cais comporta; carga e taxa não batem; regras sobre construtos de carga |
| O que trava (ASSUMINDO) | conferência dos dois registros (L-09); limite de barcas por jornada; regra provisória de construtos |
| Quem paga (ASSUMINDO) | famílias de cais; Mesa do Cais; Barcaalta e as cidades que dependem da Descida |
| Ao completar | o cais fecha por conferência até a Mesa resolver; mercadorias ficam retidas (**a Descida não é descrita rio abaixo**) |
| Efeito da inação (ASSUMINDO) | só avança com nova barca sem registro ou novo conflito de taxas |
| Entidades | `city.barcaalta`, `npc.torgrim-corda-dupla`, `npc.seis-eixos`, `faction.companhia-da-descida` |
| Fonte | doc 01 C-04; doc 05 §1 (L-09), §3 |

### `clock.passagens` — CL-04 Livro de Passagens e viajantes sem registro (2/6)
| Campo | Texto |
|---|---|
| O que move | mais viajantes sem registro; páginas arrancadas (MP-02); tarifas em disputa |
| O que trava (ASSUMINDO) | cópia do Livro nas casas de ofício; tarifa comum provisória; vigias reforçados |
| Quem paga (ASSUMINDO) | Mesa do Portão; viajantes e caravanas; Pedra-Ata (hospedagem) |
| Ao completar | o Portão passa a exigir registro duplo a quem sobe e a quem desce, por decisão da Mesa |
| Efeito da inação (ASSUMINDO) | só avança com nova página arrancada ou novo caso de delito de custódia observado |
| Entidades | `city.portao-cinza`, `npc.thaurak-escama-fria`, `faction.vigias-do-limiar` |
| Fonte | doc 01 C-05, N-02; doc 05 §1 (L-04) |

### `clock.assento` — CL-05 Fim do ciclo do assento (2/6)
| Campo | Texto |
|---|---|
| O que move | o ciclo de Velka Selo-Firme termina sem sucessor combinado; cada cidade quer o assento por primeiro; cópias da ata em disputa (MP-04) |
| O que trava (ASSUMINDO) | acordo de ordem entre pelo menos 7 cidades; arbitragem das cópias (L-08); proposta de sucessão aceita |
| Quem paga (ASSUMINDO) | Pedra-Ata; cidades pequenas; Arquivo das Coroas |
| Ao completar | o Concílio fica sem presidente de guarda por um ciclo e as matérias reservadas param |
| Efeito da inação (ASSUMINDO) | avança só quando o fim do ciclo é cumprido na cena sem acordo anunciado |
| Entidades | `city.pedra-ata`, `npc.velka-selo-firme`, `npc.maro-tinta-velha`, `faction.arquivistas-das-coroas` |
| Fonte | doc 01 C-10, N-01, N-12; doc 04 §2 |

### `clock.pecas` — CL-06 Peças raras e foro dos Despertos (2/6)
| Campo | Texto |
|---|---|
| O que move | peças raras somem e cada oficina culpa outra; construtos antigos pedem direitos que a cidade não definiu (MP-03) |
| O que trava (ASSUMINDO) | registro de peças na Casa das Peças; foro provisório acordado pelas oficinas; prova de onde as peças foram |
| Quem paga (ASSUMINDO) | oficinas menores; Barcaalta; construtos sem dono |
| Ao completar | uma oficina fecha por falta de peça ou a Assembleia vota um foro provisório; **não decide a origem dos Despertos** |
| Efeito da inação (ASSUMINDO) | só avança com nova peça sumida ou novo pedido formal de construto |
| Entidades | `city.rodaviva`, `npc.fenwick-mola-solta`, `npc.primeira-peca`, `npc.quinze-pinos`, `faction.oficinas-reunidas` |
| Fonte | doc 01 C-08, N-09; doc 04 §9, §11; doc 05 §1 (lacuna) |

### `clock.passos` — CL-07 Nevasca e passos fechados (1/6)
| Campo | Texto |
|---|---|
| O que move | nevasca que fecha os passos; mensageiros que recusam voar quando o vento muda |
| O que trava (ASSUMINDO) | mensageiros avisados com antecedência; depósitos de emergência; passos reabertos |
| Quem paga (ASSUMINDO) | Altavento; cidades que dependem da Rede dos Passos Altos; Portão-Cinza |
| Ao completar | o reino fica dividido em blocos por um ciclo; avisos entre cidades atrasam |
| Efeito da inação (ASSUMINDO) | só avança com nova nevasca observada; estação sozinha não avança |
| Entidades | `city.altavento`, `npc.iskra-pena-longa`, `npc.vireo-vento-rapido`, `faction.rede-dos-passos-altos` |
| Fonte | doc 01 C-02; doc 05 §2.2 |

**Conferência:** 7 relógios, progresso inicial 1/6 a 3/6 (nenhum acima de 3), 0 relógio a menos de 1 segmento do fim. Nenhum relógio envolve Aeltharyn.

## 4. Conflitos com causa concreta

Cada conflito (C-nn) da parte 1 do doc 01, com causa **observável**, quem perde e saída possível com custo.

| ID | Cidade | Causa concreta (observável) | Quem perde | Saída possível e custo | Relógio |
|---|---|---|---|---|---|
| C-01 | Brasalume | consumo de combustível acima do que a cidade repõe | famílias pequenas de forja | turnos ou compra do que falta (**origem desconhecida**); custo político | CL-01 |
| C-02 | Altavento | nevasca fechou os passos antes do previsto | mensageiros, cidades isoladas | depósitos de emergência; custo de mantimentos | CL-07 |
| C-03 | Veiaclara | degelo mudou de data; jusante pede sem pagar manutenção | cidades a jusante | acordo de manutenção; custo de verba | CL-02 |
| C-04 | Barcaalta | mais barcas que o cais comporta; registros não batem | famílias de cais | limite de barcas; custo de renda | CL-03 |
| C-05 | Portão-Cinza | tarifa em disputa; viajantes sem registro | caravanas, Mesa do Portão | tarifa comum; custo de atrito | CL-04 |
| C-06 | Sinuosa | galerias novas; mapas rivais | guias, viajantes | mapa de arbitragem; custo da Rotunda | (MP-06) |
| C-07 | Cumieira | menores pedem passagens à sua escala (L-07) | menores ou grandes | acesso mínimo a cada escala; custo de obra e de honra | — |
| C-08 | Rodaviva | peças raras somem; sem lei sobre Despertos | oficinas menores, construtos | registro de peças e foro provisório | CL-06 |
| C-09 | Geodália | limite de extração disputado; faceta rachada por tremor | lapidadores | cota por faceta (L-06); custo de renda | — |
| C-10 | Pedra-Ata | assento rotativo sem ordem fixada; atas contestadas | cidades pequenas | acordo de ordem; custo político | CL-05 |

**Observação:** C-06, C-07 e C-09 não viram relógio nesta partida; avançam só se o autor os promover (D-37).

## 5. Oportunidades de aventura (uma por cidade)

Oportunidade = ponto de entrada, não trama. Nenhuma exige herói predestinado; todas podem ser ignoradas, resolvidas por NPC ou piorar por falta de ação.

| ID | Cidade | Gancho | Quem pede | Em jogo | Ligação |
|---|---|---|---|---|---|
| O-01 | Brasalume | descobrir de onde vem ou deveria vir o combustível | Gundrik, famílias de forja | turno do fogo; confiança entre famílias | CL-01, C-01 |
| O-02 | Altavento | levar aviso por um passo que os mensageiros recusam | Iskra Pena-Longa | tempo, risco, mantimentos | CL-07, C-02 |
| O-03 | Veiaclara | conferir o registro das comportas antes do degelo | Pelma Gota-Medida | verdade do registro; água a jusante | CL-02, MP-01 |
| O-04 | Barcaalta | conferir carga contra taxa de uma barca sem registro | Torgrim Corda-Dupla | dinheiro do cais; reputação do jogador | CL-03, MP-05 |
| O-05 | Portão-Cinza | guiar uma caravana por um registro duvidoso | Thaurak Escama-Fria | tarifa; vigia; risco de delito | CL-04, MP-02 |
| O-06 | Sinuosa | conferir um corredor novo contra três mapas | Nael Curva-Antiga | rota; culpa dos guias | MP-06, C-06 |
| O-07 | Cumieira | medir uma passagem para os menores sem ofender o salão | Maur ou Gorrak (sem arbitragem formal) | honra de clã; obra | C-07 |
| O-08 | Rodaviva | rastrear uma peça rara até a oficina que a guarda | Fenwick Mola-Solta | oficina; foro dos Despertos | CL-06, MP-03 |
| O-09 | Geodália | medir o cristal tirado sem fazer a caverna cantar | Aurea Faceta-Longa | cota; confiança dos lapidadores | C-09 |
| O-10 | Pedra-Ata | achar a cópia mais antiga de uma ata contestada | Maro Tinta-Velha | arquivo; ordem do assento | CL-05, MP-04 |

## 6. Como um mistério ou relógio muda

- Mistério: **evidência cumulativa** (≥ 2 provas independentes) antes de a narração fixar uma hipótese; a hipótese fixada obedece aos vetos do doc 01 parte 4.
- Relógio: **1 segmento por causa observável**, com a escala da §2; ao completar, vira mudança de estado, não solução.
- Retorno de relógio (trava efetiva) tira **1 segmento** por vez, com custo real (§2).
- Nenhum desses movimentos reescreve uma ficha, uma lei ou um pacto aprovado.

## 7. Conferências

- 7 relógios; 10 conflitos; 10 oportunidades (1 por cidade); 6 mistérios (doc 01 parte 4).
- 0 NPC, cidade, facção ou assentamento novo; 0 rota ou tempo; 0 quantidade.
- Contrato anti-invenção (§9): fixo, livre, registro e proibido definidos; só vetos citam Torvelume e Aeltharyn.
- Termos de fronteira: **Torvelume, mina selada, Espinha de Vidro, Sete-Bigornas, Lúmen, Argente** não aparecem como fato do reino 2 (só em vetos de mistério, no doc 01); **minério e carvão** só com "origem desconhecida".

## 8. Decisão pendente

**D-37** (ver doc 01 parte 4, §4): aprovar os seis mistérios, os sete relógios e o progresso inicial, os dez conflitos e as dez oportunidades. Opções: **A.** aprovar tudo; **B.** trocar mistério, relógio, progresso inicial ou promover C-06, C-07 ou C-09 a relógio. **Recomendação: A.**

## 9. Contrato anti-invenção (R2-6.4)

**Estado:** rascunho de R2-6.4 para aprovação do autor (D-57) · **Etiqueta:** **[PROPOSTO]**. Vale para qualquer proposta da IA sobre as Coroas de Pedra. Herda a lista de verificação e o contrato de Aeltharyn (`reinos/01-.../06-...md` §11 e §11.1), sem alterá-los, e se apoia em `14-camadas-de-informacao.md`, `13-misterios-limites.md`, `12-relogios.md`, doc 07 e `plano/CONTRATO_DE_FRONTEIRA_AELTHARYN_COROAS.md`. A separação em fixo, livre, registro e proibido é leitura editorial (ASSUMINDO).

### 9.1 Verificação antes de aceitar uma proposta

- Os locais e entidades existem (registro mestre) ou foram criados como conteúdo novo permitido (§9.4)?
- A cronologia é compatível (doc 01 parte 3 e parte 5: Contagem das Atas)?
- O agente poderia saber disso (`12-conhecimento.csv`, camada do fato)?
- O agente tem meios e autoridade (competência e limite em `12-competencias.csv`; o Concílio convoca, não manda)?
- A rota e o tempo são conhecidos? Toda duração do reino é `desconhecida`.
- A proposta transforma hipótese em fato sem evidência? (mistério: ≥ 2 provas independentes, §6)
- A consequência contradiz estado persistente, lei (L-01 a L-10) ou decisão anterior?
- O segredo é revelado sem caminho de descoberta cumprido?

Se não puder ser confirmado, a IA reduz a precisão, mantém a incerteza ou pede aprofundamento. Não preenche lacuna com número exato nem fato definitivo para a cena fluir.

### 9.2 Quatro classes

| Classe | O que é | O que a IA faz |
|---|---|---|
| **Fixo** | Tudo o que já tem ID ou fonte: as 10 cidades (C-01 a C-10), 20 assentamentos (S-01 a S-20), 14 facções (F-01 a F-14), 45 pessoas (12 principais, 20 lideranças, 13 heróis lendários **mortos**), 7 relógios, 6 mistérios e o "a verdade não pode ser", 10 leis, 13 eventos-tipo, as sementes, os 12 nativos do catálogo, o Concílio (composição, voto, selo), os pactos, as rotas escritas **sem duração**, os recursos, o calendário, os 41 fatos e a camada de cada um, e as lacunas (*desconhecido*) | Usa; não altera nem contradiz. Lacuna continua lacuna |
| **Livre** | Detalhe de cena sem consequência persistente: clima, cheiro, objeto comum, figurante anônimo sem ID, fala dentro do tom (`12-voz.csv`) | Inventa sem registro, se não contradisser um fixo, não prometer recompensa e não criar dever, dívida ou mudança de estado |
| **Exige registro** | O que muda o mundo ou cria entidade: morte ou ferimento de pessoa com ID, vacância ou sucessão, passagem ou cais fechado, pacto ou assento suspenso, relógio que avança, NPC, organização ou assentamento recorrente novo, consequência de decisão do jogador, fato restrito ou secreto revelado | Pode **propor**; só vale depois de registrado com estado anterior, novo estado, momento, causa, agentes, quem sabe e grau de certeza (doc 02 §2) |
| **Proibido** | §9.3 | Não faz; reduz a precisão, mantém a incerteza ou pede aprofundamento |

### 9.3 Proibido (nenhuma proposta, nenhum registro da IA)

- escolher quem preside o Concílio depois de Velka Selo-Firme, ou dizer qual cidade recebe o assento primeiro (o doc 04 §2.2 não diz); criar membro, cargo ou delegado fora das regras do doc 04 §2.1 e §8;
- criar uma 11ª cidade, um 13º povo nativo de referência ou ressuscitar um dos 13 heróis lendários;
- declarar guerra ou fim do pacto; tratar secessão (suspensão do assento) como guerra automática (doc 04 §8: o Concílio responde por petição e arbitragem, não por tropa);
- resolver um mistério ou escolher uma das hipóteses abertas;
- inventar número de população, estoque, distância ou duração que o texto deixa desconhecido (origem do combustível, minério e carvão incluídas);
- conceder efeito mecânico por nome, título ou lore;
- revelar fato restrito ou secreto sem o caminho de descoberta cumprido, ou mudar a camada de um fato;
- avançar relógio sem gatilho observável (`12-relogios.csv`) ou por calendário;
- criar rota, tempo, passagem ou distância entre as Coroas e Aeltharyn, ou dizer algo sobre Aeltharyn além do que o contrato de fronteira permite; tocar o Mistério D;
- escrever conteúdo dos reinos 3 a 5.

### 9.4 Casos pedidos

| Pedido | Regra |
|---|---|
| **Novo governante ou rei** | Proibido criar. As Coroas não têm rei: o Concílio tem presidente sem regra de sucessão confirmada. Vacância do presidente só pelo evento já escrito (`event.assento-vazio`), com causa e efeito registrados; quem assume é o presidente de guarda de Pedra-Ata (doc 04 §8). Outra pessoa exige decisão do autor |
| **Nova cidade** | Proibido. Aldeia, posto ou taverna sem ID é detalhe livre enquanto não tiver função recorrente. Para virar entidade, entra como **proposta** ao autor com cidade-mãe, liderança, obrigação, povo do catálogo e fonte "proposto"; só vira ID depois de aprovada e registrada no registro mestre |
| **Guerra** | A IA não declara. Escalada local (bloqueio, disputa de turno, patrulha reforçada) pode ser proposta como oportunidade condicionada a gatilho observável, com custos; suspender o assento não é guerra |
| **NPC ou organização nova** | Figurante sem ID: livre. Com papel recorrente: proposta com `npc.` ou `faction.` novo, cidade, facção-mãe (ou "instituição"), povo do catálogo e fonte "proposto"; nunca com segredo, relação ou conhecimento que mude um mistério |
| **Morte ou ferimento de pessoa com ID** | Exige registro com causa e agentes; a IA não mata para dar peso à cena |
| **Rota ou tempo de viagem** | Proibido inventar: toda duração é `desconhecida` (doc 07). Pode dizer que o tempo é desconhecido |

### 9.5 Teste rápido (4 perguntas, em ordem)

1. Já existe ID ou fonte? **Fixo**: usar como está.
2. Está na lista proibida? **Não fazer.**
3. Muda estado, cria entidade ou revela restrito ou secreto? **Propor e registrar** antes de tratar como fato.
4. Nenhuma das anteriores e sem consequência? **Livre.**

Em dúvida entre duas classes, vale a mais restritiva.

### 9.6 Decisão pendente

**D-57:** aprovar este contrato. Opções: **A.** aprovar (recomendada); **B.** mover item entre classes ou trocar caso (diga qual). **Aguardando o autor.**
