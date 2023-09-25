package app.pixle.database.repository

import app.pixle.database.dao.AttemptDao
import app.pixle.lib.Utils
import app.pixle.model.entity.attempt.AttemptWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AttemptRepository(private val attemptDao: AttemptDao) {
    suspend fun add(attemptWithItems: AttemptWithItems) = coroutineScope {
        launch (Dispatchers.IO) { attemptDao.insert(attemptWithItems.attempt) }
        launch (Dispatchers.IO) { attemptDao.insert(attemptWithItems.attemptItems) }
    }

    suspend fun getTodayAttemptsWithItems(): List<AttemptWithItems> {
        return this.getAttemptsWithItemsForUtcDate(Utils.utcDate())
    }

    private suspend fun getAttemptsWithItemsForUtcDate(date: LocalDate) : List<AttemptWithItems> {
        val dateAsString = date.toString()
        return attemptDao.getAttemptsWithItems(dateAsString)
    }
}