package com.beloucif.blackout.core

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BorderlandEngineTest {

    private fun players(vararg names: String) = names.map { createPlayer(it) }

    @Test
    fun `initGame requires at least 2 players`() {
        try {
            BorderlandEngine.initGame(players("solo"))
            assert(false) { "expected exception" }
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("2 players"))
        }
    }

    @Test
    fun `initGame shuffles a full 52-card deck and starts on PLAYING`() {
        val state = BorderlandEngine.initGame(players("a", "b"), Random(1))
        assertEquals(52, state.deck.size)
        assertEquals(GamePhase.PLAYING, state.gamePhase)
        assertEquals(0, state.currentPlayerIndex)
    }

    @Test
    fun `drawCard moves a card from deck to currentCard`() {
        var state = BorderlandEngine.initGame(players("a", "b"), Random(1))
        state = BorderlandEngine.drawCard(state)
        assertEquals(51, state.deck.size)
        assertNotNull(state.currentCard)
        assertEquals(false, state.isCardRevealed)
    }

    @Test
    fun `revealCard flips isCardRevealed to true`() {
        var state = BorderlandEngine.initGame(players("a", "b"), Random(1))
        state = BorderlandEngine.drawCard(state)
        state = BorderlandEngine.revealCard(state)
        assertTrue(state.isCardRevealed)
    }

    @Test
    fun `nextTurn discards the current card and rotates to the next player`() {
        var state = BorderlandEngine.initGame(players("a", "b"), Random(1))
        state = BorderlandEngine.drawCard(state)
        val drawn = state.currentCard
        state = BorderlandEngine.nextTurn(state)

        assertEquals(1, state.currentPlayerIndex)
        assertNull(state.currentCard)
        assertEquals(listOf(drawn), state.discardPile)
    }

    @Test
    fun `nextTurn ends the game once the deck is empty`() {
        var state = BorderlandEngine.initGame(players("a", "b"), Random(1))
        repeat(52) {
            state = BorderlandEngine.drawCard(state)
            state = BorderlandEngine.nextTurn(state)
        }
        assertEquals(GamePhase.ENDED, state.gamePhase)
        assertTrue(state.isGameOver)
    }

    @Test
    fun `startContest requires a drawn card and PLAYING phase`() {
        val state = BorderlandEngine.initGame(players("a", "b"), Random(1))
        val unchanged = BorderlandEngine.startContest(state, state.players[1])
        assertEquals(GamePhase.PLAYING, unchanged.gamePhase)
        assertEquals(false, unchanged.contestState.active)
    }

    @Test
    fun `startContest opens a contest at level FIRST on the drawn card`() {
        var state = BorderlandEngine.initGame(players("a", "b"), Random(1))
        state = BorderlandEngine.drawCard(state)
        val challenger = state.players[1]
        state = BorderlandEngine.startContest(state, challenger)

        assertTrue(state.contestState.active)
        assertEquals(ContestLevel.FIRST, state.contestState.level)
        assertEquals(challenger, state.contestState.challenger)
        assertEquals(GamePhase.CONTEST, state.gamePhase)
    }

    @Test
    fun `escalateContest increments level up to THIRD then refuses further escalation`() {
        var state = BorderlandEngine.initGame(players("a", "b"), Random(1))
        state = BorderlandEngine.drawCard(state)
        state = BorderlandEngine.startContest(state, state.players[1])

        state = BorderlandEngine.escalateContest(state, state.players[0])
        assertEquals(ContestLevel.SECOND, state.contestState.level)
        state = BorderlandEngine.escalateContest(state, state.players[1])
        assertEquals(ContestLevel.THIRD, state.contestState.level)
        state = BorderlandEngine.escalateContest(state, state.players[0])
        assertEquals(ContestLevel.THIRD, state.contestState.level)
    }

    @Test
    fun `resolveContest applies the multiplier of the current level`() {
        var state = BorderlandEngine.initGame(players("a", "b"), Random(1))
        state = BorderlandEngine.drawCard(state)
        state = BorderlandEngine.startContest(state, state.players[1])
        state = BorderlandEngine.escalateContest(state, state.players[0])

        val baseCard = state.contestState.baseCard!!
        val (resolved, penalty) = BorderlandEngine.resolveContest(state)

        assertNotNull(penalty)
        assertEquals(baseCard.value * 2, penalty!!.amount)
        assertEquals(GamePhase.RESOLUTION, resolved.gamePhase)
    }

    @Test
    fun `resolveContest returns null penalty when no contest is active`() {
        val state = BorderlandEngine.initGame(players("a", "b"), Random(1))
        val (_, penalty) = BorderlandEngine.resolveContest(state)
        assertNull(penalty)
    }

    @Test
    fun `cancelContest resets contest state and returns to PLAYING`() {
        var state = BorderlandEngine.initGame(players("a", "b"), Random(1))
        state = BorderlandEngine.drawCard(state)
        state = BorderlandEngine.startContest(state, state.players[1])
        state = BorderlandEngine.cancelContest(state)

        assertEquals(false, state.contestState.active)
        assertEquals(GamePhase.PLAYING, state.gamePhase)
    }
}
