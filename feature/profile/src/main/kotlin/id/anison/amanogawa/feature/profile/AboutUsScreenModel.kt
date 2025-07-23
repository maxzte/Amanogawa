package id.anison.amanogawa.feature.profile

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.common.SingleEventHandler
import id.anison.amanogawa.core.common.mapDataState
import id.anison.amanogawa.core.data.search.SearchRepo
import id.anison.amanogawa.core.data.settings.SettingsRepo
import id.anison.amanogawa.core.domain.toTeamUI
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.model.TeamMemberTypeUI
import id.anison.amanogawa.core.model.TeamMemberUI
import id.anison.amanogawa.core.model.TeamUI
import id.anison.amanogawa.core.navigation.SharedScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AboutUsScreenModel(
    private val settingsRepo: SettingsRepo,
    private val log: Logger,
) : ScreenModel {
    private val singleEventHandler = SingleEventHandler()
    private val _navigationEventState = MutableSharedFlow<AboutUsNavigationEvent>()
    val navigationEventState = _navigationEventState.asSharedFlow()
    private val _teamState = mutableStateOf<DataState<List<TeamUI>>>(DataState.Loading)
    val teamState = _teamState
    val lazyListState = LazyListState()

    init {
        getTeam()
    }

    fun getTeam() = screenModelScope.launch {
        _teamState.value = settingsRepo.getTeam().mapDataState { it.map { team -> team.toTeamUI() } }
    }

    fun onEvents(event: AboutUsScreenEvents) {
        log.d(TAG, "curr event is: $event")
        when (event) {
            is AboutUsScreenEvents.PopBack -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navigationEventState.emit(
                            value = AboutUsNavigationEvent.NavigateToBack
                        )
                    }
                }
            }

            is AboutUsScreenEvents.NavigateToTitlesByTeamMemberName -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navigationEventState.emit(
                            value = AboutUsNavigationEvent.NavigateToFilterResult(
                                teamMemberName = event.teamMemberName,
                            )
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "AboutUsSM"
    }

}

data class AboutUsState(
    val team: DataState<List<TeamMemberUI>> = DataState.Loading,
    val isLoading: Boolean = true,
)

sealed interface AboutUsScreenEvents {
    object PopBack : AboutUsScreenEvents
    data class NavigateToTitlesByTeamMemberName(val teamMemberName: String) : AboutUsScreenEvents
}

sealed interface AboutUsNavigationEvent {
    object NavigateToBack : AboutUsNavigationEvent
    data class NavigateToFilterResult(val teamMemberName: String) : AboutUsNavigationEvent
}

internal fun AboutUsNavigationEvent.toScreen() = when (this) {
    AboutUsNavigationEvent.NavigateToBack -> null
    is AboutUsNavigationEvent.NavigateToFilterResult -> {
        ScreenRegistry.get(
            SharedScreen.SearchResult(
                filterId = -1L,
                filterName = this.teamMemberName,
                filterType = FilterTypeUI.ALL_TEAM_MEMBER
            )
        )
    }
}