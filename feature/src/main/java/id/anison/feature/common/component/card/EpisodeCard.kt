package id.anison.feature.common.component.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.anison.feature.common.component.CoilImageLoader
import id.anison.feature.anime.model.EpisodeUI


@Composable
fun EpisodeCard(
    episode: EpisodeUI,
    botUrl: String,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    ElevatedCard(
        modifier = modifier
            .clickable { uriHandler.openUri(botUrl + "?start=sep_${episode.botId}") }
    ) {
        CoilImageLoader(
            modifier = Modifier.aspectRatio(16f / 9f),
            titleImage = episode.screenOriginal
        )
        Text(
            text = if (episode.isOva) {
                "OVA " + "${episode.number} ${episode.name}"
            } else {
                "${episode.number} ${episode.name}"
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(4.dp)
        )
    }
}