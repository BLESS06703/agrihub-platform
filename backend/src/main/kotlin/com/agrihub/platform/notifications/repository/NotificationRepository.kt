package com.agrihub.platform.notifications.repository

import com.agrihub.platform.core.database.BaseTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

object NotificationsTable : BaseTable("notifications") {
    val userId = uuid("user_id")
    val type = varchar("type", 50)
    val channel = varchar("channel", 50).default("IN_APP")
    val title = varchar("title", 255)
    val body = text("body").nullable()
    val data = jsonb("data", {})
    val isRead = bool("is_read").default(false)
    val readAt = timestampWithTimeZone("read_at").nullable()
}

object PushTokensTable : BaseTable("user_devices") {
    val userId = uuid("user_id")
    val deviceId = varchar("device_id", 255)
    val deviceName = varchar("device_name", 255).nullable()
    val platform = varchar("platform", 50).nullable()
    val pushToken = text("push_token").nullable()
    val isActive = bool("is_active").default(true)
}

class NotificationRepository {
    
    data class Notification(val id: UUID, val type: String, val title: String, val body: String?, val isRead: Boolean)
    data class Device(val id: UUID, val deviceId: String, val platform: String?, val pushToken: String?)
    
    fun create(tenantId: UUID, userId: UUID, type: String, channel: String, title: String, body: String?, createdBy: UUID): Notification = transaction {
        val id = UUID.randomUUID()
        NotificationsTable.insert {
            it[NotificationsTable.id] = id
            it[NotificationsTable.tenantId] = tenantId
            it[NotificationsTable.userId] = userId
            it[NotificationsTable.type] = type
            it[NotificationsTable.channel] = channel
            it[NotificationsTable.title] = title
            it[NotificationsTable.body] = body
            it[NotificationsTable.createdBy] = createdBy
            it[NotificationsTable.createdAt] = Instant.now()
        }
        Notification(id, type, title, body, false)
    }
    
    fun getUserNotifications(userId: UUID, tenantId: UUID, unreadOnly: Boolean = false): List<Notification> = transaction {
        var query = NotificationsTable.selectAll()
            .where { (NotificationsTable.userId eq userId) and (NotificationsTable.tenantId eq tenantId) and (NotificationsTable.deletedAt.isNull()) }
        if (unreadOnly) query = query.andWhere { NotificationsTable.isRead eq false }
        query.orderBy(NotificationsTable.createdAt, SortOrder.DESC).limit(50).map {
            Notification(it[NotificationsTable.id], it[NotificationsTable.type], it[NotificationsTable.title], it[NotificationsTable.body], it[NotificationsTable.isRead])
        }
    }
    
    fun markAsRead(id: UUID, tenantId: UUID): Boolean = transaction {
        NotificationsTable.update({ (NotificationsTable.id eq id) and (NotificationsTable.tenantId eq tenantId) }) {
            it[isRead] = true
            it[readAt] = Instant.now()
        } > 0
    }
    
    fun markAllAsRead(userId: UUID, tenantId: UUID): Int = transaction {
        NotificationsTable.update({ (NotificationsTable.userId eq userId) and (NotificationsTable.tenantId eq tenantId) and (NotificationsTable.isRead eq false) }) {
            it[isRead] = true
            it[readAt] = Instant.now()
        }
    }
    
    fun registerDevice(tenantId: UUID, userId: UUID, deviceId: String, platform: String?, pushToken: String?): Device = transaction {
        val existing = PushTokensTable.selectAll()
            .where { (PushTokensTable.userId eq userId) and (PushTokensTable.deviceId eq deviceId) }
            .singleOrNull()
        
        if (existing != null) {
            PushTokensTable.update({ (PushTokensTable.userId eq userId) and (PushTokensTable.deviceId eq deviceId) }) {
                it[PushTokensTable.pushToken] = pushToken
                it[PushTokensTable.isActive] = true
                it[PushTokensTable.lastActiveAt] = Instant.now()
            }
        } else {
            val id = UUID.randomUUID()
            PushTokensTable.insert {
                it[PushTokensTable.id] = id
                it[PushTokensTable.tenantId] = tenantId
                it[PushTokensTable.userId] = userId
                it[PushTokensTable.deviceId] = deviceId
                it[PushTokensTable.platform] = platform
                it[PushTokensTable.pushToken] = pushToken
                it[PushTokensTable.createdAt] = Instant.now()
            }
        }
        Device(UUID.randomUUID(), deviceId, platform, pushToken)
    }
    
    fun getUnreadCount(userId: UUID, tenantId: UUID): Long = transaction {
        NotificationsTable.selectAll()
            .where { (NotificationsTable.userId eq userId) and (NotificationsTable.tenantId eq tenantId) and (NotificationsTable.isRead eq false) and (NotificationsTable.deletedAt.isNull()) }
            .count()
    }
}
