package app.pixle.ui.state

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.ObjectDetector

enum class ObjectDetectionModel(val filename: String) {
    EDL0("efficientdet-lite0.tflite"),
    EDL1("efficientdet-lite1.tflite"),
    EDL2("efficientdet-lite2.tflite")
}

@Composable
fun rememberObjectDetector(model: ObjectDetectionModel = ObjectDetectionModel.EDL1): ObjectDetector? {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val (detector, setDetector) = remember { mutableStateOf<ObjectDetector?>(null) }

    DisposableEffect(model.filename) {
        val filename = model.filename
        val coroutine = scope.launch {
            if (detector != null) {
                return@launch
            }

            Log.d("pixle:tensorflow", "Loading object detector: $filename")

            val options = ObjectDetector.ObjectDetectorOptions.builder().setMaxResults(8)
                .setScoreThreshold(0.2f)
            val baseOptions = BaseOptions.builder().setNumThreads(2).let {
                    if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                        it.useGpu()
                    } else {
                        it
                    }
                }
            val objectDetector = ObjectDetector.createFromFileAndOptions(
                context, filename, options.setBaseOptions(baseOptions.build()).build()
            )

            setDetector(objectDetector)
        }

        onDispose {
            coroutine.cancel()
            detector?.close()
            setDetector(null)
        }
    }

    return detector
}