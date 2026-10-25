# Aeltharyn — Fechamento das sete fases editoriais

**Estado:** concluído para o escopo narrativo/editorial.  
**Branch:** `world/aeltharyn-reino-das-florestas`  
**Limites:** nenhum código, APK, serialização, teste automatizado, CI, PR ou integração ao `main` foi alterado ou executado.

## Resultado executivo

As sete fases editoriais estão concluídas em conjunto com os documentos de cidades, assentamentos, instituições, rotas, recursos, modelo de aventuras e povos. Aeltharyn está fechado como ecossistema narrativo utilizável: dez cidades, vinte unidades comunitárias, onze povos nativos de referência, autoridades com interesses e limites, conflitos institucionais, rotas principais reconciliadas e sete relógios iniciais ligados a consequências possíveis.

Concluído não significa inventar números para todas as lacunas. Distâncias locais, estoques e quantidades sem evidência continuam desconhecidos; estimativas de população não serão somadas para produzir um total do reino.

## Fase 1 — Hierarquia e identidade das dez cidades

**Concluída.**
- Aeltharyn é o reino, não o mundo; ID editorial canônico: `kingdom.aeltharyn`.
- Exatamente dez cidades: Elarion (capital), Nymvar, Thalanor, Vael-Tir, Orvass, Caladwen, Brumafunda, Arven, Silvaran e Torvelume.
- Não foi criada uma décima primeira cidade nem restaurada Ithilvar.
- A Coroa é élfica, mas o reino é multicultural; povo, linhagem, cidadania, local de nascimento e lealdade são dimensões distintas.
- As dependências entre cidades fazem parte do cenário; nenhuma cidade funciona como cenário isolado.

## Fase 2 — Vinte assentamentos e posição territorial

**Concluída em escala qualitativa.**
- Os vinte registros mantêm IDs, lideranças, obrigações, interesses, tensões e vínculos políticos.
- A matriz espacial em `03-assentamentos-e-rede-politica.md` consolida núcleos urbanos/periurbanos, comunidades de entorno, agrupamento insular e postos exteriores/sazonais. A classificação não cria coordenadas nem tempos de viagem.
- **Farol da Folha Azul** é o farol urbano de Thalanor; **Farol de Âmbar** é um posto costeiro separado, com residentes.
- **Torre sem Sino** é um marco local de Brumafunda; **Torre da Névoa Mansa** é um posto exterior com comunidade residente.
- **Casa da Hospitalidade** é uma instituição de Silvaran; **Fonte do Regresso** é uma comunidade residencial de abrigo vinculada à cidade.
- Não há tempos de acesso local inventados. Uma aventura que dependa de prazo exato precisa estabelecer a rota antes de tratar o prazo como fato.

## Fase 3 — NPCs, identidades e conhecimento

**Concluída para a continuidade dos personagens centrais e das vinte lideranças.**
- Os vinte líderes de assentamento mantêm IDs editoriais próprios; Beren Três-Canais é a mesma pessoa citada anteriormente, não um segundo NPC.
- **Hadrik das Sete Bigornas** é um anão de Torvelume, morto há cerca de 180 anos. **Mestre Odran Sete-Bigornas** é outra pessoa, também anão e falecido. “Sete-Bigornas” designa uma tradição/oficina transmitida entre gerações, não uma identidade ou parentesco automaticamente compartilhado.
- **Tirven Eixo-Claro** (`npc.tirven-eixo-claro`) é o herói histórico da tríade dos Gnomos dos Vales; Hadrik não é classificado como gnomo.
- **Parel Três-Parafusos** é Gnomo dos Vales; “das engrenagens” descreve seu ofício, não uma raça.
- **Kheva Ouvido-de-Névoa** pertence ao povo Corvário das Torres; “dos Telhados” é epíteto de ofício, não linhagem separada.
- **Princesa Maereth Vaelorian** e **Maereth, a Cartógrafa das Copas** são pessoas diferentes, de épocas separadas por séculos; a cartógrafa tem ID `npc.maereth-cartografa-das-copas`.
- **Dara Guarda-da-Rota** (`npc.dara-guarda-da-rota`) e **Dara Passo-Rítmico** (`npc.dara-passo-ritmico`) são pessoas distintas.
- **Iria dos Ecos** é a mesma guardiã viva citada no perfil de Brumafunda e na tríade feérica; ID `npc.iria-dos-ecos`.
- Parel, Hadrik e as identidades que geravam ambiguidade receberam IDs editoriais estáveis onde necessário.
- Conhecimento de NPC depende de fontes, posição, mensageiros e experiência; rumor ou interpretação não vira fato automaticamente.

**Limite:** a equivalência de todos os IDs editoriais com o catálogo de personagens em código será validada na etapa técnica.

## Fase 4 — Relações políticas e sociais

**Concluída.**
- Competências da Coroa, cidades e assentamentos estão consolidadas em `04-instituicoes-justica-pactos.md`.
- Tributos, contribuições extraordinárias, jurisdição, sucessão, emergência, arquivos e secessão têm procedimentos e limites narrativos.
- Cada conflito é explicado por interesses, obrigações, recursos, evidências, conhecimento e alternativas; não basta declarar que duas partes estão em conflito.
- Grupos e instituições não são monolíticos. Ações emergenciais precisam de fundamento, alcance, registro, revisão e condição de encerramento.
- Recusa de uma cidade ou assentamento não produz guerra automaticamente.

## Fase 5 — História, economia, leis, defesa e cronologia

**Concluída editorialmente, sem calibragem econômica numérica.**
- A cronologia permanece ancorada no ano 742 da Contagem das Seivas. Datas antigas baseadas em crônicas ou testemunhos continuam aproximadas quando as fontes divergem.
- História oficial e memória local podem discordar; a divergência é intencional quando suas fontes e interesses estão identificados.
- Alimentos, água, sementes, madeira, resina, remédios e ferramentas foram cruzados com cidades, assentamentos e rotas nos documentos 08 e 09.
- Estoques, produção, consumo e reposição não recebem números sem fonte, unidade e período. População não é convertida automaticamente em consumo.
- Defesa, corredores migratórios e medidas de emergência foram relacionados a direitos civis e custos políticos.
- A sobreposição demográfica entre cidades e assentamentos continua desconhecida; não será calculado total agregado do reino.

## Fase 6 — Povos, linhagens, cidades e forasteiros

**Concluída editorialmente.**

Os onze povos nativos de referência são: elfos; sátiros/silvanos festivos; feéricos; verdejantes; micélioides; centauros das florestas; felídeos das selvas; orcanos das matas; corvídeos; pequeninos; gnomos dos vales.

- Linhagens élficas e regionais não aumentam automaticamente a contagem de povos.
- O Abissal das Enseadas é aliado costeiro/residente de origem externa, não um décimo segundo povo nativo.
- A matriz de cidades indica centros culturais, não exclusividade racial, maioria demográfica ou profissão obrigatória.
- Residentes externos podem formar famílias, obter cidadania e nascer em Aeltharyn sem alterar automaticamente a origem histórica de seu povo.
- Os outros quatro reinos permanecem fora do escopo.

## Fase 7 — Mistérios, relógios e oportunidades de aventura

**Concluída como estrutura narrativa; soluções dos mistérios permanecem abertas.**

Os sete relógios são estados e possibilidades causais, não cronômetros que avançam sozinhos. Antes de uma mudança, verificar gatilho observável, ligação causal, localidade afetada, recursos/rotas, alternativas, conhecimento de cada agente e próxima decisão.

- **Árvores-coração:** efeitos ecológicos graduais; doença não prova sabotagem.
- **Diques e tributo do sul:** materiais e contas devem ser examinados antes de atribuir fraude ou inundação inevitável.
- **Navio sem tripulação:** relatos de Thalanor, Farol de Âmbar e Ponta da Concha são observações independentes; atraso comercial não prova causalidade.
- **Mina selada:** fissuras, vibrações, relatórios e cadeia de custódia precedem conclusões.
- **Mapa que muda / oitava cidade:** versões de mapas e testemunhos são evidências de confiabilidade distinta; a oitava cidade não entra na contagem canônica.
- **Corredores centauros:** direito de passagem, largura, estação e acesso material precisam ser avaliados antes de abrir/fechar trilhas.
- **Resina ilegal:** marcas, cotas, água turva e relatos geram hipóteses concorrentes, não culpa automática.

O protagonista pode ignorar, resolver, agravar ou testemunhar os eventos. NPCs podem agir fora de cena apenas se dispuserem de conhecimento, recursos, autoridade e oportunidade plausíveis. Nenhum mistério tem solução obrigatória pré-escrita.

## Decisões consolidadas de rotas

1. **Elarion–Thalanor:** viagem direta pelo Lúmen em cerca de 3 dias para embarcação leve/rápida; rota de carga com escala Elarion–Nymvar (2 dias) e Nymvar–Thalanor (3–4 dias), total de 5–6 dias. São serviços diferentes.
2. **Nymvar–Orvass:** cerca de 3 dias em condições comuns; 2–6 dias quando cheia, desvios, canais mutáveis ou guia alterarem o corredor do Pântano.
3. **Estrada da Espinha:** Caladwen–Torvelume cerca de 3 dias; Torvelume–Vael-Tir cerca de 1–2 dias; percurso completo cerca de 4–5 dias em condições comuns, sem paradas longas.
4. Dias representam tempo decorrido em condições comuns, incluindo pausas usuais e excluindo paradas longas. Clima, carga, guia e bloqueios podem alongar ou impedir a viagem.
5. **Orvass–Brumafunda, Nymvar–Thalanor e Arven–Silvaran** continuam corredores sem duração individual validada; não sustentam prazos exatos.
6. Não há distância/tempo canônico para cada assentamento a partir de sua cidade-mãe. Essa lacuna é explícita e não autoriza a IA a inventar números.

## Critérios finais de uso

- Manter exatamente dez cidades e vinte registros comunitários.
- Não somar populações dos assentamentos aos totais urbanos nem calcular população total do reino.
- Não transformar hipótese, rumor ou tradição em conhecimento universal de NPC.
- Não avançar relógios sem gatilho e consequência plausível.
- Não criar estoque, rota, instalação, povo nativo ou identidade adicional para resolver uma cena.
- Preservar Aeltharyn como foco até a etapa seguinte.
- Não alterar código, APK, `main`, serialização, testes ou CI como parte do fechamento editorial.

**Conclusão:** as sete fases narrativas/editoriais estão concluídas. Os desconhecidos restantes são limites explícitos de evidência (mapa métrico, acessos locais, quantidades materiais e equivalência com catálogo técnico), não pendências que exijam inventar fatos ou impeçam aventuras que não dependam desses números.
