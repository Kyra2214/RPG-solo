# 13 — Mistérios com limites (R4-5.5)

**Estado:** rascunho de R4-5.5 para aprovação do autor (D-128) · **Data:** 2026-11-07

**Etiqueta:** tudo é **[PROPOSTO MPL-01 a MPL-10]**; os mistérios, hipóteses e vetos já estão no doc 01 (parte 4, D-111). Aqui eles viram dados (prefixo `mist.`), com fatos confirmados, evidência e ligação a relógios e eventos. **Fonte:** doc 01 partes 1 a 4; doc 04; doc 05; doc 06 §6; `12-conhecimento.csv`, `12-relogios.csv`, `12-eventos.csv`. Dados em `13-misterios-limites.csv`. Molde: `reinos/03-veu-dos-malditos/13-misterios-limites.md`.

## Regras

- **Sem resposta única:** cada mistério mantém **quatro hipóteses abertas** compatíveis com os fatos confirmados. Nenhuma é eleita aqui.
- **Limite da verdade:** *a verdade não pode ser* lista o que contradiria fato confirmado, lacuna de lei explícita ou mistério de outro reino. É a fronteira de qualquer resposta futura, não uma resposta.
- **Rumor e suspeita não são fato:** rumores (doc 01) e suspeitas das fichas (`12-conhecimento.csv`) aparecem só como pista (**Parte de**).
- **Resolver é opcional:** o mistério pode ficar aberto, piorar ou ser resolvido por NPC fora de cena. Resolver exige **evidência cumulativa** (pelo menos duas provas independentes) e registro da narração; a IA não escolhe a resposta (doc 06 §6).
- **Nenhum mistério explica, confirma ou nega** a Neblina Longa, o Mistério D, o Mistério E ou o Conflito G de Aeltharyn, a Descida ou Brasalume (Coroas), nem o fardo e o poço do Véu (contrato §5, itens 1, 3, 5 e 6). Esses termos só aparecem em vetos, como no doc 01.
- Mistérios não têm relógio próprio; as ligações abaixo são só às situações já escritas (`12-relogios.csv`, `12-eventos.csv`). Sementes (R4-5.3) e cadeias (R4-5.2) que tocam um mistério herdam estes limites.
- Hipóteses, vetos e provas seguem o texto do doc 01 parte 4; nenhum mistério, hipótese ou veto foi acrescentado ou retirado. MPL-01 e MPL-02 compartilham Tabuleiro-Velho e L-07; MPL-06 e MPL-08, o Terreiro-Maior e a sucessão; nenhum depende do outro.
- Rumores sem mistério (o marco do Ermo que nunca está no mesmo lugar; a "terra de árvores muito altas") seguem como rumor, sem hipótese.

## Mistérios

### `mist.mpl-01-marco-sem-cla` — MPL-01 — O marco sem clã (Tabuleiro-Velho)
- **Parte de:** rumor de Tabuleiro-Velho (um marco no campo que ninguém sabe a que clã pertence); Campo dos Marcos; era dos Campos Sem Marco.
- **Fatos confirmados:** o Campo dos Marcos guarda os marcos dos pastos pactuados entre clãs; os marcos são renovados quando o campo muda (L-02); a reserva não tem regra para ano ruim.
- **Hipóteses abertas:** (a) marco de um clã que se desfez e cujo pacto ninguém renovou; (b) marco movido ou plantado por alguém para reclamar pasto; (c) marco da 1ª era, anterior aos pactos, que ninguém soube ler; (d) rumor de quem perdeu pasto e procura culpado.
- **A verdade não pode ser:** marco de Aeltharyn, das Coroas ou do Véu, ou posto por quem de lá viesse; resposta que decida o direito à reserva (L-07 segue aberta); culpa de um só povo ou de um só clã sem prova; prova de que Moura (N-04) o escondeu.
- **Evidência para resolver:** Campo dos Marcos; relato dos Guardiões da Reserva; Moura Mel-Guardado (N-04), que **pode recusar** conferir o marco.; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.moura-mel-guardado`, `faction.guardioes-da-reserva`.
- **Relógio e evento:** `clock.reserva-em-ano-ruim`, `event.assembleia-diz-o-que-o-celeiro-deve`, `event.celeiro-guardado-por-poucos`.
- **Fonte:** doc 01 parte 4 (MPL-01); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mpl-02-conta-que-nao-fecha` — MPL-02 — A conta que não fecha (Tabuleiro-Velho)
- **Parte de:** rumor (câmara do celeiro mais fria que o resto); suspeita de N-04 (guarda parte da reserva fora da conta); desconhecido de N-05 (de onde vem a reserva dela); problema atual de Tabuleiro-Velho.
- **Fatos confirmados:** o celeiro guarda grão e carne seca para os anos ruins; Pocha (N-05) distribui reserva própria a clãs que o celeiro não atende; guardiões acusam Pocha de enfraquecer o celeiro; quem tem direito à reserva é `desconhecido`.
- **Hipóteses abertas:** (a) a câmara é fria só por ser de pedra, sem segredo; (b) Moura guarda uma reserva de emergência fora da conta por prudência; (c) a conta do celeiro está errada por costume, sem má-fé; (d) alguém desvia reserva, e a suspeita sobre Moura e Pocha serve a quem desvia.
- **A verdade não pode ser:** que a reserva de Pocha ou a câmara venha da Descida, das Coroas, de Brasalume ou de qualquer lugar fora do Vale (contrato C5); Moura ou Pocha culpados **por decreto** sem a Assembleia ouvir; resposta que decida quem tem direito à reserva (L-07); culpa de um povo (Ursinos, Porcinos) por ser quem é.
- **Evidência para resolver:** Celeiro Comprido; Cisterna do Planalto; relato da Mesa da Partilha; Moura e Pocha (N-04 e N-05), que **podem calar** o que guardam.; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.moura-mel-guardado`, `npc.pocha-ninhada-farta`, `faction.guardioes-da-reserva`, `faction.mesa-da-partilha`.
- **Relógio e evento:** `clock.reserva-em-ano-ruim`, `event.assembleia-diz-o-que-o-celeiro-deve`, `event.pasto-fraco-na-estacao`, `event.celeiro-guardado-por-poucos`.
- **Fonte:** doc 01 parte 4 (MPL-02); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mpl-03-pedra-que-mudou` — MPL-03 — A pedra que mudou de lugar (Rasovau)
- **Parte de:** rumores de Rasovau (uma pedra do vau mudou de lugar; recado entregue a quem ainda não tinha chegado); suspeita de N-09 (guarda os recados que preferiu não entregar); desconhecido de N-08 (a quem pertencem as pedras).
- **Fatos confirmados:** os dois grupos cobram pela mesma passagem; o vau não se fecha (L-04); a Tenda dos Recados guarda recados; Quira (N-09) leva recados entre grupos e clãs que não se falam.
- **Hipóteses abertas:** (a) a cheia do Argente move as pedras e a lembrança de quem as cuida não acompanha; (b) um dos grupos move pedras para ficar com a passagem; (c) os recados "adiantados" são recados lidos por alguém antes de a pessoa chegar, e a Tenda é menos fechada do que parece; (d) os dois rumores nasceram de uma disputa que precisa de culpado.
- **A verdade não pode ser:** que o Argente ou o vau tenham propriedade mágica (o rio é o de M-04 G-06; nenhum lore novo); causa ou efeito da Descida; resposta que decida a quem pertencem as pedras (L-04 segue aberta); Quira provada espiã de um dos lados sem os dois serem ouvidos; culpa de um povo (Goblins, Leporinos, Vulpinos).
- **Evidência para resolver:** Pedras do Vau; Tenda dos Recados; Cais de Cordas; Zinu (N-08) e Lumbo Pedra-de-Baixo (líder da facção F-07), que **podem não dizer** o que mudaram.; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.zinu-troca-justa`, `npc.quira-recado-certo`, `npc.lumbo-pedra-de-baixo`, `faction.pedras-de-cima`, `faction.pedras-de-baixo`.
- **Relógio e evento:** `clock.dupla-cobranca`, `event.quem-recebe-a-passagem`.
- **Fonte:** doc 01 parte 4 (MPL-03); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mpl-04-degrau-a-mais` — MPL-04 — O degrau a mais (Praia-Pardo)
- **Parte de:** rumores de Praia-Pardo (visitantes que descem sem nunca terem subido; um degrau a mais na rampa a cada estação); suspeita de N-10 (já recusou carga por um motivo que não explicou); desconhecido de N-10 (o que mais chega que ninguém registra).
- **Fatos confirmados:** o Argente entra nas planícies em Praia-Pardo; o pouso recebe carga com papel que as Coroas reconhecem e o Vale não; Vrissk (N-10) não pergunta de onde vem a carga; gnomos e construtos passam só como visitantes (D-103).
- **Hipóteses abertas:** (a) quem "desce sem ter subido" é gente que entrou no rio rio acima do pouso e o pouso só viu a chegada; (b) a rampa é refeita pelos clãs a cada estação e o degrau é lembrança de obra; (c) o motivo da recusa de Vrissk é de trato com um clã, não de carga; (d) o rumor nasceu do que o pouso não consegue medir (a chegada em número que ele não sabe contar).
- **A verdade não pode ser:** causa, origem, número ou fim da Descida do Argente; ligada ao fardo sem dono de Fozbreu ou a qualquer mistério do Véu; ligada à origem do combustível ou do alimento de Brasalume; resposta sobre o que a Descida faz rio acima; culpa de anões, kobolds, gnomos ou construtos por serem de quem são; fluxo de refugiados (contrato §5, item 12).
- **Evidência para resolver:** Rampa de Pedra; Mastro dos Avisos; Barracão dos Recém-Chegados; Vrissk (N-10), que **pode recusar** dizer por que recusou a carga.; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.vrissk-colonia-quieta`, `faction.receptores-do-pouso`, `faction.balanca-do-pouso`.
- **Relógio e evento:** `clock.desembarque`, `event.regra-da-rampa`.
- **Fonte:** doc 01 parte 4 (MPL-04); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mpl-05-cisterna-salgada` — MPL-05 — A cisterna que ficou salgada (Alvasal)
- **Parte de:** rumor de Alvasal (a cisterna ficou mais salgada depois de uma caravana partir); suspeita de N-03 (já negou água a quem tinha direito); problema atual de Alvasal.
- **Fatos confirmados:** Alvasal depende de uma só cisterna; ela não serve a todas as caravanas ao mesmo tempo; a preferência nunca foi posta em pacto (doc 04 §10, item 4); Gruzk (N-03) comanda os turnos de vigia.
- **Hipóteses abertas:** (a) variação natural da água, que o rumor ligou à caravana por coincidência; (b) uso pesado de uma caravana e pouca reposição; (c) algum desvio de água, de um lado ou de outro, sem prova; (d) o rumor serve a quem quer a preferência na cisterna.
- **A verdade não pode ser:** ligada ao Ermo de Sal como causa mágica ou de lore novo (o deserto fica sem lore); algo vindo do poço de Beira-Sal ou de qualquer mistério do Véu (nenhum lado dá "verdade" ao outro); culpa de goblins, ratoides ou Goblins de Legião por raça; resposta que decida a preferência entre caravanas (L-06 segue aberta); rota ou duração entre Alvasal e o Ermo.
- **Evidência para resolver:** Cisterna Branca; Cerca de Sal; Pátio das Cargas; Gruzk (N-03) e Aruun Poço-Compartilhado (Trato do Poço), que **podem divergir** sobre quanta água há.; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.gruzk-estandarte-baixo`, `npc.aruun-poco-compartilhado`, `faction.vigias-da-cisterna`.
- **Relógio e evento:** `clock.cisterna`, `event.preferencia-na-cisterna`, `event.caravanas-juntas-na-cisterna`.
- **Fonte:** doc 01 parte 4 (MPL-05); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mpl-06-pedra-que-esfria` — MPL-06 — A pedra que esfria (Terreiro-Maior)
- **Parte de:** rumor do Terreiro-Maior (a Pedra da Palavra esfria quando alguém mente); suspeita de N-02 (sabe de um costume da Pedra que a Assembleia esqueceu); desconhecido de N-02 (o que a Pedra registra e quem a escolheu para o cargo).
- **Fatos confirmados:** a Pedra da Palavra existe e dá a palavra na rodada; Yorna (N-02) cuida da ordem e **não vota, não escolhe o Capitão**; a reunião só se fecha quando todos foram ouvidos (L-01).
- **Hipóteses abertas:** (a) a pedra é fria por natureza e o costume leu nela o que quis; (b) Yorna conhece um gesto que faz a pedra "esfriar" e o usa para pesar a palavra; (c) a Assembleia deixou a lenda correr para impor honestidade, sem que a pedra faça nada; (d) a pedra esfria de fato e ninguém a testou fora do Fogo do Meio.
- **A verdade não pode ser:** prova que decida a sucessão (prova ou aclamação) ou dê voto à Pedra; magia ou lore de objeto de Aeltharyn, das Coroas ou do Véu; Yorna acusada de manipular a Assembleia sem os clãs ouvidos; culpa dos Orcanos como povo; a Pedra substituindo a regra de maioria (doc 04 §2.2).
- **Evidência para resolver:** Pedra da Palavra; Fogo do Meio; relato de Cassan (N-12), que ouve todos e **não toma lado em público**; Yorna, que **pode não revelar** o costume.; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.yorna-palavra-firme`, `npc.cassan-galhada-velha`.
- **Relógio e evento:** `clock.sucessao-do-capitao`, `event.declaracao-no-fogo-do-meio`, `event.terreiro-cheio`.
- **Fonte:** doc 01 parte 4 (MPL-06); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mpl-07-mapa-do-capitao` — MPL-07 — O mapa do Capitão e o terceiro campo (Vazante)
- **Parte de:** rumor da Vazante (o Capitão carrega um mapa que ninguém mais leu; a Marcha nunca dorme duas vezes no mesmo campo); suspeitas de N-01 (já decidiu quem o suceda) e de N-07 (já viu um terceiro campo e não o disse); desconhecido de N-07 (o que viu da última vez que se afastou da Marcha).
- **Fatos confirmados:** a marcha escolhe entre dois campos e cada escolha deixa um clã sem pasto; Rukhan (N-07) bate os campos e **não decide o campo**; Kharos (N-01) escolhe o campo e entrega o mandato inteiro.
- **Hipóteses abertas:** (a) o mapa é só memória de campos usados, sem segredo; (b) o mapa traz marcas de pasto que o Capitão não quer disputar antes da hora; (c) o terceiro campo existe e Rukhan o guarda para um clã que não pode ficar sem pasto; (d) o terceiro campo é desejo de Rukhan, não campo.
- **A verdade não pode ser:** mapa que mostre rota, passagem ou duração para Aeltharyn, as Coroas ou o Véu (contrato §5, item 4); o campo "certo" decidido de vez; inimigo externo ou ameaça por raça (doc 01 parte 3 §5); Kharos ou Rukhan traidores por decreto; ligado ao Conflito G ou a trilhas centauras de Aeltharyn.
- **Evidência para resolver:** Tenda do Capitão; Tablado dos Avisos; relato dos Guardas da Marcha; Rukhan e Kharos, que **podem calar** o que viram e o que decidiram.; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.kharos-casco-de-marcha`, `npc.rukhan-retorno-certo`, `faction.guardas-da-marcha`.
- **Relógio e evento:** `clock.campo-da-marcha`, `event.escolha-do-campo`, `event.pasto-fraco-na-estacao`.
- **Fonte:** doc 01 parte 4 (MPL-07); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mpl-08-campo-nunca-escolhido` — MPL-08 — O campo que nunca mais foi escolhido (Terreiro-Maior)
- **Parte de:** rumor do Terreiro-Maior (um campo escolhido uma vez nunca mais foi escolhido); suspeita de N-12 (sabe qual clã apoia qual lado); desconhecido de N-12 (por que a Ramada nunca fica vazia).
- **Fatos confirmados:** a Assembleia escolhe o campo a cada ciclo, sempre ao alcance dos clãs em marcha; o terreiro é sazonal e muda de campo; quem decide a sucessão é a Assembleia (L-03).
- **Hipóteses abertas:** (a) o pasto daquele campo se esgotou e ninguém quis voltar; (b) um pacto foi quebrado ali e o costume evita o lugar; (c) o campo é lembrado como lugar de perda de uma disputa e respeitado como tal; (d) o rumor nasceu de um campo trocado por conveniência, sem regra.
- **A verdade não pode ser:** campo ligado à Neblina Longa, ao Mistério D, ao Mistério E ou a lugares "mortos" de qualquer outro reino; causa de lore novo do solo ou do clima; que a Assembleia esconda um campo para decidir a sucessão; resposta que escolha o próximo campo; culpa de um clã ou povo.
- **Evidência para resolver:** Pedra da Palavra; relato de Yorna (N-02) sobre costumes da Assembleia; a Ramada dos Visitantes; Cassan (N-12), que **pode saber** e não dizer.; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.yorna-palavra-firme`, `npc.cassan-galhada-velha`.
- **Relógio e evento:** `clock.sucessao-do-capitao`, `event.terreiro-cheio`, `event.declaracao-no-fogo-do-meio`.
- **Fonte:** doc 01 parte 4 (MPL-08); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mpl-09-rumo-trocado` — MPL-09 — O rumo trocado (Encosta-Aberta)
- **Parte de:** rumores de Encosta-Aberta (a encosta fala de uma terra de árvores muito altas do outro lado; quem sobe volta com o rumo trocado); suspeita de N-06 (já ouviu do outro lado mais do que conta); desconhecido de N-06 (por que escolheu ficar do lado de cá).
- **Fatos confirmados:** o pouso fica no pé das elevações ocidentais; o guia leva o rebanho até o pé das elevações e **não além** (L-09); o que há do outro lado é rumor e não é do Vale; os Corredores Largos saem por aqui como visita individual (doc 05 §5).
- **Hipóteses abertas:** (a) quem sobe volta cansado e atribui ao caminho o que foi falta de descanso; (b) o rumo "trocado" é jeito de dizer que a pessoa mudou de ideia; (c) um dos clãs espalha o rumor para afastar o outro das trilhas; (d) Tessun ouviu de viajantes avulsos mais do que quer contar e o rumor cresceu dessa reserva.
- **A verdade não pode ser:** explicação da Neblina Longa, do Mistério D ou do Mistério E; resposta ou causa do Conflito G; passagem, rota ou duração nova para Aeltharyn; centauros, felídeos ou cervinos de Aeltharyn como parentes, emissários ou agentes do Vale, ou o contrário; Oranna Passo-Largo ou outro NPC de Aeltharyn atuando por causa do rumor.
- **Evidência para resolver:** Pedra de Vigia; Pouso dos Cascos; Poço Corrido; Tessun (N-06) e Sendro Clareira-Vigia, que **podem divergir** sobre o que se ouve do outro lado.; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.tessun-salto-calado`, `npc.sendro-clareira-vigia`, `faction.guias-da-encosta`, `faction.vigias-das-trilhas`.
- **Relógio e evento:** `clock.guia-das-trilhas`, `event.guia-combinado`.
- **Fonte:** doc 01 parte 4 (MPL-09); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

### `mist.mpl-10-capim-em-circulo` — MPL-10 — O capim em círculo (Gramal)
- **Parte de:** rumores de Gramal (um campo onde o capim cresce em círculo; um mourão fincado de um lado e achado do outro); suspeita de N-11 (já favoreceu os clãs de gado no último acerto); desconhecido de N-11 (qual lado tem razão sobre o capim).
- **Fatos confirmados:** clãs de gado e clãs de caça dizem que o mesmo campo é deles e cada lado acusa o outro de estragar o pasto; o campo se divide por pacto de pasto (L-08); pasto fraco vira disputa depressa.
- **Hipóteses abertas:** (a) o círculo é efeito do rodízio de pastagem e da marca dos rebanhos, sem mistério; (b) o mourão foi movido por um dos lados para ampliar o campo; (c) a caça abriu clareiras que o gado depois cobriu, e o círculo é de duas mãos; (d) o rumor foi inventado por quem precisa de culpado.
- **A verdade não pode ser:** causa mágica, de lore novo do solo ou ligada ao Ermo de Sal; resposta que decida quem tem razão sobre o capim (L-08 segue aberta); Tibbe (N-11) provado parcial sem os dois lados ouvidos; culpa dos Insetoides, Leporinos, Porcinos ou Ursinos por serem de quem são; qualquer conexão com os mistérios de outros reinos.
- **Evidência para resolver:** Aguada Larga; Tenda de Marcas; Mourão Velho; Cerca de Parição; Tibbe (N-11) e Ghorla Fartura-Dividida, que **podem divergir** sobre onde caiu a marca.; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.tibbe-salto-curto`, `npc.ghorla-fartura-dividida`, `faction.clas-do-gado`, `faction.clas-da-caca`.
- **Relógio e evento:** `clock.capim-disputado`, `event.divisao-do-campo`, `event.pasto-fraco-na-estacao`.
- **Fonte:** doc 01 parte 4 (MPL-10); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv.

## Conferências

| Verificação | Resultado |
|---|---|
| Mistérios | 10 (MPL-01 a MPL-10) |
| Hipóteses abertas por mistério | 4 em cada |
| Mistério com resposta única | 0 |
| Mistério com veto (*a verdade não pode ser*) e prova possível | 10 de 10 |
| Mistério ligado a relógio | 10 de 10 (8 relógios cobertos) |
| ID fora do registro mestre, de relógio ou de evento | 0 |
| NPC falecido como entidade | 0 |
| Campo vazio | 0 |
| Termo de fronteira em hipótese | 0 (em fato confirmado só o do doc 05: papel de carga reconhecido pelas Coroas, MPL-04) |
| Mistério que contradiz fato confirmado ou resolve mistério de outro reino | 0 |
| Texto = doc 01 parte 4 (hipóteses, vetos, provas) | idêntico |

## Decisão pendente

**D-128:** aprovar os 10 mistérios como dados. Opções: **A.** aprovar; **B.** trocar uma hipótese, veto ou ligação (diga qual). **Recomendação: A.**

---

# Fechamento da Fase 5 (F5): testes completos

Suíte local disponível: `plano/ferramentas/validar_reino.py` (reinos 2 e 3) e verificações por script sobre os 15 CSV e os `.md` do reino 4 (o formato `dados/` do reino 4 só é gerado na Fase 7).

| Verificação | Resultado |
|---|---|
| Validador do reino 2 | 0 erro, 40 avisos, 7 exceções (contagens idênticas às salvas) |
| Validador do reino 3 | 0 erro, 22 avisos, 6 exceções (contagens idênticas às salvas) |
| 3 scripts de `plano/ferramentas/` | compilam |
| Integridade dos 15 CSV do reino 4 (360 linhas de dados: colunas por linha, campo vazio) | 0 coluna fora, 0 vazio (exceto o registro mestre, por desenho) |
| IDs de `npc.`, `city.` e `faction.` em F4 e F5 fora do registro mestre | 0 |
| IDs de `clock.`, `event.` e `route.` inexistentes (relógios, eventos, sementes, canais, reputação, mistérios) | 0 |
| Sementes 24 (8 P, 8 M, 8 G); reputação 22; canais 11; voz = fichas (12) | OK |
| Mistérios: 10, 4 hipóteses cada, ligações existentes, 0 falecido | OK |
| Prazo, distância ou preço numérico nos dados de F5 | 0 |
| Referências a arquivos nos `.md` do reino 4 | 0 inválida |
| Regressão: reinos 1 a 3, `mundo/`, `referencia/`, `referencia-tecnica/` e `plano/ferramentas/` comparados com o ZIP de entrada | **0 alterado** |
