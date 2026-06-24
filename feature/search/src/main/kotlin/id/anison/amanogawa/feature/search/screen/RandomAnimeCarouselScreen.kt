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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import id.anison.amanogawa.core.model.AnimeUI
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
import id.anison.amanogawa.core.ui.spacing
import id.anison.amanogawa.feature.search.screenmodel.ModalSheetState
import id.anison.amanogawa.feature.search.screenmodel.RandomAnimeScreenModel
import id.anison.amanogawa.feature.search.screenmodel.RandomScreenEvents
import id.anison.amanogawa.feature.search.screenmodel.RandomTitleState
import id.anison.amanogawa.feature.search.screenmodel.toScreen
import kotlinx.coroutines.launch


class RandomAnimeCarouselScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val screenModel: RandomAnimeScreenModel = koinScreenModel()
        val state by screenModel.state.collectAsState()
        val randomTitles = state.titles.collectAsLazyPagingItems()
        val pagerState = rememberPagerState(
            pageCount = { randomTitles.itemCount }
        )
        val modalSheetState by screenModel.isModalSheetVisible.collectAsState()

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
                    onEvents = { screenModel.onEvents(it) },
                    state = state
                )
            }
        }
    }

    @Composable
    private fun RandomAnimeScreenContent(
        randomTitles: LazyPagingItems<AnimeUI>,
        pagerState: PagerState,
        onEvents: (RandomScreenEvents) -> Unit,
        modalSheetState: ModalSheetState,
        state: RandomTitleState,
    ) {
        val isLandscape = isLandscape()

        LaunchedEffect(pagerState.currentPage, randomTitles.itemCount) {
            // Automatic detail loading for the current page only
            if (pagerState.currentPage in 0 until randomTitles.itemCount) {
                randomTitles[pagerState.currentPage]?.let { anime ->
                    onEvents(RandomScreenEvents.LoadAnimeDetails(anime.animeId))
                }
            }

            if (modalSheetState.isVisible && modalSheetState.pageIndex != pagerState.currentPage) {
                onEvents(
                    RandomScreenEvents.SetModalSheetVisibility(
                        pageIndex = null,
                        isVisible = false,
                        animeId = null
                    )
                )
            }
        }

        Scaffold(
            floatingActionButton = {
                AnimatedVisibility(
                    visible = (pagerState.pageCount > 0) && !pagerState.canScrollForward,
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
                    val animeUI = randomTitles[index]
                    if (animeUI != null) {
                        val animeDetailsUI = state.details[animeUI.animeId]

                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Background image + overlay
                            Box(modifier = Modifier.matchParentSize()) {
                                AnimeImageLoader(
                                    titleImage = animeUI.posterMini,
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
                                    animeUI = animeUI,
                                    animeDetails = animeDetailsUI,
                                    onEvents = onEvents,
                                    modalSheetState = modalSheetState
                                )
                            } else {
                                HorizontalPage(
                                    paddingValues = paddingValues,
                                    pageIndex = index,
                                    animeUI = animeUI,
                                    animeDetails = animeDetailsUI,
                                    onEvents = onEvents,
                                    pagerState = pagerState,
                                )
                            }

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
                                            onEvents(
                                                RandomScreenEvents.SetModalSheetVisibility(
                                                    pageIndex = null,
                                                    isVisible = false,
                                                    animeId = null
                                                )
                                            )
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
//                                        IconButton(
//                                            onClick = {
//                                                onEvents(
//                                                    RandomScreenEvents.SetModalSheetVisibility(
//                                                        pageIndex = null,
//                                                        isVisible = false,
//                                                        animeId = null
//                                                    )
//                                                )
//                                            },
//                                            content = {
//                                                Icon(
//                                                    imageVector = AmanogawaIcon.close,
//                                                    contentDescription = null
//                                                )
//                                            }
//                                        )

                                        Text(
                                            text = "Description",
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier.padding(start = 16.dp),
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

                                        if (animeDetailsUI != null) {
                                            Text(
                                                text = animeDetailsUI.description,
                                                style = MaterialTheme.typography.bodyLarge,
                                            )
                                        } else {
                                            AmanogawaSpinner()
                                        }

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

                AmanogawaTopBar(
                    navigationIconContent = {
                        ArrowBackButton {
                            onEvents(RandomScreenEvents.PopBack)
                        }
                    },
                    showBottomLine = false,
                    containerColor = Color.Transparent,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }

    @Composable
    private fun VerticalPage(
        paddingValues: PaddingValues,
        animeUI: AnimeUI,
        animeDetails: AnimeDetailsUI?,
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
                if (animeDetails != null) {
                    InfoTag(text = if (animeDetails.isMovie) "Movie" else "Series")
                } else {
                    Box(
                        modifier = Modifier
                            .size(width = 60.dp, height = 24.dp)
                            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    )
                }
                if (animeUI.season.isNotEmpty()) InfoTag(text = "Season " + animeUI.season)
                if (animeUI.part.isNotEmpty()) InfoTag(text = "Part " + animeUI.part)
            }

            // Poster
            ElevatedCard(
                onClick = { onEvents(RandomScreenEvents.PushAnimeDetailsScreen(animeUI.animeId)) },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .bounceClick()
            ) {
                if (animeDetails != null) {
                    AnimeImageLoader(
                        titleImage = animeDetails.poster,
                        modifier = Modifier.aspectRatio(7f / 10f)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(7f / 10f)
                            .background(Color.Gray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AmanogawaSpinner()
                    }
                }
            }

            // Title
            Column(Modifier.fillMaxWidth(0.8f)) {
                Text(
                    text = animeUI.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = animeUI.enJpName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.LightGray
                )
                Spacer(Modifier.height(4.dp))
            }

            // Info buttons
            AnimeActionButtons(
                animeUI = animeUI,
                animeDetails = animeDetails,
                onEvents = onEvents
            )

            ShowDescriptionButton {
                onEvents(
                    RandomScreenEvents.SetModalSheetVisibility(
                        pageIndex = pageIndex,
                        isVisible = true,
                        animeId = animeUI.animeId
                    )
                )
            }
        }

        if (modalSheetState.isVisible && modalSheetState.pageIndex == pageIndex) {
            ModalBottomSheet(
                onDismissRequest = {
                    onEvents(
                        RandomScreenEvents.SetModalSheetVisibility(
                            pageIndex = null,
                            isVisible = false,
                            animeId = animeUI.animeId
                        )
                    )
                },
            ) {
                if (animeDetails != null) {
                    Text(
                        text = animeDetails.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(MaterialTheme.spacing.medium)
                    )
                } else {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.medium),
                        contentAlignment = Alignment.Center
                    ) {
                        AmanogawaSpinner()
                    }
                }
            }
        }
    }

    @Composable
    private fun HorizontalPage(
        paddingValues: PaddingValues,
        animeUI: AnimeUI,
        animeDetails: AnimeDetailsUI?,
        onEvents: (RandomScreenEvents) -> Unit,
        pagerState: PagerState,
        pageIndex: Int,
    ) {
        val mediumSpacing = MaterialTheme.spacing.medium
        val scope = rememberCoroutineScope()

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        PaddingValues(
                            start = mediumSpacing,
                            top = paddingValues.calculateTopPadding(),
                            end = mediumSpacing,
                            bottom = paddingValues.calculateBottomPadding()
                        )
                    )
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(mediumSpacing)
            ) {
                if (pagerState.currentPage > 0) {
                    FilledTopBarIcon(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
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
                    horizontalArrangement = Arrangement.spacedBy(mediumSpacing)
                ) {
                    ElevatedCard(
                        onClick = {
                            onEvents(
                                RandomScreenEvents.PushAnimeDetailsScreen(
                                    animeId = animeUI.animeId
                                )
                            )
                        },
                        modifier = Modifier.bounceClick()
                    ) {
                        if (animeDetails != null) {
                            AnimeImageLoader(
                                titleImage = animeDetails.poster,
                                modifier = Modifier.aspectRatio(7f / 10f)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(7f / 10f)
                                    .background(Color.Gray.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                AmanogawaSpinner()
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
                        ) {
                            if (animeDetails != null) {
                                InfoTag(text = if (animeDetails.isMovie) "Movie" else "Series")
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(width = 60.dp, height = 24.dp)
                                        .background(
                                            Color.Gray.copy(alpha = 0.3f),
                                            RoundedCornerShape(12.dp)
                                        )
                                )
                            }
                            if (animeUI.season.isNotEmpty()) {
                                InfoTag(text = "Season " + animeUI.season)
                            }

                            if (animeUI.part.isNotEmpty()) {
                                InfoTag(text = "Part " + animeUI.part)
                            }
                        }

                        Text(
                            text = animeUI.name,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )

                        Text(
                            text = animeUI.enJpName,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.LightGray
                        )

                        Spacer(Modifier.height(MaterialTheme.spacing.extraSmall))

                        AnimeActionButtons(
                            animeUI = animeUI,
                            animeDetails = animeDetails,
                            onEvents = onEvents,
                            isHorizontal = true
                        )

                        ShowDescriptionButton {
                            onEvents(
                                RandomScreenEvents.SetModalSheetVisibility(
                                    pageIndex = pageIndex,
                                    isVisible = true,
                                    animeId = animeUI.animeId
                                )
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = pagerState.canScrollForward,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    FilledTopBarIcon(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
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
    private fun AnimeActionButtons(
        animeUI: AnimeUI,
        animeDetails: AnimeDetailsUI?,
        onEvents: (RandomScreenEvents) -> Unit,
        isHorizontal: Boolean = false
    ) {
        val content: @Composable () -> Unit = {
            if (animeDetails != null) {
                OutlinedButton(onClick = {}) {
                    Icon(
                        imageVector = AmanogawaIcon.like,
                        null,
                        tint = Color.White
                    )
                    VerticalDivider(
                        Modifier
                            .height(16.dp)
                            .padding(horizontal = MaterialTheme.spacing.extraSmall)
                    )
                    Text(
                        text = animeDetails.liked.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                    Text(
                        text = " · ${animeDetails.disliked}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                    VerticalDivider(
                        Modifier
                            .height(16.dp)
                            .padding(horizontal = MaterialTheme.spacing.extraSmall)
                    )
                    Icon(
                        imageVector = AmanogawaIcon.disLike,
                        null,
                        tint = Color.White
                    )
                }

                animeDetails.duration.takeIf { it.isNotEmpty() }?.let {
                    OutlinedButton(onClick = {}) {
                        Icon(
                            imageVector = AmanogawaIcon.avTime,
                            null,
                            tint = Color.White
                        )
                        Text(
                            text = " $it",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            } else {
                // Placeholder for likes and duration
                Box(
                    modifier = Modifier
                        .size(width = 120.dp, height = 40.dp)
                        .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                )
                Box(
                    modifier = Modifier
                        .size(width = 80.dp, height = 40.dp)
                        .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                )
            }

            YearTag(
                year = animeUI.year,
                onYearClick = {
                    onEvents(
                        RandomScreenEvents.OnLinkClick(
                            filterId = -1L,
                            filterName = animeUI.year,
                            filterType = FilterTypeUI.YEAR
                        )
                    )
                }
            )

            if (animeDetails != null) {
                for (genreUI in animeDetails.genres) {
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
            } else {
                // Placeholders for genres
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(width = 70.dp, height = 40.dp)
                            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    )
                }
            }
        }

        if (isHorizontal) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
            ) {
                content()
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)) {
                        content()
                    }
                }
            }
        }
    }

    @Composable
    fun ShowDescriptionButton(
        onClick: () -> Unit,
    ) {
        OutlinedButton(
            onClick = onClick,
            content = {
                Text(text = "More info", color = Color.White)
            }
        )
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
