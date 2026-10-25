# RPG-solo: refinamentos de atributos, PV e combate

Data: 23/10/2026. Origem: conversa de refinamento sobre o cálculo de PV/PM de personagens, NPCs e monstros.
Este documento reúne o que foi decidido e o que falta definir. **Só o que está marcado como entregue entrou no código** (o N-01 só alterou documentação; o N-02 entregou os sete atributos sem teto e o N-03 as fórmulas de PV/PM/PA, ainda não compilados nem executados). O plano de implementação (fases, módulos e regras de execução) está na seção 14.

Legenda: ✅ decidido pelo proprietário · 🔶 proposta ou em teste (valor sugerido) · ❓ em aberto

---

## 1. Estado atual do repositório (referência)

- **Atributos** (`atributos.txt`): Poder, Habilidade, Resistência; faixa 0–5; padrão 0.
- **Recursos** (`recursos.txt`, N-03, 24/10/2026): PA = 1 + Habilidade · PM = 2 × Inteligência · PV = 5 × Resistência (mínimo 1). Antes (22/10/2026): PA = 1 + Poder · PM = 2 + 2 × Habilidade · PV = 4 + 3 × Resistência.
- **Criação** (`criacao.txt`): 10 pontos para distribuir, custo 1 por ponto, +1 ponto de status e +1 de habilidade por nível.
- **Progressão** (`progressao.txt`): a tabela lista hoje os níveis 2 a 6 (XP total 10, 25, 45, 70 e 100). Não encontrei nível máximo no código nem nos dados: a evolução é gradual e sem teto, e a curva de XP depois do nível 6 ainda não está definida.
- **Combate antigo** (`GameEngine.attack`): d20 + Poder contra defesa 10 + Habilidade.
- **Combate unificado** (`UnifiedCombat.kt`): ataque e defesa por valor efetivo; dano = max(0, ataque − defesa); crítico com d6 (limite 6); ignorar defesa com d10 (limite 10). Os dois sistemas coexistem.
- **Valor efetivo** (`EffectiveValue.kt`): soma de atributo + traço + perícia + habilidade + item + buff + bênção + condição. A partir de 6 vira valor ÷ 6 dados d6 mais o resto (8 = 1d6+2; 12 = 2d6).
- **Árvore de habilidades** (`habilidades-arvore.txt`, plano em `PLANO_ARVORE_HABILIDADES.md`): 7 habilidades em cadeia por classe (350) e por profissão (721). As de classe têm efeitos de combate; as de profissão são passivas que somam +N na perícia do ofício.
- **Catálogo de personagem** (`personagem-catalogo.json`): 50 raças, 50 sub-raças, 50 classes, 103 profissões e 20 híbridos; cada entrada dá hoje +1 em uma perícia (Ferreiro, por exemplo, +1 em Ofícios).
- **Golpe** (`dano.txt`): 1d6 + Poder.
- **NPC** (`NpcFactory`): usa o mesmo `buildFicha` do jogador. Sem pacote instalado, a IA informa os recursos direto.
- **Condições** (`condicoes.txt`): dano fixo por rodada (sangramento 1 × 3, envenenado 1 × 3, queimando 2 × 2).

---

## 2. Atributos

- ✅ **Sete atributos:** Poder, Habilidade, Resistência + **Inteligência, Agilidade, Destreza, Sorte**.
- ✅ **Sem teto:** "o limite é o céu". ✅ Entregue no N-02: o máximo 5 saiu de `atributos.txt` (`max` = `-`) e a criação não limita por atributo, só pelo orçamento de pontos.
- ✅ **Pontos (N-01):** na criação, **10 pontos de status + 2 pontos de habilidade/profissão** (os 12 do `PLANO_ARVORE_HABILIDADES.md`, agora separados por tipo); depois, +1 de status e +1 de habilidade por nível; custo 1 por ponto (mantido). Os 2 pontos de habilidade/profissão compram degraus da árvore (N-19).
- Descartado: custo crescente estilo Ragnarok Online. Com 1 ponto por nível, um atributo que custasse 2 ou mais não poderia ser comprado ao subir de nível.
- ✅ Build desequilibrada é escolha do jogador e ele arca com a dificuldade. Quem põe tudo em Resistência fica só com PV: sem ataque, PA e PM ("monte de carne").

| Atributo | Função |
|---|---|
| Poder | Ataque/dano físico (Força) e PA |
| Resistência | PV (5 por ponto) e base da defesa |
| Inteligência | Mais PM e pontos de ataque mágico ✅ |
| Habilidade | ✅ PA (ações por rodada e técnicas) e bônus nos testes de ofício e perícia (consertar, criar etc., N-20). Perde PM (vai para Inteligência), defesa (Resistência) e iniciativa (Agilidade) |
| Agilidade | Teste de esquiva contra a Destreza do atacante ✅ e **iniciativa** ✅ |
| Destreza | Teste de acerto contra a Agilidade do defensor |
| Sorte | Chance de crítico; ajuda em testes e outras coisas |

- ✅ **Papel da Habilidade e iniciativa (N-01, opção A):** a iniciativa passa para a **Agilidade** (quem é mais rápido age primeiro); a **Habilidade** vira PA e bônus em testes de ofício e perícia; o **Poder** passa a ser só ataque/dano físico (sai o PA do Poder). Fórmula exata do PA com Habilidade: fechar no N-03.
- ✅ **Requisitos de traços (N-10):** magia, espiritismo, conhecimento e informática exigem **Inteligência**; movimento, furtividade e esquiva exigem **Agilidade**; ataques de precisão, furto e tiro exigem **Destreza**; Pirata (`sorte-do-mar`) exige **Sorte**; **Habilidade** fica com ofícios, técnica, medicina, engenharia, rastreio e perícias sociais; **Poder** e **Resistência** não mudaram. 53 traços foram para Inteligência, 21 para Agilidade, 18 para Destreza e 1 para Sorte (os limiares N foram mantidos).
- ✅ **Curva de XP sem teto (N-01):** subir para o nível N custa **5 × N** de XP, o que prolonga a tabela atual sem alterá-la (níveis 2 a 6 = 10, 25, 45, 70, 100). XP total acumulado para o nível N = 5 × (N(N+1)/2 − 1). Referências: nível 10 = 270, 20 = 1.045, 30 = 2.320, 50 = 6.370, 100 = 25.245. Entra no N-04 (tabela ou fórmula).
- ✅ **Pontos por nível:** mantidos em +1 de status e +1 de habilidade, sem teto.

### 2.1 Balanceamento do ganho de XP com o nível (✅ diretriz, 🔶 valores)

- ✅ **Pedido do proprietário:** o XP ganho em **missões** e em **vitórias em batalha** deve acompanhar o nível, para o ritmo de progressão ficar parecido em qualquer faixa.
- **Hoje no código:** o XP de missão tem teto igual ao custo do nível atual (`EngineConfig.maxQuestXp`, `Commands.kt`), e acima do último limiar vale a última faixa, então **com a curva sem teto esse cálculo precisa virar fórmula**. O XP da vitória em batalha é **fixo**: `combate_xp_vitoria|5` em `criacao.txt`, pago em `CombatActions.awardCombatVictoryXp`, igual em qualquer nível. No nível 30 (155 XP por nível) seriam 31 vitórias por nível; no nível 2 (10 XP) bastam 2.
- 🔶 **Missão:** teto = custo do nível atual (regra mantida, agora por fórmula 5 × N); faixas sugeridas para o mestre propor: curta ≈ 25%, média ≈ 50%, grande ≈ 100% do custo do nível.
- 🔶 **Batalha:** XP por inimigo derrotado = fração do custo do nível do jogador (ponto de partida: ≈ 8% do custo do nível, ~12 vitórias por nível contra inimigos de nível igual), modulada pela diferença de nível entre inimigo e jogador (inimigo mais forte paga mais; muito mais fraco paga pouco ou nada, para impedir fazenda de XP). Com a categoria de ameaça do N-27 (capanga, comum, elite, chefe) entra o multiplicador de ameaça.
- ❓ Confirmar os percentuais (8% e as faixas de missão) e o desconto por inimigo fraco ao fechar o N-04; o N-27 refina o XP por categoria de ameaça.

---

## 3. PV

- ✅ **1 ponto de Resistência = 5 PV**, valor fixo, para o PV não ficar baixo.
- ✅ **Sem base:** quem não coloca Resistência tem **1 PV** e nenhuma defesa base. Fórmula: PV = 5 × Resistência, mínimo 1 (Resistência 1 = 5 PV, 3 = 15, 10 = 50).
- ✅ O recurso se chama `pv` em `recursos.txt`, mas o motor usa `hp` como padrão em `damageResource`. ✅ Resolvido no N-03: o `manifest.txt` já liga a morte a `pv` (`morte|pv`) e o app passa esse recurso ao motor; condições sem a coluna `recurso` agora herdam o recurso de `morte` do manifesto (só caem em `hp` sem `morte`).
- ✅ **PM e PA (N-03):** PM = 2 × Inteligência (base 0); PA = 1 + Habilidade (a base 1 garante ao menos 1 ação mesmo com Habilidade 0). PV = 5 × Resistência com mínimo 1. Fichas já salvas mantêm os máximos gravados; só fichas novas ou adaptadas usam a conta nova.

---

## 4. Ataque, defesa e dano

- ✅ **Ataque** = soma de atributo + habilidade/magia + equipamento + vantagens + buffs; 1 ponto = 1 de ataque. Exemplo: 2 + 3 + 2 + 1 = 8 = 1d6+2 (o motor já faz assim).
- ✅ **Defesa** segue a mesma lógica: base **Resistência** + armadura (+ vantagens + buffs). A armadura vem da calça (seção 10).
- ✅ **Dano = ataque − 0,65 × defesa** (mínimo 0). A defesa continua como definida antes: conta 65%. (Defesa a 100% deixa a luta lenta; 50% foi considerada forte demais.)
- ✅ **Arredonda para cima** o dano final.
- 🔶 **A defesa rolada nunca fica negativa.** Hoje um valor abaixo de 3 vira 1d6 − N e pode dar negativo, o que aumenta o dano acima do ataque (não testado).

**Simulação** (dano médio por golpe; PV = 5 × Resistência; arredondando para cima; sem crítico nem condições):

| Ataque × defesa | PV | Golpes (defesa 65%) | Golpes (defesa 100%) |
|---|---|---|---|
| 8 × Res 3 | 15 | ~3 | ~3 |
| 8 × (Res 3 + armadura 2) | 15 | ~4 | ~5 |
| 10 × (Res 3 + armadura 2) | 15 | ~2 | ~3 |
| 12 × (Res 4 + armadura 3) | 20 | ~4 | ~7 |
| 8 × Res 0 | 1 | 1 | 1 |
| 5 × Res 5 (só atributo, sem itens) | 25 | ~15 | ~26 |

Duelo espelhado só com atributo (n pontos em Poder contra n em Resistência, PV = 5 × n): 5 pontos ≈ 15 golpes, 10 ≈ 16, 15 ≈ 18, 20 ≈ 20, 30 ≈ 22. A duração cresce devagar com o nível, então a escala não explode em nível alto. Na prática o ataque soma habilidade, item e buff, e a luta fica mais curta.

---

## 5. Acerto e esquiva

- ✅ **Agilidade (defensor) × Destreza (atacante):** teste automático, calculado pelo motor, igual para jogador e para NPC/monstro.
- 🔶 Fluxo proposto: (1) Destreza × Agilidade decide se acerta; (2) checagem de crítico; (3) dano = ataque − 0,65 × defesa. A confirmar. O confronto unificado de hoje não tem esse teste.

---

## 6. Crítico e Sorte

- ✅ Crítico com a mesma lógica do "ignorar defesa": limite = lados do dado − bônus, mínimo 1. No crítico o ataque vale o valor cheio, sem dado.
- 🔶 Dado **d10** (base 10%, +10% por ponto de bônus). Com d10 o teto de 70% cai exatamente no limite 4; com d12 não cai em número inteiro (limite 5 = 67%, limite 4 = 75%).
- ✅ **Teto pelos atributos: 70%.** Só com Sorte o crítico chega a 70% (no d10, Sorte 6, limite 4); os itens levam a chance até **100%**. Sorte acima de 6 não aumenta mais o crítico (continua valendo para testes e outras coisas).
- 🔶 Os itens (elmo e encantamentos) dão Sorte que passa do teto: +10% por ponto, até +30% (100%). Ver a seção 10.
- ℹ️ **Situação no código (24/10/2026):** o atributo Sorte existe desde o N-02, mas **ainda não aumenta a chance de crítico**: o crítico atual é um d6 com limite 6 − bônus do alvo `crit` (`UnifiedCombat.checkCrit`), e só traços/itens com esse alvo mexem nele. A ligação Sorte → crítico (d10, +10% por ponto, teto 70%) é do **N-07**.
- ❓ Como a Sorte "ajuda em testes e outras coisas". Atenção: "sorte" já existe como mecânica (gastar PA para rerrolar, `recurso_sorte|pa`); cuidado com o nome.

---

## 7. Condições

- ✅ Dano por rodada em **% do PV máximo**, arredondando para cima, mínimo 1. Acaba o dano fixo de 1.

| Condição | Por rodada | Rodadas | Total | Exemplo com 30 PV |
|---|---|---|---|---|
| Sangramento | 10% ✅ | 3 | 30% | 3 por rodada |
| Envenenado | 5% ✅ | 3 | 15% | 2 por rodada |
| Queimando | 15% 🔶 | 2 | 30% | 5 por rodada |

- Durações mantidas. Condições ignoram a defesa, então são o contra-golpe de quem tem muita Resistência.
- Código: hoje o dano por rodada é um `Int` em `condicoes.txt`, passado por `Events.kt` e `Commands.kt` e aplicado no fim da rodada (`Commands.kt`, por volta da linha 979). O percentual passa a ser guardado e convertido na hora de aplicar, a partir do máximo do recurso. Ajustar também o texto de `CheckMenu.kt` e as validações.

---

## 8. Habilidades, elementos e condições

- ✅ Habilidades têm **elementos** e **chance de causar condição** (exemplo: golpe de espada de fogo com chance de queimadura).
- ❓ Modelo de elementos (fraquezas e resistências). ✅ **Decidido em 24/10/2026** (§15).
- ❓ Chance de aplicar a condição: fixa, por teste ou influenciada pela Sorte? ✅ **Decidido em 24/10/2026** (§15).

---

## 9. NPCs e monstros

- O motor já é unificado: jogador, NPC, aliado e chefe usam as mesmas fontes e o mesmo cálculo. Monstro = atributos + modificadores, sem fórmula própria. Com PV = 5 × Resistência (mínimo 1), um monstro sem Resistência tem 1 PV.
- 🔶 Proposta sem decisão: categoria de ameaça com multiplicador de PV (capanga ×0,5; comum ×1; elite ×1,5; chefe ×2 a ×3). PM e PA só para quem usa magia ou ação. Escala por nível (prevista em `PLANO_RPG_SOLO.md`, não implementada).
- ❓ Fechar o caminho **sem pacote**, em que a IA informa os recursos direto e o motor só confere se não são negativos. ✅ **Decidido em 24/10/2026** (§15).

---

## 10. Equipamentos e encantamentos

### Peças e bônus

- ✅ Seis peças: elmo, luvas, botas, calça, peito e arma.

| Peça | Dá | Encantável |
|---|---|---|
| Elmo | Sorte | não |
| Luvas | Destreza | não |
| Botas | Agilidade | não |
| Calça | Defesa | não |
| Peito | PV | **sim** |
| Arma | Ataque ou Inteligência | **sim** |

- ✅ **Calça dá defesa** (efeito `defesa:+N`, que já existe no motor) e **peito dá PV direto** (PV máximo). A Resistência continua sendo a base da defesa e dos PV por atributo.
- ❓ Os itens atuais que dão `defesa` no corpo e na cabeça precisam ser redistribuídos.
- ✅ **Escudo:** só pode ser usado com arma de uma mão e dá **Resistência** (5 PV e +1 de defesa por ponto). Como dá PV e defesa ao mesmo tempo, o bônus por faixa de nível precisa ser menor que o de uma peça comum.
- ❓ Arma de duas mãos bloqueia o escudo? Se sim, precisa compensar com mais ataque. O formato atual tem `mao` e `outra-mao`, mas não vi como marcar arma de duas mãos. ✅ **Decidido em 24/10/2026** (§15).
- ✅ **Arma não dá defesa:** só ataque (ou Inteligência).
- 🔶 A qualidade do item (normal 100%, boa 125%, rara 150%, lendária 200%) escala os bônus positivos; isso já existe no motor.
- 🔶 **Sorte do elmo e teto de crítico:** a Sorte de atributo para de contar no 6 (70%); a Sorte do elmo e dos encantamentos passa do teto, +10% por ponto, até 100% com +3 (elmo comum +1, raro +2, lendário +3). A confirmar.

**Hoje no motor:** os slots são `mao`, `outra-mao`, `corpo` e `cabeca`, e os efeitos de equipamento são só `ataque`, `dano` e `defesa`. Não se troca equipamento em combate. Faltam os slots luvas, botas e calça e os efeitos de bônus de atributo (Sorte, Destreza, Agilidade, Inteligência) e de PV máximo (`max_pv` já existe para traços).

### Nível mínimo e qualidade

- ✅ Todo equipamento tem **nível mínimo** para ser usado: 1, 5, 10, 20, 30 e assim por diante (sem teto, como a progressão).
- ✅ Qualidades: **normal, ótimo, raro e épico**; **épico só a partir do nível 10**.
- ❓ Esses nomes substituem os atuais (normal 100%, boa 125%, rara 150%, lendária 200%) ou a lendária continua acima do épico? Definir o multiplicador de cada qualidade.
- ❓ Bônus base por faixa de nível. Princípio sugerido: o conjunto de equipamento vale uma fração dos pontos que o personagem tem naquele nível, para ajudar sem dominar. ✅ **Decidido em 24/10/2026** (§15).
- Não vi, no formato atual do item, um campo de nível mínimo; precisa ser adicionado.

### Peso e classe

- ✅ Equipamentos em três pesos, **pesado, médio e leve** (nomes sugeridos abaixo), de acordo com a classe.
- 🔶 Cada peso muda a distribuição dos bônus nas mesmas peças: pesado com mais defesa e PV; leve com mais Agilidade, Destreza, Sorte e Inteligência; médio equilibrado.
- 🔶 Mapeamento sugerido das 50 classes do catálogo (a revisar):
  - **Pesado (13):** Guerreiro, Guardião, Paladino, Cavaleiro, Gladiador, Berserker, Guardião do Código, Guardião Rúnico, Guardião de Relíquias, Sentinela, Lanceiro, Comandante, Artífice.
  - **Médio (18):** Arqueiro, Explorador, Ladino, Batedor, Duelista, Monge, Caçador de Monstros, Domador, Assassino, Agente das Sombras, Pirata, Marinheiro, Pistoleiro, Atirador de Precisão, Bucaneiro, Espadachim, Artista Marcial, Sobrevivente.
  - **Leve (19):** Mago, Feiticeiro, Invocador, Curandeiro, Sacerdote, Druida, Bardo, Alquimista, Estrategista, Médico de Campo, Investigador, Diplomata, Oráculo, Xamã, Ilusionista, Cronista, Cartógrafo, Psíquico, Tecelão de Sonhos.
- 🔶 Nomes sugeridos: pesado (Baluarte, Couraça, Titã), médio (Patrulha, Malha, Andarilho), leve (Véu, Manto, Sombra).
- ✅ Usar equipamento de peso não destinado à classe dá **penalidade**, salvo talento ou habilidade que sobreponha o tipo.
- 🔶 Penalidade sugerida: os bônus da peça valem pela metade. Escala sozinha com o nível, sem regra nova por faixa. ✅ **Decidido em 24/10/2026** (§15).
- ✅ Quem sobrepõe a restrição tem uma **vantagem** (não uma profissão) chamada **Mestre das Armas** (usa qualquer arma); possível **Mestre dos Equipamentos** (qualquer peso de armadura). **Alfaiate**, **Armeiro** e **Armeiro de Armaduras** já existem como profissões.
- ✅ As armas também têm tipos ligados às classes (espada, arco, cajado etc.), com a mesma penalidade.
- ❓ As vantagens vivem em `tracos.txt`, com requisitos de atributo. Falta o efeito de vantagem que anula a penalidade de peso/tipo e definir o requisito para obtê-la (nível, atributo ou só depois da criação). ✅ **Decidido em 24/10/2026** (§15).
- 🔶 Nomes: mantidos os sugeridos acima (leitura a confirmar).

### Encantamento (ferreiro)

- ✅ Só **peito e arma** podem ser encantados.
- ✅ Feito pelo **ferreiro das cidades** (NPC) ou por personagem com a **profissão Ferreiro**.
- ✅ **Arma:** elemento ou debuff (sangramento, queimando, envenenado etc.).
- ✅ **Peito:** resistência a elemento, a condição (exemplo: sangramento) ou a atributo.
- ✅ **Caro e permanente:** uma vez encantado, não pode ser desencantado.
- ❓ Chance de a arma aplicar a condição: fixa, por Sorte ou só no crítico. ✅ **Decidido em 24/10/2026** (§15).
- ❓ Resistência a condição: imunidade ou redução do percentual de dano? (`resistencia_dano` e `imune_<condição>` existem para traços, não para equipamento.) ✅ **Decidido em 24/10/2026** (§15).
- ❓ Um encantamento por peça ou vários; preço por tipo e qualidade (hoje o `preco` fica em `itens.txt`, em prata/ouro). ✅ **Decidido em 24/10/2026** (§15).
- ❓ **Ferreiro já existe como profissão** no catálogo (103 profissões, incluindo Armeiro, Armeiro de Armaduras e Alfaiate), mas hoje só dá +1 em Ofícios; falta o efeito de encantar. O catálogo tem 50 classes; `tracos.txt` lista apenas 10.
- 🔶 Ideia de implementação: o id do item já aceita sufixo de qualidade (`base#qualidade`); o encantamento pode virar outro sufixo, o que torna a mudança permanente por natureza.

### Pergaminhos de encantamento

- ✅ Criados pelas **guildas de magos** ou por **mago com a habilidade de escrever pergaminhos**.
- ✅ Pergaminhos de elemento; exigem **material** e **itens mágicos**; custo alto; o efeito dura pouco (exemplo: **1 dia**).
- 🔶 Leitura: é o encantamento temporário, alternativa reversível à do ferreiro. A confirmar.
- ❓ Duração em dias exige que o motor conte o tempo da campanha. Hoje as durações são em rodadas, e não verifiquei se existe contagem de dias. ✅ **Decidido em 24/10/2026** (§15).
- ❓ A habilidade "escrever pergaminhos" (nova) e a lista de materiais.
- Os pergaminhos atuais (cura, vigor, mana, remover condição) são consumíveis de uso único; o de encantamento é outro tipo.

---

## 11. Classes e profissões: habilidades específicas

### Decisões

- ✅ Classe e profissão não devem dar só pontos de perícia (+1): devem dar **algumas habilidades específicas**, não todas as possíveis.
- ✅ Exemplos: **Ferreiro** (resistência natural ao fogo, consertar equipamentos, criar equipamentos); **Curandeiro** (criar poções de cura e de regeneração); **Ladrão** (afanar).
- ✅ As habilidades **progridem por nível**, e cada degrau libera algo novo (exemplo citado: níveis 7, 13, 21).
- ✅ Todas ficam **visíveis na criação e na escolha da profissão**, para o jogador saber o que cada ofício vai liberar.

### Hoje no projeto (`PLANO_ARVORE_HABILIDADES.md`, `habilidades-arvore.txt`)

- Cada classe e cada profissão tem 7 habilidades em cadeia, cada uma com pré-requisito (350 de classe, 721 de profissão).
- As de classe já têm efeitos de combate (o Arqueiro, por exemplo, tem dano, defesa e precisão).
- As de profissão são passivas genéricas ("Prática do Ofício", "Técnica de Precisão"…) que só somam +N na perícia do ofício. O Ferreiro tem degraus de nível 1, 5, 10, 15, 20, 25 e 30 e nenhuma habilidade específica.
- Escolher classe ou profissão não cobra pontos; desbloquear cada habilidade adicional custa 1 ponto de habilidade. A sétima é a culminante.
- A estrutura já comporta a ideia: basta trocar o conteúdo dos degraus (nome, efeito, ação), sem mudar a cadeia, o custo nem o formato do arquivo.

### Estrutura proposta (🔶)

- 🔶 **Degrau 1 vem ativo na criação**, sem custo, junto com a escolha da profissão (é o que dá a identidade do ofício logo no início, como a resistência ao fogo do Ferreiro). Os degraus seguintes custam 1 ponto de habilidade, como hoje, e exigem o nível indicado.
- 🔶 **Degraus de nível:** 1, 7, 13, 21 e 30 como ponto de partida, a partir dos números citados (hoje: 1, 5, 10, 15, 20, 25, 30). Iguais para todas as profissões, para o jogador decorar uma regra só. Os degraus além do 5º (se mantivermos 7 na cadeia) seguem a curva de XP, que ainda não existe depois do nível 6.
- 🔶 **Cada profissão: 3 a 5 habilidades específicas** (o núcleo do ofício). Os degraus que sobrarem **melhoram o que já existe** (mais chance de conserto, poção mais forte, material mais raro) em vez de inventar algo novo. Assim ninguém tem "todas as possíveis".
- 🔶 **Classe também ganha utilidade fora de combate**, misturada às de combate, como o plano já permite (exemplo: Ladino com abrir fechaduras, Batedor com rastrear).

### Exemplos por profissão (🔶 sugeridos, a ajustar)

| Degrau | Nível | Ferreiro | Curandeiro | Ladrão (ver nota) |
|---|---|---|---|---|
| 1 | 1 | Resistência natural ao fogo | Primeiros socorros (cura leve fora de combate) | Afanar (furto simples) |
| 2 | 7 | Consertar equipamentos, com chance de sucesso | Poção de cura (criar) | Passos leves (bônus de furtividade) |
| 3 | 13 | Criar equipamentos (nível limitado) | Poção de regeneração (criar) | Afanar melhorado (itens de valor e do inventário) |
| 4 | 21 | Consertar sem chance de falha; equipamento até o nível 20 | Poções de qualidade superior | Golpe de oportunidade em alvo desprevenido |
| 5 | 30 | Encantar peito e arma (seção 10) | Cura em área / ressurreição leve | Mestre do furto (fuga e saque rápido) |

- ⚠️ **Nota sobre nomes:** não existe "Ladrão" no catálogo. O equivalente é **Ladino** (classe) ou **Espião** (profissão); a habilidade de afanar deve ir nesses dois (e talvez em Assassino e Agente das Sombras). "Curandeiro" existe como **classe** e como profissão **Curandeiro Comunitário**, então a lista de cura vale para os dois.
- 🔶 Mesma ideia para as profissões-chave seguintes: **Alfaiate** (consertar e criar leves, ligado ao peso leve), **Armeiro** (armas), **Armeiro de Armaduras** (pesadas e médias).

### Análise das 103 profissões (🔶 proposta)

Quase todas se encaixam em **16 famílias**. Em vez de criar 721 habilidades diferentes, cada família ganha um **modelo de 5 degraus** (níveis 1, 7, 13, 21 e 30) e cada profissão escolhe o sabor dentro dele (o que cria, o que conserta, o que resiste). **86 das 103** recebem habilidades específicas; **17** (Conhecimento e registro, Vida cotidiana) ficam com a passiva atual.

| Família (nº de profissões) | Profissões | Habilidades-base (degraus 1 → 30) |
|---|---|---|
| Forja e armas (6) | Ferreiro, Armeiro, Armeiro de Armaduras, Perito em Arcos, Fabricante de Arcos, Fabricante de Flechas | Afinidade com o material (Ferreiro: resistência ao fogo) → consertar → criar peças do ofício → subir qualidade → encantar ou criar raro/épico. Ferreiro: peito e arma; Armeiro: armas; Armeiro de Armaduras: elmo e escudo; Perito em Arcos e fabricantes: arcos e munição especial |
| Vestuário e couro (6) | Alfaiate, Tecelão, Curtidor, Sapateiro, Cordoeiro, Preparador de Peles | Reconhecer material → consertar → criar → subir qualidade → peça rara. **Cada profissão faz uma peça**: Alfaiate = calça, Sapateiro = botas, Curtidor = luvas, Tecelão = peças leves, Cordoeiro = cordas e laços, Preparador de Peles = material bruto |
| Preparos (poções, comida, venenos) (7) | Alquimista de Oficina, Herbalista, Cozinheiro, Padeiro, Cervejeiro, Especialista em Toxinas, Perfumista | Reconhecer ingredientes → preparar consumível básico → versão forte → efeito especial → obra-prima. Alquimista: poções e bombas; Herbalista: ervas e antídoto; Cozinheiro/Padeiro/Cervejeiro: refeições e bebidas com buff; Toxinas: veneno de arma (condição envenenado) e resistência; Perfumista: essências |
| Cuidados (3) | Curandeiro Comunitário, Enfermeiro, Cirurgião | Primeiros socorros → curar condição (sangramento, veneno) → recuperar PV fora de combate → cura em grupo → cura grave. Cirurgião: remove condições pesadas |
| Furtividade e investigação (6) | Espião, Mensageiro, Perito em Armadilhas, Detetive, Caçador de Recompensas, Carcereiro | Discrição → **afanar** (Espião) / desarmar armadilha (Perito) / rastrear alvo (Caçador de Recompensas) / pistas (Detetive) → abrir fechaduras → versão avançada. Mensageiro: viagem rápida e entrega segura |
| Exploração e caça (9) | Caçador, Guia de Montanha, Guia de Floresta, Guia de Deserto, Guarda-Florestal, Minerador, Geólogo, Observador do Clima, Cartógrafo de Campo | Resistência ao ambiente (montanha: frio, deserto: calor/sede, floresta: veneno) → rastrear e orientar → **reduz o dano de perigos** (`perigos.txt` já é %) → **encontrar recursos** (ervas, minério, caça) → domínio do terreno |
| Comércio e diplomacia (10) | Comerciante, Contador, Estalajadeiro, Taverneiro, Diplomata de Corte, Mediador, Advogado, Organizador de Eventos, Mestre de Cerimônias, Escolta Profissional | Avaliar preço → desconto na compra e bônus na venda → rumores e informação → contratos e resolução sem combate → influência. Escolta: proteger aliado ou carga |
| Mecanismos (4) | Mecânico, Mecânico de Artefatos, Inventor, Relojoeiro | Identificar mecanismo → consertar engenhoca → criar engenhoca (consumível) → desarmar e reprogramar → artefato |
| Identificação e relíquias (7) | Avaliador de Antiguidades, Avaliador de Materiais, Arqueólogo, Restaurador, Reparador de Relíquias, Tradutor, Criptoanalista | Identificar item → reconhecer falsificação ou maldição → decifrar mapa e pergaminho → restaurar relíquia → relíquia reforçada |
| Animais e montarias (5) | Criador de Animais, Tratador de Montarias, Tratador de Aves Mensageiras, Treinador, Adestrador (N-31: pequenos, médios, grandes, feras, colossais) | Acalmar animais → cuidar da montaria → montaria ou ave com bônus → adestrar aliado → companheiro |
| Mar e rios (5) | Pescador, Pescador de Profundidade, Mergulhador, Navegador, Marinheiro de Carga | Resistência à água → pescar e coletar → navegação segura → mergulho (acesso a locais) → domínio do mar |
| Artes e performance (5) | Artista, Músico, Ator, Dançarino, Tatuador | Apresentação (ganha prata) → inspirar (buff pequeno em aliados) → influência sobre NPC → obra-prima. Tatuador: marca permanente com pequeno bônus (❓ similar ao encantamento) |
| Joias, vidro e cerâmica (7) | Especialista em Joias, Ourives, Lapidador, Montador de Gemas, Joalheiro de Campo, Vidreiro, Oleiro | Avaliar e lapidar (valor de venda) → engastar gema com bônus pequeno → gemas raras → joia lendária. ❓ Depende de criar um sistema de gemas |
| Construção (7) | Carpinteiro, Marceneiro, Pedreiro, Arquiteto, Construtor de Barcos, Engenheiro de Pontes, Construtor de Fortificações | Avaliar estrutura → reforçar abrigo → construir barricada, ponte ou barco → fortificar acampamento → obra-prima. ❓ Depende de existir acampamento ou base na campanha |
| Conhecimento e registro (11) | Escriba, Bibliotecário, Professor, Astrônomo, Paleontólogo, Botânico, Heraldista, Genealogista, Arquivista, Escrivão Judicial, Investigador de Incêndios | **Sem habilidade nova por enquanto.** Ficam com a passiva de perícia atual; são ofícios de narrativa e consulta |
| Vida cotidiana (6) | Agricultor, Apicultor, Jardineiro, Lavadeiro, Barbeiro, Saboeiro | **Sem habilidade nova por enquanto.** Passiva atual; no máximo 1 toque pontual (Padeiro e Apicultor poderiam dar ração e mel de cura leve) |

**Por que famílias ajudam:** o motor precisa de poucas mecânicas novas, e cada uma serve a várias profissões:

- **Consertar** e **criar** (uma só mecânica, com tipo de item e receita variando): Forja, Vestuário, Mecanismos, Construção, Joias. É o grupo maior e o que mais depende de receitas e materiais.
- **Preparar consumíveis:** Preparos e Cuidados (poções, refeições, venenos, bombas).
- **Teste furtivo** (Destreza × Agilidade, seção 5): Furtividade e investigação, mais Ladino, Assassino e Agente das Sombras entre as classes.
- **Redução de perigo e coleta de materiais:** Exploração e Mar. Uma boa ligação: **as famílias de exploração fornecem os materiais que as de criação consomem**, e o jogo solo ganha uma economia própria.
- **Preço e informação:** Comércio e diplomacia, Artes.
- **Identificar:** Identificação e relíquias, que tem a vantagem de não precisar de receitas.

**Cada peça tem seu artesão (seção 10):** elmo e escudo → Armeiro de Armaduras · peito e arma → Ferreiro/Armeiro (encantável) · calça → Alfaiate · botas → Sapateiro · luvas → Curtidor. Isso fecha o ciclo de equipamentos sem regra extra.

**Ordem sugerida de conversão:**

1. **Onda 1** (já tem sistema de apoio no documento): Forja e armas, Vestuário e couro, Preparos, Cuidados, Furtividade e investigação. São 28 profissões e cobrem os exemplos que você deu.
2. **Onda 2:** Exploração e caça, Comércio e diplomacia, Mecanismos.
3. **Onda 3:** Mar e rios, Animais e montarias, Artes e performance, Identificação e relíquias. Joias e Construção só depois de existirem gemas e acampamento.

### Ligação com outros sistemas

- 🔶 **Equipamentos:** o degrau do Ferreiro define o **nível máximo de equipamento** que ele cria e encanta (1, 5, 10, 20, 30), alinhado ao nível mínimo das peças (seção 10). O encantamento só aparece nos degraus altos, e o ferreiro NPC das cidades continua sendo a alternativa.
- 🔶 **Resistência ao fogo:** usa `resistencia_dano`, que já existe, mas depende do modelo de elementos da seção 8 (❓). Sem elementos, a habilidade não tem o que reduzir.
- 🔶 **Atributos:** os testes novos devem usar os atributos novos. Afanar: Destreza do ladrão contra Agilidade do alvo (mesma lógica do acerto, seção 5); consertar e criar: Inteligência e/ou Destreza mais o degrau, e Sorte influenciando a chance (a decidir).

### Em aberto

- ❓ **O que é o "nível" dos degraus:** o nível do personagem (7, 13, 21) ou o nível da própria habilidade (1, 2, 3…)? Na proposta acima o degrau é a ordem na cadeia e o nível do personagem é só o requisito. ✅ **Decidido em 24/10/2026** (§15).
- ❓ Faltam mecânicas novas no motor (hoje só há resistência, dano, defesa e precisão): **consertar** (teste e chance, o que acontece na falha), **criar** (receitas, materiais, tempo), **afanar** (teste, risco de ser pego, o que se pode furtar, consequência com NPCs), **poções** (criação a partir de ingredientes).
- ❓ **Receitas e materiais:** lista de ingredientes e de materiais de forja (o mesmo material das criações de pergaminho da seção 10).
- ❓ Habilidades de profissão sem ação de combate precisam de um tipo novo de ação fora de combate (`actionKind`), já que hoje elas são só `MODIFIER`.
- ❓ Converter por **famílias e ondas** (ver análise acima) em vez de profissão a profissão; as não convertidas mantêm a passiva genérica até a sua vez, sem bloquear o resto. Confirmar o agrupamento e a ordem.
- ❓ Quem escolhe profissão na criação: só uma, ou classe + profissão (como hoje), e se o degrau 1 de ambas vem ativo. ✅ **Decidido em 24/10/2026** (§15).

## 12. Itens de cura e escala

- ❓ Poções e pergaminhos (cura 3/5/10/20 PV, bandagem 2, pergaminhos 6/12) foram calibrados para PV entre 4 e 19. Com Resistência 3 o PV novo (15) é parecido com o antigo (13), então o problema aparece com Resistência alta: manter fixo, multiplicar ou passar para percentual? ✅ **Decidido em 24/10/2026** (§15).
- `perigos.txt` já é percentual da vida máxima (15/30/60/100) e não muda.

---

## 13. Arquivos a alterar na implementação

Índice por arquivo. Cada item é executado no módulo indicado na seção 14.

- `atributos.txt`: novos atributos (Inteligência, Agilidade, Destreza, Sorte), remover teto.
- `recursos.txt`: PV = 5 × Resistência (mínimo 1); PM com Inteligência; rever PA.
- `criacao.txt`: atributos de ataque, defesa e iniciativa; valores-base de combate.
- `dano.txt`: golpe e escala de dano.
- `condicoes.txt` + `Commands.kt` + `Events.kt` + `CheckMenu.kt`: dano percentual.
- `UnifiedCombat.kt`: fator de defesa 0,65, arredondar para cima, defesa sem negativo, crítico com d10 e teto de 70% pelos atributos (itens até 100%), teste Destreza × Agilidade.
- `tracos.txt`: requisitos e vantagens ligados a atributos.
- `itens.txt` / `criacao.txt`: cura de poções e pergaminhos.
- `itens.txt` + `FORMATO_PACOTE.md` + parser de itens: slots luvas, botas e calça; efeitos de atributo e de PV máximo; encantamentos permanentes.
- Novos dados: catálogo de encantamentos e de pergaminhos de encantamento; efeito de encantar na profissão Ferreiro, vantagens Mestre das Armas e Mestre dos Equipamentos, e habilidade de escrever pergaminhos (`personagem-catalogo.json` / `tracos.txt`).
- `itens.txt` + parser: nível mínimo, peso do equipamento e restrição por classe, qualidades normal/ótimo/raro/épico, escudo só com arma de uma mão, arma de duas mãos, penalidade de peso e talento que a anula.
- `habilidades-arvore.txt` + `personagem-catalogo.json`: trocar as passivas genéricas de profissão por habilidades específicas (degrau 1 ativo na criação, degraus por nível); novo tipo de ação fora de combate; mecânicas de consertar, criar, afanar e criar poções; receitas e materiais; ligação com o nível mínimo dos equipamentos.
- `progressao.txt` + `criacao.txt` (`combate_xp_vitoria`) + `Commands.kt` (`maxQuestXp`): curva de XP sem teto (5 × N por nível) e XP de missão e batalha balanceado por nível (§2.1).
- Decidir o que fazer com o combate antigo (`GameEngine.attack`, d20 contra 10 + Habilidade).

---

## 14. Plano de implementação

Legenda de status: ⬜ não iniciado · 🟡 em andamento · ✅ fechado (documentado e zip devolvido) · ⛔ bloqueado.

### 14.1 Regras de execução

1. **Um módulo por vez, na ordem.** Não se adianta trabalho de outro módulo. Algo fora do escopo vira nota ou ❓ neste documento.
2. **Antes de começar:** partir sempre do **último zip devolvido** (no primeiro módulo, do `RPG-solo-main-completo.zip` original) e ler este documento. Se o módulo tiver ❓ em "Fechar antes", perguntar ao proprietário (pergunta curta) antes de escrever código.
3. **Fechamento obrigatório de TODO módulo** (nesta ordem; o módulo só vale como ✅ depois disso):
   1. Atualizar **este documento**: status do módulo na tabela 14.2, decisões novas (✅) e ❓ que sobraram.
   2. Atualizar o **`CHANGELOG.md`** no formato do repositório (`## data — escopo: título (N-xx, estado de validação)`, com as linhas "ASSUMINDO" e "Validação").
   3. Atualizar a **documentação afetada**: `docs/pacotes/FORMATO_PACOTE.md` (se mudou formato de arquivo de dados), `docs/MECANICA_DADOS.md` (se mudou regra de dados) e o "Andamento" correspondente em `PLANO_RPG_SOLO.md`.
   4. Gerar o **zip completo** do repositório e **devolvê-lo** com um resumo curto: o que mudou, o que foi assumido, o que ficou de fora.
4. **Zip:** nome `RPG-solo-N-xx.zip` (id do módulo), mesma estrutura do zip recebido (pasta raiz `RPG-solo-main/`), **sem** `build/`, `.gradle/` e `.git/`. Este documento acompanha o zip em `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md` (cópia criada no N-01 e mantida em dia a cada módulo).
5. **Validação honesta:** só se escreve "validado" se o build ou os testes rodaram de fato. Sem Gradle/Android SDK no ambiente, o registro é "nada compilado nem executado", como o repositório já faz.
6. **Testes:** atualizar os testes que o módulo quebra (valores antigos de PV, dano e atributos) e **criar os testes novos que o módulo precisar** (✅ autorizado pelo proprietário em 23/10/2026 para toda esta rodada, N-01 a N-28; exceção à regra geral do projeto de não criar testes). Sem Gradle no ambiente, os testes ficam "escritos, não executados" (regra 5).
7. **Compatibilidade:** campanhas e fichas já salvas continuam abrindo (campos novos com valor padrão, como o repositório já faz). Quebra inevitável vira ❓ antes de implementar.
8. **Parar ao fim do módulo** e esperar o "ok" do proprietário para o próximo.

### 14.2 Visão geral

| Módulo | Nome | Fase | Status | Zip |
|---|---|---|---|---|
| N-01 | Decisões da fundação (só documentação) | 15 | ✅ | `RPG-solo-N-01.zip` |
| N-02 | Sete atributos, sem teto | 15 | ✅ (não compilado) | `RPG-solo-N-02.zip` |
| N-03 | Recursos: PV, PM e PA | 15 | ✅ (não compilado) | `RPG-solo-N-03.zip` |
| N-04 | Progressão sem teto | 15 | ✅ (não compilado) | `RPG-solo-N-04.zip` |
| N-05 | Dano e defesa | 16 | ✅ (não compilado) | `RPG-solo-N-05.zip` |
| N-06 | Acerto, esquiva e iniciativa | 16 | ✅ (não compilado) | `RPG-solo-N-06.zip` |
| N-07 | Crítico e Sorte | 16 | ✅ (não compilado) | `RPG-solo-N-07.zip` |
| N-08 | Condições percentuais | 16 | ✅ (não compilado) | `RPG-solo-N-08.zip` |
| N-09 | Combate único | 16 | ✅ (não compilado) | `RPG-solo-N-09.zip` |
| N-10 | Traços e requisitos | 16 | ✅ (não compilado) | `RPG-solo-N-10.zip` |
| N-11 | Slots e efeitos de equipamento | 17 | ✅ (não compilado) | `RPG-solo-N-11.zip` |
| N-12 | Nível mínimo e qualidades | 17 | ✅ (não compilado) | `RPG-solo-N-12.zip` |
| N-13 | Peso, classe e penalidade | 17 | ✅ (não compilado) | `RPG-solo-N-13.zip` |
| N-14 | Escudo e arma de duas mãos | 17 | ✅ (não compilado) | `RPG-solo-N-14.zip` |
| N-15 | Sorte de equipamento e teto de crítico | 17 | ✅ (não compilado) | `RPG-solo-N-15.zip` |
| N-16 | Elementos e condições em habilidades | 18 | ✅ | escrito, não compilado |
| N-17 | Encantamento do ferreiro | 18 | ✅ | escrito, não compilado |
| N-18 | Pergaminhos de encantamento | 18 | ✅ | escrito, não compilado |
| N-19 | Estrutura das habilidades por degrau | 19 | ✅ | escrito, não compilado |
| N-20 | Mecânicas comuns de ofício | 19 | ✅ | escrito, não compilado |
| N-21 | Onda 1a: Forja e armas, Vestuário e couro (12) | 19 | ✅ | escrito, não compilado |
| N-22 | Onda 1b: Preparos, Cuidados, Furtividade (16) + classes de apoio | 19 | ✅ | escrito, não compilado (cura das classes de apoio no posto 6, 24/10/2026) |
| N-23 | Onda 2: Exploração, Comércio, Mecanismos (23) | 19 | ✅ | escrito, não compilado (coleta de materiais feita por receitas `col-*` (24/10/2026, não compilada)) |
| N-24 | Onda 3: Mar, Animais, Artes, Identificação (21) | 19 | ✅ | escrito, não compilado (pesca, mergulho e apresentações feitas por receitas (24/10/2026, não compiladas); inspirar feito (24/10/2026, não compilado); montaria e ave feitas; companheiro e tatuagem pendentes) |
| N-25 | Joias e Construção (14) | 19 | ⛔ | |
| N-26 | Itens de cura e escala | 20 | ✅ | escrito, não compilado |
| N-27 | NPCs e monstros | 20 | ✅ | escrito, não compilado |
| N-28 | Revisão geral e validação | 20 | ✅ | documentação e conferência estática; suíte não executada (sem rede) |

Cada módulo abaixo termina com o **fechamento da regra 3**; ele não é opcional.

### 14.3 Fase 15 — Fundação: atributos, recursos e progressão

**N-01 Decisões da fundação** (só documentação) ✅
- Decidido: Habilidade = PA e bônus em testes de ofício/perícia, iniciativa na Agilidade (§2); 10 pontos de status + 2 de habilidade/profissão (§2); curva de XP 5 × N sem teto e diretriz de balanceamento de XP de missão e batalha (§2, §2.1); ligação `pv` × `hp` no N-03 (§3); testes novos autorizados (regra 6).
- Entregue: seções 2, 2.1 e 3 atualizadas; cópia deste documento em `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md`; `CHANGELOG.md` e `PLANO_RPG_SOLO.md` (§3.10) atualizados; `RPG-solo-N-01.zip`.
- Ficou para os módulos: fórmula de PM com Inteligência e de PA com Habilidade (N-03); percentuais de XP de batalha e missão (N-04, refinado no N-27); papel da Habilidade nos requisitos de traços (N-10).
- ☑ Fechamento (regra 3).

**N-02 Sete atributos, sem teto** ✅ (escrito, não compilado nem executado)
- Arquivos: `atributos.txt`, `criacao.txt`, `PackageParser.kt`/`RulePackage.kt` (validação de máximo), `CharacterDraft.kt`, tela de criação do `app`.
- Pronto quando: Poder, Habilidade, Resistência, Inteligência, Agilidade, Destreza e Sorte existem; sem máximo; 10 pontos na criação e +1 por nível; fichas antigas abrem com os atributos novos em 0.
- Entregue: `atributos.txt` com os sete atributos (`min` 0, padrão 0) e `max` = `-` (sem teto, guardado como `StatDef.NO_CAP` = `Int.MAX_VALUE`; o parser ainda aceita teto numérico); `StatDef.uncapped`; trecho de regra "sem teto"; tela de evolução mostra "N · atributo (sem teto)"; `AllocateStatPoints` passou a usar o padrão do pacote (0) quando a ficha antiga não tem o atributo novo; tela de criação não precisou mudar (o botão "+" já só olhava `max`; `criacao.txt` já tinha `pontos|10` e +1 de status e +1 de habilidade por nível). Testes novos: `SeteAtributosTest` (rules, 6) e `AtributosSemTetoTest` (engine, 3), escritos e não executados.
- Decisões e riscos registrados: (1) o atributo **`sorte`** tem o mesmo id do teste `sorte` em `testes.txt` e do escopo de bônus `sorte` do traço Sortudo; o parser não proíbe (ids só são únicos por arquivo) e o teste de sorte não usa o atributo (`mult` 0) até o N-07, que decide a ligação; (2) `combate_atributo_ataque/defesa/iniciativa` e as fórmulas de `recursos.txt` **não mudaram** (ficam para N-03, N-05 e N-06); (3) `tracos.txt` segue com requisitos `poder>=N`/`habilidade>=N` até o N-10.
- ✅ **Distribuição inicial 10 + 2 (decidido pelo proprietário em 24/10/2026):** `AllocateInitialBuild` agora exige exatamente 10 pontos de status entre os atributos e 2 pontos de habilidade (antes: 12 livres). Os totais vêm de `criacao.txt` (`pontos_iniciais_status`, `pontos_iniciais_habilidade`) por `EngineConfig.initialStatPoints/initialAbilityPoints` (padrão 10 e 2; campanhas antigas continuam abrindo). A tela da árvore de habilidades pede só os 10 de status e concede os 2 de habilidade automaticamente. Testes: `AtributosSemTetoTest` (+2) e `SeteAtributosTest` (config). ❓ Revisar no N-04/N-19 se os 10 pontos da criação da ficha (`pontos|10` em `criacao.txt`) e os 10 da distribuição inicial do motor não contam duas vezes.
- ☑ Fechamento (regra 3).

**N-03 Recursos: PV, PM e PA** ✅ (escrito, não compilado nem executado)
- Depende de: N-02. Arquivos: `recursos.txt`, `RulePackage.kt` (`resourcesFor`), `Adaptation.kt`, `NpcCreation.kt`, `GameEngine.kt` (recurso de dano padrão).
- Pronto quando: PV = 5 × Resistência com mínimo 1 (Resistência 0 = 1 PV, 3 = 15); PM com Inteligência; PA vindo da Habilidade e não mais do Poder; jogador e NPC usam a mesma conta; `pv` e `hp` ligados sem divergência.
- Entregue: `recursos.txt` do sistema próprio com PV `0 + 5 × resistencia` (mín. 1), PM `0 + 2 × inteligencia`, PA `1 + 1 × habilidade` (mín. 1); coluna opcional 6 `minimo` (≥ 0, padrão 0) em `recursos.txt` (`ResourceDef.min`, `PackageParser`, `RulePackage.resourcesFor`), documentada em `FORMATO_PACOTE.md`; condições sem `recurso` herdam `morte` do manifesto. NPC com pacote já passa por `buildFicha`/`resourcesFor` (mesma conta do jogador); NPC sem pacote segue com os recursos informados e marcado como não validado. Teste novo: `RecursosN03Test` (rules, 5), escrito e não executado.
- ASSUMINDO: PM = 2 × Inteligência e PA = 1 + Habilidade (fórmulas fechadas aqui, sem confirmação do proprietário; ajustáveis só em `recursos.txt`). Fichas/entidades já salvas não são recalculadas. `combate_atributo_defesa|habilidade` e `combate_atributo_iniciativa|habilidade` em `criacao.txt` **não mudaram** (N-05 e N-06); a defesa ainda usa Habilidade. O custo de PM das habilidades da árvore (1–7) agora é pago com um PM bem menor (sem Inteligência, PM = 0): ❓ revisar no N-19. Traços `max_<recurso>` somam ao total antes do mínimo (o mínimo vale sobre o total).
- ☑ Fechamento (regra 3).

**N-04 Progressão sem teto** ✅ (escrito, não compilado nem executado)
- Depende de: N-01 (curva decidida). Arquivos: `progressao.txt`, `criacao.txt` (`combate_xp_vitoria`), `Commands.kt` (`maxQuestXp`), `CombatActions.awardCombatVictoryXp`, `GameEngine.awardQuestXp`, tela de nível.
- Pronto quando: existe XP para qualquer nível (5 × N por nível, sem teto; níveis 2 a 6 inalterados); cada nível dá +1 de status e +1 de habilidade; `maxQuestXp` vira fórmula; o XP de vitória em batalha escala com o nível (hoje `combate_xp_vitoria|5` fixo) e o XP de missão respeita o teto do nível (§2.1); testes novos de curva, teto de missão e XP de batalha por nível.
- Entregue: `EngineConfig.levelCostFactor` (`custo_nivel_fator|5` em `criacao.txt`): `levelFor` continua a curva além de `progressao.txt` (nível N custa 5 × N; níveis 2–6 inalterados; nível 10 = 270, 100 = 25.245); `levelCost(nível)`; `maxQuestXp` virou o custo do nível atual por fórmula; `EngineConfig.victoryXpPercent` (`combate_xp_vitoria_percentual|8`) e `battleXp(nívelJogador, nívelInimigo)`; `CombatActions.awardCombatVictoryXp` paga por inimigo derrotado (sem o percentual, mantém o `combate_xp_vitoria` fixo). Pontos por nível (+1 status, +1 habilidade) já valiam sem teto. Teste novo: `ProgressaoSemTetoTest` (engine, 5), escrito e não executado.
- ASSUMINDO: modulação por diferença de nível = +20% por nível acima (máx. 200%) e −20% por nível abaixo, 0 a partir de 5 abaixo; mínimo 1 XP; percentual 8% proposto no §2.1; faixas de missão curta/média/grande (25/50/100%) ficam como orientação ao mestre, só o teto é imposto; NPC sem nível usa 1 (categoria de ameaça no N-27). ❓ 10 pontos da ficha (`pontos|10`) × 10 da distribuição inicial: seguem sem revisão (N-19).
- ☑ Fechamento (regra 3).

### 14.4 Fase 16 — Combate

**N-05 Dano e defesa** ✅ (escrito, não compilado nem executado)
- Depende de: N-03. Arquivos: `UnifiedCombat.kt`, `EffectiveValue.kt`, `dano.txt`, `CombatBonuses` (`app`).
- Pronto quando: defesa base = Resistência; dano = ataque − 0,65 × defesa, arredondado para cima, mínimo 0; defesa rolada nunca negativa; confere com a simulação da seção 4.
- Entregue: `UnifiedCombat.damageOf` = ataque − 0,65 × defesa, arredondado para cima (inteiro: ataque − ⌊65 × defesa / 100⌋), mínimo 0; `ClashResult.defenseTotal` nunca negativo; `combate_atributo_defesa|resistencia` em `criacao.txt` (defesa base = Resistência). Testes de `UnifiedCombatTest` atualizados (+1 novo), escritos e não executados.
- ASSUMINDO: a defesa continua rolada pela expressão do valor efetivo (valor baixo vira 1d6 − N, agora com piso 0 no total); armadura e escudo entram pelos modificadores já existentes (equipamento só muda nos N-11 a N-14); o caminho antigo de `Combat.kt`/`GameEngine.attack` (defesa como alvo de teste) não mudou e é unificado no N-09; `combate_defesa_base|10` e o recurso `hp` por padrão no motor ficam como estão. A defesa de NPCs sem atributo `resistencia` vale 0.
- ☑ Fechamento (regra 3).

**N-06 Acerto, esquiva e iniciativa** ✅ (escrito, não compilado nem executado)
- Depende de: N-05. Fechar antes: confirmar o fluxo (acerto → crítico → dano) da seção 5.
- Arquivos: `UnifiedCombat.kt`, `Combat.kt`, `CombatActions` (`app`).
- Pronto quando: Destreza do atacante × Agilidade do defensor decide o acerto, automático e igual para jogador, NPC e monstro; a iniciativa passa a usar a Agilidade (§2).
- Entregue: teste de acerto no `UnifiedCombat.resolve` (primeiro passo; `HitSpec`/`HitCheck`): chance = 50% + 5% × (Destreza − Agilidade), entre 5% e 95%, um d100 em silêncio (acerta com dado ≤ chance); erro encerra o confronto sem crítico, ataque, defesa nem dano. Chaves `combate_atributo_acerto|destreza` e `combate_atributo_esquiva|agilidade` em `criacao.txt` (validadas juntas e contra os atributos), `combate_atributo_iniciativa|agilidade`. Mesmo caminho (`CombatActions.unifiedAttack`) para jogador, NPC e monstro. Registro e cartão (`ClashRecorded.hitChance/hitRoll/missed`, `ClashReport`) mostram o acerto; mensagem "errou o ataque". Pacotes sem as chaves não têm teste de acerto (comportamento antigo). Testes novos em `UnifiedCombatTest` (+3), escritos e não executados.
- ASSUMINDO: fluxo acerto → crítico → dano (seção 5) confirmado pelo proprietário com "Siga"; a escala 50% + 5% por ponto e os limites 5%/95% são proposta minha (ajustáveis em `UnifiedCombat.HIT_*`); acerto usa o valor efetivo fixo dos atributos (com modificadores de escopo `destreza`/`agilidade`), sem rolar dado de atributo; o ataque especial/caminho antigo de `Combat.kt` não passa pelo teste de acerto até o N-09.
- ☑ Fechamento (regra 3).

**N-07 Crítico e Sorte** ✅ (escrito, não compilado nem executado)
- Depende de: N-06. Fechar antes: d10 confirmado e o efeito da Sorte em testes (§6), sem confundir com `recurso_sorte|pa`.
- Arquivos: `UnifiedCombat.kt`, `EffectiveValue.kt`, `testes.txt` (se a Sorte entrar em testes).
- Pronto quando: crítico com limite = lados − bônus (mínimo 1); no crítico o ataque vale o valor cheio; 70% é o teto pelos atributos (Sorte 6).
- Entregue: crítico em d10 (`CRIT_BASE_THRESHOLD`/`CRIT_DIE_SIDES` = 10, base 10%); limite = 10 − min(Sorte, 6) − bônus `crit`, mínimo 1 (Sorte 6 = limite 4 = 70%; itens/traços `crit` levam até 100%); no crítico o ataque continua valendo o valor cheio, sem dado. `combate_atributo_sorte|sorte` em `criacao.txt` (validado contra os atributos); `unifiedAttackBetween(luckStat)` lê a Sorte efetiva do atacante, igual para jogador e NPC. Testes de `UnifiedCombatTest` atualizados e +2, escritos e não executados.
- ASSUMINDO: a Sorte **só** liga ao crítico neste módulo; "ajudar em testes e outras coisas" fica fora (o teste `sorte` de `testes.txt` segue com `mult` 0 e o `recurso_sorte|pa` não mudou). A troca de d6 por d10 vale para todo pacote (antes: 16,7% de base). Sorte negativa conta como 0.
- ☑ Fechamento (regra 3).

**N-08 Condições percentuais** ✅ (escrito, não compilado nem executado)
- Depende de: N-03. Fechar antes: confirmar 10% / 5% / 15% (§7).
- Arquivos: `condicoes.txt`, `Commands.kt` (≈ linha 979), `Events.kt`, `CheckMenu.kt`, parser de condições.
- Pronto quando: dano por rodada em % do PV máximo, arredondado para cima, mínimo 1; `sangramento`, `envenenado` e `queimando` migrados; textos da interface corrigidos.
- Entregue: `ConditionState.damagePercent` (0–100) e `tickDamage(máximo)` = ⌈máx × % / 100⌉, mínimo 1; propagado por `AddCondition`, `ConditionAdded`, `PowerEffect.Inflict`, `ConditionDef` (coluna 6 de `condicoes.txt` aceita `N%`), `CheckMenu`, `InventoryActions`, `PowerMenu`; o fim da rodada (`NextTurn`) usa o máximo do recurso no momento do tique; `sangramento` 10% × 3, `envenenado` 5% × 3 e `queimando` 15% × 2 migrados, com descrições novas; textos do `CheckMenu` e do trecho de regras mostram o percentual. Testes novos: `CondicoesPercentuaisTest` (engine, 5) e `CondicoesPercentuaisPacoteTest` (rules, 1), escritos e não executados.
- ASSUMINDO: 10% / 5% / 15% confirmados pelo proprietário com "Siga"; o dano fixo antigo continua válido (pacotes importados e eventos salvos abrem com `damagePercent` 0); percentual sobre o máximo do recurso no momento do tique (não no da aplicação); condições continuam ignorando defesa e escudo não é tratado aqui.
- ☑ Fechamento (regra 3).

**N-09 Combate único** ✅ (escrito, não compilado nem executado)
- Depende de: N-05 a N-07. Fechar antes: o que fazer com `GameEngine.attack` (d20 + Poder contra 10 + Habilidade) (§13).
- Arquivos: `GameEngine.kt`, `CombatActions` (`app`), testes que usam o combate antigo.
- Pronto quando: só um sistema de ataque em uso, sem regra duplicada.
- Entregue: `CombatActions.attack` (golpe do jogador, multiataque, habilidades com custo e resistência elemental) e `unifiedAttack`/`resolveNpcTurn` agora passam pelo mesmo `strike` → `unifiedAttackBetween` (acerto, crítico, ataque × defesa, dano 65%). O ataque soma ao valor efetivo os bônus de escopo `ataque` e `dano`, o bônus extra da habilidade, o deslocamento de dados das condições; a defesa soma a postura defensiva (`combate_bonus_defender`), antes ignorada pelo ataque de NPC. Novo `UnifiedAttackSpec.damageReduction` (resistência a dano/elemental do alvo, subtraída do dano final, mínimo 0) e consumo da postura em `GameEngine.unifiedAttack`. `GameEngine.attack`/`AttackSpec` ficam `@Deprecated`, sem uso no app (mantidos por testes e compatibilidade). Teste novo: `CombateUnicoTest` (engine, 3), escrito e não executado.
- ASSUMINDO: dano de equipamento/traços (`dano`) vira bônus de ataque, pois no pipeline unificado o ataque rolado é o dano; `combate_dano`/perfil `golpe` (1d6) e `combate_dados_ataque`/`combate_comparacao_ataque`/`combate_defesa_base` não são mais lidos pelo ataque (continuam no pacote para outros usos e para a iniciativa); a remoção definitiva de `GameEngine.attack` e dos testes antigos fica para o N-28. Categorias de ameaça e caminho sem pacote (§9) não entram aqui (N-27).
- ☑ Fechamento (regra 3).

**N-10 Traços e requisitos** ✅ (escrito, não compilado nem executado)
- Depende de: N-02. Arquivos: `tracos.txt` (requisitos `poder>=N` e `habilidade>=N`), `criacao.txt`. Considerar o novo papel da Habilidade (§2).
- Pronto quando: requisitos e vantagens coerentes com os sete atributos, sem traço inalcançável.
- Entregue: em `tracos.txt`, 93 requisitos `habilidade>=N` migraram pelo tema do traço (ver §2): Inteligência 53, Agilidade 21, Destreza 18, Sorte 1; `estocada-perfeita` (`poder>=1;destreza>=2`) e `corte-perfeito` (`poder>=2;destreza>=1`) trocaram só a parte de Habilidade; os 59 restantes seguem em Habilidade (ofício/técnica/perícias) e `poder`/`resistencia` ficaram iguais. `arquetipos.txt`: os pontos de Habilidade dos arquétipos afetados foram redistribuídos entre os atributos novos para cada traço e cada item do cardápio (`opcoes-arquetipo.txt`) continuar alcançável (ex.: Elfo = Habilidade 2, Inteligência 2, Agilidade 2; Duelista = Agilidade 3, Destreza 2; nenhum passa de 10 pontos). `exemplos.txt`: Explorador de exemplo = poder 1, habilidade 1, resistência 1, agilidade 2 (para `reflexos`). Testes: `TracosRequisitosN10Test` (rules, 5) e `EquilibrioEOpcoesTest` ajustado (`reflexos` → `visao-noturna` na lista de vantagens gerais; `medio` ganha Agilidade 2 para o teste do `voo`), escritos e não executados.
- ASSUMINDO: o mapeamento por tema e os limiares N mantidos são proposta minha (ajustáveis só em `tracos.txt`); `bestiario-vivo` ficou em Habilidade para o Caçador de monstros não passar de 10 pontos; fichas já salvas não são revalidadas (o requisito só vale na criação/edição). Catálogo de classes/profissões e árvore de habilidades não mudaram (N-19).
- ☑ Fechamento (regra 3).

### 14.5 Fase 17 — Equipamentos

**N-11 Slots e efeitos de equipamento**
- Depende de: N-02, N-05. Arquivos: `itens.txt`, `FORMATO_PACOTE.md`, parser de itens, `InventoryActions`.
- Pronto quando: seis peças (elmo → Sorte, luvas → Destreza, botas → Agilidade, calça → defesa, peito → PV máximo, arma → ataque ou Inteligência); efeitos de atributo e de PV máximo aceitos; itens atuais (`corpo`, `cabeca`) redistribuídos.
- Entregue: efeitos de item aceitam o id de qualquer atributo e `max_<recurso>` (validados no pacote; os de atributo entram no combate pelo provedor existente: acerto, esquiva, crítico, ataque e defesa). `EquipItem`/`UnequipItem` ganharam `maxDelta` (padrão vazio) e `EquipmentChanged.maxDelta`: equipar soma ao máximo e ao atual, desequipar reduz o máximo, limita o atual e nunca mata; eventos antigos abrem. `InventoryActions` calcula o delta de `max_<recurso>` do item. `itens.txt`: `capacete` → `elmo` (`sorte:+1`); `roupa-reforcada`, `couro-batido`, `cota-de-malha` → `peito` (`max_pv:+2/+4/+6`, malha mantém `ataque:-1`); novos `luvas-de-couro` (`destreza:+1`), `botas-de-couro` (`agilidade:+1`), `calca-reforcada` e `calca-de-couro` (`defesa:+1/+2`), `cajado` (`inteligencia:+1`). `FORMATO_PACOTE.md` atualizado. Testes novos: `EquipamentoMaxRecursoTest` (engine, 3) e `SlotsEquipamentoN11Test` (rules, 3), escritos e não executados.
- ASSUMINDO: valores dos bônus (`max_pv` 2/4/6, +1/+2 de defesa) são proposta minha, a recalibrar no N-12 (faixas por nível); `inteligencia` em arma só vale como valor efetivo (não recalcula PM da ficha); `sorte` de elmo passa a somar ao crítico até o teto de 70% (o excedente é do N-15); fichas já salvas com `corpo`/`cabeca` equipados mantêm o item, mas sem slot válido no catálogo novo (podem desequipar); itens já equipados não recebem `max_pv` retroativo.
- ☑ Fechamento (regra 3).

**N-12 Nível mínimo e qualidades**
- Depende de: N-11. Fechar antes: multiplicadores de normal, ótimo, raro e épico e o destino de "lendária" (§10); bônus base por faixa de nível.
- Arquivos: `itens.txt`, `ItemQuality.kt`, parser.
- Pronto quando: todo item tem nível mínimo (1, 5, 10, 20, 30…); épico só do nível 10 em diante.
- ✅ **Decidido pelo proprietário (24/10/2026):** normal 100%, ótimo 125%, raro 150%, épico 200%, **lendária mantida acima, 250%**.
- Entregue: `ItemQuality` = normal, ótima (o id antigo `boa` continua valendo), rara, épica, lendária (`minLevel` 10 para épica e lendária); `criacao.txt`: `qualidade_otima_percent|125`, `qualidade_epica_percent|200`, `qualidade_lendaria_percent|250` (validação crescente). `ItemDef.minLevel` (coluna 8 opcional `nivel` em `itens.txt`, padrão 1; só equipamento); `RulePackage.requiredLevel(id)` = maior entre o nível do item e o da qualidade; `EquipItem.minLevel` faz o motor recusar equipar abaixo do nível (o app envia `requiredLevel`). Itens: `espada-longa`, `cota-de-malha`, `escudo-pesado`, `calca-de-couro` nível 5; os demais nível 1. Texto de regra mostra "Nível mínimo". Testes: `NivelMinimoQualidadeN12Test` (rules, 4), `EquipamentoNivelMinimoTest` (engine, 1), `ItensQualidadeTest` ajustado (lendária 250%), escritos e não executados.
- ASSUMINDO: faixas 1/5/10/20/30 só nos itens atuais; o **bônus base por faixa de nível** (❓ da seção 10) segue em aberto e entra quando houver itens de nível alto (N-17/N-21/N-26); a trava de nível vale ao equipar (itens já equipados não são desequipados); inventários com `#boa` seguem funcionando como ótima.
- ✅ **Bônus base por faixa de nível (decidido em 24/10/2026):** bônus por peça = ⌈nível ÷ 10⌉; a qualidade multiplica e arredonda para cima; no peito o bônus vira PV máximo (5 × o bônus). Implementado em 24/10/2026 (não compilado): coluna `escala` de `itens.txt`, vale para itens novos que a usem.
- ☑ Fechamento (regra 3).

**N-13 Peso, classe e penalidade**
- Depende de: N-12. Fechar antes: efeito da vantagem que anula a penalidade e seu requisito (§10).
- Arquivos: `personagem-catalogo.json` (peso por classe: 13 pesadas, 18 médias, 19 leves), `itens.txt`, `tracos.txt`.
- Pronto quando: peça de peso errado vale metade; armas têm tipo ligado à classe; **Mestre das Armas** e **Mestre dos Equipamentos** anulam a penalidade.
- ✅ **Decidido em 24/10/2026:** **Mestre das Armas** = usa qualquer arma sem penalidade; **Mestre dos Equipamentos** = sem penalidade de classe nos equipamentos. Ambos são vantagens obtidas **na criação**, pagas por desvantagens pela tabela de equilíbrio existente. Peça de peso errado vale **metade dos bônus positivos** (arredondado para cima); penalidades da peça (ex.: `ataque:-1` da cota de malha) continuam inteiras.
- Entregue: `ItemDef.weight` (`ItemWeight` pesado/médio/leve, coluna 9 `peso` de `itens.txt`) e `ItemDef.weaponType` (coluna 10 `tipo`); `personagem-catalogo.json`: cada uma das 50 classes ganhou `armorWeight` (13 pesadas, 18 médias, 19 leves, como no §10) e `weaponTypes`. Vantagens `mestre-das-armas` e `mestre-dos-equipamentos` em `tracos.txt` (escopos de traço `mestre_armas`/`mestre_equipamentos`, sem desvantagem obrigatória, equilíbrio pela tabela existente). `RulePackage.equipmentBonus(..., wearer)` aplica metade (arredonda para cima, depois da qualidade) só aos bônus positivos; `isRestricted` e `wearerProfile`; `WearerProfile` (novo `EquipmentRestriction.kt`). `CombatBonuses.wearerProfile` (só o herói) liga o perfil ao combate (`CombatActions`) e ao `max_<recurso>` do equipar/desequipar (`InventoryActions`); o inventário mostra "fora da classe: bônus pela metade". Itens com peso: `roupa-reforcada`, `luvas-de-couro`, `botas-de-couro`, `calca-reforcada` leves; `couro-batido`, `capacete`, `calca-de-couro` médios; `cota-de-malha` pesada. Tipos: espada (`espada-curta`, `espada-longa`), adaga, clava, lança, machado, arco (`arco-curto`), cajado. Correção relacionada: o validador do `PackageParser` ainda recusava efeitos de item de atributo e `max_<recurso>` (só aceitava ataque/dano/defesa), contrariando o N-11; agora aceita. Testes novos: `PesoClassePenalidadeN13Test` (rules, 12), escritos e não executados.
- ASSUMINDO: pesos dos itens atuais e tipos de arma por classe são proposta minha (pesada: espada, machado, clava, lança; média: espada, adaga, lança, arco; leve: cajado, adaga; arqueiro, atirador de precisão e pistoleiro usam arco; ladino, assassino e agente das sombras, adaga e espada; sem arma de fogo no catálogo, o arco serve de proxy); com várias classes vale a união de pesos e tipos; classe sem peso declarado não restringe; NPC e entidades sem classe vestem sem penalidade; o escudo (`outra-mao`) fica sem peso até o N-14; o `max_<recurso>` é calculado ao equipar e ao desequipar com o perfil do momento (trocar de classe ou de vantagem com a peça vestida pode deixar o máximo diferente; itens já equipados não são recalculados); as vantagens Mestre só são obtidas na criação pela tabela de equilíbrio, sem requisito de nível ou atributo.
- ☑ Fechamento (regra 3).

**N-14 Escudo e arma de duas mãos**
- Depende de: N-11. Fechar antes: duas mãos bloqueia o escudo? (§10).
- Arquivos: `itens.txt`, parser, `InventoryActions`.
- Pronto quando: escudo só com arma de uma mão, dá Resistência (bônus menor que de uma peça comum); arma de duas mãos marcada no formato.
- ✅ **Decidido em 24/10/2026:** arma de duas mãos **bloqueia** o escudo e compensa com **ataque ×1,5** sobre a arma de uma mão equivalente; escudo dá Resistência, com bônus menor que o de uma peça comum.
- Entregue: `ItemDef.hands` (coluna 11 `maos`, 1 ou 2; só arma no slot `mao`); `RulePackage.handsConflict` (duas mãos bloqueia o escudo nos dois sentidos, chamado em `InventoryActions.equipItem`); `equipmentBonus` multiplica por 1,5 (arredonda para cima, depois da qualidade e antes da metade do N-13) os bônus positivos de `ataque` de arma de duas mãos; escudos passam a dar Resistência (`resistencia:+1;max_pv:+5`, 5 PV e +1 de defesa pelo atributo de defesa `resistencia`), o pesado mantém `ataque:-1`; novos `montante` (espada, 2 mãos, nível 5) e `arco-longo` (arco, 2 mãos, nível 5). Testes novos: `EscudoDuasMaosN14Test` (rules, 4), escritos e não executados.
- ASSUMINDO: bônus do escudo é +1 de Resistência em qualquer nível (≤ peça comum; escala por faixa de nível entra com os itens de nível alto, N-21/N-26); ×1,5 vale só para `ataque` (não para `dano`); montante e arco longo têm o ataque declarado igual ao da arma de uma mão equivalente (a 1,5 vira +2); escudo sem peso; o bloqueio vale ao equipar (itens já equipados não mudam); NPC não passa por `InventoryActions`.
- ☑ Fechamento (regra 3).

**N-15 Sorte de equipamento e teto de crítico**
- Depende de: N-07, N-11. Arquivos: `UnifiedCombat.kt`, `EffectiveValue.kt`, `itens.txt`.
- Pronto quando: Sorte de elmo e encantamentos passa do teto de 70%, +10% por ponto, até 100%.
- Entregue: `checkCrit`/`UnifiedCombat.resolve`/`UnifiedAttackSpec` ganharam `luckBonus` (Sorte de itens, sem teto): limite = 10 − min(Sorte de atributo, 6) − `luckBonus` − bônus `crit`, entre 1 e 10 (Sorte 6 + 3 de item = limite 1 = 100%). `unifiedAttackBetween(luckItemBonus)` (motor e `CampaignSession`) separa a parte de itens da Sorte efetiva; `CombatActions` calcula `luckItemBonus` com `equipmentBonus(..., sorte, perfil do N-13)` (qualidade e penalidade de classe valem). Testes novos em `UnifiedCombatTest` (+2), escritos e não executados.
- ASSUMINDO: só a Sorte vinda de equipamento conta como "de item" (efeitos de encantamento entram sozinhos pelo N-17, pelo mesmo `equipmentBonus`; buffs e traços de Sorte continuam na parte de atributo, com teto 6); Sorte de item negativa conta 0; a Sorte de item ainda soma ao valor efetivo do atributo.
- ☑ Fechamento (regra 3).

### 14.6 Fase 18 — Elementos e encantamentos

**N-16 Elementos e condições em habilidades**
- Fechar antes: modelo de fraquezas e resistências; chance de aplicar condição (fixa, por teste ou Sorte) (§8).
- Arquivos: `habilidades-arvore.txt` (colunas `element` e `effects`), `condicoes.txt`, `UnifiedCombat.kt`, `resistencia_dano`.
- Pronto quando: habilidades e armas têm elemento e chance de condição; resistências a elemento funcionam.
- ✅ **Decidido em 24/10/2026:** ver §15.1 (sete elementos, ciclo, Luz e Trevas, magia combinada, chance de condição).
- Entregue (24/10/2026, escrito e não compilado): `Elements.kt` (`ElementChart.percent/apply/parse`, `ConditionChance.chance`); `UnifiedAttackSpec.elementPercent` (aplicado antes da redução fixa) e `GameEngine/CampaignSession.applyConditionByChance` (d100 no log); `itens.txt` colunas 12–14 `elemento|condicao|chance` (só arma; `ItemDef.element/conditionId/conditionChance`; condição validada contra `condicoes.txt`); `habilidades-arvore.txt` aceita `ferro`, `luz`, `trevas`, `agua`; `CombatActions.strike` usa elemento da habilidade ou da arma contra a afinidade do alvo e aplica a condição da arma com chance; 2 armas novas (`espada-de-brasa`, `adaga-envenenada`); rótulos Ferro/Trevas/Luz. `ElementosN16Test` (6 testes).
- ASSUMINDO: `METAL` = ferro, `YANG` = Luz, `YIN` = Trevas (enum preservado); elemento do alvo = afinidade (`skillTreeElement`; NPC sem elemento até o N-17); Luz×Luz e Trevas×Trevas = normal; magia combinada só com dois elementos do ciclo e, se o alvo vence um deles, fica em 50% (a desvantagem prevalece); percentual aplicado antes da resistência fixa; condição só em golpe de arma que causa dano (habilidade com conditionId segue pelos efeitos); Sorte da chance = atributo cru, sem bônus extra até o N-17; nenhuma habilidade da árvore declara elemento ainda; custo da magia combinada e magia combinada na interface seguem pendentes.
- ☑ Fechamento (regra 3).

**N-17 Encantamento do ferreiro**
- Depende de: N-11, N-12, N-16. Fechar antes: chance, imunidade ou redução, número por peça, preços (§10).
- Arquivos: `itens.txt`, `ItemQuality.kt` (sufixo no id), novo catálogo de encantamentos.
- Pronto quando: só peito e arma; permanente e caro; disponível no ferreiro NPC das cidades. A habilidade do personagem Ferreiro de encantar vem na N-21.
- ✅ **Decidido em 24/10/2026:** ver §15.2 (um encantamento por peça, permanente, preço, resistências).
- Entregue (24/10/2026, escrito e não compilado): encantamento no id do item, `base#qualidade@elemento` (`ItemRef.enchantment/of`); `RulePackage.effectiveElement`, `enchantBlock` (só peito e arma, um por peça, sem elemento próprio, com preço) e `enchantPrice` (⌈5 × preço × qualidade⌉); `CHEST_SLOT`; `InventoryActions/AppModel.enchantItem` (ferreiro NPC: paga, troca a peça por uma encantada, só desequipada e fora de combate); `ElementChart.armorPercent` (peito de Luz/Trevas ignora o outro do par; os demais seguem o ciclo, ex.: peito de água zera fogo); arma encantada dá o elemento ao golpe; peito encantado define o elemento do defensor (tem prioridade sobre a afinidade) e reduz em 50% o dano por rodada das condições aplicadas (`applyConditionByChance(damageFactorPercent)`). `EncantamentoN17Test` (3) e +1 em `ElementosN16Test`, escritos e não executados.
- ASSUMINDO: encantamento fica no sufixo do id (inventário, log e comandos sem mudança); preço arredondado para cima; ferreiro NPC é a ação `enchantItem` (sem tela própria nem cidade); peça com elemento próprio (ex.: `espada-de-brasa`) não aceita encantamento; sem desencantar; os −50% valem para condições aplicadas por arma com dano por rodada declarado; imunidade total a condição não foi implementada.
- ☑ Fechamento (regra 3).

**N-18 Pergaminhos de encantamento**
- Depende de: N-16, N-17. Fechar antes: duração em dias exige contagem de tempo na campanha (§10).
- Arquivos: `itens.txt`, motor (relógio de dias), catálogo de materiais.
- Pronto quando: pergaminho de elemento temporário (≈ 1 dia), caro, com material e item mágico.
- ✅ **Decidido em 24/10/2026:** ver §15.2 (1 dia = até o próximo descanso completo; contador de dias da campanha).
- Entregue (24/10/2026, escrito e não compilado): `GameState.day` e `scrollEnchants` (dono → slot → `ScrollEnchant(itemId, element, expiresDay)`); comandos `AdvanceDays(1..365)` e `ApplyScrollEnchant` (só `mao`/`peito`, peça equipada, um por peça) e eventos `DaysAdvanced`/`ScrollEnchantApplied` (vencidos caem ao avançar o dia); o descanso local avança 1 dia; 7 itens `pergaminho-elemento-<elemento>` (150; Luz e Trevas 300) com `item_<id>_acao|encantar`, `_elemento`, `_dias|1` em `criacao.txt`; `useItem` aplica no peito, senão na arma, sem gastar se nada aceitar; `RulePackage.scrollBlock` (peça encantada ou com elemento próprio não aceita); o golpe usa o elemento do pergaminho (arma) e do peito quando não há encantamento do ferreiro. `DiasPergaminhoN18Test` (4) e `PergaminhoElementoN18Test` (2), escritos e não executados.
- ASSUMINDO: o dia só avança no descanso local e por `AdvanceDays` (sem comando da IA ainda; sem calendário nem horas); preços 150/300 são proposta; material e item mágico exigidos, a habilidade de escrever pergaminhos e a criação por guilda/mago ficam para a lista de materiais (N-21 em diante), então os pergaminhos só se compram; peito com pergaminho age como o do ferreiro, inclusive o −50% de condição; trocar a peça deixa o pergaminho preso à peça antiga até vencer; peito é escolhido antes da arma.
- ☑ Fechamento (regra 3).

### 14.7 Fase 19 — Habilidades de classe e profissão

**N-19 Estrutura das habilidades por degrau**
- Depende de: N-02. Fechar antes: o "nível" dos degraus (personagem × habilidade) e quem escolhe profissão na criação (§11).
- Arquivos: `habilidades-arvore.txt`, `SkillTreeCatalog.kt`, `CharacterDraft.kt`, tela de criação.
- Pronto quando: degrau 1 ativo e gratuito ao escolher a profissão; degraus nos níveis 1, 7, 13, 21 e 30; novo tipo de ação fora de combate; as 350 + 721 habilidades atuais continuam carregando.
- ✅ **Decidido em 24/10/2026:** o nível dos degraus é o do **personagem** (1, 7, 13, 21, 30); na criação o jogador escolhe **classe + profissão** e o degrau 1 de ambas vem ativo e grátis.
- Entregue (24/10/2026, escrito e não compilado): `SkillTreeProgression.TIER_LEVELS`, `unlockedTier`, `tierOf` e `FREE_BASE_ORIGINS` (agora inclui `CLASS` e `PROFESSION`; vale no motor, no carregador do catálogo e no app); `SkillActionKind.OUT_OF_COMBAT` (só fora de combate; gasta o custo e aplica efeitos como `MODIFIER`); `habilidades-arvore.txt`: as 50 classes e as 103 profissões tiveram o degrau 1 marcado como gratuito (`unlockCost` 0, `freeBase` true); `grantSelectedFreeBaseSkills` concede o degrau 1 de classe/profissão que já cumpre os requisitos e deixa para depois o que ainda não cumpre (ex.: afinidade dos conjuradores). As 350 + 721 habilidades continuam carregando. `DegrausN19Test` (2) e `SkillTreeCatalogTest` ajustado (um degrau gratuito por árvore), escritos e não executados.
- ASSUMINDO: as cadeias atuais mantêm seus níveis (profissões 1, 5, 10, 15, 20, 25, 30; classes 1–7), então só o degrau 1 e o nível 30 coincidem com os degraus novos; a troca para 1, 7, 13, 21 e 30 acontece quando cada profissão ganhar seus 5 degraus (N-21 em diante); nenhuma habilidade usa `OUT_OF_COMBAT` ainda (as mecânicas de ofício são o N-20); a escolha de classe + profissão na tela de criação já existia e não mudou.
- ☑ Fechamento (regra 3).

**N-20 Mecânicas comuns de ofício**
- Depende de: N-19, N-12. Fechar antes: receitas e materiais; falha no conserto; risco do furto (§11).
- Arquivos: `engine`, `rules`, `ai` (menus e propostas), `itens.txt` (materiais).
- Pronto quando: **consertar**, **criar** (receita do pacote + teste; a IA nunca inventa receita), **preparar consumível**, **afanar** (Destreza × Agilidade) e **identificar** funcionam como ações genéricas, parametrizadas por tipo de item.
- Entregue (24/10/2026, escrito e não compilado): `Crafting.kt` (`CraftKind`, `CraftChance`, `CraftResult`, `PILFER_CAUGHT_RELATION`); `GameEngine.attemptCraft` (criar, preparar, consertar e identificar são a mesma ação, parametrizada por entradas, saídas e chance) e `attemptPilfer`, com wrappers em `CampaignSession`; `receitas.txt` (`id|nome|tipo|entradas|saidas|nivel|dificuldade|manter_na_falha`) lido por `PackageParser` (`RecipeDef`, ids de item validados, duplicados e herança por id) e carregado pelo app; `RulePackage.recipe/recipes/craftChance/executeRecipe`. 4 receitas de exemplo (barra de ferro, couro curtido, poção de cura menor, poção de mana). `OficioN20Test` (engine 8, rules 5), escritos e não executados.
- ASSUMINDO: chance = 50% + 5% × Habilidade + 2% × Sorte + 5% × degrau de ofício acima do primeiro − dificuldade, entre 5% e 95% (100% quando a habilidade garante o sucesso); falha perde metade (arredondada para baixo) das entradas fora de `manter_na_falha` e nada é entregue; afanar: chance = acerto (50% + 5% × (Destreza − Agilidade), 5–95%) com valores efetivos fornecidos por quem chama, só item não equipado e fora de combate, falha = alvo flagrou (relação do alvo com o ladrão −10); sem durabilidade nem item quebrado/não identificado no catálogo, então não há receita de conserto ou identificação de exemplo; sem tela, proposta da IA nem uso de `OUT_OF_COMBAT` (as habilidades de profissão chamam `executeRecipe` a partir do N-21); a IA só pode escolher receita existente por id.
- ☑ Fechamento (regra 3).

**N-21 Onda 1a: Forja e armas, Vestuário e couro** (12 profissões)
- Depende de: N-17, N-20. Conteúdo: Ferreiro, Armeiro, Armeiro de Armaduras, Perito em Arcos, fabricantes de arco e flecha; Alfaiate (calça), Sapateiro (botas), Curtidor (luvas), Tecelão, Cordoeiro, Preparador de Peles.
- Arquivos: `habilidades-arvore.txt`, `personagem-catalogo.json`, receitas.
- Pronto quando: cada profissão tem seus 5 degraus; o nível do Ferreiro limita o nível do equipamento que cria e encanta.
- Entregue (24/10/2026, escrito e não compilado): 12 profissões com 5 degraus (1, 7, 13, 21, 30); `receitas.txt` com `habilidade` e `garantida_por` (28 receitas) e limite de nível por degrau validado no parser (1/5/10/20/30); `RulePackage.recipeBlock/craftTierBonus/availableRecipes/smithEnchantBlock/enchantMaterial`; app `craftRecipe`, `availableRecipes`, `smithEnchantItem` (degrau 5 do Ferreiro, 1 pedra do elemento). `OficioN21Test` (6); `SkillTreeCatalogTest` ajustado.
- ASSUMINDO: degraus 2–5 passivos que liberam receitas (degrau 2 consertar, 3 criar, 4 conserto sem falha, 5 peça rara/encantar); `OUT_OF_COMBAT` ainda sem uso; sem tela; pedra↔elemento (gelo=água, raio=ferro, sombra=trevas); peças quebradas são itens narrativos concedidos pelo mestre; resistência ao fogo do Ferreiro adiada; o Armeiro de Armaduras faz elmo e escudos, o Alfaiate calça, o Sapateiro botas, o Curtidor luvas, o Tecelão peito leve.
- ☑ Fechamento (regra 3).

**N-22 Onda 1b: Preparos, Cuidados, Furtividade** (16 profissões + classes de apoio)
- Depende de: N-20. Conteúdo: as 7 de Preparos, Curandeiro Comunitário, Enfermeiro, Cirurgião, as 6 de Furtividade e investigação; nas classes, afanar para Ladino (e talvez Assassino e Agente das Sombras) e cura para Curandeiro e Médico de Campo.
- Pronto quando: cada uma tem seus 5 degraus e ficam disponíveis na criação.
- Entregue (24/10/2026, escrito e não compilado): 16 profissões com 5 degraus; Cuidados com 4 degraus `OUT_OF_COMBAT` de cura (efeito `cura`, custo em PM, validado no carregador; `activate` cura herói ou aliado vivo fora de combate); 17 receitas de preparo por degrau e 10 itens; afanar (`canPilfer`, `pilfer`) para Ladino, Assassino, Agente das Sombras e Espião. `OficioN22Test` (5).
- ASSUMINDO: ver CHANGELOG; cura das classes Curandeiro e Médico de Campo feita no posto 6 (24/10/2026); ações de Furtividade feitas (`fur-*`).
- ☑ Fechamento (regra 3).

**N-23 Onda 2: Exploração, Comércio, Mecanismos** (23 profissões)
- Depende de: N-20. Conteúdo: 9 de Exploração e caça (reduzem o dano de `perigos.txt` e fornecem materiais), 10 de Comércio e diplomacia (preço e informação), 4 de Mecanismos.
- Pronto quando: cada uma tem seus 5 degraus.
- Entregue (24/10/2026, escrito e não compilado): 23 profissões com 5 degraus (1, 7, 13, 21, 30); `RulePackage.hazardReductionPercent` (Exploração, degraus 3/4/5 = 10/20/30% do dano de perigo, mínimo 1, aplicado em `CheckMenu.rollDamage`); `tradePercent`/`discountedPrice`/`boostedSale` (Comerciante e Contador, degraus 2–5 = 5/10/15/20%, aplicado em `InventoryActions.trade`); 7 receitas `mec-*` e 8 itens de Mecanismos. `OficioN23Test` (5); `SkillTreeCatalogTest` ajustado.
- ASSUMINDO: ver CHANGELOG; coleta de materiais feita (`col-*`); desarmar/reprogramar e rumores/contratos feitos por receita (`com-*`, `mec-*`, 24/10/2026).
- ☑ Fechamento (regra 3).

**N-24 Onda 3: Mar, Animais, Artes, Identificação** (21 profissões)
- Depende de: N-20. Conteúdo: 5 de Mar e rios, 4 de Animais e montarias, 5 de Artes e performance, 7 de Identificação e relíquias.
- Pronto quando: cada uma tem seus 5 degraus.
- Entregue (24/10/2026, escrito e não compilado): 21 profissões com 5 degraus; 7 receitas `ide-*` (identificar, falsificação, mapas, relíquias) e 7 itens. `OficioN24Test` (2); `SkillTreeCatalogTest` ajustado.
- ASSUMINDO: ver CHANGELOG; pesca, mergulho e apresentação feitas (`mar-*`, `art-*`); **inspirar feito** (degrau 3 de Artista, Músico, Ator e Dançarino, `ALLIES`, 24/10/2026, não compilado); **montaria e ave feitas** (degrau 3, 24/10/2026, não compiladas); **pendente:** companheiro e tatuagem.
- ☑ Fechamento (regra 3).

**N-25 Joias e Construção** (14 profissões) ⛔
- Bloqueado por: sistema de gemas e acampamento ou base na campanha (ainda não existem). Reavaliar depois do N-24.
- ☐ Fechamento (regra 3).

### 14.8 Fase 20 — Fechamento do sistema

**N-26 Itens de cura e escala**
- Fechar antes: manter fixo, multiplicar ou usar percentual (§12).
- Arquivos: `itens.txt`, `criacao.txt`.
- Pronto quando: poções, bandagens e pergaminhos fazem sentido com Resistência alta.
- ✅ **Decidido em 24/10/2026:** cura de PV em **percentual do PV máximo** (arredondado para cima), com mínimo igual ao valor fixo atual: bandagem 10%, poções 20/30/50/100%, pergaminhos 40/70%; PM e PA continuam fixos.
- Entregue (24/10/2026, escrito e não compilado): `item_<id>_percentual` em `criacao.txt` (1–100, validado) e `RulePackage.healAmount` (maior entre o fixo e o % do máximo, arredondado para cima; escala pela qualidade, limite 100) usado em `InventoryActions.useItem`; bandagem 10%, poções 20/30/50/100%, pergaminhos 40/70%. `CuraPercentualN26Test` (4).
- ASSUMINDO: ver CHANGELOG; pacotes importados sem `percentual` não mudam.
- ☑ Fechamento (regra 3).

**N-27 NPCs e monstros**
- Depende de: N-03, N-05. Fechar antes: categorias de ameaça com multiplicador de PV e o caminho sem pacote (§9).
- Arquivos: `NpcCreation.kt`, `Adaptation.kt`, `PLANO_RPG_SOLO.md` (escala por nível).
- Pronto quando: monstros seguem a mesma fórmula do jogador, com categoria de ameaça.
- ✅ **Decidido em 24/10/2026:** categorias capanga ×0,5, comum ×1, elite ×1,5, chefe ×2, chefe final ×3 sobre o PV (5 × Resistência). Sem pacote: o motor recusa recursos negativos e limita o PV máximo a 5 × (nível + 2) × multiplicador, recortando o excesso com aviso.
- Entregue (24/10/2026, escrito e não compilado): `ThreatCategory`, `NpcProposal.threat/level`, escala do PV em `NpcFactory.create`, teto sem pacote com aviso, `threat`/`level` em `request_npc`. `AmeacaN27Test` (4).
- ASSUMINDO: ver CHANGELOG.
- ☑ Fechamento (regra 3).

**N-28 Revisão geral e validação**
- Arquivos: testes que dependem dos valores antigos, `README.md`, `PLANO_RPG_SOLO.md`, `CHANGELOG.md`.
- Pronto quando: documentação consistente com o código; testes ajustados; estado de validação registrado com honestidade (regra 5).
- Entregue (24/10/2026): tentativa da suíte completa (falhou ao baixar o Gradle, sem rede); conferência estática de catálogo, receitas e `criacao.txt`; testes ajustados (N-24 e N-27); README, PLANO e CHANGELOG atualizados; validação registrada como **não executada**.
- ☑ Fechamento (regra 3).

---

## 15. Decisões fechadas em 24/10/2026 (rodada N-12 a N-27)

Decisões do proprietário, uma pergunta de cada vez. Nada abaixo foi implementado nesta rodada (só documentação); cada item entra no módulo indicado.

### 15.1 Elementos e condições (N-16)
- **Elementos:** água, fogo, ferro, vento, terra e os especiais **Luz** e **Trevas**. A coluna `element` de `habilidades-arvore.txt` está vazia hoje (`-`); o N-16 cria os dados.
- **Ciclo dos cinco:** água vence fogo, fogo vence ferro, ferro vence vento, vento vence terra, terra vence água. Atacar o elemento em que tem vantagem causa **+50%**.
- **Ataque puro (um só elemento):** contra o elemento que vence o seu (o oposto), **não causa dano**; contra o mesmo elemento, dano normal; contra o elemento em que tem vantagem, +50%. Os demais casos: dano normal.
- **Dano sem elemento:** sempre normal.
- **Luz e Trevas:** causam **+50%** em todos os outros elementos e também **recebem +50%** deles; entre si (Luz × Trevas) o dano é **dobrado** (+100%) nos dois sentidos.
- **Magia combinada** (dois elementos, ex.: fogo+vento): causa **+50%** a mais contra tudo, **exceto** contra o elemento que vence algum dos seus: ali não ganha o bônus e ainda perde 50% do dano. O bônus de combinação **soma** com o de vantagem (+50% + +50% = **+100%**, dano dobrado). Em troca, o **custo** da magia combinada é **alto** (valor a definir no N-16).
- **Chance de aplicar condição:** fixa, declarada em cada arma ou habilidade (d100) **+ 2% por ponto de Sorte**; itens, buffs e pergaminhos também podem somar. **Teto de 90%** pela soma de base e atributos, chegando a **100%** só com itens, buffs ou pergaminhos (mesmo padrão do crítico).

### 15.2 Encantamentos e pergaminhos (N-17, N-18)
- Só **peito e arma**; o personagem escolhe o elemento **uma vez**, e depois de encantado não muda. Pelo **ferreiro** é permanente. **Um encantamento por peça.**
- Peito encantado: **−50%** do dano de uma condição/debuff e **100%** de redução contra elemento (ex.: armadura de água ignora ataque de fogo, seguindo o ciclo). Peito de **Luz** ou **Trevas**: mesma lógica dos demais elementos; assumido que ignora o **outro** do par (Luz ignora Trevas e vice-versa), já que não vencem ninguém no ciclo (a confirmar no N-16).
- **Preço no ferreiro NPC:** 5 × preço do item × multiplicador de qualidade (normal 1, ótima 1,25, rara 1,5, épica 2, lendária 2,5), igual para arma e peito. O Ferreiro jogador paga só o material (serviço entra no N-21).
- **Pergaminho de elemento** em armadura comum: efeito de **1 dia**. Peça já encantada pelo ferreiro **não aceita** pergaminho.
- **Contagem de dias (N-18):** cada **descanso completo avança 1 dia**; o pergaminho vale até o próximo descanso completo; o mestre (IA) também pode avançar dias (ex.: viagem longa).

### 15.3 Equipamentos (N-12 a N-14)
- Bônus base por peça = ⌈nível ÷ 10⌉ (nível 1–10: +1; 11–20: +2; 21–30: +3…); a qualidade multiplica e arredonda para cima; no peito vira PV máximo (5 × o bônus). Um conjunto completo vale cerca de 50% dos pontos de status do nível.
- **Mestre das Armas / Mestre dos Equipamentos:** ver N-13 (§14.5). Vantagens são obtidas na criação, com a tabela de equilíbrio existente: `equilibrio|1,3,5` em `criacao.txt` (1 vantagem = 1 desvantagem, 2 = 3, 3 = 5) e `equilibrio_apos|dobrar` depois da tabela (4 → 10, 5 → 20…). ❓ O proprietário disse "e por aí vai" depois do 3 = 5: conferir se o ritmo desejado depois da tabela é dobrar (como está) ou outro.
- Duas mãos bloqueia o escudo; ataque ×1,5.

### 15.4 Progressão, itens e monstros (N-19, N-26, N-27)
- Degraus de habilidade pelo nível do personagem (1, 7, 13, 21, 30); criação com classe + profissão; degrau 1 ativo e grátis.
- Cura percentual (N-26) e categorias de ameaça (N-27): ver §14.8.

### 15.5 Pendência fechada de N-02/N-03
- Os 10 pontos da ficha e os 10 da distribuição inicial **são os mesmos**: a criação tem **10 pontos de status + 2 de habilidade** (já é o que `AllocateInitialBuild` exige).

### 15.6 Companheiro adestrado: ficha do animal (decidido em 24/10/2026, numerado N-32; só documentação)

Decisões do proprietário para fechar a pendência do companheiro (aberta no N-24 e ligada ao Adestrador do N-31, que hoje só deixa um item narrativo). Nada abaixo foi implementado nem compilado.

**Ficha própria do animal** (o jogador abre e edita a ficha do próprio animal):
- Nome, espécie, porte (vem do degrau do Adestrador: pequeno, médio, grande, fera, colossal), tipo de corpo, PV, nível, lealdade e truques aprendidos.
- **Status:** 3 pontos para distribuir, só em **Ataque, Resistência, Velocidade e Destreza**.
- **Habilidade de ataque:** cada tipo de corpo oferece 3 opções de ataque (tabela abaixo). O jogador escolhe **1** delas como o **único ataque** do animal, e cada **ponto de habilidade** (1 a cada 3 níveis do animal) sobe o nível dessa habilidade. O dano usa o status Ataque do animal.

**Regras dos ataques** (aprovado em 24/10/2026; valores 🔶 ajustáveis):
- **Dano** = Ataque do animal × fator da opção: leve ×0,8, normal ×1, alto ×1,3.
- **Chance de efeito (d100):** base da opção + 5% por nível da habilidade, teto de 90%; o animal não usa Sorte nisso.
- **Condições** existentes em `condicoes.txt`: sangramento (10% do PV por rodada, 3 rodadas), envenenado (5%, 3 rodadas), caído (até se levantar), atordoado (1 rodada) e paralisado (2 rodadas). "Empurra" virou atordoado e "prende" virou paralisado, para não criar condição nova.
- Ataque sem efeito rende dano normal; o que tem efeito troca um pouco de dano por controle.

| Tipo de corpo | Ataque | Dano | Efeito (chance base) |
|---|---|---|---|
| Quadrúpedes | Mordida | normal | nenhum |
| | Investida | alto | caído (30%) |
| | Garra | normal | sangramento (30%) |
| Alados | Bicada | normal | nenhum |
| | Mergulho | alto | nenhum |
| | Rajada de asas | leve | atordoado (25%) |
| Rastejantes | Mordida venenosa | normal | envenenado (40%) |
| | Constrição | leve | paralisado (30%) |
| | Golpe de cauda | normal | nenhum |
| Aquáticos | Mordida | normal | nenhum |
| | Jato d'água | normal | elemento água |
| | Golpe de cauda | leve | atordoado (25%) |
| Humanoides | Golpe com arma | normal | nenhum |
| | Soco e chute | normal | nenhum |
| | Arremesso | leve | à distância |
| Insetos e aracnídeos | Ferrão | normal | envenenado (40%) |
| | Teia | leve | paralisado (30%) |
| | Picada | normal | nenhum |


**Adestramento e truques (d20):**
- O **nível de adestramento** é próprio do animal (não é o nível da habilidade de ataque). Cada nível soma **+3 no d20 (+15%)** no sucesso do truque.
- Cada truque tem dificuldade no d20; 20 natural sempre dá sucesso e 1 natural sempre falha. Pouca dificuldade para o pet, de propósito.
- 🔶 Proposta para ajuste: simples (dificuldade 5, lealdade mínima 10), médio (8, 25), difícil (11, 40), mestre (14, 60). Truques sugeridos:

| Nível | Truque | Efeito |
|---|---|---|
| Simples | Sentar e ficar | Fica parado, não foge nem atrapalha |
| Simples | Vir | Volta ao dono na hora |
| Simples | Alertar | +2 em percepção ao dono na cena |
| Médio | Buscar | Traz um item pequeno da cena |
| Médio | Rastrear | +3 para seguir um rastro |
| Médio | Guardar | Vigia o acampamento ou o inventário durante o descanso |
| Difícil | Esconder-se | Fica fora de vista até ser chamado |
| Difícil | Carregar | Aumenta a carga do dono |
| Difícil | Atacar à ordem | Ataca o alvo que o dono escolher |
| Mestre | Proteger o dono | Toma o golpe no lugar do dono |
| Mestre | Distrair | O inimigo ataca o animal em vez do dono |
| Mestre | Resgatar | Tira o dono caído do perigo |
 Aprender: 1 tentativa por descanso, d20 + (lealdade ÷ 10, arredondado para baixo) contra a dificuldade; falhar não tira lealdade.
- **Chance de executar a ordem** = chance do d20 + (100% − chance do d20) × lealdade ÷ 100. Com **lealdade 100 a chance é 100%** (sem rolar).

**Lealdade (0 a 100):**
- **Ao adestrar, o sistema rola 1d20 automático e o resultado é a lealdade inicial.**
- Sobe com **comida** (🔶 comum +2, boa +5, rara +10; preferida da espécie dobra) e **carinho** (🔶 +3, uma vez por descanso). Também aumenta a chance de aprender truques.
- **Quanto maior o nível do animal, mais difícil conquistar lealdade** (por isso é recomendado pegar animal de nível baixo e fazê-lo subir). 🔶 Ganho de comida e carinho: cheio até o nível 5, ×0,75 nos níveis 6 a 10, ×0,5 nos 11 a 20, ×0,25 do 21 em diante (arredonda para cima, mínimo 1).
- **Subir de nível aumenta a lealdade** (🔶 +5 por nível).
- Sem perda de lealdade por negligência.

**Nível do Adestrador (habilidade da profissão, 1 a 5) facilita captura e lealdade** (decidido em 24/10/2026):
- **Lealdade:** o ganho de comida e carinho é multiplicado por **1 + 0,25 × (nível − 1)**: ×1 no nível 1 e ×2 no nível 5. Soma-se ao fator de nível do animal (o fator do animal é aplicado primeiro e o do Adestrador depois, arredondando para cima, mínimo 1).
- **Captura:** **+3 no d20 por nível** do Adestrador (+15%), igual ao bônus dos truques.
- **Exemplo:** personagem nível 35 com Adestrador 1 pegando pet nível 35 (fator ×0,25): comida boa (+5) rende +2. Com Adestrador 5 (×0,5 no total): rende +3, o dobro de velocidade, mas o pet alto continua mais difícil que um filhote. Por isso o recomendado segue sendo pegar animal de nível baixo e fazê-lo subir.
- ASSUMINDO: "nível do Adestrador" é o degrau da profissão (1 a 5, Pequenos Animais até Criaturas Colossais), o mesmo do N-31.

**Comidas por tipo de corpo** (aprovado em 24/10/2026; valores 🔶 ajustáveis). Ganho de lealdade antes dos multiplicadores: comum +2, boa +5, rara +10; a comida preferida da espécie dobra o ganho; carinho +3, uma vez por descanso. O animal recusa um alimento que odeia e não ganha lealdade com ele.

| Tipo de corpo | Comum | Boa (preferida do tipo) | Rara |
|---|---|---|---|
| Quadrúpedes | Grãos, Pão assado | Carne fresca | Refeição preparada de carne |
| Alados | Grãos | Peixe fresco, Carne fresca | Ovo ou fruta rara |
| Rastejantes | Carne fresca | Ovo, Peixe fresco | Presa viva ou rara |
| Aquáticos | Alga marinha | Peixe fresco | Pérola bruta ou peixe raro |
| Humanoides | Pão assado | Refeição preparada | Doce ou iguaria |
| Insetos e aracnídeos | Fibra vegetal | Mel ou fruta | Néctar ou cogumelo raro |

Já existem em `itens.txt`: `graos`, `pao-assado`, `carne-fresca`, `peixe-fresco`, `alga-marinha`, `fibra-vegetal` e `perola-bruta`. Refeição preparada, ovo, fruta, mel, iguaria, néctar e presa viva ainda não existem e entram como itens narrativos novos na implementação.

**Em combate (decidido em 24/10/2026):** o animal tem o **turno dele** e segue a **mesma lógica de um NPC companheiro (aliado)**. No código atual, aliado é um NPC vivo e de pé na iniciativa com relação ≥ `ALLY_RELATION_MIN` (10) com o herói (`GameState.allyIdsInCombat`); o motor não tem lados fixos e o mestre cria o aliado mudando a relação. Por isso o Inspirar (N-29) e outros efeitos em aliados já valem para o animal.
- ASSUMINDO: o animal vira uma entidade NPC com a ficha própria, com relação ao herói mantida em 10 ou mais enquanto adestrado; entra na iniciativa e age no turno dele; a ordem de um truque de combate (Atacar à ordem, Proteger o dono, Distrair) é dada sem gastar o turno do dono e é executada no turno do animal, pela regra de execução acima; sem ordem, o animal usa o ataque escolhido contra o alvo mais ameaçador ou o que o mestre indicar.

**Interface (aprovado em 24/10/2026):** painel recolhível **"Companheiro"** na aba Jogar, ao lado de Inventário, Poderes, Árvore de perícias e Ofícios; só aparece quando o herói tem um animal adestrado.
- **Topo:** nome, espécie, porte, tipo de corpo, nível, PV e barra de lealdade (0 a 100).
- **Status:** Ataque, Resistência, Velocidade e Destreza, com botão + enquanto houver ponto livre dos 3 iniciais.
- **Ataque:** habilidade escolhida, dano, efeito e chance; botão para subir o nível dela quando houver ponto de habilidade (1 a cada 3 níveis).
- **Adestramento:** nível atual e bônus de +3 por nível nos truques.
- **Truques:** aprendidos (botão Ordenar) e aprendíveis agora (botão Tentar aprender, 1 por descanso).
- **Cuidados:** Alimentar (escolhe a comida do inventário) e Carinho (1 por descanso).
- Os botões passam pela mesma trava de turno dos outros painéis (`act`), desligados durante a narração ou com teste pendente. Em combate, Ordenar só aparece para os truques de combate (Atacar à ordem, Proteger o dono, Distrair). A escolha do ataque (1 das 3 opções do tipo de corpo) é feita uma vez, numa tela logo depois do adestramento.

**Módulos de implementação:** N-32.1 motor (estado, lealdade, comida, carinho) ✅ em código, não compilado; N-32.2 pacote (tipos de corpo, 18 ataques, 12 truques, comidas) ✅ em código, não compilado (`companheiros.txt`); N-32.3 combate (ataque, truques, turno do animal, subir de nível com +5 de lealdade); ✅ em código, não compilado (`Companion.kt`, `companionStrike`); N-32.4 painel "Companheiro" na aba Jogar ✅ em código, não compilado (`CompanionActions.kt`, `CompanionPanel`).

**Decidido em 24/10/2026 (N-33):** o animal ganha XP **apenas por combate** (sem missão nem ganho manual); ordem das próximas pendências: N-33.1 XP por combate ✅ em código, não compilado; N-33.2 criadouro, manutenção e perda do animal (decidido em 24/10/2026, só documentação, abaixo); N-33.3 ligar Criador de Animais e Treinador ao companheiro (decidido em 24/10/2026, §15.8, só documentação); N-33.4 gemas como itens de engaste (§15.9) e N-33.5 acampamento e Construção (§15.10), ambos decididos em 24/10/2026, só documentação, destravando o N-25.

### 15.7 Criadouro, manutenção e perda do animal (decidido em 24/10/2026, numerado N-33.2; só documentação)

Decisões do proprietário, tomadas em perguntas uma a uma. Nada abaixo foi implementado nem compilado.

**XP do animal (fecha o N-33.1)**

- O animal ganha o mesmo XP que o herói ganharia, calculado pelo nível dele (como já está no código).
- Só ganha XP o animal vivo ao fim da luta; o animal derrotado, mesmo tendo participado, não ganha.
- Interface: aviso só quando o animal sobe de nível (hoje não há aviso nenhum).

**Quantos animais e quem acompanha o herói**

- Limite total: 1 animal por grau de Adestrador (graus 1 a 5, os mesmos do N-31).
- Só 1 animal anda com o herói. Os outros ficam guardados no **criadouro** (local alugado, pago em prata). O criadouro não se prende a uma cidade: se o animal está em outra cidade, há uma interação de trazê-lo (10 prata fixas, 1 dia para chegar).
- Trazer é uma troca: o animal trazido passa a acompanhar o herói e o que estava com ele vai para o criadouro. A troca só pode ser feita na cidade.
- O animal que anda com o herói não paga manutenção nem precisa de visita.

**Manutenção (só animais guardados)**

- Cobrança por semana de jogo, descontada automaticamente ao virar a semana.
- Custo por animal: 5 prata × nível do animal. O cuidador é um NPC contratado, com salário separado: 10 prata × quantidade de animais guardados. O aluguel do criadouro está embutido nesses custos (sem taxa própria).
- Prata insuficiente: paga primeiro o cuidador e depois os animais, do de menor nível (mais barato) para o de maior, até acabar a prata. Quem ficar sem pagamento leva -30 de lealdade na semana.
- Animal pago, mas sem visita na semana: -10 de lealdade.
- Visita: botão "Visitar" no card do Companheiro. Evita a queda da semana e conta como carinho, com ganho de 1d6 de lealdade (rolado pelo motor). A semana em que o animal é deixado no criadouro conta como visitada.

**Perda e readestramento**

- Lealdade em 0: o herói perde o animal; o mestre narra.
- O herói pode tentar adestrar o mesmo animal de novo, com -30 pontos percentuais na chance de sucesso. O animal readestrado mantém nível, ataque e truques.
- Se falhar, há só mais 1 tentativa, também com -30 pontos e consumindo outro item de adestrar. Falhando de novo, o animal é perdido para sempre.

**ASSUMINDO (a confirmar):** semana de jogo = 7 dias do relógio da campanha; o cuidador é um NPC da campanha e o salário só é cobrado quando há animal guardado; a primeira tentativa de readestrar também consome o item de adestrar; o ganho de 1d6 da visita respeita o teto de lealdade (100); quem não tem animal guardado não paga nada.

**Entregue (25/10/2026):** N-33.2a (motor), N-33.2b (trazer/trocar), N-33.2c (app, não compilado) e N-33.2d (readestramento: motor executado, app não compilado) em código; ver `CHANGELOG.md`.

**Módulos previstos:** N-33.2a motor (criadouro, semana, cobrança em ordem, queda de lealdade, perda); N-33.2b troca e entrega (1 dia, 10 prata); N-33.2c botão Visitar e aviso de nível no card do Companheiro; N-33.2d readestramento com penalidade; N-33.2e testes.

### 15.8 Criador de Animais e Treinador ligados ao companheiro (decidido em 24/10/2026, numerado N-33.3; só documentação)

Decisões do proprietário. Nada abaixo foi implementado nem compilado.

- **Criador de Animais:** o animal ganha lealdade sozinho a cada semana, **+1 por grau do Criador (1 a 5)**. Vale para **todos** os animais do herói (os guardados e o que anda com ele). Soma-se à queda por falta de pagamento ou visita da §15.7 (não a substitui); teto de lealdade 100.
- **Treinador:** dá pontos extras de status ao animal, **+1 ponto por grau do Treinador (1 a 5)**, só para o **animal que anda com o herói**. A distribuição entre Ataque, Resistência, Velocidade e Destreza **segue o tipo de corpo do animal**.
- **Entregue (25/10/2026):** motor executado; `ai`/`app` não compilados (ver `CHANGELOG.md`). Pesos por corpo em `Companions.TRAINER_ORDER`, não em `companheiros.txt`.
- **ASSUMINDO (a confirmar):** os pontos do Treinador somam-se aos 3 pontos iniciais e são recalculados ao trocar de animal (o ponto extra pertence ao herói, não ao animal); a "distribuição pelo tipo de corpo" usa pesos por corpo a definir em `companheiros.txt` (hoje o arquivo não traz pesos de status); o ganho semanal do Criador usa a mesma semana de jogo da §15.7.

### 15.9 Gemas como itens de engaste (decidido em 24/10/2026, numerado N-33.4; só documentação)

Decisões do proprietário para destravar o N-25 (Joias e Construção). As gemas viram **itens de encantamento simples**, separados do encantamento do ferreiro (§15.2) e do pergaminho. Nada abaixo foi implementado nem compilado.

- **Engaste:** a gema vai engastada em uma peça, mas pode ser **removida e trocada**. Vagas por qualidade da peça: comum 1, raro 2, lendário 3.
- **Tipos (4, um por status de combate):** Ataque, Defesa, Velocidade e PV. O efeito depende do tipo e da **qualidade da gema (1, 2 ou 3)**.
- **Valores:** Ataque, Defesa e Velocidade dão +2, +4 e +6; PV dá +10, +20 e +30 (qualidade 1, 2 e 3).
- **Qualidade da gema:** vem da raridade. O Lapidador pode tentar subir **1 nível** com teste de d20: falha em 1 a 5 (25%), igual para todos. Na falha, a gema **cai 1 nível**; a de qualidade 1 **some**.
- **Engastar e remover:** o Montador de Gemas (jogador) ou o ferreiro NPC. O ferreiro NPC cobra **50 prata × qualidade da gema** por engaste ou remoção.
- **ASSUMINDO (a confirmar):** a qualidade 3 é o teto (sem lapidar além); a remoção não destrói a gema nem a peça; não há limite de tentativas de lapidar; o Montador de Gemas jogador engasta sem custo em prata; os bônus das gemas entram pelo mesmo `equipmentBonus` do N-17 e somam com o encantamento; uma peça pode ter o mesmo tipo de gema mais de uma vez.
- **Origem das gemas (decidido):** todas as fontes: drop em combate e exploração, compra de NPCs nas cidades e extração por profissão (mineração/coleta).
- **Efeito no plano:** o N-25 deixa de estar bloqueado por falta de sistema de gemas; o acampamento está na §15.10.
- **Entregue (25/10/2026, escrito e não compilado):** motor, itens, vagas por qualidade e ações do app (engastar, remover, lapidar); falta tela, origem das gemas e receitas de Joias. Ver `CHANGELOG.md`.

### 15.10 Acampamento e Construção (decidido em 24/10/2026, numerado N-33.5; só documentação)

Decisões do proprietário para destravar a parte "Construção" do N-25. Nada abaixo foi implementado nem compilado.

- **O que é:** um local que o herói monta **em qualquer lugar** para descansar com bônus (não é base fixa).
- **Bônus:** depende do grau da profissão de Construção (1 a 5): **+10% por grau** de PV e PM recuperados no descanso (grau 1 = +10%, grau 5 = +50%).
- **ASSUMINDO (a confirmar):** o bônus é percentual sobre a recuperação normal do descanso, sem passar do máximo do recurso; vale só para o herói (não para animais nem NPCs); montar o acampamento não tem custo em prata, mas consome o tempo do descanso; o descanso no acampamento não substitui o criadouro da §15.7 (os animais guardados continuam fora dele); não há acampamento sem grau 1 de Construção.
- **Decidido depois (24/10/2026):** como o descanso já recuperava 100%, o descanso comum passa a recuperar **50%** de PV e PM, o acampamento soma +10% por grau de Construção e a estalagem recupera 100%. Em código (escrito e não compilado): `RestRules`, `LocalAction.INN_REST`.
- **Efeito no plano:** com §15.9 e esta seção, o N-25 deixa de estar bloqueado; falta numerar os módulos de implementação (Joias e Construção).

### 15.11 Confirmações dos valores em aberto (decidido em 24/10/2026, numerado N-34; só documentação)

Decisões do proprietário sobre itens marcados com 🔶/❓ nas seções anteriores. Nada abaixo foi implementado nem compilado; onde o código já usa o valor, a decisão só confirma.

- **XP de batalha (§2):** 8% do custo do nível por inimigo de nível igual (≈ 12 vitórias por nível), como está.
- **XP de missão (§2):** curta 25%, média 50% e grande 100% do custo do nível, como está.
- **Inimigo mais fraco (§2):** −20% por nível abaixo, 0 a partir de 5 níveis abaixo, mínimo 1 XP, como está.
- **Defesa rolada (§5):** tem **mínimo 1** (nunca negativa nem zero). Muda o comportamento de hoje, em que um valor abaixo de 3 vira 1d6 − N e pode dar negativo.
- **Fluxo de ataque (§5):** confirmado como proposto: (1) Destreza × Agilidade decide se acerta; (2) checagem de crítico; (3) dano = ataque − 0,65 × defesa. O confronto unificado de hoje não tem o teste de acerto, então isso é trabalho novo.
- **Sorte e crítico (§6):** dado **d10** (base 10%, +10% por ponto); o teto de 70% cai exatamente no limite 4.
- **Sorte de itens (§10):** passa do teto de 70%, +10% por ponto, até **100% com +3** (elmo comum +1, raro +2, lendário +3).
- **Qualidades de itens (§10):** continuam **normal (100%), boa (125%), rara (150%) e lendária (200%)**; não entra a qualidade "épica".
- **Categoria de ameaça (§7):** multiplicador de PV: capanga ×0,5; comum ×1; elite ×1,5; chefe ×2 a ×3, **a escolha do mestre** dentro da faixa.
- **ASSUMINDO (a confirmar):** o mestre (IA) propõe o multiplicador do chefe e o motor valida que fica entre ×2 e ×3; o mínimo 1 de defesa vale para o valor rolado, antes da fórmula do dano; o teste de acerto entra como módulo novo, sem mudar o cálculo de dano existente até lá.

### 15.12 Sorte, degraus de profissão e mecânicas fora de combate (decidido em 24/10/2026, numerado N-35; só documentação)

Decisões do proprietário sobre pendências ❓/🔶 das seções 6, 11 e 12. Nada abaixo foi implementado nem compilado.

**Sorte (§6)**

- Além do crítico, a Sorte de atributo dá **+1 em todos os testes a cada 2 pontos de Sorte** e **+1% de chance de drop por ponto de Sorte** (itens e gemas).
- A Sorte de itens (elmo e encantamentos) conta nos dois efeitos, como a de atributo.
- Cuidado com o nome: "sorte" também é o recurso de rerrolar gastando PA (`recurso_sorte|pa`); a interface deve distinguir os dois.

**Profissões (§11 e §12)**

- **Degraus de nível:** 1, 7, 13, 21 e 30, iguais para todas as profissões (substituem 1, 5, 10, 15, 20, 25 e 30 de hoje).
- **Custo:** o degrau 1 vem ativo na criação, sem custo; os seguintes custam 1 ponto de habilidade.
- **Habilidades:** cada profissão tem de 3 a 5 habilidades específicas; os degraus que sobrarem melhoram o que já existe (mais chance, efeito maior, material mais raro) em vez de inventar algo novo.
- **Classes:** ganham utilidades fora de combate, misturadas às de combate (ex.: Ladino abre fechaduras, Batedor rastreia).
- **Consertar:** falha gasta o material e o item continua quebrado.
- **Afanar:** se o ladrão for pego, o mestre (IA) narra a consequência.
- **Criar:** cada receita define o próprio tempo.
- **Conversão das 103 profissões:** por famílias e ondas (análise da §12), não profissão a profissão; as não convertidas mantêm a passiva genérica até a vez delas.
- **ASSUMINDO (a confirmar):** a Sorte de atributo continua sem teto no bônus de testes (o teto de 70% vale só para o crítico); o drop soma ao valor base de cada tabela de drop; as profissões do Ferreiro (1, 5, 10, 20, 30) e as já convertidas (N-20 a N-24, com 5 degraus) precisam ser realinhadas aos novos níveis na implementação; consertar sem material suficiente nem rola o teste; a falha de criar não tem regra própria (segue a da receita).

### 15.13 Peso das classes e Tatuador (decidido em 24/10/2026, numerado N-36; só documentação)

Decisões do proprietário sobre pendências 🔶/❓ da §9 (peso e classe) e da §12 (Artes e performance). Nada abaixo foi implementado nem compilado.

**Peso e classe (§9)**

- **Nomes dos conjuntos:** pesado (Baluarte, Couraça, Titã), médio (Patrulha, Malha, Andarilho), leve (Véu, Manto, Sombra).
- **Mapeamento das 50 classes:** confirmado como está na §9 (13 pesadas, 18 médias, 19 leves).
- **Distribuição dos bônus:** como sugerido: pesado com mais Defesa e PV; leve com mais Agilidade, Destreza, Sorte e Inteligência; médio equilibrado.

**Tatuador (§12)**

- **Limite:** 1 marca por grau do Tatuador (1 a 5).
- **Efeito:** bônus pequeno em um status de combate (Ataque, Defesa, Velocidade ou PV), **+1 por grau do Tatuador que fez** (PV +5 por grau).
- **Troca:** a marca pode ser **coberta por outra de grau maior**, que substitui a anterior.
- **Preço:** 100 prata × grau do Tatuador.
- **ASSUMINDO (a confirmar):** o "grau" do limite é o do herói que tem a profissão Tatuador, enquanto o bônus e o preço usam o grau de quem tatua (jogador ou NPC); cobrir exige o grau maior que o da marca coberta e paga o preço integral da nova; o status da marca é escolhido por quem a recebe na hora de tatuar; os bônus entram pelo mesmo `equipmentBonus` e somam com gemas e encantamentos; uma marca por status é permitida mais de uma vez.

**Em aberto:** valores marcados com 🔶.

**Entregue (24/10/2026, escrito e não compilado):** Tatuador em código (`Tattoo.kt`, `ApplyTattoo`, `TattooApplied`, `InventoryActions.tattooHero`, bônus em `CombatActions`) e `ItemWeight.setNames`; sem tela e sem peças dos conjuntos nomeados (falta decidir como os três nomes de cada peso se distribuem: faixas de nível ou famílias de peças). Testes `TatuadorN36Test` e `ConjuntosPesoN36Test`.
