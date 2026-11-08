# Plano do Reino 5 — Azeroth (antes "Pacto Humano")

**Objetivo:** escrever e fechar o reino 5, o último do anel, nos mesmos moldes dos reinos 1 a 4, com as lições dos quatro já aplicadas desde o início. Depois do fechamento 6, o mundo tem os cinco reinos escritos; a integração ao RPG Solo segue `INTEGRACAO_FUTURA_RPG_SOLO.md`.
**Regra:** nada aqui inventa nome, segredo ou fato sem fonte. O que não tem fonte fica `desconhecido` ou vira decisão do autor. Um reino por vez (`PLANO_DOS_REINOS.md` §1).
**Escala (D-63):** distância, tamanho e produção são **qualitativos**. **A camada de números não será escrita**: sem duração de rota, sem faixa numérica de população, sem nível fino de recurso. Avisos do validador sobre esses campos (V-27 e afins) são esperados.
**Mestiços (D-64):** não são alocados a lugar, facção nem reino; o doc 10 só repete a regra.
**Legenda:** prioridade P0 (bloqueia o resto), P1 (importante), P2 (desejável). Etiquetas **[CANÔNICO]** (já aprovado em `mundo/` ou nos reinos 1 a 4) e **[PROPOSTO]** (precisa de aprovação). Módulos usam o prefixo `R5-`.
**Nome (D-139, parcial):** **Azeroth**, escolhido pelo autor em 07/11/2026; "Pacto Humano" é o nome de trabalho antigo, que os reinos 1 a 4, M-04 e M-05 ainda usam. Escala, governo e Estelar decididos no R5-0.3 (D-141).
**Pasta:** `reinos/05-azeroth/` (a criar com o R5-1.1), com as peças de `MODELO_DE_REINO.md` §1 (o doc 02 de arquitetura vale para todos e não se repete).
**Liberação:** o autor aprovou o fechamento 5 do Vale Palancar e liberou o reino 5 em 07/11/2026 (D-138, opção A de D-137).

## 0. Ponto de partida (o que já existe sobre o reino 5)

| Item | Fonte | Estado |
|---|---|---|
| Posição: sudeste e sul-central; planícies férteis, rios largos e costa meridional; vizinhos plenos Véu dos Malditos e Aeltharyn; contato parcial: nenhum | M-04 §3 (G-02) | **[CANÔNICO]** |
| Ordem do anel (horário, a partir do norte): Coroas, Aeltharyn, Pacto Humano, Véu, Vale Palancar | M-04 §3 | **[CANÔNICO]** |
| Aeltharyn tem humanos nas fronteiras e portos; o Pacto Humano é o último a ser escrito | M-04; doc 01 §2.4 de Aeltharyn | **[CANÔNICO]** |
| 9 nativos: Humano, Pequenino, Luminar, Tartarino, Abissal Costeiro, Nascido da Maré, Nascido do Vento, Sereiano, Estelar | M-05 §3 | **[CANÔNICO]**; Estelar como nativo é encaixe a confirmar (M-05, pergunta 5) |
| 25 forasteiros e 16 raças sem papel (matriz M-05 §4); 0 aliado | M-05 §4 | **[CANÔNICO]** |
| Fluxos que tocam o reino: **As Costas** (tritões, sereianos, tartarinos e marinheiros humanos ligam Thalanor à costa meridional humana) e **As Fugas do Véu** (refugiados chegam às planícies humanas; a Lei de Proteção de Refugiados é de Aeltharyn). Os fluxos 1 a 3 (Descida, Rota do Sal, Corredores Largos) não chegam ao reino 5 | M-05 §5 | **[CANÔNICO]**; detalhar no R5-0.4 |
| Geografia: o Mar Austral banha o sul, a costa meridional humana e a foz do Argente; humanos em portos e costa a sudeste e leste de Aeltharyn (Thalanor, Costa de Âmbar; Mistério C e contrabando) | M-04 G-06, G-09 e §5 | **[CANÔNICO]**; a foz do Argente e o Mistério C não ganham causa aqui |
| Nativos em dois reinos: Pequenino (também nativo de Aeltharyn); Abissal Costeiro (aliado em Aeltharyn, nativo aqui) | M-05 §3 e §4 | **[CANÔNICO]**; linhagem do reino 5 sem nome |
| Tom e regras de narração (W-01), ponte de calendário (W-02), convenções de nome (W-03) | `mundo/M-01`, `M-02`, `M-10` | **[CANÔNICO]** |
| Lugares, NPCs, facções, governo, história, leis, economia, relógios | — | **nada escrito** |

**Limites herdados (a detalhar no R5-0.4):** o reino 5 só afirma sobre Aeltharyn, o Véu, o Vale Palancar e as Coroas o que M-04, M-05 e os contratos de fronteira já dizem; não explica a Neblina Longa, os Mistérios D e E, o Conflito G, a Descida do Argente (origem, número ou fim) nem mistério algum dos reinos vizinhos; não altera nada dos quatro reinos fechados.

## 1. Lições dos reinos 1 a 4 aplicadas

| Problema anterior | Regra para o reino 5 |
|---|---|
| IDs conflitantes, pessoas e organizações sem ID (D-04, D-05, D-08, D-09, D-10, D-18) | todo NPC, lugar, facção e organização recebe ID **na hora da escrita**; registro mestre vivo desde a F1 |
| IDs de canal ou entidade colidiram entre reinos (R2-7.1) | conferir todo ID novo contra os registros mestres dos reinos 1, 2, 3 **e 4** antes de entrar |
| Cargos acumulados (D-11, D-40) | cargo duplicado só se declarado intencional na ficha |
| Povo do registro × texto (D-20) | povo escrito em ID do catálogo desde a primeira ficha |
| Residência de governantes (D-19) | residência de cada governante explícita no doc principal |
| Mínimos de principais (D-22) | 8 a 12 principais, ≥ 2 heróis vivos, ≥ 2 generais |
| Aliados e rivais `desconhecido` (D-23, D-61, D-99, D-136) | aliados e rivais **preenchidos na escrita**; `desconhecido` só com motivo registrado |
| Contagem errada no contrato ou na ata (R2-8.1) | contagens conferidas contra os dados (9 nativos, 25 forasteiros) |
| Exceções do validador: ligações sem dois locais existentes; lugar sem rota (V-23, V-44 do reino 4) | todo evento cita só ligações entre locais que existem; **todo lugar nasce com ao menos uma ligação documentada** |
| Número onde o texto não tem (D-63) | escala qualitativa |
| Mestiços alocados (D-64) | nenhum mestiço em lugar, facção ou reino |
| Afirmação sobre o vizinho (P-1) | cada frase sobre outro reino passa pelo contrato; o resto vira ponto de contato aberto |
| Matriz × fluxo em conflito (P-2) | todo fluxo de M-05 §5 é conferido contra a coluna Humanos da matriz **antes** de escrever o doc 10 |
| Linhagens iguais às de outro reino (P-3) | povos que também são nativos de outro reino (ex.: Humano, forasteiro nos demais reinos e F* em Aeltharyn) têm linhagem própria declarada, sem parentesco automático |
| Lacuna herdada sem dono (P-4: Lessa Cervo-Manso sem ID em Aeltharyn) | lacunas de outro reino ficam registradas no contrato, sem correção aqui |
| Doc 11 com decisão listada como pendente embora aceita (R3-8.2) | a ata lista decisões pelo estado real do `DECISOES.md` e é conferida por script |
| Status do README e do plano fora de sincronia | cada entrega confere README raiz, README do reino, `PLANO_DOS_REINOS.md`, plano e `DECISOES.md` entre si |

## Resultado do R5-0.1 (07/11/2026)

- **a) M-05:** 9 nativos, 0 aliado, 25 forasteiros e 16 raças sem papel (9 + 25 + 16 = 50), conferidos contra a matriz (coluna Humanos); nenhuma raça nova entra. **Estelar** (`ancestral-estelar`) como nativo segue como pergunta aberta de M-05 (§9, item 5), levada ao R5-0.3.
- **b) M-01 e M-02:** valem sem mudança; o reino 5 não depende de lore de mundo não aprovado. Sem decisão nova do autor para este módulo.
- **c) Calendário:** a ponte é o dia do ano (W-02, M-02 §3); a coluna do reino 5 e a contagem local ficam para o R5-3.4, como nos reinos 2 a 4. Nada escrito agora.
- **Achado corrigido:** o ponto de partida do plano dizia "0 raça sem papel"; o correto é 16 (corrigido acima).

## Resultado do R5-0.2 (07/11/2026)

Decisões herdadas, conferidas em `DECISOES.md` e aplicadas a este plano, sem decisão nova do autor:

| Decisão | Regra aplicada ao reino 5 | Onde |
|---|---|---|
| D-21 | dados no formato de texto separado por barra vertical (esquema 1); conversor e validador adaptados no R5-7.1 | R5-2.1, R5-7.1 |
| D-22 | 8 a 12 principais, ≥ 2 heróis vivos, ≥ 2 generais **cumpridos na escrita** (o rebaixamento para aviso valeu só para Aeltharyn; os reinos 3 e 4 cumpriram) | R5-1.3 |
| D-23 | aliados e rivais **preenchidos na escrita**, derivados do que o texto já diz; nada de aliança sem fonte | R5-1.4, R5-2.4 |
| D-63 | escala qualitativa; sem camada de números; avisos V-27 e afins esperados | Fases 3 e 5 |
| D-64 | nenhum mestiço em lugar, facção ou reino; só a regra no doc 10 | R5-1.6 |
| D-99 e D-136 | rival ou aliado `desconhecido` só com motivo registrado, e listado no doc 11 como limite explícito | R5-1.4, R5-8.2 |
| D-110 (P-3) | povo que é nativo em dois reinos (Pequenino) ou aliado em outro (Abissal Costeiro) tem linhagem própria declarada | R5-1.6 |

- **ASSUMINDO:** D-21 a D-23 foram escritas "válidas para os reinos 1 e 2"; os reinos 3 e 4 as herdaram pelo plano, e o reino 5 faz o mesmo. Reversível, sem efeito nos reinos fechados.
- **Dependiam do R5-0.3 (resolvido em D-141):** V-30 (10 lugares atendem), V-34 (um governante e um herdeiro por coroa) e as convenções de nome (M-10 §11).

## Resultado do R5-0.4 (07/11/2026)

Novo `plano/CONTRATO_DE_FRONTEIRA_PACTO_HUMANO.md` (D-140): fixos de Aeltharyn (13), Coroas (5), Véu (7) e Vale (4) que tocam o reino 5; 7 afirmações permitidas; 16 vedadas; 7 ganchos; 6 pontos abertos. Contato: Aeltharyn e Véu são vizinhos plenos; Coroas e Vale **não tocam** o reino 5 (elo "nenhuma"). Só As Costas e As Fugas do Véu chegam ao reino 5.

- **Abertos:** P-1 Estelar e P-6 escala e governo (R5-0.3, autor); P-2 qual pântano na frase das Fugas; P-3 fronteira Véu × costa humana na foz do Argente. **Aceitos:** P-4 (linhagens próprias, regra de D-110). **Herdado:** P-5 (Lessa Cervo-Manso).
- **ASSUMINDO:** o arquivo mantém o nome previsto no plano (`..._PACTO_HUMANO.md`) para preservar referências; o texto usa Azeroth.

## Resultado do R5-0.3 (07/11/2026) — D-141

- **a) Escala (ASSUMINDO):** **10 lugares principais** (`city.*`), qualitativos (D-63), como Aeltharyn e as Coroas; atende V-30. Tipos a definir na F1: capitais das coroas, portos do Mar Austral e da costa, cidades de rio e de planície, sem número de população.
- **b) Nome:** Azeroth (D-139).
- **c) Governo (autor):** **várias coroas rivais**, sem rei único. ASSUMINDO: **3 coroas**; cada uma com governante e herdeiro (V-34 por coroa), residência explícita (D-19), aliados e rivais preenchidos (D-23, D-99). Quem domina, a origem da rivalidade e o nome de cada coroa ficam para R5-1.1 a R5-1.5; a rivalidade não envolve outros reinos (contrato §6).
- **d) Nomes:** nova seção 11 de `mundo/M-10-nomes-minimo.md` (molde mantido, não repetição ampliada, famílias de epíteto só como tom).
- **e) Estelar (ASSUMINDO):** **mantido como nativo**; a matriz não muda. Encaixe e linhagem na F1 (R5-1.6). Reversível.
- Contrato de fronteira: P-1 e P-6 passam a decididos (D-141). **Fase 0 concluída (4/4).**

## Resultado do R5-1.1 (07/11/2026) — D-142

Nova pasta `reinos/05-azeroth/` (README e `01-reino-de-azeroth.md`, parte 1). **10 lugares fixos** em três coroas e um lugar comum: Trigal (Campoalvo, Vaudouro, Torrefronte), Cais (Alvamar, Enseada-Vigia, Concharia), Vento (Torreclara, Estrelado, Pouso-das-Brisas) e **Meiovau** (cidade livre, Mesa das três chaves). Rios nomeados: Trigueiro e Corre-Largo. Função × geografia conferida (planície, rio, costa, céu aberto, limite). Só As Costas (Enseada-Vigia, Alvamar) e As Fugas do Véu (Meiovau) tocam outros reinos, sem rota, número ou causa; foz do Argente e fronteira com o Véu não afirmadas (P-3). Nomes: 0 colisão contra os registros dos reinos 1 a 4.

- **ASSUMINDO:** nomes de lugares, coroas, rios e órgãos; faixas de população; Pouso-das-Brisas e a linha de torres como função da Coroa do Vento; Meiovau como lugar comum. Reversível, sem efeito nos reinos fechados.
- O doc 03 é do R5-1.2; facções e leis de acolhida, do R5-1.4.

## Resultado do R5-1.2 (07/11/2026) — D-143

Novo `reinos/05-azeroth/03-assentamentos-e-rede-politica.md`: fichas políticas dos **10 lugares** (lugar-mãe, liderança por papel, obrigação, interesse, tensão), posição relativa qualitativa, vínculos das coroas e de Meiovau, e rede política (três rivalidades: Trigal × Cais, Cais × Vento, Vento × Trigal). Nenhum campo vazio; todo lugar-mãe existe (as três capitais). Sem assentamentos menores (aviso V-31, como nos reinos 3 e 4). **ASSUMINDO:** lugar-mãe como vínculo de tributo; `—` nas capitais e em Meiovau. Nada novo além do doc 01.

## Resultado do R5-1.3 (07/11/2026) — D-144

`reinos/05-azeroth/01-reino-de-azeroth.md`, parte 2: **12 principais** (D-22 cumprido: 2 heróis vivos, 2 generais; governante e herdeiro nas três coroas, V-34), **8 heróis lendários** e tríade dos **10 lugares** (36 pessoas, 9 nativos cobertos, mestiços 0). Todo NPC com ID, povo em ID do catálogo, agenda, pressão, limite, confirmado, suspeita e desconhecido; nomes conferidos contra os reinos 1 a 4 (0 colisão); `mundo/M-10` §13. Residência explícita nas fichas; governantes e herdeiros fora da tríade. **ASSUMINDO:** nomes, faixas etárias, feitos dos heróis vivos e residência dos herdeiros. Sucessão, facções, aliados e rivais: R5-1.4.

## Resultado do R5-1.4 (07/11/2026) — D-145

Novo `reinos/05-azeroth/04-instituicoes-justica-pactos.md`: governo e sucessão por coroa (três costumes distintos), Mesa de Meiovau (três chaves; não decide a precedência), acordos entre coroas (sem o termo "pacto"), justiça, Costume da Porta (acolhida local própria), tributo qualitativo, governo dos 10 lugares e **15 facções** com aliados e rivais preenchidos (conferido por script: matriz simétrica, 0 aliado também rival; 1 rival `desconhecido`, F-07, com motivo). Nomes e IDs sem colisão com os reinos 1 a 4; `mundo/M-10` §14. **ASSUMINDO:** regras de sucessão, Mesa, Costume da Porta e dois líderes novos.

## Resultado do R5-1.5 (07/11/2026) — D-146

Parte 3 do doc 01 e novo `05-economia-leis-e-fluxos.md`: 4 eras locais, origem do nome "Azeroth" (2 versões, origem `desconhecida`), lendas dos 10 lugares, cronologia relativa, defesa e vulnerabilidades (nenhum inimigo afirmado), **10 leis** com lacuna, economia qualitativa e os fluxos **As Costas** e **As Fugas do Véu** só do lado de cá (conferidos contra M-05 por script; fluxos 1 a 3 fora). Sem datas, sem número, sem sincronia com outros reinos; `mundo/M-10` §15. **ASSUMINDO:** leis como costume, versões do nome, sal e madeira sem origem.

## Resultado do R5-1.6 (07/11/2026) — D-147

Novo `reinos/05-azeroth/10-matriz-povos-nativos-cidades-e-forasteiros.md`: **9 nativos** em ID do catálogo (cada um em ao menos 2 lugares), **8 sub-raças** do catálogo (Humano, Pequenino, Luminar, Tartarino e Abissal Costeiro; Nascido da Maré, Nascido do Vento, Sereiano e Estelar não têm sub-raça no catálogo), **linhagens próprias** de Humano, Pequenino e Abissal Costeiro (P-4, sem parentesco com Aeltharyn), lugares × povos (10 de 10 com ≥ 2 nativos, idênticos ao doc 01), **25 forasteiros** (8 com lugar, 17 sem lugar fixo; lidos da matriz por script), os três chegados do Véu em Meiovau (`tiefling`, `lizardino`, `vampirico`), papéis por povo com lacunas abertas (3 sem herói, 4 sem guardião, 2 sem benfeitor) e mestiços só pela regra D-64. NPCs novos: 0. **ASSUMINDO:** onde cada sub-raça aparece, motivos dos forasteiros e a outra sub-raça nos pares compartilhados com Aeltharyn.

## Resultado do R5-1.7 (07/11/2026) — D-148

`reinos/05-azeroth/01-reino-de-azeroth.md`, parte 4: **10 mistérios** (um por lugar, MA-01 a MA-10), cada um com origem em rumor, lenda ou suspeita já escrita, 4 hipóteses abertas, resposta única não escolhida, \"a verdade não pode ser\" e provas possíveis. Novo `06-modelo-operacional-e-aventuras.md`: regras comuns, custo por segmento, **10 relógios** (CA-01 a CA-10, progresso inicial 2/6 ou 3/6, sem avanço automático), **10 conflitos** com causa concreta, **10 oportunidades** e como mistério e relógio mudam. Nenhum mistério ou relógio toca o Mistério C, a Neblina Longa, os Mistérios D e E, o Conflito G, a foz do Argente ou outro reino; 0 NPC, lugar, facção ou rota novo; IDs de relógio sem colisão com os reinos 1 a 4. **ASSUMINDO:** número de relógios (dez), progresso inicial, \"o que trava\", \"quem paga\" e quem decide no segmento 5.

## Resultado do R5-4.1 (07/11/2026) — D-157

Novos `reinos/05-azeroth/12-fichas.md` e `12-fichas.csv`: **12 de 12** principais com ficha completa (16 colunas, 0 vazio), 12 agendas e 12 pressões distintas, raça, sub-raça, classe e profissão do `ALINHAMENTO_AZEROTH.md`, facções e aliados de `12-facoes.csv`, relógio ligado a cada um (CA-01 a CA-08 e CA-10), 4 acúmulos de cargo marcados (CI-07). 0 NPC, nome, lugar, facção ou fato novo; origem `desconhecida` em todos. **ASSUMINDO:** traços, medos e condições de evolução como leitura editorial; coluna `camadas` criada no R5-6.2.

## Resultado do R5-4.4 (07/11/2026) — D-160; fim da Fase 4

Novos `12-voz.md` e `12-voz.csv`: **12 de 12** principais com tom, 3 frases-modelo (instrução de tom, ASSUMINDO) e `nunca_diz` (suspeita, desconhecido e limite do cargo da ficha); 0 frase que revele suspeita ou desconhecido, 0 fato novo.

## Resultado do R5-8.2 (07/11/2026) — D-174; fim da Fase 8

Novo doc 11, `reinos/05-azeroth/11-fechamento-de-azeroth.md`: resultado das oito fases, 11 de 11 portas, 12 contradições intencionais, decisões em aberto, critérios finais de uso e conferência do conjunto dos cinco reinos (383 IDs, 0 colisão, 0 nome repetido; anel fechado). **Aguarda a aprovação do autor** (D-174); 0 fato novo, reinos 1 a 4 intactos.

## Resultado do R5-8.1 (07/11/2026) — D-173

Novo `plano/RELATORIO_R5_8_1.md`: as 11 portas de `MODELO_DE_REINO.md` §6 (1 a 10 marcadas, 3 com ressalva e 6 com lacuna explícita; 11 pendente, é o R5-8.2); 12 contradições intencionais listadas para o doc 11, 0 acidental nova. Sem alteração de lore, dados ou scripts; sem testes (só ao fim da Fase 8).

## Resultado do R5-7.3 (07/11/2026) — D-172; fim da Fase 7

Nova seção 13 em `plano/PROTOTIPO_MINIMO.md`: lugar pequeno **Estrelado** (`city.estrelado`), NPC `npc.ixra-noite-aberta` (resumo, sem ficha nem voz) e Vaudouro em uma linha; condições A (sem mundo) e B (com mundo, 1.375 caracteres); regras de classificação, roteiro de 8 turnos, métricas Q1 a Q10 e critério de aceite escritos **antes** da sessão. **Nenhuma sessão executada**; 0 fato, NPC, lugar, facção ou rota novo. ASSUMINDO: escolha de Estrelado e Vaudouro; limite de Q9 em 1.500.

**Testes de fim de F7** (locais, sem rede): 5 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos), relatórios idênticos aos salvos; conversor do reino 5 idempotente (`dados/` idêntico após nova execução) e validador 0 erro, 7 avisos, 0 exceções; 18 CSV de Azeroth íntegros; 8 IDs citados no §13 existem; 0 fato `restrito` ou `secreto`, relógio ou ID de Astrel no contexto B (1.375 caracteres); reinos 1 a 4, `referencia/`, `referencia-tecnica/`, `mundo/` e `plano/ferramentas/` idênticos à base; em `plano/` só mudaram `PROTOTIPO_MINIMO.md`, `REFINAMENTO_PACTO_HUMANO.md`, `PLANO_DOS_REINOS.md` e `DECISOES.md`; reino 5 só com o README da pasta. 0 falha, 0 regressão.

## Resultado do R5-7.2 (07/11/2026) — D-171

Nova seção 9 em `plano/MAPA_CATALOGO_KOTLIN.md`: KA1 a KA9 (0 entidade de Azeroth no `.kt`, 0 colisão de ID entre 69 e 68, 9 nativos fora do `.kt` exceto o Pequenino como linhagem própria, 8 linhagens distintas, V-A1 10 de 10 conferida à mão, lacunas do esquema). Nada instalado, nenhum código alterado, `.kt` não executado. Sem testes (só ao fim da Fase 7).

## Resultado do R5-7.1 (07/11/2026) — D-170

Novos `plano/ferramentas/converter_reino5.py` (adaptado de `converter_reino4.py`), `reinos/05-azeroth/dados/` (21 `.txt`, 485 registros) e `relatorio-validador.md`/`.txt`: **0 erro, 7 avisos, 0 exceções** (V-06 ×3, V-23, V-26, V-31, V-42). `validar_reino.py` ganhou só dois ajustes mínimos (classe `—` aceita para falecido; texto de V-34 para Azeroth); relatórios dos reinos 2, 3 e 4 idênticos. **ASSUMINDO:** chave `kingdom.azeroth` e `calendario.contagem-das-chaves`. Sem testes além do validador (só ao fim da Fase 7).

## Resultado do R5-6.4 (07/11/2026) — D-169; fim da Fase 6

Novo §9 no doc 06 (`06-modelo-operacional-e-aventuras.md`): verificação em 9 perguntas, 4 classes (fixo, livre, exige registro, proibido), lista de proibições ligada ao contrato de fronteira (§6), casos pedidos (chefe, lugar, guerra, NPC, morte, rota, As Costas e Fugas do Véu) e teste rápido de 4 perguntas. 0 entidade, regra ou fato novo. **ASSUMINDO:** separação em classes.

**Testes de fim de F6** (locais, sem rede): 4 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos), iguais aos salvos; 18 CSV de Azeroth íntegros; 63 IDs, 0 colisão com os reinos 1 a 4; 0 referência inválida nas 7 cenas; reinos 1 a 4, `referencia/`, `referencia-tecnica/`, `mundo/` e `plano/ferramentas/` idênticos à base. 0 falha, 0 regressão.

## Resultado do R5-6.3 (07/11/2026) — D-168

Novos `15-cenas-de-teste.md` e `.csv`: **7 cenas** (Campoalvo, Vaudouro, Meiovau, Concharia, Pouso-das-Brisas, Torrefronte, Enseada-Vigia), cada uma com "entra" (maior: 2103 caracteres, limite ~2.400) montado só de resumos, fatos público/local, voz, semente pequena, rumor e rota já escritos, e com "não entra". 0 fato restrito ou secreto, 0 relógio, 0 frase `nunca_diz`, 0 rota com duração, 0 fato novo; fronteiras só do lado de cá. **ASSUMINDO:** escolha dos sete lugares.

## Resultado do R5-6.2 (07/11/2026) — D-167

Novos `14-camadas-de-informacao.md` e `.csv`: **44 fatos** (10 públicos, 12 locais, 2 restritos, 20 secretos): 24 dos 12 principais, 10 dos mistérios e 10 relógios, cada restrito ou secreto com caminho e "como descobrir". Coluna `camadas` criada em `12-fichas.csv` e campo em `12-fichas.md`; `camada_max` dos resumos agora definitiva (63 `local`, 10 `secreto`). 0 segredo em resumo, 0 fato novo. **ASSUMINDO:** critério de classificação e "um caminho basta" no restrito.

## Resultado do R5-6.1 (07/11/2026) — D-166

Novos `14-resumos-de-contexto.md` e `.csv`: **73 entidades** (10 cidades, 15 facções, 10 relógios, 38 pessoas), cada uma com resumo de até 500 caracteres (maior: 476), ID e vizinhança em uma linha (maior: 181). 0 entidade sem resumo, 0 segredo ou `nunca_diz` nos resumos, 0 duração de rota ou número, 0 fato novo. **ASSUMINDO:** `camada_max` provisória até o R5-6.2.

## Testes de fim da Fase 5 (07/11/2026)

Locais, sem rede: 4 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos), iguais aos salvos; 15 CSV de Azeroth íntegros; 46 IDs novos únicos (`rep.`, `canal.`, `mist.`, `seed.` e demais), 0 colisão com os reinos 1 a 4; referências de entidade, relógio, evento e rota dos 3 CSV novos todas válidas; reinos 1 a 4, `referencia/`, `referencia-tecnica/` e `mundo/` idênticos à base; `plano/ferramentas/` sem alteração. 0 falha, 0 regressão.

## Resultado do R5-5.5 (07/11/2026) — D-165; fim da Fase 5

Novos `13-misterios-limites.md` e `.csv`: **10 mistérios** (MA-01 a MA-10, um por lugar) como dados, com fatos confirmados, 4 hipóteses abertas, "a verdade não pode ser", evidência (duas provas independentes), entidades vivas do registro e ligação ao relógio e aos eventos do lugar. 0 hipótese eleita, 0 ID inexistente, 0 mistério ou veto acrescentado, 0 NPC, lugar, facção ou rota novo.

## Resultado do R5-5.4 (07/11/2026) — D-164

Novos `13-reacao-ao-jogador.md`, `13-reputacao.csv` (25 linhas: 10 lugares e 15 facções) e `13-canais.csv` (11 canais): ganho e perda de disposição por entidade, 5 estados de procurado, 10 atos ligados às leis L-01 a L-10, favor, reparação qualitativa e intermediários ligados às rotas do doc 07. 0 ID inexistente ou falecido, 0 rota fora do doc 07, 0 campo vazio, 0 número, 0 reação dependente de raça, 0 NPC, lugar, facção, ofício ou rota novo. **ASSUMINDO:** mapeamento de ato para reação; sanção de L-06 e L-08.

## Resultado do R5-5.3 (07/11/2026) — D-163

Novos `13-sementes-de-aventura.md` e `.csv`: **30 sementes** (10 lugares × pequena, média e grande; as médias são OA-01 a OA-10, as pequenas vêm dos rumores e as grandes dos conflitos CFA-01 a CFA-10), cada uma com gancho, agentes vivos do registro, abordagens possíveis, "se ninguém age" e ligação a relógio ou evento. 0 NPC inexistente, 0 solução predefinida, 0 semente com outro reino. **ASSUMINDO:** níveis de ameaça. 0 NPC, lugar, facção ou fato novo.

## Resultado do R5-5.2 (07/11/2026) — D-162

Novos `12-eventos.md` e `12-eventos.csv`: **12 eventos-tipo** (10 completam um relógio, 2 só dão gatilho) com cadeia causal, lugares, recursos qualitativos do doc 08, pessoas e facções e rotas; 0 ID inexistente, 0 rota fora do doc 07 (6 usadas, nenhuma "Pendente"), 0 evento com outro reino, 0 colisão com os reinos 1 a 4. **ASSUMINDO:** a ordem dos elos. 0 NPC, lugar, facção, relógio ou rota novo.

## Resultado do R5-5.1 (07/11/2026) — D-161

Novos `12-relogios.md` e `12-relogios.csv`: os **10 relógios** CA-01 a CA-10 do doc 06 com gatilhos observáveis, "o que move", "o que trava", quem paga, decisão no segmento 5, efeito da inação e custo por segmento; 12 colunas, 0 campo vazio, 0 entidade fora do registro, 0 relógio que envolva outro reino ou avance por calendário, 0 colisão de ID com os reinos 1 a 4. 0 relógio, NPC, lugar ou fato novo.

## Testes de fim da Fase 4 (07/11/2026)

Locais, sem rede: 4 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos; 7, 6 e 2 exceções), iguais aos salvos; 9 CSV de Azeroth íntegros; 63 IDs únicos, 0 colisão com os reinos 1 a 4, 0 referência inválida; reinos 1 a 4, `referencia/`, `referencia-tecnica/`, `mundo/` e `plano/ferramentas/` idênticos à base. 0 falha, 0 regressão.

## Resultado do R5-4.3 (07/11/2026) — D-159

Novos `12-relacoes.md`, `12-relacoes.csv` e `12-conhecimento.csv` em `reinos/05-azeroth/`: **91 relações** (26 alianças, 16 rivalidades, 6 dívidas, 10 tensões, 21 vínculos de cargo, 12 medos, 0 afeto), todas com fonte, e **88 linhas de conhecimento** (38 C, 12 S, 38 D) para as 38 pessoas. 0 relação sem origem, 0 aliança e rivalidade no mesmo par, 0 fato novo. **ASSUMINDO:** medos (fichas) e origem em Concharia e Estrelado (guardião, sem declarar chefe).

## Resultado do R5-4.2 (07/11/2026) — D-158

Novos `reinos/05-azeroth/12-competencias.md` e `12-competencias.csv`: **38 de 38** pessoas (12 principais, 18 sem ficha, 8 heróis lendários) com raça, sub-raça, classe e profissão de `ALINHAMENTO_AZEROTH.md`, capacidades e **limites** só do que os docs 01, 04 e `12-fichas` já dizem; 0 ID fora do catálogo, 0 campo vazio, 0 capacidade sem limite, 0 habilidade ou número inventado. Sem ficha, modo e meios do ofício ficam `desconhecidos`; heróis lendários seguem falecidos. **ASSUMINDO:** classe e profissão como leitura editorial (D-151). 0 NPC, nome, lugar ou facção novo.

## Resultado do R5-3.4 (07/11/2026) — D-156; fim da Fase 3

Doc 01, parte 5, `M-02` §9 e `M-10` §16: 12 luas numeradas, quatro estações (**Pousio, Sementeira, Ceifa, Ventania**, nomes provisórios, nenhuma de coroa), cinco **Dias de Cruzamento**, **Contagem das Chaves** (época `desconhecida`); início do jogo em Pousio, Lua 1, dia 1 do **ano 457**, igual a Silêncio, Lua 1, dia 1 do ano 742 de Aeltharyn (deslocamento de 285, ASSUMINDO, sem fonte, reversível); tabela de conversão de 8 linhas; agenda sazonal só de condição (tudo ASSUMINDO); Mesa de Meiovau, troca de governante, safra, cheia, As Costas e As Fugas ficam `desconhecidos`. Nenhum evento de Azeroth datado em anos de outros reinos.

## Testes de fim da Fase 3 (07/11/2026)

Locais, sem rede: 4 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos; 7, 6 e 2 exceções), iguais aos salvos; 4 CSV de Azeroth íntegros (63, 15, 10 e 10 linhas; 0 campo vazio em facções, lugares e demografia); 63 IDs únicos, 0 colisão com os reinos 1 a 4; 0 referência inválida entre docs; 10 `city.*` dos docs 08 e 09 no registro; 24 dependências (17 críticas); 0 nome do calendário repetido nos reinos 1 a 4; reinos 1 a 4, `referencia/`, `referencia-tecnica/` e `plano/ferramentas/` idênticos à base; `mundo/` só com M-02 §9 e M-10 §16 acrescentados. 0 falha, 0 regressão.

## Resultado do R5-3.3 (07/11/2026) — D-155

Novos `reinos/05-azeroth/08-registro-operacional-de-recursos.md` e `09-matriz-material-dos-lugares.md`: "rica em", "pobre em" e "depende de" para os **10 lugares** (só do doc 01 e do doc 05 §2), **24 dependências** (17 críticas, 7 de apoio; todo lugar com ao menos uma crítica, cada uma com fonte) e **4 fluxos** qualitativos do lado de cá (grão e mar, As Costas, As Fugas do Véu, aviso e hora). 0 número, nível, unidade, preço ou tempo; origem do sal, da madeira e do grão comprado `desconhecida`; pergunta do contrato §7 mantida aberta; 0 entidade, rota ou fluxo novos. **ASSUMINDO:** lugar-mãe como apoio (6 dependências não críticas); falta de madeira em Concharia como leitura de "vive da madeira de fora".

## Resultado do R5-3.2 (07/11/2026) — D-154

Novos `reinos/05-azeroth/12-demografia.md` e `.csv`: **10 faixas** de porte relativo já presentes no doc 01 (2 grandes, 4 médias, 4 pequenas; ASSUMINDO, D-63), com evidência e confiança `baixa`; regras DM-01 a DM-05 (faixa, não somar, todos os lugares fixos, flutuantes e chegados fora de faixa, proporção entre povos `desconhecida`). 0 número, 0 censo, 0 diferença com o doc 01. **ASSUMINDO:** faixa como porte relativo dentro do reino.

## Resultado do R5-3.1 (07/11/2026) — D-153

Novos `reinos/05-azeroth/07-registro-operacional-de-rotas.md` e `07-mapa-esquematico.md`: **14 ligações** (9 entre lugares, 5 de fluxo, rio ou sinais; 10 documentadas sem duração e 4 sugeridas por dependência), 10 acessos de lugares, 6 linhas de fronteira e mapa em Mermaid. Grafo dos 10 lugares conexo (por Meiovau e as três capitais). **Toda duração é `desconhecida`**: os docs 01, 03 e 05 não trazem "curta" nem "longa" para nenhum percurso, e nada foi inventado. Azeroth ↔ Aeltharyn, Véu, Coroas e Vale seguem sem rota (As Costas e As Fugas do Véu só como ponta de cá). **ASSUMINDO:** o vínculo de lugar-mãe como ligação "sugerida por dependência"; Meiovau ↔ capitais como "documentada" (a Mesa arbitra).

## Resultado do R5-2.4 (07/11/2026) — D-152

Novo `reinos/05-azeroth/13-conferencia-cruzada.md`: aliados e rivais (0 conflito, 0 assimetria, 0 aliada de rival), idades (12 fichas, sem número), **13 acúmulos de cargo** como intencionais (4 já declarados; **9 da tríade que lideram a facção local, achados e declarados no doc 04 §10**), quem sabe o quê nos 10 mistérios, 12 contradições e pontos abertos (CI-01 a CI-12). **ASSUMINDO:** os acúmulos são desenho intencional, como nos reinos 2 a 4.

## Testes de fim da Fase 2 (07/11/2026)

Locais, sem rede: 4 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos; 7, 6 e 2 exceções), relatórios idênticos aos salvos; reinos 1 a 4, `mundo/`, `referencia-tecnica/`, `plano/ferramentas/` e `referencia/` (exceto o novo alinhamento) idênticos à base; 3 CSV de Azeroth íntegros (63, 15 e 10 linhas; 12, 11 e 6 colunas em todas; 0 campo vazio em facções e lugares); 63 IDs únicos, 0 colisão com os reinos 1 a 4, 0 ID citado sem registro (a única ocorrência é `npc.syranna-das-mares`, de Aeltharyn, em veto); 0 referência inválida entre docs; 0 ID do alinhamento fora do catálogo. 0 falha, 0 regressão.

## Resultado do R5-2.3 (07/11/2026) — D-151

Novo `referencia/racas-e-povos/ALINHAMENTO_AZEROTH.md`: **9 nativos**, **8 sub-raças** e **25 forasteiros** conferidos contra `personagem-catalogo.json` (0 divergência de ID ou nome); raça, sub-raça, classe e profissão das **38 pessoas** (0 ID fora do catálogo; toda sub-raça no lugar que o doc 10 §2 a coloca). **ASSUMINDO:** classe e profissão como leitura editorial do papel escrito, profissão `—` sem ofício, **heróis lendários sem classe nem profissão** (sem feito escrito).

## Resultado do R5-2.2 (07/11/2026) — D-150

Novos `reinos/05-azeroth/12-facoes.csv` (15 facções) e `12-assentamentos.csv` (10 lugares), derivados só dos docs 03 e 04. 0 campo vazio, 0 ID inexistente, aliados e rivais simétricos, 0 aliado que seja rival, 1 rival `desconhecido` (F-07, com motivo). **ASSUMINDO:** `recursos` `desconhecido` nas 15, `lideranca_id` `desconhecido` em Concharia e Estrelado, `tensao` da facção igual à do lugar principal.

## Resultado do R5-2.1 (07/11/2026) — D-149

Novos `reinos/05-azeroth/12-registro-mestre.csv` (formato de D-21) e `12-registro-mestre.md`: **63 entidades** (10 lugares, 15 facções, 38 pessoas: 12 com ficha, 8 heróis lendários falecidos, 16 só da tríade, 2 líderes novos). As **26 pessoas sem ficha ganharam ID** pelo nome já escrito, sem criar pessoa. Conferido por script: 0 ID duplicado, 0 colisão com os registros mestres dos reinos 1 a 4, 0 `povo_id` fora do catálogo, 0 lugar inexistente, 0 ID citado nos docs 01, 03, 04, 05, 06 e 10 ausente do registro (exceto `npc.syranna-das-mares`, de Aeltharyn, só em veto). **ASSUMINDO:** regra de ID, residência dos líderes novos e ausência de ID para coroas, rios e instituições.

## Testes de fim da Fase 1 (07/11/2026)

Locais, sem rede: 4 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos; 7, 6 e 2 exceções), iguais aos relatórios salvos; `reinos/01` a `04`, `referencia/`, `referencia-tecnica/`, `mundo/` e `plano/ferramentas/` idênticos à base; referências entre os docs de Azeroth válidas; 10 lugares, 12 principais, 15 facções e 10 relógios sem ID duplicado nem colisão com os reinos 1 a 4 (a única ocorrência de ID de outro reino é veto no doc 10). 0 falha, 0 regressão. O validador e o conversor de Azeroth só existem na Fase 7 (R5-7.1).

## Fase 0 — Pré-requisitos e decisões (P0)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R5-0.1 Camada de mundo mínima** | a) M-05 já aprovado (9 nativos, 25 forasteiros); b) M-01 e M-02 já valem; c) calendário do reino (W-02) | M-02 estendido no R5-3.4 | nenhum texto do reino 5 depende de lore de mundo não aprovado |
| **R5-0.2 Decisões herdadas** | a) D-21, D-22, D-23; b) D-63 e D-64; c) D-99 e D-136 (rival `desconhecido` só com motivo) | `DECISOES.md` | decisões aplicadas ao modelo |
| **R5-0.3 Escala e identidade** | a) escala: quantos lugares e de que tipo; b) nome: **Azeroth** (decidido, D-139); c) governo; d) convenções de nome por povo (M-10, W-04); e) Estelar como nativo (M-05, pergunta 5) | D-139 (b decidida; a, c e e abertas) | autor aprova a, b, c e e |
| **R5-0.4 Contrato de fronteira** | a) fixos de Aeltharyn, do Véu, das Coroas e do Vale que tocam o reino 5; b) o que o reino 5 **não pode** dizer; c) ganchos permitidos; d) pontos abertos P-1 em diante | `plano/CONTRATO_DE_FRONTEIRA_PACTO_HUMANO.md` | lista conferida contra os docs 01, 05 e 07 dos quatro reinos e M-04 e M-05 |

## Fase 1 — Escrita editorial (P0)

Os módulos que falam em "cidades" e "assentamentos" valem na escala que o autor decidir em D-139.

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R5-1.1 Hierarquia e identidade dos lugares** | a) função de cada lugar coerente com o relevo (planícies férteis, rios largos, costa meridional) e com os vizinhos Véu e Aeltharyn; b) ficha padrão de lugar; c) convenções de nome | `01-...md`, `03` | fichas completas; nenhuma função sem geografia que a sustente |
| **R5-1.2 Assentamentos e posição territorial** | a) liderança, obrigação, interesse, tensão e lugar-mãe; b) posição relativa qualitativa | `03-assentamentos-e-rede-politica.md` | nenhum campo vazio; lugar-mãe existe |
| **R5-1.3 NPCs, identidades e conhecimento** | a) 8 a 12 principais (≥ 2 heróis, ≥ 2 generais); b) tríade herói, guardião e benfeitor; c) ficha padrão com confirmado, suspeita e desconhecido | seções do `01` | todo NPC com ID, povo do catálogo, agenda e pressão; nenhum nome já usado nos reinos 1 a 4 |
| **R5-1.4 Relações políticas e sociais** | a) governo (D-139), conselhos, sucessão, justiça, pactos; b) facções com objetivo, métodos, aliados e rivais **preenchidos** | `04-instituicoes-justica-pactos.md` | cada facção com aliados e rivais; sem aliado e rival ao mesmo tempo |
| **R5-1.5 História, economia, leis, defesa e cronologia** | a) eras locais; b) recursos, comércio e leis (qualitativo); c) defesas e vulnerabilidades; d) fluxos que tocam o reino, só do lado dele | `01`, `05` | cronologia sem contradição com os quatro reinos |
| **R5-1.6 Povos, linhagens, lugares e forasteiros** | a) os 9 nativos; b) sub-raças; c) lugares com ≥ 2 povos; d) forasteiros e fluxos; e) linhagens próprias; f) mestiços só pela regra de D-64 | `10-matriz-povos-nativos-cidades-e-forasteiros.md` | 100% em IDs do catálogo; nenhuma raça cidadã, vilã ou leal por natureza |
| **R5-1.7 Mistérios, relógios e oportunidades** | a) mistérios sem resposta única, com "a verdade não pode ser"; b) relógios iniciais, sem avanço automático; c) conflitos com causa concreta | `01` (parte 4), `06` (a ata `11` é do R5-8.2) | nenhum mistério explica a Neblina Longa, os Mistérios D e E, o Conflito G ou mistério de reino vizinho |

## Fase 2 — Registro mestre e consistência (P0)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R5-2.1 Registro mestre** | a) tabela única no formato de D-21; b) IDs únicos contra os reinos 1 a 4; c) catálogo conferido | `12-registro-mestre.*` | 0 ID duplicado; 0 ID citado inexistente |
| **R5-2.2 Facções e lugares como dados** | a) facções com todos os campos; b) lugares com lugar-mãe | `12-facoes`, `12-assentamentos` | nenhum campo vazio sem `desconhecido` e motivo |
| **R5-2.3 Povos e catálogo** | a) alinhar os 9 nativos ao `personagem-catalogo.json`; b) raça, sub-raça, classe, profissão por NPC | `referencia/racas-e-povos/ALINHAMENTO_<REINO>.md` | 0 ID fora do catálogo |
| **R5-2.4 Conferência cruzada** | a) aliado e rival ao mesmo tempo; b) idades e cargos duplicados; c) quem sabe o quê; d) contradições intencionais registradas | `13-conferencia-cruzada.md` | lista vazia ou toda marcada como intencional |

## Fase 3 — Lacunas de evidência, em escala qualitativa (P1)

Por D-63, esta fase **não escreve números**.

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R5-3.1 Geografia e deslocamento** | a) rotas internas só com "curta", "longa" ou `desconhecida`; b) acessos dos lugares; c) ligações com outros reinos **só** as do contrato; d) mapa esquemático | `07-registro-operacional-de-rotas.md`, `07-mapa-esquematico.md` | nenhuma rota ou tempo inventado; grafo conexo ou exceção declarada |
| **R5-3.2 Demografia** | a) população pequena, média ou grande; b) regra "não somar" | `12-demografia.*` | nenhum total apresentado como censo |
| **R5-3.3 Recursos** | a) "rica em", "pobre em", "depende de"; b) dependências entre lugares; c) fluxos, só em termos qualitativos | `08`, `09` | nenhum lugar funciona isolado; cada dependência crítica com fonte |
| **R5-3.4 Calendário e estações** | a) estações do reino; b) tabela de conversão com Aeltharyn (W-02) | seção no `01`, `M-02` | ano e estação inicial compatíveis com o ano 742 de Aeltharyn |

## Fase 4 — Profundidade dos personagens (P1)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R5-4.1 Fichas completas** | a) todos os campos; b) agenda e pressão distintas por NPC | `12-fichas.*` | 100% dos principais completos |
| **R5-4.2 Competências pelo catálogo** | a) capacidades e **limites**; b) IDs do catálogo | `12-competencias.*` | todo ID existe no JSON |
| **R5-4.3 Relações e conhecimento** | a) matriz (afeto, dívida, rivalidade, medo); b) confirmado, suspeita, desconhecido | `12-relacoes.*`, `12-conhecimento.csv` | nenhuma relação sem origem |
| **R5-4.4 Voz e ganchos de diálogo** | a) 2 a 3 frases-modelo por principal; b) o que nunca diz | `12-voz.*` | usável como instrução de tom |

## Fase 5 — Mundo vivo (P1)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R5-5.1 Relógios** | a) gatilhos; b) custo por segmento; c) efeito da inação | `12-relogios.*` | cada relógio com "o que o move" e "o que o trava"; nenhum envolve outro reino |
| **R5-5.2 Cadeias causais** | a) 10 a 15 eventos-tipo; b) efeitos em IDs existentes e em ligações entre locais que existem | `12-eventos.*` | nenhum efeito aponta ID inexistente |
| **R5-5.3 Sementes de aventura** | a) por lugar: pequena, média, grande; b) sem solução predefinida | `13-sementes-de-aventura.*` | nenhuma semente depende de NPC inexistente |
| **R5-5.4 Reação do reino ao jogador** | a) fama e reputação qualitativas; b) procurado, favor, reparação; c) intermediários | `13-reputacao.csv`, `13-reacao-ao-jogador.md`, `13-canais.csv` | liga ao plano do jogo |
| **R5-5.5 Mistérios com limites** | a) sem resposta única; b) "a verdade não pode ser" | `13-misterios-limites.*` | nenhum mistério contradiz fato confirmado nem os mistérios dos reinos vizinhos |

## Fase 6 — Pacote de contexto para IA (P0 para a integração)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R5-6.1 Fichas curtas** | a) ≤ ~500 caracteres por entidade; b) vizinhança em 1 linha | `14-resumos-de-contexto.*` | toda entidade com resumo e ID |
| **R5-6.2 Camadas de informação** | a) público, local, restrito, secreto por fato; b) como descobrir | `14-camadas-de-informacao.*` | segredo nunca em resumo público |
| **R5-6.3 Cenas-teste** | a) 6 a 7 cenas cobrindo cidade, porto ou travessia de rio, assembleia, disputa, viagem e fronteira (só do lado do reino 5) | `15-cenas-de-teste.*` | cada cena com "entra" de até ~2.400 caracteres e "não entra" |
| **R5-6.4 Contrato anti-invenção** | a) fatos fixos, livres e proibidos; b) novo chefe, lugar ou guerra exige registro | seção no doc 06 | regra clara |

## Fase 7 — Preparação para implementar (P1)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R5-7.1 Dados e validador** | a) dados no formato de D-21 (adaptar `converter_reino4.py` para o reino 5, sem alterar os reinos 2, 3 e 4); b) rodar o validador | `converter_reino5.py`, `dados/`, relatório | 0 erro; avisos listados |
| **R5-7.2 Catálogo técnico** | a) verificar o que o `.kt` tem sobre o reino 5; b) mapa de conversão, sem alterar código | `MAPA_CATALOGO_KOTLIN.md` | divergências listadas, nada instalado |
| **R5-7.3 Protótipo mínimo** | a) lugar pequeno + 1 NPC + lugar de mercado em 1 linha; b) condições com e sem mundo; c) métricas fixadas antes de jogar | `PROTOTIPO_MINIMO.md` (seção do reino 5) | critério de aceite escrito antes da sessão |

## Fase 8 — Revisão e fechamento (P0)

| Módulo | Submódulos | Entregável | Critério de aceite |
|---|---|---|---|
| **R5-8.1 Revisão final** | a) checklist de `MODELO_DE_REINO.md` §6; b) contradições intencionais × acidentais | `RELATORIO_R5_8_1.md` | todas as portas marcadas |
| **R5-8.2 Ata de fechamento 6** | a) doc 11 do reino 5; b) fechar o anel; c) conferir o conjunto dos cinco reinos (IDs únicos entre reinos, fronteiras) | doc 11 | autor aprova |

## Ordem e dependências

`F0 → F1 → F2 → (F3 ∥ F4) → F5 → F6 → F7 → F8`. A F2 roda **junto** da F1. A F6 pode começar em paralelo com a F5 para as cenas-teste. A F1 não começa sem R5-0.3 (D-139) aprovado e R5-0.4 escrito.

## Primeira rodada

1. **R5-0.1** e **R5-0.2** (feitos, sem decisão do autor).
2. **R5-0.4** contrato de fronteira (**feito**, D-140).
3. **R5-0.3** (**feito**, D-141): escala, governo e Estelar. **Roadmap concluído (37/37); resta a aprovação do autor (D-174).**

## Andamento

| Módulo | Estado | Observação |
|---|---|---|
| R5-0.1 Camada de mundo mínima | **feito** | M-05 (9 nativos, 25 forasteiros, 16 sem papel), M-01 e M-02 valem; calendário fica para o R5-3.4; Estelar levado ao R5-0.3 |
| R5-0.2 Decisões herdadas | **feito** | D-21 a D-23, D-63, D-64, D-99, D-136 e D-110 aplicadas (tabela no plano); sem decisão nova |
| R5-0.4 Contrato de fronteira | **feito** | `CONTRATO_DE_FRONTEIRA_PACTO_HUMANO.md`; 6 pontos abertos (P-1 a P-6); D-140 |
| R5-0.3 Escala e identidade | **feito** | 10 lugares (ASSUMINDO), várias coroas rivais (autor; 3 ASSUMINDO), Estelar nativo (ASSUMINDO); M-10 §11; D-141. **Fase 0 concluída** |
| R5-1.1 Hierarquia e identidade dos lugares | **feito** | 10 lugares, 3 coroas, Meiovau; `reinos/05-azeroth/01-...md` parte 1; D-142 |
| R5-1.2 Assentamentos e posição territorial | **feito** | doc 03; D-143 |
| R5-1.3 NPCs, identidades e conhecimento | **feito** | 12 principais, 8 heróis lendários, tríade dos 10 lugares; parte 2 do doc 01; D-144 |
| R5-1.4 Relações políticas e sociais | **feito** | doc 04; 15 facções; D-145 |
| R5-1.5 História, economia, leis, defesa e cronologia | **feito** | parte 3 do doc 01 e doc 05; D-146 |
| R5-1.6 Povos, linhagens, lugares e forasteiros | **feito** | doc 10; 9 nativos, 25 forasteiros, 8 sub-raças; D-147 |
| R5-1.7 Mistérios, relógios e oportunidades | **feito** | parte 4 do doc 01 (10 mistérios) e doc 06 (10 relógios, 10 conflitos, 10 oportunidades); D-148. **Fase 1 concluída** |
| R5-2.1 Registro mestre | **feito** | `12-registro-mestre.md` e `.csv`: 10 lugares, 15 facções, 38 pessoas (63 IDs únicos); D-149 |
| R5-2.2 Facções e lugares como dados | **feito** | `12-facoes.csv` (15) e `12-assentamentos.csv` (10); D-150 |
| R5-2.3 Povos e catálogo | **feito** | `ALINHAMENTO_AZEROTH.md`; 38 pessoas atribuídas; D-151 |
| R5-2.4 Conferência cruzada | **feito** | `13-conferencia-cruzada.md`; 13 acúmulos, CI-01 a CI-12; D-152. **Fase 2 concluída** |
| R5-3.1 Geografia e deslocamento | **feito** | `07-registro-operacional-de-rotas.md` e `07-mapa-esquematico.md`; 14 ligações, durações `desconhecidas`; D-153 |
| R5-3.2 Demografia | **feito** | `12-demografia.md` e `.csv`; 10 faixas, sem número; D-154 |
| R5-3.3 Recursos | **feito** | `08-registro-operacional-de-recursos.md` e `09-matriz-material-dos-lugares.md`; 10 lugares, 24 dependências (17 críticas), 4 fluxos; D-155 |
| R5-3.4 Calendário e estações | **feito** | doc 01 parte 5, `M-02` §9, `M-10` §16; 4 estações, 5 Dias de Cruzamento, Contagem das Chaves (ano 457); D-156. **Fase 3 concluída** |
| R5-4.1 Fichas completas | **feito** | `12-fichas.md` e `.csv`: 12 de 12 principais, 16 campos, 12 agendas e 12 pressões distintas, 4 acúmulos marcados; D-157 |
| R5-4.2 Competências pelo catálogo | **feito** | `12-competencias.md` e `.csv`: 38 de 38 pessoas, 0 ID fora do catálogo, 0 campo vazio; D-158 |
| R5-4.3 Relações e conhecimento | **feito** | `12-relacoes.md` e `.csv` (91 relações, 0 sem fonte) e `12-conhecimento.csv` (88 linhas, 38 pessoas); D-159 |
| R5-4.4 Voz e ganchos de diálogo | **feito** | `12-voz.md` e `.csv`: 12 de 12, 3 frases cada; D-160. **Fase 4 concluída** |
| R5-5.1 Relógios | **feito** | `12-relogios.md` e `.csv`: 10 relógios, 0 envolve outro reino; D-161 |
| R5-5.2 Cadeias causais | **feito** | `12-eventos.md` e `.csv`: 12 eventos-tipo, 0 ID inexistente, 6 rotas documentadas; D-162 |
| R5-5.3 Sementes de aventura | **feito** | `13-sementes-de-aventura.md` e `.csv`: 30 sementes (10 P, 10 M, 10 G), 0 NPC inexistente; D-163 |
| R5-5.4 Reação do reino ao jogador | **feito** | `13-reacao-ao-jogador.md`, `13-reputacao.csv` (25) e `13-canais.csv` (11); 0 ID inexistente, 0 rota fora do doc 07; D-164 |
| R5-5.5 Mistérios com limites | **feito** | `13-misterios-limites.md` e `.csv`: 10 mistérios, 4 hipóteses cada, 0 ID inexistente; D-165. **Fase 5 concluída** |
| R5-6.1 Fichas curtas | **feito** | `14-resumos-de-contexto.md` e `.csv`: 73 resumos (maior 476 caracteres), 0 sem ID; D-166 |
| R5-6.2 Camadas de informação | **feito** | `14-camadas-de-informacao.md` e `.csv`: 44 fatos (10 públicos, 12 locais, 2 restritos, 20 secretos), 0 segredo em resumo; D-167 |
| R5-6.3 Cenas-teste | **feito** | `15-cenas-de-teste.md` e `.csv`: 7 cenas (maior "entra" 2103 caracteres), 0 segredo, 0 relógio; D-168 |
| R5-6.4 Contrato anti-invenção | **feito** | §9 do doc 06: 4 classes, proibições, casos pedidos e teste rápido; D-169; **F6 concluída** |
| R5-7.1 Dados e validador | **feito** | `converter_reino5.py`, `dados/` (21 `.txt`, 485 registros), relatório: 0 erro, 7 avisos; D-170 |
| R5-7.2 Catálogo técnico | **feito** | seção 9 de `MAPA_CATALOGO_KOTLIN.md`: KA1 a KA9, 0 entidade de Azeroth no `.kt`, 0 colisão; D-171 |
| R5-7.3 Protótipo mínimo | **feito** | §13 de `PROTOTIPO_MINIMO.md`: Estrelado, Ixra, Vaudouro em 1 linha; contexto B de 1.375 caracteres; Q1 a Q10 e critério de aceite escritos, nada executado; D-172. **Fase 7 concluída** |
| R5-8.1 Revisão final | **feito** | `RELATORIO_R5_8_1.md`: portas 1 a 10 marcadas, 11 pendente; 12 contradições intencionais; D-173 |
| R5-8.2 Ata de fechamento 6 | **feito** | `11-fechamento-de-azeroth.md`; 11 de 11 portas, anel dos cinco reinos conferido; D-174 aguarda o autor. **Fase 8 concluída** |

**Progresso:** 37 de 37 módulos (100%); Fases 0 a 8 concluídas. **Próximo: aprovação do autor (D-174).** Plano criado em 07/11/2026 (D-138); nome decidido em D-139.
