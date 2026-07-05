package com.agrihub.platform.core.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone
import java.util.UUID

abstract class BaseTable(name: String) : Table(name) {
    val id: Column<UUID> = uuid("id").autoGenerate()
    val tenantId: Column<UUID> = uuid("tenant_id")
    val createdBy: Column<UUID> = uuid("created_by")
    val createdAt = timestampWithTimeZone("created_at")
    val updatedBy: Column<UUID?> = uuid("updated_by").nullable()
    val updatedAt = timestampWithTimeZone("updated_at").nullable()
    val deletedAt = timestampWithTimeZone("deleted_at").nullable()
    
    override val primaryKey = PrimaryKey(id)
}
