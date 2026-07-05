package com.agrihub.platform.security.apikey

import java.security.MessageDigest
import java.util.Base64
import java.util.UUID

class ApiKeyService {
    
    data class ApiKey(
        val id: UUID,
        val tenantId: UUID,
        val name: String,
        val keyPrefix: String,
        val keyHash: String,
        val scopes: List<String>,
        val isActive: Boolean,
        val expiresAt: java.time.Instant?
    )
    
    fun generateApiKey(tenantId: UUID, name: String, scopes: List<String>): Pair<String, String> {
        val rawKey = generateSecureKey()
        val prefix = rawKey.take(8)
        val hash = hashKey(rawKey)
        
        return Pair(rawKey, "$prefix.${hash.take(8)}")
    }
    
    fun hashKey(key: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(key.toByteArray())
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash)
    }
    
    fun validateKey(rawKey: String, storedHash: String): Boolean {
        return hashKey(rawKey) == storedHash
    }
    
    private fun generateSecureKey(): String {
        val bytes = ByteArray(32)
        java.security.SecureRandom().nextBytes(bytes)
        return "ah_${Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)}"
    }
}
