package com.agrihub.platform.community.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.communityRoutes() {
    route("/v1/community") {
        get("/forums") {
            call.respond(mapOf("success" to true, "data" to listOf(
                mapOf("name" to "Maize Farmers Malawi", "members" to 3200, "posts" to 8500),
                mapOf("name" to "Organic Farming", "members" to 1800, "posts" to 4200),
                mapOf("name" to "Livestock Health", "members" to 2100, "posts" to 6100)
            )))
        }
        get("/forums/{id}/posts") {
            call.respond(mapOf("success" to true, "data" to listOf(
                mapOf("title" to "Best time to plant maize this season?", "replies" to 24, "views" to 450),
                mapOf("title" to "How to control fall armyworm naturally?", "replies" to 18, "views" to 380)
            )))
        }
        get("/experts") {
            call.respond(mapOf("success" to true, "data" to listOf(
                mapOf("name" to "Dr. Banda", "specialization" to "Agronomy", "rating" to 4.8, "responses" to 342),
                mapOf("name" to "Dr. Phiri", "specialization" to "Veterinary", "rating" to 4.9, "responses" to 256)
            )))
        }
        get("/ask-expert") {
            call.respond(mapOf("success" to true, "data" to mapOf("message" to "Submit your question to our agricultural experts")))
        }
    }
}
