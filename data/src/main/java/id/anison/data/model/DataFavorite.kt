package id.anison.data.model

data class DataFavorite(
    val isFavorite: Boolean = false,
    val isCurrent: Boolean = true,
    val id:Long = 0,
    val name: String = "",
    val en_jap_name: String = "",
    val season: Int = 0,
    val year: Int = 0,
    val poster: String = "",
    val views: Int = 0,
    val liked: Int = 0,
    val disliked: Int = 0,
    val episodes: Int = 0,
    val isNewEpisode: Boolean = false,
    val schedule: String = "",
    val timestamp: Long = 0,
)
