# Publication Meskova Android

Ce document décrit le processus de publication sur Google Play Store.

## Prerequis one-shot

Avant la première publication, exécuter ces étapes une seule fois.

### 1. Créer un compte Google Play Console

- Aller sur https://play.google.com/console
- Payer les 25 dollars de frais d'inscription
- Créer l'application avec le bundle ID `com.beloucif.meskova`
- Remplir la fiche produit (captures d'écran, description, catégorie, classification d'âge 18+)

### 2. Générer une clé de signature (keystore)

Sur ta machine Windows, générer un keystore de production et le stocker de manière sécurisée (dossier `00_Sensible` ou similaire, jamais commité).

```bash
# Créer le keystore (valide 50 ans)
keytool -genkey -v -keystore meskova-release.jks ^
  -keyalg RSA -keysize 4096 -validity 18250 ^
  -alias meskova ^
  -storepass <MOT_DE_PASSE_LONG> ^
  -keypass <MOT_DE_PASSE_LONG>
```

Mémoriser ou copier-coller les informations demandées (nom, organisation, ville, etc.) - elles font partie de l'identité de la clé.

**Jamais ne supprimer ou perdre ce fichier** - Google Play exige le même keystore pour tous les updates de l'application.

### 3. Créer un compte de service Google Cloud

- Aller sur https://console.cloud.google.com
- Créer un projet ou utiliser un existant
- Créer un compte de service avec les rôles `Editor` (temporaire) ou `Service Accounts Admin`
- Télécharger la clé au format JSON (fichier `play-store-credentials.json`)
- Ajouter le compte de service comme administrateur dans Google Play Console (Settings > Users and permissions > Invite user > email du compte de service, rôle Admin)
- Ne jamais commiter le JSON, le stocker en local sécurisé

### 4. Configurer les secrets GitHub Actions

Une fois le keystore et le compte de service générés en local, créer les secrets GitHub Actions en exécutant ces commandes exactes :

```bash
# Encoder le keystore en base64
# Sur Windows PowerShell :
$keystore = Get-Content -Path "chemin/vers/meskova-release.jks" -Encoding Byte
$base64 = [Convert]::ToBase64String($keystore)
$base64 | Set-Content -Path "keystore-base64.txt"

# Ou sur Git Bash :
cat chemin/vers/meskova-release.jks | base64 > keystore-base64.txt

# Lire le contenu et créer le secret GitHub
gh secret set ANDROID_KEYSTORE_BASE64 < keystore-base64.txt

# Créer les autres secrets (remplacer les valeurs)
gh secret set ANDROID_KEYSTORE_PASSWORD --body "MOT_DE_PASSE_LONG"
gh secret set ANDROID_KEY_ALIAS --body "meskova"
gh secret set ANDROID_KEY_PASSWORD --body "MOT_DE_PASSE_LONG"

# Créer le secret Play Service Account (remplacer chemin)
gh secret set PLAY_SERVICE_ACCOUNT_JSON < chemin/vers/play-store-credentials.json

# Vérifier tous les secrets
gh secret list
```

Après execution, les secrets sont visibles dans GitHub (Settings > Secrets and variables > Actions), jamais en clair dans le repo.

## Workflow quotidien : publier une version

### Étape 1 : Développer et tester

Développer les features sur une branche `feat/*` ou `fix/*`, commiter régulièrement, valider localement.

### Étape 2 : Une commande pour publier

Une fois prêt à publier (version testée, PR mergée sur main), exécuter **une seule commande** depuis ton Windows :

```bash
python scripts/release.py --version 1.0.0
```

Cela va :
- Bumper `versionCode` et `versionName` dans `app/build.gradle.kts`
- Commiter avec le message `chore: bump version to 1.0.0`
- Créer un tag annoté `v1.0.0`
- Pousser commits et tags vers GitHub (`origin`)

### Étape 3 : GitHub Actions se charge du reste

À la réception du tag `v1.0.0` (push vers GitHub), la workflow `.github/workflows/release.yml` se déclenche automatiquement :

1. **Tests** : exécute `:core:test` pour valider la logique
2. **Build signé** : décode le keystore depuis `ANDROID_KEYSTORE_BASE64`, construit l'AAB signé avec `bundleRelease`
3. **Upload Play** : envoie l'AAB à Google Play Console sur le track `internal` (accessible aux testeurs ajoutés en Play Console)
4. **GitHub Release** : crée une release GitHub avec l'AAB en fichier joint

### Étape 4 : Promouvoir vers la production

Une fois testé sur le track `internal` (via Google Play Console, menu Internal testing > Testers), promouvoir manuellement vers `production` dans Google Play Console. L'app sera alors visible pour tous les utilisateurs.

## Dry run avant production

Avant ta première publication réelle, tester le workflow en dry-run :

```bash
python scripts/release.py --version 1.0.0 --dry-run
```

Cela affichera les changements qui seraient apportés sans rien commiter.

## Troubleshooting

### "secrets not found" ou "did not match"

Les secrets GitHub ont pu être mal créés ou mal nommés. Vérifier avec :

```bash
gh secret list
```

Doit afficher les 5 secrets : `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`, `PLAY_SERVICE_ACCOUNT_JSON`.

Si l'un manque, le recréer exactement.

### Build échoue avec "Keystore was tampered with"

Le base64 du keystore a possiblement été corrompu en transit. Regénérer depuis zero :

```bash
# Relire la valeur stockée
gh secret view ANDROID_KEYSTORE_BASE64

# Si elle semble tronquée ou malformée, la regénérer
cat chemin/vers/meskova-release.jks | base64 > keystore-base64.txt
gh secret set ANDROID_KEYSTORE_BASE64 < keystore-base64.txt
```

### "Uploading to internal track" échoue

Vérifier dans Google Play Console :
1. Le compte de service est bien administrateur (Settings > Users and permissions)
2. L'app est créée et visible dans le dashboard
3. Aucune validation Play Console en attente (statut de l'app = "Ready" ou "In review")

Si l'app est en attente de validation, attendre que Google approuve avant de relancer le build.

## Versioning

Suivre [Semantic Versioning](https://semver.org/) :
- `MAJOR.MINOR.PATCH` (ex. 1.0.0, 1.2.3)
- **MAJOR** : breaking change (nouvel UX, nouveau format de contenu)
- **MINOR** : feature utilisateur
- **PATCH** : bugfix ou perf

À chaque release, mettre à jour le badge version du README pour que le lecteur voit directement la version courante du repo.

## References

- [Google Play Console Help](https://support.google.com/googleplay/android-developer)
- [Android App Signing](https://developer.android.com/studio/publish/app-signing)
- [Gradle bundleRelease](https://developer.android.com/build/run-tests#run-bundle-tests)
- [r0adkll/upload-google-play](https://github.com/r0adkll/upload-google-play)
