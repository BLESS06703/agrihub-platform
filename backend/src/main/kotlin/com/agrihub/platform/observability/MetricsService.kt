package com.agrihub.platform.observability

import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicLong

object MetricsService {
    private val logger = LoggerFactory.getLogger(MetricsService::class.java)
    
    private val requestCount = AtomicLong(0)
    private val errorCount = AtomicLong(0)
    private val activeUsers = AtomicLong(0)
    private val startupTime = System.currentTimeMillis()
    
    fun recordRequest() = requestCount.incrementAndGet()
    fun recordError() = errorCount.incrementAndGet()
    fun userLoggedIn() = activeUsers.incrementAndGet()
    fun userLoggedOut() = activeUsers.decrementAndGet()
    
    fun getMetrics(): Map<String, Any> {
        return mapOf(
            "uptime_seconds" to (System.currentTimeMillis() - startupTime) / 1000,
            "total_requests" to requestCount.get(),
            "total_errors" to errorCount.get(),
            "error_rate" to if (requestCount.get() > 0) 
                (errorCount.get().toDouble() / requestCount.get() * 100) else 0.0,
            "active_users" to activeUsers.get(),
            "memory_used_mb" to Runtime.getRuntime().totalMemory() / 1024 / 1024,
            "memory_free_mb" to Runtime.getRuntime().freeMemory() / 1024 / 1024
        )
    }
}
