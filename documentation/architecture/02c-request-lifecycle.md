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
