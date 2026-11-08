# 13 — Mistérios com limites (R2-5.5)

**Estado:** rascunho de R2-5.5 para aprovação do autor (D-53) · **Data:** 2026-11-04

**Etiqueta:** tudo é **[PROPOSTO MP-01 a MP-06]**; os mistérios, hipóteses e vetos já estão no doc 01 (parte 4, D-37). Aqui eles viram dados (prefixo `mist.`), com fatos confirmados, evidência e ligação a relógios e eventos. **Fonte:** doc 01 parte 4, §2 e fichas; doc 04 §2.2, §4.3, §5; doc 05 §1; `12-conhecimento.csv`, `12-relogios.csv`, `12-eventos.csv`. Dados em `13-misterios-limites.csv`. Molde: `reinos/01-.../13-misterios-limites.md`.

## Regras

- **Sem resposta única:** cada mistério mantém quatro hipóteses abertas compatíveis com os fatos confirmados. Nenhuma é eleita aqui.
- **Limite da verdade:** *a verdade não pode ser* lista o que contradiria fato confirmado, regra de consistência, lacuna explícita ou o Mistério D de Aeltharyn. É a fronteira de qualquer resposta futura, não uma resposta.
- **Rumor e suspeita não são fato:** rumores (doc 01 §2) e suspeitas das fichas (`12-conhecimento.csv`) aparecem só como pista e estão marcados como tais em *fatos confirmados*.
- **Resolver é opcional:** o mistério pode ser resolvido por NPC fora de cena, piorar ou ficar aberto. Resolver exige **evidência cumulativa** (pelo menos duas provas independentes) e registro da narração; a IA não escolhe a resposta (doc 06 §6).
- **Nenhum resolve, confirma ou nega o Mistério D** (a mina selada de Torvelume) nem liga as Coroas a evento de Aeltharyn (contrato de fronteira §3.1 e §3.6). Os termos de fronteira só aparecem em vetos (*a verdade não pode ser*), como no doc 01.
- Mistérios não têm relógio próprio; as ligações abaixo são só às situações já escritas. As sementes (R2-5.3) e as cadeias (R2-5.2) que tocam um mistério herdam estes limites.
- Itens de *a verdade não pode ser* que vêm de leitura dos textos, e não de frase explícita, são editoriais (ASSUMINDO).

## Mistérios

### `mist.mp-01-numeros-que-nao-fecham` — MP-01 — Os números que não fecham (Veiaclara)

- **Fatos confirmados:** o registro das comportas tem anotações que não batem entre si; o degelo mudou de data; Pelma Gota-Medida registra cada abertura de comporta; alterar registro é falta grave (L-02); a nascente brilhar de leve é rumor, não fato.
- **Hipóteses abertas:** comporta velha que cede sem aviso e foi mal medida; alguém ajustou o registro para esconder um corte de água; a nascente mudou de regime por causa natural; a medição sempre foi imprecisa e só agora alguém contou.
- **A verdade não pode ser:** a mesma causa dos sintomas das árvores-coração de Aeltharyn (Mistério A); algo que mude o regime do Lúmen em Aeltharyn; uma só pessoa culpada por tudo; culpa de Pelma provada só pela suspeita da ficha (se o degelo mudou por causa dela segue desconhecido).
- **Evidência para resolver:** livro de registro da Casa das Comportas; ferrugem das comportas; testemunho de medidores; pelo menos duas provas independentes antes de a narração fixar uma hipótese (doc 06 §6).
- **Entidades:** `npc.pelma-gota-medida`, `npc.pimm-veio-pequeno`, `faction.casa-das-comportas`.
- **Relógio e evento:** `clock.comportas`, `event.registro-provado`, `event.comporta-cede`.
- **Fonte:** doc 01 parte 4 (MP-01), §2.3, N-07; doc 05 §1 (L-02); 12-conhecimento.csv; 12-relogios.csv.

### `mist.mp-02-paginas-e-luzes` — MP-02 — Páginas arrancadas e luzes que não eram tochas (Portão-Cinza)

- **Fatos confirmados:** há mais viajantes sem registro do que antes; páginas faltam no Livro de Passagens; página arrancada ou omissão é delito de custódia (L-04); Thaurak Escama-Fria comanda os vigias; as luzes que um vigia aposentado conta são rumor; por que elas nunca apareceram nos registros é desconhecido.
- **Hipóteses abertas:** contrabando de passagem; alguém protegeu refugiados ou desertores; reflexo ou cristal natural no esporão; um vigia apagou o próprio registro por vergonha.
- **A verdade não pode ser:** ligada à mina selada de Torvelume, nem prova de que não há ligação; vinda de invasão ou de ordem de Aeltharyn; caso de uma só espécie (nenhum povo é suspeito por natureza); fato provado pela suspeita de que Thaurak protege quem arrancou as páginas.
- **Evidência para resolver:** cópias do Livro nas casas de ofício; tarifas pagas; relato de caravaneiros; escala dos vigias; pelo menos duas provas independentes (doc 06 §6).
- **Entidades:** `npc.thaurak-escama-fria`, `npc.varkhan-escama-vigia`, `npc.skit-fenda-quente`, `faction.vigias-do-limiar`.
- **Relógio e evento:** `clock.passagens`, `event.registro-duplo`.
- **Fonte:** doc 01 parte 4 (MP-02), §2.5, N-02; doc 05 §1 (L-04); 12-conhecimento.csv; 12-relogios.csv.

### `mist.mp-03-peca-sem-funcao` — MP-03 — A peça sem função e o construto que despertou (Rodaviva)

- **Fatos confirmados:** peças raras somem e cada oficina culpa outra; Primeira-Peça é o construto desperto mais antigo conhecido; como e por que despertou é desconhecido; a cidade não definiu os direitos dos Despertos (lacuna intencional); o construto que despertou sem ser acordado e a peça sem função são rumor.
- **Hipóteses abertas:** defeito antigo que passou por despertar; uma oficina testa algo escondido; outro construto se move por vontade própria; alguém criou o boato para pressionar o foro.
- **A verdade não pode ser:** resposta à origem dos Despertos (aberta de propósito); tradição das Sete-Bigornas ou oficina de Aeltharyn; prova de que Construtos são propriedade ou de que são pessoas; Primeira-Peça obrigada a responder (ela pode não responder).
- **Evidência para resolver:** Casa das Peças; registros de oficina; o próprio construto, que pode não responder; pelo menos duas provas independentes (doc 06 §6).
- **Entidades:** `npc.primeira-peca`, `npc.fenwick-mola-solta`, `npc.quinze-pinos`, `faction.oficinas-reunidas`, `faction.casa-dos-despertos`.
- **Relógio e evento:** `clock.pecas`, `event.peca-sumida`, `event.pedido-desperto`.
- **Fonte:** doc 01 parte 4 (MP-03), §2.8, N-09; doc 04 §5, §10; doc 05 §1; 12-conhecimento.csv; 12-relogios.csv.

### `mist.mp-04-ata-e-pacto` — MP-04 — A ata que ninguém leu e o pacto que ninguém assinou (Pedra-Ata)

- **Fatos confirmados:** existem três cópias divergentes da ata do assento e nenhuma é confirmada (doc 04 §2.2); o Arquivo guarda a cópia de um pacto que as cidades negam ter assinado (alegado, doc 04 §4.3); que Maro Tinta-Velha o tenha copiado e que Velka Selo-Firme esconda uma ata são suspeitas das fichas, não fatos; as atas não lidas em voz alta são rumor.
- **Hipóteses abertas:** falsificação posterior; pacto de ofício antigo que ficou sem selo; proposta que nunca foi aprovada; ata verdadeira suprimida por conveniência.
- **A verdade não pode ser:** uma resposta que confirme só uma das três cópias; culpa exclusiva de uma facção; uma ata que mencione Aeltharyn ou seus reinos vizinhos; a posse da cópia tornar o conteúdo verdadeiro (L-08).
- **Evidência para resolver:** selos, tinta, cadeia de custódia (L-08); a cópia mais antiga de cada cidade; pelo menos duas provas independentes (doc 06 §6).
- **Entidades:** `npc.maro-tinta-velha`, `npc.velka-selo-firme`, `npc.haldra-tinta-funda`, `faction.arquivistas-das-coroas`.
- **Relógio e evento:** `clock.assento`, `event.assento-vazio`.
- **Fonte:** doc 01 parte 4 (MP-04), §2.10, N-01, N-12; doc 04 §2.2, §4.3; doc 05 §1 (L-08); 12-conhecimento.csv.

### `mist.mp-05-barca-sem-remador` — MP-05 — A barca sem remador (Barcaalta)

- **Fatos confirmados:** o Registro da Descida e as taxas não batem; os dois registros valem até conferência (L-09); a barca que desceu sem remador e o rio que muda de som antes de cheia são rumor.
- **Hipóteses abertas:** barca sem registro de carga, solta de propósito; marinheiro que fingiu para esconder contrabando; correnteza e velha amarração; a barca nunca existiu, só o boato.
- **A verdade não pode ser:** algo vindo do Argente em Aeltharyn (o rio não o toca); ligada ao Mistério C de Aeltharyn (o navio sem tripulação); descrição do que há rio abaixo; prova do rumor só por ele mesmo.
- **Evidência para resolver:** Registro da Descida; cordoaria; testemunhas do cais; amarrações; pelo menos duas provas independentes (doc 06 §6).
- **Entidades:** `npc.torgrim-corda-dupla`, `npc.seis-eixos`, `faction.companhia-da-descida`.
- **Relógio e evento:** `clock.cais`, `event.cais-fechado`.
- **Fonte:** doc 01 parte 4 (MP-05), §2.4; doc 05 §1 (L-09), §3.3; 12-relogios.csv.

### `mist.mp-06-corredores-que-aparecem` — MP-06 — Os corredores que aparecem (Sinuosa)

- **Fatos confirmados:** as galerias novas desorientam os guias; mapas rivais circulam; nenhum mapa é fonte final (L-05); que Nael Curva-Antiga conheça uma galeria fora dos mapas é suspeita da ficha e onde ela vai dar é desconhecido; a cidade nunca ter terminado de ser escavada é rumor.
- **Hipóteses abertas:** escavação clandestina; mapas copiados errado; desmoronamento que abre vãos antigos; uma família esconde corredores de propósito.
- **A verdade não pode ser:** passagem para Aeltharyn, para a Espinha de Vidro ou para a mina selada; o que se perdeu definido de vez (as duas lendas ficam vivas); culpa de um só povo.
- **Evidência para resolver:** cópias da Rotunda dos Mapas; testemunho de guias; marcas de ferramenta; pelo menos duas provas independentes (doc 06 §6).
- **Entidades:** `npc.nael-curva-antiga`, `npc.ilmeth-curva-segura`, `npc.pip-vapor-curto`, `faction.casa-dos-guias`, `faction.mercadores-de-fendas`.
- **Relógio e evento:** `event.mapa-rival`.
- **Fonte:** doc 01 parte 4 (MP-06), §2.6, N-10; doc 05 §1 (L-05); 12-conhecimento.csv; 12-eventos.csv.

## Conferências

- 6 mistérios, 4 hipóteses cada (24), 0 resposta única, 0 campo vazio; cada um com fatos confirmados, vetos e evidência; todo ID de NPC, facção, relógio e evento existe nos registros do reino 2; 0 NPC morto como entidade.
- 0 mistério escolhe hipótese do Mistério D; termos de fronteira (Torvelume, mina selada, Espinha de Vidro, Sete-Bigornas, Lúmen, Argente, Aeltharyn) só em vetos; 0 NPC, cidade, facção ou rota novos.

## Decisão pendente

**D-53** (ver `plano/DECISOES.md`): aprovar os seis mistérios como dados, com fatos confirmados, vetos e evidência. Opções: **A.** aprovar tudo (recomendada); **B.** trocar fato, veto ou evidência de algum (diga qual).
