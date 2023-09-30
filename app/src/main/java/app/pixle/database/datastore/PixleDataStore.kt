package app.pixle.database.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import app.pixle.asset.PIXLE_DATASTORE_NAME

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PIXLE_DATASTORE_NAME)

