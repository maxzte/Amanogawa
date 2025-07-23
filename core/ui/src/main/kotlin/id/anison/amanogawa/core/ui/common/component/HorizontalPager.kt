package id.anison.amanogawa.core.ui.common.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> AmanogawaHorizontalPager(
    modifier: Modifier = Modifier,
    list: List<T>,
    pagerState: PagerState? = null,
    pageContent: @Composable (T) -> Unit,
    countColumnsInRow: Int? = null,
    key: ((Int) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    pageSpacing: Dp = 8.dp,
) {

    val state = pagerState ?: rememberPagerState(pageCount = { list.size })
    val pageSize = if (countColumnsInRow != null) {
        object : PageSize {
            override fun Density.calculateMainAxisPageSize(
                availableSpace: Int,
                pageSpacing: Int,
            ): Int {
                val targetCount = countColumnsInRow - 0.35
                return ((availableSpace - 2 * pageSpacing) / targetCount).toInt()
            }
        }
    } else PageSize.Fill

    HorizontalPager(
        state = state,
        pageSize = pageSize,
        key = key,
        contentPadding = contentPadding,
        pageSpacing = pageSpacing,
        verticalAlignment = Alignment.Top,
        beyondViewportPageCount = 1,
        modifier = modifier,
        pageContent = {
            pageContent(list[it])
        }
    )
}