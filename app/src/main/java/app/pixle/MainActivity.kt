package app.pixle

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.pixle.asset.CAMERA_ROUTE
import app.pixle.asset.MAIN_ROUTE
import app.pixle.asset.PROFILE_ROUTE
import app.pixle.ui.composable.NavigationBuilder
import app.pixle.ui.composable.BottomNavigation
import app.pixle.ui.tabs.CameraScreen
import app.pixle.ui.composable.PhotoAnalysisSheet
import app.pixle.ui.composable.SnapProvider
import app.pixle.ui.tabs.MainScreen
import app.pixle.ui.tabs.ProfileScreen
import app.pixle.ui.theme.PixleTheme
import app.pixle.ui.theme.md_theme_dark_surfaceTint
import app.pixle.ui.theme.md_theme_dark_surfaceVariant
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.common.util.concurrent.ListenableFuture
import java.time.Duration
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PixleTheme {
//                val systemUiController = rememberSystemUiController()
//
//                SideEffect {
//                    systemUiController.setStatusBarColor(
//                        color = Ligh,
//                        darkIcons = true,
//                    )
//
//                    systemUiController.setNavigationBarColor(
//                        color = md_theme_dark_surfaceVariant,
//                        darkIcons = true
//                    )
//                }

                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun App() {
    val navController = rememberNavController()

    val navBuilder = NavigationBuilder.getInstance()
                        .toMain { navController.navigate(MAIN_ROUTE) }
                        .toCamera { navController.navigate(CAMERA_ROUTE) }
                        .toProfile { navController.navigate(PROFILE_ROUTE) }


    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        NavHost(navController = navController, startDestination = MAIN_ROUTE) {
            this.composable(MAIN_ROUTE) {
                Bootstrap(navBuilder) {
                    MainScreen()
                }
            }

            this.composable(CAMERA_ROUTE) {
                println("changing to camera")
                CameraScreen()
            }

            this.composable(PROFILE_ROUTE) {
                Bootstrap(navBuilder) {
                    ProfileScreen()
                }
            }
        }
    }
}


@Composable
fun Bootstrap(navBuilder: NavigationBuilder, composable: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            composable.invoke()
        }

        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter) {
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
