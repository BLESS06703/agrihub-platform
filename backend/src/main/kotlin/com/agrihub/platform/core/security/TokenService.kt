package com.agrihub.platform.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.agrihub.platform.core.config.AppConfig
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.util.*

object TokenService {
    
    data class TokenClaims(
        val userId: UUID,
        val tenantId: UUID,
        val tenantTier: String,
        val roles: List<String>,
        val permissions: List<String>
    )
    
    fun generateAccessToken(claims: TokenClaims): String {
        return JWT.create()
            .withSubject(claims.userId.toString())
            .withClaim("tenant_id", claims.tenantId.toString())
            .withClaim("tenant_tier", claims.tenantTier)
            .withClaim("roles", claims.roles)
            .withClaim("permissions", claims.permissions)
            .withIssuedAt(Date.from(Instant.now()))
            .withExpiresAt(Date.from(Instant.now().plusSeconds(900))) // 15 min
            .withJWTId(UUID.randomUUID().toString())
            .sign(Algorithm.RSA256(getPublicKey(), getPrivateKey()))
    }
    
    fun generateRefreshToken(userId: UUID): String {
        val random = SecureRandom()
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
    
    fun verifyToken(token: String): TokenClaims? {
        return try {
            val verifier = JWT.require(Algorithm.RSA256(getPublicKey(), getPrivateKey()))
                .build()
            val jwt = verifier.verify(token)
            
            TokenClaims(
                userId = UUID.fromString(jwt.subject),
                tenantId = UUID.fromString(jwt.getClaim("tenant_id").asString()),
                tenantTier = jwt.getClaim("tenant_tier").asString(),
                roles = jwt.getClaim("roles").asList(String::class.java) ?: emptyList(),
                permissions = jwt.getClaim("permissions").asList(String::class.java) ?: emptyList()
            )
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getPublicKey(): RSAPublicKey {
        val keyBytes = Base64.getDecoder().decode(AppConfig.jwtPublicKey)
        val spec = X509EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePublic(spec) as RSAPublicKey
    }
    
    private fun getPrivateKey(): RSAPrivateKey {
        val keyBytes = Base64.getDecoder().decode(AppConfig.jwtPrivateKey)
        val spec = PKCS8EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(spec) as RSAPrivateKey
    }
}
