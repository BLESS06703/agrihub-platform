package com.agrihub.platform.autonomous.api

import com.agrihub.platform.autonomous.agent.AutonomousAgent
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.autonomousRoutes(agent: AutonomousAgent) {
    route("/v1/autonomous") {
        get("/farms/{farmId}/briefing") {
            call.respond(mapOf("success" to true, "data" to agent.getDailyBriefing(UUID.fromString(call.parameters["farmId"]!!))))
        }
        get("/farms/{farmId}/agents") {
            call.respond(mapOf("success" to true, "data" to agent.getAgentConfigs(UUID.fromString(call.parameters["farmId"]!!))))
        }
        post("/farms/{farmId}/agents") {
            val b = call.receive<Map<String, String>>()
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to agent.configureAgent(UUID.fromString(call.parameters["farmId"]!!), b["agentType"]!!, b["autonomyLevel"]!!, UUID.randomUUID())))
        }
        get("/actions/pending/{farmId}") {
            call.respond(mapOf("success" to true, "data" to agent.getPendingActions(UUID.fromString(call.parameters["farmId"]!!))))
        }
        post("/actions/{decisionId}/execute") {
            call.respond(mapOf("success" to true, "data" to agent.executeAction(UUID.fromString(call.parameters["decisionId"]!!), UUID.randomUUID())))
        }
    }
}
