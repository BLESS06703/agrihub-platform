package com.agrihub.platform.config.platform

data class CountryConfig(
    val countryCode: String,
    val countryName: String,
    val currency: String,
    val currencySymbol: String,
    val languages: List<String>,
    val defaultLanguage: String,
    val timezone: String,
    val crops: List<String>,
    val weatherProvider: String,
    val mobileMoneyProviders: List<String>,
    val measurementSystem: String
)

object PlatformRegistry {
    val countries = mapOf(
        "MW" to CountryConfig(
            countryCode = "MW",
            countryName = "Malawi",
            currency = "MWK",
            currencySymbol = "MK",
            languages = listOf("en", "ny"),
            defaultLanguage = "en",
            timezone = "Africa/Blantyre",
            crops = listOf("Maize", "Tobacco", "Tea", "Sugarcane", "Groundnuts", "Cotton", "Beans", "Cassava"),
            weatherProvider = "openweathermap",
            mobileMoneyProviders = listOf("airtel_money", "tnm_mpamba"),
            measurementSystem = "METRIC"
        ),
        "ZM" to CountryConfig(
            countryCode = "ZM",
            countryName = "Zambia",
            currency = "ZMW",
            currencySymbol = "ZK",
            languages = listOf("en", "bem", "ny"),
            defaultLanguage = "en",
            timezone = "Africa/Lusaka",
            crops = listOf("Maize", "Soybeans", "Wheat", "Cotton", "Groundnuts", "Tobacco"),
            weatherProvider = "openweathermap",
            mobileMoneyProviders = listOf("mtn_money", "airtel_money", "zamtel_kwacha"),
            measurementSystem = "METRIC"
        ),
        "TZ" to CountryConfig(
            countryCode = "TZ",
            countryName = "Tanzania",
            currency = "TZS",
            currencySymbol = "TSh",
            languages = listOf("sw", "en"),
            defaultLanguage = "sw",
            timezone = "Africa/Dar_es_Salaam",
            crops = listOf("Maize", "Rice", "Coffee", "Tea", "Cotton", "Cashew", "Tobacco", "Sisal"),
            weatherProvider = "openweathermap",
            mobileMoneyProviders = listOf("mpesa", "tigo_pesa", "airtel_money"),
            measurementSystem = "METRIC"
        )
    )
    
    fun getConfig(countryCode: String): CountryConfig {
        return countries[countryCode] ?: countries["MW"]!!
    }
}
