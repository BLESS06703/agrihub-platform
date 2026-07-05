package com.agrihub.platform.integrations.ussd

class UssdService {
    data class UssdRequest(val sessionId: String, val phoneNumber: String, val text: String)
    data class UssdResponse(val message: String, val shouldContinue: Boolean)
    
    fun handle(request: UssdRequest): UssdResponse {
        return when {
            request.text.isEmpty() -> UssdResponse(
                "Welcome to AgriHub\n1. Check Prices\n2. Weather\n3. My Farm\n4. Help",
                true
            )
            request.text == "1" -> UssdResponse(
                "Maize: MWK 800/kg\nSoybeans: MWK 1200/kg\nGroundnuts: MWK 1500/kg\n0. Back",
                true
            )
            else -> UssdResponse("Thank you for using AgriHub. Goodbye.", false)
        }
    }
}
