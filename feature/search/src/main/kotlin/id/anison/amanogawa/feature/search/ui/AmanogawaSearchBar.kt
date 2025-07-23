package id.anison.amanogawa.feature.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
import id.anison.amanogawa.core.model.AnimeDetailsUI
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.LineSide
import id.anison.amanogawa.core.ui.common.component.AnimeImageLoader
import id.anison.amanogawa.core.ui.common.component.DetailText
import id.anison.amanogawa.core.ui.common.component.ExpandedText
import id.anison.amanogawa.core.ui.common.grayLine
import id.anison.amanogawa.feature.search.screenmodel.RandomScreenEvents
import id.anison.amanogawa.feature.search.screenmodel.SearchScreenEvents


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmanogawaSearchBar(
    searchQuery: TextFieldValue,
    onEvents: (SearchScreenEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .windowInsetsPadding(SearchBarDefaults.windowInsets)
            .fillMaxWidth()
            .grayLine(side = LineSide.Bottom)
            .padding(start = 8.dp, top = 16.dp, end = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search icon",
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.text.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.search_anime),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        BasicTextField(
                            value = searchQuery,
                            onValueChange = {
                                onEvents(SearchScreenEvents.OnSearchQueryChange(it))
                            },
                            singleLine = true,
                            modifier = modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                        )
                    }

                    if (searchQuery.text.isNotBlank()) {
                        IconButton(
                            onClick = {
                                onEvents(SearchScreenEvents.OnSearchQueryChange(TextFieldValue("")))
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.clear),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        )
                    }
                }
            }
        }

        Row {
            IconButton(
                onClick = { onEvents(SearchScreenEvents.OnRandomButtonClick) },
                content = {
                    Icon(
                        painter = painterResource(R.drawable.ic_rounded_arrow_outward_24),
                        contentDescription = stringResource(R.string.filter_icon)
                    )
                }
            )

            IconButton(
                onClick = { onEvents(SearchScreenEvents.OnExtendedButtonClick) },
                content = {
                    Icon(
                        painter = painterResource(R.drawable.ic_filter_24),
                        contentDescription = stringResource(R.string.filter_icon)
                    )
                }
            )
        }
    }
}


@Composable
fun CarouselTitlesContent(
    anime: AnimeDetailsUI,
    isFavorite: Boolean,
    isLast: Boolean,
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
            AnimeImageLoader(
                titleImage = anime.poster,
//                radius = 35,
            )
            Box(
                Modifier
                    .matchParentSize()
                    .background(backgroundGradient)
            )
//            TitleActions(
//                title = anime.animeUI.name,
//                onBackClick = { onEvents(RandomScreenEvents.PopBack)/*navigator.pop()*/ },
//                actionIconContent = {
//                    IconButton(onClick = { onEvents(RandomScreenEvents.Next) }) {
//                        if (isLast) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_replay_24px),
//                                contentDescription = stringResource(id = R.string.arrow_replay),
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        } else {
//                            Icon(
//                                imageVector = Icons.AutoMirrored.Default.ArrowForward,
//                                contentDescription = stringResource(id = R.string.arrow_forward),
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    }
//                },
//            )
//            TitleHead(anime = anime)
        }
        TitleDetails(anime, modifier, onEvents = onEvents)
//        AnimeImages(anime.screensF.map { imgLink -> imgLink[0] })
        Spacer(modifier = Modifier.height(0.dp))
    }
}

@Composable
fun TitleDetails(
    title: AnimeDetailsUI,
    modifier: Modifier = Modifier,
    onEvents: (RandomScreenEvents) -> Unit,
) {
    Column(modifier = modifier.padding(8.dp)) {
        val season = if (title.animeUI.season != 0.toByte()) title.animeUI.season else ""
        val part = if (title.animeUI.part != 0.toByte()) title.animeUI.part else ""
        val extra = if (title.animeUI.extraTotal != 0) title.animeUI.extraTotal else ""
        Row {
            Text(
                text = stringResource(R.string.year),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = title.animeUI.year.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = modifier
                    .clickable {
//                        onEvents(
//                            RandomScreenEvents.OnLinkClick(
//                                filterName = title.animeUI.year.toString()
//                            )
//                        )
                    }
            )
        }
        if (title.isMovie) DetailText(
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
                    title.animeUI.episodeCount!!,
                    title.animeUI.episodesTotal.toString(),
                    extra
                )
            )
            else DetailText(
                label = stringResource(R.string.episodes),
                value = stringResource(
                    R.string.episodes_of,
                    title.animeUI.episodeCount!!,
                    title.animeUI.episodesTotal.toString()
                )
            )
        }
        DetailText(
            label = stringResource(id = R.string.duration),
            value = title.duration.toString()
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