package id.anison.amanogawa.core.data.watch

import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.data.toAppError
import id.anison.amanogawa.core.database.watch.WatchHistoryDao
import id.anison.amanogawa.core.database.watch.WatchHistoryDbo
import id.anison.amanogawa.core.domain.model.WatchHistory
import id.anison.amanogawa.core.domain.repository.WatchHistoryRepo
import id.anison.amanogawa.core.common.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class WatchHistoryRepoImpl(
    private val watchHistoryDao: WatchHistoryDao,
) : WatchHistoryRepo {

    override fun getRecentlyWatched(): Flow<DataState<List<WatchHistory>>> = flow {
        emit(DataState.Loading)
        emitAll(
            watchHistoryDao.getRecentHistory().map { list ->
                DataState.Success(list.map { it.toWatchHistory() })
            }
        )
    }.catch { e ->
        Log.e(tag = TAG) { "Error getting watch history. Cause = $e" }
        emit(DataState.Error(e.toAppError()))
    }

    override fun getAllWatched(): Flow<DataState<List<WatchHistory>>> = flow {
        emit(DataState.Loading)
        emitAll(
            watchHistoryDao.getAllHistory().map { list ->
                DataState.Success(list.map { it.toWatchHistory() })
            }
        )
    }.catch { e ->
        Log.e(tag = TAG) { "Error getting all watch history. Cause = $e" }
        emit(DataState.Error(e.toAppError()))
    }

    override suspend fun recordWatch(
        animeId: Long,
        animeName: String,
        animeEnJpName: String,
        animePoster: String,
        animePosterThumb: String,
        lastEpisodeId: Long,
        lastEpisodeNumber: Int,
        lastEpisodeTitle: String,
        lastSeason: String,
        lastPart: String,
        lastYear: String,
    ) {
        watchHistoryDao.insert(
            WatchHistoryDbo(
                animeId = animeId,
                animeName = animeName,
                animeEnJpName = animeEnJpName,
                animePoster = animePoster,
                animePosterThumb = animePosterThumb,
                lastEpisodeId = lastEpisodeId,
                lastEpisodeNumber = lastEpisodeNumber,
                lastEpisodeTitle = lastEpisodeTitle,
                lastSeason = lastSeason,
                lastPart = lastPart,
                lastYear = lastYear,
            )
        )
    }

    override suspend fun deleteByAnimeId(animeId: Long) {
        watchHistoryDao.deleteByAnimeId(animeId)
    }

    override suspend fun clearHistory() {
        watchHistoryDao.clearAll()
    }

    companion object {
        private const val TAG: String = "WatchHistoryRepo"
    }
}
