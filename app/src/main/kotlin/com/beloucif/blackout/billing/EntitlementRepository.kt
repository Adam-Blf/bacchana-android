package com.beloucif.blackout.billing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Abstracts premium entitlement so the UI never depends on a billing SDK directly.
 * The real implementation (RevenueCat + Play Billing) is gated behind
 * BuildConfig.BILLING_ENABLED and shipped in a later version; see [StubEntitlementRepository].
 */
interface EntitlementRepository {
    /** True once the user owns the premium pack bundle. */
    val isPremium: StateFlow<Boolean>

    /** Kicks off the purchase flow. Returns true on success. No-op in the stub. */
    suspend fun purchasePremium(): Boolean

    /** Restores a previous purchase (required by Play Store review guidelines). No-op in the stub. */
    suspend fun restorePurchases(): Boolean
}

/**
 * Default implementation while Play Billing / RevenueCat are not wired up.
 * Always reports isPremium = false so the app ships as a legitimate free download
 * with premium packs visibly locked, never crashing on a missing billing dependency.
 *
 * TODO(RevenueCat): replace with a Purchases SDK-backed implementation once
 * BuildConfig.BILLING_ENABLED is flipped to true and the API key is provisioned.
 * See app/build.gradle.kts for the commented dependency line.
 */
class StubEntitlementRepository : EntitlementRepository {
    private val _isPremium = kotlinx.coroutines.flow.MutableStateFlow(false)
    override val isPremium: StateFlow<Boolean> = _isPremium

    override suspend fun purchasePremium(): Boolean = false

    override suspend fun restorePurchases(): Boolean = false
}

/** Convenience accessor mirroring the StateFlow as a cold Flow, for composables that prefer collectAsState. */
val EntitlementRepository.isPremiumFlow: Flow<Boolean> get() = isPremium
