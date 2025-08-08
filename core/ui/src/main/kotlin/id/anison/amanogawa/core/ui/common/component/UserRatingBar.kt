package id.anison.amanogawa.core.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.anison.amanogawa.core.ui.R


@Composable
fun UserRatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    ratingIconPainter: Painter = painterResource(id = R.drawable.ic_circle_24),
) {
    Row(
        modifier = modifier.wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(rating) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.inverseSurface)
            )
        }

        repeat(5 - rating) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(
                        width = Dp.Hairline,
                        color = MaterialTheme.colorScheme.inverseSurface,
                        shape = CircleShape
                    )

            )
        }
    }
}
