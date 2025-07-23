package id.anison.amanogawa.feature.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.anison.amanogawa.core.model.SearchResultUI
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.component.AnimeImageLoader

@Composable
internal fun SearchResultList(
    searchResult: List<SearchResultUI>,
    onAnimeClick: (SearchResultUI) -> Unit,
) {
    val lazyListState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = searchResult,
            key = { it.animeId },
            itemContent = { anime ->
                SearchResultItem(
                    anime = anime,
                    onAnimeClick = { onAnimeClick(anime) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }
        )
    }
}

@Composable
private fun SearchResultItem(
    anime: SearchResultUI,
    onAnimeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val seasonText = anime.season.takeIf { it != 0.toByte() }?.let { "Season $it" } ?: ""
    val partText = anime.part.takeIf { it != 0.toByte() }?.let { "Part $it" } ?: ""

    Row(
        modifier = modifier.clickable { onAnimeClick() },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ElevatedCard(
            modifier = Modifier
                .widthIn(max = 50.dp)
                .aspectRatio(7f / 10f),
            content = {
                AnimeImageLoader(
                    titleImage = anime.posterMini,
                    useCache = true,
                )
            }
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = anime.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = anime.enJpName,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                color = Color.Gray,
            )
            Row {
                if (seasonText.isNotBlank()) {
                    Text(
                        text = "$seasonText · ",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                    )
                }
                if (partText.isNotBlank()) {
                    Text(
                        text = "$partText · ",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                    )
                }
                Text(
                    text = anime.year.toString(),
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    color = Color.Gray,
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_rounded_arrow_outward_24),
            contentDescription = null,
        )
    }
}