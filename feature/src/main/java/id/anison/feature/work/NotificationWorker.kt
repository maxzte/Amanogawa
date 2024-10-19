package id.anison.feature.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import id.anison.data.ReqRes
import id.anison.data.model.DataFavorite
import id.anison.data.repository.AmanogawaRepository
import id.anison.feature.R
import id.anison.feature.anime.AnimeScreen
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {
    private val repository: AmanogawaRepository by inject()

    companion object : KoinComponent {
        private const val TAG = "NotificationWorker"
        private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        fun launch(workManager: WorkManager, uniqueWorkName: String) {
            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                repeatInterval = 12,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setBackoffCriteria(
                    backoffPolicy = BackoffPolicy.LINEAR,
                    backoffDelay = 15, TimeUnit.MINUTES
                )
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun stopWork(workManager: WorkManager, uniqueWorkName: String) {
            Log.d(TAG, "stopWorker for name $uniqueWorkName")
            workManager.cancelUniqueWork(uniqueWorkName)
        }

        fun stopAllWorks(workManager: WorkManager) {
            Log.d(TAG, "stopAllWorks")
            workManager.cancelAllWork()
        }
    }

    override suspend fun doWork(): Result {
        return try {
            createNotificationChannel()
            val result =
                repository.getFavorites().filterNot { it is ReqRes.InProgress }.firstOrNull()
            when (result) {
                is ReqRes.Error -> {
                    showTestNotification("FAIL")
                    return Result.failure()
                }

                is ReqRes.Success -> {
                    result.data.forEach { favorite ->
                        if (favorite.isCurrent && repository.checkForNewEpisode(favorite))
                            showNotification(favorite)
                    }
                    return Result.success()
                }

                else -> {
                    showTestNotification("Unexpected result")
                    return Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in doWork: ${e.message}")
            showTestNotification("Exception work cause $e")
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "episodes_channel"
            val name = "New Episodes Channel"
            val descriptionText = "Channel for new episodes notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showTestNotification(message: String) {
        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, "episodes_channel")
            .setSmallIcon(R.drawable.ic_watch_eye)
            .setContentTitle("Test Notification")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify((0..1000).random(), notification)
    }

    private fun showNotification(favorite: DataFavorite) {
        val intent = Intent(applicationContext, AnimeScreen::class.java).apply {
            putExtra("animeID", favorite.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "episodes_channel"
        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_rounded_info_24)
            .setContentTitle("New Episode Available!")
            .setContentText("A new episode is available for ${favorite.en_jap_name}.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(favorite.id.toInt(), notification)
    }
}