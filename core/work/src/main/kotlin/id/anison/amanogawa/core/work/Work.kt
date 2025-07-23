package id.anison.amanogawa.core.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager

object Work {

    fun init(context: Context) {
        WorkManager.getInstance(context = context).enqueueUniquePeriodicWork(
            uniqueWorkName = CACHE_CLEAN_UP_WORKER,
            ExistingPeriodicWorkPolicy.KEEP,
            request = CacheCleanUpWorker.launchCleanUpCacheWork()
        )

        WorkManager.getInstance(context = context).enqueueUniquePeriodicWork(
            uniqueWorkName = OBSERVE_NEW_EPISODES_IN_CURRENT_TITLES_WORKER,
            ExistingPeriodicWorkPolicy.KEEP,
            request = NotificationWorker.launchObserveNewEpisodes()
        )
    }

}

internal const val CACHE_CLEAN_UP_WORKER = "cacheCleanUpWorker"
internal const val OBSERVE_NEW_EPISODES_IN_CURRENT_TITLES_WORKER = "observeNewEpisodesInCurrentTitlesWorker"
