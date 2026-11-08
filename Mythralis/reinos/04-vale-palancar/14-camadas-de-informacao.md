# 14 — Camadas de informação (R4-6.2)

**Estado:** rascunho de R4-6.2 para aprovação do autor (D-130) · **Data:** 2026-11-07

**Etiqueta:** tudo é **[PROPOSTO]**; classificação é leitura editorial, não lore novo. **Fonte:** doc 04 §3, §5 e §6; doc 06 §3 e §6; doc 01 parte 4; `12-conhecimento.csv`, `12-relogios.csv`, `12-fichas.md`, `14-resumos-de-contexto.csv`. Dados em `14-camadas-de-informacao.csv` (prefixo `fact.`). Molde: `reinos/03-veu-dos-malditos/14-camadas-de-informacao.md`; a coluna `marcador` é só do validador.

## Regras

- **Quatro camadas por fato:** público, local, restrito, secreto. A camada diz **quem pode saber sem esforço especial**; não diz se o fato é verdadeiro.
- **42 fatos:** 11 públicos, 11 locais, 6 restritos, 14 secretos. Entram os fatos *confirmado* e *suspeita* de `12-conhecimento.csv` dos 12 principais (24), um fato confirmado por mistério MPL-01 a MPL-10 (10) e os 8 relógios. Fatos *desconhecido* não entram (nada a proteger); rumor e hipótese também não.
- **Critério (ASSUMINDO, o mesmo dos reinos 1 a 3):** **secreto** = o texto diz que o titular esconde, omite, protege ou cala, ou é estado real que ninguém detém (relógios); **restrito** = o titular guarda o fato em registro, marca, recado ou saber que só cede por autorização, favor ou ordem; **local** = a comunidade ou o registro local sabe; **público** = visível a qualquer pessoa na região.
- **Segredo nunca sai por narração espontânea:** fato restrito ou secreto só entra na cena depois que o personagem cumpre um caminho da coluna `caminho`. O Mestre sabe; o personagem só sabe o que descobriu.
- **Evidência não documentada** aparece como lacuna (o que o titular aceitar dizer) ou como ASSUMINDO; nenhuma prova foi inventada.
- **Nada de lore novo:** nenhum NPC, facção, lugar, rota, número ou fato. Nada afirmado sobre Aeltharyn, as Coroas ou o Véu. Nenhum fato resolve mistério, lei ou lacuna (L-01 a L-10, o que a Descida traz).

## Camadas e acesso

| Camada | Quem sabe sem esforço | Caminho mínimo para descobrir | Pode aparecer em resumo? |
|---|---|---|---|
| **Público** | qualquer pessoa na região | ouvir, estar no local, perguntar | sim |
| **Local** | a comunidade e quem mantém o registro local | presença, perguntar ao titular, registro local, testemunho | sim (camada máxima dos resumos) |
| **Restrito** | só o titular e quem ele autorizar | favor, ordem legal (da Assembleia), registro do titular ou exame da evidência (ASSUMINDO: um caminho basta) | **não** |
| **Secreto** | só o titular (ninguém, nos relógios) | evidência independente **mais** confiança ou confronto; o titular não confessa só por ser perguntado (ASSUMINDO) | **não** |

## Caminhos (coluna `caminho`)

`ouvir` rumor ou conversa · `presenca` estar no local · `perguntar` ao titular · `registro-local` marcas, acertos, recados, turnos · `ordem-legal` decisão formal da Assembleia · `favor` confiança, dívida ou troca · `inspecao` examinar câmara, cisterna ou terreno · `comparacao` cruzar duas fontes · `testemunho` quem viu ou viveu.

## Resumos e camadas

- `14-resumos-de-contexto.csv`: `camada_max` **definitiva**: `local` para 47 resumos (cidades, facções e pessoas) e `secreto` para os 8 relógios (uso só de Mestre e motor).
- Nenhum `marcador` de fato restrito ou secreto aparece em resumo de camada `local` (0 ocorrência). A coluna `marcador` não vai ao contexto.
- Fichas: o campo *Camadas de informação* de N-01 a N-12 lista os `fact.` da pessoa (`12-fichas.md` e `.csv`).

## Subida de camada

Descobrir um fato muda o **conhecimento do personagem**, não a camada do fato. Divulgar um fato restrito ou secreto, por jogador ou NPC, é evento do mundo e exige registro; a camada só muda por esse registro (ASSUMINDO). Revelar ao Mestre não revela ao jogador.

## Fatos por camada

### Público (11)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.kharos-capitao-vazante` | É o Capitão de Marcha e reside na Vazante | confirmado | `npc.kharos-casco-de-marcha` | ouvir; presenca | ir à Vazante ou ouvir os Guardas da Marcha | 12-fichas.md (R4-4.1) |
| `fact.yorna-cuida-ordem-reuniao` | Cuida da ordem da reunião | confirmado | `npc.yorna-palavra-firme` | ouvir; presenca | ir ao Terreiro-Maior ou ouvir a Assembleia | 12-fichas.md (R4-4.1) |
| `fact.gruzk-comanda-turnos-cisterna` | Comanda os turnos de vigia da cisterna | confirmado | `npc.gruzk-estandarte-baixo` | ouvir; presenca | ir a Alvasal ou ouvir os Vigias da Cisterna | 12-fichas.md (R4-4.1) |
| `fact.zinu-chefia-grupo-cobra` | Chefia um dos grupos que cobram | confirmado | `npc.zinu-troca-justa` | ouvir; presenca | ir a Rasovau ou ouvir quem atravessa | 12-fichas.md (R4-4.1) |
| `fact.cassan-hospeda-visitantes` | Hospeda clãs de fora no Terreiro | confirmado | `npc.cassan-galhada-velha` | ouvir; presenca | ir ao Terreiro-Maior ou ouvir os clãs de fora | 12-fichas.md (R4-4.1) |
| `fact.mpl-02-celeiro-guarda-reserva` | o celeiro guarda grão e carne seca para os anos ruins | confirmado | ninguém | ouvir; presenca | ir a Tabuleiro-Velho | doc 01 parte 4 (MPL-02); doc 05 |
| `fact.mpl-03-dois-grupos-um-vau` | dois grupos cobram pela mesma passagem; o vau não se fecha | confirmado | ninguém | ouvir; presenca | ir a Rasovau ou ouvir quem atravessa | doc 01 parte 4 (MPL-03); doc 05 (L-04) |
| `fact.mpl-05-uma-so-cisterna` | Alvasal depende de uma só cisterna, que não serve a todas as caravanas ao mesmo tempo | confirmado | ninguém | ouvir; presenca | ir a Alvasal ou ouvir as caravanas | doc 01 parte 4 (MPL-05); doc 04 §10 |
| `fact.mpl-06-pedra-da-palavra` | a Pedra da Palavra dá a palavra na rodada; a reunião só se fecha quando todos foram ouvidos | confirmado | ninguém | ouvir; presenca | ir ao Terreiro-Maior ou ouvir a Assembleia | doc 01 parte 4 (MPL-06); doc 05 (L-01) |
| `fact.mpl-08-campo-a-cada-ciclo` | a Assembleia escolhe o campo do terreiro a cada ciclo, sempre ao alcance dos clãs em marcha | confirmado | ninguém | ouvir; presenca | ir ao Terreiro-Maior ou ouvir a Assembleia | doc 01 parte 4 (MPL-08); doc 05 (L-03) |
| `fact.mpl-09-guia-ate-o-pe` | o guia leva o rebanho até o pé das elevações e não além | confirmado | ninguém | ouvir; presenca | ir a Encosta-Aberta ou ouvir os Guias da Encosta | doc 01 parte 4 (MPL-09); doc 05 (L-09) |

### Local (11)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.moura-chefia-trato-celeiro` | Chefia o Trato do Celeiro | confirmado | `npc.moura-mel-guardado` | presenca; registro-local | ir a Tabuleiro-Velho ou ouvir os Guardiões da Reserva | 12-fichas.md (R4-4.1) |
| `fact.pocha-distribui-reserva-propria` | Distribui reserva própria a clãs que o celeiro não atende | confirmado | `npc.pocha-ninhada-farta` | ouvir; testemunho | ouvir os clãs atendidos ou a Mesa da Partilha | 12-fichas.md (R4-4.1) |
| `fact.tessun-guia-rebanhos-ao-pouso` | Guia rebanhos até o pouso | confirmado | `npc.tessun-salto-calado` | ouvir; presenca | ir a Encosta-Aberta ou ouvir os Guias da Encosta | 12-fichas.md (R4-4.1) |
| `fact.rukhan-bate-campos` | Bate os campos para a Marcha | confirmado | `npc.rukhan-retorno-certo` | ouvir; presenca | ir à Vazante ou ouvir os Guardas da Marcha | 12-fichas.md (R4-4.1) |
| `fact.quira-leva-recados` | Leva recados entre os grupos | confirmado | `npc.quira-recado-certo` | ouvir; presenca | ir a Rasovau ou ouvir os dois grupos | 12-fichas.md (R4-4.1) |
| `fact.vrissk-chefia-trato-desembarque` | Chefia o Trato do Desembarque | confirmado | `npc.vrissk-colonia-quieta` | presenca; registro-local | ir a Praia-Pardo ou ler o acerto de carga do pouso | 12-fichas.md (R4-4.1) |
| `fact.tibbe-chefia-trato-rebanhos` | Chefia o Trato dos Rebanhos | confirmado | `npc.tibbe-salto-curto` | presenca; registro-local | ir a Gramal ou ler as marcas da Tenda de Marcas | 12-fichas.md (R4-4.1) |
| `fact.mpl-01-marco-pastos-pactuados` | o Campo dos Marcos guarda os marcos dos pastos pactuados; os marcos são renovados quando o campo muda | confirmado | ninguém | ouvir; presenca | ir a Tabuleiro-Velho ou ouvir os Guardiões da Reserva | doc 01 parte 4 (MPL-01); doc 05 (L-02) |
| `fact.mpl-04-carga-com-papel` | o pouso recebe carga com papel que o Vale não reconhece | confirmado | ninguém | presenca; registro-local | ir a Praia-Pardo ou ler o acerto de carga do pouso | doc 01 parte 4 (MPL-04); doc 05 |
| `fact.mpl-07-dois-campos` | a marcha escolhe entre dois campos e cada escolha deixa um clã sem pasto | confirmado | ninguém | ouvir; presenca | ir à Vazante ou ouvir os Guardas da Marcha | doc 01 parte 4 (MPL-07) |
| `fact.mpl-10-gado-e-caca-mesmo-campo` | clãs de gado e clãs de caça dizem que o mesmo campo é deles e cada lado acusa o outro de estragar o pasto | confirmado | ninguém | ouvir; testemunho | ir a Gramal ou ouvir os dois lados | doc 01 parte 4 (MPL-10); doc 05 (L-08) |

### Restrito (6)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.yorna-costume-esquecido` | Sabe de um costume da Pedra da Palavra que a Assembleia esqueceu | suspeita | `npc.yorna-palavra-firme` | favor; ordem-legal | confiança de Yorna ou pedido formal à Assembleia; basta um caminho (ASSUMINDO) | 12-fichas.md (R4-4.1) |
| `fact.gruzk-negou-agua` | Já negou água a quem tinha direito para evitar um mal maior | suspeita | `npc.gruzk-estandarte-baixo` | registro-local; comparacao | relato do atendido e registro dos turnos; basta um caminho (ASSUMINDO) | 12-fichas.md (R4-4.1) |
| `fact.quira-recados-nao-entregues` | Guarda os recados que preferiu não entregar | suspeita | `npc.quira-recado-certo` | favor; registro-local | confiança de Quira ou exame da Tenda; basta um caminho (ASSUMINDO) | 12-fichas.md (R4-4.1) |
| `fact.vrissk-recusou-carga` | Já recusou carga por um motivo que não explicou | suspeita | `npc.vrissk-colonia-quieta` | registro-local; favor | acerto de carga do pouso ou confiança de Vrissk; basta um caminho (ASSUMINDO) | 12-fichas.md (R4-4.1) |
| `fact.tibbe-favoreceu-gado` | Já favoreceu os clãs de gado no último acerto | suspeita | `npc.tibbe-salto-curto` | registro-local; comparacao | marcas do último acerto cruzadas com o relato dos dois lados; basta um caminho (ASSUMINDO) | 12-fichas.md (R4-4.1) |
| `fact.cassan-sabe-quem-apoia-quem` | Sabe qual clã apoia qual lado | suspeita | `npc.cassan-galhada-velha` | favor; perguntar | confiança de Cassan; ele não toma lado em público; basta um caminho (ASSUMINDO) | 12-fichas.md (R4-4.1) |

### Secreto (14)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.kharos-decidiu-sucessor` | Já decidiu, sozinho, quem deseja que o suceda | suspeita | `npc.kharos-casco-de-marcha` | comparacao; testemunho | evidência independente (relato dos Guardas da Marcha; atos do Capitão perante a Assembleia) mais confronto; ele não confessa só por ser perguntado | 12-fichas.md (R4-4.1) |
| `fact.moura-reserva-fora-da-conta` | Guarda uma parte da reserva fora da conta | suspeita | `npc.moura-mel-guardado` | inspecao; comparacao; testemunho | evidência independente (relato da Mesa da Partilha; exame da câmara fria) mais confronto; ela pode calar | 12-fichas.md (R4-4.1) |
| `fact.pocha-sustenta-cla-recusado` | Sustenta, sem dizer, um clã que o Trato recusou | suspeita | `npc.pocha-ninhada-farta` | comparacao; testemunho | evidência independente mais confiança ou confronto; ela pode calar | 12-fichas.md (R4-4.1) |
| `fact.tessun-ouviu-do-outro-lado` | Já ouviu do outro lado mais do que conta | suspeita | `npc.tessun-salto-calado` | comparacao; testemunho | evidência independente mais confiança ou confronto; ele pode divergir ou calar | 12-fichas.md (R4-4.1) |
| `fact.rukhan-terceiro-campo` | Já viu um terceiro campo e não o disse | suspeita | `npc.rukhan-retorno-certo` | comparacao; testemunho | evidência independente mais confiança ou confronto; ele pode calar | 12-fichas.md (R4-4.1) |
| `fact.zinu-deixou-passar-sem-pagar` | Já deixou passar quem não pagou para ganhar um favor | suspeita | `npc.zinu-troca-justa` | comparacao; testemunho | evidência independente (quem passou sem pagar e o outro grupo) mais confronto; ele não confessa só por ser perguntado | 12-fichas.md (R4-4.1) |
| `fact.relogio-reserva-em-ano-ruim` | o estado do relógio A reserva sem regra (progresso 2/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-dupla-cobranca` | o estado do relógio Dois grupos, um vau (progresso 3/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-desembarque` | o estado do relógio O desembarque cobrado de dois jeitos (progresso 2/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-cisterna` | o estado do relógio A cisterna única (progresso 2/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-sucessao-do-capitao` | o estado do relógio O mandato que acaba (progresso 3/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-campo-da-marcha` | o estado do relógio Dois campos, um rumo (progresso 2/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-guia-das-trilhas` | o estado do relógio Quem guia o rebanho (progresso 2/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-capim-disputado` | o estado do relógio Gado e caça no mesmo campo (progresso 2/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |

## Conferências

| Verificação | Resultado |
|---|---|
| Fatos | 42 (11 públicos, 11 locais, 6 restritos, 14 secretos) |
| Fato restrito ou secreto com caminho e como descobrir | 20 de 20 |
| Fato restrito ou secreto em resumo `local` (marcador) | 0 |
| Pessoa principal com 2 fatos (confirmado e suspeita) | 12 de 12 |
| Relógios secretos | 8 de 8 |
| Mistérios com um fato confirmado | 10 de 10 |
| Campo vazio (exceto `marcador`, só dos restritos e secretos) | 0 |
| Fato de outro reino, número novo ou resposta de mistério | 0 |

## Decisão pendente

**D-130:** aprovar as camadas, os caminhos e o critério. Opções: **A.** aprovar; **B.** trocar a camada ou o caminho de um fato (diga qual). **Recomendação: A.**
