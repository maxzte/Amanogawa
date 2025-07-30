package id.anison.amanogawa.feature.search.screenmodel

import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.common.SingleEventHandler
import id.anison.amanogawa.core.common.mapDataState
import id.anison.amanogawa.core.data.search.SearchRepo
import id.anison.amanogawa.core.domain.toSearchResult
import id.anison.amanogawa.core.domain.toSearchResultUI
import id.anison.amanogawa.core.model.SearchResultUI
import id.anison.amanogawa.core.navigation.SharedScreen.AnimeDetails
import id.anison.amanogawa.feature.search.screen.FilterScreen
import id.anison.amanogawa.feature.search.screen.RandomAnimeCarouselScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchScreenModel(
    private val searchRepo: SearchRepo,
    private val log: Logger,
) : ScreenModel {
    private val singleEventHandler = SingleEventHandler()

    private val _query = MutableStateFlow(TextFieldValue(""))
    val query: StateFlow<TextFieldValue> = _query.asStateFlow()

    private val _navEventState = MutableSharedFlow<NavigationEvent>()
    val navEventState = _navEventState.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResultState: StateFlow<DataState<List<SearchResultUI>>> = query
        .debounce(500)
        .flatMapLatest { queryStr ->
            if (queryStr.text.trim().length < 2) {
                flowOf(DataState.Loading)
            } else {
                searchRepo.searchTitle(queryStr.text)
                    .map { dataState ->
                        dataState.mapDataState { it.map { sRes -> sRes.toSearchResultUI() } }
                    }
            }
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DataState.Loading
        )

    val searchHistoryState: StateFlow<DataState<List<SearchResultUI>>> = searchRepo
        .getSearchHistory()
        .map { dataState ->
            dataState.mapDataState { it.map { sHistory -> sHistory.toSearchResultUI() } }
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DataState.Loading
        )

    fun onEvent(event: SearchScreenEvents) {
        log.d(TAG, "curr event is: $event")
        when (event) {
            is SearchScreenEvents.OnAnimeClick -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navEventState.emit(NavigationEvent.NavigateToAnimeDetails(event.animeId))
                        addToSearchHistory(event.anime)
                    }
                }
            }

            is SearchScreenEvents.OnRandomButtonClick -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navEventState.emit(NavigationEvent.NavigateToRandom)
                    }
                }
            }

            is SearchScreenEvents.OnExtendedButtonClick -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navEventState.emit(NavigationEvent.NavigateToExtended)
                    }
                }
            }

            is SearchScreenEvents.OnDeleteAllButtonClick -> {
                if (singleEventHandler.canTrigger()) {
                    clearSearchHistory()
                }
            }

            is SearchScreenEvents.OnDeleteAnimeFromHistory -> {
                if (singleEventHandler.canTrigger()) {
                    deleteItemFromSearchHistory(event.anime)
                }
            }

            is SearchScreenEvents.OnSearchQueryChange -> {
                updateQuery(event.query)
            }
        }
    }

    private fun addToSearchHistory(anime: SearchResultUI) = screenModelScope.launch {
        searchRepo.addToSearchHistory(anime.toSearchResult())
    }

    private fun clearSearchHistory() = screenModelScope.launch {
        searchRepo.clearSearchHistory()
    }

    private fun deleteItemFromSearchHistory(animeUI: SearchResultUI) = screenModelScope.launch {
        searchRepo.deleteItemFromSearchHistory(animeUI.toSearchResult())
    }

    private fun updateQuery(newQuery: TextFieldValue) {
        _query.value = newQuery
    }

    companion object {
        private const val TAG = "SearchSM"
    }

}

sealed interface SearchScreenEvents {
    data class OnAnimeClick(val animeId: Long, val anime: SearchResultUI) : SearchScreenEvents
    object OnRandomButtonClick : SearchScreenEvents
    object OnExtendedButtonClick : SearchScreenEvents
    object OnDeleteAllButtonClick : SearchScreenEvents
    data class OnSearchQueryChange(val query: TextFieldValue) : SearchScreenEvents
    data class OnDeleteAnimeFromHistory(val anime: SearchResultUI) : SearchScreenEvents
}

interface RequiresKeyboardDismiss

sealed interface NavigationEvent {
    data class NavigateToAnimeDetails(val animeId: Long) : NavigationEvent, RequiresKeyboardDismiss
    data object NavigateToRandom : NavigationEvent, RequiresKeyboardDismiss
    data object NavigateToExtended : NavigationEvent, RequiresKeyboardDismiss
}

internal fun NavigationEvent.toScreen(): Screen = when (this) {
    is NavigationEvent.NavigateToAnimeDetails -> ScreenRegistry.get(AnimeDetails(animeId = this.animeId))
    is NavigationEvent.NavigateToRandom -> RandomAnimeCarouselScreen()
    is NavigationEvent.NavigateToExtended -> FilterScreen()
}