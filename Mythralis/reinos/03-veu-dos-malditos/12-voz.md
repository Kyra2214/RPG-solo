# 12 — Voz e ganchos de diálogo (R3-4.4)

**Estado:** rascunho de R3-4.4 para aprovação do autor (D-86) · **Data:** 2026-11-06
**Etiqueta:** **[PROPOSTO]**; **não cria fato, NPC ou relação novos**. Dados em `12-voz.csv` (`npc_id, tom, frases_modelo, nunca_diz, base`), no molde dos reinos 1 e 2.

## 1. Regras

- 12 principais (N-01 a N-12): `tom` a partir da personalidade da ficha (R3-4.1); 2 a 3 `frases_modelo` (separadas por `|`) como **instrução de tom, não como fala canônica** (ASSUMINDO); `nunca_diz` reúne o que a ficha guarda como suspeita ou desconhecido e o que ultrapassa o limite do cargo.
- Frases só repetem o que já é documentado (cargo, limite, pacto, agenda); nenhuma revela suspeita ou desconhecido das fichas.
- Morvan fala pouco e baixo; Cerrane não vota; Krell só tem rumor sobre o que há além da névoa e não liga o posto a Brumafunda (contrato de fronteira).

## 2. Conferências

| Verificação | Resultado |
|---|---|
| Principais com voz | 12 de 12 |
| Frases por principal | 2 a 3 |
| Campo vazio | 0 |
| Frase que revela suspeita ou desconhecido | 0 (lido uma a uma) |
| ID fora do registro mestre | 0 |

## 3. Decisão pendente

**D-86:** aprovar tom, frases e o que nunca dizem. Opções: **A.** aprovar; **B.** trocar tom ou frase de alguém (diga quem). **Recomendação: A.**

---

# Fechamento da Fase 4 (F4): testes completos

Suíte local disponível (o validador `plano/ferramentas/validar_reino.py` exige a pasta `dados/` do formato do reino 2, que o reino 3 só gera na Fase 7; não se aplica ainda). Rodada de verificações locais sobre os 9 CSV e os `.md` do reino 3:

| Verificação | Resultado |
|---|---|
| Integridade dos 9 CSV (colunas por linha, 276 linhas de dados) | OK; 0 campo vazio nos 8 de F4 e nos de F1 a F3, exceto o registro mestre (abaixo) |
| IDs de F4 (`fichas`, `competencias`, `relacoes`, `conhecimento`, `voz`) no registro mestre | 0 fora |
| Voz = fichas (12 de 12); 2 a 3 frases por principal | OK |
| Competências no catálogo (raça, classe, profissão); sub-raça com raça-mãe certa | OK |
| Alianças e rivalidades no mesmo par; relação sem fonte | 0; 0 |
| Registro mestre | 144 células vazias, **por desenho**: `id_catalogo` (49, o reino 3 não tem IDs de catálogo ainda) e campos de pessoa nas 19 linhas de facção e cidade; verificado, não é regressão |
| Referências `.md` | 5 apontam para arquivos que existem com caminho abreviado (`...`) ou em `mundo/`; 0 inválida de fato |
| Regressão em Aeltharyn e Coroas de Pedra | 0 arquivo alterado em F4 |
| Contrato de fronteira | nenhum NPC ou relação com Aeltharyn ou as Coroas |
