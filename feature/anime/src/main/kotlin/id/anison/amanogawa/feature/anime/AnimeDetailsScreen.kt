@file:OptIn(ExperimentalMaterial3Api::class)

package id.anison.amanogawa.feature.anime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import id.anison.amanogawa.core.model.AnimeDetailsUI
import id.anison.amanogawa.core.model.AnimeEpisodeUI
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.navigation.NavigationHandler
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.common.DataStateContent
import id.anison.amanogawa.core.ui.common.component.AmanogawaHorizontalPager
import id.anison.amanogawa.core.ui.common.component.AmanogawaSpinner
import id.anison.amanogawa.core.ui.common.component.AnimeCard
import id.anison.amanogawa.core.ui.common.component.AnimeImageLoader
import id.anison.amanogawa.core.ui.common.component.DetailText
import id.anison.amanogawa.core.ui.common.component.ErrorMessage
import id.anison.amanogawa.core.ui.common.component.ExpandedText
import id.anison.amanogawa.core.ui.common.component.ImageHorizontalPager
import id.anison.amanogawa.core.ui.common.component.button.FilledTopBarIcon
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.component.layout.showWebPage
import id.anison.amanogawa.core.ui.common.ignoreHorizontalParentPadding
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.core.ui.spacing
import id.anison.amanogawa.feature.anime.ui.AnimeHeader
import id.anison.amanogawa.feature.anime.ui.AnimeTeam
import kotlinx.coroutines.flow.first
import org.koin.core.parameter.parametersOf

class AnimeDetailsScreen(
    private val animeId: Long,
) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val screenModel: AnimeDetailsScreenModel = koinScreenModel(
            parameters = { parametersOf(animeId) }
        )
        val state by screenModel.state.collectAsStateWithLifecycle()
        val lazyGridState = screenModel.lazyGridState

        LaunchedEffect(Unit) {
            screenModel.scrollToTopEvent.collect {
                snapshotFlow { lazyGridState.layoutInfo.totalItemsCount }
                    .first { it > 0 }

                lazyGridState.scrollToItem(0)
            }
        }

        NavigationHandler(
            navigationState = screenModel.navEventState,
            toScreen = { toScreen() }
        )

        DataStateContent(
            state = state.anime,
            errorContent = {
                ErrorMessage(onUpdate = { screenModel.onEvents(AnimeDetailsScreenEvents.Refresh) })
            },
            loadingContent = {
                AmanogawaSpinner()
            },
            content = { anime ->
                AnimeDetailsScreenContent(
                    anime = anime.anime,
                    episodes = anime.episodes.data,
                    isFavorite = state.isFavorite,
                    onEvents = { screenModel.onEvents(it) },
                    lazyGridState = lazyGridState,
                )
            }
        )
    }

    @Composable
    private fun AnimeDetailsScreenContent(
        anime: AnimeDetailsUI,
        episodes: List<AnimeEpisodeUI>,
        isFavorite: Boolean,
        onEvents: (AnimeDetailsScreenEvents) -> Unit,
        lazyGridState: LazyGridState,
    ) {
        val mediumSpacing = MaterialTheme.spacing.medium
        Box(contentAlignment = Alignment.TopCenter) {
            Scaffold(
                topBar = {
                    val isTopBarVisible by remember { derivedStateOf { lazyGridState.firstVisibleItemIndex == 0 } }
                    DetailsTopBar(
                        anime = anime,
                        isFavorite = isFavorite,
                        isTopBarVisible = isTopBarVisible,
                        onEvents = onEvents
                    )
                },
            ) { paddingValues ->
                LazyVerticalGrid(
                    state = lazyGridState,
                    columns = GridCells.Adaptive(AmanogawaConst.minCardWidth(isLandscape()).dp),
                    verticalArrangement = Arrangement.spacedBy(mediumSpacing),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    contentPadding = PaddingValues(
                        start = mediumSpacing,
                        end = mediumSpacing,
                        top = 0.dp,
                        bottom = if (!isLandscape()) paddingValues.calculateBottomPadding() + AmanogawaConst.bottomBarInsets
                        else paddingValues.calculateBottomPadding()
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(
                        key = "animeHeader",
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        AnimeHeader(
                            anime = anime,
                            onEvents = onEvents,
                            modifier = Modifier.ignoreHorizontalParentPadding(mediumSpacing)
                        )
                    }

                    item(
                        key = "animeDetails",
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        AnimeDetailsSection(
                            animeDetails = anime,
                            onEvents = onEvents,
                        )
                    }

                    item(
                        key = "imageHorizontalPager",
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        ImageHorizontalPager(
                            images = anime.screensF,
                            onImageClick = { index: Int, images: List<String> ->
                                onEvents(AnimeDetailsScreenEvents.OnImageClick(index, images))
                            },
                            modifier = Modifier.ignoreHorizontalParentPadding(mediumSpacing),
                        )
                    }

                    if (!anime.groupF.isNullOrEmpty()) {
                        item(
                            key = "collectionHorizontalPager",
                            span = { GridItemSpan(maxLineSpan) },
                        ) {
                            val pagerState = rememberPagerState(
                                pageCount = { anime.groupF!!.size }
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .ignoreHorizontalParentPadding(MaterialTheme.spacing.medium),
                            ) {
                                Text(
                                    text = stringResource(id = R.string.collection),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(start = MaterialTheme.spacing.medium)
                                )

                                AmanogawaHorizontalPager(
                                    modifier = Modifier.fillMaxWidth(),
                                    pagerState = pagerState,
                                    list = anime.groupF!!,
                                    key = {
                                        anime.groupF!![it].animeId
                                    },
                                    pageContent = { animeItem ->
                                        AnimeCard(
                                            title = animeItem.name,
                                            poster = animeItem.posterThumb,
                                            year = animeItem.year,
                                            season = animeItem.season,
                                            part = animeItem.part,
                                            views = animeItem.views,
                                            timeStr = animeItem.timeStr,
                                            schedule = animeItem.schedule,
                                            isCurrent = false,
                                            onClick = {
                                                onEvents(
                                                    AnimeDetailsScreenEvents.PushAnimeDetailsScreen(
                                                        animeId = animeItem.animeId
                                                    )
                                                )
                                            },
                                        )
                                    },
                                )
                            }
                        }
                    }

                    if (!anime.isMovie) {
                        item(
                            key = "episodesText",
                            span = { GridItemSpan(maxLineSpan) },
                        ) {
                            Text(
                                text = stringResource(R.string.episodes),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }

                        items(
                            items = episodes,
                            key = { it.episodeId },
                        ) { episode ->
                            EpisodeCard(
                                episode = episode,
                                botUrl = anime.botUrl,
                                onEvents = onEvents,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsTopBar(
    anime: AnimeDetailsUI,
    isFavorite: Boolean,
    isTopBarVisible: Boolean,
    onEvents: (AnimeDetailsScreenEvents) -> Unit,
) {
    val topBarColor = if (isTopBarVisible) MaterialTheme.colorScheme.background.copy(alpha = 0f)
    else MaterialTheme.colorScheme.surface

    AmanogawaTopBar(
        titleContent = {
            AnimatedVisibility(
                visible = !isTopBarVisible,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Text(
                    text = anime.animeUI.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        },
        navigationIconContent = {
            FilledTopBarIcon(
                onClick = { onEvents(AnimeDetailsScreenEvents.PopBack) },
                backgroundColor = if (isTopBarVisible) {
                    MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                } else Color.Transparent,
                content = {
                    Icon(
                        imageVector = AmanogawaIcon.arrowBack,
                        contentDescription = stringResource(id = R.string.arrow_back),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            )
        },
        additionalActionBtnContent = {
            FilledTopBarIcon(
                onClick = { onEvents(AnimeDetailsScreenEvents.ToggleFavorite(anime)) },
                backgroundColor = if (isTopBarVisible) {
                    MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                } else Color.Transparent,
                content = {
                    Icon(
                        imageVector = AmanogawaIcon.favorite,
                        tint = if (isFavorite) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        contentDescription = if (isFavorite) {
                            stringResource(id = R.string.unliked)
                        } else {
                            stringResource(id = R.string.liked)
                        },
                    )
                }
            )
        },
        containerColor = topBarColor,
        showBottomLine = false,
        modifier = Modifier
            .then(
                if (!isTopBarVisible) {
                    Modifier.clickable {
                        onEvents(AnimeDetailsScreenEvents.ScrollToTop)
                    }
                } else Modifier
            )
    )
}

@Composable
private fun AnimeDetailsSection(
    animeDetails: AnimeDetailsUI,
    onEvents: (AnimeDetailsScreenEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        DetailText(
            label = stringResource(id = R.string.type),
            value = stringResource(id = if (animeDetails.isMovie) R.string.movie else R.string.series)
        )

        if (!animeDetails.isMovie) {
            DetailOptional(
                label = stringResource(id = R.string.season),
                value = animeDetails.animeUI.season
            )

            DetailOptional(
                label = stringResource(id = R.string.part_two_dots),
                value = animeDetails.animeUI.part
            )

            DetailEpisodes(anime = animeDetails, extra = animeDetails.animeUI.extraTotal)
        }

        DetailOptional(
            label = stringResource(id = R.string.company),
            value = animeDetails.company
        )

        DetailOptional(
            label = stringResource(id = R.string.director),
            value = animeDetails.director
        )

        Spacer(modifier = modifier.height(MaterialTheme.spacing.medium))

        AnimeTeam(
            teamMembers = animeDetails.teamMembers,
            onWordClick = { filterId: Long, filterName: String, filterType: FilterTypeUI ->
                onEvents(
                    AnimeDetailsScreenEvents.OnLinkClick(
                        filterId = filterId,
                        filterName = filterName,
                        filterType = filterType,
                    )
                )
            },
        )

        Spacer(modifier = modifier.height(MaterialTheme.spacing.medium))

        Text(
            text = stringResource(id = R.string.description),
            style = MaterialTheme.typography.titleLarge,
        )

        ExpandedText(description = animeDetails.description)
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

        anime.animeUI.ovaTotal.isNotEmpty() -> stringResource(
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

    DetailText(label = stringResource(id = R.string.episodes_two_dots), value = episodesText)
}

@Composable
private fun EpisodeCard(
    episode: AnimeEpisodeUI,
    botUrl: String,
    onEvents: (AnimeDetailsScreenEvents) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val color = MaterialTheme.colorScheme.surface.toArgb()

    ElevatedCard(
        modifier = modifier
            .clickable {
                onEvents(AnimeDetailsScreenEvents.OnEpisodeClick(episode))
                showWebPage(
                    context = context,
                    uri = (botUrl + "?start=sep_${episode.botId}").toUri(),
                    toolbarColor = color
                )
            }
    ) {
        AnimeImageLoader(
            modifier = Modifier.aspectRatio(16f / 9f),
            titleImage = episode.screen
        )
        Text(
            text = if (episode.isOva) {
                "OVA " + "${episode.number} ${episode.name}"
            } else {
                "${episode.number} ${episode.name}"
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(MaterialTheme.spacing.extraSmall)
        )
    }
}
