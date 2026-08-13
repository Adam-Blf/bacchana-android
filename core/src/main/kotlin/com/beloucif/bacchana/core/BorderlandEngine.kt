package com.beloucif.bacchana.core

/** Game phase for a Borderland session. */
enum class GamePhase {
    SETUP,
    PLAYING,
    CONTEST,
    RESOLUTION,
    ENDED,
}

/** Immutable snapshot of a full Borderland game. Mirrors GameState from src/types/index.ts. */
data class BorderlandState(
    val deck: List<Card> = emptyList(),
    val discardPile: List<Card> = emptyList(),
    val players: List<Player> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val currentCard: Card? = null,
    val isCardRevealed: Boolean = false,
    val contestState: ContestState = ContestState.INITIAL,
    val gamePhase: GamePhase = GamePhase.SETUP,
) {
    val currentPlayer: Player? get() = players.getOrNull(currentPlayerIndex)
    val cardsRemaining: Int get() = deck.size
    val isGameOver: Boolean get() = gamePhase == GamePhase.ENDED || deck.isEmpty()
}

/**
 * Pure, side-effect-free reducer for Le Coupe-Gorge (technical name: Borderland). Every transition returns a new
 * [BorderlandState]; callers (ViewModel, tests) own the mutable holder.
 * Faithful port of the transition logic in src/stores/gameStore.ts.
 */
object BorderlandEngine {

    fun initGame(players: List<Player>, random: kotlin.random.Random = kotlin.random.Random): BorderlandState {
        require(players.size >= 2) { "At least 2 players required" }
        return BorderlandState(
            deck = shuffleDeck(createDeck(), random),
            players = players.map { it.copy(active = true) },
            gamePhase = GamePhase.PLAYING,
        )
    }

    fun drawCard(state: BorderlandState): BorderlandState {
        if (state.gamePhase != GamePhase.PLAYING || state.deck.isEmpty()) {
            return if (state.deck.isEmpty()) state.copy(gamePhase = GamePhase.ENDED) else state
        }
        val drawn = state.deck.first()
        return state.copy(
            deck = state.deck.drop(1),
            currentCard = drawn,
            isCardRevealed = false,
        )
    }

    fun revealCard(state: BorderlandState): BorderlandState = state.copy(isCardRevealed = true)

    fun nextTurn(state: BorderlandState): BorderlandState {
        val discard = state.currentCard?.let { state.discardPile + it } ?: state.discardPile

        if (state.deck.isEmpty()) {
            return state.copy(
                discardPile = discard,
                currentCard = null,
                isCardRevealed = false,
                contestState = ContestState.INITIAL,
                gamePhase = GamePhase.ENDED,
            )
        }

        val nextIndex = getNextPlayerIndex(state.currentPlayerIndex, state.players)
        return state.copy(
            discardPile = discard,
            currentPlayerIndex = nextIndex,
            currentCard = null,
            isCardRevealed = false,
            contestState = ContestState.INITIAL,
            gamePhase = GamePhase.PLAYING,
        )
    }

    fun startContest(state: BorderlandState, challenger: Player): BorderlandState {
        if (state.currentCard == null || state.gamePhase != GamePhase.PLAYING) return state
        return state.copy(
            contestState = ContestState(
                active = true,
                level = ContestLevel.FIRST,
                baseCard = state.currentCard,
                challenger = challenger,
            ),
            gamePhase = GamePhase.CONTEST,
        )
    }

    fun escalateContest(state: BorderlandState, challenger: Player): BorderlandState {
        val contest = state.contestState
        if (!contest.active) return state
        val nextLevel = ContestLevel.next(contest.level) ?: return state
        return state.copy(contestState = contest.copy(level = nextLevel, challenger = challenger))
    }

    /** Resolves the active contest and returns the state plus the penalty to apply to the loser. */
    fun resolveContest(state: BorderlandState): Pair<BorderlandState, PenaltyResult?> {
        val contest = state.contestState
        val baseCard = contest.baseCard
        if (!contest.active || baseCard == null) return state to null

        val penalty = calculatePenalty(baseCard.value, contest.level, baseCard.unit)
        return state.copy(gamePhase = GamePhase.RESOLUTION) to penalty
    }

    fun cancelContest(state: BorderlandState): BorderlandState =
        state.copy(contestState = ContestState.INITIAL, gamePhase = GamePhase.PLAYING)
}
