package id.anison.amanogawa.core.notification

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.domain.model.Anime

class NewEpisodesNotifier(
    private val context: Context,
    private val log: Logger,
) : Notifier {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun postNewEpisodes(animeList: List<Anime>) {
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
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(anime.animeId.toInt(), notification)
        }
    }

    companion object {
        private const val TAG = "NewEpisodesNotifier"
    }

}