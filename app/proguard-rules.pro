# Bacchana release ProGuard rules.
# kotlinx.serialization needs its generated serializers kept.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclasseswithmembers class com.beloucif.bacchana.content.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-if @kotlinx.serialization.Serializable class com.beloucif.bacchana.content.**
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

-if @kotlinx.serialization.Serializable class com.beloucif.bacchana.content.** {
    static **$* *;
}
-keepclassmembers class com.beloucif.bacchana.content.<1>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# RevenueCat / PostHog: both ship consumer ProGuard rules in their AAR, these are a defensive
# backstop for the public API surface this app calls directly (billing/RevenueCatEntitlementRepository.kt,
# analytics/PostHogAnalyticsTracker.kt) - both SDKs stay entirely inert without an API key.
-keep class com.revenuecat.purchases.** { *; }
-keep interface com.revenuecat.purchases.** { *; }
-keep class com.posthog.** { *; }
