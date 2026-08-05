package com.beloucif.latournee.billing

import android.app.Activity
import com.beloucif.latournee.core.PremiumPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Abstracts premium entitlement so the UI never depends on a billing SDK directly.
 * The real implementation is [RevenueCatEntitlementRepository], gated behind
 * BuildConfig.BILLING_ENABLED (true only when a RevenueCat API key is configured); see
 * [StubEntitlementRepository] for the guest-mode fallback used everywhere else, including CI.
 */
interface EntitlementRepository {
    /** True once the user owns the "La Tournee Pro" entitlement. */
    val isPremium: StateFlow<Boolean>

    /**
     * Kicks off the purchase flow for [plan] (lifetime by default - our differentiator).
     * Requires an [activity] because Play Billing anchors its UI to one. Returns true on
     * success, false on failure or cancellation. Always false in the stub.
     */
    suspend fun purchasePremium(activity: Activity, plan: PremiumPlan = PremiumPlan.LIFETIME): Boolean

    /** Restores a previous purchase (required by Play Store review guidelines). No-op in the stub. */
    suspend fun restorePurchases(): Boolean
}

/**
 * Default implementation while no RevenueCat API key is configured (guest mode, CI included).
 * Always reports isPremium = false so the app ships as a legitimate free download
 * with premium packs visibly locked, never crashing on a missing billing dependency.
 *
 * See [RevenueCatEntitlementRepository] for the real implementation, selected in
 * LaTourneeApplication only when BuildConfig.BILLING_ENABLED is true.
 */
class StubEntitlementRepository : EntitlementRepository {
    private val _isPremium = kotlinx.coroutines.flow.MutableStateFlow(false)
    override val isPremium: StateFlow<Boolean> = _isPremium

    override suspend fun purchasePremium(activity: Activity, plan: PremiumPlan): Boolean = false

    override suspend fun restorePurchases(): Boolean = false
}

/** Convenience accessor mirroring the StateFlow as a cold Flow, for composables that prefer collectAsState. */
val EntitlementRepository.isPremiumFlow: Flow<Boolean> get() = isPremium
