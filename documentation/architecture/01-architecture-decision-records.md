# Architecture Decision Records (ADR)

## ADR-001: Modular Monolith Architecture
**Date:** 2026-07-04
**Status:** ACCEPTED

**Decision:** Use Modular Monolith pattern for V1.0.

**Context:** AgriHub Malawi needs to support multiple business domains
(farm, finance, marketplace, inventory, livestock) with strong data
consistency and multi-tenant isolation. Team is small.

**Consequences:**
- Single deployable JAR simplifies DevOps
- ACID transactions across modules
- Gradle modules enforce boundaries
- Can extract microservices later when needed
- Shared database requires strict schema boundaries

## ADR-002: Kotlin/Ktor/Exposed Stack
**Date:** 2026-07-04
**Status:** ACCEPTED

**Decision:** Backend in Kotlin using Ktor HTTP framework and Exposed ORM.

**Rationale:**
- Same language as Android (code sharing, skills reuse)
- Ktor is lightweight, coroutine-native
- Exposed provides type-safe SQL without heavy abstraction
- PostgreSQL-specific features available

## ADR-003: Hybrid Multi-Tenancy
**Date:** 2026-07-04
**Status:** ACCEPTED

**Decision:** Three-tier tenant isolation:
- Tier 1: Shared schema with RLS (individual farmers)
- Tier 2: Schema-per-tenant (cooperatives, mid-size)
- Tier 3: Database-per-tenant (government, large NGOs)

**Rationale:** Balances cost efficiency with isolation requirements.

## ADR-004: Offline-First with Room + Sync Engine
**Date:** 2026-07-04
**Status:** ACCEPTED

**Decision:** Mobile uses Room (SQLite) for local storage with a custom
sync engine that pushes/pulls changes via REST API.

**Rationale:**
- Offline reliability is core requirement
- Farmers in remote areas need full functionality
- Sync engine can be upgraded to WebSocket later

## ADR-005: JWT + RBAC Security Model
**Date:** 2026-07-04
**Status:** ACCEPTED

**Decision:** Stateless JWT authentication with role-based access control
and tenant-scoped permissions.

**Rationale:**
- No server-side session storage needed
- Tenant context embedded in token
- Easy to scale horizontally

## ADR-006: PostgreSQL as Primary Database
**Date:** 2026-07-04
**Status:** ACCEPTED

**Decision:** PostgreSQL 16+ as the single database system.

**Rationale:**
- Row-Level Security for multi-tenancy
- PostGIS for geospatial (farm mapping)
- JSONB for flexible data
- Strong ACID compliance
- Mature ecosystem


## ADR-007: Number & Currency Formatting Standards
**Date:** 2026-07-04
**Status:** ACCEPTED

**Decision:** All monetary and large number displays shall use locale-aware
formatting with shorthand options.

**Specification:**
- Backend stores all amounts as DECIMAL (exact precision)
- API returns raw numbers (no formatting in JSON)
- Mobile/Web formats for display using locale

**Formatting Rules:**
| Raw Value | Short Format | Full Format (MWK) |
|-----------|-------------|-------------------|
| 1,500 | 1.5K | MWK 1,500 |
| 2,500,000 | 2.5M | MWK 2,500,000 |
| 150,000,000 | 150M | MWK 150,000,000 |
| 1,200,000,000 | 1.2B | MWK 1,200,000,000 |

**Implementation:**
- Kotlin extension function: Long.toShortFormat() -> "1.5K", "2.5M"
- Locale-aware: English uses "K/M/B", Chichewa uses same symbols
- Thresholds: K=1,000, M=1,000,000, B=1,000,000,000, T=1,000,000,000,000
- Always show full value on detail screens
- Dashboard/summary cards can use short format
- Tooltip/long-press shows exact value
