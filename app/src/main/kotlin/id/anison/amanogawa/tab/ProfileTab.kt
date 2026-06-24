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
import id.anison.amanogawa.feature.profile.ProfileScreenModel
import id.anison.amanogawa.feature.profile.screen.ProfileScreen

object ProfileTab : AmanogawaTab {
    private fun readResolve(): Any = ProfileTab
    override val key: ScreenKey
        get() = "profileTab"
    override val amanogawaTabOptions: AmanogawaTabOptions
        @Composable
        get() {
            val title = stringResource(id = R.string.profile_tab)
            val icon = IconSource.VectorIcon(AmanogawaIcon.personOutlined)
            val selectedIcon = IconSource.VectorIcon(AmanogawaIcon.person)

            return remember {
                AmanogawaTabOptions(
                    index = 3u,
                    title = title,
                    selectedIcon = selectedIcon,
                    unselectedIcon = icon,
                )
            }
        }

    @Composable
    override fun Content() {
        val screenModel: MainScreenModel = koinScreenModel()
        val profileScreenModel: ProfileScreenModel = koinScreenModel()

        Navigator(
            screen = ProfileScreen(),
            key = "ProfileTabNavigator",
        ) { navigator ->

            AmanogawaBackHandler(navigator = navigator)

            LaunchedEffect(navigator.lastItem) {
                screenModel.setIsRoot(navigator.lastItem is ProfileScreen)
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
                            profileScreenModel.requestScrollToTop()
                        }

                        is MainUIEvent.NavigateToRoot -> {
                            Log.d("ProfileTab", "toROOT")
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
