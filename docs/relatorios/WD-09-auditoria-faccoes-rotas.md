# WD-09 — auditoria de facções, rotas e conflitos

Data: 14/11/2026. Fonte: `rules/src/main/resources/mundos/mythralis/reinos/*/dados/` (`faccao.txt`, `rota.txt`; molde exportado; nada foi alterado). Total: 46 cidades, 64 facções.

| Reino | Facções | Rivalidades declaradas | Rivalidades não recíprocas | Referências de facção inválidas | Rotas | Rotas com ponta inexistente | Pares de rota repetidos |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 Aeltharyn | 10 | 0 | 0 | 0 | 17 | 0 | 1 |
| 2 Coroas de Pedra | 14 | 12 | 0 | 0 | 13 | 0 | 1 |
| 3 Véu dos Malditos | 11 | 10 | 0 | 0 | 10 | 0 | 0 |
| 4 Vale Palancar | 14 | 12 | 0 | 0 | 6 | 0 | 0 |
| 5 Azeroth | 15 | 16 | 0 | 0 | 9 | 0 | 0 |

**Resultado:** toda aliança e rivalidade aponta para facção existente; as rivalidades são recíprocas; toda rota liga cidades existentes. Estados das rotas: Aeltharyn 4 documentadas, 12 com ressalva e 1 desconhecida; os demais reinos têm todas `desconhecida` (meio e duração `?`, D-63/D-153).

**Observações (sem alteração de cânone):**
- Aeltharyn: Thalanor–Elarion tem duas rotas legítimas (rio, 3, e estrada costeira, 5): meios diferentes, não é duplicata.
- Coroas de Pedra: `route.concilio-altavento-pedra-ata` e `route.altavento-pedra-ata` ligam o mesmo par, ambas com meio e duração `?`; decisão do autor (14/11/2026): **manter as duas**, não são redundantes (`concilio-altavento-pedra-ata` é trilha a pé pelos passos para delegações; `altavento-pedra-ata` é aéreo, mensageiros a voo). Conferido em `rota.txt`; nada removido. Estado: ✅ fechado.
- Aeltharyn não declara rivalidades entre facções (0), por registro do cânone; não foi inventado conflito.
