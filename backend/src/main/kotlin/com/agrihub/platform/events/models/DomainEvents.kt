package com.agrihub.platform.events.models

import java.time.Instant
import java.util.UUID

sealed class DomainEvent(
    open val eventId: UUID = UUID.randomUUID(),
    open val tenantId: UUID,
    open val userId: UUID,
    open val timestamp: Instant = Instant.now()
)

data class HarvestRecordedEvent(
    override val tenantId: UUID,
    override val userId: UUID,
    val harvestId: UUID,
    val fieldId: UUID,
    val cropId: UUID,
    val quantityKg: Double,
    val qualityGrade: String
) : DomainEvent(tenantId = tenantId, userId = userId)

data class CropPlantedEvent(
    override val tenantId: UUID,
    override val userId: UUID,
    val plantingId: UUID,
    val fieldId: UUID,
    val cropId: UUID,
    val plantingDate: String
) : DomainEvent(tenantId = tenantId, userId = userId)

data class AnimalVaccinatedEvent(
    override val tenantId: UUID,
    override val userId: UUID,
    val animalId: UUID,
    val vaccineName: String,
    val vaccinationDate: String,
    val nextDueDate: String?
) : DomainEvent(tenantId = tenantId, userId = userId)

data class InventoryUpdatedEvent(
    override val tenantId: UUID,
    override val userId: UUID,
    val itemId: UUID,
    val direction: String,
    val quantity: Double,
    val remainingStock: Double
) : DomainEvent(tenantId = tenantId, userId = userId)

data class PaymentCompletedEvent(
    override val tenantId: UUID,
    override val userId: UUID,
    val paymentId: UUID,
    val amount: Double,
    val currency: String,
    val paymentMethod: String
) : DomainEvent(tenantId = tenantId, userId = userId)

data class TenantCreatedEvent(
    override val tenantId: UUID,
    override val userId: UUID,
    val tenantName: String,
    val tenantType: String
) : DomainEvent(tenantId = tenantId, userId = userId)

data class AIInsightGeneratedEvent(
    override val tenantId: UUID,
    override val userId: UUID,
    val insightType: String,
    val message: String,
    val confidence: Double
) : DomainEvent(tenantId = tenantId, userId = userId)
