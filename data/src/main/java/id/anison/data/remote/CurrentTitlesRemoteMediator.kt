package id.anison.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import id.anison.api.AmanogawaApi
import id.anison.common.AndroidLogcatLogger
import id.anison.data.toCurrentTitleDbo
import id.anison.database.AmanogawaDatabase
import id.anison.database.model.CurrentTitlesDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/***
 * class CurrentTitlesRemoteMediator for future
 */
@OptIn(ExperimentalPagingApi::class)
class CurrentTitlesRemoteMediator(
    private val api: AmanogawaApi,
    private val database: AmanogawaDatabase,
    private val log: AndroidLogcatLogger,
) : RemoteMediator<Int, CurrentTitlesDbo>() {
    companion object {
        const val TAG = "CurrentTitles RemoteMediator"
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CurrentTitlesDbo>
    ): MediatorResult = withContext(Dispatchers.IO) {
        try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    state.anchorPosition?.let { anchorPosition ->
                        state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1) ?: 1
                    } ?: 1
                }

                LoadType.PREPEND -> {
                    state.pages.firstOrNull()?.prevKey ?: return@withContext MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }

                LoadType.APPEND -> {
                    state.pages.lastOrNull()?.prevKey ?: return@withContext MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }
            }
            log.e(TAG, "loadKey is $loadKey")
            val newTitlesResponse = api.getCurrentTitles(/*loadKey ?: 1*/)
            val newTitles = newTitlesResponse.getOrThrow()

            val endOfPaginationReached = newTitles.isEmpty()
            log.e(TAG, "end of pag $endOfPaginationReached")
            database.currentTitleDao.cleanAndInsert(newTitles.map { it.toCurrentTitleDbo() })

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}