package id.anison.data.favorite

import id.anison.database.favorite.FavoriteDbo

data class FavoriteDto(
    val favoriteId: Long,
    val isFavorite: Boolean = false,
    val isCurrent: Boolean = true,
    val name: String = "",
    val enJpName: String = "",
    val season: Byte = 0,
    val part: Byte = 0,
    val year: Short = 0,
    val poster: String = "",
    val views: Int = 0,
    val liked: Int = 0,
    val disliked: Int = 0,
    val episodes: Int = 0,
    val isNewEpisode: Boolean = false,
    val schedule: String = "",
    val timestamp: Long = 0,
)

internal fun FavoriteDbo.toFavoriteDto() = FavoriteDto(
    favoriteId = favoriteId,
    isFavorite = isFavorite,
    isCurrent = isCurrent,
    name = name,
    enJpName = enJpName,
    season = season,
    part = part,
    year = year,
    poster = poster,
    views = views,
    liked = liked,
    disliked = disliked,
    episodes = episodes,
    isNewEpisode = isNewEpisode,
    schedule = schedule,
    timestamp = timestamp,
)

internal fun FavoriteDto.toFavoriteDbo() = FavoriteDbo(
    favoriteId = favoriteId,
    isFavorite = isFavorite,
    isCurrent = isCurrent,
    name = name,
    enJpName = enJpName,
    season = season,
    part = part,
    year = year,
    poster = poster,
    views = views,
    liked = liked,
    disliked = disliked,
    episodes = episodes,
    isNewEpisode = isNewEpisode,
    schedule = schedule,
    timestamp = timestamp,
)
