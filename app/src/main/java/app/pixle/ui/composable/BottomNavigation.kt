package app.pixle.ui.composable

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import app.pixle.R
import app.pixle.asset.CAMERA_ROUTE
import app.pixle.asset.MAIN_ROUTE
import app.pixle.asset.PROFILE_ROUTE
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BottomNavigation(
    navBuilder: NavigationBuilder, backStackEntry: NavBackStackEntry?
) {

    val context = LocalContext.current
    val permissionsRequiredMessage = stringResource(R.string.permissions_required)
    val cameraAndLocationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val locationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (!cameraGranted || !locationGranted) {
            val toast = Toast.makeText(
                context,
                permissionsRequiredMessage,
                Toast.LENGTH_LONG
            )
            toast.show()
        } else {
            navBuilder.navigateToCamera()
        }
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .navigationBarsPadding()
            .padding(top = 8.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Main screen
        IconButton(
            onClick = navBuilder.navigateToMain,
            enabled = backStackEntry?.destination?.route != MAIN_ROUTE
        ) {
            Icon(
                Icons.Filled.Home, contentDescription = "Main", modifier = Modifier.size(32.dp)
            )
        }

        IconButton(
            modifier = Modifier
                .offset(y = (-8).dp)
                .scale(1.2f)
                .background(MaterialTheme.colorScheme.onBackground, CircleShape)
                .padding(2.dp),
            onClick = {
                if (cameraAndLocationPermissionState.allPermissionsGranted) {
                    navBuilder.navigateToCamera()
                } else {
                    cameraAndLocationPermissionState.launchMultiplePermissionRequest()
                }
            },
            enabled = backStackEntry?.destination?.route != CAMERA_ROUTE
        ) {
            Icon(
                Icons.Filled.Camera,
                contentDescription = "Camera",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.background
            )
        }

        IconButton(
            onClick = navBuilder.navigateToProfile,
            enabled = backStackEntry?.destination?.route != PROFILE_ROUTE
        ) {
            Icon(
                Icons.Filled.Face, contentDescription = "Profile", modifier = Modifier.size(32.dp)
            )
        }
    }
}
