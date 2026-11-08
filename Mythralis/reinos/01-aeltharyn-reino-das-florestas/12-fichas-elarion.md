# 12 — Fichas de Elarion (M3.1, região de teste)

**Escopo:** `city.elarion`, seus 2 assentamentos e as 10 pessoas com cidade = Elarion no registro mestre. Fonte: docs 01, 03 e 12; `.kt` só onde indicado (não canônico). Campo sem texto de origem fica *desconhecido*; nada foi deduzido. Estados: confirmado (C), alegado (A), suspeita (S), desconhecido (D). Agenda e pressão marcadas **[doc]**, **[.kt]** ou **ASSUMINDO**.
**Fora do escopo (ver D-18, D-19):** rei Aerendyl e princesa Maereth Vaelorian (cidade em branco no registro) e Edran Sem-Selo (sem ID).

## 1. Cidade — `city.elarion`

| Campo | Ficha |
|---|---|
| Tipo | capital política, arquivo e centro cerimonial |
| População | faixa de uso: mais de 50.000 (valor de perfil 82.000; não somar) |
| Localização | sete árvores colossais junto ao Rio Lúmen, ligadas por pontes vivas e plataformas |
| Governo local | Intendente Avariel Thorne (`npc.avariel-thorne`), nomeado pelo rei, fiscalizado pelo conselho urbano |
| Economia | administração, ensino, artes, papel de casca, instrumentos de precisão, joalheria de âmbar, hospedarias, livros |
| Defesas | Guarda das Copas, arqueiros em plataformas, barreiras de rio, rotas de evacuação. Vulnerável a fogo, sabotagem e bloqueio fluvial |
| Locais notáveis | Salão das Sete Raízes; Arquivo das Seivas; Mercado Suspenso; Jardim das Árvores-Coração; Ponte das Promessas; Observatório de Maereth |
| Problemas atuais | documentos da Guerra das Cinzas Verdes sumiram; moradia nas copas encarece; manutenção diz que a corte ignora rachaduras em pontes antigas |
| Facções presentes | Conselho das Sete Copas, Arquivistas da Seiva, Liga das Rotas, Guarda das Copas |
| Rumores (R) | raiz fechou sozinha a entrada de uma câmara sob o Arquivo; conselheiro paga por mapas antigos do sul; coruja de memória repete frase que não consta em crônica |
| Lenda | as sete árvores se inclinaram nos primeiros juramentos; registros antigos citam só um vento incomum; a versão sagrada veio séculos depois |
| Função narrativa | intriga política, investigação histórica, sucessão, diplomacia, mistérios da floresta |
| Assentamentos | `settlement.raiz-de-prata` (viveiros, 1.800), `settlement.sete-degraus` (ponte e pedágio, 2.400) |

## 2. Assentamentos

### `settlement.raiz-de-prata` — vila de viveiros
Liderança: `npc.nivara-folha-de-orvalho`. Obrigação: manter mudas de espécies comuns e reserva emergencial de nativas. Interesse: proteger sementes adaptadas ao solo local. Tensão: a Coroa exige mudas raras; Nivara diz que retirar as sementes locais agora compromete a próxima estação.

### `settlement.sete-degraus` — povoado de ponte e pedágio
Liderança: `npc.othalen-vira-pedra`. Obrigação: manter a ponte transitável; passagem a mensageiros oficiais, socorro e evacuações. Interesse: pagamento pelo desgaste e impedir que a isenção de nobres recaia nos demais. Tensão: três casas nobres atravessam sem pagar sem explicação nos registros; decreto original não visto.

## 3. Pessoas

### `npc.avariel-thorne` — Avariel Thorne
| Campo | Ficha |
|---|---|
| Idade | cerca de 176 |
| Povo (ID) | elfo (Elfo, doc 01) |
| Origem | desconhecido (não documentado) |
| Cargo e títulos | Intendente de Elarion; nomeado pelo rei |
| Cidade e facção | city.elarion; facção: D |
| Personalidade, valores, objetivos, medos, limites | eficiente, reservado; valores: manutenção urbana [doc]. Objetivos: reformar pontes (adiou). Medos: D. Limites: fiscalizado pelo conselho urbano |
| Capacidades, influência, restrições | administra a cidade; recursos orçamentários em disputa (C). Restrição: nomeado pelo rei, não é autônomo |
| Relações | nomeado pelo rei (C); fiscalizado pelo conselho urbano (C); demais: D |
| Biografia e marcos | D |
| Conhecimento | C: tem relatórios sobre pontes danificadas e adiou reformas por disputa orçamentária. S: D. D: causa do sumiço dos documentos |
| Camadas (M5.2) | `fact.avariel-pontes` (local) |
| Estado atual | vivo |
| **Agenda atual** | Decidir se admite o erro das pontes ou o oculta [doc: "pode admitir o erro ou tentar ocultá-lo"] |
| **Pressão** | Disputa orçamentária que travou as reformas; pontes com rachaduras apontadas por trabalhadores [doc] |
| Evolução plausível | Admitir o erro (reforma, risco político) ou ocultá-lo (risco de acidente). ASSUMINDO: ambas plausíveis; escolha depende das cenas |

### `npc.saelith-arco-de-chuva` — Saelith Arco-de-Chuva
| Campo | Ficha |
|---|---|
| Idade | cerca de 240 |
| Povo (ID) | elfo |
| Origem | Elfa dos Bosques |
| Cargo e títulos | General dos Vigias do Primeiro Verde (`faction.vigias-primeiro-verde`) |
| Cidade e facção | city.elarion; faction.vigias-primeiro-verde (líder) |
| Personalidade, valores, objetivos, medos, limites | desconfiada de políticos; competente em patrulha e defesa. Objetivo: achar quem corta árvores ancestrais sem prisões indiscriminadas. Valor: impedir culpa coletiva por crime individual [.kt]. Medo: D. Limite: não consegue abastecer todos os postos |
| Capacidades, influência, restrições | comanda patrulheiros, mapas de trilhas, postos de observação [doc]. Restrição: reforma depende de fiscais que resistem a prestar contas [.kt] |
| Relações | amiga de Maereth Vaelorian, discorda de seus métodos (C); respeitada pelo rei Aerendyl (C) |
| Biografia e marcos | organizou patrulhas em turnos e impediu que a guarda culpasse famílias inteiras [.kt] |
| Conhecimento | C: há corte de árvores ancestrais. S: D. D: quem corta |
| Camadas (M5.2) | `fact.saelith-corte-ancestrais` (local) |
| Estado atual | vivo |
| **Agenda atual** | Auditar a guarda e descobrir como os registros desaparecidos foram removidos [.kt]; achar os cortadores de árvores ancestrais [doc] |
| **Pressão** | Fiscais resistem a prestar contas; postos sem suprimento [doc/.kt] |
| Evolução plausível | ASSUMINDO: pode apertar a auditoria (conflito com fiscais) ou fechar trilhas em caso grave (método da facção, doc 01 §7.1) |

### `npc.nimeth-vela-chuva` — Nimeth Vela-de-Chuva
| Campo | Ficha |
|---|---|
| Idade | 187 |
| Povo (ID) | elfo-das-estrelas |
| Origem | Elfa das Estrelas |
| Cargo e títulos | guardiã viva do Arquivo das Seivas |
| Cidade e facção | city.elarion; facção: D (o Arquivo liga-se aos Arquivistas da Seiva, vínculo formal não escrito) |
| Personalidade, valores, objetivos, medos, limites | cuidadosa; valor: acesso a cópias sem expor refugiados e informantes. Medo: D. Limite: decide sozinha o que o povo pode conhecer (crítica) |
| Capacidades, influência, restrições | controla retirada e cópia de registros do Arquivo. Restrição: não consegue salvar e catalogar tudo sob pressão |
| Relações | críticos a acusam de decidir sozinha (A); demais: D |
| Biografia e marcos | impediu incêndio nos registros; salvou primeiro os raros e deixou livros fiscais comuns |
| Conhecimento | C: documentos da Guerra das Cinzas Verdes desapareceram. S: D. D: quem os levou |
| Camadas (M5.2) | `fact.nimeth-documentos-sumidos` (publico) |
| Estado atual | vivo |
| **Agenda atual** | Localizar cópias dos registros desaparecidos da Guerra das Cinzas Verdes sem expor informantes [.kt]; tornar cópias acessíveis [doc] |
| **Pressão** | Não consegue salvar e catalogar todos os documentos sob pressão [.kt] |
| Evolução plausível | ASSUMINDO: abrir cópias (críticos de elitismo diminuem, risco a informantes) ou manter controle |

### `npc.parel-tres-parafusos` — Parel Três-Parafusos
| Campo | Ficha |
|---|---|
| Idade | 63 |
| Povo (ID) | gnomo-dos-vales |
| Origem | Gnomo dos Vales; "das engrenagens" descreve o ofício |
| Cargo e títulos | benfeitor vivo dos bairros suspensos |
| Cidade e facção | city.elarion; facção: D |
| Personalidade, valores, objetivos, medos, limites | recusa exclusividade da Coroa sobre o projeto; valor: saber espalhado entre moradores. Medo: D |
| Capacidades, influência, restrições | sistema barato de inspeção de cabos e pontes; oficina pequena. Restrição: reparos lentos, não atende todas as pontes |
| Relações | moradores treinados (C); Coroa oferece patrocínio que ele recusa (C) |
| Biografia e marcos | criou o sistema de inspeção e treinou moradores |
| Conhecimento | C: pontes e cabos desgastam. S: D. D: rachaduras que a administração ignora |
| Camadas (M5.2) | — |
| Estado atual | vivo |
| **Agenda atual** | Reparar duas pontes de serviço e ensinar moradores a reconhecer cabos comprometidos antes das chuvas [.kt] |
| **Pressão** | Oficina atende só parte da cidade [.kt/doc] |
| Evolução plausível | ASSUMINDO: aliado natural do relatório de pontes de Avariel; mantém recusa de exclusividade |

### `npc.nilo-recado-certo` — Nilo Recado-Certo
| Campo | Ficha |
|---|---|
| Idade | desconhecido (não documentado) |
| Povo (ID) | homem-corvo-das-torres |
| Origem | Corvário das Torres |
| Cargo e títulos | benfeitor vivo; criador da rede de mensagens |
| Cidade e facção | city.elarion; facção: D |
| Personalidade, valores, objetivos, medos, limites | D (doc só registra a obra). Objetivo: manter mensagens entre aldeias isoladas |
| Capacidades, influência, restrições | rede de mensagens com confirmação de entrega e cópia de avisos urgentes. Restrição: a rede perde alcance quando tempestades fecham os pontos de pouso [.kt] |
| Relações | aldeias isoladas atendidas (C); demais: D |
| Biografia e marcos | criou mensagens com confirmação de entrega e cópia de avisos urgentes |
| Conhecimento | C: sua rede funciona. S: D. D: tudo o mais |
| Camadas (M5.2) | — |
| Estado atual | vivo |
| **Agenda atual** | Restaurar pontos de pouso e rotas alternativas entre aldeias [.kt] |
| **Pressão** | Tempestades fecham pontos de pouso [.kt] |
| Evolução plausível | ASSUMINDO: depende de estação (agenda sazonal, doc 01 §35) |

### `npc.nara-tres-fios` — Nara Três-Fios
| Campo | Ficha |
|---|---|
| Idade | cerca de 39 |
| Povo (ID) | homem-corvo (Corvária, D-01) |
| Origem | Corvária |
| Cargo e títulos | mensageira e restauradora de mapas em Elarion |
| Cidade e facção | city.elarion; facção: D |
| Personalidade, valores, objetivos, medos, limites | D. Limite: não conhece segredos só por trabalhar com mensagens |
| Capacidades, influência, restrições | memoriza rotas e sons; restaura mapas. Restrição: acesso depende do Arquivo (ASSUMINDO) |
| Relações | ligada ao Arquivo das Seivas pelo mapa alterado (C); demais: D |
| Biografia e marcos | D |
| Conhecimento | C: um conjunto de mapas do Arquivo foi alterado. S: D. D: quem alterou |
| Camadas (M5.2) | `fact.nara-mapas-alterados` (local) |
| Estado atual | vivo |
| **Agenda atual** | Descobrir quem alterou um conjunto de mapas do Arquivo das Seivas [doc] |
| **Pressão** | ASSUMINDO: precisa do acesso a mapas e câmaras do Arquivo, controlado por Nimeth; nada no texto diz que ela é pressionada por alguém |
| Evolução plausível | ASSUMINDO: pode ligar o mapa alterado ao rumor do conselheiro que paga por mapas do sul |

### `npc.nivara-folha-de-orvalho` — Nivara Folha-de-Orvalho
| Campo | Ficha |
|---|---|
| Idade | desconhecido (não documentado) |
| Povo (ID) | desconhecido (não documentado) |
| Origem | desconhecido (não documentado) |
| Cargo e títulos | liderança de Raiz-de-Prata; eleita por famílias viveiristas, mandato de 6 anos; confirmação distrital formal |
| Cidade e facção | settlement.raiz-de-prata; city.elarion (cidade-mãe) |
| Personalidade, valores, objetivos, medos, limites | valor: sementes locais; objetivo: proteger a próxima estação. Medo/limites: D |
| Capacidades, influência, restrições | mudas comuns e reserva de nativas. Restrição: a recusa à Coroa pode provocar disputa |
| Relações | Coroa (pressão por mudas raras); famílias viveiristas (base) |
| Biografia e marcos | D |
| Conhecimento | C: retirar sementes locais agora compromete a próxima estação (afirmação dela, A). D: efeito real |
| Camadas (M5.2) | `fact.raiz-germinacao-anormal` (local); `fact.raiz-estoque-escondido` (secreto) |
| Estado atual | vivo |
| **Agenda atual** | Segurar as sementes locais e negociar o pedido de mudas raras [doc] |
| **Pressão** | Exigência da Coroa de mudas raras para áreas nobres [doc] |
| Evolução plausível | ASSUMINDO: ceder parte das mudas ou recusar (disputa) |

### `npc.othalen-vira-pedra` — Othalen Vira-Pedra
| Campo | Ficha |
|---|---|
| Idade | desconhecido (não documentado) |
| Povo (ID) | desconhecido (não documentado) |
| Origem | desconhecido (não documentado) |
| Cargo e títulos | magistrado de Sete Degraus; eleito a cada 4 anos |
| Cidade e facção | settlement.sete-degraus; city.elarion (cidade-mãe) |
| Personalidade, valores, objetivos, medos, limites | desconfia de privilégio legal. Valores: pedágio justo. Medo/limites: D |
| Capacidades, influência, restrições | cobra e gere o pedágio, mantém a ponte. Restrição: não viu o decreto original |
| Relações | três casas nobres isentas (C); moradores e comerciantes (base) |
| Biografia e marcos | D |
| Conhecimento | C: três casas passam sem pagar. S: privilégio legal. D: decreto original |
| Camadas (M5.2) | `fact.sete-degraus-volume` (local); `fact.sete-degraus-isencao` (local); `fact.sete-degraus-tarifa-local` (local) |
| Estado atual | vivo |
| **Agenda atual** | Descobrir o decreto original das isenções [doc] |
| **Pressão** | Desgaste da ponte pago só por não isentos [doc] |
| Evolução plausível | ASSUMINDO: pode contestar as casas nobres ou aceitar a isenção |

### `npc.avarra-sete-pontes` — Avarra das Sete Pontes
| Campo | Ficha |
|---|---|
| Idade | morta (Era da Coroa de Prata) |
| Povo (ID) | elfo |
| Origem | Elfa dos Bosques |
| Cargo e títulos | arquiteta e mediadora; fundadora cívica |
| Cidade e facção | city.elarion |
| Personalidade, valores, objetivos, medos, limites | D |
| Capacidades, influência, restrições | projetou as primeiras pontes sem perfurar os troncos |
| Relações | famílias removidas do trajeto (contestam) |
| Biografia e marcos | uniu os sete assentamentos; negociou a remoção de duas famílias |
| Conhecimento | C: obra. A: "mãe da capital" (versão oficial). D: detalhes da remoção |
| Camadas (M5.2) | — |
| Estado atual | morta |
| **Agenda atual** | n/a (morta) |
| **Pressão** | n/a. Legado contestado: estátua com uma face para o palácio e outra para os bairros inferiores |
| Evolução plausível | n/a |

### `npc.maereth-cartografa-das-copas` — Maereth, a Cartógrafa das Copas
| Campo | Ficha |
|---|---|
| Idade | morta há 430 anos |
| Povo (ID) | elfo (Elfa, doc 01) |
| Origem | desconhecido (não documentado) |
| Cargo e títulos | heroína histórica; pessoa distinta da Princesa Maereth Vaelorian |
| Cidade e facção | city.elarion |
| Personalidade, valores, objetivos, medos, limites | D |
| Capacidades, influência, restrições | percorreu os níveis inferiores e desenhou rotas de evacuação |
| Relações | D |
| Biografia e marcos | salvou moradores na queda de uma ponte; parte da fama vem de relatos escritos décadas depois (A) |
| Conhecimento | C: os mapas existem. S: passagens de serviço que a administração prefere fora das plantas públicas. D: tamanho real do feito |
| Camadas (M5.2) | — |
| Estado atual | morta |
| **Agenda atual** | n/a (morta) |
| **Pressão** | n/a. Legado: mapas mostram passagens de serviço fora das plantas públicas |
| Evolução plausível | n/a |
