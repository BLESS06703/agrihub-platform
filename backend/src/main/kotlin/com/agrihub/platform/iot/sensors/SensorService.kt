package com.agrihub.platform.iot.sensors

import org.slf4j.LoggerFactory
import java.util.UUID

class SensorService {
    private val logger = LoggerFactory.getLogger(SensorService::class.java)
    
    data class SensorReading(val type: String, val value: Double, val unit: String, val timestamp: String)
    data class DeviceStatus(val deviceId: String, val deviceName: String, val batteryLevel: Double, val lastReading: String?, val status: String)
    data class FieldConditions(val soilMoisture: Double?, val temperature: Double?, val humidity: Double?, val ph: Double?, val irrigationRecommendation: String?)
    
    suspend fun getFieldSensorData(fieldId: UUID): FieldConditions {
        logger.info("Reading sensors for field={}", fieldId)
        return FieldConditions(
            soilMoisture = 32.5, temperature = 28.3, humidity = 65.0, ph = 6.4,
            irrigationRecommendation = "Soil moisture adequate. No irrigation needed for 48 hours."
        )
    }
    
    suspend fun getDevices(farmId: UUID): List<DeviceStatus> = listOf(
        DeviceStatus("SENSOR-001", "Soil Moisture - Field 1", 87.5, "2026-07-05T10:00:00Z", "ACTIVE"),
        DeviceStatus("SENSOR-002", "Temperature - Field 2", 92.0, "2026-07-05T10:00:00Z", "ACTIVE"),
        DeviceStatus("SENSOR-003", "Water Level - Tank A", 45.0, "2026-07-05T09:30:00Z", "LOW_BATTERY")
    )
    
    suspend fun getReadings(deviceId: UUID, hours: Int = 24): List<SensorReading> = listOf(
        SensorReading("SOIL_MOISTURE", 32.5, "PERCENT", "2026-07-05T10:00:00Z"),
        SensorReading("TEMPERATURE", 28.3, "CELSIUS", "2026-07-05T10:00:00Z"),
        SensorReading("HUMIDITY", 65.0, "PERCENT", "2026-07-05T10:00:00Z")
    )
}
