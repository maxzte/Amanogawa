package id.anison.api.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class Anime(
    val id: Long,
    val name: String,
    val en_jp_name: String,
    val year: Int,
    val duration: String? = "",
    val company: String,
    val director: String,
    @SerialName("descrition")
    val description: String,
    val bot_id: Int,
    val season: Int,
    val part: Int,
    val views: Int,
    val post_date: String? = "",
    val post_update: String? = "",
    val episodes_total: String? = "",
    val episodes_custom: String? = "",
    val dubed_episodes: Int,
    val extra_total: Int,
    val ova_total: Int,
    val liked: Int = 0,
    val disliked: Int = 0,
    val is_current: Boolean,
    val is_movie: Boolean = false,
    val is_dubed: Boolean = false,
    val is_haveSubs: Boolean = false,
    val torrent_url: String? = "",
    val torrent_4k_url: String? = "",
    val poster_maker: String? = "",
    val poster: String? = "",
    val poster_thumb: String,
    val poster_mini: String,
    val screens_f: List<List<String>> = emptyList(),
    var genres_f: List<List<String>> = emptyList(),
    val voices_f: List<List<String>> = emptyList(),
    val sounds_f: List<List<String>> = emptyList(),
    val transl_f: List<List<String>> = emptyList(),
    val group_f: List<GroupF>? = emptyList(),
    val episode_count: String,
    val bot_url: String,
    @Transient val rating: Int = 0,
)

@Serializable
data class GroupF(
    val id: Long = 0,
    val name: String? = "",
    val is_current: Boolean = false,
    val en_jp_name: String? = "",
    val dubed_episodes: Int = 0,
    val episodes_custom: String? = "",
    val episodes_total: String? = "",
    val extra_total: Int = 0,
    val ova_total: Int = 0,
    val year: Int = 0,
    val season: Int = 0,
    val part: Int = 0,
    val views: Int = 0,
    val poster_thumb: String? = "",
    val poster_mini: String? = "",
    val schedule: String? = "",
    val episode_count: String? = "",
)