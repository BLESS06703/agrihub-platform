package com.agrihub.platform

import com.agrihub.platform.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Application")

fun main() {
    logger.info("===========================================")
    logger.info("  AgriHub Malawi - Starting Backend Server")
    logger.info("  Environment: ${System.getenv("ENV") ?: "development"}")
    logger.info("===========================================")
    
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    configurePlugins()
    configureRouting()
}
