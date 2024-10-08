package id.anison.feature.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.data.model.AppSettings
import id.anison.feature.R
import id.anison.feature.anime.AnimeScreen
import id.anison.feature.common.component.CoilImageLoader
import id.anison.feature.common.component.ErrorMessage
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.model.Title
import id.anison.feature.profile.SettingsScreenModel
import id.anison.feature.profile.SettingsUIState
import id.anison.feature.tab.HomeTabs
import kotlinx.coroutines.launch

class HomeScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HomeScreenModel>()
        val settingsScreenModel = koinScreenModel<SettingsScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        HomeScreenContent(navigator, screenModel, settingsScreenModel)
    }

    @Composable
    internal fun HomeScreenContent(
        navigator: Navigator,
        screenModel: HomeScreenModel,
        settingsScreenModel: SettingsScreenModel,
    ) {
        val state = screenModel.animeState.collectAsState().value
        val appSettingsState by settingsScreenModel.appSettings.collectAsState()
        Pager(
            state = state,
            appSettings = when (appSettingsState) {
                is SettingsUIState.Loading -> AppSettings()
                is SettingsUIState.Success -> (appSettingsState as SettingsUIState.Success).settings
            },
            onEvents = { event ->
                when (event) {
                    is HomeScreenEvents.PushAnime -> navigator.push(AnimeScreen(event.id))
                    is HomeScreenEvents.GetCurrentTitles -> screenModel.getCurrentTitles()
                    is HomeScreenEvents.OnRefresh -> screenModel.refreshTitles()
                    is HomeScreenEvents.OnCurrentTitlesRefresh -> screenModel.refreshCurrentTitles()
                    is HomeScreenEvents.ObserveConnection -> screenModel.updateScrollPosition(
                        event.index,
                        event.currIndex
                    )
                }
            },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    internal fun Pager(
        state: HomeState,
        appSettings: AppSettings,
        onEvents: (HomeScreenEvents) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState(pageCount = { HomeTabs.entries.size })
        val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
        val allTitlesLazyGridState = rememberLazyGridState()
        val currentTitlesLazyGridState = rememberLazyGridState()
        LaunchedEffect(selectedTabIndex.value) {
            if (HomeTabs.entries[selectedTabIndex.value] == HomeTabs.Current) {
                onEvents(HomeScreenEvents.GetCurrentTitles)
            }
        }
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(
                    selectedTabIndex = selectedTabIndex.value,
                    modifier = modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .background(MaterialTheme.colorScheme.background.copy(0.8f))
                        .fillMaxWidth(),
                ) {
                    HomeTabs.entries.forEachIndexed { index, currentTab ->
                        Tab(
                            selected = selectedTabIndex.value == index,
                            onClick = {
                                if (selectedTabIndex.value == index) {
                                    scope.launch {
                                        when (currentTab) {
                                            HomeTabs.All ->
                                                allTitlesLazyGridState.animateScrollToItem(0)

                                            HomeTabs.Current ->
                                                currentTitlesLazyGridState.animateScrollToItem(0)
                                        }
                                    }
                                } else {
                                    scope.launch { pagerState.animateScrollToPage(currentTab.ordinal) }
                                }
                            },
                            modifier = modifier.fillMaxSize(),
                            text = {
                                Text(
                                    text = stringResource(id = currentTab.textResId),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            },
                        )
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    pageSpacing = 4.dp,
                    modifier = modifier
                        .fillMaxWidth()
//                        .padding(it)
                        .padding(horizontal = 4.dp)
                ) { page ->
                    when (HomeTabs.entries[page]) {
                        HomeTabs.All -> {
                            val animeTitles = state.titlesState.collectAsLazyPagingItems()
                            when (animeTitles.loadState.refresh) {
                                is LoadState.Loading -> ProgressIndicator()
                                is LoadState.Error ->
                                    ErrorMessage(onUpdate = { animeTitles.retry() /*onEvents(HomeScreenEvents.OnRefresh)*/ })

                                is LoadState.NotLoading -> {
                                    PullToRefreshBox(
                                        isRefreshing = animeTitles.loadState.refresh is LoadState.Loading,
                                        onRefresh = { onEvents(HomeScreenEvents.OnRefresh) },
                                    ) {
                                        TitlesContent(
                                            onEvents = onEvents,
                                            appSettings = appSettings,
                                            titles = animeTitles,
                                            lazyGridState = allTitlesLazyGridState,
                                        )
                                    }
                                }
                            }
                        }

                        HomeTabs.Current -> {
                            val animeTitles = state.currentTitlesState.collectAsLazyPagingItems()
                            when (animeTitles.loadState.refresh) {
                                is LoadState.Loading -> ProgressIndicator()
                                is LoadState.Error ->
                                    ErrorMessage(onUpdate = { onEvents(HomeScreenEvents.OnCurrentTitlesRefresh) })

                                is LoadState.NotLoading -> {
                                    PullToRefreshBox(
                                        isRefreshing = animeTitles.loadState.refresh is LoadState.Loading,
                                        onRefresh = { onEvents(HomeScreenEvents.OnCurrentTitlesRefresh) },
                                    ) {
                                        TitlesContent(
                                            onEvents = onEvents,
                                            appSettings = appSettings,
                                            isCurrent = true,
                                            titles = animeTitles,
                                            lazyGridState = currentTitlesLazyGridState,
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

    @Composable
    internal fun <T : Title> TitlesContent(
        onEvents: (HomeScreenEvents) -> Unit,
        appSettings: AppSettings,
        modifier: Modifier = Modifier,
        isCurrent: Boolean = false,
        titles: LazyPagingItems<T>,
        lazyGridState: LazyGridState,
    ) {
        when (titles.loadState.refresh) {
            is LoadState.Loading -> ProgressIndicator()
            is LoadState.Error -> titles.refresh()//onEvents(HomeScreenEvents.ObserveConnection(titles.itemSnapshotList.lastIndex))
            //ErrorMessage(onUpdate = { onEvents(HomeScreenEvents.OnRefresh) })

            is LoadState.NotLoading -> {
                val configuration = LocalConfiguration.current
                val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
                val count = if (!isLandscape) appSettings.countColumnsInRow
                else appSettings.countColumnsInRowLandscape
                //val itemWidth = configuration.screenWidthDp.dp / count
                LazyVerticalGrid(
                    columns = GridCells.Fixed(count),
                    modifier = modifier.fillMaxSize(),
                    state = lazyGridState,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        count = titles.itemCount,
                        key = titles.itemKey { it.id }
                    ) { index ->
                        val item = titles[index]
                        if (item != null) {
                            val season = if (item.season != 0) item.season else ""
                            val part = if (item.part != 0) "Part ${item.part}" else ""
                            val schedule = item.schedule ?: ""
                            Column(
                                modifier = modifier
                                    .padding(top = 2.dp, bottom = 2.dp)
                                    .clickable {
                                        onEvents(
                                            HomeScreenEvents.PushAnime(
                                                item.id,
                                                schedule
                                            )
                                        )
                                    },
                            ) {
                                Box {
                                    ElevatedCard(
                                        elevation = CardDefaults.elevatedCardElevation()
                                    ) {
                                        CoilImageLoader(
                                            titleImage = item.poster_thumb,
                                            modifier = modifier
                                                .fillMaxWidth()
                                                .aspectRatio(7f / 10f),
                                        )
                                    }
                                    if (isCurrent) {
                                        Column(
                                            modifier = modifier
                                                .fillMaxSize()
                                                .align(Alignment.BottomCenter)
                                                .clip(
                                                    RoundedCornerShape(
                                                        bottomEnd = 8.dp,
                                                        bottomStart = 8.dp,
                                                    )
                                                )
                                                .alpha(0.85f)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(2.dp),
                                        ) {
                                            if (!item.time_str.isNullOrBlank()) Text(
                                                text = "${item.time_str!!} ${stringResource(id = R.string.ago)}",
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                style = if (!isLandscape) MaterialTheme.typography.labelLarge
                                                else MaterialTheme.typography.bodySmall,
                                                textAlign = TextAlign.Center,
                                            )
                                            if (!item.schedule.isNullOrBlank() && !isLandscape) Text(
                                                text = item.schedule!!,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                style = MaterialTheme.typography.labelSmall,
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${item.name} $season",
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    if (part.isNotBlank()) Text(
                                        text = part,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = item.views.toString(),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Icon(
                                            painter = painterResource(R.drawable.ic_watch_eye),
                                            contentDescription = stringResource(id = R.string.views_icon),
                                            modifier = modifier.size(16.dp),
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
