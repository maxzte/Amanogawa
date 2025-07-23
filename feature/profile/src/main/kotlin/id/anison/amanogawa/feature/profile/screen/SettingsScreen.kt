package id.anison.amanogawa.feature.profile.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.ignoreHorizontalParentPadding
import id.anison.amanogawa.feature.profile.SettingsScreenEvent
import id.anison.amanogawa.feature.profile.SettingsScreenModel
import id.anison.amanogawa.feature.profile.toScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

class SettingsScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<SettingsScreenModel>()
        val isNavigating = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            screenModel.navigateEventState.collectLatest { event ->
                if (isNavigating.value) return@collectLatest
                isNavigating.value = true

                val target = event.toScreen()
                if (target != null) {
                    navigator.push(target)
                } else {
                    navigator.pop()
                }

                delay(250)
                isNavigating.value = false
            }
        }

        SettingsScreenContent(
            onEvents = { screenModel.onEvents(it) }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
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
                    top = paddingValues.calculateTopPadding() + 8.dp,
                    bottom = paddingValues.calculateBottomPadding() + 8.dp,
                    start = 8.dp,
                    end = 8.dp
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
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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

//                item {
//                    ListItem(
//                        headlineContent = {
//                            Text(
//                                text = stringResource(id = R.string.our_socials),
//                                style = MaterialTheme.typography.bodyLarge
//                            )
//                        },
//                        leadingContent = {
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_rounded_link_24),
//                                contentDescription = "Link icon",
//                            )
//                        },
//                        trailingContent = {
//                            Icon(
//                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
//                                contentDescription = stringResource(id = R.string.keyboardarrowright),
//                            )
//                        },
//                        modifier = Modifier
//                            .ignoreHorizontalParentPadding(8.dp)
//                            .clickable(
//                                onClick = { onEvents(SettingsScreenEvent.PushOurSocials) }
//                            )
//                    )
//                }

//                item {
//                    ListItem(
//                        headlineContent = {
//                            Text(
//                                text = stringResource(id = R.string.support_us),
//                                style = MaterialTheme.typography.bodyLarge
//                            )
//                        },
//                        leadingContent = {
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_gifts_24),
//                                contentDescription = stringResource(id = R.string.gift_icon),
//                            )
//                        },
//                        trailingContent = {
//                            Icon(
//                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
//                                contentDescription = stringResource(id = R.string.keyboardarrowright),
//                            )
//                        },
//                        modifier = Modifier
//                            .ignoreHorizontalParentPadding(8.dp)
//                            .clickable(
//                                onClick = { onEvents(SettingsScreenEvents.PushSupportUs) }
//                            )
//                    )
//                }
            }
        }
    }
}