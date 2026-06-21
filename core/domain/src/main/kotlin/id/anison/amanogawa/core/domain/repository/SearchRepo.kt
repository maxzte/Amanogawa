package id.anison.amanogawa.core.domain.repository

import androidx.paging.PagingData
import id.anison.amanogawa.core.model.DataState
import id.anison.amanogawa.core.domain.model.Anime
import id.anison.amanogawa.core.domain.model.FilterType
import id.anison.amanogawa.core.domain.model.SearchResult
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

    fun getRandomTitles(): Flow<PagingData<Anime>>

}