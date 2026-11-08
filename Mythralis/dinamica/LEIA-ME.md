# dinamica/

Parâmetros PROPOSTOS de simulação (F4; `PLANO_MOTOR_MYTHRALIS.md`, seção 9). Gerados por `plano/ferramentas/gerar_dinamica.py` (idempotente) e lidos por `motor/dinamica.py`.

| Arquivo | Conteúdo |
|---|---|
| `classes_longevidade.txt` | 6 classes: maturidade, faixas, vida máxima e mortalidade (por mil ao ano) |
| `longevidade.txt` | classe de cada uma das 50 raças e 50 sub-raças (sub-raça herda da raça) |
| `faixas_etarias.txt` | jovem, adulta, madura, idosa e `?` como fração da vida máxima |
| `limiares.txt` | limiares e taxas de demografia, sucessão, política, relógios e eventos |
| `economia.txt` | 13 chaves do motor de economia e rotas (F5.1): padrões de `?`, alcance, safra, bloqueio de rota, revolta |
| `ecos.txt` | 7 chaves do motor de Ecos (F5.2): atraso, passos, chance por camada, degraus de certeza |
| `misterios.txt` | 2 chaves do motor de mistérios (F5.3): chance anual de pista e teto de pistas |
| `diplomacia.txt` | tratados, rupturas e quebras de contrato entre reinos vizinhos (F7.2) |
| `guerra.txt` | guerras, baixas, paz e queda de coroa entre reinos vizinhos (F7.3) |
| `fronteira.txt` | limite de anos do regime selado (F5.4) e tensão entre reinos vizinhos (F7.1) |
| `invariantes.txt` | 2 faixas dos alertas I-17 e I-18 (F6.2) |
| `relogios_dinamica.txt` | regra executável por relógio (taxa, pressões, travas, eventos, inação); `revisado=não` até o autor conferir |

O motor só lê estes `.txt`; arquivo ausente = dinâmica vazia (motores calados). Trocar um valor muda a simulação, nunca o cânone. O hash da dinâmica entra no cabeçalho da crônica e é conferido a cada bloco (I-01).
