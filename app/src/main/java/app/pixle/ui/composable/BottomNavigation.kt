package app.pixle.ui.composable

import android.content.Context
import android.net.Uri
import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import app.pixle.BuildConfig
import app.pixle.lib.createTempPictureUri
import java.io.File


@Composable
fun BottomNavigation(
    navController: NavController,
    onStartCamera: (Uri) -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(bottom = 12.dp, top = 8.dp)
        ,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.navigate("main") }) {
            Icon(
                Icons.Filled.Home, contentDescription = "Main",
                modifier = Modifier
                    .size(32.dp)
            )
        }

        IconButton(
            modifier = Modifier
                .offset(y = (-8).dp)
                .scale(1.2f)
                .background(MaterialTheme.colorScheme.onBackground, CircleShape)
                .padding(2.dp),
            onClick = {
                val tempUri = context.createTempPictureUri()
                onStartCamera(tempUri)
            }
        ) {
            Icon(
                Icons.Filled.Camera, contentDescription = "Camera",
                modifier = Modifier
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.background
            )
        }

        IconButton(onClick = { navController.navigate("profile") }) {
            Icon(
                Icons.Filled.Face, contentDescription = "Profile",
                modifier = Modifier
                    .size(32.dp)
            )
        }
    }
}