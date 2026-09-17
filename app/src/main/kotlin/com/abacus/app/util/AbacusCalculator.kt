package com.abacus.app.util

import com.abacus.app.model.AbacusState
import com.abacus.app.model.Operation
import com.abacus.app.model.RodState
import kotlin.math.pow

/**
 * Pure-logic utilities for computing abacus values and performing arithmetic.
 */
object AbacusCalculator {

    /**
     * Converts a list of [RodState] into the numeric value they represent.
     * Rod 0 = units (×1), Rod 1 = tens (×10), …, Rod 12 = trillions (×10^12).
     */
    fun computeValue(rods: List<RodState>): Long {
        var result = 0L
        for (rod in rods) {
            val placeValue = 10.0.pow(rod.index).toLong()
            result += rod.value.toLong() * placeValue
        }
        return result
    }

    /**
     * Converts a [Long] value back to a list of [RodState] objects so the board
     * can be animated to show that value.  Values outside [0, 9_999_999_999_999]
     * are clamped.
     */
    fun valueToRodStates(
        value: Long,
        numRods: Int = AbacusState.NUM_RODS
    ): List<RodState> {
        var remaining = value.coerceIn(0L, 9_999_999_999_999L)
        return List(numRods) { i ->
            val digit      = (remaining % 10).toInt()
            remaining     /= 10
            val heavenActive = digit >= 5
            val earthCount   = if (heavenActive) digit - 5 else digit
            RodState(i, heavenActive, earthCount)
        }
    }

    /**
     * Performs the arithmetic operation and returns a [Double] result
     * (supports fractional division results).  Returns [Double.NaN] for
     * division by zero.
     */
    fun calculate(first: Long, operation: Operation, second: Long): Double =
        when (operation) {
            Operation.ADD      -> (first + second).toDouble()
            Operation.SUBTRACT -> (first - second).toDouble()
            Operation.MULTIPLY -> (first * second).toDouble()
            Operation.DIVIDE   -> if (second != 0L) first.toDouble() / second.toDouble()
                                  else Double.NaN
        }
}
