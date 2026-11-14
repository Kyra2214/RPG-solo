# Taxonomia proposta: categorias e subgrupos de raças, classes e profissões

**Estado:** revisada em 14/11/2026 (D-CG-11), sem mudança de agrupamento. **Não está no catálogo.**

**Revisão D-CG-11 (14/11/2026):** as 50 classes (6 categorias, 15 subgrupos), as 104 profissões (11 categorias, 28 subgrupos) e as 50 raças (5 categorias, 14 subgrupos) foram lidas por inteiro. Cada item tem uma só categoria e um só subgrupo, nenhum agrupamento é incoerente e as categorias ficam entre 3 e 18 itens. Os subgrupos pequenos (Estrada e correio, 2; Artrópodes, 2; Cura, 2) são válidos: pela regra de navegação 2 abaixo, categorias menores abrem direto nos cartões, então o subgrupo não aparece como degrau extra. Nada movido, para não quebrar `CriacaoDadosTest` nem fichas. ASSUMINDO: o critério é coerência interna e legibilidade da navegação, não quantidade igual por grupo.
 Serve de base ao submódulo CG-0.7 do antigo plano de criação guiada; classes e profissões já são lidas em `taxonomia-criacao.json` (ver `docs/CRIACAO_POR_ABAS.md`).
Versão para máquina: `taxonomia-proposta.json` (chave = `id` do catálogo; todas as 50 classes, 104 profissões e 50 raças cobertas, sem repetição).

## Por que existe
O catálogo hoje só tem as tags genéricas `raca`/`classe`/`profissao` e `original`, então não dá para agrupar. Os campos `weaponTypes` das classes são **conjuntos-modelo** (cinco no total) e não dizem a arma principal de cada classe; por isso o filtro por arma exigiria um campo editorial novo (`armaPrincipal`). Em 14/11/2026 (CG-3.9 e CG-3.7) a arma principal passou a ser o primeiro tipo de `weaponTypes`, com a ordem revisada por classe; o campo novo não foi criado.

## Regras
1. Cada item tem **uma categoria principal** e **um subgrupo**. A busca global encontra o item de qualquer lugar.
2. **Navegação adaptativa:** categoria → subgrupo → cartões. O nível de subgrupo só aparece quando a categoria tem mais de 12 cartões ou pelo menos 2 subgrupos com 3 ou mais cartões; senão a categoria abre direto nos cartões.
3. Sub-raças ficam **dentro da raça-mãe** (não entram na taxonomia). Híbridos formam a categoria **Mestiços**, listados pelas raças de origem.
4. Nomes de categorias e subgrupos são editáveis; os `id` do catálogo não mudam.

## Classes (50)

### Combate corpo a corpo (14)

- **Linha de frente** (5): Guerreiro, Lanceiro, Cavaleiro, Gladiador, Berserker
- **Defensores e juramentados** (5): Guardião, Sentinela, Paladino, Guardião do Código, Guardião Rúnico
- **Lâmina e artes marciais** (4): Duelista, Espadachim, Monge, Artista Marcial

### Furtivos e distância (6)

- **Furtivos** (3): Ladino, Assassino, Agente das Sombras
- **Arco e tiro** (3): Arqueiro, Atirador de Precisão, Pistoleiro

### Magia e mistérios (6)

- **Arcanistas** (3): Mago, Feiticeiro, Invocador
- **Mente e ilusão** (3): Ilusionista, Psíquico, Tecelão de Sonhos

### Fé, cura e espírito (5)

- **Cura** (2): Curandeiro, Médico de Campo
- **Fé e visão** (3): Sacerdote, Xamã, Oráculo

### Natureza, viagem e mar (9)

- **Exploração** (3): Explorador, Batedor, Sobrevivente
- **Feras e criaturas** (3): Caçador de Monstros, Domador, Druida
- **Mar** (3): Pirata, Marinheiro, Bucaneiro

### Intelecto, liderança e ofício (10)

- **Liderança e diplomacia** (3): Estrategista, Comandante, Diplomata
- **Estudo e investigação** (4): Investigador, Cronista, Cartógrafo, Guardião de Relíquias
- **Engenho e arte** (3): Artífice, Alquimista, Bardo

## Profissões (104)

### Produção e ofícios (18)

- **Metal, minas e armas** (7): Ferreiro, Armeiro, Armeiro de Armaduras, Minerador, Fabricante de Arcos, Fabricante de Flechas, Perito em Arcos
- **Madeira, pedra e barro** (5): Carpinteiro, Marceneiro, Pedreiro, Oleiro, Vidreiro
- **Tecido e couro** (6): Alfaiate, Tecelão, Curtidor, Sapateiro, Cordoeiro, Preparador de Peles

### Criação e arte (17)

- **Joias e gemas** (5): Especialista em Joias, Lapidador, Ourives, Joalheiro de Campo, Montador de Gemas
- **Artes e espetáculo** (7): Artista, Músico, Ator, Dançarino, Tatuador, Mestre de Cerimônias, Organizador de Eventos
- **Preparados e fragrâncias** (5): Perfumista, Saboeiro, Alquimista de Oficina, Especialista em Toxinas, Herbalista

### Comida e campo (7)

- **Cozinha e bebida** (3): Cozinheiro, Padeiro, Cervejeiro
- **Lavoura e criação** (4): Agricultor, Apicultor, Jardineiro, Criador de Animais

### Natureza e animais (14)

- **Guias e caça** (6): Guia de Montanha, Guia de Floresta, Guia de Deserto, Caçador, Guarda-Florestal, Observador do Clima
- **Animais** (4): Tratador de Montarias, Tratador de Aves Mensageiras, Treinador, Adestrador
- **Ciências da natureza** (4): Botânico, Geólogo, Paleontólogo, Astrônomo

### Mar e estrada (8)

- **Mar e pesca** (6): Pescador, Pescador de Profundidade, Mergulhador, Marinheiro de Carga, Construtor de Barcos, Navegador
- **Estrada e correio** (2): Mensageiro, Cartógrafo de Campo

### Saber e estudo (11)

- **Letras e ensino** (7): Escriba, Bibliotecário, Tradutor, Professor, Arquivista, Genealogista, Heraldista
- **Passado e relíquias** (4): Arqueólogo, Restaurador, Reparador de Relíquias, Avaliador de Antiguidades

### Engenharia e construção (7)

- **Construção** (3): Arquiteto, Engenheiro de Pontes, Construtor de Fortificações
- **Engenho e mecânica** (4): Inventor, Mecânico, Mecânico de Artefatos, Relojoeiro

### Cura e saúde (3)

- **Cuidados** (3): Curandeiro Comunitário, Enfermeiro, Cirurgião

### Comércio e serviços (7)

- **Comércio e contas** (3): Comerciante, Avaliador de Materiais, Contador
- **Hospedagem e cuidados pessoais** (4): Estalajadeiro, Taverneiro, Barbeiro, Lavadeiro

### Lei, investigação e proteção (12)

- **Lei e diplomacia** (4): Advogado, Escrivão Judicial, Diplomata de Corte, Mediador
- **Investigação e segredos** (5): Detetive, Investigador de Incêndios, Espião, Criptoanalista, Perito em Armadilhas
- **Proteção e captura** (3): Escolta Profissional, Carcereiro, Caçador de Recompensas

## Raças (50)

### Povos clássicos (6)

- **Povos clássicos** (6): Humano, Elfo, Anão, Orc, Pequenino, Gnomo

### Herança sobrenatural e sombria (12)

- **Sangue lendário** (6): Draconato, Nascido do Crepúsculo, Luminar, Altaneiro, Giganteano, Estelar
- **Noite, sonho e artifício** (6): Noctívago, Metamorfo Lupino, Moldável, Umbral, Onírico, Construto Desperto

### Povos bestiais (13)

- **Felinos e aves** (4): Felídeo Errante, Felino das Nuvens, Corvídeo, Corvário
- **Cascos e chifres** (4): Centauro, Labiríntico, Cervino, Porcino
- **Caninos, roedores e ursos** (5): Lupino, Vulpino, Ratoide, Leporino, Ursino

### Escamados, goblinoides e artrópodes (8)

- **Escamados** (3): Escamado, Escamado Miúdo, Tartarino
- **Goblinoides** (3): Goblin, Goblin de Legião, Ursino Sombrio
- **Artrópodes** (2): Aracnídeo, Insetoide

### Natureza, elementos e águas (11)

- **Elementais** (4): Nascido da Rocha, Nascido da Brasa, Nascido da Maré, Nascido do Vento
- **Florestas e fadas** (4): Feérico, Silvano Festivo, Verdejante, Micélioide
- **Pedra e águas** (3): Cristalino, Abissal Costeiro, Sereiano

## Mestiços (híbridos): 20
Categoria própria, exibida por par de raças de origem (campo `raceIds`). Sem subgrupos.

## Tamanhos
Classes: 6 categorias (5 a 14 itens). Profissões: 10 categorias (3 a 18 itens). Raças: 5 categorias (6 a 13 itens), mais Mestiços.
