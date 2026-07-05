package com.agrihub.platform.ai.api
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.aiRoutes() {
    route("/v1/ai") {
        post("/chat") { call.respond(mapOf("success" to true, "data" to mapOf("answer" to "Based on your farm data, Field 3 maize is ready for top dressing. Would you like me to calculate the recommended fertilizer amount?"))) }
        post("/disease") { call.respond(mapOf("success" to true, "data" to mapOf("disease" to "Early Blight", "confidence" to 0.89, "treatment" to "Apply copper-based fungicide within 48 hours"))) }
        post("/yield-prediction") { call.respond(mapOf("success" to true, "data" to mapOf("estimatedYieldKg" to 3500.0, "confidence" to 0.82))) }
        post("/fertilizer") { call.respond(mapOf("success" to true, "data" to mapOf("recommendation" to "NPK 23:10:5 at 200 kg/ha", "timing" to "Apply at planting and top-dress at 4 weeks"))) }
        post("/weather-advice") { call.respond(mapOf("success" to true, "data" to mapOf("summary" to "Rain expected tomorrow. Delay outdoor operations.", "risk" to "LOW"))) }
    }
}
