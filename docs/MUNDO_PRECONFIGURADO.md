# Mundo Pré-configurado do RPG Solo

**Status:** Proposta funcional para auditoria e implementação (texto do autor, guardado como referência; o plano de implementação está no bloco WD de `PLANO_RPG_SOLO.md`).

## 1. Objetivo

Criar um mundo de fantasia pré-configurado, consistente e persistente para o RPG Solo. O jogador poderá iniciar campanhas sem precisar gerar toda a geografia, política e população do zero. Campanhas poderão compartilhar o mesmo mundo ou utilizar linhas do tempo alternativas.

## 2. Escala mínima

- 1 mundo com nome próprio, história, calendário e estado inicial.
- Pelo menos 5 reinos ou territórios soberanos.
- Pelo menos 10 cidades por reino: mínimo de 50 cidades.
- NPCs principais em cada reino: governantes, herdeiros, heróis famosos, generais e figuras políticas.
- Montanhas, cavernas, rios, lagos, mares, ilhas, vulcões, florestas, planícies e outros biomas.
- Regiões de origem e concentração de diferentes raças e povos.
- Facções, alianças, rivalidades, rotas comerciais e conflitos.
- Memória persistente para acontecimentos que afetem campanhas futuras.

Recomendação inicial: pelo menos 8 a 12 NPCs principais por reino, com papéis variados. Os personagens precisam ter objetivos, relações e limitações próprias, não apenas nomes e títulos.

## 3. Ficha do mundo

Cada mundo deve registrar: ID permanente e nome oficial; descrição, gênero, tom e história de origem; eras históricas e situação política atual; calendário e passagem do tempo; regras gerais de magia e elementos sobrenaturais; grandes ameaças, conflitos e acontecimentos conhecidos; estado inicial canônico e data de início.

O cenário inicial não deve ser um roteiro obrigatório. As decisões dos jogadores podem alterar o mundo, desde que as mudanças sejam registradas.

## 4. Reinos

Cada reino deve possuir ID, nome, capital, forma de governo, governante, sucessão, cidades, população, povos presentes, economia, recursos, forças militares, leis, costumes, facções, fronteiras, rotas e relações diplomáticas. Também deve registrar aliados, rivais, tratados, disputas, ameaças, regiões naturais, ganchos de aventura e acontecimentos históricos relevantes. Os reinos não devem ser caricaturas de bem ou mal: devem ter conflitos internos, grupos rivais e pessoas com objetivos diferentes.

## 5. Cidades

Cada reino deve possuir pelo menos 10 cidades distintas. Para cada cidade, registrar: ID permanente, nome e reino; tipo (capital, porto, fortaleza, centro comercial, cidade sagrada, mineradora, acadêmica etc.); localização, bioma e população aproximada; povos presentes e composição cultural; governante local e NPCs relevantes; economia, recursos, serviços e defesas; facções, problemas, perigos e locais notáveis; rotas de acesso e distâncias relativas; rumores, ganchos de aventura e histórico; estado atual e alterações sofridas.

As cidades precisam ter funções coerentes com sua geografia. Portos precisam ter acesso navegável; centros mineradores precisam de recursos minerais; fortalezas devem ocupar posições estratégicas plausíveis.

## 6. NPCs principais

Cada reino deve possuir governantes, herdeiros, pelo menos dois heróis famosos, pelo menos dois generais famosos e outras figuras relevantes, como conselheiros, diplomatas, líderes religiosos e chefes de facções.

Cada NPC deve possuir: ID permanente, nome, idade ou faixa etária, raça, origem e aparência; cargo, títulos, reino, cidade e facção; personalidade, valores, objetivos, medos e limites; capacidades, influência, recursos e restrições; relações pessoais e políticas; biografia e acontecimentos marcantes; conhecimentos confirmados, suspeitas e informações desconhecidas; estado atual (vivo, morto, desaparecido, exilado ou desconhecido); objetivo atual e condições plausíveis de evolução.

Um herói pode estar aposentado ou desacreditado. Um general pode discordar do rei. Um príncipe pode disputar a sucessão. Os NPCs não devem existir apenas para servir ao protagonista.

## 7. Geografia

O mapa mundial deve incluir: cordilheiras, montanhas e passagens; cavernas, minas e redes subterrâneas; rios, nascentes, afluentes, lagos e fozes coerentes; mares, costas, portos e ilhas; vulcões ativos, adormecidos e extintos; florestas, planícies, vales e regiões agrícolas; desertos, tundras, pântanos e outros biomas adequados ao clima; ruínas, regiões perigosas e zonas de magia incomum.

Cada região natural deve ter ID, nome, tipo, localização, características, recursos, perigos, povos associados e conexões geográficas. A geografia deve respeitar a lógica: rios geralmente descem de áreas elevadas; montanhas afetam clima e rotas; portos dependem de acesso ao mar ou a rios navegáveis; vulcões influenciam recursos e perigos. Grandes mudanças geográficas precisam de um evento registrado.

## 8. Regiões de raças e povos

O cenário deve definir regiões de origem e concentração populacional dos povos, sem afirmar que todos os membros de uma raça compartilham a mesma cultura ou lealdade. Para cada povo, registrar origem, territórios históricos, assentamentos, línguas, costumes, crenças, organização social, relações históricas, migrações, diásporas, comunidades fora da região natal e diversidade interna.

Uma raça pode ter uma região de origem sem viver exclusivamente nela. Cidades multiculturais, famílias mistas, viajantes, refugiados e minorias devem ser possíveis. A presença de um povo em determinada região deve ter explicação histórica, geográfica ou social.

## 9. Facções e conflitos

O mundo deve conter organizações que possam atravessar fronteiras: guildas e ordens de aventureiros; ordens religiosas e escolas de magia; companhias mercenárias; redes comerciais e famílias influentes; sociedades secretas e organizações criminosas; tribos, clãs, confederações e movimentos políticos; grupos que protegem ou disputam ruínas e locais antigos.

Cada facção precisa de ID, objetivos, liderança, recursos, métodos, aliados, rivais, presença geográfica e estado atual. Os conflitos devem ter causas concretas: recursos, sucessão, fronteiras, crenças, segurança, comércio ou rivalidades pessoais. Nem todo conflito precisa ser uma guerra aberta.

## 10. Campanhas e cânone

O sistema deve oferecer três modos:

1. **Cânone compartilhado:** campanhas diferentes alteram o mesmo mundo. Mortes, sucessões, guerras e destruição podem afetar campanhas posteriores.
2. **Linha do tempo alternativa:** campanhas partem do mesmo cenário-base, mas suas mudanças ficam isoladas.
3. **Cópia independente:** o jogador cria uma versão própria sem modificar o mundo original.

A interface deve indicar claramente o modo escolhido. Cada campanha precisa guardar sua linha do tempo e as mudanças realizadas. Uma campanha alternativa não pode alterar silenciosamente o estado da campanha compartilhada.

## 11. Memória histórica e NPCs recorrentes

A memória persistente deve registrar quem participou de cada evento, o que aconteceu, quando e onde, quem presenciou, quem descobriu depois e quais foram as consequências. Isso inclui favores, promessas, dívidas, humilhações, derrotas, traições, alianças, mortes e rivalidades. O sistema deve distinguir fatos confirmados, suspeitas e rumores.

Exemplo: um personagem humilha um meio-orc meio-anão. Várias campanhas depois, ele se torna rei dos anões e exige uma reparação. Não precisa ser vilão: pode querer devolver a humilhação. A reação deve refletir sua personalidade, sua história e os recursos que realmente possui.

NPCs podem evoluir fora de cena, conquistar posições, perder poder, formar alianças e buscar objetivos próprios. Entretanto, o sistema não deve lhes conceder poder ilimitado nem fazê-los aparecer em qualquer lugar sem uma explicação plausível.

## 12. Geração de aventuras

A IA deve usar os dados existentes como base para criar aventuras relacionadas a disputas entre reinos, sucessões, desaparecimentos, intrigas militares, rotas interrompidas, ruínas, cavernas, vulcões, artefatos, dívidas e reencontros com NPCs antigos. Novos conteúdos podem ser criados, mas os fatos que passarem a integrar o cânone devem ser registrados. A IA não deve contradizer fatos confirmados nem atribuir conhecimento a um NPC sem uma fonte plausível.

## 13. Requisitos funcionais

- [ ] Criar e selecionar um mundo pré-configurado com nome próprio.
- [ ] Garantir pelo menos 5 reinos e 10 cidades por reino.
- [ ] Criar elenco principal para cada reino, com governantes, herdeiros, heróis e generais.
- [ ] Dar IDs permanentes a mundos, reinos, cidades, regiões, NPCs e facções.
- [ ] Definir geografia, biomas, rotas e recursos coerentes.
- [ ] Definir territórios de origem e concentração dos povos, preservando migrações e diversidade.
- [ ] Registrar facções, alianças, conflitos, fronteiras e rotas comerciais.
- [ ] Definir estado inicial e linha do tempo canônica.
- [ ] Permitir campanhas compartilhadas, alternativas e cópias independentes.
- [ ] Persistir alterações entre campanhas quando o modo compartilhado estiver ativo.
- [ ] Integrar NPCs e eventos à memória persistente.
- [ ] Gerar aventuras a partir dos dados do mundo sem contradizer o cânone.
- [ ] Validar nomes duplicados, cidades sem reino, rotas inválidas e relações contraditórias.

## 14. Critérios de validação

1. Existem pelo menos cinco reinos e dez cidades válidas por reino.
2. Cada cidade tem reino, localização, função e conexões coerentes.
3. Cada reino tem governantes e NPCs com objetivos e relações definidos.
4. Heróis e generais têm recursos, capacidades e limitações próprias.
5. A geografia é consistente o suficiente para orientar viagens e aventuras.
6. Territórios de povos não impedem migração nem diversidade cultural.
7. NPCs recorrentes preservam identidade e memória entre campanhas do mesmo cânone.
8. Campanhas alternativas não contaminam a campanha compartilhada.
9. Eventos novos são registrados sem apagar silenciosamente o histórico.
10. A IA consegue gerar aventuras usando cidades, NPCs, facções e regiões existentes.
11. NPCs não sabem fatos que não presenciaram nem descobriram.
12. O sistema recupera apenas os dados relevantes para cada cena, sem enviar o mundo inteiro em cada chamada à IA.

## 15. Diretriz de implementação

Antes de implementar, auditar os sistemas existentes de NPCs, relações, locais, agendas, memória, linha do tempo, campanhas, mapas e geração de aventuras. Reutilizar estruturas já existentes e implementar apenas lacunas reais. O código e os dados persistidos são a fonte de verdade. A IA deve consultar os dados relevantes, propor mudanças e registrar alterações aceitas. Não deve inventar silenciosamente um novo rei, cidade ou guerra que contradiga o estado persistido.

Para evitar um mundo composto apenas de nomes, todas as cidades devem ter dados estruturados e conexões coerentes. A expansão narrativa detalhada pode ser priorizada para as regiões e NPCs mais importantes para a campanha inicial, sem deixar os demais registros inválidos ou vazios.

**Resultado esperado:** um mundo pronto para jogar, com pelo menos cinco reinos, cinquenta cidades, elenco de personagens importantes, geografia, povos e conflitos interligados, capaz de evoluir ao longo de muitas campanhas.
