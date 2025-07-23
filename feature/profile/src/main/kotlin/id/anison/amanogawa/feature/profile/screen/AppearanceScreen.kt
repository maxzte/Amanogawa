package id.anison.amanogawa.feature.profile.screen

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.amanogawa.core.data.settings.AppSettings
import id.anison.amanogawa.core.data.settings.AppTheme
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.component.AmanogawaSpinner
import id.anison.amanogawa.core.ui.common.component.CustomListItemSwitch
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.feature.profile.AppearanceNavigationEvent
import id.anison.amanogawa.feature.profile.AppearanceScreenEvent
import id.anison.amanogawa.feature.profile.AppearanceScreenModel
import id.anison.amanogawa.feature.profile.AppearanceSettingsUIState

class AppearanceScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<AppearanceScreenModel>()
        val appSettings by screenModel.appSettings.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.navigationEvent.collect { event ->
                if (event is AppearanceNavigationEvent.NavigateToBack) {
                    navigator.pop()
                }
            }
        }

        when (appSettings) {
            is AppearanceSettingsUIState.Loading -> {
                AmanogawaSpinner()
            }

            is AppearanceSettingsUIState.Success -> {
                AppearanceContent(
                    appSettings = (appSettings as AppearanceSettingsUIState.Success).settings,
                    onEvents = { screenModel.onEvents(it) }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AppearanceContent(
        appSettings: AppSettings,
        onEvents: (AppearanceScreenEvent) -> Unit,
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = isLandscape()
        val columns =
            if (isLandscape) appSettings.countColumnsInRowLandscape else appSettings.countColumnsInRow
        val episodes =
            if (isLandscape) appSettings.countEpisodesInRowLandscape else appSettings.countEpisodesInRow

        val widthDp = configuration.screenWidthDp.dp
        val itemWidth = (widthDp - if (isLandscape) 64.dp else 16.dp) / columns
        val itemHeight = itemWidth * (10f / 7f)

        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    title = stringResource(id = R.string.appearance),
                    navigationIconContent = {
                        ArrowBackButton(onClick = { onEvents(AppearanceScreenEvent.PopBack) })
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    top = paddingValues.calculateTopPadding() + 8.dp,
                    end = 8.dp,
                    bottom = paddingValues.calculateBottomPadding() + 8.dp,
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    CustomListItemSwitch(
                        text = stringResource(id = R.string.black_theme),
                        checked = appSettings.isBlackTheme,
                        onClick = { onEvents(AppearanceScreenEvent.SaveBlackTheme(!appSettings.isBlackTheme)) }
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    item {
                        CustomListItemSwitch(
                            text = stringResource(id = R.string.dynamic_colors),
                            supportingContent = stringResource(id = R.string.colors_based_on_wallpapers),
                            checked = appSettings.isDynamicColors,
                            onClick = { onEvents(AppearanceScreenEvent.SaveDynamicColors(!appSettings.isDynamicColors)) }
                        )
                    }
                }

                item {
                    AppThemeColumn(theme = appSettings.theme, onEvents = onEvents)
                }

                item {
                    ColumnSettingsSection(
                        title = R.string.columns_in_row,
                        options = if (isLandscape) listOf(5, 6) else listOf(2, 3),
                        selected = columns,
                        onSelect = { value ->
                            val event = if (isLandscape)
                                AppearanceScreenEvent.SaveColumnsInRowLandscape(value)
                            else
                                AppearanceScreenEvent.SaveColumnsInRow(value)
                            onEvents(event)
                        }
                    )
                }

                item {
                    AnimatedContent(
                        targetState = columns,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { -it } togetherWith (slideOutHorizontally { it } + fadeOut())
                            } else {
                                slideInHorizontally { it } togetherWith (slideOutHorizontally { -it } + fadeOut())
                            }
                        },
                        label = "cardAnimation"
                    ) { target ->
                        ExampleCardView(target, itemWidth, itemHeight)
                    }
                }

                item {
                    ColumnSettingsSection(
                        title = R.string.episodes_in_row,
                        options = if (isLandscape) listOf(3, 4) else listOf(1, 2),
                        selected = episodes,
                        onSelect = { value ->
                            val event = if (isLandscape) {
                                AppearanceScreenEvent.SaveEpisodesInRowLandscape(value)
                            } else {
                                AppearanceScreenEvent.SaveEpisodesInRow(value)
                            }
                            onEvents(event)
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun ColumnSettingsSection(
        @StringRes title: Int,
        options: List<Int>,
        selected: Int,
        onSelect: (Int) -> Unit
    ) {
        Text(text = stringResource(id = title), modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12))
        ) {
            options.forEachIndexed { index, option ->
                if (index > 0) HorizontalDivider()
                ListItem(
                    headlineContent = {
                        Text(
                            text = when (option) {
                                1 -> stringResource(id = R.string.one)
                                2 -> stringResource(id = R.string.two)
                                3 -> stringResource(id = R.string.three)
                                4 -> stringResource(id = R.string.four)
                                5 -> stringResource(id = R.string.five)
                                else -> stringResource(id = R.string.six)
                            }
                        )
                    },
                    trailingContent = {
                        if (selected == option) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        } else null
                    },
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                onSelect(option)
                            }
                        )
                )
            }
        }
    }

    @Composable
    private fun AppThemeColumn(
        theme: AppTheme,
        onEvents: (AppearanceScreenEvent) -> Unit,
    ) {
        Text(
            text = stringResource(id = R.string.app_theme),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        val themes = listOf(
            ThemeOption(AppTheme.LIGHT, R.string.light, R.drawable.ic_rounded_light_mode_24),
            ThemeOption(AppTheme.DARK, R.string.dark, R.drawable.ic_rounded_dark_mode_24),
            ThemeOption(
                AppTheme.FOLLOW_SYSTEM,
                R.string.dynamic,
                R.drawable.ic_rounded_brightness_medium_24
            ),
        )

        Column(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12))
        ) {
            themes.forEachIndexed { index, (themeObj, labelId, iconId) ->
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && themeObj == AppTheme.FOLLOW_SYSTEM) return@forEachIndexed
                if (index > 0) HorizontalDivider()
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(labelId))
                    },
                    leadingContent = {
                        Icon(painter = painterResource(id = iconId), contentDescription = null)
                    },
                    trailingContent = {
                        if (theme == themeObj) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        } else null
                    },
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            onEvents(AppearanceScreenEvent.SaveTheme(themeObj))
                        }
                    )
                )
            }
        }
    }

    @Composable
    private fun ExampleCardView(
        count: Int,
        itemWidth: Dp,
        itemHeight: Dp,
    ) {
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) {
            items(
                count = count
            ) {
                Column(
                    modifier = Modifier
                        .width(itemWidth)
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Card(Modifier.size(itemWidth, itemHeight)) {}
                    repeat(2) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (it == 0) 16.dp else 12.dp)
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .clip(RoundedCornerShape(8))
                        )
                    }
                }
            }
        }
    }
}

private fun horizontalSlideAndScale(
    targetState: Int,
    initialState: Int
): ContentTransform {
    val slideInOffset = { size: Int -> if (targetState > initialState) size else -size }
    val slideOutOffset = { size: Int -> if (targetState > initialState) -size else size }

    return (slideInHorizontally(
        initialOffsetX = slideInOffset,
        animationSpec = tween(700, easing = LinearOutSlowInEasing)
    ) + scaleIn(animationSpec = tween(720, easing = LinearOutSlowInEasing)))
        .togetherWith(
            slideOutHorizontally(
                targetOffsetX = slideOutOffset,
                animationSpec = tween(700, easing = LinearOutSlowInEasing)
            )
        )
}


data class ThemeOption(
    val theme: AppTheme,
    @StringRes val labelId: Int,
    @DrawableRes val iconId: Int,
)
