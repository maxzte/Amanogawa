package id.anison.feature.common.component.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.anison.feature.common.bounceClick
import id.anison.feature.common.component.CoilImageLoader


@Composable
fun AnimeCardContent(
    title: String,
    poster: String,
    year: Short,
    season: Byte,
    part: Byte,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    extraContent: @Composable (BoxScope.() -> Unit)? = null
) {
    val seasonText = if (season != 0.toByte()) season.toString() else ""
    val partText = if (part != 0.toByte()) "Part $part" else ""

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        ElevatedCard(
            onClick = { onClick() },
            modifier = Modifier.bounceClick()
        ) {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(7f / 10f)) {
                CoilImageLoader(
                    titleImage = poster,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(7f / 10f),
                    showAnimatePlaceHolder = true
                )
                extraContent?.invoke(this)
            }
        }
        Text(
            text = "$title $seasonText",
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Text(
            text = if (partText.isNotBlank()) "$year · $partText" else year.toString(),
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}