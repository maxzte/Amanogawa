package id.anison.feature.common

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenModel(private val repository: SettingsRepository) :
    StateScreenModel<MainState>(MainState()) {
    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    val appSettings: StateFlow<MainUIState> = repository.settings
        .map {
            MainUIState.Success(
                settings = AppSettings(
                    theme = it.theme,
                    isDynamicColors = it.isDynamicColors,
                    isBlackTheme = it.isBlackTheme,
                )
            )
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = MainUIState.Loading,
        )

    private fun loadSettings() {
        screenModelScope.launch {
            repository.settings.collect { settings ->
                _mainState.update { currentState ->
                    currentState.copy(
                        theme = settings.theme,
                        isBlackTheme = settings.isBlackTheme,
                        isDynamicColors = settings.isDynamicColors,
                        isLoading = false
                    )
                }
            }
        }
    }
}

data class MainState (
    val isLoading: Boolean = true,
    val theme: AppTheme? = null,//AppTheme.FOLLOW_SYSTEM,
    val isBlackTheme: Boolean? = null,//false,
    val isDynamicColors: Boolean? = null,//false
)

sealed interface MainUIState {
    data object Loading : MainUIState
    data class Success(val settings: AppSettings) : MainUIState
}
