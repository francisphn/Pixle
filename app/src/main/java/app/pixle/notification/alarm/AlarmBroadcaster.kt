package app.pixle.notification.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.content.getSystemService
import java.time.LocalDateTime
import java.util.*

class AlarmBroadcaster private constructor(
    private val alarmManager: AlarmManager?,
    private val intent: PendingIntent,
) {
    fun setRepeatingAlarm() {

        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,

            Calendar.getInstance().also {
                it.timeInMillis = System.currentTimeMillis()
                it.set(Calendar.HOUR_OF_DAY, 10)
            }.timeInMillis,

            AlarmManager.INTERVAL_DAY,

            intent
        )
    }

    companion object {
        fun getInstance(context: Context): AlarmBroadcaster {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

            val intent = Intent(context, AlarmReceiver::class.java).let {
                PendingIntent.getBroadcast(context, 100, it, PendingIntent.FLAG_MUTABLE)
            }

            return AlarmBroadcaster(alarmManager, intent)
        }

    }
}