# Azeroth — Mistérios com limites (doc 13, R5-5.5)

**Estado:** R5-5.5, D-165 aceita pela continuação · **Data:** 2026-11-07

**Etiqueta:** tudo é **[PROPOSTO MA-01 a MA-10]**; os mistérios, hipóteses e vetos já estão no doc 01 (parte 4, D-148). Aqui eles viram dados (prefixo `mist.`), com fatos confirmados, evidência e ligação a relógios e eventos. **Fonte:** doc 01 partes 1 a 4; docs 04 e 05; doc 06 §6; `12-conhecimento.csv`, `12-relogios.csv`, `12-eventos.csv`. Dados em `13-misterios-limites.csv` (9 colunas, molde do reino 4).

## Regras

- **Sem resposta única:** cada mistério mantém **quatro hipóteses abertas** compatíveis com os fatos confirmados. Nenhuma é eleita aqui.
- **Limite da verdade:** *a verdade não pode ser* lista o que contradiria fato confirmado, lacuna de lei explícita ou mistério de outro reino. É a fronteira de qualquer resposta futura, não uma resposta.
- **Rumor e suspeita não são fato:** rumores (doc 01) e suspeitas das fichas aparecem só como pista (**Parte de**).
- **Resolver é opcional:** o mistério pode ficar aberto, piorar ou ser resolvido por NPC fora de cena. Resolver exige **evidência cumulativa** (pelo menos duas provas independentes) e registro da narração; a IA não escolhe a resposta (doc 06 §6).
- **Nenhum mistério explica, confirma ou nega** o Mistério C, a Neblina Longa, o Mistério D, o Mistério E ou o Conflito G de Aeltharyn, nem dá "verdade" a mistério do Véu, das Coroas ou do Vale (contrato §6). Esses termos só aparecem em vetos, como no doc 01.
- Mistérios não têm relógio próprio; as ligações são só ao relógio e aos eventos já escritos cuja primeira cidade é a do mistério. Sementes (R5-5.3) e cadeias (R5-5.2) que tocam um mistério herdam estes limites.
- Texto copiado do doc 01 parte 4; nenhum mistério, hipótese ou veto foi acrescentado ou retirado. MA-04 e MA-05 compartilham a costa e a Coroa do Cais; MA-07 e MA-09, a Casa dos Sinais e L-07; nenhum depende do outro.

## Mistérios

### `mist.ma-01-a-medida-refeita-e-o-celeiro-fundo` — MA-01 — A medida refeita e o celeiro fundo (Campoalvo)
- **Parte de:** rumores de Campoalvo (as medidas do salão foram refeitas depois de uma colheita ruim; um celeiro tem mais fundo do que mostra); suspeita de N-01 (guarda em segredo o tamanho real dos celeiros); problema atual de Campoalvo.
- **Fatos confirmados:** o Salão das Medidas fixa a medida do grão e os Celeiros Comuns guardam o excedente (L-01); a Coroa do Trigal exige precedência; quem ajusta a medida em ano de colheita ruim é lacuna.
- **Hipóteses abertas:** (a) a medida foi refeita por desgaste e costume, sem segredo; (b) Odile guarda uma reserva além da conta por prudência; (c) a medida foi ajustada depois de uma colheita ruim para esconder perda; (d) o rumor serve a quem quer abrir os celeiros ou baixar o preço.
- **A verdade não pode ser:** ligado a praga, colheita ou mistério de Aeltharyn ou do Véu; resposta que decida a precedência do Trigal ou quem ajusta a medida (L-01 segue aberta); Odile culpada por decreto sem o Conselho dos Celeiros ouvido; culpa de um povo (Humanos, Pequeninos, Luminares) por ser quem é; causa mágica do celeiro.
- **Evidência para resolver:** Casa das Pesagens; Celeiros Comuns; Salão das Medidas; Odile (N-01), que **pode calar** o tamanho dos celeiros, e Tomlin (N-04), que **pode divergir** dela; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `city.campoalvo`, `npc.odile-medida-cheia`, `npc.tomlin-horta-larga`, `faction.conselho-dos-celeiros`
- **Relógio e evento:** `clock.medida-em-ano-ruim`, `event.medida-dita-em-voz-alta`, `event.colheita-fraca-na-ceifa`
- **Fonte:** doc 01 parte 4 (MA-01); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv

### `mist.ma-02-a-comporta-que-nunca-abriu` — MA-02 — A comporta que nunca abriu (Vaudouro)
- **Parte de:** rumores de Vaudouro (o nível do rio sobe na torre antes de subir no rio; uma comporta nunca foi aberta); suspeita de N-10 (já deixou passar uma barcaça sem taxa); desconhecido de N-10 (por que ninguém lhe pediu que chefiasse a Casa).
- **Fatos confirmados:** a Casa dos Barqueiros cuida das docas, canais e comportas; as comportas não se fecham sem aviso às duas coroas (L-02); Trigal e Cais discutem quem taxa o que passa de um rio ao outro.
- **Hipóteses abertas:** (a) a Torre do Nível marca o rio por costume de pedra e régua, sem segredo; (b) a comporta nunca abriu porque ninguém precisou ou ninguém sabe operá-la; (c) os barqueiros guardam a comporta fechada como moeda de troca na disputa de taxa; (d) o rumor serve a quem quer taxar a passagem.
- **A verdade não pode ser:** poder mágico do rio ou lore novo do Trigueiro e do Corre-Largo; ligado à foz do Argente, à Descida ou a qualquer rio de outro reino (P-3); resposta que decida a quem se paga (L-02 segue aberta); Garvane (N-10) provado parcial sem a Casa ouvida; culpa de Nascidos da Maré, Pequeninos ou Humanos por serem quem são.
- **Evidência para resolver:** Comportas Velhas; Torre do Nível; Pátio das Barcaças; Feira das Pontes; Garvane (N-10), que **pode recusar** dizer por que deixou passar a barcaça; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `city.vaudouro`, `npc.garvane-remo-firme`, `faction.casa-dos-barqueiros`
- **Relógio e evento:** `clock.passagem-dos-rios`, `event.taxa-unica-da-barcaca`
- **Fonte:** doc 01 parte 4 (MA-02); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv

### `mist.ma-03-as-paginas-do-livro` — MA-03 — As páginas do Livro (Torrefronte)
- **Parte de:** rumores de Torrefronte (o livro tem páginas que ninguém lembra de ter escrito; a torre foi levantada em duas etapas por coroas diferentes); suspeita de N-07 (já abriu o limite a quem não devia, em nome do Livro); desconhecido de N-07 (que ordem original sustenta a vigia e quem escreveu as páginas).
- **Fatos confirmados:** o Livro dos Chegados registra quem chega ou parte do limite (L-03); a guarnição do Trigal e a vigia do Vento discordam sobre quem responde por quem chega; quem lê o registro é lacuna.
- **Hipóteses abertas:** (a) as páginas foram escritas em turnos diferentes e ninguém as conferiu; (b) Solenne anotou exceções à parte para proteger chegados; (c) alguém acrescentou páginas para provar a vigia de uma coroa; (d) o rumor nasceu da disputa entre guarnição e vigia e precisa de culpado.
- **A verdade não pode ser:** o Livro ligado a rota, passagem ou duração com o Véu ou Aeltharyn, ou à origem das Fugas do Véu; afirmar que os chegados do Véu passam por ele; resposta que decida quem lê o registro (L-03 segue aberta); Solenne (N-07) provada traidora sem guarnição e vigia ouvidas; culpa de Luminares ou de qualquer chegado por origem.
- **Evidência para resolver:** Livro dos Chegados; Torre da Vigia; Muro de Terra; Pátio das Guardas; Solenne (N-07), que **pode recusar** mostrar as páginas; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `city.torrefronte`, `npc.solenne-vigilia-longa`, `faction.guarnicao-do-limite`
- **Relógio e evento:** `clock.livro-lido-de-dois-modos`, `event.livro-relido-com-testemunha`
- **Fonte:** doc 01 parte 4 (MA-03); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv

### `mist.ma-04-o-navio-fora-do-molhe` — MA-04 — O navio fora do molhe (Alvamar)
- **Parte de:** rumores de Alvamar (a corrente da boca já falhou uma vez e ninguém quis dizer quando; certos navios descarregam sem passar pelo molhe); suspeitas de N-02 (deixou navios descarregarem fora do molhe em troca de apoio), N-05 (sabe de um navio que descarrega fora do molhe) e N-08 (deixou a corrente frouxa sem explicar).
- **Fatos confirmados:** o navio entra pelo molhe e a corrente guarda a boca (L-04); a Alfândega das Marés discute com a Coroa se a taxa serve ao porto ou à Coroa; Dorgan comanda a Guarda da Corrente.
- **Hipóteses abertas:** (a) barcos pequenos descarregam fora do molhe por falta de espaço no cais, sem segredo; (b) Corlan permite a descarga fora do molhe em troca de apoio; (c) a corrente falhou por desgaste e Dorgan evita falar disso; (d) o rumor serve à Alfândega para cobrar mais ou à Coroa para tirar a Alfândega do caminho.
- **A verdade não pode ser:** ligado a Thalanor, ao Mistério C, ao contrabando ou às tripulações de Aeltharyn, ou a navio sem tripulação; causa ou carga que venha do Véu ou da foz do Argente (P-3); resposta que decida se a taxa serve ao porto ou à Coroa (L-04 segue aberta); Corlan, Rilla ou Dorgan culpados por decreto; culpa dos povos do mar por serem quem são.
- **Evidência para resolver:** Molhe Grande; Corrente da Boca; Doca dos Povos do Mar; Casa do Cais; Rilla (N-05) e Dorgan (N-08), que **podem calar** o que viram e por que afrouxaram a corrente; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `city.alvamar`, `npc.corlan-cais-primeiro`, `npc.rilla-espuma-fina`, `npc.dorgan-escudo-lento`, `faction.alfandega-das-mares`, `faction.guarda-da-corrente`
- **Relógio e evento:** `clock.taxa-do-molhe`, `event.regra-de-descarga-no-molhe`
- **Fonte:** doc 01 parte 4 (MA-04); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv

### `mist.ma-05-a-luz-entre-duas-disputas` — MA-05 — A luz entre duas disputas (Enseada-Vigia)
- **Parte de:** rumores de Enseada-Vigia (um farol acende sozinho em certas noites; há uma rota de volta que só os pilotos antigos sabem); suspeita de N-09 (sabe uma rota de volta que não ensinou a ninguém); desconhecido de N-09 (como aprendeu a barra e por que pilota de graça).
- **Fatos confirmados:** o farol se mantém aceso e a barra se avisa a quem chega (L-05); a Coroa do Cais quer os faroleiros a seu mando e a Coroa do Vento quer os sinais da costa; os Pilotos da Enseada pilotam por ofício.
- **Hipóteses abertas:** (a) o farol é aceso por faroleiros em turno sem registro; (b) Nerella guarda uma marcação da barra por ofício, para não perder o trabalho dos pilotos; (c) faroleiros e pilotos combinaram um sinal que ninguém contou às coroas; (d) o rumor serve ao Vento para dizer que a luz precisa de sinal seu.
- **A verdade não pode ser:** farol com poder próprio ou lore novo do mar; a rota de volta ligada a Thalanor, ao Mistério C, ao fluxo As Costas como causa, ou a qualquer rota de outro reino; resposta que decida de quem são os faróis (L-05 segue aberta); Nerella (N-09) provada espiã de uma coroa; culpa de Sereianos, Tartarinos ou Abissais por serem quem são.
- **Evidência para resolver:** Farol do Sul; Casa dos Pilotos; Pedra da Barra; Cais dos Recém-Chegados; Nerella (N-09), que **pode recusar** ensinar a rota; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `city.enseada-vigia`, `npc.nerella-canto-da-barra`, `faction.pilotos-da-enseada`, `faction.irmandade-dos-faroleiros`
- **Relógio e evento:** `clock.luz-entre-coroas`, `event.luz-com-duas-coroas-avisadas`
- **Fonte:** doc 01 parte 4 (MA-05); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv

### `mist.ma-06-o-casco-sem-encomenda` — MA-06 — O casco sem encomenda (Concharia)
- **Parte de:** rumores de Concharia (um casco ficou pronto sem que ninguém o encomendasse; o coral cresce para onde o canal ia ser aberto); problema atual de Concharia.
- **Fatos confirmados:** o jardim e o santuário ficam em paz e o estaleiro serve a quem paga dentro dessa regra (L-06); a Coroa do Cais quer abrir mais um canal e os Guardas do Santuário dizem que isso mata o jardim.
- **Hipóteses abertas:** (a) o casco foi encomendado e esquecido, sem mistério; (b) os Construtores de Concha adiantaram o casco para forçar o canal maior; (c) o coral cresce por natureza e o rumor leu intenção nele; (d) os Guardas do Santuário espalham o rumor para travar o canal.
- **A verdade não pode ser:** o coral ou o casco com causa mágica ou lore novo do mar; casco ligado a navio sem tripulação, ao Mistério C ou a qualquer navio de Aeltharyn; resposta que decida se o canal fere o santuário (L-06 segue aberta); Orrume ou Corali provados de má-fé sem os dois lados ouvidos; culpa de Tartarinos ou Sereianos por serem quem são.
- **Evidência para resolver:** Estaleiro Grande; Jardim de Coral; Canal Estreito; Santuário da Enseada; Orrume Abrigo-de-Casco e Corali Fundo-de-Coral, que **podem divergir** sobre o casco e o coral; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `city.concharia`, `npc.orrume-abrigo-de-casco`, `npc.corali-fundo-de-coral`, `faction.construtores-de-concha`, `faction.guardas-do-santuario`
- **Relógio e evento:** `clock.canal-e-jardim`, `event.canal-ou-jardim`
- **Fonte:** doc 01 parte 4 (MA-06); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv

### `mist.ma-07-o-codigo-mudado` — MA-07 — O código mudado (Torreclara)
- **Parte de:** rumores de Torreclara (um sinal antigo ainda é repetido por uma torre que ninguém cuida; um código foi mudado sem aviso); suspeita de N-03 (já mudou um código de torre sem avisar as outras coroas); desconhecido de N-03 (quem guarda o código-mestre além dele).
- **Fatos confirmados:** o código de cada torre é guardado na Casa dos Sinais e o aviso corre a quem paga (L-07); as outras duas coroas temem depender de quem pode calar uma torre; quem é avisado quando um código muda é lacuna.
- **Hipóteses abertas:** (a) o sinal antigo é repetido por costume ou mecanismo da torre, sem segredo; (b) a Casa dos Sinais mudou o código por rotina e esqueceu de avisar; (c) Brevo mudou o código para proteger a rede do Vento; (d) o rumor circula para que Trigal e Cais exijam os códigos.
- **A verdade não pode ser:** o sinal antigo vindo de Aeltharyn, do Véu ou ligado à Neblina Longa; explicação de qualquer fenômeno ou desaparecimento de outro reino; resposta que decida quem é avisado quando um código muda (L-07 segue aberta); Brevo (N-03) provado traidor sem a Casa dos Sinais ouvida; culpa de Nascidos do Vento por serem quem são.
- **Evidência para resolver:** Torre-Mestra; Pátio dos Códigos; Mesa dos Mapas; Cata-Vento Grande; Brevo (N-03), que **pode calar** quem guarda o código-mestre; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `city.torreclara`, `npc.brevo-rumo-alto`, `faction.casa-dos-sinais`
- **Relógio e evento:** `clock.codigo-sem-aviso`, `event.aviso-de-codigo-combinado`
- **Fonte:** doc 01 parte 4 (MA-07); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv

### `mist.ma-08-a-estrela-sem-registro` — MA-08 — A estrela sem registro (Estrelado)
- **Parte de:** rumores de Estrelado (um mapa mostra uma estrela que nenhum registro lista; a cúpula se move sozinha); suspeita de N-06 (leu no céu um sinal que não contou a Brevo); problema atual de Estrelado.
- **Fatos confirmados:** o calendário de rota e a previsão saem da Mesa dos Astros (L-08); Estelares e Luminares discordam sobre quem interpreta o que o céu diz; as coroas pedem previsões que sirvam a cada uma.
- **Hipóteses abertas:** (a) a estrela é erro de anotação num mapa antigo; (b) a cúpula se move por mecanismo e costume de turno, sem segredo; (c) os Estelares guardam uma leitura própria que os Luminares não aceitam; (d) o rumor alimenta o pedido de previsões por parte das coroas.
- **A verdade não pode ser:** a estrela ou o movimento da cúpula ligados à Neblina Longa, ao Mistério D, ao Mistério E ou ao céu de outro reino; previsão de desastre dada como fato; resposta que decida quem interpreta o céu (L-08 segue aberta); Astrel (N-06) provado de ter escondido ordem de Brevo sem os dois ouvidos; Estelares ou Luminares tratados como ameaça por origem.
- **Evidência para resolver:** Arquivo dos Astros; Cúpula Grande; Poço do Céu; Pátio das Rotas; Astrel (N-06), que **pode calar** o que leu, e Ixra Noite-Aberta e Eloh Cúpula-Acesa, que **podem divergir** sobre o mapa; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `city.estrelado`, `npc.brevo-rumo-alto`, `npc.astrel-noite-clara`, `npc.eloh-cupula-acesa`, `npc.ixra-noite-aberta`, `faction.leitores-do-ceu`, `faction.vigilia-da-cupula`
- **Relógio e evento:** `clock.leitura-do-ceu`, `event.duas-leituras-do-ceu-na-mesa`
- **Fonte:** doc 01 parte 4 (MA-08); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv

### `mist.ma-09-a-pipa-com-mensagem` — MA-09 — A pipa com mensagem (Pouso-das-Brisas)
- **Parte de:** rumores de Pouso-das-Brisas (uma pipa apareceu presa na torre com uma mensagem que ninguém enviou; o vento daqui muda a pedido); suspeita de N-03 (já mudou um código de torre sem avisar as outras coroas); problema atual do Pouso.
- **Fatos confirmados:** o Posto dos Pipeiros cuida da linha de torres e pousos entre o interior e a costa; as outras coroas desconfiam de que a linha serve só ao Vento e pedem sinais próprios; o código de cada torre é guardado na Casa dos Sinais (L-07).
- **Hipóteses abertas:** (a) a mensagem foi enviada por um pipeiro e esquecida na torre; (b) a pipa é de um viajante e ficou presa sem dono; (c) alguém usa pipas para um sinal fora da Casa dos Sinais; (d) o rumor serve a quem quer sinais próprios para sua coroa.
- **A verdade não pode ser:** mensagem vinda de Aeltharyn, do Véu ou de qualquer reino; conteúdo da mensagem decidido; o vento obedecendo a pedido (magia ou lore novo); resposta que decida se outra coroa pode ter sinais próprios (L-07 segue aberta); Nascidos do Vento, Pequeninos viajantes ou Goblins visitantes culpados por serem quem são.
- **Evidência para resolver:** Oficina de Pipas; Rampa dos Sinais; Torre do Meio; Pouso Largo; Hollis Torre-do-Meio, que **pode calar** quem ensina o fio das pipas; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `city.pouso-das-brisas`, `npc.brevo-rumo-alto`, `npc.hollis-torre-do-meio`, `faction.posto-dos-pipeiros`
- **Relógio e evento:** `clock.sinais-proprios`, `event.sinal-dividido-na-linha`
- **Fonte:** doc 01 parte 4 (MA-09); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv

### `mist.ma-10-a-chave-copiada` — MA-10 — A chave copiada (Meiovau)
- **Parte de:** rumores de Meiovau (uma das chaves foi copiada; a Mesa já decidiu uma vez sem que as três estivessem presentes); suspeita de N-11 (sabe qual chave pode ter sido copiada) e de N-12 (sustenta o albergue com doação que não diz de onde vem); desconhecido de N-11 (o que a Mesa decidiu sem as três).
- **Fatos confirmados:** a Mesa só abre com as três chaves e nenhuma coroa possui Meiovau (L-09); o Costume da Porta acolhe quem chega (L-10) e as coroas discordam sobre prazo e sustento; a Mesa Estreita e o Albergue dos Chegados são rivais.
- **Hipóteses abertas:** (a) uma chave foi refeita por desgaste e a cópia ficou como reserva de guarda; (b) uma coroa copiou a sua para não ficar sem voz; (c) a Mesa decidiu uma vez sem as três por urgência, com consentimento falado; (d) o rumor serve a quem quer reabrir a decisão tomada.
- **A verdade não pode ser:** a cópia ou a doação com origem ou causa no Véu, em Aeltharyn ou em qualquer reino; os chegados (Nascidos do Crepúsculo, Escamados, Noctívagos) culpados por origem; decidir qual chave foi copiada ou qual coroa a copiou; resposta que decida a precedência entre coroas ou os pontos em aberto de L-09 e L-10; Tolla (N-11) ou Auriven (N-12) provadas de má-fé sem a Mesa e o Albergue ouvidos.
- **Evidência para resolver:** Casa das Três Chaves; Pátio das Audiências; Albergue dos Chegados; Feira Aberta; Tolla (N-11) e Auriven (N-12), que **podem calar** a chave e a origem da doação; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `city.meiovau`, `npc.tolla-mesa-posta`, `npc.auriven-porta-acesa`, `faction.albergue-dos-chegados`, `faction.mesa-estreita`
- **Relógio e evento:** `clock.acolhida-de-meiovau`, `event.prazo-e-sustento-do-chegado`, `event.chegado-sem-registro-na-porta`
- **Fonte:** doc 01 parte 4 (MA-10); doc 04; doc 05; doc 06 §6; 12-conhecimento.csv; 12-relogios.csv; 12-eventos.csv

## Conferências

| Verificação | Resultado |
|---|---|
| Mistérios no CSV | 10 (um por lugar) |
| Hipóteses por mistério | 4 |
| ID fora do registro mestre, relógio ou evento inexistente, ou NPC falecido | 0 |
| Campo vazio | 0 |
| Hipótese eleita como resposta | 0 |
| Mistério que contradiz fato confirmado ou mistério de outro reino | 0 |
| NPC, lugar, facção, rota, número ou tempo novo | 0 |

## Decisão pendente

**D-165:** aprovar os 10 mistérios como dados, com limites e evidência. Opções: **A.** aprovar; **B.** trocar hipótese, veto ou prova (diga qual). **Recomendação: A.**
