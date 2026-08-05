package com.beloucif.latournee.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.beloucif.latournee.ui.theme.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Renamed from "meskova_theme": app never published, no installs to migrate (see CHANGELOG).
private val Context.themeDataStore by preferencesDataStore(name = "latournee_theme")

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
