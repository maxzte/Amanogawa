@file:OptIn(ExperimentalMaterial3Api::class)

package id.anison.amanogawa.feature.search.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import id.anison.amanogawa.core.model.AnimeDetailsUI
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.navigation.NavigationHandler
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.common.bounceClick
import id.anison.amanogawa.core.ui.common.component.AmanogawaSpinner
import id.anison.amanogawa.core.ui.common.component.AnimeImageLoader
import id.anison.amanogawa.core.ui.common.component.ErrorMessage
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.button.FilledTopBarIcon
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.feature.search.screenmodel.ModalSheetState
import id.anison.amanogawa.feature.search.screenmodel.RandomAnimeScreenModel
import id.anison.amanogawa.feature.search.screenmodel.RandomScreenEvents
import id.anison.amanogawa.feature.search.screenmodel.toScreen


class RandomAnimeCarouselScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<RandomAnimeScreenModel>()
        val state by screenModel.state.collectAsState()
        val randomTitles = state.titles.collectAsLazyPagingItems()
        val savedPageIndex = rememberSaveable { mutableIntStateOf(0) }
        val pagerState = rememberPagerState(
            initialPage = savedPageIndex.intValue,
            pageCount = { randomTitles.itemCount }
        )
        val modalSheetState by screenModel.isModalSheetVisible.collectAsState()

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                savedPageIndex.intValue = page
            }
        }

        NavigationHandler(
            navigationState = screenModel.navEventState,
            toScreen = { toScreen() }
        )

        when (randomTitles.loadState.refresh) {
            is LoadState.Loading -> {
                AmanogawaSpinner()
            }

            is LoadState.Error -> {
                ErrorMessage(
                    onUpdate = { screenModel.onEvents(RandomScreenEvents.Refresh) }
                )
            }

            is LoadState.NotLoading -> {
                RandomAnimeScreenContent(
                    randomTitles = randomTitles,
                    pagerState = pagerState,
                    modalSheetState = modalSheetState,
                    onEvents = { screenModel.onEvents(it) }
                )
            }
        }
    }

    @Composable
    private fun RandomAnimeScreenContent(
        randomTitles: LazyPagingItems<AnimeDetailsUI>,
        pagerState: PagerState,
        onEvents: (RandomScreenEvents) -> Unit,
        modalSheetState: ModalSheetState,
    ) {
        val isLandscape = isLandscape()
        val isDataReady = remember { mutableStateOf(false) }

        LaunchedEffect(pagerState.currentPage) {
            if (modalSheetState.isVisible && modalSheetState.pageIndex != pagerState.currentPage) {
                onEvents(
                    RandomScreenEvents.SetModalSheetVisibility(
                        pageIndex = null,
                        isVisible = false
                    )
                )
            }
        }

        LaunchedEffect(pagerState.pageCount) {
            if (pagerState.pageCount > 0) {
                isDataReady.value = true
            }

        }
        Scaffold(
            floatingActionButton = {
                AnimatedVisibility(
                    visible = isDataReady.value && !pagerState.canScrollForward,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(
                        onClick = {
                            onEvents(RandomScreenEvents.Refresh)
                        },
                        content = {
                            Icon(imageVector = AmanogawaIcon.refresh, contentDescription = null)
                        }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                val isPortrait = !isLandscape

                val pager: @Composable (Int) -> Unit = { index ->
                    val animeDetailsUI = randomTitles[index]
                    if (animeDetailsUI != null) {
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Background image + overlay
                            Box(modifier = Modifier.matchParentSize()) {
                                AnimeImageLoader(
                                    titleImage = animeDetailsUI.animeUI.posterMini,
                                    useCache = false,
                                    radius = 10,
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color.Black.copy(alpha = 0.4f))
                                )
                            }

                            if (isPortrait) {
                                VerticalPage(
                                    paddingValues = paddingValues,
                                    pageIndex = index,
                                    animeDetails = animeDetailsUI,
                                    onEvents = onEvents,
                                    modalSheetState = modalSheetState
                                )
                            } else {
                                HorizontalPage(
                                    paddingValues = paddingValues,
                                    pageIndex = index,
                                    animeDetails = animeDetailsUI,
                                    onEvents = onEvents,
                                    pagerState = pagerState,
                                )
                            }

                            AmanogawaTopBar(
                                navigationIconContent = {
                                    ArrowBackButton {
                                        onEvents(RandomScreenEvents.PopBack)
                                    }
                                },
                                showBottomLine = false,
                                containerColor = Color.Transparent
                            )

                            if (!isPortrait && modalSheetState.isVisible && modalSheetState.pageIndex == index) {
                                val interactionSource =
                                    remember { MutableInteractionSource() }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.4f))
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null
                                        ) {
                                        }
                                )
                            }

                            AnimatedVisibility(
                                modifier = Modifier
                                    .fillMaxWidth(0.4f)
                                    .align(Alignment.CenterEnd),
                                visible = !isPortrait && modalSheetState.isVisible
                                        && modalSheetState.pageIndex == index,
                                enter = slideInHorizontally { it },
                                exit = slideOutHorizontally { it }
                            ) {
                                val topInset = DrawerDefaults.windowInsets
                                    .only(WindowInsetsSides.Top)
                                    .asPaddingValues()
                                    .calculateTopPadding()

                                ModalDrawerSheet(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(0.4f)
                                        .align(Alignment.CenterEnd)
                                        .clip(
                                            shape = RoundedCornerShape(
                                                topStart = 16.dp,
                                                bottomStart = 16.dp
                                            )
                                        ),
                                    windowInsets = WindowInsets(0.dp, topInset, 0.dp, 0.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        IconButton(
                                            onClick = {
                                                onEvents(
                                                    RandomScreenEvents.SetModalSheetVisibility(
                                                        pageIndex = pagerState.currentPage,
                                                        isVisible = false
                                                    )
                                                )
                                            },
                                            content = {
                                                Icon(
                                                    imageVector = AmanogawaIcon.close,
                                                    contentDescription = null
                                                )
                                            }
                                        )

                                        Text(
                                            text = "Description",
                                            style = MaterialTheme.typography.titleLarge,
                                        )
                                    }

                                    HorizontalDivider(color = Color.LightGray)

                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(start = 16.dp, end = 16.dp)
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            text = animeDetailsUI.description,
                                            style = MaterialTheme.typography.bodyLarge,
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                if (isPortrait) {
                    VerticalPager(
                        state = pagerState,
                        beyondViewportPageCount = 1,
                        modifier = Modifier.fillMaxSize(),
                        pageContent = { pager(it) }
                    )
                } else {
                    HorizontalPager(
                        state = pagerState,
                        beyondViewportPageCount = 1,
                        modifier = Modifier.fillMaxSize(),
                        pageContent = { pager(it) }
                    )
                }
            }
        }
    }

    @Composable
    private fun VerticalPage(
        paddingValues: PaddingValues,
        animeDetails: AnimeDetailsUI,
        onEvents: (RandomScreenEvents) -> Unit,
        modalSheetState: ModalSheetState,
        pageIndex: Int,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    paddingValues = PaddingValues(
                        top = paddingValues.calculateTopPadding() + AmanogawaConst.barHeight
                    )
                )
        ) {
            // Tags Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                InfoTag(text = if (animeDetails.isMovie) "Movie" else "Series")
                if (animeDetails.animeUI.season.isNotEmpty()) InfoTag(text = "Season " + animeDetails.animeUI.season)
                if (animeDetails.animeUI.part.isNotEmpty()) InfoTag(text = "Part" + animeDetails.animeUI.part)
            }

            // Poster
            ElevatedCard(
                onClick = { onEvents(RandomScreenEvents.PushAnimeDetailsScreen(animeDetails.animeUI.animeId)) },
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

            // Title
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

                item {
                    YearTag(
                        year = animeDetails.animeUI.year,
                        onYearClick = {
                            onEvents(
                                RandomScreenEvents.OnLinkClick(
                                    filterId = -1L,
                                    filterName = animeDetails.animeUI.year,
                                    filterType = FilterTypeUI.YEAR
                                )
                            )
                        }
                    )
                }

                items(animeDetails.genres, key = { it.genreId }) { genre ->
                    OutlinedButton(
                        onClick = {
                            onEvents(
                                RandomScreenEvents.OnLinkClick(
                                    filterId = genre.genreId,
                                    filterName = genre.name,
                                    filterType = FilterTypeUI.GENRE
                                )
                            )
                        },
                        content = {
                            Text(
                                text = genre.name,
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )

                        }
                    )
                }
            }

            TextButton(
                onClick = {
                    onEvents(
                        RandomScreenEvents.SetModalSheetVisibility(
                            pageIndex = pageIndex,
                            isVisible = true
                        )
                    )
                },
                content = { Text("More info") }
            )
        }

        if (modalSheetState.isVisible && modalSheetState.pageIndex == pageIndex) {
            ModalBottomSheet(
                onDismissRequest = {
                    onEvents(
                        RandomScreenEvents.SetModalSheetVisibility(
                            pageIndex = null,
                            isVisible = false
                        )
                    )
                },
            ) {
                Text(
                    text = animeDetails.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

    @Composable
    private fun HorizontalPage(
        paddingValues: PaddingValues,
        animeDetails: AnimeDetailsUI,
        onEvents: (RandomScreenEvents) -> Unit,
        pagerState: PagerState,
        pageIndex: Int,
    ) {
        val scrollToBack = remember { mutableStateOf(false) }
        val scrollToForward = remember { mutableStateOf(false) }

        LaunchedEffect(scrollToBack.value) {
            if (scrollToBack.value && pagerState.currentPage > 0) {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                scrollToBack.value = false
            }
        }

        LaunchedEffect(scrollToForward.value) {
            if (scrollToForward.value && pagerState.currentPage < pagerState.pageCount - 1) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                scrollToForward.value = false
            }
        }

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
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
                    )
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (pagerState.currentPage > 0) {
                    FilledTopBarIcon(
                        onClick = { scrollToBack.value = true },
                        backgroundColor = Color.Gray.copy(alpha = 0.5f),
                        content = {
                            Icon(
                                imageVector = AmanogawaIcon.arrowBack,
                                contentDescription = stringResource(id = R.string.arrow_back),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    )
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(1f),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedCard(
                        onClick = {
                            onEvents(
                                RandomScreenEvents.PushAnimeDetailsScreen(
                                    animeId = animeDetails.animeUI.animeId
                                )
                            )
                        },
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
                            if (animeDetails.animeUI.season.isNotEmpty()) InfoTag(text = "Season " + animeDetails.animeUI.season)
                            if (animeDetails.animeUI.part.isNotEmpty()) InfoTag(text = "Part" + animeDetails.animeUI.part)
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

                            YearTag(
                                year = animeDetails.animeUI.year,
                                onYearClick = {
                                    onEvents(
                                        RandomScreenEvents.OnLinkClick(
                                            filterId = -1L,
                                            filterName = animeDetails.animeUI.year,
                                            filterType = FilterTypeUI.YEAR
                                        )
                                    )
                                }
                            )

                            animeDetails.genres.forEach { genreUI ->
                                OutlinedButton(
                                    onClick = {
                                        onEvents(
                                            RandomScreenEvents.OnLinkClick(
                                                filterId = genreUI.genreId,
                                                filterName = genreUI.name,
                                                filterType = FilterTypeUI.GENRE
                                            )
                                        )
                                    },
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

                        TextButton(
                            onClick = {
                                onEvents(
                                    RandomScreenEvents.SetModalSheetVisibility(
                                        pageIndex = pageIndex,
                                        isVisible = true
                                    )
                                )
                            },
                            content = {
                                Text("More info")
                            }
                        )
                    }

                }

                AnimatedVisibility(
                    visible = pagerState.canScrollForward,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    FilledTopBarIcon(
                        onClick = { scrollToForward.value = true },
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
    private fun YearTag(
        year: String,
        onYearClick: () -> Unit,
    ) {
        OutlinedButton(onClick = { onYearClick() }) {
            Text(
                text = year,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
    }

}
