# AgriHub Malawi - Secure Coding Practices

## 1. Input Validation

Rule: NEVER trust user input. Validate everything.

- All API inputs validated server-side (not just client)
- Use Kotlin data classes with validation annotations
- Whitelist allowed values, don't blacklist
- Validate: type, length, format, range, business rules
- Reject invalid input with 422 + clear error message

SQL Injection Prevention:
- Use Exposed ORM exclusively (parameterized queries)
- Never concatenate strings into SQL
- Stored procedures also use parameterized queries

## 2. Output Encoding

- JSON: use kotlinx.serialization (auto-escapes)
- HTML (web admin): auto-escape in React/JSX
- PDF: sanitize user input before rendering
- Logs: strip control characters from user input

## 3. Authentication & Session

- JWT in Authorization: Bearer header
- Never in URL query parameters
- Validate on every request (Ktor plugin)
- Token blacklist for revoked tokens (Redis)

## 4. Access Control

- Authorize EVERY endpoint (deny by default)
- Tenant context check in repository layer
- Row-level security as defense-in-depth
- Test cross-tenant access in integration tests

## 5. File Uploads

- Validate file type (magic bytes, not extension)
- Limit file size: 10MB max
- Randomize filenames on storage
- Scan for malware (ClamAV in future)
- Serve from separate domain (CDN/MinIO)

## 6. Error Handling

- Never expose stack traces to users
- Return generic error messages
- Log detailed errors server-side with correlation ID
- Custom error pages for 401/403/404/500

## 7. Dependency Management

- Regular vulnerability scanning (OWASP Dependency Check)
- Keep dependencies updated (Dependabot/Renovate)
- Audit licenses for compliance
- Remove unused dependencies

## 8. Logging Sensitive Data

NEVER log:
- Passwords (even hashed)
- JWT tokens
- Credit card numbers
- Full PII (mask: j***@email.com)

ALWAYS log:
- Authentication events (success/failure)
- Authorization failures (403)
- Data modifications (audit log)
- Suspicious patterns (rapid 403s)
