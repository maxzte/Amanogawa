package id.anison.amanogawa.core.data.anime

import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.database.AmanogawaDatabase
import id.anison.amanogawa.core.domain.repository.RemoteKeyRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class RemoteKeyRepoImpl(
    private val database: AmanogawaDatabase,
    private val log: Logger,
) : RemoteKeyRepo {

    override suspend fun cleanUpCache(): Unit = withContext(Dispatchers.IO) {
        val threshold = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24)
        val expiredKeys = database.remoteKeyDao().getExpiredKeys(threshold)
        log.d(TAG, " expiredKeys is: $expiredKeys")

        expiredKeys.forEach { key ->
            database.animeDao().clearAllByListKey(key.key)
            database.remoteKeyDao().deleteKey(key.key)
        }
    }

    companion object Companion {
        private const val TAG = "RemoteKey repo"
    }

}