package id.anison.feature

import android.util.Log
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.ConnectivityRepo
import id.anison.data.settings.AppSettings
import id.anison.data.settings.AppTheme
import id.anison.data.settings.SettingsRepo
import id.anison.feature.profile.SettingsUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenModel(
    private val settingsRepo: SettingsRepo,
    private val connectivityRepo: ConnectivityRepo,
) : StateScreenModel<MainUIState>(MainUIState.Loading) {

    val isOnline = connectivityRepo.isConnected
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = true,
        )
    
    private val _toRoute = MutableStateFlow(false)
    val toRoute: StateFlow<Boolean> get() = _toRoute

    private val _isTopBarVisible = MutableStateFlow(true)
    val isTopBarVisible: StateFlow<Boolean> get() = _isTopBarVisible.asStateFlow()

    private val _isBottomBarVisible = MutableStateFlow(true)
    val isBottomBarVisible: StateFlow<Boolean> get() = _isBottomBarVisible.asStateFlow()

    val appSettings: StateFlow<MainUIState> = settingsRepo.settings
        .map { settings ->
            MainUIState.Success(
                settings = AppSettings(
                    theme = settings.theme,
                    isDynamicColors = settings.isDynamicColors,
                    isBlackTheme = settings.isBlackTheme,
                    countColumnsInRow = settings.countColumnsInRow,
                    countColumnsInRowLandscape = settings.countColumnsInRowLandscape,
                )
            )
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = MainUIState.Loading,
        )

    private fun updateSettings(updateAction: suspend (SettingsRepo) -> Unit) {
        screenModelScope.launch {
            updateAction(settingsRepo)
        }
    }

    fun setThemeValue(value: AppTheme) = updateSettings { it.saveTheme(value) }

    fun setDynamicThemeValue(value: Boolean) = updateSettings { it.saveDynamicColor(value) }

    fun setBlackThemeValue(value: Boolean) = updateSettings { it.saveBlackTheme(value) }

    fun setColumnsInRowValue(value: Int) = updateSettings { it.saveCountColumnsInRow(value) }

    fun setColumnsInRowValueLandscape(value: Int) =
        updateSettings { it.saveCountColumnsInRowLandscape(value) }

    fun setTopBarVisible(value: Boolean) {
        _isTopBarVisible.value = value
    }

    fun setBottomBarVisible(value: Boolean) {
        Log.d("MainSM", "BottomBar visibility changed: $value")
        _isBottomBarVisible.value = value
    }

    fun setNavigateToRoute(value: Boolean) {
        _toRoute.value = value
    }
}

sealed interface MainUIState {
    data object Loading : MainUIState
    data class Success(val settings: AppSettings) : MainUIState
}
