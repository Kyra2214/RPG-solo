# Changelog — implementação code-first

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
- Dados animados têm descrições semânticas para TalkBack, sem revelar o resultado antes de o jogador revelar a rolagem.

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
