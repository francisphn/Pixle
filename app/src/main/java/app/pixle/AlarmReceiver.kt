package app.pixle

import AlarmUtils
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.pixle.notification.NotificationUtils
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, mIntent: Intent?) {
        val notificationUtils = NotificationUtils(context!!)

        notificationUtils.launchNotification()

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)

        val alarmUtils = AlarmUtils(context)
        alarmUtils.initRepeatingAlarm(calendar)
    }
}
