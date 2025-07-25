package id.anison.amanogawa.feature.search.screen

import android.annotation.SuppressLint
import android.text.Layout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.amanogawa.core.model.AnimeDetailsUI
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.common.bounceClick
import id.anison.amanogawa.core.ui.common.component.AnimeCard
import id.anison.amanogawa.core.ui.common.component.AmanogawaSpinner
import id.anison.amanogawa.core.ui.common.component.AnimeCardContent
import id.anison.amanogawa.core.ui.common.component.AnimeImageLoader
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.button.FilledTopBarIcon
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.ignoreHorizontalParentPadding
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.feature.search.screenmodel.RandomAnimeScreenModel
import java.nio.file.WatchEvent


class RandomAnimeCarouselScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint(
        "StateFlowValueCalledInComposition", "SuspiciousIndentation",
        "UnusedCrossfadeTargetStateParameter"
    )
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<RandomAnimeScreenModel>()
        val state by screenModel.state.collectAsState()
        val randomTitles = state.titles.collectAsLazyPagingItems()
        val savedPageIndex = rememberSaveable { mutableIntStateOf(0) }
        val pagerState = rememberPagerState(
            initialPage = savedPageIndex.intValue,
            pageCount = {
                randomTitles.itemCount
            }
        )

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                savedPageIndex.intValue = page
            }
        }

        when (randomTitles.loadState.refresh) {
            is LoadState.Loading -> {
                AmanogawaSpinner()
            }

            is LoadState.Error -> {
            }

            is LoadState.NotLoading -> {
                val isLandscape = isLandscape()
                Scaffold(
                    topBar = {
                        AmanogawaTopBar(
                            navigationIconContent = {
                                ArrowBackButton { }
//                                FilledTopBarIcon(
//                                    onClick = { },
//                                    backgroundColor = Color.Gray.copy(alpha = 0.5f),
//                                    content = {
//                                        Icon(
//                                            imageVector = AmanogawaIcon.arrowBack,
//                                            contentDescription = stringResource(id = R.string.arrow_back),
//                                            tint = MaterialTheme.colorScheme.primary,
//                                        )
//                                    }
//                                )
                            },
                            showBottomLine = false,
                            containerColor = Color.Transparent
                        )
                    }
                ) { paddingValues ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
//                            .padding(paddingValues)
//                            .padding(
//                                paddingValues = PaddingValues(
//                                    start = 8.dp,
//                                    top = paddingValues.calculateTopPadding() + 8.dp,
//                                    end = 8.dp,
//                                    bottom = paddingValues.calculateBottomPadding() + 8.dp,
//                                ),
//                            )
                    ) {
                        if (!isLandscape) {
                            VerticalPager(
                                state = pagerState,
                                pageSpacing = 8.dp,
                                modifier = Modifier.fillMaxSize()
                            ) { index ->
                                val animeDetailsUI = randomTitles[index]
                                if (animeDetailsUI != null) {
                                    VerticalPage(paddingValues, animeDetailsUI)
                                }
                            }
                        } else {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { index ->
                                val animeDetailsUI = randomTitles[index]
                                if (animeDetailsUI != null) {
                                    HorizontalPage(paddingValues, animeDetailsUI)
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun VerticalPage(
        paddingValues: PaddingValues,
        animeDetails: AnimeDetailsUI
    ) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        var showBottomSheet by remember { mutableStateOf(false) }

        val seasonText =
            animeDetails.animeUI.season.takeIf { it != 0.toByte() }?.let { "Season $it" }
        val partText = animeDetails.animeUI.part.takeIf { it != 0.toByte() }?.let { "Part $it" }

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            // Background image + overlay
            Box(modifier = Modifier.matchParentSize()) {
                AnimeImageLoader(
                    titleImage = animeDetails.animeUI.posterMini,
                    useCache = false,
                    radius = 10,
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                // Tags Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    InfoTag(text = if (animeDetails.isMovie) "Movie" else "Series")
                    seasonText?.let { InfoTag(text = it) }
                    partText?.let { InfoTag(text = it) }
                }

                // Poster
                ElevatedCard(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .bounceClick()
                ) {
                    AnimeImageLoader(
                        titleImage = animeDetails.poster,
                        useCache = false,
                        modifier = Modifier.aspectRatio(7f / 10f)
                    )
                }

                // Titles
                Column(Modifier.fillMaxWidth(0.8f)) {
                    Text(
                        text = animeDetails.animeUI.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        text = animeDetails.animeUI.enJpName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.LightGray
                    )
                    Spacer(Modifier.height(4.dp))
                }

                // Info buttons
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        OutlinedButton(onClick = {}) {
                            Icon(
                                painterResource(id = R.drawable.ic_outline_thumb_up_24),
                                null,
                                tint = Color.White
                            )
                            VerticalDivider(
                                Modifier
                                    .height(16.dp)
                                    .padding(horizontal = 4.dp)
                            )
                            Text(
                                "${animeDetails.liked}",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                            Text(
                                " · ${animeDetails.disliked}",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                            VerticalDivider(
                                Modifier
                                    .height(16.dp)
                                    .padding(horizontal = 4.dp)
                            )
                            Icon(
                                painterResource(R.drawable.ic_outline_thumb_down_24),
                                null,
                                tint = Color.White
                            )
                        }
                    }

                    animeDetails.duration.takeIf { it.isNotEmpty() }?.let {
                        item {
                            OutlinedButton(onClick = {}) {
                                Icon(
                                    painterResource(R.drawable.ic_access_time_24),
                                    null,
                                    tint = Color.White
                                )
                                Text(
                                    " $it",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    item { YearTag(animeDetails.animeUI.year) }

                    items(animeDetails.genres, key = { it.genreId }) {
                        OutlinedButton(onClick = {}) {
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                        }
                    }
                }

                TextButton(onClick = { showBottomSheet = !showBottomSheet }) {
                    Text("More info")
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = bottomSheetState
                ) {
                    Text(
                        text = animeDetails.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun HorizontalPage(
        paddingValues: PaddingValues,
        animeDetails: AnimeDetailsUI
    ) {
        val seasonText =
            animeDetails.animeUI.season.takeIf { it != 0.toByte() }?.let { "Season $it" }
        val partText = animeDetails.animeUI.part.takeIf { it != 0.toByte() }?.let { "Part $it" }

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.matchParentSize()) {
                AnimeImageLoader(
                    titleImage = animeDetails.animeUI.posterMini,
                    useCache = false,
                    radius = 10,
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        PaddingValues(
                            start = 8.dp,
                            top = paddingValues.calculateTopPadding(),
                            end = 8.dp,
                            bottom = paddingValues.calculateBottomPadding()
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTopBarIcon(
                    onClick = { },
                    backgroundColor = Color.Gray.copy(alpha = 0.5f),
                    content = {
                        Icon(
                            imageVector = AmanogawaIcon.arrowBack,
                            contentDescription = stringResource(id = R.string.arrow_back),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(1f),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedCard(
                        onClick = {},
                        modifier = Modifier.bounceClick()
                    ) {
                        AnimeImageLoader(
                            titleImage = animeDetails.poster,
                            useCache = false,
                            modifier = Modifier.aspectRatio(7f / 10f)
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            InfoTag(text = if (animeDetails.isMovie) "Movie" else "Series")
                            seasonText?.let { InfoTag(text = it) }
                            partText?.let { InfoTag(text = it) }
                        }

                        Text(
                            text = animeDetails.animeUI.name,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )

                        Text(
                            text = animeDetails.animeUI.enJpName,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.LightGray
                        )

                        Spacer(Modifier.height(4.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
//                            contentPadding = PaddingValues(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            OutlinedButton(onClick = {}) {
                                Icon(
                                    painterResource(id = R.drawable.ic_outline_thumb_up_24),
                                    null,
                                    tint = Color.White
                                )
                                VerticalDivider(
                                    Modifier
                                        .height(16.dp)
                                        .padding(horizontal = 4.dp)
                                )
                                Text(
                                    "${animeDetails.liked}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White
                                )
                                Text(
                                    " · ${animeDetails.disliked}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White
                                )
                                VerticalDivider(
                                    Modifier
                                        .height(16.dp)
                                        .padding(horizontal = 4.dp)
                                )
                                Icon(
                                    painterResource(R.drawable.ic_outline_thumb_down_24),
                                    null,
                                    tint = Color.White
                                )
                            }

                            animeDetails.duration.takeIf { it.isNotEmpty() }?.let {
                                OutlinedButton(onClick = {}) {
                                    Icon(
                                        painterResource(R.drawable.ic_access_time_24),
                                        null,
                                        tint = Color.White
                                    )
                                    Text(
                                        " $it",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White
                                    )
                                }
                            }

                            YearTag(animeDetails.animeUI.year)

                            animeDetails.genres.forEach { genreUI ->
                                OutlinedButton(
                                    onClick = {},
                                    content = {
                                        Text(
                                            text = genreUI.name,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = Color.White
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                FilledTopBarIcon(
                    onClick = { },
                    backgroundColor = Color.Gray.copy(alpha = 0.5f),
                    content = {
                        Icon(
                            imageVector = AmanogawaIcon.arrowForward,
                            contentDescription = stringResource(id = R.string.arrow_back),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                )
            }
        }
    }

    @Composable
    private fun InfoTag(text: String) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }

    @Composable
    private fun YearTag(year: Short) {
        OutlinedButton(onClick = {}) {
            Text(
                text = year.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
    }


}
