package app.pixle.ui.composable.main

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import app.pixle.database.AppPreferences
import app.pixle.lib.GameMode
import app.pixle.lib.Utils
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.model.entity.attempt.Attempt
import app.pixle.ui.composable.SmallButton
import app.pixle.ui.state.rememberPreference
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ShareGame(attempts: List<Attempt>) {
    val context = LocalContext.current
    val gameMode by rememberPreference(AppPreferences::getGameModePreference,
        initialValue = AppPreferences.DEFAULT_GAME_MODE
    )

    val sharedContent = remember(attempts, gameMode) {
        val today = Utils.utcDate()
        val date = "${today.dayOfMonth} ${today.month.getDisplayName(TextStyle.SHORT, Locale.UK)} ${today.year}"
        val count = attempts.size.coerceAtMost(6)
        val ratio = if (gameMode == GameMode.Hard) "$count/6" else "${attempts.size}/∞"

        val header =  "#Pixle • \uD83D\uDCF8${ratio} • (on $date)"

        val body = attempts
            .takeLast(count)
            .joinToString("\n") { attempt ->
                attempt
                    .attemptItems
                    .joinToString("") { item ->
                        when (item.kind) {
                            AtomicAttemptItem.KIND_EXACT -> "\uD83D\uDFE9"
                            AtomicAttemptItem.KIND_SIMILAR -> "\uD83D\uDFE8"
                            else -> "⬛"
                        }
                    }

            }

        return@remember listOf(header, "", body)
            .joinToString("\n")
    }

    SmallButton(
        modifier = Modifier.fillMaxWidth(),
        label = "Share",
        onClick = {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, sharedContent)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, "Share your game")
            context.startActivity(shareIntent)
        }
    )
}