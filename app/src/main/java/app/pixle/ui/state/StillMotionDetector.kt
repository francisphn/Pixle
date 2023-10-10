package app.pixle.ui.state

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt


fun Float.roundToNearest(digit: Int): Float {
    val pow = 10.0.pow(digit)
    return (this * pow).roundToInt() / pow.toFloat()
}

@Composable
fun rememberStillMotion(isDetectingMotion: Boolean): MutableState<Boolean> {
    val context = LocalContext.current

    val focus = rememberSaveable { mutableStateOf(false) }

    val handler = object: SensorEventListener {
        private var stillStartTime = LocalDateTime.now()

        override fun onSensorChanged(event: SensorEvent?) {
            val ev = event ?: return
            val (x, y, z) = ev.values
            val dx = x.roundToNearest(2).absoluteValue
            val dy = y.roundToNearest(2).absoluteValue
            val dz = z.roundToNearest(2).absoluteValue

            val d = dx + dy + dz
            Log.d("pixle:accel", "dx: $dx, dy: $dy, dz: $dz")

            val isShaking = d > 0.65

            Log.d("pixle:accel", "is shaking: $isShaking")

            if (isShaking) {
                stillStartTime = LocalDateTime.now()
                focus.value = false
                return
            }

            // get difference in second
            val diffTime = ChronoUnit
                .SECONDS
                .between(stillStartTime, LocalDateTime.now())
                .absoluteValue
            if (diffTime > 3) {
                stillStartTime = LocalDateTime.now()
                focus.value = true
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
    }

    DisposableEffect(isDetectingMotion) {
        if (!isDetectingMotion) return@DisposableEffect onDispose {  }

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(handler, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            sensorManager.unregisterListener(handler)
        }
    }


    return focus
}