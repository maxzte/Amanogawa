package id.anison.amanogawa.tab

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import id.anison.amanogawa.feature.home.HomeScreen


@OptIn(InternalVoyagerApi::class)
@Composable
internal fun AmanogawaBackHandler(
    navigator: Navigator,
    localTabNavigator: TabNavigator = LocalTabNavigator.current,
) {
    val activity = LocalActivity.currentOrThrow

    BackHandler(
        enabled = true,
        onBack = {
            if (navigator.canPop) {
                navigator.pop()
            } else {
                if (navigator.lastItem is HomeScreen) {
                    activity.moveTaskToBack(true)
                } else {
                    localTabNavigator.current = HomeTab
                }
            }
        }
    )
}