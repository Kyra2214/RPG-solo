# Relatório F8.3 — quedas de coroa e lote de 500 anos × 20 sementes

**Motor F8.2, sementes 1 a 20, 500 anos, passo 10.** 20 de 20 rodadas sem falha; invariantes I-01 a I-28 sem violação.

## Quedas de coroa (meta: 0 a 2 por semente)

| quedas na semente | 0 | 1 | 2 | 3 |
|---|---|---|---|---|
| sementes | 11 | 6 | 2 | 1 |

Semente 18: anos 73, 82 e 177. Reinos que caíram, em sementes: Azeroth 5, Coroas de Pedra 4, Aeltharyn 2, Vale Palancar 1, Véu dos Malditos 1; nenhum cai em todas. **Decisão D-177:** nada foi recalibrado; 19 de 20 sementes cumprem a meta e o desvio é uma cauda explicada.

## Métricas por nível (D-M14)

| métrica | resultado nas 20 sementes |
|---|---|
| nível 1 por reino e década, mediana | 1 a 2 |
| nível 1, máximo em uma década | 8 a 12 |
| nível 1, décadas de reino vazias | 28% a 36% |
| níveis 1+2 fora de 3 a 40 | 9% a 14% (semente 1: 7 abaixo de 3 e 17 acima de 40, de 250) |
| cidades sem evento de nível 1 ou 2 | 0 em todas as sementes (46 de 46 cobertas) |

## Povos (D-M15)

Pactos entre povos formados por semente: 33 a 50 (mediana 40,5); encerrados: 14 a 27 (mediana 19,5). Ao menos um pacto em 20 de 20 sementes.

## O que continua aberto

- **Excesso acima de 40 nos níveis 1+2**, sobretudo em Vale Palancar (por exemplo, décadas com 43, 55 e 57 eventos na semente 1). Falta separar quanto vem de sucessões em cascata e quanto de laços de povos com mediador; é ajuste da F8.4.
- **Antiga métrica de deltas 3+ por década:** continua em 82 a 97 (meta antiga: 40), porque conta bloqueios de rota. Pelo D-M14 deixa de ser meta; segue no `lote.md` como informação.
- **Mistérios resolvidos:** 0 em 20 sementes (esperado: ninguém investiga sem jogador).
- Aceite global da F7 e fechamento (README, CHANGELOG): módulo F8.4.
