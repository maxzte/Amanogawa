@file:OptIn(ExperimentalMaterial3Api::class)

package id.anison.amanogawa.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.navigation.NavigationHandler
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.common.DataStateContent
import id.anison.amanogawa.core.ui.common.component.AmanogawaHorizontalPager
import id.anison.amanogawa.core.ui.common.component.AmanogawaSpinner
import id.anison.amanogawa.core.ui.common.component.AnimeCard
import id.anison.amanogawa.core.ui.common.component.ErrorMessage
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.isLandscape

class HomeScreen : Screen {
    override val key: ScreenKey = "HomeScreen"

    @Composable
    override fun Content() {
        val screenModel: HomeScreenModel = koinScreenModel()
        val state by screenModel.state.collectAsState()
        val lazyGridState = rememberLazyGridState()

        LaunchedEffect(Unit) {
            screenModel.scrollToTopEvent.collect {
                lazyGridState.animateScrollToItem(0)
            }
        }

        NavigationHandler(
            navigationState = screenModel.navEventState,
            toScreen = { toScreen() }
        )

        val isRefreshing by screenModel.isRefreshing.collectAsState()

        HomeScreenContent(
            isRefreshing = isRefreshing,
            state = state,
            lazyGridState = lazyGridState,
            onRefresh = { screenModel.refresh() },
            onPushAnime = { animeId -> screenModel.pushAnime(animeId) },
            onPushAllTitles = { screenModel.pushAllTitles() },
        )
    }

    @Composable
    private fun HomeScreenContent(
        state: HomeState,
        lazyGridState: LazyGridState,
        onRefresh: () -> Unit,
        onPushAnime: (Long) -> Unit,
        onPushAllTitles: () -> Unit,
        isRefreshing: Boolean,
    ) {
        val isLandscape = isLandscape()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier.widthIn(max = AmanogawaConst.maxContentWidth)
            ) {
                Scaffold { paddingValues ->
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = onRefresh,
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(200.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 0.dp,
                                bottom = if (!isLandscape) paddingValues.calculateBottomPadding() + AmanogawaConst.bottomBarInsets
                                else paddingValues.calculateBottomPadding()
                            ),
                            state = lazyGridState,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            if (!isLandscape) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    AmanogawaTopBar(
                                        title = stringResource(id = R.string.amanogawa),
                                        navigationIconContent = {
                                            IconButton(
                                                onClick = {},
                                                content = {
                                                    Icon(
                                                        painter = AmanogawaIcon.amanogawaLogo,
                                                        contentDescription = stringResource(
                                                            id = R.string.amanogawa
                                                        ),
                                                        tint = MaterialTheme.colorScheme.primary,
                                                    )
                                                }
                                            )
                                        },
                                        containerColor = Color.Transparent,
                                    )
                                }
                            }

                            if (isLandscape) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(paddingValues.calculateTopPadding())
                                    )
                                }
                            }

                            when {
                                state.currentAnimeState is DataState.Loading || state.animeListState is DataState.Loading -> {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        AmanogawaSpinner()
                                    }
                                }

                                state.currentAnimeState is DataState.Error || state.animeListState is DataState.Error -> {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        ErrorMessage(onUpdate = onRefresh)
                                    }
                                }

                                else -> {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Text(
                                            text = "Current titles",
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }

                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        DataStateContent(
                                            state = state.currentAnimeState,
                                            loadingContent = {},
                                            errorContent = {},
                                            content = { currentList ->
                                                val pagerState = rememberPagerState(
                                                    pageCount = { currentList.size }
                                                )

                                                AmanogawaHorizontalPager(
                                                    pagerState = pagerState,
                                                    list = currentList,
                                                    key = { currentList[it].animeId },
                                                    contentPadding = PaddingValues(16.dp),
                                                    pageContent = { anime ->
                                                        AnimeCard(
                                                            title = anime.name,
                                                            poster = anime.posterThumb,
                                                            year = anime.year,
                                                            season = anime.season,
                                                            part = anime.part,
                                                            views = anime.views,
                                                            timeStr = anime.timeStr,
                                                            schedule = anime.schedule,
                                                            isCurrent = anime.isCurrent,
                                                            onClick = {
                                                                onPushAnime(anime.animeId)
                                                            },
                                                        )
                                                    },
                                                )
                                            }
                                        )
                                    }

                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        ListItem(
                                            headlineContent = {
                                                Text(
                                                    text = "All titles",
                                                    style = MaterialTheme.typography.titleLarge,
                                                )
                                            },
                                            trailingContent = {
                                                Text(
                                                    text = "Show all",
                                                    style = MaterialTheme.typography.titleMedium,
                                                )
                                            },
                                            modifier = Modifier.clickable { onPushAllTitles() }
                                        )
                                    }

                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        DataStateContent(
                                            state = state.animeListState,
                                            loadingContent = {},
                                            errorContent = {},
                                            content = { allTitlesList ->
                                                val snapshotCount =
                                                    allTitlesList.size.coerceAtMost(10)
                                                val snapshotItems =
                                                    allTitlesList.take(snapshotCount)

                                                if (snapshotItems.isNotEmpty()) {
                                                    val allTitlesPagerState = rememberPagerState(
                                                        pageCount = { snapshotItems.size }
                                                    )

                                                    AmanogawaHorizontalPager(
                                                        pagerState = allTitlesPagerState,
                                                        list = snapshotItems,
                                                        key = { snapshotItems[it].animeId },
                                                        contentPadding = PaddingValues(16.dp),
                                                        pageContent = { anime ->
                                                            AnimeCard(
                                                                title = anime.name,
                                                                poster = anime.posterThumb,
                                                                year = anime.year,
                                                                season = anime.season,
                                                                part = anime.part,
                                                                views = anime.views,
                                                                timeStr = anime.timeStr,
                                                                schedule = anime.schedule,
                                                                isCurrent = false,
                                                                onClick = {
                                                                    onPushAnime(anime.animeId)
                                                                },
                                                            )
                                                        },
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
