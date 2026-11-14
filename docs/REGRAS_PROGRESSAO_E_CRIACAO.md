# Regras de progressão, criação, vida nova e itens do corpo

**Estado (12/11/2026):** tudo abaixo está **escrito e não compilado nem testado** (sem Gradle, Android SDK ou rede no ambiente de desenvolvimento). O CI decide. Decisões do autor, registradas na conversa de 12/11/2026.

## 1. Quatro pontuações separadas

| O quê | Pontos iniciais | Como ganha mais | Onde fica |
|---|---|---|---|
| **Status** (atributos) | 10 (`pontos_iniciais_status`) | +1 por nível; o nível vem de XP de quest, missão e mob | Árvore de status (`StatusTreePanel`) |
| **Habilidades** (árvore) | 2 (`pontos_iniciais_habilidade`) | +1 por nível, pelo mesmo caminho de XP | Árvore de habilidades (`SkillTreePanel`) |
| **Perícias** | nenhum | só pela prova dos níveis múltiplos de 5 (§3) | ficha (`skills`) |
| **Vantagens e desvantagens** | não gastam ponto | equilíbrio por quantidade: 1 vantagem pede 1 desvantagem, 2 pedem 3, 3 pedem 5, e assim por diante (`equilibrio|1,3,5`, depois dobra) | seção própria na criação |

- **Status e habilidades são duas árvores independentes**, também no motor: `AllocateInitialStats`/`InitialStatsAllocated` (só atributos, exatamente 10, trava no limite) e `GrantInitialAbilityPoints`/`InitialAbilityPointsGranted` (2 pontos, concedidos sozinhos na primeira abertura da árvore de habilidades). Uma não depende da outra. O comando antigo `AllocateInitialBuild` continua aceito, para campanhas salvas.
- **Criação de ficha:** os 10 pontos de status travam no limite (`CharacterDraft.setStat` recusa o 11º). Perícias não se distribuem na criação.
- Arquivos: `RulePackage.kt` (`statPool`, `separatePools`), `CharacterDraft.kt`, `CharactersScreen.kt`, `StatusTreePanel.kt`, `SkillTreePanel.kt`, `criacao.txt`.

## 2. Idade e sexo na criação (MI-26)

- O jogador **digita a idade em anos** (1 a 1000, obrigatória) e **escolhe o sexo** (masculino ou feminino, obrigatório). Substitui a faixa jovem/adulto/veterano.
- Ao iniciar a campanha com mundo, a idade é limitada à longevidade do povo (`vida_max` − 1) com aviso; o sexo vira o fato `heroi.sexo`. Ficha antiga sem idade continua como adulto (40%).
- **Renascimento (item de renascimento do M-04):** o herói volta **criança, com 7 anos** (`IdadeHeroi.IDADE_RENASCIDO`), sem virar bebê. A morte anterior deixa de valer (`heroi.morte = nenhuma`).
- **NPC assumido:** a vida nova começa com a **idade do NPC** (definida pelo Mestre) e a classe de longevidade do NPC (ou a do herói anterior).

## 3. Perícias: prova nos níveis múltiplos de 5

- Perícia **só ganha ponto** pela prova dos níveis 5, 10, 15… (`nivel_missao_pericia|5`, comando `AdvanceSkillByTrial`, evento `SkillTrialPassed`). Matar mob, XP e missões comuns **não** dão ponto de perícia. Uma prova por nível; máximo 5 por perícia (`skillCaps`).
- **Sem missão formal:** o Mestre recebe o contexto **PROGRESSÃO**, escolhe o momento e costura a prova na história (exemplo: armadilha de lanças que pede vários testes de esquiva). A dificuldade é o nível ÷ 5. O Mestre deve pedir **pelo menos 3 testes** e só propor `skill_trial` se o jogador passar na maioria. Ao fim, anuncia que a perícia subiu e descreve os bônus. Se o jogador falha, a oportunidade continua.
- O motor valida nível, uso único e limite; **não** conta os testes (os eventos não guardam qual perícia foi testada). Isso é instrução ao Mestre.
- Status e habilidades novos também são lembrados pelo Mestre na narrativa, sem missão.

## 4. NPC: identidade própria e ficha completa só ao assumir

- O **Mestre** define a identidade do NPC ao apresentá-lo (`identity`, `age`, `sex`, `longevityClass`, `publicInfo`, além de personalidade, objetivos, motivações e segredos) no `create_npc`. A ficha e o dossiê completos são do Mestre.
- O **jogador** vê desde a apresentação só o `publicInfo` (aparência, idade aparente) e o que descobrir por testes.
- Se o jogador **decidir continuar com o NPC** (`AssumeNpc`), a ficha vira completa para ele: identidade, idade, sexo, personalidade, objetivos, motivações e segredos (`NpcSheetView`). A idade do NPC passa a ser a do herói (§2).
- Arquivos: `Npc.kt`, `Commands.kt` (`SetNpcDossier`), `Proposals.kt`, `LegacyActions.kt`.

## 5. Recuperar itens do corpo (LG-04)

- **Fora de combate:** é só pegar (`TransferBodyItems`), sem teste.
- **Em combate:** só no turno do jogador; **gasta o turno** (sem ataque) e pede um **teste de destreza** (`destreza`, atributo `habilidade`, em `testes.txt`; usa `esquiva` se o pacote não tiver). **Sucesso:** pega os itens. **Falha:** o adversário **acerta** o jogador (golpe sem teste de acerto nem esquiva; a defesa ainda reduz o dano).
- Interface: painel "Itens do corpo" (`BodyLootPanel`) na aba Jogar. Lógica em `CombatActions.recoverBody`.
- Limite: só o inventário do corpo é transferido (equipamento vestido não).

## 6. Testes escritos (não executados)

`PoolsSeparadosTest`, `ArvoresIndependentesTest`, `ProvaDePericiaTest`, `CorpoENpcTest`.
