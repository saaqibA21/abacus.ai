package com.abacus.app.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Utility for formatting numbers as comma-separated strings and English words.
 */
object NumberFormatter {

    private val commaFormatter = NumberFormat.getNumberInstance(Locale.US)

    /** Formats a number with commas, e.g. 1234567 → "1,234,567". */
    fun formatWithCommas(value: Long): String = commaFormatter.format(value)

    /** Converts a number to English words, e.g. 1001 → "One Thousand One". */
    fun toWords(number: Long): String {
        if (number == 0L) return "Zero"
        if (number < 0L)  return "Negative ${toWords(-number)}"

        val billions  = number / 1_000_000_000L
        val millions  = (number % 1_000_000_000L) / 1_000_000L
        val thousands = (number % 1_000_000L)      / 1_000L
        val remainder = number % 1_000L

        return buildList {
            if (billions  > 0) add("${threeDigits(billions)} Billion")
            if (millions  > 0) add("${threeDigits(millions)} Million")
            if (thousands > 0) add("${threeDigits(thousands)} Thousand")
            if (remainder > 0) add(threeDigits(remainder))
        }.joinToString(" ")
    }

    private fun threeDigits(n: Long): String {
        val hundreds    = n / 100
        val tensAndOnes = n % 100
        return buildList {
            if (hundreds > 0)    add("${ONES[hundreds.toInt()]} Hundred")
            if (tensAndOnes > 0) add(
                if (tensAndOnes < 20) ONES[tensAndOnes.toInt()]
                else {
                    val t = TENS[(tensAndOnes / 10).toInt()]
                    val o = tensAndOnes % 10
                    if (o == 0L) t else "$t ${ONES[o.toInt()]}"
                }
            )
        }.joinToString(" ")
    }

    private val ONES = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
        "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    )

    private val TENS = arrayOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty",
        "Sixty", "Seventy", "Eighty", "Ninety"
    )
}
