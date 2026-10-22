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
