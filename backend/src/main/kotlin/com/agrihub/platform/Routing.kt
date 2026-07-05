package com.agrihub.platform

import com.agrihub.platform.agriculture.crops.api.cropRoutes
import com.agrihub.platform.agriculture.crops.repository.CropRepository
import com.agrihub.platform.agriculture.farms.api.farmRoutes
import com.agrihub.platform.agriculture.farms.repository.FarmRepository
import com.agrihub.platform.ai.api.aiRoutes
import com.agrihub.platform.app.SystemInfo
import com.agrihub.platform.banking.api.bankingRoutes
import com.agrihub.platform.banking.loans.BankingService
import com.agrihub.platform.carbon.api.carbonRoutes
import com.agrihub.platform.carbon.credits.CarbonService
import com.agrihub.platform.community.api.communityRoutes
import com.agrihub.platform.finance.api.financeRoutes
import com.agrihub.platform.finance.repository.FinanceRepository
import com.agrihub.platform.identity.auth.authRoutes
import com.agrihub.platform.identity.roles.repository.RoleRepository
import com.agrihub.platform.identity.users.api.identityRoutes
import com.agrihub.platform.identity.users.repository.UserRepository
import com.agrihub.platform.integrations.api.integrationRoutes
import com.agrihub.platform.integrations.mobilemoney.MobileMoneyService
import com.agrihub.platform.integrations.weather.WeatherService
import com.agrihub.platform.inventory.api.inventoryRoutes
import com.agrihub.platform.inventory.repository.InventoryRepository
import com.agrihub.platform.iot.api.iotRoutes
import com.agrihub.platform.iot.sensors.SensorService
import com.agrihub.platform.learning.api.learningRoutes
import com.agrihub.platform.livestock.animals.api.animalRoutes
import com.agrihub.platform.livestock.animals.repository.AnimalRepository
import com.agrihub.platform.livestock.tagging.api.taggingRoutes
import com.agrihub.platform.livestock.tagging.TaggingService
import com.agrihub.platform.marketplace.api.marketplaceRoutes
import com.agrihub.platform.marketplace.repository.MarketplaceRepository
import com.agrihub.platform.notifications.api.notificationRoutes
import com.agrihub.platform.notifications.repository.NotificationRepository
import com.agrihub.platform.platform.featureflags.FeatureFlagService
import com.agrihub.platform.platform.settings.SettingsService
import com.agrihub.platform.platform.platformRoutes
import com.agrihub.platform.satellite.api.satelliteRoutes
import com.agrihub.platform.satellite.imagery.SatelliteService
import com.agrihub.platform.security.audit.AuditService
import com.agrihub.platform.warehouse.api.warehouseRoutes
import com.agrihub.platform.warehouse.repository.WarehouseRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val auditService = AuditService()
    val userRepo = UserRepository(auditService); val roleRepo = RoleRepository()
    val settings = SettingsService(); val flags = FeatureFlagService()
    val farmRepo = FarmRepository(); val cropRepo = CropRepository()
    val animalRepo = AnimalRepository(); val inventoryRepo = InventoryRepository()
    val warehouseRepo = WarehouseRepository(); val marketplaceRepo = MarketplaceRepository()
    val financeRepo = FinanceRepository(); val notificationRepo = NotificationRepository()
    val moneyService = MobileMoneyService(); val weatherService = WeatherService()
    val sensorService = SensorService(); val satelliteService = SatelliteService()
    val bankingService = BankingService(); val carbonService = CarbonService()
    val taggingService = TaggingService()
import com.agrihub.platform.blockchain.api.blockchainRoutes
import com.agrihub.platform.blockchain.provenance.BlockchainService
import com.agrihub.platform.digitaltwin.api.digitalTwinRoutes
import com.agrihub.platform.digitaltwin.model.DigitalTwinService

fun Application.configureRouting() {
    val auditService = AuditService()
    val userRepo = UserRepository(auditService); val roleRepo = RoleRepository()
    val settings = SettingsService(); val flags = FeatureFlagService()
    val farmRepo = FarmRepository(); val cropRepo = CropRepository()
    val animalRepo = AnimalRepository(); val inventoryRepo = InventoryRepository()
    val warehouseRepo = WarehouseRepository(); val marketplaceRepo = MarketplaceRepository()
    val financeRepo = FinanceRepository(); val notificationRepo = NotificationRepository()
    val moneyService = MobileMoneyService(); val weatherService = WeatherService()
    val sensorService = SensorService(); val satelliteService = SatelliteService()
    val bankingService = BankingService(); val carbonService = CarbonService()
    val taggingService = TaggingService()
    val twinService = DigitalTwinService(); val blockchainService = BlockchainService()
    
    routing {
        get("/health") { call.respond(SystemInfo.getSystemStatus()) }
        get("/ready") { call.respond(mapOf("status" to "READY")) }
        get("/live") { call.respond(mapOf("status" to "LIVE")) }
        
        authenticate("auth-jwt") {
            get("/v1/profile") { call.respond(mapOf("success" to true, "data" to mapOf("userId" to call.principal<PrincipalWithClaims>()?.userId.toString()))) }
        }
        
        route("/v1") { get { call.respond(mapOf("success" to true, "data" to mapOf("platform" to "AgriHub Platform", "version" to "4.0.0-alpha", "status" to "V4_MODULES_ACTIVE"))) } }
        
        authRoutes(); identityRoutes(userRepo, roleRepo); platformRoutes(settings, flags)
        farmRoutes(farmRepo); cropRoutes(cropRepo); animalRoutes(animalRepo)
        inventoryRoutes(inventoryRepo); warehouseRoutes(warehouseRepo)
        marketplaceRoutes(marketplaceRepo); financeRoutes(financeRepo); aiRoutes()
        notificationRoutes(notificationRepo); integrationRoutes(moneyService, weatherService)
        iotRoutes(sensorService); satelliteRoutes(satelliteService)
        learningRoutes(); communityRoutes()
        bankingRoutes(bankingService); carbonRoutes(carbonService); taggingRoutes(taggingService)
        digitalTwinRoutes(twinService); blockchainRoutes(blockchainService)
    }
}
