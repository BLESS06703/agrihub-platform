# AgriHub Malawi - Threat Model
# Version 1.0

## 1. Assets to Protect

| Asset | Sensitivity | Impact if Breached |
|-------|------------|-------------------|
| Farmer PII (name, phone, location) | HIGH | Identity theft, stalking |
| Farm GPS coordinates | MEDIUM | Competitive intelligence |
| Financial records | CRITICAL | Fraud, theft |
| Harvest/yield data | MEDIUM | Market manipulation |
| Login credentials | CRITICAL | Full account takeover |
| JWT signing keys | CRITICAL | Platform-wide breach |
| Tenant data (cross-tenant) | CRITICAL | Business collapse |

## 2. Threat Actors

| Actor | Motivation | Capability |
|-------|-----------|------------|
| Curious user | Explore other tenants' data | LOW |
| Competitor | Steal farming data | MEDIUM |
| Cybercriminal | Financial fraud | HIGH |
| Insider (employee) | Data theft/sabotage | HIGH |
| Nation-state | Food security intelligence | VERY HIGH |

## 3. Attack Vectors

### 3.1 API Attacks
- SQL Injection via unvalidated inputs
- JWT token forgery/brute-force
- IDOR (Insecure Direct Object Reference) - accessing other tenants' data
- Rate limit bypass for credential stuffing
- Mass assignment via unvalidated JSON bodies

### 3.2 Mobile Attacks
- APK decompilation revealing API keys
- Local SQLite database extraction
- Man-in-the-middle on fake WiFi
- Token theft from rooted devices

### 3.3 Infrastructure Attacks
- Unpatched PostgreSQL vulnerabilities
- Docker escape
- Redis unprotected instance exposure
- MinIO anonymous access

## 4. Mitigations

| Attack | Mitigation |
|--------|-----------|
| SQL Injection | Exposed ORM parameterized queries only |
| JWT forgery | RS256 asymmetric signing, 15-min TTL |
| IDOR | Tenant context check on every query |
| Rate limiting | Nginx + application-level per-user limits |
| APK decompilation | ProGuard/R8 obfuscation, no secrets in client |
| Local DB extraction | Android Keystore encryption, EncryptedSharedPreferences |
| MITM | Certificate pinning, TLS 1.3 only |
| Docker escape | Non-root user, read-only filesystem, seccomp profiles |

## 5. Risk Matrix

| Risk | Likelihood | Impact | Priority |
|------|-----------|--------|----------|
| Cross-tenant data access | MEDIUM | CRITICAL | P0 |
| SQL Injection | LOW | CRITICAL | P0 |
| JWT key compromise | LOW | CRITICAL | P0 |
| Mobile token theft | MEDIUM | HIGH | P1 |
| DDoS on API | MEDIUM | HIGH | P1 |
| Insider data theft | LOW | HIGH | P1 |
| Unpatched CVE | HIGH | MEDIUM | P2 |
