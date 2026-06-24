package id.anison.amanogawa.core.domain.repository

import id.anison.amanogawa.core.domain.model.WatchHistory
import id.anison.amanogawa.core.common.DataState
import kotlinx.coroutines.flow.Flow

interface WatchHistoryRepo {

    fun getRecentlyWatched(): Flow<DataState<List<WatchHistory>>>

    fun getAllWatched(): Flow<DataState<List<WatchHistory>>>

    suspend fun recordWatch(
        animeId: Long,
        animeName: String,
        animeEnJpName: String,
        animePoster: String,
        animePosterThumb: String,
        lastEpisodeId: Long = 0,
        lastEpisodeNumber: Int = 0,
        lastEpisodeTitle: String = "",
        lastSeason: String = "",
        lastPart: String = "",
        lastYear: String = "",
    )

    suspend fun deleteByAnimeId(animeId: Long)

    suspend fun clearHistory()
}
