package id.anison.feature.search.screenmodel

import android.util.Log
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.ReqRes
import id.anison.data.repository.AmanogawaRepository
import id.anison.data.repository.ConnectivityRepository
import id.anison.feature.common.DataState
import id.anison.feature.model.FoundAnimeUI
import id.anison.feature.model.toDataFoundAnime
import id.anison.feature.model.toFoundAnimeUI
import id.anison.feature.search.state.SearchState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchScreenModel(
    private val repository: AmanogawaRepository,
    private val connectivityRepository: ConnectivityRepository
) : StateScreenModel<SearchState>(SearchState.None) {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query
    private val _searchState = MutableStateFlow(SearchState.None)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()
    private val _foundTitles = MutableStateFlow<List<FoundAnimeUI>>(emptyList())
    val foundTitles = _foundTitles
    val isOnline = connectivityRepository.isConnected
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Lazily,
            initialValue = true,
        )

    init {
        observeQuery()
    }

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun addToSearchHistory(anime: FoundAnimeUI) {
        screenModelScope.launch {
            val foundAnimeDbo = anime.toDataFoundAnime().copy(timestamp = System.currentTimeMillis())
            repository.addToSearchHistory(foundAnimeDbo)
        }
    }

    fun deleteItemFromSearchHistory(animeUI: FoundAnimeUI) {
        screenModelScope.launch {
            repository.deleteItemFromSearchHistory(animeUI.toDataFoundAnime())
        }
    }

    fun clearSearchHistory() {
        screenModelScope.launch {
            repository.clearSearchHistory()
        }
    }

    private fun observeQuery() {
        screenModelScope.launch {
            query.collectLatest { query ->
                if (query.isNotEmpty()) {
                    delay(500)
                    repository.searchTitle(query).collect { result ->
                        when (result) {
                            is ReqRes.Success -> {
                                _foundTitles.value = result.data.map { dataFoundAnime ->
                                    dataFoundAnime.toFoundAnimeUI()
                                }
                                _searchState.update {
                                    it.copy(
                                        foundTitles = DataState.Success(
                                            result.data.map { dataFoundAnime ->
                                                dataFoundAnime.toFoundAnimeUI()
                                            }
                                        )
                                    )
                                }
                            }

                            is ReqRes.Error -> {
                                _searchState.update { state ->
                                    state.copy(
                                        foundTitles = result.error?.let { DataState.Error(it) }
                                    )
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
                    _searchState.value = SearchState.None
                    getSearchHistory()
                }
            }
        }
    }

    private fun getSearchHistory() {
        screenModelScope.launch {
            repository.getSearchHistory().collect { history ->
                when (history) {
                    is ReqRes.Error -> {
                        Log.e("SearchScreenModel", "Error: ${history.error}")
                        _searchState.update { state ->
                            state.copy(
                                searchHistory = history.error?.cause?.let { DataState.Error(it) }
                            )
                        }
                    }

                    is ReqRes.InProgress -> {
                        _searchState.update { state -> state.copy(searchHistory = DataState.Loading) }
                    }

                    is ReqRes.Success -> {
                        _searchState.update { state ->
                            state.copy(
                                searchHistory = DataState.Success(history.data.map { it.toFoundAnimeUI() })
                            )
                        }
                    }
                }
            }
        }
    }
}