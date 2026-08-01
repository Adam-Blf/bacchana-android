package com.beloucif.blackout.core

/**
 * Creates a standard 52-card deck.
 * CRITICAL RULE: Ace cards carry [PenaltyUnit.MAJOR], every other rank carries
 * [PenaltyUnit.STANDARD]. This mirrors createDeck() in the web reference
 * (src/core/borderland.ts) exactly - do not change without updating both sides.
 */
fun createDeck(): List<Card> = Suit.entries.flatMap { suit ->
    Rank.entries.map { rank ->
        Card(
            id = "${suit.name.lowercase()}-${rank.label}",
            suit = suit,
            rank = rank,
            value = rank.value,
            unit = if (rank == Rank.ACE) PenaltyUnit.MAJOR else PenaltyUnit.STANDARD,
        )
    }
}

/**
 * Fisher-Yates shuffle. Returns a new list, never mutates [deck].
 * Accepts an injectable [random] source for deterministic tests.
 */
fun shuffleDeck(deck: List<Card>, random: kotlin.random.Random = kotlin.random.Random): List<Card> {
    val shuffled = deck.toMutableList()
    for (i in shuffled.size - 1 downTo 1) {
        val j = random.nextInt(i + 1)
        val tmp = shuffled[i]
        shuffled[i] = shuffled[j]
        shuffled[j] = tmp
    }
    return shuffled
}
