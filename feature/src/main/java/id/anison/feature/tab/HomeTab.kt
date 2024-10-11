package id.anison.feature.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import id.anison.feature.R
import id.anison.feature.common.MainScreenModel
import id.anison.feature.home.HomeScreen

object HomeTab : Tab {
    private fun readResolve(): Any = HomeTab

    override val key: ScreenKey
        get() = super.key

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(R.string.home_tab)
            val icon = painterResource(id = R.drawable.ic_home_24px)

            return remember {
                TabOptions(
                    index = 0u,
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
            screen = HomeScreen(),
            key = "homeNavigator",
        ) { navigator ->
            if (toRoute) {
                navigator.popUntilRoot()
                sm.setToRoute(false)
            }
            SlideTransition(
                navigator = navigator,
            )
        }
    }
}
