# Formato do pacote de regras

Um pacote é uma pasta (ou `.zip`) de arquivos `.txt`. Campos separados por `|`; linhas com `#` e linhas vazias são ignoradas. Ids: `a-z`, `0-9`, `-`, `_`. `-` ou vazio = "não existe".

| Arquivo | Campos | Obrigatório |
|---|---|---|
| `manifest.txt` | `chave|valor`: `id`, `sistema` (família), `edicao`, `versao`, `origem`, `licenca`, `licenca_status` (VERIFICADA/INDICADA/PENDENTE), `tipo` (TESTE/REAL); opcionais `estende` (id do pacote-base), `morte` (recurso que mata) | sim |
| `atributos.txt` | `id|nome|min|max|padrao` | sim |
| `recursos.txt` | `id|nome|base|atributo|mult` (máximo = base + mult × atributo) | não |
| `tracos.txt` | `id|tipo|nome|custo|requisitos|descricao[|bonus]` (bonus opcional: `escopo:+N` separados por `;`, ex.: `esquiva:+2;furtividade:-1`. Escopos de teste podem ser ids de teste, atributo ou perícia; escopos de combate suportados: `ataque`, `dano`, `defesa`, `resistencia_dano`. Resistência positiva reduz dano recebido; resistência negativa aumenta o dano. Só declare efeitos explícitos que sejam sempre aplicáveis; efeitos condicionais exigem suporte próprio e não devem ser inferidos da descrição); tipo: vantagem, desvantagem, pericia, poder; requisitos: `atributo>=N`, `trait:id` (exige) e/ou `not:id` (não combina, nos dois sentidos) separados por `;` | não |
| `testes.txt` | `id|nome|dados|lados|atributo|mult|comparacao|parcial|crit_sucesso|crit_falha[|pericia[|fortuna]]` (as colunas `pericia` e `fortuna` são opcionais; quando preenchida, `pericia` usa seus pontos no lugar de `atributo*mult`; `fortuna`: `NORMAL` (padrão), `ADVANTAGE` ou `DISADVANTAGE`; vantagem/desvantagem rola duas vezes e escolhe o melhor/pior resultado decidido pelo motor. Comparacao: ROLL_OVER/ROLL_UNDER/POINTS; críticos `a..b`; `POINTS` exige `1` dado real (lados 4, 6, 8, 10, 12 ou 20) e usa zero pontos como chance mínima de uma face e cada ponto positivo acrescenta uma face de sucesso, contando do topo até lados−1; bônus de traço e Ganho/Perda somam pontos; a dificuldade troca o dado — o dado do teste vale na média e sobe/desce na escada d4, d6, d8, d10, d12, d20) | não |
| `dificuldades.txt` | `id|nome|valor` | não |
| `condicoes.txt` | `id|nome|descricao[|efeito|duracao_rodadas|dano_por_rodada|recurso]` — efeito opcional: `ganho:<atributos>`, `perda:<atributos>` (ids separados por vírgula, `*` = todos) ou `-`. Ganho = +1 no bônus/limiar conforme a comparação do teste; Perda = -1 (se anulam dentro dos limites do motor; não alteram a quantidade de dados). Duração deve ser positiva; dano por rodada não pode ser negativo; o recurso deve existir no pacote. Campos finais são opcionais. | não |
| `dano.txt` | `id|nome|dados|lados|atributo_bonus` | não |
| `itens.txt` | `id|nome|tipo|slot|descricao[|efeitos]` — tipo: `equipamento`, `consumivel` ou `narrativo`; equipamento exige slot; efeitos de equipamento usam `ataque:+N`, `dano:+N`, `defesa:+N`. Itens consumíveis só executam ações declaradas pelo pacote em `criacao.txt`; itens narrativos não têm estatísticas. | não |
| `criacao.txt` | `chave|valor`: `pontos` (obrigatório no pacote resolvido), `custo_atributo` (padrão 1), `limite_desvantagens` (teto de pontos devolvidos por desvantagens; opcional), `pontos_por_nivel` (0–100 pontos de evolução concedidos por cada nível novo; padrão 0, então não há orçamento de evolução se o pacote não declarar) | sim (via herança ou próprio) |
| `progressao.txt` | `nivel|xp total` (níveis 2, 3, 4… sem buracos, XP crescente) | não |
| `opcoes-arquetipo.txt` | `arquetipo|vantagens|desvantagens` (ids separados por vírgula) — cardápio sugerido do arquétipo; cada item precisa existir, ter o tipo certo e ser alcançável com os atributos do arquétipo | não |
| `incerto.txt` | `id|nota` — regra ambígua; gera aviso, nunca é apresentada como oficial | não |
| `tutorial.txt` | `ordem|titulo|texto` | não |
| `exemplos.txt` | `id|titulo|atributo=valor,...|traco,traco` — precisam passar nas regras do próprio pacote | não |
| `arquetipos.txt` | `id|nome|descricao|atributo=valor,...|traco,traco` — ponto de partida editável da criação de ficha; precisa passar nas regras do pacote (pontos, limites, requisitos) | não |

## Herança (`estende`)
Só dentro da mesma família (`sistema`). O filho sobrescreve o pai por id; arquivos ausentes herdam tudo. `criacao.txt` mescla por chave; `progressao.txt`, se existir, substitui a do pai. Remover entradas do pai não é suportado.

## Licença
`licenca` e `licenca_status` são só informação (mostradas na Biblioteca de sistemas). **Não bloqueiam a instalação.** Pacote `REAL` só precisa de uma edição existente no catálogo, da mesma família. Pacote `TESTE` usa só mecânica própria.

## Equilíbrio vantagens x desvantagens (opcional)
Em `criacao.txt`: `equilibrio|1,3,5` (com N vantagens a ficha precisa de pelo menos tabela[N-1] desvantagens), `equilibrio_apos|dobrar` (depois do fim da tabela o mínimo dobra a cada vantagem: 4→10, 5→20; `manter` mantém o último valor) e `minimo_opcoes|3` (todo arquétipo precisa de pelo menos 3 vantagens e 3 desvantagens no `opcoes-arquetipo.txt`). Tipos `vantagem` e `poder` contam como vantagem; perícias não entram na conta. No sistema próprio esses traços têm custo 0: só atributos e perícias gastam pontos.

## Custo de criação
Cada ponto de atributo acima do `min` custa `custo_atributo`; traços somam seu `custo` (negativo devolve pontos); total ≤ `pontos`.

## Pacote de um sistema comercial (ex.: 3D&T)
O jogador pode importar o pacote por pasta ou `.zip`. Prefira escrever as `descricao` com palavras próprias, e use o PDF do sistema como guia/consulta (ver `DocumentIndex`), não como cópia dentro do app.
