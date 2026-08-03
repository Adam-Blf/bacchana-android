package com.beloucif.lataverne.core

import kotlin.random.Random

/**
 * Le Tableau d'Honneur - pure, side-effect-free engine (mirrors
 * src/core/engine/rankingSession.ts). A judge discovers a secret ranking
 * question and ranks the other players against it. The table then sees the
 * podium and must guess the real question among four. Guessed right: the
 * judge takes the penalties. Guessed wrong: every non-judge player takes one.
 */
enum class RankingPhase { HANDOFF, JUDGING, RETURN, GUESSING, REVEAL, FINISHED }

const val JUDGE_PENALTY = 3
const val GROUP_PENALTY = 1

/** [choices] holds 4 shuffled propositions, including the real [question]. */
data class RankingRound(
    val question: RankingQuestion,
    val choices: List<RankingQuestion>,
)

/**
 * Immutable snapshot of a Tableau d'Honneur session. [ranked] holds the ids
 * of the players ranked by the judge, from the top of the podium down,
 * excluding the judge itself.
 */
data class RankingSessionState(
    val players: List<Player> = emptyList(),
    val allQuestions: List<RankingQuestion> = emptyList(),
    val queue: List<RankingQuestion> = emptyList(),
    val judgeIndex: Int = 0,
    val roundNumber: Int = 1,
    val round: RankingRound? = null,
    val ranked: List<String> = emptyList(),
    val guessedId: String? = null,
    val phase: RankingPhase = RankingPhase.FINISHED,
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

fun getJudge(state: RankingSessionState): Player? = state.players.getOrNull(state.judgeIndex)

fun getContestants(state: RankingSessionState): List<Player> {
    val judge = getJudge(state)
    return state.players.filter { it.id != judge?.id }
}

private fun buildRound(question: RankingQuestion, allQuestions: List<RankingQuestion>, random: Random): RankingRound {
    val decoys = shuffle(allQuestions.filter { it.id != question.id }, random).take(3)
    return RankingRound(question = question, choices = shuffle(listOf(question) + decoys, random))
}

fun createRankingSession(
    questions: List<RankingQuestion>,
    players: List<Player>,
    random: Random = Random,
): RankingSessionState {
    val queue = shuffle(questions, random).toMutableList()
    val first = queue.removeFirstOrNull()
    return RankingSessionState(
        players = activePlayers(players),
        allQuestions = questions,
        queue = queue,
        judgeIndex = 0,
        roundNumber = 1,
        round = first?.let { buildRound(it, questions, random) },
        ranked = emptyList(),
        guessedId = null,
        phase = if (first != null) RankingPhase.HANDOFF else RankingPhase.FINISHED,
        penaltyCounts = emptyMap(),
    )
}

/** The judge has the phone in hand: onto the secret ranking. */
fun startJudging(state: RankingSessionState): RankingSessionState {
    if (state.phase != RankingPhase.HANDOFF) return state
    return state.copy(phase = RankingPhase.JUDGING)
}

/** Adds (or removes) a player from the judge's podium, in tap order. */
fun toggleRanked(state: RankingSessionState, playerId: String): RankingSessionState {
    if (state.phase != RankingPhase.JUDGING) return state
    val judge = getJudge(state)
    if (playerId == judge?.id) return state
    val ranked = if (state.ranked.contains(playerId)) {
        state.ranked.filter { it != playerId }
    } else {
        state.ranked + playerId
    }
    return state.copy(ranked = ranked)
}

/** The judge confirms their podium and hands the phone back to the table. */
fun confirmRanking(state: RankingSessionState): RankingSessionState {
    if (state.phase != RankingPhase.JUDGING) return state
    if (state.ranked.size != getContestants(state).size) return state
    return state.copy(phase = RankingPhase.RETURN)
}

/** The phone is back at the center: the table discovers the podium. */
fun startGuessing(state: RankingSessionState): RankingSessionState {
    if (state.phase != RankingPhase.RETURN) return state
    return state.copy(phase = RankingPhase.GUESSING)
}

/** The table picks a proposition; penalties fall immediately. */
fun guessQuestion(state: RankingSessionState, questionId: String): RankingSessionState {
    if (state.phase != RankingPhase.GUESSING || state.round == null) return state
    val judge = getJudge(state)
    val correct = questionId == state.round.question.id
    val penaltyCounts = state.penaltyCounts.toMutableMap()
    if (correct && judge != null) {
        penaltyCounts[judge.id] = (penaltyCounts[judge.id] ?: 0) + JUDGE_PENALTY
    } else {
        for (p in getContestants(state)) {
            penaltyCounts[p.id] = (penaltyCounts[p.id] ?: 0) + GROUP_PENALTY
        }
    }
    return state.copy(guessedId = questionId, penaltyCounts = penaltyCounts, phase = RankingPhase.REVEAL)
}

/** Next round: the judge role rotates, a new secret question is drawn. */
fun nextRound(state: RankingSessionState, random: Random = Random): RankingSessionState {
    if (state.phase != RankingPhase.REVEAL) return state
    val queue = state.queue.toMutableList()
    val next = queue.removeFirstOrNull()
    return state.copy(
        queue = queue,
        judgeIndex = (state.judgeIndex + 1) % state.players.size,
        roundNumber = state.roundNumber + 1,
        round = next?.let { buildRound(it, state.allQuestions, random) },
        ranked = emptyList(),
        guessedId = null,
        phase = if (next != null) RankingPhase.HANDOFF else RankingPhase.FINISHED,
    )
}
