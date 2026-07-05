# AgriHub Malawi - Database Standards
# Version 1.0

## 1. Naming Conventions

Tables: snake_case, plural
- farms, fields, planting_records, audit_logs

Columns: snake_case, singular
- id, tenant_id, farm_name, created_at

Primary Keys: id (UUID, always)

Foreign Keys: {referenced_table_singular}_id
- farm_id → farms.id
- created_by → users.id

Indexes: idx_{table}_{column}
- idx_farms_tenant
- idx_planting_records_date

Constraints:
- PK: pk_{table}
- FK: fk_{table}_{referenced_table}
- UNIQUE: uq_{table}_{columns}
- CHECK: ck_{table}_{column}_{rule}

Enums: UPPERCASE with underscores
- farm_status: 'ACTIVE', 'INACTIVE', 'ARCHIVED'

## 2. Required Columns (Every Table)

Column      | Type           | Description
id          | UUID           | Primary key, auto-generated
tenant_id   | UUID           | Tenant isolation (NOT NULL)
created_by  | UUID           | User who created (NOT NULL)
created_at  | TIMESTAMPTZ    | Creation timestamp (NOT NULL, DEFAULT NOW())
updated_by  | UUID           | User who last updated (nullable)
updated_at  | TIMESTAMPTZ    | Last update timestamp
deleted_at  | TIMESTAMPTZ    | Soft delete timestamp (nullable)

## 3. UUID Usage

- All primary keys: UUID v4
- Generated via: DEFAULT uuid_generate_v4()
- Never use SERIAL/BIGSERIAL for PKs
- UUIDs in API responses as strings
- No sequential ID leakage between tenants

## 4. Foreign Key Rules

- Always define FK constraints
- ON DELETE: RESTRICT or NO ACTION (never CASCADE except junction tables)
- ON UPDATE: CASCADE (rarely needed for UUIDs)
- FK column names clearly indicate relationship
- Index all FK columns

## 5. Indexing Rules

Create indexes for:
- tenant_id (every tenant-scoped table)
- Foreign key columns
- Columns used in WHERE clauses frequently
- Columns used in ORDER BY
- Columns used in date range queries
- GIST indexes for GEOGRAPHY columns

Don't index:
- Low-cardinality columns (< 5 distinct values) unless heavily queried
- Columns rarely queried
- Every column (index maintenance cost)

## 6. Soft Delete Strategy

- Use deleted_at TIMESTAMPTZ column
- NULL = active, NOT NULL = deleted
- All queries filter: WHERE deleted_at IS NULL
- Create partial indexes with WHERE deleted_at IS NULL
- Restore: SET deleted_at = NULL
- Hard delete only for GDPR/data subject requests

## 7. Audit Strategy

Audit table: audit_logs
- All CREATE, UPDATE, DELETE operations logged
- old_values and new_values as JSONB
- Partitioned by month for performance
- Retention: 1 year online, 7 years archive

## 8. Partitioning Guidelines

When to partition (table > 10M rows):
- audit_logs: by month (created_at)
- analytics_events: by month
- sync_log: by month
- notifications: by month

Method: RANGE partitioning on created_at

## 9. Migration Rules

- One migration per version
- Forward-only (no down migrations in production)
- Always test migrations on staging first
- Include rollback script (for dev/staging only)
- Never modify existing migrations (create new one)

## 10. Connection Management

- Max connections per instance: 20
- PgBouncer transaction pooling in front
- Statement timeout: 30 seconds
- Idle in transaction timeout: 60 seconds
