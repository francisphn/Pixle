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
import androidx.compose.ui.unit.TextUnit
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

enum class TextmojiSize(val emoji: TextUnit, val message: TextUnit) {
    SMALL(40.sp, 12.sp),
    MEDIUM(60.sp, 16.sp),
    LARGE(80.sp, 20.sp),
}

@Composable
fun RandomTextmojiMessage(
    message: String,
    modifier: Modifier = Modifier,
    size: TextmojiSize = TextmojiSize.MEDIUM
) {
    val (emoji, setEmoji) = remember { mutableStateOf(TEXTMOJIS.first()) }

    LaunchedEffect(message) {
        setEmoji(TEXTMOJIS.random())
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontFamily = Manrope,
            fontSize = size.emoji,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.opacity(0.35f)
        )
        Text(
            text = message,
            fontFamily = Manrope,
            fontSize = size.message,
            fontWeight = FontWeight.Medium,
        )
    }
}