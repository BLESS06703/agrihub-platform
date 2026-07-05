package com.agrihub.platform.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*

fun Application.configureCompression() {
    install(Compression) {
        gzip {
            priority = 1.0
        }
    }
}
