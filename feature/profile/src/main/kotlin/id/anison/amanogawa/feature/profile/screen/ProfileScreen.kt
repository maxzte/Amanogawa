@file:OptIn(ExperimentalMaterial3Api::class)

package id.anison.amanogawa.feature.profile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.navigation.NavigationHandler
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.common.component.AmanogawaHorizontalPager
import id.anison.amanogawa.core.ui.common.component.AnimeCardContent
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.core.ui.spacing
import id.anison.amanogawa.feature.profile.ProfileNavigationEvent
import id.anison.amanogawa.feature.profile.ProfileScreenModel
import id.anison.amanogawa.feature.profile.toScreen
import kotlinx.coroutines.flow.collectLatest

class ProfileScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val screenModel: ProfileScreenModel = koinScreenModel()
        val lazyListState = rememberLazyListState()
        val mediumSpacing = MaterialTheme.spacing.medium
        val recentlyWatchedState by screenModel.recentlyWatched.collectAsStateWithLifecycle()
        val favoritePreviewsState by screenModel.favoritePreviews.collectAsStateWithLifecycle()
        val favoriteCount by screenModel.favoriteCount.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            screenModel.scrollToTopEvent.collectLatest {
                if (lazyListState.layoutInfo.totalItemsCount > 0) {
                    lazyListState.animateScrollToItem(0)
                }
            }
        }

        NavigationHandler(
            navigationState = screenModel.navEventState,
            toScreen = { toScreen() }
        )

        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    title = stringResource(id = R.string.profile),
                    scrollBehavior = scrollBehavior,
                    additionalActionBtnContent = {
                        IconButton(
                            onClick = {
                                screenModel.onEvents(ProfileNavigationEvent.NavigateToSettings)
                            },
                            content = {
                                Icon(
                                    imageVector = AmanogawaIcon.settings,
                                    contentDescription = stringResource(id = R.string.settings),
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
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding() + mediumSpacing,
                    bottom = if (!isLandscape()) paddingValues.calculateBottomPadding() + AmanogawaConst.bottomBarInsets
                    else paddingValues.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(mediumSpacing)
            ) {
                item(key = "recently_watched_header") {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.recently_watched),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = AmanogawaIcon.keyboardArrowRight,
                                contentDescription = stringResource(R.string.keyboardarrowright)
                            )
                        },
                        modifier = Modifier.clickable {
                            screenModel.onEvents(ProfileNavigationEvent.NavigateToAllWatched)
                        }
                    )
                }

                item(key = "recently_watched_pager") {
                    val items = (recentlyWatchedState as? DataState.Success)?.data
                    if (items.isNullOrEmpty()) {
                        EmptySectionWithIcon(
                            icon = AmanogawaIcon.history,
                            text = stringResource(R.string.no_recently_watched),
                        )
                    } else {
                        AmanogawaHorizontalPager(
                            list = items,
                            modifier = Modifier.fillMaxSize(),
                            pageContent = { entry ->
                                AnimeCardContent(
                                    title = entry.animeName,
                                    poster = entry.animePosterThumb,
                                    year = entry.lastYear,
                                    season = entry.lastSeason,
                                    part = entry.lastPart,
                                    onClick = {
                                        screenModel.onEvents(
                                            ProfileNavigationEvent.NavigateToAnime(entry.animeId)
                                        )
                                    },
                                    extraContent = if (entry.lastEpisodeNumber > 0) {
                                        {
                                            EpisodeBadge(
                                                number = entry.lastEpisodeNumber,
                                                title = entry.lastEpisodeTitle,
                                            )
                                        }
                                    } else null,
                                )
                            },
                        )
                    }
                }

                item(key = "favorites_header") {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.favorites_tab),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = AmanogawaIcon.keyboardArrowRight,
                                contentDescription = stringResource(R.string.keyboardarrowright)
                            )
                        },
                        modifier = Modifier.clickable {
                            screenModel.onEvents(ProfileNavigationEvent.NavigateToFavorites)
                        }
                    )
                }

                item(key = "favorites_pager") {
                    val items = (favoritePreviewsState as? DataState.Success)?.data
                    if (items.isNullOrEmpty()) {
                        EmptySectionWithIcon(
                            icon = AmanogawaIcon.favoriteBorder,
                            text = stringResource(R.string.oops_you_don_t_have_favorites_yet),
                        )
                    } else {
                        AmanogawaHorizontalPager(
                            list = items,
                            modifier = Modifier.fillMaxSize(),
                            pageContent = { favorite ->
                                AnimeCardContent(
                                    title = favorite.name,
                                    poster = favorite.posterThumb,
                                    year = favorite.year,
                                    season = favorite.season,
                                    part = favorite.part,
                                    onClick = {
                                        screenModel.onEvents(
                                            ProfileNavigationEvent.NavigateToAnime(favorite.favoriteId)
                                        )
                                    },
                                )
                            },
                        )
                    }
                }

                item(key = "statistics_header") {
                    Text(
                        text = stringResource(R.string.statistics),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = mediumSpacing),
                    )
                }

                item(key = "favorites_count") {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.favorites_count, favoriteCount)
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = AmanogawaIcon.favorite,
                                contentDescription = null,
                            )
                        },
                    )
                }

                item(key = "watched_count") {
                    val watchedCount = (recentlyWatchedState as? DataState.Success)?.data?.size ?: 0
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.watched_count, watchedCount)
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = AmanogawaIcon.eye,
                                contentDescription = null,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
internal fun EpisodeBadge(
    number: Int,
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(horizontal = 4.dp, vertical = 2.dp),
    ) {
        Text(
            text = if (title.isNotBlank()) {
                "Еп. $number: $title"
            } else {
                "Еп. $number"
            },
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun EmptySectionWithIcon(
    icon: ImageVector,
    text: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }
    }
}
