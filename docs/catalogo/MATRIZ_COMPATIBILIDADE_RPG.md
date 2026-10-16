# MATRIZ_COMPATIBILIDADE_RPG.md

**Regra do app:** qualquer combinação (sistema/edição + ambientação) **sem regra registrada é `UNVERIFIED`**. O app nunca presume compatibilidade.

Estados: `NATIVE` (compatível nativamente) · `ADAPTED` (compatível com adaptação documentada) · `UNVERIFIED` (não verificada) · `BLOCKED` (bloqueada até resolver conflitos).

Dados em `rules/src/main/resources/catalog/compat.txt`. As classificações abaixo são **análise do projeto, não regra oficial**.

| Sistema/edição | Ambientação | Estado | Nota |
|---|---|---|---|
| `cepheus-engine` | `setting.space-opera` | NATIVE | Sistema 2D6 de ficção científica (o repositório Andromeda usa o pacote sci-fi do Cepheus) |
| `dnd-5-2-srd` | `setting.cyberpunk` | ADAPTED | Exige regras adicionais de tecnologia, hacking e implantes |
| `dnd-5-2-srd` | `setting.japao-feudal` | ADAPTED | Exige adaptar classes e equipamento ao período |
| qualquer outra | qualquer | UNVERIFIED | Padrão |

**Exemplo de aceitação (anão minerador, cyberpunk, 3D&T, tático, Hard):** o teste `exemploAnaoMineradorCyberpunk3detExigeEdicaoEAvisa` comprova que o app (1) pergunta a edição quando ela não é informada, (2) aceita `3det-alpha`, (3) classifica a combinação como `UNVERIFIED` e (4) devolve avisos para o jogador aprovar antes de começar. **Isso não valida a ficha nem as regras do 3D&T**: o pacote de regras do 3D&T ainda não existe.

**Dimensões que não entram na matriz:** tom, dificuldade, progressão e limites de conteúdo são independentes de sistema e de gênero.
