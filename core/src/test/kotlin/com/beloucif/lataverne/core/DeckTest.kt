package com.beloucif.lataverne.core

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeckTest {

    @Test
    fun `deck contains exactly 52 unique cards`() {
        val deck = createDeck()
        assertEquals(52, deck.size)
        assertEquals(52, deck.map { it.id }.toSet().size)
    }

    @Test
    fun `deck has 13 cards per suit`() {
        val deck = createDeck()
        for (suit in Suit.entries) {
            assertEquals(13, deck.count { it.suit == suit })
        }
    }

    @Test
    fun `deck maps values A=1 through K=13`() {
        val deck = createDeck()
        assertEquals(1, deck.first { it.id == "clubs-A" }.value)
        assertEquals(10, deck.first { it.id == "hearts-10" }.value)
        assertEquals(11, deck.first { it.id == "spades-J" }.value)
        assertEquals(12, deck.first { it.id == "diamonds-Q" }.value)
        assertEquals(13, deck.first { it.id == "clubs-K" }.value)
    }

    @Test
    fun `CRITICAL aces are major penalties everything else standard`() {
        val deck = createDeck()
        val aces = deck.filter { it.rank == Rank.ACE }
        assertEquals(4, aces.size)
        aces.forEach { assertEquals(PenaltyUnit.MAJOR, it.unit) }
        deck.filter { it.rank != Rank.ACE }.forEach { assertEquals(PenaltyUnit.STANDARD, it.unit) }
    }

    @Test
    fun `each suit has exactly one ace`() {
        val deck = createDeck()
        for (suit in Suit.entries) {
            assertEquals(1, deck.count { it.suit == suit && it.rank == Rank.ACE })
        }
    }

    @Test
    fun `shuffle preserves all cards and does not mutate the original`() {
        val deck = createDeck()
        val snapshot = deck.toList()
        val shuffled = shuffleDeck(deck, Random(42))

        assertEquals(snapshot, deck)
        assertEquals(52, shuffled.size)
        assertEquals(52, shuffled.map { it.id }.toSet().size)
    }

    @Test
    fun `shuffle with different seeds produces different orders`() {
        val deck = createDeck()
        val shuffledA = shuffleDeck(deck, Random(1))
        val shuffledB = shuffleDeck(deck, Random(2))
        assertNotEquals(shuffledA, shuffledB)
    }

    @Test
    fun `shuffle is deterministic for a fixed seed`() {
        val deck = createDeck()
        val a = shuffleDeck(deck, Random(7))
        val b = shuffleDeck(deck, Random(7))
        assertEquals(a, b)
    }

    @Test
    fun `shuffle of empty list returns empty list`() {
        assertTrue(shuffleDeck(emptyList(), Random(1)).isEmpty())
    }
}
