# Aeltharyn — Crônicas do Primeiro Verde (reino 1 do mundo de Mythralis)

> Este documento agora faz parte do projeto independente **Mythralis** (ver `../../README.md`). O catálogo em Kotlin que existia na branch foi movido para `../../referencia-tecnica/` e **não** faz parte do conteúdo editorial.

**ID canônico do reino:** `kingdom.aeltharyn`  
**Origem:** branch `world/aeltharyn-reino-das-florestas` do repositório RPG-solo; agora vive neste projeto independente.  
**Escopo atual:** composição e auditoria editorial do ecossistema completo do Reino das Florestas.  
**Regra de produção:** não desenvolver o segundo reino até concluir e revisar o ecossistema completo de Aeltharyn.

## Documentos canônicos desta etapa

- [01 — Reino das Florestas](01-reino-das-florestas.md): geografia, ecologia, povos selecionados do catálogo existente, história, monarquia, dez cidades, assentamentos vinculados, rotas, facções, heróis, NPCs, leis, economia, crenças, mistérios, conflitos e consistência narrativa.
- [02 — Arquitetura narrativa do mundo persistente](02-arquitetura-mundo-persistente.md): simulação em segundo plano, consulta sob demanda, separação entre estado do mundo e conhecimento do Mestre/personagem, segredos, arquivos e continuidade entre campanhas.
- [03 — Assentamentos, jurisdições e rede política](03-assentamentos-e-rede-politica.md): vinte unidades comunitárias — bairros, distritos, vilas próximas, comunidades afastadas ou postos, conforme a geografia for validada — com lideranças, obrigações, interesses, segredos, tensões e vínculos políticos. A cidade-mãe não determina a posição espacial.
- [04 — Instituições, justiça e pactos territoriais](04-instituicoes-justica-pactos.md): competências da Coroa, cidades e assentamentos; Conselho das Sete Copas; processos de arbitragem; tributos, sucessão, emergência e secessão.
- [05 — Auditoria de demografia, rotas e recursos](05-auditoria-demografia-rotas-recursos.md): números populacionais provisórios, distinção entre vínculo político e posição espacial, cruzamento dos vinte assentamentos com os perfis das cidades, ambiguidades de locais e nomes, rotas e recursos.
- [06 — Modelo operacional e geração de aventuras](06-modelo-operacional-e-geracao-de-aventuras.md): cadeia causal de eventos, geração e resolução de oportunidades, autonomia dos agentes, consequências da inação, proteção contra contradições e contrato de contexto mínimo para LLM pequena.
- [07 — Registro operacional de rotas](07-registro-operacional-de-rotas.md): inventário das ligações com tempos explícitos, corredores sem duração validada, divergências de viagem e regras para não inventar itinerários.
- [08 — Registro operacional de recursos](08-registro-operacional-de-recursos.md): modelo para produção, reservas, demanda, reposição, perdas, substitutos e dependências materiais, com estados de evidência e sem números inventados.
- [09 — Matriz material dos assentamentos](09-matriz-material-dos-assentamentos.md): cruza os vinte núcleos comunitários com recursos, obrigações, dependências, riscos e lacunas, sem presumir que sejam todos vilarejos externos nem inventar estoques ou rotas.
- Doc 01, seção 35: calendário, estações e agenda sazonal dos relógios (M2.4).
- [07 — Mapa esquemático](07-mapa-esquematico.md): grafo das rotas e posição relativa sustentada pelo texto (M2.1).
- [10 — Matriz de povos nativos, cidades e forasteiros](10-matriz-povos-nativos-cidades-e-forasteiros.md): corrige a definição de Aeltharyn como reino, organiza onze povos nativos de referência, diferencia linhagens de raças-base, registra a estrutura correta de dez cidades no total e consolida uma proposta de distribuição transversal dos onze povos nativos.

- [12 — Registro mestre de entidades](12-registro-mestre.md): IDs de cidades, facções, assentamentos e pessoas, com a situação de cada ID entre os documentos e o catálogo técnico (M1.1 do refinamento). Há também `12-registro-mestre.csv`, `12-facoes.csv` e `12-assentamentos.csv`, `12-demografia.csv` (faixas de população, M2.2), `12-recursos.csv` (recursos centrais, M2.3); `12-fichas-elarion.md` e `.csv` (fichas completas de Elarion, M3.1), `12-competencias.csv` (raça, classe e profissão das 95 pessoas, M3.2), `12-relacoes.csv` e `12-conhecimento.csv` (M3.3), `12-voz.csv` (M3.4), `12-relogios.md` e `.csv` (M4.1), `12-eventos.md` e `.csv` (M4.2); as seções 8 e 9 trazem as 10 facções e os 20 assentamentos como dados (M1.3).
- [13 — Sementes de aventura](13-sementes-de-aventura.md): 90 sementes (3 por cidade e por assentamento), sem solução predefinida (M4.3).
- [13 — Reação do reino ao jogador](13-reacao-ao-jogador.md): fama por cidade e facção, procurado, favor, reparação e canais (M4.4).
- [13 — Mistérios com limites](13-misterios-limites.md): hipóteses abertas e o que a verdade não pode ser (M4.5).
- [14 — Resumos de contexto](14-resumos-de-contexto.md): 142 resumos curtos com ID e vizinhança, para o pacote de contexto da IA (M5.1).
- [14 — Camadas de informação](14-camadas-de-informacao.md): 66 fatos em público, local, restrito e secreto, com o caminho para o personagem descobrir cada um (M5.2).
- [15 — Cenas de teste](15-cenas-de-teste.md): 5 cenas (vilarejo élfico, porto de Thalanor, audiência na corte, arquivos, viagem por rota), cada uma com lista "entra" (até ~2.400 caracteres) e "não entra" (M5.3).
- [16 — Bíblia do reino](16-biblia-do-reino.md): síntese de todo o reino 1 em um documento, com 11 propostas de lore novo (P-01 a P-11) marcadas e aguardando aprovação do autor.

## Identidade resumida

Aeltharyn, o Primeiro Verde, é uma monarquia élfica antiga construída em torno de pactos territoriais e proteção das nascentes. A capital é Elarion, Cidade das Sete Copas, governada pelo Rei Aerendyl Vaelorian. O reino é multicultural: ser de uma raça não determina cidadania, cultura ou lealdade. Cada cidade e assentamento conserva autoridades, interesses e autonomia pactuada próprios; aliança não significa harmonia automática nem obediência irrestrita à Coroa. A história oficial convive com registros locais, tradições orais e evidências contraditórias.

O cenário começa no ano 742 da Contagem das Seivas. Duas árvores-coração adoecem, as cidades de várzea exigem reparos nos diques, um navio chega a Thalanor sem tripulação, a mina de Torvelume permanece selada e o conselho debate reformas. Nenhum desses mistérios tem solução predefinida.

## Fechamento editorial

O registro de decisões, correções cruzadas e limites remanescentes está em [11 — Fechamento das sete fases editoriais](11-fechamento-das-sete-fases.md). O fechamento não transforma estimativas narrativas em censo, não cria uma décima primeira cidade e não autoriza integração técnica.

## Ordem de expansão do mundo

1. **Aeltharyn — Reino das Florestas:** em composição e revisão.
2. **Coroas de Pedra — Montanhas:** bloqueado até a conclusão do primeiro reino.
3. **Véu dos Malditos:** bloqueado até a conclusão do reino das montanhas.
4. **Marcas das Feras:** bloqueado até a conclusão do reino dos Malditos.
5. **Pacto Humano:** último reino a ser desenvolvido.

Os quatro nomes futuros são rótulos provisórios, sem história ou geografia canônica detalhada nesta fase.

## Estado editorial do trabalho

- [x] Criar branch separada de `main`.
- [x] Definir a identidade do primeiro reino e os limites de escopo dos quatro reinos futuros.
- [x] Confirmar a estrutura de dez cidades no total: Elarion como capital élfica + nove outras cidades; ver [10 — Matriz de povos nativos, cidades e forasteiros](10-matriz-povos-nativos-cidades-e-forasteiros.md).
- [x] Compor governo, facções, rotas, ecologia, história, lendas, mistérios e elenco de NPCs.
- [x] Definir agenda sazonal, relógios iniciais e dependências materiais entre as cidades.
- [x] Associar referências históricas, guardiões e benfeitores às cidades e aos povos selecionados.
- [x] Documentar o princípio de mundo persistente que evolui em segundo plano e é consultado sob demanda.
- [x] Estabelecer a primeira camada de vinte assentamentos/unidades comunitárias e suas relações políticas com as dez cidades.
- [x] Dar identidade, liderança, obrigação, interesse e conflito inicial aos vinte assentamentos.
- [x] Documentar dependências e atritos políticos entre as dez cidades.
- [x] Definir o quadro institucional de pactos, jurisdição, justiça, tributos e sucessão em documento próprio.
- [x] Auditar nomes, idades, cargos, relações, conhecimento e duplicações de NPCs; divergências intencionais e identidades distintas estão registradas no fechamento editorial.
- [x] Auditar a classificação espacial, jurisdições, rotas, tempos, recursos e dependências ecológicas; dados populacionais permanecem apenas como escala narrativa aproximada, sem somar estimativas potencialmente sobrepostas.
- [x] Cruzar os sete relógios iniciais com os recursos, rotas, localidades afetadas e limites de conhecimento; manter explícito que o cruzamento não comprova causas nem avança relógios automaticamente.
- [x] Criar registro operacional de recursos com campos mínimos, estados de evidência, procedimento de escassez e pendências quantitativas explícitas.
- [x] Cruzar os vinte perfis de assentamento com os sistemas materiais explicitamente documentados e registrar dependências e lacunas sem inventar estoques.
- [x] Definir contrato editorial operacional para converter eventos e conflitos em oportunidades de aventura sem transformar hipóteses em fatos nem exigir que uma LLM pequena receba toda a lore em cada turno.
- [x] Criar índice canônico ligando os vinte assentamentos aos IDs estáveis de suas lideranças, distinguindo o NPC já existente dos dezenove novos perfis editoriais.
- [x] Sincronizar o quadro de assentamentos do documento principal com os vinte perfis detalhados e seus IDs canônicos.
- [x] Cruzar os vinte assentamentos com lideranças, instituições e facções; registrar competências, vínculos e limites de conhecimento sem duplicar pessoas.
- [x] Consolidar os conflitos entre Coroa, cidades, assentamentos, facções e populações, preservando dissenso interno e alternativas políticas.
- [x] Conferir a taxonomia editorial de povos nativos, linhagens e forasteiros; o abissal das enseadas permanece aliado/residente costeiro, não um 12º povo nativo de referência.
- [x] Revisar mistérios, cronologia e consequências, separando contradições intencionais dentro do mundo de erros editoriais acidentais.
- [x] Fechar as sete fases editoriais de Aeltharyn antes de iniciar as Coroas de Pedra; nenhuma integração técnica foi feita.

**Importante:** o estado acima descreve conteúdo editorial escrito. Não afirma que o mundo já esteja implementado, seja carregável no APK ou tenha sido validado por testes automatizados.

## Regra de autonomia narrativa

O mundo continua avançando mesmo quando nenhum personagem visita um reino. O Mestre não precisa carregar ou pesquisar a lore de Aeltharyn numa campanha humana que nunca chega ao reino élfico. Se um elfo trouxer uma missão que leve o grupo até lá, o sistema consulta o estado atual do reino e entrega somente o contexto necessário; bibliotecas e arquivos permitem aprofundamento quando a investigação exigir. A verdade do mundo, os registros persistentes, o que cada NPC sabe, o contexto do Mestre e o conhecimento do personagem são camadas distintas.

A simulação pode avançar por ciclos e eventos relevantes; não precisa executar uma IA para cada habitante a cada segundo. Não consultar um reino não o congela, e consultá-lo não o recria do zero.

## Escopo técnico posterior

Carregamento, serialização, integração ao jogo, persistência em código e testes automatizados ficam fora dos critérios de conclusão editorial desta branch. A implementação técnica será planejada separadamente depois que os cinco reinos estiverem criados e revisados. A branch `main` não faz parte desta fase.
