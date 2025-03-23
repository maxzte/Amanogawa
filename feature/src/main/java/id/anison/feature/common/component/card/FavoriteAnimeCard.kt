package id.anison.feature.common.component.card

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import id.anison.feature.R
import id.anison.feature.common.component.button.CircleButton
import id.anison.feature.common.component.button.FavoriteButton
import id.anison.feature.favorite.FavoriteUI
import id.anison.feature.favorite.FavoritesScreenEvents



@Composable
fun FavoriteAnimeCard(
    favorite: FavoriteUI,
    onEvents: (FavoritesScreenEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val color by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.primary,
        targetValue = MaterialTheme.colorScheme.onPrimary,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "newEpColorIndicator"
    )

    AnimeCardContent(
        title = favorite.name,
        poster = favorite.poster,
        year = favorite.year,
        season = favorite.season,
        part = favorite.part,
        modifier = modifier,
        onClick = { onEvents(FavoritesScreenEvents.OnFavoriteClick(favorite.favoriteId)) },
        extraContent = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                FavoriteButton(
                    isFavorite = true,
                    onFavoriteClick = { onEvents(FavoritesScreenEvents.RemoveFromFavorite(favorite)) },
                    backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                )
            }
            if (favorite.isNewEpisode) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
                    CircleButton(onClick = {}, modifier = Modifier.padding(4.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_circle_24),
                            contentDescription = null,
                            tint = color
                        )
                    }
                }
            }
        }
    )
}