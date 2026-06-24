package id.anison.amanogawa.core.ui.common.component

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.anison.amanogawa.core.ui.spacing
import kotlin.math.max

@Composable
fun <T> AmanogawaHorizontalPager(
    modifier: Modifier = Modifier,
    list: List<T>,
    pagerState: PagerState? = null,
    pageContent: @Composable (T) -> Unit,
    key: ((Int) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues(MaterialTheme.spacing.medium),
    pageSpacing: Dp = MaterialTheme.spacing.small,
    userScrollEnabled: Boolean = true,
    minPageWidth: Dp = 150.dp,
) {
    val layoutDirection = LocalLayoutDirection.current
    BoxWithConstraints {
        val availableWidth =
            maxWidth - contentPadding.calculateLeftPadding(layoutDirection) - contentPadding.calculateRightPadding(
                layoutDirection
            )

        val columns = max(
            1,
            (availableWidth / minPageWidth).toInt()
        )

        val pageWidth = (availableWidth - pageSpacing * (columns - 1).toFloat()) / columns

        val state = pagerState ?: rememberPagerState(pageCount = { list.size })

        HorizontalPager(
            state = state,
            pageSize = PageSize.Fixed(pageWidth),
            key = key,
            contentPadding = contentPadding,
            pageSpacing = pageSpacing,
            verticalAlignment = Alignment.Top,
            beyondViewportPageCount = 1,
            userScrollEnabled = userScrollEnabled,
            modifier = modifier,
            pageContent = {
                pageContent(list[it])
            }
        )
    }
}
