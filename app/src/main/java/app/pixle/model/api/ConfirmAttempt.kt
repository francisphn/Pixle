package app.pixle.model.api

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
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

object ConfirmAttempt: Mutable<List<String>, Pair<Attempt, Bitmap?>, Unit> {

    private val saveAttempt: suspend (Attempt, Context) -> Unit = { it, ctx ->
        Log.d("database", "Saving attempt to database...")
        PixleDatabase.getInstance(ctx).attemptRepository().add(it)
    }

    override val key: List<String>
        get() = listOf("attempt", "new")

    override suspend fun mutationFn(
        keys: List<String>, args: Pair<Attempt, Bitmap?>, context: Context) {

        args.takeIf {
            it.first.attemptItems.all { item -> item.kind == AtomicAttemptItem.KIND_EXACT }
        }?.let {
            saveWinningPhoto(it.first, it.second!!, context).collect { uri ->
                it.first.winningPhoto = uri
                saveAttempt(it.first, context)
            }
        } ?: saveAttempt(args.first, context)
    }

    private suspend fun saveWinningPhoto(attempt: Attempt, bitmap: Bitmap,
                                         context: Context): Flow<Uri> = flow {
        Log.d("Media", "Saving winning photo...")

        val filename = "${attempt.solutionDate}_${attempt.uuid}"
        val file = File(context.filesDir, filename)

        // Emit the Uri immediately
        emit(file.toUri())

        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.flush()
        }

    }.flowOn(Dispatchers.IO)
}