package com.agrihub.platform.banking.api

import com.agrihub.platform.banking.loans.BankingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.bankingRoutes(bankingService: BankingService) {
    route("/v1/banking") {
        get("/credit-score") { call.respond(mapOf("success" to true, "data" to bankingService.getCreditScore(UUID.randomUUID()))) }
        get("/loan-offers") {
            val amount = call.request.queryParameters["amount"]?.toDoubleOrNull() ?: 1000000.0
            call.respond(mapOf("success" to true, "data" to bankingService.getLoanOffers(UUID.randomUUID(), amount)))
        }
        post("/loans/apply") {
            val b = call.receive<Map<String, String>>()
            call.respond(HttpStatusCode.Created, mapOf("success" to true, "data" to bankingService.applyForLoan(UUID.randomUUID(), b["amount"]!!.toDouble(), b["purpose"]!!, b["termMonths"]!!.toInt())))
        }
        get("/loans/{id}") { call.respond(mapOf("success" to true, "data" to bankingService.getLoanStatus(UUID.fromString(call.parameters["id"]!!)))) }
        post("/accounts/link") {
            val b = call.receive<Map<String, String>>()
            call.respond(mapOf("success" to true, "data" to bankingService.linkBankAccount(UUID.randomUUID(), b["bankName"]!!, b["accountNumber"]!!)))
        }
    }
}
