# AgriHub Malawi - Enterprise System Architecture
# Version 1.0

## 1. Architecture Overview

### 1.1 Architectural Style
Modular Monolith with clear domain boundaries, designed for future
extraction into microservices when scale demands it.

### 1.2 High-Level System Context

                          ┌──────────────────┐
                          │   External APIs   │
                          │  - Weather Data   │
                          │  - Market Prices  │
                          │  - Mobile Money   │
                          │  - SMS Gateway    │
                          └────────┬─────────┘
                                   │
┌──────────┐   ┌──────────┐   ┌───┴───────────┐
│ Android  │   │   Web    │   │  AgriHub      │
│   App    │───│  Admin   │───│  Backend      │
│ (Kotlin) │   │ (React)  │   │  (Ktor)       │
└──────────┘   └──────────┘   └───┬───────────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
              ┌─────┴─────┐ ┌────┴────┐ ┌──────┴──────┐
              │ PostgreSQL │ │  Redis  │ │   MinIO     │
              │  Database  │ │  Cache  │ │  Storage    │
              └───────────┘ └─────────┘ └─────────────┘
## 2. Modular Monolith Structure

### 2.1 Module Boundaries

agrihub-backend/
├── application/              # Main entry point
│   └── src/main/kotlin/com/agrihub/
│       ├── Application.kt        # Ktor bootstrap
│       ├── Modules.kt            # Module wiring
│       └── plugins/
│           ├── Authentication.kt
│           ├── Serialization.kt
│           ├── Monitoring.kt
│           └── MultiTenancy.kt
│
├── domain/                   # Shared domain (no DB dependency)
│   └── src/main/kotlin/com/agrihub/domain/
│       ├── model/
│       ├── events/
│       └── exception/
│
├── core/                     # Cross-cutting concerns
│   └── src/main/kotlin/com/agrihub/core/
│       ├── security/             # Auth, JWT, RBAC
│       ├── tenant/               # Tenant resolution
│       ├── audit/                # Audit logging
│       ├── notification/         # Push, Email, SMS
│       ├── storage/              # File storage abstraction
│       └── sync/                 # Offline sync engine
│
├── module-farm/              # Farm management module
│   └── src/main/kotlin/com/agrihub/farm/
│       ├── api/                  # REST endpoints
│       ├── service/              # Business logic
│       ├── repository/           # Data access
│       ├── model/                # Farm-specific models
│       └── dto/                  # Request/Response DTOs
│
├── module-livestock/         # Livestock management
│   └── src/main/kotlin/com/agrihub/livestock/
│       ├── api/ ├── service/ ├── repository/
│       ├── model/ └── dto/
│
├── module-finance/           # Financial management
│   └── src/main/kotlin/com/agrihub/finance/
│       ├── api/ ├── service/ ├── repository/
│       ├── model/ └── dto/
│
├── module-marketplace/       # Marketplace
│   └── src/main/kotlin/com/agrihub/marketplace/
│       ├── api/ ├── service/ ├── repository/
│       ├── model/ └── dto/
│
├── module-inventory/         # Inventory management
│   └── src/main/kotlin/com/agrihub/inventory/
│       ├── api/ ├── service/ ├── repository/
│       ├── model/ └── dto/
│
├── module-warehouse/         # Warehouse management
│   └── src/main/kotlin/com/agrihub/warehouse/
│       ├── api/ ├── service/ ├── repository/
│       ├── model/ └── dto/
│
├── module-report/            # Reports & Analytics
│   └── src/main/kotlin/com/agrihub/report/
│       ├── api/ ├── service/ ├── repository/
│       ├── model/ └── dto/
│
├── module-ai/                # AI services (future)
│   └── src/main/kotlin/com/agrihub/ai/
│       ├── api/ ├── service/ └── model/
│
└── build.gradle.kts          # Root build file

### 2.2 Module Dependency Rules

application ─────────────┐
                         │
        ┌────────────────┼────────────────┐
        │                │                │
   module-farm     module-finance   module-marketplace ...
        │                │                │
        └────────────────┼────────────────┘
                         │
                       core ──────────── domain
                         │
                    infrastructure
                    (DB, Redis, MinIO)

Rules:
1. domain has zero dependencies (pure Kotlin)
2. core depends on domain
3. Business modules depend on domain and core
4. Business modules CANNOT depend on each other directly
5. Cross-module communication via domain events or service interfaces
6. application wires everything together
## 3. Request Lifecycle (Multi-Tenant)

### 3.1 Request Processing Pipeline

1. REQUEST ARRIVES
   POST /v1/farms
   Authorization: Bearer <JWT>

2. NGINX
   - TLS termination
   - Rate limiting check
   - Forward to Ktor

3. AUTHENTICATION PLUGIN
   - Extract JWT from Authorization header
   - Verify signature (RS256)
   - Check expiry
   - Extract claims: user_id, tenant_id, tenant_tier, roles

4. TENANT PLUGIN
   - Read tenant_id from JWT
   - Lookup tenant registry:
     Tier 1 (SHARED): Set app.current_tenant_id
     Tier 2 (SCHEMA): Set search_path to tenant schema
     Tier 3 (DATABASE): Connect to tenant database
   - Store tenant context in Kotlin coroutine context

5. AUTHORIZATION PLUGIN
   - Check required permission for route
   - E.g., POST /v1/farms requires "farm:create:own"
   - Verify user has this permission in JWT claims
   - 403 if not authorized

6. ROUTE HANDLER
   - Deserialize request body
   - Validate input
   - Call service layer

7. SERVICE LAYER
   - Business logic execution
   - Domain event emission
   - Transaction management

8. REPOSITORY LAYER
   - Construct SQL query
   - Execute via Exposed ORM
   - Tenant context auto-applied (RLS or schema)

9. AUDIT LOGGING
   - Record: who, what, when, old_value, new_value
   - Async to audit log table

10. RESPONSE
    - Serialize response
    - Add correlation ID header
    - Return with appropriate status code

### 3.2 Tenant Context in Code

data class TenantContext(
    val tenantId: UUID,
    val tenantTier: TenantTier,
    val schema: String?,
    val dbName: String?
)

// Accessible in any service/repository via coroutine context
suspend fun getCurrentTenant(): TenantContext {
    return currentCoroutineContext()[TenantContextKey]
        ?: throw UnauthenticatedException()
}

// Repository auto-scopes queries
class FarmRepository {
    suspend fun findAll(): List<Farm> {
        val tenant = getCurrentTenant()
        return when (tenant.tenantTier) {
            Tier.SHARED -> Farms.selectAll()
                .where { Farms.tenantId eq tenant.tenantId }
            Tier.SCHEMA -> Farms.selectAll() // search_path handles it
            Tier.DATABASE -> Farms.selectAll() // separate connection
        }.map { it.toFarm() }
    }
}
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
## 6. Offline Sync Architecture

### 6.1 Sync Engine Design

              ANDROID DEVICE
   ┌──────────┐  ┌──────────┐  ┌───────────┐
   │ Room DB  │  │DataStore │  │Sync Engine│
   │(SQLite)  │  │(Prefs)   │  │           │
   └────┬─────┘  └────┬─────┘  └─────┬─────┘
        │             │              │
   ┌────┴─────────────┴──────────────┴─────┐
   │          Offline Queue                │
   │  [CREATE farm] [UPDATE field] ...    │
   └────────────────┬──────────────────────┘
                    │ Network Available?
                    │
            BACKEND SYNC API
   POST /v1/sync/push     (upload changes)
   GET  /v1/sync/pull     (download changes)

Conflict Resolution:
- Financial: Server wins
- Farm data: Timestamp merge
- Inventory: Last-write-wins + notify

### 6.2 Sync Protocol

PUSH (Device -> Server):
{
  "device_id": "device-uuid",
  "last_sync_timestamp": "2026-07-04T10:00:00Z",
  "changes": [
    {
      "id": "local-uuid",
      "table": "farms",
      "operation": "CREATE",
      "data": { "name": "New Farm" },
      "client_timestamp": "2026-07-04T10:05:00Z"
    }
  ]
}

PULL (Server -> Device):
GET /v1/sync/pull?since=2026-07-04T10:00:00Z
Response: { "changes": [...], "server_timestamp": "..." }

## 7. Caching Strategy

### 7.1 Cache Layers

L1: Caffeine (In-Memory)
- TTL: 5 minutes, Max: 100MB
- For: tenant config, roles, crop catalog

L2: Redis (Distributed)
- TTL: 15 minutes
- For: sessions, rate limiting, market prices

L3: PostgreSQL
- Source of truth
## 8. Deployment Architecture

### 8.1 Docker Compose (Development)

services:
  nginx:        (ports: 443)
  backend:      (Ktor, port 8080)
  postgres:     (PostgreSQL 16)
  redis:        (Redis 7 Alpine)
  minio:        (Object Storage)
  prometheus:   (Metrics)
  grafana:      (Dashboards)

### 8.2 Production Layout

              LOAD BALANCER
                   │
      ┌────────────┼────────────┐
      │            │            │
  Backend-1   Backend-2   Backend-3
  (Ktor)      (Ktor)      (Ktor)
      │            │            │
      └────────────┼────────────┘
                   │
         ┌─────────┼─────────┐
         │         │         │
    Primary PG  Replica PG  MinIO Cluster

## 9. Monitoring & Observability

METRICS (Prometheus):
- Request count, duration, error rate
- Database query performance
- Cache hit/miss ratio
- Sync success/failure rate

DASHBOARDS (Grafana):
- Application health
- Business metrics (farms, users)
- Tenant growth

ALERTING:
- CPU > 80% → Warning
- Error rate > 5% → Critical
- DB connections > 80% → Warning
- Sync failures > 10% → Warning
- Disk > 90% → Critical
