package app.pixle.ui.tabs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import app.pixle.ui.composable.NavigationBuilder
import app.pixle.ui.modifier.opacity
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun CameraScreen(navBuilder: NavigationBuilder) {
    val context = LocalContext.current

    val systemUiController = rememberSystemUiController()
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }
    var isCapturing by remember { mutableStateOf(false) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val scale by animateFloatAsState(
        targetValue = if (isCapturing) 40f else 60f,
        label = "scale",
        animationSpec = tween(125)
    )

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Black,
            darkIcons = false,
        )

        systemUiController.setNavigationBarColor(
            color = Color.Black,
            darkIcons = false
        )
    }


    Box(
        modifier = Modifier
    ) {

        // Camera preview and capture button
        Column(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxSize()
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F)
                    .clip(RoundedCornerShape(2.dp)),
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        this.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        this.scaleType = PreviewView.ScaleType.FILL_START
                    }.also { view ->
                        view.controller = cameraController
                        cameraController.bindToLifecycle(lifecycleOwner)
                    }
                }
            )



            IconButton(
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
                    .size(74.dp)
                    .border(
                        width = 4.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
                    .align(Alignment.CenterHorizontally),
            ) {

                Icon(
                    imageVector = Icons.Filled.Camera,
                    contentDescription = "snap",
                    modifier = Modifier
                        .size(scale.dp)
                        .background(Color.White, CircleShape),
                    tint = Color.Black.opacity(.025f)
                )
            }
        }

        // Top and bottom tool bars
        Column(
            modifier = Modifier
                .zIndex(2f)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        navBuilder.navigateToMain()
                    }
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "close",
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                    )
                }

                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        Icons.Filled.FlashAuto,
                        contentDescription = "switch flash",
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                    )
                }

                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "settings",
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                    )
                }
            }

            // Bottom bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "captured image",
                        modifier = Modifier
                            .size(35.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(9.dp)
                            )
                    )
                } ?:
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .background(Color.DarkGray.opacity(.5f), RoundedCornerShape(9.dp))
                        .border(
                            width = 2.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(9.dp)
                        )
                )


                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        Icons.Filled.Cameraswitch,
                        contentDescription = "switch camera",
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                    )
                }
            }

        }
    }

}