package com.agrihub.platform.integrations.api

import com.agrihub.platform.integrations.mobilemoney.MobileMoneyService
import com.agrihub.platform.integrations.weather.WeatherService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.integrationRoutes(moneyService: MobileMoneyService, weatherService: WeatherService) {
    route("/v1/integrations") {
        
        post("/payments/request") {
            val body = call.receive<Map<String, String>>()
            val result = moneyService.requestPayment(MobileMoneyService.PaymentRequest(
                body["phone"] ?: return@post, body["amount"]?.toDoubleOrNull() ?: return@post,
                body["reference"] ?: "", body["provider"] ?: "AIRTEL_MONEY"
            ))
            call.respond(if (result.success) HttpStatusCode.Created else HttpStatusCode.BadRequest, mapOf("success" to result.success, "data" to result))
        }
        
        get("/payments/{id}") {
            val id = call.parameters["id"] ?: return@get
            val result = moneyService.checkStatus(id, call.request.queryParameters["provider"] ?: "AIRTEL_MONEY")
            call.respond(mapOf("success" to true, "data" to result))
        }
        
        get("/weather/current") {
            val lat = call.request.queryParameters["lat"]?.toDoubleOrNull() ?: -15.0
            val lon = call.request.queryParameters["lon"]?.toDoubleOrNull() ?: 35.0
            call.respond(mapOf("success" to true, "data" to weatherService.getCurrentWeather(lat, lon)))
        }
        
        get("/weather/forecast") {
            val lat = call.request.queryParameters["lat"]?.toDoubleOrNull() ?: -15.0
            val lon = call.request.queryParameters["lon"]?.toDoubleOrNull() ?: 35.0
            val days = call.request.queryParameters["days"]?.toIntOrNull() ?: 7
            call.respond(mapOf("success" to true, "data" to weatherService.getForecast(lat, lon, days)))
        }
    }
}
