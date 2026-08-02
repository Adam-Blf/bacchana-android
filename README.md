<!-- adam-badges:start -->
[![commits](https://img.shields.io/github/commit-activity/t/Adam-Blf/la-taverne-android?color=001329&label=commits&style=flat-square)](https://github.com/Adam-Blf/la-taverne-android/commits)
[![version](https://img.shields.io/badge/version-0.2.0-D4A437?style=flat-square)](CHANGELOG.md)
[![platform](https://img.shields.io/badge/platform-Android%208.0%2B-001329?style=flat-square)](#)
[![kotlin](https://img.shields.io/badge/kotlin-2.0.21-7F52FF?style=flat-square)](#)
[![release](https://img.shields.io/github/actions/workflow/status/Adam-Blf/la-taverne-android/release.yml?label=release&style=flat-square)](RELEASING.md)
[![license](https://img.shields.io/github/license/Adam-Blf/la-taverne-android?style=flat-square&color=D4A437)](LICENSE)
<!-- adam-badges:end -->

# La Taverne - Android

Jeu de soirée entre potes, natif Android, DA néobrutaliste taverne : papier
crème, encre, accent orange #FF5C00, ombres dures noires, thème clair par
défaut. Le Coupe-Gorge
(jeu de cartes 52 cartes avec système de contestation escaladable) et une
poignée de modes de prompts tour par tour (Le Meneur, Action ou Vérité,
Tu préfères, Je n'ai jamais, Qui de nous, 7 secondes, C'est un 10 mais).

Store-safe par construction : zéro référence à l'alcool dans le code, les
chaînes ou le contenu. L'unité de jeu est la "pénalité", l'As déclenche une
"PÉNALITÉ MAJEURE". Rating cible 18+, disclaimer "18 ans et plus. Jouez
responsable." affiché à l'accueil.

## Architecture

```mermaid
flowchart TD
    subgraph core[":core - Kotlin JVM pur, zero dependance Android"]
        Deck[Deck / Card / Player]
        Contest[ContestState / PenaltyCalculator]
        Engine[BorderlandEngine - reducer pur]
        Prompt[PromptSession - pile sans repetition]
        Interp[interpolate - player / player2]
    end

    subgraph content["la-taverne-content (repo separe, source de verite)"]
        Packs[content/fr/packs/*.json]
        Tokens[tokens/tokens.json]
    end

    Sync[scripts/sync_content.py] -->|copie packs premium=false| Assets
    Packs --> Sync
    Sync -->|metadonnees seules| PremiumCatalog[assets/premium-catalog.json]

    subgraph app[":app - Jetpack Compose, Material 3"]
        Assets[assets/packs/*.json]
        Repo[PackRepository]
        VM[ViewModels: Borderland / Prompt / PlayerSession]
        UI[Welcome / Hub / Borderland / Prompt / Recap]
        Store[PlayerStore - DataStore]
        Billing[EntitlementRepository - stub, RevenueCat TODO]
        Analytics[AnalyticsTracker - stub, PostHog TODO]
    end

    Assets --> Repo
    PremiumCatalog --> Repo
    Repo --> VM
    core --> VM
    VM --> UI
    Store --> UI
    Billing -.gated by BuildConfig.BILLING_ENABLED.-> UI
    Analytics -.gated by BuildConfig.ANALYTICS_ENABLED + consentement.-> UI
    Tokens -.source des couleurs Compose.-> UI
```

- **`:core`** est un module Kotlin JVM pur (aucune dépendance Android) : le
  moteur du Coupe-Gorge (deck, escalade de contestation, calcul de pénalité) et
  le moteur générique de prompts (`PromptSession`, tirage sans répétition,
  règles persistantes/rôles, interpolation `{player}`/`{player2}`). Testable
  avec `./gradlew :core:test`, sans SDK Android.
- **`:app`** est le module Android (Jetpack Compose, Material 3, thème clair
  néobrutaliste taverne calqué sur `la-taverne-content/tokens/tokens.json` v2 :
  fond crème #FFF9F0, encre #111111, accent orange #FF5C00, ombres dures).
- Le contenu (packs de prompts FR) vit dans le repo séparé `la-taverne-content`
  et est synchronisé par `scripts/sync_content.py` : les packs gratuits sont
  copiés tels quels dans `assets/packs/`, les packs premium ne livrent que
  leurs métadonnées (`assets/premium-catalog.json`), jamais le texte des
  prompts, en attendant l'intégration Play Billing / RevenueCat.

## Build

Prérequis : JDK 21, Android SDK (platform 35, build-tools 35.0.0).

```bash
# Resynchroniser le contenu depuis ../la-taverne-content (idempotent)
python scripts/sync_content.py

# Tests du moteur de jeu (JVM pur, pas de SDK Android nécessaire)
./gradlew :core:test

# Build de l'APK debug
./gradlew :app:assembleDebug

# Lint
./gradlew :app:lint
```

`local.properties` (non versionné) doit pointer `sdk.dir` vers ton
installation du SDK Android.

## Conformité Play Store

- **Rating cible** : 18+ (thème soirée entre adultes, pas de contenu explicite).
- **Zéro référence alcool** : le jeu distribue des "pénalités" abstraites,
  jamais de gorgées/shots nommés. Le groupe décide en vrai ce qu'une pénalité
  vaut - c'est ce qui rend l'app publiable sans violer les policies Google
  Play sur le contenu lié à l'alcool.
- **Disclaimer** "18 ans et plus. Jouez responsable." affiché dès l'écran
  d'accueil.
- **Paiement in-app (à venir)** : `EntitlementRepository` est prêt côté
  architecture (interface + stub `isPremium = false`), l'intégration réelle
  RevenueCat/Play Billing est gated derrière `BuildConfig.BILLING_ENABLED` et
  sera livrée dans une version ultérieure avec CGV/mentions légales à jour
  (voir CLAUDE.md section 18 - conformité droit français, retractation 14j
  B2C, facturation conforme).
- **Analytics (à venir)** : `AnalyticsTracker` est un no-op tant qu'aucun
  consentement RGPD explicite (non pré-coché) n'est recueilli.
- **Restes avant soumission** : icône monochrome adaptative (Android 13+
  themed icons, actuellement non fournie - lint `MonochromeLauncherIcon`),
  fiche Play Console (captures d'écran, description longue/courte, politique
  de confidentialité publiée), signature de release (keystore, jamais commité
  - voir `.gitignore`), tests sur device physique.

## Stack

Kotlin 2.0.21, Jetpack Compose (BOM 2024.10.01), Material 3, Navigation
Compose, kotlinx.serialization, DataStore Preferences, coroutines. Min SDK 26,
target SDK 35. Polices auto-hébergées (aucun CDN) : Anton (display), Space
Grotesk (corps), Space Mono (chiffres/HUD) - jamais JetBrains Mono ni IBM
Plex Mono.

## Tests

`:core` embarque 51 tests JUnit couvrant le deck (52 cartes uniques, As =
pénalité majeure), les multiplicateurs de contestation `{0:1, 1:1, 2:2, 3:4}`,
la rotation des joueurs actifs, le moteur `BorderlandEngine` (reducer pur,
transitions de phase), l'interpolation `{player}`/`{player2}` et
`PromptSession` (tirage sans répétition, règles persistantes avec expiration,
rôles, ciblage `chosen`).
