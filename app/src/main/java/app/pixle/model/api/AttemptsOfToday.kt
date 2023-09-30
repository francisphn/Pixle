package app.pixle.model.api

import android.content.Context
import app.pixle.database.PixleDatabase
import app.pixle.lib.Utils
import app.pixle.model.entity.attempt.Attempt

object AttemptsOfToday: Queryable<List<String>, List<Attempt>> {
    override val key: List<String>
        get() = listOf("attempts", Utils.utcDate().toString())

    override suspend fun queryFn(keys: List<String>, context: Context): List<Attempt> {
        return PixleDatabase
            .getInstance(context)
            .attemptRepository()
            .getAttemptsOfToday()
    }
}