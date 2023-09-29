package app.pixle.database.repository

import app.pixle.database.dao.AttemptDao
import app.pixle.lib.Utils
import app.pixle.model.entity.attempt.AttemptWithItems
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors
import javax.inject.Inject

@AndroidEntryPoint
class AttemptRepository(@Inject private val attemptDao: AttemptDao) {
    suspend fun add(attemptWithItems: AttemptWithItems) = coroutineScope {
        launch (Dispatchers.IO) { attemptDao.insert(attemptWithItems.attempt) }
        launch (Dispatchers.IO) { attemptDao.insert(attemptWithItems.attemptItems) }
    }

    suspend fun getTodayAttemptsWithItems(): Set<AttemptWithItems> {
        return this.getAttemptsWithItemsForUtcDate(Utils.utcDate())
    }

    private suspend fun getAttemptsWithItemsForUtcDate(date: LocalDate) : Set<AttemptWithItems> {
        return attemptDao.getAttemptsWithItems(date.toString()).stream().collect(Collectors.toSet())
    }
}