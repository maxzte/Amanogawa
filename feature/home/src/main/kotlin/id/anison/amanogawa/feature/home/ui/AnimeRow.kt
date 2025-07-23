package id.anison.amanogawa.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.anison.amanogawa.core.model.AnimeUI
import id.anison.amanogawa.core.ui.common.component.AnimeCard

@Composable
fun AnimeRow(
    animeList: List<AnimeUI>,
    onAnimeClick: (animeId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = animeList.size,
            key = { anime -> animeList[anime].animeId }) {
            val anime = animeList[it]
            AnimeCard(
                anime = anime,
                isCurrent = anime.isCurrent,
                onClick = {
                    onAnimeClick(anime.animeId)
                },
                modifier = modifier
            )
        }
    }
}