package com.agrihub.platform.warehouse.repository
import com.agrihub.platform.core.database.BaseTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

object WarehousesTable : BaseTable("warehouses") {
    val warehouseName = varchar("warehouse_name", 255)
    val warehouseType = varchar("warehouse_type", 50)
    val totalCapacityKg = decimal("total_capacity_kg", 12, 2).nullable()
    val currentOccupancyKg = decimal("current_occupancy_kg", 12, 2).default(0.toBigDecimal())
}

object IntakeTable : BaseTable("warehouse_intake") {
    val warehouseId = uuid("warehouse_id")
    val cropId = uuid("crop_id")
    val quantityKg = decimal("quantity_kg", 10, 2)
    val intakeDate = date("intake_date")
    val sourceName = varchar("source_name", 255).nullable()
}

class WarehouseRepository {
    data class Warehouse(val id: UUID, val name: String, val type: String, val capacity: Double?, val occupancy: Double)
    data class Intake(val id: UUID, val quantityKg: Double, val intakeDate: String, val source: String?)
    
    fun findAll(tenantId: UUID): List<Warehouse> = transaction {
        WarehousesTable.selectAll().where { (WarehousesTable.tenantId eq tenantId) and (WarehousesTable.deletedAt.isNull()) }
            .map { Warehouse(it[WarehousesTable.id], it[WarehousesTable.warehouseName], it[WarehousesTable.warehouseType], it[WarehousesTable.totalCapacityKg]?.toDouble(), it[WarehousesTable.currentOccupancyKg].toDouble()) }
    }
    
    fun create(tenantId: UUID, name: String, type: String, capacity: Double?, createdBy: UUID): Warehouse = transaction {
        val id = UUID.randomUUID()
        WarehousesTable.insert {
            it[WarehousesTable.id] = id; it[WarehousesTable.tenantId] = tenantId; it[WarehousesTable.warehouseName] = name
            it[WarehousesTable.warehouseType] = type; it[WarehousesTable.totalCapacityKg] = capacity?.toBigDecimal()
            it[WarehousesTable.createdBy] = createdBy; it[WarehousesTable.createdAt] = Instant.now()
        }
        Warehouse(id, name, type, capacity, 0.0)
    }
    
    fun recordIntake(tenantId: UUID, warehouseId: UUID, cropId: UUID, quantityKg: Double, sourceName: String?, createdBy: UUID): Intake = transaction {
        val id = UUID.randomUUID()
        IntakeTable.insert {
            it[IntakeTable.id] = id; it[IntakeTable.tenantId] = tenantId; it[IntakeTable.warehouseId] = warehouseId
            it[IntakeTable.cropId] = cropId; it[IntakeTable.quantityKg] = quantityKg.toBigDecimal()
            it[IntakeTable.intakeDate] = java.time.LocalDate.now(); it[IntakeTable.sourceName] = sourceName
            it[IntakeTable.createdBy] = createdBy; it[IntakeTable.createdAt] = Instant.now()
        }
        WarehousesTable.update({ (WarehousesTable.id eq warehouseId) and (WarehousesTable.tenantId eq tenantId) }) {
            with(SqlExpressionBuilder) { it.update(currentOccupancyKg, currentOccupancyKg + quantityKg.toBigDecimal()) }
        }
        Intake(id, quantityKg, java.time.LocalDate.now().toString(), sourceName)
    }
}
