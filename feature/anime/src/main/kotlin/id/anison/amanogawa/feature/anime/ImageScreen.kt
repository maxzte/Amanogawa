@file:OptIn(ExperimentalMaterial3Api::class)

package id.anison.amanogawa.feature.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.common.component.AmanogawaHorizontalPager
import id.anison.amanogawa.core.ui.common.component.AnimeImageLoader
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.button.FilledTopBarIcon
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.toggleScale
import net.engawapg.lib.zoomable.zoomable


data class ImageScreen(
    val index: Int,
    val images: List<String>
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val pagerState = rememberPagerState(
            pageCount = { images.size }
        )

        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    navigationIconContent = {
                        FilledTopBarIcon(
                            onClick = { navigator.pop() },
                            backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                            content = {
                                Icon(
                                    imageVector = AmanogawaIcon.arrowBack,
                                    contentDescription = stringResource(id = R.string.arrow_back),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        )
                    },
                    containerColor = Color.Transparent,
                    showBottomLine = false,
                )
            },
        ) { paddingValues ->
            var size by remember { mutableStateOf(IntSize.Zero) }
            val zoomState = rememberZoomState(contentSize = size.toSize())

            Box {
                HorizontalPager(
                    state = pagerState,
                    pageSize = PageSize.Fill,
                    key = { images[it] },
                    verticalAlignment = Alignment.Top,
                    beyondViewportPageCount = 1,
                    userScrollEnabled = zoomState.scale == 1f,
                    modifier = Modifier.fillMaxSize(),
                    pageContent = { page ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clipToBounds(),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimeImageLoader(
                                titleImage = images[page],
                                useCache = false,
                                showAmanogawaSpinner = true,
                                modifier = Modifier
                                    .onSizeChanged { size = it }
                                    .aspectRatio(if (images.size == 1) 7f / 10f else 16f / 9f)
                                    .zoomable(
                                        zoomState = zoomState,
                                        onDoubleTap = { position ->
                                            zoomState.toggleScale(
                                                targetScale = 2f,
                                                position
                                            )
                                        },
                                        scrollGesturePropagation = ScrollGesturePropagation.ContentEdge
                                    )
                            )
                        }
                    }
                )

                Row(
                    modifier =  Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = paddingValues.calculateBottomPadding() + 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color = if (pagerState.currentPage == iteration) Color.DarkGray
                        else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(16.dp)
                        )
                    }
                }
            }
        }
    }

}