package app.pixle.notification

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.pixle.MainActivity
import app.pixle.R
import app.pixle.database.AppPreferences

private const val CHANNEL_ID = "Pixle_Daily_Reminder"
private const val CHANNEL_NAME = "Pixle Daily Reminder"
private const val CHANNEL_DESCRIPTION = "Pixle uses this to send you a reminder to complete the daily puzzle once a day."
private const val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_LOW
private const val CHANNEL_PRIORITY = NotificationCompat.PRIORITY_DEFAULT

class NotificationLauncher(private val context: Context) {
    private val dataStore = AppPreferences.getInstance(context)

    private val intent: Intent = Intent(context, MainActivity::class.java).apply {
        this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private val pendingIntent = PendingIntent
        .getActivity(context, 0, intent, FLAG_IMMUTABLE)

    private val notificationBuilder: NotificationCompat.Builder =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.bling)
            .setContentTitle("Pixle's daily puzzle is here")
            .setContentText("Test test")
            .setPriority(CHANNEL_PRIORITY)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

    private val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE).apply {
        this.description = CHANNEL_DESCRIPTION
    }

    private val notificationManager = NotificationManagerCompat
        .from(context)
        .also { it.createNotificationChannel(channel) }

    fun launchNotification() {
        if (ActivityCompat.checkSelfPermission(context, POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
            notificationManager.notify(0, notificationBuilder.build()) // todo: replace with real ID
        }
    }
}