# Modelo de reino

Derivado do que o Reino das Florestas entregou. Serve de checklist para cada novo reino. Os arquivos de Aeltharyn são o exemplo vivo.

## 1. Peças de um reino (pasta `reinos/NN-nome/`)

| Arquivo | Conteúdo | Em Aeltharyn |
|---|---|---|
| `01-...md` | Documento principal (ver seção 2) | 34 seções, 1.207 linhas |
| `02-arquitetura-mundo-persistente.md` | Princípio: mundo continua sem jogadores, consulta sob demanda, camadas de informação | Vale para **todos** os reinos; não repetir por reino |
| `03-assentamentos-e-rede-politica.md` | 20 unidades comunitárias com liderança, obrigação, interesse e tensão | ✓ |
| `04-instituicoes-justica-pactos.md` | Competências da Coroa e das cidades, arbitragem, tributos, sucessão, secessão | ✓ |
| `05-auditoria-demografia-rotas-recursos.md` | Conferência aritmética e lacunas | ✓ |
| `06-modelo-operacional-e-geracao-de-aventuras.md` | Cadeia causal, geração de oportunidades, contexto mínimo para LLM pequena | Em grande parte reutilizável |
| `07-registro-operacional-de-rotas.md` | Ligações com tempo explícito; corredores sem duração marcados como desconhecidos | ✓ |
| `08-registro-operacional-de-recursos.md` | Produção, reservas, demanda, substitutos, dependências | ✓ |
| `09-matriz-material-dos-assentamentos.md` | Assentamentos × recursos × riscos | ✓ |
| `10-matriz-povos-nativos-cidades-e-forasteiros.md` | Povos nativos × cidades × forasteiros | ✓ |
| `11-fechamento-das-sete-fases.md` | Decisões, correções cruzadas, limites | ✓ |
| `README.md` | Índice, identidade resumida, estado | ✓ |

## 2. Seções do documento principal

Identidade; governo; história em eras; política atual; **as dez cidades**; rotas e distâncias; facções; heróis históricos; NPCs vivos; leis e costumes; economia e recursos; religião e magia; mistérios e conflitos ativos; acontecimentos iniciais possíveis; regras de consistência para narrar; critérios de conclusão; fundadores e memória das cidades; agenda sazonal; rede de heróis, guardiões e benfeitores; povos (origem, vizinhança, diáspora); gerações e divergência histórica; heróis no presente; tríades de referência por povo; dependências entre cidades; estado do reino e relógios; portas de conclusão; princípio político.

## 3. Ficha padrão de cidade

`ID` · `Tipo` · `População` (aproximada; nunca somar estimativas sobrepostas) · `Localização` · `Governo local` · `Economia` · `Defesas` (incluindo vulnerabilidades) · `Locais notáveis` · `Problemas atuais` · `Facções presentes` · `Rumores` · `Lenda` (com a versão antiga e a versão sagrada, quando diferirem) · `Função narrativa`.

## 4. Ficha padrão de NPC

`ID` · nome · idade ou faixa · raça/povo (ID do catálogo) · origem · cargo e títulos · cidade e facção · personalidade, valores, objetivos, medos e limites · capacidades, influência, recursos e **restrições** · relações pessoais e políticas · biografia e marcos · **conhecimento confirmado, suspeitas e desconhecido** · estado atual (vivo, morto, desaparecido, exilado) · **agenda atual** e **pressão** que condiciona suas decisões · condições plausíveis de evolução.

Aeltharyn usa a **tríade herói + guardião + benfeitor** por cidade e por povo (cada uma com 3 pessoas distintas). Manter para os outros reinos, ou justificar a troca.

## 5. Convenções

- **IDs permanentes** com prefixo: `kingdom.`, `city.`, `settlement.`, `npc.`, `faction.`, `route.`, `event.` (eventos-tipo, M4.2), `seed.` (sementes de aventura, M4.3), `rep.` (reputação por entidade) e `canal.` (intermediários), M4.4, `mist.` (mistérios com limites, M4.5), `fact.` (fatos com camada de informação, M5.2) e `cena.` (cenas de teste, M5.3) (e `clock.` no catálogo técnico). Em minúsculas, sem acento, hífens. Nunca reutilizar um ID.
- **Povos** usam os IDs do catálogo de personagem (`referencia/dados-brutos/personagem-catalogo.json`). Sub-raça e linhagem não contam como povo novo.
- **Estados de certeza:** confirmado, alegado, rumor, inferência, desconhecido. Lacuna de evidência fica **explícita**; não se inventa distância, estoque ou população para fechar buraco.
- Nenhuma raça é cidadã, vilã ou leal por natureza; cidades têm população mista e dissidência.
- Reinos não são caricaturas de bem ou mal.

## 6. Portas de conclusão (checklist)

- [ ] 10 cidades com ficha completa e função coerente com a geografia.
- [ ] 8 a 12 NPCs principais (≥ 2 heróis, ≥ 2 generais) com agenda e pressão distintas.
- [ ] Rotas com tempo definido ou marcadas como desconhecidas; sem rota inventada.
- [ ] Recursos e dependências entre cidades (nenhuma cidade funciona isolada).
- [ ] Povos alinhados ao catálogo (rodar o alinhamento como em `ALINHAMENTO_AELTHARYN.md`).
- [ ] Facções com objetivos, métodos, aliados e rivais; conflitos com causa concreta.
- [ ] Relógios iniciais cruzados com recursos, rotas e localidades; sem avanço automático.
- [ ] Mistérios sem solução predefinida.
- [ ] Contradições intencionais separadas de erros acidentais (registrar no fechamento).
- [ ] IDs únicos e todos os IDs citados existem.
- [ ] Documento de fechamento com limites explícitos.
