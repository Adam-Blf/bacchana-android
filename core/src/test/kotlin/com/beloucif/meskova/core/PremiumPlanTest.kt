package com.beloucif.meskova.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PremiumPlanTest {

    @Test
    fun `isPremiumEntitlementActive is true only when the exact entitlement id is present`() {
        assertTrue(isPremiumEntitlementActive(setOf("La Taverne Pro")))
        assertFalse(isPremiumEntitlementActive(emptySet()))
        assertFalse(isPremiumEntitlementActive(setOf("some_other_entitlement")))
    }

    @Test
    fun `fromProductId matches the exact product id`() {
        assertEquals(PremiumPlan.MONTHLY, PremiumPlan.fromProductId("premium_monthly"))
        assertEquals(PremiumPlan.ANNUAL, PremiumPlan.fromProductId("premium_yearly"))
        assertEquals(PremiumPlan.LIFETIME, PremiumPlan.fromProductId("premium_lifetime"))
    }

    @Test
    fun `fromProductId matches store-specific suffixed ids`() {
        // Play Store product ids can carry a base plan suffix, e.g. premium_monthly:p1m
        assertEquals(PremiumPlan.MONTHLY, PremiumPlan.fromProductId("premium_monthly:p1m"))
    }

    @Test
    fun `fromProductId returns null for an unknown id`() {
        assertNull(PremiumPlan.fromProductId("unrelated_product"))
    }
}
