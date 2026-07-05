package com.agrihub.platform.security.audit

import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

class AuditService {
    
    private val logger = LoggerFactory.getLogger("AUDIT")
    
    data class AuditEntry(
        val tenantId: UUID,
        val userId: UUID,
        val action: String,
        val tableName: String? = null,
        val recordId: UUID? = null,
        val oldValues: Map<String, Any?>? = null,
        val newValues: Map<String, Any?>? = null,
        val ipAddress: String? = null,
        val userAgent: String? = null,
        val correlationId: UUID = UUID.randomUUID(),
        val timestamp: Instant = Instant.now()
    )
    
    suspend fun log(entry: AuditEntry) {
        logger.info(
            "AUDIT | tenant={} user={} action={} table={} record={} correlation={}",
            entry.tenantId,
            entry.userId,
            entry.action,
            entry.tableName ?: "-",
            entry.recordId ?: "-",
            entry.correlationId
        )
    }
    
    suspend fun logCreate(
        tenantId: UUID,
        userId: UUID,
        tableName: String,
        recordId: UUID,
        values: Map<String, Any?>
    ) {
        log(AuditEntry(
            tenantId = tenantId,
            userId = userId,
            action = "CREATE",
            tableName = tableName,
            recordId = recordId,
            newValues = values
        ))
    }
    
    suspend fun logUpdate(
        tenantId: UUID,
        userId: UUID,
        tableName: String,
        recordId: UUID,
        oldValues: Map<String, Any?>,
        newValues: Map<String, Any?>
    ) {
        log(AuditEntry(
            tenantId = tenantId,
            userId = userId,
            action = "UPDATE",
            tableName = tableName,
            recordId = recordId,
            oldValues = oldValues,
            newValues = newValues
        ))
    }
    
    suspend fun logDelete(
        tenantId: UUID,
        userId: UUID,
        tableName: String,
        recordId: UUID
    ) {
        log(AuditEntry(
            tenantId = tenantId,
            userId = userId,
            action = "DELETE",
            tableName = tableName,
            recordId = recordId
        ))
    }
    
    suspend fun logLogin(tenantId: UUID, userId: UUID, success: Boolean, ipAddress: String?) {
        log(AuditEntry(
            tenantId = tenantId,
            userId = userId,
            action = if (success) "LOGIN" else "LOGIN_FAILED",
            ipAddress = ipAddress
        ))
    }
}
