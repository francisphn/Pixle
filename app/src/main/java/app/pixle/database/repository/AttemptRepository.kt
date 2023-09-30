package app.pixle.database.repository

import app.pixle.database.dao.AttemptDao
import app.pixle.lib.Utils
import app.pixle.model.entity.attempt.Attempt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.stream.Collectors

class AttemptRepository(private val attemptDao: AttemptDao) {
    suspend fun add(attempt: Attempt) = coroutineScope {
        launch (Dispatchers.IO) { attemptDao.insert(attempt.attempt) }
        launch (Dispatchers.IO) { attemptDao.insert(attempt.attemptItems) }
    }

    suspend fun getTodayAttemptsWithItems(): Set<Attempt> {
        return this.getAttemptsWithItemsForUtcDate(Utils.utcDate())
    }

    private suspend fun getAttemptsWithItemsForUtcDate(date: LocalDate) : Set<Attempt> {
        return attemptDao.getAttemptsWithItems(date.toString()).stream().collect(Collectors.toSet())
    }
}