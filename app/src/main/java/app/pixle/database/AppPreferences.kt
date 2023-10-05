package app.pixle.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.pixle.lib.GameMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val gameModeKey = stringPreferencesKey("game_mode")
    private val sensitivityKey = floatPreferencesKey("detection_sensitivity")
    private val onboarded = booleanPreferencesKey("onboarded")

    val getGameModePreference: Flow<GameMode> = dataStore.data
        .map { preferences ->
            preferences[gameModeKey]?.let { GameMode.valueOf(it) } ?: DEFAULT_GAME_MODE
        }

    suspend fun saveGameModePreference(mode: GameMode) {
        dataStore.edit { preferences ->
            preferences[gameModeKey] = mode.toString()
        }
    }

    val getSensitivityPreference: Flow<Float> = dataStore.data
        .map { preferences ->
            preferences[sensitivityKey] ?: DEFAULT_SENSITIVITY
        }

    suspend fun saveSensitivityPreference(sensitivity: Float) {
        dataStore.edit { preferences ->
            preferences[sensitivityKey] = sensitivity
        }
    }

    val shouldLaunchOnboardingPane: Flow<Boolean> =
        dataStore.data.map { settings ->
            settings[onboarded] != null
        }

    suspend fun dismissOnboardingPane() {
        dataStore.edit { settings ->
            settings[onboarded] = true
        }
    }

    companion object {
        val DEFAULT_GAME_MODE = GameMode.Easy

        const val DEFAULT_SENSITIVITY = 0.2f

        private val Context.dataStore: DataStore<Preferences> by
            preferencesDataStore("app_preferences")

        @Volatile
        private var instance: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences = instance ?: synchronized(this) {
            AppPreferences(context.dataStore).also { instance = it }
        }
    }
}