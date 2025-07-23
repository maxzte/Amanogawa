package id.anison.amanogawa.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.anison.amanogawa.feature.home.Chip


@Composable
fun FilterChipRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(Chip.entries.size) { filterIndex ->
            val filter = Chip.entries[filterIndex]
            ElevatedFilterChip(
                selected = selectedFilter == filter.title,
                onClick = { onFilterSelected(filter.title) },
                label = {
                    Text(filter.title)
                }
            )
        }
    }
}