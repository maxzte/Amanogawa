package id.anison.feature.common.component.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
fun AmanogawaScaffold(
    isBottomBarVisible: Boolean,
    isTopBarVisible: Boolean,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    snackBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    railBar: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit
) {
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val showNavigationBar = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT


    Surface(
        color = containerColor,
        contentColor = contentColor
    ) {
        if (!showNavigationBar) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            ) {
                railBar()
                Column(modifier = modifier.weight(1f)) {
                    /*if (isTopBarVisible) {
                        topBar()
                    }*/
                    content(PaddingValues(0.dp))
                }
            }
        } else {
            Scaffold(
                /*topBar = {
                    if (isTopBarVisible) {
                        topBar()
                    }
                },*/
                bottomBar = {
//                        if (isBottomBarVisible) {
                    AnimatedVisibility(
                        visible = isBottomBarVisible,
                        enter = slideInVertically(/*animationSpec = tween(durationMillis = 0)*/) { height -> height },
                        exit = slideOutVertically(/*animationSpec = tween(durationMillis = 0)*/) { height -> height }
                    ) {
                        bottomBar()
                    }
//                        }
                },
                snackbarHost = {
                    snackBar()
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                modifier = modifier.fillMaxSize()
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .consumeWindowInsets(padding)
                ) {
                    content(padding)
                }
            }
        }
    }
}