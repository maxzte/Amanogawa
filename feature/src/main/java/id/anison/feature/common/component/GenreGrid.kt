package id.anison.feature.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.anison.feature.common.component.button.FilterButton
import id.anison.feature.common.measureTextWidth
import id.anison.feature.search.model.FilterUI
import id.anison.feature.search.model.GenreUI


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreGrid(
    genres: List<GenreUI>,
    onClick: (FilterUI, String) -> Unit,
) {
    val textStyle = TextStyle(fontSize = 16.sp)
    val max = genres.maxBy { it.name.length }.name
    val m1 = measureTextWidth(max, textStyle)

    LazyRow(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ReusableFlowColumn(
                items = genres,
                maxItemsInEachColumn = 3,
            ) { genre ->
                FilterButton(
                    name = genre.name,
                    onClick = {
                        onClick(FilterUI(genres = listOf(genre)), genre.name)
                    },
                    maxWidth = m1,
                )
            }
        }
    }
}

