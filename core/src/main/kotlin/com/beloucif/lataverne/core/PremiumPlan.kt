package com.beloucif.lataverne.core

/**
 * RevenueCat entitlement identifier configured in the dashboard.
 * Technical id created under the product's former name - do NOT rename without
 * migrating the entitlement in the RevenueCat dashboard, or existing subscribers
 * lose access. Mirrors PREMIUM_ENTITLEMENT_ID in la-taverne/src/lib/billing.ts.
 */
const val PREMIUM_ENTITLEMENT_ID = "La Taverne Pro"

/** True once one of the active entitlement ids reported by RevenueCat is the premium one. */
fun isPremiumEntitlementActive(activeEntitlementIds: Set<String>): Boolean =
    PREMIUM_ENTITLEMENT_ID in activeEntitlementIds

/**
 * The three purchasable premium plans, mirroring the RevenueCat product ids and prices
 * used by the web app (la-taverne/src/lib/billing.ts). Lifetime is highlighted as the
 * default choice - a single payment, no subscription, the differentiator against
 * subscription-only competitors.
 */
enum class PremiumPlan(val productId: String, val label: String, val priceLabel: String) {
    MONTHLY(productId = "premium_monthly", label = "Mensuel", priceLabel = "4,99 €"),
    ANNUAL(productId = "premium_yearly", label = "Annuel", priceLabel = "19,99 €"),
    LIFETIME(productId = "premium_lifetime", label = "À vie", priceLabel = "34,99 €"),
    ;

    companion object {
        /** Matches a RevenueCat store product id (which may carry a store-specific suffix). */
        fun fromProductId(productId: String): PremiumPlan? =
            entries.find { productId.startsWith(it.productId) }
    }
}
