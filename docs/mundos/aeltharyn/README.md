# Aeltharyn — Crônicas do Primeiro Verde

**ID do mundo (proposto):** `world.aeltharyn`  
**Branch:** `world/aeltharyn-reino-das-florestas`  
**Escopo atual:** composição editorial integral do Reino das Florestas, primeiro reino e o mais antigo.  
**Regra de produção:** não desenvolver o segundo reino até concluir e revisar o ecossistema completo de Aeltharyn.

## Documento canônico desta etapa

- [01 — Reino das Florestas](01-reino-das-florestas.md): geografia, clima, ecologia, povos selecionados do catálogo existente, história, monarquia, dez cidades, rotas, facções, heróis, NPCs, leis, economia, crenças, mistérios, conflitos e regras de consistência narrativa.

## Identidade resumida

Aeltharyn, o Primeiro Verde, é uma monarquia élfica antiga construída em torno de pactos territoriais e proteção das nascentes. A capital é Elarion, Cidade das Sete Copas, governada pelo Rei Aerendyl Vaelorian. O reino é multicultural; ser de uma raça não determina cidadania, cultura ou lealdade. A história oficial da Coroa convive com registros locais, tradições orais e evidências contraditórias.

O cenário começa no ano 742 da Contagem das Seivas. Duas árvores-coração adoecem, as cidades de várzea exigem reparos nos diques, um navio chega a Thalanor sem tripulação, a mina de Torvelume permanece selada e o conselho debate reformas. Nenhum desses mistérios tem solução predefinida.

## Ordem de expansão do mundo

1. **Aeltharyn — Reino das Florestas:** em composição e revisão.
2. **Coroas de Pedra — Montanhas:** bloqueado até a conclusão do primeiro reino.
3. **Véu dos Malditos:** bloqueado até a conclusão do reino das montanhas.
4. **Marcas das Feras:** bloqueado até a conclusão do reino dos Malditos.
5. **Pacto Humano:** último reino a ser desenvolvido.

Os nomes dos reinos futuros são rótulos provisórios, sem história ou geografia canônica definida.

## Estado real do trabalho

- [x] Criar branch separada de `main`.
- [x] Definir nome provisório do mundo e identidade do primeiro reino.
- [x] Compor dez cidades com perfis próprios.
- [x] Compor governo, facções, rede de rotas, ecologia, história, lendas, mistérios e elenco de NPCs.
- [x] Associar figuras históricas a cada cidade e definir agenda sazonal e ritmos locais de funcionamento.
- [x] Expandir a memória para heróis históricos, guardiões vivos e benfeitores nas dez cidades.
- [x] Definir tríades de herói, guardião e benfeitor para 26 povos/subpovos selecionados como relevantes para Aeltharyn; IDs conferidos no catálogo atual.
- [ ] Revisar coerência entre personagens repetidos, cargos, cronologia e relações; fechar as lacunas de objetivos, agenda e conhecimento de cada NPC.
- [ ] Integrar os dados ao modelo de mundo existente e validar carregamento/persistência.
- [x] Conferir os IDs de raças e sub-raças citados contra o catálogo editorial atual (50 raças e 50 sub-raças; nenhum ID citado ausente na conferência inicial).
- [ ] Revisão de continuidade, demografia, distâncias e contradições internas.
- [ ] Conferir o formato de mundo existente no código e decidir como integrar sem duplicar modelos.
- [ ] Implementar/carregar os dados no jogo e validar persistência e isolamento entre modos de cânone.
- [ ] Atualizar o estado para concluído somente após as etapas acima.

**Importante:** “composto” aqui significa conteúdo narrativo escrito. Não significa que o mundo já seja carregável no APK ou que tenha sido validado por testes automatizados.


## Escopo do Reino das Florestas — Aeltharyn

Aeltharyn é um reino élfico, não um reino com as 50 raças do catálogo. O núcleo documentado reúne os elfos e povos/linhagens próximos (16 IDs selecionados, contando as linhagens élficas separadamente) por território, ecologia e história, além de um aliado costeiro; visitantes e migrantes não são tratados como originários. A documentação atual amplia as relações materiais entre as dez cidades, as cadeias de consequência ecológica e os relógios de campanha.

O reino permanece em implementação editorial e ainda não está integrado ao modelo carregável do jogo. O desenvolvimento das Coroas de Pedra está bloqueado até concluir auditoria de consistência, modelagem de dados, integração e testes de Aeltharyn.


O código agora contém o catálogo tipado inicial em `engine/AeltharynWorld.kt`, com dez cidades, povos próximos aos elfos, figuras locais, rotas e relógios validados por testes unitários escritos. O catálogo ainda não é carregado automaticamente na criação de campanha; essa integração permanece pendente.
