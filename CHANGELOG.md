# Changelog

## [0.14.1] - 2026-08-05

- Fix contraste WCAG texte-sur-aplat (theme sombre) : signale deux fois par
  Adam en jouant ("du blanc sur du jaune c'est illisible, du blanc sur du
  vert clair c'est illisible"), jamais porte depuis le fix web du
  2026-08-04. Cause racine : `MeskovaColors.Ink` s'inverse avec le theme
  (encre foncee en clair, quasi blanche en sombre) alors que les aplats
  `Pop*`/`Neon*` restent CLAIRS dans les deux themes - du texte `Ink` pose
  dessus tombait a ~1.2:1 en sombre (roue de la roulette, badges Quitte ou
  Trinque, cartes Le Tableau d'Honneur/Le Pilori, icone d'ajout de joueur).
  Deux nouveaux jetons theme-invariants portent la meme logique que sur le
  web (`docs/DESIGN_TOKENS.md` du repo `la-taverne`) :
  - `MeskovaColors.TileInk` (`#111111` fixe) pour tout texte/icone pose
    sur `PopYellow`/`PopPink`/`PopBlue`/`PopLime`/`Neon`/`NeonDeep`/
    `NeonSoft` - remplace aussi `CardFace` (blanc) comme couleur de texte
    par defaut des boutons primaires (`Theme.kt` `onPrimary`), qui ne
    passait que 3.28:1 en clair et 2.60:1 en sombre sur un fond `Neon`.
  - `MeskovaColors.OnStatus` pour le texte pose sur `Premium`/`Success`/
    `Warning`/`Danger` (direction inverse de `Bg` entre les deux themes :
    blanc en clair, `TileInk` en sombre) - corrige les icones des
    steppers de La Criee et le resultat d'enchere.
  - `MeskovaColors.CardAccent` (`#C74300` fixe) pour le label orange de Tu
    preferes, pose sur la face de carte blanche fixe (`Neon` y tombait a
    2.60:1 en sombre).
  Rampe d'elevation du theme sombre mise a jour vers la refonte 2026-08-04
  (`BgRaised #221E28`, `Surface #2E2836`, `SurfaceElevated #3C3446`,
  `InkMuted #958FA3`, alpha de bordure fine `0.20` -> `0.38`) - alignee
  avec `docs/DESIGN_TOKENS.md` du repo web.
  Nouveau module de tokens purs Kotlin dans `core` (`core/.../theme/`,
  `PaletteColor`, `WcagContrast`, `MeskovaPalette`) : source de verite
  unique consommee a la fois par le rendu Compose
  (`app/.../ui/theme/Color.kt`) et par la garde mecanique
  `MeskovaPaletteContrastTest` (`./gradlew :core:test`, 136 tests), qui
  calcule le ratio de contraste WCAG 2.1 reel de chaque paire encre/fond
  utilisee dans le theme et echoue si une paire tombe sous 4.5:1 (texte
  normal) ou 3:1 (texte large/objets UI) - derive des memes objets
  `MeskovaPalette.Light`/`MeskovaPalette.Dark` que le rendu, pas d'une
  liste de hex recopies a la main.

## [0.14.0] - 2026-08-04

- Rebranding produit : "La Taverne" devient "Meskova" comme nom d'application
  (`app_name`, titre du paywall "Meskova Premium", texte au dos des cartes du
  Coupe-Gorge, README, docs, ProGuard rules). L'univers interne du jeu reste
  intact - aucun nom de mode, aucun texte de contenu, aucune règle ne change
  (Le Taulier, Le Pilori, La Criée, etc. gardent leurs libellés).
  `applicationId`/`namespace` passent de `com.beloucif.lataverne` à
  `com.beloucif.meskova` (arborescence de packages Kotlin et imports
  renommés en conséquence) - l'app n'étant pas encore publiée sur le Play
  Store, ce changement ne casse aucun utilisateur existant. Les noms de
  fichiers DataStore locaux (`lataverne_consent`/`lataverne_theme`/
  `lataverne_players`) sont renommés en `meskova_*` sans script de
  migration : l'app n'ayant jamais été publiée, il n'existe aucune
  installation avec des données à préserver.
  L'identifiant d'entitlement RevenueCat `"La Taverne Pro"`
  (`core/PremiumPlan.kt`) est volontairement **conservé à l'identique** -
  un identifiant d'entitlement RevenueCat n'est pas renommable côté
  dashboard sans migration, voir le commentaire dans le code. Le nom du
  repo GitHub (`la-taverne-android`), le nom du module Gradle
  (`rootProject.name`) et les références au repo de contenu séparé
  (`la-taverne-content`) restent inchangés - hors périmètre de ce lot.
  Les URLs `PRIVACY_POLICY_URL`/`LEGAL_URL` de `SettingsScreen.kt`
  continuent de pointer vers `lataverne.beloucif.com` en attendant la
  coordination avec le rebrand du site web/domaine (chantier séparé).

## [0.13.0] - 2026-08-04

- Ecran Reglages natif (`ui/screens/SettingsScreen.kt`, route `settings`,
  parite avec `SettingsScreen.tsx` sur le web) : apparence (bascule
  clair/sombre reutilisant `ThemeStore`), premium (statut, ouverture du
  paywall, "Restaurer mes achats" via `EntitlementRepository.restorePurchases()`),
  confidentialite (interrupteur consentement PostHog reutilisant
  `ConsentStore`, lien politique de confidentialite), legal (lien vers les
  pages web - pas de mentions/CGU/confidentialite natives sur Android pour
  le moment), a propos (`BuildConfig.VERSION_NAME`, editeur Adam Beloucif /
  BLF Labs), reinitialisation de la tablee (`PlayerSessionViewModel.resetAll()`,
  confirmation obligatoire, ne touche jamais au premium). Accessible depuis
  une icone engrenage discrete dans le hub (44dp, contentDescription).
  Zero duplication : chaque section delegue a un store ou repository
  existant, zero attribut perso ajoute a PostHog.
- Paywall (`PaywallScreen.kt`) : titre "La Taverne Premium" repasse en
  orange accent (`MeskovaColors.Neon`) au lieu de l'encre - en encre le
  titre devenait noir sur bordure noire en clair et blanc sur fond
  quasi-blanc en sombre, illisible dans les deux cas. Notes et compteurs
  de la modale (libelle "Contenu inclus", nombre de cartes par pack,
  paragraphe de transparence abonnements) recolores en `InkSecondary` au
  lieu de `InkMuted`, insuffisamment contraste sur `SurfaceElevated` en
  sombre - alignement sur `PremiumPaywallModal.tsx` (`text-ink-secondary`)
  qui utilisait deja ce ton.

## [0.12.0] - 2026-08-03

- Refonte DA : theme sombre "pop" ajoute sur encre neutre (jamais de brun/
  bois, fond #141216, accent orange eclairci #FF7A2E >= 7:1, aplats pop
  vibrants), corrections a11y du theme clair (orange-texte assombri
  #C74300, premium #855C12, card-red #C71F2D, tous >= AA 4.5:1). Palette
  Compose (`ui/theme/Color.kt`) alignee au token par token sur
  `src/styles/tokens.css` du web (source unique). `MeskovaColors`
  reste un `object` a l'usage identique (300+ sites d'appel `MeskovaColors.X`
  inchanges) mais lit desormais un `CompositionLocal` fourni par
  `MeskovaTheme`, qui accepte une preference clair/sombre/systeme
  persistee par le nouveau `ThemeStore` (DataStore) et exposee par un
  toggle discret dans le hub.
- Genre et statut relationnel par joueur (feature #54, parite avec
  `feat/player-attributes` sur le web) : `Player.gender`/`Player.relationship`
  optionnels dans `:core`, panneau facultatif et replie par defaut sur
  l'ecran d'inscription (chips 44dp, contentDescription explicites),
  100 % local - zero attribut envoye a PostHog (verifie dans
  `analytics/`, aucun event ne les inclut). Nouveau `Targeting.kt` dans
  `:core` (`resolveTarget`, `Random` injectable, deterministe par tour)
  resout `Targets.GENDER_M/GENDER_F/SINGLE/COUPLE/PAIR` vers un ou deux
  joueurs concrets avec repli aleatoire gracieux, branche sur
  `PromptScreen` ("C'est a X de jouer"). 13 tests JUnit ajoutes
  (`TargetingTest.kt`).

## [0.11.1] - 2026-08-03

- Ajout de `docs/ARCHITECTURE.md` : diagramme Mermaid a jour de l'app (modules
  `:core` / `:app`, couches transverses billing et analytics gated, flux de
  contenu synchronise depuis la-taverne-content et contenu embarque natif),
  plus une explication de chaque couche et du pattern moteur pur teste. Lien
  ajoute depuis la section Architecture du README. Documentation seule, aucun
  changement de code.

## [0.11.0] - 2026-08-03

- Contenu roulette et pilori porte a parite avec le web (la-taverne) : 40
  segments dans `RouletteContent.kt` (8 penalites abstraites d'origine +
  32 defis d'ambiance soft - mimes, votes, gages), 40 chefs d'accusation
  dans `TribunalContent.kt` (contre 10 avant). Zero alcool nomme, zero
  tiret long.
- Sous-titres des 4 modes de prompts classiques alignes sur le web
  (`modeRegistry.ts`) : "Action ou Verite" -> "Aveu au comptoir ou gage,
  choisis", "C'est un 10 mais" -> "Le defaut qui gache tout", "7
  Secondes" -> "Reponds avant le dernier grain". Android n'affiche
  aujourd'hui aucun sous-titre de mode dans les tuiles du hub
  (`ModeTile` ne rend que titre + joueurs min., le champ `subtitle` des
  packs JSON n'est jamais lu) - alignement documente pour une future
  UI, aucun rendu a corriger dans l'immediat.
- Fautes FR corrigees dans les chaines visibles : "7 secondes" (minuscule)
  et residu "Le Meneur" dans le README remplaces par "7 Secondes" et
  "Le Taulier" (nom actuel du mode Picolo).

## [0.10.0] - 2026-08-03

- Contenu des packs gratuits porte a 80 items chacun (sync depuis la-taverne-content 1.10.0), soirees plus longues sans repetition.

All notable changes to Meskova Android are documented here.
Format loosely follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
versioning follows [SemVer](https://semver.org/).

## [0.9.0] - 2026-08-03

### Added
- Monetisation native (RevenueCat) et analytics natives (PostHog), toutes
  deux gated : l'app compile et tourne en mode invite sans aucune cle API,
  ce qui reste toujours le cas en CI. `RevenueCatEntitlementRepository`
  encapsule le SDK Purchases (entitlement `La Taverne Pro`, 3 offres
  mensuel/annuel/a vie mappees par `core/PremiumPlan.kt`), selectionne par
  `MeskovaApplication` uniquement quand `BuildConfig.BILLING_ENABLED` est
  vrai (cle `REVENUECAT_API_KEY` presente dans `local.properties` ou en env),
  sinon `StubEntitlementRepository` (mode invite existant, inchange).
  `PostHogAnalyticsTracker` (instance EU, `eu.i.posthog.com`) n'initialise
  jamais le SDK - donc aucun appel reseau - avant un consentement explicite
  et non pre-coche (`ConsentBanner` + `ConsentStore`, DataStore local,
  boutons accepter/refuser a egalite visuelle), selectionne uniquement quand
  `BuildConfig.ANALYTICS_ENABLED` est vrai (cle `POSTHOG_API_KEY` presente).
- Nouvel ecran `PaywallScreen` : 3 offres (mensuel 4,99 €, annuel 19,99 €, a
  vie 34,99 € mis en avant comme meilleure offre - notre differenciant face
  au tout-abonnement), bouton restaurer les achats, transparence tarifaire
  totale (aucun essai gratuit mentionne). Sans cle RevenueCat, le bouton
  d'achat est desactive ("Bientot disponible"), jamais de checkout casse.
  Evenements traques : `paywall_shown`, `paywall_dismissed`,
  `purchase_started`, `purchase_completed`, `restore_completed`.
- Tuiles premium au hub : le catalogue premium (metadonnees seules, voir
  `PackRepository.loadPremiumCatalog()`) s'affiche desormais en tuiles
  verrouillees qui ouvrent le paywall (une seule offre debloque tout le
  catalogue - modele par entitlement, pas d'achat par pack).
- `core/PremiumPlan.kt` : mapping id produit RevenueCat (avec suffixes de
  base plan Play Store) -> offre, et activation de l'entitlement `La
  Taverne Pro`, teste par `PremiumPlanTest` (4 tests unitaires JVM purs,
  aucun SDK Android requis).
- Dependances ajoutees : `com.revenuecat.purchases:purchases` et
  `com.posthog:posthog-android` (versions dynamiques `8.+`/`3.+` - toujours
  resolubles sans cle, jamais initialisees sans cle).

## [0.8.0] - 2026-08-03

### Added
- Mode "Tu préfères" transformé en vraie mécanique de vote (parité avec la
  version web) : un dilemme A ou B s'affiche, le téléphone reste au centre de
  la table, chaque joueur actif tape son camp à son tour
  (`core/WouldYouRatherSession.kt`, `WouldYouRatherSessionState` immuable +
  fonctions réductrices `castVote`/`revealVotes`/`nextRound`, `Random`
  injectable pour la file mélangée). Au reveal, le camp minoritaire prend
  `MINORITY_PENALTY` (1) chacun ; une égalité parfaite ou un vote unanime ne
  coûte rien à personne. 84 dilemmes embarqués
  (`core/WouldYouRatherContent.kt`), 17 tests unitaires couvrant minorité qui
  trinque, égalité, unanimité, cumul multi-manches, fin de file et unicité du
  vote par joueur. Aucune mutation de `Player.penaltiesStandard` - le tally
  vit dans un récap interne au mode (`WouldYouRatherRecap` dans
  `WouldYouRatherScreen.kt`). Le mode passe de la voie prompt (pack de
  contenu `tu-preferes-classique.json`, retiré des assets) à un mode
  embarqué au même titre que Quitte ou Trinque ou Le Tableau d'Honneur :
  tuile dédiée au hub (2 joueurs min.), route `would-you-rather` dans le
  NavHost, `session_completed { mode: "would_you_rather", turns }` tracké à
  la sortie.

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
