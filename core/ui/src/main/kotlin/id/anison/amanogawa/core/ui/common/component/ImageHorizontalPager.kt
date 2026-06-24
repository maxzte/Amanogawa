package id.anison.amanogawa.core.ui.common.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ImageHorizontalPager(
    images: List<List<String>>,
    onImageClick: (Int, List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        pageCount = { images.size }
    )
    val thumbs = images.map { it[1] }
    val fullSize = images.map { it[0] }

    AmanogawaHorizontalPager(
        pagerState = pagerState,
        list = thumbs,
        minPageWidth = 200.dp,
        key = { thumbs[it] },
        pageContent = { image ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onImageClick(thumbs.indexOf(image), fullSize) },
            ) {
                AnimeImageLoader(
                    titleImage = image,
                    modifier = modifier.aspectRatio(16f / 9f),
                )
            }
        },
        modifier = modifier,
    )

}
