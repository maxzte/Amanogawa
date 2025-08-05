package id.anison.amanogawa.core.data.favorite

import id.anison.amanogawa.core.database.favorite.FavoriteDbo
import id.anison.amanogawa.core.domain.model.Favorite

internal fun FavoriteDbo.toFavorite() = Favorite(
    favoriteId = favoriteId,
    isFavorite = isFavorite,
    isCurrent = isCurrent,
    name = name,
    enJpName = enJpName,
    season = season,
    part = part,
    year = year,
    poster = poster,
    posterThumb = posterThumb,
    views = views,
    liked = liked,
    disliked = disliked,
    timestamp = timestamp,
)

internal fun Favorite.toFavoriteDbo() = FavoriteDbo(
    favoriteId = favoriteId,
    isFavorite = isFavorite,
    isCurrent = isCurrent,
    name = name,
    enJpName = enJpName,
    season = season,
    part = part,
    year = year,
    poster = poster,
    posterThumb = posterThumb,
    views = views,
    liked = liked,
    disliked = disliked,
    timestamp = timestamp,
)