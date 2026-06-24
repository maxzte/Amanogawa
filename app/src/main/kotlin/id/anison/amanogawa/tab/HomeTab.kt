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
import id.anison.amanogawa.feature.home.HomeScreen
import id.anison.amanogawa.feature.home.HomeScreenModel

object HomeTab : AmanogawaTab {
    private fun readResolve(): Any = HomeTab
    override val key: ScreenKey
        get() = "homeTab"
    override val amanogawaTabOptions: AmanogawaTabOptions
        @Composable get() {
            val title = stringResource(id = R.string.home_tab)
            val icon = IconSource.VectorIcon(AmanogawaIcon.homeOutlined)
            val selectedIcon = IconSource.VectorIcon(AmanogawaIcon.home)

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
        val homeScreenModel: HomeScreenModel = koinScreenModel()
        Navigator(
            screen = HomeScreen(),
            key = "HomeTabNavigator",
        ) { navigator ->

            AmanogawaBackHandler(navigator = navigator)

            LaunchedEffect(navigator.lastItem) {
                screenModel.setIsRoot(navigator.lastItem is HomeScreen)
                if (navigator.lastItem is ImageScreen) {
                    screenModel.setBottomBarVisible(false)
                } else {
                    screenModel.setBottomBarVisible(true)
                }
            }

            LaunchedEffect(Unit) {
                screenModel.events.collect { event ->
                    when (event) {
                        is MainUIEvent.ScrollToTop -> {
                            homeScreenModel.requestScrollToTop()
                            Log.d("HomeTab", "toTOP")
                        }

                        is MainUIEvent.NavigateToRoot -> {
                            Log.d("HomeTab", "toROOT")
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
