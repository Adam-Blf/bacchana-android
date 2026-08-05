package com.beloucif.latournee.analytics

/**
 * Abstracts analytics so the UI never depends on a tracking SDK directly.
 * The real implementation is [PostHogAnalyticsTracker], gated behind
 * BuildConfig.ANALYTICS_ENABLED (a PostHog key is configured) AND explicit user consent
 * (RGPD - see CLAUDE.md section 18). [NoOpAnalyticsTracker] is the default everywhere else,
 * including CI and any player who declines the consent banner.
 */
interface AnalyticsTracker {
    fun trackScreen(name: String)
    fun trackEvent(name: String, properties: Map<String, Any?> = emptyMap())

    /**
     * Called when the player grants or revokes analytics consent from the consent banner
     * (see [ConsentStore]). No-op by default - only [PostHogAnalyticsTracker] acts on it.
     */
    fun setConsent(granted: Boolean) = Unit
}

/**
 * Default implementation: every call is a no-op. No network call, no local
 * storage, no identifier is ever generated. Used whenever BuildConfig.ANALYTICS_ENABLED
 * is false (no PostHog key configured - always the case in CI).
 */
class NoOpAnalyticsTracker : AnalyticsTracker {
    override fun trackScreen(name: String) = Unit
    override fun trackEvent(name: String, properties: Map<String, Any?>) = Unit
}
