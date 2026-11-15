# Plano de correções pós-teste em aparelho (14/11/2026, reformulado)

Origem: teste do proprietário em aparelho real (OpenRouter, chave válida). Um submódulo por vez, na ordem da tabela, com ZIP completo ao fim de cada um. Estado: PT-00 corrigido e **confirmado pelo proprietário**; PT-01 a PT-06 concluídos em código (não compilados); PT-07 com os testes escritos (não executados), aguardando CI e aparelho.

## Achados

| ID | O que aconteceu | Causa encontrada no código |
|---|---|---|
| PT-00 | "+" dos pontos de status não atualizava | `PassoStatus` não lia `est.tick` (rascunho não observável). **Corrigido e confirmado em aparelho** |
| A1 | "Resposta do provedor ilegível: sem texto em `choices[0].message.content`" com chave correta (modelo `nvidia/nemotron-3.5-super-vl:free`) | `Providers.kt` só aceitava `content` como texto simples; modelos de raciocínio e visão devolvem `content` nulo, lista de partes ou texto em `reasoning` |
| A2 | Modelos eram escolhidos à mão | `ConfigScreen.kt` exige "Buscar modelos gratuitos", mostra só 12 (`freeModels.take(12)`) e só salva com um modelo escolhido; `ModelDiscovery` filtra gratuitos, mas não exclui modelos de visão (`-vl`) nem testa se respondem |
| B | História do personagem escrita à mão na criação | `PassoInformacoes` (`CriacaoAbas.kt`): aparência, personalidade e história em texto livre, regra de 40 a 4000 caracteres no total (`loreOk`) |
| C | Nova campanha só aceita texto livre | `CampaignsScreen.kt`: o campo de descrição vem primeiro e o formulário auxiliar fica escondido |
| D | Importar e exportar JSON ficam na tela Heróis, junto da criação, e poluem a tela | Cartão "Importar ficha por JSON" (`CharactersScreen.kt`, ~l. 737) e botão "Copiar JSON" em cada ficha da biblioteca (~l. 923); `AppModel.importFicha` e `exportFicha` já existem |
| E | Biblioteca de fichas: botões quebram letra por letra e há mensagem solta "cole o JSON da ficha" | Linha de `TextButton` sem quebra (`CharactersScreen.kt`, ~l. 905–930) |

## Submódulos (ordem de execução)

| # | Submódulo | Escopo | Pronto quando |
|---|---|---|---|
| PT-01 | Leitura robusta da resposta do provedor (A1) | **Concluído em código**: `ai/Providers.kt` (não compilado, sem teste novo até o PT-07): `content` como texto ou lista de partes; `reasoning`/`reasoning_content` só como último recurso e só com término normal; `refusal`, `finish_reason=length` e `{"error":...}` com status 200 viram erro com o nome do modelo e o motivo (a cadeia então troca de modelo). "Testar conexão" (`AppModel.testConnection`) pede 256 tokens e mostra o modelo e o motivo | O modelo da captura responde ou falha com motivo legível |
| PT-02 | **Concluído em código** — Modelos gratuitos automáticos (A2) | Ao salvar a chave, buscar a lista sozinho; filtrar só texto→texto (excluir visão, imagem, áudio, embeddings); sondar com chamada curta e manter na cadeia só quem responde texto; ordenar por contexto, suporte a JSON e porte; cache com data e nova busca ao falhar ou a cada 24 h. Configuração: sem escolha manual, mostrar "N modelos prontos" e botão "Atualizar modelos"; escolha manual vira opção avançada recolhida | Salvar a chave basta; a cadeia se monta sozinha. **Feito**: `ModelDiscovery` só texto→texto (entrada e saída `text`, sem `-vl`/visão/imagem/áudio/OCR); `ModelProbe` sonda em ordem e guarda até 6 que respondem texto (máx. 16 tentativas); `AppModel.refreshFreeModels` monta e grava a cadeia; renovação a cada 24 h na abertura e, no máximo a cada 10 min, quando todos os modelos da cadeia falham (`FallbackProvider.onAllFailed`); `ConfigScreen` mostra "N modelos prontos", "Atualizar modelos" e a escolha manual recolhida em "Avançado" |
| PT-03 | **Concluído em código** — Nova campanha por seleção (C) | Formulário como padrão: sistema, mundo/cenário (Mythralis, reinos, mundo próprio), tom, dificuldade, ritmo e modo; predefinições carregadas; texto livre vira "Detalhes extras" opcional; "Interpretar" por texto vira atalho secundário. Sem migração; entra no `CampaignStarter` existente | Criar campanha sem digitar nada. **Feito** em `NewCampaignSection.kt`: chips de tipo de aventura (4 predefinições), tom, dificuldade, ritmo e modo; texto livre virou "Detalhes extras (opcional)"; "Prefiro descrever por texto livre" é o atalho secundário (fluxo antigo). A seleção vira descrição via `CampaignForm.toDescription()` e entra no `NewCampaignFlow` existente |
| PT-04 | **Concluído em código** — História do personagem pela IA (B) | Tirar a história escrita à mão da criação (aparência por chips e "Sortear"; personalidade por escolha); remover a regra 40–4000 como bloqueio. Com a ficha criada, botão/balão "Pedir à IA para criar a história" usando raça, origem, classe, profissão, vantagens e aparência; texto editável, com regenerar e salvar. Sem provedor ativo, explica como configurar. A IA só propõe; o motor grava | Ficha criada sem texto livre e história gerada a partir da ficha. **Feito**: `PassoInformacoes` sem campo de história nem de aparência/personalidade livres (aparência por chips + "Sortear"; personalidade por chips + "Sortear" em `CriacaoPersonalidade`); regra 40–4000 removida como bloqueio; após criar, `CharactersScreen` mostra o cartão "Pedir à IA para criar a história" (gerar, editar, gerar de novo, salvar) sobre `AppModel.generateLore`/`approveLore` existentes; sem provedor, a mensagem manda configurar a IA |
| PT-05 | **Concluído em código** — Importar e exportar JSON em Configurações (D) | Nova seção "Fichas (JSON)" em `ConfigScreen`: importar (colar o JSON ou escolher arquivo) com a mesma validação de `AppModel.importFicha`; exportar escolhendo a ficha, com "Copiar" e "Salvar arquivo". Retirar da tela Heróis o cartão de importação e o botão "Copiar JSON". Sem mudar o formato do JSON nem o `AppModel` | A tela Heróis fica só com criação e biblioteca; importar e exportar funcionam por Configurações. **Feito**: `FichasJsonSection.kt` (importar colando ou escolhendo arquivo; exportar uma ficha com "Copiar JSON" e "Salvar arquivo") usado em `ConfigScreen`; cartão de importação e botão "Copiar JSON" removidos de `CharactersScreen`; `AppModel` e o formato do JSON intactos |
| PT-06 | **Concluído em código** — Layout da biblioteca de fichas (E) | Com o "Copiar JSON" fora, reorganizar os botões restantes (Ver ficha, Editar escolhas, Duplicar, Arquivar) em linha quebrável ou menu "Mais"; mensagens de estado no lugar certo; conferir outras linhas de botões em tela estreita | Nenhum rótulo quebrado em tela de 360 dp. **Feito**: novo `LinhaQuebravel` (`FlowRow`) substitui as 7 linhas de botões da tela Heróis (foto, história, editar/duplicar/arquivar, arquivadas, cartão da história); a mensagem de estado da biblioteca vai para o topo da biblioteca; linha de velocidade de `TimeSimulationScreen` também quebra. Confirmar em aparelho de 360 dp |
| PT-07 | **Testes escritos; execução pendente** — Validação | Testes das alterações, `ktlintCheck`, suíte e conferência em aparelho (chave → modelos → nova campanha → ficha → história → importar/exportar) | Resultado do CI e do aparelho registrado. **Feito até aqui**: `RespostaProvedorPt01Test`, `ModelosAutomaticosPt02Test`, `CampanhaPorSelecaoPt03Test` (módulo `ai`) e `CriacaoChipsPt04Test` (módulo `app`, JVM), todos escritos sem rodar. **Falta**: CI (`ktlintCheck`, suíte, E2E) e conferência em aparelho, com o resultado registrado em `docs/relatorios/` |

## Decisões que dependem do autor (assunções até responder)

- **Decidido pelo autor (14/11/2026)**: cadeia com todos os modelos gratuitos que respondem texto (sem teto de 6 nem de 16 sondas), exibindo só a contagem. Risco conhecido: a sondagem faz uma chamada por candidato e pode bater no limite do plano gratuito; modelos que falharem na sondagem ficam fora até a próxima renovação.
- **Decidido pelo autor (14/11/2026)**: história gerada só é salva depois da confirmação do jogador; exportar fichas em Configurações uma por vez. Antes eram assunções.
- **Decidido pelo autor (14/11/2026)**: as 4 predefinições de campanha ficam (aventura clássica, terror sombrio, intriga política, exploração leve).

## Assunções do PT-02

- ASSUMINDO: a renovação em segundo plano (24 h ou falha total) reaplica a configuração com `applySettings`, o que recria o controlador de jogo, como já ocorre ao salvar a configuração; se isso atrapalhar uma jogada em andamento, restringir a renovação a fora de turno.
- ASSUMINDO: escolha manual em "Avançado" vale até a próxima renovação automática (grava o horário de agora).
- ASSUMINDO: a sondagem usa "Responda apenas OK." com 256 tokens (mesma chamada do "Testar conexão"); chave inválida ou falta de internet interrompem a sondagem.

## Assunções do PT-03

- ASSUMINDO: sistema e mundo (Mythralis, reinos, mundo próprio, campanha anterior) continuam sendo resolvidos pelo interpretador e pelo passo de revisão do `NewCampaignFlow`, sem chips próprios de sistema na primeira tela (o fluxo não expõe lista de sistemas antes da interpretação).
- ASSUMINDO: "modo" = Narrativo ou Tático (combate detalhado); ritmo e modo entram na descrição como linhas de texto, sem campo novo no motor. É preciso ao menos uma predefinição escolhida para continuar.

## Assunções do PT-04

- ASSUMINDO: aparência e personalidade escolhidas não são gravadas na ficha; servem de base do pedido da IA (guardadas só na sessão da tela). A história aprovada continua em `approvedLore` (40 a 4000 caracteres ao salvar, regra do `approveLore`, que só vale no salvamento da história gerada).
- ASSUMINDO: a causa do "Criar ficha" desabilitado era `loreOk` (aparência+personalidade com menos de 40 caracteres e história vazia); a regra saiu do fluxo de criação. Confirmar em aparelho.

## Assunções do PT-05

- ASSUMINDO: a exportação lista só as fichas ativas (as arquivadas voltam à biblioteca antes de exportar); nome do arquivo `ficha-<nome>.json`.

## Pendente de investigação

- Na última aba da criação, "Criar ficha" aparece desabilitado com aparência e personalidade preenchidas e história vazia; conferir qual condição (`loreOk`, nome, idade, passos incompletos) bloqueia. O PT-04 remove a regra de 40–4000, mas a causa exata deve ser confirmada.
