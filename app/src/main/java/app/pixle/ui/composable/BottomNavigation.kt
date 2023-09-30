package app.pixle.ui.composable

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavBackStackEntry
import app.pixle.asset.CAMERA_ROUTE
import app.pixle.asset.MAIN_ROUTE
import app.pixle.asset.PROFILE_ROUTE

@Composable
fun BottomNavigation(
    navBuilder: NavigationBuilder, backStackEntry: NavBackStackEntry?
) {

    val context = LocalContext.current
    var cameraPermissionState by remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraPermissionState = true
            navBuilder.navigateToCamera()
        } else {
            cameraPermissionState = false
            Toast.makeText(
                context,
                "Pixle does not have permissions to access camera",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(bottom = 12.dp, top = 8.dp),
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
                if (cameraPermissionState) {
                    navBuilder.navigateToCamera()
                } else {
                    permissionsLauncher.launch(Manifest.permission.CAMERA)
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