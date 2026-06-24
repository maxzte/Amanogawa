package id.anison.amanogawa.feature.search.screenmodel

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.common.mapDataState
import id.anison.amanogawa.core.domain.repository.FilterRepo
import id.anison.amanogawa.core.model.mapper.toFilter
import id.anison.amanogawa.core.model.mapper.toFilterUI
import id.anison.amanogawa.core.model.FilterUI
import id.anison.amanogawa.core.model.GenreUI
import id.anison.amanogawa.core.model.TeamMemberUI
import id.anison.amanogawa.feature.search.screen.FilterResultScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

@Factory
class FilterScreenModel(
    @Provided private val filterRepo: FilterRepo,
) : StateScreenModel<FilterState>(FilterState()) {
    private val _navEventState = MutableSharedFlow<FilterNavigationEvent>()
    val navEventState = _navEventState.asSharedFlow()

    init {
        getFilters()
    }

    fun onEvents(event: FilterScreenEvents) {
        Log.d(tag = TAG) { "current event is: $event" }
        when (event) {
            is FilterScreenEvents.PopBack -> {
                screenModelScope.launch {
                    _navEventState.emit(value = FilterNavigationEvent.NavigateToBack)
                }
            }

            is FilterScreenEvents.ResetFilter -> {
                resetFilter()
            }

            is FilterScreenEvents.AddGenre -> {
                toggleGenre(genre = event.genre)
            }

            is FilterScreenEvents.AddTeamMember -> {
                toggleTeamMember(teamMemberUI = event.teamMember)
            }

            is FilterScreenEvents.ToggleYear -> {
                toggleYear(event.year)
            }

            is FilterScreenEvents.Search -> {
                screenModelScope.launch {
                    filterRepo.saveSearchFilter(
                        filter = mutableState.value.filter.toFilter()
                    )
                    _navEventState.emit(value = FilterNavigationEvent.NavigateToResult)
                }
            }

            is FilterScreenEvents.Refresh -> {
                refresh()
            }
        }
    }

    private fun getFilters() = screenModelScope.launch {
        mutableState.update { oldState ->
            oldState.copy(
                filterOptions = filterRepo.updateFilter().mapDataState { it.toFilterUI() },
            )
        }
    }

    private fun resetFilter() {
        mutableState.update { oldState -> oldState.copy(filter = FilterUI()) }
    }

    private fun toggleGenre(genre: GenreUI) = updateFilter { currentFilter ->
        val updatedGenres = currentFilter.genres.toMutableList().apply {
            if (!remove(genre)) {
                add(genre)
            }
        }
        currentFilter.copy(genres = updatedGenres)
    }

    private fun toggleTeamMember(teamMemberUI: TeamMemberUI) = updateFilter { currentFilter ->
        val currentTeamMembers = currentFilter.teamMembers
        val updatedTeamMembers = if (currentTeamMembers.contains(teamMemberUI)) {
            currentTeamMembers - teamMemberUI
        } else {
            currentTeamMembers + teamMemberUI
        }

        currentFilter.copy(teamMembers = updatedTeamMembers)
    }

    private fun toggleYear(year: Short) = updateFilter { currentFilter ->
        val currentYears = currentFilter.years
        val updatedYears = if (currentYears.contains(year)) {
            currentYears - year
        } else {
            currentYears + year
        }
        currentFilter.copy(years = updatedYears)
    }

    private fun updateFilter(transform: (FilterUI) -> FilterUI) {
        mutableState.update { oldState -> oldState.copy(filter = transform(oldState.filter)) }
    }

    private fun refresh() {
        getFilters()
    }

    companion object {
        private const val TAG = "ExtendedSearchSM"
    }

}

data class FilterState(
    val filterOptions: DataState<FilterUI> = DataState.Loading,
    val filter: FilterUI = FilterUI(),
    val selectedGenres: Set<GenreUI> = emptySet(),
    val selectedTeamMembers: Set<GenreUI> = emptySet(),
    val selectedYears: Set<Int> = emptySet(),
)

sealed interface FilterScreenEvents {
    object PopBack : FilterScreenEvents
    object ResetFilter : FilterScreenEvents
    data class AddGenre(val genre: GenreUI) : FilterScreenEvents
    data class AddTeamMember(val teamMember: TeamMemberUI) : FilterScreenEvents
    data class ToggleYear(val year: Short) : FilterScreenEvents
    object Search : FilterScreenEvents
    object Refresh : FilterScreenEvents
}

sealed interface FilterNavigationEvent {
    object NavigateToBack : FilterNavigationEvent
    object NavigateToResult : FilterNavigationEvent
}

internal fun FilterNavigationEvent.toScreen(): Screen? = when (this) {
    is FilterNavigationEvent.NavigateToBack -> null
    is FilterNavigationEvent.NavigateToResult -> FilterResultScreen(useSavedSearchFilter = true)
}
