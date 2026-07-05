package com.agrihub.platform.core.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

fun <T> executeWithAudit(
    tenantId: UUID,
    userId: UUID,
    block: () -> T
): T {
    return transaction {
        // Set tenant context for RLS
        exec("SET app.current_tenant_id = '${tenantId}'")
        block()
    }
}

fun Table.softDelete(
    id: UUID,
    userId: UUID
) {
    update({ this.columns.find { it.name == "id" }!!.eq(id) }) {
        it[this.columns.find { it.name == "deleted_at" }!!] = Instant.now()
        it[this.columns.find { it.name == "updated_by" }!!] = userId
        it[this.columns.find { it.name == "updated_at" }!!] = Instant.now()
    }
}
