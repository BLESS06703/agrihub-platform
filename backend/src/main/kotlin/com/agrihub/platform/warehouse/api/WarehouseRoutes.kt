package com.agrihub.platform.warehouse.api
import com.agrihub.platform.warehouse.repository.WarehouseRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.warehouseRoutes(repo: WarehouseRepository) {
    route("/v1/warehouse") {
        get { call.respond(mapOf("success" to true, "data" to repo.findAll(UUID.randomUUID()))) }
        post {
            val b = call.receive<Map<String, String>>()
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to repo.create(UUID.randomUUID(), b["name"]!!, b["type"]!!, b["capacity"]?.toDoubleOrNull(), UUID.randomUUID())))
        }
        post("/intake") {
            val b = call.receive<Map<String, String>>()
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to repo.recordIntake(UUID.randomUUID(), UUID.fromString(b["warehouseId"]), UUID.fromString(b["cropId"]), b["quantityKg"]!!.toDouble(), b["sourceName"], UUID.randomUUID())))
        }
    }
}
