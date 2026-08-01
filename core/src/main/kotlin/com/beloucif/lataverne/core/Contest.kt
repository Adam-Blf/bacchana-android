package com.beloucif.lataverne.core

/** Contest/duel escalation level, 0 (none) to 3 (max escalation). */
enum class ContestLevel(val level: Int) {
    NONE(0),
    FIRST(1),
    SECOND(2),
    THIRD(3),
    ;

    companion object {
        fun next(current: ContestLevel): ContestLevel? = when (current) {
            NONE -> FIRST
            FIRST -> SECOND
            SECOND -> THIRD
            THIRD -> null
        }
    }
}

/**
 * Contest multipliers by level. CRITICAL: must stay {0:1, 1:1, 2:2, 3:4},
 * mirrors CONTEST_MULTIPLIERS from src/types/index.ts.
 */
val CONTEST_MULTIPLIERS: Map<ContestLevel, Int> = mapOf(
    ContestLevel.NONE to 1,
    ContestLevel.FIRST to 1,
    ContestLevel.SECOND to 2,
    ContestLevel.THIRD to 4,
)

/** Current state of a contest/duel. */
data class ContestState(
    val active: Boolean = false,
    val level: ContestLevel = ContestLevel.NONE,
    val baseCard: Card? = null,
    val challenger: Player? = null,
) {
    companion object {
        val INITIAL = ContestState()
    }
}

/** Result of a penalty calculation. Display text is store-safe (no alcohol wording). */
data class PenaltyResult(
    val amount: Int,
    val unit: PenaltyUnit,
    val displayText: String,
)

/**
 * Calculates the final penalty for a contest resolution.
 * Unit never changes - only the amount is multiplied by the contest level.
 * Display wording is store-safe: the app hands out abstract penalties,
 * the group decides in real life what a penalty is worth.
 */
fun calculatePenalty(baseAmount: Int, level: ContestLevel, unit: PenaltyUnit): PenaltyResult {
    val multiplier = CONTEST_MULTIPLIERS.getValue(level)
    val amount = baseAmount * multiplier

    val displayText = when (unit) {
        PenaltyUnit.MAJOR -> if (amount > 1) "PÉNALITÉ MAJEURE x$amount" else "PÉNALITÉ MAJEURE"
        PenaltyUnit.STANDARD -> "$amount pénalité${if (amount > 1) "s" else ""}"
    }

    return PenaltyResult(amount, unit, displayText)
}

/**
 * Gets the next active player index, circular, skipping inactive players.
 * Returns 0 when nobody is active. Mirrors getNextPlayerIndex() from the web reference.
 */
fun getNextPlayerIndex(currentIndex: Int, players: List<Player>): Int {
    val activeCount = players.count { it.active }
    if (activeCount == 0 || players.isEmpty()) return 0

    var nextIndex = (currentIndex + 1) % players.size
    while (!players[nextIndex].active && nextIndex != currentIndex) {
        nextIndex = (nextIndex + 1) % players.size
    }
    return nextIndex
}
