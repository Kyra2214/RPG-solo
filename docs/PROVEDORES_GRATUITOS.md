# Provedores gratuitos pré-configurados (OpenRouter e Groq)

Na aba Config, **OpenRouter (grátis)** e **Groq (grátis)** já trazem o endereço do provedor. O usuário só cola a chave. **Personalizado** mantém o modo antigo (endpoint, modelo, modelos barato/melhor, Anthropic).

## Fluxo

1. Escolher o provedor e colar a chave.
2. **Buscar modelos gratuitos:** chama `GET /models` do provedor, confirma a chave e lista só modelos gratuitos de texto, do mais indicado ao menos.
3. Escolher o modelo principal (o primeiro já vem marcado). Os demais da lista viram reserva, na ordem.
   A tela exibe até 12 modelos; só os exibidos entram na cadeia de reserva.
4. **Testar conexão** e **Salvar e usar**.

## Como os modelos são escolhidos

- **OpenRouter:** gratuito se o id termina em `:free` ou se `pricing.prompt` e `pricing.completion` são zero. Só saída `text`. Fora: guard, embeddings, áudio.
- **Groq:** modelos ativos, exceto áudio (whisper, tts, playai, orpheus), guard e embeddings; modelos de chat `compound` permanecem elegíveis.
- **Ordem:** primeiro quem declara suporte a JSON (`response_format`), depois não-raciocínio antes de raciocínio (r1, thinking, qwq), depois maior número de parâmetros (`70b`) e maior contexto.

## Troca automática (`FallbackProvider`)

- Se o modelo falhar por limite (429), erro do servidor (5xx), recusa do pedido ou resposta ilegível (modelo fora do ar, id aposentado, contexto pequeno demais), a **mesma jogada** continua no próximo.
- O modelo que falhou fica de molho: 5 min (limite), 2 min (servidor), 10 min (indisponível/recusa). As próximas jogadas já vão direto ao seguinte.
- Chave inválida e falta de internet **não** trocam de modelo (não adiantaria).
- Em streaming, só troca **antes** de chegar o primeiro trecho de texto; depois disso o erro sobe, para não duplicar a narração.
- Se todos estiverem de molho, o app avisa o limite sem gastar requisições.

## Limites conhecidos

- Modelos gratuitos mudam com frequência: se algo parar, buscar de novo.
- A lista é guardada ao salvar; ela não se atualiza sozinha. Reservas aposentados saem da jogada pela troca automática.
- Não validado em rede real nem em aparelho (ver `docs/VALIDACAO_CI.md`).
