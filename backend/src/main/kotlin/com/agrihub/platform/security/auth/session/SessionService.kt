package com.agrihub.platform.security.auth.session

import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class SessionService {
    
    data class Session(
        val sessionId: UUID,
        val userId: UUID,
        val tenantId: UUID,
        val deviceId: String,
        val refreshTokenHash: String,
        val ipAddress: String,
        val userAgent: String,
        val createdAt: Instant,
        val expiresAt: Instant,
        var isActive: Boolean = true
    )
    
    private val activeSessions = ConcurrentHashMap<UUID, Session>()
    private val maxSessionsPerUser = 5
    
    fun createSession(
        userId: UUID,
        tenantId: UUID,
        deviceId: String,
        refreshTokenHash: String,
        ipAddress: String,
        userAgent: String
    ): Session {
        val userSessions = activeSessions.values.filter { it.userId == userId }
        if (userSessions.size >= maxSessionsPerUser) {
            val oldest = userSessions.minByOrNull { it.createdAt }
            oldest?.let { revokeSession(it.sessionId) }
        }
        
        val session = Session(
            sessionId = UUID.randomUUID(),
            userId = userId,
            tenantId = tenantId,
            deviceId = deviceId,
            refreshTokenHash = refreshTokenHash,
            ipAddress = ipAddress,
            userAgent = userAgent,
            createdAt = Instant.now(),
            expiresAt = Instant.now().plusSeconds(604800)
        )
        
        activeSessions[session.sessionId] = session
        return session
    }
    
    fun revokeSession(sessionId: UUID) {
        activeSessions[sessionId]?.isActive = false
        activeSessions.remove(sessionId)
    }
    
    fun revokeAllUserSessions(userId: UUID) {
        activeSessions.values
            .filter { it.userId == userId }
            .forEach { revokeSession(it.sessionId) }
    }
    
    fun getActiveSession(sessionId: UUID): Session? {
        val session = activeSessions[sessionId]
        return if (session != null && session.isActive && session.expiresAt.isAfter(Instant.now())) {
            session
        } else {
            null
        }
    }
    
    fun getUserSessions(userId: UUID): List<Session> {
        return activeSessions.values
            .filter { it.userId == userId && it.isActive }
            .toList()
    }
}
