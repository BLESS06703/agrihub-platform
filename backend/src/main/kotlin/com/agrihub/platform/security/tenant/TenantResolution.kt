package com.agrihub.platform.security.tenant

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.pipeline.*
import java.util.UUID

data class TenantContext(
    val tenantId: UUID,
    val tenantTier: String,
    val countryCode: String,
    val schemaName: String?
)

class TenantResolution {
    
    companion object {
        const val TENANT_CONTEXT_KEY = "tenant_context"
    }
    
    suspend fun resolve(claims: Map<String, Any>): TenantContext {
        val tenantId = UUID.fromString(claims["tenant_id"] as String)
        val tier = claims["tenant_tier"] as? String ?: "SHARED"
        val countryCode = claims["country_code"] as? String ?: "MW"
        
        val schema = when (tier) {
            "SCHEMA" -> "tenant_${tenantId.toString().replace("-", "_").take(12)}"
            "DATABASE" -> null
            else -> null
        }
        
        return TenantContext(
            tenantId = tenantId,
            tenantTier = tier,
            countryCode = countryCode,
            schemaName = schema
        )
    }
}

fun PipelineContext<Unit, ApplicationCall>.getTenantContext(): TenantContext {
    return call.attributes.getOrNull(
        io.ktor.util.AttributeKey<TenantContext>(TenantResolution.TENANT_CONTEXT_KEY)
    ) ?: throw SecurityException("No tenant context found")
}
