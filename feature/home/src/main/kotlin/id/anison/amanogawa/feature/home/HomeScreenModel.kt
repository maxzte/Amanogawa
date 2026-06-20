package id.anison.amanogawa.feature.home

import androidx.compose.foundation.lazy.grid.LazyGridState
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import id.anison.amanogawa.core.domain.repository.AnimeRepo
import id.anison.amanogawa.core.domain.toAnimeUI
import id.anison.amanogawa.core.model.AnimeUI
import id.anison.amanogawa.core.model.DataState
import id.anison.amanogawa.core.model.mapDataState
import id.anison.amanogawa.core.navigation.SharedScreen.AllTitles
import id.anison.amanogawa.core.navigation.SharedScreen.AnimeDetails
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single
class HomeScreenModel(
    @Provided private val animeRepo: AnimeRepo,
) : StateScreenModel<HomeState>(HomeState()) {

    private val _scrollToTopEvent = MutableSharedFlow<Unit>()
    val scrollToTopEvent = _scrollToTopEvent.asSharedFlow()

    private val _navEventState = MutableSharedFlow<HomeNavigationEvent>()
    val navEventState = _navEventState.asSharedFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    val lazyGridState = LazyGridState()

    init {
        screenModelScope.launch { loadAnime() }
    }

    fun pushAnime(animeId: Long) {
        screenModelScope.launch {
            _navEventState.emit(
                HomeNavigationEvent.NavigateToAnimeDetails(animeId = animeId)
            )
        }
    }

    fun pushAllTitles() {
        screenModelScope.launch {
            _navEventState.emit(HomeNavigationEvent.NavigateToAllTitles)
        }
    }

    fun refresh() {
        screenModelScope.launch {
            _isRefreshing.value = true
            loadAnime()
            _isRefreshing.value = false
        }
    }

    fun requestScrollToTop() {
        screenModelScope.launch {
            _scrollToTopEvent.emit(Unit)
        }
    }

    private suspend fun loadAnime() {
        mutableState.update { oldState ->
            oldState.copy(
                currentAnimeState = animeRepo.getCurrentAnimeList()
                    .mapDataState { it.map { animeUI -> animeUI.toAnimeUI() } },
                animeListState = animeRepo.getFirstPage()
                    .mapDataState { it.map { animeUI -> animeUI.toAnimeUI() } }
            )
        }
    }

}

data class HomeState(
    val currentAnimeState: DataState<List<AnimeUI>> = DataState.Loading,
    val animeListState: DataState<List<AnimeUI>> = DataState.Loading,
)

sealed interface HomeNavigationEvent {
    data class NavigateToAnimeDetails(val animeId: Long) : HomeNavigationEvent
    data object NavigateToAllTitles : HomeNavigationEvent
}

internal fun HomeNavigationEvent.toScreen(): Screen = when (this) {
    is HomeNavigationEvent.NavigateToAnimeDetails -> {
        ScreenRegistry.get(AnimeDetails(animeId = this.animeId))
    }

    HomeNavigationEvent.NavigateToAllTitles -> {
        ScreenRegistry.get(AllTitles)
    }
}