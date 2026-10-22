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
- Adicionado em `engine/src/test/kotlin/dev/rpgsolo/engine/GameEngineTest.kt` um teste de reset que confere devolução exata dos pontos investidos, preservação da habilidade básica gratuita no nível 1 e limpeza dos investimentos.
- Adicionado em `ai/src/test/kotlin/dev/rpgsolo/ai/ProposalParserTest.kt` um teste que garante rejeição explícita de propostas de evolução/loadout de árvore, ativação de habilidade e aplicação/remoção de maldição pela IA.
- O teste foi escrito, mas **não foi executado**. A execução da suíte completa fica reservada para o fim da fase 17 conforme solicitado.

## Auditoria de efeitos profissionais — defeito encontrado e corrigido na branch

- A comparação de escopo/grupo entre ranks 1–7 das árvores profissionais passivas encontrou um único desvio: `profissao-criptoanalista-7` usava alvo `criptoanalista` e grupo `profession-skill-criptoanalista`, enquanto os ranks 1–6 usavam `criptoanalise` e `profession-skill-criptoanalise`.
- Isso fazia a culminância sair do grupo de acúmulo da própria profissão; dependendo do escopo pedido pelo teste, o rank 7 podia não ser somado junto ao bônus de Criptoanálise como esperado.
- Corrigido na branch: a culminância agora usa `criptoanalise` e `profession-skill-criptoanalise`, preservando a identidade da profissão e a regra de não acumular cópias equivalentes.
- Adicionado teste de catálogo para exigir que as habilidades das árvores profissionais totalmente passivas mantenham os mesmos alvos/grupos de efeito do rank 1 até o rank 7.
- A alteração e o teste ainda não foram executados pela CI, conforme a instrução de só dispará-la ao concluir a fase.
- Reanálise estática após a correção: 1.507 registros em 295 árvores, zero linhas malformadas; nenhuma culminância rank 7 sem efeito tipado (exceto REVIVE, que é resolvida pela ação de ressurreição); nenhum ataque especial com custo de recurso diferente de zero, evitando o caminho de rejeição atual da UI; zero divergências restantes entre alvos/grupos nas árvores profissionais integralmente passivas.

## Auditoria de culminâncias e duração — verificação estática

- As 194 árvores que têm rank 6 e rank 7 comparável por magnitude bruta foram verificadas. Sete pares não têm magnitude do rank 7 maior que a do rank 6, mas todos mudam alvo/escopo ou tipo de efeito (por exemplo, defesa do inimigo versus resistência a dano própria, ou passiva de ofício versus buff de velocidade). Isso não prova fraqueza: a magnitude bruta não compara utilidades distintas. Esses sete casos ficam marcados para revisão de balanceamento contextual, sem alterar números automaticamente.
- Nenhuma culminância rank 7 não-REVIVE está sem efeito tipado. Todos os ataques POWER_STRIKE/MULTI_HIT do catálogo têm custo de recurso zero, compatível com o resolvedor atual da UI, que rejeita ataques especiais com custo não-zero. A única culminância REVIVE verificada custa 10 PM e segue o caminho específico de ressurreição.
- Catálogo de maldições: 100 registros, 200 efeitos, nenhuma linha malformada, nenhum efeito com progressão por nível (perLevel diferente de zero) e nenhum grupo que misture efeitos positivos e negativos para o mesmo alvo. As durações são permanentes (45) ou temporárias de 3–6 rodadas (55), e a UI exige combate para aplicar as temporárias.
- A revisão dos sete pares de culminância deve confirmar duração, alvo, uso real e valor narrativo/mecânico; não será feita normalização automática que possa apagar distinções de suporte/controle.

## Integridade da progressão — auditoria de pré-requisitos

- Auditoria estática dos 1.507 registros: IDs únicos; todas as 1.170 referências de pré-requisito apontam para habilidade existente; nenhum pré-requisito cruza para outra árvore; nenhum requisito forma ciclo; nenhum nível de personagem exigido regressa em relação ao pré-requisito.
- As árvores de classe, profissão e classe amaldiçoada mantêm ranks 1–7 completos; as 100 árvores de raça/sub-raça têm uma habilidade básica gratuita cada. Nenhuma habilidade marcada como gratuita exige custo de desbloqueio nem pertence a origem não permitida.
- Adicionado teste de regressão para existência, pertença à mesma árvore, coerência de nível e ausência de ciclos na cadeia de pré-requisitos. Teste ainda não executado; CI continua deliberadamente adiada até o fim da fase.

## Maldições — alinhamento de duração e cobertura de escopos

- O catálogo foi comparado campo a campo: os 200 efeitos das 100 maldições têm duração igual à da maldição (permanente ou temporária); todas as maldições temporárias declaram teste de resistência. Adicionado teste de regressão para essa correspondência.
- Já existem testes de motor escritos para exigir a resolução de resistência, aceitar aplicação após falha, expirar a duração e remover com motivo explícito; o teste novo cobre persistência em salvar/reabrir. Todos ainda aguardam execução nesta fase.
- Achado de cobertura que permanece aberto: 665 efeitos de profissões usam 84 escopos que não aparecem como IDs de teste, atributos ou escopos das perícias/traços do pacote atual. A inspeção de `GameLoop.adjusted` confirmou que `PendingCheck` repassa `request.skill` como escopo externo. Adicionado teste unitário de `prepareAdvancedCheck` que prova que o bônus contextual de `criptoanalise` altera o modificador e aparece no registro/descrição do teste. Isso valida o encanamento do escopo ao resultado preparado, mas ainda não é teste ponta a ponta do catálogo real → pedido do mestre → rolagem persistida. Não remapeei os outros alvos automaticamente para não mudar as regras sem confirmação.

## Híbridos — cobertura dos escopos sociais

- A revisão do conjunto `HybridClassRules.socialPenaltyScopes` contra os escopos reais do catálogo e as perícias do pacote próprio encontrou lacunas: `intimidacao` não estava incluído, e as árvores usam também `argumentacao`, `mediacao` e `comerciante`. Nesses testes contextuais, a penalidade de aparência híbrida poderia não ser aplicada mesmo quando o teste usava o escopo correspondente.
- Corrigido nesta branch: incluídos esses quatro escopos, mantendo os escopos sociais existentes e sem adicionar alvos de combate. Adicionado teste de regressão para cobertura dos escopos sociais e para garantir que a penalidade não contamine `ataque`, `dano`, `defesa`, `resistencia_dano`, `velocidade` ou `precisao`.
- O teste foi escrito, mas não executado. Ainda é necessário validar por execução o caminho real do teste social e confirmar que os pacotes adicionais podem declarar seus próprios escopos sem pressupor equivalência automática.

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
