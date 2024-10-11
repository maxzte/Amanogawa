package id.anison.feature.tab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import id.anison.feature.common.MainScreenModel
import id.anison.feature.common.component.CustomNavigationBar
import id.anison.feature.common.component.CustomNavigationRail

class MainScreen : Screen {
    @Composable
    override fun Content() {
        val sm = koinScreenModel<MainScreenModel>()
        val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val showNavigationBar = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
        TabNavigator(
            tab = HomeTab,
            key = "tabNavigator"
        ) {
            Scaffold(
                bottomBar = { if (showNavigationBar) CustomNavigationBar(sm) },
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                modifier = Modifier.fillMaxSize()
                //bullshit
                //.windowInsetsPadding(WindowInsets.statusBars)
                //.windowInsetsPadding(WindowInsets.navigationBars)
            ) { paddingValues ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (!showNavigationBar) CustomNavigationRail(sm)
                    Box(modifier = Modifier.fillMaxSize()) {
                        CurrentTab()
                    }
                }
            }
        }
    }
}