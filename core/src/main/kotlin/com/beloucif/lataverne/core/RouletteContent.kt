package com.beloucif.lataverne.core

/**
 * La Roulette - embedded game mode, no content pack (mirrors la-taverne/src/content/roulette.ts).
 * 8 segments, store-safe by construction: only abstract penalties, never a named alcohol.
 */
data class RouletteSegment(
    val id: String,
    val label: String,
    val detail: String,
)

val ROULETTE_SEGMENTS: List<RouletteSegment> = listOf(
    RouletteSegment("rou-01", "1 pénalité", "Le sort est plutôt clément."),
    RouletteSegment("rou-02", "2 pénalités", "Un petit rappel à l'ordre."),
    RouletteSegment("rou-03", "3 pénalités", "La roue ne fait pas de cadeau."),
    RouletteSegment("rou-04", "PÉNALITÉ MAJEURE", "Le pire tirage possible."),
    RouletteSegment("rou-05", "Distribue 2", "Choisis deux joueurs qui prennent 1 pénalité chacun."),
    RouletteSegment("rou-06", "Immunité", "Aucune pénalité au prochain tour, quoi qu'il arrive."),
    RouletteSegment("rou-07", "Tout le monde", "Toute la table prend 1 pénalité."),
    RouletteSegment("rou-08", "Rejoue", "Relance la roue immédiatement."),
)
