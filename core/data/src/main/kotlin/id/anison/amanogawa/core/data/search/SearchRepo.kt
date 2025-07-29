package id.anison.amanogawa.core.data.search

import androidx.paging.PagingData
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.data.anime.Anime
import id.anison.amanogawa.core.data.anime.AnimeDetails
import id.anison.amanogawa.core.data.filter.FilterType
import kotlinx.coroutines.flow.Flow

interface SearchRepo {

    fun searchTitle(query: String): Flow<DataState<List<SearchResult>>>

    suspend fun addToSearchHistory(anime: SearchResult)

    fun getSearchHistory(): Flow<DataState<List<SearchResult>>>

    suspend fun deleteItemFromSearchHistory(dataAnime: SearchResult)

    suspend fun clearSearchHistory()

    suspend fun getTitlesBySearchFilter(): Flow<PagingData<Anime>>

    suspend fun getTitlesByFilterName(
        filterParamId: Long?,
        filterName: String,
        filterType: FilterType
    ): Flow<PagingData<Anime>>

    fun getRandomTitles(): Flow<PagingData<AnimeDetails>>

}