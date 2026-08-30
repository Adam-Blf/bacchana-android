package com.beloucif.bacchana.core

/**
 * RevenueCat entitlement identifier configured in the dashboard.
 * Renamed to "Bacchana Pro" (no accent, exact RevenueCat dashboard id) for the product's
 * fifth name, Bacchana (display: "Bacchana"). The app is not yet published, so there are
 * no existing subscribers to migrate - unlike a normal rename, the entitlement id itself moves
 * this time. Mirrors PREMIUM_ENTITLEMENT_ID in bacchana/src/lib/billing.ts.
 */
const val PREMIUM_ENTITLEMENT_ID = "Bacchana Pro"

/** True once one of the active entitlement ids reported by RevenueCat is the premium one. */
fun isPremiumEntitlementActive(activeEntitlementIds: Set<String>): Boolean =
    PREMIUM_ENTITLEMENT_ID in activeEntitlementIds

/**
 * The one purchasable plan: a single payment, kept for good.
 *
 * WHY THIS ENUM HOLDS EXACTLY ONE VALUE, and why that is the point.
 *
 * It used to carry three: premium_monthly at 4,99, premium_yearly at 19,99 and
 * premium_lifetime at 34,99. The pricing was settled on 2026-08-30 - a single lifetime
 * purchase at 12,99, no subscription, no free trial - and the web app moved. This port did
 * not, which left the store paywall offering two subscriptions that will exist in no store
 * and a lifetime price that is wrong by 22 euros. Nothing could catch it: both plans and
 * prices are plain strings, and a paywall that renders is a paywall that looks fine.
 *
 * No-subscription is the product argument, not a temporary state, so a one-value enum is
 * the honest shape. It stays an enum rather than a constant because it keeps the product
 * id typed in one place and leaves `purchasePremium(activity, plan)` untouched for the day
 * an optional pack is added.
 *
 * Mirrors the web catalogue in bacchana/src/components/premium/PremiumPaywallModal.tsx.
 * The store price shown to the buyer always comes from the store itself - `priceLabel` is
 * only the fallback for guest mode, offline, or offerings not loaded yet.
 */
enum class PremiumPlan(val productId: String, val label: String, val priceLabel: String) {
    LIFETIME(productId = "premium_lifetime", label = "À vie", priceLabel = "12,99 €"),
    ;

    companion object {
        /** Matches a RevenueCat store product id (which may carry a store-specific suffix). */
        fun fromProductId(productId: String): PremiumPlan? =
            entries.find { productId.startsWith(it.productId) }
    }
}
