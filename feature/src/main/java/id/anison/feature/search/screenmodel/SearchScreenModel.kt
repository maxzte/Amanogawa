package id.anison.feature.search.screenmodel

import android.util.Log
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.ConnectivityRepo
import id.anison.data.ReqRes
import id.anison.data.search.SearchRepo
import id.anison.feature.common.DataState
import id.anison.feature.search.model.FoundAnimeUI
import id.anison.feature.search.model.toDataFoundAnime
import id.anison.feature.search.model.toFoundAnimeUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchScreenModel(
    private val searchRepo: SearchRepo,
    private val connectivityRepo: ConnectivityRepo
) : StateScreenModel<SearchState>(SearchState()) {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    val isOnline = connectivityRepo.isConnected
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Lazily,
            initialValue = true,
        )

    init {
        observeQuery()
    }

    private fun observeQuery() = screenModelScope.launch {
        query.collectLatest { query ->
            if (query.isNotEmpty()) {
                searchRepo.searchTitle(query).collectLatest { result ->
                    when (val foundAnimeState = result) {
                        is ReqRes.Success -> {
                            _searchState.update { state ->
                                state.copy(
                                    foundTitles = DataState.Success(
                                        foundAnimeState.data.map { dataFoundAnime ->
                                            dataFoundAnime.toFoundAnimeUI()
                                        }
                                    )
                                )
                            }
                        }

                        is ReqRes.Error -> {
                            _searchState.update { state ->
                                state.copy(foundTitles = DataState.Error(foundAnimeState.error))
                            }
                        }

                        is ReqRes.InProgress -> {
                            _searchState.update { state ->
                                state.copy(foundTitles = DataState.Loading)
                            }
                        }
                    }
                }
            } else {
                _searchState.value = SearchState()
                getSearchHistory()
            }
        }
    }

    private fun getSearchHistory() = screenModelScope.launch {
        searchRepo.getSearchHistory().collectLatest { history ->
            when (val historyState = history) {
                is ReqRes.Error -> {
                    Log.e("SearchScreenModel", "Error: ${history.error}")
                    _searchState.update { state ->
                        state.copy(searchHistory = DataState.Error(historyState.error))
                    }
                }

                is ReqRes.InProgress -> {
                    _searchState.update { state -> state.copy(searchHistory = DataState.Loading) }
                }

                is ReqRes.Success -> {
                    _searchState.update { state ->
                        state.copy(
                            searchHistory = DataState.Success(historyState.data.map { it.toFoundAnimeUI() })
                        )
                    }
                }
            }
        }
    }

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun addToSearchHistory(anime: FoundAnimeUI) = screenModelScope.launch {
        val foundAnimeDbo = anime.toDataFoundAnime().copy(
            timestamp = System.currentTimeMillis()
        )
        searchRepo.addToSearchHistory(foundAnimeDbo)
    }

    fun deleteItemFromSearchHistory(animeUI: FoundAnimeUI) = screenModelScope.launch {
        searchRepo.deleteItemFromSearchHistory(animeUI.toDataFoundAnime())
    }

    fun clearSearchHistory() = screenModelScope.launch {
        searchRepo.clearSearchHistory()
    }
}

data class SearchState(
    val foundTitles: DataState<List<FoundAnimeUI>> = DataState.Loading,
    val searchHistory: DataState<List<FoundAnimeUI>> = DataState.Loading,
    val isAll: Boolean = false,
)