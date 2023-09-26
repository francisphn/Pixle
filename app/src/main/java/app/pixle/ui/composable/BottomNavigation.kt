package app.pixle.ui.composable

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigation(
    navBuilder: NavigationBuilder
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(bottom = 12.dp, top = 8.dp)
        ,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Main screen
        IconButton(onClick = navBuilder.navigateToMain) {
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
            onClick = navBuilder.navigateToCamera
        ) {
            Icon(
                Icons.Filled.Camera, contentDescription = "Camera",
                modifier = Modifier
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.background
            )
        }

        IconButton(onClick = navBuilder.navigateToProfile ) {
            Icon(
                Icons.Filled.Face, contentDescription = "Profile",
                modifier = Modifier
                    .size(32.dp)
            )
        }
    }
}