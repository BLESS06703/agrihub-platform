package com.agrihub.platform.carbon.api

import com.agrihub.platform.carbon.credits.CarbonService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.carbonRoutes(carbonService: CarbonService) {
    route("/v1/carbon") {
        get("/projects") { call.respond(mapOf("success" to true, "data" to carbonService.getProjects(UUID.randomUUID()))) }
        post("/projects") {
            val b = call.receive<Map<String, String>>()
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to carbonService.registerProject(UUID.randomUUID(), b["projectName"]!!, b["projectType"]!!, b["methodology"] ?: "VERRA")))
        }
        post("/projects/{id}/activities") {
            val b = call.receive<Map<String, String>>()
            call.respond(mapOf("success" to true, "data" to carbonService.recordActivity(UUID.fromString(call.parameters["id"]!!), b["activityType"]!!, b["co2Kg"]!!.toDouble())))
        }
        get("/footprint") { call.respond(mapOf("success" to true, "data" to carbonService.getFootprint(UUID.randomUUID()))) }
        post("/sell") {
            val b = call.receive<Map<String, String>>()
            call.respond(mapOf("success" to true, "data" to carbonService.sellCredits(UUID.fromString(b["projectId"]!!), UUID.randomUUID(), b["amount"]!!.toDouble(), b["price"]!!.toDouble())))
        }
    }
}
