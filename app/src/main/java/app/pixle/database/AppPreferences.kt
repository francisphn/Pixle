package app.pixle.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.pixle.lib.GameMode
import app.pixle.ui.state.ObjectDetectionModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class AppPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val gameModeKey = stringPreferencesKey("game_mode")
    private val sensitivityKey = floatPreferencesKey("detection_sensitivity")
    private val modelKey = stringPreferencesKey("ml_model")
    private val onboardedKey = booleanPreferencesKey("onboarded")
    private val nextNotificationIdKey = intPreferencesKey("next_notification_id")
    private val usernameKey = stringPreferencesKey("username")


    val getUserName: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[usernameKey] ?: "Siri"
        }

    suspend fun saveUserName(username: String) {
        dataStore.edit { preferences ->
            preferences[usernameKey] = username
        }
    }

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

    val getModelPreference: Flow<ObjectDetectionModel> = dataStore.data
        .map { preferences ->
            preferences[modelKey] ?: ObjectDetectionModel.EDL1.filename
        }
        .map { filename ->
            ObjectDetectionModel.of(filename)
        }

    suspend fun saveModelPreference(model: ObjectDetectionModel) {
        dataStore.edit { preferences ->
            preferences[modelKey] = model.filename
        }
    }

    val shouldLaunchOnboardingPane: Flow<Boolean> =
        dataStore.data.map { settings ->
            settings[onboardedKey] != null
        }

    suspend fun dismissOnboardingPane() {
        dataStore.edit { settings ->
            settings[onboardedKey] = true
        }
    }

    suspend fun getNotificationId(): Flow<Int> = flow {
        dataStore.data.map { settings ->
            emit(settings[nextNotificationIdKey] ?: 1)
            incrementNotificationId()
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun incrementNotificationId() {
        dataStore.edit {
            var currentCounterValue = it[nextNotificationIdKey] ?: 0

            if (currentCounterValue > 1000) {
                currentCounterValue = 0
            }

            it[nextNotificationIdKey] = currentCounterValue + 1
        }
    }

    companion object {
        val DEFAULT_GAME_MODE = GameMode.Hard

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