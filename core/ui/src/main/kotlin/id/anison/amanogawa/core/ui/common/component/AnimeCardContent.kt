package id.anison.amanogawa.core.ui.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.anison.amanogawa.core.ui.common.bounceClick


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
    val seasonText = if (season != 0.toByte()) "Season $season" else ""
    val partText = if (part != 0.toByte()) "Part $part" else ""

    Column(modifier = modifier.fillMaxWidth()) {
        ElevatedCard(
            onClick = { onClick() },
            modifier = Modifier.bounceClick()
        ) {
            Box {
                AnimeImageLoader(
                    titleImage = poster,
                    useCache = true,
                    modifier = Modifier.aspectRatio(7f / 10f)
                )
                extraContent?.invoke(this)
            }
        }
        Spacer(
            modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
        )
        Text(
            text = title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(
            modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
        )
        Row {
            if (seasonText.isNotBlank()) {
                Text(
                    text = "$seasonText · ",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                )
            }
            if (partText.isNotBlank()) {
                Text(
                    text = "$partText · ",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                )
            }
            Text(
                text = year.toString(),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                color = Color.Gray,
            )
        }
    }
}