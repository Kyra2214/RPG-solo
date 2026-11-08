"""Uso: python3 -I motor/validar_mundo.py [saida.txt]  — valida os 5 reinos juntos (F2)."""
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import carregador as C, validador as V, dinamica as D

w = C.carregar()
F = V.validar(w, D.Dinamica()); e, a = V.resumo(F)
linhas = ['codigo|gravidade|entidade|mensagem'] + ['|'.join(x) for x in F]
linhas.append('# resumo: %d erros, %d avisos; %d reinos, %d cidades, %d NPCs, %d facções, %d relógios; molde sha256=%s' % (
    e, a, len(w.reinos), len(w.t['cidade']), len(w.t['npc']), len(w.t['faccao']), len(w.t['relogio']), w.hash_molde[:16]))
dest = sys.argv[1] if len(sys.argv) > 1 else os.path.join(C.RAIZ, 'saidas', 'validacao-mundo.txt')
open(dest, 'w', encoding='utf-8', newline='\n').write('\n'.join(linhas) + '\n')
print(linhas[-1]); sys.exit(1 if e else 0)
