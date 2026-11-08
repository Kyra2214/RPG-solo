# 14 — Resumos de contexto (M5.1)

**Fonte:** doc 01 §5, §7, §26; doc 03; doc 07; `12-registro-mestre`, `12-assentamentos.csv`, `12-facoes.csv`, `12-relogios.csv`, `12-competencias.csv`, `12-voz.csv`, `12-relacoes.csv`, `12-demografia.csv`. Dados em `14-resumos-de-contexto.csv`.

## Regras

- **142 entidades, 1 resumo cada, com ID:** 10 cidades, 20 assentamentos, 10 facções, 7 relógios e 95 pessoas. Cada resumo tem **no máximo 500 caracteres** e uma linha de **vizinhança** (no máximo 300).
- **Vizinhança:** cidade → rotas com duração só onde o doc 07 a escreve (a rota Orvass–Brumafunda segue desconhecida); assentamento → cidade-mãe em uma linha; facção → onde tem presença; relógio → localidades; pessoa → o que lidera, cidade e até 3 relações escritas.
- **Só o que o texto escreve:** nenhum campo foi completado por dedução. Onde o cânone não diz cargo, povo ou idade, o resumo não diz. Classe e profissão propostas (M3.2) **não** entram.
- **Sem segredos nem conhecimento restrito:** o resumo não usa `12-conhecimento.csv` nem a coluna *nunca diz*; uma relação marcada como segredo foi excluída. A separação formal por camada é o M5.2: a coluna `camada_max` do CSV marca `local` (todos, exceto relógios) e `secreto` (7 relógios, uso de Mestre/motor); ver `14-camadas-de-informacao.md`.
- **População** só em faixa de uso (M2.2); nenhum total somado.
- Capacidade e limite das 51 figuras do catálogo vêm do `.kt` (não canônico, ver `12-competencias.csv`). Divergência de povo de Yssara e Hadrik: D-20.
- Resumo é material de consulta para a IA, não texto de lore: não altera fato nem estado.

## Cidades

- **`city.elarion` — Elarion — Cidade das Sete Copas.** Capital política, arquivo e centro cerimonial, em sete árvores colossais junto ao Rio Lúmen, ligadas por pontes vivas. Governo: Intendente nomeado pelo rei e fiscalizado pelo conselho urbano. Vive de administração, ensino, artes, papel de casca e comércio de livros. Não é inexpugnável: fogo, sabotagem e bloqueio fluvial são riscos reais. Tensões: documentos da Guerra das Cinzas Verdes sumiram; moradia nas copas sobe; pontes antigas com rachaduras.  
  *Vizinhança:* Rotas: Nymvar (2 d estrada, 1–2 d rio); Vael-Tir (3 d, mais com quarentena); Caladwen (2 d, 1,5 d por trilhas rápidas); Arven (1,5 d); Thalanor (3 d barco, 5 d estrada); Torvelume (4 d).
- **`city.nymvar` — Nymvar — Cidade das Três Vozes.** Cidade ribeirinha, celeiro e centro de cura, na confluência de três braços do Rio das Três Vozes. Governo: Matriarca eleita dos canais, com mandato renovável após prestação de contas. Vive de grãos, frutas, ervas, peixes e transporte fluvial. Vulnerável a enchentes e à contaminação da água. Tensões: diques precisam de reparo; um fungo atingiu parte das plantações; comerciantes contestam o tributo em ano de colheita ruim. População: 30.000–49.999 habitantes (faixa de uso).  
  *Vizinhança:* Rotas: Elarion (2 d estrada, 1–2 d rio); Orvass (3 d, faixa 2–6); Arven (2–4 d caravana, 1 d viajante leve); Thalanor (3–4 d barco de carga).
- **`city.thalanor` — Thalanor — Porto da Folha Azul.** Porto marítimo e alfândega numa enseada abrigada da Costa de Âmbar, perto da foz do Lúmen. Governo: Almirante-civil escolhida por conselho de capitães e confirmada pela Coroa. Vive de pesca, navegação, sal, âmbar, cordame e reparo de barcos. Defende-se com faróis, correntes portuárias e armazéns de quarentena. Tensões: contrabando de resina, tripulações desaparecidas e atrito entre capitães locais e inspetores reais. População: 30.000–49.999 habitantes (faixa de uso).  
  *Vizinhança:* Rotas: Elarion (3 d barco, 5 d estrada); Nymvar (3–4 d barco de carga); Silvaran (4 d).
- **`city.vael-tir` — Vael-Tir — A Cidade sob as Copas.** Assentamento arbóreo de patrulha e manejo florestal, em plataformas entre árvores gigantes na borda da Mata Primordial. Governo: Guardiã de Trilhas escolhida por patrulheiros e comunidades. Vive de frutos raros, fibras, resinas autorizadas, guias e resgate. Tensões: caçadores ilegais, sinais de corte noturno e um posto da Coroa planejado sem consultar os centauros. População: 15.000–29.999 habitantes (faixa de uso).  
  *Vizinhança:* Rotas: Elarion (3 d, mais com quarentena); Brumafunda (2–4 d); Torvelume (cerca de 1–2 d).
- **`city.orvass` — Orvass — Cidade das Águas Escuras.** Cidade de pântano, pesca, ervas e travessias, em ilhas de terra firme e passarelas. Governo: Conselho das Estacas, com representantes de bairros e comunidades de água. Vive de ervas medicinais, tinturas, pesca, fungos e guias de travessia. Difícil de cercar, mas sofre com doenças e incêndios na seca. Tensões: remédios escassos, licenças caras da capital e desaparecimentos atribuídos a monstros sem investigação. População: 15.000–29.999 habitantes (faixa de uso).  
  *Vizinhança:* Rotas: Nymvar (3 d, faixa 2–6); Brumafunda (duração desconhecida).
- **`city.caladwen` — Caladwen — Cidade dos Arquivos Celestes.** Centro acadêmico e observatório numa colina ao lado do Lago das Lanternas. Governo: Reitora dos Ciclos, escolhida por três escolas e confirmada pelo conselho local. Vive de ensino, cartas celestes, instrumentos, mapas e cópia de manuscritos. Fraca em guerra aberta. Tensões: uma ala do arquivo interditada, professores divididos sobre as árvores-coração, cópias incompletas de textos proibidos à venda. População: 15.000–29.999 habitantes (faixa de uso).  
  *Vizinhança:* Rotas: Elarion (2 d, 1,5 d por trilhas rápidas); Brumafunda (2 d com guia); Torvelume (cerca de 3 d).
- **`city.brumafunda` — Brumafunda — A Cidade do Bosque Velado.** Cidade de fronteira ecológica, guias e coleta, no limite do Bosque dos Sussurros, em neblina persistente. Governo: Vigia-Mor, responsável por rotas e quarentenas. Vive de fungos, plantas de tintura, mapas, guias e instrumentos acústicos. Defende-se com marcos sonoros, trilhas marcadas e pequenas patrulhas. Tensões: viajantes somem, guias monopolizam informação, moradores acusam estudiosos de provocar fenômenos. População: 10.000–14.999 habitantes (faixa de uso).  
  *Vizinhança:* Rotas: Caladwen (2 d com guia); Vael-Tir (2–4 d); Orvass (duração desconhecida).
- **`city.arven` — Arven — As Clareiras Douradas.** Centro agrícola e de criação sustentável em clareiras férteis ao sul de Elarion. Governo: Prefeita dos Celeiros, escolhida por associações de produtores. Vive de grãos, frutas, mel, lã, sementes e criação de pasto. Defende-se com celeiros distribuídos, milícia comunitária e rotas de evacuação. Tensões: disputa de terras entre novos produtores e famílias antigas, queda na polinização e tributo calculado sobre uma colheita que não houve. População: 15.000–29.999 habitantes (faixa de uso).  
  *Vizinhança:* Rotas: Elarion (1,5 d); Nymvar (2–4 d caravana, 1 d viajante leve); Silvaran (2–4 d caravana).
- **`city.silvaran` — Silvaran — A Cidade dos Festivais.** Centro cultural, de santuários sazonais e hospitalidade, num vale onde quatro trilhas antigas se encontram. Governo: Anfitriã das Estações, cargo ritual que depende de consenso entre bairros. Vive de música, teatro, tecelagem, hospedarias e mediação de pactos. Baixa capacidade para cercos longos. Tensões: festivais atraem batedores e ladrões, uma canção antiga divide duas famílias, parte da liderança quer restringir festas. População: 15.000–29.999 habitantes (faixa de uso).  
  *Vizinhança:* Rotas: Thalanor (4 d); Arven (2–4 d caravana).
- **`city.torvelume` — Torvelume — A Fortaleza da Espinha de Vidro.** Fortaleza florestal, posto de fronteira e cidade de ofícios, na passagem entre a floresta e a Espinha de Vidro, perto de antigas galerias. Governo: Comandante-Civil escolhido pela guarnição e ratificado pelos habitantes. Vive de ferramentas, estradas, pedra, vidro natural e abastecimento de patrulhas. Tensões: a mina selada emite vibrações, há salários atrasados e a corte quer ampliar a guarnição. População: 15.000–29.999 habitantes (faixa de uso).  
  *Vizinhança:* Rotas: Elarion (4 d); Vael-Tir (cerca de 1–2 d); Caladwen (cerca de 3 d).

## Assentamentos

- **`settlement.raiz-de-prata` — Raiz-de-Prata.** Assentamento ligado a Elarion; liderança: Nivara Folha-de-Orvalho. Obrigação: manter mudas de espécies comuns e uma reserva emergencial de árvores nativas. Interesse: proteger sementes adaptadas ao solo da vila, inclusive variedades que a corte considera pouco produtivas. Tensão: a Coroa exige mudas de espécies raras para restaurar áreas nobres; Nivara afirma que retirar as sementes locais agora comprometerá a próxima estação.  
  *Vizinhança:* Cidade-mãe: Elarion (capital política, arquivo e centro cerimonial, mais de 50.000 habitantes em faixa de uso).
- **`settlement.sete-degraus` — Sete Degraus.** Assentamento ligado a Elarion; liderança: Othalen Vira-Pedra. Obrigação: manter a ponte transitável e garantir passagem a mensageiros oficiais, equipes de socorro e evacuações. Interesse: receber pagamento pelo desgaste da ponte e impedir que a isenção de nobres recaia sobre os demais viajantes. Tensão: os registros de pedágio não explicam por que três casas nobres atravessam sem pagar. Othalen suspeita de privilégio legal, mas ainda não viu o decreto original.  
  *Vizinhança:* Cidade-mãe: Elarion (capital política, arquivo e centro cerimonial, mais de 50.000 habitantes em faixa de uso).
- **`settlement.junco-alto` — Junco Alto.** Assentamento ligado a Nymvar; liderança: Beren Três-Canais. Obrigação: manter comportas e fornecer equipes durante cheias extraordinárias. Interesse: priorizar reparos preventivos em vez de grandes obras cerimoniais na capital. Tensão: a vila alega que o trecho superior recebe manutenção primeiro, desviando o risco de inundação para bairros abaixo. Beren foi acusado de desviar materiais, mas ainda precisa apresentar todos os registros.  
  *Vizinhança:* Cidade-mãe: Nymvar (cidade ribeirinha, celeiro e centro de cura, 30.000–49.999 habitantes em faixa de uso).
- **`settlement.tres-barcas` — Três Barcas.** Assentamento ligado a Nymvar; liderança: Sava Mão-de-Corrente. Obrigação: manter embarcações de emergência e publicar tarifas de travessia. Interesse: ajustar os preços à corrente, à carga e ao risco sem perder o direito de negociar livremente. Tensão: a cidade exige tarifa máxima durante crises; barqueiros afirmam que a regra os obriga a operar com prejuízo quando a corrente fica perigosa.  
  *Vizinhança:* Cidade-mãe: Nymvar (cidade ribeirinha, celeiro e centro de cura, 30.000–49.999 habitantes em faixa de uso).
- **`settlement.ponta-da-concha` — Ponta da Concha.** Assentamento ligado a Thalanor; liderança: Yara Sal-de-Maré. Obrigação: manter vigias costeiros e participar do resgate de embarcações quando convocada. Interesse: conservar áreas de pesca tradicionais e impedir que licenças favoreçam grandes frotas. Tensão: novas restrições de pesca foram impostas depois de relatos de contaminação, mas nenhuma compensação foi paga às famílias afetadas.  
  *Vizinhança:* Cidade-mãe: Thalanor (porto marítimo e alfândega, 30.000–49.999 habitantes em faixa de uso).
- **`settlement.farol-de-ambar` — Farol de Âmbar.** Assentamento ligado a Thalanor; liderança: Terenna Luz-de-Lastro. Obrigação: manter os sinais marítimos e registrar embarcações desconhecidas. Interesse: preservar a autonomia dos faroleiros para emitir alertas mesmo quando isso prejudica o comércio. Tensão: a guarda portuária quer usar o farol para observação política e censura de sinais; os moradores temem que atrasos de autorização provoquem naufrágios.  
  *Vizinhança:* Cidade-mãe: Thalanor (porto marítimo e alfândega, 30.000–49.999 habitantes em faixa de uso).
- **`settlement.copa-funda` — Copa-Funda.** Assentamento ligado a Vael-Tir; liderança: Arieth Três-Mudas. Obrigação: cumprir cotas de extração e patrulhar os limites ancestrais. Interesse: obter cotas ajustadas à praga que reduziu as árvores aptas ao corte, em vez de compensar a perda com corte em áreas novas. Tensão: a cota real foi calculada com dados anteriores à praga. Se a vila cumprir a ordem, pode perder a capacidade de regeneração; se recusar, arrisca sanções.  
  *Vizinhança:* Cidade-mãe: Vael-Tir (assentamento arbóreo, patrulha e manejo florestal, 15.000–29.999 habitantes em faixa de uso).
- **`settlement.passo-do-veado` — Passo do Veado.** Assentamento ligado a Vael-Tir; liderança: Calar Passo-Curvo. Obrigação: manter a passagem e suspender a caça durante a migração acordada. Interesse: permitir comércio sem transformar o corredor em estrada permanente. Tensão: a Liga das Rotas quer alargar a trilha; comunidades centauras afirmam que o projeto reduzirá a largura segura de passagem.  
  *Vizinhança:* Cidade-mãe: Vael-Tir (assentamento arbóreo, patrulha e manejo florestal, 15.000–29.999 habitantes em faixa de uso).
- **`settlement.lodo-claro` — Lodo-Claro.** Assentamento ligado a Orvass; liderança: Nima Barro-Manso. Obrigação: compartilhar amostras de água e fornecer apoio em surtos, preservando as reservas locais. Interesse: regular a coleta de plantas raras conforme a capacidade de regeneração. Tensão: licenças de Orvass permitem coleta comercial acima do que a aldeia considera sustentável; a cidade acusa a aldeia de esconder recursos públicos.  
  *Vizinhança:* Cidade-mãe: Orvass (cidade de pântano, pesca, ervas e travessias, 15.000–29.999 habitantes em faixa de uso).
- **`settlement.ilhas-junco-negro` — Ilhas do Junco Negro.** Assentamento ligado a Orvass; liderança: Vela Três-Margens. Obrigação: manter passagens aquáticas e abrigos de cheia. Interesse: conservar o direito de usar mapas sazonais e canais que não constam nos mapas oficiais. Tensão: guardas detêm barqueiros por usar passagens fora dos registros da cidade; os moradores alegam que os canais mudam naturalmente e que os mapas oficiais estão desatualizados.  
  *Vizinhança:* Cidade-mãe: Orvass (cidade de pântano, pesca, ervas e travessias, 15.000–29.999 habitantes em faixa de uso).
- **`settlement.vigilia-do-lago` — Vigília do Lago.** Assentamento ligado a Caladwen; liderança: Orelia Céu-de-Cinza. Obrigação: manter registros climáticos e compartilhar alertas de risco com as cidades conectadas. Interesse: publicar dados úteis à população sem entregar observações sensíveis para uso militar exclusivo. Tensão: o Colégio dos Ciclos quer classificar certos padrões como informação estratégica; agricultores afirmam que atrasar alertas custa colheitas e vidas.  
  *Vizinhança:* Cidade-mãe: Caladwen (centro acadêmico e observatório, 15.000–29.999 habitantes em faixa de uso).
- **`settlement.papel-velho` — Papel-Velho.** Assentamento ligado a Caladwen; liderança: Marel Tinta-Firme. Obrigação: conservar cópias públicas de documentos frágeis e informar danos irreversíveis. Interesse: preservar cópias independentes e permitir que fontes divergentes sejam comparadas. Tensão: casas nobres compram cópias privadas e pressionam oficinas a não reproduzir versões que contradizem a história oficial.  
  *Vizinhança:* Cidade-mãe: Caladwen (centro acadêmico e observatório, 15.000–29.999 habitantes em faixa de uso).
- **`settlement.eco-baixo` — Eco Baixo.** Assentamento ligado a Brumafunda; liderança: Mira Ouve-Chuva. Obrigação: atualizar marcos de trilha e participar de buscas por desaparecidos. Interesse: proteger caminhos residenciais e rotas de resgate sem publicá-los todos para comerciantes e caçadores. Tensão: mercadores acusam os guias de esconder rotas para criar monopólio; os moradores temem que divulgar cada passagem leve caçadores a áreas frágeis.  
  *Vizinhança:* Cidade-mãe: Brumafunda (cidade de fronteira ecológica, guias e coleta, 10.000–14.999 habitantes em faixa de uso).
- **`settlement.torre-nevoa-mansa` — Torre da Névoa Mansa.** Assentamento ligado a Brumafunda; liderança: Iven Marca-de-Som. Obrigação: emitir alertas e fechar trilhas quando houver risco verificável. Interesse: manter a autoridade de fechar a passagem com base em sinais locais, sem esperar autorização distante. Tensão: estudiosos de Caladwen querem manter a torre aberta para observar um fenômeno; os vigias dizem que os experimentos aumentam o risco para viajantes.  
  *Vizinhança:* Cidade-mãe: Brumafunda (cidade de fronteira ecológica, guias e coleta, 10.000–14.999 habitantes em faixa de uso).
- **`settlement.mel-do-poente` — Mel do Poente.** Assentamento ligado a Arven; liderança: Sena Pólen-Dourado. Obrigação: conservar corredores de polinização e contribuir com sementes durante emergências. Interesse: proteger flores silvestres e diversidade de abelhas, mesmo quando reduzem a área disponível para produção intensiva. Tensão: grandes produtores querem ampliar os campos e remover faixas floridas; apicultores alertam que a colheita futura cairá se a polinização piorar.  
  *Vizinhança:* Cidade-mãe: Arven (centro agrícola e de criação sustentável, 15.000–29.999 habitantes em faixa de uso).
- **`settlement.campo-tres-familias` — Campo das Três Famílias.** Assentamento ligado a Arven; liderança: Doria Três-Sulcos. Obrigação: entregar a contribuição de grãos pactuada segundo a colheita efetiva. Interesse: rever a base de tributo calculada a partir de uma safra excepcional que não se repetiu. Tensão: a administração regional cobra a quantia antiga, alegando que o pacto não foi formalmente alterado; famílias endividadas ameaçam abandonar terras.  
  *Vizinhança:* Cidade-mãe: Arven (centro agrícola e de criação sustentável, 15.000–29.999 habitantes em faixa de uso).
- **`settlement.mascara-nova` — Máscara Nova.** Assentamento ligado a Silvaran; liderança: Vessa Fio-Dourado. Obrigação: manter uma casa de acolhimento durante os festivais e oferecer apresentações comunitárias em ocasiões pactuadas. Interesse: preservar o direito de artistas recusarem apresentações sem perder acesso a oficinas e moradia. Tensão: Silvaran interpreta a obrigação cultural como serviço gratuito obrigatório; artistas alegam que a exigência se tornou exploração de trabalho.  
  *Vizinhança:* Cidade-mãe: Silvaran (centro cultural, santuários sazonais e hospitalidade, 15.000–29.999 habitantes em faixa de uso).
- **`settlement.fonte-do-regresso` — Fonte do Regresso.** Assentamento ligado a Silvaran; liderança: Dalen Porta-Aberta. Obrigação: oferecer abrigo temporário a viajantes em perigo e refugiados durante emergências reconhecidas. Interesse: manter o acolhimento como dever comunitário sem transferir todo o custo às famílias anfitriãs. Tensão: moradores discordam sobre acolher refugiados de uma disputa política; alguns temem represálias, outros consideram a recusa uma violação do pacto de hospitalidade.  
  *Vizinhança:* Cidade-mãe: Silvaran (centro cultural, santuários sazonais e hospitalidade, 15.000–29.999 habitantes em faixa de uso).
- **`settlement.ferro-folha` — Ferro-Folha.** Assentamento ligado a Torvelume; liderança: Meda Martelo-Calmo. Obrigação: reparar ferramentas e estruturas comuns conforme prioridades pactuadas. Interesse: dar prioridade a pontes, diques e ferramentas civis antes de encomendas militares extraordinárias. Tensão: a guarnição exige armamentos e peças de fortificação enquanto estruturas civis aguardam reparo.  
  *Vizinhança:* Cidade-mãe: Torvelume (fortaleza florestal, posto de fronteira e cidade de ofícios, 15.000–29.999 habitantes em faixa de uso).
- **`settlement.porta-selada` — Porta Selada.** Assentamento ligado a Torvelume; liderança: Rovan Cinza-de-Ferro. Obrigação: vigiar os acessos às galerias, relatar tremores e impedir entradas não autorizadas. Interesse: garantir salários e investigação transparente antes de qualquer reabertura. Tensão: trabalhadores querem retomar a mineração; famílias temem que o selamento esconda negligência, perigo real ou prova de irregularidade antiga.  
  *Vizinhança:* Cidade-mãe: Torvelume (fortaleza florestal, posto de fronteira e cidade de ofícios, 15.000–29.999 habitantes em faixa de uso).

## Facções

- **`faction.vigias-primeiro-verde` — Vigias do Primeiro Verde.** Vigias do Primeiro Verde. Liderança: Saelith Arco-de-Chuva. Objetivo: proteger ecossistemas, patrulhar áreas antigas e impedir extração destrutiva. Métodos: patrulha, apreensão, negociação e, em casos graves, fechamento de trilhas.  
  *Vizinhança:* Presença: Vael-Tir; Brumafunda; Orvass; Thalanor. Assentamentos: copa-funda
- **`faction.liga-das-rotas` — Liga das Rotas.** Liga das Rotas. Liderança: Tavian Lume-Fino. Objetivo: manter estradas, rios e portos abertos e previsíveis. Métodos: contratos, pressão econômica, arbitragem e informação.  
  *Vizinhança:* Presença: Arven; Elarion; Nymvar. Assentamentos: tres-barcas
- **`faction.arquivistas-seiva` — Arquivistas da Seiva.** Arquivistas da Seiva. Liderança: Lethariel Nascente-Clara. Objetivo: preservar fontes históricas, registros de tratados e testemunhos. Métodos: pesquisa, catalogação, autenticação e publicação.  
  *Vizinhança:* Presença: Caladwen; Elarion.
- **`faction.circulo-estacoes` — Círculo das Estações.** Círculo das Estações. Liderança: Lyris Melodia-Clara. Objetivo: manter ritos sazonais, pactos de hospitalidade e transmissão cultural. Métodos: festas, juramentos, mediação e proteção de refugiados.  
  *Vizinhança:* Presença: Silvaran.
- **`faction.irmandade-aguas` — Irmandade das Águas.** Irmandade das Águas. Liderança: Elsin Várzea-Clara. Objetivo: proteger rios, canais, diques e comunidades ribeirinhas. Métodos: manutenção, alertas, negociação e bloqueios temporários em emergências.  
  *Vizinhança:* Presença: Orvass; Nymvar; Thalanor. Assentamentos: junco-alto; lodo-claro
- **`faction.companhia-ambar` — Companhia do Âmbar.** Companhia do Âmbar. Liderança: Maelor Venn. Objetivo: explorar e comercializar âmbar, resina certificada, fibras e produtos de luxo. Métodos: patrocínio, compra de concessões e pressão sobre tarifas.  
  *Vizinhança:* Presença: Thalanor; Torvelume. Assentamentos: raiz-de-prata; ponta-da-concha; copa-funda
- **`faction.circulo-micelial` — Círculo Micelial.** Círculo Micelial. Liderança: Voz-de-Muitas-Raízes. Objetivo: proteger comunidades micélioides e estudar redes de fungos sem destruir seus habitats. Métodos: troca de conhecimento, quarentena e negociação comunitária.  
  *Vizinhança:* Presença: Orvass.
- **`faction.rede-guias` — Rede dos Guias.** Rede dos Guias. Liderança: Neralis Vento-Manso. Objetivo: oferecer travessias seguras em regiões difíceis. Métodos: contratos, treinamento, resgate e cobrança por rotas perigosas.  
  *Vizinhança:* Presença: Brumafunda; Vael-Tir. Assentamentos: eco-baixo
- **`faction.liga-celeiros` — Liga dos Celeiros.** Liga dos Celeiros. Liderança: Tessa Folhinha. Objetivo: proteger estoques, sementes e abastecimento em crises. Métodos: reserva coletiva, empréstimos e distribuição emergencial.  
  *Vizinhança:* Presença: Arven. Assentamentos: raiz-de-prata; mel-do-poente; fonte-do-regresso
- **`faction.guarda-espinha` — Guarda da Espinha.** Guarda da Espinha. Liderança: Dauren Galho-de-Ferro. Objetivo: proteger a passagem noroeste e impedir que ruínas perigosas sejam abertas sem preparação. Métodos: controle de passagem, escolta e interdição.  
  *Vizinhança:* Presença: Torvelume.

## Relógios

- **`clock.heart-trees` — Doença das árvores-coração.** Doença das árvores-coração, progresso inicial 2/6. O que move: semanas sem coleta de amostras; corte próximo a raízes; relatórios ocultos. Ao completar: uma árvore perde uma função ecológica importante; a causa ainda precisa ser descoberta.  
  *Vizinhança:* Localidades: vael-tir, elarion.
- **`clock.dikes` — Dívida dos diques.** Dívida dos diques, progresso inicial 3/6. O que move: cheias, adiamento de reparos e disputa por verbas. Ao completar: um bairro de Nymvar sofre inundação grave e a crise chega ao conselho.  
  *Vizinhança:* Localidades: nymvar, arven, junco-alto, tres-barcas.
- **`clock.resin-smuggling` — Contrabando de resina.** Contrabando de resina, progresso inicial 2/6. O que move: licenças falsas; patrulhas desviadas; demanda elevada. Ao completar: surge uma rede de extração organizada; não prova que toda a Liga das Rotas esteja envolvida.  
  *Vizinhança:* Localidades: copa-funda.
- **`clock.city-distrust` — Desconfiança entre cidades.** Desconfiança entre cidades, progresso inicial 1/6. O que move: tributos desiguais; rumores; socorro distribuído de forma injusta. Ao completar: duas cidades suspendem temporariamente uma obrigação do Pacto das Dez Cidades.  
  *Vizinhança:* Localidades: nymvar, elarion.
- **`clock.ghost-ship` — Navio sem tripulação.** Navio sem tripulação, progresso inicial 1/6. O que move: provas ignoradas; carga movimentada sem registro; testemunhas intimidadas. Ao completar: evidência física é perdida ou ocorre um segundo incidente; não revela automaticamente o culpado.  
  *Vizinhança:* Localidades: thalanor, ponta-da-concha, farol-de-ambar.
- **`clock.sealed-mine` — Mina selada de Torvelume.** Mina selada de Torvelume, progresso inicial 1/6. O que move: escavação clandestina; tremores; sabotagem. Ao completar: um acesso se rompe e cria perigo real; a natureza do que existe abaixo permanece em aberto.  
  *Vizinhança:* Localidades: torvelume, porta-selada, ferro-folha.
- **`clock.centaur-corridors` — Corredores centauros.** Corredores centauros, progresso inicial 2/6. O que move: novas cercas; colheita fora de época; patrulhas sem consulta. Ao completar: uma rota sazonal é fechada e caravanas precisam desviar.  
  *Vizinhança:* Localidades: passo-do-veado, vael-tir.

## Pessoas

- **`npc.saelith-arco-de-chuva` — Saelith Arco-de-Chuva.** Saelith Arco-de-Chuva: guardiã(o) da tríade da cidade; Elfa dos Bosques, cerca de 240 anos. vivo(a); Capacidade: Organizou patrulhas em turnos e impediu que a guarda culpasse famílias inteiras por crimes individuais. Limite: Sua reforma depende de fiscais que ainda resistem a prestar contas. Tom: seca, militar, desconfiada de políticos.  
  *Vizinhança:* lidera vigias-primeiro-verde; afeto com Princesa Maereth Vaelorian; desconfiança com políticos
- **`npc.dara-guarda-da-rota` — Dara Guarda-da-Rota.** Dara Guarda-da-Rota: guardiã(o) da tríade da cidade; povo: satiro; vivo(a); Capacidade: Preserva as rotas de festival e intervém quando rivalidades ameaçam virar violência. Limite: Sua mediação depende de ambas as partes aceitarem regras comuns.  
  *Vizinhança:* cidade: Silvaran
- **`npc.vessa-fio-dourado` — Vessa Fio-Dourado.** Vessa Fio-Dourado: benfeitor(a) da tríade da cidade; liderança de assentamento; vivo(a); Capacidade: Mantém oficinas e refeições para artistas itinerantes durante a estação de chuvas. Limite: Os recursos acabam quando as caravanas ficam retidas por semanas.  
  *Vizinhança:* lidera mascara-nova (cidade-mãe: Silvaran); dependência com Lyris Melodia-Clara; dependência com Arven — As Clareiras Douradas; rivalidade com casa nobre patrocinadora
- **`npc.ithren-quatro-estacoes` — Mestre Ithren das Quatro Estações.** Mestre Ithren das Quatro Estações: herói(na) da tríade da cidade; Elfo das Estrelas, falecido há 260 anos. morto(a); Capacidade: Corrigiu tabelas de plantio ao demonstrar erro acumulado no calendário. Limite: A reforma desagradou sacerdotes e não prevê todas as mudanças climáticas.  
  *Vizinhança:* cidade: Caladwen
- **`npc.avarra-sete-pontes` — Avarra das Sete Pontes.** Avarra das Sete Pontes: herói(na) da tríade da cidade; Elfa dos Bosques, falecida na Era da Coroa de Prata. morto(a); Capacidade: Unificou pontes entre os assentamentos das copas. Limite: Sua obra deslocou famílias; a versão oficial omite parte desse custo.  
  *Vizinhança:* cidade: Elarion; rivalidade com famílias removidas
- **`npc.parel-tres-parafusos` — Parel Três-Parafusos.** Parel Três-Parafusos: benfeitor(a) da tríade da cidade; benfeitor vivo dos bairros suspensos; vivo(a); Capacidade: Ensina moradores a inspecionar cabos e pontes. Limite: A oficina tem recursos para atender apenas parte da cidade. Tom: oficineiro, simples.  
  *Vizinhança:* cidade: Elarion; discordância com Coroa
- **`npc.dena-tres-correntes` — Dena Três-Correntes.** Dena Três-Correntes: herói(na) da tríade da cidade; Pequenina ribeirinha, falecida há cerca de 300 anos. morto(a); Capacidade: Redesenhou comportas e reduziu danos de cheias. Limite: Algumas obras deslocaram famílias e seus desenhos são incompletos.  
  *Vizinhança:* cidade: Nymvar
- **`npc.voz-muitas-raizes` — Voz-de-Muitas-Raízes.** Voz-de-Muitas-Raízes: guardiã(o) da tríade da cidade; Identidade coletiva micélioide. vivo(a); Capacidade: Preserva redes miceliais e comunica sinais de contaminação. Limite: A comunicação é lenta e exige tradução para decisões urgentes. Tom: coletiva, lenta, fala em "nós".  
  *Vizinhança:* lidera circulo-micelial
- **`npc.dara-passo-ritmico` — Dara Passo-Rítmico.** Dara Passo-Rítmico: benfeitor(a) da tríade da cidade; povo: satiro; vivo(a); Capacidade: Organiza apresentações gratuitas em bairros afastados. Limite: A agenda é limitada por recursos e transporte.  
  *Vizinhança:* cidade: Silvaran
- **`npc.iria-dos-ecos` — Iria dos Ecos.** Iria dos Ecos: herói(na) da tríade da cidade; guardiã viva da Torre sem Sino; vivo(a); Capacidade: Conduziu famílias para fora de um círculo de pactos instável e registrou os limites que não deveriam ser cruzados. Limite: Sua memória prova o caminho seguro, não explica a origem do fenômeno.  
  *Vizinhança:* cidade: Silvaran
- **`npc.meda-martelo-calmo` — Meda Martelo-Calmo.** Meda Martelo-Calmo: guardiã(o) da tríade da cidade; liderança de assentamento; vivo(a); Capacidade: Fiscaliza padrões de segurança em oficinas e portões. Limite: Pode interditar uma estrutura, mas não consegue financiá-la.  
  *Vizinhança:* lidera ferro-folha (cidade-mãe: Torvelume); dependência com Dauren Galho-de-Ferro; dependência com Vael-Tir — A Cidade sob as Copas; dependência com Arven — As Clareiras Douradas
- **`npc.hadrik-sete-bigornas` — Hadrik das Sete Bigornas.** Hadrik das Sete Bigornas: herói(na) da tríade da cidade; Anão residente, falecido há 180 anos. morto(a); Capacidade: Padronizou peças de reparo e recusou desmontar uma ponte para bloquear refugiados. Limite: Foi preso por desobediência e sua oficina perdeu contratos.  
  *Vizinhança:* cidade: Torvelume
- **`npc.oranna-passo-largo` — Oranna Passo-Largo.** Oranna Passo-Largo: herói(na) da tríade da cidade; Centaura das Florestas, viva, idade aproximada de 96 anos. vivo(a); Capacidade: Negociou a reabertura de um corredor migratório sem expulsar os pequenos agricultores. Limite: O acordo depende de revisão sazonal e não elimina todas as disputas. Tom: firme, negociadora.  
  *Vizinhança:* cidade: Vael-Tir
- **`npc.oren-galho-quieto` — Oren Galho-Quieto.** Oren Galho-Quieto: herói(na) da tríade da cidade; Elfo dos Bosques, falecido há 200 anos. morto(a); Capacidade: Abriu uma rota de resgate e depois ajudou a fechá-la para proteger a mata. Limite: Seu mapa tem versões distintas e não serve para toda estação.  
  *Vizinhança:* cidade: Vael-Tir
- **`npc.doria-tres-sulcos` — Doria Três-Sulcos.** Doria Três-Sulcos: liderança de assentamento: juíza de terras escolhida por famílias residentes e confirmada em assembleia anual  
  *Vizinhança:* lidera campo-tres-familias (cidade-mãe: Arven); dependência com Arven — As Clareiras Douradas; dependência com Nymvar — Cidade das Três Vozes; dívida com comerciantes de crédito
- **`npc.sena-polen-dourado` — Sena Pólen-Dourado.** Sena Pólen-Dourado: liderança de assentamento: eleita pelo conselho de apicultores e produtores  
  *Vizinhança:* lidera mel-do-poente (cidade-mãe: Arven); negociação com Tessa Folhinha; negociação com Liga dos Celeiros; desconfiança com vendedores de sementes "resistentes"
- **`npc.iven-marca-de-som` — Iven Marca-de-Som.** Iven Marca-de-Som: liderança de assentamento: vigia eleito pelos residentes e confirmado por acordo com os Vigias do Bosque  
  *Vizinhança:* lidera torre-nevoa-mansa (cidade-mãe: Brumafunda); dependência com Brumafunda — A Cidade do Bosque Velado; discordância com Caladwen — Cidade dos Arquivos Celestes
- **`npc.mira-ouve-chuva` — Mira Ouve-Chuva.** Mira Ouve-Chuva: liderança de assentamento: eleita pela assembleia de guias e moradores que mantêm as trilhas  
  *Vizinhança:* lidera eco-baixo (cidade-mãe: Brumafunda); cooperação com Neralis Vento-Manso; cooperação com Rede dos Guias; rivalidade com guias de fora
- **`npc.marel-tinta-firme` — Marel Tinta-Firme.** Marel Tinta-Firme: liderança de assentamento: eleito pelo conselho de oficinas  
  *Vizinhança:* lidera papel-velho (cidade-mãe: Caladwen); cooperação com Lethariel Nascente-Clara; cooperação com Biblioteca de Casca
- **`npc.orelia-ceu-de-cinza` — Orelia Céu-de-Cinza.** Orelia Céu-de-Cinza: liderança de assentamento: curadora do céu escolhida por observadores locais, com prestação de contas pública a cada estação  
  *Vizinhança:* lidera vigilia-do-lago (cidade-mãe: Caladwen); cooperação com Selvaris Noctel
- **`npc.tirven-eixo-claro` — Tirven Eixo-Claro.** Tirven Eixo-Claro: artífice histórico (comportas modulares), lembrado em Caladwen e Arven; artífice histórico, morto há cerca de 210 anos; texto do doc: Gnomo dos Vales, morto há cerca de 210 anos; Capacidade: comportas modulares (artífice histórico).  
  *Vizinhança:* sem vínculo local escrito
- **`npc.maereth-cartografa-das-copas` — Maereth, a Cartógrafa das Copas.** Maereth, a Cartógrafa das Copas: heroína histórica; desenhou rotas de evacuação nos níveis inferiores da capital; heroína histórica, morta há 430 anos; texto do doc: Elfa, morta há 430 anos (pessoa distinta da princesa); Capacidade: desenhou rotas de evacuação nos níveis inferiores. Limite: parte da fama vem de relatos tardios.  
  *Vizinhança:* cidade: Elarion
- **`npc.nivara-folha-de-orvalho` — Nivara Folha-de-Orvalho.** Nivara Folha-de-Orvalho: liderança de assentamento: eleita por famílias viveiristas por mandatos de seis anos; a confirmação distrital é formal, mas a recusa real pode provocar disputa  
  *Vizinhança:* lidera raiz-de-prata (cidade-mãe: Elarion); cooperação com Liga dos Celeiros; cooperação com Lethariel Nascente-Clara; desconfiança com Companhia do Âmbar
- **`npc.othalen-vira-pedra` — Othalen Vira-Pedra.** Othalen Vira-Pedra: liderança de assentamento: magistrado eleito por moradores e comerciantes a cada quatro anos  
  *Vizinhança:* lidera sete-degraus (cidade-mãe: Elarion); cooperação com Tavian Lume-Fino; dependência com Torvelume — A Fortaleza da Espinha de Vidro; tensão com guarda real
- **`npc.beren-tres-canais` — Beren Três-Canais.** Beren Três-Canais: liderança de assentamento: construtor de diques escolhido pelo conselho de famílias e equipes de manutenção; Orcano das Matas, 61 anos. texto do doc: Orcano das Matas, 61 anos; Capacidade: constrói diques; lidera comunidade em Nymvar. Limite: acusado de desviar materiais; ainda não apresentou todos os registros. Tom: de obra, prático.  
  *Vizinhança:* lidera junco-alto (cidade-mãe: Nymvar); cooperação com Elsin Várzea-Clara; cooperação com Irmandade das Águas; rivalidade com proprietários que desviam água
- **`npc.sava-mao-de-corrente` — Sava Mão-de-Corrente.** Sava Mão-de-Corrente: liderança de assentamento: mestra das barcas escolhida pelas guildas e sujeita a revisão pública anual  
  *Vizinhança:* lidera tres-barcas (cidade-mãe: Nymvar); dependência com Nymvar — Cidade das Três Vozes; dependência com Torvelume — A Fortaleza da Espinha de Vidro; tensão com Liga das Rotas
- **`npc.nima-barro-manso` — Nima Barro-Manso.** Nima Barro-Manso: liderança de assentamento: escolhida por curandeiros e famílias por mandato revogável  
  *Vizinhança:* lidera lodo-claro (cidade-mãe: Orvass); cooperação com Voz-de-Muitas-Raízes; cooperação com Irmandade das Águas; desconfiança com compradores de amostras
- **`npc.vela-tres-margens` — Vela Três-Margens.** Vela Três-Margens: liderança de assentamento: porta-voz escolhido pelas comunidades, com mandato revogável em assembleia  
  *Vizinhança:* lidera ilhas-junco-negro (cidade-mãe: Orvass); cooperação com Orvass — Cidade das Águas Escuras; negociação com barqueiros de Nymvar
- **`npc.dalen-porta-aberta` — Dalen Porta-Aberta.** Dalen Porta-Aberta: liderança de assentamento: anfitrião comunal escolhido por bairros e substituível por votação  
  *Vizinhança:* lidera fonte-do-regresso (cidade-mãe: Silvaran); cooperação com Liga dos Celeiros; cooperação com Máscara Nova; tensão com autoridades de Silvaran
- **`npc.terenna-luz-de-lastro` — Terenna Luz-de-Lastro.** Terenna Luz-de-Lastro: liderança de assentamento: guardiã escolhida pelos moradores e confirmada pela autoridade portuária  
  *Vizinhança:* lidera farol-de-ambar (cidade-mãe: Thalanor); dependência com Thalanor — Porto da Folha Azul
- **`npc.yara-sal-de-mare` — Yara Sal-de-Maré.** Yara Sal-de-Maré: liderança de assentamento: porta-voz eleita por famílias de pesca e capitães de barcos pequenos  
  *Vizinhança:* lidera ponta-da-concha (cidade-mãe: Thalanor); negociação com Syranna das Marés; negociação com Companhia do Âmbar; rivalidade com inspetores do porto
- **`npc.rovan-cinza-de-ferro` — Rovan Cinza-de-Ferro.** Rovan Cinza-de-Ferro: liderança de assentamento: administrador civil escolhido pelos moradores, sujeito a confirmação comunitária e inspeção da fortaleza  
  *Vizinhança:* lidera porta-selada (cidade-mãe: Torvelume); dependência com Dauren Galho-de-Ferro; dependência com Ferro-Folha; vínculo com ex-trabalhadores da mina
- **`npc.arieth-tres-mudas` — Arieth Três-Mudas.** Arieth Três-Mudas: liderança de assentamento: escolhida pelo Círculo de Guardiões por voto das famílias residentes e dos trabalhadores de manejo  
  *Vizinhança:* lidera copa-funda (cidade-mãe: Vael-Tir); cooperação com Eryndra Folha-Verde; cooperação com Vigias do Primeiro Verde; ligação comercial com Companhia do Âmbar
- **`npc.calar-passo-curvo` — Calar Passo-Curvo.** Calar Passo-Curvo: liderança de assentamento: guardião rotativo escolhido por comunidades que utilizam a rota  
  *Vizinhança:* lidera passo-do-veado (cidade-mãe: Vael-Tir); respeito com Oranna Passo-Largo; discordância com negociadores da cidade
- **`npc.toma-mel-de-ambar` — Toma Mel-de-Âmbar.** Toma Mel-de-Âmbar: herói(na) da tríade da cidade; Pequenino, falecido há 140 anos. morto(a); Capacidade: Protegeu sementes e mel durante uma praga de flores. Limite: Sua recusa em vender todo o estoque dividiu a cidade.  
  *Vizinhança:* cidade: Arven
- **`npc.avariel-thorne` — Avariel Thorne.** Avariel Thorne: Intendente de Elarion; Elfo, cerca de 176 anos. texto do doc: Elfo, cerca de 176 anos; Capacidade: administra a cidade; tem relatórios sobre pontes. Limite: adiou reformas por disputa orçamentária. Tom: reservado, eficiente, técnico.  
  *Vizinhança:* governa Elarion; vínculo com conselho urbano
- **`npc.sela-dos-retornos` — Sela dos Retornos.** Sela dos Retornos: herói(na) da tríade da cidade; Felídea das Selvas, desaparecida há 70 anos. morto(a); Capacidade: Criou cordões táteis e marcas sonoras para resgate na neblina. Limite: Desapareceu; sua morte não foi comprovada.  
  *Vizinhança:* cidade: Brumafunda
- **`npc.dauren-galho-de-ferro` — Dauren Galho-de-Ferro.** Dauren Galho-de-Ferro: Comandante-civil de Torvelume; Anão residente, 118 anos. texto do doc: Anão residente, 118 anos; Capacidade: comandante-civil; investigação segura na mina selada. Limite: não representa reino anão; cumpre o juramento de defesa mesmo achando a Coroa negligente. Tom: comandante-civil, direto.  
  *Vizinhança:* governa Torvelume; lidera guarda-espinha; desconfiança com Coroa
- **`npc.edris-do-sul` — Edris do Sul.** Edris do Sul: Representante provisório das cidades de várzea; Humano de família antiga de Nymvar, 48 anos. texto do doc: Humano de família antiga de Nymvar, 48 anos; Capacidade: representa cidades de várzea; quer fundos para diques e saúde. Limite: nomeação contestada; não fala por todos os humanos nem por todas as comunidades do sul. Tom: sincero, defende o sul.  
  *Vizinhança:* rivalidade com casas tradicionais
- **`npc.elarwen-segunda` — Elarwen II — a Rainha das Nascentes.** Elarwen II — a Rainha das Nascentes: Negociou o Pacto das Nascentes após a Guerra das Cinzas Verdes; Elfa, falecida há séculos. texto do doc: Elfa, falecida há séculos; Capacidade: negociou o Pacto das Nascentes. Limite: documentos sugerem ameaça de retirar proteção militar.  
  *Vizinhança:* sem vínculo local escrito
- **`npc.elsin-varzea-clara` — Elsin Várzea-Clara.** Elsin Várzea-Clara: Matriarca dos canais de Nymvar; Pequenina ribeirinha, 64 anos. texto do doc: Pequenina ribeirinha, 64 anos; Capacidade: conhece cheias e rotas. Limite: não domina política de corte; não sabe quem autorizou o material inferior. Tom: vem das cheias, pouca política de corte.  
  *Vizinhança:* governa Nymvar; lidera irmandade-aguas; desconfiança com autor da compra de material inferior
- **`npc.eryndra-folha-verde` — Eryndra Folha-Verde.** Eryndra Folha-Verde: Guardiã de Trilhas em Vael-Tir; Elfa dos Bosques, cerca de 132 anos. texto do doc: Elfa dos Bosques, cerca de 132 anos; Capacidade: conhece as marcas de corte na mata; defende corredores centauros. Limite: não é imparcial: sua comunidade também precisa de madeira e alimento. Tom: direta, não imparcial.  
  *Vizinhança:* governa Vael-Tir
- **`npc.fen-orvaluz` — Fen Orvaluz.** Fen Orvaluz: Guia e caçador de resgate que opera entre Vael-Tir e Brumafunda; Felídeo das Selvas, cerca de 46 anos. texto do doc: Felídeo das Selvas, cerca de 46 anos; Capacidade: conhece sinais de animais; rastreia caçadores ilegais. Limite: conhece os limites das próprias habilidades; recusa caçar espécies protegidas. Tom: de campo, reservado.  
  *Vizinhança:* sem vínculo local escrito
- **`npc.ilyra-tres-rios` — Ilyra dos Três Rios — a Curandeira da Vigília.** Ilyra dos Três Rios — a Curandeira da Vigília: Coordenou a quarentena de sementes e distribuiu remédios durante a Vigília Longa; Elfa, falecida há cerca de 160 anos. texto do doc: Elfa, falecida há cerca de 160 anos; Capacidade: coordenou quarentena de sementes e remédios. Limite: isolamento deixou famílias sem mercado por semanas.  
  *Vizinhança:* sem vínculo local escrito
- **`npc.ivara-polen-de-lume` — Ivara Pólen-de-Lume.** Ivara Pólen-de-Lume: Mediadora informal entre Silvaran e comunidades feéricas; Feérica, idade desconhecida. texto do doc: Feérica, idade desconhecida; Capacidade: mediação informal com feéricos; negocia favores. Limite: não é porta-voz de todos os feéricos; termos literais. Tom: literal, cuidadosa com promessas.  
  *Vizinhança:* sem vínculo local escrito
- **`npc.lethariel-nascente-clara` — Lethariel Nascente-Clara.** Lethariel Nascente-Clara: Arquivista-mor; Elfa das Estrelas, cerca de 310 anos. texto do doc: Elfa das Estrelas, cerca de 310 anos; Capacidade: preserva fontes primárias e registros contraditórios. Limite: não divulga documentos privados sem avaliar risco a pessoas vivas. Tom: arquivista exata, cuidadosa com pessoas vivas.  
  *Vizinhança:* lidera arquivistas-seiva
- **`npc.lyris-melodia-clara` — Lyris Melodia-Clara.** Lyris Melodia-Clara: Anfitriã das Estações de Silvaran; Silvano Festivo, idade aproximada de 72 anos. texto do doc: Silvano Festivo, idade aproximada de 72 anos; Capacidade: media disputas com humor. Limite: não tolera pactos de hospitalidade usados para esconder violência. Tom: bem-humorada, mediadora.  
  *Vizinhança:* governa Silvaran; lidera circulo-estacoes; rivalidade com casa nobre
- **`npc.maelor-venn` — Maelor Venn.** Maelor Venn: Comerciante da Companhia do Âmbar; Elfo, cerca de 154 anos. texto do doc: Elfo, cerca de 154 anos; Capacidade: financia projetos úteis; relações com a corte. Limite: riqueza não prova culpa; nega saber da extração ilegal. Tom: rico, cordial, ligado à corte.  
  *Vizinhança:* lidera companhia-ambar; vínculo com agentes contratados
- **`npc.odran-sete-bigornas` — Mestre Odran Sete-Bigornas — o Artífice sem Coroa.** Mestre Odran Sete-Bigornas — o Artífice sem Coroa: Reparou ferramentas, pontes e portões durante uma geração; Anão residente em Torvelume, falecido. texto do doc: Anão residente em Torvelume, falecido; Capacidade: reparou ferramentas, pontes e portões; encaixes reversíveis. Limite: não foi rei, general nem mago.  
  *Vizinhança:* sem vínculo local escrito
- **`npc.mirel-caule-vivo` — Mirel Caule-Vivo.** Mirel Caule-Vivo: Representante de comunidades vegetais; Verdejante, idade incerta; sua comunidade considera a pergunta mal formulada. texto do doc: Verdejante, idade incerta; sua comunidade considera a pergunta mal formulada; Capacidade: representa comunidades vegetais no conselho. Limite: pode ficar semanas sem comparecer; ritmo de comunicação diferente. Tom: lento, em outro ritmo, sem pressa humana.  
  *Vizinhança:* discordância com Coroa
- **`npc.nara-tres-fios` — Nara Três-Fios.** Nara Três-Fios: Mensageira e restauradora de mapas em Elarion; Corvária, cerca de 39 anos. texto do doc: Corvária, cerca de 39 anos; Capacidade: memoriza rotas e sons; restaura mapas. Limite: não conhece segredos só por trabalhar com mensagens. Tom: minuciosa, sensorial (rotas e sons).  
  *Vizinhança:* sem vínculo local escrito
- **`npc.neralis-vento-manso` — Neralis Vento-Manso.** Neralis Vento-Manso: Vigia-Mor de Brumafunda e figura respeitada entre guias; Elfo do Crepúsculo, cerca de 190 anos. texto do doc: Elfo do Crepúsculo, cerca de 190 anos; Capacidade: exige evidência antes de fechar trilhas; respeitado entre guias. Limite: não sabe se o mapa que guarda é confiável. Tom: cético, exige evidência.  
  *Vizinhança:* governa Brumafunda; lidera rede-guias; vínculo com pessoa desaparecida
- **`npc.nima-raiz-de-lodo` — Nima Raiz-de-Lodo.** Nima Raiz-de-Lodo: herói(na) da tríade da cidade; Curandeira orcana das matas, falecida há 90 anos. morto(a); Capacidade: Registrou tratamentos úteis e efeitos adversos perigosos. Limite: Uma mistura antiga ajudou adultos e causou reação grave em crianças.  
  *Vizinhança:* cidade: Orvass
- **`npc.maereth-vaelorian` — Princesa Maereth Vaelorian.** Princesa Maereth Vaelorian: herdeira reconhecida e Guardiã do Pacto das Nascentes; texto do doc: Elfa das Estrelas, 173 anos; Capacidade: influência política como herdeira. Limite: não comanda a guarda inteira; propostas podem ser bloqueadas pelo conselho. Tom: franca, metódica, impaciente com burocracia.  
  *Vizinhança:* discordância com Rei Aerendyl Vaelorian
- **`npc.aerendyl-vaelorian` — Rei Aerendyl Vaelorian.** Rei Aerendyl Vaelorian: Rei de Aeltharyn, guardião formal dos Sete Juramentos; texto do doc: Elfo dos Bosques, 487 anos; Capacidade: autoridade sobre a guarda real, acesso aos arquivos da Coroa, rede de mensageiros. Limite: depende do conselho e dos juramentos locais; não impõe decisões a cidades autônomas sem custo político. Tom: pausado, formal, avesso ao irreversível.  
  *Vizinhança:* afeto com Princesa Maereth Vaelorian; respeito com Saelith Arco-de-Chuva; dívida com Oranna Passo-Largo
- **`npc.selvaris-noctel` — Selvaris Noctel.** Selvaris Noctel: Reitora de Caladwen; Elfo das Estrelas, cerca de 221 anos. texto do doc: Elfo das Estrelas, cerca de 221 anos; Capacidade: valoriza método e revisão por pares. Limite: dividida entre manter a ala interditada e revelar o manuscrito. Tom: acadêmica, dividida.  
  *Vizinhança:* governa Caladwen
- **`npc.miri-corda-de-lua` — Miri Corda-de-Lua.** Miri Corda-de-Lua: herói(na) da tríade da cidade; Silvana Festiva, falecida há 110 anos. morto(a); Capacidade: Criou uma peça pública que permitiu negociação entre casas rivais. Limite: O acordo foi temporário e não resolveu a disputa por terras.  
  *Vizinhança:* cidade: Silvaran
- **`npc.syranna-das-mares` — Syranna das Marés.** Syranna das Marés: Almirante-civil de Thalanor; Elfa costeira, cerca de 205 anos. texto do doc: Elfa costeira, cerca de 205 anos; Capacidade: almirante-civil; quer faróis melhores e alfândega menos corrupta. Limite: não aceita arquivar o desaparecimento do irmão como naufrágio. Tom: direta, portuária, marcada pelo irmão desaparecido.  
  *Vizinhança:* governa Thalanor; afeto com irmão desaparecido
- **`npc.tavian-lume-fino` — Tavian Lume-Fino.** Tavian Lume-Fino: Negociador da Liga das Rotas; Gnomo dos Vales, cerca de 84 anos. texto do doc: Gnomo dos Vales, cerca de 84 anos; Capacidade: negocia estradas e tarifas; compra informação. Limite: sua rede comercial pode ter financiado extração ilegal sem seu conhecimento. Tom: pragmático, negocia tarifas.  
  *Vizinhança:* lidera liga-das-rotas
- **`npc.tessa-folhinha` — Tessa Folhinha.** Tessa Folhinha: Prefeita dos Celeiros de Arven; Pequenina ribeirinha, 57 anos. texto do doc: Pequenina ribeirinha, 57 anos; Capacidade: prioriza abastecimento e contratos transparentes. Limite: família controla um grande armazém (conflito de interesse). Tom: prática, fala de contratos e estoque.  
  *Vizinhança:* governa Arven; lidera liga-celeiros
- **`npc.yssara-folha-de-sal` — Capitã Yssara Folha-de-Sal.** Capitã Yssara Folha-de-Sal: herói(na) da tríade da cidade; Elfa costeira, falecida há 120 anos. morto(a); Capacidade: Padronizou cartas de resgate e protocolos de tempestade. Limite: Autorizou um abandono controverso durante uma quarentena.  
  *Vizinhança:* cidade: Thalanor
- **`npc.tharion-folha-de-ferro` — Tharion Folha-de-Ferro — o Capitão das Rotas Abertas.** Tharion Folha-de-Ferro — o Capitão das Rotas Abertas: Manteve rotas de evacuação durante a Vigília Longa e recusou abandonar aldeias isoladas; Elfo dos Bosques, falecido. texto do doc: Elfo dos Bosques, falecido; Capacidade: manteve rotas de evacuação. Limite: abriu trilha por área sagrada sem autorização.  
  *Vizinhança:* sem vínculo local escrito
- **`npc.anelra-copa-aberta` — Anelra Copa-Aberta.** Anelra Copa-Aberta: herói(na) da tríade da cidade; povo: plantae; morto(a); Capacidade: Recuperou um corredor de sementes após uma queimada e convenceu três aldeias a replantar espécies nativas. Limite: A área continua vulnerável a incêndios provocados por negligência.  
  *Vizinhança:* cidade: Arven
- **`npc.bento-sete-sementes` — Bento Sete-Sementes.** Bento Sete-Sementes: benfeitor(a) da tríade da cidade; povo: pequenino-ribeirinho; vivo(a); Capacidade: Mantém banco de sementes diversificadas e troca variedades testadas. Limite: A reserva é vulnerável a fungos e precisa de financiamento.  
  *Vizinhança:* cidade: Arven
- **`npc.calar-raiz-curva` — Calar Raiz-Curva.** Calar Raiz-Curva: guardiã(o) da tríade da cidade; povo: centauro-das-florestas; vivo(a); Capacidade: Coordena vigias de pastagens e impede que caçadores bloqueiem os corredores de manada. Limite: Não tem autoridade para revogar cercas legalmente registradas.  
  *Vizinhança:* cidade: Arven
- **`npc.ruma-coracao-solo` — Ruma Coração-de-Solo.** Ruma Coração-de-Solo: guardiã(o) da tríade da cidade; povo: plantae; vivo(a); Capacidade: Identifica esgotamento do solo e coordena recuperação de clareiras. Limite: Não consegue reverter uma safra perdida de imediato.  
  *Vizinhança:* cidade: Arven
- **`npc.sava-broto-novo` — Sava Broto-Novo.** Sava Broto-Novo: benfeitor(a) da tríade da cidade; povo: plantae; vivo(a); Capacidade: Distribui mudas locais e mantém viveiros comunitários sem cobrar de famílias deslocadas. Limite: O reflorestamento exige anos e não resolve a perda imediata de colheitas.  
  *Vizinhança:* cidade: Arven
- **`npc.kheva-ouvido-nevoa` — Kheva Ouvido-de-Névoa.** Kheva Ouvido-de-Névoa: herói(na) da tríade da cidade; povo: homem-corvo-das-torres; vivo(a); Capacidade: Restabeleceu a rede de sinais depois que uma tempestade derrubou torres de comunicação. Limite: A reconstrução foi coletiva, apesar de a crônica popular citar apenas seu nome.  
  *Vizinhança:* cidade: Brumafunda
- **`npc.milo-passo-bruma` — Milo Passo-de-Bruma.** Milo Passo-de-Bruma: benfeitor(a) da tríade da cidade; povo: felideo-das-selvas; vivo(a); Capacidade: Mantém abrigo para viajantes que retornam de expedições incompletas. Limite: O abrigo depende de doações e pode fechar durante a neblina extrema.  
  *Vizinhança:* cidade: Brumafunda
- **`npc.nessa-agua-lenta` — Nessa Água-Lenta.** Nessa Água-Lenta: guardiã(o) da tríade da cidade; povo: elfo-do-crepusculo; vivo(a); Capacidade: Mantém trilhas noturnas sinalizadas e registra desaparecimentos sem espalhar rumores. Limite: A neblina pode apagar sinais antes que a equipe chegue.  
  *Vizinhança:* cidade: Brumafunda
- **`npc.nima-rede-micelio` — Nima Rede-de-Micélio.** Nima Rede-de-Micélio: benfeitor(a) da tríade da cidade; povo: fungal; vivo(a); Capacidade: Construiu uma rede de compostagem e filtragem que recuperou hortas alagadas. Limite: O sistema falha quando resíduos tóxicos são despejados deliberadamente.  
  *Vizinhança:* cidade: Brumafunda
- **`npc.selene-mascara-clara` — Selenê Máscara-Clara.** Selenê Máscara-Clara: guardiã(o) da tríade da cidade; povo: feerico; vivo(a); Capacidade: Protege os santuários de hospitalidade e testemunha acordos entre viajantes e anfitriões. Limite: Não pode transformar hospitalidade em imunidade para crimes.  
  *Vizinhança:* cidade: Brumafunda
- **`npc.sivra-pata-musgo` — Sivra Pata-de-Musgo.** Sivra Pata-de-Musgo: guardiã(o) da tríade da cidade; povo: felideo-das-selvas; vivo(a); Capacidade: Protege ninhos e registra caça ilegal. Limite: Recusa revelar localizações de espécies vulneráveis.  
  *Vizinhança:* cidade: Brumafunda
- **`npc.teren-asa-corda` — Teren Asa-de-Corda.** Teren Asa-de-Corda: guardiã(o) da tríade da cidade; povo: homem-corvo-das-torres; vivo(a); Capacidade: Mantém as passagens altas seguras sem permitir que torres de aviso sejam usadas para perseguir dissidentes. Limite: Não controla torres privadas fora de sua jurisdição.  
  *Vizinhança:* cidade: Brumafunda
- **`npc.asha-nove-pergaminhos` — Asha Nove-Pergaminhos.** Asha Nove-Pergaminhos: benfeitor(a) da tríade da cidade; povo: elfo-das-estrelas; vivo(a); Capacidade: Torna pesquisas verificáveis acessíveis sem expor originais frágeis. Limite: Cópias não substituem todas as fontes perdidas.  
  *Vizinhança:* cidade: Caladwen
- **`npc.elyon-prisma-claro` — Elyon Prisma-Claro.** Elyon Prisma-Claro: guardiã(o) da tríade da cidade; povo: elfo-das-estrelas; vivo(a); Capacidade: Protege observações e publica margem de incerteza. Limite: Suas previsões dependem de instrumentos e dados incompletos.  
  *Vizinhança:* cidade: Caladwen
- **`npc.nilo-recado-certo` — Nilo Recado-Certo.** Nilo Recado-Certo: benfeitor(a) da tríade da cidade; povo: homem-corvo-das-torres; vivo(a); Capacidade: Criou uma rede de mensagens com confirmação de entrega para aldeias isoladas. Limite: A rede perde alcance quando tempestades fecham os pontos de pouso.  
  *Vizinhança:* cidade: Elarion
- **`npc.nimeth-vela-chuva` — Nimeth Vela-de-Chuva.** Nimeth Vela-de-Chuva: guardiã(o) da tríade da cidade; povo: elfo-das-estrelas; vivo(a); Capacidade: Preserva e duplica arquivos com cuidado para não expor informantes. Limite: Não consegue salvar e catalogar todos os documentos sob pressão. Tom: guardiã cuidadosa do Arquivo.  
  *Vizinhança:* cidade: Elarion; tensão com críticos
- **`npc.iven-folhas-brancas` — Iven das Folhas Brancas.** Iven das Folhas Brancas: benfeitor(a) da tríade da cidade; povo: elfo-dos-bosques; vivo(a); Capacidade: Coordena transporte de pacientes e registra tratamentos que falharam. Limite: Não dispõe de remédios raros para todos os casos.  
  *Vizinhança:* cidade: Nymvar
- **`npc.mara-barro-claro` — Mara Barro-Claro.** Mara Barro-Claro: guardiã(o) da tríade da cidade; povo: pequenino-ribeirinho; vivo(a); Capacidade: Inspeciona diques e suspende travessias inseguras. Limite: Seu rigor atrasa comércio quando a manutenção é urgente.  
  *Vizinhança:* cidade: Nymvar
- **`npc.korga-ponta-junco` — Korga Ponta-de-Junco.** Korga Ponta-de-Junco: benfeitor(a) da tríade da cidade; povo: orcano-das-matas; vivo(a); Capacidade: Organizou passarelas elevadas e depósitos de remédios. Limite: Madeira tratada e manutenção são escassas.  
  *Vizinhança:* cidade: Orvass
- **`npc.oru-rede-chuva` — Oru Rede-de-Chuva.** Oru Rede-de-Chuva: herói(na) da tríade da cidade; povo: fungal; morto(a); Capacidade: Identificou a contaminação de um lençol superficial antes que a doença se espalhasse pelas várzeas. Limite: A origem da contaminação permanece sem prova conclusiva.  
  *Vizinhança:* cidade: Orvass
- **`npc.syris-canal-velado` — Syris do Canal Velado.** Syris do Canal Velado: herói(na) da tríade da cidade; povo: elfo-do-crepusculo; morto(a); Capacidade: Mapeou canais abandonados e retirou trabalhadores de uma galeria prestes a ruir. Limite: Nunca descobriu quem adulterou os mapas antigos.  
  *Vizinhança:* cidade: Orvass
- **`npc.veyra-pavio-silencioso` — Veyra Pavio-Silencioso.** Veyra Pavio-Silencioso: benfeitor(a) da tríade da cidade; povo: elfo-do-crepusculo; vivo(a); Capacidade: Financia lanternas de segurança e travessias para trabalhadores sem recursos. Limite: O fundo não cobre todas as rotas em noites de cheia.  
  *Vizinhança:* cidade: Orvass
- **`npc.alena-quatro-mascaras` — Alena das Quatro Máscaras.** Alena das Quatro Máscaras: herói(na) da tríade da cidade; povo: silvano-dos-festivais; morto(a); Capacidade: Expôs uma fraude que desviava recursos dos festivais comunitários. Limite: A denúncia destruiu documentos que também poderiam provar outras fraudes.  
  *Vizinhança:* cidade: Silvaran
- **`npc.luma-folha-prata` — Luma Folha-de-Prata.** Luma Folha-de-Prata: benfeitor(a) da tríade da cidade; povo: feerico; vivo(a); Capacidade: Mantém abrigos e jardins de repouso para visitantes que chegam sem patrono. Limite: Em épocas de escassez, precisa limitar o tempo de permanência.  
  *Vizinhança:* cidade: Silvaran
- **`npc.voro-cancao-pedra` — Voro Canção-de-Pedra.** Voro Canção-de-Pedra: guardiã(o) da tríade da cidade; povo: silvano-dos-festivais; vivo(a); Capacidade: Protege espaços de ensaio e artistas contra coerção. Limite: Depende de locais cedidos por instituições com interesses próprios.  
  *Vizinhança:* cidade: Silvaran
- **`npc.mira-sal-prata` — Mira Sal-de-Prata.** Mira Sal-de-Prata: benfeitor(a) da tríade da cidade; povo: abissal-das-enseadas; vivo(a); Capacidade: Mantém um fundo para famílias de marinheiros desaparecidos e financia faróis de emergência. Limite: O fundo não consegue sustentar todas as famílias durante bloqueios longos.  
  *Vizinhança:* cidade: Thalanor
- **`npc.naren-mare-reversa` — Náren da Maré Reversa.** Náren da Maré Reversa: guardiã(o) da tríade da cidade; povo: abissal-das-enseadas; vivo(a); Capacidade: Monitora correntes rasas e coordena alertas de maré. Limite: Recusa resgates com chance mínima de retorno e é contestado por famílias.  
  *Vizinhança:* cidade: Thalanor
- **`npc.talia-cordame-verde` — Tália Cordame-Verde.** Tália Cordame-Verde: benfeitor(a) da tríade da cidade; povo: elfo; vivo(a); Capacidade: Mantém cooperativa de segurança e apoio a trabalhadores portuários. Limite: O fundo não cobre todos os acidentes.  
  *Vizinhança:* cidade: Thalanor
- **`npc.vessa-engrenagem-lua` — Vessa Engrenagem-de-Lua.** Vessa Engrenagem-de-Lua: benfeitor(a) da tríade da cidade; povo: gnomo-dos-vales; vivo(a); Capacidade: Oferece ferramentas e bolsas a aprendizes sem recursos. Limite: As bolsas cobrem apenas uma parte dos candidatos.  
  *Vizinhança:* cidade: Torvelume
- **`npc.eshara-chuva-folha` — Eshara Chuva-na-Folha.** Eshara Chuva-na-Folha: guardiã(o) da tríade da cidade; povo: elfo-dos-bosques; vivo(a); Capacidade: Negocia e protege corredores silvestres. Limite: Não comanda todos os patrulheiros e pode ser desobedecida.  
  *Vizinhança:* cidade: Vael-Tir
- **`npc.mirek-junco-ferro` — Mirek Junco-Ferro.** Mirek Junco-Ferro: guardiã(o) da tríade da cidade; povo: orcano-das-matas; vivo(a); Capacidade: Organiza a defesa de aldeias e prioriza evacuação antes de retaliação. Limite: Algumas comunidades o acusam de negociar demais com a corte élfica.  
  *Vizinhança:* cidade: Vael-Tir
- **`npc.tavro-passo-raiz` — Tavro Passo-de-Raiz.** Tavro Passo-de-Raiz: benfeitor(a) da tríade da cidade; povo: centauro-das-florestas; vivo(a); Capacidade: Mantém abrigos e pontos de água sem exigir juramento político. Limite: A rede depende de contribuições voluntárias.  
  *Vizinhança:* cidade: Vael-Tir
- **`npc.vaunor-eir` — Vaunor Eir.** Vaunor Eir: representante das casas de manejo florestal no Conselho das Sete Copas; quer cotas rígidas de extração; Capacidade: quer cotas rígidas de extração.  
  *Vizinhança:* sem vínculo local escrito
