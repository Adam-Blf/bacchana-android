package com.beloucif.lataverne

import android.app.Application
import com.beloucif.lataverne.analytics.AnalyticsTracker
import com.beloucif.lataverne.analytics.NoOpAnalyticsTracker
import com.beloucif.lataverne.billing.EntitlementRepository
import com.beloucif.lataverne.billing.StubEntitlementRepository
import com.beloucif.lataverne.content.PackRepository
import com.beloucif.lataverne.data.PlayerStore

/**
 * Minimal manual DI container - no Hilt/Koin for a single-module app this size.
 * Every dependency is an interface so billing/analytics can be swapped without
 * touching the UI layer.
 */
class LaTaverneApplication : Application() {
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
