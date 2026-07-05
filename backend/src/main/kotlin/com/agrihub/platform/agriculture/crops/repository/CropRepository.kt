package com.agrihub.platform.agriculture.crops.repository

import com.agrihub.platform.core.database.BaseTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

object CropCatalogTable : BaseTable("crop_catalog") {
    val nameEn = varchar("name_en", 100)
    val nameNy = varchar("name_ny", 100).nullable()
    val category = varchar("category", 50)
    val growingPeriodDays = integer("growing_period_days").nullable()
    val expectedYieldMin = decimal("expected_yield_min_kg_ha", 10, 2).nullable()
    val expectedYieldMax = decimal("expected_yield_max_kg_ha", 10, 2).nullable()
    val isActive = bool("is_active").default(true)
}

object PlantingRecordsTable : BaseTable("planting_records") {
    val farmId = uuid("farm_id")
    val fieldId = uuid("field_id")
    val cropId = uuid("crop_id")
    val plantingDate = date("planting_date")
    val seedQuantityKg = decimal("seed_quantity_kg", 10, 2).nullable()
    val areaPlantedHa = decimal("area_planted_ha", 8, 2).nullable()
    val expectedHarvestDate = date("expected_harvest_date").nullable()
    val status = varchar("status", 50).default("ACTIVE")
}

object HarvestRecordsTable : BaseTable("harvest_records") {
    val plantingId = uuid("planting_id")
    val fieldId = uuid("field_id")
    val harvestDate = date("harvest_date")
    val quantityKg = decimal("quantity_kg", 10, 2)
    val qualityGrade = varchar("quality_grade", 20).default("GRADE_A")
    val yieldKgHa = decimal("yield_kg_ha", 10, 2).nullable()
}

class CropRepository {
    
    data class Crop(val id: UUID, val nameEn: String, val category: String, val growingPeriodDays: Int?)
    data class Planting(val id: UUID, val fieldId: UUID, val cropId: UUID, val plantingDate: String, val status: String)
    data class Harvest(val id: UUID, val plantingId: UUID, val harvestDate: String, val quantityKg: Double, val qualityGrade: String)
    
    fun getCropCatalog(category: String? = null): List<Crop> = transaction {
        var query = CropCatalogTable.selectAll().where { CropCatalogTable.isActive eq true }
        category?.let { query = query.andWhere { CropCatalogTable.category eq it } }
        query.orderBy(CropCatalogTable.nameEn).map {
            Crop(it[CropCatalogTable.id], it[CropCatalogTable.nameEn], it[CropCatalogTable.category], it[CropCatalogTable.growingPeriodDays])
        }
    }
    
    fun getCrop(id: UUID): Crop? = transaction {
        CropCatalogTable.selectAll().where { CropCatalogTable.id eq id }.singleOrNull()?.let {
            Crop(it[CropCatalogTable.id], it[CropCatalogTable.nameEn], it[CropCatalogTable.category], it[CropCatalogTable.growingPeriodDays])
        }
    }
    
    fun recordPlanting(tenantId: UUID, farmId: UUID, fieldId: UUID, cropId: UUID, plantingDate: String, createdBy: UUID): Planting = transaction {
        val id = UUID.randomUUID()
        PlantingRecordsTable.insert {
            it[PlantingRecordsTable.id] = id
            it[PlantingRecordsTable.tenantId] = tenantId
            it[PlantingRecordsTable.farmId] = farmId
            it[PlantingRecordsTable.fieldId] = fieldId
            it[PlantingRecordsTable.cropId] = cropId
            it[PlantingRecordsTable.plantingDate] = java.time.LocalDate.parse(plantingDate)
            it[PlantingRecordsTable.status] = "ACTIVE"
            it[PlantingRecordsTable.createdBy] = createdBy
            it[PlantingRecordsTable.createdAt] = Instant.now()
        }
        getPlanting(id, tenantId)!!
    }
    
    fun getPlanting(id: UUID, tenantId: UUID): Planting? = transaction {
        PlantingRecordsTable.selectAll().where { (PlantingRecordsTable.id eq id) and (PlantingRecordsTable.tenantId eq tenantId) }.singleOrNull()?.let {
            Planting(it[PlantingRecordsTable.id], it[PlantingRecordsTable.fieldId], it[PlantingRecordsTable.cropId], it[PlantingRecordsTable.plantingDate].toString(), it[PlantingRecordsTable.status])
        }
    }
    
    fun getFieldPlantings(fieldId: UUID, tenantId: UUID): List<Planting> = transaction {
        PlantingRecordsTable.selectAll().where { (PlantingRecordsTable.fieldId eq fieldId) and (PlantingRecordsTable.tenantId eq tenantId) }.orderBy(PlantingRecordsTable.plantingDate, SortOrder.DESC).map {
            Planting(it[PlantingRecordsTable.id], it[PlantingRecordsTable.fieldId], it[PlantingRecordsTable.cropId], it[PlantingRecordsTable.plantingDate].toString(), it[PlantingRecordsTable.status])
        }
    }
    
    fun recordHarvest(tenantId: UUID, plantingId: UUID, fieldId: UUID, harvestDate: String, quantityKg: Double, qualityGrade: String, createdBy: UUID): Harvest = transaction {
        val id = UUID.randomUUID()
        HarvestRecordsTable.insert {
            it[HarvestRecordsTable.id] = id
            it[HarvestRecordsTable.tenantId] = tenantId
            it[HarvestRecordsTable.plantingId] = plantingId
            it[HarvestRecordsTable.fieldId] = fieldId
            it[HarvestRecordsTable.harvestDate] = java.time.LocalDate.parse(harvestDate)
            it[HarvestRecordsTable.quantityKg] = quantityKg.toBigDecimal()
            it[HarvestRecordsTable.qualityGrade] = qualityGrade
            it[HarvestRecordsTable.createdBy] = createdBy
            it[HarvestRecordsTable.createdAt] = Instant.now()
        }
        getHarvest(id, tenantId)!!
    }
    
    fun getHarvest(id: UUID, tenantId: UUID): Harvest? = transaction {
        HarvestRecordsTable.selectAll().where { (HarvestRecordsTable.id eq id) and (HarvestRecordsTable.tenantId eq tenantId) }.singleOrNull()?.let {
            Harvest(it[HarvestRecordsTable.id], it[HarvestRecordsTable.plantingId], it[HarvestRecordsTable.harvestDate].toString(), it[HarvestRecordsTable.quantityKg].toDouble(), it[HarvestRecordsTable.qualityGrade])
        }
    }
    
    fun getFieldHarvests(fieldId: UUID, tenantId: UUID): List<Harvest> = transaction {
        HarvestRecordsTable.selectAll().where { (HarvestRecordsTable.fieldId eq fieldId) and (HarvestRecordsTable.tenantId eq tenantId) }.orderBy(HarvestRecordsTable.harvestDate, SortOrder.DESC).map {
            Harvest(it[HarvestRecordsTable.id], it[HarvestRecordsTable.plantingId], it[HarvestRecordsTable.harvestDate].toString(), it[HarvestRecordsTable.quantityKg].toDouble(), it[HarvestRecordsTable.qualityGrade])
        }
    }
}
