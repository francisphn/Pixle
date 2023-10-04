package app.pixle.ui.composition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import app.pixle.ui.state.ObjectDetectionModel
import app.pixle.ui.state.rememberObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector

data class ObjectDetectionState(
    val detector: ObjectDetector? = null,
    val setModel: (ObjectDetectionModel) -> Unit = {},
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
    val (model, setModel) = remember { mutableStateOf(ObjectDetectionModel.EDL1) }
    val objectDetector = rememberObjectDetector(model = model)

    val objectDetectionState = remember(objectDetector, setModel) {
        ObjectDetectionState(objectDetector, setModel)
    }

    // TODO: Change the model based on preferences if needed

    CompositionLocalProvider(LocalObjectDetection provides objectDetectionState) {
        content()
    }
}