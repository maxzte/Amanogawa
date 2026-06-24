package id.anison.amanogawa.feature.profile

import androidx.compose.foundation.lazy.grid.LazyGridState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import id.anison.amanogawa.core.domain.model.WatchHistory
import id.anison.amanogawa.core.domain.repository.WatchHistoryRepo
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.navigation.SharedScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

@Factory
class AllWatchedScreenModel(
    @Provided private val watchHistoryRepo: WatchHistoryRepo,
) : ScreenModel {
    val lazyGridState = LazyGridState()

    val watchedState: StateFlow<DataState<List<WatchHistory>>> =
        watchHistoryRepo.getAllWatched()
            .stateIn(screenModelScope, SharingStarted.Eagerly, DataState.Loading)

    private val _navigationEvent = MutableSharedFlow<AllWatchedNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _scrollToTopEvent = MutableSharedFlow<Unit>()
    val scrollToTopEvent = _scrollToTopEvent.asSharedFlow()

    fun onEvent(event: AllWatchedScreenEvents) {
        when (event) {
            AllWatchedScreenEvents.PopBack -> {
                screenModelScope.launch {
                    _navigationEvent.emit(AllWatchedNavigationEvent.NavigateToBack)
                }
            }
            is AllWatchedScreenEvents.OnAnimeClick -> {
                screenModelScope.launch {
                    _navigationEvent.emit(
                        AllWatchedNavigationEvent.NavigateToAnime(event.animeId)
                    )
                }
            }
            AllWatchedScreenEvents.ClearHistory -> {
                screenModelScope.launch {
                    watchHistoryRepo.clearHistory()
                }
            }
            is AllWatchedScreenEvents.OnDeleteAnime -> {
                screenModelScope.launch {
                    watchHistoryRepo.deleteByAnimeId(event.animeId)
                }
            }
        }
    }

    fun requestScrollToTop() {
        screenModelScope.launch {
            _scrollToTopEvent.emit(Unit)
        }
    }
}

sealed interface AllWatchedScreenEvents {
    data object PopBack : AllWatchedScreenEvents
    data class OnAnimeClick(val animeId: Long) : AllWatchedScreenEvents
    data object ClearHistory : AllWatchedScreenEvents
    data class OnDeleteAnime(val animeId: Long) : AllWatchedScreenEvents
}

sealed interface AllWatchedNavigationEvent {
    data object NavigateToBack : AllWatchedNavigationEvent
    data class NavigateToAnime(val animeId: Long) : AllWatchedNavigationEvent
}

internal fun AllWatchedNavigationEvent.toScreen() = when (this) {
    AllWatchedNavigationEvent.NavigateToBack -> null
    is AllWatchedNavigationEvent.NavigateToAnime -> ScreenRegistry.get(
        SharedScreen.AnimeDetails(animeId = this.animeId)
    )
}
