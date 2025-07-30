package id.anison.amanogawa.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object Notification {

    fun init(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ChannelId.NEW_EPISODE_CHANNEL,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = context.getString(R.string.notification_channel_description)
            }

            val notificationManager: NotificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

}