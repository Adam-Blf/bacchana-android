package com.beloucif.bacchana.core

import kotlin.random.Random

/**
 * Quitte ou Trinque - pure, side-effect-free engine (mirrors
 * src/core/engine/quizSession.ts). Each player answers a question worth 1 to 3
 * points (rolled at random). Correct answer: the points join their cagnotte,
 * then they choose - keep it (the cagnotte grows, so does the risk) or
 * distribute it (glory to the table, cagnotte resets to zero). Wrong answer:
 * they take their cagnotte + the question's points as penalties.
 */
enum class QuizPhase { QUESTION, CHOICE, FINISHED }

sealed interface QuizOutcome {
    val playerId: String
    val amount: Int

    data class Banked(override val playerId: String, override val amount: Int) : QuizOutcome
    data class Busted(override val playerId: String, override val amount: Int) : QuizOutcome
}

/**
 * Immutable snapshot of a Quitte ou Trinque session. [pots] is the cagnotte in
 * play per player id, not yet banked nor distributed. [penaltyCounts] is what
 * a player actually took (busted); [distributedCounts] is glory only, never a
 * penalty - mirrors the TS engine's split between the two maps.
 */
data class QuizSessionState(
    val players: List<Player> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val queue: List<QuizQuestion> = emptyList(),
    val currentQuestion: QuizQuestion? = null,
    val currentPoints: Int = 0,
    val pots: Map<String, Int> = emptyMap(),
    val penaltyCounts: Map<String, Int> = emptyMap(),
    val distributedCounts: Map<String, Int> = emptyMap(),
    val lastOutcome: QuizOutcome? = null,
    val phase: QuizPhase = QuizPhase.FINISHED,
    val turnNumber: Int = 1,
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

private fun rollPoints(random: Random): Int = 1 + random.nextInt(3)

private fun activePlayers(players: List<Player>): List<Player> = players.filter { it.active }

fun getCurrentQuizPlayer(state: QuizSessionState): Player? = state.players.getOrNull(state.currentPlayerIndex)

fun createQuizSession(
    questions: List<QuizQuestion>,
    players: List<Player>,
    random: Random = Random,
): QuizSessionState {
    val queue = shuffle(questions, random).toMutableList()
    val currentQuestion = queue.removeFirstOrNull()
    return QuizSessionState(
        players = activePlayers(players),
        currentPlayerIndex = 0,
        queue = queue,
        currentQuestion = currentQuestion,
        currentPoints = rollPoints(random),
        pots = emptyMap(),
        penaltyCounts = emptyMap(),
        distributedCounts = emptyMap(),
        lastOutcome = null,
        phase = if (currentQuestion != null) QuizPhase.QUESTION else QuizPhase.FINISHED,
        turnNumber = 1,
    )
}

private fun advance(state: QuizSessionState, random: Random): QuizSessionState {
    val nextQuestion = state.queue.firstOrNull()
    return state.copy(
        queue = state.queue.drop(1),
        currentQuestion = nextQuestion,
        currentPoints = rollPoints(random),
        currentPlayerIndex = (state.currentPlayerIndex + 1) % state.players.size,
        phase = if (nextQuestion != null) QuizPhase.QUESTION else QuizPhase.FINISHED,
        turnNumber = state.turnNumber + 1,
    )
}

/** Correct answer: the points join the cagnotte, hands off to the choice phase. */
fun answerCorrect(state: QuizSessionState): QuizSessionState {
    val player = getCurrentQuizPlayer(state) ?: return state
    if (state.phase != QuizPhase.QUESTION) return state
    return state.copy(
        pots = state.pots + (player.id to (state.pots[player.id] ?: 0) + state.currentPoints),
        phase = QuizPhase.CHOICE,
        lastOutcome = null,
    )
}

/** Wrong answer: the player takes their cagnotte + the question's points as penalties. */
fun answerWrong(state: QuizSessionState, random: Random = Random): QuizSessionState {
    val player = getCurrentQuizPlayer(state) ?: return state
    if (state.phase != QuizPhase.QUESTION) return state
    val amount = (state.pots[player.id] ?: 0) + state.currentPoints
    return advance(
        state.copy(
            pots = state.pots + (player.id to 0),
            penaltyCounts = state.penaltyCounts + (player.id to (state.penaltyCounts[player.id] ?: 0) + amount),
            lastOutcome = QuizOutcome.Busted(player.id, amount),
        ),
        random,
    )
}

/** The player distributes their cagnotte as penalties to the table and resets to zero. */
fun distributePot(state: QuizSessionState, random: Random = Random): QuizSessionState {
    val player = getCurrentQuizPlayer(state) ?: return state
    if (state.phase != QuizPhase.CHOICE) return state
    val amount = state.pots[player.id] ?: 0
    return advance(
        state.copy(
            pots = state.pots + (player.id to 0),
            distributedCounts = state.distributedCounts + (player.id to (state.distributedCounts[player.id] ?: 0) + amount),
            lastOutcome = QuizOutcome.Banked(player.id, amount),
        ),
        random,
    )
}

/** The player lets their cagnotte ride - quitte ou double at the next turn. */
fun keepPot(state: QuizSessionState, random: Random = Random): QuizSessionState {
    if (state.phase != QuizPhase.CHOICE) return state
    return advance(state.copy(lastOutcome = null), random)
}
