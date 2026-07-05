package com.agrihub.platform.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf(
                    "success" to false,
                    "errors" to listOf(
                        mapOf("code" to "INTERNAL_ERROR", "message" to "An unexpected error occurred")
                    )
                )
            )
        }
    }
}
