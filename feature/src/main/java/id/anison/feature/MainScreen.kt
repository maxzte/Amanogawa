package id.anison.feature

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import id.anison.feature.common.component.layout.AmanogawaBottomBar
import id.anison.feature.common.component.layout.AmanogawaNavigationRail
import id.anison.feature.common.component.layout.AmanogawaScaffold
import id.anison.feature.common.component.layout.AmanogawaTopBar
import id.anison.feature.tab.HomeTab

class MainScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val sm = koinScreenModel<MainScreenModel>()
        val isOnline by sm.isOnline.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val isTopBarVisible by sm.isTopBarVisible.collectAsState()
        val isBottomBarVisible by sm.isBottomBarVisible.collectAsState()

        LaunchedEffect(isOnline) {
            if (!isOnline) {
                snackbarHostState.showSnackbar(
                    message = "You aren`t connected to the internet",
                    duration = Indefinite,
                )
            }
        }

        TabNavigator(
            tab = HomeTab,
            key = "tabNavigator"
        ) {
            AmanogawaScaffold(
                isTopBarVisible = isTopBarVisible,
                isBottomBarVisible = isBottomBarVisible,
                topBar = {
                    AmanogawaTopBar(
                        title = "Amanogawa",
                        scrollBehavior = scrollBehavior,
                    )
                },
                railBar = {
                    AmanogawaNavigationRail(sm = sm)
                },
                bottomBar = {
                    AmanogawaBottomBar(sm = sm)
                },
                snackBar = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
                    )
                },
                content = {
                    CurrentTab()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}