# AgriHub Malawi - Authentication & Authorization

## 1. Authentication Flow

### Registration
1. User submits: email, phone, full_name, password
2. Password validated: min 8 chars, 1 upper, 1 lower, 1 digit, 1 special
3. Password hashed with Argon2id (memory=64MB, iterations=3, parallelism=4)
4. Verification code sent to email (6-digit, 10-min expiry)
5. Account activated upon verification

### Login
1. User submits: email + password
2. Argon2id hash comparison
3. Rate limit: 5 attempts per 15 minutes per IP
4. On success: issue JWT access token (15 min) + refresh token (7 days)
5. On failure: increment counter, lock after 5 failures (30 min)
6. Log login event to audit_logs

### Token Refresh
1. Client sends refresh token
2. Server validates: exists, not revoked, not expired
3. Rotate refresh token (old invalidated, new issued)
4. New access token returned

### Logout
1. Client sends refresh token to revoke
2. Server marks token as revoked
3. Client discards both tokens

## 2. JWT Specification

Algorithm: RS256 (RSA 2048-bit key pair)

Payload claims:
- sub: user UUID
- tenant_id: tenant UUID
- tenant_tier: SHARED|SCHEMA|DATABASE
- tenant_schema: schema name (if applicable)
- roles: ["FARMER", "VIEWER"]
- permissions: ["farm:create:own", "farm:read:own", ...]
- iat: issued at
- exp: expiration (15 minutes)
- jti: unique token ID

Storage:
- Mobile: Android Keystore (hardware-backed)
- Web: HTTP-only, Secure, SameSite=Strict cookie

## 3. Authorization (RBAC)

Permission format: resource:action:scope

Resources: farm, field, crop, harvest, livestock, animal,
           finance, inventory, marketplace, warehouse, report,
           user, tenant, settings

Actions: create, read, update, delete, approve, export, manage

Scopes:
- own: records created by this user
- tenant: all records in user's tenant
- all: platform-wide (super admin only)

Authorization check order:
1. Extract JWT, validate signature + expiry
2. Extract tenant context
3. Check route's required permission against user's permissions
4. If user lacks permission → 403 Forbidden
5. Tenant-scope applied automatically in query layer

## 4. Password Policy

- Minimum 8 characters
- At least 1 uppercase (A-Z)
- At least 1 lowercase (a-z)
- At least 1 digit (0-9)
- At least 1 special character
- Cannot reuse last 3 passwords
- Expires after 90 days (configurable)
- Change on first login after reset
