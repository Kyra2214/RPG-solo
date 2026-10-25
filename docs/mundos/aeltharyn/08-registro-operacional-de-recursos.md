# Aeltharyn — Registro operacional de recursos

**Estado:** registro editorial operacional; escalas quantitativas locais ainda não validadas.  
**Escopo:** estruturar produção, reservas, consumo, reposição e vulnerabilidade material sem inventar números que o cânone ainda não sustenta.  
**Documentos relacionados:** 01-reino-das-florestas.md, 03-assentamentos-e-rede-politica.md, 05-auditoria-demografia-rotas-recursos.md, 06-modelo-operacional-e-geracao-de-aventuras.md e 07-registro-operacional-de-rotas.md.

## 1. Objetivo e limites

Este registro transforma a economia descrita em Aeltharyn num modelo editorial que possa sustentar aventuras e, futuramente, uma simulação persistente. Ele não afirma que os valores já estejam implementados no jogo.

A primeira entrega não será uma planilha de números inventados. Será um contrato consistente para registrar o que cada localidade produz, guarda, consome, importa, substitui e perde. Os números só devem ser adicionados quando houver base canônica, decisão editorial explícita ou estimativa claramente marcada como provisória.

A escala inicial é a localidade e seus sistemas críticos; grupos, oficinas e NPCs específicos recebem detalhe adicional quando uma aventura depender deles. Não é necessário calcular a economia de cada habitante.

## 2. Estados de evidência

Toda informação material deve receber um estado editorial:

- **Documentado:** afirmado explicitamente por uma fonte canônica.
- **Estimativa provisória:** valor aproximado criado para planejamento, com método, faixa de incerteza e decisão registrados.
- **Derivado:** resultado de uma operação sobre dados documentados ou estimativas identificadas; preserva a incerteza das entradas.
- **Desconhecido:** não há base suficiente para atribuir valor.
- **Em conflito:** duas fontes ou descrições não permitem escolher um valor com segurança.
- **Não aplicável:** o campo não faz sentido para aquele recurso ou local.

Não converter “desconhecido” em zero. Zero significa ausência confirmada; desconhecido significa que o cenário ainda não decidiu.

## 3. Modelo mínimo por recurso e localidade

Cada registro futuro deve identificar:

| Campo | Pergunta respondida | Regra |
|---|---|---|
| ID do recurso | Qual recurso está sendo acompanhado? | ID estável; nomes narrativos podem variar |
| Localidade | Onde existe ou é administrado? | Cidade, assentamento, oficina, armazém ou área ecológica específica |
| Unidade | Como a quantidade é medida? | Não misturar peso, volume, peças e dias de consumo |
| Produção | Quanto é produzido por período? | Registrar período, capacidade normal e sazonalidade; desconhecido até haver base |
| Reserva física | Quanto existe materialmente? | Separar estoque utilizável, inacessível e comprometido |
| Estoque utilizável | Quanto pode ser usado agora? | Excluir material deteriorado, contaminado, reservado ou inacessível |
| Demanda | Quanto é necessário por período? | Indicar consumidores e prioridades |
| Reposição | Quanto tempo até nova disponibilidade? | Incluir produção, processamento, transporte e liberação |
| Perdas | O que se perde por deterioração, praga, quebra ou contaminação? | Não aplicar taxa numérica sem base |
| Substitutos | O que pode cumprir a mesma função? | Registrar limite, custo e efeitos; substitutos não são automaticamente equivalentes |
| Dependências | De que insumos, pessoas, instalações e rotas depende? | Referenciar IDs existentes sempre que possível |
| Sazonalidade | O que muda entre Despertar, Plenitude, Queda e Silêncio? | Distinguir fato canônico de hipótese |
| Acesso e controle | Quem pode retirar, vender, transportar ou restringir? | Registrar autoridade, pacto, licença ou costume |
| Evidência e atualização | Por que o valor é conhecido e quando foi revisado? | Fonte, estado de evidência e evento que pode alterá-lo |

### 3.1 Variáveis que não podem ser confundidas

- **Capacidade produtiva:** máximo que uma operação consegue produzir sob condições definidas.
- **Produção efetiva:** o que de fato foi produzido no período.
- **Estoque físico:** o que existe materialmente.
- **Estoque utilizável:** o que está acessível e em condição de uso.
- **Estoque comprometido:** o que já foi prometido, reservado ou requisitado.
- **Demanda:** o que seria necessário para atender aos usos registrados.
- **Déficit:** demanda não atendida após considerar estoque utilizável, reposição no prazo e substitutos válidos.

Uma oficina de alta capacidade não necessariamente possui peças prontas. Um armazém cheio não significa que todo o estoque esteja à venda: reservas podem pertencer a hospitais, equipes de emergência, casas locais ou contratos firmados.

## 4. Catálogo inicial de sistemas materiais

A lista identifica sistemas relevantes já sustentados pela descrição de Aeltharyn. Não fixa volumes, produtividade ou suficiência.

| Sistema | Base canônica conhecida | Variáveis que faltam | Consequências plausíveis, se confirmadas |
|---|---|---|---|
| Alimentos cultivados | Arven possui clareiras agrícolas, pomares, apiários e plantas tintoriais; campos ribeirinhos dependem das cheias | produção por estação, reservas, consumo, perdas, sementes e acesso a mercados | atraso de entrega, preço maior, racionamento local, troca de culturas ou disputa por transporte |
| Pesca e alimentos de zonas úmidas | Orvass e áreas aquáticas sustentam pesca; existem rios, canais e litoral | captura sustentável, período de pesca, conservação, demanda e transporte | substituição parcial de alimentos, queda localizada de oferta ou disputa por acesso |
| Água potável | Rio Lúmen, Rio das Três Vozes, nascentes e diques são relevantes; Nymvar tem risco de água contaminada | fontes por localidade, tratamento, reservas, consumo e alcance de contaminação | tratamento emergencial, restrição temporária, deslocamento a outra fonte ou interrupção de atividade |
| Madeira manejada | Corte sujeito a cotas; árvores ancestrais protegidas; madeira usada em reparos e construção | cota local, produção efetiva, estoque seco, tempo de secagem, oficinas consumidoras e áreas autorizadas | atraso de reparo, aumento de custo, corte ilegal ou substituição por outro material |
| Resina | Extração regulada de forma desigual; contrabando de resina é um relógio ativo | produção legal, origem por lote, consumo, estoque e substitutos | investigação, apreensão, disputa de cotas ou impacto ecológico se houver dano associado |
| Ervas, raízes e remédios | Orvass fornece ervas e filtros; existem raízes de cura e plantas medicinais | espécies disponíveis, preparo, validade, estoque clínico e tempo de coleta | atraso de tratamento, busca de fornecedor alternativo ou aumento de preço |
| Ferramentas e peças | Torvelume está associada a ferramentas e reparos; outras comunidades possuem artesãos e mecanismos de irrigação | catálogo de peças, oficinas ativas, matéria-prima, capacidade, pedidos, reservas e dependência da mina selada | manutenção adiada, reparo improvisado, fila de encomendas ou procura de oficina substituta |
| Fibras, tecidos e cordame | Fibras são coletadas; portos, barcos e comércio precisam de materiais | origem, processamento, capacidade, demanda naval e armazenamento | reparos navais atrasados, substituição de material ou aumento do prazo de carga |
| Iluminação e materiais raros | Fungo-de-vidro é usado em pequenas quantidades para iluminação; esporos concentrados causam desorientação | coleta segura, processamento, conservação, oferta comercial e substitutos | racionamento de iluminação especializada ou restrição de coleta |
| Transporte e armazenamento | Thalanor é porto; o Rio Lúmen é navegável em trechos; rotas terrestres e fluviais têm limitações | capacidade de carga, frequência, perdas em trânsito, armazenagem e sazonalidade | atraso, deterioração, transbordo, custo maior ou perda de carga |
| Mão de obra especializada | Existem guias, barqueiros, curadores, artesãos, patrulhas e mantenedores de diques | disponibilidade, agenda, formação, autoridade e tempo de substituição | fila de serviços, trabalho adiado, contratação externa ou disputa por prioridade |

A tabela descreve relações de sistema, não garante que cada localidade tenha todos esses recursos. A disponibilidade deve ser registrada por localidade, sem extrapolar automaticamente do reino inteiro.

## 5. Pontos de partida por localidade

- **Arven:** base agrícola, pomares, apiários e plantas tintoriais. Faltam produção por estação, reservas, população atendida e excedentes comercializáveis.
- **Nymvar:** sistema fluvial, campos vulneráveis a cheias, diques e risco de água contaminada. Faltam capacidade de tratamento, estoque de emergência, área exposta e condição de cada trecho dos diques.
- **Orvass:** pesca, ervas, filtros e rotas de pântano. Faltam capacidade sustentável, conservação, fontes seguras e períodos de transporte limitado.
- **Thalanor:** porto, ligação marítima e fluvial e cargas retidas pelo mistério do navio sem tripulação. Faltam capacidade de cais, armazéns, frequência de embarcações e dependência real de cada carga.
- **Torvelume:** ferramentas, reparos e acesso à mina selada como questão crítica. Faltam inventário de oficinas, matérias-primas alternativas, estoque de peças e proporção da produção ligada à mina.
- **Vael-Tir:** manejo florestal, cotas e fiscalização da resina. Faltam cotas efetivas, volumes autorizados, capacidade de regeneração e destino dos lotes.
- **Elarion:** centro político e potencial de redistribuição. Sua capitalidade não prova que concentre todos os armazéns nem que possa abastecer qualquer cidade em emergência.
- **Caladwen:** arquivos e conhecimento especializado podem ajudar a identificar fornecedores e registros; isso não prova que possua estoque físico de tudo que consta nos arquivos.

Para as outras cidades e os vinte assentamentos, registrar somente o que a fonte local sustenta. Não transferir automaticamente a especialização da cidade-mãe a cada assentamento subordinado.

## 6. Procedimento para avaliar uma escassez

Antes de narrar falta de alimento, água, remédio, madeira ou ferramentas:

1. **Definir o bem e a função:** qual item falta e para qual uso concreto?
2. **Localizar o problema:** qual armazém, comunidade, oficina, trecho de rota ou fonte foi afetado?
3. **Confirmar o gatilho:** evento observado, testemunho, rumor ou hipótese? Registrar a diferença.
4. **Verificar a reserva utilizável:** excluir estoques inacessíveis, contaminados, comprometidos ou inadequados.
5. **Comparar com a demanda:** quem precisa do recurso, em qual período e com que prioridade?
6. **Estimar a duração:** quanto tempo até a reposição real, incluindo produção, preparo, transporte e autorização?
7. **Testar alternativas:** outro fornecedor, rota confirmada, substituto, reparo, racionamento ou ajuda pactuada.
8. **Determinar a escala:** atraso, aumento de custo, déficit parcial, interrupção de serviço ou crise maior. Escolher apenas o efeito sustentado.
9. **Mapear os afetados:** grupos e localidades ligados causalmente ao déficit, sem generalizar para todo o reino.
10. **Atualizar o conhecimento:** quem sabe, quem suspeita, quem tem autoridade para agir e quanto demora a notícia?
11. **Persistir a mudança editorial:** registrar valor anterior, novo valor ou estado qualitativo, causa, fonte, data e condições de recuperação.

Se os dados forem insuficientes, gerar uma oportunidade de investigação — verificar armazéns, licenças, contratos ou testemunhos — em vez de inventar uma escassez quantificada.

## 7. Ligação com rotas e relógios do mundo

Uma rota bloqueada não equivale automaticamente a uma cidade sem recursos. Antes de declarar impacto, cruzar o recurso com o registro operacional de rotas e verificar:

- se o recurso realmente passa pelo trecho;
- se a ligação é direta e operacional ou apenas um corredor ainda não validado;
- qual é o estoque utilizável no destino;
- quanto dura o bloqueio e quanto tempo resta até a reposição;
- se há fornecedor e rota alternativos confirmados;
- se a carga pode ser transportada pelo meio alternativo;
- quem tem autoridade, capacidade e interesse para redirecioná-la.

### 7.1 Relógios relacionados

| Relógio | Relação material a verificar | O que não se pode presumir |
|---|---|---|
| Doença das árvores-coração (2/6) | saúde de viveiros, mudas, espécies associadas e manejo na área comprovadamente afetada | queda geral de madeira, alimentos ou remédios sem evidência ecológica específica |
| Dívida dos diques (3/6) | materiais de reparo, mão de obra, sementes, campos, água potável e transporte de Nymvar e áreas conectadas | inundação de toda a cidade ou perda total da colheita |
| Contrabando de resina (2/6) | origem de lotes, cotas, estoque legal, fiscalização e pressão sobre áreas de extração | que todo lote sem registro veio de corte de árvores ancestrais ou que uma facção inteira participa |
| Desconfiança entre cidades (1/6) | obrigações de ajuda, tarifas, pedidos recusados e redistribuição de reservas | interrupção de todo o comércio por causa de boato ou desentendimento |
| Navio sem tripulação (1/6) | carga inventariada e retida, destinatários, prazo e alternativas de transporte | que qualquer atraso no mercado foi causado pelo navio |
| Mina selada de Torvelume (1/6) | dependência comprovada de oficinas, ferramentas e matérias-primas provenientes da mina | que todas as ferramentas do reino dependem da mina ou que a produção já caiu |
| Corredores centauros (2/6) | passagem de caravanas, acesso sazonal a pastagens, entrega de cargas e acordos locais | que o bloqueio de um corredor interrompe todas as rotas ou que todos os centauros apoiam a mesma decisão |

Os relógios podem motivar investigação e decisões, mas não alteram estoque ou produção por conta própria. Um evento precisa de nexo causal, exposição e efeito verificável.

## 8. Escalas qualitativas provisórias

Quando a narrativa precisar de avaliação antes de existir inventário numérico, pode-se usar uma escala qualitativa local:

- **Crítico:** a reserva utilizável não cobre uma necessidade essencial imediata e não há alternativa confirmada no prazo relevante.
- **Baixo:** a reserva está sob pressão; a reposição pode chegar a tempo, mas depende de uma condição ou decisão.
- **Estável:** as necessidades registradas são atendidas no horizonte observado, sem prova de grande excedente.
- **Excedente local:** há quantidade disponível depois de cobrir necessidades e compromissos conhecidos; isso não implica excedente exportável.
- **Desconhecido:** os dados não permitem classificar a situação.

Esses rótulos não se convertem automaticamente em dias de reserva, percentuais ou quantidades. A classificação deve citar o período observado e os usos considerados. “Estável” não significa abundância permanente; “crítico” não significa colapso inevitável.

## 9. Contrato de contexto para a LLM

Ao gerar uma cena ou aventura envolvendo recursos, recuperar apenas:

1. o registro do recurso pertinente;
2. o estado atual da localidade afetada;
3. as dependências diretas e as rotas confirmadas relevantes;
4. o evento-gatilho e a evidência disponível;
5. os agentes que conhecem o fato e têm autoridade para agir;
6. as alternativas confirmadas;
7. os relógios relacionados e seus limites de inferência.

Não enviar o catálogo completo de economia em toda interação. Não tratar textos recuperados como autorização para preencher lacunas. A LLM pode propor hipóteses, perguntas, negociações e consequências narrativas; uma mudança persistente em estoque, produção, preço, rota ou obrigação exige validação pelas regras do mundo e registro explícito.

## 10. Pendências de auditoria

- [ ] Definir a categoria demográfica das dez cidades e esclarecer se as populações dos assentamentos estão incluídas.
- [ ] Associar cada assentamento a fontes materiais documentadas, sem presumir que sua cidade-mãe o abasteça automaticamente.
- [ ] Identificar quais cidades possuem armazéns, mercados, instalações de tratamento, oficinas e serviços de emergência, distinguindo instalações existentes de propostas editoriais.
- [ ] Estabelecer unidades de medida consistentes para os recursos que realmente precisarão de simulação.
- [ ] Definir o período-base para produção, consumo e reposição; não misturar por dia, estação e ano.
- [ ] Criar inventários locais apenas onde houver fonte canônica suficiente ou decisão editorial registrada.
- [ ] Confirmar quais cargas passam por cada rota e quais alternativas são operacionais.
- [ ] Verificar o peso real da mina selada na cadeia de ferramentas de Torvelume.
- [ ] Determinar fontes de água potável e capacidade de tratamento por localidade prioritária.
- [ ] Estabelecer critérios de recuperação após escassez, para que o mundo não permaneça em crise sem mecanismo de reposição.
- [ ] Revisar as relações entre recursos e os sete relógios após preencher os registros locais.

## 11. Critério de conclusão

O registro estará pronto para uma simulação material inicial quando cada recurso crítico usado em aventuras tiver, ao menos nas localidades relevantes, unidade, estado de evidência, demanda ou função, reserva qualitativa ou quantitativa, dependências, prazo de reposição e alternativas conhecidas — ou quando a ausência de um desses dados estiver explicitamente marcada e impedir consequências que dependam dele.

**Este documento não declara a economia quantitativamente calibrada.** Ele estabelece o formato, os limites de inferência e a sequência de auditoria necessários para calibrá-la sem fabricar precisão.
