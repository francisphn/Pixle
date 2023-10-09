package app.pixle.ui.composition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.pixle.database.AppPreferences
import app.pixle.ui.state.ObjectDetectionModel
import app.pixle.ui.state.rememberObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector

data class ObjectDetectionState(
    val detector: ObjectDetector? = null,
) : State<ObjectDetector?> {
    override val value: ObjectDetector?
        get() = detector
}

val LocalObjectDetection = compositionLocalOf { ObjectDetectionState() }


@Composable
fun rememberObjectDetection(): ObjectDetectionState {
    return LocalObjectDetection.current
}

@Composable
fun ObjectDetectionProvider(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val preferences = AppPreferences.getInstance(context)
    val model by preferences.getModelPreference.collectAsState(initial = ObjectDetectionModel.EDL1)
    val objectDetector = rememberObjectDetector(model = model)

    val objectDetectionState = remember(objectDetector) {
        ObjectDetectionState(objectDetector)
    }

    CompositionLocalProvider(LocalObjectDetection provides objectDetectionState) {
        content()
    }
}