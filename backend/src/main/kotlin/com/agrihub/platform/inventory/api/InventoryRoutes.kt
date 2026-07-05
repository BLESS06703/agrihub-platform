package com.agrihub.platform.inventory.api
import com.agrihub.platform.inventory.repository.InventoryRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.inventoryRoutes(repo: InventoryRepository) {
    route("/v1/inventory") {
        get("/items") { call.respond(mapOf("success" to true, "data" to repo.findAll(UUID.randomUUID(), call.request.queryParameters["category"]))) }
        post("/items") {
            val b = call.receive<Map<String, String>>()
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to repo.create(UUID.randomUUID(), b["itemName"]!!, b["category"]!!, b["unit"]!!, UUID.randomUUID())))
        }
        post("/transactions") {
            val b = call.receive<Map<String, String>>()
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to repo.recordTransaction(UUID.randomUUID(), UUID.fromString(b["itemId"]), b["direction"]!!, b["quantity"]!!.toDouble(), b["reason"]!!, UUID.randomUUID())))
        }
    }
}
