package com.agrihub.platform.core.audit

import org.slf4j.LoggerFactory
import java.util.UUID

class AuditLogger {
    private val logger = LoggerFactory.getLogger("AUDIT")
    
    fun log(
        tenantId: UUID,
        userId: UUID,
        action: String,
        tableName: String? = null,
        recordId: UUID? = null,
        oldValues: String? = null,
        newValues: String? = null
    ) {
        logger.info(
            "AUDIT | tenant={} user={} action={} table={} record={}",
            tenantId, userId, action, tableName, recordId
        )
        // Later: persist to audit_logs table
    }
}
