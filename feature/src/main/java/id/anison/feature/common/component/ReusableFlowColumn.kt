package id.anison.feature.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ReusableFlowColumn(
    items: List<T>,
    maxItemsInEachColumn: Int,
    content: @Composable (T) -> Unit
) {
    FlowColumn(
        maxItemsInEachColumn = maxItemsInEachColumn,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { item ->
            content(item)
        }
    }
}