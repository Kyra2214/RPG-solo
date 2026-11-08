# Vale Palancar — relatório do validador (R4-7.1, D-133)

**Comando:** `python plano/ferramentas/converter_reino4.py` e depois `python plano/ferramentas/validar_reino.py reinos/04-vale-palancar/dados`. Saída bruta: `relatorio-validador.txt`.
**Resultado:** **0 erro, 11 avisos, 2 exceções aplicadas.** 21 arquivos `.txt` em `dados/` (inclui `excecoes.txt` e `ids_aposentados.txt`), 405 registros. Só reorganiza os CSV e docs já escritos; nenhum fato novo.

## Avisos (todos esperados)

- **V-06 (3):** cidade, facção e npc com fonte "proposto": aguardam o autor.
- **V-23 e V-44, exceções registradas em `excecoes.txt`:** o doc 07 liga os lugares em dois grupos (Praia-Pardo, Rasovau, Encosta-Aberta; Tabuleiro-Velho, Terreiro-Maior, Vazante, Gramal) e deixa Alvasal sem ligação interna (só o Ermo de Sal, fora do reino). Não se inventa rota para fechar o grafo (contrato §5 item 4).
- **V-26:** revisão manual contra M-04.
- **V-30:** menos de 10 cidades (D-102: oito lugares).
- **V-31:** só um reino em composição neste conjunto.
- **V-34:** sem herdeiro; o reino não tem rei, o Capitão é escolhido por prova ou aclamação e o desfecho da sucessão é desconhecido (doc 04 §5).
- **V-42 (2):** Guardas da Marcha e Vigias da Cisterna com rival `desconhecido` (com motivo no doc 04).

## Ajustes de conversão (sem alterar as fontes)

- Capital: `—` (reino sem sede fixa; a Assembleia se reúne no Terreiro-Maior).
- Fatos dos mistérios apontam para `mist.mpl-NN-…` (ID completo); o validador passou a aceitar o prefixo `mist.` em `entidade` de `fato.txt` (os relatórios dos reinos 2 e 3 saem idênticos).
- Recursos citados em `12-eventos.csv` sem correspondente exato em `recurso.txt` entram com papel `?` e nota "termo de evento", sem quantidade (D-63); localidade = primeiro lugar do evento (ASSUMINDO).
- Mensagem de V-34 ganhou ramo próprio do reino 4.
