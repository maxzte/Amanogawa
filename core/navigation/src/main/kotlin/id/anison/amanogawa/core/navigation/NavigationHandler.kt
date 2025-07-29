package id.anison.amanogawa.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <T> NavigationHandler(
    navigator: Navigator = LocalNavigator.currentOrThrow,
    navigationState: SharedFlow<T>,
    onPreNavigate: (T) -> Unit = {},
    toScreen: T.() -> Screen?,
    debounceDelay: Long = 250
) {
    val isNavigating = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        navigationState.collectLatest { event: T ->
            if (isNavigating.value) return@collectLatest
            isNavigating.value = true

            onPreNavigate(event)

            val target = event.toScreen()
            if (target != null) {
                navigator.push(target)
            } else {
                navigator.pop()
            }

            delay(debounceDelay)
            isNavigating.value = false
        }
    }
}
