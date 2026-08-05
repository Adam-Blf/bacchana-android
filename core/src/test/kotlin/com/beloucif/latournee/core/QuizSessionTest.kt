package com.beloucif.latournee.core

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizSessionTest {

    private fun players(vararg names: String) = names.map { createPlayer(it) }

    private fun questions(count: Int) = (1..count).map {
        QuizQuestion("qz-$it", QuizCategory.CULTURE_G, "Question $it ?", "Réponse $it")
    }

    @Test
    fun `createQuizSession shuffles the queue, rolls points and starts on question phase`() {
        val state = createQuizSession(questions(5), players("Adam", "Léo"), Random(1))
        assertEquals(QuizPhase.QUESTION, state.phase)
        assertEquals(1, state.turnNumber)
        assertEquals(0, state.currentPlayerIndex)
        assertTrue(state.currentPoints in 1..3)
        assertEquals(4, state.queue.size)
        assertTrue(state.pots.isEmpty())
        assertTrue(state.penaltyCounts.isEmpty())
    }

    @Test
    fun `only active players enter the session`() {
        val ps = players("Adam", "Léo", "Nina").mapIndexed { i, p -> if (i == 1) p.copy(active = false) else p }
        val state = createQuizSession(questions(3), ps, Random(1))
        assertEquals(2, state.players.size)
        assertTrue(state.players.none { it.name == "Léo" })
    }

    @Test
    fun `answerCorrect moves the points to the pot and switches to choice phase`() {
        var state = createQuizSession(questions(3), players("Adam", "Léo"), Random(1))
        val player = getCurrentQuizPlayer(state)!!
        val points = state.currentPoints

        state = answerCorrect(state)

        assertEquals(QuizPhase.CHOICE, state.phase)
        assertEquals(points, state.pots[player.id])
        assertNull(state.lastOutcome)
        // Still the same player's turn and question - only the choice screen changed.
        assertEquals(player.id, getCurrentQuizPlayer(state)?.id)
    }

    @Test
    fun `answerCorrect is a no-op outside the question phase`() {
        var state = createQuizSession(questions(3), players("Adam", "Léo"), Random(1))
        state = answerCorrect(state)
        val before = state
        state = answerCorrect(state)
        assertEquals(before, state)
    }

    @Test
    fun `answerWrong takes pot plus current points as a fresh penalty and advances the turn`() {
        var state = createQuizSession(questions(3), players("Adam", "Léo"), Random(1))
        val player = getCurrentQuizPlayer(state)!!
        val points = state.currentPoints

        state = answerWrong(state, Random(2))

        assertEquals(points, state.penaltyCounts[player.id])
        assertEquals(0, state.pots[player.id])
        assertEquals(QuizOutcome.Busted(player.id, points), state.lastOutcome)
        assertEquals(QuizPhase.QUESTION, state.phase)
        assertEquals(2, state.turnNumber)
        assertEquals(1, state.currentPlayerIndex)
    }

    @Test
    fun `answerWrong bundles a kept pot on top of the fresh question points`() {
        var state = createQuizSession(questions(3), players("Adam", "Léo"), Random(1))
        val player = getCurrentQuizPlayer(state)!!
        val firstPoints = state.currentPoints
        state = answerCorrect(state) // firstPoints join the pot
        state = distributePot(state, Random(2)) // banked as glory, resets to zero, hands off to Léo

        // Léo busts on their own turn - only their own points are at stake.
        val leo = getCurrentQuizPlayer(state)!!
        val leoPoints = state.currentPoints
        state = answerWrong(state, Random(3))

        assertEquals(leoPoints, state.penaltyCounts[leo.id])
        assertEquals(0, state.penaltyCounts[player.id] ?: 0)
        assertEquals(firstPoints, state.distributedCounts[player.id])
    }

    @Test
    fun `answerWrong is a no-op outside the question phase`() {
        var state = createQuizSession(questions(3), players("Adam", "Léo"), Random(1))
        state = answerCorrect(state)
        val before = state
        state = answerWrong(state, Random(2))
        assertEquals(before, state)
    }

    @Test
    fun `distributePot banks the pot as glory not penalty and resets it`() {
        var state = createQuizSession(questions(3), players("Adam", "Léo"), Random(1))
        val player = getCurrentQuizPlayer(state)!!
        val points = state.currentPoints
        state = answerCorrect(state)

        state = distributePot(state, Random(2))

        assertEquals(0, state.pots[player.id])
        assertEquals(points, state.distributedCounts[player.id])
        assertTrue((state.penaltyCounts[player.id] ?: 0) == 0)
        assertEquals(QuizOutcome.Banked(player.id, points), state.lastOutcome)
        assertEquals(QuizPhase.QUESTION, state.phase)
    }

    @Test
    fun `distributePot is a no-op outside the choice phase`() {
        var state = createQuizSession(questions(3), players("Adam", "Léo"), Random(1))
        val before = state
        state = distributePot(state, Random(2))
        assertEquals(before, state)
    }

    @Test
    fun `keepPot advances the turn without touching the pot`() {
        var state = createQuizSession(questions(3), players("Adam", "Léo"), Random(1))
        val player = getCurrentQuizPlayer(state)!!
        val points = state.currentPoints
        state = answerCorrect(state)

        state = keepPot(state, Random(2))

        assertEquals(points, state.pots[player.id])
        assertNull(state.lastOutcome)
        assertEquals(QuizPhase.QUESTION, state.phase)
        assertEquals(1, state.currentPlayerIndex)
    }

    @Test
    fun `keepPot is a no-op outside the choice phase`() {
        var state = createQuizSession(questions(3), players("Adam", "Léo"), Random(1))
        val before = state
        state = keepPot(state, Random(2))
        assertEquals(before, state)
    }

    @Test
    fun `quitte ou double - a kept pot survives a full round trip and grows`() {
        var state = createQuizSession(questions(4), players("Adam", "Léo"), Random(1))
        val adam = getCurrentQuizPlayer(state)!!
        val firstPoints = state.currentPoints
        state = answerCorrect(state)
        state = keepPot(state, Random(2)) // hands off to Léo, Adam's pot is untouched

        assertEquals(firstPoints, state.pots[adam.id])
        assertEquals(1, state.currentPlayerIndex)

        val leo = getCurrentQuizPlayer(state)!!
        val leoPoints = state.currentPoints
        state = answerWrong(state, Random(3)) // Léo busts, turn comes back to Adam
        assertEquals(0, state.currentPlayerIndex)
        assertEquals(leoPoints, state.penaltyCounts[leo.id])

        val secondPoints = state.currentPoints
        state = answerCorrect(state)
        assertEquals(firstPoints + secondPoints, state.pots[adam.id])
    }

    @Test
    fun `points reroll on every advance`() {
        var state = createQuizSession(questions(6), players("Adam", "Léo"), Random(42))
        val rolls = mutableListOf(state.currentPoints)
        repeat(4) { seed ->
            if (state.phase == QuizPhase.QUESTION) {
                state = answerWrong(state, Random(seed))
                rolls += state.currentPoints
            }
        }
        assertTrue(rolls.all { it in 1..3 })
    }

    @Test
    fun `an empty question queue finishes the session immediately`() {
        val state = createQuizSession(emptyList(), players("Adam", "Léo"), Random(1))
        assertEquals(QuizPhase.FINISHED, state.phase)
        assertNull(state.currentQuestion)
    }

    @Test
    fun `answering the last question in the queue finishes the session`() {
        var state = createQuizSession(questions(1), players("Adam", "Léo"), Random(1))
        assertEquals(QuizPhase.QUESTION, state.phase)
        state = answerWrong(state, Random(2))
        assertEquals(QuizPhase.FINISHED, state.phase)
        assertNull(state.currentQuestion)
    }

    @Test
    fun `createQuizSession is deterministic for a fixed seed`() {
        val a = createQuizSession(questions(10), players("Adam", "Léo"), Random(7))
        val b = createQuizSession(questions(10), players("Adam", "Léo"), Random(7))
        assertEquals(a.queue.map { it.id }, b.queue.map { it.id })
        assertEquals(a.currentQuestion?.id, b.currentQuestion?.id)
        assertEquals(a.currentPoints, b.currentPoints)
    }
}
