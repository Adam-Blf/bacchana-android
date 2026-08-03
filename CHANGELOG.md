# Changelog

All notable changes to La Taverne Android are documented here.
Format loosely follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
versioning follows [SemVer](https://semver.org/).

## [0.7.0] - 2026-08-03

### Added
- Mode "Le Tableau d'Honneur" (ranking, 5e et dernier mode) : 40 questions de
  classement embarquées (`core/RankingContent.kt`, mirroir de
  `la-taverne/src/content/ranking.ts`), un juge tourne à chaque manche et
  découvre en secret une question de classement (`RankingPhase.JUDGING`),
  classe les autres joueurs dans l'ordre des taps (exclut le juge). La table
  ne voit ensuite que le podium résultant, jamais la question, et doit
  deviner laquelle des 4 propositions mélangées (3 leurres distincts + la
  vraie) l'a produit. Bonne réponse : le juge prend `JUDGE_PENALTY` (3)
  pénalités ; mauvaise réponse : chaque joueur non-juge prend
  `GROUP_PENALTY` (1) chacun - pénalités asymétriques fidèles au moteur web.
  Moteur pur et testable `core/RankingSession.kt`
  (`RankingSessionState` immuable + fonctions réductrices
  `startJudging`/`toggleRanked`/`confirmRanking`/`startGuessing`/
  `guessQuestion`/`nextRound`, 19 tests unitaires couvrant l'invariant
  "tous classés" avant confirmation, les deux branches de pénalité, la
  rotation du juge avec wrap-around, la fin de file et le déterminisme du
  `Random` injecté), aucune mutation directe de `Player.penaltiesStandard` -
  le tally de la session vit dans un récap interne au mode (`RankingRecap`
  dans `RankingScreen.kt`), pas dans `RecapScreen`. Tuile "Le Tableau
  d'Honneur" ajoutée au hub (4 joueurs min. - juge + au moins 3 classés),
  route `ranking` dans le NavHost, `session_completed { mode: "ranking",
  turns }` tracké à la sortie.

## [0.6.0] - 2026-08-03

### Added
- Mode "Quitte ou Trinque" (quiz) : 60 questions de culture générale
  embarquées (`core/QuizContent.kt`, mirroir de
  `la-taverne/src/content/quiz.ts`, 6 catégories), la file est mélangée à
  la création puis chaque question vaut 1 à 3 points tirés au hasard. Bonne
  réponse : les points rejoignent la cagnotte du joueur, qui choisit
  ensuite - cumuler (quitte ou double au tour suivant) ou distribuer (la
  cagnotte part en pénalités pour la table, gloire pour le distributeur,
  jamais comptée dans ses propres pénalités). Mauvaise réponse : le joueur
  prend sa cagnotte + les points de la question. Moteur pur et testable
  `core/QuizSession.kt` (`QuizSessionState` immuable + fonctions réductrices
  `answerCorrect`/`answerWrong`/`distributePot`/`keepPot`, 18 tests
  unitaires couvrant les 4 transitions, le re-tirage des points à chaque
  tour, la fin de file et le déterminisme du `Random` injecté), aucune
  mutation directe de `Player.penaltiesStandard` - le tally de la session
  vit dans un récap interne au mode (`QuizRecap` dans `QuizScreen.kt`), pas
  dans `RecapScreen`. Tuile "Quitte ou Trinque" ajoutée au hub (2 joueurs
  min.), route `quiz` dans le NavHost, `session_completed { mode: "quiz",
  turns }` tracké à la sortie.

## [0.5.0] - 2026-08-03

### Added
- Mode "La Criée" (auction) : un thème est tiré (`core/AuctionContent.kt`,
  mirroir de `la-taverne/src/content/auction.ts`, 50 thèmes), la table
  surenchérit à voix haute jusqu'à ce que quelqu'un crie « tu mens ! ».
  Le dernier enchérisseur a alors 60 secondes (`CHALLENGE_SECONDS`) pour
  citer son compte, décompte porté par une coroutine `LaunchedEffect`
  annulée proprement à chaque changement de phase - échec automatique si
  le temps expire. Réussite (autant de citations que l'enchère) : c'est
  l'accusateur qui prend les pénalités ; échec : c'est l'enchérisseur.
  Aucun récap, aucun joueur nommé, `Player` jamais muté - seul le nombre
  de manches (`roundsPlayed`) est suivi pour clôturer la session. Tuile
  "La Criée" ajoutée au hub (2 joueurs min.), route `auction` dans le
  NavHost, `session_completed { mode: "auction", turns }` tracké à la
  sortie.

## [0.4.0] - 2026-08-03

### Added
- Mode "Le Pilori" (tribunal) : chaque joueur actif écrit une accusation
  secrète contre la table (ou la table joue avec les 10 chefs
  d'accusation embarqués de `core/TribunalContent.kt`, mirroir de
  `la-taverne/src/content/tribunal.ts`). Un procès tire une accusation au
  hasard, désigne un accusé au hasard en excluant l'auteur, puis passe
  par une phase de défense informative avant le vote coupable/non
  coupable. Une majorité de votes coupables ajoute une pénalité (+1
  plat, pas de `calculatePenalty` Borderland) à l'accusé. Moteur pur et
  testable `core/TribunalSession.kt` (`TribunalState` immuable +
  `TribunalEngine` reducer, 15 tests unitaires), aucune mutation directe
  de `Player.penaltiesStandard` - le tally de la session vit dans un
  récap interne au mode (`TribunalRecap` dans `TribunalScreen.kt`), pas
  dans `RecapScreen`. Tuile "Le Pilori" ajoutée au hub (3 joueurs min.),
  route `tribunal` dans le NavHost, `session_completed { mode:
  "tribunal", turns }` tracké à la sortie.

## [0.3.0] - 2026-08-03

### Added
- Mode "La Roue du Destin" (roulette) : roue à 8 segments (pénalités
  abstraites, jamais d'alcool nommé), sans joueur nommé et sans récap.
  Animation de rotation ~3.2s (easing casino, ~5 tours + offset vers le
  centre du segment tiré), dégradée en position figée si l'utilisateur a
  désactivé les animations système (`ANIMATOR_DURATION_SCALE`).
  `core/RouletteContent.kt` mirrore `la-taverne/src/content/roulette.ts`.
  Tuile "La Roue du Destin" ajoutée au hub, route `roulette` dans le
  NavHost, `session_completed { mode: "roulette", turns }` tracké à la
  sortie.

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
