# Azeroth — registro operacional de rotas (doc 07)

**Estado:** R5-3.1, D-153 aceita pela continuação · **Data:** 2026-11-07 · **Etiqueta:** [PROPOSTO]; só organiza a evidência dos docs 01, 03, 05 e 10 (D-142 a D-152). Mapa esquemático: `07-mapa-esquematico.md`.
**Regra central (herdada dos reinos 1 a 4):** uma conexão não listada aqui não é impossível; significa que ainda não tem percurso nem tempo validados. **Nenhuma rota, duração ou distância foi inventada.** Os docs do reino 5 não trazem "curta" nem "longa" para nenhum percurso; **toda duração é `desconhecida`** e a decisão é do autor (D-153).
**Fronteira:** o contrato (§6 itens 5, 7 e 14) proíbe rota, duração, distância ou passagem nova para Aeltharyn, Véu, Coroas e Vale, além de As Costas (por mar) e do contato por costa já em M-04; a seção 4 só repete os pontos de contato permitidos, sem rota. Escala qualitativa (D-63).

## 1. Como usar

Estados:
- **Documentada sem duração:** o texto liga dois lugares, ou um lugar a um fluxo ou rio, mas não diz quanto dura nem por onde vai.
- **Sugerida por dependência:** a ligação decorre de vínculo político (lugar-mãe), não de percurso descrito.
- **Pendente:** faltam evidências para dizer que existe percurso.

Regras de uso: não converter "perto" ou "longe" em dias; não concluir que uma ligação é segura, pública ou controlada por uma coroa só porque aparece aqui; ida e volta não precisam ter o mesmo tempo, mas a diferença exige causa plausível (cheia, comporta fechada, cerração, noite sem luz, torre derrubada). **Todos os dez lugares são fixos** (doc 01); "lugar-mãe" é vínculo político, não posição espacial (doc 03 §1).

## 2. Ligações entre lugares

| ID editorial | Ligação | Meio ou condição descrita | Duração | Estado | Restrição de uso |
|---|---|---|---|---|---|
| `route.campoalvo-vaudouro` | Campoalvo ↔ Vaudouro | os dois ficam no Rio Trigueiro: Campoalvo num ponto largo, Vaudouro na junção com o Corre-Largo; Campoalvo é lugar-mãe (doc 01 §2.1 e §2.2; doc 03 §1) | desconhecida | Documentada sem duração | elo de rio e de apoio; sem curso, sem foz posicionada e sem relação com o Argente |
| `route.campoalvo-torrefronte` | Campoalvo ↔ Torrefronte | Campoalvo é lugar-mãe; a guarnição depende de suprimento de dentro do reino (doc 01 §2.3; doc 03 §1) | desconhecida | Sugerida por dependência | só vínculo de apoio; o doc não descreve trilha nem estrada |
| `route.alvamar-enseada-vigia` | Alvamar ↔ Enseada-Vigia | Alvamar é lugar-mãe; as duas ficam na costa meridional (doc 01 §2.4 e §2.5; doc 03 §1) | desconhecida | Sugerida por dependência | só vínculo de apoio; a posição de uma em relação à outra não é afirmada |
| `route.alvamar-concharia` | Alvamar ↔ Concharia | Alvamar é lugar-mãe; Concharia fica numa enseada da costa sul (doc 01 §2.6; doc 03 §1) | desconhecida | Sugerida por dependência | só vínculo de apoio; o canal da enseada é do santuário e do estaleiro, não uma rota |
| `route.torreclara-pouso-das-brisas` | Torreclara ↔ Pouso-das-Brisas | uma linha de torres liga Torreclara ao litoral e Pouso-das-Brisas fica nela; Torreclara é lugar-mãe (doc 01 §2.7 e §2.9; doc 03 §1) | desconhecida | Documentada sem duração | elo de sinais e de apoio; a linha de torres não é uma estrada; incêndio ou tempestade a derrubam |
| `route.torreclara-estrelado` | Torreclara ↔ Estrelado | Torreclara é lugar-mãe; Estrelado guarda o calendário de rota e depende de a Coroa do Vento pagar (doc 01 §2.8; doc 03 §1) | desconhecida | Sugerida por dependência | só vínculo de apoio; Estrelado fica longe de rio e de cidade, sem acesso descrito |
| `route.meiovau-campoalvo` | Meiovau ↔ Campoalvo | a Mesa arbitra o que o Trigal não acerta em casa; Meiovau fica onde as terras das três coroas se tocam (doc 03 §3 e §4) | desconhecida | Documentada sem duração | ligação política; nenhuma coroa possui o caminho |
| `route.meiovau-alvamar` | Meiovau ↔ Alvamar | a Mesa arbitra o que o Cais não acerta em casa (doc 03 §3 e §4) | desconhecida | Documentada sem duração | ligação política; nenhuma coroa possui o caminho |
| `route.meiovau-torreclara` | Meiovau ↔ Torreclara | a Mesa arbitra o que o Vento não acerta em casa (doc 03 §3 e §4) | desconhecida | Documentada sem duração | ligação política; nenhuma coroa possui o caminho |

### 2.1 Elos com relevo, rota de mar e linha de sinais (só do lado de cá)

| ID editorial | Ligação | Meio ou condição descrita | Duração | Estado | Restrição de uso |
|---|---|---|---|---|---|
| `route.vaudouro-costa-sul` | Vaudouro ↔ costa sul (pelo Corre-Largo) | o Corre-Largo desce para a costa sul; se a Casa dos Barqueiros fecha as comportas, o interior perde o caminho do mar (doc 01 §1 e §2.2) | desconhecida | Documentada sem duração | **nenhum lugar de chegada nomeado**; o Corre-Largo não tem foz posicionada; sem relação com o Argente |
| `route.pouso-das-brisas-litoral` | Pouso-das-Brisas ↔ litoral (linha de torres) | a linha de torres liga Torreclara ao litoral (doc 01 §2.9) | desconhecida | Documentada sem duração | **nenhum lugar de litoral nomeado**; sem rota nova |
| `route.enseada-vigia-costas` | Enseada-Vigia ↔ rota das Costas | chega aqui o fluxo As Costas; Farol do Sul, Pedra da Barra e Casa dos Pilotos veem quem chega (doc 05 §3) | desconhecida | Documentada sem duração | **só o lado de cá**; o que a rota troca, quando começou e quanto dura a travessia são `desconhecidos` |
| `route.alvamar-costas` | Alvamar ↔ rota das Costas | chega à Doca dos Povos do Mar (doc 05 §3) | desconhecida | Documentada sem duração | **só o lado de cá**; navios que descarregam fora do molhe são rumor, não ligação (MA-04) |
| `route.meiovau-fugas` | Meiovau ↔ chegados do Véu | Meiovau recebe quem chega do Véu; o Costume da Porta rege a acolhida (doc 05 §4; doc 04 §7) | desconhecida | Documentada sem duração | **só a chegada**; como chegam, por onde e quanto dura é `desconhecido`; não se afirma que passem pelo Livro dos Chegados |

### 2.2 Pares sem ligação descrita

Todas as demais ligações entre os dez lugares (por exemplo Campoalvo ↔ Alvamar, Vaudouro ↔ Alvamar, Concharia ↔ Enseada-Vigia, Estrelado ↔ Pouso-das-Brisas, Torrefronte ↔ Meiovau) **não têm evidência**: estado **Pendente**. Nenhuma foi criada. Vaudouro × Alvamar aparece como **tensão** (quem taxa o que passa de um rio ao outro), não como rota nova. Se o autor quiser um mapa completo, a decisão é D-153.

## 3. Acessos dos lugares

Os dez lugares são a escala do reino (D-141). O texto diz o seguinte de acesso, nada além.

| Lugar | Como se chega ou o que protege o acesso | Duração | Estado | Restrição de uso |
|---|---|---|---|---|
| `city.campoalvo` | num ponto largo do Rio Trigueiro, no alto de um outeiro raso que serve de aterro; muralha baixa e canais de drenagem (doc 01 §2.1) | desconhecida | Documentada sem duração | não diz por onde se sobe nem como se chega do interior |
| `city.vaudouro` | na junção do Trigueiro com o Corre-Largo, onde as barcaças trocam de rio; sem muralha, só comportas e vigias de canal (doc 01 §2.2) | desconhecida | Documentada sem duração | o acesso é pelas comportas, que a Casa dos Barqueiros controla; sem aviso às duas coroas elas não se fecham (L-02) |
| `city.torrefronte` | no limite do reino, voltada ao lado de Aeltharyn e do Véu; torre alta e muro de terra (doc 01 §2.3) | desconhecida | Documentada sem duração | **sem rota, sem passagem e sem duração** (contrato A8, V1) |
| `city.alvamar` | numa baía abrigada da costa meridional, com molhe e corrente de ferro na boca (doc 01 §2.4) | desconhecida | Documentada sem duração | o navio entra pelo molhe (L-04); sem posição em relação à foz do Argente (P-3) |
| `city.enseada-vigia` | onde o Mar Austral e o Mar de Âmbar se encontram, na ponta mais voltada à rota que sobe a costa; faróis e uma barra difícil (doc 01 §2.5) | desconhecida | Documentada sem duração | a barra se avisa a quem chega (L-05); uma noite sem luz é perigo |
| `city.concharia` | numa enseada protegida da costa sul, cercada de recifes; o Canal Estreito é a entrada (doc 01 §2.6) | desconhecida | Documentada sem duração | os recifes prendem quem não conhece o canal; o canal é objeto de disputa (doc 03) |
| `city.torreclara` | numa planície aberta e plana onde as torres se enxergam entre si (doc 01 §2.7) | desconhecida | Documentada sem duração | vive do alcance da vista; cerração longa ou torre derrubada a deixam cega |
| `city.estrelado` | numa planície alta e seca do interior, longe de rio e de cidade (doc 01 §2.8) | desconhecida | Documentada sem duração | nenhuma defesa própria; sem acesso descrito |
| `city.pouso-das-brisas` | na planície entre o interior e a costa, na linha de torres (doc 01 §2.9) | desconhecida | Documentada sem duração | sem rota nova nem duração; torres de madeira e vigias |
| `city.meiovau` | na planície central, onde as terras das três coroas se tocam; sem muralha (doc 01 §2.10) | desconhecida | Documentada sem duração | nenhuma coroa a possui; protege-se por ser útil às três |

## 4. Ligações com Aeltharyn e outros reinos (só o já permitido)

**Nenhuma rota validada liga Azeroth a Aeltharyn, ao Véu, às Coroas ou ao Vale.** As Costas e as Fugas do Véu são fluxos com ponta em Azeroth (§2.1), não rotas.

| Ligação | Evidência | Estado | Limite |
|---|---|---|---|
| Azeroth ↔ Aeltharyn por mar (As Costas) | M-05 §5; contrato §5; doc 05 §3 | **fluxo permitido, não é rota** | só o lado de cá; sem duração, causa ou número; nada sobre Thalanor, o Mistério C ou as tripulações |
| Azeroth ↔ Véu (As Fugas do Véu) | M-05 §5; doc 05 §4 | **fluxo permitido, não é rota** | só a chegada em Meiovau; nenhum motivo, número ou começo; a fronteira e a foz do Argente não são afirmadas (P-3) |
| Torrefronte ↔ vizinhos (Aeltharyn e Véu) | M-04 (vizinhos plenos); doc 01 §2.3 | **contato permitido, sem rota** | sem passagem, duração ou distância; nada afirmado sobre o outro lado |
| Azeroth ↔ Coroas | nenhuma | **sem rota, sem tempo** | o elo é "nenhuma" (contrato §5) |
| Azeroth ↔ Vale | nenhuma | **sem rota, sem tempo** | o elo é "nenhuma" (contrato §5) |
| Qualquer par Azeroth–reino vizinho além dos acima | nenhuma | **sem rota, sem tempo** | nenhuma distância entre reinos (M-04 G-02); nenhuma passagem nova |

## 5. Conferências

- 9 ligações entre lugares (5 documentadas sem duração, 4 sugeridas por dependência) e 5 elos de fluxo, rio ou sinais (todos documentados sem duração); 10 acessos de lugares; 6 linhas de fronteira.
- Grafo das 9 ligações entre lugares **conexo** (os 10 lugares), conferido por script; Meiovau liga as três capitais.
- 0 duração, distância ou rota inventada; 0 "curta" ou "longa" atribuída; 0 rota Azeroth–vizinho; 0 passagem nova; 0 lugar de chegada inventado para o Corre-Largo e para a linha de torres.
- Todo ID `city.*` citado existe no `12-registro-mestre.csv` (conferido por script).
- Termos de fronteira (Aeltharyn, Véu, Coroas, Vale, Argente): só nas seções 2.1 e 4 e nas restrições, como "sem rota", "só o lado de cá" ou "contato permitido".
- Ainda faltam os **tempos de viagem**: decisão do autor (D-153).

## 6. Decisão pendente

**D-153:** os tempos de viagem e as categorias "curta" e "longa" não existem no cânone do reino 5. Opções: **A.** manter tudo `desconhecida` até o autor decidir (recomendada; critério "nenhuma rota ou tempo inventado" cumprido); **B.** o autor fornece "curta" ou "longa" para as 14 ligações (e quais pares entram); **C.** o autor autoriza propostas marcadas como ASSUMINDO. **Recomendação: A.**
