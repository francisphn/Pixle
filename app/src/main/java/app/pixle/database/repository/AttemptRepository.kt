package app.pixle.database.repository

import app.pixle.database.dao.AttemptDao
import app.pixle.lib.Utils
import app.pixle.model.entity.attempt.Attempt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class AttemptRepository(private val attemptDao: AttemptDao) {
    suspend fun add(attempt: Attempt) = coroutineScope {
        launch (Dispatchers.IO) { attemptDao.insert(attempt.attempt) }
        launch (Dispatchers.IO) { attemptDao.insert(attempt.attemptItems) }
    }

    suspend fun getAttemptsOfToday(): List<Attempt> {
        return this.getAttemptsOfUtcDate(Utils.utcDate())
    }

    private suspend fun getAttemptsOfUtcDate(date: LocalDate) : List<Attempt> {
        return attemptDao
            .getAttemptsWithItems(date)
            .distinct()
    }
}