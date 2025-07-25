package id.anison.amanogawa

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.data.ConnectivityRepo
import id.anison.amanogawa.core.data.settings.AppSettings
import id.anison.amanogawa.core.data.settings.SettingsRepo
import id.anison.amanogawa.tab.AmanogawaTab
import id.anison.amanogawa.tab.HomeTab
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenModel(
    private val settingsRepo: SettingsRepo,
    private val connectivityRepo: ConnectivityRepo,
    private val log: Logger,
) : StateScreenModel<MainUIState>(MainUIState.Loading) {

    val tabHistory = mutableListOf<AmanogawaTab>(HomeTab)

    private val _isRoot = MutableStateFlow(true)
    val isRoot: StateFlow<Boolean> get() = _isRoot

    private val _isTopBarVisible = MutableStateFlow(true)
    val isTopBarVisible: StateFlow<Boolean> get() = _isTopBarVisible.asStateFlow()

    private val _isBottomBarVisible = MutableStateFlow(true)
    val isBottomBarVisible: StateFlow<Boolean> get() = _isBottomBarVisible.asStateFlow()

    private val _events = MutableSharedFlow<MainUIEvent>()
    val events = _events.asSharedFlow()

    val isOnline = connectivityRepo.isConnected
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = true,
        )

    val appSettings: StateFlow<MainUIState> = settingsRepo.settings
        .map { settings ->
            MainUIState.Success(
                settings = AppSettings(
                    theme = settings.theme,
                    isDynamicColors = settings.isDynamicColors,
                    isBlackTheme = settings.isBlackTheme,
                    countColumnsInRow = settings.countColumnsInRow,
                    countColumnsInRowLandscape = settings.countColumnsInRowLandscape,
                    countEpisodesInRow = settings.countEpisodesInRow,
                    countEpisodesInRowLandscape = settings.countEpisodesInRowLandscape,
                )
            )
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = MainUIState.Loading,
        )

    fun pushTabToHistory(newTab: AmanogawaTab) {
        if (tabHistory.contains(newTab)) {
            if (newTab != HomeTab || tabHistory.first() != HomeTab) {
                tabHistory.remove(newTab)
            }
        }

        tabHistory.add(newTab)

        if (tabHistory.first() != HomeTab) {
            tabHistory.remove(HomeTab)
            tabHistory.add(HomeTab)
            val reordered = linkedSetOf<AmanogawaTab>()
            reordered.add(HomeTab)
            reordered.addAll(tabHistory.filter { it != HomeTab })
            tabHistory.clear()
            tabHistory.addAll(reordered)
        }
    }

    fun popFromTabHistory(): AmanogawaTab? {
        if (tabHistory.size > 1) {
            tabHistory.removeAt(tabHistory.lastIndex)
            log.d(TAG, "pop tabHistory = $tabHistory")
            return tabHistory.last()
        }
        return null
    }

    fun setTopBarVisible(value: Boolean) {
        _isTopBarVisible.value = value
    }

    fun setBottomBarVisible(value: Boolean) {
        _isBottomBarVisible.value = value
    }

    fun setIsRoot(value: Boolean) {
        _isRoot.value = value
    }

    fun triggerNavToRoot() {
        screenModelScope.launch {
            _events.emit(MainUIEvent.NavigateToRoot)
        }
    }

    fun triggerScrollToTop() = screenModelScope.launch {
        _events.emit(MainUIEvent.ScrollToTop)
    }

    companion object {
        private const val TAG = "MainSM"
    }

}

sealed interface MainUIState {
    object Loading : MainUIState
    data class Success(val settings: AppSettings) : MainUIState
}

sealed interface MainUIEvent {
    object ScrollToTop : MainUIEvent
    object NavigateToRoot : MainUIEvent
}
