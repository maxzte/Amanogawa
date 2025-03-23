package id.anison.feature.profile.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.R
import id.anison.feature.common.component.layout.AmanogawaTopBar
import id.anison.feature.favorite.FavoritesScreen

class ProfileScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        ProfileScreenContent(navigator)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProfileScreenContent(
        navigator: Navigator,
    ) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
                .fillMaxSize()
        ) {
            AmanogawaTopBar(
                title = stringResource(R.string.profile),
                additionalActionBtnContent = {
                    IconButton(onClick = { navigator.push(SettingsScreen()) }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = stringResource(id = R.string.search_icon)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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
                                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                contentDescription = stringResource(R.string.keyboardarrowright)
                            )
                        },
                        modifier = Modifier
                            .clickable {
                                navigator.push(FavoritesScreen())
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

