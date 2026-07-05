package com.agrihub.platform.security.auth.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.util.Date
import java.util.UUID

object JwtService {
    
    private val keyPair = KeyPairGenerator.getInstance("RSA")
        .apply { initialize(2048) }
        .generateKeyPair()
    
    private val publicKey = keyPair.public as RSAPublicKey
    private val privateKey = keyPair.private as RSAPrivateKey
    private val algorithm = Algorithm.RSA256(publicKey, privateKey)
    
    data class TokenClaims(
        val userId: UUID,
        val tenantId: UUID,
        val tenantTier: String,
        val countryCode: String,
        val roles: List<String>,
        val permissions: List<String>
    )
    
    fun generateAccessToken(claims: TokenClaims): String {
        val now = Instant.now()
        return JWT.create()
            .withSubject(claims.userId.toString())
            .withIssuer("agrihub-platform")
            .withClaim("tenant_id", claims.tenantId.toString())
            .withClaim("tenant_tier", claims.tenantTier)
            .withClaim("country_code", claims.countryCode)
            .withClaim("roles", claims.roles)
            .withClaim("permissions", claims.permissions)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(now.plusSeconds(900)))
            .withJWTId(UUID.randomUUID().toString())
            .sign(algorithm)
    }
    
    fun generateRefreshToken(userId: UUID): String {
        val bytes = ByteArray(32)
        java.security.SecureRandom().nextBytes(bytes)
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
    
    fun verifyAndExtractClaims(token: String): TokenClaims? {
        return try {
            val verifier = JWT.require(algorithm)
                .withIssuer("agrihub-platform")
                .build()
            val jwt = verifier.verify(token)
            
            TokenClaims(
                userId = UUID.fromString(jwt.subject),
                tenantId = UUID.fromString(jwt.getClaim("tenant_id").asString()),
                tenantTier = jwt.getClaim("tenant_tier").asString(),
                countryCode = jwt.getClaim("country_code").asString(),
                roles = jwt.getClaim("roles").asList(String::class.java) ?: emptyList(),
                permissions = jwt.getClaim("permissions").asList(String::class.java) ?: emptyList()
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun isTokenExpired(token: String): Boolean {
        return try {
            val jwt = JWT.decode(token)
            jwt.expiresAt.before(Date())
        } catch (e: Exception) {
            true
        }
    }
}
