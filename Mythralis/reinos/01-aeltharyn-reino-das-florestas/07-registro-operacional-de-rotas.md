# Aeltharyn — Registro operacional de rotas

**Estado:** registro editorial consolidado (M2.1, 25/10/2026): rotas principais, acessos locais dos 20 assentamentos (seção 3.1, todos com duração *desconhecida*) e corredores resolvidos ou marcados. Mapa esquemático em `07-mapa-esquematico.md`.  
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
| route.nymvar-arven | Nymvar ↔ Arven | estrada e canais; Rota dos Celeiros | 2–4 dias por caravana; 1 dia só para viajante leve (D-12) | Documentada com ressalva | No Despertar, lama e cheias podem duplicar o prazo da caravana; carga e comitiva ampliam o tempo |
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
| Nymvar ↔ Thalanor (`route.nymvar-thalanor`) | eixo de carga do Lúmen | Documentada com ressalva | Resolvido: 3–4 dias de barco de carga em condições comuns; contra a corrente pode demorar quase o dobro; viagem direta Elarion–Thalanor não equivale a esta etapa com escala |
| Arven ↔ Silvaran (`route.arven-silvaran`) | Rota dos Celeiros (Nymvar–Arven–Silvaran): "caravanas levam 2–4 dias entre cidades"; no Despertar, lama e cheias podem duplicar o prazo | Documentada com ressalva (intervalo do corredor, sem trecho individual) | Usar 2–4 dias por caravana; traçado e sazonalidade fora do Despertar seguem não definidos. Nymvar–Arven também é 2–4 dias por caravana (D-12) |
| Torvelume ↔ Vael-Tir | trecho da Estrada da Espinha | Documentada com ressalva | cerca de 1–2 dias; geada, deslizamentos ou interdição podem bloquear a passagem |
| Orvass ↔ Brumafunda (`route.orvass-brumafunda`) | Rota do Pântano; doc 01 diz que "continua sem duração individual validada"; mapas antigos não garantem passagem | **Desconhecida** (decisão M2.1: permanece sem duração) | percurso real, navegabilidade e duração; não calcular tempo nem somar via Nymvar ou Vael-Tir |
| Elarion ↔ Vael-Tir ↔ Brumafunda | corredor das Copas | Documentada com modos distintos | os 1–3 dias por trecho são para mensageiros treinados; as estimativas da seção 6 são para viagem comum e não devem ser comparadas como se fossem o mesmo ritmo |
| Caladwen ↔ Torvelume ↔ Vael-Tir | corredor da Espinha | Resolvida editorialmente | cerca de 3 dias até Torvelume e mais 1–2 dias até Vael-Tir; total normal de 4–5 dias, sem paradas longas |

O corredor da Espinha foi normalizado: Caladwen–Torvelume dura cerca de 3 dias e Torvelume–Vael-Tir cerca de 1–2, portanto o total de 4–5 dias exclui paradas longas. Os demais corredores sem duração individual continuam sem total calculável. Mesmo quando os trechos individuais existem, a rota regional pode ter desvios, paradas, controle de acesso ou caminhos diferentes. Somar durações só é permitido quando os segmentos são contínuos e comparáveis.

### 3.1 Acessos locais dos vinte assentamentos (M2.1)

Só entra o que o texto dos docs 01, 03 (seções 1.1 e 16) e 09 sustenta. **Duração desde a cidade-mãe: desconhecida para os 20** (nenhum tempo foi atribuído por suposição). "A confirmar" = o doc 03 lista o ponto como pendente.

| Assentamento | Cidade-mãe | Acesso descrito no texto | Duração |
|---|---|---|---|
| `settlement.raiz-de-prata` | Elarion | núcleo de viveiros ligado a Elarion; posição e acesso a confirmar | desconhecida |
| `settlement.sete-degraus` | Elarion | ponte e pedágio no acesso terrestre da capital | desconhecida |
| `settlement.junco-alto` | Nymvar | diques e comportas do sistema ribeirinho de Nymvar | desconhecida |
| `settlement.tres-barcas` | Nymvar | travessias fluviais e registro de cargas; rota fluvial; tráfego sazonal | desconhecida |
| `settlement.ponta-da-concha` | Thalanor | costa e barcos pequenos, ligada ao sistema portuário | desconhecida |
| `settlement.farol-de-ambar` | Thalanor | posto costeiro separado do núcleo urbano; distinto do Farol da Folha Azul | desconhecida |
| `settlement.copa-funda` | Vael-Tir | comunidade florestal; acesso por trilha a confirmar | desconhecida |
| `settlement.passo-do-veado` | Vael-Tir | trilha e corredor migratório; ocupação sazonal | desconhecida |
| `settlement.lodo-claro` | Orvass | área úmida de Orvass; passarelas ou canais a confirmar | desconhecida |
| `settlement.ilhas-junco-negro` | Orvass | passagens aquáticas entre ilhotas e canais sazonais | desconhecida |
| `settlement.vigilia-do-lago` | Caladwen | núcleo lacustre; localização do lago e acesso a confirmar | desconhecida |
| `settlement.papel-velho` | Caladwen | oficinas de copistas; no centro ou em núcleo separado, a confirmar | desconhecida |
| `settlement.eco-baixo` | Brumafunda | transição entre o núcleo e as trilhas de neblina; caminhos públicos ou reservados a confirmar | desconhecida |
| `settlement.torre-nevoa-mansa` | Brumafunda | posto exterior de observação; depende do abastecimento de Brumafunda; fecha trilhas por alerta | desconhecida |
| `settlement.mel-do-poente` | Arven | cinturão agrícola de Arven (clareiras ligadas por estrada e canais, doc 01); limites a confirmar | desconhecida |
| `settlement.campo-tres-familias` | Arven | entorno agrícola de Arven; núcleo compacto ou famílias dispersas, a confirmar | desconhecida |
| `settlement.mascara-nova` | Silvaran | distrito artesanal; dentro da malha urbana ou arredores, a confirmar | desconhecida |
| `settlement.fonte-do-regresso` | Silvaran | comunidade de acolhimento; acesso a confirmar; distinta da Casa da Hospitalidade | desconhecida |
| `settlement.ferro-folha` | Torvelume | distrito de oficinas; dentro da fortaleza, no entorno ou em rota de abastecimento, a confirmar | desconhecida |
| `settlement.porta-selada` | Torvelume | núcleo de acesso controlado junto às galerias interditadas | desconhecida |

### 3.2 Divergências com o catálogo técnico (`referencia-tecnica/AeltharynWorld.kt`, não canônico)

O `.kt` tem 14 rotas com dias fixos. Não valem como cânone; ficam registradas para o M6.3.

| Ligação no `.kt` | Dias no `.kt` | Situação nos documentos |
|---|---:|---|
| Elarion–Nymvar | 2 | confere (estrada) |
| Nymvar–Thalanor | 4 | dentro de 3–4 |
| Elarion–Vael-Tir | 2 | docs dizem 3 (trilhas patrulhadas) |
| Vael-Tir–Brumafunda | 2 | dentro de 2–4 |
| Nymvar–Arven | 2 | dentro de 2–4 por caravana (D-12) |
| Arven–Silvaran | 3 | dentro de 2–4 (corredor) |
| Caladwen–Torvelume | 4 | docs dizem 3 |
| Torvelume–Vael-Tir | 3 | docs dizem 1–2 |
| Orvass–Nymvar | 3 | confere |
| Orvass–Brumafunda | 5 | docs: **desconhecida** |
| Caladwen–Thalanor | 4 | sem ligação nos docs |
| Brumafunda–Arven | 4 | sem ligação nos docs |
| Arven–Torvelume | 4 | sem ligação nos docs |
| Elarion–Torvelume | 5 | docs dizem 4 |

Ausentes no `.kt` e presentes nos docs: Elarion–Caladwen, Elarion–Arven, Thalanor–Elarion, Thalanor–Silvaran, Caladwen–Brumafunda.

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
- [x] Validar conexões de acesso dos vinte assentamentos sem atribuir tempos por suposição (seção 3.1, M2.1).
- [x] Definir que o corredor das Copas permite 1–3 dias por trecho para mensageiros treinados; outros tempos de transmissão continuam não definidos e não podem ser presumidos instantâneos.
- [x] Cruzar as consequências de bloqueios com recursos, relógios e oportunidades de aventura; não presumir escassez imediata sem verificar reservas, demanda e alternativas.

## 8. Critério de conclusão

O registro só pode ser considerado validado quando todas as ligações e corredores usados em cenas de prazo crítico tiverem topologia e duração coerentes, os assentamentos tiverem acesso plausível e os conflitos entre documentos estiverem resolvidos ou explicitamente marcados como decisões ainda abertas.

Até lá, Aeltharyn pode produzir aventuras de investigação, negociação e exploração relacionadas a rotas incertas, mas não deve fingir possuir uma malha cartográfica completa.
