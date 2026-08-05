package com.beloucif.latournee.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Renamed from "meskova_players": app never published, no installs to migrate (see CHANGELOG).
private val Context.playerDataStore by preferencesDataStore(name = "latournee_players")

/**
 * Persists the roster of player names between sessions (DataStore, local only, no
 * cloud sync). Order is preserved via a "index|name" encoding since Preferences
 * only exposes unordered String sets.
 */
class PlayerStore(private val context: Context) {

    private val playersKey = stringSetPreferencesKey("players_ordered")

    val players: Flow<List<String>> = context.playerDataStore.data.map { prefs ->
        prefs[playersKey]
            ?.mapNotNull { entry -> entry.substringAfter('|', "").ifBlank { null }?.let { entry.substringBefore('|').toIntOrNull() to it } }
            ?.sortedBy { it.first }
            ?.map { it.second }
            ?: emptyList()
    }

    suspend fun savePlayers(names: List<String>) {
        context.playerDataStore.edit { prefs ->
            prefs[playersKey] = names.mapIndexed { index, name -> "$index|$name" }.toSet()
        }
    }

    suspend fun clear() {
        context.playerDataStore.edit { it.remove(playersKey) }
    }
}
