# RPG-solo: refinamentos de atributos, PV e combate

Data: 23/10/2026. Origem: conversa de refinamento sobre o cálculo de PV/PM de personagens, NPCs e monstros.
Este documento reúne o que foi decidido e o que falta definir. **Nada disso entrou no código ainda** (o N-01 só alterou documentação). O plano de implementação (fases, módulos e regras de execução) está na seção 14.

Legenda: ✅ decidido pelo proprietário · 🔶 proposta ou em teste (valor sugerido) · ❓ em aberto

---

## 1. Estado atual do repositório (referência)

- **Atributos** (`atributos.txt`): Poder, Habilidade, Resistência; faixa 0–5; padrão 0.
- **Recursos** (`recursos.txt`, fórmulas aprovadas em 22/10/2026): PA = 1 + Poder · PM = 2 + 2 × Habilidade · PV = 4 + 3 × Resistência.
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
- ✅ **Sem teto:** "o limite é o céu". Remover o máximo 5 de `atributos.txt` e não limitar na criação.
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
- ❓ `tracos.txt` tem muitas vantagens com requisito `poder>=N` / `habilidade>=N`; precisam de revisão.
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
- ✅ O recurso se chama `pv` em `recursos.txt`, mas o motor usa `hp` como padrão em `damageResource`: a ligação entra no plano (N-03) e deve ser resolvida lá, sem divergência entre pacote e motor.

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
- ❓ Modelo de elementos (fraquezas e resistências).
- ❓ Chance de aplicar a condição: fixa, por teste ou influenciada pela Sorte?

---

## 9. NPCs e monstros

- O motor já é unificado: jogador, NPC, aliado e chefe usam as mesmas fontes e o mesmo cálculo. Monstro = atributos + modificadores, sem fórmula própria. Com PV = 5 × Resistência (mínimo 1), um monstro sem Resistência tem 1 PV.
- 🔶 Proposta sem decisão: categoria de ameaça com multiplicador de PV (capanga ×0,5; comum ×1; elite ×1,5; chefe ×2 a ×3). PM e PA só para quem usa magia ou ação. Escala por nível (prevista em `PLANO_RPG_SOLO.md`, não implementada).
- ❓ Fechar o caminho **sem pacote**, em que a IA informa os recursos direto e o motor só confere se não são negativos.

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
- ❓ Arma de duas mãos bloqueia o escudo? Se sim, precisa compensar com mais ataque. O formato atual tem `mao` e `outra-mao`, mas não vi como marcar arma de duas mãos.
- ✅ **Arma não dá defesa:** só ataque (ou Inteligência).
- 🔶 A qualidade do item (normal 100%, boa 125%, rara 150%, lendária 200%) escala os bônus positivos; isso já existe no motor.
- 🔶 **Sorte do elmo e teto de crítico:** a Sorte de atributo para de contar no 6 (70%); a Sorte do elmo e dos encantamentos passa do teto, +10% por ponto, até 100% com +3 (elmo comum +1, raro +2, lendário +3). A confirmar.

**Hoje no motor:** os slots são `mao`, `outra-mao`, `corpo` e `cabeca`, e os efeitos de equipamento são só `ataque`, `dano` e `defesa`. Não se troca equipamento em combate. Faltam os slots luvas, botas e calça e os efeitos de bônus de atributo (Sorte, Destreza, Agilidade, Inteligência) e de PV máximo (`max_pv` já existe para traços).

### Nível mínimo e qualidade

- ✅ Todo equipamento tem **nível mínimo** para ser usado: 1, 5, 10, 20, 30 e assim por diante (sem teto, como a progressão).
- ✅ Qualidades: **normal, ótimo, raro e épico**; **épico só a partir do nível 10**.
- ❓ Esses nomes substituem os atuais (normal 100%, boa 125%, rara 150%, lendária 200%) ou a lendária continua acima do épico? Definir o multiplicador de cada qualidade.
- ❓ Bônus base por faixa de nível. Princípio sugerido: o conjunto de equipamento vale uma fração dos pontos que o personagem tem naquele nível, para ajudar sem dominar.
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
- 🔶 Penalidade sugerida: os bônus da peça valem pela metade. Escala sozinha com o nível, sem regra nova por faixa.
- ✅ Quem sobrepõe a restrição tem uma **vantagem** (não uma profissão) chamada **Mestre das Armas** (usa qualquer arma); possível **Mestre dos Equipamentos** (qualquer peso de armadura). **Alfaiate**, **Armeiro** e **Armeiro de Armaduras** já existem como profissões.
- ✅ As armas também têm tipos ligados às classes (espada, arco, cajado etc.), com a mesma penalidade.
- ❓ As vantagens vivem em `tracos.txt`, com requisitos de atributo. Falta o efeito de vantagem que anula a penalidade de peso/tipo e definir o requisito para obtê-la (nível, atributo ou só depois da criação).
- 🔶 Nomes: mantidos os sugeridos acima (leitura a confirmar).

### Encantamento (ferreiro)

- ✅ Só **peito e arma** podem ser encantados.
- ✅ Feito pelo **ferreiro das cidades** (NPC) ou por personagem com a **profissão Ferreiro**.
- ✅ **Arma:** elemento ou debuff (sangramento, queimando, envenenado etc.).
- ✅ **Peito:** resistência a elemento, a condição (exemplo: sangramento) ou a atributo.
- ✅ **Caro e permanente:** uma vez encantado, não pode ser desencantado.
- ❓ Chance de a arma aplicar a condição: fixa, por Sorte ou só no crítico.
- ❓ Resistência a condição: imunidade ou redução do percentual de dano? (`resistencia_dano` e `imune_<condição>` existem para traços, não para equipamento.)
- ❓ Um encantamento por peça ou vários; preço por tipo e qualidade (hoje o `preco` fica em `itens.txt`, em prata/ouro).
- ❓ **Ferreiro já existe como profissão** no catálogo (103 profissões, incluindo Armeiro, Armeiro de Armaduras e Alfaiate), mas hoje só dá +1 em Ofícios; falta o efeito de encantar. O catálogo tem 50 classes; `tracos.txt` lista apenas 10.
- 🔶 Ideia de implementação: o id do item já aceita sufixo de qualidade (`base#qualidade`); o encantamento pode virar outro sufixo, o que torna a mudança permanente por natureza.

### Pergaminhos de encantamento

- ✅ Criados pelas **guildas de magos** ou por **mago com a habilidade de escrever pergaminhos**.
- ✅ Pergaminhos de elemento; exigem **material** e **itens mágicos**; custo alto; o efeito dura pouco (exemplo: **1 dia**).
- 🔶 Leitura: é o encantamento temporário, alternativa reversível à do ferreiro. A confirmar.
- ❓ Duração em dias exige que o motor conte o tempo da campanha. Hoje as durações são em rodadas, e não verifiquei se existe contagem de dias.
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
| Animais e montarias (4) | Criador de Animais, Tratador de Montarias, Tratador de Aves Mensageiras, Treinador | Acalmar animais → cuidar da montaria → montaria ou ave com bônus → adestrar aliado → companheiro |
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

- ❓ **O que é o "nível" dos degraus:** o nível do personagem (7, 13, 21) ou o nível da própria habilidade (1, 2, 3…)? Na proposta acima o degrau é a ordem na cadeia e o nível do personagem é só o requisito.
- ❓ Faltam mecânicas novas no motor (hoje só há resistência, dano, defesa e precisão): **consertar** (teste e chance, o que acontece na falha), **criar** (receitas, materiais, tempo), **afanar** (teste, risco de ser pego, o que se pode furtar, consequência com NPCs), **poções** (criação a partir de ingredientes).
- ❓ **Receitas e materiais:** lista de ingredientes e de materiais de forja (o mesmo material das criações de pergaminho da seção 10).
- ❓ Habilidades de profissão sem ação de combate precisam de um tipo novo de ação fora de combate (`actionKind`), já que hoje elas são só `MODIFIER`.
- ❓ Converter por **famílias e ondas** (ver análise acima) em vez de profissão a profissão; as não convertidas mantêm a passiva genérica até a sua vez, sem bloquear o resto. Confirmar o agrupamento e a ordem.
- ❓ Quem escolhe profissão na criação: só uma, ou classe + profissão (como hoje), e se o degrau 1 de ambas vem ativo.

## 12. Itens de cura e escala

- ❓ Poções e pergaminhos (cura 3/5/10/20 PV, bandagem 2, pergaminhos 6/12) foram calibrados para PV entre 4 e 19. Com Resistência 3 o PV novo (15) é parecido com o antigo (13), então o problema aparece com Resistência alta: manter fixo, multiplicar ou passar para percentual?
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
| N-02 | Sete atributos, sem teto | 15 | ⬜ | |
| N-03 | Recursos: PV, PM e PA | 15 | ⬜ | |
| N-04 | Progressão sem teto | 15 | ⬜ | |
| N-05 | Dano e defesa | 16 | ⬜ | |
| N-06 | Acerto, esquiva e iniciativa | 16 | ⬜ | |
| N-07 | Crítico e Sorte | 16 | ⬜ | |
| N-08 | Condições percentuais | 16 | ⬜ | |
| N-09 | Combate único | 16 | ⬜ | |
| N-10 | Traços e requisitos | 16 | ⬜ | |
| N-11 | Slots e efeitos de equipamento | 17 | ⬜ | |
| N-12 | Nível mínimo e qualidades | 17 | ⬜ | |
| N-13 | Peso, classe e penalidade | 17 | ⬜ | |
| N-14 | Escudo e arma de duas mãos | 17 | ⬜ | |
| N-15 | Sorte de equipamento e teto de crítico | 17 | ⬜ | |
| N-16 | Elementos e condições em habilidades | 18 | ⬜ | |
| N-17 | Encantamento do ferreiro | 18 | ⬜ | |
| N-18 | Pergaminhos de encantamento | 18 | ⬜ | |
| N-19 | Estrutura das habilidades por degrau | 19 | ⬜ | |
| N-20 | Mecânicas comuns de ofício | 19 | ⬜ | |
| N-21 | Onda 1a: Forja e armas, Vestuário e couro (12) | 19 | ⬜ | |
| N-22 | Onda 1b: Preparos, Cuidados, Furtividade (16) + classes de apoio | 19 | ⬜ | |
| N-23 | Onda 2: Exploração, Comércio, Mecanismos (23) | 19 | ⬜ | |
| N-24 | Onda 3: Mar, Animais, Artes, Identificação (21) | 19 | ⬜ | |
| N-25 | Joias e Construção (14) | 19 | ⛔ | |
| N-26 | Itens de cura e escala | 20 | ⬜ | |
| N-27 | NPCs e monstros | 20 | ⬜ | |
| N-28 | Revisão geral e validação | 20 | ⬜ | |

Cada módulo abaixo termina com o **fechamento da regra 3**; ele não é opcional.

### 14.3 Fase 15 — Fundação: atributos, recursos e progressão

**N-01 Decisões da fundação** (só documentação) ✅
- Decidido: Habilidade = PA e bônus em testes de ofício/perícia, iniciativa na Agilidade (§2); 10 pontos de status + 2 de habilidade/profissão (§2); curva de XP 5 × N sem teto e diretriz de balanceamento de XP de missão e batalha (§2, §2.1); ligação `pv` × `hp` no N-03 (§3); testes novos autorizados (regra 6).
- Entregue: seções 2, 2.1 e 3 atualizadas; cópia deste documento em `docs/REFINAMENTOS_ATRIBUTOS_COMBATE.md`; `CHANGELOG.md` e `PLANO_RPG_SOLO.md` (§3.10) atualizados; `RPG-solo-N-01.zip`.
- Ficou para os módulos: fórmula de PM com Inteligência e de PA com Habilidade (N-03); percentuais de XP de batalha e missão (N-04, refinado no N-27); papel da Habilidade nos requisitos de traços (N-10).
- ☑ Fechamento (regra 3).

**N-02 Sete atributos, sem teto**
- Arquivos: `atributos.txt`, `criacao.txt`, `PackageParser.kt`/`RulePackage.kt` (validação de máximo), `CharacterDraft.kt`, tela de criação do `app`.
- Pronto quando: Poder, Habilidade, Resistência, Inteligência, Agilidade, Destreza e Sorte existem; sem máximo; 10 pontos na criação e +1 por nível; fichas antigas abrem com os atributos novos em 0.
- ☐ Fechamento (regra 3).

**N-03 Recursos: PV, PM e PA**
- Depende de: N-02. Arquivos: `recursos.txt`, `RulePackage.kt` (`resourcesFor`), `Adaptation.kt`, `NpcCreation.kt`, `GameEngine.kt` (recurso de dano padrão).
- Pronto quando: PV = 5 × Resistência com mínimo 1 (Resistência 0 = 1 PV, 3 = 15); PM com Inteligência (fórmula a fechar aqui); PA vindo da Habilidade e não mais do Poder (fórmula a fechar aqui); jogador e NPC usam a mesma conta; `pv` e `hp` ligados sem divergência. O formato de `recursos.txt` pode precisar de "mínimo" e "base 0" (documentar em `FORMATO_PACOTE.md`).
- ☐ Fechamento (regra 3).

**N-04 Progressão sem teto**
- Depende de: N-01 (curva decidida). Arquivos: `progressao.txt`, `criacao.txt` (`combate_xp_vitoria`), `Commands.kt` (`maxQuestXp`), `CombatActions.awardCombatVictoryXp`, `GameEngine.awardQuestXp`, tela de nível.
- Pronto quando: existe XP para qualquer nível (5 × N por nível, sem teto; níveis 2 a 6 inalterados); cada nível dá +1 de status e +1 de habilidade; `maxQuestXp` vira fórmula; o XP de vitória em batalha escala com o nível (hoje `combate_xp_vitoria|5` fixo) e o XP de missão respeita o teto do nível (§2.1); testes novos de curva, teto de missão e XP de batalha por nível.
- ☐ Fechamento (regra 3).

### 14.4 Fase 16 — Combate

**N-05 Dano e defesa**
- Depende de: N-03. Arquivos: `UnifiedCombat.kt`, `EffectiveValue.kt`, `dano.txt`, `CombatBonuses` (`app`).
- Pronto quando: defesa base = Resistência; dano = ataque − 0,65 × defesa, arredondado para cima, mínimo 0; defesa rolada nunca negativa; confere com a simulação da seção 4.
- ☐ Fechamento (regra 3).

**N-06 Acerto, esquiva e iniciativa**
- Depende de: N-05. Fechar antes: confirmar o fluxo (acerto → crítico → dano) da seção 5.
- Arquivos: `UnifiedCombat.kt`, `Combat.kt`, `CombatActions` (`app`).
- Pronto quando: Destreza do atacante × Agilidade do defensor decide o acerto, automático e igual para jogador, NPC e monstro; a iniciativa passa a usar a Agilidade (§2).
- ☐ Fechamento (regra 3).

**N-07 Crítico e Sorte**
- Depende de: N-06. Fechar antes: d10 confirmado e o efeito da Sorte em testes (§6), sem confundir com `recurso_sorte|pa`.
- Arquivos: `UnifiedCombat.kt`, `EffectiveValue.kt`, `testes.txt` (se a Sorte entrar em testes).
- Pronto quando: crítico com limite = lados − bônus (mínimo 1); no crítico o ataque vale o valor cheio; 70% é o teto pelos atributos (Sorte 6).
- ☐ Fechamento (regra 3).

**N-08 Condições percentuais**
- Depende de: N-03. Fechar antes: confirmar 10% / 5% / 15% (§7).
- Arquivos: `condicoes.txt`, `Commands.kt` (≈ linha 979), `Events.kt`, `CheckMenu.kt`, parser de condições.
- Pronto quando: dano por rodada em % do PV máximo, arredondado para cima, mínimo 1; `sangramento`, `envenenado` e `queimando` migrados; textos da interface corrigidos.
- ☐ Fechamento (regra 3).

**N-09 Combate único**
- Depende de: N-05 a N-07. Fechar antes: o que fazer com `GameEngine.attack` (d20 + Poder contra 10 + Habilidade) (§13).
- Arquivos: `GameEngine.kt`, `CombatActions` (`app`), testes que usam o combate antigo.
- Pronto quando: só um sistema de ataque em uso, sem regra duplicada.
- ☐ Fechamento (regra 3).

**N-10 Traços e requisitos**
- Depende de: N-02. Arquivos: `tracos.txt` (requisitos `poder>=N` e `habilidade>=N`), `criacao.txt`. Considerar o novo papel da Habilidade (§2).
- Pronto quando: requisitos e vantagens coerentes com os sete atributos, sem traço inalcançável.
- ☐ Fechamento (regra 3).

### 14.5 Fase 17 — Equipamentos

**N-11 Slots e efeitos de equipamento**
- Depende de: N-02, N-05. Arquivos: `itens.txt`, `FORMATO_PACOTE.md`, parser de itens, `InventoryActions`.
- Pronto quando: seis peças (elmo → Sorte, luvas → Destreza, botas → Agilidade, calça → defesa, peito → PV máximo, arma → ataque ou Inteligência); efeitos de atributo e de PV máximo aceitos; itens atuais (`corpo`, `cabeca`) redistribuídos.
- ☐ Fechamento (regra 3).

**N-12 Nível mínimo e qualidades**
- Depende de: N-11. Fechar antes: multiplicadores de normal, ótimo, raro e épico e o destino de "lendária" (§10); bônus base por faixa de nível.
- Arquivos: `itens.txt`, `ItemQuality.kt`, parser.
- Pronto quando: todo item tem nível mínimo (1, 5, 10, 20, 30…); épico só do nível 10 em diante.
- ☐ Fechamento (regra 3).

**N-13 Peso, classe e penalidade**
- Depende de: N-12. Fechar antes: efeito da vantagem que anula a penalidade e seu requisito (§10).
- Arquivos: `personagem-catalogo.json` (peso por classe: 13 pesadas, 18 médias, 19 leves), `itens.txt`, `tracos.txt`.
- Pronto quando: peça de peso errado vale metade; armas têm tipo ligado à classe; **Mestre das Armas** e **Mestre dos Equipamentos** anulam a penalidade.
- ☐ Fechamento (regra 3).

**N-14 Escudo e arma de duas mãos**
- Depende de: N-11. Fechar antes: duas mãos bloqueia o escudo? (§10).
- Arquivos: `itens.txt`, parser, `InventoryActions`.
- Pronto quando: escudo só com arma de uma mão, dá Resistência (bônus menor que de uma peça comum); arma de duas mãos marcada no formato.
- ☐ Fechamento (regra 3).

**N-15 Sorte de equipamento e teto de crítico**
- Depende de: N-07, N-11. Arquivos: `UnifiedCombat.kt`, `EffectiveValue.kt`, `itens.txt`.
- Pronto quando: Sorte de elmo e encantamentos passa do teto de 70%, +10% por ponto, até 100%.
- ☐ Fechamento (regra 3).

### 14.6 Fase 18 — Elementos e encantamentos

**N-16 Elementos e condições em habilidades**
- Fechar antes: modelo de fraquezas e resistências; chance de aplicar condição (fixa, por teste ou Sorte) (§8).
- Arquivos: `habilidades-arvore.txt` (colunas `element` e `effects`), `condicoes.txt`, `UnifiedCombat.kt`, `resistencia_dano`.
- Pronto quando: habilidades e armas têm elemento e chance de condição; resistências a elemento funcionam.
- ☐ Fechamento (regra 3).

**N-17 Encantamento do ferreiro**
- Depende de: N-11, N-12, N-16. Fechar antes: chance, imunidade ou redução, número por peça, preços (§10).
- Arquivos: `itens.txt`, `ItemQuality.kt` (sufixo no id), novo catálogo de encantamentos.
- Pronto quando: só peito e arma; permanente e caro; disponível no ferreiro NPC das cidades. A habilidade do personagem Ferreiro de encantar vem na N-21.
- ☐ Fechamento (regra 3).

**N-18 Pergaminhos de encantamento**
- Depende de: N-16, N-17. Fechar antes: duração em dias exige contagem de tempo na campanha (§10).
- Arquivos: `itens.txt`, motor (relógio de dias), catálogo de materiais.
- Pronto quando: pergaminho de elemento temporário (≈ 1 dia), caro, com material e item mágico.
- ☐ Fechamento (regra 3).

### 14.7 Fase 19 — Habilidades de classe e profissão

**N-19 Estrutura das habilidades por degrau**
- Depende de: N-02. Fechar antes: o "nível" dos degraus (personagem × habilidade) e quem escolhe profissão na criação (§11).
- Arquivos: `habilidades-arvore.txt`, `SkillTreeCatalog.kt`, `CharacterDraft.kt`, tela de criação.
- Pronto quando: degrau 1 ativo e gratuito ao escolher a profissão; degraus nos níveis 1, 7, 13, 21 e 30; novo tipo de ação fora de combate; as 350 + 721 habilidades atuais continuam carregando.
- ☐ Fechamento (regra 3).

**N-20 Mecânicas comuns de ofício**
- Depende de: N-19, N-12. Fechar antes: receitas e materiais; falha no conserto; risco do furto (§11).
- Arquivos: `engine`, `rules`, `ai` (menus e propostas), `itens.txt` (materiais).
- Pronto quando: **consertar**, **criar** (receita do pacote + teste; a IA nunca inventa receita), **preparar consumível**, **afanar** (Destreza × Agilidade) e **identificar** funcionam como ações genéricas, parametrizadas por tipo de item.
- ☐ Fechamento (regra 3).

**N-21 Onda 1a: Forja e armas, Vestuário e couro** (12 profissões)
- Depende de: N-17, N-20. Conteúdo: Ferreiro, Armeiro, Armeiro de Armaduras, Perito em Arcos, fabricantes de arco e flecha; Alfaiate (calça), Sapateiro (botas), Curtidor (luvas), Tecelão, Cordoeiro, Preparador de Peles.
- Arquivos: `habilidades-arvore.txt`, `personagem-catalogo.json`, receitas.
- Pronto quando: cada profissão tem seus 5 degraus; o nível do Ferreiro limita o nível do equipamento que cria e encanta.
- ☐ Fechamento (regra 3).

**N-22 Onda 1b: Preparos, Cuidados, Furtividade** (16 profissões + classes de apoio)
- Depende de: N-20. Conteúdo: as 7 de Preparos, Curandeiro Comunitário, Enfermeiro, Cirurgião, as 6 de Furtividade e investigação; nas classes, afanar para Ladino (e talvez Assassino e Agente das Sombras) e cura para Curandeiro e Médico de Campo.
- Pronto quando: cada uma tem seus 5 degraus e ficam disponíveis na criação.
- ☐ Fechamento (regra 3).

**N-23 Onda 2: Exploração, Comércio, Mecanismos** (23 profissões)
- Depende de: N-20. Conteúdo: 9 de Exploração e caça (reduzem o dano de `perigos.txt` e fornecem materiais), 10 de Comércio e diplomacia (preço e informação), 4 de Mecanismos.
- Pronto quando: cada uma tem seus 5 degraus.
- ☐ Fechamento (regra 3).

**N-24 Onda 3: Mar, Animais, Artes, Identificação** (21 profissões)
- Depende de: N-20. Conteúdo: 5 de Mar e rios, 4 de Animais e montarias, 5 de Artes e performance, 7 de Identificação e relíquias.
- Pronto quando: cada uma tem seus 5 degraus.
- ☐ Fechamento (regra 3).

**N-25 Joias e Construção** (14 profissões) ⛔
- Bloqueado por: sistema de gemas e acampamento ou base na campanha (ainda não existem). Reavaliar depois do N-24.
- ☐ Fechamento (regra 3).

### 14.8 Fase 20 — Fechamento do sistema

**N-26 Itens de cura e escala**
- Fechar antes: manter fixo, multiplicar ou usar percentual (§12).
- Arquivos: `itens.txt`, `criacao.txt`.
- Pronto quando: poções, bandagens e pergaminhos fazem sentido com Resistência alta.
- ☐ Fechamento (regra 3).

**N-27 NPCs e monstros**
- Depende de: N-03, N-05. Fechar antes: categorias de ameaça com multiplicador de PV e o caminho sem pacote (§9).
- Arquivos: `NpcCreation.kt`, `Adaptation.kt`, `PLANO_RPG_SOLO.md` (escala por nível).
- Pronto quando: monstros seguem a mesma fórmula do jogador, com categoria de ameaça.
- ☐ Fechamento (regra 3).

**N-28 Revisão geral e validação**
- Arquivos: testes que dependem dos valores antigos, `README.md`, `PLANO_RPG_SOLO.md`, `CHANGELOG.md`.
- Pronto quando: documentação consistente com o código; testes ajustados; estado de validação registrado com honestidade (regra 5).
- ☐ Fechamento (regra 3).
