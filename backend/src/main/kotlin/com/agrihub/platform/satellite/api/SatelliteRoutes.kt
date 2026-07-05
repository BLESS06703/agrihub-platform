package com.agrihub.platform.satellite.api

import com.agrihub.platform.satellite.imagery.SatelliteService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.satelliteRoutes(satelliteService: SatelliteService) {
    route("/v1/satellite") {
        get("/farms/{farmId}/imagery") {
            val days = call.request.queryParameters["days"]?.toIntOrNull() ?: 30
            call.respond(mapOf("success" to true, "data" to satelliteService.getFarmImagery(UUID.randomUUID(), days)))
        }
        get("/farms/{farmId}/crop-health") {
            call.respond(mapOf("success" to true, "data" to satelliteService.getCropHealth(UUID.randomUUID())))
        }
        get("/farms/{farmId}/vegetation-trend") {
            call.respond(mapOf("success" to true, "data" to satelliteService.getVegetationTrend(UUID.randomUUID())))
        }
    }
}
