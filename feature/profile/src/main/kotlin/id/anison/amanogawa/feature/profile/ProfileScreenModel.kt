package id.anison.amanogawa.feature.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.domain.model.WatchHistory
import id.anison.amanogawa.core.domain.repository.FavoriteRepo
import id.anison.amanogawa.core.domain.repository.WatchHistoryRepo
import id.anison.amanogawa.core.model.mapper.toFavoriteUI
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.model.FavoriteUI
import id.anison.amanogawa.core.common.mapDataState
import id.anison.amanogawa.core.navigation.SharedScreen
import id.anison.amanogawa.feature.profile.screen.AllWatchedScreen
import id.anison.amanogawa.feature.profile.screen.SettingsScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

@Factory
class ProfileScreenModel(
    @Provided private val watchHistoryRepo: WatchHistoryRepo,
    @Provided private val favoriteRepo: FavoriteRepo,
) : ScreenModel {

    private val _navEventState = MutableSharedFlow<ProfileNavigationEvent>()
    val navEventState = _navEventState.asSharedFlow()

    private val _scrollToTopEvent = MutableSharedFlow<Unit>()
    val scrollToTopEvent = _scrollToTopEvent.asSharedFlow()

    val recentlyWatched: StateFlow<DataState<List<WatchHistory>>> =
        watchHistoryRepo.getRecentlyWatched().map { state ->
            state.mapDataState { list -> list.take(10) }
        }.stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = DataState.Loading
        )

    val favoritePreviews: StateFlow<DataState<List<FavoriteUI>>> =
        favoriteRepo.getFavorites().map { state ->
            state.mapDataState { list ->
                list.take(10).map { it.toFavoriteUI() }
            }
        }.stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = DataState.Loading
        )

    val favoriteCount: StateFlow<Int> =
        favoriteRepo.getFavorites().map { state ->
            if (state is DataState.Success) state.data.size else 0
        }.stateIn(
            scope = screenModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )

    fun onEvents(event: ProfileNavigationEvent) {
        Log.d(tag = TAG) { "current event is: $event" }
        when (event) {
            ProfileNavigationEvent.NavigateToFavorites -> {
                screenModelScope.launch {
                    _navEventState.emit(value = ProfileNavigationEvent.NavigateToFavorites)
                }
            }

            ProfileNavigationEvent.NavigateToSettings -> {
                screenModelScope.launch {
                    _navEventState.emit(value = ProfileNavigationEvent.NavigateToSettings)
                }
            }

            is ProfileNavigationEvent.NavigateToAnime -> {
                screenModelScope.launch {
                    _navEventState.emit(value = event)
                }
            }

            ProfileNavigationEvent.NavigateToAllWatched -> {
                screenModelScope.launch {
                    _navEventState.emit(value = ProfileNavigationEvent.NavigateToAllWatched)
                }
            }
        }
    }

    fun requestScrollToTop() {
        screenModelScope.launch {
            _scrollToTopEvent.emit(Unit)
        }
    }

    companion object {
        private const val TAG = "ProfileSM"
    }

}

sealed interface ProfileNavigationEvent {
    data object NavigateToFavorites : ProfileNavigationEvent
    data object NavigateToSettings : ProfileNavigationEvent
    data class NavigateToAnime(val animeId: Long) : ProfileNavigationEvent
    data object NavigateToAllWatched : ProfileNavigationEvent
}

internal fun ProfileNavigationEvent.toScreen() = when (this) {
    ProfileNavigationEvent.NavigateToFavorites -> ScreenRegistry.get(SharedScreen.Favorites)
    ProfileNavigationEvent.NavigateToSettings -> SettingsScreen()
    ProfileNavigationEvent.NavigateToAllWatched -> AllWatchedScreen()
    is ProfileNavigationEvent.NavigateToAnime -> ScreenRegistry.get(SharedScreen.AnimeDetails(animeId = this.animeId))
}
