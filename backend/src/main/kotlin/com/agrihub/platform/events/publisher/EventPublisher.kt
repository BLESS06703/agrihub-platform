package com.agrihub.platform.events.publisher

import com.agrihub.platform.events.models.DomainEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.slf4j.LoggerFactory

object EventPublisher {
    private val logger = LoggerFactory.getLogger(EventPublisher::class.java)
    private val _events = MutableSharedFlow<DomainEvent>(replay = 0, extraBufferCapacity = 100)
    val events = _events.asSharedFlow()
    
    suspend fun publish(event: DomainEvent) {
        logger.debug("Publishing event: ${event::class.simpleName} tenant=${event.tenantId}")
        _events.emit(event)
    }
    
    suspend fun publishBatch(events: List<DomainEvent>) {
        events.forEach { publish(it) }
    }
}
