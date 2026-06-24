package id.anison.amanogawa.feature.anime

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.domain.repository.AnimeRepo
import id.anison.amanogawa.core.domain.repository.FavoriteRepo
import id.anison.amanogawa.core.domain.repository.WatchHistoryRepo
import id.anison.amanogawa.core.model.mapper.toAnimeDetailsUI
import id.anison.amanogawa.core.model.mapper.toAnimeEpisodesUI
import id.anison.amanogawa.core.model.mapper.toFavorite
import id.anison.amanogawa.core.model.mapper.toFavoriteUI
import id.anison.amanogawa.core.model.AnimeDetailsUI
import id.anison.amanogawa.core.model.AnimeEpisodeUI
import id.anison.amanogawa.core.model.AnimeEpisodesUI
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.model.SingleEventHandler
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
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Provided

@Factory
class AnimeDetailsScreenModel(
    @InjectedParam private val animeId: Long,
    @Provided private val animeRepo: AnimeRepo,
    @Provided private val favoriteRepo: FavoriteRepo,
    @Provided private val watchHistoryRepo: WatchHistoryRepo,
) : StateScreenModel<AnimeState>(AnimeState()) {
    private val singleEventHandler = SingleEventHandler()
    val lazyGridState = LazyGridState()
    private val _navEventState = MutableSharedFlow<NavigationEvent>()
    val navEventState = _navEventState.asSharedFlow()

    private val _scrollToTopEvent = MutableSharedFlow<Unit>()
    val scrollToTopEvent = _scrollToTopEvent.asSharedFlow()

    init {
        Log.d(tag = TAG) { "init" }
        isFavorite()
        loadAnimeDetails()
    }

    fun onEvents(event: AnimeDetailsScreenEvents) {
        Log.d(tag = TAG) { "curr event is: $event" }
        when (event) {
            is AnimeDetailsScreenEvents.PopBack -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navEventState.emit(value = NavigationEvent.NavigateToBack)
                    }
                }
            }

            is AnimeDetailsScreenEvents.ScrollToTop -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _scrollToTopEvent.emit(Unit)
                    }
                }
            }

            is AnimeDetailsScreenEvents.PushAnimeDetailsScreen -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navEventState.emit(
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
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navEventState.emit(
                            value = NavigationEvent.NavigateToFilteredResult(
                                filterId = event.filterId ?: -1,
                                filterName = event.filterName,
                                filterType = event.filterType
                            )
                        )
                    }
                }
            }

            is AnimeDetailsScreenEvents.OnImageClick -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navEventState.emit(
                            value = NavigationEvent.NavigateToImagesView(
                                index = event.index,
                                images = event.images
                            )
                        )
                    }
                }
            }

            is AnimeDetailsScreenEvents.OnEpisodeClick -> {
                if (singleEventHandler.canTrigger()) {
                    recordEpisodeWatch(event.episode)
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

        val animeDetails = animeRepo.getAnimeDetailsById(animeId)
        val episodes = animeRepo.getAnimeEpisodesById(animeId)

        mutableState.update { oldState ->
            val newState = when {
                animeDetails is DataState.Error -> DataState.Error(animeDetails.exception)
                episodes is DataState.Error -> DataState.Error(episodes.exception)
                animeDetails is DataState.Success && episodes is DataState.Success -> {
                    val animeUI = animeDetails.data.toAnimeDetailsUI()
                    DataState.Success(
                        AnimeDetailsFull(
                            anime = animeUI,
                            episodes = episodes.data.toAnimeEpisodesUI()
                        )
                    )
                }
                else -> DataState.Loading
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

    private fun recordEpisodeWatch(episode: AnimeEpisodeUI) = screenModelScope.launch {
        val state = mutableState.value
        val anime = (state.anime as? DataState.Success)?.data?.anime ?: return@launch
        watchHistoryRepo.recordWatch(
            animeId = anime.animeUI.animeId,
            animeName = anime.animeUI.name,
            animeEnJpName = anime.animeUI.enJpName,
            animePoster = anime.poster,
            animePosterThumb = anime.animeUI.posterThumb,
            lastEpisodeId = episode.episodeId,
            lastEpisodeNumber = episode.number,
            lastEpisodeTitle = episode.name,
            lastSeason = anime.animeUI.season,
            lastPart = anime.animeUI.part,
            lastYear = anime.animeUI.year,
        )
    }

    companion object {
        private const val TAG = "AnimeDetailsSM"
    }

}

@Immutable
data class AnimeState(
    val anime: DataState<AnimeDetailsFull> = DataState.Loading,
    val isFavorite: Boolean = false,
    val scrollToTop: Boolean = false,
)

@Immutable
data class AnimeDetailsFull(
    val anime: AnimeDetailsUI,
    val episodes: AnimeEpisodesUI,
)

sealed interface AnimeDetailsScreenEvents {
    object PopBack : AnimeDetailsScreenEvents
    object ScrollToTop : AnimeDetailsScreenEvents
    data class PushAnimeDetailsScreen(val animeId: Long) : AnimeDetailsScreenEvents
    data class ToggleFavorite(val anime: AnimeDetailsUI) : AnimeDetailsScreenEvents
    data class OnLinkClick(
        val filterId: Long?,
        val filterName: String,
        val filterType: FilterTypeUI
    ) : AnimeDetailsScreenEvents

    data class OnImageClick(
        val index: Int,
        val images: List<String>
    ) : AnimeDetailsScreenEvents

    data class OnEpisodeClick(val episode: AnimeEpisodeUI) : AnimeDetailsScreenEvents

    object Refresh : AnimeDetailsScreenEvents
}

sealed interface NavigationEvent {
    data class NavigateToAnimeDetails(val animeId: Long) : NavigationEvent
    data class NavigateToFilteredResult(
        val filterId: Long,
        val filterName: String,
        val filterType: FilterTypeUI
    ) : NavigationEvent

    data class NavigateToImagesView(
        val index: Int,
        val images: List<String>
    ) : NavigationEvent

    object NavigateToBack : NavigationEvent
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

    is NavigationEvent.NavigateToImagesView -> ImageScreen(
        index = this.index,
        images = this.images
    )
}
