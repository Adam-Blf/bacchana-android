package com.beloucif.lataverne.core

import kotlin.random.Random

/**
 * Tu preferes - embedded voting engine (no content pack, unlike the other prompt
 * modes). A dilemma A or B is shown to the whole table, the phone stays put,
 * every active player taps their own camp. On reveal, the minority camp takes
 * a local penalty; a perfect tie or a unanimous vote costs nobody anything.
 * The recap is local to this session only - never merged into
 * [Player.penaltiesStandard].
 */
enum class WouldYouRatherPhase { VOTING, REVEAL, FINISHED }

enum class VoteSide { A, B }

const val MINORITY_PENALTY = 1

/**
 * Immutable snapshot of a Tu preferes session. [votes] only holds the ballots cast
 * for [currentQuestion] in the current round - it is cleared on [nextRound].
 * [penaltyCounts] accumulates across rounds and is purely local to this session.
 */
data class WouldYouRatherSessionState(
    val players: List<Player> = emptyList(),
    val queue: List<WouldYouRatherQuestion> = emptyList(),
    val currentQuestion: WouldYouRatherQuestion? = null,
    val roundNumber: Int = 1,
    val votes: Map<String, VoteSide> = emptyMap(),
    val phase: WouldYouRatherPhase = WouldYouRatherPhase.FINISHED,
    val penaltyCounts: Map<String, Int> = emptyMap(),
)

private fun <T> shuffle(input: List<T>, random: Random): List<T> {
    val arr = input.toMutableList()
    for (i in arr.size - 1 downTo 1) {
        val j = random.nextInt(i + 1)
        val tmp = arr[i]
        arr[i] = arr[j]
        arr[j] = tmp
    }
    return arr
}

private fun activePlayers(players: List<Player>): List<Player> = players.filter { it.active }

fun createWouldYouRatherSession(
    questions: List<WouldYouRatherQuestion>,
    players: List<Player>,
    random: Random = Random,
): WouldYouRatherSessionState {
    val queue = shuffle(questions, random).toMutableList()
    val currentQuestion = queue.removeFirstOrNull()
    return WouldYouRatherSessionState(
        players = activePlayers(players),
        queue = queue,
        currentQuestion = currentQuestion,
        roundNumber = 1,
        votes = emptyMap(),
        phase = if (currentQuestion != null) WouldYouRatherPhase.VOTING else WouldYouRatherPhase.FINISHED,
        penaltyCounts = emptyMap(),
    )
}

/** Records a single vote. No-op if not voting, the player is unknown, or they already voted. */
fun castVote(state: WouldYouRatherSessionState, playerId: String, side: VoteSide): WouldYouRatherSessionState {
    if (state.phase != WouldYouRatherPhase.VOTING) return state
    if (state.players.none { it.id == playerId }) return state
    if (state.votes.containsKey(playerId)) return state
    return state.copy(votes = state.votes + (playerId to side))
}

/** True once every active player has cast a ballot for the current question. */
fun allVoted(state: WouldYouRatherSessionState): Boolean = state.votes.size == state.players.size

fun countVotes(votes: Map<String, VoteSide>): Map<VoteSide, Int> = mapOf(
    VoteSide.A to votes.values.count { it == VoteSide.A },
    VoteSide.B to votes.values.count { it == VoteSide.B },
)

/** Null when there is nothing to punish: no votes, a perfect tie, or a unanimous vote. */
fun getMinoritySide(votes: Map<String, VoteSide>): VoteSide? {
    val counts = countVotes(votes)
    val countA = counts.getValue(VoteSide.A)
    val countB = counts.getValue(VoteSide.B)
    if (countA == 0 && countB == 0) return null
    if (countA == 0 || countB == 0) return null // unanimous
    if (countA == countB) return null // perfect tie
    return if (countA < countB) VoteSide.A else VoteSide.B
}

/** Reveals the round: the minority camp (if any) takes [MINORITY_PENALTY] each. */
fun revealVotes(state: WouldYouRatherSessionState): WouldYouRatherSessionState {
    if (state.phase != WouldYouRatherPhase.VOTING) return state
    val minoritySide = getMinoritySide(state.votes)
    val updatedPenalties = if (minoritySide == null) {
        state.penaltyCounts
    } else {
        state.votes.entries.filter { it.value == minoritySide }.fold(state.penaltyCounts) { acc, (playerId, _) ->
            acc + (playerId to (acc[playerId] ?: 0) + MINORITY_PENALTY)
        }
    }
    return state.copy(phase = WouldYouRatherPhase.REVEAL, penaltyCounts = updatedPenalties)
}

/** Moves to the next dilemma, clearing the ballots. Finishes the session once the queue is empty. */
fun nextRound(state: WouldYouRatherSessionState): WouldYouRatherSessionState {
    if (state.phase != WouldYouRatherPhase.REVEAL) return state
    val nextQuestion = state.queue.firstOrNull()
    return state.copy(
        queue = state.queue.drop(1),
        currentQuestion = nextQuestion,
        roundNumber = state.roundNumber + 1,
        votes = emptyMap(),
        phase = if (nextQuestion != null) WouldYouRatherPhase.VOTING else WouldYouRatherPhase.FINISHED,
    )
}
