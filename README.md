<!-- adam-badges:start -->
[![commits](https://img.shields.io/github/commit-activity/t/Adam-Blf/la-taverne-android?color=001329&label=commits&style=flat-square)](https://github.com/Adam-Blf/la-taverne-android/commits)
[![version](https://img.shields.io/badge/version-0.12.0-D4A437?style=flat-square)](CHANGELOG.md)
[![platform](https://img.shields.io/badge/platform-Android%208.0%2B-001329?style=flat-square)](#)
[![kotlin](https://img.shields.io/badge/kotlin-2.0.21-7F52FF?style=flat-square)](#)
[![release](https://img.shields.io/github/actions/workflow/status/Adam-Blf/la-taverne-android/release.yml?label=release&style=flat-square)](RELEASING.md)
[![license](https://img.shields.io/github/license/Adam-Blf/la-taverne-android?style=flat-square&color=D4A437)](LICENSE)
<!-- adam-badges:end -->

# La Taverne - Android

Jeu de soirée entre potes, natif Android, DA néobrutaliste taverne : papier
crème, encre, accent orange #FA5600, ombres dures noires, thème clair par
défaut avec une variante sombre "pop" sur encre neutre (jamais de brun/bois,
bascule discrète clair/sombre/système dans le hub). Chaque joueur peut
préciser à l'inscription, de façon facultative et repliée par défaut, son
genre et son statut relationnel (100 % local, jamais envoyé en analytics) -
utilisés uniquement pour cibler certains contenus (ex. "c'est au tour d'un
homme/d'une femme/d'un célibataire/d'un couple"), avec repli gracieux sur un
joueur aléatoire si personne n'a rien renseigné. Le Coupe-Gorge
(jeu de cartes 52 cartes avec système de contestation escaladable), une
poignée de modes de prompts tour par tour (Le Taulier, Action ou Vérité,
Je n'ai jamais, Qui de nous, 7 Secondes, C'est un 10 mais),
La Roue du Destin (roulette à 40 segments, sans joueur nommé, sans récap),
Le Pilori (tribunal à 40 chefs d'accusation : écrits par la table ou tirés
de l'app, procès aléatoire, verdict à la majorité, récap interne au mode), La Criée
(enchère à voix haute, défi chronométré 60s, sans joueur nommé, sans récap)
Quitte ou Trinque (quiz de 60 questions, cagnotte à 1-3 points, choix
cumuler/distribuer après chaque bonne réponse, récap interne au mode),
Le Tableau d'Honneur (un juge classe la table sur une question secrète de
40, la table devine la question parmi 4 propositions sans jamais la voir,
pénalités asymétriques 3 pour le juge / 1 pour le groupe, récap interne au
mode) et Tu préfères (84 dilemmes A ou B embarqués, chaque joueur actif
vote son camp le téléphone au centre de la table, le camp minoritaire au
reveal prend 1 pénalité chacun, égalité parfaite ou vote unanime = personne
ne trinque, récap interne au mode).

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
        Tribunal[TribunalEngine - reducer pur, penaltyCounts local]
        Auction[AuctionContent - 50 themes embarques]
        Quiz[QuizSession - reducer pur, 60 questions embarquees]
        Ranking[RankingSession - reducer pur, 40 questions embarquees]
        WYR[WouldYouRatherSession - reducer pur, vote, 84 dilemmes embarques]
        Targeting[Targeting.kt - resolveTarget genre/statut/paire, Random injectable]
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
        UI[Welcome / Hub / Borderland / Prompt / Roulette / Tribunal / Auction / Quiz / Ranking / WouldYouRather / Recap]
        Store[PlayerStore - DataStore]
        Consent[ConsentStore - DataStore, opt-in RGPD]
        ThemeStoreNode[ThemeStore - DataStore, clair/sombre/systeme]
        Billing[EntitlementRepository - Stub ou RevenueCatEntitlementRepository]
        Analytics[AnalyticsTracker - NoOp ou PostHogAnalyticsTracker]
        Paywall[PaywallScreen - 3 offres, restaurer les achats]
    end

    subgraph external["Services externes (gated par cle API)"]
        RC[RevenueCat - entitlement "La Taverne Pro"]
        PH[PostHog EU - eu.i.posthog.com]
    end

    Assets --> Repo
    PremiumCatalog --> Repo
    Repo --> VM
    core --> VM
    VM --> UI
    Store --> UI
    ThemeStoreNode --> UI
    Consent --> Analytics
    UI --> Paywall
    Paywall --> Billing
    Billing -.BuildConfig.BILLING_ENABLED = cle RevenueCat presente.-> RC
    Analytics -.BuildConfig.ANALYTICS_ENABLED = cle PostHog presente ET consentement.-> PH
    Tokens -.source des couleurs Compose, clair + sombre.-> UI
```

- **`:core`** est un module Kotlin JVM pur (aucune dépendance Android) : le
  moteur du Coupe-Gorge (deck, escalade de contestation, calcul de pénalité) et
  le moteur générique de prompts (`PromptSession`, tirage sans répétition,
  règles persistantes/rôles, interpolation `{player}`/`{player2}`), et le
  reducer immuable `TribunalEngine`/`TribunalState` du Pilori (accusations,
  tirage de l'accusé en excluant l'auteur, verdict à la majorité), et les
  fonctions réductrices `QuizSession` de Quitte ou Trinque (cagnotte à 1-3
  points, choix cumuler/distribuer, `Random` injectable), le reducer
  `RankingSession` du Tableau d'Honneur (rotation du juge, classement en
  secret, pénalités asymétriques juge/groupe, `Random` injectable), et le
  reducer `WouldYouRatherSession` de Tu préfères (file de dilemmes mélangée,
  vote `castVote`/`revealVotes`/`nextRound`, camp minoritaire pénalisé sauf
  égalité ou unanimité, `Random` injectable), et `Targeting.kt` qui résout la
  cible d'un item de contenu (`Targets.GENDER_M/GENDER_F/SINGLE/COUPLE/PAIR`)
  vers un ou deux `Player` concrets à partir de leurs attributs `gender`/
  `relationship` optionnels, avec repli aléatoire gracieux si personne ne
  correspond (`Random` injectable, déterministe par tour via `seededRandom`).
  Testable avec `./gradlew :core:test`, sans SDK Android.
- **`:app`** est le module Android (Jetpack Compose, Material 3, thème
  néobrutaliste taverne calqué sur `src/styles/tokens.css` du web (source de
  vérité) : clair par défaut (fond crème #FFF9F0, encre #111111, accent orange
  #FA5600, ombres dures) avec une variante sombre "pop" sur encre neutre
  #141216 (jamais de brun/bois), bascule clair/sombre/système persistée par
  `ThemeStore` et exposée par un toggle discret dans le hub. Chaque écran lit
  `LaTaverneColors.*` (propriétés `@Composable`, mêmes 300+ sites d'appel
  qu'avant, aucun refactor visuel nécessaire) qui résout la palette courante
  via un `CompositionLocal` fourni par `LaTaverneTheme`.
- Le contenu (packs de prompts FR) vit dans le repo séparé `la-taverne-content`
  et est synchronisé par `scripts/sync_content.py` : les packs gratuits sont
  copiés tels quels dans `assets/packs/`, les packs premium ne livrent que
  leurs métadonnées (`assets/premium-catalog.json`), jamais le texte des
  prompts, en attendant l'intégration Play Billing / RevenueCat.

Détail complet des couches, du flux de contenu et du principe gated dans
[`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

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
installation du SDK Android. Copie `local.properties.example` vers
`local.properties` pour renseigner en plus `REVENUECAT_API_KEY` et
`POSTHOG_API_KEY` en local - les deux sont optionnelles, l'app tourne en
mode invité sans elles (build, tests et CI ne les ont jamais).

## Conformité Play Store

- **Rating cible** : 18+ (thème soirée entre adultes, pas de contenu explicite).
- **Zéro référence alcool** : le jeu distribue des "pénalités" abstraites,
  jamais de gorgées/shots nommés. Le groupe décide en vrai ce qu'une pénalité
  vaut - c'est ce qui rend l'app publiable sans violer les policies Google
  Play sur le contenu lié à l'alcool.
- **Disclaimer** "18 ans et plus. Jouez responsable." affiché dès l'écran
  d'accueil.
- **Paiement in-app** : `RevenueCatEntitlementRepository` encapsule le SDK
  Purchases (entitlement `La Taverne Pro`, 3 offres - mensuel 4,99 €, annuel
  19,99 €, à vie 34,99 € mis en avant - transparence tarifaire totale, aucun
  essai gratuit trompeur). Actif uniquement si `BuildConfig.BILLING_ENABLED`
  est vrai, c'est-à-dire si une clé RevenueCat (`REVENUECAT_API_KEY`) est
  configurée dans `local.properties` ou en variable d'env - sinon
  `StubEntitlementRepository` prend le relais (mode invité, bouton d'achat
  désactivé "Bientôt disponible"). CI sans clé = toujours en mode invité,
  jamais de crash. CGV/mentions légales à jour restent à publier avant mise
  en vente réelle (voir CLAUDE.md section 18 - conformité droit français,
  rétractation 14j B2C, facturation conforme).
- **Analytics** : `PostHogAnalyticsTracker` (instance EU,
  `eu.i.posthog.com`) n'est sélectionné que si `BuildConfig.ANALYTICS_ENABLED`
  est vrai (`POSTHOG_API_KEY` configurée) ; sinon `NoOpAnalyticsTracker`.
  Même avec une clé, le SDK PostHog n'est jamais initialisé - donc aucun
  appel réseau - tant que le joueur n'a pas explicitement accepté la bannière
  de consentement (`ConsentBanner` + `ConsentStore`, boutons accepter/refuser
  à égalité visuelle, refus jamais pré-coché).
- **Contenu premium livré après achat** : reste un chantier séparé - le
  catalogue premium n'expose aujourd'hui que ses métadonnées
  (`assets/premium-catalog.json`), jamais le texte des prompts. Les tuiles
  premium du hub ouvrent le paywall ; la synchronisation du contenu réel
  après achat (`scripts/sync_content.py`) n'est pas dans ce lot.
- **Restes avant soumission** : icône monochrome adaptative (Android 13+
  themed icons, actuellement non fournie - lint `MonochromeLauncherIcon`),
  fiche Play Console (captures d'écran, description longue/courte, politique
  de confidentialité publiée), signature de release (keystore, jamais commité
  - voir `.gitignore`), tests sur device physique, provisioning réel des
  produits RevenueCat (`premium_monthly`/`premium_yearly`/`premium_lifetime`)
  et de l'entitlement `La Taverne Pro` côté dashboard.

## Stack

Kotlin 2.0.21, Jetpack Compose (BOM 2024.10.01), Material 3, Navigation
Compose, kotlinx.serialization, DataStore Preferences, coroutines. RevenueCat
Purchases SDK (Android, `8.+`) et PostHog Android (`3.+`) pour la
monétisation/analytics, tous deux gated derrière une clé API absente en CI.
Min SDK 26, target SDK 35. Polices auto-hébergées (aucun CDN) : Anton
(display), Space Grotesk (corps), Space Mono (chiffres/HUD) - jamais
JetBrains Mono ni IBM Plex Mono.

## Tests

`:core` embarque 113 tests JUnit couvrant le deck (52 cartes uniques, As =
pénalité majeure), les multiplicateurs de contestation `{0:1, 1:1, 2:2, 3:4}`,
la rotation des joueurs actifs, le moteur `BorderlandEngine` (reducer pur,
transitions de phase), l'interpolation `{player}`/`{player2}`,
`PromptSession` (tirage sans répétition, règles persistantes avec expiration,
rôles, ciblage `chosen`), `TribunalEngine`, `QuizSession`, `RankingSession`
du Tableau d'Honneur (invariant "tous classés" avant confirmation, pénalités
asymétriques juge/groupe, rotation du juge avec wrap-around, déterminisme du
`Random` injecté), `WouldYouRatherSession` de Tu préfères (camp minoritaire
pénalisé, égalité et unanimité neutres, cumul multi-manches, unicité du vote
par joueur, fin de file, déterminisme du `Random` injecté), `PremiumPlan`
(mapping id produit RevenueCat -> offre, y compris les suffixes de base plan
Play Store, et l'activation de l'entitlement `La Taverne Pro`) et
`Targeting.kt` (cibles résolvables reconnues, correspondance genre/statut/
paire, repli aléatoire gracieux quand personne ne correspond ou que le
roster est vide, joueurs inactifs ignorés, déterminisme par seed de tour).
