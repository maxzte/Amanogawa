package id.anison.amanogawa.core.model



data class FavoriteUI(
    val favoriteId: Long,
    val isFavorite: Boolean,
    val isCurrent: Boolean,
    val name: String,
    val enJpName: String,
    val season: String,
    val part: String,
    val year: String,
    val poster: String,
    val posterThumb: String,
    val views: String,
    val liked: Int = 0,
    val disliked: Int = 0,
    val timestamp: Long,
    val schedule: String = "",
)
