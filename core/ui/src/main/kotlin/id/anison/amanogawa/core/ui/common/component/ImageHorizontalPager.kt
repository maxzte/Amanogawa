package id.anison.amanogawa.core.ui.common.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import id.anison.amanogawa.core.ui.common.isLandscape

@Composable
fun ImageHorizontalPager(
    images: List<List<String>>,
    onImageClick: (Int, List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLandscape = isLandscape()
    val countColumnsInRow = if (!isLandscape) 2 else 3 // TODO replace to app settings

    var currentPage by rememberSaveable { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(
        initialPage = currentPage,
        pageCount = { images.size }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            currentPage = page
        }
    }

    val thumbs = images.map { it[1] }
    val fullSize = images.map { it[0] }

    AmanogawaHorizontalPager(
        pagerState = pagerState,
        list = thumbs,
        countColumnsInRow = countColumnsInRow,
        key = { thumbs[it] },
        pageContent = { image ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onImageClick(thumbs.indexOf(image), fullSize) },
            ) {
                AnimeImageLoader(
                    titleImage = image,
                    useCache = false,
                    modifier = modifier.aspectRatio(16f / 9f),
                )
            }
        },
        modifier = modifier,
    )

}
