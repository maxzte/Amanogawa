package id.anison.amanogawa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.core.registry.ScreenRegistry
import id.anison.amanogawa.core.domain.model.AppTheme
import id.anison.amanogawa.core.ui.AmanogawaTheme
import id.anison.amanogawa.feature.anime.animeDetailsScreenModule
import id.anison.amanogawa.feature.favorite.favoriteScreenModule
import id.anison.amanogawa.feature.search.navigation.searchScreenModule
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
                        isDarkTheme = darkTheme,
                        isBlackTheme = state.settings.isBlackTheme,
                        isDynamicColors = state.settings.isDynamicColors
                    ) {
                        MainScreen().Content()
                    }
                }
            }
        }
    }
}
