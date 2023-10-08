package app.pixle.ui.composable.twicedown

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import app.pixle.MainActivity
import app.pixle.R
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.ui.composable.PhotoItem
import app.pixle.ui.composable.PolaroidFrame
import app.pixle.ui.modifier.opacity
import app.pixle.ui.theme.Manrope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwiceDownSheet(onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val requiredPermissions = listOf(
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.NEARBY_WIFI_DEVICES,
    )

    var permissionsState by remember {
        mutableStateOf(
            requiredPermissions.all {
                ActivityCompat.checkSelfPermission(
                    context, it
                ) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    LaunchedEffect(Unit, permissionsState) {
        Log.d("pixle:debug", "User granted all permissions: $permissionsState")

        // log every permission that has been granted and that has not been granted
        requiredPermissions.forEach {
            Log.d("pixle:debug", "Permission $it granted: ${ActivityCompat.checkSelfPermission(
                context, it
            ) == PackageManager.PERMISSION_GRANTED}")
        }
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted ->
        if (isGranted.all { it.value }) {
            permissionsState = true

            onDismiss()
            scope.launch {
                sheetState.hide()
            }

        } else {
            permissionsState = false
        }
    }

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.90f)
            .zIndex(40f),
        sheetState = sheetState,
        onDismissRequest = {
            scope.launch {
                onDismiss()
                sheetState.hide()
            }
        },
    ) {
        
        Column(
            Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {
            Text(
                text = "Twice Down",
                fontFamily = Manrope,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Play with a friend",
                fontFamily = Manrope,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            if (permissionsState) {

                Text(text = "You've granted permissions! All your attempts will be forfeit")

            } else {
                Text(
                    text = "Permissions",

                    fontFamily = Manrope,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Twice Down Mode requires Pixle to have permission to use your Bluetooth and WiFi connections.",

                    modifier = Modifier.padding(bottom = 30.dp),

                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal
                )

                Box(modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onTertiaryContainer)
                    .clickable {
                        scope.launch {
                            if (permissionsState) {
                                onDismiss()
                                sheetState.hide()
                            } else {
                                permissionsLauncher.launch(requiredPermissions.toTypedArray())
                            }
                        }
                    }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground.opacity(0.25f),
                        shape = CircleShape
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                    contentAlignment = Alignment.Center) {

                    Text(
                        text = "Grant permission",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface,
                    )
                }
            }
        }
    }
}