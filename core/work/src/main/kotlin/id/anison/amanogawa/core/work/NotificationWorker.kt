package id.anison.amanogawa.core.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.domain.repository.AnimeRepo
import id.anison.amanogawa.core.notification.Notifier
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val animeRepo: AnimeRepo,
    private val notifier: Notifier,
    private val log: Logger,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            log.d(TAG, "doWork")
            val animeWithNewEpisodes = animeRepo.getAnimeListWithNewEpisodes()

            when (animeWithNewEpisodes) {
                is DataState.Success -> {
                    val data = animeWithNewEpisodes.data
                    if (data.isNotEmpty()) {
                        notifier.postNewEpisodes(data)
                    }
                    return Result.success()
                }

                else -> {
                    return Result.retry()
                }
            }
        } catch (e: Exception) {
            log.e(TAG, "Exception in notification worker: $e")
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "NotificationWorker"
        private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        fun launchObserveNewEpisodes() = PeriodicWorkRequestBuilder<NotificationWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS,
        ).setBackoffCriteria(
            backoffPolicy = BackoffPolicy.LINEAR,
            backoffDelay = 15,
            timeUnit = TimeUnit.MINUTES
        ).setConstraints(constraints).build()
    }

}