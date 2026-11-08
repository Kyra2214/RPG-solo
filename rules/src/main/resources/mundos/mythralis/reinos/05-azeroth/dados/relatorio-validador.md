# Azeroth — relatório do validador (R5-7.1, D-170)

**Comando:** `python plano/ferramentas/converter_reino5.py` e depois `python plano/ferramentas/validar_reino.py reinos/05-azeroth/dados`. Saída bruta: `relatorio-validador.txt`.
**Resultado:** **0 erro, 7 avisos, 0 exceções aplicadas.** 21 arquivos `.txt` em `dados/` (inclui `excecoes.txt` e `ids_aposentados.txt`, vazios de registro), 485 registros. Só reorganiza os CSV e docs já escritos; nenhum fato novo.

## Avisos (todos esperados)

- **V-06 (3):** cidade, facção e npc com fonte "proposto": aguardam o autor.
- **V-23:** as dez cidades são conexas pelas nove ligações do doc 07, todas de duração `desconhecida` (D-153); nenhuma rota ganhou tempo.
- **V-26:** revisão manual contra M-04.
- **V-31:** só um reino em composição neste conjunto.
- **V-42:** Pilotos da Enseada com aliado ou rival `desconhecido` (motivo no doc 04 §10).

## Ajustes de conversão (sem alterar as fontes)

- Chave do reino `kingdom.azeroth` e calendário `calendario.contagem-das-chaves` (ASSUMINDO: nenhum doc define a chave); capital `—` (três capitais de coroa e Meiovau comum).
- Alvamar tem dois governantes de lugar (Pelmo Balança-de-Porto e Dorgan Escudo-Lento): `governante` com os dois IDs separados por `;`.
- Papel `herdeiro` derivado da categoria do registro mestre (Tomlin, Rilla, Astrel).
- Os oito heróis lendários falecidos não têm classe no alinhamento (`—`): o validador passou a aceitar `—` em `classe` só quando `vivo = não` (V-12). Os relatórios dos reinos 2, 3 e 4 saem idênticos (40, 22 e 11 avisos).
- V-34: texto do aviso de herdeiro ganhou ramo para Azeroth (não dispara: há três herdeiros).
- Recursos: os fluxos do doc 08 §5 entram como transportador (Grão e mar → Vaudouro; As Costas → Enseada-Vigia; As Fugas do Véu → Meiovau; Aviso e hora → Torreclara), só do lado de cá, sem quantidade (D-63).
- Rotas: só as nove ligações entre dois lugares existentes do doc 07, duração `?`; as cinco linhas de fluxo, rio ou sinais não viram `rota.txt`.
