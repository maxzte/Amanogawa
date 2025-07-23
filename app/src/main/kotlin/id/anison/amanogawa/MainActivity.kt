package id.anison.amanogawa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.amanogawa.core.data.settings.AppTheme
import id.anison.amanogawa.core.model.AppSettingsUI
import id.anison.amanogawa.core.ui.AmanogawaTheme
import id.anison.amanogawa.core.ui.common.LocalAppSettings
import id.anison.amanogawa.feature.anime.animeDetailsScreenModule
import id.anison.amanogawa.feature.favorite.favoriteScreenModule
import id.anison.amanogawa.feature.search.navigation.searchScreenModule
import id.anison.amanogawa.tab.HomeTab
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val mainScreenModel: MainScreenModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        ScreenRegistry {
            animeDetailsScreenModule()
            favoriteScreenModule()
            searchScreenModule()
        }

        splashScreen.setKeepOnScreenCondition { mainScreenModel.state.value is MainUIState.Loading }

        setContent {
            val uiState by mainScreenModel.appSettings.collectAsState()

            LaunchedEffect(uiState) {
                splashScreen.setKeepOnScreenCondition {
                    uiState is MainUIState.Loading
                }
            }

            when (val state = uiState) {
                is MainUIState.Loading -> {}
                is MainUIState.Success -> {
                    val darkTheme = when (state.settings.theme) {
                        AppTheme.DARK -> true
                        AppTheme.LIGHT -> false
                        AppTheme.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                    }

                    AmanogawaTheme(
                        darkTheme = darkTheme,
                        isBlackTheme = state.settings.isBlackTheme,
                        dynamicColors = state.settings.isDynamicColors
                    ) {
                        CompositionLocalProvider(
                            value = LocalAppSettings provides AppSettingsUI(
                                countColumnsInRow = state.settings.countColumnsInRow,
                                countColumnsInRowLandscape = state.settings.countColumnsInRowLandscape,
                                countEpisodesInRow = state.settings.countEpisodesInRow,
                                countEpisodesInRowLandscape = state.settings.countEpisodesInRowLandscape,
                            )
                        ) {
                            MainScreen().Content()
                        }
                    }
                }
            }
        }
    }

}
