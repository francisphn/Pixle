package app.pixle.ui.tabs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Environment
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.io.File


@Composable
fun CameraScreen() {
    val context = LocalContext.current

    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = androidx.compose.ui.graphics.Color.Black,
            darkIcons = false,
        )

        systemUiController.setNavigationBarColor(
            color = androidx.compose.ui.graphics.Color.Black,
            darkIcons = false
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }
    var isCapturing by remember { mutableStateOf(false) }
    var bitmap : Bitmap? by remember { mutableStateOf(null) }



    Box(
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

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(250.dp)
            )
        }

        Button(
            onClick = {

                if (!isCapturing) {
                    isCapturing = true
                    val imageCaptureCallback = object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val buffer = image.planes[0].buffer
                            val bytes = ByteArray(buffer.capacity())
                            buffer.get(bytes)
                            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)

                            // TODO: Handle where bitmap is sent

                            image.close()
                            isCapturing = false
                        }

                        override fun onError(exception: ImageCaptureException) {
                            isCapturing = false
                        }
                    }

                    cameraController.takePicture(
                        ContextCompat.getMainExecutor(context),
                        imageCaptureCallback
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(text = if (isCapturing) "Capturing..." else "Capture Photo")
        }
    }

}