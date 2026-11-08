# Azeroth — Sementes de aventura (doc 13, R5-5.3)

**Estado:** R5-5.3, D-163 aceita pela continuação · **Data:** 2026-11-07

**Etiqueta:** tudo é **[PROPOSTO]**; semente é situação jogável, não fato canônico. **Fonte:** docs 01 (parte 1), 04, 05 e 06; `12-relogios.csv`, `12-eventos.csv`, `12-registro-mestre.csv`. Dados em `13-sementes-de-aventura.csv` (11 colunas, molde do reino 4).

## Regras

- 10 lugares × 3 sementes = **30**: uma **pequena (P)**, uma **média (M)** e uma **grande (G)**. ID: `seed.<lugar>.<p|m|g>`. As médias são as dez oportunidades OA-01 a OA-10 do doc 06; as pequenas nascem dos rumores do doc 01; as grandes, dos conflitos CFA-01 a CFA-10 (problemas atuais).
- Semente é **situação jogável**, não fato canônico. Não altera estado, relógio, lei, acordo ou relação até ser resolvida por ação, regra ou evento validado.
- **Sem solução predefinida:** *abordagens possíveis* são caminhos, não respostas; nenhuma semente escolhe resposta de mistério (MA-01 a MA-10) nem decide a precedência entre as coroas ou as lacunas L-01 a L-10.
- **Cada semente tem "se ninguém age":** resultado proporcional, sem punição automática; relógio só avança por causa observável (`12-relogios.md` §1).
- **Só entidades existentes:** agentes são pessoas vivas do registro mestre (nenhum falecido, nenhum NPC novo).
- **Ameaça** (1 a 4, ASSUMINDO, mesma escala dos outros reinos): P 1; M 2 a 3; G 3 a 4; nenhuma tem nível 5.
- **Liga** só com relógio ou evento-tipo já escritos; "ligação não provada" nas pequenas, que partem de rumor.
- Nenhum tempo, distância, rota, número ou preço novo. Nada sobre Aeltharyn, o Véu, as Coroas de Pedra ou o Vale Palancar.

## Resumo

| Faixa | Sementes | Ameaça 1 | 2 | 3 | 4 |
|---|---|---|---|---|---|
| Pequena | 10 | 10 | 0 | 0 | 0 |
| Média | 10 | 0 | 8 | 2 | 0 |
| Grande | 10 | 0 | 0 | 9 | 1 |

Total: **30** sementes; 20 com ligação a relógio ou evento (as 10 médias e as 10 grandes); as 10 pequenas, sem ligação escrita.

## Sementes por lugar

### `city.campoalvo`

#### `seed.campoalvo.p` — O celeiro mais fundo do que mostra (P, ameaça 1)
- **Gancho:** Dodda Prato-Cheio ouviu dizer que um celeiro tem mais fundo do que mostra e pede que alguém olhe os Celeiros Comuns, sem afirmar que haja segredo.
- **Agentes:** Dodda Prato-Cheio (`npc.dodda-prato-cheio`)
- **Abordagens possíveis:** olhar os Celeiros Comuns; ouvir o Conselho dos Celeiros; perguntar na Casa das Pesagens
- **Se ninguém age:** pode ser só o desenho do celeiro; nada muda
- **Liga:** ligação não provada
- **Fonte:** doc 01 parte 1 (rumores); MA-01

#### `seed.campoalvo.m` — A medida conferida (M, ameaça 2)
- **Gancho:** Helion Vigília-do-Celeiro pede: conferir a medida de um celeiro na Casa das Pesagens diante de uma testemunha do Conselho.
- **Agentes:** Helion Vigília-do-Celeiro (`npc.helion-vigilia-do-celeiro`)
- **Abordagens possíveis:** fazer o que o gancho pede sem tomar o lado de nenhuma parte; ouvir as duas partes antes; levar o caso ao órgão do lugar
- **Se ninguém age:** em jogo: confiança do Conselho; preço do pão; o relógio só avança com causa observável; sem ela nada muda
- **Liga:** clock.medida-em-ano-ruim;event.medida-dita-em-voz-alta;event.colheita-fraca-na-ceifa
- **Fonte:** doc 06 OA-01 (CA-01, MA-01)

#### `seed.campoalvo.g` — O pão do reino e a precedência (G, ameaça 3)
- **Gancho:** Em dia de pesagem, compradores contestam o preço do pão e a Coroa do Trigal responde que o reino come por causa dela (preço e precedência); Odile Medida-Cheia pede que alguém ajude a achar uma saída sem tomar o lado de nenhuma parte.
- **Agentes:** Odile Medida-Cheia (`npc.odile-medida-cheia`)
- **Abordagens possíveis:** preço fixado em sessão com compradores ouvidos; custo de precedência; ouvir as partes antes de agir
- **Se ninguém age:** perdem: compradores; o Conselho dos Celeiros; o relógio só avança com causa observável; nada se resolve por calendário
- **Liga:** clock.medida-em-ano-ruim;event.medida-dita-em-voz-alta;event.colheita-fraca-na-ceifa
- **Fonte:** doc 06 CFA-01 (CA-01); doc 01 parte 1 (problemas atuais)

### `city.vaudouro`

#### `seed.vaudouro.p` — A comporta que nunca abriu (P, ameaça 1)
- **Gancho:** Larkin Barcaça-Cheia ouviu dizer que uma comporta nunca foi aberta e pede que alguém pergunte aos barqueiros, sem afirmar que esteja emperrada.
- **Agentes:** Larkin Barcaça-Cheia (`npc.larkin-barcaca-cheia`)
- **Abordagens possíveis:** perguntar aos barqueiros; olhar as comportas; ouvir a Casa dos Barqueiros
- **Se ninguém age:** pode ser só costume de uso; nada muda
- **Liga:** ligação não provada
- **Fonte:** doc 01 parte 1 (rumores); MA-02

#### `seed.vaudouro.m` — A barcaça sem dupla taxa (M, ameaça 2)
- **Gancho:** Garvane Remo-Firme pede: escoltar uma barcaça de um rio ao outro sem pagar duas vezes nem fechar a comporta.
- **Agentes:** Garvane Remo-Firme (`npc.garvane-remo-firme`)
- **Abordagens possíveis:** fazer o que o gancho pede sem tomar o lado de nenhuma parte; ouvir as duas partes antes; levar o caso ao órgão do lugar
- **Se ninguém age:** em jogo: a passagem; a Casa dos Barqueiros; o relógio só avança com causa observável; sem ela nada muda
- **Liga:** clock.passagem-dos-rios;event.taxa-unica-da-barcaca
- **Fonte:** doc 06 OA-02 (CA-02, MA-02)

#### `seed.vaudouro.g` — Duas coroas, uma barcaça (G, ameaça 3)
- **Gancho:** A mesma barcaça é cobrada por duas coroas ao passar de um rio ao outro (taxa de passagem); Garvane Remo-Firme pede que alguém ajude a achar uma saída sem tomar o lado de nenhuma parte.
- **Agentes:** Garvane Remo-Firme (`npc.garvane-remo-firme`)
- **Abordagens possíveis:** taxa única acertada na Mesa; custo de renda de uma coroa; ouvir as partes antes de agir
- **Se ninguém age:** perdem: quem passa; os barqueiros; o relógio só avança com causa observável; nada se resolve por calendário
- **Liga:** clock.passagem-dos-rios;event.taxa-unica-da-barcaca
- **Fonte:** doc 06 CFA-02 (CA-02); doc 01 parte 1 (problemas atuais)

### `city.torrefronte`

#### `seed.torrefronte.p` — As páginas que ninguém lembra (P, ameaça 1)
- **Gancho:** Sabeth Pátio-Aberto ouviu dizer que o livro tem páginas que ninguém lembra de ter escrito e pede que alguém as veja com ela, sem tirar conclusão.
- **Agentes:** Sabeth Pátio-Aberto (`npc.sabeth-patio-aberto`)
- **Abordagens possíveis:** ler o Livro dos Chegados diante de uma testemunha; ouvir a guarnição e a vigia
- **Se ninguém age:** pode ser só letra de muitos escrivães; nada muda
- **Liga:** ligação não provada
- **Fonte:** doc 01 parte 1 (rumores); MA-03

#### `seed.torrefronte.m` — O Livro relido (M, ameaça 3)
- **Gancho:** Solenne Vigília-Longa pede: reler com Solenne as páginas do Livro diante de uma testemunha da vigia, sem tomar o lado de nenhuma coroa.
- **Agentes:** Solenne Vigília-Longa (`npc.solenne-vigilia-longa`)
- **Abordagens possíveis:** fazer o que o gancho pede sem tomar o lado de nenhuma parte; ouvir as duas partes antes; levar o caso ao órgão do lugar
- **Se ninguém age:** em jogo: confiança da guarnição; o registro; o relógio só avança com causa observável; sem ela nada muda
- **Liga:** clock.livro-lido-de-dois-modos;event.livro-relido-com-testemunha
- **Fonte:** doc 06 OA-03 (CA-03, MA-03)

#### `seed.torrefronte.g` — O registro e a torre reclamada (G, ameaça 3)
- **Gancho:** Um chegado é registrado e a guarnição e a vigia leem o registro de modos diferentes (responsabilidade de limite); Solenne Vigília-Longa pede que alguém ajude a achar uma saída sem tomar o lado de nenhuma parte.
- **Agentes:** Solenne Vigília-Longa (`npc.solenne-vigilia-longa`)
- **Abordagens possíveis:** leitura combinada diante de testemunhas; custo de autoridade; ouvir as partes antes de agir
- **Se ninguém age:** perdem: os chegados; a guarnição; o relógio só avança com causa observável; nada se resolve por calendário
- **Liga:** clock.livro-lido-de-dois-modos;event.livro-relido-com-testemunha
- **Fonte:** doc 06 CFA-03 (CA-03); doc 01 parte 1 (problemas atuais)

### `city.alvamar`

#### `seed.alvamar.p` — A corrente que falhou (P, ameaça 1)
- **Gancho:** Marnu Espuma-Dada ouviu dizer que a corrente da boca já falhou uma vez e pede que alguém pergunte no molhe, sem acusar ninguém.
- **Agentes:** Marnu Espuma-Dada (`npc.marnu-espuma-dada`)
- **Abordagens possíveis:** perguntar na Guarda da Corrente; olhar o molhe; ouvir a Doca dos Povos do Mar
- **Se ninguém age:** pode ser só conversa de cais; nada muda
- **Liga:** ligação não provada
- **Fonte:** doc 01 parte 1 (rumores); MA-04

#### `seed.alvamar.m` — A ronda no molhe (M, ameaça 2)
- **Gancho:** Dorgan Escudo-Lento pede: acompanhar uma ronda da Guarda da Corrente no molhe e anotar o que entra, sem acusar ninguém.
- **Agentes:** Dorgan Escudo-Lento (`npc.dorgan-escudo-lento`)
- **Abordagens possíveis:** fazer o que o gancho pede sem tomar o lado de nenhuma parte; ouvir as duas partes antes; levar o caso ao órgão do lugar
- **Se ninguém age:** em jogo: a corrente; a Alfândega; o relógio só avança com causa observável; sem ela nada muda
- **Liga:** clock.taxa-do-molhe;event.regra-de-descarga-no-molhe
- **Fonte:** doc 06 OA-04 (CA-04, MA-04)

#### `seed.alvamar.g` — A taxa do porto e a precedência do Cais (G, ameaça 3)
- **Gancho:** A Alfândega contesta se a taxa serve ao porto ou à Coroa quando um navio descarrega fora do molhe (arrecadação); Corlan Cais-Primeiro pede que alguém ajude a achar uma saída sem tomar o lado de nenhuma parte.
- **Agentes:** Corlan Cais-Primeiro (`npc.corlan-cais-primeiro`)
- **Abordagens possíveis:** regra de descarga e de taxa pactuada; custo de renda; ouvir as partes antes de agir
- **Se ninguém age:** perdem: a Alfândega; os barcos pequenos; o relógio só avança com causa observável; nada se resolve por calendário
- **Liga:** clock.taxa-do-molhe;event.regra-de-descarga-no-molhe
- **Fonte:** doc 06 CFA-04 (CA-04); doc 01 parte 1 (problemas atuais)

### `city.enseada-vigia`

#### `seed.enseada-vigia.p` — O farol que acende sozinho (P, ameaça 1)
- **Gancho:** Ressan Concha-Aberta ouviu dizer que um farol acende sozinho em certas noites e pede que alguém o vigie, sem dizer o que o acende.
- **Agentes:** Ressan Concha-Aberta (`npc.ressan-concha-aberta`)
- **Abordagens possíveis:** vigiar o Farol do Sul numa noite; ouvir os faroleiros e os pilotos
- **Se ninguém age:** pode ser só um faroleiro de passagem; nada muda
- **Liga:** ligação não provada
- **Fonte:** doc 01 parte 1 (rumores); MA-05

#### `seed.enseada-vigia.m` — A barra à noite (M, ameaça 2)
- **Gancho:** Nerella Canto-da-Barra pede: guiar um navio pela barra à noite sem entregar a luz a nenhuma coroa.
- **Agentes:** Nerella Canto-da-Barra (`npc.nerella-canto-da-barra`)
- **Abordagens possíveis:** fazer o que o gancho pede sem tomar o lado de nenhuma parte; ouvir as duas partes antes; levar o caso ao órgão do lugar
- **Se ninguém age:** em jogo: a barra; a confiança dos pilotos; o relógio só avança com causa observável; sem ela nada muda
- **Liga:** clock.luz-entre-coroas;event.luz-com-duas-coroas-avisadas
- **Fonte:** doc 06 OA-05 (CA-05, MA-05)

#### `seed.enseada-vigia.g` — A luz entre duas coroas (G, ameaça 3)
- **Gancho:** O Cais ordena aos faroleiros e o Vento pede os sinais da costa, deixando a luz entre duas disputas (comando da luz); Nerella Canto-da-Barra pede que alguém ajude a achar uma saída sem tomar o lado de nenhuma parte.
- **Agentes:** Nerella Canto-da-Barra (`npc.nerella-canto-da-barra`)
- **Abordagens possíveis:** luz mantida pela Irmandade com as duas coroas avisadas; custo de comando; ouvir as partes antes de agir
- **Se ninguém age:** perdem: faroleiros; pilotos; o relógio só avança com causa observável; nada se resolve por calendário
- **Liga:** clock.luz-entre-coroas;event.luz-com-duas-coroas-avisadas
- **Fonte:** doc 06 CFA-05 (CA-05); doc 01 parte 1 (problemas atuais)

### `city.concharia`

#### `seed.concharia.p` — O casco sem encomenda (P, ameaça 1)
- **Gancho:** Corali Fundo-de-Coral ouviu dizer que um casco ficou pronto sem que ninguém o encomendasse e pede que alguém pergunte no estaleiro, sem afirmar de quem seja.
- **Agentes:** Corali Fundo-de-Coral (`npc.corali-fundo-de-coral`)
- **Abordagens possíveis:** perguntar aos Construtores de Concha; olhar o casco; ouvir os Guardas do Santuário
- **Se ninguém age:** pode ser encomenda esquecida; nada muda
- **Liga:** ligação não provada
- **Fonte:** doc 01 parte 1 (rumores); MA-06

#### `seed.concharia.m` — O coral antes da obra (M, ameaça 2)
- **Gancho:** Corali Fundo-de-Coral pede: levar Orrume e Corali ao Canal Estreito para olhar o coral antes de qualquer obra.
- **Agentes:** Corali Fundo-de-Coral (`npc.corali-fundo-de-coral`)
- **Abordagens possíveis:** fazer o que o gancho pede sem tomar o lado de nenhuma parte; ouvir as duas partes antes; levar o caso ao órgão do lugar
- **Se ninguém age:** em jogo: o jardim; o canal; o relógio só avança com causa observável; sem ela nada muda
- **Liga:** clock.canal-e-jardim;event.canal-ou-jardim
- **Fonte:** doc 06 OA-06 (CA-06, MA-06)

#### `seed.concharia.g` — O canal novo e o jardim (G, ameaça 3)
- **Gancho:** O Cais quer abrir mais um canal e os Guardas do Santuário dizem que isso mata o jardim (obra); Corali Fundo-de-Coral pede que alguém ajude a achar uma saída sem tomar o lado de nenhuma parte.
- **Agentes:** Corali Fundo-de-Coral (`npc.corali-fundo-de-coral`)
- **Abordagens possíveis:** obra adiada ou traçado discutido com os dois lados; custo de prazo ou de jardim; ouvir as partes antes de agir
- **Se ninguém age:** perdem: o jardim; os Construtores de Concha; o relógio só avança com causa observável; nada se resolve por calendário
- **Liga:** clock.canal-e-jardim;event.canal-ou-jardim
- **Fonte:** doc 06 CFA-06 (CA-06); doc 01 parte 1 (problemas atuais)

### `city.torreclara`

#### `seed.torreclara.p` — O sinal que ninguém cuida (P, ameaça 1)
- **Gancho:** Clarel Lume-de-Torre ouviu dizer que um sinal antigo ainda é repetido por uma torre que ninguém cuida e pede que alguém a visite, sem afirmar de quem seja.
- **Agentes:** Clarel Lume-de-Torre (`npc.clarel-lume-de-torre`)
- **Abordagens possíveis:** visitar a torre; ouvir a Casa dos Sinais; perguntar na linha de torres
- **Se ninguém age:** pode ser só hábito de vigia; nada muda
- **Liga:** ligação não provada
- **Fonte:** doc 01 parte 1 (rumores); MA-07

#### `seed.torreclara.m` — O aviso com o código limpo (M, ameaça 2)
- **Gancho:** Casimar Código-Limpo pede: entregar um aviso de torre às outras coroas com o código limpo, sem abrir o código-mestre.
- **Agentes:** Casimar Código-Limpo (`npc.casimar-codigo-limpo`)
- **Abordagens possíveis:** fazer o que o gancho pede sem tomar o lado de nenhuma parte; ouvir as duas partes antes; levar o caso ao órgão do lugar
- **Se ninguém age:** em jogo: confiança da Casa dos Sinais; o aviso; o relógio só avança com causa observável; sem ela nada muda
- **Liga:** clock.codigo-sem-aviso;event.aviso-de-codigo-combinado
- **Fonte:** doc 06 OA-07 (CA-07, MA-07)

#### `seed.torreclara.g` — O código mudado sem aviso (G, ameaça 3)
- **Gancho:** Um código muda sem aviso e as outras coroas temem depender de quem pode calar uma torre (aviso); Casimar Código-Limpo pede que alguém ajude a achar uma saída sem tomar o lado de nenhuma parte.
- **Agentes:** Casimar Código-Limpo (`npc.casimar-codigo-limpo`)
- **Abordagens possíveis:** aviso combinado a cada mudança; custo de sigilo; ouvir as partes antes de agir
- **Se ninguém age:** perdem: a Casa dos Sinais; as coroas que dependem do aviso; o relógio só avança com causa observável; nada se resolve por calendário
- **Liga:** clock.codigo-sem-aviso;event.aviso-de-codigo-combinado
- **Fonte:** doc 06 CFA-07 (CA-07); doc 01 parte 1 (problemas atuais)

### `city.estrelado`

#### `seed.estrelado.p` — A estrela sem registro (P, ameaça 1)
- **Gancho:** Ixra Noite-Aberta ouviu dizer que um mapa mostra uma estrela que nenhum registro lista e pede que alguém confira o arquivo, sem afirmar que ela exista.
- **Agentes:** Ixra Noite-Aberta (`npc.ixra-noite-aberta`)
- **Abordagens possíveis:** conferir o arquivo celeste; ouvir os Leitores do Céu e a Vigília da Cúpula
- **Se ninguém age:** pode ser erro de cópia do mapa; nada muda
- **Liga:** ligação não provada
- **Fonte:** doc 01 parte 1 (rumores); MA-08

#### `seed.estrelado.m` — As duas leituras na Mesa (M, ameaça 2)
- **Gancho:** Eloh Cúpula-Acesa pede: ouvir as duas leituras do mesmo céu e levar ambas à Mesa sem escolher uma.
- **Agentes:** Eloh Cúpula-Acesa (`npc.eloh-cupula-acesa`)
- **Abordagens possíveis:** fazer o que o gancho pede sem tomar o lado de nenhuma parte; ouvir as duas partes antes; levar o caso ao órgão do lugar
- **Se ninguém age:** em jogo: a Mesa dos Astros; a previsão; o relógio só avança com causa observável; sem ela nada muda
- **Liga:** clock.leitura-do-ceu;event.duas-leituras-do-ceu-na-mesa
- **Fonte:** doc 06 OA-08 (CA-08, MA-08)

#### `seed.estrelado.g` — Duas leituras do mesmo céu (G, ameaça 3)
- **Gancho:** Estelares e Luminares dão previsões diferentes e as coroas pedem a que lhes serve (previsão); Eloh Cúpula-Acesa pede que alguém ajude a achar uma saída sem tomar o lado de nenhuma parte.
- **Agentes:** Eloh Cúpula-Acesa (`npc.eloh-cupula-acesa`)
- **Abordagens possíveis:** previsões citadas lado a lado; custo de autoridade; ouvir as partes antes de agir
- **Se ninguém age:** perdem: os Leitores do Céu; a Vigília da Cúpula; o relógio só avança com causa observável; nada se resolve por calendário
- **Liga:** clock.leitura-do-ceu;event.duas-leituras-do-ceu-na-mesa
- **Fonte:** doc 06 CFA-08 (CA-08); doc 01 parte 1 (problemas atuais)

### `city.pouso-das-brisas`

#### `seed.pouso-das-brisas.p` — A pipa com mensagem (P, ameaça 1)
- **Gancho:** Poppel Mesa-de-Viajante ouviu dizer que uma pipa apareceu presa na torre com uma mensagem que ninguém enviou e pede que alguém a veja, sem ler mais do que está escrito.
- **Agentes:** Poppel Mesa-de-Viajante (`npc.poppel-mesa-de-viajante`)
- **Abordagens possíveis:** olhar a pipa e a mensagem; ouvir o Posto dos Pipeiros
- **Se ninguém age:** pode ser pipa solta de longe; nada muda
- **Liga:** ligação não provada
- **Fonte:** doc 01 parte 1 (rumores); MA-09

#### `seed.pouso-das-brisas.m` — A pipa de teste (M, ameaça 2)
- **Gancho:** Hollis Torre-do-Meio pede: soltar uma pipa de teste e anotar quem responde na linha, sem dar sinal a coroa alguma.
- **Agentes:** Hollis Torre-do-Meio (`npc.hollis-torre-do-meio`)
- **Abordagens possíveis:** fazer o que o gancho pede sem tomar o lado de nenhuma parte; ouvir as duas partes antes; levar o caso ao órgão do lugar
- **Se ninguém age:** em jogo: a linha de torres; a confiança do Posto; o relógio só avança com causa observável; sem ela nada muda
- **Liga:** clock.sinais-proprios;event.sinal-dividido-na-linha
- **Fonte:** doc 06 OA-09 (CA-09, MA-09)

#### `seed.pouso-das-brisas.g` — O sinal que cada coroa quer (G, ameaça 3)
- **Gancho:** Trigal e Cais pedem sinais próprios e o Posto recusa por serem da linha do Vento (sinal); Hollis Torre-do-Meio pede que alguém ajude a achar uma saída sem tomar o lado de nenhuma parte.
- **Agentes:** Hollis Torre-do-Meio (`npc.hollis-torre-do-meio`)
- **Abordagens possíveis:** horário ou fio dividido por acordo; custo de controle; ouvir as partes antes de agir
- **Se ninguém age:** perdem: o Posto dos Pipeiros; as coroas sem sinal; o relógio só avança com causa observável; nada se resolve por calendário
- **Liga:** clock.sinais-proprios;event.sinal-dividido-na-linha
- **Fonte:** doc 06 CFA-09 (CA-09); doc 01 parte 1 (problemas atuais)

### `city.meiovau`

#### `seed.meiovau.p` — A chave copiada (P, ameaça 1)
- **Gancho:** Auriven Porta-Acesa ouviu dizer que uma das chaves foi copiada e pede que alguém pergunte a Tolla, sem afirmar que haja cópia.
- **Agentes:** Auriven Porta-Acesa (`npc.auriven-porta-acesa`)
- **Abordagens possíveis:** perguntar a Tolla Mesa-Posta; ouvir a Mesa e as coroas; não abrir nada sem as três chaves
- **Se ninguém age:** pode ser só boato de porta; nada muda
- **Liga:** ligação não provada
- **Fonte:** doc 01 parte 1 (rumores); MA-10

#### `seed.meiovau.m` — O chegado entre a Mesa e o Albergue (M, ameaça 3)
- **Gancho:** Tolla Mesa-Posta pede: levar um chegado ao registro e ao Albergue sem tomar o lado da Mesa Estreita nem do Albergue.
- **Agentes:** Tolla Mesa-Posta (`npc.tolla-mesa-posta`)
- **Abordagens possíveis:** fazer o que o gancho pede sem tomar o lado de nenhuma parte; ouvir as duas partes antes; levar o caso ao órgão do lugar
- **Se ninguém age:** em jogo: o Costume da Porta; o chegado; o relógio só avança com causa observável; sem ela nada muda
- **Liga:** clock.acolhida-de-meiovau;event.prazo-e-sustento-do-chegado;event.chegado-sem-registro-na-porta
- **Fonte:** doc 06 OA-10 (CA-10, MA-10)

#### `seed.meiovau.g` — Prazo e sustento do chegado (G, ameaça 4)
- **Gancho:** A Mesa precisa decidir prazo e sustento de quem chega e as três coroas discordam (acolhida); Tolla Mesa-Posta pede que alguém ajude a achar uma saída sem tomar o lado de nenhuma parte.
- **Agentes:** Tolla Mesa-Posta (`npc.tolla-mesa-posta`)
- **Abordagens possíveis:** prazo e sustento por caso; custo de dureza ou de sustento; ouvir as partes antes de agir
- **Se ninguém age:** perdem: os chegados; o Albergue; o relógio só avança com causa observável; nada se resolve por calendário
- **Liga:** clock.acolhida-de-meiovau;event.prazo-e-sustento-do-chegado;event.chegado-sem-registro-na-porta
- **Fonte:** doc 06 CFA-10 (CA-10); doc 01 parte 1 (problemas atuais)

## Conferências

| Verificação | Resultado |
|---|---|
| Sementes no CSV | 30 (10 P, 10 M, 10 G) |
| Lugares com as 3 faixas | 10 de 10 |
| Agente fora do registro mestre, falecido ou NPC novo | 0 |
| Relógio ou evento ligado inexistente | 0 |
| Semente sem "se ninguém age" ou com solução predefinida | 0 |
| Semente que responde mistério ou decide lei, precedência ou acordo | 0 |
| Semente que envolve outro reino | 0 |
| Campo vazio | 0 |

## Decisão pendente

**D-163:** aprovar as 30 sementes. Opções: **A.** aprovar; **B.** trocar semente, agente ou ameaça (diga qual). **Recomendação: A.**
