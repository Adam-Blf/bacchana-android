package com.beloucif.blackout

import android.app.Application
import com.beloucif.blackout.analytics.AnalyticsTracker
import com.beloucif.blackout.analytics.NoOpAnalyticsTracker
import com.beloucif.blackout.billing.EntitlementRepository
import com.beloucif.blackout.billing.StubEntitlementRepository
import com.beloucif.blackout.content.PackRepository
import com.beloucif.blackout.data.PlayerStore

/**
 * Minimal manual DI container - no Hilt/Koin for a single-module app this size.
 * Every dependency is an interface so billing/analytics can be swapped without
 * touching the UI layer.
 */
class BlackOutApplication : Application() {
    lateinit var packRepository: PackRepository
        private set
    lateinit var playerStore: PlayerStore
        private set
    lateinit var entitlementRepository: EntitlementRepository
        private set
    lateinit var analyticsTracker: AnalyticsTracker
        private set

    override fun onCreate() {
        super.onCreate()
        packRepository = PackRepository(this)
        playerStore = PlayerStore(this)
        entitlementRepository = StubEntitlementRepository()
        analyticsTracker = NoOpAnalyticsTracker()
    }
}
