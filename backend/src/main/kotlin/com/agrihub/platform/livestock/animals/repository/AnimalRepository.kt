package com.agrihub.platform.livestock.animals.repository

import com.agrihub.platform.core.database.BaseTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

object AnimalsTable : BaseTable("animals") {
    val tagId = varchar("tag_id", 50)
    val name = varchar("name", 255).nullable()
    val species = varchar("species", 50)
    val breed = varchar("breed", 255).nullable()
    val sex = varchar("sex", 10)
    val dateOfBirth = date("date_of_birth").nullable()
    val status = varchar("status", 50).default("ALIVE")
    val currentWeightKg = decimal("current_weight_kg", 7, 2).nullable()
    val penId = varchar("pen_id", 100).nullable()
    val purpose = varchar("purpose", 50).nullable()
    val color = varchar("color", 100).nullable()
    
    val uniqueTag = uniqueIndex("uq_animals_tenant_tag", tenantId, tagId)
}

object VaccinationsTable : BaseTable("vaccinations") {
    val animalId = uuid("animal_id").references(AnimalsTable.id)
    val vaccineName = varchar("vaccine_name", 255)
    val dateAdministered = date("date_administered")
    val nextDueDate = date("next_due_date").nullable()
    val administeredBy = varchar("administered_by", 255).nullable()
    val batchNumber = varchar("batch_number", 100).nullable()
}

class AnimalRepository {
    
    data class Animal(val id: UUID, val tagId: String, val species: String, val status: String)
    data class Vaccination(val id: UUID, val animalId: UUID, val vaccineName: String, val dateAdministered: String, val nextDueDate: String?)
    
    fun findAll(tenantId: UUID, species: String? = null, status: String? = null, page: Int = 1, perPage: Int = 20): Pair<List<Animal>, Long> = transaction {
        var query = AnimalsTable.selectAll().where { (AnimalsTable.tenantId eq tenantId) and (AnimalsTable.deletedAt.isNull()) }
        species?.let { query = query.andWhere { AnimalsTable.species eq it } }
        status?.let { query = query.andWhere { AnimalsTable.status eq it } }
        
        val total = query.count()
        val results = query.orderBy(AnimalsTable.tagId).limit(perPage, offset = ((page - 1) * perPage).toLong()).map {
            Animal(it[AnimalsTable.id], it[AnimalsTable.tagId], it[AnimalsTable.species], it[AnimalsTable.status])
        }
        results to total
    }
    
    fun findById(id: UUID, tenantId: UUID): Animal? = transaction {
        AnimalsTable.selectAll().where { (AnimalsTable.id eq id) and (AnimalsTable.tenantId eq tenantId) }.singleOrNull()?.let {
            Animal(it[AnimalsTable.id], it[AnimalsTable.tagId], it[AnimalsTable.species], it[AnimalsTable.status])
        }
    }
    
    fun create(tenantId: UUID, tagId: String, species: String, sex: String, breed: String?, name: String?, dateOfBirth: String?, createdBy: UUID): Animal = transaction {
        val id = UUID.randomUUID()
        AnimalsTable.insert {
            it[AnimalsTable.id] = id
            it[AnimalsTable.tenantId] = tenantId
            it[AnimalsTable.tagId] = tagId
            it[AnimalsTable.species] = species
            it[AnimalsTable.sex] = sex
            it[AnimalsTable.breed] = breed
            it[AnimalsTable.name] = name
            it[AnimalsTable.dateOfBirth] = dateOfBirth?.let { java.time.LocalDate.parse(it) }
            it[AnimalsTable.status] = "ALIVE"
            it[AnimalsTable.createdBy] = createdBy
            it[AnimalsTable.createdAt] = Instant.now()
        }
        findById(id, tenantId)!!
    }
    
    fun recordVaccination(tenantId: UUID, animalId: UUID, vaccineName: String, dateAdministered: String, nextDueDate: String?, createdBy: UUID): Vaccination = transaction {
        val id = UUID.randomUUID()
        VaccinationsTable.insert {
            it[VaccinationsTable.id] = id
            it[VaccinationsTable.tenantId] = tenantId
            it[VaccinationsTable.animalId] = animalId
            it[VaccinationsTable.vaccineName] = vaccineName
            it[VaccinationsTable.dateAdministered] = java.time.LocalDate.parse(dateAdministered)
            it[VaccinationsTable.nextDueDate] = nextDueDate?.let { java.time.LocalDate.parse(it) }
            it[VaccinationsTable.createdBy] = createdBy
            it[VaccinationsTable.createdAt] = Instant.now()
        }
        Vaccination(id, animalId, vaccineName, dateAdministered, nextDueDate)
    }
    
    fun getVaccinations(animalId: UUID, tenantId: UUID): List<Vaccination> = transaction {
        VaccinationsTable.selectAll().where { (VaccinationsTable.animalId eq animalId) and (VaccinationsTable.tenantId eq tenantId) }.orderBy(VaccinationsTable.dateAdministered, SortOrder.DESC).map {
            Vaccination(it[VaccinationsTable.id], it[VaccinationsTable.animalId], it[VaccinationsTable.vaccineName], it[VaccinationsTable.dateAdministered].toString(), it[VaccinationsTable.nextDueDate]?.toString())
        }
    }
}
