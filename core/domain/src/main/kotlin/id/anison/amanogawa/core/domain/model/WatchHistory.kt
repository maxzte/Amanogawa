package id.anison.amanogawa.core.domain.model

data class WatchHistory(
    val animeId: Long,
    val animeName: String,
    val animeEnJpName: String = "",
    val animePoster: String,
    val animePosterThumb: String,
    val timestamp: Long = System.currentTimeMillis(),
    val lastEpisodeId: Long = 0,
    val lastEpisodeNumber: Int = 0,
    val lastEpisodeTitle: String = "",
    val lastSeason: String = "",
    val lastPart: String = "",
    val lastYear: String = "",
)
