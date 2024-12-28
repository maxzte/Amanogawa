package id.anison.feature.search.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.R
import id.anison.feature.anime.AnimeScreen
import id.anison.feature.common.DataState
import id.anison.feature.common.component.Message
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.model.FoundAnimeUI
import id.anison.feature.search.screenmodel.SearchScreenModel
import id.anison.feature.search.state.SearchState

class SearchScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        SearchScreenContent(navigator)
    }

    @Composable
    internal fun SearchScreenContent(
        navigator: Navigator,
    ) {
        val screenModel = koinScreenModel<SearchScreenModel>()
        val query by screenModel.query.collectAsState()
        val searchState by screenModel.searchState.collectAsState()
        SearchLayout(
            screenModel = screenModel,
            navigator = navigator,
            searchQuery = query,
            searchState = searchState,
            onSearchQueryChange = { screenModel.updateQuery(it) },
            onSearchHistoryItemClick = { navigator.push(RandomTitlesCarouselScreen()) },
            onItemClick = {
                navigator.push(AnimeScreen(it.id))
                screenModel.addToSearchHistory(it)
            }
        )
    }

    @Composable
    fun SearchLayout(
        screenModel: SearchScreenModel,
        navigator: Navigator,
        searchQuery: String,
        searchState: SearchState,
        onSearchHistoryItemClick: () -> Unit,
        onItemClick: (FoundAnimeUI) -> Unit,
        onSearchQueryChange: (String) -> Unit
    ) {
        val foundTitles by screenModel.foundTitles.collectAsState()
        val isOnline by screenModel.isOnline.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                singleLine = true,
                enabled = true,
                shape = RoundedCornerShape(8),
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_anime),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_icon)
                    )
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AnimatedVisibility(
                            visible = !isOnline,
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_rounded_language_24),
                                contentDescription = stringResource(id = R.string.no_internet_icon)
                            )
                        }
                        AnimatedVisibility(
                            visible = searchQuery.isBlank(),
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(onClick = { onSearchHistoryItemClick() }) {
                                    Text(
                                        text = stringResource(R.string.random_title),
                                        color = LocalContentColor.current,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    )
                                }
                                IconButton(onClick = { navigator.push(ExtendedSearchScreen()) }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_filter_24),
                                        contentDescription = stringResource(R.string.filter_icon)
                                    )
                                }
                            }
                        }
                        AnimatedVisibility(visible = searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
            AnimatedVisibility(visible = searchQuery.isNotBlank()) {
                FoundAnimeList(
                    animeList = foundTitles,
                    onItemClicked = { onItemClick(it) }
                )
            }
            when (val searchHistory = searchState.searchHistory) {
                is DataState.Loading -> ProgressIndicator()
                is DataState.Error -> Message("Something went wrong")
                is DataState.Success -> {
                    AnimatedVisibility(
                        visible = searchQuery.isBlank() && searchHistory.data.isNotEmpty(),
                        exit = slideOutVertically(),
                    ) {
                        SearchHistoryList(
                            animeList = searchHistory.data,
                            onDeleteItem = { screenModel.deleteItemFromSearchHistory(it) },
                            onDeleteAll = { screenModel.clearSearchHistory() },
                            onItemClick = { navigator.push(AnimeScreen(it.id)) },
                        )
                    }
                }

                null -> Unit
            }
        }
    }
}

@Composable
fun SearchHistoryList(
    modifier: Modifier = Modifier,
    animeList: List<FoundAnimeUI>,
    onDeleteItem: (FoundAnimeUI) -> Unit,
    onDeleteAll: () -> Unit,
    onItemClick: (FoundAnimeUI) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
    ) {
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
            TextButton(onClick = { onDeleteAll() }) {
                Text(
                    text = stringResource(id = R.string.clear_all),
                    color = LocalContentColor.current,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = animeList,
                key = { it.id }
            ) { anime ->
                val season = if (anime.season != 0.toByte()) "${anime.season}" else ""
                Row(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth(),
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
                            .clickable { onItemClick(anime) },
                    ) {
                        Text(
                            text = "${anime.name}/${anime.en_jp_name} $season ${anime.year}",
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
                            .clickable { onDeleteItem(anime) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun FoundAnimeList(
    animeList: List<FoundAnimeUI>,
    onItemClicked: (FoundAnimeUI) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(
            items = animeList,
            key = { it.id }
        ) { anime ->
            val season = if (anime.season != 0.toByte()) "${anime.season}" else ""
            ListItem(
                headlineContent = {
                    Text(
                        text = "${anime.name} $season / ${anime.en_jp_name} ${anime.year}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_rounded_arrow_outward_24),
                        contentDescription = stringResource(R.string.link_arrow)
                    )
                },
                modifier = Modifier
                    .animateItem()
                    .clickable { onItemClicked(anime) },
            )
        }
    }
}

