package com.beloucif.lataverne.core

/** A rule still in effect after a persistent or role item was drawn. */
data class ActiveRule(
    val itemId: String,
    val text: String,
    val target: Player,
    val remainingTurns: Int?,
)

/** One drawn prompt, fully interpolated and ready for display. */
data class PromptDraw(
    val item: PackItem,
    val text: String,
    val player: Player,
    val secondPlayer: Player?,
)

/**
 * Turn-based session for the generic prompt engine (picolo, action-verite, etc).
 * Draws items from a shuffled pile with no repetition until exhausted, tracks
 * persistent/role rules across turns and interpolates {player}/{player2} tokens.
 */
class PromptSession(
    items: List<PackItem>,
    val players: List<Player>,
    private val random: kotlin.random.Random = kotlin.random.Random,
) {
    init {
        require(items.isNotEmpty()) { "At least one item required" }
        require(players.size >= 2) { "At least 2 players required" }
    }

    private var drawPile: MutableList<PackItem> = items.shuffled(random).toMutableList()
    private var discardPile: MutableList<PackItem> = mutableListOf()
    private var currentPlayerIndex: Int = 0

    private val _activeRules: MutableList<ActiveRule> = mutableListOf()
    val activeRules: List<ActiveRule> get() = _activeRules.toList()

    val currentPlayer: Player get() = players[currentPlayerIndex]

    /** True while a prompt can still be drawn (draw pile or discard pile is non-empty). */
    fun hasNext(): Boolean = drawPile.isNotEmpty() || discardPile.isNotEmpty()

    /**
     * Draws the next prompt without repeating an item until the whole pack has been seen.
     * Reshuffles the discard pile back into the draw pile once it is exhausted.
     */
    fun drawNext(): PromptDraw {
        check(hasNext()) { "No prompt items left" }
        if (drawPile.isEmpty()) {
            drawPile = discardPile.shuffled(random).toMutableList()
            discardPile = mutableListOf()
        }

        val item = drawPile.removeAt(0)
        discardPile.add(item)

        val player = currentPlayer
        val eligibleSecond = players.filter { it.id != player.id && it.active }
        val secondPlayer = if (item.text.contains("{player2}") && eligibleSecond.isNotEmpty()) {
            eligibleSecond[random.nextInt(eligibleSecond.size)]
        } else {
            null
        }

        val text = interpolate(item.text, players, player, random)
        registerRule(item, player, secondPlayer)

        return PromptDraw(item, text, player, secondPlayer)
    }

    /** Advances to the next active player and ticks down persistent rule durations. */
    fun advanceTurn() {
        currentPlayerIndex = getNextPlayerIndex(currentPlayerIndex, players)

        val next = _activeRules.mapNotNull { rule ->
            val turns = rule.remainingTurns ?: return@mapNotNull rule
            val remaining = turns - 1
            if (remaining <= 0) null else rule.copy(remainingTurns = remaining)
        }
        _activeRules.clear()
        _activeRules.addAll(next)
    }

    private fun registerRule(item: PackItem, player: Player, secondPlayer: Player?) {
        val rule = item.rule ?: return
        when (rule.type) {
            RuleType.PERSISTENT -> {
                val target = if (item.targets == Targets.CHOSEN) secondPlayer ?: player else player
                _activeRules.add(ActiveRule(item.id, item.text, target, rule.durationTurns))
            }
            RuleType.ROLE -> {
                _activeRules.add(ActiveRule(item.id, item.text, player, remainingTurns = null))
            }
            RuleType.INSTANT -> Unit
        }
    }
}
