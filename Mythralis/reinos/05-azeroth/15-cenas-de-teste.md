# 15 — Cenas de teste de Azeroth (R5-6.3)

**Estado:** rascunho de R5-6.3 (D-168 aceita pela continuação) · **Data:** 2026-11-07

**Etiqueta:** tudo é **[PROPOSTO]**; as cenas são testes de contexto, não texto de lore. **Fonte:** `14-resumos-de-contexto` (R5-6.1), `14-camadas-de-informacao` (R5-6.2), `13-sementes-de-aventura`, `12-voz.csv`, doc 07, doc 01 (rumores) e doc 01 parte 5 (calendário). Dados em `15-cenas-de-teste.csv`. Molde: `reinos/04-vale-palancar/15-cenas-de-teste.md`.

## Regras

- Cada cena tem a lista **entra** (texto exato que vai ao contexto da IA, no máximo ~2.400 caracteres) e a lista **não entra** (o que fica fora e quando entraria).
- Só entram fatos de camada **público** ou **local**; fato restrito ou secreto só entra depois de cumprido o caminho de descoberta (R5-6.2). Relógios nunca entram.
- O texto de "entra" é montado dos resumos, fatos, sementes, vozes, rumores e linhas de rota já escritos; nada foi acrescentado. A data é a abertura do jogo: Pousio, Lua 1, dia 1 do ano 457 da Contagem das Chaves (doc 01 parte 5; a época da contagem é `desconhecida`).
- **Escolhas do roadmap (ASSUMINDO):** (a) cidade de coroa: Campoalvo; (b) travessia de rio: Vaudouro; (c) assembleia: Meiovau (a Mesa); (d) disputa: Concharia (canal × jardim); (e) viagem pela linha de torres: Pouso-das-Brisas, sem duração nem distância (D-63, D-153); (f) fronteira, só do lado de cá: Torrefronte; (g) porto e chegada pelas Costas, só do lado de cá: Enseada-Vigia.
- Nenhuma cena traz NPC com `vivo = não`, rota com duração, ou afirmação sobre outro reino. Em (f) e (g) vale o contrato de fronteira: contato permitido sem rota, nada sobre o outro lado. A única menção a outro reino é "quem chega do Véu", no resumo de Meiovau (cena c): é a chegada permitida (As Fugas do Véu), sem origem, rota ou número.
- Em Concharia e Pouso-das-Brisas os NPCs não têm ficha nem voz (só resumo, doc 12-fichas cobre 12 principais); a cena usa só o resumo.
- A contagem de caracteres inclui os rótulos `[id]` e as quebras entre linhas.

## `cena.cidade-campoalvo` — Cidade de coroa: Campoalvo

Chegada ao Salão das Medidas e primeira conversa com a governante do Trigal.

**Entra (1962 caracteres):**

- Ano 457 da Contagem das Chaves, Pousio (inverno), Lua 1, dia 1.
- [city.campoalvo] Campoalvo — A Capital do Trigal. Capital de coroa, cidade de planície (regime fixo). Liderança: Helion Vigília-do-Celeiro. Obrigação: Guardar o excedente, medir o grão e manter o aterro e os canais. Interesse: Manter a precedência do Trigal e o preço do grão. Tensão: O Trigal exige precedência; quem compra o grão questiona o preço. Povos: Humano; Pequenino; Luminar. Porte: grande (relativo, sem número).
- [city.campoalvo] Vizinhança: Ligações (sem duração): Vaudouro (documentada sem duração); Torrefronte (sugerida por dependência); Meiovau (documentada sem duração).
- [npc.odile-medida-cheia] Odile Medida-Cheia, Humano. Governante da Coroa do Trigal; despacha no Salão das Medidas. Limite: Governa o Trigal e fixa as medidas; não manda no Cais, no Vento nem em Meiovau. Pressão: O Cais diz que o excedente apodrece sem o mar; o Vento diz que ela só sabe da colheita depois do aviso; o herdeiro já fala em ceder.
- [voz npc.odile-medida-cheia] tom: firme e orgulhosa; fala em medida, pão e precedência; modelo: "O pão do reino tem preço e a medida é minha."|"O Trigal não manda no Cais nem no Vento; mas também não cala."|"Quem tem fome entra no celeiro; quem quer a precedência fala comigo."
- [fact.odile-governa-a-coroa-do|publico] Governa a Coroa do Trigal e reside em Campoalvo
- [fact.ma-01-o-salao-das-medidas|local] O Salão das Medidas fixa a medida do grão e os Celeiros Comuns guardam o excedente (L-01)
- [seed.campoalvo.p] O celeiro mais fundo do que mostra: Dodda Prato-Cheio ouviu dizer que um celeiro tem mais fundo do que mostra e pede que alguém olhe os Celeiros Comuns, sem afirmar que haja segredo.
- [rumor] dizem que as medidas do salão foram refeitas depois de uma colheita ruim; que um celeiro tem mais fundo do que mostra (rumor, não é fato).
- [regra] o tamanho real dos celeiros e quem tem a precedência não estão decididos (L-01); ficam com o Mestre e com o jogo.

**Não entra:**

- `seed.campoalvo.m` e `.g`: só se a cena os disparar
- `fact.odile-guarda-em-segredo-o-tamanho` (restrito ou secreto) e `clock.medida-em-ano-ruim` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.medida-dita-em-voz-alta`, `event.taxa-unica-da-barcaca`, `event.livro-relido-com-testemunha`, `event.colheita-fraca-na-ceifa`: só se o gatilho aparecer na cena
- `npc.tomlin-horta-larga`, `npc.wendra-aterro-primeiro`, `npc.helion-vigilia-do-celeiro`, `npc.dodda-prato-cheio`, `faction.conselho-dos-celeiros`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MA-01 (`13-misterios-limites.md`): só ao aprofundar
- nenhum dado de outro reino
- as outras nove cidades, as outras 14 facções e os outros 9 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.travessia-vaudouro` — Travessia de rio e comportas: Vaudouro

Chegada à junção dos rios e conversa com o barqueiro-chefe sobre a passagem.

**Entra (2103 caracteres):**

- Ano 457 da Contagem das Chaves, Pousio (inverno), Lua 1, dia 1.
- [city.vaudouro] Vaudouro — O Mercado dos Rios. Cidade fluvial de mercado (regime fixo). Liderança: Ebbo Praia-Rasa. Obrigação: Manter docas, canais e comportas; tributar a passagem para o Trigal. Interesse: Lucrar com a passagem entre os dois rios. Tensão: Trigal e Cais discutem quem taxa o que passa; os barqueiros dizem que a cidade é do rio. Povos: Humano; Pequenino; Nascido da Maré. Porte: média (relativo, sem número).
- [city.vaudouro] Vizinhança: Ligações (sem duração): Campoalvo (documentada sem duração); costa sul (pelo Corre-Largo) (documentada sem duração).
- [npc.garvane-remo-firme] Garvane Remo-Firme, Humano. Barqueiro-chefe das barcaças de Vaudouro; herói de Vaudouro. Limite: Conduz barcaças e fala pelos barqueiros da cheia; não manda nas comportas nem na Casa dos Barqueiros. Pressão: O Trigal e o Cais pedem que ele escolha a taxa a que obedece; a Casa dos Barqueiros o quer como símbolo e ele não quer ser.
- [voz npc.garvane-remo-firme] tom: direto e seco; fala em rio, barcaça e comporta; modelo: "A passagem entre os rios tem de ficar aberta."|"Conduzo barcaça; a Casa dos Barqueiros que fale por si."|"Não me façam símbolo de nada."
- [fact.garvane-salvou-carga-e-barqueiros|publico] Salvou carga e barqueiros na cheia
- [fact.ma-02-a-casa-dos-barqueiros|local] A Casa dos Barqueiros cuida das docas, canais e comportas
- [rota route.vaudouro-costa-sul] Vaudouro ↔ costa sul (pelo Corre-Largo): sem duração; se a Casa dos Barqueiros fecha as comportas, o interior perde o caminho do mar; nenhum lugar de chegada nomeado.
- [seed.vaudouro.p] A comporta que nunca abriu: Larkin Barcaça-Cheia ouviu dizer que uma comporta nunca foi aberta e pede que alguém pergunte aos barqueiros, sem afirmar que esteja emperrada.
- [rumor] contam que o nível do rio sobe na torre antes de subir no rio; que uma comporta nunca foi aberta (rumor, não é fato).
- [regra] as comportas não se fecham sem aviso às duas coroas e quem passa paga o que a medida manda (L-02); a quem pagar quando Trigal e Cais reclamam a mesma passagem não está decidido.

**Não entra:**

- `seed.vaudouro.m` e `.g`: só se a cena os disparar
- `fact.garvane-ja-deixou-passar-uma-barcaca` (restrito ou secreto) e `clock.passagem-dos-rios` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.taxa-unica-da-barcaca`: só se o gatilho aparecer na cena
- `npc.ebbo-praia-rasa`, `npc.larkin-barcaca-cheia`, `faction.casa-dos-barqueiros`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MA-02 (`13-misterios-limites.md`): só ao aprofundar
- nenhum dado de outro reino
- as outras nove cidades, as outras 14 facções e os outros 9 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.mesa-meiovau` — Assembleia da Mesa das Três Chaves: Meiovau

Sessão da Mesa de Meiovau e conversa com a guardiã das chaves sobre o ponto em arbitragem.

**Entra (1951 caracteres):**

- Ano 457 da Contagem das Chaves, Pousio (inverno), Lua 1, dia 1.
- [city.meiovau] Meiovau — A Cidade das Três Chaves. Cidade livre, ponto de encontro das coroas (regime fixo). Liderança: Tolla Mesa-Posta. Obrigação: Guardar as três chaves, arbitrar e acolher quem chega. Interesse: Manter a mesa neutra e útil às três. Tensão: Como acolher quem chega do Véu; precedência entre coroas. Povos: Humano; Pequenino; Luminar; Nascido da Maré; Nascido do Vento. Porte: média (relativo, sem número).
- [city.meiovau] Vizinhança: Ligações (sem duração): Campoalvo (documentada sem duração); Alvamar (documentada sem duração); Torreclara (documentada sem duração); chegados do Véu (documentada sem duração).
- [npc.tolla-mesa-posta] Tolla Mesa-Posta, Pequenino. Guardiã das Três Chaves; preside a Mesa de Meiovau. Limite: Guarda as chaves e preside a Mesa; não abre nada sem as três e não vota pelas coroas. Pressão: As coroas pedem que ela decida o tratamento de quem chega do Véu e quem tem precedência; uma chave pode ter sido copiada.
- [voz npc.tolla-mesa-posta] tom: paciente e exata; fala em chave, mesa e três; modelo: "Nada se abre sem as três chaves."|"A Mesa arbitra o ponto concreto; a precedência não é dela."|"Não voto pelas coroas."
- [fact.tolla-preside-a-mesa-de|publico] Preside a Mesa de Meiovau
- [fact.ma-10-a-mesa-so-abre|publico] A Mesa só abre com as três chaves e nenhuma coroa possui Meiovau (L-09)
- [rota route.meiovau-campoalvo] Meiovau ↔ Campoalvo: ligação política, sem duração; nenhuma coroa possui o caminho.
- [seed.meiovau.p] A chave copiada: Auriven Porta-Acesa ouviu dizer que uma das chaves foi copiada e pede que alguém pergunte a Tolla, sem afirmar que haja cópia.
- [rumor] contam que uma das chaves foi copiada; que a Mesa já decidiu uma vez sem que as três estivessem presentes (rumor, não é fato).
- [regra] a Mesa só abre com as três chaves e arbitra o ponto concreto; a precedência entre as coroas não é dela e não está decidida (L-09).

**Não entra:**

- `seed.meiovau.m` e `.g`: só se a cena os disparar
- `fact.tolla-sabe-qual-chave-pode-ter` (restrito ou secreto) e `clock.acolhida-de-meiovau` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.prazo-e-sustento-do-chegado`, `event.chegado-sem-registro-na-porta`: só se o gatilho aparecer na cena
- `npc.auriven-porta-acesa`, `npc.ostrel-chave-primeira`, `npc.ruvane-chave-contada`, `faction.albergue-dos-chegados`, `faction.mesa-estreita`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MA-10 (`13-misterios-limites.md`): só ao aprofundar
- nenhum dado de outro reino
- as outras nove cidades, as outras 14 facções e os outros 9 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.disputa-concharia` — Disputa do canal e do jardim: Concharia

Chegada ao estaleiro e escuta das duas partes sobre o canal maior.

**Entra (1727 caracteres):**

- Ano 457 da Contagem das Chaves, Pousio (inverno), Lua 1, dia 1.
- [city.concharia] Concharia — O Estaleiro dos Recifes. Estaleiro e santuário costeiro (regime fixo). Liderança: desconhecida. Obrigação: Construir e consertar casco; guardar o santuário e o jardim. Interesse: Manter o estaleiro e o santuário. Tensão: O Cais quer abrir outro canal; o santuário diz que o jardim morre. Povos: Tartarino; Sereiano; Humano; Nascido da Maré. Porte: pequena (relativo, sem número).
- [city.concharia] Vizinhança: Ligações (sem duração): Alvamar (sugerida por dependência).
- [npc.orrume-abrigo-de-casco] Orrume Abrigo-de-Casco, Tartarino. Guardião de Concharia. Líder de Construtores de Concha (objetivo: estaleiro com canal maior para navios maiores). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.
- [npc.corali-fundo-de-coral] Corali Fundo-de-Coral, Sereiano. Benfeitor de Concharia. Líder de Guardas do Santuário (objetivo: jardim e santuário em paz). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.
- [fact.ma-06-o-jardim-e-o|local] O jardim e o santuário ficam em paz e o estaleiro serve a quem paga dentro dessa regra (L-06)
- [seed.concharia.p] O casco sem encomenda: Corali Fundo-de-Coral ouviu dizer que um casco ficou pronto sem que ninguém o encomendasse e pede que alguém pergunte no estaleiro, sem afirmar de quem seja.
- [rumor] contam que um casco ficou pronto sem que ninguém o encomendasse; que o coral cresce para onde o canal ia ser aberto (rumor, não é fato).
- [regra] o jardim e o santuário ficam em paz e o estaleiro serve a quem paga dentro dessa regra (L-06); se um canal maior fere o santuário não está decidido.

**Não entra:**

- `seed.concharia.m` e `.g`: só se a cena os disparar
- `clock.canal-e-jardim` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.canal-ou-jardim`: só se o gatilho aparecer na cena
- `npc.mossa-casco-primeiro`, `faction.construtores-de-concha`, `faction.guardas-do-santuario`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MA-06 (`13-misterios-limites.md`): só ao aprofundar
- nenhum dado de outro reino
- as outras nove cidades, as outras 14 facções e os outros 9 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.viagem-pouso-das-brisas` — Viagem pela linha de torres: Pouso-das-Brisas

Pouso de viajante na linha de torres e conversa com o guardião do posto.

**Entra (1826 caracteres):**

- Ano 457 da Contagem das Chaves, Pousio (inverno), Lua 1, dia 1.
- [city.pouso-das-brisas] Pouso-das-Brisas — A Estação dos Sinais. Estação de torres e pouso de rota (regime fixo). Liderança: Hollis Torre-do-Meio. Obrigação: Manter as torres e os pousos entre o interior e a costa. Interesse: Manter a linha de sinais e o pouso. Tensão: As outras coroas desconfiam de que a linha serve só ao Vento. Povos: Nascido do Vento; Humano; Pequenino (viajante). Porte: pequena (relativo, sem número).
- [city.pouso-das-brisas] Vizinhança: Ligações (sem duração): Torreclara (documentada sem duração); litoral (linha de torres) (documentada sem duração).
- [npc.hollis-torre-do-meio] Hollis Torre-do-Meio, Humano. Guardião de Pouso-das-Brisas. Líder de Posto dos Pipeiros (objetivo: linha de torres e pousos entre o interior e a costa). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.
- [fact.ma-09-o-posto-dos-pipeiros|local] O Posto dos Pipeiros cuida da linha de torres e pousos entre o interior e a costa
- [rota route.torreclara-pouso-das-brisas] Torreclara ↔ Pouso-das-Brisas: sem duração; elo de sinais e de apoio; incêndio ou tempestade derrubam a linha. [rota route.pouso-das-brisas-litoral] Pouso-das-Brisas ↔ litoral (linha de torres): sem duração; nenhum lugar de litoral nomeado.
- [seed.pouso-das-brisas.p] A pipa com mensagem: Poppel Mesa-de-Viajante ouviu dizer que uma pipa apareceu presa na torre com uma mensagem que ninguém enviou e pede que alguém a veja, sem ler mais do que está escrito.
- [rumor] dizem que uma pipa apareceu presa na torre com uma mensagem que ninguém enviou; que o vento daqui muda a pedido (rumor, não é fato).
- [regra] a linha de torres não é uma estrada, sem duração nem distância; nenhum lugar de litoral nomeado e nenhuma rota além das documentadas.

**Não entra:**

- `seed.pouso-das-brisas.m` e `.g`: só se a cena os disparar
- `clock.sinais-proprios` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.aviso-de-codigo-combinado`, `event.sinal-dividido-na-linha`: só se o gatilho aparecer na cena
- `npc.zefir-pipa-caida`, `npc.poppel-mesa-de-viajante`, `faction.posto-dos-pipeiros`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MA-09 (`13-misterios-limites.md`): só ao aprofundar
- nenhum dado de outro reino
- as outras nove cidades, as outras 14 facções e os outros 9 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.fronteira-torrefronte` — Limite do reino, só do lado de cá: Torrefronte

Chegada ao limite e leitura do registro com a comandante da guarnição.

**Entra (2015 caracteres):**

- Ano 457 da Contagem das Chaves, Pousio (inverno), Lua 1, dia 1.
- [city.torrefronte] Torrefronte — A Guarda do Limite. Fortaleza de limite (regime fixo). Liderança: Solenne Vigília-Longa. Obrigação: Vigiar o limite e registrar quem chega e quem parte. Interesse: Manter o limite e o registro sob a própria coroa. Tensão: Guarnição e vigia discordam sobre quem responde por quem chega. Povos: Humano (das fronteiras); Luminar. Porte: pequena (relativo, sem número).
- [city.torrefronte] Vizinhança: Ligações (sem duração): Campoalvo (sugerida por dependência).
- [npc.solenne-vigilia-longa] Solenne Vigília-Longa, Luminar. Comandante da guarnição do Trigal; guardiã de Torrefronte. Líder de Guarnição do Limite (objetivo: limite vigiado e registro lido de um só modo). Limite: Comanda só a guarnição do Trigal; não decide quem passa nem responde pelo registro sem o Vento. Pressão: O Vento reivindica a torre; o registro tem páginas que ninguém explica; o suprimento vem de dentro e atrasa em ano de cheia.
- [voz npc.solenne-vigilia-longa] tom: rigorosa e curta; fala em limite, registro e turno; modelo: "O Livro dos Chegados se lê de um só modo."|"Comando a guarnição do Trigal; quem passa não sou eu quem decide."|"O limite fica vigiado."
- [fact.solenne-comanda-a-guarnicao-de|publico] Comanda a guarnição de Torrefronte
- [fact.ma-03-o-livro-dos-chegados|local] O Livro dos Chegados registra quem chega ou parte do limite (L-03)
- [fronteira] contato permitido, sem rota: a torre fica voltada ao limite do reino; sem passagem, duração ou distância; nada é afirmado sobre o outro lado.
- [seed.torrefronte.p] As páginas que ninguém lembra: Sabeth Pátio-Aberto ouviu dizer que o livro tem páginas que ninguém lembra de ter escrito e pede que alguém as veja com ela, sem tirar conclusão.
- [rumor] dizem que o livro tem páginas que ninguém lembra de ter escrito (rumor, não é fato).
- [regra] quem chega ou parte do limite é registrado no Livro dos Chegados (L-03); quem lê o registro e as páginas sem explicação não estão decididos.

**Não entra:**

- `seed.torrefronte.m` e `.g`: só se a cena os disparar
- `fact.solenne-ja-abriu-o-limite-a` (restrito ou secreto) e `clock.livro-lido-de-dois-modos` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.livro-relido-com-testemunha`, `event.colheita-fraca-na-ceifa`: só se o gatilho aparecer na cena
- `npc.halvard-limite-firme`, `npc.sabeth-patio-aberto`, `faction.guarnicao-do-limite`, `faction.posto-dos-pipeiros`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MA-03 (`13-misterios-limites.md`): só ao aprofundar
- nenhuma afirmação sobre o outro lado ou sobre outro reino
- as outras nove cidades, as outras 14 facções e os outros 9 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## `cena.porto-enseada-vigia` — Porto e chegada pelas Costas, só do lado de cá: Enseada-Vigia

Chegada ao porto da barra e conversa com a piloto sobre quem entra pelo farol.

**Entra (2053 caracteres):**

- Ano 457 da Contagem das Chaves, Pousio (inverno), Lua 1, dia 1.
- [city.enseada-vigia] Enseada-Vigia — O Porto das Costas. Porto de rota marítima (regime fixo). Liderança: Perrin Farol-Aceso. Obrigação: Manter as luzes e receber quem chega pela rota das Costas. Interesse: Manter a barra aberta e a voz dos faroleiros. Tensão: O Cais quer os faroleiros a seu mando e o Vento quer os sinais da costa. Povos: Humano; Sereiano; Tartarino; Abissal Costeiro. Porte: média (relativo, sem número).
- [city.enseada-vigia] Vizinhança: Ligações (sem duração): Alvamar (sugerida por dependência); rota das Costas (documentada sem duração).
- [npc.nerella-canto-da-barra] Nerella Canto-da-Barra, Sereiano. Piloto da Barra; herói de Enseada-Vigia. Líder de Pilotos da Enseada (objetivo: a barra pilotada por ofício, não por coroa). Limite: Guia e ensina a barra; não decide quem recebe o farol e não representa os faroleiros. Pressão: O Cais quer os pilotos a seu mando, o Vento quer os sinais da costa; os recém-chegados pedem a ela o caminho que só os antigos sabem.
- [voz npc.nerella-canto-da-barra] tom: generosa e teimosa; fala em barra, vento e noite; modelo: "A barra não é de coroa nenhuma."|"Ensino quem quer aprender; guio quem pede."|"Pilotar é ofício, não bandeira."
- [fact.nerella-pilota-a-barra-e|publico] Pilota a barra e guiou a frota
- [fact.ma-05-o-farol-se-mantem|publico] O farol se mantém aceso e a barra se avisa a quem chega (L-05)
- [rota route.enseada-vigia-costas] Enseada-Vigia ↔ rota das Costas: só o lado de cá; o que a rota troca, quando começou e quanto dura são desconhecidos.
- [seed.enseada-vigia.p] O farol que acende sozinho: Ressan Concha-Aberta ouviu dizer que um farol acende sozinho em certas noites e pede que alguém o vigie, sem dizer o que o acende.
- [rumor] dizem que um farol acende sozinho em certas noites; que há uma rota de volta que só os pilotos antigos sabem (rumor, não é fato).
- [regra] o farol se mantém aceso e a barra se avisa a quem chega (L-05); de quem são os faróis e os sinais da costa, Cais ou Vento, não está decidido.

**Não entra:**

- `seed.enseada-vigia.m` e `.g`: só se a cena os disparar
- `fact.nerella-sabe-uma-rota-de-volta` (restrito ou secreto) e `clock.luz-entre-coroas` (secreto, uso de Mestre e motor): só depois de cumprido o caminho de descoberta
- `event.luz-com-duas-coroas-avisadas`: só se o gatilho aparecer na cena
- `npc.perrin-farol-aceso`, `npc.ressan-concha-aberta`, `faction.irmandade-dos-faroleiros`, `faction.pilotos-da-enseada`, `faction.casa-dos-sinais`: só se aparecerem
- hipóteses e "a verdade não pode ser" de MA-05 (`13-misterios-limites.md`): só ao aprofundar
- nenhuma afirmação sobre o outro lado ou sobre outro reino
- as outras nove cidades, as outras 14 facções e os outros 9 relógios
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia

## Conferências

| Verificação | Resultado |
|---|---|
| Cenas | 7 (a a g do roadmap) |
| Maior "entra" | 2103 caracteres (limite ~2.400) |
| Fato restrito ou secreto (marcador) em "entra" | 0 |
| Relógio em "entra" | 0 |
| Frase `nunca_diz` copiada em "entra" | 0 |
| Cena com lista "não entra" | 7 de 7 |
| NPC falecido, rota com duração ou termo de outro reino em "entra" | 0 (única menção: "quem chega do Véu", fluxo permitido) |
| NPC, lugar, facção, rota ou fato novo | 0 |

## Decisão pendente

**D-168 (aceita pela continuação):** aprovar as sete cenas e as escolhas (a) a (g). Opções: **A.** aprovar; **B.** trocar uma cena ou o lugar de teste (diga qual). **Recomendação: A.**
