@file:OptIn(ExperimentalMaterial3Api::class)

package id.anison.amanogawa.feature.profile.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import id.anison.amanogawa.core.navigation.NavigationHandler
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.feature.profile.ProfileNavigationEvent
import id.anison.amanogawa.feature.profile.ProfileScreenModel
import id.anison.amanogawa.feature.profile.toScreen

class ProfileScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val screenModel = koinScreenModel<ProfileScreenModel>()

        NavigationHandler(
            navigationState = screenModel.navEventState,
            toScreen = { toScreen() }
        )

        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    title = stringResource(id = R.string.profile),
                    additionalActionBtnContent = {
                        IconButton(
                            onClick = {
                                screenModel.onEvents(ProfileNavigationEvent.NavigateToSettings)
                            },
                            content = {
                                Icon(
                                    imageVector = AmanogawaIcon.settings,
                                    contentDescription = stringResource(id = R.string.search_icon),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        )
                    },
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    top = paddingValues.calculateTopPadding() + 8.dp,
                    end = 8.dp,
                    bottom = if (!isLandscape()) paddingValues.calculateBottomPadding() + AmanogawaConst.bottomBarInsets
                    else paddingValues.calculateBottomPadding()
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ListItem(
                        headlineContent = {
                            Text(text = stringResource(R.string.favorites_tab))
                        },
                        trailingContent = {
                            Icon(
                                imageVector = AmanogawaIcon.keyboardArrowRight,
                                contentDescription = stringResource(R.string.keyboardarrowright)
                            )
                        },
                        modifier = Modifier
                            .clickable {
                                screenModel.onEvents(ProfileNavigationEvent.NavigateToFavorites)
                            }
                    )
                }

                item {
                    ListItem(
                        headlineContent = {
                            Text(text = "Recently watched")
                        },
                        trailingContent = {
                            OutlinedButton(
                                onClick = {
//                                    navigator.push(FavoritesScreen())
                                },
                            ) {
                                Text(text = "See all")
                            }
                        },
                    )
                }
            }
        }
    }
}

