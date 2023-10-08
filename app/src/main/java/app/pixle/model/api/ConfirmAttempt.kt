package app.pixle.model.api

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import app.pixle.asset.IMAGE_COMPRESS_FORMAT
import app.pixle.asset.IMAGE_QUALITY_PERCENTAGE
import app.pixle.model.entity.attempt.Attempt
import app.pixle.model.entity.solution.Solution
import app.pixle.database.PixleDatabase
import app.pixle.model.entity.attempt.AtomicAttemptItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream

object ConfirmAttempt: Mutable<List<String>, Pair<Attempt, Uri>, Unit> {

    private val saveAttempt: suspend (Attempt, Context) -> Unit = { it, ctx ->
        Log.d("database", "Saving attempt to database...")
        PixleDatabase.getInstance(ctx).attemptRepository().add(it)
    }

    override val key: List<String>
        get() = listOf("attempt", "new")

    override suspend fun mutationFn(
        keys: List<String>, args: Pair<Attempt, Uri>, context: Context
    ) {

        args.takeIf { it.first.isWinningAttempt }?.let {
            it.first.winningPhoto = it.second
            saveAttempt(it.first, context)
        } ?: saveAttempt(args.first, context)
    }
}