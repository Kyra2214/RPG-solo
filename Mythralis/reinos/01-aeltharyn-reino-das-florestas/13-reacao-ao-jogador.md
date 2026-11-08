# 13 — Reação do reino ao jogador (M4.4)

**Fonte:** doc 01 §5, §7, §9, §10, §13; doc 04 §4, §4.1, §7, §9, §10, §12; doc 06 §8; `plano/INTEGRACAO_FUTURA_RPG_SOLO.md` (MP-06, MP-07). Dados em `13-reputacao.csv` e `13-canais.csv`.

## 1. Regras e limites

- Este documento descreve **a camada de mundo**: o que cada lugar e facção valoriza ou pune, quem reage e por que canal a notícia chega. **Números de fama, `alcance`, preços e sorteio de rumor pertencem ao motor** (MP-07), não ao mundo; aqui só há linguagem qualitativa.
- **Reputação não substitui prova** (doc 01 §10, lei 8): rumor justifica investigação, não condenação. Mesmo ato pode dar fama diferente em cada cidade e facção (MP-07).
- A notícia viaja só pelos canais da seção 4 e pelos tempos de rota já documentados (doc 07). Onde a duração é desconhecida (`route.orvass-brumafunda`), a notícia **não tem prazo definido**; nada foi inventado.
- A IA narra a distorção do rumor, nunca a transforma em fato (MP-07). Nenhuma reação exige a presença do jogador e nenhuma é punição automática (doc 06 §8).
- Mapeamentos de ato para reação são leitura editorial dos textos (ASSUMINDO); marcado onde a fonte não diz.

## 2. Fama e reputação por cidade e facção (M4.4a)

Disposição é **por entidade**, não global. Colunas: o que ganha disposição, o que a perde, quem reage e por quais canais. Ver `13-reputacao.csv` (prefixo `rep.`).

| Entidade | Ganha disposição | Perde disposição | Quem reage | Canais |
|---|---|---|---|---|
| `city.elarion` | atos que preservam registros, documentos e a manutenção de pontes e bairros | ocultar relatórios, danificar ou desviar o Arquivo, ignorar dano estrutural conhecido | `npc.avariel-thorne`, `faction.arquivistas-seiva`, `faction.liga-das-rotas` | Arquivo das Seivas; corte; mensageiros |
| `city.nymvar` | ajudar em cheias e diques, cuidar de doentes, proteger sementes e água | contaminar a água, negociar sementes doentes, adiar reparo conhecido | `npc.elsin-varzea-clara`, `faction.irmandade-aguas`, `faction.liga-das-rotas` | Feira das Sementes; barqueiros; Hospital das Folhas Brancas |
| `city.thalanor` | resgate e socorro no mar, fiscalização que não trata todo comerciante como criminoso | contrabando de resina, carga sem registro, ocultar o destino de tripulações | `npc.syranna-das-mares`, `faction.liga-das-rotas`, `faction.companhia-ambar` | faroleiros; Mercado de Marés; capitães |
| `city.vael-tir` | proteger trilhas, consultar os centauros, rastrear caçadores ilegais | corte noturno ou acima da cota, caça ilegal, bloquear corredor sem consulta | `npc.eryndra-folha-verde`, `npc.oranna-passo-largo`, `faction.vigias-primeiro-verde` | guias; patrulheiros; Ninho dos Mapas |
| `city.orvass` | trazer remédios por via legal, respeitar o consentimento das comunidades micélioides | atribuir desaparecimentos a monstros sem prova, contaminar as redes subterrâneas | `npc.voz-muitas-raizes`, `faction.circulo-micelial`, `faction.irmandade-aguas` | barqueiros; Mercado das Lanternas Baixas |
| `city.caladwen` | método, fontes primárias e revisão aberta | vender cópias incompletas como completas, forjar autoria ou provas | `npc.selvaris-noctel`, `faction.arquivistas-seiva` | escribas; estudantes; mensageiros |
| `city.brumafunda` | buscar desaparecidos, exigir evidência antes de fechar trilhas | divulgar rotas frágeis a caçadores, provocar fenômenos, esconder rotas para monopólio | `npc.neralis-vento-manso`, `faction.rede-guias` | guias; Praça das Vozes |
| `city.arven` | proteger sementes, estoques e polinização, contratos transparentes | tributar sobre colheita que não houve, esconder conflito de interesse | `npc.tessa-folhinha`, `faction.liga-celeiros`, `faction.liga-das-rotas` | Feira da Primeira Colheita; caravanas |
| `city.silvaran` | honrar a hospitalidade, mediar disputas, proteger refugiados | usar o pacto de hospitalidade para esconder violência, entregar refugiados sem audiência | `npc.lyris-melodia-clara`, `faction.circulo-estacoes` | festivais; Casa da Hospitalidade; artistas |
| `city.torvelume` | apoiar defesa e obras civis, salários justos, investigação segura da mina | abrir a mina sem avaliação, negligenciar estruturas civis, ignorar o juramento de defesa | `npc.dauren-galho-de-ferro`, `faction.guarda-espinha` | patrulhas; Posto da Passagem |
| `faction.vigias-primeiro-verde` | proteger ecossistemas, ajudar a patrulha, entregar amostras | extração destrutiva, tratar toda restrição como obstáculo econômico | `npc.saelith-arco-de-chuva` | patrulhas; mapas de trilhas |
| `faction.liga-das-rotas` | manter estradas, rios e portos abertos, tarifas claras | fechar rotas sem consulta, tolerar contrabando | `npc.tavian-lume-fino` | contratos; crédito; informação comprada |
| `faction.arquivistas-seiva` | preservar fontes e testemunhos, cadeia de custódia | destruir ou forjar fontes, divulgar documentos que exponham pessoas vivas sem avaliar | `npc.lethariel-nascente-clara` | arquivos; escribas |
| `faction.circulo-estacoes` | mediar, proteger refugiados, manter ritos e pactos | transformar ritos em espetáculo vazio, quebrar hospitalidade | `npc.lyris-melodia-clara` | festas; redes de anfitriões |
| `faction.irmandade-aguas` | proteger rios, canais e diques; socorrer ribeirinhos | contaminar água, adiar reparo, sabotar diques | `npc.elsin-varzea-clara` | barqueiros |
| `faction.companhia-ambar` | comércio certificado e produtos de luxo; projetos úteis | extração ilegal comprovada por carga, ferramenta, pagamento ou testemunho (não por intuição) | `npc.maelor-venn` | contratos; relações com a corte |
| `faction.circulo-micelial` | consentimento, tempo para deliberar, proteger redes sem destruir habitats | coleta sem consentimento, tomar silêncio por concordância | `npc.voz-muitas-raizes` | assembleia |
| `faction.rede-guias` | travessias seguras, reputação de confiança, ajuda em resgates | abandonar viajantes, esconder rotas para monopólio | `npc.neralis-vento-manso` | guias |
| `faction.liga-celeiros` | proteger estoques, sementes e abastecimento em crises | especular com estoque, esconder reservas em crise | `npc.tessa-folhinha` | celeiros distribuídos |
| `faction.guarda-espinha` | defender a passagem noroeste, abrir ruínas só com preparação | abrir ruínas sem preparo, forçar a mina | `npc.dauren-galho-de-ferro` | patrulhas |

## 3. Procurado, favor e reparação (M4.4b)

### 3.1 Estados de "procurado" (ASSUMINDO, ordinais)

| Estado | Condição | Limite escrito |
|---|---|---|
| Rumor | alguém diz que o jogador fez algo | justifica investigação, não condenação (doc 01 §10, lei 8) |
| Investigação | acusação que indica o ato, a regra supostamente violada e a evidência | doc 04 §4.1; confissão sob ameaça não é prova independente |
| Procurado numa jurisdição | autoridade do local do incidente emite pedido | a Coroa não assume todo caso local; vale fora só por arbitragem ou pacto (doc 04 §4) |
| Disputa de jurisdição | duas autoridades reivindicam o caso | capturar alguém não decide quem julga (doc 04 §4) |
| Resolvido | decisão com reparação, compensação, restrição temporária, perda de cargo ou punição | a decisão deve distinguir os cinco (doc 04 §4.1); não se presume prisão ou pena máxima |

O acusado pode apresentar sua versão e contestar a prova. Autoridade com interesse financeiro, familiar ou político deve declarar o conflito (doc 04 §4.1).

### 3.2 Ato, regra e autoridade

| Ato | Regra | Autoridade inicial | Desfecho possível |
|---|---|---|---|
| contaminar fonte pública | Lei das Nascentes (crime grave) | autoridade local preserva o local; instância real prevista | depende de intenção e dano; prova do responsável é separada do dano |
| cortar ou ferir árvore-coração sem autorização | Lei das Árvores Ancestrais | local recolhe provas; representantes reais podem intervir | pena depende de intenção, dano e contexto |
| bloquear trilha ou corredor de modo permanente | Direito de Trilha | consulta aos povos afetados | refazer a consulta, desvio, indenização, horários (doc 01 §13 G) |
| negar abrigo a viajante em perigo, ou usar a hospitalidade para esconder crime | Pacto de Hospitalidade | anfitrião e autoridade local | o pacto não dá imunidade a criminosos |
| entregar refugiado a perseguidores sem audiência local | Proteção de Refugiados | autoridade local | regra disputada por autoridades que alegam segurança |
| interditar planta ou fungo sem compensar | Quarentena de Esporos | autoridade sanitária local; revisão regional rápida | compensação e abrigo obrigatórios se a interdição impedir o trabalho |
| disputa de dívida entre cidades sem registro | Registro de Dívidas | mediadores aceitos pelas duas | exige testemunhas ou registro |
| crime em rota, porto ou fronteira | doc 04 §4 | guarda ou magistrado do local do incidente | arbitragem entre autoridades envolvidas |
| subtrair ou forjar documento | doc 04 §4, §10 | instituição que guarda a fonte; cadeia de custódia | tribunal ou arbitragem; a posse não prova o conteúdo |

### 3.3 Favor

- **Pessoal:** NPC com vínculo (relações em `12-relacoes.csv`) pode pedir ou conceder; só sabe o que viveu ou ouviu (MP-07).
- **Institucional:** facção cobra ou concede conforme `13-reputacao.csv`; não é troca automática.
- **Palavra dada:** promessa junto à água corrente é séria em muitas comunidades, não em todas (doc 01 §10). Ivara trata promessa clara como compromisso sério (doc 01 §9).
- **Hospitalidade:** o anfitrião pergunta, na primeira refeição após viagem longa, qual parte do caminho deve ser lembrada (doc 01 §10).
- Comunidades micélioides podem precisar de tempo; silêncio não é concordância (doc 01 §10).

### 3.4 Reparação

Formas escritas no doc 04 §4.1: **reparação** (refazer o dano), **compensação** (pagar a quem perdeu), **restrição temporária** (acesso, rota ou licença), **perda de cargo** (só se o jogador tiver cargo) e **punição**. Quarentena obriga compensação quando impede o trabalho (doc 01 §10). Reparar não apaga o rumor: a reputação muda por canal e por região, no ritmo do motor.

## 4. Intermediários (M4.4c)

Quem leva a notícia, o pedido ou a decisão. Ver `13-canais.csv` (prefixo `canal.`).

| Canal | Função | O que o cânone diz | Limite |
|---|---|---|---|
| `canal.cartaz` — Cartaz | anúncio público de um ato, pedido ou procura | não escrito no cânone; ASSUMINDO uso como canal, sem criar facção nem ofício | vale só como rumor até haver acusação com ato, regra e evidência (doc 04 §4.1; Lei 8) |
| `canal.cacador` — Caçador de pessoas | rastrear alguém procurado por uma autoridade | não há caçadores de recompensa escritos em Aeltharyn; lacuna explícita, nenhuma facção criada. Rastreadores documentados: Fen Orvaluz rastreia caçadores ilegais e recusa contratos contra espécies protegidas | capturar não define quem julga (doc 04 §4) |
| `canal.emissario` — Emissário | levar decisão, convocação ou pedido de uma autoridade a outra jurisdição | a Coroa pode intervir em crime contra árvore ancestral ou nascente; a autoridade local preserva o local e recolhe provas antes | a Coroa não assume automaticamente casos locais (doc 04 §4) |
| `canal.mensageiro` — Mensageiro | levar notícia e cartas entre cidades | Nara Três-Fios memoriza rotas e sons; não conhece segredos só por levar mensagens | a notícia só chega pelo tempo de rota documentado (doc 07) |
| `canal.mercador` — Mercador e informante | propagar notícia e comprar informação | a Liga das Rotas prefere comprar informação a ameaçar | pode divulgar versão enviesada pelo interesse comercial (ASSUMINDO) |
| `canal.sinal-maritimo` — Sinais marítimos | avisar embarcações e registrar embarcações desconhecidas | faroleiros emitem alertas mesmo contra o comércio; a guarda quer usar o farol para observação política (tensão aberta) | pode ser controlado ou atrasado por autorização (M4.3) |
| `canal.arquivo` — Arquivo e cadeia de custódia | registrar fatos, testemunhos e documentos | preserva a fonte e registra a cadeia de custódia; a posse não torna o conteúdo verdadeiro | pode restringir acesso a registros politicamente perigosos (12-facoes.csv) |
| `canal.hospitalidade` — Casa da Hospitalidade | acolher viajantes e refugiados e registrar hóspedes | Fonte do Regresso resiste a entregar a lista de hóspedes sem base legal (caso E) | entregar nomes exige distinguir ameaça concreta de vigilância política |
| `canal.mediador-feerico` — Mediação feérica | negociar favores com comunidades feéricas | Ivara negocia com termos literais; promessa casual dita com clareza pode ser compromisso sério | não fala por todos os feéricos |
| `canal.festival` — Festival e artistas | espalhar histórias e canções | Silvaran vive de música, teatro e festas; uma canção pode circular sem autoria | uma canção pode exagerar ou distorcer; rumor não vira fato (MP-07) |

**Lacunas explícitas:** cartaz e caçador de pessoas não estão escritos no cânone de Aeltharyn; entram só como canais, sem facção, ofício ou NPC novos. Durações de notícia dependem do doc 07; onde a rota é desconhecida, o prazo também.

## 5. Ligação com o jogo

- **MP-07:** `13-reputacao.csv` dá a disposição qualitativa por cidade e facção; `13-canais.csv` dá os canais pelos quais a notícia viaja.
- **MP-06 (caçadores):** o mundo só autoriza rastreadores onde o texto os escreve (ver `canal.cacador`); qualquer outro depende de decisão do autor.
- **Sementes (M4.3):** reações aqui valem para as 90 sementes; nenhuma exige um NPC inexistente.
