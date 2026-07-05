package com.agrihub.platform.agriculture.farms.api

import com.agrihub.platform.agriculture.farms.repository.FarmRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.farmRoutes(farmRepository: FarmRepository) {
    
    route("/v1/farms") {
        
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val perPage = call.request.queryParameters["per_page"]?.toIntOrNull() ?: 20
            val tenantId = UUID.randomUUID()
            
            val (farms, total) = farmRepository.findAll(tenantId, page, perPage)
            call.respond(mapOf(
                "success" to true,
                "data" to farms,
                "meta" to mapOf("page" to page, "perPage" to perPage, "total" to total)
            ))
        }
        
        post {
            val body = call.receive<Map<String, String>>()
            val tenantId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            
            val farm = farmRepository.create(
                tenantId = tenantId,
                name = body["name"] ?: return@post,
                areaHectares = body["areaHectares"]?.toDoubleOrNull(),
                district = body["district"],
                village = body["village"],
                createdBy = userId
            )
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to farm))
        }
        
        get("/{id}") {
            val farmId = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@get
            val tenantId = UUID.randomUUID()
            
            val farm = farmRepository.findById(farmId, tenantId)
            if (farm != null) {
                val fields = farmRepository.getFields(farmId, tenantId)
                call.respond(mapOf("success" to true, "data" to mapOf("farm" to farm, "fields" to fields)))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("success" to false))
            }
        }
        
        put("/{id}") {
            val farmId = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@put
            val tenantId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val body = call.receive<Map<String, String>>()
            
            val farm = farmRepository.update(
                id = farmId, tenantId = tenantId,
                name = body["name"],
                areaHectares = body["areaHectares"]?.toDoubleOrNull(),
                soilType = body["soilType"],
                updatedBy = userId
            )
            if (farm != null) call.respond(mapOf("success" to true, "data" to farm))
            else call.respond(HttpStatusCode.NotFound, mapOf("success" to false))
        }
        
        get("/{id}/fields") {
            val farmId = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@get
            val tenantId = UUID.randomUUID()
            val fields = farmRepository.getFields(farmId, tenantId)
            call.respond(mapOf("success" to true, "data" to fields))
        }
        
        post("/{id}/fields") {
            val farmId = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@post
            val tenantId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val body = call.receive<Map<String, String>>()
            
            val field = farmRepository.addField(
                farmId = farmId, tenantId = tenantId,
                fieldName = body["fieldName"] ?: return@post,
                areaHectares = body["areaHectares"]?.toDoubleOrNull(),
                createdBy = userId
            )
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to field))
        }
    }
}
