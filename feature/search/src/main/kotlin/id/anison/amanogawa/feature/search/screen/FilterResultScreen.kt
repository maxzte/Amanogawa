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
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.amanogawa.core.model.AnimeUI
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.model.PagingEvent
import id.anison.amanogawa.core.navigation.SharedScreen
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.columnsInRow
import id.anison.amanogawa.core.ui.common.component.AmanogawaSpinner
import id.anison.amanogawa.core.ui.common.component.AnimeCard
import id.anison.amanogawa.core.ui.common.component.ErrorMessage
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.feature.search.screenmodel.FilterResultScreenEvents
import id.anison.amanogawa.feature.search.screenmodel.FilterResultScreenModel
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
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val filterResultScreenModel = koinScreenModel<FilterResultScreenModel>(
            parameters = { parametersOf(filterId, filterName, filterType) }
        )
        val filterResultState by filterResultScreenModel.state.collectAsState()
        val pagingItems = if (filterName == "") {
            filterResultState.filteredTitles.collectAsLazyPagingItems()
        } else {
            filterResultState.titlesByLink.collectAsLazyPagingItems()
        }

        val isRefreshing by filterResultScreenModel.isRefreshing.collectAsState()

        LaunchedEffect(pagingItems.loadState.refresh) {
            val refresh = pagingItems.loadState.refresh
            if (refresh is LoadState.NotLoading && isRefreshing) {
                filterResultScreenModel.setRefreshing(false)
            }
        }

        LaunchedEffect(Unit) {
            filterResultScreenModel.pagingEvent.collectLatest { event ->
                when (event) {
                    PagingEvent.RELOAD -> {}
                    PagingEvent.REFRESH -> pagingItems.refresh()
                    PagingEvent.RETRY -> pagingItems.retry()
                }
            }
        }

        FilterResultContent(
            topBarTitle = filterName,
            pagingItems = pagingItems,
            onEvents = { event ->
                when (event) {
                    is FilterResultScreenEvents.PopBack -> {
                        navigator.pop()
                    }

                    is FilterResultScreenEvents.PushTitle -> {
                        navigator.push(ScreenRegistry.get(SharedScreen.AnimeDetails(event.id)))
                    }

                    is FilterResultScreenEvents.Refresh -> {
                        filterResultScreenModel.refresh()
                    }
                }
            },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun FilterResultContent(
        topBarTitle: String = "",
        pagingItems: LazyPagingItems<AnimeUI>,
        onEvents: (FilterResultScreenEvents) -> Unit,
    ) {
        val lazyGridState = rememberLazyGridState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    title = if (topBarTitle == "") stringResource(R.string.found_titles) else topBarTitle,
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
                            bottom = paddingValues.calculateBottomPadding() + AmanogawaConst.bottomBarInsets,
                        ),
                        state = lazyGridState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            count = pagingItems.itemCount,
                            key = pagingItems.itemKey { it.animeId }
                        ) { index ->
                            val item = pagingItems[index]
                            if (item != null) {
                                AnimeCard(
                                    anime = item,
                                    onClick = {
                                        onEvents(FilterResultScreenEvents.PushTitle(item.animeId))
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
