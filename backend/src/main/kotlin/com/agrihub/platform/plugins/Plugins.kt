package com.agrihub.platform.plugins

import io.ktor.server.application.*

fun Application.configurePlugins() {
    configureSerialization()
    configureAuthentication()
    configureStatusPages()
    configureCORS()
    configureMonitoring()
    configureCompression()
}
