# AgriHub Platform - Data Dictionary
# Version 1.0

## Core Platform Tables

### tenants

Column: id
- Type: UUID
- Constraints: PK, DEFAULT uuid_generate_v4()
- Description: Unique tenant identifier

Column: name
- Type: VARCHAR(255)
- Constraints: NOT NULL
- Description: Organization name

Column: slug
- Type: VARCHAR(100)
- Constraints: UNIQUE, NOT NULL
- Description: URL-safe identifier

Column: tenant_type
- Type: tenant_type ENUM
- Constraints: NOT NULL
- Description: INDIVIDUAL_FARMER, COOPERATIVE, AGRIBUSINESS, NGO, GOVERNMENT, etc.

Column: tenant_tier
- Type: tenant_tier ENUM
- Constraints: NOT NULL, DEFAULT 'SHARED'
- Description: SHARED, SCHEMA, DATABASE

Column: country_code
- Type: VARCHAR(2)
- Constraints: NOT NULL, DEFAULT 'MW'
- Description: ISO 3166-1 alpha-2 country code

Column: status
- Type: tenant_status ENUM
- Constraints: NOT NULL, DEFAULT 'PENDING'
- Description: PENDING, ACTIVE, SUSPENDED, INACTIVE

Column: subscription_tier
- Type: VARCHAR(50)
- Constraints: DEFAULT 'FREE'
- Description: FREE, STARTER, PRO, ENTERPRISE

Column: max_users
- Type: INTEGER
- Constraints: DEFAULT 5
- Description: Maximum users allowed per subscription

Column: settings
- Type: JSONB
- Constraints: DEFAULT '{}'
- Description: Tenant-specific configuration

Column: created_at
- Type: TIMESTAMPTZ
- Constraints: NOT NULL, DEFAULT NOW()

Column: updated_at
- Type: TIMESTAMPTZ

Column: deleted_at
- Type: TIMESTAMPTZ
- Description: Soft delete timestamp

Indexes: idx_tenants_slug, idx_tenants_status, idx_tenants_country

### users

Column: id (UUID, PK)
Column: tenant_id (UUID, FK->tenants.id, NOT NULL)
Column: email (VARCHAR(255), NOT NULL)
Column: phone (VARCHAR(50))
Column: full_name (VARCHAR(255), NOT NULL)
Column: password_hash (VARCHAR(255), NOT NULL, Argon2id)
Column: status (user_status, NOT NULL, DEFAULT PENDING)
Column: email_verified (BOOLEAN, DEFAULT FALSE)
Column: mfa_enabled (BOOLEAN, DEFAULT FALSE)
Column: last_login_at (TIMESTAMPTZ)
Column: failed_login_attempts (INTEGER, DEFAULT 0)
Column: locked_until (TIMESTAMPTZ)
Column: created_at, updated_at, deleted_at

Unique: (tenant_id, email)

### roles

Column: id (UUID, PK)
Column: tenant_id (UUID, FK->tenants.id, nullable = system role)
Column: name (VARCHAR(100), NOT NULL)
Column: is_system (BOOLEAN, DEFAULT FALSE)

### permissions

Column: id (UUID, PK)
Column: code (VARCHAR(100), UNIQUE, NOT NULL)
  Format: resource:action:scope
  Example: farm:create:own
Column: resource (VARCHAR(50), NOT NULL)
Column: action (VARCHAR(50), NOT NULL)
Column: scope (VARCHAR(50), DEFAULT 'own')

### audit_logs

Column: id (UUID, PK)
Column: tenant_id (UUID, NOT NULL)
Column: user_id (UUID, FK->users.id)
Column: action (audit_action ENUM, NOT NULL)
  Values: CREATE, UPDATE, DELETE, LOGIN, LOGOUT, EXPORT
Column: table_name (VARCHAR(100))
Column: record_id (UUID)
Column: old_values (JSONB)
Column: new_values (JSONB)
Column: ip_address (INET)
Column: correlation_id (UUID)
Column: created_at (TIMESTAMPTZ, NOT NULL)

Partition by month when rows exceed 10 million.

## Standard Column Pattern

Every table includes:
- id: UUID PRIMARY KEY DEFAULT uuid_generate_v4()
- tenant_id: UUID NOT NULL
- created_by: UUID NOT NULL
- created_at: TIMESTAMPTZ NOT NULL DEFAULT NOW()
- updated_by: UUID
- updated_at: TIMESTAMPTZ
- deleted_at: TIMESTAMPTZ (soft delete)

## Indexing Rules

- Always index: tenant_id, foreign keys
- Index: columns in WHERE clauses, ORDER BY, date ranges
- GIST index: GEOGRAPHY columns
- Partial index: WHERE deleted_at IS NULL for active records
