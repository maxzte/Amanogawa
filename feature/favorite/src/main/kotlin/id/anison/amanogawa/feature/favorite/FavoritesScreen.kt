package id.anison.amanogawa.feature.favorite

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.amanogawa.core.model.FavoriteUI
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.DataStateContent
import id.anison.amanogawa.core.ui.common.columnsInRow
import id.anison.amanogawa.core.ui.common.component.Message
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.feature.favorite.ui.FavoriteCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

class FavoritesScreen : Screen, ComponentActivity() {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<FavoriteScreenModel>()
        val favoriteState by screenModel.favoriteState.collectAsState()
        val lazyGridState = screenModel.lazyGridState
        val isNavigating = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            screenModel.navigationEvent.collectLatest { event ->
                if (isNavigating.value) return@collectLatest
                isNavigating.value = true

                val target = event.toScreen()
                if (target != null) {
                    navigator.push(target)
                } else {
                    navigator.pop()
                }

                delay(250)
                isNavigating.value = false
            }
        }

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
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val count = columnsInRow()

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
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
        ) { paddingValues ->
            AnimatedVisibility(
                visible = favorites.isEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Message(text = stringResource(id = R.string.oops_you_don_t_have_favorites_yet))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(count),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding() + 8.dp,
                    start = 8.dp,
                    end = 8.dp,
                    bottom = paddingValues.calculateBottomPadding() + 8.dp
                ),
                modifier = Modifier.fillMaxSize(),
                state = lazyGridState,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
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
