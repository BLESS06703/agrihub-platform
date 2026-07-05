package com.agrihub.platform.livestock.animals.api

import com.agrihub.platform.livestock.animals.repository.AnimalRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.animalRoutes(animalRepository: AnimalRepository) {
    
    route("/v1/livestock/animals") {
        
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val perPage = call.request.queryParameters["per_page"]?.toIntOrNull() ?: 20
            val species = call.request.queryParameters["species"]
            val status = call.request.queryParameters["status"]
            val tenantId = UUID.randomUUID()
            
            val (animals, total) = animalRepository.findAll(tenantId, species, status, page, perPage)
            call.respond(mapOf("success" to true, "data" to animals, "meta" to mapOf("page" to page, "perPage" to perPage, "total" to total)))
        }
        
        post {
            val body = call.receive<Map<String, String>>()
            val tenantId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            
            val animal = animalRepository.create(tenantId, body["tagId"] ?: return@post, body["species"] ?: return@post, body["sex"] ?: return@post, body["breed"], body["name"], body["dateOfBirth"], userId)
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to animal))
        }
        
        get("/{id}") {
            val id = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@get
            val animal = animalRepository.findById(id, UUID.randomUUID())
            if (animal != null) call.respond(mapOf("success" to true, "data" to animal))
            else call.respond(HttpStatusCode.NotFound, mapOf("success" to false))
        }
        
        post("/{id}/vaccinations") {
            val animalId = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@post
            val body = call.receive<Map<String, String>>()
            val vaccination = animalRepository.recordVaccination(UUID.randomUUID(), animalId, body["vaccineName"] ?: return@post, body["dateAdministered"] ?: return@post, body["nextDueDate"], UUID.randomUUID())
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to vaccination))
        }
        
        get("/{id}/vaccinations") {
            val animalId = call.parameters["id"]?.let { UUID.fromString(it) } ?: return@get
            val vaccinations = animalRepository.getVaccinations(animalId, UUID.randomUUID())
            call.respond(mapOf("success" to true, "data" to vaccinations))
        }
    }
}
