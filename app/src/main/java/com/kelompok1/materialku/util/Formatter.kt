package com.kelompok1.materialku.util

import java.text.NumberFormat
import java.util.Locale

object Formatter {

    private val idLocale: Locale = Locale.forLanguageTag("in-ID")
    private val currencyFormat: NumberFormat = NumberFormat.getInstance(idLocale).apply {
        maximumFractionDigits = 0
    }

    fun rupiah(amount: Double): String = "Rp ${currencyFormat.format(amount)}"

    fun rupiah(amount: Long): String = "Rp ${currencyFormat.format(amount)}"

    fun number(value: Int): String = currencyFormat.format(value)
}
