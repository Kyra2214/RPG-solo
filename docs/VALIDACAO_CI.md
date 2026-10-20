# Validação CI — RPG Solo

## Execução autorizada

- Data da autorização: 2026-10-19.
- Branch: `implementation/code-first-combat`.
- Workflow: `.github/workflows/android-ci.yml`.
- Objetivo: executar a suíte JVM/Android e a montagem do APK de depuração no GitHub Actions.
- Escopo configurado: `./gradlew test --stacktrace` e `./gradlew :app:assembleDebug --stacktrace`.
- Artefato esperado: `rpgsolo-debug-apk`, contendo `app-debug.apk`, com retenção configurada de 14 dias.

## Regra de acompanhamento

Os resultados só devem ser marcados como aprovados após confirmação do status do workflow e dos passos individuais. Falhas devem ser registradas com etapa, resumo do erro e link para a execução/log. Nenhum teste é considerado aprovado apenas por ter sido iniciado.

## Resultado observado — 2026-10-19

- Execução #27, commit `527177a40474a2b9f399f6b72998043594b3ba45`: **failure**.
- Link: https://github.com/Kyra2214/RPG-solo/actions/runs/39256629827
- A tentativa original e a repetição dos jobs falharam em poucos segundos.
- O job terminou sem passos registrados (`steps: []`) e sem runner identificado (`runner_id: 0`).
- O endpoint de logs retornou `BlobNotFound`; portanto, não há mensagem de erro de Gradle/teste disponível para atribuir a falha à compilação ou à suíte.
- A execução #26, no commit anterior `09cd4a7420341d093fb918745fb205eb4644de94`, também terminou em **failure**: https://github.com/Kyra2214/RPG-solo/actions/runs/39256577704. A repetição dela também falhou sem passos.
- A execução #23, de 2026-10-18, apresenta o mesmo padrão: falha em cerca de 2 segundos, `steps: []`, `runner_id: 0`, sem nome de runner: https://github.com/Kyra2214/RPG-solo/actions/runs/39126879336.
- Portanto, não é uma falha introduzida pelas alterações de código mais recentes: o bloqueio de inicialização já existia antes delas e persiste em execuções diferentes.

## Diagnóstico e bloqueio externo

O workflow YAML está presente e o GitHub cria os jobs, mas nenhum runner hospedado chega a ser atribuído. Como não há etapa executada nem log, não existe evidência para atribuir o problema ao Gradle, Java ou aos testes. A hipótese principal é uma restrição de execução do GitHub Actions no nível da conta/repositório (por exemplo, limite de uso/faturamento ou disponibilidade/permissão de runner), mas isso **não pode ser confirmado** pelos dados expostos pela API.

A correção não deve ser simulada no YAML. Fazer um job terminar verde sem executar os testes seria falsificar a validação.

## Ação necessária no GitHub

O proprietário precisa abrir **Settings → Billing and licensing / Usage** e **Settings → Actions → General** no GitHub e verificar:
1. se a conta/organização atingiu limite de uso ou precisa atualizar faturamento;
2. se GitHub Actions está habilitado para o repositório e se runners hospedados são permitidos;
3. se há alguma mensagem de bloqueio exibida na página da execução.

Depois que a restrição for removida, repetir a execução. Se o runner iniciar, então investigar e corrigir os erros de compilação/testes que aparecerem.

## Conclusão provisória

**CI inconclusivo; testes e build não estão validados.** A evidência atual aponta para falha anterior à execução das etapas, mas não permite afirmar a causa. Não registrar aprovação nem corrigir código com base em um erro que ainda não foi exposto pelos logs.

## Próximo passo

Verificar a disponibilidade/configuração de runners e a página da execução no GitHub Actions. Quando o job conseguir iniciar e expuser os passos, analisar o erro real; só então corrigir e repetir a validação.
