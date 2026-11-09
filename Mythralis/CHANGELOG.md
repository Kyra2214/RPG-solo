# Changelog

## 2026-11-08 — documentação: estado real da integração

- `README.md`: a integração com o app deixou de ser descrita como "só plano"; passa a listar o que existe (`motor/exportar_molde.py`, `motor/exportar_vetores.py`, módulo `../world`, molde em `../rules`) e o que falta (uso pelo app, MI-09 em diante). Sem mudança de código nem de dados. Suíte Python reexecutada: 62 testes OK.

## 2026-11-08 — vetores dos motores de pessoas (MI-05 da integração)

- `motor/exportar_vetores.py` gera também `../world/src/test/resources/conformidade/vetores-motores-pessoas.txt`. Motor Python inalterado.

## 2026-11-08 — vetores de estado e calendário (MI-04 da integração)

- `motor/exportar_vetores.py` gera também `../world/src/test/resources/conformidade/vetores-estado.txt` (147 vetores). Motor Python inalterado.

## 2026-11-08 — vetores de conformidade RNG/JSON (MI-03 da integração)

- `motor/exportar_vetores.py` (novo): gera `../world/src/test/resources/conformidade/vetores-rng-json.txt` (276 vetores do Python para o Kotlin). Motor Python inalterado.

## 2026-11-08 — exportador do molde para o app (MI-01 e MI-02 da integração)

- `motor/exportar_molde.py` (novo): exporta o molde, a dinâmica e `derivados/subracas-em-prosa.txt` para `rules/src/main/resources/mundos/mythralis/` com `manifesto.txt`; o hash do molde (`f11f1c120acc99b1`) não muda. Nenhum dado do molde foi alterado. D-183 em `plano/DECISOES.md`.

## 2026-11-08 — documentação sincronizada

- `README.md` e `plano/PLANO_MOTOR_MYTHRALIS.md`: estado corrigido (motor fechado, F0 a F9, D-180 a D-182) e ponteiro para `../docs/PLANO_INTEGRACAO_MYTHRALIS.md`. Sem mudança de código nem de dados.

## 2026-11-08 — F9 concluída: motor fechado

- D-182: sem carência entre quedas de coroa (segue D-177). Sem mudança de código. Fase F9 concluída (F9.1, F9.2 e D-180 a D-182); sem pendência do autor no motor.
- Testes completos da fase: 62 passam (executados após a F9.1; F9.2 só adiciona chave desligada). Lote 500 anos × 20 sementes sem falha.

## 2026-11-08 — D-181: alianças de partida seguem neutras

- Decisão do autor: nenhum par de povos nasce aliado; sem mudança de código nem de dados.

## 2026-11-08 — Aceite da F7 e da F8

- Autor aceitou a F7 e a F8 como estão (D-180). `README.md`, `PLANO_MOTOR_MYTHRALIS.md` (§15) e `DECISOES.md` atualizados. Sem mudança de código.

## 2026-11-08 — F9.2 do motor: regra do rei testada e desligada

- `niveis.py`: chave `GOVERNANTE_DE_CIDADE_N1` (governante de toda cidade no nível 1). Testada em 6 sementes × 500 anos (sem falha): nível 1 com mediana 6,5 e máximo 24 a 36 por reino e década, acima da meta de 25. Chave deixada em `False`; comportamento, crônica e hashes iguais à F9.1 (D-179).
- ASSUMINDO: meta de nível 1 da §14 mantida. Testes completos não reexecutados (mudança só de leitura, desligada por padrão).

## 2026-11-08 — F9.1 do motor: sucessão conta como um evento

- `niveis.py`: `Niveis.evento` agrupa os deltas de uma sucessão (mesmo dia e posto); `niveis.md` (resumo por década) e a métrica `n12_fora` de `lote.py` contam um evento por sucessão. Crônica, hashes, motores e `dinamica/` intactos.
- ASSUMINDO: meta 3 a 40 mantida; efeito confirmado no lote (executado depois: 62 testes passam; lote 500 anos × 20 sementes sem falha; fora de 3 a 40 em 4% a 8% das décadas, mediana 5,5%; demais métricas iguais ao F8.4). Decisão D-178. Pendentes do autor: aceite F7/F8, carência entre quedas, regra do rei, alianças de partida.

## 2026-11-08 — F8 do motor: níveis de relevância, alianças entre povos e fechamento

- **F8.1 níveis (D-M14, D-175):** `motor/niveis.py` (leitura derivada dos deltas; nada novo na crônica nem nos hashes). Cada rodada gera `niveis.md` (nível 1 por evento, nível 2 resumo por década e reino, nível 3 contagem na virada de era, total por cidade) e `metricas_cidades.csv`. Invariante I-28. `lote.py`: métricas por nível, pactos e cidades sem evento.
- **F8.2 povos (D-M15, D-176):** `motor/motores/povos.py` e `dinamica/povos.txt` (17 chaves `PROPOSTO`, no gerador). 713 laços locais (46 cidades, raças e sub-raças que convivem) e 131 pares candidatos a pacto entre povos; tudo nasce neutro (lacuna do molde). Invariantes I-26 e I-27. `estado.py`, `dinamica.py`, `importancia.py`, `relatorio.py` e `motores/__init__.py` ajustados; versão do motor `F8.2`.
- **F8.3 quedas (D-177):** sem recalibragem; 19 de 20 sementes dentro de 0 a 2 quedas, semente 18 documentada (`plano/RELATORIO_F8_3.md`).
- **F8.4 fechamento:** lote 500 anos × 20 sementes sem falha; metas da §14 reescritas por nível e verificadas (`plano/RELATORIO_F8_4.md`).
- **62 testes passam** (45 anteriores + 17 em `motor/testes/test_f8.py`). Molde, reinos e documentos canônicos não foram alterados.
- ASSUMINDO: "rei" = governante ou herdeiro que vive na capital do reino; aliança entre povos começa neutra. Pendências do autor: aceite de F7 e F8; carência opcional entre quedas de coroa; decidir se governantes de cidades que não são capital sobem ao nível 1.

## 2026-11-07 — F7.3 do motor: guerras, quedas de coroa e regime aberto

- Novos: `motor/motores/guerra.py` (anual) e `dinamica/guerra.txt` (8 chaves `guerra_*` e `queda_*`, geradas por `gerar_dinamica.py`). `motores/__init__.py`: `Guerra` em `criar()`. `dinamica.py`: `Dinamica.guerra`. `estado.py`: entidades `crown.<reino>`. `fronteira.py` e `diplomacia.py`: ignoram par em guerra. `relatorio.py`: `guerra`, `queda_de_coroa` e `crown.*`. `simulador.py`: versão F7.3. `dinamica/LEIA-ME.md`.
- Regra: par em ruptura com tensão alta e coroas firmes entra em guerra; a primeira guerra abre o regime `aberto` (era.003); baixas por morte de principais e generais; queda de coroa (nova era) ou paz por tratado após 6 anos. Tudo com causa. Molde, reinos e os outros motores intactos; só compilação e uma rodada de conferência (4 sementes × 300 anos, 30 blocos `OK` cada; primeira guerra nos anos 65 a 103; 6 a 9 guerras e 0 a 2 quedas por semente); sem testes (só ao fim da F7).
- ASSUMINDO: valores das chaves (`PROPOSTO`); `guerra_tensao_min` 240 (330 nunca era alcançado, pois o incidente dispara em 300); coroa caída não volta; era nova a cada queda. Pendências: `test_f5` (Era I aos 121 anos) e calibragem de quedas (3 de 4 sementes com 2 em 300 anos). Motor: **F7.3 de 4 submódulos da F7 (87%)**; próximo: F7.4 (tramas, I-23 em diante e calibragem 500 anos × 20 sementes; fecha a F7).

## 2026-11-08 — F7.4 do motor: invariantes, tramas existentes e calibragem inicial

- `invariantes.py`: I-23 (transições de fronteira), I-24 (guerra e queda de coroa) e I-25 (gatilhos de era), conectados ao simulador F7.4. `test_f7.py` cobre as regras, cadeias causais existentes e a integração. “Tramas” reutiliza as cadeias de eventos do molde; não foi inventada uma mecânica narrativa ou lore nova.
- Correção: uma baixa podia matar um NPC que, no mesmo ciclo anual, era listado como agente do tratado de paz. `Guerra` agora exclui vítimas agendadas da seleção de agentes e de novas baixas; há teste determinístico. `queda_pm` foi calibrada de 20‰ para 10‰ por ano, sincronizada no gerador e em `dinamica/guerra.txt` (`PROPOSTO`).
- **45 testes passam**. Lote 500 anos × 20 sementes: 20/20 rodadas `OK`, 50 blocos por semente, nenhum relatório de saúde/invariantes com falha. Primeiro incidente: anos 41–62; primeira guerra: anos 55–181; mistérios resolvidos: 0. 19/20 sementes tiveram 0–2 quedas, mas a semente 18 teve 3. O máximo de deltas importantes (3+) por reino/década foi 82–97, acima da meta 40 em 20/20 sementes. Detalhes em `plano/RELATORIO_F7_4.md` e nos resultados por semente.
- **F7.4 implementada; aceite global da F7 permanece pendente.** A F8 deve decidir como tratar a métrica de eventos por década e concluir a calibragem sem mascarar os desvios; a F7 não foi marcada como plenamente aceita.

## 2026-11-07 — F7.2 do motor: tratados, rupturas e quebras de contrato

- Novos: `motor/motores/diplomacia.py` (anual) e `dinamica/diplomacia.txt` (10 chaves `dipl_*`, geradas por `gerar_dinamica.py`). `motores/__init__.py`: `Diplomacia` em `criar()`. `dinamica.py`: `Dinamica.dipl`. `estado.py`: pares `front.*` com `relacao`, `relacao_dia` e `pendente`. `fronteira.py`: marca o incidente como `pendente` e aplica o fator de tensão por relação. `relatorio.py`: `quebra_de_contrato`. `simulador.py`: versão F7.2.
- Regra: incidente pendente vira tratado (agentes: governantes vivos dos dois reinos), ruptura ou esfria; tratado dura 30 anos e pode ser quebrado (`quebra_de_contrato` e ruptura); ruptura dura 12 anos. Só fora do regime selado; tudo com causa. Molde, reinos e os outros motores intactos; só compilação e uma rodada de conferência (4 sementes × 200 anos, 20 blocos `OK` cada; 22 a 27 tratados, 5 a 15 rupturas e 9 a 17 quebras de contrato por semente); sem testes (só ao fim da F7).
- Correções de erros meus da F7.1: a versão do motor ficara F6.4 em `simulador.py` (agora F7.2) e o plano citava o primeiro incidente nos anos 84 a 100 quando o ano-base é 742 (são os anos 42 a 58 desde o início).
- ASSUMINDO: valores das chaves `dipl_*` (`PROPOSTO`); agente de tratado = governante vivo da primeira cidade com governante vivo de cada reino; uma transição por par por ano. Pendência: `test_f5` (Era I aos 121 anos). Motor: **F7.2 de 4 submódulos da F7 (84%)**; próximo: F7.3 (guerras, quedas de coroa e regime aberto).

## 2026-11-07 — F7.1 do motor: tensão entre reinos vizinhos e incidentes de fronteira

- `motores/fronteira.py` reescrito (anual); `estado.py`: `ANEL` e `_fronteiras` (5 pares `front.<a>+<b>`, só com dinâmica); `relatorio.py`: `front.*` e `incidente_fronteira`; `lote.py`: metas em anos desde o início; `plano/ferramentas/gerar_dinamica.py` e `dinamica/fronteira.txt`: 6 chaves novas (`front_tensao_*`); `dinamica/LEIA-ME.md`. Versão do motor F7.1.
- Regra: tensão por par sobe com a base e com os relógios estourados dos dois reinos; no limiar vira `incidente_fronteira` com causa; o primeiro incidente encerra a Era I (regime `poroso`, causa `primeiro-incidente-de-fronteira`); limite de 120 anos mantido. Molde, reinos e os outros motores intactos; só compilação e uma rodada de conferência (5 sementes × 130 anos, 13 blocos `OK` cada); sem testes (só ao fim da F7).
- ASSUMINDO: decomposição da F7 em F7.1 a F7.4; anel = Coroas de Pedra, Aeltharyn, Azeroth, Véu dos Malditos, Vale Palancar; tensão em pontos e valores das chaves (`PROPOSTO`); par pertence ao primeiro reino do nome. Pendência: `test_f5` (Era I aos 121 anos) a ajustar. Motor: **F7.1 de 4 submódulos da F7 (81%)**; próximo: F7.2 (tratados, rupturas e quebras de contrato).

## 2026-11-07 — F6.4 do motor: retomada `+anos` e CLI; fim da Fase 6

- `simulador.py`: `--retomar <rodada> --anos +N`, `--ate-ano A`, `--anos`/`--ate-ano` exclusivos; retomada usa o `--passo` da rodada e recupera alertas e violações acumulados (`progresso.json`: `passo`, `viol`, `alert`); Ecos antigos reconhecidos por reino; rodada em FALHA não retoma; erros de uso com código 2; versão F6.4. `lote.py`: incidente de fronteira = só `incidente_fronteira` (as `ruptura` e `disputa_aberta` da política são internas). `testes/test_f4.py`: um teste ajustado ao alerta de I-16 a I-18.
- **Testes completos de fim de F6** (locais, sem rede): scripts compilam; **37 testes passam** (12 da F3, 16 da F4, 9 da F5); 100 anos × 3 sementes: 10 blocos `OK` cada; 10 + 10 anos retomados = 20 anos direto (hash da crônica e do estado iguais); lote 2 × 20 anos gera `lote.md` e `lote.csv`; validação do mundo 0 erro e 20 avisos (hash f11f1c120acc99b1); `reinos/`, `mundo/`, `referencia/`, `referencia-tecnica/`, `plano/ferramentas/` e `dinamica/` idênticos à base. 0 falha, 0 regressão.
- ASSUMINDO: com `--retomar`, `N` e `+N` valem o mesmo. Plano: F6 ✔. Motor: **7 de 9 fases (F0 a F6, 78%)**; próximo: F7 (regimes poroso e aberto: tratados, rupturas, guerras, tramas).

## 2026-11-07 — F6.3 do motor: modo lote

- Novo `motor/lote.py`: N sementes sobre o mesmo molde (`--lote N`, a partir de `--semente`), rodadas em `saidas/<lote>/rodada-s<S>/`, `lote.csv` e `lote.md` (fim da Era I, primeiro incidente, primeira guerra, reinos caídos por semente e desequilíbrio, mistérios resolvidos, relógios que nunca ou sempre estouram, mínimo e máximo de deltas 3+ por reino por década). `simulador.py`: opção `--lote`; versão F6.3. Molde, dinâmica, motores, invariantes e relatório intactos; só compilação (sem testes até o fim da F6).
- ASSUMINDO: `incidente_fronteira` = incidente (corrigido na F6.4), `guerra` = guerra e `queda_de_coroa` = reino caído (F7 os emite; antes disso saem vazios); uma rodada com FALHA não interrompe o lote. Motor: **F6.3 de 4 submódulos da F6 (75%)**; próximo: F6.4 (retomada `+anos` e CLI).

## 2026-11-07 — F6.2 do motor: invariantes I-12, I-16, I-17, I-18 e I-20; consultas obrigatórias

- Novos: `motor/consultas.py` (linha do tempo, cadeia causal, titular em um ano, relógio em um ano; CLI) e `dinamica/invariantes.txt` (2 chaves `PROPOSTO`, geradas por `gerar_dinamica.py`). `invariantes.py`: `checar_bloco` e os cinco invariantes em `IMPLEMENTADOS`; `PENDENTES` vazio. `simulador.py`: chama `checar_bloco` a cada bloco (alertas em `saude.txt`; I-12 e I-20 são erro); versão F6.2. `dinamica.py`: `Dinamica.inv`.
- Molde, motores e relatório intactos; só compilação (sem testes até o fim da F6).
- ASSUMINDO: população medida por NPCs vivos; evento = delta de importância 3+; mínimo de 3 por década e teto de 60 por ano. Motor: **F6.2 de 4 submódulos da F6 (72%)**; próximo: F6.3 (modo lote).

## 2026-11-07 — F6.1 do motor: relatório por era e década, métricas anuais

- Novo `motor/relatorio.py` (`relatorio.md` e `metricas.csv`, derivados dos deltas; também como script para rodadas prontas). `simulador.py`: grava os dois ao fim de cada `rodar`; versão F6.1. Molde, dinâmica, motores e invariantes intactos; sem testes (só ao fim da F6).
- ASSUMINDO: decomposição da F6 em F6.1 a F6.4 (relatório e métricas; invariantes pendentes e consultas; lote; retomada e CLI); colunas das métricas; 15 marcos por década. Motor: **F6.1 de 4 submódulos da F6 (69%)**; próximo: F6.2.

## 2026-11-07 — F5 do motor: contratos de fronteira (F5.4); fim da Fase 5

- Novos: `motor/motores/fronteira.py` (anual), `dinamica/fronteira.txt` (limite de 120 anos, D-M4) e `motor/testes/test_f5.py` (9 testes). `invariantes.py`: I-10 por delta no regime selado (rota nova, Mistério D, agente de outro reino, dono de Torvelume); `PENDENTES`: I-12, I-16 a I-18 e I-20 (F6). `dinamica.py`: `Dinamica.front`. `motores/__init__.py`: `Fronteira`. `simulador.py`: versão F5.4.
- Correções da fase: `estado._economia` derivava o reino de uma coluna inexistente em `recurso` (agora `world.reino_de`); a escassez passou a ser o déficit acima do estrutural do molde (antes ~1.850 `revolta` por século; agora 0).
- **Testes completos de fim de F5** (locais, sem rede): scripts compilam; **37 testes passam** (12 da F3, 16 da F4, 9 da F5: estado inicial, rodada OK, mistério nunca resolvido, Eco restrito por rota, defeitos propositais de I-08, I-09 e I-10, fim da Era I aos 121 anos, determinismo e passo neutro); 100 anos × 3 sementes: 10 blocos `OK` cada; validação do mundo 0 erro e 20 avisos (hash f11f1c120acc99b1, molde intacto); `reinos/`, `mundo/`, `referencia/`, `referencia-tecnica/` idênticos à base e os 5 `.txt` anteriores de `dinamica/` idênticos. 0 falha, 0 regressão.
- ASSUMINDO: limite de 120 anos; escassez estrutural não é crise (o molde não diz de onde vem a importação); regras de I-10 por agente e titular. Plano: F5 ✔. Motor: **6 de 9 fases (F0 a F5, 67%)**; próximo: F6 (simulador completo, lote, relatório, retomada).

## 2026-11-07 — F5.3 do motor: mistérios, I-08 e I-09

- Novos: `motor/motores/misterios.py` (anual) e `dinamica/misterios.txt` (2 chaves `PROPOSTO`). `dinamica.py`: `Dinamica.mist`. `estado.py`: entidades `mist.*` derivadas do molde. `invariantes.py`: I-08 e I-09 por delta (saem de `PENDENTES`). `motores/__init__.py`: `Misterios` em `criar()`. `simulador.py`: versão F5.3.
- Regra: pistas sem agente apontam hipóteses do molde, até um teto; nenhuma resolução sem agente, evidência listada e verdade permitida. Molde e reinos intactos; sem testes (só ao fim da F5).
- ASSUMINDO: 7% de pista por ano, teto de 6, evidência conferida por trecho do texto do molde. Motor: **F5.3 de 4 submódulos da F5 (62%)**; próximo: F5.4 (contratos de fronteira).

## 2026-11-07 — F5.2 do motor: Ecos

- Novos: `motor/motores/ecos.py` (diária) e `dinamica/ecos.txt` (7 chaves `PROPOSTO`, geradas por `gerar_dinamica.py`). `dinamica.py`: `Dinamica.ecos`. `estado.py`: `Estado.aplicar` cria `eco.<delta>` para todo delta de importância 3+ e mantém `ecos_ativos` (derivado, não serializado). `importancia.py`: `difusao` = 0. `motores/__init__.py`: `Ecos` em `criar()`. `simulador.py`: versão F5.2.
- Regra: o Eco parte do lugar do fato e se espalha por rotas abertas do reino, mais devagar quanto mais restrita a camada; a certeza desce com os passos; cada passo é um delta `difusao` com `quem_sabe` e a rota na causa. Molde e reinos intactos; sem testes (só ao fim da F5).
- ASSUMINDO: camada por tipo de delta; lugar de origem sem lugar próprio = primeira cidade do reino; taxas e degraus de certeza. Motor: **F5.2 de 4 submódulos da F5 (60%)**; próximo: F5.3 (mistérios).

## 2026-11-07 — F5.1 do motor: economia e rotas

- Novos: `motor/motores/economia.py` (sazonal) e `dinamica/economia.txt` (13 chaves `PROPOSTO`, geradas por `plano/ferramentas/gerar_dinamica.py`). `dinamica.py`: `Dinamica.econ`. `estado.py`: entidades `route.*` e `econ.<recurso>@<lugar>` derivadas do molde (só com dinâmica). `importancia.py`: `rota_reaberta` (2) e `escassez` (2 no nível 3). `motores/__init__.py`: `Economia` em `criar()`. `simulador.py`: versão F5.1.
- Regras: bloqueio e reabertura de rota por estação, quebra e recuperação de safra, escassez por oferta local e por rota aberta, revolta após escassez máxima prolongada (homeostase). Só dentro dos reinos. Molde e reinos intactos; sem testes (só ao fim da F5).
- ASSUMINDO: decomposição da F5 em F5.1 a F5.4 (economia e rotas; Ecos; mistérios; contratos de fronteira); padrões de `?`; alcance, teto de importação e taxas. Motor: **F5.1 de 4 submódulos da F5 (58%)**; próximo: F5.2 (Ecos).

## 2026-11-07 — F4 do motor: dinâmica v0, demografia, sucessão, relógios, eventos e política; fim da Fase 4

- Novos: `dinamica/` (5 `.txt`, gerados por `plano/ferramentas/gerar_dinamica.py`), `motor/dinamica.py`, `motor/nomes.py`, `motor/motores/` (`demografia`, `sucessao`, `relogios`, `eventos`, `politica`; `criar()` monta a lista) e `motor/testes/test_f4.py`. Rodada de 100 anos só dentro dos reinos (Era I): 10 blocos `OK` em 3 sementes, I-01, I-03 a I-07, I-11, I-13, I-14, I-15 e I-21 ok.
- `estado.py`: `do_molde(world, din)` passa a derivar idade (`nasc_dia`), `saude`, 110 postos, 64 pares de facções e 62 eventos; `importancia.py`: regras de posto, par, saúde e evento; `invariantes.py`: I-04, I-11 e I-15 (`checar_estado` por bloco); `simulador.py`: `ALERTA` em `saude.txt`, dinâmica no replay, versão F4.0; `validador.py`: regra W-09 (dinâmica), 0 erro e 20 avisos em `saidas/validacao-mundo.txt`. Molde e reinos intactos (hash f11f1c120acc99b1); `carregador.py` e `hash_dinamica` agora fecham os arquivos que abrem.
- **Testes completos de fim de F4** (locais, sem rede): 28 testes passam (12 da F3, 16 da F4: cobertura da dinâmica, idade e morte, estado inicial, 100 anos, replay, NPCs simulados e nomes, mistério não resolvido, determinismo/retomada/passo neutro com motores reais, herdeiro assume a Coroa, vacância e NPC simulado, defeitos propositais de I-04 e I-11, dinâmica vazia); scripts compilam; `reinos/`, `mundo/`, `referencia/`, `referencia-tecnica/` e `plano/ferramentas/` (menos o novo gerador) idênticos à base. 0 falha, 0 regressão.
- ASSUMINDO: classe de longevidade de cada raça, faixas, limiares e taxas (todos `PROPOSTO`, D-M6); pressões e travas de relógio geradas por heurística (`revisado=não`); sílabas e recombinação de epítetos para nomes simulados (M-10 não traz lista); I-04 só vale com sucessão na rodada; 15 eventos sem relógio ficam latentes. Plano: F4 ✔ e seção "Como ficou a F4". Motor: **5 de 9 fases (F0 a F4, 56%)**; próximo: F5 (economia, rotas, Ecos, mistérios e contratos de fronteira).

## 2026-11-07 — F3 do motor: estado, crônica, replay, hash, notificações e passo de 10 anos

- Novos em `motor/`: `estado.py`, `cronica.py` (jsonl com cadeia de hash), `importancia.py`, `invariantes.py`, `simulador.py` (CLI: `--anos`, `--passo`, `--semente`, `--saida`, `--retomar`), `motores/__init__.py` (vazio até a F4) e `testes/test_f3.py`; nova pasta `dinamica/` (vazia até a F4).
- A rodada grava `cronica.jsonl`, `notificacoes.jsonl`, `saude.txt` (um check por bloco de 10 anos: OK ou FALHA; ALERTA reservado), `invariantes.txt`, `progresso.json` e snapshots. Em FALHA a rodada para e o snapshot do bloco anterior fica para depurar.
- Invariantes em operação: I-01, I-03, I-05, I-06, I-07, I-13, I-14 e I-21 por rodada; I-02, I-19 e I-22 comparando rodadas. 12 testes passam (motor vazio, motor-sonda, defeitos propositais de I-01, I-03, I-07 e I-21, replay e cadeia de hash). Pendentes: I-04, I-08 a I-12, I-15 a I-18 e I-20.
- `carregador.py`: só passou a fechar os arquivos que abre (o hash do molde e a validação do mundo ficam idênticos: 0 erro, 19 avisos, sha256 f11f1c120acc99b1). Molde e reinos intactos.
- Plano: F3 marcada como feita, com a seção "Como ficou a F3". Próximo: F4 (dinâmica v0, demografia, sucessão, relógios, eventos e política).

## 2026-11-07 — Plano do motor: passo de 10 anos, eras livres, notificações

- `plano/PLANO_MOTOR_MYTHRALIS.md`: simulador em blocos de 10 anos (`--passo 10`) com check de saúde por bloco (`saude.txt`: OK, ALERTA, FALHA); RPG literário por dia (seção 6.1 e 6.2). Eras livres, sem 3 eras nem 100 anos; regime de fronteira (selado, poroso, aberto) muda por gatilho (seção 8). Importância dos deltas e notificações (seção 10.1). Novos invariantes I-21 (notificação completa) e I-22 (passo neutro). Decisões D-M11, D-M12 e D-M13 (aguardam o autor). F3 passa a incluir `importancia`, `notificacao`, passo de 10 anos e `saude.txt`.

## 2026-11-07 — Orravel passa a se chamar Mythralis; plano do motor de mundo vivo

- Projeto e mundo renomeados de **Orravel** para **Mythralis** (pasta, textos e ID `mundo.mythralis`); o continente segue **Valdoren**.
- Novo `plano/PLANO_MOTOR_MYTHRALIS.md`: motor de mundo vivo para os 5 reinos num esquema só, simulador de eras (Eras I a III), crônica e Ecos, 20 invariantes de continuidade, fases F0 a F8 e decisões D-M1 a D-M10. Decisões D-M aguardam o autor.
- **F1:** `plano/ferramentas/converter_reino1.py` gera `reinos/01-.../dados/` (21 arquivos, `esquema=1`) a partir dos CSVs e docs de Aeltharyn. `validar_reino.py` dá 0 erro nos 5 reinos; relatórios dos reinos 2 a 5 idênticos aos salvos. Mudanças no validador: `agentes` de semente e `entidades` de canal aceitam `settlement.`; `localidade` de recurso aceita o próprio reino e facção. Marcadores de fato restrito/secreto de Aeltharyn são derivados pelo conversor (o CSV não os tinha).
- **F2:** `motor/carregador.py`, `calendario.py`, `validador.py`, `validar_mundo.py`: os 5 reinos juntos (46 cidades, 233 NPCs, 64 facções, 40 relógios); 0 erro e 19 avisos em `saidas/validacao-mundo.txt`.

## 2026-11-07 — R5-8.2: ata de fechamento 6 de Azeroth (D-174); fim da Fase 8

- Novo `reinos/05-azeroth/11-fechamento-de-azeroth.md` (doc 11): resultado das oito fases, 11 de 11 portas, 12 contradições intencionais, limites explícitos, critérios finais de uso e conferência dos cinco reinos (383 IDs, 0 colisão, 0 nome repetido, anel fechado). Aguarda a aprovação do autor (D-174). Lore dos reinos 1 a 4 intacto; troca de nomes antigos em M-04, M-05 e reinos 1 a 4 fica para o autor.
- **Testes completos de fim de F8** (locais, sem rede): 5 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos), relatórios idênticos aos salvos; conversor do reino 5 idempotente (`dados/` e relatório idênticos após nova execução), validador do reino 5 = 0 erro, 7 avisos, 0 exceções; 18 CSV de Azeroth íntegros; 383 IDs nos cinco registros mestres, 0 colisão; contagens do doc 11 conferem com os registros; arquivos citados no doc 11 existem; reinos 1 a 4, `referencia/`, `referencia-tecnica/` e `mundo/` idênticos à base; em `plano/` só mudaram `PROTOTIPO_MINIMO.md`, `REFINAMENTO_PACTO_HUMANO.md`, `PLANO_DOS_REINOS.md`, `DECISOES.md` e o novo `RELATORIO_R5_8_1.md`; `plano/ferramentas/` intacto. 0 falha, 0 regressão.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-174) ajustados. Roadmap do reino 5: **37 de 37 (100%); F8 concluída**; próximo: aprovação do autor.

## 2026-11-07 — R5-8.1: revisão final de Azeroth (D-173)

- Novo `plano/RELATORIO_R5_8_1.md`: portas 1 a 10 de `MODELO_DE_REINO.md` §6 marcadas (3 com ressalva, 6 com lacuna explícita), 11 pendente (R5-8.2); 12 contradições intencionais, 0 acidental nova; conferência por script de contagens, simetria de facções, IDs e NPC falecido. Lore, dados e scripts intactos.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-173, aceita pela continuação) ajustados. Sem testes (só ao fim da Fase 8). Roadmap do reino 5: 36 de 37 (97%); próximo R5-8.2.

## 2026-11-07 — R5-7.3: protótipo mínimo de Azeroth (D-172); fim da Fase 7

- Nova seção 13 em `plano/PROTOTIPO_MINIMO.md`: Estrelado + `npc.ixra-noite-aberta` + Vaudouro em uma linha; contexto B de 1.375 caracteres; roteiro de 8 turnos, Q1 a Q10 e critério de aceite escritos antes da sessão. Nenhuma sessão executada; 0 fato, NPC, lugar, facção ou rota novo.
- **Testes de fim de F7** (locais, sem rede): 5 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos), relatórios idênticos aos salvos; conversor do reino 5 idempotente (`dados/` idêntico após nova execução) e validador 0 erro, 7 avisos, 0 exceções; 18 CSV de Azeroth íntegros; 8 IDs citados no §13 existem; 0 fato `restrito` ou `secreto`, relógio ou ID de Astrel no contexto B (1.375 caracteres); reinos 1 a 4, `referencia/`, `referencia-tecnica/`, `mundo/` e `plano/ferramentas/` idênticos à base; em `plano/` só mudaram `PROTOTIPO_MINIMO.md`, `REFINAMENTO_PACTO_HUMANO.md`, `PLANO_DOS_REINOS.md` e `DECISOES.md`; reino 5 só com o README da pasta. 0 falha, 0 regressão.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-172, aceita pela continuação) ajustados. ASSUMINDO: escolha de Estrelado e Vaudouro; limite de Q9. Roadmap do reino 5: **35 de 37 (95%); F7 concluída**; próximo R5-8.1.

## 2026-11-07 — R5-7.2: catálogo técnico, Azeroth no `.kt` (D-171)

- Nova seção 9 em `plano/MAPA_CATALOGO_KOTLIN.md`: KA1 a KA9 (0 entidade de Azeroth no `.kt`; 0 colisão entre 69 IDs do `.kt` e 68 do reino 5; 9 nativos e 8 linhagens fora do `.kt`; V-A1 10 de 10; lacunas do esquema). Nada instalado, nenhum código alterado, `.kt` não executado.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-171, aceita pela continuação) ajustados. Sem testes (só ao fim da Fase 7). Roadmap do reino 5: 34 de 37 (92%).

## 2026-11-07 — R5-7.1: dados e validador de Azeroth (D-170)

- Novos `plano/ferramentas/converter_reino5.py`, `reinos/05-azeroth/dados/` (21 `.txt`, 485 registros) e `relatorio-validador.md`/`.txt`: **0 erro, 7 avisos, 0 exceções**. `validar_reino.py`: classe `—` aceita para falecido e texto de V-34 para Azeroth; relatórios dos reinos 2, 3 e 4 idênticos (40, 22 e 11 avisos).
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-170, aceita pela continuação) ajustados. ASSUMINDO: chave `kingdom.azeroth`, calendário `contagem-das-chaves`. Sem testes além do validador (só ao fim da Fase 7). Roadmap do reino 5: 33 de 37 (89%).

## 2026-11-07 — R5-6.4: contrato anti-invenção de Azeroth (D-169); fim da Fase 6

- Novo §9 em `reinos/05-azeroth/06-modelo-operacional-e-aventuras.md`: verificação, 4 classes (fixo, livre, exige registro, proibido), proibições alinhadas ao contrato de fronteira, casos pedidos e teste rápido; 0 entidade ou fato novo.
- **Testes de fim de F6** (locais, sem rede): 4 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos), iguais aos salvos; 18 CSV de Azeroth íntegros; 63 IDs, 0 colisão com os reinos 1 a 4; 0 referência inválida nas 7 cenas; reinos 1 a 4, `referencia/`, `referencia-tecnica/`, `mundo/` e `plano/ferramentas/` idênticos à base. 0 falha, 0 regressão.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-169, aceita pela continuação) ajustados. ASSUMINDO: separação em classes. Roadmap do reino 5: **32 de 37 (86%); F6 concluída**; próximo R5-7.1.

## 2026-11-07 — R5-6.3: cenas de teste de Azeroth (D-168)

- Novos `reinos/05-azeroth/15-cenas-de-teste.md` e `.csv`: 7 cenas (Campoalvo, Vaudouro, Meiovau, Concharia, Pouso-das-Brisas, Torrefronte, Enseada-Vigia), maior "entra" 2103 caracteres; 0 segredo, 0 relógio, 0 `nunca_diz`, 0 rota com duração, 0 fato novo; fronteiras só do lado de cá.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-168, aceita pela continuação) ajustados. ASSUMINDO: escolha dos sete lugares. Sem testes (só ao fim da Fase 6). Roadmap do reino 5: 31 de 37 (84%).

## 2026-11-07 — R5-6.2: camadas de informação de Azeroth (D-167)

- Novos `reinos/05-azeroth/14-camadas-de-informacao.md` e `.csv`: 44 fatos (10 públicos, 12 locais, 2 restritos, 20 secretos) com caminho e como descobrir; coluna `camadas` em `12-fichas.csv` e campo em `12-fichas.md`; `camada_max` dos resumos definitiva (63 `local`, 10 `secreto`); 0 segredo em resumo, 0 fato novo.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-167, aceita pela continuação) ajustados. ASSUMINDO: critério de classificação. Sem testes (só ao fim da Fase 6). Roadmap do reino 5: 30 de 37 (81%).

## 2026-11-07 — R5-6.1: resumos de contexto de Azeroth (D-166)

- Novos `reinos/05-azeroth/14-resumos-de-contexto.md` e `.csv`: 73 resumos (10 cidades, 15 facções, 10 relógios, 38 pessoas), maior 476 caracteres, vizinhança em uma linha; 0 sem ID, 0 segredo nos resumos, 0 fato novo.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-166, aceita pela continuação) ajustados. ASSUMINDO: `camada_max` provisória. Sem testes (só ao fim da Fase 6). Roadmap do reino 5: 29 de 37 (78%).

## 2026-11-07 — R5-5.5: mistérios com limites de Azeroth (D-165); fim da Fase 5

- Novos `reinos/05-azeroth/13-misterios-limites.md` e `.csv`: 10 mistérios (MA-01 a MA-10) com fatos confirmados, 4 hipóteses, vetos, evidência e ligação a relógio e evento; 0 hipótese eleita, 0 ID inexistente, 0 mistério, NPC, lugar, facção ou rota novo.
- **Testes de fim de F5** (locais, sem rede): 4 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos), iguais aos salvos; 15 CSV de Azeroth íntegros; 46 IDs novos únicos, 0 colisão com os reinos 1 a 4; 0 referência inválida; reinos 1 a 4, `referencia/`, `referencia-tecnica/`, `mundo/` e `plano/ferramentas/` idênticos à base. 0 falha, 0 regressão.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-165, aceita pela continuação) ajustados. Roadmap do reino 5: **28 de 37 (76%); F5 concluída**; próximo R5-6.1.

## 2026-11-07 — R5-5.4: reação do reino ao jogador de Azeroth (D-164)

- Novos `reinos/05-azeroth/13-reacao-ao-jogador.md`, `13-reputacao.csv` (25: 10 lugares, 15 facções) e `13-canais.csv` (11): disposição por entidade, 5 estados de procurado, 10 atos ligados a L-01 a L-10, favor, reparação e intermediários; 0 ID inexistente ou falecido, 0 rota fora do doc 07, 0 número, 0 reação dependente de raça; 0 NPC, lugar, facção, ofício ou rota novo.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-164, aceita pela continuação) ajustados. ASSUMINDO: mapeamento ato→reação; sanção de L-06 e L-08. Sem testes (só ao fim da Fase 5). Roadmap do reino 5: 27 de 37 (73%).

## 2026-11-07 — R5-5.3: sementes de aventura de Azeroth (D-163)

- Novos `reinos/05-azeroth/13-sementes-de-aventura.md` e `.csv`: 30 sementes (10 lugares × pequena, média e grande); 0 agente inexistente ou falecido, 0 solução predefinida, 0 semente com outro reino; 0 NPC, lugar, facção ou fato novo.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-163, aceita pela continuação) ajustados. ASSUMINDO: níveis de ameaça. Sem testes (só ao fim da Fase 5). Roadmap do reino 5: 26 de 37 (70%).

## 2026-11-07 — R5-5.2: eventos-tipo e cadeias causais de Azeroth (D-162)

- Novos `reinos/05-azeroth/12-eventos.md` e `12-eventos.csv`: 12 eventos-tipo (10 completam relógio, 2 dão gatilho); 0 ID inexistente, só rotas documentadas do doc 07 (6), 0 evento com outro reino; 0 NPC, lugar, facção, relógio ou rota novo.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-162, aceita pela continuação) ajustados. ASSUMINDO: ordem dos elos. Sem testes (só ao fim da Fase 5). Roadmap do reino 5: 25 de 37 (68%).

## 2026-11-07 — R5-5.1: relógios de Azeroth (D-161)

- Novos `reinos/05-azeroth/12-relogios.md` e `12-relogios.csv`: 10 relógios (CA-01 a CA-10) com gatilhos observáveis, o que move e trava, custo por segmento e efeito da inação; 0 entidade fora do registro, 0 relógio com outro reino; 0 relógio, NPC, lugar ou fato novo.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-161, aceita pela continuação) ajustados. Sem testes (só ao fim da Fase 5). Roadmap do reino 5: 24 de 37 (65%).

## 2026-11-07 — R5-4.4: voz e ganchos de diálogo de Azeroth (D-160); fim da Fase 4

- Novos `reinos/05-azeroth/12-voz.md` e `12-voz.csv`: 12 de 12 principais com tom, 3 frases-modelo e o que nunca dizem; 0 fato, NPC, lugar ou facção novo.
- **Testes de fim de F4** (locais, sem rede): 4 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos), iguais aos salvos; 9 CSV de Azeroth íntegros; 63 IDs únicos, 0 colisão com os reinos 1 a 4; 0 referência inválida; reinos 1 a 4, `referencia/`, `referencia-tecnica/`, `mundo/` e `plano/ferramentas/` idênticos à base. 0 falha, 0 regressão.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-160, aceita pela continuação) ajustados. Roadmap do reino 5: **23 de 37 (62%); F4 concluída**; próximo R5-5.1.

## 2026-11-07 — R5-4.3: relações e conhecimento de Azeroth (D-159)

- Novos `reinos/05-azeroth/12-relacoes.md`, `12-relacoes.csv` (91 relações: 26 alianças, 16 rivalidades, 6 dívidas, 10 tensões, 21 vínculos de cargo, 12 medos, 0 afeto) e `12-conhecimento.csv` (88 linhas, 38 pessoas). 0 relação sem fonte; 0 NPC, nome, lugar, facção ou fato novo.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-159, aceita pela continuação) ajustados. ASSUMINDO: medos e origem em Concharia e Estrelado. Sem testes (só ao fim da Fase 4). Roadmap do reino 5: 22 de 37 (59%).

## 2026-11-07 — R5-4.2: competências de Azeroth pelo catálogo (D-158)

- Novos `reinos/05-azeroth/12-competencias.md` e `12-competencias.csv`: 38 de 38 pessoas (12 principais, 18 sem ficha, 8 heróis lendários); raça, sub-raça, classe e profissão de `ALINHAMENTO_AZEROTH.md`; capacidades e limites só dos docs 01, 04 e `12-fichas`. 0 ID fora do catálogo, 0 campo vazio, 0 capacidade sem limite; 0 NPC, nome, lugar, facção ou fato novo.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-158, aceita pela continuação) ajustados. ASSUMINDO: classe e profissão como leitura editorial. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 4). Roadmap do reino 5: 21 de 37 (57%).

## 2026-11-07 — R5-4.1: fichas completas de Azeroth (D-157)

- Novos `reinos/05-azeroth/12-fichas.md` e `12-fichas.csv`: 12 de 12 principais completos (16 colunas, 0 vazio), 12 agendas e 12 pressões distintas, classe e profissão do alinhamento, facção e aliados do `12-facoes.csv`, relógio ligado a cada um, 4 acúmulos marcados (CI-07). 0 NPC, nome, lugar, facção ou fato novo.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-157, aceita pela continuação) ajustados. ASSUMINDO: traços, medos e condições de evolução; `camadas` só no R5-6.2. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 4). Roadmap do reino 5: 20 de 37 (54%).

## 2026-11-07 — R5-3.4: calendário e estações de Azeroth (D-156); fim da Fase 3

- `reinos/05-azeroth/01-reino-de-azeroth.md`, parte 5, `M-02` §9 e `M-10` §16: 12 luas numeradas, 4 estações (Pousio, Sementeira, Ceifa, Ventania), 5 Dias de Cruzamento, Contagem das Chaves (época `desconhecida`); ano 457 das Chaves = ano 742 de Aeltharyn (deslocamento de 285, ASSUMINDO); tabela de conversão de 8 linhas; agenda sazonal só de condição. 0 evento datado em anos de outros reinos; 0 NPC, lugar, facção ou rota novo; 0 nome repetido dos reinos 1 a 4.
- **Testes de fim de F3** (locais, sem rede): 4 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos; 7, 6 e 2 exceções), iguais aos salvos; 4 CSV de Azeroth íntegros; 63 IDs únicos, 0 colisão com os reinos 1 a 4; 0 referência inválida entre docs; 10 `city.*` dos docs 08 e 09 no registro; reinos 1 a 4, `referencia/`, `referencia-tecnica/` e `plano/ferramentas/` idênticos à base; `mundo/` só com M-02 §9 e M-10 §16 acrescentados. 0 falha, 0 regressão.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-156, aceita pela continuação) ajustados. Roadmap do reino 5: **19 de 37 (51%); F3 concluída**; próximo R5-4.1.

## 2026-11-07 — R5-3.3: recursos de Azeroth (D-155)

- Novos `reinos/05-azeroth/08-registro-operacional-de-recursos.md` e `09-matriz-material-dos-lugares.md`: rica em, pobre em e depende de dos 10 lugares; 24 dependências (17 críticas, 7 de apoio; todo lugar com ao menos uma crítica, cada uma com fonte); 4 fluxos qualitativos do lado de cá. 0 número, nível, unidade, preço ou tempo; origem do sal, da madeira e do grão comprado `desconhecida`; pergunta do contrato §7 aberta; 0 entidade, rota ou fluxo novos.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md`, doc 05 §2 e `DECISOES.md` (D-155, aceita pela continuação) ajustados. ASSUMINDO: lugar-mãe como apoio; falta de madeira em Concharia como leitura de dependência. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 3). Roadmap do reino 5: 18 de 37 (49%).

## 2026-11-07 — R5-3.2: demografia de Azeroth (D-154)

- Novos `reinos/05-azeroth/12-demografia.md` e `.csv` (10 lugares, 8 colunas): faixas do doc 01 (2 grandes, 4 médias, 4 pequenas), evidência e confiança `baixa`; regras DM-01 a DM-05. Conferido por script: 0 diferença com o doc 01, 10 IDs no registro, 0 número, 0 censo, 0 proporção entre povos.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-154, aceita pela continuação) ajustados. ASSUMINDO: faixa como porte relativo. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 3). Roadmap do reino 5: 17 de 37 (46%).

## 2026-11-07 — R5-3.1: rotas e mapa esquemático de Azeroth (D-153)

- Novos `reinos/05-azeroth/07-registro-operacional-de-rotas.md` e `07-mapa-esquematico.md`: 14 ligações (10 documentadas sem duração, 4 sugeridas por dependência), 10 acessos, 6 linhas de fronteira e mapa em Mermaid. Grafo dos 10 lugares conexo, conferido por script; todo `city.*` citado existe no registro. Toda duração `desconhecida` (nenhuma "curta" ou "longa" no cânone); sem rota para Aeltharyn, Véu, Coroas ou Vale.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md`, doc 03 §2 e `DECISOES.md` (D-153, aceita pela continuação) ajustados. ASSUMINDO: lugar-mãe como ligação sugerida; Meiovau ↔ capitais como documentada. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 3). Roadmap do reino 5: 16 de 37 (43%).

## 2026-11-07 — R5-2.4: conferência cruzada de Azeroth (D-152); fim da Fase 2

- Novo `reinos/05-azeroth/13-conferencia-cruzada.md`: aliados e rivais (0 conflito, 0 assimetria, 0 aliada de rival), idades (12 fichas, sem número), 13 acúmulos de cargo como intencionais, quem sabe o quê nos 10 mistérios, 12 contradições e pontos abertos (CI-01 a CI-12). **Achado e corrigido:** nove da tríade (Helion, Ebbo, Perrin, Orrume, Corali, Casimar, Ixra, Eloh, Hollis) lideravam a facção local fora da lista declarada do doc 04 §10; declarados (de 4 para 13).
- **Testes de fim de F2** (locais, sem rede): 4 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos; 7, 6 e 2 exceções), relatórios idênticos aos salvos; 3 CSV de Azeroth íntegros (63, 15 e 10 linhas; 0 campo vazio em facções e lugares); 63 IDs únicos, 0 colisão com os reinos 1 a 4, 0 ID citado sem registro (só `npc.syranna-das-mares`, de Aeltharyn, em veto); 0 referência inválida entre docs; 0 ID do alinhamento fora do catálogo; reinos 1 a 4, `mundo/`, `referencia-tecnica/`, `plano/ferramentas/` e `referencia/` (exceto o novo alinhamento) idênticos à base. 0 falha, 0 regressão.
- **D-150 e D-151 aceitas pela continuação**; nova **D-152** (aceita pela continuação). Roadmap do reino 5: **15 de 37 (41%); F2 concluída**; próximo R5-3.1.

## 2026-11-07 — R5-2.3: alinhamento de Azeroth ao catálogo (D-151)

- Novo `referencia/racas-e-povos/ALINHAMENTO_AZEROTH.md`: 9 nativos, 8 sub-raças e 25 forasteiros conferidos contra `personagem-catalogo.json` (0 divergência); raça, sub-raça, classe e profissão das 38 pessoas (0 ID fora do catálogo; sub-raça só no lugar do doc 10 §2). Heróis lendários sem classe e profissão (sem feito escrito).
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md`, registro mestre e `DECISOES.md` (D-151, aceita pela continuação) ajustados. ASSUMINDO: classe e profissão como leitura editorial. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 2). Roadmap do reino 5: 14 de 37 (38%).

## 2026-11-07 — R5-2.2: facções e lugares como dados de Azeroth (D-150)

- Novos `reinos/05-azeroth/12-facoes.csv` (15 facções, 11 colunas) e `12-assentamentos.csv` (10 lugares, 6 colunas), no molde dos reinos 3 e 4. Conferido por script: 0 campo vazio, 0 ID inexistente no registro, aliados e rivais simétricos, 0 aliado que seja rival, 1 rival `desconhecido` (F-07, com motivo). `recursos` é `desconhecido` (não se inventa); `lideranca_id` é `desconhecido` em Concharia e Estrelado (voz do órgão em disputa).
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md`, registro mestre (§6) e `DECISOES.md` (D-150, aceita pela continuação) ajustados. ASSUMINDO: `recursos`, lideranças em disputa e `tensao` da facção igual à do lugar principal. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 2). Roadmap do reino 5: 13 de 37 (35%).

## 2026-11-07 — R5-2.1: registro mestre de Azeroth (D-149)

- Novos `reinos/05-azeroth/12-registro-mestre.csv` (formato de D-21) e `12-registro-mestre.md`: 63 entidades (10 lugares, 15 facções, 38 pessoas). 26 pessoas sem ficha ganharam ID pelo nome já escrito. Conferido por script: 0 ID duplicado, 0 colisão com os reinos 1 a 4, 0 povo fora do catálogo, 0 lugar inexistente, 0 ID citado nos docs ausente do registro.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-149, aceita pela continuação) ajustados. ASSUMINDO: regra de ID, residência dos líderes novos e coroas, rios e instituições sem ID. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 2). Roadmap do reino 5: 12 de 37 (32%).

## 2026-11-07 — R5-1.7: mistérios, relógios e oportunidades de Azeroth (D-148); fim da Fase 1

- `reinos/05-azeroth/01-reino-de-azeroth.md`, parte 4: 10 mistérios com limites (um por lugar). Novo `06-modelo-operacional-e-aventuras.md`: 10 relógios (2/6 ou 3/6, sem avanço por calendário), 10 conflitos com causa concreta, 10 oportunidades. Nada toca o Mistério C, a Neblina Longa, os Mistérios D e E, o Conflito G, a foz do Argente ou outro reino; 0 NPC, lugar, facção ou rota novo.
- **Testes de fim de F1** (locais, sem rede): 4 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos; 7, 6 e 2 exceções), iguais aos salvos; reinos 1 a 4, `referencia/`, `referencia-tecnica/`, `mundo/` e `plano/ferramentas/` idênticos à base; referências entre docs de Azeroth válidas; 0 ID duplicado ou colidindo com os reinos 1 a 4. 0 falha, 0 regressão.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-148, aceita pela continuação) ajustados. ASSUMINDO: dez relógios, progresso inicial, travas, custos e quem decide no segmento 5. Reinos 1 a 4 intactos. Roadmap do reino 5: 11 de 37 (30%); **Fase 1 concluída**; próximo R5-2.1.

## 2026-11-07 — R5-1.6: povos, sub-raças, linhagens e forasteiros de Azeroth (D-147)

- Novo `reinos/05-azeroth/10-matriz-povos-nativos-cidades-e-forasteiros.md`: 9 nativos em ID do catálogo, 8 sub-raças, linhagens próprias de Humano, Pequenino e Abissal Costeiro (P-4), lugares × povos (10 de 10 com ≥ 2 nativos), 25 forasteiros (8 com lugar, 17 sem; conferidos contra M-05 por script), chegados do Véu sem alocação, papéis por povo com lacunas abertas e mestiços só pela regra D-64. 0 NPC novo.
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-147, aceita pela continuação) ajustados. ASSUMINDO: sub-raça por lugar, motivos dos forasteiros e a outra sub-raça nos pares com Aeltharyn. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 1). Roadmap do reino 5: 10 de 37 (27%).

## 2026-11-07 — R5-1.5: história, defesa, leis, economia e fluxos de Azeroth (D-146)

- `reinos/05-azeroth/01-reino-de-azeroth.md`, parte 3: 4 eras locais, origem do nome "Azeroth", lendas dos 10 lugares, cronologia relativa, defesa e vulnerabilidades. Novo `05-economia-leis-e-fluxos.md`: 10 leis (costume, com lacuna), economia qualitativa dos 10 lugares e os fluxos As Costas e As Fugas do Véu, só do lado de cá (povos conferidos contra M-05 por script; fluxos 1 a 3 fora).
- `mundo/M-10-nomes-minimo.md` §15, plano, README raiz e da pasta, `PLANO_DOS_REINOS.md`, doc 01 e `DECISOES.md` (D-146, aceita pela continuação) ajustados. ASSUMINDO: leis como costume, versões do nome, sal e madeira sem origem. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 1). Roadmap do reino 5: 9 de 37 (24%).

## 2026-11-07 — R5-1.4: instituições, justiça, acordos e facções de Azeroth (D-145)

- Novo `reinos/05-azeroth/04-instituicoes-justica-pactos.md`: governo e sucessão das três coroas (um costume por coroa), Mesa de Meiovau (três chaves, sem decidir a precedência), acordos entre coroas, justiça, Costume da Porta (acolhida local, sem motivo nem lei de Aeltharyn), tributo qualitativo, governo dos 10 lugares e 15 facções (aliados e rivais preenchidos; matriz simétrica conferida por script; 1 rival `desconhecido` com motivo).
- `mundo/M-10-nomes-minimo.md` §14, plano, README raiz e da pasta, `PLANO_DOS_REINOS.md`, docs 01 e 03 e `DECISOES.md` (D-145, aceita pela continuação) ajustados. ASSUMINDO: sucessões, Mesa, Costume da Porta e 2 líderes novos. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 1). Roadmap do reino 5: 8 de 37 (22%).

## 2026-11-07 — R5-1.3: NPCs principais e tríades de Azeroth (D-144)

- `reinos/05-azeroth/01-reino-de-azeroth.md`, parte 2: 12 principais (3 governantes e 3 herdeiros, 2 generais, 2 heróis vivos, guardiã e benfeitor de Meiovau), 8 heróis lendários e tríade herói, guardião e benfeitor dos 10 lugares (36 pessoas; 9 nativos; mestiços 0). Fichas completas com ID, povo do catálogo, agenda, pressão, limite, confirmado, suspeita e desconhecido; mínimos de D-22 e V-34 cumpridos.
- `mundo/M-10-nomes-minimo.md` §13 (nomes reservados; 5 candidatos trocados por quase-colisão), plano, README raiz e da pasta, `PLANO_DOS_REINOS.md`, docs 01 e 03 e `DECISOES.md` (D-144, aceita pela continuação) ajustados. ASSUMINDO: nomes, faixas etárias, feitos dos heróis vivos e residência dos herdeiros. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 1). Roadmap do reino 5: 7 de 37 (19%).

## 2026-11-07 — R5-1.2: rede política dos lugares de Azeroth (D-143)

- Novo `reinos/05-azeroth/03-assentamentos-e-rede-politica.md`: fichas políticas dos 10 lugares (lugar-mãe, liderança por papel, obrigação, interesse, tensão), posição relativa qualitativa, vínculos das coroas e de Meiovau e a rede (Trigal × Cais, Cais × Vento, Vento × Trigal). Nenhum campo vazio; sem fato novo além do doc 01; sem vilas menores (V-31).
- Plano, README raiz e da pasta, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-143, aceita pela continuação) ajustados. ASSUMINDO: lugar-mãe como vínculo de tributo. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 1). Roadmap do reino 5: 6 de 37 (16%).

## 2026-11-07 — R5-1.1: os dez lugares de Azeroth (D-142)

- Nova pasta `reinos/05-azeroth/`: `README.md` e `01-reino-de-azeroth.md` (parte 1): escala e governo (três coroas rivais, D-141), residência dos governantes e **dez lugares fixos** (Campoalvo, Vaudouro, Torrefronte; Alvamar, Enseada-Vigia, Concharia; Torreclara, Estrelado, Pouso-das-Brisas; Meiovau, cidade livre). Função × geografia conferida; só As Costas e As Fugas do Véu tocam outros reinos, sem rota, número ou causa; foz do Argente e fronteira com o Véu não afirmadas.
- `mundo/M-10-nomes-minimo.md` §12 (nomes reservados), plano, README, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-142, aceita pela continuação) ajustados. ASSUMINDO: nomes, faixas de população e divisão das coroas. Reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 1). Roadmap do reino 5: 5 de 37 (14%).

## 2026-11-07 — R5-0.3: escala, governo, Estelar e nomes de Azeroth (D-141); fim da Fase 0

- Autor: governo = **várias coroas rivais**; escala e Estelar = "decida você". ASSUMINDO: **10 lugares** (V-30), **3 coroas** (governante e herdeiro cada, V-34), **Estelar nativo** (matriz intacta). `mundo/M-10-nomes-minimo.md` §11 (regras e tom por povo), plano, contrato (P-1 e P-6 decididos), `DECISOES.md` (D-141), `PLANO_DOS_REINOS.md` e README ajustados. Reinos 1 a 4 intactos.
- **Testes de fim de F0** (locais, sem rede): 4 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos; 7, 6 e 2 exceções), iguais aos salvos; `reinos/`, `referencia/` e `referencia-tecnica/` idênticos à base; só mudaram `mundo/M-10`, plano, README e CHANGELOG. 0 falha, 0 regressão. Roadmap do reino 5: **4 de 37 (11%); F0 concluída**; próximo R5-1.1.

## 2026-11-07 — R5-0.4: contrato de fronteira do reino 5 (Azeroth, D-140)

- Novo `plano/CONTRATO_DE_FRONTEIRA_PACTO_HUMANO.md`: fixos de Aeltharyn (13), Coroas (5), Véu (7) e Vale (4); 7 afirmações permitidas, 16 vedações, 7 ganchos e 6 pontos abertos. Aeltharyn e o Véu são vizinhos plenos; Coroas e Vale não tocam o reino 5; só As Costas e As Fugas do Véu o alcançam. Mistério C, Neblina Longa, Mistérios D e E, Conflito G, motivo das Fugas e foz do Argente ficam fora do reino 5.
- **Abertos ao autor:** P-1 Estelar e P-6 escala e governo (R5-0.3), P-2 qual pântano, P-3 fronteira com o Véu na foz. P-4 aceito (D-110). ASSUMINDO: o arquivo mantém o nome previsto no plano. Plano, README, `PLANO_DOS_REINOS.md` e `DECISOES.md` (D-140) ajustados; reinos 1 a 4 intactos. Sem testes (só ao fim da Fase 0). Roadmap do reino 5: 3 de 37 (8%).

## 2026-11-07 — R5-0.2: decisões herdadas do reino 5 (Azeroth)

- `plano/REFINAMENTO_PACTO_HUMANO.md`: nova seção "Resultado do R5-0.2", com D-21, D-22, D-23, D-63, D-64, D-99, D-136 e D-110 (P-3) conferidas em `DECISOES.md` e mapeadas aos módulos onde valem. D-22 e D-23 valem **cumpridas na escrita** (o rebaixamento de D-22 foi só de Aeltharyn). ASSUMINDO: D-21 a D-23 estavam escritas para os reinos 1 e 2; o reino 5 as herda como os reinos 3 e 4.
- Nenhuma decisão nova; V-30, V-34 e convenções de nome dependem do R5-0.3. Sem testes (só ao fim da Fase 0). Roadmap do reino 5: 2 de 37 (5%).

## 2026-11-07 — R5-0.1: camada de mundo mínima do reino 5 (Azeroth)

- `plano/REFINAMENTO_PACTO_HUMANO.md`: nova seção "Resultado do R5-0.1" e ponto de partida ampliado (fluxos As Costas e As Fugas do Véu; Mar Austral e foz do Argente; Pequenino e Abissal Costeiro como nativos em dois reinos). M-05 conferido contra a matriz: 9 nativos, 0 aliado, 25 forasteiros, 16 sem papel. M-01, M-02 e M-05 valem sem mudança; calendário fica para o R5-3.4.
- **Correção:** o plano dizia "0 raça sem papel"; o correto é 16. Estelar como nativo segue aberto para o autor no R5-0.3. Sem testes (só ao fim da Fase 0). Roadmap do reino 5: 1 de 37 (3%).

## 2026-11-07 — Nome do reino 5: Azeroth (D-139, R5-0.3 parte b)

- Autor decidiu o nome **Azeroth** ("Pacto Humano" vira nome de trabalho antigo). `plano/REFINAMENTO_PACTO_HUMANO.md`, `DECISOES.md` (D-139), `PLANO_DOS_REINOS.md` e `README.md` ajustados; 0 ocorrência prévia do nome no projeto. Nenhum texto dos reinos 1 a 4, M-04 ou M-05 alterado. Risco registrado em D-139: nome de mundo de propriedade alheia. R5-0.3 segue aberto (escala, governo, Estelar); progresso 0/37.

## 2026-11-07 — Liberação do reino 5, Pacto Humano (D-138); plano criado

- Autor aprovou o fechamento 5 e liberou o reino 5 ("Siga reino 5", D-137 opção A). Doc 11 do reino 4: **[PROPOSTO]** → **[APROVADO]**; ata, README do reino e `PLANO_DOS_REINOS.md` ajustados (reino 4 **fechado**).
- Novo `plano/REFINAMENTO_PACTO_HUMANO.md`: 37 módulos em 9 fases (prefixo `R5-`), ponto de partida (M-04, M-05: 9 nativos, 25 forasteiros), lições dos reinos 1 a 4 (inclui "todo lugar nasce com ligação" e linhagens próprias) e andamento 0/37. Nenhum lore escrito; nenhum arquivo dos reinos 1 a 4 alterado além do doc 11 do reino 4 e dos READMEs.
- **D-137 aprovada**; nova **D-138**. Nome, escala, governo e encaixe do Estelar ficam para o autor no R5-0.3 (D-139, a abrir). Próximo: **R5-0.1**.

## 2026-11-07 — R4-8.2: ata de fechamento 5 do Vale Palancar (D-137); fim da Fase 8

- Novo `reinos/04-vale-palancar/11-fechamento-do-vale-palancar.md` (doc 11): resultado das Fases 1 a 8, 11 de 11 portas, 10 contradições intencionais, limites explícitos e critérios finais de uso. **Estado [PROPOSTO]; reino 5 (Pacto Humano) não liberado**, só com a aprovação do autor.
- **Testes de fim de F8** (locais, sem rede): 18 CSV do reino 4 íntegros (464 linhas, 0 coluna fora); conversor idempotente; 3 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos; 7, 6 e 2 exceções), iguais aos salvos; contagens do doc 11 conferidas contra os dados; `reinos/` idêntico à base. 0 falha, 0 regressão.
- **D-136 aceita pela continuação**; nova **D-137** (aguardando o autor). Roadmap (**37 de 37, 100%; F8 concluída**), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados.

## 2026-11-07 — R4-8.1: revisão final do Vale Palancar (D-136)

- Novo `plano/RELATORIO_R4_8_1.md`: 11 portas de `MODELO_DE_REINO.md` §6 (1 a 10 marcadas; 1, 3 e 6 com ressalva ou lacuna explícita; 11 pendente do R4-8.2) e 10 contradições intencionais × 0 acidental nova, para o doc 11. Nenhum lore, doc ou dado alterado.
- **Verificações locais** (sem rede): conversor do reino 4 idempotente; 3 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos), iguais aos salvos; 47 entidades sem ID duplicado ou citado sem registro; 8 relógios, 10 mistérios, 14 facções (0 simetria quebrada) conferidos; `reinos/` idêntico à base. 0 falha, 0 regressão.
- **D-135 aceita pela continuação**; nova **D-136** (aguardando o autor). Roadmap (36 de 37, 97%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Sem suíte de testes (só ao fim da Fase 8).

## 2026-11-07 — R4-7.3: protótipo mínimo do Vale Palancar (D-135); fim da Fase 7

- `plano/PROTOTIPO_MINIMO.md`, nova seção 12 (12.1 a 12.10): Alvasal, Gruzk Estandarte-Baixo e Rasovau em uma linha; contexto B de 1.840 caracteres (só camadas públicas, sem relógio); condições A e B, regras de classificação, roteiro de 8 turnos, métricas Q1 a Q10 e critério de aceite fixados antes de jogar. **Nenhuma sessão executada.** ASSUMINDO: escolha de lugar, NPC e linha de mercado.
- **Testes de fim de F7** (locais, sem rede): 18 CSV do reino 4 íntegros (464 linhas, 0 coluna fora); conversor do reino 4 idempotente; 3 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos; 7, 6 e 2 exceções), relatórios iguais aos salvos; IDs do §12 existem; 0 fato restrito ou secreto, relógio ou `nunca_diz` no contexto B; reinos 1 a 3, `mundo/`, `referencia/` e `referencia-tecnica/` idênticos à base. 0 falha, 0 regressão.
- **D-134 aceita pela continuação**; nova **D-135** (aguardando o autor). Roadmap (35 de 37, 95%; **F7 concluída**), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados.

## 2026-11-07 — R4-7.2: catálogo técnico, Vale Palancar no `.kt` (D-134)

- `plano/MAPA_CATALOGO_KOTLIN.md`, nova seção 8 (KP1 a KP9), por leitura e busca de texto: 0 entidade do Vale no `.kt`; 0 colisão de ID (69 do `.kt` contra 48 do reino 4); 12 nativos fora dos povos do `.kt` e 4 forasteiros compartilhados (`elfo`, `feerico`, `plantae`, `satiro`); linhagens do Vale distintas das de Aeltharyn (P-3); tríade 8 de 8 lugares; lacunas do esquema listadas (regime fixo ou sazonal, governo sem rei e sem capital, Tratos). Nada instalado, nenhum código alterado, `.kt` não executado.
- **D-133 aceita pela continuação**; nova **D-134** (aguardando o autor). Roadmap (34 de 37, 92%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Sem suíte de testes (só ao fim da Fase 7).

## 2026-11-07 — R4-7.1: dados e validador do Vale Palancar (D-133)

- Novos `plano/ferramentas/converter_reino4.py` e `reinos/04-vale-palancar/dados/` (21 `.txt`, 405 registros, formato D-21) com `relatorio-validador.md` e `.txt`: **0 erro, 11 avisos, 2 exceções** (V-23 e V-44: Alvasal sem rota interna no doc 07; grafo em dois grupos; nenhuma rota inventada). Capital `—`; recursos de eventos sem correspondente exato entram com papel `?` (ASSUMINDO).
- `validar_reino.py`: aceita o prefixo `mist.` em `entidade` de `fato.txt` e tem mensagem própria em V-34 para o reino 4. Validador rodado nos reinos 2 e 3: relatórios idênticos aos salvos (0 erro, 40 e 22 avisos). Fontes do reino 4 e reinos 1 a 3 sem alteração.
- **D-132 aceita pela continuação**; nova **D-133** (aguardando o autor). Roadmap (33 de 37, 89%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Sem suíte de testes (só ao fim da Fase 7).

## 2026-11-07 — R4-6.4: contrato anti-invenção do Vale Palancar (D-132); fim da Fase 6

- Doc 06 (`reinos/04-vale-palancar/06-modelo-operacional-e-aventuras.md`), nova §9: verificação antes de aceitar proposta (9 perguntas), quatro classes (fixo, livre, exige registro, proibido), lista proibida, 8 casos pedidos (novo chefe, lugar, guerra, NPC ou clã, morte, rota, rumor sobre Aeltharyn, Descida e Rota do Sal) e teste rápido de 4 perguntas. Herda os contratos dos reinos 1 a 3 e `CONTRATO_DE_FRONTEIRA_PALANCAR.md` sem alterá-los. ASSUMINDO: a separação em classes é leitura editorial.
- **Testes de fim de F6** (locais, sem rede): validadores dos reinos 2 e 3 = 0 erro (40 e 22 avisos, 7 e 6 exceções, contagens idênticas às salvas); 3 scripts compilam; 18 CSV do reino 4 íntegros (464 linhas, 0 coluna fora); 47 entidades no registro mestre, 0 ID citado sem registro (a única citação externa, `npc.oranna-passo-largo`, está numa proibição); contagens da §9 conferidas (8 relógios, 10 mistérios, 12 eventos, 24 sementes, 42 fatos, 55 resumos, 7 cenas, L-01 a L-10); 0 referência de arquivo inválida; reinos 1 a 3, `mundo/`, `referencia/`, `referencia-tecnica/` e `plano/ferramentas/` idênticos à base da sessão. 0 falha, 0 regressão.
- **D-131 aceita pela continuação**; nova **D-132** (aguardando o autor). Roadmap (32 de 37, 86%; **F6 concluída**), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados.

## 2026-11-07 — R4-6.3: cenas de teste do Vale Palancar (D-131)

- Novos `reinos/04-vale-palancar/15-cenas-de-teste.md` e `.csv` (5 colunas, molde do reino 3): 7 cenas (a a g do roadmap), cada uma com "entra" (maior: 2140 caracteres, limite ~2.400) montado só de resumos, fatos público e local, voz, semente pequena, rumor e regra, e "não entra". Conferido por script: 0 marcador de segredo, 0 relógio, 0 frase `nunca_diz`, 0 NPC falecido, 0 duração e 0 termo de outro reino em "entra". ASSUMINDO: (e) viagem Tabuleiro-Velho–Gramal sem duração; (f) Praia-Pardo em vez da Rota do Sal.
- **D-130 aceita pela continuação** ("Siga"); nova **D-131** (aguardando o autor). Roadmap (31 de 37, 84%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Sem suíte de testes (só ao fim da Fase 6).

## 2026-11-07 — R4-6.2: camadas de informação do Vale Palancar (D-130)

- Novos `reinos/04-vale-palancar/14-camadas-de-informacao.md` e `.csv` (11 colunas, molde do reino 3): 42 fatos (24 dos 12 principais, 10 dos mistérios, 8 relógios) em 4 camadas (11 públicos, 11 locais, 6 restritos, 14 secretos); restritos e secretos com caminho e "como descobrir" (20 de 20). `12-fichas.csv` e `12-fichas.md`: coluna e campo *Camadas de informação* de N-01 a N-12 preenchidos. `14-resumos-de-contexto.md`: `camada_max` definitiva.
- Conferido por script: 0 marcador de fato restrito ou secreto em resumo `local`, 0 campo vazio (exceto `marcador`), 0 termo de outro reino, IDs únicos.
- **D-129 aceita pela continuação** ("Siga"); nova **D-130** (aguardando o autor). Roadmap (30 de 37, 81%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Sem suíte de testes (só ao fim da Fase 6).

## 2026-11-07 — R4-6.1: resumos de contexto do Vale Palancar (D-129)

- Novos `reinos/04-vale-palancar/14-resumos-de-contexto.md` e `.csv` (8 colunas, molde do reino 3): 55 resumos (8 cidades, 14 facções, 8 relógios, 25 pessoas), até 500 caracteres (maior: 479) com vizinhança de uma linha (maior: 150). Conferido por script: 47 de 47 do registro mestre e 8 de 8 relógios cobertos, 0 uso de suspeita, desconhecido ou `nunca_diz`, 0 duração, número de população ou termo de outro reino, 0 campo vazio. `camada_max` provisória até R4-6.2.
- **D-128 aceita pela continuação** ("Siga"); nova **D-129** (aguardando o autor). Roadmap (29 de 37, 78%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 6).

## 2026-11-07 — R4-5.5: mistérios com limites do Vale Palancar (D-128); fim da Fase 5

- Novos `reinos/04-vale-palancar/13-misterios-limites.md` e `.csv` (9 colunas, molde do reino 3): 10 mistérios (MPL-01 a MPL-10, prefixo `mist.`) gerados do doc 01 parte 4, com fatos confirmados, 4 hipóteses abertas, "a verdade não pode ser", evidência (duas provas independentes) e ligação a relógio e evento. Texto idêntico ao doc 01; 0 mistério, hipótese ou veto novo; 0 ID fora do registro mestre ou falecido; 0 relógio ou evento inexistente.
- **Testes de fim de F5** (locais, sem rede): validadores dos reinos 2 e 3 = 0 erro (40 e 22 avisos, 7 e 6 exceções, contagens idênticas às salvas); 3 scripts compilam; 15 CSV do reino 4 íntegros (360 linhas, 0 coluna fora, 0 vazio); 0 ID, relógio, evento ou rota inexistente; sementes 24, reputação 22, canais 11, voz = fichas (12); 0 referência de arquivo inválida; reinos 1 a 3, `mundo/`, `referencia/`, `referencia-tecnica/` e `plano/ferramentas/` idênticos à base da sessão.
- **D-127 aceita pela continuação** ("Siga"); nova **D-128** (aguardando o autor). Roadmap (28 de 37, 76%; **F5 concluída**), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados.

## 2026-11-07 — R4-5.4: reação do Vale Palancar ao jogador (D-127)

- Novos `reinos/04-vale-palancar/13-reacao-ao-jogador.md`, `13-reputacao.csv` (22 linhas: 8 lugares e 14 facções) e `13-canais.csv` (11 canais), molde dos reinos 2 e 3: disposição por entidade, estados de procurado, ato × lei (L-01 a L-10) × autoridade, favor, reparação (doc 04 §6, qualitativa) e intermediários. Sem número de fama, preço ou prazo (do motor, MP-07); sem caçador de recompensa; nenhuma reação depende de raça. Conferido por script: 0 ID fora do registro mestre ou falecido como reagente, 0 rota fora do doc 07, 0 campo vazio, 0 menção a outro reino.
- **D-126 aceita pela continuação** ("Siga"); nova **D-127** (aguardando o autor). Roadmap (27 de 37, 73%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 5).

## 2026-11-07 — R4-5.3: sementes de aventura do Vale Palancar (D-126)

- Novos `reinos/04-vale-palancar/13-sementes-de-aventura.md` e `.csv` (11 colunas, molde dos reinos 2 e 3): 24 sementes, uma pequena, uma média e uma grande por lugar (as médias são OP-01 a OP-08 do doc 06; as pequenas partem de rumores; as grandes, dos problemas atuais). Ameaça P 1, M 2–3, G 3–4 (ASSUMINDO); "se ninguém age" em todas; sem solução predefinida. Conferido por script: 0 agente fora do registro ou falecido, 0 relógio ou evento inexistente, 0 campo vazio, 0 menção a outro reino, 0 número ou duração.
- **D-125 aceita pela continuação** ("Siga"); nova **D-126** (aguardando o autor). Roadmap (26 de 37, 70%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 5).

## 2026-11-07 — R4-5.2: eventos-tipo e cadeias causais do Vale Palancar (D-125)

- Novos `reinos/04-vale-palancar/12-eventos.md` e `12-eventos.csv` (10 colunas, molde dos reinos 2 e 3): 12 eventos-tipo (8 completam um relógio, 2 só dão gatilho, 2 sem relógio) com gatilho observável, cadeia (ASSUMINDO a ordem), lugares, termos do doc 09, pessoas e facções e rotas. Conferido por script: 0 ID fora do registro mestre, 0 relógio fora de `12-relogios.csv`, 0 rota fora do doc 07 e só entre lugares do Vale, 8 de 8 relógios cobertos, 0 campo vazio, 0 duração; nenhum evento envolve outro reino.
- **D-124 aceita pela continuação** ("Siga"); nova **D-125** (aguardando o autor). Roadmap (25 de 37, 68%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 5).

## 2026-11-07 — R4-5.1: relógios do Vale Palancar (D-124)

- Novos `reinos/04-vale-palancar/12-relogios.md` e `12-relogios.csv` (12 colunas, molde dos reinos 2 e 3): os 8 relógios CP-01 a CP-08 do doc 06 com gatilhos observáveis, custo por segmento, decisor no segmento 5 (Trato, Assembleia ou Capitão) e efeito da inação. 0 relógio, NPC, lugar ou fato novo; 0 entidade fora do registro mestre; nenhum relógio envolve outro reino; nenhum resolve mistério ou lacuna de lei.
- **D-123 aceita pela continuação** ("Siga"); nova **D-124** (aguardando o autor). Roadmap (24 de 37, 65%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 5).

## 2026-11-07 — R4-4.4: voz e ganchos de diálogo do Vale Palancar (D-123); fim da Fase 4

- Novos `reinos/04-vale-palancar/12-voz.md` e `12-voz.csv`: 12 de 12 principais com tom, 2 a 3 frases-modelo (instrução de tom, ASSUMINDO) e `nunca_diz`; 0 frase revela suspeita ou desconhecido (script e leitura); 0 número de tempo ou distância; 0 ID fora do registro.
- **Testes de fim de F4** (locais, sem rede): validador do reino 2 = 0 erro, 40 avisos, 7 exceções, e do reino 3 = 0 erro, 22 avisos, 6 exceções (relatórios idênticos aos salvos); 3 scripts compilam; 9 CSV do reino 4 íntegros; 47 IDs únicos, 0 colisão com os reinos 1 a 3, 0 ID citado sem registro; voz = fichas (12); 0 ID fora do catálogo; 0 aliado-rival, 0 assimetria; 0 referência de arquivo inválida; reinos 1 a 3, `mundo/`, `referencia/` e `referencia-tecnica/` idênticos à base da sessão.
- **D-122 aceita pela continuação** ("Siga"); nova **D-123** (aguardando o autor). Roadmap (23 de 37, 62%; **F4 concluída**), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados.

## 2026-11-07 — R4-4.3: relações e conhecimento do Vale Palancar (D-122)

- Novos `reinos/04-vale-palancar/12-relacoes.md`, `12-relacoes.csv` (81 relações: 22 alianças, 12 rivalidades, 12 dívidas, 8 tensões, 15 vínculos de cargo, 12 medos; **0 afeto**) e `12-conhecimento.csv` (66 linhas: 25 confirmado, 16 suspeita, 25 desconhecido; 25 de 25 pessoas), no formato dos reinos 2 e 3. Tudo derivado dos docs 01, 03, 04 e de `12-fichas.md`; toda relação com fonte; 0 ID fora do registro mestre, 0 aliado-rival, 0 relação com outro reino. ASSUMINDO: origem de dívida e tensão em Rasovau e Encosta-Aberta no guardião da tríade (chefe do Trato `desconhecido`); apoio ao lugar-mãe como dívida; medos das fichas.
- **D-121 aceita pela continuação** ("Siga"); nova **D-122** (aguardando o autor). Roadmap (22 de 37, 59%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 4).

## 2026-11-07 — R4-4.2: competências do Vale Palancar pelo catálogo (D-121)

- Novos `reinos/04-vale-palancar/12-competencias.md` e `12-competencias.csv` (mesmo formato dos reinos 2 e 3): 25 pessoas do registro mestre (12 principais, 7 sem ficha, 6 heróis lendários) com raça, sub-raça, classe e profissão de `ALINHAMENTO_PALANCAR.md`; capacidades e limites só do que os docs 01, 04 e `12-fichas.md` já dizem. Conferido por script contra `personagem-catalogo.json`: 0 ID fora do catálogo, 0 sub-raça com raça-mãe errada, 0 raça diferente do `povo_id`, 0 campo vazio, 12 de 12 iguais às fichas. O catálogo não liga habilidade mecânica a classe ou profissão (0 `competencies`), então nada foi inventado.
- **D-120 aceita pela continuação** ("Siga"); nova **D-121** (aguardando o autor). Roadmap (21 de 37, 57%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 4).

## 2026-11-07 — R4-4.1: fichas completas do Vale Palancar (D-120)

- Novos `reinos/04-vale-palancar/12-fichas.md` e `12-fichas.csv`: 12 de 12 principais com os 17 campos do molde dos reinos 2 e 3 (0 vazio); 12 agendas e 12 pressões distintas; povo, sub-raça, classe e profissão de `ALINHAMENTO_PALANCAR.md`; relógio ligado a cada um (8 relógios de `06`); aliados e rivais da facção (doc 04); 6 acúmulos marcados como intencionais (CI-07; Aruun sem ficha). Traços, medos e condições de evolução ASSUMINDO; `camadas` `—` até R4-6.2. Conferido por script: 0 ID fora do registro mestre; 8 de 8 lugares com principal; 0 pessoa, lugar, facção ou número novo.
- **D-119 aceita pela continuação** ("Siga"); nova **D-120** (aguardando o autor). Roadmap (20 de 37, 54%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 4).

## 2026-11-07 — R4-3.4: calendário e estações do Vale Palancar (D-119); fim da Fase 3

- Doc 01, parte 5, `M-02` §8 e `M-10` §10: 12 luas numeradas, 4 estações (Pasto Magro, Rebrota, Pasto Alto, Reunião dos Rebanhos), 5 Dias de Marco, Contagem dos Marcos (época `desconhecida`); início do jogo em Pasto Magro, Lua 1, dia 1 do ano 611 = Silêncio, Lua 1, dia 1 do ano 742 de Aeltharyn (deslocamento de 131, ASSUMINDO, reversível); tabela de conversão de 8 linhas; agenda sazonal só de condição (ASSUMINDO). Nomes conferidos: 0 colisão com os reinos 1 a 3. 0 evento datado entre reinos; migração, Assembleia e mandato do Capitão `desconhecidos`.
- **D-118 aceita pela continuação** ("Siga"); nova **D-119** (aguardando o autor). Roadmap (19 de 37, 51%; **F3 concluída**), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados.
- **Testes de fim de F3** (locais): validador do reino 2 = 0 erro, 40 avisos, 7 exceções, e do reino 3 = 0 erro, 22 avisos, 6 exceções (relatórios idênticos aos salvos); scripts Python compilam; 4 CSV do reino 4 íntegros (8, 8, 14 e 47 linhas; colunas uniformes); 47 IDs únicos, 0 colisão com os registros dos reinos 1 a 3; 0 referência de arquivo inválida; os IDs `clock.*` do doc 06 (8) sem colisão com os reinos 1 a 3 e a única citação externa (`npc.oranna-passo-largo`) está numa proibição; 8 IDs `city.*` dos docs 07, 08, 09 e do CSV de demografia existem no registro; faixas do CSV de demografia iguais às do doc 01. Reinos 1, 2 e 3, `referencia/` e `referencia-tecnica/` idênticos ao ZIP de entrada; só mudaram `mundo/M-02` e `M-10` (extensões), `plano/`, README, CHANGELOG e `reinos/04-vale-palancar/`. 0 falha, 0 regressão.

## 2026-11-07 — R4-3.3: recursos e matriz material do Vale Palancar (D-118)

- Novos `08-registro-operacional-de-recursos.md` e `09-matriz-material-dos-lugares.md`: 8 lugares com rica em, pobre em e depende de (só do doc 01, doc 03 e doc 05 §2); 12 dependências (8 críticas, todas com fonte; nenhum lugar isolado; DR-01 a DR-04 por lugar-mãe, ASSUMINDO); 4 fluxos qualitativos (Descida, Rota do Sal, Corredores Largos, troca interna). Conferido por script: 8 IDs `city.*` no registro mestre; 0 número, nível, unidade ou preço. Nada sobre a origem do combustível de Brasalume; pergunta do contrato §6 mantida aberta.
- **D-117 aceita pela continuação** ("Siga"); nova **D-118** (aguardando o autor). Roadmap (18 de 37, 49%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 3).

## 2026-11-07 — R4-3.2: demografia do Vale Palancar (D-117)

- Novos `reinos/04-vale-palancar/12-demografia.md` e `.csv` (molde dos reinos 2 e 3): 8 faixas de porte relativo (2 grandes, 3 médias, 3 pequenas) copiadas do doc 01 e conferidas por script (0 diferença; 8 IDs no registro mestre); regras DM-01 a DM-05 (faixa, não somar, sazonais variam com campo e estação, flutuantes fora, proporção entre povos desconhecida). 0 número, 0 censo.
- **D-116 aceita pela continuação** ("Siga"); nova **D-117** (aguardando o autor). Roadmap (17 de 37, 46%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 3).

## 2026-11-07 — R4-3.1: rotas e mapa esquemático do Vale Palancar (D-116)

- Novos `reinos/04-vale-palancar/07-registro-operacional-de-rotas.md` e `07-mapa-esquematico.md` (Mermaid): 9 ligações (8 documentadas sem duração, 1 sugerida por dependência), 8 acessos de lugares, 6 linhas de fronteira. **Toda duração é `desconhecida`**: o cânone do reino 4 não traz "curta" nem "longa" para nenhum percurso. Sazonais sem posição fixa; Ermo de Sal ↔ Vale, Vale ↔ Aeltharyn, Coroas e Véu seguem "sem rota" (contrato §5 itens 4 e 8). Conferido por script: 8 IDs `city.*` existem no registro mestre; 9 linhas `route.*`; nenhum "curta" ou "longa" atribuído.
- **D-115 aceita pela continuação**; nova **D-116** (aguardando o autor). Roadmap (16 de 37, 43%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 3).

## 2026-11-07 — R4-2.4: conferência cruzada do Vale Palancar (D-115); fim da Fase 2

- Novo `reinos/04-vale-palancar/13-conferencia-cruzada.md`: aliados e rivais (0 conflito, 0 assimetria, 0 aliada de rival), idades (12 fichas, sem número), 7 acúmulos de cargo como intencionais, quem sabe o quê nos 10 mistérios, 12 contradições e pontos abertos (CI-01 a CI-12). **Achado e corrigido:** Moura (Trato do Celeiro e Guardiões da Reserva) e Aruun (Trato do Poço e Vigias da Cisterna) acumulavam cargo fora da lista declarada do doc 04 §9; declarados (de 5 para 7).
- **Testes de fim de F2** (locais): validador do reino 2 = 0 erro, 40 avisos, 7 exceções, e do reino 3 = 0 erro, 22 avisos, 6 exceções (relatórios idênticos aos salvos); scripts Python compilam; 3 CSV do reino 4 íntegros (47, 14 e 8 linhas; 12, 11 e 6 colunas em todas as linhas); 47 IDs únicos, 0 colisão com os registros dos reinos 1 a 3, 0 ID do reino 4 citado sem registro (a única citação externa é `npc.oranna-passo-largo`, numa proibição); 0 ID do alinhamento fora do catálogo; 0 número de tempo; reinos 1, 2 e 3, `mundo/`, `referencia/` (exceto o novo alinhamento) e `referencia-tecnica/` idênticos ao ZIP de entrada. 0 falha, 0 regressão.
- **D-114 aceita pela continuação** ("Siga"); nova **D-115** (aguardando o autor). Roadmap (15 de 37, 41%; **F2 concluída**), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados.

## 2026-11-07 — R4-2.3: alinhamento do Vale Palancar ao catálogo (D-114)

- Novo `referencia/racas-e-povos/ALINHAMENTO_PALANCAR.md`: 12 nativos, 6 sub-raças (raça-mãe correta) e 25 forasteiros conferidos por script contra `personagem-catalogo.json` (0 divergência); raça, sub-raça, classe e profissão das 25 pessoas do registro (0 ID fora do catálogo; toda sub-raça no lugar que o doc 10 §2 lhe dá). Classe e profissão são leitura editorial (ASSUMINDO), com profissão `—` onde não há ofício escrito. P-3 mantido (linhagens do Vale distintas das de Aeltharyn).
- **D-113 aceita pela continuação** ("Siga"); nova **D-114** (aguardando o autor). Roadmap (14 de 37, 38%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 2).

## 2026-11-07 — R4-2.2: facções e lugares como dados do Vale Palancar (D-113)

- Novos `reinos/04-vale-palancar/12-facoes.csv` (14 facções, 11 colunas) e `12-assentamentos.csv` (8 lugares, 6 colunas), no molde do reino 3. Conferido por script: 0 campo vazio, 0 ID inexistente no registro, aliados e rivais simétricos, 0 aliado que seja rival. `recursos` é `desconhecido` (não se inventa); `lideranca_id` é `desconhecido` em Rasovau e Encosta-Aberta (Trato em disputa).
- **Correção:** o doc 04 e a D-108 diziam 3 rivais `desconhecido`; os dados mostram 2 (F-01 e F-10). Texto ajustado.
- **D-112 aceita pela continuação** ("Siga"); nova **D-113** (aguardando o autor). Roadmap (13 de 37, 35%), plano, README, README do reino, `PLANO_DOS_REINOS.md` e seção 6 do registro mestre ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 2).

## 2026-11-07 — R4-2.1: registro mestre do Vale Palancar (D-112)

- Novos `reinos/04-vale-palancar/12-registro-mestre.csv` e `.md` (formato D-21, com `povo_id`): 47 entidades (8 lugares, 14 facções, 25 pessoas; 19 vivas, 6 lendárias). 13 pessoas sem ficha (6 da tríade, 6 heróis lendários, Lumbo Pedra-de-Baixo) ganharam ID pelo nome já escrito, sem pessoa nova. Conferido por script: 0 ID duplicado, 0 ID do reino 4 citado nos docs sem registro, 0 colisão com os registros dos reinos 1 a 3, 0 `povo_id` fora do catálogo. Ghorla Fartura-Dividida (citada por nome no doc 06) agora tem ID.
- **D-111 aceita pela continuação** ("Siga"); nova **D-112** (aguardando o autor). Roadmap (12 de 37, 32%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 2).

## 2026-11-07 — R4-1.7: mistérios, relógios e oportunidades do Vale Palancar (D-111); fim da Fase 1

- Doc 01, parte 4: 10 mistérios (MPL-01 a MPL-10; Tabuleiro-Velho e Terreiro-Maior com dois, os demais lugares com um), cada um de rumor, suspeita ou problema já escrito, com 4 hipóteses abertas, vetos e provas possíveis. Nenhum explica a Neblina Longa, o Mistério D, o Mistério E ou o Conflito G, nem dá causa à Descida ou "verdade" a mistério do Véu; Aeltharyn, Coroas e Véu só em vetos.
- Novo `06-modelo-operacional-e-aventuras.md`: 8 relógios (`clock.reserva-em-ano-ruim`, `clock.dupla-cobranca`, `clock.desembarque`, `clock.cisterna`, `clock.sucessao-do-capitao`, `clock.campo-da-marcha`, `clock.guia-das-trilhas`, `clock.capim-disputado`; progresso 2/6 a 3/6, ASSUMINDO), 8 conflitos com causa concreta (pasto, água, passagem, sucessão de chefia, caça, tributo de caravana) e 8 oportunidades. Plano ajustado: a ata `11` é do R4-8.2, não do R4-1.7.
- **D-110 aceita pela continuação** (P-3 aceito no contrato); nova **D-111** (aguardando o autor). Roadmap (11 de 37, 30%; **F1 concluída**), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. 
- **Testes de fim de F1** (locais): validador do reino 2 = 0 erro, 40 avisos, 7 exceções, e do reino 3 = 0 erro, 22 avisos, 6 exceções (relatórios idênticos aos salvos); scripts Python compilam; reino 4: 42 IDs citados no doc 06 existem nos docs 01, 03, 04, 05 e 10 (Ghorla Fartura-Dividida, da tríade, só ganha ID no R4-2.1; achado corrigido citando-a por nome), 8 IDs `clock.*` sem colisão com os reinos 1 a 3, 10 mistérios com 4 hipóteses e 4 ou mais vetos, 0 número de tempo ou distância, status de README, plano, README do reino, `PLANO_DOS_REINOS.md`, contrato e `DECISOES.md` em sincronia (D-110 aceita, D-111 aguardando). Reinos 1 a 3, `mundo/`, `referencia/` e `referencia-tecnica/` sem alteração. 0 falha, 0 regressão.

## 2026-11-07 — R4-1.6: povos, linhagens, lugares e forasteiros do Vale Palancar (D-110)

- Novo `10-matriz-povos-nativos-cidades-e-forasteiros.md`: 12 nativos em ID do catálogo, 6 sub-raças (todas existem, raça-mãe correta), lugares × povos (8 de 8 com ao menos 2 nativos), 25 forasteiros (17 com lugar, 8 sem lugar fixo; gnomo e construto só visitantes, D-103), papéis por povo com lacunas abertas, mestiços só pela regra D-64. Conferido por script contra o catálogo, M-05 §4 e o doc 01 §3 (0 divergência).
- **P-3 proposto:** o Vale usa `orcano-das-estepes`, `felideo-das-dunas` e `centauro-das-pradarias`, sem parentesco com as de Aeltharyn; contrato §7 ajustado. Nenhum NPC novo, nenhum fluxo novo.
- **D-109 aceita pela continuação** ("Siga"); nova **D-110** (aguardando o autor). Roadmap (10 de 37, 27%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 1).

## 2026-11-07 — R4-1.5: história, economia, leis e fluxos do Vale Palancar (D-109)

- Parte 3 do `01-reino-do-vale-palancar.md`: 4 eras locais sem data, registro do nome "Palancar" (nome de reino, não de lugar; 2 versões de lenda, origem `desconhecida`), lendas dos 8 lugares com era, cronologia relativa em 6 passos, defesa e vulnerabilidades (nenhum inimigo externo afirmado). Cabeçalho do doc 01 ajustado.
- Novo `05-economia-leis-e-fluxos.md`: 10 leis (L-01 a L-10) com lacuna intencional (crime grave, exílio e pena de morte seguem `desconhecidos`), economia qualitativa dos 8 lugares, Descida (chegada em Praia-Pardo), Rota do Sal (partida de Alvasal) e Corredores Largos (saída por Encosta-Aberta), só do lado do Vale; fluxos conferidos por script contra a matriz de M-05 §4. Pergunta do contrato §6 (o que o Vale oferece à Descida e à Rota do Sal) mantida aberta.
- **D-108 aceita pela continuação**; nova **D-109** (aguardando o autor). Roadmap (9 de 37, 24%), plano, README, README do reino e `PLANO_DOS_REINOS.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 1).

## 2026-11-07 — R4-1.4: instituições, pactos e facções do Vale Palancar (D-108)

- Novo `reinos/04-vale-palancar/04-instituicoes-justica-pactos.md`: Assembleia de Clãs (oito assentos, maioria), Capitão de Marcha (prova ou aclamação, mandato limitado sem número, remoção, interregno), pactos de passagem e pasto, justiça e tributo qualitativos, oito Tratos (instituições) e 14 facções com aliados e rivais preenchidos (matriz simétrica, 3 rivais `desconhecido` com motivo). Um líder novo: Lumbo Pedra-de-Baixo (Leporino). Doc 01 recebeu as facções presentes por lugar.
- **D-107 aceita pela continuação** ("Siga"); nova **D-108** (aguardando o autor). Roadmap (8 de 37, 22%), plano, README e README do reino ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 1).

## 2026-11-07 — R4-1.3: NPCs principais e tríades do Vale Palancar (D-107)

- Parte 2 do `01-reino-do-vale-palancar.md`: 12 principais com ficha (ID, povo em ID do catálogo, lugar, agenda, pressão, limite, confirmado, suspeita, desconhecido), 2 generais (Kharos Casco-de-Marcha, Capitão de Marcha, residente na Vazante; Gruzk Estandarte-Baixo), 2 heróis vivos (Tessun Salto-Calado, Rukhan Retorno-Certo), tríade herói, guardião e benfeitor nos 8 lugares, 6 heróis lendários falecidos e cobertura dos 12 nativos. Nomes conferidos contra os reinos 1 a 3 (0 colisão exata e 0 quase-igual). Nenhum forasteiro como principal; nenhum mestiço.
- **D-106 aceita pela continuação** ("Siga"); nova **D-107** (aguardando o autor). Roadmap (7 de 37, 19%), plano, README e README do reino ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 1).

## 2026-11-07 — R4-1.2: rede política dos oito lugares do Vale Palancar (D-106)

- Novo `reinos/04-vale-palancar/03-assentamentos-e-rede-politica.md`: por lugar, lugar-mãe, liderança (papel), obrigação, interesse e tensão (nenhum campo vazio); posição relativa qualitativa; clãs sem lugar fixo entram por Trato, reserva e Assembleia (nenhum clã nomeado); pontos de contato. Sem fato novo além do vínculo de lugar-mãe (ASSUMINDO). Sem número, rota ou duração.
- **D-104 e D-105 aceitas pela continuação** ("Siga"); nova **D-106** (aguardando o autor). Roadmap (6 de 37, 16%), plano, README e README do reino ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 1).

## 2026-11-07 — R4-1.1: os oito lugares do Vale Palancar (D-105) e convenções de nome (D-104)

- Nova pasta `reinos/04-vale-palancar/` com `README.md` e `01-reino-do-vale-palancar.md` (R4-1.1): escala e governo (Assembleia de Clãs e Capitão de Marcha, D-102), residência dos governantes (Assembleia no Terreiro-Maior, Capitão na Vazante, sem sede fixa) e oito lugares com ficha padrão: **fixos** Tabuleiro-Velho, Rasovau, Praia-Pardo, Alvasal; **sazonais** Terreiro-Maior, Vazante, Encosta-Aberta, Gramal. Os 12 nativos aparecem; só forasteiros de M-05 §4; gnomos e construtos só como visitantes em Praia-Pardo (D-103). "Trato" como regra local (instituição até o R4-1.4). Liderança, facções, rotas e calendário adiados.
- `mundo/M-10-nomes-minimo.md` §9 (D-104): regras de não repetição para o reino 4 e famílias de epíteto dos 12 nativos, só como tom. Nomes conferidos contra os reinos 1 a 3 (0 colisão).
- Novas **D-104** e **D-105** (aguardando o autor). `REFINAMENTO_VALE_PALANCAR.md` (5 de 37, 14%), `PLANO_DOS_REINOS.md` e `README.md` ajustados. Nada de Aeltharyn, das Coroas ou do Véu alterado. Sem suíte de testes (só ao fim da Fase 1).

## 2026-11-07 — Fase 0 do reino 4 concluída: nome Vale Palancar, D-102 decidida, P-1 e P-2 resolvidos (D-103)

- **D-102** decidida pelo autor: nome **Vale Palancar**, escala "ok" (uma área só, oito lugares principais fixos e sazonais), governo "ok" (Assembleia de Clãs e Capitão de Marcha). O nome não cria lore nem relevo; "Marcas das Feras" fica como nome de trabalho antigo nos reinos 1 a 3, M-04 e M-05, sem reescrita.
- **D-103:** **P-1** resolvido: uma frase do doc 01 do Véu ("entra pelo leste das Marcas" → "chega das Marcas"), única ocorrência no projeto, sem mudar fato, ID ou dado. **P-2** resolvido: matriz de M-05 inalterada, com nota em M-05 §5 (gnomos e construtos só como visitantes de passagem onde a Descida chega).
- Plano e contrato renomeados: `plano/REFINAMENTO_VALE_PALANCAR.md` e `plano/CONTRATO_DE_FRONTEIRA_PALANCAR.md` (referências atualizadas). R4-0.3 feito; **Fase 0 concluída, 4 de 37 módulos (11%)**; R4-1.1 é o próximo, com D-104 (convenções de nome).
- **Testes:** validadores dos reinos 2 e 3 com relatórios idênticos aos salvos; só os arquivos listados acima mudaram em relação ao ZIP anterior.

## 2026-11-07 — Reino 4 liberado (D-101); plano e contrato de fronteira (R4-0.4); escala e governo propostos (D-102)

- O autor aprovou o fechamento 4 do Véu dos Malditos e liberou o reino 4 ("Vamos iniciar o reino 4"). **D-100** aprovada; **D-101** registra a liberação. Doc 11 e README do reino 3 passam a **[APROVADO]**; as lacunas listadas no doc 11 seguem como limite explícito.
- Novo `plano/REFINAMENTO_VALE_PALANCAR.md`: 37 módulos em 9 fases (prefixo `R4-`), sob D-63 e D-64, com 16 lições dos reinos 1 a 3 aplicadas (inclui conferir matriz × fluxo antes do doc 10 e conferir status entre README, plano e `DECISOES.md`). Andamento: 3 de 37 (R4-0.1, R4-0.2, R4-0.4).
- Novo `plano/CONTRATO_DE_FRONTEIRA_PALANCAR.md` (R4-0.4): 14 fixos de Aeltharyn (centauros, Conflito G, elevações ocidentais), 7 das Coroas (Argente, Descida), 8 do Véu (Rota do Sal, "sem rota" Ermo ↔ Marcas), 14 proibições e 6 ganchos permitidos. **Achados:** P-1, o doc 01 do Véu diz que o Argente "entra pelo leste das Marcas", o que M-04 G-06 não diz; P-2, M-05 §5 manda gnomos e construtos "para as Marcas", mas a matriz dá "—" a ambos nas Feras; P-3, linhagens próprias de Orcano, Felídeo Errante e Centauro; P-4, Lessa Cervo-Manso sem ID em Aeltharyn (lacuna herdada, não corrigida).
- Nova **D-102** (aguardando o autor): nome, escala (recomendada: uma área só, oito lugares fixos e sazonais), governo (recomendado: Assembleia de Clãs e Capitão de Marcha), pasta e os pontos P-1 e P-2. Sem ela o R4-1.1 não começa.
- Nada de Aeltharyn, das Coroas, do Véu, de `mundo/`, de `referencia/` ou de `referencia-tecnica/` foi alterado além dos status acima. Sem suíte de testes (só ao fim da Fase 0 do reino 4, depois de D-102).

## 2026-11-06 — R3-8.2: ata de fechamento 4 (D-100); Fase 8 concluída; reino 3 a 100%

- Novo `reinos/03-veu-dos-malditos/11-fechamento-do-veu-dos-malditos.md`: resultado das oito fases, 11 de 11 portas, 7 contradições intencionais e acidentais tratadas, decisões em aberto como limite explícito, critérios finais de uso e a ata. **Reino 4 (Marcas das Feras) não liberado:** só o autor libera (D-100). `PLANO_DOS_REINOS.md`, README e README do reino ajustados.
- **Testes de fim de F8** (locais): 18 CSV íntegros; conversor idempotente; validador do reino 3 = 0 erro, 22 avisos, 6 exceções; do reino 2 = 0 erro, 40 avisos, 7 exceções (relatórios iguais aos salvos); contagens do doc 11 conferem com os dados (390 registros, 41 fatos, 74 relações, 72 conhecimentos, 57 resumos); reinos 1 e 2, `mundo/`, `referencia/` e `referencia-tecnica/` idênticos ao ZIP de entrada. Achado e corrigido: o doc 11 listava D-98 e D-99 como pendentes, mas constam como aceitas pela continuação. 0 falha real, 0 regressão.
- Nova **D-100** (aguardando o autor); D-99 aceita pela continuação. Roadmap 100% (37/37).

## 2026-11-06 — R3-8.1: revisão final (D-99)

- Novo `plano/RELATORIO_R3_8_1.md`: portas 1 a 10 do checklist de `MODELO_DE_REINO.md` §6 marcadas (1, 3 e 6 com ressalva ou lacuna explícita), 11 pendente do R3-8.2; 8 contradições intencionais e 0 acidental nova (contagens de dados e docs conferidas). Nenhum lore nem texto alterado.
- Pendência: 3 facções com rival `desconhecido` (D-99, opção A recomendada: lacuna explícita).
- D-98 aceita pela continuação. Roadmap (97%, 36/37), README e README do reino ajustados. Sem suíte de testes (só ao fim da Fase 8).

## 2026-11-06 — R3-7.3: protótipo mínimo (D-98); Fase 7 concluída

- `plano/PROTOTIPO_MINIMO.md` §11: lugar Fio-Longo, NPC Ondra Fio-de-Sono e Morrovéu em uma linha (contexto B de 1.731 caracteres, texto exato registrado), condições A e B, roteiro de 8 turnos, métricas Q1 a Q10 e critério de aceite escritos antes de jogar; nenhuma sessão executada. ASSUMINDO: sem vilarejo nem cidade-mãe (D-66), usa-se uma cidade pequena e a capital.
- **Testes de fim de F7** (locais): 18 CSV íntegros; `converter_reino3.py` idempotente; validador do reino 3 = 0 erro, 22 avisos, 6 exceções (relatório igual ao salvo); validador do reino 2 = 0 erro, 40 avisos, 7 exceções (relatório idêntico); todos os IDs do §11 existem e as camadas conferem (restrito e secreto fora do contexto B, sem Aeltharyn nem duração); reinos 1 e 2, `mundo/`, `referencia/` e `referencia-tecnica/` idênticos ao ZIP de entrada. 0 falha real, 0 regressão.
- Nova **D-98** (aguardando o autor); D-97 aceita pela continuação. Roadmap (95%, 35/37), README e README do reino ajustados.

## 2026-11-06 — R3-7.2: catálogo técnico (D-97)

- `plano/MAPA_CATALOGO_KOTLIN.md` §7 (KV1 a KV8): `.kt` sem entidade do Véu, 0 colisão com os 191 IDs de `dados/`, 11 nativos fora dos 16 povos do `.kt`, `feerico`, `fungal` e `plantae` compartilhados, "neblina" e "pactos" do `.kt` só de Aeltharyn, V-A1 conferida à mão (8 de 8 lugares). Nada instalado, `.kt` não alterado nem executado.
- `converter_reino3.py`: papéis de tríade vêm do doc 01 §3; `dados/` regerado, validador igual (0 erro, 22 avisos, 6 exceções).
- Nova **D-97** (aguardando o autor); D-96 aceita pela continuação. Roadmap (92%, 34/37), README e README do reino ajustados.

## 2026-11-06 — R3-7.1: dados e validador (D-96)

- Novos `plano/ferramentas/converter_reino3.py` e `reinos/03-veu-dos-malditos/dados/` (21 `.txt`, 390 registros, formato D-21) com `relatorio-validador.txt` e `.md`: **0 erro, 22 avisos, 6 exceções** (4 eventos com rota do doc 07 sem dois locais, 2 rotas de par repetido; avisos restantes esperados: fonte "proposto", 5 heróis sem lugar, 8 lugares, sem herdeiro, 3 rivais `desconhecido`).
- `validar_reino.py` generalizado (colisão de ID contra reinos de número menor, `profissao` `—` aceita, V-34 por reino); relatório do reino 2 regerado e idêntico. Reino 2, `mundo/` e `referencia/` não alterados.
- Nova **D-96** (aguardando o autor); D-95 aceita pela continuação. Roadmap (89%, 33/37), README e README do reino ajustados. Sem testes de suíte (só ao fim da Fase 7).

## 2026-11-06 — R3-6.4: contrato anti-invenção (D-95); Fase 6 concluída

- Doc 06 do reino 3 ganhou a seção 9 (contrato anti-invenção): verificação de proposta, quatro classes (fixo, livre, registro, proibido), lista proibida (segundo rei, Provação vencida ou abolida, titular do assento de Morrovéu, 9º lugar, 12º povo, ressurreição de herói lendário, guerra, mistério resolvido, custo da estabilização, povo mau por natureza, número ou duração inventados, relógio sem gatilho, rota ou fala sobre Aeltharyn além do contrato de fronteira), 7 casos e teste rápido. Conferências do §7 atualizadas. Herda os contratos de Aeltharyn e das Coroas sem alterá-los.
- **Testes de fim de F6** (locais): 18 CSV íntegros (463 linhas), contagens da §9.2 conferem com os dados, 0 referência ausente, reinos 1 e 2, `mundo/`, `referencia/` e `referencia-tecnica/` idênticos ao ZIP de entrada, validador no reino 2: 0 erro, 40 avisos, 7 exceções (relatório idêntico). 0 falha real, 0 regressão.
- Nova **D-95** (aguardando o autor); D-94 aceita pela continuação. Roadmap (86%, 32/37), README e README do reino ajustados.

## 2026-11-06 — R3-6.3: cenas-teste (D-94)

- Novos `reinos/03-veu-dos-malditos/15-cenas-de-teste.md` e `.csv` (5 colunas, molde do reino 2): 6 cenas com "entra" (1.614 a 1.901 caracteres) e "não entra". Conferido por script: 0 marcador restrito ou secreto, 0 relógio, 0 `nunca_diz` em "entra".
- Nova **D-94** (aguardando o autor); D-93 aceita pela continuação. Roadmap (84%, 31/37), README e README do reino ajustados.

## 2026-11-06 — R3-6.2: camadas de informação (D-93)

- Novos `reinos/03-veu-dos-malditos/14-camadas-de-informacao.md` e `.csv` (11 colunas, molde do reino 2): 41 fatos em 4 camadas, com caminho e como descobrir; 0 marcador de fato restrito ou secreto em resumo `local`.
- Alterados: `12-fichas.csv` e `12-fichas.md` (campo *Camadas de informação* das 12 fichas preenchido, antes adiado) e `14-resumos-de-contexto.md` (`camada_max` definitiva). Nada mais mudou.
- Nova **D-93** (aguardando o autor); D-92 aceita pela continuação. Roadmap (81%, 30/37), README e README do reino ajustados.

## 2026-11-06 — R3-6.1: resumos de contexto (D-92)

- Novos `reinos/03-veu-dos-malditos/14-resumos-de-contexto.md` e `.csv` (8 colunas, molde do reino 2): 57 entidades com resumo ≤500 caracteres (maior 486) e vizinhança ≤300 (maior 175). Conferido por script: todos os IDs do registro mestre e dos relógios cobertos, 0 vazamento de suspeita ou desconhecido, 0 campo vazio. `camada_max` provisória até o R3-6.2.
- Nova **D-92** (aguardando o autor); D-91 aceita pela continuação. Roadmap (78%, 29/37), README e README do reino ajustados.

## 2026-11-06 — R3-5.5: mistérios com limites (D-91); Fase 5 concluída

- Novos `reinos/03-veu-dos-malditos/13-misterios-limites.md` e `.csv` (9 colunas, molde do reino 2): MV-01 a MV-09 do doc 01 como dados, 4 hipóteses cada, vetos e provas do texto, ligação a relógio e evento (8 relógios cobertos).
- **Testes de fim de F5** (locais; o validador do reino 2 não se aplica sem `dados/`): 15 CSV íntegros (359 linhas), 0 ID fora do registro, 0 falecido como agente, rotas, relógios e eventos existentes, 0 termo de fronteira ou número de prazo nos dados de F5, 0 aliado-rival, 130 arquivos de reinos 1 e 2, `mundo/` e `referencia/` idênticos ao ZIP de entrada. Único alerta: 5 caminhos abreviados com `...` em referências, arquivos existentes (falso positivo).
- Nova **D-91** (aguardando o autor); D-90 aceita pela continuação. Roadmap (76%, 28/37), README e README do reino ajustados.

## 2026-11-06 — R3-5.4: reação do reino ao jogador (D-90)

- Novos `reinos/03-veu-dos-malditos/13-reacao-ao-jogador.md`, `13-reputacao.csv` (19 linhas) e `13-canais.csv` (11 linhas): fama e reputação qualitativas, estados de procurado, ato-regra-autoridade por lei, favor, reparação (lacuna) e intermediários, ligados a MP-07 e MP-06. Conferido por script: 0 ID fora do registro, 0 falecido, 0 rota fora do doc 07, 0 campo vazio, 0 termo de fronteira.
- Nova **D-90** (aguardando o autor); D-89 aceita pela continuação. Roadmap (73%, 27/37), README e README do reino ajustados.

## 2026-11-06 — R3-5.3: sementes de aventura (D-89)

- Novos `reinos/03-veu-dos-malditos/13-sementes-de-aventura.md` e `.csv` (11 colunas, molde do reino 2): 24 sementes, 8 por faixa (ameaça P 1; M 2 a 3; G 3 a 4). Conferido por script: 0 agente fora do registro ou falecido, 0 relógio ou evento inexistente, 0 campo vazio, 0 termo de fronteira em semente.
- Nova **D-89** (aguardando o autor); D-88 aceita pela continuação. Roadmap (70%, 26/37), README e README do reino ajustados.

## 2026-11-06 — R3-5.2: eventos-tipo e cadeias causais (D-88)

- Novos `reinos/03-veu-dos-malditos/12-eventos.md` e `12-eventos.csv` (10 colunas, molde do reino 2): 12 eventos, os 8 relógios todos cobertos, cadeias só com dependências e leis já documentadas. Conferido por script contra o registro mestre, `12-relogios.csv` e as rotas do doc 07: 0 ID inexistente, 0 campo vazio, 0 termo de fronteira em evento.
- Nova **D-88** (aguardando o autor); D-87 aceita pela continuação. Roadmap (68%, 25/37), README e README do reino ajustados.

## 2026-11-06 — R3-5.1: relógios (D-87)

- Novos `reinos/03-veu-dos-malditos/12-relogios.md` e `12-relogios.csv` (12 colunas, molde do reino 2): 8 relógios de `06` com gatilhos observáveis, trava, quem paga, decisão no segmento 5, efeito ao completar e da inação. Conferido por script: 8 de 8 com move e trava, 0 campo vazio, 0 entidade fora do registro mestre, 12 principais cobertos.
- Nova **D-87** (aguardando o autor); D-86 aceita pela continuação. Roadmap (65%, 24/37), README e README do reino ajustados.

## 2026-11-06 — R3-4.4: voz e ganchos de diálogo (D-86); Fase 4 concluída

- Novos `reinos/03-veu-dos-malditos/12-voz.md` e `12-voz.csv`: 12 principais com tom, 2 a 3 frases-modelo e `nunca_diz` (molde dos reinos 1 e 2). Nenhuma frase revela suspeita ou desconhecido.
- **Testes de fim de F4** (locais; o validador do reino 2 não se aplica sem `dados/`): 9 CSV íntegros (276 linhas), 0 ID fora do registro, voz = fichas, competências no catálogo, 0 aliado-rival, 0 relação sem fonte, 0 alteração em Aeltharyn e Coroas. Dois alertas do script eram falsos positivos (células vazias do registro mestre por desenho; caminhos abreviados válidos).
- Nova **D-86** (aguardando o autor); D-85 aceita pela continuação. Roadmap (62%, 23/37), README e README do reino ajustados.

## 2026-11-06 — R3-4.3: relações e conhecimento (D-85)

- Novos `reinos/03-veu-dos-malditos/12-relacoes.md`, `12-relacoes.csv` (74 relações: alianças, rivalidades, dívidas de lugar, tensões, vínculos de cargo, medos; 0 afeto, nenhum doc o documenta) e `12-conhecimento.csv` (72 linhas, confirmado, suspeita, desconhecido, 30 de 30 pessoas), formato do reino 2. Conferido por script: 0 relação sem fonte, 0 ID fora do registro mestre, 0 aliado-rival.
- Nova **D-85** (aguardando o autor); D-84 aceita pela continuação. Roadmap (59%, 22/37), README e README do reino ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-06 — R3-4.2: competências pelo catálogo (D-84)

- Novos `reinos/03-veu-dos-malditos/12-competencias.md` e `12-competencias.csv` (formato do reino 1 e 2): 30 pessoas com raça, sub-raça, classe, profissão, capacidades, limites e evidência. Capacidades e limites só repetem fichas e docs; benfeitores sem ficha com meios `desconhecidos`; heróis lendários com limite padrão. Conferido por script contra `personagem-catalogo.json`: 0 ID fora do catálogo, 0 sub-raça com raça-mãe errada, 0 raça diferente de `povo_id`, 0 campo vazio.
- Nova **D-84** (aguardando o autor); D-83 aceita pela continuação. Roadmap (57%, 21/37), README e README do reino ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-06 — R3-4.1: fichas completas dos doze principais (D-83)

- Novos `reinos/03-veu-dos-malditos/12-fichas.md` e `12-fichas.csv` (17 colunas, molde do reino 2): 12 de 12 principais completos, 12 agendas e 12 pressões distintas, povo, sub-raça, classe e profissão de `ALINHAMENTO_VEU.md`, relógio de `06` ligado a cada um, aliados e rivais da facção (doc 04), condição de evolução (ASSUMINDO). 6 acúmulos de cargo marcados (CI-09). Coluna `camadas` `—` até R3-6.2. Conferido por script: 12 IDs no registro mestre, 0 campo vazio.
- Nova **D-83** (aguardando o autor); D-82 aceita pela continuação. Roadmap (54%, 20/37), README e README do reino ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-05 — R3-3.4: calendário e estações (D-82); fim da Fase 3

- Doc 01, parte 5: 12 luas numeradas, quatro estações (Névoa Fechada, Névoa Solta, Charco Aberto, Névoa Voltando), cinco Dias de Pacto, Contagem das Provações (época `desconhecida`), início do jogo no ano 389, igual ao ano 742 de Aeltharyn (deslocamento de 353, ASSUMINDO, reversível), tabela de conversão e agenda sazonal só de condição. `mundo/M-02` ganhou o §7 e `mundo/M-10` o §8 (nomes reservados). Cabeçalhos dos docs 01 e 03 sem campo adiado.
- **Fase 3 concluída.** Testes: validador do reino 2 sem regressão (0 erros, 40 avisos, 7 exceções); conferências do reino 3 sem pendência (49 IDs únicos, 0 citado sem registro, 0 campo vazio nos três CSVs, 0 `povo_id` fora do catálogo, 0 colisão com os reinos 1 e 2, 0 referência a arquivo inexistente, 0 colisão dos nomes novos); comparação com o ZIP original: só `M-02` e `M-10` mudaram fora do reino 3 (extensões previstas no roadmap), 0 arquivo de Aeltharyn ou das Coroas alterado.
- Nova **D-82** (aguardando o autor); D-81 aceita pela continuação. Roadmap (51%, 19/37), README e README do reino ajustados.

## 2026-11-05 — R3-3.3: recursos e matriz material (D-81)

- Novos `08-registro-operacional-de-recursos.md` e `09-matriz-material-dos-lugares.md`: termos qualitativos para os 8 lugares ("pobre em" só onde o doc 01 registra falta ou vulnerabilidade; `—` onde não há registro); 9 dependências (7 críticas, cada uma com fonte; nenhum lugar isolado); 3 fluxos (Descida, Rota do Sal, tributo em pactos). A escala de 0 a 5 do reino 2 não é repetida (ASSUMINDO; opção B de D-81). Conferido por script: 0 ID sem registro, 0 número ou unidade.
- Nova **D-81** (aguardando o autor); D-80 aceita pela continuação. Roadmap (49%, 18/37), README e README do reino ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-05 — R3-3.2: demografia (D-80)

- Doc 05 ganhou a seção 5 (demografia; Conferências e Decisão passam a 6 e 7) e novo `12-demografia.csv` (8 lugares, molde do reino 2): faixas de porte relativo já presentes no doc 01 (1 grande, 3 médias, 4 pequenas), evidência e confiança, e a regra de não somar (população flutuante, Corte, guarda, mortos e proporção entre povos fora de qualquer faixa). Conferido por script: 0 diferença entre doc 01 e CSV, 0 número ou censo.
- Cabeçalho do doc 05 ajustado. Nova **D-80** (aguardando o autor); D-79 aceita pela continuação. Roadmap (46%, 17/37), README e README do reino ajustados.

## 2026-11-05 — R3-3.1: rotas e mapa esquemático (D-79)

- Novos `07-registro-operacional-de-rotas.md` (7 ligações, 8 acessos dos lugares, 7 linhas de fronteira) e `07-mapa-esquematico.md` (diagrama de relações e posição relativa que já é cânone). Os docs do reino 3 não trazem duração nem "curta" ou "longa" para nenhum percurso: toda duração `desconhecida`. 0 rota Véu–Aeltharyn, 0 passagem nova, 0 rota ou tempo inventado; IDs `route.*` sem colisão com os reinos 1 e 2.
- Cabeçalhos dos docs 01 e 03 ajustados (rotas deixam de ser adiadas). Nova **D-79** (aguardando o autor); D-78 aceita pela continuação. Roadmap (43%, 16/37), README e README do reino ajustados.

## 2026-11-05 — R3-2.4: conferência cruzada (D-78); fim da Fase 2

- Novo `reinos/03-veu-dos-malditos/13-conferencia-cruzada.md`: 0 aliado-rival, 0 assimetria, 0 liderança fora da presença, 0 povo fora dos presentes do lugar, 0 diferença de presença entre doc 01 e CSV; 7 acúmulos de cargo marcados como intencionais propostos; mapa de quem sabe o quê dos 9 mistérios; CI-01 a CI-12.
- Corrigidos os cabeçalhos dos docs 01 e 03, que ainda listavam como adiado o que já estava escrito.
- **Fase 2 concluída.** Testes: validador do reino 2 sem regressão (0 erros, 40 avisos, 7 exceções); conferências por script do reino 3 sem pendência (49 IDs únicos, 0 citado sem registro, 0 campo vazio, 0 ID fora do catálogo, 0 colisão com os reinos 1 e 2); comparação com o ZIP anterior: 0 arquivo de Aeltharyn ou das Coroas alterado.
- Nova **D-78** (aguardando o autor); D-77 aceita pela continuação. Roadmap (41%, 15/37), README e README do reino ajustados.

## 2026-11-05 — R3-2.3: povos e catálogo (D-77)

- Novo `referencia/racas-e-povos/ALINHAMENTO_VEU.md`: 11 nativos, 10 sub-raças e 17 forasteiros conferidos contra `personagem-catalogo.json` (todos existem, nomes iguais, raça-mãe correta) e raça, sub-raça, classe e profissão das 30 pessoas do registro mestre. Classe e profissão são leitura editorial (ASSUMINDO); profissão `—` onde não há ofício escrito. Conferido por script: 0 ID fora do catálogo, toda sub-raça no lugar da pessoa.
- Nova **D-77** (aguardando o autor); D-76 aceita pela continuação. Roadmap (38%, 14/37), README e README do reino ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-05 — R3-2.2: facções e lugares como dados (D-76)

- Novos `reinos/03-veu-dos-malditos/12-facoes.csv` (11 facções) e `12-assentamentos.csv` (8 lugares; `cidade_mae` `—` por D-66), no molde do reino 2, derivados dos docs 03 e 04 e do registro mestre. 0 campo vazio; `recursos` das facções `desconhecido`; `tensao` da facção = a do lugar-sede (ASSUMINDO). Conferido por script: 0 assimetria, 0 aliado-rival, 0 ID inexistente, 3 rivais `desconhecido` com motivo.
- `12-registro-mestre.md` ganhou §6 sobre os dois arquivos. Nova **D-76** (aguardando o autor); D-75 aceita pela continuação. Roadmap (35%, 13/37), README e README do reino ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-05 — R3-2.1: registro mestre do Véu dos Malditos (D-75)

- Novos `reinos/03-veu-dos-malditos/12-registro-mestre.csv` e `.md` (formato D-21, com `povo_id`): 49 entidades (8 lugares, 11 facções, 30 pessoas; 19 vivas, 11 lendárias). 18 pessoas sem ficha (7 da tríade, 11 heróis lendários) ganharam ID pelo nome já escrito, sem pessoa nova. Conferido por script: 0 ID duplicado, 0 ID citado sem registro nos docs 01, 03, 04, 05, 06 e 10, 0 colisão com os registros dos reinos 1 e 2, 0 `povo_id` fora do catálogo.
- Nova **D-75** (aguardando o autor); D-74 aceita pela continuação. Roadmap (32%, 12/37), README e README do reino ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-05 — R3-1.7: mistérios, relógios e oportunidades (D-74); fim da Fase 1

- Doc 01, parte 4: 9 mistérios (MV-01 a MV-09; Morrovéu e Candeia-Morta com dois cada, os demais lugares com um) com 4 hipóteses abertas, vetos e provas possíveis; limites da necromancia do Lord (o que não faz, o que fica aberto de propósito). Nenhum mistério explica a Neblina Longa, o Mistério E ou o Mistério D; Aeltharyn só em vetos.
- Novo `06-modelo-operacional-e-aventuras.md`: 8 relógios (`clock.desafiantes`, `clock.partida`, `clock.fardos`, `clock.acertos-orais`, `clock.pacto-que-vence`, `clock.orla-sem-guia`, `clock.hospedes`, `clock.despedidas`; progresso 1/6 a 3/6, ASSUMINDO), 8 conflitos com causa concreta e 8 oportunidades. Conferido por script: 31 IDs citados existem nos docs 01, 03, 04, 05 e 10; 0 colisão de ID de relógio com os reinos 1 e 2; 0 número ou tempo novo.
- Nova **D-74** (aguardando o autor); D-73 aceita pela continuação. Roadmap (30%, 11/37; **F1 concluída**), README e README do reino ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-05 — R3-1.6: povos, sub-raças e forasteiros (D-73)

- Novo `10-matriz-povos-nativos-cidades-e-forasteiros.md`: 11 nativos (IDs do catálogo), 10 sub-raças, lugares × povos (8 de 8 com ≥ 2 povos), 17 forasteiros (12 com lugar, 5 sem lugar fixo), papéis por povo e 5 heróis lendários novos (`npc.mursk-juramento-de-bando`, `npc.zibb-rede-primeira`, `npc.aun-silencio-primeiro`, `npc.lish-rosto-primeiro`, `npc.xalu-no-primeiro`). Conferido por script: 0 ID fora do catálogo, 0 colisão de nome ou ID com os reinos 1 e 2. Lupino e Corvário seguem distintos de Metamorfo Lupino e Corvídeo.
- `M-10` §7: 5 nomes reservados. Nova **D-73** (aguardando o autor); D-72 aceita pela continuação. Roadmap (27%, 10/37), README e README do reino ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-05 — R3-1.5: história, leis, economia e fluxos (D-72)

- Parte 3 do doc 01: 4 eras locais, lendas dos 8 lugares e dos 6 heróis lendários com era e o que fica `desconhecido`, cronologia relativa em 8 passos (sem datas, sem sincronia com Aeltharyn ou Coroas), defesa e vulnerabilidades (nenhum inimigo externo afirmado).
- Novo `05-economia-leis-e-fluxos.md`: 9 leis (L-01 a L-09) com lacunas intencionais (necromancia fica aberta em L-01 e L-07), economia qualitativa dos 8 lugares, chegada da Descida do Argente em Fozbreu e saída das Fugas do Véu por Lamaluz, só do lado do Véu (contrato §3 itens 10 e 11 respeitados). 0 número, 0 rota, 0 ano de Aeltharyn.
- Nova **D-72** (aguardando o autor); D-71 aceita pela continuação. Roadmap (24%, 9/37), README e README do reino ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-05 — R3-1.4: instituições, pactos e facções (D-71)

- Novo `reinos/03-veu-dos-malditos/04-instituicoes-justica-pactos.md`: Corte e Conselho dos Pactuários (8 assentos), competências, 8 pactos, Provação das Armas e sucessão (ASSUMINDO), justiça, tributo qualitativo, governo local, 11 facções (F-01 a F-11) com líder, objetivo, métodos, aliados e rivais, 8 instituições sem ID de facção, 5 situações iniciais. Matriz conferida por script: 0 assimetria, 0 aliado-rival, 0 colisão de ID ou nome com os reinos 1 e 2; 3 rivais `desconhecido` com motivo. 0 pessoa nova.
- Doc 01: campo "Facções presentes" dos 8 lugares preenchido. `M-10` §7: facções reservadas. Nova **D-71** (aguardando o autor); D-70 aceita pela continuação. Roadmap (22%, 8/37), README e README do reino ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-05 — R3-1.3: NPCs principais e tríades (D-70)

- Parte 2 do `reinos/03-veu-dos-malditos/01-reino-do-veu-dos-malditos.md`: 12 principais com ficha (confirmado, suspeita, desconhecido), 2 heróis vivos, 2 generais, 6 heróis lendários, tríade dos 8 lugares, cobertura dos 11 nativos. Povo do Lord: Humano (ASSUMINDO). Nomes conferidos: 25 sem colisão com os registros dos reinos 1 e 2 e com `M-10`; 12 IDs `npc.*` sem colisão.
- `mundo/M-10-nomes-minimo.md` §7: nomes reservados do reino 3. Nova **D-70** (aguardando o autor); D-68 e D-69 aceitas pela continuação ("Siga"). Roadmap do reino 3 (19%, 7/37), README e README do reino ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-05 — R3-1.2: rede política dos oito lugares (D-69)

- Novo `reinos/03-veu-dos-malditos/03-assentamentos-e-rede-politica.md`: ficha política dos oito lugares (liderança por papel, obrigação, interesse, tensão), posição relativa qualitativa e pontos de contato; tudo derivado do doc 01. Sem assentamentos próprios nem cidade-mãe (D-66); 0 número, 0 rota, 0 campo vazio; nenhum nome novo.
- Nova **D-69** (aguardando o autor). `REFINAMENTO_VEU_DOS_MALDITOS.md` (andamento, primeira rodada, progresso 16%), `README.md` e `reinos/03-.../README.md` ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-05 — R3-1.1: os oito lugares do Véu dos Malditos (D-68)

- **D-66 fechada:** o autor confirmou a escala ("Ok"): uma área só, capital do rei e sete lugares de pacto; classe do Lord = Invocador (necromante fora do catálogo; catálogo inalterado). Nome, governo e escala decididos.
- Nova pasta `reinos/03-veu-dos-malditos/` com `README.md` e `01-reino-do-veu-dos-malditos.md` (R3-1.1): regra de duas camadas (coroa conquistada na Provação das Armas; pactos sobrevivem ao rei), o Rei Vencedor Lord Morvan Voz-Baixa (cerca de cem anos de reinado, sem ligação com a Neblina Longa) e oito lugares com ficha padrão: Morrovéu, Lamaluz, Fozbreu, Beira-Sal, Selovale, Borda-Mansa, Fio-Longo, Candeia-Morta. Os 11 nativos aparecem; só forasteiros de M-05 §4. Nomes conferidos contra os reinos 1 e 2 (0 colisão). Liderança, facções, rotas e calendário adiados.
- **Ponto a conferir em M-05:** Descida do Argente cita anões, gnomos e construtos no Véu, mas a matriz dá "—"; no doc 01 são só visitantes em Fozbreu até decisão do autor.
- Nova **D-68** (aguardando o autor). `REFINAMENTO_VEU_DOS_MALDITOS.md` (andamento e primeira rodada) e `README.md` ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-05 — D-66 respondida (nome e governo do reino 3)

- **D-66:** nome decidido (Véu dos Malditos; "o Véu" só para a faixa). Governo decidido pelo autor: autoridades ligadas aos pactos sob um rei conquistado em batalha, o reino com mais trocas de rei, hoje com um só rei há cerca de cem anos, um Lord necromante que estabilizou as terras; história e o resto delegados ao redator. Escala: 10 cidades e 20 assentamentos recusados ("apenas uma área"); número de lugares a confirmar. Limites registrados (sem ligação com a Neblina Longa, necromancia não é traço de raça, necromante fora do catálogo de classes).
- `REFINAMENTO_VEU_DOS_MALDITOS.md` (andamento e primeira rodada) e `README.md` ajustados. Nenhum documento de Aeltharyn ou das Coroas alterado.

## 2026-11-05 — Abertura do reino 3: D-65, D-66, D-67 e R3-0.4

- **D-62 aprovada pelo autor** ("Vamos seguir pro reino 3", opção A) e **D-65**: reino 3 (Véu dos Malditos) liberado; reinos 4 e 5 seguem bloqueados. Doc 11 do reino 2 passa a **[APROVADO]** (estado, critérios finais, ata e caixa de aprovação); `REFINAMENTO_COROAS_DE_PEDRA.md` (andamento R2-8.2), `PLANO_DOS_REINOS.md` (linhas dos reinos 2 e 3) e `README.md` (estado) alinhados. Nenhum dado, ID ou documento de Aeltharyn ou das Coroas alterado além desses status.
- Novo `plano/REFINAMENTO_VEU_DOS_MALDITOS.md`: ponto de partida (M-04 e M-05), lições dos reinos 1 e 2, 37 módulos em 9 fases (`R3-0.1` a `R3-8.2`), já com D-63 (escala qualitativa, sem camada de números) e D-64 (sem mestiços alocados). R3-0.1, R3-0.2 e R3-0.4 feitos; R3-0.3 proposto.
- Novo `plano/CONTRATO_DE_FRONTEIRA_AELTHARYN_VEU.md` (R3-0.4): 18 fixos de Aeltharyn (Brumafunda, Bosque dos Sussurros, Mistério E, Neblina Longa, rotas do doc 07, Lei de Proteção de Refugiados, Orvass), 11 coisas que o reino 3 não pode afirmar, ganchos permitidos, pontos de contato com as Coroas (Descida do Argente) e com os reinos 4 e 5 (não escritos), conferência para o R3-8.1 e riscos (nome "Véu" faixa × reino; Corvídeo × Corvário).
- `mundo/M-10-nomes-minimo.md` §6 (W-04): nomes reservados das Coroas (45 pessoas, 10 cidades, 20 assentamentos, 14 facções) e famílias de epíteto dos 11 nativos, só como tom.
- Novas **D-66** (escala, nome, governo e pasta) e **D-67** (nomes), ambas aguardando o autor. Nenhuma cidade do reino 3 escrita; a pasta `reinos/03-...` ainda não foi criada.

## 2026-11-05 — D-64

- Nova D-64 (decidida pelo autor): mestiços não são alocados a reino, cidade ou facção; nascem na cidade de qualquer um dos pais, sem lista fechada de 20 (qualquer par de raças ou sub-raças do catálogo), e o jogador escolhe. `hibridos.md` reescrito (regra + 20 exemplos); doc 10 do reino 2 §3 e §4 (coluna removida) e §8; `ALINHAMENTO_COROAS.md` §1.2; `ALINHAMENTO_AELTHARYN.md`; `M-05` §7; `MATRIZ_POVOS_POR_REINO.md`; `PLANO_RACAS_E_SUBRACAS.md` (requisito de seletor livre); doc 11 (critério de uso); README e plano do reino 2. Nenhum dado em `dados/` e nenhum CSV alterados.

## 2026-11-05 — D-63

- Nova D-63 (decidida pelo autor): escala qualitativa para todos os reinos; distância, tamanho e produção sem número. Lacunas numéricas do reino 2 (duração de rota, população, estoque, produção) viram escolha de escopo, não pendência; reinos 3 a 5 não escrevem a camada de números. Nenhum dado, esquema ou documento de reino alterado. `README.md`: regra 0 acrescentada.

## 2026-11-05 — R2-8.2 e fim da Fase 8

- Novo `reinos/02-.../11-fechamento-das-coroas-de-pedra.md` (doc 11): resultado executivo, as oito fases, 11 de 11 portas, contradições intencionais (5) e acidentais tratadas, decisões em aberto como limite explícito, critérios finais de uso e a Ata de fechamento 3. **Reino 3 (Véu dos Malditos) não liberado:** aguarda a aprovação do autor (D-62).
- `DECISOES.md`: D-51 a D-53 alinhadas ao plano, README e CHANGELOG (aceitas pela continuação em 04/11/2026; constavam como "aguardando" por descuido). README: "Reinos 2 a 5 bloqueados" corrigido para "3 a 5".
- **Fase 8 do reino 2 concluída; roadmap do reino 2 em 100% (37/37).** Suíte local: validador rodado de novo, 0 erro, 40 avisos, 7 exceções; 0 link quebrado em todos os `.md`; 0 referência a arquivo inexistente no doc 11; contagens do doc 11 iguais aos dados (10 cidades, 20 assentamentos, 45 pessoas, 14 facções, 7 relógios, 13 eventos, 90 sementes, 41 fatos, 6 mistérios, 12 nativos); diff contra o ZIP anterior: só documentos de plano, README, CHANGELOG e o doc 11 novo mudaram (0 dado, CSV, arquivo de Aeltharyn, referência, referência técnica ou mundo alterado). O teste Kotlin de referência não foi executado (sem compilador) e não toca o reino 2.
- Nova D-62 (aguardando o autor). Progresso: 100% (37/37).

## 2026-11-05 — R2-8.1

- Novo `plano/RELATORIO_R2_8_1.md`: as 11 portas de `MODELO_DE_REINO.md` §6 contra os dados do reino 2. Portas 1 a 10 marcadas (3: 13 rotas todas `desconhecida`; 6: aliados `?` em 1 facção e rivais `?` em 4, lacuna explícita); porta 11 pendente do R2-8.2 (doc 11). 12 principais com 12 agendas e 12 pressões distintas, 2 heróis vivos, 2 generais; 6 mistérios com 4 hipóteses; 7 relógios sem avanço automático; 0 aliança ou rivalidade assimétrica entre facções; contagens de 10 tipos de entidade iguais nos dados e nos documentos.
- Contradições: 4 intencionais listadas para o doc 11; **1 acidental corrigida:** `CONTRATO_DE_FRONTEIRA_AELTHARYN_COROAS.md` §2 ("anões, gnomos e os demais 12 nativos" → 12 nativos no total). Nenhum dado, lore ou arquivo de Aeltharyn alterado. Nova D-61 (aguardando o autor). Progresso: 97% (36/37).

## 2026-11-05 — R2-7.3 e fim da Fase 7

- `plano/PROTOTIPO_MINIMO.md` ganhou a seção 10 (reino 2): recorte Rastro-Branco + Miraq Salto-de-Névoa + Altavento em uma linha; contexto B de 1.192 caracteres (1.064 da cena + 1 + 127 da linha); condição A (sem mundo); roteiro fixo de 8 turnos; regras de classificação pelo contrato do doc 06 §9; métricas Q1 a Q10; critério de aceite (passa, falha dura, inconclusivo) escrito antes da sessão; seção de resultados vazia. Seções 1 a 9 (Aeltharyn) intactas. Nenhuma sessão executada.
- **Fase 7 do reino 2 concluída.** Suíte local: validador rodado de novo, 0 erro, 40 avisos, 7 exceções (igual a R2-7.1); 0 link quebrado em todos os `.md`; 0 ID citado na seção 10 sem definição em `dados/`; 0 colisão de ID com o `.kt`; diff contra o ZIP anterior: só CHANGELOG, README, DECISOES, MAPA_CATALOGO_KOTLIN, PROTOTIPO_MINIMO e REFINAMENTO_COROAS_DE_PEDRA mudaram (0 arquivo de Aeltharyn, referência, referência técnica, mundo ou reino 2 alterado). O teste Kotlin de referência não foi executado (sem compilador) e não toca o reino 2.
- Nova D-60 (aguardando o autor). Progresso: 95% (35/37).

## 2026-11-05 — R2-7.2

- `plano/MAPA_CATALOGO_KOTLIN.md` ganhou a seção 5 (Montanhas no `.kt`, K1 a K9): 0 entidade das Coroas no `.kt`; 0 colisão de ID (69 IDs do `.kt` contra o reino 2); `.kt` é singleton de Aeltharyn (não serve de carregador do reino 2); `mountain_pass` fora do enum `meio` (lacuna do esquema, nenhum valor criado); divergências de duração (4 × ~3 dias) e população (17.000 × ~18.000) de Torvelume listadas; `clock.sealed-mine` fica com Aeltharyn e o Mistério D. Seção 6 (limites) atualizada.
- Nenhum código, `.kt`, dado ou arquivo de Aeltharyn alterado; nada instalado ou executado. Nova D-59 (aguardando o autor). Progresso: 92% (34/37).

## 2026-11-05 — R2-7.1

- Novos `reinos/02-.../dados/`: 21 arquivos `.txt` no formato D-21 (texto com `|`, `# esquema=1`, `?` e `—`, `reino` e `fonte`), 592 registros, gerados por `plano/ferramentas/converter_reino2.py` sem acrescentar fato. Novo `plano/ferramentas/validar_reino.py` (regras V-01 a V-74 e V-99; só Python padrão). Teste do validador numa cópia quebrada: 7 defeitos plantados, todos detectados.
- Primeiro relatório: 0 erro, 40 avisos (V-06, V-23, V-26, V-27, V-31, V-34, V-42 e as exceções), 7 exceções em `excecoes.txt`. `dados/relatorio-validador.md` e `.txt`.
- **Correção:** 5 IDs de canal colidiam com Aeltharyn (V-03); os 11 canais do reino 2 viraram `canal.coroas-*` em `13-canais.csv` e `13-reacao-ao-jogador.md`. Nenhum arquivo de Aeltharyn alterado. Nova D-58 (aguardando o autor). Progresso: 89% (33/37).

## 2026-11-05 — R2-6.4 e fim da Fase 6

- Doc 06 do reino 2 ganhou a seção 9 (contrato anti-invenção): verificação de proposta, quatro classes (fixo, livre, registro, proibido), lista proibida (presidência do Concílio e ordem do assento, 11ª cidade, 13º povo nativo, ressuscitar heróis lendários, guerra, resolver mistério, número ou duração inventados, camada alterada, relógio sem gatilho, rota ou fala sobre Aeltharyn), casos pedidos e teste rápido. Conferências do §7 atualizadas.
- **Fase 6 do reino 2 concluída.** Suíte local: 0 link quebrado; 0 ID citado sem definição (290 IDs definidos entre registro mestre, relógios, eventos, sementes, mistérios, fatos, canais, reputação e rotas); 0 campo vazio nos CSVs novos (22 `marcador` vazios em fatos públicos e locais, por desenho); 0 marcador de fato restrito ou secreto em resumo `local`; 96 resumos (maior 485 caracteres, vizinhança 143); 6 cenas com `entra` de 935 a 2.033 caracteres, 0 relógio, 0 duração; 0 ID fora do catálogo (`--` = sem sub-raça); 0 arquivo de Aeltharyn, referência, referência técnica ou mundo alterado. Já conhecidos e fora do critério: 269 células vazias opcionais do registro mestre CSV, 43 `relogio_evento` vazios das sementes e 1 `entidades` de `canal.cartaz`. O teste Kotlin de referência não foi executado (sem compilador) e não toca o reino 2.
- D-56 segue aguardando; nova D-57 (aguardando o autor). Progresso: 86% (32/37).

## 2026-11-05 — R2-6.3

- Novos `reinos/02-.../15-cenas-de-teste.md` e `.csv`: 6 cenas (vilarejo de montanha, cidade subterrânea, audiência no Concílio, investigação no Arquivo, viagem por rota, travessia rumo a Torvelume), cada uma com lista "entra" (935 a 2.033 caracteres, limite 2.400) e "não entra". Textos montados só de resumos, fatos, sementes, vozes e rotas já escritos.
- V-73: só fatos `publico` ou `local`, 0 relógio, 0 NPC com `vivo = não`; 0 marcador de fato restrito ou secreto no "entra"; 0 duração de rota; nada afirmado sobre Aeltharyn além do veto do doc 07 §4. Nenhum NPC, facção, rota, número ou fato novo. D-55 segue aguardando; nova D-56 (aguardando o autor). Progresso: 84% (31/37).

## 2026-11-05 — R2-6.2

- Novos `reinos/02-.../14-camadas-de-informacao.md` e `.csv`: 41 fatos (`fact.`) em 4 camadas (12 públicos, 10 locais, 5 restritos, 14 secretos), com estado, quem sabe, evidência, caminho, como descobrir, `marcador` (restritos e secretos) e fonte. Origem: confirmado e suspeita dos 12 principais (24), fatos dos seis mistérios (10) e os 7 relógios. 0 campo vazio, 0 ID duplicado, 0 entidade ou `quem_sabe` fora do registro mestre, 0 restrito ou secreto sem `quem_sabe` (exceto relógios) ou sem `marcador`.
- V-72: 0 `marcador` de fato restrito ou secreto em resumo `local`. `camada_max` de `14-resumos-de-contexto` definitiva; campo *Camadas* de `12-fichas.md` e `.csv` preenchido (12 de 12). Evidência não documentada marcada como lacuna; nenhum NPC, facção, rota, número ou fato novo; nada sobre Aeltharyn. D-54 segue aguardando; nova D-55 (aguardando o autor). Progresso: 81% (30/37).

## 2026-11-04 — R2-6.1

- Novos `reinos/02-.../14-resumos-de-contexto.md` e `.csv`: 96 resumos (10 cidades, 20 assentamentos, 14 facções, 7 relógios, 45 pessoas), 1 por entidade do registro mestre e dos relógios, cada um com ID, no máximo 500 caracteres (maior: 485) e vizinhança de uma linha (maior: 143). 0 campo vazio, 0 ID duplicado.
- Resumos sem suspeita nem desconhecido das fichas, sem duração de rota, número ou total de população; relações só de certeza C; `camada_max` provisória (relógios `secreto`) até o R2-6.2. D-53 aceita pela continuação; nova D-54 (aguardando o autor). Progresso: 78% (29/37).

## 2026-11-04 — R2-5.5 e fim da Fase 5

- Novos `reinos/02-.../13-misterios-limites.md` e `.csv`: MP-01 a MP-06 como dados (`mist.`), com fatos confirmados (rumor e suspeita marcados como pista), 4 hipóteses abertas cada (24), "a verdade não pode ser", evidência cumulativa e ligação a relógios e eventos já escritos. 0 resposta única, 0 campo vazio, nenhum mistério escolhe hipótese, resolve ou toca o Mistério D; termos de fronteira só em vetos.
- **Fase 5 do reino 2 concluída.** Suíte local: 0 link quebrado; 0 ID citado sem definição (registro mestre, relógios, eventos, sementes, reputação, canais, mistérios e rotas do doc 07); 0 campo vazio nos CSVs novos (exceto `relogio_evento` das sementes, vazio = sem ligação escrita, e `entidades` de `canal.cartaz`, lacuna explícita); 0 ID fora do catálogo em `12-competencias.csv`; 0 arquivo de Aeltharyn alterado (só CHANGELOG, README, plano e decisões mudaram fora do reino 2). O registro mestre CSV segue com 269 células vazias de colunas opcionais, preexistentes e fora do critério. O teste Kotlin de referência não foi executado (sem compilador) e não toca o reino 2.
- D-52 aceita pela continuação; nova D-53 (aguardando o autor). Progresso: 76% (28/37).

## 2026-11-04 — R2-5.4

- Novos `reinos/02-.../13-reacao-ao-jogador.md`, `13-reputacao.csv` (24 linhas: 10 cidades e 14 facções) e `13-canais.csv` (11 canais): disposição qualitativa por entidade (ganha e perde), quem reage, canais; estados de procurado; ato × regra × autoridade pelas leis L-01 a L-10; favor; reparação em quatro formas. 0 campo vazio (exceto `entidades` de `canal.cartaz`, lacuna explícita), 0 reator fora do registro mestre ou com `vivo = não`.
- Sem número de fama, preço ou prazo (toda rota segue `desconhecida`); cartaz e caçador de pessoas só como canais, sem facção, ofício ou NPC novos; punição como pena própria fica como lacuna; nada afirmado sobre Aeltharyn. D-51 aceita pela continuação; nova D-52 (aguardando o autor). Progresso: 73% (27/37).

## 2026-11-04 — R2-5.3

- Novos `reinos/02-.../13-sementes-de-aventura.csv` e `.md`: 90 sementes (10 cidades e 20 assentamentos × pequena, média e grande), cada uma com gancho, agentes, abordagens possíveis, "se ninguém age", ligação a relógio ou evento e fonte. Ameaça 1 a 4 (P 1; M 2, duas com 3; G 3, duas com 4). 0 campo vazio, 0 ID de semente duplicado ou colidindo com o reino 1, 0 agente fora do registro mestre ou com `vivo = não`, 0 ligação a relógio ou evento inexistente.
- Nenhuma solução predefinida; nenhuma hipótese de mistério escolhida; nenhum NPC, evento, relógio, número, tempo ou rota novo; nada sobre Aeltharyn nem termos de fronteira. Nova D-51 (aguardando o autor). Progresso: 70% (26/37).

## 2026-11-04 — R2-5.2

- Novos `reinos/02-.../12-eventos.csv` e `12-eventos.md`: 13 eventos-tipo (8 ligados aos relógios CL-01 a CL-07 e 5 sem relógio: C-06, C-07, C-09 e a tensão S-14), cada um com gatilho observável, cadeia causal, cidades e assentamentos, recursos, pessoas e facções, rotas e fonte. 0 efeito em ID inexistente (registro mestre, recursos, relógios e rotas do doc 07), 0 campo vazio.
- Ordem dos elos ASSUMINDO; nenhum fato, relógio ou NPC novo; nada sobre Aeltharyn nem tempo de viagem. Nova D-50 (aguardando o autor). Progresso: 68% (25/37).

## 2026-11-04 — R2-5.1

- Novos `reinos/02-.../12-relogios.csv` e `12-relogios.md`: os 7 relógios CL-01 a CL-07 com o que move, gatilhos observáveis de avanço, o que trava, quem paga, decisão pública no segmento 5 (governo local ou Concílio já documentado), efeito ao completar e efeito da inação, mais a tabela comum de custo por segmento. 0 campo vazio, todas as entidades no registro mestre, nenhum relógio envolve Aeltharyn.
- Nenhum relógio, NPC ou fato novo; progresso inicial de D-37 mantido. Nova D-49 (aguardando o autor). Progresso: 65% (24/37).

## 2026-11-04 — R2-4.4 e fim da Fase 4

- Novos `reinos/02-.../12-voz.csv` e `12-voz.md`: 12 principais com tom, 2 a 3 frases-modelo e o que nunca dizem. As frases são instrução de tom (ASSUMINDO) e não revelam suspeita nem desconhecido; Maur só fala em particular (não fala em público, doc 01).
- **Fase 4 do reino 2 concluída.** Suíte local: 0 link quebrado; 0 ID citado sem registro (89 no registro mestre); 0 campo vazio nos CSVs de assentamentos, competências (45), conhecimento (82), demografia, facções, fichas (12), recursos, relações (91) e voz (12); 0 ID fora do catálogo em `12-competencias.csv`; 0 arquivo de Aeltharyn alterado. O registro mestre CSV segue com 269 células vazias de colunas opcionais, já existentes desde R2-2.1 e fora do critério da fase. O teste Kotlin de referência não foi executado (sem compilador) e não toca o reino 2.
- Nova D-48 (aguardando o autor). Nada novo no mundo. Progresso: 62% (23/37).

## 2026-11-04 — R2-4.3

- Novos `reinos/02-.../12-relacoes.csv`, `12-relacoes.md` e `12-conhecimento.csv`: 91 relações (16 alianças, 12 rivalidades, 20 dívidas, 21 tensões, 10 vínculos de cargo, 12 medos), todas com fonte, e 82 linhas de conhecimento (45 confirmado, 12 suspeita, 25 desconhecido) cobrindo as 45 pessoas. 0 relação sem origem, 0 ID fora do registro mestre.
- **Afeto: 0 relação**, pois nenhum doc a documenta; nada inventado. Medos das fichas ficam como ASSUMINDO. Nova D-47 (aguardando o autor). Nada novo no mundo. Progresso: 59% (22/37).

## 2026-11-04 — R2-4.2

- Novos `reinos/02-.../12-competencias.csv` e `12-competencias.md`: 45 pessoas (12 principais, 20 lideranças de assentamento, 13 heróis lendários) com raça, sub-raça, classe e profissão do catálogo, capacidades e limites tirados só do que os docs 01, 03, 04, 10 e `12-fichas.md` já dizem. 0 ID fora do catálogo, 0 sub-raça com raça-mãe errada, 0 campo vazio, nenhuma capacidade sem limite.
- Classe e profissão seguem como leitura editorial (D-39). Nova D-46 (aguardando o autor). Nada novo no mundo. Progresso: 57% (21/37).

## 2026-11-04 — R2-4.1

- Novos `reinos/02-.../12-fichas.md` e `12-fichas.csv`: os 12 principais (N-01 a N-12) com ficha completa de 17 campos (idade em faixa, povo e sub-raça, origem, cargo, cidade e facção, personalidade, capacidades, relações, biografia, conhecimento C/S/D, estado, agenda, pressão, evolução, camadas). 12 de 12 completos, 0 campo vazio, 12 agendas e 12 pressões distintas, classe e profissão conferidas em `ALINHAMENTO_COROAS.md`. Origem `desconhecida` e estado vivo em todos; Barcaalta fica sem principal (Torgrim é só liderança).
- Traços, medos e evoluções são ASSUMINDO; competências detalhadas (R2-4.2), relações (R2-4.3), voz (R2-4.4) e camadas (R2-6.2) seguem adiadas. Nova D-45 (aguardando o autor). Nada novo no mundo. Progresso: 54% (20/37).

## 2026-11-04 — R2-3.4 e fim da Fase 3

- Doc 01 do reino 2 ganhou a parte 5: 12 luas numeradas, 4 estações de montanha (Gelo Fechado, Degelo, Passo Aberto, Antes-da-Neve), 5 Dias de Balanço, início em Gelo Fechado, Lua 1, dia 1 do ano 516 das Atas (= ano 742 de Aeltharyn), agenda sazonal só como condição (nenhum relógio anda por calendário) e ciclo de guarda de um ano. Tabela de ponte de `mundo/M-02` preenchida; doc 04 §2.2 aponta a duração do ciclo.
- **Fase 3 do reino 2 concluída.** Suíte local: 0 link quebrado; 0 ID citado sem registro (89 no registro mestre); 0 campo vazio nos CSVs de recursos (84), demografia (30), facções (14) e assentamentos (20); 0 arquivo de Aeltharyn alterado (só `M-02` e docs do reino 2, README, CHANGELOG, plano e decisões mudaram); termos de fronteira da parte 5 só como "nada afirmado". O registro mestre CSV tem 269 células vazias de colunas opcionais (`id_catalogo` etc.), já existentes desde R2-2.1 e fora do critério da fase. O teste Kotlin de referência não foi executado (sem compilador) e não toca o reino 2.
- Nova D-44 (aguardando o autor). Nada novo no mundo. Progresso: 51% (19/37).

## 2026-11-04 — R2-3.3

- Novos `reinos/02-.../08-registro-operacional-de-recursos.md`, `09-matriz-material-dos-assentamentos.md` e `12-recursos.csv` (84 linhas, 12 recursos, 29 localidades): escala ordinal 0 a 5 com regra aberta, **28 níveis provisórios de produção** (±1; nenhum 0 nem 5), demanda derivada só de água e alimento (porte relativo de D-42), **reserva `?` em tudo**, 11 dependências críticas com estado e fonte (DR-01 a DR-11) e as dez cidades todas dependentes de ao menos uma (nenhuma isolada). Combustível e alimento: origem **desconhecida**; nada sobre fornecimento a Aeltharyn.
- Doc 05 e `12-registro-mestre.md` apontam para os novos arquivos. Nova D-43 (aguardando o autor). Nada novo no mundo (0 NPC, cidade, facção, assentamento ou rota). Progresso: 49% (18/37).

## 2026-11-04 — R2-3.2

- `05-economia-leis-e-descida.md` §4 (antes "adiada") e novo `12-demografia.csv` (30 linhas): faixas de **porte relativo** (grande, média, pequena) para as 10 cidades (2 grandes, 6 médias, 2 pequenas, todas ASSUMINDO) e faixas por regra da categoria espacial para os 20 assentamentos (bairro urbano = parte da cidade-mãe; posto sazonal = mínima e sazonal). Regras: nada de número, total, média ou percentual; nunca somar; população flutuante e proporção entre povos fora.
- Doc 01: os 10 campos "População" passaram de "a definir" para a faixa. Nova D-42 (aguardando o autor). Nada novo no mundo. Progresso: 46% (17/37).

## 2026-11-04 — R2-3.1

- Novos `reinos/02-.../07-registro-operacional-de-rotas.md` e `07-mapa-esquematico.md` (grafo Mermaid sem escala): 9 ligações entre cidades e fluxos (5 documentadas sem duração, 4 sugeridas por dependência), 20 acessos de assentamentos e 4 linhas de fronteira com Aeltharyn. **Toda duração é `desconhecida`**; 0 rota Coroas–Aeltharyn, 0 passagem nova pela Espinha, 0 tempo inventado.
- Nova D-41 (aguardando o autor): decidir os tempos de viagem. Nada novo no mundo. Progresso: 43% (16/37).

## 2026-11-04 — R2-2.4 e fim da Fase 2

- Novo `reinos/02-.../13-conferencia-cruzada.md`: 0 aliado e rival ao mesmo tempo; 6 pares de rivalidade e 8 de aliança, todos simétricos; cargos de título único sem duplicata; 5 acúmulos de facção e assentamento marcados como intencionais propostos; mapa de quem sabe o quê dos 6 mistérios; 13 contradições intencionais (CI-01 a CI-13).
- **Fase 2 do reino 2 concluída.** Suíte local: 0 link quebrado, 89 IDs no registro e nenhum citado sem registro, 0 ID fora do catálogo no alinhamento (45 pessoas), 0 campo vazio nos CSVs, Aeltharyn intacto (só `M-02` e `M-10` mudaram, com a ponte de anos e os nomes novos). O teste Kotlin de referência não foi executado (sem compilador) e não toca o reino 2.
- Nova D-40 (aguardando o autor). Progresso: 41% (15/37).

## 2026-11-04 — R2-2.3

- Novo `referencia/racas-e-povos/ALINHAMENTO_COROAS.md`: 12 nativos, 13 sub-raças, 8 híbridos e 17 forasteiros conferidos contra o catálogo (todos existem, nomes iguais); raça, sub-raça, classe e profissão do catálogo para as 45 pessoas, com sub-raça só em cidade onde ela aparece (doc 10 §2). `gnomo-dos-vales` fica reservado a Aeltharyn. 0 ID fora do catálogo.
- Ponto aberto `gnomo-das-rochas` mantido (D-36). Nova D-39 (aguardando o autor). Nada novo no mundo. Progresso: 38% (14/37).

## 2026-11-04 — R2-2.2

- Novos `reinos/02-.../12-facoes.csv` (14 facções) e `12-assentamentos.csv` (20 assentamentos) no molde do reino 1. Aliados e rivais convertidos de F-nn para IDs de facção; unidades locais registradas na presença em cidades. 0 campo vazio; `desconhecido` onde o doc 04 não nomeia aliado ou rival; todos os IDs de cidade, assentamento, facção e liderança existem no registro mestre.
- `12-registro-mestre.md` aponta para os dois CSVs. Nada novo no mundo; sem decisão nova. Progresso: 35% (13/37).

## 2026-11-04 — R2-2.1

- Novo `reinos/02-.../12-registro-mestre.csv` e `.md` (formato de D-21): 89 entidades (10 cidades, 14 facções, 20 assentamentos, 45 pessoas: 12 com ficha completa, 13 heróis lendários, 20 só liderança), com coluna extra `povo_id` conferida contra o catálogo. 0 ID duplicado, 0 ID citado nos docs sem registro, 0 cidade-mãe ou liderança inexistente.
- D-37 aceita pela continuação; nova D-38 (aguardando o autor). Nada novo no mundo; nenhum ID de Aeltharyn alterado. Progresso: 32% (12/37).

## 2026-11-03 — R2-1.7

- Doc 01 do reino 2 ganhou a parte 4: 6 mistérios (MP-01 a MP-06: números das comportas, luzes e páginas do Portão, peça e construto de Rodaviva, atas de Pedra-Ata, barca sem remador, corredores de Sinuosa), cada um com 4 hipóteses, vetos e provas; nenhum toca o Mistério D nem a mina selada.
- Novo `06-modelo-operacional-e-aventuras.md`: 7 relógios (CL-01 a CL-07, progresso inicial 1/6 a 3/6, sem avanço automático), 10 conflitos com causa concreta e 10 oportunidades (uma por cidade). Doc 10 ganhou Pimm Veio-Pequeno entre os papéis do Gnomo.
- D-36 aceita pela continuação; nova D-37 (aguardando o autor). Nenhum NPC, cidade, facção ou assentamento novo. Progresso: 30% (11/37).
- **Fase 1 do reino 2 concluída.** Suíte local: 0 link quebrado, 89 IDs definidos e 96 citados sem pendência, 0 ID duplicado, 0 colisão com nomes reservados, 0 arquivo de Aeltharyn alterado, termos de fronteira só em vetos ou como "nada afirmado". O teste Kotlin de referência não foi executado (sem compilador no ambiente) e não toca o reino 2.

## 2026-11-03 — R2-1.6

- Novo `reinos/02-.../10-matriz-povos-nativos-cidades-e-forasteiros.md`: 12 nativos com IDs do catálogo (30 vagas, 3 povos por cidade), 13 sub-raças e 8 híbridos do catálogo por cidade, 17 forasteiros de M-05 com motivos individuais e portas de entrada, papéis por povo e 5 heróis lendários falecidos novos (fecham lacunas de herói de Escamado Miúdo, Nascido da Brasa, Construto, Giganteano e Felino). Lacunas de guardião e benfeitor ficam abertas.
- Achado: o catálogo não tem `gnomo-das-rochas`; tratado como linhagem regional editorial (D-36, opção A). Fenwick Mola-Solta incluído na cobertura por povo. `M-10` ganhou os nomes novos; D-34 e D-35 aceitas pela continuação; nova D-36 (aguardando o autor). Nada sobre Aeltharyn, a mina selada ou o Mistério D. Progresso: 27% (10/37).

## 2026-11-03 — R2-1.5

- Doc 01 do reino 2 ganhou a parte 3: 4 eras locais (Pedras Sozinhas, Pactos de Ofício, Assento, Cópias Divergentes), lendas de origem com duas versões e o que fica desconhecido, cronologia relativa sem datas, **Contagem das Atas** (ano 516 das Atas = ano 742 de Aeltharyn, ASSUMINDO) e defesa por cidade e do reino, com o que não se diz (ameaça externa, Marcas, Aeltharyn).
- Novo `reinos/02-.../05-economia-leis-e-descida.md`: 10 leis (L-01 a L-10, inclui o detalhe do aviso de doc 04 §7), lacuna intencional sobre direitos de Construtos, 11 recursos centrais com estado já aprovado, 8 dependências críticas (origem do combustível desconhecida) e a Descida do Argente a partir de Barcaalta. Demografia adiada para R2-3.2.
- `mundo/M-02` §5 registra o deslocamento; `DECISOES.md` ganhou D-35 (aguardando o autor). Nenhum NPC, cidade, facção ou assentamento novo; nada afirmado sobre Aeltharyn, a mina selada ou o Mistério D. Progresso: 24% (9/37).

## 2026-11-03 — R2-1.4

- Novo `reinos/02-coroas-de-pedra-reino-das-montanhas/04-instituicoes-justica-pactos.md`: Concílio das Coroas (delegações, presidente, arquivista, selo, assento rotativo com três cópias divergentes da ata, voto 6/10 e 7, decisão em 7 passos), competências, Pacto do Assento, justiça e câmara de arbitragem, contribuições de ofício, limite sobre bens compartilhados, sucessão e secessão, governo local das 10 cidades (7 acúmulos intencionais) e **14 facções** (F-01 a F-14) com liderança existente, aliados e rivais (`desconhecido` onde o texto não sustenta), 6 unidades locais, instituições e 5 casos iniciais. Nenhum NPC novo; nova D-34 (aguardando o autor).
- Doc 01 do reino 2: "Governo local" e "Facções presentes" das 10 cidades preenchidos. `mundo/M-10-nomes-minimo.md`: listas de nomes reservados do reino 1 (estavam com o molde sem preencher) e das 10 facções. Conferência: 14 lideranças distintas e existentes, pares aliado/rival simétricos, 0 aliado e rival juntos, 0 ID inexistente, 0 colisão de nome. Progresso: 22% (8/37).

## 2026-10-26 — Aprovação D-33

- Autor aprovou D-33: 12 principais, 8 heróis lendários e tríades das Coroas de Pedra viram cânone do reino 2. R2-1.3 feito (7/37, 19%).

## 2026-10-26 — R2-1.3

- Doc 01 do reino 2 ganhou a parte 2: 12 NPCs principais com agenda, pressão, limite, confirmado, suspeita e desconhecido (2 heróis vivos, 2 generais), 8 heróis lendários falecidos, tríade herói, guardião e benfeitor das 10 cidades e cobertura por povo. D-32 assumida pela continuação; nova D-33. Nomes sem colisão com os reservados do reino 1.

## 2026-10-26 — R2-1.2

- Novo `reinos/02-coroas-de-pedra-reino-das-montanhas/03-assentamentos-e-rede-politica.md`: 20 assentamentos propostos (2 por cidade) com cidade-mãe, categoria espacial, liderança de apoio, obrigação, interesse, tensão e desenvolvimentos; sem população, distância ou rota. D-31 assumida pela continuação; nova D-32. Nomes novos sem colisão com os reservados do reino 1.

## 2026-10-26 — R2-1.1 e Fase 0 do reino 2 concluída

- Novo `reinos/02-coroas-de-pedra-reino-das-montanhas/01-reino-das-coroas-de-pedra.md`: dez cidades propostas (Brasalume, Altavento, Veiaclara, Barcaalta, Portão-Cinza, Sinuosa, Cumieira, Rodaviva, Geodália, Pedra-Ata), cada uma ligada a um marco canônico, com os 12 nativos presentes, lenda em duas versões e sem população, rota ou tempo (campos adiados marcados). D-28, D-29 e D-30 assumidas pela continuação; nova D-31 (aprovar as cidades). F0 do reino 2 fechada (4/37, 11%).
- Conferência: nomes novos sem colisão com os reservados do reino 1; 0 link quebrado.

## 2026-10-26 — R2-0.4

- Novo `plano/CONTRATO_DE_FRONTEIRA_AELTHARYN_COROAS.md`: 14 fixos de Aeltharyn que tocam o reino 2 (Espinha de Vidro, Torvelume, mina selada e Mistério D, rotas, dependência de ferramentas, anões residentes, Sete-Bigornas, calendário), o que o reino 2 pode e não pode afirmar, ganchos permitidos, procedimento de alteração e conferência para o R2-8.1. Nenhum lore alterado.

## 2026-10-26 — R2-0.3d

- Novo `mundo/M-10-nomes-minimo.md`: molde de nomes observado em Aeltharyn, regras de não repetição (nomes de pessoas e lugares reservados) e famílias de epíteto sugeridas para os 12 nativos das Montanhas (W-03). `DECISOES.md` ganhou D-30 (aguardando o autor). Sem nomes novos; nenhum lore de reino alterado.

## 2026-10-26 — R2-0.1

- Novos `mundo/M-01-biblia-do-mundo-minima.md` e `mundo/M-02-calendario-e-conversao.md`: só cânone (Mythralis, Valdoren, cinco reinos, calendário de Aeltharyn) mais duas propostas (W-01 tom do mundo, W-02 ponte de calendário pelo dia do ano). `DECISOES.md` ganhou D-28 e D-29 (aguardando o autor). Sem eras globais, origem ou cosmologia. Nenhum lore de reino alterado.

## 2026-10-26 — Decisões do reino 2

- `DECISOES.md`: D-21, D-22 e D-23 decididas (válidas para os dois reinos); novas D-24 (M-05 aprovado), D-25 (10 cidades, 20 assentamentos), D-26 (Coroas de Pedra; confederação com Concílio das Coroas) e D-27 (pasta). `mundo/M-05` marcado como aprovado; `PLANO_DOS_REINOS.md` e `REFINAMENTO_COROAS_DE_PEDRA.md` atualizados (R2-0.2 feito; R2-0.1 e R2-0.3 parciais; 3%). Nenhum lore de reino alterado.

## 2026-10-26 — Plano do reino 2

- Novo `plano/REFINAMENTO_COROAS_DE_PEDRA.md`: 9 fases, 37 módulos (R2-0.1 a R2-8.2) nos moldes do reino 1, com as lições D-04 a D-23 aplicadas desde o início e os limites de fronteira herdados de Aeltharyn. `PLANO_DOS_REINOS.md` atualizado (reino 2 liberado, plano pronto). Nenhum lore criado.

## 2026-10-26 — Aprovação

- Autor aprovou a Ata de fechamento 2 e liberou o reino 2. D-17 a D-23 permanecem abertas.

## 2026-10-25 — M7.2 e Fase 7 concluída

- `11-fechamento-das-sete-fases.md` ganhou a "Ata de fechamento 2": estado, 9 de 11 portas, decisões em aberto (D-17 a D-23), limites e liberação do reino 2 pendente de aprovação do autor. Nenhum lore alterado.
- F7 (M7.1 e M7.2) fechada; roadmap 100% (30/30). Conferência de fim de fase: CSVs com colunas constantes, 0 link quebrado nos `.md`.

## 2026-10-25 — M7.1

- Novo `plano/RELATORIO_M7_1.md`: 11 portas de `MODELO_DE_REINO.md` §6 conferidas (9 ok; 2 parciais), contradições intencionais (cargos acumulados, resina ilegal, registros contraditórios) separadas de acidentais (nenhuma nova). Achados: 1 general (mínimo 2), 25 principais (intervalo 8 a 12), aliados e rivais ausentes em 8 de 10 facções. `DECISOES.md` ganhou D-22 e D-23 (aguardando o autor). Nenhum lore alterado. Progresso: 97% (29/30).

## 2026-10-25 — M6.4 e Fase 6 concluída

- Novo `plano/PROTOTIPO_MINIMO.md` (especificação): recorte Raiz-de-Prata + Nivara + Elarion em uma linha (contexto B ≈ 1.244 caracteres), condição A sem mundo, roteiro fixo de 8 turnos, 6 execuções, 10 métricas (P1 a P10) e critério de aceite (falha dura em vazamento ou invenção proibida), tudo fixado antes de jogar. Sessões não executadas; resultados na §9. Nenhum lore alterado.
- F6 (M6.1 a M6.4) fechada. Conferência de fim de fase: 19 CSVs do reino com número de colunas constante; 0 link quebrado nos `.md`; 0 ID inexistente no documento novo; `.kt` não alterado. Suíte automática de testes: não existe neste projeto. Pendências herdadas: D-17, D-18, D-19, D-20, D-21. Progresso: 93% (28/30).

## 2026-10-25 — M-05 povos e migrações (rascunho)

- Novo `mundo/M-05-povos-e-migracoes.md`: matriz das 50 raças-base pelos cinco reinos (nativo, aliado, forasteiro), cinco fluxos de migração e registro dos quatro residentes de Aeltharyn. Propostas M5-01 a M5-04 aguardando aprovação. Coluna Florestas inalterada.
- `PLANO_DOS_REINOS.md` e `MATRIZ_POVOS_POR_REINO.md`: M-05 marcado como rascunho; a matriz antiga ganhou aviso de histórico.

## 2026-10-25 — M-04 aprovado

- `mundo/M-04-geografia-mestra.md`: G-01 a G-10 aprovados pelo autor (Valdoren, anel dos cinco reinos, Espinha de Vidro como esporão vulcânico e os nomes de relevo, rios e mares). Próximo: M-05 (povos e migrações).

## 2026-10-25 — M-04 geografia mestra (rascunho)

- Novo `mundo/M-04-geografia-mestra.md`: continente Valdoren e os cinco reinos num anel (Montanhas ao norte, Florestas a nordeste, Humanos a sudeste, Malditos a sudoeste, Feras a oeste), com relevo, rios, mares e fronteiras de Aeltharyn. 10 propostas (G-01 a G-10) aguardando aprovação.
- `PLANO_DOS_REINOS.md` e `README.md`: M-04 marcado como rascunho. Nenhum documento de Aeltharyn alterado.

## 2026-10-25 — Bíblia do reino 1

- `reinos/01-aeltharyn-reino-das-florestas/16-biblia-do-reino.md`: síntese canônica de Aeltharyn (identidade, geografia, povos, história, Coroa, cidades, facções, heróis, crenças, calendário, mistérios) em um só documento.
- 11 propostas de lore novo (P-01 a P-11), todas marcadas `[PROPOSTO]` e **aguardando aprovação**: sete votos dos Juramentos, Três Cinzentas, Primeira Seiva, três práticas mágicas, Dias Sem Raiz, nomes e saudação, mesa, arquitetura, emblemas, provérbios e comemoração dividida.
- Nenhum documento existente foi alterado; o §6 do plano e o README raiz ainda listam como abertos o nome do mundo e Corvídeos × Corvários, já decididos em D-02 e D-01.

## 2026-10-25 — M6.3

- Novo `plano/MAPA_CATALOGO_KOTLIN.md`: conversão campo a campo do `.kt` para o esquema (cidade, povo, figura, rota, relógio), itens descartados (`install`, `Place.secret`, população numérica, `dependsOn` solto), `validate()` e testes mapeados às regras V. `ESQUEMA_DE_DADOS.md` ganhou `povo_no_reino.txt`; `VALIDADOR_DE_REINO.md` ganhou a regra opcional V-A1 (tríade). `.kt` não alterado. Nenhum lore alterado. Progresso: 90% (27/30).

## 2026-10-25 — M6.2

- Novo `plano/VALIDADOR_DE_REINO.md` (especificação): regras V-01 a V-99 com severidade (erro ou aviso), exceções registradas, cobertura dos 12 critérios do autor e previsão não executada para Aeltharyn. `ESQUEMA_DE_DADOS.md` ganhou `papeis` e `principal` (NPC), `povos` (cidade), `marcador` (fato) e `ids_aposentados.txt`. Nenhum lore alterado. Progresso: 87% (26/30).

## 2026-10-25 — M6.1

- Novo `plano/ESQUEMA_DE_DADOS.md`: regras comuns (arquivo por tipo, delimitador, `?` e `—`, IDs únicos no mundo, enums de certeza e camada), campos das 8 entidades principais, 10 arquivos de apoio mapeados aos CSVs de Aeltharyn, molde estático e deltas. Formato `|` recomendado (ASSUMINDO); D-21 aberta. Nenhum lore alterado. Progresso: 83% (25/30).

## 2026-10-25 — M5.4 e Fase 5 concluída

- Doc 06, nova seção 11.1 (contrato anti-invenção): quatro classes (fixo, livre, exige registro, proibido), lista de proibidos, regras para novo rei, nova cidade, guerra, NPC novo e morte de NPC, teste de 4 perguntas; critério novo na seção 13. Classificação é leitura editorial (ASSUMINDO). Nenhum lore alterado.
- F5 (M5.1 a M5.4) fechada. Conferência de fim de fase: 19 CSVs do reino com número de colunas constante; 0 link quebrado nos `.md`; 0 ID inexistente nos 7 arquivos de F4/F5 conferidos; 142 resumos (máx. 491 caracteres; vizinhança máx. 186); 5 cenas entre 872 e 1.913 caracteres (limite 2.400); nenhum fato restrito ou secreto em resumo `local`; nenhum NPC morto em fatos ou cenas; arquivos antigos intactos exceto README (raiz e reino), doc 02 (ponteiro), doc 06, `MODELO_DE_REINO.md`, `REFINAMENTO_AELTHARYN.md`, resumos (coluna `camada_max`) e fichas de Elarion (campo *Camadas*). Suíte automática de testes: não existe neste projeto (só documentos e CSVs; o `.kt` não compila aqui). Pendências herdadas: D-17, D-18, D-19, D-20. Progresso: 80% (24/30).

## 2026-10-25 — M5.3

- Novos `15-cenas-de-teste.md` e `.csv`: 5 cenas (vilarejo Raiz-de-Prata, chegada a Thalanor, audiência na corte, investigação nos arquivos, viagem Elarion–Nymvar). "Entra" montado só de resumos, fatos de camada público/local, sementes e linhas de rota já escritos (872 a 1.913 caracteres; limite 2.400); "não entra" lista fatos restritos e secretos, relógios, eventos não disparados e hipóteses, com a condição para entrar. Prefixo `cena.` em `MODELO_DE_REINO.md`. Nenhum lore alterado. Progresso: 77% (23/30).

## 2026-10-25 — M5.2

- Novos `14-camadas-de-informacao.md` e `.csv`: 66 fatos (6 públicos, 34 locais, 14 restritos, 12 secretos; prefixo `fact.`) com titular, evidência, caminho de descoberta (10 códigos) e como descobrir. Critério de camada é leitura editorial (ASSUMINDO) a partir do doc 04 §10 e do doc 03. `14-resumos-de-contexto.csv` ganhou `camada_max` (local; relógios = secreto, uso de Mestre/motor); conferência por frase-marcador: nenhum fato restrito ou secreto em resumo `local`. Campo *Camadas* nas fichas de Elarion (`.md` e `.csv`); ponteiro no doc 02 §4 e no README do reino; prefixo `fact.` em `MODELO_DE_REINO.md`. Nenhum lore alterado. Progresso: 73% (22/30).

## 2026-10-25 — M5.1

- Novos `14-resumos-de-contexto.md` e `.csv`: 142 resumos (10 cidades, 20 assentamentos, 10 facções, 7 relógios, 95 pessoas), cada um com ID, até 500 caracteres e uma linha de vizinhança (rotas com duração só onde documentada, cidade-mãe, presença, relações escritas). Só o que o texto escreve; sem uso de conhecimento restrito, de "nunca diz" nem de relação secreta; população só em faixa. Nenhum lore alterado. Progresso: 70% (21/30).

## 2026-10-25 — M4.5 e Fase 4 concluída

- Novos `13-misterios-limites.md` e `.csv`: 8 mistérios (A a E, H, I, J) com fatos confirmados, hipóteses abertas (2 ou mais), o que a verdade não pode ser e evidência para resolver. Ponteiro no doc 01 §13 e três entradas no README do reino. Prefixo `mist.` em `MODELO_DE_REINO.md`. Nenhum lore alterado.
- F4 (M4.1 a M4.5) fechada. Conferência de fim de fase: 6 CSVs novos bem formados; 0 link quebrado; 0 ID inexistente nos arquivos novos (95 pessoas, 10 cidades, 10 facções, 20 assentamentos; 90 sementes conferidas contra o md); 0 NPC morto usado; arquivos antigos intactos exceto README, doc 01 (ponteiro), `MODELO_DE_REINO.md`, `REFINAMENTO_AELTHARYN.md` e `CHANGELOG.md`; calendário 12×30+5 = 365. Pendências herdadas: D-17, D-18, D-19, D-20. Progresso: 67% (20/30).

## 2026-10-25 — M4.4

- Novos `13-reacao-ao-jogador.md`, `13-reputacao.csv` (20 entidades: o que ganha e perde disposição, quem reage, canais) e `13-canais.csv` (10 intermediários). Estados de procurado, favor e reparação baseados no doc 04 §4 e nas leis do doc 01 §10; números de fama ficam com o motor (MP-07). Cartaz e caçador de pessoas não existem no cânone: entram só como canais, sem facção nem NPC novos. Prefixos `rep.` e `canal.` em `MODELO_DE_REINO.md`. Nenhum lore alterado. Progresso: 63% (19/30).

## 2026-10-25 — M4.3

- Novos `13-sementes-de-aventura.md` e `.csv`: 90 sementes (10 cidades e 20 assentamentos, uma pequena, uma média e uma grande cada), com gancho, agentes, ameaça (1–4, ASSUMINDO), abordagens possíveis sem solução predefinida e "se ninguém age". Só IDs existentes; nenhum NPC morto usado; ligações a relógios e eventos só onde escritas nas fontes. Prefixo `seed.` em `MODELO_DE_REINO.md`. Nenhum lore alterado. Progresso: 60% (18/30).

## 2026-10-25 — M4.2

- Novos `12-eventos.md` e `12-eventos.csv`: 13 eventos-tipo (`event.*`; 4 cadeias do doc 01 §25.3, 4 efeitos de relógio e 5 combinando dependências escritas) com gatilho, cadeia e efeitos em IDs existentes de cidades, assentamentos, recursos, pessoas, facções e rotas. Encadeamentos não escritos marcados ASSUMINDO; cidades do pacto suspenso a definir. Prefixo `event.` em `MODELO_DE_REINO.md`. Nenhum lore alterado. Progresso: 57% (17/30).

## 2026-10-25 — M4.1

- Novos `12-relogios.md` e `12-relogios.csv`: 7 relógios com o que move (doc 01 §26), o que trava, quem paga, ao completar e efeito da inação; tabela comum de custo por faixa de segmento. Trava, custo e inação são leitura editorial (ASSUMINDO), derivada do texto; todos os IDs citados existem. Nenhum lore alterado. Progresso: 53% (16/30).

## 2026-10-25 — M3.4 e Fase 3 concluída

- Novo `12-voz.csv` (25 NPCs principais: tom, 2 frases-modelo, nunca diz) e seção 14 do registro mestre (v9). Frases são propostas de tom (ASSUMINDO), baseadas só em personalidade e limites escritos. Nenhum lore alterado.
- F3 (M3.1 a M3.4) fechada. Pendências: D-18, D-19, D-20; Arven sem benfeitora; fichas completas só de Elarion. Conferência de fim de fase abaixo. Progresso: 50% (15/30).

## 2026-10-25 — M3.3

- Novos `12-relacoes.csv` (76 relações com origem e fonte) e `12-conhecimento.csv` (59 linhas: confirmado, suspeita, desconhecido; 33 pessoas) e seção 13 do registro mestre (v8). Relações só quando o texto as escreve; nenhuma de "medo". 1 ASSUMINDO (Maereth de Saelith = princesa). Nenhum lore alterado. Progresso: 47% (14/30).

## 2026-10-25 — M3.2

- Novo `12-competencias.csv` e seção 12 do registro mestre (v7): raça, sub-raça, classe, profissão, capacidades e limites das 95 pessoas. Todos os IDs existem no `personagem-catalogo.json`. Classe (32) e profissão (42) são propostas só com base no texto; 18 pessoas sem povo escrito ficam `—`. Nenhum lore alterado.
- D-20 aberta (povo de Yssara e Hadrik: registro × texto). Progresso: 43% (13/30).

## 2026-10-25 — M3.1

- Novos `12-fichas-elarion.md` e `.csv`: ficha de `city.elarion`, dos 2 assentamentos e das 10 pessoas com cidade = Elarion (8 vivas, 2 mortas), todos os campos do `MODELO_DE_REINO.md`. Agenda e pressão com origem marcada ([doc], [.kt], ASSUMINDO); lacunas ficam *desconhecido*. Nenhum lore alterado.
- D-18 (Edran Sem-Selo sem ID) e D-19 (cidade do rei e da princesa) abertas. Arven segue sem benfeitora (M3.1 não cobriu Arven). Progresso: 40% (12/30).

## 2026-10-25 — Fase 2 concluída

- F2 (M2.1 a M2.4) fechada. Conferência de fim de fase: IDs, links, CSVs, calendário (365 dias) e faixas sem divergência. Pendência: nomes das luas (D-17).

## 2026-10-25 — M2.4

- Doc 01, nova seção 35: ano 742 abre em Silêncio (Lua 1, dia 1); 4 estações de 3 luas; Dias Sem Raiz nos dias 91, 182, 273, 364 e 365; agenda sazonal x 7 relógios com fato e hipótese separados. Luas numeradas (`lua-01..12`, `dsr-1..5`); nomes propostos aguardam aprovação (D-17). `PLANO_DOS_REINOS` M-02 parcial. D-14 a D-16 decididas. Progresso: 37% (11/30).

## 2026-10-25 — D-13

- Doc 08 seção 12 e `12-recursos.csv`: produção com estimativa provisória (±1) para 7 produtores (Nymvar 5 e Arven 5 em grãos, Campo das Três Famílias 3, Vael-Tir 3 e Copa-Funda 2 em madeira, Copa-Funda 2 em resina, Torvelume 1 em minério). Reserva segue `?`.

## 2026-10-25 — M2.3

- Doc 08, seção 12: escala ordinal 0–5 (nível de função, não quantidade; `?` = desconhecido), papéis e dependências de grãos, água, madeira, resina e minério, cada um com estado de evidência e fonte. Demanda derivada da faixa de população só para grãos e água nas 10 cidades. Produção e reserva ficam `?` (os docs não dão magnitude). Novo `12-recursos.csv`. Nenhum lore alterado. Progresso: 33% (10/30).

## 2026-10-25 — M2.2

- Doc 05: tabelas de população trocadas por faixas de uso (cidades: 4 faixas; assentamentos: 4 faixas), valor do perfil mantido só como referência; nova regra de não somar (seção 1.2.0); somas 293.500 e 37.250 marcadas como conferência, não população. Categoria demográfica de todos: indefinida.
- Registro mestre v6 (seção 11) e novo `12-demografia.csv`; refs em docs 01 e 03. Nenhum lore alterado. Progresso: 30% (9/30).

## 2026-10-25 — D-12

- Nymvar–Arven: 2–4 dias por caravana; 1 dia só viajante leve. Aplicado no doc 01 (seção 6), doc 07 e mapa esquemático.

## 2026-10-25 — M2.1

- Doc 07: acessos locais dos 20 assentamentos (seção 3.1, duração desconhecida em todos), `route.arven-silvaran` com 2–4 dias (Rota dos Celeiros), `route.nymvar-thalanor` resolvida, `route.orvass-brumafunda` mantida **desconhecida**; seção 3.2 compara com as 14 rotas do `.kt` (não canônico).
- Novo `07-mapa-esquematico.md` (grafo e posição relativa só com base no texto). D-12 aberta (Nymvar–Arven). Nenhum tempo inventado. Progresso: 27% (8/30).

## 2026-10-25 — M1.5 fechado e Fase 1 concluída

- D-09 (A): 13 organizações locais viram unidades locais das 10 facções (registro mestre v5, seção 10; 9 com facção-mãe a definir). D-10 (A): Nymvar lista a Irmandade das Águas (doc 01). D-11 (A): cargos acumulados intencionais (adendo no doc 11).
- Presença de Vigias e Irmandade das Águas atualizada (seção 8 e `12-facoes.csv`).
- Conferência de fim de fase: ver relatório de testes na entrega. Progresso: 23% (7/30).

## 2026-10-25 — M1.5 (relatório; aguardando autor)

- `plano/RELATORIO_M1_5.md`: 0 aliado/rival simultâneo, 0 divergência de idade ou ID, 0 conhecimento sem fonte nos assentamentos. 3 pendências em `DECISOES.md` (D-09 organizações sem ID, D-10 Irmandade das Águas × Barqueiros, D-11 cargos acumulados). Nenhum lore alterado; progresso segue 20%.

## 2026-10-25 — M1.3

- `12-registro-mestre.md` v4: seção 8 (10 facções: liderança com ID, recursos, métodos, tensão, aliados, rivais, presença em cidades e assentamentos) e seção 9 (20 assentamentos: cidade-mãe, liderança, obrigação, interesse, tensão). Novos `12-facoes.csv` e `12-assentamentos.csv`.
- Vínculos conferidos: 10 + 20 IDs de liderança e 20 cidades-mãe existem no registro. Nenhum campo vazio; aliados e rivais ausentes no doc 01 em 8 facções ficam como *não informado* (lacuna explícita, nada inventado).
- Nenhum lore alterado. Progresso: 20% (6/30 módulos).

## 2026-10-25 — D-08

- `npc.vaunor-eir` registrado (doc 01, seção 4.3; registro mestre e CSV). Pessoas: **95**; só nos docs: **44**.

## 2026-10-25 — M1.2 e projeto renomeado para Mythralis

- Projeto/pasta/zip agora se chamam **Mythralis** (nome do mundo, D-02); README e referências ajustados.
- `12-registro-mestre.md`: seção 7 com a ficha mínima das 43 pessoas só dos documentos (cidade, cargo, povo e idade como no texto, `—` onde o texto não diz).
- Achado: **Vaunor Eir** (Conselho das Sete Copas) não tem ID; proposta D-08 em `DECISOES.md`, aguardando o autor.

## 2026-10-25 — M1.4 padronização (decisões D-01 a D-07 aplicadas)

- Docs 01, 10, 11: "Corvídeo(s)" para **Corvário(s)** como povo (fauna em aves mantida); "sátiro" para **Silvano Festivo**; "tabaxi" para **Felídeo Errante**; "tritões" para **Abissais Costeiros**; apelidos só como sinônimo.
- Doc 01: **32 IDs do catálogo escritos** (20 em parágrafos, 12 na tabela de tríades). Todos os 51 IDs do catálogo existem nos docs.
- Mundo = **Mythralis**: README do reino, `PLANO_DOS_REINOS.md` e `MUNDO_PRECONFIGURADO.md` ajustados; Aeltharyn segue como nome do reino.
- `referencia-tecnica/AeltharynWorld.kt`: 7 IDs renomeados para os dos documentos; Vessa Fio-Dourado movida para `city.silvaran` (Arven fica sem benfeitora no catálogo até o M3.1; o `.kt` não compila aqui).
- `12-registro-mestre` v3 e `ALINHAMENTO_AELTHARYN.md` (tabela de sinônimos).
- Nada de lore novo; o projeto foi renomeado para Mythralis logo depois.

## 2026-10-25 — M0.1 e M0.2 fechados

- `DECISOES.md`: D-01 Corvários; D-02 mundo com nome próprio, **Mythralis**; D-03 nomes do catálogo; D-04 ID dos documentos (7 pessoas); D-05 ID do catálogo escrito nos docs (32 pessoas); D-06 inventário aprovado (94 pessoas como piso); D-07 Vessa Fio-Dourado em Silvaran.
- `REFINAMENTO_AELTHARYN.md`: F0 concluída. Próximo: M1.4 aplicar as decisões nos docs.
- Nenhum conteúdo de lore alterado ainda.

## 2026-10-25 — M1.1 v2 (correção do registro mestre)

- `12-registro-mestre.md/.csv` v2: 5 pares de ID escritos de forma diferente nos docs e no catálogo estavam contados como 10 pessoas; são 5. Conflitos de ID: 2 para **7**; só nos docs: 48 para **43**; só no catálogo: 37 para **32**; pessoas distintas: 99 para **94** (12 + 7 + 43 + 32).
- Nomes quebrados limpos (Parel Três-Parafusos, Tirven Eixo-Claro, prefixos de cidade); cidade dos 19 líderes de assentamento sem entrada no catálogo preenchida pela cidade-mãe; coluna `ficha_doc` com povo e idade como escritos nos docs (22 de 43).
- `DECISOES.md`: D-04 com 7 pares, D-05 e D-06 com os números novos, **D-07 nova** (Vessa Fio-Dourado: Arven no catálogo × Silvaran nos docs).
- `REFINAMENTO_AELTHARYN.md`: baseline e andamento atualizados. Nenhum conteúdo de lore alterado.

## 2026-10-25 — Fase 0 e M1.1

- `reinos/01-.../12-registro-mestre.md` e `.csv`: registro mestre de 10 cidades, 10 facções, 20 assentamentos e **99 pessoas**, com a situação de cada ID (12 nos dois, 2 conflitos, 48 só nos docs, 37 só no catálogo). Integridade conferida: os 20 líderes de assentamento existem no registro.
- `plano/DECISOES.md`: D-01 a D-06 com opções, impacto medido e recomendação (aguardando o autor).
- `plano/REFINAMENTO_AELTHARYN.md`: baseline corrigida e tabela de andamento.
- Nenhum conteúdo de lore foi alterado.

## 2026-10-25 — plano de refinamento

- Criado `plano/REFINAMENTO_AELTHARYN.md`: 8 fases (F0 a F7), módulos e submódulos, com linha de base medida (docs × catálogo técnico: 12 IDs de NPC em comum, 50 só nos docs, 0 facções e 0 assentamentos no código).

## 2026-10-25 — projeto separado

- Criado o projeto **Aeltharyn-Mundo**, separado do RPG Solo, a partir da branch `world/aeltharyn-reino-das-florestas`.
- Movidos para `reinos/01-aeltharyn-reino-das-florestas/` os 11 documentos do Reino das Florestas e o README; ajustadas as linhas que citavam a branch e "nenhum código".
- Movidos para `referencia-tecnica/` o `AeltharynWorld.kt` e seu teste (eram código do jogo).
- Criados `plano/PLANO_DOS_REINOS.md`, `plano/MODELO_DE_REINO.md` e `plano/INTEGRACAO_FUTURA_RPG_SOLO.md`; `MUNDO_PRECONFIGURADO.md` e a proposta de NPCs recorrentes vieram do autor.
- Gerados a partir do catálogo de personagem: `racas.md`, `subracas.md`, `hibridos.md`, `classes.md`, `profissoes.md`; copiados o plano de raças, o catálogo de estilos e os arquivos de itens, companheiros e perigos.
- Criados `ALINHAMENTO_AELTHARYN.md` (16 IDs conferidos; ambiguidade Corvídeos × Corvários) e `MATRIZ_POVOS_POR_REINO.md` (só Florestas preenchida).
- Nenhum conteúdo de lore dos reinos 2 a 5 foi escrito.
