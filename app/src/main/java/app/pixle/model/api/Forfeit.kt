package app.pixle.model.api

import android.content.Context
import app.pixle.database.PixleDatabase
import app.pixle.lib.Utils
import app.pixle.model.api.contracts.Mutable

object Forfeit: Mutable<List<String>, Unit, Unit> {
    override val key: List<String>
        get() = listOf("attempt", "forfeit", Utils.utcDate().toString())

    override suspend fun mutationFn(keys: List<String>, args: Unit, context: Context) {
        PixleDatabase.getInstance(context).attemptRepository().deleteAttemptsOfToday()
    }
}