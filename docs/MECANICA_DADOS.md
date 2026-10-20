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
