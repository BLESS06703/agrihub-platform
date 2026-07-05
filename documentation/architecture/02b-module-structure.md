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
