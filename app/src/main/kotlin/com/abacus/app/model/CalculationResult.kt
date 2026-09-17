package com.abacus.app.model

/** The four arithmetic operations supported by the calculator. */
enum class Operation(val symbol: String) {
    ADD("+"),
    SUBTRACT("−"),
    MULTIPLY("×"),
    DIVIDE("÷")
}

/**
 * Immutable record of a completed calculation.
 */
data class CalculationResult(
    val firstValue:  Long,
    val operation:   Operation,
    val secondValue: Long,
    val result:      Double
) {
    /** Human-readable expression, e.g. "42 + 58 = 100". */
    val expression: String
        get() {
            val resultStr = if (result == result.toLong().toDouble()) {
                result.toLong().toString()
            } else {
                String.format("%.4f", result)
            }
            return "$firstValue ${operation.symbol} $secondValue = $resultStr"
        }
}
