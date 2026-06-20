package id.anison.amanogawa.core.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.domain.repository.RemoteKeyRepo
import org.koin.android.annotation.KoinWorker
import org.koin.core.annotation.Provided
import java.util.concurrent.TimeUnit


@KoinWorker
class CacheCleanUpWorker(
    context: Context,
    workerParams: WorkerParameters,
    @Provided private val remoteKeyRepo: RemoteKeyRepo,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d(tag = TAG) { "doWork" }
            remoteKeyRepo.cleanUpCache()
            Result.success()
        } catch (e: Exception) {
            Log.e(tag = TAG) { "Error clearing cache $e" }
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
