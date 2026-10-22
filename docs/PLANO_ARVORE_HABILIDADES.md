# Plano de implementação — Árvores de habilidades, afinidades e maldições

**Versão:** 1.2  
**Status:** plano aprovado para implementação code-first.  
**Escopo:** sistema próprio do RPG Solo.  
**Princípio obrigatório:** preservar a fórmula de ataque e o motor de combate já existentes. Não substituir por uma fórmula nova sem auditoria do código e dos pacotes.

## Decisões fechadas

1. Personagem começa no nível 1.
2. Recebe 12 pontos iniciais, distribuídos livremente entre atributos e habilidades.
3. A cada nível recebe +1 ponto de atributo e +1 ponto de habilidade.
4. Escolher raça/sub-raça, classe, profissão ou combinação híbrida não cobra pontos.
5. Toda raça e sub-raça tem uma habilidade básica própria gratuita; evoluir custa pontos.
6. A habilidade básica da Classe de Malditos também é gratuita; evoluí-la custa pontos.
7. Habilidades adicionais de classe e profissão custam 1 ponto para desbloquear, respeitando requisitos.
8. Não há limite fixo global de nível de habilidade. Cada evolução deve ter custo e efeito mecânico explícitos; progressão contínua não autoriza a IA a inventar valores.
9. Cada profissão e vertente de Classe de Malditos tem sete habilidades principais como padrão. Podem misturar ataque, defesa, buff, suporte, controle, utilidade e passivas.
10. As habilidades da mesma árvore aumentam de poder progressivamente. A sétima é a habilidade culminante e deve ser substancialmente mais poderosa, coerente com a classe (por exemplo, ressurreição para Sacerdote, tiro devastador para Arqueiro, sequência de golpes para Espadachim). Efeitos extremos precisam de regras e custos/restrições explícitos.
11. Pergaminhos, com preços em prata:
    - habilidade 1: 200 prata
    - habilidade 2: 2.000 prata
    - habilidade 3: 10.000 prata
    - habilidade 4: 15.000 prata
    - habilidade 5: 20.000 prata
    - habilidade 6: 30.000 prata
    - habilidade 7: 50.000 prata
    - conversão: 100 prata = 1 ouro.
12. Preço do item de reset: 25.000 prata (250 ouro). O reset devolve apenas os pontos investidos elegíveis; habilidades básicas gratuitas permanecem.
13. Personagem híbrido pode combinar até quatro classes. Quanto mais classes misturadas, mais monstruosa/estranha a aparência; os impactos narrativos e mecânicos devem ser explícitos e não inventados durante a narração.
14. Maldições lançadas são um catálogo separado. Não considerar que já existem 100 maldições prontas: o número 100 referido antes era conteúdo da Classe de Malditos. As maldições lançadas precisam ser criadas.
15. Classe de Malditos é uma identidade/classe com famílias e vertentes próprias (vampiros, lobisomens, necromantes, magos negros, demônios, espectros e outras); não é a mesma coisa que receber uma maldição.
16. Caminhos elementais definidos: Água, Fogo, Vento, Terra, Metal, Yin e Yang. Luz pode ser tratada como expressão de Yang, sem criar um oitavo caminho implicitamente.
17. Afinidade restringe habilidades mágicas; classe define estilo/técnica; arma define compatibilidade física. Exceções híbridas precisam estar declaradas em dados.
18. Passivas podem alterar defesa, velocidade, precisão, percepção, resistências, recuperação e afinidade. Passivas equivalentes não acumulam; vale o efeito mais forte. Efeitos diferentes podem coexistir se compatíveis.
19. Bônus de raça, classe, profissão, equipamento, buffs e condições permanecem em camadas separadas e só são aplicados uma vez.
20. Jogador e IA usam o mesmo motor determinístico. A IA escolhe/narra, mas não inventa dano, custo, duração, requisito ou resultado.
21. Não substituir a fórmula de ataque já existente. Primeiro identificar a fórmula real no código, nos perfis de dano e nos pacotes; adaptar apenas lacunas necessárias.

## Referências do projeto a preservar

- O pacote próprio declara o teste `ataque` em `rules/src/main/resources/packages/sistema-proprio/testes.txt` com d20, comparação `ROLL_OVER`, atributo `poder` e críticos configurados no pacote.
- `dano.txt`, atributos, traços, condições, recursos e os resolvedores do motor são fonte da verdade para o combate atual.
- O formato atual de `poderes.txt` suporta efeitos tipados limitados (cura, dano por perfil, condição e remoção de condição). Novos efeitos deverão ser adicionados ao modelo/parser/motor com validação; não simular efeitos complexos apenas em texto.
- O catálogo `personagem-catalogo.json` é atualmente parcialmente editorial/integrado. Uma descrição não concede um efeito mecânico sem suporte tipado.
- Antes de alterar combate, ler `PLANO_RPG_SOLO.md`, `docs/MECANICA_DADOS.md`, `docs/pacotes/FORMATO_PACOTE.md` e os resolvedores reais.

## Dados necessários por habilidade

Cada habilidade deve declarar ID estável, nome, origem, categoria, afinidade, compatibilidade com arma/alvo, requisito de nível/pré-requisitos, custo de desbloqueio, custo de evolução, nível atual, parâmetros por nível, custo de uso/recurso, alcance, duração/recarga quando aplicáveis, efeitos tipados, regras de acúmulo e método de aquisição (básica gratuita, pontos, missão ou pergaminho).

Evolução sem teto fixo não significa crescimento implícito infinito: cada nível deve ser calculado por uma curva/fórmula explícita, versionada e testável. Nenhum número pode ser improvisado pela IA. Efeitos excepcionais (ressurreição, ataques de dano extremo) precisam de regras de elegibilidade, resistência/defesa, custo e consequências.

## As 17 fases de implementação

O trabalho é code-first. Deixar a suíte completa de testes para a fase 17, mas cada mudança deve permanecer compilável e consistente por inspeção; não declarar validação sem executar.

1. **Auditoria do combate existente:** rastrear fórmula real de ataque, dano, defesa, crítico, equipamento, resistência, efeitos e paridade IA/jogador. Registrar pontos de extensão; não mudar fórmulas.
2. **Modelo de habilidade:** modelar ID, origem, categoria, afinidade, requisitos, aquisição, custos, evolução e efeitos tipados sem quebrar pacotes existentes.
3. **Modelo de árvore e catálogo:** árvores de raça/sub-raça, classe, profissão, híbridos e Classe de Malditos; IDs estáveis e compartilhamento de habilidades.
4. **Economia de pergaminhos:** implementar tabela de preços em prata e conversão 100:1; compra deve validar saldo e desbloqueio.
5. **Progressão de personagem:** integrar 12 pontos iniciais e ganhos por nível (+1 atributo, +1 habilidade) ao fluxo real da ficha e persistência, sem duplicar sistemas de pontos já existentes.
6. **Desbloqueio e evolução:** custo de 1 ponto para habilidades adicionais de classe/profissão; evolução sem teto global fixo, com curva explícita, requisitos e saldo validado.
7. **Habilidades raciais básicas:** uma habilidade gratuita para cada raça/sub-raça; upgrades custam pontos; efeitos podem ser passivos, defensivos, buffs, utilitários ou ofensivos.
8. **Afinidades elementais:** validar Água, Fogo, Vento, Terra, Metal, Yin e Yang; mapear Luz a Yang quando declarado; rejeitar incompatibilidades não autorizadas.
9. **Tipos de efeito:** ofensiva, defesa, buff, suporte/controle, passiva e utilitária; ampliar parser e motor apenas com efeitos tipados executáveis.
10. **Passivas e acúmulo:** categorias de efeito, regra de maior efeito equivalente, coexistência de efeitos distintos e origem de cada modificador para evitar duplicação.
11. **Árvores de classe e profissões:** classe com técnica/magia distintiva; sete habilidades por profissão como padrão; compartilhamento de bases sem cópias de lógica.
12. **Habilidades culminantes:** sétima habilidade da árvore com maior impacto; suporte a efeitos complexos por regras explícitas, sem narrar ressurreição/ataques extremos sem validação mecânica.
13. **Híbridos até quatro classes:** catálogo de combinações permitidas, requisitos, heranças/despertares de habilidades e gradação de aparência/efeitos conforme quantidade de classes.
14. **Classe de Malditos:** famílias, vertentes e árvores separadas das maldições lançadas; habilidade básica gratuita.
15. **Catálogo de maldições lançadas:** criar maldições próprias com origem, alvos, duração, resistência, remoção, efeitos positivos/negativos e regras tipadas; não presumir que há 100 prontas.
16. **Interface e persistência:** mostrar árvores, requisitos, afinidade, pontos, custos, níveis, pergaminhos, efeitos, reset e histórico; salvar e restaurar estado corretamente.
17. **Validação final:** somente nesta fase executar a suíte de testes relevante, compilar app, corrigir falhas, testar migrações/persistência e verificar paridade IA/jogador. Relatar precisamente o que passou e o que não foi executado.

## Reset

O item custa 25.000 prata. O escopo precisa estar explícito (habilidade, árvore ou todos os investimentos elegíveis). Não pode gerar pontos; devolve apenas os pontos investidos no escopo e preserva escolhas/benefícios gratuitos de origem.

## Critérios de conclusão

- Fórmula original de combate preservada e documentada.
- Pontos iniciais e ganhos por nível aplicados exatamente uma vez.
- Aquisição, pergaminhos, evolução e reset validados pelo motor.
- Habilidades raciais e de Classe de Malditos básicas gratuitas.
- Afinidade e compatibilidade validadas.
- Efeitos reais e tipados, não apenas descrições.
- Passivas equivalentes sem acumulação indevida.
- Árvores e catálogo persistidos; nenhuma compra duplica saldo ou habilidade.
- Habilidades finais mais poderosas, com contrapesos mecânicos definidos.
- Híbridos até quatro classes com consequências explícitas.
- Catálogo de maldições separado e realmente criado.
- Jogador e IA sujeitos às mesmas regras.
- Testes e build executados apenas na fase 17; resultados comunicados sem alegar sucesso não verificado.

## Atualização de auditoria documental — 2026-10-22

Este bloco complementa o status anterior. Ele **não** declara as 17 fases concluídas; os itens abaixo são alterações feitas na branch e ainda precisam de validação automatizada integrada.

### Alterações recentes registradas
- O catálogo de habilidades de classe foi revisado para distinguir as seis primeiras habilidades de 50 árvores de classe (300 registros), com categorias/efeitos tipados e restrição de afinidade declarada para as árvores mágicas identificadas. A sétima habilidade continua sendo a culminância e deve ser auditada separadamente para confirmar que o efeito realmente é excepcional.
- As 103 culminâncias de profissão receberam nomes distintos e efeitos voltados ao ofício; profissões não combatentes usam principalmente passivas de perícia/ofício em vez de serem forçadas a ter ataques. As seis primeiras posições de 103 árvores de profissão também foram ajustadas por perfil de ofício. Isso é conteúdo de catálogo, não prova de equilíbrio ou de integração funcional.
- O catálogo separado `rules/src/main/resources/catalogs/sistema-proprio/maldicoes-lancadas.txt` foi expandido de 20 para 100 maldições, com origem, teste/dificuldade de resistência, duração, métodos de remoção e pelo menos um efeito positivo e um negativo tipados por registro. A validação de formato está coberta por teste escrito, ainda sem resultado executado confirmado.
- A consequência mecânica de classes híbridas foi documentada no código como penalidade social crescente por mistura, de 0 para uma classe até -3 para quatro classes. Isso não substitui a validação de desbloqueio por nível, aparência na interface e persistência da combinação.
- Foram acrescentados testes de catálogo para nomes distintos, categorias de profissão, habilidades de classe/afinidade, progressão de bônus e quantidade de maldições. **Testes escritos não equivalem a testes aprovados.**

### Pendências obrigatórias antes de declarar conclusão
1. Executar os testes de `engine`, `rules`, `ai` e `app`, compilar o APK e resolver todos os erros encontrados.
2. Auditar cada efeito do catálogo contra os escopos aceitos pelo motor e contra os cálculos reais de ataque/dano/defesa; confirmar que bônus positivos e penalidades negativas não se anulam ou acumulam incorretamente por compartilharem grupos.
3. Confirmar que as 100 maldições podem ser aplicadas, resistidas, expiradas e removidas no fluxo real da campanha — não apenas carregadas pelo parser — e que a persistência sobrevive a salvar/reabrir.
4. Confirmar que o efeito de híbridos usa a quantidade de classes realmente desbloqueadas, não apenas a quantidade de árvores selecionadas, e que a penalidade social alcança todos os testes pertinentes sem contaminar outros escopos.
5. Verificar a sétima habilidade de cada árvore, em especial ressurreição, multiataque e habilidades de ofício, com custos, alvos, duração e consequências executados pelo motor.
6. Atualizar este status com o resultado real da CI e listar separadamente qualquer teste não executado. Não marcar fase 17 nem a implementação geral como concluídas antes disso.

## Status da implementação code-first — antes da fase 17

**Branch de trabalho:** `feature/arvores-habilidades`  
**Validação automatizada:** ainda não executada; reservada para a fase 17, conforme pedido.

Implementações adicionadas nesta branch:

- Modelo persistente de níveis de habilidades, investimentos, habilidades gratuitas de origem, distribuição única dos 12 pontos iniciais e recompensas de pergaminhos por missão.
- Distribuição inicial livre entre atributos e pontos de habilidade, com validação de exatamente 12 pontos e respeito aos limites de atributos definidos pelo pacote.
- Árvores carregadas por catálogo tipado: habilidades raciais/sub-raciais gratuitas, árvores de classe e profissão, e vertentes da Classe de Malditos.
- Progressão sem teto global fixo, desbloqueio por ponto, custos de evolução validados pelo motor, preços de pergaminhos em prata e reset por 25.000 prata.
- Despertar de classes híbridas por ordem: classe principal no nível 1, segunda no nível 10, terceira no nível 20 e quarta no nível 30. A interface e o contexto da IA recebem a consequência visual/social da mistura.
- Afinidade elemental validada; Luz tratada como expressão de Yang quando declarada. Habilidades ligadas à afinidade mostram o elemento escolhido e respeitam os requisitos.
- Efeitos de habilidades aplicados como modificadores tipados. Passivas equivalentes do catálogo e das árvores são comparadas por escopo e sinal, mantendo o maior bônus ou a maior penalidade em vez de somar duplicatas.
- Golpes especiais usam o resolvedor de ataque existente. A habilidade final do Espadachim executa até cinco ataques no mesmo turno; a final do Arqueiro adiciona dano ao golpe; a Ressurreição Suprema de Yang do Sacerdote custa 10 PM e consome o turno durante combate.
- Separação explícita entre Classe de Malditos e maldições lançadas. Os 100 registros editoriais antigos foram preservados em `cursedClassConcepts`, não são mais apresentados como maldições lançadas na criação de personagem.
- Catálogo separado com 20 maldições lançadas, efeitos positivos e negativos tipados, testes reais de resistência pelo teste `resistir` do pacote, duração, expiração no fim da rodada e remoção por métodos declarados.
- Interface em campanha para consultar/aplicar/remover maldições, escolher alvos de habilidades e resgatar pergaminhos de missões concluídas.
- Bônus de habilidades e maldições conectados ao cálculo de combate e ao pipeline de testes, sem trocar a fórmula-base de ataque já existente.

### Limitações conhecidas antes da fase 17

- A maioria das árvores foi gerada a partir de um modelo progressivo compartilhado. Os valores e a identidade de cada habilidade ainda precisam de balanceamento de conteúdo; a IA não pode improvisar parâmetros ausentes.
- O sistema de efeitos especiais está tipado para os escopos já suportados. Novos efeitos narrativos complexos só podem ser considerados funcionais depois de terem implementação e validação próprias.
- A interface de remoção de maldições apresenta os métodos cadastrados e pressupõe que o jogador/GM confirme narrativamente que o método foi cumprido; a elegibilidade de missões ou itens específicos ainda pode receber validação mais estrita.
- Nenhum resultado de compilação ou suíte de testes deve ser inferido a partir deste documento. A fase 17 é necessária para confirmar compatibilidade, corrigir erros e verificar migração/persistência.



## Auditoria incremental — 2026-10-22

- CI anterior confirmado verde no commit `7c2aecb3145e768ffee372ba94d61045a64ac385`: testes JVM/Android e build do APK debug passaram; E2E foi ignorado.
- Corrigido um defeito na penalidade social de híbridos: ela agora conta apenas classes cujo desbloqueio por nível já ocorreu (níveis 1, 10, 20 e 30), em vez de penalizar imediatamente por todas as classes listadas na ficha.
- O comando canônico `SetSkillTreeLoadout` agora rejeita mais de quatro classes, impedindo que um loadout inválido cause falha posterior durante o cálculo de bônus.
- Foram adicionados testes para a contagem por nível e para o limite de quatro classes. A CI dos commits que contêm essas correções ainda precisa concluir; o resultado verde anterior não valida automaticamente estes novos commits.
- O catálogo atual de maldições aplicadas contém 100 registros (não 20). O carregamento, teste de resistência, aplicação, remoção e expiração têm caminhos de código e UI; ainda falta uma auditoria específica de escopos mecânicos e de persistência salvar/reabrir para confirmar todos os casos de ponta a ponta.
- O plano continua aberto: não declarar as 17 fases concluídas até validar os efeitos de cada habilidade final, os grupos de acúmulo e a execução real das regras para jogador e IA.


### Testes de ciclo de vida acrescentados

- O teste de motor agora verifica que a maldição é recusada quando a resistência ainda não foi resolvida ou quando o alvo resiste; uma falha aplica a maldição.
- O teste percorre duas viradas completas de combate para confirmar decremento e expiração, e compara o estado final com o replay do log.
- Outro teste cobre remoção com motivo explícito e replay. A CI destes novos testes ainda está pendente no commit mais recente.


### Conteúdo da Classe de Malditos — culminâncias

- As 42 habilidades de rank 7 das vertentes da Classe de Malditos deixaram de usar nomes de placeholder como “Técnica Suprema de maldito-vampiro-1”. Cada uma recebeu um nome temático específico da família e da vertente.
- Foi acrescentado teste de catálogo para impedir que esses nomes genéricos voltem a entrar. Isso melhora a identidade do conteúdo, mas ainda não substitui a revisão de mecânicas e balanceamento de cada vertente.
