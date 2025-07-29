package id.anison.amanogawa.feature.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.common.SingleEventHandler
import id.anison.amanogawa.core.data.settings.SettingsRepo
import id.anison.amanogawa.feature.profile.screen.AboutUsScreen
import id.anison.amanogawa.feature.profile.screen.AppearanceScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val settingsRepo: SettingsRepo,
    private val log: Logger,
) : ScreenModel {
    private val singleEventHandler = SingleEventHandler()
    private val _navigateEventState = MutableSharedFlow<SettingsNavigationEvent>()
    val navigateEventState = _navigateEventState.asSharedFlow()


    fun onEvents(event: SettingsScreenEvent) {
        log.d(TAG, "current event is: $event")
        when (event) {
            is SettingsScreenEvent.PopBack -> {
                if (singleEventHandler.canTrigger()){
                    screenModelScope.launch {
                        _navigateEventState.emit(
                            value = SettingsNavigationEvent.NavigateToBack
                        )
                    }
                }
            }

            is SettingsScreenEvent.PushAppearance -> {
                if (singleEventHandler.canTrigger()){
                    screenModelScope.launch {
                        _navigateEventState.emit(
                            value = SettingsNavigationEvent.NavigateToAppearance
                        )
                    }
                }
            }

            is SettingsScreenEvent.PushAboutUs -> {
                if (singleEventHandler.canTrigger()){
                    screenModelScope.launch {
                        _navigateEventState.emit(
                            value = SettingsNavigationEvent.NavigateToAboutUs
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "SettingsSM"
    }

}



sealed interface SettingsScreenEvent {
    object PopBack: SettingsScreenEvent
    object PushAppearance: SettingsScreenEvent
    object PushAboutUs: SettingsScreenEvent
}

sealed interface SettingsNavigationEvent {
    object NavigateToBack: SettingsNavigationEvent
    object NavigateToAppearance: SettingsNavigationEvent
    object NavigateToAboutUs: SettingsNavigationEvent
}

internal fun SettingsNavigationEvent.toScreen() = when(this) {
    SettingsNavigationEvent.NavigateToBack -> null
    SettingsNavigationEvent.NavigateToAboutUs -> AboutUsScreen()
    SettingsNavigationEvent.NavigateToAppearance -> AppearanceScreen()
}