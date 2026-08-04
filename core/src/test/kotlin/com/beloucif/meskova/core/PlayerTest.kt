package com.beloucif.meskova.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerTest {

    @Test
    fun `createPlayer trims the name and starts with zeroed stats`() {
        val p = createPlayer("  Adam  ")
        assertEquals("Adam", p.name)
        assertTrue(p.active)
        assertEquals(0, p.penaltiesStandard)
        assertEquals(0, p.penaltiesMajor)
        assertEquals(0, p.contestsWon)
        assertEquals(0, p.contestsLost)
        assertEquals(0, p.cardsDrawn)
    }

    @Test
    fun `createPlayer generates unique ids`() {
        val ids = (1..100).map { createPlayer("x").id }.toSet()
        assertEquals(100, ids.size)
    }

    @Test
    fun `getNextPlayerIndex rotates circularly through active players`() {
        val players = listOf(createPlayer("a"), createPlayer("b"), createPlayer("c"))
        assertEquals(1, getNextPlayerIndex(0, players))
        assertEquals(0, getNextPlayerIndex(2, players))
    }

    @Test
    fun `getNextPlayerIndex skips inactive players`() {
        val players = listOf(
            createPlayer("a"),
            createPlayer("b").copy(active = false),
            createPlayer("c"),
        )
        assertEquals(2, getNextPlayerIndex(0, players))
    }

    @Test
    fun `getNextPlayerIndex returns 0 when nobody is active`() {
        val players = listOf(
            createPlayer("a").copy(active = false),
            createPlayer("b").copy(active = false),
        )
        assertEquals(0, getNextPlayerIndex(0, players))
    }

    @Test
    fun `getNextPlayerIndex returns 0 for an empty player list`() {
        assertEquals(0, getNextPlayerIndex(0, emptyList()))
    }
}
