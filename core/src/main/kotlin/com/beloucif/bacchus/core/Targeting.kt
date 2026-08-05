package com.beloucif.bacchus.core

import kotlin.random.Random

/**
 * Resolves a `PackItem.targets` value to concrete player(s) at the table. Mirrors
 * src/core/engine/targeting.ts on the web, minus the custom PRNG - Kotlin's
 * `kotlin.random.Random(seed)` already gives a deterministic, injectable generator.
 */

/** Targets this module knows how to resolve to concrete player(s). */
val RESOLVABLE_TARGETS: Set<Targets> = setOf(
    Targets.GENDER_M,
    Targets.GENDER_F,
    Targets.PAIR,
    Targets.SINGLE,
    Targets.COUPLE,
)

/** True when a `targets` value is one this module resolves to a concrete player. */
fun isResolvableTarget(target: Targets?): Boolean = target != null && target in RESOLVABLE_TARGETS

/**
 * Builds a deterministic RNG from a string seed - used so a UI screen can resolve the same
 * target for the same turn across recompositions without stashing state in a ViewModel (e.g.
 * seed on "${item.id}-${turnNumber}").
 */
fun seededRandom(seed: String): Random = Random(seed.hashCode().toLong())

/**
 * Resolves a content item's `targets` field to the concrete player(s) it points to.
 *
 * Genre et statut relationnel sont des attributs OPTIONNELS declares par les joueurs (voir
 * Player.gender / Player.relationship) : si personne a table ne correspond au critere demande
 * (ex. personne n'a precise son genre), on ne bloque JAMAIS la partie - on retombe sur un
 * joueur actif tire au hasard. Ne considere que les joueurs actifs ; si aucun n'est actif (ne
 * devrait pas arriver en session), retombe sur le roster complet.
 */
fun resolveTarget(players: List<Player>, target: Targets, random: Random = Random.Default): List<Player> {
    val active = players.filter { it.active }
    val pool = active.ifEmpty { players }
    if (pool.isEmpty()) return emptyList()

    fun randomOne(): List<Player> = pool.shuffled(random).take(1)
    fun matchOrFallback(predicate: (Player) -> Boolean): List<Player> {
        val matches = pool.filter(predicate)
        return if (matches.isNotEmpty()) matches.shuffled(random).take(1) else randomOne()
    }

    return when (target) {
        Targets.GENDER_M -> matchOrFallback { it.gender == Gender.M }
        Targets.GENDER_F -> matchOrFallback { it.gender == Gender.F }
        Targets.SINGLE -> matchOrFallback { it.relationship == Relationship.SINGLE }
        Targets.COUPLE -> matchOrFallback { it.relationship == Relationship.COUPLE }
        Targets.PAIR -> pool.shuffled(random).take(2)
        // 'self' / 'chosen' / 'all' are not handled by this module - graceful fallback.
        else -> randomOne()
    }
}
