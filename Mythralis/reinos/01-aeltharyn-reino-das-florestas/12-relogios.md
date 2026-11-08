# 12 — Relógios: gatilhos, trava, custo e inação (M4.1)

**Fonte:** doc 01 §13, §25.3, §26, §35.3; doc 06 §7, §8, §12; doc 03 e doc 04. Dados em `12-relogios.csv`. "O que move" e "ao completar" são o texto do doc 01 §26; "o que trava", "quem paga" e "efeito da inação" são **leitura editorial** (ASSUMINDO), derivada só do que o texto já diz (o oposto do gatilho, as alternativas do doc 06 §12 e as cadeias da §25.3). Nada novo foi criado como fato do mundo.

## 1. Regras comuns

- Nenhum relógio avança por calendário. Todo avanço exige causa observável, nexo e localidade afetada (doc 06 §7). A estação só cria condição (doc 01 §35.3).
- **Inação:** o mundo não pune o jogador automaticamente (doc 06 §8). Ignorar uma oportunidade só avança um relógio quando já existe o gatilho observável na cena (ex.: reparo adiado com cheia em curso). Sem gatilho, nada muda.
- Um avanço de segmento muda a **escala** da situação, não escolhe a consequência (doc 06 §7). Máximo de 1 segmento por causa; uma cena não avança todos os relógios.

## 2. Custo e consequência por segmento (vale para os 7)

| Segmento | Escala | Custo para quem age | O que o mundo mostra |
|---|---|---|---|
| 1–2 | sinais locais | baixo: tempo e atenção | pistas, rumores, relatos isolados |
| 3–4 | pressão sobre uma cadeia | médio: verba, patrulha, favor político | a cadeia causal da §25.3 relevante fica visível; NPCs envolvidos mudam de agenda |
| 5 | decisão pública exigida | alto: custo político ou material real em qualquer escolha | conselho, arquivo ou autoridade local precisa decidir; nenhuma saída é grátis |
| 6 | efeito ao completar | consequência irreversível dentro do escopo escrito | o efeito da coluna "ao completar"; vira mudança de estado, não solução da trama |

ASSUMINDO: a escala por faixa é regra editorial de uso; os 6 segmentos vêm do doc 01 §26 (estados `n/6`).

## 3. Os sete relógios

### `clock.heart-trees` — Doença das árvores-coração (2/6)
| Campo | Texto |
|---|---|
| O que move | semanas sem coleta de amostras; corte próximo a raízes; relatórios ocultos |
| O que trava (ASSUMINDO) | coleta e entrega de amostras; relatórios abertos ao arquivo; cortes fiscalizados pelos Vigias |
| Quem paga (ASSUMINDO) | Vigias do Primeiro Verde (Saelith) e Vael-Tir arcam com patrulha e fechamento de trilhas; Arquivistas recebem relatórios |
| Ao completar | uma árvore perde uma função ecológica importante; a causa ainda precisa ser descoberta |
| Efeito da inação (ASSUMINDO) | sem amostras e com cortes continuando, avança; sem corte nem ocultação novos, a inação sozinha não avança |
| Entidades | `npc.aerendyl-vaelorian`, `npc.saelith-arco-de-chuva`, `npc.mirel-caule-vivo`, `faction.vigias-primeiro-verde`, `faction.arquivistas-seiva`, `city.vael-tir`, `city.elarion` |
| Fonte | doc 01 §13 (Mistério A), §26, §35.3 |

### `clock.dikes` — Dívida dos diques (3/6)
| Campo | Texto |
|---|---|
| O que move | cheias, adiamento de reparos e disputa por verbas |
| O que trava (ASSUMINDO) | reparo financiado e feito antes das cheias; prova de quem autorizou o material inferior; prioridade de verba acordada |
| Quem paga (ASSUMINDO) | Nymvar, Junco Alto e Três Barcas arcam com obras; Arven sofre na cadeia de sementes; a Coroa teme precedente fiscal |
| Ao completar | um bairro de Nymvar sofre inundação grave e a crise chega ao conselho |
| Efeito da inação (ASSUMINDO) | reparo adiado mais uma cheia avança; sem cheia e sem adiamento novo, a inação não avança |
| Entidades | `city.nymvar`, `city.arven`, `settlement.junco-alto`, `settlement.tres-barcas`, `npc.elsin-varzea-clara`, `npc.beren-tres-canais`, `npc.edris-do-sul`, `npc.maereth-vaelorian` |
| Fonte | doc 01 §13 (Conflito F), §25.3, §26; doc 04 caso A; doc 06 §12 |

### `clock.resin-smuggling` — Contrabando de resina (2/6)
| Campo | Texto |
|---|---|
| O que move | licenças falsas; patrulhas desviadas; demanda elevada |
| O que trava (ASSUMINDO) | cargas, ferramentas, pagamentos e testemunhos que sustentam fiscalização; patrulha concentrada na área |
| Quem paga (ASSUMINDO) | Vigias e Copa-Funda gastam patrulha; Companhia do Âmbar e Liga das Rotas ficam sob suspeita sem prova |
| Ao completar | surge uma rede de extração organizada; não prova que toda a Liga das Rotas esteja envolvida |
| Efeito da inação (ASSUMINDO) | patrulha desviada com demanda alta avança; sem licença falsa nem desvio novo, a inação não avança |
| Entidades | `faction.companhia-ambar`, `faction.liga-das-rotas`, `faction.vigias-primeiro-verde`, `settlement.copa-funda`, `npc.maelor-venn`, `npc.tavian-lume-fino`, `npc.arieth-tres-mudas` |
| Fonte | doc 01 §13 (Conflito H), §26 |

### `clock.city-distrust` — Desconfiança entre cidades (1/6)
| Campo | Texto |
|---|---|
| O que move | tributos desiguais; rumores; socorro distribuído de forma injusta |
| O que trava (ASSUMINDO) | socorro repartido com critério público; auditoria dos tributos; mediação no conselho |
| Quem paga (ASSUMINDO) | cidades de várzea e a Coroa; o Conselho das Sete Copas absorve o desgaste |
| Ao completar | duas cidades suspendem temporariamente uma obrigação do Pacto das Dez Cidades |
| Efeito da inação (ASSUMINDO) | rumor sem resposta e socorro desigual avançam; sem tributo novo nem socorro desigual, a inação não avança |
| Entidades | `city.nymvar`, `city.elarion`, `npc.edris-do-sul`, `npc.maereth-vaelorian`, `npc.aerendyl-vaelorian` |
| Fonte | doc 01 §13 (Conflito F), §26, §28; doc 04 |

### `clock.ghost-ship` — Navio sem tripulação (1/6)
| Campo | Texto |
|---|---|
| O que move | provas ignoradas; carga movimentada sem registro; testemunhas intimidadas |
| O que trava (ASSUMINDO) | provas preservadas e registradas; testemunhas protegidas; inspeção da carga |
| Quem paga (ASSUMINDO) | Thalanor (quarentena e alfândega), artesãos de Torvelume sem insumo, famílias de náufragos |
| Ao completar | evidência física é perdida ou ocorre um segundo incidente; não revela automaticamente o culpado |
| Efeito da inação (ASSUMINDO) | prova ignorada ou carga sem registro avança; sem esses gatilhos a inação não avança |
| Entidades | `city.thalanor`, `settlement.ponta-da-concha`, `settlement.farol-de-ambar`, `npc.syranna-das-mares`, `npc.yara-sal-de-mare`, `npc.terenna-luz-de-lastro`, `faction.liga-das-rotas` |
| Fonte | doc 01 §13 (Mistério C), §25.3, §26; doc 03 §4 |

### `clock.sealed-mine` — Mina selada de Torvelume (1/6)
| Campo | Texto |
|---|---|
| O que move | escavação clandestina; tremores; sabotagem |
| O que trava (ASSUMINDO) | avaliação técnica antes de abrir; medições; inspeção segura; resposta aos pedidos de inspeção |
| Quem paga (ASSUMINDO) | Torvelume (Dauren), Porta Selada (Rovan), Ferro-Folha, trabalhadores |
| Ao completar | um acesso se rompe e cria perigo real; a natureza do que existe abaixo permanece em aberto |
| Efeito da inação (ASSUMINDO) | escavação ou sabotagem avança; tremores sem gatilho humano não avançam sozinhos por calendário |
| Entidades | `city.torvelume`, `settlement.porta-selada`, `settlement.ferro-folha`, `npc.dauren-galho-de-ferro`, `npc.rovan-cinza-de-ferro`, `faction.guarda-espinha` |
| Fonte | doc 01 §13 (Mistério D), §26; doc 03 §11 |

### `clock.centaur-corridors` — Corredores centauros (2/6)
| Campo | Texto |
|---|---|
| O que move | novas cercas; colheita fora de época; patrulhas sem consulta |
| O que trava (ASSUMINDO) | consulta prévia aos centauros; desvio, ponte, horários ou indenização (cada um com custo) |
| Quem paga (ASSUMINDO) | centauros e Passo do Veado; Liga das Rotas, que quer a estrada aberta |
| Ao completar | uma rota sazonal é fechada e caravanas precisam desviar |
| Efeito da inação (ASSUMINDO) | cerca nova ou patrulha sem consulta avança; sem esses gatilhos a inação não avança |
| Entidades | `npc.oranna-passo-largo`, `npc.calar-passo-curvo`, `npc.eryndra-folha-verde`, `settlement.passo-do-veado`, `faction.liga-das-rotas`, `city.vael-tir` |
| Fonte | doc 01 §13 (Conflito G), §26; doc 03 §5.2 |

