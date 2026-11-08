# 14 — Camadas de informação (R2-6.2)

**Estado:** rascunho de R2-6.2 para aprovação do autor (D-55) · **Data:** 2026-11-05

**Etiqueta:** tudo é **[PROPOSTO]**; classificação é leitura editorial, não lore novo. **Fonte:** doc 04 §2.2, §4.3, §10 (alcance de ata e arquivos); doc 06 §6 e §11; doc 01 parte 4; `12-conhecimento.csv`, `13-misterios-limites.csv`, `12-relogios.csv`, `12-fichas.md`. Dados em `14-camadas-de-informacao.csv` (prefixo `fact.`). Molde: `reinos/01-.../14-camadas-de-informacao.md`; coluna `marcador` pelo `ESQUEMA_DE_DADOS.md` (V-70 e V-72).

## Regras

- **Quatro camadas por fato:** público, local, restrito, secreto. A camada diz **quem pode saber sem esforço especial**; não diz se o fato é verdadeiro (a cópia de uma ata não a torna verdadeira, doc 04 §10).
- **41 fatos:** 12 públicos, 10 locais, 5 restritos, 14 secretos. Entram os fatos *confirmado* e *suspeita* de `12-conhecimento.csv` dos 12 principais (24), os fatos confirmados ou alegados dos seis mistérios (10) e os 7 relógios. Fatos *desconhecido* não entram (nada a proteger); rumor também não.
- **Critério (ASSUMINDO, mesmo do reino 1):** **secreto** = o texto diz que o titular esconde, omite, protege ou altera, ou é estado real que ninguém detém (relógios); **restrito** = o titular guarda o fato em registro, cópia, peça ou saber que só cede por autorização, favor ou ordem; **local** = a comunidade ou o registro local sabe; **público** = visível a qualquer pessoa na região.
- **Segredo nunca sai por narração espontânea** (doc 06 §11): fato restrito ou secreto só entra na cena depois que o personagem cumpre um caminho da coluna `caminho`. O Mestre sabe; o personagem só sabe o que descobriu.
- **Evidência não documentada** aparece como lacuna explícita (Vireo, Maur); nenhuma prova foi inventada. Ata, cópia, peça e galeria seguem sem conteúdo, local ou autoria além do que os docs dizem.
- **Nada de lore novo:** nenhum NPC, facção, rota, número ou fato. Nada afirmado sobre Aeltharyn; nenhum fato toca o Mistério D.
- Fatos de lideranças de assentamento (papel), lendas e leis ainda não têm camada; entram quando uma cena (R2-6.3) os usar.

## Camadas e acesso

| Camada | Quem sabe sem esforço | Caminho mínimo para descobrir | Pode aparecer em resumo? |
|---|---|---|---|
| **Público** | qualquer pessoa na região | ouvir, estar no local, perguntar | sim |
| **Local** | a comunidade e quem mantém o registro local | presença, perguntar ao titular, registro local, testemunho | sim (camada máxima dos resumos) |
| **Restrito** | só o titular e quem ele autorizar | autorização de arquivo, favor, ordem legal ou exame da evidência (ASSUMINDO: um caminho basta) | **não** |
| **Secreto** | só o titular (ninguém, nos relógios) | evidência independente **mais** confiança ou confronto; o titular não confessa só por ser perguntado (ASSUMINDO) | **não** |

## Caminhos (coluna `caminho`)

`ouvir` rumor ou conversa · `presenca` estar no local · `perguntar` ao titular · `registro-local` atas, contas, livros · `arquivo-autorizado` arquivo restrito com autorização (Arquivo das Coroas) · `ordem-legal` decisão formal · `favor` confiança, dívida ou troca · `inspecao` examinar amostra, peça ou terreno · `comparacao` cruzar duas fontes · `testemunho` quem viu ou viveu.

## Resumos e camadas

- `14-resumos-de-contexto.csv`: `camada_max` **definitiva**: `local` para 89 resumos (cidades, assentamentos, facções, pessoas) e `secreto` para os 7 relógios (uso só de Mestre e motor).
- **V-72:** nenhum `marcador` de fato restrito ou secreto aparece em resumo de camada `local` (0 ocorrência). A coluna `marcador` é só do validador; não vai ao contexto.
- Fichas: o campo *Camadas de informação* de N-01 a N-12 lista os `fact.` da pessoa (`12-fichas.md` e `.csv`).

## Subida de camada

Descobrir um fato muda o **conhecimento do personagem**, não a camada do fato. Divulgar um fato restrito ou secreto, por jogador ou NPC, é evento do mundo e exige registro; a camada só muda por esse registro (ASSUMINDO). Revelar ao Mestre não revela ao jogador.

## Fatos por camada

### Público (12)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.velka-preside-selo` | preside o Concílio neste ciclo e guarda o selo | confirmado | `npc.velka-selo-firme` | ouvir; presenca | qualquer pessoa nas cidades comenta; ir ao Concílio | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.maro-guarda-arquivo` | guarda o Arquivo das Coroas | confirmado | `npc.maro-tinta-velha` | ouvir; presenca | ir ao Arquivo ou ouvir qualquer escriba | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.thaurak-comanda-vigias` | comanda os vigias do Portão-Cinza | confirmado | `npc.thaurak-escama-fria` | ouvir; presenca | ir ao Portão-Cinza ou ouvir os vigias | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.gundrik-comanda-guarda` | comanda a guarda de Brasalume e de Veio-Fundo | confirmado | `npc.gundrik-muralha-lenta` | ouvir; presenca | ir a Brasalume ou a Veio-Fundo | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.karna-fornalha-mae` | mantém a Fornalha-Mãe | confirmado | `npc.karna-brasa-mansa` | ouvir; presenca | ir a Brasalume ou ouvir os forjadores | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.vireo-caravana-nevasca` | salvou uma caravana na Escadaria Cega em uma nevasca | confirmado | `npc.vireo-vento-rapido` | ouvir; testemunho | ouvir os mensageiros ou quem estava na caravana | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.zhae-guia-passos` | guia viajantes pelos passos de Altavento | confirmado | `npc.zhae-garra-de-nevoa` | ouvir; presenca | perguntar em Altavento | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.maur-segurou-portico` | segurou o Pórtico durante um desabamento | confirmado | `npc.maur-camada-velha` | ouvir; presenca | ouvir a cidade; Maur só fala em particular (doc 01) | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.primeira-peca-mais-antigo` | é o construto desperto mais antigo conhecido | confirmado | `npc.primeira-peca` | ouvir; presenca | perguntar na cidade ou aos Despertos | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.nael-preside-guias` | preside a Casa dos Guias | confirmado | `npc.nael-curva-antiga` | ouvir; presenca | ir à Casa dos Guias ou ouvir os guias | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.mp-01-degelo-mudou` | o degelo mudou de data | confirmado | — | ouvir; presenca | qualquer pessoa a jusante nota; a causa segue desconhecida | 13-misterios-limites.csv; doc 01 parte 4 (MP-01) |
| `fact.mp-03-direitos-despertos` | a cidade não definiu os direitos dos Despertos (lacuna intencional) | confirmado | — | ouvir; presenca | qualquer pessoa na cidade sabe; os Despertos pedem direitos que a cidade não definiu | 13-misterios-limites.csv; doc 01 parte 4 (MP-03) |

### Local (10)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.pelma-registra-comportas` | registra cada abertura de comporta | confirmado | `npc.pelma-gota-medida` | registro-local; perguntar | ler o registro das comportas ou perguntar a Pelma | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.aurea-financia-lentes` | financia a manutenção das lentes | confirmado | `npc.aurea-faceta-longa` | perguntar; presenca | perguntar a Áurea ou a quem cuida das lentes | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.zhae-expulsa-cacadores` | Zhae foi expulsa de um grupo de caçadores; o motivo é desconhecido | suspeita | `npc.zhae-garra-de-nevoa` | perguntar; testemunho | perguntar a Zhae ou a caçadores de Altavento; o motivo segue desconhecido | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.mp-01-registro-nao-bate` | o registro das comportas tem anotações que não batem entre si | confirmado | `npc.pelma-gota-medida` | registro-local; comparacao | ler o livro de registro da Casa das Comportas e comparar as anotações | 13-misterios-limites.csv; doc 01 parte 4 (MP-01) |
| `fact.mp-02-viajantes-sem-registro` | há mais viajantes sem registro do que antes | confirmado | `npc.varkhan-escama-vigia` | registro-local; presenca; testemunho | ler o Livro de Passagens, cruzar com tarifas pagas e ouvir caravaneiros | 13-misterios-limites.csv; doc 01 parte 4 (MP-02) |
| `fact.mp-02-paginas-faltam` | faltam páginas no Livro de Passagens | confirmado | `npc.varkhan-escama-vigia` | registro-local; comparacao | comparar o Livro com as cópias das casas de ofício; página arrancada é delito de custódia (L-04) | 13-misterios-limites.csv; doc 01 parte 4 (MP-02) |
| `fact.mp-03-pecas-somem` | peças raras somem e cada oficina culpa outra | confirmado | `npc.fenwick-mola-solta` | presenca; perguntar; registro-local | perguntar nas oficinas e ler os registros de oficina | 13-misterios-limites.csv; doc 01 parte 4 (MP-03) |
| `fact.mp-05-registro-descida-taxas` | o Registro da Descida e as taxas não batem; os dois registros valem até conferência (L-09) | confirmado | `npc.torgrim-corda-dupla`; `npc.seis-eixos` | registro-local; comparacao; testemunho | conferir o Registro da Descida com as taxas pagas, a cordoaria e as testemunhas do cais | 13-misterios-limites.csv; doc 01 parte 4 (MP-05) |
| `fact.mp-06-galerias-desorientam` | as galerias novas desorientam os guias | confirmado | `npc.ilmeth-curva-segura` | presenca; testemunho; perguntar | ouvir guias e ver as galerias; ainda se desconhece onde elas levam | 13-misterios-limites.csv; doc 01 parte 4 (MP-06) |
| `fact.mp-06-mapas-rivais` | mapas rivais circulam e nenhum mapa é fonte final (L-05) | confirmado | `npc.pip-vapor-curto` | ouvir; registro-local; comparacao | comparar mapas rivais com as cópias da Rotunda dos Mapas | 13-misterios-limites.csv; doc 01 parte 4 (MP-06) |

### Restrito (5)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.maro-pacto-copiado` | Maro copiou um pacto que as cidades juram nunca ter assinado | suspeita | `npc.maro-tinta-velha` | arquivo-autorizado; favor; comparacao | autorização do Arquivo ou confiança de Maro; a cópia só cede à cadeia de custódia (L-08); ata fechada só abre por matéria reservada | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.gundrik-saida-ventilacao` | Gundrik guarda uma saída de ventilação que não consta nos mapas oficiais | suspeita | `npc.gundrik-muralha-lenta` | favor; inspecao | confiança de Gundrik ou inspeção das galerias; mapas oficiais não a mostram | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.primeira-peca-peca-sem-nome` | Primeira-Peça guarda uma peça que ninguém soube identificar | suspeita | `npc.primeira-peca` | favor; inspecao | confiança do construto (que pode não responder, MP-03) ou exame da peça | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.nael-galeria-fora-dos-mapas` | Nael sabe de uma galeria que não consta em nenhum mapa | suspeita | `npc.nael-curva-antiga` | favor; comparacao; testemunho | confiança de Nael, ou comparar as cópias da Rotunda dos Mapas com marcas de ferramenta e relatos de guias; onde a galeria vai dar segue desconhecido | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.mp-04-tres-copias-ata` | existem três cópias divergentes da ata do assento e nenhuma é confirmada | confirmado | `npc.maro-tinta-velha`; `npc.haldra-tinta-funda` | arquivo-autorizado; comparacao; ordem-legal | autorização do Arquivo para comparar as cópias (cópias divergentes entram todas, doc 04 §2.2) ou ordem legal; ter a cópia não a torna verdadeira | 13-misterios-limites.csv; doc 01 parte 4 (MP-04) |

### Secreto (14)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.velka-ata-escondida` | Velka esconde uma ata que ela mesma não quer ler em voz alta | suspeita | `npc.velka-selo-firme` | comparacao; testemunho; favor | comparar as cópias da ata com a cadeia de custódia, ou ganhar a confiança de Velka; ela não confessa só por ser perguntada | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.thaurak-protege-quem-arrancou` | Thaurak sabe quem arrancou páginas do Livro de Passagens e protege essa pessoa | suspeita | `npc.thaurak-escama-fria` | comparacao; testemunho; favor | cruzar as cópias do Livro com a escala dos vigias, ou ganhar a confiança de Thaurak; ele não entrega a pessoa só por ser perguntado | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.karna-liga-que-nao-esfria` | Karna esconde uma liga que não esfria | suspeita | `npc.karna-brasa-mansa` | inspecao; favor; comparacao | examinar a liga ou ganhar a confiança de Karna; ela não a mostra só por ser perguntada | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.vireo-queda` | Vireo esconde uma queda que quase o tirou do ar | suspeita | `npc.vireo-vento-rapido` | testemunho; favor | testemunho de quem viu a queda ou confiança de Vireo; evidência não documentada (ASSUMINDO: um relato independente mais confiança) | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.pelma-registro-alterado` | Pelma alterou um registro para evitar pânico | suspeita | `npc.pelma-gota-medida` | inspecao; comparacao; testemunho; favor | comparar o livro de registro com as comportas e ouvir medidores, ou ganhar a confiança de Pelma; alterar registro é falta grave (L-02), então ela não confessa só por ser perguntada | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.maur-idade-real` | Maur é mais velho do que a cidade conta | suspeita | `npc.maur-camada-velha` | comparacao; testemunho; favor | comparar relatos antigos da cidade e ganhar a confiança de Maur, que só fala em particular; evidência não documentada (ASSUMINDO) | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.aurea-faceta-rostos` | Áurea esconde uma faceta que mostra rostos a quem olha | suspeita | `npc.aurea-faceta-longa` | inspecao; favor; comparacao | examinar a faceta ou ganhar a confiança de Áurea; ela não a mostra só por ser perguntada | 12-conhecimento.csv; 12-fichas.md (R2-4.1) |
| `fact.relogio-combustivel` | estado real e causas de “Combustível de Brasalume”: progresso inicial 3/6; o que move: fornalhas gastando mais do que a cidade consegue repor; disputa pelo turno do fogo principal (L-03) | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 06; doc 02 §4 |
| `fact.relogio-comportas` | estado real e causas de “Comportas e degelo de Veiaclara”: progresso inicial 2/6; o que move: degelo fora de data; cidades a jusante pedindo água sem pagar manutenção; registro alterado (MP-01) | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 06; doc 02 §4 |
| `fact.relogio-cais` | estado real e causas de “Cais saturado e registros em conflito”: progresso inicial 3/6; o que move: mais barcas do que o cais comporta; carga e taxa não batem; regras sobre construtos de carga | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 06; doc 02 §4 |
| `fact.relogio-passagens` | estado real e causas de “Livro de Passagens e viajantes sem registro”: progresso inicial 2/6; o que move: mais viajantes sem registro; páginas arrancadas (MP-02); tarifas em disputa | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 06; doc 02 §4 |
| `fact.relogio-assento` | estado real e causas de “Fim do ciclo do assento”: progresso inicial 2/6; o que move: o ciclo de Velka Selo-Firme termina sem sucessor combinado; cada cidade quer o assento por primeiro; cópias da ata em disputa (MP-04) | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 06; doc 02 §4 |
| `fact.relogio-pecas` | estado real e causas de “Peças raras e foro dos Despertos”: progresso inicial 2/6; o que move: peças raras somem e cada oficina culpa outra; construtos antigos pedem direitos que a cidade não definiu (MP-03) | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 06; doc 02 §4 |
| `fact.relogio-passos` | estado real e causas de “Nevasca e passos fechados”: progresso inicial 1/6; o que move: nevasca que fecha os passos; mensageiros que recusam voar quando o vento muda | confirmado | — | inspecao; comparacao; testemunho | ninguém detém o contador; o jogador percebe só os sinais (o que trava e o que falha) e deduz causas por evidência | 12-relogios.csv; doc 06; doc 02 §4 |

## Limites

- A camada é leitura editorial; se o autor discordar, muda-se a linha, não o lore.
- Suspeita de ficha continua suspeita: estar em camada restrita ou secreta não a confirma.
- O motor decide como guarda o conhecimento do personagem; aqui ficam fato, camada e caminho.
