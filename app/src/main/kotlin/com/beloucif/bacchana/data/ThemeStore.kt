package com.beloucif.bacchana.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.beloucif.bacchana.ui.theme.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Bacchana is the definitive product name: file renamed from "latournee_theme", which was
// itself renamed from "meskova_theme" and "lataverne_theme" (the theme store did not exist
// under the original blackout name), none of which migrated (see CHANGELOG). This one does,
// via legacyDataStoreMigration - see LegacyDataStoreMigration.kt for why.
private val LEGACY_THEME_STORE_NAMES = listOf(
    "bacchus_theme",
    "latournee_theme",
    "meskova_theme",
    "lataverne_theme",
)
private val Context.themeDataStore by preferencesDataStore(
    name = "bacchana_theme",
    produceMigrations = { context -> listOf(legacyDataStoreMigration(context, LEGACY_THEME_STORE_NAMES)) },
)

/**
 * Persists the clair/sombre/systeme choice locally (DataStore, no cloud sync). Mirrors
 * `themeStore.ts` on the web: defaults to [ThemePreference.SYSTEM] so a fresh install follows
 * the OS setting until the player explicitly picks one from the discreet toggle in the hub.
 */
class ThemeStore(private val context: Context) {

    private val preferenceKey = stringPreferencesKey("theme_preference")

    val preference: Flow<ThemePreference> = context.themeDataStore.data.map { prefs ->
        prefs[preferenceKey]?.let { raw ->
            runCatching { ThemePreference.valueOf(raw) }.getOrNull()
        } ?: ThemePreference.SYSTEM
    }

    suspend fun setPreference(preference: ThemePreference) {
        context.themeDataStore.edit { prefs ->
            prefs[preferenceKey] = preference.name
        }
    }
}
