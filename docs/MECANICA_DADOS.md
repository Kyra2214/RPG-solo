# Mecânica de dados — sistema próprio

Este documento descreve a regra efetivamente usada pelo pacote `sistema-proprio`. A resolução é feita pelo motor; a IA recebe apenas o desfecho narrável.

## Testes por pontos

Perícias usam a comparação `POINTS`. A dificuldade escolhe o dado:

| Dificuldade | Dado | Faces que podem contar |
|---|---:|---:|
| Fácil | d4 | 1 a 3 |
| Média | d6 | 1 a 5 |
| Difícil | d8 | 1 a 7 |
| Heroica | d10 | 1 a 9 |

Zero pontos mantém a chance mínima de uma face (só o `6` no d6). Cada ponto positivo acrescenta uma face de sucesso: com 1 ponto em d6, vencem 5–6; com 2 pontos, vencem 4–6. Os pontos efetivos ficam limitados a `1..(lados - 1)`, então um teste nunca vira sucesso automático e a face 1 continua sendo falha.

A probabilidade exata é:

```text
faces_de_sucesso = limitar(max(pontos, 0) + 1, 1, lados - 1)
chance = faces_de_sucesso / lados
```

Quando os pontos já enchem o dado (`pontos + 1 >= lados - 1`), o rótulo do teste acrescenta "já no máximo neste dado": pontos extras não aumentam a chance (ex.: 2 ou mais pontos no d4, 4 ou mais no d6).

### Chance calculada pelo motor

`CheckSpec.successChance()` (`CheckOdds.kt`) devolve a chance exata de sucesso pleno enumerando a soma dos dados e aplicando `resolveCheck` (incluindo críticos e modificador). Ignora vantagem/desvantagem e dados explosivos; aceita até 10 dados. O cartão de rolagem do chat mostra "chance de sucesso: N%" (`RollView.chance`), omitida em rolagem simples, vantagem/desvantagem e dados explosivos. Em testes de pontos coincide com `faces_de_sucesso / lados`.

### Crítico e complicação nos testes de pontos

Decisão (M-06.2): o sistema próprio **não declara crítico nem complicação** em `pericia` e `esquiva` (`crit_sucesso` e `crit_falha` ficam `-`). Motivo: a dificuldade troca o dado, então uma faixa fixa mudaria muito de chance entre dados (um `1..1` vale 25% no d4 e 10% no d10). O motor já aceita faixas em testes de pontos: elas valem sobre a face natural do dado resolvido (`resolvedPool`) e o crítico de falha tem precedência. Um pacote que declare `1..1` ou `3..4` aceita essa variação de chance por dificuldade.

### Tabela de equilíbrio

A tabela abaixo mostra as probabilidades exatas. As taxas empíricas podem ser recalculadas pelo simulador com 100.000 rolagens por célula.

| Pontos | Fácil (d4) | Média (d6) | Difícil (d8) | Heroica (d10) |
|---:|---:|---:|---:|---:|
| 0 | 25,0% | 16,7% | 12,5% | 10,0% |
| 1 | 50,0% | 33,3% | 25,0% | 20,0% |
| 2 | 75,0% | 50,0% | 37,5% | 30,0% |
| 3 | 75,0% | 66,7% | 50,0% | 40,0% |
| 4 | 75,0% | 83,3% | 62,5% | 50,0% |
| 5 | 75,0% | 83,3% | 75,0% | 60,0% |

A tabela acima mostra as probabilidades exatas; as taxas empíricas podem variar com a semente e o número de amostras. A tabela será recalculada pelo simulador durante a validação do usuário. Para reproduzir:

```bash
python3 tools/simulate_checks.py --runs 100000
```

Decisão de implementação code-first: zero pontos conserva a chance mínima de uma face; o primeiro ponto acrescenta uma face e, portanto, melhora a chance. Esta regra substitui a versão anterior em que 0 e 1 ponto empatavam.

### Tabela empírica (M-06.5)

Gerada por `python3 tools/simulate_checks.py --runs 100000` em 21/10/2026 (semente 20261018). Formato: probabilidade exata (taxa simulada). As taxas simulam a regra do script; a execução do motor Kotlin ainda não foi validada.

Amostras por célula: 100,000. Semente: 20261018.

| Pontos | fácil (d4) | média (d6) | difícil (d8) | heroica (d10) |
|---:|---:|---:|---:|---:|
| 0 | 25.0% (25.0%) | 16.7% (16.4%) | 12.5% (12.5%) | 10.0% (9.9%) |
| 1 | 50.0% (49.9%) | 33.3% (33.4%) | 25.0% (25.1%) | 20.0% (20.1%) |
| 2 | 75.0% (75.1%) | 50.0% (49.8%) | 37.5% (37.3%) | 30.0% (30.1%) |
| 3 | 75.0% (75.0%) | 66.7% (66.8%) | 50.0% (50.1%) | 40.0% (40.1%) |
| 4 | 75.0% (75.1%) | 83.3% (83.4%) | 62.5% (62.7%) | 50.0% (49.8%) |
| 5 | 75.0% (74.9%) | 83.3% (83.2%) | 75.0% (74.9%) | 60.0% (59.9%) |
