package id.anison.amanogawa.feature.favorite

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import id.anison.amanogawa.core.model.FavoriteUI
import id.anison.amanogawa.core.navigation.NavigationHandler
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.DataStateContent
import id.anison.amanogawa.core.ui.common.component.Message
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.component.layout.AnimeVerticalGrid
import id.anison.amanogawa.feature.favorite.ui.FavoriteCard

class FavoritesScreen : Screen, ComponentActivity() {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<FavoriteScreenModel>()
        val favoriteState by screenModel.favoriteState.collectAsState()
        val lazyGridState = screenModel.lazyGridState

        NavigationHandler(
            navigationState = screenModel.navigationEvent,
            toScreen = { toScreen() }
        )

        DataStateContent(
            state = favoriteState,
            errorContent = {
                Message("something went wrong")
            },
            content = { favoriteList ->
                FavoriteScreenContent(
                    lazyGridState = lazyGridState,
                    favorites = favoriteList,
                    onEvents = { screenModel.onEvent(it) },
                )
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FavoriteScreenContent(
        lazyGridState: LazyGridState,
        favorites: List<FavoriteUI>,
        onEvents: (FavoritesScreenEvents) -> Unit,
    ) {
        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    title = stringResource(id = R.string.favorites),
                    navigationIconContent = {
                        ArrowBackButton(
                            onClick = { onEvents(FavoritesScreenEvents.PopBack) },
                        )
                    },
                    additionalActionBtnContent = {
                        if (favorites.isNotEmpty()) {
                            TextButton(
                                onClick = { onEvents(FavoritesScreenEvents.ClearFavorite) },
                                content = {
                                    Text(text = stringResource(id = R.string.clear_all))
                                }
                            )
                        }
                    },
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            AnimatedVisibility(
                visible = favorites.isEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Message(text = stringResource(id = R.string.oops_you_don_t_have_favorites_yet))
            }

            AnimeVerticalGrid(
                state = lazyGridState,
                scaffoldPadding = paddingValues,
            ) {
                items(items = favorites, key = { it.favoriteId }) { favorite ->
                    FavoriteCard(
                        favorite = favorite,
                        onEvents = onEvents,
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}
