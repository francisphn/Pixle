package app.pixle.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope

val TEXTMOJIS = listOf(
    "( ͡° ͜ʖ ͡°)",
    "(⌐■_■)",
    "¯\\_(ツ)_/¯",
    "ಠ_ಠ",
    "ʢ◉ᴥ◉ʡ",
    "⤜(ʘ_ʘ)⤏",
    "ᗒ ͟ʖᗕ",
    "ლ(◕෴◕ლ)",
    "(╯⩿.⪀）╯",
    "(づʘДʘ)づ",
    "(งòᗜó)ง",
)

@Composable
fun RandomTextmojiMessage(
    message: String
) {
    val (emoji, setEmoji) = remember { mutableStateOf(TEXTMOJIS.random()) }

    LaunchedEffect(message) {
        setEmoji(TEXTMOJIS.random())
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontFamily = Manrope,
            fontSize = 60.sp,
            lineHeight = 60.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.opacity(0.35f)
        )
        Text(
            text = message,
            fontFamily = Manrope,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}