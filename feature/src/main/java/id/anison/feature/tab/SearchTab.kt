package id.anison.feature.tab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import id.anison.feature.R
import id.anison.feature.MainScreenModel
import id.anison.feature.search.screen.SearchScreen

object SearchTab : Tab {
    private fun readResolve(): Any = SearchTab

    override val key: ScreenKey
        get() = super.key

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(R.string.search_tab)
            val icon = rememberVectorPainter(Icons.Rounded.Search)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val sm = koinScreenModel<MainScreenModel>()
        val toRoute by sm.toRoute.collectAsState()
        Navigator(
            screen = SearchScreen(),
            key = "searchNavigator",
        ) { navigator ->
            if (toRoute) {
                navigator.popUntilRoot()
                sm.setNavigateToRoute(false)
            }
            SlideTransition(
                navigator = navigator,
            )
        }
    }
}