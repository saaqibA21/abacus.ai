package com.abacus.app.model

/**
 * State of a single rod on the Soroban (Japanese) abacus.
 *
 * @param index       Rod position index. 0 = units place, 1 = tens, 2 = hundreds, etc.
 * @param heavenActive  Whether the single heaven bead has been slid DOWN toward the bar (counted).
 * @param earthCount  How many earth beads have been slid UP toward the bar (0–4).
 */
data class RodState(
    val index: Int,
    val heavenActive: Boolean = false,
    val earthCount: Int = 0
) {
    /** The numeric digit this rod currently represents (0–9). */
    val value: Int get() = (if (heavenActive) 5 else 0) + earthCount
}

/**
 * Full abacus board state: a list of [RodState] for each rod.
 */
data class AbacusState(
    val rods: List<RodState> = List(NUM_RODS) { RodState(it) }
) {
    companion object {
        const val NUM_RODS    = 13  // Supports up to 9,999,999,999,999
        const val EARTH_BEADS = 4   // Soroban: 4 earth beads per rod
    }
}
