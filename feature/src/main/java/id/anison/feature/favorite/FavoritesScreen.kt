package id.anison.feature.favorite

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.R
import id.anison.feature.anime.AnimeScreen
import id.anison.feature.common.DataState
import id.anison.feature.common.component.CoilImageLoader
import id.anison.feature.common.component.CustomTopAppBar
import id.anison.feature.common.component.Message
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.common.component.button.CircleButton
import id.anison.feature.model.FavoriteUI

class FavoritesScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<FavoriteScreenModel>()
        val state = screenModel.favoriteState.collectAsState()
        when (val favoriteState = state.value.favoritesList) {
            is DataState.Loading -> ProgressIndicator()
            is DataState.Success -> FavoriteScreenContent(
                favorites = favoriteState.data,
                onEvents = { event ->
                    when (event) {
                        is FavoritesScreenEvents.PopBack -> navigator.pop()
                        is FavoritesScreenEvents.RemoveFromFavorite ->
                            screenModel.removeFromFavorite(event.favorite)

                        is FavoritesScreenEvents.ClearFavorite -> screenModel.clearFavorite()
                        is FavoritesScreenEvents.OnFavoriteClick -> navigator.push(AnimeScreen(event.id))
                    }
                },
            )

            is DataState.Error -> Message("something went wrong")
            else -> Message("something went wrong")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FavoriteScreenContent(
        favorites: List<FavoriteUI>,
        onEvents: (FavoritesScreenEvents) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Scaffold(
            topBar = {
                CustomTopAppBar(
                    title = stringResource(R.string.favorites),
                    actionBtnContent = {
                        TextButton(onClick = { onEvents(FavoritesScreenEvents.ClearFavorite) }) {
                            Text(text = stringResource(R.string.clear_all))
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            modifier = modifier
                .fillMaxSize()
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
        ) {
            val configuration = LocalConfiguration.current
            val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            if (!isLandscape) FavoritesList(
                favorites = favorites,
                onEvents = onEvents,
                modifier = Modifier.padding(top = it.calculateTopPadding())
            )
            else FavoritesGrid(
                favorites = favorites,
                onEvents = onEvents,
                modifier = Modifier.padding(top = it.calculateTopPadding())
            )
        }
    }

    @Composable
    fun FavoritesList(
        favorites: List<FavoriteUI>,
        onEvents: (FavoritesScreenEvents) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val lazyListState = rememberLazyListState()
        AnimatedVisibility(
            visible = favorites.isEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Message(text = stringResource(R.string.oops_you_don_t_have_favorites_yet))
        }
        LazyColumn(
            state = lazyListState,
            modifier = modifier.padding(horizontal = 8.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                )
            }
            items(favorites, key = { it.id }) { favorite ->
                Favorite(
                    modifier = Modifier.animateItem(),
                    favorite = favorite,
                    onEvents = onEvents,
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

    @Composable
    fun FavoritesGrid(
        favorites: List<FavoriteUI>,
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
            columns = GridCells.Fixed(3),
            modifier = modifier.padding(horizontal = 8.dp).fillMaxSize(),
            state = lazyGridState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(favorites, key = { it.id }) { favorite ->
                Favorite(
                    modifier = Modifier.animateItem(),
                    favorite = favorite,
                    onEvents = onEvents,
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

    @Composable
    fun Favorite(
        favorite: FavoriteUI,
        onEvents: (FavoritesScreenEvents) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val itemVisibility = remember { Animatable(1f) }
        var dominantColor by remember { mutableStateOf(Color.Unspecified) }
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val color by infiniteTransition.animateColor(
            initialValue = MaterialTheme.colorScheme.primary,
            targetValue = MaterialTheme.colorScheme.onPrimary,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = ""
        )
        ElevatedCard(
            onClick = { onEvents(FavoritesScreenEvents.OnFavoriteClick(favorite.id)) },
            modifier = modifier.alpha(itemVisibility.value),
        ) {
            Box(modifier = Modifier) {
                CoilImageLoader(
                    titleImage = favorite.poster,
                    radius = 25,
                    onPaletteGenerated = { palette: Palette? ->
                        dominantColor = palette?.getDominantColor(Color.Unspecified.toArgb())
                            ?.let { Color(it) }!!
                    },
                    modifier = Modifier.aspectRatio(16f / 9f)
                )
                if (favorite.isNewEpisode)
                    CircleButton(
                        onClick = {  },
                        containerColor = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.TopEnd),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_circle_24),
                            contentDescription = null,
                            tint = color,
                        )
                    }
            }
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = favorite.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                CircleButton(
                    onClick = { onEvents(FavoritesScreenEvents.RemoveFromFavorite(favorite)) },
                    containerColor = MaterialTheme.colorScheme.background,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = stringResource(id = R.string.liked)
                    )
                }
            }
        }
    }
}
