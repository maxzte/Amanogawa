package id.anison.feature.anime

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.R
import id.anison.feature.anime.model.AnimeDetailsUI
import id.anison.feature.anime.model.AnimeUI
import id.anison.feature.common.DataState
import id.anison.feature.common.component.card.AnimeCard
import id.anison.feature.common.component.AnimeHeader
import id.anison.feature.common.component.AnimeImages
import id.anison.feature.common.component.AnimeTeam
import id.anison.feature.common.component.layout.AmanogawaTopBar
import id.anison.feature.common.component.DetailText
import id.anison.feature.common.component.card.EpisodeCard
import id.anison.feature.common.component.ErrorMessage
import id.anison.feature.common.component.ExpandedText
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.common.component.button.FavoriteButton
import id.anison.feature.anime.model.EpisodeUI
import id.anison.feature.MainScreenModel
import id.anison.feature.search.model.FilterUI
import id.anison.feature.search.screen.ExtendedSearchTitlesScreen
import id.anison.feature.search.screenmodel.ExtendedSearchScreenModel
import kotlinx.coroutines.launch
import kotlin.toString

class AnimeDetailsScreen(
    private val animeId: Long
) : Screen, Activity() {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val mainScreenModel = navigator.koinNavigatorScreenModel<MainScreenModel>()
        LaunchedEffect(Unit) {
            mainScreenModel.setTopBarVisible(false)
            mainScreenModel.setBottomBarVisible(true)
        }
        AnimeDetailsScreenContent(animeId, navigator, mainScreenModel)
    }

    @Composable
    private fun AnimeDetailsScreenContent(
        id: Long,
        navigator: Navigator,
        mainScreenModel: MainScreenModel,
    ) {

        val screenModel = koinScreenModel<AnimeDetailsScreenModel>()
        val searchScreenModel = koinScreenModel<ExtendedSearchScreenModel>()
        LaunchedEffect(id) {
            screenModel.loadAnime(id)
            screenModel.isFavorite(id)
        }

        val state by screenModel.animeState.collectAsState()
//        val isFavorite by screenModel.isFavorite(id).collectAsState(initial = false)
        val isFavorite = state.isFavorite
        when (val anime = state.anime) {
            is DataState.Error -> {
                ErrorMessage(onUpdate = { screenModel.loadAnime(id) })
            }

            is DataState.Loading -> {
                ProgressIndicator()
            }

            is DataState.Success -> {
                when (val episodes = state.episodes) {
                    is DataState.Error -> {
                        ErrorMessage(onUpdate = { screenModel.loadAnime(id) })
                    }

                    is DataState.Loading -> {
                        ProgressIndicator()
                    }

                    is DataState.Success -> {
                        AnimeMainScreen(
                            anime = anime.data,
                            episodes = episodes.data,
                            isFavorite = isFavorite,
                            onEvents = { event ->
                                when (event) {
                                    is AnimeDetailsScreenEvents.PopBack -> {
                                        mainScreenModel.setTopBarVisible(true)
                                        navigator.pop()
                                    }

                                    is AnimeDetailsScreenEvents.PushAnimeDetailsScreen -> {
                                        navigator.push(AnimeDetailsScreen(event.animeId))
                                    }

                                    is AnimeDetailsScreenEvents.ToggleFavorite -> {
                                        screenModel.toggleFavorite(event.anime)
                                    }

                                    is AnimeDetailsScreenEvents.OnLinkClick -> {
                                        searchScreenModel.search(event.filter)
                                        navigator.push(ExtendedSearchTitlesScreen(event.filterName))
                                    }

                                    is AnimeDetailsScreenEvents.Refresh -> {
                                        screenModel.loadAnime(event.animeId)
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AnimeMainScreen(
        anime: AnimeDetailsUI,
        episodes: List<EpisodeUI>,
        isFavorite: Boolean,
        onEvents: (AnimeDetailsScreenEvents) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val lazyListState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        val isTopBarVisible by remember { derivedStateOf { lazyListState.firstVisibleItemIndex == 0 } }
        val backgroundModifier = Modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        Color.Transparent
                    ),
                )
            )
        //.blur(15.dp)
        val overlayColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.95f)

        Scaffold(
            topBar = {
                AnimatedVisibility(
                    visible = !isTopBarVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
                ) {
                    AmanogawaTopBar(
                        title = anime.animeUI.name,
                        popNavigate = { onEvents(AnimeDetailsScreenEvents.PopBack) },
                        additionalActionBtnContent = {
                            FavoriteButton(
                                isFavorite = isFavorite,
                                onFavoriteClick = {
                                    onEvents(AnimeDetailsScreenEvents.ToggleFavorite(anime))
                                }
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.clickable {
                            coroutineScope.launch { lazyListState.animateScrollToItem(0) }
                        }
                    )
                }
            },
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AnimeHeader(
                        anime = anime,
                        isLandscape = isLandscape,
                        isFavorite = isFavorite,
                        onEvents = onEvents
                    )
                }
                item { AnimeDetails(anime = anime, onEvents = onEvents) }
                item { AnimeImages(anime.screensF.map { it[0] }) }
                if (!anime.groupF.isNullOrEmpty()) {
                    item { AnimeCollection(data = anime.groupF, onEvents = onEvents) }
                }
                item {
                    EpisodesContent(
                        isMovie = anime.isMovie,
                        episodes = episodes,
                        botUrl = anime.botUrl,
                    )
                }
                item { }
            }
        }
    }

    @Composable
    private fun AnimeDetails(
        anime: AnimeDetailsUI,
        onEvents: (AnimeDetailsScreenEvents) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val season = if (anime.animeUI.season.toInt() != 0) "${anime.animeUI.season}" else ""
        val part = if (anime.animeUI.part.toInt() != 0) "${anime.animeUI.part}" else ""
        val extra = if (anime.animeUI.extraTotal != 0) "${anime.animeUI.extraTotal}" else ""
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            DetailText(
                label = stringResource(R.string.type),
                value = stringResource(if (anime.isMovie) R.string.movie else R.string.series)
            )
            if (!anime.isMovie) {
                DetailOptional(label = stringResource(R.string.season), value = season)
                DetailOptional(label = stringResource(R.string.part_two_dots), value = part)
                DetailEpisodes(anime = anime, extra = extra)
            }
            DetailOptional(label = stringResource(R.string.company), value = anime.company)
            DetailOptional(label = stringResource(R.string.director), value = anime.director)
            Spacer(modifier = modifier.height(16.dp))
            AnimeTeam(
                dubers = anime.dubers,
                onWordClick = { filter: FilterUI, filterName: String ->
                    onEvents(
                        AnimeDetailsScreenEvents.OnLinkClick(
                            filter = filter,
                            filterName = filterName
                        )
                    )
                },
            )
            Spacer(modifier = modifier.height(16.dp))
            Text(
                text = stringResource(R.string.description),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            ExpandedText(anime.description)
        }
    }

    @Composable
    private fun DetailOptional(label: String, value: String) {
        if (value.isNotBlank()) {
            DetailText(label = label, value = value)
        }
    }

    @Composable
    private fun DetailEpisodes(
        anime: AnimeDetailsUI,
        extra: String,
    ) {
        val episodesText = when {
            extra.isNotBlank() -> stringResource(
                R.string.episodes_of_extra,
                anime.animeUI.episodeCount!!,
                anime.animeUI.episodesTotal.toString(),
                extra
            )

            anime.animeUI.ovaTotal > 0 -> stringResource(
                R.string.episodes_of,
                anime.animeUI.episodeCount!!,
                anime.animeUI.episodesTotal.toString() + " (+ ${anime.animeUI.ovaTotal} OVA)"
            )

            else -> {
                stringResource(
                    R.string.episodes_of,
                    anime.animeUI.episodeCount!!,
                    anime.animeUI.episodesTotal.toString()
                )
            }
        }

        DetailText(label = stringResource(R.string.episodes), value = episodesText)
    }

    @Composable
    private fun AnimeCollection(
        data: List<AnimeUI>,
        onEvents: (AnimeDetailsScreenEvents) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val itemWidth = if (isLandscape) configuration.screenWidthDp.dp / 6
        else configuration.screenWidthDp.dp / 3
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = stringResource(R.string.collection), fontWeight = FontWeight.ExtraBold)
            LazyRow(
                modifier = modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(data.size) { index ->
                    val item = data[index]
                    Column(modifier = modifier.widthIn(max = itemWidth)) {
                        AnimeCard(
                            anime = item,
                            onClick = {
                                onEvents(
                                    AnimeDetailsScreenEvents.PushAnimeDetailsScreen(
                                        item.animeId
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

    @Composable
    private fun EpisodesContent(
        isMovie: Boolean,
        episodes: List<EpisodeUI>,
        botUrl: String,
        modifier: Modifier = Modifier,
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val itemWidth = (configuration.screenWidthDp - 64) / 3
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (!isLandscape) {
                if (!isMovie) {
                    Text(
                        text = stringResource(R.string.episodes),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    EpisodeList(episodes, botUrl)
                }
            } else {
                if (!isMovie) {
                    Text(
                        text = stringResource(R.string.episodes),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    EpisodeGrid(episodes, botUrl, itemWidth)
                }
            }
        }
    }

    @Composable
    private fun EpisodeList(
        episodesList: List<EpisodeUI>,
        botUrl: String,
    ) {
        episodesList.forEach { EpisodeCard(it, botUrl) }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun EpisodeGrid(
        episodesList: List<EpisodeUI>,
        botUrl: String,
        itemWidth: Int,
        modifier: Modifier = Modifier
    ) {
        FlowRow(
            maxItemsInEachRow = 3,
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            episodesList.forEach {
                EpisodeCard(
                    episode = it,
                    botUrl = botUrl,
                    modifier = Modifier.widthIn(max = itemWidth.dp)
                )
            }
        }
    }
}