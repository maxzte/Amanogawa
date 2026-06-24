package id.anison.amanogawa.feature.search.screenmodel

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.model.SingleEventHandler
import id.anison.amanogawa.core.domain.repository.SearchRepo
import id.anison.amanogawa.core.domain.repository.AnimeRepo
import id.anison.amanogawa.core.model.mapper.toAnimeDetailsUI
import id.anison.amanogawa.core.model.mapper.toAnimeUI
import id.anison.amanogawa.core.model.AnimeDetailsUI
import id.anison.amanogawa.core.model.AnimeUI
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.navigation.SharedScreen
import id.anison.amanogawa.core.navigation.SharedScreen.AnimeDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

@Factory
class RandomAnimeScreenModel(
    @Provided private val searchRepo: SearchRepo,
    @Provided private val animeRepo: AnimeRepo,
) : StateScreenModel<RandomTitleState>(RandomTitleState()) {
    private val singleEventHandler = SingleEventHandler()
    private val _navEventState = MutableSharedFlow<RandomAnimeNavigationEvent>()
    val navEventState = _navEventState.asSharedFlow()

    private val _isModalSheetVisible = MutableStateFlow(ModalSheetState())
    val isModalSheetVisible = _isModalSheetVisible.asStateFlow()

    init {
        getRandomTitles()
    }

    fun onEvents(event: RandomScreenEvents) {
        Log.d(tag = TAG) { "curr event is: $event" }
        when (event) {
            RandomScreenEvents.PopBack -> {
                screenModelScope.launch {
                    _navEventState.emit(value = RandomAnimeNavigationEvent.NavigateToBack)
                }
            }

            is RandomScreenEvents.PushAnimeDetailsScreen -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navEventState.emit(
                            value = RandomAnimeNavigationEvent.NavigateToAnimeDetails(
                                animeId = event.animeId
                            )
                        )
                    }
                }
            }

            is RandomScreenEvents.OnLinkClick -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navEventState.emit(
                            value = RandomAnimeNavigationEvent.NavigateToFilteredResult(
                                filterId = event.filterId ?: -1,
                                filterName = event.filterName,
                                filterType = event.filterType
                            )
                        )
                    }
                }
            }

            is RandomScreenEvents.SetModalSheetVisibility -> {
                if (event.isVisible && (event.animeId != null)) {
                    loadDetails(event.animeId)
                }
                _isModalSheetVisible.update { oldState ->
                    oldState.copy(
                        pageIndex = event.pageIndex,
                        isVisible = event.isVisible,
                        animeId = event.animeId
                    )
                }
            }

            is RandomScreenEvents.LoadAnimeDetails -> {
                loadDetails(event.animeId)
            }

            RandomScreenEvents.Refresh -> {
                if (singleEventHandler.canTrigger()) {
                    getRandomTitles()
                }
            }

        }
    }

    private fun getRandomTitles() {
        mutableState.update { oldState ->
            oldState.copy(
                titles = searchRepo.getRandomTitles()
                    .map { pagingData -> pagingData.map { anime -> anime.toAnimeUI() } }
                    .cachedIn(screenModelScope)
            )
        }
    }

    private fun loadDetails(animeId: Long) {
        if (state.value.details.containsKey(animeId)) return

        screenModelScope.launch {
            val result = animeRepo.getAnimeDetailsById(animeId)
            if (result is DataState.Success) {
                mutableState.update { oldState ->
                    oldState.copy(
                        details = oldState.details + (animeId to result.data.toAnimeDetailsUI())
                    )
                }
            }
        }
    }

    companion object Companion {
        private const val TAG = "RandomTitlesSM"
    }

}

data class RandomTitleState(
    val titles: Flow<PagingData<AnimeUI>> = flowOf(PagingData.empty()),
    val details: Map<Long, AnimeDetailsUI> = emptyMap(),
)

data class ModalSheetState(
    val pageIndex: Int? = null,
    val isVisible: Boolean = false,
    val animeId: Long? = null,
)

sealed interface RandomScreenEvents {
    data object PopBack : RandomScreenEvents
    data class PushAnimeDetailsScreen(val animeId: Long) : RandomScreenEvents
    data class OnLinkClick(
        val filterId: Long?,
        val filterName: String,
        val filterType: FilterTypeUI
    ) : RandomScreenEvents

    data class SetModalSheetVisibility(
        val pageIndex: Int?,
        val isVisible: Boolean,
        val animeId: Long? = null,
    ) : RandomScreenEvents

    data class LoadAnimeDetails(val animeId: Long) : RandomScreenEvents

    data object Refresh : RandomScreenEvents
}

sealed interface RandomAnimeNavigationEvent {
    data class NavigateToAnimeDetails(val animeId: Long) : RandomAnimeNavigationEvent
    data class NavigateToFilteredResult(
        val filterId: Long,
        val filterName: String,
        val filterType: FilterTypeUI
    ) : RandomAnimeNavigationEvent

    data object NavigateToBack : RandomAnimeNavigationEvent
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