package id.anison.amanogawa.core.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.data.anime.RemoteKeyRepo
import java.util.concurrent.TimeUnit


class CacheCleanUpWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val remoteKeyRepo: RemoteKeyRepo,
    private val log: Logger,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            log.d(TAG, "doWork")
            remoteKeyRepo.cleanUpCache()
            Result.success()
        } catch (e: Exception) {
            log.e(TAG, "Error clearing cache $e")
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "CacheCleanUpWorker"

        fun launchCleanUpCacheWork() = PeriodicWorkRequestBuilder<CacheCleanUpWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS,
        ).setBackoffCriteria(
            backoffPolicy = BackoffPolicy.LINEAR,
            backoffDelay = 1,
            timeUnit = TimeUnit.HOURS
        ).build()
    }

}
