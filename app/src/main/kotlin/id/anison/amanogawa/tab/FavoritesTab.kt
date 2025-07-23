package id.anison.amanogawa.tab

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import id.anison.amanogawa.MainScreenModel
import id.anison.amanogawa.MainUIEvent
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.feature.favorite.FavoritesScreen

object FavoritesTab : Tab {
    private fun readResolve(): Any = FavoritesTab
    override val key: ScreenKey
        get() = "favoriteTab"
    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(id = R.string.favorites_tab)
            val icon = painterResource(id = R.drawable.ic_favorite_24px)

            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<MainScreenModel>()

        Navigator(
            screen = FavoritesScreen(),
            key = "FavoriteTabNavigator",
        ) { navigator ->

            LaunchedEffect(navigator.lastItem) {
                screenModel.setIsRoot(navigator.lastItem is FavoritesScreen)
            }

            LaunchedEffect(Unit) {
                screenModel.events.collect { event ->
                    when (event) {
                        is MainUIEvent.ScrollToTop -> {

                        }

                        is MainUIEvent.NavigateToRoot -> {
                            Log.d("FavoriteTab", "toROOT")
                            navigator.popUntilRoot()
                        }
                    }
                }
            }

            SlideTransition(
                navigator = navigator,
            )
        }
    }
}