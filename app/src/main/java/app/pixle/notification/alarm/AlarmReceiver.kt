package app.pixle.notification.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.pixle.notification.NotificationLauncher

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val notificationUtils = NotificationLauncher(context)
            notificationUtils.launchNotification()
        }
    }
}
