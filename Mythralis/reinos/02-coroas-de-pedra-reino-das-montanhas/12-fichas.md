# 12 — Fichas completas dos doze principais (R2-4.1)

**Estado:** rascunho de R2-4.1 para aprovação do autor · **Data:** 2026-11-04
**Etiqueta:** **[PROPOSTO]**; **não cria NPC, nome, cidade ou fato novo**: reúne e completa as fichas N-01 a N-12 (doc 01 parte 2, D-33) no campo a campo de `MODELO_DE_REINO.md` §4, com raça, sub-raça, classe e profissão de `ALINHAMENTO_COROAS.md`. Dados em `12-fichas.csv` (mesmos campos do reino 1, 17 colunas).
**Estados:** C confirmado · S suspeita · D desconhecido · ASSUMINDO = leitura editorial sem fonte direta (traços de personalidade, medos e condições de evolução). `[doc]` = texto já aprovado. Nada se infere além do doc 01.
**Campos adiados:** competências detalhadas (R2-4.2), matriz de relações (R2-4.3), voz (R2-4.4), camadas de informação (feitas no R2-6.2, `14-camadas-de-informacao.csv`). Onde a ficha precisa deles, traz só o que já está documentado e `desconhecida`.

## 1. Conferências

| Verificação | Resultado |
|---|---|
| Principais com ficha completa | **12 de 12** (17 campos, 0 vazio) |
| Agenda e pressão distintas | **12 agendas e 12 pressões diferentes** |
| Heróis vivos / generais | 2 (Vireo, Maur) / 2 (Thaurak, Gundrik) |
| Povo, sub-raça, classe e profissão | todos conferidos no catálogo (`ALINHAMENTO_COROAS.md`) |
| Origem | desconhecida em todos (nenhuma documentada) |
| Estado | vivo em todos |
| Cidades com principal | 9 de 10; **Barcaalta** não tem principal (Torgrim Corda-Dupla é só liderança, doc 04 §9) |

## 2. Fichas por cidade

### Pedra-Ata — `city.pedra-ata`

#### `npc.velka-selo-firme` — Velka Selo-Firme (N-01)

| Campo | Ficha |
|---|---|
| Idade | idosa (faixa, sem número) |
| Povo (ID) e sub-raça | anao (Anão); sub-raça: anao-das-montanhas |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Presidente do Concílio das Coroas neste ciclo (presidente de guarda); Guardiã do Selo; presidente da Mesa de Pedra-Ata, escolhida pelos escribas e hospedeiros (doc 04 §2.2, §9); acúmulo intencional |
| Cidade e facção | city.pedra-ata; facção: nenhuma (Mesa de Pedra-Ata e Concílio) |
| Personalidade, valores, objetivos, medos, limites | cautelosa e formal, evita se expor; valores: neutralidade do assento; medo: que uma ata escondida seja lida em voz alta (S); objetivo: Manter o assento rotativo funcionando e impedir que uma cidade o capture [doc]; limite: Não manda nas outras nove cidades; só convoca, presidir e guardar o selo do Concílio [doc]. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | diplomata de corte; convoca e preside o Concílio e aplica o selo (C); restrição: não manda nas nove cidades e não decide a ordem do assento (doc 04 §2); catálogo: classe diplomata, profissão diplomata-de-corte (R2-4.2 detalha); influência e recursos além do que consta no cargo: desconhecidos |
| Relações | guarda o selo do Concílio (C); a Mesa governa só Pedra-Ata (C); mesma cidade do Arquivo das Coroas de Maro Tinta-Velha (C, doc 06 CL-05); relação pessoal com Maro e com as cidades: desconhecida (R2-4.3) |
| Biografia e marcos | C: preside o ciclo atual e guarda o selo; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Preside o Concílio neste ciclo; guarda o selo. S: Escondeu uma ata que ela mesma não quer ler em voz alta. D: Quem escolherá o próximo presidente |
| Estado atual | vivo |
| Agenda atual | Manter o assento rotativo funcionando e impedir que uma cidade o capture [doc] |
| Pressão | Cada cidade a cobra por uma ata diferente; o ciclo dela termina sem sucessor combinado [doc] |
| Condições plausíveis de evolução | ASSUMINDO: se o ciclo fecha sem sucessor combinado (CL-05, 2/6), ela escolhe entre entregar o selo à guarda, propor uma ordem ou manter o costume; nada se resolve por calendário (doc 06) |
| Camadas de informação | `fact.velka-preside-selo`; `fact.velka-ata-escondida` (`14-camadas-de-informacao.csv`) |

#### `npc.maro-tinta-velha` — Maro Tinta-Velha (N-12)

| Campo | Ficha |
|---|---|
| Idade | idosa (faixa, sem número) |
| Povo (ID) e sub-raça | draconato (Draconato); sub-raça: draconato-de-cobre |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Arquivista-chefe das Coroas; líder dos Arquivistas das Coroas (doc 04 §10) |
| Cidade e facção | city.pedra-ata; facção: F-14 Arquivistas das Coroas (líder) |
| Personalidade, valores, objetivos, medos, limites | escrupuloso e teimoso; valores: a ata não é reescrita; medo: ser forçado a copiar o que mente, ou o pacto negado vir à tona (S); objetivo: Guardar as atas sem que a política as reescreva [doc]; limite: Guarda e copia; não decide o que vale [doc]. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | cronista e bibliotecário; guarda e copia, não decide o que vale (C); catálogo: classe cronista, profissão bibliotecario (R2-4.2 detalha); influência e recursos além do que consta no cargo: desconhecidos |
| Relações | aliado da Rede dos Passos Altos (F-03, mensageiros carregam cópias); rival: desconhecido (doc 04 §10); Velka guarda o selo na mesma cidade (C, doc 06 CL-05); relação pessoal: desconhecida (R2-4.3) |
| Biografia e marcos | C: guarda o Arquivo das Coroas; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Guarda o Arquivo das Coroas. S: Copiou um pacto que as cidades juram nunca ter assinado. D: Quem assinou o pacto e por que as cidades o negam |
| Estado atual | vivo |
| Agenda atual | Guardar as atas sem que a política as reescreva [doc] |
| Pressão | As cidades pedem cópias diferentes; ele recusa as que mentem [doc] |
| Condições plausíveis de evolução | ASSUMINDO: se as cidades pedirem mais cópias divergentes (CL-05, 2/6), ele escolhe entre recusar todas ou copiar com a cadeia de custódia (L-08); quem assinou o pacto fica desconhecido |
| Camadas de informação | `fact.maro-guarda-arquivo`; `fact.maro-pacto-copiado`; `fact.mp-04-tres-copias-ata` (`14-camadas-de-informacao.csv`) |

### Portão-Cinza — `city.portao-cinza`

#### `npc.thaurak-escama-fria` — Thaurak Escama-Fria (N-02)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número) |
| Povo (ID) e sub-raça | draconato (Draconato); sub-raça: draconato-cinzento |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | General do Limiar do Esporão; Portão-Mor da Mesa do Portão; líder dos Vigias do Limiar (acúmulo intencional, doc 04 §9) |
| Cidade e facção | city.portao-cinza; facção: F-06 Vigias do Limiar (líder) |
| Personalidade, valores, objetivos, medos, limites | disciplinado e reservado; valores: portão aberto e vigiado; medo: ser culpado pelas lacunas que herdou (S: protege quem arrancou as páginas); objetivo: Manter o portão aberto e vigiado ao mesmo tempo, sem fechar a descida [doc]; limite: Comanda a guarda do Limiar, não os exércitos das outras cidades; só age fora do portão com voto do Concílio [doc]. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | comandante de escolta profissional; comanda a guarda do Limiar, não os exércitos das outras cidades (C); catálogo: classe comandante, profissão escolta-profissional (R2-4.2 detalha); influência e recursos além do que consta no cargo: desconhecidos |
| Relações | aliado da Rede dos Passos Altos (F-03); rival da Companhia da Descida (F-05) (C, doc 04 §10); demais relações: desconhecidas (R2-4.3) |
| Biografia e marcos | C: comanda os vigias do Portão-Cinza; herdou o Livro de Passagens com lacunas; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Comanda os vigias do Portão-Cinza. S: Sabe quem arrancou páginas do Livro e protege essa pessoa. D: Por que as luzes vistas à noite nunca apareceram nos registros |
| Estado atual | vivo |
| Agenda atual | Manter o portão aberto e vigiado ao mesmo tempo, sem fechar a descida [doc] |
| Pressão | Faltam vigias; o Livro de Passagens tem lacunas que ele herdou [doc] |
| Condições plausíveis de evolução | ASSUMINDO: se faltarem mais vigias ou o Livro (CL-04, 2/6) for contestado, escolhe entre pedir voto do Concílio para agir fora do portão ou apertar o registro dentro dele |
| Camadas de informação | `fact.thaurak-comanda-vigias`; `fact.thaurak-protege-quem-arrancou` (`14-camadas-de-informacao.csv`) |

### Brasalume — `city.brasalume`

#### `npc.gundrik-muralha-lenta` — Gundrik Muralha-Lenta (N-03)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número) |
| Povo (ID) e sub-raça | anao (Anão); sub-raça: anao-das-forjas |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | General da guarda das fornalhas; Guarda-Mor das Fornalhas e presidente do Conselho das Famílias de Fogo (cargo único de guarda e governo, doc 04 §9) |
| Cidade e facção | city.brasalume; facção: nenhuma própria (preside o Conselho; F-01 é de Karna Brasa-Mansa) |
| Personalidade, valores, objetivos, medos, limites | obstinado e prático; valores: o fogo principal nunca falta; medo: saída que não consta nos mapas ser descoberta (S); objetivo: Defender Brasalume e garantir que o fogo principal nunca falte [doc]; limite: Comanda a guarda da cidade e das minas próximas; não pode requisitar metal sem o Concílio [doc]. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | comandante e armeiro; comanda a guarda e as minas próximas, não pode requisitar metal sem o Concílio (C); catálogo: classe comandante, profissão armeiro (R2-4.2 detalha); influência e recursos além do que consta no cargo: desconhecidos |
| Relações | preside o Conselho das Famílias de Fogo (C); comanda a guarda de Brasalume e de Veio-Fundo (C); a Fornalha-Mãe é de Karna na mesma cidade (C, doc 06 CL-01); relação pessoal: desconhecida (R2-4.3) |
| Biografia e marcos | C: comanda a guarda de Brasalume e de Veio-Fundo; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Comanda a guarda de Brasalume e de Veio-Fundo. S: Guarda uma saída de ventilação que não consta nos mapas oficiais. D: Se a Fornalha-Mãe pode mesmo apagar |
| Estado atual | vivo |
| Agenda atual | Defender Brasalume e garantir que o fogo principal nunca falte [doc] |
| Pressão | Os forjadores o pressionam por mais soldados nas minas; as famílias disputam o fogo [doc] |
| Condições plausíveis de evolução | ASSUMINDO: se o combustível piorar (CL-01, 3/6), escolhe entre mais soldados nas minas, como os forjadores pedem, ou apoiar o turno do fogo (L-03); a origem do combustível não se resolve por ele |
| Camadas de informação | `fact.gundrik-comanda-guarda`; `fact.gundrik-saida-ventilacao` (`14-camadas-de-informacao.csv`) |

#### `npc.karna-brasa-mansa` — Karna Brasa-Mansa (N-11)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número) |
| Povo (ID) e sub-raça | elemental-fogo (Nascido da Brasa) |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Mestra da Fornalha-Mãe; líder da Casa das Fornalhas (doc 04 §10) |
| Cidade e facção | city.brasalume; facção: F-01 Casa das Fornalhas (líder) |
| Personalidade, valores, objetivos, medos, limites | serena e firme; valores: fogo justo entre as famílias; medo: que a liga que não esfria seja tomada (S); objetivo: Manter a Fornalha-Mãe acesa e justa entre as famílias [doc]; limite: Cuida do fogo, não da muralha [doc]. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | artífice e ferreira; cuida do fogo, não da muralha (C); catálogo: classe artifice, profissão ferreiro (R2-4.2 detalha); influência e recursos além do que consta no cargo: desconhecidos |
| Relações | aliada da Companhia da Descida (F-05); rival da Irmandade do Veio (F-02) (C, doc 04 §10); Gundrik comanda a guarda da mesma cidade (C, doc 06 CL-01); relação pessoal: desconhecida (R2-4.3) |
| Biografia e marcos | C: mantém a Fornalha-Mãe; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Mantém a Fornalha-Mãe. S: Esconde uma liga que não esfria. D: Se a liga é do passado ou do futuro |
| Estado atual | vivo |
| Agenda atual | Manter a Fornalha-Mãe acesa e justa entre as famílias [doc] |
| Pressão | O combustível falta; as famílias disputam sua vez [doc] |
| Condições plausíveis de evolução | ASSUMINDO: se o turno do fogo (L-03) for contestado (CL-01, 3/6), ela escolhe entre dar o turno à cidade primeiro ou segurar as famílias; a liga fica desconhecida |
| Camadas de informação | `fact.karna-fornalha-mae`; `fact.karna-liga-que-nao-esfria` (`14-camadas-de-informacao.csv`) |

### Altavento — `city.altavento`

#### `npc.vireo-vento-rapido` — Vireo Vento-Rápido (N-04)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número) |
| Povo (ID) e sub-raça | goliath (Altaneiro); sub-raça: altaneiro-dos-penhascos |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Herói da Escadaria Cega; Voz dos Picos, aclamado nos Ninhos Velhos e revogável pela assembleia (doc 04 §9) |
| Cidade e facção | city.altavento; facção: nenhuma própria (preside a Assembleia dos Picos) |
| Personalidade, valores, objetivos, medos, limites | franco e inquieto; valores: manter o passo aberto; medo: a queda que o quase tirou do ar (S); objetivo: Treinar mensageiros novos e manter aberto o passo que ele salvou [doc]; limite: Voa melhor que luta; não conhece o vale baixo [doc]. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | batedor e mensageiro de altitude; voa melhor que luta e não conhece o vale baixo (C); catálogo: classe batedor, profissão mensageiro (R2-4.2 detalha); influência e recursos além do que consta no cargo: desconhecidos |
| Relações | preside a Assembleia dos Picos (C); Rede dos Passos Altos é de Zhae Garra-de-Névoa, da mesma cidade (C); Iskra Pena-Longa lidera o posto Ninho-Seco (C, doc 06); relação pessoal: desconhecida (R2-4.3) |
| Biografia e marcos | C: salvou uma caravana na Escadaria Cega em uma nevasca; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Salvou uma caravana na Escadaria Cega em uma nevasca. S: Esconde uma queda que quase o tirou do ar. D: O que ouviu no passo que só se abre a quem não pergunta |
| Estado atual | vivo |
| Agenda atual | Treinar mensageiros novos e manter aberto o passo que ele salvou [doc] |
| Pressão | Quer descer menos e voar mais; a cidade o quer como rosto da gente dos picos [doc] |
| Condições plausíveis de evolução | ASSUMINDO: se nova nevasca fechar passos (CL-07, 1/6), escolhe entre voar mais, como quer, ou ficar como rosto da cidade, como ela pede; estação sozinha não avança nada |
| Camadas de informação | `fact.vireo-caravana-nevasca`; `fact.vireo-queda` (`14-camadas-de-informacao.csv`) |

#### `npc.zhae-garra-de-nevoa` — Zhae Garra-de-Névoa (N-06)

| Campo | Ficha |
|---|---|
| Idade | jovem adulta (faixa, sem número) |
| Povo (ID) e sub-raça | felino-das-nuvens (Felino das Nuvens) |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Guardiã dos passos altos; líder da Rede dos Passos Altos (doc 04 §10) |
| Cidade e facção | city.altavento; facção: F-03 Rede dos Passos Altos (líder) |
| Personalidade, valores, objetivos, medos, limites | determinada e desconfiada; valores: ninguém fica perdido; medo: ser expulsa de novo (S); motivo de ouvir os chamados: desconhecido; objetivo: Fechar os passos antes da avalanche e acompanhar viajantes perdidos [doc]; limite: Só protege até onde o passo vai; não desce o vale [doc]. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | sentinela e guia de montanha; só protege até onde o passo vai, não desce o vale (C); catálogo: classe sentinela, profissão guia-de-montanha (R2-4.2 detalha); influência e recursos além do que consta no cargo: desconhecidos |
| Relações | aliada de Vigias do Limiar (F-06), Casa dos Guias (F-07) e Arquivistas das Coroas (F-14); rival: desconhecido (doc 04 §10); felinos mais velhos duvidam dela (C, pressão) |
| Biografia e marcos | C: guia viajantes pelos passos de Altavento; S: foi expulsa de um grupo de caçadores; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Guia viajantes pelos passos de Altavento. S: Foi expulsa de um grupo de caçadores. D: O que a faz ouvir os chamados |
| Estado atual | vivo |
| Agenda atual | Fechar os passos antes da avalanche e acompanhar viajantes perdidos [doc] |
| Pressão | Nem todos aceitam ser guiados; os felinos mais velhos duvidam dela [doc] |
| Condições plausíveis de evolução | ASSUMINDO: se nevasca fechar passos (CL-07, 1/6), ela fecha antes da avalanche mesmo sem acordo; confiança dos mais velhos só muda com fato observado |
| Camadas de informação | `fact.zhae-guia-passos`; `fact.zhae-expulsa-cacadores` (`14-camadas-de-informacao.csv`) |

### Veiaclara — `city.veiaclara`

#### `npc.pelma-gota-medida` — Pelma Gota-Medida (N-07)

| Campo | Ficha |
|---|---|
| Idade | madura (faixa, sem número) |
| Povo (ID) e sub-raça | gnomo (Gnomo (das rochas)) |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Guardiã das comportas; Medidora-Mor; cadeira do Conselho da Nascente que ela convoca e registra (doc 04 §9) |
| Cidade e facção | city.veiaclara; facção: Casa das Comportas (F-04), cuja liderança é de outra pessoa (doc 04 §10) |
| Personalidade, valores, objetivos, medos, limites | meticulosa e ansiosa por registro; valores: a água medida e registrada; medo: o pânico, motivo de alterar o registro (S); objetivo: Manter a água medida e registrada para todos [doc]; limite: Só abre e fecha comportas; não decide quem recebe água [doc]. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | guardiã e mecânica; só abre e fecha comportas, não decide quem recebe água (C); catálogo: classe guardiao, profissão mecanico (R2-4.2 detalha); influência e recursos além do que consta no cargo: desconhecidos |
| Relações | uma das cadeiras do Conselho da Nascente (C); cidades a jusante pedem água sem pagar (C, pressão); Pimm Veio-Pequeno atua na Casa das Comportas (C, doc 06 CL-02); relação pessoal: desconhecida (R2-4.3) |
| Biografia e marcos | C: registra cada abertura de comporta; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Registra cada abertura de comporta. S: Alterou um registro para evitar pânico. D: Se o degelo mudou de data por causa dela |
| Estado atual | vivo |
| Agenda atual | Manter a água medida e registrada para todos [doc] |
| Pressão | As cidades a jusante pedem sem pagar; uma comporta velha pode ceder [doc] |
| Condições plausíveis de evolução | ASSUMINDO: se o registro for conferido antes do degelo (O-03, CL-02, 2/6), ela escolhe entre admitir a alteração ou mantê-la; nenhuma escolha prova que o degelo mudou por causa dela (D) |
| Camadas de informação | `fact.pelma-registra-comportas`; `fact.mp-01-registro-nao-bate`; `fact.pelma-registro-alterado` (`14-camadas-de-informacao.csv`) |

### Cumieira — `city.cumieira`

#### `npc.maur-camada-velha` — Maur Camada-Velha (N-05)

| Campo | Ficha |
|---|---|
| Idade | indeterminada (faixa, sem número) |
| Povo (ID) e sub-raça | elemental-terra (Nascido da Rocha) |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Herói do Pórtico Grande; sem cargo de governo documentado |
| Cidade e facção | city.cumieira; facção: desconhecida (F-09 e F-10 atuam na cidade; vínculo dele não documentado) |
| Personalidade, valores, objetivos, medos, limites | quieto e paciente; valores: proteger as escalas pequenas; medo: ser lido como pequeno ou grande demais por quem precisa dele; objetivo: Proteger quem vive nas escalas pequenas dentro da cidade grande [doc]; limite: Não fala em público; só age pelo exemplo [doc]. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | guardião e pedreiro (catálogo: elemental-terra); não fala em público, só age pelo exemplo (C); catálogo: classe guardiao, profissão pedreiro (R2-4.2 detalha); influência e recursos além do que consta no cargo: desconhecidos |
| Relações | relação com o Salão dos Juramentos de Sangue (Gorrak Pedra-Grande, líder do Conselho de Sangue) e com a Liga dos Degraus: desconhecida; os grandes o veem como pequeno e os menores como grande (C, pressão) |
| Biografia e marcos | C: segurou o Pórtico durante um desabamento; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Segurou o Pórtico durante um desabamento. S: É mais velho do que a cidade conta. D: Se vem da pedra ou foi esculpido nela |
| Estado atual | vivo |
| Agenda atual | Proteger quem vive nas escalas pequenas dentro da cidade grande [doc] |
| Pressão | Os grandes o veem como pequeno demais para mandar; os menores o veem como grande demais para escutar [doc] |
| Condições plausíveis de evolução | ASSUMINDO: se a Lei da Escala (L-07) for votada, ele passa a ser testemunha disputada pelos dois lados; sem relógio próprio |
| Camadas de informação | `fact.maur-segurou-portico`; `fact.maur-idade-real` (`14-camadas-de-informacao.csv`) |

### Rodaviva — `city.rodaviva`

#### `npc.primeira-peca` — Primeira-Peça (N-09)

| Campo | Ficha |
|---|---|
| Idade | indeterminada (faixa, sem número) |
| Povo (ID) e sub-raça | construto (Construto Desperto) |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Memória viva da Casa dos Despertos; sem comando |
| Cidade e facção | city.rodaviva; facção: F-11 Casa dos Despertos (memória, sem comando) |
| Personalidade, valores, objetivos, medos, limites | lenta e precisa; valores: nenhum construto é propriedade; medo: ser desmontada por uma lei, ou que a peça seja identificada (S); objetivo: Impedir que os construtos sejam tratados como propriedade [doc]; limite: Não comanda os outros construtos; só aconselha e lembra [doc]. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | cronista e relojoeira; só aconselha e lembra (C); restrição: não comanda os outros construtos; catálogo: classe cronista, profissão relojoeiro (R2-4.2 detalha); influência e recursos além do que consta no cargo: desconhecidos |
| Relações | memória da Casa dos Despertos (C, doc 04 §10); oficinas a procuram para legalizar o uso e Despertos novos para saber o que são (C, pressão); Fenwick Mola-Solta preside a Assembleia das Oficinas (C, doc 06 CL-06); relação pessoal: desconhecida (R2-4.3) |
| Biografia e marcos | C: é o construto desperto mais antigo conhecido; como e por que despertou: desconhecido |
| Conhecimento (C, S, D) | C: É o construto desperto mais antigo conhecido. S: Guarda uma peça que ninguém soube identificar. D: Como e por que despertou |
| Estado atual | vivo |
| Agenda atual | Impedir que os construtos sejam tratados como propriedade [doc] |
| Pressão | As oficinas a procuram para legalizar o uso; os Despertos novos a procuram para dizer o que são [doc] |
| Condições plausíveis de evolução | ASSUMINDO: se o foro dos Despertos for votado (CL-06, 2/6), ela escolhe se aconselha só ou fala pelos Despertos; não há lei sobre direitos de Construtos (doc 05 §1) e esta ficha não cria uma |
| Camadas de informação | `fact.primeira-peca-mais-antigo`; `fact.primeira-peca-peca-sem-nome` (`14-camadas-de-informacao.csv`) |

### Geodália — `city.geodalia`

#### `npc.aurea-faceta-longa` — Aurea Faceta-Longa (N-08)

| Campo | Ficha |
|---|---|
| Idade | idosa (faixa, sem número) |
| Povo (ID) e sub-raça | cristalino (Cristalino) |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Benfeitora da Câmara das Lentes; sem cargo de governo (a Primeira da Câmara é outra pessoa, doc 04 §9) |
| Cidade e facção | city.geodalia; facção: desconhecida (Coro das Facetas F-13 atua na cidade; vínculo dela não documentado) |
| Personalidade, valores, objetivos, medos, limites | generosa e reticente; valores: luz para todas as cidades; medo: que a faceta que mostra rostos seja descoberta (S); objetivo: Manter as lentes de luz funcionando para todas as cidades [doc]; limite: Paga lentes e reparos; não escolhe quem recebe luz [doc]. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | artífice e lapidadora; paga lentes e reparos (C); restrição: não escolhe quem recebe luz; catálogo: classe artifice, profissão lapidador (R2-4.2 detalha); influência e recursos além do que consta no cargo: desconhecidos |
| Relações | paga a manutenção das lentes (C); a Câmara é presidida por Iridel Luz-Partida (C, doc 04 §9); todas as cidades dependem da luz (DR-03); relação pessoal: desconhecida (R2-4.3) |
| Biografia e marcos | C: financia a manutenção das lentes; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Financia a manutenção das lentes. S: Esconde uma faceta que mostra rostos a quem olha. D: O que a caverna lembra e o que ela esquece |
| Estado atual | vivo |
| Agenda atual | Manter as lentes de luz funcionando para todas as cidades [doc] |
| Pressão | As cotas de extração apertam; a lente mais antiga já rachou [doc] |
| Condições plausíveis de evolução | ASSUMINDO: se as cotas apertarem mais (C-09) ou a lente antiga ceder, ela escolhe entre pagar mais reparo ou aceitar menos luz; não escolhe quem recebe luz |
| Camadas de informação | `fact.aurea-financia-lentes`; `fact.aurea-faceta-rostos` (`14-camadas-de-informacao.csv`) |

### Sinuosa — `city.sinuosa`

#### `npc.nael-curva-antiga` — Nael Curva-Antiga (N-10)

| Campo | Ficha |
|---|---|
| Idade | idosa (faixa, sem número) |
| Povo (ID) e sub-raça | minotauro (Labiríntico); sub-raça: labirintico-das-fortalezas |
| Origem | desconhecida (nenhuma origem documentada) |
| Cargo e títulos | Mestre da Casa dos Guias; líder da Casa dos Guias (doc 04 §10); presidência da Rotunda dos Mapas é de outra pessoa neste ciclo |
| Cidade e facção | city.sinuosa; facção: F-07 Casa dos Guias (líder) |
| Personalidade, valores, objetivos, medos, limites | paciente e cética; valores: ofício de guiar e mapa honesto; medo: a galeria sem mapa (S); objetivo: Manter vivo o ofício de guiar e impedir a venda do mapa errado [doc]; limite: Só guia; não comanda a cidade [doc]. Traços e medos: ASSUMINDO (leitura das fichas do doc 01) |
| Capacidades, influência, restrições | exploradora e guia de montanha; só guia, não comanda a cidade (C); catálogo: classe explorador, profissão guia-de-montanha (R2-4.2 detalha); influência e recursos além do que consta no cargo: desconhecidos |
| Relações | aliada da Rede dos Passos Altos (F-03); rival dos Mercadores de Fendas (F-08) (C, doc 04 §10); Ilmeth Curva-Segura preside a Rotunda neste ciclo (C); relação pessoal: desconhecida (R2-4.3) |
| Biografia e marcos | C: preside a Casa dos Guias; demais marcos: desconhecidos |
| Conhecimento (C, S, D) | C: Preside a Casa dos Guias. S: Sabe de uma galeria que não consta em nenhum mapa. D: Onde essa galeria vai dar |
| Estado atual | vivo |
| Agenda atual | Manter vivo o ofício de guiar e impedir a venda do mapa errado [doc] |
| Pressão | Mapas rivais circulam; ela não sabe qual está certo [doc] |
| Condições plausíveis de evolução | ASSUMINDO: se mapas rivais forem validados por alguém (MP-06), ela escolhe entre revelar a galeria ou guardá-la; para onde a galeria vai fica desconhecido |
| Camadas de informação | `fact.nael-preside-guias`; `fact.nael-galeria-fora-dos-mapas` (`14-camadas-de-informacao.csv`) |

## 3. Decisão pendente

**D-45:** aprovar as 12 fichas completas como estão. Os traços de personalidade, os medos e as condições de evolução são ASSUMINDO; nenhum muda fato aprovado. Opções: **A.** aprovar (recomendada); **B.** trocar traço, medo ou condição de alguma ficha (diga qual). **Recomendação: A.**
