package com.beloucif.latournee.core

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WouldYouRatherSessionTest {

    private fun players(vararg names: String) = names.map { createPlayer(it) }

    private fun questions(count: Int) = (1..count).map {
        WouldYouRatherQuestion("wyr-$it", "Option A $it", "Option B $it")
    }

    @Test
    fun `createWouldYouRatherSession shuffles the queue and starts on voting phase`() {
        val state = createWouldYouRatherSession(questions(5), players("Adam", "Léo", "Nina"), Random(1))
        assertEquals(WouldYouRatherPhase.VOTING, state.phase)
        assertEquals(1, state.roundNumber)
        assertEquals(4, state.queue.size)
        assertTrue(state.votes.isEmpty())
        assertTrue(state.penaltyCounts.isEmpty())
    }

    @Test
    fun `only active players enter the session`() {
        val ps = players("Adam", "Léo", "Nina").mapIndexed { i, p -> if (i == 1) p.copy(active = false) else p }
        val state = createWouldYouRatherSession(questions(3), ps, Random(1))
        assertEquals(2, state.players.size)
        assertTrue(state.players.none { it.name == "Léo" })
    }

    @Test
    fun `an empty question bank finishes the session immediately`() {
        val state = createWouldYouRatherSession(emptyList(), players("Adam", "Léo"), Random(1))
        assertEquals(WouldYouRatherPhase.FINISHED, state.phase)
        assertNull(state.currentQuestion)
    }

    @Test
    fun `castVote records a single ballot per player and ignores a second attempt`() {
        var state = createWouldYouRatherSession(questions(2), players("Adam", "Léo"), Random(1))
        val adam = state.players[0]

        state = castVote(state, adam.id, VoteSide.A)
        assertEquals(VoteSide.A, state.votes[adam.id])

        // Second vote from the same player is a no-op - the ballot does not flip.
        state = castVote(state, adam.id, VoteSide.B)
        assertEquals(VoteSide.A, state.votes[adam.id])
    }

    @Test
    fun `castVote is a no-op for an unknown player id`() {
        var state = createWouldYouRatherSession(questions(2), players("Adam", "Léo"), Random(1))
        val before = state
        state = castVote(state, "ghost-player", VoteSide.A)
        assertEquals(before, state)
    }

    @Test
    fun `castVote is a no-op outside the voting phase`() {
        var state = createWouldYouRatherSession(questions(2), players("Adam", "Léo"), Random(1))
        val adam = state.players[0]
        val leo = state.players[1]
        state = castVote(state, adam.id, VoteSide.A)
        state = castVote(state, leo.id, VoteSide.A)
        state = revealVotes(state)

        val before = state
        state = castVote(state, adam.id, VoteSide.B)
        assertEquals(before, state)
    }

    @Test
    fun `allVoted is true only once every active player has cast a ballot`() {
        var state = createWouldYouRatherSession(questions(1), players("Adam", "Léo"), Random(1))
        assertTrue(!allVoted(state))
        state = castVote(state, state.players[0].id, VoteSide.A)
        assertTrue(!allVoted(state))
        state = castVote(state, state.players[1].id, VoteSide.B)
        assertTrue(allVoted(state))
    }

    @Test
    fun `revealVotes penalizes the minority side by MINORITY_PENALTY each`() {
        var state = createWouldYouRatherSession(questions(1), players("Adam", "Léo", "Nina"), Random(1))
        val (adam, leo, nina) = state.players

        state = castVote(state, adam.id, VoteSide.A)
        state = castVote(state, leo.id, VoteSide.A)
        state = castVote(state, nina.id, VoteSide.B)

        state = revealVotes(state)

        assertEquals(WouldYouRatherPhase.REVEAL, state.phase)
        assertEquals(MINORITY_PENALTY, state.penaltyCounts[nina.id])
        assertEquals(0, state.penaltyCounts[adam.id] ?: 0)
        assertEquals(0, state.penaltyCounts[leo.id] ?: 0)
    }

    @Test
    fun `revealVotes is a no-op on a perfect tie`() {
        var state = createWouldYouRatherSession(questions(1), players("Adam", "Léo"), Random(1))
        state = castVote(state, state.players[0].id, VoteSide.A)
        state = castVote(state, state.players[1].id, VoteSide.B)

        state = revealVotes(state)

        assertEquals(WouldYouRatherPhase.REVEAL, state.phase)
        assertTrue(state.penaltyCounts.values.all { it == 0 })
    }

    @Test
    fun `revealVotes is a no-op on a unanimous vote`() {
        var state = createWouldYouRatherSession(questions(1), players("Adam", "Léo", "Nina"), Random(1))
        state.players.forEach { state = castVote(state, it.id, VoteSide.A) }

        state = revealVotes(state)

        assertEquals(WouldYouRatherPhase.REVEAL, state.phase)
        assertTrue(state.penaltyCounts.values.all { it == 0 })
    }

    @Test
    fun `revealVotes is a no-op outside the voting phase`() {
        var state = createWouldYouRatherSession(questions(2), players("Adam", "Léo"), Random(1))
        state = castVote(state, state.players[0].id, VoteSide.A)
        state = castVote(state, state.players[1].id, VoteSide.B)
        state = revealVotes(state)
        val before = state
        state = revealVotes(state)
        assertEquals(before, state)
    }

    @Test
    fun `penalties cumulate across multiple rounds`() {
        var state = createWouldYouRatherSession(questions(3), players("Adam", "Léo", "Nina"), Random(1))
        val (adam, leo, nina) = state.players

        // Round 1: Nina alone on B, takes 1 penalty.
        state = castVote(state, adam.id, VoteSide.A)
        state = castVote(state, leo.id, VoteSide.A)
        state = castVote(state, nina.id, VoteSide.B)
        state = revealVotes(state)
        state = nextRound(state)

        assertEquals(WouldYouRatherPhase.VOTING, state.phase)
        assertEquals(2, state.roundNumber)
        assertTrue(state.votes.isEmpty())

        // Round 2: Nina alone on B again, takes a second penalty - total 2.
        state = castVote(state, adam.id, VoteSide.A)
        state = castVote(state, leo.id, VoteSide.A)
        state = castVote(state, nina.id, VoteSide.B)
        state = revealVotes(state)

        assertEquals(2 * MINORITY_PENALTY, state.penaltyCounts[nina.id])
    }

    @Test
    fun `nextRound is a no-op outside the reveal phase`() {
        val state = createWouldYouRatherSession(questions(2), players("Adam", "Léo"), Random(1))
        val before = state
        val after = nextRound(state)
        assertEquals(before, after)
    }

    @Test
    fun `working through the whole queue finishes the session`() {
        var state = createWouldYouRatherSession(questions(1), players("Adam", "Léo"), Random(1))
        assertEquals(WouldYouRatherPhase.VOTING, state.phase)

        state = castVote(state, state.players[0].id, VoteSide.A)
        state = castVote(state, state.players[1].id, VoteSide.B)
        state = revealVotes(state)
        state = nextRound(state)

        assertEquals(WouldYouRatherPhase.FINISHED, state.phase)
        assertNull(state.currentQuestion)
    }

    @Test
    fun `createWouldYouRatherSession is deterministic for a fixed seed`() {
        val a = createWouldYouRatherSession(questions(10), players("Adam", "Léo"), Random(7))
        val b = createWouldYouRatherSession(questions(10), players("Adam", "Léo"), Random(7))
        assertEquals(a.queue.map { it.id }, b.queue.map { it.id })
        assertEquals(a.currentQuestion?.id, b.currentQuestion?.id)
    }
}
