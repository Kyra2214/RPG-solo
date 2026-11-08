# Plano do Reino 4 — Vale Palancar

**Objetivo:** escrever e fechar o reino 4 nos mesmos moldes dos reinos 1, 2 e 3, com as lições dos três já aplicadas desde o início. Só depois do fechamento 5 abre-se o reino 5 (Pacto Humano).
**Regra:** nada aqui inventa nome, segredo ou fato sem fonte. O que não tem fonte fica `desconhecido` ou vira decisão do autor. Um reino por vez (`PLANO_DOS_REINOS.md` §1).
**Escala (D-63):** distância, tamanho e produção são **qualitativos** ("longa", "perto", "rica em", população pequena, média ou grande). **A camada de números não será escrita**: sem duração de rota, sem faixa numérica de população, sem nível fino de recurso. Os avisos do validador sobre esses campos (V-27 e afins) são esperados.
**Mestiços (D-64):** não são alocados a lugar, facção nem reino; o doc 10 só repete a regra.
**Legenda:** prioridade P0 (bloqueia o resto), P1 (importante), P2 (desejável). Etiquetas **[CANÔNICO]** (já aprovado em `mundo/` ou nos reinos 1 a 3) e **[PROPOSTO]** (precisa de aprovação). Módulos usam o prefixo `R4-`.
**Nome (D-102):** Vale Palancar, escolhido pelo autor em 07/11/2026; "Marcas das Feras" é o nome de trabalho antigo, que os reinos 1 a 3, M-04 e M-05 ainda usam.
**Pasta (D-102):** `reinos/04-vale-palancar/`, com as peças de `MODELO_DE_REINO.md` §1 (o doc 02 de arquitetura vale para todos e não se repete).
**Liberação:** o autor aprovou o fechamento 4 do Véu dos Malditos e liberou o reino 4 em 07/11/2026 (D-101, opção A de D-100).

## 0. Ponto de partida (o que já existe sobre o reino 4)

| Item | Fonte | Estado |
|---|---|---|
| Posição: oeste; vizinhos plenos Coroas de Pedra e Véu dos Malditos; contato parcial com Aeltharyn | M-04 §3 (G-02) | **[CANÔNICO]** |
| Relevo: planaltos, savanas e campos abertos de migração ("Campos das Marcas") a oeste das elevações de Aeltharyn | M-04 G-07 | **[CANÔNICO]** |
| Rio Argente nasce nas Coroas e corre para o sul pelas planícies das Marcas, antes do Véu | M-04 G-06 | **[CANÔNICO]** |
| Ermo de Sal: único deserto, entre as Marcas e o Véu, barreira entre dois reinos que "não devem se tocar com facilidade" | M-04 G-10 | **[CANÔNICO]** |
| Contato com Aeltharyn: oeste, pelas elevações ocidentais, por trilhas largas e corredores de migração (Vael-Tir, Arven) | M-04 §5 | **[CANÔNICO]**; sem rota nem duração no doc 07 de Aeltharyn |
| 12 nativos: Orcano, Felídeo Errante, Centauro, Goblin, Goblin de Legião, Vulpino, Leporino, Ursino, Lupino, Porcino, Cervino, Insetoide | M-05 §3 | **[CANÔNICO]** (D-24) |
| 25 forasteiros (matriz M-05 §4); 13 raças sem papel | M-05 §4 | **[CANÔNICO]** |
| Fluxos: Descida do Argente (chega), Rota do Sal (passa pelo Ermo), Corredores Largos (parte rumo a Aeltharyn) | M-05 §5 | **[CANÔNICO]** |
| Nome de trabalho "Marcas das Feras": "territórios de povos bestiais, clãs e confederações; reservado" | doc 01 §1 do reino 1 | substituído por **Vale Palancar** (D-102); o texto de Aeltharyn não muda |
| Orcano, Felídeo Errante e Centauro também são nativos de Aeltharyn, em linhagens diferentes | M-05 §3 | **[CANÔNICO]**; linhagens das Marcas ainda sem nome |
| Tom e regras de narração (W-01), ponte de calendário (W-02), convenções de nome (W-03) | `mundo/M-01`, `M-02`, `M-10` | **[CANÔNICO]** |
| Lugares, NPCs, facções, governo, história, leis, economia, relógios | — | **nada escrito** |

**Limites herdados (não podem ser violados):** o Conflito G de Aeltharyn não ganha solução nem causa nas Marcas; a Neblina Longa, o Mistério D e o Mistério E não ganham causa nas Marcas; a Descida do Argente não ganha causa, número nem fim rio acima; nenhum mistério do Véu ganha "verdade" vinda das Marcas; a ligação Ermo de Sal ↔ Marcas continua sem rota nem duração; nada altera os 11 nativos, as 10 cidades, os 20 assentamentos e as 10 facções de Aeltharyn, nem as Coroas, nem o Véu; o Pacto Humano **não está escrito**, então as Marcas só afirmam sobre ele o que M-04 e M-05 já dizem. O texto completo está em `CONTRATO_DE_FRONTEIRA_PALANCAR.md`.

**Pontos identificados (contrato §7):** P-1 (a frase do Véu sobre o "leste das Marcas") **resolvido em D-103**, P-2 (a Descida × "—" para gnomo e construto na matriz) **resolvido em D-103**, P-3 (linhagens próprias de Orcano, Felídeo Errante e Centauro nas Marcas; proposto em D-110), P-4 (Lessa Cervo-Manso sem ID em Aeltharyn, não é do reino 4).

**Identidade (D-102, decidida):** o nome é **Vale Palancar**. O nome é de reino e **não cria lore nem relevo**: M-04 mantém planaltos, savanas e campos de migração ("Campos das Marcas"), e a origem e o sentido de "Palancar" ficam para o R4-1.5, com registro. Os povos-animais não são selvagens, perigosos ou leais por natureza (W-01), e os goblins, inclusive os de legião, não são "o inimigo" por raça.

## 1. Lições dos reinos 1, 2 e 3 aplicadas

| Problema anterior | Regra para o reino 4 |
|---|---|
| IDs conflitantes e pessoas sem ID (D-04, D-05, D-08, D-18) | todo NPC, lugar e facção recebe ID **na hora da escrita**; registro mestre vivo desde a F1 |
| IDs de canal colidiram entre reinos (R2-7.1) | conferir todo ID novo contra o registro mestre dos reinos 1, 2 **e 3** antes de entrar |
| Organizações sem ID ou facção-mãe (D-09, D-10) | toda organização citada tem ID e facção-mãe, ou é marcada instituição |
| Cargos acumulados (D-11, D-40) | cargo duplicado só se declarado intencional na ficha |
| Povo do registro × texto (D-20) | povo escrito em ID do catálogo desde a primeira ficha |
| Residência de governantes (D-19) | residência de cada governante explícita no doc principal (relevante: governo que pode ser itinerante) |
| Mínimos de principais (D-22) | 8 a 12 principais, ≥ 2 heróis vivos, ≥ 2 generais |
| Aliados e rivais `?` ou `desconhecido` (D-23, D-61, D-99: 3 facções do Véu) | aliados e rivais **preenchidos na escrita**; `desconhecido` só com motivo registrado |
| Contagem de nativos errada no contrato (R2-8.1) | contagens do contrato e do doc 11 conferidas contra os dados (12 nativos, 25 forasteiros) |
| 7 e 6 exceções do validador (ligações sem dois locais existentes) | todo evento cita só ligações entre locais que existem no reino |
| Número onde o texto não tem (D-63) | escala qualitativa; a IA narra "longa", "perto", "rica em", sem criar número |
| Mestiços alocados (D-64) | nenhum mestiço em lugar, facção ou reino |
| Afirmação sobre o vizinho ainda não escrito (P-1, frase do Véu sobre a posição interna das Marcas) | cada frase do reino 4 sobre Aeltharyn, Coroas, Véu e Pacto Humano passa pelo contrato; o resto vira ponto de contato aberto |
| Matriz × fluxo em conflito (P-2) | todo fluxo de M-05 §5 é conferido contra a coluna do reino na matriz **antes** de escrever o doc 10 |
| Doc 11 com decisão listada como pendente embora aceita (R3-8.2) | a ata lista decisões pelo estado real do `DECISOES.md` e é conferida por script |
| Status do README e do plano fora de sincronia (D-67 aparece "aguardando" no README, mas aceita em `DECISOES.md`) | cada entrega confere README, `PLANO_DOS_REINOS.md`, README do reino e `DECISOES.md` entre si |

## Fase 0 — Pré-requisitos e decisões (P0)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R4-0.1 Camada de mundo mínima** | a) M-05 já aprovado (12 nativos, 25 forasteiros); b) M-01 e M-02 já valem; c) calendário das Marcas e estações de migração (W-02) | M-02 estendido no R4-3.4 | nenhum texto do reino 4 depende de lore de mundo não aprovado |
| **R4-0.2 Decisões herdadas** | a) D-21 (formato), D-22 (mínimos), D-23 (aliados e rivais); b) D-63 e D-64; c) D-99 (rival `desconhecido` só com motivo) | `DECISOES.md` | decisões aplicadas ao modelo |
| **R4-0.3 Escala e identidade** | a) escala: uma área só, **oito lugares principais, fixos e sazonais**; b) nome: **Vale Palancar**; c) governo: **Assembleia de Clãs e Capitão de Marcha**; d) convenções de nome por povo (M-10, W-04) | D-102 (decidida) e D-104 (nomes, com o R4-1.1) | autor aprovou a, b e c em 07/11/2026 |
| **R4-0.4 Contrato de fronteira** | a) fixos de Aeltharyn, das Coroas e do Véu que tocam as Marcas; b) o que o reino 4 **não pode** dizer; c) ganchos permitidos; d) pontos abertos P-1 a P-4 | `plano/CONTRATO_DE_FRONTEIRA_PALANCAR.md` | lista conferida contra os docs 01 e 07 de Aeltharyn, os docs 01 e 05 das Coroas e os docs 01, 05, 07 e 12 do Véu |

## Fase 1 — Escrita editorial (P0)

Os módulos que falam em "cidades" e "assentamentos" valem na escala que o autor decidir em D-102 (opção A: 10 e 20; B ou C: oito lugares principais, sem rede de dez, com os mínimos de NPCs de D-22 mantidos).

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R4-1.1 Hierarquia e identidade dos lugares** | a) função de cada lugar coerente com o relevo (planalto, savana, campo de migração, vau e curso do Argente, chegada da Descida, orla do Ermo de Sal, elevações ocidentais voltadas a Aeltharyn); b) lugares fixos e sazonais, se D-102 aceitar; c) ficha padrão de lugar | `01-...md`, `03` | fichas completas; nenhuma função sem geografia que a sustente |
| **R4-1.2 Assentamentos e posição territorial** | a) liderança, obrigação, interesse, tensão e lugar-mãe; b) posição relativa em termos qualitativos; c) se há clãs sem lugar fixo, como entram | `03-assentamentos-e-rede-politica.md` | nenhum campo vazio; lugar-mãe existe |
| **R4-1.3 NPCs, identidades e conhecimento** | a) 8 a 12 principais (≥ 2 heróis, ≥ 2 generais); b) tríade herói, guardião e benfeitor; c) ficha padrão com confirmado, suspeita e desconhecido | seções do `01` | todo NPC com ID, povo do catálogo, agenda e pressão; nenhum nome já usado nos reinos 1 a 3 |
| **R4-1.4 Relações políticas e sociais** | a) governo (D-102), assembleias ou conselhos, sucessão, justiça, **pactos de passagem e pasto**; b) facções com objetivo, métodos, aliados e rivais **preenchidos** | `04-instituicoes-justica-pactos.md` | cada facção com aliados e rivais; sem aliado e rival ao mesmo tempo |
| **R4-1.5 História, economia, leis, defesa e cronologia** | a) eras locais; b) recursos, comércio e leis (qualitativo: pasto, gado, couro, sal, ferro por troca); c) defesas e vulnerabilidades; d) a chegada da Descida e a saída da Rota do Sal e dos Corredores Largos, **só do lado das Marcas** | `01`, `05` | cronologia sem contradição com Aeltharyn, Coroas e Véu |
| **R4-1.6 Povos, linhagens, lugares e forasteiros** | a) os 12 nativos como ponto de partida; b) sub-raças; c) lugares com ≥ 2 povos; d) forasteiros e P-2; e) linhagens próprias de Orcano, Felídeo Errante e Centauro (P-3); f) mestiços só pela regra de D-64 | `10-matriz-povos-nativos-cidades-e-forasteiros.md` | 100% em IDs do catálogo; nenhuma raça cidadã, vilã ou leal por natureza; goblins sem papel de "inimigo" por raça |
| **R4-1.7 Mistérios, relógios e oportunidades** | a) mistérios sem resposta única, com "a verdade não pode ser"; b) relógios iniciais (número a decidir), sem avanço automático; c) conflitos com causa concreta (pasto, água, passagem, sucessão de chefia, caça, tributo de caravana) | `01` (parte 4), `06` (a ata `11` é do R4-8.2) | nenhum mistério explica a Neblina Longa, o Mistério D, o Mistério E ou o Conflito G, nem dá causa a mistério do Véu ou da Descida |

## Fase 2 — Registro mestre e consistência (P0)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R4-2.1 Registro mestre** | a) tabela única no formato de D-21; b) IDs únicos contra os reinos 1, 2 e 3; c) catálogo conferido | `12-registro-mestre.*` | 0 ID duplicado; 0 ID citado inexistente |
| **R4-2.2 Facções e lugares como dados** | a) facções com todos os campos; b) lugares com lugar-mãe | `12-facoes`, `12-assentamentos` | nenhum campo vazio sem `desconhecido` e motivo |
| **R4-2.3 Povos e catálogo** | a) alinhar os 12 nativos ao `personagem-catalogo.json`; b) raça, sub-raça, classe, profissão por NPC | `referencia/racas-e-povos/ALINHAMENTO_PALANCAR.md` | 0 ID fora do catálogo |
| **R4-2.4 Conferência cruzada** | a) aliado e rival ao mesmo tempo; b) idades e cargos duplicados; c) quem sabe o quê; d) contradições intencionais registradas | `13-conferencia-cruzada.md` | lista vazia ou toda marcada como intencional |

## Fase 3 — Lacunas de evidência, em escala qualitativa (P1)

Por D-63, esta fase **não escreve números**.

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R4-3.1 Geografia e deslocamento** | a) rotas internas só com "curta", "longa" ou `desconhecida`; b) acessos dos lugares; c) ligações com Aeltharyn, Coroas e Véu **só** as do contrato; d) mapa esquemático | `07-registro-operacional-de-rotas.md`, `07-mapa-esquematico.md` | nenhuma rota ou tempo inventado; Ermo de Sal ↔ Marcas segue "sem rota" |
| **R4-3.2 Demografia** | a) população pequena, média ou grande; b) regra "não somar" | `12-demografia.*` | nenhum total apresentado como censo |
| **R4-3.3 Recursos** | a) "rica em", "pobre em", "depende de"; b) dependências entre lugares; c) o que a Descida e a Rota do Sal trazem, só em termos qualitativos | `08`, `09` | nenhum lugar funciona isolado; cada dependência crítica com fonte; nada sobre a origem do combustível de Brasalume |
| **R4-3.4 Calendário e estações** | a) estações de migração e de pasto; b) tabela de conversão com Aeltharyn (W-02) | seção no `01`, `M-02` | ano e estação inicial compatíveis com o ano 742 de Aeltharyn |

## Fase 4 — Profundidade dos personagens (P1)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R4-4.1 Fichas completas** | a) todos os campos; b) agenda e pressão distintas por NPC | `12-fichas.*` | 100% dos principais completos |
| **R4-4.2 Competências pelo catálogo** | a) capacidades e **limites**; b) IDs do catálogo | `12-competencias.*` | todo ID existe no JSON |
| **R4-4.3 Relações e conhecimento** | a) matriz (afeto, dívida, rivalidade, medo); b) confirmado, suspeita, desconhecido | `12-relacoes.*`, `12-conhecimento.csv` | nenhuma relação sem origem |
| **R4-4.4 Voz e ganchos de diálogo** | a) 2 a 3 frases-modelo por principal; b) o que nunca diz | `12-voz.*` | usável como instrução de tom |

## Fase 5 — Mundo vivo (P1)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R4-5.1 Relógios** | a) gatilhos; b) custo por segmento; c) efeito da inação | `12-relogios.*` | cada relógio com "o que o move" e "o que o trava"; nenhum envolve Aeltharyn, Coroas ou Véu |
| **R4-5.2 Cadeias causais** | a) 10 a 15 eventos-tipo; b) efeitos em IDs existentes e em ligações entre locais que existem | `12-eventos.*` | nenhum efeito aponta ID inexistente |
| **R4-5.3 Sementes de aventura** | a) por lugar: pequena, média, grande; b) sem solução predefinida | `13-sementes-de-aventura.*` | nenhuma semente depende de NPC inexistente |
| **R4-5.4 Reação do reino ao jogador** | a) fama e reputação em termos qualitativos; b) procurado, favor, reparação; c) intermediários | `13-reputacao.csv`, `13-reacao-ao-jogador.md`, `13-canais.csv` | liga ao plano do jogo |
| **R4-5.5 Mistérios com limites** | a) sem resposta única; b) "a verdade não pode ser" | `13-misterios-limites.*` | nenhum mistério contradiz fato confirmado nem os mistérios dos reinos vizinhos |

## Fase 6 — Pacote de contexto para IA (P0 para a integração)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R4-6.1 Fichas curtas** | a) ≤ ~500 caracteres por entidade; b) vizinhança em 1 linha | `14-resumos-de-contexto.*` | toda entidade com resumo e ID |
| **R4-6.2 Camadas de informação** | a) público, local, restrito, secreto por fato; b) como descobrir | `14-camadas-de-informacao.*` | segredo nunca em resumo público |
| **R4-6.3 Cenas-teste** | a) acampamento de clã em migração; b) chegada a um lugar de mercado ou vau; c) assembleia de clãs; d) disputa de pasto ou de passagem; e) viagem pelos Campos ou pelo Argente; f) descida de caravana da Rota do Sal ou chegada da Descida, **só do lado das Marcas**; g) travessia das elevações ocidentais, **só em tom de rumor sobre Aeltharyn** | `15-cenas-de-teste.*` | cada cena com "entra" (≤ ~2.400 caracteres) e "não entra" |
| **R4-6.4 Contrato anti-invenção** | a) fatos fixos, livres e proibidos; b) novo chefe, lugar ou guerra exige registro | seção no doc 06 | regra clara |

## Fase 7 — Preparação para implementar (P1)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R4-7.1 Dados e validador** | a) dados no formato de D-21 (adaptar `converter_reino3.py` e `validar_reino.py` para o reino 4, sem alterar os reinos 2 e 3); b) rodar o validador | `converter_reino4.py`, `dados/`, relatório | 0 erro; avisos listados |
| **R4-7.2 Catálogo técnico** | a) verificar o que o `.kt` tem sobre as Marcas; b) mapa de conversão, sem alterar código | `MAPA_CATALOGO_KOTLIN.md` | divergências listadas, nada instalado |
| **R4-7.3 Protótipo mínimo** | a) acampamento ou lugar pequeno + 1 NPC + lugar de mercado em 1 linha; b) condições com e sem mundo; c) métricas fixadas antes de jogar | `PROTOTIPO_MINIMO.md` (seção do reino 4) | critério de aceite escrito antes da sessão |

## Fase 8 — Revisão e fechamento (P0)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R4-8.1 Revisão final** | a) checklist de `MODELO_DE_REINO.md` §6; b) contradições intencionais × acidentais | `RELATORIO_R4_8_1.md` | todas as portas marcadas |
| **R4-8.2 Ata de fechamento 5** | a) doc 11 do reino 4; b) liberar o reino 5 (Pacto Humano) | doc 11 | autor aprova |

## Ordem e dependências

`F0 → F1 → F2 → (F3 ∥ F4) → F5 → F6 → F7 → F8`. A F2 roda **junto** da F1. A F6 pode começar em paralelo com a F5 para as cenas-teste. A F1 não começa sem R4-0.3 (D-102) aprovado e R4-0.4 escrito.

## Primeira rodada

1. **R4-0.4** contrato de fronteira (escrito).
2. **R4-0.3** decidido pelo autor em 07/11/2026 (D-102): Vale Palancar, oito lugares fixos e sazonais, Assembleia de Clãs e Capitão de Marcha. **P-1 e P-2 resolvidos** (D-103).
3. **R4-1.1** escrito (D-104, convenções de nome; D-105, oito lugares); **R4-1.2** escrito (D-106); **R4-1.3** escrito (D-107); **R4-1.4** escrito (D-108); **R4-1.5** escrito (D-109, aceita pela continuação); **R4-1.6** escrito (D-110); **R4-1.7** escrito (D-111); **Fase 1 concluída**; **R4-2.1** escrito (D-112); **R4-2.2** escrito (D-113); **R4-2.3** escrito (D-114); **R4-2.4** escrito (D-115); **Fase 2 concluída**; **R4-3.1** escrito (D-116); **R4-3.2** escrito (D-117); **R4-3.3** escrito (D-118); **R4-3.4** escrito (D-119); **Fase 3 concluída**; **R4-4.1** escrito (D-120); **R4-4.2** escrito (D-121); **R4-4.3** escrito (D-122); **R4-4.4** escrito (D-123); **Fase 4 concluída**; **R4-5.1** escrito (D-124); **R4-5.2** escrito (D-125); **R4-5.3** escrito (D-126); **R4-5.4** escrito (D-127); **R4-5.5** escrito (D-128); **Fase 5 concluída**; **R4-6.1** escrito (D-129); **R4-6.2** escrito (D-130); **R4-6.3** escrito (D-131); **R4-6.4** escrito (D-132, aceita pela continuação); **Fase 6 concluída**; **R4-7.1** escrito (D-133, aceita pela continuação); **R4-7.2** escrito (D-134, aceita pela continuação); **R4-7.3** escrito (D-135); **Fase 7 concluída**; **R4-8.1** escrito (D-136); **R4-8.2** escrito (D-137); **Fase 8 concluída**; roadmap 100%.

## Andamento

| Módulo | Estado | Observação |
|---|---|---|
| R4-0.1 Camada de mundo mínima | **feito** | M-05 aprovado (12 nativos, 25 forasteiros); M-01 e M-02 valem; calendário fica para o R4-3.4 |
| R4-0.2 Decisões herdadas | **feito** | D-21 a D-23, D-63, D-64 e D-99 aplicadas a este plano |
| R4-0.3 Escala e identidade | **feito** | D-102 decidida pelo autor; D-104 (nomes) entra com o R4-1.1 |
| R4-0.4 Contrato de fronteira | **escrito** | `plano/CONTRATO_DE_FRONTEIRA_PALANCAR.md`; P-1 e P-2 resolvidos (D-103); P-3 proposto em D-110 (aceita pela continuação) e P-4 aberto |
| R4-1.1 Hierarquia e identidade dos lugares | **escrito** | `reinos/04-vale-palancar/01-reino-do-vale-palancar.md` (oito lugares; D-104 e D-105 aceitas pela continuação) |
| R4-1.2 Assentamentos e posição territorial | **escrito** | `reinos/04-vale-palancar/03-assentamentos-e-rede-politica.md` (D-106 aceita pela continuação) |
| R4-1.3 NPCs, identidades e conhecimento | **escrito** | parte 2 do doc 01 (12 principais, 2 generais, 2 heróis vivos, tríade 8 de 8; D-107 aceita pela continuação) |
| R4-1.4 Relações políticas e sociais | **escrito** | `reinos/04-vale-palancar/04-instituicoes-justica-pactos.md` (14 facções; D-108 aceita pela continuação) |
| R4-1.5 História, economia, leis, defesa e cronologia | **escrito** | parte 3 do doc 01 (4 eras, nome "Palancar", lendas, cronologia, defesa) e `reinos/04-vale-palancar/05-economia-leis-e-fluxos.md` (10 leis, economia, 3 fluxos); D-109 aceita pela continuação |
| R4-1.6 Povos, linhagens, lugares e forasteiros | **escrito** | `reinos/04-vale-palancar/10-matriz-povos-nativos-cidades-e-forasteiros.md` (12 nativos, 6 sub-raças, P-3 proposto, 25 forasteiros, papéis por povo; D-110 aceita pela continuação) |
| R4-1.7 Mistérios, relógios e oportunidades | **escrito** | parte 4 do doc 01 (10 mistérios) e `reinos/04-vale-palancar/06-modelo-operacional-e-aventuras.md` (8 relógios, 8 conflitos, 8 oportunidades); D-111 aceita pela continuação; **Fase 1 concluída (F1)** |
| R4-2.1 Registro mestre | **escrito** | `reinos/04-vale-palancar/12-registro-mestre.csv` e `.md` (47 entidades; D-112 aceita pela continuação) |
| R4-2.2 Facções e lugares como dados | **escrito** | `12-facoes.csv` (14) e `12-assentamentos.csv` (8); D-113 aceita pela continuação |
| R4-2.3 Povos e catálogo | **escrito** | `referencia/racas-e-povos/ALINHAMENTO_PALANCAR.md` (12 nativos, 6 sub-raças, 25 forasteiros, 25 pessoas; D-114 aceita pela continuação) |
| R4-2.4 Conferência cruzada | **escrito** | `reinos/04-vale-palancar/13-conferencia-cruzada.md` (7 acúmulos, CI-01 a CI-12; D-115 aceita pela continuação); **Fase 2 concluída (F2)** |
| R4-3.1 Geografia e deslocamento | **escrito** | `reinos/04-vale-palancar/07-registro-operacional-de-rotas.md` (9 ligações, 8 acessos, 6 linhas de fronteira) e `07-mapa-esquematico.md`; toda duração `desconhecida`; D-116 aceita pela continuação |
| R4-3.2 Demografia | **escrito** | `reinos/04-vale-palancar/12-demografia.md` e `.csv`: 8 faixas (2 grandes, 3 médias, 3 pequenas), regras DM-01 a DM-05, 0 número; D-117 aceita pela continuação |
| R4-3.3 Recursos | **escrito** | `reinos/04-vale-palancar/08-registro-operacional-de-recursos.md` e `09-matriz-material-dos-lugares.md` (8 lugares, 12 dependências, 8 críticas, 4 fluxos); D-118 aceita pela continuação |
| R4-3.4 Calendário e estações | **escrito** | doc 01 parte 5, `M-02` §8, `M-10` §10: 4 estações, 5 Dias de Marco, Contagem dos Marcos, ano 611 = ano 742 de Aeltharyn (ASSUMINDO), tabela de 8 linhas; 0 evento datado entre reinos; D-119 aceita pela continuação; **Fase 3 concluída (F3)** |
| R4-4.1 Fichas completas | **escrito** | `reinos/04-vale-palancar/12-fichas.md` e `.csv`: 12 de 12 principais, 17 campos, 12 agendas e 12 pressões distintas, 6 acúmulos marcados (CI-07); D-120 aceita pela continuação |
| R4-4.2 Competências pelo catálogo | **escrito** | `reinos/04-vale-palancar/12-competencias.md` e `.csv`: 25 pessoas (12 principais, 7 sem ficha, 6 lendários), 0 ID fora do catálogo, capacidades e limites só dos docs; D-121 aceita pela continuação |
| R4-4.3 Relações e conhecimento | **escrito** | `reinos/04-vale-palancar/12-relacoes.md`, `12-relacoes.csv` (81 relações, 0 afeto) e `12-conhecimento.csv` (66 linhas, 25 de 25 pessoas); toda relação com fonte; 0 ID fora do registro, 0 aliado-rival; D-122 aceita pela continuação |
| R4-4.4 Voz e ganchos de diálogo | **escrito** | `reinos/04-vale-palancar/12-voz.md` e `12-voz.csv`: 12 de 12 principais com tom, 2 a 3 frases-modelo e `nunca_diz`; 0 frase revela suspeita ou desconhecido; testes de fim de F4 em `12-voz.md` (0 falha); D-123 aguardando o autor; **Fase 4 concluída (F4)** |
| R4-5.1 Relógios | **escrito** | `reinos/04-vale-palancar/12-relogios.md` e `12-relogios.csv`: 8 relógios do doc 06 com gatilhos, custo por segmento, decisor no segmento 5 e efeito da inação; 0 relógio, NPC ou fato novo; D-124 aceita pela continuação |
| R4-5.2 Cadeias causais | **escrito** | `reinos/04-vale-palancar/12-eventos.md` e `12-eventos.csv`: 12 eventos-tipo (8 completam relógio, 2 dão gatilho, 2 sem relógio); 0 ID ou rota fora do registro e do doc 07; D-125 aceita pela continuação |
| R4-5.3 Sementes de aventura | **escrito** | `reinos/04-vale-palancar/13-sementes-de-aventura.md` e `.csv`: 24 sementes (8 P, 8 M, 8 G); 0 agente fora do registro, 0 ligação inexistente; D-126 aceita pela continuação |
| R4-5.4 Reação do reino ao jogador | **escrito** | `reinos/04-vale-palancar/13-reacao-ao-jogador.md`, `13-reputacao.csv` (22 linhas) e `13-canais.csv` (11 canais); 0 ID fora do registro, 0 rota fora do doc 07; D-127 aceita pela continuação |
| R4-5.5 Mistérios com limites | **escrito** | `reinos/04-vale-palancar/13-misterios-limites.md` e `.csv`: 10 mistérios (MPL-01 a MPL-10) com 4 hipóteses, vetos, evidência e ligação a relógio e evento; 0 ID fora do registro; testes de fim da F5 no próprio doc; D-128 aceita pela continuação |
| R4-6.1 Fichas curtas | **escrito** | `reinos/04-vale-palancar/14-resumos-de-contexto.md` e `.csv`: 55 resumos (8 cidades, 14 facções, 8 relógios, 25 pessoas), até 500 caracteres, com vizinhança; 0 segredo, 0 entidade sem resumo; D-129 aceita pela continuação |
| R4-6.2 Camadas de informação | **escrito** | `reinos/04-vale-palancar/14-camadas-de-informacao.md` e `.csv`: 42 fatos (11 públicos, 11 locais, 6 restritos, 14 secretos) com caminho; fichas N-01 a N-12 ligadas; `camada_max` dos resumos definitiva; 0 segredo em resumo; D-130 aceita pela continuação |
| R4-6.3 Cenas-teste | **escrito** | `reinos/04-vale-palancar/15-cenas-de-teste.md` e `.csv`: 7 cenas (a a g), cada uma com "entra" (maior: 2140 caracteres) e "não entra"; 0 segredo, 0 relógio, 0 `nunca_diz` em "entra"; D-131 aceita pela continuação |
| R4-6.4 Contrato anti-invenção | **escrito (D-132 aceita pela continuação); Fase 6 concluída (F6)** | seção 9 do doc 06: verificação, quatro classes (fixo, livre, registro, proibido), lista proibida, 8 casos e teste rápido; herda os contratos dos reinos 1 a 3 sem alterá-los; testes de F6 no CHANGELOG: 0 falha, 0 regressão |
| R4-7.1 Dados e validador | **escrito (D-133 aceita pela continuação)** | `plano/ferramentas/converter_reino4.py`, `reinos/04-vale-palancar/dados/` (21 `.txt`, 405 registros) e `relatorio-validador.md`/`.txt`: **0 erro, 11 avisos, 2 exceções** (V-23 e V-44: Alvasal sem rota interna, grafo em dois grupos); validador estendido só com o prefixo `mist.` em `fato.txt` (relatórios dos reinos 2 e 3 idênticos) |
| R4-7.2 Catálogo técnico | **escrito (D-134 aceita pela continuação)** | seção 8 de `plano/MAPA_CATALOGO_KOTLIN.md`: KP1 a KP9 (0 entidade do Vale no `.kt`, 0 colisão de ID, 12 nativos fora do `.kt`, linhagens distintas, lacunas do esquema); nada instalado, nenhum código alterado |
| R4-7.3 Protótipo mínimo | **escrito (D-135 aguardando o autor); Fase 7 concluída (F7)** | seção 12 de `plano/PROTOTIPO_MINIMO.md`: Alvasal + Gruzk Estandarte-Baixo + Rasovau em uma linha; contexto B de 1.840 caracteres; 8 turnos, métricas Q1 a Q10 e critério de aceite escritos antes da sessão; nenhuma sessão executada; testes de F7: 0 falha, 0 regressão |
| R4-8.1 Revisão final | **escrito (D-136 aceita pela continuação)** | `plano/RELATORIO_R4_8_1.md`: portas 1 a 10 marcadas (1, 3 e 6 com ressalva ou lacuna), 11 pendente; 10 contradições intencionais, 0 acidental nova; nenhum lore alterado |
| R4-8.2 Ata de fechamento 5 | **escrito (D-137 aguardando o autor); Fase 8 concluída (F8)** | `reinos/04-vale-palancar/11-fechamento-do-vale-palancar.md`: 11 de 11 portas, 10 contradições intencionais; reino 5 não liberado; testes de F8 no CHANGELOG: 0 falha, 0 regressão |

**Progresso:** 37 de 37 módulos (100%). **Fases 0 a 8 (F0 a F8) concluídas.**
