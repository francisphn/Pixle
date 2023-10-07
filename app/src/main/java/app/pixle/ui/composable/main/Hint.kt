package app.pixle.ui.composable.main

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.database.AppPreferences
import app.pixle.lib.GameMode
import app.pixle.model.entity.attempt.Attempt
import app.pixle.ui.state.rememberPreference
import app.pixle.ui.theme.Manrope

@Composable
fun Hint(attempts: List<Attempt>, color: Color) {
    val gameMode by rememberPreference(
        AppPreferences::getGameModePreference,
        initialValue = AppPreferences.DEFAULT_GAME_MODE
    )

    val hintAvailable = remember(gameMode, attempts) {
        gameMode == GameMode.Easy || attempts.size > 3
    }

    if (hintAvailable) {
        Text(
            text = " •",
            fontFamily = Manrope,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        )

        IconButton(
            modifier = Modifier
                .size(21.dp),
            onClick = {
            },
        ) {
            Icon(
                Icons.Filled.Lightbulb,
                contentDescription = "hint",
                tint = color,
                modifier = Modifier
                    .size(14.dp)
            )
        }
    }
}