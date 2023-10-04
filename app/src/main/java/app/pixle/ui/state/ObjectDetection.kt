package app.pixle.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import app.pixle.model.entity.AppPreferences
import kotlinx.coroutines.launch
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.ObjectDetector

enum class ObjectDetectionModel(val filename: String) {
    EDL0("efficientdet-lite0.tflite"), EDL1("efficientdet-lite1.tflite"), EDL2("efficientdet-lite2.tflite")
}

@Composable
fun rememberObjectDetector(model: ObjectDetectionModel?): ObjectDetector? {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val (detector, setDetector) = remember { mutableStateOf<ObjectDetector?>(null) }

    val dataStore = AppPreferences(context)
    val sensitivityPreference = dataStore.getSensitivityPreference.collectAsState(initial = "")
    val sensitivityThreshold = sensitivityPreference.value?.toFloat() ?: 0.2f

    LaunchedEffect(Unit) {
        val filename = model?.filename ?: ObjectDetectionModel.EDL1.filename
        scope.launch {
            val options = ObjectDetector.ObjectDetectorOptions.builder().setMaxResults(8)
                .setScoreThreshold(sensitivityThreshold)
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
    }

    return detector
}