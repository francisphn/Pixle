package app.pixle.ui.composable.main

import android.content.Intent
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.database.AppPreferences
import app.pixle.lib.GameMode
import app.pixle.model.entity.attempt.Attempt
import app.pixle.ui.composable.PolaroidFrame
import app.pixle.ui.composable.RandomTextmojiMessage
import app.pixle.ui.composable.SmallButton
import app.pixle.ui.composable.TextmojiSize
import app.pixle.ui.state.rememberPreference
import app.pixle.ui.theme.Manrope
import coil.compose.AsyncImage

@Composable
fun WinningPhoto(attempts: List<Attempt>) {
    val gameMode by rememberPreference(AppPreferences::getGameModePreference,
        initialValue = AppPreferences.DEFAULT_GAME_MODE
    )

    val hasEnded = remember(gameMode, attempts) {
        gameMode == GameMode.Hard && attempts.size >= 6
    }

    val photo = remember(attempts) { attempts.lastOrNull { it.isWinningAttempt }?.winningPhoto }
    val rotation = remember(photo) { if (Math.random() < 0.5f) 1.5f else -1.5f }

    val animatedRotation = animateFloatAsState(
        targetValue = photo?.let { rotation } ?: 0f,
        label = "rotation",
        animationSpec = tween(300, 100)
    )


    photo?.let {
        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .defaultMinSize(minHeight = 150.dp),
            horizontalArrangement = Arrangement.spacedBy(
                12.dp, Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PolaroidFrame(
                modifier = Modifier
                    .rotate(animatedRotation.value)
                    .shadow(3.dp, shape = RoundedCornerShape(0.dp)),
                padding = Pair(10.dp, 24.dp),
            ) {
                AsyncImage(
                    model = it,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    contentDescription = "winning photo",
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(.6f),
                verticalArrangement = Arrangement.spacedBy(
                    6.dp, Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Snapped a winning photo!",
                    fontFamily = Manrope,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                ShareGame(attempts = attempts)
                SmallButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Save photo",
                    onClick = { /* TODO */ }
                )
            }
        }
    } ?: run {
        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .size(150.dp),
            verticalArrangement = Arrangement.spacedBy(
                8.dp, Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RandomTextmojiMessage(
                message = if (hasEnded)
                    "Welp, better luck tomorrow!"
                else
                    "No winning photo, time to go outside!",
                size = TextmojiSize.SMALL,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}