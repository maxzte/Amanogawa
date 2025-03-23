package id.anison.feature.favorite

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
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
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.data.settings.AppSettings
import id.anison.feature.R
import id.anison.feature.anime.AnimeDetailsScreen
import id.anison.feature.common.DataState
import id.anison.feature.common.bounceClick
import id.anison.feature.common.component.CoilImageLoader
import id.anison.feature.common.component.layout.AmanogawaTopBar
import id.anison.feature.common.component.card.FavoriteAnimeCard
import id.anison.feature.common.component.Message
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.common.component.button.FavoriteButton
import id.anison.feature.profile.SettingsScreenModel
import id.anison.feature.profile.SettingsUIState
import kotlin.math.min

class FavoritesScreen : Screen, ComponentActivity() {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<FavoriteScreenModel>()
        val settingsScreenModel = koinScreenModel<SettingsScreenModel>()
        val appSettingsState by settingsScreenModel.appSettings.collectAsState()
        val state by screenModel.favoriteState.collectAsState()

        when (val favoriteState = state.favoritesList) {
            is DataState.Loading -> {
                ProgressIndicator()
            }

            is DataState.Error -> {
                Message("something went wrong")
            }

            is DataState.Success -> {
                FavoriteScreenContent(
                    favorites = favoriteState.data,
                    appSettings = when (appSettingsState) {
                        is SettingsUIState.Loading -> {
                            AppSettings()
                        }

                        is SettingsUIState.Success -> {
                            (appSettingsState as SettingsUIState.Success).settings
                        }
                    },
                    onEvents = { event ->
                        when (event) {
                            is FavoritesScreenEvents.PopBack -> {
                                navigator.pop()
                            }

                            is FavoritesScreenEvents.RemoveFromFavorite -> {
                                screenModel.removeFromFavorite(event.favorite)
                            }

                            is FavoritesScreenEvents.ClearFavorite -> {
                                screenModel.clearFavorite()
                            }

                            is FavoritesScreenEvents.OnFavoriteClick -> {
                                navigator.push(AnimeDetailsScreen(event.favoriteId))
                            }
                        }
                    },
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FavoriteScreenContent(
        favorites: List<FavoriteUI>,
        appSettings: AppSettings,
        onEvents: (FavoritesScreenEvents) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
        ) {
            AmanogawaTopBar(
                title = stringResource(R.string.favorites),
                popNavigate = { onEvents(FavoritesScreenEvents.PopBack) },
                additionalActionBtnContent = {
                    TextButton(onClick = { onEvents(FavoritesScreenEvents.ClearFavorite) }) {
                        Text(text = stringResource(R.string.clear_all))
                    }
                },
                containerColor = Color.Transparent,
                scrollBehavior = scrollBehavior,
            )

            val configuration = LocalConfiguration.current
            val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            val count = if (!isLandscape) {
                appSettings.countColumnsInRow
            } else {
                appSettings.countColumnsInRowLandscape
            }
            if (!isLandscape) {
                FavoriteList(
                    favorites = favorites,
                    onEvents = onEvents,
                )
                /*FavoritesGrid(
                    favorites = favorites,
                    count = count,
                    onEvents = onEvents,
                )*/
            } else {
                FavoritesGrid(
                    favorites = favorites,
                    count = count,
                    onEvents = onEvents,
                )
            }
        }
    }

    @Composable
    fun FavoriteList(
        favorites: List<FavoriteUI>,
        onEvents: (FavoritesScreenEvents) -> Unit,
        modifier: Modifier = Modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(count = favorites.size, key = { fav -> favorites[fav].favoriteId }) {
                val favorite = favorites[it]
                val seasonText =
                    if (favorite.season != 0.toByte()) favorite.season.toString() else ""
                val partText = if (favorite.part != 0.toByte()) "Part ${favorite.part}" else ""
                Row(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .clickable {
                            onEvents(FavoritesScreenEvents.OnFavoriteClick(favorite.favoriteId))
                        },
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.8f),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ElevatedCard(
                            onClick = { onEvents(FavoritesScreenEvents.OnFavoriteClick(favorite.favoriteId)) },
                            modifier = Modifier.bounceClick()
                        ) {
                            CoilImageLoader(
                                titleImage = favorite.poster,
                                modifier = Modifier
                                    .fillMaxWidth(0.33f)
                                    .aspectRatio(7f / 10f)
                            )
                        }
                        Column {
                            Text(
                                text = "${favorite.name} $seasonText",
                                fontSize = 16.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = favorite.enJpName,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = if (partText.isNotBlank()) "${favorite.year} · $partText" else {
                                    favorite.year.toString()
                                },
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    FavoriteButton(
                        isFavorite = true,
                        onFavoriteClick = {
                            onEvents(
                                FavoritesScreenEvents.RemoveFromFavorite(
                                    favorite
                                )
                            )
                        },
                        backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                    )
                }
            }
            item {
                Spacer(Modifier.fillMaxWidth())
            }
        }
    }

    @Composable
    fun FavoritesGrid(
        favorites: List<FavoriteUI>,
        count: Int,
        onEvents: (FavoritesScreenEvents) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val lazyGridState = rememberLazyGridState()
        AnimatedVisibility(
            visible = favorites.isEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Message(text = stringResource(R.string.oops_you_don_t_have_favorites_yet))
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(count),
            modifier = modifier
                .padding(horizontal = 8.dp)
                .fillMaxSize(),
            state = lazyGridState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(favorites, key = { it.favoriteId }) { favorite ->
                FavoriteAnimeCard(
                    favorite = favorite,
                    onEvents = onEvents,
                    modifier = Modifier.animateItem()
                )
            }
            item {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                )
            }
        }
    }
}
