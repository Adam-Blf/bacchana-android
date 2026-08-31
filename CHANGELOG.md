# Changelog

## [0.19.0] - 2026-08-31

### « Quitte ou Trinque » vivait encore ici, trois semaines apres son renommage

Le depot web porte une garde anti-lexique depuis le 5 aout, et le mode quiz y a
ete renomme « Quitte ou Double » le meme jour. La portee de cette garde est
`bacchana/src` : ce depot n'etait couvert par rien. Le renommage n'a jamais
franchi la frontiere du depot, et personne ne s'en est avise pendant trois
semaines. La garde protegeait le fichier qu'on avait corrige, pas le produit.

Ce n'est pas un detail de style. Apple 1.4.3 interdit ce qui « encourage » la
consommation excessive d'alcool, Play interdit d'en donner une image favorable
et cite nommement le jeu a gages. Le critere n'est pas le mot isole, c'est ce
que l'ecran donne a lire - et une tuile « QUITTE OU TRINQUE » en premiere page
se lit sans ambiguite.

Seize emplacements corriges, dont six chaines VISIBLES : le titre du quiz, le
bouton « Je cumule », et les trois libelles de resultat de « Tu preferes » qui
annoncaient qui trinque. Deux cartes de contenu reformulees, « rapporte un verre
d'eau » et « renverser ton verre ».

### La garde vit desormais dans CE depot

Elle balaie `.kt`, `.kts`, `.xml` et `.md`. Trois calibrages ont ete necessaires
avant qu'elle ne soit juste, et chacun apprend quelque chose :

- **Le CHANGELOG est hors de portee.** Il raconte les faits au moment ou ils ont
  eu lieu ; reecrire « mode Quitte ou Trinque ajoute » effacerait l'histoire du
  renommage au lieu de la documenter.
- **Une affirmation d'ABSENCE n'est pas une violation.** « Zero reference
  alcool » est le positionnement recherche. Et la negation se lit sur un
  VOISINAGE de quelques lignes, pas sur la seule ligne : un paragraphe de README
  ecrit sa negation en tete et sa mention quatre lignes plus bas. Sans cette
  fenetre, la garde exigeait de tordre la prose pour la satisfaire.
- **« bois » est le verbe ET le materiau.** Le motif repris du web accusait « un
  fond brun bois ». On ne retient plus que les formes non ambigues.

L'icone est exemptee NOMMEMENT, et c'est deliberé : ses deux fichiers decrivent
un dessin de deux verres qui trinquent. Le commentaire est exact, et le reecrire
ne changerait pas le dessin. C'est l'icone qui pose question, pas sa
description ; la masquer ferait perdre la trace de ce qu'il faut redessiner.

### La garde verifie qu'elle marche avant de s'en servir

Trois fois dans la meme journee, un motif ecrit a travers deux couches
d'echappement a recu un antislash-b transforme en CARACTERE DE CONTROLE
backspace au lieu d'une echappement de regex. Le fichier se lit normalement, la
ligne parait juste, et le motif ne correspond plus jamais a rien. C'est arrive a
`git_guard`, puis a `check_tile_ink`, puis a la regle de negation d'ici - qui ne
reconnaissait plus « jamais » ni « zero ».

Se fier a l'attention a echoue trois fois, donc on mesure. `autocontrole()`
refuse de rendre un verdict si le fichier porte un caractere de controle, si un
motif n'attrape pas ce qu'il annonce, ou si une phrase anodine en declenche un.
Il a immediatement trouve le faux positif sur « bois », avant livraison.

Une garde cassee ne rend plus « vert » : elle sort en code 2 et le dit.

### Preuve

87 fichiers verifies, zero occurrence. Garde vue rouge en remettant « Quitte ou
Trinque » dans une chaine visible. `:core:test` compile et passe.

## [0.18.0] - 2026-08-31

### API 36, parce que la date est passee

Play exige la cible **Android 16 (API 36)** pour toute nouvelle application ou
mise a jour depuis le **31 aout 2026**, c'est-a-dire aujourd'hui. Un AAB en 35
est refuse **a l'envoi**, pas en revue : on ne l'apprend pas dans un rapport de
reviewer, on l'apprend quand le televersement echoue.

La montee n'est pas une ligne, c'est une chaine :

- **AGP 8.6.1 vers 8.10.1.** La 8.9 plafonne a l'API 35, verifie dans ses notes
  de version ; la 8.10 est la premiere qui compile la 36. Saut minimal assume :
  AGP est en 9.3 aujourd'hui, mais une migration majeure n'a rien a faire dans
  un lot impose par une echeance.
- **Gradle 8.10 vers 8.11.1**, minimum exige par AGP 8.10. Wrapper regenere par
  la tache `wrapper`, pas a la main.
- **`compileSdk` et `targetSdk` a 36.**

### Ce qui aurait pu casser, et qui ne casse pas

Verifie avant de monter, pas apres :

- **Le bord a bord impose.** Android 16 retire l'option de retrait pour une
  cible 36. `MainActivity` appelle deja `enableEdgeToEdge()` et les ecrans
  posent `windowInsetsPadding(WindowInsets.safeDrawing)`.
- **Les dispositions adaptatives.** Une cible 36 interdit de verrouiller
  l'orientation sur grand ecran. Le manifeste ne declare aucun
  `screenOrientation`.
- **Le retour predictif**, actif par defaut a partir de 36. L'application
  n'intercepte le retour nulle part - aucun `BackHandler`, aucun
  `onBackPressed`.
- **Le compilateur Compose** passe deja par le plugin Kotlin et non par
  l'ancien `kotlinCompilerExtensionVersion`.

Quatre facons de casser, quatre deja couvertes. Ca vaut d'etre dit : sur une
montee de version, ce qui est interessant est ce qu'elle ne touche PAS.

### Le jar du wrapper est desormais valide en integration continue

Ce lot change le jar du wrapper, ce qui est le bon moment pour poser la garde
qui manquait. `gradle-wrapper.jar` est un BINAIRE commite : son diff n'est pas
lisible dans l'interface de GitHub. Sur un depot devenu public ce matin, une PR
d'un inconnu qui le remplace passe la revue humaine sans que personne ne voie
rien, puis s'execute a la prochaine etiquette **dans le job qui decode le
keystore de signature**. Une cle de signature Android ne se change pas sans
casser la mise a jour de tous les installes.

`gradle/actions/wrapper-validation` est ajoute avant chacun des trois
`setup-gradle` des deux workflows.

### Preuve

Chaine d'outils prouvee en local avec un JDK 21 portable : Gradle 8.11.1 et AGP
8.10.1 se resolvent, `:core:test` compile et passe. Le module `app` demande le
SDK Android, absent de la machine : il est prouve par l'integration continue,
qui tourne de nouveau depuis que le depot est public.

## [0.17.0] - 2026-08-30

### Le paywall vendait un catalogue qui n'existe plus

Le prix a ete arrete le 30/08/2026 : un achat UNIQUE a 12,99 EUR, sans
abonnement. Le web a suivi, ce port non. `PremiumPlan` portait encore
`premium_monthly` a 4,99, `premium_yearly` a 19,99 et un a vie a **34,99** -
soit deux abonnements qui n'existeront dans aucun magasin, et un prix a vie faux
de 22 euros.

Rien ne pouvait l'attraper : des plans et des prix sont des chaines de
caracteres, et un paywall qui s'affiche est un paywall qui a l'air correct. Il a
fallu comparer les trois plateformes a la main pour le voir.

- **`PremiumPlan` ne porte plus qu'une valeur**, `premium_lifetime` a 12,99 EUR.
  Il reste une enumeration plutot qu'une constante pour garder l'identifiant
  produit type en un seul endroit et laisser `purchasePremium(activity, plan)`
  intact le jour ou un pack optionnel arrive.
- **Le selecteur de plan disparait**, et cette absence est l'argument : l'ecran
  n'a plus rien a arbitrer. La ligne d'offre n'est plus `clickable` - un element
  qui reagit au doigt promet une alternative, et il n'y en a pas.
- **La pastille passe de « Meilleure offre » a « Seule offre »**. « Meilleure »
  n'a pas de sens quand il n'y a rien d'autre.
- **La ligne de transparence tarifaire ne parle plus d'abonnements.** Elle
  annoncait « renouvellement automatique, resiliable a tout moment » sur un
  produit qui ne se renouvelle pas.
- **Deux tests verrouillent le catalogue** : une seule offre, a 12,99, et aucun
  identifiant produit contenant `monthly` ou `yearly`. Le prix affiche a
  l'acheteur vient toujours du magasin ; `priceLabel` n'est que le repli en mode
  invite ou hors ligne.
- **README remis d'aplomb**, et complete d'un rappel qui n'y etait pas : le
  produit a vie doit etre declare NON CONSOMMABLE cote Play. Mal declare, Play
  le consomme, le joueur peut le racheter, et son achat a vie ne le suit plus.

## [0.16.1] - 2026-08-05

- Residus de renommage : les depots GitHub ayant ete renommes, les references
  au depot Android passent de `Adam-Blf/la-tournee-android` a
  `Adam-Blf/bacchus-android` (les trois badges du README, l'en-tete de
  `scripts/release.py`) et `rootProject.name` passe de `la-tournee-android` a
  `bacchus-android` dans `settings.gradle.kts`.
- **Correctif reel, pas cosmetique** : `scripts/sync_content.py` resolvait le
  depot de contenu sur `../la-taverne-content`, dossier qui n'existe plus
  depuis son renommage en `bacchus-content`. Le script echouait donc sur
  `ERROR: content source not found`. `CONTENT_ROOT` pointe desormais sur
  `../bacchus-content`, verifie par une execution reelle (6 packs gratuits
  copies, 5 entrees de catalogue premium ecrites, sortie identique a la
  precedente donc aucun asset modifie).
- Documentation alignee sur le nouveau nom du depot de contenu
  (`bacchus-content`) : README (diagramme Mermaid, section contenu, commande
  de resynchronisation), `docs/ARCHITECTURE.md`, et les commentaires de
  `ContentPackDto.kt`, `PackRepository.kt`, `PremiumCatalogEntry.kt`,
  `HubScreen.kt`, `ContentPack.kt`, `colors.xml`.
- Non touche volontairement : les cles DataStore historiques
  (`latournee_*`, `meskova_*`, `lataverne_*`, `blackout_*`) qui servent la
  migration des donnees locales, les entrees de changelog anterieures, et les
  references au depot web `la-taverne` dont le dossier local porte toujours ce
  nom (le renommer casserait les chemins).

## [0.16.0] - 2026-08-05

- Rebranding produit : "La Tournée" devient "Bacchus", nom commercial
  **definitif** valide par Adam Beloucif (sixieme et dernier nom du produit
  apres BlackOut, La Taverne, Meskova, La Tournee) - `app_name`, titre du
  paywall "Bacchus Premium", texte au dos des cartes du Coupe-Gorge (qui
  affichait encore "MESKOVA", jamais corrige lors du rebrand La Tournee -
  dette corrigee au passage), README, docs, ProGuard rules.
  `applicationId`/`namespace` passent de `com.beloucif.latournee` à
  `com.beloucif.bacchus` (arborescence de packages Kotlin et imports
  renommés en consequence, `LaTourneeApplication`/`LaTourneeApp`/
  `LaTourneeRoutes`/`LaTourneeColors`/`LaTourneeTheme`/`LaTourneePalette`/
  `LaTourneePaletteContrastTest` -> équivalents `Bacchus*`) - l'app n'étant
  toujours pas publiée sur le Play Store, ce changement ne casse aucun
  utilisateur existant.
  Contrairement aux quatre rebrands precedents, les fichiers DataStore
  locaux (`latournee_players`/`latournee_theme`/`latournee_consent`) sont
  cette fois renommés **avec une migration reelle**
  (`data/LegacyDataStoreMigration.kt`) plutot que sans script comme avant :
  Bacchus etant le nom definitif, la chaine de migration complete est
  parcourue a la premiere ouverture (le nom le plus recent d'abord) -
  `latournee_*` -> `meskova_*` -> `lataverne_*` -> `blackout_*` pour les
  joueurs (le seul store existant depuis l'origine du projet), `latournee_*`
  -> `meskova_*` -> `lataverne_*` pour le theme et le consentement (crees
  apres le rebrand BlackOut -> La Taverne). Les cles internes n'ayant jamais
  change de nom d'un rebrand a l'autre, la migration copie tel quel le
  premier fichier legacy trouve avec des donnees - idempotente, sans effet
  si aucun fichier legacy n'existe.
  L'identifiant d'entitlement RevenueCat `core/PremiumPlan.kt` est
  **renommé** (`"La Tournee Pro"` -> `"Bacchus Pro"`, id technique a
  recreer cote dashboard RevenueCat) - toujours aucun abonné existant a
  migrer, projet RevenueCat sans aucun produit ni achat a ce jour.
  `PRIVACY_POLICY_URL`/`LEGAL_URL` de `SettingsScreen.kt` pointent
  desormais vers `bacchus.beloucif.com` (provisioning DNS/hebergement du
  sous-domaine hors perimetre de ce depot). Garde de contraste renommée
  `LaTourneePaletteContrastTest` -> `BacchusPaletteContrastTest`, toujours
  verte (136 tests `:core:test`).
  Restent inchanges pour l'instant, hors perimetre de ce lot : le nom du
  repo GitHub (`Adam-Blf/la-tournee-android`, badges et liens du README
  toujours pointes dessus), `rootProject.name` (`settings.gradle.kts`,
  suit la meme logique que le repo GitHub - les deux seront alignes
  ensemble lors du renommage du depot).

## [0.15.1] - 2026-08-05

- Durcissement securite de la chaine d'integration, suite d'audit. Les 15
  references d'actions tierces des deux workflows passent de tags mutables
  (`@v4`, `@v3`, `@v2`, `@v1`) a des SHA de commit complets, tag d'origine
  garde en commentaire. Le point dur etait `release.yml` : le job qui decode
  le keystore de signature et pousse sur le Play Store appelait
  `r0adkll/upload-google-play@v1` et `softprops/action-gh-release@v2`, deux
  tags que leurs mainteneurs peuvent redeplacer a tout moment sur du code
  qui lit `ANDROID_KEYSTORE_BASE64` et `PLAY_SERVICE_ACCOUNT_JSON`.
- Jeton d'integration au moindre privilege : `permissions: contents: read`
  au niveau des deux workflows, eleve a `contents: write` sur le seul job
  `build-and-upload`, qui publie la release GitHub.
- Le secret du keystore ne transite plus par une interpolation
  `${{ secrets.* }}` dans un corps de `run:` (l'expansion a lieu avant que
  le shell ne parse le script, donc une valeur forgee s'executerait comme du
  code) mais par un bloc `env:` d'etape lu en `$KEYSTORE_B64`. Verification :
  plus aucune reference `secrets.` dans un `run:` du depot.
- Nouveau job `secrets` dans `ci.yml` : `gitleaks` sur l'historique complet
  (`fetch-depth: 0`), aligne sur le modele du repo web `la-taverne`.
- `.github/dependabot.yml` ajoute (`github-actions` + `gradle`, hebdomadaire),
  qui est le mecanisme de rafraichissement des nouveaux pins SHA.
- `android:usesCleartextTraffic="false"` declare explicitement dans le
  manifeste : deja le defaut avec targetSdk 35, mais l'invariant devient
  lisible et survit a un futur changement de targetSdk.

## [0.15.0] - 2026-08-05

- Rebranding produit : "Meskova" devient "La Tournée" (cinquième nom du
  produit) - `app_name`, titre du paywall "La Tournée Premium", README,
  docs, ProGuard rules. L'univers interne du jeu reste intact - Le
  Taulier, Le Coupe-Gorge, Le Pilori, La Criée gardent leurs libellés.
  `applicationId`/`namespace` passent de `com.beloucif.meskova` à
  `com.beloucif.latournee` (arborescence de packages Kotlin et imports
  renommés en conséquence, `MeskovaApplication`/`MeskovaApp`/
  `MeskovaColors`/`MeskovaTheme`/`MeskovaPalette` -> équivalents
  `LaTournee*`) - l'app n'étant pas encore publiée sur le Play Store, ce
  changement ne casse aucun utilisateur existant. Les noms de fichiers
  DataStore locaux (`meskova_players`/`meskova_theme`/`meskova_consent`)
  sont renommés en `latournee_*` sans script de migration, pour la même
  raison. Contrairement au rebrand précédent, l'identifiant d'entitlement
  RevenueCat `core/PremiumPlan.kt` est cette fois **renommé**
  (`"La Taverne Pro"` -> `"La Tournee Pro"`, sans accent, id technique
  exact créé côté dashboard RevenueCat) - toujours aucun abonné existant
  à migrer. `rootProject.name` (`settings.gradle.kts`) et les badges du
  README passent de `la-taverne-android` à `la-tournee-android` (le repo
  GitHub a été renommé en conséquence). `PRIVACY_POLICY_URL`/`LEGAL_URL`
  de `SettingsScreen.kt` pointent désormais vers `latournee.beloucif.com`
  (actif). Garde de contraste renommée `MeskovaPaletteContrastTest` ->
  `LaTourneePaletteContrastTest`, toujours verte (136 tests `:core:test`).

## [0.14.1] - 2026-08-05

- Fix contraste WCAG texte-sur-aplat (theme sombre) : signale deux fois par
  Adam en jouant ("du blanc sur du jaune c'est illisible, du blanc sur du
  vert clair c'est illisible"), jamais porte depuis le fix web du
  2026-08-04. Cause racine : `LaTourneeColors.Ink` s'inverse avec le theme
  (encre foncee en clair, quasi blanche en sombre) alors que les aplats
  `Pop*`/`Neon*` restent CLAIRS dans les deux themes - du texte `Ink` pose
  dessus tombait a ~1.2:1 en sombre (roue de la roulette, badges Quitte ou
  Trinque, cartes Le Tableau d'Honneur/Le Pilori, icone d'ajout de joueur).
  Deux nouveaux jetons theme-invariants portent la meme logique que sur le
  web (`docs/DESIGN_TOKENS.md` du repo `la-taverne`) :
  - `LaTourneeColors.TileInk` (`#111111` fixe) pour tout texte/icone pose
    sur `PopYellow`/`PopPink`/`PopBlue`/`PopLime`/`Neon`/`NeonDeep`/
    `NeonSoft` - remplace aussi `CardFace` (blanc) comme couleur de texte
    par defaut des boutons primaires (`Theme.kt` `onPrimary`), qui ne
    passait que 3.28:1 en clair et 2.60:1 en sombre sur un fond `Neon`.
  - `LaTourneeColors.OnStatus` pour le texte pose sur `Premium`/`Success`/
    `Warning`/`Danger` (direction inverse de `Bg` entre les deux themes :
    blanc en clair, `TileInk` en sombre) - corrige les icones des
    steppers de La Criee et le resultat d'enchere.
  - `LaTourneeColors.CardAccent` (`#C74300` fixe) pour le label orange de Tu
    preferes, pose sur la face de carte blanche fixe (`Neon` y tombait a
    2.60:1 en sombre).
  Rampe d'elevation du theme sombre mise a jour vers la refonte 2026-08-04
  (`BgRaised #221E28`, `Surface #2E2836`, `SurfaceElevated #3C3446`,
  `InkMuted #958FA3`, alpha de bordure fine `0.20` -> `0.38`) - alignee
  avec `docs/DESIGN_TOKENS.md` du repo web.
  Nouveau module de tokens purs Kotlin dans `core` (`core/.../theme/`,
  `PaletteColor`, `WcagContrast`, `LaTourneePalette`) : source de verite
  unique consommee a la fois par le rendu Compose
  (`app/.../ui/theme/Color.kt`) et par la garde mecanique
  `LaTourneePaletteContrastTest` (`./gradlew :core:test`, 136 tests), qui
  calcule le ratio de contraste WCAG 2.1 reel de chaque paire encre/fond
  utilisee dans le theme et echoue si une paire tombe sous 4.5:1 (texte
  normal) ou 3:1 (texte large/objets UI) - derive des memes objets
  `LaTourneePalette.Light`/`LaTourneePalette.Dark` que le rendu, pas d'une
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
  orange accent (`LaTourneeColors.Neon`) au lieu de l'encre - en encre le
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
  `src/styles/tokens.css` du web (source unique). `LaTourneeColors`
  reste un `object` a l'usage identique (300+ sites d'appel `LaTourneeColors.X`
  inchanges) mais lit desormais un `CompositionLocal` fourni par
  `LaTourneeTheme`, qui accepte une preference clair/sombre/systeme
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

All notable changes to La Tournée Android are documented here.
Format loosely follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
versioning follows [SemVer](https://semver.org/).

## [0.9.0] - 2026-08-03

### Added
- Monetisation native (RevenueCat) et analytics natives (PostHog), toutes
  deux gated : l'app compile et tourne en mode invite sans aucune cle API,
  ce qui reste toujours le cas en CI. `RevenueCatEntitlementRepository`
  encapsule le SDK Purchases (entitlement `La Taverne Pro`, 3 offres
  mensuel/annuel/a vie mappees par `core/PremiumPlan.kt`), selectionne par
  `LaTourneeApplication` uniquement quand `BuildConfig.BILLING_ENABLED` est
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
