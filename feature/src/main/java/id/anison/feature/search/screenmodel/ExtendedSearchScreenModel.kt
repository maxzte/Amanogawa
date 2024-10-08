package id.anison.feature.search.screenmodel

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.ReqRes
import id.anison.data.repository.AmanogawaRepository
import id.anison.data.repository.ConnectivityRepository
import id.anison.feature.common.DataState
import id.anison.feature.model.DubersUI
import id.anison.feature.model.FilterUI
import id.anison.feature.model.GenreUI
import id.anison.feature.model.SUI
import id.anison.feature.model.TUI
import id.anison.feature.model.VUI
import id.anison.feature.model.toDataFilter
import id.anison.feature.model.toFilterUI
import id.anison.feature.model.toTitleUI
import id.anison.feature.search.state.ExtendedSearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExtendedSearchScreenModel(
    private val repository: AmanogawaRepository,
    private val connectivityRepository: ConnectivityRepository,
) : StateScreenModel<ExtendedSearchState>(ExtendedSearchState.None) {
    private val _extendedSearchState = MutableStateFlow(ExtendedSearchState.None)
    val extendedSearchState: StateFlow<ExtendedSearchState> = _extendedSearchState.asStateFlow()
    val isOnline = connectivityRepository.isConnected
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Lazily,
            initialValue = true,
        )

    init {
        Log.d("TAG", "INIT")
        screenModelScope.launch {
            _extendedSearchState.update { state -> state.copy(isOnline = isOnline.value) }
        }
        getFilters()
//        observeConnection()
    }

    /*private fun observeConnection() {
        screenModelScope.launch {
            isOnline.collect { isConnected ->
                if (isConnected) {
                    _extendedSearchState.update { it.copy(isOnline = true) }
                }
                else _extendedSearchState.update { it.copy(isOnline = false) }
            }
        }
    }*/

    fun refresh(filterName: String? = null) {
        if (_extendedSearchState.value.isOnline && _extendedSearchState.value.filterOptions is DataState.Loading) {
            getFilters()
            if (filterName != null && _extendedSearchState.value.filterOptions !is DataState.Loading) {
                searchByTeammateName(filterName) /*else search()*/
                _extendedSearchState.update { it.copy(isOnline = true) }
            }
        }
    }

    private fun getFilters() {
        screenModelScope.launch {
//        isOnline.collectLatest {
//            _extendedSearchState.update { state -> state.copy(isOnline = _extendedSearchState.value.isOnline) }
//        }
            when (val options = repository.getFilters()) {
                is ReqRes.Error -> {
                    _extendedSearchState.update { state ->
                        state.copy(filterOptions = options.error?.let { DataState.Error(it) })
                    }
                }

                is ReqRes.InProgress ->
                    _extendedSearchState.update { it.copy(filterOptions = DataState.Loading) }

                is ReqRes.Success -> {
                    _extendedSearchState.update {
                        it.copy(
                            filterOptions = DataState.Success(options.data.toFilterUI()),
                            isOnline = true
                        )
                    }
                }
            }
        }
    }

    fun addGenre(genre: GenreUI) {
        val currentState = _extendedSearchState.value
        val currentFilter = currentState.filter
        val updatedGenres =
            if (currentFilter.genres.any { it.id == genre.id }) {
                currentFilter.genres.mapNotNull { if (it.id == genre.id) null else it }
            } else currentFilter.genres + genre

        val updatedFilterUI = currentFilter.copy(genres = updatedGenres)
        _extendedSearchState.value = currentState.copy(filter = updatedFilterUI)
    }

    fun addTranslator(translator: TUI) {
        val currentState = _extendedSearchState.value
        val currentFilter = currentState.filter
        val updatedTranslators = if (currentFilter.dubers.t.any { it.id == translator.id }) {
            currentFilter.dubers.t.mapNotNull { if (it.id == translator.id) null else it }
        } else currentFilter.dubers.t + translator

        val updatedDubers = currentFilter.dubers.copy(t = updatedTranslators)
        val updatedFilterUI = currentFilter.copy(dubers = updatedDubers)
        _extendedSearchState.value = currentState.copy(filter = updatedFilterUI)
    }

    fun addVoice(voice: VUI) {
        val currentState = _extendedSearchState.value
        val currentFilter = currentState.filter
        val updatedVoices = if (currentFilter.dubers.v.any { it.id == voice.id }) {
            currentFilter.dubers.v.mapNotNull { if (it.id == voice.id) null else it }
        } else currentFilter.dubers.v + voice

        val updatedDubers = currentFilter.dubers.copy(v = updatedVoices)
        val updatedFilterUI = currentFilter.copy(dubers = updatedDubers)
        _extendedSearchState.value = currentState.copy(filter = updatedFilterUI)
    }

    fun addSound(sound: SUI) {
        val currentState = _extendedSearchState.value
        val currentFilter = currentState.filter
        val updatedSound = if (currentFilter.dubers.s.any { it.id == sound.id }) {
            currentFilter.dubers.s.mapNotNull { if (it.id == sound.id) null else it }
        } else currentFilter.dubers.s + sound

        val updatedDubers = currentFilter.dubers.copy(s = updatedSound)
        val updatedFilterUI = currentFilter.copy(dubers = updatedDubers)
        _extendedSearchState.value = currentState.copy(filter = updatedFilterUI)
    }

    fun addYear(year: Int) {
        val currentState = _extendedSearchState.value
        val currentFilter = currentState.filter

        val updatedYears = currentFilter.years + year
        val updatedFilterUI = currentFilter.copy(years = updatedYears)
        _extendedSearchState.value = currentState.copy(filter = updatedFilterUI)
    }

    fun removeYear(year: Int) {
        val currentState = _extendedSearchState.value
        val currentFilter = currentState.filter

        val updatedYears = currentFilter.years - year
        val updatedFilterUI = currentFilter.copy(years = updatedYears)
        _extendedSearchState.value = currentState.copy(filter = updatedFilterUI)
    }

    fun search(filter: FilterUI? = null) {
        val currentState = _extendedSearchState.value
        val currentFilter = filter ?: currentState.filter
        Log.d("esm", "filter is: $filter")
        screenModelScope.launch {
            val filteredTitles =
                repository.getFilteredTitles(currentFilter.toDataFilter())
                    .map { pagingData -> pagingData.map { dataTitle -> dataTitle.toTitleUI() } }
                    .cachedIn(screenModelScope)
                    .stateIn(screenModelScope, SharingStarted.Lazily, PagingData.empty())
            if (filter == null)
                _extendedSearchState.value = currentState.copy(filteredTitles = filteredTitles)
            else _extendedSearchState.value = currentState.copy(titlesByLink = filteredTitles)
        }
    }

    fun searchByTeammateName(name: String) {
        Log.d("esm", "filter is: $name")
        if (isOnline.value) {
            when (val currentFilter = _extendedSearchState.value.filterOptions) {
                is DataState.Success -> {
                    val updFilter = findMatchingFilter(currentFilter.data.dubers, name)
                    screenModelScope.launch {
                        val filteredTitles =
                            repository.getFilteredTitles(updFilter.toDataFilter())
                                .map { pagingData -> pagingData.map { dataTitle -> dataTitle.toTitleUI() } }
                                .cachedIn(screenModelScope)
                                .stateIn(
                                    screenModelScope,
                                    SharingStarted.Lazily,
                                    PagingData.empty()
                                )

                        _extendedSearchState.update { state -> state.copy(titlesByLink = filteredTitles) }
                    }
                }

                is DataState.Error -> {
                    Log.d("esm", "SOMETHIN WENT WRONG\n${currentFilter.exception}")
                }

                else -> {}
            }
        }
    }

    private fun findMatchingFilter(dubersFromFilterOptions: DubersUI, name: String): FilterUI {
        val matchedRoles = mutableListOf<Any>()
        dubersFromFilterOptions.v.find { it.name == name }
            ?.let { matchedRoles.add(VUI(it.id, it.name)) }
        dubersFromFilterOptions.s.find { it.name == name }
            ?.let { matchedRoles.add(SUI(it.id, it.name)) }
        dubersFromFilterOptions.t.find { it.name == name }
            ?.let { matchedRoles.add(TUI(it.id, it.name)) }

        return if (matchedRoles.isNotEmpty()) {
            FilterUI(
                dubers = DubersUI(
                    v = matchedRoles.filterIsInstance<VUI>(),
                    s = matchedRoles.filterIsInstance<SUI>(),
                    t = matchedRoles.filterIsInstance<TUI>()
                )
            )
        } else FilterUI()
    }
}