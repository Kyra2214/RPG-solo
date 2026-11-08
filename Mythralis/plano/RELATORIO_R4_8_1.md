# Relatório R4-8.1 — Revisão final do Vale Palancar (07/11/2026)

**Escopo:** as 11 portas de `MODELO_DE_REINO.md` §6 contra os dados atuais (`reinos/04-vale-palancar/dados/`, CSVs e docs 01 a 15). Estado: **[PROPOSTO]**, para aprovação do autor (D-136). Nenhum lore alterado; nenhuma contradição acidental nova.

## a) Portas do checklist

| # | Porta | Resultado | Evidência |
|---|---|---|---|
| 1 | 10 cidades com ficha e função coerente | **ok, com ressalva** | 8 lugares, não 10: escolha do autor (D-102, quatro fixos e quatro sazonais, D-105); 8 de 8 com resumo, função, recursos e ficha em `cidade.txt`; V-30 aviso esperado |
| 2 | 8 a 12 principais (≥ 2 heróis, ≥ 2 generais), agenda e pressão distintas | **ok** | 12 principais; 2 heróis vivos (Tessun, Rukhan), 2 generais (Kharos, Gruzk); 12 agendas e 12 pressões distintas; 0 `?` em agenda, pressão, capacidades e personalidade |
| 3 | Rotas com tempo ou desconhecidas; sem rota inventada | **ok, com ressalva** | 9 ligações no doc 07 (6 entre lugares, 3 de acesso), todas `desconhecida` (D-63); Alvasal sem rota interna e grafo em dois grupos (exceções V-23 e V-44); nenhuma rota criada para fechar o grafo |
| 4 | Recursos e dependências; nenhuma cidade isolada | **ok** | 5 a 11 linhas de recurso por lugar (66 no total, escala qualitativa); 12 dependências (8 críticas), cada lugar com ao menos uma crítica (docs 08 e 09); Alvasal depende da Rota do Sal e do Ermo, sem rota interna |
| 5 | Povos alinhados ao catálogo | **ok** | 12 nativos, 6 sub-raças e 25 forasteiros (`ALINHAMENTO_PALANCAR.md`); 25 de 25 pessoas com `raca_id`; validador 0 erro; 0 colisão com o `.kt` (D-134) |
| 6 | Facções com objetivo, métodos, aliados, rivais e conflito concreto | **ok, com lacuna explícita** | 14/14 com objetivo, métodos e tensão; 0 aliado assimétrico, 0 rival assimétrico, 0 aliado e rival ao mesmo tempo; rival `desconhecido` em 2 (Guardas da Marcha, Vigias da Cisterna), com motivo no doc 04 → D-136 |
| 7 | Relógios cruzados, sem avanço automático | **ok** | 8 relógios, progresso inicial 2 ou 3 de 6, gatilhos observáveis em 8 de 8; 12 eventos, todos ligados a relógio; 0 entidade inexistente |
| 8 | Mistérios sem solução predefinida | **ok** | 10 mistérios (MPL-01 a MPL-10), 4 hipóteses abertas cada, "a verdade não pode ser" e evidência para resolver em todos |
| 9 | Contradições intencionais × acidentais | **ok** | ver b) |
| 10 | IDs únicos e todos existem | **ok** | 47 entidades no registro (8 + 14 + 25), 0 duplicado; 0 ID de cidade, npc ou facção citado nos CSV sem registro; validador 0 erro |
| 11 | Fechamento com limites explícitos | **pendente** | é o doc 11 do reino 4, entregável do R4-8.2 |

## b) Contradições intencionais × acidentais

**Intencionais** (registrar no doc 11):
1. Mistérios MPL-01 a MPL-10 e os 8 relógios: ambiguidade deliberada, sem hipótese escolhida (doc 06; `13-misterios-limites`).
2. Toda duração `desconhecida` (D-63) e a ausência de rota do Vale com Aeltharyn, Coroas, Véu e Pacto Humano (doc 07 §4; contrato de fronteira).
3. Alvasal sem rota interna e grafo em dois grupos (exceções V-23 e V-44, `excecoes.txt`).
4. Reino sem rei e sem capital: Assembleia de Clãs e Capitão de Marcha por prova ou aclamação; desfecho da sucessão `desconhecido` (V-34; doc 04 §5).
5. Oito lugares, quatro fixos e quatro sazonais (V-30; D-102 e D-105).
6. 6 heróis lendários **mortos**, sem ressurreição; a lenda existe, o feito é `desconhecido`.
7. Linhagens do Vale distintas das de Aeltharyn, mesmo com nomes de povo iguais (P-3); 4 forasteiros compartilhados com o `.kt`.
8. A "neblina" e os pactos do `.kt` são de Aeltharyn; o Vale não explica a Neblina Longa, os Mistérios D e E nem o Conflito G (contrato de fronteira).
9. 2 facções com rival `desconhecido` e 7 pessoas sem ficha (6 da tríade, 1 líder de facção): lacunas explícitas.
10. Fonte `proposto` em 8 lugares, 14 facções e 25 pessoas (V-06): aguardam o autor.

**Acidentais** (achadas e tratadas): nenhuma nova. Os ajustes de conversão do R4-7.1 (capital `—`, prefixo `mist.`, recursos de evento com papel `?`) já estavam registrados e não são erro de lore.

**Conferido nesta revisão, sem achado novo:** contagens iguais nos dados e nos documentos (8 lugares; 47 entidades = 8 + 14 + 25; 25 pessoas = 12 principais + 6 tríade + 1 líder + 6 lendários; 14 facções; 8 relógios; 12 eventos; 24 sementes (8 P, 8 M, 8 G); 42 fatos; 55 resumos; 7 cenas; 81 relações; 66 linhas de conhecimento; 11 canais; 22 de reputação); 0 NPC morto em semente, evento ou cena; 0 decisão D-104 a D-135 pendente além de D-135 (protótipo) e D-136.

## c) Verificações locais (sem rede)

Conversor do reino 4 idempotente (0 diferença contra a base); 3 scripts compilam; validadores: reino 4 = 0 erro, 11 avisos, 2 exceções; reinos 2 e 3 = 0 erro, 40 e 22 avisos, iguais aos relatórios salvos; `reinos/` idêntico à base.

## d) Pendências para o autor

- **D-136:** aceitar os 2 rivais `desconhecido` como limite do reino, ou nomear rivais (lore novo, com registro).
- **D-135:** protótipo mínimo (nenhuma sessão executada).
- **P-4** (aberto no contrato de fronteira) e **D-67** (Véu, nomes) seguem com o autor.

## e) Veredito

Portas 1 a 10 marcadas (1, 3 e 6 com ressalva ou lacuna explícita); **11 pendente**, por ser o R4-8.2. O reino passa a "fechado 5" só no R4-8.2, com aprovação do autor.
