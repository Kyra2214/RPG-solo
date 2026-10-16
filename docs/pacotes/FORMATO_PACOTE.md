# Formato do pacote de regras

Um pacote é uma pasta (ou `.zip`) de arquivos `.txt`. Campos separados por `|`; linhas com `#` e linhas vazias são ignoradas. Ids: `a-z`, `0-9`, `-`, `_`. `-` ou vazio = "não existe".

| Arquivo | Campos | Obrigatório |
|---|---|---|
| `manifest.txt` | `chave|valor`: `id`, `sistema` (família), `edicao`, `versao`, `origem`, `licenca`, `licenca_status` (VERIFICADA/INDICADA/PENDENTE), `tipo` (TESTE/REAL); opcionais `estende` (id do pacote-base), `morte` (recurso que mata) | sim |
| `atributos.txt` | `id|nome|min|max|padrao` | sim |
| `recursos.txt` | `id|nome|base|atributo|mult` (máximo = base + mult × atributo) | não |
| `tracos.txt` | `id|tipo|nome|custo|requisitos|descricao`; tipo: vantagem, desvantagem, pericia, poder; requisitos: `atributo>=N`, `trait:id` (exige) e/ou `not:id` (não combina, nos dois sentidos) separados por `;` | não |
| `testes.txt` | `id|nome|dados|lados|atributo|mult|comparacao|parcial|crit_sucesso|crit_falha` (comparacao: ROLL_OVER/ROLL_UNDER; críticos `a..b`) | não |
| `dificuldades.txt` | `id|nome|valor` | não |
| `condicoes.txt` | `id|nome|descricao` | não |
| `dano.txt` | `id|nome|dados|lados|atributo_bonus` | não |
| `criacao.txt` | `chave|valor`: `pontos` (obrigatório no pacote resolvido), `custo_atributo` (padrão 1), `limite_desvantagens` (teto de pontos devolvidos por desvantagens; opcional) | sim (via herança ou próprio) |
| `progressao.txt` | `nivel|xp total` (níveis 2, 3, 4… sem buracos, XP crescente) | não |
| `incerto.txt` | `id|nota` — regra ambígua; gera aviso, nunca é apresentada como oficial | não |
| `tutorial.txt` | `ordem|titulo|texto` | não |
| `exemplos.txt` | `id|titulo|atributo=valor,...|traco,traco` — precisam passar nas regras do próprio pacote | não |

## Herança (`estende`)
Só dentro da mesma família (`sistema`). O filho sobrescreve o pai por id; arquivos ausentes herdam tudo. `criacao.txt` mescla por chave; `progressao.txt`, se existir, substitui a do pai. Remover entradas do pai não é suportado.

## Licença
`licenca` e `licenca_status` são só informação (mostradas na Biblioteca de sistemas). **Não bloqueiam a instalação.** Pacote `REAL` só precisa de uma edição existente no catálogo, da mesma família. Pacote `TESTE` usa só mecânica própria.

## Custo de criação
Cada ponto de atributo acima do `min` custa `custo_atributo`; traços somam seu `custo` (negativo devolve pontos); total ≤ `pontos`.

## Pacote de um sistema comercial (ex.: 3D&T)
O jogador pode importar o pacote por pasta ou `.zip`. Prefira escrever as `descricao` com palavras próprias, e use o PDF do sistema como guia/consulta (ver `DocumentIndex`), não como cópia dentro do app.
