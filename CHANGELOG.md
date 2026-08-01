# Changelog

All notable changes to BlackOut Android are documented here.
Format loosely follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
versioning follows [SemVer](https://semver.org/).

## [0.1.0] - 2026-08-01

### Added
- `:core` module (pure Kotlin JVM, no Android dependency): Le Borderland card
  engine (deck, contest escalation, penalty calculator), generic `PromptSession`
  engine (no-repeat draw pile, persistent/role rules, `{player}`/`{player2}`
  interpolation). 51 JUnit tests, runnable with `./gradlew :core:test`.
- `:app` module (Jetpack Compose, Material 3): Welcome (player check-in), Hub
  (mode grid), Borderland (card game screen), Prompt (generic turn screen),
  Recap (podium). Dark-only Neo-Tokyo Borderland theme from
  `blackout-content/tokens/tokens.json`.
- Bundled 7 free content packs (picolo, action-verite, tu-preferes, never,
  qui-de-nous, c'est-un-10, 7-secondes) as offline assets; premium packs ship
  as metadata-only catalog entries pending Play Billing integration.
- `EntitlementRepository` and `AnalyticsTracker` interfaces with safe no-op
  stub implementations, gated behind `BuildConfig.BILLING_ENABLED` /
  `BuildConfig.ANALYTICS_ENABLED` for future RevenueCat / PostHog wiring.
- `scripts/sync_content.py`: reproducible content sync from `blackout-content`.
- GitHub Actions CI: `:core:test`, `:app:assembleDebug`, `:app:lint`.

### Store safety
- Zero alcohol references anywhere in code, strings or content. Unit is
  "pénalité", Ace is "PÉNALITÉ MAJEURE". Rating target: 18+, in-app disclaimer
  "18 ans et plus. Jouez responsable."
