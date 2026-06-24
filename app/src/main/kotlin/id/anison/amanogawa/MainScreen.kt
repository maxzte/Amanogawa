package id.anison.amanogawa

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.LayoutDirection.Rtl
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.LineSide
import id.anison.amanogawa.core.ui.common.grayLine
import id.anison.amanogawa.tab.AmanogawaTab
import id.anison.amanogawa.tab.AmanogawaTabIcon
import id.anison.amanogawa.tab.HomeTab
import id.anison.amanogawa.tab.ProfileTab
import id.anison.amanogawa.tab.SearchTab


class MainScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val screenModel: MainScreenModel = koinScreenModel()
        val isOnline by screenModel.isOnline.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        val isBottomBarVisible by screenModel.isBottomBarVisible.collectAsState()
        val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val isCompact = !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
        val isExpanded = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

        val notConnectedMessage = stringResource(id = R.string.not_connected)

        LaunchedEffect(isOnline) {
            if (!isOnline) {
                snackbarHostState.showSnackbar(
                    message = notConnectedMessage,
                    duration = Indefinite,
                )
            }
        }

        TabNavigator(
            tab = HomeTab,
        ) { tabNavigator ->
            val activity = LocalActivity.current

            BackHandler {
                val previousTab = screenModel.popFromTabHistory()
                if (previousTab != null) {
                    tabNavigator.current = previousTab
                } else {
                    activity?.moveTaskToBack(true)
                }
            }

            Scaffold(
                bottomBar = {
                    if (isCompact) {
                        AnimatedVisibility(
                            visible = isBottomBarVisible,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            // TODO FlexibleBottBar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(AmanogawaConst.barHeight)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                                ),
                                            )
                                        )
                                        .border(
                                            width = Dp.Hairline,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                                            shape = CircleShape
                                        )
                                        .blur(24.dp)
                                        .shadow(
                                            elevation = 10.dp,
                                            shape = CircleShape,
                                            ambientColor = Color.Black.copy(alpha = 0.1f),
                                            spotColor = Color.Black.copy(alpha = 0.1f)
                                        )
                                )
                                BottomAppBar(
                                    content = {
                                        TabNavigationItem(tab = HomeTab)
                                        TabNavigationItem(tab = SearchTab)
                                        TabNavigationItem(tab = ProfileTab)
                                    },
                                    windowInsets = WindowInsets(0, 0, 0, 0),
                                    contentPadding = PaddingValues(
                                        vertical = 4.dp,
                                        horizontal = 4.dp
                                    ),
                                    modifier = Modifier.fillMaxSize(),
                                    containerColor = Color.Transparent,
                                    tonalElevation = 10.dp,
                                )

                            }
                        }
                    }
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
                    )
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                modifier = Modifier.fillMaxSize()
            ) { padding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            paddingValues = PaddingValues(
                                start = padding.calculateLeftPadding(layoutDirection = Ltr),
                                top = padding.calculateTopPadding(),
                                end = padding.calculateRightPadding(layoutDirection = Rtl),
                                bottom = 0.dp,
                            )
                        )
                        .consumeWindowInsets(padding)
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                ) {
                    if (!isCompact) {
                        NavigationRail(
                            header = {
                                IconButton(
                                    onClick = {},
                                    content = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.amanogawa_logo),
                                            contentDescription = stringResource(id = R.string.amanogawa),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                )
                            },
                            content = {
                                RailNavigationItem(HomeTab)
                                RailNavigationItem(SearchTab)
                                RailNavigationItem(ProfileTab)
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .widthIn(AmanogawaConst.barHeight)
                                .grayLine(side = LineSide.Right),
                            containerColor = Color.Transparent,
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CurrentTab()
                    }
                }
            }
        }
    }

    @Composable
    private fun RowScope.TabNavigationItem(
        tab: AmanogawaTab,
        tabNavigator: TabNavigator = LocalTabNavigator.current
    ) {
        val screenModel = tab.koinScreenModel<MainScreenModel>()
        val currentTab = tabNavigator.current
        val isSelected = currentTab == tab
        val isRoot by screenModel.isRoot.collectAsState()

        val defaultIcon = tab.amanogawaTabOptions.unselectedIcon
        val icon = if (isSelected) tab.amanogawaTabOptions.selectedIcon else defaultIcon
        val interactionSource = remember { MutableInteractionSource() }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = false, radius = 35.dp)
                ) {
                    when {
                        isSelected && isRoot -> {
                            Log.d("TabNavItem", "ROOT → Scroll to top")
                            screenModel.triggerScrollToTop()
                        }

                        isSelected && !isRoot -> {
                            Log.d("TabNavItem", "!ROOT")
                            screenModel.triggerNavToRoot()
                        }

                        else -> {
                            tabNavigator.current = tab
                            screenModel.pushTabToHistory(tab)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (icon != null) {
                    AmanogawaTabIcon(
                        icon = icon,
                        contentDescription = tab.options.title,
                    )
                }

                Text(
                    text = tab.options.title,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }

    @Composable
    private fun RailNavigationItem(
        tab: AmanogawaTab,
        tabNavigator: TabNavigator = LocalTabNavigator.current,
    ) {
        val screenModel = tab.koinScreenModel<MainScreenModel>()
        val isRoot by screenModel.isRoot.collectAsState()
        val currentTab = tabNavigator.current
        val isSelected = currentTab == tab

        val defaultIcon = tab.amanogawaTabOptions.unselectedIcon
        val icon = if (isSelected) tab.amanogawaTabOptions.selectedIcon else defaultIcon

        NavigationRailItem(
            selected = tabNavigator.current.key == tab.key,
            onClick = {
                when {
                    isSelected && isRoot -> {
                        Log.d("RailNavItem", "ROOT → Scroll to top")
                        screenModel.triggerScrollToTop()
                    }

                    isSelected && !isRoot -> {
                        Log.d("RailNavItem", "!ROOT")
                        screenModel.triggerNavToRoot()
                    }

                    else -> {
                        tabNavigator.current = tab
                        screenModel.pushTabToHistory(tab)
                    }
                }
            },
            icon = {
                if (icon != null) {
                    AmanogawaTabIcon(
                        icon = icon,
                        contentDescription = tab.options.title,
                    )
                }
            },
            label = {
                Text(
                    text = tab.options.title,
                    style = MaterialTheme.typography.labelSmall,
                )
            },
        )
    }

}
