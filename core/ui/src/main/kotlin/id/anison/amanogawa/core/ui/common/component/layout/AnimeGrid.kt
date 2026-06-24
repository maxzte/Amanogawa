package id.anison.amanogawa.core.ui.common.component.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.core.ui.spacing

@Composable
fun AnimeVerticalGrid(
    state: LazyGridState,
    modifier: Modifier = Modifier,
    scaffoldPadding: PaddingValues,
    verticalSpacing: Dp = MaterialTheme.spacing.medium,
    content: LazyGridScope.() -> Unit,
) {
    val isLandscape = isLandscape()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(AmanogawaConst.minCardWidth(isLandscape).dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = MaterialTheme.spacing.medium,
            top = scaffoldPadding.calculateTopPadding() + MaterialTheme.spacing.medium,
            end = MaterialTheme.spacing.medium,
            bottom = if (!isLandscape) {
                scaffoldPadding.calculateBottomPadding() + AmanogawaConst.bottomBarInsets
            } else {
                scaffoldPadding.calculateBottomPadding()
            }
        ),
        state = state,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        content = content,
    )
}
