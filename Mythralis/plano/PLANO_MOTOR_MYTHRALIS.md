# Plano do motor de Mythralis — o mundo vivo e o simulador de eras

> *"O mundo não existe para contar a sua história. Sua história acontece dentro dele."*

**Estado (07/11/2026):** plano aguardando aprovação das decisões D-M. **F1 a F4 já feitas** (conversão de Aeltharyn; carregador/validador dos 5 reinos, 0 erro; estado, crônica, replay, hash, importância, notificações, passo de 10 anos e `saude.txt`; dinâmica v0 e os motores de demografia, sucessão, relógios, eventos e política, 28 testes passando); **F5 feita** (F5.1 economia e rotas, F5.2 Ecos, F5.3 mistérios, F5.4 fronteira; 37 testes passando); **F7.1 (tensão e incidentes de fronteira), F7.2 (tratados, rupturas e quebras de contrato) e F7.3 (guerras, quedas de coroa e regime aberto) feitas; F7.4 pendente. F6 feita** (F6.1 relatório e métricas, F6.2 invariantes e consultas, F6.3 modo lote, F6.4 retomada e CLI; 37 testes passando); F7 em diante não começou.**
**Nome:** o projeto e o mundo, antes chamados Orravel, passam a se chamar **Mythralis** (o continente continua **Valdoren**, M-04 G-01; o ID do mundo passa a `mundo.mythralis`).
**Escopo:** Mythralis é independente do RPG Solo. Primeiro funciona sozinho; a integração com o app é pensada depois (seção 17).

## 0. Identidade do sistema

| Nome | O que é |
|---|---|
| **Mythralis** | o mundo vivo: molde canônico + motor que o faz andar |
| **Crônicas de Mythralis** | o registro da história mundial: tudo que o motor fez acontecer, em ordem, com causa |
| **Eras de Mythralis** | os períodos históricos: regimes de simulação e capítulos que a própria crônica nomeia |
| **Ecos de Mythralis** | acontecimentos e rumores: o que cada lugar e cada pessoa sabe, e quando fica sabendo |

## 1. Objetivo

Criar um **motor de mundo vivo** que carrega os **cinco reinos de uma vez, com um esquema só**, e os faz evoluir por anos: pessoas envelhecem, morrem e são sucedidas; facções se aliam e rompem; recursos e rotas mudam; relógios avançam e estouram em eventos; tramas, guerras e quebras de fronteira acontecem. Junto, um **simulador de eras** que roda esse mundo por décadas ou séculos, **sem jogador**, e testa se há **continuidade**: o mundo de 200 anos depois é consequência legível do de hoje e não contradiz o que está escrito.

O que **não** é: um jogo, um gerador de aventuras, nem um substituto do cânone. Não escreve prosa longa e não decide lore sozinho.

## 2. Princípios

1. **Molde imutável.** Os arquivos `dados/*.txt` dos reinos nunca são editados pelo motor. Isso é conferido por hash antes e depois de cada simulação.
2. **Um esquema, cinco reinos.** `esquema=1` vale para todos. Aeltharyn, que ainda não tem `dados/`, é convertido para o mesmo formato (F1).
3. **Cânone e simulado nunca se misturam em silêncio.** Todo fato novo nasce marcado `origem=SIMULADO`. Só vira cânone com aprovação do autor. Isso segue o que já existe (`ORIGINAL`, `DIVERGENCE`, `AI_CREATED` em `INTEGRACAO_FUTURA_RPG_SOLO.md`).
4. **O cânone é qualitativo; a simulação precisa de número.** O Mythralis escreve "grande", "madura", "rival", e não "82.000" ou "62 anos" (D-63). Então os números ficam numa **camada de dinâmica separada**, marcada `PROPOSTO`, trocável, e que nunca volta para o molde (seção 9).
5. **Determinismo.** Mesma semente + mesmo molde + mesmos parâmetros = mesma crônica, bit a bit. Sem isso não há como testar continuidade nem depurar.
6. **Nada acontece sem causa.** Todo evento da crônica registra o que o causou (relógio, tensão, morte, escolha de agente). Evento sem causa é falha de teste.
7. **Lacuna de evidência continua lacuna.** O motor não preenche `?` com valor inventado e não resolve mistério (`a_verdade_nao_pode_ser` vale).
8. **Aberto por era.** Na primeira era os contratos de fronteira valem como regra do motor; nas seguintes o mundo fica livre (seção 8).

## 3. O que já existe (auditoria de 07/11/2026)

- **Esquema único:** os `dados/` dos reinos 2 a 5 têm os mesmos 21 tipos de arquivo com **os mesmos cabeçalhos**. (Aeltharyn foi convertido na F1.) Antes dela, só faltava Aeltharyn, que tinha 19 CSVs, `AeltharynWorld.kt` (referência) e os docs 01 a 16, sem `dados/`.
- **Já há ferramentas:** `converter_reino2..5.py` (CSV e markdown → `dados/*.txt`) e `validar_reino.py` (289 linhas, regras V-xx).
- **Escala:** 5 reinos, 46 lugares-cidade (10, 10, 8, 8 e 10), 20 assentamentos de Aeltharyn e 20 das Coroas, cerca de 233 NPCs (95 em Aeltharyn e 138 nos outros quatro), 64 facções, 40 relógios (7 em Aeltharyn e 33 nos outros), centenas de relações tipadas.
- **Catálogo de personagem:** 50 raças, 50 sub-raças, 50 classes, 104 profissões; IDs já usados em todos os NPCs.
- **Pendências de cânone que o motor precisa:** M-06 (facções transnacionais), M-07 (rotas e economia entre reinos), M-08 (religião, só se preciso), M-09 (linha do tempo e conflitos globais), M-11 (figuras de nível mundial), M-12 (memória do mundo). Todos ainda `☐`.

**Lacunas dos dados que mudam o desenho do motor:**

| Lacuna | Número | Consequência |
|---|---|---|
| Idade dos NPCs como número | 90 de 138 são `?`; o resto é faixa ("madura", "idosa") | derivar idade por faixa + longevidade do povo (9.2) |
| Rotas dos reinos 2 a 5 | **todas 38** `desconhecida`, duração `?` | em Aeltharyn há ~12 rotas com dias documentados; nos outros, a simulação usa **passos** de um grafo, não dias |
| Rotas **entre** reinos | **nenhuma** | M-07 precisa criar o grafo mundial; até lá, ligações só pelos fluxos de M-05 §5 e pelos contratos |
| População | só faixas (pequena, média, grande) | demografia qualitativa; nenhum número vai para o cânone |
| Calendários | 5 contagens locais, ano comum de 365 dias, **mesmo dia do ano em todo o mundo** (M-02) | o motor corre no **dia do mundo**; o ano local é só rótulo |

## 4. Arquitetura

```
                    ┌────────────────────────────────────────────┐
  MOLDE (somente    │ reinos/*/dados/*.txt  +  mundo/*.md        │  canônico, hash conferido
  leitura)          └───────────────┬────────────────────────────┘
                                    │ carregador único (World)
                    ┌───────────────▼────────────────────────────┐
  DINÂMICA          │ dinamica/*.txt  — parâmetros PROPOSTOS      │  números de simulação,
  (não canônica)    │ longevidade, taxas, regras de relógio, ...  │  trocáveis, versionados
                    └───────────────┬────────────────────────────┘
                                    │
                    ┌───────────────▼────────────────────────────┐
  ESTADO            │ estado vivo: vivos/mortos, cargos, relógios,│  só existe na memória e em
  (mutável)         │ relações, rotas, recursos, quem sabe o quê  │  snapshots; refeito pela crônica
                    └───────────────┬────────────────────────────┘
                                    │ motores (seção 7)
                    ┌───────────────▼────────────────────────────┐
  CRÔNICAS          │ crônica.jsonl (append-only): deltas com     │  fonte da verdade do que mudou;
  + ECOS            │ causa, agentes, quem_sabe, certeza, origem  │  replay reconstrói o estado
                    └────────────────────────────────────────────┘
```

**Regra central:** `estado = molde + dinâmica + replay(crônica)`. O estado nunca é salvo como verdade; quem manda é a crônica. Isso dá replay, auditoria, "o que aconteceu com X?" e, mais tarde, a ponte com o RPG Solo (campanha = outra crônica sobre o mesmo molde).

**Formato do delta da crônica** (herda os campos de `ESQUEMA_DE_DADOS.md` §5): `id`, `dia_mundo`, `era`, `tipo`, `entidade`, `estado_anterior`, `estado_novo`, `causa` (ids + regra), `agentes`, `quem_sabe`, `certeza`, `origem`.

**Linguagem e dependências:** Python 3, **só biblioteca padrão** (roda aqui, sem instalar nada). Código em `motor/`, testes em `motor/testes/`. O desenho evita truques de Python para o porte futuro (seção 17).

### Como ficou a F3 (07/11/2026)
`motor/estado.py` (estado e aplicação de deltas: `campo`, `criacao`, `era`; outros tipos são só registro), `cronica.py` (jsonl com cadeia de hash; abrir confere a cadeia), `importancia.py` (regras-base, limiar 3), `invariantes.py`, `simulador.py` (laço por dia; bloco de `--passo` anos; `--retomar`) e `motores/__init__.py` (lista **vazia** até a F4). A rodada grava em `saidas/<rodada>/`: `cronica.jsonl`, `notificacoes.jsonl`, `saude.txt`, `invariantes.txt`, `progresso.json` (último bloco conferido, para retomar) e `snapshots/`. Ficam **pendentes** (aparecem como `pendente` em `invariantes.txt`): I-04, I-08 a I-12, I-15 a I-18 e I-20; `--ate-gatilho` e `--interativo` chegam com os motores de fronteira (F5/F7). Testes: `python3 -m unittest discover -s motor/testes -v`.

### Como ficou a F4 (07/11/2026)
**Dinâmica** (`dinamica/*.txt`, gerada por `plano/ferramentas/gerar_dinamica.py`, lida por `motor/dinamica.py`; tudo `PROPOSTO`): `classes_longevidade.txt` (efêmera, curta, humana, longa, milenar, sem idade), `longevidade.txt` (50 raças + 50 sub-raças), `faixas_etarias.txt`, `limiares.txt` (23 chaves) e `relogios_dinamica.txt` (40 relógios, `revisado=não`). Arquivo ausente = dinâmica vazia = motores calados.
**Estado novo** (derivado do molde, sem editá-lo): idade do NPC vivo (`nasc_dia`, número do texto ou derivada da faixa pelo id), `saude`, **postos** (`posto.<id>`: 110 = 41 cidades, 64 facções, 5 NPCs governante/general; só entram titulares que são NPC vivo), **pares de facções** (`par.<a>+<b>`: 64 pares dos campos `aliados`/`rivais`, tensão 0 a 100) e **eventos** (`event.*`: estado, passo, `inicio_dia`).
**Motores** (`motor/motores/`): `demografia` (anual: doença e morte natural pela classe e idade), `sucessao` (anual: herdeiro → vice por vínculo de cargo → candidato da facção ou cidade → vacância com prazo de 3 anos e interino → NPC simulado, nome pelo padrão de M-10 em `nomes.py`), `relogios` (lunar: pressões, travas, recuo e estouro), `eventos` (lunar: estouro → cadeia em passos, 2 luas entre passos; ao fim entram as consequências `se_ninguem_age` das sementes), `politica` (anual: tensão por par, ruptura, disputa aberta, trégua e tratado, com custo). Só dentro dos reinos; nada cruza fronteira.
**Invariantes**: entram I-04 (cargo ocupado ou vacância válida; só vale com o motor de sucessão na rodada), I-11 (par de facções só muda por transição permitida) e I-15 (alerta: NPC simulado sem cidade). `ALERTA` passa a existir em `saude.txt`. Pendentes: I-08 a I-10, I-12, I-16 a I-18 e I-20.
**Resultado de 100 anos** (sementes 1 a 3): 10 blocos `OK`, cerca de 9.400 a 9.700 deltas, 20 a 30 dos 40 relógios estouram, 23 a 36 eventos ocorrem, 142 a 159 NPCs simulados (a maioria de postos sem candidato). **Limites**: eventos com gatilho só em prosa (15) ficam latentes; relógio estourado não reinicia; sem morte violenta (F5/F7); calibragem fina é da F8.

### Como ficou a F5.1 (07/11/2026)
`motor/motores/economia.py` (sazonal, só dentro dos reinos) e `dinamica/economia.txt` (13 chaves `PROPOSTO`, gerado por `gerar_dinamica.py`, lido em `Dinamica.econ`). **Estado novo** (derivado do molde): `route.*` (38+ rotas: `estado` aberta ou bloqueada, `evidencia` = estado do molde) e `econ.<recurso>@<lugar>` (só cidades e assentamentos; `producao`, `producao_atual`, `demanda` de 0 a 5, `escassez` de 0 a 3, `estacoes_criticas`; `?` vira o padrão da dinâmica, ASSUMINDO). **Regras:** rota aberta bloqueia por estação (mais no inverno; rota que não é `documentada` bloqueia mais) e reabre; produtor perde 1 de produção por quebra de safra e recupera; escassez = demanda − (produção local + oferta recebida por rotas abertas, até 3 passos, teto 2); escassez máxima por 4 estações seguidas vira `revolta` e zera o contador. Registros: `bloqueio_de_rota` e `revolta` (importância 3, notificam), `rota_reaberta` (2); `escassez` 3 = importância 2. Versão do motor F5.1. **Limites:** a escassez ainda não pressiona relógios nem tensão de facções (entra com a fronteira); sem preço, moeda ou comércio entre reinos (M-07); sem demanda de lugares fora de cidade e assentamento (recursos de reino ou facção ficam de fora); calibragem fina é da F8. Sem testes novos (só ao fim da F5).

### Como ficou a F5.2 (07/11/2026)
`motor/motores/ecos.py` (diária) e `dinamica/ecos.txt` (7 chaves `PROPOSTO`, em `Dinamica.ecos`). Todo delta de importância 3 ou mais nasce como entidade `eco.<delta>` dentro de `Estado.aplicar` (derivado da crônica, então o replay e a retomada o refazem; `ecos_ativos` não é serializado). Campos: `origem_delta`, `tipo`, `entidade`, `camada` (`restrito` para pista de mistério; `publico` para importância 4 e tipos de alcance geral; `local` nos demais), `inicio` e `difusao` (`passo`, `lugares`, `certeza`, `ativo`). Lugar de origem: o do próprio delta (cidade, assentamento, `econ`, rota, NPC, posto); sem lugar, a primeira cidade do reino (ASSUMINDO). **Regra:** a cada 12 dias o Eco passa a vizinhos por rota **aberta** do mesmo reino, com chance por camada (800, 450, 120 e 20 por mil); a certeza desce a cada 2 passos (confirmado, alegado, rumor); termina em 8 passos ou sem vizinho novo. Um delta `campo difusao` por passo (importância 0): `quem_sabe` = lugares que souberam agora, a rota usada fica na causa. Consulta "quem sabe o quê e desde quando" = ler os deltas `difusao` do Eco. **Limites:** só dentro do reino; sem Ecos de campanha ou do jogador; sem distorção de conteúdo (só da certeza); I-09 segue pendente (conferência por fato `secreto` do molde vem com os mistérios, F5.3); sem testes novos (só ao fim da F5).

### Como ficou a F5.3 (07/11/2026)
`motor/motores/misterios.py` (anual) e `dinamica/misterios.txt` (2 chaves `PROPOSTO`: `mist_pista_pm` 70 e `mist_max_pistas` 6, em `Dinamica.mist`). **Estado novo:** `mist.*` (53 mistérios: `estado` aberto, `pistas`, `localidade` = primeira cidade ou assentamento de `entidades`). **Regra:** por ano, cada mistério aberto tem 7% de chance de uma pista, até 6; a pista (`pista_de_misterio`, importância 3, certeza `suspeita`) aponta uma das `hipoteses_abertas` do molde, sem criar fato. O motor **nunca resolve**. A pista vira Eco `restrito`. **Invariantes novos (por delta):** I-08 (resolver mistério exige agente, evidência contida em `evidencia_para_resolver` e verdade fora de `a_verdade_nao_pode_ser`; defeito proposital é detectado) e I-09 (Eco `restrito` ou `secreto` só avança com rota na causa). **Limites:** só há pistas, sem investigador; I-09 não confere fato `secreto` do molde virando público (fatos não são entidades do estado); sem testes novos (só ao fim da F5).

### Como ficou a F5.4 e o fecho da F5 (07/11/2026)
`motor/motores/fronteira.py` (anual) e `dinamica/fronteira.txt` (`fronteira_limite_anos` 120, em `Dinamica.front`). Regime `selado` (Era I): **I-10** confere por delta: sem criação de rota, sem resolução do Mistério D, sem agente de outro reino na entidade e sem troca de dono de Torvelume (titular de outro reino). Por segurança (D-M4), sem gatilho a era termina aos 120 anos: delta `era` para `era.002` (Fronteiras Porosas, `poroso`). Incidentes, tratados e guerras entre reinos (regimes poroso e aberto) são da F7. Versão do motor F5.4; `PENDENTES` agora: I-12, I-16 a I-18 e I-20 (F6).
**Ajustes da F5.1 vistos nos testes de fim de fase:** (1) `recurso` não tem coluna `reino`; o reino vem da localidade (`world.reino_de`); (2) a escassez passou a ser o déficit que **piora** além do déficit estrutural do molde (todas as rotas abertas e produção cheia); sem isso cerca de 1.850 `revolta` por século vinham de consumidores sem produtor alcançável. Agora: 0 revolta em 100 anos nas sementes 1 e 2.
**Resultado de 100 anos** (sementes 1 a 3): 10 blocos `OK`, cerca de 35 mil deltas e 2.900 a 3.100 notificações por século (a maioria `bloqueio_de_rota`, cerca de 17 por ano no mundo), 230 pistas de mistério, 0 mistério resolvido, I-01, I-03 a I-11, I-13 a I-15 e I-21 ok. **Limites:** a calibragem da frequência de bloqueio e de pista é da F8; escassez ainda não pressiona relógios nem tensão; sem comércio entre reinos (M-07); I-20 e I-16 a I-18 na F6.

### Como ficou a F6.1 (07/11/2026)
`motor/relatorio.py`: gera `relatorio.md` e `metricas.csv` a partir dos deltas da crônica (determinístico; fora dos hashes da rodada). O simulador os grava ao fim de cada `rodar`; uma rodada pronta se regenera com `python3 motor/relatorio.py saidas/<rodada>`. `metricas.csv` = série anual por reino (`ano|reino|deltas|importantes|mortes|sucessoes|relogios_estourados|eventos|bloqueios|pistas|rupturas|tratados|revoltas|npcs_simulados`); o reino vem do molde, das entidades criadas, de `econ`, `par`, `posto` e dos Ecos (o delta `era` e entidades sem reino ficam em `—`). `relatorio.md` = cabeçalho, uma seção por era (nome, regime, anos), uma subseção por década (contagens e até 15 marcos de importância 4) e tabela total por reino. Versão do motor F6.1. **Limites:** sem "quem governava no ano N" nem consultas por entidade (F6.2); sem comparação entre sementes (F6.3); sem testes novos (só ao fim da F6).

### Como ficou a F6.2 (07/11/2026)
**Invariantes** (`invariantes.checar_bloco`, ao fim de cada bloco; `PENDENTES` fica vazio): **I-12** (E) a mesma data do mundo dá o mesmo dia do ano nos calendários válidos dos cinco reinos; **I-16** (A) todo reino mantém NPC vivo (proxy de população: o cânone só tem faixas); **I-17** (A) mínimo de deltas de importância 3+ por reino a cada 10 anos (só em bloco completo); **I-18** (A) teto de deltas de importância 3+ por reino em um ano; **I-20** (E) toda causa remonta ao molde ou a um delta anterior (cadeia sem raiz falha). Faixas em `dinamica/invariantes.txt` (`inv_min_importantes_decada` 3 e `inv_max_importantes_ano` 60, `PROPOSTO`, em `Dinamica.inv`). **Consultas** (`motor/consultas.py`, só leitura): `linha_do_tempo` (alvo, agente ou causa), `cadeia_causal` (ancestrais e raízes), `titular_em` (quem ocupa um posto no fim de um ano, com vacância e interino) e `relogio_em` (progresso e estouro em um ano), por funções e por CLI (`python3 motor/consultas.py <rodada> linha|cadeia|titular|relogio ...`); `estado_em` refaz o estado até o fim do ano pelo replay. Versão do motor F6.2. **Limites:** I-16 não tem teto de população; I-17 e I-18 usam importância 3+ como medida de "evento"; sem testes novos (só ao fim da F6).

### Como ficou a F6.3 (07/11/2026)
`motor/lote.py` e `--lote N` no simulador (`python3 motor/simulador.py --lote 20 --anos 500 --passo 10 [--semente S] [--saida nome]`): N sementes consecutivas (a partir de `--semente`) sobre o mesmo molde, cada rodada completa em `saidas/<lote>/rodada-s<S>/` (padrão `lote-<anos>a-<N>s`); o molde e os motores são carregados uma vez. Grava `lote.csv` (uma linha por semente) e `lote.md`: fim da Era I, primeiro incidente de fronteira (`incidente_fronteira`; as `ruptura` e `disputa_aberta` da política são internas aos reinos e não contam), primeira guerra (`guerra`), reinos caídos (`queda_de_coroa`; quantas sementes por reino, com aviso de reino que cai em todas), mistérios resolvidos, relógios estourados no 1º século, relógios que nunca ou sempre estouram, e menor e maior número de deltas 3+ por reino em uma década (mundo morto e caos). Uma rodada com FALHA não interrompe o lote (fica marcada e fora das estatísticas); código de saída 1 se alguma falhar. Um lote pronto se regenera com `python3 motor/lote.py saidas/<lote>`. Versão do motor F6.3. **Limites:** guerra, incidente e queda só aparecem a partir da F7 (hoje as colunas saem vazias); sem retomada de lote (F6.4); sem testes novos (só ao fim da F6).

### Como ficou a F6.4 (07/11/2026)
**Retomada e CLI** (`simulador.py`): `--retomar <rodada> --anos +N` (com `--retomar`, `N` e `+N` valem o mesmo: N anos a mais) e `--ate-ano A` (até o ano A do mundo, com ou sem `--retomar`; erro se a rodada já passou dele); `--anos` e `--ate-ano` são exclusivos e um é obrigatório. Na retomada o bloco (`--passo`) volta ao da rodada original, salvo se informado; `progresso.json` guarda `passo` e os alertas e violações acumulados, de modo que `invariantes.txt` cobre a rodada inteira; os Ecos antigos são reconhecidos por reino; rodada terminada em FALHA não retoma. Erros de uso saem como `ERRO: ...` com código 2. Conferido: 10 + 10 anos retomados dão a mesma crônica e o mesmo estado de 20 anos direto (I-19). Versão do motor F6.4.

**Testes completos de fim de F6** (locais, sem rede): scripts compilam; **37 testes passam** (12 da F3, 16 da F4, 9 da F5). Correção da fase: `test_f4.test_vacancia_e_nomeacao_de_simulado` esperava `OK`; a morte em massa de um reino gera agora alerta I-16 a I-18 (F6.2), nunca falha, e o teste passou a aceitar só esses alertas. 100 anos × 3 sementes: 10 blocos `OK` cada (I-12, I-16 a I-18 e I-20 incluídos); lote de 2 sementes × 20 anos gera `lote.md` e `lote.csv`; validação do mundo 0 erro e 20 avisos (hash f11f1c120acc99b1); `reinos/`, `mundo/`, `referencia/`, `referencia-tecnica/`, `plano/ferramentas/` e `dinamica/` idênticos à base. 0 falha, 0 regressão.
**Limites:** sem teste novo para `--ate-ano`, `--lote` e `lote.py` (conferidos à mão); sem retomada de lote; guerra, incidente de fronteira e queda de coroa só existem na F7.

### Como ficou a F7.1 (07/11/2026)
`motores/fronteira.py` (anual) e `estado._fronteiras`: um par `front.<a>+<b>` por vizinhança do anel de M-04 (cinco pares: Coroas de Pedra–Aeltharyn–Azeroth–Véu dos Malditos–Vale Palancar–Coroas; ASSUMINDO Pacto Humano = Azeroth e Marcas das Feras = Vale Palancar), com `tensao` (pontos), `incidentes` e `ultimo_dia`, derivado do molde (só com dinâmica). Por ano a tensão soma `front_tensao_base`, `front_tensao_por_relogio` por relógio estourado nos dois reinos e um sorteio de 0 a `front_tensao_jitter`; ao chegar a `front_tensao_limiar` (300) o par emite um `incidente_fronteira` (registro de importância 3, causa = o par e até 3 relógios estourados, `quem_sabe` = os dois reinos) e a tensão cai para `front_tensao_pos_incidente` (150), sem zerar. **O primeiro incidente é o gatilho do fim da Era I (D-M4):** delta `era` para `era.002` (Fronteiras Porosas, `poroso`, causa `primeiro-incidente-de-fronteira`); o limite de 120 anos continua como segurança. No regime poroso os pares seguem produzindo incidentes; I-10 só vale no selado. Chaves novas em `dinamica/fronteira.txt` (6, `PROPOSTO`, geradas por `gerar_dinamica.py`). `relatorio.py`: `incidente_fronteira` conta em "rupturas e disputas" e o par pertence ao primeiro reino do nome; `lote.py`: anos das metas passam a ser contados desde o início da rodada. Versão do motor F7.1.
**Resultado** (sementes 1 a 5, 130 anos, só conferência de blocos): 13 blocos `OK` em cada, primeiro incidente e fim da Era I entre os anos 42 e 58 desde o início (corrigido na F7.2: o ano-base é 742; a versão do motor, que ficara F6.4, passou a F7.2), 20 a 30 dos 40 relógios estourados no 1º século. **Limites:** os incidentes ainda não têm agentes nem efeitos (tratados, rupturas e quebras de contrato são da F7.2); o Eco de um incidente só se espalha pelo reino do primeiro nome do par; a frequência de incidentes depois do primeiro (cerca de um a cada 9 anos por par com muitos relógios estourados) só é calibrada na F7.4; `test_f5` (fim da Era I aos 121 anos) precisa ser ajustado ao gatilho e fica para os testes do fim da F7; sem testes novos (só ao fim da F7).

### Como ficou a F7.2 (07/11/2026)
`motores/diplomacia.py` (anual, só fora do regime selado) e `dinamica/diplomacia.txt` (10 chaves `dipl_*`, `PROPOSTO`, geradas por `gerar_dinamica.py`; `Dinamica.dipl`). Cada par `front.<a>+<b>` ganha `relacao` (`neutra`, `tratado`, `ruptura`), `relacao_dia` e `pendente`. O motor de fronteira marca o incidente como `pendente`; por até `dipl_janela_anos` (5) o motor de diplomacia pode, a cada ano, assinar um **tratado** (`dipl_tratado_pm`: tira 120 pontos de tensão, dura 30 anos, desacelera a subida a 50%; agentes = o governante vivo de uma cidade de cada reino, o que só é permitido fora do selado e fica registrado) ou declarar uma **ruptura** (`dipl_ruptura_pm`: soma 60 pontos, dura 12 anos, acelera a subida a 150%); sem resposta o incidente esfria (`incidente-sem-resposta`). Um tratado vigente pode ser **quebrado** (`dipl_quebra_pm`, 25 por mil por ano): registro `quebra_de_contrato` e relação `ruptura`. Tratado e ruptura vencidos voltam a `neutra`. Uma transição por par por ano, tudo com causa. `relatorio.py`: `quebra_de_contrato` conta em "rupturas e disputas" (o `tratado` já tinha coluna; atenção: a política de facções também emite `tratado` e `ruptura`, dentro dos reinos, e `front.*` é que distingue os de fronteira). Versão do motor F7.2.
**Resultado** (sementes 1 a 4, 200 anos, só conferência de blocos): 20 blocos `OK` em cada; por semente, 29 a 40 incidentes de fronteira (o primeiro entre os anos 42 e 58), 22 a 27 tratados, 5 a 15 rupturas e 9 a 17 quebras de contrato entre pares. **Limites:** as relações ainda não afetam rotas, economia nem Ecos entre reinos (M-07), e a ruptura não leva a guerra (F7.3); a frequência de quebras e incidentes é só uma primeira calibragem (F7.4); não há invariante próprio de transição de relação (I-23 em diante, F7.4); `test_f5` ainda precisa do ajuste ao gatilho; sem testes novos (só ao fim da F7).

### Como ficou a F7.3 (07/11/2026)
`motores/guerra.py` (anual, só fora do selado) e `dinamica/guerra.txt` (8 chaves, `PROPOSTO`, geradas por `gerar_dinamica.py`; `Dinamica.guerra`). Novas entidades derivadas do molde: `crown.<reino>` (`estado` firme ou caída, `caida_dia`) para os cinco reinos. Um par `front.*` em `ruptura`, com tensão de pelo menos `guerra_tensao_min` (240; tem de ficar abaixo do limiar de incidente, 300, senão nunca se alcança) e as duas coroas firmes, entra em **guerra** por `guerra_pm` (80 por mil por ano): registro `guerra` (importância 4) e relação `guerra`. **A primeira guerra muda o regime para `aberto`** (`era.003`, Fronteiras Abertas, causa `primeira-guerra`). Em guerra a tensão do par fica congelada (a fronteira não gera incidente e a diplomacia não age); cada reino pode sofrer baixa (`morte-em-guerra`: morre um principal ou general, só com pelo menos 4 vivos) e, depois de 3 anos, a coroa pode **cair** (`queda_de_coroa`, `crown.<reino>` = `caida`, a guerra termina e abre-se uma era nova com o nome do reino, mesmo regime). Sem queda, a guerra acaba em 6 anos com um tratado de paz (`paz-apos-guerra`, agentes = governantes vivos). Coroa caída não volta a entrar em guerra. `relatorio.py`: `guerra` e `queda_de_coroa` contam em "rupturas e disputas"; `crown.*` pertence ao próprio reino (o lote já conta reinos caídos). Versão do motor F7.3.
**Resultado** (sementes 1 a 4, 300 anos, só conferência de blocos): 30 blocos `OK` em cada; primeira guerra entre os anos 65 e 103 desde o início (meta: 40 a 250); 6 a 9 guerras, 4 a 7 pazes, 1 a 6 baixas e 0 a 2 quedas de coroa por semente (3 das 4 sementes com 2 quedas em 300 anos). **Limites:** a queda de coroa ainda não afeta sucessão, economia, rotas nem Ecos, e a coroa caída não se restaura; as quedas estão frequentes para a meta de 0 a 2 em 500 anos e a calibragem é da F7.4; guerra e queda não mudam o molde (só a crônica e as entidades derivadas); sem invariante próprio de transição de relação (I-23 em diante, F7.4); `test_f5` ainda precisa do ajuste ao gatilho; sem testes novos (só ao fim da F7).

## 5. Estrutura de pastas (nova)

```
Mythralis/
  motor/
    carregador.py     # lê os 5 reinos num World; esquema único
    validador.py      # regras de mundo (IDs únicos no mundo, referências, calendário, grafo)
    calendario.py     # dia do mundo <-> data local de cada reino
    estado.py         # estado mutável + aplicação de deltas
    cronica.py        # escrita/leitura/replay da crônica; hash
    motores/          # demografia, politica, economia, relogios, eventos, ecos, fronteira
    simulador.py      # laço de tempo, eras, CLI
    invariantes.py    # testes de continuidade
    relatorio.py      # métricas e relatório por era
    testes/
  dinamica/           # parâmetros propostos (texto com |, igual ao molde)
  mundo/              # M-06, M-07, M-09, M-11, M-12 passam a existir como dados
  saidas/             # crônicas e relatórios de cada rodada (não versionar o volume)
```

## 6. Tempo

- **Unidade:** o **dia do mundo** (dia do ano 1 a 365 + ano). Os motores rodam em frequências diferentes dentro do mesmo laço:

| Frequência | O que roda |
|---|---|
| diária | propagação de Ecos, deslocamento de viajantes, eventos agendados |
| lunar (a cada 30 dias) | relógios, tensão de facções, economia e rotas |
| sazonal (4 por ano) | colheita, clima e bloqueio de rotas, fluxos de migração |
| anual (dia 1) | idade, morte natural, sucessão, nascimentos e chegadas, balanço do reino |

- **Ano de início:** o ano 742 de Aeltharyn (Silêncio, Lua 1, dia 1), igual a 516 das Atas, 389 das Provações, 611 dos Marcos e 457 das Chaves (M-02, deslocamentos ASSUMINDO). Cada reino mostra o ano na sua contagem.
- **Custo:** 500 anos são cerca de 182 mil dias, mas os motores só trabalham em datas de sua frequência; deve rodar em poucos minutos em Python.

### 6.1 Dois modos de tempo (decisão D-M12, 07/11/2026)

| Modo | Passo de controle | Para quê |
|---|---|---|
| **Simulador** | **bloco de 10 anos** (configurável: `--passo 10`) | rodar o mundo sem jogador e conferir se está tudo bem |
| **RPG literário** | **1 dia** | o calendário corre dia a dia; cena, viagem e descanso avançam o dia do mundo |

O laço interno é o mesmo nos dois (o dia do mundo, seção 6): o bloco de 10 anos é só o **ponto de parada e conferência**, e não muda o resultado (determinismo, I-02). Quando o RPG pula tempo ("passaram-se três anos"), o motor roda os dias pulados em blocos de até 10 anos, com a mesma conferência.

### 6.2 Check de saúde por bloco
Ao fim de cada bloco o simulador roda o **check de saúde** e grava uma linha em `saude.txt`:
- invariantes de erro (I-01 a I-14, I-19 a I-22);
- métricas da seção 14 do bloco (eventos por reino, cargos vagos, população, relógios estourados);
- resumo das **notificações** do bloco (seção 10.1).
Resultado: `OK`, `ALERTA` (invariante A ou métrica fora da faixa) ou `FALHA` (invariante E). Na `FALHA` a rodada **para** e deixa o snapshot do bloco anterior para depuração. Quem lê isso é o autor/desenvolvedor, não o jogador.

## 7. Motores

Cada motor tem: **entrada canônica** (o que lê do molde), **regra** (o que calcula) e **saída** (deltas na crônica). Todos usam um gerador aleatório **por motor e por data**, derivado da semente, para que mudar um motor não embaralhe os outros.

| Motor | Lê do molde | Regra resumida | Produz |
|---|---|---|---|
| **Demografia de pessoas** | `npc` (idade, povo, vivo, cargo, papeis) | idade por faixa e longevidade do povo; morte natural cresce com a idade; morte violenta vem de eventos | morte, doença, aposentadoria |
| **Sucessão** | `cargo`, `papeis` (governante, herdeiro, general), `faccao.lideranca`, `relacao` (vínculo de cargo) | cargo vago: herdeiro → vice nomeado por relação de cargo → candidato da facção → vacância com prazo; se ninguém serve, **nasce um NPC simulado** | posse, disputa, vacância, golpe |
| **Nascimentos e chegadas** | `povo_no_reino`, M-05, M-10 (nomes) | novos NPCs só quando o motor precisa (sucessão, filho de principal, migrante); nome por M-10; `origem=SIMULADO` | nascimento, chegada, partida |
| **Política de facções** | `faccao` (aliados, rivais, tensão), `relacao` (aliança, rivalidade, medo, dívida, tensão) | tensão por par sobe com rivalidade e pressão, desce com aliança e dívida paga; passa de limiar → rompimento, tratado, disputa aberta | tratado, ruptura, cisma, extinção, fusão |
| **Relógios** | `relogio` (segmentos, progresso, move, trava, paga, completa, inacao) | cada relógio tem **regra dinâmica** (9.3): avança com suas pressões, recua com travas, estoura em evento; inação avança devagar | avanço, estouro, resolução |
| **Eventos** | `evento` (gatilho, relogio, cadeia), `semente` (se_ninguem_age), `misterio` | evento nasce do estouro de relógio ou de gatilho; a `cadeia` vira passos em ordem com atraso; `se_ninguem_age` é a consequência quando não há jogador | cadeias de fatos |
| **Economia e rotas** | `recurso` (papel, produção, demanda 0 a 5), `rota` (estado, condição), `cidade.economia` | oferta e demanda ordinais por lugar; rota fechada ou cheia muda demanda no destino; escassez alimenta relógios e tensão | escassez, excedente, bloqueio, guerra de preço |
| **Ecos** | `canal`, `reputacao`, `fato` (camada, quem_sabe, caminho), grafo de lugares | cada fato novo parte do lugar de origem e **se espalha por passos** do grafo, mais devagar onde a camada é restrita; vira rumor distorcido a cada passo | quem sabe o quê, rumores, versões exageradas |
| **Fronteira** | contratos de fronteira, M-04 (anel), M-05 §5 (migrações), M-07/M-09 | decide o que **pode cruzar** conforme a era (seção 8) | fluxos, incidentes, guerra, ruptura |
| **Mistérios** | `misterio` (hipoteses_abertas, `a_verdade_nao_pode_ser`, evidencia) | o mundo **pode andar em torno** de um mistério, mas só resolve com a evidência listada; sem agente investigador nada se resolve | pistas, não respostas |

**Sobre os mistérios:** sem jogador, quase todo mistério fica aberto por séculos. Isso é o comportamento correto e vira métrica (14).

## 8. Eras livres e regimes de fronteira

**Não há número fixo de eras nem duração fixa (D-12/D-M13).** O tempo é livre: `--anos N`, `--ate-gatilho` ou rodar até parar.

- **Regime de fronteira** é um **estado do mundo**, e não uma contagem de anos. Começa **selado** (os contratos de fronteira valem como regras do motor: não resolver o Mistério D, não criar rota nova, não trocar Torvelume de dono, não mudar entidade de outro reino), passa a **poroso** (contratos viram avisos; toda quebra vira evento com causa) e a **aberto** (sem restrição, desde que haja causa, agentes e consequência).
- A mudança de regime acontece por **gatilho** (primeiro incidente de fronteira, primeira guerra ou ruptura), nunca por data. Segurança: um ano máximo configurável para o primeiro gatilho (padrão proposto: 120 anos).
- **Era** é só um **capítulo nomeado pela crônica** quando um marco acontece (mudança de regime, queda de coroa, guerra, sucessão de coroa), com a duração que tiver. Cada mudança fica registrada como evento de era.
- **Nenhum regime permite:** apagar o molde, alterar a prosa canônica, resolver mistério sem a evidência listada, criar efeito sem causa ou criar entidade fora da regra de origem (3).
- **Cânone confirmado** (D-M3): fatos `confirmado` do molde só mudam por evento com causa; `alegado`, `suspeita`, `rumor` e `inferência` podem ser reforçados ou derrubados com registro.

## 9. Camada de dinâmica (os números que o cânone não tem)

### 9.1 O problema
O cânone escolhe não dar número. O motor precisa de número para avançar. Se o motor inventar números em silêncio, está inventando lore.

### 9.2 A solução
Toda quantidade de simulação mora em `dinamica/*.txt`, no formato `|` do resto, cada linha com `fonte` e `etiqueta` (`PROPOSTO` ou `ASSUMINDO`). O motor **só lê**. O molde nunca as importa. Trocar um valor nunca altera o cânone, só muda a simulação. Primeiros arquivos:

| Arquivo | Conteúdo |
|---|---|
| `longevidade.txt` | uma **classe de longevidade** por raça (efêmera, curta, humana, longa, milenar, sem idade), com faixas de idade e curva de morte; as 50 raças e 50 sub-raças |
| `faixas_etarias.txt` | tradução de "jovem", "adulta", "madura", "idosa" em intervalo por classe de longevidade (para os NPCs com `?` ou faixa) |
| `limiares.txt` | tensão que rompe aliança, tensão que abre guerra, escassez que gera revolta, etc. |
| `relogios_dinamica.txt` | **regra executável por relógio** (9.3) |
| `economia.txt` | escalas ordinais 0 a 5, efeito de rota fechada, de estação e de bloqueio |
| `ecos.txt` | atraso por passo do grafo, perda de precisão por passo, efeito de camada |
| `era.txt` | regimes, gatilhos de transição e o que cada contrato bloqueia |

### 9.3 Relógios executáveis
Os campos `move`, `trava`, `paga`, `completa` e `inacao` do molde são **prosa**. Para executar, `relogios_dinamica.txt` traz por relógio: `taxa_base`, lista de **pressões** (ids de entidade, relação ou recurso que o fazem avançar), lista de **travas** (ids que o fazem recuar), `evento_ao_completar` (id de `evento.`) e `efeito_da_inacao` (taxa extra). Para os ~40 relógios o arquivo é **gerado por um script** a partir de `entidades`, `evento.relogio` e das relações, e **revisado à mão**. Relógio sem regra recebe o padrão e **entra num aviso do validador**.

### 9.4 Salvaguardas contra deriva
- **Homeostase:** pressão acumulada decai; rupturas pedem "custo"; não existe só espiral para cima.
- **Tetos por era:** nº de guerras simultâneas, de mortes por ano e de coroas caídas têm limite configurável (parâmetro, não regra de lore).
- **Alvos de calibragem** na seção 14.

## 10. Crônicas e Ecos

- **Crônica:** `saidas/<rodada>/cronica.jsonl`, uma linha por delta, em ordem; cabeçalho com semente, hashes do molde e da dinâmica e versão do motor.
- **Resumo legível** de cada década e de cada era, gerado a partir dos deltas (nunca escrito à mão), para o autor ler "o que aconteceu".
- **Ecos:** para cada delta, o motor guarda **quem sabe** e **com que certeza**, no mesmo vocabulário do molde (`confirmado`, `alegado`, `suspeita`, `rumor`, `inferência`, `desconhecido`; camadas `publico`, `local`, `restrito`, `secreto`). Consulta de exemplo: *"o que Corlan sabe da morte de Odile, e desde quando?"*.
- **Consultas obrigatórias** (viram testes): linha do tempo de uma entidade; cadeia causal de um evento; quem ocupa cada cargo em qualquer data; estado de um relógio em qualquer data.

### 10.1 Importância e notificações
Todo delta é registrado, mas cada um recebe `importancia` de 0 a 4 por regra. **Importância 3 ou mais dispara o check de notificação:** grava em `notificacoes.jsonl` (o quê, quando, onde, causa, quem sabe) e roda um mini-check dos invariantes naquele ponto; se algum falha, a notificação sai como **ALERTA**.
Contam como importantes: morte, doença grave ou sucessão de NPC principal e vacância de cargo; relógio que estoura; tratado, ruptura, cisma, fusão ou extinção de facção; rota bloqueada ou escassez que gera revolta; pista de mistério; incidente de fronteira e quebra de contrato; mudança de regime ou de era; guerra, queda de coroa e fato `confirmado` alterado; NPC simulado em cargo de poder.
**Padrão (D-M11):** só registrar e continuar; `--interativo` (pausar a cada notificação) fica opcional para rodadas únicas.

## 11. Simulador

```
python motor/simulador.py --anos 200 --passo 10 --semente 7
python motor/simulador.py --lote 20 --anos 500 --passo 10          # 20 sementes, comparação
python motor/simulador.py --retomar saidas/r01 --anos +100
```

Saídas por rodada: `cronica.jsonl`, `notificacoes.jsonl`, `saude.txt` (uma linha por bloco de 10 anos), `relatorio.md` (por era e por década), `metricas.csv` (série anual), `invariantes.txt` (resultado dos testes) e **snapshots** a cada bloco (os antigos podem ser podados) (para retomar e comparar).

**Modo lote:** várias sementes sobre o mesmo molde, com estatística: em quantas rodadas o reino X caiu, quando houve a primeira guerra, quais relógios nunca estouram. Serve para achar **desequilíbrio** (reino que sempre cai) e **mundo morto** (nada acontece).

## 12. Invariantes de continuidade

Gravidade: **E** = falha (a rodada é inválida), **A** = alerta (revisão do autor).

| # | Invariante | G |
|---|---|---|
| I-01 | **Molde intacto:** hash dos `dados/` e dos `mundo/` igual antes e depois | E |
| I-02 | **Determinismo:** mesma semente, mesma crônica (hash) | E |
| I-03 | **Morto não age:** nenhuma entidade com morte registrada é agente, governante ou líder depois da data | E |
| I-04 | **Cargo ocupado ou vacância válida:** todo governante e líder de facção tem titular, ou vacância com prazo e responsável interino | E |
| I-05 | **Referências válidas:** todo id em deltas existe no molde ou foi criado por delta com origem | E |
| I-06 | **Toda mudança tem causa** (relógio, tensão, morte, agente, regra) | E |
| I-07 | **Origem marcada:** todo fato novo é `SIMULADO`; nada novo entra como `ORIGINAL` | E |
| I-08 | **Mistério sem evidência não se resolve** | E |
| I-09 | **Camada respeitada:** fato `secreto` ou `restrito` não vira conhecimento público sem caminho registrado nos Ecos | E |
| I-10 | **Contrato de fronteira na Era I:** nenhum efeito proibido ocorre | E |
| I-11 | **Aliado e rival ao mesmo tempo é proibido** (regra V-xx do validador, agora dinâmica) | E |
| I-12 | **Calendário coerente:** a mesma data do mundo, em qualquer reino, converte para o mesmo dia do ano | E |
| I-13 | **Ordem causal:** causa vem antes do efeito na crônica | E |
| I-14 | **Linhagem:** todo NPC simulado tem origem (nascimento, chegada, nomeação) e povo/sub-raça válidos no catálogo | E |
| I-15 | **Não há entidade sem lugar** (NPC vivo sempre tem cidade, rota ou "em trânsito" com destino) | A |
| I-16 | **Mundo não morre:** população de cada reino não chega a zero nem passa de um teto | A |
| I-17 | **Mundo não congela:** há um mínimo de eventos por década por reino | A |
| I-18 | **Sem explosão:** nº de eventos por ano abaixo do teto | A |
| I-19 | **Retomada coerente:** simular 100 anos, parar e retomar por mais 100 dá a mesma crônica de rodar 200 de uma vez | E |
| I-20 | **Legibilidade:** para cada evento, a cadeia causal remonta até o molde ou um evento anterior | E |
| I-21 | **Notificação completa:** todo delta de importância 3 ou mais tem notificação | E |
| I-22 | **Passo neutro:** rodar com `--passo 1`, `--passo 10` ou `--passo 50` dá a mesma crônica (hash) | E |

**Teste de continuidade em anos:** o item que o autor pediu. Roda-se 100, 250 e 500 anos (ou o tempo que o autor quiser); I-01 a I-20 precisam passar em todas as sementes do lote, e o relatório lista, por reino, "quem governava no ano 50, 100, 200..." e por quê.

## 13. Dados que ainda precisam existir (camada do mundo)

Sem estes, a Era II e a Era III ficam pobres. São **cânone novo**, então vão para o autor aprovar (como todo `PROPOSTO`):

| Doc | Para quê | Tamanho mínimo útil |
|---|---|---|
| **M-07 Rotas e economia entre reinos** | grafo mundial de lugares (cidades-porta de cada reino, ligações entre reinos em passos), moeda, comércio, tratados | arestas entre os 5 reinos seguindo o anel de M-04 |
| **M-09 Linha do tempo e conflitos globais** | passado do mundo que as eras continuam; causas de rivalidade entre reinos | uma linha do tempo e 3 a 5 tensões entre pares de reinos |
| **M-06 Facções transnacionais** | guildas, ordens, redes comerciais e sociedades secretas que cruzam fronteira (úteis em eras livres) | 5 a 8 facções, cada uma com ids e presença |
| **M-11 Figuras de nível mundial** | NPCs que atravessam reinos | poucos, com ficha |
| **M-12 Memória do mundo** | formato dos marcos e eventos persistentes | **este plano já define o formato (seção 10)**; M-12 passa a ser o documento do autor sobre ele |
| M-08 Religião | só se uma regra do motor pedir | opcional |

Até existirem, o motor usa **placeholders marcados** (grafo mínimo gerado do anel de M-04, sem dias, só passos). A Era I roda sem eles.

## 14. Calibragem e metas

Metas **iniciais** para uma rodada de 500 anos, revisadas depois da primeira execução (não são lore):

| Métrica | Meta inicial |
|---|---|
| Mortes naturais de NPCs principais | coerentes com a longevidade (elfos e humanos não morrem no mesmo ritmo) |
| Cargos vagos ao mesmo tempo | raro, e nunca por mais de uma geração |
| Relógios estourados no 1º século | 40% a 70% dos 40 |
| Primeiro incidente de fronteira | entre os anos 20 e 120 em maioria das sementes |
| Primeira guerra entre reinos | entre os anos 40 e 250 em maioria das sementes |
| Reinos caídos em 500 anos | 0 a 2, e **nunca o mesmo em todas as sementes** (senão há desequilíbrio) |
| Mistérios resolvidos sem agente | 0 |
| Eventos por reino por década | entre 3 e 40 |

## 15. Fases e critério de aceite

| Fase | Entrega | Aceite |
|---|---|---|
| **F0** | este plano, nome Mythralis, README e CHANGELOG | autor aprova o plano e as decisões D-M1 a D-M8 |
| **F1 ✔** | `converter_reino1.py`: `dados/` de Aeltharyn no `esquema=1` | `validar_reino.py` sem erro nos **5** reinos (feito: 0 erro; Aeltharyn 136 avisos) |
| **F2 ✔** | carregador único + validador de mundo + calendário (`motor/`) | os 5 reinos carregam juntos; IDs únicos no mundo; I-12 passa (feito: 0 erro, 19 avisos em `saidas/validacao-mundo.txt`) |
| **F3 ✔** | estado, crônica, replay, hash, campos `importancia`/`notificacao`, passo de 10 anos e `saude.txt` | I-01, I-02, I-19, I-21 e I-22 passam (feito: `motor/testes/test_f3.py`, 12 testes, motor vazio e motor-sonda; também rodam I-03, I-05, I-06, I-07, I-13 e I-14 por delta) |
| **F4 ✔** | dinâmica v0 (gerada) + demografia, sucessão, relógios, eventos, política | rodar 100 anos **só dentro dos reinos** (Era I) sem erro (feito: 100 anos × 3 sementes, 10 blocos OK; `motor/testes/test_f4.py`, 28 testes no total) |
| **F5 ✔** | economia, rotas, Ecos, mistérios, contratos de fronteira (submódulos, ASSUMINDO: **F5.1 ✔** economia e rotas; **F5.2 ✔** Ecos; **F5.3 ✔** mistérios; **F5.4 ✔** contratos de fronteira) | I-01 a I-14 passam em 100 anos (feito: 100 anos × 3 sementes, 10 blocos OK; `motor/testes/test_f5.py`, 37 testes no total) |
| **F6 ✔** | simulador completo, lote, relatório, retomada (submódulos, ASSUMINDO: **F6.1 ✔** relatório e métricas; **F6.2 ✔** invariantes I-12, I-16 a I-18, I-20 e consultas obrigatórias; **F6.3 ✔** modo lote; **F6.4 ✔** retomada `+anos` e CLI) | I-01 a I-20 passam; relatório por era |
| **F7** | regimes poroso e aberto: tratados, rupturas, guerras, tramas (submódulos, ASSUMINDO: **F7.1 ✔** tensão entre reinos vizinhos, incidentes de fronteira e fim da Era I por gatilho; **F7.2 ✔** tratados, rupturas e quebras de contrato no regime poroso; **F7.3 ✔** guerras, quedas de coroa e regime aberto; F7.4 tramas, I-23 em diante e primeira calibragem 500 anos × 20 sementes) | 500 anos × 20 sementes, metas da seção 14 dentro da faixa |
| **F8** | calibragem e fechamento | relatório final ao autor; `DECISOES.md` atualizado |

**Regra de trabalho:** cada fase termina com testes rodando e com o relatório de invariantes anexado; só abre a próxima depois de revisada (mesma disciplina dos reinos).

## 16. Decisões em aberto para o autor

| # | Decisão | Padrão proposto |
|---|---|---|
| **D-M1** | Nome do projeto e do mundo: **Mythralis** | **aprovado pelo autor em 07/11/2026** |
| **D-M2** | Linguagem do motor: Python agora | Python (stdlib) |
| **D-M3** | Fatos `confirmado` do molde na Era I | só mudam por evento com causa |
| **D-M4** | Gatilho de fim da Era I | primeiro incidente de fronteira, com limite de 120 anos |
| **D-M5** | NPCs simulados (nascimentos, chegadas): permitidos? | sim, só quando o motor precisa (sucessão, migração), nome por M-10 |
| **D-M6** | Tabela de longevidade das 50 raças | classes de longevidade propostas por mim, revisadas por você |
| **D-M7** | Formato da crônica: `jsonl` ou texto com `\|` | `jsonl` (aninhamento) para a máquina + resumo legível para o autor |
| **D-M8** | Quem escreve M-07 e M-09 | autor decide; até lá, placeholders marcados |
| D-M9 | Passo do tempo: dia, com frequências | dia (seção 6) |
| D-M10 | Morte de NPC "principal" pode ocorrer na Era I? | sim, por idade ou evento; a sucessão cobre |
| **D-M11** | O que o simulador faz numa notificação | só registrar e continuar (`--interativo` opcional) |
| **D-M12** | Passo do simulador e do RPG | simulador em blocos de 10 anos com check de saúde; RPG literário por dia |
| **D-M13** | Número e duração das eras | **livres**: sem 3 eras nem 100 anos; regime por gatilho |

## 17. Integração futura com o RPG Solo (só direção)

Fica para depois do motor funcionar. O desenho já a prepara:
- O motor é um **leitor de molde + crônica**, sem dependência do app.
- A campanha do RPG Solo seria **outra crônica** (deltas) sobre o mesmo molde, no mesmo formato.
- O que o jogador faz vira **Eco** que se espalha pelas regras da seção 7.
- O jogo consulta o mundo por cena (quem está aqui, o que sabe, o que mudou), nunca despeja os 5 reinos no contexto.
- O porte para Kotlin parte de um conjunto de **testes de conformidade** em arquivos de dados (entrada → crônica esperada), que os dois motores devem passar igual.

## 18. Riscos

| Risco | Tratamento |
|---|---|
| A simulação inventa lore | camada de dinâmica separada, `origem=SIMULADO`, nada volta ao molde sem aprovação |
| Mundo sem graça (nada acontece) ou caos (tudo cai) | métricas I-16 a I-18 e metas da seção 14; lote com muitas sementes |
| Regra dos relógios é prosa | script gera a regra, o autor revisa; padrão com aviso |
| Rotas sem duração nos reinos 2 a 5 | passos no grafo, não dias; pede M-07 |
| Aeltharyn em formato diferente | F1 converte e valida antes de tudo |
| Resultado diferente a cada rodada | semente por motor e data; I-02 e I-19 |
| Esquema "diferente por reino" | validador de mundo falha se cabeçalhos divergirem |
