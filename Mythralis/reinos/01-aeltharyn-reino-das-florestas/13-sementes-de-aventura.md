# 13 — Sementes de aventura (M4.3)

**Fonte:** doc 01 §5, §9, §13, §14; doc 03; doc 06 §1 a §8; `12-assentamentos.csv`, `12-relogios.csv`, `12-eventos.csv`, `12-registro-mestre.csv`. Dados em `13-sementes-de-aventura.csv`.

## Regras

- 30 entidades (10 cidades, 20 assentamentos) × 3 sementes = **90**: uma **pequena (P)**, uma **média (M)**, uma **grande (G)**. ID: `seed.<entidade>.<p|m|g>` (prefixo novo em `MODELO_DE_REINO.md`).
- Semente é **situação jogável**, não fato canônico (doc 06 §2.4). Não altera estado, relógio nem relação até ser resolvida por ação, regra ou evento validado.
- **Sem solução predefinida:** a coluna *abordagens possíveis* lista caminhos, não respostas; a causa de cada mistério continua em aberto como no doc 01 §13.
- **Cada semente tem "se ninguém age":** resultado proporcional, sem punição automática (doc 06 §8).
- **Só entidades existentes:** agentes são cidades, assentamentos, facções e NPCs do registro mestre, todos vivos ou sem estado de morte; nenhum NPC com `vivo = não` é usado.
- **Ameaça** (escala 1 a 4, ASSUMINDO) mede o **risco para o personagem**, não a importância do problema para quem vive ali: 1 incômodo ou risco baixo; 2 risco moderado; 3 risco sério ou envolve autoridade; 4 risco alto, pode mudar uma instituição ou um relógio. Faixas: P 1–2, M 2–3, G 3–4. Nenhuma semente tem nível 5. Uma semente P não rebaixa o lugar: o problema continua real para os moradores.
- Hipóteses ficam marcadas como hipótese; ligações com relógios e eventos só quando escritas nas fontes, ou marcadas como "não provada".
- Lacuna de evidência fica explícita: nenhuma distância, número ou rota foi inventada.

## Resumo

| Faixa | Sementes | Ameaça 1 | 2 | 3 | 4 |
|---|---|---|---|---|---|
| Pequena | 30 | 30 | 0 | 0 | 0 |
| Média | 30 | 0 | 28 | 2 | 0 |
| Grande | 30 | 0 | 0 | 28 | 2 |

## Cidades

### `city.elarion` — Elarion — Cidade das Sete Copas

- **Pequena — Rachadura na ponte antiga** (`seed.elarion.p`, ameaça 1): Um trabalhador de manutenção mostra uma rachadura numa ponte antiga e quer que alguém a registre de modo que a corte não possa ignorá-la.  
  *Agentes:* `npc.avariel-thorne`, `npc.parel-tres-parafusos`. *Abordagens possíveis:* inspecionar e documentar; levar o relatório ao Intendente; pedir a Parel um escoramento provisório. *Se ninguém age:* a rachadura segue sem registro e sem reparo; o risco permanece, sem dano certo. *Liga:* event.ponte-cede. *Fonte:* doc 01 §5.1 (problemas); doc 01 §9 (Avariel).
- **Média — Mapas alterados no Arquivo** (`seed.elarion.m`, ameaça 2): Nara Três-Fios quer saber quem alterou um conjunto de mapas do Arquivo das Seivas e precisa de quem compare cópias.  
  *Agentes:* `npc.nara-tres-fios`, `npc.lethariel-nascente-clara`, `faction.arquivistas-seiva`. *Abordagens possíveis:* comparar cópias; rastrear mensageiros; consultar Lethariel; verificar quem consultou o conjunto. *Se ninguém age:* Nara continua sozinha; a alteração pode nunca ser datada nem atribuída. *Fonte:* doc 01 §5.1 (rumor do conselheiro que paga por mapas do sul, não confirmado); doc 01 §9 (Nara).
- **Grande — Os documentos desaparecidos da Guerra das Cinzas Verdes** (`seed.elarion.g`, ameaça 3): Documentos sobre a Guerra das Cinzas Verdes sumiram; roubo, perda e retirada por ordem são hipóteses abertas.  
  *Agentes:* `npc.lethariel-nascente-clara`, `npc.avariel-thorne`, `npc.vaunor-eir`, `npc.maereth-vaelorian`, `faction.arquivistas-seiva`. *Abordagens possíveis:* inventário do Arquivo; ouvir escribas; consultar registros de acesso; levar a questão ao Conselho; guardar cópias independentes. *Se ninguém age:* a versão oficial fica sem contestação; Arquivo e corte podem passar a se acusar. *Liga:* event.arquivo-sinistro. *Fonte:* doc 01 §5.1 (problemas); doc 01 §13 Lenda I (sem vínculo estabelecido).

### `city.nymvar` — Nymvar — Cidade das Três Vozes

- **Pequena — Compra suspeita na Feira das Sementes** (`seed.nymvar.p`, ameaça 1): Um feirante pede que alguém acompanhe um comprador que, segundo o rumor, adquire sementes doentes para revendê-las.  
  *Agentes:* `npc.elsin-varzea-clara`, `faction.liga-celeiros`. *Abordagens possíveis:* seguir o comprador; analisar as sementes; avisar a Feira; confirmar se o rumor tem base. *Se ninguém age:* o rumor pode se mostrar falso ou as sementes seguem circulando. *Fonte:* doc 01 §5.2 (rumores).
- **Média — A origem do fungo** (`seed.nymvar.m`, ameaça 2): Um fungo atingiu parte das plantações; é preciso decidir entre causa natural, sabotagem e efeito de uma árvore-coração enferma.  
  *Agentes:* `npc.elsin-varzea-clara`, `npc.voz-muitas-raizes`, `npc.mirel-caule-vivo`. *Abordagens possíveis:* coletar amostras de campos afetados e sadios; consultar curandeiros; pedir ao Círculo Micelial estudo com consentimento; comparar com Arven. *Se ninguém age:* o fungo avança ou estabiliza conforme o clima e o manejo; sem avanço automático. *Liga:* clock.heart-trees. *Fonte:* doc 01 §5.2 (gancho); doc 01 §13 Mistério A.
- **Grande — O material inferior do dique** (`seed.nymvar.g`, ameaça 3): Elsin tem provas de que um trecho do dique foi reparado com material inferior e não sabe quem autorizou a compra.  
  *Agentes:* `npc.elsin-varzea-clara`, `npc.beren-tres-canais`, `npc.edris-do-sul`, `npc.maereth-vaelorian`, `faction.irmandade-aguas`. *Abordagens possíveis:* rastrear a compra; ouvir a equipe de Beren; levar a prova a Edris e ao Conselho; reforçar o trecho antes da cheia. *Se ninguém age:* a próxima cheia pode testar o trecho; a autoria da compra fica em aberto. *Liga:* clock.dikes; event.dique-rompido. *Fonte:* doc 01 §9 (Elsin); doc 01 §13 Conflito F.

### `city.thalanor` — Thalanor — Porto da Folha Azul

- **Pequena — Cartas em língua desconhecida** (`seed.thalanor.p`, ameaça 1): Um faroleiro recebe cartas escritas numa língua que ninguém identificou e quer saber o que dizem.  
  *Agentes:* `npc.terenna-luz-de-lastro`, `faction.arquivistas-seiva`. *Abordagens possíveis:* copiar as cartas; pedir leitura aos Arquivistas; observar quem as entrega. *Se ninguém age:* as cartas seguem chegando e o rumor cresce; o conteúdo pode ser banal. *Fonte:* doc 01 §5.3 (rumores).
- **Média — Mapas de enseadas proibidas** (`seed.thalanor.m`, ameaça 2): Uma família compra mapas de enseadas proibidas; o motivo não está estabelecido.  
  *Agentes:* `npc.syranna-das-mares`, `faction.companhia-ambar`, `faction.liga-das-rotas`. *Abordagens possíveis:* identificar o vendedor; vigiar as enseadas; conversar com capitães; não presumir ligação com a resina. *Se ninguém age:* a compra continua sem explicação; nada indica que tenha relação com o contrabando. *Liga:* clock.resin-smuggling (ligação não provada). *Fonte:* doc 01 §5.3 (rumores); doc 01 §13 Conflito H.
- **Grande — O navio sem tripulação** (`seed.thalanor.g`, ameaça 3): O navio chegou em boas condições, sem tripulação e com carga registrada de forma incompleta; Syranna não aceita arquivar o irmão desaparecido como naufrágio sem destroços.  
  *Agentes:* `npc.syranna-das-mares`, `npc.yara-sal-de-mare`, `npc.terenna-luz-de-lastro`, `faction.liga-das-rotas`. *Abordagens possíveis:* examinar a carga registrada; entrevistar quem recebeu o navio; comparar listas de tripulação; não presumir ligação com o irmão de Syranna. *Se ninguém age:* o porto pode fechar descarga em quarentena; a explicação segue em aberto. *Liga:* clock.ghost-ship; event.navio-quarentena. *Fonte:* doc 01 §13 Mistério C; doc 01 §9 (Syranna).

### `city.vael-tir` — Vael-Tir — A Cidade sob as Copas

- **Pequena — Passos nos galhos altos** (`seed.vael-tir.p`, ameaça 1): Patrulheiros ouvem passos em galhos altos sem encontrar ninguém; a patrulha quer uma noite de vigília extra.  
  *Agentes:* `npc.eryndra-folha-verde`, `npc.fen-orvaluz`. *Abordagens possíveis:* vigília noturna; rastrear sinais de animais com Fen; avaliar se há relação com o corte noturno. *Se ninguém age:* continua sem explicação; pode ser animal ou outra coisa. *Fonte:* doc 01 §5.4 (rumores).
- **Média — Marcas anteriores ao reino** (`seed.vael-tir.m`, ameaça 2): Alguém marca árvores com símbolos anteriores à fundação do reino; falta saber quem marca e por quê.  
  *Agentes:* `npc.eryndra-folha-verde`, `npc.lethariel-nascente-clara`, `faction.vigias-primeiro-verde`. *Abordagens possíveis:* catalogar as marcas; consultar os Arquivistas; seguir quem marca; verificar se marcas precedem o corte, sem presumir. *Se ninguém age:* novas marcas podem surgir ou cessar; o sentido segue desconhecido. *Liga:* event.corte-ilegal (relação não estabelecida). *Fonte:* doc 01 §5.4 (rumores e problemas).
- **Grande — O posto da Coroa e o corredor** (`seed.vael-tir.g`, ameaça 3): A Coroa quer um posto permanente sem consultar os centauros; a Liga das Rotas quer a estrada aberta.  
  *Agentes:* `npc.oranna-passo-largo`, `npc.eryndra-folha-verde`, `npc.tavian-lume-fino`, `npc.maereth-vaelorian`. *Abordagens possíveis:* mediação com opções de desvio, pontes, horários ou indenização (cada uma com custo real); ouvir as comunidades centauras; levantar o que a Coroa pretende. *Se ninguém age:* a tensão pode escalar até fechar o corredor, ou o posto pode ser adiado. *Liga:* clock.centaur-corridors; event.corredor-fechado. *Fonte:* doc 01 §5.4 (problemas); doc 01 §13 Conflito G.

### `city.orvass` — Orvass — Cidade das Águas Escuras

- **Pequena — A ilha que surgiu após a cheia** (`seed.orvass.p`, ameaça 1): Barqueiros dizem que uma ilha surgiu depois da última cheia e querem saber se está nos mapas.  
  *Agentes:* `faction.irmandade-aguas`, `settlement.ilhas-junco-negro`. *Abordagens possíveis:* mapear a ilha; comparar com o mapa oficial e o sazonal; ouvir barqueiros. *Se ninguém age:* a ilha pode ser só um banco de lodo sazonal. *Fonte:* doc 01 §5.5 (rumores).
- **Média — Remédios que escasseiam** (`seed.orvass.m`, ameaça 2): Os remédios escasseiam e as licenças da capital são caras; é preciso trazer ou produzir ervas por via legal.  
  *Agentes:* `npc.voz-muitas-raizes`, `settlement.lodo-claro`, `faction.circulo-micelial`. *Abordagens possíveis:* trazer ervas por rota legal; negociar licença; consultar o estoque da Casa das Ervas Fundas; ouvir Lodo Claro. *Se ninguém age:* a escassez piora, estabiliza ou é compensada por outra rota; sem automatismo. *Fonte:* doc 01 §5.5 (problemas).
- **Grande — Desaparecimentos atribuídos a monstros** (`seed.orvass.g`, ameaça 3): Desaparecimentos são atribuídos a monstros sem investigação adequada.  
  *Agentes:* `npc.voz-muitas-raizes`, `settlement.ilhas-junco-negro`, `faction.vigias-primeiro-verde`. *Abordagens possíveis:* coletar relatos; checar passagens e canais fora do mapa; verificar contaminação nas redes miceliais; não presumir monstro. *Se ninguém age:* o Conselho das Estacas pode agir sem investigação, ou os casos podem cessar. *Fonte:* doc 01 §5.5 (problemas e gancho).

### `city.caladwen` — Caladwen — Cidade dos Arquivos Celestes

- **Pequena — Cópias incompletas** (`seed.caladwen.p`, ameaça 1): Estudantes vendem cópias incompletas de textos proibidos; a Reitora quer saber de onde saem.  
  *Agentes:* `npc.selvaris-noctel`, `settlement.papel-velho`. *Abordagens possíveis:* comprar e comparar cópias; rastrear copistas; levar o resultado à Reitora. *Se ninguém age:* as cópias seguem circulando; o dano é de qualidade da informação. *Fonte:* doc 01 §5.6 (problemas).
- **Média — A professora que ainda escreve** (`seed.caladwen.m`, ameaça 2): Uma professora desaparecida continua enviando notas por mensageiros que dizem nunca tê-la visto.  
  *Agentes:* `npc.selvaris-noctel`, `npc.nara-tres-fios`. *Abordagens possíveis:* rastrear os mensageiros; comparar a caligrafia; checar a última atividade da professora; manter hipóteses abertas (autoria própria, falsificação, outra). *Se ninguém age:* as notas podem cessar ou continuar; a origem segue desconhecida. *Fonte:* doc 01 §5.6 (rumores).
- **Grande — A ala interditada e a oitava cidade** (`seed.caladwen.g`, ameaça 3): Selvaris está dividida entre manter a ala interditada e revelar que um manuscrito pode provar a oitava cidade fundadora.  
  *Agentes:* `npc.selvaris-noctel`, `npc.lethariel-nascente-clara`, `faction.arquivistas-seiva`. *Abordagens possíveis:* autenticar o manuscrito; comparar com a cópia incompleta em circulação; decidir se a ala deve abrir; pesar o efeito político. *Se ninguém age:* a cópia incompleta continua circulando; a legitimidade da Coroa pode ser questionada sem prova. *Fonte:* doc 01 §13 Mistério B; doc 01 §14.

### `city.brumafunda` — Brumafunda — A Cidade do Bosque Velado

- **Pequena — A criança que atravessa a névoa** (`seed.brumafunda.p`, ameaça 1): Uma criança atravessa a neblina sem se perder; guias querem entender sem transformá-la em oráculo.  
  *Agentes:* `npc.neralis-vento-manso`, `faction.rede-guias`. *Abordagens possíveis:* observar a travessia; ouvir os guias; proteger a criança da atenção excessiva. *Se ninguém age:* a atenção em torno da criança cresce ou passa; sem explicação garantida. *Fonte:* doc 01 §5.7 (rumores).
- **Média — Marcas de trilha onde ninguém as pôs** (`seed.brumafunda.m`, ameaça 2): Guias relatam marcas de trilha em lugares onde não foram colocadas.  
  *Agentes:* `npc.neralis-vento-manso`, `settlement.eco-baixo`, `npc.mira-ouve-chuva`. *Abordagens possíveis:* verificar no terreno; comparar mapas copiados; checar quem copiou. *Se ninguém age:* novas marcas podem aparecer; a causa fica em aberto. *Fonte:* doc 01 §13 Mistério E.
- **Grande — O mapa da pessoa desaparecida** (`seed.brumafunda.g`, ameaça 3): Neralis guarda um mapa recebido de uma pessoa desaparecida e não sabe se é confiável; viajantes continuam sumindo.  
  *Agentes:* `npc.neralis-vento-manso`, `npc.fen-orvaluz`, `faction.rede-guias`, `settlement.torre-nevoa-mansa`. *Abordagens possíveis:* testar o mapa em trecho curto; organizar busca; mediar guias e estudiosos; verificar o mapa contra marcas conhecidas. *Se ninguém age:* desaparecimentos podem continuar ou cessar; o mapa segue sem validação. *Fonte:* doc 01 §9 (Neralis); doc 01 §5.7 (problemas); Mistério E.

### `city.arven` — Arven — As Clareiras Douradas

- **Pequena — Abelhas deixam uma área** (`seed.arven.p`, ameaça 1): As abelhas abandonam uma área sem motivo aparente.  
  *Agentes:* `npc.sena-polen-dourado`, `faction.liga-celeiros`. *Abordagens possíveis:* mapear a área; ouvir apicultores; comparar com campos vizinhos. *Se ninguém age:* as abelhas podem voltar ou não; causa desconhecida. *Fonte:* doc 01 §5.8 (rumores).
- **Média — Sementes que crescem depressa** (`seed.arven.m`, ameaça 2): Sementes de um fornecedor crescem depressa, mas empobrecem o solo; a Prefeita tem conflito de interesse por causa do armazém da família.  
  *Agentes:* `npc.tessa-folhinha`, `faction.liga-celeiros`. *Abordagens possíveis:* comparar solo de campos com e sem as sementes; rastrear o fornecedor; tratar o conflito de interesse de Tessa com transparência. *Se ninguém age:* o solo pode empobrecer ou o efeito pode ser menor que o temido. *Fonte:* doc 01 §5.8 (rumores); doc 01 §9 (Tessa).
- **Grande — O tributo da safra que não houve** (`seed.arven.g`, ameaça 3): O tributo é calculado com base numa colheita que não ocorreu; famílias endividadas pressionam.  
  *Agentes:* `npc.tessa-folhinha`, `npc.doria-tres-sulcos`, `npc.edris-do-sul`, `npc.maereth-vaelorian`. *Abordagens possíveis:* auditar as colheitas; levar ao Conselho; propor revisão formal do pacto; mediar entre Arven e a administração. *Se ninguém age:* a cobrança antiga segue; o desgaste entre cidades e Coroa pode crescer. *Liga:* clock.city-distrust; event.pacto-suspenso. *Fonte:* doc 01 §5.8 (problemas); doc 01 §13 Conflito F.

### `city.silvaran` — Silvaran — A Cidade dos Festivais

- **Pequena — A canção disputada** (`seed.silvaran.p`, ameaça 1): Uma disputa sobre a propriedade de uma canção antiga divide duas famílias.  
  *Agentes:* `npc.lyris-melodia-clara`, `faction.circulo-estacoes`. *Abordagens possíveis:* coletar as versões; encontrar a origem; arbitrar sem decidir por autoridade. *Se ninguém age:* a disputa esfria ou azeda; sem vencedor garantido. *Fonte:* doc 01 §5.9 (problemas).
- **Média — A celebração financiada em segredo** (`seed.silvaran.m`, ameaça 2): Uma celebração privada é financiada por um conselheiro anônimo.  
  *Agentes:* `npc.lyris-melodia-clara`, `faction.circulo-estacoes`. *Abordagens possíveis:* seguir o dinheiro; observar a festa; avaliar se há pacto desrespeitado; não presumir quem é o conselheiro. *Se ninguém age:* a festa passa sem consequência ou chama atenção do Conselho. *Fonte:* doc 01 §5.9 (rumores).
- **Grande — A canção do rei apagado** (`seed.silvaran.g`, ameaça 3): Uma canção antiga cita o nome de um rei apagado dos arquivos.  
  *Agentes:* `npc.lyris-melodia-clara`, `npc.lethariel-nascente-clara`, `npc.ivara-polen-de-lume`. *Abordagens possíveis:* registrar versões da canção; cruzar com o Arquivo; avaliar autenticidade; medir o efeito político antes de divulgar. *Se ninguém age:* a canção continua oral; o nome pode ser lenda, erro ou fato. *Fonte:* doc 01 §5.9 (rumores); sem vínculo estabelecido com Mistério B nem Lenda I.

### `city.torvelume` — Torvelume — A Fortaleza da Espinha de Vidro

- **Pequena — Salários atrasados** (`seed.torvelume.p`, ameaça 1): Trabalhadores exigem salários atrasados enquanto a corte quer ampliar a guarnição.  
  *Agentes:* `npc.dauren-galho-de-ferro`, `faction.guarda-espinha`. *Abordagens possíveis:* levantar os registros de pagamento; mediar; localizar a fonte do atraso. *Se ninguém age:* o atraso persiste e o descontentamento cresce, ou é sanado por outra via. *Fonte:* doc 01 §5.10 (problemas).
- **Média — Marcas de ferramentas na mina proibida** (`seed.torvelume.m`, ameaça 3): Há marcas de ferramentas recentes na mina; ninguém admite ter entrado.  
  *Agentes:* `npc.dauren-galho-de-ferro`, `npc.rovan-cinza-de-ferro`, `faction.guarda-espinha`. *Abordagens possíveis:* patrulhar sem abrir; checar ferramentas desaparecidas; ouvir os vigias; avaliar risco de desabamento. *Se ninguém age:* novas marcas podem surgir ou não; entrada indevida é só uma hipótese. *Liga:* clock.sealed-mine. *Fonte:* doc 01 §5.10 (rumores); doc 01 §13 Mistério D.
- **Grande — O símbolo que a Coroa declara inexistente** (`seed.torvelume.g`, ameaça 3): Um anão aposentado reconheceu um símbolo que a Coroa declara inexistente.  
  *Agentes:* `npc.dauren-galho-de-ferro`, `npc.lethariel-nascente-clara`, `faction.guarda-espinha`. *Abordagens possíveis:* localizar o aposentado; comparar com registros; avaliar o motivo da declaração da Coroa; não revelar sem avaliar risco. *Se ninguém age:* a declaração oficial segue sem contestação; o símbolo pode ser banal ou não. *Liga:* clock.sealed-mine (ligação não provada). *Fonte:* doc 01 §5.10 (rumores).

## Assentamentos

### `settlement.raiz-de-prata` — Raiz-de-Prata

- **Pequena — Reserva de mudas** (`seed.raiz-de-prata.p`, ameaça 1): Nivara precisa de ajuda para contar a reserva emergencial de árvores nativas e as mudas comuns.  
  *Agentes:* `npc.nivara-folha-de-orvalho`. *Abordagens possíveis:* contar e registrar as mudas; guardar cópia do registro. *Se ninguém age:* a reserva fica sem registro atualizado. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Variedades locais em risco** (`seed.raiz-de-prata.m`, ameaça 2): Nivara protege sementes adaptadas ao solo que a corte vê como pouco produtivas.  
  *Agentes:* `npc.nivara-folha-de-orvalho`, `npc.avariel-thorne`. *Abordagens possíveis:* documentar a adaptação ao solo; levar o caso à administração; guardar sementes em local seguro. *Se ninguém age:* a corte pode decidir sem esse registro. *Liga:* event.mudas-raras. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — As mudas raras da Coroa** (`seed.raiz-de-prata.g`, ameaça 3): A Coroa exige mudas de espécies raras; Nivara diz que retirar as sementes locais comprometerá a próxima estação.  
  *Agentes:* `npc.nivara-folha-de-orvalho`, `npc.avariel-thorne`, `npc.maereth-vaelorian`. *Abordagens possíveis:* negociar quantidade e prazo; propor reposição escalonada; arbitrar entre restauração das áreas nobres e colheita da vila. *Se ninguém age:* a ordem é cumprida, adiada ou contestada; o efeito na estação seguinte depende do que for retirado. *Liga:* event.mudas-raras. *Fonte:* doc 03; 12-assentamentos.csv; doc 01 §5.1.

### `settlement.sete-degraus` — Sete Degraus

- **Pequena — Registros do pedágio** (`seed.sete-degraus.p`, ameaça 1): Othalen quer ajuda para ordenar os registros do pedágio da ponte.  
  *Agentes:* `npc.othalen-vira-pedra`. *Abordagens possíveis:* ordenar e copiar os registros; listar as passagens isentas. *Se ninguém age:* os registros ficam desorganizados; a dúvida sobre as isenções persiste. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — O decreto original** (`seed.sete-degraus.m`, ameaça 2): Três casas nobres atravessam sem pagar; Othalen suspeita de privilégio legal, mas nunca viu o decreto.  
  *Agentes:* `npc.othalen-vira-pedra`, `npc.lethariel-nascente-clara`. *Abordagens possíveis:* procurar o decreto no Arquivo; identificar as casas; conferir se a isenção tem base legal. *Se ninguém age:* o decreto pode existir, não existir ou estar perdido. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — Ponte, privilégio e desgaste** (`seed.sete-degraus.g`, ameaça 3): Se a isenção não tiver base legal, o custo do desgaste recai injustamente sobre os demais viajantes.  
  *Agentes:* `npc.othalen-vira-pedra`, `npc.avariel-thorne`, `npc.parel-tres-parafusos`. *Abordagens possíveis:* reunir provas; levar ao Intendente e ao Conselho; negociar manutenção por outras fontes. *Se ninguém age:* as casas podem manter a isenção; a ponte pode degradar por falta de verba. *Liga:* event.ponte-cede. *Fonte:* doc 03; 12-assentamentos.csv; doc 01 §5.1.

### `settlement.junco-alto` — Junco Alto

- **Pequena — Registros da equipe de Beren** (`seed.junco-alto.p`, ameaça 1): Beren ainda não apresentou todos os registros da equipe e pede ajuda para organizá-los.  
  *Agentes:* `npc.beren-tres-canais`. *Abordagens possíveis:* organizar os registros; conferir lacunas. *Se ninguém age:* a acusação segue sem resposta documentada. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Quem recebe manutenção primeiro** (`seed.junco-alto.m`, ameaça 2): A vila alega que o trecho superior recebe manutenção antes, empurrando o risco para os bairros abaixo.  
  *Agentes:* `npc.beren-tres-canais`, `npc.elsin-varzea-clara`. *Abordagens possíveis:* comparar o calendário de reparos; visitar os trechos; levar o resultado à Matriarca. *Se ninguém age:* a disputa de prioridade continua sem critério público. *Liga:* clock.dikes. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — A acusação de desvio de materiais** (`seed.junco-alto.g`, ameaça 3): Beren foi acusado de desviar materiais; Elsin tem prova de material inferior num reparo, sem relação estabelecida.  
  *Agentes:* `npc.beren-tres-canais`, `npc.elsin-varzea-clara`, `npc.edris-do-sul`. *Abordagens possíveis:* cruzar registros de compra e de obra; ouvir a equipe; não ligar os dois casos sem prova. *Se ninguém age:* a acusação pode se confirmar, cair ou ficar pendente. *Liga:* clock.dikes; event.dique-rompido. *Fonte:* doc 03; 12-assentamentos.csv; doc 01 §9.

### `settlement.tres-barcas` — Três Barcas

- **Pequena — Travessia em corrente perigosa** (`seed.tres-barcas.p`, ameaça 1): Um barqueiro hesita em atravessar com a corrente forte e pede apoio para decidir.  
  *Agentes:* `npc.sava-mao-de-corrente`. *Abordagens possíveis:* avaliar a corrente; escoltar a travessia; adiar. *Se ninguém age:* a travessia é adiada ou feita com risco. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — A tarifa máxima em crise** (`seed.tres-barcas.m`, ameaça 2): A cidade exige tarifa máxima em crise; barqueiros dizem operar com prejuízo quando a corrente piora.  
  *Agentes:* `npc.sava-mao-de-corrente`, `npc.elsin-varzea-clara`, `faction.liga-das-rotas`. *Abordagens possíveis:* levantar custos reais; propor tarifa por faixa de risco; mediar. *Se ninguém age:* a regra segue como está; os barqueiros podem recusar operar (hipótese, não fato). *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — Cheia e tarifa** (`seed.tres-barcas.g`, ameaça 3): Numa cheia, a regra de tarifa pode deixar a vila sem travessias de emergência, se os barqueiros decidirem não operar.  
  *Agentes:* `npc.sava-mao-de-corrente`, `npc.elsin-varzea-clara`, `npc.edris-do-sul`. *Abordagens possíveis:* preparar acordo antes da cheia; garantir embarcações de emergência; acertar compensação. *Se ninguém age:* sem acordo, a cheia expõe o conflito; depende do nível da água. *Liga:* clock.dikes. *Fonte:* doc 03; 12-assentamentos.csv.

### `settlement.ponta-da-concha` — Ponta da Concha

- **Pequena — Amostras de água** (`seed.ponta-da-concha.p`, ameaça 1): Yara quer coletar amostras de água perto das áreas de pesca.  
  *Agentes:* `npc.yara-sal-de-mare`. *Abordagens possíveis:* coletar e etiquetar amostras; entregar a quem possa analisar. *Se ninguém age:* sem amostras, a contaminação segue um relato. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Compensação das famílias** (`seed.ponta-da-concha.m`, ameaça 2): Novas restrições de pesca vieram depois de relatos de contaminação, sem compensação às famílias.  
  *Agentes:* `npc.yara-sal-de-mare`, `npc.syranna-das-mares`. *Abordagens possíveis:* levantar famílias afetadas; levar o pedido à Almirante-civil; propor critério de compensação. *Se ninguém age:* as famílias seguem sem compensação ou arranjam outra via. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — A origem da contaminação** (`seed.ponta-da-concha.g`, ameaça 3): A origem dos relatos de contaminação é desconhecida; nada a liga ao navio sem tripulação.  
  *Agentes:* `npc.yara-sal-de-mare`, `npc.syranna-das-mares`, `faction.vigias-primeiro-verde`. *Abordagens possíveis:* analisar amostras; mapear onde os relatos se concentram; manter hipóteses separadas. *Se ninguém age:* a contaminação pode ser real, local ou superestimada. *Fonte:* doc 03; 12-assentamentos.csv; doc 01 §5.3.

### `settlement.farol-de-ambar` — Farol de Âmbar

- **Pequena — Sinal atrasado** (`seed.farol-de-ambar.p`, ameaça 1): Um sinal marítimo atrasa por falta de autorização e Terenna pede quem leve a mensagem à guarda.  
  *Agentes:* `npc.terenna-luz-de-lastro`. *Abordagens possíveis:* levar a mensagem; registrar o atraso; propor rito de urgência. *Se ninguém age:* o atraso pode custar um aviso ou passar sem efeito. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Embarcação desconhecida** (`seed.farol-de-ambar.m`, ameaça 2): Terenna precisa registrar uma embarcação desconhecida e quer apoio da guarda sem entregar a autonomia do farol.  
  *Agentes:* `npc.terenna-luz-de-lastro`, `npc.syranna-das-mares`. *Abordagens possíveis:* registrar; checar identidade; coordenar com a guarda portuária. *Se ninguém age:* a embarcação segue sem registro ou é identificada. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — O farol e a censura de sinais** (`seed.farol-de-ambar.g`, ameaça 3): A guarda portuária quer usar o farol para observação política e censura de sinais; moradores temem naufrágios por atrasos.  
  *Agentes:* `npc.terenna-luz-de-lastro`, `npc.syranna-das-mares`, `faction.liga-das-rotas`. *Abordagens possíveis:* negociar regra de autorização mínima; documentar casos de atraso; mediar entre guarda e faroleiros. *Se ninguém age:* a guarda pode impor controle ou recuar; o risco de atraso é real. *Fonte:* doc 03; 12-assentamentos.csv.

### `settlement.copa-funda` — Copa-Funda

- **Pequena — Contagem de árvores aptas** (`seed.copa-funda.p`, ameaça 1): Arieth precisa contar as árvores ainda aptas ao corte após a praga.  
  *Agentes:* `npc.arieth-tres-mudas`. *Abordagens possíveis:* contar; marcar; registrar. *Se ninguém age:* sem contagem, a cota segue desatualizada. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — A cota anterior à praga** (`seed.copa-funda.m`, ameaça 2): A cota foi calculada com dados anteriores à praga e Arieth precisa provar a diferença.  
  *Agentes:* `npc.arieth-tres-mudas`, `npc.eryndra-folha-verde`, `faction.vigias-primeiro-verde`. *Abordagens possíveis:* produzir novo levantamento; levar aos Vigias; pedir revisão formal. *Se ninguém age:* a cota antiga continua; Arieth pode ser sancionada. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — Cumprir a cota ou perder a regeneração** (`seed.copa-funda.g`, ameaça 3): Se a vila cumprir a ordem, pode perder a capacidade de regeneração; se recusar, arrisca sanções.  
  *Agentes:* `npc.arieth-tres-mudas`, `npc.eryndra-folha-verde`, `npc.saelith-arco-de-chuva`. *Abordagens possíveis:* negociar prazo e área; propor compensação de outra fonte; documentar o estado das árvores. *Se ninguém age:* a decisão muda o estado da vila; sem pressa de uma única solução. *Fonte:* doc 03; 12-assentamentos.csv; doc 01 §5.4.

### `settlement.passo-do-veado` — Passo do Veado

- **Pequena — Suspensão da caça** (`seed.passo-do-veado.p`, ameaça 1): Calar quer apoio para fiscalizar a suspensão da caça durante a migração.  
  *Agentes:* `npc.calar-passo-curvo`. *Abordagens possíveis:* fiscalizar; avisar caçadores; registrar infrações. *Se ninguém age:* a suspensão é desrespeitada ou respeitada. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — A trilha que alarga** (`seed.passo-do-veado.m`, ameaça 2): A Liga quer alargar a trilha; as comunidades centauras dizem que isso reduz a largura segura de passagem.  
  *Agentes:* `npc.calar-passo-curvo`, `npc.oranna-passo-largo`, `faction.liga-das-rotas`. *Abordagens possíveis:* medir a largura segura; propor desvio, pontes ou horários; ouvir ambos os lados. *Se ninguém age:* a trilha pode ser alargada ou mantida; cada opção tem custo. *Liga:* clock.centaur-corridors. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — O corredor sob disputa** (`seed.passo-do-veado.g`, ameaça 3): Se não houver acordo, o corredor pode ser fechado ou transformado em estrada permanente.  
  *Agentes:* `npc.calar-passo-curvo`, `npc.oranna-passo-largo`, `npc.tavian-lume-fino`, `npc.eryndra-folha-verde`. *Abordagens possíveis:* mediar com indenização, desvio ou horários; documentar a migração; levar a tensão ao conselho. *Se ninguém age:* o conflito escala ou esfria; fechamento é uma possibilidade, não um fato. *Liga:* clock.centaur-corridors; event.corredor-fechado. *Fonte:* doc 03; 12-assentamentos.csv; doc 01 §13 Conflito G.

### `settlement.lodo-claro` — Lodo-Claro

- **Pequena — Amostras para Orvass** (`seed.lodo-claro.p`, ameaça 1): Nima precisa entregar amostras de água a Orvass sem esgotar a reserva local.  
  *Agentes:* `npc.nima-barro-manso`. *Abordagens possíveis:* coletar; transportar; registrar. *Se ninguém age:* a entrega atrasa ou segue. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Coleta acima do sustentável** (`seed.lodo-claro.m`, ameaça 2): Licenças de Orvass permitem coleta comercial acima do que a aldeia considera sustentável.  
  *Agentes:* `npc.nima-barro-manso`, `npc.voz-muitas-raizes`. *Abordagens possíveis:* medir a regeneração; comparar com as licenças; levar os dados ao Conselho. *Se ninguém age:* a coleta pode seguir ou ser ajustada. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — A acusação de esconder recursos** (`seed.lodo-claro.g`, ameaça 3): Orvass acusa a aldeia de esconder recursos públicos; a aldeia diz proteger a regeneração.  
  *Agentes:* `npc.nima-barro-manso`, `npc.voz-muitas-raizes`, `faction.irmandade-aguas`. *Abordagens possíveis:* inventariar o que existe; separar reserva de ocultação; mediar com dados verificáveis. *Se ninguém age:* a acusação pode se confirmar, cair ou ficar pendente. *Fonte:* doc 03; 12-assentamentos.csv; doc 01 §5.5.

### `settlement.ilhas-junco-negro` — Ilhas do Junco Negro

- **Pequena — O canal que mudou** (`seed.ilhas-junco-negro.p`, ameaça 1): Vela precisa de ajuda para registrar um canal que mudou de lugar com a estação.  
  *Agentes:* `npc.vela-tres-margens`. *Abordagens possíveis:* mapear; marcar; entregar à vila. *Se ninguém age:* o canal muda de novo ou permanece. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Barqueiros detidos** (`seed.ilhas-junco-negro.m`, ameaça 2): Guardas detêm barqueiros por usar passagens fora dos registros; os moradores dizem que os canais mudam naturalmente.  
  *Agentes:* `npc.vela-tres-margens`, `faction.irmandade-aguas`. *Abordagens possíveis:* documentar a mudança dos canais; negociar a soltura; propor atualização do registro. *Se ninguém age:* as detenções continuam ou cessam. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — Mapas oficiais e mapas sazonais** (`seed.ilhas-junco-negro.g`, ameaça 3): Mapas oficiais desatualizados criam conflito jurídico; a aldeia quer manter seus mapas sazonais.  
  *Agentes:* `npc.vela-tres-margens`, `faction.irmandade-aguas`, `npc.voz-muitas-raizes`. *Abordagens possíveis:* propor recenseamento conjunto dos canais; reconhecer mapas sazonais; evitar que mapas locais virem armas. *Se ninguém age:* a cidade pode impor seu mapa ou aceitar um sazonal. *Fonte:* doc 03; 12-assentamentos.csv; doc 01 §5.5 (rumores).

### `settlement.vigilia-do-lago` — Vigília do Lago

- **Pequena — O alerta que precisa chegar** (`seed.vigilia-do-lago.p`, ameaça 1): Orelia tem um alerta de risco para as cidades conectadas e precisa que chegue a tempo.  
  *Agentes:* `npc.orelia-ceu-de-cinza`. *Abordagens possíveis:* levar o alerta; confirmar o recebimento. *Se ninguém age:* o alerta chega tarde ou a tempo. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — O padrão das luzes** (`seed.vigilia-do-lago.m`, ameaça 2): As luzes do lago formam um padrão que não aparece há cem anos.  
  *Agentes:* `npc.orelia-ceu-de-cinza`, `npc.selvaris-noctel`. *Abordagens possíveis:* registrar o padrão; compará-lo com registros antigos; evitar afirmar causa. *Se ninguém age:* o padrão pode se repetir ou sumir. *Fonte:* doc 03; 12-assentamentos.csv (rumores).
- **Grande — Dados que o Colégio quer fechar** (`seed.vigilia-do-lago.g`, ameaça 3): O Colégio quer classificar certos padrões como estratégicos; agricultores dizem que atrasar alertas custa colheitas e vidas.  
  *Agentes:* `npc.orelia-ceu-de-cinza`, `npc.selvaris-noctel`, `npc.tessa-folhinha`. *Abordagens possíveis:* negociar publicação parcial; definir critério de risco; documentar o efeito do atraso. *Se ninguém age:* o Colégio pode fechar os dados ou ceder; efeitos dependem do clima. *Liga:* event.previsao-falha. *Fonte:* doc 03; 12-assentamentos.csv.

### `settlement.papel-velho` — Papel-Velho

- **Pequena — O documento frágil** (`seed.papel-velho.p`, ameaça 1): Marel precisa copiar um documento frágil antes que se perca.  
  *Agentes:* `npc.marel-tinta-firme`. *Abordagens possíveis:* copiar; guardar duas cópias; registrar. *Se ninguém age:* o documento se perde ou resiste. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Pressão para não copiar** (`seed.papel-velho.m`, ameaça 2): Casas nobres compram cópias privadas e pressionam as oficinas a não reproduzir versões divergentes.  
  *Agentes:* `npc.marel-tinta-firme`, `faction.arquivistas-seiva`. *Abordagens possíveis:* documentar a pressão; manter cópia independente; consultar os Arquivistas. *Se ninguém age:* a oficina cede ou resiste. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — A cópia que contradiz a versão oficial** (`seed.papel-velho.g`, ameaça 3): Uma cópia incompleta da crônica da oitava cidade circula entre estudiosos e pode contradizer a história oficial.  
  *Agentes:* `npc.marel-tinta-firme`, `npc.lethariel-nascente-clara`, `npc.selvaris-noctel`. *Abordagens possíveis:* comparar cópias; autenticar; decidir quanto publicar. *Se ninguém age:* a cópia continua circulando; a veracidade segue desconhecida. *Fonte:* doc 01 §14; doc 01 §13 Mistério B.

### `settlement.eco-baixo` — Eco Baixo

- **Pequena — Marco de trilha** (`seed.eco-baixo.p`, ameaça 1): Mira precisa atualizar um marco de trilha antes que alguém se perca.  
  *Agentes:* `npc.mira-ouve-chuva`. *Abordagens possíveis:* atualizar; testar; avisar guias. *Se ninguém age:* o marco permanece desatualizado. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Busca por desaparecido** (`seed.eco-baixo.m`, ameaça 2): Mira participa da busca por um desaparecido e precisa de apoio na trilha.  
  *Agentes:* `npc.mira-ouve-chuva`, `npc.neralis-vento-manso`. *Abordagens possíveis:* organizar a busca; usar rotas de resgate; limitar a divulgação. *Se ninguém age:* a pessoa pode ser achada ou não. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — Guias acusados de monopólio** (`seed.eco-baixo.g`, ameaça 3): Mercadores acusam os guias de esconder rotas; os moradores temem que divulgar cada passagem leve caçadores a áreas frágeis.  
  *Agentes:* `npc.mira-ouve-chuva`, `npc.neralis-vento-manso`, `faction.rede-guias`, `faction.liga-das-rotas`. *Abordagens possíveis:* propor publicação parcial de rotas; criar critério de acesso; mediar. *Se ninguém age:* o monopólio pode ser reconhecido ou refutado; áreas frágeis dependem do que for publicado. *Fonte:* doc 03; 12-assentamentos.csv.

### `settlement.torre-nevoa-mansa` — Torre da Névoa Mansa

- **Pequena — Alerta de névoa** (`seed.torre-nevoa-mansa.p`, ameaça 1): Iven emite um alerta de névoa e precisa que viajantes o recebam.  
  *Agentes:* `npc.iven-marca-de-som`. *Abordagens possíveis:* levar o alerta; confirmar quem partiu. *Se ninguém age:* viajantes seguem ou recuam. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Trilha fechada por sinais locais** (`seed.torre-nevoa-mansa.m`, ameaça 2): Iven fecha trilhas por sinais locais sem esperar autorização distante.  
  *Agentes:* `npc.iven-marca-de-som`, `npc.neralis-vento-manso`. *Abordagens possíveis:* verificar os sinais; confirmar o risco; avisar guias. *Se ninguém age:* a trilha reabre ou permanece fechada. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — O fenômeno e os experimentos** (`seed.torre-nevoa-mansa.g`, ameaça 3): Estudiosos de Caladwen querem manter a torre aberta para observar um fenômeno; os vigias dizem que os experimentos aumentam o risco.  
  *Agentes:* `npc.iven-marca-de-som`, `npc.selvaris-noctel`, `npc.neralis-vento-manso`. *Abordagens possíveis:* propor protocolo de observação; limitar acesso; registrar incidentes. *Se ninguém age:* a torre pode fechar, abrir ou abrir com regras. *Fonte:* doc 03; 12-assentamentos.csv.

### `settlement.mel-do-poente` — Mel do Poente

- **Pequena — Faixa florida marcada** (`seed.mel-do-poente.p`, ameaça 1): Sena vê uma faixa florida marcada para remoção.  
  *Agentes:* `npc.sena-polen-dourado`. *Abordagens possíveis:* registrar; levar o caso à administração. *Se ninguém age:* a faixa é preservada ou removida. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Queda de polinização** (`seed.mel-do-poente.m`, ameaça 2): Os apicultores alertam que a colheita futura cai se a polinização piorar.  
  *Agentes:* `npc.sena-polen-dourado`, `faction.liga-celeiros`. *Abordagens possíveis:* medir polinização; comparar com campos sem faixa. *Se ninguém age:* a polinização pode piorar ou estabilizar. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — Campos maiores ou polinização** (`seed.mel-do-poente.g`, ameaça 3): Grandes produtores querem remover as faixas floridas; os apicultores querem protegê-las.  
  *Agentes:* `npc.sena-polen-dourado`, `npc.tessa-folhinha`, `faction.liga-celeiros`. *Abordagens possíveis:* propor compensação por área; fixar uma largura mínima; medir os dois lados. *Se ninguém age:* a faixa pode ser reduzida ou mantida; os efeitos são graduais. *Fonte:* doc 03; 12-assentamentos.csv.

### `settlement.campo-tres-familias` — Campo das Três Famílias

- **Pequena — Colheita efetiva** (`seed.campo-tres-familias.p`, ameaça 1): Dória precisa documentar a colheita efetiva da vila.  
  *Agentes:* `npc.doria-tres-sulcos`. *Abordagens possíveis:* contar; registrar; entregar. *Se ninguém age:* a cobrança segue sem dados. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Famílias endividadas** (`seed.campo-tres-familias.m`, ameaça 2): Famílias endividadas ameaçam abandonar terras se o tributo antigo for mantido.  
  *Agentes:* `npc.doria-tres-sulcos`, `npc.tessa-folhinha`. *Abordagens possíveis:* negociar parcelamento; levantar dívidas; mediar. *Se ninguém age:* as famílias podem ficar ou partir. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — Tributo antigo e pacto não alterado** (`seed.campo-tres-familias.g`, ameaça 3): A administração cobra a quantia antiga alegando que o pacto não foi alterado formalmente.  
  *Agentes:* `npc.doria-tres-sulcos`, `npc.tessa-folhinha`, `npc.edris-do-sul`, `npc.maereth-vaelorian`. *Abordagens possíveis:* documentar a safra; propor alteração formal do pacto; levar ao Conselho. *Se ninguém age:* a cobrança pode ser mantida ou revista; o desgaste entre cidades pode crescer. *Liga:* clock.city-distrust. *Fonte:* doc 03; 12-assentamentos.csv; doc 01 §13 Conflito F.

### `settlement.mascara-nova` — Máscara Nova

- **Pequena — A casa de acolhimento** (`seed.mascara-nova.p`, ameaça 1): Vessa precisa preparar a casa de acolhimento para o festival.  
  *Agentes:* `npc.vessa-fio-dourado`. *Abordagens possíveis:* preparar; organizar; registrar. *Se ninguém age:* a casa pode ficar pronta ou não. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Artistas que recusam** (`seed.mascara-nova.m`, ameaça 2): Artistas querem recusar apresentações sem perder oficina e moradia.  
  *Agentes:* `npc.vessa-fio-dourado`, `npc.lyris-melodia-clara`. *Abordagens possíveis:* definir regra de recusa; mediar; registrar o acordo. *Se ninguém age:* a regra pode ser aceita ou não. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — Serviço cultural ou exploração** (`seed.mascara-nova.g`, ameaça 3): Silvaran interpreta a obrigação cultural como serviço gratuito obrigatório; artistas alegam exploração de trabalho.  
  *Agentes:* `npc.vessa-fio-dourado`, `npc.lyris-melodia-clara`, `faction.circulo-estacoes`. *Abordagens possíveis:* renegociar o pacto; propor pagamento ou limites; ouvir ambos os lados. *Se ninguém age:* o pacto pode ser mantido, renegociado ou rompido. *Fonte:* doc 03; 12-assentamentos.csv.

### `settlement.fonte-do-regresso` — Fonte do Regresso

- **Pequena — Abrigo de emergência** (`seed.fonte-do-regresso.p`, ameaça 1): Dalen precisa abrigar viajantes em perigo por uma noite.  
  *Agentes:* `npc.dalen-porta-aberta`. *Abordagens possíveis:* organizar; escoltar; registrar. *Se ninguém age:* o abrigo funciona ou falha. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Custo das famílias anfitriãs** (`seed.fonte-do-regresso.m`, ameaça 2): O acolhimento custa às famílias anfitriãs mais do que o previsto.  
  *Agentes:* `npc.dalen-porta-aberta`, `npc.lyris-melodia-clara`. *Abordagens possíveis:* levantar o custo; propor compensação; mediar. *Se ninguém age:* as famílias podem recusar ou aceitar. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — Refugiados de uma disputa política** (`seed.fonte-do-regresso.g`, ameaça 4): Moradores discordam sobre acolher refugiados de uma disputa política; alguns temem represálias.  
  *Agentes:* `npc.dalen-porta-aberta`, `npc.lyris-melodia-clara`, `faction.circulo-estacoes`. *Abordagens possíveis:* mediar entre medo e pacto; proteger os acolhidos; consultar a Casa da Hospitalidade. *Se ninguém age:* a vila pode acolher, recusar ou dividir-se; cada opção tem custo. *Fonte:* doc 03; 12-assentamentos.csv; doc 01 §5.9.

### `settlement.ferro-folha` — Ferro-Folha

- **Pequena — Ferramentas civis** (`seed.ferro-folha.p`, ameaça 1): Meda tem ferramentas civis a reparar antes de uma obra.  
  *Agentes:* `npc.meda-martelo-calmo`. *Abordagens possíveis:* reparar; priorizar; entregar. *Se ninguém age:* a obra atrasa ou segue. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Pontes e diques na fila** (`seed.ferro-folha.m`, ameaça 2): Estruturas civis esperam reparo enquanto a guarnição pede peças.  
  *Agentes:* `npc.meda-martelo-calmo`, `npc.dauren-galho-de-ferro`. *Abordagens possíveis:* definir prioridades; mediar; registrar. *Se ninguém age:* a fila pode se acomodar ou piorar. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — Encomenda militar ou reparo civil** (`seed.ferro-folha.g`, ameaça 3): A guarnição exige armamentos e peças de fortificação enquanto estruturas civis aguardam reparo.  
  *Agentes:* `npc.meda-martelo-calmo`, `npc.dauren-galho-de-ferro`, `faction.guarda-espinha`. *Abordagens possíveis:* negociar divisão de capacidade; priorizar por risco verificável; documentar a necessidade. *Se ninguém age:* a vila pode atender um lado ou dividir; efeitos dependem do que for priorizado. *Fonte:* doc 03; 12-assentamentos.csv.

### `settlement.porta-selada` — Porta Selada

- **Pequena — Relatório de tremor** (`seed.porta-selada.p`, ameaça 1): Rovan precisa relatar um tremor nas galerias e quer quem leve o aviso.  
  *Agentes:* `npc.rovan-cinza-de-ferro`. *Abordagens possíveis:* relatar; registrar; avisar. *Se ninguém age:* o tremor se repete ou não. *Liga:* clock.sealed-mine. *Fonte:* doc 03; 12-assentamentos.csv.
- **Média — Salários e investigação** (`seed.porta-selada.m`, ameaça 3): Os trabalhadores querem salários e investigação transparente antes de qualquer reabertura.  
  *Agentes:* `npc.rovan-cinza-de-ferro`, `npc.dauren-galho-de-ferro`. *Abordagens possíveis:* mediar; fixar critérios de investigação; garantir pagamento. *Se ninguém age:* a reabertura pode ser adiada ou pressionada. *Liga:* clock.sealed-mine. *Fonte:* doc 03; 12-assentamentos.csv.
- **Grande — O selamento: negligência, perigo ou prova** (`seed.porta-selada.g`, ameaça 4): Famílias temem que o selamento esconda negligência, perigo real ou prova de irregularidade antiga.  
  *Agentes:* `npc.rovan-cinza-de-ferro`, `npc.dauren-galho-de-ferro`, `faction.guarda-espinha`. *Abordagens possíveis:* avaliar sem abrir; reunir registros; ouvir famílias; separar as três hipóteses. *Se ninguém age:* abrir sem avaliação pode causar desabamento; o selamento pode se manter. *Liga:* clock.sealed-mine; event.mina-rompe. *Fonte:* doc 03; 12-assentamentos.csv; doc 01 §13 Mistério D.
