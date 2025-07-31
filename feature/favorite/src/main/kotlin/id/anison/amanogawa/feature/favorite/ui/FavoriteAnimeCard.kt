package id.anison.amanogawa.feature.favorite.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import id.anison.amanogawa.core.model.FavoriteUI
import id.anison.amanogawa.core.ui.common.component.AnimeCardContent
import id.anison.amanogawa.core.ui.common.component.button.FavoriteButton
import id.anison.amanogawa.feature.favorite.FavoritesScreenEvents


@Composable
internal fun FavoriteCard(
    favorite: FavoriteUI,
    onEvents: (FavoritesScreenEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimeCardContent(
        title = favorite.name,
        poster = favorite.posterThumb,
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
        }
    )
}