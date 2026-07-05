package com.agrihub.platform.iot.api

import com.agrihub.platform.iot.sensors.SensorService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.iotRoutes(sensorService: SensorService) {
    route("/v1/iot") {
        get("/farms/{farmId}/devices") {
            val farmId = call.parameters["farmId"]?.let { UUID.fromString(it) } ?: return@get
            call.respond(mapOf("success" to true, "data" to sensorService.getDevices(farmId)))
        }
        get("/devices/{deviceId}/readings") {
            val hours = call.request.queryParameters["hours"]?.toIntOrNull() ?: 24
            call.respond(mapOf("success" to true, "data" to sensorService.getReadings(UUID.randomUUID(), hours)))
        }
        get("/fields/{fieldId}/conditions") {
            call.respond(mapOf("success" to true, "data" to sensorService.getFieldSensorData(UUID.randomUUID())))
        }
    }
}
