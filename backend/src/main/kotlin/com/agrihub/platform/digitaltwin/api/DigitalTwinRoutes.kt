package com.agrihub.platform.digitaltwin.api

import com.agrihub.platform.digitaltwin.model.DigitalTwinService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.digitalTwinRoutes(twinService: DigitalTwinService) {
    route("/v1/digital-twin") {
        post("/farms/{farmId}") {
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to twinService.createTwin(UUID.fromString(call.parameters["farmId"]!!), UUID.randomUUID())))
        }
        get("/farms/{farmId}") {
            call.respond(mapOf("success" to true, "data" to twinService.getTwin(UUID.fromString(call.parameters["farmId"]!!))))
        }
        get("/farms/{farmId}/model") {
            call.respond(mapOf("success" to true, "data" to twinService.getFarmModel(UUID.fromString(call.parameters["farmId"]!!))))
        }
        post("/simulate") {
            val b = call.receive<Map<String, Any>>()
            call.respond(mapOf("success" to true, "data" to twinService.runSimulation(UUID.fromString(b["twinId"] as String), b["type"] as String, b)))
        }
        get("/scenarios/{twinId}") {
            call.respond(mapOf("success" to true, "data" to twinService.compareScenarios(UUID.fromString(call.parameters["twinId"]!!))))
        }
        post("/scenarios") {
            val b = call.receive<Map<String, Any>>()
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to twinService.createScenario(UUID.fromString(b["twinId"] as String), b["name"] as String, b)))
        }
    }
}
