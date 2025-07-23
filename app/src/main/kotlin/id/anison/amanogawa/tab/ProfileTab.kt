package id.anison.amanogawa.tab

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
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
import id.anison.amanogawa.feature.profile.screen.ProfileScreen

object ProfileTab : AmanogawaTab {
    private fun readResolve(): Any = ProfileTab
    override val key: ScreenKey
        get() = "profileTab"
    override val amanogawaTabOptions: AmanogawaTabOptions
        @Composable
        get() {
            val title = stringResource(id = R.string.profile_tab)
            val icon = rememberVectorPainter(image = Icons.Outlined.Person)
            val selectedIcon = rememberVectorPainter(image = Icons.Filled.Person)

            return remember {
                AmanogawaTabOptions(
                    index = 3u,
                    title = title,
                    selectedIcon = selectedIcon,
                    unselectedIcon = icon,
                )
            }
        }

    @OptIn(InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<MainScreenModel>()

        Navigator(
            screen = ProfileScreen(),
            key = "ProfileTabNavigator",
        ) { navigator ->

            LaunchedEffect(navigator.lastItem) {
                screenModel.setIsRoot(navigator.lastItem is ProfileScreen)
            }

            LaunchedEffect(Unit) {
                screenModel.events.collect { event ->
                    when (event) {
                        is MainUIEvent.ScrollToTop -> {

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