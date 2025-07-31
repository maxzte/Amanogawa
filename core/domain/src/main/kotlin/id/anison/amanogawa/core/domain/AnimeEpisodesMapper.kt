package id.anison.amanogawa.core.domain

import id.anison.amanogawa.core.domain.model.AnimeEpisode
import id.anison.amanogawa.core.domain.model.AnimeEpisodes
import id.anison.amanogawa.core.model.AnimeEpisodeUI
import id.anison.amanogawa.core.model.AnimeEpisodesUI

fun AnimeEpisodes.toAnimeEpisodesUI() = AnimeEpisodesUI(
    data = data.map { it.toEpisodeUI() },
    pages = pages,
    botUrl = botUrl,
)

fun AnimeEpisode.toEpisodeUI() = AnimeEpisodeUI(
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

