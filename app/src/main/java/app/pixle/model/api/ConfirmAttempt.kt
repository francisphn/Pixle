package app.pixle.model.api

import android.content.Context
import app.pixle.database.PixleDatabase
import app.pixle.model.entity.attempt.Attempt

object ConfirmAttempt: Mutable<List<String>, Attempt, Unit> {
    override val key: List<String>
        get() = listOf("attempt", "new")

    override suspend fun mutationFn(keys: List<String>, args: Attempt, context: Context) {
        PixleDatabase
            .getInstance(context)
            .attemptRepository()
            .add(args)

    }
}