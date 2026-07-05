package com.agrihub.platform.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/v1/") }
        format { call ->
            val status = call.response.status()
            "HTTP ${status?.value}: ${call.request.httpMethod.value} ${call.request.path()}"
        }
    }
}
