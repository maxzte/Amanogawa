package id.anison.amanogawa.feature.home

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.paging.cachedIn
import androidx.paging.map
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.common.SingleEventHandler
import id.anison.amanogawa.core.common.mapDataState
import id.anison.amanogawa.core.data.anime.AnimeRepo
import id.anison.amanogawa.core.domain.toAnimeUI
import id.anison.amanogawa.core.model.AnimeUI
import id.anison.amanogawa.core.model.PagingEvent
import id.anison.amanogawa.core.navigation.SharedScreen.AnimeDetails
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenModel(
    private val animeRepo: AnimeRepo,
    private val log: Logger,
) : StateScreenModel<HomeState>(HomeState()) {
    private val singleEventHandler = SingleEventHandler()

    private val _scrollToTopEvent = MutableSharedFlow<Unit>()
    val scrollToTopEvent = _scrollToTopEvent.asSharedFlow()

    private val _navEventState = MutableSharedFlow<HomeNavigationEvent>()
    val navEventState = _navEventState.asSharedFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _pagingEvent = MutableSharedFlow<PagingEvent>()
    val pagingEvent: SharedFlow<PagingEvent> = _pagingEvent

    val titlesPagingData = animeRepo.getAllWithPagination()
        .map { pagingData -> pagingData.map { it.toAnimeUI() } }
        .distinctUntilChanged()
        .cachedIn(screenModelScope)

    val lazyGridState = LazyGridState()

    init {
        log.d(TAG, "init")
        getCurrentAnimeList()
    }

    fun onEvent(event: HomeScreenEvents) {
        log.d(TAG, "curr event is: $event")
        when (event) {
            is HomeScreenEvents.PushAnime -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navEventState.emit(
                            value = HomeNavigationEvent.NavigateToAnimeDetails(
                                animeId = event.animeId
                            )
                        )
                    }
                }
            }

            is HomeScreenEvents.OnRefresh -> {
//                if (singleEventHandler.canTrigger()) {
                onPullToRefresh()
                refresh()
//                }
            }
        }
    }

    fun requestScrollToTop() = screenModelScope.launch {
        if (singleEventHandler.canTrigger()) {
            _scrollToTopEvent.emit(Unit)
        }
    }

    fun onRetryFetch() = screenModelScope.launch {
        _pagingEvent.emit(PagingEvent.RETRY)
    }

    fun setRefreshing(isRefreshing: Boolean) {
        _isRefreshing.value = isRefreshing
    }

    private fun onPullToRefresh() = screenModelScope.launch {
        _pagingEvent.emit(PagingEvent.REFRESH)
    }

    private fun refresh() {
        getCurrentAnimeList()
    }

    private fun getCurrentAnimeList() = screenModelScope.launch {
        mutableState.update { oldState ->
            oldState.copy(
                currentAnimeState = animeRepo.getCurrentAnimeList()
                    .mapDataState { it.map { animeUI -> animeUI.toAnimeUI() } }
            )
        }
    }

    companion object {
        private const val TAG = "HomeSM"
    }

}

data class HomeState(
    val currentAnimeState: DataState<List<AnimeUI>> = DataState.Loading,
)

sealed interface HomeScreenEvents {
    data class PushAnime(val animeId: Long) : HomeScreenEvents
    object OnRefresh : HomeScreenEvents
}

sealed interface HomeNavigationEvent {
    data class NavigateToAnimeDetails(val animeId: Long) : HomeNavigationEvent
}

internal fun HomeNavigationEvent.toScreen(): Screen = when (this) {
    is HomeNavigationEvent.NavigateToAnimeDetails -> {
        ScreenRegistry.get(AnimeDetails(animeId = this.animeId))
    }
}