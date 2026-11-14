# Auditoria editorial de raças (CG-2.8) — 14/11/2026

Escopo: 50 raças, 50 sub-raças e 20 híbridos de `personagem-catalogo.json` (sistema-proprio). Leitura estática por script; nada executado no app.

## Conferido, sem problema
- 50 ids de raça únicos; `taxonomia-criacao.json` cobre exatamente as 50 raças (sem faltas nem sobras).
- 50 sub-raças, todas com `parentRaceId` válido (25 raças-mãe com sub-raças).
- 20 híbridos, todos com `raceIds` existentes.
- Todas as raças e sub-raças têm `summary`, `lore` e um efeito mecânico `+1`; todos os escopos usados (16) existem como perícia/traço do pacote.
- Nenhum `summary` ou `lore` repetido entre raças.

## Achados (não corrigidos: exigem texto novo do autor)
1. **`lore` igual a `summary` nas 50 raças e nas 50 sub-raças.** O \"Ver detalhes\" (CG-2.5) mostra o mesmo texto duas vezes. Lore longa é conteúdo editorial a escrever.
2. **Nenhuma raça nem sub-raça declara `benefits` ou `drawbacks`.** O painel e o comparador (CG-2.6) leem esses campos e ficam só com o bônus `+1` do catálogo. Só há benefícios/desvantagens nos campos de outras partes do catálogo.
3. **Nomes de exibição diferem dos ids** (ex.: `tiefling` = Nascido do Crepúsculo, `goliath` = Altaneiro). Intencional (nomes próprios, sem termos de terceiros); os ids ficam para não quebrar fichas salvas.

## Decisão pendente para o autor
Escrever `lore`, `benefits` e `drawbacks` das 50 raças (e sub-raças) é trabalho de conteúdo; sem texto aprovado, nada foi inventado.
