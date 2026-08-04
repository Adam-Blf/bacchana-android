package com.beloucif.meskova.core

import java.util.UUID

/**
 * Genre declare par un joueur. Optionnel et 100% local (jamais envoye en analytics -
 * voir com.beloucif.meskova.analytics). Sert uniquement a cibler du contenu
 * (Targets.GENDER_M / Targets.GENDER_F, voir targeting.kt). Mirrors PlayerGender
 * from src/types/index.ts.
 */
enum class Gender { M, F, X }

/**
 * Statut relationnel declare par un joueur. Optionnel et 100% local (jamais envoye
 * en analytics). Sert uniquement a cibler du contenu (Targets.SINGLE / Targets.COUPLE).
 * Mirrors PlayerRelationship from src/types/index.ts.
 */
enum class Relationship { SINGLE, COUPLE }

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
    /**
     * Genre declare, optionnel et 100% local (jamais envoye en analytics). Les deux
     * champs restent `null` ("non precise") tant que le joueur n'a rien choisi.
     */
    val gender: Gender? = null,
    /** Statut relationnel declare, optionnel et 100% local (jamais envoye en analytics). */
    val relationship: Relationship? = null,
)

/** Creates a player with a unique id and zeroed stats. Name is trimmed. */
fun createPlayer(name: String): Player = Player(
    id = "player-${System.currentTimeMillis()}-${UUID.randomUUID().toString().take(9)}",
    name = name.trim(),
)
