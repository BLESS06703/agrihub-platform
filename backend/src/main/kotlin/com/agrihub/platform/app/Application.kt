package com.agrihub.platform.app

import com.agrihub.platform.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("AgriHub Platform")

fun main() {
    SystemInfo.printBanner()
    logger.info("Starting AgriHub Platform...")
    logger.info("Deployment: AgriHub Malawi")
    
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    configurePlugins()
    configureRouting()
    logger.info("AgriHub Platform is ready to serve!")
}
