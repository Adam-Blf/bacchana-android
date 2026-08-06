package com.beloucif.bacchana.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Bacchana is the definitive product name: file renamed from "latournee_players", which was
// itself renamed from "meskova_players", "lataverne_players" and "blackout_players" across
// the product's four previous names, none of which migrated (see CHANGELOG). This one does,
// via legacyDataStoreMigration - see LegacyDataStoreMigration.kt for why.
private val LEGACY_PLAYER_STORE_NAMES = listOf(
    "bacchus_players",
    "latournee_players",
    "meskova_players",
    "lataverne_players",
    "blackout_players",
)
private val Context.playerDataStore by preferencesDataStore(
    name = "bacchana_players",
    produceMigrations = { context -> listOf(legacyDataStoreMigration(context, LEGACY_PLAYER_STORE_NAMES)) },
)

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
