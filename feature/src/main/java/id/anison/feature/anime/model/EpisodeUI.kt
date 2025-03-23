package id.anison.feature.anime.model

import id.anison.data.anime.EpisodeDto
import id.anison.data.anime.EpisodesDto


data class EpisodeUI(
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

data class EpisodesUI(
    val data: List<EpisodeUI>,
    val pages: Int = 0,
    val botUrl: String = "",
)

internal fun EpisodeDto.toEpisodeUI() = EpisodeUI(
    botId = botId,
    animeId = animeId,
    episodeId = episodeId,
    isExtra = isExtra,
    isOva = isOva,
    name = name,
    number = number,
    screen = screen,
    screenOriginal = screenOriginal,
)

internal fun EpisodesDto.toEpisodesUI() = EpisodesUI(
    data = data.map { it.toEpisodeUI() },
    pages = pages,
    botUrl = botUrl,
)

