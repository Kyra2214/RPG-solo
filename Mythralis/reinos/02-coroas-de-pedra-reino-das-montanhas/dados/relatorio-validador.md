# Relatório do validador — Coroas de Pedra (R2-7.1)

**Estado:** rascunho de R2-7.1 para aprovação do autor (D-58) · **Data:** 2026-11-05 · **Etiqueta:** [PROPOSTO]

**Resultado: 0 erros, 40 avisos, 7 exceções aplicadas.** Reino em `em_composicao`: as regras de mínimos (V-30 a V-35) valem como aviso (`VALIDADOR_DE_REINO.md` §1). Relatório linha a linha: `relatorio-validador.txt`.

## Como foi feito

- **Dados (D-21, opção A):** `dados/*.txt`, texto com `|`, UTF-8, LF, `# esquema=1`, campo desconhecido `?`, não se aplica `—`, `reino` e `fonte` em todo registro (`ESQUEMA_DE_DADOS.md`). Gerados dos CSVs e dos docs 01, 03, 07 e 10 por `plano/ferramentas/converter_reino2.py`; nenhum fato foi acrescentado. Para a CSV/MD continuarem a fonte editorial, rodar de novo o conversor regenera `dados/`.
- **Validador:** `plano/ferramentas/validar_reino.py` implementa as regras V-01 a V-74 e V-99 (V-A1 não se aplica: tríade não é regra do reino 2). Teste do validador numa cópia quebrada: detectou os 7 defeitos plantados (referência inexistente, rota com extremos iguais, progresso maior que segmentos, semente com porte e ameaça inválidos, mistério com 1 hipótese, restrito sem marcador, cena com 9.000 caracteres).
- Nada foi instalado; só Python padrão.

## Registros por arquivo

| Arquivo | Registros |
|---|---|
| `assentamento.txt` | 20 |
| `calendario.txt` | 1 |
| `canal.txt` | 11 |
| `cena.txt` | 6 |
| `cidade.txt` | 10 |
| `evento.txt` | 13 |
| `excecoes.txt` | 7 |
| `faccao.txt` | 14 |
| `fato.txt` | 41 |
| `ids_aposentados.txt` | 0 |
| `misterio.txt` | 6 |
| `npc.txt` | 45 |
| `povo_no_reino.txt` | 12 |
| `recurso.txt` | 84 |
| `reino.txt` | 1 |
| `relacao.txt` | 91 |
| `relogio.txt` | 7 |
| `reputacao.txt` | 24 |
| `resumo.txt` | 96 |
| `rota.txt` | 13 |
| `semente.txt` | 90 |

## Achados corrigidos nesta rodada (eram erros no primeiro relatório)

- **V-03, 5 erros:** `canal.arquivo`, `canal.cacador`, `canal.cartaz`, `canal.emissario` e `canal.mensageiro` já existem em Aeltharyn; ID é único no mundo. Os 11 canais do reino 2 passaram a `canal.coroas-*` (13-canais.csv, 13-reacao-ao-jogador.md). Aeltharyn não foi tocado.
- **V-10 e V-21 (conversor):** listas com observação entre parênteses viraram só IDs; ligação de semente "não provada" a relógio ou evento passou a `—` com a observação na `fonte`; Altavento–Pedra-Ata coincide com a delegação ao Concílio (exceção abaixo).
- **V-52:** as faixas dos assentamentos (`parte da cidade-mãe`, `mínima e sazonal`, `indefinida`) são faixas de uso do doc 05 §4, não números; entram na lista de faixas do validador (ASSUMINDO).

## Exceções registradas (`excecoes.txt`, E rebaixado a A)

| Regra | Entidade | Motivo |
|---|---|---|
| V-10 | `event.assento-vazio` | a rota citada é ligação do doc 07 sem dois locais existentes (grupo de cidades, trecho fora do reino ou destino sem nome); não vira rota.txt e não ganha duração (doc 07 §2; R2-7.1) |
| V-10 | `event.cais-fechado` | a rota citada é ligação do doc 07 sem dois locais existentes (grupo de cidades, trecho fora do reino ou destino sem nome); não vira rota.txt e não ganha duração (doc 07 §2; R2-7.1) |
| V-10 | `event.comporta-cede` | a rota citada é ligação do doc 07 sem dois locais existentes (grupo de cidades, trecho fora do reino ou destino sem nome); não vira rota.txt e não ganha duração (doc 07 §2; R2-7.1) |
| V-10 | `event.nevasca-fecha-passos` | a rota citada é ligação do doc 07 sem dois locais existentes (grupo de cidades, trecho fora do reino ou destino sem nome); não vira rota.txt e não ganha duração (doc 07 §2; R2-7.1) |
| V-10 | `event.registro-duplo` | a rota citada é ligação do doc 07 sem dois locais existentes (grupo de cidades, trecho fora do reino ou destino sem nome); não vira rota.txt e não ganha duração (doc 07 §2; R2-7.1) |
| V-10 | `event.registro-provado` | a rota citada é ligação do doc 07 sem dois locais existentes (grupo de cidades, trecho fora do reino ou destino sem nome); não vira rota.txt e não ganha duração (doc 07 §2; R2-7.1) |
| V-21 | `route.altavento-pedra-ata` | mesmo par de route.concilio-altavento-pedra-ata, mas outra natureza (mensageiros carregam cópias, não delegação); meio desconhecido nas duas (doc 07 §2; R2-7.1) |

## Avisos (lista completa no `.txt`)

| Código | Qtde | O que é |
|---|---:|---|
| V-06 | 4 | registros com fonte "proposto": aguardam aprovação do autor (cidade, assentamento, NPC, facção) |
| V-10 | 6 | exceções acima (ligações do doc 07 sem dois locais existentes) |
| V-21 | 1 | exceção acima |
| V-23 | 1 | cidades só conexas por rotas de duração desconhecida (todas as durações do reino são `?`) |
| V-26 | 1 | rio e posição de fortaleza: revisão manual contra M-04 |
| V-27 | 20 | 20 assentamentos com acesso de duração desconhecida |
| V-31 | 1 | mundo com menos de 5 reinos escritos |
| V-34 | 1 | sem herdeiro: sucessão do presidente do Concílio é lacuna explícita (doc 04 §8) |
| V-42 | 5 | facções com aliados ou rivais `?` (a ficha não nomeia) |

## Pendências para o autor e limites

- Rotas do doc 07 sem dois locais existentes (grupo de cidades, trecho fora do reino, destino sem nome) ficam fora de `rota.txt`; só as ligações entre dois locais nomeados viraram rota (13 rotas, todas `desconhecida`). Se o autor nomear o destino, a rota entra e a exceção sai.
- `acesso` das cidades fica `?` (nenhum doc dá meio navegável ou terrestre em palavras do esquema); por isso V-24 não dispara.
- `faccao` do NPC fica `—`: o registro mestre não liga NPC a facção em campo próprio (a liderança está em `faccao.txt`).
- `objetivo` do NPC principal repete a `agenda` (ASSUMINDO); `papeis` vem da categoria do doc 01 (guia, memória, mestra de fogo e arquivista = `outro`).
- O validador não substitui os testes de integração I-01 a I-06 (dependem da campanha e do motor).
