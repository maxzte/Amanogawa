package id.anison.amanogawa.feature.home

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.model.AnimeUI
import id.anison.amanogawa.core.model.FavoriteUI
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.common.component.ErrorMessage
import id.anison.amanogawa.core.ui.common.component.AmanogawaSpinner
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.button.CircleButton
import id.anison.amanogawa.core.ui.common.component.button.FavoriteButton
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar

class NotificationsScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val notificationScreenModel = koinScreenModel<NotificationScreenModel>()
        val state = notificationScreenModel.notificationState.collectAsState().value

        when (val notificationsFromFav = state.notificationsFromFavorites) {
            is DataState.Error -> {
                ErrorMessage(onUpdate = {})
            }

            is DataState.Loading -> {
                AmanogawaSpinner()
            }

            is DataState.Success<*> -> {
                when (val notificationsFromCurrentTitles = state.notificationFromCurrentTitles) {
                    is DataState.Error -> {
                        ErrorMessage(onUpdate = {})
                    }

                    is DataState.Loading -> {
                        AmanogawaSpinner()
                    }

                    is DataState.Success<*> -> {
                        NotificationScreenContent(
                            navigator = navigator,
                            notificationsFromFavorites = emptyList(),//notificationsFromFav.data,
                            notificationsFromCurrentTitles = emptyList(),//notificationsFromCurrentTitles.data
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    internal fun NotificationScreenContent(
        navigator: Navigator,
        notificationsFromFavorites: List<FavoriteUI>,
        notificationsFromCurrentTitles: List<AnimeUI>,
        modifier: Modifier = Modifier
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val color by infiniteTransition.animateColor(
            initialValue = MaterialTheme.colorScheme.primary,
            targetValue = MaterialTheme.colorScheme.onPrimary,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "newEpColorIndicator"
        )
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AmanogawaTopBar(
                    title = "Notifications",
                    navigationIconContent = {
                        ArrowBackButton(onClick = { navigator.pop() })
                    },
                    additionalActionBtnContent = {}
                )
            }
            item {
                Text(text = "Favorites")
            }

            items(notificationsFromFavorites.size) {
                val favorite = notificationsFromFavorites[it]
                ListItem(
                    headlineContent = {
                        Text(text = favorite.name)
                    },
                    leadingContent = {
                        FavoriteButton(
                            isFavorite = true,
                            onFavoriteClick = { }
                        )
                    },
                    trailingContent = {
                        CircleButton(onClick = {}, modifier = Modifier.padding(4.dp)) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_circle_24),
//                                contentDescription = null,
//                                tint = color
//                            )
                        }
                    },
                    modifier = Modifier
                        .clickable {
//                        navigator.push(AnimeDetailsScreen(animeId = favorite.favoriteId))
                    }
                )
            }
            item {
                Text(text = "Current titles")
            }
            /*items(notificationsFromCurrentTitles.size) {
                val currentTitle = notificationsFromCurrentTitles[it]
                ListItem(
                    headlineContent = {
                        Text(text = currentTitle.name)
                    },
                    leadingContent = {
                        FavoriteButton(
                            isFavorite = true,
                            onFavoriteClick = {
                                navigator.push(FavoritesScreen())
                            }
                        )
                    },
                    trailingContent = {
                        CircleButton(onClick = {}, modifier = Modifier.padding(4.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_circle_24),
                                contentDescription = null,
                                tint = color
                            )
                        }
                    },
                    modifier = Modifier.clickable {
                        navigator.push(AnimeDetailsScreen(animeId = currentTitle.animeId))
                    }
                )
            }*/
        }
    }
}