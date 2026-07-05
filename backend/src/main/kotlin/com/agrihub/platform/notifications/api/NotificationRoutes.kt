package com.agrihub.platform.notifications.api

import com.agrihub.platform.notifications.repository.NotificationRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.notificationRoutes(repo: NotificationRepository) {
    route("/v1/notifications") {
        
        get {
            val unreadOnly = call.request.queryParameters["unread"]?.toBoolean() ?: false
            val tenantId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val notifications = repo.getUserNotifications(userId, tenantId, unreadOnly)
            call.respond(mapOf("success" to true, "data" to notifications))
        }
        
        get("/unread-count") {
            val count = repo.getUnreadCount(UUID.randomUUID(), UUID.randomUUID())
            call.respond(mapOf("success" to true, "data" to mapOf("count" to count)))
        }
        
        put("/{id}/read") {
            val id = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@put
            repo.markAsRead(id, UUID.randomUUID())
            call.respond(mapOf("success" to true))
        }
        
        put("/read-all") {
            val count = repo.markAllAsRead(UUID.randomUUID(), UUID.randomUUID())
            call.respond(mapOf("success" to true, "data" to mapOf("markedRead" to count)))
        }
        
        post("/devices") {
            val body = call.receive<Map<String, String>>()
            val device = repo.registerDevice(UUID.randomUUID(), UUID.randomUUID(), body["deviceId"] ?: return@post, body["platform"], body["pushToken"])
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to device))
        }
    }
}
