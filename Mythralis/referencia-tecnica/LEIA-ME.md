# Referência técnica (não faz parte do conteúdo editorial)

Estes dois arquivos vieram da branch `world/aeltharyn-reino-das-florestas` do RPG Solo e foram **removidos do código do jogo** para este projeto poder focar no mundo:

- `AeltharynWorld.kt`: catálogo tipado de Aeltharyn (10 cidades, 16 povos e linhagens, 51 figuras, 14 rotas, 7 relógios), `validate()`, `sceneContext()` e `install()`.
- `AeltharynWorldTest.kt`: testes desse catálogo.

**Não compilam aqui** (dependem do módulo `engine` do RPG Solo: `CampaignSession`, `Place`, `NpcDossier`...). Servem como referência dos IDs e dos dados estruturados para a implementação futura. Ver `../plano/INTEGRACAO_FUTURA_RPG_SOLO.md`, seção 2.

Os dados no `.kt` e os documentos em `reinos/` devem permanecer coerentes; qualquer mudança editorial precisa ser refletida aqui antes da integração.
