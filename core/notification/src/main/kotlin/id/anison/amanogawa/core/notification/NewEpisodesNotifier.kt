package id.anison.amanogawa.core.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import id.anison.amanogawa.core.domain.model.Anime
import org.koin.core.annotation.Single

@Single
class NewEpisodesNotifier(
    private val context: Context,
) : Notifier {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun postNewEpisodes(animeList: List<Anime>) {
        val notificationManager = NotificationManagerCompat.from(context)

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        animeList.forEach { anime ->
            val notification = NotificationCompat.Builder(context, ChannelId.NEW_EPISODE_CHANNEL)
                .setSmallIcon(R.drawable.amanogawa_logo)
                .setContentTitle(context.getString(R.string.new_episode_notification_title))
                .setContentText(
                    context.getString(
                        R.string.new_episode_notification_text,
                        anime.name
                    )
                )
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        context.getString(
                            R.string.new_episode_notification_text,
                            anime.name
                        )
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setGroup(GROUP_KEY)
                .build()

            notificationManager.notify(anime.animeId.toInt(), notification)
        }

        if (animeList.size > 1) {
            val summaryNotification = NotificationCompat.Builder(context, ChannelId.NEW_EPISODE_CHANNEL)
                .setSmallIcon(R.drawable.amanogawa_logo)
                .setContentTitle(context.getString(R.string.new_episode_notification_title))
                .setContentText(
                    context.getString(
                        R.string.new_episode_notification_group_summary,
                        animeList.size
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(GROUP_KEY)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(SUMMARY_ID, summaryNotification)
        }
    }

    companion object {
        private const val TAG = "NewEpisodesNotifier"
        private const val GROUP_KEY = "NEW_EPISODE_GROUP"
        private const val SUMMARY_ID = 0
    }

}