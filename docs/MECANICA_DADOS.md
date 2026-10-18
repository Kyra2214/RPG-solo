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

Os pontos efetivos são limitados pelo motor a `1..(lados - 1)`. As faces de sucesso são contadas a partir do topo do dado. Assim, com 2 pontos em d6, as faces 5 e 6 vencem; com 0 pontos, a regra atual mantém a chance mínima de uma face (`6` no d6). A face 1 continua sendo falha.

A probabilidade exata é:

```text
faces_de_sucesso = limitar(pontos, 1, lados - 1)
chance = faces_de_sucesso / lados
```

### Tabela de equilíbrio

A tabela abaixo mostra a probabilidade exata. Os valores entre parênteses são a taxa observada pelo simulador com 100.000 rolagens por célula.

| Pontos | Fácil (d4) | Média (d6) | Difícil (d8) | Heroica (d10) |
|---:|---:|---:|---:|---:|
| 0 | 25,0% (25,0%) | 16,7% (16,7%) | 12,5% (12,5%) | 10,0% (10,0%) |
| 1 | 25,0% (25,0%) | 16,7% (16,7%) | 12,5% (12,5%) | 10,0% (10,0%) |
| 2 | 50,0% (50,1%) | 33,3% (33,3%) | 25,0% (25,0%) | 20,0% (20,0%) |
| 3 | 75,0% (75,0%) | 50,0% (50,0%) | 37,5% (37,5%) | 30,0% (30,0%) |
| 4 | 75,0% (75,0%) | 66,7% (66,7%) | 50,0% (50,0%) | 40,0% (40,0%) |
| 5 | 75,0% (75,0%) | 83,3% (83,3%) | 62,5% (62,5%) | 50,0% (50,0%) |

Os valores empíricos podem variar ligeiramente conforme a semente e o número de amostras. Para reproduzir a tabela:

```bash
python3 tools/simulate_checks.py --runs 100000
```

A diferença entre 0 e 1 ponto permanece uma decisão de design pendente. Ela está documentada explicitamente para não ser confundida com um bug silencioso.
