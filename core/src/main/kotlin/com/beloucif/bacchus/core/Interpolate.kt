package com.beloucif.bacchus.core

/**
 * Interpolates a prompt template with the current player and, when needed, a second
 * distinct player picked at random. `{player}` -> current player's name. `{player2}` ->
 * another active player, never the current one (falls back to the current player only
 * when nobody else is available, so single-player previews never crash).
 * Faithful port of src/core/engine/interpolate.ts.
 */
fun interpolate(
    text: String,
    players: List<Player>,
    currentPlayer: Player,
    random: kotlin.random.Random = kotlin.random.Random,
): String {
    var result = text.replace("{player}", currentPlayer.name)

    if (result.contains("{player2}")) {
        val others = players.filter { it.id != currentPlayer.id && it.active }
        val pick = if (others.isNotEmpty()) others[random.nextInt(others.size)] else currentPlayer
        result = result.replace("{player2}", pick.name)
    }

    return result
}
