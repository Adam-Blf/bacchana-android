package com.beloucif.lataverne.analytics

import android.content.Context
import com.beloucif.lataverne.BuildConfig
import com.posthog.PostHog
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig

/**
 * Real PostHog-backed [AnalyticsTracker]. Only ever instantiated by LaTaverneApplication
 * when BuildConfig.ANALYTICS_ENABLED is true (a PostHog key is configured) - see
 * [NoOpAnalyticsTracker] for the default used everywhere else.
 *
 * The SDK itself is never initialized (`PostHogAndroid.setup`) until consent is actually
 * granted - either restored from [ConsentStore] at construction time, or live via
 * [setConsent] - so zero network call happens before an explicit, non-pre-checked opt-in
 * (CLAUDE.md section 18).
 */
class PostHogAnalyticsTracker(
    private val context: Context,
    private val apiKey: String,
    initialConsent: Boolean,
) : AnalyticsTracker {

    private var initialized = false

    init {
        if (initialConsent) initialize()
    }

    private fun initialize() {
        if (initialized || apiKey.isBlank()) return
        val config = PostHogAndroidConfig(apiKey = apiKey, host = POSTHOG_EU_HOST).apply {
            captureApplicationLifecycleEvents = false
            debug = BuildConfig.DEBUG
        }
        PostHogAndroid.setup(context.applicationContext, config)
        initialized = true
    }

    override fun trackScreen(name: String) {
        if (!initialized) return
        PostHog.screen(screenTitle = name)
    }

    override fun trackEvent(name: String, properties: Map<String, Any?>) {
        if (!initialized) return
        val cleanProperties = properties.filterValues { it != null }.mapValues { it.value as Any }
        PostHog.capture(event = name, properties = cleanProperties)
    }

    override fun setConsent(granted: Boolean) {
        if (granted) {
            initialize()
            if (initialized) PostHog.optIn()
        } else if (initialized) {
            PostHog.optOut()
        }
    }

    private companion object {
        // EU instance - required for our RGPD data residency (CLAUDE.md section 18),
        // mirrors la-taverne/src/lib/analytics.ts on the web side.
        const val POSTHOG_EU_HOST = "https://eu.i.posthog.com"
    }
}
