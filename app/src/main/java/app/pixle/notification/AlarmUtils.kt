import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import app.pixle.AlarmReceiver
import java.util.*

class AlarmUtils(context: Context) {
    private var context = context
    private var alarmManager: AlarmManager? = null
    private var alarmIntent: PendingIntent

    init {
        alarmManager = this.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmIntent = Intent(this.context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                this.context,
                100,
                intent,
                PendingIntent.FLAG_MUTABLE
            )
        }
    }

    fun initRepeatingAlarm(calendar: Calendar) {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager?.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )
    }
}