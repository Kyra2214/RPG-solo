# Véu dos Malditos — relatório do validador (R3-7.1, D-96)

**Comando:** `python plano/ferramentas/converter_reino3.py` e depois `python plano/ferramentas/validar_reino.py reinos/03-veu-dos-malditos/dados`. Saída bruta: `relatorio-validador.txt`.
**Resultado:** **0 erro, 22 avisos, 6 exceções aplicadas.** 21 arquivos `.txt` em `dados/` (inclui `excecoes.txt` e `ids_aposentados.txt`), 390 registros.

## Avisos (todos esperados)

- **V-06 (3):** cidade, facção e npc com fonte "proposto": aguardam o autor.
- **V-10 (4) e V-21 (2), exceções registradas em `excecoes.txt`:** 4 eventos citam rotas do doc 07 sem dois locais existentes (`route.conselho-morroveu` é grupo de sete ligações, que viram sete rotas em `rota.txt`; `route.fozbreu-argente` e `route.beira-sal-ermo` saem do reino); `route.morroveu-selovale` e `route.morroveu-fozbreu` repetem o par de uma rota do Conselho, com outra natureza. `route.fozbreu-cabotagem` não é citada por evento.
- **V-15 (5):** os 5 heróis lendários de D-73 (`mursk`, `zibb`, `aun`, `lish`, `xalu`) não têm lugar nem facção (doc 10 §6.1: lugar, era e feitos `desconhecido`).
- **V-23:** as oito cidades só são conexas contando rotas de duração desconhecida (D-63: nenhuma duração).
- **V-26:** revisão manual contra M-04.
- **V-30:** 8 lugares em vez de 10 (D-66, escolha do autor).
- **V-31:** mundo com um reino neste conjunto.
- **V-34:** sem herdeiro; a sucessão é só pela Provação, desfecho `desconhecido` (doc 04 §5).
- **V-42 (3):** `faction.casa-dos-fios`, `faction.turnos-da-vigilia` e `faction.vigias-da-orla` com rival `desconhecido` e motivo registrado (D-71, D-76).
- **V-27, V-52 em assentamentos:** não ocorrem; o reino não tem assentamentos (D-66).

## Ajustes de conversão (ASSUMINDO, sem lore novo)

- `assentamento.txt` só com cabeçalho: os 8 lugares são `cidade.txt`; liderança, obrigação, interesse e tensão de `12-assentamentos.csv` não têm campo em `cidade.txt` (governante = `lideranca_id`) e seguem só no CSV e no doc 03.
- `recurso.txt`: termos de "rica em" (`produtor`), "depende de" (`consumidor`, prefixo `dependência:`), "pobre em" (`papel ?`, prefixo `falta:`) do doc 08; `producao`, `reserva` e `demanda` ficam `?` (D-63). Fluxos e dois termos usados por eventos (`cuidado dos mortos`, `pilotagem da barra`, `guarda de quem chega`) entram como no doc 08.
- `fato.txt`: marcador dos 8 fatos de relógio = `progresso inicial a/b`, como no reino 2.
- `npc.txt`: `profissao` `—` em 11 pessoas sem profissão no catálogo (heróis lendários e Nuvik); o validador passa a aceitar `—` nesse campo (como em `subraca`). `objetivo` = `agenda`; `facção` só nos 12 principais (ficha); `acesso` `?`.
- `papeis` de tríade (`heroi`, `guardiao`, `benfeitor`) vêm da tabela do doc 01 parte 2 §3, somados à categoria do registro; 8 de 8 lugares completos.
- Canais: coluna `rotas` do CSV não entra em `canal.txt` (esquema do reino 2).

## Mudanças nas ferramentas (reino 2 intocado)

`validar_reino.py`: colisão de ID contra os reinos de número menor (reino 2: só o 1, como antes; reino 3: 1 e 2); `profissao` `—` aceita; mensagem de V-34 conforme o reino. Relatório do reino 2 regerado e **idêntico** ao existente (0 erro, 40 avisos, 7 exceções).
