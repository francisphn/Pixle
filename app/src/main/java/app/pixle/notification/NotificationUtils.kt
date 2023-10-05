package app.pixle.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.pixle.MainActivity
import app.pixle.R

private const val CHANNEL_ID = "Pixle_Daily_Reminder"
private const val CHANNEL_NAME = "Pixle Daily Reminder"
private const val CHANNEL_DESCRIPTION = "Pixle uses this to send you a reminder to complete the daily puzzle once a day."
private const val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_LOW
private const val CHANNEL_PRIORITY = NotificationCompat.PRIORITY_DEFAULT

class NotificationUtils(private val context: Context) {
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
        initialiseBuilder()
    }

    fun launchNotification(){
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                return
            }

            notificationManager.notify(0, notificationBuilder.build())
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_IMPORTANCE).apply {
            this.description = CHANNEL_DESCRIPTION
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun initialiseBuilder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_MUTABLE)

        notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.bling)
            .setContentTitle("Pixle's daily puzzle is here")
            .setContentText("Test test")
            .setPriority(CHANNEL_PRIORITY)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }
}