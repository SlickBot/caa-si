# Release signing & GitHub secrets

### Create keystore

```bash
# Create the release keystore (prompts for a password)
keytool -genkeypair -v \
  -keystore release-key.jks \
  -alias key \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -dname "CN=CHANGEME, O=CHANGEME, C=CHANGEME"
```

### Upload to GitHub Secrets

```bash
# Encode the keystore and upload it as the KEYSTORE_BASE64 secret
base64 -w0 release-key.jks | gh secret set KEYSTORE_BASE64

# Key alias (must match the -alias used above)
gh secret set KEY_ALIAS --body "key"

# Passwords — gh prompts for the value.
# Enter the SAME password for both.
gh secret set KEYSTORE_PASSWORD
gh secret set KEY_PASSWORD
```

### Add release tag and push

```bash
# Tag + push → triggers the Release workflow (builds & publishes signed APKs)
git tag v1.0
git push origin v1.0
```
