package com.beloucif.bacchana

import android.app.Application
import com.beloucif.bacchana.analytics.AnalyticsTracker
import com.beloucif.bacchana.analytics.ConsentStore
import com.beloucif.bacchana.analytics.NoOpAnalyticsTracker
import com.beloucif.bacchana.analytics.PostHogAnalyticsTracker
import com.beloucif.bacchana.billing.EntitlementRepository
import com.beloucif.bacchana.billing.RevenueCatEntitlementRepository
import com.beloucif.bacchana.billing.StubEntitlementRepository
import com.beloucif.bacchana.content.PackRepository
import com.beloucif.bacchana.data.PlayerStore
import com.beloucif.bacchana.data.ThemeStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Minimal manual DI container - no Hilt/Koin for a single-module app this size.
 * Every dependency is an interface so billing/analytics can be swapped without
 * touching the UI layer. RevenueCat/PostHog are only ever selected when
 * BuildConfig.BILLING_ENABLED / ANALYTICS_ENABLED is true (a real API key is configured) -
 * otherwise the app runs entirely in guest mode, which is always the case in CI.
 */
class BacchanaApplication : Application() {
    lateinit var packRepository: PackRepository
        private set
    lateinit var playerStore: PlayerStore
        private set
    lateinit var consentStore: ConsentStore
        private set
    lateinit var themeStore: ThemeStore
        private set
    lateinit var entitlementRepository: EntitlementRepository
        private set
    lateinit var analyticsTracker: AnalyticsTracker
        private set

    override fun onCreate() {
        super.onCreate()
        packRepository = PackRepository(this)
        playerStore = PlayerStore(this)
        consentStore = ConsentStore(this)
        themeStore = ThemeStore(this)

        entitlementRepository = if (BuildConfig.BILLING_ENABLED) {
            RevenueCatEntitlementRepository(this, BuildConfig.REVENUECAT_API_KEY)
        } else {
            StubEntitlementRepository()
        }

        analyticsTracker = if (BuildConfig.ANALYTICS_ENABLED) {
            // A local DataStore read at cold start - fast, and the only way to know the
            // previously recorded consent choice before the first Compose frame renders.
            val previousConsent = runBlocking { consentStore.analyticsGranted.first() }
            PostHogAnalyticsTracker(this, BuildConfig.POSTHOG_API_KEY, previousConsent)
        } else {
            NoOpAnalyticsTracker()
        }
    }
}
