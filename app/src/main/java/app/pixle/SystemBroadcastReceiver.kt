package app.pixle

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.pixle.notification.alarm.AlarmBroadcaster

// Sets notification to be sent again after device reboots
class SystemBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            AlarmBroadcaster.getInstance(context).setRepeatingAlarm()
        }
    }
}