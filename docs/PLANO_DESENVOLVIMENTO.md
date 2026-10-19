# PLANO_DESENVOLVIMENTO.md — RPG Solo com Mestre de IA (Android, sem servidor)

Princípio: **tudo roda e fica salvo no aparelho.** A única coisa que sai do celular é a chamada à API de IA, com a chave do próprio usuário.

---

## 0. Decisões de arquitetura

| Tema | Decisão |
|---|---|
| Plataforma | Android nativo, Kotlin + Jetpack Compose |
| Banco | SQLite via Room, um banco só |
| Regras dos sistemas | Arquivos de dados (JSON/YAML) lidos pelo motor, não código por sistema |
| IA | Chamada HTTPS direta do app, provedor trocável, chave do usuário (BYO key) |
| Chave de API | Guardada no aparelho (EncryptedSharedPreferences). Nunca no APK |
| Backup | Exportar/importar arquivo `.zip` local (sem nuvem obrigatória) |
| Offline | Tudo funciona offline, menos a narração da IA |

**Segredos de NPC:** não existe tela para ver ficha de NPC. O jogador só vê o que o personagem descobriu. Os segredos ficam no mesmo banco, marcados como privados, e só vão para a IA quando a cena precisa deles.

---

## Princípio central: ficha primeiro, ambientação adaptável

O app é guiado pela **ficha** e pelo **sistema de regras**, não por um cenário fixo. O jogador cria o personagem e o coloca em qualquer cenário. A IA adapta história, NPCs, organizações, conflitos, equipamentos ficcionais e missões à combinação pedida.

**Cinco camadas separadas (no código e no banco):**

| Camada | Define | Pode ser trocada sem refazer as outras? |
|---|---|---|
| Ficha | Identidade mecânica e capacidades do personagem | sim, é reutilizável |
| Sistema + edição | Testes, combate, recursos, progressão | não muda dentro da campanha |
| Ambientação | Universo ficcional (cyberpunk, Japão medieval...) | **sim, sem nova ficha** |
| Regras adicionais | Adaptações explícitas para o que o sistema não cobre | por campanha |
| Campanha | Eventos, inventário, relações, ferimentos, missões | é o estado da aventura |

**Regras de ouro:**
- Não existe lista fechada de raça, profissão ou cenário. Combinações incomuns são resolvidas pela IA com criatividade narrativa e, se preciso, adaptação mecânica explícita.
- Nunca misturar sistemas em silêncio e nunca apresentar regra inventada como oficial.
- Profissão, raça e especialidade são **conceitos de personagem**: o app verifica se exigem vantagens, perícias, poderes ou regras adicionais, sem trocar a estrutura da ficha.

**Exemplo de teste obrigatório** (o sistema é só ilustrativo: o mesmo roteiro vale para qualquer sistema instalado): "Quero interpretar um anão minerador especializado em máquinas de combate, num cenário cyberpunk, usando [SISTEMA]." Ex.: 3D&T, D&D, Vampire, Cepheus ou outro.
1. Identifica o sistema e a **edição/variante exata** (muitos sistemas têm várias edições e estilos de jogo).
2. Cria ou valida a ficha pelas regras dessa edição.
3. Preserva atributos, recursos, vantagens, desvantagens e demais elementos do sistema.
4. Trata "minerador" e "máquinas de combate" como conceitos e checa se pedem vantagens, perícias ou poderes.
5. Adapta o cenário para cyberpunk sem trocar a ficha por outra estrutura.
6. Cria história coerente com o personagem.
7. Se uma capacidade não existir nas regras, propõe uma **regra adicional identificada**, explica os efeitos e pede aprovação quando alterar muito a ficha ou o equilíbrio.
8. Valida as operações pelo motor e salva o estado.

---

## 1. O que reaproveitar da auditoria

| Origem | Licença | Como usar |
|---|---|---|
| Andromeda | MIT | Reimplementar em Kotlin: funil de comandos, log de eventos append-only, RNG com seed, interface de ruleset, "IA só muda estado via ferramentas". Manter aviso de copyright se copiar trechos |
| Straightjacket | AGPL-3.0 | **Só ideias, sem copiar código:** memória e ativação de NPC, relógios (clocks), estrutura de capítulos, correção de cena, modelo barato para classificar intenção |
| Grand Adventure Engine | Apache-2.0 | Ideia do `NarratorContext` (código monta o contexto da cena). Código C#, só referência |
| EdgeTales | sem LICENSE | Não usar |

---

## FASE 1 — Fundação

**Objetivo:** projeto Android rodando com a estrutura de módulos e testes automáticos.

### Módulo 1.1 — Projeto base
- 1.1.1 Estrutura multi-módulo Gradle (`app`, `engine`, `data`, `ai`, `rules`)
- 1.1.2 Injeção de dependência (Hilt ou Koin)
- 1.1.3 Navegação com 6 abas
- 1.1.4 Tema, modo claro/escuro

### Módulo 1.2 — Qualidade
- 1.2.1 Testes unitários do `engine` rodando sem Android (Kotlin puro)
- 1.2.2 CI local (lint + testes)
- 1.2.3 Convenção de logs sem dados sensíveis

**Pronto quando:** app abre, 6 abas vazias, `./gradlew test` passa.

---

## FASE 2 — Motor de regras (engine)

**Objetivo:** o código decide a mecânica. A IA nunca decide.

### Módulo 2.1 — Núcleo determinístico
- 2.1.1 Dados e rolagens (`2d6`, `3d6+2`, vantagem, explosão) com seed gravada
- 2.1.2 Funil de comandos: toda mudança de estado passa por um `Command` validado
- 2.1.3 Log de eventos append-only (o estado é a soma dos eventos)
- 2.1.4 Transações com rollback (resposta inválida não altera nada)
- 2.1.5 Testes: mesma seed gera o mesmo resultado

### Módulo 2.2 — Estado do jogo
- 2.2.1 Modelos: personagem, NPC, item, missão, local, evento
- 2.2.1b Modelos separados por camada: `Ficha`, `Sistema/Edição`, `Ambientação`, `RegraAdicional`, `Campanha` (a ambientação referencia a campanha, não a ficha)
- 2.2.2 Estado canônico (verdade da campanha)
- 2.2.3 Estado de conhecimento do jogador (o que ele descobriu)
- 2.2.4 Validador de contradições (item consumido, NPC morto, promessa registrada)

### Módulo 2.3 — Resolução mecânica
- 2.3.1 Testes de perícia/atributo e dificuldade
- 2.3.2 Combate: iniciativa, ataque, dano, cura, condições
- 2.3.3 Recursos: vida, mana, munição, dinheiro
- 2.3.4 Experiência e progressão
- 2.3.5 Resultados: falha, sucesso parcial, sucesso, crítico (só quando o sistema permite)

**Status do código na branch `implementation/code-first-combat` (19/10/2026):** o motor e o app ligam iniciativa, ataques, dano, equipamento, bônus de traços/adaptações, condições com duração e dano por rodada, fuga, defesa, XP e encerramento de combate. A quantidade de dados de ataque continua definida pelo pacote; condições alteram o bônus/limiar de acordo com a comparação. Testes por pontos agora diferenciam 0 de 1 ponto: zero mantém uma face mínima e cada ponto positivo acrescenta uma face até o teto do dado. Resistência/vulnerabilidade a dano também é um efeito tipado de combate. A restauração de backup valida o estado reconstruído. A revisão estática também corrigiu o consumo de poção quando o recurso já está cheio e ligou efeitos numéricos explícitos de alguns traços. **Não executei builds/testes nesta etapa**, conforme pedido. Ainda há mecânicas fora do fluxo principal: rolagens opostas/em grupo, vantagem/desvantagem, pontos de sorte, recompensas de evolução escolhidas pelo jogador e magia tipada. Efeitos condicionais de traços não devem ser tratados como automáticos sem um modelo de gatilhos.

**Pronto quando:** uma luta completa e uma sequência de testes rodam só com código, sem IA, e os testes provam as contradições bloqueadas.

---

## FASE 3 — Persistência local

**Status do código (18/10/2026):** campanhas, eventos, fichas e metadados usam Room com migrações v1–v3; autosave e recuperação estão ligados ao app. Backup/restauração já têm fluxo Android por seletor de arquivo e validação do estado reconstituído. Capítulos, missões e linha do tempo persistem em metadados da campanha, não em tabelas dedicadas. Pendente: teste instrumentado de migração Room e validação em aparelho/emulador; não executei essas verificações nesta etapa.

### Módulo 3.1 — Banco
- 3.1.1 Tabelas: fichas, NPCs (com campo `privado`), eventos, capítulos, resumos, descobertas do jogador
- 3.1.2 Migrações versionadas

### Módulo 3.2 — Salvamento
- 3.2.1 Autosave a cada evento confirmado
- 3.2.2 Recuperação após fechar o app no meio de uma jogada

**Pronto quando:** fecha e reabre o app sem perder estado.

---

## FASE 4 — Pacotes de regras

**Objetivo:** cada sistema é um pacote de dados. Adicionar sistema = adicionar pasta, sem recompilar.

### Módulo 4.1 — Formato do pacote
- 4.1.1 `manifest` (sistema, edição, versão, origem, licença)
- 4.1.2 Esquema de atributos, perícias, vantagens, desvantagens, poderes
- 4.1.3 Tabelas de testes, dificuldades, dano, condições
- 4.1.4 Regras de criação e validação de ficha
- 4.1.5 Progressão e recompensas
- 4.1.6 Marcação de regras ambíguas ("incerto", nunca inventar como oficial)
- 4.1.7 Tutorial e exemplos dentro do pacote

### Módulo 4.2 — Carregador
- 4.2.1 Leitura e validação do pacote (falha clara se estiver inválido)
- 4.2.2 Importar pacote por arquivo
- 4.2.3 Busca por trecho de regra (só o necessário vai para a IA)

### Módulo 4.3 — Primeiros sistemas
- 4.3.1 Pacote **de teste** simples e próprio (valida o formato)
- 4.3.2 **Primeiro sistema real: escolhido pelo jogador** (o motor não pode ter nada específico de um sistema). **Escolhido: 3D&T.** Bloqueio de licença: a Licença Aberta da Jambô v1.0 não permite aplicativos (ver `docs/catalogo/LICENCAS_E_FONTES_RPG.md`); o pacote vai por importação do usuário (pasta `packages-import/`, fora do repositório) ou com autorização da Jambô. Edição a confirmar (Victory é a única coberta pela licença)
- 4.3.3 Cada edição/variante/estilo de um sistema é um pacote próprio, com herança opcional de um pacote-base (ex.: edição nova reaproveita o que não mudou)
- 4.3.4 Demais sistemas depois, conforme licença

### Módulo 4.4 — Regras adicionais (adaptações)
- 4.4.1 Modelo de `RegraAdicional`: nome, descrição, efeitos mecânicos, custo/limite, origem ("adaptação", nunca "oficial")
- 4.4.2 A IA propõe a adaptação, o motor mostra os efeitos em números
- 4.4.3 Aprovação do jogador obrigatória quando alterar muito a ficha ou o equilíbrio
- 4.4.4 Regras adicionais ficam guardadas por campanha e marcadas na ficha com um selo
- 4.4.5 Nunca importar mecânica de outro sistema sem declarar a adaptação

**Status do código (19/10/2026):** o formato/validador de pacotes, herança, pacote próprio, importação ZIP pela interface Android e restauração dos pacotes importados estão ligados ao app. A importação limita arquivos a 20 MiB e tenta resolver dependências em ordem. Adaptações podem ser propostas, recusadas ou aprovadas; os efeitos aprovados entram nos testes/combate. Alguns bônus e penalidades explícitos dos traços do pacote próprio também foram conectados aos testes ou ao combate, incluindo resistência/vulnerabilidade a dano antes de aplicar o dano final. Isso **não** significa que todas as descrições dos traços (especialmente efeitos condicionais, usos por cena ou imunidades) sejam mecânicas. 3D&T continua apenas como link de consulta por decisão de licença; PDF foi descartado. Pendente: revisar catálogo/conteúdo de cada sistema e validar importação real em aparelho.

**Atenção legal:** a maioria dos sistemas comerciais (3D&T, D&D, Vampire etc.) tem direitos autorais. O pacote deve conter mecânicas escritas por nós e/ou conteúdo que o usuário importe do próprio material. Não embutir texto oficial no app sem licença.

**Pronto quando:** o pacote de teste cria ficha, roda teste e combate pelo motor; trocar de pacote não mistura regras.

---

## FASE 4A — Catálogo de estilos, sistemas e configuração de campanha

**Origem:** documento "Catálogo de Estilos, Sistemas e Criação de Personagens". Detalhes em `docs/catalogo/`.
**Status:** núcleo implementado e testado (carregador, validação, compatibilidade, configuração). Pesquisa dos sistemas: só D&D, parcial.

### Módulo 4A.1 — Catálogo de estilos (feito)
- 4A.1.1 Seis dimensões: ambientação, estrutura de campanha, filosofia/resolução/cena, tom, dificuldade, progressão (153 entradas, IDs únicos)
- 4A.1.2 Arquivos de texto editáveis; entrada nova = linha nova
- 4A.1.3 Gêneros novos e misturas aceitos sem mudar código

### Módulo 4A.2 — Catálogo de sistemas (parcial)
- 4A.2.1 Sistema > família > edição (3D&T e Tormenta20 são famílias separadas)
- 4A.2.2 Estado de pesquisa e de licença por edição, com nota de evidência
- 4A.2.3 Pesquisa de criação de personagem por sistema (feito só para D&D)
- 4A.2.4 Esquema de ficha por sistema/edição (pendente, vem com o pacote de regras, Fase 4)

### Módulo 4A.3 — Compatibilidade (feito)
- 4A.3.1 Quatro estados: nativa, adaptada, não verificada, bloqueada
- 4A.3.2 Sem regra registrada = não verificada, e o jogador vê avisos antes de começar
- 4A.3.3 Combinação bloqueada impede iniciar a campanha

### Módulo 4A.4 — Configuração de campanha (feito, sem IA)
- 4A.4.1 Edição ambígua gera pergunta ao jogador
- 4A.4.2 Dificuldade fechada: 4 parâmetros documentados, nunca altera atributos de NPC
- 4A.4.3 Limites de conteúdo separados de gênero, dificuldade e regras
- 4A.4.4 Descrição livre vira configuração (`CampaignInterpreter`, escrito, não compilado)

### Módulo 4A.5 — Licenças e fontes
- 4A.5.1 ~~Só conteúdo com licença verificada vai embutido~~ **Cancelado**: nada de terceiros é embutido; só links de consulta (Fase 4B)
- 4A.5.2 Basic Rules do D&D Beyond **não** são CC: não embutir
- 4A.5.3 Conteúdo sem licença confirmada só por importação do usuário
- 4A.5.4 Atribuição exibida na aba Biblioteca de sistemas
- 4A.5.5 Leitura das fontes pendentes (lista em `LICENCAS_E_FONTES_RPG.md`)

**Pronto quando:** o catálogo carrega sem erro, o exemplo "anão minerador + cyberpunk" gera pergunta de edição e avisos, e o primeiro sistema real tem pesquisa e licença verificadas.

---

## FASE 4B — Links de consulta (simplificada)

**Decisão (2026-10-17):** o sistema é **próprio**; 3DeT e D&D entram **apenas como link de consulta** na criação de personagem. O app só abre o link no navegador: **não copia, não guarda, não importa nem indexa** nenhum material. Por isso **não há trabalho de licença** (4A.5) nem **importação de PDF** (4B.2, 4B.3 e o extrator).

### Módulo 4B.1 — Biblioteca de links (único escopo restante)
- 4B.1.1 `fontes.txt`: id, sistema, título, tipo, url, idioma, verificado (SIM/NAO) — feito (só 3DeT e D&D)
- 4B.1.2 Links aparecem na aba Sistemas e no fim da aba Heróis (criação de personagem) — feito
- 4B.1.3 Links não verificados ficam marcados na tela

**Cancelado:** 4B.2 (inserir PDF do usuário), 4B.3 (regras dos PDFs) e a consulta a PDF em `RuleLookupChain`. O código de `DocumentIndex`/`PdfTextExtractor` continua no módulo `rules` sem uso no app e pode ser removido.

---

## FASE 5 — Personagens e fichas

### Módulo 5.0 — Aba de criação (modelos e fichas)
- 5.0.1 Lista de **modelos** (sistemas instalados) com as **fichas prontas** de cada um — `SystemModels` (núcleo feito)
- 5.0.2 Rascunho guiado pelo pacote, sem PDF: atributos, traços permitidos agora, motivo de bloqueio, pontos restantes — `CharacterDraft` (núcleo feito)
- 5.0.3 PDF do usuário é opcional: se existir, entra como dica extra (`hints`); se não, nada muda
- 5.0.4 Se o sistema escolhido não tem pacote, o usuário pode criar a ficha livre, marcada "não validada pelo motor"

### Módulo 5.1 — Ficha-base
- 5.1.1 Criação guiada pelo pacote de regras
- 5.1.2 Validação automática (pontos, requisitos, limites)
- 5.1.3 Assistente de IA para explicar opções (opcional, depois da Fase 6)
- 5.1.4 Aprovação do jogador antes de iniciar campanha
- 5.1.5 Análise de conceito: raça, profissão e especialidade viram sugestões de vantagens, perícias, poderes ou regra adicional (4.4), sem trocar a estrutura da ficha
- 5.1.6 Entrada livre do conceito ("anão minerador de máquinas de combate"), sem lista fechada

### Módulo 5.2 — Biblioteca
- 5.2.1 Lista, busca, duplicar, arquivar
- 5.2.2 Histórico de evolução

### Módulo 5.3 — Uso em campanhas
- 5.3.1 Instância da ficha por campanha
- 5.3.2 Modos de progressão: compartilhada, independente, escolhida por campanha
- 5.3.3 Regra: campanha nunca altera outra sem comando explícito do jogador
- 5.3.4 Teste: a mudança numa campanha não vaza para outra

---

## FASE 6 — Camada de IA

### Módulo 6.1 — Provedores
- 6.1.1 Interface `AiProvider` (envio, streaming, contagem de tokens)
- 6.1.2 Primeiro provedor com plano gratuito (definir após testar limites reais)
- 6.1.3 Segundo provedor, para provar que a troca funciona
- 6.1.4 Endpoint e modelo configuráveis
- 6.1.5 Chave guardada no aparelho, mascarada na tela

### Módulo 6.2 — Robustez
- 6.2.1 Limite de requisições e fila
- 6.2.2 Retentativas com espera crescente
- 6.2.3 Erros claros: sem internet, chave inválida, limite excedido
- 6.2.4 Estimativa de tokens/custo por jogada
- 6.2.5 Resposta inválida nunca altera o estado (usa o rollback da Fase 2)

### Módulo 6.3 — Saída estruturada
- 6.3.1 A IA devolve: narrativa + lista de **propostas** de mudança
- 6.3.2 O motor valida cada proposta e aplica ou recusa
- 6.3.3 Esquema JSON validado, uma nova tentativa se vier fora do formato

### Módulo 6.4 — Economia de IA
- 6.4.1 Modelo barato para classificar intenção e resumir
- 6.4.2 Modelo melhor só para narrativa importante
- 6.4.3 Ações simples resolvidas sem IA (ver ficha, inventário, descansar)
- 6.4.4 Cache do bloco fixo (regras + cenário), quando o provedor suportar

**Status do código (19/10/2026):** os provedores compatíveis com OpenAI e Anthropic, configuração de endpoint/modelo, chave protegida pelo Android Keystore, teste de conexão, estimativa de tokens, classificação barata, roteamento de modelo e ações locais estão ligados ao app. O streaming SSE foi ligado no transporte, nos provedores, no loop de jogada e na tela, extraindo progressivamente apenas o campo `narrative` do JSON. A resposta estruturada continua validada e propostas recusadas sofrem rollback. **Cache de prompt fixo permanece pendente**, pois precisa de implementação específica por provedor e não deve ser simulado como cache real. A escolha de um provedor gratuito padrão depende de confirmar limites atuais; não foi inventado um preset sem validação. O streaming ainda não foi compilado nem validado em rede real.

---

## FASE 7 — Mestre e loop de jogo

### Módulo 7.1 — Ciclo da jogada
1. 7.1.1 Jogador escreve livremente
2. 7.1.2 Classificador interpreta a intenção
3. 7.1.3 Motor escolhe a regra e rola
4. 7.1.4 Montador de contexto envia à IA só o necessário
5. 7.1.5 IA narra o resultado já decidido
6. 7.1.6 Motor valida propostas, grava eventos, atualiza o que o jogador sabe

### Módulo 7.2 — Montador de contexto
- 7.2.1 Local, personagens presentes, missões ativas, últimos eventos
- 7.2.2 Trecho de regra relevante
- 7.2.3 Resumo do capítulo
- 7.2.4 Orçamento de tokens por requisição

### Módulo 7.3 — Regras do mestre
- 7.3.1 Prompt fixo: a IA não pode contradizer o motor nem fatos registrados
- 7.3.2 O motor diz o resultado; a IA só o descreve
- 7.3.3 Sinalizar incerteza quando a regra não existir
- 7.3.4 Sugestões de ação (opcionais, nunca obrigatórias)
- 7.3.5 Imersão: a narração nunca mostra ficha, atributos, vida em números ou segredos de NPC. Descreve só o que o personagem percebe (ex.: "parece ferido", "parece perigoso")

### Módulo 7.4 — Correção
- 7.4.1 Desfazer a última jogada (reverte eventos)
- 7.4.2 Pedir nova narração do mesmo resultado

**Pronto quando:** uma sessão de 20 jogadas roda com o pacote de teste, e nenhuma resposta da IA consegue mudar um resultado de dado.

**Status do código (19/10/2026):** o loop de jogo monta contexto da campanha, usa opções de teste do pacote ativo, envia resumo de capítulo e regras relevantes, valida propostas e desfaz alterações em caso de falha. O prompt proíbe a IA de decidir dados, dano ou segredos não liberados; há sugestões opcionais e controles de desfazer/refazer. O streaming progressivo do campo de narração está conectado ao fluxo da interface, mas continua pendente de validação em execução. Cache de prompt fixo não está implementado.

---

## FASE 8 — NPCs e conhecimento

### Módulo 8.0 — NPC criado pela IA a partir do conceito
- 8.0.1 O modelo do sistema é um guia: a IA só o segue se for criar o NPC (ex.: "vilão arqueiro élfico"); ela precisa saber as vantagens e desvantagens dele
- 8.0.2 Consulta em cadeia: pacote → PDFs do usuário (se houver) → pesquisa na rede feita pela IA — `RuleLookupChain` (núcleo feito; a pesquisa na rede entra na Fase 6)
- 8.0.3 A IA devolve uma proposta; o motor valida pelo pacote e recusa com erros claros para ela tentar de novo — `NpcFactory` (núcleo feito)
- 8.0.4 A ficha do NPC é privada: a IA **não precisa narrar** vantagens e desvantagens, apenas usá-las no combate; o jogador vê só identidade (`NpcSheet.playerView`)
- 8.0.5 Sem pacote para o sistema: NPC aceito, mas marcado "não validado pelo motor"

### Módulo 8.1 — Fichas de NPC (privadas)
- 8.1.1 Identidade, atributos, equipamento, estado
- 8.1.2 Personalidade, objetivos, motivações, relações, rivalidades
- 8.1.3 Segredos, informações falsas, conhecimento próprio
- 8.1.4 Criação sob demanda e modelos por sistema (tabelas, sem IA para NPC genérico)

### Módulo 8.2 — Continuidade
- 8.2.1 Memória do NPC: o que ele viu e prometeu
- 8.2.2 Relações com o jogador e entre NPCs
- 8.2.3 Agenda: NPCs e ameaças avançam com o tempo (ideia dos relógios)

### Módulo 8.3 — Conhecimento do jogador
- 8.3.1 Registro do que o personagem sabe, com a fonte (pista, teste, conversa)
- 8.3.2 Diário automático só com o descoberto
- 8.3.3 Informação falsa guardada como "acreditado", não como verdade

### Módulo 8.4 — Revelar informação por habilidade
- 8.4.1 Verificar se o personagem tem a habilidade
- 8.4.2 Teste definido pelo pacote de regras, rolado pelo motor
- 8.4.3 O resultado libera níveis de informação (estimativa, condição, detalhe)
- 8.4.4 A IA narra só o que o motor liberou

### Módulo 8.5 — Contexto enxuto
- 8.5.1 Segredo de NPC só vai para a IA se a cena exigir
- 8.5.2 Instrução fixa no prompt: não revelar segredo sem o motor liberar

**Status do código (18/10/2026):** criação de NPC pelo fluxo validado, dossiês privados, promessas, relações, relógios, diário do conhecimento e revelação de segredos por teste estão ligados ao motor e ao log persistido. A tela mostra apenas atitudes direcionadas ao jogador em palavras, sem números de relação. **Pendente:** enriquecer a agenda de NPCs e converter acontecimentos importantes em pistas de interface sem revelar relógios ou segredos privados.

---

## FASE 9 — Campanhas e capítulos

### Módulo 9.1 — Criação por descrição livre
- 9.1.1 Texto livre do jogador (continua sempre disponível)
- 9.1.2 Formulário auxiliar: gênero, época, tom, dificuldade, temas, duração
- 9.1.3 IA identifica sistema e edição exatos; o motor confirma se o pacote existe e, se a edição for ambígua, pergunta ao jogador
- 9.1.4 Geração incremental: só o necessário para começar

### Módulo 9.2 — Capítulos
- 9.2.1 Abertura de capítulo a partir do estado atual
- 9.2.2 Encerramento com resumo gerado e guardado uma vez
- 9.2.3 Tamanho de capítulo configurável
- 9.2.4 Registro de decisões, itens, NPCs e consequências

### Módulo 9.3 — Missões e linha do tempo
- 9.3.1 Missões, objetivos, estados
- 9.3.2 Linha do tempo de eventos importantes
- 9.3.3 Localizações e mapa textual

### Módulo 9.4 — Ambientação adaptável
- 9.4.1 Ambientação como dado separado (nome, tom, tecnologia, organizações, conflitos, equipamentos ficcionais)
- 9.4.2 A IA adapta NPCs, missões e itens à combinação ficha + sistema + ambientação
- 9.4.3 Itens e equipamentos fictícios recebem estatísticas pelo pacote de regras ou por regra adicional (4.4)
- 9.4.4 Trocar de ambientação em outra campanha reaproveita a mesma ficha
- 9.4.5 Teste: a ficha não muda de estrutura ao trocar o cenário

### Módulo 9.5 — Continuidade
- 9.5.1 Fechar e retomar sem perder nada
- 9.5.2 Teste: o capítulo seguinte começa no estado correto
- 9.5.3 Teste: promessa feita é lembrada, item consumido não volta

**Status do código (18/10/2026):** a criação incremental por descrição livre, o formulário auxiliar, revisão de compatibilidade, capítulos com resumo, tamanho configurável, missões/objetivos, linha do tempo manual, mapa de locais descobertos e persistência de ambientação/configuração estão conectados ao app. O fechamento automático de capítulo é acionado após jogadas no chat. **Pendente:** registrar automaticamente mais eventos na linha do tempo e concluir a tipagem mecânica de equipamentos ficcionais por pacote/adaptação.

---

## FASE 10 — Interface (6 abas)

### Módulo 10.1 — Jogar
- 10.1.1 Chat com o mestre, entrada livre
- 10.1.2 Sugestões de ação (opcionais)
- 10.1.3 Painel rápido: vida, recursos, condições, missão atual
- 10.1.4 Mostrar rolagens do motor de forma clara
- 10.1.5 Rolagens e números só do personagem do jogador. Nada de barras de vida ou atributos de NPC na tela, salvo o que uma habilidade de análise revelou

### Módulo 10.2 — Personagens
- 10.2.1 Criar, editar, ver evolução, reutilizar
- 10.2.2 Sem tela de ficha de NPC; só o que o personagem descobriu

### Módulo 10.3 — Tutorial e regras
- 10.3.1 Conteúdo por sistema/edição, vindo do pacote
- 10.3.2 Criação de ficha iniciada pelo tutorial

### Módulo 10.4 — Campanhas
- 10.4.1 Em andamento, capítulos, resumos, missões, diário

### Módulo 10.5 — Biblioteca de sistemas
- 10.5.1 Pacotes instalados, edição, versão, origem e licença
- 10.5.2 Importar novo pacote

### Módulo 10.6 — Configurações
- 10.6.1 Provedor, modelo, chave, limites
- 10.6.2 Preferências de geração
- 10.6.3 Backup, exportar/importar, privacidade
- 10.6.4 Uso estimado de tokens

**Status do código (19/10/2026):** as seis abas têm telas funcionais; Jogar inclui painel, dados visuais, inventário/equipamento, locais, regras adicionais, combate, atalhos locais e prévia de narração progressiva. Os dados incluem descrições semânticas para TalkBack, sem revelar faces antes da revelação. Heróis inclui criação guiada e biblioteca; Campanhas inclui backups por arquivo/texto, capítulos, missões, diário, linha do tempo e evolução; Sistemas importa ZIP; Config inclui teste de conexão, chave segura, preferências de narração e limites de conteúdo. **Pendente:** validação do streaming em execução, revisão completa TalkBack/contraste em aparelho, validação visual do primeiro uso e custo monetário confiável. A tela inicial agora mostra os três passos de configuração; a escala global de fonte já existe na Configuração, mas ainda não foi validada visualmente.

---

## FASE 11 — Testes de continuidade e acabamento

### Módulo 11.1 — Bateria de contradições (automática)
- 11.1.1 NPC lembra promessa
- 11.1.2 Item consumido não reaparece
- 11.1.3 Morte não é ignorada
- 11.1.4 Segredo descoberto permanece registrado
- 11.1.5 Habilidade respeita custo e requisito
- 11.1.6 Campanha não altera a ficha de outra
- 11.1.7 Falha de API não corrompe o estado
- 11.1.8 Exemplo obrigatório (anão minerador + cyberpunk) passa nos 8 passos **com cada pacote instalado**, para provar que o motor é independente de sistema
- 11.1.9 Regra adicional nunca aparece como oficial
- 11.1.10 Trocar ambientação não altera a estrutura da ficha

### Módulo 11.2 — Desempenho
- 11.2.1 Campanha longa (centenas de eventos) sem lentidão
- 11.2.2 Consumo de bateria e dados

### Módulo 11.3 — Entrega
- 11.3.1 Exportação/importação de backup testada
- 11.3.2 Aviso de privacidade (o texto da jogada vai ao provedor de IA)
- 11.3.3 APK de uso pessoal; versão pública só com chave do próprio usuário

**Status do código (19/10/2026):** há cobertura automatizada já existente para o motor e os fluxos puros, mas **nenhum build, teste, lint ou CI foi executado nesta etapa**, conforme pedido. Backup Android, migrações Room instrumentadas, teste em aparelho/emulador, bateria completa de continuidade e entrega/assinatura do APK continuam como validações pendentes. A revisão estática e as correções code-first foram ampliadas para streaming SSE, integração do fluxo de narração, bônus de traços explícitos e descrições acessíveis dos dados.

---

## Ordem de entrega (MVP primeiro)

| Marco | Fases | Resultado jogável |
|---|---|---|
| **MVP 1** | 1 → 4 e 4A (pacote de teste) → 5 → 6 → 7 | Criar ficha e jogar uma cena com mestre de IA, regras validadas |
| **MVP 2** | 8 → 9 | NPCs com segredos, revelação por teste, capítulos |
| **v1** | 10 completo → 11 | 6 abas, backup, bateria de testes, primeiro sistema real |

---

## Riscos conhecidos

1. **Limites do plano gratuito** podem mudar. Mitigação: provedor trocável e economia de tokens (6.4).
2. **Direitos autorais dos sistemas.** Mitigação: pacotes com mecânicas próprias ou importadas pelo usuário.
3. **A IA pode narrar um segredo cedo demais.** Mitigação: só enviar o segredo quando a cena precisa e instruir no prompt (8.5).
4. **Resposta inválida da IA.** Mitigação: validação de esquema, rollback, nova tentativa (6.2, 6.3).
5. **Perda do aparelho = perda dos dados.** Mitigação: backup local exportável (10.6.3).

---

## Atualização code-first — 19/10/2026 (fechamento dos cinco blocos)

Esta atualização registra somente alterações efetivamente gravadas nesta branch. Não substitui a validação posterior e não declara como concluídas integrações que ainda não existem.

### Implementado nesta continuação

- **Motor — testes avançados:** criada a API `AdvancedChecks.kt` para rolagem normal, vantagem/desvantagem, testes opostos e testes em grupo. Os resultados são calculados pelo motor; os dois resultados de vantagem/desvantagem ficam no log e só o resultado escolhido deve determinar a consequência. Há wrappers em `CampaignSession` para salvar os eventos no armazenamento persistente.
- **Continuidade — linha do tempo automática:** criada a projeção `automaticTimeline(events, playerId, state)`, derivada do log canônico, para descobertas do jogador, locais conhecidos, deslocamentos, XP/nível e condições do próprio personagem. Dossiês, segredos privados, relógios e mudanças ocultas de NPC ficam fora dessa projeção. A camada `PlayController` a expõe e a aba Campanhas mostra os registros.
- **IA — cache de prompt Anthropic:** `ProviderConfig.cacheSystemPrompt` habilita o bloco `cache_control: ephemeral` para o endpoint oficial `api.anthropic.com`; endpoints compatíveis de terceiros não presumem suporte. O cache é aplicado ao prefixo system, não a mensagens dinâmicas do jogador. A configuração pode ser desativada explicitamente. Isso pede suporte real do modelo/endpoint e ainda precisa de validação em rede.
- **Cobertura preparada:** adicionados testes unitários para rolagens avançadas, linha do tempo sem vazamento de dados privados e construção do pedido Anthropic com/sem cache. Eles foram escritos, mas não executados nesta etapa.
- **Documentação:** este plano e o changelog registram o estado atual e as limitações sem tratar código não compilado como validado.

### Ainda não fechado em código

1. **Mecânicas:** os testes avançados existem como API do motor, mas ainda não estão configuráveis por pacote nem integrados às decisões da interface. Continuam pendentes o gasto persistido de sorte/destino, recompensas de nível escolhidas pelo jogador e o modelo tipado completo para gatilhos/limites/imunidades dos traços e poderes.
2. **Continuidade:** a linha do tempo automática cobre apenas eventos seguramente atribuíveis ao jogador; não é ainda um sistema de pistas automáticas para agendas e consequências de NPC. A agenda continua exigindo integração explícita com o ciclo de jogada.
3. **IA:** o cache está implementado para a API oficial Anthropic, mas falta controle visual da opção e confirmação de hit/uso em rede real. A escolha de provedor gratuito padrão continua dependente de verificar limites atuais. Não declarar cache real para OpenAI-compatible: o comportamento varia por serviço.
4. **Interface:** a linha do tempo derivada foi adicionada à aba Campanhas; continuam pendentes revisão completa de TalkBack/contraste, relatório de diagnóstico e fluxo transacional de primeiro uso. O custo monetário só pode ser estimado quando houver preço configurado para modelo/provedor.
5. **Documentação e fechamento:** o estado do código está atualizado; o plano geral só poderá ser marcado como fechado depois de implementar as lacunas acima. Builds, testes, lint, CI, migrações instrumentadas e validação em aparelho permanecem deliberadamente adiados.

### Regra de trabalho mantida

Continuar corrigindo e integrando o código antes de iniciar a etapa de validação. Não marcar build, teste, rede real ou experiência em aparelho como aprovados sem executá-los.
