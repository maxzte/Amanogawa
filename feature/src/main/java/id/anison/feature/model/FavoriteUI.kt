package id.anison.feature.model

data class FavoriteUI(
    val isFavorite: Boolean,
    val isCurrent: Boolean,
    val id: Long,
    val name: String,
    val en_jap_name: String,
    val season: Int,
    val year: Int,
    val poster: String,
    val views: Int,
    val liked: Int = 0,
    val disliked: Int = 0,
    val timestamp: Long = 0,
    val episodes: Int = 0,
    val isNewEpisode: Boolean = false,
    val schedule: String = "",
)
