#!/usr/bin/env python3
"""Gera prompts, manifesto e ícones SVG procedurais (M12) a partir do catálogo.
Uso: python3 tools/gerar_imagens.py   (offline; sobrescreve imagens/)"""
import csv, hashlib, json, math, os
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
PKG = RAIZ / "rules/src/main/resources/packages/sistema-proprio"
OUT = RAIZ / "imagens"
cat = json.loads((PKG / "personagem-catalogo.json").read_text(encoding="utf8"))
tax = json.loads((PKG / "taxonomia-criacao.json").read_text(encoding="utf8"))

ESTILO = "v1"
BASE = ("Emblema circular de fantasia em estilo de gravura sobre pergaminho, paleta quente de dourado, cobre e "
        "marrom escuro, traço limpo, silhueta simples e legível em 36 px, motivo centralizado, fundo sólido neutro "
        "escuro, sem texto, sem letras, sem rostos detalhados, sem personagens ou marcas conhecidas")
GRUPOS = [("raca", "races", "racas"), ("subraca", "subraces", None), ("classe", "classes", "classes"),
          ("profissao", "professions", "profissoes"), ("hibrido", "hybrids", None)]
HUE = {"raca": 32, "subraca": 165, "classe": 212, "profissao": 98, "hibrido": 300}
ROTULO = {"raca": "povo", "subraca": "variação de povo", "classe": "classe de aventureiro",
          "profissao": "profissão", "hibrido": "ascendência mista"}

def h(texto):
    return int(hashlib.sha1(texto.encode("utf8")).hexdigest(), 16)

def esc(s):
    return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace('"', "&quot;")

def prompt(grupo, e, tx):
    partes = [BASE + ".", f"Tema: {e['name']} ({ROTULO[grupo]}).", f"Ideia: {e['summary']}"]
    if tx:
        ctx = tx["categoria"] if tx["categoria"] == tx["subgrupo"] else f"{tx['categoria']}, {tx['subgrupo']}"
        partes.append(f"Contexto: {ctx}.")
    if grupo == "subraca":
        partes.append(f"Variação do povo {e['parentRaceId']}.")
    if grupo == "hibrido":
        partes.append("Combine os símbolos de: " + " e ".join(e["raceIds"]) + ".")
    return " ".join(partes)

def svg(grupo, e):
    n = h(e["id"])
    hue = (HUE[grupo] + (n % 25) - 12) % 360
    pontas = 5 + n % 6
    giro = (n >> 8) % 360
    r1, r2 = 44, 27 + (n >> 4) % 8
    pts = []
    for i in range(pontas * 2):
        a = math.radians(giro + i * 180 / pontas)
        r = r1 if i % 2 == 0 else r2
        pts.append(f"{64 + r * math.sin(a):.1f},{64 - r * math.cos(a):.1f}")
    anel = f"hsl({hue},62%,58%)"
    miolo = f"hsl({hue},45%,22%)"
    letra = esc(e["name"].strip()[:1].upper())
    pontilhado = 3 + (n >> 12) % 4
    return (f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 128 128" width="128" height="128" role="img" '
            f'aria-label="{esc(e["name"])}"><title>{esc(e["name"])}</title>'
            f'<circle cx="64" cy="64" r="62" fill="#1B1714"/>'
            f'<circle cx="64" cy="64" r="58" fill="none" stroke="{anel}" stroke-width="3"/>'
            f'<circle cx="64" cy="64" r="52" fill="none" stroke="{anel}" stroke-width="1" stroke-dasharray="{pontilhado} 4" opacity=".7"/>'
            f'<polygon points="{" ".join(pts)}" fill="{miolo}" stroke="{anel}" stroke-width="1.5" stroke-linejoin="round"/>'
            f'<circle cx="64" cy="64" r="19" fill="#1B1714" stroke="{anel}" stroke-width="1.5"/>'
            f'<text x="64" y="73" text-anchor="middle" font-family="Georgia, serif" font-size="26" font-weight="700" fill="{anel}">{letra}</text>'
            f'</svg>\n')

manifesto, linhas = [], []
for grupo, chave, tk in GRUPOS:
    pasta = OUT / "icones" / grupo
    pasta.mkdir(parents=True, exist_ok=True)
    for e in cat[chave]:
        tx = tax[tk].get(e["id"]) if tk else None
        p = prompt(grupo, e, tx)
        (pasta / f"{e['id']}.svg").write_text(svg(grupo, e), encoding="utf8")
        manifesto.append({"id": e["id"], "grupo": grupo, "nivel": "icone",
                          "arquivo": f"icones/{grupo}/{e['id']}.webp", "svg": f"icones/{grupo}/{e['id']}.svg",
                          "versao": 1, "prompt": p, "estilo": ESTILO, "status": "pendente", "geradaEm": None})
        linhas.append([grupo, e["id"], e["name"], f"icones/{grupo}/{e['id']}.webp", p])

(OUT / "imagens-manifesto.json").write_text(json.dumps(manifesto, ensure_ascii=False, indent=2) + "\n", encoding="utf8")
with open(OUT / "prompts.csv", "w", encoding="utf8", newline="") as f:
    w = csv.writer(f)
    w.writerow(["grupo", "id", "nome", "arquivo_alvo", "prompt"])
    w.writerows(linhas)
(OUT / "PROMPT_BASE.md").write_text(f"# Prompt-base (estilo {ESTILO})\n\n{BASE}.\n\nCada linha de `prompts.csv` já inclui este texto. Saída desejada: WebP 128 px (ícone), nome igual ao `arquivo_alvo`.\n", encoding="utf8")
blocos = []
for grupo, _, _ in GRUPOS:
    fig = "".join(f'<figure><img src="icones/{m["grupo"]}/{m["id"]}.svg" width="72" height="72" alt=""><figcaption>{esc(m["id"])}</figcaption></figure>' for m in manifesto if m["grupo"] == grupo)
    blocos.append(f"<h2>{grupo}</h2><div class=g>{fig}</div>")
(OUT / "previa.html").write_text('<!doctype html><meta charset=utf-8><title>Prévia dos ícones</title><style>body{background:#14110f;color:#ede3d3;font:14px sans-serif;margin:16px}.g{display:flex;flex-wrap:wrap;gap:10px}figure{margin:0;width:84px;text-align:center}figcaption{font-size:10px;word-break:break-all}</style>' + "".join(blocos), encoding="utf8")
print(len(manifesto))
