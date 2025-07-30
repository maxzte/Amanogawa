package id.anison.amanogawa.core.data.anime

import id.anison.amanogawa.core.network.model.anime.AnimeEpisodeDto
import id.anison.amanogawa.core.network.model.anime.AnimeEpisodesDto


data class AnimeEpisode(
    val episodeId: Long,
    val animeId: Int,
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

internal fun AnimeEpisodesDto.toAnimeEpisodes() = AnimeEpisodes(
    botUrl = botUrl,
    data = data.map { it.toAnimeEpisode() },
    pages = pages,
)

internal fun AnimeEpisodeDto.toAnimeEpisode() = AnimeEpisode(
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
