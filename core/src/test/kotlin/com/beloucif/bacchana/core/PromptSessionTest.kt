package com.beloucif.bacchana.core

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptSessionTest {

    private fun item(
        id: String,
        text: String = "$id text",
        rule: Rule? = null,
        targets: Targets? = null,
    ) = PackItem(id = id, text = text, rule = rule, targets = targets)

    private fun players(vararg names: String) = names.map { createPlayer(it) }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects an empty item list`() {
        PromptSession(emptyList(), players("a", "b"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects fewer than 2 players`() {
        PromptSession(listOf(item("i1")), players("a"))
    }

    @Test
    fun `draws every item exactly once before repeating`() {
        val items = (1..10).map { item("i$it") }
        val session = PromptSession(items, players("a", "b", "c"), Random(1))

        val drawnIds = (1..10).map { session.drawNext().item.id }
        assertEquals(items.map { it.id }.toSet(), drawnIds.toSet())
        assertEquals(10, drawnIds.toSet().size)
    }

    @Test
    fun `reshuffles the discard pile once the draw pile is exhausted`() {
        val items = (1..5).map { item("i$it") }
        val session = PromptSession(items, players("a", "b"), Random(2))

        repeat(5) { session.drawNext() }
        assertTrue(session.hasNext())

        val nextRoundIds = (1..5).map { session.drawNext().item.id }.toSet()
        assertEquals(items.map { it.id }.toSet(), nextRoundIds)
    }

    @Test
    fun `interpolates player tokens in the drawn text`() {
        val items = listOf(item("i1", text = "{player} affronte {player2}."))
        val session = PromptSession(items, players("Adam", "Léo"), Random(1))
        val draw = session.drawNext()

        assertEquals("Adam", draw.player.name)
        assertEquals("Léo", draw.secondPlayer?.name)
        assertEquals("Adam affronte Léo.", draw.text)
    }

    @Test
    fun `advanceTurn rotates to the next active player`() {
        val items = listOf(item("i1"), item("i2"))
        val session = PromptSession(items, players("a", "b", "c"), Random(1))
        assertEquals("a", session.currentPlayer.name)
        session.advanceTurn()
        assertEquals("b", session.currentPlayer.name)
        session.advanceTurn()
        assertEquals("c", session.currentPlayer.name)
    }

    @Test
    fun `instant rules do not register any active rule`() {
        val items = listOf(item("i1", rule = Rule(RuleType.INSTANT)))
        val session = PromptSession(items, players("a", "b"), Random(1))
        session.drawNext()
        assertTrue(session.activeRules.isEmpty())
    }

    @Test
    fun `role rules stay active until overwritten, with no duration`() {
        val items = listOf(item("i1", rule = Rule(RuleType.ROLE)))
        val session = PromptSession(items, players("a", "b"), Random(1))
        session.drawNext()

        assertEquals(1, session.activeRules.size)
        assertNull(session.activeRules.first().remainingTurns)

        session.advanceTurn()
        session.advanceTurn()
        session.advanceTurn()
        assertEquals(1, session.activeRules.size)
    }

    @Test
    fun `persistent rules expire after durationTurns turns`() {
        val items = listOf(item("i1", rule = Rule(RuleType.PERSISTENT, durationTurns = 2)))
        val session = PromptSession(items, players("a", "b"), Random(1))
        session.drawNext()

        assertEquals(1, session.activeRules.size)
        session.advanceTurn()
        assertEquals(1, session.activeRules.size)
        assertEquals(1, session.activeRules.first().remainingTurns)
        session.advanceTurn()
        assertTrue(session.activeRules.isEmpty())
    }

    @Test
    fun `persistent rule targeting chosen applies to the second player`() {
        val items = listOf(
            item(
                "i1",
                text = "{player} choisit {player2}",
                rule = Rule(RuleType.PERSISTENT, durationTurns = 3),
                targets = Targets.CHOSEN,
            ),
        )
        val session = PromptSession(items, players("Adam", "Léo"), Random(1))
        val draw = session.drawNext()

        assertEquals(draw.secondPlayer, session.activeRules.first().target)
    }

    @Test
    fun `persistent rule without a chosen target applies to the drawing player`() {
        val items = listOf(item("i1", rule = Rule(RuleType.PERSISTENT, durationTurns = 3)))
        val session = PromptSession(items, players("Adam", "Léo"), Random(1))
        val draw = session.drawNext()

        assertEquals(draw.player, session.activeRules.first().target)
    }

    @Test
    fun `hasNext is true while items remain across reshuffles`() {
        val session = PromptSession(listOf(item("i1")), players("a", "b"), Random(1))
        assertTrue(session.hasNext())
        session.drawNext()
        assertTrue(session.hasNext())
    }
}
