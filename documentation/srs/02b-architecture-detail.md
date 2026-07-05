# 2. System Architecture - Detailed View

## Tenant Resolution Flow

1. Request arrives with JWT token
2. JWT decoded: extracts tenant_id, tenant_tier, user roles
3. Tenant Registry Lookup:
   - Tier 1 (SHARED): Route to shared database, set tenant context via RLS
   - Tier 2 (SCHEMA): Route to database, set search_path to tenant schema
   - Tier 3 (DATABASE): Route to dedicated database, connect directly
4. Query executes within tenant context
5. Response returned

## Offline Sync Architecture

Mobile Local Storage:
- Room Database (SQLite) for all domain data
- DataStore for preferences and last_sync_timestamp
- Sync Queue: Outgoing changes stored locally until connectivity

Sync Protocol:
- Outgoing: POST /v1/sync/push with batch of changes
- Incoming: GET /v1/sync/pull?since={timestamp}
- Conflict Resolution: Server-timestamp-based last-write-wins
  - Financial records: Server always wins
  - Farm data: Timestamp merge
  - Inventory: Last-write-wins with notification

## Security Architecture

Auth Flow: Login -> Argon2id verification -> JWT (15min) + Refresh Token (7 days)
RBAC Model: User -> Role -> Permissions (resource:action:scope)
Token Storage: Android Keystore (mobile), HTTP-only cookies (web)

## Database Principles

- Every table: id (UUID), tenant_id, created_at, updated_at, deleted_at
- Soft deletes only
- Audit trail on all critical tables
- Row-Level Security for shared schema tenants
- Encrypted PII columns

## API Standards

- Base: /v1/{resource}
- Success envelope: { success, data, meta, errors, timestamp }
- Pagination: ?page=&per_page= with X-Total-Count header
- Rate Limiting: 100 req/min/user, 429 on exceed
