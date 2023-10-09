package app.pixle.ui.state

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.pixle.BuildConfig
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity


const val TRANSITIONS_RECEIVER_ACTION = "${BuildConfig.APPLICATION_ID}.TRANSITIONS_RECEIVER_ACTION"

fun activityTransition(type: Int) = listOf(
    ActivityTransition.Builder()
        .setActivityType(type)
        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
        .build(),
    ActivityTransition.Builder()
        .setActivityType(type)
        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
        .build()
)

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberIsDeviceMotionStill(): Boolean {
    val context = LocalContext.current
    val permissionGranted = rememberPermissionState(Manifest.permission.ACTIVITY_RECOGNITION)
    val (isStill, setIsStill) = remember { mutableStateOf(false) }

    val receiver = object: BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            if (ActivityTransitionResult.hasResult(intent)) {
                val res = ActivityTransitionResult.extractResult(intent)
                val events = res
                    ?.transitionEvents
                    ?.filter { it.activityType == DetectedActivity.STILL }
                    ?: return
                events.forEach {
                    when (it.transitionType) {
                        ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
                            setIsStill(true)
                        }
                        ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {
                            setIsStill(false)
                        }
                    }
                }
            }
        }

    }

    DisposableEffect(permissionGranted) {
        if (!permissionGranted.status.isGranted) {
            return@DisposableEffect onDispose {}
        }

        val intent = Intent(TRANSITIONS_RECEIVER_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        context.registerReceiver(
            receiver,
            IntentFilter(TRANSITIONS_RECEIVER_ACTION),
            Context.RECEIVER_NOT_EXPORTED
        )

        val req = ActivityTransitionRequest(
            activityTransition(DetectedActivity.STILL)
        )

        val client = ActivityRecognition.getClient(context)


        client.requestActivityTransitionUpdates(req, pendingIntent)

        onDispose {
            client.removeActivityTransitionUpdates(pendingIntent)
            context.unregisterReceiver(receiver)
        }
    }

    return isStill
}