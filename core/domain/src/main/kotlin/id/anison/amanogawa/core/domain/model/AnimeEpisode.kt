package id.anison.amanogawa.core.domain.model


data class AnimeEpisode(
    val episodeId: Long,
    val animeId: Long,
    val botId: Long,
    val isExtra: Boolean = false,
    val isOva: Boolean = false,
    val name: String = "",
    val number: Int = 0,
    val screen: String = "",
    val screenOriginal: String = "",
)

data class AnimeEpisodes(
    val botUrl: String = "",
    val data: List<AnimeEpisode> = listOf(),
    val pages: Int = 0
)
