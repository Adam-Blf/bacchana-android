package com.beloucif.latournee.core

/** Phase for a Le Tribunal ("Le Pilori") session. Mirrors the `Phase` union in TribunalScreen.tsx. */
enum class TribunalPhase {
    INTRO,
    COLLECT_HANDOFF,
    COLLECT_WRITE,
    DEFENSE,
    VOTE,
    FINISHED,
}

/** One accusation, either written secretly by a player or drawn from [TRIBUNAL_CHARGES]. */
data class Accusation(
    val text: String,
    /** null = accusation embedded in the app. */
    val authorId: String? = null,
)

/**
 * Immutable snapshot of a Le Tribunal session. Mirrors the local component state of
 * src/components/screens/TribunalScreen.tsx - there is no shared engine.penaltyCounts store
 * on the mobile side, [penaltyCounts] lives only for the lifetime of this session and is never
 * written back onto [Player].
 */
data class TribunalState(
    val players: List<Player> = emptyList(),
    val phase: TribunalPhase = TribunalPhase.INTRO,
    val writerIndex: Int = 0,
    val pool: List<Accusation> = emptyList(),
    val current: Accusation? = null,
    val accused: Player? = null,
    val votesGuilty: Int = 0,
    val votesInnocent: Int = 0,
    /** null = no verdict yet, true = coupable, false = non coupable. */
    val verdict: Boolean? = null,
    val penaltyCounts: Map<String, Int> = emptyMap(),
    val trialsPlayed: Int = 0,
) {
    val activePlayers: List<Player> get() = players.filter { it.active }
    val writer: Player? get() = activePlayers.getOrNull(writerIndex)
}

/**
 * Pure, side-effect-free reducer for Le Tribunal ("Le Pilori"). Every transition returns a new
 * [TribunalState]; callers (Compose screen, tests) own the mutable holder. Faithful port of the
 * transition logic in src/components/screens/TribunalScreen.tsx, except the verdict penalty,
 * which is a flat +1 per guilty verdict (no [calculatePenalty]/Borderland import on mobile).
 */
object TribunalEngine {

    fun start(players: List<Player>): TribunalState {
        require(players.count { it.active } >= 2) { "At least 2 active players required" }
        return TribunalState(players = players)
    }

    /** Excludes [excludeIds] (typically the accusation's author) from the pool of possible accused. */
    fun pickAccused(
        players: List<Player>,
        excludeIds: List<String?>,
        random: kotlin.random.Random = kotlin.random.Random,
    ): Player {
        val active = players.filter { it.active }
        val pool = active.filter { it.id !in excludeIds }
        val candidates = pool.ifEmpty { active }
        return candidates[random.nextInt(candidates.size)]
    }

    /** Starts the secret-writing collection loop: each active player writes one accusation. */
    fun startCollect(state: TribunalState): TribunalState =
        state.copy(writerIndex = 0, pool = emptyList(), phase = TribunalPhase.COLLECT_HANDOFF)

    /** The current writer confirms it is their turn ("C'est moi, {name}"). */
    fun confirmWriter(state: TribunalState): TribunalState = state.copy(phase = TribunalPhase.COLLECT_WRITE)

    /**
     * Records the current writer's accusation (trimmed, capped at 200 chars). Hands off to the
     * next writer, or starts the first trial once everybody has written.
     */
    fun submitAccusation(
        state: TribunalState,
        text: String,
        random: kotlin.random.Random = kotlin.random.Random,
    ): TribunalState {
        val writer = state.writer ?: return state
        val trimmed = text.trim().take(200)
        if (trimmed.isEmpty()) return state

        val nextPool = state.pool + Accusation(trimmed, writer.id)
        return if (state.writerIndex + 1 < state.activePlayers.size) {
            state.copy(pool = nextPool, writerIndex = state.writerIndex + 1, phase = TribunalPhase.COLLECT_HANDOFF)
        } else {
            startTrialFrom(state, nextPool, random)
        }
    }

    /** Skips collection entirely and plays with the app-provided [TRIBUNAL_CHARGES]. */
    fun useAppCharges(state: TribunalState, random: kotlin.random.Random = kotlin.random.Random): TribunalState =
        startTrialFrom(state, emptyList(), random)

    /** Draws one accusation from [accusations] (or [TRIBUNAL_CHARGES] as fallback) and picks the accused. */
    fun startTrialFrom(
        state: TribunalState,
        accusations: List<Accusation>,
        random: kotlin.random.Random = kotlin.random.Random,
    ): TribunalState {
        val source = accusations.ifEmpty { TRIBUNAL_CHARGES.map { Accusation(it.text, authorId = null) } }
        val accusation = source[random.nextInt(source.size)]
        val nextAccused = pickAccused(state.activePlayers, listOf(accusation.authorId), random)
        return state.copy(
            pool = accusations.filterNot { it === accusation },
            current = accusation,
            accused = nextAccused,
            votesGuilty = 0,
            votesInnocent = 0,
            verdict = null,
            phase = TribunalPhase.DEFENSE,
        )
    }

    /** The accusation text with `{player}` interpolated to the accused's name. */
    fun chargeText(state: TribunalState, random: kotlin.random.Random = kotlin.random.Random): String {
        val current = state.current ?: return ""
        val accused = state.accused ?: return current.text
        return interpolate(current.text, state.activePlayers, accused, random)
    }

    fun goToVote(state: TribunalState): TribunalState = state.copy(phase = TribunalPhase.VOTE)

    fun voteGuilty(state: TribunalState): TribunalState =
        if (state.verdict != null) state else state.copy(votesGuilty = state.votesGuilty + 1)

    fun voteInnocent(state: TribunalState): TribunalState =
        if (state.verdict != null) state else state.copy(votesInnocent = state.votesInnocent + 1)

    /** Majority of guilty votes adds a flat +1 penalty to the accused. Ties favour innocence. */
    fun handleVerdict(state: TribunalState): TribunalState {
        val accused = state.accused ?: return state
        val guilty = state.votesGuilty > state.votesInnocent
        val nextCounts = if (guilty) {
            state.penaltyCounts + (accused.id to (state.penaltyCounts[accused.id] ?: 0) + 1)
        } else {
            state.penaltyCounts
        }
        return state.copy(verdict = guilty, trialsPlayed = state.trialsPlayed + 1, penaltyCounts = nextCounts)
    }

    /** Starts the next trial from the remaining pool (or [TRIBUNAL_CHARGES] once the pool is empty). */
    fun newTrial(state: TribunalState, random: kotlin.random.Random = kotlin.random.Random): TribunalState =
        startTrialFrom(state, state.pool, random)

    fun finish(state: TribunalState): TribunalState = state.copy(phase = TribunalPhase.FINISHED)

    /** Resets penalties and trial count, back to the intro screen. Never mutates [Player]. */
    fun replay(state: TribunalState): TribunalState = TribunalState(players = state.players)
}
