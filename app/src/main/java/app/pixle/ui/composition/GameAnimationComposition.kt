package app.pixle.ui.composition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay


data class GameAnimation(
    val state: State = State.IDLE,
    val setState: (State) -> Unit = {},
) {
    enum class State {
        IDLE,
        ATTEMPT,
        WIN,
    }
}

val LocalGameAnimation = compositionLocalOf { GameAnimation() }

@Composable
fun GameAnimationProvider(
    content: @Composable () -> Unit,
) {
    val (state, setState) = remember { mutableStateOf(GameAnimation.State.IDLE) }
    val gameAnimation = remember(state, setState) {
        GameAnimation(state, setState)
    }

    CompositionLocalProvider(LocalGameAnimation provides gameAnimation) {
        content()
    }
}