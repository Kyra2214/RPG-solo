# Relatório R3-8.1 — Revisão final do Véu dos Malditos (06/11/2026)

**Escopo:** as 11 portas de `MODELO_DE_REINO.md` §6 contra os dados atuais (`reinos/03-veu-dos-malditos/dados/`, CSVs e docs 01 a 15). Estado: **[PROPOSTO]**, para aprovação do autor (D-99). Nenhum lore alterado; nenhuma correção de texto necessária (nenhuma contradição acidental nova).

## a) Portas do checklist

| # | Porta | Resultado | Evidência |
|---|---|---|---|
| 1 | 10 cidades com ficha e função coerente | **ok, com ressalva** | 8 lugares, não 10: escolha do autor (D-66, "capital e 7 lugares de pacto"); todos com resumo, função, ≥ 1 rota e 5 a 8 recursos; função coerente com o vale e o Ermo de Sal (M-04) conferida em R3-1.x; V-30 aviso esperado |
| 2 | 8 a 12 principais (≥ 2 heróis, ≥ 2 generais), agenda e pressão distintas | **ok** | 12 principais; 2 heróis vivos (Tazk, Vurga), 2 generais vivos (Orsk, Ilkar); 12 agendas e 12 pressões distintas, 0 `?` em agenda, pressão, limites, capacidades e tom |
| 3 | Rotas com tempo ou desconhecidas; sem rota inventada | **ok, com ressalva** | 10 rotas, todas `desconhecida` (D-63), nenhuma inventada; Beira-Sal, Borda-Mansa, Fio-Longo e Candeia-Morta com 1 só rota (V-23 aviso) |
| 4 | Recursos e dependências; nenhuma cidade isolada | **ok** | 5 a 8 linhas de recurso por lugar (escala qualitativa, D-63); 0 lugar sem rota ou recurso; 9 dependências (7 críticas) no doc 08 |
| 5 | Povos alinhados ao catálogo | **ok** | 11 nativos e 17 forasteiros com ID do catálogo; 0 ID fora do catálogo em `12-competencias.csv`; validador sem erro V-12 |
| 6 | Facções com objetivo, métodos, aliados, rivais e conflito concreto | **ok, com lacuna explícita** | 11/11 com objetivo, métodos e tensão; aliados definidos nas 11; rivais `desconhecido` em 3 (Casa dos Fios, Turnos da Vigília, Vigias da Orla); 0 aliado e rival ao mesmo tempo; 0 aliança ou rivalidade assimétrica → D-99 |
| 7 | Relógios cruzados, sem avanço automático | **ok** | 8 relógios, progresso inicial 1 a 3 de 6, gatilhos observáveis em todos, 0 entidade inexistente |
| 8 | Mistérios sem solução predefinida | **ok** | 9 mistérios, 4 hipóteses abertas cada, "a verdade não pode ser" e evidência para resolver em todos |
| 9 | Contradições intencionais × acidentais | **ok** | ver b) |
| 10 | IDs únicos e todos existem | **ok** | validador: 0 erro; 0 ID duplicado; 0 ID citado sem definição; 0 colisão com Aeltharyn, as Coroas e o `.kt` (D-97) |
| 11 | Fechamento com limites explícitos | **pendente** | é o doc 11 do reino 3, entregável do R3-8.2 |

## b) Contradições intencionais × acidentais

**Intencionais** (registrar no doc 11):
1. Mistérios MV-01 a MV-09 e os 8 fatos de relógio: ambiguidade deliberada, sem hipótese escolhida (`13-misterios-limites`, doc 06 §6).
2. Toda ligação com duração `desconhecida` (D-63) e a ausência de rota Véu–Aeltharyn e Véu–Coroas (doc 07; contrato de fronteira §3).
3. 11 heróis lendários **mortos** (6 com lugar, 5 sem lugar, D-73), sem ressurreição (doc 06 §9.3); a lenda existe, o feito é `desconhecido`.
4. 8 lugares e 0 assentamentos (D-66): V-30 aviso; `assentamento.txt` só com cabeçalho (D-96).
5. 6 exceções do validador (`excecoes.txt`): 4 eventos que citam ligações do doc 07 sem dois locais existentes e 2 rotas que repetem o par de uma rota do Conselho, com outra natureza.
6. Corvídeo (`kenku`, nativo) e Corvário (`homem-corvo`, forasteiro) são povos distintos no Véu; a D-01 em aberto é de Aeltharyn e não muda isso.
7. A "neblina" e os "pactos" do `.kt` são de Aeltharyn; o Véu não explica a Neblina Longa nem os Mistérios D e E (contrato de fronteira §3; D-97).
8. 3 facções com rival `desconhecido` e 5 heróis sem lugar: lacunas explícitas, não erros.

**Acidentais** (achadas e tratadas):
- **A1 (corrigida em R3-7.1 e R3-7.2):** erros de conversão, não de lore: `profissao` `—` rejeitada pelo validador em 11 pessoas, e papel de guardião (tríade) não capturado nos `.txt`. Conversor e validador ajustados; reino 2 intocado.
- **Conferido nesta revisão, sem achado novo:** contagens iguais nos dados e nos documentos (8 lugares, 49 entidades no registro: 8 + 11 + 30; 30 pessoas = 12 principais + 7 tríade sem ficha + 11 lendários; 11 facções; 8 relógios; 12 eventos; 24 sementes; 41 fatos; 57 resumos = 49 + 8 relógios; 74 relações; 11 canais; 6 cenas); "10 cidades" e "10 facções" do contrato de fronteira são de Aeltharyn; 0 NPC morto como agente de semente, evento ou cena; 0 decisão D-68 a D-97 pendente.

**Lacunas explícitas, não erros:** rival de 3 facções; duração de toda ligação; titular do assento de Morrovéu, vigia da Provação e destino da guarda (doc 04 §2.1 e §5); custo da estabilização; D-98 aguardando o autor.

## c) Pendências para o autor

- **D-99:** aceitar as 3 lacunas de rival como limite do reino, ou nomear rivais (o que cria lore novo e exige registro).
- **D-98:** protótipo mínimo (R3-7.3).

## d) Veredito

Portas 1 a 10 marcadas (1, 3 e 6 com ressalva ou lacuna explícita); **11 pendente**, por ser o R3-8.2. O reino passa a "fechado 4" só no R3-8.2, com aprovação do autor.
