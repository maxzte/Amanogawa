package id.anison.amanogawa.core.model


data class AnimeEpisodeUI(
    val episodeId: Long,
    val animeId: Int,
    val botId: Long,
    val isExtra: Boolean = false,
    val isOva: Boolean = false,
    val name: String = "",
    val number: Int = 0,
    val postDate: String = "",
    val postUpdate: String = "",
    val screen: String = "",
    val screenOriginal: String = "",
)

data class AnimeEpisodesUI(
    val data: List<AnimeEpisodeUI>,
    val pages: Int = 0,
    val botUrl: String = "",
)


