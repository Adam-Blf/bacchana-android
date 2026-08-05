package com.beloucif.bacchus.data

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.firstOrNull

/**
 * Migrates a DataStore Preferences file created under a previous product name into the
 * current one. The product has been renamed five times (BlackOut -> La Taverne -> Meskova ->
 * La Tournee -> Bacchus) and every rename so far skipped this step on the assumption the app
 * had no installs yet - true at the time, but never guaranteed for whoever has an internal
 * test build on their device. Bacchus is the definitive name, so this migration walks the
 * *entire* historical chain of file names (newest first, since that is the most likely to hold
 * the freshest data) and adopts the first one it finds with data. Internal preference keys
 * never changed across renames - only the DataStore file name did - so a straight copy of the
 * whole [Preferences] map is correct; no per-key remapping needed.
 *
 * Idempotent: once the current store holds data, [shouldMigrate] returns false and this is a
 * permanent no-op. Safe to keep even if no legacy file is ever found.
 */
internal fun legacyDataStoreMigration(context: Context, legacyNamesNewestFirst: List<String>): DataMigration<Preferences> =
    object : DataMigration<Preferences> {
        override suspend fun shouldMigrate(currentData: Preferences): Boolean =
            currentData == emptyPreferences()

        override suspend fun migrate(currentData: Preferences): Preferences {
            for (legacyName in legacyNamesNewestFirst) {
                val legacyFile = context.preferencesDataStoreFile(legacyName)
                if (!legacyFile.exists()) continue

                val legacyStore = PreferenceDataStoreFactory.create(
                    produceFile = { legacyFile },
                )
                val legacyData = legacyStore.data.firstOrNull() ?: continue
                if (legacyData != emptyPreferences()) {
                    return legacyData
                }
            }
            return currentData
        }

        override suspend fun cleanUp() {
            // Legacy files are left on disk untouched: they are a handful of bytes at most,
            // and deleting them here would run on every cold start check, not just the one
            // migration that matters. Not worth the extra failure surface.
        }
    }
