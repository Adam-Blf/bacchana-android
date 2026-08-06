package com.beloucif.bacchana.analytics

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.beloucif.bacchana.data.legacyDataStoreMigration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Bacchana is the definitive product name: file renamed from "latournee_consent", which was
// itself renamed from "meskova_consent" and "lataverne_consent" (the consent store did not
// exist under the original blackout name), none of which migrated (see CHANGELOG). This one
// does, via legacyDataStoreMigration - see data/LegacyDataStoreMigration.kt for why.
private val LEGACY_CONSENT_STORE_NAMES = listOf(
    "bacchus_consent",
    "latournee_consent",
    "meskova_consent",
    "lataverne_consent",
)
private val Context.consentDataStore by preferencesDataStore(
    name = "bacchana_consent",
    produceMigrations = { context -> listOf(legacyDataStoreMigration(context, LEGACY_CONSENT_STORE_NAMES)) },
)

/**
 * Persists the RGPD analytics consent choice locally (DataStore, no cloud sync, no
 * identifier generated before consent). Opt-in only: [analyticsGranted] and [hasDecided]
 * both default to false, so the consent banner shows on first launch and analytics stays
 * off until the player explicitly accepts - never pre-checked (CLAUDE.md section 18).
 */
class ConsentStore(private val context: Context) {
    private val grantedKey = booleanPreferencesKey("analytics_consent_granted")
    private val decidedKey = booleanPreferencesKey("analytics_consent_decided")

    /** True once the player has explicitly accepted analytics. */
    val analyticsGranted: Flow<Boolean> = context.consentDataStore.data.map { it[grantedKey] ?: false }

    /** True once the player has answered the consent banner at least once (accept or decline). */
    val hasDecided: Flow<Boolean> = context.consentDataStore.data.map { it[decidedKey] ?: false }

    suspend fun setConsent(granted: Boolean) {
        context.consentDataStore.edit { prefs ->
            prefs[grantedKey] = granted
            prefs[decidedKey] = true
        }
    }
}
