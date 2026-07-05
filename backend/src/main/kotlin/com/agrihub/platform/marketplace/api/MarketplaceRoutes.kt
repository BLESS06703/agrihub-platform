package com.agrihub.platform.marketplace.api
import com.agrihub.platform.marketplace.repository.MarketplaceRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.marketplaceRoutes(repo: MarketplaceRepository) {
    route("/v1/marketplace") {
        get("/listings") { call.respond(mapOf("success" to true, "data" to repo.findAll(UUID.randomUUID()))) }
        post("/listings") {
            val b = call.receive<Map<String, String>>()
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to repo.create(UUID.randomUUID(), UUID.randomUUID(), UUID.fromString(b["cropId"]!!), b["quantityKg"]!!.toDouble(), b["pricePerKg"]!!.toDouble(), UUID.randomUUID())))
        }
        post("/listings/{id}/offers") {
            val b = call.receive<Map<String, String>>()
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to repo.makeOffer(UUID.randomUUID(), UUID.fromString(call.parameters["id"]!!), UUID.randomUUID(), b["price"]!!.toDouble(), b["quantity"]!!.toDouble(), UUID.randomUUID())))
        }
    }
}
