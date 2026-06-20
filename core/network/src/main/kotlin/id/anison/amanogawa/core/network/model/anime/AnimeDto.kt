package id.anison.amanogawa.core.network.model.anime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/***
 * class that represent anime card on main page [ref](https://amanogawa.space/)
 */
@Serializable
data class AnimeDto(

    @SerialName("id")
    val animeId: Long,

    val name: String = "",

    @SerialName("is_current")
    val isCurrent: Boolean = false,

    @SerialName("en_jp_name")
    val enJpName: String = "",

    @SerialName("dubed_episodes")
    val dubedEpisodes: Int = 0,

    @SerialName("episodes_custom")
    val episodesCustom: String? = "",

    @SerialName("episodes_total")
    val episodesTotal: String? = "",

    @SerialName("extra_total")
    val extraTotal: Int = 0,

    @SerialName("ova_total")
    val ovaTotal: Int = 0,

    val year: Short = 0,

    val season: Byte = 0,

    val part: Byte = 0,

    val views: Int = 0,

    @SerialName("poster_thumb")
    val posterThumb: String = "",

    @SerialName("poster_mini")
    val posterMini: String = "",

    val schedule: String? = "",

    @SerialName("episode_count")
    val episodeCount: String? = "",

    @SerialName("new_episode")
    val newEpisode: Int = 0,

    @SerialName("is_extra")
    val isExtra: Boolean = false,

    @SerialName("is_ova")
    val isOva: Boolean = false,

    val time: Double = 0.0,

    @SerialName("time_str")
    val timeStr: String = "",
)