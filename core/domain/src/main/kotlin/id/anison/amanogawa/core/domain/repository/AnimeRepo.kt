package id.anison.amanogawa.core.domain.repository

import androidx.paging.PagingData
import id.anison.amanogawa.core.model.DataState
import id.anison.amanogawa.core.domain.model.Anime
import id.anison.amanogawa.core.domain.model.AnimeDetails
import id.anison.amanogawa.core.domain.model.AnimeEpisodes
import kotlinx.coroutines.flow.Flow

interface AnimeRepo {

    fun getAllWithPagination(): Flow<PagingData<Anime>>

    suspend fun getCurrentAnimeList(): DataState<List<Anime>>

    suspend fun getAnimeListWithNewEpisodes(): DataState<List<Anime>>

    suspend fun getAnimeDetailsById(id: Long): DataState<AnimeDetails>

    suspend fun getAnimeEpisodesById(id: Long): DataState<AnimeEpisodes>

}
