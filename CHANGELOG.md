# Changelog

## 2026-10-17

- Backup local: adicionar exportação e restauração de JSON por seletor de arquivos Android (SAF), mantendo a alternativa de copiar e colar texto. A chave da API não integra o backup.
- Campanhas: permitir continuar uma campanha salva pela lista da aba Campanhas. A troca reabre o log de eventos e restaura capítulos, missões e o contexto de personagem/pacote em vez de reutilizar o da campanha anterior.
- Room: persistir player, ficha, sistema, edição e pacote em `campanha_meta`; adicionar migração versionada 2 → 3 sem apagar os dados existentes.
- Documentação: substituir estados históricos obsoletos do README e manter este changelog para registrar as entregas.
- Verificação: `./gradlew test :app:assembleDebug` passou com JDK 17 e Android SDK 35. A execução em aparelho/emulador e os testes instrumentados de migração Room continuam necessários.
