package app.pixle.ui.state

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberSoundEffect(mediaId: Int): MediaPlayer {
    val context = LocalContext.current
    return remember(context) { MediaPlayer.create(context, mediaId) }
}