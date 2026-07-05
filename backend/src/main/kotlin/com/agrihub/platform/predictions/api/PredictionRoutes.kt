package com.agrihub.platform.predictions.api

import com.agrihub.platform.predictions.PredictionService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.predictionRoutes(predictionService: PredictionService) {
    route("/v1/predictions") {
        get("/market") { call.respond(mapOf("success" to true, "data" to predictionService.getMarketPredictions())) }
        get("/yield/{farmId}") { call.respond(mapOf("success" to true, "data" to predictionService.getYieldPrediction(UUID.fromString(call.parameters["farmId"]!!)))) }
        get("/risks/{farmId}") { call.respond(mapOf("success" to true, "data" to predictionService.getRiskAssessment(UUID.fromString(call.parameters["farmId"]!!)))) }
        get("/season/{farmId}") { call.respond(mapOf("success" to true, "data" to predictionService.getSeasonForecast(UUID.fromString(call.parameters["farmId"]!!)))) }
    }
}
