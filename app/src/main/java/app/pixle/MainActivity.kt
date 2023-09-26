package app.pixle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.lifecycle.ProcessCameraProvider
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
import app.pixle.ui.composable.NavigationBuilder
import app.pixle.ui.composable.BottomNavigation
import app.pixle.ui.tabs.CameraScreen
import app.pixle.ui.composable.PhotoAnalysisSheet
import app.pixle.ui.composable.SnapProvider
import app.pixle.ui.tabs.MainScreen
import app.pixle.ui.tabs.ProfileScreen
import app.pixle.ui.theme.PixleTheme
import com.google.common.util.concurrent.ListenableFuture

class MainActivity : ComponentActivity() {
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

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

    val (mainRoute, profileRoute, cameraRoute) = Triple("main", "profile", "camera")

    val navigationBuilder = NavigationBuilder()
        .toMain { navController.navigate(mainRoute) }
        .toCamera { navController.navigate(cameraRoute) }
        .toProfile { navController.navigate(cameraRoute) }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {

        SnapProvider {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.background),
            ) {
                NavHost(navController = navController, startDestination = mainRoute) {
                    this.composable(mainRoute) {
                        MainScreen()
                    }

                    this.composable(cameraRoute) {
                        CameraScreen()
                    }

                    this.composable(profileRoute) {
                        ProfileScreen()
                    }
                }
            }

            BottomNavigation(navigationBuilder)

            PhotoAnalysisSheet(uri = this.uri, onDismiss = {
                delete()
            })
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