package id.anison.feature.search.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
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
import id.anison.feature.common.component.DetailText
import id.anison.feature.common.component.DetailTextWithLinks
import id.anison.feature.common.component.ErrorMessage
import id.anison.feature.common.component.ExpandedText
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.common.component.TitleActions
import id.anison.feature.common.component.TitleHead
import id.anison.feature.common.component.TitleImages
import id.anison.feature.model.AnimeUI
import id.anison.feature.model.FilterUI
import id.anison.feature.search.event.RandomScreenEvents
import id.anison.feature.search.screenmodel.ExtendedSearchScreenModel
import id.anison.feature.search.screenmodel.RandomTitleScreenModel

class RandomTitlesCarouselScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("StateFlowValueCalledInComposition", "SuspiciousIndentation")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<RandomTitleScreenModel>()
        val searchScreenModel = koinScreenModel<ExtendedSearchScreenModel>()
        val state = screenModel.randomTitleState.collectAsState().value
        when (state.animeIDs) {
            is DataState.Error -> ErrorMessage(onUpdate = { screenModel.refresh() })
            is DataState.Loading -> ProgressIndicator()
            is DataState.Success -> {
                when (val randomAnime = state.randomAnime) {
                    is DataState.Loading -> ProgressIndicator()
                    is DataState.Error -> ErrorMessage(onUpdate = { screenModel.refresh() })
                    is DataState.Success -> {
                        CarouselTitlesContent(
                            anime = randomAnime.data,
                            navigator = navigator,
                            screenModel = screenModel,
                            onEvents = { event ->
                                when (event) {
                                    is RandomScreenEvents.PopBack -> navigator.pop()
                                    is RandomScreenEvents.Next -> screenModel.getRandomAnime()
                                    is RandomScreenEvents.IsLast -> screenModel.isLast()
                                    is RandomScreenEvents.OnLinkClick -> {
                                        searchScreenModel.search(event.filter)
                                        navigator.push(ExtendedSearchTitlesScreen(event.filterName))
                                    }
                                }
                            },
                        )
                    }
                }
            }

            null -> Unit
        }
    }

    @Composable
    private fun CarouselTitlesContent(
        anime: AnimeUI,
        screenModel: RandomTitleScreenModel,
        navigator: Navigator,
        onEvents: (RandomScreenEvents) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val scrollState = rememberScrollState()
        val configuration = LocalConfiguration.current
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val isCompact = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
        val backgroundHeight = if (isCompact) (configuration.screenHeightDp / 2.4).toInt()
        else configuration.screenHeightDp
        val backgroundGradient = Brush.verticalGradient(
            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
        )
        Column(
            modifier = modifier
                .verticalScroll(state = scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = backgroundHeight.dp + 32.dp)
            ) {
                CoilImageLoader(
                    titleImage = anime.poster.toString(),
                    radius = 35,
                )
                Box(
                    Modifier
                        .matchParentSize()
                        .background(backgroundGradient)
                )
                TitleActions(
                    onBackClick = { navigator.pop() },
                    actionIconContent = {
                        IconButton(onClick = { onEvents(RandomScreenEvents.Next) }) {
                            if (screenModel.isLast()) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_replay_24px),
                                    contentDescription = stringResource(id = R.string.arrow_replay),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                                    contentDescription = stringResource(id = R.string.arrow_forward),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                )
                TitleHead(
                    anime = anime,
                    navigator = navigator,
                )
            }
            TitleDetails(anime, modifier, onEvents = onEvents)
            TitleImages(anime.screens_f.map { imgLink -> imgLink[0] })
            Spacer(modifier = Modifier.height(0.dp))
        }
    }

    @Composable
    fun TitleDetails(
        title: AnimeUI,
        modifier: Modifier = Modifier,
        onEvents: (RandomScreenEvents) -> Unit,
    ) {
        Column(modifier = modifier.padding(8.dp)) {
            val season = if (title.season != 0) title.season else ""
            val part = if (title.part != 0) title.part else ""
            val extra = if (title.extra_total != 0) title.extra_total else ""
            Row {
                Text(
                    text = stringResource(R.string.year),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = title.year.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = modifier
                        .clickable {
                            onEvents(
                                RandomScreenEvents.OnLinkClick(
                                    filter = FilterUI(years = listOf(title.year)),
                                    filterName = title.year.toString()
                                )
                            )
                        }
                )
            }
            if (title.is_movie) DetailText(
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
                        title.episode_count,
                        title.episodes_total.toString(),
                        extra
                    )
                )
                else DetailText(
                    label = stringResource(R.string.episodes),
                    value = stringResource(
                        R.string.episodes_of,
                        title.episode_count,
                        title.episodes_total.toString()
                    )
                )
            }
            DetailText(
                label = stringResource(id = R.string.duration),
                value = title.duration.toString()
            )
            DetailTextWithLinks(
                label = stringResource(R.string.genres),
                genres = title.genres_f,
                onWordClick = { filter ->
                    onEvents(
                        RandomScreenEvents.OnLinkClick(
                            filter = filter,
                            filterName = filter.genres[0].name
                        )
                    )
                }
            )
            if (title.company.isNotBlank()) DetailText(
                label = stringResource(id = R.string.company),
                value = title.company
            )
            if (title.director.isNotBlank()) DetailText(
                label = stringResource(id = R.string.director),
                value = title.director
            )
            Text(
                text = stringResource(id = R.string.description),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
            )
            ExpandedText(title.description)
        }
    }
}
