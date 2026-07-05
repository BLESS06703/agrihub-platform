package com.agrihub.platform.app

object SystemInfo {
    fun printBanner() {
        val banner = """
=========================================================
 AGRIHUB PLATFORM
 Enterprise Agricultural SaaS
=========================================================
 Deployment      : AgriHub Malawi
 Language        : Kotlin 2.x
 Framework       : Ktor
 Database        : PostgreSQL
 Cache           : Redis
 Storage         : MinIO
 Auth            : JWT + Refresh Tokens
 Authorization   : RBAC
 Multi-Tenant    : Shared Schema + RLS
 API Version     : v1
 Countries       : MW, ZM, TZ (extensible)
 Status          : Starting...
=========================================================
        """.trimIndent()
        println(banner)
    }
    
    fun getSystemStatus(): Map<String, Any> {
        return mapOf(
            "platform" to "AgriHub Platform",
            "deployment" to mapOf(
                "name" to "AgriHub Malawi",
                "country" to "MW",
                "currency" to "MWK",
                "timezone" to "Africa/Blantyre"
            ),
            "version" to "1.0.0",
            "status" to "RUNNING",
            "available_deployments" to listOf(
                mapOf("country" to "MW", "name" to "AgriHub Malawi", "status" to "ACTIVE"),
                mapOf("country" to "ZM", "name" to "AgriHub Zambia", "status" to "PLANNED"),
                mapOf("country" to "TZ", "name" to "AgriHub Tanzania", "status" to "PLANNED"),
                mapOf("country" to "KE", "name" to "AgriHub Kenya", "status" to "PLANNED"),
                mapOf("country" to "RW", "name" to "AgriHub Rwanda", "status" to "PLANNED")
            )
        )
    }
}
