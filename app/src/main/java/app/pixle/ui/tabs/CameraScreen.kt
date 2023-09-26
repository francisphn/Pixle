package app.pixle.ui.tabs

import android.graphics.Color
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView




@Composable
fun CameraScreen() {
    // val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val context = LocalContext.current
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

//@Composable
//fun cameraScreen() {
//    val context = LocalContext.current
//
//    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
//
//    cameraProviderFuture.addListener({
//        val cameraProvider = cameraProviderFuture.get()
//        bindPreview(cameraProvider, context)
//
//    }, ContextCompat.getMainExecutor(context))
//}
//
//fun bindPreview(cameraProvider : ProcessCameraProvider, context: Context) {
//    val preview : Preview = Preview.Builder()
//        .build()
//
//    val cameraSelector : CameraSelector = CameraSelector.Builder()
//        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//        .build()
//
//    preview.setSurfaceProvider(previewView.getSurfaceProvider())
//
//    var camera = cameraProvider.bindToLifecycle(context as LifecycleOwner, cameraSelector, preview)
//}