package com.abacus.app.ui.abacus

import androidx.lifecycle.ViewModel
import com.abacus.app.model.AbacusState
import com.abacus.app.model.CalculationResult
import com.abacus.app.model.Operation
import com.abacus.app.model.RodState
import com.abacus.app.util.AbacusCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Full UI state for the abacus screen. */
data class AbacusUiState(
    val rods:              List<RodState>         = List(AbacusState.NUM_RODS) { RodState(it) },
    val currentValue:      Long                   = 0L,
    val savedValue:        Long?                  = null,
    val selectedOperation: Operation?             = null,
    val history:           List<CalculationResult> = emptyList(),
    val resultMessage:     String?                = null
)

class AbacusViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AbacusUiState())
    val uiState: StateFlow<AbacusUiState> = _uiState.asStateFlow()

    // ── Bead interaction ───────────────────────────────────────────────────────

    /** Toggle the single heaven bead on a rod (slide toward / away from bar). */
    fun toggleHeavenBead(rodIndex: Int) = updateRods { rods ->
        rods[rodIndex] = rods[rodIndex].copy(heavenActive = !rods[rodIndex].heavenActive)
    }

    /**
     * Toggle earth beads on a rod using cumulative Soroban rules.
     *
     * [displayBeadIndex] 0 = closest to divider bar, 3 = farthest from bar.
     * Active beads are the ones at display indices 0 … earthCount-1.
     *
     * • Tapping an **active** bead at index i  → earthCount becomes i   (retract this + inner ones)
     * • Tapping an **inactive** bead at index i → earthCount becomes i+1 (push up to & including this)
     */
    fun toggleEarthBead(rodIndex: Int, displayBeadIndex: Int) = updateRods { rods ->
        val rod = rods[rodIndex]
        val newCount = if (displayBeadIndex < rod.earthCount) displayBeadIndex
                       else displayBeadIndex + 1
        rods[rodIndex] = rod.copy(
            earthCount = newCount.coerceIn(0, AbacusState.EARTH_BEADS)
        )
    }

    // ── Calculator actions ─────────────────────────────────────────────────────

    /** Save the current board value as the first operand. */
    fun saveFirstNumber() {
        _uiState.update { it.copy(savedValue = it.currentValue, resultMessage = null) }
    }

    /** Choose an arithmetic operation. */
    fun selectOperation(operation: Operation) {
        _uiState.update { it.copy(selectedOperation = operation) }
    }

    /**
     * Perform the calculation: first (saved) op second (current board).
     * If the result fits in the abacus range it is also animated onto the board.
     */
    fun calculate() {
        _uiState.update { state ->
            val saved  = state.savedValue        ?: return@update state
            val op     = state.selectedOperation ?: return@update state
            val second = state.currentValue
            val result = AbacusCalculator.calculate(saved, op, second)
            val record = CalculationResult(saved, op, second, result)

            // Show result on board if it is a whole non-negative number in range
            val resultLong = result.toLong()
            val canShow    = !result.isNaN() &&
                             result == resultLong.toDouble() &&
                             resultLong in 0L..9_999_999_999_999L
            val newRods    = if (canShow) AbacusCalculator.valueToRodStates(resultLong)
                             else state.rods

            state.copy(
                rods              = newRods,
                currentValue      = if (canShow) resultLong else state.currentValue,
                history           = listOf(record) + state.history.take(9),
                resultMessage     = record.expression,
                savedValue        = null,
                selectedOperation = null
            )
        }
    }

    /** Clear all beads and reset the calculator state. */
    fun reset() {
        _uiState.value = AbacusUiState()
    }

    /**
     * Set a specific [value] onto the abacus board (used by the Learn screen
     * to show hints).
     */
    fun setValueOnBoard(value: Long) = updateRods { rods ->
        val target = AbacusCalculator.valueToRodStates(value)
        target.forEachIndexed { i, rod -> rods[i] = rod }
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /** Apply a mutation to the rod list and recompute the current value. */
    private inline fun updateRods(block: (MutableList<RodState>) -> Unit) {
        _uiState.update { state ->
            val newRods = state.rods.toMutableList()
            block(newRods)
            state.copy(
                rods          = newRods,
                currentValue  = AbacusCalculator.computeValue(newRods),
                resultMessage = null
            )
        }
    }
}
