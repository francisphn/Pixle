package app.pixle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.pixle.ui.composable.BottomNavigation
import app.pixle.ui.tabs.MainScreen
import app.pixle.ui.tabs.ProfileScreen
import app.pixle.ui.theme.PixleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PixleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.background),
        ) {
            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainScreen()
                }
                composable("profile") {
                    ProfileScreen()
                }
            }
        }

        BottomNavigation(navController)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PixleTheme {
        App()
    }
}