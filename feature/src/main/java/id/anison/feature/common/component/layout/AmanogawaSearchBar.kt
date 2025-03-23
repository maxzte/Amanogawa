package id.anison.feature.common.component.layout

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.inputFieldColors
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.R
import id.anison.feature.anime.AnimeDetailsScreenModel
import id.anison.feature.anime.model.AnimeDetailsUI
import id.anison.feature.common.DataState
import id.anison.feature.common.component.AnimeHeader
import id.anison.feature.common.component.AnimeImages
import id.anison.feature.common.component.CoilImageLoader
import id.anison.feature.common.component.DetailText
import id.anison.feature.common.component.ErrorMessage
import id.anison.feature.common.component.ExpandedText
import id.anison.feature.common.component.GenreGrid
import id.anison.feature.common.component.ReusableFlowRow
import id.anison.feature.common.component.SceletonTest
//import id.anison.feature.common.component.SceletonTest
import id.anison.feature.common.component.Test
import id.anison.feature.common.component.TitleActions
import id.anison.feature.common.component.TitleHead
import id.anison.feature.common.component.TitleSection
import id.anison.feature.common.component.YearGrid
import id.anison.feature.common.component.button.FilterButton
import id.anison.feature.search.event.RandomScreenEvents
import id.anison.feature.search.event.SearchScreenEvents
import id.anison.feature.search.model.DubersUI
import id.anison.feature.search.model.FilterUI
import id.anison.feature.search.screen.ExtendedSearchTitlesScreen
import id.anison.feature.search.screen.FoundAnimeList
import id.anison.feature.search.screenmodel.ExtendedSearchScreenModel
import id.anison.feature.search.screenmodel.RandomTitleScreenModel
import id.anison.feature.search.screenmodel.SearchState
import kotlinx.coroutines.launch


@SuppressLint("UnusedCrossfadeTargetStateParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AmanogawaSearchBar(
    searchQuery: String,
    searchState: SearchState,
    onEvents: (SearchScreenEvents) -> Unit,
    extendedSearchScreenModel: ExtendedSearchScreenModel,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.currentOrThrow
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        extendedSearchScreenModel.getFilters()
    }
    val filterState by extendedSearchScreenModel.extendedSearchState.collectAsState()
    val filter = (filterState.filterOptions as? DataState.Success)?.data
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isSheetScreenVisible by rememberSaveable { mutableStateOf(false) }

    val screenModel = navigator.koinNavigatorScreenModel<RandomTitleScreenModel>()
    val searchScreenModel = navigator.koinNavigatorScreenModel<ExtendedSearchScreenModel>()
    val animeDetailsScreenModel = navigator.koinNavigatorScreenModel<AnimeDetailsScreenModel>()
    val animeState by animeDetailsScreenModel.animeState.collectAsState()
    val isFavorite = animeState.isFavorite
    val state = screenModel.randomTitleState.collectAsState().value
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {

        if (isSheetScreenVisible) {
            ModalBottomSheet(
                onDismissRequest = { isSheetScreenVisible = false },
                sheetState = bottomSheetState,
                dragHandle = {},
                sheetMaxWidth = Int.MAX_VALUE.dp,//LocalConfiguration.current.screenWidthDp.dp,
                contentWindowInsets = { WindowInsets(0, 0, 0, 0) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    when (val ids = state.animeIDs) {
                        is DataState.Error -> {
                            ErrorMessage(onUpdate = { screenModel.refresh() })
                        }

                        is DataState.Loading -> {
                            SceletonTest()
                        }

                        is DataState.Success -> {
                            when (val randomAnime = state.randomAnime) {
                                is DataState.Loading -> {
                                    Crossfade(
                                        targetState = randomAnime
                                    ) {
                                        SceletonTest()
                                    }
                                }

                                is DataState.Error -> {
                                    ErrorMessage(onUpdate = { screenModel.refresh() })
                                }

                                is DataState.Success -> {
                                    Crossfade(
                                        targetState = randomAnime
                                    ) {
                                        Test(
                                            anime = randomAnime.data,
                                            isFavorite = isFavorite,
                                            isLast = screenModel.isLast(),
                                            onEvents = { event ->
                                                when (event) {
                                                    is RandomScreenEvents.PopBack -> {
                                                        coroutineScope.launch {
                                                            bottomSheetState.hide()
                                                            isSheetScreenVisible = false
                                                        }
//                                                    navigator.pop()
                                                    }

                                                    is RandomScreenEvents.Next -> {
                                                        screenModel.getRandomAnime(ids.data)
                                                    }

                                                    is RandomScreenEvents.IsLast -> {
                                                        screenModel.isLast()
                                                    }

                                                    is RandomScreenEvents.OnLinkClick -> {
                                                        searchScreenModel.search(event.filter)
                                                        navigator.push(
                                                            ExtendedSearchTitlesScreen(
                                                                event.filterName
                                                            )
                                                        )
                                                    }
                                                }
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .semantics { traversalIndex = 0f },
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = { newValue ->
                        onEvents(SearchScreenEvents.OnSearchQueryChange(newValue))
                    },
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = { isExpanded ->
                        if (expanded != isExpanded) {
                            expanded = isExpanded
                        }
                    },
                    placeholder = {
                        Text("Search anime...")
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Row {
                            if (searchQuery.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        onEvents(SearchScreenEvents.OnSearchQueryChange(""))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = stringResource(R.string.clear)
                                    )
                                }
                            }
                            if (searchQuery.isBlank() && expanded) {
                                IconButton(
                                    onClick = {
                                        expanded = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = stringResource(R.string.clear)
                                    )
                                }
                            }
                            if (searchQuery.isEmpty()) {
                                IconButton(onClick = {
                                    isSheetScreenVisible = true
//                                        onEvents(SearchScreenEvents.OnRandomButtonClick)
                                }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_rounded_arrow_outward_24),
                                        contentDescription = stringResource(R.string.filter_icon)
                                    )
                                }
                                IconButton(onClick = { onEvents(SearchScreenEvents.OnExtendedButtonClick) }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_filter_24),
                                        contentDescription = stringResource(R.string.filter_icon)
                                    )
                                }
                            }
                        }
                    },
                    colors = inputFieldColors(
                        selectionColors = TextSelectionColors(
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            handleColor = Color.Red
                        )
                    ),
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(20.dp)
                        )
                )
            },
            expanded = expanded,
            onExpandedChange = { isExpanded ->
                if (expanded != isExpanded) {
                    expanded = isExpanded
                }
            },
        ) {
            if (expanded) {
                FoundAnimeList(
                    searchQuery = searchQuery,
                    animeHistoryList = (searchState.searchHistory as? DataState.Success)?.data
                        ?: emptyList(),
                    animeListState = searchState.foundTitles,
                    onEvents = onEvents,
                )
            }
        }
        HorizontalDivider(Modifier.fillMaxWidth(), color = Color.Gray)

        LazyColumn(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            val genres = filter?.genres
            val t = filter?.dubers?.t
            val y = filter?.years
            val v = filter?.dubers?.v
            val s = filter?.dubers?.s
            if (!genres.isNullOrEmpty()) {
                item {
                    TitleSection("Genres", Modifier.padding(start = 8.dp))
                    GenreGrid(
                        genres = genres,
                        onClick = { filter, filterName ->
                            onEvents(SearchScreenEvents.OnFilterClick(filter, filterName))
                        }
                    )
                }
            }
            if (!y.isNullOrEmpty()) {
                item {
                    TitleSection("Years", Modifier.padding(start = 8.dp))
                    YearGrid(
                        years = y,
                        onClick = { filter, filterName ->
                            onEvents(SearchScreenEvents.OnFilterClick(filter, filterName))
                        }
                    )
                }
            }
            if (!t.isNullOrEmpty()) {
                item {
                    TitleSection("Translators", Modifier.padding(start = 8.dp))
                    ReusableFlowRow(
                        items = t.values.toList(),
                        content = { name ->
                            FilterButton(
                                name = name,
                                onClick = {
                                    val filter = FilterUI(
                                        dubers = DubersUI(
                                            t = t.filterValues { it == name }
                                        )
                                    )
                                    onEvents(SearchScreenEvents.OnFilterClick(filter, name))
                                }
                            )
                        }
                    )
                }
            }
            if (!v.isNullOrEmpty()) {
                item {
                    TitleSection("Voice actors", Modifier.padding(start = 8.dp))
                    ReusableFlowRow(
                        items = v.values.toList(),
                        content = { name ->
                            FilterButton(
                                name = name,
                                onClick = {
                                    val filter = FilterUI(
                                        dubers = DubersUI(
                                            v = v.filterValues { it == name }
                                        )
                                    )
                                    onEvents(SearchScreenEvents.OnFilterClick(filter, name))
                                }
                            )
                        }
                    )
                }
            }
            if (!s.isNullOrEmpty()) {
                item {
                    TitleSection("Sound engineers", Modifier.padding(start = 8.dp))
                    ReusableFlowRow(
                        items = s.values.toList(),
                        content = { name ->
                            FilterButton(
                                name = name,
                                onClick = {
                                    val filter = FilterUI(
                                        dubers = DubersUI(
                                            s = s.filterValues { it == name }
                                        )
                                    )
                                    onEvents(SearchScreenEvents.OnFilterClick(filter, name))
                                }
                            )
                        }
                    )
                }
            }
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
                title = anime.animeUI.name,
                onBackClick = { onEvents(RandomScreenEvents.PopBack)/*navigator.pop()*/ },
                actionIconContent = {
                    IconButton(onClick = { onEvents(RandomScreenEvents.Next) }) {
                        if (isLast) {
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
            TitleHead(anime = anime)
        }
        TitleDetails(anime, modifier, onEvents = onEvents)
        AnimeImages(anime.screensF.map { imgLink -> imgLink[0] })
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
                        onEvents(
                            RandomScreenEvents.OnLinkClick(
                                filter = FilterUI(years = listOf(title.animeUI.year)),
                                filterName = title.animeUI.year.toString()
                            )
                        )
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