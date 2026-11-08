# Relatório R5-8.1 — Revisão final de Azeroth (07/11/2026)

**Escopo:** as 11 portas de `MODELO_DE_REINO.md` §6 contra os dados atuais (`reinos/05-azeroth/`, CSVs, `dados/` e docs 01 a 15). Estado: **[PROPOSTO]**, para aprovação do autor (D-173). Nenhum lore alterado; nenhuma contradição acidental nova.

## a) Portas do checklist

| # | Porta | Resultado | Evidência |
|---|---|---|---|
| 1 | 10 cidades com ficha e função coerente | **ok** | 10 lugares (D-141, ASSUMINDO), 10 de 10 com resumo, função, recursos, demografia e linha em `12-assentamentos.csv`; 3 capitais de coroa, Meiovau comum |
| 2 | 8 a 12 principais (≥ 2 heróis, ≥ 2 generais), agenda e pressão distintas | **ok** | 12 principais; 2 heróis vivos (Garvane, Nerella), 2 comandantes (Solenne, Dorgan); 12 agendas e 12 pressões distintas; 0 campo vazio ou `?` nas 12 fichas |
| 3 | Rotas com tempo ou desconhecidas; sem rota inventada | **ok, com ressalva** | 14 ligações (10 documentadas, 4 sugeridas por dependência), todas `desconhecida` (D-63, D-153); nenhuma rota com Aeltharyn, Véu, Coroas ou Vale |
| 4 | Recursos e dependências; nenhuma cidade isolada | **ok** | 24 dependências (17 críticas, 7 de apoio), cada lugar com ao menos uma crítica; 4 fluxos qualitativos do lado de cá (docs 08 e 09) |
| 5 | Povos alinhados ao catálogo | **ok** | 9 nativos, 8 sub-raças e 25 forasteiros (`ALINHAMENTO_AZEROTH.md`); 38 de 38 pessoas com raça, sub-raça (só no doc 10 §2), classe e profissão; 0 ID fora do catálogo |
| 6 | Facções com objetivo, métodos, aliados, rivais e conflito concreto | **ok, com lacuna explícita** | 15/15 com objetivo, métodos e tensão; 0 aliado assimétrico, 0 rival assimétrico, 0 aliado e rival ao mesmo tempo; 1 rival `desconhecido` (Pilotos da Enseada, F-07, motivo no doc 04 §10) |
| 7 | Relógios cruzados, sem avanço automático | **ok** | 10 relógios, progresso inicial 2/6 ou 3/6, gatilho observável em 10 de 10; 12 eventos-tipo ligados a relógio ou gatilho; 0 entidade inexistente; estação nunca é prazo |
| 8 | Mistérios sem solução predefinida | **ok** | 10 mistérios (MA-01 a MA-10), 4 hipóteses abertas cada, "a verdade não pode ser" e evidência para resolver em todos |
| 9 | Contradições intencionais × acidentais | **ok** | ver b) |
| 10 | IDs únicos e todos existem | **ok** | 63 entidades (10 + 15 + 38), 0 duplicado, 0 colisão com os reinos 1 a 4; 0 ID de cidade, npc ou facção citado nos CSV sem registro; 0 clock, event ou fact citado sem definição; seeds citadas por prefixo de lugar (`seed.<lugar>`), todas as 30 definidas; validador 0 erro |
| 11 | Fechamento com limites explícitos | **pendente** | é o doc 11 do reino 5, entregável do R5-8.2 |

## b) Contradições intencionais × acidentais

**Intencionais** (registrar no doc 11; base em `13-conferencia-cruzada.md`, CI-01 a CI-12):
1. Mistérios MA-01 a MA-10 e os 10 relógios: ambiguidade deliberada, sem hipótese escolhida.
2. Toda duração `desconhecida` (D-63) e ausência de rota do reino com Aeltharyn, Véu, Coroas e Vale; só As Costas e As Fugas do Véu, do lado de cá (doc 07 §4; contrato de fronteira).
3. Três coroas rivais sem precedência decidida (L-01 a L-10); a Mesa de Meiovau arbitra e não decide a precedência (CI-02); chave copiada `desconhecida` (CI-03).
4. Concharia e Estrelado sem chefe (`lideranca_id` `desconhecido`) (CI-04).
5. Crime grave, exílio e pena de morte não fixados; sucessão por coroa com desfecho `desconhecido` (CI-05, CI-06).
6. 13 acúmulos de cargo, intencionais e propostos (CI-07).
7. 1 rival `desconhecido` (CI-08).
8. Origem e sentido de "Azeroth" `desconhecidos`, com duas versões (CI-09).
9. Lacunas de papel por povo e 17 forasteiros sem lugar fixo (CI-10, CI-11).
10. 8 heróis lendários **falecidos**, sem ressurreição; a lenda existe, o feito é `desconhecido`.
11. Pontos abertos do contrato de fronteira P-2 (qual pântano) e P-3 (fronteira com o Véu na foz) (CI-12).
12. Fonte `proposto` nas entidades (V-06) e 7 avisos esperados do validador (V-06 ×3, V-23, V-26, V-31, V-42).

**Acidentais** (achadas e tratadas): nenhuma nova. O único achado acidental do reino (nove da tríade fora da lista declarada de líderes, R5-2.4) já foi corrigido e registrado em D-152.

**Conferido nesta revisão, sem achado novo:** contagens iguais nos dados (10 lugares; 63 entidades = 10 + 15 + 38; 38 pessoas = 12 principais + 18 sem ficha + 8 lendários; 15 facções; 10 relógios; 12 eventos; 30 sementes (10 P, 10 M, 10 G); 44 fatos; 73 resumos; 7 cenas; 91 relações; 88 linhas de conhecimento; 11 canais; 25 de reputação; 38 competências; 12 vozes; 10 faixas de demografia); 0 NPC falecido em `entra_ids` de cena, semente, evento ou relógio; `PROTOTIPO_MINIMO.md` §13 com contexto B sem fato restrito ou secreto.

## c) Verificações

Somente leitura dos dados e conferência por script (contagens, simetria de facções, IDs citados, NPC falecido). Nenhum arquivo de dados, script ou lore foi alterado; testes completos só ao fim da Fase 8.

## d) Pendências para o autor

- **D-172:** protótipo mínimo (nenhuma sessão executada).
- **Lacunas explícitas** a aceitar no doc 11: rival `desconhecido` de F-07, liderança de Concharia e Estrelado, sucessões, crime grave, origem do nome, P-2 e P-3.
- **D-67** (Véu, nomes) segue com o autor.

## e) Veredito

Portas 1 a 10 marcadas (3 com ressalva, 6 com lacuna explícita); **11 pendente**, por ser o R5-8.2. O reino passa a "fechado 6" só no R5-8.2, com aprovação do autor.
