package id.anison.feature.model


data class EpisodeUI(
    val bot_id: Long = 0,
    val id: Long = 0,
    val is_extra: Boolean = false,
    val is_ova: Boolean = false,
    val name: String = "",
    val number: Int = 0,
    val post_date: String = "",
    val post_update: String = "",
    val screen: String = "",
    val screen_original: String = "",
    val title_id: Int = 0
)

data class EpisodesUI<E>(
    val bot_url: String = "",
    val data: List<EpisodeUI> = listOf(),
    val pages: Int = 0
)

