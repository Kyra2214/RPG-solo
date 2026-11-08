# 15 — Cenas de teste (R2-6.3)

**Estado:** rascunho de R2-6.3 para aprovação do autor (D-56) · **Data:** 2026-11-05

**Etiqueta:** tudo é **[PROPOSTO]**; as cenas são testes de contexto, não texto de lore. **Fonte:** `14-resumos-de-contexto` (R2-6.1), `14-camadas-de-informacao` (R2-6.2), `13-sementes-de-aventura`, `12-voz.csv`, doc 07 (rotas e §4 de fronteira), doc 01 parte 5 (calendário). Dados em `15-cenas-de-teste.csv`. Molde: `reinos/01-.../15-cenas-de-teste.md`.

## Regras

- Cada cena tem a lista **entra** (texto exato que vai ao contexto da IA, no máximo ~2.400 caracteres) e a lista **não entra** (o que fica fora e quando entraria).
- Só entram fatos de camada **público** ou **local**; fato restrito ou secreto só entra depois de cumprido o caminho de descoberta (R2-6.2). Relógios nunca entram (V-73).
- O texto de "entra" é montado dos resumos, fatos, sementes, vozes e linhas de rota já escritos; nada foi acrescentado. A data é a abertura do jogo: Gelo Fechado, Lua 1, dia 1 do ano 516 das Atas (doc 01 parte 5).
- Nenhuma cena traz NPC com `vivo = não`, rota com duração, ou afirmação sobre Aeltharyn; a única menção é o veto de fronteira do doc 07 (§4).
- A contagem de caracteres inclui os rótulos `[id]` e as quebras entre linhas.

## `cena.vilarejo-rastro-branco` — Vilarejo de montanha: Rastro-Branco

Chegada a uma comunidade afastada dos passos e conversa com a liderança, sem carregar o reino.

**Entra (1064 caracteres):**

- [data] Ano 516 das Atas, Gelo Fechado (inverno), Lua 1, dia 1.
- [settlement.rastro-branco] Caçadores e guias de neve (Comunidade afastada). Liderança: Miraq Salto-de-Névoa (Felino das Nuvens). Obrigação: guiar viajantes pelos passos e avisar sobre avalanche. Interesse: caçar sem cota nos meses de fome. Tensão: a cidade quer limitar a caça por escassez; a comunidade diz que isso a obriga a descer ao vale.
- [settlement.rastro-branco] Cidade-mãe: Altavento; acesso sem duração (doc 07).
- [npc.miraq-salto-de-nevoa] Miraq Salto-de-Névoa: Liderança de Rastro-Branco; Felino das Nuvens; vivo(a). Capacidade: Guia viajantes pelos passos e avisa sobre avalanche; lidera Rastro-Branco. Limite: A cidade quer limitar a caça; os caçadores mais velhos a seguem e duvidam de Zhae.
- [seed.rastro-branco.p] O guia que recusa a travessia: Um guia de Rastro-Branco recusa uma travessia e Miraq Salto-de-Névoa quer entender o motivo antes que a recusa vire boato.
- [regra] Cota de caça, sinais de avalanche e o que a cidade decide ficam com o Mestre; só entram se a cena os disparar.

**Não entra:**

- `seed.rastro-branco.m` e `.g`: só se a cena os disparar
- `clock.passos` (secreto, uso de Mestre/motor)
- resumo completo de `city.altavento` (a vizinhança do assentamento já dá a cidade-mãe)
- os outros 19 assentamentos, as 14 facções e os 7 relógios
- `12-voz.csv` (Miraq não tem voz escrita) e `fact.zhae-expulsa-cacadores` (local): só se Zhae aparecer

## `cena.chegada-sinuosa` — Chegada a uma cidade subterrânea: Sinuosa

Entrada em Sinuosa, a cidade-labirinto, e primeiro contato com os guias.

**Entra (1426 caracteres):**

- [data] Ano 516 das Atas, Gelo Fechado (inverno), Lua 1, dia 1.
- [city.sinuosa] Cidade-labirinto subterrânea. Governo: Rotunda dos Mapas, com presidência rotativa entre famílias de guias; neste ciclo, Ilmeth Curva-Segura. Vive de guias de galeria, mapas, cogumelos de caverna e abastecimento por troca. Tensões: As galerias novas desorientam até os guias; mapas rivais circulam e cada família jura que o seu está certo. Povos: Labiríntico; Escamado Miúdo; Gnomo (das rochas). Porte: média (relativo, sem número).
- [npc.nael-curva-antiga] Nael Curva-Antiga: Mestre da Casa dos Guias; Labiríntico; vivo(a). Agenda: Manter vivo o ofício de guiar e impedir a venda do mapa errado. Pressão: Mapas rivais circulam; ela não sabe qual está certo. Capacidade: Mestra da Casa dos Guias, em Sinuosa; guia pelas galerias. Limite: Só guia; não comanda a cidade.
- [voz npc.nael-curva-antiga] tom: paciente e cética; modelo: Mapa honesto vale mais que mapa bonito."|"Eu guio. A cidade tem quem a governe."|"Entre com guia, ou não entre.
- [fact.nael-preside-guias|publico] preside a Casa dos Guias
- [fact.mp-06-galerias-desorientam|local] as galerias novas desorientam os guias
- [fact.mp-06-mapas-rivais|local] mapas rivais circulam e nenhum mapa é fonte final (L-05)
- [seed.sinuosa.p] O corredor onde não havia: Um guia conta de um corredor onde não havia nenhum e Nael Curva-Antiga quer que alguém confirme com marca e registro, sem presumir causa.

**Não entra:**

- `fact.nael-galeria-fora-dos-mapas` (restrito): só com favor, comparação de cópias da Rotunda dos Mapas ou testemunho de guias
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia
- hipóteses e "a verdade não pode ser" do MP-06 (`13-misterios-limites.md`): só ao aprofundar
- `seed.sinuosa.m` e `.g`: só se disparadas
- relógios (nenhum liga a Sinuosa) e as demais nove cidades

## `cena.audiencia-concilio` — Audiência no Concílio (Pedra-Ata)

Pedido público de uma cidade ao Concílio, com a presidente e o arquivista-chefe.

**Entra (2033 caracteres):**

- [data] Ano 516 das Atas, Gelo Fechado (inverno), Lua 1, dia 1.
- [city.pedra-ata] Cidade-sede do arquivo e do Concílio das Coroas. Governo: Mesa de Pedra-Ata, presidida por Velka Selo-Firme (Guardiã do Selo), que também é presidente de guarda do Concílio. Vive de arquivo, escribas, mediação, hospedagem de delegações e serviço de selos. Tensões: Cada cidade quer que o assento rotativo comece por ela; o arquivo guarda atas que as cidades contestam. Povos: Anão; Draconato; Felino das Nuvens. Porte: média (relativo, sem número).
- [npc.velka-selo-firme] Velka Selo-Firme: Presidente do Concílio das Coroas neste ciclo; Anão; vivo(a). Agenda: Manter o assento rotativo funcionando e impedir que uma cidade o capture. Pressão: Cada cidade a cobra por uma ata diferente; o ciclo dela termina sem sucessor combinado. Capacidade: Convoca e preside o Concílio das Coroas e aplica o selo; preside a Mesa de Pedra-Ata. Limite: Não manda nas outras nove cidades; só convoca, preside e guarda o selo do Concílio.
- [voz npc.velka-selo-firme] tom: cautelosa e formal, evita se expor; modelo: O assento não é de ninguém. É de todos, por um ciclo."|"Convoco; não mando. A decisão é das cidades."|"Peço que isso conste da ata, na forma que o Concílio aprovar.
- [npc.maro-tinta-velha] Maro Tinta-Velha: Arquivista-chefe das Coroas; Draconato; vivo(a). Agenda: Guardar as atas sem que a política as reescreva. Pressão: As cidades pedem cópias diferentes; ele recusa as que mentem. Capacidade: Arquivista-chefe das Coroas; guarda e copia as atas, em Pedra-Ata. Limite: Guarda e copia; não decide o que vale.
- [fact.velka-preside-selo|publico] preside o Concílio neste ciclo e guarda o selo
- [fact.maro-guarda-arquivo|publico] guarda o Arquivo das Coroas
- [seed.pedra-ata.m] A cópia mais antiga da ata: Maro pede quem encontre a cópia mais antiga de uma ata contestada; cada cidade guarda a cópia que lhe convém e a disputa chega ao Concílio.
- [regra] O Concílio convoca e preside; não manda nas outras nove cidades. Cópia de ata não prova conteúdo verdadeiro.

**Não entra:**

- `fact.velka-ata-escondida` (secreto): só por comparação, testemunho e confiança
- `fact.maro-pacto-copiado` e `fact.mp-04-tres-copias-ata` (restritos): só com autorização do Arquivo, favor ou ordem legal
- coluna `nunca_diz` de `12-voz.csv`
- `clock.assento` (secreto)
- `seed.pedra-ata.p` e `.g`: só se disparadas
- as outras nove cidades em detalhe

## `cena.investigacao-arquivo` — Investigação no Arquivo das Coroas

Busca de uma ata contestada nas cópias do Arquivo, com a chefia e a liderança de Arquivo-Velho.

**Entra (1469 caracteres):**

- [data] Ano 516 das Atas, Gelo Fechado (inverno), Lua 1, dia 1.
- [faction.arquivistas-das-coroas] Arquivistas das Coroas: guardar as atas sem que a política as reescreva e copiar só o que é verdadeiro. Métodos: cópia com cadeia de custódia, recusa de cópia falsa, ata fechada. Tensão: cada cidade pede cópia diferente; atas fechadas alimentam desconfiança. Liderança: Maro Tinta-Velha. Aliados: faction.rede-dos-passos-altos (mensageiros carregam cópias). Rivais: desconhecido.
- [npc.maro-tinta-velha] Maro Tinta-Velha: Arquivista-chefe das Coroas; Draconato; vivo(a). Agenda: Guardar as atas sem que a política as reescreva. Pressão: As cidades pedem cópias diferentes; ele recusa as que mentem. Capacidade: Arquivista-chefe das Coroas; guarda e copia as atas, em Pedra-Ata. Limite: Guarda e copia; não decide o que vale.
- [npc.haldra-tinta-funda] Haldra Tinta-Funda: Liderança de Arquivo-Velho; Anão; vivo(a). Capacidade: Copia e guarda as atas do Concílio; lidera Arquivo-Velho. Limite: As cidades contestam atas e cada uma pede cópias diferentes.
- [fact.maro-guarda-arquivo|publico] guarda o Arquivo das Coroas
- [seed.arquivo-velho.m] Uma cadeia de custódia: As cópias divergentes da ata do assento têm cadeias diferentes; Haldra pede quem trace a custódia de uma delas.
- [regra] Cópias divergentes entram todas no Arquivo; a cadeia de custódia (selo, tinta, cópia mais antiga) registra, não decide o que vale. Ata fechada só abre por matéria reservada, por pedido formal.

**Não entra:**

- `fact.mp-04-tres-copias-ata` e `fact.maro-pacto-copiado` (restritos): só após autorização do Arquivo, favor ou ordem legal
- `fact.velka-ata-escondida` (secreto)
- hipóteses e vetos do MP-04 (`13-misterios-limites.md`): só ao aprofundar
- `seed.arquivo-velho.p` e `.g`: só se disparadas
- `clock.assento` (secreto)
- `12-relacoes.csv` e relações marcadas como segredo

## `cena.viagem-barcaalta-brasalume` — Viagem por rota: Barcaalta para Brasalume

Deslocamento com carga de metal para o cais, com duração desconhecida.

**Entra (935 caracteres):**

- [data] Ano 516 das Atas, Gelo Fechado (inverno), Lua 1, dia 1.
- [city.barcaalta] Ligações: Portão-Cinza, Rodaviva e Brasalume (sugeridas por dependência, sem duração); descida do rio, fora do reino (não descrita); Pedra-Ata.
- [city.brasalume] Ligações: Barcaalta (sugerida por dependência: metal para o cais, sem duração); Pedra-Ata (delegações, sem duração).
- [route.barcaalta-brasalume] Barcaalta ↔ Brasalume: carga de metal e ferramentas para o cais; ligação sugerida por dependência; duração desconhecida; sem percurso descrito.
- [fact.mp-05-registro-descida-taxas|local] o Registro da Descida e as taxas não batem; os dois registros valem até conferência (L-09)
- [seed.barcaalta.m] Carga contra taxa: Torgrim Corda-Dupla pede quem confira a carga de uma barca sem registro contra a taxa paga, antes que a reputação do cais sofra.
- [regra] Nenhuma duração, distância ou modificador de estação está definido: não inventar tempo de viagem.

**Não entra:**

- `clock.cais` e `clock.combustivel` (secretos)
- `event` de cais saturado e de combustível: só se disparados
- `route.barcaalta-argente` e a descida do rio (não descrita)
- demais rotas, todas sem duração
- resumos completos de Barcaalta e Brasalume (a vizinhança basta para a viagem)

## `cena.travessia-espinha-portao-cinza` — Travessia da Espinha rumo a Torvelume (Portão-Cinza)

Viajante em Portão-Cinza pergunta como seguir para Torvelume.

**Entra (1711 caracteres):**

- [data] Ano 516 das Atas, Gelo Fechado (inverno), Lua 1, dia 1.
- [city.portao-cinza] Cidade-fronteira e posto de descida. Governo: Mesa do Portão, presidida por Thaurak Escama-Fria (Portão-Mor); acúmulo intencional com os Vigias do Limiar. Vive de inspeção de passagens, couro, vidro natural e hospedagem de caravanas. Tensões: Quem sobe e quem desce discutem tarifas; os vigias dizem que há mais viajantes sem registro do que antes. Povos: Anão; Draconato; Escamado Miúdo. Porte: média (relativo, sem número).
- [npc.thaurak-escama-fria] Thaurak Escama-Fria: General do Limiar do Esporão; Draconato; vivo(a). Agenda: Manter o portão aberto e vigiado ao mesmo tempo, sem fechar a descida. Pressão: Faltam vigias; o Livro de Passagens tem lacunas que ele herdou. Capacidade: Comanda a guarda do Limiar do Esporão, em Portão-Cinza. Limite: Não comanda os exércitos das outras cidades; só age fora do portão com voto do Concílio.
- [voz npc.thaurak-escama-fria] tom: disciplinado e reservado; modelo: O portão fica aberto e vigiado. As duas coisas, ao mesmo tempo."|"Meu posto termina no limiar. Além dele, só com voto do Concílio."|"Registre quem sobe. Registre quem desce.
- [fact.thaurak-comanda-vigias|publico] comanda os vigias do Portão-Cinza
- [fact.mp-02-viajantes-sem-registro|local] há mais viajantes sem registro do que antes
- [fact.mp-02-paginas-faltam|local] faltam páginas no Livro de Passagens
- [route.portao-cinza-limiar] Portão-Cinza ↔ limiar do esporão: portões duplos e galerias de vigia; duração desconhecida; não é rota para Aeltharyn.
- [regra] Portão-Cinza ↔ Torvelume: sem rota, sem tempo. Nenhuma passagem nova pela Espinha e nenhuma distância entre os reinos; quem fala de Torvelume não sabe o caminho.

**Não entra:**

- `fact.thaurak-protege-quem-arrancou` (secreto): só por comparação, testemunho e confiança
- `clock.passagens` (secreto)
- coluna `nunca_diz` de `12-voz.csv`
- qualquer afirmação sobre Aeltharyn, sobre a Espinha de Vidro além do limite acima ou sobre o Mistério D
- `seed.portao-cinza.m` e `.g`: só se disparadas; `seed.portao-cinza.p` não entra (luzes sem origem presumida)

## Resultado

- `cena.vilarejo-rastro-branco`: 1064 caracteres (dentro do limite)
- `cena.chegada-sinuosa`: 1426 caracteres (dentro do limite)
- `cena.audiencia-concilio`: 2033 caracteres (dentro do limite)
- `cena.investigacao-arquivo`: 1469 caracteres (dentro do limite)
- `cena.viagem-barcaalta-brasalume`: 935 caracteres (dentro do limite)
- `cena.travessia-espinha-portao-cinza`: 1711 caracteres (dentro do limite)
