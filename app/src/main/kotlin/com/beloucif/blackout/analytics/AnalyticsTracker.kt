package com.beloucif.blackout.analytics

/**
 * Abstracts analytics so the UI never depends on a tracking SDK directly.
 * The real implementation (PostHog) is gated behind BuildConfig.ANALYTICS_ENABLED
 * AND explicit user consent (RGPD - see CLAUDE.md section 18), shipped in a later
 * version. [NoOpAnalyticsTracker] is the default while consent UI does not exist yet.
 */
interface AnalyticsTracker {
    fun trackScreen(name: String)
    fun trackEvent(name: String, properties: Map<String, Any?> = emptyMap())
}

/**
 * Default implementation: every call is a no-op. No network call, no local
 * storage, no identifier is ever generated. Safe by construction until a consent
 * flow ships.
 *
 * TODO(PostHog): replace with a PostHog-backed implementation once
 * BuildConfig.ANALYTICS_ENABLED is true AND the user has given explicit,
 * non-pre-checked consent (RGPD base legale - see CLAUDE.md section 18).
 */
class NoOpAnalyticsTracker : AnalyticsTracker {
    override fun trackScreen(name: String) = Unit
    override fun trackEvent(name: String, properties: Map<String, Any?>) = Unit
}
