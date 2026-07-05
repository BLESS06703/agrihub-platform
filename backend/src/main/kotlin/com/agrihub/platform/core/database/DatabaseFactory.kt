package com.agrihub.platform.core.database

import com.agrihub.platform.core.config.AppConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)
    private var dataSource: HikariDataSource? = null
    
    fun init() {
        logger.info("Initializing database connection...")
        
        val config = HikariConfig().apply {
            jdbcUrl = AppConfig.databaseUrl
            username = AppConfig.databaseUser
            password = AppConfig.databasePassword
            maximumPoolSize = 20
            minimumIdle = 5
            idleTimeout = 600000
            connectionTimeout = 30000
            maxLifetime = 1800000
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_READ_COMMITTED"
            
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            addDataSourceProperty("useServerPrepStmts", "true")
        }
        
        dataSource = HikariDataSource(config)
        Database.connect(dataSource!!)
        
        logger.info("Database connection established")
    }
    
    fun shutdown() {
        logger.info("Shutting down database connection pool...")
        dataSource?.close()
        logger.info("Database connection pool closed")
    }
}
