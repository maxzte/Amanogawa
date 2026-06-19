@file:OptIn(ExperimentalMaterial3Api::class)

package id.anison.amanogawa.feature.profile.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import id.anison.amanogawa.core.navigation.NavigationHandler
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.ignoreHorizontalParentPadding
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.feature.profile.SettingsScreenEvent
import id.anison.amanogawa.feature.profile.SettingsScreenModel
import id.anison.amanogawa.feature.profile.toScreen

class SettingsScreen : Screen {
    override val key: ScreenKey
        get() = "settingsScreen"

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<SettingsScreenModel>()

        NavigationHandler(
            navigationState = screenModel.navigateEventState,
            toScreen = { toScreen() }
        )

        SettingsScreenContent(
            onEvents = { screenModel.onEvents(it) }
        )
    }

    @Composable
    private fun SettingsScreenContent(
        onEvents: (SettingsScreenEvent) -> Unit,
    ) {
        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    title = stringResource(id = R.string.settings),
                    navigationIconContent = {
                        ArrowBackButton(onClick = { onEvents(SettingsScreenEvent.PopBack) })
                    },
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            LazyColumn(
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
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(id = R.string.appearance),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_rounded_palette_24),
                                contentDescription = "Appearance icon",
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = AmanogawaIcon.keyboardArrowRight,
                                contentDescription = stringResource(id = R.string.keyboardarrowright),
                            )
                        },
                        modifier = Modifier
                            .ignoreHorizontalParentPadding(8.dp)
                            .clickable(
                                onClick = { onEvents(SettingsScreenEvent.PushAppearance) }
                            )
                    )
                }

                item {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(id = R.string.about_us),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_rounded_info_24),
                                contentDescription = "Info icon",
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = AmanogawaIcon.keyboardArrowRight,
                                contentDescription = stringResource(id = R.string.keyboardarrowright),
                            )
                        },
                        modifier = Modifier
                            .ignoreHorizontalParentPadding(8.dp)
                            .clickable(
                                onClick = { onEvents(SettingsScreenEvent.PushAboutUs) }
                            )
                    )
                }
            }
        }
    }
}