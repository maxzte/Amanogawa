package id.anison.feature.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ReusableFlowRow(
    items: List<T>,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    content: @Composable (T) -> Unit
) {
    FlowRow(
        modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
        maxItemsInEachRow = maxItemsInEachRow,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { item ->
            content(item)
        }
    }
}