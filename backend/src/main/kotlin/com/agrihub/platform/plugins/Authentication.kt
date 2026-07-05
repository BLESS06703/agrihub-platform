package com.agrihub.platform.plugins

import com.agrihub.platform.security.auth.jwt.JwtService
import com.agrihub.platform.security.rbac.AuthorizationService
import com.agrihub.platform.security.tenant.TenantContext
import com.agrihub.platform.security.tenant.TenantResolution
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

fun Application.configureAuthentication() {
    val tenantResolution = TenantResolution()
    val authorizationService = AuthorizationService()
    
    install(Authentication) {
        bearer("auth-jwt") {
            realm = "AgriHub Platform"
            
            authenticate { credential ->
                val claims = JwtService.verifyAndExtractClaims(credential.token)
                
                if (claims != null) {
                    val tenantContext = tenantResolution.resolve(
                        mapOf(
                            "tenant_id" to claims.tenantId.toString(),
                            "tenant_tier" to claims.tenantTier,
                            "country_code" to claims.countryCode
                        )
                    )
                    
                    PrincipalWithClaims(
                        userId = claims.userId,
                        tenantContext = tenantContext,
                        roles = claims.roles,
                        permissions = claims.permissions
                    )
                } else {
                    null
                }
            }
            
            challenge { _, _ ->
                call.respond(
                    io.ktor.http.HttpStatusCode.Unauthorized,
                    mapOf(
                        "success" to false,
                        "errors" to listOf(
                            mapOf("code" to "UNAUTHENTICATED", "message" to "Valid authentication required")
                        )
                    )
                )
            }
        }
    }
}

data class PrincipalWithClaims(
    val userId: java.util.UUID,
    val tenantContext: TenantContext,
    val roles: List<String>,
    val permissions: List<String>
) : Principal
