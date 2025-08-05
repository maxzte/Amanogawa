package id.anison.amanogawa.core.domain

import id.anison.amanogawa.core.domain.model.Favorite
import id.anison.amanogawa.core.model.AnimeDetailsUI
import id.anison.amanogawa.core.model.FavoriteUI

fun FavoriteUI.toFavorite() = Favorite(
    isFavorite = isFavorite,
    isCurrent = isCurrent,
    favoriteId = favoriteId,
    name = name,
    enJpName = enJpName,
    season = if (season.isNotEmpty()) season.toByte() else 0,
    part = if (part.isNotEmpty()) part.toByte() else 0,
    year = year.toShort(),
    poster = poster,
    posterThumb = posterThumb,
    views = views.toInt(),
    liked = liked,
    disliked = disliked,
    timestamp = timestamp,
)

fun Favorite.toFavoriteUI() = FavoriteUI(
    isFavorite = isFavorite,
    isCurrent = isCurrent,
    favoriteId = favoriteId,
    name = name,
    enJpName = enJpName,
    season = if (season != 0.toByte()) season.toString() else "",
    part = if (part != 0.toByte()) part.toString() else "",
    year = year.toString(),
    poster = poster,
    posterThumb = posterThumb,
    views = views.toString(),
    liked = liked,
    disliked = disliked,
    timestamp = timestamp,
)

fun AnimeDetailsUI.toFavoriteUI() = FavoriteUI(
    favoriteId = animeUI.animeId,
    isFavorite = false,
    isCurrent = animeUI.isCurrent,
    name = animeUI.name,
    enJpName = animeUI.enJpName,
    season = animeUI.season,
    part = animeUI.part,
    year = animeUI.year,
    poster = poster,
    posterThumb = animeUI.posterThumb,
    views = animeUI.views,
    liked = liked,
    disliked = disliked,
    timestamp = System.currentTimeMillis(),
)