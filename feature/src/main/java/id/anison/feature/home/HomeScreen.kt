package id.anison.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
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
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.data.settings.AppSettings
import id.anison.feature.anime.AnimeDetailsScreen
import id.anison.feature.anime.model.AnimeUI
import id.anison.feature.common.Chip
import id.anison.feature.common.DataState
import id.anison.feature.common.component.ErrorMessage
import id.anison.feature.common.component.FilterChipRow
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.common.component.TitleSection
import id.anison.feature.common.component.card.AnimeCard
import id.anison.feature.common.component.layout.AmanogawaTopBar
import id.anison.feature.profile.SettingsScreenModel
import id.anison.feature.profile.SettingsUIState

class HomeScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HomeScreenModel>()
        val settingsScreenModel = koinScreenModel<SettingsScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val state by screenModel.animeState.collectAsState()
        val animeTitles = screenModel.pagingAnimeState.collectAsLazyPagingItems()
        val appSettingsState by settingsScreenModel.appSettings.collectAsState()

        HomeScreenContent(
            animeTitles = animeTitles,
            state = state,
            settingsScreenModel = settingsScreenModel,
            appSettings = when (appSettingsState) {
                is SettingsUIState.Loading -> {
                    AppSettings()
                }

                is SettingsUIState.Success -> {
                    (appSettingsState as SettingsUIState.Success).settings
                }
            },
            onEvents = { event ->
                when (event) {
                    is HomeScreenEvents.PushAnime -> {
                        navigator.push(AnimeDetailsScreen(event.animeId))
                    }

                    is HomeScreenEvents.SetChipFilter -> {
                        screenModel.setSelectedFilterChip(event.chipTitle)
                    }

                    is HomeScreenEvents.OnRefresh -> {
                        screenModel.refreshTitles()
                    }

                    is HomeScreenEvents.ObserveConnection -> {

                    }
                }
            },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    internal fun HomeScreenContent(
        animeTitles: LazyPagingItems<AnimeUI>,
        state: HomeState,
        appSettings: AppSettings,
        settingsScreenModel: SettingsScreenModel,
        onEvents: (HomeScreenEvents) -> Unit,
    ) {
        val allTitlesLazyGridState = rememberLazyGridState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val selectedFilter = state.selectedChipFilter

        Column(
            modifier = Modifier
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
        ) {
            AmanogawaTopBar(
                title = "Amanogawa",
                isLogoVisible = true,
                scrollBehavior = scrollBehavior,
            )
            FilterChipRow(
                selectedFilter = selectedFilter,
                onFilterSelected = {
                    onEvents(HomeScreenEvents.SetChipFilter(it))
                }
            )
            HorizontalDivider(color = Color.Gray)
            when (val currentAnimeList = state.currentAnimeState) {
                is DataState.Error -> {
                    ErrorMessage(
                        onUpdate = {
                            onEvents(HomeScreenEvents.OnRefresh)
                        }
                    )
                }

                is DataState.Loading -> {
                    ProgressIndicator()
                }

                is DataState.Success -> {
                    TitlesContent(
                        selectedFilter = selectedFilter,
                        onEvents = onEvents,
                        appSettings = appSettings,
                        titles = animeTitles,
                        currentTitles = currentAnimeList.data,
                        lazyGridState = allTitlesLazyGridState,
                    )
                }
            }
        }
    }

    @Composable
    fun TitlesContent(
        selectedFilter: String,
        onEvents: (HomeScreenEvents) -> Unit,
        appSettings: AppSettings,
        modifier: Modifier = Modifier,
        titles: LazyPagingItems<AnimeUI>,
        currentTitles: List<AnimeUI>,
        lazyGridState: LazyGridState,
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val countColumnsInRow = if (!isLandscape) {
            appSettings.countColumnsInRow
        } else {
            appSettings.countColumnsInRowLandscape
        }

        when (titles.loadState.refresh) {
            is LoadState.Loading -> {
                ProgressIndicator()
            }

            is LoadState.Error -> {
                titles.refresh()
            }

            is LoadState.NotLoading -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(countColumnsInRow),
                    modifier = modifier.fillMaxSize(),
                    state = lazyGridState,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
                ) {
                    if (selectedFilter == Chip.ONLY_CURRENT.title || selectedFilter == Chip.ALL.title) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            TitleSection("Current titles")
                        }

                        items(
                            count = currentTitles.size,
                            key = { title -> "current_${currentTitles[title].animeId}" }
                        ) { index ->
                            val anime = currentTitles[index]
                            AnimeCard(
                                anime = anime,
                                onClick = {
                                    onEvents(HomeScreenEvents.PushAnime(animeId = anime.animeId))
                                },
                                isCurrent = true,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }

                    if (selectedFilter == Chip.ONLY_ALL.title || selectedFilter == Chip.ALL.title) {

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            TitleSection("All titles")
                        }

                        items(
                            count = titles.itemCount,
                            key = titles.itemKey { it.animeId },
                        ) { index ->
                            val anime = titles[index]
                            if (anime != null) {
                                AnimeCard(
                                    anime = anime,
                                    onClick = {
                                        onEvents(HomeScreenEvents.PushAnime(animeId = anime.animeId))
                                    },
                                    isCurrent = false,
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
