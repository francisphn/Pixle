package app.pixle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.pixle.asset.CAMERA_ROUTE
import app.pixle.asset.MAIN_ROUTE
import app.pixle.asset.PROFILE_ROUTE
import app.pixle.ui.composable.BottomNavigation
import app.pixle.ui.composable.CustomSystemUI
import app.pixle.ui.composable.NavigationBuilder
import app.pixle.ui.tabs.CameraScreen
import app.pixle.ui.tabs.MainScreen
import app.pixle.ui.tabs.ProfileScreen
import app.pixle.ui.theme.PixleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PixleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
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

    val navBuilder = NavigationBuilder.getInstance()
        .toMain { navController.navigate(MAIN_ROUTE) }
        .toCamera { navController.navigate(CAMERA_ROUTE) }
        .toProfile { navController.navigate(PROFILE_ROUTE) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()


    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            NavHost(navController = navController, startDestination = CAMERA_ROUTE) {
                this.composable(MAIN_ROUTE) {
                    CustomSystemUI {
                        MainScreen()
                    }
                }

                this.composable(CAMERA_ROUTE) {
                    CameraScreen(navBuilder)
                }

                this.composable(PROFILE_ROUTE) {
                    CustomSystemUI {
                        ProfileScreen()
                    }
                }
            }
        }

        if (navBackStackEntry?.destination?.route != CAMERA_ROUTE) {
            BottomNavigation(navBuilder)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PixleTheme {
        App()
    }
}
