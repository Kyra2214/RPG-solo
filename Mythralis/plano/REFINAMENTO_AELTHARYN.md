# Refinamento do Reino das Florestas (Aeltharyn)

**Objetivo:** levar Aeltharyn de "fechado editorialmente" para **pronto para ser implementado**: IDs consistentes, lacunas decididas, personagens com profundidade, mundo vivo e contexto enxuto para a IA. Só depois disso abre-se o reino 2.
**Regra:** nada aqui inventa número, distância ou segredo para fechar buraco. Onde falta evidência, o autor decide ou o item continua "desconhecido".
**Legenda:** prioridade P0 (bloqueia o resto), P1 (importante), P2 (desejável). Esforço: P (pequeno), M (médio), G (grande).

## 0. Linha de base (medida nos arquivos deste projeto)

| Item | Nos documentos (`.md`) | No catálogo técnico (`.kt`) | Leitura |
|---|---|---|---|
| Cidades | 10 | 10 | conferem |
| Povos e linhagens | 16 (11 nativos + linhagens) | 16 | conferem; todos os IDs existem no catálogo de personagem |
| Assentamentos | 20 (`settlement.*`) | **0** | só em texto |
| Facções | 10 (`faction.*`) | **0** | só em texto |
| Pessoas (NPCs) | 62 `npc.*` com ID | 51 figuras (37 vivas) | **95 pessoas distintas**: 12 nos dois, 7 com ID conflitante, 44 só nos docs, 32 só no catálogo |
| Pessoas só nos docs | 43 (rei, intendentes, conselho, líderes dos assentamentos, NPCs vivos) | ausentes | o catálogo não tem o governo |
| Pessoas só no catálogo | — | 32 (os 51 nomes aparecem nos docs; 7 têm ID diferente) | são as mesmas pessoas; faltam os IDs nos docs |
| Rotas | tabela em texto, algumas sem duração | 14, todas com dias | corredores sem duração fora do catálogo |
| Relógios | 7 | 7 | conferem |

Em resumo: o catálogo técnico cobre a **rede herói/guardião/benfeitor** (17+17+17), mas **não** o governo, os NPCs vivos relevantes, as facções nem os assentamentos. Esse é o maior buraco de consistência.

## Fase 0 — Decisões e linha de base (P0)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **M0.1 Decisões do autor** | a) Corvídeos × Corvários (`kenku` = Corvídeo, `homem-corvo` = Corvário); b) nome do mundo × nome do reino (Aeltharyn nomeia os dois); c) padronizar nomes de exibição (Silvano Festivo, Felídeo, Verdejante, Micélioide) | `plano/DECISOES.md` | cada decisão tem data, escolha e texto a corrigir |
| **M0.2 Inventário oficial** | a) contagens canônicas (cidades, assentamentos, povos, facções, NPCs, rotas, relógios); b) congelar a tabela da seção 0 como referência | tabela atualizada neste arquivo | números aprovados pelo autor |

## Fase 1 — Registro mestre e consistência docs ↔ catálogo (P0)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **M1.1 Registro mestre de entidades** | a) tabela única: id, tipo, nome, cidade, povo, facção, estado inicial, documento-fonte; b) dar ID nos docs às 32 pessoas que só têm ID no código; c) conferir os 12 em comum | `reinos/01-.../12-registro-mestre.md` | todo NPC citado tem 1 ID, e nenhum ID aparece com duas pessoas |
| **M1.2 Governo e elenco político** | a) entrada formal do rei Aerendyl Vaelorian, intendentes, matriarcas, Conselho das Sete Copas; b) herdeiros e sucessão; c) generais e heróis vivos | seção no registro mestre | as 43 pessoas só dos docs têm ficha mínima |
| **M1.3 Facções e assentamentos como dados** | a) 10 facções: objetivo, liderança, recursos, métodos, aliados, rivais, presença; b) 20 assentamentos: liderança, obrigação, interesse, tensão, cidade-mãe | tabelas no registro mestre | nenhum campo vazio; vínculos apontam para IDs existentes |
| **M1.4 Padronização de nomes** | a) aplicar as decisões do M0.1 em todos os docs; b) tabela de sinônimos; c) rodar o alinhamento com o catálogo de personagem | `ALINHAMENTO_AELTHARYN.md` atualizado | 0 divergência de nome ou ID |
| **M1.5 Conferência cruzada de relações** | a) aliado e rival ao mesmo tempo; b) idades e cargos duplicados; c) quem sabe o quê (sem conhecimento sem fonte) | relatório curto | lista de contradições vazia ou todas marcadas como intencionais |

## Fase 2 — Lacunas de evidência (P1; decisões do autor)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **M2.1 Geografia e deslocamento** | a) acessos locais dos 20 assentamentos; b) conexões sem duração (ao menos Arven↔Silvaran e Orvass↔Brumafunda); c) mapa esquemático (grafo + posição relativa) | atualizar doc 07 e criar o mapa | cada conexão tem duração ou continua explicitamente "desconhecida" |
| **M2.2 Demografia** | a) trocar estimativas por faixas; b) regra "não somar" (subtotal 37.250 dos assentamentos não é total) | doc 05 | nenhum total apresentado como censo |
| **M2.3 Recursos** | a) escolher escala (ordinal 0 a 5 em vez de números inventados); b) preencher produção, reserva e demanda dos recursos centrais (resina, grãos, madeira, minério, água) | doc 08 | cada dependência crítica tem estado e fonte |
| **M2.4 Calendário e estações** | a) meses e estações da Contagem das Seivas; b) agenda sazonal ligada aos relógios | seção nova no doc 01 | ano 742 e estação inicial definidos; liga com M-02 do plano de reinos |

## Fase 3 — Profundidade dos personagens (P1)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **M3.1 Fichas completas** | a) campos do `MODELO_DE_REINO.md`; b) agenda e pressão **distintas** por NPC; c) começar por Elarion e uma região de teste | fichas por cidade | 100% dos NPCs da região de teste completos |
| **M3.2 Competências pelo catálogo** | a) raça/sub-raça, classe e profissão de cada figura (IDs do catálogo); b) capacidades e **limites** coerentes | coluna no registro mestre | todo ID de classe, profissão e raça existe no JSON |
| **M3.3 Relações e conhecimento** | a) matriz relações (afeto, dívida, rivalidade, medo); b) o que cada NPC sabe: confirmado, suspeita, desconhecido | matriz | nenhuma relação sem origem |
| **M3.4 Voz e ganchos de diálogo** | a) 2 a 3 frases-modelo por NPC principal; b) o que ele nunca diz | campo curto na ficha | usável como instrução de tom, sem texto longo |

## Fase 4 — Mundo vivo (P1)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **M4.1 Relógios** | a) gatilhos de avanço dos 7 relógios; b) custo e consequência por segmento; c) efeito da inação | doc 06 ou tabela nova | cada relógio tem "o que o move" e "o que o trava" |
| **M4.2 Cadeias causais** | a) 10 a 15 eventos-tipo (rota fechada, dique rompido, rei adoece...); b) efeitos em cidades, recursos e relações | tabela de eventos | cada evento aponta efeitos em IDs existentes |
| **M4.3 Sementes de aventura** | a) por cidade e por assentamento: 1 pequena, 1 média, 1 grande; b) sem solução predefinida; c) nível de ameaça sem rebaixar o mundo | `13-sementes-de-aventura.md` | nenhuma semente depende de um NPC inexistente |
| **M4.4 Reação do reino ao jogador** | a) fama e reputação por cidade; b) procurado, favor e reparação; c) papel dos intermediários (cartaz, caçadores, emissários) | seção nova | liga ao MP-06 e MP-07 do plano do jogo |
| **M4.5 Mistérios com limites** | a) manter sem resposta única; b) listar o que a verdade **não pode** ser | seção nos mistérios | nenhum mistério contradiz fato confirmado |

## Fase 5 — Pacote de contexto para IA (P0 para a integração)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **M5.1 Fichas curtas** | a) resumo de até ~500 caracteres por cidade, assentamento, NPC, facção e relógio; b) uma linha de "vizinhança" (ex.: cidade-mãe em 1 linha) | `14-resumos-de-contexto.md` | cada entidade tem versão curta com ID |
| **M5.2 Camadas de informação** | a) público, local, restrito, secreto por fato; b) o que o personagem pode descobrir e como | campo na ficha | segredos nunca aparecem em resumo público |
| **M5.3 Cenas-teste** | a) vilarejo élfico (o exemplo do autor): o que entra e o que fica fora; b) chegada ao porto de Thalanor; c) audiência na corte; d) investigação nos arquivos; e) viagem por rota | `15-cenas-de-teste.md` | para cada cena, lista "entra" (≤ ~2.400 caracteres) e "não entra" |
| **M5.4 Contrato anti-invenção** | a) fatos fixos, livres e proibidos; b) o que a IA pode propor e o que exige registro | seção no doc 06 | regra clara para novo rei, cidade ou guerra |

## Fase 6 — Preparação para implementar (P1; só especificação)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **M6.1 Esquema de dados** | a) decidir texto com `|` ou JSON; b) esquema genérico de reino (cidade, assentamento, NPC, facção, rota, relógio, recurso) | `plano/ESQUEMA_DE_DADOS.md` | serve também aos reinos 2 a 5 |
| **M6.2 Validador de reino** | a) lista de regras (IDs únicos, referências existentes, rotas e portos coerentes, mínimos); b) separar erro de aviso | especificação | cobre os 12 critérios do documento do autor |
| **M6.3 Mapa do catálogo Kotlin** | a) campo a campo para o esquema novo; b) o que descartar (`install` ansioso) | tabela | ninguém depende do `.kt` para entender os dados |
| **M6.4 Protótipo mínimo** | critério: um vilarejo, seus NPCs e a cidade-mãe em uma linha, jogados numa sessão com e sem o mundo | roteiro do teste | métrica definida antes de jogar |

## Fase 7 — Revisão e fechamento (P0 no final)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **M7.1 Revisão final** | a) reler com o checklist de `MODELO_DE_REINO.md`; b) contradições intencionais × acidentais | relatório | todas as portas marcadas |
| **M7.2 Ata de fechamento 2** | a) atualizar `11-fechamento...`; b) liberar o reino 2 | doc 11 revisado | autor aprova |

## Ordem e dependências

`F0 → F1 → (F2 ∥ F3) → F4 → F5 → F6 → F7`. F2 e F3 podem andar em paralelo depois da F1. A F5 pode começar em paralelo com a F4 para as cenas-teste.

## Primeira rodada sugerida (curta)

1. **M0.1** (3 decisões do autor).
2. **M1.1** (registro mestre, o maior ganho de consistência).
3. **M1.4** (padronizar nomes).
4. **M5.3a** (cena do vilarejo élfico com "entra" e "não entra").

Isso destrava o resto e já produz algo testável.

## Andamento

| Módulo | Estado | Observação |
|---|---|---|
| M0.1 Decisões do autor | **feito** | `DECISOES.md` (D-01 a D-07) decididas em 25/10/2026 |
| M0.2 Inventário oficial | **feito, aprovado (D-06)** | números na seção 0 e em D-06 |
| M1.1 Registro mestre | **feito, v2** | `reinos/01-.../12-registro-mestre.md` e `.csv`; a v2 corrigiu 2 para 7 conflitos de ID (99 para 94; 95 com o Vaunor Eir, D-08) |
| M1.2 Governo e elenco | **feito** | seção 7 do registro mestre: ficha mínima das 44; Vaunor Eir registrado (D-08) |
| M1.3 Facções e assentamentos como dados | **feito** | registro mestre v4, seções 8 e 9 + `12-facoes.csv` e `12-assentamentos.csv`; aliados e rivais ficam "não informado" em 8 das 10 facções (lacuna do doc 01) |
| M1.5 Conferência cruzada | **feito** | `plano/RELATORIO_M1_5.md`; D-07 e D-09 a D-11 decididas; contradições intencionais no doc 11 (adendo) |
| M1.4 Padronização de nomes | **feito** | D-01 a D-07 aplicadas; 94 IDs nos docs, 51 iguais ao catálogo; sinônimos em `ALINHAMENTO_AELTHARYN.md` |
| M2.1 Geografia e deslocamento | **feito** | doc 07 (seções 3, 3.1, 3.2) e `07-mapa-esquematico.md`; Arven–Silvaran 2–4 d (corredor); Orvass–Brumafunda segue desconhecida; D-12 decidida (Nymvar–Arven 2–4 d caravana) |
| M2.2 Demografia | **feito** | doc 05 seções 1.1, 1.2, 1.2.0; registro mestre v6 seção 11 e `12-demografia.csv`; categoria indefinida para todas, nada somado |
| M2.3 Recursos | **feito** | doc 08 seção 12 (escala 0–5, papéis e dependências de grãos, água, madeira, resina e minério) e `12-recursos.csv`; produção com estimativa provisória para 7 produtores (D-13) e reserva em ?; demanda derivada só para grãos e água |
| M2.4 Calendário e estações | **feito** (nomes das luas aguardando D-17) | doc 01 seção 35: ano de 742 aberto em Silêncio (Lua 1, dia 1), estações, Dias Sem Raiz e agenda sazonal dos 7 relógios; M-02 parcial |
| M3.1 Fichas completas | **feito** (região de teste: Elarion) | `12-fichas-elarion.md` e `.csv`: cidade, 2 assentamentos e 10 pessoas com todos os campos do modelo; agenda e pressão marcadas por origem [doc], [.kt] ou ASSUMINDO; D-18 e D-19 abertas |
| M3.2 Competências pelo catálogo | **feito** | `12-competencias.csv` e seção 12 do registro mestre: 95 pessoas; todos os IDs existem no JSON; classe (32) e profissão (42) são propostas com base no texto; D-20 aberta |
| M3.3 Relações e conhecimento | **feito** | `12-relacoes.csv` (76 relações, todas com fonte) e `12-conhecimento.csv` (59 linhas, 33 pessoas); seção 13 do registro mestre; sem relação de "medo" escrita nos textos |
| M3.4 Voz e ganchos de diálogo | **feito** | `12-voz.csv` (25 NPCs principais: tom, 2 frases-modelo, nunca diz) e seção 14 do registro mestre; frases são propostas de tom |
| M4.1 Relógios | **feito** | `12-relogios.md` e `.csv`: para os 7 relógios, o que move (doc 01 §26), o que trava, quem paga, ao completar e efeito da inação; trava, custo e inação são leitura editorial (ASSUMINDO) |
| M4.2 Cadeias causais | **feito** | `12-eventos.md` e `.csv`: 13 eventos-tipo (prefixo `event.`) com gatilho, cadeia e efeitos em cidades, recursos, pessoas, facções e rotas; todos os IDs existem; quais cidades suspendem o pacto fica "a definir" |
| M4.3 Sementes de aventura | **feito** | `13-sementes-de-aventura.md` e `.csv`: 90 sementes (10 cidades + 20 assentamentos × P, M, G); ameaça 1–4 (ASSUMINDO), abordagens sem solução predefinida e "se ninguém age"; todos os IDs existem e nenhum NPC morto é usado |
| M4.4 Reação do reino ao jogador | **feito** | `13-reacao-ao-jogador.md`, `13-reputacao.csv` (10 cidades + 10 facções) e `13-canais.csv` (10 canais); estados de procurado, favor e reparação a partir do doc 04 e das leis do doc 01; cartaz e caçador de pessoas sem base no cânone, entram só como canais |
| M4.5 Mistérios com limites | **feito** | `13-misterios-limites.md` e `.csv`: 8 mistérios (A a E, H, I, J), cada um com fatos confirmados, 2+ hipóteses abertas, o que a verdade não pode ser e evidência para resolver; ponteiro no doc 01 §13 |
| M5.1 Fichas curtas | **feito** | `14-resumos-de-contexto.md` e `.csv`: 142 resumos com ID (10 cidades, 20 assentamentos, 10 facções, 7 relógios, 95 pessoas), até 500 caracteres cada, mais uma linha de vizinhança; sem dedução, sem segredos |
| M5.2 Camadas de informação | **feito** | `14-camadas-de-informacao.md` e `.csv`: 66 fatos (6 públicos, 34 locais, 14 restritos, 12 secretos) com titular, caminho e como descobrir; coluna `camada_max` nos resumos (relógios = secreto); campo *Camadas* nas fichas de Elarion; critério de camada é leitura editorial (ASSUMINDO) |
| M5.3 Cenas-teste | **feito** | `15-cenas-de-teste.md` e `.csv`: 5 cenas (a a e) com "entra" montado só de resumos, fatos público/local, sementes e rotas escritos (872 a 1.913 caracteres, todas abaixo de 2.400) e "não entra" com o motivo; nenhum fato restrito, secreto ou relógio entra |
| M5.4 Contrato anti-invenção | **feito** | doc 06 seção 11.1: classes fixo, livre, exige registro e proibido; lista de proibidos; regras para novo rei, nova cidade, guerra, NPC novo e morte de NPC; teste de 4 perguntas (classificação é leitura editorial, ASSUMINDO) |
| M6.1 Esquema de dados | **feito** (formato aguardando D-21) | `plano/ESQUEMA_DE_DADOS.md`: regras comuns, 8 entidades principais (reino, cidade, assentamento, NPC, facção, rota, relógio, recurso), 10 de apoio, molde e deltas; formato `|` recomendado (ASSUMINDO) |
| M6.2 Validador de reino | **feito** | `plano/VALIDADOR_DE_REINO.md`: 47 regras (erro ou aviso) em 8 grupos, exceções registradas, cobertura dos 12 critérios do autor (5 ficam para testes de integração I-01 a I-06) e previsão não executada para Aeltharyn; o esquema ganhou `papeis`, `principal`, `povos`, `marcador` e `ids_aposentados` |
| M6.3 Mapa do catálogo Kotlin | **feito** | `plano/MAPA_CATALOGO_KOTLIN.md`: campo a campo de `WorldCity`, `WorldPeople`, `WorldFigure`, `WorldRoute` e `WorldClock` para o esquema; 9 itens descartados (`install`, `Place.secret`, `population: Int`...); `validate()` e testes mapeados às regras V; lacuna do esquema (`povo_no_reino.txt`) e regra opcional V-A1 |
| M6.4 Protótipo mínimo | **feito** | `plano/PROTOTIPO_MINIMO.md`: recorte Raiz-de-Prata + Nivara + Elarion em uma linha, condições A (sem mundo) e B (com mundo), roteiro de 8 turnos, 10 métricas (P1 a P10) e critério de aceite fixados antes de jogar; sessões não executadas |
| M7.1 Revisão final | **feito** | `plano/RELATORIO_M7_1.md`: 11 portas conferidas (9 ok, 2 parciais: ≥ 2 generais e aliados/rivais de 8 facções); contradições intencionais × acidentais separadas; D-22 e D-23 abertas |
| M7.2 Ata de fechamento 2 | **feito** | doc 11 revisado (`Ata de fechamento 2`): estado, portas, decisões em aberto, limites; **aprovada pelo autor em 26/10/2026**; reino 2 liberado |

- [x] F0  [x] F1  [x] F2  [x] F3  [x] F4  [x] F5  [x] F6  [x] F7

**Progresso geral:** 30 de 30 módulos concluídos = **100%** (ASSUMINDO peso igual por módulo; F0 2, F1 5, F2 4, F3 4, F4 5, F5 4, F6 4, F7 2). **Fase 1 concluída.** **Fase 2 concluída.** **Fase 3 concluída.** **Fase 4 concluída.** **Fase 5 concluída.** **Fase 6 concluída.** **Fase 7 concluída.** Fechamento 2 aprovado (26/10/2026). Próximo: decisões D-17 a D-23 e início do reino 2.

*Contagens da seção 0 feitas por leitura dos arquivos deste projeto; reconfirmar no M0.2.*
