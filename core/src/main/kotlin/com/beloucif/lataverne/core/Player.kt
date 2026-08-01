package com.beloucif.lataverne.core

import java.util.UUID

/** A player in the game. Mirrors `Player` from src/types/index.ts. */
data class Player(
    val id: String,
    val name: String,
    val active: Boolean = true,
    val penaltiesStandard: Int = 0,
    val penaltiesMajor: Int = 0,
    val contestsWon: Int = 0,
    val contestsLost: Int = 0,
    val cardsDrawn: Int = 0,
)

/** Creates a player with a unique id and zeroed stats. Name is trimmed. */
fun createPlayer(name: String): Player = Player(
    id = "player-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(9)}",
    name = name.trim(),
)
