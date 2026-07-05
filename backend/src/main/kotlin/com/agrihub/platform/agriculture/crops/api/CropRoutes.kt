package com.agrihub.platform.agriculture.crops.api

import com.agrihub.platform.agriculture.crops.repository.CropRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.cropRoutes(cropRepository: CropRepository) {
    
    route("/v1/crops") {
        get("/catalog") {
            val category = call.request.queryParameters["category"]
            val crops = cropRepository.getCropCatalog(category)
            call.respond(mapOf("success" to true, "data" to crops, "meta" to mapOf("total" to crops.size)))
        }
        
        get("/catalog/{id}") {
            val id = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@get
            val crop = cropRepository.getCrop(id)
            if (crop != null) call.respond(mapOf("success" to true, "data" to crop))
            else call.respond(HttpStatusCode.NotFound, mapOf("success" to false))
        }
    }
    
    route("/v1/farms/{farmId}/fields/{fieldId}") {
        post("/plantings") {
            val fieldId = call.parameters["fieldId"]?.let { UUID.fromString(it) } ?: return@post
            val farmId = call.parameters["farmId"]?.let { UUID.fromString(it) } ?: return@post
            val tenantId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val body = call.receive<Map<String, String>>()
            
            val planting = cropRepository.recordPlanting(tenantId, farmId, fieldId, UUID.fromString(body["cropId"]), body["plantingDate"] ?: return@post, userId)
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to planting))
        }
        
        get("/plantings") {
            val fieldId = call.parameters["fieldId"]?.let { UUID.fromString(it) } ?: return@get
            val plantings = cropRepository.getFieldPlantings(fieldId, UUID.randomUUID())
            call.respond(mapOf("success" to true, "data" to plantings))
        }
        
        post("/harvests") {
            val fieldId = call.parameters["fieldId"]?.let { UUID.fromString(it) } ?: return@post
            val tenantId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val body = call.receive<Map<String, String>>()
            
            val harvest = cropRepository.recordHarvest(tenantId, UUID.fromString(body["plantingId"]), fieldId, body["harvestDate"] ?: return@post, body["quantityKg"]?.toDoubleOrNull() ?: return@post, body["qualityGrade"] ?: "GRADE_A", userId)
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to harvest))
        }
        
        get("/harvests") {
            val fieldId = call.parameters["fieldId"]?.let { UUID.fromString(it) } ?: return@get
            val harvests = cropRepository.getFieldHarvests(fieldId, UUID.randomUUID())
            call.respond(mapOf("success" to true, "data" to harvests))
        }
    }
}
