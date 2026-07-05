# AgriHub Platform - Row-Level Security Design

## Multi-Tenancy Strategy

Tier 1: Shared Schema (Individual Farmers)
- All tenants share same tables
- Isolation enforced via PostgreSQL RLS
- tenant_id column on every table
- Application sets app.current_tenant_id per session

Tier 2: Schema-per-Tenant (Cooperatives, Mid-size)
- Each tenant has own PostgreSQL schema
- Stronger isolation, no RLS needed
- Easier backup/restore per tenant

Tier 3: Database-per-Tenant (Government, Large NGOs)
- Complete database isolation
- Separate connection pools
- Independent scaling and backups

## RLS Implementation

Enable RLS on all tenant-scoped tables:

ALTER TABLE farms ENABLE ROW LEVEL SECURITY;
ALTER TABLE fields ENABLE ROW LEVEL SECURITY;
ALTER TABLE planting_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE animals ENABLE ROW LEVEL SECURITY;
ALTER TABLE income_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE expense_records ENABLE ROW LEVEL SECURITY;
ALTER TABLE inventory_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE produce_listings ENABLE ROW LEVEL SECURITY;

## Tenant Isolation Policy

Applied to every tenant-scoped table:

CREATE POLICY tenant_isolation ON farms
    FOR ALL
    USING (tenant_id = current_setting('app.current_tenant_id')::UUID)
    WITH CHECK (tenant_id = current_setting('app.current_tenant_id')::UUID);

This policy ensures:
- SELECT only returns rows matching current tenant
- INSERT requires tenant_id match current tenant
- UPDATE only affects current tenant rows
- DELETE only affects current tenant rows

## Tenant Context Functions

Get current tenant:
CREATE FUNCTION get_current_tenant() RETURNS UUID AS $$
BEGIN
    RETURN current_setting('app.current_tenant_id')::UUID;
END;
$$ LANGUAGE plpgsql;

Set tenant context (called by application on every request):
SELECT set_config('app.current_tenant_id', 'tenant-uuid-here', false);

## Security Layers (Defense in Depth)

Layer 1 - Application: JWT token contains tenant_id, verified on every request
Layer 2 - Service: Tenant context extracted from JWT, stored in coroutine context
Layer 3 - Repository: All queries automatically scoped by tenant
Layer 4 - Database: RLS as final defense against bugs or bypasses

## Tenant Isolation Tests

Test 1: Verify tenant A only sees own data
SET app.current_tenant_id = 'tenant-a-uuid';
SELECT COUNT(*) FROM farms;
-- Should only show Tenant A farms

Test 2: Verify tenant B cannot see tenant A data
SET app.current_tenant_id = 'tenant-b-uuid';
SELECT COUNT(*) FROM farms;
-- Should only show Tenant B farms

Test 3: Cross-tenant insert must fail
SET app.current_tenant_id = 'tenant-a-uuid';
INSERT INTO farms (tenant_id, name, area_hectares)
VALUES ('tenant-b-uuid', 'Hacked Farm', 100);
-- RLS WITH CHECK prevents this, raises error

Test 4: Cross-tenant update must fail
SET app.current_tenant_id = 'tenant-a-uuid';
UPDATE farms SET name = 'Hacked'
WHERE tenant_id = 'tenant-b-uuid';
-- Affects 0 rows due to USING clause

## Application Integration

Kotlin/Exposed code to set tenant context:

suspend fun setTenantContext(tenantId: UUID) {
    transaction {
        exec("SET app.current_tenant_id = '$tenantId'")
    }
}

Reset between requests:
DISCARD ALL;
-- Clears all session settings including tenant context

## Performance Considerations

- RLS policies add small overhead per query
- Index tenant_id on every table (needed with or without RLS)
- Use partial indexes: WHERE deleted_at IS NULL
- Monitor query plans for RLS-related slowdowns
- Consider statement_timeout to prevent long-running cross-tenant queries

## Bypass for System Operations

Super admin or background jobs may need to bypass RLS:

CREATE POLICY admin_access ON farms
    FOR ALL
    USING (current_setting('app.is_admin', true) = 'true');

Set in application:
SELECT set_config('app.is_admin', 'true', false);
-- Background job runs with full access
SELECT set_config('app.is_admin', 'false', false);
-- Return to normal tenant isolation

## Audit Verification

Query to verify no data leakage:
SELECT tenant_id, COUNT(*)
FROM audit_logs
WHERE table_name = 'farms'
GROUP BY tenant_id
HAVING COUNT(DISTINCT tenant_id) > 1;
-- Should always return empty if RLS working correctly
