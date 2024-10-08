package id.anison.feature.search.screen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.data.model.AppSettings
import id.anison.feature.R
import id.anison.feature.anime.AnimeScreen
import id.anison.feature.common.component.CoilImageLoader
import id.anison.feature.common.component.CustomTopAppBar
import id.anison.feature.common.component.ErrorMessage
import id.anison.feature.common.component.Message
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.model.TitleUI
import id.anison.feature.profile.SettingsScreenModel
import id.anison.feature.profile.SettingsUIState
import id.anison.feature.search.event.ExtendedSearchTitlesScreenEvents
import id.anison.feature.search.screenmodel.ExtendedSearchScreenModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExtendedSearchTitlesScreen(private val filterName: String? = null) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val screenModel = navigator.koinNavigatorScreenModel<ExtendedSearchScreenModel>()
        val state by screenModel.extendedSearchState.collectAsState()
        val settingsScreenModel = koinScreenModel<SettingsScreenModel>()
        val appSettingsState by settingsScreenModel.appSettings.collectAsState()
        val isOnline = state.isOnline
        ExtendedListContent(
            topBarTitle = filterName,
            onEvents = { event ->
                when(event) {
                    is ExtendedSearchTitlesScreenEvents.PopBack -> navigator.pop()
                    is ExtendedSearchTitlesScreenEvents.PushTitle -> navigator.push(AnimeScreen(event.id))
                    is ExtendedSearchTitlesScreenEvents.Refresh -> screenModel.refresh(filterName)
                }
            },
            state = if (filterName == null) state.filteredTitles else state.titlesByLink,
            isOnline = isOnline,
            appSettings = when (appSettingsState) {
                is SettingsUIState.Loading -> AppSettings()
                is SettingsUIState.Success -> (appSettingsState as SettingsUIState.Success).settings
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExtendedListContent(
    onEvents: (ExtendedSearchTitlesScreenEvents) -> Unit,
    topBarTitle: String? = null,
    appSettings: AppSettings,
    state: StateFlow<PagingData<TitleUI>>,
    isOnline: Boolean,
) {
    val lazyGridState = rememberLazyGridState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = topBarTitle ?: stringResource(R.string.found_titles),
                popNavigate = { onEvents(ExtendedSearchTitlesScreenEvents.PopBack) },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.clickable {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                },
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        if (isOnline)
            AllTitles(
                onEvents = onEvents,
                state = state,
                lazyGridState = lazyGridState,
                appSettings = appSettings,
                modifier = Modifier.padding(top = it.calculateTopPadding()),
            )
        else ErrorMessage(onUpdate = { onEvents(ExtendedSearchTitlesScreenEvents.Refresh) })
    }
}

@Composable
internal fun AllTitles(
    onEvents: (ExtendedSearchTitlesScreenEvents) -> Unit,
    appSettings: AppSettings,
    state: StateFlow<PagingData<TitleUI>>,
    lazyGridState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    val pagingItems = state.collectAsLazyPagingItems()
    when (pagingItems.loadState.refresh) {
        is LoadState.Loading -> ProgressIndicator()
        is LoadState.Error -> ErrorMessage(onUpdate = { onEvents(ExtendedSearchTitlesScreenEvents.Refresh) })
        is LoadState.NotLoading -> {
            val configuration = LocalConfiguration.current
            val isLandscape =
                configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            val count = if (!isLandscape) appSettings.countColumnsInRow
            else appSettings.countColumnsInRowLandscape
            if (pagingItems.itemCount > 0) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(count),
                    modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    state = lazyGridState,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(
                        count = pagingItems.itemCount,
                        key = pagingItems.itemKey { it.id }
                    ) { index ->
                        val item = pagingItems[index]
                        if (item != null) {
                            val season =
                                if (item.season != 0) item.season.toString() else ""
                            val part = if (item.part != 0) "Part ${item.part}" else ""
                            Column(
                                modifier = Modifier
                                    .padding(top = 2.dp, bottom = 2.dp)
                                    .clickable {
                                        onEvents(
                                            ExtendedSearchTitlesScreenEvents.PushTitle(
                                                item.id
                                            )
                                        )
                                    }
                            ) {
                                ElevatedCard {
                                    CoilImageLoader(
                                        titleImage = item.poster_thumb,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(7f / 10f)
                                    )
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
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Message(text = stringResource(R.string.no_titles_found_for_this_filter))
            }
        }
    }
}