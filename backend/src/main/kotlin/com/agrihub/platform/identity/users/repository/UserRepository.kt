package com.agrihub.platform.identity.users.repository

import com.agrihub.platform.core.database.BaseTable
import com.agrihub.platform.security.audit.AuditService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

object UsersTable : BaseTable("users") {
    val email = varchar("email", 255)
    val phone = varchar("phone", 50).nullable()
    val fullName = varchar("full_name", 255)
    val username = varchar("username", 100).nullable()
    val passwordHash = varchar("password_hash", 255)
    val status = varchar("status", 50).default("PENDING")
    val emailVerified = bool("email_verified").default(false)
    val emailVerifiedAt = timestampWithTimeZone("email_verified_at").nullable()
    val mfaEnabled = bool("mfa_enabled").default(false)
    val lastLoginAt = timestampWithTimeZone("last_login_at").nullable()
    val failedLoginAttempts = integer("failed_login_attempts").default(0)
    val lockedUntil = timestampWithTimeZone("locked_until").nullable()
    val avatarUrl = text("avatar_url").nullable()
    val jobTitle = varchar("job_title", 255).nullable()
    val language = varchar("language", 10).default("en")
    val refreshTokenHash = varchar("refresh_token_hash", 255).nullable()
    val lastLoginIp = varchar("last_login_ip", 50).nullable()
    
    val uniqueEmail = uniqueIndex("uq_users_tenant_email", tenantId, email)
}

class UserRepository(private val auditService: AuditService) {
    
    data class User(
        val id: UUID,
        val tenantId: UUID,
        val email: String,
        val fullName: String,
        val status: String,
        val emailVerified: Boolean
    )
    
    suspend fun findById(id: UUID, tenantId: UUID): User? = transaction {
        UsersTable.selectAll()
            .where { (UsersTable.id eq id) and (UsersTable.tenantId eq tenantId) }
            .singleOrNull()
            ?.toUser()
    }
    
    suspend fun findByEmail(email: String, tenantId: UUID): User? = transaction {
        UsersTable.selectAll()
            .where { (UsersTable.email eq email) and (UsersTable.tenantId eq tenantId) }
            .singleOrNull()
            ?.toUser()
    }
    
    suspend fun findAll(
        tenantId: UUID,
        page: Int = 1,
        perPage: Int = 20,
        status: String? = null
    ): Pair<List<User>, Long> = transaction {
        var query = UsersTable.selectAll()
            .where { UsersTable.tenantId eq tenantId }
            .andWhere { UsersTable.deletedAt.isNull() }
        
        status?.let { query = query.andWhere { UsersTable.status eq it } }
        
        val total = query.count()
        val results = query
            .orderBy(UsersTable.createdAt, SortOrder.DESC)
            .limit(perPage, offset = ((page - 1) * perPage).toLong())
            .map { it.toUser() }
        
        results to total
    }
    
    suspend fun create(
        tenantId: UUID,
        email: String,
        fullName: String,
        passwordHash: String,
        phone: String? = null,
        createdBy: UUID
    ): User = transaction {
        val id = UUID.randomUUID()
        val now = Instant.now()
        
        UsersTable.insert {
            it[UsersTable.id] = id
            it[UsersTable.tenantId] = tenantId
            it[UsersTable.email] = email
            it[UsersTable.fullName] = fullName
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.phone] = phone
            it[UsersTable.createdBy] = createdBy
            it[UsersTable.createdAt] = now
        }
        
        auditService.logCreate(
            tenantId = tenantId,
            userId = createdBy,
            tableName = "users",
            recordId = id,
            values = mapOf("email" to email, "fullName" to fullName)
        )
        
        findById(id, tenantId)!!
    }
    
    suspend fun updateStatus(
        id: UUID,
        tenantId: UUID,
        newStatus: String,
        updatedBy: UUID
    ): Boolean = transaction {
        val old = UsersTable.selectAll()
            .where { (UsersTable.id eq id) and (UsersTable.tenantId eq tenantId) }
            .singleOrNull() ?: return@transaction false
        
        UsersTable.update({
            (UsersTable.id eq id) and (UsersTable.tenantId eq tenantId)
        }) {
            it[status] = newStatus
            it[updatedBy] = updatedBy
            it[updatedAt] = Instant.now()
        }
        
        auditService.logUpdate(
            tenantId = tenantId,
            userId = updatedBy,
            tableName = "users",
            recordId = id,
            oldValues = mapOf("status" to old[UsersTable.status]),
            newValues = mapOf("status" to newStatus)
        )
        
        true
    }
    
    suspend fun softDelete(id: UUID, tenantId: UUID, deletedBy: UUID): Boolean = transaction {
        val updated = UsersTable.update({
            (UsersTable.id eq id) and (UsersTable.tenantId eq tenantId)
        }) {
            it[deletedAt] = Instant.now()
            it[updatedBy] = deletedBy
            it[updatedAt] = Instant.now()
        }
        
        if (updated > 0) {
            auditService.logDelete(tenantId, deletedBy, "users", id)
        }
        
        updated > 0
    }
    
    private fun ResultRow.toUser(): User = User(
        id = this[UsersTable.id],
        tenantId = this[UsersTable.tenantId],
        email = this[UsersTable.email],
        fullName = this[UsersTable.fullName],
        status = this[UsersTable.status],
        emailVerified = this[UsersTable.emailVerified]
    )
}
