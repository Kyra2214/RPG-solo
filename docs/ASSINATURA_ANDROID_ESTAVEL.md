# Assinatura Android estável

A keystore estável do RPG Solo foi criada para permitir que os próximos APKs sejam instalados por cima da versão anterior, sem desinstalação, desde que o `versionCode` seja incrementado.

## Identidade

- Alias: `rpgsolo`
- Tipo: PKCS12 / RSA 2048
- Validade: 15/11/2026 a 02/04/2054
- Fingerprint SHA-256: `2D:19:70:9F:EC:C1:65:8D:3D:B2:E0:D8:EC:68:EA:7B:99:5D:10:4C:E7:FB:C3:D2:5C:E9:AF:CA:DB:78:1A:D5`
- Keystore: mantida fora do repositório em `/home/ubuntu/.rpgsolo-signing/rpgsolo-stable-release.p12`

## Secrets do GitHub Actions

O workflow `.github/workflows/android-ci.yml` e `app/build.gradle.kts` já estão preparados para usar estes secrets:

- `ANDROID_SIGNING_KEYSTORE_BASE64`
- `ANDROID_SIGNING_STORE_PASSWORD`
- `ANDROID_SIGNING_KEY_ALIAS`
- `ANDROID_SIGNING_KEY_PASSWORD`

O token GitHub disponível nesta sessão não permitiu gravar secrets de Actions (`HTTP 403`). Portanto, eles precisam ser adicionados manualmente em **Settings → Secrets and variables → Actions → New repository secret** no repositório `Kyra2214/RPG-solo`.

Para gerar o primeiro valor a partir da keystore:

```bash
base64 -w0 rpgsolo-stable-release.p12
```

O APK `debug` passa a usar a assinatura estável quando os quatro secrets estão presentes. O workflow também usa `github.run_number` como `VERSION_CODE`, permitindo atualizações por cima.

## Transição da instalação atual

O APK já instalado, gerado antes desta configuração, foi assinado pela chave debug efêmera do runner. Será necessário desinstalá-lo uma única vez e instalar um APK gerado após os secrets serem configurados. Depois disso, as versões futuras poderão ser atualizadas normalmente por cima.

**Nunca commitar a keystore, as senhas ou o arquivo de backup.**
