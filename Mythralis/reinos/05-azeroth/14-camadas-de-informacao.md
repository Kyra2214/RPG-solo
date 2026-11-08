# Azeroth — Camadas de informação (doc 14, R5-6.2)

**Estado:** R5-6.2, D-167 aceita pela continuação · **Data:** 2026-11-07

**Etiqueta:** tudo é **[PROPOSTO]**; classificação é leitura editorial, não lore novo. **Fonte:** doc 04 §3, §5 e §6; doc 06 §3 e §6; doc 01 parte 4; `12-conhecimento.csv`, `12-relogios.csv`, `12-fichas.md`, `13-misterios-limites.csv`, `14-resumos-de-contexto.csv`. Dados em `14-camadas-de-informacao.csv` (prefixo `fact.`; a coluna `marcador` é só do validador). Molde: `reinos/04-vale-palancar/14-camadas-de-informacao.md`.

## Regras

- **Quatro camadas por fato:** público, local, restrito, secreto. A camada diz **quem pode saber sem esforço especial**; não diz se o fato é verdadeiro.
- **44 fatos:** 10 públicos, 12 locais, 2 restritos, 20 secretos. Entram os fatos *confirmado* e *suspeita* de `12-conhecimento.csv` dos 12 principais (24), um fato confirmado por mistério MA-01 a MA-10 (10) e os 10 relógios. Fatos *desconhecido* não entram (nada a proteger); rumor e hipótese também não.
- **Critério (ASSUMINDO, o mesmo dos reinos 1 a 4):** **secreto** = o texto diz que o titular esconde, omite, protege ou cala, ou é estado real que ninguém detém (relógios); **restrito** = o titular guarda o fato em registro, chave, recado ou saber que só cede por autorização, favor ou ordem; **local** = a comunidade ou o registro local sabe; **público** = visível a qualquer pessoa na região.
- **Segredo nunca sai por narração espontânea:** fato restrito ou secreto só entra na cena depois que o personagem cumpre um caminho da coluna `caminho`. O Mestre sabe; o personagem só sabe o que descobriu.
- **Evidência não documentada** aparece como lacuna (o que o titular aceitar dizer) ou como ASSUMINDO; nenhuma prova foi inventada.
- **Nada de lore novo:** nenhum NPC, facção, lugar, rota, número ou fato. Nada afirmado sobre Aeltharyn, as Coroas, o Véu ou o Vale. Nenhum fato resolve mistério, lei ou lacuna (L-01 a L-10) nem decide a precedência entre as coroas.

## Camadas e acesso

| Camada | Quem sabe sem esforço | Caminho mínimo para descobrir | Pode aparecer em resumo? |
|---|---|---|---|
| **Público** | qualquer pessoa na região | ouvir, estar no local, perguntar | sim |
| **Local** | a comunidade e quem mantém o registro local | presença, perguntar ao titular, registro local, testemunho | sim (camada máxima dos resumos) |
| **Restrito** | só o titular e quem ele autorizar | favor, ordem legal (da Mesa de Meiovau ou do governante da coroa), registro do titular ou exame da evidência (ASSUMINDO: um caminho basta) | **não** |
| **Secreto** | só o titular (ninguém, nos relógios) | evidência independente **mais** confiança ou confronto; o titular não confessa só por ser perguntado (ASSUMINDO) | **não** |

## Caminhos (coluna `caminho`)

`ouvir` rumor ou conversa · `presenca` estar no local · `perguntar` ao titular · `registro-local` medidas, livros, turnos, códigos e recados · `ordem-legal` decisão formal da Mesa ou do governante · `favor` confiança, dívida ou troca · `inspecao` examinar câmara, cais ou torre · `comparacao` cruzar duas fontes · `testemunho` quem viu ou viveu.

## Resumos e camadas

- `14-resumos-de-contexto.csv`: `camada_max` **definitiva**: `local` para 63 resumos (cidades, facções e pessoas) e `secreto` para os 10 relógios (uso só de Mestre e motor).
- Nenhum `marcador` de fato restrito ou secreto de pessoa aparece em resumo de camada `local` (0 ocorrência). A coluna `marcador` não vai ao contexto.
- Fichas: o campo *Camadas de informação* de N-01 a N-12 lista os `fact.` da pessoa (`12-fichas.md` e coluna `camadas` de `12-fichas.csv`).

## Subida de camada

Descobrir um fato muda o **conhecimento do personagem**, não a camada do fato. Divulgar um fato restrito ou secreto, por jogador ou NPC, é evento do mundo e exige registro; a camada só muda por esse registro (ASSUMINDO). Revelar ao Mestre não revela ao jogador.

## Fatos por camada

### Públicos (10)

| Fato | Texto | Estado | Titular | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.odile-governa-a-coroa-do` | Governa a Coroa do Trigal e reside em Campoalvo | confirmado | `npc.odile-medida-cheia` | ouvir; presenca | ir ao lugar ou ouvir quem ali vive | 12-fichas.md (R5-4.1) |
| `fact.garvane-salvou-carga-e-barqueiros` | Salvou carga e barqueiros na cheia | confirmado | `npc.garvane-remo-firme` | ouvir; presenca | ir ao lugar ou ouvir quem ali vive | 12-fichas.md (R5-4.1) |
| `fact.solenne-comanda-a-guarnicao-de` | Comanda a guarnição de Torrefronte | confirmado | `npc.solenne-vigilia-longa` | ouvir; presenca | ir ao lugar ou ouvir quem ali vive | 12-fichas.md (R5-4.1) |
| `fact.corlan-governa-a-coroa-do` | Governa a Coroa do Cais e reside em Alvamar | confirmado | `npc.corlan-cais-primeiro` | ouvir; presenca | ir ao lugar ou ouvir quem ali vive | 12-fichas.md (R5-4.1) |
| `fact.nerella-pilota-a-barra-e` | Pilota a barra e guiou a frota | confirmado | `npc.nerella-canto-da-barra` | ouvir; presenca | ir ao lugar ou ouvir quem ali vive | 12-fichas.md (R5-4.1) |
| `fact.brevo-governa-a-coroa-do` | Governa a Coroa do Vento e reside em Torreclara | confirmado | `npc.brevo-rumo-alto` | ouvir; presenca | ir ao lugar ou ouvir quem ali vive | 12-fichas.md (R5-4.1) |
| `fact.tolla-preside-a-mesa-de` | Preside a Mesa de Meiovau | confirmado | `npc.tolla-mesa-posta` | ouvir; presenca | ir ao lugar ou ouvir quem ali vive | 12-fichas.md (R5-4.1) |
| `fact.auriven-mantem-o-albergue-dos` | Mantém o Albergue dos Chegados | confirmado | `npc.auriven-porta-acesa` | ouvir; presenca | ir ao lugar ou ouvir quem ali vive | 12-fichas.md (R5-4.1) |
| `fact.ma-05-o-farol-se-mantem` | O farol se mantém aceso e a barra se avisa a quem chega (L-05) | confirmado | `city.enseada-vigia` | ouvir; presenca | ir ao lugar ou ouvir quem ali vive | doc 01 parte 4 (MA-05); docs 04 e 05 |
| `fact.ma-10-a-mesa-so-abre` | A Mesa só abre com as três chaves e nenhuma coroa possui Meiovau (L-09) | confirmado | `city.meiovau` | ouvir; presenca | ir ao lugar ou ouvir quem ali vive | doc 01 parte 4 (MA-10); docs 04 e 05 |

### Locais (12)

| Fato | Texto | Estado | Titular | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.tomlin-e-o-herdeiro-reconhecido` | É o herdeiro reconhecido do Trigal; a regra de sucessão fica para o R5-1.4 | confirmado | `npc.tomlin-horta-larga` | presenca; perguntar; registro-local | perguntar ao titular ou ao registro local | 12-fichas.md (R5-4.1) |
| `fact.rilla-e-a-herdeira-reconhecida` | É a herdeira reconhecida do Cais; a regra de sucessão fica para o R5-1.4 | confirmado | `npc.rilla-espuma-fina` | presenca; perguntar; registro-local | perguntar ao titular ou ao registro local | 12-fichas.md (R5-4.1) |
| `fact.dorgan-comanda-o-molhe-e` | Comanda o molhe e a corrente | confirmado | `npc.dorgan-escudo-lento` | presenca; perguntar; registro-local | perguntar ao titular ou ao registro local | 12-fichas.md (R5-4.1) |
| `fact.astrel-e-o-herdeiro-reconhecido` | É o herdeiro reconhecido do Vento; a regra de sucessão fica para o R5-1.4 | confirmado | `npc.astrel-noite-clara` | presenca; perguntar; registro-local | perguntar ao titular ou ao registro local | 12-fichas.md (R5-4.1) |
| `fact.ma-01-o-salao-das-medidas` | O Salão das Medidas fixa a medida do grão e os Celeiros Comuns guardam o excedente (L-01) | confirmado | `city.campoalvo` | presenca; perguntar; registro-local | perguntar ao órgão do lugar ou ao registro local | doc 01 parte 4 (MA-01); docs 04 e 05 |
| `fact.ma-02-a-casa-dos-barqueiros` | A Casa dos Barqueiros cuida das docas, canais e comportas | confirmado | `city.vaudouro` | presenca; perguntar; registro-local | perguntar ao órgão do lugar ou ao registro local | doc 01 parte 4 (MA-02); docs 04 e 05 |
| `fact.ma-03-o-livro-dos-chegados` | O Livro dos Chegados registra quem chega ou parte do limite (L-03) | confirmado | `city.torrefronte` | presenca; perguntar; registro-local | perguntar ao órgão do lugar ou ao registro local | doc 01 parte 4 (MA-03); docs 04 e 05 |
| `fact.ma-04-o-navio-entra-pelo` | O navio entra pelo molhe e a corrente guarda a boca (L-04) | confirmado | `city.alvamar` | presenca; perguntar; registro-local | perguntar ao órgão do lugar ou ao registro local | doc 01 parte 4 (MA-04); docs 04 e 05 |
| `fact.ma-06-o-jardim-e-o` | O jardim e o santuário ficam em paz e o estaleiro serve a quem paga dentro dessa regra (L-06) | confirmado | `city.concharia` | presenca; perguntar; registro-local | perguntar ao órgão do lugar ou ao registro local | doc 01 parte 4 (MA-06); docs 04 e 05 |
| `fact.ma-07-o-codigo-de-cada` | O código de cada torre é guardado na Casa dos Sinais e o aviso corre a quem paga (L-07) | confirmado | `city.torreclara` | presenca; perguntar; registro-local | perguntar ao órgão do lugar ou ao registro local | doc 01 parte 4 (MA-07); docs 04 e 05 |
| `fact.ma-08-o-calendario-de-rota` | O calendário de rota e a previsão saem da Mesa dos Astros (L-08) | confirmado | `city.estrelado` | presenca; perguntar; registro-local | perguntar ao órgão do lugar ou ao registro local | doc 01 parte 4 (MA-08); docs 04 e 05 |
| `fact.ma-09-o-posto-dos-pipeiros` | O Posto dos Pipeiros cuida da linha de torres e pousos entre o interior e a costa | confirmado | `city.pouso-das-brisas` | presenca; perguntar; registro-local | perguntar ao órgão do lugar ou ao registro local | doc 01 parte 4 (MA-09); docs 04 e 05 |

### Restritos (2)

| Fato | Texto | Estado | Titular | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.rilla-sabe-de-um-navio-que` | Sabe de um navio que descarrega fora do molhe | suspeita | `npc.rilla-espuma-fina` | favor; comparacao | confiança de Rilla ou confronto com quem viu o navio; basta um caminho (ASSUMINDO) | 12-fichas.md (R5-4.1) |
| `fact.tolla-sabe-qual-chave-pode-ter` | Sabe qual chave pode ter sido copiada | suspeita | `npc.tolla-mesa-posta` | favor; ordem-legal | confiança de Tolla ou pedido formal à Mesa com as três chaves; basta um caminho (ASSUMINDO) | 12-fichas.md (R5-4.1) |

### Secretos (20)

| Fato | Texto | Estado | Titular | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.odile-guarda-em-segredo-o-tamanho` | Guarda em segredo o tamanho real dos celeiros | suspeita | `npc.odile-medida-cheia` | comparacao; testemunho | evidência independente mais confiança ou confronto; o titular pode calar ou divergir (ASSUMINDO) | 12-fichas.md (R5-4.1) |
| `fact.tomlin-ja-conversou-com-o-cais` | Já conversou com o Cais sem avisar a governante | suspeita | `npc.tomlin-horta-larga` | comparacao; testemunho | evidência independente mais confiança ou confronto; o titular pode calar ou divergir (ASSUMINDO) | 12-fichas.md (R5-4.1) |
| `fact.garvane-ja-deixou-passar-uma-barcaca` | Já deixou passar uma barcaça sem taxa | suspeita | `npc.garvane-remo-firme` | comparacao; testemunho | evidência independente mais confiança ou confronto; o titular pode calar ou divergir (ASSUMINDO) | 12-fichas.md (R5-4.1) |
| `fact.solenne-ja-abriu-o-limite-a` | Já abriu o limite a quem não devia, em nome do Livro | suspeita | `npc.solenne-vigilia-longa` | comparacao; testemunho | evidência independente mais confiança ou confronto; o titular pode calar ou divergir (ASSUMINDO) | 12-fichas.md (R5-4.1) |
| `fact.corlan-ja-deixou-certos-navios-descarregarem` | Já deixou certos navios descarregarem fora do molhe em troca de apoio | suspeita | `npc.corlan-cais-primeiro` | comparacao; testemunho | evidência independente mais confiança ou confronto; o titular pode calar ou divergir (ASSUMINDO) | 12-fichas.md (R5-4.1) |
| `fact.dorgan-ja-deixou-a-corrente-frouxa` | Já deixou a corrente frouxa por um motivo que não explicou | suspeita | `npc.dorgan-escudo-lento` | comparacao; testemunho | evidência independente mais confiança ou confronto; o titular pode calar ou divergir (ASSUMINDO) | 12-fichas.md (R5-4.1) |
| `fact.nerella-sabe-uma-rota-de-volta` | Sabe uma rota de volta que não ensinou a ninguém | suspeita | `npc.nerella-canto-da-barra` | comparacao; testemunho | evidência independente mais confiança ou confronto; o titular pode calar ou divergir (ASSUMINDO) | 12-fichas.md (R5-4.1) |
| `fact.brevo-ja-mudou-um-codigo-de` | Já mudou um código de torre sem avisar as outras coroas | suspeita | `npc.brevo-rumo-alto` | comparacao; testemunho | evidência independente mais confiança ou confronto; o titular pode calar ou divergir (ASSUMINDO) | 12-fichas.md (R5-4.1) |
| `fact.astrel-leu-no-ceu-um-sinal` | Leu no céu um sinal que não contou a Brevo | suspeita | `npc.astrel-noite-clara` | comparacao; testemunho | evidência independente mais confiança ou confronto; o titular pode calar ou divergir (ASSUMINDO) | 12-fichas.md (R5-4.1) |
| `fact.auriven-sustenta-o-albergue-com-doacao` | Sustenta o albergue com doação que não diz de onde vem | suspeita | `npc.auriven-porta-acesa` | comparacao; testemunho | evidência independente mais confiança ou confronto; o titular pode calar ou divergir (ASSUMINDO) | 12-fichas.md (R5-4.1) |
| `fact.relogio-medida-em-ano-ruim` | o estado do relógio A medida do pão (progresso 2/6 no início) e o que o avança | confirmado | `clock.medida-em-ano-ruim` | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06; 12-relogios.csv |
| `fact.relogio-passagem-dos-rios` | o estado do relógio Duas taxas, uma barcaça (progresso 3/6 no início) e o que o avança | confirmado | `clock.passagem-dos-rios` | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06; 12-relogios.csv |
| `fact.relogio-livro-lido-de-dois-modos` | o estado do relógio O registro lido de dois modos (progresso 2/6 no início) e o que o avança | confirmado | `clock.livro-lido-de-dois-modos` | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06; 12-relogios.csv |
| `fact.relogio-taxa-do-molhe` | o estado do relógio A taxa do molhe (progresso 3/6 no início) e o que o avança | confirmado | `clock.taxa-do-molhe` | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06; 12-relogios.csv |
| `fact.relogio-luz-entre-coroas` | o estado do relógio A luz entre duas coroas (progresso 2/6 no início) e o que o avança | confirmado | `clock.luz-entre-coroas` | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06; 12-relogios.csv |
| `fact.relogio-canal-e-jardim` | o estado do relógio O canal e o jardim (progresso 2/6 no início) e o que o avança | confirmado | `clock.canal-e-jardim` | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06; 12-relogios.csv |
| `fact.relogio-codigo-sem-aviso` | o estado do relógio O código sem aviso (progresso 3/6 no início) e o que o avança | confirmado | `clock.codigo-sem-aviso` | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06; 12-relogios.csv |
| `fact.relogio-leitura-do-ceu` | o estado do relógio Duas leituras do mesmo céu (progresso 2/6 no início) e o que o avança | confirmado | `clock.leitura-do-ceu` | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06; 12-relogios.csv |
| `fact.relogio-sinais-proprios` | o estado do relógio O sinal que cada coroa quer (progresso 2/6 no início) e o que o avança | confirmado | `clock.sinais-proprios` | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06; 12-relogios.csv |
| `fact.relogio-acolhida-de-meiovau` | o estado do relógio Prazo e sustento do chegado (progresso 3/6 no início) e o que o avança | confirmado | `clock.acolhida-de-meiovau` | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06; 12-relogios.csv |

## Conferências

| Verificação | Resultado |
|---|---|
| Fatos | 44 (10 públicos, 12 locais, 2 restritos, 20 secretos) |
| Fato restrito ou secreto com caminho e como descobrir | 22 de 22 |
| Fato restrito ou secreto de pessoa em resumo `local` (marcador) | 0 |
| Pessoa principal com 2 fatos (confirmado e suspeita) | 12 de 12 |
| Relógios secretos | 10 de 10 |
| Mistérios com um fato confirmado | 10 de 10 |
| Campo vazio (exceto `marcador`, só dos restritos e secretos) | 0 |
| Fato de outro reino, número novo ou resposta de mistério | 0 |

## Decisão pendente

**D-167:** aprovar as camadas, os caminhos e o critério. Opções: **A.** aprovar; **B.** trocar a camada ou o caminho de um fato (diga qual). **Recomendação: A.**
