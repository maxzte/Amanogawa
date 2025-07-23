package id.anison.amanogawa.feature.profile

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.common.SingleEventHandler
import id.anison.amanogawa.core.data.settings.AppSettings
import id.anison.amanogawa.core.data.settings.AppTheme
import id.anison.amanogawa.core.data.settings.SettingsRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppearanceScreenModel(
    private val settingsRepo: SettingsRepo,
    private val log: Logger,
) : StateScreenModel<AppearanceSettingsUIState>(AppearanceSettingsUIState.Loading) {
    private val singleEventHandler = SingleEventHandler()
    private val _navigationEvent = MutableSharedFlow<AppearanceNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()
    val appSettings: StateFlow<AppearanceSettingsUIState> = settingsRepo.settings
        .map { settings ->
            AppearanceSettingsUIState.Success(
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
            initialValue = AppearanceSettingsUIState.Loading
        )

    fun onEvents(event: AppearanceScreenEvent) {
        log.d(TAG, "current event is: $event")
        when (event) {
            is AppearanceScreenEvent.PopBack -> {
                if (singleEventHandler.canTrigger()) {
                    screenModelScope.launch {
                        _navigationEvent.emit(value = AppearanceNavigationEvent.NavigateToBack)
                    }
                }
            }

            is AppearanceScreenEvent.SaveTheme -> {
                if (singleEventHandler.canTrigger()) {
                    setThemeValue(event.theme)
                }
            }

            is AppearanceScreenEvent.SaveDynamicColors -> {
                if (singleEventHandler.canTrigger()) {
                    setDynamicThemeValue(event.isDynamicColors)
                }
            }

            is AppearanceScreenEvent.SaveBlackTheme -> {
                if (singleEventHandler.canTrigger()) {
                    setBlackThemeValue(event.isBlackTheme)
                }
            }

            is AppearanceScreenEvent.SaveColumnsInRow -> {
                if (singleEventHandler.canTrigger()) {
                    setColumnsInRowValue(event.columns)
                }
            }

            is AppearanceScreenEvent.SaveColumnsInRowLandscape -> {
                if (singleEventHandler.canTrigger()) {
                    setColumnsInRowValueLandscape(event.columns)
                }
            }

            is AppearanceScreenEvent.SaveEpisodesInRow -> {
                if (singleEventHandler.canTrigger()) {
                    setEpisodesInRowValue(event.columns)
                }
            }

            is AppearanceScreenEvent.SaveEpisodesInRowLandscape -> {
                if (singleEventHandler.canTrigger()) {
                    setEpisodesInRowValueLandscape(event.columns)
                }
            }
        }
    }

    private fun setThemeValue(value: AppTheme) = updateSettings { it.saveTheme(value) }

    private fun setDynamicThemeValue(value: Boolean) = updateSettings { it.saveDynamicColor(value) }

    private fun setBlackThemeValue(value: Boolean) = updateSettings { it.saveBlackTheme(value) }

    private fun setColumnsInRowValue(value: Int) =
        updateSettings { it.saveCountColumnsInRow(value) }

    private fun setColumnsInRowValueLandscape(value: Int) =
        updateSettings { it.saveCountColumnsInRowLandscape(value) }

    private fun setEpisodesInRowValue(value: Int) =
        updateSettings { it.saveCountEpisodesInRow(value) }

    private fun setEpisodesInRowValueLandscape(value: Int) =
        updateSettings { it.saveCountEpisodesInRowLandscape(value) }

    private fun updateSettings(updateAction: suspend (SettingsRepo) -> Unit) {
        screenModelScope.launch {
            updateAction(settingsRepo)
        }
    }

    companion object {
        private const val TAG = "AppearanceSM"
    }

}

sealed interface AppearanceSettingsUIState {
    object Loading : AppearanceSettingsUIState
    data class Success(val settings: AppSettings) : AppearanceSettingsUIState
}

sealed interface AppearanceScreenEvent {
    object PopBack : AppearanceScreenEvent
    data class SaveTheme(val theme: AppTheme) : AppearanceScreenEvent
    data class SaveDynamicColors(val isDynamicColors: Boolean) : AppearanceScreenEvent
    data class SaveBlackTheme(val isBlackTheme: Boolean) : AppearanceScreenEvent
    data class SaveColumnsInRow(val columns: Int) : AppearanceScreenEvent
    data class SaveColumnsInRowLandscape(val columns: Int) : AppearanceScreenEvent
    data class SaveEpisodesInRow(val columns: Int) : AppearanceScreenEvent
    data class SaveEpisodesInRowLandscape(val columns: Int) : AppearanceScreenEvent
}

sealed interface AppearanceNavigationEvent {
    object NavigateToBack : AppearanceNavigationEvent
}
