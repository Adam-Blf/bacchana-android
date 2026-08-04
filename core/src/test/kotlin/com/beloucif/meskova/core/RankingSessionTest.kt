package com.beloucif.meskova.core

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RankingSessionTest {

    private fun players(vararg names: String) = names.map { createPlayer(it) }

    private fun questions(count: Int) = (1..count).map {
        RankingQuestion("rk-$it", "Question $it ?")
    }

    @Test
    fun `createRankingSession shuffles the queue and starts on handoff phase`() {
        val state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        assertEquals(RankingPhase.HANDOFF, state.phase)
        assertEquals(1, state.roundNumber)
        assertEquals(0, state.judgeIndex)
        assertNotNull(state.round)
        assertEquals(5, state.queue.size)
        assertTrue(state.ranked.isEmpty())
        assertTrue(state.penaltyCounts.isEmpty())
    }

    @Test
    fun `only active players enter the session`() {
        val ps = players("Adam", "Léo", "Nina", "Zoé").mapIndexed { i, p -> if (i == 1) p.copy(active = false) else p }
        val state = createRankingSession(questions(6), ps, Random(1))
        assertEquals(3, state.players.size)
        assertTrue(state.players.none { it.name == "Léo" })
    }

    @Test
    fun `buildRound produces 4 distinct choices including the real question`() {
        val state = createRankingSession(questions(10), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        val round = state.round!!
        assertEquals(4, round.choices.map { it.id }.toSet().size)
        assertTrue(round.choices.any { it.id == round.question.id })
    }

    @Test
    fun `getContestants excludes the judge`() {
        val state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        val judge = getJudge(state)!!
        val contestants = getContestants(state)
        assertEquals(3, contestants.size)
        assertTrue(contestants.none { it.id == judge.id })
    }

    @Test
    fun `startJudging moves handoff to judging`() {
        var state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        state = startJudging(state)
        assertEquals(RankingPhase.JUDGING, state.phase)
    }

    @Test
    fun `startJudging is a no-op outside handoff`() {
        var state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        state = startJudging(state)
        val before = state
        state = startJudging(state)
        assertEquals(before, state)
    }

    @Test
    fun `toggleRanked records tap order and can undo`() {
        var state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        state = startJudging(state)
        val (leo, nina) = getContestants(state).let { it[0] to it[1] }

        state = toggleRanked(state, nina.id)
        state = toggleRanked(state, leo.id)
        assertEquals(listOf(nina.id, leo.id), state.ranked)

        state = toggleRanked(state, nina.id)
        assertEquals(listOf(leo.id), state.ranked)
    }

    @Test
    fun `toggleRanked never ranks the judge`() {
        var state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        state = startJudging(state)
        val judge = getJudge(state)!!
        state = toggleRanked(state, judge.id)
        assertTrue(state.ranked.isEmpty())
    }

    @Test
    fun `confirmRanking requires every contestant to be ranked`() {
        var state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        state = startJudging(state)
        val contestants = getContestants(state)
        state = toggleRanked(state, contestants[0].id)

        val before = state
        state = confirmRanking(state)
        assertEquals(before, state)
        assertEquals(RankingPhase.JUDGING, state.phase)

        contestants.drop(1).forEach { state = toggleRanked(state, it.id) }
        state = confirmRanking(state)
        assertEquals(RankingPhase.RETURN, state.phase)
    }

    @Test
    fun `startGuessing moves return to guessing`() {
        var state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        state = startJudging(state)
        getContestants(state).forEach { state = toggleRanked(state, it.id) }
        state = confirmRanking(state)
        state = startGuessing(state)
        assertEquals(RankingPhase.GUESSING, state.phase)
    }

    @Test
    fun `guessQuestion correct penalizes only the judge`() {
        var state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        state = startJudging(state)
        getContestants(state).forEach { state = toggleRanked(state, it.id) }
        state = confirmRanking(state)
        state = startGuessing(state)
        val judge = getJudge(state)!!
        val realId = state.round!!.question.id

        state = guessQuestion(state, realId)

        assertEquals(RankingPhase.REVEAL, state.phase)
        assertEquals(JUDGE_PENALTY, state.penaltyCounts[judge.id])
        assertTrue(getContestants(state).all { (state.penaltyCounts[it.id] ?: 0) == 0 })
    }

    @Test
    fun `guessQuestion wrong penalizes every contestant not the judge`() {
        var state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        state = startJudging(state)
        val contestants = getContestants(state)
        contestants.forEach { state = toggleRanked(state, it.id) }
        state = confirmRanking(state)
        state = startGuessing(state)
        val judge = getJudge(state)!!
        val realId = state.round!!.question.id
        val decoyId = state.round!!.choices.first { it.id != realId }.id

        state = guessQuestion(state, decoyId)

        assertEquals(RankingPhase.REVEAL, state.phase)
        assertTrue((state.penaltyCounts[judge.id] ?: 0) == 0)
        contestants.forEach { assertEquals(GROUP_PENALTY, state.penaltyCounts[it.id]) }
    }

    @Test
    fun `guessQuestion is a no-op outside guessing`() {
        var state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        val before = state
        state = guessQuestion(state, state.round!!.question.id)
        assertEquals(before, state)
    }

    @Test
    fun `nextRound rotates the judge and draws a new secret question`() {
        var state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        state = startJudging(state)
        getContestants(state).forEach { state = toggleRanked(state, it.id) }
        state = confirmRanking(state)
        state = startGuessing(state)
        state = guessQuestion(state, state.round!!.question.id)

        state = nextRound(state, Random(2))

        assertEquals(RankingPhase.HANDOFF, state.phase)
        assertEquals(1, state.judgeIndex)
        assertEquals(2, state.roundNumber)
        assertTrue(state.ranked.isEmpty())
        assertNull(state.guessedId)
        assertNotNull(state.round)
    }

    @Test
    fun `nextRound is a no-op outside reveal`() {
        var state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        val before = state
        state = nextRound(state, Random(2))
        assertEquals(before, state)
    }

    @Test
    fun `judge rotation wraps around back to the first player`() {
        var state = createRankingSession(questions(6), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        repeat(state.players.size) {
            state = startJudging(state)
            getContestants(state).forEach { state = toggleRanked(state, it.id) }
            state = confirmRanking(state)
            state = startGuessing(state)
            state = guessQuestion(state, state.round!!.question.id)
            state = nextRound(state, Random(it))
        }
        assertEquals(0, state.judgeIndex)
    }

    @Test
    fun `an empty question queue finishes the session immediately`() {
        val state = createRankingSession(emptyList(), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        assertEquals(RankingPhase.FINISHED, state.phase)
        assertNull(state.round)
    }

    @Test
    fun `running out of questions finishes the session`() {
        var state = createRankingSession(questions(1), players("Adam", "Léo", "Nina", "Zoé"), Random(1))
        state = startJudging(state)
        getContestants(state).forEach { state = toggleRanked(state, it.id) }
        state = confirmRanking(state)
        state = startGuessing(state)
        state = guessQuestion(state, state.round!!.question.id)

        state = nextRound(state, Random(2))

        assertEquals(RankingPhase.FINISHED, state.phase)
        assertNull(state.round)
    }

    @Test
    fun `createRankingSession is deterministic for a fixed seed`() {
        val a = createRankingSession(questions(10), players("Adam", "Léo", "Nina", "Zoé"), Random(7))
        val b = createRankingSession(questions(10), players("Adam", "Léo", "Nina", "Zoé"), Random(7))
        assertEquals(a.queue.map { it.id }, b.queue.map { it.id })
        assertEquals(a.round?.question?.id, b.round?.question?.id)
        assertEquals(a.round?.choices?.map { it.id }, b.round?.choices?.map { it.id })
    }
}
