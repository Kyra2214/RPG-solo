# Aeltharyn — Modelo operacional e geração de aventuras

**Estado:** contrato editorial operacional; ainda não é implementação do motor nem integração ao APK.  
**Escopo:** transformar o cânone de Aeltharyn em entradas consultáveis para uma LLM pequena e em regras que gerem situações, decisões e aventuras sem contradizer o estado persistente.  
**Princípio:** a lore descreve o mundo; o modelo operacional explica como fatos do mundo podem causar mudanças e como essas mudanças podem chegar ao personagem.

## 1. Objetivo

Aeltharyn deve produzir aventuras a partir de problemas e decisões que já fazem sentido no reino. A IA não deve depender exclusivamente de uma lista fixa de missões, nem inventar crises apenas para manter o jogador ocupado.

Uma aventura pode nascer de:
- uma necessidade material não atendida;
- uma obrigação política descumprida;
- um conflito de interesses;
- uma ameaça ecológica ou de segurança;
- uma informação incompleta ou falsificada;
- uma oportunidade criada por uma mudança anterior;
- uma decisão do próprio jogador que alterou relações, recursos ou instituições.

O mundo não precisa transformar cada mudança em missão. Algumas consequências são apenas ambiente, notícia, custo ou obstáculo. A aventura aparece quando existe uma decisão significativa que o personagem pode tomar.

## 2. Separar quatro objetos

### 2.1 Fato do mundo

Algo que aconteceu ou está acontecendo, independentemente de alguém saber. Deve possuir, quando aplicável:
- ID estável;
- momento e local;
- entidades envolvidas;
- estado anterior e posterior;
- causa conhecida ou hipótese causal;
- evidências disponíveis;
- grau de certeza;
- efeitos diretos e efeitos ainda possíveis.

### 2.2 Estado de entidade

Retrato atual de uma cidade, assentamento, facção, NPC, rota, recurso, instituição ou conflito. O estado deve poder mudar sem reescrever toda a descrição canônica.

Exemplos: uma comporta está danificada; uma rota está temporariamente interditada; um governante perdeu apoio; um estoque de remédios está baixo.

### 2.3 Conhecimento

Uma entidade só pode agir com base no que sabe, suspeita ou interpreta. Cada informação relevante precisa distinguir:
- fato confirmado;
- testemunho atribuído;
- documento ou evidência;
- rumor;
- inferência;
- informação falsa;
- desconhecido.

O sistema deve registrar a origem e o caminho de transmissão. Conhecimento não se propaga instantaneamente para todo o reino.

### 2.4 Oportunidade de aventura

Uma oportunidade é uma situação jogável derivada de fatos e estados existentes. Não é, por si só, um acontecimento canônico. Ela pode ser oferecida, descoberta ou ignorada.

Campos conceituais mínimos:
- ID;
- situação que a originou;
- quem precisa de algo e por quê;
- objetivo possível;
- local e janela temporal;
- pré-requisitos;
- riscos e oposição plausíveis;
- recompensa ou consequência esperada;
- alternativas de abordagem;
- informações que o personagem pode obter;
- condições de expiração, resolução ou transformação;
- consequências no mundo caso seja aceita, recusada ou ignorada.

## 3. Cadeia causal obrigatória

Use esta cadeia como padrão de raciocínio:

**condição anterior → evento/gatilho → entidade exposta → reação dos agentes → consequência material ou política → informação transmitida → oportunidade de decisão.**

Nem toda cadeia chega ao fim. Um evento pode ser contido, não produzir efeito observável, ser interpretado incorretamente ou não chegar ao personagem. Cada elo precisa ser plausível e sustentado pelo cânone ou marcado explicitamente como hipótese.

Exemplo conceitual: um reparo de dique é adiado. Isso não significa que Nymvar inteiro será inundado. Primeiro é necessário identificar o trecho, a condição da estrutura, o nível da água, os trabalhadores disponíveis e as áreas expostas. Só então podem surgir efeitos como uma travessia interrompida, sementes ameaçadas, uma disputa sobre financiamento ou uma missão de escolta de materiais.

## 4. Como uma situação vira aventura

1. **Detectar:** identificar mudança relevante em um relógio, agenda, entidade, relação ou ação do jogador.
2. **Validar:** confirmar que o evento ocorreu ou manter explícito que se trata de rumor/hipótese.
3. **Resolver alcance:** identificar quais lugares, grupos, rotas e recursos têm conexão causal com o evento.
4. **Modelar agentes:** listar quem tem interesse, capacidade, informação e autoridade para reagir.
5. **Gerar opções:** considerar respostas concorrentes, incluindo cooperação, investigação, negociação, força, exploração e inação.
6. **Filtrar:** remover opções que exigem conhecimento impossível, recursos inexistentes, viagens inviáveis ou fatos ainda não estabelecidos.
7. **Apresentar:** mostrar ao personagem apenas o que ele poderia descobrir por uma fonte plausível.
8. **Aguardar decisão:** não considerar uma oportunidade aceita apenas porque foi narrada.
9. **Resolver:** aplicar resultados proporcionais às ações, recursos, oposição e incerteza.
10. **Persistir:** atualizar estado, evento, relações, relógios e conhecimento de cada participante separadamente.

A IA pode sugerir possibilidades durante o raciocínio, mas a sugestão não altera o cânone até ser resolvida por uma ação, regra ou evento validado.

## 5. Fontes de oportunidades

| Fonte | Exemplo em Aeltharyn | Oportunidades possíveis |
|---|---|---|
| Necessidade material | falta de material para reparar um dique | obter material, negociar fornecimento, proteger transporte |
| Rota ou viagem | passagem fechada por risco sazonal | encontrar desvio, contratar guia, investigar interdição |
| Instituição | obrigação de uma cidade contestada | mediar, reunir provas, defender uma parte, expor abuso |
| Mercado | carga suspeita de resina | rastrear origem, fiscalizar, escoltar, comprar informação |
| Ecologia | árvore-coração piorando | coletar amostras, proteger viveiro, comparar hipóteses |
| Segurança | desaparecimento ou ataque confirmado | buscar sobreviventes, investigar local, proteger alvos |
| Informação | documento contradiz a versão oficial | autenticar registro, localizar testemunha, acessar arquivo |
| Relação pessoal | NPC precisa de ajuda e tem seus próprios limites | favor, aliança, dívida, conflito ou recusa |
| Ação do jogador | jogador expõe um nobre ou bloqueia uma rota | retaliação, apoio público, processo, oportunidade econômica |
| Calendário | inspeção, colheita ou festival sazonal | trabalho temporário, preparação, disputa, emergência |

A tabela apresenta tipos de origem, não afirma que todos esses acontecimentos estejam ocorrendo agora.

## 6. Agentes e decisões plausíveis

Para cada agente importante, consultar apenas os atributos documentados que forem relevantes:
- objetivo atual;
- interesses e obrigações;
- recursos e limitações;
- autoridade formal e legitimidade local;
- relações e compromissos;
- informações disponíveis e fontes;
- tolerância a risco;
- ações possíveis;
- impedimentos e custo provável de agir.

Não é necessário simular cada habitante individualmente. Para questões cotidianas, podem ser usados grupos, instituições ou indicadores locais. NPCs nomeados ganham resolução individual quando a cena ou a consequência os torna relevantes.

Um objetivo não garante ação ou sucesso. Um agente pode não ter recursos, desconhecer a oportunidade, enfrentar oposição, escolher outra prioridade ou falhar.

## 7. Relógios: uso sem automatismo

Os sete relógios iniciais são indicadores de tensão narrativa, não dados que avançam só porque o tempo passou.

- Doença das árvores-coração (2/6)
- Dívida dos diques (3/6)
- Contrabando de resina (2/6)
- Desconfiança entre cidades (1/6)
- Navio sem tripulação (1/6)
- Mina selada de Torvelume (1/6)
- Corredores centauros (2/6)

Antes de mudar um relógio, aplicar o procedimento de auditoria do documento 05: identificar gatilho, verificar nexo, mapear exposição, avaliar alternativas, atualizar conhecimento separadamente, aplicar mudança proporcional e registrar a próxima decisão.

Um avanço de relógio não determina sozinho uma consequência específica. Ele indica que a situação mudou de escala; o efeito concreto depende do lugar, dos recursos, das ações e das condições existentes. Um mesmo relógio pode avançar sem gerar uma missão imediata.

## 8. O mundo pode responder à inação

Quando o jogador ignora uma oportunidade, o mundo não deve ficar parado nem punir automaticamente o jogador. O resultado depende do que os outros agentes conseguem fazer.

Possíveis resultados:
- outro agente resolve o problema;
- a situação piora por falta de resposta;
- o conflito muda de forma;
- a oportunidade expira;
- alguém assume o mérito ou a culpa;
- o problema permanece sem solução;
- a ausência do jogador não faz diferença relevante.

A resolução deve considerar recursos, tempo, oposição e informação. Não presumir que o mundo sempre piora para pressionar o jogador.

## 9. Resultado e persistência

Depois de uma ação significativa, registrar apenas mudanças justificadas. A atualização deve separar:
1. resultado observado;
2. interpretação dos agentes;
3. alteração material ou institucional;
4. alteração de relações;
5. alteração de relógios, se sustentada;
6. novos conhecimentos e seus destinatários;
7. evidências criadas ou destruídas;
8. próximo evento ou decisão possível.

A narrativa em linguagem natural é a apresentação do resultado, não a única fonte do estado. Para a implementação futura, os resultados devem ser representáveis por campos estruturados e verificáveis, com a LLM propondo e explicando mudanças sob validação das regras do mundo.

## 10. Contrato de contexto para LLM pequena

A LLM não deve receber o documento inteiro de Aeltharyn em todo turno. A consulta deve construir um pacote pequeno e suficiente para a tarefa.

### 10.1 Contexto base

- regras globais indispensáveis;
- resumo da localização atual;
- data/estação e condições locais relevantes;
- estado atual do personagem e objetivos ativos;
- eventos recentes que possam afetar a cena.

### 10.2 Contexto recuperado por necessidade

- perfis de NPCs que participam da cena;
- relações e obrigações relevantes;
- rotas e durações já documentadas;
- estado dos recursos envolvidos;
- leis e instituições aplicáveis;
- evidências e versões conhecidas do fato;
- histórico das decisões do jogador relacionadas ao caso.

### 10.3 Contexto que deve permanecer fora, salvo necessidade

- perfis completos de cidades sem relação com a tarefa;
- genealogias e cronologias sem conexão causal;
- segredos que o personagem não descobriu, quando a tarefa for narrar a perspectiva dele;
- documentos de outros reinos que não afetem a situação;
- conteúdo duplicado que possa ser substituído por IDs e resumos.

A recuperação precisa priorizar relevância causal, localização, tempo, participantes e objetivo da consulta, não apenas semelhança de palavras. Uma busca por “diques” deve poder recuperar rotas, sementes, autoridades, clima e assentamentos expostos quando essas relações forem importantes.

## 11. Proteção contra invenção e contradição

Antes de aceitar uma proposta da LLM, verificar:
- os locais e entidades existem ou foram explicitamente criados como conteúdo novo permitido?
- a cronologia é compatível?
- o agente poderia saber disso?
- o agente tem meios e autoridade para agir?
- a rota e o tempo de viagem são conhecidos e plausíveis?
- o efeito material respeita a exposição e as alternativas?
- a proposta transforma uma hipótese em fato sem evidência?
- a consequência contradiz estado persistente ou decisão anterior?
- o segredo está sendo revelado sem caminho de descoberta?
- a recompensa existe e pode ser entregue?

Se a resposta não puder ser confirmada, a IA deve reduzir a precisão, manter a incerteza ou solicitar aprofundamento. Não preencher lacunas com números exatos ou fatos definitivos apenas para fazer a cena fluir.

## 12. Exemplo de geração com os relógios atuais

**Ponto de partida documentado:** a dívida dos diques está em 3/6; Nymvar possui manutenção atrasada; Junco Alto e Três Barcas estão associados à cidade; Arven participa da cadeia de alimentos e sementes.

**O que ainda precisa ser consultado antes de narrar uma crise:** qual dique ou comporta está em risco, inspeção mais recente, nível de água, material disponível, prazo de reparo, campos ou travessias expostos, autoridade responsável e rotas alternativas. A auditoria atual ainda não fixa todos esses dados.

**Oportunidades possíveis, condicionadas à evidência:**
- escoltar materiais de reparo, se houver fornecimento confirmado;
- investigar desvio de verba, se houver discrepância documental;
- negociar prioridade entre comunidades, se a distribuição for disputada;
- encontrar rota alternativa, se a travessia estiver realmente ameaçada;
- documentar risco e convencer a autoridade, se faltarem provas ou apoio.

**Não autorizado sem evidência:** inundar toda a cidade, declarar sabotagem, acusar uma facção ou avançar automaticamente o relógio. Se nenhum gatilho concreto estiver confirmado, a IA pode apresentar sinais, perguntas ou uma investigação preliminar, mas não uma catástrofe como fato consumado.

## 13. Critérios de aceitação editorial

- [ ] Cada relógio tem gatilhos verificáveis, relações causais e limites de inferência.
- [ ] Cada conflito material relevante identifica locais expostos, agentes responsáveis e alternativas conhecidas.
- [ ] Oportunidades podem ser aceitas, recusadas, ignoradas, resolvidas por terceiros ou transformadas.
- [ ] A inação do jogador produz consequências proporcionais, não punição automática.
- [ ] NPCs agem conforme objetivos, recursos, autoridade e conhecimento.
- [ ] A IA distingue fato, hipótese, rumor e desconhecido.
- [ ] O contexto recuperado para uma LLM pequena é mínimo, causalmente relevante e aprofundável.
- [ ] Mudanças do mundo podem ser registradas estruturadamente sem depender apenas da prosa.
- [ ] Segredos e notícias seguem caminhos de descoberta e transmissão plausíveis.
- [ ] As lacunas de rotas, demografia e reservas apontadas no documento 05 permanecem explícitas até serem resolvidas.
- [ ] Exemplos condicionais não são confundidos com novos fatos canônicos.
- [ ] O modelo editorial é revisado contra as dez cidades, vinte assentamentos, instituições, NPCs e sete relógios antes de ser declarado concluído.

## 14. Limites e próxima etapa

Este documento define um contrato de funcionamento narrativo para orientar a revisão de Aeltharyn e a futura implementação. Não define banco de dados, formato de serialização, algoritmo de busca, arquitetura de software ou integração ao APK. Esses detalhes devem ser especificados quando a fase técnica começar.

A próxima etapa editorial é preencher as lacunas operacionais de Aeltharyn: rotas e tempos coerentes, jurisdições populacionais, escalas de reservas, agentes responsáveis e gatilhos observáveis. Não desenvolver os quatro reinos restantes até que o primeiro reino tenha sido auditado e aprovado.
