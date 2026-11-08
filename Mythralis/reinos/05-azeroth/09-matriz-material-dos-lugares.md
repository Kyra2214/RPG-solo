# Azeroth — matriz material dos dez lugares (doc 09)

**Estado:** R5-3.3, D-155 aceita pela continuação · **Data:** 2026-11-07
**Etiqueta:** **[PROPOSTO]**; cruza o doc 01 (fonte primária), o doc 03, o doc 05 §1 e §2 e `08-registro-operacional-de-recursos.md`. Não cria produção, estoque, preço, tempo de reposição nem rota. O reino tem dez lugares (D-141): a matriz cobre os dez, no lugar dos vinte assentamentos dos reinos 1 e 2.

## 1. Como ler

"Rica em", "pobre em" e "depende de" têm a leitura do doc 08 §2. Um ofício **não prova** estoque; uma reserva **não prova** entrega suficiente; uma vulnerabilidade **não prova** colapso. `—` significa sem registro. Os dez lugares são fixos.

## 2. Matriz

| ID | Lugar | Base material documentada | Dependência ou risco documentado | Termos (doc 08) | Dados ainda necessários |
|---|---|---|---|---|---|
| `city.campoalvo` | Campoalvo | grão, farinha, gado de planície, tecido; celeiros comuns do excedente de várias vilas | cheia ou praga de campo pesa mais que um cerco; o Trigal diz que o reino come por causa dele, quem compra questiona o preço | rica em: grão, celeiro, tecido; pobre em: defesa, resistência a cheia e praga; depende de: colheita, rio, comprador | quem ajusta a medida em ano ruim (L-01); quem compra o excedente |
| `city.vaudouro` | Vaudouro | troca de grão por peixe, sal e tecido; docas, depósitos, aluguel de canal | se a Casa fecha as comportas o interior perde o caminho do mar; Trigal e Cais discutem quem taxa | rica em: troca, canal; pobre em: defesa; depende de: comportas abertas, duas coroas, lugar-mãe | a quem pagar quando as duas coroas reclamam a mesma passagem (L-02) |
| `city.torrefronte` | Torrefronte | guarnição; registro de quem chega e de quem parte | guarnição e vigia discordam sobre quem responde por quem chega; vive de suprimento de dentro | rica em: guarnição, registro; pobre em: mercado, sal e ferramenta próprios; depende de: suprimento de dentro, entendimento guarnição-vigia | quem lê o registro (L-03); de onde vêm sal e ferramenta (`desconhecido`) |
| `city.alvamar` | Alvamar | comércio marítimo, estaleiros, sal, pescado, carga; taxa do porto | tempestade ou bloqueio do porto pesa mais que cerco por terra; a Alfândega discute se a taxa serve ao porto ou à Coroa | rica em: mar, estaleiro, carga, taxa; pobre em: —; depende de: rota e maré, grão do interior, corrente | navios que descarregam fora do molhe e destino da taxa (L-04); de onde vem o sal |
| `city.enseada-vigia` | Enseada-Vigia | carga, água, conserto e notícia de mar; luz e barra | uma noite sem luz é perigo; Cais e Vento disputam os faroleiros e os sinais da costa | rica em: luz, água, conserto, notícia; pobre em: luz sem faroleiros; depende de: faroleiros, tempo, barra, lugar-mãe | de quem são os faróis e os sinais da costa (L-05); o que a rota das Costas troca |
| `city.concharia` | Concharia | casco, vela, corda, conserto; coral e concha cultivados | vive de madeira que chega de fora e do respeito ao santuário; canal maior mata o jardim, dizem os guardas | rica em: casco, conserto, cultivo; pobre em: madeira própria; depende de: madeira de fora, santuário, lugar-mãe | se um canal maior fere o santuário (L-06); origem da madeira (`desconhecida`) |
| `city.torreclara` | Torreclara | sinais, aviso, mapa, relógio de estrelas; hora e previsão vendidas | cerração longa, torre derrubada ou código quebrado a deixam cega; pouca força própria; compra grão e madeira de fora | rica em: sinal, aviso, mapa, previsão; pobre em: força própria, grão e madeira; depende de: vista longa, compra de fora | quem é avisado quando um código muda (L-07); origem do grão e da madeira comprados |
| `city.estrelado` | Estrelado | mapas celestes, calendário de viagem, ensino de orientação, festivais de rota | nenhuma defesa própria; estelares e luminares discordam sobre quem interpreta o céu | rica em: mapa celeste, calendário, ensino; pobre em: defesa própria; depende de: doações, Coroa do Vento pagar, lugar-mãe | quem interpreta o céu e que previsão cada coroa recebe (L-08) |
| `city.pouso-das-brisas` | Pouso-das-Brisas | sinal, pouso, comida de viagem, conserto de pipa e de torre | incêndio ou tempestade derruba a linha; as outras coroas desconfiam de que a linha serve só ao Vento | rica em: sinal, pouso, conserto; pobre em: —; depende de: vento bom, linha de torres inteira, lugar-mãe | se as outras coroas podem ter sinais próprios (L-07) |
| `city.meiovau` | Meiovau | hospedagem, tabelionato, câmbio, arbitragem, feira | sem muralha; o dia em que uma coroa quiser a cidade só para si ela deixa de existir; acolher quem chega do Véu | rica em: hospedagem, arbitragem, câmbio, feira; pobre em: defesa; depende de: ser útil às três coroas, três chaves | prazo, sustento e trabalho do chegado (L-10); uma chave que pode ter sido copiada (L-09) |

## 3. Conferências

- 10 lugares, 10 linhas; todo termo vem do campo Economia, Defesas e vulnerabilidades ou Problemas atuais do doc 01, ou do doc 05 §2; 0 número, nível, unidade ou preço; 0 produção ou estoque inventados.
- Os "dados ainda necessários" apontam para lacunas **já registradas** (leis L-01 a L-10 do doc 05 §1, doc 04 §10) ou para origens já `desconhecidas` (sal, madeira, grão comprado) e não criam nova.
- Mesmos 10 IDs de `12-assentamentos.csv`; nenhum lugar novo.

## 4. Decisão pendente

**D-155** (ver `08-registro-operacional-de-recursos.md` §8).
