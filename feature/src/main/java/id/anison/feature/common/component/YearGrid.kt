package id.anison.feature.common.component

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.anison.feature.common.component.button.FilterButton
import id.anison.feature.search.model.FilterUI


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun YearGrid(
    years: List<Short>,
    onClick: (FilterUI, String) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        item {
            ReusableFlowColumn(
                items = years,
                maxItemsInEachColumn = 2
            ) { year ->
                FilterButton(
                    name = year.toString(),
                    onClick = {
                        onClick(FilterUI(years = listOf(year)), year.toString())
                    },
                )
            }
        }
    }
}