package id.anison.data.anime

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import id.anison.api.AmanogawaApi
import id.anison.common.AndroidLogcatLogger
import id.anison.database.AmanogawaDatabase
import id.anison.database.anime.entity.AnimeDbo
import id.anison.database.anime.entity.RemoteKeyDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class AnimeRemoteMediator(
    private val api: AmanogawaApi,
    private val database: AmanogawaDatabase,
    private val log: AndroidLogcatLogger,
) : RemoteMediator<Int, AnimeDbo>() {

    private val remoteKeyDao = database.remoteKeyDao()
    private val animeDao = database.animeDao()

    companion object {
        const val TAG = "RemoteMediator"
    }

    override suspend fun initialize(): InitializeAction {
        val remoteKey = database.withTransaction {
            remoteKeyDao.getKeyByMovie("current_key")
        } ?: return InitializeAction.LAUNCH_INITIAL_REFRESH

        val cacheTimeout = TimeUnit.HOURS.convert(1, TimeUnit.MILLISECONDS)

        return if ((System.currentTimeMillis() - remoteKey.lastUpdated) >= cacheTimeout) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AnimeDbo>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    1
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val remoteKey = database.withTransaction {
                        remoteKeyDao.getKeyByMovie("current_key")
                    }
                    remoteKey?.nextPage ?: return MediatorResult.Success(true)
                }
            }
            log.e(TAG, "page is $page")
            val newTitlesResponse = withContext(Dispatchers.IO) {
                api.getTitlesByPage(page)
            }
            val newTitles = newTitlesResponse.getOrThrow()
            val endOfPaginationReached = newTitles.data.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.clearKeys()
                }

                remoteKeyDao.insertKey(
                    key = RemoteKeyDbo(
                        id = "current_key",
                        totalPages = newTitles.pages,
                        currentPage = newTitles.currentPage,
                        nextPage = if (newTitles.currentPage < newTitles.pages) page + 1 else null,
                        lastUpdated = System.currentTimeMillis(),
                    )
                )

                animeDao.insert(newTitles.data.map { it.toAnimeDto().toAnimeDbo() })
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            log.e(TAG, "Err is: ${e.message}")
            MediatorResult.Error(e)
        }
    }
}
