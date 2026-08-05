# Architecture - La Tournée Android

Ce document decrit l'architecture reelle de l'application Android La Tournée :
les deux modules Gradle, les couches transverses de monetisation et
d'analytics, et le flux de contenu depuis le repo `la-taverne-content`. Le
diagramme est tenu a jour avec le code, ce n'est pas une decoration.

## Diagramme

```mermaid
flowchart TD
    subgraph content["la-taverne-content (repo separe, source de verite du contenu a prompts)"]
        Packs["content/fr/packs/*.json"]
        Tokens["tokens/tokens.json"]
    end

    Sync["scripts/sync_content.py"]
    Packs -->|"packs premium=false copies tels quels"| Assets
    Packs -->|"packs premium : metadonnees seules, jamais le texte"| PremiumCatalog
    Packs --> Sync
    Sync --> Assets
    Sync --> PremiumCatalog

    subgraph core[":core - Kotlin JVM pur, zero dependance Android, teste"]
        Deck["Deck / Card / Player / Contest - Le Coupe-Gorge"]
        Engine["BorderlandEngine - reducer pur"]
        Prompt["PromptSession - tirage sans repetition, regles, interpolate"]
        Tribunal["TribunalSession + TribunalContent - 40 accusations natives"]
        Auction["AuctionContent - 50 themes natifs"]
        Quiz["QuizSession + QuizContent - 60 questions natives"]
        Ranking["RankingSession + RankingContent - 40 questions natives"]
        WYR["WouldYouRatherSession + WouldYouRatherContent - 84 dilemmes natifs"]
        Roulette["RouletteContent - 40 segments natifs"]
        Plan["PremiumPlan - mapping produit RevenueCat vers offre"]
        Model["ContentPack / GameMode - modele de contenu, sans serialisation"]
        Targeting["Targeting.kt - resolveTarget genre/statut/paire, Random injectable"]
        Palette["theme/LaTourneePalette - tokens couleur purs, source unique clair+sombre"]
        Wcag["theme/WcagContrast - luminance relative, ratio WCAG 2.1"]
        ContrastTest["LaTourneePaletteContrastTest - garde mecanique, gradlew :core:test"]
    end

    subgraph app[":app - Jetpack Compose, Material 3, DI manuel"]
        Assets["assets/packs/*.json"]
        PremiumCatalog["assets/premium-catalog.json (metadonnees)"]
        Repo["PackRepository - lit les assets, mappe DTO vers :core"]
        AppRoot["LaTourneeApplication - conteneur DI manuel"]
        VM["ViewModels : Borderland / Prompt / PlayerSession"]
        Hub["HubScreen - bento grid des modes"]
        Modes["Ecrans de mode : Borderland / Prompt / Roulette / Tribunal / Auction / Quiz / Ranking / WouldYouRather / Recap"]
        Welcome["WelcomeScreen - disclaimer 18+"]
        Paywall["PaywallScreen - 3 offres, restaurer les achats"]
        Theme["ui/theme - clair/sombre neobrutaliste, polices auto-hebergees"]
        Store["PlayerStore - DataStore"]
        ThemeStore["ThemeStore - DataStore, clair/sombre/systeme"]
        Consent["ConsentStore + ConsentBanner - opt-in RGPD"]
        Billing["EntitlementRepository (interface)"]
        Analytics["AnalyticsTracker (interface)"]
    end

    subgraph external["Services externes (gated par cle API absente en CI)"]
        RC["RevenueCat - entitlement La Tournee Pro"]
        PH["PostHog EU - eu.i.posthog.com"]
    end

    Assets --> Repo
    PremiumCatalog --> Repo
    AppRoot --> Repo
    AppRoot --> Store
    AppRoot --> ThemeStore
    AppRoot --> Consent
    AppRoot --> Billing
    AppRoot --> Analytics
    Repo --> VM
    core --> VM
    core --> Modes
    VM --> Modes
    Hub --> Modes
    Hub --> Paywall
    Welcome --> Hub
    Store --> Hub
    ThemeStore --> Hub
    Theme --> Modes
    Consent --> Analytics
    Paywall --> Billing
    Billing -.->|"BuildConfig.BILLING_ENABLED = cle RevenueCat presente"| RC
    Analytics -.->|"BuildConfig.ANALYTICS_ENABLED = cle PostHog presente ET consentement accorde"| PH
    Tokens -.->|"source des couleurs Compose, clair + sombre"| Theme
    Palette -->|"Color.kt construit Color depuis PaletteColor, jamais un hex recopie"| Theme
    Palette --> Wcag
    Wcag --> ContrastTest
    Palette --> ContrastTest

    app -->|":app depend de :core, jamais l'inverse"| core
```

## Les couches

- **`:core` (Kotlin JVM pur).** Aucune dependance Android ni serialisation. Il
  contient les moteurs de jeu sous forme de reducers purs et testables
  (`BorderlandEngine`, `PromptSession`, `TribunalSession`, `QuizSession`,
  `RankingSession`, `WouldYouRatherSession`), le modele de contenu
  (`ContentPack`, `GameMode`), le mapping des offres (`PremiumPlan`) et
  `Targeting.kt` qui resout `Targets.GENDER_M/GENDER_F/SINGLE/COUPLE/PAIR`
  vers un ou deux `Player` a partir de leurs attributs optionnels
  `gender`/`relationship`, avec repli aleatoire gracieux. Il porte aussi
  `theme/LaTourneePalette` (source unique des tokens couleur, clair + sombre,
  memes roles que `src/styles/tokens.css` sur le web) et `theme/WcagContrast`
  (luminance relative, ratio WCAG 2.1, pur Kotlin) : `app/.../ui/theme/Color.kt`
  construit chaque `Color` Compose depuis ces memes objets plutot que de
  recopier un hex, et `LaTourneePaletteContrastTest` verifie mecaniquement
  que chaque paire encre/fond reellement utilisee dans l'UI clear 4.5:1
  (texte normal) ou 3:1 (texte large) dans les deux themes - garde
  permanente contre la regression 0.14.1 (texte `Ink` pose sur un aplat
  `Pop*`/`Neon*`, qui reste clair dans les deux themes alors qu'`Ink`
  s'inverse). Tourne avec `./gradlew :core:test` sans SDK Android.
- **`:app` (Jetpack Compose, Material 3).** L'UI : `WelcomeScreen` (genre et
  statut facultatifs par joueur, panneau replie par defaut), `HubScreen`
  (bento grid qui route vers chaque ecran de mode, toggle clair/sombre
  discret), les ecrans de mode, `PaywallScreen`, et le theme neobrutaliste
  taverne (`ui/theme`) : clair par defaut, variante sombre "pop" sur encre
  neutre, preference persistee par `ThemeStore`. `LaTourneeApplication` sert
  de conteneur de DI manuel et cable chaque dependance comme une interface.
  La dependance va toujours de `:app` vers `:core`, jamais l'inverse.
- **Billing (transverse, gated).** `EntitlementRepository` est une interface :
  `RevenueCatEntitlementRepository` n'est branche que si
  `BuildConfig.BILLING_ENABLED` est vrai (cle `REVENUECAT_API_KEY` presente),
  sinon `StubEntitlementRepository` prend le relais en mode invite. Sans cle,
  aucun appel reseau, l'app reste une application gratuite jouable.
- **Analytics (transverse, gated + consentement).** `AnalyticsTracker` est une
  interface : `PostHogAnalyticsTracker` (instance EU) n'est selectionne que si
  `BuildConfig.ANALYTICS_ENABLED` est vrai, et le SDK n'est jamais initialise
  tant que le joueur n'a pas accepte la banniere (`ConsentBanner` +
  `ConsentStore`). Sinon `NoOpAnalyticsTracker`.

## Flux de contenu

Deux sources coexistent :

1. **Contenu a prompts synchronise.** Les packs FR vivent dans le repo separe
   `la-taverne-content`. `scripts/sync_content.py` copie les packs gratuits
   tels quels dans `assets/packs/*.json` et n'ecrit que les metadonnees des
   packs premium dans `assets/premium-catalog.json` (jamais le texte des
   prompts). `PackRepository` lit ces assets, decode les DTO
   `@Serializable` et les mappe vers les types purs de `:core`. Les modes de
   prompts classiques (Le Taulier, Action ou Verite, Je n'ai jamais, etc.)
   consomment ce contenu.
2. **Contenu embarque natif.** Les modes a moteur propre (Le Pilori, La Criee,
   Quitte ou Trinque, Le Tableau d'Honneur, Tu preferes, La Roue du Destin)
   portent leur contenu directement dans `:core` (`TribunalContent`,
   `AuctionContent`, `QuizContent`, `RankingContent`, `WouldYouRatherContent`,
   `RouletteContent`), hors JSON.

## Pattern "moteur pur teste + contenu natif hors JSON"

Chaque mode a moteur propre suit le meme pattern : une session reducer pure et
deterministe dans `:core` (un `Random` injectable pour les tests), et son
contenu embarque a cote sous forme de constantes Kotlin. Ce contenu natif est
tenu a parite avec la version web (`la-taverne`), ce qui garantit qu'un meme
mode se joue identiquement sur mobile et sur le web. La logique etant separee
de Compose, elle se teste sans SDK Android (136 tests JUnit dans `:core`).

## Principe gated

Billing et analytics sont inactifs par defaut. Les flags
`BuildConfig.BILLING_ENABLED` et `BuildConfig.ANALYTICS_ENABLED` ne passent a
vrai que si la cle API correspondante est presente dans `local.properties` ou
en variable d'environnement. La CI n'ayant jamais ces cles, elle compile et
teste toujours l'app en mode invite, sans secret et sans appel reseau vers
RevenueCat ou PostHog.
