package app.pixle.ui.composable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun CustomSystemUI(
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()

    val defaultNavBarColour = MaterialTheme.colorScheme.surfaceVariant;
    val defaultStatusBarColour = MaterialTheme.colorScheme.background;
    val useDarkTheme = isSystemInDarkTheme()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = defaultStatusBarColour,
            darkIcons = !useDarkTheme,
        )

        systemUiController.setNavigationBarColor(
            color = defaultNavBarColour,
            darkIcons = false
        )
    }

    content()
}