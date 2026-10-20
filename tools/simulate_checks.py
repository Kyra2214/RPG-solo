#!/usr/bin/env python3
"""Simula a regra POINTS do sistema próprio.

Uso: python3 tools/simulate_checks.py [--runs 100000]
Zero pontos dá apenas a chance mínima (uma face); cada ponto positivo acrescenta
uma face de sucesso até lados-1. A face 1 continua sendo falha.
"""

from __future__ import annotations

import argparse
import random

DICE_BY_DIFFICULTY = {"fácil": 4, "média": 6, "difícil": 8, "heroica": 10}


def success_faces(points: int, sides: int) -> int:
    return max(1, min(max(points, 0) + 1, max(1, sides - 1)))


def exact_probability(points: int, sides: int) -> float:
    return success_faces(points, sides) / sides


def empirical_probability(points: int, sides: int, runs: int, rng: random.Random) -> float:
    wins = sum(rng.randint(1, sides) >= sides + 1 - success_faces(points, sides) for _ in range(runs))
    return wins / runs


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--runs", type=int, default=100_000)
    args = parser.parse_args()
    if args.runs < 1:
        parser.error("--runs deve ser positivo")

    rng = random.Random(20261018)
    print("# Simulação de testes por pontos")
    print()
    print(f"Amostras por célula: {args.runs:,}. Semente: 20261018.")
    print()
    print("| Pontos | " + " | ".join(f"{name} (d{sides})" for name, sides in DICE_BY_DIFFICULTY.items()) + " |")
    print("|---:|" + "---:|" * len(DICE_BY_DIFFICULTY))
    for points in range(0, 6):
        values = []
        for sides in DICE_BY_DIFFICULTY.values():
            exact = exact_probability(points, sides)
            empirical = empirical_probability(points, sides, args.runs, rng)
            values.append(f"{exact:.1%} ({empirical:.1%})")
        print(f"| {points} | " + " | ".join(values) + " |")


if __name__ == "__main__":
    main()
