package com.agrihub.platform.events.subscriber

import com.agrihub.platform.events.models.*
import com.agrihub.platform.events.publisher.EventPublisher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class EventSubscriber {
    private val logger = LoggerFactory.getLogger(EventSubscriber::class.java)
    private val scope = CoroutineScope(Dispatchers.Default)
    
    fun start() {
        scope.launch {
            EventPublisher.events.collect { event ->
                handleEvent(event)
            }
        }
    }
    
    private suspend fun handleEvent(event: DomainEvent) {
        when (event) {
            is HarvestRecordedEvent -> onHarvestRecorded(event)
            is CropPlantedEvent -> onCropPlanted(event)
            is AnimalVaccinatedEvent -> onAnimalVaccinated(event)
            is InventoryUpdatedEvent -> onInventoryUpdated(event)
            is PaymentCompletedEvent -> onPaymentCompleted(event)
            is TenantCreatedEvent -> onTenantCreated(event)
            is AIInsightGeneratedEvent -> onAIInsightGenerated(event)
        }
    }
    
    private fun onHarvestRecorded(event: HarvestRecordedEvent) {
        logger.info("Harvest recorded: farm={}, qty={}kg", event.fieldId, event.quantityKg)
    }
    
    private fun onCropPlanted(event: CropPlantedEvent) {
        logger.info("Crop planted: field={}, crop={}", event.fieldId, event.cropId)
    }
    
    private fun onAnimalVaccinated(event: AnimalVaccinatedEvent) {
        logger.info("Animal vaccinated: animal={}, vaccine={}", event.animalId, event.vaccineName)
    }
    
    private fun onInventoryUpdated(event: InventoryUpdatedEvent) {
        logger.info("Inventory updated: item={}, direction={}, qty={}", event.itemId, event.direction, event.quantity)
    }
    
    private fun onPaymentCompleted(event: PaymentCompletedEvent) {
        logger.info("Payment completed: amount={} {}", event.amount, event.currency)
    }
    
    private fun onTenantCreated(event: TenantCreatedEvent) {
        logger.info("Tenant created: name={}, type={}", event.tenantName, event.tenantType)
    }
    
    private fun onAIInsightGenerated(event: AIInsightGeneratedEvent) {
        logger.info("AI insight: type={}, confidence={}", event.insightType, event.confidence)
    }
}
