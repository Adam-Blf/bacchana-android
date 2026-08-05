package com.beloucif.latournee.core

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TargetingTest {

    private fun player(
        id: String,
        name: String,
        active: Boolean = true,
        gender: Gender? = null,
        relationship: Relationship? = null,
    ) = Player(id = id, name = name, active = active, gender = gender, relationship = relationship)

    private val players = listOf(
        player("a", "Alice", gender = Gender.F, relationship = Relationship.SINGLE),
        player("b", "Bob", gender = Gender.M, relationship = Relationship.COUPLE),
        player("c", "Chris", gender = Gender.X),
        player("d", "Dana", gender = Gender.F, relationship = Relationship.COUPLE),
    )

    @Test
    fun `isResolvableTarget recognizes the resolvable targets`() {
        assertTrue(isResolvableTarget(Targets.GENDER_M))
        assertTrue(isResolvableTarget(Targets.GENDER_F))
        assertTrue(isResolvableTarget(Targets.PAIR))
        assertTrue(isResolvableTarget(Targets.SINGLE))
        assertTrue(isResolvableTarget(Targets.COUPLE))
    }

    @Test
    fun `isResolvableTarget rejects targets outside its scope`() {
        assertFalse(isResolvableTarget(Targets.SELF))
        assertFalse(isResolvableTarget(Targets.CHOSEN))
        assertFalse(isResolvableTarget(Targets.ALL))
        assertFalse(isResolvableTarget(null))
    }

    @Test
    fun `resolves gender-m to a matching player`() {
        val result = resolveTarget(players, Targets.GENDER_M, Random(1))
        assertEquals(1, result.size)
        assertEquals(Gender.M, result[0].gender)
    }

    @Test
    fun `resolves gender-f to a matching player`() {
        val result = resolveTarget(players, Targets.GENDER_F, Random(1))
        assertEquals(1, result.size)
        assertEquals(Gender.F, result[0].gender)
    }

    @Test
    fun `resolves single to a matching player`() {
        val result = resolveTarget(players, Targets.SINGLE, Random(1))
        assertEquals(1, result.size)
        assertEquals(Relationship.SINGLE, result[0].relationship)
    }

    @Test
    fun `resolves couple to a matching player`() {
        val result = resolveTarget(players, Targets.COUPLE, Random(1))
        assertEquals(1, result.size)
        assertEquals(Relationship.COUPLE, result[0].relationship)
    }

    @Test
    fun `resolves pair to two distinct players`() {
        val result = resolveTarget(players, Targets.PAIR, Random(1))
        assertEquals(2, result.size)
        assertNotEquals(result[0].id, result[1].id)
    }

    @Test
    fun `falls back to a random active player when nobody matches the criterion`() {
        val noAttributes = listOf(player("x", "X"), player("y", "Y"))
        val result = resolveTarget(noAttributes, Targets.GENDER_M, Random(1))
        assertEquals(1, result.size)
        assertTrue(result[0].name in listOf("X", "Y"))
    }

    @Test
    fun `falls back gracefully for a single player with no matching attribute`() {
        val solo = listOf(player("z", "Zoe"))
        assertEquals(solo, resolveTarget(solo, Targets.COUPLE, Random(1)))
    }

    @Test
    fun `ignores inactive players`() {
        val withInactive = listOf(
            player("a", "Alice", active = false, gender = Gender.F),
            player("b", "Bob", active = true, gender = Gender.M),
        )
        // Alice matches gender-f but is inactive -> falls back to the only active player.
        val result = resolveTarget(withInactive, Targets.GENDER_F, Random(1))
        assertEquals(listOf(withInactive[1]), result)
    }

    @Test
    fun `returns an empty list when there are no players at all`() {
        assertEquals(emptyList<Player>(), resolveTarget(emptyList(), Targets.PAIR, Random(1)))
    }

    @Test
    fun `resolves the same target across calls with the same seed (stable per turn)`() {
        val a = resolveTarget(players, Targets.PAIR, seededRandom("item-3-turn-5"))
        val b = resolveTarget(players, Targets.PAIR, seededRandom("item-3-turn-5"))
        assertEquals(a, b)
    }

    @Test
    fun `can resolve differently for a different seed`() {
        val seeds = listOf("a-1", "b-2", "c-3", "d-4", "e-5", "f-6")
        val results = seeds.map { seed -> resolveTarget(players, Targets.GENDER_F, seededRandom(seed)).firstOrNull()?.id }
        assertTrue(results.toSet().size > 1)
    }
}
