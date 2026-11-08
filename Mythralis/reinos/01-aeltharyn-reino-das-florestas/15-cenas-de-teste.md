# 15 — Cenas de teste (M5.3)

**Fonte:** `14-resumos-de-contexto` (M5.1), `14-camadas-de-informacao` (M5.2), `13-sementes-de-aventura`, `12-voz.csv`, doc 07 (rotas), doc 01 §35 (calendário). Dados em `15-cenas-de-teste.csv`.

## Regras

- Cada cena tem a lista **entra** (texto exato que vai ao contexto da IA, no máximo ~2.400 caracteres) e a lista **não entra** (o que fica fora e quando entraria).
- Só entram fatos de camada **público** ou **local**; fato restrito ou secreto só entra depois de cumprido o caminho de descoberta (M5.2). Relógios nunca entram.
- O texto de "entra" é montado dos resumos, fatos, sementes e linhas de rota já escritos; nada foi acrescentado. A data é a abertura do ano 742 (D-14).
- A contagem de caracteres inclui os rótulos `[id]`. As cenas são testes de contexto, não texto de lore.

## `cena.vilarejo-raiz-de-prata` — Vilarejo élfico: Raiz-de-Prata

Chegada a um vilarejo e conversa com a liderança (o exemplo do autor: um vilarejo élfico, sem carregar o reino).

**Entra (1110 caracteres):**

- [data] Ano 742 da Contagem das Seivas, Silêncio (inverno), Lua 1, dia 1.
- [settlement.raiz-de-prata] Assentamento ligado a Elarion; liderança: Nivara Folha-de-Orvalho. Obrigação: manter mudas de espécies comuns e uma reserva emergencial de árvores nativas. Interesse: proteger sementes adaptadas ao solo da vila, inclusive variedades que a corte considera pouco produtivas. Tensão: a Coroa exige mudas de espécies raras para restaurar áreas nobres; Nivara afirma que retirar as sementes locais agora comprometerá a próxima estação.
- [npc.nivara-folha-de-orvalho] Nivara Folha-de-Orvalho: liderança de assentamento: eleita por famílias viveiristas por mandatos de seis anos; a confirmação distrital é formal, mas a recusa real pode provocar disputa
- [fact.raiz-germinacao-anormal|local] alguns lotes de sementes tiveram germinação anormal; a causa é desconhecida
- [seed.raiz-de-prata.p] Reserva de mudas: Nivara precisa de ajuda para contar a reserva emergencial de árvores nativas e as mudas comuns.
- [regra] Estoque, votação da cota e o que Nivara guarda ficam com o Mestre; só entram após o caminho de descoberta.

**Não entra:**

- `fact.raiz-estoque-escondido` (secreto): só após inspeção ou confiança de Nivara
- `event.mudas-raras` e `seed.raiz-de-prata.m/.g`: só se a cena os disparar
- resumo completo de `city.elarion` (a vizinhança do assentamento já dá a cidade-mãe)
- os outros 19 assentamentos, as 10 facções e os 7 relógios
- `12-voz.csv` (Nivara não tem voz escrita) e relações secretas

## `cena.chegada-thalanor` — Chegada ao porto de Thalanor

Desembarque em Thalanor por barco, com o navio sem tripulação como pano de fundo.

**Entra (1452 caracteres):**

- [data] Ano 742 da Contagem das Seivas, Silêncio (inverno), Lua 1, dia 1.
- [city.thalanor] Porto marítimo e alfândega numa enseada abrigada da Costa de Âmbar, perto da foz do Lúmen. Governo: Almirante-civil escolhida por conselho de capitães e confirmada pela Coroa. Vive de pesca, navegação, sal, âmbar, cordame e reparo de barcos. Defende-se com faróis, correntes portuárias e armazéns de quarentena. Tensões: contrabando de resina, tripulações desaparecidas e atrito entre capitães locais e inspetores reais. População: 30.000–49.999 habitantes (faixa de uso).
- [npc.syranna-das-mares] Syranna das Marés: Almirante-civil de Thalanor; Elfa costeira, cerca de 205 anos. texto do doc: Elfa costeira, cerca de 205 anos; Capacidade: almirante-civil; quer faróis melhores e alfândega menos corrupta. Limite: não aceita arquivar o desaparecimento do irmão como naufrágio. Tom: direta, portuária, marcada pelo irmão desaparecido.
- [route.thalanor-elarion] Thalanor ↔ Elarion: 3 dias por barco pelo Lúmen; 5 dias por estrada costeira. Tempos de meios diferentes; não compará-los sem considerar carga, clima e objetivo.
- [fact.mist-c-navio|publico] o navio chegou a Thalanor em boas condições, sem tripulação e sem sinais claros de combate
- [fact.mist-c-carga|local] a carga do navio foi registrada de forma incompleta
- [seed.thalanor.p] Cartas em língua desconhecida: Um faroleiro recebe cartas escritas numa língua que ninguém identificou e quer saber o que dizem.

**Não entra:**

- `clock.ghost-ship` (secreto, uso de Mestre/motor)
- hipóteses do mistério C (abandono, contrabando, evento incomum): só em aprofundamento
- `fact.ponta-luzes-navio` e `fact.farol-intervalo-luz` (locais): entram se o personagem for a Ponta da Concha ou ao Farol de Âmbar
- `event.navio-quarentena`: só se disparado
- outros 8 cidades e a Companhia do Âmbar em detalhe

## `cena.audiencia-corte` — Audiência na corte (Elarion)

Audiência pública do conselho com o rei, a princesa e o arquivista-mor.

**Entra (1913 caracteres):**

- [data] Ano 742 da Contagem das Seivas, Silêncio (inverno), Lua 1, dia 1.
- [city.elarion] Rotas: Nymvar (2 d estrada, 1–2 d rio); Vael-Tir (3 d, mais com quarentena); Caladwen (2 d, 1,5 d por trilhas rápidas); Arven (1,5 d); Thalanor (3 d barco, 5 d estrada); Torvelume (4 d).
- [npc.aerendyl-vaelorian] Rei Aerendyl Vaelorian: Rei de Aeltharyn, guardião formal dos Sete Juramentos; texto do doc: Elfo dos Bosques, 487 anos; Capacidade: autoridade sobre a guarda real, acesso aos arquivos da Coroa, rede de mensageiros. Limite: depende do conselho e dos juramentos locais; não impõe decisões a cidades autônomas sem custo político. Tom: pausado, formal, avesso ao irreversível.
- [npc.maereth-vaelorian] Princesa Maereth Vaelorian: herdeira reconhecida e Guardiã do Pacto das Nascentes; texto do doc: Elfa das Estrelas, 173 anos; Capacidade: influência política como herdeira. Limite: não comanda a guarda inteira; propostas podem ser bloqueadas pelo conselho. Tom: franca, metódica, impaciente com burocracia.
- [npc.lethariel-nascente-clara] Lethariel Nascente-Clara: Arquivista-mor; Elfa das Estrelas, cerca de 310 anos. texto do doc: Elfa das Estrelas, cerca de 310 anos; Capacidade: preserva fontes primárias e registros contraditórios. Limite: não divulga documentos privados sem avaliar risco a pessoas vivas. Tom: arquivista exata, cuidadosa com pessoas vivas.
- [voz npc.aerendyl-vaelorian] tom: pausado, formal, avesso ao irreversível; modelo: "Decisões irreversíveis pedem mais de uma estação."|"Ouvi o relatório. Ainda não ouvi a causa."|"Não confundo paciência com omissão."
- [fact.nimeth-documentos-sumidos|publico] documentos da Guerra das Cinzas Verdes desapareceram; quem os levou e onde há cópias é desconhecido
- [fact.mist-a-sintomas|publico] duas árvores-coração mostram perda de brilho nas folhas internas e raízes que evitam certas áreas; os sintomas lembram a Vigília Longa; não há prova de causa comum

**Não entra:**

- `fact.rei-extracao-ilegal` (restrito): só com autorização de arquivo ou favor
- `fact.maereth-encontros-sul` (secreto): só por testemunho, comparação e confiança
- coluna `nunca_diz` de `12-voz.csv`: o Mestre aplica como limite, não a copia para o contexto
- `clock.heart-trees`, `clock.city-distrust` (secretos)
- os outros 4 conselheiros: só se falarem
- `12-relacoes.csv` marcadas como segredo

## `cena.investigacao-arquivos` — Investigação nos arquivos de Elarion

Busca de um fato nos registros do Arquivo das Seivas.

**Entra (1794 caracteres):**

- [data] Ano 742 da Contagem das Seivas, Silêncio (inverno), Lua 1, dia 1.
- [faction.arquivistas-seiva] Arquivistas da Seiva. Liderança: Lethariel Nascente-Clara. Objetivo: preservar fontes históricas, registros de tratados e testemunhos. Métodos: pesquisa, catalogação, autenticação e publicação.
- [npc.lethariel-nascente-clara] Lethariel Nascente-Clara: Arquivista-mor; Elfa das Estrelas, cerca de 310 anos. texto do doc: Elfa das Estrelas, cerca de 310 anos; Capacidade: preserva fontes primárias e registros contraditórios. Limite: não divulga documentos privados sem avaliar risco a pessoas vivas. Tom: arquivista exata, cuidadosa com pessoas vivas.
- [npc.nara-tres-fios] Nara Três-Fios: Mensageira e restauradora de mapas em Elarion; Corvária, cerca de 39 anos. texto do doc: Corvária, cerca de 39 anos; Capacidade: memoriza rotas e sons; restaura mapas. Limite: não conhece segredos só por trabalhar com mensagens. Tom: minuciosa, sensorial (rotas e sons).
- [fact.nara-mapas-alterados|local] um conjunto de mapas do Arquivo das Seivas foi alterado; quem alterou é desconhecido
- [fact.mist-b-manuscrito|local] o manuscrito da oitava cidade é fragmentário, o nome da comunidade está apagado, ela não assinou os juramentos e uma cópia incompleta circula entre estudiosos
- [fact.mist-b-registros-divergem|local] os registros não concordam sobre todos os nomes dos sete assentamentos
- [fact.lethariel-oitava-cidade|local] Lethariel suspeita, por uma crônica, de uma oitava cidade fundadora e a procura
- [seed.elarion.m] Mapas alterados no Arquivo: Nara Três-Fios quer saber quem alterou um conjunto de mapas do Arquivo das Seivas e precisa de quem compare cópias.
- [regra] Acesso a arquivos restritos exige autorização (doc 01 §18); uma cópia na biblioteca real não prova que seja original (doc 04 §10).

**Não entra:**

- hipóteses e "a verdade não pode ser" do mistério B (`13-misterios-limites.md`): só ao aprofundar
- `event.arquivo-sinistro`: só se disparado
- `fact.selvaris-manuscrito` (local): entra se Selvaris aparecer
- fatos restritos e secretos de qualquer outra entidade
- demais cidades e relógios

## `cena.viagem-elarion-nymvar` — Viagem por rota: Elarion para Nymvar

Deslocamento por estrada ou rio, com o dique de Nymvar como destino.

**Entra (872 caracteres):**

- [data] Ano 742 da Contagem das Seivas, Silêncio (inverno), Lua 1, dia 1.
- [city.elarion] Rotas: Nymvar (2 d estrada, 1–2 d rio); Vael-Tir (3 d, mais com quarentena); Caladwen (2 d, 1,5 d por trilhas rápidas); Arven (1,5 d); Thalanor (3 d barco, 5 d estrada); Torvelume (4 d).
- [city.nymvar] Rotas: Elarion (2 d estrada, 1–2 d rio); Orvass (3 d, faixa 2–6); Arven (2–4 d caravana, 1 d viajante leve); Thalanor (3–4 d barco de carga).
- [route.elarion-nymvar] Elarion ↔ Nymvar: 2 dias por estrada; 1–2 dias pelo rio, conforme corrente. Corrente e estação podem alterar o tempo; não há modificadores de estação definidos.
- [fact.junco-defeitos-dique|local] o dique tem defeitos estruturais; quem autorizou o lote de material inferior é desconhecido
- [fact.nymvar-material-inferior|local] um trecho do dique foi reparado com material inferior; quem autorizou a compra é desconhecido

**Não entra:**

- `clock.dikes` (secreto)
- `fact.junco-amostras-reparo` e `fact.nymvar-provas-elsin` (restritos)
- `event.dique-rompido` e `event.rota-celeiros-fechada`: só se disparados
- `route.orvass-brumafunda` e demais rotas sem duração (continuam desconhecidas)
- resumos completos de Nymvar e Elarion (a vizinhança basta para a viagem)

## Resultado

- `cena.vilarejo-raiz-de-prata`: 1110 caracteres (dentro do limite)
- `cena.chegada-thalanor`: 1452 caracteres (dentro do limite)
- `cena.audiencia-corte`: 1913 caracteres (dentro do limite)
- `cena.investigacao-arquivos`: 1794 caracteres (dentro do limite)
- `cena.viagem-elarion-nymvar`: 872 caracteres (dentro do limite)
