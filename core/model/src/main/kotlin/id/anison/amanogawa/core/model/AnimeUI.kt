package id.anison.amanogawa.core.model



data class AnimeUI(
    val animeId: Long,
    val name: String,
    val isCurrent: Boolean,
    val enJpName: String,
    val dubedEpisodes: Int,
    val episodesCustom: String?,
    val episodesTotal: String?,
    val extraTotal: Int,
    val ovaTotal: Int,
    val year: Short,
    val season: Byte,
    val part: Byte,
    val views: Int,
    val posterThumb: String,
    val posterMini: String,
    val schedule: String?,
    val episodeCount: String? = "",
    val newEpisode: Int = 0,
    val isExtra: Boolean = false,
    val isOva: Boolean = false,
    val time: Double = 0.0,
    val timeStr: String = "",
)
