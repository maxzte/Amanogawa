package id.anison.amanogawa.core.domain.model

data class Favorite(
    val favoriteId: Long,
    val isFavorite: Boolean = false,
    val isCurrent: Boolean = true,
    val name: String = "",
    val enJpName: String = "",
    val season: Byte = 0,
    val part: Byte = 0,
    val year: Short = 0,
    val poster: String = "",
    val posterThumb: String = "",
    val views: Int = 0,
    val liked: Int = 0,
    val disliked: Int = 0,
    val timestamp: Long = 0,
)
