package id.anison.amanogawa.core.domain

import id.anison.amanogawa.core.data.favorite.Favorite
import id.anison.amanogawa.core.model.AnimeDetailsUI
import id.anison.amanogawa.core.model.FavoriteUI

fun FavoriteUI.toFavorite() = Favorite(
    isFavorite = isFavorite,
    isCurrent = isCurrent,
    favoriteId = favoriteId,
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
    episodes = episodeCount,
    isNewEpisode = isNewEpisode,
    schedule = schedule,
    timestamp = timestamp,
)

fun Favorite.toFavoriteUI() = FavoriteUI(
    isFavorite = isFavorite,
    isCurrent = isCurrent,
    favoriteId = favoriteId,
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
    episodeCount = episodes,
    isNewEpisode = isNewEpisode,
    schedule = schedule,
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
    episodeCount = animeUI.episodeCount?.toInt() ?: 0,
    isNewEpisode = false,
    timestamp = System.currentTimeMillis(),
)