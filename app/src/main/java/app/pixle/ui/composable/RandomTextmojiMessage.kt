package app.pixle.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope

private val positiveReactions = listOf(
    "( ͡° ͜ʖ ͡°)",
    "(⌐■_■)",
    "ʢ◉ᴥ◉ʡ",
    "¯\\_(ツ)_/¯",
    "ლ(◕෴◕ლ)",
    "(づʘДʘ)づ"
)

private val negativeReactions = listOf(
    "ᗒ ͟ʖᗕ",
    "ಠ_ಠ",
    "⤜(ʘ_ʘ)⤏",
    "(╯⩿.⪀）╯",
    "(งòᗜó)ง"
)

enum class TextmojiSize(val emoji: TextUnit, val message: TextUnit) {
    SMALL(40.sp, 12.sp),
    MEDIUM(60.sp, 16.sp),
    LARGE(80.sp, 20.sp),
}

enum class TextmojiTone {
    Positive,
    Negative,
    Any,
}

@Composable
fun RandomTextmojiMessage(
    modifier: Modifier = Modifier,
    message: String? = null,
    size: TextmojiSize = TextmojiSize.MEDIUM,
    textmojiTone: TextmojiTone = TextmojiTone.Any
) {
    val textmojis = when (textmojiTone) {
        TextmojiTone.Negative -> negativeReactions
        TextmojiTone.Positive -> positiveReactions
        else -> positiveReactions + negativeReactions
    }

    val (emoji, setEmoji) = remember { mutableStateOf(textmojis.first()) }

    LaunchedEffect(message) {
        setEmoji(textmojis.random())
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

        message?.let {
            Text(
                text = it,
                fontFamily = Manrope,
                fontSize = size.message,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

