# 2. System Architecture Overview

## 2.1 High-Level Architecture

AgriHub Malawi follows a layered architecture pattern with clear
separation of concerns:

CLIENT LAYER:
- Android App (Kotlin)
- Web Admin (React)
- USSD/SMS (Future)

API GATEWAY LAYER:
- Nginx Reverse Proxy
- Rate Limiting / Load Balancing

APPLICATION LAYER:
- Ktor Backend (Kotlin)
  - Auth Service
  - Tenant Service
  - API Routes
  - Farm Service
  - Finance Service
  - Market Service
  - Inventory Service
  - Report Service

DATA LAYER:
- Redis (Cache)
- PostgreSQL (Primary Database)
- MinIO (Object Storage)

## 2.2 Multi-Tenancy Architecture

Tier 1: Individual Farmers (Shared Schema)
- All individual farmers share database schemas
- Data isolated by tenant_id column
- Row-Level Security (RLS) enforced at database level
- Most cost-effective for high-volume, small-footprint tenants

Tier 2: Cooperatives & Mid-Size (Schema-per-Tenant)
- Each cooperative gets its own PostgreSQL schema
- Stronger isolation
- Easier backup/restore per tenant
- Better for moderate data volumes

Tier 3: Large Organizations (Database-per-Tenant)
- Government, large NGOs, major agribusinesses
- Complete database isolation
- Independent scaling
- Dedicated connection pools
- Compliance-friendly for sensitive data

## 2.3 Offline-First Mobile Architecture

Local Storage (Android):
- Room Database (SQLite) for local persistence
- DataStore for preferences

Sync Priority Levels:
1. CRITICAL: Financial transactions, contracts
2. HIGH: Farm records, harvest data
3. MEDIUM: Inventory updates, marketplace listings
4. LOW: Analytics events, logs

Offline Conflict Rules:
- Financial records: Server wins always
- Farm records: Timestamp-based merge
- Marketplace: Lock during active transactions
- Inventory: Last-write-wins with user notification

## 2.4 Security Architecture

Authentication Flow:
User -> Login -> Server validates credentials
-> Issues Access Token (JWT, 15 min TTL)
-> Issues Refresh Token (opaque, 7 day TTL)
-> Client stores tokens securely (Android Keystore)

Authorization Model (RBAC + Claims):
User -> Role -> Permissions -> Tenant ID Scope

Predefined Roles:
- TENANT_OWNER: Full tenant control
- FARM_MANAGER: Farm operations management
- FARMER: Daily farming activities
- AGRONOMIST: Crop advisory
- VET_OFFICER: Livestock management
- EXTENSION_OFFICER: Field support
- ACCOUNTANT: Financial operations
- WAREHOUSE_MANAGER: Storage management
- BUYER: Procurement
- TRANSPORTER: Logistics
- DATA_ANALYST: Reporting only
- VIEWER: Read-only access

Permission Pattern: resource:action:scope
Examples:
- farm:create:own
- farm:read:tenant
- finance:approve:own

## 2.5 Database Architecture Principles

1. Every table includes: id, tenant_id, created_at, updated_at, deleted_at
2. Soft deletes with deleted_at timestamp
3. UUID primary keys
4. Audit trail on all financial and critical tables
5. Indexed tenant_id on every tenant-scoped table
6. No foreign keys across tenant boundaries
7. Encrypted columns for PII and sensitive data

## 2.6 API Design Standards

Base URL Pattern: https://api.agrihub.mw/v1/{resource}

Standard Success Response:
{
  "success": true,
  "data": {},
  "meta": { "page": 1, "per_page": 20, "total": 150 },
  "errors": [],
  "timestamp": "2026-07-04T10:30:00Z"
}

HTTP Status Codes:
- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthenticated
- 403: Forbidden
- 404: Not Found
- 409: Conflict
- 422: Validation Error
- 429: Rate Limited
- 500: Internal Server Error

## 2.7 Technology Stack

- Mobile: Kotlin 2.0+, Jetpack Compose, Room, Koin
- Backend: Kotlin 2.0+, Ktor 3.0+, Exposed ORM
- Database: PostgreSQL 16+
- Cache: Redis 7+
- Storage: MinIO
- Auth: JWT + Argon2id
- Container: Docker
- Proxy: Nginx
- Monitor: Prometheus + Grafana

## 2.8 Deployment Architecture

Docker Host contains:
- Nginx (:443) -> Ktor (:8080)
- PostgreSQL (:5432)
- Redis (:6379)
- MinIO (:9000)
- Prometheus (:9090)
- Grafana (:3000)
