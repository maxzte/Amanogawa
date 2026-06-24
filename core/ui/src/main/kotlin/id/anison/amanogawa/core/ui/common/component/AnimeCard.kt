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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.spacing


@Composable
fun AnimeCard(
    title: String,
    poster: String,
    year: String,
    season: String,
    part: String,
    views: String,
    timeStr: String,
    schedule: String?,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimeCardContent(
        title = title,
        poster = poster,
        year = year,
        season = season,
        part = part,
        modifier = modifier,
        onClick = onClick,
        extraContent = {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(bottomEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
            ) {
                Text(
                    text = views,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Icon(
                    imageVector = AmanogawaIcon.eye,
                    contentDescription = stringResource(id = R.string.views_icon),
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = MaterialTheme.spacing.extraSmall)
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
                    if (timeStr.isNotBlank()) {
                        Text(
                            text = "$timeStr ${stringResource(id = R.string.ago)}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (!schedule.isNullOrBlank()) {
                        Text(
                            text = schedule,
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
