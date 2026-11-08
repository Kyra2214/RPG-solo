# Coroas de Pedra — registro operacional de rotas (doc 07)

**Estado:** rascunho de R2-3.1 para aprovação do autor · **Data:** 2026-11-04 · **Etiqueta:** [PROPOSTO]; só organiza a evidência que já está nos docs 01, 03, 04, 05, 06 e 10 (D-31 a D-40). Mapa esquemático: `07-mapa-esquematico.md`.
**Regra central (herdada do reino 1):** uma conexão não listada aqui não é impossível; significa que ainda não tem percurso nem tempo validados. **Nenhuma rota, duração ou distância foi inventada**: os docs do reino 2 não trazem nenhuma duração, então **toda duração é `desconhecida`** e a decisão é do autor (D-41).
**Fronteira com Aeltharyn:** o contrato (§3.3) proíbe distância, tempo ou rota nova entre os reinos e passagem nova pela Espinha; a seção 4 só repete o que já está validado.

## 1. Como usar

Estados:
- **Documentada sem duração:** o texto liga duas cidades ou uma cidade a um fluxo, mas não diz quanto tempo leva.
- **Sugerida por dependência:** a ligação decorre de uma dependência ou de uma facção presente nas duas pontas (doc 05 §2.2; doc 04 §10); não há percurso descrito.
- **Pendente:** faltam evidências para dizer que existe percurso.

Regras de uso: quando o cânone não define um modificador, descrever a incerteza qualitativamente ou pedir decisão editorial; não converter "perto" ou "longe" em dias; não concluir que uma rota é segura, pública ou controlada por alguém só porque aparece aqui; ida e volta não precisam ter o mesmo tempo, mas a diferença exige causa plausível (neve, cheia, carga, guia).

## 2. Ligações entre cidades e fluxos

| ID editorial | Ligação | Meio ou condição descrita | Duração | Estado | Restrição de uso |
|---|---|---|---|---|---|
| `route.concilio-pedra-ata` | as nove outras cidades ↔ Pedra-Ata | delegações ao Pátio das Delegações para o Concílio (doc 04 §2) | desconhecida | Documentada sem duração | vale para cada uma das nove, separadamente; nenhuma ordem de distância entre elas |
| `route.altavento-passos` | Altavento ↔ cidades que dependem da Rede dos Passos Altos | passos de altitude, mensageiros e guias de neve (F-03; doc 01 C-02) | desconhecida | Documentada sem duração | nevasca fecha os passos (CL-07); as cidades ligadas não são nomeadas |
| `route.altavento-pedra-ata` | Altavento ↔ Pedra-Ata | mensageiros carregam cópias para o Arquivo (F-03 aliada de F-14) | desconhecida | Sugerida por dependência | ligação de mensagem, não de viajante a pé |
| `route.barcaalta-portao-cinza` | Barcaalta ↔ Portão-Cinza | a Companhia da Descida (F-05) tem presença nas duas; Lei de Passagem (L-04) e arbitragem de passagem | desconhecida | Sugerida por dependência | sem percurso descrito |
| `route.barcaalta-argente` | Barcaalta ↔ fora do reino, pelo Rio Argente | cais da Descida (doc 05 §3; M-05 §5) | desconhecida | Documentada sem duração | o que há rio abaixo não é descrito; o Argente não toca Aeltharyn (M-04 G-06) |
| `route.barcaalta-rodaviva` | Barcaalta ↔ Rodaviva | Casa dos Despertos (F-11) presente nas duas; peças e construtos de carga (doc 05 §2.2) | desconhecida | Sugerida por dependência | sem percurso descrito |
| `route.barcaalta-brasalume` | Barcaalta ↔ Brasalume | carga de metal e ferramentas para o cais (doc 05 §2.2; C-04); Casa das Fornalhas aliada da Companhia da Descida (F-01 × F-05) | desconhecida | Sugerida por dependência | sem percurso descrito |
| `route.veiaclara-jusante` | Veiaclara ↔ cidades a jusante | água medida pelas comportas (doc 04 §7; L-02) | desconhecida | Documentada sem duração | as cidades a jusante não são nomeadas; vale só dentro das Coroas |
| `route.portao-cinza-limiar` | Portão-Cinza ↔ limiar do esporão | portões duplos e galerias de vigia (doc 01 parte 3 §5; doc 05 §2.3) | desconhecida | Documentada sem duração | **não é rota para Aeltharyn**; ver seção 4 |

### 2.1 Pares sem ligação descrita

As demais ligações entre as dez cidades (por exemplo Brasalume ↔ Rodaviva, Sinuosa ↔ Geodália, Cumieira ↔ Altavento) **não têm evidência** nos docs: estado **Pendente**. Nenhuma foi criada. Brasalume ↔ Rodaviva aparece como dependência de ofício (metal e peças, ASSUMINDO no doc 05 §2.2) mas não como rota. Se o autor quiser um mapa completo, a decisão é D-41.

## 3. Acessos dos assentamentos

Todos os 20 assentamentos têm cidade-mãe (registro mestre). **Nenhum tem duração de acesso no cânone**: a categoria espacial vem do doc 03 e a duração fica `desconhecida`. Um assentamento "periurbano" não implica acesso rápido; não aplicar um tempo padrão.

| Assentamento | Cidade-mãe | Categoria espacial (doc 03) | Acesso a partir da cidade-mãe | Duração |
|---|---|---|---|---|
| `settlement.cinzeiro` (Cinzeiro) | Brasalume | Distrito ou núcleo periurbano | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.veio-fundo` (Veio-Fundo) | Brasalume | Comunidade afastada | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.ninho-seco` (Ninho-Seco) | Altavento | Posto ou núcleo sazonal | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.rastro-branco` (Rastro-Branco) | Altavento | Comunidade afastada | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.comporta-velha` (Comporta-Velha) | Veiaclara | Distrito ou núcleo periurbano | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.lago-de-cima` (Lago-de-Cima) | Veiaclara | Vila próxima | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.cordoaria` (Cordoaria) | Barcaalta | Bairro urbano | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.primeira-rampa` (Primeira-Rampa) | Barcaalta | Distrito ou núcleo periurbano | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.albergue-de-cima` (Albergue-de-Cima) | Portão-Cinza | Posto ou núcleo sazonal | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.galeria-alta` (Galeria-Alta) | Portão-Cinza | Distrito ou núcleo periurbano | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.fenda-mercado` (Fenda-Mercado) | Sinuosa | Bairro urbano | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.boca-larga` (Boca-Larga) | Sinuosa | Posto ou núcleo sazonal | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.degrau-pequeno` (Degrau-Pequeno) | Cumieira | Bairro urbano | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.pasto-de-cume` (Pasto-de-Cume) | Cumieira | Comunidade afastada | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.roda-menor` (Roda-Menor) | Rodaviva | Distrito ou núcleo periurbano | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.oficina-sem-dono` (Oficina-Sem-Dono) | Rodaviva | Indefinido | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.poco-alto` (Poço-Alto) | Geodália | Distrito ou núcleo periurbano | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.corte-fino` (Corte-Fino) | Geodália | Vila próxima | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.patio-das-delegacoes` (Pátio-das-Delegações) | Pedra-Ata | Bairro urbano | descrito só pelo doc 03; sem percurso | desconhecida |
| `settlement.arquivo-velho` (Arquivo-Velho) | Pedra-Ata | Distrito ou núcleo periurbano | descrito só pelo doc 03; sem percurso | desconhecida |

## 4. Ligações com Aeltharyn (só o já validado)

O reino 1 (doc 07 de Aeltharyn) valida rotas só dentro de Aeltharyn, incluindo o corredor da Espinha (Caladwen–Torvelume–Vael-Tir). **Nenhuma rota validada liga as Coroas a Aeltharyn.**

| Ligação | Evidência | Estado | Limite |
|---|---|---|---|
| Portão-Cinza ↔ Torvelume | nenhuma; as duas cidades ficam em lados opostos da zona-tampão da Espinha (M-04 G-04) | **sem rota, sem tempo** | nenhuma rota nova nem passagem nova pela Espinha (contrato §3.3) |
| Coroas ↔ Aeltharyn (qualquer par de cidades) | nenhuma | **sem rota, sem tempo** | nenhuma distância entre os reinos (M-04 G-02) |
| Lúmen | a nascente fica nas encostas das Coroas (G-03) | contato permitido, **não é rota** | não afeta cheias em Aeltharyn sem registro (contrato §4) |
| Argente | eixo do oeste; não toca Aeltharyn (G-06) | contato permitido, **não é rota** | a Descida não chega a Aeltharyn |

Residentes individuais de Aeltharyn nas Coroas (doc 10 §5) e residentes individuais das Coroas em Torvelume (contrato §4) **não criam rota**: como chegaram é desconhecido.

## 5. Conferências

- 9 ligações descritas (5 documentadas sem duração, uma delas o limiar sem rota; 4 sugeridas por dependência); 20 acessos de assentamentos; 4 linhas de fronteira.
- 0 duração, distância ou rota inventada; 0 rota Coroas–Aeltharyn; 0 passagem nova pela Espinha.
- Termos de fronteira (Torvelume, Espinha, Lúmen, Argente): só na seção 4, como "sem rota" ou "contato permitido".
- Ainda faltam os **tempos de viagem**: decisão do autor (D-41).

## 6. Decisão pendente

**D-41:** os tempos de viagem internos não existem no cânone. Opções: **A.** manter tudo `desconhecida` até o autor decidir (recomendada; critério "nenhuma rota ou tempo inventado" cumprido); **B.** o autor fornece tempos para as 9 ligações (e quais pares entram); **C.** o autor autoriza propostas de tempo marcadas como ASSUMINDO. **Recomendação: A.**
