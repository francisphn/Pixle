package app.pixle.ui.composable.main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.R
import app.pixle.database.AppPreferences
import app.pixle.lib.GameMode
import app.pixle.lib.Utils
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.model.entity.attempt.Attempt
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.rememberPreference
import app.pixle.ui.theme.Manrope
import java.net.URLEncoder
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareGame(attempts: List<Attempt>) {
    val context = LocalContext.current

    val today = remember { Utils.utcDate() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val (isSharing, setIsSharing) = remember { mutableStateOf(false) }

    val gameMode by rememberPreference(AppPreferences::getGameModePreference,
        initialValue = AppPreferences.DEFAULT_GAME_MODE

    )

    val unknown = stringResource(R.string.unknown)
    val header = stringResource(
        R.string.shared_content_header,
        if (gameMode == GameMode.Hard) "${attempts.size.coerceAtMost(6)}/6" else "${attempts.size}/∞",
        "${today.dayOfMonth} ${today.month.getDisplayName(TextStyle.SHORT, Locale.UK)} ${today.year}"
    )
    val footer = stringResource(R.string.shared_content_footer, attempts.firstOrNull { it.isWinningAttempt }?.location ?: unknown)

    val sharedContent = remember(attempts, gameMode) {
        val count = attempts.size.coerceAtMost(6)
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

        return@remember listOf(header, "", body, "", footer)
            .joinToString("\n")
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .clickable {
                setIsSharing(true)
            }
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.share),
            fontFamily = Manrope,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }

    if (isSharing) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .height((210 + 32 * sharedContent.split("\n").size).dp),
            sheetState = sheetState,
            onDismissRequest = { setIsSharing(false) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.share_spoiler_free),
                    fontFamily = Manrope,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .fillMaxWidth()
                            .padding(12.dp),
                    ) {
                        Text(
                            text = sharedContent,
                            fontFamily = Manrope,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .clickable {
                                val intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, sharedContent)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(intent, "Share your game")
                                context.startActivity(shareIntent)
                            }
                            .background(
                                color = MaterialTheme.colorScheme.primary.opacity(0.8f),
                                shape = CircleShape
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.share),
                            fontFamily = Manrope,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .clickable {
                                val text = URLEncoder.encode(sharedContent, "UTF-8")
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://twitter.com/intent/tweet?text=$text")
                                )
                                context.startActivity(intent)
                            }
                            .background(
                                color = MaterialTheme.colorScheme.onBackground.opacity(0.9f),
                                shape = CircleShape
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.share_on_x),
                            fontFamily = Manrope,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.background,
                        )
                    }
                }
            }
        }
    }
}