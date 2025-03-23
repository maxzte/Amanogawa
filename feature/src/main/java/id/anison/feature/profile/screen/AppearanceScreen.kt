package id.anison.feature.profile.screen

import android.content.res.Configuration
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import id.anison.data.settings.AppSettings
import id.anison.data.settings.AppTheme
import id.anison.feature.R
import id.anison.feature.common.component.CustomListItem
import id.anison.feature.common.component.CustomListItemSwitch
import id.anison.feature.common.component.layout.AmanogawaTopBar
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.profile.SettingsScreenModel
import id.anison.feature.profile.SettingsUIState
import id.anison.feature.profile.event.AppearanceScreenEvents

class AppearanceScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<SettingsScreenModel>()
        val appSettings by screenModel.appSettings.collectAsState()

        when (appSettings) {
            is SettingsUIState.Loading -> {
                ProgressIndicator()
            }

            is SettingsUIState.Success -> {
                AppearanceContent(
                    appSettings = (appSettings as SettingsUIState.Success).settings,
                    onEvents = { event ->
                        when (event) {
                            is AppearanceScreenEvents.PopBack -> {
                                navigator.pop()
                            }

                            is AppearanceScreenEvents.SaveTheme -> {
                                screenModel.setThemeValue(event.theme)
                            }

                            is AppearanceScreenEvents.SaveDynamicColors -> {
                                screenModel.setDynamicThemeValue(event.isDynamicColors)
                            }

                            is AppearanceScreenEvents.SaveBlackTheme -> {
                                screenModel.setBlackThemeValue(event.isBlackTheme)
                            }

                            is AppearanceScreenEvents.SaveColumnsInRow -> {
                                screenModel.setColumnsInRowValue(event.columns)
                            }

                            is AppearanceScreenEvents.SaveColumnsInRowLandscape -> {
                                screenModel.setColumnsInRowValueLandscape(event.columns)
                            }
                        }
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppearanceContent(
        appSettings: AppSettings,
        onEvents: (AppearanceScreenEvents) -> Unit,
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val count = if (!isLandscape) appSettings.countColumnsInRow
        else appSettings.countColumnsInRowLandscape
        val itemWidth = if (!isLandscape) (configuration.screenWidthDp.dp - 16.dp) / count
        else (configuration.screenWidthDp.dp - 64.dp) / count
        val itemHeight = itemWidth * (10f / 7f)
        val isBlackTheme = appSettings.isBlackTheme
        val isDynamicColors = appSettings.isDynamicColors
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
        ) {
            AmanogawaTopBar(
                title = stringResource(id = R.string.appearance),
                popNavigate = { onEvents(AppearanceScreenEvents.PopBack) },
                scrollBehavior = scrollBehavior,
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                item { }
                item {
                    CustomListItemSwitch(
                        text = stringResource(R.string.black_theme),
                        checked = isBlackTheme,
                        onClick = { onEvents(AppearanceScreenEvents.SaveBlackTheme(!isBlackTheme)) },
                    )
                }
                item {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        CustomListItemSwitch(
                            text = stringResource(R.string.dynamic_colors),
                            supportingContent = stringResource(id = R.string.colors_based_on_wallpapers),
                            checked = isDynamicColors,
                            onClick = {
                                onEvents(AppearanceScreenEvents.SaveDynamicColors(!isDynamicColors))
                            },
                        )
                    }
                }
                item {
                    AppThemeColumn(theme = appSettings.theme, onEvents = onEvents)
                }
                item {
                    ColumnsInRowColumn(
                        count = count,
                        onEvents = onEvents,
                        isLandscape = isLandscape
                    )
                }
                item {
                    AnimatedContent(
                        targetState = count,
                        transitionSpec = {
//                            horizontalSlideAndScale(targetState, initialState)
                            slideInHorizontally() togetherWith slideOutHorizontally()
                        },
                        label = "Animation for example columns in row"
                    ) { targetCount ->
                        ExampleCardView(targetCount, itemWidth, itemHeight)
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun ColumnSelectionItem(
        count: Int,
        columns: Int,
        onSelect: (Int) -> Unit
    ) {
        val isChecked = count == columns
        CustomListItem(
            text = if (columns == 2) stringResource(id = R.string.two) else stringResource(id = R.string.three),
            trailingIcon = if (isChecked) Icons.Default.Check else null,
            onClick = { onSelect(columns) },
        )
    }

    @Composable
    private fun ColumnSelectionItemInLandscape(
        countLandscape: Int,
        columns: Int,
        onSelect: (Int) -> Unit
    ) {
        val isChecked = countLandscape == columns
        CustomListItem(
            text = if (columns == 5) stringResource(id = R.string.five) else stringResource(id = R.string.six),
            trailingIcon = if (isChecked) Icons.Default.Check else null,
            onClick = { onSelect(columns) },
        )
    }

    @Composable
    private fun ExampleCardView(
        count: Int,
        itemWidth: Dp,
        itemHeight: Dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            repeat(
                times = count,
                action = {
                    Column(
                        modifier = Modifier
                            .width(itemWidth)
                            .padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Card(
                            modifier = Modifier
                                .width(itemWidth)
                                .height(itemHeight)
                        ) { }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .clip(RoundedCornerShape(8))
                                .height(16.dp)
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .clip(RoundedCornerShape(8))
                                .height(12.dp)
                        )
                    }
                }
            )
        }
    }

    @Composable
    private fun AppThemeColumn(
        theme: AppTheme,
        onEvents: (AppearanceScreenEvents) -> Unit,
    ) {
        Text(
            text = stringResource(R.string.app_theme),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(12)
                )
        ) {
            CustomListItem(
                text = stringResource(R.string.light),
                leadingIcon = painterResource(id = R.drawable.ic_rounded_light_mode_24),
                leadingIconDesc = stringResource(R.string.light_icon),
                trailingIcon = if (theme == AppTheme.LIGHT) Icons.Default.Check else null,
                trailingIconDesc = stringResource(R.string.check),
                onClick = { onEvents(AppearanceScreenEvents.SaveTheme(AppTheme.LIGHT)) }
            )
            HorizontalDivider()
            CustomListItem(
                text = stringResource(R.string.dark),
                leadingIcon = painterResource(id = R.drawable.ic_rounded_dark_mode_24),
                leadingIconDesc = stringResource(id = R.string.dark_icon),
                trailingIcon = if (theme == AppTheme.DARK) Icons.Default.Check else null,
                trailingIconDesc = stringResource(R.string.check),
                onClick = { onEvents(AppearanceScreenEvents.SaveTheme(AppTheme.DARK)) }
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                HorizontalDivider()
                CustomListItem(
                    text = stringResource(R.string.dynamic),
                    leadingIcon = painterResource(id = R.drawable.ic_rounded_brightness_medium_24),
                    leadingIconDesc = stringResource(id = R.string.dynamic_theme_icon),
                    trailingIcon = if (theme == AppTheme.FOLLOW_SYSTEM) Icons.Default.Check else null,
                    trailingIconDesc = stringResource(R.string.check),
                    onClick = { onEvents(AppearanceScreenEvents.SaveTheme(AppTheme.FOLLOW_SYSTEM)) }
                )
            }
        }
    }

    @Composable
    private fun ColumnsInRowColumn(
        count: Int,
        onEvents: (AppearanceScreenEvents) -> Unit,
        isLandscape: Boolean,
    ) {
        Text(
            text = stringResource(id = R.string.columns_in_row),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(12)
                )
        ) {
            if (!isLandscape) {
                ColumnSelectionItem(
                    count = count,
                    columns = 2,
                    onSelect = {
                        onEvents(AppearanceScreenEvents.SaveColumnsInRow(2))
                    }
                )
                HorizontalDivider()
                ColumnSelectionItem(
                    count = count,
                    columns = 3,
                    onSelect = {
                        onEvents(AppearanceScreenEvents.SaveColumnsInRow(3))
                    }
                )
            } else {
                ColumnSelectionItemInLandscape(
                    countLandscape = count,
                    columns = 5,
                    onSelect = {
                        onEvents(AppearanceScreenEvents.SaveColumnsInRowLandscape(5))
                    }
                )
                HorizontalDivider()
                ColumnSelectionItemInLandscape(
                    countLandscape = count,
                    columns = 6,
                    onSelect = {
                        onEvents(AppearanceScreenEvents.SaveColumnsInRowLandscape(6))
                    }
                )
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
}



