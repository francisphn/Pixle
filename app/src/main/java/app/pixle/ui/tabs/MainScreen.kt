package app.pixle.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun MainScreen() {
    val systemUiController = rememberSystemUiController()

    val defaultNavBarColour = MaterialTheme.colorScheme.surfaceVariant;
    val defaultStatusBarColour = MaterialTheme.colorScheme.background;

    SideEffect {
        systemUiController.setStatusBarColor(
            color = defaultStatusBarColour,
            darkIcons = false,
        )

        systemUiController.setNavigationBarColor(
            color = defaultNavBarColour,
            darkIcons = false
        )
    }

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello Pixle"
        )
    }
}