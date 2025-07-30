package id.anison.amanogawa.core.data.anime

import androidx.paging.PagingData
import id.anison.amanogawa.core.common.DataState
import kotlinx.coroutines.flow.Flow


interface AnimeRepo {

    fun getAllWithPagination(): Flow<PagingData<Anime>>

    suspend fun getCurrentAnimeList(): DataState<List<Anime>>

    suspend fun getAnimeListWithNewEpisodes(): DataState<List<Anime>>

    suspend fun getAnimeDetailsById(id: Long): DataState<AnimeDetails>

    suspend fun getAnimeEpisodesById(id: Long): DataState<AnimeEpisodes>

}