# Coroas de Pedra — Fechamento das oito fases editoriais (doc 11)

**Estado:** **[APROVADO]** pelo autor em 05/11/2026 (D-62, opção A) · Ata de fechamento 3 (R2-8.2). Concluído para o escopo narrativo e editorial.
**Origem:** `plano/REFINAMENTO_COROAS_DE_PEDRA.md` (Fases 0 a 8), `plano/RELATORIO_R2_8_1.md` e os docs 01, 03 a 10 do reino, `12-*` a `15-*` e `dados/`.
**Limites:** este fechamento é editorial. Nada foi implementado no RPG Solo; o catálogo Kotlin (`referencia-tecnica/`) só tem Aeltharyn e não foi alterado (`plano/MAPA_CATALOGO_KOTLIN.md` §5). Nenhuma sessão de protótipo foi jogada (`plano/PROTOTIPO_MINIMO.md` §10). Aeltharyn não foi alterado.

## Resultado executivo

As Coroas de Pedra estão fechadas como ecossistema narrativo utilizável: uma confederação de dez cidades governada pelo **Concílio das Coroas** (assento rotativo, D-26), vinte assentamentos, doze povos nativos de referência, 45 pessoas (12 principais, 20 lideranças, 13 heróis lendários mortos), 14 facções, 7 relógios iniciais, 13 eventos-tipo, 90 sementes de aventura e 6 mistérios com hipóteses abertas. Os dados estão em `dados/` (formato D-21, 592 registros) e o validador roda com **0 erro**.

Concluído não significa inventar números. Duração de rota, população numérica, estoques e origem do combustível, do minério e do carvão continuam **desconhecidos** de propósito; nada disso pode ser somado ou calculado.

## Fase 1 — Cidades, assentamentos, NPCs, instituições, história, povos e mistérios

**Concluída.**
- Exatamente dez cidades: Brasalume, Altavento, Veiaclara, Barcaalta, Portão-Cinza, Sinuosa, Cumieira, Rodaviva, Geodália e Pedra-Ata. Pedra-Ata guarda o arquivo e recebe as delegações, mas não manda nas outras (C-10). Capital formal não existe; no esquema de dados `reino.capital` = Pedra-Ata (ASSUMINDO, D-58).
- Vinte assentamentos (S-01 a S-20), com liderança, obrigação, interesse e tensão.
- Doze principais (N-01 a N-12) com ficha de 17 campos; 20 lideranças e 13 heróis lendários, todos **mortos** e sem ressurreição.
- Governo, justiça, pactos, leis L-01 a L-10 e facções em `04`; história, cronologia relativa e defesa na parte 3 do doc 01; seis mistérios com "a verdade não pode ser" na parte 4.
- Doze povos nativos, todos com ID do catálogo; anões e gnomos estão entre eles. Os 17 forasteiros de M-05 não aumentam a contagem de nativos.

## Fase 2 — Registro mestre, IDs e alinhamento

**Concluída.** Registro mestre (`12-registro-mestre`), IDs únicos e alinhamento ao catálogo (`ALINHAMENTO_COROAS.md`). Classe e profissão são leitura editorial (D-39). Cargos duplicados de facção e assentamento são intencionais (D-40).

## Fase 3 — Rotas, população, recursos e calendário

**Concluída em escala qualitativa.**
- 13 rotas, todas com duração `desconhecida`; nenhuma rota inventada. Não há rota, tempo, distância ou passagem nova entre as Coroas e Aeltharyn (contrato de fronteira §3.3).
- População só em faixas relativas (pequena, média, grande), sem número e sem total do reino; recursos com nível provisório e evidência marcada.
- Calendário: 12 luas, 4 estações de montanha (Gelo Fechado, Degelo, Passo Aberto, Antes-da-Neve), 5 Dias de Balanço; início em Gelo Fechado, Lua 1, dia 1 do ano 516 das Atas (= ano 742 de Aeltharyn). Nenhum relógio anda por calendário.

## Fase 4 — Fichas, competências, relações, conhecimento e voz

**Concluída.** 45 pessoas com raça, sub-raça, classe e profissão do catálogo; 91 relações (nenhuma de afeto, porque nenhum documento a escreve); 82 linhas de conhecimento (confirmado, suspeita e desconhecido); voz dos 12 principais como instrução de tom. Conhecimento de NPC depende de fonte e posição; rumor ou suspeita não vira fato.

## Fase 5 — Relógios, eventos, sementes, reação ao jogador e mistérios

**Concluída como estrutura narrativa; as soluções dos mistérios permanecem abertas.**

Os sete relógios são estados e possibilidades causais, não cronômetros. Antes de uma mudança, verificar gatilho observável, ligação causal, localidade afetada, recursos e rotas, alternativas, conhecimento de cada agente e próxima decisão.
- **CL-01 Combustível de Brasalume:** a origem do combustível é desconhecida.
- **CL-02 Comportas e degelo de Veiaclara.**
- **CL-03 Cais saturado e registros em conflito.**
- **CL-04 Livro de Passagens e viajantes sem registro.**
- **CL-05 Fim do ciclo do assento:** nenhuma ordem de presidência é fixada.
- **CL-06 Peças raras e foro dos Despertos.**
- **CL-07 Nevasca e passos fechados:** a estação sozinha não é gatilho.

13 eventos-tipo com cadeia causal; 90 sementes (pequena, média e grande por cidade e assentamento) sem solução predefinida; reação por entidade em termos qualitativos (sem número de fama, preço ou prazo). Mistérios MP-01 a MP-06: 4 hipóteses abertas cada; nenhum escolhe hipótese nem toca o Mistério D de Aeltharyn.

O protagonista pode ignorar, resolver, agravar ou testemunhar os eventos. NPCs só agem fora de cena com conhecimento, recursos, autoridade e oportunidade plausíveis.

## Fase 6 — Resumos, camadas, cenas e contrato anti-invenção

**Concluída.** 96 resumos de até 500 caracteres; 41 fatos em quatro camadas (12 públicos, 10 locais, 5 restritos, 14 secretos), com caminho de descoberta; 6 cenas de teste com "entra" de até 2.400 caracteres; contrato anti-invenção no doc 06 §9 (fixo, livre, exige registro, proibido).

## Fase 7 — Dados, catálogo técnico e protótipo

**Concluída.** Dados no formato D-21 e validador (`plano/ferramentas/`): 0 erro, 40 avisos, 7 exceções. O `.kt` não tem nenhuma entidade das Coroas e não colide com nenhum ID. Protótipo especificado (Rastro-Branco, Miraq e Altavento em uma linha; 1.192 caracteres; métricas Q1 a Q10), **não jogado**.

## Fase 8 — Revisão final

**Concluída.** `plano/RELATORIO_R2_8_1.md`: portas 1 a 10 marcadas; a porta 11 é esta ata.

## Portas (`MODELO_DE_REINO.md` §6)

**11 de 11 marcadas** (3 e 6 com ressalva ou lacuna explícita, abaixo). Nenhuma porta depende de decisão do autor para ser marcada; D-61 trata só das lacunas de aliado e rival.

## Contradições intencionais registradas

1. **Mistérios MP-01 a MP-06 e relógios:** ambiguidade deliberada; nenhuma hipótese escolhida.
2. **Rotas sem duração** e ausência de rota Coroas–Aeltharyn.
3. **Maur só fala em particular;** os 13 heróis lendários seguem mortos.
4. **Cargos duplicados** de facção e assentamento (D-40): a facção nasce da autoridade local.
5. **7 exceções do validador** (`dados/excecoes.txt`): 6 eventos que citam ligações do doc 07 sem dois locais existentes e a rota Altavento–Pedra-Ata.

**Acidentais tratadas:** colisão de 5 IDs de canal com Aeltharyn (R2-7.1); contagem de nativos no contrato de fronteira §2 (R2-8.1); status de D-51 a D-53 no `DECISOES.md` (R2-8.2: passam a "aceita pela continuação", como já constavam no plano, README e CHANGELOG).

## Decisões em aberto (não bloqueiam o fechamento, ver `plano/DECISOES.md`)

D-38 a D-50 e D-54 a D-62 aguardam o autor. D-34 a D-37 e D-51 a D-53 foram aceitas pela continuação ("Siga"), reversíveis. Também em aberto como limite explícito: aliados `?` em 1 facção e rivais `?` em 4 (D-61); origem do combustível, minério e carvão; duração de toda rota; população numérica.

## Critérios finais de uso

- Manter exatamente dez cidades, vinte assentamentos e doze povos nativos de referência.
- Não somar população nem calcular total do reino; não inventar duração, distância, estoque ou rota (escala qualitativa, D-63).
- Mestiços não são alocados a cidade, facção ou reino (D-64): nascem na cidade de qualquer um dos pais, sem lista fechada, e o jogador escolhe.
- Não escolher quem preside o Concílio depois de Velka Selo-Firme nem qual cidade recebe o assento primeiro.
- Não ressuscitar heróis lendários, declarar guerra, resolver mistério ou criar 11ª cidade ou 13º povo nativo.
- Não avançar relógio sem gatilho observável; não revelar fato restrito ou secreto sem o caminho cumprido.
- Não afirmar nada sobre Aeltharyn além do contrato de fronteira; não tocar o Mistério D, a mina selada ou Torvelume.
- Reino 3 liberado em 05/11/2026 (D-65); não escrever conteúdo dos reinos 4 e 5 enquanto o autor não aprovar.
- Não alterar código, APK, `main`, serialização, testes ou CI como parte do fechamento editorial.

**Conclusão:** as oito fases das Coroas de Pedra estão concluídas. Os desconhecidos restantes são limites explícitos de evidência, não pendências que exijam inventar fatos.

## Ata de fechamento 3 (R2-8.2, 05/11/2026)

**Estado:** conteúdo editorial revisado (`plano/RELATORIO_R2_8_1.md`); dados em `dados/` e CSVs `12-*` a `15-*`; protótipo especificado e não jogado.

**Portas:** 11 de 11 marcadas.

**Liberação do reino 3 (Véu dos Malditos):** **liberado em 05/11/2026** (D-65). O autor aprovou esta ata com as decisões acima em aberto, que passam a limite explícito (aviso, não erro, no validador). Nada das Coroas de Pedra ou de Aeltharyn foi alterado pela aprovação.

- [x] Autor aprova o fechamento 3 e libera o reino 3 (05/11/2026, D-62 e D-65).
