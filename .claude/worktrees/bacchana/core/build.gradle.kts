// Pure JVM module: game logic with zero Android dependency, runnable with `./gradlew :core:test`.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(libs.junit)
}
