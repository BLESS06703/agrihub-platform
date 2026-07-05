package com.agrihub.platform.ai

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.aiRoutes(aiService: AIService) {
    route("/v1/ai") {
        
        post("/chat") {
            val request = call.receive<AIService.ChatRequest>()
            val tenantId = UUID.randomUUID()
            
            val response = aiService.chat(request, tenantId)
            call.respond(mapOf("success" to true, "data" to response))
        }
        
        post("/disease") {
            val request = call.receive<AIService.DiseaseDetectionRequest>()
            
            val response = aiService.detectDisease(request)
            call.respond(mapOf("success" to true, "data" to response))
        }
        
        post("/yield-prediction") {
            val request = call.receive<AIService.YieldPredictionRequest>()
            
            val response = aiService.predictYield(request)
            call.respond(mapOf("success" to true, "data" to response))
        }
        
        post("/fertilizer") {
            val request = call.receive<AIService.FertilizerRequest>()
            
            val response = aiService.recommendFertilizer(request)
            call.respond(mapOf("success" to true, "data" to response))
        }
        
        post("/weather-advice") {
            val request = call.receive<AIService.WeatherAdviceRequest>()
            
            val response = aiService.getWeatherAdvice(request)
            call.respond(mapOf("success" to true, "data" to response))
        }
    }
}
