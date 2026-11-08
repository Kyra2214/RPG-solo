# Coroas de Pedra — Registro operacional de recursos (doc 08)

**Estado:** rascunho de R2-3.3 para aprovação do autor · **Data:** 2026-11-04
**Etiqueta:** **[PROPOSTO]**; só organiza a evidência dos docs 01, 03, 04, 05, 06 e 07 (D-31 a D-42) e acrescenta **níveis provisórios** (D-43, ASSUMINDO). Nada vira cânone sem aprovação. Dados em `12-recursos.csv` (84 linhas, 29 localidades); assentamentos em `09-matriz-material-dos-assentamentos.md`.
**Regras seguidas:** escala ordinal 0 a 5, nunca quantidade; reserva `?` em toda parte; desconhecido nunca vira 0; nenhum NPC, cidade, facção, assentamento ou rota novos; nada afirma nem nega fornecimento de minério, carvão ou ferramentas a Aeltharyn (contrato §3.4, F9); nada sobre a mina selada nem o Mistério D.

## 1. Objetivo e limites

O registro diz o que cada localidade produz, guarda, consome e de quem depende, **sem fabricar precisão**. Uma oficina forte não prova peça em estoque; um armazém não prova estoque à venda; uma cota não prova reserva cheia. Os níveis servem para escolher cenas e consequências, não para simular a economia. Unidade, período-base e reposição continuam **não definidos**.

## 2. Estados de evidência

- **Documentado:** afirmado por um doc aprovado do reino 2 (cite o doc).
- **Estimativa provisória:** nível criado aqui, com método e incerteza de ±1 (D-43); cede a qualquer fato canônico.
- **Derivado:** calculado por regra aberta (seção 3.3); herda a incerteza da entrada.
- **ASSUMINDO:** dependência inferida da função, sem texto que a afirme.
- **Desconhecido:** sem base. **Em conflito:** duas fontes não permitem escolher. Desconhecido **não** é zero; zero seria ausência confirmada e **nenhum** campo recebe 0.

## 3. Escala ordinal 0 a 5

### 3.1 Leitura

| Nível | Leitura |
|---|---|
| 0 | ausência **confirmada** (nenhum uso neste reino) |
| 1 | mínimo |
| 2 | baixo ou parcial |
| 3 | médio |
| 4 | alto |
| 5 | dominante; **reservado a decisão do autor** (nenhum doc afirma monopólio) |
| ? | desconhecido |

Nível é relativo à **função local**, não é peso, volume nem dias de reserva. Níveis **não se somam** entre localidades nem se convertem em unidade.

### 3.2 Regra dos níveis provisórios de produção (D-43)

| Nível | Regra | Aplicação |
|---|---|---|
| 4 | a cidade é a fonte principal de um recurso central: é o seu ofício na contribuição do Concílio (doc 04 §6) | as dez cidades, uma por ofício (alimento e combustível não têm cidade-fonte) |
| 3 | assentamento que, por obrigação documentada, fornece insumo ao ofício da cidade-mãe | doc 03 |
| 2 | fonte parcial ou secundária documentada (inclui sazonal) | doc 01 e doc 03 |
| ? | sem produção medível (registro, hospedagem, mercado, condição) ou sem texto | sem nível |

A tendência (por exemplo, o veio que rende menos) fica em palavras, não em nível. **28 níveis de produção** foram atribuídos; todos ±1.

### 3.3 Demanda derivada (água e alimento)

Estado *Derivado*: consumo humano básico proporcional ao porte relativo do doc 05 §4 (grande 4, média 3, pequena 2), **sem** uso industrial, de carga ou de corpo (Cumieira não leva ajuste). Vale só para as dez cidades; o porte é palpite de confiança baixa (D-42), então a demanda herda isso. Bairros e postos sazonais não têm demanda própria. Demais recursos: demanda `?`.

| Cidade | Faixa (doc 05 §4) | Demanda derivada |
|---|---|---:|
| `city.brasalume` (Brasalume) | grande | 4 |
| `city.altavento` (Altavento) | pequena | 2 |
| `city.veiaclara` (Veiaclara) | média | 3 |
| `city.barcaalta` (Barcaalta) | grande | 4 |
| `city.portao-cinza` (Portão-Cinza) | média | 3 |
| `city.sinuosa` (Sinuosa) | média | 3 |
| `city.cumieira` (Cumieira) | média | 3 |
| `city.rodaviva` (Rodaviva) | média | 3 |
| `city.geodalia` (Geodália) | pequena | 2 |
| `city.pedra-ata` (Pedra-Ata) | média | 3 |

### 3.4 Reserva e balanço

**Reserva é `?` em todos os registros.** O balanço qualitativo (crítico, baixo, estável, excedente) só se deduz quando produção, reserva e demanda de uma localidade têm nível; hoje o balanço é **desconhecido** em todas. Comparar produção de uma localidade com demanda de outra, ou poço com nascente, **não é balanço**. Três situações são **documentadas só em palavras**: combustível em falta em Brasalume (grau desconhecido), cais com procura acima da capacidade em Barcaalta e peças raras sumindo em Rodaviva.

## 4. Recursos centrais por localidade

### 4.1 Metal, minério, vidro vulcânico e fogo de forja (`metal`)

| Localidade | Papel | Produção | Reserva | Demanda | Estado | Fonte |
|---|---|---:|---:|---:|---|---|
| `city.brasalume` (Brasalume — A Forja dos Dentes) | produtor (forja, minério, metal, vidro vulcânico, fogo de forja); principal fonte de metal e fogo do reino; limitada pela falta de combustível (origem do que falta: desconhecida) | 4 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 01 §2.1; doc 04 §6 |
| `settlement.veio-fundo` (Veio-Fundo — A Mina de Superfície) | produtor local (minério em cotas); o veio rende menos a cada ano (tendência documentada; nível da queda desconhecido) | 3 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 03 S-02 |
| `settlement.cinzeiro` (Cinzeiro — Vila das Cinzas) | produtor local (cinza, escória e vidro vulcânico); entrega às fornalhas; queixa sobre a fumaça é da vila, sem prova | 3 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 03 S-01 |
| `city.barcaalta` (Barcaalta — O Porto da Cabeceira) | transportador (carga de metal e ferramentas até o cais); registro de carga e de taxas não batem (L-09) | ? | ? | ? | Documentado (papel); níveis desconhecidos | doc 01 C-04; doc 05 §2.2 |
| `city.rodaviva` (Rodaviva — A Cidade que Despertou) | dependente (metal para as oficinas; ASSUMINDO, pela função); não se afirma de onde Rodaviva tira o metal além da função | ? | ? | ? | ASSUMINDO (dependência por função); níveis desconhecidos | doc 05 §2.2 |
### 4.2 Combustível (`combustivel`)

| Localidade | Papel | Produção | Reserva | Demanda | Estado | Fonte |
|---|---|---:|---:|---:|---|---|
| `city.brasalume` (Brasalume — A Forja dos Dentes) | consumidor; há produção local, abaixo do consumo; não se afirma que venha de Aeltharyn, das Marcas ou de outro lugar (contrato §3.4) | ? | ? | ? | Documentado (consumo acima da produção local; grau desconhecido); origem do que falta: Desconhecida | doc 01 C-01; doc 05 §2.1, §2.2 |
### 4.3 Água limpa e gelo (`agua`)

| Localidade | Papel | Produção | Reserva | Demanda | Estado | Fonte |
|---|---|---:|---:|---:|---|---|
| `city.veiaclara` (Veiaclara — A Cidade da Nascente) | produtor (nascente, gelo, água medida pelas comportas) e consumidor; comporta velha pode ceder; degelo mudou de data; vale só dentro das Coroas, sem efeito afirmado em Aeltharyn | 4 (provisório, ±1) | ? | 3 | Documentado (papel); produção: estimativa provisória (D-43); demanda derivada (porte relativo, ASSUMINDO) | doc 01 §2.3; doc 04 §6, §7 |
| `settlement.comporta-velha` (Comporta-Velha — O Distrito das Comportas) | regulador (guarda e registro das comportas); ninguém paga a manutenção | ? | ? | ? | Documentado (papel); níveis desconhecidos | doc 03 S-05 |
| `settlement.lago-de-cima` (Lago-de-Cima — A Vila do Gelo) | produtor local (gelo do lago alto); limite de coleta defendido pela vila, sem prova | 3 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 03 S-06 |
| `city.brasalume` (Brasalume — A Forja dos Dentes) | consumidor; poços (poucos); capacidade dos poços desconhecida; dependência de água de fora não afirmada | ? | ? | 4 | Documentado (papel); demanda derivada (porte relativo, ASSUMINDO) | doc 01 §2.1 (vulnerabilidades) |

**Demais localidades (8):** Altavento — A Cidade dos Picos, Barcaalta — O Porto da Cabeceira, Portão-Cinza — O Limiar do Esporão, Sinuosa — A Cidade dos Mil Corredores, Cumieira — A Casa dos Grandes, Rodaviva — A Cidade que Despertou, Geodália — A Cidade das Facetas, Pedra-Ata — A Cidade do Concílio. Papel: consumidor; produção e reserva `?`; fonte doc 05 §2.2. Demanda de cada cidade na tabela da seção 3.3. O CSV traz uma linha por localidade.
### 4.4 Alimento e suprimento (`alimento`)

| Localidade | Papel | Produção | Reserva | Demanda | Estado | Fonte |
|---|---|---:|---:|---:|---|---|
| `city.altavento` (Altavento — A Cidade dos Picos) | consumidor e fonte parcial (caça de neve); nevasca cortou parte dos mantimentos (C-02) | 2 (provisório, ±1) | ? | 2 | Documentado (papel); produção: estimativa provisória (D-43); demanda derivada (porte relativo, ASSUMINDO) | doc 01 §2.2 (C-02) |
| `city.sinuosa` (Sinuosa — A Cidade dos Mil Corredores) | consumidor e fonte parcial (cogumelos de caverna); abastecimento por troca; quem fornece o resto: desconhecido | 2 (provisório, ±1) | ? | 3 | Documentado (papel); produção: estimativa provisória (D-43); demanda derivada (porte relativo, ASSUMINDO) | doc 01 §2.6 |
| `city.cumieira` (Cumieira — A Casa dos Grandes) | consumidor e fonte parcial (rebanhos de altitude); sem número de rebanhos | 2 (provisório, ±1) | ? | 3 | Documentado (papel); produção: estimativa provisória (D-43); demanda derivada (porte relativo, ASSUMINDO) | doc 01 §2.7 |
| `settlement.pasto-de-cume` (Pasto-de-Cume — O Pasto de Altitude) | produtor local (carne); fornece à cidade; o caminho pelos pastos é disputado | 2 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 03 S-14 |
| `settlement.rastro-branco` (Rastro-Branco — A Comunidade da Neve) | produtor local (caça de neve); a cidade quer limitar a caça por escassez | 2 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 03 S-04 |

**Demais localidades (7):** Brasalume — A Forja dos Dentes, Veiaclara — A Cidade da Nascente, Barcaalta — O Porto da Cabeceira, Portão-Cinza — O Limiar do Esporão, Rodaviva — A Cidade que Despertou, Geodália — A Cidade das Facetas, Pedra-Ata — A Cidade do Concílio. Papel: consumidor; produção e reserva `?`; fonte doc 05 §2.2. Demanda de cada cidade na tabela da seção 3.3. O CSV traz uma linha por localidade.
### 4.5 Cristal lapidado, lentes e sinais de luz (`cristal-e-luz`)

| Localidade | Papel | Produção | Reserva | Demanda | Estado | Fonte |
|---|---|---:|---:|---:|---|---|
| `city.geodalia` (Geodália — A Cidade das Facetas) | produtor (cristal lapidado, lentes, sinais de luz); cotas apertam (L-06); lente antiga rachada (N-08) | 4 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 01 §2.9; doc 04 §6 |
| `settlement.corte-fino` (Corte-Fino — A Comunidade dos Lapidadores) | produtor local (lapidação, entrega às lentes); cotas apertaram depois de um tremor | 3 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 03 S-18 |
| `settlement.poco-alto` (Poço-Alto — O Distrito do Poço de Luz) | condição da produção (poço de luz e lentes); obras da cidade ameaçam sombrear o poço | ? | ? | ? | Documentado (papel); níveis desconhecidos | doc 03 S-17 |
| `city.veiaclara` (Veiaclara — A Cidade da Nascente) | produtor parcial (cristal bruto); só cristal bruto; sinais e lentes são de Geodália | 2 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 01 §2.3 |

**Demais localidades (9):** Brasalume — A Forja dos Dentes, Altavento — A Cidade dos Picos, Veiaclara — A Cidade da Nascente, Barcaalta — O Porto da Cabeceira, Portão-Cinza — O Limiar do Esporão, Sinuosa — A Cidade dos Mil Corredores, Cumieira — A Casa dos Grandes, Rodaviva — A Cidade que Despertou, Pedra-Ata — A Cidade do Concílio. Papel: dependente (luz e sinais); produção e reserva `?`; fonte doc 05 §2.2. O CSV traz uma linha por localidade.
### 4.6 Pedra de obra, trabalho pesado, lã e rebanhos de altitude (`pedra-e-rebanhos`)

| Localidade | Papel | Produção | Reserva | Demanda | Estado | Fonte |
|---|---|---:|---:|---:|---|---|
| `city.cumieira` (Cumieira — A Casa dos Grandes) | produtor (pedra de obra, trabalho pesado, rebanhos); escala de construção, não de população | 4 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 01 §2.7; doc 04 §6 |
| `settlement.pasto-de-cume` (Pasto-de-Cume — O Pasto de Altitude) | produtor local (lã grossa); o clã vê o caminho proposto como quebra de juramento | 3 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 03 S-14 |
| `city.altavento` (Altavento — A Cidade dos Picos) | produtor parcial (lã de cume); sem número | 2 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 01 §2.2 |
### 4.7 Peças de precisão, mecanismos e reparo (`pecas`)

| Localidade | Papel | Produção | Reserva | Demanda | Estado | Fonte |
|---|---|---:|---:|---:|---|---|
| `city.rodaviva` (Rodaviva — A Cidade que Despertou) | produtor (peças de precisão, mecanismos, reparo); peças raras somem (C-08): escassez de peça rara, não de oficina; sem lei sobre Despertos | 4 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 01 §2.8; doc 04 §6 |
| `settlement.roda-menor` (Roda-Menor — O Distrito das Rodas) | produtor parcial (rodas, engrenagens, relojoaria); acusada do sumiço das peças raras, sem prova | 2 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 03 S-15 |
| `settlement.oficina-sem-dono` (Oficina-Sem-Dono — A Casa dos Despertos) | sem registro de produção; núcleo de construtos sem dono; não é produtor registrado; foro indefinido | ? | ? | ? | Documentado (papel); níveis desconhecidos | doc 03 S-16 |
| `city.brasalume` (Brasalume — A Forja dos Dentes) | dependente (peças e reparo; ASSUMINDO, pela função); uma da outra com Rodaviva, sem percurso | ? | ? | ? | ASSUMINDO (dependência por função); níveis desconhecidos | doc 05 §2.2 |
| `city.barcaalta` (Barcaalta — O Porto da Cabeceira) | dependente (peças para cais e construtos de carga); construtos de carga exigem regras de uso | ? | ? | ? | Documentado (papel); níveis desconhecidos | doc 01 C-04; doc 05 §2.2 |
### 4.8 Mapas e guias de galeria (`mapas-e-guias`)

| Localidade | Papel | Produção | Reserva | Demanda | Estado | Fonte |
|---|---|---:|---:|---:|---|---|
| `city.sinuosa` (Sinuosa — A Cidade dos Mil Corredores) | produtor (guias de galeria e mapas); mapas rivais: é questão de confiança, não de capacidade | 4 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 01 §2.6; doc 04 §6 |
| `settlement.fenda-mercado` (Fenda-Mercado — O Mercado de Fendas) | mercado (neutro entre famílias de guias); acusado de vender o mapa errado | ? | ? | ? | Documentado (papel); níveis desconhecidos | doc 03 S-11 |
| `settlement.boca-larga` (Boca-Larga — O Acesso Externo) | produtor parcial (entrega de guia na entrada, sazonal); um viajante que entrou sozinho se perdeu | 2 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 03 S-12 |
### 4.9 Mensageiros e guia de passo (`mensagens-e-passos`)

| Localidade | Papel | Produção | Reserva | Demanda | Estado | Fonte |
|---|---|---:|---:|---:|---|---|
| `city.altavento` (Altavento — A Cidade dos Picos) | produtor (mensageiros de altitude, guia de passo); mensageiros recusam voar quando o vento muda; nevasca fecha passos (CL-07) | 4 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 01 §2.2; doc 04 §6 |
| `settlement.rastro-branco` (Rastro-Branco — A Comunidade da Neve) | produtor parcial (guia de neve, aviso de avalanche); sem número | 2 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 03 S-04 |
| `settlement.ninho-seco` (Ninho-Seco — O Posto dos Mensageiros) | posto sazonal de repasse; sem reserva para nevasca longa; pagamentos atrasados | ? | ? | ? | Documentado (papel); níveis desconhecidos | doc 03 S-03 |

**Demais localidades (9):** Brasalume — A Forja dos Dentes, Veiaclara — A Cidade da Nascente, Barcaalta — O Porto da Cabeceira, Portão-Cinza — O Limiar do Esporão, Sinuosa — A Cidade dos Mil Corredores, Cumieira — A Casa dos Grandes, Rodaviva — A Cidade que Despertou, Geodália — A Cidade das Facetas, Pedra-Ata — A Cidade do Concílio. Papel: dependente (mensagem e guia de passo); produção e reserva `?`; fonte doc 05 §2.2. O CSV traz uma linha por localidade.
### 4.10 Embarcações, cordoaria, carga e taxas (`carga-e-cais`)

| Localidade | Papel | Produção | Reserva | Demanda | Estado | Fonte |
|---|---|---:|---:|---:|---|---|
| `city.barcaalta` (Barcaalta — O Porto da Cabeceira) | produtor (cais, barcas, cordoaria, registro da Descida); mais barcas descem do que o cais comporta; os dois registros valem até conferência | 4 (provisório, ±1) | ? | ? | Em conflito (registro de carga × taxas, L-09); produção: estimativa provisória (D-43); capacidade abaixo da procura (documentado) | doc 01 C-04; doc 05 §3 |
| `settlement.cordoaria` (Cordoaria — O Bairro das Cordas) | produtor local (cordas, correntes, velas); o cais compra de fora quando o preço sobe; origem de fora: não definida | 3 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 03 S-07 |
| `settlement.primeira-rampa` (Primeira-Rampa — O Núcleo do Registro) | registro (barcas e taxas); cada lado culpa o outro | ? | ? | ? | Em conflito (registro de carga × taxas); níveis desconhecidos | doc 03 S-08 |
### 4.11 Passagem, couro, vidro natural e hospedagem de caravanas (`passagem-e-hospedagem`)

| Localidade | Papel | Produção | Reserva | Demanda | Estado | Fonte |
|---|---|---:|---:|---:|---|---|
| `city.portao-cinza` (Portão-Cinza — O Limiar do Esporão) | produtor (inspeção de passagens, couro, vidro natural, hospedagem de caravanas); tarifas em disputa; viajantes sem registro; sem rota para Aeltharyn | 4 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 01 §2.5; doc 04 §6 |
| `settlement.galeria-alta` (Galeria-Alta — O Núcleo dos Vigias) | alojamento e escala dos vigias; faltam vigias; lacunas do Livro sem causa conhecida | ? | ? | ? | Documentado (papel); níveis desconhecidos | doc 03 S-10 |
| `settlement.albergue-de-cima` (Albergue-de-Cima — A Hospedagem Sazonal) | produtor parcial (hospedagem sazonal); só nos meses de passagem; os vigias querem registrar hóspedes | 2 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 03 S-09 |
### 4.12 Arquivo, escribas, selos e hospedagem de delegações (`arquivo-e-selos`)

| Localidade | Papel | Produção | Reserva | Demanda | Estado | Fonte |
|---|---|---:|---:|---:|---|---|
| `city.pedra-ata` (Pedra-Ata — A Cidade do Concílio) | produtor (arquivo, escribas, selos, hospedagem de delegações); cópias contestadas (L-08); vulnerável a incêndio | 4 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 01 §2.10; doc 04 §6 |
| `settlement.arquivo-velho` (Arquivo-Velho — O Núcleo dos Escribas) | produtor local (cópia e guarda de atas); cada cidade pede cópias diferentes | 3 (provisório, ±1) | ? | ? | Documentado (papel); produção: estimativa provisória (D-43) | doc 03 S-20 |
| `settlement.patio-das-delegacoes` (Pátio-das-Delegações — O Bairro da Hospedagem) | hospedagem de delegados; delegados chegam sem aviso | ? | ? | ? | Documentado (papel); níveis desconhecidos | doc 03 S-19 |

**Demais localidades (9):** Brasalume — A Forja dos Dentes, Altavento — A Cidade dos Picos, Veiaclara — A Cidade da Nascente, Barcaalta — O Porto da Cabeceira, Portão-Cinza — O Limiar do Esporão, Sinuosa — A Cidade dos Mil Corredores, Cumieira — A Casa dos Grandes, Rodaviva — A Cidade que Despertou, Geodália — A Cidade das Facetas. Papel: dependente (arquivo, selo, hospedagem); produção e reserva `?`; fonte doc 05 §2.2. O CSV traz uma linha por localidade.

## 5. Dependências críticas

Cada dependência é de **tipo**; quanto, com que frequência e por qual rota continua não definido (R2-3.1: toda duração `desconhecida`).

| ID | Quem depende | De quem | Do quê | Situação | Estado de evidência | Fonte |
|---|---|---|---|---|---|---|
| DR-01 | Brasalume | origem não definida | combustível | em falta (grau desconhecido) | Documentado (falta); origem: Desconhecida | doc 01 C-01; doc 05 §2.1, §2.2 |
| DR-02 | cidades a jusante (não nomeadas) | Veiaclara | água medida | pedem sem pagar manutenção; comporta velha pode ceder | Documentado; quais cidades: Desconhecido | doc 01 C-03; doc 04 §7; doc 05 §2.2 |
| DR-03 | todas as cidades | Geodália | luz e sinais | cotas apertando | Documentado | doc 05 §2.2; doc 01 C-09 |
| DR-04 | todas as cidades | Altavento e Rede dos Passos Altos | mensagem e guia de passo | passos fechados por nevasca | Documentado (doc 05); doc 07 não nomeia as cidades ligadas | doc 05 §2.2; doc 01 C-02; doc 04 §2.4 |
| DR-05 | Brasalume e Rodaviva | uma da outra | metal e peças | peças raras somem | ASSUMINDO (pela função de cada uma) | doc 05 §2.2; doc 01 C-01, C-08 |
| DR-06 | Barcaalta | Brasalume e Rodaviva | carga de metal e ferramentas; peças | registro de carga e de taxas não batem | Documentado (carga); registro: Em conflito | doc 01 C-04; doc 05 §2.2 |
| DR-07 | Concílio e as nove cidades | Pedra-Ata | arquivo, selo e hospedagem | cópias contestadas | Documentado | doc 01 C-10; doc 04 §2, §6; doc 05 §2.2 |
| DR-08 | as dez cidades (nominais: Cumieira e Pedra-Ata) | origem não definida e demais cidades | alimento e suprimento | desconhecido | ASSUMINDO (nenhuma cidade se alimenta só do que tem); origem: Desconhecida | doc 05 §2.2 |
| DR-09 | Altavento | outras cidades (não nomeadas) | mantimentos; pagamento de repasse | nevasca cortou parte dos mantimentos; pagamentos atrasados | Documentado | doc 01 C-02; doc 03 S-03 |
| DR-10 | Sinuosa | outras cidades (não nomeadas) | abastecimento por troca | quem fornece e quanto: desconhecido | Documentado (modo: troca); fornecedor: Desconhecido | doc 01 §2.6; doc 03 S-11 |
| DR-11 | Portão-Cinza | viajantes e caravanas | tarifa, hospedagem, depósitos de emergência | tarifas em disputa; viajantes sem registro | Documentado | doc 01 C-05; doc 05 §2.3 |

### 5.1 Nenhuma cidade funciona isolada

| Cidade | Dependências (como dependente, salvo "fornece") |
|---|---|
| `city.brasalume` (Brasalume) | DR-01, DR-03, DR-04, DR-05, DR-06 (fornece), DR-07, DR-08 |
| `city.altavento` (Altavento) | DR-03, DR-04 (fornece), DR-07, DR-08, DR-09 |
| `city.veiaclara` (Veiaclara) | DR-02 (fornece), DR-03, DR-04, DR-07, DR-08 |
| `city.barcaalta` (Barcaalta) | DR-03, DR-04, DR-06, DR-07, DR-08 |
| `city.portao-cinza` (Portão-Cinza) | DR-03, DR-04, DR-07, DR-08, DR-11 |
| `city.sinuosa` (Sinuosa) | DR-03, DR-04, DR-07, DR-08, DR-10 |
| `city.cumieira` (Cumieira) | DR-03, DR-04, DR-07, DR-08 (nominal) |
| `city.rodaviva` (Rodaviva) | DR-03, DR-04, DR-05, DR-06 (fornece), DR-07, DR-08 |
| `city.geodalia` (Geodália) | DR-03 (fornece), DR-04, DR-07, DR-08 |
| `city.pedra-ata` (Pedra-Ata) | DR-03, DR-04, DR-07 (fornece), DR-08 (nominal) |

As dez cidades aparecem em pelo menos uma dependência como dependentes (V-44). A origem do combustível e do alimento é **desconhecida de propósito**; nenhuma resposta é dada aqui.

## 6. Limites de fronteira

- **Combustível e alimento:** origem desconhecida; não se afirma Aeltharyn, Marcas ou outro lugar.
- **Minério e metal:** só o que Brasalume e Veio-Fundo produzem para as Coroas; nenhum fornecimento a Aeltharyn, nem o contrário (contrato §3.4).
- **Água:** a água medida vale **dentro das Coroas**; efeito sobre o Lúmen em Aeltharyn: nenhum afirmado (contrato §4).
- **Argente e Descida:** `carga-e-cais` descreve só o cais de Barcaalta; o que há rio abaixo não é descrito e o rio não toca Aeltharyn.
- Torvelume, Espinha de Vidro, mina selada, Sete-Bigornas e ano 742 não aparecem como fato do reino 2.

## 7. Relógios e recursos

| Relógio | Relação material a verificar | Não presumir |
|---|---|---|
| CL-01 Combustível | consumo das fornalhas, turno do fogo (L-03), metal e peças que dependem dele | que a falta já parou a forja ou que se sabe a origem |
| CL-02 Comportas | água medida, registro de comportas, cidades a jusante | que todas as cidades dependem da nascente ou que a comporta cedeu |
| CL-03 Cais | capacidade do cais, registro de carga × taxas, cordoaria | que todo atraso de carga vem do cais |
| CL-04 Passagens | tarifa, Livro de Passagens, hospedagem | que viajante sem registro é contrabando |
| CL-05 Assento | arquivo, selo, hospedagem de delegações | que a falta de ordem do assento fecha o arquivo |
| CL-06 Peças | peças raras, oficinas, construtos de carga | que uma oficina específica é culpada |
| CL-07 Passos | mensagens, guias de passo, mantimentos de Altavento | que todos os passos fecham ao mesmo tempo |

Relógios motivam investigação; **não alteram nível por conta própria**. Mudança persistente exige nexo, exposição e efeito verificável, com valor anterior, novo valor, causa, fonte e data.

## 8. Como avaliar uma escassez

1. Definir o bem e o uso concreto. 2. Localizar o armazém, oficina, trecho ou fonte. 3. Distinguir evento, testemunho, rumor e hipótese. 4. Conferir a reserva utilizável (aqui `?`: gerar investigação, não número). 5. Comparar com a demanda e os consumidores. 6. Testar alternativa (outra cidade da seção 5, substituto, racionamento, arbitragem pela L-01). 7. Escolher só o efeito sustentado: atraso, custo, déficit parcial ou interrupção de serviço. 8. Mapear só os afetados por nexo. Se faltar dado, gerar **oportunidade de investigação** (conferir registro, cota, contrato) em vez de escassez quantificada.

## 9. Contrato de contexto para a IA

Recuperar apenas: o registro do recurso; o estado da localidade; as dependências diretas (seção 5); o gatilho e a evidência; quem sabe e quem tem autoridade; as alternativas confirmadas; os relógios ligados e seus limites. Não enviar o catálogo inteiro. A IA propõe hipóteses e consequências narrativas; mudança persistente em nível, reserva ou obrigação exige regra e registro.

## 10. Conferências

- 12 recursos; 84 linhas; 29 localidades (10 cidades e 19 assentamentos); 0 localidade fora do registro mestre; 0 campo vazio no CSV.
- 28 níveis provisórios de produção; 0 nível 5; 0 nível 0; 0 reserva com nível; 0 número de quantidade, volume ou preço; 0 soma.
- 11 dependências; 10 cidades com ao menos uma; 0 rota ou tempo novo; 0 NPC, cidade, facção ou assentamento novo.
- Termos de fronteira conferidos: **minério**, **Lúmen** e **Argente** só como limite (seção 6); sem uso de carvão, Torvelume, Espinha de Vidro, mina selada, Sete-Bigornas ou ano 742.
- Nenhuma raça cidadã, vilã ou leal por natureza.

## 11. Decisão pendente

**D-43:** aprovar os níveis provisórios (regra da seção 3.2) como estimativa de planejamento, não como economia calibrada. Opções: **A.** aprovar (recomendada); **B.** o autor define os níveis; **C.** deixar tudo `?`. **Recomendação: A.**

**Este documento não declara a economia quantitativamente calibrada.**
