package com.beloucif.lataverne.core

/** Card suit. Order mirrors the web reference (borderland.ts). */
enum class Suit {
    CLUBS,
    DIAMONDS,
    HEARTS,
    SPADES,
}

/** Card rank from Ace to King. */
enum class Rank(val value: Int, val label: String) {
    ACE(1, "A"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(11, "J"),
    QUEEN(12, "Q"),
    KING(13, "K"),
}

/**
 * Penalty unit. Internal identifier only, never shown verbatim to the player.
 * [STANDARD] = regular penalty, [MAJOR] = Ace-triggered major penalty.
 */
enum class PenaltyUnit {
    STANDARD,
    MAJOR,
}

/** A single playing card. Mirrors `Card` from src/types/index.ts. */
data class Card(
    val id: String,
    val suit: Suit,
    val rank: Rank,
    val value: Int,
    val unit: PenaltyUnit,
)
