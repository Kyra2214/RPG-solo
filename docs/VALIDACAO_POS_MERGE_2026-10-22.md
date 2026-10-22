# Validação pós-merge — 2026-10-22

## Escopo

Validar o merge das árvores de habilidades e maldições na `main` (`8553bec43a9e1543fe54995e06df0e212ee940e0`). A fase 17 segue aberta; nenhum resultado parcial encerra a fase.

## Regra operacional

Por solicitação do usuário, **não disparar CI durante a execução da fase 17**. Disparar a validação automatizada completa somente quando as auditorias, correções e testes estáticos planejados estiverem concluídos. Testes escritos não são testes executados.

## Auditoria estática dos catálogos

- Catálogo de habilidades: 1.507 registros, 295 árvores, 1.530 efeitos tipados; nenhuma linha malformada no exame estrutural.
- As árvores principais incluem 50 classes, 103 profissões e 42 vertentes da Classe de Malditos.
- Catálogo separado de maldições lançadas: 100 registros e 200 efeitos tipados; cada registro examinado declara efeitos positivos e negativos; nenhuma linha malformada no exame estrutural.
- A comparação automática dos efeitos culminantes por magnitude bruta não é suficiente para declarar equilíbrio: efeitos de ataque, defesa, penalidade direcionada e ofício não são diretamente comparáveis. A revisão de culminâncias deve considerar alvo, duração, número de golpes, custo, requisito e efeito real no resolvedor.
- A auditoria do ciclo de maldição encontrou testes de resistência, decremento/expiração e remoção com replay no motor. Faltava um teste explícito de salvar/reabrir campanha durante o ciclo de duração.

## Alteração feita nesta branch de auditoria

- Adicionado em `engine/src/test/kotlin/dev/rpgsolo/engine/CampaignPersistenceTest.kt` um teste que aplica uma maldição temporária, reabre a campanha, avança rodadas, reabre novamente e confirma decremento e expiração, além de conferir replay do log.
- O teste foi escrito, mas **não foi executado**. A execução da suíte completa fica reservada para o fim da fase 17 conforme solicitado.

## Integridade das ações e paridade IA/jogador

1. `ProgressSkillTree` no motor valida saldo/custo/progressão, mas não recebe o catálogo canônico; árvore, afinidade e pré-requisitos são validados em `SkillTreeActions`.
2. `ApplyCurse` valida alvo, duração e resistência, mas não verifica por si só se o ID existe no catálogo; essa validação está em `CurseActions`.
3. `ApplyTargetSkillEffects` verifica habilidade aprendida, turno/alvo e estrutura do efeito, mas recebe os valores de efeito no comando sem comparar com a definição canônica.
4. O parser de propostas da IA não permite propor diretamente os comandos de evolução da árvore nem de aplicação de maldição. Portanto, não há evidência de que a IA normal consiga explorar diretamente esses três pontos. Ainda assim, o motor não é totalmente autoritativo contra chamadas internas ou futuras rotas que contornem a camada de aplicação; é necessário decidir e testar uma fronteira de validação única sem criar dependência arquitetural indevida entre módulos.
5. A revisão de culminâncias precisa confirmar que as habilidades especiais usam o resolvedor de combate existente, respeitam turno/recursos/alvos e não se tornam apenas texto narrativo.

## Estado de CI observado antes da pausa

A execução #248 do Android CI, associada ao commit de documentação `6f2b69bd990c315c07343104052c84e6e97fbdf6`, estava em andamento quando consultada:
- `Run JVM and Android tests`: concluída com sucesso.
- E2E: em andamento (diagnóstico, não gate principal).
- Build do APK debug: ainda pendente no momento da consulta.

Esse estado é um resultado parcial de uma execução já iniciada pelo push anterior, não uma nova execução disparada durante esta etapa. Não prova o resultado de alterações nesta branch.

## Critérios restantes antes de concluir a fase 17

- Completar auditoria dos efeitos contra os escopos usados pelos resolvedores reais de combate/testes e corrigir efeitos que não tenham consumidor mecânico.
- Revisar culminâncias e suas regras de alvo, duração, custo e consequências, sem substituir a fórmula original de combate.
- Confirmar o ciclo de vida e persistência das maldições e os efeitos híbridos.
- Resolver ou documentar uma barreira autoritativa para a integridade dos comandos de habilidades/maldições.
- Só então disparar a CI completa: `./gradlew test --continue --stacktrace` e `./gradlew :app:assembleDebug --stacktrace`; analisar relatórios, corrigir falhas e repetir até obter resultado conclusivo.
- Não marcar a fase 17 nem a implementação geral como concluídas antes da validação real.
