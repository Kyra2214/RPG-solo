# Assinatura Android estável

A keystore estável do RPG Solo foi criada para permitir que os próximos APKs sejam instalados por cima da versão anterior, sem desinstalação, desde que o `versionCode` seja incrementado.

## Identidade

- Alias: `rpgsolo`
- Tipo: PKCS12 / RSA 2048
- Validade: 15/11/2026 a 02/04/2054
- Fingerprint SHA-256 atual: `49:E1:5B:B7:DB:FE:F3:9F:E9:7E:1C:E6:52:BE:B2:A2:05:42:EF:E2:42:62:3E:16:09:FD:D1:2F:81:7D:4F:B1`
- A keystore foi regenerada em 15/11/2026 com a mesma senha para o armazenamento e para a chave, eliminando a incompatibilidade de senha detectada no primeiro build.
- Keystore: mantida fora do repositório em `/home/ubuntu/.rpgsolo-signing/rpgsolo-stable-release.p12`

## Secrets do GitHub Actions

O workflow `.github/workflows/android-ci.yml` e `app/build.gradle.kts` já estão preparados para usar estes secrets:

- `ANDROID_SIGNING_KEYSTORE_BASE64`
- `ANDROID_SIGNING_STORE_PASSWORD`
- `ANDROID_SIGNING_KEY_ALIAS`
- `ANDROID_SIGNING_KEY_PASSWORD` (mantido por compatibilidade; o workflow usa a senha do store para a chave)

O token GitHub disponível nesta sessão não permitiu gravar secrets de Actions (`HTTP 403`). Portanto, eles precisam ser adicionados manualmente em **Settings → Secrets and variables → Actions → New repository secret** no repositório `Kyra2214/RPG-solo`.

Para gerar o primeiro valor a partir da keystore:

```bash
base64 -w0 rpgsolo-stable-release.p12
```

O APK `debug` passa a usar a assinatura estável quando os quatro secrets estão presentes. O workflow também usa `github.run_number` como `VERSION_CODE`, permitindo atualizações por cima.

## Transição da instalação atual

O APK já instalado, gerado antes desta configuração, foi assinado pela chave debug efêmera do runner. Será necessário desinstalá-lo uma única vez e instalar um APK gerado após os secrets serem configurados. Depois disso, as versões futuras poderão ser atualizadas normalmente por cima.

**Nunca commitar a keystore, as senhas ou o arquivo de backup.**
