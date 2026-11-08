# Relatório R2-8.1 — Revisão final das Coroas de Pedra (05/11/2026)

**Escopo:** as 11 portas de `MODELO_DE_REINO.md` §6 contra os dados atuais (`reinos/02-.../dados/`, CSVs e docs 01 a 15). Estado: **[PROPOSTO]**, para aprovação do autor (D-61). Nenhum lore alterado; uma correção de texto (item acidental A1).

## a) Portas do checklist

| # | Porta | Resultado | Evidência |
|---|---|---|---|
| 1 | 10 cidades com ficha e função coerente | **ok** | 10 cidades no registro; todas com resumo, ≥ 1 rota e ≥ 5 recursos; função coerente com a geografia (M-04) conferida em R2-1.x |
| 2 | 8 a 12 principais (≥ 2 heróis, ≥ 2 generais), agenda e pressão distintas | **ok** | 12 principais; 2 heróis vivos (Maur, Vireo), 2 generais (Gundrik, Thaurak); 12 agendas e 12 pressões distintas, 0 `?` |
| 3 | Rotas com tempo ou desconhecidas; sem rota inventada | **ok, com ressalva** | 13 rotas, todas `desconhecida` (duração `?`), nenhuma inventada; 5 cidades com 1 só rota (V-23 aviso) |
| 4 | Recursos e dependências; nenhuma cidade isolada | **ok** | 5 a 8 recursos por cidade; 0 cidade sem rota ou recurso |
| 5 | Povos alinhados ao catálogo | **ok** | 12 nativos, todos com ID do catálogo; 0 ID fora do catálogo em `12-competencias.csv`; validador sem erro |
| 6 | Facções com objetivo, métodos, aliados, rivais e conflito concreto | **ok, com lacuna explícita** | 14/14 com objetivo, métodos e tensão; aliados `?` em 1 (Conselho de Sangue) e rivais `?` em 4 (Rede dos Passos Altos, Casa das Comportas, Coro das Facetas, Arquivistas); 0 aliado e rival ao mesmo tempo; 0 aliança ou rivalidade assimétrica → D-61 |
| 7 | Relógios cruzados, sem avanço automático | **ok** | 7 relógios, progresso inicial 1 a 3 de 6, 0 entidade inexistente, todo avanço por gatilho observável (`12-relogios.csv`) |
| 8 | Mistérios sem solução predefinida | **ok** | 6 mistérios, 4 hipóteses abertas cada, "a verdade não pode ser" e evidência cumulativa |
| 9 | Contradições intencionais × acidentais | **ok** | ver b) |
| 10 | IDs únicos e todos existem | **ok** | validador: 0 erro; 0 ID duplicado; 0 ID citado sem definição (F6, F7); 0 colisão com Aeltharyn e com o `.kt` |
| 11 | Fechamento com limites explícitos | **pendente** | é o doc 11 do reino 2, entregável do R2-8.2 |

## b) Contradições intencionais × acidentais

**Intencionais** (registrar no doc 11):
1. Mistérios MP-01 a MP-06 e os fatos de relógio: ambiguidade deliberada, sem hipótese escolhida (`13-misterios-limites`).
2. Rotas só com duração `desconhecida` e a ausência de rota Coroas–Aeltharyn (doc 07 §4, contrato de fronteira §3.3).
3. Maur só fala em particular; heróis lendários **mortos** (13) sem ressurreição (doc 06 §9.3).
4. 7 exceções do validador (`excecoes.txt`): 6 eventos que citam ligações do doc 07 sem dois locais existentes e a rota Altavento–Pedra-Ata, que repete o par da delegação ao Concílio (natureza diferente).

**Acidentais** (achadas e tratadas):
- **A1 (corrigida agora):** `CONTRATO_DE_FRONTEIRA_AELTHARYN_COROAS.md` §2 dizia "anões, gnomos e os demais 12 nativos", o que somaria 14; os nativos são 12 no total, com anões e gnomos entre eles (M-05, D-24, doc 10 §1, `povo_no_reino.txt`). Texto ajustado; nenhum fato mudou.
- **A2 (já corrigida em R2-7.1):** 5 IDs de canal colidiam com Aeltharyn; os 11 canais viraram `canal.coroas-*`.
- Contagens conferidas: 10 cidades, 20 assentamentos, 45 pessoas (12 principais, 20 lideranças, 13 heróis lendários), 14 facções, 7 relógios, 13 eventos, 41 fatos, 90 sementes, 96 resumos: iguais nos dados e nos documentos. 0 NPC com cidade ou facção inexistente.

**Lacunas explícitas, não erros:** rivais `?` em 4 facções e aliados `?` em 1; duração de toda rota; origem do combustível, minério e carvão; D-38 a D-60 aguardando o autor.

## c) Pendências para o autor

- **D-61:** aceitar as 5 lacunas de aliado e rival como limite do reino, ou nomear aliados e rivais (o que cria lore novo e exige registro).
- Decisões D-38 a D-50, D-54 a D-60 continuam aguardando.

## d) Veredito

Portas 1 a 10 marcadas (3 e 6 com ressalva ou lacuna explícita); **11 pendente**, por ser o R2-8.2. O reino passa a "fechado 3" só no R2-8.2, com aprovação do autor.
