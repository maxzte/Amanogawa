package id.anison.feature.search.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.MainScreenModel
import id.anison.feature.R
import id.anison.feature.anime.AnimeDetailsScreen
import id.anison.feature.common.DataState
import id.anison.feature.common.bounceClick
import id.anison.feature.common.component.CoilImageLoader
import id.anison.feature.common.component.layout.AmanogawaSearchBar
import id.anison.feature.search.event.SearchScreenEvents
import id.anison.feature.search.model.FoundAnimeUI
import id.anison.feature.search.screenmodel.ExtendedSearchScreenModel
import id.anison.feature.search.screenmodel.SearchScreenModel
import id.anison.feature.search.screenmodel.SearchState
import kotlinx.coroutines.delay

class SearchScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val mainScreenModel = navigator.koinNavigatorScreenModel<MainScreenModel>()
        LaunchedEffect(Unit) {
            mainScreenModel.setBottomBarVisible(true)
        }

        SearchScreenContent(navigator, mainScreenModel)
    }

    @Composable
    internal fun SearchScreenContent(
        navigator: Navigator,
        mainScreenModel: MainScreenModel,
    ) {
        val screenModel = koinScreenModel<SearchScreenModel>()
        val extendedSearchScreenModel = koinScreenModel<ExtendedSearchScreenModel>()
        val query by screenModel.query.collectAsState()
        val searchState by screenModel.searchState.collectAsState()
        AmanogawaSearchBar(
            searchQuery = query,
            searchState = searchState,
            onEvents = { event ->
                when (event) {
                    is SearchScreenEvents.OnRandomButtonClick -> {
                        mainScreenModel.setBottomBarVisible(false)
                        navigator.push(RandomAnimeCarouselScreen())
                    }

                    is SearchScreenEvents.OnExtendedButtonClick -> {
                        navigator.push(ExtendedSearchScreen())
                    }

                    is SearchScreenEvents.OnDeleteAllButtonClick -> {
                        screenModel.clearSearchHistory()
                    }

                    is SearchScreenEvents.OnDeleteAnimeFromHistory -> {
                        screenModel.deleteItemFromSearchHistory(event.anime)
                    }

                    is SearchScreenEvents.OnAnimeClick -> {
                        screenModel.addToSearchHistory(event.anime)
                        navigator.push(AnimeDetailsScreen(event.animeId))
                    }

                    is SearchScreenEvents.OnFilterClick -> {
                        extendedSearchScreenModel.search(event.filter)
                        navigator.push(ExtendedSearchTitlesScreen(event.filterName))
                    }

                    is SearchScreenEvents.OnSearchQueryChange -> {
                        screenModel.updateQuery(event.query)
                    }

                }
            },
            extendedSearchScreenModel = extendedSearchScreenModel,
        )

    }
}



@Composable
internal fun FoundAnimeList(
    searchQuery: String,
    animeHistoryList: List<FoundAnimeUI>,
    animeListState: DataState<List<FoundAnimeUI>>,
    onEvents: (SearchScreenEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(animeListState) {
        if (animeListState is DataState.Success) {
            delay(150)
            isDataLoaded = true
        } else {
            isDataLoaded = false
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AnimatedContent(
                targetState = searchQuery.isEmpty(),
                transitionSpec = {
                     slideInVertically(
                        initialOffsetY = { -it }
                    ) togetherWith slideOutVertically(
                        targetOffsetY = { -it }
                    ) using (SizeTransform(clip = false))
                },
                label = "SearchHistoryAnimation"
            ) { isHistoryVisible ->
                if (isHistoryVisible) {
                    Column {
                        SearchHistoryHeader(
                            onClearHistory = {
                                onEvents(SearchScreenEvents.OnDeleteAllButtonClick)
                            }
                        )
                        animeHistoryList.forEach { anime ->
                            SearchHistoryItem(anime, onEvents, modifier.animateItem())
                        }
                    }
                }
            }
        }

        if (animeListState is DataState.Success) {
            items(
                items = animeListState.data,
                key = { it.foundAnimeId }
            ) { anime ->
                AnimatedVisibility(
                    visible = isDataLoaded,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                ) {
                    AnimeSearchResultItem(anime, onEvents, modifier.animateItem())
                }
            }
        }
        item { Spacer(Modifier.fillMaxWidth()) }
    }
}

@Composable
private fun SearchHistoryHeader(onClearHistory: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.search_history),
            color = LocalContentColor.current,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
        TextButton(onClick = onClearHistory) {
            Text(
                text = stringResource(id = R.string.clear_all),
                color = LocalContentColor.current,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun SearchHistoryItem(
    anime: FoundAnimeUI,
    onEvents: (SearchScreenEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_history_24),
            contentDescription = stringResource(id = R.string.history_icon),
            modifier = Modifier.defaultMinSize(24.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    onEvents(SearchScreenEvents.OnAnimeClick(anime.foundAnimeId, anime))
                },
        ) {
            Text(
                text = buildString {
                    append(anime.name)
                    append(" / ${anime.enJpName}")
                    if (anime.season != 0.toByte()) append(" ${anime.season}")
                    append(" ${anime.year}")
                },
                color = LocalContentColor.current,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = stringResource(id = R.string.clear_icon),
            modifier = Modifier
                .defaultMinSize(24.dp)
                .clickable { onEvents(SearchScreenEvents.OnDeleteAnimeFromHistory(anime)) }
        )
    }
}

@Composable
private fun AnimeSearchResultItem(
    anime: FoundAnimeUI,
    onEvents: (SearchScreenEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val seasonText = anime.season.takeIf { it != 0.toByte() }?.toString() ?: ""
    val partText = anime.part.takeIf { it != 0.toByte() }?.let { "Part $it" } ?: ""

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onEvents(SearchScreenEvents.OnAnimeClick(anime.foundAnimeId, anime))
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            onClick = {
                onEvents(SearchScreenEvents.OnAnimeClick(anime.foundAnimeId, anime))
            },
            modifier = Modifier.bounceClick()
        ) {
            CoilImageLoader(
                titleImage = anime.posterMini,
                modifier = Modifier
                    .widthIn(max = 50.dp)
                    .aspectRatio(7f / 10f)
            )
        }
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${anime.name} $seasonText",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = anime.enJpName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = if (partText.isNotBlank()) "${anime.year} · $partText" else anime.year.toString(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}


