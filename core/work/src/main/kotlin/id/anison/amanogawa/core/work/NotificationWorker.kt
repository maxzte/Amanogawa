package id.anison.amanogawa.core.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.domain.repository.AnimeRepo
import id.anison.amanogawa.core.model.DataState
import id.anison.amanogawa.core.notification.Notifier
import org.koin.android.annotation.KoinWorker
import org.koin.core.annotation.Provided
import java.util.concurrent.TimeUnit

@KoinWorker
class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters,
    @Provided private val animeRepo: AnimeRepo,
    @Provided private val notifier: Notifier,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d(tag = TAG) { "doWork" }
            when (val animeWithNewEpisodes = animeRepo.getAnimeListWithNewEpisodes()) {
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
            Log.e(tag = TAG) { "Exception in notification worker: $e" }
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