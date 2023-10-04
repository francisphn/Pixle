package app.pixle.model.entity

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences(private val context: Context) {

    // to make sure there is only one instance
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("app_preferences")
        val GAME_MODE_KEY = stringPreferencesKey("game_mode")
        val SENSITIVITY_KEY = stringPreferencesKey("detection_sensitivity")
    }

    val getGameModePreference: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[GAME_MODE_KEY] ?: ""
        }

    suspend fun saveGameModePreference(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[GAME_MODE_KEY] = mode
        }
    }

    val getSensitivityPreference: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[SENSITIVITY_KEY] ?: ""
        }

    suspend fun saveSensitivityPreference(sensitivity: String) {
        context.dataStore.edit { preferences ->
            preferences[SENSITIVITY_KEY] = sensitivity
        }
    }
}