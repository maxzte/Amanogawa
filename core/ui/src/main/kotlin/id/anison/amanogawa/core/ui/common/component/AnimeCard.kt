package id.anison.amanogawa.core.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.anison.amanogawa.core.model.AnimeUI
import id.anison.amanogawa.core.ui.R


@Composable
fun AnimeCard(
    anime: AnimeUI,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimeCardContent(
        title = anime.name,
        poster = anime.posterThumb,
        year = anime.year,
        season = anime.season,
        part = anime.part,
        modifier = modifier,
        onClick = onClick,
        extraContent = {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(bottomEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = anime.views.toString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_watch_eye),
                    contentDescription = stringResource(id = R.string.views_icon),
                    modifier = Modifier.size(16.dp).padding(end = 4.dp)
                )
            }
            if (isCurrent) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (anime.timeStr.isNotBlank()) {
                        Text(
                            text = "${anime.timeStr} ${stringResource(id = R.string.ago)}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (!anime.schedule.isNullOrBlank()) {
                        Text(
                            text = anime.schedule!!,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}