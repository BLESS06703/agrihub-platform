package com.agrihub.platform.agriculture.farms.repository

import com.agrihub.platform.core.database.BaseTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

object FarmsTable : BaseTable("farms") {
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val areaHectares = decimal("area_hectares", 10, 2).nullable()
    val soilType = varchar("soil_type", 50).nullable()
    val waterSource = varchar("water_source", 50).nullable()
    val status = varchar("status", 50).default("ACTIVE")
    val district = varchar("district", 100).nullable()
    val village = varchar("village", 100).nullable()
    val region = varchar("region", 100).nullable()
}

object FieldsTable : BaseTable("fields") {
    val farmId = uuid("farm_id").references(FarmsTable.id)
    val fieldName = varchar("field_name", 255)
    val fieldCode = varchar("field_code", 50).nullable()
    val areaHectares = decimal("area_hectares", 8, 2).nullable()
    val status = varchar("status", 50).default("EMPTY")
}

class FarmRepository {
    
    data class Farm(
        val id: UUID, val tenantId: UUID, val name: String,
        val areaHectares: Double?, val status: String,
        val district: String?, val village: String?, val region: String?
    )
    
    data class Field(
        val id: UUID, val farmId: UUID, val fieldName: String,
        val areaHectares: Double?, val status: String
    )
    
    fun findAll(tenantId: UUID, page: Int = 1, perPage: Int = 20): Pair<List<Farm>, Long> = transaction {
        val query = FarmsTable.selectAll()
            .where { (FarmsTable.tenantId eq tenantId) and (FarmsTable.deletedAt.isNull()) }
        
        val total = query.count()
        val results = query
            .orderBy(FarmsTable.createdAt, SortOrder.DESC)
            .limit(perPage, offset = ((page - 1) * perPage).toLong())
            .map { it.toFarm() }
        
        results to total
    }
    
    fun findById(id: UUID, tenantId: UUID): Farm? = transaction {
        FarmsTable.selectAll()
            .where { (FarmsTable.id eq id) and (FarmsTable.tenantId eq tenantId) and (FarmsTable.deletedAt.isNull()) }
            .singleOrNull()?.toFarm()
    }
    
    fun create(tenantId: UUID, name: String, areaHectares: Double?, district: String?, village: String?, createdBy: UUID): Farm = transaction {
        val id = UUID.randomUUID()
        FarmsTable.insert {
            it[FarmsTable.id] = id
            it[FarmsTable.tenantId] = tenantId
            it[FarmsTable.name] = name
            it[FarmsTable.areaHectares] = areaHectares?.toBigDecimal()
            it[FarmsTable.district] = district
            it[FarmsTable.village] = village
            it[FarmsTable.status] = "ACTIVE"
            it[FarmsTable.createdBy] = createdBy
            it[FarmsTable.createdAt] = Instant.now()
        }
        findById(id, tenantId)!!
    }
    
    fun update(id: UUID, tenantId: UUID, name: String?, areaHectares: Double?, soilType: String?, updatedBy: UUID): Farm? = transaction {
        FarmsTable.update({ (FarmsTable.id eq id) and (FarmsTable.tenantId eq tenantId) }) {
            name?.let { it[FarmsTable.name] = it }
            areaHectares?.let { it[FarmsTable.areaHectares] = it.toBigDecimal() }
            soilType?.let { it[FarmsTable.soilType] = it }
            it[FarmsTable.updatedBy] = updatedBy
            it[FarmsTable.updatedAt] = Instant.now()
        }
        findById(id, tenantId)
    }
    
    fun getFields(farmId: UUID, tenantId: UUID): List<Field> = transaction {
        FieldsTable.selectAll()
            .where { (FieldsTable.farmId eq farmId) and (FieldsTable.tenantId eq tenantId) and (FieldsTable.deletedAt.isNull()) }
            .map { it.toField() }
    }
    
    fun addField(farmId: UUID, tenantId: UUID, fieldName: String, areaHectares: Double?, createdBy: UUID): Field = transaction {
        val id = UUID.randomUUID()
        FieldsTable.insert {
            it[FieldsTable.id] = id
            it[FieldsTable.tenantId] = tenantId
            it[FieldsTable.farmId] = farmId
            it[FieldsTable.fieldName] = fieldName
            it[FieldsTable.areaHectares] = areaHectares?.toBigDecimal()
            it[FieldsTable.status] = "EMPTY"
            it[FieldsTable.createdBy] = createdBy
            it[FieldsTable.createdAt] = Instant.now()
        }
        getFields(farmId, tenantId).first { it.id == id }
    }
    
    private fun ResultRow.toFarm() = Farm(
        id = this[FarmsTable.id], tenantId = this[FarmsTable.tenantId],
        name = this[FarmsTable.name], areaHectares = this[FarmsTable.areaHectares]?.toDouble(),
        status = this[FarmsTable.status], district = this[FarmsTable.district],
        village = this[FarmsTable.village], region = this[FarmsTable.region]
    )
    
    private fun ResultRow.toField() = Field(
        id = this[FieldsTable.id], farmId = this[FieldsTable.farmId],
        fieldName = this[FieldsTable.fieldName],
        areaHectares = this[FieldsTable.areaHectares]?.toDouble(),
        status = this[FieldsTable.status]
    )
}
