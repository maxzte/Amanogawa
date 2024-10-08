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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.R
import id.anison.feature.common.DataState
import id.anison.feature.common.component.CoilImageLoader
import id.anison.feature.common.component.CustomTopAppBar
import id.anison.feature.common.component.DetailText
import id.anison.feature.common.component.DetailTextWithLinks
import id.anison.feature.common.component.ErrorMessage
import id.anison.feature.common.component.ExpandedText
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.common.component.TitleActions
import id.anison.feature.common.component.TitleImages
import id.anison.feature.common.component.UserRatingBar
import id.anison.feature.common.component.button.CircleButton
import id.anison.feature.common.component.button.RectangleButton
import id.anison.feature.model.AnimeUI
import id.anison.feature.model.EpisodeUI
import id.anison.feature.model.FilterUI
import id.anison.feature.model.GroupFUI
import id.anison.feature.search.screen.ExtendedSearchTitlesScreen
import id.anison.feature.search.screenmodel.ExtendedSearchScreenModel
import kotlinx.coroutines.launch

data class AnimeScreen(val id: Long) : Screen, Activity() {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        AnimeScreenContent(id, navigator)
    }

    @Composable
    private fun AnimeScreenContent(id: Long, navigator: Navigator) {
        val screenModel = koinScreenModel<AnimeScreenModel>()
        val searchScreenModel = koinScreenModel<ExtendedSearchScreenModel>()
        LaunchedEffect(id) { screenModel.getAnime(id) }
        val state by screenModel.animeState.collectAsState()
        when (val anime = state.anime) {
            is DataState.Error -> ErrorMessage(onUpdate = { screenModel.refresh(id) })
            is DataState.Loading -> ProgressIndicator()
            is DataState.Success -> {
                val isFavorite by screenModel.isFavorite(id).collectAsState(initial = false)
                when (val episodes = state.episodes) {
                    is DataState.Success -> {
                        AnimeMainScreen(
                            anime = anime.data,
                            episodes = episodes.data,
                            isFavorite = isFavorite,
                            onEvents = { event ->
                                when (event) {
                                    is AnimeScreenEvents.PopBack -> navigator.pop()
                                    is AnimeScreenEvents.PushAnimeScreen -> navigator.push(
                                        AnimeScreen(event.id)
                                    )

                                    is AnimeScreenEvents.AddToFavorite -> screenModel.addToFavorite(
                                        event.anime
                                    )

                                    is AnimeScreenEvents.RemoveFromFavorite -> screenModel.removeFromFavorite(
                                        event.anime
                                    )

                                    is AnimeScreenEvents.OnLinkClick -> {
                                        searchScreenModel.search(event.filter)
                                        navigator.push(ExtendedSearchTitlesScreen(event.filterName))
                                    }

                                    is AnimeScreenEvents.Refresh -> screenModel.refresh(event.id)
                                }
                            },
                        )
                    }

                    else -> ProgressIndicator()
                }
            }

            else -> Unit
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AnimeMainScreen(
        anime: AnimeUI,
        episodes: List<EpisodeUI>,
        isFavorite: Boolean,
        onEvents: (AnimeScreenEvents) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val lazyListState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        var sizeImage by remember { mutableStateOf(IntSize.Zero) }
        val gradient = Brush.verticalGradient(
            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
            startY = sizeImage.height.toFloat() / 1.3f,
            endY = sizeImage.height.toFloat()
        )
        val isTopBarVisible by remember { derivedStateOf { lazyListState.firstVisibleItemIndex == 0 } }
        Scaffold(
            topBar = {
                AnimatedVisibility(
                    visible = !isTopBarVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
                ) {
                    CustomTopAppBar(
                        title = anime.name,
                        popNavigate = { onEvents(AnimeScreenEvents.PopBack) },
                        actionBtnContent = {
                            CircleButton(
                                onClick = {
                                    if (isFavorite) onEvents(
                                        AnimeScreenEvents.RemoveFromFavorite(
                                            anime
                                        )
                                    )
                                    else onEvents(AnimeScreenEvents.AddToFavorite(anime))
                                },
                                containerColor = MaterialTheme.colorScheme.background,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    tint = if (isFavorite) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.primary,
                                    contentDescription =
                                    if (isFavorite) stringResource(id = R.string.unliked)
                                    else stringResource(id = R.string.liked)
                                )
                            }
                        },
                        modifier = Modifier.clickable {
                            coroutineScope.launch { lazyListState.animateScrollToItem(0) }
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isLandscape) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            CoilImageLoader(
                                titleImage = anime.poster.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(7f / 10f)
                                    .onGloballyPositioned { sizeImage = it.size },
                            )
                            TitleActions(
                                anime = anime,
                                isLiked = isFavorite,
                                onEvents = onEvents,
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(gradient)
                            )
                            Row(
                                modifier = modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                //.padding(bottom = 8.dp, top = 32.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = anime.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                UserRatingBar(rating = anime.rating)
                            }
                        }
                    }
                } else {
                    item {
                        TitleActions(
                            anime = anime,
                            isLiked = isFavorite,
                            isLandscape = true,
                            onEvents = onEvents,
                        )
                    }
                }
                item { Anime(anime = anime, onEvents = onEvents) }
                item { TitleImages(anime.screens_f.map { it[0] }) }
                if (!anime.group_f.isNullOrEmpty()) {
                    item { AnimeCollection(data = anime.group_f, onEvents = onEvents) }
                }
                item {
                    EpisodesContent(
                        isMovie = anime.is_movie,
                        episodes = episodes,
                        botUrl = anime.bot_url,
                        torrentUrl = anime.torrent_url.toString(),
                    )
                }
                item {  }
            }
        }
    }

    @Composable
    private fun Anime(
        anime: AnimeUI,
        onEvents: (AnimeScreenEvents) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val season = if (anime.season != 0) anime.season else ""
        val part = if (anime.part != 0) anime.part else ""
        val extra = if (anime.extra_total != 0) anime.extra_total else ""
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            DetailText(label = stringResource(R.string.original_name), value = anime.en_jp_name)
            Row {
                Text(
                    text = stringResource(R.string.year),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = anime.year.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = modifier
                        .clickable {
                            onEvents(
                                AnimeScreenEvents.OnLinkClick(
                                    filter = FilterUI(years = listOf(anime.year)),
                                    filterName = anime.year.toString()
                                )
                            )
                        }
                )
            }
            if (anime.is_movie) DetailText(
                label = stringResource(R.string.type),
                value = stringResource(R.string.movie)
            ) else {
                DetailText(
                    label = stringResource(R.string.type),
                    value = stringResource(R.string.series)
                )
                if (season != "") DetailText(
                    label = stringResource(R.string.season),
                    value = season.toString()
                )
                if (part != "") DetailText(
                    label = stringResource(R.string.part_two_dots),
                    value = part.toString()
                )
                if (extra != "") DetailText(
                    label = stringResource(R.string.episodes),
                    value = stringResource(
                        R.string.episodes_of_extra,
                        anime.episode_count,
                        anime.episodes_total.toString(),
                        extra
                    )
                )
                else DetailText(
                    label = stringResource(R.string.episodes),
                    value = stringResource(
                        R.string.episodes_of,
                        anime.episode_count,
                        anime.episodes_total.toString()
                    )
                )
            }
            DetailText(
                label = stringResource(R.string.duration),
                value = anime.duration.toString()
            )
            DetailTextWithLinks(
                label = stringResource(R.string.genres),
                genres = anime.genres_f,
                onWordClick = { filter ->
                    onEvents(
                        AnimeScreenEvents.OnLinkClick(
                            filter = filter,
                            filterName = filter.genres[0].name
                        )
                    )
                }
            )
            if (anime.company.isNotBlank()) DetailText(
                label = stringResource(R.string.company),
                value = anime.company
            )
            if (anime.director.isNotBlank()) DetailText(
                label = stringResource(R.string.director),
                value = anime.director
            )
            Spacer(modifier = modifier.height(16.dp))
            DetailTextWithLinks(
                label = stringResource(R.string.translated),
                t = anime.transl_f,
                onWordClick = { filter ->
                    onEvents(
                        AnimeScreenEvents.OnLinkClick(
                            filter = filter,
                            filterName = filter.dubers.t[0].name
                        )
                    )
                },
            )
            DetailTextWithLinks(
                label = stringResource(R.string.voiced),
                v = anime.voices_f,
                onWordClick = { filter ->
                    onEvents(
                        AnimeScreenEvents.OnLinkClick(
                            filter = filter,
                            filterName = filter.dubers.v[0].name
                        )
                    )
                },
            )
            DetailTextWithLinks(
                label = stringResource(R.string.work_with_sound),
                s = anime.sounds_f,
                onWordClick = { filter ->
                    onEvents(
                        AnimeScreenEvents.OnLinkClick(
                            filter = filter,
                            filterName = filter.dubers.s[0].name
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
//    }
    }

    @Composable
    private fun AnimeCollection(
        data: List<GroupFUI>,
        onEvents: (AnimeScreenEvents) -> Unit,
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
                        CollectionItem(item, onEvents)
                    }
                }
            }
        }
    }

    @Composable
    private fun CollectionItem(
        item: GroupFUI,
        onEvents: (AnimeScreenEvents) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val season = if (item.season != 0) item.season else ""
        val part = if (item.part != 0) "Part ${item.part}" else ""
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, bottom = 2.dp)
                .clickable { onEvents(AnimeScreenEvents.PushAnimeScreen(item.id)) },
        ) {
            CoilImageLoader(
                titleImage = item.poster_thumb.toString(),
                modifier = Modifier
                    .aspectRatio(7f / 10f)
                    .fillMaxWidth()
            )
            Text(
                text = "${item.name} $season",
                maxLines = 2,
                style = MaterialTheme.typography.titleSmall,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (part.isNotBlank()) Text(
                    text = part,
                    style = MaterialTheme.typography.bodySmall,
                )
                Row(
                    modifier = modifier.padding(horizontal = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.views.toString(),
                        style = MaterialTheme.typography.bodySmall,
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

    @Composable
    private fun EpisodesContent(
        isMovie: Boolean,
        episodes: List<EpisodeUI>,
        botUrl: String,
        torrentUrl: String,
        modifier: Modifier = Modifier,
    ) {
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val uriHandler = LocalUriHandler.current
        val itemWidth = (configuration.screenWidthDp  - 64) / 3
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (!isLandscape) {
                RectangleButton(
                    title = stringResource(R.string.watch_btn),
                    iconContent = {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = stringResource(R.string.play_icon)
                        )
                    },
                    onClick = { uriHandler.openUri(botUrl + "?start=sep_" + episodes[0].bot_id) },
                    modifier = modifier.fillMaxWidth(),
                )
                RectangleButton(
                    title = stringResource(R.string.download_btn),
                    onClick = { uriHandler.openUri(torrentUrl) },
                    iconContent = {
                        Icon(
                            painter = painterResource(R.drawable.ic_download_24),
                            contentDescription = stringResource(R.string.download_icon)
                        )
                    },
                    modifier = modifier.fillMaxWidth()
                )
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
    fun EpisodeGrid(
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

    @Composable
    fun EpisodeCard(
        episode: EpisodeUI,
        botUrl: String,
        modifier: Modifier = Modifier,
    ) {
        val uriHandler = LocalUriHandler.current
        ElevatedCard(
            modifier = modifier
                .clickable { uriHandler.openUri(botUrl + "?start=sep_${episode.bot_id}") }
        ) {
            CoilImageLoader(
                modifier = Modifier.aspectRatio(16f / 9f),
                titleImage = episode.screen_original
            )
            Text(
                text = "${episode.number} ${episode.name}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}