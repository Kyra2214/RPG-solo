# 13 — Mistérios com limites (M4.5)

**Fonte:** doc 01 §3, §5, §9, §13, §14, §15; doc 03 §8; doc 04 §10; `12-conhecimento.csv`, `12-relogios.csv`. Dados em `13-misterios-limites.csv` (prefixo `mist.`).

## Regras

- **Sem resposta única:** cada mistério mantém **duas ou mais hipóteses abertas** compatíveis com os fatos confirmados. Nenhuma hipótese é eleita aqui.
- **Limite da verdade:** a coluna *a verdade não pode ser* lista o que contradiria um fato confirmado, uma regra de consistência (doc 01 §15) ou uma lacuna que o cânone deixa explícita. Não é uma resposta: é a fronteira dentro da qual qualquer resposta futura precisa caber.
- **Rumor não é fato:** rumores (comida quente, marcas de ferramenta, símbolo declarado inexistente) aparecem só como pista (doc 01 §15 regra 3).
- **Resolver é opcional:** um mistério pode ser resolvido por NPC fora de cena, agravar-se ou permanecer aberto (doc 01 §13). Resolver exige **evidência** do tipo listado e registro; a IA não escolhe a resposta.
- Conflitos F e G (tributo e corredores) têm saída política conhecida e não são mistérios; ficam de fora.
- Itens marcados *não pode ser* que vêm da interpretação dos textos, e não de uma frase explícita, são leitura editorial (ASSUMINDO).
- As sementes do M4.3 e as cadeias do M4.2 que tocam um mistério herdam estes limites.

## Mistérios

### `mist.a-arvores-coracao` — Mistério A — As duas árvores-coração

- **Fatos confirmados:** duas árvores-coração mostram perda de brilho nas folhas internas e raízes que evitam certas áreas; os sintomas são semelhantes aos da Vigília Longa; não há prova de causa comum; Aerendyl sabe das árvores doentes e não conhece a causa completa; Arieth omitiu uma área de doença para evitar interdição total.
- **Hipóteses abertas:** doença; contaminação; alteração do lençol freático; sabotagem.
- **A verdade não pode ser:** causa já conhecida por algum NPC sem fonte; causa provada só pela semelhança com a Vigília Longa; culpa de um povo inteiro; avanço do relógio só pela inação (sem corte nem ocultação novos ele não avança).
- **Evidência para resolver:** amostras de solo, água, raiz e folha; relatórios abertos ao arquivo; comparação entre as duas árvores e com os registros da Vigília Longa; registro de cortes e de quem os fiscalizou.
- **Entidades:** `npc.aerendyl-vaelorian`, `npc.saelith-arco-de-chuva`, `npc.mirel-caule-vivo`, `npc.arieth-tres-mudas`, `npc.voz-muitas-raizes`.
- **Relógio e evento:** `clock.heart-trees`, `event.corte-ilegal`.
- **Fonte:** doc 01 §13 A, §3 (Vigília Longa); 12-conhecimento.csv; 12-relogios.csv.

### `mist.b-oitava-cidade` — Mistério B — A oitava cidade

- **Fatos confirmados:** o manuscrito é fragmentário; o nome da comunidade está apagado; ela não assinou os juramentos; os registros não concordam sobre todos os nomes dos sete assentamentos; uma cópia incompleta circula entre estudiosos; Selvaris está dividida; Lethariel procura a crônica.
- **Hipóteses abertas:** assentamento real; metáfora; falsificação posterior.
- **A verdade não pode ser:** uma comunidade que assinou os sete juramentos; um nome fixado sem evidência; algo que mude a legitimidade da Coroa por si só (a posse do documento não torna o conteúdo verdadeiro e o efeito exige reconhecimento por instituições); algo que apague as sete copas de Elarion como símbolo dos sete assentamentos.
- **Evidência para resolver:** autenticação do manuscrito; comparação com a cópia incompleta e com cópias independentes; cadeia de custódia; registros dos sete assentamentos.
- **Entidades:** `npc.lethariel-nascente-clara`, `npc.selvaris-noctel`, `npc.marel-tinta-firme`.
- **Relógio e evento:** —.
- **Fonte:** doc 01 §13 B, §3 (Sete Juramentos), §14; doc 04 §10; 12-conhecimento.csv.

### `mist.c-navio-sem-tripulacao` — Mistério C — O navio sem tripulação

- **Fatos confirmados:** o navio chegou em boas condições, sem tripulação e sem sinais claros de combate; a carga foi registrada de forma incompleta; Yara preserva relatos de luzes incomuns perto da rota; houve um intervalo de luz não explicado numa noite no farol; o destino do irmão de Syranna é desconhecido.
- **Hipóteses abertas:** abandono; contrabando; evento incomum.
- **A verdade não pode ser:** uma cena com sinais claros de combate; explicação baseada só no rumor da comida quente; ligação já confirmada com o irmão de Syranna, as luzes ou o intervalo do farol (todos desconhecidos); culpa de todos os comerciantes de Thalanor ou de toda a Companhia do Âmbar e da Liga das Rotas.
- **Evidência para resolver:** lista de tripulação e carga completa; origem e destino da viagem; testemunhas do atraque; data e posição das luzes; registro do farol.
- **Entidades:** `npc.syranna-das-mares`, `npc.yara-sal-de-mare`, `npc.terenna-luz-de-lastro`, `faction.companhia-ambar`, `faction.liga-das-rotas`.
- **Relógio e evento:** `clock.ghost-ship`, `event.navio-quarentena`.
- **Fonte:** doc 01 §13 C, §5.3; 12-conhecimento.csv.

### `mist.d-mina-selada` — Mistério D — A mina selada

- **Fatos confirmados:** a mina está interditada; há vibrações e luzes relatadas; os tremores não ocorrem em intervalos regulares; pedidos de inspeção seguem sem resposta; abrir sem avaliação pode causar desabamento e expor trabalhadores; Dauren não representa um reino anão e não deseja anexar Torvelume às montanhas.
- **Hipóteses abertas:** atividade geológica; mecanismos antigos; invasores; algo sobrenatural.
- **A verdade não pode ser:** tremores em intervalo fixo; uma abertura sem avaliação sem risco de desabamento; marcas de ferramentas e símbolo declarado inexistente tratados como fatos (são rumores); anexação de Torvelume por um reino anão.
- **Evidência para resolver:** avaliação estrutural sem abrir; respostas aos pedidos de inspeção; registro dos tremores; inventário de ferramentas; ouvir o anão aposentado.
- **Entidades:** `npc.dauren-galho-de-ferro`, `npc.rovan-cinza-de-ferro`, `faction.guarda-espinha`.
- **Relógio e evento:** `clock.sealed-mine`, `event.mina-rompe`.
- **Fonte:** doc 01 §13 D, §5.10, §9 (Dauren); 12-conhecimento.csv.

### `mist.e-mapa-que-muda` — Mistério E — O mapa que muda

- **Fatos confirmados:** guias relatam marcas de trilha onde não foram colocadas; Mira sabe quais marcas foram alteradas e quem as alterou é desconhecido; Neralis guarda um mapa de uma pessoa desaparecida e não sabe se é confiável; Iven registrou uma sombra que não coincide com a posição da torre e não afirma causa sobrenatural; existe uma trilha de evacuação fora dos mapas comerciais.
- **Hipóteses abertas:** cópia incorreta de alguns mapas; alterações a verificar no terreno.
- **A verdade não pode ser:** todos os mapas errados por cópia; autor revelado sem evidência; confiança no mapa de Neralis sem teste; causa sobrenatural afirmada; rota de evacuação publicada para os mercadores.
- **Evidência para resolver:** verificação no terreno; comparação entre cópias; datas das marcas; teste curto do mapa de Neralis.
- **Entidades:** `npc.mira-ouve-chuva`, `npc.neralis-vento-manso`, `npc.iven-marca-de-som`, `faction.rede-guias`.
- **Relógio e evento:** —.
- **Fonte:** doc 01 §13 E, §5.7; doc 03 §8; 12-conhecimento.csv.

### `mist.h-resina-ilegal` — Conflito H — Resina ilegal

- **Fatos confirmados:** a Companhia do Âmbar compra material de origem duvidosa; alguns agentes podem estar envolvidos; a responsabilidade da liderança é desconhecida; a riqueza de Maelor não prova culpa e ele nega saber.
- **Hipóteses abertas:** agentes contratados sem conhecimento da liderança; participação da liderança; fraude de terceiros.
- **A verdade não pode ser:** culpa decidida por intuição narrativa; toda a Liga das Rotas envolvida; Maelor ou Tavian culpados ou inocentes fixados sem registro; prova que não seja carga, ferramenta, pagamento ou testemunho.
- **Evidência para resolver:** registros de carga, ferramentas, pagamentos e testemunhos.
- **Entidades:** `npc.maelor-venn`, `npc.tavian-lume-fino`, `faction.companhia-ambar`, `faction.liga-das-rotas`.
- **Relógio e evento:** `clock.resin-smuggling`, `event.rede-resina`.
- **Fonte:** doc 01 §13 H, §9; 12-conhecimento.csv; 12-relogios.csv.

### `mist.i-rainha-sem-retrato` — Lenda I — A Rainha sem Retrato

- **Fatos confirmados:** uma galeria tem um espaço vazio entre duas soberanas documentadas; a corte diz que o retrato foi perdido; a tradição popular diz que ela pediu para ser apagada; nenhuma fonte confiável confirma.
- **Hipóteses abertas:** retrato perdido; pedido de apagamento; outra causa.
- **A verdade não pode ser:** uma fonte confiável já confirmando a tradição popular; contradição com as duas soberanas documentadas; identidade fixada com o rei apagado da canção de Silvaran ou com a oitava cidade sem evidência; lenda tomada como verdade literal.
- **Evidência para resolver:** documentos da corte e do Arquivo; testemunhos; registros da galeria.
- **Entidades:** `npc.lethariel-nascente-clara`.
- **Relógio e evento:** —.
- **Fonte:** doc 01 §13 I, §15 regra 4; doc 01 §5.9 (rumor da canção).

### `mist.j-raposa-nomes` — Lenda J — A raposa que devolve nomes

- **Fatos confirmados:** a raposa de espelho seria vista em noites de névoa perto de quem esqueceu o próprio nome ou abandonou uma promessa; pode ser comportamento animal interpretado como presságio; não há evidência de que julgue moralidade.
- **Hipóteses abertas:** comportamento animal; presságio cultural; fenômeno ainda não explicado.
- **A verdade não pode ser:** uma criatura que julgue moralidade (sem evidência); um bônus ou efeito mecânico (nomes e lore não concedem bônus); sobrenatural confirmado sem evidência.
- **Evidência para resolver:** observação repetida; registros de avistamentos; relatos de quem a viu.
- **Entidades:** `npc.neralis-vento-manso`.
- **Relógio e evento:** —.
- **Fonte:** doc 01 §13 J, §15 regras 4 e 9.
