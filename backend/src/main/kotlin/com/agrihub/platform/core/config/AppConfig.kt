package com.agrihub.platform.core.config

object AppConfig {
    val databaseUrl: String = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/agrihub"
    val databaseUser: String = System.getenv("DATABASE_USER") ?: "agrihub"
    val databasePassword: String = System.getenv("DATABASE_PASSWORD") ?: "agrihub"
    val redisUrl: String = System.getenv("REDIS_URL") ?: "redis://localhost:6379"
    val jwtPublicKey: String = System.getenv("JWT_PUBLIC_KEY") ?: ""
    val jwtPrivateKey: String = System.getenv("JWT_PRIVATE_KEY") ?: ""
    val environment: String = System.getenv("ENV") ?: "development"
    
    fun isDevelopment() = environment == "development"
    fun isProduction() = environment == "production"
}
