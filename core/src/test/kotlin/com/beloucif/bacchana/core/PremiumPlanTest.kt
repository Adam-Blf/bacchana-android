package com.beloucif.bacchana.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PremiumPlanTest {

    @Test
    fun `isPremiumEntitlementActive is true only when the exact entitlement id is present`() {
        assertTrue(isPremiumEntitlementActive(setOf("Bacchana Pro")))
        assertFalse(isPremiumEntitlementActive(emptySet()))
        assertFalse(isPremiumEntitlementActive(setOf("some_other_entitlement")))
    }

    @Test
    fun `fromProductId matches the exact product id`() {
        assertEquals(PremiumPlan.LIFETIME, PremiumPlan.fromProductId("premium_lifetime"))
    }

    @Test
    fun `fromProductId matches store-specific suffixed ids`() {
        assertEquals(PremiumPlan.LIFETIME, PremiumPlan.fromProductId("premium_lifetime:base"))
    }

    @Test
    fun `fromProductId returns null for an unknown id`() {
        assertNull(PremiumPlan.fromProductId("unrelated_product"))
    }

    /**
     * The catalogue is the price list shown to a buyer, so it is pinned here rather than
     * left to a screenshot. This test exists because the port kept selling premium_monthly
     * at 4,99, premium_yearly at 19,99 and a lifetime at 34,99 for weeks after the price
     * was settled at a single 12,99 - plain strings that render perfectly while being wrong.
     */
    @Test
    fun `the catalogue is one lifetime plan at 12,99 - no subscription`() {
        assertEquals(1, PremiumPlan.entries.size)
        val plan = PremiumPlan.LIFETIME
        assertEquals("premium_lifetime", plan.productId)
        assertEquals("12,99 €", plan.priceLabel)
    }

    @Test
    fun `no subscription product id survives anywhere in the catalogue`() {
        // Named explicitly: these are the two ids that must never come back, because a
        // subscription contradicts the one thing the product promises.
        for (plan in PremiumPlan.entries) {
            assertFalse(plan.productId.contains("monthly"))
            assertFalse(plan.productId.contains("yearly"))
        }
    }
}
