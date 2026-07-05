package com.agrihub.platform.identity.auth

import com.agrihub.platform.security.auth.jwt.JwtService
import com.agrihub.platform.security.auth.password.PasswordService
import com.agrihub.platform.security.auth.session.SessionService
import com.agrihub.platform.security.audit.AuditService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val accessToken: String, val refreshToken: String, val expiresIn: Long)
data class RefreshRequest(val refreshToken: String)
data class ChangePasswordRequest(val currentPassword: String, val newPassword: String)
data class ApiResponse(val success: Boolean, val data: Any? = null, val errors: List<Map<String, String>>? = null)

fun Route.authRoutes() {
    val sessionService = SessionService()
    val auditService = AuditService()
    
    route("/v1/auth") {
        
        post("/login") {
            val request = call.receive<LoginRequest>()
            
            // TODO: Validate against database
            // For now, create test user
            val userId = UUID.randomUUID()
            val tenantId = UUID.randomUUID()
            
            val claims = JwtService.TokenClaims(
                userId = userId,
                tenantId = tenantId,
                tenantTier = "SHARED",
                countryCode = "MW",
                roles = listOf("FARMER"),
                permissions = listOf("farm:read:own", "harvest:create:own")
            )
            
            val accessToken = JwtService.generateAccessToken(claims)
            val refreshToken = JwtService.generateRefreshToken(userId)
            
            sessionService.createSession(
                userId = userId,
                tenantId = tenantId,
                deviceId = "device-${UUID.randomUUID()}",
                refreshTokenHash = refreshToken.take(16),
                ipAddress = call.request.origin.remoteHost,
                userAgent = call.request.userAgent() ?: "unknown"
            )
            
            auditService.logLogin(tenantId, userId, true, call.request.origin.remoteHost)
            
            call.respond(
                ApiResponse(
                    success = true,
                    data = LoginResponse(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        expiresIn = 900
                    )
                )
            )
        }
        
        post("/logout") {
            val refreshToken = call.request.headers["X-Refresh-Token"] ?: ""
            call.respond(ApiResponse(success = true, data = mapOf("message" to "Logged out")))
        }
        
        post("/refresh") {
            val request = call.receive<RefreshRequest>()
            val userId = UUID.randomUUID()
            val tenantId = UUID.randomUUID()
            
            val claims = JwtService.TokenClaims(
                userId = userId,
                tenantId = tenantId,
                tenantTier = "SHARED",
                countryCode = "MW",
                roles = listOf("FARMER"),
                permissions = listOf("farm:read:own")
            )
            
            val newAccessToken = JwtService.generateAccessToken(claims)
            val newRefreshToken = JwtService.generateRefreshToken(userId)
            
            call.respond(
                ApiResponse(
                    success = true,
                    data = LoginResponse(newAccessToken, newRefreshToken, 900)
                )
            )
        }
        
        post("/change-password") {
            val request = call.receive<ChangePasswordRequest>()
            call.respond(ApiResponse(success = true, data = mapOf("message" to "Password changed")))
        }
        
        post("/forgot-password") {
            val request = call.receive<Map<String, String>>()
            call.respond(ApiResponse(success = true, data = mapOf("message" to "Reset link sent if email exists")))
        }
        
        post("/reset-password") {
            val request = call.receive<Map<String, String>>()
            call.respond(ApiResponse(success = true, data = mapOf("message" to "Password reset successful")))
        }
        
        get("/sessions") {
            val userId = UUID.randomUUID()
            val sessions = sessionService.getUserSessions(userId)
            call.respond(ApiResponse(success = true, data = sessions))
        }
        
        delete("/sessions/{id}") {
            val sessionId = call.parameters["id"]?.let { UUID.fromString(it) }
            sessionId?.let { sessionService.revokeSession(it) }
            call.respond(ApiResponse(success = true, data = mapOf("message" to "Session revoked")))
        }
    }
}
