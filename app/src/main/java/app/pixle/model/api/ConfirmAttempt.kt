package app.pixle.model.api

import android.content.Context
import android.net.Uri
import android.util.Log
import app.pixle.model.entity.attempt.Attempt
import app.pixle.database.PixleDatabase
import app.pixle.lib.GameMode
import app.pixle.model.api.contracts.Mutable

object ConfirmAttempt: Mutable<List<String>, Triple<Attempt, Uri, GameMode>, Unit> {
    override val key: List<String>
        get() = listOf("attempt", "new")

    override suspend fun mutationFn(keys: List<String>, args: Triple<Attempt, Uri, GameMode>, context: Context) {
        val repo = PixleDatabase.getInstance(context).attemptRepository()
        val attemptAlready = repo.getAttemptsOfToday().size

        if (args.third == GameMode.Hard && attemptAlready >= 6) {
            Log.d("database", "Attempt not saved because it's the 6th attempt of the day")
            return
        }

        Log.d("database", "Saving attempt to database...")
        args.takeIf { it.first.isWinningAttempt }?.let {
            it.first.winningPhoto = it.second
            repo.add(it.first)
        } ?: repo.add(args.first)
    }
}