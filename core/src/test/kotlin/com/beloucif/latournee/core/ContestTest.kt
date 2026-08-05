package com.beloucif.latournee.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ContestTest {

    @Test
    fun `contest multipliers are {0-1, 1-1, 2-2, 3-4}`() {
        assertEquals(1, CONTEST_MULTIPLIERS.getValue(ContestLevel.NONE))
        assertEquals(1, CONTEST_MULTIPLIERS.getValue(ContestLevel.FIRST))
        assertEquals(2, CONTEST_MULTIPLIERS.getValue(ContestLevel.SECOND))
        assertEquals(4, CONTEST_MULTIPLIERS.getValue(ContestLevel.THIRD))
    }

    @Test
    fun `calculatePenalty applies contest multipliers`() {
        assertEquals(5, calculatePenalty(5, ContestLevel.NONE, PenaltyUnit.STANDARD).amount)
        assertEquals(5, calculatePenalty(5, ContestLevel.FIRST, PenaltyUnit.STANDARD).amount)
        assertEquals(10, calculatePenalty(5, ContestLevel.SECOND, PenaltyUnit.STANDARD).amount)
        assertEquals(20, calculatePenalty(5, ContestLevel.THIRD, PenaltyUnit.STANDARD).amount)
    }

    @Test
    fun `calculatePenalty keeps the unit unchanged`() {
        assertEquals(PenaltyUnit.MAJOR, calculatePenalty(1, ContestLevel.THIRD, PenaltyUnit.MAJOR).unit)
        assertEquals(PenaltyUnit.STANDARD, calculatePenalty(1, ContestLevel.THIRD, PenaltyUnit.STANDARD).unit)
    }

    @Test
    fun `calculatePenalty formats store-safe major penalty text`() {
        assertEquals("PÉNALITÉ MAJEURE", calculatePenalty(1, ContestLevel.NONE, PenaltyUnit.MAJOR).displayText)
        assertEquals("PÉNALITÉ MAJEURE x2", calculatePenalty(1, ContestLevel.SECOND, PenaltyUnit.MAJOR).displayText)
    }

    @Test
    fun `calculatePenalty formats store-safe standard penalty text with plurals`() {
        assertEquals("1 pénalité", calculatePenalty(1, ContestLevel.NONE, PenaltyUnit.STANDARD).displayText)
        assertEquals("3 pénalités", calculatePenalty(3, ContestLevel.NONE, PenaltyUnit.STANDARD).displayText)
    }

    @Test
    fun `ContestLevel next escalates one step at a time and caps at THIRD`() {
        assertEquals(ContestLevel.FIRST, ContestLevel.next(ContestLevel.NONE))
        assertEquals(ContestLevel.SECOND, ContestLevel.next(ContestLevel.FIRST))
        assertEquals(ContestLevel.THIRD, ContestLevel.next(ContestLevel.SECOND))
        assertEquals(null, ContestLevel.next(ContestLevel.THIRD))
    }

    @Test
    fun `no penalty wording ever mentions alcohol units`() {
        val forbidden = listOf("shot", "gorgee", "verre", "alcool", "biere", "vin")
        for (unit in PenaltyUnit.entries) {
            for (level in ContestLevel.entries) {
                val text = calculatePenalty(3, level, unit).displayText.lowercase()
                forbidden.forEach { word -> assertEquals(false, text.contains(word)) }
            }
        }
    }
}
