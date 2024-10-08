package id.anison.feature.profile

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.repository.SettingsRepository
import id.anison.data.model.AppSettings
import id.anison.data.model.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val repository: SettingsRepository
) : StateScreenModel<SettingsState>(SettingsState.None) {
    private val _settingsState = MutableStateFlow(SettingsState.None)
    val settingsState = _settingsState.asStateFlow()

    val appSettings: StateFlow<SettingsUIState> = repository.settings
        .map {
            SettingsUIState.Success(
                settings = AppSettings(
                    theme = it.theme,
                    isDynamicColors = it.isDynamicColors,
                    isBlackTheme = it.isBlackTheme,
                    countColumnsInRow = it.countColumnsInRow,
                    countColumnsInRowLandscape = it.countColumnsInRowLandscape,
                )
            )
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SettingsUIState.Loading,
        )

    fun setThemeValue(value: AppTheme) {
        screenModelScope.launch {
            repository.saveTheme(value)
        }
    }

    fun setDynamicThemeValue(value: Boolean) {
        screenModelScope.launch {
            repository.saveDynamicColor(value)
        }
    }

    fun setBlackThemeValue(value: Boolean) {
        screenModelScope.launch {
            repository.saveBlackTheme(value)
        }
    }

    fun setColumnsInRowValue(value: Int) {
        screenModelScope.launch {
            repository.saveCountColumnsInRow(value)
        }
    }

    fun setColumnsInRowValueLandscape(value: Int) {
        screenModelScope.launch {
            repository.saveCountColumnsInRowLandscape(value)
        }
    }
}

sealed interface SettingsUIState {
    data object Loading : SettingsUIState
    data class Success(val settings: AppSettings) : SettingsUIState
}