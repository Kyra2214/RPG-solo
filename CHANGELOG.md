# Changelog — implementação code-first

## 2026-10-19 — correções de consistência nas fases 1–4

### Motor de regras e continuidade
- Testes opostos comparam primeiro o nível do resultado (falha/sucesso/crítico) e depois a margem; empate absoluto favorece o defensor. Margens de testes com dificuldades ou escalas diferentes deixam de ser comparadas isoladamente.
- Adicionada a API `CampaignSession.checkWithFortuneSpendingResource`: paga um recurso validado da ficha antes de aplicar vantagem/desvantagem e restaura o log se a rolagem falhar. A integração com a interface e a declaração do custo por pacote permanecem pendentes.
- O fluxo principal de jogada agora respeita `fortuneMode` declarado no teste do pacote, persistindo as duas rolagens e usando apenas o resultado selecionado. Foi adicionado teste de regressão para essa integração; ainda não executado.
- A rolagem descartada de vantagem/desvantagem agora também é preservada no registro da jogada e exibida no chat como 'Não escolhido'; apenas o resultado escolhido é enviado ao contexto de narração. A resolução avançada e a persistência de gasto de recurso foram tornadas transacionais no nível da sessão. Foram adicionados testes para vantagem/desvantagem em testes opostos e de grupo; testes não executados.
- Testes opostos deixam de comparar margens de escalas incompatíveis: a margem só desempata quando comparação, quantidade/lados de dados e dificuldade coincidem; caso contrário, empate na qualidade favorece o defensor. Foi adicionada regressão para dificuldades diferentes; não executada.
- Corrigida uma lacuna na implementação: testes opostos e de grupo agora aplicam a política de vantagem/desvantagem de cada participante por meio da mesma resolução atômica usada nos testes individuais, persistindo todas as rolagens necessárias; consequências continuam usando apenas os resultados escolhidos. Não executado.
- Falha na validação/resolução do teste agora retorna `PlayResult.Failed` e tenta restaurar a marca anterior, em vez de escapar como exceção do fluxo de jogada. Foi adicionado teste para especificação inválida; não executado.
- `EngineConfig` recusa pré-requisitos de perícia que referenciem atributos inexistentes, em vez de aceitar configuração mecanicamente inconsistente.
- A linha do tempo só revela uma pista pública na transição de relógio incompleto para completo; eventos posteriores que repetem o estado cheio não duplicam a pista. Nomes internos e contagem continuam privados. Foi acrescentada uma regressão para o evento repetido; os testes continuam sem execução.

### Estado da validação
- Foram atualizados testes unitários para os casos afetados, mas eles **não foram executados**. Nenhum build, teste, lint ou CI foi iniciado; validação geral segue adiada conforme a orientação code-first.

## 2026-10-19 — integração de narração progressiva

### IA e fluxo de jogada
- Transporte HTTP ganhou leitura linha a linha para respostas SSE, com implementação real no transporte de produção e fallback compatível com transportes simples.
- Provedores OpenAI-compatible e Anthropic implementam leitura incremental de conteúdo.
- O streaming foi propagado pelos decorators de retry, limite de requisições e contagem de uso.
- O loop extrai somente o campo `narrative` do JSON transmitido, sem exibir propostas ou outros campos estruturados durante a geração.
- A tela Jogar mostra a narração parcial enquanto o pedido está em andamento e limpa o rascunho ao terminar ou falhar.
- Retentativas de streaming não são repetidas depois do primeiro fragmento, evitando concatenar duas respostas parciais.

### Mecânica e acessibilidade
- Alguns bônus e penalidades numéricos já explícitos em traços do pacote próprio agora são aplicados pelo motor (reflexos, frágil, mão pesada, graça feérica e desconfiança/emoção confusa).
- Combate aplica resistência/vulnerabilidade numérica a dano antes de registrar o dano resolvido; Pele dura, Frágil, Tenacidade anã e Corpo de pedra usam o escopo explícito `resistencia_dano` do pacote.
- Dados animados têm descrições semânticas para TalkBack, sem revelar o resultado antes de o jogador revelar a rolagem.
- Consumível de cura não é gasto se o recurso-alvo já estiver cheio; a validação acontece antes do lote atômico de cura/consumo/avanço de turno.
- Testes por pontos agora diferenciam 0 e 1 ponto: zero conserva a face mínima de sucesso; cada ponto positivo acrescenta uma face até o teto do dado. Tutorial, fórmula e simulador foram alinhados; a tabela empírica deve ser recalculada na validação posterior.
- Se um endpoint rejeitar explicitamente o modo SSE (HTTP 400/422), o provedor tenta a resposta completa padrão para preservar compatibilidade.
- Preferência de escala global da fonte (85%–140%) adicionada à Configuração, persistida no aparelho e aplicada à interface via densidade Compose.
- A tela inicial vazia agora orienta explicitamente a sequência de primeiro uso: configurar/testar IA, criar ou escolher herói e criar campanha.

### Estado da validação
- Essas alterações são somente de código e leitura estática. **Não foram executados builds, testes, lint ou CI**. Compatibilidade SSE por provedor, Compose e comportamento em aparelho continuam pendentes de validação do usuário.


## 2026-10-18 — consolidação da branch `implementation/code-first-combat`

### Motor e combate
- Ataques usam a comparação, dados, atributos e perfil de dano declarados pelo pacote.
- Bônus de condição afetam o modificador/limiar de acerto; não alteram indevidamente a quantidade de dados.
- Equipamento, traços e adaptações aprovadas participam dos bônus de ataque, dano e defesa.
- Adicionadas ações Defender e Fugir, seleção explícita de adversários e limpeza de postura defensiva.
- Dano por condição e avanço de turnos permanecem no motor; turno pula combatente morto por condição ao fechar a rodada.
- Relógios de agenda e chave direcional de relações têm modelos explícitos no módulo engine.

### Estado, continuidade e segurança
- Restauração de backup valida o estado reconstruído antes de criar a campanha.
- Validação adicional cobre condições, mapa, dossiês de NPC, relações e relógios.
- Limites de tamanho/quantidade adicionados a dossiês de NPC e adaptações.
- Importação de pacotes restaura arquivos privados, respeita dependências e limita ZIP a 20 MiB.
- Preferências da campanha e lista de adversários preparados são persistidas por campanha.

### Interface
- Biblioteca de fichas permite consultar detalhes, duplicar, arquivar/restaurar, excluir fichas arquivadas e copiar JSON.
- Campanhas exibem linha do tempo, missões encerradas, histórico de XP/níveis e atitudes de NPCs em palavras.
- Combate mostra ações disponíveis de acordo com o pacote.
- Erros de jogada aparecem na interface; a entrada é preservada quando uma ação é recusada para permitir nova tentativa.
- Preferências de narração e limites de conteúdo ficam salvos localmente.
- A tela inicial vazia oferece atalhos para configurar a IA e iniciar uma campanha.
- O diário de campanha mostra histórico de XP/nível derivado do log e atitudes registradas dos NPCs em palavras.
- O avanço de rodada ignora um combatente que morre por efeito de condição no momento em que o turno dele começaria.
- O prompt do mestre orienta avanço parcimonioso de relógios de agenda sem expor nomes ou contagens.
- Condições temporizadas exigem recurso de referência válido, para que a duração sempre possa expirar corretamente.

### Validação desta etapa
- **Não foram executados builds, testes, lint ou CI**, conforme a orientação de concluir o código antes das validações.
- A implementação ainda não deve ser considerada validada até compilar e passar pela bateria de verificações em aparelho/emulador.

---

## 2026-10-18 — próxima etapa

- Configuração: botão **Testar conexão** faz uma chamada mínima ao provedor informado, fora da thread principal, sem salvar a configuração nem alterar a campanha.
- Equilíbrio: adicionado `tools/simulate_checks.py` com semente reproduzível e `docs/MECANICA_DADOS.md` com a tabela de chances por pontos e dificuldade.
- Jogar: atalhos locais para **Ver ficha**, **Inventário** e **Descansar**, sem chamada de IA e bloqueados enquanto há uma rolagem pendente.
- Rolagens: o cartão agora identifica “faces de sucesso” nos testes por pontos, descreve o alvo do d20 e mostra a margem como “passou por” ou “faltou”.
- Motor: testes de luta completa cobrem ataque, dano, morte, encerramento automático do combate e condição com dano por rodada e expiração.
- Combate: a aba Jogar agora mostra alvos vivos e permite atacar somente no turno do jogador; iniciativa, defesa, rolagem e dano continuam sob controle do motor, com autosave.
- Combate: no turno inimigo, a interface exige **Resolver turno inimigo**; o ataque contra o jogador é calculado e salvo antes de avançar a iniciativa.
- Equipamentos: itens equipáveis agora são filtrados pelo catálogo do pacote, aparecem com slot e quantidade, e podem ser equipados ou desequipados pela interface usando comandos validados.

## 2026-10-18

- Interface: o formato visual dos dados agora acompanha o tipo do dado (d4, d6, d8, d10, d12 e d20), sem alterar o resultado decidido pelo motor.
- Jogar: o estado de rolagens já reveladas saiu da composição da tela e passou ao `PlayController`, preservando a narração ao trocar de aba ou recriar a Activity; o comportamento ganhou teste unitário.

## 2026-10-17 (links de consulta)

- Sistema próprio + links de consulta: 3DeT e D&D passam a ser **só link** na criação de personagem (aba Heróis e aba Sistemas). Removidos da tela: selo e texto de licença, origem do pacote e a frase sobre inserir PDFs. `fontes.txt` ficou só com 3DeT e D&D (Fate e Pathfinder saíram).
- Plano: Fase 4B reduzida a links; importação de PDF e trabalho de licença (4A.5, 4B.2, 4B.3) cancelados. `DocumentIndex`/`PdfTextExtractor` seguem no módulo `rules` sem uso no app.

## 2026-10-17

- Backup local: adicionar exportação e restauração de JSON por seletor de arquivos Android (SAF), mantendo a alternativa de copiar e colar texto. A chave da API não integra o backup.
- Campanhas: permitir continuar uma campanha salva pela lista da aba Campanhas. A troca reabre o log de eventos e restaura capítulos, missões e o contexto de personagem/pacote em vez de reutilizar o da campanha anterior.
- Room: persistir player, ficha, sistema, edição e pacote em `campanha_meta`; adicionar migração versionada 2 → 3 sem apagar os dados existentes.
- Fase 2 — perícias: validar ids, níveis de 0 a 5, pré-requisitos e orçamento total do pacote; impedir que uma campanha inicie com uma ficha inválida. A decisão sobre a diferença mecânica entre 0 e 1 ponto continua pendente.
- Documentação: substituir estados históricos obsoletos do README e manter este changelog para registrar as entregas.
- Verificação: `./gradlew test :app:assembleDebug` e os testes do módulo de regras passaram com JDK 17 e Android SDK 35. A execução em aparelho/emulador e os testes instrumentados de migração Room continuam necessários.

## 2026-10-19 — fechamento code-first: motor, continuidade e cache

- Motor/regras: adicionada API para rolagens com vantagem/desvantagem, testes opostos e testes em grupo; `testes.txt` agora aceita a coluna final opcional `NORMAL`, `ADVANTAGE` ou `DISADVANTAGE`, propagada por `RulePackage` até o motor. As versões de `CampaignSession` salvam as rolagens no log persistente; rolagens compostas fazem rollback se uma especificação falhar no meio.
- Progressão: o pacote pode declarar `pontos_por_nivel`; o motor concede saldo ao subir de nível e valida aumentos de atributo/perícia contra os limites gravados na campanha. A aba Campanhas permite gastar um ponto por clique. O motor valida pré-requisitos de perícia, teto e saldo sem aplicar alterações parciais. O sistema próprio agora concede 1 ponto por nível.
- Continuidade: criada linha do tempo derivada dos eventos persistidos para descobertas, locais conhecidos, deslocamentos, XP/nível, condições do jogador e eventos de combate com participação do jogador. Segredos de NPC e alterações ocultas não são exibidos. Relógios aceitam `publicClue` opcional, revelada apenas quando o relógio completa, sem nome interno ou contagem.
- Interface: a aba Campanhas passou a mostrar a linha do tempo automática, separada das anotações manuais. A seção de evolução permite gastar pontos em atributos/perícias com rótulos TalkBack específicos. A tela Jogar agora mantém o assistente de primeiro uso até existir provedor, ficha disponível e campanha ativa.
- IA: a API oficial Anthropic recebe `cache_control: ephemeral` no prefixo de sistema por padrão; endpoints compatíveis não presumem suporte. O cache ainda precisa ser validado em rede real e pode ser desativado pela configuração.
- Uso/cache: a resposta Anthropic captura tokens lidos/criados no cache também via SSE; `UsageTracker` separa as contagens, permite estimar custos com tarifas de cache distintas e Config mostra as métricas. A confirmação de cache-hit real ainda depende de chamada de rede. A Config agora permite ativar/desativar o cache e persiste a escolha com as configurações do provedor. Também aceita preços por milhão para entrada/saída e tarifas de cache separadas, exibindo custo estimado na moeda informada.
- Testes: adicionados casos unitários para essas APIs e para evitar exposição de dados privados na linha do tempo. **Não foram executados**, conforme a etapa code-first.
