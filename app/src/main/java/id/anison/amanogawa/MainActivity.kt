package id.anison.amanogawa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import id.anison.data.settings.AppTheme
import id.anison.feature.MainScreenModel
import id.anison.feature.MainUIState
import id.anison.feature.ui.AmanogawaTheme
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val mainScreenModel: MainScreenModel by inject()
        var uiState: MainUIState by mutableStateOf(MainUIState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainScreenModel.appSettings.collect {
                    uiState = it
                }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                is MainUIState.Loading -> true
                is MainUIState.Success -> false
            }
        }

        setContent {
            val appSettings by mainScreenModel.appSettings.collectAsState()
            when (appSettings) {
                is MainUIState.Loading -> null
                is MainUIState.Success -> {
                    val successState = appSettings as MainUIState.Success
                    val darkTheme = when (successState.settings.theme) {
                        AppTheme.DARK -> true
                        AppTheme.LIGHT -> false
                        AppTheme.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                    }
                    val isBlackTheme = successState.settings.isBlackTheme
                    val isDynamicColors = successState.settings.isDynamicColors
                    KoinContext() {
                        AmanogawaTheme(
                            darkTheme = darkTheme,
                            isBlackTheme = isBlackTheme,
                            dynamicColors = isDynamicColors,
                        ) {
                            AmanogawaApp()
                        }
                    }
                }
            }
        }
    }
}