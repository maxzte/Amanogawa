package id.anison.feature.common.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import id.anison.feature.R


@Composable
fun UserRatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    ratingIconPainter: Painter = painterResource(id = R.drawable.ic_circle_24),
) {
    Row(modifier = modifier.wrapContentSize()) {
        repeat(rating) {
            StarIcon(painter = ratingIconPainter, isUnRating = false)
        }
        repeat(5 - rating) {
            StarIcon(painter = ratingIconPainter, isUnRating = true)
        }
    }
}

@Composable
fun StarIcon(
    painter: Painter,
    isUnRating: Boolean,
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painter,
        contentDescription = null,
        modifier = modifier.size(20.dp),
        tint = if (!isUnRating) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onBackground,
    )
}