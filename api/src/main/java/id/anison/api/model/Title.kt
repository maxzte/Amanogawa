package id.anison.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Title(
    val id: Long,
    val name: String,
    val is_current: Boolean,
    val en_jp_name: String,
    val dubed_episodes: Int,
    val episodes_custom: String? = "",
    val episodes_total: String? = "",
    val extra_total: Int,
    val ova_total: Int,
    val year: Int,
    val season: Int,
    val part: Int,
    val views: Int,
    val poster_thumb: String,
    val poster_mini: String,
    val schedule: String? = "",
    val episode_count: String? = "",
)

@Serializable
data class AnimeTitles(
    val pages: Int,
    val data: List<Title>
)
