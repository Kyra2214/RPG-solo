# Esquema de dados dos reinos (M6.1)

**Estado:** especificação. Nada aqui é código nem implementação. Vale para os cinco reinos de Mythralis; Aeltharyn é o primeiro a ser convertido.
**Fonte:** `INTEGRACAO_FUTURA_RPG_SOLO.md` (molde estático + deltas; formato em aberto), `MODELO_DE_REINO.md`, doc 02 §2 e os CSVs de `reinos/01-aeltharyn-reino-das-florestas/`.

## 1. Decisão de formato (D-21, aguardando o autor)

| Opção | Prós | Contras |
|---|---|---|
| **A. Texto com `|` (recomendada, ASSUMINDO)** | igual aos pacotes do RPG Solo; um arquivo por tipo; diff legível; sem aspas | campo precisa escapar `|` |
| B. JSON | aninhamento natural (listas, objetos) | diff ruim; arquivos longos; o catálogo de personagem já é JSON, mas é dado bruto, não texto editorial |
| C. Manter CSV (vírgula) | já existe em Aeltharyn | vírgula aparece em quase todo texto; exige aspas |

Até o autor decidir, esta especificação usa **A**; os campos e as regras valem igualmente para B e C.

## 2. Regras comuns

- **Arquivo por tipo:** `mundos/<mundo>/reinos/<NN-reino>/<tipo>.txt`. UTF-8, quebra de linha LF, **1ª linha = cabeçalho** com os nomes de campo, uma linha por registro. Linha que começa com `#` é comentário.
- **Delimitador** `|`; um `|` dentro do texto escreve-se `\|`. **Lista** dentro de campo: itens separados por `;`. **Par** `chave=valor` quando preciso.
- **Campo desconhecido** escreve-se `?` (lacuna explícita, não se inventa valor). **Campo não se aplica** escreve-se `—`. Campo obrigatório nunca fica vazio.
- **ID** `prefixo.slug`: minúsculas, sem acento, hífens, único **no mundo** (não só no reino), permanente e nunca reutilizado. Prefixos: `kingdom.`, `city.`, `settlement.`, `npc.`, `faction.`, `route.`, `clock.`, `event.`, `seed.`, `mist.`, `fact.`, `rep.`, `canal.`, `cena.`. Recurso e calendário usam chave composta (ver 4).
- **Referência** é sempre um ID existente; povo, sub-raça, classe e profissão usam os IDs de `personagem-catalogo.json`.
- **Estado de certeza** (enum): `confirmado`, `alegado`, `suspeita`, `rumor`, `inferência`, `desconhecido`.
- **Camada** (enum, M5.2): `publico`, `local`, `restrito`, `secreto`.
- **`reino`** é campo de todo registro (ID do reino). Em Aeltharyn, a conversão acrescenta `kingdom.aeltharyn`.
- **`fonte`** é obrigatório em todo registro: documento e seção, ou `proposto` (conteúdo ainda sem aprovação do autor).
- **IDs aposentados:** `ids_aposentados.txt` (nível mundo) lista IDs removidos; nenhum pode voltar.
- **`esquema`**: primeira linha de comentário de cada arquivo, `# esquema=1`. Mudança que quebra campo sobe o número.

## 3. Entidades principais

Legenda: **O** obrigatório, **F** formato, **→** referência.

### 3.1 `reino.txt`
`id` O · `nome` O · `mundo` O · `tema` O · `estado` O (`bloqueado`, `em_composicao`, `fechado`) · `capital` O → city · `calendario` O → calendario · `fonte` O

### 3.2 `cidade.txt`
`id` · `reino` → · `nome` · `tipo` · `populacao_faixa` (faixa de uso, nunca soma) · `localizacao` · `governo` (cargo e forma de escolha) · `governante` → npc · `economia` · `defesas` (inclui vulnerabilidades) · `locais` · `problemas` · `faccoes` → lista · `rumores` (cada um marcado `rumor`) · `lenda` · `funcao` · `povos` → catálogo (lista) · `acesso` (`navegavel`, `terrestre`, `ambos`, `?`) · `recurso_chave` → lista · `fonte`. Todos **O**, exceto `lenda` e `rumores`.

### 3.3 `assentamento.txt`
`id` · `reino` · `cidade_mae` → city · `nome` · `tipo` · `populacao_faixa` · `lideranca` → npc · `obrigacao` · `interesse_local` · `tensao` · `acesso` (duração `?` até documentada) · `fonte`. Todos **O**. A cidade-mãe é vínculo político, não posição espacial.

### 3.4 `npc.txt`
`id` · `reino` · `nome` · `idade` (número, faixa ou `?`) · `povo` → catálogo · `subraca` → catálogo ou `—` · `classe` → catálogo · `profissao` → catálogo · `cidade` → city ou `—` · `faccao` → faction ou `—` · `cargo` · `papeis` (lista: `governante`, `herdeiro`, `heroi`, `general`, `guardiao`, `benfeitor`, `conselheiro`, `diplomata`, `religioso`, `chefe_faccao`, `outro`) · `principal` (`sim`, `não`) · `vivo` (`sim`, `não`, `desaparecido`, `exilado`) · `personalidade` · `objetivo` · `limites` · `capacidades` · `agenda` · `pressao` · `tom` (voz, M3.4) · `resumo` (≤ 500 caracteres, M5.1) · `fonte`. **O:** id, reino, nome, papeis, principal, vivo, fonte; **ficha completa** (todos os campos preenchidos ou `?`) para NPCs `principal=sim`; NPC secundário pode ter só os **O** e `resumo`.

### 3.5 `faccao.txt`
`id` · `reino` · `nome` · `lideranca` → npc · `objetivo` · `recursos` · `metodos` · `tensao` · `aliados` → faction (lista) · `rivais` → faction (lista) · `presenca_cidades` → lista · `presenca_assentamentos` → lista · `fonte`. Aliado e rival ao mesmo tempo é erro (M6.2).

### 3.6 `rota.txt`
`id` · `reino` · `de` → city/settlement · `para` → city/settlement · `meio` (`estrada`, `rio`, `mar`, `trilha`, `misto`) · `duracao_min` · `duracao_max` (número ou `?`) · `unidade` (`dia`) · `estado` (`documentada`, `com_ressalva`, `desconhecida`) · `condicao` (o que altera o tempo) · `fonte`. Duração `?` fica `?`: nunca se preenche para fechar o grafo. Rota entre reinos fica em arquivo de nível mundo (M-07), com o mesmo formato.

### 3.7 `relogio.txt`
`id` · `reino` · `nome` · `segmentos` (número) · `progresso` (número ≤ segmentos) · `move` · `trava` · `paga` · `completa` · `inacao` · `entidades` → lista · `fonte`. Todos **O**. O contador é estado do mundo: camada `secreto`.

### 3.8 `recurso.txt`
Chave composta `recurso` + `localidade` → city/settlement. Campos: `papel` (`produtor`, `consumidor`, `transportador`, `?`) · `producao` · `reserva` · `demanda` (escala ordinal 0 a 5 ou `?`) · `estado_evidencia` · `nota` · `fonte`. Nunca número inventado: estimativa provisória leva `(provisório, ±1)` e fonte da decisão.

## 4. Entidades de apoio

Mantêm as colunas dos CSVs de Aeltharyn (nome do campo igual), mais `reino` e, quando faltar, `fonte`.

| Arquivo | Chave | Campos (resumo) | Origem em Aeltharyn |
|---|---|---|---|
| `relacao.txt` | `origem` + `destino` + `tipo` | `destino_texto`, `descricao`, `fonte`; tipo em lista fechada | `12-relacoes.csv` |
| `fato.txt` | `id` fact. | `entidade`, `fato`, `camada`, `estado`, `quem_sabe`, `evidencia`, `caminho`, `como_descobrir`, `marcador` (trecho distintivo do fato, obrigatório se restrito ou secreto; usado só pelo validador), `fonte` | `14-camadas-de-informacao.csv` (o `marcador` é preenchido na conversão) |
| `evento.txt` | `id` event. | `nome`, `gatilho`, `relogio`, `cadeia`, `cidades_e_assentamentos`, `recursos`, `pessoas_e_faccoes`, `rotas`, `fonte` | `12-eventos.csv` |
| `semente.txt` | `id` seed. | `entidade`, `porte` (P, M, G), `ameaca` (1 a 4), `titulo`, `gancho`, `agentes`, `abordagens_possiveis`, `se_ninguem_age`, `relogio_evento`, `fonte` | `13-sementes-de-aventura.csv` |
| `misterio.txt` | `id` mist. | `nome`, `fatos_confirmados`, `hipoteses_abertas` (2 ou mais), `a_verdade_nao_pode_ser`, `evidencia_para_resolver`, `entidades`, `relogio_evento`, `fonte` | `13-misterios-limites.csv` |
| `resumo.txt` | `id` de qualquer entidade | `tipo`, `nome`, `resumo` (≤ 500), `vizinhanca` (≤ 300), `camada_max`, `fonte` | `14-resumos-de-contexto.csv` |
| `reputacao.txt` | `id` rep. | `entidade`, `ganha_disposicao`, `perde_disposicao`, `quem_reage`, `canais`, `fonte` | `13-reputacao.csv` |
| `canal.txt` | `id` canal. | `nome`, `funcao`, `o_que_o_canone_diz`, `limite`, `entidades`, `fonte` | `13-canais.csv` |
| `calendario.txt` | `id` | `meses` (n), `dias_por_mes`, `dias_de_transicao`, `estacoes` (lista), `ano_inicial`, `estacao_inicial`, `fonte` | doc 01 §35 |
| `povo_no_reino.txt` | `povo` + `reino` | `relacao` (`originário`, `linhagem`, `aliado`, `vizinho`, `migrante`), `cidades` → lista, `nota`, `fonte` | doc 10, `MATRIZ_POVOS_POR_REINO.md` (M6.3 achou a lacuna) |
| `cena.txt` | `id` cena. | `titulo`, `entra_ids`, `caracteres` (≤ 2.400), `nao_entra`, `fonte` | `15-cenas-de-teste.csv` |

Campos de lista com texto livre longo (`frases_modelo` de voz) usam `;;` entre itens, não `|`.

## 5. Molde e deltas

- **Molde** = os arquivos acima: imutáveis, versionados com o mundo. **Delta** = evento de mudança guardado pela campanha, nunca editando o molde (decisão em `INTEGRACAO_FUTURA_RPG_SOLO.md`).
- Campos do delta (doc 02 §2): `id`, `campanha`, `momento` (data do mundo), `entidade`, `estado_anterior`, `estado_novo`, `causa`, `agentes`, `quem_sabe` (+ meio), `certeza` (enum de certeza), `origem` (`ORIGINAL`, `DIVERGENCE`, `AI_CREATED`).
- Entidade nova criada por proposta nasce com `fonte=proposto` e `origem=AI_CREATED`; só vira molde com aprovação do autor (contrato do doc 06 §11.1).
- O conhecimento do personagem é guardado à parte do molde; descobrir um fato não muda a camada dele.

## 6. O que o esquema não cobre

- Regras de validação (IDs únicos, referências, mínimos, rotas e portos): `VALIDADOR_DE_REINO.md` (M6.2).
- Mapa campo a campo do catálogo Kotlin: **M6.3**.
- Carregador, banco e busca: fora do escopo editorial.
- Camada de mundo (M-01 a M-12): quando existir, ganha arquivos próprios no mesmo formato.
