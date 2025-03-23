package id.anison.feature.favorite

import id.anison.data.favorite.FavoriteDto
import id.anison.feature.anime.model.AnimeDetailsUI


data class FavoriteUI(
    val favoriteId: Long,
    val isFavorite: Boolean,
    val isCurrent: Boolean,
    val name: String,
    val enJpName: String,
    val season: Byte,
    val part: Byte,
    val year: Short,
    val poster: String,
    val views: Int,
    val liked: Int = 0,
    val disliked: Int = 0,
    val timestamp: Long = 0,
    val episodeCount: Int = 0,
    val isNewEpisode: Boolean = false,
    val schedule: String = "",
)

internal fun FavoriteUI.toFavoriteDto() = FavoriteDto(
    isFavorite = isFavorite,
    isCurrent = isCurrent,
    favoriteId = favoriteId,
    name = name,
    enJpName = enJpName,
    season = season,
    part = part,
    year = year,
    poster = poster,
    views = views,
    liked = liked,
    disliked = disliked,
    episodes = episodeCount,
    isNewEpisode = isNewEpisode,
    schedule = schedule,
    timestamp = timestamp,
)

internal fun FavoriteDto.toFavoriteUI() = FavoriteUI(
    isFavorite = isFavorite,
    isCurrent = isCurrent,
    favoriteId = favoriteId,
    name = name,
    enJpName = enJpName,
    season = season,
    part = part,
    year = year,
    poster = poster,
    views = views,
    liked = liked,
    disliked = disliked,
    episodeCount = episodes,
    isNewEpisode = isNewEpisode,
    schedule = schedule,
    timestamp = timestamp,
)

internal fun AnimeDetailsUI.toFavoriteUI() = FavoriteUI(
    favoriteId = animeUI.animeId,
    isFavorite = false,
    isCurrent = animeUI.isCurrent,
    name = animeUI.name,
    enJpName = animeUI.enJpName,
    season = animeUI.season,
    part = animeUI.part,
    year = animeUI.year,
    poster = poster.toString(),
    views = animeUI.views,
    liked = liked,
    disliked = disliked,
    episodeCount = animeUI.episodeCount?.toInt() ?: 0,
    isNewEpisode = false,
    timestamp = System.currentTimeMillis(),
)