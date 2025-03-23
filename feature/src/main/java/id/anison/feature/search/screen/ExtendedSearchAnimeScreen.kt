package id.anison.feature.search.screen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
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
import id.anison.data.settings.AppSettings
import id.anison.feature.MainScreenModel
import id.anison.feature.MainUIState
import id.anison.feature.R
import id.anison.feature.anime.AnimeDetailsScreen
import id.anison.feature.anime.model.AnimeUI
import id.anison.feature.common.component.layout.AmanogawaTopBar
import id.anison.feature.common.component.ErrorMessage
import id.anison.feature.common.component.Message
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.common.component.card.AnimeCard
import id.anison.feature.profile.SettingsUIState
import id.anison.feature.search.event.ExtendedSearchTitlesScreenEvents
import id.anison.feature.search.screenmodel.ExtendedSearchScreenModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExtendedSearchTitlesScreen(
    private val filterName: String? = null
) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val extendedSearchScreenModel =
            navigator.koinNavigatorScreenModel<ExtendedSearchScreenModel>()
        val mainScreenModel = koinScreenModel<MainScreenModel>()
        val extendedSearchState by extendedSearchScreenModel.extendedSearchState.collectAsState()
        val appSettingsState by mainScreenModel.appSettings.collectAsState()

        ExtendedListContent(
            topBarTitle = filterName,
            onEvents = { event ->
                when (event) {
                    is ExtendedSearchTitlesScreenEvents.PopBack -> {
                        navigator.pop()
                    }

                    is ExtendedSearchTitlesScreenEvents.PushTitle -> {
                        navigator.push(AnimeDetailsScreen(event.id))
                    }

                    is ExtendedSearchTitlesScreenEvents.Refresh -> {
                        extendedSearchScreenModel.refresh(filterName)
                    }
                }
            },
            state = if (filterName == null) {
                extendedSearchState.filteredTitles
            } else {
                extendedSearchState.titlesByLink
            },
            appSettings = when (appSettingsState) {
                is MainUIState.Loading -> {
                    AppSettings()
                }

                is MainUIState.Success -> {
                    (appSettingsState as MainUIState.Success).settings
                }
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
    state: StateFlow<PagingData<AnimeUI>>,
) {
    val pagingItems = state.collectAsLazyPagingItems()
    val lazyGridState = rememberLazyGridState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        AmanogawaTopBar(
            title = topBarTitle ?: stringResource(R.string.found_titles),
            popNavigate = { onEvents(ExtendedSearchTitlesScreenEvents.PopBack) },
            scrollBehavior = scrollBehavior,
            modifier = Modifier.clickable {
                scope.launch {
                    lazyGridState.animateScrollToItem(0)
                }
            },
        )
        AllTitles(
            onEvents = onEvents,
            pagingItems = pagingItems,
            lazyGridState = lazyGridState,
            appSettings = appSettings,
        )
    }
}

@Composable
internal fun AllTitles(
    onEvents: (ExtendedSearchTitlesScreenEvents) -> Unit,
    appSettings: AppSettings,
    pagingItems: LazyPagingItems<AnimeUI>,
    lazyGridState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    when (pagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            ProgressIndicator()
        }

        is LoadState.Error -> {
            ErrorMessage(onUpdate = { onEvents(ExtendedSearchTitlesScreenEvents.Refresh) })
        }

        is LoadState.NotLoading -> {
            val configuration = LocalConfiguration.current
            val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            val count = if (!isLandscape) {
                appSettings.countColumnsInRow
            } else {
                appSettings.countColumnsInRowLandscape
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(count),
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                state = lazyGridState,
                verticalArrangement = Arrangement.spacedBy(4.dp),
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
                                onEvents(ExtendedSearchTitlesScreenEvents.PushTitle(item.animeId))
                            },
                            isCurrent = false,
                        )
                    }
                }
            }
        }
    }
}