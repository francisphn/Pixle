package app.pixle.database.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import app.pixle.model.dto.SolutionItemDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class ItemLibraryRepository(private val dataStore: DataStore<Preferences>) {

    suspend fun addAll(items: Collection<SolutionItemDto>) = coroutineScope {
        items.forEach { item ->
            launch (Dispatchers.IO) {
                dataStore.edit {
                    val key = stringPreferencesKey(item.name)
                    it[key] = item.toString()
                }
            }
        }
    }

    suspend fun get(itemName: String) : SolutionItemDto? {
        val key = stringPreferencesKey(itemName)

        return SolutionItemDto.fromString(dataStore.data.first()[key])
    }

    suspend fun get() : Flow<SolutionItemDto> {
        val hello = dataStore.data.map { preferences ->
            preferences. { entry ->
                YourDataModel(entry.key, entry.value.toString())
        }
    }

    companion object {
        @Volatile
        private var instance: ItemLibraryRepository? = null

        fun getInstance(context: Context): ItemLibraryRepository {
            return instance ?: synchronized(this) {
                ItemLibraryRepository(context.dataStore).also { instance = it }
            }
        }
    }
}