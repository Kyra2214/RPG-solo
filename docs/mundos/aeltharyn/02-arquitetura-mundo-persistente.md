# Arquitetura narrativa do mundo persistente

**Status:** princípio canônico de projeto; independente da implementação do aplicativo.  
**Aplica-se a:** todos os cinco reinos e a todas as campanhas que compartilhem o mesmo universo persistente.  
**Regra central:** o mundo continua existindo e mudando sem jogadores e sem exigir que o Mestre carregue ou conheça cada reino. O mundo pode saber coisas que o Mestre ainda não precisa consultar; o Mestre pode conhecer fatos que o personagem desconhece; o personagem só sabe aquilo que descobriu.

## 1. Separar simulação, consulta e narração

O ecossistema é a fonte persistente do estado do mundo. Ele mantém reinos, cidades, assentamentos, governantes, NPCs, relações, recursos, instituições, eventos, conflitos e consequências. A campanha não é dona exclusiva do mundo: é uma perspectiva situada dentro dele.

O Mestre não recebe atualizações automáticas de todos os reinos e não precisa pesquisar um reino irrelevante. A consulta é acionada por uma necessidade concreta: personagem viajando para lá, NPC vindo de lá, notícia que afeta a cena, consequência que alcança o local da campanha, missão, investigação ou pedido explícito de aprofundamento.

Não consultar um reino **não pausa sua existência**. Também não significa que todos os seus detalhes devam ser simulados individualmente a cada segundo.

## 2. Avanço do mundo em segundo plano

A simulação pode avançar em ciclos temporais, eventos agendados e processamento por consequência. Deve priorizar mudanças relevantes e manter causas rastreáveis, sem precisar executar uma IA independente para cada habitante continuamente.

Cada mudança importante deve registrar, conforme aplicável:
- entidade e local afetados;
- estado anterior e novo estado;
- momento do mundo em que ocorreu;
- causa ou conjunto de causas;
- agentes envolvidos e suas motivações;
- efeitos imediatos e possíveis efeitos futuros;
- quais entidades sabem do fato e por qual meio;
- grau de certeza: confirmado, alegado, rumor, inferência ou desconhecido.

A passagem do tempo deve ser consistente entre campanhas que compartilham o mesmo universo. Uma campanha que passa três meses viajando não pode retornar a um reino congelado no estado de sua última visita. A política exata de sincronização entre sessões/campanhas será definida pelo motor do mundo; este documento estabelece o requisito narrativo, não presume que já exista implementação técnica.

## 3. Consulta sob demanda e contexto mínimo

Quando algo tornar um reino relevante, o sistema consulta o estado atual e entrega ao Mestre apenas o contexto suficiente para conduzir a situação. A consulta cresce em profundidade conforme a necessidade:

1. **Referência mínima:** nome, localização, estado atual resumido e ligações relevantes.
2. **Resumo operacional:** governo atual, conflitos ativos, NPCs relevantes, riscos, relações e mudanças desde o último registro conhecido.
3. **Dossiê local:** cidade, assentamento, facções, personagens, economia, leis, segurança e detalhes necessários à cena.
4. **Arquivo aprofundado:** cronologia, tratados, genealogias, registros, mapas, versões históricas e documentos privados, quando a ação ou a investigação exigir.

Esses níveis não são mensagens periódicas. São profundidades de consulta. Se um reino nunca se tornar relevante durante uma campanha, não há obrigação de carregar sua lore para o contexto do Mestre.

## 4. Cinco camadas de informação

| Camada | Significado |
|---|---|
| **Estado real do mundo** | O que realmente aconteceu, inclusive eventos secretos e fatos que ninguém compreendeu. |
| **Registro persistente** | Eventos, estados, fontes e evidências que o sistema conserva para continuidade. |
| **Conhecimento de cada NPC** | O que aquele indivíduo sabe, suspeita, interpreta errado ou desconhece, de acordo com sua experiência e fontes. |
| **Contexto do Mestre** | O subconjunto consultado para resolver a situação atual, sem exigir conhecimento enciclopédico de todo o universo. |
| **Conhecimento do personagem/jogador** | O que foi descoberto em jogo. Não é atualizado automaticamente só porque o mundo ou o Mestre possuem a informação. |

Uma informação registrada não é necessariamente pública. Um evento pode ter ocorrido sem testemunhas; um relatório pode ter sido falsificado; duas fontes podem discordar. A história oficial, os arquivos locais e a verdade dos acontecimentos não precisam ser idênticos.

## 5. Segredos, arquivos e investigação

Bibliotecas, arquivos de guildas, templos, cartórios, casas nobres, registros militares e acervos privados são locais de acesso ao conhecimento do mundo. A profundidade e a confiabilidade dos registros variam por instituição, época, autoria, censura, conservação e acesso.

Uma missão de infiltração pode depender de obter um documento específico: o mundo conserva sua existência e sua localização atual; o Mestre consulta as regras de acesso, segurança, responsáveis, rotinas e consequências relevantes quando a missão passa a existir. O personagem ainda precisa descobrir como chegar ao documento e como obtê-lo.

Não revelar um segredo não significa que ele não exista. Revelá-lo ao Mestre para conduzir uma cena tampouco significa revelá-lo ao jogador.

## 6. Consequências compartilhadas entre campanhas

Campanhas no mesmo universo persistente consultam o mesmo estado canônico, salvo quando forem explicitamente declaradas como linhas temporais, instâncias ou universos separados. Mortes, sucessões, guerras, acordos, escassez, mudanças de rota e outros eventos persistem segundo as regras de sincronização.

Uma campanha não deve reescrever silenciosamente o passado compartilhado para acomodar seu enredo. Se houver divergência, ela precisa ser tratada como conflito de continuidade, linha temporal separada ou alteração canônica explícita.

## 7. Autonomia e limites dos NPCs

NPCs perseguem objetivos compatíveis com seus recursos, crenças, vínculos, informações e limites. Eles não sabem tudo, não agem apenas para entregar missões ao protagonista e podem falhar. O estado do mundo não deve ser confundido com onisciência de todos os seus habitantes.

A simulação deve distinguir:
- objetivo do NPC;
- plano que ele escolheu;
- recursos disponíveis;
- informação de que dispõe;
- obstáculos e oposição;
- resultado efetivo;
- interpretação que ele faz do resultado.

Um NPC pode agir fora de cena, mas sua ação não deve produzir automaticamente sucesso garantido. Causas, oportunidades e consequências precisam ser plausíveis.

## 8. Regras de escopo e desempenho

- Não carregar os cinco reinos inteiros no contexto de cada turno.
- Não exigir que o Mestre conheça reinos que nunca foram relevantes à campanha.
- Não simular cada habitante continuamente se um ciclo ou evento for suficiente.
- Não inventar retrospectivamente uma história incompatível quando um local for visitado; recuperar o estado persistente e gerar somente os detalhes ainda não definidos, respeitando o cânone.
- Não confundir ausência de consulta com ausência de mudança.
- Não expor segredos ao personagem sem um caminho de descoberta.
- Não considerar o ecossistema concluído apenas porque os textos de lore foram escritos.
- Não misturar esta fase editorial com a integração ao aplicativo. O contrato técnico e a implementação do motor serão etapas próprias, posteriores à criação e revisão dos cinco reinos.

## 9. Exemplo canônico

Um personagem humano vive toda a campanha no Pacto Humano. Aeltharyn continua avançando em segundo plano; nem o Mestre nem o personagem precisam carregar ou pesquisar a lore élfica. No final da campanha, um elfo chega pedindo ajuda e a missão leva o grupo ao Primeiro Verde. Só então o sistema consulta o estado atual de Aeltharyn e entrega o contexto necessário. Se o grupo decidir invadir uma biblioteca, o Mestre aprofunda os arquivos, a segurança e os documentos relevantes. O reino não é criado do zero na chegada: é consultado no estado em que se encontra naquele momento.

## 10. Critérios de aceitação conceituais

- [ ] É possível deixar um reino sem consulta durante uma campanha sem interromper sua evolução.
- [ ] Ao consultar esse reino depois, o estado retornado reflete o tempo e os eventos decorridos.
- [ ] A consulta inicial é mínima e pode ser aprofundada por demanda.
- [ ] Verdade, registro, conhecimento de NPC, contexto do Mestre e conhecimento do personagem são camadas distintas.
- [ ] Segredos e documentos restritos não se tornam automaticamente conhecimento do personagem.
- [ ] Campanhas do mesmo universo compartilham mudanças canônicas conforme a política de sincronização.
- [ ] A implementação técnica só será considerada validada quando houver testes específicos; este documento, por si só, não afirma que ela já existe.
