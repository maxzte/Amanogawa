package id.anison.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.data.settings.AppSettings
import id.anison.feature.anime.AnimeDetailsScreen
import id.anison.feature.common.DataState
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.common.component.card.AnimeCard
import id.anison.feature.profile.SettingsScreenModel
import id.anison.feature.profile.SettingsUIState


class CurrentTitlesScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<HomeScreenModel>()
        val settingsScreenModel = koinScreenModel<SettingsScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val state by screenModel.animeState.collectAsState()
        val appSettingsState by settingsScreenModel.appSettings.collectAsState()
        CurrentTitlesScreenContent(
            state = state,
            onEvents = { event ->
                when (event) {
                    is HomeScreenEvents.PushAnime -> {
                        navigator.push(AnimeDetailsScreen(event.animeId))
                    }

                    is HomeScreenEvents.ObserveConnection -> {}
                    HomeScreenEvents.OnRefresh -> {}
                    else -> {}
                }
            },
            appSettings = when (appSettingsState) {
                is SettingsUIState.Loading -> {
                    AppSettings()
                }

                is SettingsUIState.Success -> {
                    (appSettingsState as SettingsUIState.Success).settings
                }
            },
        )
    }

    @Composable
    internal fun CurrentTitlesScreenContent(
        state: HomeState,
        onEvents: (HomeScreenEvents) -> Unit,
        appSettings: AppSettings,
        modifier: Modifier = Modifier,
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val count = if (!isLandscape) {
            appSettings.countColumnsInRow
        } else {
            appSettings.countColumnsInRowLandscape
        }
        when (val titlesState = state.currentAnimeState) {
            is DataState.Error -> {

            }

            is DataState.Loading -> {
                ProgressIndicator()
            }

            is DataState.Success -> {
                val titles = titlesState.data
                Box(
                    modifier = Modifier.padding(horizontal = 8.dp).fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(count),
                        modifier = modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            count = titles.size,
                            key = { title -> titles[title].animeId }
                        ) { index ->
                            val anime = titles[index]
                            AnimeCard(
                                anime = anime,
                                onClick = {
                                    onEvents(HomeScreenEvents.PushAnime(animeId = anime.animeId))
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