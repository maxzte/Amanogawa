package id.anison.amanogawa.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.amanogawa.core.model.AnimeUI
import id.anison.amanogawa.core.model.PagingEvent
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.common.DataStateContent
import id.anison.amanogawa.core.ui.common.columnsInRow
import id.anison.amanogawa.core.ui.common.component.AmanogawaHorizontalPager
import id.anison.amanogawa.core.ui.common.component.AnimeCard
import id.anison.amanogawa.core.ui.common.component.ErrorMessage
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.ignoreHorizontalParentPadding
import id.anison.amanogawa.core.ui.common.isLandscape
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

class HomeScreen : Screen {
    override val key: ScreenKey = "HomeScreen"

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<HomeScreenModel>()
        val state by screenModel.state.collectAsState()
        val animeTitles = screenModel.titlesPagingData.collectAsLazyPagingItems()
        val lazyGridState = screenModel.lazyGridState
        val isNavigating = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            screenModel.navEventState.collectLatest { event ->
                if (isNavigating.value) return@collectLatest
                isNavigating.value = true
                navigator.push(event.toScreen())
                delay(250)
                isNavigating.value = false
            }
        }

        LaunchedEffect(Unit) {
            screenModel.pagingEvent.collect { action ->
                when (action) {
                    PagingEvent.RELOAD -> {
//                        animeTitles.refresh()
                    }

                    PagingEvent.REFRESH -> {
                        animeTitles.refresh()
                    }

                    PagingEvent.RETRY -> {
                        // TODO: handle retry event
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            screenModel.scrollToTopEvent.collect {
                snapshotFlow { lazyGridState.layoutInfo.totalItemsCount }.first { it > 0 }

                if (lazyGridState.firstVisibleItemIndex > 0 || lazyGridState.firstVisibleItemScrollOffset > 0) {
                    lazyGridState.scrollToItem(0)
                }
            }
        }

        HomeScreenContent(
            animeTitles = animeTitles,
            state = state,
            lazyGridState = lazyGridState,
            onEvents = { screenModel.onEvent(it) },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun HomeScreenContent(
        animeTitles: LazyPagingItems<AnimeUI>,
        state: HomeState,
        lazyGridState: LazyGridState,
        onEvents: (HomeScreenEvents) -> Unit,
    ) {
        val isLandscape = isLandscape()
        val countColumnsInRow = columnsInRow()

        Box(contentAlignment = Alignment.TopCenter) {
            Scaffold { paddingValues ->
                DataStateContent(
                    state = state.currentAnimeState,
                    errorContent = {
                        ErrorMessage(
                            onUpdate = {
                                onEvents(HomeScreenEvents.OnRefresh)
                            }
                        )
                    },
                    loadingContent = {},
                    content = { currentList ->
                        var currentPage by rememberSaveable { mutableIntStateOf(0) }
                        val pagerState = rememberPagerState(
                            initialPage = currentPage,
                            pageCount = { currentList.size }
                        )

                        LaunchedEffect(pagerState) {
                            snapshotFlow { pagerState.currentPage }.collect { page ->
                                currentPage = page
                            }
                        }

                        PullToRefreshBox(
                            isRefreshing = animeTitles.loadState.refresh is LoadState.Loading,
                            onRefresh = {
                                onEvents(HomeScreenEvents.OnRefresh)
                            },
                        ) {
                            when (animeTitles.loadState.refresh) {
                                is LoadState.Loading -> {
//                                    AmanogawaSpinner()
                                }

                                is LoadState.Error -> {
//                                    animeTitles.retry()
                                }

                                is LoadState.NotLoading -> {
                                    if (animeTitles.itemCount > 0) {
                                        LazyVerticalGrid(
                                            columns = GridCells.Fixed(count = countColumnsInRow),
                                            modifier = Modifier.fillMaxSize(),
                                            contentPadding = PaddingValues(
                                                top = if (isLandscape) paddingValues.calculateTopPadding() else 0.dp,
                                                start = 8.dp,
                                                end = 8.dp,
                                                bottom = paddingValues.calculateBottomPadding() + AmanogawaConst.bottomBarInsets
                                            ),
                                            state = lazyGridState,
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                                                        modifier = Modifier.ignoreHorizontalParentPadding(
                                                            8.dp
                                                        )
                                                    )
                                                }
                                            }

                                            item(span = { GridItemSpan(maxLineSpan) }) {
                                                Text(
                                                    text = "Current titles",
                                                    style = MaterialTheme.typography.titleLarge,
                                                )
                                            }

                                            item(span = { GridItemSpan(maxLineSpan) }) {
                                                AmanogawaHorizontalPager(
                                                    pagerState = pagerState,
                                                    list = currentList,
                                                    countColumnsInRow = countColumnsInRow,
                                                    key = { currentList[it].animeId },
                                                    pageContent = { anime ->
                                                        AnimeCard(
                                                            anime = anime,
                                                            isCurrent = anime.isCurrent,
                                                            onClick = {
                                                                onEvents(
                                                                    HomeScreenEvents.PushAnime(
                                                                        animeId = anime.animeId
                                                                    )
                                                                )
                                                            },
                                                        )
                                                    },
                                                    modifier = Modifier.ignoreHorizontalParentPadding(
                                                        8.dp
                                                    )
                                                )
                                            }

                                            item(span = { GridItemSpan(maxLineSpan) }) {
                                                Text(
                                                    text = "All titles",
                                                    style = MaterialTheme.typography.titleLarge,
                                                )
                                            }

                                            items(
                                                count = animeTitles.itemCount,
                                                key = animeTitles.itemKey { it.animeId }
                                            ) { index ->
                                                val anime = animeTitles[index]
                                                if (anime != null) {
                                                    AnimeCard(
                                                        anime = anime,
                                                        onClick = {
                                                            onEvents(
                                                                HomeScreenEvents.PushAnime(
                                                                    animeId = anime.animeId
                                                                )
                                                            )
                                                        },
                                                        isCurrent = false,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}
