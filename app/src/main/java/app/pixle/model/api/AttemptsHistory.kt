package app.pixle.model.api

import android.content.Context
import app.pixle.database.PixleDatabase
import app.pixle.model.api.contracts.Queryable
import app.pixle.model.entity.attempt.Attempt
import java.time.LocalDate

object AttemptsHistory: Queryable<List<String>, List<Pair<LocalDate, List<Attempt>>>> {
    override val key: List<String>
        get() = listOf("attempts", "all")

    override suspend fun queryFn(keys: List<String>, context: Context): List<Pair<LocalDate, List<Attempt>>> {
        return PixleDatabase
            .getInstance(context)
            .attemptRepository()
            .getAttemptsOfEachDay()
    }
}