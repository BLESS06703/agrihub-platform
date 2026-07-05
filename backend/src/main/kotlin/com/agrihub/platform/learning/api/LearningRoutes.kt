package com.agrihub.platform.learning.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.learningRoutes() {
    route("/v1/learning") {
        get("/courses") {
            call.respond(mapOf("success" to true, "data" to listOf(
                mapOf("id" to "1", "title" to "Maize Farming Best Practices", "category" to "CROPS", "difficulty" to "BEGINNER", "duration" to 45, "enrolled" to 1250),
                mapOf("id" to "2", "title" to "Soil Health Management", "category" to "SOIL", "difficulty" to "INTERMEDIATE", "duration" to 60, "enrolled" to 890),
                mapOf("id" to "3", "title" to "Pest & Disease Control", "category" to "PROTECTION", "difficulty" to "ADVANCED", "duration" to 75, "enrolled" to 670)
            )))
        }
        get("/courses/{id}") {
            call.respond(mapOf("success" to true, "data" to mapOf("id" to "1", "title" to "Maize Farming Best Practices", "lessons" to listOf(
                mapOf("title" to "Soil Preparation", "type" to "VIDEO", "duration" to 12),
                mapOf("title" to "Seed Selection", "type" to "ARTICLE", "duration" to 8),
                mapOf("title" to "Fertilizer Application", "type" to "VIDEO", "duration" to 15)
            ))))
        }
        get("/my-progress") {
            call.respond(mapOf("success" to true, "data" to mapOf("coursesCompleted" to 3, "lessonsCompleted" to 24, "totalHours" to 18.5)))
        }
    }
}
