package com.beloucif.bacchana.core

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TribunalSessionTest {

    private fun players(vararg names: String) = names.map { createPlayer(it) }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects fewer than 2 active players`() {
        TribunalEngine.start(players("a").map { it.copy(active = false) })
    }

    @Test
    fun `pickAccused excludes the author`() {
        val ps = players("a", "b", "c")
        repeat(20) {
            val accused = TribunalEngine.pickAccused(ps, listOf(ps[0].id), Random(it))
            assertFalse(accused.id == ps[0].id)
        }
    }

    @Test
    fun `pickAccused falls back to everyone when the whole pool is excluded`() {
        val ps = players("a", "b")
        val accused = TribunalEngine.pickAccused(ps, ps.map { it.id }, Random(1))
        assertTrue(ps.map { it.id }.contains(accused.id))
    }

    @Test
    fun `pickAccused only considers active players`() {
        val ps = players("a", "b", "c").mapIndexed { i, p -> if (i == 1) p.copy(active = false) else p }
        repeat(20) {
            val accused = TribunalEngine.pickAccused(ps, emptyList(), Random(it))
            assertFalse(accused.name == "b")
        }
    }

    @Test
    fun `collect flow gathers one accusation per active player then starts a trial`() {
        var state = TribunalEngine.start(players("Adam", "Léo"))
        state = TribunalEngine.startCollect(state)
        assertEquals(TribunalPhase.COLLECT_HANDOFF, state.phase)
        assertEquals("Adam", state.writer?.name)

        state = TribunalEngine.confirmWriter(state)
        assertEquals(TribunalPhase.COLLECT_WRITE, state.phase)

        state = TribunalEngine.submitAccusation(state, "Adam accuse Léo", Random(1))
        assertEquals(TribunalPhase.COLLECT_HANDOFF, state.phase)
        assertEquals("Léo", state.writer?.name)

        state = TribunalEngine.confirmWriter(state)
        state = TribunalEngine.submitAccusation(state, "Léo accuse Adam", Random(1))

        // Both accusations were collected, the trial starts automatically.
        assertEquals(TribunalPhase.DEFENSE, state.phase)
        assertNotNull(state.current)
        assertNotNull(state.accused)
    }

    @Test
    fun `submitAccusation ignores a blank draft`() {
        var state = TribunalEngine.start(players("Adam", "Léo"))
        state = TribunalEngine.startCollect(state)
        state = TribunalEngine.confirmWriter(state)
        val before = state
        state = TribunalEngine.submitAccusation(state, "   ", Random(1))
        assertEquals(before, state)
    }

    @Test
    fun `submitAccusation caps the draft at 200 characters`() {
        var state = TribunalEngine.start(players("Adam", "Léo"))
        state = TribunalEngine.startCollect(state)
        state = TribunalEngine.confirmWriter(state)
        state = TribunalEngine.confirmWriter(state)
        val huge = "x".repeat(500)
        state = TribunalEngine.submitAccusation(state, huge, Random(1))
        assertEquals(200, state.pool.first().text.length)
    }

    @Test
    fun `useAppCharges draws from TRIBUNAL_CHARGES when no accusation was written`() {
        val state = TribunalEngine.useAppCharges(TribunalEngine.start(players("Adam", "Léo")), Random(1))
        assertEquals(TribunalPhase.DEFENSE, state.phase)
        assertTrue(TRIBUNAL_CHARGES.any { it.text == state.current?.text })
        assertNull(state.current?.authorId)
    }

    @Test
    fun `chargeText interpolates the accused name`() {
        var state = TribunalEngine.start(players("Adam", "Léo"))
        state = state.copy(
            current = Accusation("{player} a triché.", authorId = null),
            accused = state.players.first { it.name == "Léo" },
        )
        assertEquals("Léo a triché.", TribunalEngine.chargeText(state))
    }

    @Test
    fun `majority guilty verdict adds one penalty to the accused`() {
        var state = TribunalEngine.start(players("Adam", "Léo"))
        state = TribunalEngine.useAppCharges(state, Random(1))
        val accusedId = state.accused!!.id

        state = TribunalEngine.goToVote(state)
        state = TribunalEngine.voteGuilty(state)
        state = TribunalEngine.voteGuilty(state)
        state = TribunalEngine.voteInnocent(state)
        state = TribunalEngine.handleVerdict(state)

        assertEquals(true, state.verdict)
        assertEquals(1, state.trialsPlayed)
        assertEquals(1, state.penaltyCounts[accusedId])
    }

    @Test
    fun `tied vote acquits the accused`() {
        var state = TribunalEngine.start(players("Adam", "Léo"))
        state = TribunalEngine.useAppCharges(state, Random(1))
        state = TribunalEngine.goToVote(state)
        state = TribunalEngine.voteGuilty(state)
        state = TribunalEngine.voteInnocent(state)
        state = TribunalEngine.handleVerdict(state)

        assertEquals(false, state.verdict)
        assertTrue(state.penaltyCounts.isEmpty())
    }

    @Test
    fun `votes are ignored once a verdict has been reached`() {
        var state = TribunalEngine.start(players("Adam", "Léo"))
        state = TribunalEngine.useAppCharges(state, Random(1))
        state = TribunalEngine.goToVote(state)
        state = TribunalEngine.voteGuilty(state)
        state = TribunalEngine.handleVerdict(state)
        val before = state
        state = TribunalEngine.voteGuilty(state)
        assertEquals(before.votesGuilty, state.votesGuilty)
    }

    @Test
    fun `penalties accumulate across multiple trials against the same accused`() {
        var state = TribunalEngine.start(players("Adam", "Léo"))
        state = TribunalEngine.useAppCharges(state, Random(7))
        val accusedId = state.accused!!.id

        repeat(2) {
            state = TribunalEngine.goToVote(state)
            state = TribunalEngine.voteGuilty(state)
            state = TribunalEngine.handleVerdict(state)
            state = TribunalEngine.newTrial(state, Random(7))
        }

        assertEquals(2, state.penaltyCounts[accusedId])
        assertEquals(2, state.trialsPlayed)
    }

    @Test
    fun `replay resets penalties and trial count without mutating players`() {
        var state = TribunalEngine.start(players("Adam", "Léo"))
        state = TribunalEngine.useAppCharges(state, Random(1))
        state = TribunalEngine.goToVote(state)
        state = TribunalEngine.voteGuilty(state)
        state = TribunalEngine.handleVerdict(state)
        state = TribunalEngine.finish(state)
        assertEquals(TribunalPhase.FINISHED, state.phase)

        val originalPlayers = state.players
        state = TribunalEngine.replay(state)

        assertEquals(TribunalPhase.INTRO, state.phase)
        assertTrue(state.penaltyCounts.isEmpty())
        assertEquals(0, state.trialsPlayed)
        assertEquals(originalPlayers, state.players)
        assertTrue(originalPlayers.all { it.penaltiesStandard == 0 && it.penaltiesMajor == 0 })
    }
}

private fun assertNotNull(value: Any?) = org.junit.Assert.assertNotNull(value)
