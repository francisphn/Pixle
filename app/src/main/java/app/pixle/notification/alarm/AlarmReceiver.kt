package app.pixle.notification.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.pixle.notification.NotificationLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

class AlarmReceiver : BroadcastReceiver(), CoroutineScope {
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val notificationLauncher = NotificationLauncher(context)
            launch(Dispatchers.IO) { notificationLauncher.launchNotification() }
        }
    }
}
