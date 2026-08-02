# Changelog

All notable changes to La Taverne Android are documented here.
Format loosely follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
versioning follows [SemVer](https://semver.org/).

## [0.2.0] - 2026-08-02

### Changed
- Refonte complète de la direction artistique : abandon du thème sombre
  Neo-Tokyo au profit de la DA néobrutaliste taverne (tokens.json v2), avec
  thème clair par défaut - fond papier crème #FFF9F0, encre #111111, accent
  orange #FF5C00, ombres dures noires, aucun halo néon.
- `Color.kt` et `colors.xml` alignés sur la palette light de
  `la-taverne-content/tokens/tokens.json` v2 (surfaces blanches, premium
  #A87718, success #1B8A5A, warning #B45309, objets de jeu fixes : carte
  blanche #FFFFFF, encre carte #111111, rouge carte #E5323E).
- `Theme.kt` passe de `darkColorScheme` à `lightColorScheme`, barre de statut
  claire (`windowLightStatusBar` activé), splash sur fond crème.
- Icône de lanceur adaptative rethémée : fond crème plat avec bande orange
  (plus de dégradé néon), carte blanche et pique encre inchangés. Les vecteurs
  sont édités à la main (aucun script de génération d'icône dans ce repo).
- Le jeu de cartes "Le Borderland" est renommé "Le Coupe-Gorge" dans toutes
  les chaînes visibles. Les identifiants techniques (routes, clés de strings,
  enums, classes `BorderlandEngine`) restent inchangés.
- Version 0.2.0 (versionCode 2), README et badge de version mis à jour.

## [0.1.0] - 2026-08-01

### Added
- `:core` module (pure Kotlin JVM, no Android dependency): Le Borderland card
  engine (deck, contest escalation, penalty calculator), generic `PromptSession`
  engine (no-repeat draw pile, persistent/role rules, `{player}`/`{player2}`
  interpolation). 51 JUnit tests, runnable with `./gradlew :core:test`.
- `:app` module (Jetpack Compose, Material 3): Welcome (player check-in), Hub
  (mode grid), Borderland (card game screen), Prompt (generic turn screen),
  Recap (podium). Dark-only Neo-Tokyo Borderland theme from
  `la-taverne-content/tokens/tokens.json`.
- Bundled 7 free content packs (picolo, action-verite, tu-preferes, never,
  qui-de-nous, c'est-un-10, 7-secondes) as offline assets; premium packs ship
  as metadata-only catalog entries pending Play Billing integration.
- `EntitlementRepository` and `AnalyticsTracker` interfaces with safe no-op
  stub implementations, gated behind `BuildConfig.BILLING_ENABLED` /
  `BuildConfig.ANALYTICS_ENABLED` for future RevenueCat / PostHog wiring.
- `scripts/sync_content.py`: reproducible content sync from `la-taverne-content`.
- GitHub Actions CI: `:core:test`, `:app:assembleDebug`, `:app:lint`.

### Store safety
- Zero alcohol references anywhere in code, strings or content. Unit is
  "pénalité", Ace is "PÉNALITÉ MAJEURE". Rating target: 18+, in-app disclaimer
  "18 ans et plus. Jouez responsable."
