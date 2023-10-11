package app.pixle.notification.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.pixle.notification.NotificationLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?): Unit = runBlocking {
        context?.let {
            val notificationLauncher = NotificationLauncher(context)
            launch(Dispatchers.IO) { notificationLauncher.launchNotification() }
        }
    }
}
