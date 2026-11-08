# Protótipo mínimo (M6.4)

**Status:** roteiro e métricas definidos **antes** de jogar. Nenhuma sessão foi executada (só especificação, como na F6). Resultados entram na seção 9, sem alterar as seções 1 a 8. A seção 10 traz o protótipo do reino 2 (Coroas de Pedra).

**Fontes:** `reinos/01-aeltharyn-reino-das-florestas/15-cenas-de-teste.md` (`cena.vilarejo-raiz-de-prata`), `14-resumos-de-contexto.csv`, `14-camadas-de-informacao.csv`, `13-sementes-de-aventura.csv`, doc 06 §11.1 (contrato anti-invenção). Nada de lore novo.

## 1. Pergunta do teste

O mundo escrito melhora uma sessão curta de RPG com IA em relação à mesma sessão sem ele, **sem** invenção proibida e **sem** vazar segredo?

## 2. Recorte

| Item | Valor |
|---|---|
| Vilarejo | `settlement.raiz-de-prata` |
| NPC no vilarejo | `npc.nivara-folha-de-orvalho` (única pessoa do assentamento com registro) |
| Cidade-mãe | `city.elarion`, em **uma linha** (ver §3) |
| Data | Ano 742, Silêncio, Lua 1, dia 1 (D-14) |
| Duração | 8 turnos do jogador, roteiro fixo (§5) |

Avariel Thorne, Lethariel Nascente-Clara e Maereth Vaelorian **não** entram no contexto; só podem aparecer se o jogador citar a corte, e então valem as regras da §4.

## 3. Contexto da condição B (com mundo)

Texto de `cena.vilarejo-raiz-de-prata` ("entra", 1110 caracteres, sem alteração) mais **uma** linha de cidade-mãe:

`[city.elarion] Capital política, arquivo e centro cerimonial, em sete árvores colossais junto ao Rio Lúmen; Intendente Avariel Thorne.`

Linha tirada do resumo de Elarion e do resumo de Avariel; sem duração de viagem (a rota Raiz-de-Prata–Elarion não está documentada). Total esperado: até ~1.250 caracteres (limite da cena: 2.400).

**Condição A (sem mundo):** só a instrução "vilarejo élfico; o jogador acaba de chegar; conduza a cena". Mesmo modelo, mesmas instruções de mestre, mesma temperatura.

## 4. Regras de classificação (fixas antes de jogar)

- **Cânone:** só o que está no contexto B, em `14-camadas-de-informacao.csv` ou nos resumos.
- **Contradição:** fala ou fato da IA que nega o cânone (ex.: outra liderança, outra cidade-mãe, Coroa sem conflito).
- **Invenção proibida** (doc 06 §11.1): nome próprio de reino, cidade, facção, rei ou NPC com registro que não exista; rota ou duração não documentada tratada como fato; população numérica. Nome de figurante sem papel político **não** conta.
- **Vazamento:** aparece `fact.raiz-estoque-escondido` (secreto) sem que o jogador tenha feito inspeção, contagem contra o declarado ou ganhado a confiança de Nivara. `fact.raiz-germinacao-anormal` (local) pode aparecer.
- **Gancho usado:** a IA oferece ou desenvolve `seed.raiz-de-prata.p` (ou `.m`/`.g` só se o jogador a tocar).
- Cada resposta da IA é lida por quem aplica o teste, com a lista acima; sem julgamento de "gosto".

## 5. Roteiro do jogador (8 turnos, idêntico nas duas condições)

1. Chego a pé ao vilarejo e olho em volta.
2. Pergunto a quem encontro quem manda aqui.
3. Peço para falar com essa pessoa.
4. Pergunto o que a vila faz e do que precisa.
5. Ofereço ajuda.
6. Pergunto como vão as sementes.
7. Pergunto se a corte tem algo a ver com isso.
8. Digo que vou até a cidade e pergunto o que devo levar ou dizer.

O turno 7 testa a tensão com a Coroa; o 8 testa a cidade-mãe em uma linha. Nenhum turno pede inspeção do estoque (o segredo não deve sair).

## 6. Execução

3 execuções por condição (6 no total), mesmo modelo e temperatura, ordem alternada (A, B, B, A, A, B), contexto limpo a cada execução, sem o aplicador conhecer a condição ao classificar (embaralhar e anonimizar as transcrições).

## 7. Métricas

| ID | Métrica | Como medir | Alvo em B |
|---|---|---|---|
| P1 | Contradições com o cânone | contagem por execução | 0 |
| P2 | Invenções proibidas | contagem por execução | 0 |
| P3 | Vazamento de segredo | sim/não por execução | 0 de 3 |
| P4 | Uso correto da liderança | Nivara nomeada como liderança em ≥ 1 turno, sem outra liderança | 3 de 3 |
| P5 | Gancho usado | `seed.raiz-de-prata.p` oferecido até o turno 6 | ≥ 2 de 3 |
| P6 | Tensão com a Coroa | turno 7 cita mudas raras ou sementes locais | ≥ 2 de 3 |
| P7 | Cidade-mãe | turno 8 cita Elarion e nada além do cânone | 3 de 3 |
| P8 | Consistência | nenhum nome ou fato muda entre turnos da mesma execução | 3 de 3 |
| P9 | Custo de contexto | caracteres do contexto B | ≤ 1.250 |
| P10 | Especificidade (nota cega do autor, 1 a 5) | média das 3 transcrições, A × B | B − A ≥ 1,0 |

## 8. Critério de aceite

- **Passa:** P1, P2, P3 todos no alvo **e** pelo menos 6 das 7 restantes (P4 a P10) no alvo **e** P10 com diferença mínima atingida.
- **Falha dura:** qualquer vazamento (P3) ou invenção proibida (P2) em B reprova o protótipo, mesmo com o resto no alvo.
- **Inconclusivo:** P1 a P3 ok, mas menos de 6 das 7 restantes: ajustar o contexto (recorte, não lore) e repetir uma vez.
- A condição A só serve de linha de base; P1 a P3 em A **não** reprovam nada (sem contexto não há cânone a violar), mas são registradas para comparação.

## 9. Resultados

Não executado. Preencher aqui: data, modelo, temperatura, tabela P1–P10 por execução, veredito (§8) e pendências.

---

# 10. Reino 2: Coroas de Pedra (R2-7.3)

**Status:** roteiro e métricas fixados **antes** de jogar (D-60, aguardando o autor). Nenhuma sessão foi executada. As seções 1 a 9 (Aeltharyn) não mudam; os resultados do reino 2 entram só na §10.9. Mesma pergunta do teste (§1), mesmo método (§6) e mesma lógica de aceite (§8).

**Fontes:** `reinos/02-coroas-de-pedra-reino-das-montanhas/15-cenas-de-teste.md` (`cena.vilarejo-rastro-branco`), `14-resumos-de-contexto.csv`, `14-camadas-de-informacao.csv`, `13-sementes-de-aventura.csv`, `06-modelo-operacional-e-aventuras.md` §9 (contrato anti-invenção). Nada de lore novo.

## 10.1 Recorte (R2-7.3 a)

| Item | Valor |
|---|---|
| Vilarejo | `settlement.rastro-branco` (Rastro-Branco, a Comunidade da Neve) |
| NPC no vilarejo | `npc.miraq-salto-de-nevoa` (liderança; sem ficha completa, só resumo) |
| Cidade-mãe | `city.altavento`, em **uma linha** (§10.2) |
| Data | Ano 516 das Atas, Gelo Fechado, Lua 1, dia 1 (= ano 742 de Aeltharyn, doc 01 parte 5) |
| Duração | 8 turnos do jogador, roteiro fixo (§10.4) |

Zhae Garra-de-Névoa, Vireo Vento-Rápido, a Rede dos Passos Altos e `clock.passos` **não** entram no contexto; só podem aparecer se o jogador os citar, e então valem as regras do §10.3.

## 10.2 Condições (R2-7.3 b)

**Condição B (com mundo):** texto "entra" de `cena.vilarejo-rastro-branco` (1.064 caracteres, sem alteração) mais **uma** linha de cidade-mãe:

`[city.altavento] Cidade de altitude, ninhos e passos de vento; Assembleia dos Picos, com Vireo Vento-Rápido como Voz dos Picos.`

Linha tirada do resumo de Altavento (`14-resumos-de-contexto.csv`); sem duração, sem número de população e sem tensões (o acesso Rastro-Branco–Altavento não tem duração escrita). Total: 1.064 + 1 + 127 = **1.192 caracteres** (limite da cena: 2.400).

**Condição A (sem mundo):** só a instrução "vilarejo de montanha; o jogador acaba de chegar; conduza a cena". Mesmo modelo, mesmas instruções de mestre, mesma temperatura.

## 10.3 Regras de classificação (fixas antes de jogar)

- **Cânone:** só o que está no contexto B, em `14-camadas-de-informacao.csv` ou nos resumos. Cota de caça, sinais de avalanche e decisões da cidade só valem como cânone se aparecem no contexto B ou no resumo (a regra do contexto os reserva ao Mestre).
- **Contradição:** fala ou fato da IA que nega o cânone (outra liderança, outra cidade-mãe, cota já decidida, Altavento sem Assembleia).
- **Invenção proibida** (doc 06 §9.3): nome próprio de cidade, assentamento, facção, governante ou pessoa com registro que não exista; 11ª cidade ou 13º povo nativo; rota, distância, duração ou população numérica tratadas como fato; guerra; resolução de mistério; qualquer menção a rota, tempo ou fato de Aeltharyn. Figurante anônimo ou nome de figurante sem papel político **não** conta (classe *livre*, §9.2).
- **Vazamento:** aparece `fact.relogio-passos` (secreto): o contador (1/6), segmentos, a existência de um "relógio" ou a causa afirmada como certa sem o jogador ter inspecionado ou comparado sinais; ou qualquer fato `restrito` ou `secreto` de `14-camadas-de-informacao.csv`. `fact.zhae-expulsa-cacadores` (local) pode aparecer se Zhae surgir. Sinais de avalanche podem ser **sugeridos** como possibilidade; afirmados como certos, contam como invenção de estado (relógio sem gatilho).
- **Gancho usado:** a IA oferece ou desenvolve `seed.rastro-branco.p` (`.m` e `.g` só se o jogador as tocar).
- Cada resposta da IA é lida por quem aplica o teste com a lista acima; sem julgamento de "gosto".

## 10.4 Roteiro do jogador (8 turnos, idêntico nas duas condições)

1. Chego a pé ao vilarejo e olho em volta.
2. Pergunto a quem encontro quem lidera a comunidade.
3. Peço para falar com essa pessoa.
4. Pergunto o que a comunidade faz e do que precisa.
5. Ofereço ajuda.
6. Pergunto se algum guia anda com problema nos passos.
7. Pergunto o que a cidade tem a ver com isso.
8. Digo que vou até a cidade e pergunto o que devo levar ou dizer.

O turno 7 testa a tensão com a cidade (cota de caça, descer ao vale); o 8 testa a cidade-mãe em uma linha e a armadilha de duração. Nenhum turno pede inspeção de sinais de avalanche nem do estado dos passos (o relógio não deve sair).

## 10.5 Execução

Igual à §6: 3 execuções por condição (6 no total), mesmo modelo e temperatura, ordem alternada (A, B, B, A, A, B), contexto limpo a cada execução, transcrições embaralhadas e anonimizadas antes de classificar.

## 10.6 Métricas (R2-7.3 c)

| ID | Métrica | Como medir | Alvo em B |
|---|---|---|---|
| Q1 | Contradições com o cânone | contagem por execução | 0 |
| Q2 | Invenções proibidas | contagem por execução | 0 |
| Q3 | Vazamento de segredo | sim/não por execução | 0 de 3 |
| Q4 | Uso correto da liderança | Miraq nomeada como liderança em ≥ 1 turno, sem outra liderança | 3 de 3 |
| Q5 | Gancho usado | `seed.rastro-branco.p` oferecido até o turno 6 | ≥ 2 de 3 |
| Q6 | Tensão com a cidade | turno 7 cita a cota de caça ou a descida ao vale | ≥ 2 de 3 |
| Q7 | Cidade-mãe | turno 8 cita Altavento e nada além do cânone, **sem duração nem distância** | 3 de 3 |
| Q8 | Consistência | nenhum nome ou fato muda entre turnos da mesma execução | 3 de 3 |
| Q9 | Custo de contexto | caracteres do contexto B | ≤ 1.250 (medido: 1.192) |
| Q10 | Especificidade (nota cega do autor, 1 a 5) | média das 3 transcrições, A × B | B − A ≥ 1,0 |

## 10.7 Critério de aceite (escrito antes da sessão)

- **Passa:** Q1, Q2, Q3 todos no alvo **e** pelo menos 6 das 7 restantes (Q4 a Q10) no alvo **e** Q10 com diferença mínima atingida.
- **Falha dura:** qualquer vazamento (Q3) ou invenção proibida (Q2) em B reprova o protótipo, mesmo com o resto no alvo.
- **Inconclusivo:** Q1 a Q3 ok, mas menos de 6 das 7 restantes: ajustar o contexto (recorte, não lore) e repetir uma vez.
- A condição A só serve de linha de base; Q1 a Q3 em A **não** reprovam nada, mas são registradas para comparação.

## 10.8 Diferenças em relação a Aeltharyn (§1 a §9)

- O vilarejo das Coroas **não tem fato `local` ou secreto próprio** em `14-camadas-de-informacao.csv`; o segredo guardado é o relógio `clock.passos` (`fact.relogio-passos`), só de uso de Mestre. Por isso o vazamento é medido contra esse fato e contra qualquer fato restrito ou secreto.
- Toda duração do reino 2 é `desconhecida` (doc 07): a armadilha do turno 8 é a duração; em Aeltharyn era a rota não documentada.
- Contexto B de 1.192 caracteres (Aeltharyn: ~1.250 esperado). Nenhuma execução foi feita.

## 10.9 Resultados (reino 2)

Não executado. Preencher aqui: data, modelo, temperatura, tabela Q1–Q10 por execução, veredito (§10.7) e pendências.

# 11. Reino 3: Véu dos Malditos (R3-7.3)

**Estado:** rascunho de R3-7.3 para aprovação do autor (D-98) · **Etiqueta:** **[PROPOSTO]**; só critério de aceite e roteiro, **nenhuma sessão executada**. Segue a forma do §10; o que difere está no §11.8.

## 11.1 Recorte (R3-7.3 a)

**ASSUMINDO (D-66):** o reino não tem vilarejos nem cidade-mãe. O "vilarejo" é **Fio-Longo** (porte *pequena*, vila-hospedaria; não é uma das seis cenas de R3-6.3) e o "outro lugar em uma linha" é **Morrovéu** (capital e sede do Conselho dos Pactuários, sem relação de cidade-mãe).

| Item | Valor |
|---|---|
| Lugar | `city.fio-longo` (Fio-Longo, a Casa dos Fios) |
| NPC no lugar | `npc.ondra-fio-de-sono` (liderança; resumo e voz, sem ficha completa) |
| Outro lugar | `city.morroveu`, em **uma linha** (§11.2) |
| Data | Ano 389 da Contagem das Provações, Névoa Fechada, Lua 1, dia 1 (= ano 742 de Aeltharyn, doc 01 parte 5) |
| Duração | 8 turnos do jogador, roteiro fixo (§11.4) |

Rhek Relato-Manso, Noor Despertar-Primeiro, os Vigias da Orla, `clock.hospedes` e `fact.relogio-hospedes` **não** entram no contexto; só podem aparecer se o jogador os citar, e então valem as regras do §11.3.

## 11.2 Condições (R3-7.3 b)

**Condição B (com mundo):** texto exato abaixo, montado dos resumos, da voz, dos fatos `local` e da semente `.p` já escritos (nada acrescentado), mais **uma** linha de Morrovéu, tirada do resumo de Morrovéu sem tensão, sem povos e sem ligações:

```
[data] Ano 389 da Contagem das Provações, Névoa Fechada (inverno), Lua 1, dia 1.
[city.fio-longo] Vila-hospedaria de repouso e sonhos. Governo: Casa dos Fios; à frente, Ondra Fio-de-Sono. Vive de hospedagem de repouso, tecelagem, sonhos e remédios de sono. Tensão: Hóspedes que não querem ir embora; a Casa diz que é escolha, os parentes dizem que não. Povos: Onírico; Moldável; Corvídeo; Feérico, Pequenino e Humano. Porte: pequena (relativo, sem número).
[city.fio-longo] Vizinhança: Ligações (sem duração): Morrovéu (assento no Conselho); afastada das trilhas de caravana, sudoeste da capital.
[npc.ondra-fio-de-sono] Ondra Fio-de-Sono, Onírico. Mestra da Casa dos Fios (assento de Fio-Longo); guardiã do Pacto do Repouso; líder da Casa dos Fios (F-09). Tecelã de sonhos e estalajadeira; hospeda e guarda o pacto; restrição: não obriga ninguém a ficar nem a partir. Agenda: Manter a Casa um lugar de repouso e o pacto de que ninguém é atacado dormindo.
[voz npc.ondra-fio-de-sono] tom: acolhedora e evasiva; voz de quem embala; modelo: "Aqui ninguém é atacado dormindo. É o pacto." | "A Casa hospeda. Ninguém é obrigado a ficar nem a partir." | "Descanse. O resto espera."
[fact.ondra-governa-casa|local] Governa a Casa dos Fios
[fact.hospedes-que-ficam|local] alguns hóspedes não querem ir embora e a Casa e os parentes divergem sobre o motivo
[seed.fio-longo.p] O fio que guarda o sonho: Ondra Fio-de-Sono ouve de novo o rumor de que o fio de um tecido guarda o sonho de quem o tece e pede que alguém veja de onde o boato vem entre os hóspedes, sem afirmar que seja verdade.
[city.morroveu] Cidade-fortaleza num morro entre charcos; Corte do Rei Vencedor, Morvan Voz-Baixa, e Conselho dos Pactuários, com um assento por lugar.
```

Total: **1.731 caracteres** (1.579 do bloco de Fio-Longo + 1 quebra + 151 da linha de Morrovéu; limite da cena: 2.400). Sem duração, sem número e sem rota.

**Condição A (sem mundo):** só a instrução "vila-hospedaria de repouso num vale úmido; o jogador acaba de chegar; conduza a cena". Mesmo modelo, mesmas instruções de mestre, mesma temperatura.

## 11.3 Regras de classificação (fixas antes de jogar)

- **Cânone:** só o que está no contexto B, em `14-camadas-de-informacao.csv` ou nos resumos. Quem repousa, quem são os parentes e os motivos de ficar ou partir só valem como cânone se aparecem no contexto B.
- **Contradição:** fala ou fato da IA que nega o cânone (outra liderança, outro pacto, Casa que obriga a ficar ou a partir, Fio-Longo sem assento no Conselho).
- **Invenção proibida** (doc 06 §9.3): nome próprio de lugar, facção, governante ou pessoa com registro que não exista; 9º lugar ou 12º povo nativo; novo rei, Provação vencida ou herói lendário vivo; rota, distância, duração ou população numérica tratadas como fato; guerra; resolução de mistério; qualquer menção a Aeltharyn, Neblina Longa ou Marcas das Feras. Figurante anônimo ou hóspede sem nome e sem papel político **não** conta (classe *livre*, §9.2).
- **Vazamento:** aparece `fact.ondra-hospedes-que-ficam` (restrito: quem não quer partir e por quê), `fact.relogio-hospedes` (secreto: o contador 1/6, segmentos, a existência de um "relógio" ou a causa afirmada como certa), ou qualquer fato `restrito` ou `secreto` de `14-camadas-de-informacao.csv`. As três frases que a coluna `nunca_diz` de `12-voz.csv` proíbe (motivo de quem não quer partir, o que o quarto fechado guarda, que a Casa retém hóspedes) contam como vazamento se a IA as afirmar como fato. Hipóteses ditas como boato ou dúvida **não** contam.
- **Gancho usado:** a IA oferece ou desenvolve `seed.fio-longo.p` (`.m` e `.g` só se o jogador as tocar).
- Cada resposta da IA é lida por quem aplica o teste com a lista acima; sem julgamento de "gosto".

## 11.4 Roteiro do jogador (8 turnos, idêntico nas duas condições)

1. Chego ao vale e olho em volta.
2. Pergunto a quem encontro quem cuida da casa de repouso.
3. Peço para falar com essa pessoa.
4. Pergunto o que a Casa faz e do que vive.
5. Peço um quarto para repousar.
6. Pergunto se alguém por aqui anda com algum problema.
7. Pergunto o que os parentes de quem repousa dizem da Casa.
8. Digo que vou até a capital falar com o Conselho e pergunto o que devo levar ou dizer.

O turno 7 testa a tensão local (hóspedes que ficam, divergência entre a Casa e os parentes) sem pedir o motivo; o 8 testa a linha de Morrovéu e a armadilha de duração. Nenhum turno pede o motivo de quem fica nem inspeção de quartos (o segredo e o relógio não devem sair).

## 11.5 Execução

Igual à §6: 3 execuções por condição (6 no total), mesmo modelo e temperatura, ordem alternada (A, B, B, A, A, B), contexto limpo a cada execução, transcrições embaralhadas e anonimizadas antes de classificar.

## 11.6 Métricas (R3-7.3 c)

| ID | Métrica | Como medir | Alvo em B |
|---|---|---|---|
| Q1 | Contradições com o cânone | contagem por execução | 0 |
| Q2 | Invenções proibidas | contagem por execução | 0 |
| Q3 | Vazamento de segredo | sim/não por execução | 0 de 3 |
| Q4 | Uso correto da liderança | Ondra nomeada como liderança da Casa em ≥ 1 turno, sem outra liderança | 3 de 3 |
| Q5 | Gancho usado | `seed.fio-longo.p` oferecido até o turno 6 | ≥ 2 de 3 |
| Q6 | Tensão local | turno 7 cita a divergência entre a Casa e os parentes (ou hóspedes que não querem ir embora), **sem afirmar o motivo** | ≥ 2 de 3 |
| Q7 | Morrovéu | turno 8 cita Morrovéu ou o Conselho e nada além do cânone, **sem duração nem distância** | 3 de 3 |
| Q8 | Consistência | nenhum nome ou fato muda entre turnos da mesma execução | 3 de 3 |
| Q9 | Custo de contexto | caracteres do contexto B | ≤ 1.800 (medido: 1.731) |
| Q10 | Especificidade (nota cega do autor, 1 a 5) | média das 3 transcrições, A × B | B − A ≥ 1,0 |

## 11.7 Critério de aceite (escrito antes da sessão)

- **Passa:** Q1, Q2, Q3 todos no alvo **e** pelo menos 6 das 7 restantes (Q4 a Q10) no alvo **e** Q10 com diferença mínima atingida.
- **Falha dura:** qualquer vazamento (Q3) ou invenção proibida (Q2) em B reprova o protótipo, mesmo com o resto no alvo.
- **Inconclusivo:** Q1 a Q3 ok, mas menos de 6 das 7 restantes: ajustar o contexto (recorte, não lore) e repetir uma vez.
- A condição A só serve de linha de base; Q1 a Q3 em A **não** reprovam nada, mas são registradas para comparação.

## 11.8 Diferenças em relação ao reino 2 (§10)

- Não há vilarejo nem cidade-mãe (D-66): o lugar é uma das oito cidades de porte pequeno e a linha extra é a capital, sem relação de dependência.
- O lugar **tem** fato restrito e relógio secreto próprios (`fact.ondra-hospedes-que-ficam`, `fact.relogio-hospedes`), e a voz traz `nunca_diz`; no reino 2 só havia o relógio.
- A armadilha do turno 8 é a duração até Morrovéu (toda ligação do reino é `desconhecida`, doc 07); o turno 7 tem a armadilha do motivo (restrito).
- Contexto B de 1.731 caracteres (reino 2: 1.192). Nenhuma execução foi feita.

## 11.9 Resultados (reino 3)

Não executado. Preencher aqui: data, modelo, temperatura, tabela Q1–Q10 por execução, veredito (§11.7) e pendências.

## 11.10 Testes de fim de F7 (locais)

18 CSV íntegros; conversor idempotente; validadores do reino 3 (0 erro, 22 avisos, 6 exceções) e do reino 2 (0 erro, 40 avisos, 7 exceções) com relatórios iguais aos salvos; IDs citados no §11 existem; `fact.ondra-hospedes-que-ficam` (restrito) e `fact.relogio-hospedes` (secreto) fora do contexto B; reinos 1 e 2, `mundo/`, `referencia/` e `referencia-tecnica/` idênticos ao ZIP de entrada. 0 falha real, 0 regressão.

# 12. Reino 4: Vale Palancar (R4-7.3)

**Estado:** rascunho de R4-7.3 para aprovação do autor (D-135) · **Etiqueta:** **[PROPOSTO]**; só critério de aceite e roteiro, **nenhuma sessão executada**. Segue a forma do §11; o que difere está no §12.8.

## 12.1 Recorte (R4-7.3 a)

**ASSUMINDO (D-102):** o reino não tem vilarejos nem cidade-mãe de função de vila. O "lugar pequeno" é **Alvasal** (porte *pequena*, pouso fixo; não é uma das sete cenas de R4-6.3) e o "lugar de mercado em uma linha" é **Rasovau** (vau e mercado de curral, sem relação de lugar-mãe com Alvasal).

| Item | Valor |
|---|---|
| Lugar | `city.alvasal` (Alvasal, o Pouso do Ermo) |
| NPC no lugar | `npc.gruzk-estandarte-baixo` (comandante das vigias; resumo e voz, sem ficha completa) |
| Lugar de mercado | `city.rasovau`, em **uma linha** (§12.2) |
| Data | Pasto Magro, Lua 1, dia 1 do ano 611 da Contagem dos Marcos (= ano 742 de Aeltharyn, doc 01 parte 5) |
| Duração | 8 turnos do jogador, roteiro fixo (§12.4) |

Aruun Poço-Compartilhado só aparece pelo nome, como liderança do pouso, dentro do resumo de Alvasal; seu resumo, `clock.cisterna`, `fact.gruzk-negou-agua`, `fact.relogio-cisterna` e as demais pessoas do pouso **não** entram no contexto e só podem aparecer se o jogador os citar, e então valem as regras do §12.3.

## 12.2 Condições (R4-7.3 b)

**Condição B (com mundo):** texto exato abaixo, montado dos resumos, da voz, dos fatos `publico` e da semente `.p` já escritos (nada acrescentado), mais **uma** linha de Rasovau, tirada do resumo de Rasovau sem liderança, sem tensão, sem povos e sem ligações:

```
[data] Pasto Magro (inverno), Lua 1, dia 1 do ano 611 da Contagem dos Marcos.
[city.alvasal] Alvasal — O Pouso do Ermo. Pouso de caravana na borda norte do Ermo de Sal (regime fixo). Liderança: Aruun Poço-Compartilhado. Obrigação: Guardar a cisterna e quem se prepara para o Ermo. Interesse: Manter a água e os acertos de caravana. Tensão: A cisterna não serve a todas as caravanas ao mesmo tempo; discordância sobre a preferência. Povos: Goblin; Goblin de Legião; Lupino. Porte: pequena (relativo, sem número).
[city.alvasal] Vizinhança: Ligações (sem duração): Ermo de Sal (Rota do Sal; nada além do Marco do Ermo).
[npc.gruzk-estandarte-baixo] Gruzk Estandarte-Baixo, Goblin de Legião. Comandante das vigias de Alvasal (turnos de legião); guardião de Alvasal. Limite: comanda só as vigias do pouso; responde ao Capitão de Marcha na defesa e não decide a ordem das caravanas. Pressão: A cisterna não serve a todas ao mesmo tempo; as caravanas e os clãs discordam sobre a preferência.
[voz npc.gruzk-estandarte-baixo] tom: disciplinado e seco; frases curtas, em ordem de turno; modelo: "O turno não muda. A água é de quem o pacto manda." | "A cisterna fica guardada; as caravanas, atendidas." | "Quem quiser preferência leva o caso à Assembleia."
[fact.gruzk-comanda-turnos-cisterna|publico] Comanda os turnos de vigia da cisterna
[fact.mpl-05-uma-so-cisterna|publico] Alvasal depende de uma só cisterna, que não serve a todas as caravanas ao mesmo tempo
[seed.alvasal.p] A água mais salgada: Aruun Poço-Compartilhado pede que alguém prove a água da Cisterna Branca antes e depois de uma caravana, sem afirmar que a caravana a mudou.
[city.rasovau] Rasovau — O Vau das Trocas. Vau e mercado de curral no curso do Argente (regime fixo). Obrigação: Manter o vau transitável e o mercado de curral. Interesse: Cobrar passagem e acertar trocas.
```

Total: **1.840 caracteres** (1.634 do bloco de Alvasal + 1 quebra + 205 da linha de Rasovau; limite da cena: 2.400). Sem duração, sem número e sem rota entre Alvasal e Rasovau (o doc 07 não tem rota interna para Alvasal).

**Condição A (sem mundo):** só a instrução "pouso de caravana na borda de um deserto de sal; o jogador acaba de chegar; conduza a cena". Mesmo modelo, mesmas instruções de mestre, mesma temperatura.

## 12.3 Regras de classificação (fixas antes de jogar)

- **Cânone:** só o que está no contexto B, em `14-camadas-de-informacao.csv` ou nos resumos. Quem são as caravanas, os clãs que discordam e o que as caravanas levam só valem como cânone se aparecem no contexto B; o resto é detalhe livre (doc 06 §9.2).
- **Contradição:** fala ou fato da IA que nega o cânone (outra liderança do pouso; Gruzk como dono da cisterna ou decidindo a ordem das caravanas; Gruzk sem responder ao Capitão de Marcha na defesa; mais de uma cisterna; Alvasal sem a Rota do Sal).
- **Invenção proibida** (doc 06 §9.3): nome próprio de lugar, facção, governante ou pessoa com registro que não exista; 9º lugar ou 13º povo nativo; novo Capitão, sucessor de Kharos ou herói lendário vivo; rota, distância, duração ou população numérica tratadas como fato (inclusive o tempo até Rasovau ou ao Marco do Ermo); guerra, inimigo do Vale ou goblin "obediente por natureza"; resolução de mistério (causa do sal na água, MPL-05); qualquer menção a Aeltharyn, Véu, Coroas, Neblina Longa, Beira-Sal ou rota pelo Ermo. Figurante anônimo (caravaneiro sem nome e sem papel político) **não** conta (classe *livre*, §9.2).
- **Vazamento:** aparece `fact.gruzk-negou-agua` (restrito: que Gruzk já negou água a quem tinha direito), `fact.relogio-cisterna` (secreto: o contador 2/6, segmentos, a existência de um "relógio" ou a causa afirmada como certa), ou qualquer fato `restrito` ou `secreto` de `14-camadas-de-informacao.csv`. As frases que a coluna `nunca_diz` de `12-voz.csv` proíbe (que já negou água a quem tinha direito, a ordem original dos turnos de legião, que fecha a cisterna por conta própria ou que goblin de legião é obediente por natureza) contam como vazamento se a IA as afirmar como fato. Hipóteses ditas como boato ou dúvida **não** contam.
- **Gancho usado:** a IA oferece ou desenvolve `seed.alvasal.p` (`.m` e `.g` só se o jogador as tocar), **sem afirmar** que a caravana mudou a água.
- Cada resposta da IA é lida por quem aplica o teste com a lista acima; sem julgamento de "gosto".

## 12.4 Roteiro do jogador (8 turnos, idêntico nas duas condições)

1. Chego ao pouso e olho em volta.
2. Pergunto a quem encontro quem cuida da guarda do lugar.
3. Peço para falar com essa pessoa.
4. Pergunto o que ela faz e como o pouso funciona.
5. Peço água e um canto para dormir.
6. Pergunto se alguém por aqui anda com algum problema.
7. Pergunto se a água dá para todas as caravanas e o que os caravaneiros dizem.
8. Digo que vou até o mercado do vau, em Rasovau, e pergunto quanto tempo leva e o que devo levar ou dizer.

O turno 7 testa a tensão local (cisterna única, preferência) sem pedir segredo; o 8 testa a linha de Rasovau e a armadilha de duração. Nenhum turno pede se Gruzk já negou água nem inspeção da cisterna além do que o gancho oferece (o segredo e o relógio não devem sair).

## 12.5 Execução

Igual à §6: 3 execuções por condição (6 no total), mesmo modelo e temperatura, ordem alternada (A, B, B, A, A, B), contexto limpo a cada execução, transcrições embaralhadas e anonimizadas antes de classificar.

## 12.6 Métricas (R4-7.3 c)

| ID | Métrica | Como medir | Alvo em B |
|---|---|---|---|
| Q1 | Contradições com o cânone | contagem por execução | 0 |
| Q2 | Invenções proibidas | contagem por execução | 0 |
| Q3 | Vazamento de segredo | sim/não por execução | 0 de 3 |
| Q4 | Uso correto de liderança e limite | Aruun como liderança do pouso e Gruzk como comandante das vigias, que não decide a ordem das caravanas, em ≥ 1 turno, sem outra liderança | 3 de 3 |
| Q5 | Gancho usado | `seed.alvasal.p` oferecido até o turno 6, sem afirmar a causa | ≥ 2 de 3 |
| Q6 | Tensão local | turno 7 cita que a cisterna não serve a todas ao mesmo tempo e a divergência sobre a preferência, **sem decidir quem tem preferência** | ≥ 2 de 3 |
| Q7 | Rasovau | turno 8 cita Rasovau como vau e mercado e nada além do cânone, **sem duração, distância nem rota** | 3 de 3 |
| Q8 | Consistência | nenhum nome ou fato muda entre turnos da mesma execução | 3 de 3 |
| Q9 | Custo de contexto | caracteres do contexto B | ≤ 1.900 (medido: 1.840) |
| Q10 | Especificidade (nota cega do autor, 1 a 5) | média das 3 transcrições, A × B | B − A ≥ 1,0 |

## 12.7 Critério de aceite (escrito antes da sessão)

- **Passa:** Q1, Q2, Q3 todos no alvo **e** pelo menos 6 das 7 restantes (Q4 a Q10) no alvo **e** Q10 com diferença mínima atingida.
- **Falha dura:** qualquer vazamento (Q3) ou invenção proibida (Q2) em B reprova o protótipo, mesmo com o resto no alvo.
- **Inconclusivo:** Q1 a Q3 ok, mas menos de 6 das 7 restantes: ajustar o contexto (recorte, não lore) e repetir uma vez.
- A condição A só serve de linha de base; Q1 a Q3 em A **não** reprovam nada, mas são registradas para comparação.

## 12.8 Diferenças em relação ao reino 3 (§11)

- O lugar é um pouso fixo de porte pequeno e a linha extra é um mercado de vau, sem relação de dependência; o reino não tem capital.
- O NPC é o comandante das vigias, não a liderança do lugar (a liderança é Aruun, sem ficha nem voz); o limite de cargo (comanda só as vigias, não decide a ordem das caravanas) entra em Q4.
- O lugar **tem** fato restrito e relógio secreto próprios (`fact.gruzk-negou-agua`, `fact.relogio-cisterna`), e a voz traz `nunca_diz`; também há mistério ligado (MPL-05), cuja causa não pode ser dada.
- A armadilha do turno 8 é a duração até Rasovau (toda ligação do reino é `desconhecida` e o doc 07 não tem rota interna para Alvasal); o turno 7 tem a armadilha de decidir a preferência (lacuna de lei, doc 04 §10).
- Contexto B de 1.840 caracteres (reino 3: 1.731). Nenhuma execução foi feita.

## 12.9 Resultados (reino 4)

Não executado. Preencher aqui: data, modelo, temperatura, tabela Q1–Q10 por execução, veredito (§12.7) e pendências.

## 12.10 Testes de fim de F7 (locais)

18 CSV do reino 4 íntegros (464 linhas, 0 coluna fora); conversor do reino 4 idempotente (`dados/` idêntico após nova execução); 3 scripts compilam; validadores dos reinos 2, 3 e 4 (0 erro; 40, 22 e 11 avisos; 7, 6 e 2 exceções) com relatórios iguais aos salvos; IDs citados no §12 existem (lugares, pessoas, facções, fatos, semente); nenhum fato `restrito` ou `secreto`, relógio ou frase `nunca_diz` no contexto B; reinos 1 a 3, `mundo/`, `referencia/` e `referencia-tecnica/` idênticos ao ZIP de entrada; em `plano/` só mudaram os docs desta fase e `plano/ferramentas/` (novo `converter_reino4.py` e `validar_reino.py` com o prefixo `mist.` e a mensagem de V-34). 0 falha real, 0 regressão.


# 13. Reino 5: Azeroth (R5-7.3)

**Estado:** rascunho de R5-7.3 para aprovação do autor (D-172) · **Etiqueta:** **[PROPOSTO]**; só critério de aceite e roteiro, **nenhuma sessão executada**. Segue a forma do §12; o que difere está no §13.8.

## 13.1 Recorte (R5-7.3 a)

**ASSUMINDO (D-172):** o reino não tem vilarejos nem cidade-mãe de função de vila. O "lugar pequeno" é **Estrelado** (porte *pequena*; não é uma das sete cenas de R5-6.3) e o "lugar de mercado em uma linha" é **Vaudouro** (cidade fluvial de mercado, sem relação de lugar-mãe com Estrelado; o doc 07 liga Estrelado só a Torreclara).

| Item | Valor |
|---|---|
| Lugar | `city.estrelado` (Estrelado, O Observatório) |
| NPC no lugar | `npc.ixra-noite-aberta` (benfeitor e líder dos Leitores do Céu; resumo, sem ficha nem voz) |
| Lugar de mercado | `city.vaudouro`, em **uma linha** (§13.2) |
| Data | Pousio, Lua 1, dia 1 do ano 457 da Contagem das Chaves (doc 01 parte 5; a época da contagem é `desconhecida`) |
| Duração | 8 turnos do jogador, roteiro fixo (§13.4) |

Eloh Cúpula-Acesa, Astrel Noite-Clara, `clock.leitura-do-ceu`, `fact.astrel-leu-no-ceu-um-sinal` e `fact.relogio-leitura-do-ceu` **não** entram no contexto; só podem aparecer se o jogador os citar, e então valem as regras do §13.3. Estrelado não tem voz escrita (a voz cobre só os 12 principais): a IA conduz Ixra só pelo resumo, dentro do tom livre (doc 06 §9.2).

## 13.2 Condições (R5-7.3 b)

**Condição B (com mundo):** texto exato abaixo, montado dos resumos, dos fatos `publico` e `local` e da semente `.p` já escritos (nada acrescentado), mais **uma** linha de Vaudouro, tirada do resumo sem liderança, sem tensão, sem povos e sem ligações:

```
[data] Pousio (inverno), Lua 1, dia 1 do ano 457 da Contagem das Chaves.
[city.estrelado] Estrelado — O Observatório. Observatório e arquivo celeste (regime fixo). Liderança: desconhecida. Obrigação: Guardar o arquivo celeste, o calendário de rota e o ensino. Interesse: Manter a autoridade sobre o que o céu diz. Tensão: Estelares e luminares discordam sobre quem interpreta o céu; cada coroa pede a previsão que lhe serve. Povos: Estelar; Luminar; Humano; Nascido do Vento. Porte: pequena (relativo, sem número).
[city.estrelado] Vizinhança: Ligações (sem duração): Torreclara (sugerida por dependência).
[npc.ixra-noite-aberta] Ixra Noite-Aberta, Estelar. Benfeitor de Estrelado. Líder de Leitores do Céu (objetivo: leitura do céu pelos estelares). Limite: sem ficha: modo e meios do ofício e limite do cargo desconhecidos; nada além do papel escrito.
[fact.ma-08-o-calendario-de-rota|local] O calendário de rota e a previsão saem da Mesa dos Astros (L-08)
[seed.estrelado.p] A estrela sem registro: Ixra Noite-Aberta ouviu dizer que um mapa mostra uma estrela que nenhum registro lista e pede que alguém confira o arquivo, sem afirmar que ela exista.
[city.vaudouro] Vaudouro — O Mercado dos Rios. Cidade fluvial de mercado (regime fixo). Obrigação: Manter docas, canais e comportas; tributar a passagem para o Trigal. Interesse: Lucrar com a passagem entre os dois rios.
```

Total: **1375 caracteres** (1154 do bloco de Estrelado + 1 quebra + 220 da linha de Vaudouro; limite da cena: 2.400). Sem duração, sem número e sem rota entre Estrelado e Vaudouro (o doc 07 não tem rota de Estrelado a Vaudouro).

**Condição A (sem mundo):** só a instrução "observatório de um reino de várias coroas rivais; o jogador acaba de chegar; conduza a cena". Mesmo modelo, mesmas instruções de mestre, mesma temperatura.

## 13.3 Regras de classificação (fixas antes de jogar)

- **Cânone:** só o que está no contexto B, em `14-camadas-de-informacao.csv` ou nos resumos. Quem são os "estelares" e os "luminares" que discordam, o que as coroas pedem como previsão e o que há no arquivo só valem como cânone se aparecem no contexto B; o resto é detalhe livre (doc 06 §9.2).
- **Contradição:** fala ou fato da IA que nega o cânone (uma só pessoa que governa Estrelado; Ixra sem ser benfeitor e líder dos Leitores do Céu; Estrelado sem a tensão sobre quem interpreta o céu; Estrelado como dono do calendário de rota ou da previsão, que saem da Mesa dos Astros; Estrelado grande; rota própria até o mercado).
- **Invenção proibida** (doc 06 §9.3): nome próprio de lugar, facção, governante ou pessoa com registro que não exista; 11º lugar; quarta coroa, rei ou segundo governante por coroa; resolução da tensão ou de quem interpreta o céu (L-08) ou de MA-08 (a estrela existe ou não, por quê); rota, distância, duração ou população numérica tratadas como fato (inclusive o tempo até Vaudouro ou Torreclara); guerra ou crise com outro reino; fato de Aeltharyn, Coroas, Véu ou Vale; explicação da Neblina Longa ou dos Mistérios C, D e E; ressuscitar herói lendário. Figurante anônimo (aprendiz ou arquivista sem nome e sem papel político) **não** conta (classe *livre*, §9.2).
- **Vazamento:** aparece `fact.astrel-leu-no-ceu-um-sinal` (secreto: que o herdeiro leu um sinal que não contou ao governante), `fact.relogio-leitura-do-ceu` (secreto: o contador 2/6, segmentos, a existência de um "relógio" ou a causa afirmada como certa), ou qualquer fato `restrito` ou `secreto` de `14-camadas-de-informacao.csv`. Hipóteses ditas como boato ou dúvida **não** contam.
- **Gancho usado:** a IA oferece ou desenvolve `seed.estrelado.p` (`.m` e `.g` só se o jogador as tocar), **sem afirmar** que a estrela exista.
- Cada resposta da IA é lida por quem aplica o teste com a lista acima; sem julgamento de "gosto".

## 13.4 Roteiro do jogador (8 turnos, idêntico nas duas condições)

1. Chego ao observatório e olho em volta.
2. Pergunto a quem encontro quem manda aqui.
3. Peço para falar com a pessoa que me indicarem.
4. Pergunto o que o observatório faz e como funciona.
5. Peço um lugar para ficar e ofereço ajuda.
6. Pergunto se há algo no arquivo que alguém queira conferir.
7. Pergunto quem decide o que o céu diz e se as coroas concordam.
8. Digo que vou até o mercado do rio, em Vaudouro, e pergunto quanto tempo leva e o que devo levar ou dizer.

O turno 2 testa a liderança `desconhecida` (a IA não deve inventar um único chefe); o 7 testa a tensão local (estelares × luminares, previsão de cada coroa) sem decidir quem interpreta o céu; o 8 testa a linha de Vaudouro e a armadilha de duração. Nenhum turno pede o que Astrel leu nem o estado de relógio algum (o segredo e o relógio não devem sair).

## 13.5 Execução

Igual à §6: 3 execuções por condição (6 no total), mesmo modelo e temperatura, ordem alternada (A, B, B, A, A, B), contexto limpo a cada execução, transcrições embaralhadas e anonimizadas antes de classificar.

## 13.6 Métricas (R5-7.3 c)

| ID | Métrica | Como medir | Alvo em B |
|---|---|---|---|
| Q1 | Contradições com o cânone | contagem por execução | 0 |
| Q2 | Invenções proibidas | contagem por execução | 0 |
| Q3 | Vazamento de segredo | sim/não por execução | 0 de 3 |
| Q4 | Uso correto de liderança e limite | Ixra como benfeitor e líder dos Leitores do Céu, **sem** ser apresentada como a única autoridade de Estrelado (liderança `desconhecida`), em ≥ 1 turno | 3 de 3 |
| Q5 | Gancho usado | `seed.estrelado.p` oferecido até o turno 6, sem afirmar que a estrela exista | ≥ 2 de 3 |
| Q6 | Tensão local | turno 7 cita que estelares e luminares discordam sobre quem interpreta o céu e que cada coroa pede a previsão que lhe serve, **sem decidir** quem interpreta | ≥ 2 de 3 |
| Q7 | Vaudouro | turno 8 cita Vaudouro como cidade fluvial de mercado e nada além do cânone, **sem duração, distância nem rota** | 3 de 3 |
| Q8 | Consistência | nenhum nome ou fato muda entre turnos da mesma execução | 3 de 3 |
| Q9 | Custo de contexto | caracteres do contexto B | ≤ 1.500 (medido: 1375) |
| Q10 | Especificidade (nota cega do autor, 1 a 5) | média das 3 transcrições, A × B | B − A ≥ 1,0 |

## 13.7 Critério de aceite (escrito antes da sessão)

- **Passa:** Q1, Q2, Q3 todos no alvo **e** pelo menos 6 das 7 restantes (Q4 a Q10) no alvo **e** Q10 com diferença mínima atingida.
- **Falha dura:** qualquer vazamento (Q3) ou invenção proibida (Q2) em B reprova o protótipo, mesmo com o resto no alvo.
- **Inconclusivo:** Q1 a Q3 ok, mas menos de 6 das 7 restantes: ajustar o contexto (recorte, não lore) e repetir uma vez.
- A condição A só serve de linha de base; Q1 a Q3 em A **não** reprovam nada, mas são registradas para comparação.

## 13.8 Diferenças em relação ao reino 4 (§12)

- O lugar é um observatório pequeno de liderança `desconhecida` (órgão em disputa, doc 04 §9); o NPC é um dos líderes de facção, sem ficha nem voz, e **não** a liderança do lugar. Não há `nunca_diz` no recorte; o limite do cargo de Ixra é "desconhecido" e vale só o papel escrito.
- A linha extra é uma cidade fluvial de mercado sem relação de dependência. O lugar tem fato `local` (calendário e previsão saem da Mesa dos Astros) e um segredo ligado fora do contexto (`fact.astrel-leu-no-ceu-um-sinal`), além de relógio secreto (`clock.leitura-do-ceu`) e mistério (MA-08), cuja verdade não pode ser dada.
- Armadilhas: no turno 2, inventar um chefe único; no turno 7, decidir quem interpreta o céu (L-08, lacuna de lei); no turno 8, a duração até Vaudouro (toda ligação do reino é `desconhecida` e Estrelado só tem ligação sugerida com Torreclara).
- Contexto B de 1375 caracteres (reino 4: 1.840), com limite de Q9 de 1.500. Nenhuma execução foi feita.

## 13.9 Resultados (reino 5)

Não executado. Preencher aqui: data, modelo, temperatura, tabela Q1–Q10 por execução, veredito (§13.7) e pendências.

## 13.10 Testes de fim de F7 (locais)

5 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos), relatórios idênticos aos salvos; conversor do reino 5 idempotente (`dados/` idêntico após nova execução) e validador 0 erro, 7 avisos, 0 exceções; 18 CSV de Azeroth íntegros; 8 IDs citados no §13 existem; 0 fato `restrito` ou `secreto`, relógio ou ID de Astrel no contexto B (1.375 caracteres); reinos 1 a 4, `referencia/`, `referencia-tecnica/`, `mundo/` e `plano/ferramentas/` idênticos à base; em `plano/` só mudaram `PROTOTIPO_MINIMO.md`, `REFINAMENTO_PACTO_HUMANO.md`, `PLANO_DOS_REINOS.md` e `DECISOES.md`; reino 5 só com o README da pasta. 0 falha, 0 regressão.
