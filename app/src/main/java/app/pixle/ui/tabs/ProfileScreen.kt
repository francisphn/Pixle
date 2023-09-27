package app.pixle.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.pixle.ui.theme.Manrope
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun ProfileScreen() {
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
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            fontFamily = Manrope
        )
    }
}