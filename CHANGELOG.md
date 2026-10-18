# Changelog — implementação code-first

## 2026-10-18 — branch `implementation/code-first-combat`

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

### Validação
- **Não foram executados builds, testes, lint ou CI**, conforme a orientação de concluir o código antes das validações.
- A implementação ainda não deve ser considerada validada até compilar e passar pela bateria de verificações em aparelho/emulador.
