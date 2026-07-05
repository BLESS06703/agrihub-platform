package com.agrihub.platform.common.constants

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormat {
    private val mwkFormat = NumberFormat.getNumberInstance(Locale("en", "MW"))
    
    fun formatMWK(amount: Double): String {
        return "MWK ${mwkFormat.format(amount)}"
    }
    
    fun formatShort(amount: Double): String {
        return when {
            amount >= 1_000_000_000_000 -> "${"%.1f".format(amount / 1_000_000_000_000)}T"
            amount >= 1_000_000_000 -> "${"%.1f".format(amount / 1_000_000_000)}B"
            amount >= 1_000_000 -> "${"%.1f".format(amount / 1_000_000)}M"
            amount >= 1_000 -> "${"%.1f".format(amount / 1_000)}K"
            else -> mwkFormat.format(amount)
        }
    }
    
    fun formatNumber(value: Long): String {
        return mwkFormat.format(value)
    }
}
