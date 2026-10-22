# Validação pós-merge — 2026-10-22

## Escopo
Validar o commit de integração das árvores de habilidades e maldições na `main` (`8553bec43a9e1543fe54995e06df0e212ee940e0`).

## Critérios automatizados
O workflow Android CI deve executar `./gradlew test --continue --stacktrace` e `./gradlew :app:assembleDebug --stacktrace`. O teste E2E é diagnóstico e não é gate desta validação.

## Auditoria estática inicial
- O workflow configurado executa testes JVM/Android e build do APK debug em push para `main`.
- Os catálogos tipados de habilidades e maldições e seus testes existem na `main`.
- A CI verde anterior ao merge não substitui a validação do commit integrado.

## Resultado
Pendente até que a execução do workflow para este commit de documentação (com o mesmo código de aplicação integrado) seja confirmada. Não marcar a fase 17 como concluída antes de conferir o resultado real e revisar os relatórios.


## Bloqueios encontrados na auditoria estática

1. `engine/Commands.kt` valida `ProgressSkillTree` quanto a custo/saldo/nível, mas não consulta o catálogo canônico nem valida árvore, afinidade ou pré-requisitos. Essas regras são verificadas por `SkillTreeActions`; se comandos puderem ser propostos diretamente pela IA, a validação não é centralizada no motor.
2. `ApplyCurse` valida alvo, duração e resultado da resistência, mas não verifica se `curseId` existe no catálogo. A validação de catálogo está na camada `CurseActions`.
3. `ApplyTargetSkillEffects` verifica se a habilidade foi aprendida e valida turno/alvo/forma dos efeitos, mas recebe os valores de efeito no próprio comando; não compara esses valores com a definição canônica da habilidade.
4. Esses três pontos precisam ser resolvidos ou ter uma barreira autoritativa comprovada no gateway da IA antes de declarar paridade mecânica IA/jogador. Não apliquei uma correção estrutural sem poder executar testes.

## Limitação de execução neste ambiente

- O clone local não pôde ser feito porque a resolução de `github.com` está indisponível neste ambiente.
- A API de status consultada não retornou checks para o commit; o conector disponível não expõe listagem/dispatch de execuções push de Actions. Portanto, o resultado real do workflow pós-merge permanece não confirmado.
- A validação automatizada permanece pendente: testes JVM, build do APK e inspeção dos relatórios.
