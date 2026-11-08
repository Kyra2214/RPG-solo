# Validador de reino (M6.2)

**Estado:** especificação; nenhuma regra foi executada nem implementada. Opera sobre os arquivos de `ESQUEMA_DE_DADOS.md`. Cobre os 12 critérios de validação do autor (`MUNDO_PRECONFIGURADO.md` §14) e as portas de `MODELO_DE_REINO.md` §6.
**Princípio:** um erro de autoria quebra o build, não o jogo (`INTEGRACAO_FUTURA_RPG_SOLO.md`).

## 1. Severidade e saída

- **Erro (E):** viola contrato ou contradiz o cânone; o reino não pode ser marcado `fechado` nem carregado.
- **Aviso (A):** lacuna explícita ou ponto de revisão; não bloqueia, mas aparece no relatório e precisa de registro no fechamento.
- **Exceção:** `excecoes.txt` (`regra|entidade|motivo|fonte`) rebaixa **E para A** quando a contradição é intencional e está registrada no fechamento (ex.: cargos acumulados, D-11). Exceção sem `fonte` é erro (V-99).
- **Relatório:** uma linha por achado: `codigo|severidade|entidade|mensagem|fonte`. Ordem: erros antes de avisos, depois por código. Resumo final com contagem de E, A e exceções. Um reino com 0 E pode ser `fechado`.
- **Reino `bloqueado` ou `em_composicao`:** regras de mínimos (V-30 a V-35) viram **A**.

## 2. Regras

### Estrutura
| Código | Sev. | Regra |
|---|---|---|
| V-01 | E | cada arquivo tem `# esquema=N`, cabeçalho completo e todos os campos obrigatórios preenchidos (`?` e `—` valem; vazio não) |
| V-02 | E | ID segue `prefixo.slug` com o prefixo do tipo |
| V-03 | E | ID único em todo o mundo |
| V-04 | E | ID não consta em `ids_aposentados.txt` |
| V-05 | E | nome duplicado dentro do mesmo tipo e reino; **A** se igual entre tipos |
| V-06 | E | `fonte` presente em todo registro; **A** se `proposto` |

### Referências e pessoas
| Código | Sev. | Regra |
|---|---|---|
| V-10 | E | toda referência aponta para ID existente do tipo esperado |
| V-11 | E | cidade tem reino existente; assentamento tem cidade-mãe do mesmo reino |
| V-12 | E | povo, sub-raça, classe e profissão existem no catálogo; sub-raça pertence ao povo |
| V-13 | E | NPC com `vivo=não` não é agente de semente, evento ou cena, nem liderança vigente |
| V-14 | E | liderança de cidade, assentamento e facção é NPC vivo do mesmo reino |
| V-15 | A | NPC sem cidade nem facção (ex.: rei e princesa, D-19) |
| V-16 | E | cidade tem reino, localização, função e ao menos uma rota ou acesso `?` explícito |

### Geografia (critérios 2 e 5)
| Código | Sev. | Regra |
|---|---|---|
| V-20 | E | rota liga dois locais existentes e distintos |
| V-21 | E | sem rota duplicada (mesmo par e meio) |
| V-22 | E | `duracao_min` ≤ `duracao_max`; `estado=desconhecida` se e só se duração `?` |
| V-23 | E | as cidades formam grafo conexo usando rotas `documentada` e `com_ressalva`; **A** se só conexo contando rotas `desconhecida` |
| V-24 | E | cidade `navegavel` ou `ambos` (porto) tem rota `rio` ou `mar`; cidade só `terrestre` não tem rota `mar` |
| V-25 | E | cidade com minério em `recurso_chave` tem `recurso.txt` com papel `produtor` de minério |
| V-26 | A | rio que desce de área elevada e fortaleza em posição plausível: não verificável sem a geografia mestra (M-04); revisão manual |
| V-27 | A | assentamento com acesso de duração `?` |

### Mínimos e elenco (critérios 1, 3 e 4)
| Código | Sev. | Regra |
|---|---|---|
| V-30 | E | reino fechado tem ≥ 10 cidades |
| V-31 | E | mundo tem ≥ 5 reinos (nível mundo; **A** enquanto houver reino bloqueado) |
| V-32 | E | ≥ 8 NPCs `principal=sim`; acima de 12 é informativo (Aeltharyn tem 25) |
| V-33 | E | ≥ 2 NPCs com papel `heroi` e ≥ 2 com `general` |
| V-34 | E | existe NPC `governante` e NPC `herdeiro` (ou sucessão explícita `?`, **A**) |
| V-35 | E | NPC principal tem `objetivo`, `agenda`, `pressao`, `capacidades` e `limites` ≠ `?`; **A** se `agenda` e `pressao` forem idênticas entre dois principais |
| V-36 | A | cidade com menos de 2 povos em `povos` (cidades têm população mista) |

### Facções e relações
| Código | Sev. | Regra |
|---|---|---|
| V-40 | E | aliado e rival ao mesmo tempo (facção × facção, NPC × NPC); relação de tipos opostos na mesma dupla é **A** |
| V-41 | E | facção tem liderança existente |
| V-42 | A | aliados ou rivais `?` ou "não informado" |
| V-43 | E | toda relação tem origem, destino ou `destino_texto`, tipo e `fonte` |
| V-44 | E | nenhuma cidade é isolada: toda cidade aparece em ≥ 1 rota e em ≥ 1 dependência de `recurso.txt` (`?` conta como lacuna, **A**) |

### Recursos e demografia
| Código | Sev. | Regra |
|---|---|---|
| V-50 | E | recurso aponta para localidade existente |
| V-51 | E | `demanda` ∈ 0 a 5 ou `?`; estimativa sem "(provisório" é erro |
| V-52 | E | `populacao_faixa` ∈ faixas de uso; nenhum campo de população total do reino |

### Mundo vivo
| Código | Sev. | Regra |
|---|---|---|
| V-60 | E | relógio: `progresso` ≤ `segmentos`; `move` e `trava` ≠ `?` |
| V-61 | E | evento: gatilho e cadeia presentes; todo ID de efeito existe |
| V-62 | E | semente: ID das entidades e agentes existem; `porte` ∈ P, M, G; `ameaca` 1 a 4; uma de cada porte por cidade e assentamento (**A** se faltar) |
| V-63 | E | mistério: ≥ 2 hipóteses abertas e `a_verdade_nao_pode_ser` preenchido; nenhum campo de solução |
| V-64 | E | calendário: meses × dias + dias de transição = 365 no caso de Aeltharyn (soma coerente com o próprio arquivo); `estacao_inicial` existe |

### Informação (critérios 10, 11 e 12)
| Código | Sev. | Regra |
|---|---|---|
| V-70 | E | fato tem camada, estado e caminho; restrito e secreto têm `quem_sabe` ≠ `—` (exceto relógio) e `marcador` |
| V-71 | E | resumo ≤ 500 caracteres e vizinhança ≤ 300; um resumo por entidade |
| V-72 | E | nenhum resumo com `camada_max` abaixo de `restrito` contém o `marcador` de fato restrito ou secreto |
| V-73 | E | cena: `entra` ≤ 2.400 caracteres, só fatos `publico` ou `local`, nenhum relógio, nenhum NPC morto |
| V-74 | A | entidade sem resumo ou NPC principal sem `tom` |

### Regra opcional de reino

| Código | Sev. | Regra |
|---|---|---|
| V-A1 | A | só para reinos que mantêm a tríade: cada cidade tem ≥ 1 NPC `heroi`, `guardiao` e `benfeitor` (M6.3) |

### Exceções
| Código | Sev. | Regra |
|---|---|---|
| V-99 | E | exceção sem `fonte`, ou que aponta para regra ou entidade inexistente |

## 3. Cobertura dos 12 critérios do autor

| # | Critério | Regras |
|---|---|---|
| 1 | 5 reinos e 10 cidades por reino | V-30, V-31 |
| 2 | cidade com reino, localização, função e conexões | V-11, V-16, V-20, V-23, V-24, V-44 |
| 3 | governantes e NPCs com objetivos e relações | V-32, V-34, V-35, V-40, V-43 |
| 4 | heróis e generais com recursos, capacidades e limites | V-33, V-35 |
| 5 | geografia consistente | V-20 a V-27 |
| 6 | territórios de povos não impedem migração | V-36 (só aviso; o resto é revisão editorial) |
| 7 | NPC recorrente preserva identidade e memória | teste de integração I-01 |
| 8 | campanha alternativa não contamina a compartilhada | I-02 |
| 9 | evento novo registrado sem apagar histórico | I-03 (delta) |
| 10 | a IA gera aventuras com entidades existentes | V-62, V-70, I-04 |
| 11 | NPC só sabe o que viu ou descobriu | V-70, V-72, I-05 |
| 12 | só o relevante vai à IA em cada cena | V-71, V-73, I-06 |

**Testes de integração (I-01 a I-06):** dependem da campanha e do motor, não dos arquivos; ficam para o M6.4 e para a implementação. Aqui só se registra que o validador **não** os substitui.

## 4. Previsão para Aeltharyn (não executada)

Com base no que os CSVs já mostram, o primeiro relatório deve trazer **avisos**, não erros, em: V-42 (8 facções com aliados ou rivais não informados), V-15 (rei e princesa sem cidade, D-19), V-27 (20 assentamentos sem duração) e V-23 (Orvass–Brumafunda desconhecida). Cargos acumulados (D-11) entram em `excecoes.txt` quando aplicável. D-18 (Edran sem ID) e D-20 (povo de Yssara e Hadrik) são decisões do autor, não regras. A previsão só vale depois de rodar o validador sobre os arquivos convertidos.

## 5. Limites

- Mapa campo a campo do catálogo Kotlin: M6.3. Protótipo e métrica de jogo: M6.4.
- Regras V-26 e V-36 dependem da geografia mestra e de dados que o catálogo ainda não tem.
- A lista de regras cresce com os reinos 2 a 5; um código nunca muda de significado.
