package com.agrihub.platform.livestock.tagging.api

import com.agrihub.platform.livestock.tagging.TaggingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.taggingRoutes(taggingService: TaggingService) {
    route("/v1/livestock/tagging") {
        post("/animals/{id}/tag") {
            val b = call.receive<Map<String, String>>()
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to taggingService.assignTag(UUID.fromString(call.parameters["id"]!!), b["tagType"] ?: "RFID")))
        }
        post("/scan") {
            val b = call.receive<Map<String, String>>()
            call.respond(mapOf("success" to true, "data" to taggingService.scanTag(UUID.fromString(b["animalId"]!!), b["tagType"] ?: "RFID", b["lat"]!!.toDouble(), b["lon"]!!.toDouble(), UUID.randomUUID())))
        }
        get("/animals/{id}/history") { call.respond(mapOf("success" to true, "data" to taggingService.getAnimalHistory(UUID.fromString(call.parameters["id"]!!)))) }
        post("/bulk-scan") {
            val b = call.receive<Map<String, List<String>>>()
            val count = taggingService.bulkScan(b["animalIds"]?.map { UUID.fromString(it) } ?: emptyList(), 0.0, 0.0, UUID.randomUUID())
            call.respond(mapOf("success" to true, "data" to mapOf("scanned" to count)))
        }
    }
}
