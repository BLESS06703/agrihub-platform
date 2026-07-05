# AgriHub Malawi - Cryptography Standards

## 1. Approved Algorithms

| Purpose | Algorithm | Parameters |
|---------|----------|-----------|
| Password hashing | Argon2id | memory=64MB, iterations=3, parallelism=4 |
| JWT signing | RS256 | RSA 2048-bit keys |
| Data at rest | AES-256-GCM | Random IV per encryption |
| Data in transit | TLS 1.3 | Strong ciphers only |
| File hashing | SHA-256 | N/A |
| Token generation | SecureRandom | 256-bit entropy |
| API key hashing | SHA-256 | Salted |

## 2. Key Management

- JWT private key: stored in Kubernetes secret / env variable
- JWT public key: embedded in application config
- Database encryption key: stored in vault, rotated annually
- API keys: hashed before storage (never stored plaintext)
- NEVER commit keys to version control

## 3. Data Encryption Rules

Encrypt at rest:
- Password hashes (already hashed)
- Refresh tokens (hashed)
- MFA secrets
- API keys (hashed)
- PII fields (email, phone — encrypt column)

Encrypt in transit:
- All HTTP traffic → HTTPS (TLS 1.3)
- Database connections → TLS
- Redis connections → TLS (stunnel or Redis TLS)
- MinIO → TLS

## 4. Random Number Generation

Always use: java.security.SecureRandom (never java.util.Random)

Use cases:
- Token generation: 32 bytes → hex encoded
- Session IDs: 16 bytes → hex encoded
- Password reset tokens: 32 bytes → hex encoded
- Invitation tokens: 32 bytes → hex encoded
