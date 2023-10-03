package app.pixle.ui.tabs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import app.pixle.model.api.AttemptsOfToday
import app.pixle.model.api.ConfirmAttempt
import app.pixle.model.api.SolutionOfToday
import app.pixle.ui.composable.LoadingScreen
import app.pixle.ui.composable.NavigationBuilder
import app.pixle.ui.composable.camera.PhotoAnalysisSheet
import app.pixle.ui.composition.GameAnimation
import app.pixle.ui.composition.LocalGameAnimation
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.rememberInvalidate
import app.pixle.ui.state.rememberMutable
import app.pixle.ui.state.rememberQuery
import app.pixle.ui.state.rememberQueryable
import app.pixle.ui.theme.Translucent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun CameraScreen(navBuilder: NavigationBuilder) {
    val flashModes = mapOf(
        Pair(FLASH_MODE_AUTO, Icons.Filled.FlashAuto),
        Pair(FLASH_MODE_ON, Icons.Filled.FlashOn),
        Pair(FLASH_MODE_OFF, Icons.Filled.FlashOff)
    )

    var rotationState by remember { mutableFloatStateOf(0f) }

    val rotation by animateFloatAsState(
        targetValue = rotationState,
        animationSpec = tween(700),
        label = "rotation"
    )


    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraController = remember { LifecycleCameraController(context) }
    var isCapturing by remember { mutableStateOf(false) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val scope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isCapturing) 40f else 60f,
        label = "scale",
        animationSpec = tween(125)
    )

    val imageCaptureCallback = object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)
            val tempBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)

            val matrix = Matrix();
            matrix.postRotate(90f);

            bitmap = Bitmap.createBitmap(
                tempBitmap,
                0,
                0,
                tempBitmap.getWidth(),
                tempBitmap.getHeight(),
                matrix,
                true
            )

            image.close()
            isCapturing = false
        }

        override fun onError(exception: ImageCaptureException) {
            isCapturing = false
        }
    }


    var currentFlashMode by remember { mutableIntStateOf(FLASH_MODE_AUTO) }

    var currentCameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }

    val isBackCamera = currentCameraSelector == CameraSelector.DEFAULT_BACK_CAMERA

    cameraController.imageCaptureFlashMode = currentFlashMode
    cameraController.cameraSelector = currentCameraSelector

    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1000)
        isLoaded = true
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
            verticalArrangement = Arrangement.spacedBy(70.dp, Alignment.CenterVertically)
        ) {

            Box {
                CameraView(cameraController = cameraController, lifecycleOwner = lifecycleOwner)

                androidx.compose.animation.AnimatedVisibility(visible = !isLoaded, enter = fadeIn(), exit = fadeOut()) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Translucent())
                        .aspectRatio(1F)
                        .clip(RoundedCornerShape(2.dp))) {
                        LoadingScreen()
                    }

                }
            }


            IconButton(
                onClick = {
                    if (!isCapturing) {
                        isCapturing = true
                        isLoaded = false

                        cameraController.takePicture(
                            ContextCompat.getMainExecutor(context),
                            imageCaptureCallback
                        ) // todo: make this async

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
                        navBuilder.navigateBack()
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
                    onClick = {
                        currentFlashMode = currentFlashMode.inc().mod(3)
                    },
                    enabled = isBackCamera
                ) {
                    Icon(
                        if (isBackCamera)
                            flashModes[currentFlashMode] ?: Icons.Filled.FlashAuto
                        else
                            Icons.Filled.FlashOff,

                        contentDescription = "switch flash",
                        tint = if (isBackCamera) Color.White else Color.DarkGray,
                        modifier = Modifier.size(28.dp)
                    )
                }

                IconButton(
                    onClick = {
                        /* TODO: More settings stuff if needed */
                    }
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
                    onClick = {
                        isLoaded = false

                        rotationState = rotationState.plus(360f)

                        scope.launch {
                            delay(500)

                            currentCameraSelector = if (isBackCamera) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                        }.invokeOnCompletion {
                            isLoaded = true
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.Cameraswitch,
                        contentDescription = "switch camera",
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .graphicsLayer {
                                this.rotationZ = rotation
                            }
                    )
                }
            }
        }


        // Photo analysis sheet
        PhotoAnalysisSheet(
            bitmap = bitmap,
            onDismiss = {
                bitmap = null
                isLoaded = true
            },
            onConfirm = {
                bitmap = null
                navBuilder.navigateToMain()
            }
        )
    }

}

@Composable
fun CameraView(cameraController: LifecycleCameraController, lifecycleOwner: LifecycleOwner) {
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
}