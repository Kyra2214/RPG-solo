# Plano do Reino 3 — Véu dos Malditos

**Objetivo:** escrever e fechar o reino 3 nos mesmos moldes dos reinos 1 e 2, com as lições dos dois já aplicadas desde o início. Só depois do fechamento 4 abre-se o reino 4 (Marcas das Feras).
**Regra:** nada aqui inventa nome, segredo ou fato sem fonte. O que não tem fonte fica `desconhecido` ou vira decisão do autor. Um reino por vez (`PLANO_DOS_REINOS.md` §1).
**Escala (D-63):** distância, tamanho e produção são **qualitativos** ("longa", "perto", "rica em", população pequena, média ou grande). **A camada de números não será escrita** neste reino: sem duração de rota, sem faixa numérica de população, sem nível fino de recurso. O validador vai avisar sobre esses campos (V-27 e afins) e o aviso é esperado.
**Mestiços (D-64):** não são alocados a cidade, facção nem reino; o doc 10 só repete a regra.
**Legenda:** prioridade P0 (bloqueia o resto), P1 (importante), P2 (desejável). Etiquetas **[CANÔNICO]** (já aprovado em `mundo/` ou nos reinos 1 e 2) e **[PROPOSTO]** (precisa de aprovação). Módulos usam o prefixo `R3-`.
**Pasta (proposta, D-66):** `reinos/03-veu-dos-malditos/`, com as peças de `MODELO_DE_REINO.md` §1 (o doc 02 de arquitetura vale para todos e não se repete).
**Liberação:** o autor aprovou o fechamento 3 e liberou o reino 3 em 05/11/2026 (D-65).

## 0. Ponto de partida (o que já existe sobre o reino 3)

| Item | Fonte | Estado |
|---|---|---|
| Posição: sudoeste e sul; vizinhos plenos Marcas das Feras e Pacto Humano; contato parcial com Aeltharyn | M-04 §3 (G-02) | **[CANÔNICO]** |
| Relevo: terras de neblina, charcos e vales marcados por pactos | M-04 §3 | **[CANÔNICO]** |
| Rio Argente atravessa o Véu até o Mar Austral (foz no sul) | M-04 G-06 | **[CANÔNICO]** |
| O Véu: faixa de neblina e charcos entre o pântano de Orvass e as terras dos Malditos; Brumafunda e o Bosque dos Sussurros ficam na orla norte; **não** explica a Neblina Longa nem o Mistério E | M-04 G-08 | **[CANÔNICO]** |
| Ermo de Sal: deserto entre as Marcas e o Véu, barreira entre dois reinos que não se tocam com facilidade | M-04 G-10 | **[CANÔNICO]** |
| Mar Austral banha o sul; foz do Argente | M-04 G-09 | **[CANÔNICO]** |
| Contato com Aeltharyn: sudoeste, pelo Bosque dos Sussurros, "só guias" (Brumafunda); sul, pelo pântano (Orvass) | M-04 §5 | **[CANÔNICO]**; rotas e durações só as do doc 07 de Aeltharyn |
| 11 nativos: Nascido do Crepúsculo, Corvídeo, Ursino Sombrio, Escamado, Ratoide, Umbral, Onírico, Noctívago, Metamorfo Lupino, Moldável, Aracnídeo | M-05 §3 | **[CANÔNICO]** (M-05 aprovado, D-24) |
| 17 forasteiros (matriz M-05 §4) | M-05 §4 | **[CANÔNICO]** |
| Fluxos: Descida do Argente (chega ao Véu), Rota do Sal (Ermo de Sal), Fugas do Véu (parte do Véu) | M-05 §5 | **[CANÔNICO]** |
| Descida do Argente descrita só do lado das Coroas; o destino no Véu não é descrito | doc 05 do reino 2 | **[CANÔNICO]** |
| Tom e regras de narração (W-01), ponte de calendário (W-02), convenções de nome (W-03) | `mundo/M-01`, `M-02`, `M-10` | **[CANÔNICO]** (aceitas pela continuação) |
| Cidades, assentamentos, NPCs, facções, história, leis, economia, relógios | — | **nada escrito** |

**Limites herdados (não podem ser violados):** a Neblina Longa e o Mistério E de Aeltharyn não ganham causa no Véu; nenhum evento de Aeltharyn depende do Véu; nenhuma rota ou duração nova para Aeltharyn; nada altera os 11 nativos, as 10 cidades, os 20 assentamentos ou as 10 facções de Aeltharyn; nada altera as Coroas de Pedra (reino 2 fechado); Marcas das Feras e Pacto Humano **não estão escritos**, então o Véu só afirma sobre eles o que M-04 e M-05 já dizem (M-01 §4: não se descrevem outros reinos).

**Ambiguidade a resolver (R3-0.3, D-66):** "O Véu" é uma **faixa geográfica** (M-04 G-08, com Brumafunda e o Bosque dos Sussurros na orla norte, em Aeltharyn) e "Véu dos Malditos" é o **reino** provisório. Brumafunda **pertence a Aeltharyn**. O reino 3 não pode anexar a faixa inteira nem tratar Brumafunda como sua; o contrato de fronteira (`CONTRATO_DE_FRONTEIRA_AELTHARYN_VEU.md`) fixa isso.

## 1. Lições dos reinos 1 e 2 aplicadas

| Problema anterior | Regra para o reino 3 |
|---|---|
| IDs conflitantes e pessoas sem ID (D-04, D-05, D-08, D-18) | todo NPC, lugar e facção recebe ID **na hora da escrita**; registro mestre vivo desde a F1 |
| 5 IDs de canal colidiram com Aeltharyn (R2-7.1) | conferir todo ID novo contra o registro mestre do reino 1 **e** do reino 2 antes de entrar |
| Organizações sem ID ou facção-mãe (D-09, D-10) | toda organização citada tem ID e facção-mãe, ou é marcada instituição |
| Cargos acumulados (D-11, D-40) | cargo duplicado só se declarado intencional na ficha |
| Povo do registro × texto (D-20) | povo escrito em ID do catálogo desde a primeira ficha |
| Residência de governantes (D-19) | residência de cada governante explícita no doc principal |
| Mínimos de principais (D-22) | 8 a 12 principais, ≥ 2 heróis vivos, ≥ 2 generais |
| Aliados e rivais `?` em 5 facções (D-23, D-61) | aliados e rivais preenchidos na escrita; `desconhecido` só com motivo registrado |
| Contagem de nativos errada no contrato (R2-8.1) | contagens do contrato e do doc 11 conferidas contra os dados |
| 7 exceções do validador (ligações sem dois locais existentes) | todo evento cita só ligações entre locais que existem no reino |
| Número onde o texto não tem (D-63) | escala qualitativa; a IA narra "longa", "perto", "rica em", sem criar número |
| Mestiços alocados (D-64) | nenhum mestiço em cidade, facção ou reino |

## Fase 0 — Pré-requisitos e decisões (P0)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R3-0.1 Camada de mundo mínima** | a) M-05 já aprovado (11 nativos, 17 forasteiros); b) M-01 e M-02 já valem; c) calendário do Véu e deslocamento do número do ano (W-02) | M-02 estendido no R3-3.4 | nenhum texto do reino 3 depende de lore de mundo não aprovado |
| **R3-0.2 Decisões herdadas** | a) D-21 (formato), D-22 (mínimos), D-23 (aliados e rivais); b) D-63 e D-64 | `DECISOES.md` | decisões aplicadas ao modelo |
| **R3-0.3 Escala e identidade** | a) nº de cidades e assentamentos; b) nome oficial; c) tipo de governo (decisão do autor); d) convenções de nome por povo (M-10, W-04); e) como tratar o nome "Véu" (faixa × reino) | D-66 e D-67 | autor aprova |
| **R3-0.4 Contrato de fronteira com Aeltharyn** | a) fixos de Aeltharyn que tocam o Véu; b) o que o reino 3 **não pode** dizer; c) ganchos permitidos; d) pontos de contato com as Coroas (Descida do Argente) | `plano/CONTRATO_DE_FRONTEIRA_AELTHARYN_VEU.md` | lista conferida contra os docs 01, 06, 07 de Aeltharyn e o doc 05 das Coroas |

## Fase 1 — Escrita editorial (P0)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R3-1.1 Hierarquia e identidade das cidades** | a) função de cada cidade coerente com o relevo (neblina, charco, vale de pacto, foz do Argente no Mar Austral, orla do Ermo de Sal, orla norte voltada a Aeltharyn); b) ficha padrão de cidade | `01-...md`, `03` | fichas completas; nenhuma função sem geografia que a sustente |
| **R3-1.2 Assentamentos e posição territorial** | a) liderança, obrigação, interesse, tensão e cidade-mãe; b) posição relativa em termos qualitativos | `03-assentamentos-e-rede-politica.md` | nenhum campo vazio; cidade-mãe existe |
| **R3-1.3 NPCs, identidades e conhecimento** | a) 8 a 12 principais (≥ 2 heróis, ≥ 2 generais); b) tríade herói, guardião e benfeitor; c) ficha padrão com confirmado, suspeita e desconhecido | seções do `01` | todo NPC com ID, povo do catálogo, agenda e pressão |
| **R3-1.4 Relações políticas e sociais** | a) governo, conselhos, sucessão, justiça, **pactos** (o tema do reino); b) facções com objetivo, métodos, aliados e rivais | `04-instituicoes-justica-pactos.md` | cada facção com aliados e rivais; sem aliado e rival ao mesmo tempo |
| **R3-1.5 História, economia, leis, defesa e cronologia** | a) eras locais; b) recursos, comércio e leis (qualitativo); c) defesas e vulnerabilidades; d) a chegada da Descida do Argente e a saída das Fugas do Véu, **só do lado do Véu** | `01`, `05` | cronologia sem contradição com Aeltharyn e Coroas |
| **R3-1.6 Povos, linhagens, cidades e forasteiros** | a) os 11 nativos como ponto de partida; b) sub-raças; c) cidades com ≥ 2 povos; d) forasteiros; e) mestiços só pela regra de D-64 | `10-matriz-povos-nativos-cidades-e-forasteiros.md` | 100% em IDs do catálogo; nenhuma raça cidadã, vilã ou leal por natureza (os Malditos **não são maus por natureza**) |
| **R3-1.7 Mistérios, relógios e oportunidades** | a) mistérios sem resposta única, com "a verdade não pode ser"; b) relógios iniciais (número a decidir), sem avanço automático; c) conflitos com causa concreta | `01`, `06`, `11` | nenhum mistério explica a Neblina Longa, o Mistério E ou o Mistério D |

## Fase 2 — Registro mestre e consistência (P0)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R3-2.1 Registro mestre** | a) tabela única no formato de D-21; b) IDs únicos contra os reinos 1 e 2; c) catálogo conferido | `12-registro-mestre.*` | 0 ID duplicado; 0 ID citado inexistente |
| **R3-2.2 Facções e assentamentos como dados** | a) facções com todos os campos; b) assentamentos com cidade-mãe | `12-facoes`, `12-assentamentos` | nenhum campo vazio sem `desconhecido` |
| **R3-2.3 Povos e catálogo** | a) alinhar os 11 nativos ao `personagem-catalogo.json`; b) raça, sub-raça, classe, profissão por NPC | `ALINHAMENTO_VEU.md` | 0 ID fora do catálogo |
| **R3-2.4 Conferência cruzada** | a) aliado e rival ao mesmo tempo; b) idades e cargos duplicados; c) quem sabe o quê; d) contradições intencionais registradas | relatório | lista vazia ou toda marcada como intencional |

## Fase 3 — Lacunas de evidência, em escala qualitativa (P1)

Por D-63, esta fase **não escreve números**.

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R3-3.1 Geografia e deslocamento** | a) rotas internas só com "curta", "longa" ou `desconhecida`; b) acessos dos assentamentos; c) ligações com Aeltharyn **só** as do doc 07; d) mapa esquemático | `07-registro-operacional-de-rotas.md` | nenhuma rota ou tempo inventado |
| **R3-3.2 Demografia** | a) população pequena, média ou grande; b) regra "não somar" | `05` | nenhum total apresentado como censo |
| **R3-3.3 Recursos** | a) "rica em", "pobre em", "depende de"; b) dependências entre cidades; c) o que a Descida e a Rota do Sal trazem, só em termos qualitativos | `08`, `09` | nenhuma cidade funciona isolada; cada dependência crítica com fonte |
| **R3-3.4 Calendário e estações** | a) estações de neblina e charco; b) tabela de conversão com Aeltharyn (W-02) | seção no `01`, `M-02` | ano e estação inicial compatíveis com o ano 742 de Aeltharyn |

## Fase 4 — Profundidade dos personagens (P1)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R3-4.1 Fichas completas** | a) todos os campos; b) agenda e pressão distintas por NPC | `12-fichas.*` | 100% dos principais completos |
| **R3-4.2 Competências pelo catálogo** | a) capacidades e **limites**; b) IDs do catálogo | `12-competencias` | todo ID existe no JSON |
| **R3-4.3 Relações e conhecimento** | a) matriz (afeto, dívida, rivalidade, medo); b) confirmado, suspeita, desconhecido | `12-relacoes`, `12-conhecimento` | nenhuma relação sem origem |
| **R3-4.4 Voz e ganchos de diálogo** | a) 2 a 3 frases-modelo por principal; b) o que nunca diz | `12-voz` | usável como instrução de tom |

## Fase 5 — Mundo vivo (P1)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R3-5.1 Relógios** | a) gatilhos; b) custo por segmento; c) efeito da inação | `12-relogios` | cada relógio com "o que o move" e "o que o trava" |
| **R3-5.2 Cadeias causais** | a) 10 a 15 eventos-tipo; b) efeitos em IDs existentes e em ligações entre locais que existem | `12-eventos` | nenhum efeito aponta ID inexistente |
| **R3-5.3 Sementes de aventura** | a) por cidade e assentamento: pequena, média, grande; b) sem solução predefinida | `13-sementes-de-aventura` | nenhuma semente depende de NPC inexistente |
| **R3-5.4 Reação do reino ao jogador** | a) fama e reputação em termos qualitativos; b) procurado, favor, reparação; c) intermediários | `13-reputacao`, `13-reacao-ao-jogador` | liga ao plano do jogo |
| **R3-5.5 Mistérios com limites** | a) sem resposta única; b) "a verdade não pode ser" | `13-misterios-limites` | nenhum mistério contradiz fato confirmado nem os mistérios de Aeltharyn |

## Fase 6 — Pacote de contexto para IA (P0 para a integração)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R3-6.1 Fichas curtas** | a) ≤ ~500 caracteres por entidade; b) vizinhança em 1 linha | `14-resumos-de-contexto` | toda entidade com resumo e ID |
| **R3-6.2 Camadas de informação** | a) público, local, restrito, secreto por fato; b) como descobrir | `14-camadas-de-informacao` | segredo nunca em resumo público |
| **R3-6.3 Cenas-teste** | a) vilarejo de charco; b) chegada a uma cidade de neblina; c) audiência de pacto; d) investigação em arquivo; e) viagem por rota (Argente ou Ermo de Sal); f) travessia da orla norte, **só em tom de rumor sobre Aeltharyn** | `15-cenas-de-teste` | cada cena com "entra" (≤ ~2.400 caracteres) e "não entra" |
| **R3-6.4 Contrato anti-invenção** | a) fatos fixos, livres e proibidos; b) novo rei, cidade ou guerra exige registro | seção no doc 06 | regra clara |

## Fase 7 — Preparação para implementar (P1)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R3-7.1 Dados e validador** | a) dados no formato de D-21 (adaptar `converter_reino2.py` e `validar_reino.py` para o reino 3, sem alterar o reino 2); b) rodar o validador | relatório | 0 erro; avisos listados |
| **R3-7.2 Catálogo técnico** | a) verificar o que o `.kt` tem sobre o Véu; b) mapa de conversão, sem alterar código | `MAPA_CATALOGO_KOTLIN.md` | divergências listadas, nada instalado |
| **R3-7.3 Protótipo mínimo** | a) vilarejo + 1 NPC + cidade-mãe em 1 linha; b) condições com e sem mundo; c) métricas fixadas antes de jogar | `PROTOTIPO_MINIMO.md` (seção do reino 3) | critério de aceite escrito antes da sessão |

## Fase 8 — Revisão e fechamento (P0)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R3-8.1 Revisão final** | a) checklist de `MODELO_DE_REINO.md` §6; b) contradições intencionais × acidentais | relatório | todas as portas marcadas |
| **R3-8.2 Ata de fechamento 4** | a) doc 11 do reino 3; b) liberar o reino 4 (Marcas das Feras) | doc 11 | autor aprova |

## Ordem e dependências

`F0 → F1 → F2 → (F3 ∥ F4) → F5 → F6 → F7 → F8`. A F2 roda **junto** da F1. A F6 pode começar em paralelo com a F5 para as cenas-teste. A F1 não começa sem R3-0.3 (D-66) aprovado e R3-0.4 escrito.

## Primeira rodada

1. **R3-0.4** contrato de fronteira (escrito nesta entrega).
2. **R3-0.3** feito (D-66); nomes (D-67) aguardam o autor.
3. **R3-1.1** escrito: os oito lugares (D-68, aguardando o autor). Os módulos que falam em "dez cidades" e "20 assentamentos" (R3-1.1, R3-1.2, R3-1.3 e R3-6.3) valem na escala de oito lugares, sem assentamentos próprios, com os mínimos de NPCs de D-22 mantidos.
4. **R3-1.2 escrito** (D-69, aceita pela continuação). **R3-1.3 escrito** (D-70, aceita pela continuação). **R3-1.4 escrito** (D-71, aceita pela continuação). **R3-1.5 escrito** (D-72, aceita pela continuação). **R3-1.6 escrito** (D-73, aceita pela continuação). **R3-1.7 escrito** (D-74, aceita pela continuação). **Fase 1 concluída (F1).**
5. **R3-2.1 escrito** (D-75, aceita pela continuação). **R3-2.2 escrito** (D-76, aceita pela continuação). **R3-2.3 escrito** (D-77, aceita pela continuação). **R3-2.4 escrito** (D-78, aceita pela continuação). **Fase 2 concluída (F2).** **R3-3.1 escrito** (D-79, aceita pela continuação). **R3-3.2 escrito** (D-80, aceita pela continuação). **R3-3.3 escrito** (D-81, aceita pela continuação). **R3-3.4 escrito** (D-82, aceita pela continuação). **Fase 3 concluída (F3).** **R3-4.1 escrito** (D-83, aceita pela continuação). **R3-4.2 escrito** (D-84, aceita pela continuação). **R3-4.3 escrito** (D-85, aceita pela continuação). **R3-4.4 escrito** (D-86, aceita pela continuação). **Fase 4 concluída (F4).** **R3-5.1 escrito** (D-87, aceita pela continuação). **R3-5.2 escrito** (D-88, aceita pela continuação). **R3-5.3 escrito** (D-89, aceita pela continuação). **R3-5.4 escrito** (D-90, aceita pela continuação). **R3-5.5 escrito** (D-91, aceita pela continuação). **Fase 5 concluída (F5).** **R3-6.1 escrito** (D-92, aceita pela continuação). **R3-6.2 escrito** (D-93, aceita pela continuação). **R3-6.3 escrito** (D-94, aceita pela continuação). **R3-6.4 escrito** (D-95, aceita pela continuação). **Fase 6 concluída (F6).** **R3-7.1 escrito** (D-96, aceita pela continuação). **R3-7.2 escrito** (D-97, aceita pela continuação). **R3-7.3 escrito** (D-98, aceita pela continuação). **Fase 7 concluída (F7).** **R3-8.1 escrito** (D-99, aceita pela continuação). **R3-8.2 escrito** (D-100, aguardando o autor). **Fase 8 concluída (F8); reino 3 concluído editorialmente, reino 4 não liberado.**

## Andamento

| Módulo | Estado | Observação |
|---|---|---|
| R3-0.1 Camada de mundo mínima | **feito** | M-05 aprovado (11 nativos, 17 forasteiros); M-01 e M-02 valem; calendário do Véu fica para o R3-3.4 |
| R3-0.2 Decisões herdadas | **feito** | D-21, D-22, D-23 valem para os três reinos; D-63 e D-64 aplicadas |
| R3-0.3 Escala e identidade | **feito (D-66)**; D-67 aguarda | nome, governo e escala decididos pelo autor: uma área só, capital e 7 lugares de pacto; autoridades de pacto sob rei conquistado em batalha; um só rei há cerca de cem anos, Lord necromante (classe Invocador). D-67 (nomes, `M-10` §6) aguarda o autor |
| R3-1.1 Lugares do reino | **escrito (D-68 aceita pela continuação)** | `reinos/03-veu-dos-malditos/01-reino-do-veu-dos-malditos.md`: regra de duas camadas (coroa e pactos), oito lugares V-01 a V-08 com ficha padrão (liderança, facções, rotas e calendário adiados), povos só de M-05, ponto a conferir sobre a Descida do Argente |
| R3-1.2 Lugares e posição territorial | **escrito (D-69 aceita pela continuação)** | `reinos/03-veu-dos-malditos/03-assentamentos-e-rede-politica.md`: 8 lugares com liderança (papel), obrigação, interesse e tensão, posição relativa qualitativa e pontos de contato; sem assentamentos próprios nem cidade-mãe (D-66); 0 número, 0 rota, 0 campo vazio |
| R3-1.3 NPCs, identidades e conhecimento | **escrito (D-70 aceita pela continuação)** | parte 2 do doc 01: 12 principais (2 heróis vivos, 2 generais), povo do Lord (Humano, ASSUMINDO), 6 heróis lendários, tríade dos 8 lugares, cobertura dos 11 nativos; 25 nomes e 12 IDs sem colisão com os reinos 1 e 2 |
| R3-1.4 Relações políticas e sociais | **escrito (D-71 aceita pela continuação)** | `reinos/03-.../04-instituicoes-justica-pactos.md`: Conselho de 8 assentos, competências, 8 pactos, Provação, sucessão, justiça, tributo qualitativo, governo local, 11 facções com aliados e rivais (matriz simétrica, 0 aliado-rival, 3 `desconhecido` com motivo), 5 situações iniciais |
| R3-1.5 História, economia, leis, defesa e cronologia | **escrito (D-72 aceita pela continuação)** | parte 3 do doc 01 (4 eras, lendas, cronologia relativa sem datas, defesa) e `05-economia-leis-e-fluxos.md` (9 leis com lacunas, economia qualitativa, chegada da Descida em Fozbreu, saída das Fugas por Lamaluz, só do lado do Véu); 0 número, 0 ano de Aeltharyn |
| R3-1.6 Povos, linhagens, cidades e forasteiros | **escrito (D-73 aceita pela continuação)** | `reinos/03-.../10-matriz-povos-nativos-cidades-e-forasteiros.md`: 11 nativos, 10 sub-raças do catálogo, 17 forasteiros (12 com lugar, 5 sem lugar fixo), lugares × povos, papéis por povo, 5 heróis lendários novos, regra D-64; 0 ID fora do catálogo |
| R3-1.7 Mistérios, relógios e oportunidades | **escrito (D-74 aceita pela continuação)** | doc 01 parte 4 (9 mistérios MV-01 a MV-09 com 4 hipóteses e vetos; limites da necromancia do Lord) e `06-modelo-operacional-e-aventuras.md` (8 relógios, 8 conflitos, 8 oportunidades); 0 entidade nova, 0 número, Aeltharyn só em vetos |
| R3-2.1 Registro mestre | **escrito (D-75 aceita pela continuação)** | `reinos/03-.../12-registro-mestre.csv` e `.md`: 49 entidades (8 lugares, 11 facções, 30 pessoas), 18 IDs novos para NPCs já nomeados, 0 ID duplicado, 0 citado sem registro, 0 colisão com os reinos 1 e 2, 12 `povo_id` no catálogo |
| R3-2.2 Facções e lugares como dados | **escrito (D-76 aceita pela continuação)** | `12-facoes.csv` (11 facções) e `12-assentamentos.csv` (8 lugares, cidade-mãe `—` por D-66); 0 campo vazio, 0 aliado-rival, 0 assimetria, 3 rivais `desconhecido` com motivo |
| R3-2.3 Povos e catálogo | **escrito (D-77 aceita pela continuação)** | `referencia/racas-e-povos/ALINHAMENTO_VEU.md`: 11 nativos, 10 sub-raças, 17 forasteiros e 30 pessoas com raça, sub-raça, classe e profissão; 0 ID fora do catálogo |
| R3-2.4 Conferência cruzada | **escrito (D-78 aceita pela continuação)** | `13-conferencia-cruzada.md`: 0 aliado-rival, 0 assimetria, 7 acúmulos de cargo intencionais propostos, quem sabe o quê dos 9 mistérios, CI-01 a CI-12; 2 cabeçalhos corrigidos. **F2 concluída** |
| R3-3.1 Geografia e deslocamento | **escrito (D-79 aceita pela continuação)** | `07-registro-operacional-de-rotas.md` e `07-mapa-esquematico.md`: 7 ligações, 8 acessos, 7 linhas de fronteira; toda duração `desconhecida`; 0 rota Véu–Aeltharyn |
| R3-3.2 Demografia | **escrito (D-80 aceita pela continuação)** | doc 05 seção 5 e `12-demografia.csv`: 8 faixas (1 grande, 3 médias, 4 pequenas), regra de não somar, 0 número, 0 diferença com o doc 01 |
| R3-3.3 Recursos | **escrito (D-81 aceita pela continuação)** | `08-registro-operacional-de-recursos.md` e `09-matriz-material-dos-lugares.md`: termos qualitativos, 9 dependências (7 críticas, com fonte), 3 fluxos; nenhum lugar isolado; 0 número |
| R3-3.4 Calendário e estações | **escrito (D-82 aceita pela continuação)** | doc 01 parte 5, `M-02` §7, `M-10` §8: 4 estações, 5 Dias de Pacto, Contagem das Provações, ano 389 = ano 742 de Aeltharyn (ASSUMINDO), tabela de 8 linhas; 0 evento datado entre reinos. **F3 concluída** |
| R3-4.1 Fichas completas | **escrito (D-83 aceita pela continuação)** | `12-fichas.md` e `12-fichas.csv`: 12 de 12 principais com 17 campos, 12 agendas e 12 pressões distintas, povo, sub-raça, classe e profissão do catálogo, relógio ligado a cada um, 6 acúmulos de cargo marcados (CI-09); 0 entidade nova, 0 número |
| R3-4.2 Competências pelo catálogo | **escrito (D-84 aceita pela continuação)** | `12-competencias.md` e `12-competencias.csv`: 30 pessoas (12 principais, 7 da tríade, 11 heróis lendários) com raça, sub-raça, classe, profissão, capacidades e limites; 0 ID fora do catálogo, 0 campo vazio, toda capacidade com limite |
| R3-4.3 Relações e conhecimento | **escrito (D-85 aceita pela continuação)** | `12-relacoes.md`, `12-relacoes.csv` (74 relações, 0 afeto) e `12-conhecimento.csv` (72 linhas, 30 de 30 pessoas); toda relação com fonte; 0 ID fora do registro, 0 aliado-rival |
| R3-4.4 Voz e ganchos de diálogo | **escrito (D-86 aceita pela continuação); Fase 4 concluída (F4)** | `12-voz.md` e `12-voz.csv`: 12 de 12 principais, 2 a 3 frases-modelo e `nunca_diz`; testes de F4 no fim de `12-voz.md`: 0 falha real |
| R3-5.1 Relógios | **escrito (D-87 aceita pela continuação)** | `12-relogios.md` e `12-relogios.csv`: 8 relógios, cada um com "o que move", "o que trava", 2 gatilhos observáveis, custo por segmento, decisão no segmento 5 e efeito da inação; 0 entidade fora do registro |
| R3-5.2 Cadeias causais | **escrito (D-88 aceita pela continuação)** | `12-eventos.md` e `12-eventos.csv`: 12 eventos-tipo (8 completam relógio, 2 dão gatilho, 2 sem relógio); 0 ID de lugar, pessoa, facção, relógio ou rota inexistente |
| R3-5.3 Sementes de aventura | **escrito (D-89 aceita pela continuação)** | `13-sementes-de-aventura.md` e `.csv`: 24 sementes (8 lugares × P, M, G; ameaça 1 a 4), cada uma com "se ninguém age" e sem solução predefinida; 0 agente fora do registro, 0 relógio ou evento inexistente, 0 mistério resolvido |
| R3-5.4 Reação do reino ao jogador | **escrito (D-90 aceita pela continuação)** | `13-reacao-ao-jogador.md`, `13-reputacao.csv` (19 entidades) e `13-canais.csv` (11 canais): fama qualitativa, estados de procurado, ato-regra-autoridade (L-01 a L-09), favor, reparação (lacuna) e intermediários; 0 número, 0 ID inexistente |
| R3-5.5 Mistérios com limites | **escrito (D-91 aceita pela continuação); Fase 5 concluída (F5)** | `13-misterios-limites.md` e `.csv`: 9 mistérios (MV-01 a MV-09) com 4 hipóteses cada, "a verdade não pode ser", prova possível e ligação a relógio e evento; testes de F5 no fim do `.md`: 0 falha real, 0 regressão |
| R3-6.1 Fichas curtas | **escrito (D-92 aceita pela continuação)** | `14-resumos-de-contexto.md` e `.csv`: 57 entidades (8 cidades, 11 facções, 8 relógios, 30 pessoas), cada uma com resumo ≤500 caracteres (maior 486) e vizinhança ≤300; 0 uso de suspeita, desconhecido ou `nunca_diz`; 0 ID do registro sem resumo |
| R3-6.2 Camadas de informação | **escrito (D-93 aceita pela continuação)** | `14-camadas-de-informacao.md` e `.csv`: 41 fatos (7 públicos, 14 locais, 4 restritos, 16 secretos) com caminho e como descobrir; `camadas` das 12 fichas preenchida; `camada_max` dos resumos definitiva; 0 marcador restrito ou secreto em resumo `local` |
| R3-6.3 Cenas-teste | **escrito (D-94 aceita pela continuação)** | `15-cenas-de-teste.md` e `.csv`: 6 cenas (charco, chegada, audiência, arquivo, rota do Ermo de Sal, orla norte em rumor), cada uma com "entra" (maior 1.901 caracteres) e "não entra"; 0 segredo, relógio ou `nunca_diz` em "entra" |
| R3-6.4 Contrato anti-invenção | **escrito (D-95 aceita pela continuação); Fase 6 concluída (F6)** | seção 9 do doc 06: verificação, quatro classes (fixo, livre, registro, proibido), lista proibida, 7 casos e teste rápido; herda os contratos de Aeltharyn e das Coroas sem alterá-los; testes de F6 em `15-cenas-de-teste.md` §fim e no CHANGELOG: 0 falha real |
| R3-8.2 Ata de fechamento 4 | **escrito (D-100 aguardando o autor); Fase 8 concluída (F8)** | doc 11 do reino 3: 11 de 11 portas, contradições, limites e ata; reino 4 **não liberado**; testes de F8: 0 falha, 0 regressão |
| R3-8.1 Revisão final | **escrito (D-99 aceita pela continuação)** | `RELATORIO_R3_8_1.md`: portas 1 a 10 marcadas (1, 3 e 6 com ressalva ou lacuna), 11 pendente do R3-8.2; 8 contradições intencionais, 0 acidental nova; pendência: 3 facções com rival `desconhecido` |
| R3-7.3 Protótipo mínimo | **escrito (D-98 aceita pela continuação); Fase 7 concluída (F7)** | `PROTOTIPO_MINIMO.md` §11: Fio-Longo, Ondra e Morrovéu em uma linha (contexto B de 1.731 caracteres), condições A e B, 8 turnos, Q1 a Q10 e critério de aceite antes de jogar; nenhuma sessão executada; testes de F7: 0 falha, 0 regressão |
| R3-7.2 Catálogo técnico | **escrito (D-97 aceita pela continuação)** | `MAPA_CATALOGO_KOTLIN.md` §7 (KV1 a KV8): 0 entidade do Véu no `.kt`, 0 colisão de ID, nada instalado nem alterado |
| R3-7.1 Dados e validador | **escrito (D-96 aceita pela continuação)** | `dados/` (21 `.txt`, 390 registros), `converter_reino3.py`, `validar_reino.py` generalizado e `dados/relatorio-validador.md`: **0 erro, 22 avisos, 6 exceções**; relatório do reino 2 idêntico |
| R3-0.4 Contrato de fronteira | **feito** | `plano/CONTRATO_DE_FRONTEIRA_AELTHARYN_VEU.md` |

**Progresso geral:** F0 (4) + R3-1.1 a R3-1.7 + R3-2.1 a R3-2.4 + R3-3.1 a R3-3.4 + R3-4.1 a R3-4.4 + R3-5.1 a R3-5.5 + R3-6.1 a R3-6.4 + R3-7.1 a R3-7.3 + R3-8.1 + R3-8.2 = 37 de 37 módulos = **100%** (ASSUMINDO peso igual por módulo).
