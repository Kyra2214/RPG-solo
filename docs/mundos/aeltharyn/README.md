# Aeltharyn — Crônicas do Primeiro Verde

**ID do mundo (proposto):** `world.aeltharyn`  
**Branch:** `world/aeltharyn-reino-das-florestas`  
**Escopo atual:** composição e auditoria editorial do ecossistema completo do Reino das Florestas.  
**Regra de produção:** não desenvolver o segundo reino até concluir e revisar o ecossistema completo de Aeltharyn.

## Documentos canônicos desta etapa

- [01 — Reino das Florestas](01-reino-das-florestas.md): geografia, ecologia, povos selecionados do catálogo existente, história, monarquia, dez cidades, assentamentos vinculados, rotas, facções, heróis, NPCs, leis, economia, crenças, mistérios, conflitos e consistência narrativa.
- [02 — Arquitetura narrativa do mundo persistente](02-arquitetura-mundo-persistente.md): simulação em segundo plano, consulta sob demanda, separação entre estado do mundo e conhecimento do Mestre/personagem, segredos, arquivos e continuidade entre campanhas.
- [03 — Assentamentos, jurisdições e rede política](03-assentamentos-e-rede-politica.md): vinte assentamentos com lideranças nomeadas, obrigações, interesses, segredos, tensões e relações entre as dez cidades.
- [04 — Instituições, justiça e pactos territoriais](04-instituicoes-justica-pactos.md): competências da Coroa, cidades e assentamentos; Conselho das Sete Copas; processos de arbitragem; tributos, sucessão, emergência e secessão.

## Identidade resumida

Aeltharyn, o Primeiro Verde, é uma monarquia élfica antiga construída em torno de pactos territoriais e proteção das nascentes. A capital é Elarion, Cidade das Sete Copas, governada pelo Rei Aerendyl Vaelorian. O reino é multicultural: ser de uma raça não determina cidadania, cultura ou lealdade. Cada cidade e assentamento conserva autoridades, interesses e autonomia pactuada próprios; aliança não significa harmonia automática nem obediência irrestrita à Coroa. A história oficial convive com registros locais, tradições orais e evidências contraditórias.

O cenário começa no ano 742 da Contagem das Seivas. Duas árvores-coração adoecem, as cidades de várzea exigem reparos nos diques, um navio chega a Thalanor sem tripulação, a mina de Torvelume permanece selada e o conselho debate reformas. Nenhum desses mistérios tem solução predefinida.

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
- [x] Compor dez cidades com perfis próprios.
- [x] Compor governo, facções, rotas, ecologia, história, lendas, mistérios e elenco de NPCs.
- [x] Definir agenda sazonal, relógios iniciais e dependências materiais entre as cidades.
- [x] Associar referências históricas, guardiões e benfeitores às cidades e aos povos selecionados.
- [x] Documentar o princípio de mundo persistente que evolui em segundo plano e é consultado sob demanda.
- [x] Estabelecer a primeira camada de vinte assentamentos e suas relações políticas com as dez cidades.
- [x] Dar identidade, liderança, obrigação, interesse e conflito inicial aos vinte assentamentos.
- [x] Documentar dependências e atritos políticos entre as dez cidades.
- [x] Definir o quadro institucional de pactos, jurisdição, justiça, tributos e sucessão em documento próprio.
- [ ] Auditar nomes, idades, cargos, genealogias, relações, conhecimento de NPCs e duplicações.
- [ ] Auditar demografia, jurisdições, rotas, tempos de viagem, recursos e dependências ecológicas.
- [x] Criar índice canônico ligando os vinte assentamentos aos IDs estáveis de suas lideranças, distinguindo o NPC já existente dos dezenove novos perfis editoriais.
- [ ] Cruzar os vinte assentamentos com os NPCs e as facções existentes, eliminando duplicações e fechando lacunas de jurisdição.
- [ ] Aprofundar os conflitos entre Coroa, cidades, assentamentos, facções e populações, sem tornar os grupos monolíticos.
- [ ] Conferir consistência dos povos/linhagens selecionados e dos IDs referenciados contra o catálogo editorial.
- [ ] Revisar mistérios, cronologia e consequências para garantir que fatos intencionais contraditórios sejam distinguidos de erros acidentais.
- [ ] Fechar a revisão editorial de Aeltharyn antes de iniciar as Coroas de Pedra.

**Importante:** o estado acima descreve conteúdo editorial escrito. Não afirma que o mundo já esteja implementado, seja carregável no APK ou tenha sido validado por testes automatizados.

## Regra de autonomia narrativa

O mundo continua avançando mesmo quando nenhum personagem visita um reino. O Mestre não precisa carregar ou pesquisar a lore de Aeltharyn numa campanha humana que nunca chega ao reino élfico. Se um elfo trouxer uma missão que leve o grupo até lá, o sistema consulta o estado atual do reino e entrega somente o contexto necessário; bibliotecas e arquivos permitem aprofundamento quando a investigação exigir. A verdade do mundo, os registros persistentes, o que cada NPC sabe, o contexto do Mestre e o conhecimento do personagem são camadas distintas.

A simulação pode avançar por ciclos e eventos relevantes; não precisa executar uma IA para cada habitante a cada segundo. Não consultar um reino não o congela, e consultá-lo não o recria do zero.

## Escopo técnico posterior

Carregamento, serialização, integração ao jogo, persistência em código e testes automatizados ficam fora dos critérios de conclusão editorial desta branch. A implementação técnica será planejada separadamente depois que os cinco reinos estiverem criados e revisados. A branch `main` não faz parte desta fase.
