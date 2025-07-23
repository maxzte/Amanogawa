package id.anison.amanogawa.tab

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import id.anison.amanogawa.MainScreenModel
import id.anison.amanogawa.MainUIEvent
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.feature.search.screen.SearchScreen

object SearchTab : AmanogawaTab {
    private fun readResolve(): Any = SearchTab
    override val key: ScreenKey
        get() = "searchTab"
    override val amanogawaTabOptions: AmanogawaTabOptions
        @Composable
        get() {
            val title = stringResource(id = R.string.search_tab)
            val icon = rememberVectorPainter(image = Icons.Outlined.Search)
            val selectedIcon = rememberVectorPainter(image = Icons.Filled.Search)

            return remember {
                AmanogawaTabOptions(
                    index = 1u,
                    title = title,
                    unselectedIcon = icon,
                    selectedIcon = selectedIcon
                )
            }
        }

    @OptIn(InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<MainScreenModel>()

        Navigator(
            screen = SearchScreen(),
            key = "SearchTabNavigator",
        ) { navigator ->

            LaunchedEffect(navigator.lastItem) {
                screenModel.setIsRoot(navigator.lastItem is SearchScreen)
            }

            LaunchedEffect(Unit) {
                screenModel.events.collect { event ->
                    when (event) {
                        is MainUIEvent.ScrollToTop -> {

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