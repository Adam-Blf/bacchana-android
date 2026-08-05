import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
}

// RevenueCat / PostHog API keys: read from local.properties (gitignored, see
// local.properties.example) or env vars, never hardcoded. Both stay blank in CI, which is
// exactly what keeps the app buildable and running in guest mode with zero secrets.
val secretsProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) FileInputStream(file).use { load(it) }
}

fun secret(key: String): String = secretsProperties.getProperty(key) ?: System.getenv(key) ?: ""

val revenueCatApiKey = secret("REVENUECAT_API_KEY")
val postHogApiKey = secret("POSTHOG_API_KEY")

android {
    namespace = "com.beloucif.latournee"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.beloucif.latournee"
        minSdk = 26
        targetSdk = 35
        versionCode = 17
        versionName = "0.15.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val envKeystorePath = System.getenv("KEYSTORE_PATH")
            val envKeystorePassword = System.getenv("KEYSTORE_PASSWORD")
            val envKeyAlias = System.getenv("KEY_ALIAS")
            val envKeyPassword = System.getenv("KEY_PASSWORD")

            if (envKeystorePath != null && envKeystorePassword != null && envKeyAlias != null && envKeyPassword != null) {
                storeFile = file(envKeystorePath)
                storePassword = envKeystorePassword
                keyAlias = envKeyAlias
                keyPassword = envKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }

    // BuildConfig flags gating RevenueCat/PostHog: true only when the matching API key is
    // present (local.properties or env), so CI - which never has these keys - always builds
    // and runs in guest mode, and no real network call is ever attempted without a key.
    buildFeatures {
        compose = true
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("boolean", "BILLING_ENABLED", "${revenueCatApiKey.isNotBlank()}")
        buildConfigField("boolean", "ANALYTICS_ENABLED", "${postHogApiKey.isNotBlank()}")
        buildConfigField("String", "REVENUECAT_API_KEY", "\"$revenueCatApiKey\"")
        buildConfigField("String", "POSTHOG_API_KEY", "\"$postHogApiKey\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":core"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material.icons.core)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.revenuecat.purchases)
    implementation(libs.posthog.android)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
