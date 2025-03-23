package id.anison.feature.search.screenmodel

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.ReqRes
import id.anison.data.ConnectivityRepo
import id.anison.data.search.SearchRepo
import id.anison.feature.anime.model.AnimeUI
import id.anison.feature.anime.model.toAnimeUI
import id.anison.feature.common.DataState
import id.anison.feature.search.model.DubersUI
import id.anison.feature.search.model.FilterUI
import id.anison.feature.search.model.GenreUI
import id.anison.feature.search.model.toFilterDto
import id.anison.feature.search.model.toFilterUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtendedSearchScreenModel(
    private val searchRepo: SearchRepo,
) : StateScreenModel<ExtendedSearchState>(ExtendedSearchState()) {

    private val _extendedSearchState = MutableStateFlow(ExtendedSearchState())
    val extendedSearchState: StateFlow<ExtendedSearchState> = _extendedSearchState.asStateFlow()

    companion object {
        const val TAG = "ExtendedSearchSM"
    }

    init {
//        getFilters()
    }

    fun getFilters() = screenModelScope.launch {
        when (val options = searchRepo.getFilters()) {
            is ReqRes.Error -> {
                _extendedSearchState.update { state ->
                    state.copy(filterOptions = DataState.Error(options.error))
                }
            }

            is ReqRes.InProgress -> {
                _extendedSearchState.update { state ->
                    state.copy(filterOptions = DataState.Loading)
                }
            }

            is ReqRes.Success -> {
                _extendedSearchState.update { state ->
                    state.copy(
                        filterOptions = DataState.Success(options.data.toFilterUI()),
                    )
                }
            }
        }
    }

    private fun updateDubers(
        current: Map<Long, String>,
        new: Map<Long, String>
    ): Map<Long, String> {
        return current.toMutableMap().apply {
            new.forEach { (id, name) ->
                if (containsKey(id)) {
                    remove(id)
                } else {
                    put(id, name)
                }
            }
        }
    }

    private fun findMatchingFilter(dubers: DubersUI, name: String): FilterUI {
        val matchedV = dubers.v.filterValues { it == name }
        val matchedS = dubers.s.filterValues { it == name }
        val matchedT = dubers.t.filterValues { it == name }

        val matchesDubers = DubersUI(v = matchedV, s = matchedS, t = matchedT)

        return if (matchesDubers.v.isNotEmpty() || matchesDubers.s.isNotEmpty() || matchesDubers.t.isNotEmpty()) {
            FilterUI(dubers = matchesDubers)
        } else {
            FilterUI()
        }
    }

    fun addGenre(genre: GenreUI) = updateFilter { currentFilter ->
        val updatedGenres = currentFilter.genres.toMutableList().apply {
            if (!remove(genre)) {
                add(genre)
            }
        }
        currentFilter.copy(genres = updatedGenres)
    }

    fun addTranslator(t: Map.Entry<Long, String>) = updateDubersFilter(t, DubersUI::t)

    fun addVoice(v: Map.Entry<Long, String>) = updateDubersFilter(v, DubersUI::v)

    fun addSound(s: Map.Entry<Long, String>) = updateDubersFilter(s, DubersUI::s)

    private fun updateDubersFilter(
        entry: Map.Entry<Long, String>,
        selector: (DubersUI) -> Map<Long, String>
    ) {
        updateFilter { filter ->
            filter.copy(
                dubers = filter.dubers.copy(
                    updateDubers(
                        selector(filter.dubers),
                        mapOf(entry.key to entry.value)
                    )
                )
            )
        }
    }

    fun addYear(year: Short) = updateFilter { currentFilter ->
        currentFilter.copy(years = currentFilter.years + year)
    }

    fun removeYear(year: Short) = updateFilter { currentFilter ->
        currentFilter.copy(years = currentFilter.years - year)
    }

    fun updateFilter(transform: (FilterUI) -> FilterUI) {
        _extendedSearchState.update { it.copy(filter = transform(it.filter)) }
    }

    fun search(filter: FilterUI? = null) {
        val appliedFilter = filter ?: _extendedSearchState.value.filter
        Log.e(TAG, "filter is: $filter")
        screenModelScope.launch {
            val filteredTitles = searchRepo.getFilteredTitles(appliedFilter.toFilterDto())
                .map { pagingData -> pagingData.map { dataTitle -> dataTitle.toAnimeUI() } }
                .cachedIn(screenModelScope)
                .stateIn(
                    scope = screenModelScope,
                    started = SharingStarted.WhileSubscribed(5000L),
                    initialValue = PagingData.empty()
                )

            _extendedSearchState.update { state ->
                if (filter == null) {
                    state.copy(filteredTitles = filteredTitles)
                } else {
                    state.copy(titlesByLink = filteredTitles)
                }
            }
        }
    }

    fun searchByTeammateName(name: String) {
        Log.e(TAG, "filter is: $name")
        val currentFilter = _extendedSearchState.value.filterOptions
        if (currentFilter is DataState.Success) {
            screenModelScope.launch {
                val updatedFilter = findMatchingFilter(currentFilter.data.dubers, name)
                val filteredTitles = searchRepo.getFilteredTitles(updatedFilter.toFilterDto())
                    .map { it.map { dataTitle -> dataTitle.toAnimeUI() } }
                    .cachedIn(screenModelScope)
                    .stateIn(
                        scope = screenModelScope,
                        started = SharingStarted.WhileSubscribed(5000L),
                        initialValue = PagingData.empty()
                    )

                _extendedSearchState.update { state -> state.copy(titlesByLink = filteredTitles) }
            }
        } else {
            Log.e(TAG, "SOMETHING WENT WRONG\n${(currentFilter as? DataState.Error)?.exception}")
        }
    }

    fun refresh(filterName: String? = null) {
        if (_extendedSearchState.value.filterOptions is DataState.Loading) return
        getFilters()
        filterName?.let { searchByTeammateName(it) }
        /*if (_extendedSearchState.value.filterOptions is DataState.Loading) {
            getFilters()
            if (filterName != null && _extendedSearchState.value.filterOptions !is DataState.Loading) {
                searchByTeammateName(filterName)
            }
        }*/
    }
}

data class ExtendedSearchState(
    val filterOptions: DataState<FilterUI> = DataState.Loading,
    val filter: FilterUI = FilterUI(),
    val filteredTitles: StateFlow<PagingData<AnimeUI>> = MutableStateFlow(PagingData.empty()),
    val titlesByLink: StateFlow<PagingData<AnimeUI>> = MutableStateFlow(PagingData.empty()),
)