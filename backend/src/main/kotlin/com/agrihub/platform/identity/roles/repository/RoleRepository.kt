package com.agrihub.platform.identity.roles.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

object RolesTable : Table("roles") {
    val id = uuid("id").autoGenerate()
    val tenantId = uuid("tenant_id").nullable()
    val name = varchar("name", 100)
    val description = text("description").nullable()
    val isSystem = bool("is_system").default(false)
    override val primaryKey = PrimaryKey(id)
}

object PermissionsTable : Table("permissions") {
    val id = uuid("id").autoGenerate()
    val code = varchar("code", 100).uniqueIndex()
    val resource = varchar("resource", 50)
    val action = varchar("action", 50)
    val scope = varchar("scope", 50).default("own")
    override val primaryKey = PrimaryKey(id)
}

object RolePermissionsTable : Table("role_permissions") {
    val roleId = uuid("role_id").references(RolesTable.id)
    val permissionId = uuid("permission_id").references(PermissionsTable.id)
    override val primaryKey = PrimaryKey(roleId, permissionId)
}

object UserRolesTable : Table("user_roles") {
    val userId = uuid("user_id")
    val roleId = uuid("role_id").references(RolesTable.id)
    val assignedBy = uuid("assigned_by").nullable()
    val assignedAt = timestampWithTimeZone("assigned_at").default(Instant.now())
    override val primaryKey = PrimaryKey(userId, roleId)
}

class RoleRepository {
    data class Role(val id: UUID, val tenantId: UUID?, val name: String, val isSystem: Boolean)
    data class Permission(val id: UUID, val code: String, val resource: String, val action: String)
    
    fun findAll(tenantId: UUID? = null): List<Role> = transaction {
        var query = RolesTable.selectAll()
        tenantId?.let { query = query.where { RolesTable.tenantId eq it } }
        query.map { Role(it[RolesTable.id], it[RolesTable.tenantId], it[RolesTable.name], it[RolesTable.isSystem]) }
    }
    
    fun findById(id: UUID): Role? = transaction {
        RolesTable.selectAll().where { RolesTable.id eq id }.singleOrNull()?.let {
            Role(it[RolesTable.id], it[RolesTable.tenantId], it[RolesTable.name], it[RolesTable.isSystem])
        }
    }
    
    fun assignRoleToUser(userId: UUID, roleId: UUID, assignedBy: UUID) = transaction {
        UserRolesTable.insertIgnore {
            it[UserRolesTable.userId] = userId
            it[UserRolesTable.roleId] = roleId
            it[UserRolesTable.assignedBy] = assignedBy
        }
    }
    
    fun removeRoleFromUser(userId: UUID, roleId: UUID) = transaction {
        UserRolesTable.deleteWhere { (UserRolesTable.userId eq userId) and (UserRolesTable.roleId eq roleId) }
    }
    
    fun getUserRoles(userId: UUID): List<Role> = transaction {
        (UserRolesTable innerJoin RolesTable).selectAll().where { UserRolesTable.userId eq userId }
            .map { Role(it[RolesTable.id], it[RolesTable.tenantId], it[RolesTable.name], it[RolesTable.isSystem]) }
    }
    
    fun create(name: String, tenantId: UUID?, description: String? = null): Role = transaction {
        val id = UUID.randomUUID()
        RolesTable.insert {
            it[RolesTable.id] = id; it[RolesTable.tenantId] = tenantId
            it[RolesTable.name] = name; it[RolesTable.description] = description
        }
        findById(id)!!
    }
}
