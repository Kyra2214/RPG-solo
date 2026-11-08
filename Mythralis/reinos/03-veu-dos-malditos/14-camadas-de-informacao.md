# 14 — Camadas de informação (R3-6.2)

**Estado:** rascunho de R3-6.2 para aprovação do autor (D-93) · **Data:** 2026-11-06

**Etiqueta:** tudo é **[PROPOSTO]**; classificação é leitura editorial, não lore novo. **Fonte:** doc 04 §3, §5 e §6; doc 06 §3 e §6; doc 01 parte 4; `12-conhecimento.csv`, `12-relogios.csv`, `12-fichas.md`, `14-resumos-de-contexto.csv`. Dados em `14-camadas-de-informacao.csv` (prefixo `fact.`). Molde: `reinos/02-.../14-camadas-de-informacao.md`; a coluna `marcador` é só do validador.

## Regras

- **Quatro camadas por fato:** público, local, restrito, secreto. A camada diz **quem pode saber sem esforço especial**; não diz se o fato é verdadeiro.
- **41 fatos:** 7 públicos, 14 locais, 4 restritos, 16 secretos. Entram os fatos *confirmado* e *suspeita* de `12-conhecimento.csv` dos 12 principais (24), um fato confirmado por mistério MV-01 a MV-09 (9) e os 8 relógios. Fatos *desconhecido* não entram (nada a proteger); rumor e hipótese também não.
- **Critério (ASSUMINDO, o mesmo dos reinos 1 e 2):** **secreto** = o texto diz que o titular esconde, omite, protege ou cala, ou é estado real que ninguém detém (relógios); **restrito** = o titular guarda o fato em registro, arquivo ou saber que só cede por autorização, favor ou ordem; **local** = a comunidade ou o registro local sabe; **público** = visível a qualquer pessoa na região.
- **Segredo nunca sai por narração espontânea:** fato restrito ou secreto só entra na cena depois que o personagem cumpre um caminho da coluna `caminho`. O Mestre sabe; o personagem só sabe o que descobriu.
- **Evidência não documentada** aparece como lacuna (o que o titular aceitar dizer); nenhuma prova foi inventada.
- **Nada de lore novo:** nenhum NPC, facção, rota, número ou fato. Nada afirmado sobre Aeltharyn. Nenhum fato resolve mistério, lei ou lacuna (L-01, L-07, L-09, o que a Descida traz, o que o fim do pacto concede).

## Camadas e acesso

| Camada | Quem sabe sem esforço | Caminho mínimo para descobrir | Pode aparecer em resumo? |
|---|---|---|---|
| **Público** | qualquer pessoa na região | ouvir, estar no local, perguntar | sim |
| **Local** | a comunidade e quem mantém o registro local | presença, perguntar ao titular, registro local, testemunho | sim (camada máxima dos resumos) |
| **Restrito** | só o titular e quem ele autorizar | autorização de arquivo, favor, ordem legal ou exame da evidência (ASSUMINDO: um caminho basta) | **não** |
| **Secreto** | só o titular (ninguém, nos relógios) | evidência independente **mais** confiança ou confronto; o titular não confessa só por ser perguntado (ASSUMINDO) | **não** |

## Caminhos (coluna `caminho`)

`ouvir` rumor ou conversa · `presenca` estar no local · `perguntar` ao titular · `registro-local` atas, contas, livros · `arquivo-autorizado` arquivo restrito com autorização (Arquivo dos Pactos Velhos) · `ordem-legal` decisão formal · `favor` confiança, dívida ou troca · `inspecao` examinar poço, muro ou terreno · `comparacao` cruzar duas fontes · `testemunho` quem viu ou viveu.

## Resumos e camadas

- `14-resumos-de-contexto.csv`: `camada_max` **definitiva**: `local` para 49 resumos (cidades, facções e pessoas) e `secreto` para os 8 relógios (uso só de Mestre e motor).
- Nenhum `marcador` de fato restrito ou secreto aparece em resumo de camada `local` (0 ocorrência). A coluna `marcador` não vai ao contexto.
- Fichas: o campo *Camadas de informação* de N-01 a N-12 lista os `fact.` da pessoa (`12-fichas.md` e `.csv`).

## Subida de camada

Descobrir um fato muda o **conhecimento do personagem**, não a camada do fato. Divulgar um fato restrito ou secreto, por jogador ou NPC, é evento do mundo e exige registro; a camada só muda por esse registro (ASSUMINDO). Revelar ao Mestre não revela ao jogador.

## Fatos por camada

### Público (7)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.morvan-necromante-reina` | É necromante; reina em Morrovéu há cerca de cem anos; estabilizou as terras | confirmado | `npc.morvan-voz-baixa` | ouvir; presenca | qualquer pessoa em Morrovéu comenta; ir à Corte | 12-fichas.md (R3-4.1) |
| `fact.orsk-comanda-guarda` | Comanda a guarda que serve ao rei | confirmado | `npc.orsk-cicatriz-de-bando` | ouvir; presenca | ir a Morrovéu ou ouvir os guardas | 12-fichas.md (R3-4.1) |
| `fact.cerrane-preside-conselho` | Preside o Conselho dos Pactuários | confirmado | `npc.cerrane-heranca-firme` | ouvir; presenca | ir ao Conselho ou ouvir os Pactuários | 12-fichas.md (R3-4.1) |
| `fact.ilkar-comanda-vigias` | Comanda as vigias da cidade | confirmado | `npc.ilkar-turno-aberto` | ouvir; presenca | ir a Candeia-Morta ou ouvir os vigias | 12-fichas.md (R3-4.1) |
| `fact.lista-nao-anda` | a lista de desafiantes não anda há cerca de cem anos | confirmado | ninguém | ouvir; presenca | qualquer pessoa em Morrovéu comenta; ir ao Campo das Provações | doc 01 parte 4 (MV-01); doc 04 §5 |
| `fact.canais-e-partida` | os canais mudam de lugar com a cheia; famílias querem partir e o pacto manda que fiquem | confirmado | ninguém | ouvir; presenca | ir a Lamaluz ou ouvir os pilotos | doc 01 parte 4 (MV-03); doc 05 §1 (L-09) |
| `fact.cidade-cuida-dos-mortos` | a cidade cuida dos mortos e das despedidas | confirmado | ninguém | ouvir; presenca | ir a Candeia-Morta | doc 01 parte 4 (MV-09); doc 05 §1 (L-07) |

### Local (14)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.tazk-conhece-canais` | Conhece os canais mutáveis de Lamaluz | confirmado | `npc.tazk-pedra-ao-sol` | ouvir; presenca | ir a Lamaluz ou ouvir os pilotos | 12-fichas.md (R3-4.1) |
| `fact.rusk-guarda-barra` | Guarda o Pacto da Barra | confirmado | `npc.rusk-rede-da-barra` | presenca; registro-local | ir a Fozbreu ou ler o registro da Casa da Barra | 12-fichas.md (R3-4.1) |
| `fact.vurga-conduz-caravanas` | Conduz caravanas pelo Ermo | confirmado | `npc.vurga-sede-vencida` | ouvir; presenca | ir a Beira-Sal ou ouvir os guias | 12-fichas.md (R3-4.1) |
| `fact.teshi-guarda-pocos` | Guarda o Poço Primeiro e as sentinelas de sal | confirmado | `npc.teshi-no-de-poco` | presenca; registro-local | ir a Beira-Sal ou ver os poços sinalizados | 12-fichas.md (R3-4.1) |
| `fact.yeshe-preside-selos` | Preside o Conselho dos Selos; guarda o Arquivo dos Pactos Velhos | confirmado | `npc.yeshe-forma-emprestada` | presenca; registro-local | ir a Selovale ou ler o registro do Conselho dos Selos | 12-fichas.md (R3-4.1) |
| `fact.krell-comanda-torre` | Comanda a Torre das Fogueiras | confirmado | `npc.krell-eco-da-vigia` | ouvir; presenca | ir a Borda-Mansa ou ouvir os vigias | 12-fichas.md (R3-4.1) |
| `fact.ondra-governa-casa` | Governa a Casa dos Fios | confirmado | `npc.ondra-fio-de-sono` | ouvir; presenca | ir a Fio-Longo ou ouvir os hóspedes | 12-fichas.md (R3-4.1) |
| `fact.dhun-guarda-muro` | Conduz as despedidas e guarda o Muro dos Nomes | confirmado | `npc.dhun-contorno-quieto` | presenca; registro-local | ir a Candeia-Morta ou ver o Muro dos Nomes | 12-fichas.md (R3-4.1) |
| `fact.necromancia-e-lei-dos-mortos` | a lei dos mortos não diz se a necromancia a viola | confirmado | ninguém | ouvir; perguntar | perguntar aos Pactuários das Despedidas ou ouvir quem cuida dos mortos | doc 01 parte 4 (MV-02); doc 05 §1 (L-07) |
| `fact.armazem-sem-dono` | o Armazém dos Fardos Sem Dono guarda carga sem papel que o pacto reconheça | confirmado | ninguém | presenca; registro-local | ir a Fozbreu ou ler o registro da Casa da Barra | doc 01 parte 4 (MV-04); doc 05 §1 (L-04) |
| `fact.pactos-orais` | guias cobram por pactos orais que ninguém prova | confirmado | ninguém | ouvir; presenca | ir a Beira-Sal ou ouvir guias e caravanas | doc 01 parte 4 (MV-05); doc 05 §1 (L-05) |
| `fact.pacto-antigo-vence` | um pacto antigo vence e ninguém sabe o que o fim concede | confirmado | ninguém | ouvir; registro-local | ir a Selovale ou ler o registro do Conselho dos Selos | doc 01 parte 4 (MV-06); doc 05 §1 (L-08) |
| `fact.chegadas-sem-guia` | gente de longe aparece sem guia e sem registro | confirmado | ninguém | ouvir; presenca | ir a Borda-Mansa ou ouvir os vigias | doc 01 parte 4 (MV-07); doc 05 §1 (L-03) |
| `fact.hospedes-que-ficam` | alguns hóspedes não querem ir embora e a Casa e os parentes divergem sobre o motivo | confirmado | ninguém | ouvir; presenca | ir a Fio-Longo ou ouvir hóspedes e parentes | doc 01 parte 4 (MV-08); doc 05 §1 (L-06) |

### Restrito (4)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.vurga-guia-sem-sede` | Sabe qual guia sumiu e voltou sem sede | suspeita | `npc.vurga-sede-vencida` | testemunho; favor | favor ou testemunho dos guias; um caminho basta | 12-fichas.md (R3-4.1) |
| `fact.yeshe-clausula-lida` | Já leu uma cláusula que o Conselho desconhece | suspeita | `npc.yeshe-forma-emprestada` | arquivo-autorizado; favor | autorização do Arquivo ou favor de Yeshe; um caminho basta | 12-fichas.md (R3-4.1) |
| `fact.ondra-hospedes-que-ficam` | Sabe de quem não quer partir e por quê | suspeita | `npc.ondra-fio-de-sono` | registro-local; favor | favor de Ondra ou registro da Casa; um caminho basta | 12-fichas.md (R3-4.1) |
| `fact.dhun-nome-mudou` | Já viu um nome mudar de lugar no Muro | suspeita | `npc.dhun-contorno-quieto` | inspecao; registro-local | examinar o Muro e os livros de turno com autorização; um caminho basta | 12-fichas.md (R3-4.1) |

### Secreto (16)

| ID | Fato | Estado | Quem sabe | Caminho | Como descobrir | Fonte |
|---|---|---|---|---|---|---|
| `fact.morvan-custo-estabilizacao` | A estabilidade tem um custo que ele paga e ninguém nomeia | suspeita | `npc.morvan-voz-baixa` | comparacao; testemunho | evidência independente (pareceres do Conselho dos Selos, relatos das Despedidas) mais confronto; ele não confessa só por ser perguntado | 12-fichas.md (R3-4.1) |
| `fact.orsk-guarda-sem-rei` | Já pensou, sozinho, a quem a guarda seguiria se o rei caísse | suspeita | `npc.orsk-cicatriz-de-bando` | comparacao; testemunho | evidência independente da lealdade da guarda mais confiança ou confronto; ele pode recusar falar | 12-fichas.md (R3-4.1) |
| `fact.cerrane-clausula-nao-lida` | Sabe de uma cláusula da coroa que o Conselho não leu | suspeita | `npc.cerrane-heranca-firme` | comparacao; testemunho | evidência independente no Arquivo mais confiança ou confronto; ela pode recusar falar | 12-fichas.md (R3-4.1) |
| `fact.ilkar-pedido-da-corte` | A Corte já pediu algo que o Pacto da Vigília proibiria | suspeita | `npc.ilkar-turno-aberto` | comparacao; testemunho | evidência independente nos turnos e na Corte mais confiança ou confronto | 12-fichas.md (R3-4.1) |
| `fact.tazk-guiou-quem-partia` | Já guiou alguém que partia | suspeita | `npc.tazk-pedra-ao-sol` | comparacao; testemunho | testemunho de quem partiu mais confiança dos pilotos; guiar é acusável (L-09) | 12-fichas.md (R3-4.1) |
| `fact.rusk-dono-do-fardo` | Sabe quem é o dono do fardo que ninguém reclamou | suspeita | `npc.rusk-rede-da-barra` | comparacao; testemunho | evidência independente no registro e no armazém mais confiança ou confronto; ele pode não dizer | 12-fichas.md (R3-4.1) |
| `fact.teshi-agua-escondida` | Escondeu quanto água o posto realmente tem | suspeita | `npc.teshi-no-de-poco` | comparacao; inspecao | inspecionar os poços mais confiança ou confronto; ela pode ter escondido | 12-fichas.md (R3-4.1) |
| `fact.krell-ajuda-de-dentro` | Alguém sem registro já passou pela orla com ajuda de dentro | suspeita | `npc.krell-eco-da-vigia` | comparacao; testemunho | evidência independente nos registros de chegada mais confiança; ele suspeita e não afirma | 12-fichas.md (R3-4.1) |
| `fact.relogio-desafiantes` | o estado do relógio A lista parada e o Conselho dividido (2/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-partida` | o estado do relógio Partida e acusação em Lamaluz (3/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-fardos` | o estado do relógio Fardos sem papel em Fozbreu (2/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-acertos-orais` | o estado do relógio Acertos orais em Beira-Sal (2/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-pacto-que-vence` | o estado do relógio O pacto antigo que vence em Selovale (2/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-orla-sem-guia` | o estado do relógio Chegadas sem guia em Borda-Mansa (1/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-hospedes` | o estado do relógio Hóspedes que não partem em Fio-Longo (1/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |
| `fact.relogio-despedidas` | o estado do relógio A divisão das Despedidas em Candeia-Morta (2/6 no início) e o que o avança | confirmado | ninguém | comparacao; testemunho | Mestre e motor; o personagem só nota os sinais que aparecem em cena | doc 06 §3; 12-relogios.csv |

## Conferências

| Verificação | Resultado |
|---|---|
| Fatos | 41 (7 públicos, 14 locais, 4 restritos, 16 secretos) |
| Fato restrito ou secreto com caminho e como descobrir | 20 de 20 |
| Fato restrito ou secreto em resumo `local` (marcador) | 0 |
| Pessoa principal com 2 fatos (confirmado e suspeita) | 12 de 12 |
| Relógios secretos | 8 de 8 |
| Campo vazio (exceto `marcador`, só dos restritos e secretos) | 0 |
| Fato de Aeltharyn, número novo ou resposta de mistério | 0 |

## Decisão pendente

**D-93:** aprovar as camadas, os caminhos e o critério. Opções: **A.** aprovar; **B.** trocar a camada ou o caminho de um fato (diga qual). **Recomendação: A.**
