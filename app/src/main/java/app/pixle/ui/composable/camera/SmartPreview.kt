package app.pixle.ui.composable.camera

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import app.pixle.model.api.Library
import app.pixle.model.api.SolutionOfToday
import app.pixle.model.entity.attempt.AtomicAttempt
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.model.entity.attempt.Attempt
import app.pixle.ui.composition.rememberObjectDetection
import app.pixle.ui.state.rememberQueryable
import app.pixle.ui.state.rememberStillMotion
import kotlinx.coroutines.suspendCancellableCoroutine
import org.tensorflow.lite.support.image.TensorImage
import java.io.File
import java.util.UUID
import java.util.concurrent.Executors
import kotlin.coroutines.resume

@Composable
fun SmartPreview(
    uri: Uri?,
    isDetectingMotion: Boolean,
    captureCount: MutableIntState,
    cameraController: LifecycleCameraController
) {
    val context = LocalContext.current
    val executor = remember { Executors.newSingleThreadExecutor() }
    val objectDetector by rememberObjectDetection()

    val (goal, _) = rememberQueryable(SolutionOfToday)
    val (lib, _) = rememberQueryable(Library)
    val (isStill) = rememberStillMotion(isDetectingMotion = isDetectingMotion)
    val (attempt, setAttempt) = remember { mutableStateOf<Attempt?>(null) }

    LaunchedEffect(isStill, objectDetector, lib, goal, uri) {
        if (!isStill || uri != null) {
            setAttempt(null)
            return@LaunchedEffect
        }
        val detector = objectDetector ?: return@LaunchedEffect
        val knowledgeBase = lib ?: return@LaunchedEffect
        val items = goal?.solutionItems ?: return@LaunchedEffect

        val filename = UUID.randomUUID().toString()
        val file = File(context.filesDir, filename)
        cameraController.imageCaptureFlashMode = ImageCapture.FLASH_MODE_OFF
        val image = suspendCancellableCoroutine<Uri?> { cont ->
            cameraController.takePicture(
                ImageCapture.OutputFileOptions.Builder(file).build(),
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        captureCount.intValue += 1
                        cont.resume(outputFileResults.savedUri)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        cont.resume(null)
                    }
                }
            )
        } ?: return@LaunchedEffect

        val raw = ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(context.contentResolver, image)
        )
        val bitmap = raw.copy(Bitmap.Config.ARGB_8888, true)
        raw.recycle()

        val predictions = detector.detect(TensorImage.fromBitmap(bitmap))
        val givens = predictions
            .map { obj ->
                obj.categories.mapNotNull { category ->
                    knowledgeBase.find { it.name == category.label }
                }
            }
            .toMutableList()

        val currentAttempt = AtomicAttempt(
            uuid = UUID.randomUUID().toString(),
            solutionDate = goal.solution.date,
            winningPhoto = null,
            location = ""
        )

        Log.d("pixle:analyse", "goal items: ${items.map { it.name }.joinToString(", ")}")

        val exacts = items.map { item ->
            val index = givens.indexOfFirst { given ->
                given.any { each -> each.name == item.name }
            }
            if (index == -1) return@map null
            val chosen = givens[index]
            givens.removeAt(index)
            return@map chosen.find { it.name == item.name }
        }

        Log.d("pixle:analyse", "exacts: ${exacts.map { it?.icon }.joinToString(", ")}")

        val similars = items.map { item ->
            val index = givens.indexOfFirst { given ->
                given.any { each -> each.category == item.category }
            }
            if (index == -1) return@map null
            val chosen = givens[index]
            givens.removeAt(index)
            return@map chosen.find { it.category == item.category }
        }

        Log.d("pixle:analyse", "similar: ${similars.map { it?.icon }.joinToString(", ")}")

        val result = items.mapIndexed { idx, _ ->
            val exact = exacts[idx]

            if (exact != null) {
                return@mapIndexed AtomicAttemptItem(
                    icon = exact.icon,
                    attemptUuid = currentAttempt.uuid,
                    positionInAttempt = idx.toLong(),
                    kind = AtomicAttemptItem.KIND_EXACT
                )
            }

            val similar = similars[idx]

            if (similar != null) {
                return@mapIndexed AtomicAttemptItem(
                    icon = similar.icon,
                    attemptUuid = currentAttempt.uuid,
                    positionInAttempt = idx.toLong(),
                    kind = AtomicAttemptItem.KIND_SIMILAR
                )
            }

            val unmatched = givens.removeFirstOrNull()?.firstOrNull()

            if (unmatched != null) {
                return@mapIndexed AtomicAttemptItem(
                    icon = unmatched.icon,
                    attemptUuid = currentAttempt.uuid,
                    positionInAttempt = idx.toLong(),
                    kind = AtomicAttemptItem.KIND_NONE
                )
            }

            return@mapIndexed AtomicAttemptItem(
                icon = "",
                attemptUuid = currentAttempt.uuid,
                positionInAttempt = idx.toLong(),
                kind = AtomicAttemptItem.KIND_NONE
            )
        }

        bitmap.recycle()
        Log.d("pixle:analyse", "result: ${result.map { it.icon }.joinToString(", ")}")
        setAttempt(Attempt(currentAttempt, result))
    }


    Column(
        modifier = Modifier
            .zIndex(3f)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.fillMaxWidth(.45f).aspectRatio(1F))
        AnimatedVisibility(
            visible = attempt != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 44.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.CenterHorizontally
                ),
            ) {
                items(
                    items = attempt?.attemptItems
                        ?: listOf(),
                    key = { it.positionInAttempt }
                ) {
                    PhotoItemPreview(
                        item = it.icon,
                        kind = AtomicAttemptItem.KIND_NONE,
                        delay = it.positionInAttempt.toInt() * 100
                    )
                }
            }
        }
    }
}