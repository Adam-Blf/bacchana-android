package com.beloucif.bacchus.core

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InterpolateTest {

    @Test
    fun `replaces {player} with the current player name`() {
        val p = createPlayer("Adam")
        val result = interpolate("{player}, prends une pénalité.", listOf(p), p)
        assertEquals("Adam, prends une pénalité.", result)
    }

    @Test
    fun `replaces {player2} with another active player, never the current one`() {
        val p1 = createPlayer("Adam")
        val p2 = createPlayer("Léo")
        val result = interpolate("{player} défie {player2}.", listOf(p1, p2), p1, Random(1))
        assertEquals("Adam défie Léo.", result)
    }

    @Test
    fun `falls back to the current player when nobody else is available`() {
        val p1 = createPlayer("Adam")
        val result = interpolate("{player} défie {player2}.", listOf(p1), p1)
        assertEquals("Adam défie Adam.", result)
    }

    @Test
    fun `ignores inactive players when picking player2`() {
        val p1 = createPlayer("Adam")
        val p2 = createPlayer("Léo").copy(active = false)
        val p3 = createPlayer("Nina")
        val result = interpolate("{player2}", listOf(p1, p2, p3), p1, Random(3))
        assertNotEquals("Léo", result)
        assertTrue(result == "Adam" || result == "Nina")
    }

    @Test
    fun `text without tokens is returned unchanged`() {
        val p = createPlayer("Adam")
        assertEquals("Rafale générale !", interpolate("Rafale générale !", listOf(p), p))
    }
}
