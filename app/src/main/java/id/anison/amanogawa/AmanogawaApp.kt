package id.anison.amanogawa

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import id.anison.feature.tab.FavoritesTab
import id.anison.feature.tab.HomeTab
import id.anison.feature.tab.ProfileTab
import id.anison.feature.tab.SearchTab
import id.anison.feature.ui.AmanogawaBackground
import id.anison.feature.ui.AmanogawaTheme

@Composable
fun AmanogawaApp(windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass) {
    val showNavigationBar = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    TabNavigator(
        tab = HomeTab,
        tabDisposable = {
            TabDisposable(
                navigator = it,
                tabs = listOf(HomeTab, SearchTab, FavoritesTab, ProfileTab)
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                if (showNavigationBar) {
                    NavigationBar(
                        modifier = Modifier.height(80.dp),
                    ) {
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(SearchTab)
                        TabNavigationItem(FavoritesTab)
                        TabNavigationItem(ProfileTab)
                    }
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier.fillMaxSize()
            //bullshit
            //.windowInsetsPadding(WindowInsets.statusBars)
            //.windowInsetsPadding(WindowInsets.navigationBars)
        ) { paddingValues ->
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (!showNavigationBar) {
                    NavigationRail(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(max = 64.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .widthIn(max = 64.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            NavRailItem(HomeTab)
                            NavRailItem(SearchTab)
                            NavRailItem(FavoritesTab)
                            NavRailItem(ProfileTab)
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    CurrentTab()
                }
            }
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val isSelected = tabNavigator.current == tab
    val isFavoritesTab = tab.options.index == 2u.toUShort()

    val selectedColor = when {
        isSelected && isFavoritesTab -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary
        else -> LocalContentColor.current
    }
    NavigationBarItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = {
            Icon(
                painter = tab.options.icon!!,
                contentDescription = tab.options.title,
                tint = selectedColor,
            )
        },
        label = {
            Text(
                text = tab.options.title,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp
            )
        },
    )
}

@Composable
private fun NavRailItem(
    tab: Tab,
    tabNavigator: TabNavigator = LocalTabNavigator.current,
) {
    val isSelected = tabNavigator.current == tab
    val isFavoritesTab = tab.options.index == 2u.toUShort()
    val selectedColor = when {
        isSelected && isFavoritesTab -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary
        else -> LocalContentColor.current
    }
    NavigationRailItem(
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = {
            Icon(
                painter = tab.options.icon!!,
                contentDescription = tab.options.title,
                tint = selectedColor,
            )
        },
        label = {
            Text(
                text = tab.options.title,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp
            )
        },
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light theme")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark theme")
annotation class ThemePreviews

@ThemePreviews
@Composable
fun GradientBackgroundDefault() {
    AmanogawaTheme(dynamicColors = true) {
        AmanogawaBackground(Modifier.size(100.dp), content = {})
    }
}

@ThemePreviews
@Composable
fun GradientBackgroundDynamic() {
    AmanogawaTheme(dynamicColors = false) {
        AmanogawaBackground(Modifier.size(100.dp), content = {})
    }
}