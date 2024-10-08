package id.anison.feature.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.data.model.AppSettings
import id.anison.feature.search.screenmodel.ExtendedSearchScreenModel
import id.anison.feature.profile.SettingsScreenModel
import id.anison.feature.profile.SettingsUIState
import id.anison.feature.search.screen.ExtendedListContent

/*
class TitlesByLinkScreen(private val filterName: String) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val screenModel = navigator.koinNavigatorScreenModel<ExtendedSearchScreenModel>()
        val state by screenModel.extendedSearchState.collectAsState()
        val settingsScreenModel = koinScreenModel<SettingsScreenModel>()
        val appSettingsState by settingsScreenModel.appSettings.collectAsState()
        ExtendedListContent(
            topBarTitle = filterName,
            navigator = navigator,
            state = state.titlesByLink,
            appSettings = when (appSettingsState) {
                is SettingsUIState.Loading -> AppSettings()
                is SettingsUIState.Success -> (appSettingsState as SettingsUIState.Success).settings
            },
        )
    }
}*/
