package id.anison.amanogawa.feature.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.navigation.SharedScreen
import id.anison.amanogawa.feature.profile.screen.SettingsScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class ProfileScreenModel(
    private val log: Logger,
) : ScreenModel {

    private val _navEventState = MutableSharedFlow<ProfileNavigationEvent>()
    val navEventState = _navEventState.asSharedFlow()

    fun onEvents(event: ProfileNavigationEvent) {
        log.d(TAG, "current event is: $event")
        when(event) {
            ProfileNavigationEvent.NavigateToFavorites -> {
                screenModelScope.launch {
                    _navEventState.emit(value = ProfileNavigationEvent.NavigateToFavorites)
                }
            }

            ProfileNavigationEvent.NavigateToSettings -> {
                screenModelScope.launch {
                    _navEventState.emit(value = ProfileNavigationEvent.NavigateToSettings)
                }
            }
        }
    }

    companion object {
        private const val TAG = "ProfileSM"
    }

}

sealed interface ProfileNavigationEvent {
    data object NavigateToFavorites : ProfileNavigationEvent
    data object NavigateToSettings : ProfileNavigationEvent
}

internal fun ProfileNavigationEvent.toScreen() = when(this) {
    ProfileNavigationEvent.NavigateToFavorites -> ScreenRegistry.get(SharedScreen.Favorites)
    ProfileNavigationEvent.NavigateToSettings -> SettingsScreen()
}