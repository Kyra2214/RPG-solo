# Vale Palancar — Fechamento das oito fases editoriais (doc 11)

**Estado:** **[APROVADO]** pelo autor em 07/11/2026 (D-137 opção A, D-138) · Ata de fechamento 5 (R4-8.2). Concluído para o escopo narrativo e editorial; reino fechado 5.
**Origem:** `plano/REFINAMENTO_VALE_PALANCAR.md` (Fases 0 a 8), `plano/RELATORIO_R4_8_1.md`, `plano/CONTRATO_DE_FRONTEIRA_PALANCAR.md` e os docs 01, 03 a 10 do reino, `12-*` a `15-*` e `dados/`.
**Limites:** este fechamento é editorial. Nada foi implementado no RPG Solo; o catálogo Kotlin (`referencia-tecnica/`) não tem entidade do Vale e não foi alterado (`plano/MAPA_CATALOGO_KOTLIN.md` §8). Nenhuma sessão de protótipo foi jogada (`plano/PROTOTIPO_MINIMO.md` §12). Aeltharyn, as Coroas de Pedra e o Véu não foram alterados.

## Resultado executivo

O Vale Palancar está fechado como ecossistema narrativo utilizável: um reino de clãs sem rei e sem capital, governado por uma **Assembleia de Clãs** e um **Capitão de Marcha** escolhido por prova ou aclamação (D-102), com **oito lugares**, quatro fixos (Tabuleiro-Velho, Rasovau, Praia-Pardo, Alvasal) e quatro sazonais (Terreiro-Maior, Vazante, Encosta-Aberta, Gramal) (D-105), doze povos nativos de referência, 25 pessoas (12 principais, 6 da tríade sem ficha, 1 líder de facção sem ficha, 6 heróis lendários mortos), 14 facções, 8 relógios iniciais, 12 eventos-tipo, 24 sementes de aventura e 10 mistérios com hipóteses abertas. Os dados estão em `dados/` (formato D-21, 405 registros) e o validador roda com **0 erro**.

Concluído não significa inventar números. Duração de rota, população numérica, estoque e preço continuam **desconhecidos** de propósito (D-63); a sucessão do Capitão também. Nada disso pode ser somado, calculado ou escolhido.

## Fase 1 — Lugares, rede política, NPCs, instituições, história, povos e mistérios

**Concluída.**
- Exatamente oito lugares, quatro fixos e quatro sazonais; não há capital nem rei.
- Doze principais com ficha de 17 campos (2 generais: Kharos Casco-de-Marcha e Gruzk Estandarte-Baixo; 2 heróis vivos: Tessun Salto-Calado e Rukhan Retorno-Certo), tríade herói, guardião e benfeitor nos oito lugares e 6 heróis lendários, todos **mortos** e sem ressurreição.
- Governo, justiça, pactos (Tratos) e 14 facções em `04`; história em 4 eras, nome "Palancar", leis, economia e fluxos em `01` e `05`; dez mistérios com "a verdade não pode ser".
- Doze povos nativos e 25 forasteiros (M-05), todos com ID do catálogo; linhagens do Vale distintas das de Aeltharyn (P-3).

## Fase 2 — Registro mestre, IDs e alinhamento

**Concluída.** Registro mestre (`12-registro-mestre`, 47 entidades: 8 lugares, 14 facções, 25 pessoas), IDs únicos, alinhamento ao catálogo (`ALINHAMENTO_PALANCAR.md`) e conferência cruzada (CI-01 a CI-12).

## Fase 3 — Rotas, demografia, recursos e calendário

**Concluída em escala qualitativa.**
- 9 ligações no doc 07 (6 entre lugares e 3 de acesso), todas com duração `desconhecida`; nenhuma rota inventada. Alvasal sem rota interna (exceções V-23 e V-44). Não há rota, tempo, distância ou passagem nova entre o Vale e Aeltharyn, as Coroas, o Véu ou o Pacto Humano.
- População só em faixas relativas (2 grandes, 3 médias, 3 pequenas), sem número; recursos em "rica em", "pobre em" e "depende de", 12 dependências (8 críticas) e 4 fluxos.
- Calendário: 4 estações, 5 Dias de Marco e Contagem dos Marcos; início em Pasto Magro, Lua 1, dia 1 do ano 611 (= ano 742 de Aeltharyn, ASSUMINDO). Nenhum relógio anda por calendário.

## Fase 4 — Fichas, competências, relações, conhecimento e voz

**Concluída.** 25 pessoas com raça, sub-raça, classe e profissão do catálogo; 81 relações; 66 linhas de conhecimento (25 de 25 pessoas); voz dos 12 principais com tom, frases-modelo e `nunca_diz`. Rumor ou suspeita não vira fato.

## Fase 5 — Relógios, eventos, sementes, reação ao jogador e mistérios

**Concluída como estrutura narrativa; as soluções dos mistérios permanecem abertas.** Oito relógios (progresso inicial 2 ou 3 de 6) são estados e possibilidades causais, não cronômetros, todos com gatilho observável. 12 eventos-tipo (8 completam relógio, 2 dão gatilho, 2 sem relógio); 24 sementes (8 P, 8 M, 8 G) sem solução predefinida; reação por entidade (22 reputações, 11 canais). Mistérios MPL-01 a MPL-10: 4 hipóteses abertas cada; nenhum explica a Neblina Longa nem toca os Mistérios D e E ou o Conflito G. O protagonista pode ignorar, resolver, agravar ou testemunhar os eventos.

## Fase 6 — Resumos, camadas, cenas e contrato anti-invenção

**Concluída.** 55 resumos de até 500 caracteres; 42 fatos em quatro camadas (11 públicos, 11 locais, 6 restritos, 14 secretos), com caminho de descoberta; 7 cenas de teste (maior "entra": 2.140 caracteres); contrato anti-invenção no doc 06 §9 (fixo, livre, exige registro, proibido).

## Fase 7 — Dados, catálogo técnico e protótipo

**Concluída.** Dados e validador (`plano/ferramentas/converter_reino4.py`, `validar_reino.py`): 0 erro, 11 avisos, 2 exceções. O `.kt` não tem entidade do Vale e não colide com nenhum ID. Protótipo especificado (Alvasal, Gruzk Estandarte-Baixo e Rasovau em uma linha; contexto B de 1.840 caracteres; métricas Q1 a Q10), **não jogado**.

## Fase 8 — Revisão final

**Concluída.** `plano/RELATORIO_R4_8_1.md`: portas 1 a 10 marcadas; a porta 11 é esta ata. Testes de fim de F8 (locais, sem rede): 18 CSV do reino 4 íntegros (464 linhas, 0 coluna fora); conversor idempotente; 3 scripts compilam; validadores dos reinos 2, 3 e 4 com 0 erro (40, 22 e 11 avisos; 7, 6 e 2 exceções), iguais aos relatórios salvos; contagens deste doc conferem com os dados; `reinos/`, `mundo/`, `referencia/` e `referencia-tecnica/` sem alteração pelos testes. 0 falha, 0 regressão.

## Portas (`MODELO_DE_REINO.md` §6)

**11 de 11 marcadas** (1, 3 e 6 com ressalva ou lacuna explícita, abaixo). Nenhuma porta depende de decisão do autor para ser marcada; D-136 trata só das lacunas de rival.

## Contradições intencionais registradas

1. **Mistérios MPL-01 a MPL-10 e relógios:** ambiguidade deliberada; nenhuma hipótese escolhida.
2. **Ligações sem duração** e ausência de rota do Vale com Aeltharyn, Coroas, Véu e Pacto Humano.
3. **Alvasal sem rota interna** e grafo em dois grupos (`dados/excecoes.txt`: V-23 e V-44).
4. **Reino sem rei e sem capital;** sucessão do Capitão `desconhecida` (V-34).
5. **Oito lugares, quatro fixos e quatro sazonais** (V-30; D-102 e D-105).
6. **Seis heróis lendários mortos,** sem ressurreição; lenda existe, feito `desconhecido`.
7. **Linhagens do Vale distintas das de Aeltharyn** com nomes de povo iguais (P-3); 4 forasteiros compartilhados com o `.kt`.
8. **"Neblina" e pactos do `.kt`** são de Aeltharyn; o Vale não os explica nem os herda.
9. **Rival `desconhecido` em 2 facções** (Guardas da Marcha, Vigias da Cisterna) e 7 pessoas sem ficha: lacunas explícitas.
10. **Fonte `proposto`** em 8 lugares, 14 facções e 25 pessoas (V-06).

**Acidentais tratadas:** nenhuma achada na revisão (R4-8.1); os ajustes de conversão do R4-7.1 (capital `—`, prefixo `mist.`, recursos de evento com papel `?`) não são erro de lore. Nenhum fato mudou.

## Decisões em aberto (não bloqueiam o fechamento, ver `plano/DECISOES.md`)

D-102 e D-103 foram decididas pelo autor; D-104 a D-136 foram aceitas pela continuação ("Siga"), reversíveis. D-137 (esta ata) foi aprovada pelo autor em 07/11/2026 (D-138). Também em aberto como limite explícito: rival `desconhecido` em 2 facções (D-136); duração de toda ligação; população numérica; sucessão do Capitão de Marcha; preferência na cisterna (lacuna de lei, D-135); protótipo não jogado (D-135); P-4 (Lessa Cervo-Manso sem ID no registro de Aeltharyn, lacuna herdada).

## Critérios finais de uso

- Manter exatamente oito lugares (quatro fixos, quatro sazonais) e doze povos nativos de referência; sem capital, sem rei e sem assentamento além dos oito.
- Não somar população nem calcular total do reino; não inventar duração, distância, estoque, preço ou rota (escala qualitativa, D-63).
- Mestiços não são alocados a lugar, facção ou reino (D-64): nascem no lugar de qualquer um dos pais, sem lista fechada, e o jogador escolhe.
- Governo só pela Assembleia de Clãs e pelo Capitão de Marcha; não decidir a sucessão nem nomear herdeiro.
- Não ressuscitar heróis lendários, declarar guerra, resolver mistério, criar 9º lugar ou 13º povo nativo.
- Não avançar relógio sem gatilho observável; não revelar fato restrito ou secreto sem o caminho cumprido.
- Não afirmar nada sobre Aeltharyn, as Coroas ou o Véu além do contrato de fronteira; não explicar a Neblina Longa nem tocar os Mistérios D e E ou o Conflito G.
- O reino 5 (Pacto Humano) foi liberado em 07/11/2026 (D-138); nada do reino 4 afirma o que ele ainda não escreveu.
- Não alterar código, APK, `main`, serialização, testes ou CI como parte do fechamento editorial.

**Conclusão:** as oito fases do Vale Palancar estão concluídas. Os desconhecidos restantes são limites explícitos de evidência, não pendências que exijam inventar fatos.

## Ata de fechamento 5 (R4-8.2, 07/11/2026)

**Estado:** conteúdo editorial revisado (`plano/RELATORIO_R4_8_1.md`); dados em `dados/` e CSVs `12-*` a `15-*`; protótipo especificado e não jogado.

**Portas:** 11 de 11 marcadas.

**Liberação do reino 5 (Pacto Humano):** **liberado** pelo autor em 07/11/2026 ("Siga reino 5", D-138). As decisões em aberto passam a limite explícito (aviso, não erro, no validador); nada do Vale, do Véu, das Coroas ou de Aeltharyn é alterado pela aprovação. O plano do reino 5 está em `plano/REFINAMENTO_PACTO_HUMANO.md`.

- [x] Autor aprova o fechamento 5 e libera o reino 5 (D-137, D-138; 07/11/2026).
