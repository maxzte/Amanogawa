package id.anison.amanogawa.feature.search.screenmodel

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.data.search.SearchRepo
import id.anison.amanogawa.core.domain.toAnimeDetailsUI
import id.anison.amanogawa.core.model.AnimeDetailsUI
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.navigation.SharedScreen
import id.anison.amanogawa.core.navigation.SharedScreen.AnimeDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RandomAnimeScreenModel(
    private val searchRepo: SearchRepo,
    private val log: Logger,
) : StateScreenModel<RandomTitleState>(RandomTitleState()) {

    private val _navEventState = MutableSharedFlow<RandomAnimeNavigationEvent>()
    val navEventState = _navEventState.asSharedFlow()

    init {
        getRandomTitles()
    }

    fun onEvents(event: RandomScreenEvents) {
        log.d(TAG, "curr event is: $event")
        when (event) {
            is RandomScreenEvents.PopBack -> {
                screenModelScope.launch {
                    _navEventState.tryEmit(value = RandomAnimeNavigationEvent.NavigateToBack)
                }
            }

            is RandomScreenEvents.PushAnimeDetailsScreen -> {
                screenModelScope.launch {
                    _navEventState.tryEmit(
                        value = RandomAnimeNavigationEvent.NavigateToAnimeDetails(
                            animeId = event.animeId
                        )
                    )
                }
            }

            is RandomScreenEvents.OnLinkClick -> {
                screenModelScope.launch {
                    _navEventState.tryEmit(
                        value = RandomAnimeNavigationEvent.NavigateToFilteredResult(
                            filterId = event.filterId ?: -1,
                            filterName = event.filterName,
                            filterType = event.filterType
                        )
                    )
                }
            }

            is RandomScreenEvents.Refresh -> {
                getRandomTitles()
            }

        }
    }

    private fun getRandomTitles() {
        mutableState.update { oldState ->
            oldState.copy(
                titles = searchRepo.getRandomTitles()
                    .map { pagingData -> pagingData.map { animeDetails -> animeDetails.toAnimeDetailsUI() } }
                    .cachedIn(screenModelScope)
            )
        }
    }

    companion object Companion {
        private const val TAG = "RandomTitlesSM"
    }

}

data class RandomTitleState(
    val titles: Flow<PagingData<AnimeDetailsUI>> = flowOf(PagingData.empty()),
)

sealed interface RandomScreenEvents {
    object PopBack : RandomScreenEvents
    data class PushAnimeDetailsScreen(val animeId: Long) : RandomScreenEvents
    data class OnLinkClick(
        val filterId: Long?,
        val filterName: String,
        val filterType: FilterTypeUI
    ) : RandomScreenEvents

    object Refresh : RandomScreenEvents
}

sealed interface RandomAnimeNavigationEvent {
    data class NavigateToAnimeDetails(val animeId: Long) : RandomAnimeNavigationEvent
    data class NavigateToFilteredResult(
        val filterId: Long,
        val filterName: String,
        val filterType: FilterTypeUI
    ) : RandomAnimeNavigationEvent

    object NavigateToBack : RandomAnimeNavigationEvent
}

internal fun RandomAnimeNavigationEvent.toScreen(): Screen? = when (this) {
    is RandomAnimeNavigationEvent.NavigateToAnimeDetails -> ScreenRegistry.get(AnimeDetails(animeId = this.animeId))
    is RandomAnimeNavigationEvent.NavigateToBack -> null
    is RandomAnimeNavigationEvent.NavigateToFilteredResult -> ScreenRegistry.get(
        SharedScreen.SearchResult(
            filterId = this.filterId,
            filterName = this.filterName,
            filterType = this.filterType
        )
    )
}