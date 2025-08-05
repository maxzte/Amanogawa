package id.anison.amanogawa.feature.search.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import id.anison.amanogawa.core.model.AnimeUI
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.model.PagingEvent
import id.anison.amanogawa.core.navigation.NavigationHandler
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.columnsInRow
import id.anison.amanogawa.core.ui.common.component.AmanogawaSpinner
import id.anison.amanogawa.core.ui.common.component.AnimeCard
import id.anison.amanogawa.core.ui.common.component.ErrorMessage
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.feature.search.screenmodel.FilterResultScreenEvents
import id.anison.amanogawa.feature.search.screenmodel.FilterResultScreenModel
import id.anison.amanogawa.feature.search.screenmodel.toScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

/**
 * @param [filterName] and [filterId] - pass these params only if you want get search result by one filter
 */
class FilterResultScreen(
    private val filterId: Long? = null,
    private val filterName: String = "",
    private val filterType: FilterTypeUI = FilterTypeUI.YEAR,
) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<FilterResultScreenModel>(
            parameters = { parametersOf(filterId, filterName, filterType) }
        )
        val filterResultState by screenModel.state.collectAsState()
        val pagingItems = if (filterName == "") {
            filterResultState.filteredTitles.collectAsLazyPagingItems()
        } else {
            filterResultState.titlesByLink.collectAsLazyPagingItems()
        }

        NavigationHandler(
            navigationState = screenModel.navigationEventState,
            toScreen = { toScreen() }
        )

        val isRefreshing by screenModel.isRefreshing.collectAsState()

        LaunchedEffect(pagingItems.loadState.refresh) {
            val refresh = pagingItems.loadState.refresh
            if (refresh is LoadState.NotLoading && isRefreshing) {
                screenModel.setRefreshing(false)
            }
        }

        LaunchedEffect(Unit) {
            screenModel.pagingEvent.collectLatest { event ->
                when (event) {
                    PagingEvent.RELOAD -> {}
                    PagingEvent.REFRESH -> pagingItems.refresh()
                    PagingEvent.RETRY -> pagingItems.retry()
                }
            }
        }

        FilterResultContent(
            pagingItems = pagingItems,
            onEvents = { screenModel.onEvents(it) },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun FilterResultContent(
        pagingItems: LazyPagingItems<AnimeUI>,
        onEvents: (FilterResultScreenEvents) -> Unit,
    ) {
        val lazyGridState = rememberLazyGridState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    title = if (filterName == "") stringResource(R.string.found_titles) else filterName,
                    navigationIconContent = {
                        ArrowBackButton(onClick = { onEvents(FilterResultScreenEvents.PopBack) })
                    },
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.clickable {
                        scope.launch {
                            lazyGridState.scrollToItem(0)
                        }
                    },
                    containerColor = Color.Transparent
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { paddingValues ->
            when (pagingItems.loadState.refresh) {
                is LoadState.Loading -> {
                    AmanogawaSpinner()
                }

                is LoadState.Error -> {
                    ErrorMessage(onUpdate = { onEvents(FilterResultScreenEvents.Refresh) })
                }

                is LoadState.NotLoading -> {
                    val count = columnsInRow()

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(count),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            top = paddingValues.calculateTopPadding() + 8.dp,
                            end = 8.dp,
                            bottom = if (!isLandscape()) paddingValues.calculateBottomPadding() + AmanogawaConst.bottomBarInsets
                            else paddingValues.calculateBottomPadding()
                        ),
                        state = lazyGridState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            count = pagingItems.itemCount,
                            key = pagingItems.itemKey { it.animeId }
                        ) { index ->
                            val anime = pagingItems[index]
                            if (anime != null) {
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
                                        onEvents(FilterResultScreenEvents.PushTitle(anime.animeId))
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}
