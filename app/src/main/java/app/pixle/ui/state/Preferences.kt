package app.pixle.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import app.pixle.database.AppPreferences
import kotlinx.coroutines.flow.Flow

@Composable
fun rememberPreferences(): AppPreferences {
    val context = LocalContext.current
    return AppPreferences.getInstance(context)
}

@Composable
fun <T> rememberPreference(get: AppPreferences.() -> Flow<T>, initialValue: T): State<T> {
    val preferences = rememberPreferences()
    return preferences.get().collectAsState(initial = initialValue)
}