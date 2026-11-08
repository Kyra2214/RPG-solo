# 13 — Reação do reino ao jogador (R3-5.4)

**Estado:** rascunho de R3-5.4 para aprovação do autor (D-90) · **Data:** 2026-11-06

**Etiqueta:** tudo é **[PROPOSTO]**. **Fonte:** doc 01 (parte 1), 04 (§3 a §9), 05 (§1 a §3), 06 e 08; `12-registro-mestre.csv`, `12-relacoes.csv`, `12-relogios.csv`; `plano/INTEGRACAO_FUTURA_RPG_SOLO.md` (MP-06, MP-07). Dados em `13-reputacao.csv` (19 linhas) e `13-canais.csv` (11 linhas). Molde: `reinos/02-.../13-reacao-ao-jogador.md`.

## 1. Regras e limites

- Descreve **a camada de mundo**: o que cada lugar e facção valoriza ou pune, quem reage e por que canal a notícia chega. **Números de fama, alcance, preço e sorteio de rumor pertencem ao motor** (MP-07); aqui só há linguagem qualitativa.
- **Reputação não substitui prova**: rumor justifica investigação, não condenação. O mesmo ato pode dar fama diferente em cada lugar e facção.
- A notícia viaja só pelos canais da seção 4. **Toda duração de rota é `desconhecida` (doc 07):** a notícia **não tem prazo definido**.
- A IA narra a distorção do rumor, nunca a transforma em fato. Nenhuma reação exige a presença do jogador e nenhuma é punição automática.
- **Nada aqui toca Aeltharyn, as Coroas, as Marcas das Feras ou Brumafunda**, nem diz como a fama chegaria a eles. Não há cidade-mãe nem jurisdição fora do reino (D-66).
- Mapeamento de ato para reação é leitura editorial (ASSUMINDO), derivada só de objetivos, leis e justiça já escritos; nenhuma facção, ofício ou NPC novo.

## 2. Fama e reputação por lugar e facção (R3-5.4a)

Disposição é **por entidade**, não global. Ver `13-reputacao.csv` (prefixo `rep.`).

| Entidade | Ganha disposição | Perde disposição | Quem reage | Canais |
|---|---|---|---|---|
| `city.morroveu` | honrar a Lei da Provação (inscrição registrada, desafiante não recusado); respeitar o Conselho e os pactos dos outros lugares; hospedar quem vem desafiar | recusar ou esconder uma inscrição; exigir do lugar o que o pacto não prevê; forçar a Guarda a escolher entre rei e cidade | `npc.morvan-voz-baixa`, `npc.orsk-cicatriz-de-bando`, `npc.cerrane-heranca-firme`, `faction.guarda-do-rei`, `faction.defensores-da-paz`, `faction.memoria-das-provacoes` | Conselho dos Pactuários; Arquivo dos Reis que Passaram; Escada das Vozes Baixas |
| `city.lamaluz` | conhecer e respeitar os canais; ajudar as lamparinas e os pilotos; honrar o pacto das famílias | guiar quem parte sem passar pelo pacto; quebrar passarela ou lamparina; tratar o pedido de partida só como traição ou só como direito | `npc.tazk-pedra-ao-sol`, `npc.nuvik-canal-torto`, `npc.lorra-emprestimo-justo`, `faction.pilotos-dos-canais`, `faction.guardioes-das-lamparinas` | Casa dos Pilotos; Cais das Lamparinas; Pactuários dos Canais |
| `city.fozbreu` | declarar a carga e o visitante; honrar a barra e o papel que o pacto reconhece; ajudar os pilotos da barra | passar carga ou visitante sem registro; esconder fardo; contornar a Casa da Barra | `npc.rusk-rede-da-barra`, `npc.ssavi-lodo-firme`, `faction.defensores-da-paz`, `faction.pilotos-dos-canais` | Casa da Barra; Torre das Marés; Armazém dos Fardos Sem Dono |
| `city.beira-sal` | fazer acertos no Pátio dos Acertos com testemunha; pagar o combinado ao guia; respeitar os poços sinalizados | quebrar acerto oral; sujar ou esconder poço; desrespeitar sentinela de sal | `npc.vurga-sede-vencida`, `npc.teshi-no-de-poco`, `npc.brisk-poco-cheio`, `faction.guias-de-sal`, `faction.casa-dos-pocos` | Pátio dos Acertos; Casa dos Guias de Sal; Feira da Beira |
| `city.selovale` | registrar cláusula no Arquivo dos Pactos Velhos; ler o texto sem prejulgar; respeitar os selos | forjar ou subtrair cópia; invocar cláusula em voz alta para surpreender; romper selo | `npc.yeshe-forma-emprestada`, `npc.garrun-toca-funda`, `faction.memoria-das-provacoes` | Arquivo dos Pactos Velhos; Casa das Cláusulas; Conselho dos Selos |
| `city.borda-mansa` | registrar quem chega sem fechar o abrigo; ajudar a vigia e as fogueiras de sinal; respeitar a Lei do Abrigo | passar sem registro; apagar fogueira de sinal; usar o abrigo para encobrir quem não deve | `npc.krell-eco-da-vigia`, `npc.szia-fio-aberto`, `faction.vigias-da-orla` | Torre das Fogueiras; Pátio dos Sem Guia; Pactuários da Vigia |
| `city.fio-longo` | respeitar o repouso e o pacto de ninguém atacado dormindo; ouvir o hóspede e os parentes; usar a hospedagem sem encobrir | atacar quem dorme; forçar hóspede a ficar ou a partir; entrar no quarto fechado | `npc.ondra-fio-de-sono`, `npc.rhek-relato-manso`, `faction.casa-dos-fios` | Casa dos Fios; Salão das Camas Longas; Tear Grande |
| `city.candeia-morta` | respeitar os mortos e a vigília; honrar os turnos; dizer a verdade na Casa das Despedidas | perturbar os mortos; deixar turno sem cobertura; mexer no Muro dos Nomes | `npc.dhun-contorno-quieto`, `npc.ilkar-turno-aberto`, `faction.irmandade-das-despedidas`, `faction.turnos-da-vigilia` | Casa das Despedidas; Ala dos Turnos; Praça das Candeias |
| `faction.guarda-do-rei` | servir ao rei e à guarda sem trair a cidade; respeitar a disciplina e os turnos | forçar a guarda a escolher entre rei e cidade; atacar a Corte | `npc.orsk-cicatriz-de-bando` | Corte; turnos da guarda |
| `faction.defensores-da-paz` | sustentar a paz que o rei trouxe; votar com o Conselho; dizer o que a paz custou a cada lugar | chamar o rei a perder em público sem motivo; pressionar o lugar além do pacto | `npc.rusk-rede-da-barra` | voto no Conselho dos Pactuários |
| `faction.memoria-das-provacoes` | lembrar a regra de que o rei deve ser vencido; inscrever desafiantes; ler os textos antigos com cuidado | esconder inscrição; calar a regra; forjar cópia | `npc.yeshe-forma-emprestada` | Arquivo dos Reis que Passaram; Arquivo dos Pactos Velhos |
| `faction.pilotos-dos-canais` | manter os canais navegáveis; guiar quem pede segundo o pacto; ajudar a barra | quebrar passarela; prender quem guia por vingança; fechar canal | `npc.tazk-pedra-ao-sol` | Casa dos Pilotos; lamparinas |
| `faction.guardioes-das-lamparinas` | manter as famílias de pacto em Lamaluz; vigiar o pacto | tirar famílias do pacto sem passar pelos Pactuários; acusar sem ato | `npc.nuvik-canal-torto` | vigia do pacto; Pactuários dos Canais |
| `faction.guias-de-sal` | manter a Rota do Sal e os acertos honestos; testemunhar o que viu | cobrar acerto sem prova; guiar para fora do pacto da Caravana | `npc.vurga-sede-vencida` | Casa dos Guias de Sal; Pátio dos Acertos |
| `faction.casa-dos-pocos` | guardar os poços sinalizados; manter o Pátio dos Acertos neutro | tomar partido no Pátio; esconder falha de poço | `npc.teshi-no-de-poco` | sinalização; sentinelas de sal |
| `faction.vigias-da-orla` | abrigar quem chega e registrar; manter as fogueiras de sinal | fechar o abrigo; deixar passar sem registro | `npc.krell-eco-da-vigia` | fogueiras de sinal; patrulha |
| `faction.casa-dos-fios` | manter o repouso; ouvir hóspede e parentes | retê-lo contra a vontade; revelar o que o hóspede conta | `npc.ondra-fio-de-sono` | hospedagem; tecelagem |
| `faction.irmandade-das-despedidas` | manter os mortos em descanso; conduzir as despedidas; guardar o Muro dos Nomes | perturbar os mortos; calar a divisão sobre o rei | `npc.dhun-contorno-quieto` | Casa das Despedidas; Muro dos Nomes |
| `faction.turnos-da-vigilia` | manter os turnos sem fechar; proteger a cidade de dia | deixar turno sem cobertura; tirar vigia do portão | `npc.ilkar-turno-aberto` | vigias; portões |

## 3. Procurado, favor e reparação (R3-5.4b)

### 3.1 Estados de "procurado" (ASSUMINDO, ordinais)

| Estado | Condição | Limite escrito |
|---|---|---|
| Rumor | alguém diz que o jogador fez algo | justifica investigação, não condenação |
| Investigação | o lugar ou a facção aponta o ato e a regra | testemunho, registro e inferência são provas diferentes (leitura editorial) |
| Procurado num lugar | os Pactuários do lugar apontam quebra do pacto | quem julga a quebra do pacto de um lugar são os Pactuários do lugar (doc 04 §6) |
| Disputa entre lugares | dois lugares reivindicam o caso | o Conselho dos Pactuários julga o conflito entre lugares (doc 04 §6); o Conselho não depõe o rei |
| Crime contra o rei ou a coroa | ofensa à Corte | a Corte julga (doc 04 §6); o resultado não está decidido aqui |
| Resolvido | decisão registrada | a pena é `desconhecida` (L-09); não se presume pena máxima |

### 3.2 Ato, regra e autoridade

| Ato | Regra | Autoridade inicial | Desfecho possível |
|---|---|---|---|
| recusar ou esconder inscrição de desafiante | Lei da Provação (L-01) | Conselho; Campo das Provações | o Conselho decide; se a necromancia vale na Provação segue `desconhecido` |
| romper ou exigir além do pacto de um lugar | Lei do Pacto Vivo (L-02) | Pactuários do lugar | o pacto não é rompido por rei nem por Conselho; o que ocorre se o guardião some é `desconhecido` |
| passar sem registro pela orla ou usar o abrigo para encobrir | Lei do Abrigo (L-03) | Pactuários da Vigia | como abrigar quem não tem guia nem registro segue `desconhecido` |
| passar carga ou visitante sem papel pela barra | Lei da Barra (L-04) | Pactuários da Foz | o que é "papel que o pacto reconheça" segue `desconhecido` |
| quebrar acerto ou ignorar poço | Lei dos Poços (L-05) | Pactuários dos Poços | o valor de pacto oral sem prova segue `desconhecido` |
| atacar quem dorme ou forçar hóspede | Lei do Repouso (L-06) | Casa dos Fios | quem decide se hóspede fica ou parte segue `desconhecido` |
| perturbar os mortos | Lei dos Mortos (L-07) | Pactuários das Despedidas | se a necromancia a viola segue aberto; não se decide aqui |
| forjar, subtrair ou invocar cláusula sem registro | Lei das Cláusulas (L-08) | Conselho dos Selos | parecer; quem decide qual versão vale segue `desconhecido` |
| guiar quem parte de Lamaluz | Lei da Partida (L-09) | Pactuários dos Canais | acusação de quebra do pacto; pena e alcance fora de Lamaluz `desconhecidos` |

### 3.3 Favor

- **Pessoal:** NPC com vínculo (`12-relacoes.csv`) pode pedir ou conceder; só sabe o que consta em `12-conhecimento.csv`. Afeto: 0 relação documentada (D-85); nenhum favor de afeto é inventado.
- **Institucional:** facção cobra ou concede conforme `13-reputacao.csv`; não é troca automática.
- **Dívida de pacto:** entre lugares, a contribuição do pacto ao tributo (doc 04 §7) é a moeda de favor; não há preço nem quantia aqui.
- **Hospitalidade:** o Pacto do Repouso e o Pacto da Orla obrigam a abrigar; não obrigam quem não os jurou (ASSUMINDO).

### 3.4 Reparação

O Véu **não escreve** formas de reparação. Leitura editorial (ASSUMINDO): refazer o dano, compensar quem perdeu e restringir o acesso ao lugar, sempre decididas pelos Pactuários ou pelo Conselho competente. Punição como pena própria é lacuna (L-09, `desconhecida`). Reparar não apaga o rumor: a reputação muda por canal e por lugar, no ritmo do motor.

## 4. Intermediários (R3-5.4c)

Quem leva a notícia, o pedido ou a decisão. Ver `13-canais.csv` (prefixo `canal.`).

| Canal | Função | O que o cânone diz | Limite | Rota |
|---|---|---|---|---|
| `canal.veu-conselho` — Conselho dos Pactuários | levar o pedido ou a decisão entre os lugares e a Corte | vota questões comuns, chama o rei a responder, registra desafiantes e julga o conflito entre dois lugares; preside Cerrane, sem voto | não depõe o rei (só a Provação); o resultado de um voto não está decidido aqui | `route.conselho-morroveu` |
| `canal.veu-arquivo-reis` — Arquivo dos Reis que Passaram | registrar quem se inscreve para a Provação | o Conselho registra o nome do desafiante no Arquivo; o rei não pode recusar desafiante inscrito | registra o nome, não o resultado; por que a lista não anda é `desconhecido` | nenhuma escrita |
| `canal.veu-pilotos` — Pilotos dos Canais | levar notícia entre Lamaluz e Fozbreu pelos canais | os Pilotos dos Canais atuam em Lamaluz e em Fozbreu (F-04); guiam barcas por canais incertos | a duração de qualquer travessia é `desconhecida` (doc 07); nada vale como prova | `route.lamaluz-fozbreu` |
| `canal.veu-barra` — Casa da Barra e Torre das Marés | registrar o que desce o rio e o que entra pelo porto | a Torre das Marés e a Casa da Barra veem o que desce; o Armazém dos Fardos Sem Dono guarda a carga sem papel | prova o que foi visto, não o conteúdo; o que a Descida traz continua `desconhecido` | `route.fozbreu-argente` |
| `canal.veu-rota-do-sal` — Rota do Sal e Pátio dos Acertos | levar notícia pelas caravanas e registrar acertos | guias conduzem caravanas pelo Ermo; acertos são feitos no Pátio dos Acertos | só do lado do Véu; nada sobre as Marcas das Feras; o valor de pacto oral sem prova é `desconhecido` | `route.beira-sal-ermo` |
| `canal.veu-selos` — Arquivo dos Pactos Velhos e Conselho dos Selos | levar parecer e cópia de selo entre Selovale e Morrovéu | o Conselho dos Selos interpreta cláusulas antigas e dá parecer; o Arquivo guarda as cópias | não executa o parecer; ter a cópia não prova o conteúdo; qual versão vale é `desconhecido` | `route.morroveu-selovale` |
| `canal.veu-fogueiras` — Fogueiras de sinal da Torre | levar aviso em Borda-Mansa e ao posto | a Torre das Fogueiras comanda os sinais; a vigia só vê o que a névoa deixa ver | alcance e duração `desconhecidos`; sem ligação escrita com outros lugares além do tributo (doc 04 §7) | nenhuma escrita |
| `canal.veu-hospedes` — Hospedagem da Casa dos Fios | levar notícia por hóspedes e parentes | a Casa hospeda; parentes cobram a volta de hóspedes; ninguém é obrigado a ficar nem a partir | a Casa guarda o que o hóspede conta como confiança; a origem dos hóspedes é `desconhecida` (DR-07) | nenhuma escrita |
| `canal.veu-despedidas` — Casa das Despedidas e Muro dos Nomes | registrar nomes e levar a notícia dos mortos | a Irmandade conduz as despedidas e guarda o Muro dos Nomes | o que o Muro mostra é registro, não prova do que aconteceu; um nome que mudou de lugar é rumor | nenhuma escrita |
| `canal.veu-tributo` — Tributo em pactos | levar notícia entre os lugares junto do que cada pacto entrega à Corte | cada lugar entrega o que o seu pacto produz, sem quantia; o rei não exige o que o pacto não prevê | sem prazo e sem rota escrita além das do doc 07; leitura como canal é ASSUMINDO | nenhuma escrita |
| `canal.veu-rumor` — Rumor local | espalhar uma versão de um ato dentro do lugar | cada lugar tem rumores não confirmados (doc 01 parte 1 §2) | rumor não vira fato e justifica investigação, não condenação; a IA narra a distorção sem transformá-la em fato | nenhuma escrita |

**Lacunas explícitas:** não há caçador de recompensa, cartaz ou mensageiro de ofício escrito no Véu; nenhum foi criado. Borda-Mansa, Fio-Longo e Candeia-Morta não têm rota escrita no doc 07; a notícia só chega a eles pelo tributo (ASSUMINDO) e por quem chega ou parte.

## 5. Ligação com o plano do jogo

- **MP-07 (fama e rumores):** a disposição é por entidade, qualitativa, e vem de `13-reputacao.csv`; os canais de `13-canais.csv` e as rotas do doc 07 dão os caminhos; números, alcance e duração são do motor, não daqui.
- **MP-06 (caçadores):** o Véu não escreve caçador de recompensa; se o motor oferecer um, só pode agir pelos canais acima e sem criar facção ou ofício.
- Gatilhos de relógio (`12-relogios.csv`) e eventos (`12-eventos.csv`) podem mudar a disposição só por causa observável; nenhuma reação é automática.

## 6. Conferências

| Verificação | Resultado |
|---|---|
| Linhas de reputação | 19 (8 lugares e 11 facções) |
| Canais | 11 |
| ID fora do registro mestre ou NPC falecido | 0 |
| Rota fora do doc 07 | 0 |
| Campo vazio | 0 |
| Fronteira (Aeltharyn, Coroas, Marcas das Feras, Brumafunda) | 0 |
| Número de fama, preço, prazo ou distância | 0 |

## 7. Decisão pendente

**D-90:** aprovar a reação, os estados de procurado e os 11 canais. Opções: **A.** aprovar; **B.** trocar ou tirar uma reação ou canal (diga qual). **Recomendação: A.**
