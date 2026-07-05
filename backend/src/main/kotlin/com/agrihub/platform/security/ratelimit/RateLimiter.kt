package com.agrihub.platform.security.ratelimit

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class RateLimiter {
    
    data class RateLimitWindow(
        val identifier: String,
        val endpoint: String,
        var count: Int = 0,
        val windowStart: Instant = Instant.now(),
        val windowDurationSeconds: Long = 60,
        val maxRequests: Int = 100
    ) {
        val isExpired: Boolean
            get() = Instant.now().isAfter(windowStart.plusSeconds(windowDurationSeconds))
        
        val isExceeded: Boolean
            get() = count > maxRequests
    }
    
    private val windows = ConcurrentHashMap<String, RateLimitWindow>()
    
    fun check(identifier: String, endpoint: String, maxRequests: Int = 100): Boolean {
        val key = "$identifier:$endpoint"
        val window = windows.getOrPut(key) {
            RateLimitWindow(identifier, endpoint, maxRequests = maxRequests)
        }
        
        if (window.isExpired) {
            windows[key] = RateLimitWindow(identifier, endpoint, maxRequests = maxRequests)
            windows[key]!!.count = 1
            return true
        }
        
        window.count++
        return !window.isExceeded
    }
    
    fun getRemaining(identifier: String, endpoint: String): Int {
        val key = "$identifier:$endpoint"
        val window = windows[key] ?: return 100
        return (window.maxRequests - window.count).coerceAtLeast(0)
    }
    
    fun getResetTime(identifier: String, endpoint: String): Long {
        val key = "$identifier:$endpoint"
        val window = windows[key] ?: return 60
        val elapsed = Instant.now().epochSecond - window.windowStart.epochSecond
        return (window.windowDurationSeconds - elapsed).coerceAtLeast(0)
    }
    
    fun reset(identifier: String, endpoint: String) {
        windows.remove("$identifier:$endpoint")
    }
}
