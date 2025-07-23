//package id.anison.amanogawa.feature.home.ui
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.WindowInsets
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.statusBars
//import androidx.compose.foundation.layout.windowInsetsPadding
//import androidx.compose.foundation.lazy.grid.rememberLazyGridState
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Tab
//import androidx.compose.material3.TabRow
//import androidx.compose.material3.Text
//import androidx.compose.material3.pulltorefresh.PullToRefreshBox
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.derivedStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import androidx.paging.LoadState
//import androidx.paging.compose.collectAsLazyPagingItems
//import id.anison.data.settings.AppSettings
//import id.anison.amanogawa.feature.home.HomeScreenEvents
//import id.anison.amanogawa.feature.home.HomeState
//import id.anison.amanogawa.tab.HomeTabs
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//internal fun Pager(
//    state: HomeState,
//    appSettings: AppSettings,
//    onEvents: (HomeScreenEvents) -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    val scope = rememberCoroutineScope()
//    val pagerState = rememberPagerState(pageCount = { HomeTabs.entries.size })
//    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
//    val allTitlesLazyGridState = rememberLazyGridState()
//    val currentTitlesLazyGridState = rememberLazyGridState()
//    LaunchedEffect(selectedTabIndex.value) {
//        if (HomeTabs.entries[selectedTabIndex.value] == HomeTabs.Current) {
////            onEvents(HomeScreenEvents.GetCurrentTitles)
//        }
//    }
//    Surface(modifier = Modifier.fillMaxSize()) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            TabRow(
//                selectedTabIndex = selectedTabIndex.value,
//                modifier = modifier
//                    .windowInsetsPadding(WindowInsets.statusBars)
//                    .background(MaterialTheme.colorScheme.background.copy(0.8f))
//                    .fillMaxWidth(),
//            ) {
//                HomeTabs.entries.forEachIndexed { index, currentTab ->
//                    Tab(
//                        selected = selectedTabIndex.value == index,
//                        onClick = {
//                            if (selectedTabIndex.value == index) {
//                                scope.launch {
//                                    when (currentTab) {
//                                        HomeTabs.All -> {
//                                            allTitlesLazyGridState.animateScrollToItem(0)
//                                        }
//
//                                        HomeTabs.Current -> {
//                                            currentTitlesLazyGridState.animateScrollToItem(0)
//                                        }
//                                    }
//                                }
//                            } else {
//                                scope.launch { pagerState.animateScrollToPage(currentTab.ordinal) }
//                            }
//                        },
//                        modifier = modifier.fillMaxSize(),
//                        text = {
//                            Text(
//                                text = stringResource(id = currentTab.textResId),
//                                style = MaterialTheme.typography.titleSmall
//                            )
//                        },
//                    )
//                }
//            }
//            HorizontalPager(
//                state = pagerState,
//                pageSpacing = 4.dp,
//                modifier = modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 4.dp)
//            ) { page ->
//                when (HomeTabs.entries[page]) {
//                    HomeTabs.All -> {
//                        val animeTitles = state.animeState.collectAsLazyPagingItems()
//                        when (animeTitles.loadState.refresh) {
//                            is LoadState.Loading -> {
//                                ProgressIndicator()
//                            }
//
//                            is LoadState.Error -> {
//                                ErrorMessage(onUpdate = { animeTitles.retry() })
//                            }
//
//                            is LoadState.NotLoading -> {
//                                PullToRefreshBox(
//                                    isRefreshing = animeTitles.loadState.refresh is LoadState.Loading,
//                                    onRefresh = { onEvents(HomeScreenEvents.OnRefresh) },
//                                ) {
//                                    /*TitlesContent(
//                                        onEvents = onEvents,
//                                        appSettings = appSettings,
//                                        titles = animeTitles,
//                                        lazyGridState = allTitlesLazyGridState,
//                                    )*/
//                                }
//                            }
//                        }
//                    }
//
//                    HomeTabs.Current -> {
//                        val animePagingItems = state.animeState.collectAsLazyPagingItems()
//                        when (animePagingItems.loadState.refresh) {
//                            is LoadState.Loading -> {
//                                ProgressIndicator()
//                            }
//
//                            is LoadState.Error -> {
//                                ErrorMessage(
//                                    onUpdate = {
//                                        onEvents(HomeScreenEvents.OnRefresh)
//                                    }
//                                )
//                            }
//
//                            is LoadState.NotLoading -> {
//                                PullToRefreshBox(
//                                    isRefreshing = animePagingItems.loadState.refresh is LoadState.Loading,
//                                    onRefresh = { onEvents(HomeScreenEvents.OnRefresh) },
//                                ) {
//                                    /*TitlesContent(
//                                        onEvents = onEvents,
//                                        appSettings = appSettings,
//                                        isCurrent = true,
//                                        titles = animePagingItems,
//                                        lazyGridState = currentTitlesLazyGridState,
//                                    )*/
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}