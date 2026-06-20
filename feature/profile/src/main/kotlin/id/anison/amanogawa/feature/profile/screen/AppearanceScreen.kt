package id.anison.amanogawa.feature.profile.screen

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.amanogawa.core.domain.model.AppSettings
import id.anison.amanogawa.core.domain.model.AppTheme
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
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
    override val key: ScreenKey
        get() = "appearanceScreen"

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
        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    title = stringResource(id = R.string.appearance),
                    navigationIconContent = {
                        ArrowBackButton(onClick = { onEvents(AppearanceScreenEvent.PopBack) })
                    },
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
                    bottom = if (!isLandscape()) paddingValues.calculateBottomPadding() + AmanogawaConst.bottomBarInsets
                    else paddingValues.calculateBottomPadding()
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
            }
        }
    }

    @Composable
    private fun AppThemeColumn(
        theme: AppTheme,
        onEvents: (AppearanceScreenEvent) -> Unit,
    ) {
        val themes = listOf(
            ThemeOption(
                theme = AppTheme.LIGHT,
                labelId = R.string.light,
                iconId = R.drawable.ic_rounded_light_mode_24
            ),
            ThemeOption(
                theme = AppTheme.DARK,
                labelId = R.string.dark,
                iconId = R.drawable.ic_rounded_dark_mode_24
            ),
            ThemeOption(
                theme = AppTheme.FOLLOW_SYSTEM,
                labelId = R.string.dynamic,
                iconId = R.drawable.ic_rounded_brightness_medium_24
            ),
        )

        Text(
            text = stringResource(id = R.string.app_theme),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(8.dp))

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
                        AnimatedVisibility(visible = theme == themeObj) {
                            Icon(imageVector = AmanogawaIcon.check, contentDescription = null)
                        }
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

}

private data class ThemeOption(
    val theme: AppTheme,
    @StringRes val labelId: Int,
    @DrawableRes val iconId: Int,
)
