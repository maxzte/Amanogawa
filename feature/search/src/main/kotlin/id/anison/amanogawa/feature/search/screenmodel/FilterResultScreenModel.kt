package id.anison.amanogawa.feature.search.screenmodel

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.model.SingleEventHandler
import id.anison.amanogawa.core.domain.repository.FilterRepo
import id.anison.amanogawa.core.domain.model.FilterType
import id.anison.amanogawa.core.domain.repository.SearchRepo
import id.anison.amanogawa.core.domain.toAnimeUI
import id.anison.amanogawa.core.model.AnimeUI
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.model.PagingEvent
import id.anison.amanogawa.core.navigation.SharedScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Provided


@Factory
class FilterResultScreenModel(
    @InjectedParam private val filterParamId: Long? = null,
    @InjectedParam private val filterName: String = "",
    @InjectedParam private val filterType: FilterTypeUI,
    @Provided private val searchRepo: SearchRepo,
    @Provided private val filterRepo: FilterRepo,
) : StateScreenModel<FilterResultState>(FilterResultState()) {
    private val singleEventHandler = SingleEventHandler()
    private val _pagingEvent = MutableSharedFlow<PagingEvent>()
    val pagingEvent: SharedFlow<PagingEvent> = _pagingEvent

    private val _navigationEventState = MutableSharedFlow<FilterResultNavigationState>()
    val navigationEventState = _navigationEventState.asSharedFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        if (filterName != "") {
            searchByFilterName()
        } else {
            searchByFilter()
        }
    }

    fun onEvents(event: FilterResultScreenEvents) {
        when (event) {
            is FilterResultScreenEvents.PopBack -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navigationEventState.emit(
                            value = FilterResultNavigationState.NavigateToBack
                        )
                    }
                }
            }

            is FilterResultScreenEvents.PushTitle -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navigationEventState.emit(
                            value = FilterResultNavigationState.NavigateToDetails(
                                animeId = event.animeId
                            )
                        )
                    }
                }
            }

            is FilterResultScreenEvents.Refresh -> {
                if (singleEventHandler.canTrigger()) {
                    refresh()
                }
            }
        }
    }

    fun searchByFilter() = screenModelScope.launch {
        val filteredTitles = searchRepo.getTitlesBySearchFilter()
            .onEach {
                _pagingEvent.emit(PagingEvent.RELOAD)
            }
            .map { it.map { dataTitle -> dataTitle.toAnimeUI() } }
            .cachedIn(screenModelScope)
            .stateIn(
                scope = screenModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = PagingData.empty()
            )

        mutableState.update { state -> state.copy(filteredTitles = filteredTitles) }
    }

    fun searchByFilterName() = screenModelScope.launch {
        val isDisplayedFilterExist = filterRepo.isExist(FilterType.DISPLAYED)
        Log.d(tag = TAG) { "isExist: $isDisplayedFilterExist" }
        if (!isDisplayedFilterExist) filterRepo.updateFilter()
        val filteredTitles = searchRepo.getTitlesByFilterName(
            filterParamId,
            filterName,
            FilterType.valueOf(filterType.name)
        )
            .onEach {
                _pagingEvent.emit(PagingEvent.RELOAD)
            }
            .map { it.map { dataTitle -> dataTitle.toAnimeUI() } }
            .cachedIn(screenModelScope)
            .stateIn(
                scope = screenModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = PagingData.empty()
            )

        mutableState.update { state -> state.copy(titlesByLink = filteredTitles) }
    }

    fun setRefreshing(isRefreshing: Boolean) {
        _isRefreshing.value = isRefreshing
    }

    private fun refresh() = screenModelScope.launch {
        _isRefreshing.value = true
        if (filterName != "") {
            searchByFilterName()
        } else {
            searchByFilter()
        }
    }


    companion object {
        private const val TAG = "FilterResultSM"
    }

}

data class FilterResultState(
    val filteredTitles: Flow<PagingData<AnimeUI>> = flowOf(value = PagingData.empty()),
    val titlesByLink: Flow<PagingData<AnimeUI>> = flowOf(value = PagingData.empty()),
)

sealed interface FilterResultScreenEvents {
    object PopBack : FilterResultScreenEvents
    data class PushTitle(val animeId: Long) : FilterResultScreenEvents
    object Refresh : FilterResultScreenEvents
}

sealed interface FilterResultNavigationState {
    object NavigateToBack : FilterResultNavigationState
    data class NavigateToDetails(val animeId: Long) : FilterResultNavigationState
}

internal fun FilterResultNavigationState.toScreen() = when (this) {
    FilterResultNavigationState.NavigateToBack -> null
    is FilterResultNavigationState.NavigateToDetails -> ScreenRegistry.get(
        SharedScreen.AnimeDetails(animeId = this.animeId)
    )
}