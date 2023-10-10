package app.pixle

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.pixle.asset.CAMERA_ROUTE
import app.pixle.asset.MAIN_ROUTE
import app.pixle.asset.PREFERENCES_ROUTE
import app.pixle.asset.PROFILE_ROUTE
import app.pixle.model.api.Library
import app.pixle.notification.alarm.AlarmBroadcaster
import app.pixle.ui.composable.BottomNavigation
import app.pixle.ui.composable.NavigationBuilder
import app.pixle.ui.composable.main.OnboardingSheet
import app.pixle.ui.composition.GameAnimationProvider
import app.pixle.ui.composition.ObjectDetectionProvider
import app.pixle.ui.state.rememberQueryablePreload
import app.pixle.ui.tabs.CameraScreen
import app.pixle.ui.tabs.MainScreen
import app.pixle.ui.tabs.Preferences
import app.pixle.ui.tabs.ProfileScreen
import app.pixle.ui.theme.PixleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("pixle:debug", "Main activity created")

        AlarmBroadcaster
            .getInstance(this)
            .setRepeatingAlarm()

        setContent {
            PixleTheme {
                GameAnimationProvider {
                    ObjectDetectionProvider {
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
    }

}

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    val navBuilder = NavigationBuilder.getInstance()
        .toMain { navController.navigate(MAIN_ROUTE) }
        .toCamera { navController.navigate(CAMERA_ROUTE) }
        .toProfile { navController.navigate(PROFILE_ROUTE) }
        .toPreferences { navController.navigate(PREFERENCES_ROUTE) }
        .back { navController.popBackStack() }


    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val systemUiController = rememberSystemUiController()
    val defaultNavBarColour = MaterialTheme.colorScheme.surfaceVariant
    val defaultStatusBarColour = MaterialTheme.colorScheme.background
    val useDarkTheme = isSystemInDarkTheme()

    val preloadLib = rememberQueryablePreload(Library)

    LaunchedEffect(Unit) {
        scope.launch {
            preloadLib()
        }
    }



    // Change system bars color when the screen state changes
    LaunchedEffect(navBackStackEntry?.destination?.route) {
        if (navBackStackEntry?.destination?.route == CAMERA_ROUTE) {
            systemUiController.setStatusBarColor(
                color = Color.Black,
                darkIcons = false,
            )

            systemUiController.setNavigationBarColor(
                color = Color.Black, darkIcons = false
            )
            return@LaunchedEffect
        }

        systemUiController.setStatusBarColor(
            color = defaultStatusBarColour,
            darkIcons = !useDarkTheme,
        )

        systemUiController.setNavigationBarColor(
            color = defaultNavBarColour, darkIcons = false
        )
    }

    OnboardingSheet()

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            NavHost(navController = navController, startDestination = MAIN_ROUTE) {
                composable(
                    route = MAIN_ROUTE,
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(500)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(500)
                        )
                    }
                ) {
                    MainScreen()
                }

                composable(
                    route = CAMERA_ROUTE,
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(500)
                        )
                    },
                    exitTransition = {
                        when (navBackStackEntry?.destination?.route) {
                            MAIN_ROUTE -> slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )

                            PROFILE_ROUTE -> slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )
                            else -> slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                                animationSpec = tween(500)
                            )
                        }
                    }
                ) {
                    CameraScreen(navBuilder)
                }

                composable(
                    route = PROFILE_ROUTE,
                    enterTransition = {
                        if (initialState.destination.route == PREFERENCES_ROUTE) {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )
                        } else {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )
                        }

                    },
                    exitTransition = {
                        if (targetState.destination.route == PREFERENCES_ROUTE) {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(500)
                            )
                        } else {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )
                        }
                    }
                ) {
                    ProfileScreen(navBuilder)
                }
                composable(
                    route = PREFERENCES_ROUTE,
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(500)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(500)
                        )
                    }
                ) {
                    Preferences(navBuilder)
                }
            }
        }

        AnimatedVisibility(
            visible = navBackStackEntry?.destination?.route != CAMERA_ROUTE,
            enter = slideInVertically(initialOffsetY = { 100 }),
            exit = slideOutVertically(targetOffsetY = { 100 })
        ) {
            BottomNavigation(navBuilder, navBackStackEntry)
        }
    }
}