@file:OptIn(ExperimentalMaterial3Api::class)

package id.anison.amanogawa.feature.profile.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import id.anison.amanogawa.core.domain.model.WatchHistory
import id.anison.amanogawa.core.navigation.NavigationHandler
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.DataStateContent
import id.anison.amanogawa.core.ui.common.component.AnimeCardContent
import id.anison.amanogawa.core.ui.common.component.Message
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.component.layout.AnimeVerticalGrid
import id.anison.amanogawa.feature.profile.AllWatchedScreenEvents
import id.anison.amanogawa.feature.profile.AllWatchedScreenModel
import id.anison.amanogawa.feature.profile.toScreen

class AllWatchedScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val screenModel: AllWatchedScreenModel = koinScreenModel()
        val watchedState by screenModel.watchedState.collectAsState()
        val lazyGridState = screenModel.lazyGridState

        LaunchedEffect(Unit) {
            screenModel.scrollToTopEvent.collect {
                lazyGridState.animateScrollToItem(0)
            }
        }

        NavigationHandler(
            navigationState = screenModel.navigationEvent,
            toScreen = { toScreen() }
        )

        DataStateContent(
            state = watchedState,
            errorContent = {
                Message("something went wrong")
            },
            content = { watchedList ->
                AllWatchedScreenContent(
                    lazyGridState = lazyGridState,
                    watchedList = watchedList,
                    onEvents = { screenModel.onEvent(it) },
                )
            }
        )
    }

    @Composable
    fun AllWatchedScreenContent(
        lazyGridState: LazyGridState,
        watchedList: List<WatchHistory>,
        onEvents: (AllWatchedScreenEvents) -> Unit,
    ) {
        var deleteTarget by remember { mutableStateOf<WatchHistory?>(null) }

        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    title = stringResource(id = R.string.recently_watched),
                    navigationIconContent = {
                        ArrowBackButton(
                            onClick = { onEvents(AllWatchedScreenEvents.PopBack) },
                        )
                    },
                    additionalActionBtnContent = {
                        if (watchedList.isNotEmpty()) {
                            TextButton(
                                onClick = { onEvents(AllWatchedScreenEvents.ClearHistory) },
                                content = {
                                    Text(text = stringResource(id = R.string.clear_all))
                                }
                            )
                        }
                    },
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            AnimatedVisibility(
                visible = watchedList.isEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Message(text = stringResource(id = R.string.no_recently_watched))
            }

            AnimeVerticalGrid(
                state = lazyGridState,
                scaffoldPadding = paddingValues,
            ) {
                items(items = watchedList, key = { it.animeId }) { entry ->
                    WatchHistoryCard(
                        entry = entry,
                        onClick = { onEvents(AllWatchedScreenEvents.OnAnimeClick(entry.animeId)) },
                        onLongClick = { deleteTarget = entry },
                    )
                }
            }
        }

        deleteTarget?.let { target ->
            AlertDialog(
                onDismissRequest = { deleteTarget = null },
                title = { Text(text = target.animeName) },
                text = { Text(text = stringResource(R.string.delete_watch_history_confirmation)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onEvents(AllWatchedScreenEvents.OnDeleteAnime(target.animeId))
                            deleteTarget = null
                        },
                        content = { Text(text = stringResource(R.string.delete)) },
                    )
                },
                dismissButton = {
                    TextButton(
                        onClick = { deleteTarget = null },
                        content = { Text(text = stringResource(R.string.cancel)) },
                    )
                },
            )
        }
    }
}

@Composable
internal fun WatchHistoryCard(
    entry: WatchHistory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit = {},
) {
    AnimeCardContent(
        title = entry.animeName,
        poster = entry.animePosterThumb,
        year = entry.lastYear,
        season = entry.lastSeason,
        part = entry.lastPart,
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick,
        extraContent = {
            if (entry.lastEpisodeNumber > 0) {
                EpisodeBadge(
                    number = entry.lastEpisodeNumber,
                    title = entry.lastEpisodeTitle,
                )
            }
        }
    )
}
