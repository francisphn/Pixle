package app.pixle.ui.tabs

import android.Manifest
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG
import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController.IMAGE_CAPTURE
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import app.pixle.R
import app.pixle.ui.composable.NavigationBuilder
import app.pixle.ui.composable.camera.SmartPreview
import app.pixle.ui.composable.camera.SmartPreviewToggle
import app.pixle.ui.composable.camera.PhotoAnalysisSheet
import app.pixle.ui.modifier.opacity
import app.pixle.ui.state.rememberSoundEffect
import app.pixle.ui.theme.Translucent
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import java.util.concurrent.Executors

private val flashModes = mapOf(
    Pair(FLASH_MODE_AUTO, Icons.Filled.FlashAuto),
    Pair(FLASH_MODE_ON, Icons.Filled.FlashOn),
    Pair(FLASH_MODE_OFF, Icons.Filled.FlashOff)
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalZeroShutterLag::class)
fun CameraScreen(navBuilder: NavigationBuilder) {
    var rotationState by remember { mutableFloatStateOf(0f) }

    val rotation by animateFloatAsState(
        targetValue = rotationState,
        animationSpec = tween(700),
        label = "rotation"
    )

    val (isDetectingMotion, setIsDetectingMotion) = rememberSaveable { mutableStateOf(false) }
    val permission = rememberPermissionState(Manifest.permission.ACTIVITY_RECOGNITION) {
        setIsDetectingMotion(it)
    }

    // Keep track of how many times we've captured an image
    val captureCount = remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val shutterSound = rememberSoundEffect(R.raw.shutter)
    val cameraController = remember { LifecycleCameraController(context) }
    val executor = Executors.newSingleThreadExecutor()

    cameraController.setEnabledUseCases(IMAGE_CAPTURE)
    cameraController.imageCaptureMode = CAPTURE_MODE_ZERO_SHUTTER_LAG

    var isCapturing by remember { mutableStateOf(false) }
    var uri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val scope = rememberCoroutineScope()

    val scale by animateFloatAsState(
        targetValue = if (isCapturing) 40f else 60f,
        label = "scale",
        animationSpec = tween(125)
    )

    var currentFlashMode by rememberSaveable { mutableIntStateOf(FLASH_MODE_OFF) }
    var currentCameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    val isBackCamera = currentCameraSelector == CameraSelector.DEFAULT_BACK_CAMERA

    cameraController.cameraSelector = currentCameraSelector

    var isLoaded by remember { mutableStateOf(false) }

    val imageSavedCallback = object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            uri = outputFileResults.savedUri?.also {
                isCapturing = false
                isLoaded = true
            }
            captureCount.intValue += 1
        }

        override fun onError(exception: ImageCaptureException) {
            isCapturing = false
            isLoaded = true
        }
    }

    // This is a hack to reset the camera after a few s
    LaunchedEffect(captureCount, currentCameraSelector) {
        if (captureCount.intValue < 10) return@LaunchedEffect
        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        cameraController.cameraSelector = currentCameraSelector
    }

    LaunchedEffect(Unit) {
        cameraController.initializationFuture.addListener(
            kotlinx.coroutines.Runnable {
                isLoaded = true
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
        }

        onDispose {
            cameraController.unbind()
        }
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Translucent())
                            .aspectRatio(1F)
                            .clip(RoundedCornerShape(2.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                }
            }


            IconButton(
                onClick = {
                    if (!isCapturing) {
                        isCapturing = true
                        isLoaded = false

                        val filename = UUID.randomUUID().toString()
                        val file = File(context.filesDir, filename)
                        cameraController.imageCaptureFlashMode = currentFlashMode
                        cameraController.takePicture(
                            OutputFileOptions.Builder(file).build(),
                            executor,
                            imageSavedCallback
                        )
                        shutterSound.start()
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



                SmartPreviewToggle(
                    isDetectingMotion = isDetectingMotion,
                    onEnable = {
                        if (!permission.status.isGranted) {
                            permission.launchPermissionRequest()
                        } else {
                            setIsDetectingMotion(true)
                        }
                    },
                    onDisable = {
                        setIsDetectingMotion(false)
                    }
                )
            }

            // Bottom bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                uri?.let {
                    AsyncImage(
                        model = it,
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

                        rotationState = rotationState.plus(-360f)

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


        SmartPreview(
            uri = uri,
            isDetectingMotion = isDetectingMotion,
            captureCount = captureCount,
            cameraController = cameraController
        )


        // Photo analysis sheet
        PhotoAnalysisSheet(
            uri = uri,
            onDismiss = {
                uri = null
                isLoaded = true
            },
            onConfirm = {
                uri = null
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
                this.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
            }.also { view ->
                view.controller = cameraController
                cameraController.bindToLifecycle(lifecycleOwner)
            }
        }
    )
}