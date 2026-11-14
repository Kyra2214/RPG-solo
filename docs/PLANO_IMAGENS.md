# Plano de imagens (M12)

Estado em 14/11/2026: não há imagem de IA. Existem os 274 prompts, o manifesto e 274 ícones SVG procedurais provisórios em `imagens/` (gerados por `tools/gerar_imagens.py`; abra `imagens/previa.html` para ver). Os SVG ainda não estão ligados ao app. O app funciona sem arte; o glifo no medalhão do `Bloco` (CG-0.5) é o fallback. Este plano não altera mecânica.

## 1. Inventário
| Grupo | Qtde |
|---|---|
| Raças | 50 |
| Sub-raças | 50 |
| Classes | 50 |
| Profissões | 104 |
| Híbridos nomeados | 20 |
| **Total de ícones de catálogo** | **274** |

Fora do catálogo: reinos e cidades de nascimento, cartões de mundo e heróis prontos (M5/M6), retrato do personagem (M8), cenas de campanha.

## 2. Níveis de arte
1. **Ícone de bloco** (274): medalhão de 36 dp. Prioridade 1; o `Bloco` já tem o lugar.
2. **Ilustração de detalhe** (raça, classe, profissão): em "Ver detalhes". Prioridade 2.
3. **Retrato do herói e cenas**: sob demanda, por IA, no jogo. Prioridade 3.

## 3. Guia de estilo (fixar antes de gerar)
- Um estilo único: pergaminho/gravura quente, combinando com o dourado e o cobre do tema.
- Fundo transparente ou neutro, enquadramento centrado, silhueta legível em 36 dp.
- Prompt-base fixo mais um trecho variável por entrada.
- Sem texto na imagem, sem personagens ou marcas de terceiros (o cânone evita termos de terceiros).
- Raça não determina aparência: preferir símbolos e ambiente a rostos.

## 4. Pipeline
1. Prompt por entrada, montado de `name`, `summary` e subgrupo (classes, profissões) ou reino (raças), salvo em arquivo versionado.
2. Geração em lote, fora do app, para os 274 ícones.
3. Revisão manual: aprovar, rejeitar ou refazer cada imagem, registrada no manifesto.
4. Empacotamento em WebP (128 px ícone, 512 px detalhe), em `assets`, nome igual ao `id`.
5. No app: se existir arquivo para o `id`, mostra a imagem no medalhão; senão, mantém o glifo.

## 5. Manifesto (`imagens-manifesto.json`)
Um registro por imagem:
```json
{
  "id": "elfo",
  "grupo": "raca",
  "nivel": "icone",
  "arquivo": "icones/raca/elfo.webp",
  "versao": 1,
  "prompt": "<texto final usado>",
  "estilo": "v1",
  "status": "aprovada",
  "geradaEm": "AAAA-MM-DD"
}
```
- `grupo`: `raca`, `subraca`, `classe`, `profissao`, `hibrido`, `reino`, `cidade`, `heroi`.
- `nivel`: `icone`, `detalhe`, `retrato`, `cena`.
- `status`: `pendente`, `gerada`, `aprovada`, `rejeitada`.
- O app só carrega imagens `aprovada`.
- Mudou o prompt ou o estilo: sobe `versao` e volta a `pendente`.

## 6. Imagens por IA em jogo (retratos e cenas)
- Só quando o jogador pedir.
- Cache local, chave `id` + hash do prompt; a mesma imagem não é gerada duas vezes.
- Limite de tamanho do cache, com remoção da mais antiga.
- Offline ou falha: glifo ou retrato padrão.
- Provedor de imagem: **a decidir**. Chave e provedor de texto já existem em Config.

## 7. Fases
1. **Estilo**: guia, prompt-base e 5 provas (uma de cada grupo); aprovar antes de seguir.
2. **Lote 1**: 50 raças + 50 classes.
3. **Lote 2**: 104 profissões + 50 sub-raças + 20 híbridos.
4. **Código**: manifesto, carregamento do asset no `Bloco`, fallback por glifo.
5. **Detalhe**: ilustrações nos painéis.
6. **Em jogo**: retrato e cenas por IA, com cache.

## 8. Riscos e decisões abertas
- Custo e tempo de 274 ícones mais refações: definir orçamento.
- Coerência de estilo: causa comum de retrabalho; por isso a fase 1 vem antes.
- Tamanho do app: 274 WebP pequenos, poucos MB.
- Direitos de uso: conferir os termos do provedor de imagem.
- Abertas: provedor, estilo visual e quem gera o lote (este ambiente não tem rede).

## 9. Entregue em `imagens/` (14/11/2026)
- `imagens-manifesto.json`: 274 registros, status `pendente` (arte de IA), com o campo extra `svg` apontando o ícone provisório.
- `prompts.csv`: grupo, id, nome, arquivo alvo e prompt final de cada ícone; `PROMPT_BASE.md`: o prompt-base do estilo v1.
- `icones/<grupo>/<id>.svg`: emblema provisório (anel, estrela de pontas e inicial do nome; cor por grupo, variação por id). Não é arte de IA e não foi conferido visualmente aqui, só validado como XML.
- Como usar com um gerador de imagens: rodar cada linha de `prompts.csv`, salvar em WebP 128 px com o nome de `arquivo_alvo` em `imagens/`, revisar, e mudar o `status` para `aprovada` no manifesto. Para refazer tudo: `python3 tools/gerar_imagens.py` (sobrescreve `imagens/`, inclusive o manifesto: faça isso antes de editar status).
- Ligar ao app (fase 4) continua pendente: o Compose não lê SVG sem biblioteca; ASSUMINDO conversão para WebP/vetor Android na hora de ligar.
