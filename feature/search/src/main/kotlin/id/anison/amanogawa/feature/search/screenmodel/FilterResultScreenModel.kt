package id.anison.amanogawa.feature.search.screenmodel

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.data.filter.FilterRepo
import id.anison.amanogawa.core.data.filter.FilterType
import id.anison.amanogawa.core.data.search.SearchRepo
import id.anison.amanogawa.core.domain.toAnimeUI
import id.anison.amanogawa.core.model.AnimeUI
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.model.PagingEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class FilterResultScreenModel(
    private val filterParamId: Long? = null,
    private val filterName: String = "",
    private val filterType: FilterTypeUI,
    private val searchRepo: SearchRepo,
    private val filterRepo: FilterRepo,
    private val log: Logger,
) : StateScreenModel<FilterResultState>(FilterResultState()) {
    private val _pagingEvent = MutableSharedFlow<PagingEvent>()
    val pagingEvent: SharedFlow<PagingEvent> = _pagingEvent
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        if (filterName != "") {
            searchByFilterName()
        } else {
            searchByFilter()
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
        log.d(TAG, "isExist: $isDisplayedFilterExist")
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

    fun refresh() = screenModelScope.launch {
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
    data class PushTitle(val id: Long) : FilterResultScreenEvents
    object Refresh : FilterResultScreenEvents
}
