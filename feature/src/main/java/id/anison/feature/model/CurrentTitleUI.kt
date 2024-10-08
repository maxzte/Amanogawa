package id.anison.feature.model

data class CurrentTitleUI(
    override val id: Long,
    override val name: String,
    override val is_current: Boolean,
    override val en_jp_name: String,
    override val dubed_episodes: Int,
    override val episodes_custom: String?,
    override val episodes_total: String?,
    override val extra_total: Int,
    override val ova_total: Int,
    override val year: Int,
    override val season: Int,
    override val part: Int,
    override val views: Int,
    override val poster_thumb: String,
    override val poster_mini: String,
    override val schedule: String?,
    override val episode_count: String?,
    override val time_str: String?,
) : Title