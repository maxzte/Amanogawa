package id.anison.feature.profile

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.settings.SettingsRepo
import id.anison.data.settings.AppSettings
import id.anison.data.settings.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val repository: SettingsRepo
) : StateScreenModel<SettingsUIState>(SettingsUIState.Loading) {

    val appSettings: StateFlow<SettingsUIState> = repository.settings
        .map { settings ->
            SettingsUIState.Success(
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
            initialValue = SettingsUIState.Loading
        )

    private fun updateSettings(updateAction: suspend (SettingsRepo) -> Unit) {
        screenModelScope.launch {
            updateAction(repository)
        }
    }

    fun setThemeValue(value: AppTheme) = updateSettings { it.saveTheme(value) }

    fun setDynamicThemeValue(value: Boolean) = updateSettings { it.saveDynamicColor(value) }

    fun setBlackThemeValue(value: Boolean) = updateSettings { it.saveBlackTheme(value) }

    fun setColumnsInRowValue(value: Int) = updateSettings { it.saveCountColumnsInRow(value) }

    fun setColumnsInRowValueLandscape(value: Int) =
        updateSettings { it.saveCountColumnsInRowLandscape(value) }
}

sealed interface SettingsUIState {
    data object Loading : SettingsUIState
    data class Success(val settings: AppSettings) : SettingsUIState
}
