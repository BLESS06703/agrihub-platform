package com.agrihub.platform.identity.users.api

import com.agrihub.platform.identity.roles.repository.RoleRepository
import com.agrihub.platform.identity.users.repository.UserRepository
import com.agrihub.platform.security.audit.AuditService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.identityRoutes(
    userRepository: UserRepository,
    roleRepository: RoleRepository
) {
    val auditService = AuditService()
    
    route("/v1/admin") {
        
        // Users
        get("/users") {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val perPage = call.request.queryParameters["per_page"]?.toIntOrNull() ?: 20
            val status = call.request.queryParameters["status"]
            val tenantId = UUID.randomUUID() // From JWT
            
            val (users, total) = userRepository.findAll(tenantId, page, perPage, status)
            val totalPages = ((total + perPage - 1) / perPage).toInt()
            
            call.respond(mapOf(
                "success" to true,
                "data" to users.map { mapOf(
                    "id" to it.id.toString(),
                    "email" to it.email,
                    "fullName" to it.fullName,
                    "status" to it.status
                )},
                "meta" to mapOf("page" to page, "perPage" to perPage, "total" to total, "totalPages" to totalPages)
            ))
        }
        
        get("/users/{id}") {
            val userId = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@get
            val tenantId = UUID.randomUUID()
            
            val user = userRepository.findById(userId, tenantId)
            if (user != null) {
                val roles = roleRepository.getUserRoles(userId)
                call.respond(mapOf("success" to true, "data" to mapOf(
                    "user" to user,
                    "roles" to roles.map { it.name }
                )))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("success" to false, "errors" to listOf(
                    mapOf("code" to "NOT_FOUND", "message" to "User not found")
                )))
            }
        }
        
        put("/users/{id}/status") {
            val userId = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@put
            val tenantId = UUID.randomUUID()
            val updatedBy = UUID.randomUUID()
            val body = call.receive<Map<String, String>>()
            val newStatus = body["status"] ?: return@put
            
            val updated = userRepository.updateStatus(userId, tenantId, newStatus, updatedBy)
            if (updated) {
                call.respond(mapOf("success" to true, "data" to mapOf("message" to "Status updated")))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("success" to false))
            }
        }
        
        delete("/users/{id}") {
            val userId = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@delete
            val tenantId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()
            
            userRepository.softDelete(userId, tenantId, deletedBy)
            call.respond(mapOf("success" to true, "data" to mapOf("message" to "User deleted")))
        }
        
        // Roles
        get("/roles") {
            val tenantId = UUID.randomUUID()
            val roles = roleRepository.findAll(tenantId)
            call.respond(mapOf("success" to true, "data" to roles))
        }
        
        post("/roles") {
            val body = call.receive<Map<String, String>>()
            val name = body["name"] ?: return@post
            val tenantId = UUID.randomUUID()
            
            val role = roleRepository.create(name, tenantId)
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to role))
        }
        
        post("/users/{id}/roles") {
            val userId = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@post
            val body = call.receive<Map<String, String>>()
            val roleId = body["roleId"]?.let { UUID.fromString(it) } ?: return@post
            val assignedBy = UUID.randomUUID()
            
            roleRepository.assignRoleToUser(userId, roleId, assignedBy)
            call.respond(mapOf("success" to true, "data" to mapOf("message" to "Role assigned")))
        }
        
        delete("/users/{id}/roles/{roleId}") {
            val userId = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@delete
            val roleId = call.parameters["roleId"]?.let { UUID.fromString(it) } ?: return@delete
            
            roleRepository.removeRoleFromUser(userId, roleId)
            call.respond(mapOf("success" to true, "data" to mapOf("message" to "Role removed")))
        }
        
        // Tenant
        get("/tenant") {
            call.respond(mapOf("success" to true, "data" to mapOf(
                "name" to "AgriHub Malawi",
                "country" to "MW",
                "status" to "ACTIVE",
                "subscription" to "PRO"
            )))
        }
        
        put("/tenant") {
            val body = call.receive<Map<String, String>>()
            call.respond(mapOf("success" to true, "data" to mapOf("message" to "Tenant updated")))
        }
    }
}
