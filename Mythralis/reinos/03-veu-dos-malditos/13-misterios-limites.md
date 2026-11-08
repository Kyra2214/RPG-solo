# 13 — Mistérios com limites (R3-5.5)

**Estado:** rascunho de R3-5.5 para aprovação do autor (D-91) · **Data:** 2026-11-06

**Etiqueta:** tudo é **[PROPOSTO MV-01 a MV-09]**; os mistérios, hipóteses e vetos já estão no doc 01 (parte 4). Aqui eles viram dados (prefixo `mist.`), com fatos confirmados, evidência e ligação a relógios e eventos. **Fonte:** doc 01 parte 4, §2 e fichas; doc 04 §3 a §6; doc 05 §1; doc 06; `12-conhecimento.csv`, `12-relogios.csv`, `12-eventos.csv`. Dados em `13-misterios-limites.csv`. Molde: `reinos/02-.../13-misterios-limites.md`.

## Regras

- **Sem resposta única:** cada mistério mantém **quatro hipóteses abertas** compatíveis com os fatos confirmados. Nenhuma é eleita aqui.
- **Limite da verdade:** *a verdade não pode ser* lista o que contradiria fato confirmado, regra de consistência, lacuna explícita ou os mistérios de Aeltharyn. É a fronteira de qualquer resposta futura, não uma resposta.
- **Rumor e suspeita não são fato:** rumores (doc 01 §2) e suspeitas das fichas (`12-conhecimento.csv`) aparecem só como pista.
- **Resolver é opcional:** o mistério pode ficar aberto, piorar ou ser resolvido por NPC fora de cena. Resolver exige **evidência cumulativa** (pelo menos duas provas independentes) e registro da narração; a IA não escolhe a resposta (doc 06 §6).
- **Nenhum mistério confirma, nega ou resolve mistério de Aeltharyn** (Neblina Longa, Mistério E, Brumafunda, Bosque dos Sussurros) nem liga o Véu a evento de lá (contrato de fronteira §3). Os termos de fronteira só aparecem em vetos (*a verdade não pode ser*), como no doc 01.
- Mistérios não têm relógio próprio; as ligações abaixo são só às situações já escritas (`12-relogios.csv`, `12-eventos.csv`). As sementes (R3-5.3) e as cadeias (R3-5.2) que tocam um mistério herdam estes limites.
- Itens de *a verdade não pode ser* seguem o texto do doc 01; nenhum veto foi acrescentado ou retirado.

## Mistérios

### `mist.mv-01-numeros-lista-parada` — MV-01 — A lista que não anda (Morrovéu)
- **Fatos confirmados:** a lista de desafiantes não anda há cerca de cem anos; o rei não pode recusar desafiante inscrito (L-01); o Conselho se divide entre defender o rei e lembrar que ele deve ser vencido.
- **Hipóteses abertas:** ninguém se inscreve, por medo ou por conta da paz; alguém se inscreveu e a inscrição não chegou ao Arquivo; há cláusula esquecida na regra da Provação que trava a lista; o Conselho adia o registro de propósito.
- **A verdade não pode ser:** que o Lord impede a lista por ameaça ou por feitiço, sem prova (a necromancia fica aberta em L-01); que a Provação foi abolida (a lei vale); culpa de um só povo ou de uma só facção; resposta que decida o que a guarda faria se o rei caísse (doc 04 §5 item 5).
- **Evidência para resolver:** Arquivo dos Reis que Passaram; registros do Conselho; testemunho de Cerrane (N-03) e de Orsk (N-02), que podem recusar falar; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.cerrane-heranca-firme`, `npc.orsk-cicatriz-de-bando`, `npc.morvan-voz-baixa`, `faction.memoria-das-provacoes`, `faction.defensores-da-paz`.
- **Relógio e evento:** `clock.desafiantes`, `event.conselho-vota-em-publico`.
- **Fonte:** doc 01 parte 4 (MV-01); doc 04; doc 05 §1; doc 06 §5 e §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mv-02-custo-da-estabilizacao` — MV-02 — O que a estabilização custou (Morrovéu e Candeia-Morta)
- **Fatos confirmados:** o Lord é necromante; reina há cerca de cem anos; estabilizou as terras; a necromancia dele tem custo e limites (D-66); o pacto de despedida manda que os mortos descansem (L-07), e a lei não diz se a necromancia a viola.
- **Hipóteses abertas:** o custo é pessoal e é pago só pelo Lord; o custo é repartido entre os lugares como parte do tributo em pactos, sem que se diga; a estabilização não tem custo oculto e a suspeita é só desconfiança dos Pactuários; o método é conhecido por poucos e silenciado por pacto.
- **A verdade não pode ser:** ligada à Neblina Longa, ao Mistério E ou a qualquer desaparecido de Aeltharyn; exército de mortos ou ressurreição de heróis lendários como fato; necromancia como traço de povo ou de raça (D-66 limite 2); resposta que declare o Lord vilão ou benfeitor por decreto; resposta que rompa ou reescreva um pacto (o rei não vence os pactos, doc 04 §3).
- **Evidência para resolver:** pareceres do Conselho dos Selos; Muro dos Nomes; relatos dos Pactuários das Despedidas; o que o Lord aceitar dizer, **se aceitar**; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.morvan-voz-baixa`, `npc.dhun-contorno-quieto`, `npc.ilkar-turno-aberto`, `faction.irmandade-das-despedidas`.
- **Relógio e evento:** `clock.despedidas`, `event.divisao-dita-em-voz-alta`.
- **Fonte:** doc 01 parte 4 (MV-02); doc 04; doc 05 §1; doc 06 §5 e §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mv-03-canal-da-partida` — MV-03 — O canal que só aparece para quem parte (Lamaluz)
- **Fatos confirmados:** os canais mudam de lugar com a cheia; famílias querem partir e o pacto manda que fiquem; quem guia é acusado de quebrar o pacto.
- **Hipóteses abertas:** os pilotos conhecem um caminho que não divulgam; o canal é efeito da cheia e quem parte só o nota porque o procura; alguém acende lamparinas para guiar escondido; a história é conforto de quem parte e nunca foi verificada.
- **A verdade não pode ser:** passagem para Aeltharyn ou para outro reino (nenhuma rota nova, contrato §3 item 4); resposta que decida **por que** as famílias partem (as razões não são fixadas, doc 05 §4); culpa do pacto, do rei ou dos Malditos pela partida; prova de que quem guia **quebra** o pacto (a pena e o alcance de L-09 são `desconhecidos`).
- **Evidência para resolver:** Casa dos Pilotos; relatos de Tazk (N-05); lamparinas do Cais; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.tazk-pedra-ao-sol`, `npc.nuvik-canal-torto`, `faction.pilotos-dos-canais`, `faction.guardioes-das-lamparinas`.
- **Relógio e evento:** `clock.partida`, `event.julgamento-de-quem-guia`, `event.cheia-fora-de-epoca`.
- **Fonte:** doc 01 parte 4 (MV-03); doc 04; doc 05 §1; doc 06 §5 e §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mv-04-fardo-sem-dono` — MV-04 — O fardo que ninguém reclamou (Fozbreu)
- **Fatos confirmados:** chega carga pelo rio sem papel que o pacto reconheça; o Armazém dos Fardos Sem Dono guarda carga sem dono; o porto não confere o conteúdo da Descida.
- **Hipóteses abertas:** carga comum perdida e esquecida; contrabando que espera quem a reclame; fardo de alguém que desceu e não pôde voltar; o fardo é lenda de armazém e o armazém sempre esteve vazio nesse ponto.
- **A verdade não pode ser:** explicação do que a Descida faz rio acima ou nas Marcas (doc 05 §3); ligada ao navio sem tripulação (Mistério C) ou a qualquer evento de Aeltharyn; coisa que o rio "repete" do mar como fato (a lenda dos Corvídeos fica viva); culpa dos anões, gnomos, kobolds ou construtos por serem de quem são.
- **Evidência para resolver:** Casa da Barra; Torre das Marés; registro do armazém; Rusk (N-06), que sabe quem é o dono e pode não dizer; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.rusk-rede-da-barra`, `faction.defensores-da-paz`.
- **Relógio e evento:** `clock.fardos`, `event.fardos-suspensos`.
- **Fonte:** doc 01 parte 4 (MV-04); doc 04; doc 05 §1; doc 06 §5 e §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mv-05-poco-salgado` — MV-05 — O poço que mudou de gosto (Beira-Sal)
- **Fatos confirmados:** o posto depende de poucos poços; guias cobram por pactos orais que ninguém prova; caravanas de fora contestam a conta.
- **Hipóteses abertas:** variação natural do poço; desvio de água por um dos lados, sem prova; o guia que voltou sem sede teve ajuda que não conta; o rumor nasceu para justificar a cobrança.
- **A verdade não pode ser:** algo vindo das Marcas das Feras ou do Pacto Humano (nenhum dos dois está escrito, contrato §6); ligada ao Ermo de Sal como causa mágica (o deserto fica sem lore novo); culpa de um povo; resposta que decida o valor de um pacto oral (lacuna de L-05).
- **Evidência para resolver:** Poço Primeiro; sentinelas de sal; Pátio dos Acertos; Teshi (N-08), que **pode ter escondido** quanta água há (suspeita, não fato); pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.vurga-sede-vencida`, `npc.teshi-no-de-poco`, `faction.guias-de-sal`, `faction.casa-dos-pocos`.
- **Relógio e evento:** `clock.acertos-orais`, `event.acertos-orais-suspensos`.
- **Fonte:** doc 01 parte 4 (MV-05); doc 04; doc 05 §1; doc 06 §5 e §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mv-06-clausula-que-vence` — MV-06 — A cláusula que vence (Selovale)
- **Fatos confirmados:** um pacto antigo vence; ninguém sabe o que o fim dele concede, a quem ou contra quem; os Pactuários discordam de qual versão do texto vale; cláusula esquecida pode ser invocada por quem a encontrar.
- **Hipóteses abertas:** o fim devolve algo a quem o pacto prendeu; o fim só encerra uma obrigação formal e nada mais; a cláusula foi alterada numa cópia e o vencimento é de outro texto; o pacto vence e se renova por um ato que ninguém lembra.
- **A verdade não pode ser:** que os selos prendiam "algo" (a lenda dos Moldáveis fica viva, sem confirmação); que a coroa nasceu desse pacto (lenda de Morrovéu, doc 04 §4); ligada a pacto, selo ou desaparecido de Aeltharyn; resposta que escolha qual versão do texto vale.
- **Evidência para resolver:** Arquivo dos Pactos Velhos; Casa das Cláusulas; cópias de selo; Yeshe (N-09), que **já leu** uma cláusula que o Conselho desconhece (suspeita); pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.yeshe-forma-emprestada`, `faction.memoria-das-provacoes`.
- **Relógio e evento:** `clock.pacto-que-vence`, `event.sessao-publica-de-parecer`.
- **Fonte:** doc 01 parte 4 (MV-06); doc 04; doc 05 §1; doc 06 §5 e §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mv-07-quem-chega-sem-guia` — MV-07 — Quem chega sem guia (Borda-Mansa)
- **Fatos confirmados:** gente de longe aparece sem guia e sem registro; o Pacto da Orla manda abrigar quem chega; o posto não sabe quem é quem; a vigia só vê o que a névoa deixa.
- **Hipóteses abertas:** viajantes avulsos de passagem, como em qualquer fronteira; alguém do posto ajuda pessoas a passar sem registro; a névoa desorienta e só parece que chegam sem guia; o número de recém-chegados é percepção da vigia, não fato.
- **A verdade não pode ser:** que a orla liga o Véu ao Bosque dos Sussurros, a Brumafunda ou a qualquer lugar de Aeltharyn (só rumor, contrato §4); resposta que explique o Mistério E ou a Neblina Longa; recém-chegados vindos das Coroas ou de Aeltharyn como fluxo de refugiados (contrato §3 item 11); NPC de Aeltharyn atuando a mando do Véu.
- **Evidência para resolver:** Torre das Fogueiras; Pátio dos Sem Guia; Krell (N-10), que **suspeita** de ajuda de dentro e não afirma; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.krell-eco-da-vigia`, `faction.vigias-da-orla`.
- **Relógio e evento:** `clock.orla-sem-guia`, `event.audiencia-sobre-registro`, `event.neblina-cobre-a-orla`.
- **Fonte:** doc 01 parte 4 (MV-07); doc 04; doc 05 §1; doc 06 §5 e §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mv-08-nao-querem-acordar` — MV-08 — Os que não querem acordar (Fio-Longo)
- **Fatos confirmados:** alguns hóspedes não querem ir embora; a Casa diz que é escolha deles e os parentes dizem que não; ninguém é atacado dormindo, por pacto.
- **Hipóteses abertas:** escolha livre de quem encontrou repouso; dependência de remédios de sono, sem má intenção; o pacto do repouso é lido de modo a segurar o hóspede; os parentes querem a volta por interesse próprio.
- **A verdade não pode ser:** ligada à Vigília Longa ou a qualquer desaparecimento de Aeltharyn; o quarto fechado definido de vez; hóspede prisioneiro **provado** sem a Casa ter ouvido o hóspede; culpa dos Oníricos como povo; resposta que decida quem manda ficar ou partir (lacuna de L-06).
- **Evidência para resolver:** Casa dos Fios; Salão das Camas Longas; Tear Grande; Ondra (N-11), que sabe de quem não quer partir e por quê (suspeita); pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.ondra-fio-de-sono`, `faction.casa-dos-fios`.
- **Relógio e evento:** `clock.hospedes`, `event.julgamento-de-hospede`.
- **Fonte:** doc 01 parte 4 (MV-08); doc 04; doc 05 §1; doc 06 §5 e §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mv-09-nome-que-mudou` — MV-09 — O nome que mudou de lugar (Candeia-Morta)
- **Fatos confirmados:** a cidade cuida dos mortos e das despedidas; Dhun (N-12) guarda o Muro dos Nomes; ele suspeita de ter visto um nome mudar de lugar.
- **Hipóteses abertas:** erro de quem grava ou reorganiza o Muro; alguém reescreve nomes de propósito; o nome é de quem morreu longe e a cidade o recoloca quando a notícia chega; o rumor nasceu de uma candeia mal lembrada.
- **A verdade não pode ser:** que o nome é de um desaparecido de Aeltharyn (Sela dos Retornos ou qualquer outro, contrato §3 item 3); ligada à necromancia do Lord como fato (MV-02 segue aberto); mortos que se movem como fato; culpa dos Umbrais ou dos Noctívagos como povos.
- **Evidência para resolver:** Muro dos Nomes; Casa das Despedidas; livros de turno da Ala dos Turnos; Dhun (N-12); pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.dhun-contorno-quieto`, `npc.ilkar-turno-aberto`, `faction.irmandade-das-despedidas`.
- **Relógio e evento:** `clock.despedidas`.
- **Fonte:** doc 01 parte 4 (MV-09); doc 04; doc 05 §1; doc 06 §5 e §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

## Conferências

| Verificação | Resultado |
|---|---|
| Mistérios | 9 (MV-01 a MV-09) |
| Hipóteses abertas por mistério | 4 em cada |
| Mistério com resposta única | 0 |
| Mistério com veto (*a verdade não pode ser*) e prova possível | 9 de 9 |
| Mistério ligado a relógio | 9 de 9 (8 relógios cobertos) |
| ID fora do registro mestre, de relógio ou de evento | 0 |
| NPC falecido como entidade | 0 |
| Campo vazio | 0 |
| Mistério que contradiz fato confirmado ou resolve mistério de Aeltharyn | 0 |

## Decisão pendente

**D-91:** aprovar os 9 mistérios como dados. Opções: **A.** aprovar; **B.** trocar uma hipótese, veto ou ligação (diga qual). **Recomendação: A.**

---

# Fechamento da Fase 5 (F5): testes completos

Suíte local disponível (o validador `plano/ferramentas/validar_reino.py` exige a pasta `dados/` do formato do reino 2, que o reino 3 só gera na Fase 7; não se aplica ainda). Verificações locais sobre os 15 CSV (359 linhas de dados) e os `.md` do reino 3:

| Verificação | Resultado |
|---|---|
| Integridade dos 15 CSV (colunas por linha, campo vazio) | OK; 0 vazio, exceto o registro mestre (144 células, por desenho: `id_catalogo` e campos de pessoa em facção e cidade) |
| IDs de `npc.`, `city.` e `faction.` em F4 e F5 fora do registro mestre | 0 |
| Sementes: 24 (8 P, 8 M, 8 G), ameaça 1 a 4, agentes vivos, relógio ou evento existente | OK |
| Eventos: relógio de `12-relogios.csv` e rota do doc 07 existentes | OK |
| Canais: rota do doc 07 existente | OK |
| Mistérios: 9, 4 hipóteses cada, ligações existentes | OK |
| Reputação cobre 8 lugares e 11 facções | OK |
| Voz = fichas (12); competências no catálogo; 0 aliado-rival; 0 relação sem fonte | OK |
| Termo de fronteira fora de `limite` e `fonte` nos dados de F5 | 0 |
| Prazo, distância ou preço numérico nos dados de F5 | 0 |
| Regressão: 130 arquivos de `reinos/01`, `reinos/02`, `mundo/` e `referencia/` comparados com o ZIP de entrada | **0 alterado** |
| Referências a arquivos em `.md` | 5 alertas, todos caminhos abreviados (`reinos/01-.../`, `reinos/02-.../`) de arquivos que existem; 0 inválida de fato |
