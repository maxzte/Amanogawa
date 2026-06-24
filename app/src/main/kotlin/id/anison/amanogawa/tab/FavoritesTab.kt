package id.anison.amanogawa.tab

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import id.anison.amanogawa.MainScreenModel
import id.anison.amanogawa.MainUIEvent
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.feature.favorite.FavoriteScreenModel
import id.anison.amanogawa.feature.favorite.FavoritesScreen

object FavoritesTab : AmanogawaTab {
    private fun readResolve(): Any = FavoritesTab
    override val key: ScreenKey
        get() = "favoriteTab"
    override val amanogawaTabOptions: AmanogawaTabOptions
        @Composable get() {
            val title = stringResource(id = R.string.favorites_tab)
            val icon = IconSource.VectorIcon(AmanogawaIcon.favoriteBorder)
            val selectedIcon = IconSource.VectorIcon(AmanogawaIcon.favorite)

            return remember {
                AmanogawaTabOptions(
                    index = 0u,
                    title = title,
                    unselectedIcon = icon,
                    selectedIcon = selectedIcon,
                )
            }
        }

    @Composable
    override fun Content() {
        val screenModel: MainScreenModel = koinScreenModel()
        val favoriteScreenModel: FavoriteScreenModel = koinScreenModel()

        Navigator(
            screen = FavoritesScreen(),
            key = "FavoriteTabNavigator",
        ) { navigator ->

            AmanogawaBackHandler(navigator = navigator)

            LaunchedEffect(navigator.lastItem) {
                screenModel.setIsRoot(navigator.lastItem is FavoritesScreen)
            }

            LaunchedEffect(Unit) {
                screenModel.events.collect { event ->
                    when (event) {
                        is MainUIEvent.ScrollToTop -> {
                            favoriteScreenModel.requestScrollToTop()
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