## 4. Database Architecture

### 4.1 Connection Management

         Application Pool (HikariCP: 20 connections)
                          │
                    PgBouncer (Transaction Pooling)
                          │
                    PostgreSQL 16
          ┌───────────────┼───────────────┐
     Tier 1:         Tier 2:         Tier 3:
   Shared Schema   Schema/Tenant   DB/Tenant
      (RLS)

### 4.2 Migration Strategy

database/
├── migrations/
│   ├── V1__core_tables.sql
│   ├── V2__farm_tables.sql
│   ├── V3__livestock_tables.sql
│   ├── V4__finance_tables.sql
│   ├── V5__marketplace_tables.sql
│   ├── V6__inventory_tables.sql
│   ├── V7__warehouse_tables.sql
│   ├── V8__report_tables.sql
│   └── V9__seed_data.sql
├── seeds/
│   ├── crop_catalog.sql
│   ├── pest_disease_catalog.sql
│   ├── vaccine_schedules.sql
│   └── default_roles_permissions.sql
└── rollbacks/
    └── ...

## 5. Security Architecture

### 5.1 Authentication Flow

Mobile App                Ktor Backend           PostgreSQL
   │                          │                      │
   │ 1. POST /auth/login      │                      │
   │─────────────────────────>│                      │
   │                          │ 2. SELECT user       │
   │                          │─────────────────────>│
   │                          │ 3. Return user       │
   │                          │<─────────────────────│
   │                          │ 4. Verify (Argon2id) │
   │                          │ 5. Generate tokens   │
   │ 6. Return tokens         │                      │
   │<─────────────────────────│                      │
   │ 7. Store in Keystore     │                      │

### 5.2 JWT Token Payload

{
  "sub": "user-uuid",
  "tenant_id": "tenant-uuid",
  "tenant_tier": "SCHEMA",
  "tenant_schema": "tenant_abc123",
  "roles": ["FARMER", "VIEWER"],
  "permissions": [
    "farm:create:own",
    "farm:read:own",
    "harvest:create:own"
  ],
  "iat": 1719000000,
  "exp": 1719000900,
  "jti": "unique-token-id"
}

### 5.3 Permission Model

Format: resource:action:scope

Resources: farm, field, crop, harvest, livestock, animal,
           finance, inventory, marketplace, warehouse, report,
           user, tenant, settings

Actions: create, read, update, delete, approve, export, manage

Scopes:
  own    = Only records created by this user
  tenant = All records in user's tenant
  all    = Platform-wide (admin only)

Examples:
  farm:create:own        = Create own farm records
  finance:approve:tenant = Approve any tenant financial record
  report:export:all      = Export all platform reports
