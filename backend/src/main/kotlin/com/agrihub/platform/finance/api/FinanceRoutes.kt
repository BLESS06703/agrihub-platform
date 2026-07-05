package com.agrihub.platform.finance.api
import com.agrihub.platform.finance.repository.FinanceRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.financeRoutes(repo: FinanceRepository) {
    route("/v1/finance") {
        post("/income") { val b = call.receive<Map<String, String>>(); call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to repo.recordIncome(UUID.randomUUID(), b["amount"]!!.toDouble(), b["source"]!!, b["date"]!!, b["method"]!!, UUID.randomUUID()))) }
        post("/expenses") { val b = call.receive<Map<String, String>>(); call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to repo.recordExpense(UUID.randomUUID(), b["amount"]!!.toDouble(), b["category"]!!, b["date"]!!, b["method"]!!, UUID.randomUUID()))) }
        get("/income") { call.respond(mapOf("success" to true, "data" to repo.getIncome(UUID.randomUUID()))) }
        get("/expenses") { call.respond(mapOf("success" to true, "data" to repo.getExpenses(UUID.randomUUID()))) }
        get("/profit-loss") { call.respond(mapOf("success" to true, "data" to repo.getProfitLoss(UUID.randomUUID()))) }
    }
}
