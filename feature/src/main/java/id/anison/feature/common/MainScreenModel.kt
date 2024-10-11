package id.anison.feature.common

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.model.AppSettings
import id.anison.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainScreenModel(repository: SettingsRepository) :
    StateScreenModel<MainUIState>(MainUIState.Loading) {
    private val _toRoute = MutableStateFlow(false)
    val toRoute: StateFlow<Boolean> get() = _toRoute

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

    fun setToRoute(value: Boolean) {
        _toRoute.value = value
    }
}

sealed interface MainUIState {
    data object Loading : MainUIState
    data class Success(val settings: AppSettings) : MainUIState
}
