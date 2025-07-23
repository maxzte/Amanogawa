package id.anison.amanogawa.feature.anime

import androidx.compose.foundation.lazy.grid.LazyGridState
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.common.SingleEventHandler
import id.anison.amanogawa.core.common.mapDataState
import id.anison.amanogawa.core.data.anime.AnimeRepo
import id.anison.amanogawa.core.data.favorite.FavoriteRepo
import id.anison.amanogawa.core.domain.toAnimeDetailsUI
import id.anison.amanogawa.core.domain.toAnimeEpisodesUI
import id.anison.amanogawa.core.domain.toFavorite
import id.anison.amanogawa.core.domain.toFavoriteUI
import id.anison.amanogawa.core.model.AnimeDetailsUI
import id.anison.amanogawa.core.model.AnimeEpisodesUI
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.navigation.SharedScreen
import id.anison.amanogawa.core.navigation.SharedScreen.AnimeDetails
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnimeDetailsScreenModel(
    private val animeId: Long,
    private val animeRepo: AnimeRepo,
    private val favoriteRepo: FavoriteRepo,
    private val log: Logger,
) : StateScreenModel<AnimeState>(AnimeState()) {
    private val singleEventHandler = SingleEventHandler()
    val lazyGridState = LazyGridState()
    private val _navEventState = MutableSharedFlow<NavigationEvent>()
    val navEventState = _navEventState.asSharedFlow()

    private val _scrollToTopEvent = MutableSharedFlow<Unit>()
    val scrollToTopEvent = _scrollToTopEvent.asSharedFlow()

    init {
        log.d(TAG, "init")
        isFavorite()
        loadAnimeDetails()
    }

    fun onEvents(event: AnimeDetailsScreenEvents) {
        log.d(TAG, "curr event is: $event")
        when (event) {
            is AnimeDetailsScreenEvents.PopBack -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navEventState.tryEmit(value = NavigationEvent.NavigateToBack)
                    }
                }
            }

            is AnimeDetailsScreenEvents.ScrollToTop -> {
                if (singleEventHandler.canTrigger()) {
                    _scrollToTopEvent.tryEmit(Unit)
                }
            }

            is AnimeDetailsScreenEvents.PushAnimeDetailsScreen -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navEventState.tryEmit(
                            value = NavigationEvent.NavigateToAnimeDetails(
                                animeId = event.animeId
                            )
                        )
                    }
                }
            }

            is AnimeDetailsScreenEvents.ToggleFavorite -> {
                if (singleEventHandler.canTrigger()) {
                    toggleFavorite(anime = event.anime)
                }
            }

            is AnimeDetailsScreenEvents.OnLinkClick -> {
                log.d(TAG, "$event")
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navEventState.tryEmit(
                            value = NavigationEvent.NavigateToFilteredResult(
                                filterId = event.filterId ?: -1,
                                filterName = event.filterName,
                                filterType = event.filterType
                            )
                        )
                    }
                }
            }

            is AnimeDetailsScreenEvents.Refresh -> {
                if (singleEventHandler.canTrigger()) {
                    loadAnimeDetails()
                }
            }
        }
    }

    private fun loadAnimeDetails() = screenModelScope.launch {
        mutableState.update { it.copy(anime = DataState.Loading) }

        val anime = animeRepo.getAnimeDetailsById(animeId).mapDataState { it.toAnimeDetailsUI() }
        val episodes =
            animeRepo.getAnimeEpisodesById(animeId).mapDataState { it.toAnimeEpisodesUI() }

        mutableState.update { oldState ->
            val newState = when (anime) {
                is DataState.Error -> DataState.Error(anime.exception)
                DataState.Loading -> DataState.Loading
                is DataState.Success -> {
                    when (episodes) {
                        is DataState.Error -> DataState.Error(episodes.exception)
                        DataState.Loading -> DataState.Loading
                        is DataState.Success -> DataState.Success(
                            AnimeDetailsFull(
                                anime = anime.data,
                                episodes = episodes.data
                            )
                        )
                    }
                }
            }

            oldState.copy(anime = newState)
        }
    }

    private fun isFavorite(): StateFlow<Boolean> = favoriteRepo.isFavorite(animeId)
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        ).also { stateFlow ->
            screenModelScope.launch {
                stateFlow.collectLatest { isFav ->
                    mutableState.update { it.copy(isFavorite = isFav) }
                }
            }
        }

    private fun toggleFavorite(anime: AnimeDetailsUI) = screenModelScope.launch {
        if (mutableState.value.isFavorite) {
            favoriteRepo.removeFromFavorite(anime.toFavoriteUI().toFavorite())
        } else {
            favoriteRepo.addToFavorite(anime.toFavoriteUI().toFavorite())
        }
    }

    companion object {
        private const val TAG = "AnimeDetailsSM"
    }

}

data class AnimeState(
    val anime: DataState<AnimeDetailsFull> = DataState.Loading,
    val isFavorite: Boolean = false,
    val scrollToTop: Boolean = false,
)

data class AnimeDetailsFull(
    val anime: AnimeDetailsUI,
    val episodes: AnimeEpisodesUI,
)

sealed interface NavigationEvent {
    data class NavigateToAnimeDetails(val animeId: Long) : NavigationEvent
    data class NavigateToFilteredResult(
        val filterId: Long,
        val filterName: String,
        val filterType: FilterTypeUI
    ) : NavigationEvent

    data object NavigateToBack : NavigationEvent
}

internal fun NavigationEvent.toScreen(): Screen? = when (this) {
    is NavigationEvent.NavigateToAnimeDetails -> ScreenRegistry.get(AnimeDetails(animeId = this.animeId))
    is NavigationEvent.NavigateToBack -> null
    is NavigationEvent.NavigateToFilteredResult -> ScreenRegistry.get(
        SharedScreen.SearchResult(
            filterId = this.filterId,
            filterName = this.filterName,
            filterType = this.filterType
        )
    )
}
