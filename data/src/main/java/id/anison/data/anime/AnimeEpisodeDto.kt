package id.anison.data.anime

import id.anison.api.model.anime.Episode
import id.anison.api.model.anime.Episodes


data class EpisodeDto(
    val episodeId: Long = 0,
    val animeId: Int = 0,
    val botId: Long = 0,
    val isExtra: Boolean = false,
    val isOva: Boolean = false,
    val name: String = "",
    val number: Int = 0,
    val screen: String = "",
    val screenOriginal: String = "",
)

data class EpisodesDto(
    val botUrl: String = "",
    val data: List<EpisodeDto> = listOf(),
    val pages: Int = 0
)

internal fun Episode.toEpisodeDto() = EpisodeDto(
    episodeId = episodeId,
    animeId = animeId,
    botId = botId,
    isExtra = isExtra,
    isOva = isOva,
    name = name,
    number = number,
    screen = screen,
    screenOriginal = screenOriginal,
)

internal fun Episodes.toEpisodesDto() = EpisodesDto(
    botUrl = botUrl,
    data = data.map { it.toEpisodeDto() },
    pages = pages,
)