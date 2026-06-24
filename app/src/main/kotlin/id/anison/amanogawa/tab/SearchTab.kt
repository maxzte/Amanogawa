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
import id.anison.amanogawa.feature.anime.ImageScreen
import id.anison.amanogawa.feature.search.screen.RandomAnimeCarouselScreen
import id.anison.amanogawa.feature.search.screen.SearchScreen
import id.anison.amanogawa.feature.search.screenmodel.SearchScreenModel

object SearchTab : AmanogawaTab {
    private fun readResolve(): Any = SearchTab
    override val key: ScreenKey
        get() = "searchTab"
    override val amanogawaTabOptions: AmanogawaTabOptions
        @Composable
        get() {
            val title = stringResource(id = R.string.search_tab)
            val icon = IconSource.VectorIcon(AmanogawaIcon.searchOutlined)
            val selectedIcon = IconSource.VectorIcon(AmanogawaIcon.search)

            return remember {
                AmanogawaTabOptions(
                    index = 1u,
                    title = title,
                    unselectedIcon = icon,
                    selectedIcon = selectedIcon
                )
            }
        }

    @Composable
    override fun Content() {
        val screenModel: MainScreenModel = koinScreenModel()
        val searchScreenModel: SearchScreenModel = koinScreenModel()

        Navigator(
            screen = SearchScreen(),
            key = "SearchTabNavigator",
        ) { navigator ->

            AmanogawaBackHandler(navigator = navigator)

            LaunchedEffect(navigator.lastItem) {
                screenModel.setIsRoot(navigator.lastItem is SearchScreen)
                when (navigator.lastItem) {
                    is RandomAnimeCarouselScreen -> {
                        screenModel.setBottomBarVisible(false)
                    }

                    is ImageScreen -> {
                        screenModel.setBottomBarVisible(false)
                    }

                    else -> {
                        screenModel.setBottomBarVisible(true)
                    }
                }
            }

            LaunchedEffect(Unit) {
                screenModel.events.collect { event ->
                    when (event) {
                        is MainUIEvent.ScrollToTop -> {
                            searchScreenModel.requestScrollToTop()
                        }

                        is MainUIEvent.NavigateToRoot -> {
                            Log.d("SearchTab", "toROOT")
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