package com.agrihub.platform.platform.settings

import java.util.UUID

class SettingsService {
    
    private val settings = mutableMapOf<String, Any>(
        "platform.name" to "AgriHub Platform",
        "platform.version" to "1.0.0",
        "platform.maintenance_mode" to false,
        "auth.session_timeout_minutes" to 15,
        "auth.max_login_attempts" to 5,
        "auth.lockout_duration_minutes" to 30,
        "sync.batch_size" to 50,
        "sync.conflict_resolution" to "server_wins",
        "storage.max_file_size_mb" to 10,
        "storage.allowed_types" to listOf("jpg", "jpeg", "png", "pdf", "csv", "xlsx")
    )
    
    fun get(key: String): Any? = settings[key]
    
    fun getString(key: String): String = settings[key]?.toString() ?: ""
    
    fun getInt(key: String): Int = (settings[key] as? Int) ?: 0
    
    fun getBoolean(key: String): Boolean = (settings[key] as? Boolean) ?: false
    
    fun set(key: String, value: Any) {
        settings[key] = value
    }
    
    fun getAll(): Map<String, Any> = settings.toMap()
    
    fun getPublic(): Map<String, Any> = mapOf(
        "name" to getString("platform.name"),
        "version" to getString("platform.version"),
        "maintenance_mode" to getBoolean("platform.maintenance_mode")
    )
}
