package com.agrihub.platform.blockchain.api

import com.agrihub.platform.blockchain.provenance.BlockchainService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.blockchainRoutes(blockchainService: BlockchainService) {
    route("/v1/blockchain") {
        post("/events") {
            val b = call.receive<Map<String, Any>>()
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to blockchainService.recordEvent(UUID.fromString(b["productId"] as String), b["eventType"] as String, UUID.randomUUID(), b["location"] as? String ?: "0,0", b)))
        }
        get("/provenance/{productId}") {
            call.respond(mapOf("success" to true, "data" to blockchainService.getProvenance(UUID.fromString(call.parameters["productId"]!!))))
        }
        get("/verify/{productId}") {
            call.respond(mapOf("success" to true, "data" to blockchainService.verifyChain(UUID.fromString(call.parameters["productId"]!!))))
        }
        get("/blocks") {
            call.respond(mapOf("success" to true, "data" to blockchainService.getLatestBlocks()))
        }
    }
}
