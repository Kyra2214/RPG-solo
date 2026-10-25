# Aeltharyn — Registro operacional de rotas

**Estado:** registro editorial consolidado para as rotas principais; permanecem pendentes apenas os acessos locais dos assentamentos e conexões sem duração individual.  
**Escopo:** preparar a rede de deslocamento para consultas narrativas, geração de aventuras e futura simulação.  
**Fonte principal:** seção 6, “Rede de rotas e distâncias”, de `01-reino-das-florestas.md`, confrontada com as divergências registradas em `05-auditoria-demografia-rotas-recursos.md`.  
**Regra:** uma conexão não listada aqui não é impossível; significa que ainda não possui um percurso e tempo canônicos validados.

## 1. Como usar este registro

Este documento não é um mapa cartográfico final. Organiza a evidência disponível para impedir que a IA invente tempos de viagem ou trate todo caminho descrito em prosa como uma rota medida.

Estados editoriais:
- **Documentada:** há uma ligação individual e uma estimativa explícita no documento principal.
- **Documentada com ressalva:** há estimativa explícita, mas também uma variação, condição ou ambiguidade que precisa ser preservada.
- **Corredor sem duração individual:** a conexão é sugerida por um corredor regional, mas não tem duração própria validada.
- **Conflito de duração:** duas descrições não permitem estabelecer com segurança se tratam do mesmo trecho, de um trecho parcial ou de condições diferentes.
- **Pendente:** faltam evidências suficientes para fixar caminho ou tempo.

“Documentada” significa que o texto existe no cânone; não significa que a duração já foi testada contra um mapa em escala.

## 2. Ligações interurbanas explicitamente descritas

| ID editorial | Ligação | Meio/condição descrita | Tempo no documento principal | Estado operacional | Restrição de uso |
|---|---|---|---|---|---|
| route.elarion-nymvar | Elarion ↔ Nymvar | estrada; alternativa fluvial | 2 dias por estrada; 1–2 dias pelo rio, conforme corrente | Documentada com ressalva | Usar o intervalo fluvial; corrente e estação podem alterar o tempo |
| route.elarion-vael-tir | Elarion ↔ Vael-Tir | trilhas patrulhadas | 3 dias; mais com quarentena florestal | Documentada com ressalva | Quarentena pode alongar ou bloquear o percurso; não há duração máxima fixada |
| route.elarion-caladwen | Elarion ↔ Caladwen | estrada; trilhas rápidas | 2 dias de estrada; 1,5 dia por trilhas rápidas | Documentada | A opção rápida pressupõe trilhas disponíveis e conhecimento adequado |
| route.elarion-arven | Elarion ↔ Arven | estrada | 1,5 dia | Documentada | A estação e as condições da estrada ainda não têm modificadores definidos |
| route.nymvar-orvass | Nymvar ↔ Orvass | barco e passarelas; canais sazonais | cerca de 3 dias em condições comuns; 2–6 dias com desvios, cheia ou canais alterados | Documentada com variação sazonal | Usar 3 dias como referência normal; a faixa 2–6 descreve variação do corredor, não uma segunda rota contraditória |
| route.nymvar-arven | Nymvar ↔ Arven | estrada e canais | 1 dia | Documentada | Não há condições sazonais específicas quantificadas |
| route.thalanor-elarion | Thalanor ↔ Elarion | barco pelo Lúmen; estrada costeira | 3 dias por barco; 5 dias por estrada | Documentada com ressalva | Tempos pertencem a meios diferentes; não compará-los sem considerar carga, clima e objetivo |
| route.thalanor-silvaran | Thalanor ↔ Silvaran | estrada por pomares e vales | 4 dias | Documentada | Não há rota alternativa quantificada |
| route.caladwen-brumafunda | Caladwen ↔ Brumafunda | percurso com guia | 2 dias com guia; sem guia pode demorar muito mais | Documentada com ressalva | Não aplicar o tempo de 2 dias a viajante sem guia |
| route.brumafunda-vael-tir | Brumafunda ↔ Vael-Tir | trilhas abertas | 2–4 dias | Documentada com ressalva | A duração depende das trilhas disponíveis |
| route.torvelume-elarion | Torvelume ↔ Elarion | estrada de fronteira | 4 dias | Documentada | Postos, bloqueios e condições da fronteira não têm duração adicional definida |
| route.torvelume-vael-tir | Torvelume ↔ Vael-Tir | trecho da Estrada da Espinha; sujeito a geada, deslizamento e interdição | cerca de 1–2 dias | Estimativa operacional consolidada | Usar como intervalo provisório de referência; se a passagem estiver fechada, não calcular desvio sem rota confirmada |
| route.torvelume-caladwen | Torvelume ↔ Caladwen | estrada com risco de deslizamento na chuva | cerca de 3 dias | Documentada com ressalva | O trecho até Vael-Tir é adicional; ver a ligação `route.torvelume-vael-tir` |

### 2.1 Interpretação de intervalos e estimativas

- Uma duração expressa em dias é uma estimativa de viagem, não uma garantia de chegada.
- Não converter “1,5 dia” automaticamente em número exato de horas para cenas com relógio preciso.
- Se um personagem viaja sem guia, com carga pesada, ferido, com animais ou por trilhas fechadas, não aplicar automaticamente a estimativa padrão.
- Quando o cânone não define um modificador, descrever a incerteza qualitativamente ou pedir uma decisão editorial; não inventar um multiplicador.
- A ida e a volta não precisam ter o mesmo tempo se corrente, vento, carga ou condições mudarem, mas a diferença precisa de uma causa plausível.

## 3. Conexões mencionadas em corredores, ainda sem duração individual validada

A auditoria do documento 05 identifica as seguintes conexões mencionadas ou sugeridas pelos corredores regionais:

| Ligação sugerida | Evidência editorial disponível | Estado | O que falta decidir |
|---|---|---|---|
| Nymvar ↔ Thalanor | corredor regional de viagem | Corredor sem duração individual | se há ligação direta ou se a viagem exige passar por outra cidade; modo e tempo |
| Arven ↔ Silvaran | corredor regional de viagem | Corredor sem duração individual | traçado, modo, duração e sazonalidade |
| Torvelume ↔ Vael-Tir | corredor regional da Espinha | Corredor sem duração individual | se é trecho contínuo e qual o tempo específico |
| Orvass ↔ Brumafunda | corredor regional do Pântano | Corredor sem duração individual | percurso real, navegabilidade e duração em condições comuns |
| Elarion ↔ Vael-Tir ↔ Brumafunda | corredor das Copas reforça o eixo | Parcialmente documentada | Elarion–Vael-Tir e Vael-Tir–Brumafunda têm tempos; falta confirmar se o corredor usa exatamente os mesmos trechos |
| Caladwen ↔ Torvelume ↔ Vael-Tir | corredor da Espinha | Conflito de duração | duração do trecho completo versus duração de cada ligação e condições de chuva |

O corredor da Espinha foi normalizado: Caladwen–Torvelume dura cerca de 3 dias e Torvelume–Vael-Tir cerca de 1–2, portanto o total de 4–5 dias exclui paradas longas. Os demais corredores sem duração individual continuam sem total calculável. Mesmo quando os trechos individuais existem, a rota regional pode ter desvios, paradas, controle de acesso ou caminhos diferentes. Somar durações só é permitido quando os segmentos são contínuos e comparáveis.

## 4. Regras provisórias para o Mestre e para a LLM

Para as rotas principais já consolidadas, aplicar as decisões acima; para as conexões ainda sem duração individual:

1. Usar os tempos explicitamente documentados apenas para as ligações correspondentes e sob suas condições descritas.
2. Para conexões de corredor sem tempo, narrar a existência de um caminho apenas se a descrição do corredor sustentar essa leitura; tratar a duração como desconhecida.
3. Não anunciar uma hora exata de chegada, encontro ou prazo crítico a partir de um caminho não validado.
4. Não concluir que uma rota alternativa existe apenas porque duas cidades são próximas na narrativa.
5. Não concluir que a rota é segura, pública ou controlada pela Coroa apenas porque aparece em um corredor.
6. Se uma missão depende de tempo de viagem exato, considerar a rota um pré-requisito editorial pendente e não resolver o prazo por improviso.
7. Um bloqueio deve afetar apenas os fluxos que passam pelo trecho bloqueado. Verificar alternativas conhecidas antes de declarar isolamento ou escassez.
8. Para notícias, separar o tempo físico da viagem do tempo necessário para preparar, enviar e retransmitir a mensagem. O sistema ainda não possui durações canônicas para toda a rede de mensageiros.

## 5. Contrato mínimo para cada rota futura

Antes de promover uma ligação pendente a rota operacional, registrar:

- ID estável;
- localidades de origem e destino;
- se a ligação é direta ou composta por trechos;
- modo de viagem e condições padrão;
- duração normal como intervalo, quando houver evidência suficiente;
- condições que aumentam, reduzem ou impedem a viagem;
- sazonalidade e eventos que alteram a passagem;
- existência de rota alternativa confirmada;
- autoridade ou comunidade que mantém, controla ou fecha o caminho;
- quem conhece o trajeto;
- efeitos de um bloqueio sobre comércio, mensagens, socorro e deslocamento;
- fonte canônica e decisão editorial que validou a entrada.

Não preencher campos ausentes com valores fictícios. Usar “não definido” até que uma decisão baseada no cenário seja registrada.

## 6. Relação entre rotas, recursos e aventuras

Uma rota pode ligar sistemas diferentes, mas a consequência de seu bloqueio depende da carga, dos viajantes e das alternativas.

| Fluxo | Verificação necessária antes de gerar consequência |
|---|---|
| Alimentos | origem, destino, estoque local, prazo de reposição e rota alternativa conhecida |
| Ferramentas e materiais | oficina produtora, encomenda, prioridade civil/militar e estoque disponível |
| Ervas e remédios | fornecedor, validade, necessidade clínica e capacidade de atendimento |
| Mensagens | remetente, destinatário, portador ou rede, acesso à rota e prazo plausível |
| Patrulhas e socorro | autoridade responsável, disponibilidade de equipe e caminho transitável |
| Migração e pastoreio | corredor reconhecido, estação, acordo local e grupos afetados |
| Contrabando | caminho conhecido pelos envolvidos, risco, ocultação e possibilidade real de fiscalização |

A IA não deve presumir que uma interrupção causa escassez imediata. É necessário verificar estoque, demanda, substitutos, duração do bloqueio e capacidade de redirecionar o fluxo. Um bloqueio curto pode gerar atraso; um bloqueio prolongado pode gerar perda, aumento de preço ou conflito, mas a escala precisa ser sustentada pelo estado material.

## 7. Decisões editoriais pendentes

- [x] Distinguir viagem direta Elarion–Thalanor (3 dias, embarcação leve/rápida) da rota de carga via Nymvar (5–6 dias com escala).
- [x] Resolver Nymvar–Orvass: 3 dias em condições comuns; 2–6 dias quando as condições do corredor do Pântano exigirem desvios.
- [x] Resolver a Estrada da Espinha: Caladwen–Torvelume ≈ 3 dias; Torvelume–Vael-Tir ≈ 1–2 dias; corredor completo ≈ 4–5 dias.
- [x] Definir dias como tempo decorrido de viagem em condições comuns, incluindo pausas usuais, mas excluindo paradas longas; clima, carga, guia e bloqueios podem alterar o intervalo.
- [ ] Validar conexões de acesso dos vinte assentamentos sem atribuir tempos por suposição.
- [ ] Definir quais rotas sustentam mensageiros regulares e quanto tempo as notícias demoram, sem presumir transmissão instantânea.
- [x] Cruzar as consequências de bloqueios com recursos, relógios e oportunidades de aventura; não presumir escassez imediata sem verificar reservas, demanda e alternativas.

## 8. Critério de conclusão

O registro só pode ser considerado validado quando todas as ligações e corredores usados em cenas de prazo crítico tiverem topologia e duração coerentes, os assentamentos tiverem acesso plausível e os conflitos entre documentos estiverem resolvidos ou explicitamente marcados como decisões ainda abertas.

Até lá, Aeltharyn pode produzir aventuras de investigação, negociação e exploração relacionadas a rotas incertas, mas não deve fingir possuir uma malha cartográfica completa.
