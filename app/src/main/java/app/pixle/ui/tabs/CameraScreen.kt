package app.pixle.ui.tabs

import android.graphics.Color
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState


@Composable
fun CameraScreen(
    hasCameraPermission: Boolean,
    handleRequestPermission: () -> Unit
) {
    val context = LocalContext.current

    if (!hasCameraPermission) {
        handleRequestPermission.invoke()

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Grant Pixle access to the camera to start tracking things."
            )
            
            Button(onClick = handleRequestPermission) {
                Text(text = "Grant camera permission")
            }
        }




    } else {
        val lifecycleOwner = LocalLifecycleOwner.current
        val cameraController = remember { LifecycleCameraController(context) }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        this.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        this.setBackgroundColor(Color.BLACK)
                        this.scaleType = PreviewView.ScaleType.FILL_START
                    }.also { view ->
                        view.controller = cameraController
                        cameraController.bindToLifecycle(lifecycleOwner)
                    }
                }
            )
        }
    }


}