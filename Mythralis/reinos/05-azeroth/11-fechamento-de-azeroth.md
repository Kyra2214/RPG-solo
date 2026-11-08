# Azeroth — Fechamento das oito fases editoriais (doc 11)

**Estado:** **[PROPOSTO]** · Ata de fechamento 6 (R5-8.2), para aprovação do autor (D-174). Concluído para o escopo narrativo e editorial; reino em **fechado 6** só com a aprovação.
**Origem:** `plano/REFINAMENTO_PACTO_HUMANO.md` (Fases 0 a 8), `plano/RELATORIO_R5_8_1.md`, `plano/CONTRATO_DE_FRONTEIRA_PACTO_HUMANO.md` e os docs 01, 03 a 10 do reino, `12-*` a `15-*` e `dados/`.
**Limites:** este fechamento é editorial. Nada foi implementado no RPG Solo; o catálogo Kotlin (`referencia-tecnica/`) não tem entidade de Azeroth e não foi alterado (`plano/MAPA_CATALOGO_KOTLIN.md` §9). Nenhuma sessão de protótipo foi jogada (`plano/PROTOTIPO_MINIMO.md` §13). Aeltharyn, as Coroas de Pedra, o Véu e o Vale não foram alterados.

## Resultado executivo

Azeroth está fechado como ecossistema narrativo utilizável: um reino de **três coroas rivais** (Trigal, Cais e Vento) e uma cidade livre, **Meiovau**, onde a Mesa das Três Chaves arbitra sem decidir a precedência (D-141). Tem **dez lugares** (Campoalvo, Vaudouro e Torrefronte no Trigal; Alvamar, Enseada-Vigia e Concharia no Cais; Torreclara, Estrelado e Pouso-das-Brisas no Vento; Meiovau), nove povos nativos, 38 pessoas (12 principais, 18 sem ficha de principal, 8 heróis lendários falecidos), 15 facções, 10 relógios iniciais, 12 eventos-tipo, 30 sementes de aventura e 10 mistérios com hipóteses abertas. Os dados estão em `dados/` (formato D-21, 485 registros) e o validador roda com **0 erro**.

Concluído não significa inventar números. Duração de rota, população numérica, estoque e preço continuam **desconhecidos** de propósito (D-63); a precedência entre as coroas, a sucessão e o crime grave também.

## Fase 1 — Lugares, rede política, NPCs, instituições, história, povos e mistérios

**Concluída.**
- Dez lugares fixos sob três coroas e Meiovau; cada coroa com um governante e um herdeiro reconhecido (Odile Medida-Cheia e Tomlin Horta-Larga; Corlan Cais-Primeiro e Rilla Espuma-Fina; Brevo Rumo-Alto e Astrel Noite-Clara); Tolla Mesa-Posta guarda as três chaves e preside a Mesa sem voto.
- Doze principais, tríade (herói, guardião, benfeitor) nos dez lugares e 8 heróis lendários, todos **falecidos** e sem ressurreição.
- Governo, justiça, pactos e 15 facções em `04`; história, leis (L-01 a L-10), economia e fluxos em `01` e `05`; dez mistérios com "a verdade não pode ser".
- Nove nativos, oito sub-raças e 25 forasteiros (M-05), todos com ID do catálogo; linhagens próprias, sem parentesco automático com as de Aeltharyn (P-4).

## Fase 2 — Registro mestre, IDs e alinhamento

**Concluída.** Registro mestre (`12-registro-mestre`, 63 entidades: 10 lugares, 15 facções, 38 pessoas), IDs únicos, alinhamento ao catálogo (`ALINHAMENTO_AZEROTH.md`) e conferência cruzada (CI-01 a CI-12). Achado acidental da fase (nove da tríade fora da lista declarada de líderes) corrigido em D-152.

## Fase 3 — Rotas, demografia, recursos e calendário

**Concluída em escala qualitativa.**
- 14 ligações no doc 07 (10 documentadas, 4 sugeridas por dependência), todas com duração `desconhecida`; nenhuma rota inventada. Não há rota, tempo, distância ou passagem nova entre Azeroth e Aeltharyn, as Coroas, o Véu ou o Vale; só As Costas e As Fugas do Véu, do lado de cá.
- População só em faixas relativas (2 grandes, 4 médias, 4 pequenas), sem número; recursos em "rica em", "pobre em" e "depende de", 24 dependências (17 críticas) e 4 fluxos.
- Calendário: 12 luas, 4 estações (Pousio, Sementeira, Ceifa, Ventania), 5 Dias de Cruzamento e Contagem das Chaves; ano 457 = ano 742 de Aeltharyn (ASSUMINDO). Nenhum relógio anda por calendário.

## Fase 4 — Fichas, competências, relações, conhecimento e voz

**Concluída.** 38 pessoas com raça, sub-raça, classe e profissão do catálogo; 91 relações; 88 linhas de conhecimento (38 de 38 pessoas); voz dos 12 principais com tom, frases-modelo e `nunca_diz`. Rumor ou suspeita não vira fato.

## Fase 5 — Relógios, eventos, sementes, reação ao jogador e mistérios

**Concluída como estrutura narrativa; as soluções dos mistérios permanecem abertas.** Dez relógios (progresso inicial 2/6 ou 3/6) são estados e possibilidades causais, não cronômetros, todos com gatilho observável. 12 eventos-tipo (10 completam relógio, 2 dão gatilho); 30 sementes (10 P, 10 M, 10 G) sem solução predefinida; reação por entidade (25 reputações, 11 canais). Mistérios MA-01 a MA-10: 4 hipóteses abertas cada; nenhum explica a Neblina Longa nem toca os Mistérios D e E ou o Conflito G. O protagonista pode ignorar, resolver, agravar ou testemunhar os eventos.

## Fase 6 — Resumos, camadas, cenas e contrato anti-invenção

**Concluída.** 73 resumos de até 500 caracteres; 44 fatos em quatro camadas (10 públicos, 12 locais, 2 restritos, 20 secretos), com caminho de descoberta; 7 cenas de teste (maior "entra": 2.103 caracteres); contrato anti-invenção no doc 06 §9 (fixo, livre, exige registro, proibido).

## Fase 7 — Dados, catálogo técnico e protótipo

**Concluída.** Dados e validador (`plano/ferramentas/converter_reino5.py`, `validar_reino.py`): 0 erro, 7 avisos, 0 exceções. O `.kt` não tem entidade de Azeroth e não colide com nenhum ID. Protótipo especificado (Estrelado, Ixra Noite-Aberta e Vaudouro em uma linha; contexto B de 1.375 caracteres; métricas Q1 a Q10), **não jogado**.

## Fase 8 — Revisão final e fechamento

**Concluída (a aprovar).** `plano/RELATORIO_R5_8_1.md`: portas 1 a 10 marcadas; a porta 11 é esta ata. **Testes completos de fim de F8** (locais, sem rede): 5 scripts compilam; validadores dos reinos 2, 3 e 4 = 0 erro (40, 22 e 11 avisos), relatórios idênticos aos salvos; conversor do reino 5 idempotente (`dados/` e relatório idênticos após nova execução), validador do reino 5 = 0 erro, 7 avisos, 0 exceções; 18 CSV de Azeroth íntegros; 383 IDs nos cinco registros mestres, 0 colisão; contagens do doc 11 conferem com os registros; arquivos citados no doc 11 existem; reinos 1 a 4, `referencia/`, `referencia-tecnica/` e `mundo/` idênticos à base; em `plano/` só mudaram `PROTOTIPO_MINIMO.md`, `REFINAMENTO_PACTO_HUMANO.md`, `PLANO_DOS_REINOS.md`, `DECISOES.md` e o novo `RELATORIO_R5_8_1.md`; `plano/ferramentas/` intacto. 0 falha, 0 regressão.

## Portas (`MODELO_DE_REINO.md` §6)

**11 de 11 marcadas** (3 com ressalva e 6 com lacuna explícita, abaixo). Nenhuma porta depende de decisão do autor para ser marcada; a decisão do autor é a aprovação desta ata (D-174).

## Conjunto dos cinco reinos (fechamento do anel)

Ordem do anel (M-04 G-02, sentido horário a partir do norte): Coroas de Pedra, Aeltharyn, **Azeroth**, Véu dos Malditos, Vale Palancar, e de novo Coroas de Pedra. Vizinhos plenos de Azeroth: Aeltharyn e o Véu; com as Coroas e o Vale o elo é "nenhum" (contrato §5).

| Reino | Entidades no registro mestre | Lugares | Facções | Pessoas |
|---|---|---|---|---|
| 1 Aeltharyn | 135 | 30 (10 cidades, 20 assentamentos) | 10 | 95 |
| 2 Coroas de Pedra | 89 | 30 (10 cidades, 20 assentamentos) | 14 | 45 |
| 3 Véu dos Malditos | 49 | 8 | 11 | 30 |
| 4 Vale Palancar | 47 | 8 | 14 | 25 |
| 5 Azeroth | 63 | 10 | 15 | 38 |
| **Total** | **383** | | | |

- **IDs únicos entre reinos:** 383 IDs, 0 colisão; 0 nome de lugar, facção ou pessoa repetido entre reinos.
- **Fronteiras:** os reinos 1 a 4 não mencionam "Azeroth" (usam o nome de trabalho antigo, "Pacto Humano", como M-04 e M-05); nenhum deles foi alterado. Azeroth não afirma rota, tempo, passagem, fluxo, guerra nem aliança com os outros quatro; só As Costas e As Fugas do Véu, do lado de cá.
- **Nomes:** M-04, M-05 e os reinos 1 a 4 ainda dizem "Pacto Humano" e "Marcas das Feras"; trocar para "Azeroth" e "Vale Palancar" nesses textos é decisão do autor (não feita aqui, para não alterar reinos fechados).

## Contradições intencionais registradas

1. **Mistérios MA-01 a MA-10 e relógios:** ambiguidade deliberada; nenhuma hipótese escolhida.
2. **Ligações sem duração** e ausência de rota com Aeltharyn, Véu, Coroas e Vale (só As Costas e As Fugas do Véu, lado de cá).
3. **Três coroas rivais** sem precedência decidida (L-01 a L-10); a Mesa arbitra o ponto concreto e não decide a precedência; chave copiada `desconhecida` (CI-02, CI-03).
4. **Concharia e Estrelado sem chefe** (`lideranca_id` `desconhecido`) (CI-04).
5. **Crime grave, exílio e pena de morte** não fixados; sucessão por coroa com desfecho `desconhecido` (CI-05, CI-06).
6. **13 acúmulos de cargo,** intencionais e propostos (CI-07).
7. **Rival `desconhecido`** em 1 facção, Pilotos da Enseada (F-07) (CI-08).
8. **Origem e sentido de "Azeroth"** `desconhecidos`, com duas versões (CI-09).
9. **Lacunas de papel por povo** (3 sem herói, 4 sem guardião, 2 sem benfeitor) e 17 forasteiros sem lugar fixo (CI-10, CI-11).
10. **Oito heróis lendários falecidos,** sem ressurreição; lenda existe, feito `desconhecido`.
11. **Pontos abertos do contrato** P-2 (qual pântano) e P-3 (fronteira com o Véu e foz do Argente) (CI-12); P-5 é lacuna herdada de Aeltharyn (Lessa Cervo-Manso sem ID).
12. **Fonte `proposto`** em lugares, facções e pessoas (V-06) e os 7 avisos esperados do validador.

**Acidentais tratadas:** uma, achada na R5-2.4 e já corrigida (D-152). Nenhuma nova na revisão (R5-8.1).

## Decisões em aberto (não bloqueiam o fechamento, ver `plano/DECISOES.md`)

D-138 (liberação) e a parte do autor de D-141 (governo de várias coroas) foram decididas pelo autor; D-139 (nome) foi escolhido pelo autor; D-140 e D-142 a D-173 foram aceitas pela continuação ("Siga"), reversíveis. D-174 (esta ata) aguarda o autor. Também em aberto como limite explícito: rival `desconhecido` de F-07; liderança de Concharia e Estrelado; precedência entre as coroas e lacunas de lei L-01 a L-10; sucessões; crime grave; origem do nome; P-2 e P-3; duração de toda ligação; população numérica; protótipo não jogado (D-172); D-67 (nomes do Véu).

## Critérios finais de uso

- Manter exatamente dez lugares, três coroas rivais e Meiovau, e nove povos nativos de referência; sem quarta coroa, rei ou segundo governante por coroa.
- Não somar população nem calcular total do reino; não inventar duração, distância, estoque, preço ou rota (escala qualitativa, D-63).
- Mestiços não são alocados a lugar, facção ou reino (D-64): nascem no lugar de qualquer um dos pais, sem lista fechada, e o jogador escolhe.
- A Mesa não decide a precedência e o presidente não vota; não decidir sucessão nem nomear herdeiro.
- Não ressuscitar heróis lendários, declarar guerra, resolver mistério, criar 11º lugar ou 10º povo nativo.
- Não avançar relógio sem gatilho observável; não revelar fato restrito ou secreto sem o caminho cumprido.
- Não afirmar nada sobre Aeltharyn, as Coroas, o Véu ou o Vale além do contrato de fronteira; não explicar a Neblina Longa nem tocar os Mistérios D e E ou o Conflito G.
- Não alterar código, APK, `main`, serialização, testes ou CI como parte do fechamento editorial.

**Conclusão:** as oito fases de Azeroth estão concluídas e o anel dos cinco reinos está escrito. Os desconhecidos restantes são limites explícitos de evidência, não pendências que exijam inventar fatos.

## Ata de fechamento 6 (R5-8.2, 07/11/2026)

**Estado:** conteúdo editorial revisado (`plano/RELATORIO_R5_8_1.md`); dados em `dados/` e CSVs `12-*` a `15-*`; protótipo especificado e não jogado.

**Portas:** 11 de 11 marcadas.

**Anel:** cinco reinos escritos; não há reino 6. A integração ao RPG Solo segue `plano/INTEGRACAO_FUTURA_RPG_SOLO.md` e só começa por decisão do autor.

- [ ] Autor aprova o fechamento 6 (D-174).
