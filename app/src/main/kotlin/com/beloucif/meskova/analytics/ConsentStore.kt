package com.beloucif.meskova.analytics

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.consentDataStore by preferencesDataStore(name = "meskova_consent")

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
