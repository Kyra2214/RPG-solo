# 13 — Sementes de aventura (R2-5.3)

**Estado:** rascunho de R2-5.3 para aprovação do autor (D-51) · **Data:** 2026-11-04

**Etiqueta:** tudo é **[PROPOSTO]**; semente é situação jogável, não fato canônico. **Fonte:** doc 01 (partes 1, 2 e 4), doc 03, doc 04, doc 05, doc 06 (relógios, conflitos, oportunidades); `12-relogios.csv`, `12-eventos.csv`, `12-registro-mestre.csv`, `12-assentamentos.csv`. Dados em `13-sementes-de-aventura.csv` (molde: `ESQUEMA_DE_DADOS.md`, `semente.txt`).

## Regras

- 30 entidades (10 cidades, 20 assentamentos) × 3 sementes = **90**: uma **pequena (P)**, uma **média (M)**, uma **grande (G)**. ID: `seed.<entidade>.<p|m|g>`.
- Semente é **situação jogável**, não fato canônico (doc 06 §2.4 do reino 1; doc 06 §6 do reino 2). Não altera estado, relógio, lei, pacto ou relação até ser resolvida por ação, regra ou evento validado.
- **Sem solução predefinida:** *abordagens possíveis* são caminhos, não respostas; nenhuma semente escolhe hipótese de mistério (MP-01 a MP-06) nem resolve a origem dos Despertos, a origem do combustível ou o que há rio abaixo.
- **Cada semente tem "se ninguém age":** resultado proporcional, sem punição automática; relógio só avança por causa observável (doc 06 §1).
- **Só entidades existentes:** agentes são cidades, assentamentos, facções e NPCs do registro mestre; nenhum NPC com `vivo = não` é usado; nenhum NPC, cidade, facção ou assentamento novo. Pedidos feitos por "viajante", "pedreiro" ou "relojoeiro" sem nome são cenário, não personagem.
- **Ameaça** (1 a 4, ASSUMINDO, mesma escala do reino 1) mede o **risco para o personagem**, não a importância do problema para quem vive ali: 1 incômodo ou risco baixo; 2 risco moderado; 3 risco sério ou envolve autoridade; 4 risco alto, pode mudar uma instituição ou um relógio. Faixas: P 1, M 2–3, G 3–4. Nenhuma semente tem nível 5.
- **Liga** só com relógio (`clock.`) ou evento-tipo (`event.`) já escritos; "ligação não provada" onde as fontes não ligam. Sem relógio novo, sem evento novo.
- Lacuna explícita: nenhum tempo, distância, rota, número, população ou preço novo. Maur Camada-Velha só aparece em conversa particular (doc 01 N-05). Nada sobre Aeltharyn, Torvelume, a mina selada, a Espinha de Vidro, o Lúmen, o Argente ou as Sete-Bigornas.

## Resumo

| Faixa | Sementes | Ameaça 1 | 2 | 3 | 4 |
|---|---|---|---|---|---|
| Pequena | 30 | 30 | 0 | 0 | 0 |
| Média | 30 | 0 | 28 | 2 | 0 |
| Grande | 30 | 0 | 0 | 28 | 2 |

Total: **90** sementes; 47 com ligação a relógio ou evento (as demais, sem ligação escrita).

## Cidades e assentamentos

### `city.brasalume` — Brasalume — A Forja dos Dentes

- **Pequena — A liga que não esfria** (`seed.brasalume.p`, ameaça 1): Karna Brasa-Mansa ouve de novo o rumor da liga que não esfria e pede que alguém descubra onde o boato nasce, sem afirmar que a liga exista.  
  *Agentes:* `npc.karna-brasa-mansa`. *Abordagens possíveis:* ouvir ferreiros da Galeria dos Cadinhos; comparar as versões do boato; conferir o que a Fornalha-Mãe registra. *Se ninguém age:* o boato segue sem origem; pode ser só conversa de oficina. *Fonte:* doc 01 §2.1 (rumores).
- **Média — O turno do fogo principal** (`seed.brasalume.m`, ameaça 2): As famílias de forja disputam a vez do fogo principal e Gundrik Muralha-Lenta quer quem confira os turnos registrados contra a Lei do Fogo Partilhado.  
  *Agentes:* `npc.gundrik-muralha-lenta`, `npc.karna-brasa-mansa`, `faction.casa-das-fornalhas`. *Abordagens possíveis:* conferir o registro de turnos; ouvir as famílias pequenas; propor redução voluntária de consumo; levar o impasse à câmara de arbitragem. *Se ninguém age:* a disputa continua sem acordo; nenhum turno muda por conta própria. *Liga:* clock.combustivel. *Fonte:* doc 01 §2.1 (problemas); doc 05 §1 (L-03); doc 06 O-01.
- **Grande — O combustível que falta** (`seed.brasalume.g`, ameaça 4): As fornalhas gastam mais combustível do que a cidade produz e ninguém sabe de onde ele vem ou deveria vir; Gundrik precisa de quem levante o que entra e o que sai antes que o turno reduzido seja imposto.  
  *Agentes:* `npc.gundrik-muralha-lenta`, `npc.karna-brasa-mansa`, `npc.brundar-veio-alto`, `faction.casa-das-fornalhas`, `faction.irmandade-do-veio`. *Abordagens possíveis:* levantar consumo e entradas registradas; ouvir as famílias de forja; levar o quadro ao Conselho das Famílias de Fogo; propor registro do combustível pelo Concílio; não presumir a origem. *Se ninguém age:* o turno reduzido só vem com nova disputa ou nova queda de fornecimento observada; a origem do combustível continua desconhecida. *Liga:* clock.combustivel; event.fogo-reduzido. *Fonte:* doc 01 §2.1 (problemas); doc 05 §1 (L-03), §2.1; doc 06 CL-01, O-01.

### `settlement.cinzeiro` — Cinzeiro — Vila das Cinzas (de `city.brasalume`)

- **Pequena — Cinza fina para os lapidadores** (`seed.cinzeiro.p`, ameaça 1): Zeka Faísca-Mansa pede quem leve uma amostra de cinza fina a lapidadores da cidade, para saber se ela serve antes de pedir cota às fornalhas.  
  *Agentes:* `npc.zeka-faisca-mansa`. *Abordagens possíveis:* levar a amostra; ouvir o que os lapidadores dizem; guardar o resultado sem pedir cota ainda. *Se ninguém age:* a amostra fica sem avaliação; a vila segue sem destino para a cinza fina. *Fonte:* doc 03 S-01 (interesse local).
- **Média — Onde despejar a escória** (`seed.cinzeiro.m`, ameaça 2): A vila diz que a fumaça adoece as crianças e pede outro lugar de despejo; Zeka quer quem avalie o lugar atual e proponha um novo.  
  *Agentes:* `npc.zeka-faisca-mansa`, `npc.gundrik-muralha-lenta`, `faction.casa-das-fornalhas`. *Abordagens possíveis:* avaliar o lugar atual; ouvir as famílias da vila; negociar com os forjadores; propor local alternativo. *Se ninguém age:* o despejo continua como está; a queixa fica sem resposta. *Fonte:* doc 03 S-01 (tensão).
- **Grande — A nova regra de despejo** (`seed.cinzeiro.g`, ameaça 3): As fornalhas exigem toda a cinza e a vila ameaça migrar; uma nova regra de despejo pode ser imposta ou negociada.  
  *Agentes:* `npc.zeka-faisca-mansa`, `npc.gundrik-muralha-lenta`, `npc.karna-brasa-mansa`, `faction.casa-das-fornalhas`. *Abordagens possíveis:* mediar entre vila e forjadores; ouvir o Conselho das Famílias de Fogo; propor despejo por turnos; documentar o dano alegado. *Se ninguém age:* as famílias podem migrar ou aceitar; nenhuma regra muda por conta própria. *Liga:* clock.combustivel (ligação não provada). *Fonte:* doc 03 S-01 (tensão e desenvolvimentos).

### `settlement.veio-fundo` — Veio-Fundo — A Mina de Superfície (de `city.brasalume`)

- **Pequena — Escoras da galeria** (`seed.veio-fundo.p`, ameaça 1): Brundar Veio-Alto pede quem confira o escoramento de uma galeria antes do próximo turno, por receio de desabamento.  
  *Agentes:* `npc.brundar-veio-alto`. *Abordagens possíveis:* inspecionar as escoras; ouvir os mineiros; marcar o trecho duvidoso. *Se ninguém age:* o trecho segue sem conferência; o risco permanece, sem dano certo. *Fonte:* doc 03 S-02 (interesse local).
- **Média — A cota que paga o risco** (`seed.veio-fundo.m`, ameaça 2): O veio rende menos a cada ano e a cidade não aceita cota menor; Brundar quer quem levante o rendimento registrado.  
  *Agentes:* `npc.brundar-veio-alto`, `npc.gundrik-muralha-lenta`, `faction.irmandade-do-veio`. *Abordagens possíveis:* levantar o rendimento registrado; ouvir os mineiros; levar a conta à cidade; propor cota nova. *Se ninguém age:* a cota segue como está; a queixa dos mineiros continua. *Fonte:* doc 03 S-02 (tensão).
- **Grande — Greve ou veio novo** (`seed.veio-fundo.g`, ameaça 3): Os mineiros ameaçam parar e alguém propõe procurar veio novo; nada está afirmado sobre onde ele estaria.  
  *Agentes:* `npc.brundar-veio-alto`, `npc.gundrik-muralha-lenta`, `npc.karna-brasa-mansa`, `faction.irmandade-do-veio`, `faction.casa-das-fornalhas`. *Abordagens possíveis:* negociar com os mineiros; ouvir a Casa das Fornalhas; avaliar a proposta de galeria nova (com custo e risco); recusar a busca. *Se ninguém age:* a greve pode ou não acontecer; o veio novo segue só como ideia. *Fonte:* doc 03 S-02 (tensão e desenvolvimentos).

### `city.altavento` — Altavento — A Cidade dos Picos

- **Pequena — Chamados que não eram de ave** (`seed.altavento.p`, ameaça 1): Viajantes dizem ter ouvido chamados que não pareciam de ave; Zhae Garra-de-Névoa quer alguém que escute com ela numa plataforma de vigia.  
  *Agentes:* `npc.zhae-garra-de-nevoa`, `npc.miraq-salto-de-nevoa`. *Abordagens possíveis:* vigília numa plataforma; comparar os relatos dos viajantes; consultar caçadores de neve. *Se ninguém age:* os relatos continuam; pode ser vento, ave ou outra coisa. *Fonte:* doc 01 §2.2 (rumores).
- **Média — Aviso que ninguém quer levar** (`seed.altavento.m`, ameaça 2): Os mensageiros recusam voar quando o vento muda e Iskra Pena-Longa precisa de quem leve um aviso por um passo que eles evitam.  
  *Agentes:* `npc.iskra-pena-longa`, `npc.vireo-vento-rapido`, `faction.rede-dos-passos-altos`. *Abordagens possíveis:* levar o aviso a pé com guia de neve; esperar o vento mudar; combinar repasse por Ninho-Seco ou Rastro-Branco. *Se ninguém age:* o aviso atrasa ou chega por outra via; ninguém é punido pelo atraso. *Liga:* clock.passos. *Fonte:* doc 01 §2.2 (problemas); doc 06 O-02.
- **Grande — Passos fechados antes da hora** (`seed.altavento.g`, ameaça 3): Uma nevasca fechou os passos antes do previsto e a cidade ficou sem parte dos mantimentos; Vireo Vento-Rápido e a Assembleia dos Picos precisam decidir como dividir o que resta.  
  *Agentes:* `npc.vireo-vento-rapido`, `npc.iskra-pena-longa`, `npc.zhae-garra-de-nevoa`, `faction.rede-dos-passos-altos`. *Abordagens possíveis:* inventariar o que resta; abrir depósitos de emergência (com custo de mantimentos); buscar caminho entre os já registrados no doc 07; ouvir os guias de neve. *Se ninguém age:* a falta se agrava com nova nevasca observada; a estação sozinha não agrava. *Liga:* clock.passos; event.nevasca-fecha-passos. *Fonte:* doc 01 §2.2 (problemas); doc 06 CL-07, C-02.

### `settlement.ninho-seco` — Ninho-Seco — O Posto dos Mensageiros (de `city.altavento`)

- **Pequena — O mensageiro que não pousou** (`seed.ninho-seco.p`, ameaça 1): Um mensageiro não pousou em Ninho-Seco e Iskra Pena-Longa quer quem confira os pousos da rota.  
  *Agentes:* `npc.iskra-pena-longa`, `npc.zhae-garra-de-nevoa`. *Abordagens possíveis:* conferir os pousos; ouvir os mensageiros; procurar sinais ao longo da rota usual. *Se ninguém age:* o mensageiro pode aparecer ou não; a carta segue sem entrega. *Fonte:* doc 03 S-03 (desenvolvimentos).
- **Média — Tarifas atrasadas** (`seed.ninho-seco.m`, ameaça 2): As outras cidades pagam o repasse de cartas atrasado e o posto não tem reserva; Iskra pede quem levante o que cada uma deve.  
  *Agentes:* `npc.iskra-pena-longa`, `npc.vireo-vento-rapido`, `faction.rede-dos-passos-altos`. *Abordagens possíveis:* levantar o que cada cidade deve; ouvir a Rede dos Passos Altos; levar o pedido ao Concílio. *Se ninguém age:* o posto segue sem reserva; o fechamento sazonal fica mais provável, sem ser certo. *Fonte:* doc 03 S-03 (tensão).
- **Grande — Nevasca longa sem reserva** (`seed.ninho-seco.g`, ameaça 3): O posto não tem reserva para uma nevasca longa e Iskra teme não poder abrigar quem voa; é preciso decidir de onde vem o mantimento.  
  *Agentes:* `npc.iskra-pena-longa`, `npc.miraq-salto-de-nevoa`, `npc.zhae-garra-de-nevoa`, `faction.rede-dos-passos-altos`. *Abordagens possíveis:* montar depósito de emergência (com custo); pedir mantimento a Altavento; combinar abrigo com Rastro-Branco. *Se ninguém age:* a reserva só é testada com nova nevasca observada; a estação sozinha não é gatilho. *Liga:* clock.passos; event.nevasca-fecha-passos. *Fonte:* doc 03 S-03 (tensão); doc 06 CL-07.

### `settlement.rastro-branco` — Rastro-Branco — A Comunidade da Neve (de `city.altavento`)

- **Pequena — O guia que recusa a travessia** (`seed.rastro-branco.p`, ameaça 1): Um guia de Rastro-Branco recusa uma travessia e Miraq Salto-de-Névoa quer entender o motivo antes que a recusa vire boato.  
  *Agentes:* `npc.miraq-salto-de-nevoa`. *Abordagens possíveis:* ouvir o guia; examinar o trecho; comparar com o que outros guias dizem. *Se ninguém age:* a recusa fica sem explicação; o trecho segue evitado. *Fonte:* doc 03 S-04 (desenvolvimentos).
- **Média — Cota de caça nos meses de fome** (`seed.rastro-branco.m`, ameaça 2): A cidade quer limitar a caça por escassez; a comunidade diz que isso a obriga a descer ao vale; Miraq pede quem leve a conta dos caçadores.  
  *Agentes:* `npc.miraq-salto-de-nevoa`, `npc.vireo-vento-rapido`, `faction.rede-dos-passos-altos`. *Abordagens possíveis:* levantar o que os caçadores trazem; ouvir a cidade; propor cota com exceção para os meses de fome. *Se ninguém age:* a cota segue em disputa; a comunidade decide sozinha o que fazer. *Fonte:* doc 03 S-04 (tensão).
- **Grande — Sinais de avalanche** (`seed.rastro-branco.g`, ameaça 3): Miraq vê sinais de avalanche e a cidade quer limitar a caça; avisar os passos tem custo para quem ouvir e para quem calar.  
  *Agentes:* `npc.miraq-salto-de-nevoa`, `npc.zhae-garra-de-nevoa`, `npc.iskra-pena-longa`. *Abordagens possíveis:* confirmar os sinais com guias; avisar Ninho-Seco e Altavento; propor desvio dos passos; ouvir quem vive no trecho. *Se ninguém age:* a avalanche pode ou não vir; o aviso, se calado, não é punido. *Liga:* clock.passos (ligação não provada). *Fonte:* doc 03 S-04 (obrigação e desenvolvimentos).

### `city.veiaclara` — Veiaclara — A Cidade da Nascente

- **Pequena — Brilho em noite sem lua** (`seed.veiaclara.p`, ameaça 1): Dizem que a água da nascente brilha de leve em noites sem lua; Pelma Gota-Medida quer que alguém anote quando e onde, sem presumir causa.  
  *Agentes:* `npc.pelma-gota-medida`, `npc.aurel-faceta-calma`. *Abordagens possíveis:* observar na Praça do Degelo e no Lago Alto; comparar com o registro dos medidores; ouvir quem viu. *Se ninguém age:* o brilho fica sem registro; pode ser reflexo ou conversa. *Fonte:* doc 01 §2.3 (rumores).
- **Média — O registro antes do degelo** (`seed.veiaclara.m`, ameaça 2): Pelma pede quem confira o registro das comportas contra a Casa das Comportas antes do degelo; as anotações não batem entre si.  
  *Agentes:* `npc.pelma-gota-medida`, `npc.pimm-veio-pequeno`, `faction.casa-das-comportas`. *Abordagens possíveis:* cotejar as anotações; inspecionar as comportas; ouvir os medidores; não presumir fraude (as hipóteses de MP-01 seguem abertas). *Se ninguém age:* o registro segue sem conferência; o degelo acontece quando acontecer. *Liga:* clock.comportas. *Fonte:* doc 01 §2.3 (problemas), MP-01; doc 06 O-03.
- **Grande — A comporta velha e quem paga** (`seed.veiaclara.g`, ameaça 3): Cidades a jusante pedem água sem pagar a manutenção e uma comporta velha pode ceder; o Conselho da Nascente precisa decidir quem arca com o reparo.  
  *Agentes:* `npc.pelma-gota-medida`, `npc.pimm-veio-pequeno`, `faction.casa-das-comportas`. *Abordagens possíveis:* vistoriar a comporta; levantar quem deve manutenção; levar o caso ao Conselho da Nascente; reparo emergencial com custo real. *Se ninguém age:* a comporta só cede com prova nova de registro alterado ou degelo fora de data observado; sem isso, o impasse segue. *Liga:* clock.comportas; event.comporta-cede. *Fonte:* doc 01 §2.3 (problemas); doc 05 §1 (L-02); doc 06 CL-02, C-03.

### `settlement.comporta-velha` — Comporta-Velha — O Distrito das Comportas (de `city.veiaclara`)

- **Pequena — Ferrugem na comporta** (`seed.comporta-velha.p`, ameaça 1): Pimm Veio-Pequeno pede quem registre o estado da ferrugem numa comporta velha, para que o Conselho tenha algo que não possa ignorar.  
  *Agentes:* `npc.pimm-veio-pequeno`. *Abordagens possíveis:* inspecionar e documentar; ouvir os engenheiros; levar o registro a Pelma. *Se ninguém age:* a ferrugem segue sem registro; o risco permanece, sem dano certo. *Liga:* clock.comportas. *Fonte:* doc 03 S-05 (tensão); doc 06 CL-02.
- **Média — A verba que ninguém paga** (`seed.comporta-velha.m`, ameaça 2): Ninguém paga a manutenção das comportas; Pimm pede quem leve a conta às cidades a jusante.  
  *Agentes:* `npc.pimm-veio-pequeno`, `npc.pelma-gota-medida`, `faction.casa-das-comportas`. *Abordagens possíveis:* levantar o que cada cidade usa e deve; levar o pedido ao Conselho da Nascente; propor acordo de manutenção. *Se ninguém age:* a verba segue sem pagamento; a comporta continua em uso. *Liga:* clock.comportas. *Fonte:* doc 03 S-05 (interesse e tensão); doc 06 C-03.
- **Grande — Reparo emergencial ou falha parcial** (`seed.comporta-velha.g`, ameaça 3): Uma comporta velha ameaça ceder e não há verba; Pimm precisa decidir entre reparo emergencial e falha parcial aceita.  
  *Agentes:* `npc.pimm-veio-pequeno`, `npc.pelma-gota-medida`, `faction.casa-das-comportas`. *Abordagens possíveis:* reparar com material emergencial (custo real); isolar o trecho; avisar as cidades a jusante pela Lei do Aviso. *Se ninguém age:* a comporta só cede com degelo fora de data ou prova nova de registro alterado. *Liga:* clock.comportas; event.comporta-cede. *Fonte:* doc 03 S-05 (desenvolvimentos); doc 05 §1 (L-01, L-02).

### `settlement.lago-de-cima` — Lago-de-Cima — A Vila do Gelo (de `city.veiaclara`)

- **Pequena — O visitante do lago** (`seed.lago-de-cima.p`, ameaça 1): Um visitante quer olhar o fundo do lago e Aurel Faceta-Calma pede quem o acompanhe sem deixá-lo tocar a água.  
  *Agentes:* `npc.aurel-faceta-calma`, `npc.pelma-gota-medida`. *Abordagens possíveis:* acompanhar o visitante; explicar o limite da vila; ouvir o que ele procura. *Se ninguém age:* o visitante vai embora ou insiste; a vila decide sozinha. *Fonte:* doc 03 S-06 (desenvolvimentos).
- **Média — O limite que ninguém prova** (`seed.lago-de-cima.m`, ameaça 2): A cidade quer aprofundar a coleta e a vila recusa por um limite que não sabe provar; Aurel pede quem ajude a testar sem tocar o fundo.  
  *Agentes:* `npc.aurel-faceta-calma`, `npc.pelma-gota-medida`. *Abordagens possíveis:* testar o limite por ressonância; ouvir os coletores; comparar a coleta atual com a proposta. *Se ninguém age:* a coleta nova segue em espera; o limite continua sem prova. *Fonte:* doc 03 S-06 (tensão).
- **Grande — A disputa sobre o fundo do lago** (`seed.lago-de-cima.g`, ameaça 3): A cidade decide aprofundar a coleta contra a recusa da vila; o Conselho da Nascente precisa julgar.  
  *Agentes:* `npc.aurel-faceta-calma`, `npc.pelma-gota-medida`, `npc.pimm-veio-pequeno`. *Abordagens possíveis:* levar a disputa ao Conselho; propor coleta em etapas com vigília; ouvir os Cristalinos sobre o lago. *Se ninguém age:* a coleta pode avançar ou ser adiada; nenhuma regra muda por conta própria. *Fonte:* doc 03 S-06 (tensão e desenvolvimentos); doc 04 §9.

### `city.barcaalta` — Barcaalta — O Porto da Cabeceira

- **Pequena — O som do rio** (`seed.barcaalta.p`, ameaça 1): Marinheiros do Cais Alto juram que o rio muda de som antes de cheia e pedem que alguém anote quando isso acontece.  
  *Agentes:* `npc.torgrim-corda-dupla`, `npc.seis-eixos`. *Abordagens possíveis:* anotar o som e o momento; comparar com as cheias lembradas; ouvir os barqueiros mais velhos. *Se ninguém age:* a história fica sem registro; pode ser costume de quem vive no rio. *Fonte:* doc 01 §2.4 (rumores).
- **Média — Carga contra taxa** (`seed.barcaalta.m`, ameaça 2): Torgrim Corda-Dupla pede quem confira a carga de uma barca sem registro contra a taxa paga, antes que a reputação do cais sofra.  
  *Agentes:* `npc.torgrim-corda-dupla`, `npc.seis-eixos`, `faction.companhia-da-descida`. *Abordagens possíveis:* conferir carga e taxa; ouvir as famílias do cais; levar a diferença à Mesa do Cais. *Se ninguém age:* a barca fica retida ou segue sem conferência; as famílias continuam se culpando. *Liga:* clock.cais. *Fonte:* doc 01 §2.4 (problemas); doc 05 §1 (L-09); doc 06 O-04.
- **Grande — A barca sem remador** (`seed.barcaalta.g`, ameaça 3): Corre o rumor de uma barca que desceu sem remador e chegou inteira; o Registro da Descida e as taxas não batem e a Mesa do Cais quer saber se a barca existiu.  
  *Agentes:* `npc.torgrim-corda-dupla`, `npc.seis-eixos`, `faction.companhia-da-descida`, `faction.casa-dos-despertos`. *Abordagens possíveis:* cotejar o Registro da Descida com as taxas; ouvir testemunhas do cais; examinar amarrações e cordas; não presumir a causa (hipóteses de MP-05) nem descrever o que há rio abaixo. *Se ninguém age:* o cais pode fechar por conferência só com nova barca sem registro ou novo conflito de taxas; o rumor segue. *Liga:* clock.cais; event.cais-fechado. *Fonte:* doc 01 §2.4 (rumores), MP-05; doc 05 §3.3; doc 06 CL-03.

### `settlement.cordoaria` — Cordoaria — O Bairro das Cordas (de `city.barcaalta`)

- **Pequena — A corda que rompe** (`seed.cordoaria.p`, ameaça 1): Uma corda do cais rompeu e Torgrim Corda-Dupla quer quem inspecione o lote antes que outra ceda.  
  *Agentes:* `npc.torgrim-corda-dupla`. *Abordagens possíveis:* inspecionar o lote; ouvir os cordoeiros; comparar com o último lote entregue. *Se ninguém age:* o lote segue em uso; o risco permanece, sem dano certo. *Fonte:* doc 03 S-07 (desenvolvimentos).
- **Média — A encomenda grande** (`seed.cordoaria.m`, ameaça 2): Uma encomenda grande chega ou há suspeita de fraude de qualidade; Torgrim pede quem confira, sem presumir qual das duas.  
  *Agentes:* `npc.torgrim-corda-dupla`, `npc.seis-eixos`, `faction.companhia-da-descida`. *Abordagens possíveis:* conferir o lote contra o pedido; ouvir os mestres cordoeiros; comparar com a corda antiga. *Se ninguém age:* a encomenda é aceita sem conferência ou fica parada; a dúvida sobre qualidade continua. *Fonte:* doc 03 S-07 (tensão e desenvolvimentos).
- **Grande — O cais compra de fora** (`seed.cordoaria.g`, ameaça 3): Quando o preço sobe, o cais compra de fora e as oficinas ficam sem encomenda; Torgrim precisa de uma saída para as famílias.  
  *Agentes:* `npc.torgrim-corda-dupla`, `npc.seis-eixos`, `faction.companhia-da-descida`. *Abordagens possíveis:* negociar preço estável; propor preferência no cais; ouvir os cordoeiros; levar o caso à Mesa do Cais. *Se ninguém age:* as oficinas seguem sem encomenda; o cais pode voltar a comprar da vila ou não. *Liga:* clock.cais (ligação não provada). *Fonte:* doc 03 S-07 (interesse e tensão).

### `settlement.primeira-rampa` — Primeira-Rampa — O Núcleo do Registro (de `city.barcaalta`)

- **Pequena — Páginas do Registro** (`seed.primeira-rampa.p`, ameaça 1): Seis-Eixos pede quem confira algumas páginas do Registro da Descida cuja taxa não consta.  
  *Agentes:* `npc.seis-eixos`, `npc.torgrim-corda-dupla`. *Abordagens possíveis:* conferir as páginas; comparar com o registro de carga; anotar o que falta. *Se ninguém age:* as páginas seguem sem conferência; a diferença permanece. *Liga:* clock.cais. *Fonte:* doc 03 S-08 (tensão); doc 06 CL-03.
- **Média — Auditoria dos dois registros** (`seed.primeira-rampa.m`, ameaça 2): Os registros de carga e de taxas não batem e cada lado culpa o outro; Seis-Eixos quer uma auditoria que ambos aceitem.  
  *Agentes:* `npc.seis-eixos`, `npc.torgrim-corda-dupla`, `faction.companhia-da-descida`. *Abordagens possíveis:* propor auditoria conjunta; cotejar os registros; ouvir os dois lados; manter os dois válidos até conferência. *Se ninguém age:* os registros seguem em conflito; a Lei do Cais vale como está. *Liga:* clock.cais. *Fonte:* doc 03 S-08 (tensão); doc 05 §1 (L-09).
- **Grande — Os construtos de carga no registro** (`seed.primeira-rampa.g`, ameaça 3): Os construtos de carga pedem reconhecimento como parte do registro e o pedido entra na disputa entre carga e taxa.  
  *Agentes:* `npc.seis-eixos`, `npc.quinze-pinos`, `npc.torgrim-corda-dupla`, `faction.casa-dos-despertos`. *Abordagens possíveis:* ouvir os construtos de carga; propor regra provisória de uso; levar o pedido à Mesa do Cais; não decidir a origem dos Despertos. *Se ninguém age:* o pedido segue sem resposta; nenhuma regra muda por conta própria. *Liga:* event.pedido-desperto; clock.pecas (ligação não provada). *Fonte:* doc 03 S-08 (interesse e desenvolvimentos); doc 01 C-04.

### `city.portao-cinza` — Portão-Cinza — O Limiar do Esporão

- **Pequena — Luzes que não vieram de tochas** (`seed.portao-cinza.p`, ameaça 1): Um vigia aposentado fala de luzes que não vieram de tochas; Thaurak Escama-Fria quer um registro dos relatos, sem presumir origem.  
  *Agentes:* `npc.thaurak-escama-fria`, `npc.varkhan-escama-vigia`. *Abordagens possíveis:* ouvir o vigia aposentado; observar as galerias à noite; comparar os relatos com o Livro de Passagens. *Se ninguém age:* os relatos ficam sem explicação; podem ser reflexo, cristal natural ou outra coisa. *Fonte:* doc 01 §2.5 (rumores), MP-02.
- **Média — A caravana e o registro duvidoso** (`seed.portao-cinza.m`, ameaça 3): Thaurak pede quem guie uma caravana por um registro duvidoso do Livro de Passagens; qualquer erro pode virar delito de custódia.  
  *Agentes:* `npc.thaurak-escama-fria`, `npc.varkhan-escama-vigia`, `npc.skit-fenda-quente`, `faction.vigias-do-limiar`. *Abordagens possíveis:* conferir o registro antes de guiar; obter a cópia do Livro numa casa de ofício; combinar a travessia com os vigias; recusar a guia. *Se ninguém age:* a caravana espera ou segue por conta própria; a Mesa não age sem caso concreto. *Liga:* clock.passagens. *Fonte:* doc 01 §2.5 (problemas), MP-02; doc 05 §1 (L-04); doc 06 O-05.
- **Grande — Quem sobe e quem desce sem registro** (`seed.portao-cinza.g`, ameaça 3): Há mais viajantes sem registro e as tarifas seguem em disputa; a Mesa do Portão pondera exigir registro duplo.  
  *Agentes:* `npc.thaurak-escama-fria`, `npc.varkhan-escama-vigia`, `npc.skit-fenda-quente`, `faction.vigias-do-limiar`, `faction.companhia-da-descida`. *Abordagens possíveis:* comparar tarifas pagas e Livro; ouvir caravaneiros; propor tarifa comum provisória; auditar a escala dos vigias; não presumir quem protege os sem registro (hipóteses de MP-02). *Se ninguém age:* a Mesa só decide o registro duplo com nova página arrancada ou novo delito de custódia; sem isso, a disputa segue. *Liga:* clock.passagens; event.registro-duplo. *Fonte:* doc 01 §2.5 (problemas); doc 05 §1 (L-04); doc 06 CL-04, C-05.

### `settlement.albergue-de-cima` — Albergue-de-Cima — A Hospedagem Sazonal (de `city.portao-cinza`)

- **Pequena — O hóspede sem registro** (`seed.albergue-de-cima.p`, ameaça 1): Um hóspede chega sem registro e Skit Fenda-Quente pede quem ajude a acolhê-lo sem perguntar a origem, como manda o costume.  
  *Agentes:* `npc.skit-fenda-quente`, `npc.varkhan-escama-vigia`. *Abordagens possíveis:* acolher e observar; conversar com o hóspede; combinar com Varkhan o que será anotado. *Se ninguém age:* o hóspede parte ou fica; os vigias decidem sozinhos se vão anotá-lo. *Fonte:* doc 03 S-09 (desenvolvimentos); doc 05 §1 (L-10).
- **Média — Registrar todos os hóspedes** (`seed.albergue-de-cima.m`, ameaça 2): Os vigias querem registrar todos os hóspedes e o albergue diz que isso afasta os clientes; Skit pede quem proponha um meio-termo.  
  *Agentes:* `npc.skit-fenda-quente`, `npc.thaurak-escama-fria`, `faction.vigias-do-limiar`. *Abordagens possíveis:* propor registro mínimo; ouvir hóspedes e vigias; testar o meio-termo numa temporada. *Se ninguém age:* o registro segue em disputa; o albergue decide por conta própria quem anotar. *Liga:* clock.passagens. *Fonte:* doc 03 S-09 (tensão); doc 06 CL-04.
- **Grande — Portas abertas com o portão fechado** (`seed.albergue-de-cima.g`, ameaça 3): O albergue quer manter as portas abertas mesmo quando o portão fecha; uma inspeção pode forçar o fechamento de inverno.  
  *Agentes:* `npc.skit-fenda-quente`, `npc.thaurak-escama-fria`, `npc.varkhan-escama-vigia`. *Abordagens possíveis:* negociar o que fica aberto; ouvir os vigias; propor regra de inverno; documentar quem abrigou quem. *Se ninguém age:* a inspeção pode vir ou não; só um novo delito de custódia observado endurece a regra. *Liga:* event.registro-duplo (ligação não provada). *Fonte:* doc 03 S-09 (interesse e desenvolvimentos).

### `settlement.galeria-alta` — Galeria-Alta — O Núcleo dos Vigias (de `city.portao-cinza`)

- **Pequena — Turno vazio** (`seed.galeria-alta.p`, ameaça 1): Um turno ficou sem vigia e Varkhan Escama-Vigia pede quem o cubra por uma noite.  
  *Agentes:* `npc.varkhan-escama-vigia`. *Abordagens possíveis:* cobrir o turno; ouvir os vigias; anotar o que aconteceu sem vigia. *Se ninguém age:* o turno segue vazio; sem dano certo. *Fonte:* doc 03 S-10 (desenvolvimentos).
- **Média — Faltam vigias** (`seed.galeria-alta.m`, ameaça 2): Faltam vigias e os turnos são longos; Varkhan quer quem proponha recrutamento sem baixar o rigor.  
  *Agentes:* `npc.varkhan-escama-vigia`, `npc.thaurak-escama-fria`, `faction.vigias-do-limiar`. *Abordagens possíveis:* propor recrutamento; ouvir os vigias mais antigos; sugerir turnos menores com apoio das casas de ofício. *Se ninguém age:* os turnos seguem longos; a escala é mantida como está. *Fonte:* doc 03 S-10 (interesse e tensão).
- **Grande — As lacunas do Livro** (`seed.galeria-alta.g`, ameaça 3): O Livro de Passagens tem lacunas que ninguém quer explicar; Varkhan precisa de quem ajude a descobrir, sem presumir quem as causou.  
  *Agentes:* `npc.varkhan-escama-vigia`, `npc.thaurak-escama-fria`, `npc.skit-fenda-quente`, `faction.vigias-do-limiar`. *Abordagens possíveis:* conferir as lacunas com cópias das casas de ofício; ouvir os vigias em particular; não presumir culpa (hipóteses de MP-02). *Se ninguém age:* as lacunas seguem sem explicação; só nova página arrancada observada piora o quadro. *Liga:* clock.passagens. *Fonte:* doc 03 S-10 (tensão e desenvolvimentos); doc 01 MP-02.

### `city.sinuosa` — Sinuosa — A Cidade dos Mil Corredores

- **Pequena — O corredor onde não havia** (`seed.sinuosa.p`, ameaça 1): Um guia conta de um corredor onde não havia nenhum e Nael Curva-Antiga quer que alguém confirme com marca e registro, sem presumir causa.  
  *Agentes:* `npc.nael-curva-antiga`, `npc.ilmeth-curva-segura`. *Abordagens possíveis:* marcar a entrada e voltar; comparar com os mapas da Rotunda; ouvir outros guias. *Se ninguém age:* o corredor segue sem confirmação; pode ser erro de percurso. *Fonte:* doc 01 §2.6 (rumores), MP-06.
- **Média — Um corredor contra os mapas** (`seed.sinuosa.m`, ameaça 2): Nael pede quem confira um corredor novo contra os mapas rivais que circulam, para que a Rotunda tenha uma base comum.  
  *Agentes:* `npc.nael-curva-antiga`, `npc.ilmeth-curva-segura`, `npc.pip-vapor-curto`, `faction.casa-dos-guias`. *Abordagens possíveis:* percorrer com guia; cotejar os mapas; procurar marcas de ferramenta; ouvir as famílias de guias. *Se ninguém age:* os mapas continuam rivais; os guias se arriscam por conta própria. *Liga:* event.mapa-rival. *Fonte:* doc 01 §2.6 (problemas), MP-06; doc 06 O-06.
- **Grande — Mapas rivais na Rotunda** (`seed.sinuosa.g`, ameaça 3): Cada família de guias jura que o seu mapa está certo e acusa o mercado de vender o errado; a Rotunda dos Mapas, presidida por Ilmeth, precisa arbitrar pela Lei do Mapa.  
  *Agentes:* `npc.ilmeth-curva-segura`, `npc.nael-curva-antiga`, `npc.pip-vapor-curto`, `faction.casa-dos-guias`, `faction.mercadores-de-fendas`. *Abordagens possíveis:* reunir as cópias de cada família; ouvir os guias; verificar o que cada mapa promete e o que o guia entrega; propor mapa de arbitragem (com custo para a Rotunda). *Se ninguém age:* as acusações seguem; a Rotunda pode adiar a arbitragem. *Liga:* event.mapa-rival. *Fonte:* doc 01 §2.6 (problemas); doc 05 §1 (L-05); doc 06 C-06.

### `settlement.fenda-mercado` — Fenda-Mercado — O Mercado de Fendas (de `city.sinuosa`)

- **Pequena — A compra suspeita** (`seed.fenda-mercado.p`, ameaça 1): Pip Vapor-Curto vê um comprador levar um mapa sem discutir preço e pede quem o acompanhe discretamente.  
  *Agentes:* `npc.pip-vapor-curto`. *Abordagens possíveis:* seguir o comprador; ouvir quem vendeu; comparar o mapa com o original. *Se ninguém age:* a compra fica sem explicação; pode ser pressa. *Fonte:* doc 03 S-11 (desenvolvimentos).
- **Média — O mapa falso** (`seed.fenda-mercado.m`, ameaça 2): Suspeita-se que um mapa foi vendido sabendo-se errado, o que é fraude pela Lei do Mapa; Pip quer provar ou afastar.  
  *Agentes:* `npc.pip-vapor-curto`, `npc.nael-curva-antiga`, `faction.mercadores-de-fendas`, `faction.casa-dos-guias`. *Abordagens possíveis:* comparar o mapa com cópias da Rotunda; ouvir vendedor e comprador; consultar a Casa dos Guias; não presumir fraude. *Se ninguém age:* a suspeita segue; ninguém é punido sem prova. *Liga:* event.mapa-rival (ligação não provada). *Fonte:* doc 03 S-11 (desenvolvimentos); doc 05 §1 (L-05).
- **Grande — A briga das famílias** (`seed.fenda-mercado.g`, ameaça 3): As famílias de guias acusam o mercado de vender o mapa errado e a briga ameaça a neutralidade do mercado.  
  *Agentes:* `npc.pip-vapor-curto`, `npc.nael-curva-antiga`, `npc.ilmeth-curva-segura`, `faction.mercadores-de-fendas`, `faction.casa-dos-guias`. *Abordagens possíveis:* mediar entre famílias e mercado; propor registro de origem dos mapas; levar o caso à Rotunda dos Mapas. *Se ninguém age:* a briga pode esfriar ou piorar; o mercado segue aberto. *Liga:* event.mapa-rival. *Fonte:* doc 03 S-11 (tensão); doc 06 C-06.

### `settlement.boca-larga` — Boca-Larga — O Acesso Externo (de `city.sinuosa`)

- **Pequena — A recusa de guia** (`seed.boca-larga.p`, ameaça 1): Um viajante recusa o guia em Boca-Larga e Ilmeth Curva-Segura pede quem o convença ou, ao menos, registre a recusa.  
  *Agentes:* `npc.ilmeth-curva-segura`. *Abordagens possíveis:* conversar com o viajante; registrar a recusa; oferecer outro guia. *Se ninguém age:* o viajante entra sozinho ou desiste; o registro fica incompleto. *Fonte:* doc 03 S-12 (desenvolvimentos).
- **Média — O viajante perdido** (`seed.boca-larga.m`, ameaça 2): Um viajante entrou sem guia e se perdeu; Ilmeth pede quem ajude a procurá-lo.  
  *Agentes:* `npc.ilmeth-curva-segura`, `npc.nael-curva-antiga`, `faction.casa-dos-guias`. *Abordagens possíveis:* organizar busca com guias; reconstruir o percurso; ouvir quem o viu entrar. *Se ninguém age:* o viajante pode ser achado por NPCs ou não; sem automatismo. *Liga:* event.mapa-rival (ligação não provada). *Fonte:* doc 03 S-12 (tensão); doc 06 §4 (C-06).
- **Grande — O preço de exigir guia** (`seed.boca-larga.g`, ameaça 3): Boca-Larga quer que ninguém entre sem guia, mesmo pagando; viajantes e mercadores reclamam e a Rotunda pode ter de decidir.  
  *Agentes:* `npc.ilmeth-curva-segura`, `npc.nael-curva-antiga`, `npc.pip-vapor-curto`, `faction.casa-dos-guias`, `faction.mercadores-de-fendas`. *Abordagens possíveis:* ouvir viajantes e guias; propor exceções documentadas; levar o caso à Rotunda dos Mapas. *Se ninguém age:* a regra segue como está; a Rotunda não decide sem caso levado a ela. *Fonte:* doc 03 S-12 (interesse e tensão); doc 05 §1 (L-05).

### `city.cumieira` — Cumieira — A Casa dos Grandes

- **Pequena — A pedra que se moveu** (`seed.cumieira.p`, ameaça 1): Um pedreiro conta que uma pedra se moveu sozinha; Brunhild Degrau-Fundo quer que alguém marque o lugar e observe.  
  *Agentes:* `npc.brunhild-degrau-fundo`, `npc.gorrak-pedra-grande`, `npc.maur-camada-velha`. *Abordagens possíveis:* marcar a pedra; ouvir o pedreiro; consultar Maur Camada-Velha, só em particular. *Se ninguém age:* a pedra segue sem explicação; pode ser engano de medida. *Fonte:* doc 01 §2.7 (rumores); doc 01 N-05 (Maur só fala em particular).
- **Média — A passagem dos menores** (`seed.cumieira.m`, ameaça 2): Brunhild pede quem meça uma passagem para os menores sem ofender o salão; Maur ou Gorrak podem ser ouvidos, ainda sem arbitragem formal.  
  *Agentes:* `npc.brunhild-degrau-fundo`, `npc.gorrak-pedra-grande`, `npc.maur-camada-velha`, `faction.liga-dos-degraus`. *Abordagens possíveis:* medir a passagem; propor acesso mínimo à escala dos menores; ouvir os grandes em particular; pesar obra e honra. *Se ninguém age:* o pedido fica parado; os menores seguem sem acesso. *Liga:* event.degrau-na-escala. *Fonte:* doc 01 §2.7 (problemas); doc 05 §1 (L-07); doc 06 O-07.
- **Grande — O caminho pelos pastos** (`seed.cumieira.g`, ameaça 3): A cidade quer abrir um caminho pelos pastos do clã e o Salão dos Juramentos de Sangue vê nisso uma quebra de juramento.  
  *Agentes:* `npc.gorrak-pedra-grande`, `npc.brunhild-degrau-fundo`, `npc.maur-camada-velha`, `faction.conselho-de-sangue`, `faction.liga-dos-degraus`. *Abordagens possíveis:* ouvir o clã e a cidade; propor traçado alternativo com custo de obra; chamar a Liga dos Degraus como mediadora; deixar a decisão ao Salão. *Se ninguém age:* a cidade pode adiar o caminho ou abri-lo sem acordo; sem automatismo. *Liga:* event.caminho-nos-pastos. *Fonte:* doc 01 §2.7 (problemas); doc 03 S-14 (tensão); doc 06 C-07.

### `settlement.degrau-pequeno` — Degrau-Pequeno — O Bairro dos Menores (de `city.cumieira`)

- **Pequena — O degrau que cedeu** (`seed.degrau-pequeno.p`, ameaça 1): Um acidente numa escada dos pequenos deixou um ferido leve e Brunhild Degrau-Fundo pede quem examine o degrau.  
  *Agentes:* `npc.brunhild-degrau-fundo`. *Abordagens possíveis:* examinar o degrau; ouvir os moradores; propor conserto simples. *Se ninguém age:* o degrau segue como está; o risco permanece. *Fonte:* doc 03 S-13 (desenvolvimentos).
- **Média — A obra recusada** (`seed.degrau-pequeno.m`, ameaça 2): Um pedido de degraus e portas no tamanho certo foi recusado; Brunhild pede quem documente onde faltam.  
  *Agentes:* `npc.brunhild-degrau-fundo`, `npc.gorrak-pedra-grande`, `faction.liga-dos-degraus`. *Abordagens possíveis:* documentar os pontos sem acesso; apresentar à Liga dos Degraus; ouvir os grandes em particular. *Se ninguém age:* o pedido segue recusado; o bairro adapta o que pode. *Liga:* event.degrau-na-escala. *Fonte:* doc 03 S-13 (interesse e desenvolvimentos); doc 05 §1 (L-07).
- **Grande — A ofensa à casa antiga** (`seed.degrau-pequeno.g`, ameaça 3): Os grandes veem os pedidos do bairro como ofensa à casa antiga; sem arbitragem formal, alguém precisa mediar.  
  *Agentes:* `npc.brunhild-degrau-fundo`, `npc.gorrak-pedra-grande`, `npc.maur-camada-velha`, `faction.liga-dos-degraus`, `faction.conselho-de-sangue`. *Abordagens possíveis:* mediar em particular; propor acesso mínimo por escala; pesar obra e honra de clã. *Se ninguém age:* a tensão pode esfriar ou crescer; nada muda sem acordo. *Liga:* event.degrau-na-escala. *Fonte:* doc 03 S-13 (tensão); doc 06 C-07.

### `settlement.pasto-de-cume` — Pasto-de-Cume — O Pasto de Altitude (de `city.cumieira`)

- **Pequena — O rebanho perdido** (`seed.pasto-de-cume.p`, ameaça 1): Um rebanho de altitude se perdeu e Gorrak Pedra-Grande pede quem ajude a procurá-lo antes da neve.  
  *Agentes:* `npc.gorrak-pedra-grande`. *Abordagens possíveis:* procurar com pastores; seguir rastros; avisar a cidade. *Se ninguém age:* o rebanho pode voltar sozinho ou não; a perda é do clã. *Fonte:* doc 03 S-14 (desenvolvimentos).
- **Média — Pastos livres de caravanas** (`seed.pasto-de-cume.m`, ameaça 2): O clã quer os pastos livres de obras e de caravanas; Gorrak pede quem acompanhe uma caravana por outro desvio.  
  *Agentes:* `npc.gorrak-pedra-grande`, `npc.brunhild-degrau-fundo`, `faction.conselho-de-sangue`. *Abordagens possíveis:* acompanhar a caravana; propor desvio; ouvir o clã e a cidade. *Se ninguém age:* a caravana passa pelos pastos ou espera; o clã decide como reagir. *Fonte:* doc 03 S-14 (interesse).
- **Grande — O juramento posto à prova** (`seed.pasto-de-cume.g`, ameaça 3): Se a cidade abrir o caminho sem consultar o clã, o juramento será posto à prova e lã e carne podem chegar atrasadas; Gorrak teme uma ruptura.  
  *Agentes:* `npc.gorrak-pedra-grande`, `npc.maur-camada-velha`, `npc.brunhild-degrau-fundo`, `faction.conselho-de-sangue`, `faction.liga-dos-degraus`. *Abordagens possíveis:* propor consulta formal ao clã; chamar a Liga dos Degraus como mediadora; ouvir Maur em particular. *Se ninguém age:* o clã pode aceitar, protestar ou romper; a cidade pode adiar. *Liga:* event.caminho-nos-pastos. *Fonte:* doc 03 S-14 (tensão e desenvolvimentos); doc 06 C-07.

### `city.rodaviva` — Rodaviva — A Cidade que Despertou

- **Pequena — A peça que ninguém soube nomear** (`seed.rodaviva.p`, ameaça 1): Um relojoeiro guarda uma peça que ninguém soube dizer para que serve; Fenwick Mola-Solta quer quem a descreva e desenhe, sem presumir função.  
  *Agentes:* `npc.fenwick-mola-solta`, `npc.primeira-peca`. *Abordagens possíveis:* descrever e desenhar a peça; mostrá-la a outras oficinas; perguntar a Primeira-Peça, que pode não responder. *Se ninguém age:* a peça segue guardada sem função conhecida. *Fonte:* doc 01 §2.8 (rumores), MP-03.
- **Média — A peça rara e a oficina** (`seed.rodaviva.m`, ameaça 2): Fenwick pede quem rastreie uma peça rara até a oficina que a guarda, antes que cada oficina culpe outra.  
  *Agentes:* `npc.fenwick-mola-solta`, `npc.primeira-peca`, `faction.oficinas-reunidas`. *Abordagens possíveis:* consultar a Casa das Peças; seguir o registro de oficina; ouvir os oficineiros; não presumir culpa. *Se ninguém age:* a peça segue perdida e as acusações continuam; nenhuma oficina fecha por conta disso. *Liga:* clock.pecas; event.peca-sumida. *Fonte:* doc 01 §2.8 (problemas); doc 06 O-08.
- **Grande — Falha em cascata** (`seed.rodaviva.g`, ameaça 3): As oficinas dependem umas das outras e a falta de uma peça pode parar rodas e guardas-construtos; Fenwick precisa saber onde a cadeia é mais frágil.  
  *Agentes:* `npc.fenwick-mola-solta`, `npc.primeira-peca`, `npc.quinze-pinos`, `faction.oficinas-reunidas`, `faction.casa-dos-despertos`. *Abordagens possíveis:* mapear quem depende de qual peça; ouvir oficinas maiores e menores; propor registro na Casa das Peças; escolher onde reforçar primeiro (cada escolha tem custo). *Se ninguém age:* a falha só avança com nova peça sumida; sem isso, a fragilidade permanece sem dano certo. *Liga:* clock.pecas; event.peca-sumida. *Fonte:* doc 01 §2.8 (defesas e vulnerabilidades); doc 05 §2.2 (DR-05, DR-06); doc 06 CL-06.

### `settlement.roda-menor` — Roda-Menor — O Distrito das Rodas (de `city.rodaviva`)

- **Pequena — A roda parada** (`seed.roda-menor.p`, ameaça 1): Uma roda de Roda-Menor parou e Fenwick Mola-Solta pede quem examine o eixo antes que a acusação de peça sumida apareça.  
  *Agentes:* `npc.fenwick-mola-solta`. *Abordagens possíveis:* examinar o eixo; ouvir os relojoeiros; comparar com a última manutenção. *Se ninguém age:* a roda segue parada; as oficinas dependentes esperam. *Fonte:* doc 03 S-15 (desenvolvimentos).
- **Média — A vila acusada** (`seed.roda-menor.m`, ameaça 2): Peças raras somem e a vila é acusada; Fenwick pede quem ajude a limpar ou confirmar a suspeita.  
  *Agentes:* `npc.fenwick-mola-solta`, `npc.primeira-peca`, `faction.oficinas-reunidas`. *Abordagens possíveis:* consultar a Casa das Peças; ouvir as outras oficinas; seguir o registro de peças; não presumir culpa. *Se ninguém age:* a acusação segue; a vila perde confiança, sem dano certo. *Liga:* clock.pecas; event.peca-sumida. *Fonte:* doc 03 S-15 (tensão); doc 06 C-08.
- **Grande — Oficinas menores paradas** (`seed.roda-menor.g`, ameaça 3): Sem peça, as oficinas menores param; a Assembleia pode votar foro provisório ou uma oficina fechar.  
  *Agentes:* `npc.fenwick-mola-solta`, `npc.primeira-peca`, `npc.quinze-pinos`, `faction.oficinas-reunidas`, `faction.casa-dos-despertos`. *Abordagens possíveis:* levantar quem parou e por quê; propor registro de peças; ouvir a Assembleia; evitar decidir a origem dos Despertos. *Se ninguém age:* só nova peça sumida observada avança a situação; sem isso, a falta de peça segue sem fechar oficina. *Liga:* clock.pecas; event.peca-sumida. *Fonte:* doc 03 S-15; doc 05 §2.2 (DR-05); doc 06 CL-06.

### `settlement.oficina-sem-dono` — Oficina-Sem-Dono — A Casa dos Despertos (de `city.rodaviva`)

- **Pequena — O construto que reagiu** (`seed.oficina-sem-dono.p`, ameaça 1): Um construto do núcleo reagiu de um modo que ninguém esperava e Quinze-Pinos pede quem acompanhe, sem presumir causa.  
  *Agentes:* `npc.quinze-pinos`, `npc.primeira-peca`. *Abordagens possíveis:* observar o construto; ouvir Primeira-Peça, que pode não responder; registrar o que aconteceu sem rótulo. *Se ninguém age:* o episódio fica sem explicação; pode ser defeito. *Fonte:* doc 03 S-16 (desenvolvimentos); doc 01 MP-03.
- **Média — Reivindicados como propriedade** (`seed.oficina-sem-dono.m`, ameaça 2): Algumas oficinas reclamam construtos do núcleo como propriedade; Quinze-Pinos pede quem registre quais e por quê.  
  *Agentes:* `npc.quinze-pinos`, `npc.fenwick-mola-solta`, `faction.casa-dos-despertos`, `faction.oficinas-reunidas`. *Abordagens possíveis:* listar as reivindicações; ouvir as oficinas; levar o registro à Assembleia sem pedir decisão ainda. *Se ninguém age:* as reivindicações seguem em aberto; os construtos não votam. *Liga:* clock.pecas (ligação não provada). *Fonte:* doc 03 S-16 (tensão); doc 04 §9.
- **Grande — O pedido de reconhecimento** (`seed.oficina-sem-dono.g`, ameaça 3): O núcleo faz um pedido formal de reconhecimento legal e do direito de recusar tarefas; a cidade precisa definir um foro.  
  *Agentes:* `npc.quinze-pinos`, `npc.primeira-peca`, `npc.seis-eixos`, `npc.fenwick-mola-solta`, `faction.casa-dos-despertos`, `faction.oficinas-reunidas`. *Abordagens possíveis:* redigir o pedido com os construtos; levar à Assembleia; propor foro provisório; não decidir a origem dos Despertos nem se são pessoas ou propriedade. *Se ninguém age:* a Assembleia discute sem decidir; os construtos sem dono seguem sem voto. *Liga:* event.pedido-desperto; clock.pecas. *Fonte:* doc 03 S-16 (interesse e desenvolvimentos); doc 05 §1 (lacuna).

### `city.geodalia` — Geodália — A Cidade das Facetas

- **Pequena — A faceta de muitos rostos** (`seed.geodalia.p`, ameaça 1): Lapidadores contam de uma faceta que mostra um rosto diferente a cada pessoa; Aurea Faceta-Longa quer registrar o que cada um diz ter visto, sem sugerir.  
  *Agentes:* `npc.aurea-faceta-longa`, `npc.tuck-lente-torta`. *Abordagens possíveis:* colher relatos separados; observar com luz variada; comparar os relatos. *Se ninguém age:* os relatos ficam anedóticos; pode ser reflexo. *Fonte:* doc 01 §2.9 (rumores).
- **Média — Cota sem fazer a caverna cantar** (`seed.geodalia.m`, ameaça 2): Aurea pede quem meça o cristal tirado sem fazer a caverna cantar, para dar aos lapidadores uma cota que todos aceitem.  
  *Agentes:* `npc.aurea-faceta-longa`, `npc.iridel-luz-partida`, `npc.tuck-lente-torta`, `faction.coro-das-facetas`. *Abordagens possíveis:* medir o cristal tirado; testar o eco com cada retirada; ouvir os lapidadores; propor cota por faceta. *Se ninguém age:* os lapidadores seguem discordando; a caverna pode ou não cantar. *Liga:* event.faceta-rachada. *Fonte:* doc 01 §2.9 (problemas); doc 05 §1 (L-06); doc 06 O-09.
- **Grande — Os sinais de luz em risco** (`seed.geodalia.g`, ameaça 3): Depois de um tremor que rachou uma faceta antiga, as cotas apertam e a manutenção das lentes encarece; Iridel Luz-Partida teme pelos sinais que outras cidades usam.  
  *Agentes:* `npc.iridel-luz-partida`, `npc.aurea-faceta-longa`, `npc.tuck-lente-torta`, `faction.coro-das-facetas`. *Abordagens possíveis:* avaliar a faceta rachada; levantar o que os sinais atendem; ouvir os lapidadores sobre a cota; propor manutenção em etapas. *Se ninguém age:* só novo tremor observado piora a faceta; sem isso, o custo segue alto e os sinais, em risco. *Liga:* event.faceta-rachada. *Fonte:* doc 01 §2.9 (problemas); doc 05 §2.2 (DR-03); doc 06 C-09.

### `settlement.poco-alto` — Poço-Alto — O Distrito do Poço de Luz (de `city.geodalia`)

- **Pequena — A lente fora de alinho** (`seed.poco-alto.p`, ameaça 1): Uma lente do poço saiu de alinho e Iridel Luz-Partida pede quem ajude a realinhá-la.  
  *Agentes:* `npc.iridel-luz-partida`. *Abordagens possíveis:* realinhar a lente; ouvir os guardiões; registrar o que mudou. *Se ninguém age:* a lente segue fora de alinho; a luz do poço diminui. *Fonte:* doc 03 S-17 (obrigação).
- **Média — Obra sobre o poço** (`seed.poco-alto.m`, ameaça 2): A cidade planeja uma obra que pode sombrear o poço; Iridel pede quem estude a sombra.  
  *Agentes:* `npc.iridel-luz-partida`, `npc.aurea-faceta-longa`, `faction.coro-das-facetas`. *Abordagens possíveis:* estudar a sombra em diferentes horas; ouvir os guardiões; propor outro lugar para a obra. *Se ninguém age:* a obra segue em estudo; a sombra só aparece se a obra começar. *Fonte:* doc 03 S-17 (tensão e desenvolvimentos).
- **Grande — A lente rachada e a disputa** (`seed.poco-alto.g`, ameaça 3): Uma lente rachou e a disputa sobre quem paga a manutenção ameaça o poço de luz.  
  *Agentes:* `npc.iridel-luz-partida`, `npc.aurea-faceta-longa`, `npc.tuck-lente-torta`, `faction.coro-das-facetas`. *Abordagens possíveis:* avaliar a lente; levantar quem usa os sinais; propor manutenção compartilhada; ouvir os lapidadores. *Se ninguém age:* a lente só piora com novo tremor observado; sem isso, o custo segue sem acordo. *Liga:* event.faceta-rachada (ligação não provada). *Fonte:* doc 03 S-17 (desenvolvimentos); doc 05 §2.2 (DR-03).

### `settlement.corte-fino` — Corte-Fino — A Comunidade dos Lapidadores (de `city.geodalia`)

- **Pequena — O tremor na bancada** (`seed.corte-fino.p`, ameaça 1): Um tremor menor sacudiu as bancadas de lapidação e Tuck Lente-Torta pede quem confira se alguma peça rachou.  
  *Agentes:* `npc.tuck-lente-torta`. *Abordagens possíveis:* examinar as bancadas; ouvir os lapidadores; registrar o dano. *Se ninguém age:* o dano segue sem registro; pode não haver dano. *Fonte:* doc 03 S-18 (desenvolvimentos).
- **Média — Encomenda recusada** (`seed.corte-fino.m`, ameaça 2): As cotas apertaram e os lapidadores perdem encomendas; Tuck pede quem leve o pedido à cidade.  
  *Agentes:* `npc.tuck-lente-torta`, `npc.aurea-faceta-longa`, `faction.coro-das-facetas`. *Abordagens possíveis:* levantar encomendas perdidas; ouvir a Câmara das Facetas; propor cota por faceta. *Se ninguém age:* as encomendas seguem perdidas; a cota fica como está. *Liga:* event.faceta-rachada (ligação não provada). *Fonte:* doc 03 S-18 (tensão e desenvolvimentos); doc 05 §1 (L-06).
- **Grande — A extração clandestina** (`seed.corte-fino.g`, ameaça 3): Alguns lapidadores extraem mais cristal bruto do que a cidade permite; é preciso decidir entre punir, negociar a cota ou ignorar.  
  *Agentes:* `npc.tuck-lente-torta`, `npc.iridel-luz-partida`, `npc.aurea-faceta-longa`, `faction.coro-das-facetas`. *Abordagens possíveis:* ouvir os lapidadores; medir o dano à caverna; propor cota nova; levar o caso à Câmara das Facetas. *Se ninguém age:* a extração segue sem resolução; a caverna pode ou não reagir. *Fonte:* doc 03 S-18 (interesse e desenvolvimentos); doc 05 §1 (L-06).

### `city.pedra-ata` — Pedra-Ata — A Cidade do Concílio

- **Pequena — O pacto que ninguém assinou** (`seed.pedra-ata.p`, ameaça 1): Corre o rumor de que um escriba copiou um pacto que as cidades juram nunca ter assinado; Maro Tinta-Velha quer quem verifique selo e tinta, sem acusar ninguém.  
  *Agentes:* `npc.maro-tinta-velha`, `npc.haldra-tinta-funda`. *Abordagens possíveis:* examinar selo e tinta; ouvir os escribas; comparar com as cópias já registradas. *Se ninguém age:* o rumor segue; o pacto pode ser só uma cópia sem selo. *Fonte:* doc 01 §2.10 (rumores), MP-04.
- **Média — A cópia mais antiga da ata** (`seed.pedra-ata.m`, ameaça 3): Maro pede quem encontre a cópia mais antiga de uma ata contestada; cada cidade guarda a cópia que lhe convém e a disputa chega ao Concílio.  
  *Agentes:* `npc.maro-tinta-velha`, `npc.haldra-tinta-funda`, `npc.velka-selo-firme`, `faction.arquivistas-das-coroas`. *Abordagens possíveis:* seguir a cadeia de custódia; comparar tinta e selo; ouvir os arquivistas; pedir cópias às cidades; não confirmar uma só cópia. *Se ninguém age:* a ata segue contestada; cada cidade mantém sua versão. *Liga:* clock.assento. *Fonte:* doc 01 §2.10 (problemas), MP-04; doc 05 §1 (L-08); doc 06 O-10.
- **Grande — O fim do ciclo do assento** (`seed.pedra-ata.g`, ameaça 4): O ciclo de Velka Selo-Firme termina sem sucessor combinado e cada cidade quer o assento por primeiro; as cópias da ata seguem em disputa e o Concílio pode ficar sem presidente de guarda.  
  *Agentes:* `npc.velka-selo-firme`, `npc.maro-tinta-velha`, `npc.sharra-selo-de-voto`, `npc.haldra-tinta-funda`, `faction.arquivistas-das-coroas`. *Abordagens possíveis:* negociar a ordem do assento com várias cidades; arbitrar as cópias; ouvir delegações no Pátio; propor sucessão aceita por maioria do Concílio. *Se ninguém age:* só o fim do ciclo cumprido sem acordo anunciado deixa o Concílio sem presidente de guarda; antes disso, a corrida segue. *Liga:* clock.assento; event.assento-vazio. *Fonte:* doc 01 §2.10 (problemas); doc 04 §2; doc 06 CL-05, C-10.

### `settlement.patio-das-delegacoes` — Pátio-das-Delegações — O Bairro da Hospedagem (de `city.pedra-ata`)

- **Pequena — A delegação sem aviso** (`seed.patio-das-delegacoes.p`, ameaça 1): Uma delegação chega sem aviso e Sharra Selo-de-Voto pede quem ajude a acomodá-la sem favorecer ninguém.  
  *Agentes:* `npc.sharra-selo-de-voto`, `npc.velka-selo-firme`. *Abordagens possíveis:* acomodar a delegação; ouvir o delegado; registrar o que foi dado. *Se ninguém age:* a delegação é acomodada ou parte descontente; o bairro absorve o custo. *Fonte:* doc 03 S-19 (desenvolvimentos).
- **Média — A conta não paga** (`seed.patio-das-delegacoes.m`, ameaça 2): O Concílio paga as despesas das delegações atrasado; Sharra pede quem leve a conta a Pedra-Ata.  
  *Agentes:* `npc.sharra-selo-de-voto`, `npc.velka-selo-firme`, `npc.maro-tinta-velha`. *Abordagens possíveis:* levantar as contas; levar o pedido a Velka; ouvir os hospedeiros; propor pagamento por etapas. *Se ninguém age:* a conta segue em aberto; o bairro segue sem reserva. *Fonte:* doc 03 S-19 (interesse e desenvolvimentos).
- **Grande — A ofensa de hospitalidade** (`seed.patio-das-delegacoes.g`, ameaça 3): Um delegado se sente ofendido pela hospedagem e o assunto pode contaminar a disputa pelo assento.  
  *Agentes:* `npc.sharra-selo-de-voto`, `npc.velka-selo-firme`, `npc.maro-tinta-velha`, `faction.arquivistas-das-coroas`. *Abordagens possíveis:* reparar a ofensa com gesto proporcional; ouvir o delegado e os hospedeiros; levar o caso a Velka; evitar favorecer. *Se ninguém age:* a ofensa pode esfriar ou crescer; nada muda sem queixa formal. *Liga:* clock.assento (ligação não provada). *Fonte:* doc 03 S-19 (desenvolvimentos); doc 05 §1 (L-10).

### `settlement.arquivo-velho` — Arquivo-Velho — O Núcleo dos Escribas (de `city.pedra-ata`)

- **Pequena — A ata fechada** (`seed.arquivo-velho.p`, ameaça 1): Haldra Tinta-Funda quer quem apresente um pedido formal de acesso a uma ata guardada fechada, sem prometer abri-la.  
  *Agentes:* `npc.haldra-tinta-funda`, `npc.maro-tinta-velha`. *Abordagens possíveis:* redigir o pedido; ouvir o Arquivo das Coroas; esperar a resposta do Concílio. *Se ninguém age:* o pedido fica sem resposta; a ata segue fechada. *Fonte:* doc 03 S-20 (interesse local).
- **Média — Uma cadeia de custódia** (`seed.arquivo-velho.m`, ameaça 2): As cópias divergentes da ata do assento têm cadeias diferentes; Haldra pede quem trace a custódia de uma delas.  
  *Agentes:* `npc.haldra-tinta-funda`, `npc.maro-tinta-velha`, `faction.arquivistas-das-coroas`. *Abordagens possíveis:* traçar a cadeia; comparar selo e tinta; registrar a divergência sem escolher cópia. *Se ninguém age:* a cadeia segue sem rastreio; as cópias seguem divergentes. *Liga:* clock.assento (ligação não provada). *Fonte:* doc 03 S-20; doc 01 MP-04; doc 05 §1 (L-08).
- **Grande — A ata que não foi lida em voz alta** (`seed.arquivo-velho.g`, ameaça 3): Corre o rumor de atas que o Concílio não leu em voz alta; Haldra quer saber se existem sem presumir supressão.  
  *Agentes:* `npc.haldra-tinta-funda`, `npc.maro-tinta-velha`, `npc.velka-selo-firme`, `faction.arquivistas-das-coroas`. *Abordagens possíveis:* comparar atas lidas e guardadas; pedir acesso formal; consultar a cadeia de custódia; não presumir supressão (hipóteses de MP-04). *Se ninguém age:* as atas seguem fechadas; o rumor cresce ou esfria sem prova. *Fonte:* doc 01 §2.10 (rumores), MP-04; doc 03 S-20.

## Conferências

- 90 sementes, 30 entidades, 3 por entidade (P, M, G); 0 ID de semente duplicado; 0 campo vazio exceto `relogio_evento` (vazio = sem ligação escrita).
- Todo agente, relógio e evento existe nos registros do reino 2; 0 NPC com `vivo = não`.
- 0 solução predefinida; 0 hipótese de mistério escolhida; 0 número, tempo ou rota novo; 0 termo de fronteira usado como fato.

## Decisão pendente

**D-51** (ver `plano/DECISOES.md`): aprovar as 90 sementes e a escala de ameaça. Opções: **A.** aprovar tudo; **B.** trocar semente, ameaça ou ligação. **Recomendação: A.**
