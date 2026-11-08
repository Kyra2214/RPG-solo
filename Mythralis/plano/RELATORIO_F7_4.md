# Relatório F7.4 — invariantes, tramas existentes e calibragem inicial

**Data:** 08/11/2026
**Branch:** `mythralis`
**Estado:** implementação e validação técnica concluídas; aceite integral da fase F7 permanece pendente por metas quantitativas da seção 14.

## Escopo executado

“Tramas” foi interpretado conservadoramente como as cadeias de eventos e sementes já existentes no molde, executadas pelos relógios e pelo motor `Eventos`. Não foi criada uma nova mecânica de arcos, escolhas ou fatos canônicos. As consequências `se_ninguem_age` continuam alegadas e causadas; nenhum mistério foi resolvido.

Foram implementados três invariantes mecânicos. **I-23** valida as transições da relação de cada par `front.*`: neutra, tratado, ruptura e guerra, incluindo auto-transições usadas para renovar uma relação. **I-24** exige ruptura elegível, tensão mínima, regime não selado e coroas firmes para iniciar guerra; uma coroa só pode cair durante guerra e não pode ser restaurada. **I-25** exige gatilho causal para a criação e transição de eras: início da rodada, primeiro incidente/limite de segurança, primeira guerra ou queda de coroa.

A integração também corrige um defeito descoberto no lote: uma baixa podia matar um NPC e, no mesmo ciclo anual, o motor ainda o selecionava como signatário da paz. O motor de guerra agora exclui vítimas já programadas para morrer tanto de novas baixas quanto da seleção de agentes diplomáticos no mesmo ciclo. O teste determinístico reproduz esse caso.

A calibragem de `queda_pm` foi reduzida de **20‰ para 10‰ por ano de guerra**, mantendo o valor marcado `PROPOSTO`; o gerador e `dinamica/guerra.txt` estão sincronizados.

## Testes e lote

Comando da suíte, executado na raiz de `Mythralis`:

```bash
python3 -m unittest discover -s motor/testes -v
```

**Resultado: 45 testes passaram.** O lote final foi executado com 20 sementes consecutivas (1–20), 500 anos por semente e passo de 10 anos: **20/20 rodadas OK**, 50 blocos cada, sem falha em `saude.txt` nem em `invariantes.txt`.

| Métrica da seção 14 | Resultado final | Avaliação |
|---|---:|---|
| Primeiro incidente | anos 41–62; 20/20 dentro de 20–120 | Dentro da meta |
| Primeira guerra | anos 55–181; 20/20 dentro de 40–250 | Dentro da meta |
| Relógios estourados no primeiro século | 17–30 de 40; 19/20 dentro de 16–28 | Maioria dentro; 1 semente acima |
| Reinos caídos em 500 anos | 0–3 por semente; 19/20 dentro de 0–2 | **Uma semente excedeu:** semente 18 teve 3 quedas |
| Mesmo reino cai em todas as sementes | Nenhum | Sem desequilíbrio universal |
| Mistérios resolvidos sem agente | 0 | Dentro da meta |
| Deltas importantes (3+) por reino/década | mínimos por semente: 20–29; máximos: 82–97 | **Máximos excedem 40 em 20/20 sementes** |
| Saúde e invariantes | 20 relatórios de cada, zero falhas | Passou |

Os resultados por semente estão arquivados em [`RESULTADO_LOTE_F7_4_500A_20S.md`](RESULTADO_LOTE_F7_4_500A_20S.md) e [`RESULTADO_LOTE_F7_4_500A_20S.csv`](RESULTADO_LOTE_F7_4_500A_20S.csv).

## Limites e próximo passo

O relatório de lote mede, para o último alvo da tabela, **deltas** com importância 3 ou mais. Esse total inclui registros operacionais repetidos de bloqueio de rotas e deltas `campo` que materializam mudanças de estado. A meta do plano é formulada como “eventos por reino por década” e o resultado medido excede 40 em todas as sementes. O relatório preserva a contagem atual, sem reclassificar eventos nem alterar a importância para aparentar conformidade.

A fase F7 **não deve ser marcada como plenamente aceita** enquanto permanecerem o excesso da semente 18 no número de quedas e a divergência entre a meta de 40 eventos e a métrica de deltas. A F8 deve resolver explicitamente a interpretação/métrica de “evento por década”, calibrar os parâmetros envolvidos sem alterar o cânone e repetir o lote de 500 anos × 20 sementes. Vacâncias e mortalidade “coerentes com longevidade” seguem qualitativas e não receberam limiares inventados.
